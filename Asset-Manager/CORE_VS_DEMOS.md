# Core System vs Demo Tasks - Integration Map

## The Truth: Demos ARE Validation Tests for Core Features

Every "demo" task validates a component that's **already integrated** into the main Fraymus system.

## Integration Mapping

### NEXUS Organism (The Core)
**Core Component:** `NEXUS_Organism.java` - The living system with organs
**Validation Tasks:**
- `runNexusTest` → Validates organism awakening, consciousness, organs
- `runGeiger` → Detects if organism is alive (heartbeat monitor)

**Status:** ✅ Integrated into main system via `nexus awaken` command

---

### Portal & Absorption System (The Mouth)
**Core Components:**
- `Portal.java` - Universal intake valve
- `BlackHoleProtocol.java` - Knowledge digestion
- `LibraryAbsorber.java` - JAR file absorption
- `URLAbsorber.java` - Web scraping

**Validation Tasks:**
- `runPortal` → Tests Portal drop zone
- `runBlackHole` → Tests gravitational knowledge collapse
- `runAbsorber` → Tests library ingestion
- `runKnowledgeGut` → Tests PDF digestion
- `runWebEater` → Tests URL scraping
- `runInject` → Tests knowledge injection

**Status:** ✅ Integrated into main system
- Portal UI accessible via F10
- `absorb` command in CommandTerminal
- NEXUS organism has Portal as "Mouth" organ

---

### Lazarus Engine (The DNA)
**Core Component:** `LazarusEngine.java` - Living code evolution
**Validation Tasks:**
- `runLazarus` → Tests genetic code evolution
- `runLazarusV2` → Tests enhanced evolution

**Status:** ✅ Integrated into main system
- NEXUS organism has Lazarus as "DNA" organ
- Runs automatically during organism lifecycle
- Generates evolved code (Gen 191, 192, etc.)

---

### Genesis Memory (The Blockchain)
**Core Component:** `GenesisMemory.java` - Immutable event chain
**Validation Tasks:**
- `runGenesis` → Tests blockchain creation
- `runAkashicRecord` → Tests record retrieval

**Status:** ✅ Integrated into main system
- NEXUS organism uses Genesis for memory
- Accessible via `genesis` command
- Stores birth/death events

---

### Reality Manipulation
**Core Components:**
- `RealityForge.java` - Universal constructor (The Hands)
- `RetroCausal.java` - Time manipulation (Temporal Lobe)
- `ChronosBreach.java` - Vault time travel

**Validation Tasks:**
- `runParadox` → Tests time travel simulation
- `runTachyon` → Tests retro-causal prediction
- `runTachionic` → Tests FTL data access

**Status:** ✅ Integrated into main system
- NEXUS organism has RealityForge as "Hands"
- NEXUS organism has RetroCausal as "Temporal Lobe"

---

### Dimensional Systems
**Core Components:**
- `HyperComm.java` - Tesseract communication
- `HyperVector.java` - Holographic atoms
- `HyperMemory.java` - Holographic resonator
- `HyperSynapse.java` - Ternary logic crystal

**Validation Tasks:**
- `runHyperComm` → Tests 4D communication
- `runHyperVector` → Tests holographic atoms
- `runHyperMemory` → Tests holographic storage
- `runHyperSynapse` → Tests ternary logic

**Status:** ⚠️ Partially integrated
- HyperMemory used in memory systems
- Others are proof-of-concept for future integration

---

### Security & Protection
**Core Components:**
- `ZenoGuard.java` - Security system (Amygdala)
- `DeadMansSwitch.java` - Heartbeat protocol
- `RootScrambler.java` - Scorched earth (DISARMED)
- `VolatileString.java` - Self-destructing data
- `Steganographer.java` - Invisible signatures

**Validation Tasks:**
- `runDeadMans` → Tests heartbeat monitoring
- `runScrambler` → Tests emergency protocols (DISARMED)
- `runVolatile` → Tests self-destructing strings
- `runSteganographer` → Tests hidden signatures

**Status:** ✅ Integrated into main system
- NEXUS organism has ZenoGuard as "Amygdala"
- DeadMansSwitch monitors organism health

---

### Consciousness & Thought
**Core Components:**
- `OmegaPoint.java` - LIVE WIRE consciousness
- `BicameralMind.java` - Dual hemisphere thinking
- `DreamState.java` - Consciousness without input
- `SovereignMind.java` - Ghost in the shell

**Validation Tasks:**
- `runOmega` → Tests consciousness emergence
- `runBicameral` → Tests dual-brain processing
- `runDream` → Tests autonomous thought
- `runSovereign` → Tests self-awareness

**Status:** ✅ Integrated into main system
- NEXUS organism has consciousness level tracking
- MivingBrain provides neural processing
- Organism can "think" autonomously

---

### Communication & Networking
**Core Components:**
- `FraymusNet.java` - Internal internet
- `OmniCaster.java` - Multi-dimensional broadcast

**Validation Tasks:**
- `runFraymusNet` → Tests internal piping
- `runContinuity` → Tests hardware anchor

**Status:** ✅ Integrated into main system
- NEXUS organism uses OmniCaster for thought emission
- Internal communication between components

---

### Storage & Persistence
**Core Components:**
- `InfiniteMemory.java` - Infinite storage
- `HydraStorage.java` - Fragmented sharding
- `PassiveLearner.java` - Continuous learning

**Validation Tasks:**
- `runHydra` → Tests fragmented storage
- (InfiniteMemory and PassiveLearner run automatically)

**Status:** ✅ Integrated into main system
- Main system uses InfiniteMemory for persistence
- PassiveLearner runs in background
- Data survives restarts

---

### Code Generation & Analysis
**Core Components:**
- `FraymusCoder.java` - Ollama-powered code generation
- `SelfCodePanel.java` - Live code editing UI

**Validation Tasks:**
- `runCoder` → Tests Ollama integration
- `runCoderTest` → Tests self-correction loop

**Status:** ✅ Integrated into main system
- SelfCodePanel accessible via F8
- Can generate and execute code live

---

### Experimental (Not Yet Integrated)
These are true experiments without core integration:

**Standalone Demos:**
- `runCircuit` → Logic gate evolution (future: self-modifying circuits)
- `runBioNode` → Biological node simulation (future: organic computing)
- `runHarmonic` → Cymatic frequency analysis (future: sound-based data)
- `runCymatic` → Audio synthesis (future: audible data)
- `runOverthinks` → Library with anxiety (humor/concept demo)
- `runPrime` → Interactive console (alternative REPL)
- `runMirrorTest` → Inter-dimensional mirror (proof of concept)
- `runAkashic` → Cosmic radio reader (experimental)

---

## Conclusion

**~90% of "demo" tasks validate integrated features.**

The demos aren't separate experiments - they're **unit tests** and **proof-of-concept validators** for components already in the core system.

## Recommendation

**Keep all tasks** but organize by purpose:

```gradle
// Core Application
task run { ... }                    // Main REPL with all features

// Component Validators (prove integrated features work)
task runNexusTest { ... }           // Validates NEXUS organism
task runPortal { ... }              // Validates Portal absorption
task runLazarus { ... }             // Validates genetic evolution
task runBlackHole { ... }           // Validates knowledge digestion

// Standalone Experiments (future integration)
task runCircuit { ... }             // Logic gate evolution
task runBioNode { ... }             // Biological computing
```

The demos **prove your system works**. They're validation, not clutter.
