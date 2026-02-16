package fraymus.llm.api;

import java.util.*;

/**
 * 🧬 CHAT REQUEST - Absorbed from Ollama Go
 * Source: ollama-main/api/types.go
 * 
 * Direct Java equivalent of Ollama's ChatRequest struct.
 */
public class ChatRequest {
    
    public String model;
    public List<Message> messages;
    public String format;
    public Boolean stream;
    public Long keepAlive;
    public List<Tool> tools;
    public Map<String, Object> options;
    public Boolean think;
    public Boolean truncate;
    public Boolean shift;
    
    public ChatRequest() {
        this.messages = new ArrayList<>();
        this.options = new HashMap<>();
        this.tools = new ArrayList<>();
    }
    
    public ChatRequest model(String model) {
        this.model = model;
        return this;
    }
    
    public ChatRequest addMessage(String role, String content) {
        messages.add(new Message(role, content));
        return this;
    }
    
    public ChatRequest system(String content) {
        return addMessage("system", content);
    }
    
    public ChatRequest user(String content) {
        return addMessage("user", content);
    }
    
    public ChatRequest assistant(String content) {
        return addMessage("assistant", content);
    }
    
    public ChatRequest stream(boolean stream) {
        this.stream = stream;
        return this;
    }
    
    public ChatRequest option(String key, Object value) {
        this.options.put(key, value);
        return this;
    }
    
    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"model\":\"").append(escape(model)).append("\"");
        
        sb.append(",\"messages\":[");
        for (int i = 0; i < messages.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(messages.get(i).toJson());
        }
        sb.append("]");
        
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
    
    public static class Message {
        public String role;
        public String content;
        public List<byte[]> images;
        public List<ToolCall> toolCalls;
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        public String toJson() {
            return String.format("{\"role\":\"%s\",\"content\":\"%s\"}", 
                role, escape(content));
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
    
    public static class Tool {
        public String type;
        public Function function;
        
        public static class Function {
            public String name;
            public String description;
            public Map<String, Object> parameters;
        }
    }
    
    public static class ToolCall {
        public String id;
        public String type;
        public Function function;
        
        public static class Function {
            public String name;
            public Map<String, Object> arguments;
        }
    }
}
