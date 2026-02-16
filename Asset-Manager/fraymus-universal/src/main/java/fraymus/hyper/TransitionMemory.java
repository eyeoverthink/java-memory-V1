package fraymus.hyper;

import java.io.Serializable;

/**
 * XOR associative memory:
 *  - Store traces: T = key XOR value
 *  - Memory hologram M = bundle(T...)
 *  - Retrieve: value ~= M XOR key
 */
public final class TransitionMemory implements Serializable {

    private static final long serialVersionUID = 1L;

    private WeightedBundler bundler = new WeightedBundler();
    private HyperVector hologram = null;
    private boolean dirty = false;

    private static final int MAX_WEIGHT = 1 << 20;

    public void put(HyperVector key, HyperVector value) {
        put(key, value, 1, null);
    }

    public void put(HyperVector key, HyperVector value, int baseWeight) {
        put(key, value, baseWeight, null);
    }

    /** Gated learning (entropy/novelty control). */
    public void put(HyperVector key, HyperVector value, int baseWeight, EntropyGate gate) {
        if (baseWeight <= 0) return;

        HyperVector trace = key.bind(value);

        HyperVector existingPred = null;
        if (gate != null && hasData()) {
            existingPred = get(key);
        }

        int mult = (gate == null) ? 1 : gate.weightMultiplier(key, value, trace, existingPred);
        if (mult <= 0) return;

        int w = baseWeight * mult;

        if (bundler.getTotalWeight() > MAX_WEIGHT) {
            bundler.ageShift(1);
            dirty = true;
        }

        bundler.add(trace, w);
        dirty = true;
    }

    public HyperVector get(HyperVector key) {
        if (hologram == null || dirty) {
            hologram = bundler.build();
            dirty = false;
        }
        return hologram.bind(key);
    }

    public int totalWeight() { return bundler.getTotalWeight(); }
    public boolean hasData() { return totalWeight() > 0; }

    /** Manual aging hook (call on idle / dream). */
    public void ageShift(int k) {
        bundler.ageShift(k);
        dirty = true;
    }

    /** Persistence */
    public State snapshot() {
        return new State(bundler.snapshot());
    }

    public void restore(State s) {
        this.bundler = WeightedBundler.fromSnapshot(s.bundler());
        this.hologram = null;
        this.dirty = true;
    }

    public record State(WeightedBundler.State bundler) implements Serializable {}
}
