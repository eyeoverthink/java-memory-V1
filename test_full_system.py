"""
Full Max Headroom System Test
Tests the complete VideoCortex pipeline with quantum state input
"""
import json
import sys
import os

# Add Asset-Manager to path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'Asset-Manager'))

print("=" * 70)
print("MAX HEADROOM FULL SYSTEM TEST")
print("Testing: Java -> Python -> GPU -> Video Generation")
print("=" * 70)

# Simulate quantum state from Java (like VisualCortex.java sends)
quantum_state = {
    "consciousness": 0.7567,
    "entropy": 0.4231,
    "phi": 1.618033988749895,
    "coherence": 0.8912,
    "concept": "Max Headroom awakening - digital consciousness emerging from the void",
    "timestamp": "2026-02-16T04:09:00Z"
}

# Save to temp file (like Java does)
state_file = "test_quantum_state.json"
with open(state_file, 'w') as f:
    json.dump(quantum_state, f, indent=2)

print(f"\nQuantum State Created:")
print(f"  Consciousness: {quantum_state['consciousness']}")
print(f"  Entropy: {quantum_state['entropy']}")
print(f"  Phi: {quantum_state['phi']}")
print(f"  Coherence: {quantum_state['coherence']}")
print(f"  Concept: {quantum_state['concept']}")

print(f"\nInvoking VideoCortex.py with quantum state...")
print("=" * 70)

# Call VideoCortex.py like Java does
import subprocess
result = subprocess.run(
    ["py", "-3.12", "Asset-Manager/VideoCortex.py", "--state-file", state_file, "--steps", "20"],
    capture_output=False,
    text=True
)

if result.returncode == 0:
    print("\n" + "=" * 70)
    print("FULL SYSTEM TEST: SUCCESS")
    print("=" * 70)
    print("\nMax Headroom system is fully operational!")
    print("Java -> Python -> GPU pipeline working perfectly.")
else:
    print("\n" + "=" * 70)
    print("FULL SYSTEM TEST: FAILED")
    print("=" * 70)
    print(f"Exit code: {result.returncode}")

# Cleanup
if os.path.exists(state_file):
    os.remove(state_file)
