package gemini.root;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * THE OLLAMA SPINE
 * Role: Connects the Java Nervous System to your Local LLM.
 * Abstraction: "Thought" is just an API call to localhost:11434.
 * 
 * ZERO EXTERNAL DEPENDENCIES - uses java.net built-in.
 */
public class OllamaSpine {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private String modelName;
    private double temperature = 0.618; // Phi-harmonic default

    public OllamaSpine(String modelName) {
        this.modelName = modelName;
    }

    public void setTemperature(double temp) {
        this.temperature = temp;
    }

    /**
     * TRANSMIT THOUGHT
     * Sends the prompt + context (Transmudder data) to the AI.
     */
    public String think(String prompt, String context) {
        try {
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(300000); // 5 min for long responses

            // Construct JSON Payload
            String fullPrompt = context.isEmpty() 
                ? prompt 
                : "[CONTEXT: " + context + "] USER: " + prompt;
            
            String jsonInput = String.format(
                "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false, \"options\": {\"temperature\": %f}}",
                modelName, escapeJson(fullPrompt), temperature
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the Response
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return "[OLLAMA ERROR] HTTP " + responseCode;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            return extractResponseText(response.toString());

        } catch (java.net.ConnectException e) {
            return "[OLLAMA OFFLINE] Make sure 'ollama serve' is running.";
        } catch (Exception e) {
            return "[OLLAMA ERROR] " + e.getMessage();
        }
    }

    /**
     * SIMPLE THINK (No context)
     */
    public String think(String prompt) {
        return think(prompt, "");
    }

    /**
     * STREAM THINK (For real-time output)
     */
    public void thinkStream(String prompt, String context, java.util.function.Consumer<String> onToken) {
        try {
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String fullPrompt = context.isEmpty() ? prompt : "[CONTEXT: " + context + "] " + prompt;
            String jsonInput = String.format(
                "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": true}",
                modelName, escapeJson(fullPrompt)
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes("utf-8"));
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
                String token = extractResponseText(line);
                if (!token.isEmpty()) {
                    onToken.accept(token);
                }
            }
            br.close();

        } catch (Exception e) {
            onToken.accept("[ERROR] " + e.getMessage());
        }
    }

    private String extractResponseText(String json) {
        // Extract "response" field without external JSON library
        int startIndex = json.indexOf("\"response\":\"");
        if (startIndex == -1) {
            startIndex = json.indexOf("\"response\": \"");
            if (startIndex != -1) startIndex += 13;
            else return "";
        } else {
            startIndex += 12;
        }
        
        // Find end of response string
        int endIndex = startIndex;
        boolean escaped = false;
        while (endIndex < json.length()) {
            char c = json.charAt(endIndex);
            if (escaped) {
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                break;
            }
            endIndex++;
        }
        
        if (endIndex > startIndex) {
            String raw = json.substring(startIndex, endIndex);
            return raw.replace("\\n", "\n").replace("\\\"", "\"").replace("\\t", "\t");
        }
        return "";
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public String getModel() { return modelName; }
    public void setModel(String model) { this.modelName = model; }
}
