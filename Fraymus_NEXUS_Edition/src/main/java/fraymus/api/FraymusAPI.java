package fraymus.api;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import fraymus.core.*;
import fraymus.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * FRAYMUS REST API
 * "Every subsystem, one curl away."
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * Exposes all Fraymus subsystems through HTTP endpoints:
 * - Spatial Computing (PhiNode, Gravity, Fusion)
 * - Quantum Systems (Tunneling, Fingerprinting)
 * - Neural Networks (PhiNeuralNet)
 * - Memory Systems (InfiniteMemory, Holographic)
 * - Genome/DNA (QRGenome, FractalDNA)
 * - Ollama Integration
 * - Knowledge Scraping
 * - And 50+ other subsystems
 * 
 * Usage:
 *   FraymusAPI api = new FraymusAPI(8080);
 *   api.start();
 *   
 *   curl http://localhost:8080/api/spatial/status
 *   curl http://localhost:8080/api/universe/export
 *   curl -X POST http://localhost:8080/api/inject -d '{"data":"Java","x":0,"y":0,"z":0}'
 */
public class FraymusAPI {
    
    private HttpServer server;
    private int port;
    private boolean running = false;
    
    // Subsystem references
    private CreativeEngine creativeEngine;
    private ExperimentManager experimentManager;
    
    public FraymusAPI(int port) {
        this.port = port;
    }
    
    public void setCreativeEngine(CreativeEngine engine) {
        this.creativeEngine = engine;
    }
    
    public void setExperimentManager(ExperimentManager mgr) {
        this.experimentManager = mgr;
    }
    
    /**
     * Start the API server
     */
    public void start() throws IOException {
        if (running) {
            System.out.println("⚠️  API already running on port " + port);
            return;
        }
        
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Register all endpoints
        registerEndpoints();
        
        server.setExecutor(null); // Use default executor
        server.start();
        running = true;
        
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ FRAYMUS REST API - ONLINE                                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  Port: " + port);
        System.out.println("  Base URL: http://localhost:" + port);
        System.out.println();
        System.out.println("  Documentation: http://localhost:" + port + "/api/help");
        System.out.println();
    }
    
    /**
     * Stop the API server
     */
    public void stop() {
        if (!running) {
            System.out.println("⚠️  API not running");
            return;
        }
        
        server.stop(0);
        running = false;
        System.out.println("✅ FRAYMUS API STOPPED");
    }
    
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Register all API endpoints
     */
    private void registerEndpoints() {
        // Root & Documentation
        server.createContext("/", new RootHandler());
        server.createContext("/api/help", new HelpHandler());
        
        // Spatial Computing
        server.createContext("/api/spatial/status", new SpatialStatusHandler());
        server.createContext("/api/spatial/start", new SpatialStartHandler());
        server.createContext("/api/spatial/stop", new SpatialStopHandler());
        server.createContext("/api/gravity/status", new GravityStatusHandler());
        server.createContext("/api/fusion/status", new FusionStatusHandler());
        server.createContext("/api/universe/export", new UniverseExportHandler());
        server.createContext("/api/universe/hot", new UniverseHotHandler());
        server.createContext("/api/inject", new InjectHandler());
        
        // System Status
        server.createContext("/api/status", new SystemStatusHandler());
        server.createContext("/api/nodes", new NodesHandler());
        server.createContext("/api/colony", new ColonyHandler());
        
        // Memory Systems
        server.createContext("/api/memory/status", new MemoryStatusHandler());
        server.createContext("/api/memory/export", new MemoryExportHandler());
        server.createContext("/api/memory/search", new MemorySearchHandler());
        
        // Genome/DNA
        server.createContext("/api/genome/status", new GenomeStatusHandler());
        server.createContext("/api/genome/export", new GenomeExportHandler());
        
        // Ollama
        server.createContext("/api/ollama/status", new OllamaStatusHandler());
        server.createContext("/api/ollama/models", new OllamaModelsHandler());
        server.createContext("/api/ollama/ask", new OllamaAskHandler());
        
        // Quantum
        server.createContext("/api/quantum/status", new QuantumStatusHandler());
        server.createContext("/api/quantum/fingerprint", new QuantumFingerprintHandler());
        
        // Export All
        server.createContext("/api/export/all", new ExportAllHandler());
    }
    
    // ========================================================================
    // HANDLERS
    // ========================================================================
    
    /**
     * Root handler - API info
     */
    class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "FRAYMUS REST API v1.0\n\n" +
                "Documentation: http://localhost:" + port + "/api/help\n" +
                "Status: http://localhost:" + port + "/api/status\n" +
                "Export All: http://localhost:" + port + "/api/export/all\n";
            sendResponse(exchange, 200, response);
        }
    }
    
    /**
     * Help handler - Full API documentation
     */
    class HelpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("╔═══════════════════════════════════════════════════════════╗\n");
            sb.append("║ FRAYMUS REST API - DOCUMENTATION                          ║\n");
            sb.append("╚═══════════════════════════════════════════════════════════╝\n\n");
            
            sb.append("=== SPATIAL COMPUTING ===\n");
            sb.append("GET  /api/spatial/status       - Spatial computing status\n");
            sb.append("POST /api/spatial/start        - Start Creative Engine\n");
            sb.append("POST /api/spatial/stop         - Stop Creative Engine\n");
            sb.append("GET  /api/gravity/status       - Gravity engine stats\n");
            sb.append("GET  /api/fusion/status        - Fusion reactor stats\n");
            sb.append("GET  /api/universe/export      - Export entire universe (JSON)\n");
            sb.append("GET  /api/universe/hot         - Hottest nodes\n");
            sb.append("POST /api/inject               - Inject concept (JSON body: {data, x, y, z})\n\n");
            
            sb.append("=== SYSTEM STATUS ===\n");
            sb.append("GET  /api/status               - Overall system status\n");
            sb.append("GET  /api/nodes                - All living entities\n");
            sb.append("GET  /api/colony               - Colony intelligence report\n\n");
            
            sb.append("=== MEMORY SYSTEMS ===\n");
            sb.append("GET  /api/memory/status        - Memory system status\n");
            sb.append("GET  /api/memory/export        - Export all memories (JSON)\n");
            sb.append("GET  /api/memory/search?q=X    - Search memories\n\n");
            
            sb.append("=== GENOME/DNA ===\n");
            sb.append("GET  /api/genome/status        - Genome status\n");
            sb.append("GET  /api/genome/export        - Export genome (JSON)\n\n");
            
            sb.append("=== OLLAMA LLM ===\n");
            sb.append("GET  /api/ollama/status        - Ollama connection status\n");
            sb.append("GET  /api/ollama/models        - Available models\n");
            sb.append("POST /api/ollama/ask           - Ask question (JSON body: {question})\n\n");
            
            sb.append("=== QUANTUM ===\n");
            sb.append("GET  /api/quantum/status       - Quantum systems status\n");
            sb.append("POST /api/quantum/fingerprint  - Generate fingerprint (JSON body: {data})\n\n");
            
            sb.append("=== EXPORT ===\n");
            sb.append("GET  /api/export/all           - Export entire system state (JSON)\n\n");
            
            sb.append("=== EXAMPLES ===\n");
            sb.append("curl http://localhost:" + port + "/api/status\n");
            sb.append("curl http://localhost:" + port + "/api/universe/export\n");
            sb.append("curl -X POST http://localhost:" + port + "/api/spatial/start\n");
            sb.append("curl -X POST http://localhost:" + port + "/api/inject \\\n");
            sb.append("  -H 'Content-Type: application/json' \\\n");
            sb.append("  -d '{\"data\":\"Java\",\"x\":0,\"y\":0,\"z\":0}'\n");
            
            sendResponse(exchange, 200, sb.toString());
        }
    }
    
    /**
     * Spatial status handler
     */
    class SpatialStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, Object> status = new HashMap<>();
            
            if (creativeEngine == null) {
                status.put("status", "NOT_INITIALIZED");
                status.put("running", false);
            } else {
                status.put("status", creativeEngine.isRunning() ? "RUNNING" : "STOPPED");
                status.put("running", creativeEngine.isRunning());
                status.put("universe_size", GravityEngine.getUniverseSize());
                status.put("generation", Lazarus.GEN_COUNT);
            }
            
            sendJsonResponse(exchange, 200, status);
        }
    }
    
    /**
     * Start spatial computing
     */
    class SpatialStartHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            
            if (creativeEngine != null && creativeEngine.isRunning()) {
                sendJsonResponse(exchange, 200, Map.of("status", "already_running"));
                return;
            }
            
            creativeEngine = new CreativeEngine();
            creativeEngine.start();
            
            sendJsonResponse(exchange, 200, Map.of("status", "started", "message", "Creative Engine ONLINE"));
        }
    }
    
    /**
     * Stop spatial computing
     */
    class SpatialStopHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            
            if (creativeEngine == null || !creativeEngine.isRunning()) {
                sendJsonResponse(exchange, 200, Map.of("status", "not_running"));
                return;
            }
            
            creativeEngine.stop();
            sendJsonResponse(exchange, 200, Map.of("status", "stopped"));
        }
    }
    
    /**
     * Gravity engine status
     */
    class GravityStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (creativeEngine == null || !creativeEngine.isRunning()) {
                sendJsonResponse(exchange, 503, Map.of("error", "Creative Engine not running"));
                return;
            }
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("universe_size", GravityEngine.getUniverseSize());
            stats.put("generation", Lazarus.GEN_COUNT);
            stats.put("phi", 1.6180339887);
            stats.put("cooling_rate", 0.99);
            
            sendJsonResponse(exchange, 200, stats);
        }
    }
    
    /**
     * Fusion reactor status
     */
    class FusionStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (creativeEngine == null || !creativeEngine.isRunning()) {
                sendJsonResponse(exchange, 503, Map.of("error", "Creative Engine not running"));
                return;
            }
            
            FusionReactor reactor = creativeEngine.getFusionReactor();
            Map<String, Object> stats = new HashMap<>();
            stats.put("total_fusions", reactor.getTotalFusions());
            stats.put("last_fusion_time", reactor.getLastFusionTime());
            stats.put("critical_mass", 5.0);
            
            sendJsonResponse(exchange, 200, stats);
        }
    }
    
    /**
     * Export entire universe
     */
    class UniverseExportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, Object> export = new HashMap<>();
            export.put("generation", Lazarus.GEN_COUNT);
            export.put("total_nodes", Lazarus.getAllNodes().size());
            
            java.util.List<Map<String, Object>> nodes = new java.util.ArrayList<>();
            for (PhiNode node : Lazarus.getAllNodes()) {
                Map<String, Object> nodeData = new HashMap<>();
                nodeData.put("id", node.id);
                nodeData.put("x", node.x);
                nodeData.put("y", node.y);
                nodeData.put("z", node.z);
                nodeData.put("w", node.w);
                nodeData.put("amplitude", node.a);
                nodeData.put("birth_time", node.birthTime);
                nodes.add(nodeData);
            }
            export.put("nodes", nodes);
            
            sendJsonResponse(exchange, 200, export);
        }
    }
    
    /**
     * Get hottest nodes
     */
    class UniverseHotHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            java.util.List<PhiNode> hottest = Lazarus.getHottestNodes(10);
            java.util.List<Map<String, Object>> nodes = new java.util.ArrayList<>();
            
            for (PhiNode node : hottest) {
                Map<String, Object> nodeData = new HashMap<>();
                nodeData.put("id", node.id);
                nodeData.put("x", node.x);
                nodeData.put("y", node.y);
                nodeData.put("z", node.z);
                nodeData.put("amplitude", node.a);
                nodes.add(nodeData);
            }
            
            sendJsonResponse(exchange, 200, Map.of("hot_nodes", nodes));
        }
    }
    
    /**
     * Inject concept
     */
    class InjectHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            
            if (creativeEngine == null || !creativeEngine.isRunning()) {
                sendJsonResponse(exchange, 503, Map.of("error", "Creative Engine not running"));
                return;
            }
            
            // Parse JSON body (simplified - would use proper JSON library in production)
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            
            // Simple JSON parsing (replace with proper library)
            try {
                String data = extractJsonValue(body, "data");
                int x = Integer.parseInt(extractJsonValue(body, "x"));
                int y = Integer.parseInt(extractJsonValue(body, "y"));
                int z = Integer.parseInt(extractJsonValue(body, "z"));
                
                PhiSuit<String> concept = creativeEngine.inject(data, x, y, z);
                
                Map<String, Object> response = new HashMap<>();
                response.put("status", "injected");
                response.put("id", concept.id);
                response.put("data", data);
                response.put("x", x);
                response.put("y", y);
                response.put("z", z);
                response.put("amplitude", concept.a);
                
                sendJsonResponse(exchange, 200, response);
            } catch (Exception e) {
                sendJsonResponse(exchange, 400, Map.of("error", "Invalid JSON: " + e.getMessage()));
            }
        }
    }
    
    // Placeholder handlers for other endpoints
    class SystemStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, Object> status = new HashMap<>();
            status.put("system", "FRAYMUS NEXUS v2.0");
            status.put("status", "ONLINE");
            status.put("uptime_ms", System.currentTimeMillis());
            sendJsonResponse(exchange, 200, status);
        }
    }
    
    class NodesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, 200, Map.of("nodes", "TODO: Implement PhiWorld node export"));
        }
    }
    
    class ColonyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, 200, Map.of("colony", "TODO: Implement colony intelligence export"));
        }
    }
    
    class MemoryStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, 200, Map.of("memory", "TODO: Implement memory status"));
        }
    }
    
    class MemoryExportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, 200, Map.of("memory_export", "TODO: Implement memory export"));
        }
    }
    
    class MemorySearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, 200, Map.of("search", "TODO: Implement memory search"));
        }
    }
    
    class GenomeStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, 200, Map.of("genome", "TODO: Implement genome status"));
        }
    }
    
    class GenomeExportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, 200, Map.of("genome_export", "TODO: Implement genome export"));
        }
    }
    
    class OllamaStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, 200, Map.of("ollama", "TODO: Implement Ollama status"));
        }
    }
    
    class OllamaModelsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, 200, Map.of("models", "TODO: Implement model list"));
        }
    }
    
    class OllamaAskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, 200, Map.of("answer", "TODO: Implement Ollama ask"));
        }
    }
    
    class QuantumStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, 200, Map.of("quantum", "TODO: Implement quantum status"));
        }
    }
    
    class QuantumFingerprintHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendJsonResponse(exchange, 200, Map.of("fingerprint", "TODO: Implement fingerprint"));
        }
    }
    
    class ExportAllHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, Object> export = new HashMap<>();
            export.put("system", "FRAYMUS NEXUS v2.0");
            export.put("timestamp", System.currentTimeMillis());
            export.put("spatial_computing", "TODO");
            export.put("memory", "TODO");
            export.put("genome", "TODO");
            export.put("quantum", "TODO");
            sendJsonResponse(exchange, 200, export);
        }
    }
    
    // ========================================================================
    // UTILITIES
    // ========================================================================
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
    
    private void sendJsonResponse(HttpExchange exchange, int statusCode, Map<String, Object> data) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        String json = mapToJson(data);
        sendResponse(exchange, statusCode, json);
    }
    
    private String mapToJson(Map<String, Object> map) {
        // Simple JSON serialization (replace with proper library in production)
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                sb.append(value);
            } else if (value instanceof java.util.List) {
                sb.append(listToJson((java.util.List<?>) value));
            } else {
                sb.append("\"").append(value).append("\"");
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    private String listToJson(java.util.List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : list) {
            if (!first) sb.append(",");
            first = false;
            if (item instanceof Map) {
                sb.append(mapToJson((Map<String, Object>) item));
            } else if (item instanceof String) {
                sb.append("\"").append(item).append("\"");
            } else {
                sb.append(item);
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    private String extractJsonValue(String json, String key) {
        // Simple JSON value extraction (replace with proper library)
        String pattern = "\"" + key + "\"\\s*:\\s*\"?([^,}\"]+)\"?";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1).trim();
        }
        return "";
    }
}
