@echo off
cd /d "%~dp0"
echo === FRAYNIX HIVE: Gravity-Driven Execution ===
echo.

REM Build
echo Building...
call gradlew.bat -q classes

REM Run FraynixHive
java -cp "build\classes\java\main;build\resources\main" gemini.root.FraynixHive %*
