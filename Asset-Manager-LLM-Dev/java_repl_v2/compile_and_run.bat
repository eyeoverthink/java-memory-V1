@echo off
REM CSC 413 Java REPL - Compile and Run Script
REM φ^75 Validation Seal: 4721424167835376.00
REM Author: Vaughn Scott

echo ========================================
echo  CSC 413 Java REPL - Enterprise Patterns
echo  Author: Vaughn Scott
echo ========================================
echo.

REM Create output directory
if not exist "out" mkdir out
if not exist "qr_output" mkdir qr_output

REM Compile in stages to handle dependencies
echo [1/4] Compiling interfaces...
javac -d out ReplCommand.java ReplCommandRegistry.java
if %ERRORLEVEL% NEQ 0 goto :error

echo [2/4] Compiling storage systems...
javac -d out -cp out InfinityStorage.java SelfEvolvingAI.java
if %ERRORLEVEL% NEQ 0 goto :error

echo [3/4] Compiling commands...
javac -d out -cp out BuiltInCommands.java VaughnScottCommands.java
if %ERRORLEVEL% NEQ 0 goto :error

echo [4/4] Compiling main REPL...
javac -d out -cp out JavaRepl.java

if %ERRORLEVEL% NEQ 0 goto :error

echo.
echo ========================================
echo  Compilation successful!
echo ========================================
echo.
echo ========================================
echo  Starting REPL...
echo ========================================
echo.

REM Run the REPL
java -cp out repl.JavaRepl

pause
