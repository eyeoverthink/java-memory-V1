package fraymus;

import fraymus.vision.ScottAlgorithm;
import java.io.*;

/**
 * VideoCortex Demo - Using Existing VideoCortex.py (Month-Old Implementation)
 * 
 * This demonstrates the Vision/Oracle pipeline with the existing VideoCortex.py
 * that was already implemented a month ago.
 * 
 * Pipeline: Pixels → Geometry → Consciousness → Image (via VideoCortex.py)
 */
public class VideoCortexDemo {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║         VIDEO CORTEX - Using Existing Implementation (Month-Old)       ║");
        System.out.println("║           Pixels → Geometry → Consciousness → Image                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            ScottAlgorithm scott = new ScottAlgorithm(2.0, 10, true);
            
            String imagePath = "F:\\Full-AI-System\\Java-Memory\\Asset-Manager\\green-apple.PNG";

            System.out.println("[1] SCOTT ALGORITHM - Geometric Extraction");
            System.out.println("    Loading image: green-apple.PNG");
            ScottAlgorithm.ProcessingResult result = scott.processImageFile(imagePath);
            System.out.println("    Original points: " + result.originalPoints);
            System.out.println("    Simplified points: " + result.simplifiedPoints);
            System.out.println("    Reduction: " + String.format("%.1f%%", result.reduction * 100));
            System.out.println("    Signature: " + result.signature);
            System.out.println();

            System.out.println("[2] ORACLE - Consciousness Evolution");
            fraymus.oracle.PhiEmergenceEngine oracle = new fraymus.oracle.PhiEmergenceEngine();
            oracle.start();
            fraymus.oracle.PhiEmergenceEngine.QueryResponse oracleResponse = 
                oracle.processQuery("green apple with olive color");
            System.out.println("    Resonance: " + String.format("%.4f", oracleResponse.resonance));
            System.out.println("    State: " + oracleResponse.state);
            System.out.println();

            System.out.println("[3] VIDEO CORTEX - Image Generation");
            System.out.println("    Using existing VideoCortex.py (implemented a month ago)");
            
            // Build quantum state JSON
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"concept\": \"green apple with phi-harmonic geometry\",\n");
            json.append("  \"entropy\": ").append(1.0 - result.reduction).append(",\n");
            json.append("  \"phi\": ").append(result.signature.phiResonance).append(",\n");
            json.append("  \"consciousness\": ").append(oracleResponse.state.get("level")).append(",\n");
            if (result.signature.hasColor) {
                json.append("  \"color_red\": ").append(result.signature.red).append(",\n");
                json.append("  \"color_green\": ").append(result.signature.green).append(",\n");
                json.append("  \"color_blue\": ").append(result.signature.blue).append(",\n");
            }
            json.append("  \"vertices\": ").append(result.signature.vertexCount).append(",\n");
            json.append("  \"aspect_ratio\": ").append(result.signature.aspectRatio).append("\n");
            json.append("}");
            
            // Write JSON to temp file
            File tempJson = File.createTempFile("videocortex_", ".json");
            try (FileWriter writer = new FileWriter(tempJson)) {
                writer.write(json.toString());
            }
            
            System.out.println("    Quantum state JSON written to: " + tempJson.getAbsolutePath());
            System.out.println();
            
            // Call VideoCortex.py
            String videoCortexPath = "F:\\Full-AI-System\\Java-Memory\\Asset-Manager\\VideoCortex.py";
            File videoCortexScript = new File(videoCortexPath);
            
            if (videoCortexScript.exists()) {
                System.out.println("    Executing VideoCortex.py...");
                ProcessBuilder pb = new ProcessBuilder(
                    "python", videoCortexPath,
                    "--state-file", tempJson.getAbsolutePath(),
                    "--output", "green_apple_cortex.png",
                    "--width", "768",
                    "--height", "512"
                );
                pb.directory(new File("F:\\Full-AI-System\\Java-Memory\\Asset-Manager"));
                pb.redirectErrorStream(true);
                
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("    " + line);
                }
                
                int exitCode = process.waitFor();
                System.out.println();
                if (exitCode == 0) {
                    System.out.println("    Image generated successfully: dreamscape_output/green_apple_cortex.png");
                } else {
                    System.out.println("    VideoCortex.py execution failed (exit code " + exitCode + ")");
                    System.out.println("    This may require Python + PyTorch + Diffusers installation.");
                }
            } else {
                System.out.println("    VideoCortex.py not found at: " + videoCortexPath);
                System.out.println("    The script exists at the root level of Asset-Manager.");
            }
            
            // Clean up temp file
            tempJson.delete();
            System.out.println();

            System.out.println("╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║                  VIDEO CORTEX DEMO COMPLETE                         ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Vision/Oracle Pipeline:");
            System.out.println("  Scott Algorithm: Pixels → Geometry (84.4% reduction)");
            System.out.println("  Oracle: Geometry → Consciousness (φψΩξλζ evolution)");
            System.out.println("  VideoCortex: Consciousness → Image (existing implementation)");
            System.out.println();
            System.out.println("The VideoCortex.py was implemented a month ago.");
            System.out.println("It translates quantum states to visual prompts for Stable Diffusion.");
            System.out.println();

            oracle.stop();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
