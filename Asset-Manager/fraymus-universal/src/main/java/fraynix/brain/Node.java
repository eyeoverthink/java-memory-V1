package fraynix.brain;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NODE: A single neuron in the HyperTesseract.
 * 
 * Contains activation level, memory, logic rules, and references.
 * Deterministic and explainable.
 */
public class Node {

    private final int[] coordinates;    // [w, x, y, z] position
    private final String id;
    
    // Activation
    private double activation = 0.0;
    private double threshold = 0.5;
    private double decay = 0.1;
    
    // Memory
    private final Map<String, Object> memory = new ConcurrentHashMap<>();
    private final Deque<String> history = new ArrayDeque<>();
    private static final int HISTORY_LIMIT = 100;
    
    // Logic
    private final List<LogicRule> rules = new ArrayList<>();
    
    // Connections
    private final List<NodeRef> inputs = new ArrayList<>();
    private final List<NodeRef> outputs = new ArrayList<>();

    public Node(int w, int x, int y, int z) {
        this.coordinates = new int[]{w, x, y, z};
        this.id = String.format("N[%d,%d,%d,%d]", w, x, y, z);
    }

    public String getId() { return id; }
    public int[] getCoordinates() { return coordinates.clone(); }
    public synchronized double getActivation() { return activation; }
    public synchronized double getThreshold() { return threshold; }
    public Map<String, Object> getMemory() { return Collections.unmodifiableMap(memory); }
    public List<LogicRule> getRules() { return Collections.unmodifiableList(rules); }

    public synchronized void setActivation(double activation) {
        this.activation = Math.max(0, Math.min(1, activation));
    }

    public synchronized void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public synchronized void stimulate(double amount) {
        this.activation = Math.min(1.0, this.activation + amount);
    }

    public synchronized void tick() {
        // Apply decay
        this.activation = Math.max(0, this.activation - decay * activation);
        
        // Fire if above threshold
        if (activation >= threshold) {
            fire();
        }
    }

    private void fire() {
        // Propagate to outputs
        for (NodeRef output : outputs) {
            if (output.node != null) {
                double signal = activation * output.weight;
                output.node.stimulate(signal);
            }
        }
        
        // Log firing
        history.addLast(String.format("FIRE@%d:%.3f", System.currentTimeMillis() % 100000, activation));
        while (history.size() > HISTORY_LIMIT) {
            history.removeFirst();
        }
    }

    public void remember(String key, Object value) {
        memory.put(key, value);
    }

    public Object recall(String key) {
        return memory.get(key);
    }

    public void addRule(LogicRule rule) {
        rules.add(rule);
    }

    public void connect(Node target, double weight) {
        synchronized (this) {
            outputs.add(new NodeRef(target, weight));
        }
        synchronized (target) {
            target.inputs.add(new NodeRef(this, weight));
        }
    }

    public List<String> getHistory() {
        synchronized (this) {
            return List.copyOf(history);
        }
    }

    public boolean isActive() {
        synchronized (this) {
            return activation >= threshold;
        }
    }

    public NodeState getState() {
        synchronized (this) {
            return new NodeState(
                id,
                coordinates.clone(),
                activation,
                threshold,
                new HashMap<>(memory),
                inputs.size(),
                outputs.size()
            );
        }
    }

    public record NodeState(
        String id,
        int[] coordinates,
        double activation,
        double threshold,
        Map<String, Object> memory,
        int inputCount,
        int outputCount
    ) {}

    public record NodeRef(Node node, double weight) {}

    @FunctionalInterface
    public interface LogicRule {
        double evaluate(Node self, Map<String, Object> context);
    }
}
