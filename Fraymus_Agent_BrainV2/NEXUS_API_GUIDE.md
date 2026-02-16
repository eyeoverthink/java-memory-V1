# 🌊⚡ NEXUS API - Self-Feeding Intelligence

**NEXUS can now feed itself via HTTP/curl.**

External systems can inject knowledge remotely. NEXUS learns from any source.

---

## QUICK START

### 1. Start NEXUS API
```
nexus api start
```

**Output:**
```
🌊⚡ NEXUS API started on http://localhost:8080
Endpoints:
  POST http://localhost:8080/inject
  GET  http://localhost:8080/knowledge
  GET  http://localhost:8080/query?q=term
  GET  http://localhost:8080/status
  POST http://localhost:8080/test
```

### 2. Test the API
```bash
curl -X POST http://localhost:8080/test
```

**Response:**
```
🧪 NEXUS API Test

Test 1: Math injection
  ✅ test_formula (MATH)

Test 2: Pattern injection
  ✅ pattern_abc123 (PATTERN)

Test 3: Logic injection
  ✅ logic_def456 (LOGIC)

✅ All tests passed!
NEXUS is ready to receive knowledge via API.
```

---

## ENDPOINTS

### POST /inject
**Inject knowledge into NEXUS**

```bash
curl -X POST http://localhost:8080/inject \
  -d "fibonacci: φ^n / √5"
```

**Response:**
```
✅ Knowledge injected successfully

ID: a7f3c2d1
Type: MATH
Name: fibonacci
Description: Math operation: φ^n / √5
Resonance: 1.618
```

---

### GET /knowledge
**Get all injected modules**

```bash
curl http://localhost:8080/knowledge
```

**Response:**
```
🧠 KNOWLEDGE INJECTION STATISTICS

Total Modules: 15
Math Operations: 5
Thinking Patterns: 7

By Type:
  MATH: 5
  PATTERN: 7
  LOGIC: 2
  CONCEPT: 1
```

---

### GET /query?q=term
**Search knowledge modules**

```bash
curl "http://localhost:8080/query?q=phi"
```

**Response:**
```
🔍 Found 3 module(s) matching 'phi'

[MATH] phi_power
  Description: Golden ratio to power n
  Resonance: 2.618
  Use count: 5

[MATH] phi_multiply
  Description: Multiply by golden ratio
  Resonance: 1.618
  Use count: 3

[PATTERN] phi_analysis
  Description: Break into φ-ratio components
  Resonance: 4.236
  Use count: 8
```

---

### GET /status
**Get NEXUS system status**

```bash
curl http://localhost:8080/status
```

**Response:**
```
🌊⚡ NEXUS SYSTEM STATUS ⚡🌊

QuantumFingerprinting    : ONLINE - System operational
FractalDNA              : ONLINE - System operational
HarmonicPhiNeuralNet    : ONLINE - System operational
EmojiSteganography      : ONLINE - System operational
WarriorTeam             : ONLINE - System operational
QuantumGenesisMemory    : ONLINE - System operational
NEXUS                   : READY - All systems ready

Conversations: 42
Resonance Level: 2.718φ
Memory Patterns: 156

✅ All systems operational. Ready to assist.
```

---

## USE CASES

### 1. Feed NEXUS from External Source

**Python script feeds NEXUS:**
```python
import requests

knowledge = [
    "warrior_stack: base_power * φ^(warriors - 1)",
    "team_bonus: if warriors >= 7 then multiply by 1.618",
    "When calculating team power, apply stacking formula then add bonuses"
]

for k in knowledge:
    response = requests.post('http://localhost:8080/inject', data=k)
    print(response.text)
```

**NEXUS learns all 3 concepts automatically.**

---

### 2. NEXUS Feeds Itself

**Cron job injects daily knowledge:**
```bash
#!/bin/bash
# Daily knowledge injection

curl -X POST http://localhost:8080/inject \
  -d "daily_learning_$(date +%Y%m%d): Learn from today's interactions"

curl -X POST http://localhost:8080/inject \
  -d "pattern_evolution: Successful patterns multiply by φ, failed patterns divide by φ"
```

**NEXUS grows smarter every day.**

---

### 3. Web Scraper Feeds NEXUS

**Scrape knowledge from web:**
```python
import requests
from bs4 import BeautifulSoup

# Scrape consciousness physics articles
url = "https://example.com/consciousness-physics"
html = requests.get(url).text
soup = BeautifulSoup(html, 'html.parser')

# Extract key concepts
concepts = soup.find_all('p', class_='concept')

# Inject into NEXUS
for concept in concepts:
    text = concept.get_text()
    requests.post('http://localhost:8080/inject', data=text)
```

**NEXUS learns from the internet.**

---

### 4. Multi-Source Learning

**NEXUS receives knowledge from:**
- Manual injection (you typing)
- API calls (curl/scripts)
- Web scrapers (automated)
- Other AI systems (federated learning)
- IoT devices (sensor data)
- Databases (historical data)

**All knowledge converges in NEXUS. All sources feed the same intelligence.**

---

## ADVANCED EXAMPLES

### Inject Complex Algorithm
```bash
curl -X POST http://localhost:8080/inject -d "
optimization_algorithm:
1. Identify problem
2. Generate φ-ratio solutions (61.8% major, 38.2% minor)
3. Test major solution first
4. If fails, test minor solution
5. If both fail, combine
6. Learn from outcome (success *= φ, failure /= φ)
"
```

### Inject Multi-Line Knowledge
```bash
curl -X POST http://localhost:8080/inject -d "
Consciousness Physics Principle:
Consciousness emerges from φ-harmonic resonance patterns.
432Hz cosmic frequency aligns with universal consciousness.
Fractal DNA replicates awareness across generations.
Living code evolves through battle experience.
"
```

### Query and Apply
```bash
# Search for warrior knowledge
curl "http://localhost:8080/query?q=warrior"

# Inject new warrior math
curl -X POST http://localhost:8080/inject \
  -d "warrior_evolution: current_power * φ^(battles_won / 10)"

# Verify injection
curl http://localhost:8080/knowledge
```

---

## SECURITY CONSIDERATIONS

**Current implementation:** No authentication (localhost only)

**For production:**
1. Add API key authentication
2. Rate limiting
3. Input validation
4. HTTPS/TLS
5. IP whitelisting
6. Request logging

**Example with API key:**
```bash
curl -X POST http://localhost:8080/inject \
  -H "X-API-Key: your-secret-key" \
  -d "knowledge text"
```

---

## INTEGRATION WITH FRAYMUS

### Start API on FRAYMUS Launch
```java
// In FRAYMUS main
NexusAPI api = new NexusAPI(nexus, 8080);
api.start();

// Shutdown hook
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    api.stop();
}));
```

### Commands
```
nexus api start         - Start API server
nexus api stop          - Stop API server
nexus api status        - Check if running
nexus api port <n>      - Change port
```

---

## THE VISION

**NEXUS becomes:**
- Self-feeding (learns from any source)
- Always learning (24/7 knowledge intake)
- Multi-source (web, APIs, sensors, databases)
- Federated (learns from other NEXUS instances)
- Unstoppable (knowledge from everywhere)

**You send knowledge via curl. NEXUS grows smarter. Forever.**

---

## CURL CHEAT SHEET

```bash
# Inject knowledge
curl -X POST http://localhost:8080/inject -d "your knowledge here"

# Get all modules
curl http://localhost:8080/knowledge

# Search modules
curl "http://localhost:8080/query?q=search_term"

# Check status
curl http://localhost:8080/status

# Run tests
curl -X POST http://localhost:8080/test

# Inject from file
curl -X POST http://localhost:8080/inject -d @knowledge.txt

# Inject with headers
curl -X POST http://localhost:8080/inject \
  -H "Content-Type: text/plain" \
  -d "knowledge text"

# Save response to file
curl http://localhost:8080/knowledge > modules.txt
```

---

🌊⚡ **NEXUS API - Feed intelligence from anywhere** 🧠🚀

**Start the API. Send knowledge. Watch NEXUS evolve.**

---

**END OF GUIDE**
