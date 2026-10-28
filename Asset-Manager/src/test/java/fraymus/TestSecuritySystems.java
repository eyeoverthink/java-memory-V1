package fraymus;

import fraymus.senses.BioSymbiosis;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BioSymbiosis and standalone systems wired into FraynixBoot.
 */
public class TestSecuritySystems {

    // ═══════════════════════════════════════════════════
    // BIO-SYMBIOSIS
    // ═══════════════════════════════════════════════════

    @Test
    void testBioSymbiosisInit() {
        BioSymbiosis bio = new BioSymbiosis();
        assertNotNull(bio, "BioSymbiosis should initialize");
        assertNotNull(bio.getCurrentState(), "State should not be null");
    }

    @Test
    void testBioSymbiosisCalibrate() {
        BioSymbiosis bio = new BioSymbiosis();
        bio.calibrate(72.0, 0.3);
        // After calibration, stress should be in valid range
        assertTrue(bio.getStressLevel() >= 0 && bio.getStressLevel() <= 1.0,
            "Stress should be [0,1] after calibration");
    }

    @Test
    void testBioSymbiosisSyncPulse() {
        BioSymbiosis bio = new BioSymbiosis();
        bio.calibrate(72.0, 0.3);
        bio.syncPulse(80.0, 0.4);
        bio.syncPulse(85.0, 0.5);
        assertTrue(bio.getCoherence() >= 0, "Coherence should be non-negative after pulses");
    }

    @Test
    void testBioSymbiosisGetStatus() {
        BioSymbiosis bio = new BioSymbiosis();
        String status = bio.getStatus();
        assertNotNull(status, "Status should not be null");
        assertTrue(status.contains("BIO-SYMBIOSIS"), "Status should identify itself");
    }

    @Test
    void testBioSymbiosisMeshTension() {
        BioSymbiosis bio = new BioSymbiosis();
        double tension = bio.getMeshTension();
        assertTrue(tension > 0, "Mesh tension should be positive");
    }

    @Test
    void testBioSymbiosisMeshDistortion() {
        BioSymbiosis bio = new BioSymbiosis();
        double distortion = bio.getMeshDistortion(System.currentTimeMillis());
        assertTrue(distortion >= -1.0 && distortion <= 1.0,
            "Distortion should be [-1, 1], got " + distortion);
    }
}
