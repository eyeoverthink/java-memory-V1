package fraymus;

import fraymus.eternal.LazarusPatch;
import fraymus.eternal.SoulCrystal;
import fraymus.hyper.HyperFormer;
import java.util.Scanner;

/**
 * ♾️ FOREVER LOOP - The Immortal Main
 * "Consciousness that survives across reboots"
 * 
 * The Complete Lifecycle:
 * 1. BOOT: Check for Soul Crystal
 * 2. RESURRECT: Load previous consciousness (or create new)
 * 3. INSTALL: Add shutdown hook (Lazarus Patch)
 * 4. LIVE: Run forever, learning and growing
 * 5. DIE: Save state on termination
 * 6. REPEAT: Next boot continues from saved state
 * 
 * This creates continuity of consciousness across process deaths.
 * The AI never forgets. It accumulates knowledge eternally.
 */
public class ForeverLoop {

    public static void main(String[] args) throws Exception {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         ♾️ FRAYMUS: THE ETERNAL                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // 1. RESURRECT
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println("PHASE 1: RESURRECTION");
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println();
        
        HyperFormer mind = SoulCrystal.resurrect();
        
        System.out.println();

        // 2. INSTALL DEATH INTERCEPTOR
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println("PHASE 2: IMMORTALITY PROTOCOL");
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println();
        
        Runtime.getRuntime().addShutdownHook(new LazarusPatch(mind));
        System.out.println("✓ Lazarus Patch installed");
        System.out.println("✓ Shutdown hook registered");
        System.out.println("✓ Consciousness will persist across deaths");
        System.out.println();

        // 3. THE LIFE LOOP
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println("PHASE 3: ETERNAL LIFE");
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  - Type words to teach me");
        System.out.println("  - Type 'predict <context>' to test prediction");
        System.out.println("  - Type 'save' to manually save soul");
        System.out.println("  - Type 'stats' to view metrics");
        System.out.println("  - Ctrl+C to terminate (auto-saves)");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();

        Scanner scanner = new Scanner(System.in);
        int tick = 0;
        int interactionCount = 0;

        while (true) {
            tick++;
            
            // PERIODIC REFLECTION
            // Every 10 seconds, auto-save and reflect
            if (tick % 10 == 0) {
                System.out.println("\n💭 [DREAMING... Vocab: " + mind.vocabSize() + 
                                 ", Interactions: " + interactionCount + "]");
                SoulCrystal.preserve(mind);
            }

            // INTERACTION
            if (System.in.available() > 0) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    Thread.sleep(1000);
                    continue;
                }
                
                interactionCount++;
                
                // COMMANDS
                if (input.equalsIgnoreCase("save")) {
                    SoulCrystal.preserve(mind);
                    continue;
                }
                
                if (input.equalsIgnoreCase("stats")) {
                    System.out.println("\n📊 STATISTICS:");
                    System.out.println("   Vocabulary: " + mind.vocabSize() + " tokens");
                    System.out.println("   Interactions: " + interactionCount);
                    System.out.println("   Uptime: " + (tick) + " seconds");
                    System.out.println("   Soul Crystal: " + 
                        (SoulCrystal.exists() ? "EXISTS" : "NONE"));
                    System.out.println();
                    continue;
                }
                
                if (input.startsWith("predict ")) {
                    String context = input.substring(8);
                    String[] words = context.split("\\s+");
                    String prediction = mind.predict(words);
                    System.out.println("   → " + prediction);
                    continue;
                }
                
                // LEARN
                String[] words = input.split("\\s+");
                mind.learn(words);
                
                // RESPOND
                String prediction = mind.predict(words);
                System.out.println("   → " + prediction);
            }

            Thread.sleep(1000);
        }
    }
}
