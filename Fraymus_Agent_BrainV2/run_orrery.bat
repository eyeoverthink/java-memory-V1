@echo off
REM Fraymus Orrery Launcher
REM Requires Processing core.jar

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║  🌌 FRAYMUS ORRERY - UNIVERSE MODEL                        ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo "The Orrery of Logic."
echo.

REM Check if Processing core.jar exists
if not exist "lib\core.jar" (
    echo ❌ ERROR: Processing core.jar not found!
    echo.
    echo Please download Processing core library:
    echo   1. Visit: https://processing.org/download
    echo   2. Extract core.jar from Processing installation
    echo   3. Place in: lib\core.jar
    echo.
    echo Or use Maven to download dependencies.
    echo.
    pause
    exit /b 1
)

echo ⚡ Compiling UniverseModel.java...
javac -cp "lib\core.jar" -d out src\main\java\fraymus\sim\UniverseModel.java

if errorlevel 1 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.
echo 🚀 Launching Orrery...
echo.
java -cp "lib\core.jar;out" fraymus.sim.UniverseModel

pause
