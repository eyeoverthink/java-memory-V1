package com.eyeoverthink.fraymus.evolution;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class FraynixDNATest {

    @Test
    void saveLoadSeed_roundTrip() throws Exception {
        Path tmpDir = Files.createTempDirectory("fraynix_dna_test");
        Path seed = tmpDir.resolve("Fraynix_Seed.dna");

        FraynixDNA dna = new FraynixDNA();
        dna.genes.put("PhysicsEngine", "Relativistic");
        dna.generation = 7;
        dna.fitnessScore = 3.14;

        dna.saveSeed(seed);

        FraynixDNA loaded = FraynixDNA.loadSeed(seed);
        assertEquals("Relativistic", loaded.genes.get("PhysicsEngine"));
        assertEquals(7, loaded.generation);
        assertEquals(3.14, loaded.fitnessScore, 1e-9);

        // hash file exists
        assertTrue(Files.exists(Path.of(seed.toString() + ".sha256")));
    }

    @Test
    void mutate_incrementsGeneration_andChangesSomething() {
        FraynixDNA dna = new FraynixDNA();
        dna.generation = 1;

        FraynixDNA child = dna.mutate(new Random(123));

        assertEquals(2, child.generation);
        assertFalse(child.parentHash.isBlank());
        assertEquals(dna.genes.size(), child.genes.size());
    }
}
