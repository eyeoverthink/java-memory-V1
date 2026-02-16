package fraymus;

import fraymus.absorption.LibraryAbsorber;

/**
 * COMMAND TERMINAL: ABSORPTION MODULE
 * 
 * Handles all Universal Absorption / Black Hole Protocol commands:
 * - absorb <package>       - Absorb Java package
 * - absorb jar <path>      - Absorb JAR file
 * - absorb url <url>       - Absorb web content
 * - query <term>           - Search absorbed knowledge
 * - execute <skill> <args> - Execute absorbed skill
 * - absorber stats         - Show absorption statistics
 */
public class CommandTerminalAbsorption {
    
    private static LibraryAbsorber absorber = null;
    
    /**
     * Handle all absorption-related commands
     */
    public static void handle(String command, String args) {
        // Lazy initialization
        if (absorber == null) {
            CommandTerminal.printInfo("Initializing Black Hole Protocol...");
            absorber = new LibraryAbsorber();
        }
        
        String[] parts = args.split("\\s+", 2);
        String subCommand = parts.length > 0 ? parts[0] : "";
        String value = parts.length > 1 ? parts[1] : "";
        
        switch (command) {
            case "absorb":
                handleAbsorb(subCommand, value);
                break;
                
            case "query":
                handleQuery(args);
                break;
                
            case "execute":
                handleExecute(args);
                break;
                
            case "absorber":
                handleAbsorber(subCommand);
                break;
                
            default:
                CommandTerminal.printError("Unknown absorption command: " + command);
        }
    }
    
    /**
     * Handle absorb command
     */
    private static void handleAbsorb(String type, String target) {
        if (type.isEmpty()) {
            CommandTerminal.printError("Usage: absorb <package|jar|url> <target>");
            CommandTerminal.print("Examples:");
            CommandTerminal.print("  absorb package java.util");
            CommandTerminal.print("  absorb jar /path/to/library.jar");
            CommandTerminal.print("  absorb url https://example.com/api");
            return;
        }
        
        switch (type.toLowerCase()) {
            case "package":
            case "pkg":
                if (target.isEmpty()) {
                    CommandTerminal.printError("Usage: absorb package <package.name>");
                    return;
                }
                absorber.absorb(target);
                break;
                
            case "jar":
                if (target.isEmpty()) {
                    CommandTerminal.printError("Usage: absorb jar <path/to/file.jar>");
                    return;
                }
                absorber.absorbJar(target);
                break;
                
            case "url":
            case "web":
                if (target.isEmpty()) {
                    CommandTerminal.printError("Usage: absorb url <https://...>");
                    return;
                }
                absorber.absorbUrl(target);
                break;
                
            default:
                // Assume it's a package name
                absorber.absorb(type + (target.isEmpty() ? "" : " " + target));
                break;
        }
    }
    
    /**
     * Handle query command
     */
    private static void handleQuery(String searchTerm) {
        if (searchTerm.isEmpty()) {
            CommandTerminal.printError("Usage: query <search_term>");
            CommandTerminal.print("Example: query sqrt");
            return;
        }
        
        String results = absorber.query(searchTerm);
        System.out.println(results);
    }
    
    /**
     * Handle execute command
     */
    private static void handleExecute(String skillAndArgs) {
        if (skillAndArgs.isEmpty()) {
            CommandTerminal.printError("Usage: execute <skill> [args...]");
            CommandTerminal.print("Example: execute sqrt 16");
            return;
        }
        
        String[] parts = skillAndArgs.split("\\s+");
        String skill = parts[0];
        
        // Parse arguments
        Object[] args = new Object[parts.length - 1];
        for (int i = 1; i < parts.length; i++) {
            // Try to parse as number
            try {
                if (parts[i].contains(".")) {
                    args[i - 1] = Double.parseDouble(parts[i]);
                } else {
                    args[i - 1] = Integer.parseInt(parts[i]);
                }
            } catch (NumberFormatException e) {
                // Keep as string
                args[i - 1] = parts[i];
            }
        }
        
        absorber.execute(skill, args);
    }
    
    /**
     * Handle absorber subcommands
     */
    private static void handleAbsorber(String subCommand) {
        switch (subCommand.toLowerCase()) {
            case "stats":
            case "status":
                absorber.showStats();
                break;
                
            case "help":
                showHelp();
                break;
                
            default:
                CommandTerminal.printError("Unknown absorber command: " + subCommand);
                CommandTerminal.print("Try: absorber help");
        }
    }
    
    /**
     * Show absorption help
     */
    private static void showHelp() {
        CommandTerminal.printColored("\n=== UNIVERSAL ABSORPTION (BLACK HOLE PROTOCOL) ===\n", 0.0f, 1.0f, 1.0f);
        
        CommandTerminal.printColored("ABSORB COMMANDS:", 1.0f, 0.84f, 0.0f);
        CommandTerminal.print("  absorb package <name>    Absorb Java package (e.g., java.util)");
        CommandTerminal.print("  absorb jar <path>        Absorb entire JAR file");
        CommandTerminal.print("  absorb url <url>         Absorb web content");
        CommandTerminal.print("");
        
        CommandTerminal.printColored("QUERY COMMANDS:", 1.0f, 0.84f, 0.0f);
        CommandTerminal.print("  query <term>             Search absorbed knowledge");
        CommandTerminal.print("  execute <skill> [args]   Execute absorbed method");
        CommandTerminal.print("");
        
        CommandTerminal.printColored("INFO COMMANDS:", 1.0f, 0.84f, 0.0f);
        CommandTerminal.print("  absorber stats           Show absorption statistics");
        CommandTerminal.print("  absorber help            Show this help");
        CommandTerminal.print("");
        
        CommandTerminal.printColored("EXAMPLES:", 0.0f, 1.0f, 0.0f);
        CommandTerminal.print("  absorb package java.util");
        CommandTerminal.print("  query add");
        CommandTerminal.print("  execute sqrt 16");
        CommandTerminal.print("  absorber stats");
        CommandTerminal.print("");
        
        CommandTerminal.printColored("CONCEPT:", 0.5f, 0.5f, 1.0f);
        CommandTerminal.print("  We don't just USE libraries. We ABSORB them.");
        CommandTerminal.print("  Point FRAYMUS at ANY library and it ingests it,");
        CommandTerminal.print("  strips it for parts, and integrates it into its body.");
        CommandTerminal.print("  Then you can query and execute via natural language.");
        CommandTerminal.print("");
        CommandTerminal.print("  This is Universal Integration.");
        CommandTerminal.print("  This is The Black Hole Protocol.");
        CommandTerminal.print("");
    }
}
