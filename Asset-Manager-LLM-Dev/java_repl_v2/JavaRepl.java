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
    
    // CSC 413 Required: History and Debug state
    private final List<String> history = new ArrayList<>();
    private boolean debugMode = false;
    
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
        
        // Register built-in commands
        BuiltInCommands.registerAll(registry);
        
        // Register Vaughn Scott's unique commands (φ-harmonic, living code, factoring)
        VaughnScottCommands.registerAll(registry);
        
        // CSC 413 REQUIRED: Colon-prefixed commands
        registerColonCommands();
    }
    
    /**
     * CSC 413 REQUIRED: Register :version, :help, :history, :debug
     */
    private void registerColonCommands() {
        // :version - display version info
        registry.register(":version",
            args -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Fraymus REPL v1.0\n");
                sb.append("Author: Vaughn Scott\n");
                sb.append("Course: CSC 413 - Enterprise Java\n");
                sb.append(String.format("φ^75 Seal: %.2e\n", PHI_SEAL));
                sb.append("Java: " + System.getProperty("java.version"));
                return sb.toString();
            },
            "Display version information",
            ":version");
        
        // :help - display all commands
        registry.register(":help",
            args -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Available Commands:\n");
                sb.append("═══════════════════\n");
                for (String cmd : registry.getCommandNames()) {
                    sb.append("  " + cmd + " - " + registry.getHelp(cmd) + "\n");
                }
                return sb.toString();
            },
            "Display all available commands",
            ":help");
        
        // :history - display command history
        registry.register(":history",
            args -> {
                if (history.isEmpty()) {
                    return "No command history.";
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Command History:\n");
                for (int i = 0; i < history.size(); i++) {
                    sb.append(String.format("  %d: %s\n", i + 1, history.get(i)));
                }
                return sb.toString();
            },
            "Display command history",
            ":history");
        
        // :debug - toggle debug mode
        registry.register(":debug",
            args -> {
                debugMode = !debugMode;
                return "Debug mode: " + (debugMode ? "ON" : "OFF");
            },
            "Toggle debug mode",
            ":debug");
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
        writer.println("Type 'help' for available commands, 'exit' to quit.");
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
                
                // Track history (CSC 413 requirement)
                history.add(line.trim());
                
                // Debug output if enabled
                if (debugMode) {
                    writer.println("[DEBUG] Command: " + command);
                    writer.println("[DEBUG] Args: " + args);
                }
                
                // Execute via registry - NO if/else chain!
                String result = registry.execute(command, args);
                
                // Check for exit signal
                if ("EXIT_SIGNAL".equals(result)) {
                    writer.println("Goodbye! φ-resonance preserved.");
                    running = false;
                } else if (result != null && !result.isEmpty()) {
                    writer.println(result);
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
     * Check if REPL is running.
     * 
     * @return true if running
     */
    public boolean isRunning() {
        return running;
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
