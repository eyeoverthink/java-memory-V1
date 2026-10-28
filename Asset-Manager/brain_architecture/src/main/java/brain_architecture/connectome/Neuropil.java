package brain_architecture.connectome;

import java.util.UUID;

/**
 * Neuropil - Represents an anatomically defined brain region.
 * 
 * Inspired by the Drosophila connectome which has 78 anatomically defined
 * neuropils (brain regions) that serve as specialized processing units.
 */
public class Neuropil {
    
    private final UUID neuropilId;
    private final String name;
    private final String description;
    private final NeuropilType type;
    
    public Neuropil(String name, String description, NeuropilType type) {
        this.neuropilId = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.type = type;
    }
    
    public UUID getNeuropilId() {
        return neuropilId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public NeuropilType getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return "Neuropil{" +
            "name='" + name + '\'' +
            ", type=" + type +
            '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Neuropil neuropil = (Neuropil) o;
        return neuropilId.equals(neuropil.neuropilId);
    }
    
    @Override
    public int hashCode() {
        return neuropilId.hashCode();
    }
    
    /**
     * NeuropilType - Classification of brain regions by function.
     */
    public enum NeuropilType {
        SENSORY("Sensory processing region"),
        MOTOR("Motor output region"),
        ASSOCIATION("Association and integration region"),
        MEMORY("Memory storage region"),
        MODULATION("Neuromodulation region"),
        HUB("Central hub for information routing");
        
        private final String description;
        
        NeuropilType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
