@echo off
echo ========================================
echo FRAYNIX OS + FraymusConvergence Launcher
echo ========================================
echo.
echo Starting FraymusConvergence with WebSocket server...
echo WebSocket: ws://localhost:8082
echo HTTP: http://localhost:8083/fraynix-os.html
echo.

REM Build the project first
call gradlew.bat build -x test

REM Start FraymusConvergence in interactive mode
java -cp build/libs/Asset-Manager.jar;build/install/Asset-Manager/lib/* fraymus.FraymusConvergence

pause
