@echo off
echo ╔═══════════════════════════════════════════════════════════╗
echo ║ SPATIAL COMPUTING STANDALONE TEST                         ║
echo ╚═══════════════════════════════════════════════════════════╝
echo.

mkdir build\classes 2>nul

echo [1/6] Compiling PhiNode...
javac -d build/classes src/main/java/fraymus/core/PhiNode.java
if %errorlevel% neq 0 (
    echo ❌ FAILED: PhiNode
    exit /b 1
)
echo ✅ PhiNode compiled

echo [2/6] Compiling Lazarus...
javac -d build/classes -cp build/classes src/main/java/fraymus/core/Lazarus.java
if %errorlevel% neq 0 (
    echo ❌ FAILED: Lazarus
    exit /b 1
)
echo ✅ Lazarus compiled

echo [3/6] Compiling PhiSuit...
javac -d build/classes -cp build/classes src/main/java/fraymus/core/PhiSuit.java
if %errorlevel% neq 0 (
    echo ❌ FAILED: PhiSuit
    exit /b 1
)
echo ✅ PhiSuit compiled

echo [4/6] Compiling GravityEngine...
javac -d build/classes -cp build/classes src/main/java/fraymus/core/GravityEngine.java
if %errorlevel% neq 0 (
    echo ❌ FAILED: GravityEngine
    exit /b 1
)
echo ✅ GravityEngine compiled

echo [5/6] Compiling FusionReactor...
javac -d build/classes -cp build/classes src/main/java/fraymus/core/FusionReactor.java
if %errorlevel% neq 0 (
    echo ❌ FAILED: FusionReactor
    exit /b 1
)
echo ✅ FusionReactor compiled

echo [6/6] Compiling CreativeEngine...
javac -d build/classes -cp build/classes src/main/java/fraymus/core/CreativeEngine.java
if %errorlevel% neq 0 (
    echo ❌ FAILED: CreativeEngine
    exit /b 1
)
echo ✅ CreativeEngine compiled

echo.
echo ╔═══════════════════════════════════════════════════════════╗
echo ║ SPATIAL COMPUTING CORE: COMPILATION SUCCESSFUL            ║
echo ╚═══════════════════════════════════════════════════════════╝
echo.

echo Testing BigBang demo...
javac -d build/classes -cp build/classes src/main/java/fraymus/core/BigBang.java
if %errorlevel% neq 0 (
    echo ❌ FAILED: BigBang demo
    exit /b 1
)
echo ✅ BigBang demo compiled

echo.
echo Running BigBang demo (10 seconds)...
echo.
java -cp build/classes fraymus.core.BigBang

echo.
echo ╔═══════════════════════════════════════════════════════════╗
echo ║ TEST COMPLETE                                             ║
echo ╚═══════════════════════════════════════════════════════════╝
