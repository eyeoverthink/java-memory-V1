package brain_architecture.connectome;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RichClub - Detects and manages highly interconnected hub neurons.
 * 
 * Inspired by the Drosophila connectome where ~30% of neurons are rich club
 * members that serve as core integrators and broadcasters of signals.
 */
public class RichClub {
    
    private List<UUID> members;
    private double threshold; // Top X% by degree
    private double richClubCoefficient;
    
    public RichClub() {
        this.members = new ArrayList<>();
        this.threshold = 0.3; // Top 30% by default
        this.richClubCoefficient = 0.0;
    }
    
    public RichClub(double threshold) {
        this();
        this.threshold = Math.max(0.1, Math.min(1.0, threshold));
    }
    
    public List<UUID> getMembers() {
        return new ArrayList<>(members);
    }
    
    public double getThreshold() {
        return threshold;
    }
    
    public void setThreshold(double threshold) {
        this.threshold = Math.max(0.1, Math.min(1.0, threshold));
    }
    
    public double getRichClubCoefficient() {
        return richClubCoefficient;
    }
    
    public int getMemberCount() {
        return members.size();
    }
    
    /**
     * Detect rich club members from the connectome graph.
     * Selects top X% of neurons by degree (number of connections).
     */
    public void detectRichClub(Map<UUID, List<UUID>> adjacencyList) {
        // Calculate degree for each neuron
        Map<UUID, Integer> degrees = new HashMap<>();
        for (Map.Entry<UUID, List<UUID>> entry : adjacencyList.entrySet()) {
            degrees.put(entry.getKey(), entry.getValue().size());
        }
        
        // Sort neurons by degree (descending)
        List<UUID> sortedNeurons = degrees.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        // Select top threshold percentage as rich club
        int richClubSize = (int) Math.ceil(sortedNeurons.size() * threshold);
        this.members = sortedNeurons.subList(0, Math.min(richClubSize, sortedNeurons.size()));
        
        // Calculate rich club coefficient
        calculateRichClubCoefficient(adjacencyList);
    }
    
    /**
     * Calculate rich club coefficient Φ(k).
     * Φ(k) = #connections between rich club / max possible connections
     */
    private void calculateRichClubCoefficient(Map<UUID, List<UUID>> adjacencyList) {
        if (members.size() < 2) {
            this.richClubCoefficient = 0.0;
            return;
        }
        
        // Count actual connections between rich club members
        int actualConnections = 0;
        for (UUID member1 : members) {
            List<UUID> neighbors = adjacencyList.getOrDefault(member1, new ArrayList<>());
            for (UUID neighbor : neighbors) {
                if (members.contains(neighbor)) {
                    actualConnections++;
                }
            }
        }
        
        // Divide by 2 since each connection counted twice
        actualConnections /= 2;
        
        // Calculate maximum possible connections (complete graph)
        int maxConnections = (members.size() * (members.size() - 1)) / 2;
        
        // Calculate coefficient
        this.richClubCoefficient = maxConnections > 0 ? 
            (double) actualConnections / maxConnections : 0.0;
    }
    
    /**
     * Check if a neuron is a rich club member.
     */
    public boolean isMember(UUID neuronId) {
        return members.contains(neuronId);
    }
    
    /**
     * Add a neuron to rich club (manual override).
     */
    public void addMember(UUID neuronId) {
        if (!members.contains(neuronId)) {
            members.add(neuronId);
        }
    }
    
    /**
     * Remove a neuron from rich club.
     */
    public void removeMember(UUID neuronId) {
        members.remove(neuronId);
    }
    
    /**
     * Clear all members.
     */
    public void clear() {
        members.clear();
        richClubCoefficient = 0.0;
    }
    
    /**
     * Get statistics about the rich club.
     */
    public RichClubStatistics getStatistics() {
        return new RichClubStatistics(
            members.size(),
            threshold,
            richClubCoefficient
        );
    }
    
    @Override
    public String toString() {
        return "RichClub{" +
            "members=" + members.size() +
            ", threshold=" + String.format("%.1f%%", threshold * 100) +
            ", coefficient=" + String.format("%.3f", richClubCoefficient) +
            '}';
    }
    
    /**
     * RichClubStatistics - Statistics about the rich club.
     */
    public static class RichClubStatistics {
        public final int memberCount;
        public final double threshold;
        public final double coefficient;
        
        public RichClubStatistics(int memberCount, double threshold, double coefficient) {
            this.memberCount = memberCount;
            this.threshold = threshold;
            this.coefficient = coefficient;
        }
        
        @Override
        public String toString() {
            return "RichClubStatistics{" +
                "memberCount=" + memberCount +
                ", threshold=" + String.format("%.1f%%", threshold * 100) +
                ", coefficient=" + String.format("%.3f", coefficient) +
                '}';
        }
    }
}
