package fraymus.core;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 📡 FRAYMUS HTTP (Sovereign Network)
 * "We speak raw TCP/IP wrapped in SSL."
 * 
 * Zero-dependency HTTP client using pure java.net.
 * No OkHttp. No Apache HttpClient. Just standard library.
 * 
 * SOVEREIGNTY PRINCIPLE:
 * External HTTP libraries add bloat and attack surface.
 * java.net.HttpURLConnection is verbose but sovereign.
 * We wrap it for usability.
 * 
 * FEATURES:
 * - GET, POST, PUT, DELETE methods
 * - Custom headers
 * - Timeout configuration
 * - Error handling
 * - Response streaming
 * 
 * USAGE:
 *   String response = FraymusHTTP.post("http://api.example.com", jsonBody);
 *   String data = FraymusHTTP.get("http://api.example.com/data");
 */
public class FraymusHTTP {
    
    // Default configuration
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;  // 5 seconds
    private static final int DEFAULT_READ_TIMEOUT = 15000;    // 15 seconds
    private static final String USER_AGENT = "Fraymus/1.0 (Sovereign)";
    
    /**
     * HTTP GET request
     * 
     * @param urlStr URL to request
     * @return Response body as string
     */
    public static String get(String urlStr) {
        return request("GET", urlStr, null, null);
    }
    
    /**
     * HTTP GET request with custom headers
     * 
     * @param urlStr URL to request
     * @param headers Custom headers
     * @return Response body as string
     */
    public static String get(String urlStr, Map<String, String> headers) {
        return request("GET", urlStr, null, headers);
    }
    
    /**
     * HTTP POST request
     * 
     * @param urlStr URL to request
     * @param body Request body (usually JSON)
     * @return Response body as string
     */
    public static String post(String urlStr, String body) {
        return request("POST", urlStr, body, null);
    }
    
    /**
     * HTTP POST request with custom headers
     * 
     * @param urlStr URL to request
     * @param body Request body
     * @param headers Custom headers
     * @return Response body as string
     */
    public static String post(String urlStr, String body, Map<String, String> headers) {
        return request("POST", urlStr, body, headers);
    }
    
    /**
     * HTTP POST request with custom timeout
     * 
     * @param urlStr URL to request
     * @param body Request body
     * @param timeoutMs Timeout in milliseconds
     * @return Response body as string
     */
    public static String post(String urlStr, String body, int timeoutMs) {
        return requestWithTimeout("POST", urlStr, body, null, timeoutMs);
    }
    
    /**
     * HTTP GET request with custom timeout
     * 
     * @param urlStr URL to request
     * @param timeoutMs Timeout in milliseconds
     * @return Response body as string
     */
    public static String get(String urlStr, int timeoutMs) {
        return requestWithTimeout("GET", urlStr, null, null, timeoutMs);
    }
    
    /**
     * HTTP PUT request
     * 
     * @param urlStr URL to request
     * @param body Request body
     * @return Response body as string
     */
    public static String put(String urlStr, String body) {
        return request("PUT", urlStr, body, null);
    }
    
    /**
     * HTTP DELETE request
     * 
     * @param urlStr URL to request
     * @return Response body as string
     */
    public static String delete(String urlStr) {
        return request("DELETE", urlStr, null, null);
    }
    
    /**
     * Core HTTP request method with custom timeout
     * 
     * @param method HTTP method (GET, POST, PUT, DELETE)
     * @param urlStr URL to request
     * @param body Request body (null for GET/DELETE)
     * @param customHeaders Custom headers (null for defaults)
     * @param timeoutMs Timeout in milliseconds
     * @return Response body as string
     */
    private static String requestWithTimeout(String method, String urlStr, String body, Map<String, String> customHeaders, int timeoutMs) {
        HttpURLConnection conn = null;
        
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            
            // Set default headers
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept", "application/json");
            
            // Set custom headers
            if (customHeaders != null) {
                for (Map.Entry<String, String> header : customHeaders.entrySet()) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            
            // Set custom timeout
            conn.setConnectTimeout(timeoutMs);
            conn.setReadTimeout(timeoutMs);
            
            // Write body if present
            if (body != null && !body.isEmpty()) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = body.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            
            // Get response code
            int status = conn.getResponseCode();
            
            // Select input stream based on status
            InputStream is;
            if (status >= 200 && status < 300) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }
            
            // Handle null stream
            if (is == null) {
                return createErrorResponse(status, "No response stream");
            }
            
            // Read response
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            
            return response.toString();
            
        } catch (java.net.ConnectException e) {
            return createErrorResponse(0, "Connection refused: " + e.getMessage());
        } catch (java.net.SocketTimeoutException e) {
            return createErrorResponse(0, "Connection timeout: " + e.getMessage());
        } catch (java.net.UnknownHostException e) {
            return createErrorResponse(0, "Unknown host: " + e.getMessage());
        } catch (IOException e) {
            return createErrorResponse(0, "IO error: " + e.getMessage());
        } catch (Exception e) {
            return createErrorResponse(0, "Unexpected error: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    /**
     * Core HTTP request method
     * 
     * @param method HTTP method (GET, POST, PUT, DELETE)
     * @param urlStr URL to request
     * @param body Request body (null for GET/DELETE)
     * @param customHeaders Custom headers (null for defaults)
     * @return Response body as string
     */
    private static String request(String method, String urlStr, String body, Map<String, String> customHeaders) {
        HttpURLConnection conn = null;
        
        try {
            // Create connection
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            
            // Configure method
            conn.setRequestMethod(method);
            
            // Set default headers
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept", "application/json");
            
            // Set custom headers
            if (customHeaders != null) {
                for (Map.Entry<String, String> header : customHeaders.entrySet()) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            
            // Set timeouts
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            
            // Write body if present
            if (body != null && !body.isEmpty()) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = body.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            
            // Get response code
            int status = conn.getResponseCode();
            
            // Select input stream based on status
            InputStream is;
            if (status >= 200 && status < 300) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }
            
            // Handle null stream
            if (is == null) {
                return createErrorResponse(status, "No response stream");
            }
            
            // Read response
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            
            return response.toString();
            
        } catch (java.net.ConnectException e) {
            return createErrorResponse(0, "Connection refused: " + e.getMessage());
        } catch (java.net.SocketTimeoutException e) {
            return createErrorResponse(0, "Connection timeout: " + e.getMessage());
        } catch (java.net.UnknownHostException e) {
            return createErrorResponse(0, "Unknown host: " + e.getMessage());
        } catch (IOException e) {
            return createErrorResponse(0, "IO error: " + e.getMessage());
        } catch (Exception e) {
            return createErrorResponse(0, "Unexpected error: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    /**
     * Create JSON error response
     */
    private static String createErrorResponse(int status, String message) {
        return String.format("{\"error\": \"%s\", \"status\": %d}", 
                           escapeJson(message), status);
    }
    
    /**
     * Escape JSON special characters
     */
    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Response object with status and body
     */
    public static class Response {
        public final int status;
        public final String body;
        public final boolean success;
        
        public Response(int status, String body) {
            this.status = status;
            this.body = body;
            this.success = (status >= 200 && status < 300);
        }
    }
    
    /**
     * Make request and return Response object with status
     */
    public static Response requestWithStatus(String method, String urlStr, String body) {
        HttpURLConnection conn = null;
        
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            
            if (body != null && !body.isEmpty()) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.getBytes(StandardCharsets.UTF_8));
                }
            }
            
            int status = conn.getResponseCode();
            InputStream is = (status >= 200 && status < 300) 
                ? conn.getInputStream() 
                : conn.getErrorStream();
            
            if (is == null) {
                return new Response(status, "");
            }
            
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            
            return new Response(status, response.toString());
            
        } catch (Exception e) {
            return new Response(0, createErrorResponse(0, e.getMessage()));
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
