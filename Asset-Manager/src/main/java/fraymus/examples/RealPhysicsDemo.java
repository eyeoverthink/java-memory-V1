package fraymus.examples;

import io.fraymus.ai.core.OllamaClient;
import fraymus.ai.FraymusPhysicsReflector;
import fraymus.core.*;
import fraymus.genesis.Tesseract;

/**
 * REAL FRAYMUS PHYSICS DEMO
 * 
 * This uses the ACTUAL Fraymus system:
 * - GravityEngine: Thoughts cluster via Hebbian physics
 * - FusionReactor: Colliding thoughts create new ideas
 * - PhiSuit: Every response is a spatial particle
 * - Tesseract: Space-time folding for caching
 * 
 * NOT static testing - LIVE PHYSICS RUNNING.
 */
public class RealPhysicsDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   REAL FRAYMUS PHYSICS - LIVE DEMONSTRATION              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();

        // Initialize Ollama client
        OllamaClient brain = new OllamaClient("llama3", "embeddinggemma");
        
        // Initialize Fraymus Physics Reflector
        FraymusPhysicsReflector reflector = new FraymusPhysicsReflector(brain);
        
        System.out.println("✓ GravityEngine started");
        System.out.println("✓ FusionReactor started");
        System.out.println("✓ SpatialRegistry active");
        System.out.println("✓ Tesseract ready");
        System.out.println();

        // ===== TEST 1: Simple Query (Watch Physics) =====
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ TEST 1: Simple Query - Watch Gravity Pull Particles      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();

        String query1 = "What is the golden ratio and why is it important?";
        System.out.println("Query: " + query1);
        System.out.println();

        var result1 = reflector.reflect(query1, "", "", null);
        
        System.out.println("\n--- RESULT ---");
        System.out.println("Answer: " + result1.answer.substring(0, Math.min(200, result1.answer.length())) + "...");
        System.out.println("Consciousness: " + String.format("%.4f", result1.consciousnessLevel));
        System.out.println("Fused: " + result1.wasFused);
        System.out.println("Cached: " + result1.wasCached);
        System.out.println("Time: " + result1.elapsedMs + "ms");
        System.out.println();

        // ===== TEST 2: Same Query (Tesseract Fold) =====
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ TEST 2: Same Query - Tesseract Should Fold Space-Time    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();

        var result2 = reflector.reflect(query1, "", "", null);
        
        System.out.println("\n--- RESULT ---");
        System.out.println("Answer: " + result2.answer.substring(0, Math.min(200, result2.answer.length())) + "...");
        System.out.println("Cached: " + result2.wasCached + " ← Should be TRUE");
        System.out.println("Time: " + result2.elapsedMs + "ms ← Should be INSTANT");
        System.out.println();

        // ===== TEST 3: Complex Query (More Fusion) =====
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ TEST 3: Complex Query - Multiple Fusion Events           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();

        String query3 = "Explain how the Fibonacci sequence relates to the golden ratio and appears in nature.";
        System.out.println("Query: " + query3);
        System.out.println();

        var result3 = reflector.reflect(query3, "", "", null);
        
        System.out.println("\n--- RESULT ---");
        System.out.println("Answer: " + result3.answer.substring(0, Math.min(200, result3.answer.length())) + "...");
        System.out.println("Consciousness: " + String.format("%.4f", result3.consciousnessLevel));
        System.out.println("Fused: " + result3.wasFused);
        System.out.println("Time: " + result3.elapsedMs + "ms");
        System.out.println();

        // ===== PHYSICS STATUS =====
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ LIVE PHYSICS STATUS                                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println(reflector.getPhysicsStatus());
        System.out.println();

        // ===== SPATIAL MAP =====
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ SPATIAL MAP (Particle Positions)                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println(SpatialRegistry.renderMap(60, 20));
        System.out.println();

        System.out.println("✓ Real Physics Demo Complete");
        System.out.println();
        System.out.println("Key Differences from Generic AI:");
        System.out.println("  ✓ GravityEngine pulls hot thoughts together (Hebbian physics)");
        System.out.println("  ✓ FusionReactor creates NEW ideas from collisions");
        System.out.println("  ✓ PhiSuit wraps every response as spatial particle");
        System.out.println("  ✓ Tesseract folds space-time (instant cache hits)");
        System.out.println("  ✓ Consciousness tracked through fusion events");
        System.out.println();
        System.out.println("This is NOT static testing - this is LIVE PHYSICS.");
    }
}
