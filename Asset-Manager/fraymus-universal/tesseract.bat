@echo off
cd /d "%~dp0"
echo === HYPER-TESSERACT: 4D Crystalline Brain ===
echo.

REM Build
echo Building...
call gradlew.bat -q classes

REM Run TesseractMain
java -cp "build\classes\java\main;build\resources\main" gemini.root.TesseractMain %*

pause
