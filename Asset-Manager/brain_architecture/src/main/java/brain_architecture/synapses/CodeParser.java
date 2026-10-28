package brain_architecture.synapses;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CodeParser - Parses code files and extracts functions, classes, variables.
 * 
 * Placeholder implementation with regex-based extraction.
 * TODO: Implement AST-based parsing for each language.
 */
public class CodeParser {
    
    private static final Pattern FUNCTION_PATTERN = Pattern.compile(
        "(?:public|private|protected|static|final|synchronized|native|abstract)?\\s+" +
        "(?:\\w+\\s+)+\\s*(\\w+)\\s*\\([^)]*\\)\\s*(?:throws\\s+[\\w\\s,]+)?\\s*\\{"
    );
    
    private static final Pattern CLASS_PATTERN = Pattern.compile(
        "(?:public|private|protected|static|final|abstract)?\\s+" +
        "(?:class|interface|enum)\\s+(\\w+)"
    );
    
    private static final Pattern VARIABLE_PATTERN = Pattern.compile(
        "(?:final\\s+)?(?:\\w+\\s+)+(\\w+)\\s*(?:=|;)"
    );
    
    /**
     * Parse code content and extract meaningful elements.
     */
    public String parse(String codeContent, String language) {
        StringBuilder extracted = new StringBuilder();
        
        // Extract functions
        List<String> functions = extractFunctions(codeContent);
        if (!functions.isEmpty()) {
            extracted.append("Functions:\n");
            for (String func : functions) {
                extracted.append("- ").append(func).append("\n");
            }
            extracted.append("\n");
        }
        
        // Extract classes
        List<String> classes = extractClasses(codeContent);
        if (!classes.isEmpty()) {
            extracted.append("Classes:\n");
            for (String cls : classes) {
                extracted.append("- ").append(cls).append("\n");
            }
            extracted.append("\n");
        }
        
        // Extract comments (simple heuristic)
        String comments = extractComments(codeContent);
        if (!comments.isEmpty()) {
            extracted.append("Comments:\n");
            extracted.append(comments).append("\n");
        }
        
        // If nothing extracted, return original content
        if (extracted.length() == 0) {
            return codeContent;
        }
        
        return extracted.toString();
    }
    
    /**
     * Extract function names from code.
     */
    private List<String> extractFunctions(String codeContent) {
        List<String> functions = new ArrayList<>();
        Matcher matcher = FUNCTION_PATTERN.matcher(codeContent);
        while (matcher.find()) {
            functions.add(matcher.group(1));
        }
        return functions;
    }
    
    /**
     * Extract class/interface names from code.
     */
    private List<String> extractClasses(String codeContent) {
        List<String> classes = new ArrayList<>();
        Matcher matcher = CLASS_PATTERN.matcher(codeContent);
        while (matcher.find()) {
            classes.add(matcher.group(1));
        }
        return classes;
    }
    
    /**
     * Extract comments from code.
     */
    private String extractComments(String codeContent) {
        // Single-line comments
        Pattern singleLineComment = Pattern.compile("//(.*)");
        // Multi-line comments
        Pattern multiLineComment = Pattern.compile("/\\*(.*?)\\*/", Pattern.DOTALL);
        
        StringBuilder comments = new StringBuilder();
        
        Matcher singleMatcher = singleLineComment.matcher(codeContent);
        while (singleMatcher.find()) {
            comments.append(singleMatcher.group(1).trim()).append("\n");
        }
        
        Matcher multiMatcher = multiLineComment.matcher(codeContent);
        while (multiMatcher.find()) {
            comments.append(multiMatcher.group(1).trim()).append("\n");
        }
        
        return comments.toString();
    }
}
