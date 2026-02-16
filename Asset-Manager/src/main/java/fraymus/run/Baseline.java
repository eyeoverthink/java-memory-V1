package fraymus.run;

/**
 * Baseline optimization method for comparison
 */
public interface Baseline {
    /**
     * Baseline name for identification
     */
    String name();
    
    /**
     * Run baseline with given context and budget
     */
    EngineResult run(RunContext ctx, EngineBudget budget) throws Exception;
}
