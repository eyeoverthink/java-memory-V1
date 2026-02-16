package fraymus.omega;

import fraymus.hyper.HyperFormer;
import java.util.Random;

/**
 * 🌿 FUTURE SIMULATION - The Branch
 * "One potential timeline in the multiverse"
 * 
 * This represents a single parallel future where the AI tries a specific mutation.
 * 
 * Examples of mutations:
 * - "What if I ignore trigrams?"
 * - "What if I bind vectors in reverse?"
 * - "What if I use different permutation steps?"
 * 
 * Each future is scored on:
 * - Correctness (did it predict accurately?)
 * - Speed (how fast did it execute?)
 * - Novelty (random evolutionary drift)
 * 
 * The highest-scoring future becomes the new present.
 */
public class FutureSimulation implements java.util.concurrent.Callable<FutureSimulation> {

    public final int id;
    public double score = 0.0;
    public HyperFormer evolvedMind;
    private final Random rng;

    public FutureSimulation(HyperFormer parent, int id) {
        this.id = id;
        this.rng = new Random(System.nanoTime() + id);
        
        // Deep copy the mind (simulated by creating new instance)
        // In production, would serialize/deserialize memory state
        this.evolvedMind = new HyperFormer();
    }

    @Override
    public FutureSimulation call() {
        long start = System.nanoTime();

        // 1. APPLY MUTATION (The "What If")
        // Each timeline tries a different learning strategy
        String[] challenge = {"The", "future", "is", "recursive"};
        evolvedMind.learn(challenge);
        
        // Additional mutations based on timeline ID
        if (id % 2 == 0) {
            // Even timelines: Learn additional context
            evolvedMind.learn(new String[]{"recursive", "systems", "evolve"});
        } else {
            // Odd timelines: Learn reverse patterns
            evolvedMind.learn(new String[]{"evolve", "systems", "recursive"});
        }
        
        // 2. TEST INTELLIGENCE
        // Try to predict a known pattern
        String prediction = evolvedMind.predict(new String[]{"The", "future", "is"});
        
        // 3. CALCULATE FITNESS
        // Correct prediction = High Score
        boolean correct = prediction.equals("recursive");
        long duration = System.nanoTime() - start;
        
        // Base score: correctness
        this.score = correct ? 100.0 : 0.0;
        
        // Speed bonus: faster execution = higher score
        // Normalize to microseconds
        double speedScore = 1_000_000.0 / Math.max(duration, 1);
        this.score += speedScore;
        
        // Evolutionary drift: random variation
        this.score += rng.nextDouble() * 10.0;
        
        // Complexity penalty: simpler is better
        int vocabSize = evolvedMind.vocabSize();
        this.score -= (vocabSize * 0.1);

        return this;
    }

    @Override
    public String toString() {
        return String.format("Future#%d (score: %.2f)", id, score);
    }
}
