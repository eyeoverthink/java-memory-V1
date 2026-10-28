package fraymus.neural;

import java.util.*;

/**
 * SensorimotorEngine - Motor command and sensory input processing
 * 
 * Phase 5: Embodiment & Multi-Modal
 * 
 * This class provides:
 * - Motor command processing (actuators, movement)
 * - Sensory input processing (sensors, feedback)
 * - Sensorimotor coordination
 * - Error handling and safety
 * 
 * RULE SC-1: Never modify existing data structures without safety checks
 * RULE SC-2: Maintains backward compatibility - optional enhancement
 */
public class SensorimotorEngine {
    
    private final SentientCore core;
    private final Map<String, MotorCommand> motorCommands;
    private final Map<String, SensoryInput> sensoryInputs;
    private final Queue<SensorimotorEvent> eventQueue;
    
    // Statistics
    private int motorCommandsProcessed = 0;
    private int sensoryInputsProcessed = 0;
    private int coordinationEvents = 0;
    private long totalProcessingTimeMs = 0;
    
    public SensorimotorEngine(SentientCore core) {
        if (core == null) {
            throw new IllegalArgumentException("SentientCore cannot be null");
        }
        this.core = core;
        this.motorCommands = new HashMap<>();
        this.sensoryInputs = new HashMap<>();
        this.eventQueue = new LinkedList<>();
    }
    
    /**
     * Process a motor command
     * 
     * @param command The motor command to process
     * @return Processing result
     */
    public MotorResult processMotorCommand(MotorCommand command) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate command
            if (command == null || command.action == null) {
                return new MotorResult(false, "Invalid command", 0);
            }
            
            // Safety check
            if (!isCommandSafe(command)) {
                return new MotorResult(false, "Command blocked by safety check", 0);
            }
            
            // Process command
            motorCommands.put(command.id, command);
            motorCommandsProcessed++;
            
            // Log to core
            core.learn("motor command: " + command.action + " on " + command.target);
            
            long elapsed = System.currentTimeMillis() - startTime;
            totalProcessingTimeMs += elapsed;
            
            return new MotorResult(true, "Command executed", elapsed);
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new MotorResult(false, "ERROR: " + e.getMessage(), elapsed);
        }
    }
    
    /**
     * Process a sensory input
     * 
     * @param input The sensory input to process
     * @return Processing result
     */
    public SensoryResult processSensoryInput(SensoryInput input) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate input
            if (input == null || input.type == null) {
                return new SensoryResult(false, "Invalid input", 0);
            }
            
            // Process input
            sensoryInputs.put(input.id, input);
            sensoryInputsProcessed++;
            
            // Log to core
            core.learn("sensory input: " + input.type + " value: " + input.value);
            
            long elapsed = System.currentTimeMillis() - startTime;
            totalProcessingTimeMs += elapsed;
            
            return new SensoryResult(true, "Input processed", elapsed);
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new SensoryResult(false, "ERROR: " + e.getMessage(), elapsed);
        }
    }
    
    /**
     * Coordinate sensorimotor event
     * 
     * @param event The sensorimotor event to coordinate
     * @return Coordination result
     */
    public CoordinationResult coordinateEvent(SensorimotorEvent event) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate event
            if (event == null || event.type == null) {
                return new CoordinationResult(false, "Invalid event", 0);
            }
            
            // Add to queue
            eventQueue.add(event);
            coordinationEvents++;
            
            // Process coordination
            String coordination = "coordinated " + event.type + " with " + event.sensoryInput + " and " + event.motorCommand;
            core.learn(coordination);
            
            long elapsed = System.currentTimeMillis() - startTime;
            totalProcessingTimeMs += elapsed;
            
            return new CoordinationResult(true, "Event coordinated", elapsed);
            
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new CoordinationResult(false, "ERROR: " + e.getMessage(), elapsed);
        }
    }
    
    /**
     * Safety check for motor commands
     */
    private boolean isCommandSafe(MotorCommand command) {
        // Basic safety checks
        if (command.action.toLowerCase().contains("destroy") || 
            command.action.toLowerCase().contains("damage") ||
            command.action.toLowerCase().contains("harm")) {
            return false;
        }
        return true;
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        long avgTime = motorCommandsProcessed + sensoryInputsProcessed + coordinationEvents > 0 
            ? totalProcessingTimeMs / (motorCommandsProcessed + sensoryInputsProcessed + coordinationEvents)
            : 0;
        
        return String.format(
            "Sensorimotor Engine Statistics:\n" +
            "  Motor commands processed: %d\n" +
            "  Sensory inputs processed: %d\n" +
            "  Coordination events: %d\n" +
            "  Total operations: %d\n" +
            "  Average processing time: %dms\n",
            motorCommandsProcessed,
            sensoryInputsProcessed,
            coordinationEvents,
            motorCommandsProcessed + sensoryInputsProcessed + coordinationEvents,
            avgTime
        );
    }
    
    // =========================================================================================
    // INNER CLASSES
    // =========================================================================================
    
    public static class MotorCommand {
        public final String id;
        public final String action;
        public final String target;
        public final double intensity;
        
        public MotorCommand(String id, String action, String target, double intensity) {
            this.id = id;
            this.action = action;
            this.target = target;
            this.intensity = intensity;
        }
    }
    
    public static class SensoryInput {
        public final String id;
        public final String type;
        public final double value;
        public final long timestamp;
        
        public SensoryInput(String id, String type, double value) {
            this.id = id;
            this.type = type;
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public static class SensorimotorEvent {
        public final String type;
        public final String sensoryInput;
        public final String motorCommand;
        
        public SensorimotorEvent(String type, String sensoryInput, String motorCommand) {
            this.type = type;
            this.sensoryInput = sensoryInput;
            this.motorCommand = motorCommand;
        }
    }
    
    public static class MotorResult {
        public final boolean success;
        public final String message;
        public final long elapsedTimeMs;
        
        public MotorResult(boolean success, String message, long elapsedTimeMs) {
            this.success = success;
            this.message = message;
            this.elapsedTimeMs = elapsedTimeMs;
        }
    }
    
    public static class SensoryResult {
        public final boolean success;
        public final String message;
        public final long elapsedTimeMs;
        
        public SensoryResult(boolean success, String message, long elapsedTimeMs) {
            this.success = success;
            this.message = message;
            this.elapsedTimeMs = elapsedTimeMs;
        }
    }
    
    public static class CoordinationResult {
        public final boolean success;
        public final String message;
        public final long elapsedTimeMs;
        
        public CoordinationResult(boolean success, String message, long elapsedTimeMs) {
            this.success = success;
            this.message = message;
            this.elapsedTimeMs = elapsedTimeMs;
        }
    }
}
