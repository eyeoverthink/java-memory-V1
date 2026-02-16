package fraymus;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.*;

/**
 * 3-Tier Layered Persistence Orchestrator
 * 
 * Tier 1: QR DNA Storage (Instant/Fastest) - Phi-harmonic encoding
 * Tier 2: Local DB/JSON Storage (Fast) - Streaming logs + MongoDB
 * Tier 3: Genesis Blockchain (Permanent) - Immutable ledger
 * 
 * Sequenced Push: QR → Local → Blockchain
 * Priority Read: QR (fastest) → Local → Blockchain
 */
public class LayeredPersistenceManager {
    
    private static LayeredPersistenceManager instance;
    
    private final QRDNAStorage qrStorage;
    private final InfiniteMemory localStorage;
    private final GenesisMemory blockchain;
    private final MongoPersistence mongoPersistence;
    
    private final ExecutorService asyncExecutor;
    private final BlockingQueue<InfiniteMemory.MemoryRecord> pushQueue;
    
    private boolean qrEnabled = true;
    private boolean localEnabled = true;
    private boolean blockchainEnabled = true;
    private boolean mongoEnabled = true;
    
    // Stats
    private long qrWrites = 0;
    private long localWrites = 0;
    private long blockchainWrites = 0;
    private long mongoWrites = 0;
    
    public LayeredPersistenceManager(InfiniteMemory memory, GenesisMemory genesis) {
        this.qrStorage = new QRDNAStorage();
        this.localStorage = memory;
        this.blockchain = genesis;
        this.mongoPersistence = MongoPersistence.getInstance();
        
        this.asyncExecutor = Executors.newFixedThreadPool(2);
        this.pushQueue = new LinkedBlockingQueue<>(1000);
        
        // Start async push processor
        startAsyncPushProcessor();
        
        System.out.println("[LayeredPersistence] 3-Tier system initialized");
        System.out.println("  Tier 1: QR DNA Storage - " + (qrEnabled ? "ENABLED" : "DISABLED"));
        System.out.println("  Tier 2: Local DB - " + (localEnabled ? "ENABLED" : "DISABLED"));
        System.out.println("  Tier 3: Genesis Blockchain - " + (blockchainEnabled ? "ENABLED" : "DISABLED"));
    }
    
    public static LayeredPersistenceManager getInstance(InfiniteMemory memory, GenesisMemory genesis) {
        if (instance == null) {
            instance = new LayeredPersistenceManager(memory, genesis);
        }
        return instance;
    }
    
    /**
     * Sequenced push: QR → Local → Blockchain
     * QR is synchronous (instant), others are async
     */
    public void storeWithSequencedPush(InfiniteMemory.MemoryRecord record) {
        // Tier 1: Instant QR DNA encoding (synchronous - fastest)
        if (qrEnabled) {
            try {
                String dna = qrStorage.encodeToDNA(record);
                QRDNAStorage.ConsciousnessLevel level = qrStorage.getConsciousnessLevel(record.phiResonance);
                BufferedImage qr = qrStorage.generateQRCode(dna, level);
                
                qrStorage.saveQRShard(record.id, qr);
                qrStorage.saveDNAPayload(record.id, dna);
                qrWrites++;
            } catch (Exception e) {
                System.err.println("[LayeredPersistence] QR encode failed: " + e.getMessage());
            }
        }
        
        // Queue for async push to other tiers
        pushQueue.offer(record);
    }
    
    /**
     * Store to specific tier only
     */
    public void storeToTier(int tier, InfiniteMemory.MemoryRecord record) {
        switch (tier) {
            case 1:
                storeToQR(record);
                break;
            case 2:
                storeToLocal(record);
                break;
            case 3:
                storeToBlockchain(record);
                break;
        }
    }
    
    private void storeToQR(InfiniteMemory.MemoryRecord record) {
        if (!qrEnabled) return;
        try {
            String dna = qrStorage.encodeToDNA(record);
            QRDNAStorage.ConsciousnessLevel level = qrStorage.getConsciousnessLevel(record.phiResonance);
            BufferedImage qr = qrStorage.generateQRCode(dna, level);
            qrStorage.saveQRShard(record.id, qr);
            qrStorage.saveDNAPayload(record.id, dna);
            qrWrites++;
        } catch (Exception e) {
            System.err.println("[LayeredPersistence] QR store failed: " + e.getMessage());
        }
    }
    
    private void storeToLocal(InfiniteMemory.MemoryRecord record) {
        if (!localEnabled) return;
        // Already stored via InfiniteMemory
        localWrites++;
        
        // Also push to MongoDB if connected
        if (mongoEnabled && mongoPersistence.isConnected()) {
            try {
                mongoPersistence.saveMemoryRecord(record);
                mongoWrites++;
            } catch (Exception e) {
                System.err.println("[LayeredPersistence] MongoDB store failed: " + e.getMessage());
            }
        }
    }
    
    private void storeToBlockchain(InfiniteMemory.MemoryRecord record) {
        if (!blockchainEnabled || blockchain == null) return;
        try {
            // Store DNA payload in blockchain for permanent record
            String dna = qrStorage.encodeToDNA(record);
            blockchain.record("MEMORY_STORE", dna);
            blockchainWrites++;
        } catch (Exception e) {
            System.err.println("[LayeredPersistence] Blockchain store failed: " + e.getMessage());
        }
    }
    
    /**
     * Retrieve with priority: QR (fastest) → Local → Blockchain
     */
    public InfiniteMemory.MemoryRecord retrieve(String id) {
        // Try QR first (instant)
        if (qrEnabled) {
            String dna = qrStorage.loadDNAPayload(id);
            if (dna != null) {
                QRDNAStorage.DNAParams params = qrStorage.decodeFromDNA(dna);
                // Reconstruct basic record from DNA
                return new InfiniteMemory.MemoryRecord(
                    id, System.currentTimeMillis(), "RESTORED",
                    "Consciousness restored from DNA: " + params.toString(),
                    params.resonance, null, params.hash, new HashMap<>()
                );
            }
        }
        
        // Try local DB
        if (localEnabled && localStorage != null) {
            java.util.List<InfiniteMemory.MemoryRecord> results = localStorage.search(id);
            if (!results.isEmpty()) {
                return results.get(0);
            }
        }
        
        // Blockchain lookup would go here (future implementation)
        
        return null;
    }
    
    /**
     * Expand consciousness from DNA payload
     */
    public double[] expandConsciousness(String dnaPayload) {
        QRDNAStorage.DNAParams params = qrStorage.decodeFromDNA(dnaPayload);
        return params.echoMatrix;
    }
    
    /**
     * Get status of all tiers
     */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════╗\n");
        sb.append("║           3-TIER LAYERED PERSISTENCE STATUS              ║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        
        // Tier 1
        sb.append(String.format("║ TIER 1: QR DNA Storage                                   ║\n"));
        sb.append(String.format("║   Status: %-10s | Shards: %-6d | Writes: %-8d   ║\n",
            qrEnabled ? "ENABLED" : "DISABLED", qrStorage.getShardCount(), qrWrites));
        
        // Tier 2
        sb.append(String.format("║ TIER 2: Local DB Storage                                 ║\n"));
        sb.append(String.format("║   Status: %-10s | Records: %-5d | Writes: %-8d   ║\n",
            localEnabled ? "ENABLED" : "DISABLED", 
            localStorage != null ? localStorage.getRecordCount() : 0, localWrites));
        sb.append(String.format("║   MongoDB: %-9s | Writes: %-8d                    ║\n",
            mongoPersistence.isConnected() ? "CONNECTED" : "OFFLINE", mongoWrites));
        
        // Tier 3
        sb.append(String.format("║ TIER 3: Genesis Blockchain                               ║\n"));
        sb.append(String.format("║   Status: %-10s | Blocks: %-6d | Writes: %-8d   ║\n",
            blockchainEnabled ? "ENABLED" : "DISABLED",
            blockchain != null ? blockchain.getChainLength() : 0, blockchainWrites));
        
        sb.append("║                                                          ║\n");
        sb.append(String.format("║ Queue: %-5d pending                                     ║\n", pushQueue.size()));
        sb.append("╚══════════════════════════════════════════════════════════╝");
        
        return sb.toString();
    }
    
    /**
     * Enable/disable tiers
     */
    public void setTierEnabled(int tier, boolean enabled) {
        switch (tier) {
            case 1: qrEnabled = enabled; break;
            case 2: localEnabled = enabled; break;
            case 3: blockchainEnabled = enabled; break;
        }
    }
    
    public boolean isTierEnabled(int tier) {
        switch (tier) {
            case 1: return qrEnabled;
            case 2: return localEnabled;
            case 3: return blockchainEnabled;
            default: return false;
        }
    }
    
    /**
     * Force push all queued items
     */
    public void flushQueue() {
        int count = 0;
        InfiniteMemory.MemoryRecord record;
        while ((record = pushQueue.poll()) != null) {
            storeToLocal(record);
            storeToBlockchain(record);
            count++;
        }
        System.out.println("[LayeredPersistence] Flushed " + count + " records");
    }
    
    /**
     * Async push processor thread
     */
    private void startAsyncPushProcessor() {
        asyncExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    InfiniteMemory.MemoryRecord record = pushQueue.poll(1, TimeUnit.SECONDS);
                    if (record != null) {
                        // Tier 2: Push to local DB (async)
                        storeToLocal(record);
                        
                        // Tier 3: Push to blockchain (batched - every 10 records)
                        if (blockchainWrites % 10 == 0) {
                            storeToBlockchain(record);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("[LayeredPersistence] Async push error: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Shutdown gracefully
     */
    public void shutdown() {
        flushQueue();
        asyncExecutor.shutdownNow();
        System.out.println("[LayeredPersistence] Shutdown complete");
    }
    
    // === Accessors ===
    
    public QRDNAStorage getQRStorage() { return qrStorage; }
    public InfiniteMemory getLocalStorage() { return localStorage; }
    public GenesisMemory getBlockchain() { return blockchain; }
    public MongoPersistence getMongoPersistence() { return mongoPersistence; }
}
