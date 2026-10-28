package fraymus.orchestration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command Parser for Fraymus System
 * 
 * Formalizes the "ai " control surface as a Natural Language Instruction Bus.
 * Parses commands with intent, target, method, constraints, and output format.
 * 
 * Canonical format: ai [INTENT] [TARGET] [METHOD] [CONSTRAINTS] [OUTPUT]
 * 
 * Constraints:
 * - Commands must start with "ai "
 * - No escape characters (quotes, backticks, slashes, JSON)
 * - Flat, linear, symbolic text preferred
 */
public class CommandParser {
    
    private static final String PREFIX = "ai ";
    private static final Pattern COMMAND_PATTERN = 
        Pattern.compile("^ai\\s+(\\w+)(?:\\s+(\\S+))?(?:\\s+(.+))?$");
    
    /**
     * Parsed command structure
     */
    public static class ParsedCommand {
        public String raw;
        public String intent;
        public String target;
        public List<String> modifiers;
        public Map<String, String> constraints;
        public String outputFormat;
        
        public ParsedCommand(String raw) {
            this.raw = raw;
            this.modifiers = new ArrayList<>();
            this.constraints = new HashMap<>();
        }
        
        public boolean isValid() {
            return intent != null && !intent.isEmpty();
        }
        
        @Override
        public String toString() {
            return String.format("ParsedCommand{intent=%s, target=%s, modifiers=%s, constraints=%s, output=%s}",
                intent, target, modifiers, constraints, outputFormat);
        }
    }
    
    /**
     * Parse a command string
     */
    public static ParsedCommand parse(String command) {
        if (command == null || command.isEmpty()) {
            return null;
        }
        
        String trimmed = command.trim();
        
        // Check for ai prefix
        if (!trimmed.toLowerCase().startsWith(PREFIX)) {
            return null;
        }
        
        ParsedCommand parsed = new ParsedCommand(trimmed);
        
        // Remove prefix
        String content = trimmed.substring(PREFIX.length()).trim();
        
        // Try structured format: intent=target method=value
        if (content.contains("=")) {
            return parseStructuredFormat(content, parsed);
        }
        
        // Try free format: intent target modifiers
        return parseFreeFormat(content, parsed);
    }
    
    /**
     * Parse structured format: intent=target method=value output=...
     */
    private static ParsedCommand parseStructuredFormat(String content, ParsedCommand parsed) {
        String[] parts = content.split("\\s+");
        
        for (String part : parts) {
            if (part.contains("=")) {
                String[] kv = part.split("=", 2);
                if (kv.length == 2) {
                    String key = kv[0].toLowerCase();
                    String value = kv[1];
                    
                    switch (key) {
                        case "intent":
                            parsed.intent = value;
                            break;
                        case "target":
                            parsed.target = value;
                            break;
                        case "method":
                            parsed.modifiers.add("method:" + value);
                            break;
                        case "output":
                            parsed.outputFormat = value;
                            break;
                        default:
                            parsed.constraints.put(key, value);
                    }
                }
            }
        }
        
        return parsed;
    }
    
    /**
     * Parse free format: intent target modifiers...
     */
    private static ParsedCommand parseFreeFormat(String content, ParsedCommand parsed) {
        String[] parts = content.split("\\s+");
        
        if (parts.length == 0) {
            return parsed;
        }
        
        // First word is intent
        parsed.intent = parts[0].toLowerCase();
        
        // Second word (if exists) is target
        if (parts.length > 1) {
            parsed.target = parts[1];
        }
        
        // Remaining words are modifiers/constraints
        if (parts.length > 2) {
            for (int i = 2; i < parts.length; i++) {
                parsed.modifiers.add(parts[i]);
            }
        }
        
        // Extract output format if present
        for (int i = 0; i < parsed.modifiers.size(); i++) {
            String mod = parsed.modifiers.get(i);
            if (mod.startsWith("output:") || mod.equals("output")) {
                if (mod.startsWith("output:")) {
                    parsed.outputFormat = mod.substring(7);
                } else if (i + 1 < parsed.modifiers.size()) {
                    parsed.outputFormat = parsed.modifiers.get(i + 1);
                    parsed.modifiers.remove(i + 1);
                }
                parsed.modifiers.remove(i);
                break;
            }
        }
        
        return parsed;
    }
    
    /**
     * Check if command is valid ai command
     */
    public static boolean isAiCommand(String command) {
        if (command == null || command.isEmpty()) {
            return false;
        }
        return command.trim().toLowerCase().startsWith(PREFIX);
    }
    
    /**
     * Extract intent from command
     */
    public static String extractIntent(String command) {
        ParsedCommand parsed = parse(command);
        return parsed != null ? parsed.intent : null;
    }
    
    /**
     * Route command to appropriate handler based on intent
     */
    public static String routeCommand(String command) {
        ParsedCommand parsed = parse(command);
        if (parsed == null || !parsed.isValid()) {
            return "unknown";
        }
        
        switch (parsed.intent) {
            case "build":
                return "builder";
            case "analyze":
                return "analyzer";
            case "generate":
                return "generator";
            case "execute":
                return "executor";
            case "test":
                return "tester";
            case "document":
                return "documenter";
            case "fix":
                return "fixer";
            case "optimize":
                return "optimizer";
            case "integrate":
                return "integrator";
            case "deploy":
                return "deployer";
            case "transcend":
                return "hydra";
            case "status":
                if (parsed.target != null) {
                    String target = parsed.target.toLowerCase();
                    if (target.contains("hydra") || target.contains("aubo")) {
                        return "hydra_status";
                    }
                }
                return "status";
            case "hydra":
                return "hydra_status";
            default:
                return "general";
        }
    }
    
    /**
     * Build canonical command string
     */
    public static String buildCanonical(String intent, String target, String method, String output) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX);
        sb.append(intent);
        
        if (target != null && !target.isEmpty()) {
            sb.append(" ").append(target);
        }
        
        if (method != null && !method.isEmpty()) {
            sb.append(" method:").append(method);
        }
        
        if (output != null && !output.isEmpty()) {
            sb.append(" output:").append(output);
        }
        
        return sb.toString();
    }
    
    /**
     * Normalize command to canonical format
     */
    public static String normalize(String command) {
        ParsedCommand parsed = parse(command);
        if (parsed == null || !parsed.isValid()) {
            return command;
        }
        
        return buildCanonical(parsed.intent, parsed.target, 
            getMethodFromModifiers(parsed.modifiers), parsed.outputFormat);
    }
    
    private static String getMethodFromModifiers(List<String> modifiers) {
        for (String mod : modifiers) {
            if (mod.startsWith("method:")) {
                return mod.substring(7);
            }
        }
        return null;
    }
    
    /**
     * Validate command for escape characters
     */
    public static boolean hasEscapeChars(String command) {
        if (command == null) {
            return false;
        }
        
        // Check for problematic characters
        String[] escapePatterns = {"\"", "'", "`", "\\", "{", "}", "[", "]"};
        
        for (String pattern : escapePatterns) {
            if (command.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Sanitize command by removing escape characters
     */
    public static String sanitize(String command) {
        if (command == null) {
            return null;
        }
        
        return command.replaceAll("[\"'`\\\\{}\\[\\]]", "");
    }
}
