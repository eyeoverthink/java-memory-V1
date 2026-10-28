package brain_architecture.backends;

import brain_architecture.core.BackendInterface;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Ollama Backend - Local LLM infrastructure
 * Real implementation using localhost:11434 API
 */
public class OllamaBackend implements BackendInterface {
    private final BackendType backendType = BackendType.OLLAMA;
    private final BackendCapabilities capabilities;
    private boolean initialized = false;
    private boolean healthy = true;
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String ollamaHost;
    private final String defaultModel;
    
    public OllamaBackend() {
        this("http://localhost:11434", "llama2");
    }
    
    public OllamaBackend(String ollamaHost, String defaultModel) {
        // Ollama is local LLM, fast and versatile
        this.capabilities = new BackendCapabilities(
            true,  // supportsReasoning
            true,  // supportsGeneration
            true,  // supportsKnowledge
            true,  // supportsFastInference (local = fast)
            true,  // supportsComplexAnalysis
            200.0, // maxLatencyMs (fastest - local)
            0.7    // minConfidence
        );
        
        this.ollamaHost = ollamaHost;
        this.defaultModel = defaultModel;
        
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
        // Check if Ollama is running
        try {
            Request request = new Request.Builder()
                .url(ollamaHost + "/api/tags")
                .get()
                .build();
            
            Response response = httpClient.newCall(request).execute();
            
            if (response.isSuccessful()) {
                this.initialized = true;
                this.healthy = true;
            } else {
                throw new Exception("Ollama API returned error: " + response.code());
            }
            
            response.close();
        } catch (IOException e) {
            throw new Exception("Failed to connect to Ollama: " + e.getMessage());
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
        
        try {
            Request request = new Request.Builder()
                .url(ollamaHost + "/api/tags")
                .get()
                .build();
            
            Response response = httpClient.newCall(request).execute();
            boolean isHealthy = response.isSuccessful();
            response.close();
            this.healthy = isHealthy;
            return isHealthy;
        } catch (IOException e) {
            this.healthy = false;
            return false;
        }
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
        
        // Get model from parameters or use default
        String model = (String) parameters.getOrDefault("model", defaultModel);
        
        // Build request body
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("prompt", query);
        requestBody.addProperty("stream", false);
        
        // Optional parameters
        if (parameters.containsKey("temperature")) {
            requestBody.addProperty("temperature", (Double) parameters.get("temperature"));
        }
        if (parameters.containsKey("num_predict")) {
            requestBody.addProperty("num_predict", (Integer) parameters.get("num_predict"));
        }
        
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.parse("application/json")
        );
        
        Request request = new Request.Builder()
            .url(ollamaHost + "/api/generate")
            .post(body)
            .build();
        
        long startTime = System.currentTimeMillis();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response response = httpClient.newCall(request).execute();
                long latency = System.currentTimeMillis() - startTime;
                
                if (!response.isSuccessful()) {
                    return new QueryResult(
                        "",
                        0.0,
                        latency,
                        false,
                        "Ollama API error: " + response.code(),
                        Map.of("backend", backendType.getId())
                    );
                }
                
                String responseBody = response.body().string();
                response.close();
                
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                String content = jsonResponse.get("response").getAsString();
                
                return new QueryResult(
                    content,
                    0.8, // Ollama generally provides good quality
                    latency,
                    true,
                    null,
                    Map.of(
                        "backend", backendType.getId(),
                        "taskType", taskType.getId(),
                        "local", true,
                        "model", model
                    )
                );
            } catch (IOException e) {
                return new QueryResult(
                    "",
                    0.0,
                    System.currentTimeMillis() - startTime,
                    false,
                    "Ollama API call failed: " + e.getMessage(),
                    Map.of("backend", backendType.getId())
                );
            }
        });
    }
}
