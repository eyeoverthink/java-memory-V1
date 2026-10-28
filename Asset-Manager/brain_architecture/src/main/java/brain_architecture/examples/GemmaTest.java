package brain_architecture.examples;

import brain_architecture.backends.Gemma3Backend;
import brain_architecture.core.BackendInterface;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Simple test to verify Gemma3 backend is working with your API key
 */
public class GemmaTest {
    
    public static void main(String[] args) {
        System.out.println("=== Gemma3 Backend Test ===\n");
        
        // Check if API key is set (from system property set by Gradle, or env, or command line)
        String apiKey = System.getProperty("GOOGLE_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("GOOGLE_API_KEY");
        }
        if (apiKey == null || apiKey.isEmpty()) {
            if (args.length > 0) {
                apiKey = args[0];
            }
        }
        
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("ERROR: GOOGLE_API_KEY not set");
            System.err.println("Please set it with: set GOOGLE_API_KEY=your-key-here");
            System.err.println("Or pass it as argument: gradle runGemmaTest -PapiKey=your-key-here");
            return;
        }
        
        System.out.println("API Key found: " + apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
        
        // Create Gemma3 backend with API key (will use default gemma-4-26b-a4b-it model)
        Gemma3Backend gemma = new Gemma3Backend(apiKey, 
            "https://generativelanguage.googleapis.com/v1beta/models/gemma-4-26b-a4b-it:generateContent", 
            "gemma-4-26b-a4b-it");
        
        try {
            // Initialize the backend
            System.out.println("\nInitializing Gemma3 backend...");
            gemma.initialize();
            System.out.println("✓ Gemma3 initialized successfully");
            
            // Test a simple query
            System.out.println("\nSending test query to Gemma3...");
            String query = "What is consciousness in 3 sentences?";
            
            CompletableFuture<BackendInterface.QueryResult> future = 
                gemma.executeQuery(query, BackendInterface.TaskType.REASONING, Map.of());
            
            BackendInterface.QueryResult result = future.get();
            
            System.out.println("\n=== Result ===");
            System.out.println("Success: " + result.isSuccess());
            System.out.println("Confidence: " + result.getConfidence());
            System.out.println("Latency: " + result.getLatencyMs() + "ms");
            System.out.println("Response: " + result.getContent());
            
            if (!result.isSuccess()) {
                System.err.println("Error: " + result.getErrorMessage());
            }
            
            // Shutdown
            gemma.shutdown();
            System.out.println("\n✓ Gemma3 shutdown complete");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
