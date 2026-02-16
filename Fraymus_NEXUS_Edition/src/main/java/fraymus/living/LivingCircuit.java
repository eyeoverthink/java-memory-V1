package fraymus.living;

/**
 * LIVING CIRCUIT - Autonomous Computing Entity
 * 
 * Self-contained computing unit with DNA and brain.
 * Breathes, evolves, ages, and reproduces.
 */
public class LivingCircuit {
    
    private static final double PHI = 1.618033988749895;
    private static final double BASE_SIZE = 10.0;
    private static final double REPRODUCTION_THRESHOLD = 15.0;
    
    private CircuitDNA dna;
    private LogicBrain brain;
    private int age;
    private double size;
    private double pulse;
    private double time;
    
    public LivingCircuit() {
        this.dna = new CircuitDNA();
        this.brain = new LogicBrain();
        this.age = 0;
        this.size = BASE_SIZE;
        this.pulse = 0;
        this.time = 0;
    }
    
    public LivingCircuit(CircuitDNA dna, LogicBrain brain) {
        this.dna = dna;
        this.brain = brain;
        this.age = 0;
        this.size = BASE_SIZE;
        this.pulse = 0;
        this.time = 0;
    }
    
    /**
     * Update circuit (breathe, evolve, age)
     */
    public void update() {
        // Breathe (phi-harmonic oscillation)
        pulse = Math.sin(dna.getFrequency() * time * 0.01) * dna.getResonance();
        size = BASE_SIZE + pulse;
        
        // Evolve frequency
        dna.evolve();
        
        // Age
        age++;
        time += 0.1;
    }
    
    /**
     * Compute through brain
     */
    public boolean[] compute(boolean[] inputs) {
        return brain.compute(inputs);
    }
    
    /**
     * Check if ready to reproduce
     */
    public boolean readyToReproduce() {
        return size > REPRODUCTION_THRESHOLD;
    }
    
    /**
     * Mitosis (asexual reproduction)
     */
    public LivingCircuit reproduce() {
        LogicBrain childBrain = brain.clone();
        childBrain.mutate();
        
        CircuitDNA childDNA = dna.clone();
        childDNA.mutate();
        
        return new LivingCircuit(childDNA, childBrain);
    }
    
    /**
     * Sexual reproduction
     */
    public LivingCircuit reproduce(LivingCircuit partner) {
        LogicBrain childBrain = brain.crossover(partner.brain);
        CircuitDNA childDNA = dna.crossover(partner.dna);
        
        return new LivingCircuit(childDNA, childBrain);
    }
    
    /**
     * Fitness score (for selection)
     */
    public double getFitness() {
        return dna.getFrequency() * dna.getResonance() * PHI;
    }
    
    public CircuitDNA getDNA() {
        return dna;
    }
    
    public LogicBrain getBrain() {
        return brain;
    }
    
    public int getAge() {
        return age;
    }
    
    public double getSize() {
        return size;
    }
    
    @Override
    public String toString() {
        return String.format("Circuit[age=%d, size=%.2f, freq=%.2f Hz, fitness=%.2f]", 
            age, size, dna.getFrequency(), getFitness());
    }
}

/**
 * CIRCUIT DNA - Biological Parameters
 */
class CircuitDNA {
    
    private static final double MIN_FREQ = 432.0;
    private static final double MAX_FREQ = 528.0;
    private static final double EVOLUTION_RATE = 0.05;
    
    private double frequency;
    private double resonance;
    private double evolution;
    
    public CircuitDNA() {
        this.frequency = MIN_FREQ + Math.random() * (MAX_FREQ - MIN_FREQ);
        this.resonance = 0.5 + Math.random() * 0.5;
        this.evolution = EVOLUTION_RATE;
    }
    
    public CircuitDNA(double frequency, double resonance, double evolution) {
        this.frequency = frequency;
        this.resonance = resonance;
        this.evolution = evolution;
    }
    
    /**
     * Evolve frequency
     */
    public void evolve() {
        frequency += evolution;
        if (frequency > MAX_FREQ) {
            frequency = MIN_FREQ;
        }
    }
    
    /**
     * Mutate DNA
     */
    public void mutate() {
        if (Math.random() < 0.1) {
            frequency += (Math.random() - 0.5) * 10;
            frequency = Math.max(MIN_FREQ, Math.min(MAX_FREQ, frequency));
        }
        if (Math.random() < 0.1) {
            resonance += (Math.random() - 0.5) * 0.2;
            resonance = Math.max(0.1, Math.min(1.0, resonance));
        }
    }
    
    /**
     * Sexual reproduction
     */
    public CircuitDNA crossover(CircuitDNA partner) {
        double childFreq = (this.frequency + partner.frequency) / 2.0;
        double childRes = (this.resonance + partner.resonance) / 2.0;
        double childEvo = (this.evolution + partner.evolution) / 2.0;
        
        CircuitDNA child = new CircuitDNA(childFreq, childRes, childEvo);
        child.mutate();
        return child;
    }
    
    /**
     * Clone DNA
     */
    public CircuitDNA clone() {
        return new CircuitDNA(frequency, resonance, evolution);
    }
    
    public double getFrequency() {
        return frequency;
    }
    
    public double getResonance() {
        return resonance;
    }
    
    public double getEvolution() {
        return evolution;
    }
}
