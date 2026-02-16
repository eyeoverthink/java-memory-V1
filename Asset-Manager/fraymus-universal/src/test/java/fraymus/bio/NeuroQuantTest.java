package fraymus.bio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NeuroQuantTest {

    @Test
    void deterministic_sameConcept_sameIdAndBits() {
        NeuroQuant a = new NeuroQuant("Quantum Encryption");
        NeuroQuant b = new NeuroQuant("Quantum Encryption");

        assertEquals(a.id, b.id);
        assertArrayEquals(a.bitsCopy(), b.bitsCopy());
        assertEquals(0, a.hammingDistance(b));
        assertEquals(1.0, a.resonance(b), 1e-9);
    }

    @Test
    void differentConcept_differentSignature() {
        NeuroQuant a = new NeuroQuant("Quantum Encryption");
        NeuroQuant b = new NeuroQuant("Classical Plumbing");

        assertNotEquals(a.id, b.id);
        assertTrue(a.hammingDistance(b) > 0);
        assertTrue(a.resonance(b) < 1.0);
    }
}
