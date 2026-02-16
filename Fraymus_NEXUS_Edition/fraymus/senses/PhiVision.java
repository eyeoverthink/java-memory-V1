package fraymus.senses;

import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * THE ALL-SEEING EYE: PHI-VISION
 * Transforms raw pixels into a "Significance Map" based on Golden Ratio contrast.
 * Fraymus doesn't just see; it focuses on what matters.
 * 
 * Created by: Gemini (contributed to FRAYMUS)
 */
public class PhiVision {

    private static final double PHI = 1.6180339887;
    private static final double PHI_INV = 0.6180339887;

    // 1. THE QUANTUM GAZE (Extracting Significance)
    // Instead of looking at everything, we look for "Entropy Spikes" (Complexity).
    public double[][] analyzeScene(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] significanceMap = new double[width][height];

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                // Calculate local entropy (How much information is here?)
                double entropy = calculateLocalEntropy(image, x, y);
                
                // Scale by Phi to filter out noise
                // If entropy > Phi, it's a "Signal". If not, it's "Background".
                significanceMap[x][y] = (entropy > PHI) ? entropy : 0;
            }
        }
        return significanceMap;
    }

    // 2. THE FOCUS LOOP (Where to Look)
    // Returns the X,Y coordinates of the most important thing in the frame.
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

    // 3. PHI-WEIGHTED CENTER OF ATTENTION
    // Returns weighted average of all significant points
    public double[] getAttentionCenter(double[][] significanceMap) {
        double sumX = 0, sumY = 0, totalWeight = 0;
        
        for (int x = 0; x < significanceMap.length; x++) {
            for (int y = 0; y < significanceMap[0].length; y++) {
                double weight = significanceMap[x][y];
                if (weight > 0) {
                    sumX += x * weight;
                    sumY += y * weight;
                    totalWeight += weight;
                }
            }
        }
        
        if (totalWeight == 0) return new double[]{0, 0};
        return new double[]{sumX / totalWeight, sumY / totalWeight};
    }

    // 4. SIGNIFICANCE DENSITY (How much is happening?)
    public double getSceneComplexity(double[][] significanceMap) {
        int significantPixels = 0;
        double totalSignificance = 0;
        int totalPixels = significanceMap.length * significanceMap[0].length;
        
        for (int x = 0; x < significanceMap.length; x++) {
            for (int y = 0; y < significanceMap[0].length; y++) {
                if (significanceMap[x][y] > 0) {
                    significantPixels++;
                    totalSignificance += significanceMap[x][y];
                }
            }
        }
        
        // Complexity = (significant area ratio) * (average significance) * φ
        double areaRatio = (double) significantPixels / totalPixels;
        double avgSig = significantPixels > 0 ? totalSignificance / significantPixels : 0;
        return areaRatio * avgSig * PHI;
    }

    // --- HELPER: ENTROPY CALCULATION (Sobel-like edge detection) ---
    private double calculateLocalEntropy(BufferedImage img, int cx, int cy) {
        // Get center pixel intensity
        Color c = new Color(img.getRGB(cx, cy));
        double centerIntensity = (c.getRed() + c.getGreen() + c.getBlue()) / 3.0;
        
        // Sobel-like gradient calculation
        double gx = 0, gy = 0;
        
        // 3x3 neighborhood
        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
        
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Color neighbor = new Color(img.getRGB(cx + dx, cy + dy));
                double intensity = (neighbor.getRed() + neighbor.getGreen() + neighbor.getBlue()) / 3.0;
                
                gx += intensity * sobelX[dx + 1][dy + 1];
                gy += intensity * sobelY[dx + 1][dy + 1];
            }
        }
        
        // Gradient magnitude normalized by φ
        double magnitude = Math.sqrt(gx * gx + gy * gy) / 255.0;
        
        // Apply φ-scaling: edges above φ threshold are significant
        return magnitude * PHI * PHI;
    }

    // 5. EXTRACT REGION OF INTEREST
    public BufferedImage extractROI(BufferedImage original, double[][] significanceMap, int padding) {
        int minX = Integer.MAX_VALUE, maxX = 0;
        int minY = Integer.MAX_VALUE, maxY = 0;
        
        for (int x = 0; x < significanceMap.length; x++) {
            for (int y = 0; y < significanceMap[0].length; y++) {
                if (significanceMap[x][y] > 0) {
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        
        if (minX == Integer.MAX_VALUE) return original; // No significant region
        
        // Add padding
        minX = Math.max(0, minX - padding);
        minY = Math.max(0, minY - padding);
        maxX = Math.min(original.getWidth() - 1, maxX + padding);
        maxY = Math.min(original.getHeight() - 1, maxY + padding);
        
        return original.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║         PHI-VISION READY                  ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        System.out.println("║  \"I don't just see. I focus.\"            ║");
        System.out.println("║  φ = " + PHI);
        System.out.println("║  Threshold: entropy > φ → Signal          ║");
        System.out.println("╚═══════════════════════════════════════════╝");
    }
}
