@echo off
cd /d "%~dp0"

echo === FRAYMUS UNIVERSAL INTERFACE ===
echo.

REM Build fat jar if not exists
if not exist "build\libs\fraymus-universal-1.0.0.jar" (
    echo Building fat jar...
    call gradlew.bat jar
)

REM Run with fat jar
java -jar "build\libs\fraymus-universal-1.0.0.jar" %*
pause
