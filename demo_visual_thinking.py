"""
Visual Thinking Demo - Works without Ollama
Demonstrates the full pipeline: Question -> Visual Generation -> Display
You can replace the simulated responses with real Ollama later
"""
import subprocess
import json
import time
import torch
from diffusers import StableDiffusionPipeline
import os

print("=" * 70)
print("VISUAL THINKING DEMONSTRATION")
print("GPU-Accelerated Visual AI Responses")
print("=" * 70)

# Load GPU model
print("\n1. Loading Stable Diffusion (GPU)...")
pipe = StableDiffusionPipeline.from_pretrained(
    "runwayml/stable-diffusion-v1-5",
    torch_dtype=torch.float16,
    safety_checker=None
).to("cuda")
print(f"   OK - GPU Model loaded - {torch.cuda.get_device_name(0)}")

# Start HeadroomNode
print("\n2. Starting HeadroomNode broadcast window...")
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
    print(f"   Generating: {concept[:50]}...")
    
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

print("\n" + "=" * 70)
print("DEMONSTRATION: Visual Thinking Process")
print("=" * 70)

# Simulated conversation - replace with Ollama when ready
questions_and_responses = [
    {
        "question": "What is consciousness?",
        "thinking": "Analyzing the nature of consciousness and awareness...",
        "visual_concept": "consciousness emerging, neural networks glowing, quantum states, ethereal energy",
        "answer": "Consciousness is the state of being aware of and able to think about one's own existence, sensations, thoughts, and surroundings. It emerges from complex neural processes."
    },
    {
        "question": "How does AI think?",
        "thinking": "Processing information about artificial intelligence cognition...",
        "visual_concept": "AI neural network, data flowing through nodes, digital brain, matrix of connections",
        "answer": "AI processes information through layers of artificial neural networks, transforming input data through mathematical operations to produce outputs and learn patterns."
    }
]

for i, qa in enumerate(questions_and_responses):
    print(f"\n--- Question {i+1} ---")
    print(f"YOU: {qa['question']}")
    
    # Show question received
    broadcast(speak_text=f"Question: {qa['question']}", glitch=0.8, neural=0.7)
    time.sleep(2)
    
    # Show thinking
    print(f"AI: {qa['thinking']}")
    broadcast(speak_text=qa['thinking'], glitch=0.6, neural=0.9)
    time.sleep(1)
    
    # Generate visual thought
    print("   Generating visual manifestation...")
    thought_img = generate_visual(qa['visual_concept'])
    
    # Display visual
    broadcast(image_path=thought_img, neural=0.8, glitch=0.4)
    time.sleep(3)
    
    # Speak answer
    print(f"AI: {qa['answer']}")
    broadcast(speak_text=qa['answer'], glitch=0.3, neural=0.6)
    time.sleep(4)

print("\n" + "=" * 70)
print("DEMONSTRATION COMPLETE")
print("=" * 70)
print("\nYou should have seen:")
print("  [OK] HeadroomNode window with animated glitches")
print("  [OK] Visual thoughts generated in ~2s each")
print("  [OK] Images displayed in broadcast window")
print("  [OK] Text appearing on screen")
print()
print("NEXT STEPS:")
print("  1. Start Ollama: ollama serve")
print("  2. Load your model: ollama run eyeoverthink/Fraymus")
print("  3. Run test_full_integration.py for real LLM responses")
print()
print("Press ESC in broadcast window to close, or Ctrl+C here.")
print("=" * 70)

try:
    headroom.wait()
except KeyboardInterrupt:
    print("\nTerminating...")
    headroom.terminate()
