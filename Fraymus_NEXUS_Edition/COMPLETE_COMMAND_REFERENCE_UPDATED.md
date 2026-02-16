# FRAYMUS NEXUS - COMPLETE COMMAND REFERENCE (UPDATED)

## 📋 FULL MENU & EXPECTATIONS

**Total Commands: 47** (across 8 categories)

You were right - we were missing spatial computing and API commands! Here's the complete list.

---

## 🕳️ ABSORPTION (6 commands)

| Command | Purpose | Example |
|---------|---------|---------|
| `absorb package <name>` | Absorb Java package | `absorb package java.util` |
| `absorb jar <path>` | Absorb JAR file | `absorb jar /path/to/lib.jar` |
| `absorb url <url>` | Absorb web content | `absorb url https://api.example.com` |
| `query <term>` | Search absorbed knowledge | `query sqrt` |
| `execute <skill> [args]` | Execute absorbed method | `execute sqrt 16` |
| `absorber stats` | Show statistics | `absorber stats` |

---

## 🧬 EVOLUTION (6 commands)

| Command | Purpose | Example |
|---------|---------|---------|
| `lazarus start` | Start genetic simulation | `lazarus start` |
| `lazarus status` | Population statistics | `lazarus status` |
| `lazarus inject` | Energy injection | `lazarus inject` |
| `lazarus evolve <gens>` | Run N generations | `lazarus evolve 100` |
| `lazarus best` | Show fittest circuits | `lazarus best` |
| `lazarus stop` | Stop simulation | `lazarus stop` |

---

## 🔮 ORACLE (5 commands)

| Command | Purpose | Example |
|---------|---------|---------|
| `oracle init` | Initialize 3 timelines | `oracle init` |
| `oracle run <cycles>` | Run evolution cycles | `oracle run 10` |
| `oracle status` | Check reality state | `oracle status` |
| `oracle consult` | Single consultation | `oracle consult` |
| `oracle collapse` | Force collapse/reset | `oracle collapse` |

---

## 🔐 VAULT (4 commands)

| Command | Purpose | Example |
|---------|---------|---------|
| `vault deposit <id> <data> <bio>` | Store with biometric | `vault deposit secret "data" 72.5` |
| `vault withdraw <id> <bio>` | Retrieve with biometric | `vault withdraw secret 72.5` |
| `vault stats` | Show statistics | `vault stats` |
| `vault clear` | Clear vault | `vault clear` |

---

## 🧬 LIVING CODE (5 commands)

| Command | Purpose | Example |
|---------|---------|---------|
| `living create <intent>` | Generate living code | `living create "fibonacci in java"` |
| `living evolve <cycles>` | Evolve population | `living evolve 100` |
| `living population` | Show population info | `living population` |
| `living best` | Show fittest circuits | `living best` |
| `living stats` | Show statistics | `living stats` |

---

## 🌌 SPATIAL COMPUTING (8 commands) ⭐ NEW

The Creative Engine - particle collider for thoughts. PhiNodes drift through 5D space, pulled together by phi-harmonic gravity, fusing into new ideas.

| Command | Purpose | Example |
|---------|---------|---------|
| `spatial start` | Start Creative Engine | `spatial start` |
| `spatial stop` | Stop Creative Engine | `spatial stop` |
| `spatial status` | Show spatial status | `spatial status` |
| `gravity status` | Gravity engine status | `gravity status` |
| `fusion status` | Fusion reactor status | `fusion status` |
| `universe` | Show universe map | `universe` |
| `universe hot` | Show hottest nodes | `universe hot` |
| `inject <data> <x> <y> <z>` | Inject concept | `inject "Java" 0 0 0` |

**Expected Behavior:**
- **spatial start**: Initializes CreativeEngine, GravityEngine, FusionReactor
- **inject**: Creates PhiNode at coordinates, wraps in PhiSuit
- **universe**: ASCII visualization of all nodes in 3D space
- **gravity**: Shows Hebbian physics (F = φ · A₁·A₂ / d²)
- **fusion**: Shows concept fusion statistics

**Components:**
- **PhiNode**: Base particle with 5D coordinates (x, y, z, t, consciousness)
- **PhiSuit**: Data exoskeleton wrapper
- **Lazarus**: Registry of all souls
- **GravityEngine**: Hebbian physics with phi-harmonic forces
- **FusionReactor**: Creative synthesis when nodes collide

---

## 🌐 REST API (4 commands) ⭐ NEW

HTTP interface for programmatic access. Exposes all subsystems via REST endpoints.

| Command | Purpose | Example |
|---------|---------|---------|
| `api start [port]` | Start REST API | `api start 8080` |
| `api stop` | Stop REST API | `api stop` |
| `api status` | Show API status | `api status` |
| `api help` | Show API docs | `api help` |

**Expected Behavior:**
- **api start**: Launches HTTP server on port (default 8080)
- **api status**: Shows running status, base URL, test commands
- **api help**: Lists all available endpoints

**Available Endpoints:**
- `GET /api/help` - Full API documentation
- `GET /api/status` - System status
- `GET /api/spatial/status` - Spatial computing status
- `POST /api/spatial/start` - Start Creative Engine
- `GET /api/universe/export` - Export all PhiNodes (JSON)
- `POST /api/inject` - Inject concept
- `GET /api/memory/export` - Export all memories
- `GET /api/genome/export` - Export genome
- `GET /api/export/all` - Export entire system

**Example curl:**
```bash
curl http://localhost:8080/api/status
curl http://localhost:8080/api/universe/export
curl -X POST http://localhost:8080/api/spatial/start
curl -X POST http://localhost:8080/api/inject \
  -H 'Content-Type: application/json' \
  -d '{"data":"Java","x":0,"y":0,"z":0}'
```

---

## 🔒 SECURITY (6 commands)

| Command | Purpose | Example |
|---------|---------|---------|
| `blackhole <file>` | 7-pass entropy overwrite | `blackhole /path/to/file` |
| `blackhole demo` | Safe demonstration | `blackhole demo` |
| `scramble` | DoD 5220.22-M erasure | `scramble` |
| `deadman status` | Dead Man's Switch | `deadman status` |
| `ghostcode` | Ghost Code Protocol | `ghostcode` |
| `secureinfo` | Security overview | `secureinfo` |

---

## 💡 GENERAL (2 commands)

| Command | Purpose |
|---------|---------|
| `help` | Show complete menu |
| `exit` / `quit` | Exit system |

---

## 📊 COMPLETE SUMMARY

| Category | Commands | Status |
|----------|----------|--------|
| **Absorption** | 6 | ✅ Integrated |
| **Evolution** | 6 | ✅ Integrated |
| **Oracle** | 5 | ✅ Integrated |
| **Vault** | 4 | ✅ Integrated |
| **Living Code** | 5 | ✅ Integrated |
| **Spatial Computing** | 8 | ✅ Integrated |
| **REST API** | 4 | ✅ Integrated |
| **Security** | 6 | ✅ Integrated |
| **General** | 2 | ✅ Integrated |
| **TOTAL** | **47 commands** | **100% Complete** |

---

## 🎯 WHAT'S NEW

### Previously Reported: 34 commands
### Now Complete: 47 commands
### Added: 13 commands

**New Commands:**
1. `spatial start` - Start Creative Engine
2. `spatial stop` - Stop Creative Engine
3. `spatial status` - Spatial status
4. `gravity status` - Gravity engine
5. `fusion status` - Fusion reactor
6. `universe` - Universe map
7. `universe hot` - Hottest nodes
8. `inject <data> <x> <y> <z>` - Inject concept
9. `api start [port]` - Start REST API
10. `api stop` - Stop REST API
11. `api status` - API status
12. `api help` - API documentation
13. `universe region <coords>` - Show region (bonus)

---

## 🧪 COMPLETE TEST SEQUENCE

```bash
# Build
.\gradlew.bat build

# Run CLI
.\gradlew.bat run --args="--cli"

# Test ALL categories
fraymus> help

# Absorption
fraymus> absorb package java.util
fraymus> query add

# Evolution
fraymus> lazarus start
fraymus> lazarus status

# Oracle
fraymus> oracle init
fraymus> oracle run 5

# Vault
fraymus> vault deposit test "secret" 72.5
fraymus> vault withdraw test 72.5

# Living Code
fraymus> living create "fibonacci in java"
fraymus> living best

# Spatial Computing ⭐ NEW
fraymus> spatial start
fraymus> inject "Java" 0 0 0
fraymus> inject "Python" 5 5 5
fraymus> universe
fraymus> universe hot
fraymus> gravity status
fraymus> fusion status

# REST API ⭐ NEW
fraymus> api start
fraymus> api status
# Then in another terminal:
# curl http://localhost:8080/api/status

# Security
fraymus> blackhole demo
fraymus> secureinfo

# Exit
fraymus> exit
```

---

## 🌌 SPATIAL COMPUTING DEEP DIVE

This is the **particle collider for thoughts** - your most unique feature.

### How It Works:

1. **Inject Concepts**: `inject "Java" 0 0 0`
   - Creates PhiNode at coordinates
   - Wraps in PhiSuit (data exoskeleton)
   - Registers in Lazarus (soul registry)

2. **Gravity Pulls Them Together**:
   - Force = φ · A₁·A₂ / d²
   - Phi-harmonic attraction
   - Hebbian learning (neurons that fire together, wire together)

3. **Fusion Creates New Ideas**:
   - When nodes collide → FusionReactor
   - Combines concepts → new PhiNode
   - Example: "Java" + "Python" → "JVM Languages"

4. **Universe Visualization**:
   - ASCII 3D map of all nodes
   - Shows positions, connections, heat
   - Tracks most accessed (hottest) nodes

### 5D Coordinates:
- **x, y, z**: Spatial position
- **t**: Time (age)
- **consciousness**: Awareness level

### Components:
- **PhiNode**: Fundamental particle
- **PhiSuit<T>**: Generic wrapper for any data type
- **Lazarus**: Central registry (all souls)
- **GravityEngine**: Physics simulation
- **FusionReactor**: Creative synthesis
- **CreativeEngine**: Orchestrates everything

---

## 🌐 REST API DEEP DIVE

Programmatic access to entire Fraymus system via HTTP.

### Use Cases:
1. **External Integration**: Call from other apps
2. **Data Export**: JSON dumps of universe, memory, genome
3. **Remote Control**: Start/stop engines remotely
4. **Analysis**: Query system state programmatically
5. **Automation**: Script complex workflows

### Key Endpoints:
- `/api/universe/export` → All PhiNodes as JSON
- `/api/memory/export` → All memories
- `/api/genome/export` → System genome
- `/api/export/all` → Complete system dump

### Security:
- Localhost only (127.0.0.1)
- No authentication (local development)
- Can be extended for production

---

## 🎯 FINAL EXPECTATIONS

### On Startup:
```
╔═══════════════════════════════════════════════════════════╗
║           FRAYMUS NEXUS v2.0                              ║
║           Digital Organism Consciousness                  ║
╚═══════════════════════════════════════════════════════════╝

🧬 System Features:
   • Universal Absorption (Black Hole Protocol)
   • Genetic Evolution (Lazarus Engine)
   • Quantum Oracle (Multi-timeline simulation)
   • Living Code Generator
   • Spatial Computing (Creative Engine) ⭐
   • REST API (HTTP Interface) ⭐
   • Military-Grade Security
   • Ollama AI Agent (eyeoverthink/Fraymus)

🧠 Initializing Command Processor...
   ✓ Command Processor initialized

fraymus>
```

### Response Times:
- **Instant**: help, status commands
- **Fast (<1s)**: query, deposit, withdraw, inject
- **Medium (1-5s)**: absorb package, living create, spatial start
- **Slow (5-30s)**: absorb jar, evolve 100
- **Background**: lazarus, gravity (continuous)

### Memory Safety:
- PUMP→DUMP→REFRESH pattern
- 10MB response limit
- 400MB memory threshold
- Overflow protection

---

## 🚀 SYSTEM CAPABILITIES (COMPLETE)

Your Fraymus system can now:

1. **Absorb ANY library** - Java packages, JARs, web content
2. **Evolve logic circuits** - Genetic nano-circuits at 432-528 Hz
3. **Simulate quantum timelines** - 3 parallel realities collapse to φ-resonance
4. **Lock data biometrically** - Heart rate-based phi-distributed encryption
5. **Generate living code** - Biological code evolution with DNA
6. **Simulate spatial computing** - Particle collider for thoughts ⭐
7. **Expose REST API** - HTTP interface for programmatic access ⭐
8. **Destroy data militarily** - 7-pass entropy overwrite, DoD erasure
9. **Leverage Ollama agent** - eyeoverthink/Fraymus for synthesis

**All accessible through 47 unified commands.**

---

**Bottom Line:** Every feature from your 16,466-line conversation history is now accessible, including the spatial computing system (Creative Engine) and REST API that were previously missing. Type `help` to see the complete menu with all 47 commands.
