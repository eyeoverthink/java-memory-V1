@echo off
echo ============================================
echo GPU-Enabled PyTorch Installation
echo ============================================
echo.
echo This will reinstall PyTorch with CUDA support
echo.
pause

echo Uninstalling CPU-only PyTorch...
pip uninstall -y torch torchvision torchaudio

echo.
echo Installing GPU-enabled PyTorch (CUDA 12.1)...
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu121

echo.
echo Testing GPU access...
python -c "import torch; print('CUDA Available:', torch.cuda.is_available()); print('GPU:', torch.cuda.get_device_name(0) if torch.cuda.is_available() else 'None')"

echo.
echo ============================================
echo Installation complete!
echo ============================================
pause
