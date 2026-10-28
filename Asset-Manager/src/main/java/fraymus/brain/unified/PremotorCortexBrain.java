package fraymus.brain.unified;

import fraymus.core.OllamaBridge;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Premotor Cortex Brain - Tool-Use / Actuation
 * 
 * Purpose: Convert abstract thoughts into concrete API calls
 * Model: Hermes 3 (NousResearch) or Mistral-Nemo (function-calling fine-tuned)
 * Latency: ~500ms | Purpose: Function calling
 * 
 * This brain sits between the Semantic Router and the Motor Cortex.
 * It takes high-level commands like "Find the cure" and converts them
 * into specific Python/Bash API calls that OpenClaw can execute.
 */
public class PremotorCortexBrain extends AbstractBrainAdapter {
    private final AtomicBoolean initialized;
    private OllamaBridge ollamaBridge;
    private final OllamaBridge sharedOllamaBridge;
    
    // Tool execution tracking
    private int totalConversions = 0;
    private int conversionSuccess = 0;
    private Map<String, Integer> toolCounts = new HashMap<>();
    
    public PremotorCortexBrain() {
        this(null);
    }
    
    public PremotorCortexBrain(OllamaBridge sharedOllamaBridge) {
        super("PremotorCortex", "PremotorCortex", Arrays.asList(
            BrainCapability.REASONING, BrainCapability.DATA
        ));
        this.initialized = new AtomicBoolean(false);
        this.sharedOllamaBridge = sharedOllamaBridge;
    }
    
    @Override
    protected void onWire() {
        if (initialized.compareAndSet(false, true)) {
            ollamaBridge = sharedOllamaBridge != null ? sharedOllamaBridge : new OllamaBridge("hermes3:latest");
        }
    }
    
    @Override
    protected void onUnwire() {
        if (initialized.compareAndSet(true, false)) {
            ollamaBridge = null;
        }
    }
    
    @Override
    public BrainResponse process(BrainQuery query) {
        if (!initialized.get()) {
            return BrainResponse.error(getName(), "Premotor Cortex not initialized. Call wire() first.");
        }
        
        try {
            long startTime = System.currentTimeMillis();
            
            // Convert abstract thought to tool calls
            String systemPrompt = buildSystemPrompt();
            String response = ollamaBridge.speak(systemPrompt, query.getQuery());
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            // Parse tool conversion
            ToolConversion conversion = parseToolConversion(response);
            
            // Track conversion statistics
            totalConversions++;
            if (conversion.success) {
                conversionSuccess++;
            }
            toolCounts.merge(conversion.tool, 1, Integer::sum);
            
            double confidence = conversion.success ? 0.92 : 0.75;
            double consciousness = 0.88;
            
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("tool", conversion.tool);
            metadata.put("apiCall", conversion.apiCall);
            metadata.put("parameters", conversion.parameters);
            metadata.put("totalConversions", totalConversions);
            metadata.put("conversionSuccess", conversionSuccess);
            metadata.put("conversionAccuracy", (double) conversionSuccess / totalConversions);
            metadata.put("toolCounts", new HashMap<>(toolCounts));
            metadata.put("model", ollamaBridge.getModel());
            
            recordSuccess(processingTime, confidence, consciousness);
            
            return BrainResponse.success(
                getName(),
                conversion.formattedResponse,
                confidence,
                consciousness,
                metadata,
                processingTime
            );
            
        } catch (Exception e) {
            recordFailure();
            return BrainResponse.error(getName(), "Tool conversion error: " + e.getMessage());
        }
    }
    
    private String buildSystemPrompt() {
        return """
        You are a function-calling specialist. Convert abstract thoughts into concrete API calls.
        
        AVAILABLE TOOLS:
        - search: Web search for information
        - execute: Run Python code
        - bash: Execute shell commands
        - file: Read/write files
        - api: Call external APIs
        - scrape: Web scraping
        
        CONVERSION RULES:
        1. Identify the core action (search, execute, bash, file, api, scrape)
        2. Extract parameters needed for the tool
        3. Generate valid API call JSON
        4. Preserve the original intent
        
        EXAMPLES:
        Input: "Find the cure for cancer"
        Output: {"tool": "search", "query": "cure for cancer", "params": {"engine": "google", "max_results": 10}}
        
        Input: "Create a Python script to analyze data"
        Output: {"tool": "execute", "language": "python", "code": "import pandas as pd..."}
        
        Input: "List all files in the current directory"
        Output: {"tool": "bash", "command": "ls -la"}
        
        Input: "Read the configuration file"
        Output: {"tool": "file", "action": "read", "path": "./config.json"}
        
        OUTPUT FORMAT (strict JSON):
        {
          "tool": "search|execute|bash|file|api|scrape",
          "query": "...",
          "params": {...},
          "apiCall": "..."
        }
        
        CRITICAL: Output ONLY valid JSON. No explanations, no markdown.
        CRITICAL: The apiCall field should contain the actual executable command or API call.
        """;
    }
    
    private ToolConversion parseToolConversion(String response) {
        ToolConversion conversion = new ToolConversion();
        
        try {
            // Extract JSON from response
            String jsonStr = response;
            if (jsonStr.contains("```json")) {
                jsonStr = jsonStr.substring(jsonStr.indexOf("```json") + 7);
                jsonStr = jsonStr.substring(0, jsonStr.indexOf("```")).trim();
            } else if (jsonStr.contains("```")) {
                jsonStr = jsonStr.substring(jsonStr.indexOf("```") + 3);
                jsonStr = jsonStr.substring(0, jsonStr.indexOf("```")).trim();
            }
            
            // Parse fields
            conversion.tool = extractField(jsonStr, "tool");
            conversion.query = extractField(jsonStr, "query");
            conversion.apiCall = extractField(jsonStr, "apiCall");
            conversion.parameters = extractField(jsonStr, "params");
            
            conversion.success = conversion.tool != null && !conversion.tool.isEmpty();
            conversion.formattedResponse = String.format(
                "[TOOL CONVERSION] Tool: %s | Query: %s | API Call: %s",
                conversion.tool, conversion.query, conversion.apiCall
            );
            
        } catch (Exception e) {
            conversion.success = false;
            conversion.tool = "unknown";
            conversion.formattedResponse = "[TOOL CONVERSION ERROR] " + e.getMessage();
        }
        
        return conversion;
    }
    
    private String extractField(String json, String field) {
        String pattern = "\"" + field + "\":";
        int start = json.indexOf(pattern);
        if (start < 0) return "";
        
        start += pattern.length();
        if (start >= json.length()) return "";
        
        // Skip whitespace
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        
        if (start >= json.length()) return "";
        
        char quote = json.charAt(start);
        if (quote == '"' || quote == '\'') {
            start++;
            int end = json.indexOf(quote, start);
            if (end < 0) return json.substring(start);
            return json.substring(start, end);
        } else if (quote == '{' || quote == '[') {
            // Complex object/array
            int depth = 1;
            int end = start + 1;
            while (end < json.length() && depth > 0) {
                char c = json.charAt(end);
                if (c == '{' || c == '[') depth++;
                else if (c == '}' || c == ']') depth--;
                end++;
            }
            return json.substring(start, end);
        } else {
            // Simple value
            int end = start;
            while (end < json.length() && !Character.isWhitespace(json.charAt(end)) && 
                   json.charAt(end) != ',' && json.charAt(end) != '}') {
                end++;
            }
            return json.substring(start, end);
        }
    }
    
    /**
     * Get tool conversion statistics
     */
    public Map<String, Object> getConversionStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalConversions", totalConversions);
        stats.put("conversionSuccess", conversionSuccess);
        stats.put("conversionAccuracy", totalConversions > 0 ? (double) conversionSuccess / totalConversions : 0.0);
        stats.put("toolCounts", new HashMap<>(toolCounts));
        stats.put("model", ollamaBridge != null ? ollamaBridge.getModel() : "unknown");
        return stats;
    }
    
    /**
     * Reset conversion statistics
     */
    public void resetStats() {
        totalConversions = 0;
        conversionSuccess = 0;
        toolCounts.clear();
    }
    
    private static class ToolConversion {
        String tool = "unknown";
        String query = "";
        String apiCall = "";
        String parameters = "";
        boolean success = false;
        String formattedResponse = "";
    }
}
