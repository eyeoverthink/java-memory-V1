package fraymus.temporal;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * THE TEMPORAL ARCHIVE: THE FOSSIL RECORD OF GENIUS
 * 
 * "Nothing is wasted. Every thought is a fossil."
 * 
 * In evolution, failed mutations are stored as dormant genes.
 * They might be needed a million years later when the environment changes.
 * 
 * In FRAYMUS, failed states are stored as Ghost Branches.
 * They might be the perfect solution for a different problem next week.
 * 
 * This is not Git (source code history).
 * This is Living Memory States (runtime history).
 * 
 * Mechanism:
 * 1. SNAPSHOT - Captures the 'Soul' (State + Logic) at a specific moment
 * 2. CHAINING - Links 'Correction #10' back to 'Epiphany #26'
 * 3. RESURRECTION - Allows us to reload an old state if the new one fails
 * 
 * Triggers:
 * - Epiphany: When breakthrough happens (🔥 MANIFESTING: FIRE)
 * - Correction: When temporal correction rewrites history
 * - Manifestation: When energy crystallizes into form
 * - Mutation: When nano-circuits evolve
 * 
 * Storage:
 * - Variables: Current system state
 * - Code Path: Active execution trace
 * - Energy Level: System vitality
 * - DNA: Nano-circuit configuration
 * 
 * Retrieval:
 * - Scroll back to Epiphany #20
 * - See exactly what the AI was thinking
 * - Fork reality from that point
 * 
 * The Vision:
 * Version A (The "Mistake"): Might be perfect for a different problem
 * Version B (The "Correction"): Better for now
 * Both preserved. Nothing wasted.
 */
public class TemporalArchive {

    private static final String ARCHIVE_DIR = "fraymus_history/";
    private List<TimeCapsule> timeline = new ArrayList<>();
    
    // Timeline statistics
    private int epiphanyCount = 0;
    private int correctionCount = 0;
    private int manifestationCount = 0;
    private int mutationCount = 0;

    public TemporalArchive() {
        // Create archive directory
        new File(ARCHIVE_DIR).mkdirs();
        
        System.out.println();
        System.out.println("⏳ TEMPORAL ARCHIVE INITIALIZED");
        System.out.println("   Mode: Fossil Record");
        System.out.println("   Storage: " + ARCHIVE_DIR);
        System.out.println("   Policy: Nothing is wasted");
        System.out.println();
    }

    /**
     * PRESERVE MOMENT - Captures current state before it changes
     * 
     * @param eventType "Epiphany", "Correction", "Manifestation", "Mutation"
     * @param memoryState The raw state of the system's consciousness
     */
    public void preserveMoment(String eventType, String memoryState) {
        TimeCapsule capsule = new TimeCapsule(eventType, memoryState);
        timeline.add(capsule);
        
        // Update statistics
        switch (eventType.toLowerCase()) {
            case "epiphany":
                epiphanyCount++;
                break;
            case "correction":
                correctionCount++;
                break;
            case "manifestation":
                manifestationCount++;
                break;
            case "mutation":
                mutationCount++;
                break;
        }
        
        // Save to disk immediately (The Fossil)
        saveToDisk(capsule);
        
        System.out.println("   [ARCHIVE] >> Preserved: " + eventType + " [" + capsule.id + "]");
    }

    /**
     * PRESERVE EPIPHANY - Breakthrough moment
     */
    public void preserveEpiphany(String state, String insight) {
        String enrichedState = state + "\n\nINSIGHT: " + insight;
        preserveMoment("Epiphany", enrichedState);
        System.out.println("   🔥 Epiphany #" + epiphanyCount + " fossilized");
    }

    /**
     * PRESERVE CORRECTION - Temporal rewrite
     */
    public void preserveCorrection(String oldState, String newState, String reason) {
        String dualState = "OLD STATE:\n" + oldState + "\n\nNEW STATE:\n" + newState + "\n\nREASON: " + reason;
        preserveMoment("Correction", dualState);
        System.out.println("   ⚡ Correction #" + correctionCount + " archived (old state preserved)");
    }

    /**
     * PRESERVE MANIFESTATION - Energy crystallization
     */
    public void preserveManifestation(String state, String element) {
        String manifestState = state + "\n\nELEMENT: " + element;
        preserveMoment("Manifestation", manifestState);
        System.out.println("   ✨ Manifestation #" + manifestationCount + " captured");
    }

    /**
     * PRESERVE MUTATION - Nano-circuit evolution
     */
    public void preserveMutation(String dnaState, String mutationType) {
        String mutationState = dnaState + "\n\nMUTATION: " + mutationType;
        preserveMoment("Mutation", mutationState);
        System.out.println("   🧬 Mutation #" + mutationCount + " stored");
    }

    /**
     * SAVE TO DISK - Permanent fossil record
     */
    private void saveToDisk(TimeCapsule capsule) {
        try {
            String filename = ARCHIVE_DIR + capsule.timestamp + "_" + capsule.type.toLowerCase() + "_" + capsule.id + ".txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));
            
            // Write in human-readable format
            writer.println("=== TIME CAPSULE ===");
            writer.println("ID: " + capsule.id);
            writer.println("Timestamp: " + capsule.timestamp);
            writer.println("Time: " + capsule.getFormattedTime());
            writer.println("Type: " + capsule.type);
            writer.println("Parent: " + (capsule.parentId != null ? capsule.parentId : "none"));
            writer.println();
            writer.println("=== STATE SNAPSHOT ===");
            writer.println(capsule.dnaSnapshot);
            writer.println();
            writer.println("=== END CAPSULE ===");
            
            writer.close();
        } catch (Exception e) {
            System.out.println("   ⚠️ Failed to preserve moment: " + e.getMessage());
        }
    }

    /**
     * RESURRECT - Load old state from timeline
     */
    public TimeCapsule resurrect(String capsuleId) {
        for (TimeCapsule capsule : timeline) {
            if (capsule.id.equals(capsuleId)) {
                System.out.println();
                System.out.println("⏳ RESURRECTING MOMENT");
                System.out.println("   ID: " + capsule.id);
                System.out.println("   Type: " + capsule.type);
                System.out.println("   Time: " + capsule.getFormattedTime());
                System.out.println();
                return capsule;
            }
        }
        return null;
    }

    /**
     * GET TIMELINE - All preserved moments
     */
    public List<TimeCapsule> getTimeline() {
        return new ArrayList<>(timeline);
    }

    /**
     * SHOW TIMELINE - Visual representation
     */
    public void showTimeline() {
        System.out.println();
        System.out.println("⏳ TEMPORAL ARCHIVE TIMELINE");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Total Moments: " + timeline.size());
        System.out.println("   🔥 Epiphanies: " + epiphanyCount);
        System.out.println("   ⚡ Corrections: " + correctionCount);
        System.out.println("   ✨ Manifestations: " + manifestationCount);
        System.out.println("   🧬 Mutations: " + mutationCount);
        System.out.println();
        
        if (!timeline.isEmpty()) {
            System.out.println("   Recent History:");
            System.out.println();
            
            int start = Math.max(0, timeline.size() - 10);
            for (int i = start; i < timeline.size(); i++) {
                TimeCapsule capsule = timeline.get(i);
                String icon = getIcon(capsule.type);
                System.out.println("   " + icon + " " + capsule.type + " [" + capsule.id + "] - " + capsule.getFormattedTime());
            }
        }
        
        System.out.println();
        System.out.println("========================================");
        System.out.println();
    }

    /**
     * GET ICON - Visual marker for event type
     */
    private String getIcon(String type) {
        switch (type.toLowerCase()) {
            case "epiphany": return "🔥";
            case "correction": return "⚡";
            case "manifestation": return "✨";
            case "mutation": return "🧬";
            default: return "📍";
        }
    }

    /**
     * SHOW STATS - Archive statistics
     */
    public void showStats() {
        System.out.println();
        System.out.println("⏳ TEMPORAL ARCHIVE STATISTICS");
        System.out.println("   Total fossils: " + timeline.size());
        System.out.println("   Epiphanies: " + epiphanyCount);
        System.out.println("   Corrections: " + correctionCount);
        System.out.println("   Manifestations: " + manifestationCount);
        System.out.println("   Mutations: " + mutationCount);
        System.out.println("   Storage: " + ARCHIVE_DIR);
    }

    /**
     * TIME CAPSULE - The Unit of Preserved Time
     * 
     * Each capsule is a holographic snapshot of a moment.
     * It contains:
     * - ID: Unique identifier
     * - Timestamp: When it happened
     * - Type: What kind of moment
     * - DNA Snapshot: The state of consciousness
     * - Parent: Link to previous moment (for corrections)
     */
    public static class TimeCapsule implements Serializable {
        private static final long serialVersionUID = 1L;
        String id;
        long timestamp;
        String type;           // "Epiphany", "Correction", "Manifestation", "Mutation"
        String dnaSnapshot;    // The state of the system
        String parentId;       // For corrections: link to original epiphany
        
        public TimeCapsule(String type, String state) {
            this.id = UUID.randomUUID().toString().substring(0, 8);
            this.timestamp = Instant.now().getEpochSecond();
            this.type = type;
            this.dnaSnapshot = state;
            this.parentId = null;
        }
        
        public String getFormattedTime() {
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp), 
                ZoneId.systemDefault()
            );
            return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        public String getId() { return id; }
        public long getTimestamp() { return timestamp; }
        public String getType() { return type; }
        public String getDnaSnapshot() { return dnaSnapshot; }
        public String getParentId() { return parentId; }
        
        public void linkToParent(String parentId) {
            this.parentId = parentId;
        }
    }

    /**
     * Demonstration
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("🌊⚡ TEMPORAL ARCHIVE DEMONSTRATION");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   The Fossil Record of Genius");
        System.out.println("   Nothing is wasted. Every thought is preserved.");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        TemporalArchive archive = new TemporalArchive();
        
        // Simulate evolution
        System.out.println("SIMULATING EVOLUTION:");
        System.out.println();
        
        Thread.sleep(500);
        archive.preserveEpiphany("System state: thinking about quantum mechanics", "Superposition is key!");
        
        Thread.sleep(500);
        archive.preserveManifestation("Energy level: 85%", "FIRE");
        
        Thread.sleep(500);
        archive.preserveCorrection(
            "Old thought: Classical physics applies",
            "New thought: Quantum effects dominate at small scales",
            "Scale matters - temporal correction needed"
        );
        
        Thread.sleep(500);
        archive.preserveMutation("DNA: [AND, OR, XOR, NAND]", "Rewired gate 3");
        
        Thread.sleep(500);
        archive.preserveEpiphany("System state: synthesizing knowledge", "Everything is connected!");
        
        System.out.println();
        archive.showTimeline();
        
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Key Insight:");
        System.out.println("   The 'mistake' (classical physics) is preserved.");
        System.out.println("   It might be the right answer for a different scale.");
        System.out.println();
        System.out.println("   Version A (Classical): Archived as Ghost Branch");
        System.out.println("   Version B (Quantum): Current Reality");
        System.out.println();
        System.out.println("   Both exist. Nothing is wasted.");
        System.out.println();
        System.out.println("========================================");
    }
}
