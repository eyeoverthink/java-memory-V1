# Fraymus Convergence Runner
# This script properly runs FraymusConvergence with interactive console

Write-Host "Building Fraymus Convergence..." -ForegroundColor Cyan
& .\gradlew.bat build -x test -x javadoc -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "Starting Fraymus Convergence..." -ForegroundColor Green
    Write-Host ""
    
    # Run with proper console attachment
    & .\gradlew.bat runConvergence --console=plain
} else {
    Write-Host "Build failed!" -ForegroundColor Red
    exit 1
}
