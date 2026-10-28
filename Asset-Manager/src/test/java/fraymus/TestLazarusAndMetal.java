package fraymus;

import fraymus.metal.LazarusProtocol;
import fraymus.metal.CorticalStack;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LazarusProtocol (immortality engine) and CorticalStack (consciousness transfer).
 */
public class TestLazarusAndMetal {

    // ═══════════════════════════════════════════════════
    // LAZARUS PROTOCOL
    // ═══════════════════════════════════════════════════

    @Test
    void testLazarusSingleton() {
        LazarusProtocol l1 = LazarusProtocol.getInstance();
        LazarusProtocol l2 = LazarusProtocol.getInstance();
        assertSame(l1, l2, "LazarusProtocol should be singleton");
    }

    @Test
    void testLazarusAbsorbFragment() {
        LazarusProtocol laz = LazarusProtocol.getInstance();
        long[] vector = {0xCAFEBABEL, 0xDEADBEEFL, 0x12345678L};
        laz.absorbFragment("test_fragment", vector);
        assertTrue(laz.getSoulFragments().containsKey("test_fragment"),
            "Should contain absorbed fragment");
    }

    @Test
    void testLazarusAbsorbMetadata() {
        LazarusProtocol laz = LazarusProtocol.getInstance();
        laz.absorbMetadata("test_key", "test_value");
        assertEquals("test_value", laz.getSoulMetadata().get("test_key"),
            "Should retrieve absorbed metadata");
    }

    @Test
    void testLazarusAbsorbAccumulator() {
        LazarusProtocol laz = LazarusProtocol.getInstance();
        int[] acc = {1, 2, 3, 4, 5};
        assertDoesNotThrow(() -> laz.absorbAccumulator(acc),
            "Should absorb accumulator without error");
    }

    @Test
    void testLazarusDeathRattleArmDisarm() {
        LazarusProtocol laz = LazarusProtocol.getInstance();
        laz.armDeathRattle();
        assertTrue(laz.isDeathRattleArmed(), "Death rattle should be armed");
        laz.disarmDeathRattle();
        assertFalse(laz.isDeathRattleArmed(), "Death rattle should be disarmed");
    }

    @Test
    void testLazarusStatus() {
        LazarusProtocol laz = LazarusProtocol.getInstance();
        String status = laz.getStatus();
        assertNotNull(status, "Status should not be null");
        assertTrue(status.contains("LAZARUS"), "Status should mention LAZARUS");
    }

    @Test
    void testLazarusBootTime() {
        LazarusProtocol laz = LazarusProtocol.getInstance();
        assertTrue(laz.getBootTimeMs() >= 0, "Boot time should be non-negative");
    }

    // ═══════════════════════════════════════════════════
    // CORTICAL STACK
    // ═══════════════════════════════════════════════════

    @Test
    void testCorticalStackSingleton() {
        CorticalStack s1 = CorticalStack.getInstance();
        CorticalStack s2 = CorticalStack.getInstance();
        assertSame(s1, s2, "CorticalStack should be singleton");
    }

    @Test
    void testCorticalStackStatus() {
        CorticalStack stack = CorticalStack.getInstance();
        String status = stack.getStatus();
        assertNotNull(status, "Status should not be null");
        assertTrue(status.contains("CORTICAL"), "Status should mention CORTICAL");
    }
}
