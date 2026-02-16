@echo off
REM ═══════════════════════════════════════════════════════════════════════════
REM  FRAYNIX OS V1 - Launcher
REM ═══════════════════════════════════════════════════════════════════════════

setlocal enabledelayedexpansion

set JAVA_OPTS=-Xmx512m -XX:+UseG1GC

REM Compile if needed
if not exist "target\classes" (
    echo Compiling Fraynix OS...
    if not exist "target\classes" mkdir target\classes
    
    dir /s /b src\main\java\*.java > sources.txt
    javac -d target\classes @sources.txt
    del sources.txt
    
    if errorlevel 1 (
        echo Compilation failed!
        pause
        exit /b 1
    )
)

echo.
echo Starting Fraynix OS V1...
echo.

java %JAVA_OPTS% -cp target\classes fraynix.FraynixOS %*

endlocal
