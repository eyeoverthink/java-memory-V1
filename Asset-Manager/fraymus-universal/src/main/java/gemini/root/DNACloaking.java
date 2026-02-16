package gemini.root;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.*;

/**
 * DNA CLOAKING & PHI-HARMONIC MATHEMATICS
 * 
 * Ported from nervous_system.py GEN 195
 * 
 * - PHI (φ = 1.618033988749895) governs all transformations
 * - DNA fingerprints for traceable code evolution
 * - Prime-based compression for infinite memory
 */
public class DNACloaking {

    public static final double PHI = 1.618033988749895;
    
    private final Map<String, MemoryEntry> memory = new LinkedHashMap<>();

    public static class MemoryEntry {
        public final String data;        // compressed base64
        public final double phiRatio;
        public final int originalSize;

        public MemoryEntry(String data, double phiRatio, int originalSize) {
            this.data = data;
            this.phiRatio = phiRatio;
            this.originalSize = originalSize;
        }
    }

    /**
     * Generate PHI-anchored fingerprint from data.
     * φ-{hex} format for traceable evolution.
     */
    public String generatePhiHash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);
            
            // Take first 16 hex chars (64 bits), scale by PHI
            String hex16 = bytesToHex(hash).substring(0, 16);
            BigInteger val = new BigInteger(hex16, 16);
            double phiSig = val.doubleValue() * PHI;
            
            return "φ-" + Long.toHexString((long) phiSig);
        } catch (Exception e) {
            return "φ-error";
        }
    }

    public String generatePhiHash(String text) {
        return generatePhiHash(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Convert text to prime number for compression.
     * Uses Miller-Rabin primality test.
     */
    public BigInteger textToPrime(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));
            BigInteger p = new BigInteger(1, hash).or(BigInteger.ONE); // ensure odd
            
            while (!isPrime(p)) {
                p = p.add(BigInteger.TWO);
            }
            return p;
        } catch (Exception e) {
            return BigInteger.valueOf(2);
        }
    }

    /**
     * Miller-Rabin primality test (5 rounds).
     */
    public boolean isPrime(BigInteger n) {
        if (n.compareTo(BigInteger.TWO) < 0) return false;
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) return true;
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) return false;

        // n-1 = 2^r * s
        BigInteger s = n.subtract(BigInteger.ONE);
        int r = 0;
        while (s.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            r++;
            s = s.divide(BigInteger.TWO);
        }

        Random rnd = new Random();
        for (int i = 0; i < 5; i++) {
            BigInteger a = new BigInteger(n.bitLength() - 1, rnd).add(BigInteger.TWO);
            if (a.compareTo(n.subtract(BigInteger.TWO)) > 0) {
                a = n.subtract(BigInteger.TWO);
            }

            BigInteger x = a.modPow(s, n);
            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) continue;

            boolean composite = true;
            for (int j = 0; j < r - 1; j++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(n.subtract(BigInteger.ONE))) {
                    composite = false;
                    break;
                }
            }
            if (composite) return false;
        }
        return true;
    }

    /**
     * PHI-scaled compression using DEFLATE.
     * Returns [compressed_base64, phi_ratio].
     */
    public Object[] compress(byte[] data) {
        try {
            Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
            deflater.setInput(data);
            deflater.finish();

            byte[] buffer = new byte[data.length + 100];
            int len = deflater.deflate(buffer);
            deflater.end();

            byte[] compressed = Arrays.copyOf(buffer, len);
            double ratio = (double) data.length / len;
            double phiRatio = ratio / PHI;

            String b64 = Base64.getEncoder().encodeToString(compressed);
            return new Object[]{b64, phiRatio};
        } catch (Exception e) {
            return new Object[]{"", 0.0};
        }
    }

    public Object[] compress(String text) {
        return compress(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decompress from base64 DEFLATE.
     */
    public String decompress(String b64) {
        try {
            byte[] compressed = Base64.getDecoder().decode(b64);
            Inflater inflater = new Inflater();
            inflater.setInput(compressed);

            byte[] buffer = new byte[compressed.length * 10];
            int len = inflater.inflate(buffer);
            inflater.end();

            return new String(buffer, 0, len, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Store in infinite memory with phi-hash key.
     */
    public String store(String key, String value) {
        String phiKey = generatePhiHash(key);
        Object[] comp = compress(value);
        String data = (String) comp[0];
        double phiRatio = (Double) comp[1];

        memory.put(phiKey, new MemoryEntry(data, phiRatio, value.length()));
        return phiKey;
    }

    /**
     * Retrieve from infinite memory.
     */
    public String retrieve(String phiKey) {
        MemoryEntry e = memory.get(phiKey);
        if (e == null) return null;
        return decompress(e.data);
    }

    /**
     * Generate DNA lock from file content (product of two primes).
     */
    public BigInteger generateLock(byte[] content) {
        int mid = content.length / 2;
        byte[] a = Arrays.copyOfRange(content, 0, mid);
        byte[] b = Arrays.copyOfRange(content, mid, content.length);

        BigInteger primeA = textToPrime(new String(a, StandardCharsets.UTF_8));
        BigInteger primeB = textToPrime(new String(b, StandardCharsets.UTF_8));

        return primeA.multiply(primeB);
    }

    public int memorySize() {
        return memory.size();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
