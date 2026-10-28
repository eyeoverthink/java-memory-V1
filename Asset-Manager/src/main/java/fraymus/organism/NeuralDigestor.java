package fraymus.organism;

import fraymus.seed.UniversalIntelligenceSeed;

/**
 * DEEPLEARNING4J — Neural Digestive Tract / Synthesizer
 *
 * Converts raw text into structured knowledge by feeding each meaningful word
 * to the shared UniversalIntelligenceSeed via ABSORB.
 * Each digestion run returns "calories" (energy) proportional to words processed.
 */
public class NeuralDigestor {

    private static final int    MIN_WORD_LENGTH  = 4;
    private static final double ENERGY_PER_WORD  = 0.0005;

    private final UniversalIntelligenceSeed seed;
    private double totalEnergyExtracted = 0.0;
    private int    totalWordsDigested   = 0;
    private int    digestRuns           = 0;

    public NeuralDigestor(UniversalIntelligenceSeed seed) {
        this.seed = seed;
    }

    /**
     * Digest raw text: absorb each meaningful word into the Seed's knowledge graph.
     * Returns energy (calories) extracted from this data source.
     */
    public double digest(String rawData) {
        digestRuns++;
        String[] words = rawData.split("[^A-Za-z0-9]+");
        int wordsThisRun = 0;
        for (String word : words) {
            if (word.length() >= MIN_WORD_LENGTH) {
                try { seed.query("ABSORB " + word.toUpperCase()); } catch (Exception ignored) {}
                wordsThisRun++;
            }
        }
        double energy = wordsThisRun * ENERGY_PER_WORD;
        totalWordsDigested   += wordsThisRun;
        totalEnergyExtracted += energy;
        return energy;
    }

    public double getTotalEnergyExtracted() { return totalEnergyExtracted; }
    public int    getTotalWordsDigested()   { return totalWordsDigested; }
    public int    getDigestRuns()           { return digestRuns; }

    public String getStatus() {
        return String.format("[DEEPLEARNING4J] Runs=%d Words=%d Energy=%.4f",
            digestRuns, totalWordsDigested, totalEnergyExtracted);
    }
}
