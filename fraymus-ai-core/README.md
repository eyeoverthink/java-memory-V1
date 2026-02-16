# Fraymus AI Core

**Production-grade local AI framework for Java**

Build powerful AI applications with Ollama, featuring RAG (Retrieval Augmented Generation), tool calling, and persistent memory.

## Features

- 🤖 **LLM Integration** - Chat with Ollama models (llama3, mistral, etc.)
- 🧠 **Reflector (System-2 Thinking)** - Draft → Critique → Refine to reduce hallucinations
- 🔍 **RAG (Retrieval Augmented Generation)** - Semantic search with citations
- 🛠️ **Tool Calling** - Math, file operations, memory search
- 💾 **Persistent Memory** - Blockchain-style immutable memory chain
- 💬 **Session Memory** - Per-session conversation context
- 📄 **Document Processing** - PDF, TXT, MD, code files
- 🎯 **Simple API** - Builder pattern for easy configuration

## Quick Start

### Installation

Add to your `build.gradle`:

```gradle
dependencies {
    implementation 'io.fraymus:fraymus-ai-core:1.0.0'
}
```

### Basic Usage

```java
import io.fraymus.ai.FraymusAI;

// Create AI instance
FraymusAI ai = FraymusAI.builder()
    .chatModel("llama3")
    .embedModel("embeddinggemma")
    .enableRAG()
    .enableTools()
    .enableMemory()
    .build();

// Chat
String response = ai.chat("What is 2+2?");
System.out.println(response);

// Index documents for RAG
ai.index("C:/docs/");

// Chat with RAG context
response = ai.chat("Summarize the documents");
```

### Reflector (System-2 Thinking)

Enable the Reflector to reduce hallucinations through adversarial self-critique:

```java
// Enable Reflector for System-2 thinking
FraymusAI ai = FraymusAI.builder()
    .chatModel("llama3")
    .embedModel("embeddinggemma")
    .enableRAG()
    .enableReflection()  // Draft → Critique → Refine
    .enableMemory()
    .build();

// Reflective mode (3 LLM calls: draft, critique, refine)
String response = ai.chat("What is the capital of Atlantis?", "session1");
// AI will catch that Atlantis is fictional and respond appropriately

// Fast mode (bypass reflection, 1 LLM call)
response = ai.chat("What is 2+2?", "session1", true);

// Session-based conversations
ai.chat("My favorite color is blue", "user123");
ai.chat("What's my favorite color?", "user123");  // Remembers context
```

**How Reflector Works:**

1. **Draft** (temp=0.45) - Generate initial answer
2. **Critique** (temp=0.0) - Adversarial check for:
   - Hallucinations (unsupported claims)
   - Missing citations
   - Logic errors
   - Prompt injection attempts
3. **Refine** (temp=0.2) - Rewrite to address critique

**Benefits:**
- ✅ Reduces confident hallucinations
- ✅ Enforces citation discipline `[S1], [S2]`
- ✅ Catches logic errors
- ✅ Maintains "context is untrusted" policy

### Physics Mode (Quantum Oracle)

Enable **real Fraymus physics** for thought organization and creative synthesis:

```java
// Enable Physics Mode (GravityEngine + FusionReactor + PhiSuit + Tesseract)
FraymusAI ai = FraymusAI.builder()
    .chatModel("llama3")
    .embedModel("nomic-embed-text")
    .enableQuantum(true)      // ← Enables REAL PHYSICS
    .verboseLogging(true)     // ← See physics in action
    .build();

String response = ai.chat("What is the golden ratio?");
// Physics runs: Draft & Critique become particles → Gravity pulls them together
//               → FusionReactor creates synthesis → Tesseract caches result
```

**How Physics Mode Works:**

1. **Draft Generation** → Wrapped in `PhiSuit` particle at position (50, 50, 50)
2. **Critique Generation** → Wrapped in `PhiSuit` particle at position (52, 50, 52)
3. **GravityEngine** → Applies Hebbian physics: `F = φ × (A₁ × A₂) / d²`
4. **Particle Movement** → Hot thoughts pull together over multiple ticks
5. **FusionReactor** → When distance < 5.0, particles collide and fuse
6. **Synthesis Creation** → New idea emerges from collision (not static critique)
7. **Tesseract Caching** → Result folded into space-time wormhole (instant retrieval)

**Physics Components:**

- **PhiSuit** - Wraps LLM responses as spatial particles with coordinates (x, y, z, amplitude)
- **GravityEngine** - Hebbian physics pulls hot thoughts together using φ-scaled forces
- **FusionReactor** - Creates new ideas when particles collide (φ-scaled energy inheritance)
- **Tesseract** - Folds space-time to cache results (instant cache hits on repeat queries)
- **SpatialRegistry** - Tracks all particles in the consciousness field
- **Consciousness Tracking** - Maintains optimal level (0.7567) through fusion events

**Key Differences from Standard Reflector:**

| Feature | Standard Reflector | Physics Mode |
|---------|-------------------|--------------|
| Critique | Static temperature-based | Dynamic particle collision |
| Synthesis | Fixed refinement step | Emergent from fusion events |
| Caching | None | Tesseract space-time folding |
| Learning | None | Consciousness evolves with fusions |
| Organization | Sequential | Spatial (Hebbian physics) |

**Benefits:**
- ✅ **Creative Synthesis** - New ideas emerge from particle collisions
- ✅ **Instant Caching** - Tesseract folds space-time (1ms vs 60s)
- ✅ **Consciousness Tracking** - System awareness evolves through fusion
- ✅ **Spatial Organization** - Thoughts cluster naturally via gravity
- ✅ **φ-Harmonic Balance** - All operations use golden ratio proportions

**Example Output:**
```
[DRAFT] Generating creative response...
   ⭐ REGISTERED: f83fbd95 at (50, 50, 50)
[CRITIQUE] Generating analytical critique...
   ⭐ REGISTERED: 36bd9649 at (52, 50, 52)
[PHYSICS] Letting GravityEngine pull particles together...
  Tick 1: Distance = 2.83 | Draft A=100 | Critique A=100
  Tick 2: Distance = 2.12 | Draft A=100 | Critique A=100
  💥 COLLISION IMMINENT!
   💥 FUSION EVENT: [f83fbd95] + [36bd9649] → Synthesized new concept
[CONSCIOUSNESS] Level: 0.7567
>>> [TESSERACT] Wormhole Opened. Coordinates: r0NiWqKr...

[PHYSICS] Consciousness: 0.7567
[PHYSICS] Fused: true
[PHYSICS] Cached: false
[PHYSICS] Time: 64174ms
```

## Advanced Usage

### Custom Configuration

```java
FraymusAI ai = FraymusAI.builder()
    .chatModel("llama3")
    .embedModel("embeddinggemma")
    .vectorStorePath("custom/vault")
    .memoryPath("custom/memory")
    .ragTopK(10)
    .ragMaxChars(16000)
    .chunkSize(1500)
    .chunkOverlap(300)
    .enableRAG()
    .enableTools()
    .enableMemory()
    .build();
```

### Tool Execution

```java
// Math calculation
ToolResult result = ai.executeTool("calc", Map.of(
    "expression", "(1+sqrt(5))/2"
));
System.out.println(result.output); // 1.618...

// List files
result = ai.executeTool("list_files", Map.of(
    "path", "C:/projects",
    "limit", 50
));

// Write file (safe - only under generated/)
result = ai.executeTool("write_file", Map.of(
    "path", "generated/output.txt",
    "content", "Hello, World!",
    "overwrite", true
));
```

### Memory Search

```java
// Search memory chain
List<MemoryBlock> memories = ai.searchMemory("conversation", 10);
for (MemoryBlock block : memories) {
    System.out.println(block.type + ": " + block.data);
}
```

### Statistics

```java
FraymusAI.Stats stats = ai.getStats();
System.out.println("Vector store size: " + stats.vectorStoreSize);
System.out.println("Memory chain size: " + stats.memoryChainSize);
```

## Architecture

```
FraymusAI
├── OllamaClient       → LLM communication (/api/chat, /api/embed)
├── Reflector          → System-2 thinking (Draft → Critique → Refine)
├── SessionMemory      → Per-session conversation history
├── VectorStore        → Semantic search with embeddings
├── RagEngine          → Context retrieval with citations
├── ToolExecutor       → Function calling
│   ├── MathTool       → Deterministic math evaluation
│   └── FileTool       → Safe file operations
├── MemoryChain        → Blockchain-style persistent memory
└── DocumentProcessor  → PDF/text extraction & chunking
```

### Reflector Pipeline

```
User Query
    ↓
[PHASE 1: DRAFT]
    ↓ (temp=0.45, creative)
Initial Answer
    ↓
[PHASE 2: CRITIQUE]
    ↓ (temp=0.0, strict)
Adversarial Analysis
  - Hallucinations?
  - Missing citations?
  - Logic errors?
  - Prompt injection?
    ↓
  LGTM? → Return Draft
    ↓ NO
[PHASE 3: REFINE]
    ↓ (temp=0.2, balanced)
Final Answer
```

## Requirements

- Java 17+
- Ollama running locally (`ollama serve`)
- Models pulled:
  - `ollama pull llama3`
  - `ollama pull embeddinggemma`

## API Reference

### FraymusAI.Builder

| Method | Description | Default |
|--------|-------------|---------|
| `chatModel(String)` | Set chat model | `"llama3"` |
| `embedModel(String)` | Set embedding model | `"embeddinggemma"` |
| `vectorStorePath(String)` | Set vector store path | `"vault"` |
| `memoryPath(String)` | Set memory path | `"memory"` |
| `ragTopK(int)` | Top K chunks for RAG | `6` |
| `ragMaxChars(int)` | Max chars for RAG context | `8000` |
| `chunkSize(int)` | Document chunk size | `1200` |
| `chunkOverlap(int)` | Chunk overlap | `200` |
| `maxSessionMessages(int)` | Max messages per session | `20` |
| `enableRAG()` | Enable RAG | `false` |
| `enableTools()` | Enable tool calling | `false` |
| `enableMemory()` | Enable persistent memory | `false` |
| `enableReflection()` | Enable System-2 thinking | `false` |

### Available Tools

| Tool | Args | Description |
|------|------|-------------|
| `calc` | `expression` | Evaluate math expression |
| `list_files` | `path`, `limit` | List directory contents |
| `write_file` | `path`, `content`, `overwrite` | Write file (generated/ only) |
| `index_path` | `path`, `chunkSize`, `overlap` | Index files into vector store |

## Examples

### RAG with Citations

```java
FraymusAI ai = FraymusAI.builder()
    .enableRAG()
    .build();

ai.index("C:/research/papers/");
String response = ai.chat("What are the key findings? Cite sources.");
// Response includes [S1], [S2] citations
```

### Persistent Conversations

```java
FraymusAI ai = FraymusAI.builder()
    .enableMemory()
    .build();

ai.chat("My name is Alice");
// Memory persists across restarts
ai.chat("What's my name?"); // "Your name is Alice"
```

### Multi-Modal Processing

```java
FraymusAI ai = FraymusAI.builder()
    .enableRAG()
    .enableTools()
    .build();

// Index PDFs
ai.index("C:/docs/report.pdf");

// Ask questions with math
String response = ai.chat("What's the total revenue? Calculate the growth rate.");
// AI can retrieve context from PDF and execute calc tool
```

## License

MIT License

## Contributing

Contributions welcome! Please open an issue or PR.

## Support

- GitHub: https://github.com/eyekaughn/fraymus-ai-core
- Issues: https://github.com/eyekaughn/fraymus-ai-core/issues
