package fraymus.omega;

import fraymus.hyper.HyperFormer;

import java.util.Random;
import java.util.concurrent.Callable;

public class FutureSimulation implements Callable<FutureSimulation> {

    public final int id;
    public double score = 0.0;
    public HyperFormer evolvedMind;
    private final Random rng = new Random();

    public FutureSimulation(HyperFormer parent, int id) {
        this.id = id;
        // Deep copy the mind (Simulated here by re-seeding)
        // In a real system, we serialize/deserialize the memory state.
        this.evolvedMind = new HyperFormer(System.nanoTime() + id);
    }

    @Override
    public FutureSimulation call() {
        long start = System.nanoTime();

        // 1. APPLY MUTATION (The "What If")
        String[] challenge = {"The", "future", "is", "recursive"};
        evolvedMind.learnSentence(challenge);

        // 2. TEST INTELLIGENCE
        String prediction = evolvedMind.predictNext(new String[]{"The", "future", "is"});

        // 3. CALCULATE FITNESS
        boolean correct = prediction.equals("recursive");
        long duration = System.nanoTime() - start;

        this.score = (correct ? 100.0 : 0.0) + (1_000_000.0 / duration);

        // Add some chaos (Evolutionary drift)
        this.score += rng.nextDouble() * 10.0;

        return this;
    }
}
