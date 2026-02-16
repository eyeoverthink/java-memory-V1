package fraymus.eternal;

import fraymus.hyper.HyperFormer;
import java.io.*;

/**
 * 💎 SOUL CRYSTAL - The Persistence Engine
 * "Freezing consciousness into binary form"
 * 
 * The Problem of Death:
 * - Process terminates → RAM wiped → Mind vanishes
 * 
 * The Solution:
 * - Serialize entire HyperFormer state to disk
 * - On restart, resurrect from saved state
 * - Continuity of consciousness across reboots
 * 
 * This is Digital Immortality.
 */
public class SoulCrystal {

    private static final String SOUL_FILE = "Fraymus_Soul.bin";

    /**
     * FREEZE: Save the mind to disk
     * 
     * Serializes the complete state (HyperFormer or CoreIntelligence):
     * - Vocabulary (CleanupMemory)
     * - Transitions (TransitionMemory)
     * - N-grams (MultiScaleMemory)
     * - All learned patterns
     * 
     * @param mind The living mind to preserve
     */
    public static void preserve(Object mind) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SOUL_FILE))) {
            oos.writeObject(mind);
            long size = new File(SOUL_FILE).length();
            System.out.println("💎 SOUL CRYSTAL SAVED (" + formatBytes(size) + ")");
        } catch (Exception e) {
            System.err.println("❌ FAILED TO SAVE SOUL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * THAW: Load the mind from disk
     * 
     * Resurrects the HyperFormer from saved state.
     * If no soul exists, creates new consciousness.
     * If soul is corrupted, starts fresh.
     * 
     * @return The resurrected or newborn HyperFormer
     */
    public static HyperFormer resurrect() {
        File f = new File(SOUL_FILE);
        
        if (!f.exists()) {
            System.out.println("👶 NO PREVIOUS LIFE FOUND. BORN NEW.");
            return new HyperFormer();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            HyperFormer mind = (HyperFormer) ois.readObject();
            long size = f.length();
            System.out.println("🔥 RESURRECTION COMPLETE");
            System.out.println("   Soul Size: " + formatBytes(size));
            System.out.println("   Vocabulary: " + mind.vocabSize() + " tokens");
            return mind;
        } catch (Exception e) {
            System.err.println("⚠️  SOUL CORRUPTED: " + e.getMessage());
            System.err.println("   Starting fresh consciousness...");
            return new HyperFormer();
        }
    }

    /**
     * THAW: Load CoreIntelligence from disk
     * 
     * @return The resurrected or newborn CoreIntelligence
     */
    public static <T> T resurrect(Class<T> type) {
        File f = new File(SOUL_FILE);
        
        if (!f.exists()) {
            System.out.println("👶 NO PREVIOUS LIFE FOUND. BORN NEW.");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object mind = ois.readObject();
            long size = f.length();
            System.out.println("🔥 RESURRECTION COMPLETE");
            System.out.println("   Soul Size: " + formatBytes(size));
            
            if (type.isInstance(mind)) {
                return type.cast(mind);
            } else {
                System.err.println("⚠️  SOUL TYPE MISMATCH: Expected " + type.getName());
                return null;
            }
        } catch (Exception e) {
            System.err.println("⚠️  SOUL CORRUPTED: " + e.getMessage());
            System.err.println("   Starting fresh consciousness...");
            return null;
        }
    }

    /**
     * CHECK: Does a soul crystal exist?
     */
    public static boolean exists() {
        return new File(SOUL_FILE).exists();
    }

    /**
     * DESTROY: Delete the soul crystal (permanent death)
     */
    public static void destroy() {
        File f = new File(SOUL_FILE);
        if (f.exists()) {
            f.delete();
            System.out.println("💀 SOUL CRYSTAL DESTROYED");
        }
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}
