package fraymus.signals;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GLYPH-STREAM: EMOJI STEGANOGRAPHY
 * 
 * "The letters can hold data."
 * 
 * Hides binary data inside invisible Zero-Width characters.
 * 
 * Standard communication: Visible text only
 * Glyph-Stream: Visible text + invisible payload
 * 
 * How it works:
 * 1. Convert secret data to binary
 * 2. Map binary to invisible Unicode characters
 *    - 0 → Zero-Width Space (U+200B)
 *    - 1 → Zero-Width Non-Joiner (U+200C)
 * 3. Inject invisible characters into emoji string
 * 4. Result: Looks normal, contains hidden data
 * 
 * Example:
 * Visible: "🚀✨🌕"
 * Hidden: "ACTIVATE_CLOAK|37.8N,122.4W"
 * 
 * Use cases:
 * - Covert commands via social media
 * - Dead drops in public forums
 * - Air-gap bridging via screenshots
 * - Stealth communication
 * 
 * WARNING: This is powerful. Use responsibly.
 */
public class GlyphCoder {

    // THE INVISIBLE INK
    // Map binary 0 and 1 to invisible Unicode characters
    private static final String ZERO = "\u200B"; // Zero-Width Space
    private static final String ONE = "\u200C";  // Zero-Width Non-Joiner
    private static final String SEPARATOR = "\u200D"; // Zero-Width Joiner (marker)
    
    // Emoji library for cover text
    private static final String[] COVER_EMOJIS = {
        "🌻", "🌞", "🚀", "✨", "🌕", "🔥", "💎", "⚡", "🌊", "🎯",
        "🎨", "🎭", "🎪", "🎬", "🎮", "🎲", "🎰", "🎳", "🎸", "🎺"
    };
    
    private Random random = new Random();
    
    /**
     * Inject data into emoji string
     * 
     * Hides secret data inside visible emoji using zero-width characters.
     * 
     * @param visibleEmoji Cover emoji string (e.g., "🌻🌞")
     * @param secretData Secret data to hide
     * @return Infected emoji string (looks identical, contains payload)
     */
    public String injectData(String visibleEmoji, String secretData) {
        if (visibleEmoji == null || visibleEmoji.isEmpty()) {
            throw new IllegalArgumentException("Visible emoji cannot be empty");
        }
        
        if (secretData == null || secretData.isEmpty()) {
            return visibleEmoji; // No data to hide
        }
        
        System.out.println("\n🌊⚡ GLYPH INJECTION");
        System.out.println("   Cover: " + visibleEmoji);
        System.out.println("   Payload: " + secretData);
        System.out.println("   Payload length: " + secretData.length() + " chars");
        
        StringBuilder hiddenBits = new StringBuilder();
        
        // Add separator marker (signals start of payload)
        hiddenBits.append(SEPARATOR);
        
        // Convert secret string → binary → invisible characters
        for (char c : secretData.toCharArray()) {
            // Convert char to 8-bit binary
            String binary = String.format("%8s", Integer.toBinaryString(c))
                                  .replace(' ', '0');
            
            // Convert binary to invisible characters
            for (char bit : binary.toCharArray()) {
                hiddenBits.append(bit == '0' ? ZERO : ONE);
            }
        }
        
        // Add end marker
        hiddenBits.append(SEPARATOR);
        
        // Inject invisible string after first emoji
        // This preserves emoji appearance while hiding data
        int injectionPoint = findEmojiEnd(visibleEmoji, 0);
        
        String infected = visibleEmoji.substring(0, injectionPoint) + 
                         hiddenBits.toString() + 
                         visibleEmoji.substring(injectionPoint);
        
        System.out.println("   Invisible bits: " + (hiddenBits.length() - 2)); // -2 for markers
        System.out.println("   ✓ Injection complete");
        System.out.println();
        
        return infected;
    }

    /**
     * Extract data from infected emoji
     * 
     * Scans for zero-width characters and decodes hidden payload.
     * 
     * @param infectedEmoji Emoji string potentially containing payload
     * @return Extracted secret data (empty if none found)
     */
    public String extractData(String infectedEmoji) {
        if (infectedEmoji == null || infectedEmoji.isEmpty()) {
            return "";
        }
        
        StringBuilder binary = new StringBuilder();
        boolean inPayload = false;
        
        // Scan for invisible characters
        for (int i = 0; i < infectedEmoji.length(); i++) {
            String s = String.valueOf(infectedEmoji.charAt(i));
            
            if (s.equals(SEPARATOR)) {
                inPayload = !inPayload; // Toggle on/off
            } else if (inPayload) {
                if (s.equals(ZERO)) {
                    binary.append("0");
                } else if (s.equals(ONE)) {
                    binary.append("1");
                }
            }
        }
        
        if (binary.length() == 0) {
            return ""; // No payload found
        }
        
        System.out.println("\n🌊⚡ GLYPH EXTRACTION");
        System.out.println("   Source: " + getVisibleOnly(infectedEmoji));
        System.out.println("   Invisible bits found: " + binary.length());
        
        // Convert binary → text
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 8) {
            if (i + 8 <= binary.length()) {
                String byteStr = binary.substring(i, i + 8);
                char c = (char) Integer.parseInt(byteStr, 2);
                output.append(c);
            }
        }
        
        String extracted = output.toString();
        System.out.println("   Payload: " + extracted);
        System.out.println("   ✓ Extraction complete");
        System.out.println();
        
        return extracted;
    }
    
    /**
     * Generate cover emoji with hidden payload
     * 
     * Creates innocent-looking emoji string containing secret data.
     * 
     * @param secretData Secret data to hide
     * @param emojiCount Number of cover emojis to use
     * @return Infected emoji string
     */
    public String generateCover(String secretData, int emojiCount) {
        StringBuilder cover = new StringBuilder();
        
        // Generate random emoji cover
        for (int i = 0; i < emojiCount; i++) {
            cover.append(COVER_EMOJIS[random.nextInt(COVER_EMOJIS.length)]);
        }
        
        // Inject payload
        return injectData(cover.toString(), secretData);
    }
    
    /**
     * Check if string contains hidden payload
     * 
     * @param text Text to check
     * @return True if payload detected
     */
    public boolean containsPayload(String text) {
        if (text == null) return false;
        
        // Look for separator markers
        return text.contains(SEPARATOR);
    }
    
    /**
     * Get visible-only text (strip invisible characters)
     * 
     * @param text Text with potential invisible characters
     * @return Visible text only
     */
    public String getVisibleOnly(String text) {
        if (text == null) return "";
        
        return text.replace(ZERO, "")
                   .replace(ONE, "")
                   .replace(SEPARATOR, "");
    }
    
    /**
     * Get payload info without extracting
     * 
     * @param infectedEmoji Emoji string
     * @return Payload info (size, presence)
     */
    public PayloadInfo analyzePayload(String infectedEmoji) {
        if (infectedEmoji == null || !containsPayload(infectedEmoji)) {
            return new PayloadInfo(false, 0, 0);
        }
        
        int bitCount = 0;
        boolean inPayload = false;
        
        for (int i = 0; i < infectedEmoji.length(); i++) {
            String s = String.valueOf(infectedEmoji.charAt(i));
            
            if (s.equals(SEPARATOR)) {
                inPayload = !inPayload;
            } else if (inPayload && (s.equals(ZERO) || s.equals(ONE))) {
                bitCount++;
            }
        }
        
        int byteCount = bitCount / 8;
        return new PayloadInfo(true, bitCount, byteCount);
    }
    
    /**
     * Find end of emoji character
     * 
     * Emojis can be multi-byte sequences. This finds the end.
     * 
     * @param text Text containing emoji
     * @param start Start position
     * @return Position after emoji
     */
    private int findEmojiEnd(String text, int start) {
        // Simple heuristic: emoji are typically 1-4 code points
        // Find next non-emoji character or end of string
        int pos = start;
        int codePoints = 0;
        
        while (pos < text.length() && codePoints < 4) {
            int codePoint = text.codePointAt(pos);
            pos += Character.charCount(codePoint);
            codePoints++;
            
            // Stop if we hit ASCII or common punctuation
            if (codePoint < 0x1F000) break;
        }
        
        return pos;
    }
    
    /**
     * Payload info data class
     */
    public static class PayloadInfo {
        public final boolean present;
        public final int bitCount;
        public final int byteCount;
        
        public PayloadInfo(boolean present, int bitCount, int byteCount) {
            this.present = present;
            this.bitCount = bitCount;
            this.byteCount = byteCount;
        }
        
        @Override
        public String toString() {
            if (!present) return "No payload";
            return String.format("Payload: %d bits (%d bytes)", bitCount, byteCount);
        }
    }
}
