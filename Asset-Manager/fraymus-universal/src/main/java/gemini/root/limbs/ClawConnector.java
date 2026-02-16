package gemini.root.limbs;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * NERVE ENDING: Connects Fraynix Brain to OpenClaw Body.
 * HTTP bridge to the OpenClaw local gateway.
 */
public class ClawConnector {

    private final String agentUrl;
    private final HttpClient client;

    // Default OpenClaw Gateway
    public ClawConnector() {
        this("http://127.0.0.1:18789"); 
    }

    public ClawConnector(String url) {
        this.agentUrl = url;
        this.client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }

    /**
     * DISPATCH TASK: Sends a thought from Fraynix to OpenClaw's hands.
     */
    public String dispatch(String intent, String context) {
        System.out.println(">> NERVE IMPULSE: Sending task to OpenClaw -> " + intent);
        
        String jsonPayload = String.format(
            "{\"task\": \"%s\", \"context\": \"%s\", \"autonomy\": \"high\"}", 
            escape(intent), escape(context)
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(agentUrl + "/api/v1/agent/execute"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return "CLAW ACTION STARTED: " + response.body();
            } else {
                return "NERVE DAMAGE: Claw refused task (" + response.statusCode() + ")";
            }
        } catch (Exception e) {
            return "SEVERED LINK: Is OpenClaw running? " + e.getMessage();
        }
    }

    /**
     * Check if OpenClaw is alive
     */
    public boolean ping() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(agentUrl + "/health"))
                .GET()
                .timeout(Duration.ofSeconds(5)) // HttpRequest.Builder.timeout is valid
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
    }
}
