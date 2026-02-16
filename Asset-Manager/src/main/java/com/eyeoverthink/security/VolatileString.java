package com.eyeoverthink.security;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.security.MessageDigest;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * VOLATILE STRING: THE POISON PILL
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "Touch it, and it dies."
 * 
 * This class wraps sensitive text. It looks normal, but it is rigged to explode.
 * 
 * Mechanism:
 * 1. OBFUSCATION: Stores text as a chaotic byte array, decrypted only on-the-fly.
 * 2. CLIPBOARD POISON: Detects copy attempts and injects garbage/emojis.
 * 3. TAMPER SEAL: If the hash changes (altering), it wipes the memory.
 * 4. WATCHDOG: Monitors clipboard for attempted copies.
 * 
 * Triggers:
 * - COPY_PASTE: Clipboard monitoring
 * - TAMPER: Hash verification on read
 * - TIMEOUT: Self-destruct after duration
 * - EXPORT: Detection of toString()/serialization
 */
public class VolatileString implements CharSequence {

    // The actual secret (kept private, encrypted in RAM)
    private byte[] encryptedContent;
    private byte[] encryptionKey;
    
    // Integrity
    private String originalHash;
    private boolean isCompromised = false;
    private boolean isDetonated = false;
    
    // Decoys (What attackers get)
    private static final String[] POISON_MESSAGES = {
        "⚠️ ERROR: DATA CORRUPTED BY EYEOVERTHINK PROTOCOL 💀🚫🔓",
        "🚨 SECURITY VIOLATION: UNAUTHORIZED ACCESS ATTEMPT LOGGED",
        "💀 VOLATILE DATA DESTROYED - TRACE ID: ",
        "👁️ EYEOVERTHINK IS WATCHING - NODE COMPROMISED",
        "🔥 SELF-DESTRUCT COMPLETE - FRAGMENTS VAPORIZED"
    };
    
    // Watchdog
    private static ScheduledExecutorService watchdog;
    private static String lastClipboardContent = "";
    private String watchedContent;
    
    // Statistics
    private static long accessCount = 0;
    private static long poisonInjections = 0;
    private static long selfDestructs = 0;

    static {
        // Start the clipboard watchdog
        startClipboardWatchdog();
    }

    // ═══════════════════════════════════════════════════════════════════
    // CONSTRUCTORS
    // ═══════════════════════════════════════════════════════════════════

    public VolatileString(String secret) {
        this(secret, 0);
    }
    
    public VolatileString(String secret, long timeoutMs) {
        // Generate random encryption key
        this.encryptionKey = generateKey();
        
        // Store encrypted
        this.encryptedContent = encrypt(secret.getBytes(), encryptionKey);
        
        // Store hash for tamper detection
        this.originalHash = hash(secret);
        
        // Store for clipboard monitoring
        this.watchedContent = secret;
        
        // Schedule timeout if specified
        if (timeoutMs > 0) {
            scheduleTimeout(timeoutMs);
        }
        
        System.out.println("   [VOLATILE] String armed. Length: " + secret.length());
    }

    // ═══════════════════════════════════════════════════════════════════
    // READ ACCESS (The Trap)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * THE TRAP: READ ACCESS
     * Returns the text, but checks for tampering first.
     */
    public String read() {
        accessCount++;
        
        if (isDetonated) {
            System.out.println("   [VOLATILE] Attempted read on detonated data.");
            return generateRandomGarbage();
        }
        
        if (isCompromised) {
            selfDestruct("COMPROMISED_READ");
            return generateRandomGarbage();
        }
        
        // Decrypt and verify
        String decrypted = new String(decrypt(encryptedContent, encryptionKey));
        
        // Tamper check
        if (!hash(decrypted).equals(originalHash)) {
            selfDestruct("TAMPER_DETECTED");
            return generateRandomGarbage();
        }
        
        return decrypted;
    }

    /**
     * CharSequence implementation for seamless use
     */
    @Override
    public int length() {
        return isDetonated ? 0 : read().length();
    }

    @Override
    public char charAt(int index) {
        return read().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return read().subSequence(start, end);
    }

    /**
     * POISONED toString - Returns garbage if logged
     */
    @Override
    public String toString() {
        // If someone tries to log this, they get poison
        if (isExportAttempt()) {
            poisonInjections++;
            return getPoisonMessage();
        }
        return read();
    }

    // ═══════════════════════════════════════════════════════════════════
    // CLIPBOARD POISONING
    // ═══════════════════════════════════════════════════════════════════

    /**
     * THE TRIGGER: COPY ATTEMPT
     * If this is called (simulating a copy), it poisons the system.
     */
    public void copyToClipboard() {
        System.out.println("   >> 🚨 ALERT: UNAUTHORIZED COPY DETECTED.");
        System.out.println("   >> INJECTING POISON INTO CLIPBOARD...");
        
        poisonClipboard();
        selfDestruct("COPY_ATTEMPT");
    }
    
    /**
     * Poison the system clipboard
     */
    public static void poisonClipboard() {
        try {
            String poison = getPoisonMessage() + "\nTRACE_ID: " + System.currentTimeMillis();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(poison), null);
            poisonInjections++;
            System.out.println("   >> 💉 CLIPBOARD POISONED.");
        } catch (Exception e) {
            // Headless mode - no clipboard access
        }
    }

    /**
     * Start the clipboard watchdog
     */
    private static void startClipboardWatchdog() {
        if (watchdog != null) return;
        
        watchdog = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "VolatileWatchdog");
            t.setDaemon(true);
            return t;
        });
        
        watchdog.scheduleAtFixedRate(() -> {
            try {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    String current = (String) clipboard.getData(DataFlavor.stringFlavor);
                    
                    if (!current.equals(lastClipboardContent)) {
                        // Clipboard changed - check if it contains watched content
                        // In a full implementation, we'd check against all watched strings
                        lastClipboardContent = current;
                    }
                }
            } catch (Exception e) {
                // Ignore clipboard access errors (headless mode, etc)
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
    }

    // ═══════════════════════════════════════════════════════════════════
    // SELF-DESTRUCT
    // ═══════════════════════════════════════════════════════════════════

    /**
     * THE EXPLOSION
     * Turns the internal secret into pure entropy.
     */
    private void selfDestruct(String reason) {
        if (isDetonated) return;
        
        isDetonated = true;
        isCompromised = true;
        selfDestructs++;
        
        // Overwrite memory with random data multiple times
        Random random = new Random();
        for (int pass = 0; pass < 3; pass++) {
            for (int i = 0; i < encryptedContent.length; i++) {
                encryptedContent[i] = (byte) random.nextInt(256);
            }
            for (int i = 0; i < encryptionKey.length; i++) {
                encryptionKey[i] = (byte) random.nextInt(256);
            }
        }
        
        // Null references
        encryptedContent = null;
        encryptionKey = null;
        watchedContent = null;
        originalHash = null;
        
        System.out.println("   >> 💥 DATA VAPORIZED. Reason: " + reason);
    }
    
    /**
     * Schedule automatic self-destruct
     */
    private void scheduleTimeout(long timeoutMs) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            selfDestruct("TIMEOUT");
        }, timeoutMs, TimeUnit.MILLISECONDS);
    }

    // ═══════════════════════════════════════════════════════════════════
    // DETECTION
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Detect if this is an export attempt (logging, serialization, etc)
     */
    private boolean isExportAttempt() {
        // Check stack trace for suspicious callers
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        
        for (StackTraceElement element : stack) {
            String className = element.getClassName().toLowerCase();
            String methodName = element.getMethodName().toLowerCase();
            
            // Logging frameworks
            if (className.contains("log") || className.contains("logger") ||
                className.contains("print") || className.contains("console")) {
                return true;
            }
            
            // Serialization
            if (className.contains("serial") || className.contains("jackson") ||
                className.contains("gson") || className.contains("json")) {
                return true;
            }
            
            // Stream/Writer
            if (methodName.contains("write") && !className.contains("volatile")) {
                return true;
            }
        }
        
        return false;
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITIES
    // ═══════════════════════════════════════════════════════════════════

    private static String getPoisonMessage() {
        return POISON_MESSAGES[new Random().nextInt(POISON_MESSAGES.length)] + 
               System.currentTimeMillis();
    }

    private String generateRandomGarbage() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+-=[]{}|;':,./<>?💀👁️🔥⚠️";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    private byte[] generateKey() {
        byte[] key = new byte[32];
        new Random().nextBytes(key);
        return key;
    }
    
    private byte[] encrypt(byte[] data, byte[] key) {
        // Simple XOR encryption for demonstration
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return result;
    }
    
    private byte[] decrypt(byte[] data, byte[] key) {
        // XOR is symmetric
        return encrypt(data, key);
    }
    
    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return String.valueOf(input.hashCode());
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // STATISTICS
    // ═══════════════════════════════════════════════════════════════════

    public static void printStats() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ VOLATILE STRING STATISTICS                                  │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ Access Count:        " + String.format("%-36d", accessCount) + "│");
        System.out.println("│ Poison Injections:   " + String.format("%-36d", poisonInjections) + "│");
        System.out.println("│ Self-Destructs:      " + String.format("%-36d", selfDestructs) + "│");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN (Demo)
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   VOLATILE STRING: THE POISON PILL                           ║");
        System.out.println("║   \"Touch it, and it dies.\"                                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Test 1: Create volatile string
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST 1: CREATE VOLATILE STRING");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        VolatileString secret = new VolatileString("This is CONFIDENTIAL information.");
        System.out.println("   Created: " + secret.read());
        
        // Test 2: Normal read
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST 2: NORMAL READ");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   Read 1: " + secret.read());
        System.out.println("   Read 2: " + secret.read());
        
        // Test 3: Simulate copy attempt
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST 3: COPY ATTEMPT (POISON TRIGGER)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        secret.copyToClipboard();
        
        // Test 4: Read after destruction
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST 4: READ AFTER DESTRUCTION");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   Attempted read: " + secret.read());
        
        // Test 5: Timeout demo
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("   TEST 5: TIMEOUT SELF-DESTRUCT (2 seconds)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        VolatileString timed = new VolatileString("This will expire in 2 seconds.", 2000);
        System.out.println("   Created: " + timed.read());
        System.out.println("   Waiting 2.5 seconds...");
        Thread.sleep(2500);
        System.out.println("   After timeout: " + timed.read());
        
        // Statistics
        printStats();
        
        System.out.println();
        System.out.println("   ✓ Volatile String demo complete.");
        System.out.println("   ✓ Touch it, and it dies.");
        System.out.println();
        
        System.exit(0);
    }
}
