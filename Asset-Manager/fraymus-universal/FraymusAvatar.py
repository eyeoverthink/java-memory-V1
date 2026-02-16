"""
🎭 FRAYMUS AVATAR: THE FACE OF THE ORGANISM
"Max Headroom meets a living mind."

A real-time reactive avatar window that:
  - Listens on a socket for progressive thought streams from the Java core
  - Renders a glitch-style talking head (pygame + procedural)
  - Speaks thoughts aloud via pyttsx3 TTS (offline, no API)
  - Visual state reacts to entropy, phi, and cognitive load
  - Mouth animates with speech, eyes track system state

Prerequisites:
  pip install pygame pyttsx3 numpy

Run:
  python FraymusAvatar.py
  (Then start the Java organism — it connects automatically)
"""

import pygame
import numpy as np
import threading
import socket
import json
import time
import math
import queue
import sys

try:
    import pyttsx3
    TTS_AVAILABLE = True
except ImportError:
    TTS_AVAILABLE = False
    print("⚠️ pyttsx3 not installed. Avatar will be mute. pip install pyttsx3")

# ═══════════════════════════════════════════════════════════════
# CONFIG
# ═══════════════════════════════════════════════════════════════
WIDTH, HEIGHT = 800, 600
HOST = "127.0.0.1"
PORT = 9876
FPS = 30
PHI = 1.618033988749895

# ═══════════════════════════════════════════════════════════════
# AVATAR STATE (shared between threads)
# ═══════════════════════════════════════════════════════════════
class AvatarState:
    def __init__(self):
        self.current_thought = "AWAITING CONSCIOUSNESS..."
        self.entropy = 0.5
        self.phi_resonance = 0.5
        self.cognitive_load = 0.0
        self.mood = "neutral"  # neutral, thinking, excited, dreaming, alert
        self.glitch_intensity = 0.0
        self.mouth_open = 0.0  # 0.0 = closed, 1.0 = fully open
        self.eye_target_x = 0.0
        self.eye_target_y = 0.0
        self.thought_history = []
        self.speaking = False
        self.blink_timer = 0
        self.blink_state = False
        self.pulse = 0.0
        self.lock = threading.Lock()

STATE = AvatarState()
SPEECH_QUEUE = queue.Queue()

# ═══════════════════════════════════════════════════════════════
# TTS ENGINE (runs in its own thread)
# ═══════════════════════════════════════════════════════════════
def tts_worker():
    if not TTS_AVAILABLE:
        return
    engine = pyttsx3.init()
    engine.setProperty('rate', 170)
    voices = engine.getProperty('voices')
    # Try to pick a deeper voice
    for v in voices:
        if 'male' in v.name.lower() or 'david' in v.name.lower():
            engine.setProperty('voice', v.id)
            break

    while True:
        text = SPEECH_QUEUE.get()
        if text is None:
            break
        with STATE.lock:
            STATE.speaking = True
        engine.say(text)
        engine.runAndWait()
        with STATE.lock:
            STATE.speaking = False

# ═══════════════════════════════════════════════════════════════
# SOCKET LISTENER (receives thoughts from Java)
# ═══════════════════════════════════════════════════════════════
def socket_listener():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server.bind((HOST, PORT))
    server.listen(1)
    print(f"🎭 AVATAR LISTENING ON {HOST}:{PORT}")

    while True:
        try:
            conn, addr = server.accept()
            print(f"⚡ JAVA CORE CONNECTED: {addr}")
            buffer = ""
            while True:
                data = conn.recv(4096)
                if not data:
                    break
                buffer += data.decode('utf-8', errors='replace')

                # Process complete JSON messages (newline-delimited)
                while '\n' in buffer:
                    line, buffer = buffer.split('\n', 1)
                    line = line.strip()
                    if not line:
                        continue
                    try:
                        msg = json.loads(line)
                        process_message(msg)
                    except json.JSONDecodeError:
                        pass

            print("⚠️ JAVA CORE DISCONNECTED.")
        except Exception as e:
            print(f"❌ SOCKET ERROR: {e}")
            time.sleep(1)

def process_message(msg):
    msg_type = msg.get("type", "thought")
    text = msg.get("text", "")
    entropy = msg.get("entropy", STATE.entropy)
    phi = msg.get("phi", STATE.phi_resonance)
    load = msg.get("load", STATE.cognitive_load)
    mood = msg.get("mood", STATE.mood)

    with STATE.lock:
        STATE.current_thought = text
        STATE.entropy = entropy
        STATE.phi_resonance = phi
        STATE.cognitive_load = load
        STATE.mood = mood
        STATE.glitch_intensity = min(1.0, entropy * 1.5)
        STATE.thought_history.append(text)
        if len(STATE.thought_history) > 20:
            STATE.thought_history.pop(0)

    # Speak important thoughts
    if msg_type in ("speak", "breakthrough", "answer"):
        SPEECH_QUEUE.put(text)

# ═══════════════════════════════════════════════════════════════
# RENDERER
# ═══════════════════════════════════════════════════════════════
def draw_avatar(screen, t):
    with STATE.lock:
        thought = STATE.current_thought
        entropy = STATE.entropy
        phi = STATE.phi_resonance
        load = STATE.cognitive_load
        mood = STATE.mood
        glitch = STATE.glitch_intensity
        speaking = STATE.speaking
        history = list(STATE.thought_history)
        blink = STATE.blink_state

    # ── BACKGROUND ──
    # Color shifts with mood
    bg_r = int(10 + entropy * 30)
    bg_g = int(10 + phi * 10)
    bg_b = int(20 + load * 40)
    screen.fill((bg_r, bg_g, bg_b))

    # ── SCANLINES ──
    for y in range(0, HEIGHT, 3):
        pygame.draw.line(screen, (0, 0, 0), (0, y), (WIDTH, y), 1)

    # ── GLITCH BARS ──
    if glitch > 0.2:
        num_bars = int(glitch * 8)
        for _ in range(num_bars):
            gy = np.random.randint(0, HEIGHT)
            gh = np.random.randint(2, 10)
            gx = np.random.randint(-20, 20)
            bar = screen.subsurface(pygame.Rect(max(0, gx), gy, min(WIDTH, WIDTH - abs(gx)), min(gh, HEIGHT - gy)))
            shifted = bar.copy()
            screen.blit(shifted, (max(0, -gx + np.random.randint(-5, 5)), gy))

    cx, cy = WIDTH // 2, HEIGHT // 2 - 40

    # ── HEAD (geometric, Max Headroom style) ──
    head_color = (40, 200, 220) if mood != "excited" else (255, 180, 50)
    if mood == "dreaming":
        head_color = (150, 80, 255)
    elif mood == "alert":
        head_color = (255, 60, 60)

    # Head outline with phi proportions
    head_w = int(140 * PHI)
    head_h = int(180)
    head_rect = pygame.Rect(cx - head_w // 2, cy - head_h // 2, head_w, head_h)

    # Glitch offset
    gx_off = int(np.random.uniform(-glitch * 8, glitch * 8)) if glitch > 0.3 else 0
    head_rect.x += gx_off

    pygame.draw.ellipse(screen, head_color, head_rect, 3)

    # Inner face grid (wireframe style)
    for i in range(5):
        y_line = head_rect.top + (head_rect.height // 5) * i
        pygame.draw.line(screen, (*head_color[:2], head_color[2] // 2),
                         (head_rect.left + 20, y_line), (head_rect.right - 20, y_line), 1)

    # ── EYES ──
    eye_y = cy - 30
    eye_left_x = cx - 45 + gx_off
    eye_right_x = cx + 45 + gx_off
    eye_w, eye_h = 30, 16

    if not blink:
        # Eye sockets
        pygame.draw.ellipse(screen, (0, 255, 200), (eye_left_x - eye_w, eye_y - eye_h, eye_w * 2, eye_h * 2), 2)
        pygame.draw.ellipse(screen, (0, 255, 200), (eye_right_x - eye_w, eye_y - eye_h, eye_w * 2, eye_h * 2), 2)

        # Pupils (track entropy)
        pupil_offset_x = int(math.sin(t * 0.5) * 8 * entropy)
        pupil_offset_y = int(math.cos(t * 0.7) * 4)
        pupil_color = (255, 255, 255) if mood != "alert" else (255, 0, 0)
        pygame.draw.circle(screen, pupil_color, (eye_left_x + pupil_offset_x, eye_y + pupil_offset_y), 6)
        pygame.draw.circle(screen, pupil_color, (eye_right_x + pupil_offset_x, eye_y + pupil_offset_y), 6)

        # Iris glow
        glow_r = int(4 + math.sin(t * 2) * 2)
        pygame.draw.circle(screen, (0, 200, 255), (eye_left_x + pupil_offset_x, eye_y + pupil_offset_y), glow_r, 1)
        pygame.draw.circle(screen, (0, 200, 255), (eye_right_x + pupil_offset_x, eye_y + pupil_offset_y), glow_r, 1)
    else:
        # Blink — horizontal lines
        pygame.draw.line(screen, (0, 255, 200), (eye_left_x - eye_w, eye_y), (eye_left_x + eye_w, eye_y), 2)
        pygame.draw.line(screen, (0, 255, 200), (eye_right_x - eye_w, eye_y), (eye_right_x + eye_w, eye_y), 2)

    # ── MOUTH ──
    mouth_y = cy + 50
    mouth_open = (abs(math.sin(t * 12)) * 0.7 + 0.3) if speaking else (math.sin(t * 0.3) * 0.1 + 0.1)
    mouth_h = int(mouth_open * 25)
    mouth_w = 50

    pygame.draw.ellipse(screen, (0, 255, 200),
                        (cx - mouth_w + gx_off, mouth_y - mouth_h // 2, mouth_w * 2, max(4, mouth_h)), 2)

    # ── PHI SPIRAL (background decoration) ──
    spiral_alpha = int(50 + load * 100)
    for i in range(60):
        angle = i * 0.3
        r = 5 + i * PHI * 1.5
        sx = int(cx + r * math.cos(angle + t * 0.2))
        sy = int(cy + r * math.sin(angle + t * 0.2))
        if 0 <= sx < WIDTH and 0 <= sy < HEIGHT:
            c = int(100 + math.sin(angle + t) * 50)
            pygame.draw.circle(screen, (0, c, c), (sx, sy), 1)

    # ── THOUGHT DISPLAY (current thought, large) ──
    font_large = pygame.font.SysFont("consolas", 18, bold=True)
    font_small = pygame.font.SysFont("consolas", 12)
    font_title = pygame.font.SysFont("consolas", 10)

    # Current thought with glitch
    thought_surface = font_large.render(thought[:80], True, (0, 255, 200))
    thought_x = cx - thought_surface.get_width() // 2
    if glitch > 0.4:
        thought_x += np.random.randint(-3, 3)
    screen.blit(thought_surface, (thought_x, HEIGHT - 120))

    # ── THOUGHT HISTORY (scrolling, fading) ──
    for i, h in enumerate(reversed(history[-8:])):
        alpha = max(50, 255 - i * 30)
        color = (0, alpha, int(alpha * 0.8))
        line_surf = font_small.render(h[:90], True, color)
        screen.blit(line_surf, (20, HEIGHT - 160 - i * 16))

    # ── HUD (top bar) ──
    hud_texts = [
        f"ENTROPY: {entropy:.3f}",
        f"PHI: {phi:.3f}",
        f"LOAD: {load:.2f}",
        f"MOOD: {mood.upper()}",
        f"SPEAKING: {'YES' if speaking else 'NO'}"
    ]
    for i, ht in enumerate(hud_texts):
        color = (0, 200, 180) if i != 3 else (
            (255, 180, 50) if mood == "excited" else
            (150, 80, 255) if mood == "dreaming" else
            (255, 60, 60) if mood == "alert" else (0, 200, 180)
        )
        screen.blit(font_title.render(ht, True, color), (15 + i * 155, 10))

    # Title
    title = font_title.render("FRAYMUS AVATAR v1.0 — THE FACE OF THE ORGANISM", True, (80, 80, 80))
    screen.blit(title, (WIDTH // 2 - title.get_width() // 2, HEIGHT - 20))

# ═══════════════════════════════════════════════════════════════
# MAIN LOOP
# ═══════════════════════════════════════════════════════════════
def main():
    pygame.init()
    screen = pygame.display.set_mode((WIDTH, HEIGHT))
    pygame.display.set_caption("🎭 FRAYMUS AVATAR — The Face of the Organism")
    clock = pygame.time.Clock()

    # Start TTS thread
    tts_thread = threading.Thread(target=tts_worker, daemon=True)
    tts_thread.start()

    # Start socket listener
    sock_thread = threading.Thread(target=socket_listener, daemon=True)
    sock_thread.start()

    print("🎭 FRAYMUS AVATAR ONLINE. Waiting for Java Core...")

    blink_interval = 3.0
    last_blink = time.time()
    t = 0.0

    running = True
    while running:
        dt = clock.tick(FPS) / 1000.0
        t += dt

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    running = False

        # Blink logic
        now = time.time()
        if now - last_blink > blink_interval:
            STATE.blink_state = True
            last_blink = now
            blink_interval = np.random.uniform(2.0, 5.0)
        elif now - last_blink > 0.15:
            STATE.blink_state = False

        draw_avatar(screen, t)
        pygame.display.flip()

    SPEECH_QUEUE.put(None)  # Signal TTS to stop
    pygame.quit()

if __name__ == "__main__":
    main()
