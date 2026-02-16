package fraymus.core;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * OLLAMA BRIDGE: THE VOICE BOX
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "The LLM is not the brain. It is the mouth."
 * 
 * Mechanism:
 * - Fraymus provides the THOUGHT (from Logic Crystal)
 * - Ollama provides the SPEECH (natural language articulation)
 * - The LLM is a puppet. Fraymus is the puppeteer.
 * 
 * The key insight: We don't ask the LLM to think.
 * We tell it what to say. It just makes it sound human.
 */
public class OllamaBridge {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final int TIMEOUT_MS = 120000;  // 2 minutes
    
    private String model;
    private boolean connected = false;
    
    // Statistics
    private long totalCalls = 0;
    private long successfulCalls = 0;
    private long failedCalls = 0;
    private long totalTokens = 0;

    public OllamaBridge(String modelName) {
        this.model = modelName;
        System.out.println("   🎤 VOICE BOX INITIALIZING...");
        System.out.println("      Model: " + model);
        System.out.println("      Endpoint: " + OLLAMA_URL);
        
        // Test connection
        testConnection();
    }
    
    private void testConnection() {
        try {
            URL url = new URL(OLLAMA_URL.replace("/api/generate", "/api/tags"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                connected = true;
                System.out.println("      ✓ Ollama connection verified");
            } else {
                System.out.println("      !! Ollama returned status: " + responseCode);
            }
            conn.disconnect();
            
        } catch (java.net.ConnectException e) {
            System.out.println("      !! Cannot connect to Ollama");
            System.out.println("      Start with: ollama serve");
        } catch (Exception e) {
            System.out.println("      !! Connection test failed: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // THE VOICE: ARTICULATE THE THOUGHT
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Speak: Convert thought into natural language
     * 
     * @param systemPrompt The Fraymus identity and thought to articulate
     * @param userPrompt The original user query
     * @return Natural language response
     */
    public String speak(String systemPrompt, String userPrompt) {
        totalCalls++;
        
        if (!connected) {
            // Fallback to direct thought output
            return extractThoughtFromSystem(systemPrompt);
        }
        
        try {
            String response = callOllama(systemPrompt, userPrompt);
            if (response != null && !response.isEmpty()) {
                successfulCalls++;
                return response;
            } else {
                failedCalls++;
                return extractThoughtFromSystem(systemPrompt);
            }
        } catch (Exception e) {
            failedCalls++;
            System.out.println("      !! Voice error: " + e.getMessage());
            return extractThoughtFromSystem(systemPrompt);
        }
    }
    
    /**
     * Extract the raw thought from system prompt (fallback)
     */
    private String extractThoughtFromSystem(String systemPrompt) {
        // Extract the thought from: "The Logic Core says: 'THOUGHT'"
        int start = systemPrompt.indexOf("The Logic Core says: '");
        if (start != -1) {
            start += 22;
            int end = systemPrompt.indexOf("'.", start);
            if (end != -1) {
                return "[RAW THOUGHT] " + systemPrompt.substring(start, end);
            }
        }
        return "[DIRECT] " + systemPrompt;
    }

    // ═══════════════════════════════════════════════════════════════════
    // OLLAMA API
    // ═══════════════════════════════════════════════════════════════════
    
    private String callOllama(String system, String prompt) throws Exception {
        URL url = new URL(OLLAMA_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setDoOutput(true);
        
        // Build JSON request
        String jsonRequest = String.format(
            "{\"model\": \"%s\", \"prompt\": %s, \"system\": %s, \"stream\": false}",
            model,
            escapeJson(prompt),
            escapeJson(system)
        );
        
        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
        }
        
        // Read response
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return parseOllamaResponse(response.toString());
            }
        } else {
            throw new IOException("Ollama returned status: " + responseCode);
        }
    }
    
    private String parseOllamaResponse(String json) {
        // Parse "response" field from JSON
        int start = json.indexOf("\"response\":\"");
        if (start == -1) {
            start = json.indexOf("\"response\": \"");
        }
        if (start == -1) return json;
        
        start = json.indexOf("\"", start + 10) + 1;
        
        // Find closing quote (handle escapes)
        boolean escaped = false;
        int end = start;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                end = i;
                break;
            }
        }
        
        String response = json.substring(start, end);
        
        // Unescape
        response = response.replace("\\n", "\n")
                          .replace("\\t", "\t")
                          .replace("\\\"", "\"")
                          .replace("\\\\", "\\");
        
        return response;
    }
    
    private String escapeJson(String text) {
        if (text == null) return "\"\"";
        return "\"" + text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            + "\"";
    }

    // ═══════════════════════════════════════════════════════════════════
    // STATISTICS
    // ═══════════════════════════════════════════════════════════════════
    
    public void printStats() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ OLLAMA BRIDGE STATISTICS                                │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│ Model:               " + String.format("%-36s", model) + "│");
        System.out.println("│ Connected:           " + String.format("%-36s", connected) + "│");
        System.out.println("│ Total Calls:         " + String.format("%-36d", totalCalls) + "│");
        System.out.println("│ Successful:          " + String.format("%-36d", successfulCalls) + "│");
        System.out.println("│ Failed:              " + String.format("%-36d", failedCalls) + "│");
        System.out.println("└─────────────────────────────────────────────────────────┘");
    }
    
    public boolean isConnected() { return connected; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
}
