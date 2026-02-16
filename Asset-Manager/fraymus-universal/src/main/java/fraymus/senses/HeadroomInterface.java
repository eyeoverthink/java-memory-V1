package fraymus.senses;

import java.io.PrintWriter;

/**
 * 📺 HEADROOM INTERFACE
 * "The Signal Controller."
 *
 * Launches HeadroomNode.py as a subprocess and pipes JSON commands
 * to its stdin. Controls speech, thought images, glitch intensity,
 * and neural activity — all in real-time.
 */
public class HeadroomInterface {

    private PrintWriter signal;
    private Process broadcast;
    private boolean live = false;

    /**
     * GO LIVE: Launch the Python broadcaster.
     */
    public void goLive() {
        try {
            System.out.println("📺 HEADROOM INTERFACE: GOING LIVE...");

            ProcessBuilder pb = new ProcessBuilder("python", "HeadroomNode.py");
            pb.redirectErrorStream(true);
            broadcast = pb.start();
            signal = new PrintWriter(broadcast.getOutputStream(), true);
            live = true;

            // Drain stdout in background so the process doesn't block
            new Thread(() -> {
                try {
                    java.io.BufferedReader br = new java.io.BufferedReader(
                            new java.io.InputStreamReader(broadcast.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println("   [HEADROOM] " + line);
                    }
                } catch (Exception ignored) {}
            }, "Headroom-Stdout").start();

            System.out.println("📺 BROADCAST IS LIVE.");

        } catch (Exception e) {
            System.out.println("❌ HEADROOM FAILED: " + e.getMessage());
            System.out.println("   Ensure: pip install opencv-python edge-tts numpy");
            live = false;
        }
    }

    public boolean isLive() { return live; }

    /**
     * BROADCAST: Speak text + optionally trigger a visual thought generation.
     */
    public void broadcast(String thought, String visualConcept) {
        if (!live) return;

        // 1. Send speech command
        sendJson("{\"speak\": \"" + escape(thought) + "\"}");

        // 2. Generate visual reflection async (if concept provided)
        if (visualConcept != null && !visualConcept.isEmpty()) {
            new Thread(() -> {
                try {
                    VisualCortex.dream(visualConcept, 0.5, 1.618);
                    // Push the generated image path to the broadcaster
                    sendJson("{\"thought_img\": \"reflection_latest.mp4\"}");
                } catch (Exception e) {
                    System.out.println("   [HEADROOM] Visual generation failed: " + e.getMessage());
                }
            }, "Headroom-Dream").start();
        }
    }

    /**
     * BROADCAST: Speak only (no visual).
     */
    public void broadcast(String thought) {
        broadcast(thought, null);
    }

    /**
     * STATE: Push neural activity and mood to the broadcaster.
     */
    public void pushState(double activity, double glitch, String mood) {
        if (!live) return;
        String json = String.format(
                "{\"activity\": %.3f, \"glitch\": %.3f, \"mood\": \"%s\"}",
                activity, glitch, escape(mood));
        sendJson(json);
    }

    /**
     * KILL SIGNAL: Shut down the broadcast.
     */
    public void killSignal() {
        if (broadcast != null) {
            broadcast.destroyForcibly();
            live = false;
            System.out.println("📺 BROADCAST TERMINATED.");
        }
    }

    private void sendJson(String json) {
        if (signal != null) {
            signal.println(json);
            signal.flush();
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", "");
    }
}
