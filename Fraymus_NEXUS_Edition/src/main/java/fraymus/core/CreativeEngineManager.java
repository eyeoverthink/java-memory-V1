package fraymus.core;

/**
 * CREATIVE ENGINE MANAGER
 * Singleton pattern for shared CreativeEngine instance
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * Provides centralized access to the CreativeEngine across:
 * - CommandTerminal
 * - FraymusAPI
 * - Main application
 * 
 * This ensures only one CreativeEngine instance exists,
 * preventing duplicate gravity/fusion threads.
 */
public class CreativeEngineManager {
    
    private static CreativeEngine instance = null;
    private static boolean autoStart = false;
    
    /**
     * Get the singleton CreativeEngine instance
     * Creates it if it doesn't exist
     */
    public static synchronized CreativeEngine getInstance() {
        if (instance == null) {
            instance = new CreativeEngine();
            if (autoStart) {
                instance.start();
            }
        }
        return instance;
    }
    
    /**
     * Check if CreativeEngine exists and is running
     */
    public static boolean isRunning() {
        return instance != null && instance.isRunning();
    }
    
    /**
     * Start the CreativeEngine
     */
    public static void start() {
        CreativeEngine engine = getInstance();
        if (!engine.isRunning()) {
            engine.start();
        }
    }
    
    /**
     * Stop the CreativeEngine
     */
    public static void stop() {
        if (instance != null && instance.isRunning()) {
            instance.stop();
        }
    }
    
    /**
     * Set whether to auto-start on first getInstance() call
     */
    public static void setAutoStart(boolean auto) {
        autoStart = auto;
    }
    
    /**
     * Reset the singleton (for testing)
     */
    public static synchronized void reset() {
        if (instance != null && instance.isRunning()) {
            instance.stop();
        }
        instance = null;
    }
    
    /**
     * Get statistics
     */
    public static void printStats() {
        if (instance != null) {
            instance.printStats();
        } else {
            System.out.println("CreativeEngine not initialized");
        }
    }
}
