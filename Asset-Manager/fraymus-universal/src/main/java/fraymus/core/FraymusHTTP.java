package fraymus.core;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 📡 FRAYMUS HTTP (Internalized)
 * Zero-dependency Networking.
 * "We speak raw TCP/IP wrapped in SSL."
 */
public class FraymusHTTP {

    public static String post(String urlStr, String jsonBody) {
        return request("POST", urlStr, jsonBody);
    }

    public static String get(String urlStr) {
        return request("GET", urlStr, null);
    }

    private static String request(String method, String urlStr, String body) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "Fraymus/1.0 (Sovereign)");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(15000);

            if (body != null) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.getBytes(StandardCharsets.UTF_8));
                }
            }

            int status = conn.getResponseCode();
            InputStream is = (status > 299) ? conn.getErrorStream() : conn.getInputStream();
            
            if (is == null) return "ERR: " + status;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) response.append(line);
                return response.toString();
            }

        } catch (Exception e) {
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}
