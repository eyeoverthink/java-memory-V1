package fraymus.knowledge;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AKASHIC RECORD: THE UNIVERSAL MEMORY
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "All knowledge, past and future, stored in one place."
 * 
 * The Akashic Record is the permanent knowledge storage for FRAYMUS.
 * It stores:
 * - Absorbed library knowledge
 * - Learned patterns and behaviors
 * - Historical decisions and outcomes
 * - Universal truths and constants
 * 
 * Structure:
 * - BLOCKS: Individual units of knowledge
 * - CHAINS: Linked sequences of related blocks
 * - INDEX: Fast lookup by category and hash
 * 
 * Persistence:
 * - Memory: ConcurrentHashMap for fast access
 * - Disk: JSON/Binary files for permanent storage
 * - Chain: Linked hash for integrity verification
 */
public class AkashicRecord {

    private static final double PHI = 1.618033988749895;
    private static final String STORAGE_DIR = ".akashic/";
    private static final String CHAIN_FILE = ".akashic/chain.dat";
    private static final String BLOCKS_FILE = ".akashic/blocks.dat";
    private static final String PENDING_BLOCKS_FILE = ".akashic/blocks-pending.dat";
    private static final String STATS_FILE = ".akashic/stats.dat";
    private static final String INDEX_FILE = ".akashic/index.dat";
    private static final long BLOCKS_FILE_SOFT_LIMIT_BYTES =
        Long.getLong("fraymus.akashic.blocksFileSoftLimitBytes", Long.MAX_VALUE);
    private static volatile boolean blocksFullyLoaded = true;
    private static volatile boolean pendingFullyLoaded = true;
    private static volatile boolean chainFullyLoaded = true;
    private static final Object INIT_LOCK = new Object();
    private static final KnowledgeRecordArchive RECORD_ARCHIVE = new KnowledgeRecordArchive(Paths.get(".akashic/record-archive"));
    private static volatile boolean initialized = false;
    private static volatile boolean shutdownHookRegistered = false;
    
    // In-memory storage
    private static Map<String, List<KnowledgeBlock>> categories;
    private static Map<String, KnowledgeBlock> blockIndex;
    private static List<String> chainHashes;
    
    // Statistics
    private static long blocksAdded = 0;
    private static long queriesProcessed = 0;
    private static long chainLength = 0;

    /**
     * GitHub rejects the single 2 GB block file. The chain is stored as
     * .akashic/blocks.parts/part-00 and joined here before the record loads.
     */
    private static void ensureBlocksMaterialized() {
        try {
            Path blocks = Paths.get(BLOCKS_FILE);
            Path partsDir = Paths.get(".akashic/blocks.parts");
            if (!Files.isDirectory(partsDir)) {
                return;
            }
            List<Path> parts = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(partsDir, "part-*")) {
                for (Path part : stream) {
                    if (Files.isRegularFile(part)) {
                        parts.add(part);
                    }
                }
            }
            if (parts.isEmpty()) {
                return;
            }
            parts.sort(Comparator.comparing(path -> path.getFileName().toString()));
            long expected = 0L;
            for (Path part : parts) {
                expected += Files.size(part);
            }
            long existing = Files.isRegularFile(blocks) ? Files.size(blocks) : 0L;
            if (existing >= expected) {
                return;
            }
            Path temp = Paths.get(BLOCKS_FILE + ".assembling");
            byte[] buffer = new byte[1024 * 1024];
            try (OutputStream out = Files.newOutputStream(temp)) {
                for (Path part : parts) {
                    try (InputStream in = Files.newInputStream(part)) {
                        int read;
                        while ((read = in.read(buffer)) >= 0) {
                            if (read > 0) {
                                out.write(buffer, 0, read);
                            }
                        }
                    }
                }
            }
            Files.move(temp, blocks, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("   Akashic blocks assembled from " + parts.size() + " parts");
        } catch (Exception e) {
            System.out.println("   Akashic blocks were not assembled: " + e.getMessage());
        }
    }

    public AkashicRecord() {
        synchronized (INIT_LOCK) {
            if (!initialized) {
                categories = new ConcurrentHashMap<>();
                blockIndex = new ConcurrentHashMap<>();
                chainHashes = Collections.synchronizedList(new ArrayList<>());

                try {
                    Files.createDirectories(Paths.get(STORAGE_DIR));
                } catch (Exception e) {
                    // Ignore
                }

                ensureBlocksMaterialized();
                loadAll();
                initialized = true;
            }

            if (!shutdownHookRegistered) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    saveAll();
                    System.out.println("   \uD83D\uDCDA AkashicRecord persisted (" + blockIndex.size() + " blocks)");
                }, "AkashicRecord-Shutdown"));
                shutdownHookRegistered = true;
            }
        }
    }

    /**
     * ADD BLOCK - Add a knowledge block to the record
     */
    public String addBlock(String category, String content) {
        synchronized (INIT_LOCK) {
        KnowledgeBlock block = new KnowledgeBlock(category, content);
        try { RECORD_ARCHIVE.save(block); }
        catch (IOException failure) { throw new UncheckedIOException("Knowledge record was not durably archived", failure); }
        if (blockIndex.containsKey(block.hash)) return block.hash;
        blocksAdded++;
        
        // Add to category
        categories.computeIfAbsent(category, k -> Collections.synchronizedList(new ArrayList<>()))
                  .add(block);
        
        // Add to index
        blockIndex.put(block.hash, block);
        
        // Add to chain
        chainHashes.add(block.hash);
        chainLength++;
        
        // Persist periodically
        if (blocksAdded % 50 == 0) {
            saveAll();
        }
        
        return block.hash;
        }
    }

    /**
     * QUERY - Search for knowledge
     */
    public List<KnowledgeBlock> query(String searchTerm) {
        queriesProcessed++;
        
        List<KnowledgeBlock> results = new ArrayList<>();
        
        for (List<KnowledgeBlock> categoryBlocks : categories.values()) {
            for (KnowledgeBlock block : categoryBlocks) {
                if (block.content.toLowerCase().contains(searchTerm.toLowerCase())) {
                    results.add(block);
                }
            }
        }
        
        return results;
    }

    /**
     * QUERY BY CATEGORY
     */
    public List<KnowledgeBlock> queryCategory(String category) {
        queriesProcessed++;
        return categories.getOrDefault(category, Collections.emptyList());
    }

    /**
     * GET BLOCK BY HASH
     */
    public KnowledgeBlock getBlock(String hash) {
        queriesProcessed++;
        return blockIndex.get(hash);
    }

    /**
     * VERIFY CHAIN INTEGRITY
     */
    public boolean verifyIntegrity() {
        if (chainHashes.isEmpty()) return true;
        
        String previousHash = null;
        for (String hash : chainHashes) {
            KnowledgeBlock block = blockIndex.get(hash);
            if (block == null) return false;
            
            // Verify hash matches content
            String computedHash = computeHash(block.category + block.content + block.timestamp);
            if (!computedHash.equals(hash)) return false;
            
            previousHash = hash;
        }
        
        return true;
    }

    /**
     * SAVE ALL - Persist everything to disk
     */
    public void saveAll() {
        synchronized (INIT_LOCK) {
            if (!saveBlocks()) return;
            saveChain();
            if (blocksFullyLoaded) saveIndex();
            saveStats();
        }
    }

    /**
     * SAVE CHAIN TO DISK
     */
    private void saveChain() {
        if (chainFullyLoaded) saveObject(CHAIN_FILE, new ArrayList<>(chainHashes));
    }

    /**
     * SAVE BLOCKS TO DISK
     */
    private boolean saveBlocks() {
        if (!pendingFullyLoaded) {
            System.err.println("[AKASHIC] Checkpoint retained: pending overlay could not be read; repair it before saving");
            return false;
        }
        String target = blocksFullyLoaded ? BLOCKS_FILE : PENDING_BLOCKS_FILE;
        boolean saved = saveObject(target, new ArrayList<>(blockIndex.values()));
        if (saved && blocksFullyLoaded) {
            try { Files.deleteIfExists(Paths.get(PENDING_BLOCKS_FILE)); }
            catch (IOException error) { System.err.println("[AKASHIC] Retained pending snapshot: " + error.getMessage()); }
        }
        return saved;
    }

    /** Never truncate the previous checkpoint before a complete replacement exists. */
    private boolean saveObject(String filename, Object value) {
        Path target = Paths.get(filename).toAbsolutePath();
        Path temporary = target.resolveSibling(target.getFileName() + "." + UUID.randomUUID() + ".tmp");
        try {
            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(temporary, StandardOpenOption.CREATE_NEW))) {
                output.writeObject(value);
            }
            try { Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException ignored) { Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING); }
            return true;
        } catch (Exception | OutOfMemoryError error) {
            System.err.println("[AKASHIC] Checkpoint replacement failed; previous file retained: " + error.getMessage());
            return false;
        } finally {
            try { Files.deleteIfExists(temporary); } catch (IOException ignored) {}
        }
    }

    /**
     * SAVE CATEGORY INDEX TO DISK
     */
    private void saveIndex() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(INDEX_FILE))) {
            // Save as category -> list of hashes
            HashMap<String, List<String>> indexMap = new HashMap<>();
            for (Map.Entry<String, List<KnowledgeBlock>> entry : categories.entrySet()) {
                List<String> hashes = new ArrayList<>();
                for (KnowledgeBlock block : entry.getValue()) {
                    hashes.add(block.hash);
                }
                indexMap.put(entry.getKey(), hashes);
            }
            oos.writeObject(indexMap);
        } catch (Exception e) {
            // Ignore
        }
    }

    /**
     * SAVE STATS TO DISK
     */
    private void saveStats() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(STATS_FILE))) {
            oos.writeObject(new long[]{blocksAdded, queriesProcessed, chainLength});
        } catch (Exception e) {
            // Ignore
        }
    }

    /**
     * LOAD ALL - Restore everything from disk
     */
    private void loadAll() {
        try {
            loadChain();
            loadBlocks();
            if (blocksFullyLoaded && chainFullyLoaded) try { restoreArchivedRecords(); }
            catch(UncheckedIOException failure) { blocksFullyLoaded=false; System.err.println("[AKASHIC] Record recovery incomplete; original archive retained: "+failure.getMessage()); }
            loadIndex();
            loadStats();
            chainLength=chainHashes.size();
        } catch (OutOfMemoryError oom) {
            System.out.println("   [AKASHIC] Load aborted due to heap pressure: " + oom.getMessage());
            blocksFullyLoaded = false;
            categories = new ConcurrentHashMap<>();
            blockIndex = new ConcurrentHashMap<>();
            // Keep an already loaded chain; never replace unread historical state.
        }
    }

    /**
     * LOAD CHAIN FROM DISK
     */
    @SuppressWarnings("unchecked")
    private void loadChain() {
        chainFullyLoaded = true;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(CHAIN_FILE))) {
            chainHashes = Collections.synchronizedList((List<String>) ois.readObject());
            chainLength = chainHashes.size();
        } catch (FileNotFoundException e) {
            chainHashes = Collections.synchronizedList(new ArrayList<>());
        } catch (Exception | OutOfMemoryError e) {
            chainFullyLoaded = false;
            System.err.println("[AKASHIC] Unread chain retained: " + e.getMessage());
        }
    }

    /**
     * LOAD BLOCKS FROM DISK
     */
    @SuppressWarnings("unchecked")
    private void loadBlocks() {
        File blocksFile = new File(BLOCKS_FILE);
        blocksFullyLoaded = true;
        if (blocksFile.exists()) {
            if (blocksFile.length() > BLOCKS_FILE_SOFT_LIMIT_BYTES) {
                blocksFullyLoaded = false;
                System.out.println("   [AKASHIC] Base snapshot deferred by configured limit; new records use blocks-pending.dat");
            } else blocksFullyLoaded = loadBlockFile(blocksFile);
        }
        File pending = new File(PENDING_BLOCKS_FILE);
        pendingFullyLoaded = !pending.exists() || loadBlockFile(pending);
        if (!pendingFullyLoaded) blocksFullyLoaded = false;
    }

    @SuppressWarnings("unchecked") private boolean loadBlockFile(File file) {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            for (KnowledgeBlock block : (List<KnowledgeBlock>) input.readObject()) blockIndex.put(block.hash, block);
            return true;
        } catch (Exception | OutOfMemoryError error) {
            System.err.println("[AKASHIC] Snapshot retained without replacement after load failure: " + file + " : " + error.getMessage());
            return false;
        }
    }

    /**
     * LOAD CATEGORY INDEX FROM DISK
     */
    @SuppressWarnings("unchecked")
    private void loadIndex() {
        // Categories are derivable from records, including any pending overlay.
        categories.clear();
        for (KnowledgeBlock block : blockIndex.values())
            categories.computeIfAbsent(block.category, ignored -> Collections.synchronizedList(new ArrayList<>())).add(block);
    }

    public boolean hasDeferredBlocks() { return !blocksFullyLoaded; }

    /** Rehydrate committed per-record files after a lost aggregate checkpoint. */
    public int restoreArchivedRecords() {
        synchronized (INIT_LOCK) {
            int restored = 0;
            try {
                for (Path path : RECORD_ARCHIVE.records()) {
                    String hash = String.valueOf(KnowledgeRecordArchive.read(path).get("hash"));
                    if (blockIndex.containsKey(hash)) continue;
                    KnowledgeBlock block = RECORD_ARCHIVE.load(path); blockIndex.put(hash, block); restored++;
                    if (!chainHashes.contains(hash)) chainHashes.add(hash);
                }
                loadIndex(); chainLength = chainHashes.size();
            } catch (IOException failure) { throw new UncheckedIOException("Record archive recovery failed; aggregate files retained", failure); }
            return restored;
        }
    }

    public Map<String,Object> integrityReport() {
        synchronized (INIT_LOCK) {
            var missing = new LinkedHashSet<String>(); var invalid = new LinkedHashSet<String>();
            for (String hash : chainHashes) if (!blockIndex.containsKey(hash)) missing.add(hash);
            for (var block : blockIndex.values()) if (!computeHash(block.category + block.content + block.timestamp).equals(block.hash)) invalid.add(block.hash);
            var historical = new LinkedHashSet<String>();
            Path history = Paths.get(".akashic/history");
            if (Files.isDirectory(history)) try (var files = Files.list(history)) {
                for (Path file : files.filter(p -> p.getFileName().toString().startsWith("reconciliation-") && p.toString().endsWith(".json")).toList()) {
                    Object values = KnowledgeRecordArchive.read(file).get("unresolved_hashes");
                    if (values instanceof List<?> hashes) for (Object hash : hashes) historical.add(String.valueOf(hash));
                }
            } catch (IOException error) { throw new UncheckedIOException(error); }
            historical.addAll(missing); historical.removeAll(blockIndex.keySet());
            return Map.of("active_chain_valid", missing.isEmpty() && invalid.isEmpty() && !hasDeferredBlocks() && chainFullyLoaded,
                "available_records", blockIndex.size(), "active_chain_entries", chainHashes.size(), "missing_active_hashes", new ArrayList<>(missing),
                "invalid_record_hashes", new ArrayList<>(invalid), "historical_unresolved_hashes", new ArrayList<>(historical),
                "historical_complete", historical.isEmpty(), "deferred_blocks", hasDeferredBlocks());
        }
    }

    /** Preserve the original chain and explicit gaps, then start a valid available-record chain. */
    @SuppressWarnings("unchecked") public Map<String,Object> reconcileChain() throws IOException {
        synchronized (INIT_LOCK) {
            if (hasDeferredBlocks() || !chainFullyLoaded) throw new IOException("Load the complete aggregate checkpoint before reconciling its history");
            int recovered = restoreArchivedRecords(); var before = integrityReport();
            if (!((List<?>) before.get("invalid_record_hashes")).isEmpty()) throw new IOException("Corrupt available records require verified recovery before chain reconciliation");
            for (var block : blockIndex.values()) RECORD_ARCHIVE.save(block);
            Path history = Files.createDirectories(Paths.get(".akashic/history"));
            byte[] original = Files.exists(Paths.get(CHAIN_FILE)) ? Files.readAllBytes(Paths.get(CHAIN_FILE)) : new byte[0];
            String digest = KnowledgeRecordArchive.sha(original);
            Path preserved = history.resolve("chain-" + digest + ".dat");
            if (!Files.exists(preserved)) Files.write(preserved, original, StandardOpenOption.CREATE_NEW);
            Map<String,Object> receipt = new LinkedHashMap<>(); receipt.put("format", "fraynix.chain-reconciliation.v1");
            receipt.put("original_chain_sha256", digest); receipt.put("original_chain", preserved.toAbsolutePath().toString());
            receipt.put("unresolved_hashes", before.get("missing_active_hashes")); receipt.put("before", before);
            receipt.put("records_recovered_from_archive", recovered); receipt.put("missing_content_reconstructed", false);
            KnowledgeRecordArchive.atomic(history.resolve("reconciliation-" + digest + ".json"), fraymus.core.FraymusJSON.stringify(receipt));
            var active = new ArrayList<String>();
            for (String hash : chainHashes) if (blockIndex.containsKey(hash)) active.add(hash);
            blockIndex.values().stream().sorted(Comparator.comparingLong(b -> b.timestamp)).map(b -> b.hash).filter(h -> !active.contains(h)).forEach(active::add);
            chainHashes = Collections.synchronizedList(active); chainLength = active.size();
            if(!saveBlocks() || !saveObject(CHAIN_FILE,new ArrayList<>(chainHashes)))throw new IOException("Reconciled chain could not be committed; preserved history and record files remain available");
            saveIndex();saveStats();receipt.put("disk_commit_confirmed",true);
            receipt.put("after", integrityReport()); return receipt;
        }
    }

    /**
     * LOAD STATS FROM DISK
     */
    private void loadStats() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(STATS_FILE))) {
            long[] stats = (long[]) ois.readObject();
            blocksAdded = stats[0];
            queriesProcessed = stats[1];
            chainLength = stats[2];
        } catch (Exception e) {
            // No stats file yet — start fresh
        }
    }

    /**
     * GET PERSISTED BLOCK COUNT (from current in-memory state)
     */
    public int getPersistedBlockCount() {
        return blockIndex.size();
    }

    /**
     * GET CATEGORY COUNT
     */
    public int getCategoryCount() {
        return categories.size();
    }

    /**
     * PURGE - Clear all knowledge and disk files
     */
    public void purge() {
        try { RECORD_ARCHIVE.purge(); KnowledgeRecordArchive.deleteTree(Paths.get(".akashic/history")); }
        catch(IOException failure) { throw new UncheckedIOException("Could not purge archived knowledge",failure); }
        categories.clear();
        blockIndex.clear();
        chainHashes.clear();
        blocksAdded = 0;
        queriesProcessed = 0;
        chainLength = 0;
        blocksFullyLoaded = true;
        pendingFullyLoaded = true;
        chainFullyLoaded = true;
        
        // Delete disk files
        try { Files.deleteIfExists(Paths.get(CHAIN_FILE)); } catch (Exception e) {}
        try { Files.deleteIfExists(Paths.get(BLOCKS_FILE)); } catch (Exception e) {}
        try { Files.deleteIfExists(Paths.get(PENDING_BLOCKS_FILE)); } catch (Exception e) {}
        try { Files.deleteIfExists(Paths.get(INDEX_FILE)); } catch (Exception e) {}
        try { Files.deleteIfExists(Paths.get(STATS_FILE)); } catch (Exception e) {}
        
        System.out.println("   \uD83D\uDDD1\uFE0F AkashicRecord purged. All knowledge erased.");
    }

    /**
     * COMPUTE PHI-ENHANCED HASH
     */
    private String computeHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes());
            
            // PHI enhancement
            long phiSig = (long) (bytes[0] * PHI * 1000000);
            
            StringBuilder sb = new StringBuilder();
            sb.append("φ-");
            for (int i = 0; i < 8; i++) {
                sb.append(String.format("%02x", bytes[i]));
            }
            
            return sb.toString();
        } catch (Exception e) {
            return "φ-" + Integer.toHexString(input.hashCode());
        }
    }

    /**
     * GET STATISTICS
     */
    public void printStats() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ AKASHIC RECORD STATISTICS                                   │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ Categories:          " + String.format("%-36d", categories.size()) + "│");
        System.out.println("│ Total Blocks:        " + String.format("%-36d", blockIndex.size()) + "│");
        System.out.println("│ Chain Length:        " + String.format("%-36d", chainLength) + "│");
        System.out.println("│ Blocks Added:        " + String.format("%-36d", blocksAdded) + "│");
        System.out.println("│ Queries Processed:   " + String.format("%-36d", queriesProcessed) + "│");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        
        for (String category : categories.keySet()) {
            int count = categories.get(category).size();
            System.out.println("│ " + String.format("%-20s", category) + ": " + 
                              String.format("%-35d", count) + "│");
        }
        
        System.out.println("└─────────────────────────────────────────────────────────────┘");
    }

    /**
     * KNOWLEDGE BLOCK - Individual unit of knowledge
     */
    public static class KnowledgeBlock implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public final String hash;
        public final String category;
        public final String content;
        public final long timestamp;
        public final String formattedTime;
        
        public KnowledgeBlock(String category, String content) {
            this(category, content, System.currentTimeMillis());
        }
        private KnowledgeBlock(String category, String content, long timestamp) {
            this(category,content,timestamp,LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(timestamp),java.time.ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        private KnowledgeBlock(String category,String content,long timestamp,String formattedTime) {
            this.category = category;
            this.content = content;
            this.timestamp = timestamp;
            this.formattedTime = formattedTime;
            
            // Compute hash
            this.hash = computeBlockHash(category, content, this.timestamp);
        }
        public static KnowledgeBlock restore(String category, String content, long timestamp, String expectedHash) {
            KnowledgeBlock restored = new KnowledgeBlock(category, content, timestamp);
            if (!restored.hash.equals(expectedHash)) throw new IllegalArgumentException("Knowledge hash differs from archived identity");
            return restored;
        }
        public static KnowledgeBlock restore(String category,String content,long timestamp,String expectedHash,String formattedTime) {
            KnowledgeBlock restored=new KnowledgeBlock(category,content,timestamp,formattedTime);
            if(!restored.hash.equals(expectedHash))throw new IllegalArgumentException("Knowledge hash differs from archived identity");return restored;
        }
        
        private static String computeBlockHash(String category, String content, long timestamp) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] bytes = md.digest((category + content + timestamp).getBytes());
                StringBuilder sb = new StringBuilder("φ-");
                for (int i = 0; i < 8; i++) {
                    sb.append(String.format("%02x", bytes[i]));
                }
                return sb.toString();
            } catch (Exception e) {
                return "φ-" + Integer.toHexString((category + content).hashCode());
            }
        }
        
        @Override
        public String toString() {
            return String.format("Block[%s | %s | %s]", hash, category, 
                content.length() > 50 ? content.substring(0, 50) + "..." : content);
        }
    }

    /**
     * Demonstration
     */
    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   AKASHIC RECORD: THE UNIVERSAL MEMORY                       ║");
        System.out.println("║   \"All knowledge, past and future, stored in one place.\"     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        AkashicRecord akashic = new AkashicRecord();
        
        // Add some knowledge
        System.out.println("   Adding knowledge blocks...");
        akashic.addBlock("MATH", "PI = 3.14159265359");
        akashic.addBlock("MATH", "PHI = 1.618033988749895");
        akashic.addBlock("MATH", "E = 2.71828182845904");
        akashic.addBlock("PHYSICS", "c = 299792458 m/s (speed of light)");
        akashic.addBlock("PHYSICS", "G = 6.67430e-11 (gravitational constant)");
        akashic.addBlock("FRAYMUS", "Patent: VS-PoQC-19046423-φ⁷⁵-2025");
        
        // Query
        System.out.println();
        System.out.println("   Querying for 'PHI'...");
        List<KnowledgeBlock> results = akashic.query("PHI");
        for (KnowledgeBlock block : results) {
            System.out.println("   >> " + block);
        }
        
        // Query by category
        System.out.println();
        System.out.println("   Querying category 'MATH'...");
        results = akashic.queryCategory("MATH");
        for (KnowledgeBlock block : results) {
            System.out.println("   >> " + block);
        }
        
        // Verify integrity
        System.out.println();
        System.out.println("   Verifying chain integrity...");
        boolean valid = akashic.verifyIntegrity();
        System.out.println("   >> Chain valid: " + valid);
        
        // Stats
        akashic.printStats();
        
        System.out.println();
        System.out.println("   ✓ Akashic Record demo complete.");
        System.out.println();
    }
}
