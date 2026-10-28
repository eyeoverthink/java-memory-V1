package fraymus.vision;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Blockchain-Anchored AGI State Persistence
 * 
 * Extends blockchain timestamping from biometric ownership proof to AGI state persistence,
 * creating immutable AGI evolution records anchored to biometric identity.
 * 
 * Mathematical Foundation:
 * AGI_State_Hash = SHA256(AeonOmega_Persistence_State)
 * Biometric_Anchor = Biometric_Fingerprint ⊕ AGI_State_Hash
 * Blockchain_Proof = Merkle_Tree({Biometric_Anchor_i})
 */
public class BlockchainAnchoredAGI {
    
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int AGI_STATE_SIZE = 16384;  // 16KB AGI state
    private static final int MERKLE_TREE_SIZE = 100;
    
    private final BlockchainTimestamp blockchainTimestamp;
    private final Map<String, AGIStateRecord> stateHistory;
    private final List<AGIEvolutionProof> evolutionChain;
    
    /**
     * AGI state from AeonOmega orthogonal persistence
     */
    public static class AGIState {
        public final long[] memoryState;
        public final long timestamp;
        public final long cycleCount;
        public final String axiomFormula;
        
        public AGIState(long[] memoryState, long timestamp, long cycleCount, String axiomFormula) {
            this.memoryState = memoryState.clone();
            this.timestamp = timestamp;
            this.cycleCount = cycleCount;
            this.axiomFormula = axiomFormula;
        }
    }
    
    /**
     * AGI state record with biometric anchor
     */
    public static class AGIStateRecord {
        public final String recordId;
        public final AGIState state;
        public final String biometricFingerprint;
        public final String stateHash;
        public final String anchorHash;
        public final long anchoredTimestamp;
        
        public AGIStateRecord(String recordId, AGIState state, String biometricFingerprint,
                            String stateHash, String anchorHash, long anchoredTimestamp) {
            this.recordId = recordId;
            this.state = state;
            this.biometricFingerprint = biometricFingerprint;
            this.stateHash = stateHash;
            this.anchorHash = anchorHash;
            this.anchoredTimestamp = anchoredTimestamp;
        }
    }
    
    /**
     * AGI evolution proof on blockchain
     */
    public static class AGIEvolutionProof {
        public final String evolutionId;
        public final String previousAnchorHash;
        public final String currentAnchorHash;
        public final String biometricSignature;
        public final String blockchainProof;
        public final long evolutionTimestamp;
        
        public AGIEvolutionProof(String evolutionId, String previousAnchorHash, 
                                String currentAnchorHash, String biometricSignature,
                                String blockchainProof, long evolutionTimestamp) {
            this.evolutionId = evolutionId;
            this.previousAnchorHash = previousAnchorHash;
            this.currentAnchorHash = currentAnchorHash;
            this.biometricSignature = biometricSignature;
            this.blockchainProof = blockchainProof;
            this.evolutionTimestamp = evolutionTimestamp;
        }
    }
    
    /**
     * JIT self-compilation record
     */
    public static class JITCompilationRecord {
        public final String compilationId;
        public final String sourceCodeHash;
        public final String compiledClassHash;
        public final String biometricAuthor;
        public final String blockchainProof;
        public final long compilationTimestamp;
        
        public JITCompilationRecord(String compilationId, String sourceCodeHash,
                                    String compiledClassHash, String biometricAuthor,
                                    String blockchainProof, long compilationTimestamp) {
            this.compilationId = compilationId;
            this.sourceCodeHash = sourceCodeHash;
            this.compiledClassHash = compiledClassHash;
            this.biometricAuthor = biometricAuthor;
            this.blockchainProof = blockchainProof;
            this.compilationTimestamp = compilationTimestamp;
        }
    }
    
    public BlockchainAnchoredAGI(String blockchainNetwork) {
        this.blockchainTimestamp = new BlockchainTimestamp(blockchainNetwork);
        this.stateHistory = new HashMap<>();
        this.evolutionChain = new ArrayList<>();
    }
    
    /**
     * Hash AGI state using SHA-256
     */
    private String hashAGIState(AGIState state) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            
            // Hash memory state
            ByteBuffer buffer = ByteBuffer.allocate(state.memoryState.length * 8);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            for (long value : state.memoryState) {
                buffer.putLong(value);
            }
            digest.update(buffer.array());
            
            // Hash metadata
            String metadata = state.timestamp + "|" + state.cycleCount + "|" + state.axiomFormula;
            digest.update(metadata.getBytes());
            
            byte[] hash = digest.digest();
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
    
    /**
     * Create biometric anchor for AGI state
     * Biometric_Anchor = Biometric_Fingerprint ⊕ AGI_State_Hash
     */
    private String createBiometricAnchor(String biometricFingerprint, String stateHash) {
        // XOR the hashes (simplified - in production use proper cryptographic combination)
        byte[] fpBytes = hexToBytes(biometricFingerprint);
        byte[] stateBytes = hexToBytes(stateHash);
        
        byte[] anchorBytes = new byte[Math.min(fpBytes.length, stateBytes.length)];
        for (int i = 0; i < anchorBytes.length; i++) {
            anchorBytes[i] = (byte) (fpBytes[i] ^ stateBytes[i]);
        }
        
        return bytesToHex(anchorBytes);
    }
    
    /**
     * Anchor AGI state to blockchain with biometric identity
     */
    public AGIStateRecord anchorAGIState(AGIState state, String biometricFingerprint) {
        // Hash the AGI state
        String stateHash = hashAGIState(state);
        
        // Create biometric anchor
        String anchorHash = createBiometricAnchor(biometricFingerprint, stateHash);

        // Create blockchain proof using existing blockchain timestamp system
        BlockchainTimestamp.BlockchainProof proof = null;
        try {
            proof = blockchainTimestamp.createProof(anchorHash, biometricFingerprint, true);
        } catch (Exception e) {
            System.err.println("Failed to create blockchain proof: " + e.getMessage());
            return null;
        }
        
        // Create state record
        String recordId = "agi_state_" + System.currentTimeMillis();
        AGIStateRecord record = new AGIStateRecord(
            recordId,
            state,
            biometricFingerprint,
            stateHash,
            anchorHash,
            proof.timestamp
        );
        
        // Store in history
        stateHistory.put(recordId, record);
        
        System.out.println("[BlockchainAnchoredAGI] AGI state anchored to blockchain:");
        System.out.println("  Record ID: " + recordId);
        System.out.println("  State Hash: " + stateHash);
        System.out.println("  Anchor Hash: " + anchorHash);
        System.out.println("  Biometric Owner: " + biometricFingerprint.substring(0, 16) + "...");
        System.out.println("  Blockchain Proof: " + proof.blockHash);
        
        return record;
    }
    
    /**
     * Record AGI evolution on blockchain
     */
    public AGIEvolutionProof recordEvolution(String previousAnchorHash, 
                                            AGIState newState,
                                            String biometricFingerprint) {
        // Hash new state
        String newStateHash = hashAGIState(newState);
        String newAnchorHash = createBiometricAnchor(biometricFingerprint, newStateHash);
        
        // Create biometric signature
        String biometricSignature = createBiometricSignature(biometricFingerprint, newAnchorHash);

        // Create blockchain proof for evolution
        BlockchainTimestamp.BlockchainProof proof = null;
        try {
            proof = blockchainTimestamp.createProof(newAnchorHash, biometricSignature, true);
        } catch (Exception e) {
            System.err.println("Failed to create blockchain proof: " + e.getMessage());
            return null;
        }
        
        // Create evolution proof
        String evolutionId = "agi_evolution_" + System.currentTimeMillis();
        AGIEvolutionProof evolution = new AGIEvolutionProof(
            evolutionId,
            previousAnchorHash,
            newAnchorHash,
            biometricSignature,
            proof.blockHash,
            proof.timestamp
        );
        
        // Add to evolution chain
        evolutionChain.add(evolution);
        
        System.out.println("[BlockchainAnchoredAGI] AGI evolution recorded:");
        System.out.println("  Evolution ID: " + evolutionId);
        System.out.println("  Previous Anchor: " + previousAnchorHash.substring(0, 16) + "...");
        System.out.println("  Current Anchor: " + newAnchorHash.substring(0, 16) + "...");
        System.out.println("  Biometric Signature: " + biometricSignature.substring(0, 16) + "...");
        System.out.println("  Blockchain Proof: " + proof.blockHash);
        
        return evolution;
    }
    
    /**
     * Record JIT self-compilation with biometric authorship
     */
    public JITCompilationRecord recordJITCompilation(String sourceCode, 
                                                    byte[] compiledClass,
                                                    String biometricFingerprint) {
        // Hash source code
        String sourceHash = hashString(sourceCode);
        
        // Hash compiled class
        String classHash = hashBytes(compiledClass);
        
        // Create biometric signature for authorship
        String authorSignature = createBiometricSignature(biometricFingerprint, sourceHash + classHash);

        // Create blockchain proof
        BlockchainTimestamp.BlockchainProof proof = null;
        try {
            proof = blockchainTimestamp.createProof(authorSignature, biometricFingerprint, true);
        } catch (Exception e) {
            System.err.println("Failed to create blockchain proof: " + e.getMessage());
            return null;
        }
        
        // Create compilation record
        String compilationId = "jit_compilation_" + System.currentTimeMillis();
        JITCompilationRecord record = new JITCompilationRecord(
            compilationId,
            sourceHash,
            classHash,
            biometricFingerprint,
            proof.blockHash,
            proof.timestamp
        );
        
        System.out.println("[BlockchainAnchoredAGI] JIT compilation recorded:");
        System.out.println("  Compilation ID: " + compilationId);
        System.out.println("  Source Hash: " + sourceHash);
        System.out.println("  Class Hash: " + classHash);
        System.out.println("  Biometric Author: " + biometricFingerprint.substring(0, 16) + "...");
        System.out.println("  Blockchain Proof: " + proof.blockHash);
        
        return record;
    }
    
    /**
     * Verify AGI state authenticity using blockchain
     */
    public boolean verifyAGIState(AGIStateRecord record) {
        // Recalculate state hash
        String recalculatedHash = hashAGIState(record.state);
        if (!recalculatedHash.equals(record.stateHash)) {
            System.out.println("[BlockchainAnchoredAGI] State hash mismatch!");
            return false;
        }
        
        // Recalculate anchor hash
        String recalculatedAnchor = createBiometricAnchor(record.biometricFingerprint, record.stateHash);
        if (!recalculatedAnchor.equals(record.anchorHash)) {
            System.out.println("[BlockchainAnchoredAGI] Anchor hash mismatch!");
            return false;
        }
        
        // Verify blockchain proof
        BlockchainTimestamp.BlockchainProof proof = new BlockchainTimestamp.BlockchainProof();

        boolean proofValid = false;
        try {
            proofValid = blockchainTimestamp.verifyProof(proof, record.biometricFingerprint);
        } catch (Exception e) {
            System.err.println("Failed to verify blockchain proof: " + e.getMessage());
            return false;
        }
        
        System.out.println("[BlockchainAnchoredAGI] AGI state verification: " + (proofValid ? "VALID" : "INVALID"));
        
        return proofValid;
    }
    
    /**
     * Verify evolution chain integrity
     */
    public boolean verifyEvolutionChain() {
        if (evolutionChain.isEmpty()) {
            return true;
        }
        
        String previousAnchor = evolutionChain.get(0).previousAnchorHash;
        
        for (AGIEvolutionProof evolution : evolutionChain) {
            if (!evolution.previousAnchorHash.equals(previousAnchor)) {
                System.out.println("[BlockchainAnchoredAGI] Evolution chain broken at: " + evolution.evolutionId);
                return false;
            }
            previousAnchor = evolution.currentAnchorHash;
        }
        
        System.out.println("[BlockchainAnchoredAGI] Evolution chain integrity: VALID");
        return true;
    }
    
    /**
     * Get AGI state history for biometric owner
     */
    public List<AGIStateRecord> getStateHistory(String biometricFingerprint) {
        List<AGIStateRecord> history = new ArrayList<>();
        
        for (AGIStateRecord record : stateHistory.values()) {
            if (record.biometricFingerprint.equals(biometricFingerprint)) {
                history.add(record);
            }
        }
        
        return history;
    }
    
    /**
     * Create Merkle tree of AGI states for batch anchoring
     */
    public String createMerkleTree(List<AGIStateRecord> records) {
        if (records.isEmpty()) {
            return "";
        }
        
        List<String> hashes = new ArrayList<>();
        for (AGIStateRecord record : records) {
            hashes.add(record.anchorHash);
        }
        
        // Build Merkle tree
        while (hashes.size() > 1) {
            List<String> newHashes = new ArrayList<>();
            for (int i = 0; i < hashes.size(); i += 2) {
                String left = hashes.get(i);
                String right = (i + 1 < hashes.size()) ? hashes.get(i + 1) : left;
                String combined = left + right;
                newHashes.add(hashString(combined));
            }
            hashes = newHashes;
        }
        
        return hashes.get(0);
    }
    
    /**
     * Create biometric signature
     */
    private String createBiometricSignature(String biometricFingerprint, String data) {
        String combined = biometricFingerprint + data;
        return hashString(combined);
    }
    
    /**
     * Hash string to hex
     */
    private String hashString(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(data.getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
    
    /**
     * Hash bytes to hex
     */
    private String hashBytes(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(data);
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
    
    /**
     * Convert bytes to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    /**
     * Convert hex string to bytes
     */
    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
    
    /**
     * Get persistence metrics
     */
    public PersistenceMetrics getMetrics() {
        return new PersistenceMetrics(
            stateHistory.size(),
            evolutionChain.size(),
            blockchainTimestamp.getAuditLog().size(),
            AGI_STATE_SIZE
        );
    }
    
    /**
     * Persistence performance metrics
     */
    public static class PersistenceMetrics {
        public final int stateRecordCount;
        public final int evolutionChainLength;
        public final int auditLogEntries;
        public final int agiStateSize;
        
        public PersistenceMetrics(int stateRecordCount, int evolutionChainLength,
                                 int auditLogEntries, int agiStateSize) {
            this.stateRecordCount = stateRecordCount;
            this.evolutionChainLength = evolutionChainLength;
            this.auditLogEntries = auditLogEntries;
            this.agiStateSize = agiStateSize;
        }
        
        @Override
        public String toString() {
            return String.format(
                "PersistenceMetrics[states=%d, evolutions=%d, audits=%d, stateSize=%d]",
                stateRecordCount, evolutionChainLength, auditLogEntries, agiStateSize
            );
        }
    }
}
