package fraymus.senses;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 📺 HEADROOM INTERFACE
 * "The Controller of the Signal"
 * 
 * This class controls the live broadcast. It:
 * 1. Launches the Python broadcaster (HeadroomNode.py)
 * 2. Sends speech commands (text-to-speech)
 * 3. Triggers visual thought generation (LTX-Video)
 * 4. Pushes thought images to the broadcast
 * 5. Controls glitch intensity and neural activity
 * 
 * The AI doesn't just respond - it BROADCASTS.
 */
public class HeadroomInterface {
    
    private PrintWriter signal;
    private Process broadcast;
    private Thread outputReader;
    private AtomicBoolean broadcasting;
    
    // Statistics
    private int totalBroadcasts = 0;
    private int totalThoughts = 0;
    
    public HeadroomInterface() {
        this.broadcasting = new AtomicBoolean(false);
    }
    
    /**
     * GO LIVE - Start the broadcast
     */
    public void goLive() {
        if (broadcasting.get()) {
            System.out.println("📺 Already broadcasting");
            return;
        }
        
        try {
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║          📺 HEADROOM INTERFACE: GOING LIVE                    ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");
            System.out.println();
            
            // Check if Python script exists
            Path scriptPath = Paths.get("HeadroomNode.py");
            if (!Files.exists(scriptPath)) {
                System.out.println("❌ HeadroomNode.py not found in current directory");
                System.out.println("   Expected: " + scriptPath.toAbsolutePath());
                return;
            }
            
            // Launch the Python Broadcaster
            ProcessBuilder pb = new ProcessBuilder("python", "HeadroomNode.py");
            pb.redirectErrorStream(true);
            
            broadcast = pb.start();
            signal = new PrintWriter(broadcast.getOutputStream(), true);
            broadcasting.set(true);
            
            // Start thread to read broadcaster output
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
            
            // Give broadcaster time to initialize
            Thread.sleep(1000);
            
            System.out.println("✅ BROADCAST LIVE");
            System.out.println("   A window should appear showing the signal");
            System.out.println();
            
        } catch (Exception e) {
            System.out.println("❌ FAILED TO GO LIVE: " + e.getMessage());
            e.printStackTrace();
            broadcasting.set(false);
        }
    }
    
    /**
     * BROADCAST - Speak text and show visual thought
     * 
     * @param thought The text to speak
     * @param visualConcept The concept to visualize
     */
    public void broadcast(String thought, String visualConcept) {
        broadcast(thought, visualConcept, 0.5);
    }
    
    /**
     * BROADCAST - Speak text and show visual thought with neural activity
     * 
     * @param thought The text to speak
     * @param visualConcept The concept to visualize
     * @param neuralActivity Neural activity level (0.0 = calm, 1.0 = intense)
     */
    public void broadcast(String thought, String visualConcept, double neuralActivity) {
        if (!broadcasting.get()) {
            System.out.println("⚠️ Not broadcasting. Call goLive() first.");
            return;
        }
        
        totalBroadcasts++;
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("📺 BROADCASTING");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   Speech: " + thought);
        System.out.println("   Visual: " + visualConcept);
        System.out.println("   Neural: " + String.format("%.2f", neuralActivity));
        System.out.println();
        
        try {
            // 1. Send speech command
            String speechJson = String.format(
                "{\"speak\": \"%s\", \"neural\": %.2f}",
                escapeJson(thought),
                neuralActivity
            );
            signal.println(speechJson);
            signal.flush();
            
            // 2. Generate visual reflection (async)
            if (VisualCortex.isAvailable()) {
                new Thread(() -> {
                    try {
                        System.out.println("   🎥 Generating visual thought...");
                        
                        // Calculate entropy from neural activity
                        double entropy = neuralActivity;
                        double consciousness = 1.0 - entropy;
                        
                        // Generate video/image
                        VisualCortex.dream(
                            visualConcept,
                            entropy,
                            1.618033988749895,
                            consciousness
                        );
                        
                        totalThoughts++;
                        
                        // Find the most recent generated file
                        Path outputDir = Paths.get("dreamscape_output");
                        if (Files.exists(outputDir)) {
                            // Get most recent file
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
                                // Extract first frame as image for display
                                // For now, just send the video path
                                String imgJson = String.format(
                                    "{\"thought_img\": \"%s\"}",
                                    escapeJson(latestFile.toString())
                                );
                                signal.println(imgJson);
                                signal.flush();
                                
                                System.out.println("   ✅ Visual thought sent to broadcast");
                            }
                        }
                        
                    } catch (Exception e) {
                        System.out.println("   ⚠️ Visual generation failed: " + e.getMessage());
                    }
                }, "VisualGenerator").start();
            } else {
                System.out.println("   ⚠️ Visual Cortex not available (text-only mode)");
            }
            
        } catch (Exception e) {
            System.out.println("❌ BROADCAST ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * SPEAK - Just speak without visual (faster)
     */
    public void speak(String text) {
        if (!broadcasting.get()) {
            System.out.println("⚠️ Not broadcasting. Call goLive() first.");
            return;
        }
        
        String json = String.format("{\"speak\": \"%s\"}", escapeJson(text));
        signal.println(json);
        signal.flush();
    }
    
    /**
     * GLITCH - Trigger glitch effect
     */
    public void glitch(double intensity) {
        if (!broadcasting.get()) return;
        
        String json = String.format("{\"glitch\": %.2f}", intensity);
        signal.println(json);
        signal.flush();
    }
    
    /**
     * SET NEURAL ACTIVITY - Control the AI's "excitement level"
     */
    public void setNeuralActivity(double level) {
        if (!broadcasting.get()) return;
        
        String json = String.format("{\"neural\": %.2f}", level);
        signal.println(json);
        signal.flush();
    }
    
    /**
     * SHOW THOUGHT - Display a pre-generated image
     */
    public void showThought(String imagePath) {
        if (!broadcasting.get()) return;
        
        String json = String.format("{\"thought_img\": \"%s\"}", escapeJson(imagePath));
        signal.println(json);
        signal.flush();
    }
    
    /**
     * OFF AIR - Stop the broadcast
     */
    public void offAir() {
        if (!broadcasting.get()) {
            return;
        }
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("📺 GOING OFF AIR");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println(getStats());
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        broadcasting.set(false);
        
        if (signal != null) {
            signal.close();
        }
        
        if (broadcast != null) {
            broadcast.destroy();
        }
    }
    
    /**
     * Get broadcast statistics
     */
    public String getStats() {
        return String.format("""
            📺 BROADCAST STATISTICS
               Total broadcasts: %d
               Visual thoughts: %d
               Status: %s
            """,
            totalBroadcasts, totalThoughts,
            broadcasting.get() ? "ON AIR" : "OFF AIR");
    }
    
    /**
     * Check if currently broadcasting
     */
    public boolean isLive() {
        return broadcasting.get();
    }
    
    /**
     * Escape JSON special characters
     */
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
