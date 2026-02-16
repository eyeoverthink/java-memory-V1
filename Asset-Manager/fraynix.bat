@echo off
REM ═══════════════════════════════════════════════════════════════════════════
REM FRAYNIX v4.0 - THE DIGITAL ORGANISM
REM ═══════════════════════════════════════════════════════════════════════════

set JAVA_OPTS=-Xmx2g

REM Check for mode argument
if "%1"=="" (
    set MODE=
) else (
    set MODE=--%1
)

REM Check for model argument
if "%2"=="" (
    set MODEL=llama3
) else (
    set MODEL=%2
)

echo Starting FRAYNIX v4.0...
echo.

java %JAVA_OPTS% -cp "build/classes/java/main;lib/*" fraymus.FraynixMain %MODE% --model=%MODEL%
