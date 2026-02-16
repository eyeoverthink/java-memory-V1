package fraymus.hyper;

import java.io.Serializable;
import java.util.List;

public final class MultiScaleMemory implements Serializable {

    private static final long serialVersionUID = 1L;

    // Forward: ... -> next
    private final TransitionMemory uniF = new TransitionMemory();
    private final TransitionMemory biF  = new TransitionMemory();
    private final TransitionMemory triF = new TransitionMemory();

    // Backward: right-context -> previous
    private final TransitionMemory uniB = new TransitionMemory();
    private final TransitionMemory biB  = new TransitionMemory();
    private final TransitionMemory triB = new TransitionMemory();

    private final EntropyGate gate;

    public MultiScaleMemory() {
        this(new EntropyGate());
    }

    public MultiScaleMemory(EntropyGate gate) {
        this.gate = gate;
    }

    /** Forward key for last n tokens using Π shifts: newest gets shift=1 */
    private HyperVector keyNForward(List<HyperVector> seq, int endInclusive, int n) {
        HyperVector key = new HyperVector();
        for (int i = 0; i < n; i++) {
            int idx = endInclusive - (n - 1 - i);
            int shift = n - i;
            key = key.bind(seq.get(idx).permute(shift));
        }
        return key;
    }

    /** Backward key for first n tokens on the right using Π shifts: nearest gets shift=1 */
    private HyperVector keyNBackward(List<HyperVector> right, int startInclusive, int n) {
        HyperVector key = new HyperVector();
        for (int i = 0; i < n; i++) {
            int idx = startInclusive + i;
            int shift = i + 1;
            key = key.bind(right.get(idx).permute(shift));
        }
        return key;
    }

    /** Learn transitions forward and backward. */
    public void learnSequence(List<HyperVector> seq) {
        if (seq.size() < 2) return;

        for (int t = 0; t < seq.size() - 1; t++) {
            HyperVector cur = seq.get(t);
            HyperVector next = seq.get(t + 1);

            // ---------- Forward: cur -> next ----------
            HyperVector k1 = cur.permute(1);
            uniF.put(k1, next, 1, gate);

            if (t >= 1) {
                HyperVector k2 = keyNForward(seq, t, 2);
                biF.put(k2, next, 2, gate);
            }
            if (t >= 2) {
                HyperVector k3 = keyNForward(seq, t, 3);
                triF.put(k3, next, 4, gate);
            }

            // ---------- Backward: next -> cur ----------
            HyperVector kb1 = next.permute(1);
            uniB.put(kb1, cur, 1, gate);

            if (t + 2 < seq.size()) {
                HyperVector kb2 = keyNBackward(seq, t + 1, 2);
                biB.put(kb2, cur, 2, gate);
            }

            if (t + 3 < seq.size()) {
                HyperVector kb3 = keyNBackward(seq, t + 1, 3);
                triB.put(kb3, cur, 4, gate);
            }
        }
    }

    /** Predict next vector from left context. */
    public HyperVector predictNext(List<HyperVector> leftContext) {
        int n = leftContext.size();
        HyperVector last = leftContext.get(n - 1);

        WeightedBundler mix = new WeightedBundler();

        if (n >= 3 && triF.hasData()) {
            mix.add(triF.get(keyNForward(leftContext, n - 1, 3)), 6);
        }
        if (n >= 2 && biF.hasData()) {
            mix.add(biF.get(keyNForward(leftContext, n - 1, 2)), 3);
        }
        if (uniF.hasData()) {
            mix.add(uniF.get(last.permute(1)), 1);
        }

        return mix.build();
    }

    /** Predict previous vector from right context (first token is nearest). */
    public HyperVector predictPrev(List<HyperVector> rightContext) {
        int n = rightContext.size();
        HyperVector first = rightContext.get(0);

        WeightedBundler mix = new WeightedBundler();

        if (n >= 3 && triB.hasData()) {
            mix.add(triB.get(keyNBackward(rightContext, 0, 3)), 6);
        }
        if (n >= 2 && biB.hasData()) {
            mix.add(biB.get(keyNBackward(rightContext, 0, 2)), 3);
        }
        if (uniB.hasData()) {
            mix.add(uniB.get(first.permute(1)), 1);
        }

        return mix.build();
    }

    public boolean hasAny() {
        return uniF.hasData() || biF.hasData() || triF.hasData() || uniB.hasData() || biB.hasData() || triB.hasData();
    }

    public int totalWeight() {
        return uniF.totalWeight() + biF.totalWeight() + triF.totalWeight()
             + uniB.totalWeight() + biB.totalWeight() + triB.totalWeight();
    }

    /** Dream/idle aging hook: forget older traces. */
    public void ageShiftAll(int k) {
        uniF.ageShift(k); biF.ageShift(k); triF.ageShift(k);
        uniB.ageShift(k); biB.ageShift(k); triB.ageShift(k);
    }

    /** Persistence */
    public State snapshot() {
        return new State(
                uniF.snapshot(), biF.snapshot(), triF.snapshot(),
                uniB.snapshot(), biB.snapshot(), triB.snapshot()
        );
    }

    public void restore(State s) {
        uniF.restore(s.uniF()); biF.restore(s.biF()); triF.restore(s.triF());
        uniB.restore(s.uniB()); biB.restore(s.biB()); triB.restore(s.triB());
    }

    public record State(
            TransitionMemory.State uniF,
            TransitionMemory.State biF,
            TransitionMemory.State triF,
            TransitionMemory.State uniB,
            TransitionMemory.State biB,
            TransitionMemory.State triB
    ) implements Serializable {}
}
