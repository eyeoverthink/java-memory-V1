Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = (Resolve-Path (Join-Path $scriptDir '..')).Path
$mainSource = Join-Path $projectRoot 'src\main\java'
$testSource = Join-Path $projectRoot 'src\offlineTest\java'
$buildDir = Join-Path $projectRoot 'build\offline-kernel'
$classesDir = Join-Path $buildDir 'classes'
$sourcesFile = Join-Path $buildDir 'sources.txt'

$javac = Get-Command javac -ErrorAction SilentlyContinue
if (-not $javac) {
    throw 'javac was not found in PATH.'
}

New-Item -ItemType Directory -Force -Path $buildDir | Out-Null
New-Item -ItemType Directory -Force -Path $classesDir | Out-Null

$sources = @()
if (Test-Path $mainSource) {
    $sources += Get-ChildItem $mainSource -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName
}
if (Test-Path $testSource) {
    $sources += Get-ChildItem $testSource -Recurse -File -Filter *.java | Select-Object -ExpandProperty FullName
}

if (-not $sources -or $sources.Count -eq 0) {
    throw 'No Java sources found for offline verification.'
}

[System.IO.File]::WriteAllLines($sourcesFile, $sources)

& $javac.Source -encoding UTF-8 -d $classesDir "@$sourcesFile"
if ($LASTEXITCODE -ne 0) {
    throw "javac failed with exit code $LASTEXITCODE"
}

Write-Host ("Workspace verification passed. Sources=" + $sources.Count)
