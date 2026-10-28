param(
    [string]$ModelRoot = 'F:\LocalDiskC\Users\eyeka\.ollama\models',
    [int]$Port = 11435,
    [string]$StoreId = 'archive-f'
)
$ErrorActionPreference = 'Stop'
$resolvedRoot = (Resolve-Path -LiteralPath $ModelRoot).Path
if (!(Test-Path -LiteralPath (Join-Path $resolvedRoot 'manifests')) -or !(Test-Path -LiteralPath (Join-Path $resolvedRoot 'blobs'))) {
    throw 'The store must have both manifests and blobs.'
}
if ($Port -lt 1024 -or $Port -gt 65535 -or $Port -eq 11434) { throw 'Use a distinct, non-privileged port for the additional store.' }
$endpoint = "http://127.0.0.1:$Port"
$state = Join-Path $env:USERPROFILE '.fraymus\models'
New-Item -ItemType Directory -Path $state -Force | Out-Null
try { $null = Invoke-RestMethod -Uri "$endpoint/api/version" -TimeoutSec 2 }
catch {
    $exe = Join-Path $env:LOCALAPPDATA 'Programs\Ollama\ollama.exe'
    if (!(Test-Path -LiteralPath $exe)) { $exe = (Get-Command ollama -ErrorAction Stop).Source }
    $savedHost = $env:OLLAMA_HOST
    $savedModels = $env:OLLAMA_MODELS
    $savedCloud = $env:OLLAMA_NO_CLOUD
    try {
        $env:OLLAMA_HOST = "127.0.0.1:$Port"
        $env:OLLAMA_MODELS = $resolvedRoot
        $env:OLLAMA_NO_CLOUD = '1'
        $process = Start-Process -FilePath $exe -ArgumentList 'serve' -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $state "$StoreId.stdout.log") -RedirectStandardError (Join-Path $state "$StoreId.stderr.log")
        $process.Id | Set-Content -LiteralPath (Join-Path $state "$StoreId.pid")
    } finally {
        $env:OLLAMA_HOST = $savedHost
        $env:OLLAMA_MODELS = $savedModels
        $env:OLLAMA_NO_CLOUD = $savedCloud
    }
    $ready = $false
    for ($attempt = 0; $attempt -lt 30; $attempt++) {
        try { $null = Invoke-RestMethod -Uri "$endpoint/api/version" -TimeoutSec 1; $ready = $true; break } catch { Start-Sleep -Milliseconds 250 }
    }
    if (!$ready) { throw "Additional Ollama store failed to start. See $state\$StoreId.stderr.log" }
}
$tags = Invoke-RestMethod -Uri "$endpoint/api/tags" -TimeoutSec 10
if (!$tags.models.Count) { throw 'The additional endpoint has no models.' }
$show = Invoke-RestMethod -Uri "$endpoint/api/show" -Method Post -ContentType 'application/json' -Body (@{model=$tags.models[0].name} | ConvertTo-Json) -TimeoutSec 10
$from = @($show.modelfile -split "`n" | Where-Object { $_ -match '^FROM\s+' })
if (!$from.Count -or !(($from[0] -replace '^FROM\s+', '').Trim().Trim('"').StartsWith($resolvedRoot + '\', [StringComparison]::OrdinalIgnoreCase))) {
    throw 'The endpoint is not serving the requested model store. Its configuration was not changed.'
}
$configPath = Join-Path $state 'ollama-stores.json'
$stores = @()
if (Test-Path -LiteralPath $configPath) { $stores = @((Get-Content -LiteralPath $configPath -Raw | ConvertFrom-Json).stores | Where-Object { $_.id -ne $StoreId }) }
$stores += [ordered]@{id=$StoreId;endpoint=$endpoint;modelRoot=$resolvedRoot}
[IO.File]::WriteAllText($configPath, (@{stores=$stores} | ConvertTo-Json -Depth 5), (New-Object Text.UTF8Encoding($false)))
[pscustomobject]@{store=$StoreId;endpoint=$endpoint;models=$tags.models.Count;modelRoot=$resolvedRoot;config=$configPath} | ConvertTo-Json -Compress
