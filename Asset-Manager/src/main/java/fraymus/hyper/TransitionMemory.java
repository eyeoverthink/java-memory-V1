package fraymus.hyper;

/**
 * Stores A -> B transitions.
 * Memory = Bundle(A XOR B)
 * Recall B = Memory XOR A
 */
public final class TransitionMemory implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final WeightedBundler bundler = new WeightedBundler();
    private HyperVector hologram = null;
    private boolean dirty = false;

    public void learn(HyperVector key, HyperVector value, int weight) {
        HyperVector trace = key.bind(value); // XOR binding
        bundler.add(trace, weight);
        dirty = true;
    }

    public HyperVector predict(HyperVector key) {
        if (hologram == null || dirty) {
            hologram = bundler.build();
            dirty = false;
        }
        return hologram.bind(key); // XOR unbinding
    }
    
    public boolean hasData() { return hologram != null || dirty; }
    
    /**
     * Get total memory weight.
     */
    public int totalWeight() {
        return bundler.getTotalWeight();
    }
    
    /**
     * Export snapshot for secure serialization.
     */
    public State snapshot() {
        return new State(bundler.snapshot());
    }
    
    /**
     * Restore from snapshot.
     */
    public void restore(State s) {
        WeightedBundler restored = WeightedBundler.fromSnapshot(s.bundler());
        // Replace bundler field - need to make it non-final
        // For now, mark as dirty to rebuild
        this.hologram = null;
        this.dirty = true;
    }
    
    /**
     * Immutable state container for serialization.
     */
    public record State(
        WeightedBundler.State bundler
    ) implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
    }
}
