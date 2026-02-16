package fraymus.language;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

/**
 * THE MEMORY LINK: Hippocampus
 * 
 * Role: Saves dynamic interaction so Broca can learn from it instantly
 * Function: Short-Term Memory that feeds back into the Speech Center
 * 
 * When the User speaks to the AI, the AI saves the conversation,
 * feeds it to Broca, and immediately learns the User's cadence.
 * This is how it learns to talk like you.
 */
public class Hippocampus {

    private static final String MEMORY_FILE = "memory_stream.txt";
    private static int interactionCount = 0;

    /**
     * REMEMBER INTERACTION
     * Saves user's words to the memory stream
     */
    public static void rememberInteraction(String userText) {
        try (FileWriter writer = new FileWriter(MEMORY_FILE, true)) {
            writer.write(userText + " ");
            interactionCount++;
            System.out.println(">>> [HIPPOCAMPUS] Interaction saved to memory.");
        } catch (IOException e) {
            System.err.println(">>> [HIPPOCAMPUS] Memory Write Failed: " + e.getMessage());
        }
    }

    /**
     * REMEMBER WITH CONTEXT
     * Saves interaction with timestamp and speaker label
     */
    public static void rememberWithContext(String speaker, String text) {
        try (FileWriter writer = new FileWriter(MEMORY_FILE, true)) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            writer.write("[" + timestamp + "][" + speaker + "] " + text + " ");
            interactionCount++;
            System.out.println(">>> [HIPPOCAMPUS] Contextual memory saved.");
        } catch (IOException e) {
            System.err.println(">>> [HIPPOCAMPUS] Memory Write Failed: " + e.getMessage());
        }
    }

    /**
     * CLEAR MEMORY
     * Resets the memory stream (fresh start)
     */
    public static void clearMemory() {
        try {
            File file = new File(MEMORY_FILE);
            if (file.exists()) {
                file.delete();
            }
            interactionCount = 0;
            System.out.println(">>> [HIPPOCAMPUS] Memory cleared.");
        } catch (Exception e) {
            System.err.println(">>> [HIPPOCAMPUS] Failed to clear memory: " + e.getMessage());
        }
    }

    /**
     * GET MEMORY FILE PATH
     */
    public static String getMemoryFile() {
        return MEMORY_FILE;
    }

    /**
     * GET INTERACTION COUNT
     */
    public static int getInteractionCount() {
        return interactionCount;
    }

    /**
     * CHECK IF MEMORY EXISTS
     */
    public static boolean hasMemory() {
        return new File(MEMORY_FILE).exists();
    }

    /**
     * GET MEMORY SIZE
     */
    public static long getMemorySize() {
        File file = new File(MEMORY_FILE);
        return file.exists() ? file.length() : 0;
    }

    /**
     * Print statistics
     */
    public static void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   HIPPOCAMPUS MEMORY STATISTICS                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Memory File: " + MEMORY_FILE);
        System.out.println("  Exists: " + hasMemory());
        System.out.println("  Size: " + getMemorySize() + " bytes");
        System.out.println("  Interactions Saved: " + interactionCount);
    }
}
