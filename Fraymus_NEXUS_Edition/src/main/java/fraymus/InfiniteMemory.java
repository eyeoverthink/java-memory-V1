package fraymus;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InfiniteMemory {

    public static class MemoryRecord {
        public final String id;
        public final long timestamp;
        public final String category;
        public final String content;
        public final double phiResonance;
        public final String entityName;
        public final String hash;
        public final Map<String, String> metadata;

        public MemoryRecord(String category, String content, double phiResonance, String entityName) {
            this.id = UUID.randomUUID().toString().substring(0, 8);
            this.timestamp = System.currentTimeMillis();
            this.category = category;
            this.content = content;
            this.phiResonance = phiResonance;
            this.entityName = entityName;
            this.hash = computeHash(category + content + timestamp);
            this.metadata = new HashMap<>();
        }

        public MemoryRecord(String id, long timestamp, String category, String content,
                            double phiResonance, String entityName, String hash, Map<String, String> metadata) {
            this.id = id;
            this.timestamp = timestamp;
            this.category = category;
            this.content = content;
            this.phiResonance = phiResonance;
            this.entityName = entityName;
            this.hash = hash;
            this.metadata = metadata != null ? metadata : new HashMap<>();
        }

        private static String computeHash(String input) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] digest = md.digest(input.getBytes());
                StringBuilder hex = new StringBuilder();
                for (int i = 0; i < 8; i++) {
                    hex.append(String.format("%02x", digest[i]));
                }
                return hex.toString();
            } catch (Exception e) {
                return "00000000";
            }
        }

        public String serialize() {
            StringBuilder sb = new StringBuilder();
            sb.append(id).append("|");
            sb.append(timestamp).append("|");
            sb.append(category).append("|");
            sb.append(base64Encode(content)).append("|");
            sb.append(String.format("%.10f", phiResonance)).append("|");
            sb.append(entityName != null ? entityName : "").append("|");
            sb.append(hash);
            if (!metadata.isEmpty()) {
                sb.append("|");
                List<String> entries = new ArrayList<>();
                for (Map.Entry<String, String> e : metadata.entrySet()) {
                    entries.add(e.getKey() + "=" + base64Encode(e.getValue()));
                }
                sb.append(String.join(",", entries));
            }
            return sb.toString();
        }

        public static MemoryRecord deserialize(String line) {
            try {
                String[] parts = line.split("\\|", -1);
                if (parts.length < 7) return null;
                String id = parts[0];
                long ts = Long.parseLong(parts[1]);
                String cat = parts[2];
                String content = base64Decode(parts[3]);
                double res = Double.parseDouble(parts[4]);
                String entity = parts[5].isEmpty() ? null : parts[5];
                String hash = parts[6];
                Map<String, String> meta = new HashMap<>();
                if (parts.length > 7 && !parts[7].isEmpty()) {
                    for (String entry : parts[7].split(",")) {
                        String[] kv = entry.split("=", 2);
                        if (kv.length == 2) {
                            meta.put(kv[0], base64Decode(kv[1]));
                        }
                    }
                }
                return new MemoryRecord(id, ts, cat, content, res, entity, hash, meta);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String toString() {
            String snippet = content.length() > 60 ? content.substring(0, 60) + "..." : content;
            return String.format("[%s] %s: %s (phi=%.4f)", id, category, snippet, phiResonance);
        }
    }

    public static final String CAT_EVENT = "EVENT";
    public static final String CAT_PATTERN = "PATTERN";
    public static final String CAT_KNOWLEDGE = "KNOWLEDGE";
    public static final String CAT_CODE = "CODE";
    public static final String CAT_QUESTION = "QUESTION";
    public static final String CAT_ANSWER = "ANSWER";
    public static final String CAT_GENOME = "GENOME";
    public static final String CAT_LEARNING = "LEARNING";

    // STREAMING ARCHITECTURE - No full dataset in RAM
    private final Map<String, List<String>> categoryIndex = new ConcurrentHashMap<>(); // category -> record IDs
    private final Map<String, Long> recordOffsets = new ConcurrentHashMap<>(); // record ID -> file offset
    private final Path logFile;
    private final Path indexFile;
    private RandomAccessFile logWriter;
    private int totalRecordsEver = 0;
    private final Object writeLock = new Object();
    
    // Parallel processing support
    private final java.util.concurrent.ExecutorService parallelExecutor = 
        java.util.concurrent.Executors.newFixedThreadPool(4); // Quad-core processing

    public InfiniteMemory() {
        Path dataDir = Paths.get("data");
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            System.err.println("[InfiniteMemory] Cannot create data directory: " + e.getMessage());
        }
        
        // Streaming log file (append-only)
        String dateStamp = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        this.logFile = dataDir.resolve("memory_log_" + dateStamp + ".dat");
        this.indexFile = dataDir.resolve("memory_index.dat");
        
        try {
            this.logWriter = new RandomAccessFile(logFile.toFile(), "rw");
            this.logWriter.seek(logWriter.length()); // Append mode
        } catch (IOException e) {
            System.err.println("[InfiniteMemory] Cannot open log file: " + e.getMessage());
        }
        
        loadIndex(); // Load lightweight index only, not full dataset
    }

    public MemoryRecord store(String category, String content, double phiResonance, String entityName) {
        MemoryRecord record = new MemoryRecord(category, content, phiResonance, entityName);
        
        // STREAMING WRITE - append immediately, never rewrite
        synchronized (writeLock) {
            try {
                long offset = logWriter.getFilePointer();
                String serialized = record.serialize();
                logWriter.writeBytes(serialized);
                logWriter.writeByte('\n');
                
                // Update in-memory index only
                recordOffsets.put(record.id, offset);
                categoryIndex.computeIfAbsent(category, k -> Collections.synchronizedList(new ArrayList<>()))
                    .add(record.id);
                totalRecordsEver++;
                
                // Async index save (non-blocking)
                parallelExecutor.submit(this::saveIndex);
                
            } catch (IOException e) {
                System.err.println("[InfiniteMemory] Write error: " + e.getMessage());
            }
        }
        return record;
    }

    public MemoryRecord store(String category, String content, double phiResonance) {
        return store(category, content, phiResonance, null);
    }

    public MemoryRecord storeWithMeta(String category, String content, double phiResonance,
                                       String entityName, Map<String, String> metadata) {
        MemoryRecord record = store(category, content, phiResonance, entityName);
        if (metadata != null) {
            record.metadata.putAll(metadata);
        }
        return record;
    }

    public List<MemoryRecord> getByCategory(String category) {
        List<String> recordIds = categoryIndex.get(category);
        if (recordIds == null) return Collections.emptyList();
        
        // PARALLEL RETRIEVAL - use quad-core processing
        List<MemoryRecord> result = Collections.synchronizedList(new ArrayList<>());
        List<java.util.concurrent.Future<?>> futures = new ArrayList<>();
        
        synchronized (recordIds) {
            for (String id : recordIds) {
                futures.add(parallelExecutor.submit(() -> {
                    MemoryRecord rec = loadRecordById(id);
                    if (rec != null) result.add(rec);
                }));
            }
        }
        
        // Wait for parallel reads to complete
        for (java.util.concurrent.Future<?> f : futures) {
            try { f.get(); } catch (Exception e) {}
        }
        
        return result;
    }

    public List<MemoryRecord> getByResonanceRange(double minRes, double maxRes) {
        // Parallel scan of all records
        List<MemoryRecord> result = Collections.synchronizedList(new ArrayList<>());
        List<java.util.concurrent.Future<?>> futures = new ArrayList<>();
        
        for (String id : recordOffsets.keySet()) {
            futures.add(parallelExecutor.submit(() -> {
                MemoryRecord r = loadRecordById(id);
                if (r != null && r.phiResonance >= minRes && r.phiResonance <= maxRes) {
                    result.add(r);
                }
            }));
        }
        
        for (java.util.concurrent.Future<?> f : futures) {
            try { f.get(); } catch (Exception e) {}
        }
        return result;
    }

    public List<MemoryRecord> getByEntity(String entityName) {
        // Parallel scan
        List<MemoryRecord> result = Collections.synchronizedList(new ArrayList<>());
        List<java.util.concurrent.Future<?>> futures = new ArrayList<>();
        
        for (String id : recordOffsets.keySet()) {
            futures.add(parallelExecutor.submit(() -> {
                MemoryRecord r = loadRecordById(id);
                if (r != null && entityName.equals(r.entityName)) {
                    result.add(r);
                }
            }));
        }
        
        for (java.util.concurrent.Future<?> f : futures) {
            try { f.get(); } catch (Exception e) {}
        }
        return result;
    }

    public List<MemoryRecord> search(String query) {
        String lower = query.toLowerCase();
        // Parallel search across all records
        List<MemoryRecord> result = Collections.synchronizedList(new ArrayList<>());
        List<java.util.concurrent.Future<?>> futures = new ArrayList<>();
        
        for (String id : recordOffsets.keySet()) {
            futures.add(parallelExecutor.submit(() -> {
                MemoryRecord r = loadRecordById(id);
                if (r != null && (r.content.toLowerCase().contains(lower) ||
                        r.category.toLowerCase().contains(lower) ||
                        (r.entityName != null && r.entityName.toLowerCase().contains(lower)))) {
                    result.add(r);
                }
            }));
        }
        
        for (java.util.concurrent.Future<?> f : futures) {
            try { f.get(); } catch (Exception e) {}
        }
        return result;
    }

    public List<MemoryRecord> getRecent(int count) {
        // Get most recent record IDs by timestamp
        List<String> allIds = new ArrayList<>(recordOffsets.keySet());
        List<MemoryRecord> result = Collections.synchronizedList(new ArrayList<>());
        List<java.util.concurrent.Future<?>> futures = new ArrayList<>();
        
        int start = Math.max(0, allIds.size() - count);
        for (int i = start; i < allIds.size(); i++) {
            String id = allIds.get(i);
            futures.add(parallelExecutor.submit(() -> {
                MemoryRecord r = loadRecordById(id);
                if (r != null) result.add(r);
            }));
        }
        
        for (java.util.concurrent.Future<?> f : futures) {
            try { f.get(); } catch (Exception e) {}
        }
        
        // Sort by timestamp
        result.sort((a, b) -> Long.compare(a.timestamp, b.timestamp));
        return result;
    }

    public int getRecordCount() {
        return recordOffsets.size();
    }
    
    // LAZY LOAD - Fetch record from disk only when needed
    private MemoryRecord loadRecordById(String id) {
        Long offset = recordOffsets.get(id);
        if (offset == null) return null;
        
        synchronized (writeLock) {
            try {
                logWriter.seek(offset);
                String line = logWriter.readLine();
                return MemoryRecord.deserialize(line);
            } catch (IOException e) {
                System.err.println("[InfiniteMemory] Read error: " + e.getMessage());
                return null;
            }
        }
    }
    
    // Shutdown hook for clean executor shutdown
    public void shutdown() {
        parallelExecutor.shutdown();
        try {
            if (logWriter != null) logWriter.close();
        } catch (IOException e) {}
    }

    public int getTotalRecordsEver() {
        return totalRecordsEver;
    }

    public Map<String, Integer> getCategoryCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> e : categoryIndex.entrySet()) {
            counts.put(e.getKey(), e.getValue().size());
        }
        return counts;
    }

    public double getAverageResonance() {
        if (recordOffsets.isEmpty()) return 0;
        
        // Parallel computation of average
        final double[] sum = {0};
        List<java.util.concurrent.Future<?>> futures = new ArrayList<>();
        
        for (String id : recordOffsets.keySet()) {
            futures.add(parallelExecutor.submit(() -> {
                MemoryRecord r = loadRecordById(id);
                if (r != null) {
                    synchronized (sum) {
                        sum[0] += r.phiResonance;
                    }
                }
            }));
        }
        
        for (java.util.concurrent.Future<?> f : futures) {
            try { f.get(); } catch (Exception e) {}
        }
        
        return sum[0] / recordOffsets.size();
    }

    public void forceSave() {
        saveIndex();
    }

    // STREAMING - No full file rewrites, only save lightweight index
    private void saveIndex() {
        try (BufferedWriter writer = Files.newBufferedWriter(indexFile)) {
            writer.write("FRAYMUS_MEMORY_INDEX_V2");
            writer.newLine();
            writer.write(String.valueOf(totalRecordsEver));
            writer.newLine();
            
            // Save category index (lightweight)
            for (Map.Entry<String, List<String>> e : categoryIndex.entrySet()) {
                writer.write(e.getKey() + "|" + String.join(",", e.getValue()));
                writer.newLine();
            }
            
            // Save record offsets
            writer.write("OFFSETS");
            writer.newLine();
            for (Map.Entry<String, Long> e : recordOffsets.entrySet()) {
                writer.write(e.getKey() + "|" + e.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[InfiniteMemory] Save failed: " + e.getMessage());
        }
    }

    // Load lightweight index only, not full dataset
    private void loadIndex() {
        if (!Files.exists(indexFile)) {
            System.out.println("[InfiniteMemory] No existing index - starting fresh");
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(indexFile)) {
            String header = reader.readLine();
            if (header == null || !header.startsWith("FRAYMUS_MEMORY_INDEX")) return;
            
            String countLine = reader.readLine();
            if (countLine != null) {
                try {
                    totalRecordsEver = Integer.parseInt(countLine.trim());
                } catch (NumberFormatException e) {
                    totalRecordsEver = 0;
                }
            }
            
            // Load category index
            String line;
            boolean inOffsets = false;
            while ((line = reader.readLine()) != null) {
                if (line.equals("OFFSETS")) {
                    inOffsets = true;
                    continue;
                }
                
                String[] parts = line.split("\\|", 2);
                if (parts.length != 2) continue;
                
                if (!inOffsets) {
                    // Category mapping: category|id1,id2,id3
                    String category = parts[0];
                    String[] ids = parts[1].split(",");
                    List<String> idList = Collections.synchronizedList(new ArrayList<>());
                    for (String id : ids) {
                        if (!id.isEmpty()) idList.add(id);
                    }
                    categoryIndex.put(category, idList);
                } else {
                    // Record offset: id|offset
                    String id = parts[0];
                    try {
                        long offset = Long.parseLong(parts[1]);
                        recordOffsets.put(id, offset);
                    } catch (NumberFormatException e) {
                        // Skip invalid offset
                    }
                }
            }
            
            System.out.printf("[InfiniteMemory] Loaded index: %d records, %d categories%n", 
                recordOffsets.size(), categoryIndex.size());
        } catch (IOException e) {
            System.err.println("[InfiniteMemory] Load failed: " + e.getMessage());
        }
    }

    private static String base64Encode(String input) {
        if (input == null) return "";
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    private static String base64Decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) return "";
        try {
            return new String(Base64.getDecoder().decode(encoded));
        } catch (Exception e) {
            return encoded;
        }
    }
}
