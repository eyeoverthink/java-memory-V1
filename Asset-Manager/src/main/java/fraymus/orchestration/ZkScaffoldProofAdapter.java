package fraymus.orchestration;

/**
 * ZkScaffoldProofAdapter
 *
 * Future zk prover/verifier adapter wired to the shared ProofAdapter contract.
 * For now it wraps the hash-chain bundle and annotates zk mode explicitly.
 */
public final class ZkScaffoldProofAdapter implements ProofAdapter {

    private final HashChainProofAdapter fallback = new HashChainProofAdapter();

    @Override
    public String adapterId() {
        return "zk_scaffold";
    }

    @Override
    public ProofBundle generate(ProofInput input) {
        ProofBundle base = fallback.generate(input);
        String mode = input.incrementalUpdates()
            ? "incremental_zk_scaffold"
            : "static_zk_scaffold";
        String notes =
            "zk scaffold adapter active. Using hash-chain fallback while preserving prover/verifier contract for future dynamic zk-SNARK backend.";
        return new ProofBundle(
            mode,
            base.frames(),
            base.latestFrameHash(),
            base.verifierDigest(),
            notes
        );
    }

    @Override
    public boolean verify(ProofBundle bundle, ProofInput input) {
        // Until a real zk verifier is wired, reuse deterministic frame verification.
        return fallback.verify(new ProofBundle(
            "hash_chain_shadow_verify",
            bundle.frames(),
            bundle.latestFrameHash(),
            bundle.verifierDigest(),
            bundle.notes()
        ), input);
    }
}
