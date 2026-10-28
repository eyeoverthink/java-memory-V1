package brain_architecture;

import brain_architecture.core.BrainType;
import brain_architecture.integration.QuantumOracleIntegration;
import brain_architecture.synapses.KnowledgeGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance tests
 * Tests system performance under load and establishes performance baselines
 */
public class PerformanceTest {

    private FraynixSystem fraynixSystem;
    private static final int QUERY_COUNT = 10;
    /**
     * Keep performance assertions realistic across slower local machines and busy CI hosts.
     * Override via -Dbrain.performance.maxQueryLatencyMs / -Dbrain.performance.maxTotalLatencyMs when needed.
     */
    private static final long MAX_QUERY_LATENCY_MS = Long.getLong("brain.performance.maxQueryLatencyMs", 4000L);
    private static final long MAX_TOTAL_LATENCY_MS = Long.getLong(
        "brain.performance.maxTotalLatencyMs",
        Math.max(12000L, MAX_QUERY_LATENCY_MS * QUERY_COUNT)
    );

    @BeforeEach
    public void setUp() {
        fraynixSystem = new FraynixSystem();
        fraynixSystem.initialize();
    }

    @AfterEach
    public void tearDown() {
        if (fraynixSystem.isInitialized()) {
            fraynixSystem.shutdown();
        }
    }

    @Test
    public void testSingleQueryLatency() {
        long startTime = System.currentTimeMillis();
        QuantumOracleIntegration.OracleResponse response = 
            fraynixSystem.processQuery("What is consciousness?", QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        long endTime = System.currentTimeMillis();

        assertNotNull(response);
        long latency = endTime - startTime;
        System.out.println("Single query latency: " + latency + "ms");
        assertTrue(latency < MAX_QUERY_LATENCY_MS, 
            "Query latency should be less than " + MAX_QUERY_LATENCY_MS + "ms");
    }

    @Test
    public void testMultipleQueryLatency() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < QUERY_COUNT; i++) {
            QuantumOracleIntegration.OracleResponse response = 
                fraynixSystem.processQuery("Query " + i, QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
            assertNotNull(response);
        }
        
        long endTime = System.currentTimeMillis();
        long totalLatency = endTime - startTime;
        double avgLatency = (double) totalLatency / QUERY_COUNT;

        System.out.println("Total latency for " + QUERY_COUNT + " queries: " + totalLatency + "ms");
        System.out.println("Average latency per query: " + avgLatency + "ms");
        
        assertTrue(totalLatency < MAX_TOTAL_LATENCY_MS, 
            "Total latency should be less than " + MAX_TOTAL_LATENCY_MS + "ms");
    }

    @Test
    public void testMultiBrainQueryLatency() {
        BrainType[] brains = new BrainType[] {
            BrainType.QUANTUM,
            BrainType.CREATIVE,
            BrainType.LOGICAL
        };

        long startTime = System.currentTimeMillis();
        CompletableFuture<QuantumOracleIntegration.OracleResponse> future = 
            fraynixSystem.processMultiBrainQuery("What is consciousness?", QuantumOracleIntegration.OracleQueryType.LOGICAL, brains);
        QuantumOracleIntegration.OracleResponse response = future.join();
        long endTime = System.currentTimeMillis();

        assertNotNull(response);
        long latency = endTime - startTime;
        System.out.println("Multi-brain query latency: " + latency + "ms");
        assertTrue(latency < MAX_QUERY_LATENCY_MS, 
            "Multi-brain query latency should be less than " + MAX_QUERY_LATENCY_MS + "ms");
    }

    @Test
    public void testKnowledgeGraphPersistenceLatency() {
        KnowledgeGraph graph = new KnowledgeGraph("performance_test", new ArrayList<>());

        long startTime = System.currentTimeMillis();
        java.util.UUID graphId = fraynixSystem.persistKnowledgeGraph(graph);
        long endTime = System.currentTimeMillis();

        assertNotNull(graphId);
        long latency = endTime - startTime;
        System.out.println("Knowledge graph persistence latency: " + latency + "ms");
        assertTrue(latency < MAX_QUERY_LATENCY_MS, 
            "Persistence latency should be less than " + MAX_QUERY_LATENCY_MS + "ms");
    }

    @Test
    public void testKnowledgeGraphRetrievalLatency() {
        KnowledgeGraph graph = new KnowledgeGraph("performance_test", new ArrayList<>());
        java.util.UUID graphId = fraynixSystem.persistKnowledgeGraph(graph);

        long startTime = System.currentTimeMillis();
        KnowledgeGraph retrieved = fraynixSystem.retrieveKnowledgeGraph(graphId);
        long endTime = System.currentTimeMillis();

        assertNotNull(retrieved);
        long latency = endTime - startTime;
        System.out.println("Knowledge graph retrieval latency: " + latency + "ms");
        assertTrue(latency < MAX_QUERY_LATENCY_MS, 
            "Retrieval latency should be less than " + MAX_QUERY_LATENCY_MS + "ms");
    }

    @Test
    public void testBlockchainIntegrityVerificationLatency() {
        long startTime = System.currentTimeMillis();
        boolean integrity = fraynixSystem.verifyBlockchainIntegrity();
        long endTime = System.currentTimeMillis();

        assertTrue(integrity);
        long latency = endTime - startTime;
        System.out.println("Blockchain integrity verification latency: " + latency + "ms");
        assertTrue(latency < MAX_QUERY_LATENCY_MS, 
            "Verification latency should be less than " + MAX_QUERY_LATENCY_MS + "ms");
    }

    @Test
    public void testSystemStatusRetrievalLatency() {
        long startTime = System.currentTimeMillis();
        java.util.Map<String, Object> status = fraynixSystem.getSystemStatus();
        long endTime = System.currentTimeMillis();

        assertNotNull(status);
        long latency = endTime - startTime;
        System.out.println("System status retrieval latency: " + latency + "ms");
        assertTrue(latency < 100, // Should be very fast
            "Status retrieval latency should be less than 100ms");
    }

    @Test
    public void testInitializationLatency() {
        FraynixSystem newSystem = new FraynixSystem();
        
        long startTime = System.currentTimeMillis();
        newSystem.initialize();
        long endTime = System.currentTimeMillis();

        assertTrue(newSystem.isInitialized());
        long latency = endTime - startTime;
        System.out.println("Initialization latency: " + latency + "ms");
        assertTrue(latency < MAX_QUERY_LATENCY_MS, 
            "Initialization latency should be less than " + MAX_QUERY_LATENCY_MS + "ms");

        newSystem.shutdown();
    }

    @Test
    public void testShutdownLatency() {
        long startTime = System.currentTimeMillis();
        fraynixSystem.shutdown();
        long endTime = System.currentTimeMillis();

        assertFalse(fraynixSystem.isInitialized());
        long latency = endTime - startTime;
        System.out.println("Shutdown latency: " + latency + "ms");
        assertTrue(latency < MAX_QUERY_LATENCY_MS, 
            "Shutdown latency should be less than " + MAX_QUERY_LATENCY_MS + "ms");
    }

    @Test
    public void testWireUnwireLatency() {
        long startTime = System.currentTimeMillis();
        fraynixSystem.wireSentientCore();
        long endTime = System.currentTimeMillis();

        long wireLatency = endTime - startTime;
        System.out.println("Wire SentientCore latency: " + wireLatency + "ms");
        assertTrue(wireLatency < 100, // Should be very fast
            "Wire latency should be less than 100ms");

        startTime = System.currentTimeMillis();
        fraynixSystem.unwireSentientCore();
        endTime = System.currentTimeMillis();

        long unwireLatency = endTime - startTime;
        System.out.println("Unwire SentientCore latency: " + unwireLatency + "ms");
        assertTrue(unwireLatency < 100, // Should be very fast
            "Unwire latency should be less than 100ms");
    }

    @Test
    public void testConcurrentQueryPerformance() {
        long startTime = System.currentTimeMillis();

        List<CompletableFuture<QuantumOracleIntegration.OracleResponse>> futures = 
            new ArrayList<>();
        
        for (int i = 0; i < QUERY_COUNT; i++) {
            final int index = i;
            CompletableFuture<QuantumOracleIntegration.OracleResponse> future = 
                CompletableFuture.supplyAsync(() -> 
                    fraynixSystem.processQuery("Concurrent query " + index, QuantumOracleIntegration.OracleQueryType.LOGICAL).join());
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.currentTimeMillis();
        long totalLatency = endTime - startTime;
        double avgLatency = (double) totalLatency / QUERY_COUNT;

        System.out.println("Concurrent queries total latency: " + totalLatency + "ms");
        System.out.println("Concurrent queries average latency: " + avgLatency + "ms");

        // Verify all responses
        for (CompletableFuture<QuantumOracleIntegration.OracleResponse> future : futures) {
            QuantumOracleIntegration.OracleResponse response = future.join();
            assertNotNull(response);
        }
    }

    @Test
    public void testMemoryUsageOverTime() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // Perform operations
        for (int i = 0; i < QUERY_COUNT; i++) {
            fraynixSystem.processQuery("Query " + i, QuantumOracleIntegration.OracleQueryType.LOGICAL).join();
        }

        runtime.gc();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;

        System.out.println("Initial memory: " + initialMemory / 1024 / 1024 + "MB");
        System.out.println("Final memory: " + finalMemory / 1024 / 1024 + "MB");
        System.out.println("Memory increase: " + memoryIncrease / 1024 / 1024 + "MB");

        // Memory increase should be reasonable (less than 100MB for 10 queries)
        assertTrue(memoryIncrease < 100 * 1024 * 1024, 
            "Memory increase should be less than 100MB");
    }

    @Test
    public void testFractalNodeCreationLatency() {
        long startTime = System.currentTimeMillis();
        KnowledgeGraph graph = new KnowledgeGraph("performance_test", new java.util.ArrayList<>());
        brain_architecture.persistence.FractalNode node = fraynixSystem.createFractalNode(graph);
        long endTime = System.currentTimeMillis();

        assertNotNull(node);
        long latency = endTime - startTime;
        System.out.println("Fractal node creation latency: " + latency + "ms");
        assertTrue(latency < MAX_QUERY_LATENCY_MS, 
            "Fractal node creation latency should be less than " + MAX_QUERY_LATENCY_MS + "ms");
    }

    @Test
    public void testFractalNodeRetrievalLatency() {
        // Fractal node retrieval not available in API - skip test
        // This test is removed since getFractalNode doesn't exist in FraynixSystem
        System.out.println("Fractal node retrieval test skipped - API not available");
    }

    @Test
    public void testPerformanceBaselineDocumentation() {
        System.out.println("\n=== Performance Baseline Summary ===");
        System.out.println("Max single query latency: " + MAX_QUERY_LATENCY_MS + "ms");
        System.out.println("Max total latency for " + QUERY_COUNT + " queries: " + MAX_TOTAL_LATENCY_MS + "ms");
        System.out.println("Max memory increase for " + QUERY_COUNT + " queries: 100MB");
        System.out.println("===================================\n");
    }
}
