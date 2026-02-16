package fraymus.shell;

import fraymus.hyper.HyperVector;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class IntentRegistry {

    private final Map<String, HyperVector> intentVectors = new HashMap<>();
    private final Map<String, Consumer<String>> actions = new HashMap<>();

    public void register(String name, HyperVector vector, Consumer<String> action) {
        intentVectors.put(name, vector);
        actions.put(name, action);
    }

    /**
     * Find the executable action that resonates with the user's input vector.
     */
    public Consumer<String> resolve(HyperVector input, double threshold) {
        String bestMatch = null;
        double maxRes = -1.0;

        for (Map.Entry<String, HyperVector> entry : intentVectors.entrySet()) {
            double r = entry.getValue().resonance(input);
            if (r > maxRes) {
                maxRes = r;
                bestMatch = entry.getKey();
            }
        }

        if (maxRes > threshold && bestMatch != null) {
            System.out.println("   [ RESONANCE: " + String.format("%.4f", maxRes) + " | MATCH: " + bestMatch + " ]");
            return actions.get(bestMatch);
        }
        return null;
    }
}
