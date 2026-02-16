package io.fraymus.ai.core;

import com.google.gson.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * OLLAMA CLIENT - LLM Interface
 * 
 * Handles communication with Ollama API:
 * - /api/chat for text generation
 * - /api/embed for embeddings
 */
public class OllamaClient {

    private static final String CHAT_URL = "http://localhost:11434/api/chat";
    private static final String EMBED_URL = "http://localhost:11434/api/embed";

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private final String chatModel;
    private final String embedModel;

    public OllamaClient(String chatModel, String embedModel) {
        this.chatModel = chatModel;
        this.embedModel = embedModel;
    }

    public static class Message {
        public String role;
        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    /**
     * CHAT ONCE
     * Non-streaming chat completion
     */
    public String chatOnce(List<Message> messages, JsonElement formatSchema, Map<String, Object> options) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("model", chatModel);
            body.add("messages", gson.toJsonTree(messages));
            body.addProperty("stream", false);

            if (formatSchema != null) {
                body.add("format", formatSchema);
            }
            if (options != null && !options.isEmpty()) {
                body.add("options", gson.toJsonTree(options));
            }

            JsonObject resp = postJson(CHAT_URL, body);
            JsonObject msg = resp.getAsJsonObject("message");
            return msg != null && msg.has("content") ? msg.get("content").getAsString() : resp.toString();
        } catch (Exception e) {
            return "[OLLAMA ERROR] " + e.getMessage();
        }
    }

    /**
     * EMBED ONE
     * Generate embedding for single text
     */
    public double[] embedOne(String text) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("model", embedModel);
            body.addProperty("input", text);

            JsonObject resp = postJson(EMBED_URL, body);
            JsonArray embeddings = resp.getAsJsonArray("embeddings");
            if (embeddings == null || embeddings.size() == 0) return new double[0];

            JsonArray vec = embeddings.get(0).getAsJsonArray();
            double[] out = new double[vec.size()];
            for (int i = 0; i < vec.size(); i++) out[i] = vec.get(i).getAsDouble();
            return out;
        } catch (Exception e) {
            return new double[0];
        }
    }

    /**
     * EMBED BATCH
     * Generate embeddings for multiple texts
     */
    public List<double[]> embedBatch(List<String> texts) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("model", embedModel);
            body.add("input", gson.toJsonTree(texts));

            JsonObject resp = postJson(EMBED_URL, body);
            JsonArray embeddings = resp.getAsJsonArray("embeddings");
            List<double[]> out = new ArrayList<>();
            if (embeddings == null) return out;

            for (int i = 0; i < embeddings.size(); i++) {
                JsonArray vec = embeddings.get(i).getAsJsonArray();
                double[] arr = new double[vec.size()];
                for (int j = 0; j < vec.size(); j++) arr[j] = vec.get(j).getAsDouble();
                out.add(arr);
            }
            return out;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private JsonObject postJson(String urlStr, JsonObject body) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        byte[] payload = gson.toJson(body).getBytes(StandardCharsets.UTF_8);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload);
        }

        InputStream is = (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)
                ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return JsonParser.parseString(sb.toString()).getAsJsonObject();
    }
}
