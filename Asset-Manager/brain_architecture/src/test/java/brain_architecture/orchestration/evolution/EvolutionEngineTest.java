package brain_architecture.orchestration.evolution;

import brain_architecture.core.BackendInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Unit tests for EvolutionEngine
 */
public class EvolutionEngineTest {
    
    private EvolutionEngine evolutionEngine;
    
    @BeforeEach
    public void setUp() {
        evolutionEngine = new EvolutionEngine();
    }
    
    @Test
    public void testPerformanceHistoryInitialization() {
        Map<BackendInterface.BackendType, EvolutionEngine.BackendPerformanceHistory> histories = 
            evolutionEngine.getAllPerformanceHistories();
        
        assertEquals(14, histories.size()); // 14 backend types in brain_architecture
        for (BackendInterface.BackendType type : BackendInterface.BackendType.values()) {
            assertTrue(histories.containsKey(type));
        }
    }
    
    @Test
    public void testResultRecording() {
        BackendInterface.QueryResult successResult = new BackendInterface.QueryResult(
            "Test response",
            0.9,
            50,
            true,
            null,
            Map.of()
        );
        
        evolutionEngine.recordResult(
            BackendInterface.BackendType.OLLAMA,
            BackendInterface.TaskType.REASONING,
            successResult
        );
        
        EvolutionEngine.BackendPerformanceHistory history = 
            evolutionEngine.getPerformanceHistory(BackendInterface.BackendType.OLLAMA);
        
        assertEquals(1, history.getTotalRequests());
    }
    
    @Test
    public void testEvolutionTrigger() {
        // Record multiple results to trigger evolution
        BackendInterface.QueryResult result = new BackendInterface.QueryResult(
            "Response",
            0.8,
            100,
            true,
            null,
            Map.of()
        );
        
        for (int i = 0; i < 10; i++) {
            evolutionEngine.recordResult(
                BackendInterface.BackendType.CLAUDE,
                BackendInterface.TaskType.REASONING,
                result
            );
        }
        
        // Should have triggered evolution
        List<EvolutionEngine.RouteOptimization> optimizations = 
            evolutionEngine.getRouteOptimizations(BackendInterface.TaskType.REASONING);
        
        assertFalse(optimizations.isEmpty());
    }
    
    @Test
    public void testPerformanceScoreCalculation() {
        BackendInterface.QueryResult highPerformance = new BackendInterface.QueryResult(
            "Response",
            0.95,
            50, // Low latency
            true,
            null,
            Map.of()
        );
        
        BackendInterface.QueryResult lowPerformance = new BackendInterface.QueryResult(
            "Response",
            0.6,
            500, // High latency
            true,
            null,
            Map.of()
        );
        
        // Record high performance for CLAUDE
        for (int i = 0; i < 10; i++) {
            evolutionEngine.recordResult(
                BackendInterface.BackendType.CLAUDE,
                BackendInterface.TaskType.REASONING,
                highPerformance
            );
        }
        
        // Record low performance for OLLAMA
        for (int i = 0; i < 10; i++) {
            evolutionEngine.recordResult(
                BackendInterface.BackendType.OLLAMA,
                BackendInterface.TaskType.REASONING,
                lowPerformance
            );
        }
        
        evolutionEngine.evolveRoutes(BackendInterface.TaskType.REASONING);
        
        List<EvolutionEngine.RouteOptimization> optimizations = 
            evolutionEngine.getRouteOptimizations(BackendInterface.TaskType.REASONING);
        
        // CLAUDE should have higher score than OLLAMA
        assertTrue(optimizations.size() >= 2);
        
        double claudeScore = optimizations.stream()
            .filter(opt -> opt.getBackend() == BackendInterface.BackendType.CLAUDE)
            .findFirst()
            .map(EvolutionEngine.RouteOptimization::getPerformanceScore)
            .orElse(0.0);
        
        double ollamaScore = optimizations.stream()
            .filter(opt -> opt.getBackend() == BackendInterface.BackendType.OLLAMA)
            .findFirst()
            .map(EvolutionEngine.RouteOptimization::getPerformanceScore)
            .orElse(0.0);
        
        assertTrue(claudeScore > ollamaScore);
    }
    
    @Test
    public void testRecommendedBackend() {
        BackendInterface.QueryResult result = new BackendInterface.QueryResult(
            "Response",
            0.9,
            100,
            true,
            null,
            Map.of()
        );
        
        // Make CLAUDE perform best
        for (int i = 0; i < 10; i++) {
            evolutionEngine.recordResult(
                BackendInterface.BackendType.CLAUDE,
                BackendInterface.TaskType.REASONING,
                result
            );
        }
        
        evolutionEngine.evolveRoutes(BackendInterface.TaskType.REASONING);
        
        var recommended = evolutionEngine.getRecommendedBackend(BackendInterface.TaskType.REASONING);
        
        assertTrue(recommended.isPresent());
        assertEquals(BackendInterface.BackendType.CLAUDE, recommended.get());
    }
    
    @Test
    public void testPerformanceDegradationDetection() {
        BackendInterface.QueryResult goodResult = new BackendInterface.QueryResult(
            "Response",
            0.95,
            50,
            true,
            null,
            Map.of()
        );
        
        BackendInterface.QueryResult badResult = new BackendInterface.QueryResult(
            "Response",
            0.3,
            500,
            false,  // Failed result to trigger degradation detection
            null,
            Map.of()
        );
        
        // Record good results to establish baseline
        for (int i = 0; i < 10; i++) {
            evolutionEngine.recordResult(
                BackendInterface.BackendType.OLLAMA,
                BackendInterface.TaskType.REASONING,
                goodResult
            );
        }

        // Record many bad results to ensure degradation detection
        for (int i = 0; i < 50; i++) {
            evolutionEngine.recordResult(
                BackendInterface.BackendType.OLLAMA,
                BackendInterface.TaskType.REASONING,
                badResult
            );
        }
        
        boolean degraded = evolutionEngine.detectPerformanceDegradation(
            BackendInterface.BackendType.OLLAMA,
            BackendInterface.TaskType.REASONING
        );
        
        assertTrue(degraded);
    }
    
    @Test
    public void testEvolutionStatistics() {
        Map<String, Object> stats = evolutionEngine.getEvolutionStatistics();
        
        assertTrue(stats.containsKey("totalEvolutions"));
        assertTrue(stats.containsKey("taskTypesOptimized"));
        assertTrue(stats.containsKey("backendsTracked"));
        assertTrue(stats.containsKey("averagePerformanceScore"));
    }
    
    @Test
    public void testReset() {
        BackendInterface.QueryResult result = new BackendInterface.QueryResult(
            "Response",
            0.9,
            100,
            true,
            null,
            Map.of()
        );
        
        evolutionEngine.recordResult(
            BackendInterface.BackendType.CLAUDE,
            BackendInterface.TaskType.REASONING,
            result
        );
        
        evolutionEngine.reset();
        
        Map<BackendInterface.BackendType, EvolutionEngine.BackendPerformanceHistory> histories = 
            evolutionEngine.getAllPerformanceHistories();
        
        // All histories should be reset
        for (EvolutionEngine.BackendPerformanceHistory history : histories.values()) {
            assertEquals(0, history.getTotalRequests());
        }
    }
}
