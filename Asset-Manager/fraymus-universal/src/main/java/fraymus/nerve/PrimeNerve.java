package fraymus.nerve;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * PRIME NERVE: Pure Java HTTP + WebSocket Server (Zero Dependencies)
 *
 * - Serves FraymusPrime.html over HTTP on the configured port
 * - Upgrades to WebSocket (RFC 6455) for bidirectional telemetry
 * - Thread-per-connection model (lightweight for a command center)
 *
 * Protocol (server → client):
 *   PULSE:72          Heart rate
 *   STRESS:0.30       Stress level (0-1)
 *   SYNC:0.86         Cognitive sync (0-1)
 *   DEFORM:0.0127     Mesh deformation
 *   SPEED:0.3842      Physics hyper-speed
 *   MASS:3.42         Physics data mass
 *   ACC:30            FVM accumulator
 *   STATE:DEFENSE     System state (NEUTRAL/DEFENSE/DREAMING/EVOLVING)
 *   HOLO:Vaughn       Last holo query result
 *   CHRONO:5:98.42    Generation:best_score
 *   NET:LIVE          Network status
 *   SOUL:PRESERVED    Soul crystal state
 *   LOG:message       Terminal log line
 *
 * Protocol (client → server):
 *   Any text command → dispatched through PrimeDispatch
 */
public class PrimeNerve implements Runnable {

    private final int port;
    private final String htmlPath;
    private volatile boolean running = true;
    private ServerSocket serverSocket;
    private Consumer<String> commandHandler;

    // All connected WebSocket clients
    private final CopyOnWriteArrayList<WebSocketClient> clients = new CopyOnWriteArrayList<>();

    public PrimeNerve(int port, String htmlPath) {
        this.port = port;
        this.htmlPath = htmlPath;
    }

    public void setCommandHandler(Consumer<String> handler) {
        this.commandHandler = handler;
    }

    /** Broadcast a message to ALL connected WebSocket clients */
    public void broadcast(String message) {
        for (WebSocketClient client : clients) {
            try {
                client.send(message);
            } catch (Exception e) {
                clients.remove(client);
            }
        }
    }

    /** Send a message to a specific client */
    public void send(WebSocketClient client, String message) {
        try {
            client.send(message);
        } catch (Exception e) {
            clients.remove(client);
        }
    }

    public int getClientCount() {
        return clients.size();
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            System.out.println("🌐 [NERVE] PrimeNerve listening on http://localhost:" + port);
            System.out.println("   Dashboard: http://localhost:" + port + "/");

            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    Thread handler = new Thread(() -> handleConnection(socket), "Nerve-" + socket.getPort());
                    handler.setDaemon(true);
                    handler.start();
                } catch (SocketException e) {
                    if (running) System.err.println("[NERVE] Accept error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[NERVE] Failed to start: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CONNECTION HANDLER (HTTP or WebSocket upgrade)
    // ═══════════════════════════════════════════════════════════════
    private void handleConnection(Socket socket) {
        try {
            socket.setSoTimeout(30000); // 30s timeout for initial HTTP
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // Read HTTP request
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String requestLine = reader.readLine();
            if (requestLine == null) { socket.close(); return; }

            Map<String, String> headers = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                int colon = line.indexOf(':');
                if (colon > 0) {
                    headers.put(line.substring(0, colon).trim().toLowerCase(), line.substring(colon + 1).trim());
                }
            }

            // WebSocket upgrade?
            String upgrade = headers.getOrDefault("upgrade", "");
            if (upgrade.equalsIgnoreCase("websocket")) {
                handleWebSocketUpgrade(socket, in, out, headers);
                return;
            }

            // Regular HTTP — serve the dashboard
            String[] parts = requestLine.split(" ");
            String path = parts.length > 1 ? parts[1] : "/";

            if (path.equals("/") || path.equals("/index.html") || path.equals("/FraymusPrime.html")) {
                serveFile(out, htmlPath, "text/html");
            } else if (path.equals("/favicon.ico")) {
                sendHttpResponse(out, 204, "No Content", "text/plain", new byte[0]);
            } else {
                sendHttpResponse(out, 404, "Not Found", "text/plain", "404 Not Found".getBytes());
            }

            socket.close();
        } catch (Exception e) {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // HTTP FILE SERVING
    // ═══════════════════════════════════════════════════════════════
    private void serveFile(OutputStream out, String filePath, String contentType) throws IOException {
        byte[] content = Files.readAllBytes(Path.of(filePath));
        sendHttpResponse(out, 200, "OK", contentType, content);
    }

    private void sendHttpResponse(OutputStream out, int code, String status, String contentType, byte[] body) throws IOException {
        String header = "HTTP/1.1 " + code + " " + status + "\r\n"
                + "Content-Type: " + contentType + "; charset=utf-8\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "Connection: close\r\n"
                + "Access-Control-Allow-Origin: *\r\n"
                + "\r\n";
        out.write(header.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    // ═══════════════════════════════════════════════════════════════
    // WEBSOCKET HANDSHAKE (RFC 6455)
    // ═══════════════════════════════════════════════════════════════
    private void handleWebSocketUpgrade(Socket socket, InputStream in, OutputStream out, Map<String, String> headers) throws Exception {
        String key = headers.get("sec-websocket-key");
        if (key == null) { socket.close(); return; }

        // RFC 6455 magic GUID
        String accept = base64Encode(sha1(key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"));

        String response = "HTTP/1.1 101 Switching Protocols\r\n"
                + "Upgrade: websocket\r\n"
                + "Connection: Upgrade\r\n"
                + "Sec-WebSocket-Accept: " + accept + "\r\n"
                + "\r\n";
        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.flush();

        // Upgrade complete — enter WebSocket frame loop
        socket.setSoTimeout(0); // No timeout for WebSocket
        WebSocketClient client = new WebSocketClient(socket, in, out);
        clients.add(client);

        System.out.println("🌐 [NERVE] Dashboard connected. Clients: " + clients.size());
        client.send("LOG:FRAYMUS NERVE: Connected. Telemetry active.");

        try {
            while (running && !socket.isClosed()) {
                String message = client.readFrame();
                if (message == null) break; // Connection closed

                // Dispatch command
                if (commandHandler != null) {
                    try {
                        commandHandler.accept(message);
                    } catch (Exception e) {
                        client.send("LOG:ERROR: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            // Connection lost
        } finally {
            clients.remove(client);
            try { socket.close(); } catch (IOException ignored) {}
            System.out.println("🌐 [NERVE] Dashboard disconnected. Clients: " + clients.size());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // WEBSOCKET CLIENT (Frame read/write per RFC 6455)
    // ═══════════════════════════════════════════════════════════════
    public static class WebSocketClient {
        private final Socket socket;
        private final InputStream in;
        private final OutputStream out;

        WebSocketClient(Socket socket, InputStream in, OutputStream out) {
            this.socket = socket;
            this.in = in;
            this.out = out;
        }

        /** Send a text frame */
        public synchronized void send(String message) throws IOException {
            byte[] payload = message.getBytes(StandardCharsets.UTF_8);
            int len = payload.length;

            // Frame: FIN=1, opcode=0x1 (text)
            out.write(0x81);

            if (len <= 125) {
                out.write(len);
            } else if (len <= 65535) {
                out.write(126);
                out.write((len >> 8) & 0xFF);
                out.write(len & 0xFF);
            } else {
                out.write(127);
                for (int i = 7; i >= 0; i--) {
                    out.write((int) ((len >> (8 * i)) & 0xFF));
                }
            }

            out.write(payload);
            out.flush();
        }

        /** Read a text frame (handles masking from client) */
        public String readFrame() throws IOException {
            int b1 = in.read();
            if (b1 == -1) return null;

            int opcode = b1 & 0x0F;

            // Close frame
            if (opcode == 0x8) return null;

            // Ping → respond with pong
            if (opcode == 0x9) {
                int b2 = in.read();
                int pLen = b2 & 0x7F;
                boolean masked = (b2 & 0x80) != 0;
                byte[] pData = readExact(pLen);
                if (masked) {
                    byte[] mask = readExact(4);
                    for (int i = 0; i < pData.length; i++) pData[i] ^= mask[i % 4];
                }
                // Send pong
                synchronized (this) {
                    out.write(0x8A); // FIN + pong
                    out.write(pData.length);
                    out.write(pData);
                    out.flush();
                }
                return readFrame(); // Continue reading
            }

            int b2 = in.read();
            if (b2 == -1) return null;

            boolean masked = (b2 & 0x80) != 0;
            long payloadLen = b2 & 0x7F;

            if (payloadLen == 126) {
                payloadLen = ((in.read() & 0xFF) << 8) | (in.read() & 0xFF);
            } else if (payloadLen == 127) {
                payloadLen = 0;
                for (int i = 0; i < 8; i++) {
                    payloadLen = (payloadLen << 8) | (in.read() & 0xFF);
                }
            }

            byte[] maskKey = masked ? readExact(4) : null;
            byte[] payload = readExact((int) payloadLen);

            if (masked && maskKey != null) {
                for (int i = 0; i < payload.length; i++) {
                    payload[i] ^= maskKey[i % 4];
                }
            }

            return new String(payload, StandardCharsets.UTF_8);
        }

        private byte[] readExact(int n) throws IOException {
            byte[] buf = new byte[n];
            int read = 0;
            while (read < n) {
                int r = in.read(buf, read, n - read);
                if (r == -1) throw new IOException("EOF");
                read += r;
            }
            return buf;
        }

        public boolean isOpen() {
            return !socket.isClosed();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CRYPTO HELPERS (SHA-1 + Base64 for WebSocket handshake)
    // ═══════════════════════════════════════════════════════════════
    private static byte[] sha1(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    private static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
}
