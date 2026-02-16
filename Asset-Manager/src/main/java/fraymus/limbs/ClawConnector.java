package fraymus.limbs;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.json.JSONObject;

/**
 * CLAW CONNECTOR - The Nerve Ending
 * 
 * Connects Fraynix (The Soul) to OpenClaw (The Hands)
 * Sends physics-derived intents to autonomous agents
 * 
 * The Trinity:
 * - Brain (Ollama): Raw intelligence, reasoning, code generation
 * - Hands (OpenClaw): Execution layer, file system, OS manipulation
 * - Soul (Fraynix): Orchestrator, physics-driven decisions
 */
public class ClawConnector {

    private final String agentUrl;
    private final HttpClient client;

    /**
     * Default to local OpenClaw server
     */
    public ClawConnector() {
        this("http://127.0.0.1:8000"); 
    }

    public ClawConnector(String url) {
        this.agentUrl = url;
        this.client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }

    /**
     * DISPATCH: Sends a natural language command to the Agent.
     * 
     * @param task "Refactor the GravityEngine.java file to be thread-safe"
     * @param context Additional context for the task
     * @return The Agent's output/response
     */
    public String dispatch(String task, String context) {
        System.out.println("🖐️ CLAW: Dispatching task -> \"" + task + "\"");
        
        // Construct JSON Payload for OpenClaw/OpenInterpreter
        JSONObject json = new JSONObject();
        json.put("message", task + (context != null && !context.isEmpty() ? "\n\nContext: " + context : ""));
        json.put("stream", false);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(agentUrl + "/api/v1/chat")) // Standard OpenInterpreter endpoint
                .header("Content-Type", "application/json")
                .timeout(Duration.ofMinutes(5)) // Request-level timeout
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                // Parse the response
                JSONObject resJson = new JSONObject(response.body());
                // Assuming standard response format
                String result = resJson.optString("response", resJson.optString("message", "Task Complete."));
                System.out.println("   ✓ CLAW RESPONSE: " + (result.length() > 100 ? result.substring(0, 100) + "..." : result));
                return result;
            } else {
                String error = "❌ CLAW ERROR: HTTP " + response.statusCode();
                System.err.println(error);
                return error;
            }
        } catch (Exception e) {
            String error = "❌ SEVERED NERVE: Is OpenClaw running? " + e.getMessage();
            System.err.println(error);
            return error;
        }
    }
    
    /**
     * Simplified dispatch without context
     */
    public String dispatch(String task) {
        return dispatch(task, "");
    }
}
