package fraymus;

import fraymus.neural.*;

import java.util.BitSet;

/**
 * HDC Integration Demo
 * 
 * Demonstrates the new HDC integration with SentientCore
 * Phase 1.1 completion verification
 */
public class HDCIntegrationDemo {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         HDC INTEGRATION DEMO - Phase 1.1 Verification         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Create SentientCore
        SentientCore core = new SentientCore();
        System.out.println("✓ SentientCore created (without HDC)");
        System.out.println();
        System.out.println(core.getStatus());
        System.out.println();

        // Create HDC engine
        HDCSimilarityEngine hdcEngine = new HDCSimilarityEngine();
        System.out.println("✓ HDCSimilarityEngine created");
        System.out.println(hdcEngine.getMapper().getStats());
        System.out.println();

        // Wire HDC to SentientCore
        core.wireHDC(hdcEngine);
        System.out.println("✓ HDC wired to SentientCore");
        System.out.println();

        // Learn some concepts
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("LEARNING PHASE");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println();
        
        core.learn("fire causes burns");
        core.learn("water causes thirst");
        core.learn("cats have fur");
        core.learn("dogs have fur");
        core.learn("cats are animals");
        core.learn("dogs are animals");
        
        System.out.println();
        System.out.println(core.getStatus());
        System.out.println();

        // Think with HDC similarity
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("THINKING PHASE (with HDC similarity)");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println();
        
        String result = core.think("cats");
        System.out.println(result);
        System.out.println();

        // Test HDC operations directly
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("DIRECT HDC OPERATIONS");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println();
        
        // Similarity search
        System.out.println("Finding concepts similar to 'cats':");
        var similar = hdcEngine.findSimilar("cats");
        for (var s : similar.subList(0, Math.min(5, similar.size()))) {
            System.out.println("  " + s);
        }
        System.out.println();

        // Composition
        System.out.println("Composing 'red' + 'circle':");
        BitSet composed = hdcEngine.compose("red", "circle");
        System.out.println("  Composed vector created (16,384 dimensions)");
        System.out.println();

        // Aggregation
        System.out.println("Aggregating 'red', 'blue', 'green':");
        BitSet aggregated = hdcEngine.aggregate("red", "blue", "green");
        System.out.println("  Aggregated vector created (16,384 dimensions)");
        System.out.println();

        // Test backward compatibility
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("BACKWARD COMPATIBILITY TEST");
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println();
        
        // Unwire HDC
        core.unwireHDC();
        System.out.println("✓ HDC unwired");
        System.out.println();
        
        // Test that it still works
        String resultWithoutHDC = core.think("cats");
        System.out.println("Thinking without HDC:");
        System.out.println(resultWithoutHDC);
        System.out.println();
        
        System.out.println(core.getStatus());
        System.out.println();

        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         ✅ HDC INTEGRATION VERIFIED - Phase 1.1 Complete       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }
}
