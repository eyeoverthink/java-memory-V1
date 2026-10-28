package fraymus.model;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * ModelAdapter - Backend abstraction for different model runtimes.
 * 
 * This provides a unified interface for interacting with models across
 * different backends (Ollama, GGUF, OpenAI-compatible, etc.).
 */
public interface ModelAdapter {
    
    /**
     * Backend types supported by the adapter system
     */
    enum BackendType {
        OLLAMA("Ollama", "Local model server"),
        GGUF_DIRECT("GGUF", "Direct GGUF file loading"),
        OPENAI_COMPATIBLE("OpenAI", "OpenAI-compatible API"),
        VLLM("vLLM", "vLLM inference server"),
        TENSOR_RT_LLM("TensorRT-LLM", "NVIDIA TensorRT LLM"),
        CUSTOM("Custom", "Custom backend");
        
        private final String name;
        private final String description;
        
        BackendType(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
    }
    
    /**
     * Model inference request
     */
    class InferenceRequest {
        private final String modelId;
        private final String prompt;
        private final Map<String, Object> parameters;
        private final List<ToolCall> tools;
        private final List<Message> history;
        
        public InferenceRequest(String modelId, String prompt, 
                               Map<String, Object> parameters,
                               List<ToolCall> tools, List<Message> history) {
            this.modelId = modelId;
            this.prompt = prompt;
            this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
            this.tools = tools != null ? new ArrayList<>(tools) : new ArrayList<>();
            this.history = history != null ? new ArrayList<>(history) : new ArrayList<>();
        }
        
        public String getModelId() { return modelId; }
        public String getPrompt() { return prompt; }
        public Map<String, Object> getParameters() { return new HashMap<>(parameters); }
        public List<ToolCall> getTools() { return new ArrayList<>(tools); }
        public List<Message> getHistory() { return new ArrayList<>(history); }
        
        public Builder toBuilder() {
            return new Builder(modelId, prompt)
                .parameters(parameters)
                .tools(tools)
                .history(history);
        }
        
        public static class Builder {
            private final String modelId;
            private final String prompt;
            private Map<String, Object> parameters = new HashMap<>();
            private List<ToolCall> tools = new ArrayList<>();
            private List<Message> history = new ArrayList<>();
            
            public Builder(String modelId, String prompt) {
                this.modelId = modelId;
                this.prompt = prompt;
            }
            
            public Builder parameter(String key, Object value) {
                parameters.put(key, value);
                return this;
            }
            
            public Builder parameters(Map<String, Object> params) {
                parameters.putAll(params);
                return this;
            }
            
            public Builder tool(ToolCall tool) {
                tools.add(tool);
                return this;
            }
            
            public Builder tools(List<ToolCall> tools) {
                this.tools.addAll(tools);
                return this;
            }
            
            public Builder message(Message message) {
                history.add(message);
                return this;
            }
            
            public Builder history(List<Message> history) {
                this.history.addAll(history);
                return this;
            }
            
            public InferenceRequest build() {
                return new InferenceRequest(modelId, prompt, parameters, tools, history);
            }
        }
    }
    
    /**
     * Model inference response
     */
    class InferenceResponse {
        private final String modelId;
        private final String content;
        private final List<ToolCall> toolCalls;
        private final Map<String, Object> metadata;
        private final long latencyMs;
        private final boolean success;
        private final String error;
        
        public InferenceResponse(String modelId, String content, 
                                List<ToolCall> toolCalls, 
                                Map<String, Object> metadata,
                                long latencyMs, boolean success, String error) {
            this.modelId = modelId;
            this.content = content;
            this.toolCalls = toolCalls != null ? new ArrayList<>(toolCalls) : new ArrayList<>();
            this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
            this.latencyMs = latencyMs;
            this.success = success;
            this.error = error;
        }
        
        public static InferenceResponse success(String modelId, String content, long latencyMs) {
            return new InferenceResponse(modelId, content, null, null, latencyMs, true, null);
        }
        
        public static InferenceResponse success(String modelId, String content, 
                                              List<ToolCall> toolCalls, long latencyMs) {
            return new InferenceResponse(modelId, content, toolCalls, null, latencyMs, true, null);
        }
        
        public static InferenceResponse error(String modelId, String error) {
            return new InferenceResponse(modelId, null, null, null, 0, false, error);
        }
        
        public String getModelId() { return modelId; }
        public String getContent() { return content; }
        public List<ToolCall> getToolCalls() { return new ArrayList<>(toolCalls); }
        public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
        public long getLatencyMs() { return latencyMs; }
        public boolean isSuccess() { return success; }
        public String getError() { return error; }
    }
    
    /**
     * Message in conversation history
     */
    class Message {
        enum Role { USER, ASSISTANT, SYSTEM, TOOL }
        
        private final Role role;
        private final String content;
        private final Map<String, Object> metadata;
        
        public Message(Role role, String content, Map<String, Object> metadata) {
            this.role = role;
            this.content = content;
            this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
        }
        
        public static Message user(String content) {
            return new Message(Role.USER, content, null);
        }
        
        public static Message assistant(String content) {
            return new Message(Role.ASSISTANT, content, null);
        }
        
        public static Message system(String content) {
            return new Message(Role.SYSTEM, content, null);
        }
        
        public static Message tool(String content) {
            return new Message(Role.TOOL, content, null);
        }
        
        public Role getRole() { return role; }
        public String getContent() { return content; }
        public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
    }
    
    /**
     * Tool/function call
     */
    class ToolCall {
        private final String id;
        private final String name;
        private final Map<String, Object> arguments;
        
        public ToolCall(String id, String name, Map<String, Object> arguments) {
            this.id = id;
            this.name = name;
            this.arguments = arguments != null ? new HashMap<>(arguments) : new HashMap<>();
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public Map<String, Object> getArguments() { return new HashMap<>(arguments); }
    }
    
    /**
     * Execute inference request synchronously
     */
    InferenceResponse infer(InferenceRequest request);
    
    /**
     * Execute inference request asynchronously
     */
    CompletableFuture<InferenceResponse> inferAsync(InferenceRequest request);
    
    /**
     * Get backend type
     */
    BackendType getBackendType();
    
    /**
     * Check if backend is available
     */
    boolean isAvailable();
    
    /**
     * Get backend health/status
     */
    BackendHealth getHealth();
    
    /**
     * Backend health information
     */
    class BackendHealth {
        private final boolean healthy;
        private final double responseTimeMs;
        private final double successRate;
        private final String message;
        private final Map<String, Object> details;
        
        public BackendHealth(boolean healthy, double responseTimeMs, 
                           double successRate, String message,
                           Map<String, Object> details) {
            this.healthy = healthy;
            this.responseTimeMs = responseTimeMs;
            this.successRate = successRate;
            this.message = message;
            this.details = details != null ? new HashMap<>(details) : new HashMap<>();
        }
        
        public boolean isHealthy() { return healthy; }
        public double getResponseTimeMs() { return responseTimeMs; }
        public double getSuccessRate() { return successRate; }
        public String getMessage() { return message; }
        public Map<String, Object> getDetails() { return new HashMap<>(details); }
    }
    
    /**
     * Initialize adapter with configuration
     */
    void initialize(Map<String, Object> config);
    
    /**
     * Shutdown adapter and release resources
     */
    void shutdown();
}
