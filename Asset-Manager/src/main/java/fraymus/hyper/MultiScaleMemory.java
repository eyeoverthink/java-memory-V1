package fraymus.hyper;

import java.util.List;

/**
 * Stores 1-gram, 2-gram, and 3-gram patterns.
 */
public final class MultiScaleMemory implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final TransitionMemory uni = new TransitionMemory();
    private final TransitionMemory bi  = new TransitionMemory();
    private final TransitionMemory tri = new TransitionMemory();

    public void learnSequence(List<HyperVector> seq) {
        if (seq.size() < 2) return;
        
        for (int i = 0; i < seq.size() - 1; i++) {
            HyperVector target = seq.get(i+1);
            
            // 1-gram: P(t) -> t+1
            HyperVector k1 = seq.get(i).permute(1);
            uni.learn(k1, target, 1);
            
            // 2-gram: P(P(t-1)) + P(t) -> t+1
            if (i >= 1) {
                HyperVector k2 = seq.get(i-1).permute(2).bind(seq.get(i).permute(1));
                bi.learn(k2, target, 2);
            }
        }
    }

    public HyperVector predict(List<HyperVector> context) {
        int n = context.size();
        HyperVector last = context.get(n - 1);
        
        WeightedBundler mix = new WeightedBundler();
        
        // 1-gram Prediction
        mix.add(uni.predict(last.permute(1)), 1);
        
        // 2-gram Prediction
        if (n >= 2) {
             HyperVector k2 = context.get(n-2).permute(2).bind(last.permute(1));
             mix.add(bi.predict(k2), 2);
        }
        
        return mix.build();
    }
    
    public boolean hasData() { return uni.hasData(); }
    
    /**
     * Get total memory weight.
     */
    public int totalWeight() {
        return uni.totalWeight() + bi.totalWeight() + tri.totalWeight();
    }
    
    /**
     * Export snapshot for secure serialization.
     */
    public State snapshot() {
        return new State(
            uni.snapshot(),
            bi.snapshot(),
            tri.snapshot()
        );
    }
    
    /**
     * Restore from snapshot.
     */
    public void restore(State s) {
        uni.restore(s.uni());
        bi.restore(s.bi());
        tri.restore(s.tri());
    }
    
    /**
     * Immutable state container for serialization.
     */
    public record State(
        TransitionMemory.State uni,
        TransitionMemory.State bi,
        TransitionMemory.State tri
    ) implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
    }
}
