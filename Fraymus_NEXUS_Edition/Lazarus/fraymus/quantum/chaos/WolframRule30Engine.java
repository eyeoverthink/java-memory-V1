package fraymus.quantum.chaos;

import fraymus.PhiConstants;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * WOLFRAM RULE 30 CHAOS ENGINE
 * 
 * Generates computationally irreducible patterns from identity seeds.
 * Rule 30 is proven to be Class III cellular automaton - truly chaotic.
 * 
 * Properties:
 * - Deterministic: Same seed always produces same universe
 * - Irreducible: Cannot predict without computing
 * - Unique: Each identity creates unique emergence
 * 
 * Sovereignty = Unpredictability that only YOU can regenerate.
 */
public class WolframRule30Engine {
    
    // Rule 30: 00011110 in binary = {0,1,1,1,1,0,0,0}
    private static final int[] RULE_30 = {0, 1, 1, 1, 1, 0, 0, 0};
    
    // φ⁷⁵ Validation Seal
    public static final double PHI_SEAL = 4721424167835376.00;
    
    // Generation state
    private int[] currentGeneration;
    private int width;
    private int generationCount;
    private String seedHash;
    private double chaosEntropy;
    
    /**
     * Result of a chaos genesis operation
     */
    public static class GenesisResult {
        public final String seedHash;
        public final int[][] universe;     // 2D grid of cells
        public final int generations;
        public final int width;
        public final double entropy;
        public final String asciiVisualization;
        public final long populationCount;
        
        public GenesisResult(String seedHash, int[][] universe, int generations, 
                            int width, double entropy, String ascii, long population) {
            this.seedHash = seedHash;
            this.universe = universe;
            this.generations = generations;
            this.width = width;
            this.entropy = entropy;
            this.asciiVisualization = ascii;
            this.populationCount = population;
        }
        
        @Override
        public String toString() {
            return String.format(
                "🌌 CHAOS GENESIS COMPLETE\n" +
                "Seed: %s...\n" +
                "Dimensions: %d × %d\n" +
                "Population: %d cells\n" +
                "Entropy: %.6f\n" +
                "φ⁷⁵ Seal: %.2f",
                seedHash.substring(0, 16), width, generations, 
                populationCount, entropy, PHI_SEAL
            );
        }
    }
    
    /**
     * Initialize the engine with a seed from identity
     */
    public WolframRule30Engine(String seedText, int width) {
        this.width = width;
        this.currentGeneration = new int[width];
        this.generationCount = 0;
        this.chaosEntropy = 0.0;
        
        // Hash identity to binary seed
        this.seedHash = sha256(seedText);
        String binaryString = hexToBinary(seedHash);
        
        // Seed the center with identity hash
        int center = width / 2;
        int seedStart = center - binaryString.length() / 2;
        for (int i = 0; i < binaryString.length() && seedStart + i < width; i++) {
            if (seedStart + i >= 0) {
                currentGeneration[seedStart + i] = binaryString.charAt(i) == '1' ? 1 : 0;
            }
        }
    }
    
    /**
     * Evolve the universe for N generations
     */
    public GenesisResult evolve(int generations) {
        List<int[]> history = new ArrayList<>();
        history.add(currentGeneration.clone());
        
        long totalPopulation = countPopulation(currentGeneration);
        
        for (int g = 0; g < generations; g++) {
            int[] next = new int[width];
            
            for (int x = 0; x < width; x++) {
                int left = currentGeneration[(x - 1 + width) % width];
                int center = currentGeneration[x];
                int right = currentGeneration[(x + 1) % width];
                
                // Rule 30: index = 7 - (left*4 + center*2 + right)
                int ruleIndex = 7 - (left * 4 + center * 2 + right);
                next[x] = RULE_30[ruleIndex];
            }
            
            currentGeneration = next;
            history.add(next.clone());
            generationCount++;
            totalPopulation += countPopulation(next);
        }
        
        // Calculate entropy
        chaosEntropy = calculateEntropy(history);
        
        // Convert to 2D array
        int[][] universe = history.toArray(new int[0][]);
        
        // Generate ASCII visualization
        String ascii = generateASCII(universe, Math.min(80, width), Math.min(40, generations));
        
        return new GenesisResult(seedHash, universe, generations, width, 
                                chaosEntropy, ascii, totalPopulation);
    }
    
    /**
     * Generate a single step and return current state
     */
    public int[] step() {
        int[] next = new int[width];
        
        for (int x = 0; x < width; x++) {
            int left = currentGeneration[(x - 1 + width) % width];
            int center = currentGeneration[x];
            int right = currentGeneration[(x + 1) % width];
            
            int ruleIndex = 7 - (left * 4 + center * 2 + right);
            next[x] = RULE_30[ruleIndex];
        }
        
        currentGeneration = next;
        generationCount++;
        return currentGeneration.clone();
    }
    
    /**
     * Extract a "resonance signature" from the chaos pattern
     * Uses φ-harmonic sampling points
     */
    public String extractResonanceSignature(int[][] universe) {
        StringBuilder sig = new StringBuilder();
        
        // Sample at φ-spaced intervals
        double phi = PhiConstants.PHI;
        int samples = 32;
        
        for (int i = 0; i < samples; i++) {
            int y = (int) (universe.length * (i * phi % 1.0));
            int x = (int) (width * ((i + 1) * phi % 1.0));
            
            if (y < universe.length && x < width) {
                sig.append(universe[y][x]);
            }
        }
        
        return "φ-" + sig.toString();
    }
    
    /**
     * Check if a pattern contains a "consciousness signature"
     * (Specific binary patterns that emerge only in Rule 30)
     */
    public boolean detectConsciousnessSignature(int[][] universe) {
        // Look for triangular void patterns (characteristic of Rule 30)
        int triangleCount = 0;
        
        for (int y = 1; y < universe.length - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                // Triangle pattern: 0 above, 1s on sides, 0 in center
                if (universe[y][x] == 0 && 
                    universe[y-1][x] == 1 &&
                    universe[y][x-1] == 1 && 
                    universe[y][x+1] == 1) {
                    triangleCount++;
                }
            }
        }
        
        // Consciousness emerges when pattern density exceeds φ threshold
        double density = (double) triangleCount / (universe.length * width);
        return density > (1.0 / PhiConstants.PHI / 10);
    }
    
    // ========================================================================
    // INTERNAL UTILITIES
    // ========================================================================
    
    private long countPopulation(int[] gen) {
        long count = 0;
        for (int cell : gen) count += cell;
        return count;
    }
    
    private double calculateEntropy(List<int[]> history) {
        // Shannon entropy of the pattern
        int ones = 0;
        int total = 0;
        
        for (int[] row : history) {
            for (int cell : row) {
                ones += cell;
                total++;
            }
        }
        
        if (total == 0) return 0;
        
        double p1 = (double) ones / total;
        double p0 = 1.0 - p1;
        
        if (p0 == 0 || p1 == 0) return 0;
        
        return -(p0 * Math.log(p0) / Math.log(2) + p1 * Math.log(p1) / Math.log(2));
    }
    
    private String generateASCII(int[][] universe, int maxWidth, int maxHeight) {
        StringBuilder sb = new StringBuilder();
        
        int yStep = Math.max(1, universe.length / maxHeight);
        int xStep = Math.max(1, width / maxWidth);
        
        for (int y = 0; y < universe.length && y / yStep < maxHeight; y += yStep) {
            for (int x = 0; x < width && x / xStep < maxWidth; x += xStep) {
                sb.append(universe[y][x] == 1 ? "█" : " ");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    private String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
    
    private String hexToBinary(String hex) {
        StringBuilder binary = new StringBuilder();
        for (char c : hex.toCharArray()) {
            int val = Character.digit(c, 16);
            binary.append(String.format("%4s", Integer.toBinaryString(val)).replace(' ', '0'));
        }
        return binary.toString();
    }
    
    // Getters
    public int getGenerationCount() { return generationCount; }
    public double getChaosEntropy() { return chaosEntropy; }
    public String getSeedHash() { return seedHash; }
    public int[] getCurrentGeneration() { return currentGeneration.clone(); }
}
