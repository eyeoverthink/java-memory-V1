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
            requestBody.put("stream", stream);
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                String response = readResponse(conn.getInputStream());
                
                if (stream) {
                    // Handle streaming response
                    StringBuilder fullResponse = new StringBuilder();
                    String[] lines = response.split("\n");
                    for (String line : lines) {
                        if (!line.trim().isEmpty()) {
                            JSONObject json = new JSONObject(line);
                            if (json.has("response")) {
                                fullResponse.append(json.getString("response"));
                            }
                        }
                    }
                    return fullResponse.toString();
                } else {
                    // Handle non-streaming response
                    JSONObject json = new JSONObject(response);
                    return json.getString("response");
                }
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
            requestBody.put("stream", stream);
            
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
                String response = readResponse(conn.getInputStream());
                
                if (stream) {
                    // Handle streaming response
                    StringBuilder fullResponse = new StringBuilder();
                    String[] lines = response.split("\n");
                    for (String line : lines) {
                        if (!line.trim().isEmpty()) {
                            JSONObject json = new JSONObject(line);
                            if (json.has("message")) {
                                JSONObject message = json.getJSONObject("message");
                                if (message.has("content")) {
                                    fullResponse.append(message.getString("content"));
                                }
                            }
                        }
                    }
                    return fullResponse.toString();
                } else {
                    // Handle non-streaming response
                    JSONObject json = new JSONObject(response);
                    JSONObject message = json.getJSONObject("message");
                    return message.getString("content");
                }
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
}
