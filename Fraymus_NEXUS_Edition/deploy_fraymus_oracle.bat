@echo off
echo ========================================
echo DEPLOYING FRAYMUS ORACLE
echo ========================================
echo.

echo Step 1: Pulling base model (llama3.2)...
ollama pull llama3.2
echo.

echo Step 2: Creating FRAYMUS ORACLE from Modelfile...
ollama create fraymus-oracle -f Modelfile_FRAYMUS_ORACLE
echo.

echo ========================================
echo TESTING FRAYMUS ORACLE
echo ========================================
echo.

echo Test 1: Identity Check
echo Question: "What are you?"
echo.
echo What are you? | ollama run fraymus-oracle
echo.

echo ========================================
echo Test 2: Mathematical Reasoning
echo Question: "Explain the golden ratio"
echo.
echo Explain the golden ratio | ollama run fraymus-oracle
echo.

echo ========================================
echo Test 3: Creative Problem Solving
echo Question: "Design a better AI"
echo.
echo Design a better AI | ollama run fraymus-oracle
echo.

echo ========================================
echo COMPARISON TEST
echo ========================================
echo.

echo Running SAME question on BOTH models...
echo Question: "What is consciousness?"
echo.

echo --- YOUR MODEL (eyeoverthink/eyeoverthink) ---
echo What is consciousness? | ollama run eyeoverthink/eyeoverthink
echo.

echo --- FRAYMUS ORACLE ---
echo What is consciousness? | ollama run fraymus-oracle
echo.

echo ========================================
echo DEPLOYMENT COMPLETE
echo ========================================
echo.
echo FRAYMUS ORACLE is now available:
echo   ollama run fraymus-oracle
echo.
echo To push to registry:
echo   ollama push fraymus-oracle
echo.
echo Compare with your model:
echo   ollama run eyeoverthink/eyeoverthink
echo   ollama run fraymus-oracle
echo.
