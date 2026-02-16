package fraymus.nexus;

import fraymus.core.FraymusHTTP;
import fraymus.core.FraymusJSON;
import java.util.Map;
import java.util.HashMap;

/**
 * 🧠 OLLAMA BRIDGE
 * "The Interface to the Bicameral Mind"
 * 
 * Connects to Ollama's local API to access large language models.
 * Uses sovereign infrastructure (FraymusHTTP, FraymusJSON).
 * 
 * BICAMERAL ARCHITECTURE:
 * - Left Brain: Analysis, logic, structure
 * - Right Brain: Creativity, optimization, elegance
 * 
 * This bridge allows Fraymus to think with both hemispheres.
 */
public class OllamaBridge {
    
    private final String model;
    private final String ollamaUrl;
    private final int timeout;
    
    /**
     * Create bridge to Ollama
     * 
     * @param model Model name (e.g., "llama3:70b", "codellama", "mistral")
     */
    public OllamaBridge(String model) {
        this.model = model;
        this.ollamaUrl = "http://localhost:11434/api/generate";
        this.timeout = 120000; // 2 minutes for large models
    }
    
    /**
     * Ask the bicameral mind a question
     * 
     * @param prompt The question/task
     * @return The AI's response
     */
    public String ask(String prompt) {
        try {
            System.out.println("🧠 CONSULTING BICAMERAL MIND...");
            System.out.println("   Model: " + model);
            System.out.println("   Prompt length: " + prompt.length() + " chars");
            
            // Build request payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", model);
            payload.put("prompt", prompt);
            payload.put("stream", false); // Get complete response
            
            String jsonPayload = FraymusJSON.stringify(payload);
            
            // Send request
            long start = System.currentTimeMillis();
            String response = FraymusHTTP.post(ollamaUrl, jsonPayload, timeout);
            long elapsed = System.currentTimeMillis() - start;
            
            System.out.println("   Response received in " + elapsed + "ms");
            
            // Parse response
            Map<String, Object> jsonResponse = (Map<String, Object>) FraymusJSON.parse(response);
            
            if (jsonResponse.containsKey("error")) {
                throw new RuntimeException("Ollama error: " + jsonResponse.get("error"));
            }
            
            String result = (String) jsonResponse.get("response");
            
            if (result == null || result.isEmpty()) {
                throw new RuntimeException("Empty response from Ollama");
            }
            
            System.out.println("   Result length: " + result.length() + " chars");
            System.out.println("✅ BICAMERAL PROCESSING COMPLETE");
            
            return result;
            
        } catch (Exception e) {
            System.err.println("❌ BICAMERAL BRIDGE ERROR: " + e.getMessage());
            e.printStackTrace();
            return "// ERROR: Could not connect to Ollama\n// Make sure Ollama is running: ollama serve\n// And the model is available: ollama pull " + model;
        }
    }
    
    /**
     * Check if Ollama is available
     * 
     * @return true if Ollama is responding
     */
    public boolean isAvailable() {
        try {
            String healthUrl = "http://localhost:11434/api/tags";
            String response = FraymusHTTP.get(healthUrl, 5000);
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get available models
     * 
     * @return Array of model names
     */
    public String[] getAvailableModels() {
        try {
            String tagsUrl = "http://localhost:11434/api/tags";
            String response = FraymusHTTP.get(tagsUrl, 5000);
            
            Map<String, Object> json = (Map<String, Object>) FraymusJSON.parse(response);
            
            if (json.containsKey("models")) {
                java.util.List<Map<String, Object>> models = 
                    (java.util.List<Map<String, Object>>) json.get("models");
                
                return models.stream()
                    .map(m -> (String) m.get("name"))
                    .toArray(String[]::new);
            }
            
            return new String[0];
            
        } catch (Exception e) {
            System.err.println("Could not fetch models: " + e.getMessage());
            return new String[0];
        }
    }
    
    /**
     * Test the bridge
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║          🧠 OLLAMA BRIDGE TEST                                ║");
        System.out.println("║          Bicameral Mind Interface                             ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Test with a lightweight model first
        String testModel = args.length > 0 ? args[0] : "llama3.2";
        
        OllamaBridge bridge = new OllamaBridge(testModel);
        
        // Check availability
        System.out.println("Checking Ollama availability...");
        if (!bridge.isAvailable()) {
            System.err.println("❌ Ollama is not running!");
            System.err.println("   Start it with: ollama serve");
            System.exit(1);
        }
        System.out.println("✅ Ollama is available");
        System.out.println();
        
        // List models
        System.out.println("Available models:");
        String[] models = bridge.getAvailableModels();
        for (String model : models) {
            System.out.println("   - " + model);
        }
        System.out.println();
        
        // Test query
        System.out.println("Testing bicameral processing...");
        String testPrompt = "Optimize this code:\nfunction add(a,b){return a+b;}";
        
        String result = bridge.ask(testPrompt);
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("RESULT:");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println(result);
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
}
