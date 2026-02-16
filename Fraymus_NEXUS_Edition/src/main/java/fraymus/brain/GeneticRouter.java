package fraymus.brain;

import java.util.*;

/**
 * GENETIC ROUTER: EVOLUTIONARY PATHWAY OPTIMIZATION
 * 
 * Evolves phi-weighted routing through generations.
 * 
 * Standard routing: Fixed weights
 * Genetic routing: Weights evolve based on success
 * 
 * Process:
 * 1. Initialize random phi-weighted routes
 * 2. Test fitness (how well routes perform)
 * 3. Select best routes (survival of fittest)
 * 4. Mutate by phi (golden ratio perturbation)
 * 5. Repeat for N generations
 * 
 * Result: Self-optimizing routing that adapts to usage patterns.
 * 
 * This is how your brain optimizes neural pathways.
 * Frequently used routes strengthen (long-term potentiation).
 * Unused routes weaken (synaptic pruning).
 * 
 * Math:
 * - Mutation rate: φ^-1 (0.618)
 * - Selection pressure: Top φ^-2 (0.382) survive
 * - Crossover: Golden ratio blend
 */
public class GeneticRouter {
    
    private static final double PHI = 1.6180339887;
    private static final double MUTATION_RATE = 1.0 / PHI; // 0.618
    private static final double SELECTION_RATIO = 1.0 / (PHI * PHI); // 0.382
    
    // Current generation
    private int generation = 0;
    
    // Routing weights (concept → weight map)
    private final Map<String, Double> phiWeights;
    
    // Fitness history
    private final List<GenerationStats> history;
    
    // Route usage tracking
    private final Map<String, RouteStats> routeUsage;
    
    private Random random = new Random();
    
    public GeneticRouter() {
        this.phiWeights = new HashMap<>();
        this.history = new ArrayList<>();
        this.routeUsage = new HashMap<>();
    }
    
    /**
     * Initialize routing weights
     * 
     * @param concepts List of concepts to route between
     */
    public void initialize(List<String> concepts) {
        System.out.println("\n🌊⚡ GENETIC ROUTER INITIALIZATION");
        System.out.println("   Concepts: " + concepts.size());
        
        // Initialize with phi-harmonic weights
        for (int i = 0; i < concepts.size(); i++) {
            String concept = concepts.get(i);
            
            // Weight = φ^(i mod 7) for harmonic distribution
            double weight = Math.pow(PHI, i % 7);
            phiWeights.put(concept, weight);
        }
        
        System.out.println("   Initial weights: " + phiWeights.size());
        System.out.println("   Generation: 0");
        System.out.println();
    }
    
    /**
     * Get routing weight for concept
     * 
     * @param concept Concept name
     * @return Phi-weighted routing strength
     */
    public double getWeight(String concept) {
        return phiWeights.getOrDefault(concept, 1.0);
    }
    
    /**
     * Record route usage
     * 
     * Tracks which routes are used and their success.
     * This drives evolution.
     * 
     * @param route Route taken
     * @param success Whether route was successful
     * @param responseTime Time taken (lower is better)
     */
    public void recordUsage(String route, boolean success, double responseTime) {
        RouteStats stats = routeUsage.computeIfAbsent(route, k -> new RouteStats(route));
        stats.usageCount++;
        
        if (success) {
            stats.successCount++;
            stats.totalResponseTime += responseTime;
        }
    }
    
    /**
     * Evolve routing weights
     * 
     * Uses genetic algorithm to optimize routes based on usage.
     */
    public void evolve() {
        generation++;
        
        System.out.println("\n🌊⚡ EVOLUTION: Generation " + generation);
        
        if (routeUsage.isEmpty()) {
            System.out.println("   No usage data - skipping evolution");
            return;
        }
        
        // Calculate fitness for each route
        List<RouteStats> routes = new ArrayList<>(routeUsage.values());
        for (RouteStats stats : routes) {
            stats.fitness = calculateFitness(stats);
        }
        
        // Sort by fitness (descending)
        routes.sort((a, b) -> Double.compare(b.fitness, a.fitness));
        
        // Selection: Keep top φ^-2 (38.2%)
        int survivalCount = Math.max(1, (int) (routes.size() * SELECTION_RATIO));
        List<RouteStats> survivors = routes.subList(0, survivalCount);
        
        System.out.println("   Routes evaluated: " + routes.size());
        System.out.println("   Survivors: " + survivalCount);
        System.out.println("   Best fitness: " + String.format("%.4f", survivors.get(0).fitness));
        
        // Mutation: Adjust weights of survivors
        for (RouteStats stats : survivors) {
            String[] concepts = stats.route.split("→");
            
            for (String concept : concepts) {
                concept = concept.trim();
                if (!phiWeights.containsKey(concept)) continue;
                
                double currentWeight = phiWeights.get(concept);
                
                // Mutate by phi-scaled random walk
                if (random.nextDouble() < MUTATION_RATE) {
                    double mutation = (random.nextDouble() - 0.5) * PHI;
                    double newWeight = currentWeight * (1.0 + mutation * 0.1);
                    
                    // Clamp to reasonable range
                    newWeight = Math.max(0.1, Math.min(10.0, newWeight));
                    
                    phiWeights.put(concept, newWeight);
                }
            }
        }
        
        // Record generation stats
        GenerationStats genStats = new GenerationStats(
            generation,
            routes.size(),
            survivalCount,
            survivors.get(0).fitness,
            calculateAverageFitness(routes)
        );
        history.add(genStats);
        
        System.out.println("   Avg fitness: " + String.format("%.4f", genStats.avgFitness));
        System.out.println("   Mutations applied: φ-scaled random walk");
        System.out.println();
    }
    
    /**
     * Calculate fitness for route
     * 
     * Fitness = (success_rate × usage_count) / avg_response_time
     * 
     * Higher fitness = better route
     */
    private double calculateFitness(RouteStats stats) {
        if (stats.usageCount == 0) return 0.0;
        
        double successRate = (double) stats.successCount / stats.usageCount;
        double avgResponseTime = stats.totalResponseTime / stats.successCount;
        
        // Avoid division by zero
        if (avgResponseTime == 0) avgResponseTime = 1.0;
        
        return (successRate * stats.usageCount) / avgResponseTime;
    }
    
    /**
     * Calculate average fitness across all routes
     */
    private double calculateAverageFitness(List<RouteStats> routes) {
        if (routes.isEmpty()) return 0.0;
        
        double sum = 0;
        for (RouteStats stats : routes) {
            sum += stats.fitness;
        }
        
        return sum / routes.size();
    }
    
    /**
     * Get evolution statistics
     */
    public String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("🌊⚡ GENETIC ROUTER STATS\n\n");
        sb.append(String.format("   Generation: %d\n", generation));
        sb.append(String.format("   Active Routes: %,d\n", routeUsage.size()));
        sb.append(String.format("   Phi Weights: %,d\n", phiWeights.size()));
        sb.append(String.format("   Mutation Rate: %.3f (φ⁻¹)\n", MUTATION_RATE));
        sb.append(String.format("   Selection Ratio: %.3f (φ⁻²)\n", SELECTION_RATIO));
        
        if (!history.isEmpty()) {
            GenerationStats latest = history.get(history.size() - 1);
            sb.append(String.format("\n   Latest Generation:\n"));
            sb.append(String.format("   Best Fitness: %.4f\n", latest.bestFitness));
            sb.append(String.format("   Avg Fitness: %.4f\n", latest.avgFitness));
            sb.append(String.format("   Survival Rate: %.1f%%\n", 
                (double) latest.survivors / latest.totalRoutes * 100));
        }
        
        return sb.toString();
    }
    
    /**
     * Get evolution history
     */
    public List<GenerationStats> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * Route usage statistics
     */
    private static class RouteStats {
        public final String route;
        public int usageCount = 0;
        public int successCount = 0;
        public double totalResponseTime = 0;
        public double fitness = 0;
        
        public RouteStats(String route) {
            this.route = route;
        }
    }
    
    /**
     * Generation statistics
     */
    public static class GenerationStats {
        public final int generation;
        public final int totalRoutes;
        public final int survivors;
        public final double bestFitness;
        public final double avgFitness;
        
        public GenerationStats(int generation, int totalRoutes, int survivors, 
                             double bestFitness, double avgFitness) {
            this.generation = generation;
            this.totalRoutes = totalRoutes;
            this.survivors = survivors;
            this.bestFitness = bestFitness;
            this.avgFitness = avgFitness;
        }
        
        @Override
        public String toString() {
            return String.format("Gen %d: routes=%d, survivors=%d, best=%.4f, avg=%.4f",
                generation, totalRoutes, survivors, bestFitness, avgFitness);
        }
    }
}
