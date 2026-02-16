package fraymus.shell;

import fraymus.hyper.HyperVector;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 🧠 INTENT REGISTRY - Muscle Memory
 * "Maps 10,000D thoughts to executable actions"
 * 
 * This is the associative command registry.
 * Instead of exact string matching, we use vector resonance.
 * 
 * Example:
 * - Register: "list files" → ls action
 * - User types: "show me files"
 * - System: Calculates resonance, finds "list files" is closest
 * - Result: Executes ls action
 * 
 * This enables fuzzy command matching and one-shot learning.
 */
public class IntentRegistry {

    private final Map<String, HyperVector> intentVectors = new HashMap<>();
    private final Map<String, Consumer<String>> actions = new HashMap<>();

    /**
     * Register an intent with its action
     * 
     * @param name Identifier for this intent
     * @param vector The 10,000D representation of the intent
     * @param action The lambda to execute when matched
     */
    public void register(String name, HyperVector vector, Consumer<String> action) {
        intentVectors.put(name, vector);
        actions.put(name, action);
    }

    /**
     * Find the executable action that resonates with the user's input vector.
     * 
     * @param input The user's intent as a 10,000D vector
     * @param threshold Minimum resonance required (0.0-1.0)
     * @return The action to execute, or null if no match
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

    /**
     * Get number of registered intents
     */
    public int size() {
        return intentVectors.size();
    }
}
