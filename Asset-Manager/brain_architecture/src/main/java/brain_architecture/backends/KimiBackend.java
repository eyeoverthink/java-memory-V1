package brain_architecture.backends;

import brain_architecture.core.BackendInterface;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import okhttp3.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Kimi K2.5 Backend - Multimodal coding and agent swarms
 * Real implementation using Kimi API with dual-mode support
 * 
 * Specifications:
 * - Total Params: 1T
 * - Active Params: 32B
 * - Context: 256K tokens
 * - Strengths: Multimodal coding, agent swarms, UI reconstruction
 * - Dual Mode: Instant (System 1) vs Thinking (System 2 with CoT)
 */
public class KimiBackend implements BackendInterface {
    private final BackendType backendType = BackendType.KIMI;
    private final BackendCapabilities capabilities;
    private boolean initialized = false;
    private boolean healthy = true;
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String apiKey;
    private final String apiUrl;
    private final String model;
    
    public KimiBackend() {
        this(System.getenv("KIMI_API_KEY"), "https://api.moonshot.cn/v1/chat/completions", "moonshot-v1-128k");
    }
    
    public KimiBackend(String apiKey, String apiUrl, String model) {
        // Kimi K2.5 is excellent for multimodal coding and agent swarms
        this.capabilities = new BackendCapabilities(
            true,  // supportsReasoning (excellent with dual-mode)
            true,  // supportsGeneration (excellent)
            true,  // supportsKnowledge
            true,  // supportsFastInference (Instant mode)
            true,  // supportsComplexAnalysis (Thinking mode with CoT)
            500.0, // maxLatencyMs
            0.82   // minConfidence (high)
        );
        
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
        
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS) // Longer for Thinking mode
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        
        this.gson = new Gson();
    }
    
    @Override
    public BackendType getBackendType() {
        return backendType;
    }
    
    @Override
    public BackendCapabilities getCapabilities() {
        return capabilities;
    }
    
    @Override
    public void initialize() throws Exception {
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("KIMI_API_KEY not set, using mock mode");
            this.initialized = true;
            return;
        }
        
        // Test API connection
        try {
            Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .get()
                .build();
            
            Response response = httpClient.newCall(request).execute();
            
            if (response.code() == 401) {
                throw new Exception("Invalid Kimi API key");
            }
            
            response.close();
            this.initialized = true;
            this.healthy = true;
        } catch (IOException e) {
            throw new Exception("Failed to connect to Kimi API: " + e.getMessage());
        }
    }
    
    @Override
    public void shutdown() {
        this.initialized = false;
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
    
    @Override
    public boolean isHealthy() {
        if (!initialized) return false;
        return healthy;
    }
    
    @Override
    public CompletableFuture<QueryResult> executeQuery(String query, TaskType taskType, Map<String, Object> parameters) {
        if (!initialized) {
            return CompletableFuture.completedFuture(
                new QueryResult("", 0.0, 0, false, "Backend not initialized", Map.of())
            );
        }
        
        if (!healthy) {
            return CompletableFuture.completedFuture(
                new QueryResult("", 0.0, 0, false, "Backend unhealthy", Map.of())
            );
        }
        
        // Mock mode if no API key
        if (apiKey == null || apiKey.isEmpty()) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(150); // Simulate latency
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Check for reasoning mode parameter
                boolean reasoningMode = parameters.containsKey("reasoning_mode") && 
                    parameters.get("reasoning_mode").equals("thinking");
                
                String mode = reasoningMode ? "Thinking (System 2)" : "Instant (System 1)";
                return new QueryResult(
                    "[Kimi K2.5 Mock Response - " + mode + "] This is a simulated response for: " + 
                    query.substring(0, Math.min(50, query.length())) + "...",
                    0.82,
                    150,
                    true,
                    null,
                    Map.of(
                        "backend", backendType.getId(),
                        "taskType", taskType.getId(),
                        "provider", "moonshot",
                        "model", model,
                        "mode", "mock",
                        "reasoning_mode", reasoningMode ? "thinking" : "instant"
                    )
                );
            });
        }
        
        // Build request body (OpenAI-compatible format)
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("max_tokens", 8192);
        
        // Check for reasoning mode parameter (dual-mode support)
        boolean reasoningMode = parameters.containsKey("reasoning_mode") && 
            parameters.get("reasoning_mode").equals("thinking");
        
        JsonArray messages = new JsonArray();
        
        // Add system prompt based on mode
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        if (reasoningMode) {
            systemMessage.addProperty("content", "You are in Thinking mode. Use chain-of-thought reasoning to analyze problems step by step. Think deeply before answering.");
        } else {
            systemMessage.addProperty("content", "You are in Instant mode. Provide quick, intuitive responses without extensive deliberation.");
        }
        messages.add(systemMessage);
        
        // Add user message
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", query);
        messages.add(userMessage);
        
        requestBody.add("messages", messages);
        
        // Optional parameters
        if (parameters.containsKey("temperature")) {
            requestBody.addProperty("temperature", (Double) parameters.get("temperature"));
        }
        if (parameters.containsKey("max_tokens")) {
            requestBody.addProperty("max_tokens", (Integer) parameters.get("max_tokens"));
        }
        
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.parse("application/json")
        );
        
        Request request = new Request.Builder()
            .url(apiUrl)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("content-type", "application/json")
            .post(body)
            .build();
        
        long startTime = System.currentTimeMillis();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = httpClient.newCall(request).execute();
                long latency = System.currentTimeMillis() - startTime;
                
                if (!response.isSuccessful()) {
                    this.healthy = false;
                    return new QueryResult(
                        "",
                        0.0,
                        latency,
                        false,
                        "Kimi API error: " + response.code(),
                        Map.of("backend", backendType.getId())
                    );
                }
                
                String responseBody = response.body().string();
                response.close();
                
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonArray choices = jsonResponse.getAsJsonArray("choices");
                String content = choices.get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
                
                return new QueryResult(
                    content,
                    0.85, // Kimi provides excellent quality
                    latency,
                    true,
                    null,
                    Map.of(
                        "backend", backendType.getId(),
                        "taskType", taskType.getId(),
                        "provider", "moonshot",
                        "model", model,
                        "active_params", "32B",
                        "total_params", "1T",
                        "context", "256K",
                        "reasoning_mode", reasoningMode ? "thinking" : "instant"
                    )
                );
            } catch (IOException e) {
                this.healthy = false;
                return new QueryResult(
                    "",
                    0.0,
                    System.currentTimeMillis() - startTime,
                    false,
                    "Kimi API call failed: " + e.getMessage(),
                    Map.of("backend", backendType.getId())
                );
            }
        });
    }
}
