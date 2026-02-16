package fraymus.run;

import fraymus.chaos.EvolutionaryChaos;

/**
 * Runtime context for a single run
 * Provides reproducible randomness and logging
 * 
 * Uses EvolutionaryChaos - the self-aware fractal RNG
 * Superior to standard RNG because:
 * - Self-aware (detects and escapes patterns)
 * - Infinite state (BigInteger, never overflows)
 * - Recursive evolution (each value depends on history)
 * - Cryptographically mixed (SHA-512)
 */
public class RunContext {
    public final RunConfig cfg;
    public final EvolutionaryChaos rng;
    public final EventLogger log;
    
    public RunContext(RunConfig cfg, EventLogger log) {
        this.cfg = cfg;
        // Use seed as custom seed for EvolutionaryChaos
        this.rng = new EvolutionaryChaos(String.valueOf(cfg.seed));
        this.log = log;
    }
    
    /**
     * Generate random double in [0, 1)
     * NEVER use Math.random() - always use this
     * 
     * Uses EvolutionaryChaos.nextDouble() - self-aware fractal randomness
     */
    public double nextDouble() {
        return rng.nextDouble();
    }
    
    /**
     * Generate random double in [min, max)
     */
    public double nextDouble(double min, double max) {
        return min + rng.nextDouble() * (max - min);
    }
    
    /**
     * Generate random int in [0, bound)
     */
    public int nextInt(int bound) {
        return rng.nextInt(bound);
    }
    
    /**
     * Generate random int in [min, max)
     */
    public int nextInt(int min, int max) {
        return min + rng.nextInt(max - min);
    }
    
    /**
     * Generate random boolean
     */
    public boolean nextBoolean() {
        return rng.nextDouble() < 0.5;
    }
    
    /**
     * Generate random Gaussian (mean=0, stddev=1)
     */
    public double nextGaussian() {
        // Box-Muller transform using EvolutionaryChaos
        double u1 = rng.nextDouble();
        double u2 = rng.nextDouble();
        return Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
    }
    
    /**
     * Get RNG statistics (patterns detected, mutations, etc.)
     */
    public String getRNGStats() {
        return rng.getStats();
    }
}
