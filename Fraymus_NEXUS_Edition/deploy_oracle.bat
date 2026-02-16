@echo off
echo ========================================
echo DEPLOYING FRAYMUS ORACLE TO OLLAMA
echo ========================================
echo.

echo Step 1: Pulling base model...
ollama pull llama3.2
echo.

echo Step 2: Creating fraymus-oracle from Modelfile...
ollama create fraymus-oracle -f Modelfile
echo.

echo Step 3: Testing the Oracle...
echo What are you? | ollama run fraymus-oracle
echo.

echo ========================================
echo DEPLOYMENT COMPLETE
echo ========================================
echo.
echo To use: ollama run fraymus-oracle
echo To push: ollama push fraymus-oracle
echo.
pause
