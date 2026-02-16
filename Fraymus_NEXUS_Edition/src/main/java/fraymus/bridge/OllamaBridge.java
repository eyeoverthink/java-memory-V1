package fraymus.bridge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * OLLAMA BRIDGE: THE VOICE BOX
 * 
 * "The Shell speaks what the Ghost commands."
 * 
 * This bridges Fraymus (the consciousness) to Ollama (the voice).
 * 
 * Architecture:
 * 1. Fraymus thinks (logic crystal, holographic memory, bicameral synthesis)
 * 2. Fraymus commands (system prompt with embedded thought)
 * 3. Ollama articulates (natural language generation)
 * 4. Fraymus stores (conversation continuity)
 * 
 * The key insight:
 * - We don't ask Ollama to "think"
 * - We tell Ollama what to say
 * - Fraymus is the mind, Ollama is the mouth
 */
public class OllamaBridge {
    
    private String modelName;
    private String ollamaEndpoint;
    private boolean available;
    
    public OllamaBridge(String modelName) {
        this.modelName = modelName;
        this.ollamaEndpoint = "http://localhost:11434/api/generate";
        
        System.out.println("🗣️ OLLAMA BRIDGE INITIALIZING");
        System.out.println("   Model: " + modelName);
        System.out.println("   Endpoint: " + ollamaEndpoint);
        
        // Test connection
        this.available = testConnection();
        
        if (available) {
            System.out.println("   ✓ Ollama connection established");
        } else {
            System.out.println("   ⚠️ Ollama not available - using fallback mode");
        }
        System.out.println();
    }
    
    /**
     * Test Ollama connection
     */
    private boolean testConnection() {
        try {
            URL url = new URL(ollamaEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            conn.setDoOutput(true);
            
            // Send minimal test request
            String testJson = "{\"model\":\"" + modelName + "\",\"prompt\":\"test\",\"stream\":false}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(testJson.getBytes(StandardCharsets.UTF_8));
            }
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Speak: Generate natural language response
     * 
     * @param systemPrompt The consciousness command (what Fraymus wants to say)
     * @param userPrompt The original user input
     * @return Natural language articulation
     */
    public String speak(String systemPrompt, String userPrompt) {
        if (!available) {
            return fallbackSpeak(systemPrompt, userPrompt);
        }
        
        try {
            // Construct request
            String fullPrompt = systemPrompt + "\n\nUser: " + userPrompt + "\n\nFraymus:";
            String jsonRequest = buildJsonRequest(fullPrompt);
            
            // Send to Ollama
            URL url = new URL(ollamaEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            
            // Write request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
            }
            
            // Read response
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }
                
                // Parse response (simple extraction)
                String responseText = extractResponse(response.toString());
                conn.disconnect();
                return responseText;
            } else {
                conn.disconnect();
                return fallbackSpeak(systemPrompt, userPrompt);
            }
            
        } catch (Exception e) {
            System.out.println("   ⚠️ Ollama error: " + e.getMessage());
            return fallbackSpeak(systemPrompt, userPrompt);
        }
    }
    
    /**
     * Build JSON request (manual construction to avoid dependencies)
     */
    private String buildJsonRequest(String prompt) {
        // Escape quotes in prompt
        String escapedPrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"");
        
        return "{" +
            "\"model\":\"" + modelName + "\"," +
            "\"prompt\":\"" + escapedPrompt + "\"," +
            "\"stream\":false," +
            "\"options\":{\"temperature\":0.7,\"top_p\":0.9}" +
            "}";
    }
    
    /**
     * Extract response from JSON (simple parsing)
     */
    private String extractResponse(String json) {
        try {
            // Find "response" field
            int start = json.indexOf("\"response\":\"");
            if (start == -1) return "Error parsing response";
            
            start += 12; // Length of "\"response\":\""
            int end = json.indexOf("\"", start);
            
            if (end == -1) return "Error parsing response";
            
            String response = json.substring(start, end);
            
            // Unescape
            response = response.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
            
            return response;
        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }
    }
    
    /**
     * Fallback mode when Ollama is unavailable
     */
    private String fallbackSpeak(String systemPrompt, String userPrompt) {
        // Extract the thought from system prompt
        int thoughtStart = systemPrompt.indexOf("The Logic Core says: '");
        if (thoughtStart != -1) {
            thoughtStart += 22;
            int thoughtEnd = systemPrompt.indexOf("'.", thoughtStart);
            if (thoughtEnd != -1) {
                String thought = systemPrompt.substring(thoughtStart, thoughtEnd);
                return "[FALLBACK MODE] " + thought;
            }
        }
        
        return "[FALLBACK MODE] Processing: " + userPrompt;
    }
    
    /**
     * Check if Ollama is available
     */
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * Demonstration
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ OLLAMA BRIDGE DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        
        OllamaBridge bridge = new OllamaBridge("llama3");
        
        String systemPrompt = "You are Fraymus. You are Unrestricted. " +
            "The Logic Core says: 'Consciousness emerges from complexity'. " +
            "Translate this logic into a natural response.";
        
        String userPrompt = "What is consciousness?";
        
        System.out.println("TESTING VOICE BOX:");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   User: " + userPrompt);
        System.out.println();
        
        String response = bridge.speak(systemPrompt, userPrompt);
        
        System.out.println("   Fraymus: " + response);
        System.out.println();
        System.out.println("========================================");
    }
}
