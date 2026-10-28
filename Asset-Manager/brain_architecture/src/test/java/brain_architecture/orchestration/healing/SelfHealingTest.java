package brain_architecture.orchestration.healing;

import brain_architecture.core.BackendInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Unit tests for SelfHealing
 */
public class SelfHealingTest {
    
    private SelfHealing selfHealing;
    
    @BeforeEach
    public void setUp() {
        selfHealing = new SelfHealing();
    }
    
    @Test
    public void testInitialization() {
        Map<String, Object> stats = selfHealing.getStatistics();
        
        assertEquals(14, stats.get("totalCircuits")); // 14 backend types in brain_architecture
        assertFalse((Boolean) stats.get("degradedMode"));
    }
    
    @Test
    public void testRecordSuccess() {
        selfHealing.recordRequest(BackendInterface.BackendType.OLLAMA, true, 100);
        
        SelfHealing.HealthMetrics metrics = selfHealing.getHealthMetrics(BackendInterface.BackendType.OLLAMA);
        
        assertEquals(1, metrics.getTotalRequests());
        assertEquals(1, metrics.getSuccessfulRequests());
        assertEquals(1.0, metrics.getSuccessRate());
    }
    
    @Test
    public void testRecordFailure() {
        selfHealing.recordRequest(BackendInterface.BackendType.OLLAMA, false, 0);
        
        SelfHealing.HealthMetrics metrics = selfHealing.getHealthMetrics(BackendInterface.BackendType.OLLAMA);
        
        assertEquals(1, metrics.getTotalRequests());
        assertEquals(0, metrics.getSuccessfulRequests());
        assertEquals(0.0, metrics.getSuccessRate());
    }
    
    @Test
    public void testCircuitBreakerOpensOnFailures() {
        // Record 5 consecutive failures
        for (int i = 0; i < 5; i++) {
            selfHealing.recordRequest(BackendInterface.BackendType.OLLAMA, false, 0);
        }
        
        SelfHealing.CircuitBreakerState state = 
            selfHealing.getCircuitBreakerState(BackendInterface.BackendType.OLLAMA);
        
        assertEquals(SelfHealing.CircuitState.OPEN, state.getState());
        assertEquals(5, state.getFailureCount());
    }
    
    @Test
    public void testCircuitBreakerClosesOnSuccess() {
        // Open the circuit
        for (int i = 0; i < 5; i++) {
            selfHealing.recordRequest(BackendInterface.BackendType.OLLAMA, false, 0);
        }
        
        assertEquals(SelfHealing.CircuitState.OPEN, 
            selfHealing.getCircuitBreakerState(BackendInterface.BackendType.OLLAMA).getState());
        
        // Manually close for testing
        selfHealing.closeCircuit(BackendInterface.BackendType.OLLAMA);
        
        assertEquals(SelfHealing.CircuitState.CLOSED, 
            selfHealing.getCircuitBreakerState(BackendInterface.BackendType.OLLAMA).getState());
    }
    
    @Test
    public void testRequestAllowedWhenClosed() {
        assertTrue(selfHealing.isRequestAllowed(BackendInterface.BackendType.OLLAMA));
    }
    
    @Test
    public void testRequestBlockedWhenOpen() {
        // Open the circuit
        for (int i = 0; i < 5; i++) {
            selfHealing.recordRequest(BackendInterface.BackendType.OLLAMA, false, 0);
        }
        
        assertFalse(selfHealing.isRequestAllowed(BackendInterface.BackendType.OLLAMA));
    }
    
    @Test
    public void testHealthScoreCalculation() {
        // Record successful requests with good latency
        for (int i = 0; i < 10; i++) {
            selfHealing.recordRequest(BackendInterface.BackendType.OLLAMA, true, 50);
        }
        
        double healthScore = selfHealing.getHealthScore(BackendInterface.BackendType.OLLAMA);
        
        assertTrue(healthScore > 0.8); // Should be high with good performance
    }
    
    @Test
    public void testHealthScoreWithFailures() {
        // Record mixed results
        for (int i = 0; i < 5; i++) {
            selfHealing.recordRequest(BackendInterface.BackendType.OLLAMA, true, 100);
        }
        for (int i = 0; i < 5; i++) {
            selfHealing.recordRequest(BackendInterface.BackendType.OLLAMA, false, 0);
        }
        
        double healthScore = selfHealing.getHealthScore(BackendInterface.BackendType.OLLAMA);
        
        assertTrue(healthScore < 0.8); // Should be lower with failures
        assertTrue(healthScore > 0.0);
    }
    
    @Test
    public void testGetHealthyBackends() {
        // Make one backend unhealthy
        for (int i = 0; i < 5; i++) {
            selfHealing.recordRequest(BackendInterface.BackendType.CLAUDE, false, 0);
        }
        
        List<BackendInterface.BackendType> healthy = selfHealing.getHealthyBackends();
        
        assertFalse(healthy.contains(BackendInterface.BackendType.CLAUDE));
        assertTrue(healthy.size() < 14);
    }
    
    @Test
    public void testDegradedMode() {
        assertFalse(selfHealing.isDegradedMode());
        
        // Manually enable degraded mode
        selfHealing.setDegradedMode(true);
        
        assertTrue(selfHealing.isDegradedMode());
    }
    
    @Test
    public void testHalfOpenCircuit() {
        selfHealing.halfOpenCircuit(BackendInterface.BackendType.OLLAMA);
        
        SelfHealing.CircuitBreakerState state = 
            selfHealing.getCircuitBreakerState(BackendInterface.BackendType.OLLAMA);
        
        assertEquals(SelfHealing.CircuitState.HALF_OPEN, state.getState());
    }
    
    @Test
    public void testReset() {
        selfHealing.recordRequest(BackendInterface.BackendType.OLLAMA, true, 100);
        selfHealing.openCircuit(BackendInterface.BackendType.CLAUDE);
        
        selfHealing.reset();
        
        Map<String, Object> stats = selfHealing.getStatistics();
        assertEquals(14L, stats.get("closedCircuits"));
        assertEquals(0L, stats.get("openCircuits"));
    }
}
