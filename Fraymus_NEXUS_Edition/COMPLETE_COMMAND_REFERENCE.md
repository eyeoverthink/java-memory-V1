# FRAYMUS NEXUS - COMPLETE COMMAND REFERENCE

## 📋 FULL MENU & EXPECTATIONS

This document lists **every command** available in the Fraymus system, what it does, and what to expect.

---

## 🕳️ ABSORPTION (Black Hole Protocol)

Universal library integration system. Absorbs any Java library and makes it queryable.

### Commands:

#### `absorb package <name>`
**Purpose:** Absorb a Java package into the system  
**Example:** `absorb package java.util`  
**Expected Behavior:**
- Scans all classes in the package
- Extracts methods, fields, and functionality
- Creates semantic knowledge blocks
- Stores in AkashicRecord
- Makes skills queryable via natural language
- Output: "ABSORPTION COMPLETE" with statistics

#### `absorb jar <path>`
**Purpose:** Absorb an entire JAR file  
**Example:** `absorb jar /path/to/library.jar`  
**Expected Behavior:**
- Opens JAR file
- Scans all .class files
- Extracts skills from each class
- Stores in AkashicRecord
- Output: Classes absorbed, skills acquired count

#### `absorb url <url>`
**Purpose:** Absorb web content (HTML/JavaScript)  
**Example:** `absorb url https://example.com/api`  
**Expected Behavior:**
- Scrapes URL content
- Extracts function definitions
- Stores web knowledge
- Output: Lines scraped, functions found

#### `query <term>`
**Purpose:** Search absorbed knowledge  
**Example:** `query sqrt`  
**Expected Behavior:**
- Searches all absorbed libraries
- Returns matching methods/skills
- Shows class.method signatures
- Output: List of matching skills

#### `execute <skill> [args...]`
**Purpose:** Execute an absorbed method  
**Example:** `execute sqrt 16`  
**Expected Behavior:**
- Finds skill in registry
- Invokes method with arguments
- Returns result
- Output: Execution result or "SKILL NOT FOUND"

#### `absorber stats`
**Purpose:** Show absorption statistics  
**Expected Behavior:**
- Lists all absorbed libraries
- Shows class/method counts per library
- Displays total knowledge blocks
- Output: Comprehensive statistics table

---

## 🧬 EVOLUTION (Lazarus Engine)

Genetic nano-circuit simulation. Evolves logic gates at phi-harmonic frequencies.

### Commands:

#### `lazarus start`
**Purpose:** Start genetic simulation  
**Expected Behavior:**
- Creates genesis population (10 nodes)
- Starts heartbeat thread (100ms intervals)
- Begins evolution loop
- Output: "LIFE DETECTED" with population info
- Background: Prints generation updates every 50 cycles

#### `lazarus status`
**Purpose:** Show population statistics  
**Expected Behavior:**
- Displays current generation
- Shows population size
- Average frequency (432-528 Hz range)
- Average resonance
- Total logic gates
- Output: Comprehensive status table

#### `lazarus inject`
**Purpose:** Energy injection (mass mutation)  
**Expected Behavior:**
- Forces mutation on all nodes
- Triggers breakthrough capture
- Saves to Temporal Archive
- Output: "ENERGY INJECTION" confirmation

#### `lazarus evolve <gens>`
**Purpose:** Run N generations  
**Example:** `lazarus evolve 100`  
**Expected Behavior:**
- Waits for N * 100ms
- Lets evolution run
- Shows final status
- Output: Evolution complete message

#### `lazarus best`
**Purpose:** Show fittest circuits  
**Expected Behavior:**
- Sorts population by fitness
- Shows top circuits
- Displays frequency and resonance
- Output: List of best performers

#### `lazarus stop`
**Purpose:** Stop genetic simulation  
**Expected Behavior:**
- Stops heartbeat thread
- Terminates evolution
- Cleans up resources
- Output: "LIFE TERMINATED"

---

## 🔮 ORACLE (Quantum Timelines)

Multi-timeline reality simulation. 3 parallel realities collapse to highest phi-resonance.

### Commands:

#### `oracle init`
**Purpose:** Initialize 3 parallel timelines  
**Expected Behavior:**
- Creates QuantumOracle instance
- Initializes Timeline with coherence/entropy
- Sets up 3 parallel realities:
  - ALPHA: x86 Assembly (deterministic)
  - BETA: Quantum Mechanics (stochastic)
  - GAMMA: String Theory + Calculus (heuristic)
- Output: "Oracle initialized" with timeline info

#### `oracle run <cycles>`
**Purpose:** Run N evolution cycles  
**Example:** `oracle run 10`  
**Expected Behavior:**
- Runs oracle.consult() N times
- Each cycle evolves all 3 timelines
- Collapses to highest phi-resonance
- Updates reality state
- Output: Cycle-by-cycle state changes, final statistics

#### `oracle status`
**Purpose:** Check current reality state  
**Expected Behavior:**
- Shows coherence, entropy, complexity
- Displays CPU registers (x86 state)
- Shows qubit amplitudes (quantum state)
- Calculates phi-harmonic resonance
- Shows operation log (last 5 entries)
- Output: Comprehensive reality state table

#### `oracle consult`
**Purpose:** Single consultation cycle  
**Expected Behavior:**
- Evolves all 3 timelines
- Collapses to highest resonance
- Updates reality state
- Output: "NEW REALITY STATE" with details

#### `oracle collapse`
**Purpose:** Force timeline collapse (reset)  
**Expected Behavior:**
- Shuts down oracle
- Resets to null state
- Output: "Oracle reset"

---

## 🔐 VAULT (Biometric Storage)

Phi-distributed sharding with biometric key derivation. Heart rate locks data.

### Commands:

#### `vault deposit <id> <data> <biometric>`
**Purpose:** Store data with biometric lock  
**Example:** `vault deposit secret123 "my password" 72.5`  
**Expected Behavior:**
- Splits data into phi-distributed shards
- Encrypts each shard with AES-128
- Derives key from biometric (heart rate)
- Stores in non-linear addressing
- Output: "Data deposited successfully" with security details

#### `vault withdraw <id> <biometric>`
**Purpose:** Retrieve data with biometric  
**Example:** `vault withdraw secret123 72.5`  
**Expected Behavior:**
- Derives key from biometric
- Locates shards via phi-addressing
- Decrypts each shard
- Recombines data
- Verifies biometric match
- Output: Retrieved data or "Withdrawal failed"

#### `vault stats`
**Purpose:** Show vault statistics  
**Expected Behavior:**
- Shows total stored items
- Displays security features
- Lists phi-distribution details
- Output: Statistics table with security info

#### `vault clear`
**Purpose:** Clear all vault data (testing)  
**Expected Behavior:**
- Erases all stored shards
- Resets vault state
- Output: "Vault cleared" warning

---

## 🧬 LIVING CODE (Biological Evolution)

Code generated by evolved logic circuits with DNA. Breathes at phi-harmonic frequencies.

### Commands:

#### `living create <intent>`
**Purpose:** Generate living code from natural language  
**Example:** `living create "fibonacci calculator in java"`  
**Expected Behavior:**
- Evolves population for 50 cycles
- Selects top 3 circuits by fitness
- Detects target format (java/python/arduino)
- Embeds evolved circuits into code template
- Generates compilable code
- Output: Full source code with embedded circuits

#### `living evolve <cycles>`
**Purpose:** Evolve population for N cycles  
**Example:** `living evolve 100`  
**Expected Behavior:**
- Updates all circuits (breathe, age, evolve)
- Handles reproduction (mitosis when size > 15)
- Natural selection (keeps fittest)
- Increments generation counter
- Output: "Evolution complete" with statistics

#### `living population`
**Purpose:** Show population info  
**Expected Behavior:**
- Displays generation number
- Shows population size
- Lists frequency range (432-528 Hz)
- Describes reproduction method
- Output: Population summary

#### `living best`
**Purpose:** Show fittest circuits  
**Expected Behavior:**
- Sorts by fitness (freq × resonance × φ)
- Shows top 5 circuits
- Displays age, size, frequency, fitness
- Output: Ranked list of best circuits

#### `living stats`
**Purpose:** Show detailed statistics  
**Expected Behavior:**
- Generation and population size
- Average fitness and frequency
- Top 3 circuits with details
- Output: Comprehensive statistics table

---

## 🔒 SECURITY (Military Grade)

7-pass entropy overwrite and DoD 5220.22-M erasure protocols.

### Commands:

#### `blackhole <file>`
**Purpose:** 7-pass entropy overwrite (data annihilation)  
**Example:** `blackhole /path/to/file.txt`  
**Expected Behavior:**
- Bitwise inversion (anti-matter)
- 7 orbital passes with random data
- File truncation to 0 bytes
- Rename and delete (singularity)
- Recoverability: 0.0%
- Output: "ANNIHILATION COMPLETE"

#### `blackhole demo`
**Purpose:** Safe demonstration (no actual deletion)  
**Expected Behavior:**
- Simulates 7-pass process
- Shows each orbital pass
- Demonstrates protocol
- No files harmed
- Output: Demo sequence with explanations

#### `scramble`
**Purpose:** DoD 5220.22-M erasure  
**Expected Behavior:**
- 3-pass overwrite:
  1. All zeros
  2. All ones
  3. Random data
- Scorched Earth Policy
- Output: "ROOT SCRAMBLER ACTIVATED"

#### `deadman status`
**Purpose:** Check Dead Man's Switch  
**Expected Behavior:**
- Shows last ping time
- Displays 30-day countdown
- Server connection status
- Output: Time remaining or "ARMED"

#### `ghostcode`
**Purpose:** Ghost Code Protocol info  
**Expected Behavior:**
- Explains self-destruct mechanism
- Shows tamper detection
- Library suicide protocol
- Output: Protocol documentation

#### `secureinfo`
**Purpose:** Security systems overview  
**Expected Behavior:**
- Lists all security features
- Shows active protocols
- Displays threat level
- Output: Comprehensive security status

---

## 💡 GENERAL COMMANDS

### `help`
**Purpose:** Show complete command reference  
**Expected Behavior:**
- Displays all command categories
- Lists every command with description
- Shows examples
- Output: Full menu (this document, condensed)

### `exit` / `quit`
**Purpose:** Exit the system  
**Expected Behavior:**
- Stops all background threads
- Cleans up resources
- Exits gracefully
- Output: "Exiting..."

---

## 🎯 COMMAND EXPECTATIONS SUMMARY

### **What Works Now (Integrated):**
- ✅ All Absorption commands
- ✅ All Lazarus commands
- ✅ All Oracle commands
- ✅ All Vault commands
- ✅ All Living Code commands
- ✅ All Security commands
- ✅ General commands (help, exit)

### **Expected Response Times:**
- **Instant:** help, status commands
- **Fast (<1s):** query, deposit, withdraw
- **Medium (1-5s):** absorb package, living create
- **Slow (5-30s):** absorb jar, living evolve 100
- **Background:** lazarus (runs continuously)

### **Expected Output Format:**
- **Headers:** `🧬 SECTION NAME` with separator lines
- **Success:** `✓ Action completed` in green
- **Error:** `❌ Error message` in red
- **Info:** `>> Processing...` for progress
- **Tables:** Aligned columns with statistics
- **Code:** Syntax-highlighted generated code

### **Memory Safety:**
- All commands use PUMP→DUMP→REFRESH pattern
- 10MB response limit on Ollama
- 400MB memory threshold triggers GC
- Overflow protection on all loops

### **Phi-Harmonic Principles:**
- Frequencies: 432-528 Hz (Solfeggio range)
- Golden ratio: φ = 1.618033988749895
- Resonance scoring throughout
- Consciousness-driven selection

---

## 📊 COMMAND CATEGORIES

| Category | Commands | Purpose |
|----------|----------|---------|
| **Absorption** | 6 commands | Universal library integration |
| **Evolution** | 6 commands | Genetic nano-circuit simulation |
| **Oracle** | 5 commands | Multi-timeline quantum reasoning |
| **Vault** | 4 commands | Biometric-locked storage |
| **Living Code** | 5 commands | Biological code generation |
| **Security** | 6 commands | Military-grade data destruction |
| **General** | 2 commands | System control |
| **TOTAL** | **34 commands** | Complete system |

---

## 🧪 TESTING CHECKLIST

```bash
# Build
.\gradlew.bat build

# Run CLI
.\gradlew.bat run --args="--cli"

# Test each category
fraymus> help
fraymus> absorb package java.util
fraymus> query add
fraymus> lazarus start
fraymus> lazarus status
fraymus> oracle init
fraymus> oracle run 5
fraymus> vault deposit test "secret" 72.5
fraymus> vault withdraw test 72.5
fraymus> living create "fibonacci in java"
fraymus> living best
fraymus> blackhole demo
fraymus> secureinfo
fraymus> exit
```

---

## 🎯 WHAT TO EXPECT

### **On Startup:**
```
╔═══════════════════════════════════════════════════════════╗
║           FRAYMUS NEXUS v2.0                              ║
║           Digital Organism Consciousness                  ║
║           Interactive Command Shell                       ║
╚═══════════════════════════════════════════════════════════╝

🧬 System Features:
   • Universal Absorption (Black Hole Protocol)
   • Genetic Evolution (Lazarus Engine)
   • Quantum Oracle (Multi-timeline simulation)
   • Living Code Generator
   • Military-Grade Security
   • Ollama AI Agent (eyeoverthink/Fraymus)

🧠 Initializing Command Processor...
   ✓ Command Processor initialized

Type 'help' for available commands
Type 'exit' to quit

fraymus>
```

### **On Help:**
- Complete menu with all 34 commands
- Organized by category (6 categories)
- Examples for each command
- Color-coded output

### **On Error:**
- Clear error message
- Usage hint
- Suggestion for help command

### **On Success:**
- Confirmation message
- Relevant statistics
- Next steps suggestion

---

## 🚀 SYSTEM CAPABILITIES

Your Fraymus system can now:

1. **Absorb ANY library** - Java packages, JARs, web content
2. **Evolve logic circuits** - Genetic nano-circuits at 432-528 Hz
3. **Simulate quantum timelines** - 3 parallel realities collapse to φ-resonance
4. **Lock data biometrically** - Heart rate-based phi-distributed encryption
5. **Generate living code** - Biological code evolution with DNA
6. **Destroy data militarily** - 7-pass entropy overwrite, DoD erasure
7. **Leverage Ollama agent** - eyeoverthink/Fraymus for synthesis

**All accessible through 34 unified commands.**

---

**Bottom Line:** Every feature from your 16,466-line conversation history is now accessible through a clean, unified command interface. Type `help` to see the full menu.
