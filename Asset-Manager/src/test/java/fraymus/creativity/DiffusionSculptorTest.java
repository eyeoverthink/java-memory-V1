package fraymus.creativity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for DiffusionSculptor - Stochastic Differential Equations for reality generation
 */
public class DiffusionSculptorTest {
    
    @Test
    public void testDiffusionSculptorCreation() {
        DiffusionSculptor sculptor = new DiffusionSculptor();
        
        assertNotNull(sculptor);
        assertEquals(16384, sculptor.getDimension());
        assertEquals(1.0, sculptor.getT(), 0.001);
    }
    
    @Test
    public void testGenerateNoise() {
        DiffusionSculptor sculptor = new DiffusionSculptor();
        
        double[] noise = sculptor.generateNoise();
        
        assertNotNull(noise);
        assertEquals(16384, noise.length);
        
        // Verify it's actually random (not all zeros)
        boolean hasNonZero = false;
        for (double val : noise) {
            if (val != 0.0) {
                hasNonZero = true;
                break;
            }
        }
        assertTrue(hasNonZero, "Generated noise should contain non-zero values");
    }
    
    @Test
    public void testForwardSDE() {
        DiffusionSculptor sculptor = new DiffusionSculptor(100, 1.0, 0.1); // Smaller dimension for testing
        
        double[] x0 = new double[100];
        for (int i = 0; i < 100; i++) {
            x0[i] = 1.0; // Start with all ones
        }
        
        double[] xT = sculptor.forwardSDE(x0);
        
        assertNotNull(xT);
        assertEquals(100, xT.length);
        assertTrue(sculptor.getForwardSteps() > 0);
        
        // After forward diffusion, values should be noisy (not all ones)
        boolean hasNoise = false;
        for (double val : xT) {
            if (val != 1.0) {
                hasNoise = true;
                break;
            }
        }
        assertTrue(hasNoise, "Forward SDE should add noise to the data");
    }
    
    @Test
    public void testReverseSDE() {
        DiffusionSculptor sculptor = new DiffusionSculptor(100, 1.0, 0.1);
        
        double[] xT = sculptor.generateNoise();
        double[] target = new double[100];
        for (int i = 0; i < 100; i++) {
            target[i] = 1.0; // Target is all ones
        }
        
        double[] x0 = sculptor.reverseSDE(xT, target);
        
        assertNotNull(x0);
        assertEquals(100, x0.length);
        assertTrue(sculptor.getReverseSteps() > 0);
    }
    
    @Test
    public void testScoreFunction() {
        DiffusionSculptor sculptor = new DiffusionSculptor(100, 1.0, 0.1);
        
        double[] x = new double[100];
        double[] target = new double[100];
        for (int i = 0; i < 100; i++) {
            x[i] = 0.0;
            target[i] = 1.0;
        }
        
        double[] score = sculptor.scoreFunction(x, 0.5, target);
        
        assertNotNull(score);
        assertEquals(100, score.length);
        
        // Score should point toward target (positive values)
        for (double val : score) {
            assertTrue(val >= 0, "Score should point toward target");
        }
    }
    
    @Test
    public void testSculptCycle() {
        DiffusionSculptor sculptor = new DiffusionSculptor(100, 1.0, 0.1);
        
        double[] data = new double[100];
        double[] target = new double[100];
        for (int i = 0; i < 100; i++) {
            data[i] = 0.5;
            target[i] = 1.0;
        }
        
        double[] regenerated = sculptor.sculpt(data, target);
        
        assertNotNull(regenerated);
        assertEquals(100, regenerated.length);
        assertTrue(sculptor.getForwardSteps() > 0);
        assertTrue(sculptor.getReverseSteps() > 0);
    }
    
    @Test
    public void testToBipolar() {
        DiffusionSculptor sculptor = new DiffusionSculptor();
        
        double[] x = new double[16384];
        for (int i = 0; i < 16384; i++) {
            x[i] = Math.random() * 2 - 1; // Random values in [-1, 1]
        }
        
        byte[] bipolar = sculptor.toBipolar(x);
        
        assertNotNull(bipolar);
        assertEquals(16384, bipolar.length);
        
        // Verify all values are bipolar {-1, 1}
        for (byte val : bipolar) {
            assertTrue(val == 1 || val == -1,
                "toBipolar should produce bipolar values, got: " + val);
        }
    }
    
    @Test
    public void testFromBipolar() {
        DiffusionSculptor sculptor = new DiffusionSculptor();
        
        byte[] bipolar = new byte[16384];
        for (int i = 0; i < 16384; i++) {
            bipolar[i] = (i % 2 == 0) ? (byte) 1 : (byte) -1;
        }
        
        double[] x = sculptor.fromBipolar(bipolar);
        
        assertNotNull(x);
        assertEquals(16384, x.length);
        
        // Verify conversion preserves values
        for (int i = 0; i < 16384; i++) {
            assertEquals(bipolar[i], (byte) x[i], 0.001);
        }
    }
    
    @Test
    public void testGetStats() {
        DiffusionSculptor sculptor = new DiffusionSculptor();
        
        String stats = sculptor.getStats();
        
        assertNotNull(stats);
        assertTrue(stats.contains("DiffusionSculptor"));
        assertTrue(stats.contains("Dim=16384"));
    }
    
    @Test
    public void testResetStats() {
        DiffusionSculptor sculptor = new DiffusionSculptor(100, 1.0, 0.1);
        
        // Perform some operations
        double[] x0 = new double[100];
        sculptor.forwardSDE(x0);
        
        assertTrue(sculptor.getForwardSteps() > 0);
        
        sculptor.resetStats();
        
        assertEquals(0, sculptor.getForwardSteps());
        assertEquals(0, sculptor.getReverseSteps());
    }
    
    @Test
    public void testBetaSchedule() {
        DiffusionSculptor sculptor = new DiffusionSculptor(100, 1.0, 0.1);
        
        // Test beta at different times
        double beta0 = sculptor.beta(0.0);
        double betaT = sculptor.beta(1.0);
        
        // Beta should increase with time
        assertTrue(betaT > beta0, "Beta should increase over time");
    }
    
    @Test
    public void testGFunction() {
        DiffusionSculptor sculptor = new DiffusionSculptor(100, 1.0, 0.1);
        
        double g0 = sculptor.g(0.0);
        double gT = sculptor.g(1.0);
        
        // g(t) = sqrt(beta(t)), so should also increase
        assertTrue(gT > g0, "g(t) should increase over time");
    }
}
