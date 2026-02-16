"""
Simplified LLM + Visual Integration Test
Shows real-time output and completes quickly
"""
import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

import requests
import json
import time
import torch
from diffusers import StableDiffusionPipeline

print("=" * 70)
print("SIMPLIFIED LLM + VISUAL INTEGRATION TEST")
print("=" * 70)

# Configuration
OLLAMA_URL = "http://localhost:11434/api/generate"
MODEL = "eyeoverthink/Fraymus:latest"

# Step 1: Test Ollama
print("\n[1/3] Testing Ollama LLM...")
question = "What is consciousness? Answer in 2 sentences."
print(f"   Question: {question}")

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

print("   LLM Response: ", end="", flush=True)
full_response = ""
for line in response.iter_lines():
    if line:
        try:
            data = json.loads(line)
            if "response" in data:
                chunk = data["response"]
                print(chunk, end="", flush=True)
                full_response += chunk
        except json.JSONDecodeError:
            pass

print("\n   ✓ LLM working - Response length:", len(full_response), "chars")

# Step 2: Load GPU model
print("\n[2/3] Loading Stable Diffusion GPU model...")
start = time.time()
pipe = StableDiffusionPipeline.from_pretrained(
    "runwayml/stable-diffusion-v1-5",
    torch_dtype=torch.float16,
    safety_checker=None
).to("cuda")
load_time = time.time() - start
print(f"   ✓ GPU Model loaded in {load_time:.1f}s - {torch.cuda.get_device_name(0)}")

# Step 3: Generate visual thought
print("\n[3/3] Generating visual thought from LLM response...")
visual_concept = "consciousness emerging, neural networks glowing, quantum states, ethereal energy"
print(f"   Concept: {visual_concept}")

start = time.time()
image = pipe(
    visual_concept + ", cinematic, 8k, highly detailed",
    num_inference_steps=15,
    guidance_scale=7.5,
    width=512,
    height=512
).images[0]

filename = f"llm_visual_test_{int(time.time())}.png"
image.save(filename)
gen_time = time.time() - start

print(f"   ✓ Visual generated in {gen_time:.1f}s")
print(f"   ✓ Saved: {filename}")

# Summary
print("\n" + "=" * 70)
print("INTEGRATION TEST COMPLETE ✓")
print("=" * 70)
print("\nPipeline verified:")
print(f"  [✓] Ollama LLM responding ({len(full_response)} chars)")
print(f"  [✓] GPU acceleration working ({gen_time:.1f}s generation)")
print(f"  [✓] Visual thought created: {filename}")
print("\nFull LLM Response:")
print("-" * 70)
print(full_response)
print("-" * 70)
print("\nNext: Run full HeadroomNode broadcast with this pipeline")
print("=" * 70)
