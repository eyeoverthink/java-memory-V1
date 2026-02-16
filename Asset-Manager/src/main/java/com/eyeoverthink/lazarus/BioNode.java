package com.eyeoverthink.lazarus;

import java.util.Random;

/**
 * THE BIO-NODE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "A living cell of code."
 * 
 * Based on 'dr_frank.html' Node class.
 * 
 * Properties:
 * - Frequency (Hz): The harmonic vibration of the node
 * - Resonance: Connection strength to other nodes
 * - Evolution Rate: Speed of adaptation
 * 
 * The frequency range is capped between 432Hz (grounding)
 * and 528Hz (transformation), resetting in cycles.
 */
public class BioNode {

    private static final double PHI = 1.618033988749895;
    private static final double BASE_FREQUENCY = 432.0;   // Hz (Grounding)
    private static final double PEAK_FREQUENCY = 528.0;   // Hz (Transformation)
    
    private static long nodeCounter = 0;

    // Identity
    public final long id;
    public final long birthTime;
    
    // Virtual coordinates (for potential UI visualization)
    public double x, y;
    
    // The Brain
    public LogicCircuit brain;
    
    // The Genetic Code
    public DNA dna;
    
    // State
    private int age = 0;
    private boolean alive = true;
    
    private Random random = new Random();

    /**
     * Create a new BioNode
     * @param parent If null, creates a genesis node. Otherwise inherits.
     */
    public BioNode(BioNode parent) {
        this.id = nodeCounter++;
        this.birthTime = System.currentTimeMillis();
        
        if (parent != null) {
            // INHERITANCE
            this.dna = new DNA(
                parent.dna.frequency + (random.nextDouble() - 0.5) * 2, // Slight variation
                parent.dna.resonance * (0.9 + random.nextDouble() * 0.2), // ~90-110%
                parent.dna.evolutionRate * (0.95 + random.nextDouble() * 0.1) // ~95-105%
            );
            
            // Evolve the brain (Sexual reproduction with self)
            this.brain = parent.brain.crossover(parent.brain);
            
            // Inherit position near parent
            this.x = parent.x + (random.nextDouble() - 0.5) * 10;
            this.y = parent.y + (random.nextDouble() - 0.5) * 10;
            
        } else {
            // GENESIS (Adam/Eve node)
            this.dna = new DNA(
                BASE_FREQUENCY + random.nextDouble() * 20, // Start near 432Hz
                0.5 + random.nextDouble(),                  // Resonance 0.5-1.5
                0.05 + random.nextDouble() * 0.05           // Evolution rate 0.05-0.10
            );
            this.brain = new LogicCircuit();
            
            // Random position
            this.x = random.nextDouble() * 100;
            this.y = random.nextDouble() * 100;
        }
    }

    /**
     * UPDATE the node (Called every tick)
     * Evolves frequency, ages, and checks for death conditions
     */
    public void update() {
        if (!alive) return;
        
        age++;
        
        // 1. EVOLVE FREQUENCY
        this.dna.frequency += this.dna.evolutionRate;

        // 2. LIMITER (The 528Hz Cap)
        // When frequency exceeds peak, reset to base (Rebirth cycle)
        if (this.dna.frequency > PEAK_FREQUENCY) {
            this.dna.frequency = BASE_FREQUENCY;
            // Resonance boost on cycle completion
            this.dna.resonance *= 1.1;
        }
        
        // 3. PHI RESONANCE
        // Apply golden ratio decay to resonance
        this.dna.resonance *= (1.0 - (1.0 / (PHI * 100)));
        
        // 4. EVALUATE BRAIN
        boolean[] inputs = generateInputs();
        brain.evaluate(inputs);
        
        // 5. NATURAL DEATH (Old age)
        if (age > 1000 && random.nextDouble() < 0.01) {
            die("OLD_AGE");
        }
    }

    /**
     * Generate inputs for the logic circuit based on current state
     */
    private boolean[] generateInputs() {
        return new boolean[] {
            dna.frequency > 480,                    // High frequency
            dna.resonance > 1.0,                    // High resonance
            age % 2 == 0,                           // Even age
            x > 50,                                 // Right side
            y > 50,                                 // Upper side
            brain.getFitness() > 0.5,               // High fitness
            random.nextBoolean(),                   // Random noise
            (System.currentTimeMillis() % 2) == 0   // Time parity
        };
    }

    /**
     * Check if ready to reproduce
     * Logic: High resonance = High fertility
     */
    public boolean readyToReproduce() {
        if (!alive) return false;
        if (age < 10) return false;  // Must be mature
        
        // Probability based on resonance and fitness
        double probability = (this.dna.resonance * 0.01) * (brain.getFitness() + 0.1);
        return random.nextDouble() < probability;
    }

    /**
     * Create offspring with another parent
     */
    public BioNode reproduce(BioNode partner) {
        if (!alive || !partner.alive) return null;
        
        BioNode child = new BioNode(this);
        
        // Mix DNA from partner
        child.dna.frequency = (this.dna.frequency + partner.dna.frequency) / 2;
        child.dna.resonance = Math.max(this.dna.resonance, partner.dna.resonance);
        child.dna.evolutionRate = (this.dna.evolutionRate + partner.dna.evolutionRate) / 2;
        
        // Crossover brains
        child.brain = this.brain.crossover(partner.brain);
        
        return child;
    }

    /**
     * Kill this node
     */
    public void die(String cause) {
        this.alive = false;
    }

    /**
     * Inject energy (mutation stimulus)
     */
    public void injectEnergy() {
        if (!alive) return;
        brain.mutate();
        dna.resonance *= 1.2;  // Boost resonance
    }

    public boolean isAlive() {
        return alive;
    }

    public int getAge() {
        return age;
    }

    public double getFrequency() {
        return dna.frequency;
    }

    public double getResonance() {
        return dna.resonance;
    }

    public double getFitness() {
        return brain.getFitness();
    }

    // ═══════════════════════════════════════════════════════════════════
    // DNA INNER CLASS
    // ═══════════════════════════════════════════════════════════════════

    public static class DNA {
        public double frequency;      // Hz (432-528)
        public double resonance;      // Connection strength
        public double evolutionRate;  // Speed of change

        public DNA(double f, double r, double e) {
            this.frequency = Math.max(BASE_FREQUENCY, Math.min(PEAK_FREQUENCY, f));
            this.resonance = Math.max(0.1, r);
            this.evolutionRate = Math.max(0.01, e);
        }
        
        @Override
        public String toString() {
            return String.format("DNA[%.1fHz, R=%.2f, E=%.3f]", frequency, resonance, evolutionRate);
        }
    }

    @Override
    public String toString() {
        return String.format("BioNode[#%d, Age=%d, %s, Fitness=%.2f, %s]", 
            id, age, alive ? "ALIVE" : "DEAD", brain.getFitness(), dna);
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN (Demo)
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   THE BIO-NODE: A LIVING CELL OF CODE                        ║");
        System.out.println("║   \"Properties: Frequency, Resonance, Evolution.\"             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Create genesis node
        BioNode eve = new BioNode(null);
        System.out.println("   GENESIS: " + eve);
        
        // Simulate 100 ticks
        System.out.println();
        System.out.println("   LIFE CYCLE (100 ticks):");
        
        for (int tick = 0; tick < 100; tick++) {
            eve.update();
            
            if (tick % 20 == 0) {
                System.out.println("   Tick " + tick + ": " + eve);
            }
            
            // Attempt reproduction
            if (eve.readyToReproduce()) {
                BioNode child = new BioNode(eve);
                System.out.println("   → BIRTH: " + child);
            }
        }
        
        System.out.println();
        System.out.println("   FINAL STATE: " + eve);
        System.out.println();
        System.out.println("   ✓ BioNode demo complete.");
        System.out.println();
    }
}
