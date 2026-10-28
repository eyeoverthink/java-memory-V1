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
 * DeepSeek v3.2 Backend - Mixture-of-Experts reasoning model
 * Real implementation using DeepSeek API
 * 
 * Specifications:
 * - Total Params: 671B
 * - Active Params: 37B (MoE architecture)
 * - Context: 163K tokens
 * - Strengths: Math reasoning (96.0% GSM8K), cost-efficient coding
 */
public class DeepSeekBackend implements BackendInterface {
    private static final String OPEN_MODEL_SYSTEM_MESSAGE =
        "Operate as an evidence-aware open-model reasoning backend. " +
        "Prefer concrete, testable output. Mark assumptions explicitly.";
    private final BackendType backendType = BackendType.DEEPSEEK;
    private final BackendCapabilities capabilities;
    private boolean initialized = false;
    private boolean healthy = true;
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String apiKey;
    private final String apiUrl;
    private final String model;
    
    public DeepSeekBackend() {
        this(System.getenv("DEEPSEEK_API_KEY"), "https://api.deepseek.com/v1/chat/completions", defaultModelFromEnv());
    }
    
    public DeepSeekBackend(String apiKey, String apiUrl, String model) {
        // DeepSeek v3.2 is excellent for math reasoning and coding
        this.capabilities = new BackendCapabilities(
            true,  // supportsReasoning (excellent - 96.0% GSM8K)
            true,  // supportsGeneration (excellent)
            true,  // supportsKnowledge
            true,  // supportsFastInference
            true,  // supportsComplexAnalysis (excellent)
            400.0, // maxLatencyMs
            0.85   // minConfidence (high)
        );
        
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
        
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        
        this.gson = new Gson();
    }

    private static String defaultModelFromEnv() {
        String configured = System.getenv("DEEPSEEK_MODEL");
        if (configured == null || configured.trim().isEmpty()) {
            return "deepseek-v3";
        }
        return configured.trim();
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
            System.out.println("DEEPSEEK_API_KEY not set, using mock mode");
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
                throw new Exception("Invalid DeepSeek API key");
            }
            
            response.close();
            this.initialized = true;
            this.healthy = true;
        } catch (IOException e) {
            throw new Exception("Failed to connect to DeepSeek API: " + e.getMessage());
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
                    Thread.sleep(100); // Simulate latency
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return new QueryResult(
                    "[DeepSeek v3.2 Mock Response] This is a simulated response for: " + query.substring(0, Math.min(50, query.length())) + "...",
                    0.85,
                    100,
                    true,
                    null,
                    Map.of(
                        "backend", backendType.getId(),
                        "taskType", taskType.getId(),
                        "provider", "deepseek",
                        "model", model,
                        "mode", "mock"
                    )
                );
            });
        }
        
        // Build request body (OpenAI-compatible format)
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("max_tokens", 4096);
        
        JsonArray messages = new JsonArray();
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", OPEN_MODEL_SYSTEM_MESSAGE);
        messages.add(systemMessage);
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", query);
        messages.add(message);
        requestBody.add("messages", messages);
        
        // Optional parameters
        if (parameters.containsKey("temperature")) {
            requestBody.addProperty("temperature", (Double) parameters.get("temperature"));
        } else {
            requestBody.addProperty("temperature", defaultTemperature(taskType));
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
                        "DeepSeek API error: " + response.code(),
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
                    0.88, // DeepSeek provides excellent quality
                    latency,
                    true,
                    null,
                    Map.of(
                        "backend", backendType.getId(),
                        "taskType", taskType.getId(),
                        "provider", "deepseek",
                        "model", model,
                        "active_params", "37B",
                        "total_params", "671B",
                        "context", "163K"
                    )
                );
            } catch (IOException e) {
                this.healthy = false;
                return new QueryResult(
                    "",
                    0.0,
                    System.currentTimeMillis() - startTime,
                    false,
                    "DeepSeek API call failed: " + e.getMessage(),
                    Map.of("backend", backendType.getId())
                );
            }
        });
    }

    private double defaultTemperature(TaskType taskType) {
        return switch (taskType) {
            case REASONING, COMPLEX_ANALYSIS -> 0.1;
            case GENERATION -> 0.35;
            case FAST_INFERENCE -> 0.2;
            default -> 0.25;
        };
    }
}
