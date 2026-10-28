package fraymus.facade;

// SELFBUILD_TARGET Asset-Manager/src/main/java/fraymus/facade/ExperimentFacade.java
// SELFBUILD_GOAL promote facade selfbuild set
// SELFBUILD_FINGERPRINT φ-5667106D7557

import fraymus.QuantumTunneler;
import fraymus.HashReverser;
import fraymus.RSASandbox;
import java.math.BigInteger;

/**
 * EXPERIMENT FACADE - Abstracted from ExperimentManager
 * 
 * Exposes crypto/math experiments without requiring PhiWorld,
 * PhiNode, CommandTerminal, FraymusUI, or any AGI subsystems.
 * 
 * Pure static utility calls → String output. Zero heavy deps.
 * 
 * φ^∞ © 2026 Vaughn Scott
 */
public class ExperimentFacade {

    // ═══════════════════════════════════════════════════════════════════
    // PRIME TEST (Quantum Tunneler)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Test if a number is prime using Miller-Rabin.
     */
    public String primeTest(String numberStr) {
        if (numberStr == null || numberStr.isEmpty()) {
            return "   Usage: experiment prime <number>";
        }
        try {
            long n = Long.parseLong(numberStr.trim());
            boolean result = QuantumTunneler.isPrime(n);
            return String.format("   [PRIME] %d is %s", n, result ? "PRIME ✓" : "NOT prime");
        } catch (NumberFormatException e) {
            try {
                BigInteger big = new BigInteger(numberStr.trim());
                boolean result = QuantumTunneler.isPrimeBig(big);
                return String.format("   [PRIME] %s is %s (Miller-Rabin)", 
                    truncate(big.toString(), 40), result ? "PROBABLY PRIME ✓" : "NOT prime");
            } catch (NumberFormatException e2) {
                return "   [!] Invalid number: " + numberStr;
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // FACTORING (Quantum Tunnel)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Factor a number using quantum tunneling algorithm.
     */
    public String factor(String numberStr) {
        if (numberStr == null || numberStr.isEmpty()) {
            return "   Usage: experiment factor <number>";
        }
        try {
            long n = Long.parseLong(numberStr.trim());
            if (n < 2) return "   [!] Number must be >= 2";

            QuantumTunneler.TunnelResult result = QuantumTunneler.quantumTunnel(n, null);
            if (result.success) {
                return String.format(
                    "   [FACTOR] TUNNELED in %d iterations (%.3f ms)\n" +
                    "     %d = %s × %s\n" +
                    "     Verified: %s",
                    result.iterations, result.elapsedNanos / 1_000_000.0,
                    n, result.factorP, result.factorQ,
                    result.factorP.multiply(result.factorQ).equals(BigInteger.valueOf(n)) ? "YES ✓" : "NO ✗");
            } else {
                String extra = QuantumTunneler.isPrime(n) ? " (number is prime)" : "";
                return String.format("   [FACTOR] Failed after %d iterations%s", result.iterations, extra);
            }
        } catch (NumberFormatException e) {
            try {
                BigInteger big = new BigInteger(numberStr.trim());
                QuantumTunneler.TunnelResult result = QuantumTunneler.quantumTunnelBig(big, null);
                if (result.success) {
                    return String.format(
                        "   [FACTOR] TUNNELED in %d iterations (%.3f ms)\n" +
                        "     = %s × %s",
                        result.iterations, result.elapsedNanos / 1_000_000.0,
                        result.factorP, result.factorQ);
                } else {
                    return String.format("   [FACTOR] Failed after %d iterations", result.iterations);
                }
            } catch (NumberFormatException e2) {
                return "   [!] Invalid number: " + numberStr;
            }
        }
    }

    /**
     * Generate and crack a semiprime of given bit size.
     */
    public String tunnel(String bitsStr) {
        int bits = 32;
        if (bitsStr != null && !bitsStr.isEmpty()) {
            try {
                bits = Integer.parseInt(bitsStr.trim());
                bits = Math.max(8, Math.min(128, bits));
            } catch (NumberFormatException e) {
                return "   Usage: experiment tunnel <bits>";
            }
        }

        BigInteger n = QuantumTunneler.generateSemiprime(bits);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("   [TUNNEL] Generated %d-bit semiprime: %s\n", bits, truncate(n.toString(), 40)));

        QuantumTunneler.TunnelResult result;
        if (bits <= 64) {
            result = QuantumTunneler.quantumTunnel(n.longValue(), null);
        } else {
            result = QuantumTunneler.quantumTunnelBig(n, null);
        }

        if (result.success) {
            sb.append(String.format("   [TUNNEL] CRACKED in %d iterations (%.3f ms)\n",
                result.iterations, result.elapsedNanos / 1_000_000.0));
            sb.append(String.format("     p = %s\n", result.factorP));
            sb.append(String.format("     q = %s\n", result.factorQ));
            sb.append(String.format("     Verified: %s",
                result.factorP.multiply(result.factorQ).equals(n) ? "YES ✓" : "NO ✗"));
        } else {
            sb.append(String.format("   [TUNNEL] Failed after %d iterations", result.iterations));
        }
        return sb.toString();
    }

    // ═══════════════════════════════════════════════════════════════════
    // PHI-HARMONIC HASH
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Generate phi-harmonic hash of text.
     */
    public String hash(String text) {
        if (text == null || text.isEmpty()) {
            return "   Usage: experiment hash <text>";
        }
        HashReverser.HashResult result = HashReverser.phiHash(text);
        return String.format(
            "   [HASH] Phi-Harmonic Hash\n" +
            "     Input:     %s\n" +
            "     SHA-256:   %s\n" +
            "     φ Reson:   %.10f\n" +
            "     Harmonic:  %.2f Hz",
            result.input, result.hash, result.phiResonance, result.harmonicFrequency);
    }

    // ═══════════════════════════════════════════════════════════════════
    // RSA CHALLENGE
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Run RSA challenge: generate keypair, encrypt, crack, decrypt.
     */
    public String rsaChallenge(String bitsStr) {
        int bits = 16;
        if (bitsStr != null && !bitsStr.isEmpty()) {
            try {
                bits = Integer.parseInt(bitsStr.trim());
                bits = Math.max(8, Math.min(64, bits));
            } catch (NumberFormatException e) {
                return "   Usage: experiment rsa <bits>";
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("   [RSA] === %d-BIT CHALLENGE ===\n", bits));

        // Blue Team: Generate keypair
        RSASandbox.BlueTeam blue = new RSASandbox.BlueTeam(bits);
        String nHex = blue.N.toString(16);
        sb.append(String.format("   [BLUE] Public N: %s (%d bits)\n",
            truncate(nHex, 32), blue.N.bitLength()));

        // Encrypt
        String secret = bits <= 16 ? "PHI" : "ALIVE";
        sb.append(String.format("   [BLUE] Encrypting: \"%s\"\n", secret));
        BigInteger cipher = blue.encrypt(secret);

        // Red Team: Crack
        RSASandbox.RedTeam red = new RSASandbox.RedTeam();
        long start = System.nanoTime();
        BigInteger[] factors = red.crack(blue.N);
        long elapsed = System.nanoTime() - start;

        if (factors != null) {
            sb.append(String.format("   [RED]  CRACKED in %d steps (%.3f ms)\n",
                red.getSteps(), elapsed / 1_000_000.0));
            sb.append(String.format("     p = %s\n", factors[0]));
            sb.append(String.format("     q = %s\n", factors[1]));

            BigInteger stolenD = red.derivePrivateKey(factors[0], factors[1], blue.e);
            String decrypted = red.decryptMessage(cipher, stolenD, blue.N);
            sb.append(String.format("   [RED]  DECRYPTED: \"%s\"\n", decrypted));
            sb.append(String.format("   [RSA]  %s", 
                decrypted.equals(secret) ? "RSA DEFEATED ✓" : "Decryption mismatch ✗"));
        } else {
            sb.append(String.format("   [RED]  Factorization failed after %d steps", red.getSteps()));
        }
        return sb.toString();
    }

    // ═══════════════════════════════════════════════════════════════════
    // STATUS
    // ═══════════════════════════════════════════════════════════════════

    public String getStatus() {
        return "═══════════════════════════════════════════\n" +
            "  ⚗️ EXPERIMENT FACADE STATUS\n" +
            "═══════════════════════════════════════════\n" +
            "  Available Experiments:\n" +
            "    prime <n>     Test primality (Miller-Rabin)\n" +
            "    factor <n>    Quantum tunnel factorization\n" +
            "    tunnel [bits] Generate + crack semiprime\n" +
            "    hash <text>   Phi-harmonic SHA-256\n" +
            "    rsa [bits]    RSA generate/encrypt/crack/decrypt\n" +
            "  Dependencies:   NONE (all static utility calls)\n";
    }

    private static String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}
