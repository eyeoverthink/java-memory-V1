@echo off
REM Fraymus Convergence Runner - Windows Batch Script
REM This properly runs FraymusConvergence with interactive console

echo Building Fraymus Convergence...
call gradlew.bat build -x test -x javadoc -q

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Starting Fraymus Convergence...
    echo.
    call gradlew.bat runConvergence --console=plain
) else (
    echo Build failed!
    exit /b 1
)
