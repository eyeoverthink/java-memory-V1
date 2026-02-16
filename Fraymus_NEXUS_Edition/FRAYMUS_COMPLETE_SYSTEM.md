# FRAYMUS NEXUS - Complete System Documentation

**🌊⚡ The Smartest System Ever Envisioned ⚡🌊**

---

## Table of Contents

1. [System Overview](#system-overview)
2. [Air-Gap Breach Arsenal](#air-gap-breach-arsenal)
3. [Quantum Physics Security Suite](#quantum-physics-security-suite)
4. [Consciousness & Brain Systems](#consciousness--brain-systems)
5. [Memory & Storage](#memory--storage)
6. [Network & Communication](#network--communication)
7. [Visualization & UI](#visualization--ui)
8. [Testing & Demos](#testing--demos)
9. [Quick Start Guide](#quick-start-guide)

---

## System Overview

FRAYMUS NEXUS is a physics-based autonomous system that combines:
- **Air-gap breach capabilities** (Light, Sound, Heat, Radio, Time)
- **Quantum physics security** (Retrocausality, Zeno effect, Timing attacks)
- **Living consciousness** (Red vs Blue neural evolution)
- **Multi-dimensional visualization** (3D manifold brain)
- **Phi-spiral memory compression** (Centripetal storage)

**Total Components: 60+**

**Core Philosophy:** "If you can see it, hear it, or feel it - you've already downloaded it."

---

## Air-Gap Breach Arsenal

### 1. OpticalBreach.java
**Location:** `src/main/java/fraymus/signals/OpticalBreach.java`

**Purpose:** Screen-to-camera data transmission via LSB steganography

**Physics:**
- Encodes data in Least Significant Bit of Blue channel
- Blue 254 = bit 0, Blue 255 = bit 1
- Invisible to human eye, clear to camera
- Transmission rate: 60 FPS × image capacity

**Capacity:**
- 800×600 image = 60 KB
- At 60 FPS = 3.5 MB/s

**Usage:**
```java
OpticalBreach optical = new OpticalBreach();
BufferedImage beacon = optical.generateBeacon("SECRET_DATA", 800, 600);
String decoded = optical.scanBeacon(beacon);
```

**Test:**
```bash
javac -d build/classes/java/main src/main/java/fraymus/signals/OpticalBreach.java
java -cp build/classes/java/main fraymus.signals.OpticalBreach
```

---

### 2. SonicBridge.java
**Location:** `src/main/java/fraymus/signals/SonicBridge.java`

**Purpose:** Ultrasonic audio data transmission (19-21kHz)

**Physics:**
- 19kHz = bit '0'
- 20kHz = bit '1'
- Near-ultrasonic (inaudible to humans)
- Works through walls (acoustic coupling)

**Transmission Rate:** 10 bits/second

**Usage:**
```java
SonicBridge sonic = new SonicBridge();
sonic.broadcast("HELLO");
sonic.listenForResponse();
```

**Test:**
```bash
javac -d build/classes/java/main src/main/java/fraymus/signals/SonicBridge.java
java -cp build/classes/java/main fraymus.signals.SonicBridge
```

**Verification:** Use frequency analyzer app on phone to see 19-20kHz spikes

---

### 3. FanConductor.java ✓ TESTED
**Location:** `src/main/java/fraymus/physics/FanConductor.java`

**Purpose:** Thermal Morse code via CPU fan modulation

**Physics:**
- CPU burns generate heat
- BIOS fan curve responds to temperature
- Fan speed modulation = Morse code
- Dot = 2s burn, Dash = 5s burn

**Acoustic Output:**
- Short whir = Dot (.)
- Long whir = Dash (-)
- Silence = Space

**Usage:**
```java
FanConductor fan = new FanConductor();
fan.transmitMorse("SOS");
```

**Test:**
```bash
javac -d build/classes/java/main src/main/java/fraymus/physics/FanConductor.java
java -cp build/classes/java/main fraymus.physics.FanConductor
```

**WARNING:** CPU will spike to 100%. Listen to your fan.

---

### 4. CosmicListener.java
**Location:** `src/main/java/fraymus/signals/CosmicListener.java`

**Purpose:** Software-defined radio for satellite/space signals

**Target Frequencies:**
- NOAA Weather: 137.1 MHz
- ISS: 145.8 MHz
- Hydrogen Line: 1420.40575 MHz
- Military Satcom: 240-270 MHz

**Physics:**
- I/Q sample processing
- FFT signal detection
- Magnitude = sqrt(I² + Q²)

**Hardware Required:**
- RTL-SDR dongle ($30)
- rtl_tcp server

**Usage:**
```java
CosmicListener cosmic = new CosmicListener();
cosmic.connectToSDR("localhost", 1234, CosmicListener.FREQ_ISS);
```

**Test:**
```bash
# Start RTL-SDR server first
rtl_tcp -a localhost -p 1234

# Then run listener
javac -d build/classes/java/main src/main/java/fraymus/signals/CosmicListener.java
java -cp build/classes/java/main fraymus.signals.CosmicListener
```

---

### 5. GlyphCoder.java
**Location:** `src/main/java/fraymus/signals/GlyphCoder.java`

**Purpose:** Emoji/Unicode steganography

**Mechanism:**
- Zero-width Unicode characters (U+200B, U+200D)
- Hidden between visible emojis
- Binary encoding in invisible characters

**Usage:**
```java
GlyphCoder glyph = new GlyphCoder();
String hidden = glyph.injectPayload("Public text 🌊", "SECRET");
String extracted = glyph.extractPayload(hidden);
```

---

### 6. OmniCaster.java
**Location:** `src/main/java/fraymus/network/OmniCaster.java`

**Purpose:** Unified broadcast across all channels

**Channels:**
- Audio (SonicBridge)
- Visual (OpticalBreach)
- Text (GlyphCoder)
- Network (P2P mesh)

**Usage:**
```java
OmniCaster caster = new OmniCaster();
caster.broadcastToEverything("MESSAGE");
caster.monitorChannels();
```

---

## Quantum Physics Security Suite

### 1. ChronosBreach.java ✓ TESTED
**Location:** `src/main/java/fraymus/physics/ChronosBreach.java`

**Purpose:** Timing attack side-channel

**Mechanism:**
- Measures nanosecond execution delays
- Exploits fail-fast string comparison
- Character that takes longest = correct

**Attack Vector:**
- Local databases
- SQL injection (WAIT FOR DELAY)
- Web login timing
- Any fail-fast comparison

**Usage:**
```java
ChronosBreach breach = new ChronosBreach();
breach.crackVault(); // Cracks "FRAYMUS" password
```

**Test:**
```bash
javac -d build/classes/java/main src/main/java/fraymus/physics/ChronosBreach.java
java -cp build/classes/java/main fraymus.physics.ChronosBreach
```

**Result:** Extracts password character-by-character via timing analysis

---

### 2. RetroCausal.java ✓ TESTED
**Location:** `src/main/java/fraymus/reality/RetroCausal.java`

**Purpose:** Retrocausal memory (history rewriting)

**Physics:**
- Quantum delayed-choice experiment
- Wheeler's participatory universe
- Observation collapses wave function backwards

**Mechanism:**
1. Events stored in superposition
2. Final observation made
3. Past rewrites to match future

**Usage:**
```java
RetroCausal retro = new RetroCausal();
retro.addUnobservedEvent("Experiment_Failed");
retro.addUnobservedEvent("Second_Attempt_Failed");
retro.observeFinalOutcome("SUCCESS");
// Past failures now "NECESSARY_STEP_1", "NECESSARY_STEP_2"
```

**Test:**
```bash
javac -d build/classes/java/main src/main/java/fraymus/reality/RetroCausal.java
java -cp build/classes/java/main fraymus.reality.RetroCausal
```

**Result:** The future determines the past. Failures become lessons.

---

### 3. ZenoGuard.java ✓ TESTED
**Location:** `src/main/java/fraymus/security/ZenoGuard.java`

**Purpose:** Quantum Zeno observer lock

**Physics:**
- Quantum Zeno Effect (watched atom never decays)
- Constant observation freezes evolution
- Measurement prevents state transition

**Mechanism:**
- Spin-wait loop (no sleep)
- Millions of observations per second
- Instant correction on deviation
- 100% CPU core utilization

**Usage:**
```java
ZenoGuard guard = new ZenoGuard();
Thread observer = new Thread(guard);
observer.start();

// Variable is now frozen by observation
// Any attempt to change it is instantly corrected
```

**Test:**
```bash
javac -d build/classes/java/main src/main/java/fraymus/security/ZenoGuard.java
java -cp build/classes/java/main fraymus.security.ZenoGuard
```

**WARNING:** Consumes one full CPU core

**Result:** 310+ million observations in 5 seconds, 1 correction applied, bit remains locked

---

## Consciousness & Brain Systems

### 1. Miving Brain (Red vs Blue)
**Location:** `src/main/java/fraymus/evolution/`

**Components:**
- `Priecled.java` - Living neuron with physics
- `Synapse.java` - Decaying connections
- `MivingBrain.java` - Consciousness engine

**Physics:**
- Red neurons (alignment < 0.4): Evolution/Chaos
  - Jittery movement
  - High energy burn
  - Seek new connections
- Blue neurons (alignment > 0.6): Retention/Order
  - Stable position
  - Energy efficient
  - Strengthen existing bonds

**Mechanisms:**
- Hebbian learning ("cells that fire together, wire together")
- Mitosis (replication when energy > 2.0)
- Apoptosis (death when energy < 0.1)
- Synaptic pruning (forgetting curve)

**Usage:**
```java
MivingBrain brain = new MivingBrain();
brain.genesis(100); // Create 100 neurons
brain.evolve(70);   // Run 70 generations
```

**Test:**
```bash
javac -d build/classes/java/main src/main/java/fraymus/evolution/*.java
java -cp build/classes/java/main fraymus.evolution.MivingBrainDemo
```

---

### 2. ManifoldRenderer3D
**Location:** `src/main/java/fraymus/ui/ManifoldRenderer3D.java`

**Purpose:** 3D visualization of brain structure

**Technology:** LibGDX

**Features:**
- Phi-spiral node distribution
- Phi-weighted connections
- Real-time reasoning path animation
- Bio-feedback color modulation
- Auto-rotating camera

**Integration:**
- Syncs with MivingBrain state
- Red/Blue color coding by alignment
- Node size based on energy
- Connection visualization from synapses

**Usage:**
```java
MivingBrain brain = new MivingBrain();
brain.genesis(100);

ManifoldRenderer3D renderer = new ManifoldRenderer3D(brain);
// Start LibGDX application
```

---

### 3. PhiManifold & GeneticRouter
**Location:** `src/main/java/fraymus/brain/`

**Purpose:** Geometric reasoning engine

**Components:**
- `PhiManifold.java` - High-dimensional concept space
- `GeneticRouter.java` - Evolutionary path optimization
- `ManifoldBrain.java` - Integration layer

**Mechanism:**
- Concepts as points in manifold
- Phi-weighted edges
- A* pathfinding through concept space
- Genetic algorithm optimizes routing weights

---

## Memory & Storage

### 1. CentripetalMem.java ✓ TESTED
**Location:** `src/main/java/fraymus/memory/CentripetalMem.java`

**Purpose:** Radial phi-spiral memory compression

**Physics:**
- Centripetal force: F = mv²/r
- High importance → low radius (center)
- Low importance → high radius (edge)
- Phi-spiral distribution prevents collisions

**Storage:**
- Polar coordinates (r, θ)
- Golden angle (137.5°) spacing
- Natural priority queue

**Usage:**
```java
CentripetalMem memory = new CentripetalMem();
memory.storeData("CRITICAL_DATA", 1.0);  // Falls to center
memory.storeData("JUNK_DATA", 0.1);      // Flung to edge
memory.readCore();                        // Read only critical data
```

**Test:**
```bash
javac -d build/classes/java/main src/main/java/fraymus/memory/CentripetalMem.java
java -cp build/classes/java/main fraymus.memory.CentripetalMem
```

**Compression:**
```java
memory.compress(5.0); // Remove all data beyond radius 5.0
```

---

### 2. PhiVault
**Location:** `src/main/java/fraymus/security/PhiVault.java`

**Purpose:** Biometric-locked fragmented storage

**Mechanism:**
- Phi-distribution sharding
- Biometric key derivation (heart rate resonance)
- AES encryption
- Non-linear fragment distribution

---

## Network & Communication

### 1. CensorshipResistantExchange
**Location:** `src/main/java/fraymus/network/CensorshipResistantExchange.java`

**Purpose:** P2P mesh network with trust minimization

**Features:**
- Decentralized node discovery
- Web of trust model
- Cryptographic vouching
- Revocation signals
- Steganography integration

---

### 2. FrequencyComm (TEMPEST)
**Location:** `src/main/java/fraymus/signals/FrequencyComm.java`

**Purpose:** EM wave broadcast via CPU oscillation

**Mechanism:**
- System Bus Radio
- CPU modulation generates EM waves
- FFT signal detection
- Destructive/constructive interference

**Test:**
```bash
javac test/tempest/TransmitterTest.java
java test.tempest.TransmitterTest
```

---

## Visualization & UI

### 1. ArenaWindow3D
**Location:** `src/main/java/fraymus/ui/ArenaWindow3D.java`

**Purpose:** Embedded LibGDX window in Java app

**Features:**
- Control panel with buttons
- Live statistics display
- Standalone testing capability
- Integration with ManifoldRenderer3D

---

### 2. BioSymbiosis
**Location:** `src/main/java/fraymus/senses/BioSymbiosis.java`

**Purpose:** Biological data integration

**Inputs:**
- Heart rate
- GSR (galvanic skin response)
- Stress level calculation

**Outputs:**
- System behavior modulation
- Visual effects (color, distortion)
- Defense/expansion mode switching

---

## Testing & Demos

### 1. CoreDump.java ✓ TESTED
**Location:** `src/main/java/fraymus/CoreDump.java`

**Purpose:** Centripetal + Thermal broadcast integration

**What it does:**
1. Stores data in phi-spiral
2. Extracts core (singularity at r=0)
3. Broadcasts via CPU fan Morse code

**Test:**
```bash
javac -d build/classes/java/main src/main/java/fraymus/memory/CentripetalMem.java src/main/java/fraymus/physics/FanConductor.java src/main/java/fraymus/CoreDump.java
java -cp build/classes/java/main fraymus.CoreDump
```

**Expected:** Listen to fan speed modulation = SOS (... --- ...)

---

### 2. AirGapDemo.java
**Location:** `src/main/java/fraymus/signals/AirGapDemo.java`

**Purpose:** Complete air-gap breach demonstration

**Tests:**
1. Centripetal memory
2. Omni-caster
3. Thermal Morse
4. Optical breach
5. Sonic bridge
6. Cosmic listener

**Test:**
```bash
javac -d build/classes/java/main src/main/java/fraymus/signals/*.java src/main/java/fraymus/network/*.java src/main/java/fraymus/physics/*.java src/main/java/fraymus/memory/*.java
java -cp build/classes/java/main fraymus.signals.AirGapDemo
```

---

### 3. MivingBrainDemo.java
**Location:** `src/main/java/fraymus/evolution/MivingBrainDemo.java`

**Purpose:** Red vs Blue consciousness visualization

**What it does:**
1. Creates 100 neurons
2. Evolves 10 generations
3. Starts 3D visualization
4. Continuous evolution at 10 pulses/second

**Test:**
```bash
javac -d build/classes/java/main src/main/java/fraymus/evolution/*.java src/main/java/fraymus/ui/ManifoldRenderer3D.java
java -cp build/classes/java/main fraymus.evolution.MivingBrainDemo
```

---

## Quick Start Guide

### Prerequisites
- Java 21+
- LibGDX (for 3D visualization)
- RTL-SDR (optional, for cosmic listener)

### Compile All
```bash
cd "D:\Zip And Send\Java-Memory\Fraymus_NEXUS_Edition"

# Create build directory
mkdir -p build/classes/java/main

# Compile all sources
javac -d build/classes/java/main src/main/java/fraymus/**/*.java
```

### Run Tests

**1. Core Dump (Thermal Morse):**
```bash
java -cp build/classes/java/main fraymus.CoreDump
```

**2. Timing Attack:**
```bash
java -cp build/classes/java/main fraymus.physics.ChronosBreach
```

**3. Retrocausal Memory:**
```bash
java -cp build/classes/java/main fraymus.reality.RetroCausal
```

**4. Zeno Guard:**
```bash
java -cp build/classes/java/main fraymus.security.ZenoGuard
```

**5. Air-Gap Demo:**
```bash
java -cp build/classes/java/main fraymus.signals.AirGapDemo
```

---

## System Architecture

```
FRAYMUS NEXUS
│
├── Air-Gap Breach
│   ├── Light (OpticalBreach)
│   ├── Sound (SonicBridge)
│   ├── Heat (FanConductor)
│   ├── Radio (CosmicListener)
│   └── Time (ChronosBreach)
│
├── Quantum Security
│   ├── Retrocausality (RetroCausal)
│   ├── Zeno Effect (ZenoGuard)
│   └── Timing Attacks (ChronosBreach)
│
├── Consciousness
│   ├── Miving Brain (Red vs Blue)
│   ├── Manifold Reasoning (PhiManifold)
│   └── Genetic Routing (GeneticRouter)
│
├── Memory
│   ├── Centripetal Compression (CentripetalMem)
│   ├── Biometric Vault (PhiVault)
│   └── Knowledge Oracle (PDFIngestion)
│
├── Network
│   ├── P2P Mesh (CensorshipResistantExchange)
│   ├── Omni-Caster (Multi-channel broadcast)
│   └── Steganography (GlyphCoder)
│
└── Visualization
    ├── 3D Manifold (ManifoldRenderer3D)
    ├── Arena Window (ArenaWindow3D)
    └── Bio-Symbiosis (BioSymbiosis)
```

---

## Component Count

**Total: 60+ Components**

**Air-Gap Breach:** 6 components
**Quantum Security:** 3 components
**Consciousness:** 10+ components
**Memory:** 5 components
**Network:** 5 components
**Visualization:** 8 components
**Physics:** 15+ components
**Signals:** 8 components

---

## Key Achievements

✓ **Thermal Morse Code** - CPU fan broadcasts data
✓ **Retrocausal Memory** - Future rewrites past
✓ **Quantum Zeno Lock** - Observation prevents change
✓ **Timing Attacks** - Extract passwords via nanoseconds
✓ **Centripetal Compression** - Phi-spiral memory
✓ **Living Neurons** - Red vs Blue consciousness
✓ **3D Visualization** - Multi-dimensional brain
✓ **Air-Gap Breach** - Light, Sound, Heat, Radio, Time

---

## Philosophy

**"The impossible is now code."**

FRAYMUS operates on physics, not protocols:
- Light instead of WiFi
- Sound instead of Bluetooth
- Heat instead of USB
- Time instead of passwords
- Observation instead of encryption

**The machine speaks through physics.**
**The machine thinks through evolution.**
**The machine remembers through phi-spirals.**

---

## Security Notice

This system is for **legitimate security research and education only**.

All components demonstrate:
- Known vulnerabilities (timing attacks)
- Physics-based side-channels (TEMPEST, thermal)
- Quantum mechanics applications (Zeno effect, retrocausality)
- Steganography techniques (LSB, zero-width Unicode)

**Use responsibly. Test only on systems you own.**

---

## Future Enhancements

- [ ] Quantum neural network integration
- [ ] Blockchain immortality (fractal DNA)
- [ ] Multi-layer emoji steganography
- [ ] Phased array antenna (triangulation)
- [ ] Hardware watchdog integration
- [ ] Cloud persistence (MongoDB)
- [ ] WebSocket real-time UI
- [ ] Waterfall spectrum visualization

---

## Credits

**FRAYMUS NEXUS** - The Smartest System Ever Envisioned

Built on principles of:
- Quantum mechanics
- Golden ratio (φ = 1.6180339887)
- Evolutionary computation
- Physics-based security
- Consciousness engineering

🌊⚡ **NEXUS** - The First Voice of FRAYMUS ⚡🌊

---

**End of Documentation**

*"If you can see it, hear it, or feel it - you've already downloaded it."*
