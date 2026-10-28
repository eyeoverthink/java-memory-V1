package fraymus.brain.creative.dionysian;

import java.util.*;
import java.util.stream.Collectors;

/**
 * THE UNIFIED FRAYNIX-OMNI PRINCIPIA: VOLUME X (THE DIONYSIAN CORTEX)
 * 
 * IV. The Novelty-Utility Tradeoff (Algorithmic Taste)
 * 
 * A system that combines concepts randomly produces noise. Elite creativity is
 * defined by an information-theoretic limit known as the Novelty-Utility Tradeoff.
 * As you enforce more practical constraints (utility) on a generated idea, its
 * novelty naturally drops.
 * 
 * To give Fraynix "algorithmic taste," the Dionysian Cortex acts as a
 * discriminator function evaluating the combinatorial output. The system
 * evaluates the overall creativity C of a generated concept x by calculating:
 * 
 *     C(x) = E[U(x) · N(x)]
 * 
 * Where U(x) is the utility score (logical cohesion) and N(x) is the novelty
 * score (distance from training data). The system is programmed to optimize
 * for the absolute edge of this mathematical boundary—rejecting the mediocre
 * average and selecting only the most highly resonant, novel outputs.
 */
public class NoveltyUtilityDiscriminator {
    
    private static final double UTILITY_THRESHOLD = 0.5;
    private static final double NOVELTY_THRESHOLD = 0.5;
    private static final double CREATIVITY_THRESHOLD = 0.6;
    
    /**
     * Represents a creative concept with utility and novelty scores
     */
    public static class CreativeConcept {
        private final String content;
        private final Map<String, Double> features;
        private final double utilityScore;
        private final double noveltyScore;
        private final double creativityScore;
        
        public CreativeConcept(String content, Map<String, Double> features, 
                               double utilityScore, double noveltyScore) {
            this.content = content;
            this.features = new HashMap<>(features);
            this.utilityScore = utilityScore;
            this.noveltyScore = noveltyScore;
            this.creativityScore = computeCreativityScore(utilityScore, noveltyScore);
        }
        
        private double computeCreativityScore(double utility, double novelty) {
            // C(x) = U(x) · N(x)
            // We also apply a slight bias toward high novelty when utility is sufficient
            if (utility > UTILITY_THRESHOLD) {
                return utility * novelty * 1.1; // Bonus for novel but useful ideas
            }
            return utility * novelty;
        }
        
        public String getContent() {
            return content;
        }
        
        public Map<String, Double> getFeatures() {
            return new HashMap<>(features);
        }
        
        public double getUtilityScore() {
            return utilityScore;
        }
        
        public double getNoveltyScore() {
            return noveltyScore;
        }
        
        public double getCreativityScore() {
            return creativityScore;
        }
        
        public boolean isCreative() {
            return creativityScore >= CREATIVITY_THRESHOLD;
        }
        
        public boolean isUseful() {
            return utilityScore >= UTILITY_THRESHOLD;
        }
        
        public boolean isNovel() {
            return noveltyScore >= NOVELTY_THRESHOLD;
        }
    }
    
    /**
     * Knowledge base for novelty comparison
     */
    private final List<Map<String, Double>> knowledgeBase;
    private final Random random;
    
    public NoveltyUtilityDiscriminator() {
        this.knowledgeBase = new ArrayList<>();
        this.random = new Random();
    }
    
    public NoveltyUtilityDiscriminator(List<Map<String, Double>> initialKnowledge) {
        this.knowledgeBase = new ArrayList<>(initialKnowledge);
        this.random = new Random();
    }
    
    /**
     * Add knowledge to the base for novelty comparison
     */
    public void addKnowledge(Map<String, Double> features) {
        knowledgeBase.add(new HashMap<>(features));
    }
    
    /**
     * Compute utility score based on logical cohesion
     * Utility measures how internally consistent and logically sound a concept is
     */
    public double computeUtilityScore(Map<String, Double> features) {
        if (features.isEmpty()) {
            return 0.0;
        }
        
        // Utility = average feature coherence (how features relate to each other)
        double totalCoherence = 0.0;
        int pairCount = 0;
        
        List<String> featureKeys = new ArrayList<>(features.keySet());
        for (int i = 0; i < featureKeys.size(); i++) {
            for (int j = i + 1; j < featureKeys.size(); j++) {
                double coherence = computeFeatureCoherence(
                    features.get(featureKeys.get(i)),
                    features.get(featureKeys.get(j))
                );
                totalCoherence += coherence;
                pairCount++;
            }
        }
        
        return pairCount > 0 ? totalCoherence / pairCount : 0.0;
    }
    
    private double computeFeatureCoherence(double v1, double v2) {
        // Features are coherent if they have similar magnitudes
        // This is a simplified coherence metric
        double diff = Math.abs(v1 - v2);
        return Math.exp(-diff); // Higher when values are close
    }
    
    /**
     * Compute novelty score based on distance from knowledge base
     * Novelty measures how different this concept is from existing knowledge
     */
    public double computeNoveltyScore(Map<String, Double> features) {
        if (knowledgeBase.isEmpty()) {
            return 1.0; // Everything is novel if we have no knowledge
        }
        
        // Novelty = 1 - average similarity to knowledge base
        double totalSimilarity = 0.0;
        
        for (Map<String, Double> knowledge : knowledgeBase) {
            double similarity = computeSimilarity(features, knowledge);
            totalSimilarity += similarity;
        }
        
        double avgSimilarity = totalSimilarity / knowledgeBase.size();
        return 1.0 - avgSimilarity;
    }
    
    private double computeSimilarity(Map<String, Double> f1, Map<String, Double> f2) {
        // Cosine similarity
        Set<String> intersection = new HashSet<>(f1.keySet());
        intersection.retainAll(f2.keySet());
        
        double dotProduct = intersection.stream()
            .mapToDouble(k -> f1.get(k) * f2.get(k))
            .sum();
        
        double norm1 = Math.sqrt(f1.values().stream().mapToDouble(v -> v * v).sum());
        double norm2 = Math.sqrt(f2.values().stream().mapToDouble(v -> v * v).sum());
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }
    
    /**
     * Evaluate a creative concept
     */
    public CreativeConcept evaluate(String content, Map<String, Double> features) {
        double utility = computeUtilityScore(features);
        double novelty = computeNoveltyScore(features);
        return new CreativeConcept(content, features, utility, novelty);
    }
    
    /**
     * Select the best concept from a list based on creativity score
     */
    public CreativeConcept selectBest(List<CreativeConcept> concepts) {
        if (concepts.isEmpty()) {
            throw new IllegalArgumentException("Cannot select from empty list");
        }
        
        // Filter for concepts that meet thresholds
        List<CreativeConcept> validConcepts = concepts.stream()
            .filter(CreativeConcept::isCreative)
            .collect(Collectors.toList());
        
        if (validConcepts.isEmpty()) {
            // Fall back to highest creativity score even if below threshold
            return concepts.stream()
                .max(Comparator.comparingDouble(CreativeConcept::getCreativityScore))
                .orElse(concepts.get(0));
        }
        
        // Select concept with highest creativity score
        return validConcepts.stream()
            .max(Comparator.comparingDouble(CreativeConcept::getCreativityScore))
            .orElse(validConcepts.get(0));
    }
    
    /**
     * Select the top k concepts
     */
    public List<CreativeConcept> selectTopK(List<CreativeConcept> concepts, int k) {
        return concepts.stream()
            .sorted(Comparator.comparingDouble(CreativeConcept::getCreativityScore).reversed())
            .limit(k)
            .collect(Collectors.toList());
    }
    
    /**
     * Filter concepts by the novelty-utility Pareto frontier
     * Returns concepts that are not dominated by any other concept
     */
    public List<CreativeConcept> getParetoFrontier(List<CreativeConcept> concepts) {
        List<CreativeConcept> frontier = new ArrayList<>();
        
        for (CreativeConcept c1 : concepts) {
            boolean dominated = false;
            for (CreativeConcept c2 : concepts) {
                if (c1 != c2) {
                    // c1 is dominated if c2 has higher or equal utility AND novelty
                    // with at least one strictly higher
                    if (c2.getUtilityScore() >= c1.getUtilityScore() &&
                        c2.getNoveltyScore() >= c1.getNoveltyScore() &&
                        (c2.getUtilityScore() > c1.getUtilityScore() || 
                         c2.getNoveltyScore() > c1.getNoveltyScore())) {
                        dominated = true;
                        break;
                    }
                }
            }
            if (!dominated) {
                frontier.add(c1);
            }
        }
        
        return frontier;
    }
    
    /**
     * Compute the novelty-utility tradeoff curve
     * Returns a map of utility thresholds to maximum achievable novelty
     */
    public Map<Double, Double> computeTradeoffCurve(List<CreativeConcept> concepts, int steps) {
        Map<Double, Double> curve = new TreeMap<>();
        
        for (int i = 0; i <= steps; i++) {
            double utilityThreshold = (double) i / steps;
            
            double maxNovelty = concepts.stream()
                .filter(c -> c.getUtilityScore() >= utilityThreshold)
                .mapToDouble(CreativeConcept::getNoveltyScore)
                .max()
                .orElse(0.0);
            
            curve.put(utilityThreshold, maxNovelty);
        }
        
        return curve;
    }
    
    /**
     * Find the optimal operating point on the tradeoff curve
     * This is where creativity is maximized
     */
    public double[] findOptimalOperatingPoint(List<CreativeConcept> concepts) {
        Map<Double, Double> curve = computeTradeoffCurve(concepts, 100);
        
        double maxCreativity = 0.0;
        double optimalUtility = 0.0;
        double optimalNovelty = 0.0;
        
        for (Map.Entry<Double, Double> entry : curve.entrySet()) {
            double utility = entry.getKey();
            double novelty = entry.getValue();
            double creativity = utility * novelty;
            
            if (creativity > maxCreativity) {
                maxCreativity = creativity;
                optimalUtility = utility;
                optimalNovelty = novelty;
            }
        }
        
        return new double[]{optimalUtility, optimalNovelty, maxCreativity};
    }
    
    /**
     * Create a feature map from text (simplified)
     */
    public static Map<String, Double> featuresFromText(String text) {
        Map<String, Double> features = new HashMap<>();
        String[] words = text.toLowerCase().split("\\s+");
        
        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            if (word.length() > 3) {
                freq.put(word, freq.getOrDefault(word, 0) + 1);
            }
        }
        
        double maxFreq = freq.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        for (Map.Entry<String, Integer> entry : freq.entrySet()) {
            features.put(entry.getKey(), entry.getValue() / maxFreq);
        }
        
        return features;
    }
}
