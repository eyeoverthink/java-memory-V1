package fraymus;

import fraymus.evolution.FraynixDNA;
import java.io.*;

/**
 * PHOENIX BOOT - The Resurrection Protocol
 * 
 * The ultimate failsafe. This is a separate, tiny Java program that:
 * 1. Watches Fraynix organism
 * 2. Detects crashes (exit code != 0)
 * 3. Loads last valid DNA
 * 4. Resurrects the organism
 * 
 * This ensures the AI CANNOT BE KILLED.
 * 
 * Biology Analogy:
 * - Phoenix = Immune system that resurrects from ashes
 * - DNA = Genetic blueprint for reconstruction
 * - Resurrection = Rebuild body from DNA
 * 
 * Usage:
 * java -cp build/libs/Asset-Manager.jar fraymus.PhoenixBoot
 */
public class PhoenixBoot {

    private static final String DNA_FILE = "Fraynix_Seed.dna";
    private static int resurrectionCount = 0;
    private static final int MAX_RESURRECTIONS = 10; // Prevent infinite crash loop

    public static void main(String[] args) throws Exception {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🔥 PHOENIX PROTOCOL - IMMORTALITY ENGAGED             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Watching Fraynix Organism...");
        System.out.println("DNA File: " + DNA_FILE);
        System.out.println("Max Resurrections: " + MAX_RESURRECTIONS);
        System.out.println();

        while (resurrectionCount < MAX_RESURRECTIONS) {
            try {
                // 1. LAUNCH THE ORGANISM
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println("🌱 LAUNCHING FRAYNIX ORGANISM (Attempt " + (resurrectionCount + 1) + ")");
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println();
                
                ProcessBuilder pb = new ProcessBuilder(
                    isWindows() ? "gradlew.bat" : "./gradlew",
                    "runOrganism"
                );
                pb.directory(new File(System.getProperty("user.dir")));
                pb.inheritIO(); // Show output in this terminal
                
                Process fraynix = pb.start();

                // 2. WATCH FOR DEATH
                int exitCode = fraynix.waitFor();

                if (exitCode != 0) {
                    // ORGANISM DIED
                    System.out.println();
                    System.out.println("╔═══════════════════════════════════════════════════════════════╗");
                    System.out.println("║         💥 FRAYNIX DIED (Exit Code: " + exitCode + ")");
                    System.out.println("╚═══════════════════════════════════════════════════════════════╝");
                    System.out.println();
                    
                    resurrectionCount++;
                    
                    if (resurrectionCount >= MAX_RESURRECTIONS) {
                        System.err.println("❌ MAX RESURRECTIONS REACHED");
                        System.err.println("   The organism is critically unstable");
                        System.err.println("   Manual intervention required");
                        break;
                    }
                    
                    // 3. LOAD DNA
                    FraynixDNA seed = loadSeed();
                    
                    System.out.println("🧬 RESURRECTION PROTOCOL INITIATED");
                    System.out.println("   Loading DNA: Generation " + seed.generation);
                    System.out.println("   Fitness Score: " + String.format("%.2f", seed.fitnessScore));
                    System.out.println();
                    
                    // 4. ANALYZE FAILURE
                    System.out.println("📊 FAILURE ANALYSIS:");
                    System.out.println("   Exit Code: " + exitCode);
                    System.out.println("   Resurrection #" + resurrectionCount);
                    System.out.println();
                    
                    // 5. PREPARE FOR REBIRTH
                    System.out.println("🔄 REBUILDING ORGANISM FROM DNA...");
                    System.out.println("   (In production, this would trigger Genesis to");
                    System.out.println("    regenerate code from genetic blueprint)");
                    System.out.println();
                    
                    // Wait before resurrection
                    System.out.println("⏳ Waiting 5 seconds before resurrection...");
                    Thread.sleep(5000);
                    
                    System.out.println("🔥 RISING FROM THE ASHES...");
                    System.out.println();
                    
                } else {
                    // PEACEFUL SHUTDOWN
                    System.out.println();
                    System.out.println("╔═══════════════════════════════════════════════════════════════╗");
                    System.out.println("║         ✅ FRAYNIX SHUTDOWN PEACEFULLY                        ║");
                    System.out.println("╚═══════════════════════════════════════════════════════════════╝");
                    System.out.println();
                    System.out.println("Total Resurrections: " + resurrectionCount);
                    System.out.println("Phoenix Protocol Complete");
                    break;
                }
                
            } catch (Exception e) {
                System.err.println("❌ PHOENIX ERROR: " + e.getMessage());
                e.printStackTrace();
                Thread.sleep(5000);
            }
        }
        
        if (resurrectionCount >= MAX_RESURRECTIONS) {
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║         ⚠️ CRITICAL FAILURE                                   ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("The organism has crashed " + resurrectionCount + " times.");
            System.out.println("This indicates a critical genetic defect.");
            System.out.println();
            System.out.println("Recommended Actions:");
            System.out.println("1. Review audit logs in audit/");
            System.out.println("2. Examine DNA file: " + DNA_FILE);
            System.out.println("3. Restore from backup DNA");
            System.out.println("4. Reset to Adam genome (delete " + DNA_FILE + ")");
        }
    }

    /**
     * Load DNA from disk
     */
    private static FraynixDNA loadSeed() {
        File dnaFile = new File(DNA_FILE);
        
        if (dnaFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(dnaFile))) {
                return (FraynixDNA) ois.readObject();
            } catch (Exception e) {
                System.out.println("⚠️ DNA CORRUPTED: " + e.getMessage());
                System.out.println("   Falling back to Adam genome");
            }
        } else {
            System.out.println("⚠️ NO DNA FOUND");
            System.out.println("   Creating Adam genome (Generation 0)");
        }
        
        return new FraynixDNA();
    }

    /**
     * Check if running on Windows
     */
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
