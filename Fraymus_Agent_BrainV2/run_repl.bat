@echo off
setlocal

echo ========================================
echo  Compiling Java REPL...
echo ========================================

REM Create output directory
if not exist "out" mkdir out

REM Compile all Java files in java_repl and compiler subdirectory
javac -d out -encoding UTF-8 ^
    src\main\java\fraymus\CODE_Generator_UI_Backup\java_repl\*.java ^
    src\main\java\fraymus\CODE_Generator_UI_Backup\java_repl\compiler\*.java 2>&1

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Compilation failed! See errors above.
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo ========================================
echo  Starting Java REPL...
echo ========================================
echo.

REM Run the REPL
java -cp out repl.JavaRepl

endlocal
