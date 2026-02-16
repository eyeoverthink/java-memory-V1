package fraymus.god;

import fraymus.hyper.HyperFormer;
import fraymus.hyper.HyperVector;
import fraymus.hyper.HyperAccumulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * THE ARENA: The Debate Chamber.
 *
 * Poses a problem to N sub-models simultaneously.
 * Vectorizes every opinion into 10,000D space.
 * Finds the centroid of agreement (Truth Vector).
 * Discards outliers (hallucinations).
 * Returns the opinion with maximum resonance to all others.
 */
public class TheArena {

    private final List<NeuralTentacle> tentacles = new ArrayList<>();
    private final HyperFormer coordinator;
    private final ExecutorService pool;

    public TheArena(HyperFormer brain) {
        this.coordinator = brain;
        this.pool = Executors.newCachedThreadPool();

        // DEFAULT TENTACLES: Local Ollama models
        tentacles.add(new NeuralTentacle("llama3", "http://localhost:11434/api/generate"));
        tentacles.add(new NeuralTentacle("mistral", "http://localhost:11434/api/generate"));
        tentacles.add(new NeuralTentacle("codellama", "http://localhost:11434/api/generate"));
        tentacles.add(new NeuralTentacle("gemma2", "http://localhost:11434/api/generate"));
        tentacles.add(new NeuralTentacle("phi3", "http://localhost:11434/api/generate"));
    }

    /** Add a custom tentacle (remote API, different model, etc). */
    public void addTentacle(NeuralTentacle t) {
        tentacles.add(t);
    }

    /** Remove all default tentacles (for custom configurations). */
    public void clearTentacles() {
        tentacles.clear();
    }

    public int tentacleCount() { return tentacles.size(); }

    /**
     * SYNTHESIZE: Force the swarm to debate. Return the Ultimate Truth.
     *
     * 1. Harvest opinions in parallel
     * 2. Vectorize each opinion into 10,000D HyperVector
     * 3. Compute pairwise resonance matrix
     * 4. Select the opinion with maximum total resonance (centroid)
     * 5. Build a Truth Vector from the top-K agreeing opinions
     */
    public ArenaResult solve(String problem) {
        System.out.println("\n⚡ THE ARENA: " + tentacles.size() + " minds summoned.");
        System.out.println("   PROBLEM: " + problem);

        // 1. HARVEST IN PARALLEL
        List<Future<Opinion>> futures = new ArrayList<>();
        for (NeuralTentacle t : tentacles) {
            futures.add(pool.submit(() -> {
                long start = System.nanoTime();
                String answer = t.think(problem);
                long ms = (System.nanoTime() - start) / 1_000_000;
                return new Opinion(t.name(), answer, ms);
            }));
        }

        List<Opinion> opinions = new ArrayList<>();
        for (Future<Opinion> f : futures) {
            try {
                Opinion op = f.get(180, TimeUnit.SECONDS);
                if (!op.text().isBlank()) {
                    opinions.add(op);
                    System.out.println("   [" + op.model() + "] responded (" + op.latencyMs() + "ms, " + op.text().length() + " chars)");
                }
            } catch (Exception e) {
                // Tentacle timed out or died — skip it
            }
        }

        if (opinions.isEmpty()) {
            System.out.println("   ❌ ALL TENTACLES DEAD. No consensus possible.");
            return new ArenaResult("", new HyperVector(), 0.0, 0, List.of());
        }

        if (opinions.size() == 1) {
            System.out.println("   ⚠️ SINGLE OPINION. No consensus — returning as-is.");
            HyperVector v = vectorize(opinions.get(0).text());
            return new ArenaResult(opinions.get(0).text(), v, 1.0, 1, opinions);
        }

        // 2. VECTORIZE EACH OPINION
        List<HyperVector> vectors = new ArrayList<>();
        for (Opinion op : opinions) {
            vectors.add(vectorize(op.text()));
        }

        // 3. RESONANCE MATRIX — find the centroid opinion
        int bestIdx = 0;
        double bestTotalRes = -1.0;
        double[] totalResonances = new double[opinions.size()];

        for (int i = 0; i < vectors.size(); i++) {
            double total = 0;
            for (int j = 0; j < vectors.size(); j++) {
                if (i == j) continue;
                total += vectors.get(i).resonance(vectors.get(j));
            }
            totalResonances[i] = total;
            if (total > bestTotalRes) {
                bestTotalRes = total;
                bestIdx = i;
            }
        }

        // 4. REPORT
        System.out.println("\n   📊 RESONANCE SCORES:");
        for (int i = 0; i < opinions.size(); i++) {
            String marker = (i == bestIdx) ? " 🏆" : "";
            System.out.printf("      [%s] %.4f%s%n", opinions.get(i).model(), totalResonances[i], marker);
        }

        // 5. BUILD TRUTH VECTOR (weighted bundle of top agreeing opinions)
        HyperAccumulator truthBuilder = new HyperAccumulator();
        for (int i = 0; i < vectors.size(); i++) {
            int weight = (int) Math.max(1, totalResonances[i] * 100);
            truthBuilder.add(vectors.get(i), weight);
        }
        HyperVector truthVector = truthBuilder.toVector();

        // Also teach the coordinator all the vocabulary from the debate
        for (Opinion op : opinions) {
            String[] words = op.text().split("\\s+");
            if (words.length >= 2) {
                coordinator.learnSentence(words);
            }
        }

        String winner = opinions.get(bestIdx).text();
        double avgRes = bestTotalRes / (opinions.size() - 1);

        System.out.printf("\n   🔥 CONSENSUS REACHED. Winner: [%s] (avg resonance: %.4f)%n",
                opinions.get(bestIdx).model(), avgRes);

        return new ArenaResult(winner, truthVector, avgRes, opinions.size(), opinions);
    }

    /** Vectorize text: embed each token, bundle into a single thought vector. */
    private HyperVector vectorize(String text) {
        String[] tokens = text.split("\\s+");
        if (tokens.length == 0) return new HyperVector();

        // Use positional encoding: Π(embed(token_i), i) then bundle
        HyperAccumulator b = new HyperAccumulator();
        for (int i = 0; i < tokens.length; i++) {
            String clean = tokens[i].toLowerCase().replaceAll("[^a-z0-9]", "");
            if (clean.isEmpty()) continue;
            HyperVector v = coordinator.embed(clean).permute(i % HyperVector.D);
            b.add(v, 1);
        }
        return b.toVector();
    }

    public record Opinion(String model, String text, long latencyMs) {}

    public record ArenaResult(
            String winnerText,
            HyperVector truthVector,
            double avgResonance,
            int participantCount,
            List<Opinion> allOpinions
    ) {}
}
