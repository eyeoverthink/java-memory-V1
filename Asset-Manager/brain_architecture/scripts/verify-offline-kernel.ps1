param(
    [switch]$FullSuite
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Ensure-RustCargoOnPath {
    $cargoBin = Join-Path $env:USERPROFILE '.cargo\bin'
    $cargoExe = Join-Path $cargoBin 'cargo.exe'
    if (-not (Test-Path -LiteralPath $cargoExe)) {
        return
    }

    $segments = @($env:Path -split ';' | Where-Object { -not [string]::IsNullOrWhiteSpace($_) })
    $normalizedCargoBin = $cargoBin.TrimEnd('\')
    $hasCargoPath = $false
    foreach ($segment in $segments) {
        if ($segment.TrimEnd('\') -ieq $normalizedCargoBin) {
            $hasCargoPath = $true
            break
        }
    }

    if (-not $hasCargoPath) {
        $env:Path = "$cargoBin;$env:Path"
    }
}

Ensure-RustCargoOnPath

function Get-SourceFiles {
    param(
        [string[]]$Roots
    )

    $sources = New-Object System.Collections.Generic.List[string]
    foreach ($root in @($Roots)) {
        if ([string]::IsNullOrWhiteSpace($root) -or -not (Test-Path $root)) {
            continue
        }
        $files = @(Get-ChildItem $root -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName)
        foreach ($file in $files) {
            $sources.Add([System.IO.Path]::GetFullPath($file))
        }
    }
    return @($sources | Sort-Object -Unique)
}

function Add-ExistingFiles {
    param(
        [System.Collections.Generic.List[string]]$Target,
        [string[]]$Paths
    )

    foreach ($path in @($Paths)) {
        if ([string]::IsNullOrWhiteSpace($path) -or -not (Test-Path -LiteralPath $path)) {
            continue
        }
        $resolved = (Resolve-Path -LiteralPath $path).Path
        $Target.Add([System.IO.Path]::GetFullPath($resolved))
    }
}

function Get-TrackedInputFiles {
    param(
        [string[]]$CompileRoots,
        [string[]]$ExtraFiles
    )

    $tracked = New-Object System.Collections.Generic.List[string]
    foreach ($source in @(Get-SourceFiles -Roots $CompileRoots)) {
        $tracked.Add($source)
    }
    Add-ExistingFiles -Target $tracked -Paths $ExtraFiles
    return @($tracked | Sort-Object -Unique)
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
        $JavacCommand,
        [string[]]$ClassPathEntries
    )

    if (-not $Sources -or $Sources.Count -eq 0) {
        throw 'No offline kernel Java sources found.'
    }

    New-Item -ItemType Directory -Force -Path $CacheRoot | Out-Null

    $sourceState = Get-SourceStateLines -Sources $Sources
    $stateHash = Get-TextHash -Lines $sourceState
    $classesDir = Join-Path $CacheRoot ("classes-" + $stateHash)
    $sentinel = Join-Path $classesDir 'brain_architecture\kernel\KernelSmokeTest.class'
    $compileClassPathEntries = @($ClassPathEntries | Where-Object {
        -not [string]::IsNullOrWhiteSpace($_) -and (Test-Path -LiteralPath $_)
    } | ForEach-Object {
        [System.IO.Path]::GetFullPath((Resolve-Path -LiteralPath $_).Path)
    } | Sort-Object -Unique)
    $compileClassPath = if ($compileClassPathEntries.Count -gt 0) {
        [string]::Join([System.IO.Path]::PathSeparator, $compileClassPathEntries)
    } else {
        $null
    }
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

        $javacArgs = @('-encoding', 'UTF-8')
        if ($compileClassPath) {
            $javacArgs += @('-cp', $compileClassPath)
        }
        $javacArgs += @('-d', $stagingDir, "@$sourcesFile")
        & $JavacCommand.Source @javacArgs
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

function Get-StateMap {
    param(
        [string[]]$Lines
    )

    $map = @{}
    foreach ($line in @($Lines)) {
        if ([string]::IsNullOrWhiteSpace($line)) {
            continue
        }
        $parts = $line -split '\|', 3
        if ($parts.Length -eq 3) {
            $map[$parts[0]] = $parts[1] + '|' + $parts[2]
        }
    }
    return $map
}

function Get-ChangedPaths {
    param(
        [string[]]$CurrentState,
        [string]$BaselineFile
    )

    if (-not (Test-Path $BaselineFile)) {
        return @($CurrentState | ForEach-Object { ($_ -split '\|', 2)[0] })
    }

    $baselineState = Get-Content -LiteralPath $BaselineFile
    $currentMap = Get-StateMap -Lines $CurrentState
    $baselineMap = Get-StateMap -Lines $baselineState
    $changed = New-Object System.Collections.Generic.HashSet[string]([System.StringComparer]::OrdinalIgnoreCase)

    foreach ($path in $currentMap.Keys) {
        if (-not $baselineMap.ContainsKey($path) -or $baselineMap[$path] -ne $currentMap[$path]) {
            [void]$changed.Add($path)
        }
    }
    foreach ($path in $baselineMap.Keys) {
        if (-not $currentMap.ContainsKey($path)) {
            [void]$changed.Add($path)
        }
    }

    return @($changed | Sort-Object)
}

function Add-TestClasses {
    param(
        [System.Collections.Generic.HashSet[string]]$Target,
        [string[]]$Tests
    )

    foreach ($test in @($Tests)) {
        if (-not [string]::IsNullOrWhiteSpace($test)) {
            [void]$Target.Add($test)
        }
    }
}

function Get-TestClassForPath {
    param(
        [string]$PathValue
    )

    if ([string]::IsNullOrWhiteSpace($PathValue)) {
        return $null
    }
    $fileName = [System.IO.Path]::GetFileNameWithoutExtension($PathValue)
    if ([string]::IsNullOrWhiteSpace($fileName) -or -not $fileName.EndsWith('Test')) {
        return $null
    }
    $normalized = $PathValue.Replace('\', '/')
    $marker = '/src/offlineTest/java/'
    $markerIndex = $normalized.IndexOf($marker, [System.StringComparison]::OrdinalIgnoreCase)
    if ($markerIndex -lt 0) {
        return $null
    }
    $relative = $normalized.Substring($markerIndex + $marker.Length)
    if (-not $relative.EndsWith('.java')) {
        return $null
    }
    return $relative.Substring(0, $relative.Length - 5).Replace('/', '.')
}

function Select-TestClasses {
    param(
        [string[]]$ChangedPaths,
        [string[]]$AllTests,
        [switch]$ForceFullSuite
    )

    if ($ForceFullSuite) {
        return [pscustomobject]@{
            Mode = 'full-suite'
            Tests = $AllTests
            Changed = @($ChangedPaths)
        }
    }

    if (-not $ChangedPaths -or $ChangedPaths.Count -eq 0) {
        return [pscustomobject]@{
            Mode = 'unchanged-smoke'
            Tests = @(
                'brain_architecture.kernel.KernelSmokeTest',
                'brain_architecture.kernel.ValidationSuiteFactoryTest'
            )
            Changed = @()
        }
    }

    if ($ChangedPaths.Count -gt 8) {
        return [pscustomobject]@{
            Mode = 'broad-change'
            Tests = $AllTests
            Changed = @($ChangedPaths)
        }
    }

    $fullImpactFiles = @(
        'OfflineKernel.java',
        'KernelConfiguration.java',
        'ExecutionTrace.java',
        'KernelRunResult.java',
        'TaskEnvelope.java',
        'TaskBudget.java',
        'TaskMode.java',
        'TaskPlanner.java',
        'StrategyBanditRouter.java'
    )

    $selected = New-Object System.Collections.Generic.HashSet[string]([System.StringComparer]::Ordinal)
    Add-TestClasses -Target $selected -Tests @('brain_architecture.kernel.KernelSmokeTest')

    foreach ($changedPath in @($ChangedPaths)) {
        $normalized = $changedPath.Replace('\', '/')
        $fileName = [System.IO.Path]::GetFileName($normalized)
        if ($fullImpactFiles -contains $fileName) {
            return [pscustomobject]@{
                Mode = 'core-impact'
                Tests = $AllTests
                Changed = @($ChangedPaths)
            }
        }

        $directTestClass = Get-TestClassForPath -PathValue $normalized
        if ($directTestClass) {
            Add-TestClasses -Target $selected -Tests @($directTestClass)
            continue
        }

        switch -Regex ($normalized) {
            'brain_architecture/scripts/verify-offline-kernel\.ps1$' {
                return [pscustomobject]@{
                    Mode = 'verifier-script-change'
                    Tests = $AllTests
                    Changed = @($ChangedPaths)
                }
            }
            'brain_architecture/offline-toolchain\.toml$|brain_architecture/scripts/run-offline-kernel\.ps1$|brain_architecture/scripts/verify-workspace\.ps1$' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.ValidationSuiteFactoryTest',
                    'brain_architecture.kernel.ToolchainBootstrapTest',
                    'brain_architecture.kernel.ShadowWorkspaceMinimizationTest',
                    'brain_architecture.kernel.MultiStepEvolutionTest',
                    'brain_architecture.kernel.RepairGoalProposalFocusTest'
                )
                continue
            }
            'brain_architecture/scripts/verify-f0-engine\.ps1$' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.ValidationSuiteFactoryTest',
                    'brain_architecture.kernel.ToolchainBootstrapTest',
                    'brain_architecture.kernel.SubstrateConformanceTest'
                )
                continue
            }
            '/scripts/verify-root-workspace\.ps1$|/scripts/set-live-training-scenario\.ps1$|/scripts/train-live-repair-curriculum\.ps1$|/live-training/scenarios/scenarios\.json$|(^|/)offline-toolchain\.toml$' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.ValidationSuiteFactoryTest',
                    'brain_architecture.kernel.ShadowWorkspaceMinimizationTest',
                    'brain_architecture.kernel.MultiStepEvolutionTest',
                    'brain_architecture.kernel.RepairGoalProposalFocusTest',
                    'brain_architecture.kernel.ValidatedRepairGoalNoOpTest',
                    'brain_architecture.kernel.MissingImportRepairTest',
                    'brain_architecture.kernel.SignatureMismatchRepairTest',
                    'brain_architecture.kernel.MissingFieldRepairTest',
                    'brain_architecture.kernel.PackageDeclarationRepairTest',
                    'brain_architecture.kernel.DependentCollaboratorSynthesisTest',
                    'brain_architecture.kernel.RepairMemoryIntegrationTest',
                    'brain_architecture.FraynixSystemKernelForwardingTest',
                    'brain_architecture.integration.SystemOrchestratorKernelForwardingTest'
                )
                continue
            }
            'FraynixSystem\.java$|SystemOrchestrator\.java$|Unified[A-Za-z]+\.java$' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.FraynixSystemKernelForwardingTest',
                    'brain_architecture.integration.SystemOrchestratorKernelForwardingTest',
                    'brain_architecture.integration.unified.UnifiedKernelGatewayRichResultTest',
                    'brain_architecture.integration.unified.UnifiedKernelGatewayMultiIterationPreviewTest',
                    'brain_architecture.kernel.KernelSmokeTest',
                    'brain_architecture.kernel.ValidationSuiteFactoryTest'
                )
                continue
            }
            'FoundationalSubstrateBootstrapper\.java$|FoundationalSubstrateReport\.java$|LocalModelDescriptor\.java$|TokenizerDigest\.java$|LocalModelRuntimeDescriptor\.java$|LocalModelSubstrateCatalog\.java$|LocalModelRuntimeAdapter\.java$|OfflineKernelCli\.java$|GgufRuntimeModule\.java$|GgufTokenPrimitiveEngine\.java$|ModelLoader\.java$|runtime/local_transformers_runtime\.py$' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.FoundationalSubstrateBootstrapTest',
                    'brain_architecture.kernel.LocalModelRuntimeAdapterTest',
                    'brain_architecture.kernel.PythonTransformersRuntimeAdapterTest',
                    'brain_architecture.kernel.JavaGgufRuntimeAdapterTest',
                    'brain_architecture.kernel.DockerLlamaServerRuntimeAdapterTest',
                    'brain_architecture.kernel.GgufTokenPrimitiveEngineTest',
                    'brain_architecture.kernel.OllamaManifestDiscoveryTest',
                    'brain_architecture.kernel.CanonicalTruthAuditorTest',
                    'brain_architecture.kernel.CanonicalRefreshMemoryIntegrationTest',
                    'brain_architecture.kernel.KernelSmokeTest',
                    'brain_architecture.kernel.ValidationSuiteFactoryTest'
                )
                continue
            }
            'MutationProposalEngine\.java$' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.MutationProposalEngineTest',
                    'brain_architecture.kernel.VerifierGuidedSourceRepairTest',
                    'brain_architecture.kernel.MissingImportRepairTest',
                    'brain_architecture.kernel.MissingImportFallbackRepairTest',
                    'brain_architecture.kernel.MissingTypeSynthesisRepairTest',
                    'brain_architecture.kernel.FailingTestConstructionSynthesisTest',
                    'brain_architecture.kernel.MultiFileCollaboratorSynthesisTest',
                    'brain_architecture.kernel.DependentCollaboratorSynthesisTest',
                    'brain_architecture.kernel.SignatureMismatchRepairTest',
                    'brain_architecture.kernel.MissingMethodRepairTest',
                    'brain_architecture.kernel.MissingFieldRepairTest',
                    'brain_architecture.kernel.TypeNameAlignmentRepairTest',
                    'brain_architecture.kernel.PackageDeclarationRepairTest',
                    'brain_architecture.kernel.MultiStepEvolutionTest',
                    'brain_architecture.kernel.RepairGoalProposalFocusTest',
                    'brain_architecture.kernel.ValidatedRepairGoalNoOpTest',
                    'brain_architecture.kernel.CorpusGuidedEvolutionTest',
                    'brain_architecture.kernel.RepairTemplateExpansionTest',
                    'brain_architecture.kernel.TypeSynthesisTemplateIntegrationTest',
                    'brain_architecture.kernel.MultiFileConstructionTemplateIntegrationTest'
                )
                continue
            }
            'KernelVerifier\.java$|VerifierDiagnostic\.java$|VerifierResult\.java$' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.VerifierDiagnosticsTest',
                    'brain_architecture.kernel.VerifierGuidedSourceRepairTest',
                    'brain_architecture.kernel.MissingImportRepairTest',
                    'brain_architecture.kernel.MissingImportFallbackRepairTest',
                    'brain_architecture.kernel.MissingTypeSynthesisRepairTest',
                    'brain_architecture.kernel.FailingTestConstructionSynthesisTest',
                    'brain_architecture.kernel.MultiFileCollaboratorSynthesisTest',
                    'brain_architecture.kernel.DependentCollaboratorSynthesisTest',
                    'brain_architecture.kernel.SignatureMismatchRepairTest',
                    'brain_architecture.kernel.MissingMethodRepairTest',
                    'brain_architecture.kernel.MissingFieldRepairTest',
                    'brain_architecture.kernel.TypeNameAlignmentRepairTest',
                    'brain_architecture.kernel.PackageDeclarationRepairTest',
                    'brain_architecture.kernel.MultiStepEvolutionTest',
                    'brain_architecture.kernel.RepairGoalProposalFocusTest',
                    'brain_architecture.kernel.ValidatedRepairGoalNoOpTest'
                )
                continue
            }
            'ShadowMutationEngine\.java$|EvolutionReport\.java$' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.EvolutionLoopTest',
                    'brain_architecture.kernel.ShadowWorkspaceMinimizationTest',
                    'brain_architecture.kernel.MultiStepEvolutionTest',
                    'brain_architecture.kernel.RepairMemoryIntegrationTest',
                    'brain_architecture.kernel.ValidatedRepairGoalNoOpTest'
                )
                continue
            }
            'RepairMemoryStore\.java$|MutationProposal\.java$|MutationCandidate\.java$|MutationOperation\.java$' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.RepairMemoryStoreTest',
                    'brain_architecture.kernel.RepairMemoryIntegrationTest',
                    'brain_architecture.kernel.RepairTemplateIntegrationTest',
                    'brain_architecture.kernel.RepairTemplateExpansionTest',
                    'brain_architecture.kernel.TypeSynthesisTemplateIntegrationTest',
                    'brain_architecture.kernel.MultiFileConstructionTemplateIntegrationTest',
                    'brain_architecture.kernel.DependentCollaboratorSynthesisTest',
                    'brain_architecture.kernel.RepairGoalProposalFocusTest',
                    'brain_architecture.kernel.MutationProposalEngineTest'
                )
                continue
            }
            'HybridMemoryStore\.java$|MemoryDocument\.java$|MemoryHit\.java$|RetrievalProfile\.java$|CorpusMutationSeed\.java$|KernelHypervectorSpace\.java$|fraymus/causal/|fraymus/hdc/' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.CausalMemoryIntegrationTest',
                    'brain_architecture.kernel.KernelPersistenceTest',
                    'brain_architecture.kernel.CorpusGuidedEvolutionTest',
                    'brain_architecture.kernel.PdfDistillationEngineTest',
                    'brain_architecture.kernel.MutationProposalEngineTest'
                )
                continue
            }
            'PdfDistillation|OptionalPdfTextExtractor\.java$' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.PdfDistillationEngineTest',
                    'brain_architecture.kernel.CorpusGuidedEvolutionTest',
                    'brain_architecture.kernel.ProjectMemoryArtifactsTest'
                )
                continue
            }
            'ProjectMemoryArtifactWriter\.java$|TraceReplayStore\.java$|KernelStateStore\.java$|MerkleAppendOnlyJournal\.java$' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.ProjectMemoryArtifactsTest',
                    'brain_architecture.kernel.CanonicalRefreshMemoryIntegrationTest',
                    'brain_architecture.kernel.KernelPersistenceTest',
                    'brain_architecture.kernel.KernelSmokeTest'
                )
                continue
            }
            'CanonicalTruthAuditor\.java$|ClaimRecord\.java$|ClaimStatus\.java$' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.CanonicalTruthAuditorTest',
                    'brain_architecture.kernel.CanonicalRefreshMemoryIntegrationTest',
                    'brain_architecture.kernel.ProjectMemoryArtifactsTest',
                    'brain_architecture.kernel.KernelSmokeTest'
                )
                continue
            }
            'ValidationSuiteFactory\.java$|ValidationSuite\.java$|ValidationCommand\.java$|OfflineToolchainManifest\.java$' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.ValidationSuiteFactoryTest',
                    'brain_architecture.kernel.ToolchainBootstrapTest',
                    'brain_architecture.kernel.ShadowWorkspaceMinimizationTest',
                    'brain_architecture.kernel.KernelSmokeTest'
                )
                continue
            }
            'Substrate|KernelExecutionBridgeAdapter\.java$|F0RuntimeSubstrateAdapter\.java$|UserSpaceSubstrateAdapter\.java$|fraymus/execution/' {
                Add-TestClasses -Target $selected -Tests @(
                    'brain_architecture.kernel.SubstrateConformanceTest',
                    'brain_architecture.kernel.EvolutionLoopTest',
                    'brain_architecture.kernel.KernelSmokeTest'
                )
                continue
            }
            default {
                return [pscustomobject]@{
                    Mode = 'unknown-change'
                    Tests = $AllTests
                    Changed = @($ChangedPaths)
                }
            }
        }
    }

    $tests = @($selected | Sort-Object)
    if (-not $tests -or $tests.Count -eq 0) {
        $tests = @('brain_architecture.kernel.KernelSmokeTest')
    }
    return [pscustomobject]@{
        Mode = 'targeted'
        Tests = $tests
        Changed = @($ChangedPaths)
    }
}

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = (Resolve-Path (Join-Path $scriptDir '..')).Path
$assetManagerRoot = (Resolve-Path (Join-Path $projectRoot '..')).Path
$rootWorkspace = (Resolve-Path (Join-Path $assetManagerRoot '..\..')).Path
$bundleMarker = Join-Path $rootWorkspace 'release-capsules\offline-production\BUNDLE_MODE.txt'
$bundleMode = Test-Path -LiteralPath $bundleMarker
$mainSource = Join-Path $projectRoot 'src\main\java\brain_architecture\kernel'
$unifiedSource = Join-Path $projectRoot 'src\main\java\brain_architecture\integration\unified'
$causalSource = Join-Path $assetManagerRoot 'src\main\java\fraymus\causal'
$hdcSource = Join-Path $assetManagerRoot 'src\main\java\fraymus\hdc'
$executionSource = Join-Path $assetManagerRoot 'src\main\java\fraymus\execution'
$llmModelLoader = Join-Path $assetManagerRoot 'src\main\java\fraymus\llm\ModelLoader.java'
$testSource = Join-Path $projectRoot 'src\offlineTest\java'
$legacyMainClasses = Join-Path $projectRoot 'build\classes\java\main'
$cacheRoot = Join-Path $projectRoot 'build\offline-kernel-verify-cache'
$baselineFile = Join-Path $cacheRoot 'last-verified-state.txt'
$selectionFile = Join-Path $cacheRoot 'last-selection.txt'

$javac = Get-Command javac -ErrorAction SilentlyContinue
$java = Get-Command java -ErrorAction SilentlyContinue

if (-not $javac) {
    throw 'javac was not found in PATH.'
}

if (-not $java) {
    throw 'java was not found in PATH.'
}

$compatibilitySources = New-Object System.Collections.Generic.List[string]
if ($bundleMode) {
    Add-ExistingFiles -Target $compatibilitySources -Paths @(
        $llmModelLoader
    )
} else {
    Add-ExistingFiles -Target $compatibilitySources -Paths @(
        (Join-Path $projectRoot 'src\main\java\brain_architecture\FraynixSystem.java'),
        (Join-Path $projectRoot 'src\main\java\brain_architecture\integration\SystemOrchestrator.java'),
        (Join-Path $projectRoot 'src\main\java\brain_architecture\integration\QuantumOracleIntegration.java'),
        (Join-Path $projectRoot 'src\main\java\brain_architecture\core\BrainType.java'),
        $llmModelLoader
    )
}
foreach ($source in @(Get-SourceFiles -Roots @($unifiedSource))) {
    $compatibilitySources.Add($source)
}

$mainCompileRoots = if ($bundleMode) {
    @($mainSource, $unifiedSource, $causalSource, $hdcSource, $executionSource, $testSource)
} else {
    @($mainSource, $causalSource, $hdcSource, $executionSource, $testSource)
}

$trackedCompileRoots = if ($bundleMode) {
    @($mainSource, $unifiedSource, $causalSource, $hdcSource, $executionSource, $testSource)
} else {
    @($mainSource, $unifiedSource, $causalSource, $hdcSource, $executionSource, $testSource)
}

$compileSources = @(
    @(Get-SourceFiles -Roots $mainCompileRoots) +
    @($compatibilitySources)
) | Sort-Object -Unique

if ($bundleMode) {
    $compileSources = @($compileSources | Where-Object {
        $fileName = [System.IO.Path]::GetFileName([string]$_)
        $fileName -notin @('FraynixSystemKernelForwardingTest.java', 'SystemOrchestratorKernelForwardingTest.java')
    })
}

$trackedExtraFiles = @(
    @($compatibilitySources) +
    @(
    (Join-Path $projectRoot 'offline-toolchain.toml'),
    (Join-Path $projectRoot 'scripts\run-offline-kernel.ps1'),
    (Join-Path $projectRoot 'scripts\verify-offline-kernel.ps1'),
    (Join-Path $projectRoot 'scripts\verify-workspace.ps1'),
    (Join-Path $projectRoot 'scripts\verify-f0-engine.ps1'),
    (Join-Path $projectRoot 'runtime\local_transformers_runtime.py'),
    (Join-Path $rootWorkspace 'offline-toolchain.toml'),
    $bundleMarker,
    (Join-Path $rootWorkspace 'scripts\verify-root-workspace.ps1'),
    (Join-Path $rootWorkspace 'scripts\set-live-training-scenario.ps1'),
    (Join-Path $rootWorkspace 'scripts\train-live-repair-curriculum.ps1'),
    (Join-Path $rootWorkspace 'live-training\scenarios\scenarios.json')
    )
)
$trackedInputs = Get-TrackedInputFiles -CompileRoots $trackedCompileRoots -ExtraFiles $trackedExtraFiles
$sourceState = Get-SourceStateLines -Sources $trackedInputs
$classesDir = Ensure-CompiledCache -Sources $compileSources -CacheRoot $cacheRoot -JavacCommand $javac -ClassPathEntries @($legacyMainClasses)
$runtimeClassPathEntries = New-Object System.Collections.Generic.List[string]
$runtimeClassPathEntries.Add([System.IO.Path]::GetFullPath($classesDir))
if (Test-Path -LiteralPath $legacyMainClasses) {
    $resolvedLegacyMain = [System.IO.Path]::GetFullPath((Resolve-Path -LiteralPath $legacyMainClasses).Path)
    if (-not $resolvedLegacyMain.Equals([System.IO.Path]::GetFullPath($classesDir), [System.StringComparison]::OrdinalIgnoreCase)) {
        $runtimeClassPathEntries.Add($resolvedLegacyMain)
    }
}
$runtimeClassPath = [string]::Join([System.IO.Path]::PathSeparator, @($runtimeClassPathEntries))

$allTests = @(
    'brain_architecture.kernel.KernelSmokeTest',
    'brain_architecture.kernel.KernelPersistenceTest',
    'brain_architecture.kernel.CausalMemoryIntegrationTest',
    'brain_architecture.kernel.SubstrateConformanceTest',
    'brain_architecture.kernel.ValidationSuiteFactoryTest',
    'brain_architecture.kernel.MutationProposalEngineTest',
    'brain_architecture.kernel.EvolutionLoopTest',
    'brain_architecture.kernel.ToolchainBootstrapTest',
    'brain_architecture.kernel.PdfDistillationEngineTest',
    'brain_architecture.kernel.CorpusGuidedEvolutionTest',
    'brain_architecture.kernel.VerifierDiagnosticsTest',
    'brain_architecture.kernel.VerifierGuidedSourceRepairTest',
    'brain_architecture.kernel.MissingImportRepairTest',
    'brain_architecture.kernel.MissingImportFallbackRepairTest',
    'brain_architecture.kernel.MissingTypeSynthesisRepairTest',
    'brain_architecture.kernel.FailingTestConstructionSynthesisTest',
    'brain_architecture.kernel.MultiFileCollaboratorSynthesisTest',
    'brain_architecture.kernel.DependentCollaboratorSynthesisTest',
    'brain_architecture.kernel.LiveTrainingDependentCollaboratorIsolationTest',
    'brain_architecture.kernel.SignatureMismatchRepairTest',
    'brain_architecture.kernel.MissingMethodRepairTest',
    'brain_architecture.kernel.MissingFieldRepairTest',
    'brain_architecture.kernel.TypeNameAlignmentRepairTest',
    'brain_architecture.kernel.PackageDeclarationRepairTest',
    'brain_architecture.kernel.RepairMemoryStoreTest',
    'brain_architecture.kernel.RepairMemoryIntegrationTest',
    'brain_architecture.kernel.RepairTemplateIntegrationTest',
    'brain_architecture.kernel.RepairTemplateExpansionTest',
    'brain_architecture.kernel.TypeSynthesisTemplateIntegrationTest',
    'brain_architecture.kernel.MemoryIngestionConvergencePolicyTest',
    'brain_architecture.kernel.MultiFileConstructionTemplateIntegrationTest',
    'brain_architecture.kernel.ProjectMemoryArtifactsTest',
    'brain_architecture.kernel.CanonicalTruthAuditorTest',
    'brain_architecture.kernel.CanonicalRefreshMemoryIntegrationTest',
    'brain_architecture.kernel.ShadowWorkspaceMinimizationTest',
    'brain_architecture.kernel.MultiStepEvolutionTest',
    'brain_architecture.kernel.RepairGoalProposalFocusTest',
    'brain_architecture.kernel.ValidatedRepairGoalNoOpTest',
    'brain_architecture.kernel.FoundationalSubstrateBootstrapTest',
    'brain_architecture.kernel.LocalModelRuntimeAdapterTest',
    'brain_architecture.kernel.PythonTransformersRuntimeAdapterTest',
    'brain_architecture.kernel.JavaGgufRuntimeAdapterTest',
    'brain_architecture.kernel.DockerLlamaServerRuntimeAdapterTest',
    'brain_architecture.kernel.GgufTokenPrimitiveEngineTest',
    'brain_architecture.kernel.OllamaManifestDiscoveryTest',
    'brain_architecture.integration.unified.UnifiedKernelGatewayRichResultTest',
    'brain_architecture.integration.unified.UnifiedKernelGatewayMultiIterationPreviewTest'
)

if (-not $bundleMode) {
    $allTests += @(
        'brain_architecture.integration.SystemOrchestratorKernelForwardingTest',
        'brain_architecture.FraynixSystemKernelForwardingTest'
    )
}

$selection = Select-TestClasses -ChangedPaths (Get-ChangedPaths -CurrentState $sourceState -BaselineFile $baselineFile) -AllTests $allTests -ForceFullSuite:$FullSuite
$tests = @($selection.Tests)
$changedSelection = @($selection.Changed | Where-Object { -not [string]::IsNullOrWhiteSpace($_) })

Write-Host ("Offline kernel verifier mode: {0}; tests={1}/{2}" -f $selection.Mode, $tests.Count, $allTests.Count)
if ($changedSelection.Count -gt 0) {
    Write-Host ("Changed inputs: " + [string]::Join(', ', @($changedSelection | ForEach-Object { [System.IO.Path]::GetFileName($_) })))
}

foreach ($testClass in $tests) {
    & $java.Source -cp $runtimeClassPath $testClass
    if ($LASTEXITCODE -ne 0) {
        throw "$testClass failed with exit code $LASTEXITCODE"
    }
}

[System.IO.File]::WriteAllLines($baselineFile, $sourceState)
[System.IO.File]::WriteAllLines($selectionFile, @(
    "mode=$($selection.Mode)",
    "tests=$($tests.Count)",
    "changed=$($changedSelection.Count)"
))
Write-Host 'Offline kernel verification passed.'
