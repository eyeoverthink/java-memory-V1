package fraymus.neural;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SensorimotorEngine
 * Phase 5: Embodiment & Multi-Modal
 */
public class SensorimotorEngineTest {
    
    private SentientCore core;
    private SensorimotorEngine engine;
    
    @BeforeEach
    void setUp() {
        core = new SentientCore();
        engine = new SensorimotorEngine(core);
    }
    
    @Test
    void testConstructorWithNullCore() {
        assertThrows(IllegalArgumentException.class, () -> new SensorimotorEngine(null));
    }
    
    @Test
    void testConstructorWithValidCore() {
        assertNotNull(engine);
        assertEquals(core, core);
    }
    
    @Test
    void testProcessMotorCommand() {
        SensorimotorEngine.MotorCommand command = new SensorimotorEngine.MotorCommand(
            "cmd1", "move", "arm", 0.5);
        SensorimotorEngine.MotorResult result = engine.processMotorCommand(command);
        
        assertTrue(result.success);
        assertEquals("Command executed", result.message);
        assertTrue(result.elapsedTimeMs >= 0);
    }
    
    @Test
    void testProcessMotorCommandWithNullCommand() {
        SensorimotorEngine.MotorResult result = engine.processMotorCommand(null);
        
        assertFalse(result.success);
        assertEquals("Invalid command", result.message);
    }
    
    @Test
    void testProcessMotorCommandWithUnsafeCommand() {
        SensorimotorEngine.MotorCommand command = new SensorimotorEngine.MotorCommand(
            "cmd1", "destroy", "arm", 0.5);
        SensorimotorEngine.MotorResult result = engine.processMotorCommand(command);
        
        assertFalse(result.success);
        assertEquals("Command blocked by safety check", result.message);
    }
    
    @Test
    void testProcessSensoryInput() {
        SensorimotorEngine.SensoryInput input = new SensorimotorEngine.SensoryInput(
            "sensor1", "temperature", 25.0);
        SensorimotorEngine.SensoryResult result = engine.processSensoryInput(input);
        
        assertTrue(result.success);
        assertEquals("Input processed", result.message);
        assertTrue(result.elapsedTimeMs >= 0);
    }
    
    @Test
    void testProcessSensoryInputWithNullInput() {
        SensorimotorEngine.SensoryResult result = engine.processSensoryInput(null);
        
        assertFalse(result.success);
        assertEquals("Invalid input", result.message);
    }
    
    @Test
    void testCoordinateEvent() {
        SensorimotorEngine.SensorimotorEvent event = new SensorimotorEngine.SensorimotorEvent(
            "coordination", "sensor1", "cmd1");
        SensorimotorEngine.CoordinationResult result = engine.coordinateEvent(event);
        
        assertTrue(result.success);
        assertEquals("Event coordinated", result.message);
        assertTrue(result.elapsedTimeMs >= 0);
    }
    
    @Test
    void testCoordinateEventWithNullEvent() {
        SensorimotorEngine.CoordinationResult result = engine.coordinateEvent(null);
        
        assertFalse(result.success);
        assertEquals("Invalid event", result.message);
    }
    
    @Test
    void testGetStats() {
        engine.processMotorCommand(new SensorimotorEngine.MotorCommand("cmd1", "move", "arm", 0.5));
        engine.processSensoryInput(new SensorimotorEngine.SensoryInput("sensor1", "temp", 25.0));
        
        String stats = engine.getStats();
        assertNotNull(stats);
        assertTrue(stats.contains("Sensorimotor Engine Statistics"));
        assertTrue(stats.contains("Motor commands processed: 1"));
        assertTrue(stats.contains("Sensory inputs processed: 1"));
    }
    
    @Test
    void testMotorCommandFields() {
        SensorimotorEngine.MotorCommand command = new SensorimotorEngine.MotorCommand(
            "cmd1", "move", "arm", 0.5);
        
        assertEquals("cmd1", command.id);
        assertEquals("move", command.action);
        assertEquals("arm", command.target);
        assertEquals(0.5, command.intensity);
    }
    
    @Test
    void testSensoryInputFields() {
        SensorimotorEngine.SensoryInput input = new SensorimotorEngine.SensoryInput(
            "sensor1", "temperature", 25.0);
        
        assertEquals("sensor1", input.id);
        assertEquals("temperature", input.type);
        assertEquals(25.0, input.value);
        assertTrue(input.timestamp > 0);
    }
    
    @Test
    void testSensorimotorEventFields() {
        SensorimotorEngine.SensorimotorEvent event = new SensorimotorEngine.SensorimotorEvent(
            "coordination", "sensor1", "cmd1");
        
        assertEquals("coordination", event.type);
        assertEquals("sensor1", event.sensoryInput);
        assertEquals("cmd1", event.motorCommand);
    }
    
    @Test
    void testMultipleOperations() {
        for (int i = 0; i < 5; i++) {
            engine.processMotorCommand(new SensorimotorEngine.MotorCommand(
                "cmd" + i, "move", "arm", 0.5));
            engine.processSensoryInput(new SensorimotorEngine.SensoryInput(
                "sensor" + i, "temp", 25.0));
        }
        
        String stats = engine.getStats();
        assertTrue(stats.contains("Motor commands processed: 5"));
        assertTrue(stats.contains("Sensory inputs processed: 5"));
    }
}
