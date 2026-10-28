package fraymus.brain.unified;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Test suite for Sensory Expansion components
 * Tests OccipitalLobeBrain, TemporalLobeBrain, and ParietalLobeBrain
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SensoryExpansionTest {
    
    private static OccipitalLobeBrain occipitalLobe;
    private static TemporalLobeBrain temporalLobe;
    private static ParietalLobeBrain parietalLobe;
    
    @BeforeAll
    static void setUp() {
        occipitalLobe = new OccipitalLobeBrain();
        temporalLobe = new TemporalLobeBrain();
        parietalLobe = new ParietalLobeBrain();
    }
    
    // ==================== OccipitalLobeBrain Tests ====================
    
    @Test
    @Order(1)
    public void testOccipitalLobeInitialization() {
        assertNotNull(occipitalLobe);
        assertEquals("OccipitalLobe", occipitalLobe.getName());
        assertEquals("Vision", occipitalLobe.getType());
    }
    
    @Test
    @Order(2)
    public void testOccipitalLobeWire() {
        occipitalLobe.wire();
        Map<String, Object> stats = occipitalLobe.getVisionStats();
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalImagesProcessed"));
        assertTrue(stats.containsKey("analysisAccuracy"));
    }
    
    @Test
    @Order(3)
    public void testOccipitalLobeStats() {
        occipitalLobe.wire();
        occipitalLobe.resetStats();
        
        Map<String, Object> stats = occipitalLobe.getVisionStats();
        assertNotNull(stats);
        assertEquals(0, stats.get("totalImagesProcessed"));
        assertEquals(0, stats.get("successfulAnalysis"));
    }
    
    @Test
    @Order(4)
    public void testOccipitalLobeUnwire() {
        occipitalLobe.wire();
        occipitalLobe.unwire();
        
        BrainQuery query = new BrainQuery(
            "Test visual input",
            java.util.Collections.emptyList(),
            new java.util.HashMap<>(),
            1.0
        );
        
        BrainResponse response = occipitalLobe.process(query);
        assertFalse(response.isSuccess());
    }
    
    // ==================== TemporalLobeBrain Tests ====================
    
    @Test
    @Order(5)
    public void testTemporalLobeInitialization() {
        assertNotNull(temporalLobe);
        assertEquals("TemporalLobe", temporalLobe.getName());
        assertEquals("Temporal", temporalLobe.getType());
    }
    
    @Test
    @Order(6)
    public void testTemporalLobeWire() {
        temporalLobe.wire();
        Map<String, Object> stats = temporalLobe.getTemporalStats();
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalSeriesProcessed"));
        assertTrue(stats.containsKey("forecastAccuracy"));
    }
    
    @Test
    @Order(7)
    public void testTemporalLobeStats() {
        temporalLobe.wire();
        temporalLobe.resetStats();
        
        Map<String, Object> stats = temporalLobe.getTemporalStats();
        assertNotNull(stats);
        assertEquals(0, stats.get("totalSeriesProcessed"));
        assertEquals(0, stats.get("successfulForecasts"));
    }
    
    @Test
    @Order(8)
    public void testTemporalLobeAddTimeSeriesData() {
        temporalLobe.wire();
        double[] testData = {1.0, 2.0, 3.0, 4.0, 5.0};
        temporalLobe.addTimeSeriesData(testData);
        
        Map<String, Object> stats = temporalLobe.getTemporalStats();
        assertEquals(1, stats.get("historySize"));
    }
    
    @Test
    @Order(9)
    public void testTemporalLobeUnwire() {
        temporalLobe.wire();
        temporalLobe.unwire();
        
        BrainQuery query = new BrainQuery(
            "Test temporal data",
            java.util.Collections.emptyList(),
            new java.util.HashMap<>(),
            1.0
        );
        
        BrainResponse response = temporalLobe.process(query);
        assertFalse(response.isSuccess());
    }
    
    // ==================== ParietalLobeBrain Tests ====================
    
    @Test
    @Order(10)
    public void testParietalLobeInitialization() {
        assertNotNull(parietalLobe);
        assertEquals("ParietalLobe", parietalLobe.getName());
        assertEquals("Spatial", parietalLobe.getType());
    }
    
    @Test
    @Order(11)
    public void testParietalLobeWire() {
        parietalLobe.wire();
        Map<String, Object> stats = parietalLobe.getScrapingStats();
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalPagesScraped"));
        assertTrue(stats.containsKey("extractionAccuracy"));
    }
    
    @Test
    @Order(12)
    public void testParietalLobeStats() {
        parietalLobe.wire();
        parietalLobe.resetStats();
        
        Map<String, Object> stats = parietalLobe.getScrapingStats();
        assertNotNull(stats);
        assertEquals(0, stats.get("totalPagesScraped"));
        assertEquals(0, stats.get("successfulExtractions"));
    }
    
    @Test
    @Order(13)
    public void testParietalLobeCache() {
        parietalLobe.wire();
        parietalLobe.clearCache();
        
        Map<String, Object> stats = parietalLobe.getScrapingStats();
        assertEquals(0, stats.get("cacheSize"));
    }
    
    @Test
    @Order(14)
    public void testParietalLobeUnwire() {
        parietalLobe.wire();
        parietalLobe.unwire();
        
        BrainQuery query = new BrainQuery(
            "Test web scraping",
            java.util.Collections.emptyList(),
            new java.util.HashMap<>(),
            1.0
        );
        
        BrainResponse response = parietalLobe.process(query);
        assertFalse(response.isSuccess());
    }
    
    // ==================== Integration Tests ====================
    
    @Test
    @Order(15)
    public void testSensorySystemIntegration() {
        // Wire all sensory components
        occipitalLobe.wire();
        temporalLobe.wire();
        parietalLobe.wire();
        
        // Check all are wired
        assertNotNull(occipitalLobe.getVisionStats());
        assertNotNull(temporalLobe.getTemporalStats());
        assertNotNull(parietalLobe.getScrapingStats());
        
        // Reset stats
        occipitalLobe.resetStats();
        temporalLobe.resetStats();
        parietalLobe.resetStats();
        
        // Verify reset worked
        assertEquals(0, occipitalLobe.getVisionStats().get("totalImagesProcessed"));
        assertEquals(0, temporalLobe.getTemporalStats().get("totalSeriesProcessed"));
        assertEquals(0, parietalLobe.getScrapingStats().get("totalPagesScraped"));
        
        // Cleanup
        occipitalLobe.unwire();
        temporalLobe.unwire();
        parietalLobe.unwire();
    }
}
