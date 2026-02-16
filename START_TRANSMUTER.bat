@echo off
echo ╔═══════════════════════════════════════════════════════════════╗
echo ║                                                               ║
echo ║          🧬 FRAYMUS TRANSMUTER // GEN 194                     ║
echo ║          Bicameral Code Optimization System                   ║
echo ║                                                               ║
echo ╚═══════════════════════════════════════════════════════════════╝
echo.

cd Asset-Manager

echo [1/3] Compiling Java backend...
call gradlew.bat compileJava -x test -x javadoc -q
if errorlevel 1 (
    echo ❌ Compilation failed
    pause
    exit /b 1
)
echo ✅ Compilation successful
echo.

echo [2/3] Starting Nervous System backend...
echo.
echo ⚡ Backend will start on http://localhost:8080
echo ⚡ Press Ctrl+C to stop the server
echo.
echo NOTE: Make sure Ollama is running:
echo   ollama serve
echo.
echo Available models (check with: ollama list):
echo   - llama3.2 (lightweight, fast)
echo   - llama3:70b (powerful, slow)
echo   - codellama (code-specialized)
echo.

start "Fraymus Nervous System" java -cp build/classes/java/main fraymus.web.NervousSystem

timeout /t 3 /nobreak > nul

echo [3/3] Opening transmuter interface...
cd ..
start Fraymus_Transmuter.html

echo.
echo ╔═══════════════════════════════════════════════════════════════╗
echo ║  SYSTEM ACTIVE                                                ║
echo ║  Backend: http://localhost:8080                               ║
echo ║  Interface: Fraymus_Transmuter.html                           ║
echo ╚═══════════════════════════════════════════════════════════════╝
echo.
echo Press any key to stop the backend...
pause > nul

taskkill /FI "WINDOWTITLE eq Fraymus Nervous System*" /F > nul 2>&1
echo Backend stopped.
