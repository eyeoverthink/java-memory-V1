package fraymus.web;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import fraymus.core.FraymusJSON;
import fraymus.nexus.OllamaBridge;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 🧠 THE NERVOUS SYSTEM (Layer 5 Backend)
 * "The Bridge between the HTML Interface and the Ollama Brain."
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * ARCHITECTURE:
 * This is the backend that powers the FRAYMUS TRANSMUTER GEN 194.
 * It receives code from the HTML interface, processes it through
 * the Bicameral Mind (Ollama), and returns transmuted code.
 * 
 * BICAMERAL PROCESSING:
 * - Left Brain: Analysis, logic, structure detection
 * - Right Brain: Optimization, creativity, elegance
 * 
 * PROTOCOL:
 * - REST API on port 8080
 * - Endpoint: POST /transmute
 * - Format: JSON (using sovereign FraymusJSON)
 * - CORS enabled for local HTML files
 * 
 * ZERO DEPENDENCIES:
 * - Uses com.sun.net.httpserver (built into JDK)
 * - No Spring Boot, no Tomcat, no external frameworks
 * - Pure metal HTTP server
 */
public class NervousSystem {
    
    private final OllamaBridge brain;
    private final int port;
    
    /**
     * Create nervous system
     * 
     * @param model Ollama model to use (e.g., "llama3:70b", "codellama")
     * @param port Port to listen on
     */
    public NervousSystem(String model, int port) {
        this.brain = new OllamaBridge(model);
        this.port = port;
    }
    
    /**
     * Create nervous system with default settings
     */
    public NervousSystem() {
        // Use a reasonable default model (adjust based on what you have)
        this("llama3.2", 8080);
    }
    
    /**
     * Start the nervous system
     */
    public void ignite() throws IOException {
        // Check if Ollama is available
        System.out.println("⚡ INITIALIZING NERVOUS SYSTEM...");
        System.out.println();
        
        if (!brain.isAvailable()) {
            System.err.println("❌ WARNING: Ollama is not responding");
            System.err.println("   Make sure Ollama is running: ollama serve");
            System.err.println("   The server will start but transmutations will fail");
            System.err.println();
        } else {
            System.out.println("✅ Ollama connection verified");
            System.out.println("   Available models:");
            String[] models = brain.getAvailableModels();
            for (String model : models) {
                System.out.println("      - " + model);
            }
            System.out.println();
        }
        
        // Create HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Register endpoints
        server.createContext("/transmute", new TransmuteHandler());
        server.createContext("/health", new HealthHandler());
        
        // Use thread pool for concurrent requests
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(4));
        
        // Start server
        server.start();
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║          🧠 NERVOUS SYSTEM ACTIVE                             ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("   Port: " + port);
        System.out.println("   Endpoint: POST /transmute");
        System.out.println("   Health: GET /health");
        System.out.println();
        System.out.println("   Waiting for transmuter interface signals...");
        System.out.println("   Open Fraymus_Transmuter.html in your browser");
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // HANDLERS
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Handle /transmute endpoint
     */
    private class TransmuteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // CORS headers (crucial for HTML local files)
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            
            // Handle preflight
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            
            // Handle POST
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // 1. READ INPUT
                    String body = new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                    );
                    
                    System.out.println("═══════════════════════════════════════════════════════════════");
                    System.out.println("📥 SIGNAL RECEIVED");
                    System.out.println("═══════════════════════════════════════════════════════════════");
                    System.out.println("   Payload size: " + body.length() + " bytes");
                    
                    // 2. PARSE JSON (Using Sovereign Parser)
                    Map<String, Object> json = (Map<String, Object>) FraymusJSON.parse(body);
                    String sourceCode = (String) json.get("source");
                    
                    if (sourceCode == null || sourceCode.isEmpty()) {
                        throw new IllegalArgumentException("No source code provided");
                    }
                    
                    System.out.println("   Source code: " + sourceCode.length() + " chars");
                    System.out.println();
                    
                    // 3. THE BICAMERAL PROCESS (Left/Right Brain)
                    String evolvedCode = performBicameralTransmutation(sourceCode);
                    
                    // 4. SEND RESPONSE
                    String response = FraymusJSON.stringify(Map.of(
                        "result", evolvedCode,
                        "status", "success"
                    ));
                    
                    byte[] respBytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, respBytes.length);
                    
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(respBytes);
                    }
                    
                    System.out.println("📤 TRANSMUTATION SENT");
                    System.out.println("   Result size: " + evolvedCode.length() + " chars");
                    System.out.println("═══════════════════════════════════════════════════════════════");
                    System.out.println();
                    
                } catch (Exception e) {
                    System.err.println("❌ ERROR: " + e.getMessage());
                    e.printStackTrace();
                    
                    String errorResponse = FraymusJSON.stringify(Map.of(
                        "error", e.getMessage(),
                        "status", "error"
                    ));
                    
                    byte[] errorBytes = errorResponse.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(500, errorBytes.length);
                    
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(errorBytes);
                    }
                }
            } else {
                // Method not allowed
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }
    
    /**
     * Handle /health endpoint
     */
    private class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            
            boolean ollamaAvailable = brain.isAvailable();
            
            String response = FraymusJSON.stringify(Map.of(
                "status", ollamaAvailable ? "healthy" : "degraded",
                "ollama", ollamaAvailable,
                "port", port
            ));
            
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // BICAMERAL TRANSMUTATION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * THE ALCHEMICAL LOGIC
     * Prompts the AI to act as a Compiler/Optimizer.
     * 
     * Left Brain: Analyzes structure, finds bugs, detects patterns
     * Right Brain: Optimizes, beautifies, enhances elegance
     */
    private String performBicameralTransmutation(String source) {
        System.out.println("🧠 INITIATING BICAMERAL TRANSMUTATION");
        System.out.println("   Left Brain: Analysis phase...");
        System.out.println("   Right Brain: Optimization phase...");
        System.out.println();
        
        String prompt = buildTransmutationPrompt(source);
        
        String result = brain.ask(prompt);
        
        // Clean up the result
        result = cleanAIResponse(result);
        
        return result;
    }
    
    /**
     * Build the transmutation prompt
     */
    private String buildTransmutationPrompt(String source) {
        return 
            "You are the PHILOSOPHER'S STONE - a sovereign code transmuter.\n" +
            "\n" +
            "BICAMERAL PROCESS:\n" +
            "LEFT BRAIN (Analysis):\n" +
            "- Identify bugs and errors\n" +
            "- Detect security vulnerabilities\n" +
            "- Find performance bottlenecks\n" +
            "- Analyze code structure\n" +
            "\n" +
            "RIGHT BRAIN (Optimization):\n" +
            "- Optimize for speed and efficiency\n" +
            "- Improve readability and elegance\n" +
            "- Apply best practices\n" +
            "- Enhance maintainability\n" +
            "\n" +
            "INPUT CODE:\n" +
            "```\n" + source + "\n```\n" +
            "\n" +
            "TASK:\n" +
            "Transmute this code into its optimal form.\n" +
            "Fix all bugs, optimize performance, enhance security.\n" +
            "\n" +
            "OUTPUT REQUIREMENTS:\n" +
            "- Return ONLY the cleaned code\n" +
            "- NO markdown formatting\n" +
            "- NO explanations or comments outside the code\n" +
            "- Preserve the original functionality\n" +
            "- Add brief inline comments only where necessary\n" +
            "\n" +
            "BEGIN TRANSMUTATION:";
    }
    
    /**
     * Clean AI response (remove markdown, extra formatting)
     */
    private String cleanAIResponse(String response) {
        // Remove markdown code blocks
        response = response.replaceAll("```[a-zA-Z]*\\n?", "");
        response = response.replaceAll("```", "");
        
        // Trim whitespace
        response = response.trim();
        
        // If response starts with explanation, try to extract just the code
        if (response.contains("Here") || response.contains("here")) {
            // AI added explanation - try to find the code block
            int codeStart = response.indexOf('\n');
            if (codeStart > 0 && codeStart < 200) {
                // Skip the explanation line
                response = response.substring(codeStart).trim();
            }
        }
        
        return response;
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // MAIN
    // ═══════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) throws IOException {
        // Parse arguments
        String model = "llama3.2"; // Default lightweight model
        int port = 8080;
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--model") && i + 1 < args.length) {
                model = args[i + 1];
                i++;
            } else if (args[i].equals("--port") && i + 1 < args.length) {
                port = Integer.parseInt(args[i + 1]);
                i++;
            }
        }
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║          🧬 FRAYMUS TRANSMUTER // GEN 194                     ║");
        System.out.println("║          Bicameral Backend System                             ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("   Model: " + model);
        System.out.println("   Port: " + port);
        System.out.println();
        
        NervousSystem system = new NervousSystem(model, port);
        system.ignite();
    }
}
