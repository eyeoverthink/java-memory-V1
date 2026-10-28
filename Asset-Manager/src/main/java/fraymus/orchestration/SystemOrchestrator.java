package fraymus.orchestration;

import fraymus.execution.ExecutionBridge;
import fraymus.field.FieldEngine;

/**
 * System Orchestrator
 * 
 * Central routing and coordination for all Fraymus components.
 * Implements the canonical pipeline: Acquire → Transmute → Absorb → Index → Reason → Act → Reflect
 * 
 * Components:
 * - CommandParser: Input routing and intent classification
 * - FieldEngine: Continuous-state cognitive processing
 * - CoherenceBuilderMode: Output validation and execution discipline
 * - ExecutionBridge: Bare-metal code execution
 */
public class SystemOrchestrator {
    
    private CommandParser commandParser;
    private FieldEngine fieldEngine;
    private CoherenceBuilderMode coherenceBuilder;
    private ExecutionBridge executionBridge;
    private HydraProtocol hydraProtocol;
    
    private boolean executionBridgeEnabled;
    private boolean coherenceBuilderEnabled;
    
    public SystemOrchestrator() {
        this.commandParser = new CommandParser();
        this.fieldEngine = new FieldEngine(16384, 0.01f, 0.1f, 0.5f);
        this.coherenceBuilder = new CoherenceBuilderMode();
        this.executionBridge = new ExecutionBridge();
        this.hydraProtocol = new HydraProtocol();
        
        this.executionBridgeEnabled = true;
        this.coherenceBuilderEnabled = true;
    }
    
    /**
     * Main processing pipeline
     */
    public String processCommand(String command) {
        // Step 1: Parse command
        CommandParser.ParsedCommand parsed = commandParser.parse(command);
        if (parsed == null || !parsed.isValid()) {
            return "Invalid command. Use format: ai [INTENT] [TARGET] [METHOD] [OUTPUT]";
        }
        
        // Step 2: Route to appropriate handler
        String route = commandParser.routeCommand(command);
        
        // Step 3: Process through field engine (cognitive layer)
        processThroughFieldEngine(parsed);
        
        // Step 4: Generate response (placeholder - would call actual brains)
        String response = generateResponse(parsed, route);
        
        // Step 5: Validate response with coherence builder
        if (coherenceBuilderEnabled) {
            boolean isValid = CoherenceBuilderMode.validateResponse(response);
            if (!isValid) {
                // Apply coherence builder contract
                response = applyCoherenceContract(parsed, response);
            }
        }
        
        // Step 6: Execute code blocks if present
        if (executionBridgeEnabled && response.contains("```")) {
            response = executionBridge.processText(response);
        }
        
        return response;
    }
    
    /**
     * Process command through field engine
     */
    private void processThroughFieldEngine(CommandParser.ParsedCommand parsed) {
        // Convert command to field state (simplified)
        float[] input = commandToFieldState(parsed);
        
        // Excite field with input
        fieldEngine.excite(input, 0.5f);
        
        // Integrate field dynamics
        fieldEngine.integrate();
        
        // Apply homeostasis
        fieldEngine.homeostasis(0.4f, 0.05f);
    }
    
    /**
     * Convert parsed command to field state
     */
    private float[] commandToFieldState(CommandParser.ParsedCommand parsed) {
        int dim = Math.min(fieldEngine.getDimension(), 64);
        float[] state = new float[dim];
        
        // Simple encoding: use hash of intent and target
        int intentHash = parsed.intent != null ? parsed.intent.hashCode() : 0;
        int targetHash = parsed.target != null ? parsed.target.hashCode() : 0;
        
        for (int i = 0; i < dim; i++) {
            // Distribute hash across field
            if (i % 2 == 0) {
                state[i] = ((intentHash >> (i / 2)) % 100) / 100.0f;
            } else {
                state[i] = ((targetHash >> (i / 2)) % 100) / 100.0f;
            }
            // Clamp to [-1, 1]
            state[i] = Math.max(-1.0f, Math.min(1.0f, state[i]));
        }
        
        return state;
    }
    
    /**
     * Generate response (placeholder - would call actual multi-brain system)
     */
    private String generateResponse(CommandParser.ParsedCommand parsed, String route) {
        if ("hydra_status".equals(route)) {
            return hydraProtocol.renderCompactStatus();
        }
        if ("hydra".equals(route) || "transcend".equals(parsed.intent)) {
            return hydraProtocol.execute(parsed, fieldEngine.getDimension());
        }

        StringBuilder sb = new StringBuilder();
        
        sb.append("Processing command: ").append(parsed.intent);
        if (parsed.target != null) {
            sb.append(" ").append(parsed.target);
        }
        sb.append("\n");
        sb.append("Route: ").append(route).append("\n");
        sb.append("Field state: ");
        sb.append(fieldEngine.getDimension()).append(" dimensions\n");
        
        // Placeholder for actual brain response
        sb.append("\n[Response would be generated by multi-brain system]\n");
        
        return sb.toString();
    }
    
    /**
     * Apply coherence builder contract to response
     */
    private String applyCoherenceContract(CommandParser.ParsedCommand parsed, String originalResponse) {
        // Build appropriate contract based on intent
        CoherenceBuilderMode.OutputContract contract;
        
        if (parsed.intent.equals("build") && parsed.target != null && parsed.target.contains("field")) {
            contract = CoherenceBuilderMode.buildFieldRuntimeContract();
        } else {
            // Generic contract
            contract = new CoherenceBuilderMode.OutputContract();
            contract.coherenceDefinition = new CoherenceBuilderMode.CoherenceDefinition(
                "generic coherence",
                "C = Σ(output_quality)",
                "Generic coherence metric"
            );
        }
        
        // Append contract requirements to response
        return originalResponse + "\n\n" + contract.generateReport();
    }
    
    /**
     * Enable/disable execution bridge
     */
    public void setExecutionBridgeEnabled(boolean enabled) {
        this.executionBridgeEnabled = enabled;
    }
    
    /**
     * Enable/disable coherence builder
     */
    public void setCoherenceBuilderEnabled(boolean enabled) {
        this.coherenceBuilderEnabled = enabled;
    }
    
    /**
     * Get field engine
     */
    public FieldEngine getFieldEngine() {
        return fieldEngine;
    }
    
    /**
     * Get command parser
     */
    public CommandParser getCommandParser() {
        return commandParser;
    }
    
    /**
     * Get execution bridge
     */
    public ExecutionBridge getExecutionBridge() {
        return executionBridge;
    }
    
    /**
     * Reset field engine to neutral state
     */
    public void resetFieldEngine() {
        fieldEngine.reset();
    }
    
    /**
     * Get current field state summary
     */
    public String getFieldStateSummary() {
        float[] state = fieldEngine.getState();
        float meanActivity = 0.0f;
        float maxActivity = 0.0f;
        
        for (float s : state) {
            meanActivity += Math.abs(s);
            if (Math.abs(s) > maxActivity) {
                maxActivity = Math.abs(s);
            }
        }
        meanActivity /= state.length;
        
        return String.format("Field: %d dims, mean activity: %.3f, max activity: %.3f",
            state.length, meanActivity, maxActivity);
    }
}
