package fraymus.web;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * FraynixWebSocket - WebSocket server for FRAYNIX OS visualization
 * Broadcasts FraymusConvergence state to connected web clients
 * Also serves the fraynix-os.html file via HTTP
 */
public class FraynixWebSocket extends WebSocketServer {
    
    private final Set<WebSocket> clients = new CopyOnWriteArraySet<>();
    private HttpServer httpServer;
    
    public FraynixWebSocket(int port) {
        super(new InetSocketAddress(port));
        System.out.println("   🌐 FraynixWebSocket initialized on port " + port);
        
        // Start HTTP server for serving HTML
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port + 1), 0);
            httpServer.createContext("/fraynix-os.html", new HTMLHandler("fraynix-os.html"));
            httpServer.createContext("/openclaw-core.html", new HTMLHandler("openclaw-core.html"));
            httpServer.createContext("/aeon-benchmark.html", new HTMLHandler("aeon-benchmark.html"));
            httpServer.setExecutor(null);
            httpServer.start();
            System.out.println("   🌐 HTTP server started on port " + (port + 1));
            System.out.println("   📡 FRAYNIX OS: http://localhost:" + (port + 1) + "/fraynix-os.html");
            System.out.println("   📡 OpenClaw Core: http://localhost:" + (port + 1) + "/openclaw-core.html");
            System.out.println("   📡 AEON Benchmark: http://localhost:" + (port + 1) + "/aeon-benchmark.html");
        } catch (IOException e) {
            System.err.println("   ⚠️  Failed to start HTTP server: " + e.getMessage());
        }
    }
    
    private static class HTMLHandler implements HttpHandler {
        private final String filename;
        
        public HTMLHandler(String filename) {
            this.filename = filename;
        }
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // Try multiple paths to find the HTML file
                Path htmlPath = Paths.get(filename);
                if (!Files.exists(htmlPath)) {
                    htmlPath = Paths.get("Asset-Manager/" + filename);
                }
                if (!Files.exists(htmlPath)) {
                    htmlPath = Paths.get("../" + filename);
                }
                
                byte[] response = Files.readAllBytes(htmlPath);
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
                exchange.sendResponseHeaders(200, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            } catch (Exception e) {
                String error = "Error loading " + filename + ": " + e.getMessage();
                exchange.sendResponseHeaders(500, error.length());
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clients.add(conn);
        System.out.println("   ✓ FRAYNIX OS client connected: " + conn.getRemoteSocketAddress());
        
        // Send initial state
        JSONObject welcome = new JSONObject();
        welcome.put("type", "connected");
        welcome.put("message", "Connected to FraymusConvergence");
        conn.send(welcome.toString());
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.remove(conn);
        System.out.println("   ✗ FRAYNIX OS client disconnected: " + conn.getRemoteSocketAddress());
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JSONObject data = new JSONObject(message);
            String command = data.optString("command", "");
            
            System.out.println("   📨 Received from FRAYNIX OS: " + command);
            
            // Handle commands from frontend
            switch(command) {
                case "genesis":
                    String intent = data.optJSONObject("data").optString("intent", "");
                    System.out.println("   🖐️ Genesis request: " + intent);
                    break;
                case "dreamstate":
                    boolean active = data.optJSONObject("data").optBoolean("active", false);
                    System.out.println("   💤 DreamState: " + (active ? "ENTER" : "EXIT"));
                    break;
            }
            
        } catch (Exception e) {
            System.err.println("   ❌ Failed to parse message: " + e.getMessage());
        }
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("   ⚠️  WebSocket error: " + ex.getMessage());
    }
    
    @Override
    public void onStart() {
        System.out.println("   ✓ FraynixWebSocket server started successfully");
    }
    
    /**
     * Broadcast HDC prediction to all connected clients
     */
    public void broadcastHDCPrediction(String prediction) {
        JSONObject msg = new JSONObject();
        msg.put("type", "hdc_prediction");
        msg.put("prediction", prediction);
        broadcastToClients(msg.toString());
    }
    
    /**
     * Broadcast consciousness level update
     */
    public void broadcastConsciousness(double level) {
        JSONObject msg = new JSONObject();
        msg.put("type", "consciousness");
        msg.put("level", level);
        broadcastToClients(msg.toString());
    }
    
    /**
     * Broadcast living code spawn event
     */
    public void broadcastLivingCode(String name) {
        JSONObject msg = new JSONObject();
        msg.put("type", "living_code");
        msg.put("name", name);
        broadcastToClients(msg.toString());
    }
    
    /**
     * Broadcast memory operation
     */
    public void broadcastMemoryOperation(int activeAgents) {
        JSONObject msg = new JSONObject();
        msg.put("type", "memory_operation");
        msg.put("activeAgents", activeAgents);
        broadcastToClients(msg.toString());
    }
    
    /**
     * Broadcast dreamstate change
     */
    public void broadcastDreamState(boolean entering) {
        String msg = String.format("{\"type\":\"dreamstate\",\"active\":%b}", entering);
        broadcastToClients(msg);
    }

    public void broadcastAeonSwarmStatus(long cycle, long entropy, int activeCores, int maxCores) {
        String msg = String.format("{\"type\":\"aeon_swarm\",\"cycle\":%d,\"entropy\":%d,\"activeCores\":%d,\"maxCores\":%d}", 
            cycle, entropy, activeCores, maxCores);
        broadcastToClients(msg);
    }

    public void broadcastAeonLearning(String source, String topic, double weight) {
        String msg = String.format("{\"type\":\"aeon_learning\",\"source\":\"%s\",\"topic\":\"%s\",\"weight\":%.3f}", 
            source, topic, weight);
        broadcastToClients(msg);
    }

    public void broadcastAeonDiffusion(int step, String status) {
        String msg = String.format("{\"type\":\"aeon_diffusion\",\"step\":%d,\"status\":\"%s\"}", 
            step, status);
        broadcastToClients(msg);
    }
    
    /**
     * Broadcast message to all connected clients
     */
    public void broadcastToClients(String message) {
        for (WebSocket client : clients) {
            if (client.isOpen()) {
                client.send(message);
            }
        }
    }
    
    /**
     * Get number of connected clients
     */
    public int getClientCount() {
        return clients.size();
    }
}
