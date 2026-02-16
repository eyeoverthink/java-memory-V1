"""
Minimal test: Generate ONE image to verify GPU works
Run this AFTER installing GPU-enabled PyTorch
"""
import torch
from diffusers import StableDiffusionPipeline
import time

print("=" * 60)
print("SINGLE IMAGE GPU TEST")
print("=" * 60)

# Check GPU
print(f"\nGPU Available: {torch.cuda.is_available()}")
if torch.cuda.is_available():
    print(f"GPU Device: {torch.cuda.get_device_name(0)}")
    device = "cuda"
else:
    print("WARNING: No GPU detected - will be SLOW")
    device = "cpu"

print(f"\nUsing device: {device}")
print("\nLoading Stable Diffusion model...")
start = time.time()

pipe = StableDiffusionPipeline.from_pretrained(
    "runwayml/stable-diffusion-v1-5",
    torch_dtype=torch.float16 if device == "cuda" else torch.float32,
    safety_checker=None
)
pipe = pipe.to(device)

load_time = time.time() - start
print(f"Model loaded in {load_time:.1f}s")

# Generate single test image
prompt = "Golden ratio spiral, fibonacci sequence, sacred geometry, 8k"
print(f"\nGenerating image with prompt: {prompt}")
print("Steps: 20 (fast test)")

start = time.time()
image = pipe(
    prompt,
    num_inference_steps=20,
    guidance_scale=7.5,
    width=512,
    height=512
).images[0]
gen_time = time.time() - start

output_path = "test_output.png"
image.save(output_path)

print(f"\nImage generated in {gen_time:.1f}s")
print(f"Saved to: {output_path}")
print("\n" + "=" * 60)
print("SUCCESS - GPU is working!")
print("=" * 60)
print(f"\nExpected times:")
print(f"  GPU: ~3-10 seconds per image")
print(f"  CPU: ~40-60 minutes per image")
print(f"\nYour time: {gen_time:.1f}s")
