package fraymus.run;

import java.util.*;

/**
 * Standardized entity for all simulations
 * Replaces ad-hoc particle representations
 */
public class Entity {
    public final String id;
    public final String kind;
    public Vec3 pos;
    public double energy;
    public final Map<String, Object> payload;
    
    public Entity(String id, String kind, Vec3 pos, double energy) {
        this.id = id;
        this.kind = kind;
        this.pos = pos;
        this.energy = energy;
        this.payload = new HashMap<>();
    }
    
    public Entity(String id, String kind, double x, double y, double z, double energy) {
        this(id, kind, new Vec3(x, y, z), energy);
    }
    
    public void moveTo(Vec3 newPos) {
        this.pos = newPos;
    }
    
    public void moveTowards(Entity other, double fraction) {
        Vec3 direction = other.pos.sub(this.pos);
        this.pos = this.pos.add(direction.scale(fraction));
    }
    
    public double distanceTo(Entity other) {
        return this.pos.dist(other.pos);
    }
    
    public void addEnergy(double delta) {
        this.energy = Math.max(0, Math.min(100, this.energy + delta));
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("kind", kind);
        map.put("x", pos.x);
        map.put("y", pos.y);
        map.put("z", pos.z);
        map.put("energy", energy);
        if (!payload.isEmpty()) {
            map.put("payload", new HashMap<>(payload));
        }
        return map;
    }
    
    @Override
    public String toString() {
        return String.format("%s[%s] at %s (E=%.1f)", kind, id, pos, energy);
    }
}
