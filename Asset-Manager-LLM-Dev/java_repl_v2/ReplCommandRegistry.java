/**
 * ReplCommandRegistry - CSC 413 Enterprise Java Pattern
 * 
 * Registry pattern using Map<String, ReplCommand> for command lookup.
 * NO if/else chains - commands are registered dynamically.
 * 
 * This demonstrates:
 * - Registry Pattern
 * - Functional Interfaces (Java 8+)
 * - Lambda expressions
 * - Method references
 * - Open/Closed Principle (open for extension, closed for modification)
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;
import java.util.function.Function;

/**
 * Central registry for all REPL commands.
 * Commands are stored in a Map and looked up by name - no if/else needed.
 */
public class ReplCommandRegistry {
    
    // φ-Harmonic constants (from Fraymus architecture)
    private static final double PHI = 1.618033988749895;
    private static final double PHI_75 = Math.pow(PHI, 7.5);
    
    // The command registry - THIS is the key pattern for CSC 413
    // Map<String, ReplCommand> replaces giant if/else blocks
    private final Map<String, ReplCommand> commands;
    
    // Help text registry
    private final Map<String, String> helpTexts;
    
    // Usage syntax registry
    private final Map<String, String> usageTexts;
    
    /**
     * Initialize the registry with an empty command map.
     */
    public ReplCommandRegistry() {
        this.commands = new HashMap<>();
        this.helpTexts = new HashMap<>();
        this.usageTexts = new HashMap<>();
    }
    
    /**
     * Register a command with the registry.
     * This is the ALTERNATIVE to if/else chains.
     * 
     * @param name Command name (what user types)
     * @param command The ReplCommand implementation
     * @return this (for fluent chaining)
     */
    public ReplCommandRegistry register(String name, ReplCommand command) {
        commands.put(name.toLowerCase(), command);
        return this;
    }
    
    /**
     * Register a command with help text.
     * 
     * @param name Command name
     * @param command The ReplCommand implementation
     * @param help Help text for the command
     * @param usage Usage syntax
     * @return this (for fluent chaining)
     */
    public ReplCommandRegistry register(String name, ReplCommand command, 
                                         String help, String usage) {
        commands.put(name.toLowerCase(), command);
        helpTexts.put(name.toLowerCase(), help);
        usageTexts.put(name.toLowerCase(), usage);
        return this;
    }
    
    /**
     * Register a command using a lambda or method reference.
     * Demonstrates Java 8 functional programming.
     * 
     * @param name Command name
     * @param handler Function that takes args and returns result
     * @return this (for fluent chaining)
     */
    public ReplCommandRegistry registerLambda(String name, 
            Function<List<String>, String> handler) {
        commands.put(name.toLowerCase(), args -> handler.apply(args));
        return this;
    }
    
    /**
     * Execute a command by name.
     * NO if/else - just map lookup!
     * 
     * @param name Command name
     * @param args Arguments to pass
     * @return Result string, or error if command not found
     */
    public String execute(String name, List<String> args) {
        ReplCommand command = commands.get(name.toLowerCase());
        
        if (command == null) {
            return "Unknown command: " + name + ". Type 'help' for available commands.";
        }
        
        try {
            return command.execute(args);
        } catch (Exception e) {
            return "Error executing '" + name + "': " + e.getMessage();
        }
    }
    
    /**
     * Check if a command exists.
     * 
     * @param name Command name
     * @return true if registered
     */
    public boolean hasCommand(String name) {
        return commands.containsKey(name.toLowerCase());
    }
    
    /**
     * Get all registered command names.
     * 
     * @return Set of command names
     */
    public Set<String> getCommandNames() {
        return Collections.unmodifiableSet(commands.keySet());
    }
    
    /**
     * Get help text for a command.
     * 
     * @param name Command name
     * @return Help text or default message
     */
    public String getHelp(String name) {
        return helpTexts.getOrDefault(name.toLowerCase(), 
            "No help available for '" + name + "'");
    }
    
    /**
     * Get usage syntax for a command.
     * 
     * @param name Command name
     * @return Usage syntax
     */
    public String getUsage(String name) {
        return usageTexts.getOrDefault(name.toLowerCase(), name);
    }
    
    /**
     * Get the number of registered commands.
     * 
     * @return Command count
     */
    public int size() {
        return commands.size();
    }
    
    /**
     * Unregister a command.
     * 
     * @param name Command name to remove
     * @return true if removed, false if didn't exist
     */
    public boolean unregister(String name) {
        String key = name.toLowerCase();
        helpTexts.remove(key);
        usageTexts.remove(key);
        return commands.remove(key) != null;
    }
    
    /**
     * Get φ-resonance value for this registry.
     * Demonstrates integration with Fraymus architecture.
     * 
     * @return φ^7.5 resonance based on command count
     */
    public double getPhiResonance() {
        return PHI_75 * (1.0 + commands.size() * 0.01);
    }
}
