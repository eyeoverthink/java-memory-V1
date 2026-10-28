param(
    [string]$OllamaUrl = 'http://127.0.0.1:11434',
    [string]$OutputPath = (Join-Path $env:USERPROFILE '.fraymus\models\local-model-catalog.json'),
    [string]$FileInventory,
    [switch]$ScanDisks
)
$ErrorActionPreference = 'Stop'
# Metadata only: no pulls, model creation, daemon restart, or execution of discovered Modelfiles.
$endpoint = [Uri]$OllamaUrl
if (!$endpoint.IsLoopback) { throw 'This inventory accepts a loopback Ollama endpoint only.' }
$drives = @(Get-PSDrive -PSProvider FileSystem | Where-Object { $_.Name -match '^[A-Z]$' } |
    ForEach-Object { [ordered]@{ root=$_.Root; freeBytes=$_.Free; usedBytes=$_.Used } })
$stores = @([pscustomobject]@{id='primary';endpoint=$OllamaUrl})
$storeConfig = Join-Path (Split-Path -Parent ([IO.Path]::GetFullPath($OutputPath))) 'ollama-stores.json'
if (Test-Path -LiteralPath $storeConfig) { $stores += @((Get-Content -LiteralPath $storeConfig -Raw | ConvertFrom-Json).stores) }
$models = @()
foreach ($store in $stores) {
$storeUrl = $store.endpoint
if (!([Uri]$storeUrl).IsLoopback) { throw 'Every model store endpoint must be loopback.' }
try { $tags = Invoke-RestMethod -Uri "$storeUrl/api/tags" -TimeoutSec 5 }
catch { Write-Warning "Store unavailable: $storeUrl"; continue }
foreach ($tag in $tags.models) {
    $remote = ![string]::IsNullOrWhiteSpace($tag.remote_host) -or
        ![string]::IsNullOrWhiteSpace($tag.remote_model) -or $tag.name -match '-cloud$'
    $record = [ordered]@{
        name=$tag.name; digest=$tag.digest; sizeBytes=$tag.size; status='REMOTE_PROXY'; endpoint=$storeUrl; storeId=$store.id
        remoteHost=$tag.remote_host; family=$tag.details.family
        parameterSize=$tag.details.parameter_size; quantization=$tag.details.quantization_level
    }
    if (!$remote) {
        try {
            $body = @{model=$tag.name} | ConvertTo-Json -Compress
            $show = Invoke-RestMethod -Uri "$storeUrl/api/show" -Method Post -Body $body -ContentType 'application/json' -TimeoutSec 20
            $record.status = 'LOCAL_INSTALLED'
            $record.capabilities = @($show.capabilities)
            $context = @($show.model_info.PSObject.Properties | Where-Object { $_.Name -match '\.context_length$' } | ForEach-Object { $_.Value })
            $record.contextLength = if ($context.Count) { [long]$context[0] } else { 0 }
            $ctx = [regex]::Match($show.parameters, '(?m)^num_ctx\s+(\d+)')
            $record.configuredContext = if ($ctx.Success) { [int]$ctx.Groups[1].Value } else { 0 }
            $from = @($show.modelfile -split "`n" | Where-Object { $_ -match '^FROM\s+' })
            $record.sourcePath = if ($from.Count) { ($from[0] -replace '^FROM\s+', '').Trim().Trim('"') } else { '' }
            $record.sourceExists = ![string]::IsNullOrWhiteSpace($record.sourcePath) -and (Test-Path -LiteralPath $record.sourcePath -PathType Leaf)
            # Preserve provenance without publishing the model's private system prompt.
            $sha = [Security.Cryptography.SHA256]::Create()
            try { $record.modelfileSha256 = ([BitConverter]::ToString($sha.ComputeHash([Text.Encoding]::UTF8.GetBytes($show.modelfile)))).Replace('-','').ToLowerInvariant() }
            finally { $sha.Dispose() }
        } catch {
            $record.status = 'METADATA_UNAVAILABLE'
            $record.error = $_.Exception.Message
        }
    }
    $models += [pscustomobject]$record
}
}
$files = @()
if ($ScanDisks) {
    if (!(Get-Command rg -ErrorAction SilentlyContinue)) { throw 'Disk discovery needs ripgrep (rg).' }
    $roots = @($drives | ForEach-Object { if ($_.root -ne 'C:\') { $_.root } })
    $roots += @((Join-Path $env:USERPROFILE 'Downloads'), (Join-Path $env:USERPROFILE 'Documents'), (Join-Path $env:USERPROFILE '.cache\huggingface\hub'))
    $files = @(& rg --files --hidden --no-ignore -g '*.gguf' -g '*.safetensors' -g '*.ggml' -g 'Modelfile*' -g '*Modelfile' -g 'pytorch_model*.bin' -g '!**/node_modules/**' -g '!**/.git/**' -g '!**/.gradle/**' -g '!**/WindowsApps/**' -g '!**/System Volume Information/**' -g '!**/$RECYCLE.BIN/**' -g '!**/WindowsImageBackup/**' -g '!**/FileHistory/**' @roots 2>$null)
} elseif ($FileInventory) {
    $files = @(Get-Content -LiteralPath $FileInventory | Where-Object { $_ })
}
$artifacts = @($files | Sort-Object -Unique | ForEach-Object {
    if ((Test-Path -LiteralPath $_ -PathType Leaf) -and [IO.Path]::GetExtension($_) -notin @('.class','.java','.py','.js')) {
        $item = Get-Item -LiteralPath $_
        $kind = if ($item.Extension -eq '.gguf') { 'GGUF' } elseif ($item.Extension -eq '.safetensors') { 'SAFETENSORS' } elseif ($item.Name -like '*Modelfile*') { 'MODEL_RECIPE' } else { 'WEIGHT_CANDIDATE' }
        $status = if ($kind -eq 'GGUF' -and $item.Length -lt 1024) { 'NON_RUNNABLE_FIXTURE' } else { 'DISCOVERED_NOT_ACTIVATED' }
        [ordered]@{ path=$item.FullName; kind=$kind; sizeBytes=$item.Length; modifiedUtc=$item.LastWriteTimeUtc.ToString('o'); status=$status }
    }
})
# Retain an earlier disk scan when refreshing only the daemon catalog.
if (!$FileInventory -and !$ScanDisks -and (Test-Path -LiteralPath $OutputPath)) {
    try { $artifacts = @((Get-Content -LiteralPath $OutputPath -Raw | ConvertFrom-Json).diskArtifacts) } catch { }
}
$catalog = [ordered]@{
    schemaVersion=1; observedUtc=[DateTime]::UtcNow.ToString('o'); endpoint=$OllamaUrl
    drives=$drives; stores=$stores; models=$models; diskArtifacts=$artifacts
    notes=@('LOCAL_INSTALLED means observed daemon metadata, not benchmarked quality.',
        'Model recipes and loose weights are cataloged without executing or importing them.',
        'Different tags may share weight blobs while retaining distinct prompts and parameters.')
}
$directory = Split-Path -Parent ([IO.Path]::GetFullPath($OutputPath))
New-Item -ItemType Directory -Path $directory -Force | Out-Null
$temp = Join-Path $directory ('catalog-' + [Guid]::NewGuid().ToString('N') + '.tmp')
[IO.File]::WriteAllText($temp, ($catalog | ConvertTo-Json -Depth 12), (New-Object Text.UTF8Encoding($false)))
if (Test-Path -LiteralPath $OutputPath) { [IO.File]::Replace($temp, [IO.Path]::GetFullPath($OutputPath), ([IO.Path]::GetFullPath($OutputPath) + '.previous')) }
else { [IO.File]::Move($temp, [IO.Path]::GetFullPath($OutputPath)) }
[pscustomobject]@{catalog=$OutputPath; installed=@($models | Where-Object status -eq 'LOCAL_INSTALLED').Count; remote=@($models | Where-Object status -eq 'REMOTE_PROXY').Count; artifacts=$artifacts.Count; drives=$drives.Count} | ConvertTo-Json -Compress
