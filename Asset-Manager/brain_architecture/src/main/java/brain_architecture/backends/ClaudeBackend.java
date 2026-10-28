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
 * Claude Backend - Anthropic's model
 * Real implementation using Anthropic Messages API
 */
public class ClaudeBackend implements BackendInterface {
    private final BackendType backendType = BackendType.CLAUDE;
    private final BackendCapabilities capabilities;
    private boolean initialized = false;
    private boolean healthy = true;
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String apiKey;
    private final String apiUrl;
    private final String model;
    
    public ClaudeBackend() {
        this(System.getenv("ANTHROPIC_API_KEY"), "https://api.anthropic.com/v1/messages", "claude-3-sonnet-20240229");
    }
    
    public ClaudeBackend(String apiKey, String apiUrl, String model) {
        // Claude is sophisticated, excellent for reasoning and synthesis
        this.capabilities = new BackendCapabilities(
            true,  // supportsReasoning (excellent)
            true,  // supportsGeneration (excellent)
            true,  // supportsKnowledge
            true,  // supportsFastInference
            true,  // supportsComplexAnalysis (excellent)
            350.0, // maxLatencyMs
            0.8    // minConfidence (highest)
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
            throw new Exception("ANTHROPIC_API_KEY environment variable not set");
        }
        
        // Test API connection
        try {
            Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .get()
                .build();
            
            Response response = httpClient.newCall(request).execute();
            
            if (response.code() == 401) {
                throw new Exception("Invalid Anthropic API key");
            }
            
            response.close();
            this.initialized = true;
            this.healthy = true;
        } catch (IOException e) {
            throw new Exception("Failed to connect to Anthropic API: " + e.getMessage());
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
        
        // Build request body
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("max_tokens", 1024);
        
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", query);
        
        JsonArray messages = new JsonArray();
        messages.add(message);
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
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
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
                        "Claude API error: " + response.code(),
                        Map.of("backend", backendType.getId())
                    );
                }
                
                String responseBody = response.body().string();
                response.close();
                
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonArray contentArray = jsonResponse.getAsJsonArray("content");
                String content = contentArray.get(0).getAsJsonObject().get("text").getAsString();
                
                return new QueryResult(
                    content,
                    0.9, // Claude provides excellent quality
                    latency,
                    true,
                    null,
                    Map.of(
                        "backend", backendType.getId(),
                        "taskType", taskType.getId(),
                        "provider", "anthropic",
                        "model", model
                    )
                );
            } catch (IOException e) {
                this.healthy = false;
                return new QueryResult(
                    "",
                    0.0,
                    System.currentTimeMillis() - startTime,
                    false,
                    "Claude API call failed: " + e.getMessage(),
                    Map.of("backend", backendType.getId())
                );
            }
        });
    }
}
