"""
Create MP4 video from generated frames using imageio
"""
import imageio
import os
from pathlib import Path

print("=" * 70)
print("CREATING VIDEO FROM FRAMES")
print("=" * 70)

# Find all frame files
frame_files = sorted([f for f in os.listdir('.') if f.startswith('video_frame_') and f.endswith('.png')])

if not frame_files:
    print("ERROR: No video frames found!")
    print("Expected files like: video_frame_001.png, video_frame_002.png, etc.")
    exit(1)

print(f"\nFound {len(frame_files)} frames:")
for f in frame_files:
    print(f"  - {f}")

# Load frames
print("\nLoading frames...")
frames = []
for frame_file in frame_files:
    frame = imageio.imread(frame_file)
    frames.append(frame)
    print(f"  Loaded {frame_file} ({frame.shape[1]}x{frame.shape[0]})")

# Create video
output_file = "max_headroom_awakening.mp4"
fps = 2  # 2 frames per second for smooth viewing

print(f"\nCreating video: {output_file}")
print(f"  FPS: {fps}")
print(f"  Duration: {len(frames)/fps:.1f} seconds")

imageio.mimsave(output_file, frames, fps=fps, codec='libx264', quality=8)

print(f"\n{'=' * 70}")
print(f"VIDEO CREATED SUCCESSFULLY!")
print(f"{'=' * 70}")
print(f"File: {output_file}")
print(f"Frames: {len(frames)}")
print(f"Duration: {len(frames)/fps:.1f}s")
print(f"{'=' * 70}")
print(f"\nOpening video...")

# Open the video
import subprocess
subprocess.run(['start', output_file], shell=True)
