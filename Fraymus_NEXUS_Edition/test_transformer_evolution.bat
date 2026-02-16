@echo off
echo ╔═══════════════════════════════════════════════════════════╗
echo ║ TRANSFORMER EVOLUTION TEST                                ║
echo ╚═══════════════════════════════════════════════════════════╝
echo.

mkdir build\classes 2>nul

echo [1/2] Compiling TransformerMutation...
javac -d build/classes src/main/java/fraymus/core/TransformerMutation.java
if %errorlevel% neq 0 (
    echo ❌ FAILED: TransformerMutation
    exit /b 1
)
echo ✅ TransformerMutation compiled

echo [2/2] Compiling TransformerEvolutionDemo...
javac -d build/classes -cp build/classes src/main/java/fraymus/core/TransformerEvolutionDemo.java
if %errorlevel% neq 0 (
    echo ❌ FAILED: TransformerEvolutionDemo
    exit /b 1
)
echo ✅ TransformerEvolutionDemo compiled

echo.
echo ╔═══════════════════════════════════════════════════════════╗
echo ║ RUNNING TRANSFORMER EVOLUTION DEMO                        ║
echo ╚═══════════════════════════════════════════════════════════╝
echo.

java -cp build/classes fraymus.core.TransformerEvolutionDemo

echo.
echo ╔═══════════════════════════════════════════════════════════╗
echo ║ TEST COMPLETE                                             ║
echo ╚═══════════════════════════════════════════════════════════╝
