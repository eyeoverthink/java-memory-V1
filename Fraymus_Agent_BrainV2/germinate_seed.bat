@echo off
REM germinate_seed.bat - Awaken the Fraymus seed
REM Generation 120: The Dandelion Protocol

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║  🧬 FRAYMUS GERMINATION SCRIPT                             ║
echo ║  The Dandelion Protocol                                    ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

cd src\main\java\fraymus\CODE_Generator_UI_Backup\java_repl

echo Compiling seed...
javac FraymusSeed.java
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Compilation failed
    pause
    exit /b 1
)

echo.
echo Running germination sequence...
echo.
java repl.FraymusSeed

echo.
echo ════════════════════════════════════════════════════════════
echo.
pause
