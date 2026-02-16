package fraymus.hyper;

import java.io.Serializable;
import java.util.*;

/**
 * Cleanup memory: stores prototype vectors for tokens.
 * Decode = pick token with max resonance. Supports Top-K and clustering.
 */
public final class CleanupMemory implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, HyperVector> prototypes = new HashMap<>();

    /** Register/refresh prototype for a token. */
    public void observe(String token, HyperVector v) {
        prototypes.put(token, v);
    }

    /** Alias for observe — backward compat */
    public void memorize(String token, HyperVector v) {
        prototypes.put(token, v);
    }

    public void setPrototype(String token, HyperVector v) {
        prototypes.put(token, v);
    }

    public boolean contains(String token) { return prototypes.containsKey(token); }
    public HyperVector prototypeOf(String token) { return prototypes.get(token); }
    public int size() { return prototypes.size(); }

    public record ScoredToken(String token, double resonance) implements Serializable {}

    /** Top-K by resonance */
    public List<ScoredToken> topK(HyperVector target, int k) {
        if (k <= 0) return List.of();
        PriorityQueue<ScoredToken> pq = new PriorityQueue<>(Comparator.comparingDouble(ScoredToken::resonance));

        for (Map.Entry<String, HyperVector> e : prototypes.entrySet()) {
            double r = e.getValue().resonance(target);
            if (pq.size() < k) pq.add(new ScoredToken(e.getKey(), r));
            else if (r > pq.peek().resonance()) {
                pq.poll();
                pq.add(new ScoredToken(e.getKey(), r));
            }
        }

        ArrayList<ScoredToken> out = new ArrayList<>(pq);
        out.sort((a, b) -> Double.compare(b.resonance(), a.resonance()));
        return out;
    }

    /** Basic decode */
    public String decode(HyperVector target, double minResonance) {
        ScoredToken best = bestOne(target);
        return (best.resonance() >= minResonance) ? best.token() : "???";
    }

    /** Resonance clustering decode: stabilize by bundling top-K prototypes and decoding again. */
    public String decodeClustered(HyperVector target, int k, double minRes, double acceptIfAbove) {
        ScoredToken best = bestOne(target);
        if (best.resonance() < minRes) return "???";
        if (best.resonance() >= acceptIfAbove) return best.token();

        List<ScoredToken> candidates = topK(target, k);
        if (candidates.isEmpty()) return "???";
        if (candidates.size() == 1) return candidates.get(0).token();

        WeightedBundler b = new WeightedBundler();
        int w = candidates.size();
        for (ScoredToken st : candidates) {
            HyperVector p = prototypes.get(st.token());
            b.add(p, w);
            w--;
        }

        HyperVector clustered = b.build();
        return decode(clustered, minRes);
    }

    private ScoredToken bestOne(HyperVector target) {
        String best = "???";
        double bestR = -1.0;
        for (Map.Entry<String, HyperVector> e : prototypes.entrySet()) {
            double r = e.getValue().resonance(target);
            if (r > bestR) {
                bestR = r;
                best = e.getKey();
            }
        }
        return new ScoredToken(best, bestR);
    }

    /** Snapshot for persistence */
    public Map<String, HyperVector> snapshot() {
        return new HashMap<>(prototypes);
    }

    /** Restore from persistence */
    public void restore(Map<String, HyperVector> snap) {
        prototypes.clear();
        prototypes.putAll(snap);
    }
}
