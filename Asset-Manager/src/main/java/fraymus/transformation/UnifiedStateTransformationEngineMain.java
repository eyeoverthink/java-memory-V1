package fraymus.transformation;

/**
 * Main class to run Unified State Transformation Engine
 * 
 * This demonstrates the deepest layer: everything as sequences of constrained transformations in state space.
 */
public class UnifiedStateTransformationEngineMain {
    
    public static void main(String[] args) {
        System.out.println("🔬 UNIFIED STATE TRANSFORMATION ENGINE");
        System.out.println("=====================================");
        System.out.println();
        
        UnifiedStateTransformationEngine engine = new UnifiedStateTransformationEngine();
        
        // Initialize
        engine.initialize();
        System.out.println();
        
        // Show status
        System.out.println("STATUS:");
        System.out.println(engine.getStatus());
        System.out.println();
        
        // Compile and test
        System.out.println("COMPILING AND TESTING:");
        engine.compileAndTest();
        System.out.println();
        
        // Scan files
        System.out.println("SCANNING FILES:");
        engine.scanFiles(".");
        System.out.println();
        
        // Scan home folders (commented out to prevent excessive scanning)
        // System.out.println("SCANNING HOME FOLDERS:");
        // engine.scanHomeFolders();
        // System.out.println();
        
        // Save improvements
        System.out.println("SAVING IMPROVEMENTS:");
        engine.saveImprovements();
        System.out.println();
        
        // Shutdown
        System.out.println("SHUTDOWN:");
        engine.shutdown();
        System.out.println();
        
        System.out.println("✓ Unified State Transformation Engine demonstration complete");
    }
}
