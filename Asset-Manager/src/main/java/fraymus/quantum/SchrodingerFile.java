package fraymus.quantum;

import fraymus.quantum.core.PhiQuantumConstants;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * SCHRÖDINGER'S FILE: PROBABILISTIC STORAGE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "It is a cat. It is a code. It depends on who looks."
 * 
 * One container. Two realities. Perfect deniability.
 * 
 * The Math (XOR Triangle):
 *   Container = Secret ⊕ KeySecret
 *   KeyDecoy  = Container ⊕ Decoy
 *   
 * Therefore:
 *   Container ⊕ KeySecret = Secret  (The Truth)
 *   Container ⊕ KeyDecoy  = Decoy   (The Alibi)
 * 
 * Why It's Unbreakable:
 * - Brute force finds ALL possible strings (including "LAUNCH_CODES" and "GROCERY_LIST")
 * - No way to prove which reality you actually stored
 * - Mathematically perfect plausible deniability
 * 
 * Standard Encryption: "I have a secret file, but you can't read it." (Suspicious)
 * Schrödinger's Encryption: "I have a grocery list." (Innocent)
 */
public class SchrodingerFile {

    private static final double PHI = PhiQuantumConstants.PHI;
    
    // Use SecureRandom for cryptographically strong randomness
    private final SecureRandom random = new SecureRandom();

    // ═══════════════════════════════════════════════════════════════════
    // 1. THE CREATION (Entangling the Data)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Entangle a secret and a decoy into a single quantum container
     * 
     * @param secret The real data (only you can access with KeySecret)
     * @param decoy The cover story (give KeyDecoy to enemies)
     * @return QuantumState containing container and both keys
     */
    public QuantumState entangle(String secret, String decoy) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   ENTANGLING REALITIES");
        System.out.println("═══════════════════════════════════════════");
        System.out.println();
        
        // Pad strings to same length for XOR math
        int maxLength = Math.max(secret.length(), decoy.length());
        String secretPad = pad(secret, maxLength);
        String decoyPad = pad(decoy, maxLength);
        
        byte[] secretBytes = secretPad.getBytes(StandardCharsets.UTF_8);
        byte[] decoyBytes = decoyPad.getBytes(StandardCharsets.UTF_8);
        
        System.out.println("Secret: " + secret.substring(0, Math.min(20, secret.length())) + 
            (secret.length() > 20 ? "..." : ""));
        System.out.println("Decoy:  " + decoy.substring(0, Math.min(20, decoy.length())) + 
            (decoy.length() > 20 ? "..." : ""));
        System.out.println("Container size: " + maxLength + " bytes");
        System.out.println();
        
        // A. GENERATE KEY ALPHA (Random Noise)
        // This is the key YOU keep.
        byte[] keySecret = new byte[maxLength];
        random.nextBytes(keySecret);
        
        // B. CREATE CONTAINER (The Blob)
        // Container = Secret XOR KeySecret
        byte[] container = xor(secretBytes, keySecret);
        
        // C. GENERATE KEY BETA (The Alibi)
        // KeyDecoy = Container XOR Decoy
        // This forces the math to work backwards to the Decoy.
        byte[] keyDecoy = xor(container, decoyBytes);
        
        System.out.println(">> STATE SUPERPOSITION ACHIEVED.");
        System.out.println(">> Container holds BOTH realities simultaneously.");
        System.out.println(">> KeySecret reveals the SECRET.");
        System.out.println(">> KeyDecoy reveals the DECOY.");
        
        return new QuantumState(container, keySecret, keyDecoy, secret, decoy);
    }

    /**
     * Entangle binary data (for files)
     */
    public QuantumState entangleBinary(byte[] secret, byte[] decoy) {
        // Pad to same length
        int maxLength = Math.max(secret.length, decoy.length);
        byte[] secretPad = new byte[maxLength];
        byte[] decoyPad = new byte[maxLength];
        
        System.arraycopy(secret, 0, secretPad, 0, secret.length);
        System.arraycopy(decoy, 0, decoyPad, 0, decoy.length);
        
        // Generate keys
        byte[] keySecret = new byte[maxLength];
        random.nextBytes(keySecret);
        
        byte[] container = xor(secretPad, keySecret);
        byte[] keyDecoy = xor(container, decoyPad);
        
        return new QuantumState(container, keySecret, keyDecoy, null, null);
    }

    // ═══════════════════════════════════════════════════════════════════
    // 2. THE OBSERVATION (Collapsing the Wave Function)
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Observe the container with a key - collapses to one reality
     */
    public String observe(byte[] container, byte[] key) {
        byte[] result = xor(container, key);
        return new String(result, StandardCharsets.UTF_8).trim();
    }

    /**
     * Observe and return raw bytes (for binary data)
     */
    public byte[] observeBinary(byte[] container, byte[] key) {
        return xor(container, key);
    }

    /**
     * Observe a QuantumState with the secret key
     */
    public String observeSecret(QuantumState state) {
        return observe(state.container, state.keySecret);
    }

    /**
     * Observe a QuantumState with the decoy key
     */
    public String observeDecoy(QuantumState state) {
        return observe(state.container, state.keyDecoy);
    }

    // ═══════════════════════════════════════════════════════════════════
    // 3. FILE OPERATIONS
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Save quantum state to files
     */
    public void saveToFiles(QuantumState state, String basePath) throws IOException {
        System.out.println(">> Saving quantum state to: " + basePath);
        
        // Save container (the innocuous-looking blob)
        Files.write(Path.of(basePath + ".qbox"), state.container);
        
        // Save secret key (keep this hidden!)
        Files.write(Path.of(basePath + ".key_alpha"), state.keySecret);
        
        // Save decoy key (give this to enemies)
        Files.write(Path.of(basePath + ".key_beta"), state.keyDecoy);
        
        System.out.println(">> Saved:");
        System.out.println("   " + basePath + ".qbox      (Container - looks like random noise)");
        System.out.println("   " + basePath + ".key_alpha (Secret key - HIDE THIS)");
        System.out.println("   " + basePath + ".key_beta  (Decoy key - give to enemies)");
    }

    /**
     * Load quantum state from files
     */
    public QuantumState loadFromFiles(String basePath) throws IOException {
        byte[] container = Files.readAllBytes(Path.of(basePath + ".qbox"));
        byte[] keySecret = Files.readAllBytes(Path.of(basePath + ".key_alpha"));
        byte[] keyDecoy = Files.readAllBytes(Path.of(basePath + ".key_beta"));
        
        return new QuantumState(container, keySecret, keyDecoy, null, null);
    }

    /**
     * Export as portable string (Base64)
     */
    public String exportPortable(QuantumState state) {
        StringBuilder sb = new StringBuilder();
        sb.append("QBOX:").append(Base64.getEncoder().encodeToString(state.container));
        sb.append("|ALPHA:").append(Base64.getEncoder().encodeToString(state.keySecret));
        sb.append("|BETA:").append(Base64.getEncoder().encodeToString(state.keyDecoy));
        return sb.toString();
    }

    /**
     * Import from portable string
     */
    public QuantumState importPortable(String data) {
        String[] parts = data.split("\\|");
        byte[] container = Base64.getDecoder().decode(parts[0].substring(5)); // Remove "QBOX:"
        byte[] keySecret = Base64.getDecoder().decode(parts[1].substring(6)); // Remove "ALPHA:"
        byte[] keyDecoy = Base64.getDecoder().decode(parts[2].substring(5));  // Remove "BETA:"
        
        return new QuantumState(container, keySecret, keyDecoy, null, null);
    }

    // ═══════════════════════════════════════════════════════════════════
    // 4. VERIFICATION & PROOF
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Verify that both realities exist in the same container
     */
    public boolean verify(QuantumState state) {
        if (state.originalSecret == null || state.originalDecoy == null) {
            System.out.println("Cannot verify - original data not available.");
            return false;
        }
        
        String recoveredSecret = observeSecret(state);
        String recoveredDecoy = observeDecoy(state);
        
        boolean secretMatch = recoveredSecret.equals(state.originalSecret);
        boolean decoyMatch = recoveredDecoy.equals(state.originalDecoy);
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   VERIFICATION");
        System.out.println("═══════════════════════════════════════════");
        System.out.println();
        System.out.println("Secret Key → \"" + recoveredSecret + "\"");
        System.out.println("  Match: " + (secretMatch ? "✓" : "✗"));
        System.out.println();
        System.out.println("Decoy Key → \"" + recoveredDecoy + "\"");
        System.out.println("  Match: " + (decoyMatch ? "✓" : "✗"));
        
        return secretMatch && decoyMatch;
    }

    /**
     * Demonstrate the interrogation scenario
     */
    public void demonstrateInterrogation(QuantumState state) {
        System.out.println();
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   THE INTERROGATION");
        System.out.println("═══════════════════════════════════════════");
        
        System.out.println();
        System.out.println("--- SCENARIO A: YOU (The Owner) ---");
        System.out.println("You apply the Secret Key...");
        String realityA = observeSecret(state);
        System.out.println("REALITY COLLAPSED TO: " + realityA);
        
        System.out.println();
        System.out.println("--- SCENARIO B: THE ENEMY (The Raid) ---");
        System.out.println("They demand the key. You give them the Decoy Key.");
        System.out.println("They apply the Decoy Key...");
        String realityB = observeDecoy(state);
        System.out.println("REALITY COLLAPSED TO: " + realityB);
        
        System.out.println();
        System.out.println(">> MATH PROVEN. BOTH EXIST IN THE SAME BYTES.");
        System.out.println(">> The enemy cannot prove the secret exists.");
        System.out.println(">> Perfect plausible deniability.");
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITY
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * XOR two byte arrays
     */
    private byte[] xor(byte[] a, byte[] b) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ b[i]);
        }
        return out;
    }
    
    /**
     * Pad string to length
     */
    private String pad(String s, int n) {
        if (s.length() >= n) return s;
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < n) {
            sb.append(' ');
        }
        return sb.toString();
    }

    // ═══════════════════════════════════════════════════════════════════
    // DATA STRUCTURE
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Quantum state containing container and both keys
     */
    public static class QuantumState {
        public final byte[] container;    // The blob (looks like random noise)
        public final byte[] keySecret;    // Keep this safe!
        public final byte[] keyDecoy;     // Give this to enemies
        
        // For verification
        public final String originalSecret;
        public final String originalDecoy;

        public QuantumState(byte[] container, byte[] keySecret, byte[] keyDecoy,
                           String originalSecret, String originalDecoy) {
            this.container = container;
            this.keySecret = keySecret;
            this.keyDecoy = keyDecoy;
            this.originalSecret = originalSecret;
            this.originalDecoy = originalDecoy;
        }

        public String getContainerBase64() {
            return Base64.getEncoder().encodeToString(container);
        }

        public String getKeySecretBase64() {
            return Base64.getEncoder().encodeToString(keySecret);
        }

        public String getKeyDecoyBase64() {
            return Base64.getEncoder().encodeToString(keyDecoy);
        }

        public int size() {
            return container.length;
        }

        @Override
        public String toString() {
            return String.format(
                "QuantumState[size=%d, container=%s...]",
                container.length,
                getContainerBase64().substring(0, Math.min(20, getContainerBase64().length()))
            );
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN - Standalone Demo
    // ═══════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   SCHRÖDINGER'S FILE DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════");
        System.out.println();
        System.out.println("\"It is a cat. It is a code. It depends on who looks.\"");
        System.out.println();
        
        SchrodingerFile box = new SchrodingerFile();
        
        // 1. INPUTS
        String secretData = "LAUNCH_CODES: 99-AA-BB-CC-DD";
        String decoyData  = "GROCERY_LIST: Milk, Eggs, Bread";
        
        System.out.println("INPUT A (Secret): " + secretData);
        System.out.println("INPUT B (Decoy):  " + decoyData);
        System.out.println();
        
        // 2. ENTANGLE
        QuantumState state = box.entangle(secretData, decoyData);
        
        System.out.println();
        System.out.println(">> CONTAINER CREATED (Binary Blob)");
        String blobString = state.getContainerBase64();
        System.out.println("[ " + blobString.substring(0, Math.min(40, blobString.length())) + "... ]");
        System.out.println();
        System.out.println("This blob looks like random noise to anyone who doesn't have a key.");
        
        // 3. THE INTERROGATION
        box.demonstrateInterrogation(state);
        
        // 4. VERIFICATION
        box.verify(state);
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   WHY IT'S UNBREAKABLE");
        System.out.println("═══════════════════════════════════════════");
        System.out.println();
        System.out.println("If the FBI brute-forces the container, they find:");
        System.out.println("  - \"LAUNCH_CODES: 99-AA-BB-CC-DD\"");
        System.out.println("  - \"GROCERY_LIST: Milk, Eggs, Bread\"");
        System.out.println("  - \"KILL_THE_PRESIDENT\"");
        System.out.println("  - \"I_LOVE_CATS\"");
        System.out.println("  - ... and every other possible string.");
        System.out.println();
        System.out.println("They CANNOT prove which one you actually stored.");
        System.out.println("Perfect. Mathematical. Deniability.");
    }
}
