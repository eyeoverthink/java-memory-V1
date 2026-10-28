package fraymus.vision;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

/**
 * Biometric Vault - Local-Only Encrypted Storage
 * 
 * This vault stores biometric data locally with encryption.
 * No raw face data is ever uploaded to external servers.
 * User has full control over their biometric data lifecycle.
 * 
 * Features:
 * - Local-only storage (no cloud upload)
 * - AES-256 encryption for all stored data
 * - User-controlled encryption key
 * - Export/delete pipeline for data lifecycle control
 * - Consent tracking for all operations
 */
public class BiometricVault {
    
    private final String vaultPath;
    private final String userSecret;
    private final SecretKey encryptionKey;
    private final ConsentManager consentManager;
    
    // Vault structure
    private Map<String, EncryptedBiometricProfile> profiles;
    private List<AuditLogEntry> auditLog;
    
    public BiometricVault(String vaultPath, String userSecret) throws Exception {
        this.vaultPath = vaultPath;
        this.userSecret = userSecret;
        this.encryptionKey = deriveEncryptionKey(userSecret);
        this.consentManager = new ConsentManager();
        this.profiles = new HashMap<>();
        this.auditLog = new ArrayList<>();
        
        // Ensure vault directory exists
        File vaultDir = new File(vaultPath);
        if (!vaultDir.exists()) {
            vaultDir.mkdirs();
        }
        
        // Load existing vault data
        loadVault();
    }
    
    /**
     * Derive encryption key from user secret using PBKDF2
     */
    private SecretKey deriveEncryptionKey(String secret) throws Exception {
        byte[] salt = "FRAYNIX_BIOMETRIC_VAULT_SALT".getBytes(StandardCharsets.UTF_8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }
    
    /**
     * Store biometric profile with explicit consent
     */
    public void storeProfile(String profileId, 
                           FacialFingerprintExtractor.FacialFingerprintResult result,
                           boolean explicitConsent) throws Exception {
        
        // Require explicit consent
        if (!explicitConsent) {
            throw new SecurityException("Explicit consent required to store biometric data");
        }
        
        // Log consent
        consentManager.recordConsent(profileId, "STORE_BIOMETRIC_PROFILE", System.currentTimeMillis());
        
        // Encrypt the profile data
        String profileJson = serializeProfile(result);
        String encryptedData = encrypt(profileJson);
        
        // Store in vault
        EncryptedBiometricProfile encryptedProfile = new EncryptedBiometricProfile();
        encryptedProfile.profileId = profileId;
        encryptedProfile.encryptedData = encryptedData;
        encryptedProfile.timestamp = System.currentTimeMillis();
        encryptedProfile.consentGiven = true;
        
        profiles.put(profileId, encryptedProfile);
        
        // Audit log
        auditLog.add(new AuditLogEntry(
            "STORE_PROFILE",
            profileId,
            System.currentTimeMillis(),
            "Explicit consent given"
        ));
        
        // Persist to disk
        saveVault();
        
        System.out.println("[BiometricVault] Profile stored locally: " + profileId);
        System.out.println("    No raw face data uploaded to external servers");
    }
    
    /**
     * Retrieve biometric profile (local-only)
     */
    public FacialFingerprintExtractor.FacialFingerprintResult retrieveProfile(String profileId) throws Exception {
        EncryptedBiometricProfile encryptedProfile = profiles.get(profileId);
        
        if (encryptedProfile == null) {
            throw new IllegalArgumentException("Profile not found: " + profileId);
        }
        
        // Decrypt the profile data
        String decryptedData = decrypt(encryptedProfile.encryptedData);
        FacialFingerprintExtractor.FacialFingerprintResult result = deserializeProfile(decryptedData);
        
        // Audit log
        auditLog.add(new AuditLogEntry(
            "RETRIEVE_PROFILE",
            profileId,
            System.currentTimeMillis(),
            "Local-only retrieval"
        ));
        
        return result;
    }
    
    /**
     * Delete biometric profile with explicit consent
     */
    public void deleteProfile(String profileId, boolean explicitConsent) throws Exception {
        if (!explicitConsent) {
            throw new SecurityException("Explicit consent required to delete biometric data");
        }
        
        EncryptedBiometricProfile profile = profiles.get(profileId);
        if (profile == null) {
            throw new IllegalArgumentException("Profile not found: " + profileId);
        }
        
        // Log consent
        consentManager.recordConsent(profileId, "DELETE_BIOMETRIC_PROFILE", System.currentTimeMillis());
        
        // Remove from vault
        profiles.remove(profileId);
        
        // Audit log
        auditLog.add(new AuditLogEntry(
            "DELETE_PROFILE",
            profileId,
            System.currentTimeMillis(),
            "Explicit consent given, data deleted"
        ));
        
        // Persist to disk
        saveVault();
        
        System.out.println("[BiometricVault] Profile deleted: " + profileId);
    }
    
    /**
     * Export biometric profile for user control
     */
    public String exportProfile(String profileId, boolean explicitConsent) throws Exception {
        if (!explicitConsent) {
            throw new SecurityException("Explicit consent required to export biometric data");
        }
        
        FacialFingerprintExtractor.FacialFingerprintResult result = retrieveProfile(profileId);
        
        // Log consent
        consentManager.recordConsent(profileId, "EXPORT_BIOMETRIC_PROFILE", System.currentTimeMillis());
        
        // Serialize for export
        String exportData = serializeProfile(result);
        
        // Audit log
        auditLog.add(new AuditLogEntry(
            "EXPORT_PROFILE",
            profileId,
            System.currentTimeMillis(),
            "Explicit consent given, data exported to user"
        ));
        
        return exportData;
    }
    
    /**
     * Revoke all biometric data (emergency delete)
     */
    public void revokeAllData(boolean explicitConsent) throws Exception {
        if (!explicitConsent) {
            throw new SecurityException("Explicit consent required to revoke all biometric data");
        }
        
        // Log consent
        consentManager.recordConsent("GLOBAL", "REVOKE_ALL_BIOMETRIC_DATA", System.currentTimeMillis());
        
        // Delete all profiles
        profiles.clear();
        
        // Audit log
        auditLog.add(new AuditLogEntry(
            "REVOKE_ALL",
            "GLOBAL",
            System.currentTimeMillis(),
            "Emergency revocation - all data deleted"
        ));
        
        // Save empty vault
        saveVault();
        
        System.out.println("[BiometricVault] All biometric data revoked and deleted");
    }
    
    /**
     * Get consent status for a profile
     */
    public boolean hasConsent(String profileId, String operation) {
        return consentManager.hasConsent(profileId, operation);
    }
    
    /**
     * Get audit log for transparency
     */
    public List<AuditLogEntry> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
    
    /**
     * Encrypt data using AES-256
     */
    private String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        // Combine IV and encrypted data
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
        
        return Base64.getEncoder().encodeToString(combined);
    }
    
    /**
     * Decrypt data using AES-256
     */
    private String decrypt(String encryptedData) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedData);
        
        byte[] iv = new byte[16];
        byte[] encrypted = new byte[combined.length - 16];
        System.arraycopy(combined, 0, iv, 0, 16);
        System.arraycopy(combined, 16, encrypted, 0, encrypted.length);
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new IvParameterSpec(iv));
        byte[] decrypted = cipher.doFinal(encrypted);
        
        return new String(decrypted, StandardCharsets.UTF_8);
    }
    
    /**
     * Serialize profile to JSON
     */
    private String serializeProfile(FacialFingerprintExtractor.FacialFingerprintResult result) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"fingerprint\":\"").append(result.fingerprint).append("\",");
        json.append("\"privateKey\":\"").append(result.privateKey).append("\",");
        json.append("\"eyeSeparation\":").append(result.eyeSeparation).append(",");
        json.append("\"faceWidth\":").append(result.faceWidth).append(",");
        json.append("\"faceHeight\":").append(result.faceHeight).append(",");
        json.append("\"faceAspectRatio\":").append(result.faceAspectRatio).append(",");
        json.append("\"symmetryScore\":").append(result.symmetryScore).append(",");
        json.append("\"grayMean\":").append(result.grayMean).append(",");
        json.append("\"grayStd\":").append(result.grayStd).append(",");
        json.append("\"nuanceScore\":").append(result.nuanceScore).append(",");
        if (result.scottSignature != null) {
            json.append("\"phiResonance\":").append(result.scottSignature.phiResonance);
        } else {
            json.append("\"phiResonance\":0.0");
        }
        json.append("}");
        return json.toString();
    }
    
    /**
     * Deserialize profile from JSON
     */
    private FacialFingerprintExtractor.FacialFingerprintResult deserializeProfile(String json) {
        // Simplified JSON parsing for demo
        FacialFingerprintExtractor.FacialFingerprintResult result = 
            new FacialFingerprintExtractor.FacialFingerprintResult();
        
        // Extract values from JSON (simplified)
        result.fingerprint = extractValue(json, "fingerprint");
        result.privateKey = extractValue(json, "privateKey");
        
        return result;
    }
    
    private String extractValue(String json, String key) {
        int start = json.indexOf("\"" + key + "\":\"") + key.length() + 3;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
    
    /**
     * Save vault to disk
     */
    private void saveVault() throws Exception {
        File vaultFile = new File(vaultPath, "vault.dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(vaultFile))) {
            oos.writeObject(profiles);
            oos.writeObject(auditLog);
        }
    }
    
    /**
     * Load vault from disk
     */
    @SuppressWarnings("unchecked")
    private void loadVault() throws Exception {
        File vaultFile = new File(vaultPath, "vault.dat");
        if (!vaultFile.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(vaultFile))) {
            profiles = (Map<String, EncryptedBiometricProfile>) ois.readObject();
            auditLog = (List<AuditLogEntry>) ois.readObject();
        }
    }
    
    /**
     * Encrypted biometric profile
     */
    private static class EncryptedBiometricProfile implements Serializable {
        String profileId;
        String encryptedData;
        long timestamp;
        boolean consentGiven;
    }
    
    /**
     * Audit log entry
     */
    public static class AuditLogEntry implements Serializable {
        String operation;
        String profileId;
        long timestamp;
        String details;
        
        public AuditLogEntry(String operation, String profileId, long timestamp, String details) {
            this.operation = operation;
            this.profileId = profileId;
            this.timestamp = timestamp;
            this.details = details;
        }
        
        @Override
        public String toString() {
            return String.format("[%tF %<tT] %s - %s: %s", 
                timestamp, operation, profileId, details);
        }
    }
    
    /**
     * Consent manager for explicit consent tracking
     */
    private static class ConsentManager {
        private final Map<String, Map<String, Long>> consentLog;
        
        public ConsentManager() {
            this.consentLog = new HashMap<>();
        }
        
        public void recordConsent(String profileId, String operation, long timestamp) {
            consentLog.computeIfAbsent(profileId, k -> new HashMap<>())
                      .put(operation, timestamp);
        }
        
        public boolean hasConsent(String profileId, String operation) {
            Map<String, Long> operations = consentLog.get(profileId);
            return operations != null && operations.containsKey(operation);
        }
    }
}
