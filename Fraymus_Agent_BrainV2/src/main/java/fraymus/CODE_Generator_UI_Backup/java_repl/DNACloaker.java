import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

// The Identity Engine
// The cryptographic "soul" of your nodes.
// It converts string seeds into prime number signatures.

public class DNACloaker {
    
    // Result container for identity generation
    public static class IdentityResult {
        public final byte[] hashBytes;      // Compact 32-byte identity for networking/logging
        public final BigInteger signature;   // Prime number "soul"
        public final String hexHash;         // Hex string for display
        
        public IdentityResult(byte[] hash, BigInteger sig) {
            this.hashBytes = hash;
            this.signature = sig;
            this.hexHash = bytesToHex(hash).substring(0, 16); // First 16 chars for compact ID
        }
        
        private static String bytesToHex(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
    }
    
    // Generates a deterministic Prime Identity from a seed string
    // Returns both the hash bytes (compact) and the prime (soul)
    public static IdentityResult generateIdentityFull(String seed) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(seed.getBytes("UTF-8"));
            
            // Convert hash to a positive BigInteger
            BigInteger bigInt = new BigInteger(1, hash);
            
            // Find the next probable prime to ensure it's a valid "Prime Identity"
            // This is the "Uncapped" military-grade logic we discussed
            BigInteger prime = bigInt.nextProbablePrime();
            
            return new IdentityResult(hash, prime);
        } catch (Exception e) {
            throw new RuntimeException("Crypto Failure", e);
        }
    }
    
    // Legacy method - returns just the prime signature
    public static BigInteger generateIdentity(String seed) {
        return generateIdentityFull(seed).signature;
    }

    // Creates a "Lock" (N) from two Nodes
    public static BigInteger entangleIdentities(PhiNode a, PhiNode b) {
        return a.signature.multiply(b.signature);
    }
    
    // Verify if a node's signature matches its DNA seed
    public static boolean verifyIdentity(PhiNode node) {
        BigInteger expected = generateIdentity(node.dnaSeed);
        return node.signature.equals(expected);
    }
}
