@echo off
echo ============================================
echo FRAYMUS LLaMA Server Setup
echo ============================================

echo Creating Python virtual environment...
python -m venv venv

echo Activating venv...
call venv\Scripts\activate.bat

echo Installing dependencies...
pip install torch transformers accelerate bitsandbytes flask sentencepiece

echo.
echo ============================================
echo Setup complete!
echo.
echo To start the server:
echo   1. cd llm_server
echo   2. venv\Scripts\activate.bat
echo   3. python llama_server.py
echo ============================================
