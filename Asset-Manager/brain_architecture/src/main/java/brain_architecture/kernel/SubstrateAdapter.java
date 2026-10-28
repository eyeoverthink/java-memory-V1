package brain_architecture.kernel;

import java.util.List;

/**
 * ABI for execution targets.
 */
public interface SubstrateAdapter {
    String getAdapterId();

    SubstrateSession loadContext(TaskEnvelope task, List<MemoryHit> context);

    SubstrateResult step(SubstrateSession session, String stepInstruction);

    SubstrateResult act(SubstrateSession session, SubstrateAction action);

    SubstrateSnapshot snapshot(SubstrateSession session);

    SubstrateSession restore(SubstrateSnapshot snapshot);
}
