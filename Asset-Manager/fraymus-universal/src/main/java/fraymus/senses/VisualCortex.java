package fraymus.senses;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 👁️ VISUAL CORTEX
 * "The Eye that sees the Math."
 *
 * Bridges the Java organism to the Python LTX-Video dream engine.
 * Translates quantum state (concept, entropy, phi) into video reflections.
 */
public class VisualCortex {

    public static void dream(String concept, double entropy, double phi) {
        System.out.println("👁️ VISUAL CORTEX: TRANSMITTING STATE TO DREAM ENGINE...");

        // Construct the Quantum State JSON
        String jsonState = String.format(
            "{\"concept\":\"%s\", \"entropy\":%.4f, \"phi\":%.4f}",
            concept.replace("\"", "\\\""), entropy, phi
        );

        try {
            // Invoke the Python LTX-Video Script
            ProcessBuilder pb = new ProcessBuilder(
                "python", "VideoCortex.py", jsonState
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();

            // Read output (Live Dreaming Logs)
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("   [DREAM] " + line);
            }

            int exitCode = p.waitFor();
            if (exitCode == 0) {
                System.out.println("✅ REFLECTION MANIFESTED ON DISK.");
            } else {
                System.out.println("❌ DREAM ENGINE EXITED WITH CODE " + exitCode);
            }

        } catch (Exception e) {
            System.out.println("❌ VISUAL CORTEX FAILED: " + e.getMessage());
            System.out.println("   Ensure: pip install torch diffusers transformers accelerate");
            System.out.println("   Ensure: VideoCortex.py is in the working directory");
        }
    }
}
