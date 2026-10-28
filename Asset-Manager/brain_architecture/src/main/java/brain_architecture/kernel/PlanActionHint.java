package brain_architecture.kernel;

/**
 * Hint for the executor about the dominant action in a step.
 */
public enum PlanActionHint {
    INGEST_ARTIFACTS,
    QUERY_MEMORY,
    EXECUTE_SUBSTRATE,
    VALIDATE,
    MUTATE,
    PERSIST
}
