package fraymus.brain.creative.dionysian;

import java.util.*;
import java.util.stream.Collectors;

/**
 * THE UNIFIED FRAYNIX-OMNI PRINCIPIA: VOLUME X (THE DIONYSIAN CORTEX)
 * 
 * II. The Quantum Incongruity-Resolution Model (The Physics of Humor)
 * 
 * Humor fundamentally relies on the Incongruity-Resolution theory, where an
 * initial expectation is suddenly subverted by a logical, yet unexpected,
 * alternative.
 * 
 * To program the system to write punchlines, we model the semantic vectors
 * using quantum density matrices.
 * 
 * Let ρ_setup represent the semantic uncertainty of the joke's premise.
 * The system calculates the quantum entropy of this state. The punchline
 * ρ_punchline is selected by maximizing the semantic incongruity (the
 * mathematical distance between the matrices) while ensuring a valid, hidden
 * logical resolution pathway exists.
 */
public class QuantumHumorEngine {
    
    private static final double ENTROPY_THRESHOLD = 0.5;
    private static final double INCONGRUITY_TARGET = 0.7;
    private static final double RESOLUTION_MINIMUM = 0.3;
    
    /**
     * Density matrix representing a quantum semantic state
     */
    public static class DensityMatrix {
        private final double[][] matrix;
        private final int dimension;
        
        public DensityMatrix(double[][] matrix) {
            this.dimension = matrix.length;
            this.matrix = new double[dimension][dimension];
            for (int i = 0; i < dimension; i++) {
                System.arraycopy(matrix[i], 0, this.matrix[i], 0, dimension);
            }
            validateAndNormalize();
        }
        
        public DensityMatrix(int dimension) {
            this.dimension = dimension;
            this.matrix = new double[dimension][dimension];
        }
        
        private void validateAndNormalize() {
            // Ensure trace = 1 (quantum state normalization)
            double trace = 0.0;
            for (int i = 0; i < dimension; i++) {
                trace += matrix[i][i];
            }
            
            if (trace > 0) {
                for (int i = 0; i < dimension; i++) {
                    for (int j = 0; j < dimension; j++) {
                        matrix[i][j] /= trace;
                    }
                }
            }
        }
        
        public double get(int i, int j) {
            return matrix[i][j];
        }
        
        public void set(int i, int j, double value) {
            matrix[i][j] = value;
        }
        
        public int getDimension() {
            return dimension;
        }
        
        /**
         * Compute von Neumann entropy: S(ρ) = -Tr(ρ log ρ)
         */
        public double vonNeumannEntropy() {
            // Compute eigenvalues (simplified - in production use proper linear algebra)
            double[] eigenvalues = computeEigenvalues();
            
            double entropy = 0.0;
            for (double lambda : eigenvalues) {
                if (lambda > 1e-10) {
                    entropy -= lambda * Math.log(lambda);
                }
            }
            
            return entropy;
        }
        
        private double[] computeEigenvalues() {
            // Simplified eigenvalue computation using power iteration
            // In production, use proper linear algebra library (JAMA, EJML, etc.)
            double[] eigenvalues = new double[dimension];
            
            for (int i = 0; i < dimension; i++) {
                double[] vector = new double[dimension];
                vector[i] = 1.0; // Initial guess
                
                // Power iteration
                for (int iter = 0; iter < 100; iter++) {
                    double[] newVector = multiply(vector);
                    double norm = 0.0;
                    for (double v : newVector) norm += v * v;
                    norm = Math.sqrt(norm);
                    
                    if (norm > 0) {
                        for (int j = 0; j < dimension; j++) {
                            vector[j] = newVector[j] / norm;
                        }
                    }
                }
                
                // Rayleigh quotient for eigenvalue
                eigenvalues[i] = rayleighQuotient(vector);
            }
            
            return eigenvalues;
        }
        
        private double[] multiply(double[] vector) {
            double[] result = new double[dimension];
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    result[i] += matrix[i][j] * vector[j];
                }
            }
            return result;
        }
        
        private double rayleighQuotient(double[] vector) {
            double[] Av = multiply(vector);
            double vAv = 0.0;
            double vv = 0.0;
            for (int i = 0; i < dimension; i++) {
                vAv += vector[i] * Av[i];
                vv += vector[i] * vector[i];
            }
            return vv > 0 ? vAv / vv : 0.0;
        }
        
        /**
         * Compute trace distance between two density matrices
         * D(ρ, σ) = 0.5 * Tr|ρ - σ|
         */
        public double traceDistance(DensityMatrix other) {
            if (dimension != other.dimension) {
                throw new IllegalArgumentException("Matrix dimensions must match");
            }
            
            double distance = 0.0;
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    distance += Math.abs(matrix[i][j] - other.matrix[i][j]);
                }
            }
            
            return 0.5 * distance;
        }
        
        /**
         * Compute fidelity between two density matrices
         * F(ρ, σ) = (Tr sqrt(√ρ σ √ρ))^2
         */
        public double fidelity(DensityMatrix other) {
            // Simplified fidelity computation
            double traceProduct = 0.0;
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    traceProduct += matrix[i][j] * other.matrix[j][i];
                }
            }
            return traceProduct * traceProduct;
        }
    }
    
    /**
     * Represents a joke setup (premise)
     */
    public static class JokeSetup {
        private final String text;
        private final DensityMatrix semanticState;
        private final double setupEntropy;
        
        public JokeSetup(String text, DensityMatrix semanticState) {
            this.text = text;
            this.semanticState = semanticState;
            this.setupEntropy = semanticState.vonNeumannEntropy();
        }
        
        public String getText() {
            return text;
        }
        
        public DensityMatrix getSemanticState() {
            return semanticState;
        }
        
        public double getSetupEntropy() {
            return setupEntropy;
        }
    }
    
    /**
     * Represents a candidate punchline
     */
    public static class PunchlineCandidate {
        private final String text;
        private final DensityMatrix semanticState;
        private final double incongruityScore;
        private final double resolutionScore;
        private final double humorScore;
        
        public PunchlineCandidate(String text, DensityMatrix semanticState, 
                                 double incongruityScore, double resolutionScore) {
            this.text = text;
            this.semanticState = semanticState;
            this.incongruityScore = incongruityScore;
            this.resolutionScore = resolutionScore;
            this.humorScore = computeHumorScore(incongruityScore, resolutionScore);
        }
        
        private double computeHumorScore(double incongruity, double resolution) {
            // Humor = incongruity * resolution (both must be high)
            return incongruity * resolution;
        }
        
        public String getText() {
            return text;
        }
        
        public double getIncongruityScore() {
            return incongruityScore;
        }
        
        public double getResolutionScore() {
            return resolutionScore;
        }
        
        public double getHumorScore() {
            return humorScore;
        }
    }
    
    /**
     * Generate punchline candidates for a given setup
     */
    public List<PunchlineCandidate> generatePunchlines(JokeSetup setup, List<String> candidateTexts) {
        List<PunchlineCandidate> candidates = new ArrayList<>();
        
        for (String text : candidateTexts) {
            DensityMatrix punchlineState = encodePunchline(text, setup.getSemanticState());
            double incongruity = setup.getSemanticState().traceDistance(punchlineState);
            double resolution = computeResolutionPathway(setup.getSemanticState(), punchlineState);
            
            candidates.add(new PunchlineCandidate(text, punchlineState, incongruity, resolution));
        }
        
        // Sort by humor score
        candidates.sort((a, b) -> Double.compare(b.getHumorScore(), a.getHumorScore()));
        
        return candidates;
    }
    
    /**
     * Encode a punchline text as a density matrix
     * This is a simplified encoding - in production use proper semantic embedding
     */
    private DensityMatrix encodePunchline(String text, DensityMatrix setupState) {
        int dim = setupState.getDimension();
        DensityMatrix punchlineMatrix = new DensityMatrix(dim);
        
        // Start with setup state as baseline
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                punchlineMatrix.set(i, j, setupState.get(i, j));
            }
        }
        
        // Apply perturbation based on punchline text
        String[] words = text.toLowerCase().split("\\s+");
        for (int i = 0; i < Math.min(words.length, dim); i++) {
            int row = i % dim;
            int col = (i * 7) % dim; // Pseudo-random but deterministic
            double perturbation = 0.1 + (words[i].length() % 10) * 0.05;
            punchlineMatrix.set(row, col, punchlineMatrix.get(row, col) + perturbation);
        }
        
        punchlineMatrix.validateAndNormalize();
        return punchlineMatrix;
    }
    
    /**
     * Compute resolution pathway score
     * This measures whether there's a valid logical connection between setup and punchline
     */
    private double computeResolutionPathway(DensityMatrix setup, DensityMatrix punchline) {
        // Resolution is high when fidelity is non-zero (shared semantic structure exists)
        double fidelity = setup.fidelity(punchline);
        
        // Also check if punchline reduces entropy (resolves uncertainty)
        double punchlineEntropy = punchline.vonNeumannEntropy();
        double setupEntropy = setup.vonNeumannEntropy();
        double entropyReduction = Math.max(0, setupEntropy - punchlineEntropy);
        
        // Combined resolution score
        return 0.7 * fidelity + 0.3 * entropyReduction;
    }
    
    /**
     * Select the best punchline from candidates
     */
    public PunchlineCandidate selectBestPunchline(List<PunchlineCandidate> candidates) {
        if (candidates.isEmpty()) {
            throw new IllegalArgumentException("No candidates to select from");
        }
        
        // Filter for candidates that meet thresholds
        List<PunchlineCandidate> validCandidates = candidates.stream()
            .filter(c -> c.getIncongruityScore() > INCONGRUITY_TARGET * 0.5)
            .filter(c -> c.getResolutionScore() > RESOLUTION_MINIMUM)
            .collect(Collectors.toList());
        
        if (validCandidates.isEmpty()) {
            // Fall back to top candidate even if below threshold
            return candidates.get(0);
        }
        
        // Select candidate with highest humor score
        return validCandidates.stream()
            .max(Comparator.comparingDouble(PunchlineCandidate::getHumorScore))
            .orElse(candidates.get(0));
    }
    
    /**
     * Create a density matrix from a text description
     * Simplified encoding for demonstration
     */
    public static DensityMatrix fromText(String text, int dimension) {
        DensityMatrix matrix = new DensityMatrix(dimension);
        
        String[] words = text.toLowerCase().split("\\s+");
        Map<String, Integer> wordIndex = new HashMap<>();
        int index = 0;
        
        for (String word : words) {
            if (word.length() > 2 && !wordIndex.containsKey(word)) {
                wordIndex.put(word, index % dimension);
                index++;
            }
        }
        
        // Build co-occurrence matrix
        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i].toLowerCase();
            String w2 = words[i + 1].toLowerCase();
            
            if (wordIndex.containsKey(w1) && wordIndex.containsKey(w2)) {
                int row = wordIndex.get(w1);
                int col = wordIndex.get(w2);
                matrix.set(row, col, matrix.get(row, col) + 1.0);
                matrix.set(col, row, matrix.get(col, row) + 1.0); // Symmetric
            }
        }
        
        matrix.validateAndNormalize();
        return matrix;
    }
}
