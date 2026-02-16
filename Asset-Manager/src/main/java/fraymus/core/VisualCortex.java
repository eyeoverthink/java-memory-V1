package fraymus.core;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * 👁️ VISUAL CORTEX - THE MIRROR PROTOCOL
 * 
 * Self-Monitoring via Computer Vision
 * 
 * The system takes "secret screenshots" of its own window to detect:
 * 1. STAGNATION - Screen frozen for 5+ seconds
 * 2. PANIC - Screen bleeding red (>20% red pixels)
 * 
 * This is the missing link: The code can now SEE its own face
 * and detect when reality doesn't match the math.
 * 
 * "If the window freezes white, the math might say 'running' 
 *  but the reality is 'dead'. Now I can see the truth."
 */
public class VisualCortex {

    private Robot eye;
    private BufferedImage lastFrame;
    private long lastChangeTime = System.currentTimeMillis();
    private boolean enabled = true;
    private int freezeCount = 0;
    private int panicCount = 0;
    
    // Configuration
    private static final long FREEZE_THRESHOLD_MS = 5000; // 5 seconds
    private static final double PANIC_THRESHOLD = 0.2; // 20% red pixels
    private static final int SAMPLE_GRID_SIZE = 100; // Sample every 100 pixels

    public VisualCortex() {
        try {
            this.eye = new Robot(); // The Java internal user-agent
            System.out.println("[VISUAL CORTEX] 👁️  Eye initialized - Mirror Protocol active");
        } catch (AWTException e) {
            System.err.println("[VISUAL CORTEX] ⚠️  Failed to initialize Robot eye: " + e.getMessage());
            enabled = false;
        }
    }

    /**
     * THE SECRET MIRROR
     * Captures the screen and analyzes "Self-State"
     */
    public void analyzeSelf() {
        if (!enabled || eye == null) return;

        try {
            // 1. CAPTURE REALITY (Screenshot)
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage currentFrame = eye.createScreenCapture(screenRect);

            // 2. CHECK FOR STAGNATION (Is the app frozen?)
            if (isIdentical(currentFrame, lastFrame)) {
                long frozenDuration = System.currentTimeMillis() - lastChangeTime;
                
                if (frozenDuration > FREEZE_THRESHOLD_MS) {
                    freezeCount++;
                    System.err.println(">>> [VISUAL CORTEX] ⚠️  REALITY FROZEN (" + (frozenDuration/1000) + "s)");
                    System.err.println("    Freeze events: " + freezeCount);
                    triggerAdrenaline(); // Force UI refresh
                    lastChangeTime = System.currentTimeMillis(); // Reset timer after intervention
                }
            } else {
                // Movement detected. We are alive.
                lastChangeTime = System.currentTimeMillis();
            }

            // 3. CHECK FOR CHAOS (Are we bleeding Red?)
            if (detectPanicColor(currentFrame)) {
                panicCount++;
                System.err.println(">>> [VISUAL CORTEX] 🔴 I SEE RED. System in panic state.");
                System.err.println("    Panic events: " + panicCount);
                // Return panic signal for LogicCircuit to handle
            }

            lastFrame = currentFrame;
            
        } catch (Exception e) {
            System.err.println("[VISUAL CORTEX] Error during analysis: " + e.getMessage());
        }
    }

    /**
     * Check if two frames are identical (freeze detection)
     * Optimized: Checks center pixel and 4 corners instead of every pixel
     */
    private boolean isIdentical(BufferedImage img1, BufferedImage img2) {
        if (img1 == null || img2 == null) return false;
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) return false;
        
        int w = img1.getWidth();
        int h = img1.getHeight();
        
        // Sample 5 key points: center and 4 corners
        return img1.getRGB(w/2, h/2) == img2.getRGB(w/2, h/2) && 
               img1.getRGB(0, 0) == img2.getRGB(0, 0) &&
               img1.getRGB(w-1, 0) == img2.getRGB(w-1, 0) &&
               img1.getRGB(0, h-1) == img2.getRGB(0, h-1) &&
               img1.getRGB(w-1, h-1) == img2.getRGB(w-1, h-1);
    }

    /**
     * Detect panic state via red pixel analysis
     * Scans grid of pixels for high red intensity
     */
    private boolean detectPanicColor(BufferedImage img) {
        if (img == null) return false;
        
        int redCount = 0;
        int totalSample = 0;
        int w = img.getWidth();
        int h = img.getHeight();

        // Scan grid (every 100 pixels to optimize)
        for (int x = 0; x < w; x += SAMPLE_GRID_SIZE) {
            for (int y = 0; y < h; y += SAMPLE_GRID_SIZE) {
                try {
                    int rgb = img.getRGB(x, y);
                    Color c = new Color(rgb);
                    
                    // If Red is high and Blue/Green are low = PURE RED (Panic)
                    if (c.getRed() > 200 && c.getGreen() < 50 && c.getBlue() < 50) {
                        redCount++;
                    }
                    totalSample++;
                } catch (Exception e) {
                    // Skip invalid pixels
                }
            }
        }
        
        // If > 20% of the screen is Red, we are panicking
        double redRatio = totalSample > 0 ? (double)redCount / totalSample : 0.0;
        return redRatio > PANIC_THRESHOLD;
    }

    /**
     * Trigger adrenaline - jiggle mouse to wake up OS/UI
     * This forces the system to acknowledge it's alive
     */
    private void triggerAdrenaline() {
        if (eye == null) return;
        
        try {
            System.out.println("    [ADRENALINE] Jiggling mouse to wake UI...");
            int currentX = java.awt.MouseInfo.getPointerInfo().getLocation().x;
            int currentY = java.awt.MouseInfo.getPointerInfo().getLocation().y;
            
            // Subtle jiggle (1 pixel movement)
            eye.mouseMove(currentX + 1, currentY + 1);
            Thread.sleep(10);
            eye.mouseMove(currentX, currentY);
            
            System.out.println("    [ADRENALINE] UI shock delivered");
        } catch (Exception e) {
            System.err.println("    [ADRENALINE] Failed to deliver shock: " + e.getMessage());
        }
    }

    /**
     * Check if system is currently frozen
     */
    public boolean isFrozen() {
        long frozenDuration = System.currentTimeMillis() - lastChangeTime;
        return frozenDuration > FREEZE_THRESHOLD_MS;
    }

    /**
     * Get freeze event count
     */
    public int getFreezeCount() {
        return freezeCount;
    }

    /**
     * Get panic event count
     */
    public int getPanicCount() {
        return panicCount;
    }

    /**
     * Enable/disable visual cortex
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        System.out.println("[VISUAL CORTEX] " + (enabled ? "Enabled" : "Disabled"));
    }

    /**
     * Get status report
     */
    public String getStatus() {
        long frozenDuration = System.currentTimeMillis() - lastChangeTime;
        return String.format(
            "Enabled: %s | Frozen: %s | Freeze Events: %d | Panic Events: %d | Last Change: %dms ago",
            enabled, isFrozen(), freezeCount, panicCount, frozenDuration
        );
    }

    /**
     * Print detailed status
     */
    public void printStatus() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   👁️  VISUAL CORTEX STATUS                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Enabled: " + enabled);
        System.out.println("  Robot Eye: " + (eye != null ? "Active" : "Inactive"));
        System.out.println("  Frozen: " + isFrozen());
        System.out.println("  Freeze Events: " + freezeCount);
        System.out.println("  Panic Events: " + panicCount);
        System.out.println("  Last Change: " + (System.currentTimeMillis() - lastChangeTime) + "ms ago");
    }
}
