package fraymus.hyper;

import java.io.Serializable;
import java.util.List;

/**
 * HOLO-ATTENTION:
 * - No N^2 token-token comparisons.
 * - Compresses entire history into a single fixed-size hologram using permutation.
 * - "Resonance clustering" = only bundle tokens that resonate with query above threshold.
 */
public final class HoloAttention implements Serializable {
    private static final long serialVersionUID = 1L;

    private final NcaDenoiser denoiser = new NcaDenoiser();

    /**
     * Build Context Hologram: Σ permute(token_i, timeDistance)
     * Optional resonance clustering:
     *   include token only if resonance(query, token) >= threshold
     */
    public HyperVector buildContext(List<HyperVector> sequence, HyperVector query, double threshold) {
        HyperAccumulator acc = new HyperAccumulator();

        for (int i = 0; i < sequence.size(); i++) {
            HyperVector token = sequence.get(i);
            int timeDistance = (sequence.size() - 1) - i;
            HyperVector temporal = token.permute(timeDistance);

            if (threshold <= 0.0 || query.resonance(token) >= threshold) {
                acc.add(temporal);
            }
        }

        HyperVector context = acc.toVector();
        return denoiser.denoise(context, 2);
    }

    /** Convenience: no threshold (include all tokens). */
    public HyperVector buildContext(List<HyperVector> sequence) {
        if (sequence.isEmpty()) return new HyperVector();
        return buildContext(sequence, sequence.get(sequence.size() - 1), 0.0);
    }

    /**
     * Query the hologram:
     * answer = context XOR query (since inverse(query)=query in XOR space)
     */
    public HyperVector attend(List<HyperVector> sequence, HyperVector query, double threshold) {
        HyperVector context = buildContext(sequence, query, threshold);
        return context.bind(query);
    }

    /** Convenience: no threshold. */
    public HyperVector attend(List<HyperVector> sequence, HyperVector query) {
        return attend(sequence, query, 0.0);
    }
}
