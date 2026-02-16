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

    private final List<MemoryRecord> records = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, List<Integer>> categoryIndex = new ConcurrentHashMap<>();
    private final Path storageFile;
    private int totalRecordsEver = 0;
    private long lastSaveTime = 0;
    private static final int SAVE_INTERVAL_MS = 30000;
    private boolean dirty = false;
    
    // MongoDB backend for cloud persistence
    private MemoryConfig config;
    private MongoMemoryBackend mongoBackend;
    private static final int MAX_IN_MEMORY_RECORDS = 5000; // Limit to prevent overflow

    public InfiniteMemory() {
        Path dataDir = Paths.get("data");
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            System.err.println("[InfiniteMemory] Cannot create data directory: " + e.getMessage());
        }
        this.storageFile = dataDir.resolve("infinite_memory.dat");
        
        // Load config and initialize MongoDB if configured
        this.config = new MemoryConfig();
        initMongoBackend();
        
        loadFromFile();
    }
    
    private void initMongoBackend() {
        if (config.getBackendType() == MemoryConfig.BackendType.MONGODB ||
            config.getBackendType() == MemoryConfig.BackendType.HYBRID) {
            if (config.isMongoConfigured()) {
                try {
                    mongoBackend = new MongoMemoryBackend(
                        config.getMongoConnectionString(),
                        config.getMongoDatabaseName()
                    );
                    System.out.println("[InfiniteMemory] MongoDB backend initialized");
                } catch (Exception e) {
                    System.err.println("[InfiniteMemory] MongoDB init failed: " + e.getMessage());
                    mongoBackend = null;
                }
            }
        }
    }
    
    public void connectMongo(String connectionString) {
        config.setMongoConnectionString(connectionString);
        config.setBackendType(MemoryConfig.BackendType.HYBRID);
        try {
            if (mongoBackend != null) mongoBackend.shutdown();
            mongoBackend = new MongoMemoryBackend(connectionString, config.getMongoDatabaseName());
            System.out.println("[InfiniteMemory] MongoDB connected!");
        } catch (Exception e) {
            System.err.println("[InfiniteMemory] MongoDB connect failed: " + e.getMessage());
        }
    }
    
    public boolean isMongoConnected() {
        return mongoBackend != null && mongoBackend.isConnected();
    }

    public MemoryRecord store(String category, String content, double phiResonance, String entityName) {
        MemoryRecord record = new MemoryRecord(category, content, phiResonance, entityName);
        
        // Save to MongoDB first (cloud backup)
        if (mongoBackend != null && mongoBackend.isConnected()) {
            mongoBackend.storeRecord(record);
        }
        
        // Limit in-memory records to prevent overflow
        if (records.size() >= MAX_IN_MEMORY_RECORDS) {
            // Remove oldest 20% when limit reached
            int toRemove = MAX_IN_MEMORY_RECORDS / 5;
            for (int i = 0; i < toRemove && !records.isEmpty(); i++) {
                records.remove(0);
            }
            rebuildCategoryIndex();
            System.gc(); // Force garbage collection
        }
        
        int idx = records.size();
        records.add(record);
        categoryIndex.computeIfAbsent(category, k -> Collections.synchronizedList(new ArrayList<>())).add(idx);
        totalRecordsEver++;
        dirty = true;
        autoSave();
        return record;
    }
    
    private void rebuildCategoryIndex() {
        categoryIndex.clear();
        for (int i = 0; i < records.size(); i++) {
            MemoryRecord r = records.get(i);
            categoryIndex.computeIfAbsent(r.category, k -> Collections.synchronizedList(new ArrayList<>())).add(i);
        }
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
        List<Integer> indices = categoryIndex.get(category);
        if (indices == null) return Collections.emptyList();
        List<MemoryRecord> result = new ArrayList<>();
        synchronized (indices) {
            for (int idx : indices) {
                if (idx < records.size()) {
                    result.add(records.get(idx));
                }
            }
        }
        return result;
    }

    public List<MemoryRecord> getByResonanceRange(double minRes, double maxRes) {
        return records.stream()
                .filter(r -> r.phiResonance >= minRes && r.phiResonance <= maxRes)
                .collect(Collectors.toList());
    }

    public List<MemoryRecord> getByEntity(String entityName) {
        return records.stream()
                .filter(r -> entityName.equals(r.entityName))
                .collect(Collectors.toList());
    }

    public List<MemoryRecord> search(String query) {
        String lower = query.toLowerCase();
        return records.stream()
                .filter(r -> r.content.toLowerCase().contains(lower) ||
                        r.category.toLowerCase().contains(lower) ||
                        (r.entityName != null && r.entityName.toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    public List<MemoryRecord> getRecent(int count) {
        int start = Math.max(0, records.size() - count);
        return new ArrayList<>(records.subList(start, records.size()));
    }

    public int getRecordCount() {
        return records.size();
    }

    public MemoryConfig getConfig() {
        return config;
    }

    public int getTotalRecordsEver() {
        return totalRecordsEver;
    }

    public Map<String, Integer> getCategoryCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Map.Entry<String, List<Integer>> e : categoryIndex.entrySet()) {
            counts.put(e.getKey(), e.getValue().size());
        }
        return counts;
    }

    public double getAverageResonance() {
        if (records.isEmpty()) return 0;
        double sum = 0;
        for (MemoryRecord r : records) {
            sum += r.phiResonance;
        }
        return sum / records.size();
    }

    public void forceSave() {
        saveToFile();
    }

    private void autoSave() {
        long now = System.currentTimeMillis();
        if (dirty && (now - lastSaveTime) > SAVE_INTERVAL_MS) {
            saveToFile();
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(storageFile)) {
            writer.write("FRAYMUS_INFINITE_MEMORY_V1");
            writer.newLine();
            writer.write(String.valueOf(totalRecordsEver));
            writer.newLine();
            synchronized (records) {
                for (MemoryRecord r : records) {
                    writer.write(r.serialize());
                    writer.newLine();
                }
            }
            lastSaveTime = System.currentTimeMillis();
            dirty = false;
        } catch (IOException e) {
            System.err.println("[InfiniteMemory] Save failed: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        if (!Files.exists(storageFile)) return;
        try (BufferedReader reader = Files.newBufferedReader(storageFile)) {
            String header = reader.readLine();
            if (header == null || !header.startsWith("FRAYMUS_INFINITE_MEMORY")) return;
            String countLine = reader.readLine();
            if (countLine != null) {
                try {
                    totalRecordsEver = Integer.parseInt(countLine.trim());
                } catch (NumberFormatException e) {
                    totalRecordsEver = 0;
                }
            }
            String line;
            while ((line = reader.readLine()) != null) {
                MemoryRecord record = MemoryRecord.deserialize(line);
                if (record != null) {
                    int idx = records.size();
                    records.add(record);
                    categoryIndex.computeIfAbsent(record.category,
                            k -> Collections.synchronizedList(new ArrayList<>())).add(idx);
                }
            }
            System.out.printf("[InfiniteMemory] Loaded %d records from disk%n", records.size());
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
