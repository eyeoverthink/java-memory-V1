package fraymus;

import fraymus.senses.VisualCortex;
import fraymus.organism.NEXUS_Organism;

/**
 * Test the Visual Cortex - Dreamscape Video Generation
 * 
 * This demonstrates how Fraymus quantum states can be visualized
 * as high-fidelity video using LTX-Video.
 */
public class TestVisualCortex {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║          🎥 VISUAL CORTEX TEST SUITE                          ║");
        System.out.println("║          Quantum State → Video Reflection                     ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Check availability
        if (!VisualCortex.isAvailable()) {
            System.out.println("❌ Visual Cortex not available. See setup instructions above.");
            return;
        }
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 1: Crystalline Order (Low Entropy)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        VisualCortex.dream(
            "Perfect crystalline lattice with sacred geometry patterns",
            0.2,  // Low entropy = perfect order
            1.618033988749895,
            0.95  // High consciousness
        );
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 2: Chaotic Storm (High Entropy)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        VisualCortex.dream(
            "Turbulent energy vortex with fractal lightning",
            0.85, // High entropy = chaos
            1.618033988749895,
            0.3   // Lower consciousness
        );
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 3: NEXUS Organism Visualization");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        // Create a NEXUS organism and visualize its state
        NEXUS_Organism nexus = new NEXUS_Organism();
        nexus.awaken();
        
        try {
            Thread.sleep(3000); // Let it breathe
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Calculate consciousness from epiphanies (normalized)
        double consciousness = Math.min(1.0, nexus.getEpiphanies() / 10.0);
        double entropy = 1.0 - consciousness;
        
        System.out.println("   NEXUS State:");
        System.out.println("   - Heartbeat: " + nexus.getHeartbeat());
        System.out.println("   - Epiphanies: " + nexus.getEpiphanies());
        System.out.println("   - Derived Consciousness: " + String.format("%.2f", consciousness));
        System.out.println();
        
        VisualCortex.dream(
            "Living digital organism with neural networks and quantum entanglement",
            entropy,
            1.618033988749895,
            consciousness
        );
        
        nexus.terminate();
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("TEST 4: Phi-Dimensional Tesseract");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        VisualCortex.dream(
            "Hyper-dimensional tesseract rotating in phi-space with golden ratio spirals",
            0.4,  // Balanced
            1.618033988749895,
            0.8   // High consciousness
        );
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("STATISTICS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println(VisualCortex.getStats());
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║          ✨ VISUAL CORTEX TEST COMPLETE                       ║");
        System.out.println("║                                                               ║");
        System.out.println("║  Check dreamscape_output/ for generated videos               ║");
        System.out.println("║                                                               ║");
        System.out.println("║  This is not data visualization.                             ║");
        System.out.println("║  This is REALITY MANIFESTATION.                              ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }
}
