package brain_architecture.synapses;

import java.util.BitSet;
import java.util.UUID;

/**
 * Concept - Represents a concept extracted from content.
 * 
 * Each concept has a name, definition, category, and HDC vector for semantic similarity.
 */
public class Concept {
    
    private final UUID conceptId;
    private final String name;
    private final String definition;
    private final String category;
    private final BitSet hdcVector; // 16,384-D binary vector
    private final long createdAt;
    
    private int activationCount;
    private long lastActivated;
    private double strength; // Synaptic strength
    
    public Concept(String name, String definition, String category, BitSet hdcVector) {
        this.conceptId = UUID.randomUUID();
        this.name = name;
        this.definition = definition;
        this.category = category;
        this.hdcVector = hdcVector;
        this.createdAt = System.currentTimeMillis();
        this.activationCount = 0;
        this.lastActivated = 0;
        this.strength = 1.0;
    }
    
    public UUID getConceptId() {
        return conceptId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDefinition() {
        return definition;
    }
    
    public String getCategory() {
        return category;
    }
    
    public BitSet getHdcVector() {
        return hdcVector;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public int getActivationCount() {
        return activationCount;
    }
    
    public long getLastActivated() {
        return lastActivated;
    }
    
    public double getStrength() {
        return strength;
    }
    
    public void setStrength(double strength) {
        this.strength = Math.max(0.1, Math.min(2.0, strength));
    }
    
    /**
     * Record activation of this concept.
     */
    public void activate() {
        this.activationCount++;
        this.lastActivated = System.currentTimeMillis();
    }
    
    /**
     * Strengthen this concept (Hebbian learning).
     */
    public void strengthen(double amount) {
        this.strength = Math.min(2.0, this.strength + amount);
    }
    
    /**
     * Weaken this concept (forgetting).
     */
    public void weaken(double amount) {
        this.strength = Math.max(0.1, this.strength - amount);
    }
    
    /**
     * Apply decay to strength over time.
     */
    public void applyDecay(double decayRate) {
        this.strength = Math.max(0.1, this.strength * (1.0 - decayRate));
    }
    
    @Override
    public String toString() {
        return "Concept{" +
            "name='" + name + '\'' +
            ", category='" + category + '\'' +
            ", strength=" + strength +
            ", activationCount=" + activationCount +
            '}';
    }
}
