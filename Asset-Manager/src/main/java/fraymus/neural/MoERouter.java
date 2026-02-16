package fraymus.neural;

import java.util.Arrays;

/**
 * MIXTURE OF EXPERTS ROUTER
 * The Math: y = Σ G(x)ᵢ Eᵢ(x)
 *   G(x) = Gating Network (The Router)
 *   Eᵢ(x) = Expert Network
 * 
 * The Pattern: 8 expert brains. Router picks 2 best for each prompt.
 * Massive knowledge, low compute.
 * 
 * FRAYMUS Experts: KAI, TriMe, NEXUS, Dark, Logic, Creative, Memory, Security
 */
public class MoERouter {

    private static final double PHI = 1.618033988749895;
    
    private int numExperts;
    private int topK;           // How many experts to activate
    private double[][] gateWeights;
    private Expert[] experts;
    private int inputDim;

    public MoERouter(int inputDim, int numExperts, int topK) {
        this.inputDim = inputDim;
        this.numExperts = numExperts;
        this.topK = topK;
        
        // Initialize gate weights
        this.gateWeights = new double[inputDim][numExperts];
        double scale = Math.sqrt(2.0 / inputDim) / PHI;
        for (int i = 0; i < inputDim; i++) {
            for (int j = 0; j < numExperts; j++) {
                gateWeights[i][j] = (Math.random() - 0.5) * scale;
            }
        }
        
        // Initialize experts
        this.experts = new Expert[numExperts];
        String[] names = {"KAI", "TriMe", "NEXUS", "Dark", "Logic", "Creative", "Memory", "Security"};
        for (int i = 0; i < numExperts; i++) {
            String name = i < names.length ? names[i] : "Expert" + i;
            experts[i] = new Expert(name, inputDim);
        }
    }

    /**
     * Route input to top-K experts and combine outputs
     */
    public double[] forward(double[] input) {
        // Step 1: Compute gate scores G(x)
        double[] gateScores = computeGateScores(input);
        
        // Step 2: Softmax over gate scores
        double[] probs = softmax(gateScores);
        
        // Step 3: Find top-K experts
        int[] topExperts = findTopK(probs, topK);
        
        // Step 4: Normalize top-K probabilities
        double sumTopK = 0;
        for (int idx : topExperts) {
            sumTopK += probs[idx];
        }
        
        // Step 5: Compute weighted sum of expert outputs
        double[] output = new double[inputDim];
        for (int idx : topExperts) {
            double weight = probs[idx] / sumTopK;
            double[] expertOut = experts[idx].process(input);
            for (int i = 0; i < output.length; i++) {
                output[i] += weight * expertOut[i];
            }
        }
        
        return output;
    }

    /**
     * Get which experts were selected for given input
     */
    public String[] getSelectedExperts(double[] input) {
        double[] gateScores = computeGateScores(input);
        double[] probs = softmax(gateScores);
        int[] topExperts = findTopK(probs, topK);
        
        String[] names = new String[topK];
        for (int i = 0; i < topK; i++) {
            names[i] = experts[topExperts[i]].getName() + 
                       String.format(" (%.2f%%)", probs[topExperts[i]] * 100);
        }
        return names;
    }

    private double[] computeGateScores(double[] input) {
        double[] scores = new double[numExperts];
        for (int j = 0; j < numExperts; j++) {
            double sum = 0;
            for (int i = 0; i < inputDim; i++) {
                sum += input[i] * gateWeights[i][j];
            }
            scores[j] = sum;
        }
        return scores;
    }

    private double[] softmax(double[] x) {
        double max = Arrays.stream(x).max().orElse(0);
        double[] exp = new double[x.length];
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            exp[i] = Math.exp(x[i] - max);
            sum += exp[i];
        }
        for (int i = 0; i < x.length; i++) {
            exp[i] /= sum;
        }
        return exp;
    }

    private int[] findTopK(double[] scores, int k) {
        int[] indices = new int[k];
        boolean[] used = new boolean[scores.length];
        
        for (int i = 0; i < k; i++) {
            int maxIdx = -1;
            double maxVal = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < scores.length; j++) {
                if (!used[j] && scores[j] > maxVal) {
                    maxVal = scores[j];
                    maxIdx = j;
                }
            }
            indices[i] = maxIdx;
            used[maxIdx] = true;
        }
        return indices;
    }

    /**
     * Expert network - simple feed-forward
     */
    public static class Expert {
        private String name;
        private double[][] weights;
        private int dim;
        
        public Expert(String name, int dim) {
            this.name = name;
            this.dim = dim;
            this.weights = new double[dim][dim];
            
            // Initialize with identity + small perturbation
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    weights[i][j] = (i == j ? 1.0 : 0.0) + (Math.random() - 0.5) * 0.1;
                }
            }
        }
        
        public double[] process(double[] input) {
            double[] output = new double[dim];
            for (int j = 0; j < dim; j++) {
                double sum = 0;
                for (int i = 0; i < dim; i++) {
                    sum += input[i] * weights[i][j];
                }
                // ReLU activation
                output[j] = Math.max(0, sum);
            }
            return output;
        }
        
        public String getName() { return name; }
    }

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║    MIXTURE OF EXPERTS ROUTER TEST         ║");
        System.out.println("║    y = Σ G(x)ᵢ Eᵢ(x)                      ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        
        MoERouter router = new MoERouter(8, 8, 2);
        
        // Test different inputs
        double[][] testInputs = {
            {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, // Logic-like
            {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, // Memory-like
            {0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5}, // Balanced
        };
        
        String[] testNames = {"Logic query", "Memory query", "Balanced query"};
        
        for (int t = 0; t < testInputs.length; t++) {
            String[] selected = router.getSelectedExperts(testInputs[t]);
            System.out.println("║");
            System.out.println("║  " + testNames[t] + ":");
            System.out.println("║    → " + selected[0]);
            System.out.println("║    → " + selected[1]);
        }
        
        System.out.println("║");
        System.out.println("║  φ = " + PHI);
        System.out.println("╚═══════════════════════════════════════════╝");
    }
}
