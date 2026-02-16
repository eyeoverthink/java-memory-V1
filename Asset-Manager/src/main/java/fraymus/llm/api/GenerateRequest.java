package fraymus.llm.api;

import java.util.*;

/**
 * 🧬 GENERATE REQUEST - Absorbed from Ollama Go
 * Source: ollama-main/api/types.go
 * 
 * Direct Java equivalent of Ollama's GenerateRequest struct.
 */
public class GenerateRequest {
    
    public String model;
    public String prompt;
    public String suffix;
    public String system;
    public String template;
    public int[] context;
    public Boolean stream;
    public boolean raw;
    public String format;
    public Long keepAlive;
    public List<byte[]> images;
    public Map<String, Object> options;
    public Boolean think;
    public Boolean truncate;
    public Boolean shift;
    public boolean logprobs;
    public int topLogprobs;
    
    public GenerateRequest() {
        this.options = new HashMap<>();
        this.images = new ArrayList<>();
    }
    
    public GenerateRequest model(String model) {
        this.model = model;
        return this;
    }
    
    public GenerateRequest prompt(String prompt) {
        this.prompt = prompt;
        return this;
    }
    
    public GenerateRequest system(String system) {
        this.system = system;
        return this;
    }
    
    public GenerateRequest stream(boolean stream) {
        this.stream = stream;
        return this;
    }
    
    public GenerateRequest option(String key, Object value) {
        this.options.put(key, value);
        return this;
    }
    
    public GenerateRequest temperature(double temp) {
        return option("temperature", temp);
    }
    
    public GenerateRequest topP(double p) {
        return option("top_p", p);
    }
    
    public GenerateRequest topK(int k) {
        return option("top_k", k);
    }
    
    public GenerateRequest numPredict(int n) {
        return option("num_predict", n);
    }
    
    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"model\":\"").append(escape(model)).append("\"");
        if (prompt != null) sb.append(",\"prompt\":\"").append(escape(prompt)).append("\"");
        if (system != null) sb.append(",\"system\":\"").append(escape(system)).append("\"");
        if (stream != null) sb.append(",\"stream\":").append(stream);
        if (!options.isEmpty()) {
            sb.append(",\"options\":{");
            boolean first = true;
            for (Map.Entry<String, Object> e : options.entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(e.getKey()).append("\":");
                if (e.getValue() instanceof String) {
                    sb.append("\"").append(escape(e.getValue().toString())).append("\"");
                } else {
                    sb.append(e.getValue());
                }
                first = false;
            }
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }
    
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
