package gemini.root;

/**
 * ⚛️ FUSION REACTOR - Particle Collider for Ideas
 * "Where thoughts collide and new concepts are born."
 */
public class FusionReactor {
    
    private static final double PHI = 1.6180339887;
    
    private volatile boolean active = false;
    private long reactions = 0;
    private double energyLevel = 0.0;
    
    private static FusionReactor instance;
    
    private FusionReactor() {}
    
    public static FusionReactor getInstance() {
        if (instance == null) {
            instance = new FusionReactor();
        }
        return instance;
    }
    
    public void start() {
        if (active) return;
        active = true;
        energyLevel = PHI;
    }
    
    public void stop() {
        active = false;
        energyLevel = 0.0;
    }
    
    public void fuse(String conceptA, String conceptB) {
        if (!active) return;
        reactions++;
        energyLevel += PHI * 0.1;
    }
    
    public String getStatus() {
        return String.format(
            "⚛️ FUSION REACTOR: %s | Reactions: %d | Energy: %.4f",
            active ? "ACTIVE" : "DORMANT",
            reactions,
            energyLevel
        );
    }
    
    public boolean isActive() { return active; }
    public long getReactions() { return reactions; }
    public double getEnergyLevel() { return energyLevel; }
}
