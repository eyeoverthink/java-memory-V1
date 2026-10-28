package fraymus;

import fraymus.vision.ScottAlgorithm;
import fraymus.vision.ImagePixelLoader;
import fraymus.vision.VisualCortex;
import fraymus.vision.VisionMemory;
import java.io.IOException;

public class GreenAppleLearningDemo {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║           REAL IMAGE LEARNING - Green Apple Regeneration            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            ScottAlgorithm scott = new ScottAlgorithm(2.0, 10, true);
            VisualCortex visualCortex = new VisualCortex("build/visual_output/");
            visualCortex.initialize();
            VisionMemory visionMemory = new VisionMemory();
            fraymus.oracle.PhiEmergenceEngine oracle = new fraymus.oracle.PhiEmergenceEngine();
            oracle.start();

            String imagePath = "F:\\Full-AI-System\\Java-Memory\\Asset-Manager\\green-apple.PNG";

            System.out.println("[1] Loading real image: green-apple.PNG");
            System.out.println();

            System.out.println("[2] PHASE 1: LEARNING - Extract and store signature");
            ScottAlgorithm.ProcessingResult originalResult = scott.processImageFile(imagePath);
            System.out.println("    Original points: " + originalResult.originalPoints);
            System.out.println("    Simplified points: " + originalResult.simplifiedPoints);
            System.out.println("    Reduction: " + String.format("%.1f%%", originalResult.reduction * 100));
            System.out.println("    Signature: " + originalResult.signature);
            System.out.println();

            fraymus.oracle.PhiEmergenceEngine.QueryResponse oracleResponse = 
                oracle.processQuery("green apple with olive color");
            System.out.println("    Oracle resonance: " + String.format("%.4f", oracleResponse.resonance));
            System.out.println();

            visionMemory.storeSignature("green-apple", originalResult, oracleResponse);
            System.out.println("    Stored in VisionMemory: " + visionMemory.getStats());
            System.out.println();

            System.out.println("[3] PHASE 2: REGENERATION - Recalling from memory");
            ScottAlgorithm.ProcessingResult regenerated = visionMemory.regenerate("green-apple");
            System.out.println("    Regenerated signature: " + regenerated.signature);
            System.out.println();

            System.out.println("[4] COMPARISON - Original vs Regenerated");
            System.out.println("    Original phi resonance: " + String.format("%.4f", originalResult.signature.phiResonance));
            System.out.println("    Regenerated phi resonance: " + String.format("%.4f", regenerated.signature.phiResonance));
            System.out.println("    Phi improvement: " + String.format("%.2f%%", 
                (regenerated.signature.phiResonance - originalResult.signature.phiResonance) / originalResult.signature.phiResonance * 100));
            System.out.println();

            System.out.println("[5] Generating visual outputs");
            visualCortex.generateVisual(originalResult, "green_apple_learned.png");
            visualCortex.generateVisualWithTexture(originalResult, "green_apple_learned_textured.png");
            visualCortex.generateVisual(regenerated, "green_apple_recreated.png");
            visualCortex.generateVisualWithTexture(regenerated, "green_apple_recreated_textured.png");
            System.out.println("    Generated: green_apple_learned.png (from original image)");
            System.out.println("    Generated: green_apple_recreated.png (TRUE regeneration from signature)");
            System.out.println();

            System.out.println("╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║              REAL IMAGE REGENERATION COMPLETE                       ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("The system learned from the real green-apple.PNG image");
            System.out.println("and regenerated it from memory (bare metal, no training).");
            System.out.println();
            System.out.println("Generated Outputs:");
            System.out.println("  build/visual_output/green-apple_original_basic.png");
            System.out.println("  build/visual_output/green-apple_original_textured.png");
            System.out.println("  build/visual_output/green-apple_regenerated_basic.png");
            System.out.println("  build/visual_output/green-apple_regenerated_textured.png");
            System.out.println();

            oracle.stop();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
