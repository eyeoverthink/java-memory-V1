package fraymus.quantum.healing;

import fraymus.quantum.brain.LogicBrain;
import java.util.*;

/**
 * Self Healer - Brain Snapshot & Recovery System
 * 
 * Takes snapshots of healthy brain states and can restore them
 * when an entity's fitness or energy drops below thresholds.
 */
public class SelfHealer {

    private static final double FITNESS_THRESHOLD = 0.3;
    private static final float ENERGY_THRESHOLD = 0.2f;

    public static class BrainSnapshot {
        public final String entityName;
        public final String brainEncoding;
        public final double fitnessAtSnapshot;
        public final float energyAtSnapshot;
        public final long timestamp;

        public BrainSnapshot(String entityName, String brainEncoding, double fitness, float energy) {
            this.entityName = entityName;
            this.brainEncoding = brainEncoding;
            this.fitnessAtSnapshot = fitness;
            this.energyAtSnapshot = energy;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public String toString() {
            return String.format("Snapshot[%s, fitness=%.3f, energy=%.2f]",
                entityName, fitnessAtSnapshot, energyAtSnapshot);
        }
    }

    private static final Map<String, BrainSnapshot> snapshots = new HashMap<>();
    private static int totalHeals = 0;
    private static int totalSnapshots = 0;

    /**
     * Take a snapshot of the current brain state if entity is healthy.
     */
    public static boolean takeSnapshot(String entityName, LogicBrain brain, double fitness, float energy) {
        if (fitness < FITNESS_THRESHOLD || energy < ENERGY_THRESHOLD) {
            return false;
        }

        BrainSnapshot existing = snapshots.get(entityName);
        if (existing != null && existing.fitnessAtSnapshot >= fitness) {
            return false;
        }

        BrainSnapshot snapshot = new BrainSnapshot(entityName, brain.encode(), fitness, energy);
        snapshots.put(entityName, snapshot);
        totalSnapshots++;
        return true;
    }

    /**
     * Check if entity needs healing based on current state.
     */
    public static boolean needsHealing(double fitness, float energy) {
        return fitness < FITNESS_THRESHOLD || energy < ENERGY_THRESHOLD;
    }

    /**
     * Attempt to heal by restoring brain from snapshot.
     */
    public static boolean heal(String entityName, LogicBrain brain) {
        BrainSnapshot snapshot = snapshots.get(entityName);
        if (snapshot == null) {
            return false;
        }

        LogicBrain restored = LogicBrain.decode(snapshot.brainEncoding);
        if (restored == null) {
            return false;
        }

        var restoredGates = restored.getGates();
        var currentGates = brain.getGates();
        int n = Math.min(restoredGates.size(), currentGates.size());
        for (int i = 0; i < n; i++) {
            var src = restoredGates.get(i);
            var dst = currentGates.get(i);
            dst.type = src.type;
            dst.in1 = src.in1;
            dst.in2 = src.in2;
        }

        totalHeals++;
        return true;
    }

    /**
     * Get snapshot for an entity.
     */
    public static BrainSnapshot getSnapshot(String entityName) {
        return snapshots.get(entityName);
    }

    /**
     * Check if snapshot exists.
     */
    public static boolean hasSnapshot(String entityName) {
        return snapshots.containsKey(entityName);
    }

    /**
     * Remove snapshot.
     */
    public static void removeSnapshot(String entityName) {
        snapshots.remove(entityName);
    }

    public static int getTotalHeals() { return totalHeals; }
    public static int getTotalSnapshots() { return totalSnapshots; }
    public static int getSnapshotCount() { return snapshots.size(); }
    
    public static void printStats() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║          SELF HEALER STATS           ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("║  Total Snapshots:   %6d            ║%n", totalSnapshots);
        System.out.printf("║  Total Heals:       %6d            ║%n", totalHeals);
        System.out.printf("║  Active Snapshots:  %6d            ║%n", snapshots.size());
        System.out.println("╚══════════════════════════════════════╝");
    }
}
