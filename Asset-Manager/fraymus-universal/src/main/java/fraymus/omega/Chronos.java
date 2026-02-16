package fraymus.omega;

import fraymus.hyper.HyperFormer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Chronos {

    private final HyperFormer currentMind;
    private final ExecutorService multiverse = Executors.newFixedThreadPool(8); // 8 Parallel Futures

    public Chronos(HyperFormer mind) {
        this.currentMind = mind;
    }

    /**
     * RETROCAUSAL STEP:
     * 1. Split timeline into 8 Futures.
     * 2. Mutate each future slightly.
     * 3. Run them.
     * 4. Select the Winner.
     * 5. Collapse Reality (Become the Winner).
     */
    public HyperFormer step() {
        System.out.println("⏳ CHRONOS: Splitting Timeline...");

        List<Callable<FutureSimulation>> timelines = new ArrayList<>();

        // Fork 8 possible futures
        for (int i = 0; i < 8; i++) {
            timelines.add(new FutureSimulation(currentMind, i));
        }

        try {
            // Run all futures in parallel
            List<Future<FutureSimulation>> results = multiverse.invokeAll(timelines);

            FutureSimulation bestFuture = null;
            double bestScore = -1.0;

            for (Future<FutureSimulation> f : results) {
                FutureSimulation sim = f.get();
                if (sim.score > bestScore) {
                    bestScore = sim.score;
                    bestFuture = sim;
                }
            }

            System.out.println("⚡ TIMELINE COLLAPSE: Choosing Future #" + bestFuture.id + " (Score: " + String.format("%.4f", bestScore) + ")");

            // The Present becomes the Best Future
            return bestFuture.evolvedMind;

        } catch (Exception e) {
            e.printStackTrace();
            return currentMind; // Safety fallback
        }
    }

    public void shutdown() {
        multiverse.shutdownNow();
    }
}
