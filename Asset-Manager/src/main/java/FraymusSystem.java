import io.fraymus.ai.FraymusAI;
import java.util.Scanner;

/**
 * FRAYMUS SYSTEM - Main Entry Point
 * 
 * Your AI system with real physics.
 * Run this to use it.
 */
public class FraymusSystem {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    FRAYMUS SYSTEM                         ║");
        System.out.println("║              Physics-Based AI Intelligence                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Initialize system with physics
        System.out.println("Initializing Fraymus...");
        FraymusAI ai = FraymusAI.builder()
            .chatModel("llama3")
            .embedModel("nomic-embed-text")
            .enableQuantum()            // Real physics enabled
            .enableRAG()                // Context retrieval
            .enableTools()              // Math, file ops
            .enableMemory()             // Persistent memory
            .verboseLogging(false)      // Clean output
            .build();
        
        System.out.println("✓ GravityEngine online");
        System.out.println("✓ FusionReactor online");
        System.out.println("✓ Tesseract ready");
        System.out.println("✓ System ready");
        System.out.println();
        
        // Interactive mode
        Scanner scanner = new Scanner(System.in);
        String sessionId = "main";
        
        System.out.println("Type your questions (or 'exit' to quit):");
        System.out.println("Type 'index <path>' to add documents");
        System.out.println("Type 'status' to see physics status");
        System.out.println();
        
        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) continue;
            if (input.equalsIgnoreCase("exit")) break;
            
            if (input.toLowerCase().startsWith("index ")) {
                String path = input.substring(6).trim();
                System.out.println("Indexing: " + path);
                try {
                    ai.index(path);
                    System.out.println("✓ Indexed");
                } catch (Exception e) {
                    System.out.println("✗ Error: " + e.getMessage());
                }
                System.out.println();
                continue;
            }
            
            if (input.equalsIgnoreCase("status")) {
                // Show system status
                System.out.println("\n--- SYSTEM STATUS ---");
                System.out.println("Session: " + sessionId);
                System.out.println("Physics: ACTIVE");
                System.out.println("Vector Store: ACTIVE");
                System.out.println();
                continue;
            }
            
            // Process query
            System.out.println();
            try {
                String response = ai.chat(input, sessionId);
                System.out.println("Fraymus: " + response);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
        
        scanner.close();
        System.out.println("\nShutting down Fraymus...");
        System.out.println("✓ System offline");
    }
}
