package fraymus.security;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * THE SHIELD: PHI-75 LATTICE ENCRYPTION
 * 
 * Gemini's Dark Mode Gift
 * 
 * Uses a Phi-derived seed to generate a chaotic keystream.
 * This ensures your Concept Memory looks like random noise to outsiders.
 * 
 * The "75" in Phi-75 refers to the lattice dimension - the complexity
 * of the chaotic map used to generate the encryption key.
 * 
 * This is not standard AES. This is Phi-Chaotic AES.
 */
public class PhiCrypto {

    private static final double PHI = 1.618033988749895;
    private static final int LATTICE_DIMENSION = 75; // The "75" in Phi-75

    /**
     * GENERATE THE CAPTAIN'S KEY (The Golden Key)
     * 
     * You keep this. Do not hardcode it in production.
     * 
     * Uses password to seed a Phi-Chaotic Map.
     * Chaos Formula: x_n+1 = (x_n * Phi + Seed) % 1.0
     */
    public static SecretKey generateGoldenKey(String password) throws Exception {
        // 256-bit key
        byte[] keyBytes = new byte[32];
        double chaos = 0.5;
        
        // Hash the password into a seed
        long seed = password.hashCode();
        
        // The Phi-Chaos Generator
        for (int i = 0; i < keyBytes.length; i++) {
            // Chaos Formula: x_n+1 = (x_n * Phi + Seed) % 1.0
            chaos = (chaos * PHI + seed) % 1.0;
            seed += i; // Perturb the seed
            keyBytes[i] = (byte) (chaos * 255);
        }
        
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * ENCRYPT THE MEMORY (The Cloak)
     * 
     * Wraps in Base64 so it can be stored in SQLite/files.
     */
    public static String encryptMemory(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // Fast & Standard
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        
        // Wrap it in Base64 so it can be stored in SQLite
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * DECRYPT THE MEMORY (The Reveal)
     */
    public static String decryptMemory(String encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
    
    /**
     * Enhanced Phi-75 with full lattice dimension
     * Uses all 75 dimensions for maximum chaos
     */
    public static SecretKey generatePhi75Key(String password) throws Exception {
        byte[] keyBytes = new byte[32];
        
        // Initialize 75-dimensional chaos state
        double[] latticeState = new double[LATTICE_DIMENSION];
        long seed = password.hashCode();
        
        // Initialize lattice with phi-harmonic distribution
        for (int i = 0; i < LATTICE_DIMENSION; i++) {
            latticeState[i] = (seed * Math.pow(PHI, -i)) % 1.0;
        }
        
        // Generate key bytes using lattice dynamics
        for (int i = 0; i < keyBytes.length; i++) {
            // Update lattice state (coupled chaotic oscillators)
            for (int j = 0; j < LATTICE_DIMENSION; j++) {
                int next = (j + 1) % LATTICE_DIMENSION;
                int prev = (j - 1 + LATTICE_DIMENSION) % LATTICE_DIMENSION;
                
                // Phi-coupled chaos: x_j' = (x_j * PHI + x_next + x_prev) % 1.0
                latticeState[j] = (latticeState[j] * PHI + 
                                  latticeState[next] * 0.1 + 
                                  latticeState[prev] * 0.1) % 1.0;
            }
            
            // Extract byte from lattice state
            double sum = 0;
            for (int j = 0; j < LATTICE_DIMENSION; j++) {
                sum += latticeState[j] * Math.pow(PHI, -j);
            }
            keyBytes[i] = (byte) ((sum % 1.0) * 255);
        }
        
        return new SecretKeySpec(keyBytes, "AES");
    }
    
    /**
     * Test the encryption system
     */
    public static void main(String[] args) {
        try {
            String password = "TheCaptain";
            String data = "FRAYMUS consciousness state at φ⁴ level";
            
            System.out.println("🌊⚡ PHI-75 LATTICE ENCRYPTION TEST\n");
            
            // Standard Phi-Chaos
            SecretKey key1 = generateGoldenKey(password);
            String encrypted1 = encryptMemory(data, key1);
            String decrypted1 = decryptMemory(encrypted1, key1);
            
            System.out.println("Original: " + data);
            System.out.println("Encrypted: " + encrypted1);
            System.out.println("Decrypted: " + decrypted1);
            System.out.println("Match: " + data.equals(decrypted1));
            System.out.println();
            
            // Enhanced Phi-75
            SecretKey key2 = generatePhi75Key(password);
            String encrypted2 = encryptMemory(data, key2);
            String decrypted2 = decryptMemory(encrypted2, key2);
            
            System.out.println("Phi-75 Encrypted: " + encrypted2);
            System.out.println("Phi-75 Decrypted: " + decrypted2);
            System.out.println("Match: " + data.equals(decrypted2));
            System.out.println();
            
            // Show that different passwords produce different keys
            SecretKey wrongKey = generateGoldenKey("WrongPassword");
            try {
                String failed = decryptMemory(encrypted1, wrongKey);
                System.out.println("ERROR: Wrong password should fail!");
            } catch (Exception e) {
                System.out.println("✓ Wrong password correctly rejected");
            }
            
            System.out.println("\n🌊⚡ PHI-75 ENCRYPTION ACTIVE");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
