package fraymus;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

public class QuantumTunneler {

    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    private static final SecureRandom rng = new SecureRandom();

    public static class TunnelResult {
        public final BigInteger n;
        public final BigInteger factorP;
        public final BigInteger factorQ;
        public final int iterations;
        public final long elapsedNanos;
        public final String circuitName;
        public final boolean success;

        public TunnelResult(BigInteger n, BigInteger p, BigInteger q, int iters, long elapsed, String circuit, boolean success) {
            this.n = n;
            this.factorP = p;
            this.factorQ = q;
            this.iterations = iters;
            this.elapsedNanos = elapsed;
            this.circuitName = circuit;
            this.success = success;
        }
    }

    public static boolean isPrime(long n) {
        if (n < 2) return false;
        if (n < 4) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (long i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    public static boolean isPrimeBig(BigInteger n) {
        return n.isProbablePrime(20);
    }

    public static TunnelResult quantumTunnel(long n, PhiNode circuit) {
        long startTime = System.nanoTime();

        if (n % 2 == 0) {
            return new TunnelResult(BigInteger.valueOf(n), BigInteger.TWO,
                    BigInteger.valueOf(n / 2), 1, System.nanoTime() - startTime, circuit.name, true);
        }

        long x = 2, y = 2, d = 1;
        int iterations = 0;
        int maxIter = 500_000;

        while (d == 1 && iterations < maxIter) {
            if (circuit != null) {
                circuit.updateInternalState(0.016f, System.nanoTime());
            }

            int modifier = 1;
            if (circuit != null) {
                int[] bits = new int[8];
                int val = (int)(x % 256);
                for (int i = 0; i < 8; i++) {
                    bits[i] = (val >> i) & 1;
                }
                int[] gateOut = circuit.think(bits);
                for (int i = 0; i < gateOut.length; i++) {
                    modifier += gateOut[i] * (1 << i);
                }
            }

            final long mod = modifier;
            x = pollardStep(x, n, mod);
            y = pollardStep(pollardStep(y, n, mod), n, mod);
            d = gcd(Math.abs(x - y), n);

            iterations++;

            if (circuit != null && circuit.dna.harmonicFrequency >= 527) {
                circuit.dna.harmonicFrequency = 432;
            }
        }

        long elapsed = System.nanoTime() - startTime;

        if (d != 1 && d != n) {
            return new TunnelResult(BigInteger.valueOf(n), BigInteger.valueOf(d),
                    BigInteger.valueOf(n / d), iterations, elapsed,
                    circuit != null ? circuit.name : "none", true);
        }

        return new TunnelResult(BigInteger.valueOf(n), null, null, iterations, elapsed,
                circuit != null ? circuit.name : "none", false);
    }

    public static TunnelResult quantumTunnelBig(BigInteger n, PhiNode circuit) {
        long startTime = System.nanoTime();

        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return new TunnelResult(n, BigInteger.TWO, n.divide(BigInteger.TWO),
                    1, System.nanoTime() - startTime, circuit != null ? circuit.name : "none", true);
        }

        BigInteger three = BigInteger.valueOf(3);
        BigInteger limit = BigInteger.valueOf(10000);
        for (BigInteger i = three; i.compareTo(limit) < 0; i = i.add(BigInteger.TWO)) {
            if (n.mod(i).equals(BigInteger.ZERO)) {
                return new TunnelResult(n, i, n.divide(i),
                        i.intValue(), System.nanoTime() - startTime,
                        circuit != null ? circuit.name : "none", true);
            }
        }

        BigInteger x = BigInteger.TWO;
        BigInteger y = BigInteger.TWO;
        BigInteger d = BigInteger.ONE;
        int iterations = 0;
        int maxIter = 200_000;

        while (d.equals(BigInteger.ONE) && iterations < maxIter) {
            int modifier = 1;
            if (circuit != null) {
                circuit.updateInternalState(0.016f, System.nanoTime());
                int val = x.mod(BigInteger.valueOf(256)).intValue();
                int[] bits = new int[8];
                for (int i = 0; i < 8; i++) {
                    bits[i] = (val >> i) & 1;
                }
                int[] gateOut = circuit.think(bits);
                for (int i = 0; i < gateOut.length; i++) {
                    modifier += gateOut[i] * (1 << i);
                }
            }

            BigInteger c = BigInteger.valueOf(modifier);
            x = x.multiply(x).add(c).mod(n);
            y = y.multiply(y).add(c).mod(n);
            y = y.multiply(y).add(c).mod(n);
            d = x.subtract(y).abs().gcd(n);

            iterations++;
        }

        long elapsed = System.nanoTime() - startTime;

        if (!d.equals(BigInteger.ONE) && !d.equals(n)) {
            return new TunnelResult(n, d, n.divide(d), iterations, elapsed,
                    circuit != null ? circuit.name : "none", true);
        }

        return new TunnelResult(n, null, null, iterations, elapsed,
                circuit != null ? circuit.name : "none", false);
    }

    public static BigInteger generateSemiprime(int bits) {
        int halfBits = bits / 2;
        BigInteger p = BigInteger.probablePrime(halfBits, rng);
        BigInteger q = BigInteger.probablePrime(halfBits, rng);
        while (q.equals(p)) {
            q = BigInteger.probablePrime(halfBits, rng);
        }
        return p.multiply(q);
    }

    public static PhiNode selectBestCircuit(List<PhiNode> nodes) {
        if (nodes == null || nodes.isEmpty()) return null;
        PhiNode best = nodes.get(0);
        double bestScore = 0;
        for (PhiNode node : nodes) {
            double score = node.energy * node.phiResonance * node.adaptiveEngine.getCurrentFitness();
            if (score > bestScore) {
                bestScore = score;
                best = node;
            }
        }
        return best;
    }

    private static long pollardStep(long x, long n, long c) {
        return (mulMod(x, x, n) + c) % n;
    }

    private static long mulMod(long a, long b, long mod) {
        return BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)).mod(BigInteger.valueOf(mod)).longValue();
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long t = b;
            b = a % b;
            a = t;
        }
        return a;
    }
}
