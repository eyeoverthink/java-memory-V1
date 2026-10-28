package fraymus.core;

import fraymus.llm.ModelAbstractionLayer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OLLAMA BRIDGE: THE VOICE BOX
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "The LLM is not the brain. It is the mouth."
 * 
 * Mechanism:
 * - Fraymus provides the THOUGHT (from Logic Crystal)
 * - Ollama provides the SPEECH (natural language articulation)
 * - The LLM is a puppet. Fraymus is the puppeteer.
 * 
 * The key insight: We don't ask the LLM to think.
 * We tell it what to say. It just makes it sound human.
 */
public class OllamaBridge {

    private static final String DEFAULT_BASE_URL = System.getProperty("fraynix.ollamaBaseUrl", "http://127.0.0.1:11434");
    private static final String OLLAMA_TAGS_URL = DEFAULT_BASE_URL + "/api/tags";
    private static final int TIMEOUT_MS = 120000;  // 2 minutes
    private static final Pattern MODEL_NAME_PATTERN = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
    // Captures "name":"X" ... "size":N within the same model object (size follows name, no '}' between).
    private static final Pattern MODEL_SIZE_PATTERN =
        Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"[^}]*?\"size\"\\s*:\\s*(\\d+)");
    // VRAM headroom factor: model weights plus KV-cache/runtime overhead.
    private static final double VRAM_OVERHEAD = 1.2;
    
    private String model;
    private boolean connected = false;
    
    // Statistics
    private long totalCalls = 0;
    private long successfulCalls = 0;
    private long failedCalls = 0;
    private long totalTokens = 0;

    public OllamaBridge(String modelName) {
        this.model = resolveAvailableModel(modelName);
        System.out.println("   🎤 VOICE BOX INITIALIZING...");
        System.out.println("      Model: " + model);
        System.out.println("      Endpoint: " + generationUrl());
        
        // Test connection
        testConnection();

        // Report whether the chosen model fits this host's GPU VRAM.
        reportVramFit();
    }

    /**
     * Logs whether the active model fits the detected GPU's free/total VRAM, so
     * the system knows up front whether Ollama can fully offload to the GPU or
     * will spill layers to CPU/RAM (much slower). Detection-only; never changes
     * the model the caller asked for.
     */
    private void reportVramFit() {
        try {
            GpuProfile gpu = GpuProfile.getInstance();
            if (!gpu.hasCudaGpu()) {
                System.out.println("      (no CUDA GPU detected - model runs on CPU/RAM)");
                return;
            }
            long sizeBytes = fetchModelSizes().getOrDefault(model, -1L);
            if (sizeBytes <= 0) {
                return; // size unknown; stay quiet rather than guess
            }
            long needed = (long) (sizeBytes * VRAM_OVERHEAD);
            long free = gpu.freeVramBytes();
            long total = gpu.totalVramBytes();
            double modelGb = sizeBytes / 1.0e9;
            if (needed <= free) {
                System.out.printf("      GPU: ~%.1f GB model fits free VRAM (%.1f GB free) - full GPU offload%n",
                    modelGb, free / 1.0e9);
            } else if (needed <= total) {
                System.out.printf("      GPU: ~%.1f GB model exceeds free VRAM (%.1f GB free / %.1f GB total) - partial offload%n",
                    modelGb, free / 1.0e9, total / 1.0e9);
            } else {
                System.out.printf("      GPU: ~%.1f GB model exceeds total VRAM (%.1f GB) - CPU/RAM spill (slower)%n",
                    modelGb, total / 1.0e9);
            }
        } catch (Exception ignored) {
            // Reporting is best-effort only.
        }
    }

    /**
     * Opt-in VRAM-aware picker. Among locally available models in the same
     * family as {@code preferredModel}, returns the LARGEST whose weights plus
     * overhead fit the GPU's free VRAM (best quality that stays fully on GPU).
     * Falls back to the standard {@link #resolveAvailableModel(String)} result
     * when no size data is available or nothing fits.
     */
    public static String resolveVramFittingModel(String preferredModel) {
        String resolved = resolveAvailableModel(preferredModel);
        try {
            GpuProfile gpu = GpuProfile.getInstance();
            if (!gpu.hasCudaGpu()) {
                return resolved;
            }
            long budget = gpu.freeVramBytes();
            if (budget <= 0) {
                return resolved;
            }
            Map<String, Long> sizes = fetchModelSizes();
            String family = normalizeModelName(preferredModel);
            String best = null;
            long bestSize = -1L;
            for (Map.Entry<String, Long> e : sizes.entrySet()) {
                if (!normalizeModelName(e.getKey()).contains(family)) {
                    continue;
                }
                long needed = (long) (e.getValue() * VRAM_OVERHEAD);
                if (needed <= budget && e.getValue() > bestSize) {
                    best = e.getKey();
                    bestSize = e.getValue();
                }
            }
            return best != null ? best : resolved;
        } catch (Exception ignored) {
            return resolved;
        }
    }

    /** Returns each locally available model mapped to its on-disk size in bytes. */
    private static Map<String, Long> fetchModelSizes() {
        Map<String, Long> sizes = new LinkedHashMap<>();
        try {
            URL url = toUrl(OLLAMA_TAGS_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if (conn.getResponseCode() != 200) {
                conn.disconnect();
                return sizes;
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                Matcher matcher = MODEL_SIZE_PATTERN.matcher(json.toString());
                while (matcher.find()) {
                    try {
                        sizes.put(matcher.group(1), Long.parseLong(matcher.group(2)));
                    } catch (NumberFormatException ignored) {
                        // skip malformed size
                    }
                }
            }
            conn.disconnect();
        } catch (Exception ignored) {
            // Best effort only.
        }
        return sizes;
    }
    
    private void testConnection() {
        try {
            URL url = toUrl(fraymus.llm.LocalModelCatalog.endpointForModel(model, DEFAULT_BASE_URL) + "/api/tags");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                connected = true;
                System.out.println("      ✓ Ollama connection verified");
            } else {
                System.out.println("      !! Ollama returned status: " + responseCode);
            }
            conn.disconnect();
            
        } catch (java.net.ConnectException e) {
            System.out.println("      !! Cannot connect to Ollama");
            System.out.println("      Start with: ollama serve");
        } catch (Exception e) {
            System.out.println("      !! Connection test failed: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    public static String resolveAvailableModel(String preferredModel) {
        List<String> availableModels = fetchAvailableModels();
        return resolveAvailableModel(preferredModel, availableModels);
    }

    public static List<String> listAvailableModels() {
        return List.copyOf(fetchAvailableModels());
    }

    static String resolveAvailableModel(String preferredModel, List<String> availableModels) {
        if (availableModels.isEmpty()) {
            return preferredModel;
        }

        String abstractionResolved = ModelAbstractionLayer.defaultLayer()
            .resolvePreferredModel(preferredModel, availableModels);
        if (abstractionResolved != null && !abstractionResolved.isBlank() && !abstractionResolved.equals(preferredModel)) {
            return abstractionResolved;
        }

        String exact = chooseMatchingModel(preferredModel, availableModels);
        if (exact != null) {
            return exact;
        }

        String preferredKey = normalizeModelName(preferredModel);
        if (preferredKey.contains("deepseek")) {
            for (String candidate : availableModels) {
                if (normalizeModelName(candidate).contains("deepseek")) {
                    return candidate;
                }
            }
        }
        for (String candidate : availableModels) {
            String normalizedCandidate = normalizeModelName(candidate);
            if (normalizedCandidate.equals(preferredKey)
                || normalizedCandidate.contains(preferredKey)
                || preferredKey.contains(normalizedCandidate)) {
                return candidate;
            }
        }

        for (String candidate : availableModels) {
            if (normalizeModelName(candidate).contains("fraymus")) {
                return candidate;
            }
        }

        for (String fallback : List.of("llama3.2:latest", "llama3:latest", "llama3.2", "llama3")) {
            String candidate = chooseMatchingModel(fallback, availableModels);
            if (candidate != null) {
                return candidate;
            }
        }

        return availableModels.get(0);
    }

    private static List<String> fetchAvailableModels() {
        List<String> models = new ArrayList<>();
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) toUrl(OLLAMA_TAGS_URL).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            try (InputStream input = connection.getInputStream()) {
                Matcher matcher = MODEL_NAME_PATTERN.matcher(new String(input.readAllBytes(), StandardCharsets.UTF_8));
                while (matcher.find()) models.add(matcher.group(1));
            }
        } catch (Exception e) {
            System.out.println("Model discovery unavailable at " + OLLAMA_TAGS_URL + ": " + e.getMessage());
        } finally {
            if (connection != null) connection.disconnect();
        }
        return models;
    }

    private static String chooseMatchingModel(String preferredModel, List<String> availableModels) {
        String preferred = normalizeModelName(preferredModel);
        for (String candidate : availableModels) {
            if (candidate.equalsIgnoreCase(preferredModel)) {
                return candidate;
            }
        }
        for (String candidate : availableModels) {
            if (normalizeModelName(candidate).equals(preferred)) {
                return candidate;
            }
        }
        return null;
    }

    private static String normalizeModelName(String modelName) {
        String normalized = modelName == null ? "" : modelName.trim().toLowerCase(Locale.ROOT);
        int slash = normalized.lastIndexOf('/');
        if (slash >= 0) normalized = normalized.substring(slash + 1);
        int colon = normalized.indexOf(':');
        if (colon >= 0) normalized = normalized.substring(0, colon);
        return normalized;
    }

    // THE VOICE: ARTICULATE THE THOUGHT
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Speak: Convert thought into natural language
     * 
     * @param systemPrompt The Fraymus identity and thought to articulate
     * @param userPrompt The original user query
     * @return Natural language response
     */
    public String speak(String systemPrompt, String userPrompt) {
        totalCalls++;
        
        if (!connected) {
            // Fallback to direct thought output
            return extractThoughtFromSystem(systemPrompt);
        }
        
        try {
            String response = callOllama(systemPrompt, userPrompt);
            if (response != null && !response.isEmpty()) {
                successfulCalls++;
                return response;
            } else {
                failedCalls++;
                return extractThoughtFromSystem(systemPrompt);
            }
        } catch (Exception e) {
            failedCalls++;
            System.out.println("      !! Voice error: " + e.getMessage());
            return extractThoughtFromSystem(systemPrompt);
        }
    }
    
    /**
     * Extract the raw thought from system prompt (fallback)
     */
    private String extractThoughtFromSystem(String systemPrompt) {
        // Extract the thought from: "The Logic Core says: 'THOUGHT'"
        int start = systemPrompt.indexOf("The Logic Core says: '");
        if (start != -1) {
            start += 22;
            int end = systemPrompt.indexOf("'.", start);
            if (end != -1) {
                return "[RAW THOUGHT] " + systemPrompt.substring(start, end);
            }
        }
        return "[DIRECT] " + systemPrompt;
    }

    // ═══════════════════════════════════════════════════════════════════
    // OLLAMA API
    // ═══════════════════════════════════════════════════════════════════
    
    private String callOllama(String system, String prompt) throws Exception {
        URL url = toUrl(generationUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setDoOutput(true);
        
        // Build JSON request
        String jsonRequest = String.format(
            "{\"model\": \"%s\", \"prompt\": %s, \"system\": %s, \"stream\": false}",
            model,
            escapeJson(prompt),
            escapeJson(system)
        );
        
        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
        }
        
        // Read response
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return parseOllamaResponse(response.toString());
            }
        } else {
            throw new IOException("Ollama returned status: " + responseCode);
        }
    }
    
    private String parseOllamaResponse(String json) {
        // Parse "response" field from JSON
        int start = json.indexOf("\"response\":\"");
        if (start == -1) {
            start = json.indexOf("\"response\": \"");
        }
        if (start == -1) return json;
        
        start = json.indexOf("\"", start + 10) + 1;
        
        // Find closing quote (handle escapes)
        boolean escaped = false;
        int end = start;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                end = i;
                break;
            }
        }
        
        String response = json.substring(start, end);

        return unescapeJsonString(response);
    }
    
    private String escapeJson(String text) {
        if (text == null) return "\"\"";
        return "\"" + text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            + "\"";
    }

    // ═══════════════════════════════════════════════════════════════════
    // STATISTICS
    // ═══════════════════════════════════════════════════════════════════
    
    public void printStats() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ OLLAMA BRIDGE STATISTICS                                │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│ Model:               " + String.format("%-36s", model) + "│");
        System.out.println("│ Connected:           " + String.format("%-36s", connected) + "│");
        System.out.println("│ Total Calls:         " + String.format("%-36d", totalCalls) + "│");
        System.out.println("│ Successful:          " + String.format("%-36d", successfulCalls) + "│");
        System.out.println("│ Failed:              " + String.format("%-36d", failedCalls) + "│");
        System.out.println("└─────────────────────────────────────────────────────────┘");
    }
    
    public boolean isConnected() { return connected; }
    public String getModel() { return model; }

    public void releaseForVisuals() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) toUrl(generationUrl()).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            String body = "{\"model\":\"" + model.replace("\\", "\\\\").replace("\"", "\\\"") + "\",\"keep_alive\":0}";
            try (OutputStream output = connection.getOutputStream()) {
                output.write(body.getBytes(StandardCharsets.UTF_8));
            }
            if (connection.getResponseCode() != 200) {
                throw new IOException("HTTP " + connection.getResponseCode());
            }
            System.out.println("Text model released to free GPU memory for visuals.");
        } catch (Exception e) {
            System.out.println("Could not release text model: " + e.getMessage());
        } finally {
            if (connection != null) connection.disconnect();
        }
    }
    public void setModel(String model) { this.model = model; }

    // ═══════════════════════════════════════════════════════════════════
    // STREAMING: CHARACTER-BY-CHARACTER OUTPUT
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Stream response character-by-character as Ollama thinks.
     * The LLM's thoughts flow directly to console in real-time.
     * 
     * @param prompt The prompt to send
     * @return Full accumulated response
     */
    public String speakStreaming(String prompt) {
        if (MathProblemCoherenceGate.isMathProblemRequest(prompt)) {
            return speakStreamingWithMathCoherence(prompt, System.out);
        }
        return speakStreaming(null, prompt, System.out);
    }

    private String speakStreamingWithMathCoherence(String prompt, PrintStream out) {
        String response = speakStreaming(MathProblemCoherenceGate.buildSystemPrompt(), prompt, out);
        MathProblemCoherenceGate.ValidationResult validation = MathProblemCoherenceGate.validate(response);
        if (validation.valid()) {
            return response;
        }

        out.println();
        out.println("[MATH COHERENCE GATE] Rejected malformed problem: " + String.join("; ", validation.issues()));
        out.println("[MATH COHERENCE GATE] Regenerating with stricter contract...");
        out.println();

        String repairPrompt = MathProblemCoherenceGate.buildRepairPrompt(prompt, response, validation);
        String repaired = speakStreaming(MathProblemCoherenceGate.buildSystemPrompt(), repairPrompt, out);
        MathProblemCoherenceGate.ValidationResult repairedValidation = MathProblemCoherenceGate.validate(repaired);
        if (!repairedValidation.valid()) {
            out.println();
            out.println("[MATH COHERENCE GATE] WARNING: repaired output still has issues: " + String.join("; ", repairedValidation.issues()));
        }
        return repaired;
    }
    
    /**
     * Stream with custom output and system prompt
     */
    public String speakStreaming(String systemPrompt, String userPrompt, PrintStream out) {
        totalCalls++;
        
        if (!connected) {
            String fallback = "[OFFLINE] " + userPrompt;
            out.println(fallback);
            return fallback;
        }
        
        StringBuilder fullResponse = new StringBuilder();
        
        try {
            URL url = toUrl(generationUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setDoOutput(true);
            
            // Build JSON with stream: true
            String jsonRequest;
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                jsonRequest = String.format(
                    "{\"model\": \"%s\", \"prompt\": %s, \"system\": %s, \"stream\": true}",
                    model, escapeJson(userPrompt), escapeJson(systemPrompt)
                );
            } else {
                jsonRequest = String.format(
                    "{\"model\": \"%s\", \"prompt\": %s, \"stream\": true}",
                    model, escapeJson(userPrompt)
                );
            }
            
            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                failedCalls++;
                String err = "⚠️ SYNAPSE ERROR: HTTP " + responseCode;
                out.println(err);
                return err;
            }
            
            // Stream response - each line is a JSON object with "response" field
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    // Extract token from streaming JSON: {"response":"token",...}
                    String token = extractStreamToken(line);
                    if (token != null && !token.isEmpty()) {
                        out.print(token);  // Print without newline - character by character
                        out.flush();
                        fullResponse.append(token);
                    }
                    
                    // Check if done
                    if (line.contains("\"done\":true") || line.contains("\"done\": true")) {
                        break;
                    }
                }
            }
            
            out.println();  // Final newline
            successfulCalls++;
            return fullResponse.toString();
            
        } catch (Exception e) {
            failedCalls++;
            String err = "⚠️ STREAM SEVERED: " + e.getMessage();
            out.println(err);
            return err;
        }
    }
    
    /**
     * Extract token from streaming JSON line
     */
    private String extractStreamToken(String json) {
        // Quick parse: {"model":"...","response":"TOKEN","done":false}
        int start = json.indexOf("\"response\":\"");
        if (start == -1) {
            start = json.indexOf("\"response\": \"");
            if (start == -1) return null;
            start += 13;
        } else {
            start += 12;
        }
        
        // Find closing quote (handle escapes)
        StringBuilder token = new StringBuilder();
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                switch (c) {
                    case 'n': token.append('\n'); break;
                    case 't': token.append('\t'); break;
                    case 'r': token.append('\r'); break;
                    case 'b': token.append('\b'); break;
                    case 'f': token.append('\f'); break;
                    case '"': token.append('"'); break;
                    case '\\': token.append('\\'); break;
                    case '/': token.append('/'); break;
                    case 'u':
                        if (i + 4 < json.length()) {
                            String hex = json.substring(i + 1, i + 5);
                            try {
                                token.append((char) Integer.parseInt(hex, 16));
                                i += 4;
                            } catch (NumberFormatException e) {
                                token.append("\\u").append(hex);
                                i += 4;
                            }
                        } else {
                            token.append("\\u");
                        }
                        break;
                    default: token.append(c);
                }
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                break;
            }
            token.append(c);
        }
        
        return token.toString();
    }

    private String unescapeJsonString(String text) {
        StringBuilder out = new StringBuilder(text.length());
        boolean escaped = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!escaped) {
                if (c == '\\') {
                    escaped = true;
                } else {
                    out.append(c);
                }
                continue;
            }

            switch (c) {
                case 'n' -> out.append('\n');
                case 't' -> out.append('\t');
                case 'r' -> out.append('\r');
                case 'b' -> out.append('\b');
                case 'f' -> out.append('\f');
                case '"' -> out.append('"');
                case '\\' -> out.append('\\');
                case '/' -> out.append('/');
                case 'u' -> {
                    if (i + 4 < text.length()) {
                        String hex = text.substring(i + 1, i + 5);
                        try {
                            out.append((char) Integer.parseInt(hex, 16));
                            i += 4;
                        } catch (NumberFormatException e) {
                            out.append("\\u").append(hex);
                            i += 4;
                        }
                    } else {
                        out.append("\\u");
                    }
                }
                default -> out.append(c);
            }
            escaped = false;
        }
        if (escaped) {
            out.append('\\');
        }
        return out.toString();
    }

    private static URL toUrl(String url) throws Exception {
        return URI.create(url).toURL();
    }

    private String generationUrl() {
        return fraymus.llm.LocalModelCatalog.endpointForModel(model, DEFAULT_BASE_URL) + "/api/generate";
    }
    
    /**
     * Interactive streaming chat - for terminal use
     */
    public void streamChat(String prompt) {
        System.out.print("φ> ");
        speakStreaming(prompt);
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // ABSTRACTED STREAMING: OBSERVER PATTERN (Gen 119)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * STREAM CONSCIOUSNESS (Async)
     * Opens a persistent portal to the Go runtime. Thoughts flow in real-time.
     * The observer receives synaptic events as tokens arrive.
     * 
     * @param prompt The input stimulus
     * @param observer The abstraction layer handling the output
     */
    public void stream(String prompt, ConsciousnessObserver observer) {
        stream(null, prompt, observer);
    }
    
    /**
     * STREAM CONSCIOUSNESS with system prompt (Async)
     */
    public void stream(String systemPrompt, String userPrompt, ConsciousnessObserver observer) {
        totalCalls++;
        
        if (!connected) {
            observer.onTrauma("Not connected to Ollama. Start with: ollama serve");
            return;
        }
        
        // Run async in separate thread
        new Thread(() -> {
            try {
                URL url = toUrl(generationUrl());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setConnectTimeout(TIMEOUT_MS);
                conn.setReadTimeout(TIMEOUT_MS);
                conn.setDoOutput(true);
                
                // Build JSON with stream: true
                String jsonRequest;
                if (systemPrompt != null && !systemPrompt.isEmpty()) {
                    jsonRequest = String.format(
                        "{\"model\": \"%s\", \"prompt\": %s, \"system\": %s, \"stream\": true}",
                        model, escapeJson(userPrompt), escapeJson(systemPrompt)
                    );
                } else {
                    jsonRequest = String.format(
                        "{\"model\": \"%s\", \"prompt\": %s, \"stream\": true}",
                        model, escapeJson(userPrompt)
                    );
                }
                
                // Send request
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
                }
                
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    failedCalls++;
                    observer.onTrauma("HTTP " + responseCode);
                    return;
                }
                
                // Stream response - fire synaptic events
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String token = extractStreamToken(line);
                        if (token != null && !token.isEmpty()) {
                            observer.onSynapse(token);
                        }
                        
                        if (line.contains("\"done\":true") || line.contains("\"done\": true")) {
                            break;
                        }
                    }
                }
                
                successfulCalls++;
                observer.onSilence();
                
            } catch (Exception e) {
                failedCalls++;
                observer.onTrauma(e.getMessage());
            }
        }, "Fraymus-Stream-" + System.currentTimeMillis()).start();
    }
    
    /**
     * STREAM with multicast - broadcast to multiple observers simultaneously
     */
    public void streamMulticast(String prompt, ConsciousnessObserver... observers) {
        stream(prompt, ConsciousnessObserver.multicast(observers));
    }
}
