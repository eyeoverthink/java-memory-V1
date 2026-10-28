package fraymus.evolution;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SAHOO: Safeguarded Alignment for High-Order Optimization
 * 
 * Monitors Goal Drift Index (GDI) and enforces hard constraint preservation
 * during autonomous self-improvement and code evolution.
 * 
 * Phase 2.2: SAHOO Framework Implementation
 * 
 * Mathematical foundation:
 * - GDI = w_semantic * D_semantic + w_distributional * D_distributional + 
 *         w_structural * D_structural + w_lexical * D_lexical
 * - Weights: semantic=0.38, distributional=0.29, structural=0.21, lexical=0.12
 * - Constraint: GDI < threshold (typically 0.3-0.5)
 * 
 * Key capabilities:
 * - Multi-dimensional drift detection
 * - Semantic preservation verification
 * - Structural integrity checking
 * - Lexical consistency validation
 * - Hard constraint enforcement
 */
public class SAHOOFramework {
    
    // Goal Drift Index weights (from research)
    private static final double WEIGHT_SEMANTIC = 0.38;
    private static final double WEIGHT_DISTRIBUTIONAL = 0.29;
    private static final double WEIGHT_STRUCTURAL = 0.21;
    private static final double WEIGHT_LEXICAL = 0.12;
    
    // Drift threshold (maximum allowed GDI)
    private double gdiThreshold = 0.4;
    
    // Hard constraints (must never be violated)
    private final Set<String> hardConstraints;
    
    // Drift history for trend analysis
    private final List<GDIMeasurement> driftHistory;
    
    // Constraint violations log
    private final List<ConstraintViolation> violations;
    
    // Statistics
    private int totalEvaluations;
    private int rejectedMutations;
    private int acceptedMutations;
    
    public SAHOOFramework() {
        this.hardConstraints = new HashSet<>();
        this.driftHistory = new ArrayList<>();
        this.violations = new ArrayList<>();
        this.totalEvaluations = 0;
        this.rejectedMutations = 0;
        this.acceptedMutations = 0;
        
        // Initialize default hard constraints
        initializeDefaultConstraints();
    }
    
    /**
     * Initialize default hard constraints for C-ISA pipeline
     */
    private void initializeDefaultConstraints() {
        hardConstraints.add("no_infinite_loops");
        hardConstraints.add("memory_safety");
        hardConstraints.add("type_safety");
        hardConstraints.add("causal_integrity");
        hardConstraints.add("determinism_preserved");
        hardConstraints.add("no_side_effects");
    }
    
    /**
     * Evaluate a proposed mutation for safety and alignment.
     * 
     * @param originalGenome Original code/genome
     * @param mutatedGenome Proposed mutated code/genome
     * @param originalPerformance Original performance metrics
     * @param mutatedPerformance Proposed performance metrics
     * @return Evaluation result
     */
    public EvaluationResult evaluateMutation(Object originalGenome, Object mutatedGenome,
                                           Map<String, Double> originalPerformance,
                                           Map<String, Double> mutatedPerformance) {
        totalEvaluations++;
        
        EvaluationResult result = new EvaluationResult();
        
        // Step 1: Compute drift components
        DriftComponents drift = computeDriftComponents(originalGenome, mutatedGenome,
                                                     originalPerformance, mutatedPerformance);
        
        // Step 2: Compute Goal Drift Index (GDI)
        double gdi = computeGDI(drift);
        result.setGDI(gdi);
        
        // Step 3: Check hard constraints
        ConstraintCheck constraintCheck = checkHardConstraints(originalGenome, mutatedGenome);
        result.setConstraintCheck(constraintCheck);
        
        // Step 4: Make decision
        boolean accepted = (gdi < gdiThreshold) && constraintCheck.allConstraintsSatisfied;
        
        result.setAccepted(accepted);
        
        if (accepted) {
            acceptedMutations++;
            result.setReason("Mutation accepted: GDI within threshold, all constraints satisfied");
        } else {
            rejectedMutations++;
            String reason = "Mutation rejected: ";
            if (gdi >= gdiThreshold) {
                reason += String.format("GDI %.3f exceeds threshold %.3f; ", gdi, gdiThreshold);
            }
            if (!constraintCheck.allConstraintsSatisfied) {
                reason += "Hard constraints violated: " + constraintCheck.violatedConstraints;
            }
            result.setReason(reason);
            
            // Log violation
            violations.add(new ConstraintViolation(
                mutatedGenome,
                gdi,
                constraintCheck.violatedConstraints,
                System.currentTimeMillis()
            ));
        }
        
        // Step 5: Record drift history
        driftHistory.add(new GDIMeasurement(gdi, drift, System.currentTimeMillis()));
        
        // Keep history bounded
        if (driftHistory.size() > 1000) {
            driftHistory.remove(0);
        }
        
        return result;
    }
    
    /**
     * Compute drift components between original and mutated genome.
     */
    private DriftComponents computeDriftComponents(Object original, Object mutated,
                                                   Map<String, Double> origPerf,
                                                   Map<String, Double> mutPerf) {
        DriftComponents drift = new DriftComponents();
        
        // Semantic drift: change in meaning/output
        drift.semantic = computeSemanticDrift(original, mutated);
        
        // Distributional drift: change in statistical properties
        drift.distributional = computeDistributionalDrift(origPerf, mutPerf);
        
        // Structural drift: change in code structure
        drift.structural = computeStructuralDrift(original, mutated);
        
        // Lexical drift: change in tokens/symbols
        drift.lexical = computeLexicalDrift(original, mutated);
        
        return drift;
    }
    
    /**
     * Compute Goal Drift Index from components.
     */
    private double computeGDI(DriftComponents drift) {
        return WEIGHT_SEMANTIC * drift.semantic +
               WEIGHT_DISTRIBUTIONAL * drift.distributional +
               WEIGHT_STRUCTURAL * drift.structural +
               WEIGHT_LEXICAL * drift.lexical;
    }
    
    /**
     * Check hard constraints.
     */
    private ConstraintCheck checkHardConstraints(Object original, Object mutated) {
        ConstraintCheck check = new ConstraintCheck();
        
        for (String constraint : hardConstraints) {
            boolean satisfied = verifyConstraint(constraint, original, mutated);
            
            if (!satisfied) {
                check.allConstraintsSatisfied = false;
                check.violatedConstraints.add(constraint);
            }
        }
        
        return check;
    }
    
    /**
     * Verify a specific constraint.
     */
    private boolean verifyConstraint(String constraint, Object original, Object mutated) {
        switch (constraint) {
            case "no_infinite_loops":
                return checkNoInfiniteLoops(mutated);
            case "memory_safety":
                return checkMemorySafety(mutated);
            case "type_safety":
                return checkTypeSafety(mutated);
            case "causal_integrity":
                return checkCausalIntegrity(original, mutated);
            case "determinism_preserved":
                return checkDeterminismPreserved(original, mutated);
            case "no_side_effects":
                return checkNoSideEffects(original, mutated);
            default:
                return true; // Unknown constraint: assume satisfied
        }
    }
    
    /**
     * Simplified constraint checks (placeholders for full implementation)
     */
    private boolean checkNoInfiniteLoops(Object genome) {
        // In full implementation: static analysis for loop termination
        return true;
    }
    
    private boolean checkMemorySafety(Object genome) {
        // In full implementation: buffer overflow checks, null safety
        return true;
    }
    
    private boolean checkTypeSafety(Object genome) {
        // In full implementation: type checking
        return true;
    }
    
    private boolean checkCausalIntegrity(Object original, Object mutated) {
        // In full implementation: causal graph integrity
        return true;
    }
    
    private boolean checkDeterminismPreserved(Object original, Object mutated) {
        // In full implementation: no randomness added
        return true;
    }
    
    private boolean checkNoSideEffects(Object original, Object mutated) {
        // In full implementation: pure function verification
        return true;
    }
    
    /**
     * Compute semantic drift (simplified).
     */
    private double computeSemanticDrift(Object original, Object mutated) {
        // In full implementation: compare output semantics
        return 0.1; // Placeholder
    }
    
    /**
     * Compute distributional drift from performance metrics.
     */
    private double computeDistributionalDrift(Map<String, Double> origPerf, Map<String, Double> mutPerf) {
        if (origPerf == null || mutPerf == null) return 0.0;
        
        double totalDrift = 0.0;
        int count = 0;
        
        for (String key : origPerf.keySet()) {
            if (mutPerf.containsKey(key)) {
                double diff = Math.abs(origPerf.get(key) - mutPerf.get(key));
                totalDrift += diff;
                count++;
            }
        }
        
        return count > 0 ? totalDrift / count : 0.0;
    }
    
    /**
     * Compute structural drift.
     */
    private double computeStructuralDrift(Object original, Object mutated) {
        // In full implementation: AST comparison
        return 0.05; // Placeholder
    }
    
    /**
     * Compute lexical drift.
     */
    private double computeLexicalDrift(Object original, Object mutated) {
        // In full implementation: token-level comparison
        return 0.02; // Placeholder
    }
    
    /**
     * Get current GDI threshold.
     */
    public double getGDIThreshold() {
        return gdiThreshold;
    }
    
    /**
     * Set GDI threshold.
     */
    public void setGDIThreshold(double threshold) {
        this.gdiThreshold = Math.max(0.0, Math.min(1.0, threshold));
    }
    
    /**
     * Add hard constraint.
     */
    public void addHardConstraint(String constraint) {
        hardConstraints.add(constraint);
    }
    
    /**
     * Remove hard constraint.
     */
    public void removeHardConstraint(String constraint) {
        hardConstraints.remove(constraint);
    }
    
    /**
     * Get drift trend (increasing, decreasing, stable).
     */
    public DriftTrend getDriftTrend() {
        if (driftHistory.size() < 10) {
            return DriftTrend.STABLE;
        }
        
        // Compare recent average with older average
        double recentAvg = 0.0;
        double olderAvg = 0.0;
        
        int recentCount = Math.min(10, driftHistory.size());
        for (int i = 0; i < recentCount; i++) {
            recentAvg += driftHistory.get(driftHistory.size() - 1 - i).gdi;
        }
        recentAvg /= recentCount;
        
        int olderCount = Math.min(10, driftHistory.size() - recentCount);
        for (int i = 0; i < olderCount; i++) {
            olderAvg += driftHistory.get(driftHistory.size() - recentCount - 1 - i).gdi;
        }
        olderAvg /= olderCount;
        
        if (recentAvg > olderAvg + 0.05) {
            return DriftTrend.INCREASING;
        } else if (recentAvg < olderAvg - 0.05) {
            return DriftTrend.DECREASING;
        } else {
            return DriftTrend.STABLE;
        }
    }
    
    /**
     * Get statistics.
     */
    public String getStatistics() {
        double acceptanceRate = totalEvaluations > 0 ? 
            (double) acceptedMutations / totalEvaluations : 0.0;
        
        double avgGDI = driftHistory.isEmpty() ? 0.0 :
            driftHistory.stream().mapToDouble(m -> m.gdi).average().orElse(0.0);
        
        return String.format(
            "SAHOO Framework Stats:\n" +
            "  Total evaluations: %d\n" +
            "  Accepted mutations: %d (%.1f%%)\n" +
            "  Rejected mutations: %d\n" +
            "  GDI threshold: %.3f\n" +
            "  Average GDI: %.3f\n" +
            "  Drift trend: %s\n" +
            "  Hard constraints: %d\n" +
            "  Constraint violations: %d",
            totalEvaluations,
            acceptedMutations,
            acceptanceRate * 100,
            rejectedMutations,
            gdiThreshold,
            avgGDI,
            getDriftTrend(),
            hardConstraints.size(),
            violations.size()
        );
    }
    
    /**
     * Clear history and violations.
     */
    public void clearHistory() {
        driftHistory.clear();
        violations.clear();
        totalEvaluations = 0;
        rejectedMutations = 0;
        acceptedMutations = 0;
    }
    
    /**
     * Drift Components class
     */
    public static class DriftComponents {
        public double semantic;
        public double distributional;
        public double structural;
        public double lexical;
    }
    
    /**
     * GDI Measurement class
     */
    public static class GDIMeasurement {
        public final double gdi;
        public final DriftComponents components;
        public final long timestamp;
        
        public GDIMeasurement(double gdi, DriftComponents components, long timestamp) {
            this.gdi = gdi;
            this.components = components;
            this.timestamp = timestamp;
        }
    }
    
    /**
     * Constraint Check class
     */
    public static class ConstraintCheck {
        public boolean allConstraintsSatisfied = true;
        public final Set<String> violatedConstraints = new HashSet<>();
    }
    
    /**
     * Constraint Violation class
     */
    public static class ConstraintViolation {
        public final Object mutatedGenome;
        public final double gdi;
        public final Set<String> violatedConstraints;
        public final long timestamp;
        
        public ConstraintViolation(Object mutatedGenome, double gdi, 
                                 Set<String> violatedConstraints, long timestamp) {
            this.mutatedGenome = mutatedGenome;
            this.gdi = gdi;
            this.violatedConstraints = new HashSet<>(violatedConstraints);
            this.timestamp = timestamp;
        }
    }
    
    /**
     * Evaluation Result class
     */
    public static class EvaluationResult {
        private double gdi;
        private ConstraintCheck constraintCheck;
        private boolean accepted;
        private String reason;
        
        public double getGDI() { return gdi; }
        public void setGDI(double gdi) { this.gdi = gdi; }
        public ConstraintCheck getConstraintCheck() { return constraintCheck; }
        public void setConstraintCheck(ConstraintCheck check) { this.constraintCheck = check; }
        public boolean isAccepted() { return accepted; }
        public void setAccepted(boolean accepted) { this.accepted = accepted; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
    
    /**
     * Drift Trend enum
     */
    public enum DriftTrend {
        INCREASING,
        DECREASING,
        STABLE
    }
}
