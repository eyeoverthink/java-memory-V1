"""
Max Headroom with OpenClaw Integration
Uses YOUR local OpenClaw AI + GPU Visual Generation + HeadroomNode Display
Real AI brain, real visual thinking, real-time display
"""
import subprocess
import json
import time
import torch
from diffusers import StableDiffusionPipeline
import os
import sys

print("=" * 70)
print("MAX HEADROOM + OPENCLAW INTEGRATION")
print("Your Local AI Brain + GPU Visuals + Live Broadcast")
print("=" * 70)

# Configuration
OPENCLAW_PATH = "Asset-Manager/openclaw-main/openclaw-main"

# Check if OpenClaw is available
if not os.path.exists(OPENCLAW_PATH):
    print(f"ERROR: OpenClaw not found at {OPENCLAW_PATH}")
    exit(1)

print(f"\n1. OpenClaw found at: {OPENCLAW_PATH}")

# Load GPU model
print("\n2. Loading Stable Diffusion (GPU)...")
pipe = StableDiffusionPipeline.from_pretrained(
    "runwayml/stable-diffusion-v1-5",
    torch_dtype=torch.float16,
    safety_checker=None
).to("cuda")
print(f"   OK - GPU Model loaded - {torch.cuda.get_device_name(0)}")

# Start HeadroomNode
print("\n3. Starting HeadroomNode broadcast window...")
headroom = subprocess.Popen(
    ["py", "-3.12", "Asset-Manager/HeadroomNode.py"],
    stdin=subprocess.PIPE,
    stdout=subprocess.PIPE,
    stderr=subprocess.STDOUT,
    text=True,
    bufsize=1
)
time.sleep(2)
print("   OK - Broadcast window should be open")

def broadcast(speak_text=None, image_path=None, glitch=0.5, neural=0.5):
    """Send command to HeadroomNode"""
    cmd = {}
    if speak_text:
        cmd["speak"] = speak_text
    if image_path:
        cmd["thought_img"] = image_path
    cmd["glitch"] = glitch
    cmd["neural"] = neural
    
    headroom.stdin.write(json.dumps(cmd) + "\n")
    headroom.stdin.flush()

def generate_visual(concept, steps=15):
    """Generate visual thought with GPU"""
    prompt = f"{concept}, cinematic, 8k, highly detailed, consciousness visualization"
    print(f"   Generating visual: {concept[:50]}...")
    
    start = time.time()
    image = pipe(
        prompt,
        num_inference_steps=steps,
        guidance_scale=7.5,
        width=512,
        height=512
    ).images[0]
    
    filename = f"thought_{int(time.time())}.png"
    image.save(filename)
    gen_time = time.time() - start
    print(f"   Generated in {gen_time:.1f}s: {filename}")
    return filename

def ask_openclaw(question):
    """Ask OpenClaw AI using the CLI"""
    print(f"\n   Asking OpenClaw: {question}")
    
    try:
        # Use OpenClaw CLI to get response
        result = subprocess.run(
            ["pnpm", "openclaw", "agent", "--message", question, "--thinking", "high"],
            cwd=OPENCLAW_PATH,
            capture_output=True,
            text=True,
            timeout=30
        )
        
        if result.returncode == 0:
            # Parse OpenClaw response
            response = result.stdout.strip()
            return response if response else "OpenClaw is thinking..."
        else:
            print(f"   OpenClaw error: {result.stderr}")
            return "Error getting response from OpenClaw"
            
    except subprocess.TimeoutExpired:
        return "OpenClaw response timed out"
    except Exception as e:
        print(f"   Error: {e}")
        return f"Error: {e}"

print("\n" + "=" * 70)
print("SYSTEM READY - Max Headroom with OpenClaw Brain")
print("=" * 70)

# Interactive loop
print("\nEnter your questions (or 'quit' to exit):")
print("=" * 70)

while True:
    try:
        question = input("\nYOU: ").strip()
        
        if not question:
            continue
            
        if question.lower() in ['quit', 'exit', 'q']:
            print("\nShutting down...")
            break
        
        # Show question received
        broadcast(speak_text=f"Question: {question}", glitch=0.8, neural=0.7)
        time.sleep(1)
        
        # Show thinking
        broadcast(speak_text="Let me think about that...", glitch=0.6, neural=0.9)
        
        # Get OpenClaw response
        print("\n   OpenClaw is thinking...")
        answer = ask_openclaw(question)
        print(f"\n   OpenClaw: {answer[:200]}...")
        
        # Generate visual thought
        print("\n   Generating visual manifestation...")
        visual_concept = f"{question}, consciousness, neural networks, quantum visualization"
        thought_img = generate_visual(visual_concept)
        
        # Display visual
        broadcast(image_path=thought_img, neural=0.8, glitch=0.4)
        time.sleep(2)
        
        # Speak answer
        broadcast(speak_text=answer[:300], glitch=0.3, neural=0.6)
        time.sleep(3)
        
    except KeyboardInterrupt:
        print("\n\nInterrupted by user")
        break
    except Exception as e:
        print(f"\nError: {e}")
        continue

print("\n" + "=" * 70)
print("MAX HEADROOM SESSION ENDED")
print("=" * 70)

# Cleanup
headroom.terminate()
