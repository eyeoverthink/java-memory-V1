package fraymus.senses;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 🎭 AVATAR CORTEX
 * "The nerve that connects the mind to the face."
 *
 * Maintains a persistent socket to FraymusAvatar.py.
 * Streams progressive thoughts, state changes, and breakthroughs
 * as newline-delimited JSON so the avatar reacts in real-time.
 *
 * Message types:
 *   thought     - passive observation (displayed, not spoken)
 *   speak       - important thought (displayed AND spoken via TTS)
 *   breakthrough- critical event (spoken, mood shifts to excited)
 *   answer      - LLM response (spoken)
 *   state       - pure state update (entropy/phi/load change, no text)
 */
public class AvatarCortex {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9876;

    private Socket socket;
    private PrintWriter out;
    private boolean connected = false;

    public AvatarCortex() {
        connect();
    }

    private void connect() {
        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            System.out.println("🎭 [AVATAR] Connected to FraymusAvatar on " + HOST + ":" + PORT);
        } catch (Exception e) {
            connected = false;
            System.out.println("🎭 [AVATAR] Not running (start: python FraymusAvatar.py)");
        }
    }

    /** Check if the avatar is connected. */
    public boolean isConnected() { return connected; }

    /** Reconnect if disconnected. */
    public void reconnect() {
        if (!connected) {
            connect();
        }
    }

    // ── SEND METHODS ──────────────────────────────────────────

    /** Send a passive thought (displayed but not spoken). */
    public void thought(String text) {
        send("thought", text, -1, -1, -1, null);
    }

    /** Send an important thought (displayed AND spoken aloud). */
    public void speak(String text) {
        send("speak", text, -1, -1, -1, null);
    }

    /** Send a breakthrough event (spoken, mood = excited). */
    public void breakthrough(String text) {
        send("breakthrough", text, -1, -1, -1, "excited");
    }

    /** Send an LLM answer (spoken). */
    public void answer(String text) {
        send("answer", text, -1, -1, -1, "thinking");
    }

    /** Send a pure state update (no text, just numbers). */
    public void state(double entropy, double phi, double load, String mood) {
        send("state", "", entropy, phi, load, mood);
    }

    /** Send a thought with full state context. */
    public void thoughtWithState(String text, double entropy, double phi, double load, String mood) {
        send("speak", text, entropy, phi, load, mood);
    }

    // ── CORE SEND ─────────────────────────────────────────────

    private void send(String type, String text, double entropy, double phi, double load, String mood) {
        if (!connected) return;

        try {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"type\":\"").append(escapeJson(type)).append("\"");

            if (text != null && !text.isEmpty()) {
                json.append(",\"text\":\"").append(escapeJson(text)).append("\"");
            }
            if (entropy >= 0) json.append(",\"entropy\":").append(entropy);
            if (phi >= 0) json.append(",\"phi\":").append(phi);
            if (load >= 0) json.append(",\"load\":").append(load);
            if (mood != null) json.append(",\"mood\":\"").append(escapeJson(mood)).append("\"");

            json.append("}");

            out.println(json.toString());
            out.flush();

        } catch (Exception e) {
            connected = false;
            System.out.println("🎭 [AVATAR] Connection lost.");
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /** Graceful shutdown. */
    public void shutdown() {
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (Exception ignored) {}
        connected = false;
    }
}
