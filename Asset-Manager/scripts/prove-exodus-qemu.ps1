param(
    [string]$ImagePath = "$PSScriptRoot\..\fraynix_genesis.img",
    [string]$OutputPath = "$PSScriptRoot\..\build\reports\exodus-qemu-screen.ppm",
    # Retained for existing callers; the verifier now uses QMP over standard I/O.
    [int]$MonitorPort = 45454
)

$ErrorActionPreference = "Stop"
$python = Get-Command python -ErrorAction Stop
$image = (Resolve-Path -LiteralPath $ImagePath).Path
$output = [IO.Path]::GetFullPath($OutputPath)
$parent = Split-Path -Parent $output
New-Item -ItemType Directory -Force -Path $parent | Out-Null
$proofRoot = Join-Path $parent ("exodus-proof-" + [guid]::NewGuid().ToString("N"))
$verificationOutput = & $python.Source "$PSScriptRoot\verify-exodus.py" --image $image --output $proofRoot
if ($LASTEXITCODE -ne 0) {
    throw "Exodus boot verification failed. Evidence: $proofRoot\report.json`n$verificationOutput"
}
$report = Get-Content -LiteralPath (Join-Path $proofRoot "report.json") -Raw | ConvertFrom-Json
if (-not $report.passed) {
    throw "Exodus verification did not pass. Evidence: $proofRoot\report.json"
}
Copy-Item -LiteralPath (Join-Path $proofRoot "repeated_input.ppm") -Destination $output -Force
[PSCustomObject]@{
    passed = $true
    image = $image
    sha256 = $report.sha256_after
    screenshot = $output
    report = (Join-Path $proofRoot "report.json")
} | ConvertTo-Json
