package fraymus.brain.creative.dionysian;

import java.util.*;
import java.util.stream.Collectors;

/**
 * THE UNIFIED FRAYNIX-OMNI PRINCIPIA: VOLUME X (THE DIONYSIAN CORTEX)
 * 
 * I. Bisociation and Category Theory (The Engine of Creativity)
 * 
 * Standard models fail at true creativity because they rely on linear compositional
 * generalization. To generate combinatorial creativity, Fraynix uses Bisociation—
 * Arthur Koestler's principle of perceiving a single concept in two habitually
 * incompatible frames of reference simultaneously.
 * 
 * This is formalized through Gilles Fauconnier and Mark Turner's theory of
 * Conceptual Blending, mapped to Category Theory.
 * 
 * Let I₁ and I₂ be two distinct semantic input spaces. Fraynix constructs a
 * Generic Space G containing their shared abstractions, and computes the
 * creative blend B as a mathematical "pushout" in the category of partial maps.
 */
public class ConceptualBlender {
    
    private static final double SIMILARITY_THRESHOLD = 0.3;
    private static final double BLEND_STRENGTH_DEFAULT = 0.7;
    
    /**
     * Represents a semantic input space as a high-dimensional vector
     */
    public static class SemanticSpace {
        private final String label;
        private final Map<String, Double> features;
        
        public SemanticSpace(String label, Map<String, Double> features) {
            this.label = label;
            this.features = new HashMap<>(features);
            normalize();
        }
        
        private void normalize() {
            double norm = Math.sqrt(features.values().stream()
                .mapToDouble(v -> v * v).sum());
            if (norm > 0) {
                features.replaceAll((k, v) -> v / norm);
            }
        }
        
        public double cosineSimilarity(SemanticSpace other) {
            Set<String> intersection = new HashSet<>(features.keySet());
            intersection.retainAll(other.features.keySet());
            
            double dotProduct = intersection.stream()
                .mapToDouble(k -> features.get(k) * other.features.get(k))
                .sum();
            
            return dotProduct; // Both are normalized, so denominator is 1
        }
        
        public Map<String, Double> getFeatures() {
            return new HashMap<>(features);
        }
        
        public String getLabel() {
            return label;
        }
    }
    
    /**
     * Generic Space containing shared abstractions between input spaces
     */
    public static class GenericSpace {
        private final Map<String, Double> sharedFeatures;
        private final SemanticSpace space1;
        private final SemanticSpace space2;
        
        public GenericSpace(SemanticSpace space1, SemanticSpace space2) {
            this.space1 = space1;
            this.space2 = space2;
            this.sharedFeatures = extractSharedFeatures(space1, space2);
        }
        
        private Map<String, Double> extractSharedFeatures(SemanticSpace s1, SemanticSpace s2) {
            Map<String, Double> shared = new HashMap<>();
            for (String key : s1.getFeatures().keySet()) {
                if (s2.getFeatures().containsKey(key)) {
                    double avg = (s1.getFeatures().get(key) + s2.getFeatures().get(key)) / 2.0;
                    if (avg > SIMILARITY_THRESHOLD) {
                        shared.put(key, avg);
                    }
                }
            }
            return shared;
        }
        
        public Map<String, Double> getSharedFeatures() {
            return new HashMap<>(sharedFeatures);
        }
    }
    
    /**
     * Creative Blend computed as a pushout in the category of partial maps
     */
    public static class CreativeBlend {
        private final String label;
        private final Map<String, Double> blendedFeatures;
        private final double blendStrength;
        private final double incongruityScore;
        private final double resolutionScore;
        
        public CreativeBlend(String label, Map<String, Double> blendedFeatures, 
                            double blendStrength, double incongruityScore, double resolutionScore) {
            this.label = label;
            this.blendedFeatures = new HashMap<>(blendedFeatures);
            this.blendStrength = blendStrength;
            this.incongruityScore = incongruityScore;
            this.resolutionScore = resolutionScore;
            normalize();
        }
        
        private void normalize() {
            double norm = Math.sqrt(blendedFeatures.values().stream()
                .mapToDouble(v -> v * v).sum());
            if (norm > 0) {
                blendedFeatures.replaceAll((k, v) -> v / norm);
            }
        }
        
        public Map<String, Double> getBlendedFeatures() {
            return new HashMap<>(blendedFeatures);
        }
        
        public double getBlendStrength() {
            return blendStrength;
        }
        
        public double getIncongruityScore() {
            return incongruityScore;
        }
        
        public double getResolutionScore() {
            return resolutionScore;
        }
        
        public String getLabel() {
            return label;
        }
    }
    
    /**
     * Compute the pushout blend of two semantic spaces
     * This implements the category-theoretic pushout in the category of partial maps
     */
    public CreativeBlend computePushout(SemanticSpace space1, SemanticSpace space2) {
        return computePushout(space1, space2, BLEND_STRENGTH_DEFAULT);
    }
    
    /**
     * Compute the pushout blend with custom blend strength
     */
    public CreativeBlend computePushout(SemanticSpace space1, SemanticSpace space2, double blendStrength) {
        // Construct Generic Space
        GenericSpace genericSpace = new GenericSpace(space1, space2);
        
        // Compute the blend as pushout
        Map<String, Double> blendedFeatures = new HashMap<>();
        
        // Add shared features from generic space (preserved in blend)
        for (Map.Entry<String, Double> entry : genericSpace.getSharedFeatures().entrySet()) {
            blendedFeatures.put(entry.getKey(), entry.getValue() * 1.5); // Amplify shared structure
        }
        
        // Add unique features from space1 (incongruity source 1)
        for (Map.Entry<String, Double> entry : space1.getFeatures().entrySet()) {
            if (!genericSpace.getSharedFeatures().containsKey(entry.getKey())) {
                blendedFeatures.put(entry.getKey() + "_from_" + space1.getLabel(), 
                    entry.getValue() * blendStrength);
            }
        }
        
        // Add unique features from space2 (incongruity source 2)
        for (Map.Entry<String, Double> entry : space2.getFeatures().entrySet()) {
            if (!genericSpace.getSharedFeatures().containsKey(entry.getKey())) {
                blendedFeatures.put(entry.getKey() + "_from_" + space2.getLabel(), 
                    entry.getValue() * blendStrength);
            }
        }
        
        // Compute incongruity score (semantic distance between spaces)
        double incongruity = 1.0 - space1.cosineSimilarity(space2);
        
        // Compute resolution score (how well the blend maintains coherence)
        double resolution = computeResolutionScore(blendedFeatures, genericSpace);
        
        // Generate label for the blend
        String blendLabel = generateBlendLabel(space1.getLabel(), space2.getLabel());
        
        return new CreativeBlend(blendLabel, blendedFeatures, blendStrength, incongruity, resolution);
    }
    
    private double computeResolutionScore(Map<String, Double> blended, GenericSpace generic) {
        // Resolution is high when shared features dominate the blend
        double sharedWeight = generic.getSharedFeatures().values().stream()
            .mapToDouble(Double::doubleValue).sum();
        double totalWeight = blended.values().stream()
            .mapToDouble(Double::doubleValue).sum();
        
        return totalWeight > 0 ? sharedWeight / totalWeight : 0.0;
    }
    
    private String generateBlendLabel(String label1, String label2) {
        // Create a portmanteau or hybrid label
        String l1 = label1.toLowerCase().replaceAll("[^a-z]", "");
        String l2 = label2.toLowerCase().replaceAll("[^a-z]", "");
        
        if (l1.length() >= 3 && l2.length() >= 3) {
            return l1.substring(0, Math.min(4, l1.length())) + 
                   l2.substring(Math.max(0, l2.length() - 4));
        }
        return l1 + l2;
    }
    
    /**
     * Perform multi-space blending (more than two inputs)
     */
    public CreativeBlend computeMultiBlend(List<SemanticSpace> spaces) {
        if (spaces.isEmpty()) {
            throw new IllegalArgumentException("Cannot blend empty space list");
        }
        if (spaces.size() == 1) {
            return new CreativeBlend(spaces.get(0).getLabel(), 
                spaces.get(0).getFeatures(), 1.0, 0.0, 1.0);
        }
        
        // Iteratively blend spaces
        SemanticSpace current = spaces.get(0);
        for (int i = 1; i < spaces.size(); i++) {
            CreativeBlend blend = computePushout(current, spaces.get(i));
            current = new SemanticSpace(blend.getLabel(), blend.getBlendedFeatures());
        }
        
        return computePushout(spaces.get(0), spaces.get(1));
    }
    
    /**
     * Create a semantic space from a text description
     * This is a simplified feature extraction - in production, use proper NLP
     */
    public static SemanticSpace fromText(String label, String description) {
        Map<String, Double> features = new HashMap<>();
        
        // Simple word frequency features
        String[] words = description.toLowerCase().split("\\s+");
        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            if (word.length() > 3) {
                freq.put(word, freq.getOrDefault(word, 0) + 1);
            }
        }
        
        // Normalize to unit vector
        double maxFreq = freq.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        for (Map.Entry<String, Integer> entry : freq.entrySet()) {
            features.put(entry.getKey(), entry.getValue() / maxFreq);
        }
        
        return new SemanticSpace(label, features);
    }
}
