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

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$workspaceRoot = (Resolve-Path (Join-Path $scriptDir '..\..\..\..')).Path
$f0Root = Join-Path $workspaceRoot 'f0_engine'
$cargo = Get-Command cargo -ErrorAction SilentlyContinue

if (-not $cargo) {
    throw 'cargo was not found in PATH.'
}

Push-Location $f0Root
try {
    & $cargo.Source test
    if ($LASTEXITCODE -ne 0) {
        throw "cargo test failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}

Write-Host 'f0_engine verification passed.'
