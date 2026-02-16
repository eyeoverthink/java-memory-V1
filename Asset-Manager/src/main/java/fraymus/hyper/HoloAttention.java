package fraymus.hyper;

import java.util.List;

public final class HoloAttention implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Collapses sequence into a single holographic state.
     * H = V_t + P(V_{t-1}) + P(P(V_{t-2}))...
     */
    public HyperVector attend(List<HyperVector> seq) {
        WeightedBundler b = new WeightedBundler();
        int n = seq.size();
        for (int i = 0; i < n; i++) {
            int dist = n - 1 - i;
            b.add(seq.get(i).permute(dist), 1);
        }
        return b.build();
    }

}
