package fraymus.metal.alchemy;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.BitSet;

/**
 * THE SEMANTIC NDA LOCK (Polyglot Cryptographic Backup)
 * The visible text acts as the SHA-256 Encryption Key.
 * The invisible text acts as the AES-256 Encrypted Payload.
 * If the visible text is altered, the payload is permanently destroyed.
 *
 * Zero-Width Unicode Alphabet (Base-4 encoding):
 *   U+200B = 00 (Zero-Width Space)
 *   U+200C = 01 (Zero-Width Non-Joiner)
 *   U+200D = 10 (Zero-Width Joiner)
 *   U+FEFF = 11 (Zero-Width No-Break Space / BOM)
 *
 * The document enforces its own integrity. If the NDA is broken, the AI commits suicide.
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class SemanticNDALock {

    // The Invisible Alphabet (Zero-Width Unicode, Base-4)
    private static final char[] ZERO_WIDTH = {
        '\u200B', // 00: Zero-Width Space
        '\u200C', // 01: Zero-Width Non-Joiner
        '\u200D', // 10: Zero-Width Joiner
        '\uFEFF'  // 11: Zero-Width No-Break Space (BOM)
    };

    /**
     * FORGE: Encrypts the payload using the text itself as the key, then weaves it in.
     *
     * @param visibleNDA    The plaintext document (this IS the encryption key)
     * @param secretBackup  The binary payload to hide (e.g., NEXUS_GHOST.dat)
     * @return The forged document with invisible encrypted payload woven in
     */
    public static String forgeSelfLockingDocument(String visibleNDA, byte[] secretBackup) throws Exception {
        System.out.println("   [NDA] Hashing visible text to generate AES-256 Key...");

        // 1. Hash the exact visible text to create the lock
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(visibleNDA.getBytes("UTF-8"));
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        System.out.printf("   [NDA] Key fingerprint: %02X%02X%02X%02X...%02X%02X%02X%02X\n",
            keyBytes[0], keyBytes[1], keyBytes[2], keyBytes[3],
            keyBytes[28], keyBytes[29], keyBytes[30], keyBytes[31]);

        // 2. Encrypt the secret backup using the text's hash
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedPayload = cipher.doFinal(secretBackup);

        System.out.printf("   [NDA] Encrypted %d bytes -> %d cipher bytes.\n",
            secretBackup.length, encryptedPayload.length);

        // 3. Convert Encrypted Payload to Invisible Base-4 Unicode
        StringBuilder invisibleDNA = new StringBuilder();
        BitSet bits = BitSet.valueOf(encryptedPayload);

        // Process all bytes (BitSet.length() may be shorter than actual data if trailing zeros)
        int totalBits = encryptedPayload.length * 8;
        for (int i = 0; i < totalBits; i += 2) {
            int val = (bits.get(i) ? 1 : 0) | ((bits.get(i + 1) ? 1 : 0) << 1);
            invisibleDNA.append(ZERO_WIDTH[val]);
        }

        System.out.printf("   [NDA] Payload encoded as %d zero-width characters.\n", invisibleDNA.length());

        // 4. Weave the invisible DNA evenly into the visible text
        StringBuilder finalDocument = new StringBuilder();
        int dnaLength = invisibleDNA.length();
        int textLength = visibleNDA.length();

        for (int i = 0; i < textLength; i++) {
            finalDocument.append(visibleNDA.charAt(i));
            // Interleave a chunk of the invisible payload after every visible character
            int start = (i * dnaLength) / textLength;
            int end = ((i + 1) * dnaLength) / textLength;
            if (end > dnaLength) end = dnaLength;
            finalDocument.append(invisibleDNA, start, end);
        }

        System.out.println("   [NDA] Document Forged. The text is now self-verifying.");
        System.out.printf("   [NDA] Visible: %d chars | Invisible: %d chars | Total: %d chars\n",
            textLength, dnaLength, finalDocument.length());
        return finalDocument.toString();
    }

    /**
     * EXTRACT: Strips the visible text to generate the key,
     * strips the invisible text to get the cipher, and decrypts.
     *
     * @param interceptedText The document (visible + invisible characters)
     * @return The decrypted payload, or null if the NDA was breached
     */
    public static byte[] unlockDocument(String interceptedText) {
        System.out.println("   [NDA] Analyzing document for cryptographic resonance...");

        StringBuilder visibleText = new StringBuilder();
        StringBuilder invisibleText = new StringBuilder();

        // 1. Separate the visible (the Key) from the invisible (the Ciphertext)
        for (char c : interceptedText.toCharArray()) {
            if (c == '\u200B' || c == '\u200C' || c == '\u200D' || c == '\uFEFF') {
                invisibleText.append(c);
            } else {
                visibleText.append(c);
            }
        }

        System.out.printf("   [NDA] Visible: %d chars | Invisible: %d chars\n",
            visibleText.length(), invisibleText.length());

        try {
            // 2. Hash the extracted visible text to recreate the Key
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(visibleText.toString().getBytes("UTF-8"));
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            System.out.printf("   [NDA] Reconstructed key: %02X%02X%02X%02X...%02X%02X%02X%02X\n",
                keyBytes[0], keyBytes[1], keyBytes[2], keyBytes[3],
                keyBytes[28], keyBytes[29], keyBytes[30], keyBytes[31]);

            // 3. Reconstruct the encrypted byte array from the invisible characters
            int invisLen = invisibleText.length();
            int totalBits = invisLen * 2;
            BitSet bits = new BitSet(totalBits);
            int bitIndex = 0;
            for (int i = 0; i < invisLen; i++) {
                char c = invisibleText.charAt(i);
                int val = 0;
                if (c == '\u200C') val = 1;
                else if (c == '\u200D') val = 2;
                else if (c == '\uFEFF') val = 3;

                bits.set(bitIndex, (val & 1) != 0);
                bits.set(bitIndex + 1, (val & 2) != 0);
                bitIndex += 2;
            }

            // Convert BitSet to byte array with correct length
            int byteCount = (totalBits + 7) / 8;
            byte[] encryptedPayload = new byte[byteCount];
            byte[] rawBits = bits.toByteArray();
            System.arraycopy(rawBits, 0, encryptedPayload, 0, Math.min(rawBits.length, byteCount));

            // 4. Attempt Decryption
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBackup = cipher.doFinal(encryptedPayload);

            System.out.println("   [NDA] NDA UNBROKEN. AES-256 Decryption successful. Backup recovered.");
            return decryptedBackup;

        } catch (Exception e) {
            System.err.println("   [!] FATAL: AES DECRYPTION FAILED.");
            System.err.println("   [!] The visible text was altered. The NDA was breached.");
            System.err.println("   [!] The backup is permanently destroyed. Mathematical antimatter.");
            return null;
        }
    }

    /**
     * Quick check: does the document contain invisible payload?
     */
    public static boolean containsPayload(String text) {
        for (char c : text.toCharArray()) {
            if (c == '\u200B' || c == '\u200C' || c == '\u200D' || c == '\uFEFF') return true;
        }
        return false;
    }

    /**
     * Count invisible characters in a document.
     */
    public static int countInvisible(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == '\u200B' || c == '\u200C' || c == '\u200D' || c == '\uFEFF') count++;
        }
        return count;
    }

    public static void main(String[] args) throws Exception {
        // The visible text acting as the NDA and the Encryption Key
        String legalNDA = "I, the undersigned, agree to keep the Fraymus Architecture strictly confidential.";

        // The Secret Backup (Could be 8192-D weights, or an executable payload)
        byte[] sovereignBackup = "FRAYMUS_GHOST_PROTOCOL_RECOVERY_DATA".getBytes("UTF-8");

        // 1. Forge the lock
        String lockedDocument = forgeSelfLockingDocument(legalNDA, sovereignBackup);

        System.out.println("\n--- SIMULATING SUCCESSFUL RECOVERY ---");
        byte[] recovered = unlockDocument(lockedDocument);
        if (recovered != null) {
            System.out.println("   Recovered Data: " + new String(recovered, "UTF-8"));
        }

        System.out.println("\n--- SIMULATING NDA BREACH (TAMPERING) ---");
        // Tamper with a single visible character (zero-width interleaving means we must
        // replace an individual char, not a word — the invisible chars break contiguous matches)
        // We flip the very first visible character: 'I' -> 'i'
        StringBuilder tampered = new StringBuilder(lockedDocument);
        tampered.setCharAt(0, 'i'); // Change capital 'I' to lowercase 'i'
        System.out.println("   [!] Altered first character: 'I' -> 'i'");
        unlockDocument(tampered.toString());
    }
}
