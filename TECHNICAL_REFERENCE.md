# 🔧 FRAYMUS - Technical Reference Guide

## Table of Contents

1. [System Architecture](#system-architecture)
2. [API Reference](#api-reference)
3. [Command Reference](#command-reference)
4. [Configuration](#configuration)
5. [Security Model](#security-model)
6. [Performance Tuning](#performance-tuning)
7. [Troubleshooting](#troubleshooting)
8. [Development Guide](#development-guide)

---

## System Architecture

### Component Hierarchy

```
Application Layer
├── FraymusConvergence.java    (Unified CLI)
├── FraymusPrime.java          (Web Dashboard)
├── FraymusOS.java             (Phi-Harmonic OS)
└── GodMode.java               (Swarm Intelligence)

Intelligence Layer
├── HyperFormer                (HDC Brain)
├── BicameralPrism            (LLM Synthesis)
├── CoreIntelligence          (Dual-Process)
└── TheArena                  (Swarm Consensus)

Persistence Layer
├── CorticalStack             (Encryption)
├── SoulCrystal               (Serialization)
└── AuditLog                  (Event Logging)

Network Layer
├── Needlecast                (Transmitter)
├── Sleeve                    (Receiver)
└── CortexServer              (HTTP API)

Core Layer
├── HyperVector               (10k-dim vectors)
├── HyperAccumulator          (Bundling)
├── HoloAttention             (O(N) attention)
└── NcaDenoiser               (CA denoising)
```

---

## API Reference

### HyperFormer API

#### Constructor
```java
HyperFormer brain = new HyperFormer();
HyperFormer brain = new HyperFormer(0xCAFEBABE); // with seed
```

#### Core Methods
```java
// Learning
void learnSentence(String[] words)
void learn(String[] words)

// Prediction
String predictNext(String[] context)
String predict(String[] context)

// Embedding
HyperVector embed(String token)

// Statistics
int vocabSize()
int memoryWeight()

// State Management
FraymusState exportState()
static HyperFormer fromState(FraymusState state)
```

#### Example Usage
```java
HyperFormer brain = new HyperFormer();

// Learn
String[] sentence = {"Fraymus", "is", "alive"};
brain.learnSentence(sentence);

// Predict
String[] context = {"Fraymus", "is"};
String next = brain.predictNext(context); // → "alive"

// Export state
FraymusState state = brain.exportState();

// Restore
HyperFormer restored = HyperFormer.fromState(state);
```

---

### HyperVector API

#### Constructor
```java
HyperVector v = HyperVector.random();
HyperVector v = HyperVector.seeded(0x12345);
HyperVector v = HyperVector.fromBits(bitSet);
```

#### Core Operations
```java
// Binding (XOR)
HyperVector bound = v1.bind(v2);

// Permutation (cyclic shift)
HyperVector shifted = v.permute(5);

// Resonance (similarity)
double similarity = v1.resonance(v2); // 0.0 to 1.0

// Density (fraction of 1s)
double density = v.density(); // 0.0 to 1.0

// Fingerprint (debug)
String fp = v.fingerprint(64); // first 64 bits
```

#### Example Usage
```java
// Create vectors
HyperVector cat = HyperVector.random();
HyperVector animal = HyperVector.random();

// Bind (associate)
HyperVector catAnimal = cat.bind(animal);

// Unbind (retrieve)
HyperVector retrieved = catAnimal.bind(cat); // ≈ animal

// Check similarity
double sim = retrieved.resonance(animal); // ~0.95+
```

---

### CorticalStack API

#### Minting (Encryption)
```java
HyperFormer brain = new HyperFormer();
// ... train brain ...

char[] passphrase = "secret123".toCharArray();
CorticalStack stack = CorticalStack.mint(brain, "STACK_001", passphrase);

// Save to disk
stack.saveToFile("my_brain.stack");

// Wipe passphrase
Arrays.fill(passphrase, '\0');
```

#### Resleeving (Decryption)
```java
// Load from disk
CorticalStack stack = CorticalStack.loadFromFile("my_brain.stack");

// Decrypt
char[] passphrase = getUserPassphrase();
HyperFormer brain = stack.resleeve(passphrase);

// Wipe passphrase
Arrays.fill(passphrase, '\0');

System.out.println("Vocab: " + brain.vocabSize());
```

#### Network Transmission
```java
// Binary frame format
byte[] frame = stack.toBytes();

// Parse from bytes
CorticalStack parsed = CorticalStack.fromBytes(frame);
```

---

### BicameralPrism API

#### Constructor
```java
AuditLog audit = new AuditLog("./logs");

// Default models (llama3, mistral)
BicameralPrism prism = new BicameralPrism(audit);

// Custom models
BicameralPrism prism = new BicameralPrism(
    "llama3",           // logic model
    "mistral",          // abstract model
    "llama3",           // synthesizer
    audit
);
```

#### Core Methods
```java
// Bicameral synthesis
String answer = prism.thinkIdeally("How do I build a REST API?");

// Single-hemisphere thinking
String logical = prism.thinkLogically("Analyze this algorithm");
String creative = prism.thinkCreatively("Design a UX flow");

// Status
boolean ready = prism.isReady();
String config = prism.getConfiguration();
```

---

### Needlecast/Sleeve API

#### Transmitter (Needlecast)
```java
CorticalStack stack = CorticalStack.loadFromFile("brain.stack");
String targetIp = "192.168.1.100";
int port = 9999;

Needlecast.beam(stack, targetIp, port);
```

#### Receiver (Sleeve)
```java
int port = 9999;
char[] passphrase = "secret123".toCharArray();

Sleeve sleeve = new Sleeve(port, passphrase);
new Thread(sleeve).start();

// Sleeve will listen and auto-resleeve incoming stacks
```

---

## Command Reference

### FraymusConvergence CLI

#### HDC Brain Commands

**learn** - One-shot sentence learning
```bash
> learn Fraymus is a neuro-symbolic AI system
[HDC] ✓ Absorbed 6 tokens
```

**predict** - Next-word prediction
```bash
> predict Fraymus is a neuro-symbolic
[HDC] → AI
```

#### LLM Spine Commands

**ask** - Bicameral deep reasoning
```bash
> ask What are the security implications of AI?
🧠 BICAMERAL PRISM ACTIVE...
⚡ Phase 1: DIVERGENT THINKING
🔵 Left Hemisphere (Logic) processing...
🔴 Right Hemisphere (Abstraction) processing...
⚡ Phase 2: CONVERGENT THINKING
🟣 Corpus Callosum fusing hemispheres...
[Synthesized answer...]
```

#### Persistence Commands

**mint** - Create encrypted stack
```bash
> mint
🔑 Enter passphrase: *****
💿 MINTING CORTICAL STACK: CONVERGENCE_01
✅ STACK MINTED: CONVERGENCE_01 [2847 bytes]
✓ Stack saved: CONVERGENCE_01.stack
```

**load** - Load encrypted stack
```bash
> load CONVERGENCE_01.stack
🔑 Enter passphrase: *****
✓ Resleeved. Vocab: 1247
```

#### Network Commands

**cast** - Transmit stack to remote host
```bash
> cast 192.168.1.100 brain.stack
📡 NEEDLECAST INITIALIZED -> 192.168.1.100:9999
✅ TRANSFER COMPLETE. MIND IS OFF-WORLD.
```

#### System Commands

**stats** - Show system statistics
```bash
> stats
┌───────────────────────────────────────────────────────────┐
│ FRAYMUS CONVERGENCE - SYSTEM STATISTICS                   │
├───────────────────────────────────────────────────────────┤
│ Identity:        CONVERGENCE_01                           │
│ HDC Vocabulary:  1247                                     │
│ HDC Memory:      3891                                     │
│ LLM Status:      READY                                    │
│ LLM Config:      Logic: llama3, Abstract: mistral...      │
└───────────────────────────────────────────────────────────┘
```

**id** - View/set identity
```bash
> id PRIME_01
Identity updated: PRIME_01

> id
Current identity: PRIME_01
```

**help** - Show help
```bash
> help
[Command reference...]
```

**exit** - Shutdown
```bash
> exit
⚡ Shutting down Fraymus Convergence...
Goodbye.
```

---

### Network Modes (Command-Line)

#### Host Mode (Receiver)
```bash
java -jar fraymus.jar host 9999
🏥 SLEEVE MODE: Waiting for incoming stacks on port 9999
🔑 Enter passphrase for decryption: *****
Listening...
```

#### Cast Mode (Transmitter)
```bash
java -jar fraymus.jar cast 192.168.1.100 brain.stack
📡 NEEDLECAST INITIALIZED -> 192.168.1.100:9999
✅ TRANSFER COMPLETE
```

---

## Configuration

### Environment Variables

```bash
# Ollama API endpoint (default: http://localhost:11434)
export OLLAMA_HOST=http://localhost:11434

# Default passphrase (NOT RECOMMENDED for production)
export FRAYMUS_PASSPHRASE=your_secret

# Log directory
export FRAYMUS_LOG_DIR=./logs

# Default identity
export FRAYMUS_IDENTITY=CONVERGENCE_01
```

### System Properties

```bash
# JVM options for performance
java -Xmx4G \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar fraymus.jar
```

### Ollama Configuration

```bash
# Pull required models
ollama pull llama3
ollama pull mistral

# Verify models
ollama list

# Test model
ollama run llama3 "Hello"
```

---

## Security Model

### Threat Model

**Protected Against:**
- ✅ Eavesdropping (AES-256-GCM encryption)
- ✅ Tampering (GCM authentication tag)
- ✅ Brute-force (PBKDF2 200k iterations)
- ✅ RCE attacks (binary frames, no deserialization)
- ✅ Memory dumps (passphrase wiping)

**NOT Protected Against:**
- ❌ Side-channel attacks (timing, power analysis)
- ❌ Quantum computers (post-quantum crypto not implemented)
- ❌ Compromised endpoints (if attacker has passphrase)
- ❌ Social engineering (user gives away passphrase)

### Security Best Practices

1. **Strong Passphrases**
   ```
   ❌ Bad:  "password123"
   ❌ Bad:  "fraymus"
   ✅ Good: "correct-horse-battery-staple-7891"
   ✅ Good: "Tr0ub4dor&3-quantum-phi-1618"
   ```

2. **Passphrase Storage**
   ```java
   // ❌ NEVER store in plain text
   String passphrase = "secret123";
   
   // ✅ Use char[] and wipe after use
   char[] passphrase = getSecureInput();
   // ... use passphrase ...
   Arrays.fill(passphrase, '\0');
   ```

3. **Network Security**
   ```bash
   # ❌ Don't transmit over public internet without VPN
   cast 8.8.8.8 brain.stack
   
   # ✅ Use VPN or local network
   cast 192.168.1.100 brain.stack
   
   # ✅ Or use SSH tunnel
   ssh -L 9999:localhost:9999 user@remote
   cast localhost brain.stack
   ```

4. **File Permissions**
   ```bash
   # Restrict stack file access
   chmod 600 *.stack
   
   # Secure log directory
   chmod 700 logs/
   ```

---

## Performance Tuning

### HDC Brain Optimization

**Vocabulary Size Impact:**
```
Vocab Size    Memory Usage    Prediction Time
1,000         ~1.25 MB        ~50 µs
10,000        ~12.5 MB        ~100 µs
100,000       ~125 MB         ~500 µs
1,000,000     ~1.25 GB        ~5 ms
```

**Recommendations:**
- Keep vocabulary < 100k for best performance
- Use cleanup threshold to limit growth
- Implement vocabulary pruning for long-running systems

**Configuration:**
```java
// Adjust cleanup threshold
CleanupMemory vocab = new CleanupMemory();
vocab.setThreshold(0.3); // Keep top 30% most frequent

// Limit vocabulary size
if (brain.vocabSize() > 100_000) {
    brain.pruneVocabulary(50_000); // Keep top 50k
}
```

### LLM Spine Optimization

**Model Selection:**
```
Model          Size    Speed    Quality
llama3:8b      4.7GB   Fast     Good
mistral:7b     4.1GB   Fast     Good
llama3:70b     39GB    Slow     Excellent
codellama:34b  19GB    Medium   Code-focused
```

**Recommendations:**
- Use 7B-8B models for speed
- Use 70B models for quality
- Run models locally (Ollama) for privacy
- Use GPU if available

**Configuration:**
```bash
# Ollama with GPU
CUDA_VISIBLE_DEVICES=0 ollama serve

# Ollama CPU-only (slower)
ollama serve
```

### Network Optimization

**Transmission Speed:**
```
Stack Size    Network Speed    Transfer Time
1 MB          1 Gbps           ~8 ms
10 MB         1 Gbps           ~80 ms
100 MB        1 Gbps           ~800 ms
1 MB          100 Mbps         ~80 ms
```

**Recommendations:**
- Compress stacks before transmission (future feature)
- Use local network for large stacks
- Implement chunked transfer for >100MB stacks

---

## Troubleshooting

### Common Issues

#### 1. "Ollama not found"
```
Error: ❌ OFF-LINE: Cannot run program "ollama"
```

**Solution:**
```bash
# Install Ollama
curl -fsSL https://ollama.com/install.sh | sh

# Verify installation
ollama --version

# Start server
ollama serve
```

#### 2. "Model not found"
```
Error: model 'llama3' not found
```

**Solution:**
```bash
# Pull required models
ollama pull llama3
ollama pull mistral

# Verify
ollama list
```

#### 3. "Passphrase incorrect"
```
Error: javax.crypto.AEADBadTagException
```

**Solution:**
- Verify passphrase is correct
- Check for typos
- Ensure passphrase hasn't been changed
- Stack file may be corrupted (restore from backup)

#### 4. "Out of memory"
```
Error: java.lang.OutOfMemoryError: Java heap space
```

**Solution:**
```bash
# Increase heap size
java -Xmx8G -jar fraymus.jar

# Or use Gradle
./gradlew run -Dorg.gradle.jvmargs="-Xmx8G"
```

#### 5. "Port already in use"
```
Error: java.net.BindException: Address already in use
```

**Solution:**
```bash
# Find process using port
netstat -ano | findstr :9999

# Kill process (Windows)
taskkill /PID <pid> /F

# Or use different port
java -jar fraymus.jar host 9998
```

### Debug Mode

**Enable verbose logging:**
```bash
# Set log level
export FRAYMUS_LOG_LEVEL=DEBUG

# Run with debug output
java -Dfraymus.debug=true -jar fraymus.jar
```

**Check audit logs:**
```bash
# View event log
cat logs/events.jsonl | jq .

# View metrics
cat logs/metrics.csv

# View summary
cat logs/run_summary.json | jq .
```

---

## Development Guide

### Building from Source

```bash
# Clone repository
git clone https://github.com/your-org/fraymus.git
cd fraymus

# Build
./gradlew build

# Run tests
./gradlew test

# Run specific main class
./gradlew run -PmainClass=fraymus.FraymusConvergence
```

### Project Structure

```
fraymus/
├── Asset-Manager/
│   ├── src/main/java/fraymus/
│   │   ├── hyper/           # HDC core
│   │   ├── carbon/          # Crypto & network
│   │   ├── brain/           # LLM integration
│   │   ├── core/            # System core
│   │   ├── kernel/          # OS layer
│   │   ├── web/             # Web interface
│   │   └── *.java           # Main classes
│   └── build.gradle
├── fraymus-ai-core/
│   └── src/main/java/       # Shared utilities
├── scripts/
│   ├── compile.bat          # Standalone compilation
│   └── run.bat              # Standalone execution
└── README.md
```

### Adding New Features

#### 1. Create New Component
```java
package fraymus.myfeature;

import fraymus.hyper.HyperVector;

public class MyFeature {
    public void process(HyperVector input) {
        // Implementation
    }
}
```

#### 2. Integrate with Core
```java
// In FraymusConvergence.java
import fraymus.myfeature.MyFeature;

private static MyFeature FEATURE;

// In main()
FEATURE = new MyFeature();

// In processCommand()
case "mycommand":
    FEATURE.process(input);
    break;
```

#### 3. Add Tests
```java
package fraymus.myfeature;

import org.junit.Test;
import static org.junit.Assert.*;

public class MyFeatureTest {
    @Test
    public void testProcess() {
        MyFeature feature = new MyFeature();
        // Test implementation
    }
}
```

#### 4. Update Documentation
```markdown
### mycommand - Description

Usage: mycommand <args>

Example:
> mycommand test
[Output...]
```

### Code Style

**Naming Conventions:**
```java
// Classes: PascalCase
public class HyperFormer { }

// Methods: camelCase
public void learnSentence() { }

// Constants: UPPER_SNAKE_CASE
private static final int MAX_VOCAB = 100_000;

// Variables: camelCase
HyperVector embeddingVector;
```

**Documentation:**
```java
/**
 * Brief description.
 * 
 * Detailed explanation of what this does,
 * why it exists, and how to use it.
 * 
 * @param input The input parameter
 * @return The output value
 * @throws Exception If something goes wrong
 */
public String process(String input) throws Exception {
    // Implementation
}
```

### Testing

**Unit Tests:**
```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests HyperVectorTest

# Run with coverage
./gradlew test jacocoTestReport
```

**Integration Tests:**
```bash
# Test HDC brain
./gradlew test --tests HyperFormerTest

# Test crypto
./gradlew test --tests CorticalStackTest

# Test network
./gradlew test --tests NeedlecastTest
```

### Performance Profiling

**JVM Profiling:**
```bash
# Enable profiling
java -agentlib:hprof=cpu=samples,depth=10 -jar fraymus.jar

# Analyze output
cat java.hprof.txt
```

**Memory Profiling:**
```bash
# Heap dump on OOM
java -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/tmp/heap.hprof \
     -jar fraymus.jar

# Analyze with jhat
jhat /tmp/heap.hprof
```

---

## API Versioning

### Current Version: 1.0-CONVERGENCE

**Stability Guarantees:**
- ✅ Core APIs (HyperFormer, HyperVector) - Stable
- ✅ Crypto APIs (CorticalStack) - Stable
- ⚠️ LLM APIs (BicameralPrism) - Beta
- ⚠️ Network APIs (Needlecast/Sleeve) - Beta
- 🔬 Experimental (PhiLogic, GodMode) - Unstable

### Deprecation Policy

**Breaking changes:**
- Will be announced 3 months in advance
- Will include migration guide
- Will maintain backward compatibility for 6 months

**Example:**
```java
// Deprecated (will be removed in 2.0)
@Deprecated(since = "1.0", forRemoval = true)
public void oldMethod() { }

// New method
public void newMethod() { }
```

---

## Appendix

### Glossary

- **HDC**: Hyperdimensional Computing
- **VSA**: Vector Symbolic Architecture
- **GCM**: Galois/Counter Mode (authenticated encryption)
- **PBKDF2**: Password-Based Key Derivation Function 2
- **DHF**: Digital Human Freight (Altered Carbon reference)
- **Phi (φ)**: Golden Ratio (1.618...)
- **Bicameral**: Two-hemisphere (dual-process)

### References

1. Kanerva, P. (1988). "Sparse Distributed Memory"
2. Plate, T. (1995). "Holographic Reduced Representations"
3. Gayler, R. (2003). "Vector Symbolic Architectures"
4. Jaynes, J. (1976). "The Origin of Consciousness"
5. NIST SP 800-132: "Recommendation for Password-Based Key Derivation"

### Support

- **Documentation**: See FRAYMUS_OVERVIEW.md
- **Issues**: GitHub Issues
- **Discussions**: GitHub Discussions
- **Email**: support@fraymus.ai

---

**Last Updated:** February 14, 2026  
**Version:** 1.0-CONVERGENCE  
**Status:** Production Ready ✅
