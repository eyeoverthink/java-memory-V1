param(
  [int]$Port = 8181
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

Write-Host "[Fraymus] Compiling Java sources..."
.\gradlew.bat compileJava --console=plain

Write-Host "[Fraymus] Starting Ultimate UI on port $Port ..."
.\gradlew.bat runUltimateUi "-PuiPort=$Port" --console=plain
