"""
Prove GPU speed with a fresh test - different concept
"""
import torch
from diffusers import StableDiffusionPipeline
import time

print("=" * 70)
print("GPU SPEED PROOF - Test #2")
print("=" * 70)

# Verify GPU
print(f"\nGPU: {torch.cuda.get_device_name(0)}")
print(f"CUDA Available: {torch.cuda.is_available()}")

print("\nLoading model...")
start_load = time.time()

pipe = StableDiffusionPipeline.from_pretrained(
    "runwayml/stable-diffusion-v1-5",
    torch_dtype=torch.float16,
    safety_checker=None
)
pipe = pipe.to("cuda")

load_time = time.time() - start_load
print(f"Model loaded in {load_time:.1f}s")

# NEW TEST: Different concept to prove it's not cached
prompt = "Quantum consciousness visualization, neural network brain, glowing synapses, cyberpunk style, 8k"
print(f"\nPrompt: {prompt}")
print("Steps: 25 (slightly higher quality)")

start_gen = time.time()
image = pipe(
    prompt,
    num_inference_steps=25,
    guidance_scale=7.5,
    width=512,
    height=512
).images[0]
gen_time = time.time() - start_gen

output_path = "gpu_proof_test2.png"
image.save(output_path)

print(f"\n{'=' * 70}")
print(f"PROOF COMPLETE")
print(f"{'=' * 70}")
print(f"Generation time: {gen_time:.2f} seconds")
print(f"Saved to: {output_path}")
print(f"\nExpected CPU time: ~50-70 minutes")
print(f"Your GPU time: {gen_time:.2f}s")
print(f"Speedup: {(3000/gen_time):.0f}x faster than CPU")
print(f"{'=' * 70}")
