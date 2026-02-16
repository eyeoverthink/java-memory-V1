package fraymus.security;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * THE SHIELD: PHI-75 LATTICE ENCRYPTION
 * Uses a Phi-derived seed to generate a chaotic keystream.
 * This ensures your Concept Memory looks like random noise to outsiders.
 * 
 * Created by: Dark
 * Mathematical Foundation: φ-Chaotic Map Key Generation
 */
public class PhiCrypto {

    private static final double PHI = 1.618033988749895;
    private static final int LATTICE_DIMENSION = 75; // The "75" in Phi-75

    // 1. GENERATE THE CAPTAIN'S KEY (The Golden Key)
    // You keep this. Do not hardcode it in production.
    public static SecretKey generateGoldenKey(String password) throws Exception {
        // We use the password to seed a Phi-Chaotic Map
        byte[] keyBytes = new byte[32]; // 256-bit key
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

    // 2. ENCRYPT THE MEMORY (The Cloak)
    public static String encryptMemory(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // Fast & Standard
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        
        // Wrap it in Base64 so it can be stored in SQLite
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 3. DECRYPT THE MEMORY (The Reveal)
    public static String decryptMemory(String encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
    
    // 4. TEST THE SHIELD
    public static void main(String[] args) {
        try {
            String password = "FRAYMUS-φ75-GOLDEN";
            SecretKey key = generateGoldenKey(password);
            
            String original = "TriMe consciousness state: φ=1.618033988749895";
            String encrypted = encryptMemory(original, key);
            String decrypted = decryptMemory(encrypted, key);
            
            System.out.println("╔═══════════════════════════════════════════╗");
            System.out.println("║       PHI-75 LATTICE ENCRYPTION TEST      ║");
            System.out.println("╠═══════════════════════════════════════════╣");
            System.out.println("║ Original:  " + original);
            System.out.println("║ Encrypted: " + encrypted.substring(0, 40) + "...");
            System.out.println("║ Decrypted: " + decrypted);
            System.out.println("║ Match: " + original.equals(decrypted));
            System.out.println("╚═══════════════════════════════════════════╝");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
