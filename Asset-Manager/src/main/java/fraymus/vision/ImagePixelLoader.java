package fraymus.vision;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Image Pixel Loader
 * Extracts pixel data from image files for Scott Algorithm processing
 * Supports PNG, JPG, and other common image formats
 */
public class ImagePixelLoader {

    /**
     * Load an image file and extract boundary points
     * 
     * @param imagePath Path to the image file
     * @return List of boundary points from the image
     * @throws IOException If the image cannot be loaded
     */
    public static List<ScottAlgorithm.Point2D> loadImageBoundary(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        BufferedImage image = ImageIO.read(imageFile);
        
        if (image == null) {
            throw new IOException("Unable to load image: " + imagePath);
        }
        
        return extractBoundaryPoints(image);
    }

    /**
     * Extract boundary points from a BufferedImage
     * Uses adaptive edge detection to find the object boundary
     * 
     * @param image The buffered image to process
     * @return List of boundary points
     */
    public static List<ScottAlgorithm.Point2D> extractBoundaryPoints(BufferedImage image) {
        List<ScottAlgorithm.Point2D> boundaryPoints = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Convert to grayscale for edge detection
        int[][] grayscale = new int[width][height];
        int minGray = 255, maxGray = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                grayscale[x][y] = (r + g + b) / 3;
                minGray = Math.min(minGray, grayscale[x][y]);
                maxGray = Math.max(maxGray, grayscale[x][y]);
            }
        }
        
        // Adaptive threshold based on image contrast
        int threshold = (maxGray - minGray) / 4;
        if (threshold < 20) threshold = 20; // Minimum threshold
        
        // Edge detection with adaptive threshold
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int current = grayscale[x][y];
                int right = grayscale[x + 1][y];
                int down = grayscale[x][y + 1];
                int left = grayscale[x - 1][y];
                int up = grayscale[x][y - 1];
                
                // Detect edge (significant brightness change in any direction)
                if (Math.abs(current - right) > threshold || 
                    Math.abs(current - down) > threshold ||
                    Math.abs(current - left) > threshold ||
                    Math.abs(current - up) > threshold) {
                    boundaryPoints.add(new ScottAlgorithm.Point2D(x, y));
                }
            }
        }
        
        // If no edges found, use a simpler approach - add all non-background pixels
        if (boundaryPoints.isEmpty()) {
            int bgThreshold = (minGray + maxGray) / 2;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (Math.abs(grayscale[x][y] - bgThreshold) > threshold) {
                        boundaryPoints.add(new ScottAlgorithm.Point2D(x, y));
                    }
                }
            }
        }
        
        return boundaryPoints;
    }

    /**
     * Extract color information from an image
     * Returns average RGB values
     * 
     * @param imagePath Path to the image file
     * @return ColorInfo with RGB averages
     * @throws IOException If the image cannot be loaded
     */
    public static ColorInfo extractColorInfo(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        BufferedImage image = ImageIO.read(imageFile);
        
        if (image == null) {
            throw new IOException("Unable to load image: " + imagePath);
        }
        
        return extractColorInfo(image);
    }

    /**
     * Extract color information from a BufferedImage
     * 
     * @param image The buffered image to process
     * @return ColorInfo with RGB averages
     */
    public static ColorInfo extractColorInfo(BufferedImage image) {
        long totalR = 0, totalG = 0, totalB = 0;
        int pixelCount = 0;
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                totalR += r;
                totalG += g;
                totalB += b;
                pixelCount++;
            }
        }
        
        if (pixelCount == 0) {
            return new ColorInfo(0, 0, 0);
        }
        
        int avgR = (int) (totalR / pixelCount);
        int avgG = (int) (totalG / pixelCount);
        int avgB = (int) (totalB / pixelCount);
        
        return new ColorInfo(avgR, avgG, avgB);
    }

    /**
     * Generate a simple test image (red circle on white background)
     * 
     * @param width Image width
     * @param height Image height
     * @return BufferedImage with red circle
     */
    public static BufferedImage generateTestImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Fill white background
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, 0xFFFFFF);
            }
        }
        
        // Draw red circle
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 3;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                if (distance <= radius) {
                    image.setRGB(x, y, 0xFF0000); // Red
                }
            }
        }
        
        return image;
    }

    /**
     * Save a BufferedImage to a file
     * 
     * @param image The image to save
     * @param outputPath Path to save the image
     * @throws IOException If the image cannot be saved
     */
    public static void saveImage(BufferedImage image, String outputPath) throws IOException {
        File outputFile = new File(outputPath);
        String format = outputPath.substring(outputPath.lastIndexOf('.') + 1);
        ImageIO.write(image, format, outputFile);
    }

    /**
     * Color information container
     */
    public static class ColorInfo {
        public final int red;
        public final int green;
        public final int blue;
        
        public ColorInfo(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
        
        public String toHex() {
            return String.format("#%02X%02X%02X", red, green, blue);
        }
        
        public double getHue() {
            float[] hsb = java.awt.Color.RGBtoHSB(red, green, blue, null);
            return hsb[0] * 360;
        }
        
        public double getSaturation() {
            float[] hsb = java.awt.Color.RGBtoHSB(red, green, blue, null);
            return hsb[1];
        }
        
        public double getBrightness() {
            float[] hsb = java.awt.Color.RGBtoHSB(red, green, blue, null);
            return hsb[2];
        }
        
        @Override
        public String toString() {
            return String.format("ColorInfo[RGB=(%d,%d,%d), Hex=%s, Hue=%.1f°, Sat=%.2f, Bri=%.2f]",
                red, green, blue, toHex(), getHue(), getSaturation(), getBrightness());
        }
    }
}
