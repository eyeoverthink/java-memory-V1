package fraymus.ollama;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * THE OLLAMA SPINE
 * 
 * Role: Connects the Java Nervous System to your Local LLM
 * Abstraction: "Thought" is just an API call to localhost:11434
 * 
 * This replaces Broca.java with Real Intelligence.
 * Java doesn't "think" anymore - it commands Ollama to think.
 */
public class OllamaSpine {

    private static final String OLLAMA_BASE = "http://localhost:11434/api";
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final ObjectMapper M = new ObjectMapper();
    
    private final HttpClient http;
    private String modelName; // e.g., "llama3" or "mistral"
    private int requestCount = 0;
    private long totalThinkTime = 0;

    public OllamaSpine(String modelName) {
        this.modelName = modelName;
        this.http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();
        System.out.println(">>> [OLLAMA SPINE] Initialized with model: " + modelName);
    }

    /**
     * TRANSMIT THOUGHT
     * Sends the prompt + context (Transmudder data) to the AI
     */
    public String think(String prompt, String context) {
        long startTime = System.currentTimeMillis();
        requestCount++;
        
        try {
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(60000); // 60 second timeout for generation

            // Construct JSON Payload (The Abstraction)
            // We feed the "Transmudder" context as a System Prompt
            String jsonInput = buildJsonPayload(prompt, context);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return "[OLLAMA ERROR] HTTP " + responseCode + " - Check if Ollama is running";
            }

            // Read the Response (The Voice)
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            br.close();

            long thinkTime = System.currentTimeMillis() - startTime;
            totalThinkTime += thinkTime;
            
            System.out.println(">>> [OLLAMA] Response received in " + thinkTime + "ms");

            // Simple Parsing (Abstracting the JSON response)
            return extractResponseText(response.toString());

        } catch (java.net.ConnectException e) {
            return "[OLLAMA DISCONNECTED] Cannot connect to localhost:11434. Is Ollama running? Start with: ollama serve";
        } catch (java.net.SocketTimeoutException e) {
            return "[OLLAMA TIMEOUT] Generation took too long. Try a smaller prompt or faster model.";
        } catch (Exception e) {
            return "[OLLAMA ERROR] " + e.getMessage();
        }
    }

    /**
     * Build JSON payload for Ollama API
     */
    private String buildJsonPayload(String prompt, String context) {
        // Escape quotes in strings
        String escapedPrompt = prompt.replace("\"", "\\\"").replace("\n", "\\n");
        String escapedContext = context.replace("\"", "\\\"").replace("\n", "\\n");
        
        String fullPrompt;
        if (context != null && !context.trim().isEmpty()) {
            fullPrompt = "[CONTEXT: " + escapedContext + "]\\n\\nUSER: " + escapedPrompt;
        } else {
            fullPrompt = escapedPrompt;
        }
        
        return String.format(
            "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}",
            modelName, fullPrompt
        );
    }

    /**
     * Extract response text from JSON
     * Quick & Dirty extraction to avoid libraries
     */
    private String extractResponseText(String json) {
        try {
            int startIndex = json.indexOf("\"response\":\"");
            if (startIndex == -1) return json; // Fallback
            
            startIndex += 12; // Length of "\"response\":\""
            int endIndex = json.indexOf("\",\"done\"", startIndex);
            
            if (endIndex == -1) {
                endIndex = json.indexOf("\"}", startIndex);
            }
            
            if (endIndex > startIndex) {
                String response = json.substring(startIndex, endIndex);
                // Unescape JSON
                return response.replace("\\n", "\n")
                              .replace("\\\"", "\"")
                              .replace("\\t", "\t")
                              .replace("\\\\", "\\");
            }
            
            return json; // Fallback
        } catch (Exception e) {
            return json; // Fallback
        }
    }

    /**
     * Test connection to Ollama
     */
    public boolean testConnection() {
        try {
            URL url = new URL("http://localhost:11434/api/tags");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            
            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get available models
     */
    public String listModels() {
        try {
            URL url = new URL("http://localhost:11434/api/tags");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            
            return response.toString();
        } catch (Exception e) {
            return "[ERROR] " + e.getMessage();
        }
    }

    /**
     * Set model
     */
    public void setModel(String modelName) {
        this.modelName = modelName;
        System.out.println(">>> [OLLAMA SPINE] Model changed to: " + modelName);
    }

    /**
     * Get current model
     */
    public String getModel() {
        return modelName;
    }

    /**
     * Get request count
     */
    public int getRequestCount() {
        return requestCount;
    }

    /**
     * Get average think time
     */
    public long getAverageThinkTime() {
        return requestCount > 0 ? totalThinkTime / requestCount : 0;
    }

    /**
     * EMBED
     * Get embeddings for text chunks using /api/embed
     * Ollama returns L2-normalized vectors (unit length)
     * Cosine similarity = dot product for normalized vectors
     */
    public double[][] embed(String embedModel, List<String> inputs) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", embedModel);
            payload.put("input", inputs); // Supports batch input

            String body = M.writeValueAsString(payload);

            HttpRequest req = HttpRequest.newBuilder(URI.create(OLLAMA_BASE + "/embed"))
                .timeout(Duration.ofSeconds(120))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) {
                throw new RuntimeException("HTTP " + resp.statusCode() + ": " + resp.body());
            }

            JsonNode root = M.readTree(resp.body());
            JsonNode arr = root.path("embeddings");
            if (!arr.isArray()) {
                throw new RuntimeException("No embeddings in response: " + resp.body());
            }

            double[][] out = new double[arr.size()][];
            for (int i = 0; i < arr.size(); i++) {
                JsonNode v = arr.get(i);
                out[i] = new double[v.size()];
                for (int j = 0; j < v.size(); j++) {
                    out[i][j] = v.get(j).asDouble();
                }
            }
            
            System.out.println(">>> [OLLAMA] Embedded " + inputs.size() + " chunks (dim=" + out[0].length + ")");
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Embed failed: " + e.getMessage(), e);
        }
    }

    /**
     * Print statistics
     */
    public void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   OLLAMA SPINE STATISTICS                                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Model: " + modelName);
        System.out.println("  Connected: " + (testConnection() ? "Yes" : "No"));
        System.out.println("  Requests: " + requestCount);
        System.out.println("  Avg Think Time: " + getAverageThinkTime() + "ms");
        System.out.println("  Total Think Time: " + totalThinkTime + "ms");
    }
}
