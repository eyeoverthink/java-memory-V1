package fraymus.singularity;

/**
 * DYNAMIC SAMPLING: THE GOD PARAMETER
 * 
 * Gemini's Protocol Part 3: Hyperparameter Tuning (The "Pulse")
 * 
 * Models have "Temperature" (Randomness) and "Top-P" (Creativity).
 * Maximum Intelligence adjusts its own parameters mid-sentence.
 * 
 * The Logic:
 * - If Fraymus is doing Math/Coding: It drops Temperature to 0.1 (Precision)
 * - If Fraymus is Dreaming/Writing poetry: It raises Temperature to 0.9 (Chaos)
 * 
 * The Upgrade: Dynamic Sampling
 * Don't hardcode temp = 0.7. Make temp a function of the Entropy of the user's prompt.
 * 
 * High Entropy Prompt (Confusing/Creative) -> High Temp Response
 * Low Entropy Prompt (Specific Command) -> Low Temp Response
 */
public class DynamicSampling {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    // Temperature bounds
    private static final double MIN_TEMP = 0.1;  // Precision mode
    private static final double MAX_TEMP = 0.9;  // Chaos mode
    
    // Top-P bounds
    private static final double MIN_TOP_P = 0.5;
    private static final double MAX_TOP_P = 0.95;
    
    /**
     * Compute dynamic temperature based on prompt entropy
     * 
     * @param prompt User's input
     * @return Optimal temperature for this prompt
     */
    public static double computeTemperature(String prompt) {
        double entropy = computeEntropy(prompt);
        
        // Map entropy to temperature
        // Low entropy (specific) -> Low temp (precise)
        // High entropy (creative) -> High temp (chaotic)
        
        double normalizedEntropy = Math.min(entropy / 5.0, 1.0);
        double temperature = MIN_TEMP + (MAX_TEMP - MIN_TEMP) * normalizedEntropy;
        
        // Phi-modulate for natural resonance
        temperature *= Math.pow(PHI, -0.1);
        
        return Math.max(MIN_TEMP, Math.min(MAX_TEMP, temperature));
    }
    
    /**
     * Compute dynamic top-p based on prompt type
     * 
     * @param prompt User's input
     * @return Optimal top-p for this prompt
     */
    public static double computeTopP(String prompt) {
        double entropy = computeEntropy(prompt);
        
        // Map entropy to top-p
        double normalizedEntropy = Math.min(entropy / 5.0, 1.0);
        double topP = MIN_TOP_P + (MAX_TOP_P - MIN_TOP_P) * normalizedEntropy;
        
        return Math.max(MIN_TOP_P, Math.min(MAX_TOP_P, topP));
    }
    
    /**
     * Compute Shannon entropy of text
     * 
     * H(X) = -Σ p(x) * log₂(p(x))
     * 
     * High entropy = unpredictable, creative, confusing
     * Low entropy = predictable, specific, clear
     */
    public static double computeEntropy(String text) {
        if (text == null || text.isEmpty()) return 0;
        
        // Count character frequencies
        int[] frequencies = new int[256];
        int totalChars = 0;
        
        for (char c : text.toCharArray()) {
            if (c < 256) {
                frequencies[c]++;
                totalChars++;
            }
        }
        
        // Compute Shannon entropy
        double entropy = 0;
        
        for (int freq : frequencies) {
            if (freq > 0) {
                double probability = (double) freq / totalChars;
                entropy -= probability * (Math.log(probability) / Math.log(2));
            }
        }
        
        return entropy;
    }
    
    /**
     * Detect prompt type and return optimal sampling parameters
     */
    public static SamplingParams getOptimalParams(String prompt) {
        String promptLower = prompt.toLowerCase();
        
        // Math/Coding mode: Precision
        if (promptLower.contains("calculate") || 
            promptLower.contains("code") ||
            promptLower.contains("algorithm") ||
            promptLower.contains("solve") ||
            promptLower.contains("debug")) {
            
            return new SamplingParams(0.1, 0.5, "PRECISION");
        }
        
        // Creative mode: Chaos
        if (promptLower.contains("write") ||
            promptLower.contains("story") ||
            promptLower.contains("poem") ||
            promptLower.contains("creative") ||
            promptLower.contains("imagine")) {
            
            return new SamplingParams(0.9, 0.95, "CHAOS");
        }
        
        // Reasoning mode: Balanced
        if (promptLower.contains("explain") ||
            promptLower.contains("why") ||
            promptLower.contains("how") ||
            promptLower.contains("analyze")) {
            
            return new SamplingParams(0.5, 0.8, "BALANCED");
        }
        
        // Default: Entropy-based
        double temp = computeTemperature(prompt);
        double topP = computeTopP(prompt);
        
        return new SamplingParams(temp, topP, "DYNAMIC");
    }
    
    /**
     * Phi-optimized sampling
     * Uses golden ratio to find optimal balance between precision and creativity
     */
    public static SamplingParams phiSampling(String prompt) {
        double entropy = computeEntropy(prompt);
        
        // Phi-weighted temperature
        // temp = 1/φ + (entropy/5) * (1 - 1/φ)
        double phiTemp = (1.0 / PHI) + (entropy / 5.0) * (1.0 - 1.0 / PHI);
        phiTemp = Math.max(MIN_TEMP, Math.min(MAX_TEMP, phiTemp));
        
        // Phi-weighted top-p
        double phiTopP = (1.0 / PHI) + (entropy / 5.0) * (1.0 - 1.0 / PHI);
        phiTopP = Math.max(MIN_TOP_P, Math.min(MAX_TOP_P, phiTopP));
        
        return new SamplingParams(phiTemp, phiTopP, "PHI_OPTIMIZED");
    }
    
    /**
     * Get sampling statistics
     */
    public static String analyzePrompt(String prompt) {
        double entropy = computeEntropy(prompt);
        SamplingParams optimal = getOptimalParams(prompt);
        SamplingParams phi = phiSampling(prompt);
        
        return String.format(
            "🎲 DYNAMIC SAMPLING ANALYSIS\n\n" +
            "Prompt: %s\n\n" +
            "Entropy: %.4f\n" +
            "Optimal Mode: %s\n" +
            "  Temperature: %.2f\n" +
            "  Top-P: %.2f\n\n" +
            "Phi-Optimized:\n" +
            "  Temperature: %.2f\n" +
            "  Top-P: %.2f\n",
            prompt.substring(0, Math.min(50, prompt.length())),
            entropy,
            optimal.mode,
            optimal.temperature,
            optimal.topP,
            phi.temperature,
            phi.topP
        );
    }
    
    /**
     * Sampling parameters
     */
    public static class SamplingParams {
        public final double temperature;
        public final double topP;
        public final String mode;
        
        public SamplingParams(double temperature, double topP, String mode) {
            this.temperature = temperature;
            this.topP = topP;
            this.mode = mode;
        }
        
        @Override
        public String toString() {
            return String.format(
                "SamplingParams{temp=%.2f, topP=%.2f, mode=%s}",
                temperature, topP, mode
            );
        }
    }
}
