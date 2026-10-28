package com.eyeoverthink.lazarus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LazarusEngineTest {

    @Test
    void activeInferenceProbeProducesBeliefUpdateAndActionSelection() {
        LazarusEngine engine = new LazarusEngine();

        LazarusEngine.ActiveInferenceReport report = engine.executeActiveInferenceProbe();

        assertNotNull(report);
        assertNotNull(report.selectedAction());
        assertTrue(report.variationalFreeEnergy() >= 0.0);
        assertTrue(report.posteriorCalmBelief() >= 0.0 && report.posteriorCalmBelief() <= 1.0);
        assertTrue(report.posteriorStressBelief() >= 0.0 && report.posteriorStressBelief() <= 1.0);
        assertEquals(1.0, report.posteriorCalmBelief() + report.posteriorStressBelief(), 1e-6);
        assertTrue(report.selectedExpectedFreeEnergy() <= report.explore().expectedFreeEnergy() + 1e-9
            || report.selectedExpectedFreeEnergy() <= report.stabilize().expectedFreeEnergy() + 1e-9
            || report.selectedExpectedFreeEnergy() <= report.exploit().expectedFreeEnergy() + 1e-9);
    }
}
