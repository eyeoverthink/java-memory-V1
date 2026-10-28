# FraynixSystem Getting Started Guide

**Version**: 1.0  
**Date**: April 11, 2026  
**Status**: ✅ COMPLETE

---

## Table of Contents

1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Installation](#installation)
4. [Quick Start](#quick-start)
5. [Basic Usage](#basic-usage)
6. [Advanced Features](#advanced-features)
7. [Configuration](#configuration)
8. [Troubleshooting](#troubleshooting)
9. [Next Steps](#next-steps)

---

## Introduction

FraynixSystem is the single entry point for the entire brain architecture system. It provides a unified interface for:

- **Brain Architecture**: 12 specialized brains with 6 AI backends each
- **SentientCore AGI**: Bayesian reasoning, HDC, memory systems (optional)
- **Hybrid Orchestrator**: Backend discovery, self-healing, organic growth (optional)
- **Knowledge Ingestion**: Files, directories, URLs, websites, GitHub
- **Persistence Layer**: Blockchain, Merkle Tree, Fractal Nodes

---

## Prerequisites

### Required
- Java 17 or higher
- Gradle 9.0 or higher

### Optional (for real API calls)
- Ollama running on localhost:11434 (for OllamaBackend)
- ANTHROPIC_API_KEY environment variable (for ClaudeBackend)
- GOOGLE_API_KEY environment variable (for Gemma3Backend)

---

## Installation

### Clone the Repository

```bash
cd F:\Full-AI-System\Java-Memory\Asset-Manager
```

### Build the Project

```bash
./gradlew.bat :brain_architecture:build
```

### Run Tests

```bash
./gradlew.bat :brain_architecture:test
```

---

## Quick Start

### 1. Create a FraynixSystem Instance

```java
import brain_architecture.FraynixSystem;

public class MyApplication {
    public static void main(String[] args) {
        // Create the system
        FraynixSystem fraynix = new FraynixSystem();
        
        // Initialize
        fraynix.initialize();
        
        // Your code here...
        
        // Shutdown
        fraynix.shutdown();
    }
}
```

### 2. Process a Query

```java
import brain_architecture.FraynixSystem;
import brain_architecture.integration.QuantumOracleIntegration;
import java.util.concurrent.ExecutionException;

public class QueryExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        try {
            var response = fraynix.processQuery(
                "What is the meaning of life?",
                QuantumOracleIntegration.OracleQueryType.LOGICAL
            ).get();
            
            System.out.println("Response: " + response.getContent());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        
        fraynix.shutdown();
    }
}
```

---

## Basic Usage

### Initialization

Always initialize the system before using it:

```java
FraynixSystem fraynix = new FraynixSystem();
fraynix.initialize();
```

Check if initialized:

```java
if (fraynix.isInitialized()) {
    // System is ready
}
```

### Query Processing

#### Single Brain Query

```java
var response = fraynix.processQuery(
    "Your question here",
    QuantumOracleIntegration.OracleQueryType.LOGICAL
).get();
```

#### Multi-Brain Collaboration

```java
import brain_architecture.brains.BrainType;

var response = fraynix.processMultiBrainQuery(
    "Your complex question",
    QuantumOracleIntegration.OracleQueryType.LOGICAL,
    new BrainType[]{
        BrainType.PHYSICAL,
        BrainType.QUANTUM,
        BrainType.CREATIVE
    }
).get();
```

### Knowledge Ingestion

#### Ingest a File

```java
import java.nio.file.Paths;

var graph = fraynix.ingestFile(
    Paths.get("path/to/your/file.md")
).get();

System.out.println("Extracted " + graph.getConcepts().size() + " concepts");
```

#### Ingest a Directory

```java
var graph = fraynix.ingestDirectory(
    Paths.get("path/to/your/directory")
).get();

System.out.println("Extracted " + graph.getConcepts().size() + " concepts");
```

### Persistence

#### Persist Knowledge Graph

```java
UUID graphId = fraynix.persistKnowledgeGraph(graph);
System.out.println("Graph ID: " + graphId);
```

#### Retrieve Knowledge Graph

```java
KnowledgeGraph retrieved = fraynix.retrieveKnowledgeGraph(graphId);
```

#### Create Fractal Node

```java
FractalNode node = fraynix.createFractalNode(subgraph);
```

#### Verify Blockchain Integrity

```java
boolean valid = fraynix.verifyBlockchainIntegrity();
System.out.println("Blockchain valid: " + valid);
```

### System Status

#### Get Status Map

```java
Map<String, Object> status = fraynix.getSystemStatus();
System.out.println(status);
```

#### Print Status to Console

```java
fraynix.printSystemStatus();
```

---

## Advanced Features

### SentientCore Integration

SentientCore provides Bayesian reasoning, HDC, and memory systems.

#### Wire SentientCore

```java
fraynix.wireSentientCore();
```

#### Process Query with SentientCore

```java
String response = fraynix.processQueryWithSentientCore("Your question");
System.out.println(response);
```

#### Learn with SentientCore

```java
String result = fraynix.learnWithSentientCore("Fact to learn");
System.out.println(result);
```

#### Get SentientCore Status

```java
String status = fraynix.getSentientCoreStatus();
System.out.println(status);
```

#### Unwire SentientCore

```java
fraynix.unwireSentientCore();
```

### Hybrid Orchestrator Integration

Hybrid Orchestrator provides backend discovery, self-healing, and organic growth.

#### Wire Hybrid Orchestrator

```java
fraynix.wireHybridOrchestrator();
```

#### Get Orchestrator Status

```java
if (fraynix.getOrchestratorBridge() != null) {
    Map<String, Object> status = fraynix.getOrchestratorBridge().getStatus();
    System.out.println(status);
}
```

#### Unwire Hybrid Orchestrator

```java
fraynix.unwireHybridOrchestrator();
```

---

## Configuration

### Environment Variables

For real API calls, set the following environment variables:

```bash
# Claude API (for ClaudeBackend)
export ANTHROPIC_API_KEY="your-anthropic-api-key"

# Google AI Studio (for Gemma3Backend)
export GOOGLE_API_KEY="your-google-api-key"

# Ollama (for OllamaBackend)
# Requires running: ollama serve
```

### Optional Environment Variables

```bash
# OpenClaw API (for OpenClawBackend)
export OPENCLAW_API_KEY="your-openclaw-api-key"

# NeoClaw API (for NeoClawBackend)
export NEOCLAW_API_KEY="your-neoclaw-api-key"

# Obsidian API (for ObsidianBackend)
export OBSIDIAN_API_KEY="your-obsidian-api-key"
```

### Backend Selection

The system supports 6 AI backends:
1. **Ollama** - Local LLM (requires Ollama service)
2. **Claude** - Anthropic Claude (requires API key)
3. **Gemma3** - Google Gemma 3 (requires API key)
4. **OpenClaw** - TypeScript/JavaScript agent system
5. **NeoClaw** - Enhanced openclaw evolution API
6. **Obsidian** - Knowledge base API

Each brain has 3 backends from the 6 available.

---

## Troubleshooting

### System Not Initialized

**Error**: `IllegalStateException: FraynixSystem not initialized`

**Solution**: Call `fraynix.initialize()` before using the system.

```java
FraynixSystem fraynix = new FraynixSystem();
fraynix.initialize(); // Don't forget this!
```

### SentientCore Not Wired

**Issue**: `processQueryWithSentientCore()` returns "NOT WIRED"

**Solution**: Call `fraynix.wireSentientCore()` before using SentientCore features.

```java
fraynix.wireSentientCore();
String response = fraynix.processQueryWithSentientCore("question");
```

### Backend Unavailable

**Issue**: Backend returns error or mock response

**Solution**: 
- For Ollama: Ensure `ollama serve` is running
- For Claude/Gemma3: Set appropriate API key environment variables
- Check backend health status

### Compilation Errors

**Issue**: Cannot find symbol errors

**Solution**: Ensure you're using the correct package imports:
- `brain_architecture.core.BrainType` (not `brain_architecture.brains.BrainType`)
- `brain_architecture.core.QuantumBrain` (not `brain_architecture.brains.QuantumBrain`)
- `brain_architecture.synapses.KnowledgeGraph` (not `brain_architecture.persistence.KnowledgeGraph`)
- `brain_architecture.persistence.FractalNode` (not `brain_architecture.synapses.FractalNode`)

---

## Next Steps

### Learn More

- [API Documentation](FRAYNIX_SYSTEM_API.md) - Complete API reference
- [Usage Examples](FRAYNIX_SYSTEM_EXAMPLES.md) - Detailed usage examples
- [Architecture Diagram](FRAYNIX_SYSTEM_ARCHITECTURE.md) - System architecture

### Explore Features

- Try different query types (PHYSICAL, QUANTUM, CREATIVE, etc.)
- Experiment with multi-brain collaboration
- Enable SentientCore for Bayesian reasoning
- Enable Hybrid Orchestrator for intelligent backend routing
- Ingest your own knowledge bases

### Integration

- Integrate FraynixSystem into your application
- Use the unified query interface for all AI operations
- Leverage knowledge ingestion for your data
- Use persistence for long-term storage

---

## Support

For issues or questions:
1. Check the [API Documentation](FRAYNIX_SYSTEM_API.md)
2. Review the [Usage Examples](FRAYNIX_SYSTEM_EXAMPLES.md)
3. Examine the test cases in `FraynixSystemTest.java`
4. Run the example in `FraynixSystemExample.java`

---

## Safety Notes

- All advanced features are optional via wire/unwire pattern
- The system operates normally without any optional components
- All integrations are reversible - no breaking changes
- Components can operate independently
- Graceful degradation when components unavailable

---

## Performance Tips

1. **Async Operations**: Query processing and knowledge ingestion are asynchronous via `CompletableFuture`. Use async patterns for better performance.

2. **Batch Operations**: For multiple knowledge ingestion operations, consider batching them.

3. **Caching**: The system includes HDC similarity caching for improved performance.

4. **Selective Integration**: Only wire the features you need. Unwire unused features to reduce overhead.

---

## License

See the project license file for details.
