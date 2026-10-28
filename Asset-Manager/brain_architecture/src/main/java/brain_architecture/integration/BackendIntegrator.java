package brain_architecture.integration;

import brain_architecture.core.BackendInterface.BackendType;
import java.util.*;
import java.util.concurrent.*;
import java.lang.reflect.*;

/**
 * BackendIntegrator - Integrates with existing Fraymus backends instead of creating duplicates
 * 
 * This integrator connects brain_architecture to the existing Fraymus backend infrastructure:
 * - Ollama → Fraymus Layer 0 (OllamaSpine)
 * - Claude → Fraymus Layer 12 (NeuralTentacle)
 * - Gemma3 → Fraymus Layer 12 (NeuralTentacle)
 * - OpenClaw → Fraymus Layer 0 (ClawConnector)
 * - NeoClaw → Fraymus Layer 0 (ClawConnector)
 * - Obsidian → Fraymus Layer 0 (VectorVault)
 * 
 * This avoids duplicate backend implementations and leverages the existing infrastructure.
 */
public class BackendIntegrator {
    
    private final Map<BackendType, Object> fraymusBackends = new ConcurrentHashMap<>();
    private final Map<BackendType, Boolean> wiredStatus = new ConcurrentHashMap<>();
    
    // Fraymus backend components (to be injected)
    private Object ollamaSpine;       // Layer 0 - Local LLM connection
    private Object neuralTentacle;    // Layer 12 - HTTP client for Claude/OpenAI
    private Object clawConnector;    // Layer 0 - OpenClaw/NeoClaw connector
    private Object vectorVault;      // Layer 0 - Vector embedding storage
    
    private boolean initialized = false;
    
    /**
     * Wire Fraymus backends to the integrator
     */
    public void wireFraymusBackends(
            Object ollamaSpine,
            Object neuralTentacle,
            Object clawConnector,
            Object vectorVault) {
        
        this.ollamaSpine = ollamaSpine;
        this.neuralTentacle = neuralTentacle;
        this.clawConnector = clawConnector;
        this.vectorVault = vectorVault;
        
        this.initialized = true;
        
        // Map backend types to Fraymus backends
        mapFraymusBackends();
    }
    
    /**
     * Unwire Fraymus backends (revert to standalone mode)
     */
    public void unwireFraymusBackends() {
        this.ollamaSpine = null;
        this.neuralTentacle = null;
        this.clawConnector = null;
        this.vectorVault = null;
        
        fraymusBackends.clear();
        wiredStatus.clear();
        
        this.initialized = false;
    }
    
    /**
     * Map backend types to Fraymus backends
     */
    private void mapFraymusBackends() {
        if (!initialized) return;
        
        // Ollama → Fraymus Layer 0 (OllamaSpine)
        fraymusBackends.put(BackendType.OLLAMA, ollamaSpine);
        
        // Claude → Fraymus Layer 12 (NeuralTentacle)
        fraymusBackends.put(BackendType.CLAUDE, neuralTentacle);
        
        // Gemma3 → Fraymus Layer 12 (NeuralTentacle)
        fraymusBackends.put(BackendType.GEMMA3, neuralTentacle);
        
        // OpenClaw → Fraymus Layer 0 (ClawConnector)
        fraymusBackends.put(BackendType.OPENCLAW, clawConnector);
        
        // NeoClaw → Fraymus Layer 0 (ClawConnector)
        fraymusBackends.put(BackendType.NEOCLAW, clawConnector);
        
        // Obsidian → Fraymus Layer 0 (VectorVault)
        fraymusBackends.put(BackendType.OBSIDIAN, vectorVault);
        
        // Mark wired status
        for (BackendType type : BackendType.values()) {
            wiredStatus.put(type, fraymusBackends.containsKey(type) && fraymusBackends.get(type) != null);
        }
    }
    
    /**
     * Get the Fraymus backend for a given backend type
     */
    public Optional<Object> getFraymusBackend(BackendType backendType) {
        return Optional.ofNullable(fraymusBackends.get(backendType));
    }
    
    /**
     * Check if a backend type is wired to Fraymus
     */
    public boolean isWiredToFraymus(BackendType backendType) {
        return wiredStatus.getOrDefault(backendType, false);
    }
    
    /**
     * Get all wired backend types
     */
    public Set<BackendType> getWiredBackendTypes() {
        Set<BackendType> wired = new HashSet<>();
        for (Map.Entry<BackendType, Boolean> entry : wiredStatus.entrySet()) {
            if (entry.getValue()) {
                wired.add(entry.getKey());
            }
        }
        return wired;
    }
    
    /**
     * Get initialization status
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Get status report
     */
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("initialized", initialized);
        status.put("fraymusBackendsCount", fraymusBackends.size());
        status.put("wiredBackendTypes", getWiredBackendTypes());
        status.put("wiredStatus", new HashMap<>(wiredStatus));
        return status;
    }
    
    /**
     * Execute query using Fraymus backend
     * Uses reflection to call appropriate methods on Fraymus components
     * Implements graceful degradation with fallback to error messages
     */
    public CompletableFuture<String> executeQuery(BackendType backendType, String query, Map<String, Object> options) {
        if (!isWiredToFraymus(backendType)) {
            return CompletableFuture.failedFuture(new IllegalStateException("Backend " + backendType + " is not wired to Fraymus"));
        }
        
        Object backend = fraymusBackends.get(backendType);
        if (backend == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("Fraymus backend is null for " + backendType));
        }
        
        try {
            switch (backendType) {
                case OLLAMA:
                    // OllamaSpine.think(String prompt)
                    return callOllamaSpine(backend, query);
                    
                case CLAUDE:
                case GEMMA3:
                    // NeuralTentacle.think(String prompt)
                    return callNeuralTentacle(backend, query);
                    
                case OPENCLAW:
                case NEOCLAW:
                    // ClawConnector.dispatch(String task, String context)
                    String context = options != null ? options.getOrDefault("context", "").toString() : "";
                    return callClawConnector(backend, query, context);
                    
                case OBSIDIAN:
                    // VectorVault.topK(double[] queryVec, int k)
                    // For Obsidian, we need to convert query to vector first
                    // This is a placeholder - actual implementation would use Ollama embeddings
                    return CompletableFuture.completedFuture("Obsidian backend requires vector embedding conversion (not yet implemented)");
                    
                default:
                    return CompletableFuture.failedFuture(new UnsupportedOperationException("Unsupported backend type: " + backendType));
            }
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException("Failed to execute query via Fraymus backend: " + backendType, e));
        }
    }
    
    /**
     * Call OllamaSpine.think(String prompt)
     */
    private CompletableFuture<String> callOllamaSpine(Object ollamaSpine, String query) {
        try {
            Method thinkMethod = ollamaSpine.getClass().getMethod("think", String.class);
            String result = (String) thinkMethod.invoke(ollamaSpine, query);
            return CompletableFuture.completedFuture(result);
        } catch (NoSuchMethodException e) {
            return CompletableFuture.failedFuture(new RuntimeException("OllamaSpine.think() method not found", e));
        } catch (IllegalAccessException e) {
            return CompletableFuture.failedFuture(new RuntimeException("Cannot access OllamaSpine.think()", e));
        } catch (InvocationTargetException e) {
            return CompletableFuture.failedFuture(new RuntimeException("OllamaSpine.think() threw exception", e.getCause()));
        }
    }
    
    /**
     * Call NeuralTentacle.think(String prompt)
     */
    private CompletableFuture<String> callNeuralTentacle(Object neuralTentacle, String query) {
        try {
            Method thinkMethod = neuralTentacle.getClass().getMethod("think", String.class);
            String result = (String) thinkMethod.invoke(neuralTentacle, query);
            return CompletableFuture.completedFuture(result);
        } catch (NoSuchMethodException e) {
            return CompletableFuture.failedFuture(new RuntimeException("NeuralTentacle.think() method not found", e));
        } catch (IllegalAccessException e) {
            return CompletableFuture.failedFuture(new RuntimeException("Cannot access NeuralTentacle.think()", e));
        } catch (InvocationTargetException e) {
            return CompletableFuture.failedFuture(new RuntimeException("NeuralTentacle.think() threw exception", e.getCause()));
        }
    }
    
    /**
     * Call ClawConnector.dispatch(String task, String context)
     */
    private CompletableFuture<String> callClawConnector(Object clawConnector, String task, String context) {
        try {
            Method dispatchMethod = clawConnector.getClass().getMethod("dispatch", String.class, String.class);
            String result = (String) dispatchMethod.invoke(clawConnector, task, context);
            return CompletableFuture.completedFuture(result);
        } catch (NoSuchMethodException e) {
            // Try single-parameter version
            try {
                Method dispatchSimple = clawConnector.getClass().getMethod("dispatch", String.class);
                String result = (String) dispatchSimple.invoke(clawConnector, task);
                return CompletableFuture.completedFuture(result);
            } catch (Exception e2) {
                return CompletableFuture.failedFuture(new RuntimeException("ClawConnector.dispatch() method not found", e2));
            }
        } catch (IllegalAccessException e) {
            return CompletableFuture.failedFuture(new RuntimeException("Cannot access ClawConnector.dispatch()", e));
        } catch (InvocationTargetException e) {
            return CompletableFuture.failedFuture(new RuntimeException("ClawConnector.dispatch() threw exception", e.getCause()));
        }
    }
}
