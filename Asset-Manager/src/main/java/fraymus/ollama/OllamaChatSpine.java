package fraymus.ollama;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * OLLAMA CHAT SPINE - Production /api/chat Interface
 * 
 * Supports:
 * - messages[] array (proper chat history)
 * - Streaming tokens
 * - format parameter (JSON schema)
 * - keep_alive
 * - options (temperature, num_ctx, etc.)
 */
public class OllamaChatSpine {
    private static final String CHAT_URL = "http://localhost:11434/api/chat";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String modelName;

    public OllamaChatSpine(String modelName) {
        this.modelName = modelName;
    }

    @FunctionalInterface
    public interface TokenSink {
        void onToken(String token);
    }

    public static class Result {
        public final String content;
        public final String thinking;
        
        public Result(String content, String thinking) {
            this.content = content;
            this.thinking = thinking;
        }
    }

    /**
     * CHAT STREAM
     * Streams response from /api/chat with full message history
     */
    public Result chatStream(
            List<ConversationMemory.Msg> messages,
            Map<String, Object> formatSchemaOrNull,
            Map<String, Object> optionsOrNull,
            String keepAlive,
            TokenSink sink
    ) throws IOException {

        URL url = new URL(CHAT_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(300000); // 5 minutes for long responses

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", modelName);

        // Convert messages to Ollama format
        List<Map<String, Object>> msgList = new ArrayList<>();
        for (ConversationMemory.Msg m : messages) {
            msgList.add(Map.of("role", m.role, "content", m.content));
        }
        payload.put("messages", msgList);

        payload.put("stream", true);

        if (keepAlive != null && !keepAlive.isBlank()) {
            payload.put("keep_alive", keepAlive);
        }
        if (optionsOrNull != null && !optionsOrNull.isEmpty()) {
            payload.put("options", optionsOrNull);
        }
        if (formatSchemaOrNull != null) {
            payload.put("format", formatSchemaOrNull);
        }

        byte[] body = MAPPER.writeValueAsBytes(payload);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body);
        }

        // Check response code
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP " + responseCode + " from Ollama");
        }

        StringBuilder content = new StringBuilder();
        StringBuilder thinking = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                JsonNode node = MAPPER.readTree(line);

                JsonNode msg = node.get("message");
                if (msg != null) {
                    JsonNode c = msg.get("content");
                    if (c != null && !c.isNull()) {
                        String t = c.asText();
                        if (!t.isEmpty()) {
                            content.append(t);
                            if (sink != null) {
                                sink.onToken(t);
                            }
                        }
                    }
                    JsonNode th = msg.get("thinking");
                    if (th != null && !th.isNull()) {
                        String tt = th.asText();
                        if (!tt.isEmpty()) {
                            thinking.append(tt);
                        }
                    }
                }

                JsonNode done = node.get("done");
                if (done != null && done.asBoolean(false)) {
                    break;
                }
            }
        }

        return new Result(content.toString(), thinking.toString());
    }

    /**
     * Test connection
     */
    public boolean testConnection() {
        try {
            URL url = new URL("http://localhost:11434/api/tags");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
