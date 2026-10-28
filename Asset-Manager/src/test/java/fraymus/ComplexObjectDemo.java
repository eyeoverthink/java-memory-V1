package fraymus;

import fraymus.vision.ScottAlgorithm;
import fraymus.vision.ImagePixelLoader;
import fraymus.vision.VisualCortex;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

/**
 * Complex Object Generation Demo
 * Demonstrates scaling up to multiple objects and scene composition
 */
public class ComplexObjectDemo {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║           COMPLEX OBJECT GENERATION - Multi-Object Scenes          ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            // Initialize components
            ScottAlgorithm scott = new ScottAlgorithm(2.0, 10, true);
            VisualCortex visualCortex = new VisualCortex("build/visual_output/");
            visualCortex.initialize();
            fraymus.oracle.PhiEmergenceEngine oracle = new fraymus.oracle.PhiEmergenceEngine();
            oracle.start();

            System.out.println("[1] Systems initialized");
            System.out.println();

            // Scene 1: Fruit Bowl (multiple fruits)
            System.out.println("[2] Generating Scene 1: Fruit Bowl");
            generateFruitBowl(scott, visualCortex, oracle);
            System.out.println();

            // Scene 2: Geometric Pattern
            System.out.println("[3] Generating Scene 2: Geometric Pattern");
            generateGeometricPattern(scott, visualCortex, oracle);
            System.out.println();

            // Scene 3: Complex Shape (Star with details)
            System.out.println("[4] Generating Scene 3: Complex Star");
            generateComplexStar(scott, visualCortex, oracle);
            System.out.println();

            // Summary
            System.out.println("╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║              COMPLEX OBJECT GENERATION COMPLETE                   ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Scalability Demonstrated:");
            System.out.println("  ✓ Multiple objects in single scene (Fruit Bowl)");
            System.out.println("  ✓ Geometric patterns and symmetry");
            System.out.println("  ✓ Complex shapes with internal details");
            System.out.println("  ✓ Color variation across objects");
            System.out.println("  ✓ Phi-harmonic texture synthesis");
            System.out.println();
            System.out.println("Generated Scenes:");
            System.out.println("  build/visual_output/fruit_bowl_*.png");
            System.out.println("  build/visual_output/geometric_pattern_*.png");
            System.out.println("  build/visual_output/complex_star_*.png");
            System.out.println();

            oracle.stop();

        } catch (IOException e) {
            System.err.println("Error during demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void generateFruitBowl(ScottAlgorithm scott, VisualCortex visualCortex, 
                                         fraymus.oracle.PhiEmergenceEngine oracle) throws IOException {
        // Apple (red)
        List<ScottAlgorithm.Point2D> apple = generateApple(100, 100);
        ImagePixelLoader.ColorInfo appleColor = new ImagePixelLoader.ColorInfo(255, 0, 0);
        ScottAlgorithm.ProcessingResult appleResult = scott.processBoundaryWithColor(apple, appleColor);
        visualCortex.generateVisual(appleResult, "fruit_bowl_apple.png");
        visualCortex.generateVisualWithTexture(appleResult, "fruit_bowl_apple_textured.png");
        System.out.println("    Apple: " + appleResult.originalPoints + " → " + appleResult.simplifiedPoints + " points");

        // Orange (orange)
        List<ScottAlgorithm.Point2D> orange = generateCircle(200, 100, 50);
        ImagePixelLoader.ColorInfo orangeColor = new ImagePixelLoader.ColorInfo(255, 165, 0);
        ScottAlgorithm.ProcessingResult orangeResult = scott.processBoundaryWithColor(orange, orangeColor);
        visualCortex.generateVisual(orangeResult, "fruit_bowl_orange.png");
        visualCortex.generateVisualWithTexture(orangeResult, "fruit_bowl_orange_textured.png");
        System.out.println("    Orange: " + orangeResult.originalPoints + " → " + orangeResult.simplifiedPoints + " points");

        // Banana (yellow)
        List<ScottAlgorithm.Point2D> banana = generateBanana(300, 100);
        ImagePixelLoader.ColorInfo bananaColor = new ImagePixelLoader.ColorInfo(255, 255, 0);
        ScottAlgorithm.ProcessingResult bananaResult = scott.processBoundaryWithColor(banana, bananaColor);
        visualCortex.generateVisual(bananaResult, "fruit_bowl_banana.png");
        visualCortex.generateVisualWithTexture(bananaResult, "fruit_bowl_banana_textured.png");
        System.out.println("    Banana: " + bananaResult.originalPoints + " → " + bananaResult.simplifiedPoints + " points");

        // Process through Oracle
        oracle.processQuery("fruit bowl scene with multiple colored objects");
    }

    private static void generateGeometricPattern(ScottAlgorithm scott, VisualCortex visualCortex,
                                                 fraymus.oracle.PhiEmergenceEngine oracle) throws IOException {
        // Create a pattern with multiple circles in phi-harmonic arrangement
        List<ScottAlgorithm.Point2D> pattern = new ArrayList<>();
        double centerX = 150;
        double centerY = 150;
        double phi = ScottAlgorithm.PHI;

        // Central circle
        for (int i = 0; i < 100; i++) {
            double angle = 2 * Math.PI * i / 100;
            double x = centerX + 40 * Math.cos(angle);
            double y = centerY + 40 * Math.sin(angle);
            pattern.add(new ScottAlgorithm.Point2D(x, y));
        }

        // Surrounding circles in phi-harmonic positions
        for (int j = 0; j < 6; j++) {
            double angle = 2 * Math.PI * j / 6;
            double cx = centerX + 80 * Math.cos(angle);
            double cy = centerY + 80 * Math.sin(angle);
            for (int i = 0; i < 50; i++) {
                double a = 2 * Math.PI * i / 50;
                double x = cx + 20 * Math.cos(a);
                double y = cy + 20 * Math.sin(a);
                pattern.add(new ScottAlgorithm.Point2D(x, y));
            }
        }

        ImagePixelLoader.ColorInfo patternColor = new ImagePixelLoader.ColorInfo(128, 0, 128);
        ScottAlgorithm.ProcessingResult patternResult = scott.processBoundaryWithColor(pattern, patternColor);
        visualCortex.generateVisual(patternResult, "geometric_pattern.png");
        visualCortex.generateVisualWithTexture(patternResult, "geometric_pattern_textured.png");
        System.out.println("    Pattern: " + patternResult.originalPoints + " → " + patternResult.simplifiedPoints + " points");

        oracle.processQuery("geometric pattern with phi-harmonic arrangement");
    }

    private static void generateComplexStar(ScottAlgorithm scott, VisualCortex visualCortex,
                                          fraymus.oracle.PhiEmergenceEngine oracle) throws IOException {
        // 5-pointed star with internal details
        List<ScottAlgorithm.Point2D> star = new ArrayList<>();
        double centerX = 150;
        double centerY = 150;
        double outerRadius = 80;
        double innerRadius = 40;

        // Star points
        for (int i = 0; i < 10; i++) {
            double angle = 2 * Math.PI * i / 10 - Math.PI / 2;
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            star.add(new ScottAlgorithm.Point2D(x, y));
        }

        // Add inner pentagon detail
        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI * i / 5 - Math.PI / 2;
            double x = centerX + innerRadius * 0.5 * Math.cos(angle);
            double y = centerY + innerRadius * 0.5 * Math.sin(angle);
            star.add(new ScottAlgorithm.Point2D(x, y));
        }

        ImagePixelLoader.ColorInfo starColor = new ImagePixelLoader.ColorInfo(255, 215, 0);
        ScottAlgorithm.ProcessingResult starResult = scott.processBoundaryWithColor(star, starColor);
        visualCortex.generateVisual(starResult, "complex_star.png");
        visualCortex.generateVisualWithTexture(starResult, "complex_star_textured.png");
        System.out.println("    Star: " + starResult.originalPoints + " → " + starResult.simplifiedPoints + " points");

        oracle.processQuery("complex star with internal geometric details");
    }

    private static List<ScottAlgorithm.Point2D> generateApple(double centerX, double centerY) {
        List<ScottAlgorithm.Point2D> points = new ArrayList<>();
        double radiusX = 40;
        double radiusY = 35;
        
        for (int i = 0; i < 100; i++) {
            double angle = 2 * Math.PI * i / 100;
            double x = centerX + radiusX * Math.cos(angle);
            double y = centerY + radiusY * Math.sin(angle);
            points.add(new ScottAlgorithm.Point2D(x, y));
        }
        
        // Stem
        for (int i = 0; i < 15; i++) {
            double t = i / 15.0;
            double x = centerX - 5 + t * 10;
            double y = centerY - radiusY - 5 - t * 20;
            points.add(new ScottAlgorithm.Point2D(x, y));
        }
        
        return points;
    }

    private static List<ScottAlgorithm.Point2D> generateCircle(double centerX, double centerY, double radius) {
        List<ScottAlgorithm.Point2D> points = new ArrayList<>();
        for (int i = 0; i < 80; i++) {
            double angle = 2 * Math.PI * i / 80;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            points.add(new ScottAlgorithm.Point2D(x, y));
        }
        return points;
    }

    private static List<ScottAlgorithm.Point2D> generateBanana(double centerX, double centerY) {
        List<ScottAlgorithm.Point2D> points = new ArrayList<>();
        
        // Curved banana shape
        for (int i = 0; i < 80; i++) {
            double t = i / 80.0;
            double x = centerX - 30 + t * 60 + Math.sin(t * Math.PI) * 10;
            double y = centerY + Math.sin(t * Math.PI) * 15;
            points.add(new ScottAlgorithm.Point2D(x, y));
        }
        
        // Add thickness
        for (int i = 0; i < 80; i++) {
            double t = i / 80.0;
            double x = centerX - 30 + t * 60 + Math.sin(t * Math.PI) * 10;
            double y = centerY + Math.sin(t * Math.PI) * 15 + 10;
            points.add(new ScottAlgorithm.Point2D(x, y));
        }
        
        return points;
    }
}
