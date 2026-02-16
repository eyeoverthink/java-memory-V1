@echo off
echo ╔═══════════════════════════════════════════════════════════╗
echo ║ FRAYMUS COMPILATION TEST                                  ║
echo ╚═══════════════════════════════════════════════════════════╝
echo.

echo [1/3] Compiling core spatial computing classes...
javac -d build/classes ^
  src/main/java/fraymus/core/PhiNode.java ^
  src/main/java/fraymus/core/PhiSuit.java ^
  src/main/java/fraymus/core/Lazarus.java ^
  src/main/java/fraymus/core/GravityEngine.java ^
  src/main/java/fraymus/core/FusionReactor.java ^
  src/main/java/fraymus/core/CreativeEngine.java

if %errorlevel% neq 0 (
    echo ❌ FAILED: Core spatial computing classes
    exit /b 1
)
echo ✅ Core spatial computing classes compiled

echo.
echo [2/3] Compiling command system...
javac -d build/classes -cp build/classes ^
  src/main/java/fraymus/CommandValidator.java ^
  src/main/java/fraymus/CommandTerminalSpatial.java ^
  src/main/java/fraymus/CommandTerminalAPI.java

if %errorlevel% neq 0 (
    echo ❌ FAILED: Command system
    exit /b 1
)
echo ✅ Command system compiled

echo.
echo [3/3] Compiling REST API...
javac -d build/classes -cp build/classes ^
  src/main/java/fraymus/api/FraymusAPI.java

if %errorlevel% neq 0 (
    echo ❌ FAILED: REST API
    exit /b 1
)
echo ✅ REST API compiled

echo.
echo ╔═══════════════════════════════════════════════════════════╗
echo ║ COMPILATION SUCCESSFUL                                    ║
echo ╚═══════════════════════════════════════════════════════════╝
echo.
echo All new components compiled successfully:
echo   ✓ PhiNode, PhiSuit, Lazarus
echo   ✓ GravityEngine, FusionReactor, CreativeEngine
echo   ✓ CommandValidator, CommandTerminalSpatial, CommandTerminalAPI
echo   ✓ FraymusAPI
echo.
