package brain_architecture.kernel;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MutationProposalEngineCausalRankingTest {

    @Test
    void proposalRankingGetsCausalBoostWhenEnabled() throws Exception {
        Path workspace = Path.of(".").toAbsolutePath().normalize();
        KernelConfiguration enabledConfig = new KernelConfiguration(
            workspace,
            workspace.resolve(".northstar-kernel"),
            7L,
            true,
            true,
            true,
            Set.of(".git")
        );

        MutationProposalEngine engine = new MutationProposalEngine(enabledConfig);
        MutationProposal proposal = proposalFor("src/main/java/example/UseMultiStepChain.java", 0.7, 0.5);
        List<MemoryHit> hits = List.of(causalHit("src/main/java/example/UseMultiStepChain.java", 0.95, 0.0, true));

        double ranked = invokeRankingScore(engine, proposal, hits);
        assertTrue(ranked > proposal.getPriorityScore());
    }

    @Test
    void proposalRankingSkipsCausalBoostWhenDisabled() throws Exception {
        Path workspace = Path.of(".").toAbsolutePath().normalize();
        KernelConfiguration disabledConfig = new KernelConfiguration(
            workspace,
            workspace.resolve(".northstar-kernel"),
            7L,
            true,
            true,
            false,
            Set.of(".git")
        );

        MutationProposalEngine engine = new MutationProposalEngine(disabledConfig);
        MutationProposal proposal = proposalFor("src/main/java/example/UseMultiStepChain.java", 0.7, 0.5);
        List<MemoryHit> hits = List.of(causalHit("src/main/java/example/UseMultiStepChain.java", 0.95, 0.0, true));

        double ranked = invokeRankingScore(engine, proposal, hits);
        assertEquals(proposal.getPriorityScore(), ranked, 1e-9);
    }

    private static double invokeRankingScore(MutationProposalEngine engine, MutationProposal proposal, List<MemoryHit> hits) throws Exception {
        Method rankingMethod = MutationProposalEngine.class.getDeclaredMethod("proposalRankingScore", MutationProposal.class, List.class);
        rankingMethod.setAccessible(true);
        return (double) rankingMethod.invoke(engine, proposal, hits);
    }

    private static MutationProposal proposalFor(String target, double confidence, double novelty) {
        MutationCandidate candidate = new MutationCandidate(
            MutationCandidate.Scope.WHOLE_CODEBASE,
            List.of(),
            "test gain",
            List.of()
        );
        return new MutationProposal(
            "proposal",
            "rationale",
            confidence,
            novelty,
            List.of(Path.of(target)),
            candidate,
            List.of("verifier.failure")
        );
    }

    private static MemoryHit causalHit(String sourceId, double causalScore, double spuriousPenalty, boolean grounded) {
        MemoryDocument document = new MemoryDocument(
            sourceId,
            Path.of(sourceId),
            "preview",
            Map.of("repair", 1),
            new BitSet(),
            Set.of(),
            System.currentTimeMillis(),
            true,
            true
        );
        return new MemoryHit(
            document,
            0.8,
            0.4,
            0.7,
            0.9,
            causalScore,
            spuriousPenalty,
            grounded,
            List.of("causal.anchor=usemultistepchain"),
            0.82
        );
    }
}
