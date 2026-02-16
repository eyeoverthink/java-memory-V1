/**
 * ReplCommand Interface - CSC 413 Enterprise Java Pattern
 * 
 * Functional interface for REPL commands using the Command Pattern.
 * This allows registration in Map<String, ReplCommand> without if/else chains.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.List;

/**
 * Functional interface representing a REPL command.
 * Each command takes arguments and returns a result string.
 */
@FunctionalInterface
public interface ReplCommand {
    
    /**
     * Execute the command with the given arguments.
     * 
     * @param args List of string arguments passed to the command
     * @return Result string to display to the user
     */
    String execute(List<String> args);
    
    /**
     * Get the help text for this command.
     * Default implementation returns a generic message.
     * 
     * @return Help text describing the command
     */
    default String getHelp() {
        return "No help available for this command.";
    }
    
    /**
     * Get the usage syntax for this command.
     * Default implementation returns empty string.
     * 
     * @return Usage syntax string
     */
    default String getUsage() {
        return "";
    }
}
