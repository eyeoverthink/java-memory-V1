# Scraping and Storage System Guide

## Current Status

### ✅ What's Set Up
1. **KnowledgeScraper** - Fully implemented, ready to use
2. **MongoDB Integration** - Configured with your credentials
3. **QR System** - Integrated with QRGenome class
4. **attached_assets/** - Folder exists with 54 files ready to scrape

### 🔧 What Needs Configuration

#### 1. Working Directory Issue
The scraper looks for `attached_assets/` relative to where the app runs. 

**Current Path:** `D:\Zip And Send\Java-Memory\Fraymus_Agent_BrainV2\attached_assets`

**Solution:** Run the app from the correct directory or use the new diagnostic command.

#### 2. MongoDB Backend (Optional)
MongoDB is configured but **not enabled by default**. The system uses local files (STREAMING_LOG mode).

**Your MongoDB Config:**
- Connection: `mongodb+srv://eyeoverthink:wolverine@aigenerator.12uhq.mongodb.net/`
- Database: `fraymus_memory`
- Status: Ready but not active

---

## How to Use the Scraping System

### Check System Status
```
diag paths        # Check if attached_assets/ is found
diag memory       # Check memory backend status
scrape            # Show scraper status
```

### Scrape Files
```
scrape all                           # Scrape all files in attached_assets/
scrape <filename>                    # Scrape specific file
scrape search <query>                # Search scraped knowledge
scrape topic <name>                  # Get knowledge on a topic
```

### Supported File Types
- **PDFs** - Extracts text from all pages (max 1000 pages)
- **Text files** - .txt, .md, .html, .json, .csv
- **Code files** - .py, .java

### File Size Limits
- Max file size: 50 MB
- Max pages per PDF: 1000
- Max chunks per file: 500

---

## MongoDB Setup (Optional)

### Why Use MongoDB?
- **Cloud backup** - Your knowledge persists across machines
- **Scalability** - Handle millions of memory records
- **Search** - Fast full-text search across all knowledge
- **Sharing** - Access from multiple instances

### Enable MongoDB

**Option 1: MongoDB Only**
```
memory backend MONGODB
```
All memory goes to cloud (slower but persistent).

**Option 2: Hybrid Mode (Recommended)**
```
memory backend HYBRID
```
Local files for speed + MongoDB for backup.

**Check Status:**
```
memory status
diag memory
```

### MongoDB Connection Details
Your credentials are already configured in `MemoryConfig.java`:
```java
mongoConnectionString = "mongodb+srv://eyeoverthink:wolverine@aigenerator.12uhq.mongodb.net/"
mongoDatabaseName = "fraymus_memory"
```

The system will:
1. Create indexes automatically
2. Store all memory records with metadata
3. Enable full-text search
4. Provide parallel writes for performance

---

## QR System Integration

### What's Set Up
- **QRGenome** - Genetic programming system for entities
- **QR Backup** - Optional QR encoding of memory
- **DNA Encoding** - Phi-harmonic DNA for entities

### Enable QR Backup
```
memory qr enable
```

This encodes memory snapshots as QR codes for:
- Visual backup
- Portable consciousness
- Cross-system transfer

### QR Commands
```
qrcode                    # Show QR system status
qrcode <entity_name>      # Encode entity DNA as QR
genome                    # Show genome statistics
```

---

## Troubleshooting

### "File not found" Errors

**Cause:** App running from wrong directory.

**Fix:**
```
diag paths
```
This shows where the app is looking for files.

**Manual Fix:**
1. Check working directory
2. Ensure you're in: `D:\Zip And Send\Java-Memory\Fraymus_Agent_BrainV2`
3. Verify `attached_assets/` exists there

### "Scrape all fails"

**Common Causes:**
1. **Memory too high** - Run `memory save` first
2. **Large PDFs** - System auto-limits to 1000 pages
3. **Corrupted files** - Check crash log: `data/crash_log.json`

**Check Memory:**
```
memory status
```

**Force Save:**
```
memory save
```

### MongoDB Connection Fails

**Symptoms:**
- "MongoDB connection failed" error
- Backend stays in STREAMING_LOG mode

**Fixes:**
1. Check internet connection
2. Verify credentials in `data/memory_config.properties`
3. Test connection: `memory backend MONGODB`
4. Check MongoDB Atlas dashboard

---

## File Organization

### Directory Structure
```
Fraymus_Agent_BrainV2/
├── attached_assets/        # Files to scrape (54 files)
│   ├── *.pdf              # PDF documents
│   ├── *.py               # Python code
│   ├── *.txt              # Text files
│   └── *.md               # Markdown docs
├── data/                   # Memory storage
│   ├── memory_log.dat     # Local memory (STREAMING_LOG)
│   ├── memory_config.properties
│   ├── crash_log.json
│   └── genesis_chain.json
└── src/main/java/fraymus/
    ├── KnowledgeScraper.java
    ├── MongoMemoryBackend.java
    ├── MemoryConfig.java
    └── SystemDiagnostics.java
```

### Memory Files
- **memory_log.dat** - Local append-only log (fast)
- **MongoDB** - Cloud database (persistent)
- **QR codes** - Visual backups (optional)

---

## Performance Tips

### Before Scraping Large Batches
1. Check memory: `memory status`
2. Save if needed: `memory save`
3. Monitor progress: `scrape` (shows current file)

### Optimize MongoDB
- Use HYBRID mode for best performance
- Local writes are instant
- MongoDB syncs in background

### Large PDF Handling
The system automatically:
- Skips files > 50 MB
- Limits to 1000 pages per PDF
- Chunks text into 500-char segments
- Detects topics for organization

---

## Next Steps

### 1. Verify Paths
```
diag paths
```

### 2. Test Scraping
```
scrape scott-algo_-_Copy_1769219190031_1770291536100.pdf
```

### 3. Enable MongoDB (Optional)
```
memory backend HYBRID
```

### 4. Scrape All Files
```
scrape all
```

### 5. Search Knowledge
```
scrape search quantum
scrape topic physics
```

---

## Advanced Features

### Phi-Harmonic Resonance
Each scraped chunk gets a phi-resonance score:
```java
double phiResonance = computeChunkResonance(chunk, index, totalChunks);
```

This enables:
- Intelligent retrieval
- Relevance ranking
- Harmonic pattern matching

### Topic Detection
Automatic topic extraction from content:
- Physics, quantum, mathematics
- Programming, algorithms
- Consciousness, AI

### Passive Learning Integration
Scraped knowledge feeds into:
- `PassiveLearner` - Pattern recognition
- `PhiNeuralNet` - Neural processing
- `InfiniteMemory` - Long-term storage

---

## Summary

**Your system is ready!** The scraping infrastructure is complete:
- ✅ KnowledgeScraper implemented
- ✅ MongoDB configured (not enabled)
- ✅ QR system integrated
- ✅ 54 files ready in attached_assets/

**Main issue:** Working directory path. Use `diag paths` to verify.

**To start scraping:**
1. Run `diag paths` to check paths
2. Run `scrape all` to process all files
3. Run `scrape search <query>` to test retrieval

**Optional:** Enable MongoDB with `memory backend HYBRID` for cloud persistence.
