package fraymus;

import java.net.InetSocketAddress;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

/**
 * 🧬 FRAYMUS NERVE CENTER
 * Connects the Java "Brain" to the HTML "Eyes".
 * 
 * This WebSocket server broadcasts real-time organism state
 * from Java threads (Lazarus, LogicCircuit, NEXUS organs) to
 * the living HTML arena visualization.
 * 
 * Port: 8887
 * Protocol: WebSocket
 * Format: JSON pulses
 */
public class NerveCenter extends WebSocketServer {

    private static NerveCenter instance = null;
    private int connectedClients = 0;

    public NerveCenter(int port) {
        super(new InetSocketAddress(port));
    }

    public static NerveCenter getInstance() {
        if (instance == null) {
            instance = new NerveCenter(8887);
            new Thread(() -> {
                instance.start();
            }).start();
        }
        return instance;
    }

    @Override
    public void onStart() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   🧬 NERVE CENTER ONLINE                                  ║");
        System.out.println("║   Neural Uplink: ws://localhost:" + getPort() + "                    ║");
        System.out.println("║   Status: AWAITING VISUAL CORTEX CONNECTION              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        connectedClients++;
        System.out.println("[NERVE CENTER] 👁️  Visual cortex connected: " + conn.getRemoteSocketAddress());
        System.out.println("[NERVE CENTER] Active eyes: " + connectedClients);
        
        // Send welcome pulse
        JSONObject welcome = new JSONObject();
        welcome.put("type", "SYSTEM_INIT");
        welcome.put("message", "Neural uplink established");
        welcome.put("timestamp", System.currentTimeMillis());
        conn.send(welcome.toString());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connectedClients--;
        System.out.println("[NERVE CENTER] 👁️  Visual cortex disconnected");
        System.out.println("[NERVE CENTER] Active eyes: " + connectedClients);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Handle incoming messages from HTML (future: user commands)
        try {
            JSONObject msg = new JSONObject(message);
            String type = msg.optString("type", "");
            
            if ("PING".equals(type)) {
                JSONObject pong = new JSONObject();
                pong.put("type", "PONG");
                pong.put("timestamp", System.currentTimeMillis());
                conn.send(pong.toString());
            }
        } catch (Exception e) {
            System.err.println("[NERVE CENTER] Error parsing message: " + e.getMessage());
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("[NERVE CENTER] ⚠️  Error: " + ex.getMessage());
    }

    /**
     * Broadcast organism state to all connected HTML clients
     * 
     * @param threadName - Identifier (e.g., "Lazarus", "LogicCircuit", "Portal")
     * @param entropy - Chaos level (0.0 = calm/blue, 1.0 = chaotic/red)
     * @param momentum - Speed/activity level
     * @param memoryUsage - Size indicator (bytes or normalized 0-1)
     */
    public void broadcastOrganism(String threadName, double entropy, double momentum, double memoryUsage) {
        if (connectedClients == 0) return; // No eyes watching
        
        try {
            JSONObject pulse = new JSONObject();
            pulse.put("type", "ORGANISM_PULSE");
            pulse.put("id", threadName);
            pulse.put("entropy", Math.max(0.0, Math.min(1.0, entropy)));   // Clamp 0-1
            pulse.put("momentum", Math.max(0.0, Math.min(1.0, momentum))); // Clamp 0-1
            pulse.put("size", memoryUsage);
            pulse.put("timestamp", System.currentTimeMillis());
            
            broadcast(pulse.toString());
        } catch (Exception e) {
            System.err.println("[NERVE CENTER] Error broadcasting: " + e.getMessage());
        }
    }

    /**
     * Broadcast system event (birth, death, mutation)
     */
    public void broadcastEvent(String eventType, String message) {
        if (connectedClients == 0) return;
        
        try {
            JSONObject event = new JSONObject();
            event.put("type", "SYSTEM_EVENT");
            event.put("event", eventType);
            event.put("message", message);
            event.put("timestamp", System.currentTimeMillis());
            
            broadcast(event.toString());
        } catch (Exception e) {
            System.err.println("[NERVE CENTER] Error broadcasting event: " + e.getMessage());
        }
    }

    /**
     * Broadcast organism death (triggers explosion effect)
     */
    public void broadcastDeath(String threadName, String cause) {
        if (connectedClients == 0) return;
        
        try {
            JSONObject death = new JSONObject();
            death.put("type", "ORGANISM_DEATH");
            death.put("id", threadName);
            death.put("cause", cause);
            death.put("timestamp", System.currentTimeMillis());
            
            broadcast(death.toString());
            System.out.println("[NERVE CENTER] 💀 Organism death broadcast: " + threadName + " (" + cause + ")");
        } catch (Exception e) {
            System.err.println("[NERVE CENTER] Error broadcasting death: " + e.getMessage());
        }
    }

    public int getConnectedClients() {
        return connectedClients;
    }

    public boolean hasEyes() {
        return connectedClients > 0;
    }
}
