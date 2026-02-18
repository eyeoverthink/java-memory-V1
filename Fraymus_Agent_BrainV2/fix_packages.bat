@echo off
setlocal enabledelayedexpansion
set count=0
for %%f in ("d:\Zip And Send\Java-Memory\Fraymus_Agent_BrainV2\src\main\java\fraymus\CODE_Generator_UI_Backup\java_repl\*.java") do (
    powershell -Command "(Get-Content '%%f' -Raw).Replace('package repl;', 'package fraymus.CODE_Generator_UI_Backup.java_repl;') | Set-Content '%%f' -NoNewline"
    set /a count+=1
    echo Fixed: %%~nxf
)
echo Total: %count% files
