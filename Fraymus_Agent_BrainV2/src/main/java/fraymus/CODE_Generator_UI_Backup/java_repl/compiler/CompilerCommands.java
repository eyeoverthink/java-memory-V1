/**
 * CompilerCommands.java - REPL Integration for Compiler
 * 
 * Registers compiler commands in the REPL:
 * - :compile - Compile and execute code
 * - :lex - Show lexer tokens
 * - :parse - Show AST
 * - :symbols - Show symbol table
 * - :breakpoint - Manage breakpoints
 * - :step - Step through code
 * - :inspect - Inspect variables
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl.compiler;

import repl.ReplCommandRegistry;
import java.util.*;

/**
 * Compiler command registration for REPL.
 */
public class CompilerCommands {
    
    private static final double PHI = 1.618033988749895;
    
    private static Lexer lexer;
    private static Parser parser;
    private static Interpreter interpreter;
    private static Debugger debugger;
    private static ProgramNode lastAST;
    
    /**
     * Register all compiler commands.
     */
    public static void registerAll(ReplCommandRegistry registry) {
        
        // Initialize compiler components
        interpreter = new Interpreter();
        debugger = new Debugger();
        
        // :COMPILE command - Full compilation and execution
        registry.register(":compile",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :compile <code>\nExample: :compile var x = 42; print(x);";
                }
                
                String code = String.join(" ", args);
                
                try {
                    // Lex
                    lexer = new Lexer(code);
                    List<Token> tokens = lexer.tokenize();
                    
                    // Parse
                    parser = new Parser(tokens);
                    lastAST = parser.parse();
                    
                    if (parser.hadErrors()) {
                        StringBuilder sb = new StringBuilder("Parse errors:\n");
                        for (String error : parser.getErrors()) {
                            sb.append("  ").append(error).append("\n");
                        }
                        return sb.toString();
                    }
                    
                    // Execute
                    interpreter.clearOutput();
                    interpreter.execute(lastAST);
                    
                    // Get output
                    StringBuilder sb = new StringBuilder();
                    sb.append("╔════════════════════════════════════════════════════════════╗\n");
                    sb.append("║  COMPILATION SUCCESSFUL                                     ║\n");
                    sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
                    
                    List<String> output = interpreter.getOutput();
                    if (!output.isEmpty()) {
                        sb.append("Output:\n");
                        for (String line : output) {
                            sb.append("  ").append(line).append("\n");
                        }
                    } else {
                        sb.append("(no output)\n");
                    }
                    
                    return sb.toString();
                    
                } catch (Exception e) {
                    return "Compilation error: " + e.getMessage();
                }
            },
            "Compile and execute code",
            ":compile <code>");
        
        // :LEX command - Show tokens
        registry.register(":lex",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :lex <code>\nExample: :lex x = 42 + y";
                }
                
                String code = String.join(" ", args);
                lexer = new Lexer(code);
                List<Token> tokens = lexer.tokenize();
                
                StringBuilder sb = new StringBuilder();
                sb.append("╔════════════════════════════════════════════════════════════╗\n");
                sb.append("║  LEXER OUTPUT                                               ║\n");
                sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
                
                for (Token token : tokens) {
                    sb.append(token.toString()).append("\n");
                }
                
                sb.append("\n").append(lexer.getStatistics());
                
                return sb.toString();
            },
            "Tokenize code and show lexer output",
            ":lex <code>");
        
        // :PARSE command - Show AST
        registry.register(":parse",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :parse <code>\nExample: :parse var x = 42;";
                }
                
                String code = String.join(" ", args);
                
                try {
                    lexer = new Lexer(code);
                    List<Token> tokens = lexer.tokenize();
                    
                    parser = new Parser(tokens);
                    lastAST = parser.parse();
                    
                    if (parser.hadErrors()) {
                        StringBuilder sb = new StringBuilder("Parse errors:\n");
                        for (String error : parser.getErrors()) {
                            sb.append("  ").append(error).append("\n");
                        }
                        return sb.toString();
                    }
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append("╔════════════════════════════════════════════════════════════╗\n");
                    sb.append("║  ABSTRACT SYNTAX TREE                                       ║\n");
                    sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
                    sb.append(lastAST.toDebugString(0));
                    
                    return sb.toString();
                    
                } catch (Exception e) {
                    return "Parse error: " + e.getMessage();
                }
            },
            "Parse code and show AST",
            ":parse <code>");
        
        // :SYMBOLS command - Show symbol table
        registry.register(":symbols",
            args -> {
                return interpreter.getSymbolTable().dump();
            },
            "Show symbol table (memory layout)",
            ":symbols");
        
        // :BREAKPOINT command - Manage breakpoints
        registry.register(":breakpoint",
            args -> {
                if (args.isEmpty()) {
                    Set<Integer> bps = debugger.getBreakpoints();
                    if (bps.isEmpty()) {
                        return "No breakpoints set.\nUsage: :breakpoint <add|remove|clear> [line]";
                    }
                    StringBuilder sb = new StringBuilder("Breakpoints:\n");
                    List<Integer> sorted = new ArrayList<>(bps);
                    Collections.sort(sorted);
                    for (int line : sorted) {
                        sb.append("  Line ").append(line).append("\n");
                    }
                    return sb.toString();
                }
                
                String action = args.get(0).toLowerCase();
                
                switch (action) {
                    case "add":
                        if (args.size() < 2) return "Usage: :breakpoint add <line>";
                        try {
                            int line = Integer.parseInt(args.get(1));
                            debugger.addBreakpoint(line);
                            return "Breakpoint added at line " + line;
                        } catch (NumberFormatException e) {
                            return "Invalid line number";
                        }
                        
                    case "remove":
                        if (args.size() < 2) return "Usage: :breakpoint remove <line>";
                        try {
                            int line = Integer.parseInt(args.get(1));
                            debugger.removeBreakpoint(line);
                            return "Breakpoint removed at line " + line;
                        } catch (NumberFormatException e) {
                            return "Invalid line number";
                        }
                        
                    case "clear":
                        debugger.clearBreakpoints();
                        return "All breakpoints cleared";
                        
                    default:
                        return "Unknown action: " + action + "\nUsage: :breakpoint <add|remove|clear> [line]";
                }
            },
            "Manage breakpoints (add, remove, clear)",
            ":breakpoint [add|remove|clear] [line]");
        
        // :STEP command - Enable/disable stepping
        registry.register(":step",
            args -> {
                if (args.isEmpty() || args.get(0).equalsIgnoreCase("on")) {
                    debugger.enableStepping();
                    return "Step mode: ENABLED\nCode will pause at each line.";
                } else if (args.get(0).equalsIgnoreCase("off")) {
                    debugger.disableStepping();
                    return "Step mode: DISABLED";
                } else {
                    return "Usage: :step [on|off]";
                }
            },
            "Enable/disable step-through debugging",
            ":step [on|off]");
        
        // :INSPECT command - Inspect variable
        registry.register(":inspect",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :inspect <variable>\nExample: :inspect x";
                }
                
                String varName = args.get(0);
                return debugger.inspectVariable(varName, interpreter.getSymbolTable());
            },
            "Inspect variable value and metadata",
            ":inspect <variable>");
        
        // :STACK command - Show call stack
        registry.register(":stack",
            args -> {
                return debugger.getStackTrace();
            },
            "Show call stack trace",
            ":stack");
        
        // :DEBUGGER command - Show debugger status
        registry.register(":debugger",
            args -> {
                return debugger.getStatus();
            },
            "Show debugger status and statistics",
            ":debugger");
        
        // :CONTINUE command - Continue execution after breakpoint
        registry.register(":continue",
            args -> {
                if (!debugger.isPaused()) {
                    return "Not paused. Use :step or :breakpoint to pause execution.";
                }
                debugger.continueExecution();
                return "Continuing execution...";
            },
            "Continue execution after breakpoint",
            ":continue");
    }
}
