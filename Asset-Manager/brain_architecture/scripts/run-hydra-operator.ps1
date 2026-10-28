param(
    [Parameter(Position = 0)]
    [string]$Command = 'status',
    [Parameter(Position = 1)]
    [string]$WorkspaceRoot = '',
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$CommandArguments
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Resolve-SourceFiles {
    param(
        [string[]]$Candidates
    )

    $sources = New-Object System.Collections.Generic.List[string]
    foreach ($candidate in @($Candidates)) {
        if ([string]::IsNullOrWhiteSpace($candidate)) {
            continue
        }
        if (Test-Path -LiteralPath $candidate) {
            $sources.Add([System.IO.Path]::GetFullPath((Resolve-Path -LiteralPath $candidate).Path)) | Out-Null
        }
    }
    return @($sources | Sort-Object -Unique)
}

function Get-SourceStateLines {
    param(
        [string[]]$Sources
    )

    $lines = New-Object System.Collections.Generic.List[string]
    foreach ($source in @($Sources)) {
        $item = Get-Item -LiteralPath $source
        $lines.Add(('{0}|{1}|{2}' -f $item.FullName.Replace('\', '/'), $item.Length, $item.LastWriteTimeUtc.Ticks))
    }
    return @($lines)
}

function Get-TextHash {
    param(
        [string[]]$Lines
    )

    $sha = [System.Security.Cryptography.SHA256]::Create()
    try {
        $text = [string]::Join("`n", @($Lines))
        $bytes = [System.Text.Encoding]::UTF8.GetBytes($text)
        $hashBytes = $sha.ComputeHash($bytes)
        return ([System.BitConverter]::ToString($hashBytes)).Replace('-', '').ToLowerInvariant()
    }
    finally {
        $sha.Dispose()
    }
}

function Acquire-CompileLock {
    param(
        [string]$LockPath
    )

    $deadline = (Get-Date).AddMinutes(5)
    while ((Get-Date) -lt $deadline) {
        try {
            return [System.IO.File]::Open($LockPath, [System.IO.FileMode]::OpenOrCreate, [System.IO.FileAccess]::ReadWrite, [System.IO.FileShare]::None)
        } catch {
            Start-Sleep -Milliseconds 200
        }
    }
    throw "Timed out waiting for compile lock: $LockPath"
}

function Prune-CompiledCache {
    param(
        [string]$CacheRoot,
        [string]$KeepHash
    )

    $directories = @(Get-ChildItem -LiteralPath $CacheRoot -Directory -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -like 'classes-*' } |
        Sort-Object LastWriteTimeUtc -Descending)
    $kept = 0
    foreach ($directory in $directories) {
        $hash = $directory.Name.Substring('classes-'.Length)
        if ($hash -eq $KeepHash -or $kept -lt 2) {
            $kept++
            continue
        }
        Remove-Item -Recurse -Force $directory.FullName -ErrorAction SilentlyContinue
    }
}

function Ensure-CompiledCache {
    param(
        [string[]]$Sources,
        [string]$CacheRoot,
        $JavacCommand
    )

    if (-not $Sources -or $Sources.Count -eq 0) {
        throw 'No Hydra operator Java sources found.'
    }

    New-Item -ItemType Directory -Force -Path $CacheRoot | Out-Null

    $sourceState = Get-SourceStateLines -Sources $Sources
    $stateHash = Get-TextHash -Lines $sourceState
    $classesDir = Join-Path $CacheRoot ("classes-" + $stateHash)
    $sentinel = Join-Path $classesDir 'fraymus\orchestration\HydraOperatorCli.class'
    if (Test-Path $sentinel) {
        return $classesDir
    }

    $lockPath = Join-Path $CacheRoot 'compile.lock'
    $lockHandle = $null
    try {
        $lockHandle = Acquire-CompileLock -LockPath $lockPath
        if (Test-Path $sentinel) {
            return $classesDir
        }

        $stagingDir = Join-Path $CacheRoot ("staging-" + $PID + "-" + $stateHash)
        $sourcesFile = Join-Path $stagingDir 'sources.txt'
        if (Test-Path $stagingDir) {
            Remove-Item -Recurse -Force $stagingDir -ErrorAction SilentlyContinue
        }
        New-Item -ItemType Directory -Force -Path $stagingDir | Out-Null
        [System.IO.File]::WriteAllLines($sourcesFile, $Sources)

        & $JavacCommand.Source -encoding UTF-8 -d $stagingDir "@$sourcesFile"
        if ($LASTEXITCODE -ne 0) {
            throw "javac failed with exit code $LASTEXITCODE"
        }

        if (Test-Path $classesDir) {
            Remove-Item -Recurse -Force $stagingDir -ErrorAction SilentlyContinue
        } else {
            Move-Item -LiteralPath $stagingDir -Destination $classesDir
        }

        $stateFile = Join-Path $classesDir 'source-state.txt'
        [System.IO.File]::WriteAllLines($stateFile, $sourceState)
        Prune-CompiledCache -CacheRoot $CacheRoot -KeepHash $stateHash
        return $classesDir
    }
    finally {
        if ($lockHandle -ne $null) {
            $lockHandle.Dispose()
        }
    }
}

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$brainRoot = (Resolve-Path (Join-Path $scriptDir '..')).Path
$assetManagerRoot = (Resolve-Path (Join-Path $brainRoot '..')).Path
$defaultWorkspaceRoot = [System.IO.Path]::GetFullPath((Join-Path $scriptDir '..\..\..\..'))
$resolvedWorkspaceRoot = if ([string]::IsNullOrWhiteSpace($WorkspaceRoot)) {
    $defaultWorkspaceRoot
} else {
    [System.IO.Path]::GetFullPath($WorkspaceRoot)
}

if (-not (Test-Path -LiteralPath $resolvedWorkspaceRoot)) {
    throw "Workspace root not found: $resolvedWorkspaceRoot"
}

$orchestrationRoot = Join-Path $assetManagerRoot 'src\main\java\fraymus\orchestration'
$neuralRoot = Join-Path $assetManagerRoot 'src\main\java\fraymus\neural'
$cacheRoot = Join-Path $brainRoot 'build\hydra-operator-cli-cache'

$javac = Get-Command javac -ErrorAction SilentlyContinue
$java = Get-Command java -ErrorAction SilentlyContinue
if (-not $javac) {
    throw 'javac was not found in PATH.'
}
if (-not $java) {
    throw 'java was not found in PATH.'
}

$sourceCandidates = @(
    (Join-Path $orchestrationRoot 'CommandParser.java'),
    (Join-Path $orchestrationRoot 'HydraProtocol.java'),
    (Join-Path $orchestrationRoot 'ProofAdapter.java'),
    (Join-Path $orchestrationRoot 'HashChainProofAdapter.java'),
    (Join-Path $orchestrationRoot 'ZkScaffoldProofAdapter.java'),
    (Join-Path $orchestrationRoot 'HydraOperatorCli.java'),
    (Join-Path $neuralRoot 'AuboLedger.java')
)
$sources = @(Resolve-SourceFiles -Candidates $sourceCandidates)
$classesDir = Ensure-CompiledCache -Sources $sources -CacheRoot $cacheRoot -JavacCommand $javac

$normalizedCommand = $Command.ToLowerInvariant()
$javaArgs = @(
    '-cp', $classesDir,
    'fraymus.orchestration.HydraOperatorCli',
    $normalizedCommand,
    '--workspace-root', $resolvedWorkspaceRoot
) + @($CommandArguments)

Push-Location $resolvedWorkspaceRoot
try {
    & $java.Source @javaArgs
    if ($LASTEXITCODE -ne 0) {
        throw "HydraOperatorCli failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}
