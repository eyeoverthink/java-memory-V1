package fraymus.baselines;

import fraymus.run.*;

/**
 * Random Search Baseline
 * 
 * Simplest possible optimization: random sampling
 * Useful for establishing lower bound on performance
 */
public class RandomSearchBaseline implements Baseline {
    
    @Override
    public String name() {
        return "RandomSearch";
    }
    
    @Override
    public EngineResult run(RunContext ctx, EngineBudget budget) throws Exception {
        long startTime = System.currentTimeMillis();
        
        double bestFitness = Double.NEGATIVE_INFINITY;
        int bestIteration = 0;
        
        // Random sampling within budget
        for (int i = 0; i < budget.evaluations; i++) {
            // Generate random solution
            double fitness = evaluateRandomSolution(ctx);
            
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestIteration = i;
            }
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        return EngineResult.builder(name())
            .seed(ctx.cfg.seed)
            .fitnessScore(bestFitness)
            .iterations(budget.evaluations)
            .runtimeMs(elapsed)
            .noveltyCount(0)  // Random search doesn't create novelty
            .metric("best_iteration", bestIteration)
            .build();
    }
    
    private double evaluateRandomSolution(RunContext ctx) {
        // Generate random parameters
        double param1 = ctx.nextDouble();
        double param2 = ctx.nextDouble();
        double param3 = ctx.nextDouble();
        
        // Simple fitness function (can be overridden per problem)
        return param1 * 0.5 + param2 * 0.3 + param3 * 0.2;
    }
}
