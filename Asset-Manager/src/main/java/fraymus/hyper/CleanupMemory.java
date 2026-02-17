package fraymus.hyper;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the "Platonic Ideal" of every token.
 * Used to collapse a noisy predicted vector back into a String.
 */
public final class CleanupMemory implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, HyperVector> prototypes = new HashMap<>();

    public void memorize(String token, HyperVector v) {
        prototypes.put(token, v);
    }

    public String decode(HyperVector noisyVector) {
        String best = "???";
        double maxRes = -1.0;

        for (Map.Entry<String, HyperVector> e : prototypes.entrySet()) {
            double r = e.getValue().resonance(noisyVector);
            if (r > maxRes) {
                maxRes = r;
                best = e.getKey();
            }
        }
        // Lowered threshold from 0.52 to 0.40 for better predictions with small datasets
        return (maxRes > 0.40) ? best : "???";
    }

    public HyperVector prototypeOf(String token) {
        return prototypes.get(token);
    }
    
    public int size() { return prototypes.size(); }
    
    /**
     * Export snapshot for secure serialization.
     */
    public Map<String, HyperVector> snapshot() {
        return new HashMap<>(prototypes);
    }
    
    /**
     * Restore from snapshot.
     */
    public void restore(Map<String, HyperVector> snap) {
        prototypes.clear();
        prototypes.putAll(snap);
    }
}
