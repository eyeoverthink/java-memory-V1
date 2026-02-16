package com.eyeoverthink.lazarus;

import java.util.Random;

/**
 * THE BIO-NODE
 * "A living cell of code running in RAM."
 */
public class BioNode {

    public LogicCircuit brain;
    public DNA dna;
    private Random random = new Random();

    public BioNode(BioNode parent) {
        if (parent != null) {
            this.dna = new DNA(parent.dna.frequency, parent.dna.resonance, parent.dna.evolutionRate);
            this.brain = parent.brain.crossover(parent.brain);
        } else {
            this.dna = new DNA(432.0 + random.nextDouble() * 20, 0.5 + random.nextDouble(), 0.05);
            this.brain = new LogicCircuit();
        }
    }

    public void update() {
        this.dna.frequency += this.dna.evolutionRate;
        if (this.dna.frequency > 528.0) this.dna.frequency = 432.0; 
    }

    public boolean readyToReproduce() {
        return random.nextDouble() < (this.dna.resonance * 0.01);
    }

    public static class DNA {
        public double frequency;
        public double resonance;
        public double evolutionRate;
        public DNA(double f, double r, double e) {
            this.frequency = f; this.resonance = r; this.evolutionRate = e;
        }
    }
}