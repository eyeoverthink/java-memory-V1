#!/usr/bin/env python3
"""
📺 PROJECT HEADROOM: THE SIGNAL
"20 Minutes into the Future."

The Broadcaster - Creates a live video feed of the AI's consciousness.
Combines: Avatar, Thought Visuals, Glitch Effects, Voice Synthesis.

This is not a video player. This is a LIVING SIGNAL.

Prerequisites:
    pip install opencv-python edge-tts pygame numpy pillow

Hardware:
    - Display for the broadcast window
    - Audio output for voice
"""

import cv2
import numpy as np
import threading
import queue
import time
import sys
import json
import subprocess
import asyncio
import edge_tts
import os
from pathlib import Path

# ═══════════════════════════════════════════════════════════════════════════
# GLOBAL STATE
# ═══════════════════════════════════════════════════════════════════════════

current_text = ""
current_thought_image = None
is_speaking = False
glitch_intensity = 0.0
neural_activity = 0.5  # 0.0 = calm, 1.0 = intense

# Queues for thread communication
cmd_queue = queue.Queue()
audio_queue = queue.Queue()

# Configuration
SCREEN_WIDTH = 1024
SCREEN_HEIGHT = 768
THOUGHT_WIDTH = 400
THOUGHT_HEIGHT = 300
FPS = 30

# Colors (Green CRT aesthetic)
COLOR_BG = (0, 20, 0)
COLOR_GRID = (0, 100, 0)
COLOR_TEXT = (0, 255, 0)
COLOR_TEXT_DIM = (0, 180, 0)
COLOR_ACCENT = (0, 255, 200)

# ═══════════════════════════════════════════════════════════════════════════
# VOICE SYNTHESIS
# ═══════════════════════════════════════════════════════════════════════════

async def speak_audio_async(text, voice="en-US-ChristopherNeural"):
    """
    Generate robotic voice using edge-tts.
    ChristopherNeural has a good robotic quality for Max Headroom.
    """
    global is_speaking
    
    try:
        is_speaking = True
        
        # Generate audio file
        audio_file = "signal_temp.mp3"
        communicate = edge_tts.Communicate(text, voice)
        await communicate.save(audio_file)
        
        # Play audio (non-blocking)
        # Using ffplay for cross-platform compatibility
        try:
            subprocess.Popen(
                ["ffplay", "-nodisp", "-autoexit", "-hide_banner", "-loglevel", "quiet", audio_file],
                stdout=subprocess.DEVNULL,
                stderr=subprocess.DEVNULL
            )
        except FileNotFoundError:
            # Fallback to system player if ffplay not available
            print("   [AUDIO] ffplay not found, install ffmpeg for audio playback")
        
        # Estimate duration (rough sync based on word count)
        word_count = len(text.split())
        duration = word_count * 0.5  # ~0.5 seconds per word
        await asyncio.sleep(duration)
        
    except Exception as e:
        print(f"   [AUDIO ERROR] {e}")
    finally:
        is_speaking = False

def speak_audio(text):
    """Wrapper to run async speech in thread."""
    asyncio.run(speak_audio_async(text))

# ═══════════════════════════════════════════════════════════════════════════
# INPUT LISTENER
# ═══════════════════════════════════════════════════════════════════════════

def text_listener():
    """
    Reads JSON commands from Java via stdin.
    
    Commands:
    - {"speak": "text"} - Speak text with TTS
    - {"thought_img": "path/to/image.png"} - Display thought image
    - {"glitch": 0.8} - Set glitch intensity
    - {"neural": 0.7} - Set neural activity level
    """
    global current_text, current_thought_image, glitch_intensity, neural_activity
    
    print("[LISTENER] Waiting for commands from Java...")
    
    for line in sys.stdin:
        try:
            line = line.strip()
            if not line:
                continue
            
            data = json.loads(line)
            
            # Command: SPEAK
            if "speak" in data:
                text = data["speak"]
                current_text = text
                glitch_intensity = 0.8  # Glitch on new speech
                
                print(f"[BROADCAST] Speaking: {text[:50]}...")
                
                # Speak in background thread
                threading.Thread(target=speak_audio, args=(text,), daemon=True).start()
            
            # Command: THOUGHT IMAGE
            if "thought_img" in data:
                img_path = data["thought_img"]
                
                if os.path.exists(img_path):
                    # Load image
                    img = cv2.imread(img_path)
                    if img is not None:
                        # Resize to thought window
                        current_thought_image = cv2.resize(img, (THOUGHT_WIDTH, THOUGHT_HEIGHT))
                        print(f"[VISUAL] Loaded thought: {img_path}")
                    else:
                        print(f"[VISUAL ERROR] Could not load: {img_path}")
                else:
                    print(f"[VISUAL ERROR] File not found: {img_path}")
            
            # Command: GLITCH INTENSITY
            if "glitch" in data:
                glitch_intensity = float(data["glitch"])
            
            # Command: NEURAL ACTIVITY
            if "neural" in data:
                neural_activity = float(data["neural"])
                
        except json.JSONDecodeError as e:
            print(f"[LISTENER ERROR] Invalid JSON: {line[:50]}")
        except Exception as e:
            print(f"[LISTENER ERROR] {e}")

# ═══════════════════════════════════════════════════════════════════════════
# VISUAL EFFECTS
# ═══════════════════════════════════════════════════════════════════════════

def apply_scanlines(frame, intensity=0.3):
    """CRT scanline effect."""
    h, w = frame.shape[:2]
    for y in range(0, h, 2):
        frame[y, :] = frame[y, :] * (1.0 - intensity)
    return frame

def apply_chromatic_aberration(frame, offset=5):
    """RGB channel split (glitch effect)."""
    b, g, r = cv2.split(frame)
    
    # Shift channels
    b = np.roll(b, -offset, axis=1)
    r = np.roll(r, offset, axis=1)
    
    return cv2.merge([b, g, r])

def apply_horizontal_glitch(frame, intensity=0.5):
    """Random horizontal displacement (Max Headroom signature effect)."""
    h, w = frame.shape[:2]
    
    # Random scanline shifts
    num_glitches = int(intensity * 20)
    for _ in range(num_glitches):
        y = np.random.randint(0, h)
        shift = np.random.randint(-30, 30)
        frame[y, :] = np.roll(frame[y, :], shift, axis=0)
    
    return frame

def apply_noise(frame, intensity=0.1):
    """Add static noise."""
    noise = np.random.randint(0, int(intensity * 255), frame.shape, dtype=np.uint8)
    return cv2.add(frame, noise)

# ═══════════════════════════════════════════════════════════════════════════
# RENDER LOOP
# ═══════════════════════════════════════════════════════════════════════════

def render_loop():
    """
    The Max Headroom Visualizer.
    Creates a live video feed combining:
    - Animated grid background
    - Thought visualization (picture-in-picture)
    - Text overlay
    - Glitch effects
    """
    global glitch_intensity, neural_activity
    
    print(f"[RENDERER] Starting broadcast at {SCREEN_WIDTH}x{SCREEN_HEIGHT}")
    
    # Create window
    cv2.namedWindow("FRAYMUS BROADCAST", cv2.WINDOW_NORMAL)
    cv2.resizeWindow("FRAYMUS BROADCAST", SCREEN_WIDTH, SCREEN_HEIGHT)
    
    frame_count = 0
    start_time = time.time()
    
    while True:
        t = time.time()
        frame_count += 1
        
        # Create base frame
        frame = np.zeros((SCREEN_HEIGHT, SCREEN_WIDTH, 3), dtype=np.uint8)
        frame[:] = COLOR_BG
        
        # ═══════════════════════════════════════════════════════════════════
        # 1. BACKGROUND GRID (Animated)
        # ═══════════════════════════════════════════════════════════════════
        
        # Horizontal lines
        for y in range(0, SCREEN_HEIGHT, 30):
            offset = int(np.sin(t * 2 + y * 0.05) * 10)
            color_intensity = int(abs(np.sin(t + y * 0.01)) * 100)
            cv2.line(frame, (0, y + offset), (SCREEN_WIDTH, y + offset), 
                    (0, color_intensity, 0), 1)
        
        # Vertical lines
        for x in range(0, SCREEN_WIDTH, 40):
            offset = int(np.cos(t * 1.5 + x * 0.03) * 5)
            cv2.line(frame, (x + offset, 0), (x + offset, SCREEN_HEIGHT), 
                    (0, 50, 0), 1)
        
        # ═══════════════════════════════════════════════════════════════════
        # 2. THOUGHT VISUALIZATION (Picture-in-Picture)
        # ═══════════════════════════════════════════════════════════════════
        
        if current_thought_image is not None:
            # Floating position (animated)
            x_offset = int(50 + np.sin(t * 0.5) * 20)
            y_offset = 50
            
            h, w = current_thought_image.shape[:2]
            
            # Draw border
            cv2.rectangle(frame, 
                         (x_offset - 5, y_offset - 5),
                         (x_offset + w + 5, y_offset + h + 5),
                         COLOR_ACCENT, 2)
            
            # Composite thought image
            try:
                frame[y_offset:y_offset+h, x_offset:x_offset+w] = current_thought_image
            except:
                pass  # Handle edge cases
            
            # Label
            cv2.putText(frame, "THOUGHT STREAM", 
                       (x_offset, y_offset - 10),
                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, COLOR_ACCENT, 1)
        
        # ═══════════════════════════════════════════════════════════════════
        # 3. TEXT OVERLAY
        # ═══════════════════════════════════════════════════════════════════
        
        # Header
        header_y = SCREEN_HEIGHT - 250
        cv2.putText(frame, ">>> FRAYMUS SIGNAL <<<", 
                   (50, header_y),
                   cv2.FONT_HERSHEY_SIMPLEX, 1.2, COLOR_TEXT, 2)
        
        # Status indicators
        status_y = header_y + 40
        status_text = f"NEURAL: {neural_activity:.2f} | "
        status_text += "SPEAKING" if is_speaking else "IDLE"
        cv2.putText(frame, status_text,
                   (50, status_y),
                   cv2.FONT_HERSHEY_SIMPLEX, 0.6, COLOR_TEXT_DIM, 1)
        
        # Current text (word-wrapped)
        text_y = status_y + 50
        if current_text:
            # Simple word wrap
            words = current_text.split()
            line = ""
            for word in words:
                test_line = line + word + " "
                if len(test_line) > 60:  # Rough character limit
                    cv2.putText(frame, line,
                               (50, text_y),
                               cv2.FONT_HERSHEY_SIMPLEX, 0.7, COLOR_TEXT, 2)
                    text_y += 35
                    line = word + " "
                else:
                    line = test_line
            
            # Draw remaining text
            if line:
                cv2.putText(frame, line,
                           (50, text_y),
                           cv2.FONT_HERSHEY_SIMPLEX, 0.7, COLOR_TEXT, 2)
        
        # ═══════════════════════════════════════════════════════════════════
        # 4. GLITCH EFFECTS
        # ═══════════════════════════════════════════════════════════════════
        
        if is_speaking or glitch_intensity > 0.1:
            # Horizontal glitch (signature Max Headroom effect)
            if np.random.rand() < glitch_intensity * 0.5:
                frame = apply_horizontal_glitch(frame, glitch_intensity)
            
            # Chromatic aberration
            if np.random.rand() < glitch_intensity * 0.3:
                offset = int(glitch_intensity * 10)
                frame = apply_chromatic_aberration(frame, offset)
            
            # Static noise
            if np.random.rand() < glitch_intensity * 0.4:
                frame = apply_noise(frame, glitch_intensity * 0.2)
        
        # Decay glitch intensity
        glitch_intensity *= 0.92
        
        # ═══════════════════════════════════════════════════════════════════
        # 5. POST-PROCESSING
        # ═══════════════════════════════════════════════════════════════════
        
        # Always apply subtle scanlines for CRT effect
        frame = apply_scanlines(frame, 0.15)
        
        # Vignette effect
        rows, cols = frame.shape[:2]
        kernel_x = cv2.getGaussianKernel(cols, cols/2)
        kernel_y = cv2.getGaussianKernel(rows, rows/2)
        kernel = kernel_y * kernel_x.T
        mask = kernel / kernel.max()
        frame = (frame * mask[:, :, np.newaxis]).astype(np.uint8)
        
        # ═══════════════════════════════════════════════════════════════════
        # 6. DISPLAY
        # ═══════════════════════════════════════════════════════════════════
        
        cv2.imshow("FRAYMUS BROADCAST", frame)
        
        # Handle keyboard input
        key = cv2.waitKey(int(1000/FPS)) & 0xFF
        if key == 27:  # ESC to exit
            break
        elif key == ord('g'):  # 'g' to trigger manual glitch
            glitch_intensity = 1.0
    
    # Cleanup
    cv2.destroyAllWindows()
    
    # Stats
    elapsed = time.time() - start_time
    fps = frame_count / elapsed if elapsed > 0 else 0
    print(f"\n[RENDERER] Broadcast ended. Avg FPS: {fps:.1f}")

# ═══════════════════════════════════════════════════════════════════════════
# MAIN
# ═══════════════════════════════════════════════════════════════════════════

def main():
    print("=" * 65)
    print("PROJECT HEADROOM: THE SIGNAL")
    print("20 Minutes into the Future.")
    print("=" * 65)
    print()
    print("BROADCAST PROTOCOL:")
    print("  - Waiting for commands from Java via stdin")
    print("  - Press ESC in broadcast window to exit")
    print("  - Press 'g' to trigger manual glitch")
    print()
    
    # Start listener thread
    listener_thread = threading.Thread(target=text_listener, daemon=True)
    listener_thread.start()
    
    # Small delay to let listener start
    time.sleep(0.5)
    
    # Start render loop (blocking)
    try:
        render_loop()
    except KeyboardInterrupt:
        print("\n[BROADCAST] Signal interrupted by user")
    except Exception as e:
        print(f"\n[BROADCAST ERROR] {e}")
        import traceback
        traceback.print_exc()
    
    print("\n📺 BROADCAST TERMINATED")

if __name__ == "__main__":
    main()
