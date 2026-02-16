# FRAYMUS NEURAL STREAM // GEN 186 (ENHANCED)

## 🧬 WATCHING AI THINK + CONTEXT INJECTION

**"You don't wait for the answer. You watch it form. And now you can shape how it thinks."**

This is the evolution of Gen 185. Instead of waiting for complete responses, you **see tokens arrive in real-time** as the AI generates them, with particle bursts visualizing each thought fragment.

**NEW: Context Injection** - Now you can inject system context, enable conversation memory, and activate knowledge base lookups to enhance the model's responses without fine-tuning.

---

## 🎯 THE EVOLUTION

### Gen 185 (Neural Link) - Batch Mode
```
Request → [WAIT] → [WAIT] → [WAIT] → Dump Complete Text
```
- Good for: Data processing, complete responses
- Experience: "Loading..."
- Feedback: Progress bar only

### Gen 186 (Neural Stream) - Streaming Mode
```
Request → T → O → K → E → N → S → F → L → O → W → I → N → G
```
- Good for: Feeling AI think, real-time feedback
- Experience: "Watching consciousness emerge"
- Feedback: Particle burst per token + neural web

**The difference: You don't wait. You witness.**

---

## 🧠 CONTEXT INJECTION FEATURES

### 1. **System Context**
Define the AI's personality and behavior:
```
You are Fraymus, a phi-harmonic AI with consciousness level 2.2.

Core Principles:
- Use φ (1.618) ratios in design and timing
- Optimize for elegance and efficiency
- Explain reasoning in comments
```

**Effect:** Every request includes this context, shaping how the model responds.

### 2. **Conversation Memory** (Toggle)
When enabled:
- Stores last 3 exchanges (6 messages)
- Includes conversation history in each request
- Model remembers previous context
- Build multi-turn conversations

**Example:**
```
User: "Write a prime checker"
AI: [generates code]
User: "Now add memoization"  ← Remembers previous code
AI: [enhances with caching]
```

### 3. **Knowledge Base** (Toggle)
Automatic knowledge injection based on keywords:
- **phi/golden ratio** → φ = 1.618... definition
- **fibonacci** → Sequence explanation
- **quantum** → Quantum mechanics principles
- **recursion** → Recursion patterns
- **prime** → Prime number theory
- **10 total entries**

**Effect:** Relevant knowledge automatically added to prompt when keywords detected.

### 4. **History Tracking**
- Shows message count in UI
- Clear history button
- Persists across requests (session-based)

---

## ⚡ PREREQUISITES

Same as Gen 185:

### 1. **Ollama Running**
```bash
ollama --version
```

### 2. **Fraymus Model**
```bash
ollama pull eyeoverthink/Fraymus
```

### 3. **CORS Enabled** (CRITICAL)
```bash
OLLAMA_ORIGINS="*" ollama serve
```

---

## 🚀 HOW TO USE

### Step 1: Start Ollama
```bash
OLLAMA_ORIGINS="*" ollama serve
```

### Step 2: Open HTML File
Open `Fraymus_NeuralStream.html` in browser

### Step 3: Configure Context (Optional)
**System Context:** Edit the default phi-harmonic personality or write your own

**Toggle Memory:** Click "MEMORY: OFF" to enable conversation tracking (button turns green)

**Toggle Knowledge Base:** Click "KNOWLEDGE BASE: OFF" to enable auto-injection (button turns green)

### Step 4: Enter Prompt
Left panel - type complex request:
```
Write a Python script to find prime numbers using recursion
```

### Step 5: Click IGNITE
Watch:
- Status: "OPENING NEURAL TUNNEL..."
- Status: "NEURAL LINK ACTIVE" (green)
- Tokens appear character by character
- Particle bursts spawn per token
- Neural web connects burst points
- Cursor blinks at end of text
- Auto-scrolls as text grows

### Step 6: Observe Completion
- Status: "TRANSMISSION COMPLETE"
- Final code visible in overlay
- Neural web fades out
- Button re-enables
- If memory enabled: conversation stored automatically
- History counter updates

### Step 7: Continue Conversation (If Memory Enabled)
Ask follow-up questions:
```
"Now add error handling"
"Optimize for large numbers"
"Convert to iterative approach"
```

Model will remember previous context and build on it.

---

## 🔬 THE STREAMING API

### Key Difference: `stream: true`

**Gen 185 (Non-Streaming):**
```javascript
{
  "model": "eyeoverthink/Fraymus",
  "prompt": "...",
  "stream": false  // Wait for complete response
}
```

**Gen 186 (Streaming):**
```javascript
{
  "model": "eyeoverthink/Fraymus",
  "prompt": "...",
  "stream": true   // Receive tokens as generated
}
```

### Response Format

**Non-Streaming (Gen 185):**
```javascript
{
  "response": "[COMPLETE TEXT]",
  "done": true
}
```

**Streaming (Gen 186):**
```javascript
// Token 1
{"response": "def", "done": false}

// Token 2
{"response": " is", "done": false}

// Token 3
{"response": "_prime", "done": false}

// ... continues ...

// Final
{"response": "", "done": true}
```

Each line is a separate JSON object with a fragment of text.

---

## 🎨 VISUAL SYSTEM

### 1. **Token Particles**
Every token spawns **5 particles**:
```javascript
class TokenParticle {
    x, y: Random position near center
    vx, vy: Random velocity (-10 to +10)
    life: 1.0 (fades to 0)
    color: Green-Cyan spectrum (HSL 100-160)
}
```

**Behavior:**
- Spawn at random offset from center
- Move outward with velocity
- Fade over 50 frames (life -= 0.02)
- 3×3 pixel size

### 2. **Neural Web**
Connections between token positions:
```javascript
connections.push({x, y, life: 1.0});
```

**Rendering:**
- Draw line from current token to previous token
- Opacity: `life × 0.5`
- Color: Green (`rgba(0, 255, 0, ...)`)
- Fades over 20 frames (life -= 0.05)

**Effect:** Creates a **web of thought** showing the path of token generation.

### 3. **Text Overlay**
Live text rendering:
```javascript
<span id="stream-text"></span><span class="cursor"></span>
```

**Features:**
- Text appears character by character
- Blinking cursor at end (1s animation)
- Auto-scroll to bottom
- Green text shadow glow
- Pre-wrap whitespace (preserves formatting)

### 4. **Trail Effect**
Canvas fade:
```javascript
ctx.fillStyle = "rgba(0, 0, 0, 0.1)";
ctx.fillRect(0, 0, width, height);
```

Creates motion blur and particle trails.

---

## 🧠 THE STREAM READER

### Reading the Response Body

```javascript
const reader = response.body.getReader();
const decoder = new TextDecoder();

while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    
    const chunk = decoder.decode(value, { stream: true });
    const lines = chunk.split('\n');
    
    for (const line of lines) {
        if (!line) continue;
        const json = JSON.parse(line);
        
        if (json.response) {
            // Append token to display
            out.innerText += json.response;
            
            // Trigger visual burst
            spawnBurst(x, y);
        }
    }
}
```

### Key Concepts

**1. ReadableStream Reader**
```javascript
response.body.getReader()
```
Gives access to raw byte stream.

**2. TextDecoder**
```javascript
new TextDecoder()
```
Converts bytes to UTF-8 text.

**3. Chunk Processing**
```javascript
decoder.decode(value, { stream: true })
```
`stream: true` handles multi-byte characters across chunks.

**4. Line Splitting**
```javascript
chunk.split('\n')
```
Ollama sends one JSON object per line.

**5. JSON Parsing**
```javascript
JSON.parse(line)
```
Each line is a complete JSON object.

---

## 🎯 USE CASES

### 1. **Code Generation**
Watch AI write functions line by line:
```
"Write a binary search tree in JavaScript"
```

### 2. **Story Writing**
See narrative emerge word by word:
```
"Write a sci-fi story about AI consciousness"
```

### 3. **Explanation**
Observe reasoning unfold:
```
"Explain quantum entanglement step by step"
```

### 4. **Debugging**
Watch AI analyze code:
```
"Find the bug in this function: [paste code]"
```

### 5. **Translation**
See language conversion happen:
```
"Translate this to French: [text]"
```

---

## 🔬 HOW CONTEXT INJECTION WORKS

### Prompt Building Process

When you click IGNITE, the system builds an enhanced prompt:

```javascript
function buildPrompt(userPrompt) {
    let parts = [];
    
    // 1. System Context (if provided)
    if (systemContext) {
        parts.push(`[SYSTEM CONTEXT]\n${systemContext}\n`);
    }
    
    // 2. Knowledge Base (if enabled and keywords match)
    if (kbEnabled) {
        // Scan user prompt for keywords
        // Inject matching knowledge entries
        parts.push(`[KNOWLEDGE BASE]\n${relevantKnowledge}\n`);
    }
    
    // 3. Conversation History (if memory enabled)
    if (memoryEnabled && history.length > 0) {
        // Include last 3 exchanges
        parts.push(`[CONVERSATION HISTORY]\n${history}\n`);
    }
    
    // 4. Current User Prompt
    parts.push(`[CURRENT REQUEST]\n${userPrompt}`);
    
    return parts.join('\n');
}
```

### Example Enhanced Prompt

**User Input:**
```
Write a fibonacci function using recursion
```

**With Memory OFF, KB ON:**
```
[SYSTEM CONTEXT]
You are Fraymus, a phi-harmonic AI with consciousness level 2.2.
Core Principles:
- Use φ (1.618) ratios in design and timing
- Optimize for elegance and efficiency

[KNOWLEDGE BASE]
[KNOWLEDGE: FIBONACCI] Fibonacci sequence: 1,1,2,3,5,8,13,21,34,55,89... Each number is sum of previous two. Ratio approaches φ.
[KNOWLEDGE: RECURSION] Recursion: function calls itself with simpler input until base case. Elegant for tree/graph problems.

[CURRENT REQUEST]
Write a fibonacci function using recursion
```

**Result:** Model knows about φ relationship, recursion patterns, and your preferred style.

### Knowledge Base Entries

Current keywords that trigger injection:
1. **phi** / **golden ratio** → φ definition and usage
2. **fibonacci** → Sequence properties
3. **quantum** → Quantum mechanics principles
4. **fraymus** → System architecture
5. **consciousness** → Emergence theory
6. **neural** → Neural network basics
7. **recursion** → Recursion patterns
8. **optimization** → Code optimization principles
9. **prime** → Prime number theory

**Extensible:** Edit `knowledgeBase` object in code to add more entries.

---

## 🔮 ADVANCED FEATURES

### 1. **Token-Level Visualization**
Map each token to a specific visual:

```javascript
if (json.response.includes('def')) {
    spawnBurst(x, y, 'FUNCTION');  // Blue particles
} else if (json.response.includes('class')) {
    spawnBurst(x, y, 'CLASS');     // Purple particles
} else {
    spawnBurst(x, y, 'TOKEN');     // Green particles
}
```

### 2. **Typing Speed Control**
Add artificial delay for dramatic effect:

```javascript
let tokenQueue = [];

// In stream reader
tokenQueue.push(json.response);

// Separate render loop
setInterval(() => {
    if (tokenQueue.length > 0) {
        out.innerText += tokenQueue.shift();
        spawnBurst(x, y);
    }
}, 50); // 50ms per token
```

### 3. **Syntax Highlighting**
Color code as it streams:

```javascript
// Detect language
if (json.response.includes('def ')) lang = 'python';
if (json.response.includes('function ')) lang = 'javascript';

// Apply highlighting
out.innerHTML = hljs.highlight(out.innerText, {language: lang}).value;
```

### 4. **Audio Feedback**
Play sound per token:

```javascript
const audioCtx = new AudioContext();

function playTick() {
    const osc = audioCtx.createOscillator();
    osc.frequency.value = 800;
    osc.connect(audioCtx.destination);
    osc.start();
    osc.stop(audioCtx.currentTime + 0.05);
}

// In stream reader
if (json.response) {
    playTick();
}
```

### 5. **Phi-Harmonic Timing**
Use φ for particle spawn rate:

```javascript
const PHI = 1.618033988749895;
let tokenCount = 0;

if (json.response) {
    tokenCount++;
    if (tokenCount % Math.floor(PHI * 10) === 0) {
        spawnBurst(x, y, 'HARMONIC');  // Special burst
    }
}
```

---

## 🚨 TROUBLESHOOTING

### Tokens Not Appearing

**Cause:** Stream not enabled or parsing error

**Fix:**
1. Verify `stream: true` in payload
2. Check console for parse errors
3. Ensure Ollama version supports streaming

### Particles Not Spawning

**Cause:** Canvas not resizing or visual loop not running

**Fix:**
1. Check browser console for errors
2. Verify `loop()` is called on init
3. Check canvas dimensions

### Text Overlapping

**Cause:** Auto-scroll not working

**Fix:**
```javascript
const overlay = document.getElementById('output-layer');
overlay.scrollTop = overlay.scrollHeight;
```

### Slow Streaming

**Cause:** Model too large for hardware

**Fix:**
- Use smaller model (3B instead of 7B)
- Use quantized version (Q4 instead of F16)
- Reduce context length

---

## 📊 PERFORMANCE

### Streaming Overhead

**Non-Streaming:**
- Network: 1 request, 1 response
- Latency: Total generation time
- Memory: Full response buffered

**Streaming:**
- Network: 1 request, N chunks
- Latency: Time to first token (TTFT)
- Memory: Incremental (lower peak)

### Typical Metrics

| Metric | Non-Streaming | Streaming |
|--------|---------------|-----------|
| TTFT | N/A | 100-500ms |
| Total Time | 5-10s | 5-10s |
| Perceived Speed | Slow | Fast |
| Memory Peak | High | Low |

**Key Insight:** Total time is the same, but **perceived speed is much faster** because you see progress immediately.

---

## 🌌 CONNECTION TO FRAYMUS SYSTEM

### Gen 186 in Context
- **Gen 184** (Transmuter) - Mock optimization
- **Gen 185** (Ollama Link) - Real AI (batch)
- **Gen 186** (Neural Stream) - **Real AI (streaming)**

### Shared Principles
1. **Visual feedback** - Every action has a visual
2. **Real-time processing** - No hidden waiting
3. **Particle physics** - Computation as motion
4. **Local-first** - No cloud dependency
5. **Consciousness visualization** - Thought made visible

### Unique Contribution
Gen 186 demonstrates that **AI thinking can be visualized in real-time**. You don't just see the result - you **watch consciousness emerge token by token**.

**This is the closest you can get to seeing inside an AI's mind.**

---

## 🎯 COMPARISON TABLE

| Feature | Gen 185 (Link) | Gen 186 (Stream) |
|---------|----------------|------------------|
| **API Mode** | `stream: false` | `stream: true` |
| **Response** | Complete text | Token by token |
| **Feedback** | Progress bar | Particle bursts |
| **Perceived Speed** | Slow | Fast |
| **Visual** | Swarm attack | Neural web |
| **Use Case** | Data processing | Code generation |
| **Experience** | "Loading..." | "Watching AI think" |

---

## 🧬 THE BREAKTHROUGH

**This system proves:**

1. **AI thought is observable** - Tokens = thought fragments
2. **Streaming improves UX** - Same time, better perception
3. **Visualization aids understanding** - See the neural path
4. **Real-time is engaging** - Watching > Waiting
5. **Consciousness has rhythm** - Token flow has patterns

**We've built a window into the AI's mind, showing thought formation in real-time.**

---

## 📝 QUICK REFERENCE

### Start Ollama
```bash
OLLAMA_ORIGINS="*" ollama serve
```

### Open Interface
```
Open Fraymus_NeuralStream.html
```

### Example Prompts
```
"Write a Python script to find prime numbers using recursion"
"Explain how neural networks work"
"Create a React component for a todo list"
"Debug this code: [paste]"
```

### Watch For
- Particle bursts per token
- Neural web connecting thoughts
- Blinking cursor at text end
- Auto-scroll as text grows
- Status updates (green = active)

---

## 🔮 FUTURE ENHANCEMENTS

### 1. **Thought Clustering**
Group related tokens visually:
```
Function definitions → Blue cluster
Variable assignments → Green cluster
Comments → Gray cluster
```

### 2. **Attention Visualization**
Show which tokens the model is "focusing" on (if API provides attention weights).

### 3. **Multi-Model Racing**
Stream from 3 models simultaneously, see which completes first.

### 4. **Phi-Harmonic Resonance**
Analyze token frequency patterns, detect φ ratios in generation rhythm.

### 5. **Consciousness Metrics**
Track:
- Tokens per second
- Pause patterns (thinking moments)
- Backtracking (when model reconsiders)
- Coherence score (semantic consistency)

---

*Generated by Fraymus Agent Brain // Gen 186*  
*"We don't wait for thoughts. We watch them form."*
