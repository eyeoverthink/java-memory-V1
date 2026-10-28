package fraymus.orchestration;

import java.util.ArrayList;
import java.util.List;

/**
 * HashChainProofAdapter
 *
 * Deterministic incremental proof frames using SHA-256 chaining.
 * This is the active low-cost integrity backend.
 */
public final class HashChainProofAdapter implements ProofAdapter {

    @Override
    public String adapterId() {
        return "hash_chain";
    }

    @Override
    public ProofBundle generate(ProofInput input) {
        List<HydraProtocol.ProofFrame> frames = new ArrayList<>();
        String previous = input.previousVerifiedFrameHash() == null || input.previousVerifiedFrameHash().isBlank()
            ? HydraProtocol.sha256Hex("hydra_genesis|" + input.seed())
            : input.previousVerifiedFrameHash();

        List<HydraProtocol.ShardAssignment> assignments = input.assignments();
        for (int i = 0; i < assignments.size(); i++) {
            HydraProtocol.ShardAssignment assignment = assignments.get(i);
            String payload = previous
                + "|" + assignment.shardId
                + "|" + assignment.role
                + "|" + assignment.peerId
                + "|" + assignment.peerPort
                + "|" + input.epoch()
                + "|" + i;
            String hash = HydraProtocol.sha256Hex(payload);
            frames.add(new HydraProtocol.ProofFrame(input.epoch(), assignment.shardId, hash, previous));
            previous = input.incrementalUpdates() ? hash : previous;
        }

        String mode = input.incrementalUpdates()
            ? "incremental_hash_chain_zk_ready"
            : "static_hash_chain_zk_ready";
        String digest = HydraProtocol.sha256Hex(
            "verifier|" + adapterId() + "|" + input.epoch() + "|" + previous + "|" + assignments.size()
        );
        String notes =
            "Hash-chain prover/verifier active. Frames are deterministic and locally auditable. zk backend can replace this adapter without contract changes.";
        return new ProofBundle(mode, frames, previous, digest, notes);
    }

    @Override
    public boolean verify(ProofBundle bundle, ProofInput input) {
        if (bundle == null || bundle.frames() == null || bundle.frames().isEmpty()) {
            return false;
        }
        String previous = input.previousVerifiedFrameHash() == null || input.previousVerifiedFrameHash().isBlank()
            ? HydraProtocol.sha256Hex("hydra_genesis|" + input.seed())
            : input.previousVerifiedFrameHash();

        List<HydraProtocol.ShardAssignment> assignments = input.assignments();
        if (assignments.size() != bundle.frames().size()) {
            return false;
        }

        for (int i = 0; i < assignments.size(); i++) {
            HydraProtocol.ShardAssignment assignment = assignments.get(i);
            HydraProtocol.ProofFrame frame = bundle.frames().get(i);
            if (!previous.equals(frame.previousHash)) {
                return false;
            }
            String expected = HydraProtocol.sha256Hex(
                previous
                    + "|" + assignment.shardId
                    + "|" + assignment.role
                    + "|" + assignment.peerId
                    + "|" + assignment.peerPort
                    + "|" + input.epoch()
                    + "|" + i
            );
            if (!expected.equals(frame.frameHash)) {
                return false;
            }
            previous = input.incrementalUpdates() ? expected : previous;
        }
        return previous.equals(bundle.latestFrameHash());
    }
}
