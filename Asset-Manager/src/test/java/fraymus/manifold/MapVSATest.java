package fraymus.manifold;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for MapVSA - including Continuous Normalizing Flows
 */
public class MapVSATest {
    
    @Test
    public void testContinuousBind() {
        MapVSA vsa = new MapVSA();
        
        byte[] a = vsa.seed("concept_a");
        byte[] b = vsa.seed("concept_b");
        
        // Test continuous binding
        byte[] continuousResult = vsa.continuousBind(a, b);
        
        assertNotNull(continuousResult);
        assertEquals(MapVSA.D, continuousResult.length);
        assertTrue(vsa.getContinuousBindCount() > 0);
    }
    
    @Test
    public void testContinuousBundle() {
        MapVSA vsa = new MapVSA();
        
        byte[] a = vsa.seed("concept_a");
        byte[] b = vsa.seed("concept_b");
        byte[] c = vsa.seed("concept_c");
        
        // Test continuous bundling
        byte[] continuousResult = vsa.continuousBundle(a, b, c);
        
        assertNotNull(continuousResult);
        assertEquals(MapVSA.D, continuousResult.length);
        assertTrue(vsa.getContinuousBundleCount() > 0);
    }
    
    @Test
    public void testContinuousBindProducesValidBipolar() {
        MapVSA vsa = new MapVSA();
        
        byte[] a = vsa.seed("concept_a");
        byte[] b = vsa.seed("concept_b");
        
        byte[] continuousResult = vsa.continuousBind(a, b);
        
        // Verify all values are bipolar {-1, 1}
        for (byte val : continuousResult) {
            assertTrue(val == 1 || val == -1, 
                "Continuous bind should produce bipolar values, got: " + val);
        }
    }
    
    @Test
    public void testContinuousBundleProducesValidBipolar() {
        MapVSA vsa = new MapVSA();
        
        byte[] a = vsa.seed("concept_a");
        byte[] b = vsa.seed("concept_b");
        byte[] c = vsa.seed("concept_c");
        
        byte[] continuousResult = vsa.continuousBundle(a, b, c);
        
        // Verify all values are bipolar {-1, 1}
        for (byte val : continuousResult) {
            assertTrue(val == 1 || val == -1,
                "Continuous bundle should produce bipolar values, got: " + val);
        }
    }
    
    @Test
    public void testBindingGradient() {
        MapVSA vsa = new MapVSA();
        
        byte[] a = vsa.seed("concept_a");
        byte[] b = vsa.seed("concept_b");
        
        // Get gradient at t=0
        double[] gradient = vsa.getBindingGradient(a, b, 0.0);
        
        assertNotNull(gradient);
        assertEquals(MapVSA.D, gradient.length);
    }
    
    @Test
    public void testContinuousVsDiscreteBind() {
        MapVSA vsa = new MapVSA();
        
        byte[] a = vsa.seed("concept_a");
        byte[] b = vsa.seed("concept_b");
        
        // Discrete binding
        byte[] discreteResult = vsa.bind(a, b);
        
        // Continuous binding
        byte[] continuousResult = vsa.continuousBind(a, b);
        
        // Both should be valid bipolar vectors
        assertEquals(MapVSA.D, discreteResult.length);
        assertEquals(MapVSA.D, continuousResult.length);
        
        // They might be different due to continuous flow
        // but both should be valid
        for (byte val : discreteResult) {
            assertTrue(val == 1 || val == -1);
        }
        for (byte val : continuousResult) {
            assertTrue(val == 1 || val == -1);
        }
    }
    
    @Test
    public void testStatusIncludesContinuousOperations() {
        MapVSA vsa = new MapVSA();
        
        // Perform some continuous operations
        byte[] a = vsa.seed("concept_a");
        byte[] b = vsa.seed("concept_b");
        vsa.continuousBind(a, b);
        vsa.continuousBundle(a, b);
        
        String status = vsa.getStatus();
        
        assertNotNull(status);
        assertTrue(status.contains("continuousBinds"));
        assertTrue(status.contains("continuousBundles"));
    }
    
    @Test
    public void testContinuousOperationCounters() {
        MapVSA vsa = new MapVSA();
        
        long initialBindCount = vsa.getContinuousBindCount();
        long initialBundleCount = vsa.getContinuousBundleCount();
        
        byte[] a = vsa.seed("concept_a");
        byte[] b = vsa.seed("concept_b");
        
        vsa.continuousBind(a, b);
        assertEquals(initialBindCount + 1, vsa.getContinuousBindCount());
        
        vsa.continuousBundle(a, b);
        assertEquals(initialBundleCount + 1, vsa.getContinuousBundleCount());
    }
}
