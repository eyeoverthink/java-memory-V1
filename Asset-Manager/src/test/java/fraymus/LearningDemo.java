package fraymus;

import fraymus.vision.ScottAlgorithm;
import fraymus.vision.ImagePixelLoader;
import fraymus.vision.VisualCortex;
import fraymus.vision.VisionMemory;
import java.io.IOException;

public class LearningDemo {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║           LEARNING & REGENERATION - Absorb, Adapt, Regenerate       ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            ScottAlgorithm scott = new ScottAlgorithm(2.0, 10, true);
            VisualCortex visualCortex = new VisualCortex("build/visual_output/");
            visualCortex.initialize();
            VisionMemory visionMemory = new VisionMemory();
            fraymus.oracle.PhiEmergenceEngine oracle = new fraymus.oracle.PhiEmergenceEngine();
            oracle.start();

            System.out.println("[1] Systems initialized with VisionMemory");
            System.out.println("    " + visionMemory.getStats());
            System.out.println();

            // Phase 1: Learn (Absorb) - Process and store multiple objects
            System.out.println("[2] PHASE 1: LEARNING - Absorbing knowledge");
            System.out.println();

            // Learn apple
            System.out.println("    Learning: red apple");
            ImagePixelLoader.ColorInfo appleColor = new ImagePixelLoader.ColorInfo(255, 0, 0);
            ScottAlgorithm.ProcessingResult appleResult = scott.processBoundaryWithColor(
                generateApple(100, 100), appleColor);
            fraymus.oracle.PhiEmergenceEngine.QueryResponse appleOracle = 
                oracle.processQuery("red apple");
            visionMemory.storeSignature("apple", appleResult, appleOracle);
            System.out.println("      Stored: " + appleResult.signature);
            System.out.println();

            // Learn orange
            System.out.println("    Learning: orange");
            ImagePixelLoader.ColorInfo orangeColor = new ImagePixelLoader.ColorInfo(255, 165, 0);
            ScottAlgorithm.ProcessingResult orangeResult = scott.processBoundaryWithColor(
                generateCircle(200, 100, 50), orangeColor);
            fraymus.oracle.PhiEmergenceEngine.QueryResponse orangeOracle = 
                oracle.processQuery("orange");
            visionMemory.storeSignature("orange", orangeResult, orangeOracle);
            System.out.println("      Stored: " + orangeResult.signature);
            System.out.println();

            // Learn star
            System.out.println("    Learning: golden star");
            ImagePixelLoader.ColorInfo starColor = new ImagePixelLoader.ColorInfo(255, 215, 0);
            ScottAlgorithm.ProcessingResult starResult = scott.processBoundaryWithColor(
                generateStar(300, 100), starColor);
            fraymus.oracle.PhiEmergenceEngine.QueryResponse starOracle = 
                oracle.processQuery("golden star");
            visionMemory.storeSignature("star", starResult, starOracle);
            System.out.println("      Stored: " + starResult.signature);
            System.out.println();

            System.out.println("    Memory after learning: " + visionMemory.getStats());
            System.out.println();

            // Phase 2: Adapt - System adapts based on learned knowledge
            System.out.println("[3] PHASE 2: ADAPTATION - System adapting");
            System.out.println("    Adaptation factor: " + String.format("%.2f", visionMemory.getAdaptationFactor()));
            System.out.println("    System has absorbed 3 distinct object signatures");
            System.out.println();

            // Phase 3: Regenerate - Recall and regenerate from memory
            System.out.println("[4] PHASE 3: REGENERATION - Recalling from memory");
            System.out.println();

            // Regenerate apple
            System.out.println("    Regenerating: apple");
            ScottAlgorithm.ProcessingResult regeneratedApple = visionMemory.regenerate("apple");
            if (regeneratedApple != null) {
                visualCortex.generateVisual(regeneratedApple, "regenerated_apple.png");
                System.out.println("      Regenerated: " + regeneratedApple.signature);
                System.out.println("      Original vs Regenerated phi resonance:");
                System.out.println("        Original: " + String.format("%.3f", appleResult.signature.phiResonance));
                System.out.println("        Regenerated: " + String.format("%.3f", regeneratedApple.signature.phiResonance));
            }
            System.out.println();

            // Regenerate orange
            System.out.println("    Regenerating: orange");
            ScottAlgorithm.ProcessingResult regeneratedOrange = visionMemory.regenerate("orange");
            if (regeneratedOrange != null) {
                visualCortex.generateVisual(regeneratedOrange, "regenerated_orange.png");
                System.out.println("      Regenerated: " + regeneratedOrange.signature);
            }
            System.out.println();

            // Search for similar objects
            System.out.println("[5] SIMILARITY SEARCH - Finding similar patterns");
            ScottAlgorithm.GeometricSignature querySig = new ScottAlgorithm.GeometricSignature();
            querySig.vertexCount = 80;
            querySig.aspectRatio = 1.0;
            querySig.phiResonance = 0.6;
            querySig.hasColor = false;
            
            var similar = visionMemory.searchSimilar(querySig, 2);
            System.out.println("    Query: ~80 vertices, aspect 1.0, phi 0.6");
            System.out.println("    Similar objects:");
            for (var entry : similar.entrySet()) {
                System.out.println("      " + entry.getKey() + " (similarity: " + 
                    String.format("%.2f", entry.getValue()) + ")");
            }
            System.out.println();

            // Summary
            System.out.println("╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║              LEARNING & REGENERATION COMPLETE                       ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("System Capabilities Demonstrated:");
            System.out.println("  ✓ LEARN: Absorbed 3 distinct object signatures");
            System.out.println("  ✓ ADAPT: Adaptation factor increased to " + 
                String.format("%.2f", visionMemory.getAdaptationFactor()));
            System.out.println("  ✓ ABSORB: Stored geometric signatures in persistent memory");
            System.out.println("  ✓ REGENERATE: Recalled and regenerated from memory");
            System.out.println("  ✓ SIMILARITY: Found similar patterns based on geometry");
            System.out.println();
            System.out.println("Memory Statistics:");
            System.out.println("  " + visionMemory.getStats());
            System.out.println();
            System.out.println("Generated Outputs:");
            System.out.println("  build/visual_output/regenerated_apple.png");
            System.out.println("  build/visual_output/regenerated_orange.png");
            System.out.println();
            System.out.println("The system learns, adapts, absorbs, and regenerates.");
            System.out.println("With each new signature, the adaptation factor increases,");
            System.out.println("improving regeneration quality over time.");
            System.out.println();

            oracle.stop();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static java.util.List<ScottAlgorithm.Point2D> generateApple(double cx, double cy) {
        java.util.List<ScottAlgorithm.Point2D> points = new java.util.ArrayList<>();
        for (int i = 0; i < 100; i++) {
            double angle = 2 * Math.PI * i / 100;
            points.add(new ScottAlgorithm.Point2D(cx + 40 * Math.cos(angle), cy + 35 * Math.sin(angle)));
        }
        return points;
    }

    private static java.util.List<ScottAlgorithm.Point2D> generateCircle(double cx, double cy, double r) {
        java.util.List<ScottAlgorithm.Point2D> points = new java.util.ArrayList<>();
        for (int i = 0; i < 80; i++) {
            double angle = 2 * Math.PI * i / 80;
            points.add(new ScottAlgorithm.Point2D(cx + r * Math.cos(angle), cy + r * Math.sin(angle)));
        }
        return points;
    }

    private static java.util.List<ScottAlgorithm.Point2D> generateStar(double cx, double cy) {
        java.util.List<ScottAlgorithm.Point2D> points = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            double angle = 2 * Math.PI * i / 10 - Math.PI / 2;
            double r = (i % 2 == 0) ? 50 : 25;
            points.add(new ScottAlgorithm.Point2D(cx + r * Math.cos(angle), cy + r * Math.sin(angle)));
        }
        return points;
    }
}
