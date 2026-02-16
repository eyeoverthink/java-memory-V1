package fraymus.genesis;

import java.util.*;
import java.security.MessageDigest;

/**
 * THE TESSERACT: Folding Time and Space
 * 
 * Philosophy: Why calculate if you have already been there?
 * Function: Creates Wormholes between "Input States" and "Future Outcomes"
 * 
 * This is the Fourth Dimension - non-linear execution.
 * Standard code: Line 1 → Line 2 → Line 3
 * Tesseract code: Line 1 → (Fold) → Line 1,000,000
 */
public class Tesseract {
    
    // The Hypercube: Maps a "Situation Hash" -> "Future Reality"
    // This allows the system to skip execution time
    private static Map<String, Object> wormholes = new HashMap<>();
    private static int foldCount = 0;
    private static int traversalCount = 0;
    private static int hitCount = 0;

    /**
     * FOLD SPACE
     * Create a shortcut. When the system sees 'Input A', 
     * it instantly knows 'Result B' without work.
     */
    public static void fold(Object inputState, Object futureOutcome) {
        String coordinates = calculateGeometry(inputState);
        wormholes.put(coordinates, futureOutcome);
        foldCount++;
        System.out.println(">>> [TESSERACT] Wormhole Opened. Coordinates: " + coordinates.substring(0, 8) + "...");
    }

    /**
     * TRAVERSE
     * Attempt to traverse the fold.
     * Returns the Future if the path exists. Returns NULL if we must walk linearly.
     */
    public static Object traverse(Object inputState) {
        String coordinates = calculateGeometry(inputState);
        traversalCount++;
        
        if (wormholes.containsKey(coordinates)) {
            hitCount++;
            System.out.println(">>> [TESSERACT] TIME FOLDED. Skipping execution...");
            System.out.println("    Hit Rate: " + String.format("%.1f%%", getHitRate() * 100));
            return wormholes.get(coordinates);
        }
        return null; // No shortcut found. Must run linearly.
    }

    /**
     * QUANTUM HASHING
     * Measuring the 'Geometry' of Data
     * We don't just hash the value; we hash the structure (The 'Vibe')
     */
    private static String calculateGeometry(Object state) {
        try {
            String raw = state.toString(); 
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return String.valueOf(state.hashCode());
        }
    }

    /**
     * Get hit rate (cache efficiency)
     */
    public static double getHitRate() {
        if (traversalCount == 0) return 0.0;
        return (double) hitCount / traversalCount;
    }

    /**
     * Get wormhole count
     */
    public static int getWormholeCount() {
        return wormholes.size();
    }

    /**
     * Clear all wormholes
     */
    public static void collapse() {
        wormholes.clear();
        foldCount = 0;
        traversalCount = 0;
        hitCount = 0;
        System.out.println(">>> [TESSERACT] All wormholes collapsed");
    }

    /**
     * Print statistics
     */
    public static void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   TESSERACT STATISTICS                                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Wormholes Created: " + foldCount);
        System.out.println("  Active Wormholes: " + wormholes.size());
        System.out.println("  Traversal Attempts: " + traversalCount);
        System.out.println("  Successful Jumps: " + hitCount);
        System.out.println("  Hit Rate: " + String.format("%.1f%%", getHitRate() * 100));
        System.out.println("  Time Saved: " + hitCount + " executions skipped");
    }
}
