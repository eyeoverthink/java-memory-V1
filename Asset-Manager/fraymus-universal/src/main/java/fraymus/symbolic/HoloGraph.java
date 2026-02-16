package fraymus.symbolic;

import fraymus.hyper.CleanupMemory;
import fraymus.hyper.HyperVector;
import fraymus.hyper.WeightedBundler;

public class HoloGraph {

    private final RelationBinder binder = new RelationBinder();
    private final CleanupMemory concepts = new CleanupMemory();
    private final WeightedBundler graphAccumulator = new WeightedBundler();
    private HyperVector graphHologram = new HyperVector(); // The Sum of All Knowledge
    private boolean dirty = false;

    // REGISTER CONCEPT (The Nouns)
    public HyperVector define(String name) {
        if (concepts.contains(name)) return concepts.prototypeOf(name);

        // Deterministic Seed based on name hash
        long seed = name.hashCode();
        HyperVector v = HyperVector.seeded(seed);
        concepts.observe(name, v);
        return v;
    }

    // LEARN FACT (The Edges)
    public void learn(String subj, String rel, String obj) {
        HyperVector s = define(subj);
        HyperVector r = define(rel);
        HyperVector o = define(obj);

        HyperVector fact = binder.encodeFact(s, r, o);

        // Add to the infinite bundle
        graphAccumulator.add(fact, 1);
        dirty = true;
        System.out.println("   [LEARNED] " + subj + " --" + rel + "--> " + obj);
    }

    // RETRIEVE FACT (The Query)
    public String ask(String subj, String rel) {
        if (dirty) {
            graphHologram = graphAccumulator.build();
            dirty = false;
        }

        HyperVector s = define(subj);
        HyperVector r = define(rel);

        // Extract the noisy answer vector
        HyperVector guess = binder.query(graphHologram, s, r);

        // Cleanup: Find the closest known concept
        return concepts.decode(guess, 0.45); // Threshold 0.45 for noise tolerance
    }
}
