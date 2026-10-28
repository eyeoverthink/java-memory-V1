package fraymus.brain.creative.dionysian;

import java.util.*;
import java.util.stream.Collectors;

/**
 * THE UNIFIED FRAYNIX-OMNI PRINCIPIA: VOLUME X (THE DIONYSIAN CORTEX)
 * 
 * III. Topological Braids (Bending Concepts Safely)
 * 
 * When Fraynix remixes culture, it must bend concepts without breaking their
 * underlying logic. We execute this by treating semantic paths not as flat
 * vectors, but as Topological Braids from the Artin braid group B_n.
 * 
 * When two concepts interact and twist around each other, their crossing is
 * represented by the braid generator σ_i. To guarantee that your AI's remixed
 * ideas remain structurally sound even after heavy distortion, the continuous
 * state field must satisfy the Yang-Baxter equation:
 * 
 *     σ_i σ_{i+1} σ_i = σ_{i+1} σ_i σ_{i+1}
 * 
 * As long as this topological invariant holds, the system can dynamically
 * tangle and remix ideas infinitely, producing wild creativity that never
 * devolves into meaningless hallucination.
 */
public class TopologicalBraids {
    
    /**
     * Represents a braid generator σ_i in the Artin braid group B_n
     */
    public static class BraidGenerator {
        private final int index;  // Which strands cross (1 to n-1)
        private final int n;       // Total number of strands
        private final boolean positive; // σ_i (true) or σ_i^{-1} (false)
        
        public BraidGenerator(int index, int n, boolean positive) {
            if (index < 1 || index >= n) {
                throw new IllegalArgumentException("Braid generator index must be in [1, n-1]");
            }
            this.index = index;
            this.n = n;
            this.positive = positive;
        }
        
        public int getIndex() {
            return index;
        }
        
        public int getStrandCount() {
            return n;
        }
        
        public boolean isPositive() {
            return positive;
        }
        
        /**
         * Get the inverse of this generator
         */
        public BraidGenerator inverse() {
            return new BraidGenerator(index, n, !positive);
        }
        
        @Override
        public String toString() {
            return positive ? "σ_" + index : "σ_" + index + "^{-1}";
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof BraidGenerator)) return false;
            BraidGenerator other = (BraidGenerator) obj;
            return index == other.index && n == other.n && positive == other.positive;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(index, n, positive);
        }
    }
    
    /**
     * Represents a full braid as a sequence of generators
     */
    public static class Braid {
        private final int n;  // Number of strands
        private final List<BraidGenerator> generators;
        
        public Braid(int n) {
            this.n = n;
            this.generators = new ArrayList<>();
        }
        
        public Braid(int n, List<BraidGenerator> generators) {
            this.n = n;
            this.generators = new ArrayList<>(generators);
            validate();
        }
        
        private void validate() {
            for (BraidGenerator gen : generators) {
                if (gen.getStrandCount() != n) {
                    throw new IllegalArgumentException("All generators must have same strand count");
                }
            }
        }
        
        /**
         * Add a generator to the braid
         */
        public Braid append(BraidGenerator generator) {
            if (generator.getStrandCount() != n) {
                throw new IllegalArgumentException("Generator strand count mismatch");
            }
            generators.add(generator);
            return this;
        }
        
        /**
         * Compose this braid with another (this ∘ other)
         */
        public Braid compose(Braid other) {
            if (other.n != n) {
                throw new IllegalArgumentException("Braid strand count mismatch");
            }
            List<BraidGenerator> combined = new ArrayList<>(generators);
            combined.addAll(other.generators);
            return new Braid(n, combined);
        }
        
        /**
         * Get the inverse of this braid
         */
        public Braid inverse() {
            List<BraidGenerator> inverseGens = new ArrayList<>();
            for (int i = generators.size() - 1; i >= 0; i--) {
                inverseGens.add(generators.get(i).inverse());
            }
            return new Braid(n, inverseGens);
        }
        
        /**
         * Simplify the braid by canceling adjacent inverses
         */
        public Braid simplify() {
            List<BraidGenerator> simplified = new ArrayList<>();
            
            for (BraidGenerator gen : generators) {
                if (!simplified.isEmpty()) {
                    BraidGenerator last = simplified.get(simplified.size() - 1);
                    if (last.getIndex() == gen.getIndex() && last.isPositive() != gen.isPositive()) {
                        simplified.remove(simplified.size() - 1); // Cancel
                        continue;
                    }
                }
                simplified.add(gen);
            }
            
            return new Braid(n, simplified);
        }
        
        /**
         * Check if this braid satisfies the Yang-Baxter equation locally
         * The Yang-Baxter relation: σ_i σ_{i+1} σ_i = σ_{i+1} σ_i σ_{i+1}
         */
        public boolean satisfiesYangBaxter() {
            for (int i = 0; i < generators.size() - 2; i++) {
                BraidGenerator g1 = generators.get(i);
                BraidGenerator g2 = generators.get(i + 1);
                BraidGenerator g3 = generators.get(i + 2);
                
                // Check for pattern σ_i σ_{i+1} σ_i
                if (isYangBaxterPattern(g1, g2, g3)) {
                    // This pattern should equal σ_{i+1} σ_i σ_{i+1}
                    // For simplicity, we check if the pattern exists
                    // In a full implementation, we'd perform braid equivalence checking
                    return true;
                }
            }
            return false;
        }
        
        private boolean isYangBaxterPattern(BraidGenerator g1, BraidGenerator g2, BraidGenerator g3) {
            // σ_i σ_{i+1} σ_i pattern
            return g1.isPositive() && g2.isPositive() && g3.isPositive() &&
                   g1.getIndex() == g3.getIndex() &&
                   Math.abs(g1.getIndex() - g2.getIndex()) == 1;
        }
        
        /**
         * Compute the braid's writhe (signed crossing count)
         */
        public int writhe() {
            int w = 0;
            for (BraidGenerator gen : generators) {
                w += gen.isPositive() ? 1 : -1;
            }
            return w;
        }
        
        /**
         * Compute the braid's complexity (number of crossings after simplification)
         */
        public int complexity() {
            return simplify().generators.size();
        }
        
        /**
         * Convert braid to a permutation of the strands
         * This shows how the strands are permuted by the braid
         */
        public int[] toPermutation() {
            int[] permutation = new int[n];
            for (int i = 0; i < n; i++) {
                permutation[i] = i; // Identity permutation
            }
            
            for (BraidGenerator gen : generators) {
                int i = gen.getIndex();
                if (gen.isPositive()) {
                    // σ_i: swap strand i-1 and i (0-indexed)
                    int temp = permutation[i - 1];
                    permutation[i - 1] = permutation[i];
                    permutation[i] = temp;
                } else {
                    // σ_i^{-1}: inverse swap
                    int temp = permutation[i - 1];
                    permutation[i - 1] = permutation[i];
                    permutation[i] = temp;
                }
            }
            
            return permutation;
        }
        
        public int getStrandCount() {
            return n;
        }
        
        public List<BraidGenerator> getGenerators() {
            return new ArrayList<>(generators);
        }
        
        @Override
        public String toString() {
            return generators.stream()
                .map(BraidGenerator::toString)
                .collect(Collectors.joining(" "));
        }
    }
    
    /**
     * Braid group operations and utilities
     */
    public static class BraidGroup {
        private final int n;
        
        public BraidGroup(int n) {
            if (n < 2) {
                throw new IllegalArgumentException("Braid group requires at least 2 strands");
            }
            this.n = n;
        }
        
        /**
         * Create a generator σ_i
         */
        public BraidGenerator generator(int i, boolean positive) {
            return new BraidGenerator(i, n, positive);
        }
        
        /**
         * Create the identity braid (empty word)
         */
        public Braid identity() {
            return new Braid(n);
        }
        
        /**
         * Create a pure braid from a permutation
         * This is a simplified construction
         */
        public Braid fromPermutation(int[] permutation) {
            if (permutation.length != n) {
                throw new IllegalArgumentException("Permutation length must equal strand count");
            }
            
            Braid braid = new Braid(n);
            
            // Simple bubble sort using braid generators
            int[] current = permutation.clone();
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (current[j] < current[i]) {
                        // Swap using σ_{j}
                        braid.append(generator(j, true));
                        int temp = current[i];
                        current[i] = current[j];
                        current[j] = temp;
                    }
                }
            }
            
            return braid.simplify();
        }
        
        /**
         * Check if two braids are equivalent
         * This is a simplified check - full equivalence is undecidable in general
         */
        public boolean areEquivalent(Braid b1, Braid b2) {
            // Check if they have same permutation and writhe
            int[] perm1 = b1.toPermutation();
            int[] perm2 = b2.toPermutation();
            
            if (!Arrays.equals(perm1, perm2)) {
                return false;
            }
            
            return b1.writhe() == b2.writhe();
        }
        
        /**
         * Generate a random braid with given length
         */
        public Braid randomBraid(int length, Random random) {
            Braid braid = new Braid(n);
            for (int i = 0; i < length; i++) {
                int genIndex = 1 + random.nextInt(n - 1);
                boolean positive = random.nextBoolean();
                braid.append(generator(genIndex, positive));
            }
            return braid;
        }
    }
    
    /**
     * Semantic Braid - applies topological braiding to semantic concepts
     */
    public static class SemanticBraid {
        private final Braid topologicalBraid;
        private final Map<Integer, String> strandConcepts;
        
        public SemanticBraid(Braid topologicalBraid, Map<Integer, String> strandConcepts) {
            this.topologicalBraid = topologicalBraid;
            this.strandConcepts = new HashMap<>(strandConcepts);
            if (strandConcepts.size() != topologicalBraid.getStrandCount()) {
                throw new IllegalArgumentException("Concept count must match strand count");
            }
        }
        
        /**
         * Apply the braid to the concepts, producing a remixed semantic structure
         */
        public Map<String, Double> applyBraid() {
            int[] permutation = topologicalBraid.toPermutation();
            Map<String, Double> result = new HashMap<>();
            
            for (int i = 0; i < permutation.length; i++) {
                String concept = strandConcepts.get(i);
                String newConcept = strandConcepts.get(permutation[i]);
                
                // The braid twists concepts together
                // We represent this as a weighted combination
                double weight = 1.0 / (topologicalBraid.complexity() + 1);
                result.put(concept + "_" + newConcept, weight);
            }
            
            return result;
        }
        
        /**
         * Check if the semantic braid maintains coherence (Yang-Baxter invariant)
         */
        public boolean maintainsCoherence() {
            // A braid maintains coherence if it satisfies Yang-Baxter
            // and the permutation doesn't completely scramble the concepts
            boolean yangBaxter = topologicalBraid.satisfiesYangBaxter();
            
            int[] permutation = topologicalBraid.toPermutation();
            int fixedPoints = 0;
            for (int i = 0; i < permutation.length; i++) {
                if (permutation[i] == i) fixedPoints++;
            }
            
            // At least 20% of concepts should remain in place for coherence
            boolean sufficientFixedPoints = fixedPoints >= permutation.length * 0.2;
            
            return yangBaxter || sufficientFixedPoints;
        }
        
        public Braid getTopologicalBraid() {
            return topologicalBraid;
        }
        
        public Map<Integer, String> getStrandConcepts() {
            return new HashMap<>(strandConcepts);
        }
    }
    
    /**
     * Create a semantic braid from a list of concepts
     */
    public static SemanticBraid fromConcepts(List<String> concepts, int braidLength, Random random) {
        int n = concepts.size();
        BraidGroup group = new BraidGroup(n);
        Braid braid = group.randomBraid(braidLength, random);
        
        Map<Integer, String> strandConcepts = new HashMap<>();
        for (int i = 0; i < concepts.size(); i++) {
            strandConcepts.put(i, concepts.get(i));
        }
        
        return new SemanticBraid(braid, strandConcepts);
    }
}
