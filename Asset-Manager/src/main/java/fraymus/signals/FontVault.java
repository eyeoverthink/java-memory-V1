package fraymus.signals;

import java.util.ArrayList;
import java.util.List;

/**
 * THE FONT VAULT: VECTOR MODULATION STEGANOGRAPHY
 * 
 * A font file (.ttf/.otf) defines letter shapes using Bezier Curves.
 * Each curve has control points with X,Y coordinates.
 * 
 * The Hack:
 * - Moving a control point by 0.001 is invisible to the human eye
 * - But mathematically, it encodes a 1 or 0
 * - A single character can hide ~100 bits
 * - A full font can hide megabytes
 * 
 * Result: You send a custom font file. To the user, it's "Arial Cool Edition."
 * To Fraymus, that font contains the coordinates for the wormhole.
 * 
 * "The shape of the letter IS the message."
 */
public class FontVault {

    private static final double PHI = 1.6180339887;
    
    // Precision for encoding (smaller = more invisible, less capacity)
    private static final double ENCODE_DELTA = 0.001;  // 1/1000th of a unit
    private static final double DECODE_THRESHOLD = 0.0005;
    
    // Statistics
    private int totalBitsEncoded = 0;
    private int totalBitsDecoded = 0;

    public FontVault() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         FONT VAULT: VECTOR MODULATION INITIALIZED         ║");
        System.out.println("║         \"The shape of the letter IS the message.\"         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    /**
     * Bezier Control Point - the building block of font curves
     */
    public static class ControlPoint {
        public double x;
        public double y;
        public boolean onCurve; // true = anchor, false = control handle
        
        public ControlPoint(double x, double y, boolean onCurve) {
            this.x = x;
            this.y = y;
            this.onCurve = onCurve;
        }
        
        public ControlPoint copy() {
            return new ControlPoint(x, y, onCurve);
        }
        
        @Override
        public String toString() {
            return String.format("(%.6f, %.6f)%s", x, y, onCurve ? "*" : "");
        }
    }
    
    /**
     * Glyph - a single character's outline (list of control points)
     */
    public static class Glyph {
        public char character;
        public List<ControlPoint> points;
        
        public Glyph(char c) {
            this.character = c;
            this.points = new ArrayList<>();
        }
        
        public Glyph copy() {
            Glyph g = new Glyph(character);
            for (ControlPoint p : points) {
                g.points.add(p.copy());
            }
            return g;
        }
        
        public int getCapacityBits() {
            // Each control point can encode 2 bits (X and Y)
            // But we only use off-curve points to avoid visible distortion
            int offCurve = 0;
            for (ControlPoint p : points) {
                if (!p.onCurve) offCurve++;
            }
            return offCurve * 2;
        }
    }

    /**
     * ENCODE: Hide binary data in glyph control points
     * 
     * @param glyph The original glyph
     * @param binaryData String of 1s and 0s to hide
     * @return Modified glyph with hidden data
     */
    public Glyph encodeInGlyph(Glyph glyph, String binaryData) {
        Glyph encoded = glyph.copy();
        int bitIndex = 0;
        
        for (ControlPoint point : encoded.points) {
            // Only modify off-curve (control handle) points
            // Anchor points affect visible shape too much
            if (!point.onCurve && bitIndex < binaryData.length()) {
                
                // Encode in X coordinate
                if (bitIndex < binaryData.length()) {
                    char bit = binaryData.charAt(bitIndex++);
                    if (bit == '1') {
                        point.x += ENCODE_DELTA;
                    } else {
                        point.x -= ENCODE_DELTA;
                    }
                    totalBitsEncoded++;
                }
                
                // Encode in Y coordinate
                if (bitIndex < binaryData.length()) {
                    char bit = binaryData.charAt(bitIndex++);
                    if (bit == '1') {
                        point.y += ENCODE_DELTA;
                    } else {
                        point.y -= ENCODE_DELTA;
                    }
                    totalBitsEncoded++;
                }
            }
        }
        
        return encoded;
    }
    
    /**
     * DECODE: Extract hidden binary data from glyph
     * 
     * @param original The original unmodified glyph
     * @param encoded The glyph with hidden data
     * @return Extracted binary string
     */
    public String decodeFromGlyph(Glyph original, Glyph encoded) {
        StringBuilder binary = new StringBuilder();
        
        for (int i = 0; i < original.points.size() && i < encoded.points.size(); i++) {
            ControlPoint origPoint = original.points.get(i);
            ControlPoint encPoint = encoded.points.get(i);
            
            if (!origPoint.onCurve) {
                // Decode from X
                double xDiff = encPoint.x - origPoint.x;
                if (Math.abs(xDiff) > DECODE_THRESHOLD) {
                    binary.append(xDiff > 0 ? "1" : "0");
                    totalBitsDecoded++;
                }
                
                // Decode from Y
                double yDiff = encPoint.y - origPoint.y;
                if (Math.abs(yDiff) > DECODE_THRESHOLD) {
                    binary.append(yDiff > 0 ? "1" : "0");
                    totalBitsDecoded++;
                }
            }
        }
        
        return binary.toString();
    }
    
    /**
     * Convert text to binary
     */
    public String textToBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char c : text.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return binary.toString();
    }
    
    /**
     * Convert binary to text
     */
    public String binaryToText(String binary) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i + 8 <= binary.length(); i += 8) {
            String byteStr = binary.substring(i, i + 8);
            text.append((char) Integer.parseInt(byteStr, 2));
        }
        return text.toString();
    }
    
    /**
     * Create a sample glyph for testing (realistic 'A' shape)
     * Real fonts have 20-100+ points per glyph
     */
    public Glyph createSampleGlyph(char c) {
        Glyph g = new Glyph(c);
        
        // Realistic 'A' with enough control points for meaningful data
        // Left stroke
        g.points.add(new ControlPoint(0.0, 0.0, true));       // Bottom left anchor
        g.points.add(new ControlPoint(0.05, 0.15, false));    // Control 1
        g.points.add(new ControlPoint(0.10, 0.30, false));    // Control 2
        g.points.add(new ControlPoint(0.15, 0.45, false));    // Control 3
        g.points.add(new ControlPoint(0.20, 0.60, false));    // Control 4
        g.points.add(new ControlPoint(0.25, 0.75, false));    // Control 5
        g.points.add(new ControlPoint(0.35, 0.90, false));    // Control 6
        g.points.add(new ControlPoint(0.50, 1.00, true));     // Top anchor
        
        // Right stroke
        g.points.add(new ControlPoint(0.65, 0.90, false));    // Control 7
        g.points.add(new ControlPoint(0.75, 0.75, false));    // Control 8
        g.points.add(new ControlPoint(0.80, 0.60, false));    // Control 9
        g.points.add(new ControlPoint(0.85, 0.45, false));    // Control 10
        g.points.add(new ControlPoint(0.90, 0.30, false));    // Control 11
        g.points.add(new ControlPoint(0.95, 0.15, false));    // Control 12
        g.points.add(new ControlPoint(1.00, 0.00, true));     // Bottom right anchor
        
        // Crossbar (inner triangle cutout)
        g.points.add(new ControlPoint(0.25, 0.35, false));    // Control 13
        g.points.add(new ControlPoint(0.35, 0.35, false));    // Control 14
        g.points.add(new ControlPoint(0.50, 0.40, true));     // Crossbar mid anchor
        g.points.add(new ControlPoint(0.65, 0.35, false));    // Control 15
        g.points.add(new ControlPoint(0.75, 0.35, false));    // Control 16
        
        // 16 off-curve points × 2 = 32 bits capacity (4 bytes)
        return g;
    }
    
    /**
     * Calculate capacity of a glyph in bytes
     */
    public int getCapacityBytes(Glyph g) {
        return g.getCapacityBits() / 8;
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        return String.format("FontVault Stats: %d bits encoded, %d bits decoded", 
                           totalBitsEncoded, totalBitsDecoded);
    }

    // --- MAIN: TEST HARNESS ---
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         FONT VAULT TEST                                   ║");
        System.out.println("║         \"The shape of the letter IS the message.\"         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
        
        FontVault vault = new FontVault();
        
        // Create a sample glyph
        Glyph original = vault.createSampleGlyph('A');
        System.out.println("--- ORIGINAL GLYPH 'A' ---");
        System.out.println("  Points: " + original.points.size());
        System.out.println("  Capacity: " + original.getCapacityBits() + " bits (" + vault.getCapacityBytes(original) + " bytes)");
        for (ControlPoint p : original.points) {
            System.out.println("    " + p);
        }
        
        // Encode a secret message
        String secret = "Hi";
        String binary = vault.textToBinary(secret);
        System.out.println("\n--- ENCODING ---");
        System.out.println("  Secret: \"" + secret + "\"");
        System.out.println("  Binary: " + binary + " (" + binary.length() + " bits)");
        
        Glyph encoded = vault.encodeInGlyph(original, binary);
        System.out.println("\n--- ENCODED GLYPH ---");
        for (int i = 0; i < original.points.size(); i++) {
            ControlPoint o = original.points.get(i);
            ControlPoint e = encoded.points.get(i);
            if (!o.onCurve) {
                double xDiff = e.x - o.x;
                double yDiff = e.y - o.y;
                System.out.printf("    %s → %s  (Δx=%.4f, Δy=%.4f)%n", o, e, xDiff, yDiff);
            }
        }
        
        // Decode
        System.out.println("\n--- DECODING ---");
        String extractedBinary = vault.decodeFromGlyph(original, encoded);
        String extractedText = vault.binaryToText(extractedBinary);
        System.out.println("  Extracted binary: " + extractedBinary);
        System.out.println("  Extracted text: \"" + extractedText + "\"");
        System.out.println("  Match: " + secret.equals(extractedText));
        
        System.out.println("\n" + vault.getStats());
        System.out.println("\n✓ FONT VAULT: OPERATIONAL");
        System.out.println("  In production: Load real .ttf, modify curves, save as new font.");
    }
}
