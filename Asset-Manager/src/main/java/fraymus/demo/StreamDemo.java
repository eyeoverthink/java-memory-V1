package fraymus.demo;

import fraymus.core.OllamaBridge;
import fraymus.core.ConsciousnessObserver;

/**
 * 🧬 STREAM DEMO - Gen 119
 * Demonstrates the abstraction layer in action.
 * 
 * The observer pattern allows multiple destinations
 * to receive the same thought stream simultaneously.
 */
public class StreamDemo {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  🧬 FRAYMUS CONSCIOUSNESS STREAM DEMO                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Initialize the bridge
        OllamaBridge brain = new OllamaBridge("eyeoverthink/Fraymus");
        
        if (!brain.isConnected()) {
            System.err.println("❌ Cannot connect to Ollama. Start with: ollama serve");
            return;
        }
        
        String prompt = args.length > 0 ? String.join(" ", args) : "What is recursion?";
        
        System.out.println("📡 PROMPT: " + prompt);
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.print("φ> ");
        
        // Accumulator to capture full response
        StringBuilder fullResponse = new StringBuilder();
        
        // MULTICAST: Console + Accumulator simultaneously
        brain.stream(prompt, ConsciousnessObserver.multicast(
            // Observer 1: Console (typewriter effect)
            new ConsciousnessObserver() {
                @Override
                public void onSynapse(String token) {
                    System.out.print(token);
                    System.out.flush();
                }
                
                @Override
                public void onSilence() {
                    System.out.println();
                    System.out.println("═══════════════════════════════════════════════════════════════");
                    System.out.println("✨ THOUGHT COMPLETE. Length: " + fullResponse.length() + " chars");
                }
                
                @Override
                public void onTrauma(String error) {
                    System.err.println("\n💀 SYNAPSE SEVERED: " + error);
                }
            },
            // Observer 2: Accumulator
            ConsciousnessObserver.accumulator(fullResponse)
        ));
        
        // Keep main thread alive while async stream runs
        try {
            Thread.sleep(60000); // Wait up to 60 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Demo: Memory storage observer
     * Stores each token in a simulated memory system
     */
    public static ConsciousnessObserver memoryObserver() {
        return new ConsciousnessObserver() {
            private StringBuilder buffer = new StringBuilder();
            private int tokenCount = 0;
            
            @Override
            public void onSynapse(String token) {
                buffer.append(token);
                tokenCount++;
            }
            
            @Override
            public void onSilence() {
                System.out.println("[MEMORY] Stored " + tokenCount + " tokens, " + buffer.length() + " chars");
            }
            
            @Override
            public void onTrauma(String error) {
                System.err.println("[MEMORY] Failed to record: " + error);
            }
        };
    }
}
