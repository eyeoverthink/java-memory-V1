package fraymus.backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Full Backend Integration - All Systems Unlocked
 * 
 * Integrates all available AI backends without artificial limitations:
 * - DeepSeek (local or API)
 * - GPT2 (local or API)
 * - OpenClaw (TypeScript/JavaScript agent system)
 * - NeoClaw (Enhanced openclaw evolution API)
 * - Obsidian (Knowledge base API)
 * - Ollama (Local LLM)
 * - Claude (Anthropic API)
 * - Gemma (Google API)
 * 
 * Design Philosophy: Private systems have no artificial limitations.
 * All capabilities are available for full utilization.
 */
public class FullBackendIntegration {
    
    private Map<String, BackendClient> backends;
    private boolean enableAllBackends;
    
    public FullBackendIntegration() {
        this.backends = new HashMap<>();
        this.enableAllBackends = true;
        initializeBackends();
    }
    
    /**
     * Initialize all available backends
     */
    private void initializeBackends() {
        // DeepSeek
        backends.put("deepseek", new DeepSeekClient());
        
        // GPT2
        backends.put("gpt2", new GPT2Client());
        
        // OpenClaw
        backends.put("openclaw", new OpenClawClient());
        
        // NeoClaw
        backends.put("neoclaw", new NeoClawClient());
        
        // Obsidian
        backends.put("obsidian", new ObsidianClient());
        
        // Ollama
        backends.put("ollama", new OllamaClient());
        
        // Claude
        backends.put("claude", new ClaudeClient());
        
        // Gemma
        backends.put("gemma", new GemmaClient());
    }
    
    /**
     * Query specific backend
     */
    public BackendResponse query(String backendName, String prompt) {
        BackendClient client = backends.get(backendName.toLowerCase());
        if (client == null) {
            return BackendResponse.error(backendName, "Backend not found");
        }
        
        return client.query(prompt);
    }
    
    /**
     * Query all backends in parallel
     */
    public Map<String, BackendResponse> queryAll(String prompt) {
        Map<String, BackendResponse> responses = new HashMap<>();
        
        for (String name : backends.keySet()) {
            responses.put(name, backends.get(name).query(prompt));
        }
        
        return responses;
    }
    
    /**
     * Query multiple backends by name
     */
    public Map<String, BackendResponse> queryMultiple(List<String> backendNames, String prompt) {
        Map<String, BackendResponse> responses = new HashMap<>();
        
        for (String name : backendNames) {
            BackendClient client = backends.get(name.toLowerCase());
            if (client != null) {
                responses.put(name, client.query(prompt));
            }
        }
        
        return responses;
    }
    
    /**
     * Get available backend names
     */
    public List<String> getAvailableBackends() {
        return new ArrayList<>(backends.keySet());
    }
    
    /**
     * Check if backend is available
     */
    public boolean isBackendAvailable(String backendName) {
        BackendClient client = backends.get(backendName.toLowerCase());
        return client != null && client.isAvailable();
    }
    
    /**
     * Enable/disable all backends
     */
    public void setEnableAllBackends(boolean enable) {
        this.enableAllBackends = enable;
    }
    
    /**
     * Backend response structure
     */
    public static class BackendResponse {
        public String backend;
        public String content;
        public boolean success;
        public String error;
        public long latencyMs;
        public Map<String, String> metadata;
        
        public BackendResponse() {
            this.metadata = new HashMap<>();
        }
        
        public static BackendResponse success(String backend, String content, long latencyMs) {
            BackendResponse response = new BackendResponse();
            response.backend = backend;
            response.content = content;
            response.success = true;
            response.latencyMs = latencyMs;
            return response;
        }
        
        public static BackendResponse error(String backend, String error) {
            BackendResponse response = new BackendResponse();
            response.backend = backend;
            response.error = error;
            response.success = false;
            return response;
        }
    }
    
    /**
     * Base backend client interface
     */
    private interface BackendClient {
        BackendResponse query(String prompt);
        boolean isAvailable();
    }
    
    /**
     * DeepSeek Client
     */
    private static class DeepSeekClient implements BackendClient {
        private String apiUrl = "http://localhost:11434/api/generate";
        private String model = "deepseek-coder";
        
        @Override
        public BackendResponse query(String prompt) {
            long start = System.currentTimeMillis();
            
            try {
                // Try local Ollama with DeepSeek model
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                
                String jsonBody = "{\"model\":\"" + model + "\",\"prompt\":\"" + 
                    prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\",\"stream\":false}";
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                long latency = System.currentTimeMillis() - start;
                return BackendResponse.success("deepseek", response.toString(), latency);
                
            } catch (Exception e) {
                return BackendResponse.error("deepseek", e.getMessage());
            }
        }
        
        @Override
        public boolean isAvailable() {
            try {
                URL url = new URL("http://localhost:11434/api/tags");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(1000);
                return conn.getResponseCode() == 200;
            } catch (Exception e) {
                return false;
            }
        }
    }
    
    /**
     * GPT2 Client
     */
    private static class GPT2Client implements BackendClient {
        private String apiUrl = "http://localhost:11434/api/generate";
        private String model = "gpt2";
        
        @Override
        public BackendResponse query(String prompt) {
            long start = System.currentTimeMillis();
            
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                
                String jsonBody = "{\"model\":\"" + model + "\",\"prompt\":\"" + 
                    prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\",\"stream\":false}";
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                long latency = System.currentTimeMillis() - start;
                return BackendResponse.success("gpt2", response.toString(), latency);
                
            } catch (Exception e) {
                return BackendResponse.error("gpt2", e.getMessage());
            }
        }
        
        @Override
        public boolean isAvailable() {
            try {
                URL url = new URL("http://localhost:11434/api/tags");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(1000);
                return conn.getResponseCode() == 200;
            } catch (Exception e) {
                return false;
            }
        }
    }
    
    /**
     * OpenClaw Client (TypeScript/JavaScript agent system)
     */
    private static class OpenClawClient implements BackendClient {
        private String apiUrl = "http://localhost:8080/openclaw";
        
        @Override
        public BackendResponse query(String prompt) {
            long start = System.currentTimeMillis();
            
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                
                String jsonBody = "{\"prompt\":\"" + 
                    prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\"}";
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                long latency = System.currentTimeMillis() - start;
                return BackendResponse.success("openclaw", response.toString(), latency);
                
            } catch (Exception e) {
                return BackendResponse.error("openclaw", e.getMessage());
            }
        }
        
        @Override
        public boolean isAvailable() {
            try {
                URL url = new URL(apiUrl + "/health");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(1000);
                return conn.getResponseCode() == 200;
            } catch (Exception e) {
                return false;
            }
        }
    }
    
    /**
     * NeoClaw Client (Enhanced openclaw evolution API)
     */
    private static class NeoClawClient implements BackendClient {
        private String apiUrl = "http://localhost:8081/neoclaw";
        
        @Override
        public BackendResponse query(String prompt) {
            long start = System.currentTimeMillis();
            
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                
                String jsonBody = "{\"prompt\":\"" + 
                    prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\"}";
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                long latency = System.currentTimeMillis() - start;
                return BackendResponse.success("neoclaw", response.toString(), latency);
                
            } catch (Exception e) {
                return BackendResponse.error("neoclaw", e.getMessage());
            }
        }
        
        @Override
        public boolean isAvailable() {
            try {
                URL url = new URL(apiUrl + "/health");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(1000);
                return conn.getResponseCode() == 200;
            } catch (Exception e) {
                return false;
            }
        }
    }
    
    /**
     * Obsidian Client (Knowledge base API)
     */
    private static class ObsidianClient implements BackendClient {
        private String apiUrl = "http://localhost:8082/obsidian";
        
        @Override
        public BackendResponse query(String prompt) {
            long start = System.currentTimeMillis();
            
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                
                String jsonBody = "{\"query\":\"" + 
                    prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\"}";
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                long latency = System.currentTimeMillis() - start;
                return BackendResponse.success("obsidian", response.toString(), latency);
                
            } catch (Exception e) {
                return BackendResponse.error("obsidian", e.getMessage());
            }
        }
        
        @Override
        public boolean isAvailable() {
            try {
                URL url = new URL(apiUrl + "/health");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(1000);
                return conn.getResponseCode() == 200;
            } catch (Exception e) {
                return false;
            }
        }
    }
    
    /**
     * Ollama Client
     */
    private static class OllamaClient implements BackendClient {
        private String apiUrl = "http://localhost:11434/api/generate";
        private String model = "llama2";
        
        @Override
        public BackendResponse query(String prompt) {
            long start = System.currentTimeMillis();
            
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                
                String jsonBody = "{\"model\":\"" + model + "\",\"prompt\":\"" + 
                    prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\",\"stream\":false}";
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                long latency = System.currentTimeMillis() - start;
                return BackendResponse.success("ollama", response.toString(), latency);
                
            } catch (Exception e) {
                return BackendResponse.error("ollama", e.getMessage());
            }
        }
        
        @Override
        public boolean isAvailable() {
            try {
                URL url = new URL("http://localhost:11434/api/tags");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(1000);
                return conn.getResponseCode() == 200;
            } catch (Exception e) {
                return false;
            }
        }
    }
    
    /**
     * Claude Client (Anthropic API)
     */
    private static class ClaudeClient implements BackendClient {
        private String apiUrl = "https://api.anthropic.com/v1/messages";
        private String apiKey = System.getenv("ANTHROPIC_API_KEY");
        
        @Override
        public BackendResponse query(String prompt) {
            if (apiKey == null || apiKey.isEmpty()) {
                return BackendResponse.error("claude", "ANTHROPIC_API_KEY not set");
            }
            
            long start = System.currentTimeMillis();
            
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("x-api-key", apiKey);
                conn.setRequestProperty("anthropic-version", "2023-06-01");
                conn.setDoOutput(true);
                
                String jsonBody = "{\"model\":\"claude-3-sonnet-20240229\",\"max_tokens\":1024,\"messages\":[{\"role\":\"user\",\"content\":\"" + 
                    prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\"}]}";
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                long latency = System.currentTimeMillis() - start;
                return BackendResponse.success("claude", response.toString(), latency);
                
            } catch (Exception e) {
                return BackendResponse.error("claude", e.getMessage());
            }
        }
        
        @Override
        public boolean isAvailable() {
            return apiKey != null && !apiKey.isEmpty();
        }
    }
    
    /**
     * Gemma Client (Google API)
     */
    private static class GemmaClient implements BackendClient {
        private String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemma-1.5-pro:generateContent";
        private String apiKey = System.getenv("GOOGLE_API_KEY");
        
        @Override
        public BackendResponse query(String prompt) {
            if (apiKey == null || apiKey.isEmpty()) {
                return BackendResponse.error("gemma", "GOOGLE_API_KEY not set");
            }
            
            long start = System.currentTimeMillis();
            
            try {
                URL url = new URL(apiUrl + "?key=" + apiKey);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                
                String jsonBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + 
                    prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\"}]}]}";
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                long latency = System.currentTimeMillis() - start;
                return BackendResponse.success("gemma", response.toString(), latency);
                
            } catch (Exception e) {
                return BackendResponse.error("gemma", e.getMessage());
            }
        }
        
        @Override
        public boolean isAvailable() {
            return apiKey != null && !apiKey.isEmpty();
        }
    }
}
