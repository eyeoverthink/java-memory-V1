package fraymus.optimization;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Particle Swarm Optimization (PSO) for Layer 5
 * 
 * Refactors Layer 5 to utilize PSO for distributed swarm intelligence
 * optimization across the cognitive architecture.
 * 
 * Phase 4.2: Particle Swarm Optimization Implementation
 * 
 * Key capabilities:
 * - Multi-dimensional optimization space
 * - Swarm intelligence with velocity and position updates
 * - Global best (gbest) and local best (pbest) tracking
 * - Adaptive inertia weight for convergence
 * - Constraint handling for bounded optimization
 * 
 * Mathematical foundation:
 * - Velocity update: v[i] = w*v[i] + c1*r1*(pbest[i] - x[i]) + c2*r2*(gbest - x[i])
 * - Position update: x[i] = x[i] + v[i]
 * - Inertia weight: w = w_max - (w_max - w_min) * iteration / max_iterations
 * 
 * Parameters:
 * - w: inertia weight [0.4, 0.9]
 * - c1: cognitive coefficient [1.5, 2.0]
 * - c2: social coefficient [1.5, 2.0]
 */
public class ParticleSwarmOptimizer {
    
    // PSO parameters
    private double inertiaWeight = 0.7;
    private double cognitiveCoefficient = 1.5;
    private double socialCoefficient = 1.5;
    private double maxInertia = 0.9;
    private double minInertia = 0.4;
    
    // Swarm configuration
    private int swarmSize = 50;
    private int maxIterations = 1000;
    private int dimension;
    
    // Bounds for search space
    private double[] lowerBounds;
    private double[] upperBounds;
    
    // Objective function
    private Function<double[], Double> objectiveFunction;
    
    // Swarm particles
    private List<Particle> swarm;
    
    // Global best
    private double[] globalBestPosition;
    private double globalBestFitness;
    
    // Optimization status
    private volatile boolean running = false;
    private volatile boolean converged = false;
    private int currentIteration;
    
    // Statistics
    private final List<IterationStatistics> statisticsHistory;
    
    public ParticleSwarmOptimizer(int dimension) {
        this.dimension = dimension;
        this.lowerBounds = new double[dimension];
        this.upperBounds = new double[dimension];
        Arrays.fill(lowerBounds, -1.0);
        Arrays.fill(upperBounds, 1.0);
        this.statisticsHistory = new ArrayList<>();
    }
    
    /**
     * Set objective function to optimize.
     */
    public void setObjectiveFunction(Function<double[], Double> objectiveFunction) {
        this.objectiveFunction = objectiveFunction;
    }
    
    /**
     * Set search space bounds.
     */
    public void setBounds(double[] lowerBounds, double[] upperBounds) {
        if (lowerBounds.length != dimension || upperBounds.length != dimension) {
            throw new IllegalArgumentException("Bounds must match dimension");
        }
        this.lowerBounds = lowerBounds.clone();
        this.upperBounds = upperBounds.clone();
    }
    
    /**
     * Set PSO parameters.
     */
    public void setParameters(double inertiaWeight, double cognitiveCoefficient, 
                            double socialCoefficient) {
        this.inertiaWeight = inertiaWeight;
        this.cognitiveCoefficient = cognitiveCoefficient;
        this.socialCoefficient = socialCoefficient;
    }
    
    /**
     * Set swarm size.
     */
    public void setSwarmSize(int swarmSize) {
        this.swarmSize = swarmSize;
    }
    
    /**
     * Set maximum iterations.
     */
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
    
    /**
     * Initialize swarm with random positions.
     */
    private void initializeSwarm() {
        swarm = new ArrayList<>();
        globalBestFitness = Double.MAX_VALUE;
        globalBestPosition = new double[dimension];
        
        for (int i = 0; i < swarmSize; i++) {
            Particle particle = new Particle(dimension);
            
            // Random position within bounds
            for (int d = 0; d < dimension; d++) {
                particle.position[d] = lowerBounds[d] + 
                    Math.random() * (upperBounds[d] - lowerBounds[d]);
                particle.velocity[d] = (Math.random() - 0.5) * 0.1;
            }
            
            // Evaluate fitness
            particle.fitness = evaluateFitness(particle.position);
            particle.personalBestFitness = particle.fitness;
            System.arraycopy(particle.position, 0, particle.personalBestPosition, 0, dimension);
            
            // Update global best
            if (particle.fitness < globalBestFitness) {
                globalBestFitness = particle.fitness;
                System.arraycopy(particle.position, 0, globalBestPosition, 0, dimension);
            }
            
            swarm.add(particle);
        }
    }
    
    /**
     * Evaluate fitness of a position.
     */
    private double evaluateFitness(double[] position) {
        if (objectiveFunction == null) {
            throw new IllegalStateException("Objective function not set");
        }
        return objectiveFunction.apply(position);
    }
    
    /**
     * Update inertia weight (linear decrease).
     */
    private void updateInertiaWeight() {
        inertiaWeight = maxInertia - (maxInertia - minInertia) * 
            currentIteration / maxIterations;
    }
    
    /**
     * Update particle velocities and positions.
     */
    private void updateParticles() {
        for (Particle particle : swarm) {
            for (int d = 0; d < dimension; d++) {
                double r1 = Math.random();
                double r2 = Math.random();
                
                // Velocity update
                particle.velocity[d] = inertiaWeight * particle.velocity[d] +
                    cognitiveCoefficient * r1 * (particle.personalBestPosition[d] - particle.position[d]) +
                    socialCoefficient * r2 * (globalBestPosition[d] - particle.position[d]);
                
                // Clamp velocity
                double maxVelocity = (upperBounds[d] - lowerBounds[d]) * 0.2;
                particle.velocity[d] = Math.max(-maxVelocity, Math.min(maxVelocity, particle.velocity[d]));
                
                // Position update
                particle.position[d] += particle.velocity[d];
                
                // Clamp to bounds
                particle.position[d] = Math.max(lowerBounds[d], Math.min(upperBounds[d], particle.position[d]));
            }
            
            // Evaluate new fitness
            particle.fitness = evaluateFitness(particle.position);
            
            // Update personal best
            if (particle.fitness < particle.personalBestFitness) {
                particle.personalBestFitness = particle.fitness;
                System.arraycopy(particle.position, 0, particle.personalBestPosition, 0, dimension);
                
                // Update global best
                if (particle.fitness < globalBestFitness) {
                    globalBestFitness = particle.fitness;
                    System.arraycopy(particle.position, 0, globalBestPosition, 0, dimension);
                }
            }
        }
    }
    
    /**
     * Check convergence.
     */
    private boolean checkConvergence() {
        if (currentIteration < 10) return false;
        
        // Check if global best hasn't improved significantly
        double improvementThreshold = 1e-6;
        
        if (statisticsHistory.size() >= 10) {
            double recentBest = statisticsHistory.get(statisticsHistory.size() - 1).globalBestFitness;
            double oldBest = statisticsHistory.get(statisticsHistory.size() - 10).globalBestFitness;
            
            if (Math.abs(recentBest - oldBest) < improvementThreshold) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Record iteration statistics.
     */
    private void recordStatistics() {
        double avgFitness = swarm.stream()
            .mapToDouble(p -> p.fitness)
            .average()
            .orElse(0.0);
        
        double stdDev = Math.sqrt(swarm.stream()
            .mapToDouble(p -> (p.fitness - avgFitness) * (p.fitness - avgFitness))
            .average()
            .orElse(0.0));
        
        IterationStatistics stats = new IterationStatistics(
            currentIteration,
            globalBestFitness,
            avgFitness,
            stdDev,
            inertiaWeight
        );
        
        statisticsHistory.add(stats);
    }
    
    /**
     * Run PSO optimization.
     */
    public OptimizationResult optimize() {
        if (objectiveFunction == null) {
            throw new IllegalStateException("Objective function not set");
        }
        
        running = true;
        converged = false;
        currentIteration = 0;
        statisticsHistory.clear();
        
        // Initialize swarm
        initializeSwarm();
        
        long startTime = System.currentTimeMillis();
        
        // Main optimization loop
        while (running && currentIteration < maxIterations && !converged) {
            currentIteration++;
            
            // Update inertia weight
            updateInertiaWeight();
            
            // Update particles
            updateParticles();
            
            // Record statistics
            recordStatistics();
            
            // Check convergence
            converged = checkConvergence();
        }
        
        long endTime = System.currentTimeMillis();
        
        running = false;
        
        return new OptimizationResult(
            globalBestPosition.clone(),
            globalBestFitness,
            currentIteration,
            converged,
            endTime - startTime,
            new ArrayList<>(statisticsHistory)
        );
    }
    
    /**
     * Stop optimization.
     */
    public void stop() {
        running = false;
    }
    
    /**
     * Get current global best.
     */
    public double[] getGlobalBestPosition() {
        return globalBestPosition.clone();
    }
    
    /**
     * Get current global best fitness.
     */
    public double getGlobalBestFitness() {
        return globalBestFitness;
    }
    
    /**
     * Get statistics history.
     */
    public List<IterationStatistics> getStatisticsHistory() {
        return new ArrayList<>(statisticsHistory);
    }
    
    /**
     * Particle class
     */
    private static class Particle {
        public double[] position;
        public double[] velocity;
        public double fitness;
        public double[] personalBestPosition;
        public double personalBestFitness;
        
        public Particle(int dimension) {
            this.position = new double[dimension];
            this.velocity = new double[dimension];
            this.personalBestPosition = new double[dimension];
            this.fitness = Double.MAX_VALUE;
            this.personalBestFitness = Double.MAX_VALUE;
        }
    }
    
    /**
     * Iteration Statistics class
     */
    public static class IterationStatistics {
        public final int iteration;
        public final double globalBestFitness;
        public final double averageFitness;
        public final double fitnessStdDev;
        public final double inertiaWeight;
        
        public IterationStatistics(int iteration, double globalBestFitness,
                                  double averageFitness, double fitnessStdDev,
                                  double inertiaWeight) {
            this.iteration = iteration;
            this.globalBestFitness = globalBestFitness;
            this.averageFitness = averageFitness;
            this.fitnessStdDev = fitnessStdDev;
            this.inertiaWeight = inertiaWeight;
        }
    }
    
    /**
     * Optimization Result class
     */
    public static class OptimizationResult {
        public final double[] bestPosition;
        public final double bestFitness;
        public final int iterations;
        public final boolean converged;
        public final long executionTime;
        public final List<IterationStatistics> statistics;
        
        public OptimizationResult(double[] bestPosition, double bestFitness,
                                int iterations, boolean converged,
                                long executionTime, List<IterationStatistics> statistics) {
            this.bestPosition = bestPosition;
            this.bestFitness = bestFitness;
            this.iterations = iterations;
            this.converged = converged;
            this.executionTime = executionTime;
            this.statistics = statistics;
        }
        
        @Override
        public String toString() {
            return String.format(
                "OptimizationResult[fitness=%.6f, iterations=%d, converged=%s, time=%d ms]",
                bestFitness, iterations, converged, executionTime
            );
        }
    }
}
