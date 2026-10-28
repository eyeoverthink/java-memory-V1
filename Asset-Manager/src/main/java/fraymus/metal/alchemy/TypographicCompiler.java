package fraymus.metal.alchemy;

import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;
import java.util.BitSet;

/**
 * 🖋️ TYPOGRAPHIC COMPILER (Orthographic Steganography)
 * Encodes raw machine code into the microscopic spacing (kerning) of a font.
 * The text becomes a redundant, executable QR-code equivalent.
 *
 * Encoding: BIT_ZERO = 0.0000f tracking, BIT_ONE = 0.0001f tracking
 * Each character in the carrier text hides one bit of the payload.
 */
public class TypographicCompiler {

    // The sub-pixel shift. Invisible to the human eye.
    private static final float BIT_ZERO = 0.0000f;
    private static final float BIT_ONE  = 0.0001f;

    /**
     * ENCODE: The text becomes the carrier wave.
     * Injects raw Fraymus Assembly instructions into the font spacing.
     */
    public static AttributedString forgeDocument(String carrierText, byte[] machineCode) {
        System.out.println("   [🖋️] Weaving machine code into typography...");

        AttributedString document = new AttributedString(carrierText);
        BitSet payloadBits = BitSet.valueOf(machineCode);

        int bitIndex = 0;
        int textLength = carrierText.length();

        // We iterate through every character in the innocent sentence
        for (int i = 0; i < textLength; i++) {
            if (bitIndex < payloadBits.length()) {
                // Read the current bit of the machine code
                boolean isBitOne = payloadBits.get(bitIndex);

                // Modulate the physical font tracking (spacing) for this specific character
                float trackingValue = isBitOne ? BIT_ONE : BIT_ZERO;
                document.addAttribute(TextAttribute.TRACKING, trackingValue, i, i + 1);

                bitIndex++;
            } else {
                // Pad the rest of the document with zero-spacing
                document.addAttribute(TextAttribute.TRACKING, BIT_ZERO, i, i + 1);
            }
        }

        System.out.println("   [+] Document forged. " + machineCode.length + " bytes encoded in font rendering.");
        return document;
    }

    /**
     * DECODE: The execution of the typography.
     * Scans the font tracking, reconstructs the machine code, and prepares it for execution.
     */
    public static byte[] extractAndExecute(AttributedString document) {
        System.out.println("   [👁️] Scanning document topography for executable logic...");

        AttributedCharacterIterator iterator = document.getIterator();
        BitSet extractedBits = new BitSet();
        int bitIndex = 0;

        // Scan the document character by character
        while (iterator.getIndex() < iterator.getEndIndex()) {
            // Read the sub-pixel tracking value
            Float tracking = (Float) iterator.getAttribute(TextAttribute.TRACKING);

            if (tracking != null) {
                // Collapse the physical space back into binary logic
                if (Math.abs(tracking - BIT_ONE) < 0.00001f) {
                    extractedBits.set(bitIndex, true);
                } else {
                    extractedBits.set(bitIndex, false);
                }
                bitIndex++;
            }
            iterator.next();
        }

        byte[] machineCode = extractedBits.toByteArray();
        System.out.println("   [+] Extraction complete. Recovered " + machineCode.length + " executable bytes.");

        return machineCode;
    }

    /**
     * Returns the maximum payload capacity for a given carrier text length.
     */
    public static int getCapacity(String carrierText) {
        return carrierText.length() / 8; // bits to bytes
    }

    // --- TEST SEQUENCE ---
    public static void main(String[] args) {
        // 1. The innocent carrier text
        String bookChapter = "The quick brown fox jumps over the lazy dog. This is just a standard test string containing no anomalous data.";

        // 2. The Fraymus Machine Code (e.g., ADD EAX, 5; HALT)
        byte[] lethalPayload = { 0x05, 0x05, (byte) 0xFF };

        // 3. Forge the document
        AttributedString weaponizedText = forgeDocument(bookChapter, lethalPayload);

        // 4. Extract the DNA from the font spacing
        byte[] resurrectedCode = extractAndExecute(weaponizedText);

        // Verify structural integrity
        System.out.print("   [?] Verifying Payload Hex: ");
        for (byte b : resurrectedCode) {
            System.out.printf("0x%02X ", b);
        }
        System.out.println();

        if (java.util.Arrays.equals(lethalPayload, resurrectedCode)) {
            System.out.println("   [!] INTEGRITY VERIFIED. Payload survived the typography cycle.");
        } else {
            System.out.println("   [!] INTEGRITY FAILURE. Payload corrupted.");
        }
    }
}
