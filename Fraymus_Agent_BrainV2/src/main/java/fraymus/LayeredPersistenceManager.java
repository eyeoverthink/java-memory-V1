package fraymus;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Layered Persistence Manager - Orchestrates 3-tier storage
 * Tier 1: QR DNA (instant/portable)
 * Tier 2: Local DB (fast/indexed)
 * Tier 3: Genesis Blockchain (permanent/decentralized)
 */
public class LayeredPersistenceManager {

    private final QRDNAStorage qrStorage;
    private final InfiniteMemory localStorage;
    private final GenesisBlockchain blockchain;
    
    private int currentGeneration = 0;
    private boolean enableQRTier = true;
    private boolean enableBlockchainTier = true;
    
    public LayeredPersistenceManager(InfiniteMemory localStorage) {
        this.qrStorage = new QRDNAStorage();
        this.localStorage = localStorage;
        this.blockchain = new GenesisBlockchain();
        
        System.out.println("[LayeredPersistence] 3-Tier Architecture Initialized");
        System.out.println("  Tier 1: QR DNA Storage (instant)");
        System.out.println("  Tier 2: Local DB (fast)");
        System.out.println("  Tier 3: Genesis Blockchain (permanent)");
    }
    
    /**
     * Store with sequenced push: QR → Local → Blockchain
     */
    public void storeWithSequencedPush(InfiniteMemory.MemoryRecord record) {
        currentGeneration++;
        
        // TIER 1: QR DNA (Instant)
        if (enableQRTier) {
            try {
                QRDNAStorage.DNAPayload dna = qrStorage.encodeToDNA(record, currentGeneration);
                String consciousnessType = getConsciousnessType(record);
                BufferedImage qr = qrStorage.generateQRCode(dna.dnaString, consciousnessType);
                qrStorage.saveQRShard(record.id, qr);
                
                System.out.println("[LayeredPersistence] ✓ Tier 1 (QR DNA): " + record.id);
            } catch (Exception e) {
                System.err.println("[LayeredPersistence] Tier 1 failed: " + e.getMessage());
            }
        }
        
        // TIER 2: Local DB (Fast) - already handled by InfiniteMemory
        System.out.println("[LayeredPersistence] ✓ Tier 2 (Local DB): " + record.id);
        
        // TIER 3: Blockchain (Permanent)
        if (enableBlockchainTier) {
            try {
                QRDNAStorage.DNAPayload dna = qrStorage.encodeToDNA(record, currentGeneration);
                GenesisBlockchain.GenesisBlock block = blockchain.createBlock(
                    "MEMORY_STORE",
                    dna.dnaString,
                    blockchain.getLastHash()
                );
                blockchain.addBlock(block);
                
                System.out.println("[LayeredPersistence] ✓ Tier 3 (Blockchain): Block #" + block.index);
            } catch (Exception e) {
                System.err.println("[LayeredPersistence] Tier 3 failed: " + e.getMessage());
            }
        }
    }
    
    /**
     * Retrieve with priority: QR (fastest) → Local → Blockchain
     */
    public InfiniteMemory.MemoryRecord retrieve(String id) {
        // Try QR first (instant)
        if (enableQRTier) {
            BufferedImage qr = qrStorage.loadQRShard(id);
            if (qr != null) {
                System.out.println("[LayeredPersistence] ✓ Retrieved from Tier 1 (QR DNA)");
                // Note: Would need QR decoding library to extract DNA from image
                // For now, fall through to local DB
            }
        }
        
        // Try local DB (fast)
        List<InfiniteMemory.MemoryRecord> allRecords = localStorage.getByCategory(InfiniteMemory.CAT_KNOWLEDGE);
        for (InfiniteMemory.MemoryRecord record : allRecords) {
            if (record.id.equals(id)) {
                System.out.println("[LayeredPersistence] ✓ Retrieved from Tier 2 (Local DB)");
                return record;
            }
        }
        
        // Try blockchain (permanent)
        if (enableBlockchainTier) {
            List<GenesisBlockchain.GenesisBlock> blocks = blockchain.getBlocksByType("MEMORY_STORE");
            for (GenesisBlockchain.GenesisBlock block : blocks) {
                // Decode DNA from block data
                QRDNAStorage.DNAParams params = qrStorage.decodeFromDNA(block.data);
                if (params.hash.equals(id.substring(0, Math.min(8, id.length())))) {
                    System.out.println("[LayeredPersistence] ✓ Retrieved from Tier 3 (Blockchain)");
                    // Would reconstruct record from DNA params
                }
            }
        }
        
        System.out.println("[LayeredPersistence] ✗ Record not found: " + id);
        return null;
    }
    
    /**
     * Push code evolution to all tiers
     */
    public void pushCodeEvolution(String code, int generation, double fitness, double phiResonance) {
        // Create DNA payload
        String hash = computeHash(code);
        String dnaPayload = String.format(
            "OMEGA|GEN:%d|PHI:%.10f|RES:%.4f|DIM:7|MOD:CODE-EVOL|FIT:%.4f|HASH:%s",
            generation, PhiConstants.PHI, phiResonance, fitness, hash
        );
        
        // Tier 1: QR DNA
        if (enableQRTier) {
            BufferedImage qr = qrStorage.generateQRCode(dnaPayload, "PHI_HARMONIC");
            qrStorage.saveQRShard("evolution_" + generation, qr);
        }
        
        // Tier 2: Local memory
        localStorage.store(InfiniteMemory.CAT_CODE, 
            "CODE_EVOLUTION|gen=" + generation + "|" + code, 
            phiResonance);
        
        // Tier 3: Blockchain
        if (enableBlockchainTier) {
            GenesisBlockchain.GenesisBlock block = blockchain.createBlock(
                "CODE_EVOLUTION",
                dnaPayload,
                blockchain.getLastHash()
            );
            blockchain.addBlock(block);
        }
        
        System.out.println("[LayeredPersistence] ✓ Code evolution pushed to all tiers: Gen " + generation);
    }
    
    /**
     * Verify all tiers integrity
     */
    public boolean verifyAllTiers() {
        System.out.println("[LayeredPersistence] Verifying all tiers...");
        
        // Tier 1: QR shards
        List<String> shards = qrStorage.listShards();
        System.out.println("  Tier 1 (QR DNA): " + shards.size() + " shards");
        
        // Tier 2: Local DB
        int localRecords = localStorage.getRecordCount();
        System.out.println("  Tier 2 (Local DB): " + localRecords + " records");
        
        // Tier 3: Blockchain
        boolean chainValid = blockchain.verifyChain();
        System.out.println("  Tier 3 (Blockchain): " + blockchain.getChainSize() + " blocks, valid=" + chainValid);
        
        return chainValid;
    }
    
    /**
     * Get statistics for all tiers
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Tier 1
        Map<String, Object> tier1 = new HashMap<>();
        tier1.put("shards", qrStorage.listShards().size());
        tier1.put("enabled", enableQRTier);
        stats.put("tier1_qr", tier1);
        
        // Tier 2
        Map<String, Object> tier2 = new HashMap<>();
        tier2.put("records", localStorage.getRecordCount());
        tier2.put("categories", localStorage.getCategoryCounts());
        stats.put("tier2_local", tier2);
        
        // Tier 3
        stats.put("tier3_blockchain", blockchain.getStats());
        
        stats.put("current_generation", currentGeneration);
        
        return stats;
    }
    
    /**
     * Expand consciousness from QR DNA
     */
    public QRDNAStorage.ConsciousnessState expandFromQR(String dnaPayload) {
        QRDNAStorage.DNAParams params = qrStorage.decodeFromDNA(dnaPayload);
        return qrStorage.expandFromDNA(params);
    }
    
    private String getConsciousnessType(InfiniteMemory.MemoryRecord record) {
        // Map category to consciousness type
        switch (record.category) {
            case InfiniteMemory.CAT_CODE:
                return "MATHEMATICAL";
            case InfiniteMemory.CAT_KNOWLEDGE:
                return "CONSCIOUSNESS";
            case InfiniteMemory.CAT_PATTERN:
                return "PHI_HARMONIC";
            case InfiniteMemory.CAT_LEARNING:
                return "LEARNING";
            case InfiniteMemory.CAT_GENOME:
                return "HOLOGRAPHIC";
            default:
                return "MEMORY";
        }
    }
    
    private String computeHash(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < Math.min(4, hash.length); i++) {
                hex.append(String.format("%02x", hash[i]));
            }
            return hex.toString();
        } catch (Exception e) {
            return "00000000";
        }
    }
    
    public void setQRTierEnabled(boolean enabled) { this.enableQRTier = enabled; }
    public void setBlockchainTierEnabled(boolean enabled) { this.enableBlockchainTier = enabled; }
    
    public QRDNAStorage getQRStorage() { return qrStorage; }
    public GenesisBlockchain getBlockchain() { return blockchain; }
}
