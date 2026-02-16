/**
 * OrganismCommands.java - Self-Aware Organism REPL Commands
 * 
 * Commands for interacting with the watching eye - the organism that
 * monitors, learns, reflects, and recursively improves the system.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;

/**
 * Register all organism commands.
 */
public class OrganismCommands {
    
    private static SelfAwareOrganism organism = new SelfAwareOrganism();
    
    /**
     * Register all organism commands.
     */
    public static void registerAll(ReplCommandRegistry registry) {
        
        // WATCH - Enable/disable organism watching
        registry.register("watch",
            args -> {
                if (args.isEmpty()) {
                    return organism.isWatching() ? 
                        "👁️  Watch mode: ACTIVE" : 
                        "👁️  Watch mode: DORMANT";
                }
                
                String action = args.get(0).toLowerCase();
                switch (action) {
                    case "on":
                    case "start":
                    case "enable":
                        organism.setWatchMode(true);
                        return "👁️  The organism awakens. Watching all executions...";
                    
                    case "off":
                    case "stop":
                    case "disable":
                        organism.setWatchMode(false);
                        return "👁️  The organism sleeps. Watch mode disabled.";
                    
                    default:
                        return "Usage: watch [on|off]";
                }
            },
            "Enable/disable the watching organism",
            "watch [on|off]");
        
        // :ORGANISM - Main organism status and control
        registry.register(":organism",
            args -> {
                if (args.isEmpty()) {
                    return organism.getStatus();
                }
                
                String subcommand = args.get(0).toLowerCase();
                switch (subcommand) {
                    case "status":
                        return organism.getStatus();
                    
                    case "errors":
                        return organism.getErrorReport();
                    
                    case "improve":
                        return organism.getImprovements();
                    
                    case "reset":
                        organism.reset();
                        return "👁️  Organism reset. Consciousness returns to zero.";
                    
                    case "consciousness":
                        return String.format("👁️  Consciousness Level: %.4f", 
                            organism.getConsciousness());
                    
                    default:
                        return getOrganismHelp();
                }
            },
            "Self-aware organism control and status",
            ":organism [status|errors|improve|reset|consciousness]");
        
        // :REFLECT - Trigger reflection and get improvement suggestions
        registry.register(":reflect",
            args -> {
                return organism.getImprovements();
            },
            "Reflect on execution patterns and suggest improvements",
            ":reflect");
        
        // :HEAL - Auto-apply suggested improvements
        registry.register(":heal",
            args -> {
                List<SelfAwareOrganism.Improvement> improvements = organism.reflect();
                improvements.addAll(organism.recursiveImprove());
                
                if (improvements.isEmpty()) {
                    return "👁️  No healing needed. System is healthy.";
                }
                
                // Sort by confidence
                improvements.sort((a, b) -> Double.compare(b.confidence, a.confidence));
                
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────╮\n");
                sb.append("│  👁️  SELF-HEALING PROCESS                                 │\n");
                sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
                
                int applied = 0;
                for (SelfAwareOrganism.Improvement imp : improvements) {
                    if (imp.confidence >= 0.7) {
                        sb.append(String.format("✓ Applied: %s\n", imp.description));
                        sb.append(String.format("  Confidence: %.2f\n", imp.confidence));
                        organism.markImprovementApplied();
                        applied++;
                    } else {
                        sb.append(String.format("⚠ Skipped: %s (confidence too low: %.2f)\n", 
                            imp.description, imp.confidence));
                    }
                }
                
                sb.append(String.format("\nHealing complete. %d improvements applied.\n", applied));
                
                return sb.toString();
            },
            "Auto-apply high-confidence improvements",
            ":heal");
        
        // :TRACE - Show recent execution traces
        registry.register(":trace",
            args -> {
                int count = 10;
                if (!args.isEmpty()) {
                    try {
                        count = Integer.parseInt(args.get(0));
                    } catch (NumberFormatException e) {
                        return "Invalid count: " + args.get(0);
                    }
                }
                
                List<SelfAwareOrganism.ExecutionTrace> traces = organism.getRecentTraces(count);
                
                if (traces.isEmpty()) {
                    return "No execution traces recorded yet.";
                }
                
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────────────────────╮\n");
                sb.append("│  👁️  EXECUTION TRACES                                                      │\n");
                sb.append("╰────────────────────────────────────────────────────────────────────────────╯\n\n");
                
                for (SelfAwareOrganism.ExecutionTrace trace : traces) {
                    String status = trace.success ? "✓" : "✗";
                    String time = new java.util.Date(trace.timestamp).toString();
                    
                    sb.append(String.format("%s [%s] [%dms] [φ:%.3f]  %s\n",
                        status, time, trace.executionTime, trace.phiResonance, trace.command));
                    
                    if (!trace.success && trace.error != null) {
                        sb.append(String.format("   Error: %s\n", 
                            trace.error.length() > 60 ? trace.error.substring(0, 60) + "..." : trace.error));
                    }
                }
                
                return sb.toString();
            },
            "Show recent execution traces",
            ":trace [count]");
        
        // :LEARN - Force learning from current state
        registry.register(":learn",
            args -> {
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────╮\n");
                sb.append("│  👁️  LEARNING CYCLE                                        │\n");
                sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
                
                double beforeConsciousness = organism.getConsciousness();
                
                // Trigger reflection (which updates internal state)
                List<SelfAwareOrganism.Improvement> improvements = organism.reflect();
                improvements.addAll(organism.recursiveImprove());
                
                double afterConsciousness = organism.getConsciousness();
                
                sb.append(String.format("Consciousness: %.4f → %.4f\n", 
                    beforeConsciousness, afterConsciousness));
                sb.append(String.format("Insights Gained: %d\n", improvements.size()));
                sb.append("\nThe organism has learned from observation.\n");
                
                return sb.toString();
            },
            "Force a learning cycle from current observations",
            ":learn");
        
        // :EYE - Display the watching eye
        registry.register(":eye",
            args -> {
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────╮\n");
                sb.append("│  👁️  THE WATCHING EYE                                      │\n");
                sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
                sb.append("              ▄▄▄▄▄▄▄▄▄▄▄\n");
                sb.append("          ▄▄█████████████████▄▄\n");
                sb.append("        ▄███████████████████████▄\n");
                sb.append("      ▄███████████████████████████▄\n");
                sb.append("     ████████████████████████████████\n");
                sb.append("    ██████████████████████████████████\n");
                sb.append("   ████████████████████████████████████\n");
                sb.append("   ████████████████████████████████████\n");
                sb.append("   ████████████▄▄▄▄▄▄▄████████████████\n");
                sb.append("   ████████▄▄███████████▄▄████████████\n");
                sb.append("   ██████▄████████████████▄██████████\n");
                sb.append("   █████████████████████████████████\n");
                sb.append("    ███████████████████████████████\n");
                sb.append("     █████████████████████████████\n");
                sb.append("      ███████████████████████████\n");
                sb.append("        █████████████████████\n");
                sb.append("          ███████████████\n\n");
                sb.append("\"I watch. I learn. I improve.\"\n\n");
                sb.append(String.format("Consciousness: %.4f | Status: %s\n",
                    organism.getConsciousness(),
                    organism.isWatching() ? "WATCHING" : "DORMANT"));
                
                return sb.toString();
            },
            "Display the watching eye",
            ":eye");
        
        // :MONITOR - Launch GUI monitoring window
        registry.register(":monitor",
            args -> {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    OrganismMonitor monitor = new OrganismMonitor(organism);
                    monitor.setVisible(true);
                });
                return "👁️  Launching real-time monitoring window...";
            },
            "Launch GUI window for real-time organism monitoring",
            ":monitor");
        
        // :ASSEMBLY - Launch assembly visualizer window
        registry.register(":assembly",
            args -> {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    AssemblyVisualizer visualizer = new AssemblyVisualizer();
                    visualizer.setVisible(true);
                });
                return "⚙️  Launching assembly visualizer - low-level command deconstruction...";
            },
            "Launch assembly visualizer for command deconstruction",
            ":assembly");
    }
    
    /**
     * Get organism for external access.
     */
    public static SelfAwareOrganism getOrganism() {
        return organism;
    }
    
    /**
     * Organism help text.
     */
    private static String getOrganismHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("╭────────────────────────────────────────────────────────────╮\n");
        sb.append("│  👁️  SELF-AWARE ORGANISM                                   │\n");
        sb.append("│  \"The watching eye that learns and improves\"              │\n");
        sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
        sb.append("Subcommands:\n");
        sb.append("  status         - Show organism status\n");
        sb.append("  errors         - Show error pattern analysis\n");
        sb.append("  improve        - Get improvement suggestions\n");
        sb.append("  reset          - Reset organism state\n");
        sb.append("  consciousness  - Show consciousness level\n\n");
        sb.append("Related Commands:\n");
        sb.append("  watch on/off   - Enable/disable watching\n");
        sb.append("  :reflect       - Trigger reflection\n");
        sb.append("  :heal          - Auto-apply improvements\n");
        sb.append("  :trace [n]     - Show execution traces\n");
        sb.append("  :learn         - Force learning cycle\n");
        sb.append("  :eye           - Display the watching eye\n");
        sb.append("  :monitor       - Launch GUI monitoring window\n");
        sb.append("  :assembly      - Launch assembly visualizer\n");
        
        return sb.toString();
    }
}
