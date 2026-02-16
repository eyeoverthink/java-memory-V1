package fraymus.genesis;

import java.io.*;

/**
 * THE EXPORTER: GenesisExport
 * 
 * Philosophy: Save the DNA, not the Body.
 * Function: Crystallizes knowledge to disk and resurrects it
 * 
 * Zero Dependencies - Only java.io
 */
public class GenesisExport {

    /**
     * CRYSTALLIZE
     * Save knowledge to disk as .genesis file
     */
    public static void crystallize(GenesisBlock knowledge, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(knowledge);
            File file = new File(filename);
            System.out.println(">>> [EXPORT] Knowledge Crystallized to '" + filename + "'");
            System.out.println("    File Size: " + file.length() + " bytes");
            System.out.println("    Nodes: " + knowledge.countNodes());
        } catch (IOException e) {
            System.err.println(">>> [EXPORT] Failed: " + e.getMessage());
        }
    }

    /**
     * AWAKEN
     * Resurrect knowledge from disk
     */
    public static GenesisBlock awaken(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            GenesisBlock knowledge = (GenesisBlock) ois.readObject();
            System.out.println(">>> [IMPORT] Knowledge Awakened from '" + filename + "'");
            System.out.println("    Nodes: " + knowledge.countNodes());
            return knowledge;
        } catch (Exception e) {
            System.err.println(">>> [IMPORT] Failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if genesis file exists
     */
    public static boolean exists(String filename) {
        return new File(filename).exists();
    }

    /**
     * Delete genesis file
     */
    public static boolean destroy(String filename) {
        return new File(filename).delete();
    }

    /**
     * Print export statistics
     */
    public static void printStats(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File does not exist: " + filename);
            return;
        }

        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   GENESIS EXPORT STATISTICS                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Filename: " + filename);
        System.out.println("  Size: " + file.length() + " bytes");
        System.out.println("  Exists: " + file.exists());
        System.out.println("  Readable: " + file.canRead());
        System.out.println("  Writable: " + file.canWrite());
    }
}
