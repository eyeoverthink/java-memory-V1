package fraymus.quantum.brain;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptive Logic Engine - Self-Modifying Brain System
 * 
 * Manages brain evolution through fitness-tested mutations:
 * 1. Snapshot current brain as baseline
 * 2. Mutate a gate
 * 3. Run trial for TRIAL_DURATION ticks
 * 4. Measure fitness (energy, spikes, entanglement, reproduction)
 * 5. If improvement > MIN_IMPROVEMENT → ADOPT mutation
 *    Else → REVERT to baseline
 * 6. Store proven strategies for inheritance
 */
public class AdaptiveLogicEngine {

    private static final int TRIAL_DURATION = 300;
    private static final int MAX_STRATEGIES = 10;
    private static final double MIN_IMPROVEMENT = 0.05;

    private List<StrategyGenome> provenStrategies = new ArrayList<>();
    private StrategyGenome currentBaseline;
    private StrategyGenome trialStrategy;
    private boolean inTrial;
    private int trialTicksRemaining;
    private double preTrialFitness;

    private double fitnessAccumulator;
    private int fitnessSamples;
    private double currentFitness;

    private int adaptationCount;
    private int revertCount;
    private int totalTrials;
    private int generation;

    public AdaptiveLogicEngine(LogicBrain brain, int generation) {
        this.currentBaseline = new StrategyGenome(brain, generation);
        this.inTrial = false;
        this.trialTicksRemaining = 0;
        this.fitnessAccumulator = 0;
        this.fitnessSamples = 0;
        this.currentFitness = 0;
        this.adaptationCount = 0;
        this.revertCount = 0;
        this.totalTrials = 0;
        this.generation = generation;
    }

    /**
     * Record a fitness sample based on current entity state.
     * Called each tick to build up fitness data.
     */
    public void recordFitnessSample(float energy, boolean spikeActive, int nearbyEntangled, boolean reproduced) {
        double sample = energy * 0.4;
        if (spikeActive) sample += 0.2;
        sample += Math.min(nearbyEntangled * 0.1, 0.3);
        if (reproduced) sample += 0.3;

        fitnessAccumulator += sample;
        fitnessSamples++;

        if (fitnessSamples > 0) {
            currentFitness = fitnessAccumulator / fitnessSamples;
        }
    }

    /**
     * Begin a new mutation trial.
     * Snapshots current brain, records fitness, then mutates.
     */
    public void beginTrial(LogicBrain brain) {
        if (inTrial) return;

        currentBaseline = new StrategyGenome(brain, generation);
        preTrialFitness = currentFitness;
        fitnessAccumulator = 0;
        fitnessSamples = 0;

        brain.mutate();

        trialStrategy = new StrategyGenome(brain, generation);
        inTrial = true;
        trialTicksRemaining = TRIAL_DURATION;
        totalTrials++;
    }

    /**
     * Tick the trial forward. Returns the result when trial completes.
     */
    public TrialResult tickTrial(LogicBrain brain) {
        if (!inTrial) return TrialResult.NOT_IN_TRIAL;

        trialTicksRemaining--;
        if (trialTicksRemaining > 0) return TrialResult.IN_PROGRESS;

        double trialFitness = fitnessSamples > 0 ? fitnessAccumulator / fitnessSamples : 0;
        double improvement = trialFitness - preTrialFitness;

        if (improvement > MIN_IMPROVEMENT) {
            trialStrategy.fitnessScore = trialFitness;
            trialStrategy.survivedTicks = TRIAL_DURATION;
            addProvenStrategy(trialStrategy);
            currentBaseline = trialStrategy;
            adaptationCount++;
            inTrial = false;
            return TrialResult.ADOPTED;
        } else {
            currentBaseline.applyTo(brain);
            revertCount++;
            inTrial = false;
            return TrialResult.REVERTED;
        }
    }

    private void addProvenStrategy(StrategyGenome strategy) {
        int replaceIndex = -1;
        for (int i = 0; i < provenStrategies.size(); i++) {
            if (provenStrategies.get(i).similarity(strategy) > 0.9) {
                if (strategy.fitnessScore > provenStrategies.get(i).fitnessScore) {
                    replaceIndex = i;
                }
                break;
            }
        }

        if (replaceIndex >= 0) {
            provenStrategies.set(replaceIndex, strategy);
            return;
        }

        provenStrategies.add(strategy);

        if (provenStrategies.size() > MAX_STRATEGIES) {
            int weakestIdx = 0;
            for (int i = 1; i < provenStrategies.size(); i++) {
                if (provenStrategies.get(i).fitnessScore < provenStrategies.get(weakestIdx).fitnessScore) {
                    weakestIdx = i;
                }
            }
            provenStrategies.remove(weakestIdx);
        }
    }

    public StrategyGenome getBestStrategy() {
        if (provenStrategies.isEmpty()) return currentBaseline;
        StrategyGenome best = provenStrategies.get(0);
        for (StrategyGenome s : provenStrategies) {
            if (s.fitnessScore > best.fitnessScore) best = s;
        }
        return best;
    }

    public List<StrategyGenome> getProvenStrategies() {
        return provenStrategies;
    }

    /**
     * Inherit strategies from a parent engine (for offspring).
     * Inherited strategies get 80% of original fitness.
     */
    public void inheritStrategies(AdaptiveLogicEngine parent) {
        for (StrategyGenome s : parent.provenStrategies) {
            StrategyGenome inherited = s.copy();
            inherited.fitnessScore *= 0.8;
            addProvenStrategy(inherited);
        }
    }

    public String encodeStrategies() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < provenStrategies.size(); i++) {
            sb.append(provenStrategies.get(i).encode());
            if (i < provenStrategies.size() - 1) sb.append("##");
        }
        return sb.toString();
    }

    public void decodeStrategies(String encoded) {
        if (encoded == null || encoded.isEmpty()) return;
        String[] parts = encoded.split("##");
        for (String part : parts) {
            StrategyGenome sg = StrategyGenome.decode(part);
            if (sg != null) addProvenStrategy(sg);
        }
    }

    // Getters
    public boolean isInTrial() { return inTrial; }
    public int getTrialTicksRemaining() { return trialTicksRemaining; }
    public double getCurrentFitness() { return currentFitness; }
    public int getAdaptationCount() { return adaptationCount; }
    public int getRevertCount() { return revertCount; }
    public int getTotalTrials() { return totalTrials; }
    public int getProvenStrategyCount() { return provenStrategies.size(); }
    public StrategyGenome getCurrentBaseline() { return currentBaseline; }
    public int getGeneration() { return generation; }

    public enum TrialResult {
        NOT_IN_TRIAL,
        IN_PROGRESS,
        ADOPTED,
        REVERTED
    }

    @Override
    public String toString() {
        return String.format("Adaptive[trials=%d, adopted=%d, reverted=%d, strategies=%d, fitness=%.3f%s]",
                totalTrials, adaptationCount, revertCount, provenStrategies.size(), currentFitness,
                inTrial ? " TRIAL(" + trialTicksRemaining + ")" : "");
    }
}
