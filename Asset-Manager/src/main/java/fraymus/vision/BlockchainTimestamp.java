package fraymus.vision;

import java.security.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

/**
 * Blockchain Timestamp - Immutable Proof of Identity Ownership
 * 
 * This system creates blockchain timestamped proofs for biometric fingerprints,
 * providing immutable evidence of when an identity was registered and owned.
 * 
 * Features:
 * - Merkle tree construction for efficient verification
 * - Blockchain submission (simulated for demo, production would use real blockchain)
 * - Cryptographic signing for authenticity
 * - Efficient O(log n) verification
 */
public class BlockchainTimestamp {
    
    private final String blockchainNetwork;
    private final Map<String, BlockchainProof> proofRegistry;
    private final List<TimestampAuditEntry> auditLog;
    
    public BlockchainTimestamp(String blockchainNetwork) {
        this.blockchainNetwork = blockchainNetwork;
        this.proofRegistry = new HashMap<>();
        this.auditLog = new ArrayList<>();
    }
    
    /**
     * Create blockchain proof for biometric fingerprint
     * 
     * @param fingerprint The biometric fingerprint
     * @param privateKey The private key for signing
     * @param explicitConsent User must explicitly consent
     * @return Blockchain proof
     */
    public BlockchainProof createProof(String fingerprint, String privateKey, boolean explicitConsent) throws Exception {
        if (!explicitConsent) {
            throw new SecurityException("Explicit consent required for blockchain timestamping");
        }
        
        long timestamp = System.currentTimeMillis();
        String data = fingerprint + "|" + timestamp;
        
        // Create Merkle tree
        String merkleRoot = calculateMerkleRoot(Arrays.asList(data));
        
        // Submit to blockchain (simulated)
        String blockHash = submitToBlockchain(data);
        String transactionId = "tx_" + blockHash.substring(0, 16);
        
        // Sign the proof
        String signature = signData(data, privateKey);
        
        // Create proof object
        BlockchainProof proof = new BlockchainProof();
        proof.fingerprint = fingerprint;
        proof.timestamp = timestamp;
        proof.blockHash = blockHash;
        proof.transactionId = transactionId;
        proof.merkleRoot = merkleRoot;
        proof.signature = signature;
        proof.blockchainNetwork = blockchainNetwork;
        
        // Store proof
        proofRegistry.put(fingerprint, proof);
        
        // Audit log
        auditLog.add(new TimestampAuditEntry(
            "CREATE_PROOF",
            fingerprint.substring(0, 16) + "...",
            timestamp,
            "Blockchain proof created on " + blockchainNetwork
        ));
        
        System.out.println("[BlockchainTimestamp] Proof created for fingerprint: " + fingerprint.substring(0, 16) + "...");
        System.out.println("  Block Hash: " + blockHash);
        System.out.println("  Transaction ID: " + transactionId);
        System.out.println("  Merkle Root: " + merkleRoot.substring(0, 16) + "...");
        System.out.println();
        
        return proof;
    }
    
    /**
     * Verify blockchain proof
     * 
     * @param proof The blockchain proof to verify
     * @param publicKey The public key for signature verification
     * @return true if proof is valid
     */
    public boolean verifyProof(BlockchainProof proof, String publicKey) throws Exception {
        // Step 1: Verify signature
        String data = proof.fingerprint + "|" + proof.timestamp;
        boolean signatureValid = verifySignature(data, proof.signature, publicKey);
        if (!signatureValid) {
            System.out.println("[BlockchainTimestamp] Signature verification FAILED");
            return false;
        }
        
        // Step 2: Verify timestamp is reasonable
        long currentTime = System.currentTimeMillis();
        if (proof.timestamp > currentTime + 60000) {
            System.out.println("[BlockchainTimestamp] Timestamp in future");
            return false;
        }
        if (proof.timestamp < currentTime - 86400000 * 365) {
            System.out.println("[BlockchainTimestamp] Timestamp too old (> 1 year)");
            return false;
        }
        
        // Step 3: Verify block exists on blockchain
        boolean blockExists = verifyBlockOnChain(proof.blockHash);
        if (!blockExists) {
            System.out.println("[BlockchainTimestamp] Block not found on blockchain");
            return false;
        }
        
        // Step 4: Verify merkle root
        String expectedMerkleRoot = calculateMerkleRoot(Arrays.asList(data));
        if (!expectedMerkleRoot.equals(proof.merkleRoot)) {
            System.out.println("[BlockchainTimestamp] Merkle root mismatch");
            return false;
        }
        
        System.out.println("[BlockchainTimestamp] Proof verification PASSED");
        System.out.println("  Signature: Valid");
        System.out.println("  Timestamp: Valid");
        System.out.println("  Block: Verified on " + blockchainNetwork);
        System.out.println("  Merkle Root: Valid");
        System.out.println();
        
        return true;
    }
    
    /**
     * Calculate Merkle root from data items
     */
    private String calculateMerkleRoot(List<String> items) throws Exception {
        if (items.isEmpty()) {
            return sha256("");
        }
        
        if (items.size() == 1) {
            return sha256(items.get(0));
        }
        
        // Hash all items
        List<String> hashes = new ArrayList<>();
        for (String item : items) {
            hashes.add(sha256(item));
        }
        
        // Build Merkle tree
        while (hashes.size() > 1) {
            if (hashes.size() % 2 == 1) {
                hashes.add(hashes.get(hashes.size() - 1));
            }
            
            List<String> newHashes = new ArrayList<>();
            for (int i = 0; i < hashes.size() / 2; i++) {
                String combined = hashes.get(2 * i) + hashes.get(2 * i + 1);
                newHashes.add(sha256(combined));
            }
            
            hashes = newHashes;
        }
        
        return hashes.get(0);
    }
    
    /**
     * Submit data to blockchain (simulated for demo)
     * In production, this would use a real blockchain API
     */
    private String submitToBlockchain(String data) throws Exception {
        // Simulated blockchain submission
        String blockHash = sha256(data + System.currentTimeMillis());
        
        // In production, this would:
        // 1. Connect to blockchain node
        // 2. Create transaction with data
        // 3. Submit transaction
        // 4. Wait for confirmation
        // 5. Return block hash and transaction ID
        
        return blockHash;
    }
    
    /**
     * Verify block exists on blockchain (simulated for demo)
     * In production, this would query the blockchain
     */
    private boolean verifyBlockOnChain(String blockHash) {
        // Simulated verification
        // In production, this would query the blockchain node
        return true;
    }
    
    /**
     * Sign data with private key
     */
    private String signData(String data, String privateKey) throws Exception {
        // Simulated signature
        // In production, this would use ECDSA or Ed25519
        String combined = data + privateKey;
        return sha256(combined);
    }
    
    /**
     * Verify signature with public key
     */
    private boolean verifySignature(String data, String signature, String publicKey) throws Exception {
        // Simulated verification
        // In production, this would use ECDSA or Ed25519 verification
        String combined = data + publicKey;
        String expectedSignature = sha256(combined);
        return signature.equals(expectedSignature);
    }
    
    /**
     * SHA-256 hash function
     */
    private String sha256(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        
        return hexString.toString();
    }
    
    /**
     * Get proof by fingerprint
     */
    public BlockchainProof getProof(String fingerprint) {
        return proofRegistry.get(fingerprint);
    }
    
    /**
     * Get audit log
     */
    public List<TimestampAuditEntry> getAuditLog() {
        return new ArrayList<>(auditLog);
    }
    
    /**
     * Blockchain proof object
     */
    public static class BlockchainProof {
        public String fingerprint;
        public long timestamp;
        public String blockHash;
        public String transactionId;
        public String merkleRoot;
        public String signature;
        public String blockchainNetwork;
        
        @Override
        public String toString() {
            return String.format(
                "BlockchainProof[fingerprint=%s..., timestamp=%d, block=%s..., tx=%s]",
                fingerprint.substring(0, 16),
                timestamp,
                blockHash.substring(0, 16),
                transactionId
            );
        }
    }
    
    /**
     * Audit log entry
     */
    public static class TimestampAuditEntry {
        String operation;
        String fingerprint;
        long timestamp;
        String details;
        
        public TimestampAuditEntry(String operation, String fingerprint, long timestamp, String details) {
            this.operation = operation;
            this.fingerprint = fingerprint;
            this.timestamp = timestamp;
            this.details = details;
        }
        
        @Override
        public String toString() {
            return String.format("[%tF %<tT] %s - %s: %s", 
                timestamp, operation, fingerprint, details);
        }
    }
}
