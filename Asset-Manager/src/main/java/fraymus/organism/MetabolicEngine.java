package fraymus.organism;

/**
 * LAZARUS — Metabolic Engine
 *
 * Regulates the organism's hunger, energy, and excretion cycles.
 * Entropy rises every metabolize() call; food (data) reduces hunger and adds energy.
 */
public class MetabolicEngine {

    private static final double HUNGER_RATE      = 0.05;
    private static final double ENERGY_DECAY     = 0.02;
    private static final double HUNGER_THRESHOLD = 0.50;

    private double hunger     = 0.80;
    private double energy     = 0.20;
    private int    cycles     = 0;
    private int    feedEvents = 0;

    /** Called each lifecycle cycle — entropy rises, energy decays. */
    public void metabolize() {
        cycles++;
        hunger = Math.min(1.0, hunger + HUNGER_RATE);
        energy = Math.max(0.0, energy - ENERGY_DECAY);
    }

    /** Feed the organism data-derived calories. */
    public void feed(double calories) {
        feedEvents++;
        hunger = Math.max(0.0, hunger - calories * 0.15);
        energy = Math.min(1.0, energy + calories * 0.08);
    }

    public boolean isHungry()  { return hunger > HUNGER_THRESHOLD; }
    public double  getHunger() { return hunger; }
    public double  getEnergy() { return energy; }
    public int     getCycles() { return cycles; }

    public String getStatus() {
        return String.format("[LAZARUS] Hunger=%.2f Energy=%.2f Cycles=%d FeedEvents=%d State=%s",
            hunger, energy, cycles, feedEvents, isHungry() ? "HUNGRY" : "SATIATED");
    }
}
