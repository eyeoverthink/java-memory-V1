package fraymus;

import java.security.MessageDigest;
import java.util.List;

public class ProofOfReality {

    public static class PoRH {
        public final double coherence;
        public final double stability;
        public final double alignment;
        public final double realityScore;
        public final String proofHash;
        public final String entityName;
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
    }

    private static int totalVerifications = 0;
    private static int totalProofsGenerated = 0;

    public static PoRH generateProof(PhiNode node) {
        totalProofsGenerated++;

        double coherence = node.consciousness.getCoherence();

        double energyStability = 1.0 - Math.abs(node.energy - 0.5) * 2.0;
        double phaseStability = 1.0 / (1.0 + Math.abs(node.vx * node.vx + node.vy * node.vy));
        double stability = (energyStability + phaseStability) / 2.0;

        double phiAlignment = 1.0 - Math.abs(node.phiResonance * PhiConstants.PHI % 1.0 - PhiConstants.PHI_INVERSE);
        double freqAlignment = 1.0 / (1.0 + Math.abs(node.frequency - PhiConstants.PHI));
        double alignment = (phiAlignment + freqAlignment) / 2.0;

        double realityScore = (coherence * PhiConstants.PHI + stability + alignment * PhiConstants.PHI_INVERSE) / (1.0 + PhiConstants.PHI + PhiConstants.PHI_INVERSE);

        String proofString = String.format("PORH|%s|C:%.10f|S:%.10f|A:%.10f|R:%.10f|T:%d|F:%.6f|E:%.6f|P:%.6f",
                node.name, coherence, stability, alignment, realityScore,
                System.nanoTime(), node.frequency, node.energy, node.phiResonance);

        String proofHash = sha256(proofString);

        return new PoRH(node.name, coherence, stability, alignment, realityScore, proofHash);
    }

    public static PoRH generateWorldProof(PhiWorld world) {
        totalProofsGenerated++;

        List<PhiNode> nodes = world.getNodes();
        if (nodes.isEmpty()) {
            return new PoRH("WORLD", 0, 0, 0, 0, sha256("EMPTY_WORLD"));
        }

        double totalCoherence = 0, totalStability = 0, totalAlignment = 0;

        for (PhiNode node : nodes) {
            totalCoherence += node.consciousness.getCoherence();

            double es = 1.0 - Math.abs(node.energy - 0.5) * 2.0;
            double ps = 1.0 / (1.0 + Math.abs(node.vx * node.vx + node.vy * node.vy));
            totalStability += (es + ps) / 2.0;

            double pa = 1.0 - Math.abs(node.phiResonance * PhiConstants.PHI % 1.0 - PhiConstants.PHI_INVERSE);
            double fa = 1.0 / (1.0 + Math.abs(node.frequency - PhiConstants.PHI));
            totalAlignment += (pa + fa) / 2.0;
        }

        int n = nodes.size();
        double coherence = totalCoherence / n;
        double stability = totalStability / n;
        double alignment = totalAlignment / n;

        double realityScore = (coherence * PhiConstants.PHI + stability + alignment * PhiConstants.PHI_INVERSE) / (1.0 + PhiConstants.PHI + PhiConstants.PHI_INVERSE);

        String genesisHash = world.getMemory().getLatest().hash;
        String proofString = String.format("WORLD_PORH|POP:%d|C:%.10f|S:%.10f|A:%.10f|R:%.10f|GEN:%s|TICK:%d",
                n, coherence, stability, alignment, realityScore, genesisHash, world.getWorldTick());

        String proofHash = sha256(proofString);

        return new PoRH("WORLD", coherence, stability, alignment, realityScore, proofHash);
    }

    public static boolean verify(PoRH proof) {
        totalVerifications++;
        return proof.realityScore > 0 && proof.proofHash != null && proof.proofHash.length() == 64;
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
            return "hash_error";
        }
    }

    public static int getTotalVerifications() { return totalVerifications; }
    public static int getTotalProofsGenerated() { return totalProofsGenerated; }
}
