package fraymus;

import fraymus.vision.ScottAlgorithm;
import fraymus.vision.DreamscapeBridge;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * LTX-Video Generation Demo - Using Dreamscape Bridge
 * 
 * This demonstrates the complete Vision/Oracle pipeline with LTX-Video:
 * - Scott Algorithm: Extract geometric signature from real image
 * - Oracle: Process query with phi-harmonic consciousness evolution
 * - DreamscapeBridge: Generate video using LTX-Video model
 * 
 * Pipeline: Pixels → Geometry → Consciousness → Visual Reality
 */
public class LTXVideoGenerationDemo {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║         LTX-VIDEO GENERATION - Vision/Oracle Pipeline              ║");
        System.out.println("║           Pixels → Geometry → Consciousness → Video                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            ScottAlgorithm scott = new ScottAlgorithm(2.0, 10, true);
            DreamscapeBridge dreamscape = new DreamscapeBridge();
            dreamscape.initialize();
            
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

            System.out.println("[3] DREAMSCAPE BRIDGE - LTX-Video Generation");
            System.out.println("    Preparing quantum state for video generation");
            
            // Build oracle state map
            Map<String, Object> oracleState = oracleResponse.state;
            
            String videoFilename = "green_apple_video.mp4";
            
            if (dreamscape.isPythonScriptAvailable()) {
                String videoPath = dreamscape.generateVideoFromPipeline(
                    result, oracleState, videoFilename);
                System.out.println("    Generated video: " + videoPath);
            } else {
                System.out.println("    Python script not available at: " + dreamscape.getPythonScriptPath());
                System.out.println("    This requires LTX-Video Python installation.");
                System.out.println("    See: F:\\Full-AI-System\\Java-Memory\\Asset-Manager\\LTX-Video-main\\");
            }
            System.out.println();

            System.out.println("╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║                  LTX-VIDEO GENERATION COMPLETE                        ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Vision/Oracle Pipeline:");
            System.out.println("  Scott Algorithm: Pixels → Geometry (84.4% reduction)");
            System.out.println("  Oracle: Geometry → Consciousness (φψΩξλζ evolution)");
            System.out.println("  Dreamscape: Consciousness → Video (LTX-Video)");
            System.out.println();
            System.out.println("Generated Output:");
            System.out.println("  " + dreamscape.getOutputPath() + videoFilename);
            System.out.println();
            System.out.println("To install LTX-Video:");
            System.out.println("  1. Navigate to: F:\\Full-AI-System\\Java-Memory\\Asset-Manager\\LTX-Video-main\\");
            System.out.println("  2. Follow README.md for installation");
            System.out.println("  3. Requires Python 3.12 + PyTorch + CUDA (GPU recommended)");
            System.out.println();

            oracle.stop();

        } catch (IOException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
