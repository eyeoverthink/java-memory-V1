@echo off
echo ========================================
echo Testing OpenAI Integration
echo ========================================
echo.

cd /d "%~dp0"

echo Building project...
call gradlew.bat build -x test -x javadoc
if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Starting FraymusConvergence with OpenAI...
echo.

java -jar build\libs\fraymus-convergence.jar

pause
