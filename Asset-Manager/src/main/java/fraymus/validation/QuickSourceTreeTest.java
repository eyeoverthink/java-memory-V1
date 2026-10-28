package fraymus.validation;

import fraymus.neural.LibraryAbsorber;
import fraymus.knowledge.AkashicRecord;

/**
 * Quick standalone test for absorbSourceTree() — no full Fraynix boot needed.
 */
public class QuickSourceTreeTest {
    public static void main(String[] args) {
        System.out.println("=== QUICK SOURCE TREE ABSORPTION TEST ===\n");

        AkashicRecord akashic = new AkashicRecord();
        int before = akashic.getPersistedBlockCount();
        System.out.println("Akashic blocks before: " + before);

        LibraryAbsorber absorber = new LibraryAbsorber();
        absorber.wireAkashic(akashic);

        // Pillar 0: python4j (small — 21 files, proves pipeline)
        System.out.println("\n--- PILLAR 0: python4j (The Bridge) ---");
        String r0 = absorber.absorbJar("knowledge_base/deeplearning4j-master/deeplearning4j-master/python4j");
        System.out.println(r0);

        // Pillar 1: nd4j (The Muscle — tensor engine)
        System.out.println("\n--- PILLAR 1: ND4J (The Muscle) ---");
        String r1 = absorber.absorbJar("knowledge_base/deeplearning4j-master/deeplearning4j-master/nd4j");
        System.out.println(r1);

        // Pillar 2: datavec (The Stomach — ETL)
        System.out.println("\n--- PILLAR 2: DataVec (The Stomach) ---");
        String r2 = absorber.absorbJar("knowledge_base/deeplearning4j-master/deeplearning4j-master/datavec");
        System.out.println(r2);

        // Pillar 3: deeplearning4j (The Brain — neural network API)
        System.out.println("\n--- PILLAR 3: DL4J Core (The Brain) ---");
        String r3 = absorber.absorbJar("knowledge_base/deeplearning4j-master/deeplearning4j-master/deeplearning4j");
        System.out.println(r3);

        // Query test
        System.out.println("\n--- QUERY TESTS ---");
        String[] queries = {"INDArray", "MultiLayerNetwork", "RecordReader", "PythonObject", "fit", "transform"};
        int hits = 0;
        for (String q : queries) {
            String result = absorber.query(q);
            boolean found = !result.startsWith("No results");
            if (found) hits++;
            System.out.println("  " + q + " → " + (found ? "✓ FOUND" : "✗ MISS"));
        }
        System.out.printf("\nQuery accuracy: %d / %d (%.0f%%)\n", hits, queries.length,
            100.0 * hits / queries.length);

        // Status
        System.out.println("\n--- ABSORBER STATUS ---");
        System.out.println(absorber.getStatus());

        int after = akashic.getPersistedBlockCount();
        System.out.println("Akashic blocks after: " + after + " (+" + (after - before) + " new)");
        System.out.println("\n=== TEST COMPLETE ===");
    }
}
