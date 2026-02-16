package fraymus.agent;

import java.util.*;

/**
 * CODING PROMPT INTERFACE
 * 
 * Simple command-line interface for coding requests.
 * 
 * Commands:
 * > code: <request>           - Generate code
 * > code python: <request>    - Specify language
 * > code show                 - Show last generated code
 * > code stats                - Show statistics
 * > code history              - Show request history
 * 
 * Examples:
 * > code: write a function to calculate fibonacci
 * > code python: create a class for binary search tree
 * > code java: implement quicksort algorithm
 * > code c++: write a linked list with insert and delete
 */
public class CodingPrompt {
    
    private final CodingAgent agent;
    
    public CodingPrompt(CodingAgent agent) {
        this.agent = agent;
    }
    
    /**
     * Process coding command
     */
    public String processCommand(String command) {
        command = command.trim();
        
        // Remove "code:" or "code " prefix
        if (command.toLowerCase().startsWith("code:")) {
            command = command.substring(5).trim();
        } else if (command.toLowerCase().startsWith("code ")) {
            command = command.substring(5).trim();
        }
        
        // Handle special commands
        if (command.equalsIgnoreCase("show")) {
            return agent.showLast();
        }
        
        if (command.equalsIgnoreCase("stats")) {
            return agent.getStats();
        }
        
        if (command.equalsIgnoreCase("history")) {
            return showHistory();
        }
        
        if (command.equalsIgnoreCase("help")) {
            return showHelp();
        }
        
        // Generate code
        String code = agent.code(command);
        
        return formatCodeResponse(code);
    }
    
    /**
     * Format code response for display
     */
    private String formatCodeResponse(String code) {
        StringBuilder response = new StringBuilder();
        
        response.append("🤖 GENERATED CODE:\n\n");
        response.append("```\n");
        response.append(code);
        response.append("\n```\n\n");
        response.append("Type 'code show' to see this again\n");
        response.append("Type 'code stats' for statistics\n");
        
        return response.toString();
    }
    
    /**
     * Show request history
     */
    private String showHistory() {
        List<CodingAgent.CodingRequest> history = agent.getHistory();
        
        if (history.isEmpty()) {
            return "No coding requests yet";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("🤖 CODING HISTORY\n\n");
        
        for (CodingAgent.CodingRequest req : history) {
            sb.append(String.format("#%d [%s] %s (%dms) %s\n",
                req.id,
                req.language,
                req.prompt.substring(0, Math.min(50, req.prompt.length())),
                req.duration,
                req.valid ? "✓" : "✗"
            ));
        }
        
        return sb.toString();
    }
    
    /**
     * Show help
     */
    private String showHelp() {
        return """
            🤖 CODING AGENT HELP
            
            Commands:
              code: <request>           - Generate code from natural language
              code python: <request>    - Specify language explicitly
              code show                 - Show last generated code
              code stats                - Show statistics
              code history              - Show request history
              code help                 - Show this help
            
            Supported Languages:
              - Python
              - Java
              - C++
              - JavaScript
              - TypeScript
              - Rust
              - Go
              - C#
            
            Examples:
              > code: write a function to calculate fibonacci
              > code python: create a class for binary search tree
              > code java: implement quicksort algorithm
              > code c++: write a linked list with insert and delete
              > code rust: create a hashmap with custom hash function
            
            The agent will:
              1. Understand your request (Ollama)
              2. Query knowledge base for examples
              3. Generate code using real patterns
              4. Refine through chain of density
              5. Validate with process reward model
              6. Return working code
            
            Powered by:
              - Ollama (KAI) for language understanding
              - Knowledge Base for real examples
              - Chain of Density for refinement
              - Process Reward Model for validation
              - Dynamic Sampling for optimal generation
            """;
    }
}
