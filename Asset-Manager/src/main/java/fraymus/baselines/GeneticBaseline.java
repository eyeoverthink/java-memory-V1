package fraymus.baselines;

import fraymus.run.*;
import java.util.*;

/**
 * Simple Genetic Algorithm Baseline
 * 
 * Population-based search with selection, crossover, mutation
 * Establishes upper baseline performance
 */
public class GeneticBaseline implements Baseline {
    
    @Override
    public String name() {
        return "GeneticAlgorithm";
    }
    
    @Override
    public EngineResult run(RunContext ctx, EngineBudget budget) throws Exception {
        long startTime = System.currentTimeMillis();
        
        int populationSize = budget.population;
        int generations = budget.steps;
        
        // Initialize population
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(randomIndividual(ctx));
        }
        
        // Evaluate initial population
        for (Individual ind : population) {
            ind.fitness = evaluate(ind);
        }
        
        double bestFitness = Double.NEGATIVE_INFINITY;
        int noveltyCount = 0;
        
        // Evolution loop
        for (int gen = 0; gen < generations; gen++) {
            // Sort by fitness
            population.sort((a, b) -> Double.compare(b.fitness, a.fitness));
            
            // Track best
            if (population.get(0).fitness > bestFitness) {
                bestFitness = population.get(0).fitness;
                noveltyCount++;
            }
            
            // Create next generation
            List<Individual> nextGen = new ArrayList<>();
            
            // Elitism: keep top 10%
            int eliteCount = populationSize / 10;
            for (int i = 0; i < eliteCount; i++) {
                nextGen.add(population.get(i).copy());
            }
            
            // Fill rest with offspring
            while (nextGen.size() < populationSize) {
                // Tournament selection
                Individual parent1 = tournamentSelect(population, ctx);
                Individual parent2 = tournamentSelect(population, ctx);
                
                // Crossover
                Individual child = crossover(parent1, parent2, ctx);
                
                // Mutation
                if (ctx.nextDouble() < 0.1) {
                    mutate(child, ctx);
                }
                
                // Evaluate
                child.fitness = evaluate(child);
                nextGen.add(child);
            }
            
            population = nextGen;
        }
        
        // Final best
        population.sort((a, b) -> Double.compare(b.fitness, a.fitness));
        bestFitness = population.get(0).fitness;
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        return EngineResult.builder(name())
            .seed(ctx.cfg.seed)
            .fitnessScore(bestFitness)
            .iterations(generations)
            .runtimeMs(elapsed)
            .noveltyCount(noveltyCount)
            .metric("population_size", populationSize)
            .metric("generations", generations)
            .build();
    }
    
    static class Individual {
        double[] genes;
        double fitness;
        
        Individual(int size) {
            this.genes = new double[size];
        }
        
        Individual copy() {
            Individual ind = new Individual(genes.length);
            System.arraycopy(genes, 0, ind.genes, 0, genes.length);
            ind.fitness = this.fitness;
            return ind;
        }
    }
    
    private Individual randomIndividual(RunContext ctx) {
        Individual ind = new Individual(10);
        for (int i = 0; i < ind.genes.length; i++) {
            ind.genes[i] = ctx.nextDouble();
        }
        return ind;
    }
    
    private Individual tournamentSelect(List<Individual> population, RunContext ctx) {
        int tournamentSize = 3;
        Individual best = null;
        
        for (int i = 0; i < tournamentSize; i++) {
            Individual candidate = population.get(ctx.nextInt(population.size()));
            if (best == null || candidate.fitness > best.fitness) {
                best = candidate;
            }
        }
        
        return best;
    }
    
    private Individual crossover(Individual p1, Individual p2, RunContext ctx) {
        Individual child = new Individual(p1.genes.length);
        
        // Uniform crossover
        for (int i = 0; i < child.genes.length; i++) {
            child.genes[i] = ctx.nextBoolean() ? p1.genes[i] : p2.genes[i];
        }
        
        return child;
    }
    
    private void mutate(Individual ind, RunContext ctx) {
        // Mutate 1-2 genes
        int numMutations = 1 + ctx.nextInt(2);
        for (int i = 0; i < numMutations; i++) {
            int idx = ctx.nextInt(ind.genes.length);
            ind.genes[idx] = ctx.nextDouble();
        }
    }
    
    private double evaluate(Individual ind) {
        // Simple fitness: weighted sum
        double fitness = 0;
        for (int i = 0; i < ind.genes.length; i++) {
            fitness += ind.genes[i] * (1.0 / (i + 1));
        }
        return fitness / ind.genes.length;
    }
}
