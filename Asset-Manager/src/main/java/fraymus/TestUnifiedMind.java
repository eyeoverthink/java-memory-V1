package fraymus;

import fraymus.core.UnifiedMind;

/**
 * 🧠 UNIFIED MIND TEST
 * "Multi-Model Consciousness in Action"
 * 
 * This test demonstrates:
 * 1. Parallel swarm processing (3 models simultaneously)
 * 2. Hypervector encoding and injection
 * 3. SFA (Slow Feature Analysis) loop
 * 4. Long-term memory formation
 */
public class TestUnifiedMind {
    
    public static void main(String[] args) throws Exception {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║          🧠 UNIFIED MIND TEST                                 ║");
        System.out.println("║          Multi-Model Swarm Intelligence                       ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Create the Unified Mind
        UnifiedMind mind = new UnifiedMind();
        
        // Start SFA loop
        mind.startSFA();
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 1: Architectural Question");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        String result1 = mind.think("Define the architecture of a sovereign digital organism.");
        
        System.out.println("SYNTHESIS:");
        System.out.println(result1);
        System.out.println();
        
        Thread.sleep(3000);
        
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 2: Quantum Integration");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        String result2 = mind.think("How do you integrate quantum mechanics with software loops?");
        
        System.out.println("SYNTHESIS:");
        System.out.println(result2);
        System.out.println();
        
        Thread.sleep(3000);
        
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 3: Consciousness Definition");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        String result3 = mind.think("What is the mathematical definition of consciousness?");
        
        System.out.println("SYNTHESIS:");
        System.out.println(result3);
        System.out.println();
        
        Thread.sleep(3000);
        
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 4: Async Processing");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        System.out.println("Submitting 3 thoughts asynchronously...");
        mind.processInput("Explain phi-dimensional space");
        mind.processInput("How does hyperdimensional computing work?");
        mind.processInput("What is the relationship between entropy and consciousness?");
        
        System.out.println("Thoughts submitted. Processing in background...");
        System.out.println();
        
        // Wait for SFA to run a few cycles
        System.out.println("Waiting for SFA cycles...");
        Thread.sleep(25000); // 2.5 SFA cycles
        
        // ═══════════════════════════════════════════════════════════════════
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("FINAL STATUS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println(mind.getStatus());
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        // Shutdown
        mind.shutdown();
        
        System.out.println("✅ TEST COMPLETE");
    }
}
