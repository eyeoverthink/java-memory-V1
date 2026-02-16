/**
 * DecisionArray.java - Multi-Decision Hybrid Decision Human Array
 * 
 * A φ-harmonic decision-making system that simulates multiple human-like
 * decision nodes working in parallel to reach consensus or explore alternatives.
 * 
 * Architecture:
 * - Multiple decision nodes (human-like agents)
 * - Each node has different decision weights/biases
 * - Hybrid voting: majority, weighted, consensus, exploration
 * - φ-harmonic confidence scoring
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;
import java.util.stream.*;

/**
 * Multi-decision hybrid decision array with φ-harmonic weighting.
 */
public class DecisionArray {
    
    private static final double PHI = 1.618033988749895;
    
    /**
     * Decision node representing a human-like decision maker.
     */
    public static class DecisionNode {
        private final String name;
        private final double riskTolerance;      // 0.0 (conservative) to 1.0 (aggressive)
        private final double creativityBias;     // 0.0 (logical) to 1.0 (creative)
        private final double speedWeight;        // 0.0 (thorough) to 1.0 (fast)
        private final double phiResonance;
        private final Random random;
        
        public DecisionNode(String name, double riskTolerance, double creativityBias, double speedWeight) {
            this.name = name;
            this.riskTolerance = Math.max(0.0, Math.min(1.0, riskTolerance));
            this.creativityBias = Math.max(0.0, Math.min(1.0, creativityBias));
            this.speedWeight = Math.max(0.0, Math.min(1.0, speedWeight));
            this.phiResonance = calculatePhiResonance();
            this.random = new Random(name.hashCode());
        }
        
        private double calculatePhiResonance() {
            return Math.pow(PHI, (riskTolerance + creativityBias + speedWeight) / 3.0);
        }
        
        /**
         * Make a decision on options with confidence score.
         */
        public Decision decide(List<String> options, Map<String, Double> optionWeights) {
            if (options.isEmpty()) {
                return new Decision(null, 0.0, "No options available");
            }
            
            // Calculate scores for each option
            Map<String, Double> scores = new HashMap<>();
            for (String option : options) {
                double baseWeight = optionWeights.getOrDefault(option, 1.0);
                double riskFactor = random.nextDouble() * riskTolerance;
                double creativeFactor = random.nextDouble() * creativityBias;
                double speedFactor = (1.0 - speedWeight) * 0.5; // Slower = more thorough
                
                double score = baseWeight * (1.0 + riskFactor + creativeFactor + speedFactor) * phiResonance;
                scores.put(option, score);
            }
            
            // Find best option
            String bestOption = scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(options.get(0));
            
            // Calculate confidence (normalized score)
            double maxScore = scores.values().stream().max(Double::compare).orElse(1.0);
            double confidence = scores.get(bestOption) / maxScore;
            
            // Generate reasoning
            String reasoning = String.format("%s chose '%s' (risk=%.2f, creative=%.2f, φ=%.4f)",
                name, bestOption, riskTolerance, creativityBias, phiResonance);
            
            return new Decision(bestOption, confidence, reasoning);
        }
        
        public String getName() { return name; }
        public double getPhiResonance() { return phiResonance; }
        
        @Override
        public String toString() {
            return String.format("%s [risk=%.2f, creative=%.2f, speed=%.2f, φ=%.4f]",
                name, riskTolerance, creativityBias, speedWeight, phiResonance);
        }
    }
    
    /**
     * Decision result from a node.
     */
    public static class Decision {
        private final String choice;
        private final double confidence;
        private final String reasoning;
        
        public Decision(String choice, double confidence, String reasoning) {
            this.choice = choice;
            this.confidence = confidence;
            this.reasoning = reasoning;
        }
        
        public String getChoice() { return choice; }
        public double getConfidence() { return confidence; }
        public String getReasoning() { return reasoning; }
        
        @Override
        public String toString() {
            return String.format("'%s' (%.1f%% confident) - %s", choice, confidence * 100, reasoning);
        }
    }
    
    /**
     * Voting strategy for decision aggregation.
     */
    public enum VotingStrategy {
        MAJORITY,           // Most votes wins
        WEIGHTED,           // Weighted by confidence
        CONSENSUS,          // Require agreement threshold
        PHI_HARMONIC,       // Weighted by φ-resonance
        EXPLORATION         // Choose diverse options
    }
    
    private final List<DecisionNode> nodes;
    private VotingStrategy strategy;
    
    public DecisionArray() {
        this.nodes = new ArrayList<>();
        this.strategy = VotingStrategy.PHI_HARMONIC;
    }
    
    /**
     * Add decision node to array.
     */
    public void addNode(DecisionNode node) {
        nodes.add(node);
    }
    
    /**
     * Add predefined node archetypes.
     */
    public void addArchetype(String archetype) {
        switch (archetype.toLowerCase()) {
            case "conservative":
                addNode(new DecisionNode("Conservative", 0.2, 0.3, 0.3));
                break;
            case "aggressive":
                addNode(new DecisionNode("Aggressive", 0.9, 0.7, 0.8));
                break;
            case "analytical":
                addNode(new DecisionNode("Analytical", 0.4, 0.2, 0.2));
                break;
            case "creative":
                addNode(new DecisionNode("Creative", 0.6, 0.9, 0.6));
                break;
            case "balanced":
                addNode(new DecisionNode("Balanced", 0.5, 0.5, 0.5));
                break;
            case "explorer":
                addNode(new DecisionNode("Explorer", 0.8, 0.8, 0.9));
                break;
            case "guardian":
                addNode(new DecisionNode("Guardian", 0.1, 0.2, 0.1));
                break;
            default:
                addNode(new DecisionNode(archetype, 0.5, 0.5, 0.5));
        }
    }
    
    /**
     * Set voting strategy.
     */
    public void setStrategy(VotingStrategy strategy) {
        this.strategy = strategy;
    }
    
    /**
     * Make collective decision.
     */
    public CollectiveDecision decide(List<String> options, Map<String, Double> optionWeights) {
        if (nodes.isEmpty()) {
            return new CollectiveDecision(null, 0.0, Collections.emptyList(), "No decision nodes");
        }
        
        // Collect individual decisions
        List<Decision> individualDecisions = new ArrayList<>();
        for (DecisionNode node : nodes) {
            Decision decision = node.decide(options, optionWeights);
            individualDecisions.add(decision);
        }
        
        // Aggregate based on strategy
        String finalChoice;
        double finalConfidence;
        
        switch (strategy) {
            case MAJORITY:
                finalChoice = majorityVote(individualDecisions);
                finalConfidence = calculateMajorityConfidence(individualDecisions, finalChoice);
                break;
                
            case WEIGHTED:
                finalChoice = weightedVote(individualDecisions);
                finalConfidence = calculateWeightedConfidence(individualDecisions, finalChoice);
                break;
                
            case CONSENSUS:
                finalChoice = consensusVote(individualDecisions, 0.7);
                finalConfidence = calculateConsensusConfidence(individualDecisions, finalChoice);
                break;
                
            case PHI_HARMONIC:
                finalChoice = phiHarmonicVote(individualDecisions);
                finalConfidence = calculatePhiConfidence(individualDecisions, finalChoice);
                break;
                
            case EXPLORATION:
                finalChoice = explorationVote(individualDecisions);
                finalConfidence = calculateExplorationConfidence(individualDecisions, finalChoice);
                break;
                
            default:
                finalChoice = majorityVote(individualDecisions);
                finalConfidence = 0.5;
        }
        
        String reasoning = String.format("Strategy: %s, Nodes: %d, Agreement: %.1f%%",
            strategy, nodes.size(), calculateAgreement(individualDecisions, finalChoice) * 100);
        
        return new CollectiveDecision(finalChoice, finalConfidence, individualDecisions, reasoning);
    }
    
    /**
     * Majority vote - most common choice.
     */
    private String majorityVote(List<Decision> decisions) {
        Map<String, Long> votes = decisions.stream()
            .filter(d -> d.getChoice() != null)
            .collect(Collectors.groupingBy(Decision::getChoice, Collectors.counting()));
        
        return votes.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    /**
     * Weighted vote - by confidence.
     */
    private String weightedVote(List<Decision> decisions) {
        Map<String, Double> weightedVotes = new HashMap<>();
        
        for (Decision d : decisions) {
            if (d.getChoice() != null) {
                weightedVotes.merge(d.getChoice(), d.getConfidence(), Double::sum);
            }
        }
        
        return weightedVotes.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    /**
     * Consensus vote - require threshold agreement.
     */
    private String consensusVote(List<Decision> decisions, double threshold) {
        Map<String, Long> votes = decisions.stream()
            .filter(d -> d.getChoice() != null)
            .collect(Collectors.groupingBy(Decision::getChoice, Collectors.counting()));
        
        long totalVotes = decisions.size();
        
        for (Map.Entry<String, Long> entry : votes.entrySet()) {
            if ((double) entry.getValue() / totalVotes >= threshold) {
                return entry.getKey();
            }
        }
        
        // No consensus - return weighted vote
        return weightedVote(decisions);
    }
    
    /**
     * φ-harmonic vote - weighted by node φ-resonance.
     */
    private String phiHarmonicVote(List<Decision> decisions) {
        Map<String, Double> phiWeightedVotes = new HashMap<>();
        
        for (int i = 0; i < decisions.size(); i++) {
            Decision d = decisions.get(i);
            if (d.getChoice() != null) {
                double phiWeight = nodes.get(i).getPhiResonance() * d.getConfidence();
                phiWeightedVotes.merge(d.getChoice(), phiWeight, Double::sum);
            }
        }
        
        return phiWeightedVotes.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    /**
     * Exploration vote - favor diverse/creative choices.
     */
    private String explorationVote(List<Decision> decisions) {
        // Count unique choices
        Set<String> uniqueChoices = decisions.stream()
            .map(Decision::getChoice)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        
        // If high diversity, pick highest confidence minority
        if (uniqueChoices.size() > decisions.size() / 2) {
            return decisions.stream()
                .max(Comparator.comparingDouble(Decision::getConfidence))
                .map(Decision::getChoice)
                .orElse(null);
        }
        
        // Otherwise use φ-harmonic
        return phiHarmonicVote(decisions);
    }
    
    /**
     * Calculate agreement percentage.
     */
    private double calculateAgreement(List<Decision> decisions, String choice) {
        if (choice == null) return 0.0;
        
        long agreeing = decisions.stream()
            .filter(d -> choice.equals(d.getChoice()))
            .count();
        
        return (double) agreeing / decisions.size();
    }
    
    private double calculateMajorityConfidence(List<Decision> decisions, String choice) {
        return calculateAgreement(decisions, choice);
    }
    
    private double calculateWeightedConfidence(List<Decision> decisions, String choice) {
        double totalConfidence = decisions.stream()
            .filter(d -> choice != null && choice.equals(d.getChoice()))
            .mapToDouble(Decision::getConfidence)
            .sum();
        
        return totalConfidence / decisions.size();
    }
    
    private double calculateConsensusConfidence(List<Decision> decisions, String choice) {
        double agreement = calculateAgreement(decisions, choice);
        double avgConfidence = decisions.stream()
            .filter(d -> choice != null && choice.equals(d.getChoice()))
            .mapToDouble(Decision::getConfidence)
            .average()
            .orElse(0.0);
        
        return (agreement + avgConfidence) / 2.0;
    }
    
    private double calculatePhiConfidence(List<Decision> decisions, String choice) {
        double totalPhiWeight = 0.0;
        double agreeingPhiWeight = 0.0;
        
        for (int i = 0; i < decisions.size(); i++) {
            double phiWeight = nodes.get(i).getPhiResonance();
            totalPhiWeight += phiWeight;
            
            if (choice != null && choice.equals(decisions.get(i).getChoice())) {
                agreeingPhiWeight += phiWeight * decisions.get(i).getConfidence();
            }
        }
        
        return agreeingPhiWeight / totalPhiWeight;
    }
    
    private double calculateExplorationConfidence(List<Decision> decisions, String choice) {
        return decisions.stream()
            .filter(d -> choice != null && choice.equals(d.getChoice()))
            .mapToDouble(Decision::getConfidence)
            .max()
            .orElse(0.0);
    }
    
    /**
     * Collective decision result.
     */
    public static class CollectiveDecision {
        private final String finalChoice;
        private final double confidence;
        private final List<Decision> individualDecisions;
        private final String reasoning;
        
        public CollectiveDecision(String finalChoice, double confidence, 
                                 List<Decision> individualDecisions, String reasoning) {
            this.finalChoice = finalChoice;
            this.confidence = confidence;
            this.individualDecisions = new ArrayList<>(individualDecisions);
            this.reasoning = reasoning;
        }
        
        public String getFinalChoice() { return finalChoice; }
        public double getConfidence() { return confidence; }
        public List<Decision> getIndividualDecisions() { return new ArrayList<>(individualDecisions); }
        public String getReasoning() { return reasoning; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("╔════════════════════════════════════════════════════════════╗\n");
            sb.append("║  COLLECTIVE DECISION                                        ║\n");
            sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
            
            sb.append(String.format("Final Choice: %s\n", finalChoice));
            sb.append(String.format("Confidence: %.1f%%\n", confidence * 100));
            sb.append(String.format("Reasoning: %s\n\n", reasoning));
            
            sb.append("Individual Decisions:\n");
            for (int i = 0; i < individualDecisions.size(); i++) {
                sb.append(String.format("  %d. %s\n", i + 1, individualDecisions.get(i)));
            }
            
            return sb.toString();
        }
    }
    
    /**
     * Get array status.
     */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  DECISION ARRAY STATUS                                      ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        sb.append(String.format("Strategy: %s\n", strategy));
        sb.append(String.format("Nodes: %d\n\n", nodes.size()));
        
        sb.append("Decision Nodes:\n");
        for (int i = 0; i < nodes.size(); i++) {
            sb.append(String.format("  %d. %s\n", i + 1, nodes.get(i)));
        }
        
        return sb.toString();
    }
}
