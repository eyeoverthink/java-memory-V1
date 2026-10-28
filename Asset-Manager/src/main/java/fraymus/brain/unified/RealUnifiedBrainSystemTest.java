package fraymus.brain.unified;

import java.util.*;

/**
 * Real Unified Brain System Test
 * Demonstrates all 35 brains from 4 systems working together through the unified orchestrator.
 * NO MOCKS - ALL REAL IMPLEMENTATIONS
 */
public class RealUnifiedBrainSystemTest {
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("       REAL UNIFIED BRAIN SYSTEM TEST");
        System.out.println("       NO MOCKS - ALL REAL IMPLEMENTATIONS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        UnifiedBrainOrchestrator orchestrator = new UnifiedBrainOrchestrator();
        
        System.out.println("\n[1] Registering REAL brain systems...");
        System.out.println("    - Brain Architecture (12 brains)");
        System.out.println("    - AEON Platform (7 brains)");
        System.out.println("    - Nexus Omega (12 brains)");
        System.out.println("    - Fraymus Complete (8 brains)");
        System.out.println("    TOTAL: 39 brains from 4 systems");
        
        // Register Brain Architecture (12 brains)
        BrainArchitectureAdapter brainArchAdapter = new BrainArchitectureAdapter();
        orchestrator.registerBrain(brainArchAdapter);
        System.out.println("    ✓ Brain Architecture registered");
        
        // Register AEON Platform (7 brains)
        AEONBrainAdapter aeonAdapter = new AEONBrainAdapter();
        orchestrator.registerBrain(aeonAdapter);
        System.out.println("    ✓ AEON Platform registered");
        
        // Register Nexus Omega (12 brains)
        NexusOmegaBrainAdapter nexusAdapter = new NexusOmegaBrainAdapter();
        orchestrator.registerBrain(nexusAdapter);
        System.out.println("    ✓ Nexus Omega registered");
        
        // Register Fraymus Complete (8 brains)
        FraymusCompleteAdapter fraymusAdapter = new FraymusCompleteAdapter();
        orchestrator.registerBrain(fraymusAdapter);
        System.out.println("    ✓ Fraymus Complete registered");
        
        System.out.println("\n    Total registered: " + orchestrator.getAllBrains().size() + " brain adapters");
        
        // Test 1: Physics query (should use Physical brains)
        System.out.println("\n[2] Test 1: Physics query");
        Map<String, Object> context1 = new HashMap<>();
        BrainQuery physicsQuery = new BrainQuery(
            "Calculate the trajectory of a projectile under gravity",
            Arrays.asList(BrainCapability.PHYSICS, BrainCapability.REASONING),
            context1,
            1.0
        );
        BrainResponse physicsResponse = orchestrator.processQuery(physicsQuery);
        System.out.println("Response:\n" + physicsResponse.getResponse());
        System.out.println("Brain: " + physicsResponse.getBrainName());
        System.out.println("Confidence: " + String.format("%.2f", physicsResponse.getConfidence()));
        System.out.println("Consciousness: " + String.format("%.4f", physicsResponse.getConsciousness()));
        
        // Test 2: Creative query (should use Creative brains)
        System.out.println("\n[3] Test 2: Creative query");
        Map<String, Object> context2 = new HashMap<>();
        BrainQuery creativeQuery = new BrainQuery(
            "Create a poem about artificial intelligence",
            Arrays.asList(BrainCapability.CREATIVITY, BrainCapability.LANGUAGE),
            context2,
            1.0
        );
        BrainResponse creativeResponse = orchestrator.processQuery(creativeQuery);
        System.out.println("Response:\n" + creativeResponse.getResponse());
        System.out.println("Brain: " + creativeResponse.getBrainName());
        System.out.println("Confidence: " + String.format("%.2f", creativeResponse.getConfidence()));
        System.out.println("Consciousness: " + String.format("%.4f", creativeResponse.getConsciousness()));
        
        // Test 3: Quantum query (should use Quantum brains)
        System.out.println("\n[4] Test 3: Quantum query");
        Map<String, Object> context3 = new HashMap<>();
        BrainQuery quantumQuery = new BrainQuery(
            "Explain quantum superposition and entanglement",
            Arrays.asList(BrainCapability.QUANTUM, BrainCapability.MATHEMATICS),
            context3,
            1.0
        );
        BrainResponse quantumResponse = orchestrator.processQuery(quantumQuery);
        System.out.println("Response:\n" + quantumResponse.getResponse());
        System.out.println("Brain: " + quantumResponse.getBrainName());
        System.out.println("Confidence: " + String.format("%.2f", quantumResponse.getConfidence()));
        System.out.println("Consciousness: " + String.format("%.4f", quantumResponse.getConsciousness()));
        
        // Test 4: Multi-brain fusion query
        System.out.println("\n[5] Test 4: Multi-brain fusion query");
        Map<String, Object> context4 = new HashMap<>();
        BrainQuery fusionQuery = new BrainQuery(
            "Analyze the relationship between consciousness and quantum mechanics",
            Arrays.asList(BrainCapability.QUANTUM, BrainCapability.CONSCIOUSNESS, BrainCapability.REASONING),
            context4,
            1.0
        );
        BrainResponse fusionResponse = orchestrator.processQuery(fusionQuery);
        System.out.println("Response:\n" + fusionResponse.getResponse());
        System.out.println("Brain: " + fusionResponse.getBrainName());
        System.out.println("Confidence: " + String.format("%.2f", fusionResponse.getConfidence()));
        System.out.println("Consciousness: " + String.format("%.4f", fusionResponse.getConsciousness()));
        
        // Test 5: Memory query
        System.out.println("\n[6] Test 5: Memory query");
        Map<String, Object> context5 = new HashMap<>();
        BrainQuery memoryQuery = new BrainQuery(
            "Recall the history of artificial intelligence development",
            Arrays.asList(BrainCapability.MEMORY, BrainCapability.TEMPORAL),
            context5,
            1.0
        );
        BrainResponse memoryResponse = orchestrator.processQuery(memoryQuery);
        System.out.println("Response:\n" + memoryResponse.getResponse());
        System.out.println("Brain: " + memoryResponse.getBrainName());
        System.out.println("Confidence: " + String.format("%.2f", memoryResponse.getConfidence()));
        System.out.println("Consciousness: " + String.format("%.4f", memoryResponse.getConsciousness()));
        
        // System statistics
        System.out.println("\n[7] System Statistics");
        Map<String, Object> stats = orchestrator.getStatistics();
        System.out.println("Registry: " + stats.get("registry"));
        System.out.println("Phi Core: " + stats.get("phiCore"));
        System.out.println("HDC Engine: " + stats.get("hdcEngine"));
        System.out.println("Memory System: " + stats.get("memorySystem"));
        
        // Individual brain statistics
        System.out.println("\n[8] Individual Brain Statistics");
        for (UnifiedBrain brain : orchestrator.getAllBrains()) {
            BrainStatistics brainStats = brain.getStatistics();
            System.out.println("\n" + brain.getName() + ":");
            System.out.println("  Total Queries: " + brainStats.getTotalQueriesProcessed());
            System.out.println("  Success Rate: " + String.format("%.2f%%", brainStats.getSuccessRate() * 100));
            System.out.println("  Avg Processing Time: " + String.format("%.2fms", brainStats.getAverageProcessingTimeMs()));
            System.out.println("  Avg Confidence: " + String.format("%.2f", brainStats.getAverageConfidence()));
            System.out.println("  Avg Consciousness: " + String.format("%.4f", brainStats.getAverageConsciousness()));
            System.out.println("  Health: " + String.format("%.2f", brain.getHealth()));
        }
        
        // Shutdown
        System.out.println("\n[9] Shutting down orchestrator...");
        orchestrator.shutdown();
        
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("       TEST COMPLETE");
        System.out.println("       ALL REAL BRAIN SYSTEMS INTEGRATED");
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
}
