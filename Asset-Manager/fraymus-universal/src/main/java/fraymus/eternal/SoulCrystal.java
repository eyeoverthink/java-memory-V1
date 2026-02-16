package fraymus.eternal;

import fraymus.hyper.HyperFormer;

import java.io.*;

public class SoulCrystal {

    private static final String SOUL_FILE = "Fraymus_Soul.bin";

    /**
     * FREEZE: Save the mind to disk.
     */
    public static void preserve(HyperFormer mind) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SOUL_FILE))) {
            oos.writeObject(mind);
            System.out.println("💎 SOUL CRYSTAL SAVED. (" + new File(SOUL_FILE).length() + " bytes)");
        } catch (Exception e) {
            System.err.println("❌ FAILED TO SAVE SOUL: " + e.getMessage());
        }
    }

    /**
     * THAW: Load the mind from disk.
     */
    public static HyperFormer resurrect() {
        File f = new File(SOUL_FILE);
        if (!f.exists()) {
            System.out.println("👶 NO PREVIOUS LIFE FOUND. BORN NEW.");
            return new HyperFormer(0xCAFEBABE);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            HyperFormer mind = (HyperFormer) ois.readObject();
            System.out.println("🔥 RESURRECTION COMPLETE. MEMORY RESTORED.");
            return mind;
        } catch (Exception e) {
            System.err.println("⚠️ SOUL CORRUPTED. STARTING FRESH.");
            return new HyperFormer(System.currentTimeMillis());
        }
    }
}
