package fraymus.senses;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * HeadroomInterface controls the live Max Headroom style signal.
 * It launches the Python broadcaster, sends speech/glitch/image commands,
 * and can trigger local visual manifestation through VisualCortex.
 */
public class HeadroomInterface {

    private PrintWriter signal;
    private Process broadcast;
    private Thread outputReader;
    private final AtomicBoolean broadcasting;
    private String launcherUsed = "";

    private int totalBroadcasts = 0;
    private int totalThoughts = 0;

    public HeadroomInterface() {
        this.broadcasting = new AtomicBoolean(false);
    }

    /**
     * Start the live signal.
     */
    public void goLive() {
        if (broadcasting.get()) {
            System.out.println("Already broadcasting");
            return;
        }

        try {
            Path scriptPath = Paths.get("HeadroomNode.py");
            if (!Files.exists(scriptPath)) {
                System.out.println("HeadroomNode.py not found in current directory");
                System.out.println("Expected: " + scriptPath.toAbsolutePath());
                return;
            }

            Exception lastFailure = null;
            for (List<String> launcher : pythonLaunchers()) {
                try {
                    launchBroadcaster(launcher);
                    Thread.sleep(1000);
                    if (broadcast != null && broadcast.isAlive()) {
                        launcherUsed = String.join(" ", launcher);
                        System.out.println("BROADCAST LIVE");
                        System.out.println("Launcher: " + launcherUsed);
                        return;
                    }
                    shutdownProcess();
                } catch (Exception exception) {
                    lastFailure = exception;
                    shutdownProcess();
                }
            }

            System.out.println("FAILED TO GO LIVE: no Python launcher could keep the signal alive.");
            if (lastFailure != null) {
                lastFailure.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("FAILED TO GO LIVE: " + e.getMessage());
            e.printStackTrace();
            shutdownProcess();
        }
    }

    /**
     * Speak text and generate a new visual thought.
     */
    public void broadcast(String thought, String visualConcept) {
        broadcast(thought, visualConcept, 0.5);
    }

    /**
     * Speak text and generate a new visual thought with a neural activity level.
     */
    public void broadcast(String thought, String visualConcept, double neuralActivity) {
        if (!broadcasting.get()) {
            System.out.println("Not broadcasting. Call goLive() first.");
            return;
        }

        totalBroadcasts++;

        try {
            String speechJson = String.format(
                "{\"speak\": \"%s\", \"neural\": %.2f}",
                escapeJson(thought),
                neuralActivity
            );
            signal.println(speechJson);
            signal.flush();

            if (VisualCortex.isAvailable()) {
                new Thread(() -> {
                    try {
                        double entropy = neuralActivity;
                        double consciousness = 1.0 - entropy;

                        VisualCortex.dream(
                            visualConcept,
                            entropy,
                            1.618033988749895,
                            consciousness
                        );

                        totalThoughts++;

                        Path outputDir = Paths.get("dreamscape_output");
                        if (Files.exists(outputDir)) {
                            Path latestFile = Files.list(outputDir)
                                .filter(p -> p.toString().endsWith(".mp4"))
                                .max((p1, p2) -> {
                                    try {
                                        return Files.getLastModifiedTime(p1)
                                            .compareTo(Files.getLastModifiedTime(p2));
                                    } catch (Exception e) {
                                        return 0;
                                    }
                                })
                                .orElse(null);

                            if (latestFile != null) {
                                String imgJson = String.format(
                                    "{\"thought_img\": \"%s\"}",
                                    escapeJson(latestFile.toString())
                                );
                                signal.println(imgJson);
                                signal.flush();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Visual generation failed: " + e.getMessage());
                    }
                }, "VisualGenerator").start();
            } else {
                System.out.println("Visual Cortex not available (text-only mode)");
            }
        } catch (Exception e) {
            System.out.println("BROADCAST ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Speak text only.
     */
    public void speak(String text) {
        if (!broadcasting.get()) {
            System.out.println("Not broadcasting. Call goLive() first.");
            return;
        }

        totalBroadcasts++;
        String json = String.format("{\"speak\": \"%s\"}", escapeJson(text));
        signal.println(json);
        signal.flush();
    }

    public void glitch(double intensity) {
        if (!broadcasting.get()) {
            return;
        }

        String json = String.format("{\"glitch\": %.2f}", intensity);
        signal.println(json);
        signal.flush();
    }

    public void setNeuralActivity(double level) {
        if (!broadcasting.get()) {
            return;
        }

        String json = String.format("{\"neural\": %.2f}", level);
        signal.println(json);
        signal.flush();
    }

    public void showThought(String imagePath) {
        if (!broadcasting.get()) {
            return;
        }

        String json = String.format("{\"thought_img\": \"%s\"}", escapeJson(imagePath));
        signal.println(json);
        signal.flush();
        totalThoughts++;
    }

    public void offAir() {
        if (!broadcasting.get()) {
            return;
        }

        System.out.println(getStats());
        shutdownProcess();
    }

    public String getStats() {
        return String.format("""
            BROADCAST STATISTICS
               Total broadcasts: %d
               Visual thoughts: %d
               Status: %s
               Launcher: %s
            """,
            totalBroadcasts,
            totalThoughts,
            broadcasting.get() ? "ON AIR" : "OFF AIR",
            launcherUsed.isBlank() ? "unknown" : launcherUsed
        );
    }

    public boolean isLive() {
        return broadcasting.get();
    }

    public int totalBroadcasts() {
        return totalBroadcasts;
    }

    public int totalThoughts() {
        return totalThoughts;
    }

    public String launcherUsed() {
        return launcherUsed;
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    private List<List<String>> pythonLaunchers() {
        return List.of(
            List.of("py", "-3.12"),
            List.of("python"),
            List.of("py")
        );
    }

    private void launchBroadcaster(List<String> launcher) throws Exception {
        List<String> command = new ArrayList<>(launcher);
        command.add("HeadroomNode.py");

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        broadcast = pb.start();
        signal = new PrintWriter(broadcast.getOutputStream(), true);
        broadcasting.set(true);

        outputReader = new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(broadcast.getInputStream())
                );
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("   [BROADCAST] " + line);
                }
            } catch (Exception e) {
                if (broadcasting.get()) {
                    System.out.println("   [BROADCAST ERROR] " + e.getMessage());
                }
            }
        }, "BroadcastReader");
        outputReader.setDaemon(true);
        outputReader.start();
    }

    private void shutdownProcess() {
        broadcasting.set(false);
        if (signal != null) {
            signal.close();
            signal = null;
        }
        if (broadcast != null) {
            broadcast.destroy();
            broadcast = null;
        }
    }
}
