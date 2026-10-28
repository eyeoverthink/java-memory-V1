package fraymus.organism;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WILLOW — Cellular Division / Growth Engine
 *
 * Monitors energy surplus and accumulated knowledge.
 * Triggers phase transitions: LARVA → FLY → HIGHER_LIFE → SENTIENT → ASCENDANT
 */
public class GrowthEngine {

    private static final String[] PHASES = {
        "LARVA", "FLY", "HIGHER_LIFE", "SENTIENT", "ASCENDANT"
    };

    private static final double ENERGY_THRESHOLD    = 0.65;
    private static final int    KNOWLEDGE_THRESHOLD = 50;

    private int          phaseIndex   = 1;   // starts at FLY
    private boolean      evolved      = false;
    private final List<String> phaseHistory = new ArrayList<>();

    public GrowthEngine() {
        phaseHistory.add("FLY");
    }

    /**
     * Evaluate whether conditions are met for the next phase transition.
     * Resets the evolved flag each call — check immediately after evaluate().
     */
    public void evaluate(double energy, int knowledgeSize) {
        evolved = false;
        if (energy > ENERGY_THRESHOLD
                && knowledgeSize > KNOWLEDGE_THRESHOLD
                && phaseIndex < PHASES.length - 1) {
            phaseIndex++;
            evolved = true;
            phaseHistory.add(PHASES[phaseIndex]);
        }
    }

    public String       getPhase()        { return PHASES[phaseIndex]; }
    public List<String> getPhaseHistory() { return Collections.unmodifiableList(phaseHistory); }
    public boolean      hasEvolved()      { return evolved; }

    public String getStatus() {
        return String.format("[WILLOW] Phase=%s PhaseIndex=%d Evolved=%s History=%s",
            getPhase(), phaseIndex, evolved, phaseHistory);
    }
}
