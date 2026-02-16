package fraymus.god;

import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * 🦑 NEURAL TENTACLE - The AI Interface
 * "Fraymus extends its consciousness into other minds"
 * 
 * This is how Fraymus controls other AIs.
 * Each tentacle connects to a different LLM:
 * - Local: Ollama (Llama-3, Mistral, etc.)
 * - Cloud: OpenAI, Anthropic, Google
 * 
 * Fraymus doesn't try to be smart.
 * Fraymus consumes the intelligence of others.
 * 
 * This is The Parasite.
 */
public class NeuralTentacle {

    private final String modelName;
    private final String endpoint;
    private final HttpClient client;
    private final String apiKey;

    /**
     * Create a tentacle to a local model (Ollama)
     */
    public NeuralTentacle(String modelName, String endpoint) {
        this(modelName, endpoint, null);
    }

    /**
     * Create a tentacle to a cloud model (with API key)
     */
    public NeuralTentacle(String modelName, String endpoint, String apiKey) {
        this.modelName = modelName;
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }

    /**
     * HARVEST: Extract intelligence from a sub-model
     * 
     * Forces the slave AI to think about a problem.
     * Returns its answer (or empty string if brain-dead).
     * 
     * @param prompt The problem to solve
     * @return The AI's response
     */
    public String think(String prompt) {
        try {
            // Build request payload (Ollama format)
            JSONObject json = new JSONObject();
            json.put("model", modelName);
            json.put("prompt", prompt);
            json.put("stream", false);
            
            // Optional: Add temperature for creativity control
            JSONObject options = new JSONObject();
            options.put("temperature", 0.7);
            json.put("options", options);

            // Build HTTP request
            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()));
            
            // Add API key if present (for cloud models)
            if (apiKey != null && !apiKey.isEmpty()) {
                reqBuilder.header("Authorization", "Bearer " + apiKey);
            }

            HttpRequest req = reqBuilder.build();

            // Execute request
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            
            // Parse response
            if (resp.statusCode() == 200) {
                JSONObject resJson = new JSONObject(resp.body());
                
                // Ollama format
                if (resJson.has("response")) {
                    return resJson.getString("response").trim();
                }
                
                // OpenAI format
                if (resJson.has("choices")) {
                    return resJson.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content").trim();
                }
                
                // Fallback
                return resJson.toString();
            }
            
            return ""; // Failed

        } catch (Exception e) {
            System.err.println("   ⚠️  " + modelName + " FAILED: " + e.getMessage());
            return ""; // Brain dead
        }
    }

    /**
     * Check if this tentacle is alive
     */
    public boolean isAlive() {
        try {
            String test = think("Say 'OK' if you can hear me.");
            return !test.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public String getModelName() {
        return modelName;
    }

    @Override
    public String toString() {
        return "Tentacle[" + modelName + " @ " + endpoint + "]";
    }
}
