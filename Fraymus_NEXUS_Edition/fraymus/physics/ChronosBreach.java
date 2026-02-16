package fraymus.physics;

import fraymus.quantum.core.PhiQuantumConstants;
import java.util.ArrayList;
import java.util.List;

/**
 * THE CHRONOS BREACH: SIDE-CHANNEL TIMING ATTACK
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "I don't need the key. I just need to watch the lock turn."
 * 
 * Exploits the fundamental physics of computation:
 * - Wrong guess → CPU rejects INSTANTLY (fast)
 * - Correct partial → CPU checks NEXT character (slow)
 * - The nanosecond difference reveals the secret
 * 
 * This works because:
 * 1. Most string comparisons fail-fast on first mismatch
 * 2. Each additional matching character adds CPU cycles
 * 3. We measure time, not logic - physics doesn't lie
 */
public class ChronosBreach {

    private static final double PHI = PhiQuantumConstants.PHI;

    // Configuration
    private int warmupIterations = 100;      // JIT warmup
    private int samplesPerChar = 50;         // Samples for statistical significance
    private int spinLoops = 100;             // CPU work per character
    private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    // Statistics
    private List<TimingResult> timingHistory = new ArrayList<>();
    private long totalAttempts = 0;
    private long successfulBreaches = 0;

    /**
     * A vault to crack - simulates insecure string comparison
     */
    public static class Vault {
        private final String secret;
        private long checkCount = 0;
        
        public Vault(String secret) {
            this.secret = secret;
        }
        
        /**
         * INSECURE password check - fails fast on mismatch
         * This creates the timing leak we exploit
         */
        public boolean checkPassword(String input) {
            checkCount++;
            
            if (input.length() != secret.length()) {
                return false;
            }
            
            for (int i = 0; i < secret.length(); i++) {
                if (input.charAt(i) != secret.charAt(i)) {
                    return false; // FAIL FAST = TIMING LEAK
                }
                // Simulate real work (hash checks, DB lookups, etc.)
                spinCPU(100);
            }
            return true;
        }
        
        /**
         * SECURE password check - constant time
         * No timing leak (for comparison)
         */
        public boolean checkPasswordSecure(String input) {
            checkCount++;
            
            if (input.length() != secret.length()) {
                spinCPU(secret.length() * 100); // Constant time even on length mismatch
                return false;
            }
            
            int result = 0;
            for (int i = 0; i < secret.length(); i++) {
                result |= input.charAt(i) ^ secret.charAt(i);
                spinCPU(100);
            }
            return result == 0;
        }
        
        public long getCheckCount() { return checkCount; }
        public int getSecretLength() { return secret.length(); }
    }

    /**
     * Crack a vault using timing analysis
     */
    public String crackVault(Vault vault) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   CHRONOS BREACH: TIMING SIDE-CHANNEL");
        System.out.println("═══════════════════════════════════════════");
        System.out.println();
        System.out.println("Target: " + vault.getSecretLength() + " character secret");
        System.out.println("Alphabet: " + alphabet);
        System.out.println("Warmup: " + warmupIterations + " iterations");
        System.out.println("Samples: " + samplesPerChar + " per character");
        System.out.println();
        System.out.println(">> INITIATING TIMING ANALYSIS...");
        System.out.println();
        
        // JIT Warmup - make timing more consistent
        warmupJIT(vault);
        
        StringBuilder extracted = new StringBuilder();
        int secretLength = vault.getSecretLength();
        
        for (int position = 0; position < secretLength; position++) {
            System.out.print("Position " + (position + 1) + "/" + secretLength + ": ");
            
            long maxDuration = -1;
            char bestChar = '?';
            List<TimingResult> positionResults = new ArrayList<>();
            
            // Try every character in alphabet
            for (char c : alphabet.toCharArray()) {
                // Build attempt string
                String attempt = buildAttempt(extracted.toString(), c, secretLength);
                
                // Take multiple samples for statistical significance
                long totalTime = 0;
                for (int sample = 0; sample < samplesPerChar; sample++) {
                    long start = System.nanoTime();
                    vault.checkPassword(attempt);
                    long end = System.nanoTime();
                    totalTime += (end - start);
                    totalAttempts++;
                }
                
                long avgTime = totalTime / samplesPerChar;
                positionResults.add(new TimingResult(c, avgTime));
                
                // The character with longest time = correct (more verification work)
                if (avgTime > maxDuration) {
                    maxDuration = avgTime;
                    bestChar = c;
                }
            }
            
            extracted.append(bestChar);
            timingHistory.addAll(positionResults);
            
            // Calculate confidence (how much longer than average)
            double avgAllChars = positionResults.stream()
                .mapToLong(r -> r.nanoseconds).average().orElse(0);
            double confidence = (maxDuration - avgAllChars) / avgAllChars * 100;
            
            System.out.println("Found [" + bestChar + "] | " +
                "Latency: " + maxDuration + "ns | " +
                "Δ: +" + String.format("%.1f", confidence) + "%");
        }
        
        // Verify the crack
        boolean success = vault.checkPassword(extracted.toString());
        successfulBreaches += success ? 1 : 0;
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   BREACH " + (success ? "SUCCESSFUL" : "FAILED"));
        System.out.println("═══════════════════════════════════════════");
        System.out.println("Extracted: " + extracted);
        System.out.println("Verified: " + success);
        System.out.println("Total Attempts: " + totalAttempts);
        System.out.println("Vault Checks: " + vault.getCheckCount());
        
        return extracted.toString();
    }

    /**
     * Demonstrate the timing difference
     */
    public void demonstrateLeak(Vault vault, String correctPrefix, String wrongPrefix) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   TIMING LEAK DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════");
        System.out.println();
        
        int padLength = vault.getSecretLength();
        String correct = padRight(correctPrefix, padLength, 'X');
        String wrong = padRight(wrongPrefix, padLength, 'X');
        
        // Warmup
        for (int i = 0; i < 1000; i++) {
            vault.checkPassword(correct);
            vault.checkPassword(wrong);
        }
        
        // Measure
        long correctTime = 0, wrongTime = 0;
        int samples = 1000;
        
        for (int i = 0; i < samples; i++) {
            long start = System.nanoTime();
            vault.checkPassword(correct);
            correctTime += System.nanoTime() - start;
            
            start = System.nanoTime();
            vault.checkPassword(wrong);
            wrongTime += System.nanoTime() - start;
        }
        
        correctTime /= samples;
        wrongTime /= samples;
        
        System.out.println("Correct prefix \"" + correctPrefix + "\": " + correctTime + " ns");
        System.out.println("Wrong prefix \"" + wrongPrefix + "\": " + wrongTime + " ns");
        System.out.println("Difference: " + (correctTime - wrongTime) + " ns");
        System.out.println();
        System.out.println("The " + (correctTime > wrongTime ? "CORRECT" : "WRONG") + 
            " prefix takes longer because the CPU does more work.");
    }

    /**
     * Warmup JIT compiler for consistent timing
     */
    private void warmupJIT(Vault vault) {
        String dummy = padRight("", vault.getSecretLength(), 'A');
        for (int i = 0; i < warmupIterations; i++) {
            vault.checkPassword(dummy);
        }
    }

    /**
     * Build an attempt string with known prefix + guess + padding
     */
    private String buildAttempt(String known, char guess, int targetLength) {
        StringBuilder sb = new StringBuilder(known);
        sb.append(guess);
        while (sb.length() < targetLength) {
            sb.append('X'); // Padding character
        }
        return sb.toString();
    }

    /**
     * Pad string to length
     */
    private String padRight(String s, int length, char pad) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < length) {
            sb.append(pad);
        }
        return sb.toString();
    }

    /**
     * CPU spin for timing
     */
    private static void spinCPU(int loops) {
        double x = 1.0;
        for (int i = 0; i < loops; i++) {
            x = Math.sin(x * PhiQuantumConstants.PHI);
        }
        if (x == Double.NaN) System.out.print(""); // Prevent optimization
    }

    // Configuration setters
    public void setWarmupIterations(int n) { this.warmupIterations = n; }
    public void setSamplesPerChar(int n) { this.samplesPerChar = n; }
    public void setSpinLoops(int n) { this.spinLoops = n; }
    public void setAlphabet(String a) { this.alphabet = a; }
    
    public long getTotalAttempts() { return totalAttempts; }
    public long getSuccessfulBreaches() { return successfulBreaches; }
    public List<TimingResult> getTimingHistory() { return timingHistory; }

    /**
     * Timing result record
     */
    public static class TimingResult {
        public final char character;
        public final long nanoseconds;
        
        public TimingResult(char c, long ns) {
            this.character = c;
            this.nanoseconds = ns;
        }
    }

    /**
     * Main entry point for standalone testing
     */
    public static void main(String[] args) {
        ChronosBreach breach = new ChronosBreach();
        
        // Create a vault with a secret
        String secret = args.length > 0 ? args[0] : "FRAYMUS";
        Vault vault = new Vault(secret);
        
        System.out.println("Creating vault with secret: " + secret);
        System.out.println();
        
        // Demonstrate the timing leak first
        breach.demonstrateLeak(vault, "F", "A");
        System.out.println();
        
        // Now crack it
        String extracted = breach.crackVault(vault);
        
        System.out.println();
        System.out.println("Original: " + secret);
        System.out.println("Cracked:  " + extracted);
        System.out.println("Match: " + secret.equals(extracted));
    }
}
