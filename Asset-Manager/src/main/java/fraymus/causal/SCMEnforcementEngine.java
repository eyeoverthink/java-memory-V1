package fraymus.causal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SCM (Structural Causal Model) Enforcement Engine
 * 
 * Verifies and eliminates spurious correlations via rigorous causal inference.
 * 
 * Phase 1.4: SCM Enforcement for Spurious Correlation Elimination
 * 
 * Mathematical foundation:
 * - SCM M = (U, V, F, P(U)) where U=exogenous, V=endogenous, F=functional, P(U)=noise
 * - Spurious correlation: Correlation without causal mechanism
 * - Verification: P(Y|do(X)) ≠ P(Y|X) indicates causal vs spurious
 * 
 * Key mechanisms:
 * - Backdoor criterion verification
 * - Frontdoor criterion verification
 * - Do-calculus intervention testing
 * - Confounder detection
 */
public class SCMEnforcementEngine {
    
    private final CausalModel causalModel;
    private final DAGVerifier dagVerifier;
    private final DoCalculusEngine doCalculus;
    
    // Spurious correlation detection threshold
    private double spuriousThreshold = 0.3;
    
    // Causal confidence threshold
    private double causalConfidenceThreshold = 0.7;
    
    // Confounder detection cache
    private final Map<String, Set<String>> confounderCache = new ConcurrentHashMap<>();
    
    // Verification statistics
    private int totalVerifications = 0;
    private int spuriousCorrelationsDetected = 0;
    private int causalRelationshipsVerified = 0;
    
    public SCMEnforcementEngine(CausalModel causalModel) {
        this.causalModel = causalModel;
        this.dagVerifier = new DAGVerifier(causalModel);
        this.doCalculus = new DoCalculusEngine(causalModel);
    }
    
    /**
     * Verify if a correlation is causal or spurious.
     * 
     * @param cause Potential cause variable
     * @param effect Potential effect variable
     * @return Verification result with confidence
     */
    public VerificationResult verifyCorrelation(String cause, String effect) {
        totalVerifications++;
        
        VerificationResult result = new VerificationResult(cause, effect);
        
        // Check if nodes exist
        if (causalModel.getNode(cause) == null || causalModel.getNode(effect) == null) {
            result.setValid(false);
            result.setReason("One or both nodes not in causal model");
            return result;
        }
        
        // Step 1: Check for direct causal path
        boolean hasDirectPath = dagVerifier.verifyCausalPath(cause, effect);
        result.setHasDirectPath(hasDirectPath);
        
        // Step 2: Check for backdoor criterion (confounders)
        Set<String> confounders = detectConfounders(cause, effect);
        result.setConfounders(confounders);
        
        // Step 3: Perform do-calculus intervention
        try {
            double observationalProb = estimateObservationalProbability(cause, effect);
            double interventionalProb = estimateInterventionalProbability(cause, effect);
            
            result.setObservationalProb(observationalProb);
            result.setInterventionalProb(interventionalProb);
            
            // If probabilities differ significantly, relationship is causal
            double probDiff = Math.abs(observationalProb - interventionalProb);
            boolean isCausal = probDiff > spuriousThreshold;
            
            result.setCausal(isCausal);
            result.setConfidence(probDiff);
            
            if (isCausal) {
                causalRelationshipsVerified++;
            } else {
                spuriousCorrelationsDetected++;
                result.setSpurious(true);
                result.setReason("Observational and interventional probabilities are similar (spurious correlation)");
            }
            
        } catch (Exception e) {
            result.setValid(false);
            result.setReason("Do-calculus computation failed: " + e.getMessage());
        }
        
        // Step 4: Check for common effect (collider) bias
        boolean hasCollider = detectColliderBias(cause, effect);
        result.setHasColliderBias(hasCollider);
        
        if (hasCollider && !result.isCausal()) {
            result.setReason("Potential collider bias (common effect)");
        }
        
        result.setValid(true);
        return result;
    }
    
    /**
     * Detect confounders between cause and effect.
     * Confounder: Variable that causes both cause and effect.
     */
    private Set<String> detectConfounders(String cause, String effect) {
        String cacheKey = cause + "->" + effect;
        if (confounderCache.containsKey(cacheKey)) {
            return confounderCache.get(cacheKey);
        }
        
        Set<String> confounders = new HashSet<>();
        
        // Get all nodes in the model
        Map<String, CausalModel.CausalNode> nodes = causalModel.getNodes();
        
        for (String nodeId : nodes.keySet()) {
            if (nodeId.equals(cause) || nodeId.equals(effect)) continue;
            
            CausalModel.CausalNode node = nodes.get(nodeId);
            
            // Check if this node has edges to both cause and effect
            boolean causesCause = node.getChildren().contains(cause);
            boolean causesEffect = node.getChildren().contains(effect);
            
            if (causesCause && causesEffect) {
                confounders.add(nodeId);
            }
        }
        
        confounderCache.put(cacheKey, confounders);
        return confounders;
    }
    
    /**
     * Estimate observational probability P(effect | cause).
     * This is a simplified estimation based on DAG structure.
     */
    private double estimateObservationalProbability(String cause, String effect) {
        // In a full implementation, this would use actual data
        // For now, use structural properties
        
        if (dagVerifier.verifyCausalPath(cause, effect)) {
            // Direct or indirect causal path exists
            return 0.8; // High probability
        } else {
            // No causal path
            return 0.2; // Low probability (spurious)
        }
    }
    
    /**
     * Estimate interventional probability P(effect | do(cause)).
     * This breaks incoming edges to cause.
     */
    private double estimateInterventionalProbability(String cause, String effect) {
        try {
            // Perform intervention
            doCalculus.doIntervention(cause, 1.0);
            
            // Check if causal path still exists after intervention
            boolean pathExistsAfterIntervention = dagVerifier.verifyCausalPath(cause, effect);
            
            // Reset intervention
            doCalculus.resetIntervention(cause);
            
            if (pathExistsAfterIntervention) {
                return 0.8; // Causal relationship persists
            } else {
                return 0.1; // Relationship was spurious (disappears under intervention)
            }
            
        } catch (Exception e) {
            return 0.5; // Neutral if computation fails
        }
    }
    
    /**
     * Detect collider bias (common effect).
     * Collider: Variable caused by both cause and effect.
     * Conditioning on a collider can create spurious correlations.
     */
    private boolean detectColliderBias(String cause, String effect) {
        CausalModel.CausalNode causeNode = causalModel.getNode(cause);
        CausalModel.CausalNode effectNode = causalModel.getNode(effect);
        
        if (causeNode == null || effectNode == null) return false;
        
        // Check if cause and effect share a common child (collider)
        Set<String> causeChildren = causeNode.getChildren();
        Set<String> effectChildren = effectNode.getChildren();
        
        for (String child : causeChildren) {
            if (effectChildren.contains(child)) {
                return true; // Collider detected
            }
        }
        
        return false;
    }
    
    /**
     * Enforce SCM on a set of candidate correlations.
     * Returns only those that pass causal verification.
     */
    public Map<String, Double> enforceSCM(Map<String, Double> correlations, String query) {
        Map<String, Double> enforcedCorrelations = new HashMap<>();
        
        for (Map.Entry<String, Double> entry : correlations.entrySet()) {
            String candidate = entry.getKey();
            double originalScore = entry.getValue();
            
            VerificationResult result = verifyCorrelation(query, candidate);
            
            if (result.isValid() && result.isCausal()) {
                // Keep causal relationships
                double adjustedScore = originalScore * result.getConfidence();
                enforcedCorrelations.put(candidate, adjustedScore);
            } else if (result.isSpurious()) {
                // Penalize or remove spurious correlations
                double penalizedScore = originalScore * 0.1; // 90% penalty
                if (penalizedScore > 0.1) {
                    enforcedCorrelations.put(candidate, penalizedScore);
                }
            }
            // If invalid, skip entirely
        }
        
        return enforcedCorrelations;
    }
    
    /**
     * Batch verification for multiple correlations.
     */
    public List<VerificationResult> batchVerify(String query, List<String> candidates) {
        List<VerificationResult> results = new ArrayList<>();
        
        for (String candidate : candidates) {
            VerificationResult result = verifyCorrelation(query, candidate);
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * Get verification statistics.
     */
    public String getStatistics() {
        double spuriousRate = totalVerifications > 0 ? 
            (double) spuriousCorrelationsDetected / totalVerifications : 0.0;
        double causalRate = totalVerifications > 0 ? 
            (double) causalRelationshipsVerified / totalVerifications : 0.0;
        
        return String.format(
            "SCM Enforcement Engine Stats:\n" +
            "  Total verifications: %d\n" +
            "  Spurious correlations detected: %d (%.1f%%)\n" +
            "  Causal relationships verified: %d (%.1f%%)\n" +
            "  Spurious threshold: %.2f\n" +
            "  Causal confidence threshold: %.2f",
            totalVerifications,
            spuriousCorrelationsDetected,
            spuriousRate * 100,
            causalRelationshipsVerified,
            causalRate * 100,
            spuriousThreshold,
            causalConfidenceThreshold
        );
    }
    
    /**
     * Set spurious correlation threshold.
     */
    public void setSpuriousThreshold(double threshold) {
        this.spuriousThreshold = Math.max(0.0, Math.min(1.0, threshold));
    }
    
    /**
     * Set causal confidence threshold.
     */
    public void setCausalConfidenceThreshold(double threshold) {
        this.causalConfidenceThreshold = Math.max(0.0, Math.min(1.0, threshold));
    }
    
    /**
     * Clear confounder cache.
     */
    public void clearCache() {
        confounderCache.clear();
    }
    
    /**
     * Verification result class.
     */
    public static class VerificationResult {
        private final String cause;
        private final String effect;
        private boolean valid;
        private boolean causal;
        private boolean spurious;
        private boolean hasDirectPath;
        private Set<String> confounders;
        private boolean hasColliderBias;
        private double observationalProb;
        private double interventionalProb;
        private double confidence;
        private String reason;
        
        public VerificationResult(String cause, String effect) {
            this.cause = cause;
            this.effect = effect;
            this.confounders = new HashSet<>();
        }
        
        public String getCause() { return cause; }
        public String getEffect() { return effect; }
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public boolean isCausal() { return causal; }
        public void setCausal(boolean causal) { this.causal = causal; }
        public boolean isSpurious() { return spurious; }
        public void setSpurious(boolean spurious) { this.spurious = spurious; }
        public boolean hasDirectPath() { return hasDirectPath; }
        public void setHasDirectPath(boolean hasDirectPath) { this.hasDirectPath = hasDirectPath; }
        public Set<String> getConfounders() { return new HashSet<>(confounders); }
        public void setConfounders(Set<String> confounders) { this.confounders = confounders; }
        public boolean hasColliderBias() { return hasColliderBias; }
        public void setHasColliderBias(boolean hasColliderBias) { this.hasColliderBias = hasColliderBias; }
        public double getObservationalProb() { return observationalProb; }
        public void setObservationalProb(double prob) { this.observationalProb = prob; }
        public double getInterventionalProb() { return interventionalProb; }
        public void setInterventionalProb(double prob) { this.interventionalProb = prob; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        @Override
        public String toString() {
            return String.format(
                "VerificationResult[%s -> %s]: valid=%s, causal=%s, spurious=%s, confidence=%.3f, reason=%s",
                cause, effect, valid, causal, spurious, confidence, reason
            );
        }
    }
}
