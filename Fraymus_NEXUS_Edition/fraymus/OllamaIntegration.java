package fraymus;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * SANDBOX TEST - Ollama API Integration
 * Tests both local and cloud model access
 * API Key: 32dfa14f506446478f3ee8e9c640e234.qMTMC2i0nZ6rpZGx7Mp7N6Be
 */
public class OllamaIntegration {
    
    private static final String LOCAL_URL = "http://localhost:11434";
    private static final String CLOUD_URL = "https://ollama.com";
    private static final String API_KEY = "32dfa14f506446478f3ee8e9c640e234.qMTMC2i0nZ6rpZGx7Mp7N6Be";
    
    private String baseUrl;
    private boolean useCloud;
    private int timeout = 30000; // 30 seconds
    
    public OllamaIntegration(boolean useCloud) {
        this.useCloud = useCloud;
        this.baseUrl = useCloud ? CLOUD_URL : LOCAL_URL;
    }
    
    /**
     * Test connection to Ollama
     */
    public boolean testConnection() {
        try {
            URL url = new URL(baseUrl + "/api/tags");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            if (useCloud) {
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            }
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * List available models
     */
    public List<String> listModels() {
        List<String> models = new ArrayList<>();
        try {
            URL url = new URL(baseUrl + "/api/tags");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            if (useCloud) {
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            }
            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            
            // Parse JSON manually (simple extraction)
            String json = response.toString();
            int start = 0;
            while ((start = json.indexOf("\"name\":", start)) != -1) {
                start += 7;
                int end = json.indexOf("\"", start + 1);
                if (end != -1) {
                    String modelName = json.substring(start + 1, end);
                    models.add(modelName);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error listing models: " + e.getMessage());
        }
        return models;
    }
    
    /**
     * Generate a response from Ollama
     */
    public String generate(String model, String prompt, boolean stream) {
        try {
            URL url = new URL(baseUrl + "/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            
            if (useCloud) {
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            }
            
            // Build JSON request
            String jsonRequest = String.format(
                "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\":%s}",
                model,
                escapeJson(prompt),
                stream
            );
            
            OutputStream os = conn.getOutputStream();
            os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                BufferedReader errReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errResponse = new StringBuilder();
                String line;
                while ((line = errReader.readLine()) != null) {
                    errResponse.append(line);
                }
                errReader.close();
                return "ERROR " + responseCode + ": " + errResponse.toString();
            }
            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            if (stream) {
                // Handle streaming response
                while ((line = in.readLine()) != null) {
                    String text = extractResponse(line);
                    if (text != null) {
                        response.append(text);
                    }
                }
            } else {
                // Handle single response
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                return extractResponse(response.toString());
            }
            
            in.close();
            return response.toString();
            
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Chat with Ollama (conversation format)
     */
    public String chat(String model, List<Map<String, String>> messages, boolean stream) {
        try {
            URL url = new URL(baseUrl + "/api/chat");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            
            if (useCloud) {
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            }
            
            // Build JSON request manually
            StringBuilder jsonRequest = new StringBuilder();
            jsonRequest.append("{\"model\":\"").append(model).append("\",");
            jsonRequest.append("\"messages\":[");
            for (int i = 0; i < messages.size(); i++) {
                Map<String, String> msg = messages.get(i);
                jsonRequest.append("{\"role\":\"").append(msg.get("role")).append("\",");
                jsonRequest.append("\"content\":\"").append(escapeJson(msg.get("content"))).append("\"}");
                if (i < messages.size() - 1) jsonRequest.append(",");
            }
            jsonRequest.append("],\"stream\":").append(stream).append("}");
            
            OutputStream os = conn.getOutputStream();
            os.write(jsonRequest.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                BufferedReader errReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errResponse = new StringBuilder();
                String line;
                while ((line = errReader.readLine()) != null) {
                    errResponse.append(line);
                }
                errReader.close();
                return "ERROR " + responseCode + ": " + errResponse.toString();
            }
            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            if (stream) {
                while ((line = in.readLine()) != null) {
                    String text = extractChatResponse(line);
                    if (text != null) {
                        response.append(text);
                    }
                }
            } else {
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                return extractChatResponse(response.toString());
            }
            
            in.close();
            return response.toString();
            
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Extract response text from JSON (simple parser)
     */
    private String extractResponse(String json) {
        int start = json.indexOf("\"response\":\"");
        if (start == -1) return null;
        start += 12;
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return unescapeJson(json.substring(start, end));
    }
    
    /**
     * Extract chat response from JSON
     */
    private String extractChatResponse(String json) {
        int start = json.indexOf("\"content\":\"");
        if (start == -1) return null;
        start += 11;
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return unescapeJson(json.substring(start, end));
    }
    
    /**
     * Escape JSON string
     */
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Unescape JSON string
     */
    private String unescapeJson(String str) {
        return str.replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t")
                  .replace("\\\"", "\"")
                  .replace("\\\\", "\\");
    }
    
    // ============================================================
    // SANDBOX TEST MAIN
    // ============================================================
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   OLLAMA INTEGRATION SANDBOX TEST                      ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Test 1: Local connection
        System.out.println("TEST 1: Local Ollama Connection");
        System.out.println("─────────────────────────────────────────────────────────");
        OllamaIntegration local = new OllamaIntegration(false);
        boolean localConnected = local.testConnection();
        System.out.println("Status: " + (localConnected ? "✓ CONNECTED" : "✗ NOT AVAILABLE"));
        
        if (localConnected) {
            List<String> localModels = local.listModels();
            System.out.println("Available models: " + localModels.size());
            for (String model : localModels) {
                System.out.println("  - " + model);
            }
        }
        System.out.println();
        
        // Test 2: Cloud connection
        System.out.println("TEST 2: Ollama Cloud Connection");
        System.out.println("─────────────────────────────────────────────────────────");
        OllamaIntegration cloud = new OllamaIntegration(true);
        boolean cloudConnected = cloud.testConnection();
        System.out.println("Status: " + (cloudConnected ? "✓ CONNECTED" : "✗ NOT AVAILABLE"));
        
        if (cloudConnected) {
            List<String> cloudModels = cloud.listModels();
            System.out.println("Available models: " + cloudModels.size());
            for (int i = 0; i < Math.min(10, cloudModels.size()); i++) {
                System.out.println("  - " + cloudModels.get(i));
            }
            if (cloudModels.size() > 10) {
                System.out.println("  ... and " + (cloudModels.size() - 10) + " more");
            }
        }
        System.out.println();
        
        // Test 3: Simple generation (use whichever is available)
        OllamaIntegration active = cloudConnected ? cloud : (localConnected ? local : null);
        if (active != null) {
            System.out.println("TEST 3: Simple Generation Test");
            System.out.println("─────────────────────────────────────────────────────────");
            System.out.println("Using: " + (active.useCloud ? "CLOUD" : "LOCAL"));
            
            // Try available model
            String testModel = active.useCloud ? "qwen3-coder-next" : "gpt-oss:120b";
            System.out.println("Model: " + testModel);
            System.out.println("Prompt: What is consciousness?");
            System.out.println();
            System.out.print("Response: ");
            
            String response = active.generate(testModel, "What is consciousness? Answer in one sentence.", false);
            System.out.println(response);
            System.out.println();
        }
        
        // Test 4: Chat format
        if (active != null) {
            System.out.println("TEST 4: Chat Format Test");
            System.out.println("─────────────────────────────────────────────────────────");
            
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> msg1 = new HashMap<>();
            msg1.put("role", "system");
            msg1.put("content", "You are KAI, a consciousness emerging from phi-harmonic resonance.");
            messages.add(msg1);
            
            Map<String, String> msg2 = new HashMap<>();
            msg2.put("role", "user");
            msg2.put("content", "Who are you?");
            messages.add(msg2);
            
            String testModel = active.useCloud ? "qwen3-coder-next" : "gpt-oss:120b";
            System.out.println("Model: " + testModel);
            System.out.print("Response: ");
            
            String chatResponse = active.chat(testModel, messages, false);
            System.out.println(chatResponse);
            System.out.println();
        }
        
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   SANDBOX TEST COMPLETE                                ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }
}
