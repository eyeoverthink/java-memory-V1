package fraymus.baselines;

import fraymus.run.*;

/**
 * Greedy Hill Climbing Baseline
 * 
 * Local search: start random, make small changes, keep improvements
 * Good for establishing mid-tier performance benchmark
 */
public class GreedyBaseline implements Baseline {
    
    @Override
    public String name() {
        return "GreedyHillClimb";
    }
    
    @Override
    public EngineResult run(RunContext ctx, EngineBudget budget) throws Exception {
        long startTime = System.currentTimeMillis();
        
        // Start with random solution
        Solution current = randomSolution(ctx);
        double currentFitness = evaluate(current);
        
        int improvements = 0;
        
        // Hill climbing within budget
        for (int i = 0; i < budget.evaluations; i++) {
            // Generate neighbor (small mutation)
            Solution neighbor = mutate(current, ctx);
            double neighborFitness = evaluate(neighbor);
            
            // Greedy: keep if better
            if (neighborFitness > currentFitness) {
                current = neighbor;
                currentFitness = neighborFitness;
                improvements++;
            }
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        return EngineResult.builder(name())
            .seed(ctx.cfg.seed)
            .fitnessScore(currentFitness)
            .iterations(budget.evaluations)
            .runtimeMs(elapsed)
            .noveltyCount(improvements)
            .metric("improvements", improvements)
            .build();
    }
    
    static class Solution {
        double[] params;
        
        Solution(int size) {
            this.params = new double[size];
        }
        
        Solution copy() {
            Solution s = new Solution(params.length);
            System.arraycopy(params, 0, s.params, 0, params.length);
            return s;
        }
    }
    
    private Solution randomSolution(RunContext ctx) {
        Solution s = new Solution(10);
        for (int i = 0; i < s.params.length; i++) {
            s.params[i] = ctx.nextDouble();
        }
        return s;
    }
    
    private Solution mutate(Solution s, RunContext ctx) {
        Solution mutated = s.copy();
        
        // Mutate 1-3 parameters slightly
        int numMutations = 1 + ctx.nextInt(3);
        for (int i = 0; i < numMutations; i++) {
            int idx = ctx.nextInt(mutated.params.length);
            double delta = ctx.nextGaussian() * 0.1;  // Small change
            mutated.params[idx] = Math.max(0, Math.min(1, mutated.params[idx] + delta));
        }
        
        return mutated;
    }
    
    private double evaluate(Solution s) {
        // Simple fitness: weighted sum
        double fitness = 0;
        for (int i = 0; i < s.params.length; i++) {
            fitness += s.params[i] * (1.0 / (i + 1));
        }
        return fitness / s.params.length;
    }
}
