package com.eyeoverthink.fraymus.limbs;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.json.JSONObject;

public class ClawConnector {

    private final String agentUrl;
    private final HttpClient client;

    public ClawConnector() {
        this("http://127.0.0.1:8000");
    }

    public ClawConnector(String url) {
        this.agentUrl = url;
        this.client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMinutes(5))
            .build();
    }

    /**
     * DISPATCH: Sends a natural language command to the Agent.
     */
    public String dispatch(String task) {
        System.out.println("🖐️ CLAW: Dispatching task -> \"" + task + "\"");

        JSONObject json = new JSONObject();
        json.put("message", task);
        json.put("stream", false);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(agentUrl + "/api/v1/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject resJson = new JSONObject(response.body());
                return resJson.optString("response", "Task Complete.");
            }
            return "❌ CLAW ERROR: " + response.statusCode() + "\n" + response.body();
        } catch (Exception e) {
            return "❌ SEVERED NERVE: Is OpenClaw running? " + e.getMessage();
        }
    }

    public boolean ping() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(agentUrl + "/health"))
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
