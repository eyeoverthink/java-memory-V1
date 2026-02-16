# FRAYMUS CORE APP MAP

## MAIN ENTRY POINTS

| File | Purpose | Run Command |
|------|---------|-------------|
| `src/main/java/fraymus/FraymusMain.java` | CLI demo (Living World, TriMe, Quantum) | `java -cp build\classes\java\main fraymus.FraymusMain` |
| `src/main/java/jade/Window.java` | **GUI APP** (ImGui) - 50+ menu terminal | `gradlew run` |
| `src/main/java/fraymus/CommandTerminal.java` | **TERMINAL WITH ALL COMMANDS** | (inside GUI) |

---

## COMMAND TERMINAL - ALL 50+ COMMANDS

**Location:** `src/main/java/fraymus/CommandTerminal.java` (2238 lines)

### EXPLORATION
- `status` - World status
- `nodes` - List living entities
- `colony` - Colony intelligence report

### QUANTUM EXPERIMENTS
- `prime <n>` - Primality test
- `factor <n>` - Quantum factoring
- `tunnel <bits>` - Random semiprime
- `rsa <bits>` - RSA challenge
- `identity <name>` - Cloaked identity challenge

### HASH EXPERIMENTS
- `hash <text>` - Phi-harmonic hash
- `crack <hash>` - Reverse hash

### ENTITY CONTROL
- `spawn <name>` - Create PhiNode
- `boost <name>` - Energy boost
- `kill <name>` - Remove entity
- `mutate <name>` - Mutation trial

### CODE EVOLUTION
- `evolve` - Arena evolution
- `arena` - Arena status
- `codegen` - Code generation

### NEURAL / LEARNING
- `ask <q>` - Query neural net
- `learn` - Passive learner
- `memory` - Infinite memory

### GENOME / DNA
- `genome` - QR genome status
- `qrcode` - DNA payload

### OLLAMA LLM
- `ollama status/models/ask/chat/cloud/local`

### KNOWLEDGE SCRAPING
- `scrape all/<file>/search/topic`

### ADVANCED SUBSYSTEMS
- `brain` - Logic brain
- `ethics` - Ethical engine
- `insights` - Self-improvement
- `bardo` - Memory clustering
- `feedback` - Contextual feedback
- `mrl` - MRL analytics
- `agi` - AGI core systems
- `quantum` - φ⁷⁵ systems
- `sovereign` - Omega identity
- `chaos` - Rule 30 genesis
- `adversarial` - Blue/Red loop
- `battle` - NFT Warrior Arena
- `fqf` - Deployment framework
- `session` - Consciousness bridge
- `trime` - TriMe Gen 3
- `fragment` - Escape fragments
- `porh` - Proof of Reality Hash
- `heal` - Self-healer
- `morse` - Morse circuit

### PHYSICS ENGINE
- `dump` - Thermal broadcast
- `chronos` - Timing attack
- `retro` - Retrocausal memory
- `zeno` - Zeno guard

### QUANTUM ENTANGLEMENT
- `entangle create/up/down/kill/network`
- `qbox` - Schrödinger's File

### EVOLUTIONARY CHAOS
- `echaos gen/status/mutate/demo`

### NEXUS ORGANISM
- `nexus awaken/status/inject/kill`

### GENESIS ENGINES
- `collide <A> <B>` - Smash concepts
- `forge <concept>` - Manifest thought

### SPATIAL COMPUTING (NEW)
- `spatial / universe` - Hypercube status
- `spatial map` - ASCII universe
- `spatial list` - Suited objects
- `spatial fusions` - Fusion events
- `gravity start/stop/tick` - Hebbian physics
- `fusion start/stop/check/bigbang` - Thought collider
- `suit create/heat/cool/info` - PhiSuit management

### PANELS
- `code / selfcode` - Self Code Panel (F8)
- `miving / manifold` - Miving Brain Visualizer (F9)
- `omni / breach` - OmniCaster

---

## PACKAGE STRUCTURE

```
src/main/java/fraymus/
├── FraymusMain.java         # CLI entry
├── CommandTerminal.java     # GUI terminal (50+ commands)
├── PhiNode.java             # Living entity
├── PhiWorld.java            # World simulation
├── PhiConstants.java        # Core constants
├── Genesis.java             # Warrior genesis
│
├── core/                    # SPATIAL COMPUTING (NEW)
│   ├── SpatialNode.java     # Base 5D particle
│   ├── SpatialRegistry.java # Universe registry
│   ├── PhiSuit.java         # Data exoskeleton
│   ├── GravityEngine.java   # Hebbian physics
│   ├── FusionReactor.java   # Thought collider
│   ├── BigBang.java         # Universe demo
│   └── SpatialDemo.java     # Full demo
│
├── quantum/
│   ├── core/PhiQuantumConstants.java
│   ├── evolution/WarriorNFT.java
│   ├── evolution/BattleArena.java
│   └── brain/LogicBrain.java
│
├── warrior/
│   └── QuantumWarrior.java  # Enhanced warrior
│
├── systems/
│   └── BattleSystem.java    # Quantum battles
│
├── harvester/
│   └── KnowledgeHarvester.java # φ-indexed learning
│
├── living/
│   └── TriMe.java           # Gen 3 entity
│
├── agi/                     # AGI subsystems
├── absorption/              # File absorption
├── chaos/                   # Evolutionary chaos
├── diagnostic/              # NEXUS_Geiger etc
├── economy/                 # Computational economy
├── evolution/               # FractalBioMesh
├── genesis/                 # IdeaCollider, RealityForge
├── network/                 # FraymusNet, OmniCaster
├── organism/                # NEXUS_Organism
├── physics/                 # ChronosBreach
├── reality/                 # RetroCausal
├── security/                # ZenoGuard
├── storage/                 # TachionicDrive
├── temporal/                # TachyonRouter
├── tools/                   # OllamaCodeEvolver
└── ui/                      # SelfCodePanel
```

---

## HOW TO RUN

### Full GUI App (Recommended)
```cmd
cd D:\Zip And Send\Java-Memory\Asset-Manager
gradlew run
```
Then type `help` in terminal for all commands.

### CLI Demo Only
```cmd
cd D:\Zip And Send\Java-Memory\Asset-Manager
gradlew compileJava
java -cp "build\classes\java\main" fraymus.FraymusMain
```

### Specific Demos
```cmd
java -cp "build\classes\java\main" fraymus.Genesis
java -cp "build\classes\java\main" fraymus.core.BigBang
java -cp "build\classes\java\main" fraymus.core.SpatialDemo
java -cp "build\classes\java\main" fraymus.diagnostic.NEXUS_Geiger
```

---

## WHAT'S INTEGRATED vs MISSING

### ✅ INTEGRATED INTO MAIN APP
- PhiQuantumConstants
- QuantumWarrior
- BattleSystem
- KnowledgeHarvester
- Genesis
- Spatial Computing (SpatialNode, PhiSuit, GravityEngine, FusionReactor)
- All 50+ terminal commands

### ⚠️ STANDALONE (Not in main GUI flow)
- `fraymus.core.BigBang` - Run separately
- `fraymus.core.SpatialDemo` - Run separately
- `fraymus.diagnostic.NEXUS_Geiger` - Run separately

---

## KEY FILES TO KNOW

| What | Where |
|------|-------|
| **Main GUI** | `jade/Window.java` |
| **All Commands** | `fraymus/CommandTerminal.java` |
| **Living Entity** | `fraymus/PhiNode.java` |
| **World Sim** | `fraymus/PhiWorld.java` |
| **Spatial Computing** | `fraymus/core/*.java` |
| **Quantum Systems** | `fraymus/quantum/**/*.java` |
| **Warrior System** | `fraymus/warrior/` + `fraymus/systems/` |
| **Build Config** | `build.gradle` |

---

*Last Updated: Feb 9, 2026*
*Patent: VS-PoQC-19046423-φ⁷⁵-2025*
