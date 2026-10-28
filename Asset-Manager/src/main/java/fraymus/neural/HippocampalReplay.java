package fraymus.neural;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HippocampalReplay - Memory consolidation through replay
 * 
 * This class implements a hippocampal replay system that consolidates memories
 * by replaying episodes, strengthening important ones, and identifying patterns.
 * 
 * CAPABILITIES:
 * - Replay episodes for consolidation
 * - Strengthen frequently accessed memories
 * - Pattern detection in sequences
 * - Configurable replay frequency
 * - Statistics tracking
 * 
 * DESIGN PRINCIPLES:
 * - Safe: No modification of existing SentientCore structures
 * - Efficient: O(n) replay operations
 * - Configurable: Replay frequency adjustable
 * - Deterministic: No randomness
 * 
 * PHASE 1.3: Episodic Memory
 * Part of the AGI System Blueprint
 */
public class HippocampalReplay {

    // Default replay frequency (replay every N adds)
    public static final int DEFAULT_REPLAY_FREQUENCY = 10;
    
    // Configuration
    private volatile int replayFrequency = DEFAULT_REPLAY_FREQUENCY;
    
    // Reference to episodic memory
    private final EpisodicMemory episodicMemory;
    
    // Statistics
    private final AtomicInteger replayOperations = new AtomicInteger(0);
    private final AtomicInteger consolidations = new AtomicInteger(0);
    private final AtomicInteger patternDetections = new AtomicInteger(0);
    
    // Add counter for triggering replay
    private final AtomicInteger addCounter = new AtomicInteger(0);

    /**
     * Create a new HippocampalReplay with default settings.
     * 
     * @param episodicMemory The episodic memory to replay
     */
    public HippocampalReplay(EpisodicMemory episodicMemory) {
        if (episodicMemory == null) {
            throw new IllegalArgumentException("EpisodicMemory cannot be null");
        }
        this.episodicMemory = episodicMemory;
    }

    /**
     * Create a new HippocampalReplay with specified replay frequency.
     * 
     * @param episodicMemory The episodic memory to replay
     * @param replayFrequency Replay every N adds
     */
    public HippocampalReplay(EpisodicMemory episodicMemory, int replayFrequency) {
        this(episodicMemory);
        setReplayFrequency(replayFrequency);
    }

    /**
     * Trigger a replay operation.
     * 
     * This replays recent episodes to consolidate them into long-term memory.
     */
    public void replay() {
        if (episodicMemory.isEmpty()) {
            return;
        }
        
        replayOperations.incrementAndGet();
        
        // Get recent episodes (last 60 seconds)
        List<EpisodicMemory.Episode> recent = episodicMemory.getRecent(60);
        
        // Replay and consolidate
        for (EpisodicMemory.Episode episode : recent) {
            // Strengthen the episode
            episodicMemory.strengthen(episode.id, 0.1);
            consolidations.incrementAndGet();
        }
        
        // Detect patterns
        detectPatterns(recent);
    }

    /**
     * Notify that an episode was added.
     * 
     * This triggers replay if the frequency threshold is reached.
     */
    public void notifyAdd() {
        int count = addCounter.incrementAndGet();
        if (count >= replayFrequency) {
            replay();
            addCounter.set(0);
        }
    }

    /**
     * Detect patterns in a sequence of episodes.
     * 
     * This identifies repeated patterns or sequences in the episodes.
     * 
     * @param episodes The episodes to analyze
     * @return List of detected patterns
     */
    public List<String> detectPatterns(List<EpisodicMemory.Episode> episodes) {
        if (episodes == null || episodes.size() < 2) {
            return new ArrayList<>();
        }
        
        List<String> patterns = new ArrayList<>();
        
        // Look for repeated content
        Map<String, Integer> contentCounts = new HashMap<>();
        for (EpisodicMemory.Episode episode : episodes) {
            String content = episode.content.toLowerCase();
            contentCounts.put(content, contentCounts.getOrDefault(content, 0) + 1);
        }
        
        // Find content that appears multiple times
        for (Map.Entry<String, Integer> entry : contentCounts.entrySet()) {
            if (entry.getValue() > 1) {
                patterns.add("Repeated: " + entry.getKey() + " (" + entry.getValue() + " times)");
                patternDetections.incrementAndGet();
            }
        }
        
        return patterns;
    }

    /**
     * Consolidate a specific episode.
     * 
     * @param episodeId The episode ID to consolidate
     */
    public void consolidate(long episodeId) {
        EpisodicMemory.Episode episode = episodicMemory.get(episodeId);
        if (episode != null) {
            episodicMemory.strengthen(episodeId, 0.2);
            consolidations.incrementAndGet();
        }
    }

    /**
     * Consolidate multiple episodes.
     * 
     * @param episodeIds The episode IDs to consolidate
     */
    public void consolidateMultiple(List<Long> episodeIds) {
        for (Long id : episodeIds) {
            consolidate(id);
        }
    }

    /**
     * Get consolidation suggestions.
     * 
     * Returns episodes that should be consolidated based on strength and recency.
     * 
     * @return List of episode IDs to consolidate
     */
    public List<Long> getConsolidationSuggestions() {
        List<EpisodicMemory.Episode> all = episodicMemory.getAll();
        List<Long> suggestions = new ArrayList<>();
        
        // Suggest episodes with low strength but recent
        for (EpisodicMemory.Episode episode : all) {
            if (episode.strength < 0.5 && episode.age() < 300) { // Less than 5 minutes old
                suggestions.add(episode.id);
            }
        }
        
        return suggestions;
    }

    /**
     * Set the replay frequency.
     * 
     * @param frequency Replay every N adds
     */
    public void setReplayFrequency(int frequency) {
        if (frequency <= 0) {
            throw new IllegalArgumentException("Frequency must be positive");
        }
        this.replayFrequency = frequency;
    }

    /**
     * Get the current replay frequency.
     * 
     * @return Current replay frequency
     */
    public int getReplayFrequency() {
        return replayFrequency;
    }

    /**
     * Get statistics about replay operations.
     * 
     * @return Statistics string
     */
    public String getStats() {
        return String.format(
            "HippocampalReplay Stats:\n" +
            "  Replay frequency: %,d\n" +
            "  Replay operations: %,d\n" +
            "  Consolidations: %,d\n" +
            "  Pattern detections: %,d\n" +
            "  Add counter: %,d",
            replayFrequency,
            replayOperations.get(),
            consolidations.get(),
            patternDetections.get(),
            addCounter.get()
        );
    }

    /**
     * Get the episodic memory.
     * 
     * @return The episodic memory
     */
    public EpisodicMemory getEpisodicMemory() {
        return episodicMemory;
    }
}
