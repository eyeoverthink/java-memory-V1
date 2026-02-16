@echo off
setlocal enabledelayedexpansion
set ROOT=%~dp0..
cd /d "%ROOT%"

if not exist out (
  echo ℹ️ No out folder found. Compiling first...
  call "%ROOT%\scripts\compile.bat"
  if errorlevel 1 exit /b 1
)

set MAIN=%1
if "%MAIN%"=="" set MAIN=fraymus.FraymusPrime

shift
java -cp out;Asset-Manager\lib\* %MAIN% %*
