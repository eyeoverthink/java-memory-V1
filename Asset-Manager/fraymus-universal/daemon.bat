@echo off
cd /d "%~dp0"
echo ╔═══════════════════════════════════════════════════════╗
echo ║   FRAYNIX DAEMON: Living Digital Organism             ║
echo ╚═══════════════════════════════════════════════════════╝
echo.

REM Build
echo Building organism...
call gradlew.bat -q classes

if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Starting FRAYNIX ORGANISM...
echo   - Brain: 2,048 nodes (4D Tesseract)
echo   - Heart: 4-phase cognitive loop
echo   - Eyes: Cortex visualizer (ws://localhost:8888)
echo   - Hands: Genesis architect + OpenClaw
echo.
echo Open web\fraymus_eye.html in browser to visualize the brain.
echo.

REM Run FraynixOrganism
java -cp "build\classes\java\main;build\resources\main" gemini.root.FraynixOrganism %*
