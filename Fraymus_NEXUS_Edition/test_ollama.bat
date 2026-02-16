@echo off
echo Testing Ollama Integration with eyeoverthink/Fraymus
echo =====================================================
echo.

echo Test 1: Check if Ollama is running
curl -s http://localhost:11434/api/tags > nul 2>&1
if %errorlevel% neq 0 (
    echo [FAIL] Ollama service not responding
    echo Please start Ollama first
    exit /b 1
)
echo [PASS] Ollama service is running
echo.

echo Test 2: Check if eyeoverthink/Fraymus model exists
ollama list | findstr /i "eyeoverthink/Fraymus" > nul 2>&1
if %errorlevel% neq 0 (
    echo [FAIL] Model eyeoverthink/Fraymus not found
    echo Available models:
    ollama list
    exit /b 1
)
echo [PASS] Model eyeoverthink/Fraymus found
echo.

echo Test 3: Test /api/chat endpoint with eyeoverthink/Fraymus
echo Request: Hello!
curl -s http://localhost:11434/api/chat -d "{\"model\":\"eyeoverthink/Fraymus\",\"messages\":[{\"role\":\"user\",\"content\":\"Hello!\"}],\"stream\":false}"
echo.
echo.

echo Test 4: Test /api/generate endpoint with eyeoverthink/Fraymus
echo Request: What is the golden ratio?
curl -s http://localhost:11434/api/generate -d "{\"model\":\"eyeoverthink/Fraymus\",\"prompt\":\"What is the golden ratio?\",\"stream\":false}"
echo.
echo.

echo =====================================================
echo Tests complete
echo.
echo If you see responses above, Ollama is working correctly.
echo Your Java code should work with these same endpoints.
pause
