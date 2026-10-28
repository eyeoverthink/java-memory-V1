package fraymus;

import fraymus.vision.ScottAlgorithm;
import fraymus.vision.ImagePixelLoader;
import fraymus.vision.VisualCortex;
import java.io.IOException;

public class RealImageDemo {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java fraymus.RealImageDemo <image_path>");
            return;
        }

        String imagePath = args[0];
        System.out.println("Processing: " + imagePath);

        try {
            ScottAlgorithm scott = new ScottAlgorithm(2.0, 10, true);
            VisualCortex visualCortex = new VisualCortex("build/visual_output/");
            visualCortex.initialize();
            fraymus.oracle.PhiEmergenceEngine oracle = new fraymus.oracle.PhiEmergenceEngine();
            oracle.start();

            ImagePixelLoader.ColorInfo colorInfo = ImagePixelLoader.extractColorInfo(imagePath);
            System.out.println("Color: " + colorInfo);

            ScottAlgorithm.ProcessingResult result = scott.processImageFile(imagePath);
            System.out.println("Points: " + result.originalPoints + " -> " + result.simplifiedPoints);
            System.out.println("Reduction: " + String.format("%.1f%%", result.reduction * 100));
            System.out.println("Signature: " + result.signature);

            String baseName = imagePath.substring(imagePath.lastIndexOf('\\') + 1).replaceFirst("[.][^.]+$", "");
            visualCortex.generateVisual(result, baseName + "_basic.png");
            visualCortex.generateVisualWithTexture(result, baseName + "_textured.png");
            System.out.println("Generated: " + baseName + "_basic.png, " + baseName + "_textured.png");

            oracle.stop();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
