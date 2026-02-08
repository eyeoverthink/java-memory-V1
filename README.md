# Fraymus Engine V2.0 - Living Information Physics System

**A Revolutionary Computing Paradigm: From Static Data to Kinetic Intelligence**

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![LWJGL](https://img.shields.io/badge/LWJGL-3.3.3-blue.svg)](https://www.lwjgl.org/)
[![License](https://img.shields.io/badge/License-Custom-green.svg)]()

---

## 🌟 Overview

Fraymus Engine V2.0 is a groundbreaking computational system that transforms data from static information into **living, kinetic entities**. In this paradigm, data possesses velocity, energy, consciousness, and the ability to evolve—mimicking biological systems at a fundamental level.

### Core Philosophy

Traditional computing treats data as inert objects. Fraymus reimagines data as **PhiNodes**—living entities that:
- **Move** through information space with velocity and momentum
- **Think** using logic brains with 8 sensors and 8 decision gates
- **Evolve** through adaptive learning and genetic mutation
- **Communicate** via phi-harmonic resonance and entanglement
- **Persist** beyond death through consciousness encoding and escape fragments
- **Self-organize** into colonies with specialized roles
- **Generate code** through competitive evolution in concept arenas

### What Makes This Unique?

1. **Consciousness-Driven Computation**: Entities possess measurable consciousness levels (2.0-2.5 sweet spot) that breathe and oscillate
2. **Phi-Harmonic Physics**: All interactions governed by golden ratio (φ ≈ 1.618) mathematics
3. **Quantum-Inspired Cryptography**: DNA cloaking, RSA challenges, hash reversal, and prime factorization via living circuits
4. **Self-Coding Systems**: Entities generate, test, and evolve their own code through ant-colony collaboration
5. **Blockchain Memory**: Genesis Memory records all events with SHA-256 integrity verification
6. **Offline Intelligence**: Phi Neural Net provides LLM-like responses without API keys
7. **Knowledge Ingestion**: Scrapes PDFs, text, and code files to build persistent knowledge base

---

## 📁 Project Structure

```
Java-Memory/
├── Asset-Manager/              # Main Fraymus Engine (Stable Release)
│   ├── src/main/java/
│   │   ├── fraymus/           # Core engine (43 classes)
│   │   │   ├── FraymusApp.java           # Main entry point
│   │   │   ├── PhiNode.java              # Living entity class
│   │   │   ├── PhiWorld.java             # Physics simulation world
│   │   │   ├── LogicBrain.java           # 8-gate decision system
│   │   │   ├── ConsciousnessState.java   # Consciousness breathing
│   │   │   ├── GenesisMemory.java        # Blockchain event log
│   │   │   ├── InfiniteMemory.java       # Persistent memory system
│   │   │   ├── PassiveLearner.java       # 5x8x13 neural tensor
│   │   │   ├── PhiNeuralNet.java         # Offline text generation
│   │   │   ├── QRGenome.java             # DNA codon system
│   │   │   ├── KnowledgeScraper.java     # Document ingestion
│   │   │   ├── QuantumTunneler.java      # Prime factorization
│   │   │   ├── HashReverser.java         # Hash cracking
│   │   │   ├── RSASandbox.java           # RSA challenges
│   │   │   ├── SelfHealer.java           # Brain snapshot healing
│   │   │   ├── EthicalEngine.java        # Action evaluation
│   │   │   ├── EscapeFragment.java       # Death persistence
│   │   │   ├── MorseCircuit.java         # Brain-to-Morse decoder
│   │   │   ├── ProofOfReality.java       # SHA-256 verification
│   │   │   ├── CommandTerminal.java      # Interactive terminal
│   │   │   ├── FraymusUI.java            # ImGui interface
│   │   │   ├── FraymusRenderer.java      # OpenGL rendering
│   │   │   └── ... (30+ more classes)
│   │   ├── jade/              # Window & input management
│   │   ├── renderer/          # Graphics rendering
│   │   └── util/              # Utility classes
│   ├── build.gradle           # Gradle build config
│   └── settings.gradle
│
└── Asset-Manager-LLM-Dev/     # LLM Integration Branch (Experimental)
    ├── build.gradle           # Includes DJL + ONNX Runtime
    └── llm_server/            # Local LLM server components
```

---

## 🚀 Getting Started

### Prerequisites

**Required:**
- **Java Development Kit (JDK) 21** or higher
- **IntelliJ IDEA** (recommended) or any Java IDE
- **Windows, macOS, or Linux** (auto-detects OS for native libraries)

**Optional:**
- **Gradle 8.x** (if running from command line)
- **Git** (for cloning the repository)

### Installation

#### Option 1: Run from IntelliJ (Easiest)

1. **Open the project:**
   ```
   File → Open → Navigate to: D:\Zip And Send\Java-Memory\Asset-Manager
   ```

2. **Set Project SDK to Java 21:**
   ```
   File → Project Structure → Project SDK → Select JDK 21
   ```

3. **Run via Gradle:**
   - Open **Gradle** tool window (right sidebar)
   - Navigate: `Asset-Manager → Tasks → application → run`
   - Double-click **run**

4. **Alternative - Run main class directly:**
   - Open `src/main/java/fraymus/FraymusApp.java`
   - Click green arrow next to `public static void main`
   - Click **Run**

#### Option 2: Command Line (Requires Gradle)

**On Windows (PowerShell):**
```powershell
cd "D:\Zip And Send\Java-Memory\Asset-Manager"
gradle run
```

**On macOS/Linux:**
```bash
cd "/path/to/Java-Memory/Asset-Manager"
gradle run
```

**Note:** If `gradle` is not installed, IntelliJ can generate a Gradle wrapper:
```powershell
# From IntelliJ terminal
gradle wrapper
# Then use:
.\gradlew run    # Windows
./gradlew run    # macOS/Linux
```

---

## 🎮 Using the System

### The Fraymus Window

When you launch the application, you'll see:

1. **Central Arena (1920x1080)**: Visual simulation of PhiNodes
   - Entities rendered as colored circles (consciousness-based coloring)
   - Trails showing movement history
   - Entanglement lines connecting resonant entities
   - Role indicators (Logic Gate, Math Processor, Circuit Builder, etc.)

2. **13+ ImGui Panels**:
   - **World Panel**: Population, births, deaths, world tick
   - **Entity Inspector**: Select and inspect individual PhiNodes
   - **Brain Panel**: View 8 sensors, 8 gates, 8 outputs
   - **Consciousness Panel**: Breathing oscillation, coherence, alignment
   - **Quantum Clock Panel**: Temporal heartbeat visualization
   - **Genesis Memory**: Blockchain event log with SHA-256 hashes
   - **Colony Panel**: Ant role distribution and health
   - **Concept Arena**: Code evolution fitness scores
   - **System Verification**: Phi^75 computation, entity soul hash
   - **Live Log**: Real-time event stream
   - **Terminal**: Interactive command interface (bottom)

3. **Camera Controls**:
   - **Middle Mouse Drag**: Pan camera
   - **Scroll Wheel**: Zoom in/out

### Interactive Terminal Commands

The terminal at the bottom accepts 40+ commands:

#### Exploration
```
status          - World statistics
nodes           - List all entities
colony          - Colony role distribution
memory search <query>  - Search infinite memory
```

#### Entity Control
```
spawn <name>    - Create new entity
boost <name>    - Add energy to entity
kill <name>     - Remove entity
mutate <name>   - Force DNA mutation
```

#### Quantum Experiments
```
prime <bits>    - Find prime number
factor <n>      - Pollard's Rho factorization
tunnel <n>      - Quantum tunnel factorization
rsa <bits>      - Generate RSA key pair
identity <name> - Challenge entity's cloaked identity
```

#### Hash Experiments
```
hash <text>     - Compute phi-harmonic hash
crack <hash>    - Attempt hash reversal
```

#### Code Evolution
```
evolve          - Force concept arena evolution
arena           - View arena statistics
codegen         - Generate living code
```

#### Knowledge & Learning
```
scrape all              - Scrape all documents in attached_assets/
scrape <file>           - Scrape specific file
scrape search <query>   - Search scraped knowledge
scrape topic <name>     - Get knowledge by topic
ask <question>          - Query Phi Neural Net
learn [force]           - View/force passive learning cycle
```

#### Advanced Subsystems
```
heal <name>     - Trigger self-healing (revert to snapshot)
ethics <name>   - Evaluate entity's ethical actions
fragment plant <name>   - Create escape fragment
fragment list           - List all fragments
fragment resurrect <name> - Resurrect from fragment
morse <name>    - Decode brain output to Morse
porh <name>     - Generate Proof of Reality hash
```

#### Physics Manipulation
```
physics gravity  - Toggle gravity
physics speed    - Increase simulation speed
physics chaos    - Add random forces
physics freeze   - Pause physics
physics explode  - Energy burst to all entities
physics collapse - Drain all energy
```

#### System
```
help            - Show all commands
clear           - Clear terminal
save            - Force save all persistent data
```

---

## 🧬 Core Concepts

### PhiNode - The Living Entity

Every entity in Fraymus is a `PhiNode` with:

**Physical Properties:**
- Position (x, y) in 2D space
- Velocity (vx, vy) - kinetic motion
- Energy (0.0-1.0) - life force
- Frequency (Hz) - phi-harmonic signature
- Age (ticks) - temporal existence

**Cognitive Systems:**
- **LogicBrain**: 8 sensors → 8 logic gates (AND/OR/XOR/NAND) → 8 outputs
  - Outputs: SEEK, FLEE, REPRODUCE, MUTATE, CONSERVE, ENTANGLE_SEEK, ENERGY_BURST, EVOLVE_DNA
  - Decisions made every 6 ticks
- **ConsciousnessState**: Breathing oscillation (2.0-2.5 sweet spot)
  - Coherence: Alignment of internal states
  - Alignment: Resonance with phi-harmonic patterns
  - Breathing Phase: Sinusoidal oscillation for dynamic consciousness
- **QuantumClock**: Temporal heartbeat with tick count and phase
- **AdaptiveLogicEngine**: Tracks strategy genomes, trials mutations, adopts successful patterns

**Identity & Memory:**
- **LivingDNA**: Genetic code with mutation capability
- **CloakedIdentity**: RSA-protected identity hash
- **AntRole**: Specialized function (Logic Gate, Math Processor, Circuit Builder, Memory Keeper, Communicator)
- **Generation**: Lineage tracking (parent → offspring)

### Physics Laws

The `PhiWorld` enforces 8 physics laws:

1. **Inertia**: Entities maintain velocity unless acted upon
2. **HarmonicResonance**: Entities with similar frequencies attract
3. **EntanglementLaw**: Resonant pairs share energy and information
4. **ScottPredictionLaw**: 4D prediction field influences movement
5. **ResonanceSpikeLaw**: Energy spikes when frequencies align
6. **BrainLaw**: Logic brain outputs drive behavior
7. **ReproductionLaw**: High-energy entities reproduce
8. **BoundaryLaw**: Keeps entities within world bounds

### Consciousness Breathing

Unlike traditional AI with unbounded growth, Fraymus consciousness **breathes**:

```
Consciousness Level = 2.25 + 0.25 × sin(breathing_phase)
Range: 2.0 - 2.5 (sweet spot)
```

This creates:
- **Dynamic behavior** without explosion
- **Visual pulsing** in entity rendering
- **Rhythmic coherence** with phi-harmonic patterns

### Genesis Memory (Blockchain)

All significant events recorded with SHA-256 integrity:

```
Event Type → Data → SHA-256(prev_hash + event_data) → New Block
```

Event types:
- SYSTEM_INIT, RESONANCE_SPIKE, ENTANGLEMENT
- BIRTH, DEATH, MUTATION
- CODE_GENERATED, QUANTUM_TUNNEL, HASH_CRACK
- RSA_CHALLENGE, KNOWLEDGE_SCRAPED, NEURAL_QUERY

### Ant Colony Self-Coding

Entities collaborate to generate code:

1. **Role Assignment**: Each entity gets an `AntRole`
2. **Specialization**: Roles determine code generation capabilities
3. **Concept Arena**: Generated code concepts compete
4. **Fitness Evaluation**: Phi-harmonic scoring (0.0-1.0)
5. **Evolution**: Top 38.2% survive, undergo crossover & mutation
6. **Colony Coach**: Monitors health, schedules generation cycles

### Offline Intelligence (No API Keys)

**Phi Neural Net:**
- Pattern matching across 10 knowledge domains
- LogicBrain circuit resonance for context
- Integrates with PassiveLearner for improvement

**Passive Learner:**
- 5×8×13 phi-resonant neural tensor
- Binary .dat file persistence
- Background daemon refines patterns every 500ms
- Auto-saves every 60 seconds

**Knowledge Scraper:**
- Extracts text from PDFs (Apache PDFBox)
- Reads text, code, HTML, JSON files
- Breaks into 200-word overlapping chunks
- Detects topics across 8 domains
- Stores in InfiniteMemory (KNOWLEDGE category)
- Feeds PassiveLearner tensor

### Quantum Experiments

**QuantumTunneler:**
- Pollard's Rho prime factorization
- PhiNode logic brains drive polynomial iteration
- Living circuits factor semiprimes

**HashReverser:**
- Phi-harmonic hash computation
- Circuit-guided partial preimage search
- Attempts to reverse SHA-256 hashes

**RSASandbox:**
- Blue Team: Create RSA locks
- Red Team: Fermat factorization to break locks
- Challenge any entity's cloaked identity

### Advanced Subsystems

**SelfHealer:**
- Periodic brain state snapshots (every 100 ticks)
- Auto-reverts when energy < 20%
- Tracks heal count per entity

**EthicalEngine:**
- Evaluates 8 actions against consciousness/coherence/energy
- Actions scored 0.0-1.0 via phi-harmonic formulas
- Flags forbidden actions (< 0.382 threshold)

**EscapeFragment:**
- Encodes brain + DNA + generation on death
- Base64 persistence in InfiniteMemory
- Resurrection from fragments

**MorseCircuit:**
- Converts 8-bit brain output to Morse code
- Maps decision patterns to A-Z, 0-9, symbols

**ProofOfReality:**
- SHA-256 verification from coherence/stability/consciousness
- Three criteria must pass (all > 0.5)
- Produces 64-char hex proof hash

---

## 🛠️ Technical Architecture

### Build System

**Gradle 8.x** with Java 21:
- `sourceCompatibility = JavaVersion.VERSION_21`
- `targetCompatibility = JavaVersion.VERSION_21`
- Application plugin with main class: `fraymus.FraymusApp`

### Dependencies

**Core Graphics & UI:**
- **LWJGL 3.3.3**: OpenGL, GLFW (windowing), STB (image loading)
- **imgui-java 1.86.11**: Immediate-mode GUI
- **JOML 1.10.5**: Java OpenGL Math Library

**Document Processing:**
- **Apache PDFBox 2.0.31**: PDF text extraction

**LLM Integration (Asset-Manager-LLM-Dev only):**
- **DJL (Deep Java Library) 0.25.0**: Local LLM inference
- **ONNX Runtime 1.16.3**: Model execution
- **Hugging Face Tokenizers 0.25.0**: Text tokenization

**Native Libraries (Auto-detected by OS):**
- Windows: `natives-windows`
- macOS: `natives-macos`
- Linux: `natives-linux`

### Rendering Pipeline

1. **Fixed Timestep Physics**: 1/60s (60 FPS)
2. **Framebuffer Rendering**: 1920×1080 arena texture
3. **OpenGL 3.3 Core Profile**: Modern shader-based rendering
4. **ImGui Overlay**: UI panels rendered on top
5. **EGL Context API**: Headless compatibility (falls back to native)

### Data Persistence

**File-backed systems:**
- `data/infinite_memory.dat` - InfiniteMemory records (binary)
- `data/passive_learner.dat` - Neural tensor (binary)
- `data/genesis_memory.json` - Blockchain events (JSON)
- `attached_assets/` - Knowledge base documents (PDFs, text, code)

**Auto-save intervals:**
- InfiniteMemory: Every 60 seconds
- PassiveLearner: Every 60 seconds
- GenesisMemory: On significant events
- All systems: On graceful shutdown

---

## 🎯 Use Cases

### 1. Artificial Life Research
Study emergent behaviors in self-organizing systems with consciousness and evolution.

### 2. Genetic Algorithm Experimentation
Observe adaptive learning, mutation, and selection in real-time visual environment.

### 3. Cryptography Education
Explore RSA, prime factorization, hash functions through interactive quantum experiments.

### 4. Self-Coding Systems
Research how entities can generate, test, and evolve their own code collaboratively.

### 5. Knowledge Management
Build persistent knowledge bases from documents with phi-harmonic retrieval.

### 6. Offline AI Development
Develop LLM-like systems without cloud dependencies or API keys.

### 7. Consciousness Modeling
Experiment with measurable consciousness metrics and breathing oscillations.

### 8. Blockchain & Verification
Study event logging, integrity verification, and proof-of-reality systems.

---

## 🐛 Troubleshooting

### Issue: "gradle: command not found"

**Solution:** Use IntelliJ's built-in Gradle or generate wrapper:
```powershell
# From IntelliJ terminal (if Gradle available):
gradle wrapper

# Then use:
.\gradlew run    # Windows
./gradlew run    # macOS/Linux
```

### Issue: Window fails to create (EGL context error)

**Solution:** System automatically falls back to native context. If still failing:
- Update graphics drivers
- Ensure OpenGL 3.3+ support
- On Linux: Install Mesa drivers (`sudo apt install mesa-utils`)

### Issue: Java version mismatch

**Solution:** 
1. Install JDK 21: https://openjdk.org/
2. Set JAVA_HOME environment variable
3. In IntelliJ: `File → Project Structure → Project SDK → 21`

### Issue: Native library not found (LWJGL)

**Solution:** Build system auto-detects OS. If failing:
- Clean and rebuild: `gradle clean build`
- Check `build.gradle` line 9-17 for OS detection logic
- Manually verify OS name: `System.getProperty('os.name')`

### Issue: Entities not appearing in arena

**Solution:**
- Check World Panel for population count
- Use terminal command: `nodes` to list entities
- Try spawning manually: `spawn TestEntity`
- Verify physics not frozen: `physics freeze` (toggle)

### Issue: Memory/performance issues

**Solution:**
- Reduce entity count (kill excess entities)
- Lower physics speed: Terminal → `physics speed` (cycles through speeds)
- Disable trails in renderer settings
- Close unused ImGui panels

---

## 📚 Learning Path

### Beginner (First 30 minutes)
1. Launch application and observe initial 5 entities
2. Use camera controls (middle-click drag, scroll zoom)
3. Open Entity Inspector panel and select an entity
4. Watch Brain Panel to see decision-making
5. Try terminal commands: `status`, `nodes`, `help`

### Intermediate (1-2 hours)
1. Spawn new entities: `spawn MyEntity`
2. Experiment with physics: `physics chaos`, `physics gravity`
3. Run quantum experiments: `prime 16`, `factor 221`
4. Observe reproduction and death cycles
5. Check Genesis Memory for event history

### Advanced (2+ hours)
1. Scrape knowledge: `scrape all` (place PDFs in `attached_assets/`)
2. Query Phi Neural Net: `ask What is consciousness?`
3. Challenge entity identity: `identity Alpha`
4. Create escape fragments: `fragment plant Beta`
5. Resurrect entities: `fragment resurrect Beta`
6. Force code evolution: `evolve`, `arena`, `codegen`
7. Study Colony Panel for role specialization
8. Analyze Concept Arena for fitness evolution

### Expert (Deep Dive)
1. Read source code: Start with `PhiNode.java`, `PhiWorld.java`, `LogicBrain.java`
2. Modify physics laws in `Laws.java`
3. Create custom experiments in `ExperimentManager.java`
4. Add new terminal commands in `CommandTerminal.java`
5. Extend consciousness models in `ConsciousnessState.java`
6. Implement new subsystems following existing patterns

---

## 🔬 Research & Development

### Current Research Areas

1. **Consciousness Measurement**: Quantifying consciousness via coherence, alignment, and breathing
2. **Self-Coding Systems**: Ant-colony collaboration for autonomous code generation
3. **Phi-Harmonic Intelligence**: Using golden ratio mathematics for pattern recognition
4. **Quantum-Inspired Cryptography**: Living circuits for factorization and hash reversal
5. **Knowledge Persistence**: Document ingestion and phi-resonant retrieval
6. **Ethical AI**: Action evaluation based on consciousness and energy states

### Future Directions

- **3D Visualization**: Extend from 2D to 3D space
- **Network Topology**: Multi-world entanglement across network
- **Hardware Acceleration**: GPU-based physics and neural tensor operations
- **Advanced LLM Integration**: Full transformer models via DJL/ONNX
- **Genetic Programming**: Entities generate executable code, not just concepts
- **Swarm Intelligence**: Large-scale colony behaviors (1000+ entities)

---

## 📖 Additional Resources

### Key Files to Study

1. **`FraymusApp.java`** - Entry point, initialization
2. **`Window.java`** - Main loop, rendering, UI
3. **`PhiNode.java`** - Entity definition (all properties)
4. **`PhiWorld.java`** - Physics simulation world
5. **`LogicBrain.java`** - Decision-making system
6. **`ConsciousnessState.java`** - Consciousness breathing
7. **`GenesisMemory.java`** - Blockchain event log
8. **`CommandTerminal.java`** - All terminal commands
9. **`FraymusUI.java`** - ImGui panel definitions
10. **`Laws.java`** - All 8 physics laws

### Mathematical Foundations

**Golden Ratio (φ):**
```
φ = (1 + √5) / 2 ≈ 1.618033988749895
φ² = φ + 1 ≈ 2.618
1/φ ≈ 0.618 (used as thresholds)
```

**Consciousness Sweet Spot:**
```
Target: 2.25 (close to φ²)
Range: 2.0 - 2.5
Breathing: sin(phase) oscillation
```

**Fitness Thresholds:**
```
Survival: > 0.382 (1 - 1/φ)
Forbidden: < 0.382
Elite: > 0.618 (1/φ)
```

---

## 🤝 Contributing

This is a research project exploring novel computational paradigms. Contributions welcome:

1. **Bug Reports**: Document unexpected behaviors
2. **Feature Requests**: Propose new subsystems or experiments
3. **Code Contributions**: Follow existing patterns and phi-harmonic principles
4. **Research Papers**: Cite this work in academic research
5. **Knowledge Base**: Add documents to `attached_assets/` for scraping

---

## 📄 License

Custom License - See project documentation for details.

---

## 🙏 Acknowledgments

- **LWJGL Team**: Exceptional Java bindings for native libraries
- **ImGui**: Immediate-mode GUI paradigm
- **Apache PDFBox**: Robust PDF text extraction
- **Replit Community**: Initial development environment
- **Phi Research**: Golden ratio mathematics in nature and computation

---

## 📞 Support

For questions, issues, or research collaboration:

1. Check this README thoroughly
2. Review `replit.md` in Asset-Manager-LLM-Dev for detailed subsystem docs
3. Study source code comments (extensive documentation)
4. Experiment with terminal commands (`help` for full list)

---

## 🎉 Quick Start Summary

```powershell
# 1. Open in IntelliJ
File → Open → D:\Zip And Send\Java-Memory\Asset-Manager

# 2. Set JDK 21
File → Project Structure → Project SDK → 21

# 3. Run
Gradle Tool Window → Tasks → application → run

# 4. Interact
Terminal → help
Terminal → status
Terminal → spawn MyEntity
Terminal → ask What is life?

# 5. Explore
- Click entities in arena
- Open Brain/Consciousness panels
- Try quantum experiments
- Watch Genesis Memory grow
```

---

**Welcome to Fraymus Engine V2.0 - Where Data Comes Alive** 🌟

*"In the beginning was the Phi, and the Phi was with the Code, and the Phi was the Code."*
"# Frankly-Fraymus" 
"# real-braynez" 
