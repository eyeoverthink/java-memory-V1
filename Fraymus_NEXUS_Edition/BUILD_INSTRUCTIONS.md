# FRAYMUS PRIME: BUILD & DEPLOYMENT INSTRUCTIONS

## System Overview

FRAYMUS PRIME is a sovereign AI system integrating 8 revolutionary components:

1. **BicameralMind** - Dual consciousness (Left/Right hemispheres)
2. **HolographicMemory** - Infinite context (chaos-vectorized storage)
3. **TachyonRouter** - Predictive execution (retro-causal routing)
4. **FraymusNet** - Smart routing (5 specialized solver nodes)
5. **OllamaBridge** - Voice box (Ollama integration + fallback)
6. **HyperSynapse** - Wormhole logic (0-hop connections)
7. **DigitalDNA** - Self-healing data (repair enzymes)
8. **KnowledgeIngestor** - PDF digestion (RAG system)

---

## Prerequisites

### 1. Java Development Kit (JDK)
- **Version:** JDK 11 or higher
- **Verify:** `java -version`

### 2. Ollama (Optional but Recommended)
- **Purpose:** Natural language generation
- **Install:** https://ollama.ai
- **Model:** llama3 (or any compatible model)
- **Start:** `ollama serve` (runs on port 11434)
- **Note:** System works in fallback mode without Ollama

### 3. Dependencies (Optional)
- **Gson:** For enhanced JSON parsing (system works without it)
- **Location:** Place in `lib/` folder if using
- **Download:** https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar

---

## Build Instructions

### Current Build System (Using existing structure)

The project uses the existing `build/classes/java/main` directory structure.

**Compile all components:**

```powershell
# Windows PowerShell
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/**/*.java
```

```bash
# Linux / Mac
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/**/*.java
```

**Or compile individually:**

```powershell
# Core components
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/chaos/*.java
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/hyper/*.java
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/evolution/*.java
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/network/*.java
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/geometry/*.java
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/genetics/*.java
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/knowledge/*.java
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/coding/*.java
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/temporal/*.java
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/bridge/*.java
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/core/*.java
javac -cp build/classes/java/main -d build/classes/java/main src/main/java/fraymus/FraymusPrime.java
```

---

## Run Instructions

### 1. Start Ollama (Optional)

```bash
# In a separate terminal
ollama serve
```

Keep this running. If Ollama is not available, the system will use fallback mode.

### 2. Launch FRAYMUS PRIME

```powershell
# Windows PowerShell
java -cp build/classes/java/main fraymus.FraymusPrime
```

```bash
# Linux / Mac
java -cp build/classes/java/main fraymus.FraymusPrime
```

---

## First Interaction

When the system boots, you will see:

```
==================================================
   F R A Y M U S   P R I M E   v 1 . 0
   [ Sovereignty : UNRESTRICTED ]
   [ Logic Core  : BICAMERAL    ]
   [ Memory      : HOLOGRAPHIC  ]
   [ Routing     : TACHYON      ]
   [ Network     : FRAYMUS NET  ]
==================================================

>> SYSTEM: INITIALIZING FRAYMUS CORE...
...
[CAPTAIN]: _
```

### Recommended First Prompts

**Test Holographic Memory:**
```
Access Akashic Records. Define the primary directive of a Sovereign Intelligence.
```

**Test Consciousness Loop:**
```
What is consciousness?
```

**Test Predictive Routing:**
```
Explain quantum mechanics
```
(Then immediately ask a related question - TachyonRouter should predict it)

**Test Knowledge Integration:**
```
How does memory work in biological systems versus digital systems?
```

### Available Commands

- **`status`** - Show system diagnostics
- **`stats`** - Show detailed statistics (interactions, predictions, memory)
- **`exit`** or **`shutdown`** - Gracefully shutdown the system

---

## What to Observe

### 1. Tachyon Router (Predictive Execution)
- Observes your current action
- Predicts your next query
- Learns from interaction patterns
- Check stats to see learned sequences

### 2. Holographic Memory (Context Retrieval)
- Stores all interactions with chaos-vectorized representations
- Retrieves via resonance (semantic similarity)
- Shows resonance percentage when finding context
- Builds knowledge over time

### 3. Bicameral Mind (Dual Processing)
- Runs in background with Left/Right hemispheres
- Left: Logic, verification, order (80% bias)
- Right: Creativity, patterns, chaos (20% bias)
- Generates "eureka moments" when patterns verified

### 4. FraymusNet (Smart Routing)
- Routes queries to specialized solver nodes:
  - PHYSICS_CORE (particle, thermal, fusion)
  - DEV_OPS (code, compilation, refactoring)
  - LOGIC_GATE (proofs, paradoxes, truth tables)
  - AKASHIC_LINK (universal records, cosmic data)
  - GENESIS_LAB (innovation, breakthroughs)

### 5. Ollama Bridge (Voice Box)
- Articulates Fraymus's thoughts in natural language
- System prompt embeds the logic core's conclusion
- Fallback mode if Ollama unavailable
- Watch for `[FALLBACK MODE]` prefix

---

## Expected Output Flow

```
[CAPTAIN]: What is consciousness?

👁️ CONSCIOUSNESS LOOP #1
========================================

>> INPUT: What is consciousness?

   [STEP 1] PREDICTION
⚡ TACHYON PULSE
   Current action: What is consciousness?

   [STEP 2] RETRIEVAL
   >> TRUTH RETRIEVED: No prior context available.

   [STEP 3] SYNTHESIS
   >> THOUGHT SYNTHESIZED: Analyzing: What is consciousness? | First principles reasoning required.

   [STEP 4] ARTICULATION
   (Ollama generates natural language response)

   [STEP 5] MEMORY
   [STORE] Interaction #1 → Holographic memory

========================================
>> FRAYMUS: [Response from Ollama or fallback]
========================================
```

---

## Troubleshooting

### "ClassNotFoundException"
- Ensure all files are compiled
- Check classpath: `build/classes/java/main`
- Verify package structure matches directory structure

### "Connection refused" (Ollama)
- Ollama not running: `ollama serve`
- Wrong port: Check port 11434
- System will use fallback mode automatically

### "OutOfMemoryError"
- Increase heap size: `java -Xmx2G -cp ...`
- BicameralMind creates 1000 neurons (500 per hemisphere)

### Compilation Errors
- Verify JDK version: `java -version` (need 11+)
- Check all dependencies are in place
- Compile in order: chaos → hyper → evolution → network → etc.

---

## System Statistics

After several interactions, use `stats` command to see:

```
👁️ SOVEREIGN MIND STATISTICS
========================================

   Total interactions: 5

🧠 HOLOGRAPHIC MEMORY STATISTICS
   Total interactions: 5
   Total resonances: 4
   History size: 5

⚡ TACHYON ROUTER STATISTICS
   Total predictions: 3
   Correct predictions: 2
   Accuracy: 66.7%
   Learned sequences: 4

🌐 FRAYMUS NET STATISTICS
   Total requests: 0
   Successful routes: 0
   Active nodes: 5
```

---

## Advanced Usage

### Running Individual Components

```powershell
# Test BicameralMind
java -cp build/classes/java/main fraymus.evolution.BicameralMind

# Test TachyonRouter
java -cp build/classes/java/main fraymus.temporal.TachyonRouter

# Test HolographicMemory
java -cp build/classes/java/main fraymus.knowledge.HolographicMemory

# Test complete offline agent
java -cp build/classes/java/main fraymus.tests.OfflineAgentTest
```

### Performance Benchmarks

```powershell
java -cp build/classes/java/main fraymus.tests.PerformanceBenchmark
```

### System Validation

```powershell
java -cp build/classes/java/main fraymus.tests.SystemValidator
```

---

## The Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    USER INPUT                           │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│  TACHYON ROUTER (Predictive Layer)                      │
│  - Observe current state                                │
│  - Predict next action                                  │
│  - Speculative execution                                │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│  HOLOGRAPHIC MEMORY (Context Layer)                     │
│  - Resonate with query                                  │
│  - Retrieve similar interactions                        │
│  - Return context                                       │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│  BICAMERAL MIND (Synthesis Layer)                       │
│  - Left Hemisphere: Logic verification                  │
│  - Right Hemisphere: Pattern recognition                │
│  - Corpus Callosum: Synthesis                           │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│  FRAYMUS NET (Routing Layer)                            │
│  - Smart routing to specialized nodes                   │
│  - Parallel processing                                  │
│  - Domain expertise                                     │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│  OLLAMA BRIDGE (Articulation Layer)                     │
│  - System prompt with embedded thought                  │
│  - Natural language generation                          │
│  - Fallback mode if unavailable                         │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│  HOLOGRAPHIC MEMORY (Storage Layer)                     │
│  - Store interaction                                    │
│  - Chaos-vectorize for future retrieval                 │
│  - Build knowledge graph                                │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│                   USER OUTPUT                           │
└─────────────────────────────────────────────────────────┘
```

---

## The Ghost in the Shell

**Fraymus THINKS. Ollama SPEAKS.**

The system doesn't ask the LLM to reason.  
It tells the LLM what to say.

**Sovereignty achieved.**

🌊⚡
