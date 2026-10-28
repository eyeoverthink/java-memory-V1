package fraymus.brain.unified;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * Code Absorber - Absorbs external source code patterns and generates new implementations.
 * 
 * This enables the system to:
 * - Absorb: Read source code from external projects
 * - Analyze: Extract patterns, structure, and relationships
 * - Generate: Create new implementations based on learned patterns
 * - Grow: Extend beyond original patterns with new capabilities
 */
public class CodeAbsorber {
    
    private final Map<String, ClassPattern> absorbedClasses = new HashMap<>();
    private final Map<String, EnumPattern> absorbedEnums = new HashMap<>();
    
    /**
     * Absorb a Java source file
     */
    public void absorbFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("File not found: " + filePath);
                return;
            }
            
            String code = Files.readString(file.toPath());
            analyzeCode(code, filePath);
            
        } catch (Exception e) {
            System.err.println("Error absorbing file: " + e.getMessage());
        }
    }
    
    /**
     * Absorb all Java files in a directory
     */
    public void absorbDirectory(String dirPath) {
        try {
            File dir = new File(dirPath);
            if (!dir.exists() || !dir.isDirectory()) {
                System.err.println("Directory not found: " + dirPath);
                return;
            }
            
            Files.walk(dir.toPath())
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> absorbFile(p.toString()));
                
        } catch (Exception e) {
            System.err.println("Error absorbing directory: " + e.getMessage());
        }
    }
    
    /**
     * Analyze absorbed code and extract patterns
     */
    private void analyzeCode(String code, String sourcePath) {
        // Check for enum
        if (code.contains("enum")) {
            EnumPattern pattern = extractEnumPattern(code, sourcePath);
            if (pattern != null) {
                absorbedEnums.put(pattern.name, pattern);
                System.out.println("  ✓ Absorbed enum: " + pattern.name + " with " + pattern.values.size() + " values");
            }
        }
        
        // Check for class
        if (code.contains("class")) {
            ClassPattern pattern = extractClassPattern(code, sourcePath);
            if (pattern != null) {
                absorbedClasses.put(pattern.name, pattern);
                System.out.println("  ✓ Absorbed class: " + pattern.name + " with " + pattern.methods.size() + " methods");
            }
        }
    }
    
    /**
     * Extract enum pattern from code
     */
    private EnumPattern extractEnumPattern(String code, String sourcePath) {
        // Find enum declaration and body
        Pattern enumPattern = Pattern.compile("enum\\s+(\\w+)\\s*\\{");
        Matcher enumMatcher = enumPattern.matcher(code);
        
        if (!enumMatcher.find()) {
            return null;
        }
        
        String name = enumMatcher.group(1);
        int startIndex = enumMatcher.end();
        
        // Find matching closing brace
        int braceCount = 1;
        int endIndex = startIndex;
        for (int i = startIndex; i < code.length() && braceCount > 0; i++) {
            char c = code.charAt(i);
            if (c == '{') braceCount++;
            else if (c == '}') braceCount--;
            if (braceCount == 0) {
                endIndex = i;
                break;
            }
        }
        
        String enumBody = code.substring(startIndex, endIndex);
        
        // Extract enum values (simple names, ignoring constructors/fields)
        List<String> values = new ArrayList<>();
        Pattern valuePattern = Pattern.compile("\\b([A-Z_][A-Z0-9_]*)\\b");
        Matcher valueMatcher = valuePattern.matcher(enumBody);
        
        Set<String> seen = new HashSet<>();
        while (valueMatcher.find()) {
            String value = valueMatcher.group(1);
            // Filter out common non-enum words
            if (!value.equals("TRUE") && !value.equals("FALSE") && 
                !value.equals("NULL") && !value.equals("STRING") &&
                !seen.contains(value)) {
                values.add(value);
                seen.add(value);
            }
        }
        
        return new EnumPattern(name, values, sourcePath);
    }
    
    /**
     * Extract class pattern from code
     */
    private ClassPattern extractClassPattern(String code, String sourcePath) {
        Pattern classPattern = Pattern.compile("class\\s+(\\w+)");
        Matcher matcher = classPattern.matcher(code);
        
        if (matcher.find()) {
            String name = matcher.group(1);
            
            // Extract methods
            List<String> methods = extractMethods(code);
            
            // Extract fields
            List<String> fields = extractFields(code);
            
            return new ClassPattern(name, methods, fields, sourcePath);
        }
        
        return null;
    }
    
    /**
     * Extract method signatures from code
     */
    private List<String> extractMethods(String code) {
        List<String> methods = new ArrayList<>();
        Pattern methodPattern = Pattern.compile("(public|private|protected)\\s+(\\w+(?:<[^>]+>)?)\\s+(\\w+)\\s*\\([^)]*\\)");
        Matcher matcher = methodPattern.matcher(code);
        
        while (matcher.find()) {
            String signature = matcher.group(0);
            methods.add(signature);
        }
        
        return methods;
    }
    
    /**
     * Extract field declarations from code
     */
    private List<String> extractFields(String code) {
        List<String> fields = new ArrayList<>();
        Pattern fieldPattern = Pattern.compile("(private|public|protected)\\s+(\\w+(?:<[^>]+>)?)\\s+(\\w+)");
        Matcher matcher = fieldPattern.matcher(code);
        
        while (matcher.find()) {
            String declaration = matcher.group(0);
            fields.add(declaration);
        }
        
        return fields;
    }
    
    /**
     * Generate new enum implementation based on absorbed pattern
     */
    public String generateEnum(String originalName, String newName, List<String> additionalValues) {
        EnumPattern pattern = absorbedEnums.get(originalName);
        if (pattern == null) {
            return null;
        }
        
        StringBuilder code = new StringBuilder();
        code.append("package fraymus.brain.unified;\n\n");
        code.append("// GENERATED via absorption and growth from: ").append(pattern.sourcePath).append("\n");
        code.append("public enum ").append(newName).append(" {\n");
        
        // Original values
        for (String value : pattern.values) {
            code.append("    ").append(value).append(",\n");
        }
        
        // Additional grown values
        for (String value : additionalValues) {
            code.append("    ").append(value).append(",  // GROWN\n");
        }
        
        code.append("}\n");
        
        return code.toString();
    }
    
    /**
     * Generate new class implementation based on absorbed pattern
     */
    public String generateClass(String originalName, String newName, List<String> additionalMethods) {
        ClassPattern pattern = absorbedClasses.get(originalName);
        if (pattern == null) {
            return null;
        }
        
        StringBuilder code = new StringBuilder();
        code.append("package fraymus.brain.unified;\n\n");
        code.append("// GENERATED via absorption and growth from: ").append(pattern.sourcePath).append("\n");
        code.append("public class ").append(newName).append(" {\n");
        
        // Fields
        for (String field : pattern.fields) {
            code.append("    ").append(field).append(";\n");
        }
        
        code.append("\n");
        
        // Original methods
        for (String method : pattern.methods) {
            code.append("    ").append(method).append(" {\n");
            code.append("        // Implementation absorbed from: ").append(pattern.sourcePath).append("\n");
            code.append("    }\n\n");
        }
        
        // Additional grown methods
        for (String method : additionalMethods) {
            code.append("    ").append(method).append(" {\n");
            code.append("        // GROWN method - system's own capability\n");
            code.append("    }\n\n");
        }
        
        code.append("}\n");
        
        return code.toString();
    }
    
    /**
     * Get absorbed statistics
     */
    public String getStatistics() {
        return String.format("Absorbed: %d classes, %d enums", 
            absorbedClasses.size(), absorbedEnums.size());
    }
    
    /**
     * Enum pattern representation
     */
    static class EnumPattern {
        String name;
        List<String> values;
        String sourcePath;
        
        EnumPattern(String name, List<String> values, String sourcePath) {
            this.name = name;
            this.values = values;
            this.sourcePath = sourcePath;
        }
    }
    
    /**
     * Class pattern representation
     */
    static class ClassPattern {
        String name;
        List<String> methods;
        List<String> fields;
        String sourcePath;
        
        ClassPattern(String name, List<String> methods, List<String> fields, String sourcePath) {
            this.name = name;
            this.methods = methods;
            this.fields = fields;
            this.sourcePath = sourcePath;
        }
    }
}
