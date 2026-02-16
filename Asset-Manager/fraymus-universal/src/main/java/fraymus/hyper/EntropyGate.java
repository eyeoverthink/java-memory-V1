package fraymus.hyper;

import java.io.Serializable;

public final class EntropyGate implements Serializable {

    private static final long serialVersionUID = 1L;

    private final double redundantHigh;
    private final double noisyLow;
    private final double minDensity;
    private final double maxDensity;

    public EntropyGate() {
        this(0.92, 0.52, 0.30, 0.70);
    }

    public EntropyGate(double redundantHigh, double noisyLow, double minDensity, double maxDensity) {
        this.redundantHigh = redundantHigh;
        this.noisyLow = noisyLow;
        this.minDensity = minDensity;
        this.maxDensity = maxDensity;
    }

    /**
     * Decide weight multiplier for a memory update.
     * return 0 -> reject update
     * return 1 -> normal
     * return 2 -> boost (strong learning signal)
     */
    public int weightMultiplier(HyperVector key, HyperVector incomingValue, HyperVector trace, HyperVector existingPrediction) {
        if (existingPrediction != null) {
            double r = existingPrediction.resonance(incomingValue);

            if (r >= redundantHigh) return 1;
            if (r <= noisyLow) return 0;

            if (r >= 0.80) return 2;
        }

        double d = trace.density();
        if (d < minDensity || d > maxDensity) return 1;

        return 1;
    }
}
