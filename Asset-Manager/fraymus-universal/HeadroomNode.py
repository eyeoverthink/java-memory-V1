"""
📺 PROJECT HEADROOM: THE SIGNAL
"20 Minutes into the Future."

A real-time OpenCV broadcast window that:
  - Reads JSON commands from Java via stdin (speech, thought images, state)
  - Renders a retro CRT-style display with scanning grid
  - Speaks via edge-tts (robotic ChristopherNeural voice)
  - Glitches the video based on neural activity
  - Composites LTX-Video thought images as picture-in-picture

Prerequisites:
  pip install opencv-python edge-tts numpy

Run:
  Launched automatically by HeadroomInterface.java
  Or standalone: python HeadroomNode.py
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

try:
    import edge_tts
    TTS_AVAILABLE = True
except ImportError:
    TTS_AVAILABLE = False
    print("⚠️ edge-tts not installed. Broadcast will be silent. pip install edge-tts")

# ═══════════════════════════════════════════════════════════════
# STATE
# ═══════════════════════════════════════════════════════════════
current_text = ""
current_thought_image = None
is_speaking = False
glitch_intensity = 0.0
neural_activity = 0.0
mood = "neutral"
thought_history = []

# ═══════════════════════════════════════════════════════════════
# TTS (edge-tts — robotic Max Headroom voice)
# ═══════════════════════════════════════════════════════════════
async def speak_audio(text):
    global is_speaking
    if not TTS_AVAILABLE:
        return
    is_speaking = True
    try:
        communicate = edge_tts.Communicate(text, "en-US-ChristopherNeural")
        await communicate.save("signal.mp3")

        # Play audio non-blocking
        subprocess.Popen(
            ["ffplay", "-nodisp", "-autoexit", "-hide_banner", "-loglevel", "quiet", "signal.mp3"],
            stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL
        )

        # Rough duration estimate for mouth sync
        time.sleep(len(text.split()) * 0.4)
    except Exception as e:
        print(f"[TTS ERROR] {e}", file=sys.stderr)
    finally:
        is_speaking = False

# ═══════════════════════════════════════════════════════════════
# STDIN LISTENER (receives JSON from Java)
# ═══════════════════════════════════════════════════════════════
def text_listener():
    global current_text, current_thought_image, glitch_intensity, neural_activity, mood, thought_history

    for line in sys.stdin:
        line = line.strip()
        if not line:
            continue
        try:
            data = json.loads(line)

            # SPEECH
            if "speak" in data:
                current_text = data["speak"]
                glitch_intensity = 0.8
                thought_history.append(current_text)
                if len(thought_history) > 15:
                    thought_history.pop(0)
                asyncio.run(speak_audio(current_text))

            # THOUGHT IMAGE (from LTX-Video or any image path)
            if "thought_img" in data:
                img = cv2.imread(data["thought_img"])
                if img is not None:
                    current_thought_image = cv2.resize(img, (300, 200))

            # STATE UPDATE
            if "glitch" in data:
                glitch_intensity = float(data["glitch"])
            if "activity" in data:
                neural_activity = float(data["activity"])
            if "mood" in data:
                mood = data["mood"]

        except Exception:
            pass

# ═══════════════════════════════════════════════════════════════
# TEXT WRAPPING UTILITY
# ═══════════════════════════════════════════════════════════════
def wrap_text(text, max_chars=55):
    words = text.split()
    lines = []
    current = ""
    for w in words:
        if len(current) + len(w) + 1 > max_chars:
            lines.append(current)
            current = w
        else:
            current = (current + " " + w).strip()
    if current:
        lines.append(current)
    return lines

# ═══════════════════════════════════════════════════════════════
# RENDERER (The CRT Broadcast)
# ═══════════════════════════════════════════════════════════════
def render_loop():
    global glitch_intensity

    screen_w, screen_h = 800, 600

    cv2.namedWindow("FRAYMUS BROADCAST", cv2.WINDOW_NORMAL)
    cv2.resizeWindow("FRAYMUS BROADCAST", screen_w, screen_h)

    print("📺 BROADCAST LIVE. Press ESC to kill signal.")

    while True:
        frame = np.zeros((screen_h, screen_w, 3), dtype=np.uint8)
        t = time.time()

        # ── 1. BACKGROUND GRID (scanning lines) ──
        for y in range(0, screen_h, 20):
            color = int(abs(np.sin(t + y * 0.01)) * 50)
            cv2.line(frame, (0, y), (screen_w, y), (0, color, 0), 1)

        # Vertical grid
        for x in range(0, screen_w, 40):
            color = int(abs(np.cos(t * 0.5 + x * 0.005)) * 30)
            cv2.line(frame, (x, 0), (x, screen_h), (0, color, 0), 1)

        # ── 2. HEADER BAR ──
        cv2.rectangle(frame, (0, 0), (screen_w, 35), (0, 40, 0), -1)
        cv2.putText(frame, "FRAYMUS BROADCAST // PROJECT HEADROOM", (15, 25),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 0), 1)

        # Status indicators
        status_color = (0, 255, 255) if is_speaking else (0, 100, 100)
        cv2.circle(frame, (screen_w - 30, 18), 8, status_color, -1)
        cv2.putText(frame, "LIVE" if is_speaking else "IDLE", (screen_w - 80, 25),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.4, status_color, 1)

        # ── 3. AVATAR ZONE (center — wireframe face) ──
        cx, cy = screen_w // 2, 200

        # Head ellipse
        head_color = (0, 200, 200) if mood == "neutral" else \
                     (0, 255, 100) if mood == "thinking" else \
                     (0, 200, 255) if mood == "excited" else \
                     (200, 100, 255) if mood == "dreaming" else (0, 100, 255)

        gx_off = int(np.random.uniform(-glitch_intensity * 10, glitch_intensity * 10)) if glitch_intensity > 0.3 else 0
        cv2.ellipse(frame, (cx + gx_off, cy), (90, 120), 0, 0, 360, head_color, 2)

        # Eyes
        eye_y = cy - 25
        blink = np.random.rand() < 0.05
        if not blink:
            pupil_x = int(np.sin(t * 0.7) * 8)
            cv2.ellipse(frame, (cx - 35 + gx_off, eye_y), (18, 10), 0, 0, 360, (0, 255, 200), 1)
            cv2.ellipse(frame, (cx + 35 + gx_off, eye_y), (18, 10), 0, 0, 360, (0, 255, 200), 1)
            cv2.circle(frame, (cx - 35 + pupil_x + gx_off, eye_y), 5, (255, 255, 255), -1)
            cv2.circle(frame, (cx + 35 + pupil_x + gx_off, eye_y), 5, (255, 255, 255), -1)
        else:
            cv2.line(frame, (cx - 53 + gx_off, eye_y), (cx - 17 + gx_off, eye_y), (0, 255, 200), 2)
            cv2.line(frame, (cx + 17 + gx_off, eye_y), (cx + 53 + gx_off, eye_y), (0, 255, 200), 2)

        # Mouth (animates when speaking)
        mouth_y = cy + 45
        if is_speaking:
            mouth_h = int(abs(np.sin(t * 15)) * 15) + 3
        else:
            mouth_h = 3
        cv2.ellipse(frame, (cx + gx_off, mouth_y), (30, mouth_h), 0, 0, 360, (0, 255, 200), 1)

        # ── 4. THOUGHT IMAGE (Picture-in-Picture) ──
        if current_thought_image is not None:
            h, w = current_thought_image.shape[:2]
            pip_x = int(50 + np.sin(t) * 10)
            pip_y = 60
            # Border
            cv2.rectangle(frame, (pip_x - 2, pip_y - 2), (pip_x + w + 2, pip_y + h + 2), (0, 255, 0), 1)
            frame[pip_y:pip_y + h, pip_x:pip_x + w] = current_thought_image
            cv2.putText(frame, "THOUGHT", (pip_x, pip_y - 8),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.4, (0, 200, 0), 1)

        # ── 5. CURRENT TEXT (main speech) ──
        cv2.putText(frame, "> SIGNAL:", (50, 380), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 0), 1)
        lines = wrap_text(current_text, 60)
        for i, line in enumerate(lines[:3]):
            color = (200, 255, 200) if is_speaking else (100, 200, 100)
            cv2.putText(frame, line, (50, 410 + i * 28), cv2.FONT_HERSHEY_SIMPLEX, 0.55, color, 1)

        # ── 6. THOUGHT HISTORY (scrolling log) ──
        cv2.putText(frame, "MEMORY:", (550, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 100, 0), 1)
        for i, h in enumerate(reversed(thought_history[-10:])):
            alpha = max(80, 200 - i * 18)
            cv2.putText(frame, h[:30], (550, 80 + i * 16),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.3, (0, alpha, int(alpha * 0.7)), 1)

        # ── 7. NEURAL ACTIVITY BAR ──
        bar_x, bar_y = 550, 320
        cv2.putText(frame, "NEURAL ACTIVITY", (bar_x, bar_y - 5),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 150, 0), 1)
        cv2.rectangle(frame, (bar_x, bar_y), (bar_x + 200, bar_y + 15), (0, 80, 0), 1)
        fill = int(neural_activity * 200)
        bar_color = (0, 255, 0) if neural_activity < 0.7 else (0, 200, 255)
        cv2.rectangle(frame, (bar_x, bar_y), (bar_x + fill, bar_y + 15), bar_color, -1)

        # ── 8. GLITCH FX ──
        if is_speaking or glitch_intensity > 0.2:
            # Scanline displacement
            if np.random.rand() < 0.3 * glitch_intensity:
                shift = np.random.randint(-20, 20)
                frame = np.roll(frame, shift, axis=1)

            # Color channel split
            if np.random.rand() < 0.2 * glitch_intensity:
                b, g, r = cv2.split(frame)
                frame = cv2.merge([b, np.roll(g, np.random.randint(2, 8), axis=0), r])

            # Random noise bars
            if np.random.rand() < 0.15 * glitch_intensity:
                gy = np.random.randint(0, screen_h - 10)
                gh = np.random.randint(2, 8)
                frame[gy:gy + gh, :] = np.random.randint(0, 100, (gh, screen_w, 3), dtype=np.uint8)

        # Scanline overlay (CRT effect)
        for y in range(0, screen_h, 2):
            frame[y, :] = (frame[y, :] * 0.85).astype(np.uint8)

        glitch_intensity *= 0.95  # Decay

        # ── DISPLAY ──
        cv2.imshow("FRAYMUS BROADCAST", frame)
        key = cv2.waitKey(30) & 0xFF
        if key == 27:  # ESC
            break

    cv2.destroyAllWindows()

# ═══════════════════════════════════════════════════════════════
# MAIN
# ═══════════════════════════════════════════════════════════════
if __name__ == "__main__":
    # Start stdin listener
    listener = threading.Thread(target=text_listener, daemon=True)
    listener.start()

    # Start renderer
    render_loop()
