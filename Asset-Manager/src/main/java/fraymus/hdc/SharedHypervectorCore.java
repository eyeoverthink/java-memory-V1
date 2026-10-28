package fraymus.hdc;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.SplittableRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shared deterministic HDC/VSA core used by both legacy and kernel runtime paths.
 */
public final class SharedHypervectorCore {
    public static final int DIMENSION = 16_384;
    private static final Pattern TOKEN_PATTERN = Pattern.compile("[a-zA-Z0-9_./\\\\-]{2,}");

    public BitSet seed(String token) {
        BitSet vector = new BitSet(DIMENSION);
        SplittableRandom random = new SplittableRandom(hash64(token));
        for (int i = 0; i < DIMENSION; i++) {
            if (random.nextBoolean()) {
                vector.set(i);
            }
        }
        return vector;
    }

    public BitSet bind(BitSet left, BitSet right) {
        BitSet result = (BitSet) left.clone();
        result.xor(right);
        return result;
    }

    public BitSet permute(BitSet vector, int shift) {
        BitSet result = new BitSet(DIMENSION);
        int normalized = Math.floorMod(shift, DIMENSION);
        for (int bit = vector.nextSetBit(0); bit >= 0; bit = vector.nextSetBit(bit + 1)) {
            result.set((bit + normalized) % DIMENSION);
        }
        return result;
    }

    public BitSet bundle(Collection<BitSet> vectors) {
        int[] counts = new int[DIMENSION];
        for (BitSet vector : vectors) {
            for (int i = 0; i < DIMENSION; i++) {
                counts[i] += vector.get(i) ? 1 : -1;
            }
        }
        BitSet bundled = new BitSet(DIMENSION);
        for (int i = 0; i < DIMENSION; i++) {
            if (counts[i] > 0) {
                bundled.set(i);
            }
        }
        return bundled;
    }

    public double hammingSimilarity(BitSet left, BitSet right) {
        BitSet delta = (BitSet) left.clone();
        delta.xor(right);
        return 1.0 - ((double) delta.cardinality() / DIMENSION);
    }

    public double cosineSimilarity(BitSet left, BitSet right) {
        BitSet delta = (BitSet) left.clone();
        delta.xor(right);
        double dot = DIMENSION - (2.0 * delta.cardinality());
        return dot / DIMENSION;
    }

    public BitSet encodeText(String text) {
        List<String> tokens = tokenize(text);
        if (tokens.isEmpty()) {
            return seed("_empty");
        }
        List<BitSet> vectors = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            BitSet role = permute(seed(tokens.get(i)), i + 1);
            vectors.add(role);
        }
        return bundle(vectors);
    }

    public List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return tokens;
        }
        Matcher matcher = TOKEN_PATTERN.matcher(text.toLowerCase(Locale.ROOT));
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

    public double[] toSignedArray(BitSet vector) {
        double[] signed = new double[DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            signed[i] = vector.get(i) ? 1.0 : -1.0;
        }
        return signed;
    }

    public BitSet fromSignedArray(double[] vector) {
        BitSet bitSet = new BitSet(DIMENSION);
        int limit = Math.min(DIMENSION, vector == null ? 0 : vector.length);
        for (int i = 0; i < limit; i++) {
            if (vector[i] > 0.0) {
                bitSet.set(i);
            }
        }
        return bitSet;
    }

    private long hash64(String value) {
        long hash = 0xcbf29ce484222325L;
        for (char c : value.toCharArray()) {
            hash ^= c;
            hash *= 0x100000001b3L;
        }
        return hash;
    }
}
