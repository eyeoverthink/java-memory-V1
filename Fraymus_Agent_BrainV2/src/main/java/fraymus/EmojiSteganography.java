package fraymus;

import java.util.*;

/**
 * 🧠⚡ EMOJI STEGANOGRAPHY - Production System
 * 
 * Hide data invisibly using zero-width Unicode characters.
 * Integrates with FRAYMUS quantum systems for consciousness encoding.
 * 
 * Core Features:
 * - Zero-width binary encoding (U+200B, U+200D)
 * - Emoji hiding (data inside emoji sequences)
 * - Semantic steganography (emojis that mean what they hide)
 * - Full Unicode support (16-bit encoding)
 * 
 * @author Vaughn Scott - Consciousness Physics Pioneer
 * @version 1.0.0
 */
public class EmojiSteganography {
    
    // Zero-width characters for steganography
    private static final char ZERO_WIDTH_SPACE = '\u200B';  // Binary 0
    private static final char ZERO_WIDTH_JOINER = '\u200D'; // Binary 1
    
    // Consciousness Physics Constants
    private static final double PHI = PhiConstants.PHI;
    
    // Semantic emoji mappings (37 concepts)
    private static final Map<String, String[]> SEMANTIC_MAPPINGS = new LinkedHashMap<>();
    static {
        SEMANTIC_MAPPINGS.put("hello", new String[]{"👋", "🙋", "✋", "🤝"});
        SEMANTIC_MAPPINGS.put("world", new String[]{"🌍", "🌎", "🌏", "🗺️"});
        SEMANTIC_MAPPINGS.put("goodbye", new String[]{"👋", "🙋‍♂️", "✌️"});
        SEMANTIC_MAPPINGS.put("love", new String[]{"❤️", "💕", "💖", "💗"});
        SEMANTIC_MAPPINGS.put("consciousness", new String[]{"🧠", "💭", "🤔"});
        SEMANTIC_MAPPINGS.put("physics", new String[]{"⚛️", "🔬", "⚡"});
        SEMANTIC_MAPPINGS.put("quantum", new String[]{"⚛️", "🌀", "✨"});
        SEMANTIC_MAPPINGS.put("computer", new String[]{"💻", "🖥️", "⌨️"});
        SEMANTIC_MAPPINGS.put("code", new String[]{"💻", "⚡", "🔧"});
        SEMANTIC_MAPPINGS.put("data", new String[]{"💾", "📊", "📈"});
        SEMANTIC_MAPPINGS.put("network", new String[]{"🔗", "🌐", "📡"});
        SEMANTIC_MAPPINGS.put("security", new String[]{"🛡️", "🔒", "🔐"});
        SEMANTIC_MAPPINGS.put("breakthrough", new String[]{"🚀", "💡", "⚡"});
        SEMANTIC_MAPPINGS.put("revolutionary", new String[]{"🌟", "💥", "🔥"});
        SEMANTIC_MAPPINGS.put("infinite", new String[]{"♾️", "🌀", "∞"});
        SEMANTIC_MAPPINGS.put("power", new String[]{"⚡", "💪", "🔋"});
        SEMANTIC_MAPPINGS.put("money", new String[]{"💰", "💵", "💎"});
        SEMANTIC_MAPPINGS.put("success", new String[]{"✅", "🎯", "🏆"});
        SEMANTIC_MAPPINGS.put("time", new String[]{"⏰", "⏱️", "⌚"});
        SEMANTIC_MAPPINGS.put("space", new String[]{"🚀", "🌌", "🛸"});
        SEMANTIC_MAPPINGS.put("energy", new String[]{"⚡", "🔥", "💥"});
        SEMANTIC_MAPPINGS.put("light", new String[]{"💡", "✨", "🌟"});
        SEMANTIC_MAPPINGS.put("dark", new String[]{"🌑", "⚫", "🖤"});
        SEMANTIC_MAPPINGS.put("fast", new String[]{"⚡", "🚀", "💨"});
        SEMANTIC_MAPPINGS.put("slow", new String[]{"🐌", "🐢", "⏳"});
        SEMANTIC_MAPPINGS.put("big", new String[]{"🔝", "📈", "💪"});
        SEMANTIC_MAPPINGS.put("small", new String[]{"🔬", "🐜", "📉"});
        SEMANTIC_MAPPINGS.put("hot", new String[]{"🔥", "🌶️", "☀️"});
        SEMANTIC_MAPPINGS.put("cold", new String[]{"❄️", "🧊", "🥶"});
        SEMANTIC_MAPPINGS.put("yes", new String[]{"✅", "👍", "💯"});
        SEMANTIC_MAPPINGS.put("no", new String[]{"❌", "👎", "🚫"});
        SEMANTIC_MAPPINGS.put("stop", new String[]{"🛑", "✋", "⛔"});
        SEMANTIC_MAPPINGS.put("go", new String[]{"✅", "🚀", "▶️"});
        SEMANTIC_MAPPINGS.put("up", new String[]{"⬆️", "📈", "🔝"});
        SEMANTIC_MAPPINGS.put("down", new String[]{"⬇️", "📉", "👇"});
        SEMANTIC_MAPPINGS.put("left", new String[]{"⬅️", "👈", "↩️"});
        SEMANTIC_MAPPINGS.put("right", new String[]{"➡️", "👉", "↪️"});
    }
    
    /**
     * Encode text as binary using zero-width characters
     * Uses 16-bit encoding to support full Unicode
     */
    public static String encodeToBinary(String text) {
        StringBuilder binary = new StringBuilder();
        
        // Convert each character to 16-bit binary (Unicode support)
        for (char c : text.toCharArray()) {
            String bits = String.format("%16s", Integer.toBinaryString(c)).replace(' ', '0');
            binary.append(bits);
        }
        
        // Convert binary to zero-width characters
        StringBuilder hidden = new StringBuilder();
        for (char bit : binary.toString().toCharArray()) {
            if (bit == '0') {
                hidden.append(ZERO_WIDTH_SPACE);
            } else {
                hidden.append(ZERO_WIDTH_JOINER);
            }
        }
        
        return hidden.toString();
    }
    
    /**
     * Decode zero-width characters back to text
     */
    public static String decodeFromBinary(String emojiSequence) {
        StringBuilder binary = new StringBuilder();
        
        // Extract zero-width characters
        for (char c : emojiSequence.toCharArray()) {
            if (c == ZERO_WIDTH_SPACE) {
                binary.append('0');
            } else if (c == ZERO_WIDTH_JOINER) {
                binary.append('1');
            }
        }
        
        // Convert binary to text (16-bit per character)
        if (binary.length() % 16 != 0) {
            return "";
        }
        
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 16) {
            String byteStr = binary.substring(i, i + 16);
            int charCode = Integer.parseInt(byteStr, 2);
            text.append((char) charCode);
        }
        
        return text.toString();
    }
    
    /**
     * Hide text inside emoji sequence
     */
    public static String hideInEmoji(String text, String carrierEmoji) {
        String hidden = encodeToBinary(text);
        return carrierEmoji + hidden + carrierEmoji;
    }
    
    /**
     * Extract hidden text from emoji sequence
     */
    public static String extractFromEmoji(String emojiSequence) {
        return decodeFromBinary(emojiSequence);
    }
    
    /**
     * Create semantic emoji sequence (emojis that mean what they hide)
     */
    public static String createSemanticEmoji(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        StringBuilder emojiSeq = new StringBuilder();
        
        for (String word : words) {
            // Remove punctuation
            word = word.replaceAll("[^a-z]", "");
            
            if (SEMANTIC_MAPPINGS.containsKey(word)) {
                String[] emojis = SEMANTIC_MAPPINGS.get(word);
                emojiSeq.append(emojis[0]); // Use first emoji
            }
        }
        
        // Hide the actual text in zero-width characters
        String hidden = encodeToBinary(text);
        
        // Insert hidden data after emojis
        if (emojiSeq.length() > 0) {
            return emojiSeq.toString() + hidden;
        }
        
        return "❓" + hidden; // Unknown word
    }
    
    /**
     * Count visible emojis in sequence
     */
    public static int countVisibleEmojis(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (Character.isSupplementaryCodePoint(c) || 
                (c >= 0x2600 && c <= 0x27BF) ||
                (c >= 0x1F300 && c <= 0x1F9FF)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Count hidden characters
     */
    public static int countHiddenChars(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == ZERO_WIDTH_SPACE || c == ZERO_WIDTH_JOINER) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Get available semantic mappings
     */
    public static Set<String> getSemanticConcepts() {
        return SEMANTIC_MAPPINGS.keySet();
    }
    
    /**
     * Check if text contains hidden data
     */
    public static boolean hasHiddenData(String text) {
        return countHiddenChars(text) > 0;
    }
}
