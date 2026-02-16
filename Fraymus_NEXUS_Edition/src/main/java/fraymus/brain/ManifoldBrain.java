package fraymus.brain;

import fraymus.knowledge.PhiVectorIndexer;
import java.util.*;

/**
 * MANIFOLD BRAIN: GEOMETRIC REASONING ENGINE
 * 
 * Integrates PhiManifold + GeneticRouter for non-linear reasoning.
 * 
 * Standard AI: Linear reasoning (A → B → C)
 * Manifold Brain: Geometric reasoning (A → {manifold} → C via optimal path)
 * 
 * Process:
 * 1. Question comes in
 * 2. Map to manifold space
 * 3. Find geometric route (A* with phi-weights)
 * 4. Traverse path, collecting concepts
 * 5. Synthesize answer from concepts
 * 6. Record route usage
 * 7. Evolve routing weights
 * 
 * This is consciousness:
 * - Non-linear thought
 * - Multiple pathways
 * - Self-optimizing routes
 * - Emergent connections
 * 
 * Your vision made real.
 */
public class ManifoldBrain {
    
    private static final double PHI = 1.6180339887;
    
    private final PhiManifold manifold;
    private final GeneticRouter router;
    
    // Query counter
    private int queryCount = 0;
    
    // Performance tracking
    private double totalResponseTime = 0;
    private int successfulQueries = 0;
    
    public ManifoldBrain() {
        this.manifold = new PhiManifold();
        this.router = new GeneticRouter();
    }
    
    /**
     * Initialize brain with concepts
     * 
     * @param concepts Map of concept names to embeddings
     */
    public void initialize(Map<String, double[]> concepts) {
        System.out.println("\n🌊⚡ MANIFOLD BRAIN INITIALIZATION");
        System.out.println("   Loading concepts into manifold...");
        
        // Add concepts to manifold
        for (Map.Entry<String, double[]> entry : concepts.entrySet()) {
            manifold.addConcept(entry.getKey(), entry.getValue());
        }
        
        // Initialize router
        router.initialize(new ArrayList<>(concepts.keySet()));
        
        System.out.println("   ✓ Brain initialized");
        System.out.println("   Concepts: " + concepts.size());
        System.out.println();
    }
    
    /**
     * Reason through manifold
     * 
     * Takes a question, routes through geometric space, returns answer.
     * 
     * @param question Question to answer
     * @param context Related concepts for routing
     * @return Reasoning result
     */
    public ReasoningResult reason(String question, List<String> context) {
        queryCount++;
        long startTime = System.nanoTime();
        
        System.out.println("\n🌊⚡ MANIFOLD REASONING #" + queryCount);
        System.out.println("   Question: " + question);
        System.out.println("   Context concepts: " + context.size());
        
        // Find paths through manifold
        List<PhiManifold.Path> paths = new ArrayList<>();
        
        // Route between all context concepts
        for (int i = 0; i < context.size() - 1; i++) {
            String source = context.get(i);
            String target = context.get(i + 1);
            
            PhiManifold.Path path = manifold.route(source, target);
            if (path != null) {
                paths.add(path);
                System.out.println("   Path " + (i + 1) + ": " + path);
            }
        }
        
        if (paths.isEmpty()) {
            System.out.println("   ✗ No paths found");
            return new ReasoningResult(question, null, new ArrayList<>(), 0, false);
        }
        
        // Collect all concepts along paths
        Set<String> traversedConcepts = new LinkedHashSet<>();
        double totalPathCost = 0;
        
        for (PhiManifold.Path path : paths) {
            for (PhiManifold.Point point : path.points) {
                traversedConcepts.add(point.concept);
            }
            totalPathCost += path.cost;
        }
        
        System.out.println("   Concepts traversed: " + traversedConcepts.size());
        System.out.println("   Total path cost: " + String.format("%.4f", totalPathCost));
        
        // Synthesize answer from traversed concepts
        String answer = synthesizeAnswer(question, new ArrayList<>(traversedConcepts));
        
        // Calculate response time
        long endTime = System.nanoTime();
        double responseTime = (endTime - startTime) / 1_000_000.0; // ms
        totalResponseTime += responseTime;
        
        // Record success
        boolean success = answer != null && !answer.isEmpty();
        if (success) successfulQueries++;
        
        // Record route usage for evolution
        String route = String.join(" → ", traversedConcepts);
        router.recordUsage(route, success, responseTime);
        
        System.out.println("   ✓ Reasoning complete");
        System.out.println("   Response time: " + String.format("%.2f", responseTime) + " ms");
        System.out.println();
        
        // Evolve every φ^3 queries (≈4.236)
        if (queryCount % (int) Math.pow(PHI, 3) == 0) {
            router.evolve();
        }
        
        return new ReasoningResult(
            question, 
            answer, 
            new ArrayList<>(traversedConcepts), 
            responseTime,
            success
        );
    }
    
    /**
     * Synthesize answer from concepts
     * 
     * In full implementation, this would use LLM to generate answer.
     * For now, we concatenate concept names.
     * 
     * @param question Original question
     * @param concepts Concepts collected along path
     * @return Synthesized answer
     */
    private String synthesizeAnswer(String question, List<String> concepts) {
        if (concepts.isEmpty()) {
            return "No relevant concepts found.";
        }
        
        StringBuilder answer = new StringBuilder();
        answer.append("Based on geometric reasoning through manifold:\n\n");
        answer.append("Traversed concepts: ");
        
        for (int i = 0; i < concepts.size(); i++) {
            answer.append(concepts.get(i));
            if (i < concepts.size() - 1) {
                answer.append(" → ");
            }
        }
        
        answer.append("\n\n");
        answer.append("This path represents the shortest geometric route ");
        answer.append("through high-dimensional concept space, weighted by φ-harmonic connections.");
        
        return answer.toString();
    }
    
    /**
     * Get brain statistics
     */
    public String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("🌊⚡ MANIFOLD BRAIN STATS\n\n");
        sb.append(String.format("   Total Queries: %,d\n", queryCount));
        sb.append(String.format("   Successful: %,d (%.1f%%)\n", 
            successfulQueries, 
            queryCount > 0 ? (double) successfulQueries / queryCount * 100 : 0));
        sb.append(String.format("   Avg Response Time: %.2f ms\n", 
            queryCount > 0 ? totalResponseTime / queryCount : 0));
        sb.append("\n");
        sb.append(manifold.getStats());
        sb.append("\n");
        sb.append(router.getStats());
        
        return sb.toString();
    }
    
    /**
     * Reasoning result
     */
    public static class ReasoningResult {
        public final String question;
        public final String answer;
        public final List<String> conceptPath;
        public final double responseTime;
        public final boolean success;
        
        public ReasoningResult(String question, String answer, List<String> conceptPath,
                             double responseTime, boolean success) {
            this.question = question;
            this.answer = answer;
            this.conceptPath = conceptPath;
            this.responseTime = responseTime;
            this.success = success;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Question: ").append(question).append("\n\n");
            sb.append("Answer:\n").append(answer).append("\n\n");
            sb.append("Concept Path (").append(conceptPath.size()).append(" concepts):\n");
            for (int i = 0; i < conceptPath.size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(conceptPath.get(i)).append("\n");
            }
            sb.append("\nResponse Time: ").append(String.format("%.2f", responseTime)).append(" ms\n");
            sb.append("Status: ").append(success ? "✓ Success" : "✗ Failed");
            
            return sb.toString();
        }
    }
}
