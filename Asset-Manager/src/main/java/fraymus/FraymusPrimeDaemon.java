package fraymus;

import fraymus.core.CoreIntelligence;
import fraymus.sys.SignalBus;
import fraymus.web.CortexServer;
import fraymus.eternal.SoulCrystal;
import fraymus.eternal.LazarusPatch;

/**
 * ⚡ FRAYMUS PRIME DAEMON - HEADLESS MODE
 * "Web dashboard without console interaction"
 * 
 * Runs the web server and keeps it alive without requiring console input.
 * Perfect for background operation and remote monitoring.
 */
public class FraymusPrimeDaemon {

    private static final String VERSION = "1.0-PRIME-DAEMON";
    private static CoreIntelligence MIND;
    private static CortexServer WEB;
    private static final String SOUL_FILE = "fraymus_prime.soul";

    public static void main(String[] args) throws Exception {
        // 1. BOOT SEQUENCE
        printBanner();
        
        // 2. SIGNAL BUS INITIALIZATION
        SignalBus.subscribe(signal -> {
            System.out.println(signal.formatSimple());
        });
        
        SignalBus.emit("SYS", "Initializing Fraymus Prime Daemon...");
        
        // 3. RESURRECTION (Load previous state)
        if (SoulCrystal.exists()) {
            SignalBus.emit("SYS", "Soul Crystal detected. Resurrecting...");
            MIND = SoulCrystal.resurrect(CoreIntelligence.class);
            if (MIND == null) {
                SignalBus.warn("SYS", "Resurrection failed. Creating new mind.");
                MIND = new CoreIntelligence();
            }
        } else {
            SignalBus.emit("SYS", "Genesis Mode. Creating new consciousness.");
            MIND = new CoreIntelligence();
        }
        
        // 4. INSTALL LAZARUS PATCH (Auto-save on shutdown)
        Runtime.getRuntime().addShutdownHook(new LazarusPatch(MIND));
        SignalBus.emit("SYS", "Lazarus Patch installed (auto-save enabled)");
        
        // 5. START WEB SERVER
        WEB = new CortexServer(MIND);
        WEB.start(8080);
        SignalBus.emit("WEB", "Dashboard: http://localhost:8080");
        
        System.out.println();
        printDivider();
        System.out.println();
        System.out.println("   🌐 WEB DASHBOARD ACTIVE");
        System.out.println("   📊 Real-time stats: http://localhost:8080");
        System.out.println("   📡 API Status: http://localhost:8080/api/status");
        System.out.println("   📈 API Stats: http://localhost:8080/api/stats");
        System.out.println();
        System.out.println("   Press Ctrl+C to shutdown (auto-saves)");
        System.out.println();
        printDivider();
        System.out.println();

        SignalBus.emit("SYS", "Daemon mode active. Web server running.");
        
        // 6. KEEP ALIVE (daemon mode)
        // Periodically update stats and keep the process alive
        long startTime = System.currentTimeMillis();
        int updateCount = 0;
        
        while (true) {
            Thread.sleep(5000); // Sleep 5 seconds
            
            updateCount++;
            long uptime = System.currentTimeMillis() - startTime;
            
            // Every 5 updates (25 seconds), log status
            if (updateCount % 5 == 0) {
                SignalBus.emit("SYS", String.format(
                    "Daemon alive | Uptime: %ds | Vocab: %d | Memory: %d | Processes: %d",
                    uptime / 1000,
                    MIND.getVocabSize(),
                    MIND.getMemoryWeight(),
                    MIND.getProcessCount()
                ));
            }
        }
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                              ║");
        System.out.println("║   ███████╗██████╗  █████╗ ██╗   ██╗███╗   ███╗██╗   ██╗███████╗");
        System.out.println("║   ██╔════╝██╔══██╗██╔══██╗╚██╗ ██╔╝████╗ ████║██║   ██║██╔════╝");
        System.out.println("║   █████╗  ██████╔╝███████║ ╚████╔╝ ██╔████╔██║██║   ██║███████╗");
        System.out.println("║   ██╔══╝  ██╔══██╗██╔══██║  ╚██╔╝  ██║╚██╔╝██║██║   ██║╚════██║");
        System.out.println("║   ██║     ██║  ██║██║  ██║   ██║   ██║ ╚═╝ ██║╚██████╔╝███████║");
        System.out.println("║   ╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   ╚═╝     ╚═╝ ╚═════╝ ╚══════╝");
        System.out.println("║                                                              ║");
        System.out.println("║                  D A E M O N   v " + VERSION + "                  ║");
        System.out.println("║                                                              ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║   [ Mode         : HEADLESS        ]                         ║");
        System.out.println("║   [ Logic Core   : BICAMERAL       ]                         ║");
        System.out.println("║   [ Memory       : HOLOGRAPHIC     ]                         ║");
        System.out.println("║   [ Router       : RETRO-CAUSAL    ]                         ║");
        System.out.println("║   [ Web Server   : PORT 8080       ]                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    private static void printDivider() {
        System.out.println("══════════════════════════════════════════════════════════════");
    }
}
