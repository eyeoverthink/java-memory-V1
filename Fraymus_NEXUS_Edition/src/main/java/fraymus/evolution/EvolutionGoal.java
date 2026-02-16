package fraymus.evolution;

import fraymus.hardware.LogicBlock;
import java.util.List;

/**
 * EVOLUTION GOAL
 * Defines what the system should evolve towards.
 * 
 * Goals range from simple (XOR gate) to complex (64-bit CPU).
 * Each goal has test cases and a fitness function.
 */
public abstract class EvolutionGoal {
    
    protected String name;
    protected String description;
    protected int complexity; // 1-100 scale
    protected int maxGenerations;
    
    public EvolutionGoal(String name, String description, int complexity) {
        this.name = name;
        this.description = description;
        this.complexity = complexity;
        this.maxGenerations = complexity * 1000; // Scale with complexity
    }
    
    /**
     * Calculate fitness score (0-100)
     * Higher = closer to goal
     */
    public abstract int calculateFitness(List<LogicBlock> circuit);
    
    /**
     * Is the goal achieved?
     */
    public abstract boolean isSolved(List<LogicBlock> circuit);
    
    /**
     * Get human-readable status
     */
    public abstract String getStatus(List<LogicBlock> circuit);
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getComplexity() { return complexity; }
    public int getMaxGenerations() { return maxGenerations; }
    
    @Override
    public String toString() {
        return String.format("Goal[%s] Complexity=%d/100", name, complexity);
    }
}
