package fraymus.causal;

import java.util.*;

/**
 * DoCalculusEngine - Implements do-calculus operations for causal reasoning
 * 
 * Implements the three rules of do-calculus:
 * Rule 1: Ignoring observations - P(y | do(x), z, w) = P(y | do(x), w) if (Y ⊥ Z | X, W)
 * Rule 2: Action/Observation exchange - P(y | do(x), do(z), w) = P(y | do(x), z, w) if (Y ⊥ Z | X, W)
 * Rule 3: Ignoring actions - P(y | do(x), do(z), w) = P(y | do(x), w) if (Y ⊥ Z | X, W) in modified graph
 * 
 * Mathematical foundation:
 * - do-operator: do(X = x) represents intervention
 * - Causal effect: P(Y | do(X)) vs conditional probability P(Y | X)
 * - Identification: expressing do-probabilities in terms of observational probabilities
 * 
 * Phase 1.2: DAG-guided Do-Calculus Integration
 */
public class DoCalculusEngine {
    
    private final CausalModel model;
    private final DAGVerifier verifier;
    
    public DoCalculusEngine(CausalModel model) {
        this.model = model;
        this.verifier = new DAGVerifier(model);
    }
    
    /**
     * Causal query result
     */
    public static class CausalQueryResult {
        private final String query;
        private final double probability;
        private final boolean identifiable;
        private final String method;
        private final List<String> explanation;
        
        public CausalQueryResult(String query, double probability, boolean identifiable, 
                               String method, List<String> explanation) {
            this.query = query;
            this.probability = probability;
            this.identifiable = identifiable;
            this.method = method;
            this.explanation = explanation;
        }
        
        public String getQuery() { return query; }
        public double getProbability() { return probability; }
        public boolean isIdentifiable() { return identifiable; }
        public String getMethod() { return method; }
        public List<String> getExplanation() { return explanation; }
    }
    
    /**
     * Apply Rule 1: Ignoring observations
     * P(y | do(x), z, w) = P(y | do(x), w) if (Y ⊥ Z | X, W)
     */
    public CausalQueryResult applyRule1(String y, String x, Set<String> z, Set<String> w) {
        // Check if Y is d-separated from each variable in Z given X and W
        Set<String> conditioning = new HashSet<>();
        conditioning.add(x);
        conditioning.addAll(w);
        
        boolean dSeparated = true;
        for (String zVar : z) {
            if (!verifier.isDSeparated(y, zVar, conditioning)) {
                dSeparated = false;
                break;
            }
        }
        
        List<String> explanation = new ArrayList<>();
        explanation.add("Rule 1: Ignoring observations");
        explanation.add(String.format("Check if %s ⊥ %s | {%s, %s}", y, String.join(", ", z), x, String.join(", ", w)));
        explanation.add(String.format("d-separated: %s", dSeparated));
        
        if (dSeparated) {
            explanation.add("Result: Can ignore Z, reduce to P(Y | do(X), W)");
            return new CausalQueryResult(
                String.format("P(%s | do(%s), %s, %s)", y, x, String.join(", ", z), String.join(", ", w)),
                0.0, // Would need actual probability computation
                true,
                "Rule 1 simplification",
                explanation
            );
        } else {
            explanation.add("Result: Cannot apply Rule 1, Z is not d-separated");
            return new CausalQueryResult(
                String.format("P(%s | do(%s), %s, %s)", y, x, String.join(", ", z), String.join(", ", w)),
                0.0,
                false,
                "Rule 1 not applicable",
                explanation
            );
        }
    }
    
    /**
     * Apply Rule 2: Action/Observation exchange
     * P(y | do(x), do(z), w) = P(y | do(x), z, w) if (Y ⊥ Z | X, W)
     */
    public CausalQueryResult applyRule2(String y, String x, String z, Set<String> w) {
        // Check if Y is d-separated from Z given X and W
        Set<String> conditioning = new HashSet<>();
        conditioning.add(x);
        conditioning.addAll(w);
        
        boolean dSeparated = verifier.isDSeparated(y, z, conditioning);
        
        List<String> explanation = new ArrayList<>();
        explanation.add("Rule 2: Action/Observation exchange");
        explanation.add(String.format("Check if %s ⊥ %s | {%s, %s}", y, z, x, String.join(", ", w)));
        explanation.add(String.format("d-separated: %s", dSeparated));
        
        if (dSeparated) {
            explanation.add("Result: Can exchange do(Z) for Z, reduce to P(Y | do(X), Z, W)");
            return new CausalQueryResult(
                String.format("P(%s | do(%s), do(%s), %s)", y, x, z, String.join(", ", w)),
                0.0,
                true,
                "Rule 2 simplification",
                explanation
            );
        } else {
            explanation.add("Result: Cannot apply Rule 2, Z is not d-separated");
            return new CausalQueryResult(
                String.format("P(%s | do(%s), do(%s), %s)", y, x, z, String.join(", ", w)),
                0.0,
                false,
                "Rule 2 not applicable",
                explanation
            );
        }
    }
    
    /**
     * Apply Rule 3: Ignoring actions
     * P(y | do(x), do(z), w) = P(y | do(x), w) if (Y ⊥ Z | X, W) in graph where incoming edges to X are removed
     */
    public CausalQueryResult applyRule3(String y, String x, String z, Set<String> w) {
        // Create modified graph where incoming edges to X are removed
        CausalModel modifiedModel = createModifiedGraph(x);
        DAGVerifier modifiedVerifier = new DAGVerifier(modifiedModel);
        
        // Check d-separation in modified graph
        Set<String> conditioning = new HashSet<>();
        conditioning.add(x);
        conditioning.addAll(w);
        
        boolean dSeparated = modifiedVerifier.isDSeparated(y, z, conditioning);
        
        List<String> explanation = new ArrayList<>();
        explanation.add("Rule 3: Ignoring actions");
        explanation.add(String.format("Modified graph: removed incoming edges to %s", x));
        explanation.add(String.format("Check if %s ⊥ %s | {%s, %s} in modified graph", y, z, x, String.join(", ", w)));
        explanation.add(String.format("d-separated: %s", dSeparated));
        
        if (dSeparated) {
            explanation.add("Result: Can ignore do(Z), reduce to P(Y | do(X), W)");
            return new CausalQueryResult(
                String.format("P(%s | do(%s), do(%s), %s)", y, x, z, String.join(", ", w)),
                0.0,
                true,
                "Rule 3 simplification",
                explanation
            );
        } else {
            explanation.add("Result: Cannot apply Rule 3, Z is not d-separated in modified graph");
            return new CausalQueryResult(
                String.format("P(%s | do(%s), do(%s), %s)", y, x, z, String.join(", ", w)),
                0.0,
                false,
                "Rule 3 not applicable",
                explanation
            );
        }
    }
    
    /**
     * Create modified graph where incoming edges to node X are removed
     */
    private CausalModel createModifiedGraph(String x) {
        CausalModel modified = new CausalModel();
        
        // Copy all nodes
        for (String nodeId : model.getNodes().keySet()) {
            CausalModel.CausalNode original = model.getNode(nodeId);
            CausalModel.CausalNode copy = modified.addNode(nodeId, original.getVariable());
            copy.setValue(original.getValue());
            copy.setConfidence(original.getConfidence());
        }
        
        // Copy edges, but skip incoming edges to X
        for (String parentId : model.getNodes().keySet()) {
            for (String childId : model.getChildren(parentId)) {
                if (!childId.equals(x)) {
                    modified.addEdge(parentId, childId);
                }
            }
        }
        
        return modified;
    }
    
    /**
     * Estimate causal effect P(Y | do(X)) using adjustment formula
     * P(Y | do(X)) = Σ_z P(Y | X, z)P(z) if backdoor criterion satisfied
     */
    public CausalQueryResult estimateCausalEffect(String y, String x, Set<String> adjustmentSet) {
        List<String> explanation = new ArrayList<>();
        explanation.add("Causal Effect Estimation");
        explanation.add(String.format("Target: P(%s | do(%s))", y, x));
        explanation.add(String.format("Adjustment set: {%s}", String.join(", ", adjustmentSet)));
        
        // Verify backdoor criterion
        boolean backdoorSatisfied = verifier.verifyBackdoorCriterion(x, y, adjustmentSet);
        explanation.add(String.format("Backdoor criterion satisfied: %s", backdoorSatisfied));
        
        if (backdoorSatisfied) {
            explanation.add(String.format("Formula: P(%s | do(%s)) = Σ_z P(%s | %s, z)P(z)", y, x, y, x));
            explanation.add("Result: Causal effect identifiable via adjustment");
            return new CausalQueryResult(
                String.format("P(%s | do(%s))", y, x),
                0.0, // Would need actual computation
                true,
                "Adjustment formula",
                explanation
            );
        } else {
            explanation.add("Result: Backdoor criterion not satisfied, need different adjustment set");
            return new CausalQueryResult(
                String.format("P(%s | do(%s))", y, x),
                0.0,
                false,
                "Backdoor criterion failed",
                explanation
            );
        }
    }
    
    /**
     * Find minimal adjustment set for causal effect estimation
     */
    public Set<String> findMinimalAdjustmentSet(String x, String y) {
        // Get all parents of X (potential adjustment set)
        Set<String> parents = model.getParents(x);
        
        return parents;
    }

    /**
     * Lightweight identifiability check for a causal effect P(effect | do(cause)).
     * Uses backdoor-adjustment feasibility as the primary criterion.
     */
    public boolean isIdentifiable(String cause, String effect) {
        if (cause == null || effect == null || cause.isBlank() || effect.isBlank() || cause.equals(effect)) {
            return false;
        }
        if (model.getNode(cause) == null || model.getNode(effect) == null) {
            return false;
        }

        Set<String> adjustmentSet = findMinimalAdjustmentSet(cause, effect);
        if (adjustmentSet == null) {
            adjustmentSet = Set.of();
        }

        if (verifier.verifyBackdoorCriterion(cause, effect, adjustmentSet)) {
            return true;
        }

        CausalQueryResult estimate = estimateCausalEffect(effect, cause, adjustmentSet);
        return estimate != null && estimate.isIdentifiable();
    }

    /**
     * Generate a minimal causal chain-of-thought trace for a natural-language query.
     */
    public List<String> causalChainOfThought(String question) {
        List<String> chain = new ArrayList<>();
        chain.add("Causal Chain of Thought");
        chain.add("Question: " + (question == null ? "" : question));

        if (question == null || question.isBlank() || model.getNodes().isEmpty()) {
            chain.add("No causal variables available for analysis.");
            return chain;
        }

        String normalized = question.toUpperCase(Locale.ROOT);
        List<String> mentioned = new ArrayList<>();
        for (String nodeId : model.getNodes().keySet()) {
            if (normalized.contains(nodeId.toUpperCase(Locale.ROOT))) {
                mentioned.add(nodeId);
            }
        }

        if (mentioned.size() < 2) {
            chain.add("Insufficient variable mentions to form cause/effect pair.");
            return chain;
        }

        String cause = mentioned.get(0);
        String effect = mentioned.get(1);
        Set<String> adjustmentSet = findMinimalAdjustmentSet(cause, effect);
        if (adjustmentSet == null) {
            adjustmentSet = Set.of();
        }

        chain.add("Hypothesized cause: " + cause);
        chain.add("Hypothesized effect: " + effect);
        chain.add("Adjustment set: " + adjustmentSet);
        chain.add("Identifiable: " + isIdentifiable(cause, effect));
        return chain;
    }
    
    /**
     * Perform intervention on variable (do-operator)
     */
    public void doIntervention(String variable, double value) {
        CausalModel.CausalNode node = model.getNode(variable);
        if (node != null) {
            node.setValue(value);
        }
    }
    
    /**
     * Reset intervention on variable
     */
    public void resetIntervention(String variable) {
        CausalModel.CausalNode node = model.getNode(variable);
        if (node != null) {
            node.setValue(0.0); // Reset to default
        }
    }
}
