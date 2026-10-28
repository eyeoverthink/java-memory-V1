package fraymus.transformation;

import java.util.*;

/**
 * 🔬 Cross-Domain Transfer - Transformation Transfer Between Domains
 * 
 * Enables transformations learned in one domain to be applied in another domain.
 * Uses abstraction and pattern matching to transfer knowledge across domains.
 */
public class CrossDomainTransfer {
    
    private boolean initialized;
    
    /**
     * Domain types
     */
    public enum Domain {
        CHESS,           // Board game domain
        ROBOTICS,        // Physical manipulation domain
        NATURAL_LANGUAGE, // Language domain
        CODE,            // Programming domain
        MATHEMATICS,     // Mathematical domain
        LOGIC,           // Logical reasoning domain
        GENERAL          // General purpose
    }
    
    /**
     * Domain mapping - stores transformation mappings between domains
     */
    private Map<Domain, Map<String, UnifiedStateTransformationEngine.Transformation>> domainTransformations;
    
    /**
     * Domain similarity cache
     */
    private Map<DomainPair, Double> similarityCache;
    
    /**
     * Domain pair for caching
     */
    private static class DomainPair {
        Domain domain1;
        Domain domain2;
        
        DomainPair(Domain d1, Domain d2) {
            this.domain1 = d1;
            this.domain2 = d2;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DomainPair)) return false;
            DomainPair other = (DomainPair) obj;
            return (domain1 == other.domain1 && domain2 == other.domain2) ||
                   (domain1 == other.domain2 && domain2 == other.domain1);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(domain1, domain2) + Objects.hash(domain2, domain1);
        }
    }
    
    /**
     * Initialize cross-domain transfer
     */
    public void initialize() {
        domainTransformations = new HashMap<>();
        similarityCache = new HashMap<>();
        
        // Initialize domain maps
        for (Domain domain : Domain.values()) {
            domainTransformations.put(domain, new HashMap<>());
        }
        
        // Load default domain mappings
        loadDefaultDomainMappings();
        
        initialized = true;
        System.out.println("  [Cross-Domain Transfer] Initialized");
    }
    
    /**
     * Load default domain mappings
     */
    private void loadDefaultDomainMappings() {
        // Chess domain - movement transformations
        Map<String, UnifiedStateTransformationEngine.Transformation> chess = 
            domainTransformations.get(Domain.CHESS);
        chess.put("KNIGHT_MOVE", new UnifiedStateTransformationEngine.Transformation(
            "KNIGHT_MOVE",
            new ArrayList<>(Arrays.asList("push", "move_2_straight", "turn", "move_1_perpendicular", "compare")),
            new ArrayList<>()
        ));
        chess.put("BISHOP_MOVE", new UnifiedStateTransformationEngine.Transformation(
            "BISHOP_MOVE",
            new ArrayList<>(Arrays.asList("push", "move_diagonal", "compare")),
            new ArrayList<>()
        ));
        chess.put("ANALYZE", new UnifiedStateTransformationEngine.Transformation(
            "ANALYZE",
            new ArrayList<>(Arrays.asList("push", "evaluate", "branch")),
            new ArrayList<>()
        ));
        
        // Robotics domain - physical transformations
        Map<String, UnifiedStateTransformationEngine.Transformation> robotics = 
            domainTransformations.get(Domain.ROBOTICS);
        robotics.put("REACH", new UnifiedStateTransformationEngine.Transformation(
            "REACH",
            new ArrayList<>(Arrays.asList("push", "extend_arm", "position_hand", "compare")),
            new ArrayList<>()
        ));
        robotics.put("GRASP", new UnifiedStateTransformationEngine.Transformation(
            "GRASP",
            new ArrayList<>(Arrays.asList("push", "close_hand", "apply_pressure", "compare")),
            new ArrayList<>()
        ));
        robotics.put("LIFT", new UnifiedStateTransformationEngine.Transformation(
            "LIFT",
            new ArrayList<>(Arrays.asList("push", "apply_force_upward", "maintain_grip", "branch")),
            new ArrayList<>()
        ));
        
        // Code domain - programming transformations
        Map<String, UnifiedStateTransformationEngine.Transformation> code = 
            domainTransformations.get(Domain.CODE);
        code.put("FUNCTION_CALL", new UnifiedStateTransformationEngine.Transformation(
            "FUNCTION_CALL",
            new ArrayList<>(Arrays.asList("push", "push_arguments", "jump_to_function", "execute", "compare")),
            new ArrayList<>()
        ));
        code.put("LOOP", new UnifiedStateTransformationEngine.Transformation(
            "LOOP",
            new ArrayList<>(Arrays.asList("push", "check_condition", "execute_body", "increment", "branch")),
            new ArrayList<>()
        ));
        
        // Mathematics domain - mathematical transformations
        Map<String, UnifiedStateTransformationEngine.Transformation> math = 
            domainTransformations.get(Domain.MATHEMATICS);
        math.put("CALCULATE", new UnifiedStateTransformationEngine.Transformation(
            "CALCULATE",
            new ArrayList<>(Arrays.asList("push", "compute", "compare", "branch")),
            new ArrayList<>()
        ));
        math.put("PROVE", new UnifiedStateTransformationEngine.Transformation(
            "PROVE",
            new ArrayList<>(Arrays.asList("push", "axiom", "deduce", "compare")),
            new ArrayList<>()
        ));
        
        // Logic domain - logical transformations
        Map<String, UnifiedStateTransformationEngine.Transformation> logic = 
            domainTransformations.get(Domain.LOGIC);
        logic.put("DEDUCE", new UnifiedStateTransformationEngine.Transformation(
            "DEDUCE",
            new ArrayList<>(Arrays.asList("push", "premise", "infer", "compare")),
            new ArrayList<>()
        ));
        logic.put("VALIDATE", new UnifiedStateTransformationEngine.Transformation(
            "VALIDATE",
            new ArrayList<>(Arrays.asList("push", "check", "branch", "compare")),
            new ArrayList<>()
        ));
    }
    
    /**
     * Calculate similarity between two domains
     */
    public double calculateDomainSimilarity(Domain domain1, Domain domain2) {
        if (domain1 == domain2) {
            return 1.0;
        }
        
        DomainPair pair = new DomainPair(domain1, domain2);
        
        // Check cache
        if (similarityCache.containsKey(pair)) {
            return similarityCache.get(pair);
        }
        
        // Calculate similarity based on operation/primitive overlap
        Map<String, UnifiedStateTransformationEngine.Transformation> map1 = 
            domainTransformations.get(domain1);
        Map<String, UnifiedStateTransformationEngine.Transformation> map2 = 
            domainTransformations.get(domain2);
        
        // Collect all operations from both domains
        Set<String> operations1 = new HashSet<>();
        for (UnifiedStateTransformationEngine.Transformation t : map1.values()) {
            operations1.addAll(t.operations);
        }
        
        Set<String> operations2 = new HashSet<>();
        for (UnifiedStateTransformationEngine.Transformation t : map2.values()) {
            operations2.addAll(t.operations);
        }
        
        // Jaccard similarity on operations
        Set<String> intersection = new HashSet<>(operations1);
        intersection.retainAll(operations2);
        
        Set<String> union = new HashSet<>(operations1);
        union.addAll(operations2);
        
        double similarity = union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
        
        // Cache result
        similarityCache.put(pair, similarity);
        
        return similarity;
    }
    
    /**
     * Transfer transformation from source domain to target domain
     */
    public UnifiedStateTransformationEngine.Transformation transferTransformation(
        String transformationName,
        Domain sourceDomain,
        Domain targetDomain
    ) {
        System.out.println("  [Cross-Domain Transfer] Transferring " + transformationName + 
            " from " + sourceDomain + " to " + targetDomain);
        
        // Get source transformation
        UnifiedStateTransformationEngine.Transformation source = 
            domainTransformations.get(sourceDomain).get(transformationName);
        
        if (source == null) {
            System.out.println("  [Cross-Domain Transfer] Source transformation not found");
            return null;
        }
        
        // Calculate domain similarity
        double similarity = calculateDomainSimilarity(sourceDomain, targetDomain);
        System.out.println("  [Cross-Domain Transfer] Domain similarity: " + similarity);
        
        // If domains are identical, return source
        if (similarity == 1.0) {
            return source;
        }
        
        // Abstract source transformation
        List<String> abstractedOperations = abstractOperations(source.operations);
        
        // Instantiate in target domain
        List<String> targetOperations = instantiateOperations(abstractedOperations, targetDomain);
        
        // Create target transformation
        UnifiedStateTransformationEngine.Transformation target = 
            new UnifiedStateTransformationEngine.Transformation(
                transformationName + "_TRANSFERRED",
                targetOperations,
                new ArrayList<>(source.constraints)
            );
        
        // Add to target domain
        domainTransformations.get(targetDomain).put(target.name, target);
        
        System.out.println("  [Cross-Domain Transfer] Transfer complete: " + target.name);
        return target;
    }
    
    /**
     * Abstract operations to domain-independent form
     */
    private List<String> abstractOperations(List<String> operations) {
        List<String> abstracted = new ArrayList<>();
        
        for (String op : operations) {
            // Abstract domain-specific terms
            String abstractedOp = op
                .replace("knight", "agent")
                .replace("bishop", "agent")
                .replace("arm", "actuator")
                .replace("hand", "end_effector")
                .replace("function", "procedure")
                .replace("move", "translate");
            
            abstracted.add(abstractedOp);
        }
        
        return abstracted;
    }
    
    /**
     * Instantiate abstracted operations in target domain
     */
    private List<String> instantiateOperations(List<String> abstracted, Domain targetDomain) {
        List<String> instantiated = new ArrayList<>();
        
        for (String op : abstracted) {
            String targetOp = op;
            
            // Domain-specific instantiation
            switch (targetDomain) {
                case CHESS:
                    targetOp = op.replace("agent", "piece")
                        .replace("actuator", "movement")
                        .replace("end_effector", "position");
                    break;
                case ROBOTICS:
                    targetOp = op.replace("agent", "robot")
                        .replace("actuator", "arm")
                        .replace("end_effector", "hand");
                    break;
                case CODE:
                    targetOp = op.replace("agent", "function")
                        .replace("actuator", "operation")
                        .replace("end_effector", "return");
                    break;
                default:
                    // Keep abstracted
                    break;
            }
            
            instantiated.add(targetOp);
        }
        
        return instantiated;
    }
    
    /**
     * Find similar transformations in target domain
     */
    public List<String> findSimilarTransformations(
        String transformationName,
        Domain sourceDomain,
        Domain targetDomain
    ) {
        List<String> similar = new ArrayList<>();
        
        UnifiedStateTransformationEngine.Transformation source = 
            domainTransformations.get(sourceDomain).get(transformationName);
        
        if (source == null) {
            return similar;
        }
        
        Map<String, UnifiedStateTransformationEngine.Transformation> targetMap = 
            domainTransformations.get(targetDomain);
        
        for (String name : targetMap.keySet()) {
            UnifiedStateTransformationEngine.Transformation target = targetMap.get(name);
            double similarity = calculateTransformationSimilarity(source, target);
            
            if (similarity > 0.5) {  // Threshold
                similar.add(name + " (similarity: " + String.format("%.2f", similarity) + ")");
            }
        }
        
        return similar;
    }
    
    /**
     * Calculate similarity between two transformations
     */
    private double calculateTransformationSimilarity(
        UnifiedStateTransformationEngine.Transformation t1,
        UnifiedStateTransformationEngine.Transformation t2
    ) {
        // Compare operation sets
        Set<String> set1 = new HashSet<>(t1.operations);
        Set<String> set2 = new HashSet<>(t2.operations);
        
        // Jaccard similarity
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }
    
    /**
     * Add transformation to domain
     */
    public void addTransformationToDomain(
        UnifiedStateTransformationEngine.Transformation transformation,
        Domain domain
    ) {
        domainTransformations.get(domain).put(transformation.name, transformation);
        System.out.println("  [Cross-Domain Transfer] Added " + transformation.name + 
            " to " + domain);
    }
    
    /**
     * Get transformations in domain
     */
    public Map<String, UnifiedStateTransformationEngine.Transformation> getTransformations(Domain domain) {
        return new HashMap<>(domainTransformations.get(domain));
    }
    
    /**
     * Get status
     */
    public String getStatus() {
        if (!initialized) {
            return "NOT INITIALIZED";
        }
        
        StringBuilder status = new StringBuilder();
        status.append("READY\n");
        
        for (Domain domain : Domain.values()) {
            status.append("  ").append(domain).append(": ")
                .append(domainTransformations.get(domain).size())
                .append(" transformations\n");
        }
        
        return status.toString();
    }
    
    /**
     * Shutdown
     */
    public void shutdown() {
        domainTransformations.clear();
        similarityCache.clear();
        initialized = false;
        System.out.println("  [Cross-Domain Transfer] Shutdown complete");
    }
}
