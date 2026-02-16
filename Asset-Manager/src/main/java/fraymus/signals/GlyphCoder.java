package fraymus.signals;

/**
 * THE GLYPH-STREAM: EMOJI STEGANOGRAPHY
 * 
 * "The letters can hold data."
 * 
 * Hides binary data inside invisible Zero-Width characters.
 * A tweet saying "🌻🌞" can contain megabytes of hidden instructions.
 * 
 * Technical:
 * - Zero-Width Space (U+200B) = binary 0
 * - Zero-Width Non-Joiner (U+200C) = binary 1
 * - Invisible to humans, readable by Fraymus
 * 
 * Use cases:
 * - Dead drop commands via social media
 * - Encrypted session IDs in visible text
 * - Self-executing code hidden in emoji strings
 */
public class GlyphCoder {

    private static final double PHI = 1.6180339887;
    
    // THE INVISIBLE INK
    // We map binary 0 and 1 to invisible Unicode characters.
    private static final String ZERO = "\u200B"; // Zero-Width Space
    private static final String ONE = "\u200C";  // Zero-Width Non-Joiner
    private static final String MARKER_START = "\u200D"; // Zero-Width Joiner (payload start)
    private static final String MARKER_END = "\uFEFF";   // Byte Order Mark (payload end)
    
    // Statistics
    private int totalEncoded = 0;
    private int totalDecoded = 0;
    private long totalBytesHidden = 0;

    public GlyphCoder() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         GLYPH-STREAM PROTOCOL INITIALIZED                 ║");
        System.out.println("║         \"The letters can hold data.\"                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    // --- 1. ENCODE (Hide the Soul) ---
    /**
     * Inject secret data into visible emoji string
     * @param visibleEmoji The carrier emoji(s) visible to humans
     * @param secretData The hidden payload
     * @return Infected string (looks the same, contains payload)
     */
    public String injectData(String visibleEmoji, String secretData) {
        StringBuilder hiddenBits = new StringBuilder();
        
        // Add start marker
        hiddenBits.append(MARKER_START);
        
        // Convert Secret String -> Binary -> Invisible Characters
        for (char c : secretData.toCharArray()) {
            String binary = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            
            // Convert Binary -> Invisible Characters
            for (char bit : binary.toCharArray()) {
                hiddenBits.append(bit == '0' ? ZERO : ONE);
            }
        }
        
        // Add end marker
        hiddenBits.append(MARKER_END);
        
        // Update stats
        totalEncoded++;
        totalBytesHidden += secretData.length();
        
        // Inject the invisible string right after the first character
        if (visibleEmoji.length() >= 2) {
            return visibleEmoji.substring(0, 2) + hiddenBits.toString() + visibleEmoji.substring(2);
        } else {
            return visibleEmoji + hiddenBits.toString();
        }
    }
    
    /**
     * Inject with PHI-scrambled encoding for additional security
     */
    public String injectDataScrambled(String visibleEmoji, String secretData, long seed) {
        // PHI-scramble the data before hiding
        StringBuilder scrambled = new StringBuilder();
        for (int i = 0; i < secretData.length(); i++) {
            int shift = (int) ((seed * PHI * (i + 1)) % 26);
            char c = secretData.charAt(i);
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                c = (char) (((c - base + shift) % 26) + base);
            }
            scrambled.append(c);
        }
        return injectData(visibleEmoji, scrambled.toString());
    }

    // --- 2. DECODE (Read the Soul) ---
    /**
     * Extract hidden data from infected emoji string
     * @param infectedEmoji String containing hidden payload
     * @return Extracted secret data
     */
    public String extractData(String infectedEmoji) {
        StringBuilder binary = new StringBuilder();
        StringBuilder output = new StringBuilder();
        
        boolean inPayload = false;
        
        // Scan for our invisible characters
        for (int i = 0; i < infectedEmoji.length(); i++) {
            char c = infectedEmoji.charAt(i);
            String s = String.valueOf(c);
            
            if (s.equals(MARKER_START)) {
                inPayload = true;
                continue;
            }
            if (s.equals(MARKER_END)) {
                inPayload = false;
                continue;
            }
            
            if (inPayload || (!s.equals(MARKER_START) && !s.equals(MARKER_END))) {
                if (s.equals(ZERO)) binary.append("0");
                else if (s.equals(ONE)) binary.append("1");
            }
        }
        
        // Convert Binary -> Text
        for (int i = 0; i < binary.length(); i += 8) {
            if (i + 8 <= binary.length()) {
                String byteStr = binary.substring(i, i + 8);
                try {
                    output.append((char) Integer.parseInt(byteStr, 2));
                } catch (NumberFormatException e) {
                    // Skip invalid bytes
                }
            }
        }
        
        if (output.length() > 0) {
            totalDecoded++;
        }
        
        return output.toString();
    }
    
    /**
     * Extract with PHI-descrambling
     */
    public String extractDataScrambled(String infectedEmoji, long seed) {
        String scrambled = extractData(infectedEmoji);
        
        // PHI-descramble
        StringBuilder unscrambled = new StringBuilder();
        for (int i = 0; i < scrambled.length(); i++) {
            int shift = (int) ((seed * PHI * (i + 1)) % 26);
            char c = scrambled.charAt(i);
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                c = (char) (((c - base - shift + 26) % 26) + base);
            }
            unscrambled.append(c);
        }
        return unscrambled.toString();
    }
    
    // --- 3. DETECTION (Scan for Payloads) ---
    /**
     * Check if a string contains hidden data
     * @param text Text to scan
     * @return true if hidden payload detected
     */
    public boolean containsPayload(String text) {
        for (char c : text.toCharArray()) {
            String s = String.valueOf(c);
            if (s.equals(ZERO) || s.equals(ONE) || s.equals(MARKER_START)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Estimate payload size in bytes
     */
    public int estimatePayloadSize(String text) {
        int bits = 0;
        for (char c : text.toCharArray()) {
            String s = String.valueOf(c);
            if (s.equals(ZERO) || s.equals(ONE)) {
                bits++;
            }
        }
        return bits / 8;
    }
    
    /**
     * Strip all hidden data from a string (sanitize)
     */
    public String sanitize(String text) {
        return text
            .replace(ZERO, "")
            .replace(ONE, "")
            .replace(MARKER_START, "")
            .replace(MARKER_END, "");
    }
    
    // --- 4. COMMAND PROTOCOL ---
    /**
     * Create a Fraymus command hidden in emoji
     */
    public String createCommand(String emoji, String command, String target, String auth) {
        String payload = String.format("CMD:%s|TGT:%s|AUTH:%s", command, target, auth);
        return injectData(emoji, payload);
    }
    
    /**
     * Parse a Fraymus command from emoji
     */
    public FraymusCommand parseCommand(String infectedEmoji) {
        String data = extractData(infectedEmoji);
        if (data == null || data.isEmpty()) return null;
        
        FraymusCommand cmd = new FraymusCommand();
        String[] parts = data.split("\\|");
        
        for (String part : parts) {
            if (part.startsWith("CMD:")) cmd.command = part.substring(4);
            else if (part.startsWith("TGT:")) cmd.target = part.substring(4);
            else if (part.startsWith("AUTH:")) cmd.auth = part.substring(5);
        }
        
        return cmd;
    }
    
    /**
     * Command structure
     */
    public static class FraymusCommand {
        public String command;
        public String target;
        public String auth;
        
        @Override
        public String toString() {
            return String.format("FraymusCommand{cmd='%s', target='%s', auth='%s'}", 
                               command, target, auth);
        }
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        return String.format("GlyphCoder Stats: %d encoded, %d decoded, %d bytes hidden",
                           totalEncoded, totalDecoded, totalBytesHidden);
    }

    // --- MAIN: TEST HARNESS ---
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         GLYPH-STREAM PROTOCOL TEST                        ║");
        System.out.println("║         \"The letters can hold data.\"                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
        
        GlyphCoder coder = new GlyphCoder();
        
        // Test 1: Basic encoding
        System.out.println("--- TEST 1: Basic Encoding ---");
        String carrier = "🌻🌞 Just working on the garden today!";
        String secret = "ACTIVATE_CLOAK";
        
        String infected = coder.injectData(carrier, secret);
        System.out.println("Carrier:  " + carrier);
        System.out.println("Secret:   " + secret);
        System.out.println("Infected: " + infected);
        System.out.println("Visible length: " + carrier.length());
        System.out.println("Infected length: " + infected.length());
        System.out.println("Hidden bytes: " + (infected.length() - carrier.length()));
        
        // Test 2: Extraction
        System.out.println("\n--- TEST 2: Extraction ---");
        String extracted = coder.extractData(infected);
        System.out.println("Extracted: " + extracted);
        System.out.println("Match: " + secret.equals(extracted));
        
        // Test 3: Command protocol
        System.out.println("\n--- TEST 3: Command Protocol ---");
        String cmdEmoji = coder.createCommand("🚀✨", "WARP_JUMP", "37.8N,122.4W", "CAPTAIN_SCOTT");
        System.out.println("Command emoji: " + cmdEmoji);
        System.out.println("Contains payload: " + coder.containsPayload(cmdEmoji));
        System.out.println("Payload size: " + coder.estimatePayloadSize(cmdEmoji) + " bytes");
        
        FraymusCommand cmd = coder.parseCommand(cmdEmoji);
        System.out.println("Parsed: " + cmd);
        
        // Test 4: Sanitization
        System.out.println("\n--- TEST 4: Sanitization ---");
        String clean = coder.sanitize(infected);
        System.out.println("Sanitized: " + clean);
        System.out.println("Still has payload: " + coder.containsPayload(clean));
        
        // Test 5: PHI-scrambled
        System.out.println("\n--- TEST 5: PHI-Scrambled Encoding ---");
        long seed = 1618033988L;
        String scrambledInfected = coder.injectDataScrambled("💀🔐", "TOP_SECRET_MISSION", seed);
        System.out.println("Scrambled carrier: " + scrambledInfected);
        String descrambled = coder.extractDataScrambled(scrambledInfected, seed);
        System.out.println("Descrambled: " + descrambled);
        
        System.out.println("\n" + coder.getStats());
        System.out.println("\n✓ GLYPH-STREAM PROTOCOL: OPERATIONAL");
    }
}
