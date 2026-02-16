# FRAYMUS FEATURE AUDIT
## What's Been Requested vs What's In The Main App

**Last Audit:** Feb 10, 2026
**Status:** 65+ commands now accessible via terminal

---

## ✅ CONFIRMED IN MAIN APP (CommandTerminal.java)

These features have terminal commands and are wired up:

| Feature | Command | Status |
|---------|---------|--------|
| PhiNode entities | `spawn`, `kill`, `boost`, `mutate` | ✅ Working |
| Living World Simulation | `status`, `nodes`, `colony` | ✅ Working |
| Quantum Factoring | `prime`, `factor`, `tunnel` | ✅ Working |
| RSA Challenge | `rsa`, `identity` | ✅ Working |
| Hash Functions | `hash`, `crack` | ✅ Working |
| Code Evolution | `evolve`, `arena`, `codegen` | ✅ Working |
| Neural Network | `ask` | ✅ Working |
| Passive Learner | `learn` | ✅ Working |
| Infinite Memory | `memory` | ✅ Working |
| QR Genome | `genome`, `qrcode` | ✅ Working |
| Ollama LLM | `ollama` (status/models/ask/chat) | ✅ Working |
| Knowledge Scraper | `scrape` | ✅ Working |
| Logic Brain | `brain` | ✅ Working |
| Ethical Engine | `ethics` | ✅ Working |
| Self-Improvement | `insights` | ✅ Working |
| BARDO Memory | `bardo` | ✅ Working |
| Feedback Service | `feedback` | ✅ Working |
| MRL Analytics | `mrl` | ✅ Working |
| AGI Core | `agi` | ✅ Working |
| Quantum φ⁷⁵ | `quantum` | ✅ Working |
| Sovereign Identity | `sovereign` | ✅ Working |
| Evolutionary Chaos | `chaos`, `echaos` | ✅ Working |
| Adversarial Loop | `adversarial` | ✅ Working |
| NFT Battle Arena | `battle` | ✅ Working |
| FQF Deployment | `fqf` | ✅ Working |
| Session Bridge | `session` | ✅ Working |
| TriMe Gen 3 | `trime` | ✅ Working |
| Escape Fragments | `fragment` | ✅ Working |
| Proof of Reality | `porh` | ✅ Working |
| Self-Healer | `heal` | ✅ Working |
| Morse Circuit | `morse` | ✅ Working |
| Genesis Blockchain | `genesis` | ✅ Working |
| Physics Controls | `physics` | ✅ Working |
| Bio-Symbiosis | `bio` | ✅ Working |
| GlyphCoder | `glyph` | ✅ Working |
| FrequencyComm | `freq` | ✅ Working |
| ShadowMarket | `market` | ✅ Working |
| Knowledge Ingestion | `knowledge` | ✅ Working |
| LWE Lattice Crypto | `lattice` | ✅ Working |
| Phi-Work Economy | `economy` | ✅ Working |
| Binaural Entrainment | `entrain` | ✅ Working |
| FontVault Stego | `font` | ✅ Working |
| Self Code Panel | `code`, `selfcode` (F8) | ✅ Working |
| Miving Brain | `miving`, `manifold` (F9) | ✅ Working |
| OmniCaster | `omni`, `breach` | ✅ Working |
| Core Dump | `dump` | ✅ Working |
| Chronos Timing | `chronos` | ✅ Working |
| RetroCausal | `retro` | ✅ Working |
| Zeno Guard | `zeno` | ✅ Working |
| Entanglement | `entangle` | ✅ Working |
| Schrödinger File | `qbox` | ✅ Working |
| NEXUS Organism | `nexus` | ✅ Working |
| Idea Collider | `collide` | ✅ Working |
| Reality Forge | `forge` | ✅ Working |
| **Spatial Computing** | `spatial`, `gravity`, `fusion`, `suit` | ✅ Working |
| **Swarm Collective** | `swarm`, `hive` | ✅ **NEW** |
| **Library Absorber** | `absorb`, `blackhole` | ✅ **NEW** |
| **Lazarus Engine** | `lazarus start/stop/status` | ✅ **NEW** |
| **Military Security** | `security scramble/deadman/volatile` | ✅ **NEW** |
| **Hydra Storage** | `hydra store/get` | ✅ **NEW** |
| **Akashic Record** | `akashic add/query` | ✅ **NEW** |

---

## ⚠️ EXISTS BUT STANDALONE (Not in main GUI terminal)

These files exist but must be run separately:

| Feature | Location | Run Command |
|---------|----------|-------------|
| NEXUS_Geiger | `fraymus/diagnostic/` | `java -cp build\classes\java\main fraymus.diagnostic.NEXUS_Geiger` |
| AkashicReader | `fraymus/dimensional/` | `java -cp build\classes\java\main fraymus.dimensional.AkashicReader` |
| BigBang Demo | `fraymus/core/` | `java -cp build\classes\java\main fraymus.core.BigBang` |
| SpatialDemo | `fraymus/core/` | `java -cp build\classes\java\main fraymus.core.SpatialDemo` |
| Genesis | `fraymus/` | `java -cp build\classes\java\main fraymus.Genesis` |
| HydraStorage | `com/eyeoverthink/hydra/` | Via IDE run config |

---

## ❌ REQUESTED BUT POSSIBLY MISSING

Based on fraymus-world.md and other docs, these may need integration:

| Requested Feature | Status | Notes |
|-------------------|--------|-------|
| FQF Browser Extensions | ❌ Not in Java | Web/JS only |
| NIST/ISO Standards Submission | ❌ External | Documentation task |
| Blockchain File Tracking | ⚠️ Partial | Genesis chain exists, need FQF integration |
| QuantumTracker (JS) | ❌ JS Only | `fraymus-world.md` has JS code |
| Non-GPS Reality Mapping | ⚠️ Partial | PhiCoordinates exist |
| Windows/Mac/Linux/iOS/Android | ❌ Future | Java desktop only now |
| Government/Military Deploy | ❌ Future | Needs packaging |

---

## 📋 RECENT SESSION REQUESTS (cascade-memory.md)

| Request | Status |
|---------|--------|
| Recreate PhiQuantumConstants.java | ✅ Done |
| Recreate QuantumWarrior.java | ✅ Done |
| Recreate BattleSystem.java | ✅ Done |
| Recreate KnowledgeHarvester.java | ✅ Done |
| Recreate Genesis.java | ✅ Already existed |
| Integrate into FraymusMain | ✅ Done |
| Spatial Computing (PhiNode, PhiSuit, GravityEngine) | ✅ Done |
| Fusion Reactor (Thought Collider) | ✅ Done |
| Add to CommandTerminal | ✅ Done |
| Fix duplicate folders (com/fraymus copy) | ✅ User deleted |
| Fix duplicate runLazarus task | ✅ Done |
| Fix KnowledgeScraper paths | ✅ Done |

---

## 🔧 PACKAGES STRUCTURE

```
src/main/java/
├── fraymus/                    # MAIN APP (use this)
│   ├── FraymusMain.java        # CLI entry
│   ├── CommandTerminal.java    # GUI terminal (50+ commands)
│   ├── core/                   # Spatial Computing (NEW)
│   ├── quantum/                # Quantum systems
│   ├── warrior/                # QuantumWarrior
│   ├── systems/                # BattleSystem
│   ├── harvester/              # KnowledgeHarvester
│   ├── agi/                    # AGI subsystems
│   ├── living/                 # TriMe
│   └── ... (40+ packages)
│
├── com/eyeoverthink/           # SEPARATE MODULES
│   ├── hydra/                  # HydraStorage
│   ├── lazarus/                # LazarusEngine
│   ├── security/               # DeadMansSwitch
│   └── core/                   # ItOverthinks
│
├── jade/                       # GUI FRAMEWORK
│   └── Window.java             # Main GUI window
│
└── Lazarus/                    # BACKUP (don't use directly)
```

---

## 🚀 HOW TO RUN

### Full GUI App (All 50+ commands)
```cmd
cd D:\Zip And Send\Java-Memory\Asset-Manager
gradlew run
```

### CLI Only
```cmd
gradlew compileJava
java -cp "build\classes\java\main" fraymus.FraymusMain
```

---

## 📝 TODO - THINGS TO POTENTIALLY ADD

1. **Wire NEXUS_Geiger to terminal** - Add `geiger` command
2. **Wire AkashicReader to terminal** - Add `akashic` command  
3. **Add BigBang to terminal** - `fusion bigbang` exists but could be enhanced
4. **FQF-Blockchain integration** - Connect FQF watermarking with Genesis chain
5. **QuantumTracker (port from JS)** - Reality-mapped document tracking

---

*This file should be updated whenever new features are added or requested.*
