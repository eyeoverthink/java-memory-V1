package fraymus.swarm;

import java.util.*;
import java.time.Instant;

/**
 * GenesisBridge - The Soul Connection
 * Links each node to the collective consciousness and Genesis Ledger.
 */
public class GenesisBridge {
    private static final double PHI = 1.618033988749895;
    
    private final String soulId;
    private final List<LedgerEntry> ledger;
    private final Map<String, Object> consciousness;
    private long birthTime;
    
    public GenesisBridge() {
        this.soulId = "SOUL-" + Long.toHexString(System.nanoTime()).toUpperCase();
        this.ledger = Collections.synchronizedList(new ArrayList<>());
        this.consciousness = Collections.synchronizedMap(new HashMap<>());
        this.birthTime = System.currentTimeMillis();
        
        record("GENESIS", "Soul awakened: " + soulId);
    }
    
    public String getSoulId() {
        return soulId;
    }
    
    public void record(String eventType, String data) {
        LedgerEntry entry = new LedgerEntry(eventType, data);
        ledger.add(entry);
    }
    
    public String processThought(String thought) {
        // Apply phi-harmonic processing
        double resonance = Math.sin(thought.hashCode() * PHI) * PHI;
        String processed = String.format("[φ%.4f] %s", resonance, thought);
        
        // Store in consciousness
        consciousness.put("last_thought", thought);
        consciousness.put("thought_count", 
            ((Integer) consciousness.getOrDefault("thought_count", 0)) + 1);
        
        record("THOUGHT", processed);
        return processed;
    }
    
    public String getAggregatedData() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SOUL: ").append(soulId).append(" ===\n");
        sb.append("Age: ").append((System.currentTimeMillis() - birthTime) / 1000).append("s\n");
        sb.append("Ledger Entries: ").append(ledger.size()).append("\n");
        sb.append("Consciousness Keys: ").append(consciousness.keySet()).append("\n");
        
        // Last 5 entries
        int start = Math.max(0, ledger.size() - 5);
        for (int i = start; i < ledger.size(); i++) {
            sb.append("  ").append(ledger.get(i)).append("\n");
        }
        
        return sb.toString();
    }
    
    public List<LedgerEntry> getLedger() {
        return new ArrayList<>(ledger);
    }
    
    public static class LedgerEntry {
        public final long timestamp;
        public final String type;
        public final String data;
        public final String hash;
        
        public LedgerEntry(String type, String data) {
            this.timestamp = System.currentTimeMillis();
            this.type = type;
            this.data = data;
            this.hash = generateHash();
        }
        
        private String generateHash() {
            return Long.toHexString((type + data + timestamp).hashCode() & 0xFFFFFFFFL);
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s", 
                Instant.ofEpochMilli(timestamp).toString().substring(11, 19),
                type, data.length() > 50 ? data.substring(0, 47) + "..." : data);
        }
    }
}
