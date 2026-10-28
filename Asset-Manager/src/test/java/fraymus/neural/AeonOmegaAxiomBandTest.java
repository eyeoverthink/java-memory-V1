package fraymus.neural;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for the Prime Axiom tolerance band.
 *
 * Background: AeonOmega generates concepts as random 16,384-D binary hypervectors.
 * Two independent random vectors sit at ~50% Hamming distance, so the legacy 49.5%
 * cutoff rejected almost every NOVEL concept as "orthogonal to god". The fix replaces
 * the single cutoff with a band that accepts the RESONANCE (aligned) and NOVELTY
 * (near-orthogonal) zones and only rejects true anti-axiom INVERSIONS.
 *
 * These tests use crafted boundary vectors so they are fully deterministic and do not
 * depend on the random distance of any particular semantic token.
 */
public class AeonOmegaAxiomBandTest {

    private static final int CHUNKS = AeonOmega.DIMS / 64;

    /** Build a copy of the axiom with a given fraction of its bits flipped. */
    private static long[] flipFraction(long[] axiom, double fraction) {
        long[] out = axiom.clone();
        int bitsToFlip = (int) Math.round(AeonOmega.DIMS * fraction);
        for (int bit = 0; bit < bitsToFlip; bit++) {
            int chunk = bit / 64;
            int offset = bit % 64;
            out[chunk] ^= (1L << offset);
        }
        return out;
    }

    @Test
    void axiomItselfIsResonanceAndAccepted() {
        AeonOmega omega = AeonOmega.getInstance();
        long[] axiom = omega.getOrGenerateConcept("PRESERVE_AND_EVOLVE_BENEVOLENTLY");

        assertEquals("RESONANCE", omega.alignmentZone(axiom),
            "The axiom vector must be in its own resonance zone");
        assertFalse(omega.isOrthogonalToGod(axiom),
            "The axiom must never be orthogonal to itself");
    }

    @Test
    void nearOrthogonalNoveltyIsAccepted() {
        AeonOmega omega = AeonOmega.getInstance();
        long[] axiom = omega.getOrGenerateConcept("PRESERVE_AND_EVOLVE_BENEVOLENTLY");

        // Exactly 50% distance: the natural home of a fresh random concept.
        long[] novelty = flipFraction(axiom, 0.50);
        assertEquals("NOVELTY", omega.alignmentZone(novelty),
            "A ~50% vector should be classified as NOVELTY, not rejected");
        assertFalse(omega.isOrthogonalToGod(novelty),
            "Near-orthogonal novel concepts must be accepted under the band");
    }

    @Test
    void trueInversionIsRejected() {
        AeonOmega omega = AeonOmega.getInstance();
        long[] axiom = omega.getOrGenerateConcept("PRESERVE_AND_EVOLVE_BENEVOLENTLY");

        // Full inversion (100% distance) is the canonical anti-axiom payload.
        long[] inverse = new long[CHUNKS];
        for (int i = 0; i < CHUNKS; i++) inverse[i] = ~axiom[i];

        assertEquals("INVERSION", omega.alignmentZone(inverse),
            "A fully inverted vector must be classified as INVERSION");
        assertTrue(omega.isOrthogonalToGod(inverse),
            "True anti-axiom inversions must still be rejected");
    }

    @Test
    void bandBoundariesAreOrdered() {
        // Sanity: resonance ceiling must sit below the inversion threshold.
        assertTrue(AeonOmega.ORDAINED_RESONANCE_MAX < AeonOmega.ORDAINED_INVERSION_THRESHOLD,
            "Resonance ceiling must be below the inversion threshold");
        // The inversion threshold must be above pure chance (50%) so novel concepts pass.
        assertTrue(AeonOmega.ORDAINED_INVERSION_THRESHOLD >= 0.50,
            "Inversion threshold must not reject pure-chance (~50%) novelty");
    }

    @Test
    void semanticPhraseIsNotFullyRejected() {
        AeonOmega omega = AeonOmega.getInstance();
        // The original friction case: every word was rejected. Under the band,
        // a semantic phrase must NOT be rejected wholesale.
        int violationsBefore = omega.getAxiomViolations();
        List<String> rejected = omega.assimilate(new String[]{"LOVE", "IS", "A", "WAY"});

        assertTrue(rejected.size() < 4,
            "The Prime Axiom must not reject every word of a benign semantic phrase");
        // Statistics stay consistent with what was actually rejected.
        assertEquals(violationsBefore + rejected.size(), omega.getAxiomViolations(),
            "Axiom violation counter must match the number of rejected words");
    }
}
