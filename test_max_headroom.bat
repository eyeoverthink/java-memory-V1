@echo off
echo Testing Max Headroom with GPU...
echo.
cd Asset-Manager
py -3.12 VideoCortex.py --state-file test_state.json --steps 20
pause
