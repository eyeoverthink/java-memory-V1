@echo off
setlocal enabledelayedexpansion
set ROOT=%~dp0..
cd /d "%ROOT%"

echo Collecting Java sources...
if exist sources.txt del sources.txt
for /r "Asset-Manager\src\main\java" %%f in (*.java) do echo %%f>> sources.txt

echo Cleaning output directory...
if exist out rmdir /s /q out
mkdir out

echo Compiling with UTF-8 encoding...
javac -encoding UTF-8 -d out -cp "Asset-Manager\lib\*" @sources.txt
if errorlevel 1 (
  echo ❌ Compilation failed.
  exit /b 1
)

echo ✅ Compiled to %ROOT%\out
del sources.txt
