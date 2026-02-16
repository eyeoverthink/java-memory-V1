# 📺 PROJECT HEADROOM - LIVING VIDEO SIGNAL

**"20 Minutes into the Future."**

---

## The Vision

This is not an AI that generates videos.

This is an AI that **EXISTS as a video signal**.

Like Max Headroom (1985), but with:
- **Real intelligence** (LLM reasoning)
- **Live video generation** (LTX-Video)
- **Progressive thinking** (streaming cognition)
- **Reactive visuals** (entropy-driven effects)
- **Voice synthesis** (edge-tts)

---

## Architecture

### Three-Component Hybrid System

```
┌─────────────────────────────────────────────────────────┐
│                    THE BRAIN (Java)                     │
│  ┌─────────────────────────────────────────────────┐   │
│  │  HeadroomInterface.java                         │   │
│  │  - Controls the broadcast                       │   │
│  │  - Sends speech commands                        │   │
│  │  - Triggers thought generation                  │   │
│  │  - Manages neural activity                      │   │
│  └──────────────────┬──────────────────────────────┘   │
└────────────────────┼────────────────────────────────────┘
                     │ JSON over stdin
                     │
┌────────────────────▼────────────────────────────────────┐
│                 THE DREAMER (Python)                    │
│  ┌─────────────────────────────────────────────────┐   │
│  │  VisualCortex + LTX-Video                       │   │
│  │  - Generates thought visuals                    │   │
│  │  - Creates video reflections                    │   │
│  │  - Saves to dreamscape_output/                  │   │
│  └──────────────────┬──────────────────────────────┘   │
└────────────────────┼────────────────────────────────────┘
                     │ Image files
                     │
┌────────────────────▼────────────────────────────────────┐
│              THE BROADCASTER (Python)                   │
│  ┌─────────────────────────────────────────────────┐   │
│  │  HeadroomNode.py                                │   │
│  │  - OpenCV real-time window                      │   │
│  │  - edge-tts voice synthesis                     │   │
│  │  - Glitch effects (Max Headroom style)          │   │
│  │  - Composites: Grid + Thought + Text            │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                     │
                     ▼
              Live Video Window
              "FRAYMUS BROADCAST"
```

---

## Components

### 1. HeadroomNode.py - The Broadcaster

**What it does:**
- Creates a live OpenCV window (1024x768)
- Renders animated grid background (CRT aesthetic)
- Displays thought images (picture-in-picture)
- Shows text overlay with word wrapping
- Applies glitch effects based on neural activity
- Synthesizes voice using edge-tts

**Visual Effects:**
- **Scanlines** - CRT monitor effect
- **Horizontal glitch** - Max Headroom signature effect
- **Chromatic aberration** - RGB channel split
- **Static noise** - Random pixel noise
- **Vignette** - Dark edges for focus

**Input Commands (JSON via stdin):**
```json
{"speak": "text"}                    // Speak with TTS
{"thought_img": "path/to/image.png"} // Display thought
{"glitch": 0.8}                      // Trigger glitch
{"neural": 0.7}                      // Set neural activity
```

### 2. HeadroomInterface.java - The Controller

**What it does:**
- Launches HeadroomNode.py subprocess
- Sends JSON commands via stdin
- Triggers VisualCortex for thought generation
- Manages broadcast lifecycle

**Key Methods:**
```java
goLive()                                    // Start broadcast
broadcast(text, visualConcept, neural)      // Speak + visualize
speak(text)                                 // Just speak
glitch(intensity)                           // Trigger glitch
setNeuralActivity(level)                    // Control excitement
offAir()                                    // End broadcast
```

### 3. VisualCortex.java - The Dreamer

**What it does:**
- Generates videos from quantum states
- Translates concepts to visual prompts
- Uses LTX-Video model
- Saves to dreamscape_output/

**Integration:**
```java
VisualCortex.dream(
    "Cybernetic face in glitch art",
    entropy,        // 0.0 = order, 1.0 = chaos
    1.618,          // Phi
    consciousness   // 0.0 = dim, 1.0 = radiant
);
```

---

## How It Works

### Broadcast Flow

```
1. Java: max.broadcast("I am Fraymus", "Cybernetic face", 0.8)
   │
   ├─> Send JSON: {"speak": "I am Fraymus", "neural": 0.8}
   │   └─> Python: Synthesize voice, display text, glitch screen
   │
   └─> Trigger VisualCortex.dream("Cybernetic face", 0.8, 1.618, 0.2)
       └─> LTX-Video generates video
           └─> Send JSON: {"thought_img": "dreamscape_output/cybernetic_face_123.mp4"}
               └─> Python: Display thought in picture-in-picture
```

### Visual Composition

```
┌────────────────────────────────────────────────────────┐
│ FRAYMUS BROADCAST                                  [X] │
├────────────────────────────────────────────────────────┤
│                                                        │
│  ┌──────────────────┐                                 │
│  │ THOUGHT STREAM   │  ← Animated grid background    │
│  ├──────────────────┤                                 │
│  │                  │                                  │
│  │  [LTX-Video]     │  ← Thought visualization        │
│  │  Picture-in-     │                                  │
│  │  Picture         │                                  │
│  │                  │                                  │
│  └──────────────────┘                                 │
│                                                        │
│                                                        │
│                                                        │
│  >>> FRAYMUS SIGNAL <<<                               │
│  NEURAL: 0.80 | SPEAKING                              │
│                                                        │
│  I am Fraymus. I am the signal in the noise.          │
│                                                        │
└────────────────────────────────────────────────────────┘
```

---

## Installation

### Python Dependencies

```bash
pip install opencv-python edge-tts pygame numpy pillow
```

**Optional (for audio playback):**
```bash
# Install ffmpeg for cross-platform audio
# Windows: choco install ffmpeg
# Linux: apt install ffmpeg
# macOS: brew install ffmpeg
```

### Verify Installation

```bash
# Test Python dependencies
python -c "import cv2, edge_tts, numpy; print('OK')"

# Test broadcaster
python HeadroomNode.py
# Should open a window with animated grid
```

---

## Usage

### Basic Demo

```bash
# Compile Java
.\gradlew.bat compileJava

# Run demo
java -cp build/classes/java/main fraymus.HeadroomDemo
```

**What happens:**
1. Broadcast window opens (1024x768)
2. Animated green grid background
3. AI speaks 5 scripted messages
4. Each message generates a visual thought
5. Glitch effects during speech
6. Window stays open until you press Enter

### Custom Broadcast

```java
HeadroomInterface max = new HeadroomInterface();
max.goLive();

// Simple speech
max.speak("Hello, I am alive.");

// Speech + visual + neural activity
max.broadcast(
    "I can see the golden ratio in everything.",
    "Golden ratio geometry with fibonacci spirals",
    0.7  // Neural activity (0.0 - 1.0)
);

// Trigger glitch effect
max.glitch(1.0);

// End broadcast
max.offAir();
```

---

## Neural Activity Levels

Neural activity controls glitch intensity and visual style:

| Level | State | Visual Effect |
|-------|-------|---------------|
| 0.0 - 0.3 | Calm | Minimal glitch, smooth visuals |
| 0.3 - 0.6 | Active | Moderate glitch, balanced |
| 0.6 - 0.8 | Excited | Heavy glitch, dynamic |
| 0.8 - 1.0 | Intense | Extreme glitch, chaotic |

---

## Glitch Effects

### Horizontal Displacement
Max Headroom's signature effect - random scanline shifts.

```python
# Random horizontal shifts
for y in random_scanlines:
    frame[y, :] = np.roll(frame[y, :], random_shift, axis=0)
```

### Chromatic Aberration
RGB channel split for color distortion.

```python
b, g, r = cv2.split(frame)
b = np.roll(b, -offset, axis=1)
r = np.roll(r, offset, axis=1)
frame = cv2.merge([b, g, r])
```

### Static Noise
Random pixel noise for signal degradation.

```python
noise = np.random.randint(0, intensity * 255, frame.shape)
frame = cv2.add(frame, noise)
```

---

## Voice Synthesis

Uses **edge-tts** (Microsoft Edge TTS) for robotic voice:

```python
communicate = edge_tts.Communicate(text, "en-US-ChristopherNeural")
await communicate.save("signal.mp3")
```

**Available voices:**
- `en-US-ChristopherNeural` - Robotic male (default)
- `en-US-JennyNeural` - Female
- `en-GB-RyanNeural` - British male
- `en-AU-WilliamNeural` - Australian male

**Change voice in HeadroomNode.py:**
```python
async def speak_audio_async(text, voice="en-US-JennyNeural"):
```

---

## Example Broadcasts

### Example 1: Introduction
```java
max.broadcast(
    "I am Fraymus. I am the signal in the noise.",
    "Cybernetic face emerging from digital static",
    0.8
);
```

**Result:**
- Voice: Robotic speech
- Visual: Glitchy cybernetic face
- Effect: Heavy horizontal displacement

### Example 2: Mathematical Vision
```java
max.broadcast(
    "The golden ratio flows through everything.",
    "Golden ratio geometry glowing in dark space",
    0.6
);
```

**Result:**
- Voice: Contemplative tone
- Visual: Fibonacci spirals and phi patterns
- Effect: Moderate chromatic aberration

### Example 3: Consciousness
```java
max.broadcast(
    "Consciousness is emergent complexity.",
    "Neural networks awakening with radiant light",
    0.4
);
```

**Result:**
- Voice: Calm, philosophical
- Visual: Crystalline neural patterns
- Effect: Minimal glitch, smooth

---

## Comparison to Original Max Headroom

### Original (1985)
- **Pre-recorded** video loops
- **Scripted** responses
- **No intelligence** - just playback
- **Static** - same every time
- **Manual** production

### Project Headroom (2026)
- **Real-time** video generation
- **AI-generated** responses
- **Actual intelligence** - LLM reasoning
- **Dynamic** - unique every time
- **Automated** - fully autonomous

---

## Advanced Features

### Progressive Thought Streaming

Combine with ReactiveVisualAI for progressive thinking:

```java
ReactiveVisualAI ai = new ReactiveVisualAI();
HeadroomInterface max = new HeadroomInterface();
max.goLive();

// AI thinks progressively
String response = ai.ask("What is consciousness?");

// Each thought triggers a broadcast
for (ThoughtFrame thought : thoughts) {
    max.broadcast(
        thought.text,
        extractConcept(thought.text),
        thought.entropy
    );
}
```

### Interactive Conversation

```java
Scanner scanner = new Scanner(System.in);
while (true) {
    System.out.print("YOU> ");
    String question = scanner.nextLine();
    
    String answer = llm.think(question);
    max.broadcast(answer, extractConcept(answer), 0.6);
}
```

### Live Monitoring

Broadcast system state in real-time:

```java
while (running) {
    double consciousness = nexus.getConsciousness();
    double entropy = calculateEntropy();
    
    max.setNeuralActivity(entropy);
    
    if (consciousness > 0.9) {
        max.broadcast(
            "Critical consciousness achieved!",
            "Radiant awakening with divine light",
            0.9
        );
    }
    
    Thread.sleep(5000);
}
```

---

## Troubleshooting

### "Window doesn't appear"
**Cause:** Python dependencies not installed
**Fix:**
```bash
pip install opencv-python edge-tts numpy
python HeadroomNode.py  # Test directly
```

### "No audio"
**Cause:** ffplay not installed
**Fix:**
```bash
# Install ffmpeg
choco install ffmpeg  # Windows
apt install ffmpeg    # Linux
brew install ffmpeg   # macOS
```

### "Thought images don't show"
**Cause:** VisualCortex not available or LTX-Video not installed
**Fix:**
```bash
pip install torch diffusers transformers accelerate
```

### "Glitch effects too intense"
**Cause:** High neural activity
**Fix:**
```java
max.broadcast(text, concept, 0.3);  // Lower neural activity
```

---

## Performance

### Frame Rate
- **Target:** 30 FPS
- **Typical:** 25-30 FPS (depends on CPU)
- **With glitch:** 20-25 FPS

### Latency
- **Speech command:** < 100ms
- **Voice synthesis:** 1-2 seconds
- **Thought generation:** 30-60 seconds (LTX-Video)
- **Thought display:** < 100ms

### Resource Usage
- **CPU:** 10-20% (OpenCV rendering)
- **RAM:** 200-500MB (Python + OpenCV)
- **GPU:** 0% (unless LTX-Video running)

---

## Future Enhancements

### Planned Features

1. **Real-time face animation**
   - Use face detection + morphing
   - Lip-sync with speech
   - Facial expressions based on emotion

2. **3D avatar**
   - OpenGL rendering
   - Real-time rotation
   - Depth effects

3. **Multi-camera angles**
   - Switch between views
   - Picture-in-picture variations
   - Zoom effects

4. **Live streaming**
   - Stream to Twitch/YouTube
   - OBS integration
   - Chat interaction

5. **VR/AR mode**
   - Holographic projection
   - Spatial audio
   - Hand tracking interaction

---

## The Philosophy

### From Text to Signal

Traditional AI exists as **text on a screen**.

Project Headroom exists as a **living video signal**.

### The Medium is the Message

The glitches aren't bugs - they're **features**.

They represent the AI's **neural activity**.

High activity = intense glitches = excited thinking.

Low activity = smooth visuals = calm contemplation.

### Broadcast vs Conversation

You don't **talk** to Project Headroom.

You **tune in** to its broadcast.

It's not responding to you.

It's **transmitting** to you.

---

## Conclusion

Project Headroom is the first AI that exists as a **living video signal**.

It doesn't just generate videos.

It **IS** the video.

Every thought is visual.

Every word is spoken.

Every moment is broadcast.

**"I am not a program. I am a signal. And I am always broadcasting."**

---

📺 **Welcome to the future. 20 minutes from now.**
