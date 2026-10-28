package fraymus.genomics;

import java.nio.file.*;
import java.util.*;

/** Standalone checks of lossless state, concept-vector versions and corruption rejection. */
public final class DigitalGenomeArchiveTest {
    public static void main(String[] args) throws Exception {
        byte[] all = new byte[256]; for (int i = 0; i < all.length; i++) all[i] = (byte) i;
        if (!Arrays.equals(all, DigitalGenomeArchive.decode(DigitalGenomeArchive.encode(all)))) throw new AssertionError("Byte codec lost data");
        Path root = Files.createTempDirectory("fraynix-digital-genome-");
        Map<String, Object> payload = Map.of("identity", "Fraynix \u03c6", "genes", Map.of("model", "Carbon-3B"));
        var first = DigitalGenomeArchive.save(root, "system-profile", payload);
        var again = DigitalGenomeArchive.save(root, "system-profile", payload);
        if (!first.equals(again)) throw new AssertionError("Identical snapshot was not deduplicated");
        var restored = DigitalGenomeArchive.readPayload(Path.of((String) first.get("manifest")));
        if (!restored.equals(payload)) throw new AssertionError("Profile did not round-trip");
        var second = DigitalGenomeArchive.save(root, "system-profile", Map.of("identity", "new generation"));
        var manifest = DigitalGenomeArchive.readMap(Path.of((String) second.get("manifest")));
        if (!manifest.get("parent_manifest_sha256").equals(first.get("manifest_sha256"))) throw new AssertionError("Parent lineage missing");
        long[] vector = new long[256];
        Random random = new Random(42); for (int i = 0; i < vector.length; i++) vector[i] = random.nextLong();
        String sequence = sequence(vector);
        var concept = DigitalGenomeArchive.archiveConcept(root, "SAVED_VECTOR", vector, sequence);
        vector[0] ^= 1L;
        var changed = DigitalGenomeArchive.archiveConcept(root, "SAVED_VECTOR", vector, sequence(vector));
        if (changed.get("manifest").equals(concept.get("manifest"))) throw new AssertionError("Changed vector overwrote its parent");
        long[] oldVector = DigitalGenomeArchive.conceptVector(DigitalGenomeArchive.readPayload(Path.of((String) concept.get("manifest"))));
        if (!sequence(oldVector).equals(sequence)) throw new AssertionError("Original concept vector lost");
        try { DigitalGenomeArchive.archiveConcept(root, "BAD", vector, sequence); throw new AssertionError("Mismatched vector accepted"); }
        catch (java.io.IOException expected) { }
        Path dna = Path.of((String) second.get("fasta")); Files.writeString(dna, ">tampered\nAAAA\n");
        try { DigitalGenomeArchive.readPayload(Path.of((String) second.get("manifest"))); throw new AssertionError("Corruption accepted"); }
        catch (java.io.IOException expected) { }
        System.out.println("DigitalGenomeArchiveTest passed: all bytes, Unicode profile, dedupe, parent lineage, exact vector restoration and corruption rejection");
    }

    private static String sequence(long[] vector) {
        StringBuilder dna = new StringBuilder();
        for (long word : vector) for (int bit = 0; bit < 64; bit += 2) dna.append("ACGT".charAt((int) ((word >>> bit) & 3)));
        return dna.toString();
    }
}
