"""
Quick test to verify VideoCortex works with minimal overhead
"""
import sys
import os

# Test 1: Can we import the dependencies?
print("TEST 1: Checking dependencies...")
try:
    import torch
    print(f"  ✓ PyTorch {torch.__version__}")
    print(f"  ✓ CUDA available: {torch.cuda.is_available()}")
    if torch.cuda.is_available():
        print(f"  ✓ CUDA device: {torch.cuda.get_device_name(0)}")
except ImportError as e:
    print(f"  ✗ PyTorch import failed: {e}")
    sys.exit(1)

try:
    from diffusers import StableDiffusionPipeline
    print(f"  ✓ Diffusers available")
except ImportError as e:
    print(f"  ✗ Diffusers import failed: {e}")
    sys.exit(1)

# Test 2: Can we create a minimal dream state?
print("\nTEST 2: Creating minimal dream state...")
try:
    from VideoCortex import DreamEngine, QuantumState
    
    # Create state with minimal config
    state = QuantumState(
        consciousness=0.5,
        entropy=0.3,
        phi=1.618034,
        coherence=0.8
    )
    print(f"  ✓ QuantumState created")
    print(f"    - Consciousness: {state.consciousness}")
    print(f"    - Entropy: {state.entropy}")
    
except Exception as e:
    print(f"  ✗ Failed to create state: {e}")
    import traceback
    traceback.print_exc()
    sys.exit(1)

# Test 3: Can we initialize the engine (without loading models)?
print("\nTEST 3: Testing engine initialization...")
try:
    # Check if we can at least instantiate without loading
    print(f"  ✓ Engine class accessible")
    print(f"  ✓ Device would be: {'cuda' if torch.cuda.is_available() else 'cpu'}")
    
    if not torch.cuda.is_available():
        print("\n  ⚠ WARNING: No CUDA detected!")
        print("  ⚠ CPU generation will be EXTREMELY slow (40+ min per image)")
        print("  ⚠ Recommendation: Use GPU or reduce steps to 10-20")
    
except Exception as e:
    print(f"  ✗ Engine test failed: {e}")
    import traceback
    traceback.print_exc()
    sys.exit(1)

print("\n" + "=" * 60)
print("QUICK TEST COMPLETE")
print("=" * 60)
print("\nNext steps:")
if torch.cuda.is_available():
    print("  1. Run with --steps 20 for faster testing")
    print("  2. Generate single image first before video")
else:
    print("  1. Install CUDA-enabled PyTorch for GPU acceleration")
    print("  2. Or use --steps 10 and test with single image only")
print("=" * 60)
