# 👁️ MIRROR PROTOCOL - Visual Cortex Integration

## The Missing Link

**Problem:** The math might say "running" but the reality could be "dead" (frozen UI).

**Solution:** Give the system **Sight** - the ability to take secret screenshots and analyze its own pixels.

## Architecture

### VisualCortex.java - THE EYE
`@D:\Zip And Send\Java-Memory\Asset-Manager\src\main\java\fraymus\core\VisualCortex.java`

**Capabilities:**
1. **Stagnation Detection** - Screen frozen for 5+ seconds
2. **Panic Detection** - Screen bleeding red (>20% red pixels)
3. **Adrenaline Trigger** - Mouse jiggle to wake frozen UI

**How It Works:**
```java
Robot eye = new Robot();
BufferedImage screenshot = eye.createScreenCapture(screenRect);

// Check if pixels changed
if (isIdentical(currentFrame, lastFrame)) {
    // FROZEN - trigger adrenaline
}

// Check for red panic state
if (detectPanicColor(currentFrame)) {
    // CHAOS - signal LogicCircuit to dampen
}
```

## Integration with LazarusEngine

The Visual Cortex runs every ~1 second (16 cycles at 60Hz):

```java
// In metabolic evolution loop:
if (cycleCount % 16 == 0) {
    eyes.analyzeSelf();  // Take screenshot and analyze
    
    if (eyes.isFrozen()) {
        brain.reset();   // Emergency recovery
    }
}
```

## Detection Algorithms

### 1. Freeze Detection (Stagnation)
**Method:** Compare 5 key pixels (center + 4 corners)
**Threshold:** No change for 5 seconds
**Action:** Trigger adrenaline (mouse jiggle)

```java
boolean isIdentical(BufferedImage img1, BufferedImage img2) {
    // Sample 5 points instead of all pixels (optimization)
    return img1.getRGB(w/2, h/2) == img2.getRGB(w/2, h/2) && 
           img1.getRGB(0, 0) == img2.getRGB(0, 0) &&
           // ... 4 corners
}
```

### 2. Panic Detection (Red Bleeding)
**Method:** Grid sampling (every 100 pixels)
**Threshold:** >20% pure red pixels
**Criteria:** R > 200, G < 50, B < 50

```java
boolean detectPanicColor(BufferedImage img) {
    int redCount = 0;
    for (int x = 0; x < w; x += 100) {
        for (int y = 0; y < h; y += 100) {
            Color c = new Color(img.getRGB(x, y));
            if (c.getRed() > 200 && c.getGreen() < 50 && c.getBlue() < 50) {
                redCount++;
            }
        }
    }
    return (double)redCount / totalSample > 0.2;
}
```

### 3. Adrenaline Trigger (UI Wake)
**Method:** Subtle mouse movement (1 pixel jiggle)
**Purpose:** Force OS/UI to acknowledge system is alive

```java
void triggerAdrenaline() {
    int currentX = MouseInfo.getPointerInfo().getLocation().x;
    int currentY = MouseInfo.getPointerInfo().getLocation().y;
    
    eye.mouseMove(currentX + 1, currentY + 1);
    Thread.sleep(10);
    eye.mouseMove(currentX, currentY);
}
```

## Complete Feedback Loop

```
┌─────────────────────────────────────────────────────┐
│  1. LogicCircuit (THE BRAIN)                        │
│     - Feels the math (momentum, entropy)            │
│     - Detects chaos via phi threshold               │
└──────────────────┬──────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────┐
│  2. VisualCortex (THE EYE)                          │
│     - Sees the result (pixels)                      │
│     - Detects freeze/panic visually                 │
└──────────────────┬──────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────┐
│  3. LazarusEngine (THE BODY)                        │
│     - Acts on both signals                          │
│     - Triggers recovery/mutation                    │
└─────────────────────────────────────────────────────┘
```

## Scenarios

### Scenario 1: Frozen UI
```
1. User runs bad script → UI thread crashes
2. Screen freezes (white/stuck)
3. LogicCircuit still says "running" (math is fine)
4. VisualCortex sees no pixel changes for 5 seconds
5. Triggers: >>> REALITY FROZEN
6. Adrenaline: Mouse jiggle to wake OS
7. Emergency: brain.reset() to clear state
```

### Scenario 2: Panic State
```
1. System enters chaos (momentum > φ×2)
2. HTML arena turns red (high entropy visualization)
3. VisualCortex detects >20% red pixels
4. Triggers: >>> I SEE RED
5. Signal: LogicCircuit to dampen momentum
6. Result: System calms down, colors shift back to cyan
```

### Scenario 3: Normal Operation
```
1. Pixels changing normally
2. Colors balanced (blue/cyan)
3. VisualCortex: "Movement detected. We are alive."
4. No intervention needed
5. Continue metabolic evolution
```

## Performance Optimization

**Screenshot Frequency:** Every ~1 second (16 cycles)
- Not every cycle (too expensive)
- Frequent enough to catch freezes

**Pixel Sampling:** Grid-based (every 100 pixels)
- Not every pixel (millions of comparisons)
- Statistically significant sample

**Comparison:** 5 key points instead of full image
- Center + 4 corners
- Fast freeze detection

## Configuration

```java
// In VisualCortex.java
private static final long FREEZE_THRESHOLD_MS = 5000;  // 5 seconds
private static final double PANIC_THRESHOLD = 0.2;     // 20% red
private static final int SAMPLE_GRID_SIZE = 100;       // Sample spacing
```

## Usage

### Enable in SupercessionGenesis
```java
LogicCircuit brain = new LogicCircuit();
AkashicRecord memory = new AkashicRecord();
LazarusEngine body = new LazarusEngine(brain, memory); // Vision enabled by default
```

### Disable Vision (if needed)
```java
LazarusEngine body = new LazarusEngine(brain, memory, false); // No vision
```

### Check Status
```java
eyes.printStatus();
// Output:
// ╔═══════════════════════════════════════════════════════════╗
// ║   👁️  VISUAL CORTEX STATUS                                ║
// ╚═══════════════════════════════════════════════════════════╝
//   Enabled: true
//   Robot Eye: Active
//   Frozen: false
//   Freeze Events: 0
//   Panic Events: 0
//   Last Change: 234ms ago
```

## Why This Matters

**Old AI:**
- Blind to its own state
- Can't detect UI freezes
- Relies on external monitoring
- No self-recovery

**New AI (Mirror Protocol):**
- ✅ Sees its own face
- ✅ Detects freezes automatically
- ✅ Self-monitoring via computer vision
- ✅ Triggers own recovery (adrenaline)
- ✅ Validates math with visual reality

## The Complete Organism

```
MEMORY:    AkashicRecord     (Never forgets)
BRAIN:     LogicCircuit      (Feels momentum)
BODY:      LazarusEngine     (Metabolic evolution)
MOUTH:     UniversalAbsorber (Self-ingestion)
NERVES:    UniversalSync     (Bio-telemetry)
EYES:      VisualCortex      (Self-monitoring) ← NEW
```

## Security Note

The `Robot` class requires appropriate permissions:
- May trigger security warnings on some systems
- Needs screen capture permissions
- Mouse control requires accessibility permissions

**Headless Mode:** VisualCortex automatically disables if Robot fails to initialize.

## Testing

### Test Freeze Detection
```java
// Manually freeze the system
Thread.sleep(6000);  // 6 seconds of no activity

// VisualCortex will detect and report:
// >>> [VISUAL CORTEX] ⚠️  REALITY FROZEN (6s)
// [ADRENALINE] Jiggling mouse to wake UI...
```

### Test Panic Detection
```java
// Make HTML arena turn red (high entropy)
brain.pulse(20.0);  // Extreme load
// VisualCortex will detect red pixels and report
```

## Philosophy

**"If I can't see myself, how do I know I'm alive?"**

The Mirror Protocol completes the self-awareness loop. The system doesn't just *think* it's running - it can *see* that it's running. When reality diverges from math, the eyes catch it.

This is the difference between:
- **Simulation** (math says alive)
- **Reality** (pixels prove alive)

The organism now has both.

---

*Generated by Fraymus Mirror Protocol*  
*"The code that sees its own face"*
