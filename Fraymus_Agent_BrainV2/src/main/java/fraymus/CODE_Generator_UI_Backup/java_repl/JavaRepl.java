/**
 * JavaRepl - CSC 413 Enterprise Java Pattern
 * 
 * Main REPL driver class demonstrating:
 * - Command Registry Pattern (Map<String, ReplCommand>)
 * - NO if/else chains for command dispatch
 * - Functional interfaces and lambdas
 * - Clean separation of concerns
 * - Input/Output stream handling
 * 
 * This is the ENTERPRISE way to build a REPL - extensible, testable, maintainable.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.io.*;
import java.util.*;

/**
 * Main REPL (Read-Eval-Print-Loop) implementation.
 * Uses ReplCommandRegistry for command dispatch instead of if/else chains.
 */
public class JavaRepl {
    
    // φ-Harmonic constants
    private static final double PHI = 1.618033988749895;
    private static final double PHI_SEAL = Math.pow(PHI, 75);
    
    // The command registry - THE key pattern
    private final ReplCommandRegistry registry;
    
    // I/O streams (Enterprise pattern - injectable for testing)
    private final BufferedReader reader;
    private final PrintWriter writer;
    
    // REPL state
    private boolean running;
    private String prompt;
    private List<String> commandHistory;
    private boolean debugMode;
    private HistoryManager historyManager;
    
    /**
     * Create REPL with standard I/O.
     */
    public JavaRepl() {
        this(new BufferedReader(new InputStreamReader(System.in)),
             new PrintWriter(System.out, true));
    }
    
    /**
     * Create REPL with custom I/O streams.
     * This is the ENTERPRISE pattern - dependency injection for testability.
     * 
     * @param reader Input stream
     * @param writer Output stream
     */
    public JavaRepl(BufferedReader reader, PrintWriter writer) {
        this.reader = reader;
        this.writer = writer;
        this.registry = new ReplCommandRegistry();
        this.running = false;
        this.prompt = "φ> ";
        this.commandHistory = new ArrayList<>();
        this.debugMode = false;
        this.historyManager = new HistoryManager();
        
        // Commands will be registered externally to avoid circular dependencies
    }
    
    /**
     * Get the command registry for external command registration.
     * 
     * @return The command registry
     */
    public ReplCommandRegistry getRegistry() {
        return registry;
    }
    
    /**
     * Get the history manager.
     * 
     * @return The history manager
     */
    public HistoryManager getHistoryManager() {
        return historyManager;
    }
    
    /**
     * Set the REPL prompt.
     * 
     * @param prompt New prompt string
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    /**
     * Print the welcome banner.
     */
    private void printBanner() {
        writer.println("╔════════════════════════════════════════════════════════════╗");
        writer.println("║           Java REPL - CSC 413 Enterprise Patterns          ║");
        writer.println("║                                                            ║");
        writer.println("║  Command Registry Pattern - NO if/else chains!             ║");
        writer.println("║  Commands registered via Map<String, ReplCommand>          ║");
        writer.println("║                                                            ║");
        writer.println(String.format("║  φ^75 Validation Seal: %.2e                   ║", PHI_SEAL));
        writer.println("╚════════════════════════════════════════════════════════════╝");
        writer.println();
        writer.println("CSC 413 Required Commands: :version | :help | :history | :debug");
        writer.println("Type ':help' for all commands, 'exit' to quit.");
        writer.println();
    }
    
    /**
     * Parse input line into command and arguments.
     * 
     * @param line Input line
     * @return Array where [0] is command, rest are arguments
     */
    private String[] parseInput(String line) {
        if (line == null || line.trim().isEmpty()) {
            return new String[0];
        }
        return line.trim().split("\\s+");
    }
    
    /**
     * Run the REPL loop.
     * This is the main entry point.
     */
    public void run() {
        printBanner();
        running = true;
        
        while (running) {
            try {
                // Print prompt
                writer.print(prompt);
                writer.flush();
                
                // Read input
                String line = reader.readLine();
                
                // Handle EOF
                if (line == null) {
                    writer.println("\nGoodbye!");
                    break;
                }
                
                // Parse input
                String[] parts = parseInput(line);
                if (parts.length == 0) {
                    continue;
                }
                
                // Extract command and arguments
                String command = parts[0];
                List<String> args = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    args.add(parts[i]);
                }
                
                // Track command history
                commandHistory.add(line);
                
                // Debug output
                if (debugMode) {
                    writer.println("[DEBUG] Command: '" + command + "' Args: " + args);
                }
                
                // Execute via registry - NO if/else chain!
                long startTime = System.currentTimeMillis();
                String result = registry.execute(command, args);
                long executionTime = System.currentTimeMillis() - startTime;
                
                // Broadcast to activity bus
                boolean success = result != null && !result.contains("Error") && !result.contains("error");
                ActivityBus.commandActivity(line, success, executionTime);
                
                // Calculate phi resonance for command
                double phiResonance = calculatePhiResonance(line);
                
                // Let organism observe execution (if watching)
                SelfAwareOrganism organism = OrganismCommands.getOrganism();
                if (organism.isWatching()) {
                    String error = success ? null : result;
                    organism.observe(line, executionTime, success, result, error, phiResonance);
                }
                
                // Save to history manager
                historyManager.addCommand(line, result, executionTime);
                
                // Check for exit signal
                if ("EXIT_SIGNAL".equals(result)) {
                    writer.println("Goodbye! φ-resonance preserved.");
                    running = false;
                } else if (result != null && !result.isEmpty()) {
                    // Check for replay command
                    if (result.startsWith("REPLAY:")) {
                        String replayCmd = result.substring(7);
                        writer.println("Replaying: " + replayCmd);
                        // Re-parse and execute the replayed command
                        String[] replayParts = parseInput(replayCmd);
                        if (replayParts.length > 0) {
                            String replayCommand = replayParts[0];
                            List<String> replayArgs = new ArrayList<>();
                            for (int i = 1; i < replayParts.length; i++) {
                                replayArgs.add(replayParts[i]);
                            }
                            String replayResult = registry.execute(replayCommand, replayArgs);
                            if (replayResult != null && !replayResult.isEmpty()) {
                                writer.println(replayResult);
                            }
                        }
                    } else {
                        writer.println(result);
                    }
                }
                
            } catch (IOException e) {
                writer.println("I/O Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Stop the REPL.
     */
    public void stop() {
        running = false;
    }
    
    /**
     * Calculate φ-harmonic resonance of a command.
     */
    private double calculatePhiResonance(String command) {
        if (command == null || command.isEmpty()) return 0.0;
        
        double resonance = 0.0;
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            resonance += (c * Math.pow(PHI, (i % 7) / 7.0)) % 1.0;
        }
        
        return (resonance / command.length()) * PHI;
    }
    
    /**
     * Check if REPL is running.
     * 
     * @return true if running
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Get command history.
     * 
     * @return List of executed commands
     */
    public List<String> getCommandHistory() {
        return new ArrayList<>(commandHistory);
    }
    
    /**
     * Check if debug mode is enabled.
     * 
     * @return true if debug mode is on
     */
    public boolean isDebugMode() {
        return debugMode;
    }
    
    /**
     * Toggle debug mode.
     * 
     * @return new debug mode state
     */
    public boolean toggleDebugMode() {
        debugMode = !debugMode;
        return debugMode;
    }
    
    /**
     * Execute a single command (for testing or scripting).
     * 
     * @param commandLine Full command line
     * @return Result string
     */
    public String executeCommand(String commandLine) {
        String[] parts = parseInput(commandLine);
        if (parts.length == 0) {
            return "";
        }
        
        String command = parts[0];
        List<String> args = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            args.add(parts[i]);
        }
        
        return registry.execute(command, args);
    }
    
    /**
     * Main entry point.
     * 
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        JavaRepl repl = new JavaRepl();
        
        // Demonstrate extensibility - register a custom command
        repl.getRegistry().register("greet",
            cmdArgs -> {
                String name = cmdArgs.isEmpty() ? "World" : String.join(" ", cmdArgs);
                return "Hello, " + name + "! φ-resonance: " + PHI;
            },
            "Greet someone with φ-harmonic resonance",
            "greet [name]");
        
        // Run the REPL
        repl.run();
    }
}
