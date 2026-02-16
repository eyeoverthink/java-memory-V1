/**
 * DecisionArrayCommands.java - REPL Commands for Decision Array
 * 
 * Registers decision array commands in the REPL:
 * - :decision - Create and run decision array
 * - :addnode - Add decision node
 * - :strategy - Set voting strategy
 * - :decide - Make collective decision
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;

/**
 * Decision array command registration for REPL.
 */
public class DecisionArrayCommands {
    
    private static final double PHI = 1.618033988749895;
    private static DecisionArray currentArray;
    
    /**
     * Register all decision array commands.
     */
    public static void registerAll(ReplCommandRegistry registry) {
        
        // Initialize default array
        currentArray = new DecisionArray();
        
        // :DECISION command - Create decision array with archetypes
        registry.register(":decision",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :decision <archetype1> <archetype2> ...\n" +
                           "Archetypes: conservative, aggressive, analytical, creative, balanced, explorer, guardian\n" +
                           "Example: :decision conservative aggressive analytical creative";
                }
                
                currentArray = new DecisionArray();
                
                for (String archetype : args) {
                    currentArray.addArchetype(archetype);
                }
                
                return currentArray.getStatus();
            },
            "Create decision array with archetypes",
            ":decision <archetype1> <archetype2> ...");
        
        // :ADDNODE command - Add custom decision node
        registry.register(":addnode",
            args -> {
                if (args.size() < 4) {
                    return "Usage: :addnode <name> <risk> <creativity> <speed>\n" +
                           "Values: 0.0 to 1.0\n" +
                           "Example: :addnode CustomNode 0.7 0.8 0.5";
                }
                
                try {
                    String name = args.get(0);
                    double risk = Double.parseDouble(args.get(1));
                    double creativity = Double.parseDouble(args.get(2));
                    double speed = Double.parseDouble(args.get(3));
                    
                    DecisionArray.DecisionNode node = new DecisionArray.DecisionNode(
                        name, risk, creativity, speed);
                    currentArray.addNode(node);
                    
                    return "Added node: " + node.toString();
                    
                } catch (NumberFormatException e) {
                    return "Error: Invalid number format. Use values between 0.0 and 1.0";
                }
            },
            "Add custom decision node to array",
            ":addnode <name> <risk> <creativity> <speed>");
        
        // :STRATEGY command - Set voting strategy
        registry.register(":strategy",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :strategy <type>\n" +
                           "Types: majority, weighted, consensus, phi_harmonic, exploration\n" +
                           "Example: :strategy phi_harmonic";
                }
                
                String strategyName = args.get(0).toUpperCase();
                
                try {
                    DecisionArray.VotingStrategy strategy = DecisionArray.VotingStrategy.valueOf(strategyName);
                    currentArray.setStrategy(strategy);
                    return "Strategy set to: " + strategy;
                } catch (IllegalArgumentException e) {
                    return "Unknown strategy: " + args.get(0) + "\n" +
                           "Available: majority, weighted, consensus, phi_harmonic, exploration";
                }
            },
            "Set voting strategy for decision array",
            ":strategy <type>");
        
        // :DECIDE command - Make collective decision
        registry.register(":decide",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :decide <option1> <option2> ...\n" +
                           "Example: :decide \"Build Compiler\" \"Build Interpreter\" \"Build Both\"";
                }
                
                List<String> options = new ArrayList<>(args);
                Map<String, Double> weights = new HashMap<>();
                
                // Equal weights by default
                for (String option : options) {
                    weights.put(option, 1.0);
                }
                
                DecisionArray.CollectiveDecision decision = currentArray.decide(options, weights);
                return decision.toString();
            },
            "Make collective decision on options",
            ":decide <option1> <option2> ...");
        
        // :DECIDEW command - Make weighted decision
        registry.register(":decidew",
            args -> {
                if (args.size() < 2 || args.size() % 2 != 0) {
                    return "Usage: :decidew <option1> <weight1> <option2> <weight2> ...\n" +
                           "Example: :decidew \"Option A\" 1.5 \"Option B\" 0.8 \"Option C\" 1.2";
                }
                
                List<String> options = new ArrayList<>();
                Map<String, Double> weights = new HashMap<>();
                
                for (int i = 0; i < args.size(); i += 2) {
                    String option = args.get(i);
                    try {
                        double weight = Double.parseDouble(args.get(i + 1));
                        options.add(option);
                        weights.put(option, weight);
                    } catch (NumberFormatException e) {
                        return "Error: Invalid weight for option '" + option + "'";
                    }
                }
                
                DecisionArray.CollectiveDecision decision = currentArray.decide(options, weights);
                return decision.toString();
            },
            "Make weighted collective decision",
            ":decidew <option1> <weight1> <option2> <weight2> ...");
        
        // :DARRAY command - Show decision array status
        registry.register(":darray",
            args -> {
                return currentArray.getStatus();
            },
            "Show decision array status",
            ":darray");
        
        // :PRESET command - Load preset decision arrays
        registry.register(":preset",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :preset <name>\n" +
                           "Presets:\n" +
                           "  board - Corporate board (conservative, analytical, balanced, guardian)\n" +
                           "  startup - Startup team (aggressive, creative, explorer, balanced)\n" +
                           "  research - Research team (analytical, creative, explorer, conservative)\n" +
                           "  balanced - Balanced team (all archetypes)\n" +
                           "  extreme - Extreme diversity (aggressive, guardian, creative, analytical)";
                }
                
                String preset = args.get(0).toLowerCase();
                currentArray = new DecisionArray();
                
                switch (preset) {
                    case "board":
                        currentArray.addArchetype("conservative");
                        currentArray.addArchetype("analytical");
                        currentArray.addArchetype("balanced");
                        currentArray.addArchetype("guardian");
                        currentArray.setStrategy(DecisionArray.VotingStrategy.CONSENSUS);
                        break;
                        
                    case "startup":
                        currentArray.addArchetype("aggressive");
                        currentArray.addArchetype("creative");
                        currentArray.addArchetype("explorer");
                        currentArray.addArchetype("balanced");
                        currentArray.setStrategy(DecisionArray.VotingStrategy.WEIGHTED);
                        break;
                        
                    case "research":
                        currentArray.addArchetype("analytical");
                        currentArray.addArchetype("creative");
                        currentArray.addArchetype("explorer");
                        currentArray.addArchetype("conservative");
                        currentArray.setStrategy(DecisionArray.VotingStrategy.PHI_HARMONIC);
                        break;
                        
                    case "balanced":
                        currentArray.addArchetype("conservative");
                        currentArray.addArchetype("aggressive");
                        currentArray.addArchetype("analytical");
                        currentArray.addArchetype("creative");
                        currentArray.addArchetype("balanced");
                        currentArray.addArchetype("explorer");
                        currentArray.addArchetype("guardian");
                        currentArray.setStrategy(DecisionArray.VotingStrategy.PHI_HARMONIC);
                        break;
                        
                    case "extreme":
                        currentArray.addArchetype("aggressive");
                        currentArray.addArchetype("guardian");
                        currentArray.addArchetype("creative");
                        currentArray.addArchetype("analytical");
                        currentArray.setStrategy(DecisionArray.VotingStrategy.EXPLORATION);
                        break;
                        
                    default:
                        return "Unknown preset: " + preset;
                }
                
                return "Loaded preset: " + preset + "\n\n" + currentArray.getStatus();
            },
            "Load preset decision array configuration",
            ":preset <name>");
    }
}
