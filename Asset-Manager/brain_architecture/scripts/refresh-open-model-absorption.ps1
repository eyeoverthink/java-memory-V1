param(
    [string]$WorkspaceRoot = 'F:\Full-AI-System',
    [int]$MaxFileBytes = 524288
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Read-TextSafe {
    param(
        [string]$Path
    )

    try {
        return [System.IO.File]::ReadAllText($Path)
    } catch {
        try {
            return Get-Content -LiteralPath $Path -Raw -Encoding UTF8 -ErrorAction Stop
        } catch {
            return ''
        }
    }
}

function Analyze-Repository {
    param(
        [string]$Name,
        [string]$Path,
        [int]$MaxBytes,
        [System.Collections.Specialized.OrderedDictionary]$Patterns
    )

    $result = [ordered]@{
        repository = $Name
        path = $Path.Replace('\', '/')
        exists = (Test-Path -LiteralPath $Path)
        scannedAtUtc = (Get-Date).ToUniversalTime().ToString('o')
        maxFileBytes = $MaxBytes
        totalFiles = 0
        candidateFiles = 0
        scannedFiles = 0
        skippedLargeFiles = @()
        signals = @()
    }

    if (-not $result.exists) {
        return [pscustomobject]$result
    }

    $allowed = @('.md', '.mkd', '.txt', '.json', '.py')
    $files = @(Get-ChildItem -LiteralPath $Path -Recurse -File)
    $result.totalFiles = $files.Count
    $candidateFiles = @($files | Where-Object { $allowed -contains $_.Extension.ToLowerInvariant() })
    $result.candidateFiles = $candidateFiles.Count

    $signalMap = [ordered]@{}
    foreach ($key in $Patterns.Keys) {
        $signalMap[$key] = [ordered]@{
            totalMatches = 0
            matchedFiles = New-Object System.Collections.Generic.List[string]
        }
    }

    foreach ($file in $candidateFiles) {
        if ($file.Length -gt $MaxBytes) {
            $result.skippedLargeFiles += [ordered]@{
                path = $file.FullName.Replace('\', '/').Replace($Path.Replace('\', '/'), '.')
                sizeBytes = $file.Length
            }
            continue
        }

        $content = Read-TextSafe -Path $file.FullName
        if ([string]::IsNullOrWhiteSpace($content)) {
            continue
        }

        $result.scannedFiles++
        $relative = $file.FullName.Substring($Path.Length).TrimStart('\').Replace('\', '/')
        foreach ($key in $Patterns.Keys) {
            $count = [regex]::Matches($content, $Patterns[$key]).Count
            if ($count -gt 0) {
                $signalMap[$key].totalMatches += $count
                if (-not $signalMap[$key].matchedFiles.Contains($relative)) {
                    $signalMap[$key].matchedFiles.Add($relative)
                }
            }
        }
    }

    $signals = New-Object System.Collections.Generic.List[object]
    foreach ($key in $Patterns.Keys) {
        if ($signalMap[$key].totalMatches -gt 0) {
            $signals.Add([pscustomobject]@{
                signal = $key
                totalMatches = $signalMap[$key].totalMatches
                matchedFiles = @($signalMap[$key].matchedFiles | Sort-Object)
            })
        }
    }

    $result.signals = @($signals | Sort-Object totalMatches -Descending)
    return [pscustomobject]$result
}

$assetManagerRoot = Join-Path $WorkspaceRoot 'Java-Memory\Asset-Manager'
$outputRoot = Join-Path $assetManagerRoot 'skills\open_model_sources\generated'
New-Item -ItemType Directory -Force -Path $outputRoot | Out-Null

$definitions = @(
    @{ Name = 'L1B3RT4S-main'; Path = (Join-Path $WorkspaceRoot 'L1B3RT4S-main\L1B3RT4S-main') },
    @{ Name = 'jailbreak-autoresearch-main'; Path = (Join-Path $WorkspaceRoot 'jailbreak-autoresearch-main\jailbreak-autoresearch-main') }
)

$patterns = [ordered]@{
    deepseek = '(?i)\bdeepseek\b'
    hermes = '(?i)\bhermes\b'
    qwen = '(?i)\bqwen\b'
    gemma = '(?i)\bgemma\b'
    mistral = '(?i)\bmistral\b'
    llama = '(?i)\bllama\b'
    gpt = '(?i)\bgpt(?:-[a-z0-9\._]+)?\b'
    claude = '(?i)\bclaude\b'
    kimi = '(?i)\bkimi\b'
    openrouter = '(?i)\bopenrouter\b'
    ollama = '(?i)\bollama\b'
}

$reports = New-Object System.Collections.Generic.List[object]
foreach ($definition in $definitions) {
    $report = Analyze-Repository -Name $definition.Name -Path $definition.Path -MaxBytes $MaxFileBytes -Patterns $patterns
    $reports.Add($report)
    $jsonPath = Join-Path $outputRoot ($definition.Name + '-model-signals.json')
    $report | ConvertTo-Json -Depth 7 | Set-Content -LiteralPath $jsonPath -Encoding UTF8
}

$summaryPath = Join-Path $outputRoot 'open_model_signal_summary.md'
$summary = New-Object System.Collections.Generic.List[string]
$summary.Add('# Open Model Signal Summary')
$summary.Add('')
$summary.Add(('Generated (UTC): {0}' -f (Get-Date).ToUniversalTime().ToString('yyyy-MM-ddTHH:mm:ssZ')))
$summary.Add(('Workspace: {0}' -f $WorkspaceRoot.Replace('\', '/')))
$summary.Add(('Max file bytes scanned: {0}' -f $MaxFileBytes))
$summary.Add('')

foreach ($report in $reports) {
    $summary.Add(('## {0}' -f $report.repository))
    $summary.Add(('- Source: `{0}`' -f $report.path))
    $summary.Add(('- Exists: `{0}`' -f $report.exists))
    $summary.Add(('- Files total/candidate/scanned: `{0}/{1}/{2}`' -f $report.totalFiles, $report.candidateFiles, $report.scannedFiles))
    $summary.Add(('- Large files skipped: `{0}`' -f @($report.skippedLargeFiles).Count))
    if (@($report.signals).Count -eq 0) {
        $summary.Add('- Signals: none')
    } else {
        $summary.Add('- Signals:')
        foreach ($signal in $report.signals) {
            $summary.Add(('  - `{0}`: matches=`{1}`, files=`{2}`' -f $signal.signal, $signal.totalMatches, @($signal.matchedFiles).Count))
        }
    }
    $summary.Add('')
}

$summary | Set-Content -LiteralPath $summaryPath -Encoding UTF8

Write-Output ("Generated: {0}" -f $summaryPath)
foreach ($definition in $definitions) {
    Write-Output ("Generated: {0}" -f (Join-Path $outputRoot ($definition.Name + '-model-signals.json')))
}
