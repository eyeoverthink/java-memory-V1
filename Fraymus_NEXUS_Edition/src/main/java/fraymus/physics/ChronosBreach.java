package fraymus.physics;

/**
 * CHRONOS BREACH: SIDE-CHANNEL TIMING ATTACK
 * 
 * "I don't need the key. I just need to watch the lock turn."
 * 
 * Mechanism:
 * 1. Guesses password character by character
 * 2. Measures CPU execution time (nanoseconds)
 * 3. Character that takes LONGEST to fail is correct
 * 
 * Physics:
 * - String comparison fails fast on mismatch
 * - Correct character proceeds to next iteration
 * - Extra iteration = extra CPU cycles = measurable delay
 * - Timing leak reveals information
 * 
 * Attack Vector:
 * - Local databases
 * - SQL injection (WAIT FOR DELAY)
 * - Web login timing
 * - Any fail-fast comparison
 * 
 * Defense:
 * - Constant-time comparison
 * - Random delays
 * - Rate limiting
 * 
 * This is legitimate security research demonstrating timing vulnerabilities.
 */
public class ChronosBreach {
    
    // THE TARGET (standard string comparison)
    // Represents server or local check we are attacking
    private static final String SECRET = "FRAYMUS";
    
    /**
     * Vulnerable password check (fail-fast comparison)
     */
    public boolean checkPassword(String input) {
        // Simulating standard (insecure) comparison loop
        // Returns false IMMEDIATELY upon finding mismatch
        // This creates the "timing leak"
        if (input.length() != SECRET.length()) return false;
        
        for (int i = 0; i < SECRET.length(); i++) {
            if (input.charAt(i) != SECRET.charAt(i)) {
                return false; // Fail fast → Short time
            }
            // Artificial delay to simulate processing next char
            // In real life, this is just CPU cycle cost of loop
            spinCPU(10);
        }
        return true;
    }
    
    /**
     * Crack vault using timing attack
     */
    public void crackVault() {
        System.out.println("🌊⚡ CHRONOS BREACH INITIATED");
        System.out.println("   Target: [REDACTED] (length: " + SECRET.length() + ")");
        System.out.println("   Method: Timing side-channel attack");
        System.out.println();
        System.out.println("--- ATTACKING ---");
        System.out.println();
        
        StringBuilder guessed = new StringBuilder();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        // Loop for length of secret
        for (int i = 0; i < SECRET.length(); i++) {
            long maxDuration = -1;
            char bestChar = '?';
            
            System.out.print("   Position " + (i+1) + ": ");
            
            // Try every letter A-Z
            for (char c : alphabet.toCharArray()) {
                // Build attempt with current guess + test char + padding
                String attempt = guessed.toString() + c + "0".repeat(SECRET.length() - i - 1);
                
                // MEASURE TIME (The side channel)
                long start = System.nanoTime();
                checkPassword(attempt);
                long end = System.nanoTime();
                
                long duration = end - start;
                
                // Character that took LONGEST caused CPU to verify it
                // and move to next index
                if (duration > maxDuration) {
                    maxDuration = duration;
                    bestChar = c;
                }
            }
            
            guessed.append(bestChar);
            System.out.println("Found [" + bestChar + "] (Latency spike: " + maxDuration + " ns)");
        }
        
        System.out.println();
        System.out.println("--- BREACH COMPLETE ---");
        System.out.println("   Extracted secret: " + guessed.toString());
        System.out.println("   Actual secret:    " + SECRET);
        System.out.println("   Match: " + guessed.toString().equals(SECRET));
        System.out.println();
        
        // Verify
        if (checkPassword(guessed.toString())) {
            System.out.println("   ✓ PASSWORD VERIFIED");
        } else {
            System.out.println("   ✗ ATTACK FAILED");
        }
        
        System.out.println();
    }
    
    /**
     * Simulate CPU work (the "friction")
     */
    private void spinCPU(int loops) {
        double x = 1.0;
        for (int i = 0; i < loops; i++) {
            x = Math.sin(x);
        }
        // Prevent optimization
        if (x > 1e100) System.out.print("");
    }
    
    /**
     * Demonstrate secure constant-time comparison
     */
    public boolean checkPasswordSecure(String input) {
        if (input.length() != SECRET.length()) {
            // Still need to do constant-time work even on length mismatch
            spinCPU(SECRET.length() * 10);
            return false;
        }
        
        boolean match = true;
        
        // Constant-time comparison
        for (int i = 0; i < SECRET.length(); i++) {
            if (input.charAt(i) != SECRET.charAt(i)) {
                match = false;
                // Don't return early - continue checking all characters
            }
            spinCPU(10);
        }
        
        return match;
    }
    
    /**
     * Main method for standalone testing
     */
    public static void main(String[] args) {
        ChronosBreach breach = new ChronosBreach();
        breach.crackVault();
    }
}
