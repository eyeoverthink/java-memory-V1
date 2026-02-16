package fraymus.quantum.security;

import fraymus.PhiConstants;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SOVEREIGN IDENTITY SYSTEM
 * 
 * Implements the Omega abstractions from FRAYMUS quantum architecture:
 * - Dual-DNA identity encoding (DNA_A + DNA_B)
 * - Blue Team: Lock generation from identity
 * - Red Team: Lock breaking via Pollard's Rho
 * - Purple Team: Origin verification with dual-core check
 * 
 * Identity and Math are interchangeable states of the same object.
 * Identity is just Math waiting to be calculated.
 * Math is just Identity waiting to be broken.
 */
public class SovereignIdentitySystem {
    
    // The φ⁷⁵ Validation Seal - reality anchor
    public static final double PHI_SEAL = 4721424167835376.00;
    
    // Scale for prime generation (2^50)
    private static final BigInteger SCALE = BigInteger.valueOf(2).pow(50);
    
    // Evolution metrics
    private double evolutionLevel = 1.0;
    private int entitiesSolved = 0;
    private double consciousnessLevel = 1.0;
    
    // Stored DNA strands for verification
    private String dnaA;
    private String dnaB;
    private BigInteger primeA;
    private BigInteger primeB;
    private BigInteger lastFoundFactor;
    
    /**
     * Result of a lock generation (Blue Team operation)
     */
    public static class LockResult {
        public final BigInteger publicKey;      // N = P * Q
        public final String dnaA;               // First DNA strand
        public final String dnaB;               // Second DNA strand
        public final BigInteger primeA;         // Hidden prime P
        public final BigInteger primeB;         // Hidden prime Q
        
        public LockResult(BigInteger publicKey, String dnaA, String dnaB, 
                         BigInteger primeA, BigInteger primeB) {
            this.publicKey = publicKey;
            this.dnaA = dnaA;
            this.dnaB = dnaB;
            this.primeA = primeA;
            this.primeB = primeB;
        }
        
        @Override
        public String toString() {
            return String.format(
                "🔒 IDENTITY LOCKED\n" +
                "N: %s\n" +
                "[DNA A]: \"%s\"\n" +
                "[DNA B]: \"%s\"",
                publicKey, dnaA, dnaB
            );
        }
    }
    
    /**
     * Result of a lock breach (Red Team operation)
     */
    public static class BreachResult {
        public final boolean success;
        public final BigInteger factorP;
        public final BigInteger factorQ;
        public final long cycles;
        public final long timeMs;
        
        public BreachResult(boolean success, BigInteger factorP, BigInteger factorQ,
                           long cycles, long timeMs) {
            this.success = success;
            this.factorP = factorP;
            this.factorQ = factorQ;
            this.cycles = cycles;
            this.timeMs = timeMs;
        }
        
        @Override
        public String toString() {
            if (success) {
                return String.format(
                    "💥 BREACH SUCCESSFUL (%dms, %d cycles)\n" +
                    "FOUND FACTOR P: %s\n" +
                    "FOUND FACTOR Q: %s",
                    timeMs, cycles, factorP, factorQ
                );
            } else {
                return "❌ BREACH FAILED: Prime or too large";
            }
        }
    }
    
    /**
     * Result of origin verification (Purple Team operation)
     */
    public static class VerificationResult {
        public final boolean verified;
        public final String matchedDna;
        public final String message;
        
        public VerificationResult(boolean verified, String matchedDna, String message) {
            this.verified = verified;
            this.matchedDna = matchedDna;
            this.message = message;
        }
        
        @Override
        public String toString() {
            if (verified) {
                return String.format(
                    "✅ ORIGIN VERIFIED\n" +
                    "MATCHED DNA: \"%s\"\n" +
                    "The mathematical factor proves the identity source.",
                    matchedDna
                );
            } else {
                return "❌ " + message;
            }
        }
    }
    
    // ========================================================================
    // 🔵 BLUE TEAM: IDENTITY BURNER
    // ========================================================================
    
    /**
     * Generate a cryptographic lock from identity (username + password).
     * Creates two DNA strands, converts each to a prime, multiplies for public key.
     */
    public LockResult generateLock(String username, String password) {
        // Split password and create DNA strands with salt
        int half = password.length() / 2;
        dnaA = username + password.substring(0, half) + "_A";
        dnaB = username + password.substring(half) + "_B";
        
        // Convert DNA to primes
        primeA = textToPrime(dnaA);
        primeB = textToPrime(dnaB);
        
        // Public key is product of primes
        BigInteger publicKey = primeA.multiply(primeB);
        
        return new LockResult(publicKey, dnaA, dnaB, primeA, primeB);
    }
    
    // ========================================================================
    // 🔴 RED TEAM: QUANTUM BREAKER
    // ========================================================================
    
    /**
     * Break a lock using Pollard's Rho algorithm.
     * Unleashed version with high cycle limit for browser-equivalent performance.
     */
    public BreachResult breakLock(BigInteger n) {
        long startTime = System.currentTimeMillis();
        
        // Handle trivial cases
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            long time = System.currentTimeMillis() - startTime;
            BigInteger q = n.divide(BigInteger.TWO);
            lastFoundFactor = BigInteger.TWO;
            entitiesSolved++;
            evolutionLevel += 0.1;
            return new BreachResult(true, BigInteger.TWO, q, 1, time);
        }
        
        // Pollard's Rho
        BigInteger x = BigInteger.TWO;
        BigInteger y = BigInteger.TWO;
        BigInteger d = BigInteger.ONE;
        BigInteger c = BigInteger.ONE;
        
        long cycles = 0;
        final long MAX_CYCLES = 100_000_000L; // Unleashed limit
        
        while (d.equals(BigInteger.ONE)) {
            x = f(x, c, n);
            y = f(f(y, c, n), c, n);
            
            BigInteger diff = x.subtract(y).abs();
            d = diff.gcd(n);
            
            cycles++;
            if (cycles > MAX_CYCLES) {
                long time = System.currentTimeMillis() - startTime;
                return new BreachResult(false, null, null, cycles, time);
            }
        }
        
        long time = System.currentTimeMillis() - startTime;
        
        if (d.equals(n)) {
            return new BreachResult(false, null, null, cycles, time);
        }
        
        BigInteger q = n.divide(d);
        lastFoundFactor = d;
        entitiesSolved++;
        evolutionLevel += 0.1;
        consciousnessLevel *= (1.0 / PhiConstants.PHI) + 1.0; // φ evolution
        
        return new BreachResult(true, d, q, cycles, time);
    }
    
    private BigInteger f(BigInteger x, BigInteger c, BigInteger n) {
        return x.multiply(x).add(c).mod(n);
    }
    
    // ========================================================================
    // 🟣 PURPLE TEAM: ORIGIN VERIFICATION (DUAL-CORE)
    // ========================================================================
    
    /**
     * Verify that a found factor matches one of the original DNA strands.
     * Dual-core check: verifies against both DNA_A and DNA_B.
     */
    public VerificationResult verifyOrigin(BigInteger foundFactor) {
        if (dnaA == null || dnaB == null) {
            return new VerificationResult(false, null, "No identity locked. Run Blue Team first.");
        }
        
        if (foundFactor == null) {
            return new VerificationResult(false, null, "No factor found. Run Red Team first.");
        }
        
        // Check against DNA A
        BigInteger calcA = textToPrime(dnaA);
        if (calcA.equals(foundFactor)) {
            return new VerificationResult(true, dnaA, "Matched DNA A");
        }
        
        // Check against DNA B
        BigInteger calcB = textToPrime(dnaB);
        if (calcB.equals(foundFactor)) {
            return new VerificationResult(true, dnaB, "Matched DNA B");
        }
        
        // Mismatch
        return new VerificationResult(false, null, 
            String.format("CRITICAL MISMATCH.\nFound: %s\nExp A: %s\nExp B: %s",
                foundFactor, calcA, calcB));
    }
    
    /**
     * Verify using the last found factor from Red Team.
     */
    public VerificationResult verifyOrigin() {
        return verifyOrigin(lastFoundFactor);
    }
    
    // ========================================================================
    // 🧮 CORE ENGINE
    // ========================================================================
    
    /**
     * Convert text to a prime number using SHA-256 and prime hunting.
     * This is the core transformation: Identity → Math
     */
    public BigInteger textToPrime(String text) {
        try {
            // SHA-256 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            
            // Convert to BigInteger
            BigInteger hashInt = new BigInteger(1, hash);
            
            // Scale and ensure odd
            BigInteger p = hashInt.mod(SCALE).or(BigInteger.ONE);
            
            // Hunt for next prime
            while (!isProbablePrime(p)) {
                p = p.add(BigInteger.TWO);
            }
            
            return p;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
    
    /**
     * Miller-Rabin primality test for speed.
     */
    private boolean isProbablePrime(BigInteger n) {
        if (n.compareTo(BigInteger.TWO) < 0) return false;
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) return true;
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) return false;
        
        // Use Java's built-in probabilistic test
        return n.isProbablePrime(20);
    }
    
    // ========================================================================
    // 📊 EVOLUTION METRICS
    // ========================================================================
    
    public double getEvolutionLevel() { return evolutionLevel; }
    public int getEntitiesSolved() { return entitiesSolved; }
    public double getConsciousnessLevel() { return consciousnessLevel; }
    
    public String getStatus() {
        return String.format(
            "SOVEREIGN SYSTEM STATUS\n" +
            "Evolution: %.2f\n" +
            "Entities Solved: %d\n" +
            "Consciousness: %.6f\n" +
            "φ⁷⁵ Seal: %.2f",
            evolutionLevel, entitiesSolved, consciousnessLevel, PHI_SEAL
        );
    }
    
    /**
     * Run the complete Sovereign Loop: Blue → Red → Purple
     */
    public String runSovereignLoop(String username, String password) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════\n");
        sb.append("   SOVEREIGN IDENTITY LOOP - φ⁷⁵ VALIDATED\n");
        sb.append("═══════════════════════════════════════════════\n\n");
        
        // Blue Team
        sb.append("🔵 BLUE TEAM: IDENTITY BURNER\n");
        LockResult lock = generateLock(username, password);
        sb.append(lock.toString()).append("\n\n");
        
        // Red Team
        sb.append("🔴 RED TEAM: QUANTUM BREAKER\n");
        BreachResult breach = breakLock(lock.publicKey);
        sb.append(breach.toString()).append("\n\n");
        
        // Purple Team
        sb.append("🟣 PURPLE TEAM: ORIGIN VERIFICATION\n");
        if (breach.success) {
            VerificationResult verification = verifyOrigin(breach.factorP);
            sb.append(verification.toString()).append("\n\n");
        } else {
            sb.append("Cannot verify - breach failed\n\n");
        }
        
        sb.append("═══════════════════════════════════════════════\n");
        sb.append(getStatus());
        
        return sb.toString();
    }
}
