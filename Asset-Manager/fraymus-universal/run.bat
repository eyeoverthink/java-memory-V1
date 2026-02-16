@echo off
cd /d "%~dp0"
echo === FRAYMUS UNIVERSAL ===
echo.

REM Build fat JAR
echo Building fat JAR...
call gradlew.bat -q jar

REM Run directly
echo Starting WebSocket server on port 8887...
java -jar "build\libs\fraymus-universal-1.0.0.jar" %*
