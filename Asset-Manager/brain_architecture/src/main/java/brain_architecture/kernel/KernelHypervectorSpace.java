package brain_architecture.kernel;

import fraymus.hdc.SharedHypervectorCore;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

/**
 * Deterministic VSA/HDC utilities used by the offline kernel.
 */
public final class KernelHypervectorSpace {
    public static final int DIMENSION = 16_384;
    private final SharedHypervectorCore sharedCore;

    public KernelHypervectorSpace() {
        this.sharedCore = new SharedHypervectorCore();
    }

    public BitSet seed(String token) {
        return sharedCore.seed(token);
    }

    public BitSet bind(BitSet left, BitSet right) {
        return sharedCore.bind(left, right);
    }

    public BitSet permute(BitSet vector, int shift) {
        return sharedCore.permute(vector, shift);
    }

    public BitSet bundle(Collection<BitSet> vectors) {
        return sharedCore.bundle(vectors);
    }

    public double hammingSimilarity(BitSet left, BitSet right) {
        return sharedCore.hammingSimilarity(left, right);
    }

    public double cosineSimilarity(BitSet left, BitSet right) {
        return sharedCore.cosineSimilarity(left, right);
    }

    public BitSet encodeText(String text) {
        return sharedCore.encodeText(text);
    }

    public List<String> tokenize(String text) {
        return new ArrayList<>(sharedCore.tokenize(text));
    }
}
