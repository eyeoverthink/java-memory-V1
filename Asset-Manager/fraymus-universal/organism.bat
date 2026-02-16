@echo off
cd /d "%~dp0"
echo === FRAYNIX ORGANISM: Digital Lifeform ===
echo.

REM Build
echo Building...
call gradlew.bat -q classes

REM Run FraynixOrganism with current directory as root
echo Starting organism...
java -cp "build\classes\java\main;build\resources\main" gemini.root.FraynixOrganism %*
