param([string]$PhysicsRoot = '', [switch]$Configure)
$ErrorActionPreference = 'Stop'
$appRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$workspaceRoot = Split-Path -Parent (Split-Path -Parent $appRoot)
if (-not $PhysicsRoot) { $PhysicsRoot = Join-Path $workspaceRoot 'integrations\physics-scene' }
$PhysicsRoot = (Resolve-Path -LiteralPath $PhysicsRoot).Path
Push-Location -LiteralPath $PhysicsRoot
try {
    & '.\gradlew.bat' --offline --no-daemon --console=plain check writeRobotSceneClasspath "-PfraynixRoot=$workspaceRoot"
    if ($LASTEXITCODE -ne 0) { throw 'Physics bridge build or verification failed' }
} finally { Pop-Location }
if ($Configure) {
    $configPath = Join-Path $env:USERPROFILE '.fraymus\models\specialist-runtime.json'
    $config = if (Test-Path -LiteralPath $configPath) { Get-Content -LiteralPath $configPath -Raw | ConvertFrom-Json } else { [pscustomobject]@{} }
    $config | Add-Member -NotePropertyName physics_root -NotePropertyValue $PhysicsRoot -Force
    New-Item -ItemType Directory -Path (Split-Path -Parent $configPath) -Force | Out-Null
    [IO.File]::WriteAllText($configPath, ($config | ConvertTo-Json -Depth 30), [Text.UTF8Encoding]::new($false))
    Write-Output "Physics bridge configured: $PhysicsRoot"
}
