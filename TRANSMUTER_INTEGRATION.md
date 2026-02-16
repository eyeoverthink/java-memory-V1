# 🧬 BICAMERAL TRANSMUTER - CONVERGENCE INTEGRATION

**"Code Optimization Now Embedded in the Unified Intelligence System"**

---

## Integration Complete

The **Bicameral Transmuter** (NervousSystem + OllamaBridge) has been integrated into **Fraymus Convergence** as a native subsystem.

---

## What Was Added

### New Components

**OllamaBridge** - AI interface to Ollama models
- Connects to localhost:11434
- Supports llama3.2, llama3:70b, codellama, etc.
- 120-second timeout for large models
- Health checking and model listing

**NervousSystem** - HTTP server for visual interface
- Pure Java HTTP server (com.sun.net.httpserver)
- Port 8080 with `/transmute` and `/health` endpoints
- CORS enabled for local HTML files
- Zero external dependencies

### New Commands

```
CONVERGENCE_01> transmute <code>
```
**Direct code optimization via Ollama**
- Left Brain: Analyzes structure, bugs, security
- Right Brain: Optimizes speed, elegance, best practices
- Output: Cleaned, optimized code

```
CONVERGENCE_01> startserver
```
**Start HTTP server on port 8080**
- Enables visual interface (Fraymus_Transmuter.html)
- Runs in background thread
- Auto-checks Ollama availability

```
CONVERGENCE_01> stopserver
```
**Stop HTTP server**
- Gracefully shuts down background thread
- Frees port 8080

---

## Updated Banner

```
╔═══════════════════════════════════════════════════════════════╗
║                                                               ║
║   ⚡ FRAYMUS CONVERGENCE ⚡                                    ║
║                                                               ║
║   Neuro-Symbolic Hybrid Intelligence System                  ║
║                                                               ║
╠═══════════════════════════════════════════════════════════════╣
║   HDC Brain      : HyperFormer (10k-dim XOR logic)           ║
║   LLM Spine      : Bicameral Prism (dual-model synthesis)    ║
║   🧬 Transmuter  : Ollama-powered code optimization          ║
║   Crypto Stack   : AES-256-GCM encrypted persistence         ║
║   Network        : Needlecast transmission protocol          ║
║   🦞 Claw Spine  : OpenClaw integration (skills + sandbox)   ║
║   🧬 Meta-Layer  : Self-coding & Darwinian evolution         ║
║   🧠 Neuro-Quant : 10,000D HyperCortex + Omega Point         ║
╚═══════════════════════════════════════════════════════════════╝
```

---

## Startup Sequence

When you run `.\run-convergence.bat`, you'll now see:

```
🧬 Initializing Bicameral Transmuter...
   ✓ Ollama Bridge online (llama3.2)
   Available models: llama3.2, codellama, mistral
   ✓ Transmuter ready (use 'startserver' to activate)
```

Or if Ollama is not running:

```
🧬 Initializing Bicameral Transmuter...
   ⚠️  Ollama offline (start with: ollama serve)
   ✓ Transmuter ready (use 'startserver' to activate)
```

---

## Usage Examples

### Example 1: Direct Code Transmutation

```
CONVERGENCE_01> transmute function add(a,b){return a+b;}

🧬 BICAMERAL TRANSMUTATION
   Left Brain: Analyzing structure...
   Right Brain: Optimizing elegance...

═══════════════════════════════════════════════════════════════
  TRANSMUTED CODE
═══════════════════════════════════════════════════════════════
/**
 * Adds two numbers
 * @param {number} a - First operand
 * @param {number} b - Second operand
 * @returns {number} Sum of a and b
 */
function add(a, b) {
  return a + b;
}
═══════════════════════════════════════════════════════════════
```

### Example 2: Start Visual Interface

```
CONVERGENCE_01> startserver

⚡ TRANSMUTER SERVER STARTED
   Endpoint: http://localhost:8080/transmute
   Health: http://localhost:8080/health
   Open Fraymus_Transmuter.html to use visual interface
```

Then open `Fraymus_Transmuter.html` in your browser and use the particle physics visualization.

### Example 3: Combined Workflow

```
CONVERGENCE_01> startserver
⚡ TRANSMUTER SERVER STARTED
   Endpoint: http://localhost:8080/transmute
   Health: http://localhost:8080/health
   Open Fraymus_Transmuter.html to use visual interface

CONVERGENCE_01> transmute class User { constructor(n) { this.name = n; } }

🧬 BICAMERAL TRANSMUTATION
   Left Brain: Analyzing structure...
   Right Brain: Optimizing elegance...

═══════════════════════════════════════════════════════════════
  TRANSMUTED CODE
═══════════════════════════════════════════════════════════════
class User {
  constructor(name) {
    if (!name) throw new Error('Name is required');
    this.name = name;
  }
  
  getName() {
    return this.name;
  }
}
═══════════════════════════════════════════════════════════════

CONVERGENCE_01> stopserver
   ✓ Transmuter server stopped
```

---

## Architecture Integration

### Component Hierarchy

```
FraymusConvergence (Main)
├─ HDC_BRAIN (HyperFormer)
├─ LLM_SPINE (BicameralPrism)
├─ OLLAMA_BRAIN (OllamaBridge) ← NEW
├─ TRANSMUTER_SERVER (NervousSystem) ← NEW
├─ SKILLS (SkillLoader)
├─ SANDBOX (DockerBox)
├─ OBSIDIAN (ObsidianWeaver)
├─ PHASE_LOCK (PhaseLocker)
├─ QUANTUM (QuantumBinder)
├─ CODE_EVOLVER (SelfCodeEvolver)
├─ CODE_GEN (LivingCodeGenerator)
├─ REFLECTOR (CodeReflector)
├─ EVOLUTION_LOOP (DarwinianLoop)
├─ NEURO_CORTEX (HyperCortex)
└─ OMEGA (OmegaPoint.OmegaProtocol)
```

### Data Flow

```
USER INPUT
    ↓
CONVERGENCE_01> transmute <code>
    ↓
OLLAMA_BRAIN.ask(prompt)
    ↓
HTTP POST to localhost:11434/api/generate
    ↓
Ollama AI (llama3.2)
├─ Left Brain: Analysis
└─ Right Brain: Optimization
    ↓
Response cleaning (remove markdown)
    ↓
Display transmuted code
    ↓
AUDIT.log("code_transmuted", "ollama")
```

### HTTP Server Flow

```
USER BROWSER
    ↓
POST http://localhost:8080/transmute
    ↓
NervousSystem.TransmuteHandler
    ↓
FraymusJSON.parse(body)
    ↓
OLLAMA_BRAIN.ask(prompt)
    ↓
Clean response
    ↓
FraymusJSON.stringify(result)
    ↓
HTTP 200 OK
    ↓
USER BROWSER (visual particle animation)
```

---

## Configuration

### Environment Variables

**OLLAMA_MODEL** - Override default model
```bash
# PowerShell
$env:OLLAMA_MODEL = "llama3:70b"

# Then run
.\run-convergence.bat
```

**OPENAI_API_KEY** - For BicameralPrism (existing)
```bash
$env:OPENAI_API_KEY = "sk-..."
```

### Model Selection

Available models (check with `ollama list`):
- **llama3.2** - Fast, lightweight (2GB) - DEFAULT
- **llama3:70b** - Powerful, slow (40GB)
- **codellama** - Code-specialized (7GB)
- **mistral** - Balanced (7GB)

---

## Command Reference

### Transmuter Commands

| Command | Description | Example |
|---------|-------------|---------|
| `transmute <code>` | Optimize code via Ollama | `transmute function test() { return 1; }` |
| `startserver` | Start HTTP server (port 8080) | `startserver` |
| `stopserver` | Stop HTTP server | `stopserver` |

### Related Commands

| Command | Description | Example |
|---------|-------------|---------|
| `ask <question>` | Deep LLM reasoning | `ask How do I optimize this algorithm?` |
| `evolve <code>` | Phi-harmonic code evolution | `evolve function test() { }` |
| `smartevolve <code>` | LLM + Phi evolution | `smartevolve function test() { }` |

---

## Comparison: Transmute vs Evolve vs SmartEvolve

### `transmute <code>`
- **Engine:** Ollama AI (llama3.2)
- **Method:** Bicameral processing (left brain + right brain)
- **Focus:** Bug fixes, optimization, security
- **Speed:** 3-30 seconds (depends on model)
- **Output:** Clean, optimized code

### `evolve <code>`
- **Engine:** SelfCodeEvolver (phi-harmonic)
- **Method:** Phi-resonance pattern extraction
- **Focus:** Mathematical elegance, phi integrity
- **Speed:** <1 second
- **Output:** Phi-enhanced code + metrics

### `smartevolve <code>`
- **Engine:** BicameralPrism + SelfCodeEvolver
- **Method:** LLM analysis → Phi evolution
- **Focus:** Deep analysis + mathematical enhancement
- **Speed:** 5-35 seconds
- **Output:** Analysis + evolved code

**Use Cases:**
- **transmute** - General code cleanup and optimization
- **evolve** - Add phi-harmonic structure
- **smartevolve** - Deep analysis + phi enhancement

---

## Files Modified

### FraymusConvergence.java
- Added imports: `NervousSystem`, `OllamaBridge`
- Added static fields: `TRANSMUTER_SERVER`, `OLLAMA_BRAIN`, `SERVER_THREAD`
- Added initialization in `main()`
- Added commands: `transmute`, `startserver`, `stopserver`
- Updated banner and help text

### Files Created (Previous Session)
- `NervousSystem.java` - HTTP server
- `OllamaBridge.java` - Ollama interface
- `FraymusHTTP.java` - Extended with timeout support
- `Fraymus_Transmuter.html` - Visual interface
- `BICAMERAL_TRANSMUTER.md` - Documentation

---

## Testing

### Test 1: Check Ollama Connection

```
CONVERGENCE_01> transmute console.log("test")

If Ollama is running:
🧬 BICAMERAL TRANSMUTATION
   Left Brain: Analyzing structure...
   Right Brain: Optimizing elegance...
[transmuted code appears]

If Ollama is not running:
   ❌ Ollama not available. Start with: ollama serve
```

### Test 2: Start HTTP Server

```
CONVERGENCE_01> startserver

⚡ TRANSMUTER SERVER STARTED
   Endpoint: http://localhost:8080/transmute
   Health: http://localhost:8080/health
   Open Fraymus_Transmuter.html to use visual interface
```

### Test 3: Health Check

Open browser: `http://localhost:8080/health`

Expected response:
```json
{
  "status": "healthy",
  "ollama": true,
  "port": 8080
}
```

### Test 4: Visual Interface

1. `CONVERGENCE_01> startserver`
2. Open `Fraymus_Transmuter.html` in browser
3. Paste code in left panel
4. Click "TRANSMUTE"
5. Watch particle swarm optimize
6. See result in right panel

---

## Troubleshooting

### Issue: "Ollama not available"

**Cause:** Ollama not running

**Solution:**
```bash
# Start Ollama
ollama serve

# In another terminal
.\run-convergence.bat
```

### Issue: "Server already running on port 8080"

**Cause:** Previous server instance still active

**Solution:**
```
CONVERGENCE_01> stopserver
CONVERGENCE_01> startserver
```

### Issue: "Empty response from Ollama"

**Cause:** Model not downloaded or incompatible

**Solution:**
```bash
# Check available models
ollama list

# Pull a model
ollama pull llama3.2

# Set model in Convergence
$env:OLLAMA_MODEL = "llama3.2"
.\run-convergence.bat
```

### Issue: "Connection timeout"

**Cause:** Model too large or slow

**Solution:**
- Use smaller model (llama3.2 instead of llama3:70b)
- Increase timeout in OllamaBridge.java (default: 120s)

---

## Performance

### Transmutation Speed

| Model | Size | Speed | Quality |
|-------|------|-------|---------|
| llama3.2 | 2GB | 3-10s | Good |
| codellama | 7GB | 10-30s | Very Good |
| llama3:70b | 40GB | 30-120s | Excellent |

### HTTP Server

- **Startup:** <1 second
- **Request handling:** 4 concurrent threads
- **Memory overhead:** ~10MB

---

## Integration Benefits

### Before Integration

**Separate systems:**
- Convergence: HDC + LLM + Skills
- Transmuter: Standalone HTTP server

**Workflow:**
1. Exit Convergence
2. Start Transmuter server manually
3. Open HTML interface
4. No integration with other Convergence features

### After Integration

**Unified system:**
- All features in one executable
- Seamless command switching

**Workflow:**
```
CONVERGENCE_01> learn function add(a,b){return a+b;}
   [HDC] ✓ Absorbed 4 tokens

CONVERGENCE_01> transmute function add(a,b){return a+b;}
   [Ollama] Transmuted code with docs and validation

CONVERGENCE_01> evolve function add(a,b){return a+b;}
   [Phi] Enhanced with golden ratio structure

CONVERGENCE_01> ask How can I make this faster?
   [LLM] Analysis and suggestions
```

**All in one session, no context switching.**

---

## Future Enhancements

### Planned Features

1. **Model Auto-Selection**
   - Detect code complexity
   - Choose optimal model (fast vs quality)

2. **Transmutation History**
   - Store before/after pairs
   - Learn from user preferences

3. **Batch Transmutation**
   - Process entire directories
   - Git integration

4. **Custom Prompts**
   - User-defined optimization rules
   - Style guide enforcement

5. **Metrics Dashboard**
   - Complexity reduction
   - Performance improvements
   - Security vulnerabilities fixed

---

## Conclusion

The **Bicameral Transmuter** is now a core subsystem of **Fraymus Convergence**, providing:

✅ **Direct code optimization** via `transmute` command  
✅ **Visual interface** via HTTP server  
✅ **Seamless integration** with existing features  
✅ **Zero external dependencies** (pure Java)  
✅ **Ollama AI integration** (llama3.2, codellama, etc.)  

**The convergence is complete.**

---

🧬 **"One system. Infinite intelligence."**
