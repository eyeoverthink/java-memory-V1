"""
Test Video Generation with GPU
Generates a short video sequence by creating multiple frames and stitching them together
"""
import torch
from diffusers import StableDiffusionPipeline
import time
from PIL import Image
import os

print("=" * 70)
print("VIDEO GENERATION TEST - GPU Accelerated")
print("=" * 70)

# Check GPU
print(f"\nGPU: {torch.cuda.get_device_name(0)}")
print(f"CUDA Available: {torch.cuda.is_available()}")

# Load model
print("\nLoading Stable Diffusion...")
start = time.time()
pipe = StableDiffusionPipeline.from_pretrained(
    "runwayml/stable-diffusion-v1-5",
    torch_dtype=torch.float16,
    safety_checker=None
).to("cuda")
print(f"Model loaded in {time.time() - start:.1f}s")

# Video parameters
num_frames = 5  # Start small for testing
width = 512
height = 512
steps = 15  # Lower steps for faster generation

# Base prompt with variations
base_prompt = "Max Headroom digital consciousness"
variations = [
    "awakening in the void, dark background",
    "emerging with glowing energy, purple and gold",
    "fully manifested, bright spiraling patterns",
    "radiating consciousness waves, intense colors",
    "transcendent state, golden ratio spiral"
]

print(f"\nGenerating {num_frames} frames for video...")
print(f"Resolution: {width}x{height}")
print(f"Steps per frame: {steps}")
print()

frames = []
total_start = time.time()

for i, variation in enumerate(variations[:num_frames]):
    prompt = f"{base_prompt} {variation}, cinematic, 8k, highly detailed"
    print(f"Frame {i+1}/{num_frames}: {variation[:40]}...")
    
    frame_start = time.time()
    image = pipe(
        prompt,
        num_inference_steps=steps,
        guidance_scale=7.5,
        width=width,
        height=height
    ).images[0]
    
    frame_time = time.time() - frame_start
    print(f"  Generated in {frame_time:.1f}s")
    
    # Save individual frame
    frame_path = f"video_frame_{i+1:03d}.png"
    image.save(frame_path)
    frames.append(image)

total_time = time.time() - total_start

print()
print("=" * 70)
print("VIDEO GENERATION COMPLETE")
print("=" * 70)
print(f"Total frames: {num_frames}")
print(f"Total time: {total_time:.1f}s")
print(f"Average per frame: {total_time/num_frames:.1f}s")
print(f"Frames saved: video_frame_001.png to video_frame_{num_frames:03d}.png")
print()
print("Next step: Use ffmpeg to create video:")
print(f'  ffmpeg -framerate 2 -i video_frame_%03d.png -c:v libx264 -pix_fmt yuv420p max_headroom_video.mp4')
print("=" * 70)
