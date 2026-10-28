package fraymus.orchestration;

import java.util.List;

/**
 * ProofAdapter
 *
 * Shared prover/verifier contract for Hydra integrity layers.
 * Current implementations:
 * - HashChainProofAdapter (active)
 * - ZkScaffoldProofAdapter (future zk hook with same contract)
 */
public interface ProofAdapter {

    String adapterId();

    ProofBundle generate(ProofInput input);

    boolean verify(ProofBundle bundle, ProofInput input);

    record ProofInput(
        long epoch,
        String seed,
        boolean incrementalUpdates,
        String previousVerifiedFrameHash,
        List<HydraProtocol.ShardAssignment> assignments
    ) {
    }

    record ProofBundle(
        String mode,
        List<HydraProtocol.ProofFrame> frames,
        String latestFrameHash,
        String verifierDigest,
        String notes
    ) {
    }
}
