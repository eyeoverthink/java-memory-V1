package fraymus.ui;

import fraymus.senses.BioSymbiosis;
import fraymus.brain.ManifoldBrain;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * UI SERVER: WEB INTERFACE BRIDGE
 * 
 * Serves the HTML UI and provides WebSocket-like updates.
 * 
 * This connects the Java backend to the browser frontend.
 * 
 * Endpoints:
 * - GET / → Serves fraymus_ui.html
 * - GET /api/status → JSON status update
 * - POST /api/pulse → Receive heart rate data
 * 
 * The UI polls /api/status every second for updates.
 */
public class UIServer {
    
    private static final int PORT = 8080;
    private final HttpServer server;
    private final BioSymbiosis bioSymbiosis;
    private final ManifoldBrain brain;
    
    // Shared state for UI
    private volatile double currentHR = 72.0;
    private volatile double currentStress = 0.0;
    private volatile String systemState = "SYNCHRONIZED";
    private volatile List<String> consoleLog = new CopyOnWriteArrayList<>();
    
    public UIServer(BioSymbiosis bioSymbiosis, ManifoldBrain brain) throws IOException {
        this.bioSymbiosis = bioSymbiosis;
        this.brain = brain;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        setupEndpoints();
    }
    
    /**
     * Setup HTTP endpoints
     */
    private void setupEndpoints() {
        // Serve HTML UI
        server.createContext("/", exchange -> {
            try {
                // Read fraymus_ui.html
                Path htmlPath = Paths.get("fraymus_ui.html");
                String html = Files.readString(htmlPath);
                
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, html.length());
                
                OutputStream os = exchange.getResponseBody();
                os.write(html.getBytes());
                os.close();
            } catch (Exception e) {
                String error = "Error loading UI: " + e.getMessage();
                exchange.sendResponseHeaders(500, error.length());
                exchange.getResponseBody().write(error.getBytes());
                exchange.getResponseBody().close();
            }
        });
        
        // API: Get status
        server.createContext("/api/status", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                // Build JSON response
                String json = buildStatusJSON();
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, json.length());
                
                OutputStream os = exchange.getResponseBody();
                os.write(json.getBytes());
                os.close();
            }
        });
        
        // API: Receive pulse data
        server.createContext("/api/pulse", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Read body
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes());
                
                // Parse JSON (simple parsing)
                // Expected: {"hr": 75.0, "gsr": 0.3}
                try {
                    double hr = parseValue(body, "hr");
                    double gsr = parseValue(body, "gsr");
                    
                    // Sync with BioSymbiosis
                    bioSymbiosis.syncPulse(hr, gsr);
                    
                    // Update shared state
                    currentHR = bioSymbiosis.getHeartRate();
                    currentStress = bioSymbiosis.getStressLevel();
                    systemState = bioSymbiosis.getCurrentState().toString();
                    
                    // Log
                    addLog("Pulse synced: " + hr + " BPM");
                    
                    String response = "{\"status\": \"ok\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                    exchange.sendResponseHeaders(200, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                    exchange.getResponseBody().close();
                    
                } catch (Exception e) {
                    String error = "{\"error\": \"" + e.getMessage() + "\"}";
                    exchange.sendResponseHeaders(400, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.getResponseBody().close();
                }
            }
        });
    }
    
    /**
     * Build status JSON for UI
     */
    private String buildStatusJSON() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"hr\": ").append(currentHR).append(",\n");
        json.append("  \"stress\": ").append(String.format("%.2f", currentStress)).append(",\n");
        json.append("  \"state\": \"").append(systemState).append("\",\n");
        json.append("  \"phi\": 1.618,\n");
        json.append("  \"meshColor\": ").append(Arrays.toString(bioSymbiosis.getMeshColor())).append(",\n");
        json.append("  \"meshDistortion\": ").append(bioSymbiosis.getMeshDistortion(System.currentTimeMillis())).append(",\n");
        json.append("  \"console\": [\n");
        
        // Last 10 console messages
        int start = Math.max(0, consoleLog.size() - 10);
        for (int i = start; i < consoleLog.size(); i++) {
            json.append("    \"").append(escapeJSON(consoleLog.get(i))).append("\"");
            if (i < consoleLog.size() - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("  ]\n");
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * Parse value from simple JSON
     */
    private double parseValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*([0-9.]+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Double.parseDouble(m.group(1));
        }
        throw new IllegalArgumentException("Key not found: " + key);
    }
    
    /**
     * Escape JSON string
     */
    private String escapeJSON(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    /**
     * Add console log message
     */
    public void addLog(String message) {
        consoleLog.add(message);
        
        // Keep only last 100 messages
        if (consoleLog.size() > 100) {
            consoleLog.remove(0);
        }
    }
    
    /**
     * Start server
     */
    public void start() {
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        
        System.out.println("\n🌊⚡ UI SERVER STARTED");
        System.out.println("   Port: " + PORT);
        System.out.println("   URL: http://localhost:" + PORT);
        System.out.println("   Status: ONLINE");
        System.out.println();
        
        addLog("FRAYMUS UI Server initialized");
        addLog("Waiting for biological data stream...");
    }
    
    /**
     * Stop server
     */
    public void stop() {
        server.stop(0);
        System.out.println("\n🌊⚡ UI SERVER STOPPED\n");
    }
}
