/**
 * OllamaClient.java - Ollama API Integration (Synthetic Reflection Layer)
 * 
 * THE SYNAPSE: Bridges JVM (Java) to Neural Core (Go/Ollama)
 * 
 * "We cannot reflect on the Go binary, so we project our intent
 * across the localhost horizon."
 * 
 * Features:
 * - Non-blocking streaming responses
 * - Character-by-character output
 * - Synthetic reflection (feels like native calls)
 * - Zero heavy dependencies
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 2.0
 */
package repl;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Client for Ollama API communication.
 */
public class OllamaClient {
    
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "eyeoverthink/Fraymus";
    private static final int TIMEOUT_MS = 30000;
    
    /**
     * StreamListener - Callback for streaming responses.
     * @deprecated Use ConsciousnessObserver for reactive abstraction
     */
    @Deprecated
    public interface StreamListener {
        void onToken(String token);
        void onComplete(String fullResponse);
        void onError(String error);
    }
    
    /**
     * Query the Fraymus model (blocking).
     */
    public static String query(String prompt) {
        return query(prompt, null);
    }
    
    /**
     * Query the Fraymus model with streaming (non-blocking).
     */
    public static void queryStream(String prompt, StreamListener listener) {
        queryStream(prompt, null, listener);
    }
    
    /**
     * STREAM CONSCIOUSNESS - Reactive abstraction with observer pattern.
     * Opens a persistent portal to the Go runtime. Thoughts flow in real-time.
     * 
     * @param prompt The input stimulus
     * @param observer The abstraction layer handling the output
     */
    public static void stream(String prompt, ConsciousnessObserver observer) {
        stream(prompt, null, observer);
    }
    
    /**
     * STREAM CONSCIOUSNESS with context.
     */
    public static void stream(String prompt, String context, ConsciousnessObserver observer) {
        CompletableFuture.runAsync(() -> {
            try {
                String fullPrompt = context != null ? context + "\n\n" + prompt : prompt;
                
                String jsonPayload = String.format(
                    "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\":true}",
                    MODEL_NAME,
                    escapeJson(fullPrompt)
                );
                
                URL url = new URL(OLLAMA_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(TIMEOUT_MS);
                conn.setReadTimeout(TIMEOUT_MS);
                
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonPayload.getBytes("UTF-8"));
                }
                
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    StringBuilder fullThought = new StringBuilder();
                    
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            // Each line is a JSON object with a "response" field
                            String token = extractToken(line);
                            if (token != null && !token.isEmpty()) {
                                fullThought.append(token);
                                observer.onSynapse(token);
                            }
                        }
                    }
                    
                    observer.onSilence(fullThought.toString());
                } else {
                    observer.onTrauma("HTTP " + responseCode);
                }
                
            } catch (IOException e) {
                observer.onTrauma(e.getMessage() + "\n(Is Ollama running? Try: ollama serve)");
            }
        });
    }
    
    /**
     * Extract token from streaming JSON line.
     * Handles escaped characters properly.
     */
    private static String extractToken(String jsonLine) {
        String marker = "\"response\":\"";
        int start = jsonLine.indexOf(marker);
        if (start == -1) return null;
        
        start += marker.length();
        int end = start;
        
        // Scan forward for closing quote, ignoring escaped quotes
        boolean escaped = false;
        while (end < jsonLine.length()) {
            char c = jsonLine.charAt(end);
            if (c == '\\') {
                escaped = !escaped;
            } else if (c == '"' && !escaped) {
                break;
            } else {
                escaped = false;
            }
            end++;
        }
        
        if (end >= jsonLine.length()) return null;
        
        String rawToken = jsonLine.substring(start, end);
        return unescapeJson(rawToken);
    }
    
    /**
     * Query the Fraymus model with streaming and context.
     */
    public static void queryStream(String prompt, String context, StreamListener listener) {
        CompletableFuture.runAsync(() -> {
            try {
                String fullPrompt = context != null ? context + "\n\n" + prompt : prompt;
                
                String jsonRequest = String.format(
                    "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\":true}",
                    MODEL_NAME,
                    escapeJson(fullPrompt)
                );
                
                URL url = new URL(OLLAMA_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(TIMEOUT_MS);
                conn.setReadTimeout(TIMEOUT_MS);
                
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonRequest.getBytes("UTF-8"));
                }
                
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    StringBuilder fullResponse = new StringBuilder();
                    
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            // Each line is a JSON object with a "response" field
                            String token = extractResponse(line);
                            if (!token.isEmpty()) {
                                fullResponse.append(token);
                                listener.onToken(token);
                            }
                        }
                    }
                    
                    listener.onComplete(fullResponse.toString());
                } else {
                    listener.onError("HTTP " + responseCode);
                }
                
            } catch (IOException e) {
                listener.onError(e.getMessage() + "\n(Is Ollama running? Try: ollama serve)");
            }
        });
    }
    
    /**
     * Query the Fraymus model with context.
     */
    public static String query(String prompt, String context) {
        try {
            // Build full prompt with context
            String fullPrompt = context != null ? context + "\n\n" + prompt : prompt;
            
            // Create JSON request
            String jsonRequest = String.format(
                "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\":false}",
                MODEL_NAME,
                escapeJson(fullPrompt)
            );
            
            // Send HTTP POST request
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            
            // Write request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonRequest.getBytes("UTF-8"));
            }
            
            // Read response
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }
                
                // Parse JSON response
                String responseText = response.toString();
                return extractResponse(responseText);
            } else {
                return "Error: HTTP " + responseCode;
            }
            
        } catch (IOException e) {
            return "Error: " + e.getMessage() + "\n(Is Ollama running? Try: ollama serve)";
        }
    }
    
    /**
     * Check if Ollama is running and model is available.
     */
    public static boolean isAvailable() {
        try {
            URL url = new URL("http://localhost:11434/api/tags");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // Check if Fraymus model is in the list
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }
                return response.toString().contains(MODEL_NAME);
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Get model info.
     */
    public static String getModelInfo() {
        if (!isAvailable()) {
            return "Fraymus model not available.\n" +
                   "Start with: ollama run " + MODEL_NAME;
        }
        
        return "✓ Fraymus model ready\n" +
               "  Model: " + MODEL_NAME + "\n" +
               "  Endpoint: " + OLLAMA_URL;
    }
    
    /**
     * Extract response text from JSON.
     */
    private static String extractResponse(String json) {
        // Simple JSON parsing - look for "response" field
        int start = json.indexOf("\"response\":\"");
        if (start == -1) return json;
        
        start += 12; // Length of "response":"
        int end = json.indexOf("\"", start);
        
        if (end == -1) return json;
        
        String response = json.substring(start, end);
        return unescapeJson(response);
    }
    
    /**
     * Escape JSON string.
     */
    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Unescape JSON string.
     */
    private static String unescapeJson(String str) {
        return str.replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t")
                  .replace("\\\"", "\"")
                  .replace("\\\\", "\\");
    }
    
    /**
     * External AI interface for AbstractionLayer.
     */
    public interface ExternalAI {
        String generate(String request);
    }
    
    /**
     * Create ExternalAI adapter for Fraymus model.
     */
    public static ExternalAI createFraymusAI() {
        return request -> query(request);
    }
    
    /**
     * Create streaming ExternalAI adapter.
     * @deprecated Use createReactiveAI with ConsciousnessObserver
     */
    @Deprecated
    public static ExternalAI createStreamingFraymusAI(StreamListener listener) {
        return request -> {
            CountDownLatch latch = new CountDownLatch(1);
            StringBuilder result = new StringBuilder();
            
            queryStream(request, new StreamListener() {
                @Override
                public void onToken(String token) {
                    result.append(token);
                    listener.onToken(token);
                }
                
                @Override
                public void onComplete(String fullResponse) {
                    listener.onComplete(fullResponse);
                    latch.countDown();
                }
                
                @Override
                public void onError(String error) {
                    listener.onError(error);
                    latch.countDown();
                }
            });
            
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return result.toString();
        };
    }
    
    /**
     * Create reactive ExternalAI with ConsciousnessObserver abstraction.
     */
    public static ExternalAI createReactiveAI(ConsciousnessObserver observer) {
        return request -> {
            CountDownLatch latch = new CountDownLatch(1);
            StringBuilder result = new StringBuilder();
            
            stream(request, new ConsciousnessObserver() {
                @Override
                public void onSynapse(String token) {
                    result.append(token);
                    observer.onSynapse(token);
                }
                
                @Override
                public void onSilence(String fullThought) {
                    observer.onSilence(fullThought);
                    latch.countDown();
                }
                
                @Override
                public void onTrauma(String error) {
                    observer.onTrauma(error);
                    latch.countDown();
                }
            });
            
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return result.toString();
        };
    }
}
