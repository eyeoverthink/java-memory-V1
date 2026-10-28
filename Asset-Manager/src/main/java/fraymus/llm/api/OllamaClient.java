package fraymus.llm.api;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * 🧬 OLLAMA CLIENT - Absorbed from Ollama Go
 * Source: ollama-main/api/client.go
 * 
 * Pure Java HTTP client for Ollama API.
 * Replaces OllamaBridge with native absorbed implementation.
 * 
 * "The bridge is absorbed. We speak directly."
 */
public class OllamaClient {
    
    private static final String DEFAULT_HOST = "http://localhost:11434";
    
    private final String baseUrl;
    private int timeoutMs = 120_000;
    private boolean connected = false;
    private boolean routeCatalog;
    
    public OllamaClient() {
        this(DEFAULT_HOST);
        this.routeCatalog = true;
    }
    
    public OllamaClient(String host) {
        this.baseUrl = host.endsWith("/") ? host.substring(0, host.length() - 1) : host;
        checkConnection();
    }
    
    private void checkConnection() {
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create(baseUrl + "/api/tags").toURL().openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            connected = conn.getResponseCode() == 200;
            conn.disconnect();
        } catch (Exception e) {
            connected = false;
        }
    }
    
    public boolean isConnected() { return connected; }
    
    // ═══════════════════════════════════════════════════════════════════════
    // GENERATE API
    // ═══════════════════════════════════════════════════════════════════════
    
    public GenerateResponse generate(GenerateRequest request) {
        request.stream = false;
        String response = post("/api/generate", request.toJson(), request.model);
        return GenerateResponse.fromJson(response);
    }
    
    public void generateStream(GenerateRequest request, Consumer<String> onToken, Consumer<GenerateResponse> onComplete) {
        request.stream = true;
        postStream("/api/generate", request.toJson(), line -> {
            GenerateResponse r = GenerateResponse.fromJson(line);
            if (r.response != null && !r.response.isEmpty()) {
                onToken.accept(r.response);
            }
            if (r.done && onComplete != null) {
                onComplete.accept(r);
            }
        }, request.model);
    }
    
    public String generate(String model, String prompt) {
        GenerateRequest req = new GenerateRequest().model(model).prompt(prompt);
        return generate(req).response;
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // CHAT API
    // ═══════════════════════════════════════════════════════════════════════
    
    public String chat(ChatRequest request) {
        request.stream = false;
        String response = post("/api/chat", request.toJson(), request.model);
        // Extract message content from response
        String pattern = "\"content\":\"";
        int start = response.indexOf(pattern);
        if (start < 0) return response;
        start += pattern.length();
        int end = response.indexOf("\"", start);
        while (end > 0 && response.charAt(end - 1) == '\\') {
            end = response.indexOf("\"", end + 1);
        }
        if (end < 0) return response;
        return unescape(response.substring(start, end));
    }
    
    public void chatStream(ChatRequest request, Consumer<String> onToken) {
        request.stream = true;
        postStream("/api/chat", request.toJson(), line -> {
            String pattern = "\"content\":\"";
            int start = line.indexOf(pattern);
            if (start >= 0) {
                start += pattern.length();
                int end = line.indexOf("\"", start);
                while (end > 0 && line.charAt(end - 1) == '\\') {
                    end = line.indexOf("\"", end + 1);
                }
                if (end > start) {
                    onToken.accept(unescape(line.substring(start, end)));
                }
            }
        }, request.model);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // HTTP LAYER
    // ═══════════════════════════════════════════════════════════════════════
    
    private String post(String endpoint, String json, String model) {
        try {
            String host = routeCatalog ? fraymus.llm.LocalModelCatalog.endpointForModel(model, baseUrl) : baseUrl;
            HttpURLConnection conn = (HttpURLConnection) URI.create(host + endpoint).toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(timeoutMs);
            conn.setReadTimeout(timeoutMs);
            conn.setDoOutput(true);
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            
            return response.toString();
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
    
    private void postStream(String endpoint, String json, Consumer<String> onLine, String model) {
        try {
            String host = routeCatalog ? fraymus.llm.LocalModelCatalog.endpointForModel(model, baseUrl) : baseUrl;
            HttpURLConnection conn = (HttpURLConnection) URI.create(host + endpoint).toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(timeoutMs);
            conn.setReadTimeout(timeoutMs);
            conn.setDoOutput(true);
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.isEmpty()) {
                        onLine.accept(line);
                    }
                }
            }
        } catch (Exception e) {
            onLine.accept("{\"error\":\"" + e.getMessage() + "\",\"done\":true}");
        }
    }
    
    private String unescape(String s) {
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
    
    public OllamaClient timeout(int ms) {
        this.timeoutMs = ms;
        return this;
    }
}
