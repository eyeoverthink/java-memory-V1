package fraymus.brain.unified;

/**
 * Statistics about a brain's performance.
 */
public class BrainStatistics {
    private final long totalQueriesProcessed;
    private final long successfulQueries;
    private final long failedQueries;
    private final double averageProcessingTimeMs;
    private final double averageConfidence;
    private final double averageConsciousness;

    public BrainStatistics(long totalQueriesProcessed, long successfulQueries, 
                          long failedQueries, double averageProcessingTimeMs,
                          double averageConfidence, double averageConsciousness) {
        this.totalQueriesProcessed = totalQueriesProcessed;
        this.successfulQueries = successfulQueries;
        this.failedQueries = failedQueries;
        this.averageProcessingTimeMs = averageProcessingTimeMs;
        this.averageConfidence = averageConfidence;
        this.averageConsciousness = averageConsciousness;
    }

    public long getTotalQueriesProcessed() {
        return totalQueriesProcessed;
    }

    public long getSuccessfulQueries() {
        return successfulQueries;
    }

    public long getFailedQueries() {
        return failedQueries;
    }

    public double getSuccessRate() {
        if (totalQueriesProcessed == 0) return 0.0;
        return (double) successfulQueries / totalQueriesProcessed;
    }

    public double getAverageProcessingTimeMs() {
        return averageProcessingTimeMs;
    }

    public double getAverageConfidence() {
        return averageConfidence;
    }

    public double getAverageConsciousness() {
        return averageConsciousness;
    }

    public static BrainStatistics empty() {
        return new BrainStatistics(0, 0, 0, 0.0, 0.0, 0.0);
    }
}
