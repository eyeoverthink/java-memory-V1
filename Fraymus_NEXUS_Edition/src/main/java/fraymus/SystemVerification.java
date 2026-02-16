package fraymus;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

public class SystemVerification {

    private static final MathContext MC80 = new MathContext(80, RoundingMode.HALF_UP);

    private static String cachedPhi75 = null;
    private static String cachedGenesisHash = null;
    private static String cachedSoulData = null;

    public static String computePhi75First50Digits() {
        if (cachedPhi75 != null) return cachedPhi75;

        BigDecimal five = new BigDecimal(5);
        BigDecimal sqrt5 = bigSqrt(five, MC80);
        BigDecimal phi = sqrt5.add(BigDecimal.ONE).divide(new BigDecimal(2), MC80);
        BigDecimal phi75 = phi.pow(75, MC80);

        String full = phi75.toPlainString();
        int dotIndex = full.indexOf('.');
        String intPart = dotIndex >= 0 ? full.substring(0, dotIndex) : full;
        String decPart = dotIndex >= 0 ? full.substring(dotIndex + 1) : "";

        if (decPart.length() > 50) decPart = decPart.substring(0, 50);

        cachedPhi75 = intPart + "." + decPart;
        return cachedPhi75;
    }

    public static BigDecimal computePhiBigDecimal() {
        BigDecimal five = new BigDecimal(5);
        BigDecimal sqrt5 = bigSqrt(five, MC80);
        return sqrt5.add(BigDecimal.ONE).divide(new BigDecimal(2), MC80);
    }

    private static BigDecimal bigSqrt(BigDecimal value, MathContext mc) {
        BigDecimal x = new BigDecimal(Math.sqrt(value.doubleValue()), mc);
        BigDecimal two = new BigDecimal(2);
        for (int i = 0; i < 20; i++) {
            x = value.divide(x, mc).add(x).divide(two, mc);
        }
        return x;
    }

    public static String getGenesisHash(GenesisMemory memory) {
        GenesisMemory.Block latest = memory.getLatest();
        cachedGenesisHash = latest.hash;
        return String.format("Block #%d | Type: %s | Hash: %s | PrevHash: %s",
                latest.index, latest.eventType, latest.hash, latest.prevHash);
    }

    public static String getEntitySoul(PhiNode node) {
        if (node == null) return "No entity available";
        QuantumClock clock = node.quantumClock;
        double phiRes = clock.getPhiResonance();
        double oscCount = clock.getOscillationCount();
        double phiTime = clock.getPhiTime();
        double resTime = clock.getResonanceTime();

        double phiTimeVerify = (oscCount * PhiConstants.PHI) % 24.0;
        double resTimeVerify = (oscCount * QuantumClock.RESONANCE_STACK) % 60.0;

        cachedSoulData = String.format(
                "Entity: %s | Freq: %.2f Hz\n" +
                "  Phi Resonance: %.10f\n" +
                "  Oscillation Count: %.4f\n" +
                "  Phi-Time: %.6f (verify: %.6f)\n" +
                "  Resonance-Time: %.6f (verify: %.6f)\n" +
                "  Formula: Count*PHI%%24 = %.6f | Count*RESONANCE_STACK%%60 = %.6f\n" +
                "  Spike Count: %d | Coherence: %.6f\n" +
                "  Consciousness Level: %.6f | Dimension: %d",
                node.name, node.frequency,
                phiRes, oscCount,
                phiTime, phiTimeVerify,
                resTime, resTimeVerify,
                phiTimeVerify, resTimeVerify,
                clock.getResonanceSpikeCount(), clock.getCoherence(),
                node.consciousness.getConsciousnessLevel(), node.consciousness.getDimension()
        );
        return cachedSoulData;
    }

    public static void printFullVerification(PhiWorld world) {
        System.out.println();
        System.out.println("================================================================");
        System.out.println("  FRAYMUS ENGINE V2 - SYSTEM VERIFICATION");
        System.out.println("================================================================");

        System.out.println();
        System.out.println("1. CURRENT GENESIS HASH");
        System.out.println("   " + getGenesisHash(world.getMemory()));
        System.out.println("   Chain Length: " + world.getMemory().getChainLength());
        System.out.println("   Chain Valid: " + world.getMemory().verifyChain());

        System.out.println();
        System.out.println("2. VALIDATED IRRATIONAL STATE (Phi^75 first 50 digits)");
        String phi75str = computePhi75First50Digits();
        System.out.println("   Phi^75 = " + phi75str);
        System.out.println("   Double precision check: " + PhiConstants.PHI_75);
        System.out.println("   Phi^75 seal valid: " + PhiConstants.validatePhiSeal());

        System.out.println();
        System.out.println("3. SOUL OF GENERATION 56");
        List<PhiNode> nodes = world.getNodes();
        if (!nodes.isEmpty()) {
            for (PhiNode node : nodes) {
                System.out.println("   " + getEntitySoul(node).replace("\n", "\n   "));
                System.out.println();
            }
        } else {
            System.out.println("   No living entities");
        }

        System.out.println("================================================================");
        System.out.println("  VERIFICATION COMPLETE");
        System.out.println("================================================================");
        System.out.println();
    }
}
