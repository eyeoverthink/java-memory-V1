package fraymus.genomics;

import fraymus.knowledge.AkashicRecord;
import java.nio.file.*;
import java.util.*;

/** Three fresh JVMs exercise the exact oversized-file overwrite regression. */
public final class AkashicDeferredPersistenceTest {
    public static void main(String[] args) throws Exception {
        String phase = args[0];
        var memory = new AkashicRecord();
        Path base = Path.of(".akashic/blocks.dat"), proof = Path.of("base-sha.txt");
        if (phase.equals("seed")) {
            memory.addBlock("EARLIER", "retained earlier knowledge ".repeat(500)); memory.saveAll();
            Files.writeString(proof, DigitalGenomeArchive.sha(Files.readAllBytes(base)));
        } else if (phase.equals("pending")) {
            if (!memory.hasDeferredBlocks()) throw new AssertionError("Configured deferral did not occur");
            memory.addBlock("LATER", "new knowledge while the base is deferred"); memory.saveAll();
            if (!Files.readString(proof).equals(DigitalGenomeArchive.sha(Files.readAllBytes(base)))) throw new AssertionError("Deferred base checkpoint was overwritten");
            if (!Files.isRegularFile(Path.of(".akashic/blocks-pending.dat"))) throw new AssertionError("Pending evidence was not retained");
        } else {
            if (memory.hasDeferredBlocks() || memory.queryCategory("EARLIER").size() != 1 || memory.queryCategory("LATER").size() != 1)
                throw new AssertionError("A fresh full load failed to merge earlier and pending knowledge");
            if (!memory.verifyIntegrity()) throw new AssertionError("Merged knowledge chain failed integrity");
            memory.saveAll();
            if (Files.exists(Path.of(".akashic/blocks-pending.dat"))) throw new AssertionError("Committed pending overlay was not folded into the base");
        }
        System.out.println("PASS: Akashic deferred persistence phase=" + phase);
    }
}
