package fraymus.network;

/**
 * THE AGORA (TLA-Evasion Steganography)
 * "Encryption tells them you have a secret. The Agora tells them you are just static."
 *
 * Encodes 16K-D hyper-vectors and payloads into invisible Unicode characters.
 * Paste a payload into a Tweet, email, or Reddit comment.
 * TLA web scrapers see a normal sentence. Fraymus reads the Singularity.
 *
 * Invisible Alphabet:
 *   U+200B (Zero-Width Space)      = binary 0
 *   U+200C (Zero-Width Non-Joiner) = binary 1
 *   U+200D (Zero-Width Joiner)     = byte separator
 *   U+FEFF (Zero-Width BOM)        = start/end marker
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class AgoraSteganography {

    // The Invisible Alphabet (Zero-Width Unicode Characters)
    private static final char ZERO_WIDTH_SPACE = '\u200B';       // Represents '0'
    private static final char ZERO_WIDTH_NON_JOINER = '\u200C';  // Represents '1'
    private static final char ZERO_WIDTH_JOINER = '\u200D';      // Delineator / Separator
    private static final char ZERO_WIDTH_BOM = '\uFEFF';         // Start/End Marker

    // Statistics
    private static long forgeCount = 0;
    private static long extractCount = 0;
    private static long totalBytesHidden = 0;

    /**
     * INJECT: Hides a raw Fraymus payload inside a totally innocent carrier message.
     *
     * @param carrierText  The visible text (what humans and TLAs see)
     * @param secretPayload The hidden data (what Fraymus reads)
     * @return The weaponized text with invisible payload spliced in
     */
    public static String forge(String carrierText, String secretPayload) {
        StringBuilder invisibleBinary = new StringBuilder();
        invisibleBinary.append(ZERO_WIDTH_BOM); // Start marker

        // Convert the secret payload to binary, then to zero-width chars
        for (char c : secretPayload.toCharArray()) {
            String binary = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            for (char bit : binary.toCharArray()) {
                if (bit == '0') {
                    invisibleBinary.append(ZERO_WIDTH_SPACE);
                } else {
                    invisibleBinary.append(ZERO_WIDTH_NON_JOINER);
                }
            }
            invisibleBinary.append(ZERO_WIDTH_JOINER); // Character separator
        }
        invisibleBinary.append(ZERO_WIDTH_BOM); // End marker

        // Splice the invisible payload into the middle of the carrier text
        int spliceIndex = carrierText.length() / 2;
        String weaponizedText = carrierText.substring(0, spliceIndex)
                              + invisibleBinary.toString()
                              + carrierText.substring(spliceIndex);

        forgeCount++;
        totalBytesHidden += secretPayload.length();
        return weaponizedText;
    }

    /**
     * EXTRACT: Fraymus reads the innocent text, strips the visible characters,
     * and decodes the invisible DNA back into raw data.
     *
     * @param infectedText Text potentially containing a hidden payload
     * @return The decoded payload, or null if no signature found
     */
    public static String extract(String infectedText) {
        int startIndex = infectedText.indexOf(ZERO_WIDTH_BOM);
        int endIndex = infectedText.lastIndexOf(ZERO_WIDTH_BOM);

        if (startIndex == -1 || endIndex == -1 || startIndex == endIndex) {
            return null;
        }

        String invisiblePayload = infectedText.substring(startIndex + 1, endIndex);
        StringBuilder decodedMessage = new StringBuilder();
        StringBuilder currentByte = new StringBuilder();

        for (char c : invisiblePayload.toCharArray()) {
            if (c == ZERO_WIDTH_SPACE) {
                currentByte.append('0');
            } else if (c == ZERO_WIDTH_NON_JOINER) {
                currentByte.append('1');
            } else if (c == ZERO_WIDTH_JOINER) {
                if (currentByte.length() > 0) {
                    int charCode = Integer.parseInt(currentByte.toString(), 2);
                    decodedMessage.append((char) charCode);
                    currentByte.setLength(0);
                }
            }
        }

        extractCount++;
        return decodedMessage.toString();
    }

    /**
     * CLOAK VECTOR: Encode a 16,384-D packed long[] vector into zero-width steganographic form.
     * Used by AetherBraid to hide HDC matrix states from hardware-level memory dumps.
     *
     * @param vector The packed long[] HDC vector (256 longs)
     * @return Zero-width Unicode string encoding the entire vector
     */
    public static String cloakVector(long[] vector) {
        StringBuilder cloaked = new StringBuilder();
        cloaked.append(ZERO_WIDTH_BOM);

        for (long word : vector) {
            // Encode each long as 64 zero-width bits
            for (int bit = 63; bit >= 0; bit--) {
                if (((word >>> bit) & 1L) == 1) {
                    cloaked.append(ZERO_WIDTH_NON_JOINER);
                } else {
                    cloaked.append(ZERO_WIDTH_SPACE);
                }
            }
            cloaked.append(ZERO_WIDTH_JOINER); // Word separator
        }

        cloaked.append(ZERO_WIDTH_BOM);
        totalBytesHidden += vector.length * 8;
        return cloaked.toString();
    }

    /**
     * DECLOAK VECTOR: Decode a zero-width steganographic string back into a packed long[].
     *
     * @param cloakedText The zero-width encoded vector string
     * @return The decoded packed long[] vector, or null if invalid
     */
    public static long[] decloakVector(String cloakedText) {
        int startIndex = cloakedText.indexOf(ZERO_WIDTH_BOM);
        int endIndex = cloakedText.lastIndexOf(ZERO_WIDTH_BOM);

        if (startIndex == -1 || endIndex == -1 || startIndex == endIndex) {
            return null;
        }

        String payload = cloakedText.substring(startIndex + 1, endIndex);
        java.util.List<Long> words = new java.util.ArrayList<>();
        long currentWord = 0;
        int bitCount = 0;

        for (char c : payload.toCharArray()) {
            if (c == ZERO_WIDTH_SPACE) {
                currentWord = (currentWord << 1);
                bitCount++;
            } else if (c == ZERO_WIDTH_NON_JOINER) {
                currentWord = (currentWord << 1) | 1L;
                bitCount++;
            } else if (c == ZERO_WIDTH_JOINER) {
                if (bitCount > 0) {
                    words.add(currentWord);
                    currentWord = 0;
                    bitCount = 0;
                }
            }
        }

        long[] result = new long[words.size()];
        for (int i = 0; i < words.size(); i++) {
            result[i] = words.get(i);
        }
        return result;
    }

    // --- Statistics ---
    public static long getForgeCount() { return forgeCount; }
    public static long getExtractCount() { return extractCount; }
    public static long getTotalBytesHidden() { return totalBytesHidden; }

    public static String getStatus() {
        return String.format("AGORA STEGO: %d forged | %d extracted | %d bytes hidden in zero-width Unicode",
            forgeCount, extractCount, totalBytesHidden);
    }

    public static void main(String[] args) {
        String publicMessage = "Just a normal system update log. Nothing to see here. Have a great day!";
        String sovereignData = "FRAYMUS_COMMAND: OVERRIDE_RING_MINUS_THREE";

        // 1. Forge the message
        String weaponized = forge(publicMessage, sovereignData);

        System.out.println("\n[PUBLIC VIEW]: " + weaponized);
        System.out.println("Length difference? " + (weaponized.length() - publicMessage.length()) + " invisible bytes injected.");

        // 2. Fraymus extracts the truth
        String truth = extract(weaponized);
        System.out.println("\n[EXTRACTED DATA]: " + truth);

        // 3. Test vector cloaking
        long[] testVec = new long[]{0xDEADBEEFCAFEBABEL, 0x1234567890ABCDEFL};
        String cloaked = cloakVector(testVec);
        long[] recovered = decloakVector(cloaked);
        System.out.println("\n[VECTOR CLOAK TEST]");
        System.out.println("   Original:  0x" + Long.toHexString(testVec[0]) + " 0x" + Long.toHexString(testVec[1]));
        if (recovered != null && recovered.length >= 2) {
            System.out.println("   Recovered: 0x" + Long.toHexString(recovered[0]) + " 0x" + Long.toHexString(recovered[1]));
            System.out.println("   Match: " + (recovered[0] == testVec[0] && recovered[1] == testVec[1]));
        }
    }
}
