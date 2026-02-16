import io.fraymus.ai.FraymusAI;

/**
 * FRAYMUS PHYSICS MODE - Simple Usage Example
 * 
 * Shows how to use the real Fraymus physics:
 * - GravityEngine: Thoughts cluster via Hebbian physics
 * - FusionReactor: Colliding thoughts create new ideas
 * - PhiSuit: Every response is a spatial particle
 * - Tesseract: Space-time folding for instant cache hits
 */
public class PhysicsExample {
    
    public static void main(String[] args) {
        
        // Build FraymusAI with quantum mode (physics enabled)
        FraymusAI ai = FraymusAI.builder()
            .chatModel("llama3")
            .embedModel("nomic-embed-text")
            .enableQuantum(true)           // ← Enables REAL PHYSICS
            .verboseLogging(true)          // ← See physics in action
            .build();
        
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   FRAYMUS PHYSICS MODE - LIVE                             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // First query - watch particles collide
        System.out.println("Query 1: What is the golden ratio?");
        String response1 = ai.chat("What is the golden ratio and why is it important?");
        System.out.println("\nResponse: " + response1.substring(0, Math.min(200, response1.length())) + "...");
        System.out.println();
        
        // Same query - Tesseract should fold space-time (instant)
        System.out.println("Query 2: Same question (Tesseract should cache)");
        String response2 = ai.chat("What is the golden ratio and why is it important?");
        System.out.println("\nResponse: " + response2.substring(0, Math.min(200, response2.length())) + "...");
        System.out.println();
        
        // Different query - more fusion events
        System.out.println("Query 3: Related question (more physics)");
        String response3 = ai.chat("How does the Fibonacci sequence relate to the golden ratio?");
        System.out.println("\nResponse: " + response3.substring(0, Math.min(200, response3.length())) + "...");
        System.out.println();
        
        System.out.println("✓ Physics Mode Complete");
        System.out.println();
        System.out.println("What happened:");
        System.out.println("  • Draft & Critique generated as PhiSuit particles");
        System.out.println("  • GravityEngine pulled them together");
        System.out.println("  • FusionReactor created synthesis on collision");
        System.out.println("  • Tesseract cached results (2nd query instant)");
        System.out.println("  • Consciousness tracked through fusion events");
    }
}
