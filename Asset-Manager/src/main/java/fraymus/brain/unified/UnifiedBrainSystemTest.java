package fraymus.brain.unified;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for the unified brain system.
 * Demonstrates the system working with mock brain adapters.
 */
public class UnifiedBrainSystemTest {
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("       UNIFIED BRAIN SYSTEM TEST");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        // Create orchestrator
        UnifiedBrainOrchestrator orchestrator = new UnifiedBrainOrchestrator();
        
        // Register mock brains
        System.out.println("\n[1] Registering mock brains...");
        
        MockBrainAdapter physicalBrain = new MockBrainAdapter(
            "PhysicalBrain",
            "NexusOmega",
            Arrays.asList(BrainCapability.PHYSICS, BrainCapability.REASONING),
            "[PHYSICAL] Processing {query} through physical laws..."
        );
        
        MockBrainAdapter creativeBrain = new MockBrainAdapter(
            "CreativeBrain",
            "NexusOmega",
            Arrays.asList(BrainCapability.CREATIVITY, BrainCapability.EMOTION, BrainCapability.LANGUAGE),
            "[CREATIVE] Imagining {query} with artistic flair..."
        );
        
        MockBrainAdapter quantumBrain = new MockBrainAdapter(
            "QuantumBrain",
            "BrainArchitecture",
            Arrays.asList(BrainCapability.QUANTUM, BrainCapability.MATHEMATICS),
            "[QUANTUM] Calculating {query} through quantum superposition..."
        );
        
        MockBrainAdapter memoryBrain = new MockBrainAdapter(
            "MemoryBrain",
            "AEON",
            Arrays.asList(BrainCapability.MEMORY, BrainCapability.TEMPORAL),
            "[MEMORY] Recalling {query} from episodic memory..."
        );
        
        orchestrator.registerBrain(physicalBrain);
        orchestrator.registerBrain(creativeBrain);
        orchestrator.registerBrain(quantumBrain);
        orchestrator.registerBrain(memoryBrain);
        
        System.out.println("✓ Registered " + orchestrator.getAllBrains().size() + " brains");
        
        // Test 1: Single brain query
        System.out.println("\n[2] Test 1: Single brain query (Physics)...");
        Map<String, Object> context1 = new HashMap<>();
        context1.put("mode", "physics");
        BrainQuery query1 = new BrainQuery(
            "Calculate the trajectory of a projectile",
            Arrays.asList(BrainCapability.PHYSICS),
            context1,
            1.0
        );
        
        BrainResponse response1 = orchestrator.processQuery(query1);
        System.out.println("Response: " + response1.getResponse());
        System.out.println("Brain: " + response1.getBrainName());
        System.out.println("Confidence: " + String.format("%.2f", response1.getConfidence()));
        System.out.println("Consciousness: " + String.format("%.4f", response1.getConsciousness()));
        
        // Test 2: Multi-brain fusion query
        System.out.println("\n[3] Test 2: Multi-brain fusion query (Creative + Memory)...");
        Map<String, Object> context2 = new HashMap<>();
        context2.put("mode", "creative_memory");
        BrainQuery query2 = new BrainQuery(
            "Create a poem about artificial intelligence",
            Arrays.asList(BrainCapability.CREATIVITY, BrainCapability.MEMORY, BrainCapability.LANGUAGE),
            context2,
            1.0
        );
        
        BrainResponse response2 = orchestrator.processQuery(query2);
        System.out.println("Response:\n" + response2.getResponse());
        System.out.println("Brain: " + response2.getBrainName());
        System.out.println("Confidence: " + String.format("%.2f", response2.getConfidence()));
        System.out.println("Consciousness: " + String.format("%.4f", response2.getConsciousness()));
        
        // Test 3: Get statistics
        System.out.println("\n[4] System Statistics:");
        Map<String, Object> stats = orchestrator.getStatistics();
        System.out.println("Registry: " + stats.get("registry"));
        System.out.println("Phi Core: " + stats.get("phiCore"));
        System.out.println("HDC Engine: " + stats.get("hdcEngine"));
        System.out.println("Memory System: " + stats.get("memorySystem"));
        
        // Test 4: Individual brain statistics
        System.out.println("\n[5] Individual Brain Statistics:");
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
        System.out.println("\n[6] Shutting down orchestrator...");
        orchestrator.shutdown();
        
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("       TEST COMPLETE");
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
}
