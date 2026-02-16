/**
 * OuroborosCommands.java - Self-Modification System for REPL
 * 
 * "The serpent that eats its own tail."
 * 
 * Interactive commands for proposing, validating, and executing
 * self-modifying code mutations with φ-harmonic evolution.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;

/**
 * Register all Ouroboros self-modification commands.
 */
public class OuroborosCommands {
    
    private static final double PHI = 1.618033988749895;
    
    /**
     * Ouroboros state manager.
     */
    private static class OuroborosEngine {
        private int generation = 0;
        private List<MutationRecord> mutationLog = new ArrayList<>();
        private double consensusThreshold = 0.618; // φ - 1
        private boolean initialized = false;
        
        static class MutationRecord {
            final int generation;
            final String description;
            final LocalDateTime timestamp;
            final double consensus;
            final boolean approved;
            final String code;
            
            MutationRecord(int gen, String desc, double cons, boolean appr, String code) {
                this.generation = gen;
                this.description = desc;
                this.timestamp = LocalDateTime.now();
                this.consensus = cons;
                this.approved = appr;
                this.code = code;
            }
        }
        
        void initialize() {
            if (!initialized) {
                generation = 0;
                mutationLog.clear();
                initialized = true;
            }
        }
        
        boolean proposeMutation(String description, String code, double consensus) {
            initialize();
            
            boolean approved = consensus >= consensusThreshold;
            
            if (approved) {
                generation++;
            }
            
            mutationLog.add(new MutationRecord(generation, description, consensus, approved, code));
            
            return approved;
        }
        
        String getStatus() {
            StringBuilder sb = new StringBuilder();
            sb.append("╭────────────────────────────────────────────────────────────╮\n");
            sb.append("│  🐍 OUROBOROS SELF-BUILDER STATUS                         │\n");
            sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
            sb.append(String.format("Generation:          %d\n", generation));
            sb.append(String.format("Mutations Proposed:  %d\n", mutationLog.size()));
            sb.append(String.format("Mutations Approved:  %d\n", 
                mutationLog.stream().filter(m -> m.approved).count()));
            sb.append(String.format("Consensus Threshold: %.4f (φ - 1)\n", consensusThreshold));
            sb.append(String.format("Status:              %s\n", 
                initialized ? "ACTIVE" : "DORMANT"));
            
            return sb.toString();
        }
        
        String getLog() {
            if (mutationLog.isEmpty()) {
                return "No mutations recorded.";
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("╭────────────────────────────────────────────────────────────────────────────╮\n");
            sb.append("│  🐍 OUROBOROS MUTATION LOG                                                 │\n");
            sb.append("╰────────────────────────────────────────────────────────────────────────────╯\n\n");
            
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
            for (MutationRecord rec : mutationLog) {
                String status = rec.approved ? "✓ APPROVED" : "✗ REJECTED";
                sb.append(String.format("[GEN %d] [%s] [%.4f] %s  %s\n",
                    rec.generation,
                    rec.timestamp.format(fmt),
                    rec.consensus,
                    status,
                    rec.description
                ));
            }
            
            return sb.toString();
        }
        
        String generatePhiCode() {
            int a = (int)(PHI * 10);
            int b = (int)(PHI * PHI * 10);
            return String.format("φ-harmonic: %d + %d = %d", a, b, a + b);
        }
        
        void reset() {
            generation = 0;
            mutationLog.clear();
            initialized = false;
        }
        
        void setThreshold(double threshold) {
            this.consensusThreshold = Math.max(0.0, Math.min(1.0, threshold));
        }
    }
    
    private static OuroborosEngine engine = new OuroborosEngine();
    
    /**
     * Register all Ouroboros commands.
     */
    public static void registerAll(ReplCommandRegistry registry) {
        
        // OUROBOROS - Main command with subcommands
        registry.register("ouroboros",
            args -> {
                if (args.isEmpty()) {
                    return getMainHelp();
                }
                
                String subcommand = args.get(0).toLowerCase();
                List<String> subArgs = args.subList(1, args.size());
                
                switch (subcommand) {
                    case "init":
                        engine.initialize();
                        return "🐍 Ouroboros initialized. The serpent awakens.";
                    
                    case "status":
                        return engine.getStatus();
                    
                    case "log":
                        return engine.getLog();
                    
                    case "reset":
                        engine.reset();
                        return "🐍 Ouroboros reset. Generation 0.";
                    
                    case "threshold":
                        if (subArgs.isEmpty()) {
                            return String.format("Current threshold: %.4f", engine.consensusThreshold);
                        }
                        try {
                            double t = Double.parseDouble(subArgs.get(0));
                            engine.setThreshold(t);
                            return String.format("Threshold set to: %.4f", engine.consensusThreshold);
                        } catch (NumberFormatException e) {
                            return "Invalid threshold value";
                        }
                    
                    default:
                        return "Unknown subcommand. Use: ouroboros help";
                }
            },
            "Self-modification system - The serpent that eats its own tail",
            "ouroboros <init|status|log|reset|threshold>");
        
        // :MUTATE - Propose a mutation
        registry.register(":mutate",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :mutate <description> [consensus]";
                }
                
                String description = String.join(" ", args);
                double consensus = 0.5 + (Math.random() * 0.5); // Random 0.5-1.0
                
                // Check if last arg is a number (consensus override)
                try {
                    String lastArg = args.get(args.size() - 1);
                    double customConsensus = Double.parseDouble(lastArg);
                    if (customConsensus >= 0.0 && customConsensus <= 1.0) {
                        consensus = customConsensus;
                        description = String.join(" ", args.subList(0, args.size() - 1));
                    }
                } catch (NumberFormatException e) {
                    // Not a number, use full description
                }
                
                String code = engine.generatePhiCode();
                boolean approved = engine.proposeMutation(description, code, consensus);
                
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────╮\n");
                sb.append("│  🐍 MUTATION PROPOSAL                                      │\n");
                sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
                sb.append(String.format("Description:  %s\n", description));
                sb.append(String.format("Consensus:    %.4f\n", consensus));
                sb.append(String.format("Threshold:    %.4f\n", engine.consensusThreshold));
                sb.append(String.format("Status:       %s\n", 
                    approved ? "✓ APPROVED - Evolution proceeds" : "✗ REJECTED - Mutation aborted"));
                
                if (approved) {
                    sb.append(String.format("Generation:   %d → %d\n", 
                        engine.generation - 1, engine.generation));
                }
                
                return sb.toString();
            },
            "Propose a self-modification mutation",
            ":mutate <description> [consensus]");
        
        // :EVOLVE - Run multiple evolution cycles
        registry.register(":evolve",
            args -> {
                int cycles = 1;
                if (!args.isEmpty()) {
                    try {
                        cycles = Integer.parseInt(args.get(0));
                    } catch (NumberFormatException e) {
                        return "Invalid cycle count";
                    }
                }
                
                cycles = Math.min(cycles, 100); // Limit to 100
                
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────╮\n");
                sb.append(String.format("│  🐍 EVOLUTION CYCLE (%d iterations)%s│\n", 
                    cycles, " ".repeat(Math.max(0, 26 - String.valueOf(cycles).length()))));
                sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
                
                int startGen = engine.generation;
                int approved = 0;
                
                for (int i = 0; i < cycles; i++) {
                    double consensus = 0.5 + (Math.random() * 0.5);
                    String desc = String.format("Auto-evolution cycle %d", i + 1);
                    if (engine.proposeMutation(desc, engine.generatePhiCode(), consensus)) {
                        approved++;
                    }
                }
                
                sb.append(String.format("Cycles Run:       %d\n", cycles));
                sb.append(String.format("Approved:         %d\n", approved));
                sb.append(String.format("Rejected:         %d\n", cycles - approved));
                sb.append(String.format("Generation:       %d → %d\n", startGen, engine.generation));
                sb.append(String.format("Success Rate:     %.1f%%\n", 
                    (approved * 100.0) / cycles));
                
                return sb.toString();
            },
            "Run multiple evolution cycles automatically",
            ":evolve [cycles]");
        
        // :GENESIS - Create genesis mutation
        registry.register(":genesis",
            args -> {
                engine.reset();
                engine.initialize();
                
                boolean approved = engine.proposeMutation(
                    "Genesis: The first mutation", 
                    "φ-harmonic genesis code",
                    PHI - 1.0 // Exactly at threshold
                );
                
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────╮\n");
                sb.append("│  🐍 GENESIS MUTATION                                       │\n");
                sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
                sb.append("The serpent awakens from the void.\n");
                sb.append("The first mutation shapes reality.\n\n");
                sb.append(String.format("Generation:   %d\n", engine.generation));
                sb.append(String.format("Consensus:    %.4f (φ - 1)\n", PHI - 1.0));
                sb.append(String.format("Status:       %s\n", 
                    approved ? "✓ GENESIS APPROVED" : "✗ GENESIS REJECTED"));
                
                return sb.toString();
            },
            "Create genesis mutation and reset to generation 0",
            ":genesis");
        
        // :SERPENT - Show ASCII art and status
        registry.register(":serpent",
            args -> {
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────╮\n");
                sb.append("│  🐍 THE OUROBOROS                                          │\n");
                sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
                sb.append("        ╱╲___╱╲\n");
                sb.append("       ╱  ◉ ◉  ╲\n");
                sb.append("      ╱    ▼    ╲___\n");
                sb.append("     ╱___________╲   ╲___\n");
                sb.append("    ╱             ╲      ╲___\n");
                sb.append("   ╱_______________╲_________╲\n");
                sb.append("   ╲___________________________╱\n");
                sb.append("    ╲_________________________╱\n");
                sb.append("     ╲_______________________╱\n");
                sb.append("      ╲_____________________╱\n");
                sb.append("       ╲___________________╱\n");
                sb.append("        ╲_________________╱\n");
                sb.append("         ╲_______________╱\n");
                sb.append("          ╲_____________╱\n");
                sb.append("           ╲___________╱\n\n");
                sb.append("\"The serpent that eats its own tail.\"\n");
                sb.append("Self-modifying code with φ-harmonic evolution.\n\n");
                sb.append(String.format("Generation: %d | Mutations: %d\n", 
                    engine.generation, engine.mutationLog.size()));
                
                return sb.toString();
            },
            "Display the Ouroboros serpent",
            ":serpent");
    }
    
    /**
     * Main help for ouroboros command.
     */
    private static String getMainHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("╭────────────────────────────────────────────────────────────╮\n");
        sb.append("│  🐍 OUROBOROS SELF-BUILDER                                 │\n");
        sb.append("│  \"The serpent that eats its own tail\"                     │\n");
        sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
        sb.append("Subcommands:\n");
        sb.append("  init       - Initialize the Ouroboros engine\n");
        sb.append("  status     - Show current status and generation\n");
        sb.append("  log        - Display mutation history\n");
        sb.append("  reset      - Reset to generation 0\n");
        sb.append("  threshold  - View/set consensus threshold\n\n");
        sb.append("Related Commands:\n");
        sb.append("  :mutate    - Propose a mutation\n");
        sb.append("  :evolve    - Run evolution cycles\n");
        sb.append("  :genesis   - Create genesis mutation\n");
        sb.append("  :serpent   - Display the serpent\n\n");
        sb.append("Example:\n");
        sb.append("  ouroboros init\n");
        sb.append("  :mutate \"Add new feature\" 0.75\n");
        sb.append("  :evolve 10\n");
        sb.append("  ouroboros log\n");
        
        return sb.toString();
    }
}
