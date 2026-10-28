package fraymus.neural;

import java.util.*;

/**
 * Entity - Named Entity (Person, Place, Organization, Date, etc.)
 */
public class Entity {
    
    private String id;
    private String name;
    private String type; // PERSON, PLACE, ORGANIZATION, DATE, etc.
    private String context;
    private int frequency;
    private Map<String, Object> attributes;
    private long firstSeen;
    private long lastSeen;
    
    public Entity(String name, String type) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.context = "";
        this.frequency = 1;
        this.attributes = new HashMap<>();
        this.firstSeen = System.currentTimeMillis();
        this.lastSeen = System.currentTimeMillis();
    }
    
    public String getId() { return id; }
    
    public String getName() { return name; }
    
    public String getType() { return type; }
    
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    
    public int getFrequency() { return frequency; }
    public void incrementFrequency() { 
        this.frequency++;
        this.lastSeen = System.currentTimeMillis();
    }
    
    public Map<String, Object> getAttributes() { return attributes; }
    public void addAttribute(String key, Object value) { this.attributes.put(key, value); }
    
    public long getFirstSeen() { return firstSeen; }
    
    public long getLastSeen() { return lastSeen; }
    
    @Override
    public String toString() {
        return String.format("Entity[name=%s, type=%s, freq=%d]", name, type, frequency);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entity other = (Entity) obj;
        return name.equals(other.name) && type.equals(other.type);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
