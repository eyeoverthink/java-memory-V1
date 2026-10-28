package brain_architecture.kernel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Benchmark report comparing retrieval profiles.
 */
public final class MemoryBenchmarkReport {
    private final String query;
    private final Map<RetrievalProfile, List<MemoryHit>> hitsByProfile;
    private final Map<RetrievalProfile, Double> aggregateScores;
    private final RetrievalProfile winner;

    public MemoryBenchmarkReport(
            String query,
            Map<RetrievalProfile, List<MemoryHit>> hitsByProfile,
            Map<RetrievalProfile, Double> aggregateScores,
            RetrievalProfile winner) {
        this.query = query;
        this.hitsByProfile = Map.copyOf(hitsByProfile);
        this.aggregateScores = Map.copyOf(aggregateScores);
        this.winner = winner;
    }

    public String getQuery() {
        return query;
    }

    public Map<RetrievalProfile, List<MemoryHit>> getHitsByProfile() {
        return hitsByProfile;
    }

    public Map<RetrievalProfile, Double> getAggregateScores() {
        return aggregateScores;
    }

    public RetrievalProfile getWinner() {
        return winner;
    }

    public Map<String, Object> toSummaryMap() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("query", query);
        summary.put("winner", winner.name());
        for (Map.Entry<RetrievalProfile, Double> entry : aggregateScores.entrySet()) {
            summary.put(entry.getKey().name().toLowerCase(), entry.getValue());
        }
        return summary;
    }
}
