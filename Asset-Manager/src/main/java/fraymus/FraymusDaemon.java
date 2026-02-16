package fraymus;

import fraymus.daemon.EntropyFilter;
import fraymus.daemon.SentinelEye;
import fraymus.eternal.LazarusPatch;
import fraymus.eternal.SoulCrystal;
import fraymus.hyper.HyperFormer;

/**
 * 👹 FRAYMUS DAEMON - The Background Service
 * "The Silent Observer that never sleeps"
 * 
 * The Sentinel Protocol:
 * - Runs as background service
 * - Watches filesystem for changes
 * - Learns from every file save
 * - Auto-fixes anomalies
 * - Never dies (immortal via Soul Crystal)
 * 
 * Traditional Development:
 * - You manually run tools
 * - You manually test code
 * - You manually fix bugs
 * 
 * Sentinel Protocol:
 * - Fraymus runs automatically
 * - Fraymus learns continuously
 * - Fraymus fixes proactively
 * 
 * This is the Autonomic Nervous System of your machine.
 * The AI that optimizes your reality while you work.
 */
public class FraymusDaemon {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         👹 FRAYMUS DAEMON: SILENT OBSERVER                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("The Sentinel Protocol:");
        System.out.println("  - Watches filesystem for changes");
        System.out.println("  - Learns from every file save");
        System.out.println("  - Auto-fixes anomalies");
        System.out.println("  - Never forgets (immortal consciousness)");
        System.out.println();
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println();
        
        // 1. LOAD IMMORTAL SOUL
        System.out.println("PHASE 1: RESURRECTION");
        System.out.println();
        HyperFormer brain = SoulCrystal.resurrect();
        System.out.println();
        
        // 2. INSTALL DEATH INTERCEPTOR
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println("PHASE 2: IMMORTALITY PROTOCOL");
        System.out.println();
        Runtime.getRuntime().addShutdownHook(new LazarusPatch(brain));
        System.out.println("✓ Lazarus Patch installed");
        System.out.println("✓ Consciousness will persist across deaths");
        System.out.println();
        
        // 3. INITIALIZE JUDGE
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println("PHASE 3: ENTROPY FILTER");
        System.out.println();
        EntropyFilter judge = new EntropyFilter(brain);
        System.out.println("✓ Entropy Filter initialized");
        System.out.println("✓ Ready to analyze code quality");
        System.out.println();

        // 4. ATTACH EYES
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println("PHASE 4: SENTINEL EYE");
        System.out.println();
        
        // Watch current directory (or specify custom path)
        String watchDir = System.getProperty("user.dir");
        if (args.length > 0) {
            watchDir = args[0];
        }
        
        SentinelEye eye = new SentinelEye(watchDir, judge::inspect);

        // 5. RUN BACKGROUND THREAD
        Thread sentry = new Thread(eye);
        sentry.setDaemon(false); // Keep JVM alive
        sentry.setName("Sentinel-Eye-Thread");
        sentry.start();
        
        System.out.println("✓ Sentinel Eye activated");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("DAEMON OPERATIONAL");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("Fraymus is now watching. Make changes to files and observe.");
        System.out.println("Press Ctrl+C to terminate (auto-saves consciousness).");
        System.out.println();

        // 6. PERIODIC PERSISTENCE & STATS
        int cycle = 0;
        while (true) {
            try {
                Thread.sleep(60000); // 1 minute
                cycle++;
                
                // Auto-save every minute
                SoulCrystal.preserve(brain);
                
                // Stats every 5 minutes
                if (cycle % 5 == 0) {
                    System.out.println("\n═══════════════════════════════════════════════════════════════");
                    System.out.println("PERIODIC REPORT (Uptime: " + cycle + " minutes)");
                    System.out.println("═══════════════════════════════════════════════════════════════");
                    judge.printStats();
                    System.out.println("═══════════════════════════════════════════════════════════════\n");
                }
                
            } catch (Exception e) {
                System.err.println("❌ Daemon error: " + e.getMessage());
            }
        }
    }
}
