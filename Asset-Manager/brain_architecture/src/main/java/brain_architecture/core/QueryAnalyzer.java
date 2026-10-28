package brain_architecture.core;

/**
 * Query analyzer for task classification and requirement extraction.
 * TODO: Integrate from sandbox or implement fresh
 */
public class QueryAnalyzer {
    
    public AnalysisResult analyze(String query) {
        // Simple analysis - default to reasoning
        return new AnalysisResult(
            BackendInterface.TaskType.REASONING,
            0.5,
            0.5,
            0.5,
            java.util.Set.of()
        );
    }
    
    public static class AnalysisResult {
        private final BackendInterface.TaskType taskType;
        private final double complexityScore;
        private final double latencyRequirement;
        private final double accuracyRequirement;
        private final java.util.Set<String> keywords;
        
        public AnalysisResult(BackendInterface.TaskType taskType, double complexityScore,
                            double latencyRequirement, double accuracyRequirement,
                            java.util.Set<String> keywords) {
            this.taskType = taskType;
            this.complexityScore = complexityScore;
            this.latencyRequirement = latencyRequirement;
            this.accuracyRequirement = accuracyRequirement;
            this.keywords = keywords;
        }
        
        public BackendInterface.TaskType getTaskType() {
            return taskType;
        }
        
        public double getComplexityScore() {
            return complexityScore;
        }
        
        public double getLatencyRequirement() {
            return latencyRequirement;
        }
        
        public double getAccuracyRequirement() {
            return accuracyRequirement;
        }
        
        public java.util.Set<String> getKeywords() {
            return keywords;
        }
    }
}
