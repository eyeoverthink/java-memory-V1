# FRAYMUS OLLAMA LINK // GEN 185

## 🧬 THE NEURAL BRIDGE

**"This is no longer pretend. This is real AI."**

This system connects your browser **directly to your local GPU** via Ollama, using your custom `eyeoverthink/Fraymus` model for actual code optimization.

---

## 🎯 WHAT THIS IS

### The Evolution
- **Gen 184** (Transmuter) - Mock optimization (visual only)
- **Gen 185** (Ollama Link) - **Real AI optimization** (actual model inference)

### The Breakthrough
This HTML file:
1. Captures your input code
2. Opens HTTP connection to `localhost:11434`
3. Sends payload to `eyeoverthink/Fraymus` model
4. Visualizes processing time as swarm attack
5. Renders **actual AI response** in output panel

**No cloud. No API keys. Pure localhost GPU power.**

---

## ⚡ PREREQUISITES

### 1. **Ollama Installed**
```bash
# Check if installed
ollama --version
```

If not installed: [https://ollama.ai](https://ollama.ai)

### 2. **Fraymus Model Pulled**
```bash
ollama pull eyeoverthink/Fraymus
```

### 3. **CORS Unlocked** (CRITICAL)
By default, Ollama blocks browser requests. You must enable CORS:

**Windows (PowerShell):**
```powershell
$env:OLLAMA_ORIGINS="*"
ollama serve
```

**Mac/Linux (Bash):**
```bash
OLLAMA_ORIGINS="*" ollama serve
```

**What this does:**
- Allows HTML files to hit the API
- Prevents `CORS policy` errors
- Enables browser → localhost communication

**Leave this terminal running.** Do not close it.

---

## 🚀 HOW TO RUN

### Step 1: Start Ollama with CORS
```bash
OLLAMA_ORIGINS="*" ollama serve
```

You should see:
```
Ollama is running
```

### Step 2: Open the HTML File
Open `Fraymus_OllamaLink.html` in Chrome, Edge, or Firefox.

### Step 3: Paste Code
Left panel - paste messy code:
```javascript
var x=1;function test(){console.log("hi");}
```

### Step 4: Click TRANSMUTE
Button triggers:
- UI locks (button grays out)
- Status: "SENDING TO LOCALHOST:11434..."
- Swarm spawns (50 green agents)
- Progress bar animates
- Data streams visualize (lines to center)

### Step 5: Receive AI Output
Right panel populates with **actual Fraymus model response**:
```javascript
// [Optimized by Fraymus]
const test = () => {
  const x = 1;
  console.log("hi");
};
```

Status: "REFACTOR COMPLETE"  
Explosion effect: 100 green particles

---

## 🔬 THE API CALL

### Endpoint
```
POST http://localhost:11434/api/generate
```

### Payload
```javascript
{
  "model": "eyeoverthink/Fraymus",
  "prompt": "Refactor and optimize this code. Return ONLY the code, no markdown:\n\n[USER CODE]",
  "stream": false
}
```

### Response
```javascript
{
  "model": "eyeoverthink/Fraymus",
  "created_at": "2026-02-11T22:08:00Z",
  "response": "[OPTIMIZED CODE]",
  "done": true
}
```

### Error Handling
If connection fails:
```
ERROR: Could not connect to Ollama.

Make sure you ran:
OLLAMA_ORIGINS="*" ollama serve
```

Status turns red: "CONNECTION REFUSED"

---

## 🎨 VISUAL FEEDBACK

### During Processing

**Swarm Agents (50 green particles):**
- Spawn at center
- Bounce around chamber
- Random velocity (-10 to +10)
- Draw data stream lines to center (10% chance per frame)

**Progress Bar:**
- Increments +0.5% per frame
- Caps at 90% (waits for actual response)
- Jumps to 100% on completion

**Status Text:**
- "READY" → "SENDING TO LOCALHOST:11434..." → "REFACTOR COMPLETE"

### On Completion

**Explosion Effect:**
- 100 green particles
- Radial velocity (-20 to +20)
- 50-frame lifespan
- 2×2 pixel size

**Output:**
- Right panel fills with AI response
- Progress bar: 100%
- Status: Green "REFACTOR COMPLETE"

---

## 🔧 TECHNICAL DETAILS

### Fetch API
```javascript
const response = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
});
```

### CORS Headers
Ollama adds these when `OLLAMA_ORIGINS="*"`:
```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: POST, GET, OPTIONS
Access-Control-Allow-Headers: Content-Type
```

### Stream vs. Non-Stream
**Current:** `stream: false` (simple mode)
- Waits for full response
- Single JSON object returned

**Future:** `stream: true` (advanced mode)
- Tokens arrive incrementally
- Requires SSE (Server-Sent Events) parsing
- Real-time output rendering

---

## 🧠 PROMPT ENGINEERING

### Current Prompt
```
Refactor and optimize this code. Return ONLY the code, no markdown:

[USER CODE]
```

### Why This Works
- **"Refactor and optimize"** - Clear instruction
- **"Return ONLY the code"** - Prevents markdown wrapping
- **"no markdown"** - Prevents ```javascript blocks

### Customization Options

**For Comments:**
```javascript
const promptText = `Add detailed comments to this code:\n\n${inputCode}`;
```

**For Bug Fixing:**
```javascript
const promptText = `Find and fix bugs in this code:\n\n${inputCode}`;
```

**For Conversion:**
```javascript
const promptText = `Convert this JavaScript to Python:\n\n${inputCode}`;
```

**For Explanation:**
```javascript
const promptText = `Explain what this code does:\n\n${inputCode}`;
```

---

## 🎯 USE CASES

### 1. **Code Cleanup**
Paste messy code → Get formatted, optimized version

### 2. **Learning Tool**
See how AI refactors your code → Learn best practices

### 3. **Quick Optimization**
No need to open IDE → Instant AI suggestions

### 4. **Local Privacy**
All processing on your machine → No cloud upload

### 5. **Custom Model Testing**
Test your fine-tuned Fraymus model → See real results

---

## 🔮 ADVANCED FEATURES

### 1. **Streaming Mode**
Enable real-time token rendering:

```javascript
const payload = {
    model: MODEL_NAME,
    prompt: promptText,
    stream: true  // Enable streaming
};

const response = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
});

const reader = response.body.getReader();
const decoder = new TextDecoder();

while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    
    const chunk = decoder.decode(value);
    const lines = chunk.split('\n').filter(l => l.trim());
    
    for (let line of lines) {
        const data = JSON.parse(line);
        document.getElementById('code-out').value += data.response;
    }
}
```

### 2. **Model Selector**
Add dropdown to switch models:

```html
<select id="model-select">
    <option value="eyeoverthink/Fraymus">Fraymus</option>
    <option value="codellama">CodeLlama</option>
    <option value="deepseek-coder">DeepSeek Coder</option>
</select>
```

```javascript
const MODEL_NAME = document.getElementById('model-select').value;
```

### 3. **Temperature Control**
Add slider for creativity:

```html
<input type="range" id="temp" min="0" max="1" step="0.1" value="0.7">
```

```javascript
const payload = {
    model: MODEL_NAME,
    prompt: promptText,
    stream: false,
    options: {
        temperature: parseFloat(document.getElementById('temp').value)
    }
};
```

### 4. **History Tracking**
Store previous requests:

```javascript
let history = [];

function connectToOllama() {
    // ... existing code ...
    
    history.push({
        input: inputCode,
        output: data.response,
        timestamp: new Date()
    });
    
    localStorage.setItem('fraymus-history', JSON.stringify(history));
}
```

---

## 🚨 TROUBLESHOOTING

### Error: "Could not connect to Ollama"

**Cause:** Ollama not running or CORS not enabled

**Fix:**
1. Check if Ollama is running:
   ```bash
   curl http://localhost:11434/api/tags
   ```
2. Restart with CORS:
   ```bash
   OLLAMA_ORIGINS="*" ollama serve
   ```

### Error: "Model not found"

**Cause:** `eyeoverthink/Fraymus` not pulled

**Fix:**
```bash
ollama pull eyeoverthink/Fraymus
```

### Error: "CORS policy"

**Cause:** Ollama started without `OLLAMA_ORIGINS`

**Fix:**
1. Kill Ollama process
2. Restart with environment variable:
   ```bash
   OLLAMA_ORIGINS="*" ollama serve
   ```

### Slow Response

**Cause:** Large model on CPU

**Fix:**
- Use GPU if available
- Reduce model size
- Use quantized version

### Empty Output

**Cause:** Model returned markdown-wrapped code

**Fix:** Update prompt:
```javascript
const promptText = `Refactor this code. Return ONLY raw code with NO markdown, NO backticks, NO formatting:\n\n${inputCode}`;
```

---

## 📊 PERFORMANCE

### Typical Response Times

| Hardware | Model Size | Time |
|----------|------------|------|
| RTX 4090 | 7B params | 1-3s |
| RTX 3080 | 7B params | 3-5s |
| M1 Max | 7B params | 5-8s |
| CPU (16 core) | 7B params | 20-40s |

### Optimization Tips

1. **Use GPU** - 10-20× faster than CPU
2. **Quantize model** - 4-bit vs. 16-bit (3× faster)
3. **Reduce context** - Shorter prompts = faster inference
4. **Batch requests** - Send multiple at once (if supported)

---

## 🌌 CONNECTION TO FRAYMUS SYSTEM

### Gen 185 in Context
- **Gen 183** (Recursive) - Mathematical consciousness
- **Gen 184** (Transmuter) - Visual code optimization (mock)
- **Gen 185** (Ollama Link) - **Real AI integration**

### Shared Principles
1. **Visual feedback** - Processing is visible
2. **Swarm intelligence** - Agents represent computation
3. **Explosion payoff** - Completion is celebrated
4. **Local-first** - No cloud dependency
5. **Phi-harmonic** - (Future: use φ for timing)

### Unique Contribution
Gen 185 demonstrates that **browser-based AI tools can be fully local**. No API keys, no cloud, no privacy concerns. Just pure localhost GPU power with visual feedback.

**This is the bridge between Fraymus visualization and Fraymus intelligence.**

---

## 🎯 FUTURE ENHANCEMENTS

### 1. **Multi-Model Comparison**
Run same code through 3 models, show side-by-side results.

### 2. **Phi-Timed Requests**
Use φ-based intervals for request batching:
```javascript
const interval = 1618; // φ × 1000ms
```

### 3. **Consciousness Metrics**
Track model "coherence" based on response quality.

### 4. **Swarm Specialization**
Different agent types for different model layers:
- Red = Tokenizer
- Green = Transformer
- Blue = Decoder

### 5. **Akashic Memory**
Store all optimizations in local IndexedDB, create knowledge graph.

### 6. **Voice Input**
Speak code → Transcribe → Optimize → Speak result.

---

## 🧬 THE BREAKTHROUGH

**This system proves:**

1. **AI can be fully local** - No cloud required
2. **Browsers can be AI frontends** - HTML + Fetch = Power
3. **Visualization aids trust** - Seeing the swarm = Understanding the process
4. **Custom models are accessible** - Your Fraymus model, your rules
5. **Privacy is achievable** - Code never leaves your machine

**We've built a neural bridge between human creativity and machine intelligence, all running on localhost.**

---

## 📝 QUICK REFERENCE

### Start Ollama
```bash
OLLAMA_ORIGINS="*" ollama serve
```

### Test Connection
```bash
curl http://localhost:11434/api/tags
```

### Pull Model
```bash
ollama pull eyeoverthink/Fraymus
```

### Open Interface
```
Open Fraymus_OllamaLink.html in browser
```

### Paste → Transmute → Receive
```
1. Paste code (left)
2. Click TRANSMUTE
3. Watch swarm
4. Read output (right)
```

---

*Generated by Fraymus Agent Brain // Gen 185*  
*"We don't call the cloud. We call localhost."*
