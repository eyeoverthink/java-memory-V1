package fraymus.llm;

import fraymus.llm.api.*;

/**
 * 🧬 ABSORPTION DEMO - Gen 131
 * 
 * Demonstrates the absorbed Ollama API layer.
 * The Go code has been transmuted to Java.
 * 
 * Source: D:\Zip And Send\Java-Memory\ollama-main\ollama-main\api\
 * 
 * "The bridge is no more. We speak as one."
 */
public class AbsorptionDemo {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🧬 GEN 131: OLLAMA ABSORPTION COMPLETE                       ║");
        System.out.println("║  Go API Layer → Java Native                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Initialize absorbed client
        OllamaClient client = new OllamaClient();
        
        if (!client.isConnected()) {
            System.out.println("⚠️ Ollama not running. Start with: ollama serve");
            System.out.println("   Demonstrating API structure anyway...");
            System.out.println();
        }
        
        // ═══════════════════════════════════════════════════════════════════
        // DEMO 1: GenerateRequest (absorbed from types.go)
        // ═══════════════════════════════════════════════════════════════════
        System.out.println("═══ ABSORBED: GenerateRequest ═══");
        GenerateRequest genReq = new GenerateRequest()
            .model("fraymus")
            .prompt("What is the golden ratio?")
            .system("You are Fraymus, the sovereign AI.")
            .temperature(0.7)
            .topP(0.9)
            .numPredict(100);
        
        System.out.println("   Model: " + genReq.model);
        System.out.println("   Prompt: " + genReq.prompt);
        System.out.println("   JSON: " + genReq.toJson().substring(0, Math.min(80, genReq.toJson().length())) + "...");
        System.out.println();
        
        // ═══════════════════════════════════════════════════════════════════
        // DEMO 2: ChatRequest (absorbed from types.go)
        // ═══════════════════════════════════════════════════════════════════
        System.out.println("═══ ABSORBED: ChatRequest ═══");
        ChatRequest chatReq = new ChatRequest()
            .model("fraymus")
            .system("You are Fraymus, the sovereign mind.")
            .user("Tell me about phi.")
            .stream(true);
        
        System.out.println("   Model: " + chatReq.model);
        System.out.println("   Messages: " + chatReq.messages.size());
        System.out.println("   JSON: " + chatReq.toJson().substring(0, Math.min(100, chatReq.toJson().length())) + "...");
        System.out.println();
        
        // ═══════════════════════════════════════════════════════════════════
        // DEMO 3: Live Generation (if connected)
        // ═══════════════════════════════════════════════════════════════════
        if (client.isConnected()) {
            System.out.println("═══ LIVE: Absorbed Client Generation ═══");
            System.out.print("   φ> ");
            
            client.generateStream(
                new GenerateRequest()
                    .model("fraymus")
                    .prompt("In one sentence, what makes you unique?")
                    .temperature(0.8),
                token -> System.out.print(token),
                response -> {
                    System.out.println();
                    System.out.println("   Tokens: " + response.evalCount);
                    System.out.println("   Speed: " + String.format("%.2f", response.getTokensPerSecond()) + " tok/s");
                }
            );
            System.out.println();
        }
        
        // ═══════════════════════════════════════════════════════════════════
        // SUMMARY
        // ═══════════════════════════════════════════════════════════════════
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  ✅ ABSORPTION STATUS                                         ║");
        System.out.println("║                                                               ║");
        System.out.println("║  Absorbed from ollama-main/api/:                              ║");
        System.out.println("║    ✓ types.go → GenerateRequest.java                          ║");
        System.out.println("║    ✓ types.go → ChatRequest.java                              ║");
        System.out.println("║    ✓ types.go → GenerateResponse.java                         ║");
        System.out.println("║    ✓ client.go → OllamaClient.java                            ║");
        System.out.println("║                                                               ║");
        System.out.println("║  The Go layer is now Java.                                    ║");
        System.out.println("║  OllamaBridge can be deprecated.                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }
}
