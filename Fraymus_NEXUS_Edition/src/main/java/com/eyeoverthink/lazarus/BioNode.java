package com.eyeoverthink.lazarus;

import java.util.Random;

/**
 * THE BIO-NODE
 * 
 * "A living cell of code."
 * 
 * Based on 'dr_frank.html' Node class.
 * 
 * Properties:
 * - Frequency (Hz): Harmonic frequency (432-528 Hz range)
 * - Resonance: Vibrational strength
 * - Evolution Rate: Speed of adaptation
 * 
 * Each node has a LogicCircuit (brain) that evolves over time.
 */
public class BioNode {

    public double x, y;             // Virtual coordinates (for potential UI visualization)
    public LogicCircuit brain;      // The evolving neural network
    public DNA dna;                 // Genetic code
    
    private Random random = new Random();

    /**
     * Constructor
     * 
     * @param parent If null, creates genesis node. Otherwise inherits from parent.
     */
    public BioNode(BioNode parent) {
        if (parent != null) {
            // Inheritance
            this.dna = new DNA(
                parent.dna.frequency, 
                parent.dna.resonance, 
                parent.dna.evolutionRate
            );
            
            // Evolve the brain through crossover
            this.brain = parent.brain.crossover(parent.brain);
            
        } else {
            // Genesis
            this.dna = new DNA(
                432.0 + random.nextDouble() * 20,  // Start near 432Hz
                0.5 + random.nextDouble(),          // Random resonance
                0.05                                 // Base evolution rate
            );
            this.brain = new LogicCircuit();
        }
        
        // Random position
        this.x = random.nextDouble() * 100;
        this.y = random.nextDouble() * 100;
    }

    /**
     * UPDATE - Called every tick
     * 
     * Evolves the node's frequency and checks for cycle reset.
     */
    public void update() {
        // 1. EVOLVE FREQUENCY
        // "node.nodularDNA.harmonicFrequency += evolution;"
        this.dna.frequency += this.dna.evolutionRate;

        // 2. LIMITER (The 528Hz Cap)
        // When frequency exceeds 528Hz, reset to 432Hz (cycle)
        if (this.dna.frequency > 528.0) {
            this.dna.frequency = 432.0; // Reset cycle
        }
    }

    /**
     * Check if node is ready to reproduce
     * 
     * Logic: High resonance = High fertility
     */
    public boolean readyToReproduce() {
        return random.nextDouble() < (this.dna.resonance * 0.01);
    }

    /**
     * The Genetic Code
     */
    public static class DNA {
        public double frequency;        // Harmonic frequency (Hz)
        public double resonance;        // Vibrational strength
        public double evolutionRate;    // Speed of adaptation

        public DNA(double f, double r, double e) {
            this.frequency = f;
            this.resonance = r;
            this.evolutionRate = e;
        }
    }
    
    @Override
    public String toString() {
        return String.format("BioNode[Freq=%.2fHz, Res=%.2f, Brain=%s]", 
            dna.frequency, dna.resonance, brain);
    }
}
