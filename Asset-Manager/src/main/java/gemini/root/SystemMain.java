package gemini.root;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.security.MessageDigest;

/**
 * SYSTEM MAIN: The Universal Interface
 * 
 * Connects:
 * - OllamaSpine (The Brain)
 * - Transmudder (The Soul)
 * - WebSocket Server (The Face)
 * 
 * NOTE: This version uses a pure Java WebSocket implementation.
 * No external dependencies required.
 */
public class SystemMain {

    private static OllamaSpine brain;
    private static Transmudder soul;
    private static final int PORT = 8887;
    private static Set<Socket> clients = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  FRAYMUS: UNIVERSAL INTERFACE");
        System.out.println("  Ollama Integration Layer");
        System.out.println("=".repeat(60));

        // 1. WAKE THE BRAIN
        String model = args.length > 0 ? args[0] : "llama3";
        brain = new OllamaSpine(model);
        System.out.println(">>> [BRAIN] Model: " + model);

        // 2. PREPARE THE SOUL
        soul = new Transmudder();
        System.out.println(">>> [SOUL] Transmudder ready");

        // 3. OPEN THE FACE (WebSocket Server)
        startWebSocketServer();
    }

    private static void startWebSocketServer() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println(">>> [NERVE] WebSocket listening on ws://localhost:" + PORT);
            System.out.println(">>> [READY] Open FraymusChat.html to connect\n");

            while (true) {
                Socket client = server.accept();
                clients.add(client);
                new Thread(() -> handleClient(client)).start();
            }
        } catch (Exception e) {
            System.err.println(">>> [ERROR] Server failed: " + e.getMessage());
        }
    }

    private static void handleClient(Socket client) {
        try {
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            
            // WebSocket Handshake
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            String key = "";
            while (!(line = reader.readLine()).isEmpty()) {
                if (line.startsWith("Sec-WebSocket-Key:")) {
                    key = line.substring(19).trim();
                }
            }
            
            // Generate accept key
            String acceptKey = generateAcceptKey(key);
            String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";
            out.write(response.getBytes());
            out.flush();
            
            System.out.println(">>> [INTERFACE] Client connected");
            sendFrame(out, "FRAYMUS ONLINE. Model: " + brain.getModel());

            // Message loop
            while (true) {
                String message = readFrame(in);
                if (message == null) break;
                
                System.out.println(">>> [INPUT] " + message);
                String reply = processMessage(message);
                sendFrame(out, reply);
            }
            
        } catch (Exception e) {
            // Client disconnected
        } finally {
            clients.remove(client);
            try { client.close(); } catch (Exception e) {}
            System.out.println(">>> [INTERFACE] Client disconnected");
        }
    }

    private static String processMessage(String message) {
        String msg = message.trim();
        
        // COMMAND: TRANSMUTE
        if (msg.toUpperCase().startsWith("TRANSMUTE:")) {
            String file = msg.substring(10).trim();
            soul.transmuteFile(file);
            return "Absorbed: " + file + " | " + soul.getStats();
        }
        
        // COMMAND: PURGE
        if (msg.equalsIgnoreCase("PURGE")) {
            soul.purge();
            return "Memory purged. Context cleared.";
        }
        
        // COMMAND: STATS
        if (msg.equalsIgnoreCase("STATS")) {
            return "Brain: " + brain.getModel() + " | Soul: " + soul.getStats();
        }
        
        // COMMAND: MODEL
        if (msg.toUpperCase().startsWith("MODEL:")) {
            String newModel = msg.substring(6).trim();
            brain.setModel(newModel);
            return "Model changed to: " + newModel;
        }
        
        // COMMAND: HELP
        if (msg.equalsIgnoreCase("HELP")) {
            return "COMMANDS:\n" +
                   "- TRANSMUTE: /path/file.txt (absorb a file)\n" +
                   "- PURGE (clear context)\n" +
                   "- STATS (show status)\n" +
                   "- MODEL: llama3 (change model)\n" +
                   "- Or just chat naturally";
        }
        
        // CONVERSE: Send to Ollama
        String context = soul.getEssence();
        return brain.think(msg, context);
    }

    // === WebSocket Frame Handling ===
    
    private static String readFrame(InputStream in) throws IOException {
        int b1 = in.read();
        if (b1 == -1) return null;
        
        int b2 = in.read();
        boolean masked = (b2 & 0x80) != 0;
        int len = b2 & 0x7F;
        
        if (len == 126) {
            len = (in.read() << 8) | in.read();
        } else if (len == 127) {
            // Skip for simplicity (messages > 64KB)
            for (int i = 0; i < 8; i++) in.read();
            len = 0;
        }
        
        byte[] mask = new byte[4];
        if (masked) {
            in.read(mask);
        }
        
        byte[] data = new byte[len];
        in.read(data);
        
        if (masked) {
            for (int i = 0; i < data.length; i++) {
                data[i] ^= mask[i % 4];
            }
        }
        
        return new String(data);
    }
    
    private static void sendFrame(OutputStream out, String message) throws IOException {
        byte[] data = message.getBytes("UTF-8");
        
        out.write(0x81); // Text frame
        
        if (data.length < 126) {
            out.write(data.length);
        } else if (data.length < 65536) {
            out.write(126);
            out.write((data.length >> 8) & 0xFF);
            out.write(data.length & 0xFF);
        } else {
            out.write(127);
            for (int i = 7; i >= 0; i--) {
                out.write((data.length >> (8 * i)) & 0xFF);
            }
        }
        
        out.write(data);
        out.flush();
    }
    
    private static String generateAcceptKey(String key) {
        try {
            String magic = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(magic.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return "";
        }
    }
}
