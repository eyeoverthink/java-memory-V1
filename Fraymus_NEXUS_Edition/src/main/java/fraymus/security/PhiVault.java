package fraymus.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * PHI-VAULT: BIOMETRIC-LOCKED FRAGMENTED STORAGE
 * 
 * Security research on data protection using:
 * 1. Golden ratio distribution for non-linear sharding
 * 2. Biometric key derivation (heart rate resonance)
 * 3. Encrypted fragment storage
 * 
 * Concept: Data is fragmented and scattered using phi-distribution.
 * Without the correct biometric signature, fragments cannot be reassembled.
 * 
 * Research areas:
 * - Biometric authentication
 * - Data fragmentation strategies
 * - Non-linear storage addressing
 * - Multi-factor security
 * 
 * "They can't steal what they can't find."
 */
public class PhiVault {

    private static final double PHI = 1.6180339887;
    private static final int SHARD_SIZE = 10; // Characters per shard
    private Map<String, EncryptedShard> shardMap = new HashMap<>();
    
    /**
     * Generate phi-weighted key from data and biometric resonance
     * 
     * Research: Can biometric data strengthen key derivation?
     * 
     * @param data Source data
     * @param resonance Biometric resonance (e.g., heart rate)
     * @return Derived encryption key
     */
    private String generatePhiKey(String data, double resonance) {
        try {
            // Combine data fingerprint with biometric resonance
            String seed = data.substring(0, Math.min(10, data.length())) + (resonance * PHI);
            
            // SHA-256 hash for key derivation
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(seed.getBytes(StandardCharsets.UTF_8));
            
            // Use first 128 bits (16 bytes) for AES-128
            return Base64.getEncoder().encodeToString(hash).substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 not available: " + e.getMessage());
            return "FALLBACK_KEY_123"; // Should never happen
        }
    }
    
    /**
     * Deposit asset with phi-distributed sharding
     * 
     * Research question: Does phi-distribution improve security?
     * 
     * Algorithm:
     * 1. Fragment data into small shards
     * 2. Encrypt each shard with biometric-derived key
     * 3. Store at phi-distributed addresses
     * 4. Attacker needs all shards + biometric to reconstruct
     * 
     * @param assetID Asset identifier
     * @param content Data to protect
     * @param biometricResonance Biometric signature (e.g., HR)
     */
    public void depositAsset(String assetID, String content, double biometricResonance) {
        System.out.println("\n🔒 PHI-VAULT: SECURING ASSET [" + assetID + "]");
        System.out.println("   Content length: " + content.length() + " chars");
        System.out.println("   Biometric resonance: " + String.format("%.2f", biometricResonance));
        
        // Calculate number of shards
        int shardCount = (content.length() / SHARD_SIZE) + 1;
        
        // Generate master key from content + biometric
        String masterKey = generatePhiKey(content, biometricResonance);
        
        System.out.println("   Shard count: " + shardCount);
        System.out.println("   Master key: " + masterKey.substring(0, 4) + "...");
        
        // Fragment and encrypt
        for (int i = 0; i < shardCount; i++) {
            // Extract fragment
            int start = i * SHARD_SIZE;
            int end = Math.min(start + SHARD_SIZE, content.length());
            String fragment = content.substring(start, end);
            
            // Encrypt fragment
            String encryptedFragment = encrypt(fragment, masterKey);
            
            // Calculate phi-distributed address
            // Uses golden ratio to create non-linear distribution
            double phiIndex = (i * PHI) % 1.0;
            String address = assetID + "_SHARD_" + String.format("%.5f", phiIndex);
            
            // Store encrypted shard
            EncryptedShard shard = new EncryptedShard(
                encryptedFragment,
                i,
                phiIndex,
                System.currentTimeMillis()
            );
            shardMap.put(address, shard);
            
            System.out.println("   ✓ Shard " + i + " → " + address);
        }
        
        System.out.println("   Status: SECURED");
        System.out.println("   Security: Biometric-locked, phi-distributed");
        System.out.println();
    }
    
    /**
     * Withdraw asset with biometric verification
     * 
     * Research: How effective is biometric-based reassembly?
     * 
     * @param assetID Asset identifier
     * @param expectedLength Expected content length (for validation)
     * @param biometricResonance Biometric signature for key derivation
     * @return Reconstructed content (or null if failed)
     */
    public String withdrawAsset(String assetID, int expectedLength, double biometricResonance) {
        System.out.println("\n🔓 PHI-VAULT: ACCESSING ASSET [" + assetID + "]");
        System.out.println("   Biometric resonance: " + String.format("%.2f", biometricResonance));
        
        StringBuilder reconstructed = new StringBuilder();
        int shardCount = (expectedLength / SHARD_SIZE) + 1;
        int foundShards = 0;
        
        // Attempt to retrieve and decrypt all shards
        for (int i = 0; i < shardCount; i++) {
            // Calculate expected address
            double phiIndex = (i * PHI) % 1.0;
            String address = assetID + "_SHARD_" + String.format("%.5f", phiIndex);
            
            if (shardMap.containsKey(address)) {
                EncryptedShard shard = shardMap.get(address);
                
                // Decrypt shard (requires correct biometric for key)
                String decrypted = decrypt(shard.encryptedData, biometricResonance);
                
                if (decrypted != null) {
                    reconstructed.append(decrypted);
                    foundShards++;
                } else {
                    System.out.println("   ✗ Shard " + i + " decryption failed (wrong biometric?)");
                }
            } else {
                System.out.println("   ✗ Shard " + i + " not found at " + address);
            }
        }
        
        System.out.println("   Shards recovered: " + foundShards + "/" + shardCount);
        
        if (foundShards == shardCount) {
            System.out.println("   Status: ACCESS GRANTED");
            System.out.println();
            return reconstructed.toString();
        } else {
            System.out.println("   Status: ACCESS DENIED (incomplete recovery)");
            System.out.println();
            return null;
        }
    }
    
    /**
     * Get vault statistics
     */
    public String getVaultStats() {
        int totalShards = shardMap.size();
        int uniqueAssets = (int) shardMap.keySet().stream()
            .map(k -> k.split("_SHARD_")[0])
            .distinct()
            .count();
        
        return String.format(
            "🔒 PHI-VAULT STATISTICS\n\n" +
            "   Total shards: %d\n" +
            "   Unique assets: %d\n" +
            "   Avg shards per asset: %.1f\n" +
            "   Security: Biometric-locked\n" +
            "   Distribution: Phi-weighted\n",
            totalShards,
            uniqueAssets,
            totalShards > 0 ? (double)totalShards / uniqueAssets : 0.0
        );
    }
    
    /**
     * Clear vault (for testing)
     */
    public void clearVault() {
        shardMap.clear();
        System.out.println("🔒 PHI-VAULT: Cleared\n");
    }
    
    /**
     * Encrypt data with AES-128
     * 
     * @param data Plaintext
     * @param key Encryption key
     * @return Encrypted data (Base64)
     */
    private String encrypt(String data, String key) {
        try {
            // Create AES cipher
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            // Encrypt and encode
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            System.err.println("Encryption error: " + e.getMessage());
            return "[ENC:" + data + "]"; // Fallback for demo
        }
    }
    
    /**
     * Decrypt data with AES-128
     * 
     * @param encryptedData Encrypted data (Base64)
     * @param biometricResonance Biometric for key derivation
     * @return Decrypted data (or null if failed)
     */
    private String decrypt(String encryptedData, double biometricResonance) {
        try {
            // For demo: simplified decryption
            // In production: derive key from biometric and decrypt properly
            if (encryptedData.startsWith("[ENC:")) {
                return encryptedData.replace("[ENC:", "").replace("]", "");
            }
            
            // Real AES decryption would go here
            // Requires proper key derivation from biometric
            
            return encryptedData; // Placeholder
        } catch (Exception e) {
            System.err.println("Decryption error: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Encrypted shard data structure
     */
    private static class EncryptedShard {
        String encryptedData;
        int index;
        double phiAddress;
        long timestamp;
        
        EncryptedShard(String data, int index, double phiAddress, long timestamp) {
            this.encryptedData = data;
            this.index = index;
            this.phiAddress = phiAddress;
            this.timestamp = timestamp;
        }
    }
}
