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
 * NeoClaw Backend - Enhanced openclaw evolution
 * Infrastructure ready for API integration - currently uses mock responses
 */
public class NeoClawBackend implements BackendInterface {
    private final BackendType backendType = BackendType.NEOCLAW;
    private final BackendCapabilities capabilities;
    private boolean initialized = false;
    private boolean healthy = true;
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String apiUrl;
    private final String apiKey;
    
    public NeoClawBackend() {
        this(System.getenv("NEOCLAW_API_KEY"), "http://localhost:3001/api/evolution");
    }
    
    public NeoClawBackend(String apiKey, String apiUrl) {
        // NeoClaw is enhanced evolution, good for adaptive learning
        this.capabilities = new BackendCapabilities(
            true,  // supportsReasoning
            true,  // supportsGeneration
            true,  // supportsKnowledge
            true,  // supportsFastInference
            true,  // supportsComplexAnalysis (enhanced)
            400.0, // maxLatencyMs
            0.75   // minConfidence
        );
        
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        
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
        // Check if NeoClaw API is available
        if (apiUrl != null && !apiUrl.isEmpty()) {
            try {
                Request request = new Request.Builder()
                    .url(apiUrl + "/health")
                    .get()
                    .build();
                
                Response response = httpClient.newCall(request).execute();
                
                if (response.isSuccessful()) {
                    this.initialized = true;
                    this.healthy = true;
                    response.close();
                    return;
                }
                
                response.close();
            } catch (IOException e) {
                // API not available, fall back to mock mode
                System.out.println("NeoClaw API not available, using mock mode");
            }
        }
        
        // Initialize in mock mode
        this.initialized = true;
        this.healthy = true;
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
        
        // If API is configured, try real API call
        if (apiUrl != null && !apiUrl.isEmpty() && apiKey != null) {
            return executeRealApiCall(query, taskType, parameters);
        }
        
        // Otherwise use mock response
        return executeMockResponse(query, taskType);
    }
    
    private CompletableFuture<QueryResult> executeRealApiCall(String query, TaskType taskType, Map<String, Object> parameters) {
        // Build request body
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("query", query);
        requestBody.addProperty("taskType", taskType.getId());
        
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.parse("application/json")
        );
        
        Request request = new Request.Builder()
            .url(apiUrl + "/evolve")
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
                        "NeoClaw API error: " + response.code(),
                        Map.of("backend", backendType.getId())
                    );
                }
                
                String responseBody = response.body().string();
                response.close();
                
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                String content = jsonResponse.get("response").getAsString();
                
                return new QueryResult(
                    content,
                    0.8,
                    latency,
                    true,
                    null,
                    Map.of(
                        "backend", backendType.getId(),
                        "taskType", taskType.getId(),
                        "evolution", "enhanced"
                    )
                );
            } catch (IOException e) {
                this.healthy = false;
                // Fall back to mock on error
                return executeMockResponse(query, taskType).join();
            }
        });
    }
    
    private CompletableFuture<QueryResult> executeMockResponse(String query, TaskType taskType) {
        int latency = java.util.concurrent.ThreadLocalRandom.current().nextInt(40, 180);
        
        try {
            Thread.sleep(latency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String response = String.format(
            "[NEOCLAW] Enhanced evolution response to: %s (Task: %s)",
            query.substring(0, Math.min(query.length(), 50)),
            taskType.getId()
        );
        
        return CompletableFuture.completedFuture(
            new QueryResult(
                response,
                0.75 + java.util.concurrent.ThreadLocalRandom.current().nextDouble() * 0.25,
                latency,
                true,
                null,
                Map.of(
                    "backend", backendType.getId(),
                    "taskType", taskType.getId(),
                    "evolution", "enhanced",
                    "mode", "mock"
                )
            )
        );
    }
}
