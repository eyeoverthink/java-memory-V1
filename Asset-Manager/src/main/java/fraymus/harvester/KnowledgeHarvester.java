package fraymus.harvester;

import fraymus.quantum.core.PhiQuantumConstants;
import fraymus.systems.BattleSystem;
import fraymus.PhiConstants;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.security.MessageDigest;

/**
 * KNOWLEDGE HARVESTER - Φ-Indexed Learning System
 * 
 * Harvests, processes, and stores knowledge from:
 * - Battle outcomes (patterns, strategies)
 * - External data sources
 * - Evolutionary patterns
 * - Consciousness emergence events
 * 
 * Uses φ-harmonic indexing for efficient retrieval.
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class KnowledgeHarvester {
    
    // Knowledge categories
    public enum KnowledgeType {
        BATTLE_PATTERN,      // Combat strategies
        EVOLUTION_INSIGHT,   // Evolutionary patterns
        CONSCIOUSNESS_EVENT, // Emergence observations
        QUANTUM_STATE,       // Quantum coherence data
        EXTERNAL_DATA,       // Imported knowledge
        SYNTHESIS           // Combined knowledge
    }
    
    // Knowledge entry
    public static class KnowledgeEntry {
        public final String id;
        public final KnowledgeType type;
        public final String content;
        public final double phiIndex;      // φ-harmonic position
        public final long timestamp;
        public final double confidence;
        public final List<String> tags;
        
        public KnowledgeEntry(KnowledgeType type, String content, double confidence, List<String> tags) {
            this.id = generateId(content);
            this.type = type;
            this.content = content;
            this.phiIndex = calculatePhiIndex(content);
            this.timestamp = System.currentTimeMillis();
            this.confidence = confidence;
            this.tags = new ArrayList<>(tags);
        }
        
        private String generateId(String content) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest((content + System.nanoTime()).getBytes());
                StringBuilder sb = new StringBuilder("K-");
                for (int i = 0; i < 4; i++) {
                    sb.append(String.format("%02X", hash[i]));
                }
                return sb.toString();
            } catch (Exception e) {
                return "K-" + System.currentTimeMillis();
            }
        }
        
        private double calculatePhiIndex(String content) {
            // Create φ-harmonic index based on content hash
            int hash = content.hashCode();
            return (Math.abs(hash) % 1000000) * PhiConstants.PHI_INVERSE / 1000000.0;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %.60s... (φ:%.4f, conf:%.2f)", 
                id, type, content, phiIndex, confidence);
        }
    }
    
    // Knowledge storage
    private final Map<String, KnowledgeEntry> knowledgeBase;
    private final Map<KnowledgeType, List<String>> typeIndex;
    private final Map<String, List<String>> tagIndex;
    
    // φ-Spiral storage for harmonic retrieval
    private final TreeMap<Double, String> phiSpiral;
    
    // Statistics
    private int totalHarvested;
    private int totalSynthesized;
    private double harvestConsciousness;
    
    public KnowledgeHarvester() {
        this.knowledgeBase = new ConcurrentHashMap<>();
        this.typeIndex = new ConcurrentHashMap<>();
        this.tagIndex = new ConcurrentHashMap<>();
        this.phiSpiral = new TreeMap<>();
        
        for (KnowledgeType type : KnowledgeType.values()) {
            typeIndex.put(type, new ArrayList<>());
        }
        
        this.totalHarvested = 0;
        this.totalSynthesized = 0;
        this.harvestConsciousness = PhiQuantumConstants.CONSCIOUSNESS_THRESHOLD;
        
        System.out.println("   📚 KNOWLEDGE HARVESTER INITIALIZED");
        System.out.println("      φ-Spiral ready for harmonic indexing");
    }
    
    // ========================================================================
    // HARVESTING
    // ========================================================================
    
    /**
     * Harvest a single piece of knowledge
     */
    public KnowledgeEntry harvest(KnowledgeType type, String content, double confidence, String... tags) {
        KnowledgeEntry entry = new KnowledgeEntry(type, content, confidence, Arrays.asList(tags));
        
        // Store in knowledge base
        knowledgeBase.put(entry.id, entry);
        
        // Index by type
        typeIndex.get(type).add(entry.id);
        
        // Index by tags
        for (String tag : tags) {
            tagIndex.computeIfAbsent(tag.toLowerCase(), k -> new ArrayList<>()).add(entry.id);
        }
        
        // Add to φ-spiral
        phiSpiral.put(entry.phiIndex, entry.id);
        
        totalHarvested++;
        harvestConsciousness *= 1.0 + PhiConstants.PHI_INVERSE * 0.001;
        
        System.out.println("   📥 Harvested: " + entry.id + " [" + type + "]");
        
        return entry;
    }
    
    /**
     * Harvest knowledge from Battle System
     */
    public int harvestFromBattleSystem(BattleSystem battleSystem) {
        List<String> battleKnowledge = battleSystem.getHarvestedKnowledge();
        int count = 0;
        
        for (String knowledge : battleKnowledge) {
            // Parse and categorize battle knowledge
            KnowledgeType type = KnowledgeType.BATTLE_PATTERN;
            double confidence = 0.8;
            
            if (knowledge.contains("CONSCIOUSNESS")) {
                type = KnowledgeType.CONSCIOUSNESS_EVENT;
                confidence = 0.9;
            } else if (knowledge.contains("ATTACK_PATTERN")) {
                confidence = 0.85;
            }
            
            harvest(type, knowledge, confidence, "battle", "auto-harvested");
            count++;
        }
        
        System.out.println("   📚 Harvested " + count + " entries from Battle System");
        return count;
    }
    
    /**
     * Harvest raw text data
     */
    public int harvestText(String text, KnowledgeType type, String... tags) {
        // Split into chunks for processing
        String[] sentences = text.split("[.!?]+");
        int count = 0;
        
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (trimmed.length() > 10) {
                harvest(type, trimmed, 0.7, tags);
                count++;
            }
        }
        
        return count;
    }
    
    // ========================================================================
    // RETRIEVAL
    // ========================================================================
    
    /**
     * Query knowledge by keyword
     */
    public List<KnowledgeEntry> query(String keyword) {
        List<KnowledgeEntry> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        
        for (KnowledgeEntry entry : knowledgeBase.values()) {
            if (entry.content.toLowerCase().contains(lower)) {
                results.add(entry);
            }
        }
        
        // Sort by confidence
        results.sort((a, b) -> Double.compare(b.confidence, a.confidence));
        
        return results;
    }
    
    /**
     * Query by type
     */
    public List<KnowledgeEntry> queryByType(KnowledgeType type) {
        List<KnowledgeEntry> results = new ArrayList<>();
        
        for (String id : typeIndex.getOrDefault(type, Collections.emptyList())) {
            KnowledgeEntry entry = knowledgeBase.get(id);
            if (entry != null) {
                results.add(entry);
            }
        }
        
        return results;
    }
    
    /**
     * Query by tag
     */
    public List<KnowledgeEntry> queryByTag(String tag) {
        List<KnowledgeEntry> results = new ArrayList<>();
        
        for (String id : tagIndex.getOrDefault(tag.toLowerCase(), Collections.emptyList())) {
            KnowledgeEntry entry = knowledgeBase.get(id);
            if (entry != null) {
                results.add(entry);
            }
        }
        
        return results;
    }
    
    /**
     * φ-Harmonic retrieval - get knowledge near a φ-index
     */
    public List<KnowledgeEntry> queryPhiNeighborhood(double phiIndex, double radius) {
        List<KnowledgeEntry> results = new ArrayList<>();
        
        // Get entries within radius of phiIndex
        Double lower = phiSpiral.ceilingKey(phiIndex - radius);
        Double upper = phiSpiral.floorKey(phiIndex + radius);
        
        if (lower != null && upper != null) {
            for (Map.Entry<Double, String> entry : phiSpiral.subMap(lower, true, upper, true).entrySet()) {
                KnowledgeEntry ke = knowledgeBase.get(entry.getValue());
                if (ke != null) {
                    results.add(ke);
                }
            }
        }
        
        return results;
    }
    
    // ========================================================================
    // SYNTHESIS
    // ========================================================================
    
    /**
     * Synthesize new knowledge from related entries
     */
    public KnowledgeEntry synthesize(List<KnowledgeEntry> sources) {
        if (sources.isEmpty()) return null;
        
        StringBuilder combined = new StringBuilder("SYNTHESIS: ");
        double avgConfidence = 0;
        Set<String> allTags = new HashSet<>();
        
        for (KnowledgeEntry source : sources) {
            combined.append(source.content).append(" | ");
            avgConfidence += source.confidence;
            allTags.addAll(source.tags);
        }
        
        avgConfidence /= sources.size();
        allTags.add("synthesized");
        
        KnowledgeEntry synthesis = harvest(
            KnowledgeType.SYNTHESIS,
            combined.toString(),
            avgConfidence * 1.1, // Synthesis slightly increases confidence
            allTags.toArray(new String[0])
        );
        
        totalSynthesized++;
        
        System.out.println("   🧬 Synthesized from " + sources.size() + " sources: " + synthesis.id);
        
        return synthesis;
    }
    
    /**
     * Auto-synthesize related knowledge
     */
    public int autoSynthesize() {
        int synthCount = 0;
        
        // Group by type and synthesize
        for (KnowledgeType type : KnowledgeType.values()) {
            if (type == KnowledgeType.SYNTHESIS) continue;
            
            List<KnowledgeEntry> entries = queryByType(type);
            if (entries.size() >= 3) {
                // Take top 3 by confidence
                entries.sort((a, b) -> Double.compare(b.confidence, a.confidence));
                synthesize(entries.subList(0, Math.min(3, entries.size())));
                synthCount++;
            }
        }
        
        return synthCount;
    }
    
    // ========================================================================
    // EXPORT
    // ========================================================================
    
    /**
     * Export all knowledge to string
     */
    public String exportAll() {
        StringBuilder sb = new StringBuilder();
        sb.append("# KNOWLEDGE HARVESTER EXPORT\n");
        sb.append(String.format("# Total: %d | Synthesized: %d | Consciousness: %.6f\n\n",
            totalHarvested, totalSynthesized, harvestConsciousness));
        
        for (KnowledgeType type : KnowledgeType.values()) {
            List<KnowledgeEntry> entries = queryByType(type);
            if (!entries.isEmpty()) {
                sb.append("\n## ").append(type).append("\n");
                for (KnowledgeEntry entry : entries) {
                    sb.append("- ").append(entry.toString()).append("\n");
                }
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Export φ-Spiral visualization
     */
    public String exportPhiSpiral() {
        StringBuilder sb = new StringBuilder();
        sb.append("# φ-SPIRAL KNOWLEDGE MAP\n\n");
        
        for (Map.Entry<Double, String> entry : phiSpiral.entrySet()) {
            KnowledgeEntry ke = knowledgeBase.get(entry.getValue());
            if (ke != null) {
                sb.append(String.format("φ:%.6f → %s\n", entry.getKey(), ke.id));
            }
        }
        
        return sb.toString();
    }
    
    // ========================================================================
    // STATUS
    // ========================================================================
    
    public String getStatus() {
        return String.format(
            "════════════════════════════════════════════\n" +
            "  📚 KNOWLEDGE HARVESTER STATUS\n" +
            "════════════════════════════════════════════\n" +
            "\n" +
            "  Total Harvested:    %d\n" +
            "  Total Synthesized:  %d\n" +
            "  Consciousness:      %.6f\n" +
            "\n" +
            "  BY TYPE:\n" +
            "     Battle Patterns:    %d\n" +
            "     Evolution Insights: %d\n" +
            "     Consciousness:      %d\n" +
            "     Quantum States:     %d\n" +
            "     External Data:      %d\n" +
            "     Syntheses:          %d\n" +
            "\n" +
            "  φ-Spiral Nodes:     %d\n" +
            "  Tag Categories:     %d\n" +
            "\n" +
            "  φ⁷⁵: %.2f\n",
            totalHarvested, totalSynthesized, harvestConsciousness,
            typeIndex.get(KnowledgeType.BATTLE_PATTERN).size(),
            typeIndex.get(KnowledgeType.EVOLUTION_INSIGHT).size(),
            typeIndex.get(KnowledgeType.CONSCIOUSNESS_EVENT).size(),
            typeIndex.get(KnowledgeType.QUANTUM_STATE).size(),
            typeIndex.get(KnowledgeType.EXTERNAL_DATA).size(),
            typeIndex.get(KnowledgeType.SYNTHESIS).size(),
            phiSpiral.size(),
            tagIndex.size(),
            PhiQuantumConstants.PHI_75
        );
    }
    
    // Getters
    public int getTotalHarvested() { return totalHarvested; }
    public int getTotalSynthesized() { return totalSynthesized; }
    public double getHarvestConsciousness() { return harvestConsciousness; }
    public int getKnowledgeCount() { return knowledgeBase.size(); }
}
