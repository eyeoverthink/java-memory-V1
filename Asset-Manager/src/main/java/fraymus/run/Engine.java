package fraymus.run;

/**
 * Standardized interface for all optimization engines
 */
public interface Engine {
    /**
     * Engine name for logging and identification
     */
    String name();
    
    /**
     * Run the engine with given context
     * Returns results for comparison
     */
    EngineResult run(RunContext ctx) throws Exception;
}
