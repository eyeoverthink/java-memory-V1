package fraymus.core;

/**
 * CREATIVE ENGINE - The Complete System
 * "A Particle Collider for Thoughts"
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * This is the unified interface for the entire Spatial Computing system:
 * - PhiNode: Base particle (every object has coordinates)
 * - PhiSuit: Data exoskeleton (wraps any object in 5D space)
 * - Lazarus: Registry of all souls (tracks everything)
 * - GravityEngine: Hebbian physics (pulls related thoughts together)
 * - FusionReactor: Creative synthesis (spawns new ideas from collisions)
 * 
 * Usage:
 *   CreativeEngine engine = new CreativeEngine();
 *   engine.start();
 *   
 *   PhiSuit<String> concept = engine.inject("Java", 10, 10, 0);
 *   concept.get(); // Use it, make it hot
 *   
 *   // Watch as related concepts drift together and fuse
 */
public class CreativeEngine {
    
    private GravityEngine gravityEngine;
    private FusionReactor fusionReactor;
    private Thread gravityThread;
    private Thread fusionThread;
    private boolean running = false;
    
    /**
     * Start the Creative Engine
     */
    public void start() {
        if (running) {
            System.out.println("⚠️  Creative Engine already running");
            return;
        }
        
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║ CREATIVE ENGINE - INITIALIZING                            ║");
        System.out.println("║ Patent: VS-PoQC-19046423-φ⁷⁵-2025                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        gravityEngine = new GravityEngine();
        fusionReactor = new FusionReactor();
        
        gravityThread = new Thread(gravityEngine, "GravityEngine");
        fusionThread = new Thread(fusionReactor, "FusionReactor");
        
        gravityThread.start();
        fusionThread.start();
        
        running = true;
        
        System.out.println("✅ CREATIVE ENGINE ONLINE");
        System.out.println();
    }
    
    /**
     * Stop the Creative Engine
     */
    public void stop() {
        if (!running) {
            System.out.println("⚠️  Creative Engine not running");
            return;
        }
        
        System.out.println("🛑 Shutting down Creative Engine...");
        
        gravityEngine.stop();
        fusionReactor.stop();
        gravityThread.interrupt();
        fusionThread.interrupt();
        
        running = false;
        
        System.out.println("✅ CREATIVE ENGINE SHUTDOWN COMPLETE");
    }
    
    /**
     * Inject a new concept into the universe
     */
    public <T> PhiSuit<T> inject(T data, int x, int y, int z) {
        PhiSuit<T> suit = new PhiSuit<>(data, x, y, z);
        System.out.println("💉 Injected: " + suit.toString());
        return suit;
    }
    
    /**
     * Get current universe statistics
     */
    public void printStats() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ CREATIVE ENGINE STATISTICS                              │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│ Status:              " + String.format("%-36s", running ? "RUNNING" : "STOPPED") + "│");
        System.out.println("│ Universe Size:       " + String.format("%-36d", GravityEngine.getUniverseSize()) + "│");
        System.out.println("│ Total Fusions:       " + String.format("%-36d", fusionReactor.getTotalFusions()) + "│");
        System.out.println("│ Generation:          " + String.format("%-36d", Lazarus.GEN_COUNT) + "│");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println();
    }
    
    /**
     * Visualize the universe
     */
    public void visualize() {
        Lazarus.printUniverse();
    }
    
    /**
     * Get the Gravity Engine
     */
    public GravityEngine getGravityEngine() {
        return gravityEngine;
    }
    
    /**
     * Get the Fusion Reactor
     */
    public FusionReactor getFusionReactor() {
        return fusionReactor;
    }
    
    /**
     * Check if engine is running
     */
    public boolean isRunning() {
        return running;
    }
}
