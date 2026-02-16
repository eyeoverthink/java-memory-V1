package gemini.root;

import com.google.gson.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class OllamaSpine {

    private static final String CHAT_URL = "http://localhost:11434/api/chat";
    private static final String EMBED_URL = "http://localhost:11434/api/embed";

    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 120_000;
    private static final int MAX_RETRIES = 3;
    private static final int MAX_EMBED_BATCH = 32;
    private static final int MAX_EMBED_CHARS = 8000;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private final String chatModel;
    private final String embedModel;

    public OllamaSpine(String chatModel, String embedModel) {
        this.chatModel = chatModel;
        this.embedModel = embedModel;
    }

    public static class Msg {
        public String role;   // system | user | assistant
        public String content;

        public Msg(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    /**
     * CHAT ONCE: Non-streaming chat with optional schema enforcement.
     */
    public String chatOnce(List<Msg> messages, JsonElement formatSchema, Map<String, Object> options) {
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
            return "[OLLAMA CHAT ERROR] " + e.getMessage();
        }
    }

    public float[] embedOne(String text) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("model", embedModel);
            body.addProperty("input", text);

            JsonObject resp = postJson(EMBED_URL, body);
            JsonArray embeddings = resp.getAsJsonArray("embeddings");
            if (embeddings == null || embeddings.size() == 0) return new float[0];

            JsonArray vec = embeddings.get(0).getAsJsonArray();
            float[] out = new float[vec.size()];
            for (int i = 0; i < vec.size(); i++) out[i] = vec.get(i).getAsFloat();
            return out;
        } catch (Exception e) {
            return new float[0];
        }
    }

    /**
     * EMBED BATCH: Get embeddings for multiple texts.
     * Hard caps: MAX_EMBED_BATCH texts, MAX_EMBED_CHARS per text.
     */
    public List<float[]> embedBatch(List<String> texts) {
        List<float[]> out = new ArrayList<>();
        if (texts == null || texts.isEmpty()) return out;

        // Cap batch size
        List<String> capped = new ArrayList<>();
        for (int i = 0; i < Math.min(texts.size(), MAX_EMBED_BATCH); i++) {
            String t = texts.get(i);
            if (t.length() > MAX_EMBED_CHARS) t = t.substring(0, MAX_EMBED_CHARS);
            capped.add(t);
        }

        try {
            JsonObject body = new JsonObject();
            body.addProperty("model", embedModel);
            body.add("input", gson.toJsonTree(capped));

            JsonObject resp = postJsonWithRetry(EMBED_URL, body);
            JsonArray embeddings = resp.getAsJsonArray("embeddings");
            if (embeddings == null) return out;

            for (int i = 0; i < embeddings.size(); i++) {
                JsonArray vec = embeddings.get(i).getAsJsonArray();
                float[] arr = new float[vec.size()];
                for (int j = 0; j < vec.size(); j++) arr[j] = vec.get(j).getAsFloat();
                out.add(arr);
            }
            return out;
        } catch (Exception e) {
            System.err.println("[EMBED_BATCH_ERROR] " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private JsonObject postJsonWithRetry(String urlStr, JsonObject body) throws IOException {
        IOException lastErr = null;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return postJson(urlStr, body);
            } catch (IOException e) {
                lastErr = e;
                if (attempt < MAX_RETRIES) {
                    try { Thread.sleep(500L * attempt); } catch (InterruptedException ignored) {}
                }
            }
        }
        throw lastErr != null ? lastErr : new IOException("Max retries exceeded");
    }

    private JsonObject postJson(String urlStr, JsonObject body) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        byte[] payload = gson.toJson(body).getBytes(StandardCharsets.UTF_8);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload);
        }

        int code = conn.getResponseCode();
        InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
        if (is == null) throw new IOException("HTTP " + code + " with no body");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return JsonParser.parseString(sb.toString()).getAsJsonObject();
    }

}
