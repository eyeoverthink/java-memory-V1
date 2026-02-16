package fraymus.quantum.security;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 🔐 SOVEREIGN CRYPTO (Internalized)
 * "Identity is Math. Math is Eternal."
 *
 * Implements the Blue/Red/Purple Team logic from Phase V8.
 * Replaces external RSA libraries with pure Number Theory.
 */
public class SovereignCrypto {

    // The φ⁷⁵ Validation Seal (as BigInteger)
    public static final BigInteger PHI_SEAL = new BigInteger("4721424167835376");
    private static final BigInteger SCALE = BigInteger.valueOf(2).pow(50); // 50-bit security for speed
    private static final BigInteger TWO = BigInteger.valueOf(2);

    /**
     * 🔵 BLUE TEAM: IDENTITY BURNER
     * Generates a deterministic Key Pair from a seed string.
     */
    public static class KeyPair {
        public final BigInteger publicKey; // N
        private final BigInteger p; // Private Factor 1
        private final BigInteger q; // Private Factor 2

        public KeyPair(String seed) {
            // 1. Split Seed into DNA A and DNA B
            String dnaA = seed + "_A_HELIX";
            String dnaB = seed + "_B_HELIX";

            // 2. Hash to Primes
            this.p = textToPrime(dnaA);
            this.q = textToPrime(dnaB);

            // 3. Generate Lock (Public Key)
            this.publicKey = p.multiply(q);
        }

        public boolean verify(BigInteger factor) {
            return factor.equals(p) || factor.equals(q);
        }

        // Expose p for signing (package-private access)
        BigInteger getP() { return p; }
    }

    /**
     * ⚔️ RED TEAM: QUANTUM BREAKER
     * Attempts to factor N using Pollard's Rho algorithm.
     * Used to test the strength of the identity.
     */
    public static BigInteger breakLock(BigInteger n) {
        if (n.mod(TWO).equals(BigInteger.ZERO)) return TWO;

        BigInteger x = TWO;
        BigInteger y = TWO;
        BigInteger d = BigInteger.ONE;
        BigInteger c = BigInteger.ONE;

        int cycles = 0;
        // Limit cycles to prevent infinite loops on strong keys
        while (d.equals(BigInteger.ONE) && cycles < 100000) {
            x = f(x, c, n);
            y = f(f(y, c, n), c, n);

            // d = gcd(|x-y|, n)
            d = x.subtract(y).abs().gcd(n);
            cycles++;
        }

        if (d.equals(n)) return null; // Failed to factor
        return d;
    }

    // Pollard's f(x) = (x^2 + c) % n
    private static BigInteger f(BigInteger x, BigInteger c, BigInteger n) {
        return x.multiply(x).add(c).mod(n);
    }

    // ========================================================================
    // 🧮 CORE MATH ENGINE (Replaces BouncyCastle)
    // ========================================================================

    private static BigInteger textToPrime(String text) {
        try {
            // SHA-256 Hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));

            // Convert to BigInt
            BigInteger num = new BigInteger(1, hash);

            // Scale to target range and ensure odd
            BigInteger candidate = num.mod(SCALE).or(BigInteger.ONE);

            // Prime Hunter: Walk forward until prime
            // Miller-Rabin test (certainty 20)
            while (!candidate.isProbablePrime(20)) {
                candidate = candidate.add(TWO);
            }

            return candidate;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 Missing - System Compromised");
        }
    }

    /**
     * 🟣 PURPLE TEAM: ORIGIN VERIFICATION
     * Uses the Identity to sign a message without revealing the password.
     */
    public static String sign(String message, KeyPair identity) {
        // Simple signature: Hash(Message + PrivateFactor)
        try {
            String payload = message + identity.getP().toString();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] sig = md.digest(payload.getBytes(StandardCharsets.UTF_8));

            // Return Hex String
            StringBuilder hex = new StringBuilder();
            for (byte b : sig) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) { return "ERR"; }
    }
}
