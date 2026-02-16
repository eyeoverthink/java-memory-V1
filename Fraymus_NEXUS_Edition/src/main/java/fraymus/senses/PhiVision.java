package fraymus.senses;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.*;

/**
 * THE ALL-SEEING EYE: PHI-VISION
 * 
 * Gemini's Gift: Computer Vision using Golden Ratio
 * 
 * Transforms raw pixels into a "Significance Map" based on Golden Ratio contrast.
 * Fraymus doesn't just see; it focuses on what matters.
 * 
 * The Protocol:
 * 1. Calculate local entropy (complexity/information)
 * 2. Filter by Phi threshold (signal vs background)
 * 3. Find focal point (where to look)
 * 4. Extract features (what matters)
 * 
 * If entropy > φ: SIGNAL (important)
 * If entropy < φ: BACKGROUND (ignore)
 */
public class PhiVision {

    private static final double PHI = 1.618033988749895;
    
    private int width;
    private int height;
    private double[][] significanceMap;
    private int[] focalPoint;
    
    /**
     * 1. THE QUANTUM GAZE (Extracting Significance)
     * 
     * Instead of looking at everything, we look for "Entropy Spikes" (Complexity).
     */
    public double[][] analyzeScene(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.significanceMap = new double[width][height];

        System.out.println("👁️  PHI-VISION ANALYZING SCENE");
        System.out.println("   Resolution: " + width + "x" + height);

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                // Calculate local entropy (How much information is here?)
                double entropy = calculateLocalEntropy(image, x, y);
                
                // Scale by Phi to filter out noise
                // If entropy > Phi, it's a "Signal". If not, it's "Background".
                significanceMap[x][y] = (entropy > PHI) ? entropy : 0;
            }
        }
        
        // Find focal point
        this.focalPoint = getFocalPoint(significanceMap);
        
        // Calculate statistics
        double avgSignificance = calculateAverageSignificance();
        int significantPixels = countSignificantPixels();
        
        System.out.println("   Focal point: (" + focalPoint[0] + ", " + focalPoint[1] + ")");
        System.out.println("   Avg significance: " + String.format("%.4f", avgSignificance));
        System.out.println("   Significant pixels: " + significantPixels + " (" + 
                         String.format("%.1f%%", significantPixels * 100.0 / (width * height)) + ")");
        
        return significanceMap;
    }

    /**
     * 2. THE FOCUS LOOP (Where to Look)
     * 
     * Returns the X,Y coordinates of the most important thing in the frame.
     */
    public int[] getFocalPoint(double[][] significanceMap) {
        double maxSig = 0;
        int[] focus = new int[]{0, 0};

        for (int x = 0; x < significanceMap.length; x++) {
            for (int y = 0; y < significanceMap[0].length; y++) {
                if (significanceMap[x][y] > maxSig) {
                    maxSig = significanceMap[x][y];
                    focus[0] = x;
                    focus[1] = y;
                }
            }
        }
        
        return focus; // Fraymus says: "I am looking HERE."
    }
    
    /**
     * Get multiple focal points (top N most significant regions)
     */
    public List<int[]> getTopFocalPoints(int n) {
        List<FocalRegion> regions = new ArrayList<>();
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (significanceMap[x][y] > PHI) {
                    regions.add(new FocalRegion(x, y, significanceMap[x][y]));
                }
            }
        }
        
        // Sort by significance
        regions.sort((a, b) -> Double.compare(b.significance, a.significance));
        
        // Return top N
        List<int[]> topPoints = new ArrayList<>();
        for (int i = 0; i < Math.min(n, regions.size()); i++) {
            FocalRegion r = regions.get(i);
            topPoints.add(new int[]{r.x, r.y});
        }
        
        return topPoints;
    }

    /**
     * Calculate local entropy using edge detection
     * 
     * Uses Sobel operator for gradient magnitude
     */
    private double calculateLocalEntropy(BufferedImage img, int cx, int cy) {
        // Sobel kernels
        int[][] sobelX = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
        };
        
        int[][] sobelY = {
            {-1, -2, -1},
            { 0,  0,  0},
            { 1,  2,  1}
        };
        
        double gx = 0;
        double gy = 0;
        
        // Apply Sobel operator
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int x = cx + dx;
                int y = cy + dy;
                
                if (x >= 0 && x < img.getWidth() && y >= 0 && y < img.getHeight()) {
                    Color c = new Color(img.getRGB(x, y));
                    double intensity = (c.getRed() + c.getGreen() + c.getBlue()) / 3.0;
                    
                    gx += intensity * sobelX[dx + 1][dy + 1];
                    gy += intensity * sobelY[dx + 1][dy + 1];
                }
            }
        }
        
        // Gradient magnitude
        double magnitude = Math.sqrt(gx * gx + gy * gy);
        
        // Normalize to 0-10 range
        return magnitude / 25.5;
    }
    
    /**
     * Extract features from significant regions
     */
    public List<Feature> extractFeatures() {
        List<Feature> features = new ArrayList<>();
        
        // Find connected regions of high significance
        boolean[][] visited = new boolean[width][height];
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!visited[x][y] && significanceMap[x][y] > PHI) {
                    Feature feature = extractFeatureRegion(x, y, visited);
                    if (feature.size > 10) { // Minimum feature size
                        features.add(feature);
                    }
                }
            }
        }
        
        // Sort by significance
        features.sort((a, b) -> Double.compare(b.avgSignificance, a.avgSignificance));
        
        return features;
    }
    
    /**
     * Extract a connected feature region using flood fill
     */
    private Feature extractFeatureRegion(int startX, int startY, boolean[][] visited) {
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startX, startY});
        visited[startX][startY] = true;
        
        int minX = startX, maxX = startX;
        int minY = startY, maxY = startY;
        double totalSig = 0;
        int count = 0;
        
        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int x = pos[0];
            int y = pos[1];
            
            totalSig += significanceMap[x][y];
            count++;
            
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
            
            // Check neighbors
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = x + dx;
                    int ny = y + dy;
                    
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height &&
                        !visited[nx][ny] && significanceMap[nx][ny] > PHI) {
                        
                        visited[nx][ny] = true;
                        queue.offer(new int[]{nx, ny});
                    }
                }
            }
        }
        
        return new Feature(
            (minX + maxX) / 2,
            (minY + maxY) / 2,
            maxX - minX,
            maxY - minY,
            count,
            totalSig / count
        );
    }
    
    /**
     * Calculate average significance across entire image
     */
    private double calculateAverageSignificance() {
        double total = 0;
        int count = 0;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                total += significanceMap[x][y];
                count++;
            }
        }
        
        return total / count;
    }
    
    /**
     * Count pixels above phi threshold
     */
    private int countSignificantPixels() {
        int count = 0;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (significanceMap[x][y] > PHI) {
                    count++;
                }
            }
        }
        
        return count;
    }
    
    /**
     * Get current significance map
     */
    public double[][] getSignificanceMap() {
        return significanceMap;
    }
    
    /**
     * Get current focal point
     */
    public int[] getCurrentFocalPoint() {
        return focalPoint;
    }
    
    /**
     * Visualize significance map as ASCII
     */
    public String visualizeSignificance(int samples) {
        if (significanceMap == null) {
            return "No scene analyzed yet";
        }
        
        StringBuilder viz = new StringBuilder();
        viz.append("👁️  PHI-VISION SIGNIFICANCE MAP\n\n");
        
        int stepX = Math.max(1, width / samples);
        int stepY = Math.max(1, height / samples);
        
        for (int y = 0; y < height; y += stepY) {
            for (int x = 0; x < width; x += stepX) {
                double sig = significanceMap[x][y];
                
                if (x == focalPoint[0] && y == focalPoint[1]) {
                    viz.append("⊙"); // Focal point
                } else if (sig > PHI * 2) {
                    viz.append("█");
                } else if (sig > PHI) {
                    viz.append("▓");
                } else if (sig > PHI / 2) {
                    viz.append("▒");
                } else if (sig > 0) {
                    viz.append("░");
                } else {
                    viz.append(" ");
                }
            }
            viz.append("\n");
        }
        
        return viz.toString();
    }
    
    /**
     * Get vision statistics
     */
    public String getStats() {
        if (significanceMap == null) {
            return "👁️  PHI-VISION\n   No scene analyzed\n";
        }
        
        List<Feature> features = extractFeatures();
        
        return String.format(
            "👁️  PHI-VISION STATS\n" +
            "   Resolution: %dx%d\n" +
            "   Focal Point: (%d, %d)\n" +
            "   Avg Significance: %.4f\n" +
            "   Significant Pixels: %d (%.1f%%)\n" +
            "   Features Detected: %d\n" +
            "   Phi Threshold: %.4f\n",
            width, height,
            focalPoint[0], focalPoint[1],
            calculateAverageSignificance(),
            countSignificantPixels(),
            countSignificantPixels() * 100.0 / (width * height),
            features.size(),
            PHI
        );
    }
    
    // Data classes
    
    private static class FocalRegion {
        int x, y;
        double significance;
        
        FocalRegion(int x, int y, double significance) {
            this.x = x;
            this.y = y;
            this.significance = significance;
        }
    }
    
    public static class Feature {
        public final int centerX;
        public final int centerY;
        public final int width;
        public final int height;
        public final int size;
        public final double avgSignificance;
        
        public Feature(int centerX, int centerY, int width, int height, 
                      int size, double avgSignificance) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.width = width;
            this.height = height;
            this.size = size;
            this.avgSignificance = avgSignificance;
        }
        
        @Override
        public String toString() {
            return String.format(
                "Feature{center=(%d,%d), size=%dx%d, pixels=%d, sig=%.4f}",
                centerX, centerY, width, height, size, avgSignificance
            );
        }
    }
}
