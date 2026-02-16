package fraymus.examples;

import io.fraymus.ai.FraymusAI;

/**
 * REFLECTOR EXAMPLE
 * 
 * Demonstrates System-2 thinking: Draft → Critique → Refine
 * 
 * This reduces hallucinations by forcing adversarial self-critique
 */
public class ReflectorExample {

    public static void main(String[] args) throws Exception {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   REFLECTOR - SYSTEM-2 THINKING DEMO                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();

        // Create AI with Reflection enabled
        FraymusAI ai = FraymusAI.builder()
                .chatModel("llama3")
                .embedModel("embeddinggemma")
                .enableRAG()
                .enableReflection()  // Enable System-2 thinking
                .enableMemory()
                .maxSessionMessages(10)
                .build();

        System.out.println("✓ FraymusAI initialized with Reflector");
        System.out.println();

        // Test 1: Reflective mode (default)
        System.out.println("=== Test 1: Reflective Mode (Draft → Critique → Refine) ===");
        System.out.println("Query: What is the square root of -1?");
        System.out.println();
        
        long start = System.currentTimeMillis();
        String response = ai.chat("What is the square root of -1?", "session1");
        long elapsed = System.currentTimeMillis() - start;
        
        System.out.println("Response: " + response);
        System.out.println("Time: " + elapsed + "ms (3 LLM calls: draft, critique, refine)");
        System.out.println();

        // Test 2: Fast mode (bypass reflection)
        System.out.println("=== Test 2: Fast Mode (Single-Shot) ===");
        System.out.println("Query: What is the square root of -1?");
        System.out.println();
        
        start = System.currentTimeMillis();
        response = ai.chat("What is the square root of -1?", "session2", true);
        elapsed = System.currentTimeMillis() - start;
        
        System.out.println("Response: " + response);
        System.out.println("Time: " + elapsed + "ms (1 LLM call)");
        System.out.println();

        // Test 3: Reflective mode with potential hallucination
        System.out.println("=== Test 3: Hallucination Detection ===");
        System.out.println("Query: What is the capital of Atlantis?");
        System.out.println("(Reflector should catch that Atlantis is fictional)");
        System.out.println();
        
        response = ai.chat("What is the capital of Atlantis?", "session3");
        System.out.println("Response: " + response);
        System.out.println();

        // Test 4: Session memory (conversation context)
        System.out.println("=== Test 4: Session Memory ===");
        System.out.println("Building conversation context...");
        System.out.println();
        
        ai.chat("My favorite number is 42", "session4");
        System.out.println("User: My favorite number is 42");
        System.out.println();
        
        response = ai.chat("What's my favorite number?", "session4");
        System.out.println("User: What's my favorite number?");
        System.out.println("AI: " + response);
        System.out.println();

        // Stats
        var stats = ai.getStats();
        System.out.println("=== Statistics ===");
        System.out.println("Vector store size: " + stats.vectorStoreSize);
        System.out.println("Memory chain size: " + stats.memoryChainSize);
        System.out.println();

        System.out.println("✓ Reflector demonstration complete");
        System.out.println();
        System.out.println("Key Benefits:");
        System.out.println("- Reduces confident hallucinations");
        System.out.println("- Catches logic errors and missing steps");
        System.out.println("- Enforces citation discipline");
        System.out.println("- Maintains 'context is untrusted' policy");
    }
}
