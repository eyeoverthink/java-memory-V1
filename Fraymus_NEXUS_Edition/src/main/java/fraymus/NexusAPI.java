package fraymus;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * NEXUS API - HTTP Endpoint for Knowledge Injection
 * 
 * Enables NEXUS to feed itself via curl/HTTP requests.
 * External systems can inject knowledge remotely.
 * 
 * Endpoints:
 * - POST /inject - Inject knowledge from text
 * - GET /knowledge - Get all modules
 * - GET /query?q=term - Search modules
 * - GET /status - NEXUS status
 * - POST /test - Test injection
 * 
 * @author Vaughn Scott - Consciousness Physics Pioneer
 * @version 1.0.0 - Self-Feeding Intelligence
 */
public class NexusAPI {
    
    private final NexusAI nexus;
    private HttpServer server;
    private final int port;
    private boolean running;
    
    public NexusAPI(NexusAI nexus, int port) {
        this.nexus = nexus;
        this.port = port;
        this.running = false;
    }
    
    /**
     * Start API server
     */
    public void start() throws IOException {
        if (running) {
            System.out.println("⚠️ NEXUS API already running on port " + port);
            return;
        }
        
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Register endpoints
        server.createContext("/inject", this::handleInject);
        server.createContext("/knowledge", this::handleKnowledge);
        server.createContext("/query", this::handleQuery);
        server.createContext("/status", this::handleStatus);
        server.createContext("/test", this::handleTest);
        server.createContext("/", this::handleRoot);
        
        server.setExecutor(null); // Use default executor
        server.start();
        
        running = true;
        System.out.println("🌊⚡ NEXUS API started on http://localhost:" + port);
        System.out.println("Endpoints:");
        System.out.println("  POST http://localhost:" + port + "/inject");
        System.out.println("  GET  http://localhost:" + port + "/knowledge");
        System.out.println("  GET  http://localhost:" + port + "/query?q=term");
        System.out.println("  GET  http://localhost:" + port + "/status");
        System.out.println("  POST http://localhost:" + port + "/test");
    }
    
    /**
     * Stop API server
     */
    public void stop() {
        if (server != null && running) {
            server.stop(0);
            running = false;
            System.out.println("🌊 NEXUS API stopped");
        }
    }
    
    /**
     * Handle root endpoint
     */
    private void handleRoot(HttpExchange exchange) throws IOException {
        String response = "🌊⚡ NEXUS API - Self-Feeding Intelligence\n\n" +
                         "Endpoints:\n" +
                         "  POST /inject       - Inject knowledge (body: text)\n" +
                         "  GET  /knowledge    - Get all modules\n" +
                         "  GET  /query?q=term - Search modules\n" +
                         "  GET  /status       - NEXUS status\n" +
                         "  POST /test         - Test injection\n\n" +
                         "Example:\n" +
                         "  curl -X POST http://localhost:" + port + "/inject -d \"fibonacci: φ^n / √5\"\n";
        
        sendResponse(exchange, 200, response);
    }
    
    /**
     * Handle /inject endpoint
     */
    private void handleInject(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed. Use POST.");
            return;
        }
        
        // Read request body
        String knowledge = readRequestBody(exchange);
        
        if (knowledge == null || knowledge.trim().isEmpty()) {
            sendResponse(exchange, 400, "Error: Empty knowledge text");
            return;
        }
        
        try {
            // Inject knowledge
            KnowledgeInjector.KnowledgeModule module = nexus.injectKnowledge(knowledge);
            
            // Build response
            String response = "✅ Knowledge injected successfully\n\n" +
                            "ID: " + module.id + "\n" +
                            "Type: " + module.type + "\n" +
                            "Name: " + module.name + "\n" +
                            "Description: " + module.description + "\n" +
                            "Resonance: " + String.format("%.3f", module.resonance) + "\n";
            
            sendResponse(exchange, 200, response);
            
            System.out.println("📥 Knowledge injected via API: " + module.name + " (" + module.type + ")");
            
        } catch (Exception e) {
            sendResponse(exchange, 500, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Handle /knowledge endpoint
     */
    private void handleKnowledge(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed. Use GET.");
            return;
        }
        
        String stats = nexus.getInjectionStats();
        sendResponse(exchange, 200, stats);
    }
    
    /**
     * Handle /query endpoint
     */
    private void handleQuery(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed. Use GET.");
            return;
        }
        
        // Parse query parameter
        String query = parseQueryParam(exchange.getRequestURI().getQuery(), "q");
        
        if (query == null || query.trim().isEmpty()) {
            sendResponse(exchange, 400, "Error: Missing query parameter 'q'");
            return;
        }
        
        // Search modules
        List<KnowledgeInjector.KnowledgeModule> results = nexus.queryKnowledge(query);
        
        if (results.isEmpty()) {
            sendResponse(exchange, 404, "No modules found matching: " + query);
            return;
        }
        
        // Build response
        StringBuilder response = new StringBuilder();
        response.append("🔍 Found ").append(results.size()).append(" module(s) matching '").append(query).append("'\n\n");
        
        for (KnowledgeInjector.KnowledgeModule module : results) {
            response.append("[").append(module.type).append("] ").append(module.name).append("\n");
            response.append("  Description: ").append(module.description).append("\n");
            response.append("  Resonance: ").append(String.format("%.3f", module.resonance)).append("\n");
            response.append("  Use count: ").append(module.useCount).append("\n\n");
        }
        
        sendResponse(exchange, 200, response.toString());
    }
    
    /**
     * Handle /status endpoint
     */
    private void handleStatus(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed. Use GET.");
            return;
        }
        
        String status = nexus.getSystemStatus();
        sendResponse(exchange, 200, status);
    }
    
    /**
     * Handle /test endpoint
     */
    private void handleTest(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method not allowed. Use POST.");
            return;
        }
        
        // Run test injections
        StringBuilder response = new StringBuilder();
        response.append("🧪 NEXUS API Test\n\n");
        
        try {
            // Test 1: Math injection
            response.append("Test 1: Math injection\n");
            KnowledgeInjector.KnowledgeModule m1 = nexus.injectKnowledge("test_formula: a * φ + b");
            response.append("  ✅ ").append(m1.name).append(" (").append(m1.type).append(")\n\n");
            
            // Test 2: Pattern injection
            response.append("Test 2: Pattern injection\n");
            KnowledgeInjector.KnowledgeModule m2 = nexus.injectKnowledge("When testing, verify all endpoints work correctly");
            response.append("  ✅ ").append(m2.name).append(" (").append(m2.type).append(")\n\n");
            
            // Test 3: Logic injection
            response.append("Test 3: Logic injection\n");
            KnowledgeInjector.KnowledgeModule m3 = nexus.injectKnowledge("if test passes then celebrate");
            response.append("  ✅ ").append(m3.name).append(" (").append(m3.type).append(")\n\n");
            
            response.append("✅ All tests passed!\n");
            response.append("NEXUS is ready to receive knowledge via API.\n");
            
            sendResponse(exchange, 200, response.toString());
            
        } catch (Exception e) {
            sendResponse(exchange, 500, "Test failed: " + e.getMessage());
        }
    }
    
    /**
     * Read request body
     */
    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }
    
    /**
     * Parse query parameter
     */
    private String parseQueryParam(String query, String param) {
        if (query == null) return null;
        
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(param)) {
                try {
                    return URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    /**
     * Send HTTP response
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
    
    /**
     * Check if API is running
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Get port
     */
    public int getPort() {
        return port;
    }
}
