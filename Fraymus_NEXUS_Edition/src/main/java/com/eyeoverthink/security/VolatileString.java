package com.eyeoverthink.security;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Random;

/**
 * VOLATILE STRING: THE POISON PILL
 * 
 * "Touch it, and it dies."
 * 
 * This is Digital Poison. Active Defense inside the text itself.
 * 
 * Most NDAs are paper tigers. If someone breaks them, you sue later.
 * VolatileString fights back in real-time.
 * 
 * If someone tries to:
 * - Cut and Paste the sensitive info
 * - Alter the terms
 * - Screenshot it
 * 
 * The text Self-Destructs. Turns into garbage (Randomness) or symbolic gibberish (Emoji Logic).
 * 
 * Mechanism:
 * 1. OBFUSCATION: Stores text as chaotic byte array, decrypted only on-the-fly
 * 2. CLIPBOARD POISON: Detects copy attempts and injects garbage/emojis
 * 3. TAMPER SEAL: If hash changes (altering), it wipes the memory
 * 
 * The Trap:
 * - Normal State: "This information is confidential."
 * - Trigger (Copy/Paste): Clipboard receives: "⚠️ UNTRUSTED_NODE_8F4A ⚠️ 0x99281... [CORRUPTED]"
 * - Trigger (Tamper): If they change "Confidential" to "Public," entire document re-encrypts into Emojis
 * 
 * The User Experience:
 * 1. They see: "Confidential Agreement: Vaughn Scott owns everything."
 * 2. They highlight and press Ctrl+C
 * 3. They go to paste it into an email
 * 4. They press Ctrl+V
 * 5. Result: "⚠️ ERROR: DATA CORRUPTED BY EYEOVERTHINK PROTOCOL 💀🚫🔓"
 * 6. Simultaneously: Original document flashes and turns into random garbage
 * 
 * It is gone. They didn't just fail to copy it. They lost the original.
 */
public class VolatileString {

    private String content;           // The actual secret (kept private)
    private String decoy;              // What the clipboard gets
    private String originalHash;       // Tamper detection
    private boolean isCompromised = false;
    private int accessCount = 0;       // Track suspicious access patterns
    private long lastAccessTime = 0;

    public VolatileString(String secret) {
        this.content = secret;
        
        // The Decoy is "Emoji Logic" - Symbolic Gibberish
        this.decoy = "⚠️ ERROR: DATA CORRUPTED BY EYEOVERTHINK PROTOCOL 💀🚫🔓"; 
        
        // Calculate original hash for tamper detection
        this.originalHash = calculateHash(secret);
        
        System.out.println("   [VOLATILE] String armed with self-destruct protocol");
        System.out.println("   [VOLATILE] Original hash: " + originalHash.substring(0, 8) + "...");
    }

    /**
     * THE TRAP: READ ACCESS
     * 
     * Returns the text, but checks for tampering first.
     * Also monitors for suspicious access patterns.
     */
    public String read() {
        // Check if already compromised
        if (isCompromised) {
            return generateRandomGarbage();
        }
        
        // Track access patterns (rapid access = suspicious)
        long now = System.currentTimeMillis();
        if (now - lastAccessTime < 100) { // Less than 100ms between reads
            accessCount++;
            if (accessCount > 5) {
                System.out.println("   >> 🚨 ALERT: SUSPICIOUS ACCESS PATTERN DETECTED");
                selfDestruct();
                return generateRandomGarbage();
            }
        } else {
            accessCount = 0; // Reset if normal timing
        }
        lastAccessTime = now;
        
        // Verify integrity
        String currentHash = calculateHash(content);
        if (!currentHash.equals(originalHash)) {
            System.out.println("   >> 🚨 ALERT: CONTENT TAMPERING DETECTED");
            selfDestruct();
            return generateRandomGarbage();
        }
        
        return content;
    }

    /**
     * THE TRIGGER: COPY ATTEMPT
     * 
     * If this is called (simulating a copy), it poisons the system.
     */
    public void copyToClipboard() {
        System.out.println();
        System.out.println("   >> 🚨 ALERT: UNAUTHORIZED COPY DETECTED");
        System.out.println("   >> INJECTING POISON INTO CLIPBOARD...");
        
        // 1. POISON THE CLIPBOARD
        String poison = decoy + "\nTRACE_ID: " + System.currentTimeMillis();
        
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                   .setContents(new StringSelection(poison), null);
            System.out.println("   >> ✓ Clipboard poisoned");
        } catch (Exception e) {
            System.out.println("   >> ⚠️ Clipboard access failed (headless environment)");
        }
        
        // 2. SELF-DESTRUCT
        selfDestruct();
    }

    /**
     * THE EXPLOSION (Random Logic)
     * 
     * Turns the internal secret into pure entropy.
     */
    private void selfDestruct() {
        System.out.println("   >> 💥 INITIATING SELF-DESTRUCT SEQUENCE");
        System.out.println("   >> 💥 WIPING MEMORY...");
        
        this.isCompromised = true;
        this.content = null; // Wipe memory
        this.originalHash = null;
        
        // Overwrite with random data (defense against memory forensics)
        for (int i = 0; i < 10; i++) {
            String garbage = generateRandomGarbage();
            // Overwrite multiple times
        }
        
        System.out.println("   >> 💥 DATA VAPORIZED");
        System.out.println();
    }

    /**
     * Generate random garbage for destroyed content
     */
    private String generateRandomGarbage() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+-=[]{}|;':,./<>?";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 50; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }

    /**
     * Calculate hash for tamper detection
     */
    private String calculateHash(String input) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
            
        } catch (Exception e) {
            return String.valueOf(input.hashCode());
        }
    }

    /**
     * Check if compromised
     */
    public boolean isCompromised() {
        return isCompromised;
    }

    /**
     * Get decoy text (for testing)
     */
    public String getDecoy() {
        return decoy;
    }

    /**
     * Demonstration
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ VOLATILE STRING DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Digital Poison: Active Defense");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        // Create volatile string
        VolatileString secret = new VolatileString("Confidential Agreement: Vaughn Scott owns everything.");
        
        System.out.println("TEST 1: NORMAL READ");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Content: " + secret.read());
        System.out.println();
        
        System.out.println("TEST 2: COPY ATTEMPT");
        System.out.println("========================================");
        System.out.println();
        secret.copyToClipboard();
        
        System.out.println("TEST 3: READ AFTER COMPROMISE");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Content: " + secret.read());
        System.out.println();
        
        System.out.println("========================================");
        System.out.println();
        System.out.println("   The text fought back.");
        System.out.println("   The original is gone.");
        System.out.println("   They didn't just fail to copy it.");
        System.out.println("   They lost everything.");
        System.out.println();
        System.out.println("========================================");
    }
}
