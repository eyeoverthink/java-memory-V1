package fraymus.run;

/**
 * Budget constraints for fair baseline comparison
 * Ensures apples-to-apples comparison
 */
public class EngineBudget {
    public final int evaluations;
    public final int steps;
    public final int population;
    
    public EngineBudget(int evaluations, int steps, int population) {
        this.evaluations = evaluations;
        this.steps = steps;
        this.population = population;
    }
    
    public static EngineBudget fromConfig(RunConfig cfg) {
        return new EngineBudget(
            cfg.steps * cfg.populationSize,
            cfg.steps,
            cfg.populationSize
        );
    }
}
