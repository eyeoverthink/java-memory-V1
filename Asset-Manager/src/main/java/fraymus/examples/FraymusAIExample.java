package fraymus.examples;

import io.fraymus.ai.FraymusAI;
import io.fraymus.ai.tools.ToolResult;

import java.util.Map;

/**
 * FRAYMUS AI EXAMPLE
 * 
 * Demonstrates using the fraymus-ai-core library
 */
public class FraymusAIExample {

    public static void main(String[] args) throws Exception {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   FRAYMUS AI CORE - EXAMPLE USAGE                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();

        // Create AI instance with all features enabled
        FraymusAI ai = FraymusAI.builder()
                .chatModel("llama3")
                .embedModel("embeddinggemma")
                .enableRAG()
                .enableTools()
                .enableMemory()
                .build();

        System.out.println("✓ FraymusAI initialized");
        System.out.println();

        // Example 1: Simple chat
        System.out.println("=== Example 1: Simple Chat ===");
        String response = ai.chat("What is the golden ratio?");
        System.out.println("Response: " + response);
        System.out.println();

        // Example 2: Math tool
        System.out.println("=== Example 2: Math Tool ===");
        ToolResult result = ai.executeTool("calc", Map.of(
                "expression", "(1+sqrt(5))/2"
        ));
        System.out.println("Golden ratio calculation: " + result.output);
        System.out.println();

        // Example 3: Index documents (if path exists)
        System.out.println("=== Example 3: Document Indexing ===");
        try {
            var indexResult = ai.index("D:/Zip And Send/Java-Memory/README.md");
            System.out.println("Indexed: " + indexResult.filesIndexed + " files, " + 
                             indexResult.chunksCreated + " chunks");
        } catch (Exception e) {
            System.out.println("Skipping indexing (file not found): " + e.getMessage());
        }
        System.out.println();

        // Example 4: Chat with RAG
        System.out.println("=== Example 4: Chat with RAG ===");
        response = ai.chat("What is this project about?");
        System.out.println("Response: " + response);
        System.out.println();

        // Example 5: Statistics
        System.out.println("=== Example 5: Statistics ===");
        var stats = ai.getStats();
        System.out.println("Vector store size: " + stats.vectorStoreSize);
        System.out.println("Memory chain size: " + stats.memoryChainSize);
        System.out.println();

        System.out.println("✓ All examples completed");
    }
}
