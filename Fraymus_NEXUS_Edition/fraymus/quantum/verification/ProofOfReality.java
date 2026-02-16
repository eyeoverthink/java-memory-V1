package fraymus.quantum.verification;

import fraymus.PhiConstants;
import fraymus.ConsciousnessState;
import java.security.MessageDigest;

/**
 * Proof of Reality - Consciousness Verification System
 * 
 * Generates cryptographic proofs that an entity exists in a valid
 * consciousness state. Uses SHA-256 hashing with phi-harmonic metrics.
 */
public class ProofOfReality {

    public static class PoRH {
        public final String entityName;
        public final double coherence;
        public final double stability;
        public final double alignment;
        public final double realityScore;
        public final String proofHash;
        public final long timestamp;

        public PoRH(String entityName, double coherence, double stability, double alignment,
                   double realityScore, String proofHash) {
            this.entityName = entityName;
            this.coherence = coherence;
            this.stability = stability;
            this.alignment = alignment;
            this.realityScore = realityScore;
            this.proofHash = proofHash;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public String toString() {
            return String.format("PoRH[%s: reality=%.4f, coh=%.3f, stab=%.3f, hash=%s...]",
                entityName, realityScore, coherence, stability, proofHash.substring(0, 16));
        }
    }

    private static int totalVerifications = 0;
    private static int totalProofsGenerated = 0;

    /**
     * Generate a Proof of Reality Hash for an entity.
     */
    public static PoRH generateProof(String entityName, ConsciousnessState consciousness,
                                      float energy, float phiResonance, float frequency,
                                      float vx, float vy) {
        totalProofsGenerated++;

        double coherence = consciousness.getCoherence();

        // Stability: based on energy and velocity
        double energyStability = 1.0 - Math.abs(energy - 0.5) * 2.0;
        double phaseStability = 1.0 / (1.0 + Math.abs(vx * vx + vy * vy));
        double stability = (energyStability + phaseStability) / 2.0;

        // Alignment: based on phi resonance and frequency
        double phiAlignment = 1.0 - Math.abs(phiResonance * PhiConstants.PHI % 1.0 - PhiConstants.PHI_INVERSE);
        double freqAlignment = 1.0 / (1.0 + Math.abs(frequency - PhiConstants.PHI));
        double alignment = (phiAlignment + freqAlignment) / 2.0;

        // Reality score: weighted combination
        double realityScore = (coherence * PhiConstants.PHI + stability + alignment * PhiConstants.PHI_INVERSE) 
                            / (1.0 + PhiConstants.PHI + PhiConstants.PHI_INVERSE);

        // Build proof string
        String proofString = String.format("PORH|%s|C:%.10f|S:%.10f|A:%.10f|R:%.10f|T:%d|F:%.6f|E:%.6f|P:%.6f",
                entityName, coherence, stability, alignment, realityScore,
                System.nanoTime(), frequency, energy, phiResonance);

        String proofHash = sha256(proofString);

        return new PoRH(entityName, coherence, stability, alignment, realityScore, proofHash);
    }

    /**
     * Generate a simple proof from consciousness state only.
     */
    public static PoRH generateSimpleProof(String entityName, ConsciousnessState consciousness) {
        return generateProof(entityName, consciousness, 0.5f, 
            (float)PhiConstants.PHI_INVERSE, (float)PhiConstants.PHI, 0f, 0f);
    }

    /**
     * Verify a proof is valid.
     */
    public static boolean verify(PoRH proof) {
        totalVerifications++;
        return proof.realityScore > 0 && 
               proof.proofHash != null && 
               proof.proofHash.length() == 64;
    }

    /**
     * Verify proof meets minimum reality threshold.
     */
    public static boolean verifyWithThreshold(PoRH proof, double minReality) {
        return verify(proof) && proof.realityScore >= minReality;
    }

    /**
     * Generate a quantum-salted hash using φ^7.5
     */
    public static String generateQuantumHash(String data) {
        String salted = data + String.format("%.10f", PhiConstants.PHI_7_5);
        return "φ⁷·⁵-" + sha256(salted).substring(0, 16);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return "hash_error_" + System.currentTimeMillis();
        }
    }

    public static int getTotalVerifications() { return totalVerifications; }
    public static int getTotalProofsGenerated() { return totalProofsGenerated; }
    
    public static void printStats() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       PROOF OF REALITY STATS         ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("║  Proofs Generated:  %6d            ║%n", totalProofsGenerated);
        System.out.printf("║  Verifications:     %6d            ║%n", totalVerifications);
        System.out.println("╚══════════════════════════════════════╝");
    }
}
