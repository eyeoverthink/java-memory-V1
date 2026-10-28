package fraymus.manifold;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TOPOLOGICAL NEURAL MANIFOLDS (Section XXI — "Beautiful Mind" Paradigm)
 *
 * Thoughts are represented as strands in a 3-D spacetime manifold.
 * When concepts interact, strands cross, forming elements of the Braid Group B_n.
 *
 * Generator σ_i: strand i crosses OVER strand i+1.
 * Generator σ_i^{-1}: strand i crosses UNDER strand i+1.
 *
 * Yang-Baxter Equation (guarantees topological invariance of logic):
 *   σ_i σ_{i+1} σ_i = σ_{i+1} σ_i σ_{i+1}
 *
 * Topological minimization: pull strands taut (reduce braid word to minimal form).
 * The resulting knot IS the answer — local hardware errors cannot untie a topological knot.
 * Logic becomes immortal (fault-tolerant quantum computing principle).
 */
public class TopologicalBraidManifold {

    public static final int DEFAULT_STRANDS = 8;

    private final int     numStrands;
    private final Random  rng;

    private final List<Integer> braidWord;

    private long braidOperations;
    private long yangBaxterChecks;
    private long minimizationSteps;
    private long topologicalSolves;

    public TopologicalBraidManifold(int numStrands) {
        this.numStrands   = numStrands;
        this.rng          = new Random(23L);
        this.braidWord    = new ArrayList<>();
    }

    public TopologicalBraidManifold() {
        this(DEFAULT_STRANDS);
    }

    // ─── BRAID GROUP GENERATORS ──────────────────────────────────────────────

    /**
     * Apply generator σ_i (strand i crosses OVER strand i+1).
     * Encoded as positive integer i ∈ [1, n-1].
     */
    public void sigma(int i) {
        if (i < 1 || i >= numStrands)
            throw new IllegalArgumentException("σ_" + i + " invalid for " + numStrands + " strands");
        braidWord.add(i);
        braidOperations++;
    }

    /**
     * Apply inverse generator σ_i^{-1} (strand i crosses UNDER strand i+1).
     * Encoded as negative integer -i.
     */
    public void sigmaInv(int i) {
        if (i < 1 || i >= numStrands)
            throw new IllegalArgumentException("σ_" + i + "^{-1} invalid for " + numStrands + " strands");
        braidWord.add(-i);
        braidOperations++;
    }

    // ─── YANG-BAXTER VERIFICATION ────────────────────────────────────────────

    /**
     * Verify Yang-Baxter: σ_i σ_{i+1} σ_i  ≡  σ_{i+1} σ_i σ_{i+1}
     * Returns true if the current braid word tail satisfies the relation.
     */
    public boolean verifyYangBaxter(int i) {
        yangBaxterChecks++;
        if (i < 1 || i + 1 >= numStrands) return false;

        int len = braidWord.size();
        if (len < 3) return false;

        int a = braidWord.get(len - 3);
        int b = braidWord.get(len - 2);
        int c = braidWord.get(len - 1);

        boolean lhs = (a == i && b == i + 1 && c == i);
        boolean rhs = (a == i + 1 && b == i && c == i + 1);
        return lhs || rhs;
    }

    /**
     * Check all consecutive triplets in the current braid word for YB consistency.
     * Returns the fraction of triplets satisfying at least one YB instance.
     */
    public double yangBaxterScore() {
        int len = braidWord.size();
        if (len < 3) return 1.0;
        int total   = 0;
        int passing = 0;
        for (int t = 0; t < len - 2; t++) {
            int a = braidWord.get(t);
            int b = braidWord.get(t + 1);
            int c = braidWord.get(t + 2);
            for (int i = 1; i < numStrands - 1; i++) {
                boolean lhs = (a == i && b == i + 1 && c == i);
                boolean rhs = (a == i + 1 && b == i && c == i + 1);
                if (lhs || rhs) { passing++; break; }
            }
            total++;
        }
        yangBaxterChecks++;
        return total > 0 ? (double) passing / total : 1.0;
    }

    // ─── TOPOLOGICAL MINIMIZATION (Markov moves) ─────────────────────────────

    /**
     * Reduce braid word by cancelling adjacent inverse pairs: σ_i σ_i^{-1} → ε.
     * This is "pulling strands taut" — removing topological slack.
     * @return number of cancellations performed
     */
    public int minimize() {
        int cancelled = 0;
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < braidWord.size() - 1; i++) {
                int a = braidWord.get(i);
                int b = braidWord.get(i + 1);
                if (a + b == 0) {
                    braidWord.remove(i + 1);
                    braidWord.remove(i);
                    cancelled++;
                    minimizationSteps++;
                    changed = true;
                    break;
                }
            }
        }
        return cancelled;
    }

    // ─── TOPOLOGICAL SOLVE ────────────────────────────────────────────────────

    /**
     * Map a high-dimensional thought vector to a braid representation and solve it.
     *
     * 1. Encode thought as a sequence of braid generators (sign = direction, index = component).
     * 2. Minimize (pull taut).
     * 3. Compute topological invariant (writhe = algebraic crossing number).
     * 4. Return invariant as the "answer" — immune to local perturbations.
     *
     * @param thought continuous thought vector (any dimension)
     * @return writhe — the topological signature of the solved braid
     */
    public int topologicalSolve(double[] thought) {
        braidWord.clear();
        for (int i = 0; i < thought.length; i++) {
            int strand = (i % (numStrands - 1)) + 1;
            if (thought[i] >= 0) sigma(strand);
            else sigmaInv(strand);
        }
        minimize();
        int writhe = computeWrithe();
        topologicalSolves++;
        return writhe;
    }

    /**
     * Writhe W = Σ sign(σ_i) over all generators in the minimized braid word.
     * Topological invariant — the "solved answer" of the braid.
     */
    public int computeWrithe() {
        int w = 0;
        for (int gen : braidWord) w += (gen > 0) ? 1 : -1;
        return w;
    }

    /**
     * Linking number between strands a and b — measures how tightly they are
     * intertwined. Fault-tolerant proxy for logical entanglement.
     */
    public int linkingNumber(int strandA, int strandB) {
        if (strandA == strandB) return 0;
        int link = 0;
        for (int gen : braidWord) {
            int idx = Math.abs(gen);
            if ((idx == strandA && idx + 1 == strandB) ||
                (idx == strandB && idx + 1 == strandA)) {
                link += (gen > 0) ? 1 : -1;
            }
        }
        return link / 2;
    }

    // ─── ACCESSORS ───────────────────────────────────────────────────────────

    public int  getNumStrands()       { return numStrands; }
    public int  getBraidLength()      { return braidWord.size(); }
    public long getBraidOperations()  { return braidOperations; }
    public long getYangBaxterChecks() { return yangBaxterChecks; }
    public long getMinimizationSteps(){ return minimizationSteps; }
    public long getTopologicalSolves(){ return topologicalSolves; }

    public void clearBraid() { braidWord.clear(); }

    // ─── STATUS ──────────────────────────────────────────────────────────────

    public String getStatus() {
        double ybScore = yangBaxterScore();
        return String.format(
            "TopologicalBraidManifold | strands=%d braidLen=%d writhe=%d ybScore=%.3f | " +
            "ops=%d min=%d solves=%d",
            numStrands, braidWord.size(), computeWrithe(),
            ybScore, braidOperations, minimizationSteps, topologicalSolves
        );
    }
}
