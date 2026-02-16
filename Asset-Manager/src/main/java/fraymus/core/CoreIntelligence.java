package fraymus.core;

import fraymus.hyper.HyperFormer;
import fraymus.hyper.FraymusState;
import fraymus.symbolic.HoloGraph;
import fraymus.hyper.HyperVector;

import java.io.Serializable;

/**
 * 🧠 CORE INTELLIGENCE - The Unified Brain
 * "Fast System + Slow System = Complete Mind"
 * 
 * Dual-Process Theory (Kahneman):
 * - System 1 (Fast): Intuitive, automatic, pattern-based (HyperFormer)
 * - System 2 (Slow): Logical, deliberate, fact-based (HoloGraph)
 * 
 * This unifies both systems into a single processing core.
 * It doesn't mock data. It processes real input.
 */
public class CoreIntelligence implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public final HyperFormer fastSystem;  // Intuition (Text Prediction)
    public final HoloGraph slowSystem;    // Logic (Knowledge Graph)
    public final long birthTime;
    
    private long processCount = 0;

    public CoreIntelligence() {
        this(0xCAFEBABE);
    }

    public CoreIntelligence(long seed) {
        this.fastSystem = new HyperFormer(seed);
        this.slowSystem = new HoloGraph();
        this.birthTime = System.currentTimeMillis();
    }

    /**
     * PROCESS: The Main Loop of Intelligence
     * 
     * 1. Embed Input
     * 2. Learn Sequence (Fast System)
     * 3. Extract Facts (Slow System)
     * 4. Return Reaction
     * 
     * @param input The text to process
     * @return The system's response
     */
    public String process(String input) {
        if (input == null || input.isBlank()) return "";

        processCount++;
        String[] tokens = input.split("\\s+");
        
        // 1. FAST LEARNING (Implicit Pattern Recognition)
        fastSystem.learnSentence(tokens);

        // 2. SLOW LEARNING (Explicit Fact Extraction)
        // Simple Heuristic: If input is "A is B", learn it as a fact
        if (tokens.length >= 3 && tokens[1].equalsIgnoreCase("is")) {
            String subject = tokens[0];
            String object = tokens[2];
            
            slowSystem.learn(subject, "is", object);
            
            return "✅ FACT INTEGRATED: " + subject + " is " + object;
        }

        // 3. REACTION (Fast System Prediction)
        // Predict what comes next based on learned patterns
        String prediction = fastSystem.predictNext(tokens);
        
        // 4. FACT CHECK (Slow System Query)
        // If prediction is uncertain, try to query the knowledge graph
        // Note: HoloGraph query functionality would go here
        // For now, we just return the fast system prediction
        if (prediction.equals("???") && tokens.length > 0) {
            // Future: Query knowledge graph for facts
            // String fact = slowSystem.query(lastToken, "is");
        }

        return prediction;
    }

    /**
     * Get statistics
     */
    public long getProcessCount() {
        return processCount;
    }

    public long getUptime() {
        return System.currentTimeMillis() - birthTime;
    }

    public int getVocabSize() {
        return fastSystem.vocabSize();
    }

    public int getMemoryWeight() {
        return fastSystem.memoryWeight();
    }

    public int getFactCount() {
        return slowSystem.getFactCount();
    }

    public int getConceptCount() {
        return slowSystem.getConceptCount();
    }

    /**
     * Export state for persistence
     */
    public CoreState exportState() {
        return new CoreState(
            fastSystem.exportState(),
            slowSystem,
            birthTime,
            processCount
        );
    }

    /**
     * Restore from state
     */
    public static CoreIntelligence fromState(CoreState state) {
        CoreIntelligence core = new CoreIntelligence();
        // Note: Would need to implement proper restoration
        // For now, this is a placeholder
        return core;
    }

    /**
     * State container for serialization
     */
    public record CoreState(
        FraymusState fastState,
        HoloGraph slowState,
        long birthTime,
        long processCount
    ) implements Serializable {
        private static final long serialVersionUID = 1L;
    }
}
