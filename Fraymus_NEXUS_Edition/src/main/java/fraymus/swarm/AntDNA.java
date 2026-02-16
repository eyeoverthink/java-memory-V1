package fraymus.swarm;

import java.util.Random;

/**
 * THE GENETIC CODE
 * "Determines the potential of a Digital Ant."
 * 
 * Every ant has three core attributes:
 * - LOGIC: CPU power (ability to solve complex problems)
 * - MEMORY: RAM capacity (ability to hold data)
 * - SPEED: Bus speed (how fast it processes)
 * 
 * EVOLUTION:
 * When an ant spawns a partner due to stress, the child inherits
 * the parent's DNA but with mutations based on the specific need.
 * 
 * STRESS-BASED MUTATION:
 * - Need LOGIC → +50% logic power
 * - Need MEMORY → +50% memory capacity
 * - Need SPEED → +50% processing speed
 * - Random spark → +10% to random attribute
 * 
 * "Children are stronger than their parents."
 */
public class AntDNA {
    public double logic;    // CPU Power (ability to solve problems)
    public double memory;   // RAM Capacity (ability to hold data)
    public double speed;    // Bus Speed (processing rate)
    
    private Random r = new Random();

    /**
     * Genesis DNA (Base Stats)
     * Used for Adam & Eve - the first ants
     */
    public AntDNA() {
        this.logic = 1.0;
        this.memory = 1.0;
        this.speed = 1.0;
    }

    /**
     * Evolutionary DNA (Child is stronger than Parent)
     * 
     * @param parent Parent's DNA to inherit from
     * @param need What the parent needs help with ("LOGIC", "MEMORY", "SPEED")
     */
    public AntDNA(AntDNA parent, String need) {
        // Inherit parent's base stats
        this.logic = parent.logic;
        this.memory = parent.memory;
        this.speed = parent.speed;

        // STRESS-BASED MUTATION
        // "If the parent can spawn, the help it needs is stronger."
        switch (need) {
            case "LOGIC":
                this.logic *= 1.5; // +50% Logic (Math Giant)
                break;
            case "MEMORY":
                this.memory *= 1.5; // +50% Memory (Data Holder)
                break;
            case "SPEED":
                this.speed *= 1.5; // +50% Speed (Fast Processor)
                break;
        }
        
        // Random Mutation (The Spark of Evolution)
        // 50% chance to gain a small bonus to a random attribute
        if (r.nextBoolean()) {
            switch (r.nextInt(3)) {
                case 0: this.logic += 0.1; break;
                case 1: this.memory += 0.1; break;
                case 2: this.speed += 0.1; break;
            }
        }
    }
    
    /**
     * Clone DNA (for reproduction without mutation)
     */
    public AntDNA clone() {
        AntDNA copy = new AntDNA();
        copy.logic = this.logic;
        copy.memory = this.memory;
        copy.speed = this.speed;
        return copy;
    }
    
    /**
     * Calculate overall fitness
     */
    public double getFitness() {
        return logic + memory + speed;
    }
    
    /**
     * Get specialization type
     */
    public String getSpecialization() {
        if (logic > memory && logic > speed) return "LOGIC_SPECIALIST";
        if (memory > logic && memory > speed) return "MEMORY_SPECIALIST";
        if (speed > logic && speed > memory) return "SPEED_SPECIALIST";
        return "GENERALIST";
    }
    
    @Override
    public String toString() {
        return String.format("[L:%.1f M:%.1f S:%.1f]", logic, memory, speed);
    }
}
