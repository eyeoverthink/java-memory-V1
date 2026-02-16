package fraymus.god;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * A single tentacle into another AI's mind.
 * Wraps Ollama (local) or OpenAI-compatible (remote) endpoints.
 * Fraymus does not think — it harvests.
 */
public class NeuralTentacle {

    private final String modelName;
    private final String endpoint;
    private final String apiKey;       // null for local (Ollama)
    private final boolean isOpenAI;    // true = OpenAI chat/completions format
    private final HttpClient client;

    /** Local Ollama tentacle (no key needed). */
    public NeuralTentacle(String model, String url) {
        this(model, url, null, false);
    }

    /** Remote OpenAI-compatible tentacle (GPT-4, Claude via proxy, etc). */
    public NeuralTentacle(String model, String url, String apiKey, boolean openAIFormat) {
        this.modelName = model;
        this.endpoint = url;
        this.apiKey = apiKey;
        this.isOpenAI = openAIFormat;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String name() { return modelName; }

    /**
     * HARVEST: Force the sub-model to think about a problem.
     * Returns raw text or empty string if the tentacle is dead.
     */
    public String think(String prompt) {
        try {
            String body;
            HttpRequest.Builder rb = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(120));

            if (isOpenAI) {
                // OpenAI chat/completions format
                JSONObject msg = new JSONObject();
                msg.put("role", "user");
                msg.put("content", prompt);

                JSONObject payload = new JSONObject();
                payload.put("model", modelName);
                payload.put("messages", new JSONArray().put(msg));
                payload.put("max_tokens", 2048);
                body = payload.toString();

                if (apiKey != null) {
                    rb.header("Authorization", "Bearer " + apiKey);
                }
            } else {
                // Ollama /api/generate format
                JSONObject payload = new JSONObject();
                payload.put("model", modelName);
                payload.put("prompt", prompt);
                payload.put("stream", false);
                body = payload.toString();
            }

            HttpRequest req = rb.POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200) {
                System.out.println("   [" + modelName + "] HTTP " + resp.statusCode());
                return "";
            }

            return extractText(resp.body());

        } catch (Exception e) {
            System.out.println("   [" + modelName + "] DEAD: " + e.getMessage());
            return "";
        }
    }

    private String extractText(String json) {
        try {
            JSONObject obj = new JSONObject(json);

            // Ollama format
            if (obj.has("response")) {
                return obj.getString("response").trim();
            }

            // OpenAI format
            if (obj.has("choices")) {
                JSONArray choices = obj.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject first = choices.getJSONObject(0);
                    if (first.has("message")) {
                        return first.getJSONObject("message").getString("content").trim();
                    }
                    if (first.has("text")) {
                        return first.getString("text").trim();
                    }
                }
            }

            return obj.toString();
        } catch (Exception e) {
            return json;
        }
    }
}
