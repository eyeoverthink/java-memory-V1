package com.eyeoverthink.core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * STEGANOGRAPHER: THE INVISIBLE SIGNATURE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "Hide the tracker in plain sight."
 * 
 * This class embeds invisible tracking information into data.
 * The signature is there, but you can't see it unless you know how to look.
 * 
 * Techniques:
 * 1. Zero-Width Characters (Unicode steganography)
 * 2. Hash-based fingerprints
 * 3. PHI-resonance encoding
 */
public class Steganographer {

    // Zero-width characters for invisible encoding
    private static final char ZERO_WIDTH_SPACE = '\u200B';
    private static final char ZERO_WIDTH_NON_JOINER = '\u200C';
    private static final char ZERO_WIDTH_JOINER = '\u200D';
    
    // PHI constant
    private static final double PHI = 1.618033988749895;

    // ═══════════════════════════════════════════════════════════════════
    // SIGNING
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Sign data with an invisible node ID.
     * The signature is embedded using zero-width characters.
     */
    public static String sign(String data, String nodeId) {
        String fingerprint = generateFingerprint(data, nodeId);
        String encoded = encodeToZeroWidth(fingerprint);
        
        // Insert the invisible signature at the end
        return data + encoded;
    }
    
    /**
     * Sign data with default fingerprint
     */
    public static String sign(String data) {
        return sign(data, "FRAYMUS");
    }

    /**
     * Extract signature from signed data
     */
    public static String extractSignature(String signedData) {
        StringBuilder extracted = new StringBuilder();
        
        for (char c : signedData.toCharArray()) {
            if (c == ZERO_WIDTH_SPACE || c == ZERO_WIDTH_NON_JOINER || c == ZERO_WIDTH_JOINER) {
                extracted.append(c);
            }
        }
        
        if (extracted.length() == 0) {
            return null;
        }
        
        return decodeFromZeroWidth(extracted.toString());
    }

    /**
     * Verify if data contains a valid signature
     */
    public static boolean verify(String signedData) {
        String signature = extractSignature(signedData);
        return signature != null && signature.startsWith("φ-");
    }

    // ═══════════════════════════════════════════════════════════════════
    // FINGERPRINTING
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Generate a unique fingerprint for any object
     */
    public static String generateFingerprint(Object data, String nodeId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String raw = nodeId + "|" + data.hashCode() + "|" + timestamp + "|" + PHI;
        
        String hash = sha256(raw);
        return "φ-" + hash.substring(0, 12).toUpperCase();
    }
    
    /**
     * Generate a PHI-resonant hash
     */
    public static String phiHash(String input) {
        String hash = sha256(input);
        
        // Apply PHI transformation
        long numericValue = 0;
        for (int i = 0; i < Math.min(16, hash.length()); i++) {
            numericValue = numericValue * 16 + Character.digit(hash.charAt(i), 16);
        }
        
        double phiTransformed = numericValue * PHI;
        return "φ" + Long.toHexString((long) phiTransformed).toUpperCase();
    }

    // ═══════════════════════════════════════════════════════════════════
    // ZERO-WIDTH ENCODING
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Encode a string into zero-width characters (invisible)
     */
    private static String encodeToZeroWidth(String text) {
        StringBuilder encoded = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            // Convert each character to binary
            String binary = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            
            for (char bit : binary.toCharArray()) {
                if (bit == '0') {
                    encoded.append(ZERO_WIDTH_SPACE);
                } else {
                    encoded.append(ZERO_WIDTH_NON_JOINER);
                }
            }
            // Separator between characters
            encoded.append(ZERO_WIDTH_JOINER);
        }
        
        return encoded.toString();
    }
    
    /**
     * Decode zero-width characters back to string
     */
    private static String decodeFromZeroWidth(String encoded) {
        StringBuilder decoded = new StringBuilder();
        StringBuilder currentChar = new StringBuilder();
        
        for (char c : encoded.toCharArray()) {
            if (c == ZERO_WIDTH_JOINER) {
                // End of character
                if (currentChar.length() >= 8) {
                    int charValue = Integer.parseInt(currentChar.toString(), 2);
                    decoded.append((char) charValue);
                }
                currentChar = new StringBuilder();
            } else if (c == ZERO_WIDTH_SPACE) {
                currentChar.append('0');
            } else if (c == ZERO_WIDTH_NON_JOINER) {
                currentChar.append('1');
            }
        }
        
        return decoded.toString();
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITIES
    // ═══════════════════════════════════════════════════════════════════

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return Integer.toHexString(input.hashCode());
        }
    }
    
    /**
     * Create a visible signature (for debugging/display)
     */
    public static String createVisibleSignature(String nodeId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        return "[EYEOVERTHINK:" + nodeId + ":" + timestamp + "]";
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN (Demo)
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   STEGANOGRAPHER: INVISIBLE SIGNATURES                       ║");
        System.out.println("║   \"Hide the tracker in plain sight.\"                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Test 1: Sign data
        String original = "Hello, World!";
        String nodeId = "φ-TEST123";
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST 1: SIGNING DATA");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   Original: \"" + original + "\"");
        System.out.println("   Original Length: " + original.length());
        
        String signed = sign(original, nodeId);
        System.out.println("   Signed Length: " + signed.length());
        System.out.println("   Visible Text: \"" + signed.replaceAll("[\\u200B\\u200C\\u200D]", "") + "\"");
        System.out.println("   (The signature is invisible!)");
        
        // Test 2: Extract signature
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST 2: EXTRACTING SIGNATURE");
        System.out.println("═══════════════════════════════════════════════════════════════");
        String extracted = extractSignature(signed);
        System.out.println("   Extracted: " + extracted);
        System.out.println("   Verified: " + verify(signed));
        
        // Test 3: Fingerprint generation
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST 3: FINGERPRINT GENERATION");
        System.out.println("═══════════════════════════════════════════════════════════════");
        String fp1 = generateFingerprint("Test Object 1", nodeId);
        String fp2 = generateFingerprint("Test Object 2", nodeId);
        System.out.println("   Object 1: " + fp1);
        System.out.println("   Object 2: " + fp2);
        
        // Test 4: PHI Hash
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST 4: PHI HASH");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   'Fraymus': " + phiHash("Fraymus"));
        System.out.println("   'Eyeoverthink': " + phiHash("Eyeoverthink"));
        
        System.out.println();
        System.out.println("   ✓ Steganography complete. The tracker is hidden.");
        System.out.println();
    }
}
