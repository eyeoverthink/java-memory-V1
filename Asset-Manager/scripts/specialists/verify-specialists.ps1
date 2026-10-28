param(
    [switch]$WithCarbon,
    [switch]$WithPhysics,
    [string]$Python = 'python',
    [string]$EvidenceRoot = ''
)
$ErrorActionPreference = 'Stop'
function Invoke-LoggedNative {
    param([string]$Executable, [string[]]$Arguments, [string]$Log)
    $previousAction = $ErrorActionPreference
    $nativeExit = 1
    try {
        # Windows PowerShell 5.1 wraps native stderr progress as ErrorRecords.
        $ErrorActionPreference = 'Continue'
        & $Executable @Arguments *> $Log
        $nativeExit = $LASTEXITCODE
    } finally { $ErrorActionPreference = $previousAction }
    if ($nativeExit -ne 0) { throw "Native verification failed ($nativeExit): $Log" }
}
$appRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
if (-not $EvidenceRoot) {
    $EvidenceRoot = Join-Path $appRoot ('build\specialist-validation\' + (Get-Date -Format 'yyyyMMdd-HHmmss') + '-' + [Guid]::NewGuid().ToString('N').Substring(0,8))
}
New-Item -ItemType Directory -Path $EvidenceRoot -Force | Out-Null
$EvidenceRoot = (Resolve-Path -LiteralPath $EvidenceRoot).Path
Push-Location -LiteralPath $appRoot
try {
    Invoke-LoggedNative $Python @('-m','unittest','discover','-s','scripts/genomics','-p','test_*.py','-v') (Join-Path $EvidenceRoot 'genomics-unit.log')
    Invoke-LoggedNative $Python @('-m','unittest','discover','-s','scripts/specialists','-p','test_*.py','-v') (Join-Path $EvidenceRoot 'specialist-unit.log')
    Invoke-LoggedNative '.\gradlew.bat' @('--offline','--no-daemon','--console=plain','writeSpecialistTestClasspath') (Join-Path $EvidenceRoot 'compile.log')
    $classPath = (Get-Content -LiteralPath (Join-Path $appRoot 'build\specialist-tests\classpath.txt') -Raw).Trim()
    Invoke-LoggedNative 'java' @('-cp',$classPath,'fraymus.genomics.DigitalGenomeArchiveTest') (Join-Path $EvidenceRoot 'genome-codec.log')
    Invoke-LoggedNative 'java' @('-cp',$classPath,'fraymus.genomics.EntericLifecycleTest','leave') (Join-Path $EvidenceRoot 'monitor-daemon.log')
    Invoke-LoggedNative 'java' @('-cp',$classPath,'fraymus.genomics.EntericLifecycleTest') (Join-Path $EvidenceRoot 'monitor-close.log')
    $deferredFixture = Join-Path $EvidenceRoot 'deferred-memory-fixture'
    New-Item -ItemType Directory -Path $deferredFixture -Force | Out-Null
    Push-Location -LiteralPath $deferredFixture
    try {
        Invoke-LoggedNative 'java' @('-cp',$classPath,'fraymus.genomics.AkashicDeferredPersistenceTest','seed') (Join-Path $EvidenceRoot 'memory-seed.log')
        Invoke-LoggedNative 'java' @('-Dfraymus.akashic.blocksFileSoftLimitBytes=1','-cp',$classPath,'fraymus.genomics.AkashicDeferredPersistenceTest','pending') (Join-Path $EvidenceRoot 'memory-pending.log')
        Invoke-LoggedNative 'java' @('-cp',$classPath,'fraymus.genomics.AkashicDeferredPersistenceTest','restore') (Join-Path $EvidenceRoot 'memory-merge.log')
    } finally { Pop-Location }
    $quotedFixture = Join-Path $EvidenceRoot 'quoted-kronos-fixture'
    New-Item -ItemType Directory -Path $quotedFixture -Force | Out-Null
    Push-Location -LiteralPath $quotedFixture
    try {
        foreach ($phase in @('seed','restore')) {
            Invoke-LoggedNative 'java' @('-cp',$classPath,'fraymus.genomics.KronosEscapedPersistenceTest',$phase) (Join-Path $EvidenceRoot ('kronos-quoted-' + $phase + '.log'))
        }
    } finally { Pop-Location }
    $lanes = @('specialist')
    if ($WithCarbon) { $lanes += 'genomics' }
    if ($WithPhysics) { $lanes += 'robot' }
    $recoveryFixture=Join-Path $EvidenceRoot 'record-recovery-fixture'
    New-Item -ItemType Directory -Path $recoveryFixture -Force | Out-Null
    Push-Location -LiteralPath $recoveryFixture
    try {
        Invoke-LoggedNative 'java' @('-cp',$classPath,'fraymus.genomics.AkashicRecordRecoveryTest','seed') (Join-Path $EvidenceRoot 'record-seed.log')
        Remove-Item -LiteralPath (Join-Path $recoveryFixture '.akashic\blocks.dat')
        Invoke-LoggedNative 'java' @('-cp',$classPath,'fraymus.genomics.AkashicRecordRecoveryTest','restore') (Join-Path $EvidenceRoot 'record-restore.log')
    } finally {Pop-Location}
    foreach ($lane in $lanes) {
        $fixture = Join-Path $EvidenceRoot ($lane + '-fixture')
        New-Item -ItemType Directory -Path $fixture -Force | Out-Null
        foreach ($relative in @('scripts\specialists','scripts\genomics','data\specialists','data\genomics','skills\genomics_carbon','skills\open_model_sources\generated')) {
            $target = Join-Path $fixture $relative
            New-Item -ItemType Directory -Path (Split-Path -Parent $target) -Force | Out-Null
            Copy-Item -LiteralPath (Join-Path $appRoot $relative) -Destination $target -Recurse -Force
        }
        if ($lane -eq 'genomics') {
            $original = Get-Content -LiteralPath (Join-Path $appRoot 'data\genomics\references\U91966.1\reference.fasta')
            $original[0] = '>sample_A'
            [IO.File]::WriteAllLines((Join-Path $fixture 'blind-sample-A.fasta'), $original, [Text.Encoding]::ASCII)
        }
        $main = switch($lane) {
            'specialist' { 'fraymus.genomics.SpecialistBrainIntegrationTest' }
            'robot' { 'fraymus.genomics.RobotPhysicsIntegrationTest' }
            default { 'fraymus.genomics.GenomicsBrainIntegrationTest' }
        }
        Push-Location -LiteralPath $fixture
        try {
            Invoke-LoggedNative 'java' @('-Xmx1g','-cp',$classPath,$main) (Join-Path $EvidenceRoot ($lane + '-integration.log'))
            Invoke-LoggedNative 'java' @('-Xmx1g','-cp',$classPath,$main,'restore') (Join-Path $EvidenceRoot ($lane + '-restart.log'))
        } finally { Pop-Location }
    }
    Write-Output "PASS: current-source compilation, 22 Python checks, genome codec, daemon shutdown, deferred memory, escaped Kronos persistence and $($lanes -join ', ') integration/restart. Evidence: $EvidenceRoot"
} finally { Pop-Location }
