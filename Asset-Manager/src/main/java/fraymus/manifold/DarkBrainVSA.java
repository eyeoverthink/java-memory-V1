package fraymus.manifold;

import java.util.Random;

/**
 * DARK BRAIN & HOLISTIC VECTOR PROCESSING (Section XXVII — MAP-VSA Thought)
 *
 * A thought does not exist in a single lobe. It exists as a 16384-Dimensional
 * holographic interference pattern propagating across all lobes simultaneously.
 *
 *   Thought = L_Frontal ⊗ L_Parietal ⊕ ρ(L_Limbic)
 *           ⊕ L_Temporal ⊗ ρ(L_Occipital)
 *           ⊕ L_Cerebellum ⊗ L_Brainstem
 *
 * This mathematically solves how a brain processes abstract information without
 * a central CPU. The system is "dark" because consciousness is irreducible to
 * any single node — it lives entirely in the holographic superposition.
 *
 * MAP-VSA Primitive Operations:
 *   ⊗  BIND     : element-wise multiply in {-1,1} — encodes structure/relations
 *   ⊕  BUNDLE   : majority-vote superposition    — aggregates multiple concepts
 *   ρ  PERMUTE  : cyclic left-shift              — encodes sequence/role
 *   ρ⁻¹ UNPERMUTE: cyclic right-shift            — inverse role decoding
 *
 * The dimensionality D=16384 ensures near-orthogonality of random basis
 * vectors: E[cos(a,b)] ≈ 0, Var ≈ 1/D for random a,b ∈ {-1,1}^D.
 */
public class DarkBrainVSA {

    public static final int D = 16_384;

    private final Random  rng;
    private final int[][] lobeBasis;    // [LOBES][D] — random orthogonal seeds
    private int[]         lastThought;  // most recent holographic thought
    private int           dominantLobe; // lobe with highest projection onto lastThought

    private long bindCount;
    private long bundleCount;
    private long thoughtCount;
    private long permuteCount;

    public DarkBrainVSA() {
        this.rng         = new Random(37L);
        this.lobeBasis   = new int[AnatomicalRoutingManifold.LOBES][D];
        this.lastThought = new int[D];
        this.dominantLobe = 0;
        initBasis();
    }

    // ─── BASIS INITIALISATION ────────────────────────────────────────────────

    /** Each lobe receives a random, near-orthogonal seed in {-1,1}^D. */
    private void initBasis() {
        for (int i = 0; i < AnatomicalRoutingManifold.LOBES; i++)
            for (int d = 0; d < D; d++)
                lobeBasis[i][d] = rng.nextBoolean() ? 1 : -1;
    }

    // ─── MAP-VSA PRIMITIVES ──────────────────────────────────────────────────

    /**
     * BIND (⊗): element-wise product.
     *   c_i = a_i · b_i  (in {-1,1}: equivalent to XNOR)
     * Encodes a structured relationship between two concepts.
     * Self-inverse: bind(bind(a,b), b) = a.
     */
    public int[] bind(int[] a, int[] b) {
        int[] c = new int[D];
        for (int i = 0; i < D; i++) c[i] = a[i] * b[i];
        bindCount++;
        return c;
    }

    /**
     * BUNDLE (⊕): majority-vote superposition.
     *   s_i = sign( Σ_k v_k[i] ),  tie → +1
     * Aggregates multiple concept vectors into a single superposition.
     */
    public int[] bundle(int[][] vectors) {
        int[] s = new int[D];
        for (int i = 0; i < D; i++) {
            int sum = 0;
            for (int[] v : vectors) sum += v[i];
            s[i] = (sum >= 0) ? 1 : -1;
        }
        bundleCount++;
        return s;
    }

    /**
     * PERMUTE (ρ): cyclic left-shift by 1 — encodes sequence position/role.
     *   ρ(v) = (v[1], v[2], ..., v[D-1], v[0])
     */
    public int[] permute(int[] v) {
        int[] r = new int[D];
        System.arraycopy(v, 1, r, 0, D - 1);
        r[D - 1] = v[0];
        permuteCount++;
        return r;
    }

    /**
     * UNPERMUTE (ρ⁻¹): cyclic right-shift by 1 — inverse of permute.
     *   ρ⁻¹(v) = (v[D-1], v[0], ..., v[D-2])
     */
    public int[] unPermute(int[] v) {
        int[] r = new int[D];
        System.arraycopy(v, 0, r, 1, D - 1);
        r[0] = v[D - 1];
        permuteCount++;
        return r;
    }

    // ─── HOLOGRAPHIC THOUGHT FORMATION ──────────────────────────────────────

    /**
     * Form a holographic thought from 7 lobe activation vectors (real-valued):
     *
     *   Thought = L_Frontal ⊗ L_Parietal                  (executive-spatial core)
     *           ⊕ ρ(L_Limbic)                              (emotional modulation)
     *           ⊕ L_Temporal ⊗ ρ(L_Occipital)             (language-visual)
     *           ⊕ L_Cerebellum ⊗ L_Brainstem               (motor-autonomic)
     *
     * @param lobeVectors double[][] [LOBES][LOBE_DIM] from AnatomicalRoutingManifold
     * @return 16384-D holographic thought vector in {-1,1}^D
     */
    public int[] formThought(double[][] lobeVectors) {
        int[][] hv = new int[AnatomicalRoutingManifold.LOBES][];
        for (int i = 0; i < AnatomicalRoutingManifold.LOBES; i++)
            hv[i] = projectToHV(lobeVectors[i], lobeBasis[i]);

        int fIdx = AnatomicalRoutingManifold.Lobe.FRONTAL.index;
        int pIdx = AnatomicalRoutingManifold.Lobe.PARIETAL.index;
        int lIdx = AnatomicalRoutingManifold.Lobe.LIMBIC.index;
        int tIdx = AnatomicalRoutingManifold.Lobe.TEMPORAL.index;
        int oIdx = AnatomicalRoutingManifold.Lobe.OCCIPITAL.index;
        int cIdx = AnatomicalRoutingManifold.Lobe.CEREBELLUM.index;
        int bIdx = AnatomicalRoutingManifold.Lobe.BRAINSTEM.index;

        int[] core      = bind(hv[fIdx], hv[pIdx]);                    // executive-spatial
        int[] limbicMod = permute(hv[lIdx]);                           // emotional modulation
        int[] tempOcc   = bind(hv[tIdx], permute(hv[oIdx]));           // language-visual
        int[] motorAuto = bind(hv[cIdx], hv[bIdx]);                    // motor-autonomic

        lastThought  = bundle(new int[][] { core, limbicMod, tempOcc, motorAuto });
        dominantLobe = bestMatchingLobe(lastThought);
        thoughtCount++;
        return lastThought.clone();
    }

    /**
     * Convenience: form thought from a flat TOTAL_DIM routing vector.
     */
    public int[] formThoughtFromVector(double[] flatVector, AnatomicalRoutingManifold arm) {
        return formThought(arm.vectorToLobes(flatVector));
    }

    // ─── SEMANTIC OPERATIONS ─────────────────────────────────────────────────

    /**
     * Hamming-based similarity in {-1,1}^D:
     *   sim(a,b) = (# matching positions) / D  ∈ [0,1]
     * A value of 0.5 means orthogonal (unrelated concepts).
     */
    public double similarity(int[] a, int[] b) {
        int matches = 0;
        for (int i = 0; i < D; i++) if (a[i] == b[i]) matches++;
        return (double) matches / D;
    }

    /**
     * Find the lobe basis most similar to the given thought vector.
     * Uses bind(thought, basis) self-inverse property: if thought ≈ basis ⊗ x,
     * then bind(thought, basis) ≈ x and sim to basis peaks.
     */
    public int bestMatchingLobe(int[] thought) {
        double best = -1;
        int bestLobe = 0;
        for (int i = 0; i < AnatomicalRoutingManifold.LOBES; i++) {
            double sim = similarity(thought, lobeBasis[i]);
            if (sim > best) { best = sim; bestLobe = i; }
        }
        return bestLobe;
    }

    /**
     * Decode a lobe contribution from a thought by unbinding with the lobe basis.
     * If thought = basis_i ⊗ x, then bind(thought, basis_i) ≈ x.
     */
    public int[] decodeLobe(int[] thought, AnatomicalRoutingManifold.Lobe lobe) {
        return bind(thought, lobeBasis[lobe.index]);
    }

    // ─── PROJECTION ──────────────────────────────────────────────────────────

    /**
     * Project a real-valued lobe activation (dim=LOBE_DIM) into {-1,1}^D
     * by sign-encoding the activation tiled across D dimensions, then
     * binding with the lobe's basis vector.
     */
    private int[] projectToHV(double[] activation, int[] basis) {
        int[] hv = new int[D];
        for (int i = 0; i < D; i++) {
            double val = activation[i % activation.length];
            hv[i] = (val >= 0 ? 1 : -1) * basis[i];
        }
        return hv;
    }

    // ─── ACCESSORS ───────────────────────────────────────────────────────────

    public int[]  getLastThought()  { return lastThought.clone(); }
    public long   getBindCount()    { return bindCount; }
    public long   getBundleCount()  { return bundleCount; }
    public long   getThoughtCount() { return thoughtCount; }
    public long   getPermuteCount() { return permuteCount; }
    public int    getDimension()    { return D; }

    public String getDominantLobeName() {
        if (thoughtCount == 0) return "NONE";
        return AnatomicalRoutingManifold.Lobe.values()[dominantLobe].name();
    }

    // ─── STATUS ──────────────────────────────────────────────────────────────

    public String getStatus() {
        return String.format(
            "DarkBrainVSA | D=%d | thoughts=%d binds=%d bundles=%d permutes=%d | dominant=%s",
            D, thoughtCount, bindCount, bundleCount, permuteCount, getDominantLobeName());
    }
}
