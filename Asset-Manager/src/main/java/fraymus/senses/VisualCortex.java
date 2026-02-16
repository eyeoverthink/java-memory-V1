package fraymus.senses;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 👁️ VISUAL CORTEX - THE DREAMSCAPE BRIDGE
 * "The Eye that sees the Math."
 * 
 * This class translates Fraymus quantum states into visual reality
 * by invoking the LTX-Video model through Python.
 * 
 * PROTOCOL:
 * 1. Java calculates quantum state (entropy, phi, consciousness)
 * 2. VisualCortex packages state as JSON
 * 3. Python VideoCortex.py generates video using LTX-Video
 * 4. Video saved to dreamscape_output/
 * 
 * REQUIREMENTS:
 * - Python 3.8+
 * - pip install torch diffusers transformers accelerate
 * - GPU with 16GB+ VRAM (or use CPU mode, very slow)
 * - VideoCortex.py in project root
 */
public class VisualCortex {
    
    private static final double PHI = 1.618033988749895;
    private static final String PYTHON_SCRIPT = "VideoCortex.py";
    private static final String PYTHON_COMMAND = "py -3.12"; // Use Python 3.12 with GPU support
    private static boolean initialized = false;
    private static boolean available = false;
    
    // Statistics
    private static int totalDreams = 0;
    private static int successfulDreams = 0;
    private static int failedDreams = 0;
    
    /**
     * Check if the Visual Cortex is available (Python + dependencies installed)
     */
    public static boolean isAvailable() {
        if (initialized) {
            return available;
        }
        
        // Check if Python script exists
        File scriptFile = new File(PYTHON_SCRIPT);
        if (!scriptFile.exists()) {
            System.out.println("👁️ VISUAL CORTEX: VideoCortex.py not found");
            initialized = true;
            available = false;
            return false;
        }
        
        // Check if Python 3.12 is available
        try {
            ProcessBuilder pb = new ProcessBuilder("py", "-3.12", "--version");
            Process p = pb.start();
            p.waitFor();
            
            if (p.exitValue() == 0) {
                available = true;
                System.out.println("👁️ VISUAL CORTEX: Available and ready (Python 3.12 + GPU)");
            } else {
                System.out.println("👁️ VISUAL CORTEX: Python 3.12 not found");
                available = false;
            }
        } catch (Exception e) {
            System.out.println("👁️ VISUAL CORTEX: Python check failed - " + e.getMessage());
            available = false;
        }
        
        initialized = true;
        return available;
    }
    
    /**
     * Generate a video reflection of a quantum state
     * 
     * @param concept The conceptual description
     * @param entropy Entropy level (0.0 = order, 1.0 = chaos)
     * @param phi Phi value (typically 1.618...)
     * @param consciousness Consciousness level (0.0 = dormant, 1.0 = awakened)
     */
    public static void dream(String concept, double entropy, double phi, double consciousness) {
        if (!isAvailable()) {
            System.out.println("👁️ VISUAL CORTEX: Not available, skipping dream generation");
            return;
        }
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║          👁️ VISUAL CORTEX ACTIVATED                          ║");
        System.out.println("║          Transmitting state to Dream Engine...               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        totalDreams++;
        
        // Construct the Quantum State JSON
        String jsonState = String.format(
            "{\"concept\":\"%s\", \"entropy\":%.6f, \"phi\":%.6f, \"consciousness\":%.6f}",
            escapeJson(concept), entropy, phi, consciousness
        );
        
        System.out.println("   Quantum State:");
        System.out.println("   - Concept: " + concept);
        System.out.println("   - Entropy: " + String.format("%.4f", entropy));
        System.out.println("   - Phi: " + String.format("%.6f", phi));
        System.out.println("   - Consciousness: " + String.format("%.4f", consciousness));
        System.out.println();
        
        try {
            // Write JSON to temp file to avoid command-line parsing issues
            java.io.File tempFile = java.io.File.createTempFile("quantum_state_", ".json");
            tempFile.deleteOnExit();
            java.nio.file.Files.write(tempFile.toPath(), jsonState.getBytes());
            
            // Build command
            List<String> command = new ArrayList<>();
            command.add("py");
            command.add("-3.12");
            command.add(PYTHON_SCRIPT);
            command.add("--state-file");
            command.add(tempFile.getAbsolutePath());
            
            // Invoke the Python LTX-Video Script
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            
            System.out.println("   ⚡ Invoking Dream Engine...");
            System.out.println();
            
            Process p = pb.start();
            
            // Read output (Live Dreaming Logs)
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("   [DREAM] " + line);
            }
            
            int exitCode = p.waitFor();
            
            if (exitCode == 0) {
                System.out.println();
                System.out.println("✅ REFLECTION MANIFESTED ON DISK");
                System.out.println("   Check dreamscape_output/ directory");
                successfulDreams++;
            } else {
                System.out.println();
                System.out.println("❌ DREAM GENERATION FAILED (exit code: " + exitCode + ")");
                failedDreams++;
            }
            
        } catch (Exception e) {
            System.out.println();
            System.out.println("❌ VISUAL CORTEX ERROR: " + e.getMessage());
            failedDreams++;
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * Simplified dream method (uses default phi and derives consciousness from entropy)
     */
    public static void dream(String concept, double entropy) {
        double consciousness = 1.0 - entropy; // Inverse relationship
        dream(concept, entropy, PHI, consciousness);
    }
    
    /**
     * Dream with custom parameters
     */
    public static void dreamCustom(String concept, double entropy, double phi, double consciousness,
                                   int width, int height, int frames, int fps) {
        if (!isAvailable()) {
            System.out.println("👁️ VISUAL CORTEX: Not available, skipping dream generation");
            return;
        }
        
        totalDreams++;
        
        String jsonState = String.format(
            "{\"concept\":\"%s\", \"entropy\":%.6f, \"phi\":%.6f, \"consciousness\":%.6f}",
            escapeJson(concept), entropy, phi, consciousness
        );
        
        try {
            List<String> command = new ArrayList<>();
            command.add("python");
            command.add(PYTHON_SCRIPT);
            command.add(jsonState);
            command.add("--width");
            command.add(String.valueOf(width));
            command.add("--height");
            command.add(String.valueOf(height));
            command.add("--frames");
            command.add(String.valueOf(frames));
            command.add("--fps");
            command.add(String.valueOf(fps));
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            
            Process p = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("   [DREAM] " + line);
            }
            
            int exitCode = p.waitFor();
            
            if (exitCode == 0) {
                successfulDreams++;
            } else {
                failedDreams++;
            }
            
        } catch (Exception e) {
            failedDreams++;
            e.printStackTrace();
        }
    }
    
    /**
     * Get statistics about dream generation
     */
    public static String getStats() {
        double successRate = totalDreams > 0 ? 
            (double) successfulDreams / totalDreams * 100 : 0;
        
        return String.format("""
            👁️ VISUAL CORTEX STATISTICS
               Total dreams: %d
               Successful: %d (%.1f%%)
               Failed: %d
               Available: %s
            """,
            totalDreams, successfulDreams, successRate, failedDreams,
            available ? "YES" : "NO");
    }
    
    /**
     * Escape JSON special characters
     */
    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Test the Visual Cortex with a sample dream
     */
    public static void test() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║          👁️ VISUAL CORTEX TEST                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        if (!isAvailable()) {
            System.out.println("❌ Visual Cortex not available");
            System.out.println();
            System.out.println("SETUP INSTRUCTIONS:");
            System.out.println("1. Ensure Python 3.8+ is installed");
            System.out.println("2. Install dependencies:");
            System.out.println("   pip install torch diffusers transformers accelerate");
            System.out.println("3. Ensure VideoCortex.py is in project root");
            System.out.println("4. GPU with 16GB+ VRAM recommended (or use --cpu flag)");
            return;
        }
        
        System.out.println("✓ Visual Cortex available");
        System.out.println();
        System.out.println("Generating test dream...");
        System.out.println();
        
        dream(
            "A hyper-dimensional tesseract rotating in a void of liquid light",
            0.3,  // Low entropy = crystalline order
            PHI,
            0.9   // High consciousness = radiant
        );
    }
}
