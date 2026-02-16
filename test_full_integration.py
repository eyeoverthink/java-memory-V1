"""
Full Max Headroom Integration Test
Connects: Ollama LLM → Visual Generation → HeadroomNode Display
Real-time AI thinking with visual manifestation
"""
import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

import subprocess
import json
import time
import requests
import torch
from diffusers import StableDiffusionPipeline
import os

print("=" * 70)
print("MAX HEADROOM FULL INTEGRATION TEST")
print("Ollama LLM + GPU Visual Generation + Live Broadcast")
print("=" * 70)

# Configuration
OLLAMA_URL = "http://localhost:11434/api/generate"
MODEL = "eyeoverthink/Fraymus:latest"

# Check Ollama is running
print("\n1. Checking Ollama connection...")
try:
    response = requests.post(OLLAMA_URL, json={"model": MODEL, "prompt": "test", "stream": True}, stream=True, timeout=5)
    if response.status_code == 200:
        print(f"   OK - Ollama connected - Model: {MODEL}")
    else:
        print(f"   ERROR - Ollama error: {response.status_code}")
        print("   Make sure Ollama is running: ollama serve")
        exit(1)
except Exception as e:
    print(f"   ERROR - Cannot connect to Ollama: {e}")
    print("   Make sure Ollama is running: ollama serve")
    exit(1)

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

# Helper function to send commands to HeadroomNode
def broadcast(speak_text=None, image_path=None, glitch=0.5, neural=0.5):
    cmd = {}
    if speak_text:
        cmd["speak"] = speak_text
    if image_path:
        cmd["thought_img"] = image_path
    cmd["glitch"] = glitch
    cmd["neural"] = neural
    
    headroom.stdin.write(json.dumps(cmd) + "\n")
    headroom.stdin.flush()

# Helper function to generate visual thought
def generate_visual_thought(concept, entropy=0.5):
    prompt = f"{concept}, cinematic, 8k, highly detailed, consciousness visualization"
    print(f"      Generating visual: {concept[:50]}...")
    
    start = time.time()
    image = pipe(
        prompt,
        num_inference_steps=15,  # Fast for real-time
        guidance_scale=7.5,
        width=512,
        height=512
    ).images[0]
    
    # Save
    filename = f"thought_{int(time.time())}.png"
    image.save(filename)
    gen_time = time.time() - start
    print(f"      Generated in {gen_time:.1f}s: {filename}")
    return filename

# Helper function to ask Ollama
def ask_ollama(question):
    print(f"\n   Asking: {question}")
    
    response = requests.post(
        OLLAMA_URL,
        json={
            "model": MODEL,
            "prompt": question,
            "stream": True
        },
        stream=True,
        timeout=60
    )
    
    if response.status_code == 200:
        full_response = ""
        for line in response.iter_lines():
            if line:
                try:
                    data = json.loads(line)
                    if "response" in data:
                        full_response += data["response"]
                except json.JSONDecodeError:
                    pass
        return full_response
    else:
        return "Error: Could not get response"

print("\n" + "=" * 70)
print("SYSTEM READY - Starting Visual Conversation")
print("=" * 70)

# Test conversation
question = "What is consciousness?"

print(f"\nYOU: {question}")

# Send to broadcast
broadcast(speak_text=f"Question received: {question}", glitch=0.8, neural=0.7)
time.sleep(2)

# Get LLM response
print("\n   AI is thinking...")
broadcast(speak_text="Let me think about that...", glitch=0.6, neural=0.9)

answer = ask_ollama(question)
print(f"\n   AI Response: {answer[:200]}...")

# Generate visual thought based on answer
print("\n   Generating visual manifestation...")
visual_concept = "consciousness emerging, neural networks, quantum states, glowing energy"
thought_image = generate_visual_thought(visual_concept, entropy=0.6)

# Display visual thought
broadcast(image_path=thought_image, neural=0.8, glitch=0.4)
time.sleep(2)

# Speak answer
broadcast(speak_text=answer[:200], glitch=0.3, neural=0.6)

print("\n" + "=" * 70)
print("FULL INTEGRATION TEST COMPLETE")
print("=" * 70)
print("\nYou should see:")
print("  [OK] HeadroomNode window with animated glitches")
print("  [OK] Visual thought image displayed")
print("  [OK] AI response spoken (if audio working)")
print("  [OK] Real-time LLM thinking -> Visual generation -> Display")
print()
print("Press ESC in broadcast window to close, or Ctrl+C here.")
print("=" * 70)

# Keep alive
try:
    headroom.wait()
except KeyboardInterrupt:
    print("\nTerminating...")
    headroom.terminate()
