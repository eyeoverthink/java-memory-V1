package fraymus.metal.alchemy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 🖋️ TRUE RTF ORTHOGRAPHIC COMPILER
 * Generates a physical .rtf file that hides raw executable bytes inside
 * 1/1440th of an inch kerning adjustments (\expndtw tags).
 * No external libraries. Survives process death.
 *
 * Encoding: \expndtw0 = binary 0, \expndtw1 = binary 1
 * Each character in the carrier text hides one bit of the payload.
 * Opens normally in MS Word / WordPad — looks like standard text.
 */
public class RTF_Steganography {

    // RTF Document Headers (Courier font, 12pt)
    private static final String RTF_HEADER = "{\\rtf1\\ansi\\ansicpg1252\\deff0\\nouicompat{\\fonttbl{\\f0\\fnil\\fcharset0 Courier New;}}\n{\\*\\generator Fraymus Kernel;}\\viewkind4\\uc1 \n\\pard\\f0\\fs24\\lang1033 ";
    private static final String RTF_FOOTER = "\\par\n}\n";

    /**
     * FORGE: Writes a physical .rtf document to the hard drive.
     */
    public static void forgePhysicalDocument(String filePath, String carrierText, byte[] machineCode) throws IOException {
        System.out.println("   [🖋️] Weaving " + machineCode.length + " bytes of machine code into physical RTF twips...");

        StringBuilder rtfContent = new StringBuilder();
        rtfContent.append(RTF_HEADER);

        BitSet payloadBits = BitSet.valueOf(machineCode);
        int bitIndex = 0;

        // Iterate through the innocent carrier text
        for (char c : carrierText.toCharArray()) {
            // Escape special RTF characters
            String character = (c == '\\' || c == '{' || c == '}') ? "\\" + c : String.valueOf(c);

            if (bitIndex < payloadBits.length()) {
                // If the bit is 1, expand kerning by 1 twip (\expndtw1). If 0, standard kerning (\expndtw0).
                int kerningValue = payloadBits.get(bitIndex) ? 1 : 0;

                // Wrap the character in the invisible tracking tag
                rtfContent.append("{\\expndtw").append(kerningValue).append(" ").append(character).append("}");
                bitIndex++;
            } else {
                // Out of payload data, use standard 0 twip spacing
                rtfContent.append("{\\expndtw0 ").append(character).append("}");
            }
        }

        rtfContent.append(RTF_FOOTER);

        // Write to raw metal disk
        Path path = Paths.get(filePath);
        Files.write(path, rtfContent.toString().getBytes());
        System.out.println("   [+] Physical document forged at: " + path.toAbsolutePath());
    }

    /**
     * EXTRACT: Reads an .rtf file from the disk and strips the machine code out of the kerning tags.
     */
    public static byte[] extractFromPhysicalDocument(String filePath) throws IOException {
        System.out.println("   [👁️] Scanning physical document topography at " + filePath + "...");

        String rawRTF = new String(Files.readAllBytes(Paths.get(filePath)));
        BitSet extractedBits = new BitSet();
        int bitIndex = 0;

        // Regex to hunt for the \expndtwN tag and extract the N value (0 or 1)
        Pattern kerningPattern = Pattern.compile("\\\\expndtw([01])\\s");
        Matcher matcher = kerningPattern.matcher(rawRTF);

        while (matcher.find()) {
            int bitValue = Integer.parseInt(matcher.group(1));
            if (bitValue == 1) {
                extractedBits.set(bitIndex, true);
            } else {
                extractedBits.set(bitIndex, false);
            }
            bitIndex++;
        }

        byte[] machineCode = extractedBits.toByteArray();
        System.out.println("   [+] Extraction complete. Recovered " + machineCode.length + " executable bytes.");

        return machineCode;
    }

    /**
     * Returns the maximum payload capacity for a given carrier text.
     */
    public static int getCapacity(String carrierText) {
        return carrierText.length() / 8; // bits to bytes
    }

    // --- PROOF OF EXECUTION ---
    public static void main(String[] args) {
        try {
            String fileName = "fraymus_manifesto.rtf";
            String innocentText = "The architecture is absolute. This document appears to be standard rich text. However, it is an executable carrier wave. It contains the logic to rebuild the machine.";

            // The Fraymus Machine Code Payload (Example: MOV, ADD, JMP, HALT)
            byte[] lethalPayload = { 0x01, (byte) 0x80, 0x05, 0x1A, (byte) 0xFF, 0x42, 0x77 };

            // 1. Forge the physical file to the disk
            forgePhysicalDocument(fileName, innocentText, lethalPayload);

            // 2. Read the physical file back from the disk and extract the binary
            byte[] resurrectedCode = extractFromPhysicalDocument(fileName);

            // 3. Verify absolute mathematical integrity
            System.out.print("   [?] Verifying Payload Hex: ");
            for (byte b : resurrectedCode) {
                System.out.printf("0x%02X ", b);
            }
            System.out.println();

            if (java.util.Arrays.equals(lethalPayload, resurrectedCode)) {
                System.out.println("\n   [!] INTEGRITY VERIFIED. The DNA survived the physical write cycle.");
            }

        } catch (IOException e) {
            System.err.println("   [!] FRACTURE: " + e.getMessage());
        }
    }
}
