# FraynixSystem Usage Examples

**Version**: 1.0  
**Date**: April 11, 2026  
**Status**: ✅ COMPLETE

---

## Table of Contents

1. [Basic Examples](#basic-examples)
2. [Query Processing Examples](#query-processing-examples)
3. [Knowledge Ingestion Examples](#knowledge-ingestion-examples)
4. [Persistence Examples](#persistence-examples)
5. [SentientCore Integration Examples](#sentientcore-integration-examples)
6. [Hybrid Orchestrator Integration Examples](#hybrid-orchestrator-integration-examples)
7. [Multi-Brain Collaboration Examples](#multi-brain-collaboration-examples)
8. [Advanced Use Cases](#advanced-use-cases)

---

## Basic Examples

### Example 1: Simple Initialization and Shutdown

```java
import brain_architecture.FraynixSystem;

public class BasicExample {
    public static void main(String[] args) {
        // Create the system
        FraynixSystem fraynix = new FraynixSystem();
        
        // Initialize
        fraynix.initialize();
        
        // Check if initialized
        System.out.println("Initialized: " + fraynix.isInitialized());
        
        // Print system status
        fraynix.printSystemStatus();
        
        // Shutdown
        fraynix.shutdown();
        
        // Check if initialized after shutdown
        System.out.println("Initialized after shutdown: " + fraynix.isInitialized());
    }
}
```

### Example 2: Get System Status

```java
import brain_architecture.FraynixSystem;
import java.util.Map;

public class StatusExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        // Get status as map
        Map<String, Object> status = fraynix.getSystemStatus();
        
        System.out.println("System Status:");
        System.out.println("  FraynixSystem: " + status.get("fraynixSystem"));
        System.out.println("  QuantumOracle: " + status.get("quantumOracle"));
        System.out.println("  Integrations: " + status.get("integrations"));
        
        // Print formatted status
        fraynix.printSystemStatus();
        
        fraynix.shutdown();
    }
}
```

---

## Query Processing Examples

### Example 3: Simple Query

```java
import brain_architecture.FraynixSystem;
import brain_architecture.integration.QuantumOracleIntegration;
import java.util.concurrent.ExecutionException;

public class SimpleQueryExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        try {
            var response = fraynix.processQuery(
                "What is artificial intelligence?",
                QuantumOracleIntegration.OracleQueryType.LOGICAL
            ).get();
            
            System.out.println("Query: " + response.getQuery());
            System.out.println("Response: " + response.getContent());
            System.out.println("Confidence: " + response.getConfidence());
            System.out.println("Brain Used: " + response.getBrainType());
            System.out.println("Query Type: " + response.getQueryType());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        fraynix.shutdown();
    }
}
```

### Example 4: Different Query Types

```java
import brain_architecture.FraynixSystem;
import brain_architecture.integration.QuantumOracleIntegration;
import brain_architecture.brains.BrainType;

public class QueryTypesExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        try {
            // Logical reasoning
            var logicalResponse = fraynix.processQuery(
                "What is 2 + 2?",
                QuantumOracleIntegration.OracleQueryType.LOGICAL
            ).get();
            System.out.println("Logical: " + logicalResponse.getContent());
            
            // Creative generation
            var creativeResponse = fraynix.processQuery(
                "Write a poem about AI",
                QuantumOracleIntegration.OracleQueryType.CREATIVE
            ).get();
            System.out.println("Creative: " + creativeResponse.getContent());
            
            // Quantum phenomena
            var quantumResponse = fraynix.processQuery(
                "Explain quantum entanglement",
                QuantumOracleIntegration.OracleQueryType.QUANTUM
            ).get();
            System.out.println("Quantum: " + quantumResponse.getContent());
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        fraynix.shutdown();
    }
}
```

---

## Knowledge Ingestion Examples

### Example 5: Ingest a File

```java
import brain_architecture.FraynixSystem;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class FileIngestionExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        try {
            var graph = fraynix.ingestFile(
                Paths.get("README.md")
            ).get();
            
            System.out.println("Ingested file successfully");
            System.out.println("Concepts extracted: " + graph.getConcepts().size());
            System.out.println("Synapses created: " + graph.getSynapses().size());
            System.out.println("Source: " + graph.getSource());
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        fraynix.shutdown();
    }
}
```

### Example 6: Ingest a Directory

```java
import brain_architecture.FraynixSystem;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class DirectoryIngestionExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        try {
            var graph = fraynix.ingestDirectory(
                Paths.get("src/main/java")
            ).get();
            
            System.out.println("Ingested directory successfully");
            System.out.println("Total concepts: " + graph.getConcepts().size());
            System.out.println("Total synapses: " + graph.getSynapses().size());
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        fraynix.shutdown();
    }
}
```

---

## Persistence Examples

### Example 7: Persist and Retrieve Knowledge Graph

```java
import brain_architecture.FraynixSystem;
import brain_architecture.synapses.KnowledgeGraph;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PersistenceExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        try {
            // Ingest a file
            KnowledgeGraph graph = fraynix.ingestFile(
                Paths.get("README.md")
            ).get();
            
            // Persist the knowledge graph
            UUID graphId = fraynix.persistKnowledgeGraph(graph);
            System.out.println("Persisted graph with ID: " + graphId);
            
            // Retrieve the knowledge graph
            KnowledgeGraph retrieved = fraynix.retrieveKnowledgeGraph(graphId);
            System.out.println("Retrieved graph with " + retrieved.getConcepts().size() + " concepts");
            
            // Verify blockchain integrity
            boolean valid = fraynix.verifyBlockchainIntegrity();
            System.out.println("Blockchain valid: " + valid);
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        fraynix.shutdown();
    }
}
```

### Example 8: Create Fractal Node

```java
import brain_architecture.FraynixSystem;
import brain_architecture.synapses.KnowledgeGraph;
import brain_architecture.persistence.FractalNode;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class FractalNodeExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        try {
            // Ingest a file
            KnowledgeGraph graph = fraynix.ingestFile(
                Paths.get("README.md")
            ).get();
            
            // Create a fractal node
            FractalNode node = fraynix.createFractalNode(graph);
            System.out.println("Created fractal node");
            System.out.println("Node ID: " + node.getNodeId());
            System.out.println("Pattern: " + node.getPattern());
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        fraynix.shutdown();
    }
}
```

---

## SentientCore Integration Examples

### Example 9: Basic SentientCore Usage

```java
import brain_architecture.FraynixSystem;

public class SentientCoreExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        // Wire SentientCore
        fraynix.wireSentientCore();
        System.out.println("SentientCore wired: " + (fraynix.getSentientCoreBridge() != null));
        
        // Process a query with SentientCore
        String response = fraynix.processQueryWithSentientCore("What is consciousness?");
        System.out.println("Response: " + response);
        
        // Learn from a statement
        String learnResult = fraynix.learnWithSentientCore("Consciousness is the state of being aware.");
        System.out.println("Learn result: " + learnResult);
        
        // Get SentientCore status
        String status = fraynix.getSentientCoreStatus();
        System.out.println("Status: " + status);
        
        // Unwire SentientCore
        fraynix.unwireSentientCore();
        System.out.println("SentientCore unwired: " + (fraynix.getSentientCoreBridge() == null));
        
        fraynix.shutdown();
    }
}
```

### Example 10: SentientCore for Complex Reasoning

```java
import brain_architecture.FraynixSystem;

public class SentientCoreReasoningExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        fraynix.wireSentientCore();
        
        // Learn multiple facts
        String[] facts = {
            "The speed of light is 299,792,458 meters per second.",
            "E = mc^2 relates energy and mass.",
            "Gravity is the curvature of spacetime."
        };
        
        for (String fact : facts) {
            String result = fraynix.learnWithSentientCore(fact);
            System.out.println("Learned: " + result);
        }
        
        // Ask a question that requires combining facts
        String question = "How does mass relate to energy according to physics?";
        String answer = fraynix.processQueryWithSentientCore(question);
        System.out.println("Answer: " + answer);
        
        fraynix.unwireSentientCore();
        fraynix.shutdown();
    }
}
```

---

## Hybrid Orchestrator Integration Examples

### Example 11: Basic Hybrid Orchestrator Usage

```java
import brain_architecture.FraynixSystem;
import java.util.Map;

public class HybridOrchestratorExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        // Wire Hybrid Orchestrator
        fraynix.wireHybridOrchestrator();
        System.out.println("Hybrid Orchestrator wired: " + (fraynix.getOrchestratorBridge() != null));
        
        // Get orchestrator status
        if (fraynix.getOrchestratorBridge() != null) {
            Map<String, Object> status = fraynix.getOrchestratorBridge().getStatus();
            System.out.println("Orchestrator Status:");
            System.out.println("  Discovery Wired: " + status.get("discoveryWired"));
            System.out.println("  Healing Wired: " + status.get("healingWired"));
            System.out.println("  Evolution Wired: " + status.get("evolutionWired"));
            System.out.println("  Organic Wired: " + status.get("organicWired"));
            System.out.println("  Synthesis Wired: " + status.get("synthesisWired"));
        }
        
        // Unwire Hybrid Orchestrator
        fraynix.unwireHybridOrchestrator();
        System.out.println("Hybrid Orchestrator unwired: " + (fraynix.getOrchestratorBridge() == null));
        
        fraynix.shutdown();
    }
}
```

---

## Multi-Brain Collaboration Examples

### Example 12: Multi-Brain Query

```java
import brain_architecture.FraynixSystem;
import brain_architecture.integration.QuantumOracleIntegration;
import brain_architecture.brains.BrainType;
import java.util.concurrent.ExecutionException;

public class MultiBrainExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        try {
            // Get all available brains
            var brains = fraynix.getBrains();
            System.out.println("Available brains: " + brains.keySet());
            
            // Process query with multiple brains
            var response = fraynix.processMultiBrainQuery(
                "How can we solve climate change?",
                QuantumOracleIntegration.OracleQueryType.LOGICAL,
                new BrainType[]{
                    BrainType.PHYSICAL,
                    BrainType.QUANTUM,
                    BrainType.CREATIVE
                }
            ).get();
            
            System.out.println("Query: " + response.getQuery());
            System.out.println("Response: " + response.getContent());
            System.out.println("Collaboration: " + response.getBrainType());
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        fraynix.shutdown();
    }
}
```

### Example 13: Different Brain Combinations

```java
import brain_architecture.FraynixSystem;
import brain_architecture.integration.QuantumOracleIntegration;
import brain_architecture.brains.BrainType;
import java.util.concurrent.ExecutionException;

public class BrainCombinationExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        try {
            // Physical + Logical (practical reasoning)
            var practicalResponse = fraynix.processMultiBrainQuery(
                "How do I build a house?",
                QuantumOracleIntegration.OracleQueryType.PHYSICAL,
                new BrainType[]{BrainType.PHYSICAL, BrainType.LOGICAL}
            ).get();
            System.out.println("Practical: " + practicalResponse.getContent());
            
            // Creative + Spiritual (philosophical reasoning)
            var philosophicalResponse = fraynix.processMultiBrainQuery(
                "What is the meaning of existence?",
                QuantumOracleIntegration.OracleQueryType.SPIRITUAL,
                new BrainType[]{BrainType.CREATIVE, BrainType.SPIRITUAL}
            ).get();
            System.out.println("Philosophical: " + philosophicalResponse.getContent());
            
            // Quantum + Fractal (pattern recognition)
            var patternResponse = fraynix.processMultiBrainQuery(
                "Find patterns in this data",
                QuantumOracleIntegration.OracleQueryType.FRACTAL,
                new BrainType[]{BrainType.QUANTUM, BrainType.FRACTAL}
            ).get();
            System.out.println("Pattern: " + patternResponse.getContent());
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        fraynix.shutdown();
    }
}
```

---

## Advanced Use Cases

### Example 14: Complete Workflow

```java
import brain_architecture.FraynixSystem;
import brain_architecture.integration.QuantumOracleIntegration;
import brain_architecture.brains.BrainType;
import brain_architecture.synapses.KnowledgeGraph;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CompleteWorkflowExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        try {
            // Step 1: Ingest knowledge
            System.out.println("=== Step 1: Ingest Knowledge ===");
            KnowledgeGraph graph = fraynix.ingestFile(
                Paths.get("README.md")
            ).get();
            System.out.println("Ingested " + graph.getConcepts().size() + " concepts");
            
            // Step 2: Persist knowledge
            System.out.println("\n=== Step 2: Persist Knowledge ===");
            UUID graphId = fraynix.persistKnowledgeGraph(graph);
            System.out.println("Persisted with ID: " + graphId);
            
            // Step 3: Wire SentientCore
            System.out.println("\n=== Step 3: Wire SentientCore ===");
            fraynix.wireSentientCore();
            
            // Step 4: Learn from knowledge
            System.out.println("\n=== Step 4: Learn from Knowledge ===");
            for (var concept : graph.getConcepts().subList(0, 5)) {
                String learnResult = fraynix.learnWithSentientCore(
                    concept.getName() + ": " + concept.getDescription()
                );
                System.out.println("Learned: " + learnResult);
            }
            
            // Step 5: Query with multi-brain collaboration
            System.out.println("\n=== Step 5: Multi-Brain Query ===");
            var response = fraynix.processMultiBrainQuery(
                "Summarize the key concepts from the ingested knowledge",
                QuantumOracleIntegration.OracleQueryType.LOGICAL,
                new BrainType[]{BrainType.LOGICAL, BrainType.MEMORY}
            ).get();
            System.out.println("Response: " + response.getContent());
            
            // Step 6: Unwire SentientCore
            System.out.println("\n=== Step 6: Cleanup ===");
            fraynix.unwireSentientCore();
            
            // Step 7: Verify blockchain
            boolean valid = fraynix.verifyBlockchainIntegrity();
            System.out.println("Blockchain valid: " + valid);
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        fraynix.shutdown();
    }
}
```

### Example 15: Error Handling

```java
import brain_architecture.FraynixSystem;
import brain_architecture.integration.QuantumOracleIntegration;
import java.util.concurrent.ExecutionException;

public class ErrorHandlingExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        
        // Try to use without initializing
        try {
            fraynix.processQuery(
                "test",
                QuantumOracleIntegration.OracleQueryType.LOGICAL
            ).get();
        } catch (IllegalStateException e) {
            System.out.println("Expected error: " + e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
        
        // Initialize properly
        fraynix.initialize();
        
        // Try to use SentientCore without wiring
        String response = fraynix.processQueryWithSentientCore("test");
        System.out.println("SentientCore response (not wired): " + response);
        
        // Wire and use properly
        fraynix.wireSentientCore();
        response = fraynix.processQueryWithSentientCore("test");
        System.out.println("SentientCore response (wired): " + response);
        
        fraynix.shutdown();
    }
}
```

### Example 16: Async Query Processing

```java
import brain_architecture.FraynixSystem;
import brain_architecture.integration.QuantumOracleIntegration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AsyncQueryExample {
    public static void main(String[] args) {
        FraynixSystem fraynix = new FraynixSystem();
        fraynix.initialize();
        
        // Submit multiple queries asynchronously
        CompletableFuture<?>[] futures = new CompletableFuture<?>[]{
            fraynix.processQuery("Question 1", QuantumOracleIntegration.OracleQueryType.LOGICAL)
                .thenAccept(r -> System.out.println("Q1: " + r.getContent())),
            
            fraynix.processQuery("Question 2", QuantumOracleIntegration.OracleQueryType.CREATIVE)
                .thenAccept(r -> System.out.println("Q2: " + r.getContent())),
            
            fraynix.processQuery("Question 3", QuantumOracleIntegration.OracleQueryType.QUANTUM)
                .thenAccept(r -> System.out.println("Q3: " + r.getContent()))
        };
        
        // Wait for all to complete
        CompletableFuture.allOf(futures).join();
        
        System.out.println("All queries completed");
        
        fraynix.shutdown();
    }
}
```

---

## Summary

These examples demonstrate the key capabilities of FraynixSystem:

- **Basic Usage**: Initialization, shutdown, status checking
- **Query Processing**: Single and multi-brain queries
- **Knowledge Ingestion**: Files and directories
- **Persistence**: Blockchain storage and retrieval
- **SentientCore**: Bayesian reasoning and learning
- **Hybrid Orchestrator**: Intelligent backend routing
- **Multi-Brain Collaboration**: Combining multiple brain types
- **Advanced Workflows**: Complete end-to-end processes

For more information, see:
- [API Documentation](FRAYNIX_SYSTEM_API.md)
- [Getting Started Guide](FRAYNIX_SYSTEM_GUIDE.md)
- [Architecture Diagram](FRAYNIX_SYSTEM_ARCHITECTURE.md)
