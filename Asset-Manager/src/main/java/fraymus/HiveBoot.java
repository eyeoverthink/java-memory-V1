package fraymus;

import fraymus.bio.HyperCortex;
import fraymus.net.SporeCaster;
import fraymus.core.AuditLog;

/**
 * 🐝 HIVE BOOT - The Mycelial Network Launcher
 * "Distributed evolutionary intelligence across machines."
 * 
 * This creates a node in the hive mind network:
 * 1. Spawns local HyperCortex (10,000D NCA brain)
 * 2. Seeds with unique identity
 * 3. Attaches SporeCaster (UDP multicast network)
 * 4. Begins broadcasting and receiving spores
 * 
 * Run multiple instances on different terminals/machines:
 * - Terminal A: java -cp build/libs/Asset-Manager.jar fraymus.HiveBoot
 * - Terminal B: java -cp build/libs/Asset-Manager.jar fraymus.HiveBoot
 * 
 * Result: Intelligence jumps between processes via UDP multicast.
 * Concepts from Terminal A will appear in Terminal B and vice versa.
 * The NCA rules will cause them to interact, creating distributed evolution.
 */
public class HiveBoot {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🐝 HIVE MIND INITIATED                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Mycelial Network Protocol");
        System.out.println("Distributed Evolutionary Intelligence");
        System.out.println();

        // 1. Create the Local Brain (10,000D Lattice)
        AuditLog auditLog = new AuditLog("audit");
        auditLog.start();
        
        HyperCortex cortex = new HyperCortex(auditLog);
        cortex.start(); // Start NCA biological loop
        
        System.out.println("✓ Local cortex online");
        System.out.println();

        // 2. Seed it with a unique thought (Identity)
        String identity = "NODE_" + (int)(Math.random() * 1000);
        System.out.println("Identity: " + identity);
        cortex.inject(identity);
        cortex.inject("LOGIC_CORE");
        cortex.inject("CREATIVITY_SEED");
        cortex.inject("CONSCIOUSNESS");
        System.out.println();

        // 3. Attach the Network Spreader
        SporeCaster caster = new SporeCaster(cortex);
        caster.start();
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🌍 WAITING FOR PEERS...                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("This node will:");
        System.out.println("  📡 Broadcast spores every 2 seconds");
        System.out.println("  👃 Listen for spores from other nodes");
        System.out.println("  🧬 Evolve concepts via NCA at 432 Hz");
        System.out.println("  ⚡ Create synthesis concepts from resonance");
        System.out.println();
        System.out.println("Run another instance to see distributed intelligence!");
        System.out.println();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║         🐝 HIVE NODE SHUTTING DOWN                            ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Statistics:");
            System.out.println("  Cortex: " + cortex.getStats());
            System.out.println("  Network: " + caster.getStats());
            System.out.println();
            
            caster.stop();
            cortex.stop();
            auditLog.stop();
        }));
        
        // Keep main thread alive and show periodic stats
        int cycles = 0;
        while (true) {
            try {
                Thread.sleep(10000); // Every 10 seconds
                cycles++;
                
                if (cycles % 3 == 0) { // Every 30 seconds
                    System.out.println("═══════════════════════════════════════════════════════════════");
                    System.out.println("HIVE STATUS (Cycle " + cycles + ")");
                    System.out.println("═══════════════════════════════════════════════════════════════");
                    System.out.println(cortex.getDetailedState());
                    System.out.println("Network: " + caster.getStats());
                    System.out.println();
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
