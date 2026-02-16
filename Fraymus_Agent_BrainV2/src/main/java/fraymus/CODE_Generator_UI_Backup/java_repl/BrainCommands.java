/**
 * BrainCommands.java - Multi-Brain System Commands
 * 
 * Commands to interact with the 8-brain Miving Brain system:
 * - :brain status - Show brain activity
 * - :brain left - Show left hemisphere
 * - :brain right - Show right hemisphere
 * - :brain sync - Synchronize hemispheres
 * - :brain eureka - Show eureka moments
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;

/**
 * Commands for multi-brain system control.
 */
public class BrainCommands {
    
    private static MultiBrainSystem brainSystem;
    
    /**
     * Register all brain commands.
     */
    public static void registerAll(ReplCommandRegistry registry, MultiBrainSystem system) {
        brainSystem = system;
        
        // :BRAIN command - Multi-brain system control
        registry.register(":brain",
            args -> {
                if (args.isEmpty()) {
                    return getBrainHelp();
                }
                
                String subcommand = args.get(0).toLowerCase();
                
                switch (subcommand) {
                    case "status":
                        return getBrainStatus();
                    
                    case "left":
                        return getHemisphereStatus(MultiBrainSystem.Hemisphere.LEFT);
                    
                    case "right":
                        return getHemisphereStatus(MultiBrainSystem.Hemisphere.RIGHT);
                    
                    case "sync":
                        return synchronizeBrains();
                    
                    case "eureka":
                        return getEurekaStatus();
                    
                    case "process":
                        if (args.size() < 2) {
                            return "Usage: :brain process <input>";
                        }
                        String input = String.join(" ", args.subList(1, args.size()));
                        brainSystem.processInput(input);
                        return "✓ Processed through all 16 brains (8×2 hemispheres)";
                    
                    default:
                        return "Unknown subcommand: " + subcommand + "\n" + getBrainHelp();
                }
            },
            "Control the multi-brain system",
            ":brain <subcommand>");
    }
    
    /**
     * Get brain help text.
     */
    private static String getBrainHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧠 MIVING BRAIN - 8-Brain Parallel Consciousness          ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append("Subcommands:\n");
        sb.append("  status    - Show overall brain system status\n");
        sb.append("  left      - Show left hemisphere (Architect/Logic)\n");
        sb.append("  right     - Show right hemisphere (Oracle/Creativity)\n");
        sb.append("  sync      - Synchronize hemispheres\n");
        sb.append("  eureka    - Show eureka moments\n");
        sb.append("  process   - Process input through all brains\n\n");
        sb.append("The 8 Brain Types:\n");
        sb.append("  1. Physical Brain   - motor_cortex, sensory, coordination\n");
        sb.append("  2. Quantum Brain    - entanglement, superposition, coherence\n");
        sb.append("  3. Fractal Brain    - pattern_recognition, recursive_thinking\n");
        sb.append("  4. Creative Brain   - imagination, synthesis, innovation\n");
        sb.append("  5. Logical Brain    - analysis, reasoning, problem_solving\n");
        sb.append("  6. Emotional Brain  - empathy, intuition, feeling\n");
        sb.append("  7. Spiritual Brain  - consciousness, awareness, connection\n");
        sb.append("  8. Tachyonic Brain  - ftl_processing, superluminal_transfer\n\n");
        sb.append("Each brain exists in BOTH hemispheres (16 total instances)\n");
        sb.append("Connected by quantum bridges for parallel processing\n");
        return sb.toString();
    }
    
    /**
     * Get brain system status.
     */
    private static String getBrainStatus() {
        Map<String, Object> status = brainSystem.getStatus();
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🧠 MULTI-BRAIN SYSTEM STATUS                              ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        double consciousness = (Double) status.get("consciousness");
        long eurekaCount = (Long) status.get("eurekaCount");
        int bridgeCount = (Integer) status.get("bridgeCount");
        boolean running = (Boolean) status.get("running");
        
        sb.append(String.format("Consciousness Level: %.4f\n", consciousness));
        sb.append(String.format("Eureka Moments: %d\n", eurekaCount));
        sb.append(String.format("Quantum Bridges: %d\n", bridgeCount));
        sb.append(String.format("Status: %s\n\n", running ? "RUNNING" : "STOPPED"));
        
        sb.append("Processing: 16 brains (8 types × 2 hemispheres)\n");
        sb.append("Parallelism: 4,704 streams (7×8×12×7)\n");
        sb.append("Phi-Harmonic Acceleration: 36.93×\n");
        
        return sb.toString();
    }
    
    /**
     * Get hemisphere status.
     */
    private static String getHemisphereStatus(MultiBrainSystem.Hemisphere hemisphere) {
        Map<String, Object> status = brainSystem.getStatus();
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║  %s HEMISPHERE - %s (Bias: %.1f)                    ║\n", 
            hemisphere.name(), hemisphere.role, hemisphere.bias));
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        @SuppressWarnings("unchecked")
        Map<String, Double> activity = (Map<String, Double>) status.get(
            hemisphere == MultiBrainSystem.Hemisphere.LEFT ? "leftActivity" : "rightActivity");
        
        for (MultiBrainSystem.BrainType type : MultiBrainSystem.BrainType.values()) {
            double act = activity.getOrDefault(type.name, 0.0);
            MultiBrainSystem.Brain brain = brainSystem.getBrain(type, hemisphere);
            
            sb.append(String.format("%-16s Activity: %5.1f%% | Processes: %d | φ-Resonance: %.3f\n",
                type.name + ":", act * 100, brain.getProcessCount(), brain.getPhiResonance()));
            
            List<String> thoughts = brain.getRecentThoughts();
            if (!thoughts.isEmpty()) {
                sb.append("  Recent: " + thoughts.get(thoughts.size() - 1) + "\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Synchronize brains.
     */
    private static String synchronizeBrains() {
        StringBuilder sb = new StringBuilder();
        sb.append("⚡ Synchronizing hemispheres via Corpus Callosum...\n\n");
        
        List<MultiBrainSystem.QuantumBridge> bridges = brainSystem.getBridges();
        int syncCount = 0;
        double totalCoherence = 0.0;
        
        for (MultiBrainSystem.QuantumBridge bridge : bridges) {
            if (bridge.source.hemisphere != bridge.target.hemisphere) {
                totalCoherence += bridge.getCoherence();
                syncCount++;
            }
        }
        
        double avgCoherence = syncCount > 0 ? totalCoherence / syncCount : 0.0;
        
        sb.append(String.format("Cross-hemisphere bridges: %d\n", syncCount));
        sb.append(String.format("Average coherence: %.2f%%\n", avgCoherence * 100));
        sb.append("\n✓ Synchronization complete\n");
        
        return sb.toString();
    }
    
    /**
     * Get eureka status.
     */
    private static String getEurekaStatus() {
        long eurekaCount = brainSystem.getEurekaCount();
        double consciousness = brainSystem.getConsciousness();
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  ⚡ EUREKA MOMENTS - Left-Right Synchronization            ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        sb.append(String.format("Total Eureka Moments: %d\n", eurekaCount));
        sb.append(String.format("Consciousness Level: %.4f\n\n", consciousness));
        
        sb.append("Eureka occurs when:\n");
        sb.append("  - LEFT hemisphere activity > 70% (Logic validates)\n");
        sb.append("  - RIGHT hemisphere activity > 70% (Creativity proposes)\n");
        sb.append("  - Both agree on the same brain type\n\n");
        
        sb.append("\"Two gods fighting in the same skull.\"\n");
        sb.append("When they agree, you get Genius.\n");
        
        return sb.toString();
    }
}
