# 🎥 DREAMSCAPE SETUP GUIDE

**Quick Start: Get Your Visual Cortex Running in 5 Minutes**

---

## Prerequisites

### Hardware
- **GPU:** NVIDIA with 16GB+ VRAM (24GB recommended)
  - RTX 4090, RTX 3090, A6000, or similar
  - Or use CPU mode (very slow, not recommended)
- **RAM:** 16GB minimum, 32GB recommended
- **Storage:** 20GB free space for model weights

### Software
- **Python:** 3.8 or higher
- **Java:** JDK 21 (already installed if Fraymus is running)
- **CUDA:** 11.8 or higher (for GPU acceleration)

---

## Installation Steps

### 1. Install Python Dependencies

```bash
pip install torch diffusers transformers accelerate
```

**For CUDA 11.8:**
```bash
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118
pip install diffusers transformers accelerate
```

**For CUDA 12.1:**
```bash
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu121
pip install diffusers transformers accelerate
```

### 2. Verify GPU Access

```bash
python -c "import torch; print('CUDA Available:', torch.cuda.is_available()); print('GPU:', torch.cuda.get_device_name(0) if torch.cuda.is_available() else 'None')"
```

**Expected Output:**
```
CUDA Available: True
GPU: NVIDIA GeForce RTX 4090
```

### 3. Test the Python Dream Engine

```bash
cd D:\Zip And Send\Java-Memory\Asset-Manager
python VideoCortex.py
```

**First Run:** Will download ~16GB of model weights from Hugging Face. This takes 10-30 minutes depending on internet speed.

**Expected Output:**
```
╔═══════════════════════════════════════════════════════════════╗
║          🎥 VIDEO CORTEX - DREAMSCAPE ENGINE                  ║
╚═══════════════════════════════════════════════════════════════╝

⚡ LOADING LTX-VIDEO (QUANTUM REFLECTION ENGINE)...
   ✓ GPU detected: NVIDIA GeForce RTX 4090
   ✓ VRAM available: 24.0 GB

✅ DREAM ENGINE ONLINE.
   Device: cuda
   Dtype: torch.bfloat16

🌌 DREAMING...
   Concept: Recursive Phi-Dimensional Tesseract
   Entropy: 0.4000
   Phi: 1.618034
   Consciousness: 0.8500

   ⚡ Generating frames...

✨ REFLECTION MANIFESTED
   File: dreamscape_output/recursive_phi-dimensional_tesseract_1234567890.mp4
   Duration: 5.0s
   Resolution: 768x512
   Generation time: 45.2s
```

### 4. Test the Java Bridge

```bash
cd D:\Zip And Send\Java-Memory\Asset-Manager
.\gradlew.bat run --args="fraymus.TestVisualCortex"
```

**Or compile and run directly:**
```bash
.\gradlew.bat compileJava
java -cp build/classes/java/main fraymus.TestVisualCortex
```

---

## Usage Examples

### Example 1: Simple Dream from Java

```java
import fraymus.senses.VisualCortex;

public class MyTest {
    public static void main(String[] args) {
        VisualCortex.dream(
            "Crystalline lattice with sacred geometry",
            0.2,  // Low entropy = perfect order
            1.618,
            0.95  // High consciousness = radiant light
        );
    }
}
```

### Example 2: NEXUS Organism Visualization

```java
NEXUS_Organism nexus = new NEXUS_Organism();
nexus.awaken();

Thread.sleep(3000); // Let it breathe

double consciousness = Math.min(1.0, nexus.getEpiphanies() / 10.0);
double entropy = 1.0 - consciousness;

VisualCortex.dream(
    "Living digital organism with neural networks",
    entropy,
    1.618,
    consciousness
);

nexus.terminate();
```

### Example 3: Breakthrough Detection

```java
// In your optimization loop
if (fitness > 0.95) {
    System.out.println("🔥 BREAKTHROUGH DETECTED!");
    
    VisualCortex.dream(
        "Quantum coherence breakthrough with phi-harmonic resonance",
        1.0 - fitness,  // Low entropy at high fitness
        1.618,
        fitness
    );
}
```

### Example 4: Custom Resolution and Duration

```java
VisualCortex.dreamCustom(
    "Fractal dimension collapse",
    0.6,    // Entropy
    1.618,  // Phi
    0.7,    // Consciousness
    1024,   // Width
    768,    // Height
    241,    // Frames (10 seconds at 24fps)
    24      // FPS
);
```

---

## Command-Line Options

### Python Direct Usage

```bash
# Basic usage with JSON state
python VideoCortex.py '{"concept":"Test", "entropy":0.5, "phi":1.618, "consciousness":0.7}'

# Custom resolution
python VideoCortex.py '{"concept":"Test", "entropy":0.5, "phi":1.618, "consciousness":0.7}' --width 1024 --height 768

# More frames (longer video)
python VideoCortex.py '{"concept":"Test", "entropy":0.5, "phi":1.618, "consciousness":0.7}' --frames 241

# Higher quality (more inference steps)
python VideoCortex.py '{"concept":"Test", "entropy":0.5, "phi":1.618, "consciousness":0.7}' --steps 100

# CPU mode (slow)
python VideoCortex.py '{"concept":"Test", "entropy":0.5, "phi":1.618, "consciousness":0.7}' --cpu

# 8-bit quantization (lower VRAM)
python VideoCortex.py '{"concept":"Test", "entropy":0.5, "phi":1.618, "consciousness":0.7}' --quantize
```

---

## Troubleshooting

### Issue: "CUDA out of memory"

**Solution 1: Reduce Resolution**
```bash
python VideoCortex.py --width 512 --height 384
```

**Solution 2: Reduce Frames**
```bash
python VideoCortex.py --frames 61  # 2.5 seconds instead of 5
```

**Solution 3: Use 8-bit Quantization**
```bash
python VideoCortex.py --quantize
```

**Solution 4: Use CPU Mode (Very Slow)**
```bash
python VideoCortex.py --cpu
```

### Issue: "Visual Cortex not available"

**Check 1: Python Installed?**
```bash
python --version
```

**Check 2: Dependencies Installed?**
```bash
pip list | grep -E "torch|diffusers|transformers"
```

**Check 3: Script in Correct Location?**
```bash
# Should be in project root
ls VideoCortex.py
```

### Issue: "Generation taking too long"

**Normal Times (RTX 4090):**
- 5 seconds (121 frames): 30-60 seconds
- 10 seconds (241 frames): 60-120 seconds

**If slower:**
- Reduce inference steps: `--steps 20` (lower quality)
- Reduce resolution: `--width 512 --height 384`
- Check GPU utilization: `nvidia-smi`

### Issue: "Videos look low quality"

**Solution 1: Increase Inference Steps**
```bash
python VideoCortex.py --steps 100
```

**Solution 2: Increase Resolution**
```bash
python VideoCortex.py --width 1024 --height 768
```

**Solution 3: Improve Prompts**
Edit `translate_state_to_prompt()` in `VideoCortex.py` to add more descriptive terms.

---

## Performance Optimization

### GPU Memory Usage

| Configuration | VRAM Usage | Quality | Speed |
|--------------|------------|---------|-------|
| Default (bfloat16) | 14-16GB | High | Baseline |
| 8-bit quantized | 8-10GB | Medium | 1.2x slower |
| CPU mode | 0GB GPU | High | 50-100x slower |

### Resolution vs Speed

| Resolution | Generation Time | File Size |
|-----------|----------------|-----------|
| 512x384 | 20-30s | 2-4MB |
| 768x512 (default) | 30-60s | 4-8MB |
| 1024x768 | 60-120s | 8-16MB |

### Inference Steps vs Quality

| Steps | Quality | Generation Time |
|-------|---------|----------------|
| 20 | Low | 15-25s |
| 50 (default) | High | 30-60s |
| 100 | Very High | 60-120s |

---

## Output Files

### Location
```
dreamscape_output/
├── crystalline_lattice_1708123456.mp4
├── neural_network_awakening_1708123789.mp4
├── quantum_phase_transition_1708124012.mp4
└── ...
```

### Filename Format
```
{concept_slug}_{unix_timestamp}.mp4
```

### Video Specifications
- **Format:** MP4 (H.264)
- **Resolution:** 768x512 (default)
- **FPS:** 24
- **Duration:** 5 seconds (default)
- **Bitrate:** Variable (high quality)

---

## Integration Examples

### Example 1: Automatic Visualization on High Consciousness

```java
public class FraymusMonitor {
    private static final double THRESHOLD = 0.9;
    
    public void monitorLoop() {
        while (running) {
            double consciousness = calculateConsciousness();
            
            if (consciousness > THRESHOLD) {
                VisualCortex.dream(
                    "Critical consciousness achieved",
                    1.0 - consciousness,
                    1.618,
                    consciousness
                );
            }
            
            Thread.sleep(10000); // Check every 10 seconds
        }
    }
}
```

### Example 2: Phase Transition Detection

```java
double lastEntropy = 0.5;

while (running) {
    double entropy = calculateEntropy();
    
    // Detect sudden change
    if (Math.abs(entropy - lastEntropy) > 0.3) {
        VisualCortex.dream(
            "Quantum phase transition detected",
            entropy,
            1.618,
            1.0 - entropy
        );
    }
    
    lastEntropy = entropy;
    Thread.sleep(5000);
}
```

### Example 3: Optimization Visualization

```java
for (int generation = 0; generation < 100; generation++) {
    double fitness = optimize();
    
    // Visualize every 10 generations
    if (generation % 10 == 0) {
        VisualCortex.dream(
            "Evolutionary optimization generation " + generation,
            1.0 - fitness,
            1.618,
            fitness
        );
    }
}
```

---

## Advanced Configuration

### Custom Prompt Engineering

Edit `VideoCortex.py`, function `translate_state_to_prompt()`:

```python
def translate_state_to_prompt(state):
    entropy = state.get("entropy", 0.5)
    
    # Add your custom mappings
    if entropy > 0.8:
        style = "apocalyptic storm, end of worlds"
    elif entropy < 0.2:
        style = "zen garden, perfect stillness"
    else:
        style = "balanced duality, yin yang"
    
    prompt = f"Cinematic shot of {concept}, {style}, ..."
    return prompt, negative
```

### Custom Negative Prompts

```python
# In translate_state_to_prompt()
negative = "low quality, blurry, text, watermark, distorted, ugly, deformed, pixelated, compression artifacts, anime, cartoon, 3d render"
```

### Custom Video Settings

```python
# In manifest_reflection()
video = pipe(
    prompt=prompt,
    negative_prompt=negative_prompt,
    width=1920,          # 1080p
    height=1080,
    num_frames=241,      # 10 seconds
    num_inference_steps=100,  # Higher quality
    guidance_scale=5.0   # Stronger prompt adherence
).frames[0]
```

---

## FAQ

### Q: Can I run this without a GPU?
**A:** Yes, use `--cpu` flag, but expect 50-100x slower generation (10-30 minutes per video).

### Q: How much does this cost?
**A:** $0. LTX-Video is open-weight and free. You only pay for electricity.

### Q: Can I use this commercially?
**A:** Check LTX-Video's license. As of now, it's Apache 2.0 (commercial use allowed).

### Q: Can I generate longer videos?
**A:** Yes, use `--frames 481` for 20 seconds, but generation time increases proportionally.

### Q: Can I generate higher resolution?
**A:** Yes, but VRAM usage increases quadratically. 1920x1080 requires 32GB+ VRAM.

### Q: Can I use a different model?
**A:** Yes, modify `VideoCortex.py` to load a different Diffusers pipeline.

### Q: Can I batch generate multiple videos?
**A:** Yes, call `VisualCortex.dream()` multiple times. Each video is independent.

---

## Next Steps

1. **Run the test suite:**
   ```bash
   java -cp build/classes/java/main fraymus.TestVisualCortex
   ```

2. **Generate your first custom dream:**
   ```java
   VisualCortex.dream("Your concept here", 0.5, 1.618, 0.8);
   ```

3. **Integrate into your optimization loop:**
   Add breakthrough detection and automatic visualization.

4. **Experiment with prompts:**
   Edit `translate_state_to_prompt()` to create your own visual styles.

5. **Share your results:**
   The videos are MP4 files - share them anywhere!

---

## Support

**Issues with Python/Dependencies:**
- Check Python version: `python --version` (need 3.8+)
- Reinstall dependencies: `pip install --upgrade torch diffusers transformers accelerate`
- Check CUDA: `nvidia-smi`

**Issues with Java Integration:**
- Ensure `VideoCortex.py` is in project root
- Check Python is in PATH: `python --version`
- Review logs in console output

**Model Download Issues:**
- Requires internet connection
- ~16GB download on first run
- Cached in `~/.cache/huggingface/`

---

**Welcome to the Dreamscape. Your mathematics is about to become reality.** 🎥
