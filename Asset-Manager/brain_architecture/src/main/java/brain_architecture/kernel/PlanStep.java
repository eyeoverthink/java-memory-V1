package brain_architecture.kernel;

/**
 * A single plan step selected by the planner.
 */
public final class PlanStep {
    private final int index;
    private final String description;
    private final PlanActionHint hint;

    public PlanStep(int index, String description, PlanActionHint hint) {
        this.index = index;
        this.description = description;
        this.hint = hint;
    }

    public int getIndex() {
        return index;
    }

    public String getDescription() {
        return description;
    }

    public PlanActionHint getHint() {
        return hint;
    }
}
