package brain_architecture.integration;

import brain_architecture.synapses.HyperdimensionalMapper;
import brain_architecture.synapses.HDCSimilarityEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Test suite for HDCIntegrator
 * Tests integration with Fraymus HyperFormer instead of duplicate HDC
 */
public class HDCIntegratorTest {
    
    private HDCIntegrator integrator;
    private HyperdimensionalMapper brainMapper;
    private HDCSimilarityEngine brainSimilarityEngine;
    
    @BeforeEach
    public void setUp() {
        integrator = new HDCIntegrator();
        brainMapper = new HyperdimensionalMapper();
        brainSimilarityEngine = new HDCSimilarityEngine();
        
        // Wire brain_architecture components for fallback functionality
        integrator.wireFraymusHDC(null, null, null, brainMapper, brainSimilarityEngine);
        // Unwire to set useFraymusHDC to false (but keep brain components)
        integrator.unwireFraymusHDC();
    }
    
    @Test
    public void testInitialization() {
        // After setUp, brain components are wired but useFraymusHDC is false
        assertFalse(integrator.isInitialized());
        assertFalse(integrator.isUsingFraymusHDC());
        assertNotNull(integrator.getStatus());
    }
    
    @Test
    public void testWireFraymusHDC() {
        integrator.wireFraymusHDC(
            new MockHyperFormer(), new Object(), null, brainMapper, brainSimilarityEngine);

        assertTrue(integrator.isInitialized());
        assertTrue(integrator.isUsingFraymusHDC());
    }
    
    @Test
    public void testUnwireFraymusHDC() {
        // Wire first
        integrator.wireFraymusHDC(
            new MockHyperFormer(), new Object(), null, brainMapper, brainSimilarityEngine);

        assertTrue(integrator.isInitialized());

        // Unwire
        integrator.unwireFraymusHDC();

        assertFalse(integrator.isInitialized());
        assertFalse(integrator.isUsingFraymusHDC());
    }
    
    @Test
    public void testMapConceptWithoutFraymus() {
        // Without Fraymus HDC, should use brain_architecture HDC
        Object vector = integrator.mapConcept("test");
        assertNotNull(vector);
    }
    
    @Test
    public void testMapConceptWithFraymus() {
        integrator.wireFraymusHDC(
            new MockHyperFormer(), new Object(), null, brainMapper, brainSimilarityEngine);

        Object vector = integrator.mapConcept("test");
        assertNotNull(vector);
    }
    
    @Test
    public void testCalculateSimilarityWithoutFraymus() {
        // Without Fraymus HDC, should use brain_architecture HDC
        double similarity = integrator.calculateSimilarity("concept1", "concept2");
        assertTrue(similarity >= 0.0 && similarity <= 1.0);
    }
    
    @Test
    public void testCalculateSimilarityWithFraymus() {
        integrator.wireFraymusHDC(
            new MockHyperFormer(), new Object(), null, brainMapper, brainSimilarityEngine);

        double similarity = integrator.calculateSimilarity("concept1", "concept2");
        assertTrue(similarity >= 0.0 && similarity <= 1.0);
    }
    
    @Test
    public void testBindConceptsWithoutFraymus() {
        // Without Fraymus HDC, should use brain_architecture HDC
        Object bound = integrator.bindConcepts("concept1", "concept2");
        assertNotNull(bound);
    }
    
    @Test
    public void testBindConceptsWithFraymus() {
        integrator.wireFraymusHDC(
            new MockHyperFormer(), new Object(), null, brainMapper, brainSimilarityEngine);

        Object bound = integrator.bindConcepts("concept1", "concept2");
        assertNotNull(bound);
    }
    
    @Test
    public void testBundleConceptsWithoutFraymus() {
        // Without Fraymus HDC, should use brain_architecture HDC
        java.util.List<String> concepts = java.util.Arrays.asList("a", "b", "c");
        Object bundled = integrator.bundleConcepts(concepts);
        assertNotNull(bundled);
    }
    
    @Test
    public void testBundleConceptsWithFraymus() {
        integrator.wireFraymusHDC(
            new MockHyperFormer(), new Object(), null, brainMapper, brainSimilarityEngine);

        java.util.List<String> concepts = java.util.Arrays.asList("a", "b", "c");
        Object bundled = integrator.bundleConcepts(concepts);
        assertNotNull(bundled);
    }
    
    @Test
    public void testConvertBrainVectorToFraymus() {
        Object brainVector = brainMapper.map("test");
        Object fraymusVector = integrator.convertBrainVectorToFraymus(brainVector);
        assertNotNull(fraymusVector);
    }
    
    @Test
    public void testConvertFraymusVectorToBrain() {
        Object fraymusVector = "FraymusVector_test";
        Object brainVector = integrator.convertFraymusVectorToBrain(fraymusVector);
        assertNotNull(brainVector);
    }
    
    @Test
    public void testGetStatus() {
        // Status before wiring
        Map<String, Object> status = integrator.getStatus();
        assertFalse((Boolean) status.get("initialized"));
        assertFalse((Boolean) status.get("useFraymusHDC"));

        // Status after wiring
        integrator.wireFraymusHDC(
            new MockHyperFormer(), new Object(), null, brainMapper, brainSimilarityEngine);

        status = integrator.getStatus();
        assertTrue((Boolean) status.get("initialized"));
        assertTrue((Boolean) status.get("useFraymusHDC"));
        assertNotNull(status.get("conversionCacheSize"));
        assertNotNull(status.get("hyperFormerAvailable"));
        assertNotNull(status.get("holoGraphAvailable"));
        assertNotNull(status.get("weightedBundlerAvailable"));
    }

    // Mock classes for reflection-based testing

    /**
     * Mock HyperVector class with methods expected by reflection
     */
    private static class MockHyperVector {
        private final String concept;
        private int bits = 10000; // 10,000-D vectors

        public MockHyperVector(String concept) {
            this.concept = concept;
        }

        public int cardinality() {
            return bits;
        }

        public MockHyperVector xor(MockHyperVector other) {
            // Return a new mock vector representing XOR result
            return new MockHyperVector(this.concept + "_xor_" + other.concept);
        }
    }

    /**
     * Mock HyperFormer class with embed method expected by reflection
     */
    private static class MockHyperFormer {
        private final Map<String, MockHyperVector> cache = new HashMap<>();

        public MockHyperVector embed(String token) {
            return cache.computeIfAbsent(token, MockHyperVector::new);
        }
    }
}
