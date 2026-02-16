package fraymus.llm.api;

import java.util.*;

/**
 * 🧬 GENERATE RESPONSE - Absorbed from Ollama Go
 * Source: ollama-main/api/types.go
 * 
 * Direct Java equivalent of Ollama's GenerateResponse struct.
 */
public class GenerateResponse {
    
    public String model;
    public String createdAt;
    public String response;
    public boolean done;
    public String doneReason;
    public int[] context;
    
    // Metrics
    public long totalDuration;
    public long loadDuration;
    public int promptEvalCount;
    public long promptEvalDuration;
    public int evalCount;
    public long evalDuration;
    
    public double getTokensPerSecond() {
        if (evalDuration == 0) return 0;
        return evalCount * 1_000_000_000.0 / evalDuration;
    }
    
    public double getPromptTokensPerSecond() {
        if (promptEvalDuration == 0) return 0;
        return promptEvalCount * 1_000_000_000.0 / promptEvalDuration;
    }
    
    public static GenerateResponse fromJson(String json) {
        GenerateResponse r = new GenerateResponse();
        
        r.model = extractString(json, "model");
        r.createdAt = extractString(json, "created_at");
        r.response = extractString(json, "response");
        r.done = extractBool(json, "done");
        r.doneReason = extractString(json, "done_reason");
        r.totalDuration = extractLong(json, "total_duration");
        r.loadDuration = extractLong(json, "load_duration");
        r.promptEvalCount = extractInt(json, "prompt_eval_count");
        r.promptEvalDuration = extractLong(json, "prompt_eval_duration");
        r.evalCount = extractInt(json, "eval_count");
        r.evalDuration = extractLong(json, "eval_duration");
        
        return r;
    }
    
    private static String extractString(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start < 0) return null;
        start += pattern.length();
        int end = json.indexOf("\"", start);
        if (end < 0) return null;
        return unescape(json.substring(start, end));
    }
    
    private static boolean extractBool(String json, String key) {
        return json.contains("\"" + key + "\":true");
    }
    
    private static int extractInt(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start < 0) return 0;
        start += pattern.length();
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) end++;
        if (end == start) return 0;
        return Integer.parseInt(json.substring(start, end));
    }
    
    private static long extractLong(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start < 0) return 0;
        start += pattern.length();
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) end++;
        if (end == start) return 0;
        return Long.parseLong(json.substring(start, end));
    }
    
    private static String unescape(String s) {
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
