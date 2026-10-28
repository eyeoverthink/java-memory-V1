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

function Ensure-CompiledCache {
    param(
        [string[]]$Sources,
        [string]$CacheRoot,
        $JavacCommand
    )

    if (-not $Sources -or $Sources.Count -eq 0) {
        throw 'No offline kernel Java sources found.'
    }

    New-Item -ItemType Directory -Force -Path $CacheRoot | Out-Null

    $sourceState = Get-SourceStateLines -Sources $Sources
    $stateHash = Get-TextHash -Lines $sourceState
    $classesDir = Join-Path $CacheRoot ("classes-" + $stateHash)
    $sentinel = Join-Path $classesDir 'brain_architecture\kernel\OfflineKernelCli.class'
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
        $sourcesFile = Join-Path $stagingDir 'main-sources.txt'
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

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = (Resolve-Path (Join-Path $scriptDir '..')).Path
$assetManagerRoot = (Resolve-Path (Join-Path $projectRoot '..')).Path
$mainSource = Join-Path $projectRoot 'src\main\java\brain_architecture\kernel'
$causalSource = Join-Path $assetManagerRoot 'src\main\java\fraymus\causal'
$hdcSource = Join-Path $assetManagerRoot 'src\main\java\fraymus\hdc'
$executionSource = Join-Path $assetManagerRoot 'src\main\java\fraymus\execution'
$llmModelLoader = Join-Path $assetManagerRoot 'src\main\java\fraymus\llm\ModelLoader.java'
$cacheRoot = Join-Path $projectRoot 'build\offline-kernel-cli-cache'

$javac = Get-Command javac -ErrorAction SilentlyContinue
$java = Get-Command java -ErrorAction SilentlyContinue

if (-not $javac) {
    throw 'javac was not found in PATH.'
}

if (-not $java) {
    throw 'java was not found in PATH.'
}

$sources = New-Object System.Collections.Generic.List[string]
foreach ($source in @(Get-SourceFiles -Roots @($mainSource, $causalSource, $hdcSource, $executionSource))) {
    $sources.Add($source)
}
if (Test-Path -LiteralPath $llmModelLoader) {
    $resolvedModelLoader = [System.IO.Path]::GetFullPath((Resolve-Path -LiteralPath $llmModelLoader).Path)
    $sources.Add($resolvedModelLoader)
}
$sources = @($sources | Sort-Object -Unique)
$classesDir = Ensure-CompiledCache -Sources $sources -CacheRoot $cacheRoot -JavacCommand $javac

& $java.Source -cp $classesDir 'brain_architecture.kernel.OfflineKernelCli' @args
if ($LASTEXITCODE -ne 0) {
    throw "OfflineKernelCli failed with exit code $LASTEXITCODE"
}
