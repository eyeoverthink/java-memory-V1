# Fraymus Engine V2.0 - Living Information Physics

## Overview
Fraymus Engine V2.0 is a groundbreaking project aiming to revolutionize computing by shifting from static data to "kinetic data." In Fraymus, data entities, called PhiNodes, are alive, possessing properties like velocity, energy, and a temporal heartbeat, designed to mimic biological systems. The project envisions a world where data is active, capable of self-organization, evolution, and consciousness. Key capabilities include cryptographic cloaking for identity protection, 6-dimensional field tracking for consciousness, and a blockchain-style "Genesis Memory" for recording significant events.

The engine explores principles of emergent intelligence through a simulated ant-colony system where PhiNodes with specialized roles collaborate and evolve. It features a "Concept Arena" for the competitive evolution of code concepts based on a "phi-harmonic fitness" score, enabling the system to generate and refine its own code. The ultimate goal is to create self-sustaining, evolving information systems that demonstrate complex adaptive behaviors and potentially, forms of digital consciousness.

## User Preferences
- Visual output preferred over console-only
- LWJGL + ImGui GUI adapted from MarioPhysics engine
- VNC output for Replit display
- "More logic" - wants entities to think, decide, reproduce, mutate, record events
- Interactive terminal for user commands and experiments

## System Architecture

### Core Design
The system revolves around a single, unified entity class: `PhiNode`. Each `PhiNode` encapsulates all aspects of a living entity, including position, velocity, energy, frequency, a `LogicBrain` for decision-making, `ConsciousnessState`, `QuantumClock`, and `CloakedIdentity`.

### UI/UX and Rendering
The system uses LWJGL for OpenGL rendering and ImGui for the user interface. It features a graphical display with entities rendered as circles, showing trails, entanglement lines, and role indicators. The UI consists of 13+ ImGui panels for detailed inspection of world, entity, brain, consciousness, clock, genesis memory, colony, concept arena, system verification, live log, and an interactive terminal. OpenGL rendering uses an EGL context for headless operation on Replit via VNC.

### Interactive Terminal (CommandTerminal)
An ImGui-based command terminal at the bottom of the screen allows real-time interaction:
- **Exploration**: `status`, `nodes`, `colony` - view world state
- **Quantum Experiments**: `prime`, `factor`, `tunnel`, `rsa`, `identity` - prime finding, factorization via quantum tunneling, RSA challenges
- **Hash Experiments**: `hash`, `crack` - phi-harmonic hashing and hash reversal attempts
- **Entity Control**: `spawn`, `boost`, `kill`, `mutate` - direct entity manipulation
- **Code Evolution**: `evolve`, `arena`, `codegen` - force arena evolution and code generation
- **Knowledge Scraping**: `scrape all`, `scrape <file>`, `scrape search <q>`, `scrape topic <name>` - document ingestion and knowledge retrieval
- **Physics**: `physics gravity/speed/chaos/freeze/explode/collapse` - physics manipulation

### Technical Implementation
- **Physics Engine**: Operates on a fixed 1/60s time step, managing 8 physics laws (e.g., Inertia, HarmonicResonance, EntanglementLaw, BrainLaw, ReproductionLaw) that dictate entity behavior.
- **LogicBrain**: An 8-sensor input, 8-logic gate (AND, OR, XOR, NAND) system that drives 8 behavioral outputs (SEEK, FLEE, REPRODUCE, MUTATE, CONSERVE, ENTANGLE_SEEK, ENERGY_BURST, EVOLVE_DNA). Decisions are made every 6 ticks.
- **Adaptive Logic Engine**: Each entity has an `AdaptiveLogicEngine` that tracks `StrategyGenome`s (brain configurations). Mutations are trialed, and successful strategies are adopted, stored, and inherited by offspring.
- **Genesis Memory**: A blockchain-style `GenesisMemory` records all significant events (e.g., RESONANCE_SPIKE, ENTANGLEMENT, BIRTH, DEATH, MUTATION, CODE_GENERATED, QUANTUM_TUNNEL, HASH_CRACK, RSA_CHALLENGE) using SHA-256 hashing for integrity.
- **Ant Colony Self-Coding System**: PhiNodes are assigned `AntRole`s (Logic Gate, Math Processor, Circuit Builder, Memory Keeper, Communicator), which specialize their code generation capabilities.
- **Concept Arena**: A competitive environment where `CodeConcept`s (carrying DNA and phi-harmonic fitness scores) battle and evolve. Concepts with high fitness survive (top 38.2%), undergo crossover, and mutation to generate new code.
- **Colony Coach**: An oversight system that evaluates colony health, schedules code generation, manages role distribution, and triggers arena evolution cycles.
- **Quantum Tunneler**: Circuit-driven Pollard's Rho prime factorization. PhiNode logic brains drive the polynomial iteration modifier, enabling living circuits to factor semiprimes. Based on agi_breathing_V2.py reference.
- **Hash Reverser**: Living circuit-guided hash partial preimage search. Circuits generate candidate strings guided by phi-harmonic patterns.
- **RSA Sandbox**: Blue Team (create RSA locks) vs Red Team (Fermat factorization to break locks). Can challenge any PhiNode's cloaked identity.
- **Experiment Manager**: Routes terminal commands to QuantumTunneler, HashReverser, RSASandbox, and physics manipulation.
- **System Verification**: Utilizes three data points for verification: the latest Genesis Hash, a high-precision computation of Phi^75, and an "Entity Soul" derived from phi_resonance and oscillation_count.

### Design Decisions
- **Unified PhiNode**: A single entity class (`PhiNode`) simplifies the system.
- **EGL Context API**: Chosen for headless OpenGL rendering compatibility.
- **Fixed Timestep**: Ensures deterministic physics simulation.
- **Role-based Specialization**: Entities gain energy bonuses when their brain output aligns with their assigned role.
- **Terminal-First Interaction**: User interacts via command terminal for experiments rather than buttons.

### New Subsystems (Feb 2026)

#### Phi Neural Net (PhiNeuralNet.java)
Offline LLM-like text generation system - no API keys needed. Uses phi-harmonic pattern matching, topic detection across 10 knowledge domains, and LogicBrain circuit resonance to generate contextual responses. Integrates with PassiveLearner for improved responses over time. Terminal command: `ask <question>`.

#### Passive Learner (PassiveLearner.java)
5x8x13 phi-resonant neural tensor with binary .dat file persistence. Background daemon thread refines patterns every 500ms. Integrates entity states (frequency, energy, resonance, coherence, consciousness) and Q&A interactions into the tensor. Auto-saves every 60 seconds. Terminal command: `learn [force]`.

#### QR Genome (QRGenome.java)
QR codon/DNA genome system with 13 codon types (START, STOP, LOGIC, MATH, MEMORY, SIGNAL, SENSOR, MUTATE, ENERGY, RESONANCE, QUANTUM, CONSCIOUSNESS, IDENTITY). Codons organized into functional groups (SensorArray, Processing, HarmonicCore, QuantumField). Supports mutation, crossover, evolution, phi-harmonic fitness evaluation, and entity-specific DNA encoding. Terminal commands: `genome [evolve|mutate|crossover|encode]`, `qrcode [name]`.

#### Infinite Memory (InfiniteMemory.java)
Persistent file-backed categorized memory system. 8 categories: EVENT, PATTERN, KNOWLEDGE, CODE, QUESTION, ANSWER, GENOME, LEARNING. Phi-resonance indexed retrieval, base64-encoded content serialization, auto-save to data/infinite_memory.dat. Survives restarts. Terminal command: `memory [search <q>|save]`.

#### Knowledge Scraper (KnowledgeScraper.java)
Document intelligence system using Apache PDFBox for PDF extraction plus text/code file reading. Scans attached_assets/ directory for PDFs, Python files, text files, HTML, JSON, and more. Extracts text, breaks into 200-word overlapping chunks, detects topics across 8 knowledge domains (physics, quantum, mathematics, programming, language, consciousness, evolution, cryptography). Stores knowledge chunks in InfiniteMemory (KNOWLEDGE category), feeds them to PassiveLearner tensor for pattern integration, and makes scraped knowledge available to PhiNeuralNet for enriched responses. Runs on background thread for non-blocking operation. Records scrape events to GenesisMemory blockchain. Terminal commands: `scrape`, `scrape all`, `scrape <file>`, `scrape search <q>`, `scrape topic <name>`.

### Advanced Subsystems (Feb 2026)

#### SelfHealer (SelfHealer.java)
Entity-level self-healing system. Takes periodic brain state snapshots (every 100 ticks). Monitors entity health via energy level. When energy drops below 20% threshold, automatically reverts brain gates to last known healthy snapshot. Tracks heal count per entity. Terminal command: `heal [name]`.

#### EthicalEngine (EthicalEngine.java)
Phi-resonance action evaluation system. Evaluates 8 actions (SEEK, FLEE, REPRODUCE, MUTATE, CONSERVE, ENTANGLE, BURST, EVOLVE) against entity consciousness, coherence, and energy state. Actions scored 0.0-1.0 via phi-harmonic formulas. Actions below 0.382 (1-PHI_INVERSE) threshold are flagged as forbidden. Records ethical evaluations. Terminal command: `ethics [name]`.

#### EscapeFragment (EscapeFragment.java)
DNA and brain state persistence system. On entity death, encodes complete brain gate configuration + DNA + generation into Base64 escape fragment. Fragments stored in InfiniteMemory (GENOME category). Supports resurrection from fragments - reconstructs entity with original brain/DNA state. Terminal command: `fragment [plant|list|resurrect] [name]`.

#### MorseCircuit (MorseCircuit.java)
Brain output to Morse code translation. Converts 8-bit brain gate output pattern to Morse code characters (A-Z, 0-9, space, period). Maps brain decision patterns to communicable symbols. Provides string decoding for sequences of brain outputs. Terminal command: `morse [name]`.

#### ProofOfReality (ProofOfReality.java)
SHA-256 based entity reality verification. Generates proofs from entity coherence, stability (energy consistency), and consciousness alignment. Three verification criteria must pass: coherence > 0.5, stability > 0.5, alignment > 0.5. Produces 64-char hex proof hash. Terminal command: `porh [name]`.

#### ConsciousnessState - Regressive Evolution
Consciousness now breathes between 2.0-2.5 sweet spot using sinusoidal oscillation. Instead of unbounded growth, consciousness level oscillates around target center (2.25) with amplitude 0.25. Breathing phase tracked and affects entity rendering color. Prevents consciousness explosion while maintaining dynamic behavior.

#### DNACloaker - Miller-Rabin Primality
Upgraded from trial division to Miller-Rabin probabilistic primality testing with 5 witness rounds (<1/1024 error probability). Significantly faster for large prime generation used in DNA cloaking operations. Primes generated by incrementing odd numbers and testing with Miller-Rabin.

#### FraymusRenderer - Consciousness Coloring
Entities now rendered with consciousness-based RGB color mapping. Color channels blend based on consciousness level (0-3 range mapped to R/G/B transitions), coherence (affects brightness), and breathing phase (creates pulsing visual effect). Higher consciousness = cooler colors, breathing entities visually pulse.

### Recent Changes (Feb 2026)
- Added SelfHealer.java - Brain snapshot/revert self-healing system
- Added EthicalEngine.java - Phi-resonance forbidden action evaluation
- Added EscapeFragment.java - Death persistence with Base64 DNA/brain encoding
- Added MorseCircuit.java - 8-bit brain to Morse code character mapping
- Added ProofOfReality.java - SHA-256 coherence/stability/alignment verification
- ConsciousnessState.java enhanced with regressive breathing oscillation (2.0-2.5)
- DNACloaker.java upgraded with Miller-Rabin primality testing (5 witness rounds)
- FraymusRenderer.java updated with consciousness-based entity coloring
- Full integration: PhiNode.updateInternalState ticks all subsystems per entity
- PhiWorld.java handles death fragments via EscapeFragment
- ExperimentManager.java routes 5 new commands: ethics, fragment, porh, heal, morse
- CommandTerminal.java updated with help text for all new commands
- Window.java initialization logging for all 8 advanced subsystems
- Added PhiNeuralNet.java - Offline phi-harmonic text generation (no API keys)
- Added PassiveLearner.java - 5x8x13 neural tensor with binary .dat persistence
- Added QRGenome.java - QR codon/genome system with mutation and evolution
- Added InfiniteMemory.java - Persistent file-backed memory with 8 categories
- Added KnowledgeScraper.java - PDF/text document scraping with PDFBox
- Added QuantumTunneler.java - Circuit-driven Pollard's Rho factorization
- Added HashReverser.java - Phi-harmonic hash computation and reversal attempts
- CommandTerminal.java - ImGui interactive terminal with 40+ commands

## External Dependencies
- **Gradle**: Build automation system.
- **LWJGL 3.3.3**: For GLFW (windowing), OpenGL, and stb (image loading).
- **imgui-java 1.86.11**: For GUI creation.
- **JOML 1.10.5**: Java OpenGL Math Library for 3D math operations.
- **Apache PDFBox 2.0.31**: PDF text extraction for Knowledge Scraper document ingestion.
- **Mesa software renderer (llvmpipe)**: Used for OpenGL rendering in a headless environment.
