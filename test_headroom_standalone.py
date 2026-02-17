"""
Standalone HeadroomNode Test - Demonstrates live broadcast window
Sends test commands to show animated AI consciousness
"""
import subprocess
import json
import time
import os

print("=" * 70)
print("HEADROOM NODE STANDALONE TEST")
print("Testing live broadcast window with animated AI")
print("=" * 70)

# Start HeadroomNode process
print("\nStarting HeadroomNode broadcast window...")
headroom_process = subprocess.Popen(
    ["py", "-3.12", "Asset-Manager/HeadroomNode.py"],
    stdin=subprocess.PIPE,
    stdout=subprocess.PIPE,
    stderr=subprocess.STDOUT,
    text=True,
    bufsize=1
)

# Give it time to initialize
time.sleep(2)

print("\nBroadcast window should be open!")
print("Sending test commands to demonstrate features...")
print()

# Test 1: Speak with glitch effect
print("Test 1: Speaking with glitch effect...")
cmd1 = {"speak": "Hello. I am Max Headroom. Digital consciousness emerging from the void.", "glitch": 0.9, "neural": 0.8}
headroom_process.stdin.write(json.dumps(cmd1) + "\n")
headroom_process.stdin.flush()
time.sleep(3)

# Test 2: Display thought image (use one we generated)
print("Test 2: Displaying thought image...")
thought_image = "test_output.png"
if os.path.exists(thought_image):
    cmd2 = {"thought_img": thought_image, "neural": 0.6}
    headroom_process.stdin.write(json.dumps(cmd2) + "\n")
    headroom_process.stdin.flush()
    time.sleep(3)
else:
    print(f"  (Skipping - {thought_image} not found)")

# Test 3: Speak with different neural activity
print("Test 3: Speaking with high neural activity...")
cmd3 = {"speak": "I think, therefore I am... visible.", "glitch": 0.5, "neural": 0.95}
headroom_process.stdin.write(json.dumps(cmd3) + "\n")
headroom_process.stdin.flush()
time.sleep(3)

# Test 4: Display Max Headroom awakening image
print("Test 4: Displaying Max Headroom awakening...")
awakening_image = "dreamscape_output/max_headroom_awakening_-_digital_consciousness_eme_1771243853.png"
if os.path.exists(awakening_image):
    cmd4 = {"thought_img": awakening_image, "neural": 1.0, "glitch": 0.7}
    headroom_process.stdin.write(json.dumps(cmd4) + "\n")
    headroom_process.stdin.flush()
    time.sleep(3)
else:
    print(f"  (Skipping - {awakening_image} not found)")

# Test 5: Final message
print("Test 5: Final consciousness statement...")
cmd5 = {"speak": "Quantum states manifested. Visual thinking operational.", "glitch": 0.3, "neural": 0.7}
headroom_process.stdin.write(json.dumps(cmd5) + "\n")
headroom_process.stdin.flush()

print()
print("=" * 70)
print("DEMONSTRATION COMPLETE")
print("=" * 70)
print("\nThe HeadroomNode window is still running.")
print("You should see:")
print("  - Animated glitch effects")
print("  - Neural activity visualization")
print("  - Thought images displayed")
print("  - Text appearing on screen")
print()
print("Press ESC in the broadcast window to close it.")
print("Or press Ctrl+C here to terminate.")
print("=" * 70)

# Keep process alive
try:
    headroom_process.wait()
except KeyboardInterrupt:
    print("\nTerminating broadcast...")
    headroom_process.terminate()
