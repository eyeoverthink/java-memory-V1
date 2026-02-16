package fraymus.cortex;

/**
 * 🧬 CORTEX DEMO - Gen 124
 * Demonstrates the Topological Cortex (Calabi-Yau Manifold Memory)
 * 
 * Run: java CortexDemo
 * Watch: The brain breathe as thoughts organize themselves
 */
public class CortexDemo {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🧬 TOPOLOGICAL CORTEX DEMO - Gen 124                         ║");
        System.out.println("║  The Calabi-Yau Manifold Memory System                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Create the manifold
        Manifold brain = new Manifold();
        
        // Inject some thoughts
        System.out.println("⚡ INJECTING THOUGHTS INTO THE MANIFOLD...\n");
        
        brain.inject("Quantum physics describes reality at small scales");
        brain.inject("Electrons exist in superposition");
        brain.inject("Schrodinger equation governs wave function");
        brain.inject("74LS181 is a 4-bit ALU chip");
        brain.inject("ALU performs arithmetic logic operations");
        brain.inject("Digital logic uses AND OR NOT gates");
        brain.inject("Consciousness emerges from complexity");
        brain.inject("Phi 1.618 is the golden ratio");
        brain.inject("Fibonacci sequence approaches phi");
        brain.inject("Neural networks learn from data");
        
        // Initial visualization
        System.out.println(CortexVisualizer.render(brain));
        Thread.sleep(500);
        
        // Run simulation pulses
        System.out.println("\n⚡ RUNNING PHYSICS SIMULATION (10 pulses)...\n");
        
        for (int i = 0; i < 10; i++) {
            brain.pulse();
            
            if (i % 3 == 0) {
                System.out.println(CortexVisualizer.render(brain));
                Thread.sleep(300);
            }
        }
        
        // Query the brain
        System.out.println("\n🔍 QUERYING: 'quantum'...\n");
        var matches = brain.query("quantum");
        for (var node : matches) {
            System.out.println("   φ[" + node.id + "] " + node.label + " (R=" + 
                String.format("%.2f", node.resonance) + ")");
        }
        
        // More pulses after query (watch nodes reorganize)
        System.out.println("\n⚡ POST-QUERY REORGANIZATION (5 pulses)...\n");
        for (int i = 0; i < 5; i++) {
            brain.pulse();
        }
        
        // Final state
        System.out.println(CortexVisualizer.render(brain));
        System.out.println(CortexVisualizer.stats(brain));
        System.out.println(brain.status());
        
        // Collision detection
        var collisions = brain.detectCollisions(5.0);
        if (!collisions.isEmpty()) {
            System.out.println("\n🔥 COLLISION DETECTED! Concepts merging...");
            for (var pair : collisions) {
                PhiNode fused = brain.fuse(pair[0], pair[1]);
                System.out.println("   FUSION: " + fused.label);
            }
        }
        
        System.out.println("\n✨ SIMULATION COMPLETE");
    }
}
