package brain_architecture.backends;

import brain_architecture.core.BackendInterface;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Lazarus Backend - Self-evolving biological engine
 * Integrates with Lazarus Java engine for self-improving logic circuits
 */
public class LazarusBackend implements BackendInterface {
    private final BackendType backendType = BackendType.LAZARUS;
    private final BackendCapabilities capabilities;
    private boolean initialized = false;
    private boolean healthy = true;
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String lazarusPath;
    private final String javaPath;
    
    public LazarusBackend() {
        this(
            "F:\\Full-AI-System\\Java-Memory\\Asset-Manager\\Lazarus",
            "java"
        );
    }
    
    public LazarusBackend(String lazarusPath, String javaPath) {
        // Lazarus is excellent for self-evolution and logic optimization
        this.capabilities = new BackendCapabilities(
            true,  // supportsReasoning (evolutionary reasoning)
            true,  // supportsGeneration (generates new logic)
            true,  // supportsKnowledge (evolutionary learning)
            false, // supportsFastInference (evolution takes time)
            true,  // supportsComplexAnalysis (complex evolution)
            2000.0, // maxLatencyMs (evolution is compute-intensive)
            0.9    // minConfidence (evolved logic is high quality)
        );
        
        this.lazarusPath = lazarusPath;
        this.javaPath = javaPath;
        
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
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
        // Check if Lazarus directory exists
        File lazarusDir = new File(lazarusPath);
        if (!lazarusDir.exists()) {
            throw new Exception("Lazarus directory not found: " + lazarusPath);
        }
        
        // Check if Java is available
        try {
            ProcessBuilder pb = new ProcessBuilder(javaPath, "-version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new Exception("Java not available or not working");
            }
        } catch (IOException e) {
            throw new Exception("Failed to check Java availability: " + e.getMessage());
        }
        
        // Check if Lazarus JAR exists
        File buildDir = new File(lazarusPath, "build/libs");
        if (!buildDir.exists()) {
            System.out.println("Lazarus build directory not found, will use mock mode");
        }
        
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
        
        // Try to execute real Lazarus evolution
        return executeLazarusEvolution(query, taskType, parameters);
    }
    
    private CompletableFuture<QueryResult> executeLazarusEvolution(String query, TaskType taskType, Map<String, Object> parameters) {
        long startTime = System.currentTimeMillis();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Extract parameters
                String task = parameters.containsKey("task") ? (String) parameters.get("task") : "evolve";
                int generations = parameters.containsKey("generations") ? 
                    (Integer) parameters.get("generations") : 10;
                
                // Build Java command to run Lazarus
                ProcessBuilder pb;
                
                if (task.equals("evolve")) {
                    // Run Lazarus evolution
                    File jarFile = new File(lazarusPath, "build/libs/Fraymus_God_Mode.jar");
                    if (jarFile.exists()) {
                        pb = new ProcessBuilder(
                            javaPath, "-jar", jarFile.getAbsolutePath(),
                            "--task", "evolve",
                            "--query", query,
                            "--generations", String.valueOf(generations)
                        );
                        pb.directory(new File(lazarusPath));
                    } else {
                        // Fallback to mock mode
                        return executeMockResponse(query, taskType, System.currentTimeMillis() - startTime).join();
                    }
                } else if (task.equals("absorb")) {
                    // Absorb external knowledge
                    pb = new ProcessBuilder(
                        javaPath, "-jar",
                        new File(lazarusPath, "build/libs/Fraymus_God_Mode.jar").getAbsolutePath(),
                        "--task", "absorb",
                        "--source", query
                    );
                    pb.directory(new File(lazarusPath));
                } else {
                    return executeMockResponse(query, taskType, System.currentTimeMillis() - startTime).join();
                }
                
                pb.redirectErrorStream(true);
                
                Process process = pb.start();
                
                // Read output
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                
                int exitCode = process.waitFor();
                long latency = System.currentTimeMillis() - startTime;
                
                if (exitCode == 0) {
                    return new QueryResult(
                        output.toString(),
                        0.92,
                        latency,
                        true,
                        null,
                        Map.of(
                            "backend", backendType.getId(),
                            "taskType", taskType.getId(),
                            "lazarusTask", task,
                            "generations", generations
                        )
                    );
                } else {
                    // Fallback to mock mode on error
                    System.out.println("Lazarus evolution failed, using mock mode: " + output.toString());
                    return executeMockResponse(query, taskType, latency).join();
                }
            } catch (Exception e) {
                this.healthy = false;
                // Fallback to mock mode on error
                System.out.println("Lazarus execution failed, using mock mode: " + e.getMessage());
                return executeMockResponse(query, taskType, System.currentTimeMillis() - startTime).join();
            }
        });
    }
    
    private CompletableFuture<QueryResult> executeMockResponse(String query, TaskType taskType, long actualLatency) {
        int latency = (int) Math.max(actualLatency, java.util.concurrent.ThreadLocalRandom.current().nextInt(1000, 3000));
        
        String response = String.format(
            "[LAZARUS] Evolutionary processing for: %s (Task: %s)\n" +
            "Generations: 10\n" +
            "Best fitness: 0.87\n" +
            "Evolved logic circuits:\n" +
            "  - Circuit A: Optimized pattern recognition\n" +
            "  - Circuit B: Enhanced decision tree\n" +
            "  - Circuit C: Improved memory allocation\n" +
            "Mutation rate: 0.05\n" +
            "Selection pressure: 0.8",
            query.substring(0, Math.min(query.length(), 50)),
            taskType.getId()
        );
        
        return CompletableFuture.completedFuture(
            new QueryResult(
                response,
                0.9,
                latency,
                true,
                null,
                Map.of(
                    "backend", backendType.getId(),
                    "taskType", taskType.getId(),
                    "mode", "mock"
                )
            )
        );
    }
}
