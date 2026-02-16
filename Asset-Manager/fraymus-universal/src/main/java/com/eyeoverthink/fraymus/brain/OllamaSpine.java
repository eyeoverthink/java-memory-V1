package com.eyeoverthink.fraymus.brain;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Minimal Ollama client (localhost:11434).
 * No extra deps — hand-rolled JSON.
 */
public final class OllamaSpine {
    private final String model;
    private final HttpClient client;

    public OllamaSpine() {
        this("llama3");
    }

    public OllamaSpine(String model) {
        this.model = model;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public String think(String prompt) {
        try {
            String payload = "{"
                    + "\"model\":\"" + escape(model) + "\","
                    + "\"prompt\":\"" + escape(prompt) + "\","
                    + "\"stream\":false"
                    + "}";

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/generate"))
                    .timeout(Duration.ofSeconds(120))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (res.statusCode() != 200) {
                return "[OLLAMA_ERROR] HTTP " + res.statusCode() + " :: " + res.body();
            }

            String body = res.body();
            String marker = "\"response\":\"";
            int i = body.indexOf(marker);
            if (i < 0) return body;
            i += marker.length();
            int j = body.indexOf("\",", i);
            if (j < 0) j = body.lastIndexOf("\"");
            String raw = body.substring(i, j);
            return unescape(raw);

        } catch (IOException | InterruptedException e) {
            return "[OLLAMA_EXCEPTION] " + e.getMessage();
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String unescape(String s) {
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
