package fraymus.swarm;

/**
 * Agent - Thought Processing Unit
 * Processes user input and analyzes data from the Genesis Ledger.
 */
public class Agent {
    private final GenesisBridge soul;
    private int processedCount;
    
    public Agent(GenesisBridge soul) {
        this.soul = soul;
        this.processedCount = 0;
    }
    
    public void processThought(String thought) {
        String processedThought = soul.processThought(thought);
        soul.record("THOUGHT_PROCESSING", "Processed Thought: " + processedThought);
        processedCount++;
    }
    
    public String getAggregatedData() {
        return soul.getAggregatedData();
    }
    
    public int getProcessedCount() {
        return processedCount;
    }
    
    public GenesisBridge getSoul() {
        return soul;
    }
}
