"""
Max Headroom LIVE - Full Integration
Ollama (running) + GPU Visual Generation + HeadroomNode Display
Real AI thinking with visual manifestation
"""
import subprocess
import json
import time
import torch
from diffusers import StableDiffusionPipeline
import requests

print("=" * 70)
print("MAX HEADROOM LIVE SYSTEM")
print("Ollama + RTX 3070 GPU + HeadroomNode Broadcast")
print("=" * 70)

# Configuration
OLLAMA_URL = "http://localhost:11434/api/generate"
MODEL = "eyeoverthink/Fraymus"

# Check Ollama
print("\n1. Checking Ollama (already running)...")
try:
    response = requests.get("http://localhost:11434/api/tags", timeout=3)
    if response.status_code == 200:
        models = response.json().get("models", [])
        model_names = [m["name"] for m in models]
        print(f"   OK - Ollama running with {len(models)} models")
        print(f"   Available: {', '.join(model_names[:3])}")
        
        # Check if our model is available
        if not any(MODEL in name for name in model_names):
            print(f"   WARNING: {MODEL} not found, using first available model")
            if models:
                MODEL = models[0]["name"]
                print(f"   Using: {MODEL}")
    else:
        print(f"   ERROR: Ollama responded with {response.status_code}")
        exit(1)
except Exception as e:
    print(f"   ERROR: Cannot connect to Ollama: {e}")
    exit(1)

# Load GPU model
print("\n2. Loading Stable Diffusion (GPU)...")
pipe = StableDiffusionPipeline.from_pretrained(
    "runwayml/stable-diffusion-v1-5",
    torch_dtype=torch.float16,
    safety_checker=None
).to("cuda")
print(f"   OK - GPU loaded - {torch.cuda.get_device_name(0)}")

# Start HeadroomNode
print("\n3. Starting HeadroomNode broadcast...")
headroom = subprocess.Popen(
    ["py", "-3.12", "Asset-Manager/HeadroomNode.py"],
    stdin=subprocess.PIPE,
    stdout=subprocess.PIPE,
    stderr=subprocess.STDOUT,
    text=True,
    bufsize=1
)
time.sleep(2)
print("   OK - Broadcast window open")

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

def generate_visual(concept, steps=15):
    prompt = f"{concept}, cinematic, 8k, highly detailed, consciousness visualization"
    print(f"      Generating: {concept[:40]}...")
    start = time.time()
    image = pipe(prompt, num_inference_steps=steps, guidance_scale=7.5, width=512, height=512).images[0]
    filename = f"thought_{int(time.time())}.png"
    image.save(filename)
    print(f"      Done in {time.time() - start:.1f}s")
    return filename

def ask_ollama(question):
    print(f"   Ollama thinking...")
    try:
        response = requests.post(
            OLLAMA_URL,
            json={"model": MODEL, "prompt": question, "stream": False},
            timeout=60
        )
        if response.status_code == 200:
            return response.json()["response"]
        return "Error: No response"
    except Exception as e:
        return f"Error: {e}"

print("\n" + "=" * 70)
print("SYSTEM READY - Ask me anything!")
print("Type 'quit' to exit")
print("=" * 70)

while True:
    try:
        question = input("\nYOU: ").strip()
        
        if not question or question.lower() in ['quit', 'exit', 'q']:
            break
        
        # Broadcast question
        broadcast(speak_text=f"Question: {question}", glitch=0.8, neural=0.7)
        time.sleep(1)
        
        # Show thinking
        broadcast(speak_text="Thinking...", glitch=0.6, neural=0.9)
        
        # Get AI response
        answer = ask_ollama(question)
        print(f"\nAI: {answer[:200]}...")
        
        # Generate visual
        print("\n   Creating visual thought...")
        visual = f"{question}, neural networks, consciousness, quantum visualization"
        img = generate_visual(visual)
        
        # Display
        broadcast(image_path=img, neural=0.8, glitch=0.4)
        time.sleep(2)
        
        # Speak answer
        broadcast(speak_text=answer[:300], glitch=0.3, neural=0.6)
        time.sleep(3)
        
    except KeyboardInterrupt:
        break
    except Exception as e:
        print(f"Error: {e}")

print("\nShutting down...")
headroom.terminate()
