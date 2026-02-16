package gemini.root.viz;

import gemini.root.hyper.HyperTesseract;
import gemini.root.physics.HiveGravityEngine;
import gemini.root.physics.PhiSuit;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * CORTEX SERVER: The Optic Nerve
 * 
 * WebSocket server that broadcasts the Tesseract brain state
 * and physics particles at 30-60 FPS for real-time visualization.
 * 
 * Protocol:
 *   - Connects on port 8888
 *   - Broadcasts JSON frames containing:
 *     - particles: Physics engine particles (x, y, z, energy, category)
 *     - nodes: Active Tesseract nodes (w, x, y, z, charge, hash)
 *     - stats: System statistics (entropy, pulses, etc.)
 */
public class CortexServer extends WebSocketServer {

    private final HiveGravityEngine physics;
    private final HyperTesseract brain;
    
    private volatile boolean broadcasting = false;
    private int frameRate = 30;  // FPS
    private long frameCount = 0;
    
    // Stats
    private double systemEntropy = 0.0;
    private long totalPulses = 0;

    public CortexServer(HiveGravityEngine physics, HyperTesseract brain) {
        super(new InetSocketAddress(8888));
        this.physics = physics;
        this.brain = brain;
        setReuseAddr(true);
    }

    public CortexServer(HiveGravityEngine physics, HyperTesseract brain, int port) {
        super(new InetSocketAddress(port));
        this.physics = physics;
        this.brain = brain;
        setReuseAddr(true);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("👁️ OPTIC NERVE CONNECTED: " + conn.getRemoteSocketAddress());
        
        // Send initial state
        conn.send(buildInitFrame());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("👁️ OPTIC NERVE DISCONNECTED: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Handle commands from visualizer
        if (message.equals("PING")) {
            conn.send("{\"type\":\"PONG\",\"time\":" + System.currentTimeMillis() + "}");
        } else if (message.startsWith("FPS:")) {
            try {
                frameRate = Integer.parseInt(message.substring(4));
                System.out.println("👁️ Frame rate set to " + frameRate + " FPS");
            } catch (NumberFormatException e) {
                // ignore
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("👁️ CORTEX ERROR: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("🚀 CORTEX VISUALIZER SERVER STARTED ON PORT " + getPort());
        System.out.println("   Open fraymus_eye.html in browser to visualize");
        
        // Start broadcast loop
        broadcasting = true;
        new Thread(this::broadcastLoop, "CortexBroadcast").start();
    }

    /**
     * Main broadcast loop - sends frames at specified FPS
     */
    private void broadcastLoop() {
        long frameDelayMs = 1000 / frameRate;
        
        while (broadcasting) {
            if (getConnections().size() > 0) {
                String json = buildFrameJson();
                broadcast(json);
                frameCount++;
            }
            
            try {
                Thread.sleep(frameDelayMs);
            } catch (InterruptedException e) {
                broadcasting = false;
            }
        }
    }

    /**
     * Build initial state frame
     */
    private String buildInitFrame() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"INIT\",");
        sb.append("\"version\":\"2.0\",");
        sb.append("\"dimensions\":{\"w\":4,\"x\":8,\"y\":8,\"z\":8},");
        sb.append("\"totalNodes\":2048,");
        sb.append("\"time\":").append(System.currentTimeMillis());
        sb.append("}");
        return sb.toString();
    }

    /**
     * Build frame JSON with current state
     */
    private String buildFrameJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"FRAME\",");
        sb.append("\"frame\":").append(frameCount).append(",");
        sb.append("\"time\":").append(System.currentTimeMillis()).append(",");
        
        // Stats
        sb.append("\"stats\":{");
        sb.append("\"entropy\":").append(String.format("%.2f", systemEntropy)).append(",");
        sb.append("\"pulses\":").append(totalPulses).append(",");
        sb.append("\"particles\":").append(physics != null ? physics.getParticleCount() : 0).append(",");
        sb.append("\"connections\":").append(getConnections().size());
        sb.append("},");
        
        // Particles from physics engine
        sb.append("\"particles\":[");
        if (physics != null) {
            List<PhiSuit<?>> particles = physics.getParticles();
            boolean first = true;
            for (PhiSuit<?> p : particles) {
                if (!p.active) continue;
                if (!first) sb.append(",");
                first = false;
                
                sb.append("{");
                sb.append("\"id\":\"").append(escapeJson(p.label)).append("\",");
                sb.append("\"x\":").append(String.format("%.1f", p.x)).append(",");
                sb.append("\"y\":").append(String.format("%.1f", p.y)).append(",");
                sb.append("\"z\":").append(String.format("%.1f", p.z)).append(",");
                sb.append("\"e\":").append(String.format("%.1f", p.amplitude + p.heat)).append(",");
                sb.append("\"cat\":\"").append(categorize(p.label)).append("\"");
                sb.append("}");
            }
        }
        sb.append("],");
        
        // Active nodes from Tesseract (sample high-charge nodes only for performance)
        sb.append("\"nodes\":[");
        if (brain != null) {
            boolean first = true;
            int nodeCount = 0;
            
            for (int w = 0; w < 4 && nodeCount < 100; w++) {
                for (int x = 0; x < 8 && nodeCount < 100; x++) {
                    for (int y = 0; y < 8 && nodeCount < 100; y++) {
                        for (int z = 0; z < 8 && nodeCount < 100; z++) {
                            HyperTesseract.Node node = brain.getNode(w, x, y, z);
                            
                            // Only send active nodes (charge > 0.1)
                            if (node.charge > 0.1) {
                                if (!first) sb.append(",");
                                first = false;
                                
                                sb.append("{");
                                sb.append("\"w\":").append(w).append(",");
                                sb.append("\"x\":").append(x).append(",");
                                sb.append("\"y\":").append(y).append(",");
                                sb.append("\"z\":").append(z).append(",");
                                sb.append("\"c\":").append(String.format("%.2f", node.charge)).append(",");
                                sb.append("\"h\":\"").append(node.hash != null ? node.hash.substring(0, Math.min(12, node.hash.length())) : "").append("\"");
                                sb.append("}");
                                
                                nodeCount++;
                            }
                        }
                    }
                }
            }
        }
        sb.append("]");
        
        sb.append("}");
        return sb.toString();
    }

    /**
     * Categorize particle by label
     */
    private String categorize(String label) {
        if (label == null) return "UNKNOWN";
        String upper = label.toUpperCase();
        
        if (upper.contains("CLAW") || upper.contains("AGENT")) return "AGENT";
        if (upper.contains("TASK")) return "TASK";
        if (upper.contains("THOUGHT") || upper.contains("DREAM")) return "THOUGHT";
        if (upper.contains("BRAIN") || upper.contains("CORE")) return "BRAIN";
        if (upper.contains("FILE") || upper.contains("MEMORY")) return "MEMORY";
        if (upper.contains("SIM")) return "SIMULATION";
        
        return "PARTICLE";
    }

    /**
     * Escape JSON string
     */
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ========== PUBLIC API ==========

    /**
     * Push a custom update to all clients
     */
    public void pushUpdate(String json) {
        broadcast(json);
    }

    /**
     * Push an event notification
     */
    public void pushEvent(String eventType, String message) {
        String json = String.format(
            "{\"type\":\"EVENT\",\"event\":\"%s\",\"message\":\"%s\",\"time\":%d}",
            eventType, escapeJson(message), System.currentTimeMillis()
        );
        broadcast(json);
    }

    /**
     * Update system entropy for display
     */
    public void setSystemEntropy(double entropy) {
        this.systemEntropy = entropy;
    }

    /**
     * Update pulse count
     */
    public void setTotalPulses(long pulses) {
        this.totalPulses = pulses;
    }

    /**
     * Set frame rate
     */
    public void setFrameRate(int fps) {
        this.frameRate = Math.max(1, Math.min(60, fps));
    }

    /**
     * Stop broadcasting
     */
    public void shutdown() {
        broadcasting = false;
        try {
            stop(1000);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public long getFrameCount() {
        return frameCount;
    }

    public int getConnectionCount() {
        return getConnections().size();
    }
}
