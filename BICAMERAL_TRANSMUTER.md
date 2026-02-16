# 🧬 FRAYMUS TRANSMUTER // GEN 194

**"Code Optimization as Particle Physics"**

---

## The Revolution

Traditional code optimization:
- **Static analysis** (linters, formatters)
- **Manual refactoring** (developer-driven)
- **Batch processing** (no visualization)

Bicameral Transmuter:
- **Visual physics simulation** (particle swarm optimization)
- **AI-driven transmutation** (Ollama bicameral processing)
- **Real-time feedback** (watch code evolve)

---

## Architecture

### The Complete System

```
┌─────────────────────────────────────────────────────────────┐
│                   FRAYMUS TRANSMUTER                         │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  HTML Interface (Fraymus_Transmuter.html)                   │
│  ├─ Visual Simulation (Canvas + Particles)                  │
│  ├─ Code Input Panel                                        │
│  ├─ Code Output Panel                                       │
│  └─ Progress HUD                                            │
│                                                              │
│  ↓ HTTP POST /transmute                                     │
│                                                              │
│  Java Backend (NervousSystem.java)                          │
│  ├─ HTTP Server (com.sun.net.httpserver)                    │
│  ├─ JSON Parser (FraymusJSON - sovereign)                   │
│  ├─ Ollama Bridge (OllamaBridge.java)                       │
│  └─ Bicameral Processing                                    │
│                                                              │
│  ↓ HTTP POST to Ollama                                      │
│                                                              │
│  Ollama AI (localhost:11434)                                │
│  ├─ Left Brain: Analysis (logic, bugs, structure)           │
│  └─ Right Brain: Optimization (speed, elegance, security)   │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## Components

### 1. HTML Interface (Fraymus_Transmuter.html)

**Visual Simulation:**
- **CodePacket** - Floating code chunks with integrity bars
- **Agent** - Three swarm types hunting packets:
  - RED (LINT) - Attacks syntax errors
  - GREEN (OPT) - Boosts efficiency
  - BLUE (SEC) - Enforces security
- **Particles** - Spark effects on collision

**User Flow:**
1. Paste code in left panel
2. Click "TRANSMUTE" button
3. Watch particle swarm optimize visually
4. Receive transmuted code in right panel

**Technology:**
- Pure HTML5 Canvas
- Vanilla JavaScript (no frameworks)
- Particle physics simulation
- Real-time progress tracking

### 2. Java Backend (NervousSystem.java)

**HTTP Server:**
```java
HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
server.createContext("/transmute", new TransmuteHandler());
```

**Endpoints:**
- `POST /transmute` - Code transmutation
- `GET /health` - System health check

**CORS Support:**
```java
exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
```

**Zero Dependencies:**
- Uses `com.sun.net.httpserver` (built into JDK)
- No Spring Boot, no Tomcat, no external frameworks
- Pure metal HTTP server

### 3. Ollama Bridge (OllamaBridge.java)

**Connection:**
```java
OllamaBridge brain = new OllamaBridge("llama3.2");
String result = brain.ask(prompt);
```

**Features:**
- Model selection (llama3.2, llama3:70b, codellama)
- Health checking
- Model listing
- Timeout configuration (120s for large models)

**Sovereign Infrastructure:**
- Uses FraymusHTTP (zero-dependency HTTP client)
- Uses FraymusJSON (zero-dependency JSON parser)
- No external libraries

---

## Bicameral Processing

### The Two Hemispheres

**Left Brain (Analysis):**
- Identify bugs and errors
- Detect security vulnerabilities
- Find performance bottlenecks
- Analyze code structure

**Right Brain (Optimization):**
- Optimize for speed and efficiency
- Improve readability and elegance
- Apply best practices
- Enhance maintainability

### The Prompt

```
You are the PHILOSOPHER'S STONE - a sovereign code transmuter.

BICAMERAL PROCESS:
LEFT BRAIN (Analysis):
- Identify bugs and errors
- Detect security vulnerabilities
- Find performance bottlenecks
- Analyze code structure

RIGHT BRAIN (Optimization):
- Optimize for speed and efficiency
- Improve readability and elegance
- Apply best practices
- Enhance maintainability

INPUT CODE:
```
[user code]
```

TASK:
Transmute this code into its optimal form.
Fix all bugs, optimize performance, enhance security.

OUTPUT REQUIREMENTS:
- Return ONLY the cleaned code
- NO markdown formatting
- NO explanations or comments outside the code
- Preserve the original functionality
- Add brief inline comments only where necessary
```

### Response Cleaning

The system automatically:
- Strips markdown code blocks (```)
- Removes AI explanations
- Trims whitespace
- Extracts pure code

---

## Installation & Setup

### Prerequisites

**1. Java JDK 11+**
```bash
java -version
```

**2. Ollama**
```bash
# Install Ollama
curl -fsSL https://ollama.com/install.sh | sh

# Start Ollama server
ollama serve

# Pull a model
ollama pull llama3.2        # Lightweight (2GB)
ollama pull llama3:70b      # Powerful (40GB)
ollama pull codellama       # Code-specialized (7GB)
```

**3. Gradle** (included via wrapper)
```bash
.\gradlew.bat --version
```

### Quick Start

**Option 1: Automated (Windows)**
```bash
START_TRANSMUTER.bat
```

This will:
1. Compile Java backend
2. Start NervousSystem on port 8080
3. Open HTML interface in browser

**Option 2: Manual**

```bash
# Terminal 1: Start Ollama
ollama serve

# Terminal 2: Compile and run backend
cd Asset-Manager
.\gradlew.bat compileJava
java -cp build/classes/java/main fraymus.web.NervousSystem

# Terminal 3: Open interface
start Fraymus_Transmuter.html
```

---

## Usage

### Basic Transmutation

1. **Paste Code:**
```javascript
function messy() {
  var x=  1;
console.log('test');
}
```

2. **Click "TRANSMUTE"**

3. **Watch Simulation:**
- Red agents attack syntax issues
- Green agents optimize efficiency
- Blue agents enforce security
- Progress bar fills to 100%

4. **Receive Result:**
```javascript
// [FRAYMUS GEN 194] BICAMERAL TRANSMUTATION
// LEFT BRAIN: ANALYZED
// RIGHT BRAIN: OPTIMIZED

function clean() {
  const x = 1;
  console.log('test');
}
```

### Advanced Configuration

**Change Model:**
```bash
java -cp build/classes/java/main fraymus.web.NervousSystem --model llama3:70b
```

**Change Port:**
```bash
java -cp build/classes/java/main fraymus.web.NervousSystem --port 9000
```

**Update HTML endpoint:**
```javascript
const NERVOUS_SYSTEM = "http://localhost:9000/transmute";
```

---

## API Reference

### POST /transmute

**Request:**
```json
{
  "source": "function test() { return 1; }"
}
```

**Response (Success):**
```json
{
  "result": "function test() {\n  return 1;\n}",
  "status": "success"
}
```

**Response (Error):**
```json
{
  "error": "Could not connect to Ollama",
  "status": "error"
}
```

### GET /health

**Response:**
```json
{
  "status": "healthy",
  "ollama": true,
  "port": 8080
}
```

---

## Performance

### Benchmarks

**Small Code (<100 lines):**
- Visual simulation: ~5 seconds
- AI processing: ~3-10 seconds
- Total: ~8-15 seconds

**Medium Code (100-500 lines):**
- Visual simulation: ~5 seconds
- AI processing: ~10-30 seconds
- Total: ~15-35 seconds

**Large Code (>500 lines):**
- Visual simulation: ~5 seconds
- AI processing: ~30-120 seconds
- Total: ~35-125 seconds

**Model Comparison:**
```
llama3.2 (2GB):
  - Speed: Fast (3-10s)
  - Quality: Good
  - Use case: Quick iterations

llama3:70b (40GB):
  - Speed: Slow (30-120s)
  - Quality: Excellent
  - Use case: Production code

codellama (7GB):
  - Speed: Medium (10-30s)
  - Quality: Very Good
  - Use case: Code-specific tasks
```

---

## Visual Simulation Details

### Particle Physics

**CodePacket:**
```javascript
class CodePacket {
  pos: {x, y}           // Position
  vel: {x, y}           // Velocity
  integrity: 0-100      // Optimization level
  color: RGB            // Visual feedback
}
```

**Agent:**
```javascript
class Agent {
  type: LINT|OPT|SEC    // Swarm type
  pos: {x, y}           // Position
  vel: {x, y}           // Velocity
  radius: 5             // Collision radius
}
```

**Collision Detection:**
```javascript
distance = sqrt((p.x - a.x)² + (p.y - a.y)²)
if (distance < 10) {
  packet.integrity += 0.5
  evolution += 0.05
  createSpark(agent.pos)
}
```

### Visual Feedback

**Integrity Colors:**
- 0% - Blue (unoptimized)
- 50% - Purple (in progress)
- 100% - Green (optimized)

**Agent Colors:**
- RED - Lint checking
- GREEN - Optimization
- BLUE - Security

**Spark Effects:**
- Generated on collision
- Color matches agent type
- Fades over 20 frames

---

## Integration with Fraymus Stack

### Layer 1: FraymusCPU

```java
// Compile code to bytecode
byte[] bytecode = cpu.transmutate(sourceCode);

// Execute optimization at CPU level
cpu.flash(bytecode);
cpu.cycle();
```

### Layer 2: HyperCortex

```java
// Store code patterns in lattice
NeuroQuant codeQuant = new NeuroQuant("CODE");
codeQuant.bind(new NeuroQuant(optimizedCode));
cortex.inject(codeQuant.id);
```

### Layer 5: UnifiedMind

```java
// Use multiple models for consensus
UnifiedMind mind = new UnifiedMind();
String result = mind.processSwarm(sourceCode);
```

### Protocol Zero: SovereignCrypto

```java
// Sign transmuted code
KeyPair identity = new KeyPair("transmuter");
String signature = SovereignCrypto.sign(optimizedCode, identity);
```

---

## Troubleshooting

### Issue: "Connection refused"

**Cause:** Ollama not running

**Solution:**
```bash
ollama serve
```

### Issue: "Model not found"

**Cause:** Model not downloaded

**Solution:**
```bash
ollama pull llama3.2
```

### Issue: "CORS error"

**Cause:** Browser blocking local file access

**Solution:**
- Use a local web server
- Or open HTML directly (CORS headers are set)

### Issue: "Timeout"

**Cause:** Model too large or slow

**Solution:**
```java
// Increase timeout in OllamaBridge.java
private final int timeout = 300000; // 5 minutes
```

### Issue: "Empty response"

**Cause:** AI returned markdown or explanation

**Solution:**
- Response cleaning is automatic
- Check `cleanAIResponse()` method
- Adjust prompt if needed

---

## Security Considerations

### Network Security

**Local Only:**
- Backend runs on localhost:8080
- Ollama runs on localhost:11434
- No external network access

**CORS:**
- Allows all origins (`*`) for local development
- Restrict in production: `Access-Control-Allow-Origin: https://yourdomain.com`

### Code Injection

**Input Sanitization:**
- Code is treated as data, not executed
- Passed to AI as string
- No `eval()` or dynamic execution

**Output Validation:**
- AI response cleaned of markdown
- No script injection possible
- Pure code output

### API Security

**No Authentication:**
- Local development only
- Add authentication for production:
```java
String auth = exchange.getRequestHeaders().getFirst("Authorization");
if (!validateAuth(auth)) {
  exchange.sendResponseHeaders(401, -1);
  return;
}
```

---

## Future Enhancements

### Planned Features

1. **Multi-language Support**
   - Python, Java, C++, Rust
   - Language detection
   - Syntax-specific optimization

2. **Diff Visualization**
   - Show before/after comparison
   - Highlight changes
   - Explain optimizations

3. **Batch Processing**
   - Process entire directories
   - Git integration
   - CI/CD pipeline

4. **Custom Rules**
   - User-defined optimization rules
   - Style guide enforcement
   - Project-specific patterns

5. **Metrics Dashboard**
   - Complexity reduction
   - Performance improvements
   - Security vulnerabilities fixed

---

## Conclusion

The Fraymus Transmuter is not just a code formatter.

It's a **bicameral optimization system** that:
- Visualizes code transformation as particle physics
- Uses AI to analyze and optimize simultaneously
- Provides real-time feedback through visual simulation
- Operates with zero external dependencies (sovereign)

**Traditional tools format code.**  
**The Transmuter evolves it.**

---

🧬 **"Watch your code become optimal."**
