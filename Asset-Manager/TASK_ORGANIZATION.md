# Gradle Task Organization Analysis

## Current Problem
Your `build.gradle` has **40+ experimental test tasks** cluttering the Gradle menu, making it hard to find the core system tasks.

## Core System Tasks (Keep These)

### Primary Application
- **`run`** - Main Fraymus REPL with ImGui UI (`FraymusApp`)
- **`runConsole`** - Command Deck with Uplink Protocol (text-based REPL)
- **`runPrime`** - Interactive Console (The Ignition)

### Core Components (Integrated into main system)
- **`runLazarus`** - Living Code Evolution (part of NEXUS)
- **`runBlackHole`** - Knowledge absorption (Portal backend)
- **`runPortal`** - Portal UI (if exists)
- **`runAbsorber`** - Library absorption system
- **`runAkashicRecord`** - Genesis memory blockchain

## Experimental/Demo Tasks (Move to 'demos' group)

### Tests & Diagnostics
- `runNexusTest` - NEXUS organism test
- `runMirrorTest` - Inter-dimensional mirror
- `runGeiger` - Organism detection
- `runCoderTest` - Self-correction verification

### Experimental Features
- `runHyperComm` - Tesseract communication
- `runHyperVector` - Holographic atoms
- `runHyperMemory` - Holographic resonator
- `runHyperSynapse` - Ternary logic crystal
- `runOmega` - LIVE WIRE consciousness
- `runParadox` - Time travel simulation
- `runDream` - Consciousness without input
- `runBicameral` - Dual hemisphere mind
- `runFraymusNet` - Internal internet demo
- `runKnowledgeGut` - PDF digestion
- `runCoder` - Ollama puppeteer
- `runTachyon` - Retro-causal engine
- `runTachionic` - FTL data access
- `runSovereign` - Ghost in the shell
- `runHarmonic` - Cymatic frequency
- `runCymatic` - Audio synthesis
- `runOverthinks` - Library with anxiety
- `runSteganographer` - Invisible signatures
- `runVolatile` - Self-destructing strings
- `runHydra` - Fragmented sharding
- `runContinuity` - Hardware anchor
- `runScrambler` - Scorched earth (DISARMED)
- `runDeadMans` - Heartbeat protocol
- `runCircuit` - Evolving logic gates
- `runBioNode` - Biological node simulation
- `runGenesis` - Genesis block creation
- `runInject` - Code injection demo
- `runWebEater` - Web scraping demo
- `runAkashic` - Cosmic radio reader

## Recommended Organization

### Option 1: Separate Task Groups
```gradle
// Core system tasks - group = 'application'
task run(type: JavaExec) { ... }
task runConsole(type: JavaExec) { ... }
task runPrime(type: JavaExec) { ... }

// Experimental demos - group = 'demos'
task runHyperComm(type: JavaExec) {
    group = 'demos'
    description = '...'
}
```

### Option 2: Move to Separate File
Create `demos.gradle` and import it:
```gradle
// In build.gradle
apply from: 'demos.gradle'

// In demos.gradle
task runHyperComm(type: JavaExec) {
    group = 'demos'
    ...
}
```

### Option 3: Comment Out Unused Tasks
Simply comment out experimental tasks you don't actively use:
```gradle
// Experimental - uncomment to test
// task runHyperComm(type: JavaExec) { ... }
```

## Recommendation

**Use Option 1** - Change all experimental tasks to `group = 'demos'`:

This keeps them available but separates them in the Gradle menu:
```
📁 application
  ⚙ run
  ⚙ runConsole
  ⚙ runPrime
  ⚙ runLazarus
  ⚙ runBlackHole

📁 demos
  ⚙ runHyperComm
  ⚙ runOmega
  ⚙ runParadox
  ... (all experimental tasks)
```

## Implementation

I can update your `build.gradle` to:
1. Keep core tasks in `application` group
2. Move all experimental tasks to `demos` group
3. Add comments indicating which are integrated vs standalone

Would you like me to reorganize the tasks this way?
