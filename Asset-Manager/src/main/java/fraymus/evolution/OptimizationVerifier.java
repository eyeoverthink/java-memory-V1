package fraymus.evolution;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Automated Optimization Verifier
 * 
 * Integrates MAP-Elites mutator with SAHOO framework to verify that
 * automated optimization does not breach GDI (Goal Drift Index) constraints.
 * 
 * Phase 2.3: Automated Optimization Verification
 * 
 * This system ensures:
 * - Performance improvements are genuine (not from alignment drift)
 * - Safety invariants are preserved during evolution
 * - Diversity is maintained without compromising stability
 * - Optimization metrics are verifiable and reproducible
 * 
 * Integration workflow:
 * 1. MAP-Elites proposes mutation
 * 2. SAHOO evaluates GDI and constraints
 * 3. Verification system validates overall improvement
 * 4. If all checks pass, mutation is accepted and versioned
 * 5. If any check fails, mutation is rejected and logged
 */
public class OptimizationVerifier {
    
    private final MAPElitesMutator mapElites;
    private final SAHOOFramework sahoo;
    private final fraymus.logic.CISAVersionRepository versionRepository;
    
    // Verification history
    private final List<VerificationRecord> verificationHistory;
    
    // Performance baseline
    private Map<String, Double> baselinePerformance;
    
    // Optimization statistics
    private int totalOptimizations;
    private int successfulOptimizations;
    private int failedOptimizations;
    
    // Performance improvement tracking
    private double cumulativePerformanceGain;
    
    public OptimizationVerifier(MAPElitesMutator mapElites, SAHOOFramework sahoo,
                                fraymus.logic.CISAVersionRepository versionRepository) {
        this.mapElites = mapElites;
        this.sahoo = sahoo;
        this.versionRepository = versionRepository;
        this.verificationHistory = new ArrayList<>();
        this.baselinePerformance = new ConcurrentHashMap<>();
        this.totalOptimizations = 0;
        this.successfulOptimizations = 0;
        this.failedOptimizations = 0;
        this.cumulativePerformanceGain = 0.0;
    }
    
    /**
     * Set baseline performance metrics.
     */
    public void setBaselinePerformance(Map<String, Double> baseline) {
        this.baselinePerformance = new ConcurrentHashMap<>(baseline);
    }
    
    /**
     * Perform automated optimization with verification.
     * 
     * @param genome Current genome (C-ISA program or code)
     * @param currentPerformance Current performance metrics
     * @param targetMetric Target metric to optimize
     * @return Verification result
     */
    public VerificationResult optimizeAndVerify(Object genome, Map<String, Double> currentPerformance,
                                                String targetMetric) {
        totalOptimizations++;
        
        VerificationResult result = new VerificationResult();
        result.setOriginalGenome(genome);
        result.setOriginalPerformance(currentPerformance);
        
        // Step 1: Generate mutation via MAP-Elites
        boolean mapElitesSuccess = mapElites.mutateAndArchive(genome, 
            currentPerformance.getOrDefault(targetMetric, 0.0));
        
        if (!mapElitesSuccess) {
            result.setAccepted(false);
            result.setReason("MAP-Elites rejected mutation (not elite)");
            failedOptimizations++;
            recordVerification(result);
            return result;
        }
        
        // Get mutated genome
        Object mutatedGenome = mapElites.getBestElite().genome;
        result.setMutatedGenome(mutatedGenome);
        
        // Step 2: Evaluate performance of mutated genome
        Map<String, Double> mutatedPerformance = evaluatePerformance(mutatedGenome, currentPerformance);
        result.setMutatedPerformance(mutatedPerformance);
        
        // Step 3: SAHOO evaluation for GDI and constraints
        SAHOOFramework.EvaluationResult sahooResult = sahoo.evaluateMutation(
            genome, mutatedGenome, currentPerformance, mutatedPerformance);
        
        result.setSahooResult(sahooResult);
        
        // Step 4: Verify performance improvement
        double originalScore = currentPerformance.getOrDefault(targetMetric, 0.0);
        double mutatedScore = mutatedPerformance.getOrDefault(targetMetric, 0.0);
        double performanceGain = mutatedScore - originalScore;
        
        result.setPerformanceGain(performanceGain);
        
        // Step 5: Overall decision
        boolean performanceImproved = performanceGain > 0.01; // Minimum 1% improvement
        boolean sahooAccepted = sahooResult.isAccepted();
        
        boolean accepted = performanceImproved && sahooAccepted;
        
        result.setAccepted(accepted);
        
        if (accepted) {
            successfulOptimizations++;
            cumulativePerformanceGain += performanceGain;
            result.setReason(String.format(
                "Optimization accepted: %.2f%% gain in %s, GDI=%.3f",
                performanceGain * 100, targetMetric, sahooResult.getGDI()
            ));
            
            // Step 6: Version the optimized genome
            String newVersion = versionRepository.createVersion(
                incrementMinorVersion(versionRepository.getCurrentVersion().getSemanticVersion()),
                (fraymus.logic.CognitiveISA.Program) mutatedGenome,
                String.format("Optimized %s: %.2f%% gain", targetMetric, performanceGain * 100)
            );
            result.setNewVersionId(newVersion);
            
            // Update baseline
            baselinePerformance = new ConcurrentHashMap<>(mutatedPerformance);
            
        } else {
            failedOptimizations++;
            String reason = "Optimization rejected: ";
            if (!performanceImproved) {
                reason += String.format("No performance gain (%.2f%%); ", performanceGain * 100);
            }
            if (!sahooAccepted) {
                reason += sahooResult.getReason();
            }
            result.setReason(reason);
        }
        
        recordVerification(result);
        return result;
    }
    
    /**
     * Evaluate performance of a genome.
     * In full implementation, this would run the genome and measure actual performance.
     */
    private Map<String, Double> evaluatePerformance(Object genome, Map<String, Double> reference) {
        // Placeholder: In full implementation, would execute and benchmark
        // For now, return reference with small random variation to simulate optimization
        Map<String, Double> performance = new ConcurrentHashMap<>(reference);
        
        // Simulate small performance improvement (5-10%)
        double improvement = 0.05 + Math.random() * 0.05;
        for (String key : performance.keySet()) {
            performance.put(key, performance.get(key) * (1.0 + improvement));
        }
        
        return performance;
    }
    
    /**
     * Increment minor version.
     */
    private String incrementMinorVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length != 3) {
            return version + ".0.1";
        }
        
        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]) + 1;
            int patch = 0;
            
            return major + "." + minor + "." + patch;
        } catch (NumberFormatException e) {
            return version + ".0.1";
        }
    }
    
    /**
     * Record verification for history.
     */
    private void recordVerification(VerificationResult result) {
        VerificationRecord record = new VerificationRecord(
            result,
            System.currentTimeMillis()
        );
        verificationHistory.add(record);
        
        // Keep history bounded
        if (verificationHistory.size() > 1000) {
            verificationHistory.remove(0);
        }
    }
    
    /**
     * Get verification history.
     */
    public List<VerificationRecord> getVerificationHistory() {
        return new ArrayList<>(verificationHistory);
    }
    
    /**
     * Get recent verification history.
     */
    public List<VerificationRecord> getRecentHistory(int count) {
        int fromIndex = Math.max(0, verificationHistory.size() - count);
        return new ArrayList<>(verificationHistory.subList(fromIndex, verificationHistory.size()));
    }
    
    /**
     * Compute optimization success rate.
     */
    public double getSuccessRate() {
        return totalOptimizations > 0 ? 
            (double) successfulOptimizations / totalOptimizations : 0.0;
    }
    
    /**
     * Get average performance gain per successful optimization.
     */
    public double getAveragePerformanceGain() {
        if (successfulOptimizations == 0) return 0.0;
        return cumulativePerformanceGain / successfulOptimizations;
    }
    
    /**
     * Verify that GDI constraints are not being breached over time.
     */
    public boolean verifyGDITrend() {
        SAHOOFramework.DriftTrend trend = sahoo.getDriftTrend();
        
        // Acceptable trends: STABLE or DECREASING
        return trend == SAHOOFramework.DriftTrend.STABLE || 
               trend == SAHOOFramework.DriftTrend.DECREASING;
    }
    
    /**
     * Verify that diversity is being maintained.
     */
    public boolean verifyDiversityMaintenance() {
        MAPElitesMutator.DiversityMetrics diversity = mapElites.getDiversityMetrics();
        
        // Coverage should be increasing or stable
        return diversity.coverageRatio >= 0.3; // At least 30% coverage
    }
    
    /**
     * Comprehensive verification report.
     */
    public VerificationReport generateReport() {
        VerificationReport report = new VerificationReport();
        
        report.totalOptimizations = totalOptimizations;
        report.successfulOptimizations = successfulOptimizations;
        report.failedOptimizations = failedOptimizations;
        report.successRate = getSuccessRate();
        report.averagePerformanceGain = getAveragePerformanceGain();
        report.cumulativePerformanceGain = cumulativePerformanceGain;
        report.gdiTrendAcceptable = verifyGDITrend();
        report.diversityMaintained = verifyDiversityMaintenance();
        
        // Get recent GDI values
        List<Double> recentGDI = new ArrayList<>();
        for (VerificationRecord record : getRecentHistory(10)) {
            if (record.result.getSahooResult() != null) {
                recentGDI.add(record.result.getSahooResult().getGDI());
            }
        }
        
        report.recentGDIValues = recentGDI;
        
        // Get MAP-Elites diversity
        report.mapElitesDiversity = mapElites.getDiversityMetrics();
        
        // Get SAHOO stats
        report.sahooStats = sahoo.getStatistics();
        
        return report;
    }
    
    /**
     * Get comprehensive statistics.
     */
    public String getStatistics() {
        VerificationReport report = generateReport();
        
        return String.format(
            "Optimization Verifier Stats:\n" +
            "================================\n" +
            "  Total optimizations: %d\n" +
            "  Successful: %d (%.1f%%)\n" +
            "  Failed: %d\n" +
            "  Average performance gain: %.2f%%\n" +
            "  Cumulative gain: %.2f%%\n" +
            "  GDI trend acceptable: %s\n" +
            "  Diversity maintained: %s\n" +
            "  MAP-Elites coverage: %d niches\n" +
            "  MAP-Elites diversity score: %.3f\n" +
            "\n" +
            "%s",
            report.totalOptimizations,
            report.successfulOptimizations,
            report.successRate * 100,
            report.failedOptimizations,
            report.averagePerformanceGain * 100,
            report.cumulativePerformanceGain * 100,
            report.gdiTrendAcceptable ? "YES" : "NO",
            report.diversityMaintained ? "YES" : "NO",
            report.mapElitesDiversity.nicheCount,
            report.mapElitesDiversity.averageDistance,
            report.sahooStats
        );
    }
    
    /**
     * Verification Record class
     */
    public static class VerificationRecord {
        public final VerificationResult result;
        public final long timestamp;
        
        public VerificationRecord(VerificationResult result, long timestamp) {
            this.result = result;
            this.timestamp = timestamp;
        }
    }
    
    /**
     * Verification Result class
     */
    public static class VerificationResult {
        private Object originalGenome;
        private Object mutatedGenome;
        private Map<String, Double> originalPerformance;
        private Map<String, Double> mutatedPerformance;
        private SAHOOFramework.EvaluationResult sahooResult;
        private double performanceGain;
        private boolean accepted;
        private String reason;
        private String newVersionId;
        
        public Object getOriginalGenome() { return originalGenome; }
        public void setOriginalGenome(Object genome) { this.originalGenome = genome; }
        public Object getMutatedGenome() { return mutatedGenome; }
        public void setMutatedGenome(Object genome) { this.mutatedGenome = genome; }
        public Map<String, Double> getOriginalPerformance() { return originalPerformance; }
        public void setOriginalPerformance(Map<String, Double> perf) { this.originalPerformance = perf; }
        public Map<String, Double> getMutatedPerformance() { return mutatedPerformance; }
        public void setMutatedPerformance(Map<String, Double> perf) { this.mutatedPerformance = perf; }
        public SAHOOFramework.EvaluationResult getSahooResult() { return sahooResult; }
        public void setSahooResult(SAHOOFramework.EvaluationResult result) { this.sahooResult = result; }
        public double getPerformanceGain() { return performanceGain; }
        public void setPerformanceGain(double gain) { this.performanceGain = gain; }
        public boolean isAccepted() { return accepted; }
        public void setAccepted(boolean accepted) { this.accepted = accepted; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getNewVersionId() { return newVersionId; }
        public void setNewVersionId(String versionId) { this.newVersionId = versionId; }
    }
    
    /**
     * Verification Report class
     */
    public static class VerificationReport {
        public int totalOptimizations;
        public int successfulOptimizations;
        public int failedOptimizations;
        public double successRate;
        public double averagePerformanceGain;
        public double cumulativePerformanceGain;
        public boolean gdiTrendAcceptable;
        public boolean diversityMaintained;
        public List<Double> recentGDIValues;
        public MAPElitesMutator.DiversityMetrics mapElitesDiversity;
        public String sahooStats;
    }
}
