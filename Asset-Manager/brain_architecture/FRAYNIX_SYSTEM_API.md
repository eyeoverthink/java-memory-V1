# FraynixSystem API Documentation

**Version**: 1.0  
**Date**: April 11, 2026  
**Status**: ✅ COMPLETE

---

## Overview

FraynixSystem is the single entry point for the entire brain architecture system. It provides a unified interface for:

- Brain Architecture (12 specialized brains with 6 AI backends each)
- SentientCore AGI (Bayesian reasoning, HDC, memory systems) - optional
- Hybrid Orchestrator (Backend discovery, self-healing, organic growth) - optional
- Knowledge Ingestion (Files, directories, URLs, websites, GitHub)
- Persistence Layer (Blockchain, Merkle Tree, Fractal Nodes)

---

## Class: FraynixSystem

**Package**: `brain_architecture`

### Constructors

```java
public FraynixSystem()
```

Creates a new FraynixSystem instance. The system is not initialized until `initialize()` is called.

---

### Lifecycle Management

#### `initialize()`
```java
public void initialize()
```

Initializes the FraynixSystem. This initializes the Brain Architecture core components.

**Throws**: `IllegalStateException` if already initialized

---

#### `shutdown()`
```java
public void shutdown()
```

Shuts down the FraynixSystem gracefully. This unwires all integrations and shuts down core components.

---

#### `isInitialized()`
```java
public boolean isInitialized()
```

Returns `true` if the system is initialized, `false` otherwise.

---

### Unified Query Interface

#### `processQuery()`
```java
public CompletableFuture<QuantumOracleIntegration.OracleResponse> processQuery(
    String query, 
    QuantumOracleIntegration.OracleQueryType queryType
)
```

Process a query through the brain architecture. This is the primary unified query interface.

**Parameters**:
- `query`: The query to process
- `queryType`: The type of query (PHYSICAL, QUANTUM, FRACTAL, CREATIVE, LOGICAL, EMOTIONAL, SPIRITUAL, TACHYONIC, VISUAL, AUDITORY)

**Returns**: `CompletableFuture<OracleResponse>` containing the oracle response

**Throws**: `IllegalStateException` if system not initialized

---

#### `processMultiBrainQuery()`
```java
public CompletableFuture<QuantumOracleIntegration.OracleResponse> processMultiBrainQuery(
    String query, 
    QuantumOracleIntegration.OracleQueryType queryType, 
    BrainType[] brainTypes
)
```

Process a query with multi-brain collaboration.

**Parameters**:
- `query`: The query to process
- `queryType`: The type of query
- `brainTypes`: The brains to collaborate

**Returns**: `CompletableFuture<OracleResponse>` containing the oracle response

**Throws**: `IllegalStateException` if system not initialized

---

### Knowledge Ingestion

#### `ingestFile()`
```java
public CompletableFuture<KnowledgeGraph> ingestFile(Path filePath)
```

Ingest knowledge from a file.

**Parameters**:
- `filePath`: Path to the file to ingest

**Returns**: `CompletableFuture<KnowledgeGraph>` containing the knowledge graph

**Throws**: `IllegalStateException` if system not initialized

---

#### `ingestDirectory()`
```java
public CompletableFuture<KnowledgeGraph> ingestDirectory(Path directoryPath)
```

Ingest knowledge from a directory.

**Parameters**:
- `directoryPath`: Path to the directory to ingest

**Returns**: `CompletableFuture<KnowledgeGraph>` containing the knowledge graph

**Throws**: `IllegalStateException` if system not initialized

---

### Persistence

#### `persistKnowledgeGraph()`
```java
public UUID persistKnowledgeGraph(KnowledgeGraph graph)
```

Persist a knowledge graph to blockchain storage.

**Parameters**:
- `graph`: The knowledge graph to persist

**Returns**: `UUID` of the persisted graph

**Throws**: `IllegalStateException` if system not initialized

---

#### `retrieveKnowledgeGraph()`
```java
public KnowledgeGraph retrieveKnowledgeGraph(UUID graphId)
```

Retrieve a knowledge graph by ID.

**Parameters**:
- `graphId`: UUID of the graph to retrieve

**Returns**: The knowledge graph

**Throws**: `IllegalStateException` if system not initialized

---

#### `createFractalNode()`
```java
public FractalNode createFractalNode(KnowledgeGraph subgraph)
```

Create a fractal node for hierarchical storage.

**Parameters**:
- `subgraph`: The knowledge graph to store as a fractal node

**Returns**: The created fractal node

**Throws**: `IllegalStateException` if system not initialized

---

#### `verifyBlockchainIntegrity()`
```java
public boolean verifyBlockchainIntegrity()
```

Verify blockchain integrity.

**Returns**: `true` if blockchain is valid, `false` otherwise

**Throws**: `IllegalStateException` if system not initialized

---

### SentientCore Integration (Optional)

#### `wireSentientCore()`
```java
public void wireSentientCore()
```

Wire SentientCore AGI integration. Enables Bayesian reasoning, HDC, and memory systems.

**Throws**: `IllegalStateException` if system not initialized

---

#### `unwireSentientCore()`
```java
public void unwireSentientCore()
```

Unwire SentientCore AGI integration. Disables SentientCore - system uses native brain architecture.

---

#### `processQueryWithSentientCore()`
```java
public String processQueryWithSentientCore(String query)
```

Process a query using SentientCore (if wired).

**Parameters**:
- `query`: The query to process

**Returns**: Response from SentientCore or "NOT WIRED" message

---

#### `learnWithSentientCore()`
```java
public String learnWithSentientCore(String statement)
```

Learn from a statement using SentientCore (if wired).

**Parameters**:
- `statement`: The statement to learn from

**Returns**: Learning confirmation or "NOT WIRED" message

---

#### `getSentientCoreStatus()`
```java
public String getSentientCoreStatus()
```

Get SentientCore status (if wired).

**Returns**: Status string or "NOT WIRED" message

---

#### `getSentientCoreBridge()`
```java
public SentientCoreBridge getSentientCoreBridge()
```

Get SentientCore bridge (if wired).

**Returns**: `SentientCoreBridge` instance or `null` if not wired

---

### Hybrid Orchestrator Integration (Optional)

#### `wireHybridOrchestrator()`
```java
public void wireHybridOrchestrator()
```

Wire Hybrid AI Orchestrator integration. Enables backend discovery, self-healing, and organic growth.

**Throws**: `IllegalStateException` if system not initialized

---

#### `unwireHybridOrchestrator()`
```java
public void unwireHybridOrchestrator()
```

Unwire Hybrid AI Orchestrator integration. Disables orchestration - system uses basic backend routing.

---

#### `getOrchestratorBridge()`
```java
public OrchestratorBridge getOrchestratorBridge()
```

Get Orchestrator bridge (if wired).

**Returns**: `OrchestratorBridge` instance or `null` if not wired

---

### System Status and Statistics

#### `getSystemStatus()`
```java
public Map<String, Object> getSystemStatus()
```

Get comprehensive system status.

**Returns**: `Map<String, Object>` containing system status information

---

#### `getBrains()`
```java
public Map<BrainType, QuantumBrain> getBrains()
```

Get all brains from the system.

**Returns**: `Map<BrainType, QuantumBrain>` of brain types to brain instances

---

#### `getCommunication()`
```java
public BrainCommunication getCommunication()
```

Get brain communication system.

**Returns**: `BrainCommunication` instance

---

#### `getKnowledgeScraper()`
```java
public KnowledgeScraper getKnowledgeScraper()
```

Get knowledge scraper.

**Returns**: `KnowledgeScraper` instance

---

#### `getPersistentMemory()`
```java
public PersistentMemory getPersistentMemory()
```

Get persistent memory coordinator.

**Returns**: `PersistentMemory` instance

---

#### `printSystemStatus()`
```java
public void printSystemStatus()
```

Print system status to console.

---

## Enums

### OracleQueryType

**Package**: `brain_architecture.integration.QuantumOracleIntegration`

```java
public enum OracleQueryType {
    PHYSICAL,     // Physical world interaction
    QUANTUM,      // Quantum phenomena
    FRACTAL,      // Pattern recognition
    CREATIVE,     // Creative generation
    LOGICAL,      // Logical reasoning
    EMOTIONAL,    // Emotional intelligence
    SPIRITUAL,    // Consciousness and awareness
    TACHYONIC,    // FTL processing
    VISUAL,       // Visual generation and understanding
    AUDITORY      // Speech and audio processing
}
```

---

### BrainType

**Package**: `brain_architecture.core`

```java
public enum BrainType {
    PHYSICAL,      // Physical brain
    QUANTUM,       // Quantum brain
    FRACTAL,       // Fractal brain
    CREATIVE,      // Creative brain
    LOGICAL,       // Logical brain
    EMOTIONAL,     // Emotional brain
    SPIRITUAL,     // Spiritual brain
    TACHYONIC,     // Tachyonic brain
    VISUAL,        // Visual brain
    AUDITORY,      // Auditory brain
    EVOLUTIONARY,   // Evolutionary brain
    MEMORY         // Memory brain
}
```

---

## Example Usage

See `FraynixSystemExample.java` for comprehensive examples of how to use the FraynixSystem API.

---

## Safety Notes

- All advanced features (SentientCore, Hybrid Orchestrator) are optional via wire/unwire pattern
- The system operates normally without any optional components
- All integrations are reversible - no breaking changes
- Components can operate independently
- Graceful degradation when components unavailable

---

## Thread Safety

FraynixSystem is not thread-safe. For multi-threaded access, external synchronization is required.

---

## Error Handling

Most methods throw `IllegalStateException` if called before `initialize()`. Always call `initialize()` before using the system.

---

## Performance Considerations

- Query processing is asynchronous via `CompletableFuture`
- Knowledge ingestion is asynchronous via `CompletableFuture`
- Wire/unwire operations are synchronous
- System status retrieval is synchronous

---

## Dependencies

FraynixSystem depends on:
- Brain Architecture core components
- SystemOrchestrator
- QuantumOracleIntegration
- SentientCoreBridge (optional)
- OrchestratorBridge (optional)

---

## Version History

**v1.0** (April 11, 2026)
- Initial release
- Single entry point for entire system
- Unified query interface
- Optional integrations via wire/unwire pattern
- 32 unit tests (all passing)
