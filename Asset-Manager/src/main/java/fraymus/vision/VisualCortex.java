package fraymus.vision;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Visual Cortex - Visual Generation from Geometric Signatures
 * 
 * This component generates visual representations from Scott Algorithm output.
 * In production, this would integrate with Dreamscape/LTX-Video for AI-powered video generation.
 * For demonstration, it renders geometric shapes with color using Java graphics.
 */
public class VisualCortex {

    private boolean initialized;
    private String outputPath;

    /**
     * Create a VisualCortex with default output path
     */
    public VisualCortex() {
        this("build/visual_output/");
    }

    /**
     * Create a VisualCortex with specified output path
     * 
     * @param outputPath Directory for generated images
     */
    public VisualCortex(String outputPath) {
        this.outputPath = outputPath;
        this.initialized = false;
    }

    /**
     * Initialize the Visual Cortex
     */
    public void initialize() {
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        this.initialized = true;
        System.out.println("[VisualCortex] Initialized with output path: " + outputPath);
    }

    /**
     * Generate a visual from a processing result
     * 
     * @param result Processing result from Scott Algorithm
     * @param filename Output filename
     * @throws IOException If the image cannot be saved
     */
    public void generateVisual(ScottAlgorithm.ProcessingResult result, String filename) throws IOException {
        if (!initialized) {
            initialize();
        }

        ScottAlgorithm.GeometricSignature sig = result.signature;
        
        // Calculate canvas size
        int padding = 50;
        int width = (int) (sig.width + padding * 2);
        int height = (int) (sig.height + padding * 2);
        
        // Create image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Fill background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        // Set color from signature
        if (sig.hasColor) {
            g2d.setColor(new Color(sig.red, sig.green, sig.blue));
        } else {
            g2d.setColor(Color.BLUE);
        }
        
        // Draw simplified boundary
        List<ScottAlgorithm.Point2D> boundary = result.simplifiedBoundary;
        if (!boundary.isEmpty()) {
            // Find min/max for centering
            double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
            
            for (ScottAlgorithm.Point2D p : boundary) {
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }
            
            // Draw filled shape
            int[] xPoints = new int[boundary.size()];
            int[] yPoints = new int[boundary.size()];
            
            for (int i = 0; i < boundary.size(); i++) {
                ScottAlgorithm.Point2D p = boundary.get(i);
                xPoints[i] = (int) (p.x - minX + padding);
                yPoints[i] = (int) (p.y - minY + padding);
            }
            
            g2d.fillPolygon(xPoints, yPoints, boundary.size());
            
            // Draw outline
            g2d.setColor(Color.BLACK);
            g2d.drawPolygon(xPoints, yPoints, boundary.size());
        }
        
        // Add info text
        g2d.setColor(Color.BLACK);
        g2d.drawString("Vertices: " + sig.vertexCount, 10, height - 40);
        g2d.drawString("Reduction: " + String.format("%.1f%%", result.reduction * 100), 10, height - 25);
        if (sig.hasColor) {
            g2d.drawString("Color: RGB(" + sig.red + "," + sig.green + "," + sig.blue + ")", 10, height - 10);
        }
        
        g2d.dispose();
        
        // Save image
        File outputFile = new File(outputPath + filename);
        ImageIO.write(image, "PNG", outputFile);
        
        System.out.println("[VisualCortex] Generated visual: " + outputFile.getAbsolutePath());
    }

    /**
     * Generate a visual with texture overlay
     * Uses phi-harmonic patterns for natural texture simulation
     * 
     * @param result Processing result from Scott Algorithm
     * @param filename Output filename
     * @throws IOException If the image cannot be saved
     */
    public void generateVisualWithTexture(ScottAlgorithm.ProcessingResult result, String filename) throws IOException {
        if (!initialized) {
            initialize();
        }

        ScottAlgorithm.GeometricSignature sig = result.signature;
        
        // Calculate canvas size
        int padding = 50;
        int width = (int) (sig.width + padding * 2);
        int height = (int) (sig.height + padding * 2);
        
        // Create image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Fill background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        // Set base color from signature
        Color baseColor;
        if (sig.hasColor) {
            baseColor = new Color(sig.red, sig.green, sig.blue);
        } else {
            baseColor = Color.BLUE;
        }
        
        // Draw simplified boundary with phi-harmonic texture
        List<ScottAlgorithm.Point2D> boundary = result.simplifiedBoundary;
        if (!boundary.isEmpty()) {
            // Find min/max for centering
            double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
            
            for (ScottAlgorithm.Point2D p : boundary) {
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }
            
            // Draw filled shape with texture
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Check if point is inside shape (simplified check)
                    double px = x - padding + minX;
                    double py = y - padding + minY;
                    
                    // Phi-harmonic texture pattern
                    double phiPattern = Math.sin((x * ScottAlgorithm.PHI) * 0.1) * 
                                      Math.cos((y * ScottAlgorithm.PHI) * 0.1);
                    
                    // Simple distance check from center
                    double centerX = (minX + maxX) / 2;
                    double centerY = (minY + maxY) / 2;
                    double distance = Math.sqrt(Math.pow(px - centerX, 2) + Math.pow(py - centerY, 2));
                    double maxDistance = Math.sqrt(Math.pow(maxX - minX, 2) + Math.pow(maxY - minY, 2)) / 2;
                    
                    if (distance < maxDistance) {
                        // Apply texture
                        int r = baseColor.getRed();
                        int g = baseColor.getGreen();
                        int b = baseColor.getBlue();
                        
                        // Phi-harmonic variation
                        int variation = (int) (phiPattern * 30);
                        r = Math.max(0, Math.min(255, r + variation));
                        g = Math.max(0, Math.min(255, g + variation));
                        b = Math.max(0, Math.min(255, b + variation));
                        
                        g2d.setColor(new Color(r, g, b));
                        g2d.fillRect(x, y, 1, 1);
                    }
                }
            }
            
            // Draw outline
            g2d.setColor(Color.BLACK);
            int[] xPoints = new int[boundary.size()];
            int[] yPoints = new int[boundary.size()];
            
            for (int i = 0; i < boundary.size(); i++) {
                ScottAlgorithm.Point2D p = boundary.get(i);
                xPoints[i] = (int) (p.x - minX + padding);
                yPoints[i] = (int) (p.y - minY + padding);
            }
            
            g2d.drawPolygon(xPoints, yPoints, boundary.size());
        }
        
        // Add info text
        g2d.setColor(Color.BLACK);
        g2d.drawString("Vertices: " + sig.vertexCount, 10, height - 40);
        g2d.drawString("Reduction: " + String.format("%.1f%%", result.reduction * 100), 10, height - 25);
        g2d.drawString("Texture: Phi-Harmonic", 10, height - 10);
        
        g2d.dispose();
        
        // Save image
        File outputFile = new File(outputPath + filename);
        ImageIO.write(image, "PNG", outputFile);
        
        System.out.println("[VisualCortex] Generated textured visual: " + outputFile.getAbsolutePath());
    }

    /**
     * Get the output path
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * Check if initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
}
