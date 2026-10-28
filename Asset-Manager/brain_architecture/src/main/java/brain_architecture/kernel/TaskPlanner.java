package brain_architecture.kernel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Small best-first planner over a handful of executable templates.
 */
public final class TaskPlanner {

    public List<PlanStep> plan(TaskEnvelope envelope, List<MemoryHit> hits, String strategy) {
        List<PlanCandidate> candidates = new ArrayList<>();
        candidates.add(buildInspectFirstCandidate(envelope));
        candidates.add(buildMutationCandidate(envelope));
        candidates.add(buildSubstrateCandidate(envelope, strategy));

        candidates.sort(Comparator.comparingDouble(PlanCandidate::score).reversed());
        return candidates.get(0).steps();
    }

    private PlanCandidate buildInspectFirstCandidate(TaskEnvelope envelope) {
        double score = 0.75;
        if (!envelope.getArtifacts().isEmpty()) {
            score += 0.20;
        }
        List<PlanStep> steps = List.of(
            new PlanStep(1, "Inspect the requested artifacts and collect local context.", PlanActionHint.INGEST_ARTIFACTS),
            new PlanStep(2, "Query hybrid memory for lexical, graph, and HDC matches.", PlanActionHint.QUERY_MEMORY),
            new PlanStep(3, "Execute the primary user-space probe for the goal.", PlanActionHint.EXECUTE_SUBSTRATE),
            new PlanStep(4, "Run validation checks and enforce offline invariants.", PlanActionHint.VALIDATE),
            new PlanStep(5, "Persist the trace, routing outcome, and replay metadata.", PlanActionHint.PERSIST)
        );
        return new PlanCandidate(score, steps);
    }

    private PlanCandidate buildMutationCandidate(TaskEnvelope envelope) {
        double score = envelope.getMode() == TaskMode.MUTATION ? 1.10 : 0.40;
        List<PlanStep> steps = List.of(
            new PlanStep(1, "Inspect candidate files in a shadow workspace.", PlanActionHint.INGEST_ARTIFACTS),
            new PlanStep(2, "Retrieve relevant local memory and prior traces.", PlanActionHint.QUERY_MEMORY),
            new PlanStep(3, "Apply the mutation candidate in shadow and compare against baseline.", PlanActionHint.MUTATE),
            new PlanStep(4, "Validate the candidate before any promotion.", PlanActionHint.VALIDATE),
            new PlanStep(5, "Persist mutation evidence and promotion decision.", PlanActionHint.PERSIST)
        );
        return new PlanCandidate(score, steps);
    }

    private PlanCandidate buildSubstrateCandidate(TaskEnvelope envelope, String strategy) {
        double score = envelope.requestsF0Runtime() || envelope.requestsLocalModelRuntime() ? 1.05 : 0.45;
        if (envelope.requestsLocalModelRuntime()) {
            score += 0.08;
        }
        if ("logical".equals(strategy)) {
            score += 0.10;
        }
        List<PlanStep> steps = List.of(
            new PlanStep(1, "Load the task into the substrate adapter boundary.", PlanActionHint.EXECUTE_SUBSTRATE),
            new PlanStep(2, "Probe the F0 runtime or current substrate target.", PlanActionHint.EXECUTE_SUBSTRATE),
            new PlanStep(3, "Evaluate substrate output against replay and offline invariants.", PlanActionHint.VALIDATE),
            new PlanStep(4, "Write substrate observations into local memory.", PlanActionHint.QUERY_MEMORY),
            new PlanStep(5, "Persist a replayable substrate trace.", PlanActionHint.PERSIST)
        );
        return new PlanCandidate(score, steps);
    }

    private record PlanCandidate(double score, List<PlanStep> steps) {
    }
}
