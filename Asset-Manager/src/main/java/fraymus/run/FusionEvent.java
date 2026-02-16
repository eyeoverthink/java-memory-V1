package fraymus.run;

import java.util.*;

/**
 * Standardized fusion event record
 * Logged to both console and JSONL
 */
public class FusionEvent {
    public final int step;
    public final String parentA;
    public final String parentB;
    public final String action;  // RELATE, APPLY, COMBINE
    public final String kindA;
    public final String kindB;
    public final double distance;
    public final double energyA;
    public final double energyB;
    public final String childId;
    public final String childKind;
    public final String note;
    public final long timestamp;
    
    private FusionEvent(Builder builder) {
        this.step = builder.step;
        this.parentA = builder.parentA;
        this.parentB = builder.parentB;
        this.action = builder.action;
        this.kindA = builder.kindA;
        this.kindB = builder.kindB;
        this.distance = builder.distance;
        this.energyA = builder.energyA;
        this.energyB = builder.energyB;
        this.childId = builder.childId;
        this.childKind = builder.childKind;
        this.note = builder.note;
        this.timestamp = System.currentTimeMillis();
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("timestamp", timestamp);
        map.put("step", step);
        map.put("parent_a", parentA);
        map.put("parent_b", parentB);
        map.put("action", action);
        map.put("kind_a", kindA);
        map.put("kind_b", kindB);
        map.put("distance", distance);
        map.put("energy_a", energyA);
        map.put("energy_b", energyB);
        if (childId != null) {
            map.put("child_id", childId);
            map.put("child_kind", childKind);
        }
        if (note != null) {
            map.put("note", note);
        }
        return map;
    }
    
    public String toPrettyString() {
        String arrow = action.equals("RELATE") ? "↔" : "→";
        String result = String.format("💥 FUSION: [%s] + [%s] %s %s", 
            parentA, parentB, arrow, action);
        if (childId != null) {
            result += String.format(" → [%s]", childId);
        }
        return result;
    }
    
    public static class Builder {
        private int step;
        private String parentA;
        private String parentB;
        private String action;
        private String kindA;
        private String kindB;
        private double distance;
        private double energyA;
        private double energyB;
        private String childId;
        private String childKind;
        private String note;
        
        public Builder step(int step) { this.step = step; return this; }
        public Builder parentA(String id) { this.parentA = id; return this; }
        public Builder parentB(String id) { this.parentB = id; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder kindA(String kind) { this.kindA = kind; return this; }
        public Builder kindB(String kind) { this.kindB = kind; return this; }
        public Builder distance(double dist) { this.distance = dist; return this; }
        public Builder energyA(double e) { this.energyA = e; return this; }
        public Builder energyB(double e) { this.energyB = e; return this; }
        public Builder child(String id, String kind) { 
            this.childId = id; 
            this.childKind = kind; 
            return this; 
        }
        public Builder note(String note) { this.note = note; return this; }
        
        public FusionEvent build() {
            return new FusionEvent(this);
        }
    }
}
