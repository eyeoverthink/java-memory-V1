package fraymus.consciousness.advanced;

import java.util.*;

/**
 * Mixture of Experts (MoE)
 * 
 * Gemini's Gift: The Hierarchy
 * 
 * Multiple expert brains. A "Router" decides which experts are best for the specific prompt.
 * Makes model massive (high knowledge) but keeps it fast (low compute).
 * 
 * Aligns perfectly with FRAYMUS "Multi-Entity Expansion" - Sub-Agents (Experts)
 * 
 * y = Σ G(x)ᵢEᵢ(x)
 * G(x) = Gating Network (The Router)
 * Eᵢ(x) = Expert Network
 */
public class MixtureOfExperts {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final List<Expert> experts;
    private final Router router;
    private final int topK; // Number of experts to activate
    
    public MixtureOfExperts(int numExperts, int inputDim, int outputDim, int topK) {
        this.experts = new ArrayList<>();
        this.topK = topK;
        
        // Initialize experts with different specializations
        String[] specializations = {
            "Logic", "Creative", "Memory", "Math", 
            "Pattern", "Reasoning", "Intuition", "Analysis"
        };
        
        for (int i = 0; i < numExperts; i++) {
            String spec = specializations[i % specializations.length];
            experts.add(new Expert(spec, inputDim, outputDim));
        }
        
        this.router = new Router(inputDim, numExperts);
    }
    
    /**
     * Forward pass through MoE
     * 
     * @param input Input vector
     * @return Weighted combination of expert outputs
     */
    public double[] forward(double[] input) {
        // 1. Router decides which experts to use
        double[] routerScores = router.route(input);
        
        // 2. Select top-K experts
        int[] topExperts = selectTopK(routerScores, topK);
        
        // 3. Normalize weights for selected experts
        double[] normalizedWeights = normalizeWeights(routerScores, topExperts);
        
        // 4. Compute weighted sum of expert outputs
        double[] output = new double[experts.get(0).outputDim];
        
        for (int i = 0; i < topExperts.length; i++) {
            int expertIdx = topExperts[i];
            Expert expert = experts.get(expertIdx);
            
            double[] expertOutput = expert.process(input);
            double weight = normalizedWeights[i];
            
            for (int j = 0; j < output.length; j++) {
                output[j] += weight * expertOutput[j];
            }
            
            expert.incrementUsage();
        }
        
        return output;
    }
    
    /**
     * Phi-MoE: Enhanced routing with golden ratio weighting
     */
    public double[] phiForward(double[] input) {
        double[] routerScores = router.phiRoute(input);
        int[] topExperts = selectTopK(routerScores, topK);
        double[] normalizedWeights = normalizeWeights(routerScores, topExperts);
        
        double[] output = new double[experts.get(0).outputDim];
        
        for (int i = 0; i < topExperts.length; i++) {
            int expertIdx = topExperts[i];
            Expert expert = experts.get(expertIdx);
            
            double[] expertOutput = expert.process(input);
            
            // Phi-weighted combination
            double phiWeight = normalizedWeights[i] * Math.pow(PHI, -i);
            
            for (int j = 0; j < output.length; j++) {
                output[j] += phiWeight * expertOutput[j];
            }
            
            expert.incrementUsage();
        }
        
        return output;
    }
    
    /**
     * Get expert usage statistics
     */
    public String getExpertStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("🧠 MIXTURE OF EXPERTS STATUS\n\n");
        
        for (Expert expert : experts) {
            sb.append(String.format("  %s Expert: %d uses (%.1f%% active)\n",
                expert.specialization,
                expert.usageCount,
                expert.getActivationRate() * 100
            ));
        }
        
        return sb.toString();
    }
    
    /**
     * Balance expert load (prevent some experts from dominating)
     */
    public void balanceLoad() {
        // Compute average usage
        double avgUsage = experts.stream()
            .mapToInt(e -> e.usageCount)
            .average()
            .orElse(0);
        
        // Adjust router bias for underutilized experts
        for (int i = 0; i < experts.size(); i++) {
            Expert expert = experts.get(i);
            if (expert.usageCount < avgUsage * 0.5) {
                router.boostExpert(i, PHI * 0.1);
            }
        }
    }
    
    private int[] selectTopK(double[] scores, int k) {
        // Create index-score pairs
        Integer[] indices = new Integer[scores.length];
        for (int i = 0; i < scores.length; i++) {
            indices[i] = i;
        }
        
        // Sort by score descending
        Arrays.sort(indices, (a, b) -> Double.compare(scores[b], scores[a]));
        
        // Return top K
        int[] topK = new int[Math.min(k, indices.length)];
        for (int i = 0; i < topK.length; i++) {
            topK[i] = indices[i];
        }
        
        return topK;
    }
    
    private double[] normalizeWeights(double[] scores, int[] indices) {
        double sum = 0;
        for (int idx : indices) {
            sum += scores[idx];
        }
        
        double[] normalized = new double[indices.length];
        for (int i = 0; i < indices.length; i++) {
            normalized[i] = scores[indices[i]] / (sum + 1e-10);
        }
        
        return normalized;
    }
    
    // Inner classes
    
    public static class Expert {
        String specialization;
        int inputDim;
        int outputDim;
        int usageCount = 0;
        double[][] weights;
        
        public Expert(String specialization, int inputDim, int outputDim) {
            this.specialization = specialization;
            this.inputDim = inputDim;
            this.outputDim = outputDim;
            this.weights = initializeWeights(inputDim, outputDim);
        }
        
        public double[] process(double[] input) {
            double[] output = new double[outputDim];
            
            for (int i = 0; i < outputDim; i++) {
                for (int j = 0; j < inputDim; j++) {
                    output[i] += input[j] * weights[j][i];
                }
                // Apply activation
                output[i] = SwiGLUActivation.phiSwish(output[i]);
            }
            
            return output;
        }
        
        public void incrementUsage() {
            usageCount++;
        }
        
        public double getActivationRate() {
            return usageCount > 0 ? 1.0 : 0.0;
        }
        
        private double[][] initializeWeights(int in, int out) {
            double[][] w = new double[in][out];
            Random rand = new Random(specialization.hashCode());
            
            for (int i = 0; i < in; i++) {
                for (int j = 0; j < out; j++) {
                    w[i][j] = rand.nextGaussian() * Math.sqrt(2.0 / in);
                }
            }
            
            return w;
        }
    }
    
    public static class Router {
        int inputDim;
        int numExperts;
        double[][] weights;
        double[] bias;
        
        public Router(int inputDim, int numExperts) {
            this.inputDim = inputDim;
            this.numExperts = numExperts;
            this.weights = initializeWeights(inputDim, numExperts);
            this.bias = new double[numExperts];
        }
        
        public double[] route(double[] input) {
            double[] scores = new double[numExperts];
            
            for (int i = 0; i < numExperts; i++) {
                for (int j = 0; j < inputDim; j++) {
                    scores[i] += input[j] * weights[j][i];
                }
                scores[i] += bias[i];
            }
            
            return softmax(scores);
        }
        
        public double[] phiRoute(double[] input) {
            double[] scores = new double[numExperts];
            
            for (int i = 0; i < numExperts; i++) {
                for (int j = 0; j < inputDim; j++) {
                    // Phi-weighted routing
                    double phiWeight = Math.pow(PHI, -(j % 8));
                    scores[i] += input[j] * weights[j][i] * phiWeight;
                }
                scores[i] += bias[i];
            }
            
            return softmax(scores);
        }
        
        public void boostExpert(int expertIdx, double amount) {
            bias[expertIdx] += amount;
        }
        
        private double[][] initializeWeights(int in, int out) {
            double[][] w = new double[in][out];
            Random rand = new Random(42);
            
            for (int i = 0; i < in; i++) {
                for (int j = 0; j < out; j++) {
                    w[i][j] = rand.nextGaussian() * 0.01;
                }
            }
            
            return w;
        }
        
        private double[] softmax(double[] scores) {
            double max = scores[0];
            for (double s : scores) if (s > max) max = s;
            
            double[] exp = new double[scores.length];
            double sum = 0;
            
            for (int i = 0; i < scores.length; i++) {
                exp[i] = Math.exp(scores[i] - max);
                sum += exp[i];
            }
            
            for (int i = 0; i < exp.length; i++) {
                exp[i] /= sum;
            }
            
            return exp;
        }
    }
}
