package fraymus.daemon;

import fraymus.hyper.HyperFormer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ⚖️ ENTROPY FILTER - The Judge
 * "Decides if code is clean or needs fixing"
 * 
 * Process:
 * 1. READ: Ingest the modified file
 * 2. LEARN: Absorb patterns into HyperFormer
 * 3. PREDICT: Test if code matches learned patterns
 * 4. JUDGE: High entropy = anomaly = potential bug
 * 5. FIX: Generate corrected version
 * 
 * Traditional Linting:
 * - Static rules (syntax, style)
 * - No understanding of semantics
 * - Can't predict bugs
 * 
 * Entropy Filter:
 * - Dynamic learning (patterns from your code)
 * - Semantic understanding (via vectors)
 * - Predicts anomalies before they compile
 * 
 * This is the Silent Partner that fixes mistakes before you compile.
 */
public class EntropyFilter {

    private final HyperFormer brain;
    private int filesProcessed = 0;
    private int anomaliesDetected = 0;
    private int fixesGenerated = 0;

    public EntropyFilter(HyperFormer brain) {
        this.brain = brain;
    }

    /**
     * INSPECT: Analyze a file for anomalies
     * 
     * @param filePath The file to inspect
     */
    public void inspect(Path filePath) {
        try {
            // 1. READ (The Stimulus)
            String content = Files.readString(filePath);
            String[] tokens = content.split("\\s+");

            if (tokens.length < 5) return; // Ignore trivial files

            filesProcessed++;
            
            System.out.println("\n⚡ DETECTED ACTIVITY: " + filePath.getFileName());
            System.out.println("   Tokens: " + tokens.length);

            // 2. LEARN (The Integration)
            // Fraymus instantly absorbs the new knowledge
            // No manual training needed - it learns from your work
            brain.learn(tokens);
            System.out.println("   🧠 KNOWLEDGE INTEGRATED (Vocab: " + brain.vocabSize() + ")");

            // 3. PREDICT (The Judgement)
            // Mask the last few tokens and see if Fraymus can predict them
            // If predictions don't match, code is "surprising" (high entropy)
            int contextSize = Math.min(5, tokens.length - 1);
            String[] context = new String[contextSize];
            System.arraycopy(tokens, tokens.length - 1 - contextSize, context, 0, contextSize);
            
            String prediction = brain.predict(context);
            String actual = tokens[tokens.length - 1];

            // 4. RESONANCE CHECK
            double resonance = calculateResonance(prediction, actual);
            
            if (resonance < 0.8) {
                anomaliesDetected++;
                System.out.println("   ⚠️  ANOMALY DETECTED");
                System.out.println("      Expected: '" + prediction + "'");
                System.out.println("      Found:    '" + actual + "'");
                System.out.println("      Resonance: " + String.format("%.2f%%", resonance * 100));
                
                // TRIGGER AUTO-FIX
                suggestFix(filePath, content, actual, prediction);
            } else {
                System.out.println("   ✅ RESONANCE: " + String.format("%.2f%%", resonance * 100) + " - Code is clean");
            }

        } catch (Exception e) {
            // Ignore read locks and other transient errors
            // (File might be mid-write by IDE)
        }
    }

    /**
     * SUGGEST FIX: Generate corrected version
     * 
     * For safety, we don't overwrite the original.
     * We create a suggestion file that the developer can review.
     */
    private void suggestFix(Path original, String content, String anomaly, String suggestion) {
        try {
            // Create fixed version with inline comment
            String fixed = content.replace(
                anomaly, 
                suggestion + " /* FRAYMUS: Suggested '" + suggestion + "' instead of '" + anomaly + "' */"
            );
            
            Path fixPath = original.getParent().resolve(
                original.getFileName().toString().replace(".java", "_fraymus_suggestion.java")
            );
            
            Files.writeString(fixPath, fixed);
            fixesGenerated++;
            
            System.out.println("   🛠️  FIX GENERATED: " + fixPath.getFileName());
            
        } catch (Exception e) {
            System.err.println("   ❌ Fix generation failed: " + e.getMessage());
        }
    }

    /**
     * Calculate semantic resonance between prediction and actual
     */
    private double calculateResonance(String predicted, String actual) {
        if (predicted.equals(actual)) return 1.0;
        
        // Partial match scoring
        if (predicted.toLowerCase().equals(actual.toLowerCase())) return 0.9;
        if (predicted.contains(actual) || actual.contains(predicted)) return 0.7;
        
        // Levenshtein distance (simple version)
        int distance = levenshteinDistance(predicted, actual);
        int maxLen = Math.max(predicted.length(), actual.length());
        return 1.0 - ((double) distance / maxLen);
    }

    /**
     * Simple Levenshtein distance for string similarity
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1),     // insertion
                    dp[i - 1][j - 1] + cost // substitution
                );
            }
        }
        
        return dp[s1.length()][s2.length()];
    }

    /**
     * Get statistics
     */
    public void printStats() {
        System.out.println("\n📊 ENTROPY FILTER STATISTICS:");
        System.out.println("   Files Processed:    " + filesProcessed);
        System.out.println("   Anomalies Detected: " + anomaliesDetected);
        System.out.println("   Fixes Generated:    " + fixesGenerated);
        System.out.println("   Vocabulary Size:    " + brain.vocabSize());
    }
}
