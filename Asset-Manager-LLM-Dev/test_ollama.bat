@echo off
echo ============================================
echo OLLAMA SANDBOX TEST
echo ============================================
echo.
echo This will test Ollama connection before integrating into FRAYMUS.
echo.
echo Prerequisites:
echo   1. Ollama installed and running (ollama serve)
echo   2. A model pulled (ollama pull llama3.2)
echo.
pause

cd /d "d:\Zip And Send\Java-Memory\Asset-Manager-LLM-Dev"
gradle run -PmainClass=fraymus.OllamaClient --console=plain

pause
