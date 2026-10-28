package fraymus.connectome;

import fraymus.exodus.ExodusMBRGenerator;

/**
 * CONNECTOME BOOT — Integrates the 9-node biological swarm and Exodus protocol
 * 
 * This class provides a simple integration point for the new connectome components:
 * - BrainFeedbackListener (Synaptic Bridge event bus)
 * - BiologicalConnectome (unified 9-node swarm)
 * - ExodusMBRGenerator (bare-metal MBR generation)
 * 
 * Call this from FraynixBoot or any main entry point to initialize the connectome.
 */
public class ConnectomeBoot {
    
    private static BrainFeedbackListener synapticBridge;
    private static BiologicalConnectome biologicalConnectome;
    
    /**
     * Initialize the connectome system
     */
    public static void initialize() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   CONNECTOME BOOT: 9-Node Biological Swarm                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Initialize Synaptic Bridge
        synapticBridge = new BrainFeedbackListener();
        synapticBridge.start();
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Initialize Biological Connectome
        biologicalConnectome = new BiologicalConnectome();
        biologicalConnectome.start();
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("   ✓ Connectome boot complete.");
        System.out.println();
    }
    
    /**
     * Shutdown the connectome system
     */
    public static void shutdown() {
        System.out.println();
        System.out.println("   Shutting down connectome...");
        
        if (biologicalConnectome != null) {
            biologicalConnectome.stop();
        }
        
        if (synapticBridge != null) {
            synapticBridge.stop();
        }
        
        System.out.println("   ✓ Connectome shutdown complete.");
        System.out.println();
    }
    
    /**
     * Get the Synaptic Bridge
     */
    public static BrainFeedbackListener getSynapticBridge() {
        return synapticBridge;
    }
    
    /**
     * Get the Biological Connectome
     */
    public static BiologicalConnectome getBiologicalConnectome() {
        return biologicalConnectome;
    }
    
    /**
     * Generate Exodus MBR image
     */
    public static void generateExodusMBR(String outputPath) {
        System.out.println();
        System.out.println("   Generating Exodus MBR...");
        try {
            ExodusMBRGenerator.generateMBR(outputPath);
            ExodusMBRGenerator.verifyMBR(outputPath);
        } catch (Exception e) {
            System.err.println("   [Exodus] Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Get connectome status
     */
    public static String getStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Connectome Status:\n");
        
        if (synapticBridge != null) {
            status.append("  Synaptic Bridge: ").append(synapticBridge.getStatus()).append("\n");
        } else {
            status.append("  Synaptic Bridge: NOT INITIALIZED\n");
        }
        
        if (biologicalConnectome != null) {
            status.append("  Biological Connectome: consciousness=").append(biologicalConnectome.getConsciousnessLevel())
                .append(" phi-res=").append(biologicalConnectome.getPhiResonance()).append("\n");
        } else {
            status.append("  Biological Connectome: NOT INITIALIZED\n");
        }
        
        return status.toString();
    }
    
    /**
     * Demonstration
     */
    public static void main(String[] args) throws InterruptedException {
        // Initialize connectome
        initialize();
        
        // Show status
        System.out.println(getStatus());
        
        // Execute a Lazarus cycle
        System.out.println("   Executing Lazarus cycle...");
        biologicalConnectome.executeLazarusCycle();
        
        Thread.sleep(1000);
        
        // Generate Exodus MBR
        generateExodusMBR("fraynix_genesis.img");
        
        Thread.sleep(1000);
        
        // Show final status
        System.out.println(getStatus());
        
        // Shutdown
        shutdown();
    }
}
