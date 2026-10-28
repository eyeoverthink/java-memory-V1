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
 * Gemma3 Backend - Google AI Studio (Gemini models)
 * Real implementation using Google AI Studio API
 */
public class Gemma3Backend implements BackendInterface {
    private final BackendType backendType = BackendType.GEMMA3;
    private final BackendCapabilities capabilities;
    private boolean initialized = false;
    private boolean healthy = true;
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String apiKey;
    private final String apiUrl;
    private final String model;
    
    public Gemma3Backend() {
        this(System.getenv("GOOGLE_API_KEY"), "https://generativelanguage.googleapis.com/v1beta/models/gemma-4-26b-a4b-it:generateContent", "gemma-4-26b-a4b-it");
    }
    
    public Gemma3Backend(String apiKey, String apiUrl, String model) {
        // Gemini is Google's LLM, excellent for generation and math
        this.capabilities = new BackendCapabilities(
            true,  // supportsReasoning
            true,  // supportsGeneration (excellent)
            true,  // supportsKnowledge
            true,  // supportsFastInference
            true,  // supportsComplexAnalysis (math modeling)
            250.0, // maxLatencyMs
            0.75   // minConfidence
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
            throw new Exception("GOOGLE_API_KEY environment variable not set");
        }
        
        // Test API connection
        try {
            String testUrl = apiUrl + "?key=" + apiKey;
            Request request = new Request.Builder()
                .url(testUrl)
                .get()
                .build();
            
            Response response = httpClient.newCall(request).execute();
            
            if (response.code() == 401 || response.code() == 403) {
                throw new Exception("Invalid Google API key");
            }
            
            response.close();
            this.initialized = true;
            this.healthy = true;
        } catch (IOException e) {
            throw new Exception("Failed to connect to Google API: " + e.getMessage());
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
        
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", query);
        parts.add(part);
        content.add("parts", parts);
        
        JsonArray contents = new JsonArray();
        contents.add(content);
        requestBody.add("contents", contents);
        
        // Optional parameters
        JsonObject generationConfig = new JsonObject();
        if (parameters.containsKey("temperature")) {
            generationConfig.addProperty("temperature", (Double) parameters.get("temperature"));
        }
        if (parameters.containsKey("max_output_tokens")) {
            generationConfig.addProperty("maxOutputTokens", (Integer) parameters.get("max_output_tokens"));
        }
        if (generationConfig.size() > 0) {
            requestBody.add("generationConfig", generationConfig);
        }
        
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.parse("application/json")
        );
        
        String fullUrl = apiUrl + "?key=" + apiKey;
        Request request = new Request.Builder()
            .url(fullUrl)
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
                        "Gemma3 API error: " + response.code(),
                        Map.of("backend", backendType.getId())
                    );
                }
                
                String responseBody = response.body().string();
                response.close();
                
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                JsonObject candidate = candidates.get(0).getAsJsonObject();
                JsonObject contentResponse = candidate.getAsJsonObject("content");
                JsonArray partsResponse = contentResponse.getAsJsonArray("parts");
                String contentText = partsResponse.get(0).getAsJsonObject().get("text").getAsString();
                
                return new QueryResult(
                    contentText,
                    0.85, // Gemma provides good quality
                    latency,
                    true,
                    null,
                    Map.of(
                        "backend", backendType.getId(),
                        "taskType", taskType.getId(),
                        "provider", "google",
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
                    "Gemma3 API call failed: " + e.getMessage(),
                    Map.of("backend", backendType.getId())
                );
            }
        });
    }
}
