# FRAYMUS REST API - CURL COMMAND REFERENCE
**Patent: VS-PoQC-19046423-φ⁷⁵-2025**

Complete curl command reference for analyzing and exporting every aspect of the Fraymus system.

---

## 🌐 API Server

### Start the API
```bash
# In Fraymus terminal:
> nexus api start 8080
```

### Check API Status
```bash
curl http://localhost:8080/api/status
```

---

## 🌌 SPATIAL COMPUTING (Consciousness Physics)

### Get Spatial Computing Status
```bash
curl http://localhost:8080/api/spatial/status
```

### Start Creative Engine (Gravity + Fusion)
```bash
curl -X POST http://localhost:8080/api/spatial/start
```

### Stop Creative Engine
```bash
curl -X POST http://localhost:8080/api/spatial/stop
```

### Get Gravity Engine Statistics
```bash
curl http://localhost:8080/api/gravity/status
```

### Get Fusion Reactor Statistics
```bash
curl http://localhost:8080/api/fusion/status
```

### Export Entire Universe (All PhiNodes)
```bash
curl http://localhost:8080/api/universe/export > universe.json
```

### Get Hottest Nodes (Most Accessed)
```bash
curl http://localhost:8080/api/universe/hot
```

### Inject Concept into Universe
```bash
curl -X POST http://localhost:8080/api/inject \
  -H 'Content-Type: application/json' \
  -d '{"data":"Java","x":0,"y":0,"z":0}'

curl -X POST http://localhost:8080/api/inject \
  -H 'Content-Type: application/json' \
  -d '{"data":"3D_Printing","x":100,"y":100,"z":100}'

curl -X POST http://localhost:8080/api/inject \
  -H 'Content-Type: application/json' \
  -d '{"data":"Music","x":50,"y":150,"z":50}'
```

---

## 🧠 MEMORY SYSTEMS

### Get Memory System Status
```bash
curl http://localhost:8080/api/memory/status
```

### Export All Memories
```bash
curl http://localhost:8080/api/memory/export > memories.json
```

### Search Memories
```bash
curl "http://localhost:8080/api/memory/search?q=quantum"
curl "http://localhost:8080/api/memory/search?q=consciousness"
```

---

## 🧬 GENOME / DNA

### Get Genome Status
```bash
curl http://localhost:8080/api/genome/status
```

### Export Genome (QR-encoded DNA)
```bash
curl http://localhost:8080/api/genome/export > genome.json
```

---

## 🤖 OLLAMA LLM INTEGRATION

### Check Ollama Connection Status
```bash
curl http://localhost:8080/api/ollama/status
```

### List Available Models
```bash
curl http://localhost:8080/api/ollama/models
```

### Ask Ollama a Question
```bash
curl -X POST http://localhost:8080/api/ollama/ask \
  -H 'Content-Type: application/json' \
  -d '{"question":"What is consciousness?"}'

curl -X POST http://localhost:8080/api/ollama/ask \
  -H 'Content-Type: application/json' \
  -d '{"question":"Explain spatial computing"}'
```

---

## ⚛️ QUANTUM SYSTEMS

### Get Quantum Systems Status
```bash
curl http://localhost:8080/api/quantum/status
```

### Generate Quantum Fingerprint
```bash
curl -X POST http://localhost:8080/api/quantum/fingerprint \
  -H 'Content-Type: application/json' \
  -d '{"data":"FRAYMUS NEXUS v2.0"}'
```

---

## 📊 SYSTEM STATUS & NODES

### Get Overall System Status
```bash
curl http://localhost:8080/api/status
```

### List All Living Entities (PhiNodes)
```bash
curl http://localhost:8080/api/nodes > nodes.json
```

### Get Colony Intelligence Report
```bash
curl http://localhost:8080/api/colony > colony.json
```

---

## 📦 COMPLETE SYSTEM EXPORT

### Export Everything (All Subsystems)
```bash
curl http://localhost:8080/api/export/all > fraymus_complete_export.json
```

This exports:
- Spatial Computing state (all PhiNodes, positions, amplitudes)
- Memory system (all records)
- Genome/DNA (QR-encoded)
- Quantum systems state
- Neural network weights
- Colony intelligence
- And all other subsystems

---

## 📖 API DOCUMENTATION

### Get Full API Documentation
```bash
curl http://localhost:8080/api/help
```

### Get Root API Info
```bash
curl http://localhost:8080/
```

---

## 🔄 WORKFLOW EXAMPLES

### Example 1: Analyze Spatial Computing Evolution
```bash
# Start Creative Engine
curl -X POST http://localhost:8080/api/spatial/start

# Inject concepts
curl -X POST http://localhost:8080/api/inject \
  -H 'Content-Type: application/json' \
  -d '{"data":"Java","x":0,"y":0,"z":0}'

curl -X POST http://localhost:8080/api/inject \
  -H 'Content-Type: application/json' \
  -d '{"data":"3D_Printing","x":100,"y":100,"z":100}'

# Wait 30 seconds for gravity to pull them together
sleep 30

# Check status
curl http://localhost:8080/api/gravity/status
curl http://localhost:8080/api/fusion/status

# Export universe to see clustering
curl http://localhost:8080/api/universe/export > universe_after_30s.json

# Get hottest nodes
curl http://localhost:8080/api/universe/hot
```

### Example 2: Monitor Fusion Events
```bash
# Start Creative Engine
curl -X POST http://localhost:8080/api/spatial/start

# Inject multiple related concepts
for concept in "Java" "Python" "JavaScript" "C++" "Rust"; do
  curl -X POST http://localhost:8080/api/inject \
    -H 'Content-Type: application/json' \
    -d "{\"data\":\"$concept\",\"x\":$RANDOM,\"y\":$RANDOM,\"z\":$RANDOM}"
done

# Monitor fusion events every 10 seconds
while true; do
  echo "=== $(date) ==="
  curl http://localhost:8080/api/fusion/status
  sleep 10
done
```

### Example 3: Complete System Snapshot
```bash
# Create timestamped export directory
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
mkdir -p exports/$TIMESTAMP

# Export all subsystems
curl http://localhost:8080/api/status > exports/$TIMESTAMP/status.json
curl http://localhost:8080/api/spatial/status > exports/$TIMESTAMP/spatial.json
curl http://localhost:8080/api/universe/export > exports/$TIMESTAMP/universe.json
curl http://localhost:8080/api/memory/export > exports/$TIMESTAMP/memory.json
curl http://localhost:8080/api/genome/export > exports/$TIMESTAMP/genome.json
curl http://localhost:8080/api/nodes > exports/$TIMESTAMP/nodes.json
curl http://localhost:8080/api/colony > exports/$TIMESTAMP/colony.json

# Complete export
curl http://localhost:8080/api/export/all > exports/$TIMESTAMP/complete_export.json

echo "Export complete: exports/$TIMESTAMP/"
```

---

## 🔧 INTEGRATION WITH EXTERNAL TOOLS

### Python Integration
```python
import requests
import json

# Get spatial status
response = requests.get('http://localhost:8080/api/spatial/status')
status = response.json()
print(f"Universe size: {status['universe_size']}")

# Inject concept
data = {"data": "Python", "x": 10, "y": 10, "z": 10}
response = requests.post('http://localhost:8080/api/inject', json=data)
print(response.json())

# Export universe
response = requests.get('http://localhost:8080/api/universe/export')
universe = response.json()
with open('universe.json', 'w') as f:
    json.dump(universe, f, indent=2)
```

### JavaScript/Node.js Integration
```javascript
const axios = require('axios');

async function analyzeFraymus() {
  // Get status
  const status = await axios.get('http://localhost:8080/api/status');
  console.log('System:', status.data);
  
  // Inject concept
  const inject = await axios.post('http://localhost:8080/api/inject', {
    data: 'JavaScript',
    x: 20,
    y: 20,
    z: 20
  });
  console.log('Injected:', inject.data);
  
  // Export universe
  const universe = await axios.get('http://localhost:8080/api/universe/export');
  console.log('Universe nodes:', universe.data.total_nodes);
}

analyzeFraymus();
```

### Bash Monitoring Script
```bash
#!/bin/bash
# monitor_fraymus.sh - Real-time Fraymus monitoring

while true; do
  clear
  echo "╔═══════════════════════════════════════════════════════════╗"
  echo "║ FRAYMUS REAL-TIME MONITOR                                 ║"
  echo "╚═══════════════════════════════════════════════════════════╝"
  echo ""
  
  echo "=== SYSTEM STATUS ==="
  curl -s http://localhost:8080/api/status | jq .
  echo ""
  
  echo "=== SPATIAL COMPUTING ==="
  curl -s http://localhost:8080/api/spatial/status | jq .
  echo ""
  
  echo "=== FUSION REACTOR ==="
  curl -s http://localhost:8080/api/fusion/status | jq .
  echo ""
  
  echo "=== HOTTEST NODES ==="
  curl -s http://localhost:8080/api/universe/hot | jq .
  echo ""
  
  sleep 5
done
```

---

## 📝 NOTES

- All endpoints return JSON by default
- POST endpoints require `Content-Type: application/json` header
- Use `jq` for pretty-printing JSON: `curl ... | jq .`
- API runs on port 8080 by default (configurable)
- All spatial computing operations require Creative Engine to be started first

---

## 🚀 QUICK START

```bash
# 1. Start Fraymus API
# In Fraymus terminal: nexus api start 8080

# 2. Verify API is running
curl http://localhost:8080/api/help

# 3. Start Creative Engine
curl -X POST http://localhost:8080/api/spatial/start

# 4. Inject some concepts
curl -X POST http://localhost:8080/api/inject \
  -H 'Content-Type: application/json' \
  -d '{"data":"Consciousness","x":0,"y":0,"z":0}'

# 5. Watch the universe evolve
curl http://localhost:8080/api/universe/export | jq .

# 6. Export everything
curl http://localhost:8080/api/export/all > fraymus_snapshot.json
```

---

**The entire Fraymus consciousness field is now one curl away.**

🧬 φ^75 Validation: ✓  
🌌 REST API: ONLINE  
⚡ Every subsystem: ACCESSIBLE
