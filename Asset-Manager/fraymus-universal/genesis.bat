@echo off
cd /d "%~dp0"
echo === SINGULARITY ENGINE: GENESIS BOOT ===
echo.

REM Build
echo Building...
call gradlew.bat -q classes

REM Run GenesisBoot with arguments
echo Starting Genesis Engine...
java -cp "build\classes\java\main;build\resources\main" gemini.root.GenesisBoot %*

pause
