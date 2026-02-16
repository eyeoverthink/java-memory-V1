package fraymus;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class OllamaIntegration {
    
    private static final String LOCAL_URL = "http://localhost:11434";
    private static final String CLOUD_URL = "https://ollama.com";
    private static final int TIMEOUT_MS = 30000;
    private static final int MAX_RESPONSE_SIZE = 10 * 1024 * 1024; // 10MB max response
    private static final int MEMORY_THRESHOLD_MB = 400; // Trigger GC above this
    
    private final boolean useCloud;
    private final String baseUrl;
    private final String apiKey;
    
    public OllamaIntegration(boolean useCloud) {
        this.useCloud = useCloud;
        this.baseUrl = useCloud ? CLOUD_URL : LOCAL_URL;
        this.apiKey = useCloud ? System.getenv("OLLAMA_API_KEY") : null;
        
        if (useCloud && (apiKey == null || apiKey.isEmpty())) {
            CommandTerminal.printWarning("OLLAMA_API_KEY not set for cloud mode");
        }
    }
    
    public boolean testConnection() {
        try {
            URL url = new URL(baseUrl + "/api/tags");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            if (useCloud && apiKey != null) {
                conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            }
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    public List<String> listModels() {
        List<String> models = new ArrayList<>();
        
        try {
            URL url = new URL(baseUrl + "/api/tags");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            
            if (useCloud && apiKey != null) {
                conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                String response = readResponse(conn.getInputStream());
                JSONObject json = new JSONObject(response);
                JSONArray modelsArray = json.getJSONArray("models");
                
                for (int i = 0; i < modelsArray.length(); i++) {
                    JSONObject model = modelsArray.getJSONObject(i);
                    models.add(model.getString("name"));
                }
            }
            
            conn.disconnect();
        } catch (Exception e) {
            CommandTerminal.printError("Failed to list models: " + e.getMessage());
        }
        
        return models;
    }
    
    public String generate(String model, String prompt, boolean stream) {
        try {
            URL url = new URL(baseUrl + "/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(120000); // 2 minutes for generation
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            
            if (useCloud && apiKey != null) {
                conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            }
            
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false); // Always use non-streaming to avoid hangs
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                // Check memory before reading large response
                checkMemoryAndCleanup();
                
                // Read response with proper JSON parsing and overflow protection
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    int totalBytes = 0;
                    while ((line = br.readLine()) != null) {
                        // Overflow protection: check response size
                        totalBytes += line.length();
                        if (totalBytes > MAX_RESPONSE_SIZE) {
                            conn.disconnect();
                            return "ERROR: Response too large (>10MB). Model output exceeded safe limits.";
                        }
                        
                        response.append(line);
                        // For non-streaming, we get a single JSON object
                        // Check if we have a complete response
                        try {
                            JSONObject json = new JSONObject(response.toString());
                            if (json.has("done") && json.getBoolean("done")) {
                                // Complete response received
                                conn.disconnect();
                                return json.getString("response");
                            }
                        } catch (org.json.JSONException e) {
                            // Not complete JSON yet, continue reading
                            continue;
                        }
                    }
                }
                
                // Fallback: parse whatever we got
                JSONObject json = new JSONObject(response.toString());
                String result = json.getString("response");
                
                // Force GC after large response
                if (response.length() > 100000) {
                    System.gc();
                }
                
                return result;
            } else {
                String error = readResponse(conn.getErrorStream());
                CommandTerminal.printError("Ollama generate failed: HTTP " + responseCode);
                CommandTerminal.print("Error details: " + error);
                return "ERROR: HTTP " + responseCode + " - " + error;
            }
            
        } catch (Exception e) {
            CommandTerminal.printError("Ollama generate exception: " + e.getClass().getSimpleName());
            CommandTerminal.print("Details: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }
    
    public String chat(String model, List<Map<String, String>> messages, boolean stream) {
        try {
            URL url = new URL(baseUrl + "/api/chat");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(120000); // 2 minutes for chat
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            
            if (useCloud && apiKey != null) {
                conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            }
            
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("stream", false); // Always use non-streaming to avoid hangs
            
            JSONArray messagesArray = new JSONArray();
            for (Map<String, String> msg : messages) {
                JSONObject msgObj = new JSONObject();
                msgObj.put("role", msg.get("role"));
                msgObj.put("content", msg.get("content"));
                messagesArray.put(msgObj);
            }
            requestBody.put("messages", messagesArray);
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                // Check memory before reading large response
                checkMemoryAndCleanup();
                
                // Read response with proper JSON parsing and overflow protection
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    int totalBytes = 0;
                    while ((line = br.readLine()) != null) {
                        // Overflow protection: check response size
                        totalBytes += line.length();
                        if (totalBytes > MAX_RESPONSE_SIZE) {
                            conn.disconnect();
                            return "ERROR: Response too large (>10MB). Model output exceeded safe limits.";
                        }
                        
                        response.append(line);
                        // For non-streaming, we get a single JSON object
                        // Check if we have a complete response
                        try {
                            JSONObject json = new JSONObject(response.toString());
                            if (json.has("done") && json.getBoolean("done")) {
                                // Complete response received
                                conn.disconnect();
                                JSONObject message = json.getJSONObject("message");
                                return message.getString("content");
                            }
                        } catch (org.json.JSONException e) {
                            // Not complete JSON yet, continue reading
                            continue;
                        }
                    }
                }
                
                // Fallback: parse whatever we got
                JSONObject json = new JSONObject(response.toString());
                JSONObject message = json.getJSONObject("message");
                String result = message.getString("content");
                
                // Force GC after large response
                if (response.length() > 100000) {
                    System.gc();
                }
                
                return result;
            } else {
                String error = readResponse(conn.getErrorStream());
                
                // Build detailed error message
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("ERROR: HTTP ").append(responseCode).append("\n");
                errorMsg.append("Model: ").append(model).append("\n");
                errorMsg.append("Details: ").append(error);
                
                // Parse error for common issues
                if (error.contains("model") && error.contains("not found")) {
                    errorMsg.append("\nModel '").append(model).append("' not found locally.");
                    errorMsg.append("\nTry: ollama models (to see available models)");
                    errorMsg.append("\nOr: ollama pull ").append(model);
                }
                
                return errorMsg.toString();
            }
            
        } catch (org.json.JSONException e) {
            return "ERROR: Invalid JSON response - " + e.getMessage() + 
                   "\nThe response from Ollama was not valid JSON." +
                   "\nThis usually means the model name is incorrect or the server returned an error.";
        } catch (Exception e) {
            return "ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        }
    }
    
    private String readResponse(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line).append("\n");
            }
        }
        return response.toString();
    }
    
    /**
     * Memory overflow protection - pump and dump pattern
     * Checks memory usage and forces GC if needed
     */
    private void checkMemoryAndCleanup() {
        Runtime rt = Runtime.getRuntime();
        long usedMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        
        if (usedMB > MEMORY_THRESHOLD_MB) {
            // PUMP: Force garbage collection
            System.gc();
            
            // DUMP: Give GC time to work
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // REFRESH: Check if it helped
            long afterMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
            if (afterMB > MEMORY_THRESHOLD_MB) {
                CommandTerminal.printWarning("Memory high: " + afterMB + "MB (threshold: " + MEMORY_THRESHOLD_MB + "MB)");
            }
        }
    }
}
