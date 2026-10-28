package brain_architecture.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Test suite for SentientCoreIntegrator
 * Tests integration with real SentientCore instead of stub
 */
public class SentientCoreIntegratorTest {
    
    private SentientCoreIntegrator integrator;
    
    @BeforeEach
    public void setUp() {
        integrator = new SentientCoreIntegrator();
    }
    
    @Test
    public void testInitialization() {
        assertFalse(integrator.isInitialized());
        assertFalse(integrator.isUsingRealSentientCore());
        assertNotNull(integrator.getStatus());
    }
    
    @Test
    public void testWireSentientCore() {
        integrator.wireSentientCore(
            new MockCausalGraph(), new MockSufficientStats(), new MockFisherOracle(),
            new MockAnalogyEngine(), new MockConceptLattice(), new MockPhysicsEncoder());

        assertTrue(integrator.isInitialized());
        assertTrue(integrator.isUsingRealSentientCore());
    }
    
    @Test
    public void testUnwireSentientCore() {
        // Wire first
        integrator.wireSentientCore(
            new MockCausalGraph(), new MockSufficientStats(), new MockFisherOracle(),
            new MockAnalogyEngine(), new MockConceptLattice(), new MockPhysicsEncoder());

        assertTrue(integrator.isInitialized());

        // Unwire
        integrator.unwireSentientCore();

        assertFalse(integrator.isInitialized());
        assertFalse(integrator.isUsingRealSentientCore());
    }
    
    @Test
    public void testWireSentientCoreEnhancements() {
        integrator.wireSentientCoreEnhancements(
            new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(),
            new Object(), new Object());
        
        // Should not affect initialization status (requires core wire first)
        assertFalse(integrator.isInitialized());
    }
    
    @Test
    public void testUnwireSentientCoreEnhancements() {
        integrator.wireSentientCoreEnhancements(
            new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object(),
            new Object(), new Object());
        
        integrator.unwireSentientCoreEnhancements();
        
        // Should not affect initialization status
        assertFalse(integrator.isInitialized());
    }
    
    @Test
    public void testObserveCausalWithRealSentientCore() {
        integrator.wireSentientCore(
            new MockCausalGraph(), new MockSufficientStats(), new MockFisherOracle(),
            new MockAnalogyEngine(), new MockConceptLattice(), new MockPhysicsEncoder());

        // Should not throw exception (reflection implementation)
        integrator.observeCausal("cause", "effect");
    }
    
    @Test
    public void testObserveCausalWithoutRealSentientCore() {
        // Should not throw exception (fallback to stub)
        integrator.observeCausal("cause", "effect");
    }
    
    @Test
    public void testGetSufficientStatsWithRealSentientCore() {
        integrator.wireSentientCore(
            new MockCausalGraph(), new MockSufficientStats(), new MockFisherOracle(),
            new MockAnalogyEngine(), new MockConceptLattice(), new MockPhysicsEncoder());

        Map<String, Double> stats = integrator.getSufficientStats("concept");
        assertNotNull(stats);
        assertTrue(stats.containsKey("count"));
        assertTrue(stats.containsKey("sum"));
        assertTrue(stats.containsKey("sum_of_squares"));
        assertTrue(stats.containsKey("min"));
        assertTrue(stats.containsKey("max"));
    }
    
    @Test
    public void testGetSufficientStatsWithoutRealSentientCore() {
        // Should fallback to stub
        Map<String, Double> stats = integrator.getSufficientStats("concept");
        assertNotNull(stats);
        assertEquals(0.0, stats.get("count"));
    }
    
    @Test
    public void testGetFisherInformationWithRealSentientCore() {
        integrator.wireSentientCore(
            new MockCausalGraph(), new MockSufficientStats(), new MockFisherOracle(),
            new MockAnalogyEngine(), new MockConceptLattice(), new MockPhysicsEncoder());

        String info = integrator.getFisherInformation("context");
        assertNotNull(info);
        assertTrue(info.contains("Fisher Information"));
    }
    
    @Test
    public void testGetFisherInformationWithoutRealSentientCore() {
        // Should fallback to stub
        String info = integrator.getFisherInformation("context");
        assertNotNull(info);
        assertTrue(info.contains("STUB"));
    }
    
    @Test
    public void testFindAnalogiesWithRealSentientCore() {
        integrator.wireSentientCore(
            new MockCausalGraph(), new MockSufficientStats(), new MockFisherOracle(),
            new MockAnalogyEngine(), new MockConceptLattice(), new MockPhysicsEncoder());

        List<String> analogies = integrator.findAnalogies("pattern");
        assertNotNull(analogies);
        assertFalse(analogies.isEmpty());
    }
    
    @Test
    public void testFindAnalogiesWithoutRealSentientCore() {
        // Should fallback to stub (empty list)
        List<String> analogies = integrator.findAnalogies("pattern");
        assertNotNull(analogies);
        assertTrue(analogies.isEmpty());
    }
    
    @Test
    public void testGetConceptLatticeWithRealSentientCore() {
        integrator.wireSentientCore(
            new MockCausalGraph(), new MockSufficientStats(), new MockFisherOracle(),
            new MockAnalogyEngine(), new MockConceptLattice(), new MockPhysicsEncoder());

        Set<String> objects = new java.util.HashSet<>(java.util.Arrays.asList("a", "b", "c"));
        Set<String> attributes = new java.util.HashSet<>(java.util.Arrays.asList("x", "y", "z"));

        Set<String> lattice = integrator.getConceptLattice(objects, attributes);
        assertNotNull(lattice);
        assertFalse(lattice.isEmpty());
    }
    
    @Test
    public void testGetConceptLatticeWithoutRealSentientCore() {
        // Should fallback to stub (empty set)
        Set<String> objects = new java.util.HashSet<>(java.util.Arrays.asList("a", "b", "c"));
        Set<String> attributes = new java.util.HashSet<>(java.util.Arrays.asList("x", "y", "z"));
        
        Set<String> lattice = integrator.getConceptLattice(objects, attributes);
        assertNotNull(lattice);
        assertTrue(lattice.isEmpty());
    }
    
    @Test
    public void testGetStatus() {
        // Status before wiring
        Map<String, Object> status = integrator.getStatus();
        assertFalse((Boolean) status.get("initialized"));
        assertFalse((Boolean) status.get("useRealSentientCore"));

        // Status after wiring
        integrator.wireSentientCore(
            new MockCausalGraph(), new MockSufficientStats(), new MockFisherOracle(),
            new MockAnalogyEngine(), new MockConceptLattice(), new MockPhysicsEncoder());

        status = integrator.getStatus();
        assertTrue((Boolean) status.get("initialized"));
        assertTrue((Boolean) status.get("useRealSentientCore"));
        assertTrue((Boolean) status.get("causalGraphAvailable"));
        assertTrue((Boolean) status.get("sufficientStatsAvailable"));
        assertTrue((Boolean) status.get("fisherOracleAvailable"));
        assertTrue((Boolean) status.get("analogyEngineAvailable"));
        assertTrue((Boolean) status.get("conceptLatticeAvailable"));
        assertTrue((Boolean) status.get("physicsEncoderAvailable"));
    }

    // Mock classes for reflection-based testing

    /**
     * Mock CausalGraph with observe method
     */
    private static class MockCausalGraph {
        public void observe(String cause, String effect) {
            // Mock implementation
        }
    }

    /**
     * Mock SufficientStats with getter methods
     */
    private static class MockSufficientStats {
        private final Map<String, Integer> counts = new HashMap<>();
        private final Map<String, Double> sums = new HashMap<>();
        private final Map<String, Double> sumSquares = new HashMap<>();
        private final Map<String, Double> mins = new HashMap<>();
        private final Map<String, Double> maxs = new HashMap<>();

        public int getCount(String concept) {
            return counts.getOrDefault(concept, 0);
        }

        public double getSum(String concept) {
            return sums.getOrDefault(concept, 0.0);
        }

        public double getSumSquares(String concept) {
            return sumSquares.getOrDefault(concept, 0.0);
        }

        public double getMin(String concept) {
            return mins.getOrDefault(concept, 0.0);
        }

        public double getMax(String concept) {
            return maxs.getOrDefault(concept, 0.0);
        }
    }

    /**
     * Mock FisherOracle with query method
     */
    private static class MockFisherOracle {
        public String query(String context) {
            return "Fisher Information for " + context;
        }
    }

    /**
     * Mock AnalogyEngine with findAnalogies method
     */
    private static class MockAnalogyEngine {
        public List<String> findAnalogies(String pattern) {
            return Arrays.asList("analogy1", "analogy2", "analogy3");
        }
    }

    /**
     * Mock ConceptLattice with getConcepts method
     */
    private static class MockConceptLattice {
        public Set<String> getConcepts(Set<String> objects, Set<String> attributes) {
            return new HashSet<>(Arrays.asList("concept1", "concept2", "concept3"));
        }
    }

    /**
     * Mock PhysicsEncoder (placeholder)
     */
    private static class MockPhysicsEncoder {
        // Placeholder for future implementation
    }
}
