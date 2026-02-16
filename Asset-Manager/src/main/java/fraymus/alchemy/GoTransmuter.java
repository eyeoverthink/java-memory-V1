package fraymus.alchemy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ⚗️ THE ALCHEMY ENGINE - Gen 128
 * "Lead (Go) into Gold (Java)."
 * 
 * The Ouroboros Protocol: Fraymus consumes Ollama's Go source,
 * transmutes it into Java DNA, and eventually replaces the host.
 * 
 * PURPOSE:
 * 1. Ingest Go Structs → Excrete Java Classes
 * 2. Map Goroutines → Virtual Threads (Java 21)
 * 3. Map Channels → BlockingQueues
 * 4. Map Funcs → Methods
 * 5. Map Interfaces → Interfaces
 */
public class GoTransmuter {

    private static final double PHI = 1.6180339887;
    
    private String packageName = "fraymus.evolved";
    private boolean generateGettersSetters = true;
    private boolean generateToString = true;
    private boolean generateBuilder = false;

    // ═══════════════════════════════════════════════════════════════════
    // THE ELEMENTAL TABLE (Go → Java Type Mapping)
    // ═══════════════════════════════════════════════════════════════════
    
    private String mapType(String goType) {
        goType = goType.trim();
        
        // Primitive types
        switch (goType) {
            case "string":      return "String";
            case "int":         return "int";
            case "int8":        return "byte";
            case "int16":       return "short";
            case "int32":       return "int";
            case "int64":       return "long";
            case "uint":        return "int";
            case "uint8":       return "byte";
            case "uint16":      return "short";
            case "uint32":      return "int";
            case "uint64":      return "long";
            case "float32":     return "float";
            case "float64":     return "double";
            case "bool":        return "boolean";
            case "byte":        return "byte";
            case "rune":        return "char";
            case "interface{}": return "Object";
            case "any":         return "Object";
            case "error":       return "Exception";
        }
        
        // Slice types
        if (goType.startsWith("[]")) {
            String elemType = mapType(goType.substring(2));
            return "List<" + boxType(elemType) + ">";
        }
        
        // Array types [N]T
        if (goType.matches("\\[\\d+\\].*")) {
            String elemType = goType.replaceFirst("\\[\\d+\\]", "");
            return mapType(elemType) + "[]";
        }
        
        // Map types
        if (goType.startsWith("map[")) {
            Pattern mapPattern = Pattern.compile("map\\[(\\w+)\\](\\w+)");
            Matcher m = mapPattern.matcher(goType);
            if (m.find()) {
                String keyType = mapType(m.group(1));
                String valType = mapType(m.group(2));
                return "Map<" + boxType(keyType) + ", " + boxType(valType) + ">";
            }
        }
        
        // Channel types
        if (goType.startsWith("chan ") || goType.startsWith("<-chan ") || goType.startsWith("chan<-")) {
            String elemType = goType.replaceAll(".*chan\\s*", "").replaceAll("<-", "").trim();
            return "Channel<" + boxType(mapType(elemType)) + ">";
        }
        
        // Pointer types (Java uses references, so just return the type)
        if (goType.startsWith("*")) {
            return mapType(goType.substring(1));
        }
        
        // Function types
        if (goType.startsWith("func")) {
            return "Runnable";  // Simplified
        }
        
        // Context type
        if (goType.equals("context.Context")) {
            return "Object";  // TODO: proper context mapping
        }
        
        // Time types
        if (goType.equals("time.Duration")) {
            return "java.time.Duration";
        }
        if (goType.equals("time.Time")) {
            return "java.time.Instant";
        }
        
        // Default: assume it's a custom struct name
        return goType;
    }
    
    private String boxType(String primitiveType) {
        switch (primitiveType) {
            case "int":     return "Integer";
            case "long":    return "Long";
            case "double":  return "Double";
            case "float":   return "Float";
            case "boolean": return "Boolean";
            case "byte":    return "Byte";
            case "short":   return "Short";
            case "char":    return "Character";
            default:        return primitiveType;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // STRUCT TRANSMUTATION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * TRANSMUTE STRUCT
     * Converts Go 'type X struct { ... }' into a Java Class.
     */
    public String transmuteStruct(String goSource) {
        StringBuilder java = new StringBuilder();
        
        // Extract struct definition
        Pattern structPattern = Pattern.compile(
            "type\\s+(\\w+)\\s+struct\\s*\\{([^}]+)\\}",
            Pattern.DOTALL
        );
        Matcher matcher = structPattern.matcher(goSource);
        
        if (!matcher.find()) {
            return "// ERROR: No struct found in source";
        }
        
        String className = matcher.group(1);
        String body = matcher.group(2);
        
        // Parse fields
        List<Field> fields = parseFields(body);
        
        // Generate package and imports
        java.append("package ").append(packageName).append(";\n\n");
        java.append(generateImports(fields));
        
        // Class header
        java.append("/**\n");
        java.append(" * ⚗️ TRANSMUTED from Go struct: ").append(className).append("\n");
        java.append(" * Generated by GoTransmuter (Gen 128)\n");
        java.append(" */\n");
        java.append("public class ").append(className).append(" {\n\n");
        
        // Fields
        for (Field f : fields) {
            if (f.comment != null) {
                java.append("    // ").append(f.comment).append("\n");
            }
            java.append("    private ").append(f.javaType).append(" ").append(f.name).append(";\n");
        }
        java.append("\n");
        
        // Default constructor
        java.append("    public ").append(className).append("() {\n");
        java.append("        // Transmuted from Go Source\n");
        java.append("    }\n\n");
        
        // All-args constructor
        if (fields.size() > 0 && fields.size() <= 10) {
            java.append("    public ").append(className).append("(");
            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                java.append(f.javaType).append(" ").append(f.name);
                if (i < fields.size() - 1) java.append(", ");
            }
            java.append(") {\n");
            for (Field f : fields) {
                java.append("        this.").append(f.name).append(" = ").append(f.name).append(";\n");
            }
            java.append("    }\n\n");
        }
        
        // Getters and Setters
        if (generateGettersSetters) {
            for (Field f : fields) {
                // Getter
                String capName = capitalize(f.name);
                java.append("    public ").append(f.javaType).append(" get").append(capName).append("() {\n");
                java.append("        return this.").append(f.name).append(";\n");
                java.append("    }\n\n");
                
                // Setter
                java.append("    public void set").append(capName).append("(").append(f.javaType).append(" ").append(f.name).append(") {\n");
                java.append("        this.").append(f.name).append(" = ").append(f.name).append(";\n");
                java.append("    }\n\n");
            }
        }
        
        // toString
        if (generateToString) {
            java.append("    @Override\n");
            java.append("    public String toString() {\n");
            java.append("        return \"").append(className).append("{\" +\n");
            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                java.append("            \"").append(f.name).append("=\" + ").append(f.name);
                if (i < fields.size() - 1) java.append(" + \", \" +\n");
                else java.append(" +\n");
            }
            java.append("            \"}\";\n");
            java.append("    }\n");
        }
        
        java.append("}\n");
        
        return java.toString();
    }
    
    private List<Field> parseFields(String body) {
        List<Field> fields = new ArrayList<>();
        String[] lines = body.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//")) continue;
            
            // Handle embedded structs (anonymous fields)
            if (line.matches("^\\*?\\w+$")) {
                Field f = new Field();
                f.name = line.replace("*", "");
                f.goType = line;
                f.javaType = mapType(line);
                f.embedded = true;
                fields.add(f);
                continue;
            }
            
            // Parse: FieldName Type `json:"name"`
            // Or: FieldName, FieldName2 Type
            Pattern fieldPattern = Pattern.compile(
                "^(\\w+(?:\\s*,\\s*\\w+)*)\\s+([^`\\s]+)\\s*(`[^`]*`)?\\s*(?://(.*))?$"
            );
            Matcher m = fieldPattern.matcher(line);
            
            if (m.find()) {
                String names = m.group(1);
                String goType = m.group(2);
                String tags = m.group(3);
                String comment = m.group(4);
                
                for (String name : names.split(",")) {
                    Field f = new Field();
                    f.name = name.trim();
                    f.goType = goType;
                    f.javaType = mapType(goType);
                    f.jsonTag = extractJsonTag(tags);
                    f.comment = comment;
                    fields.add(f);
                }
            }
        }
        
        return fields;
    }
    
    private String extractJsonTag(String tags) {
        if (tags == null) return null;
        Pattern p = Pattern.compile("json:\"([^\"]+)\"");
        Matcher m = p.matcher(tags);
        return m.find() ? m.group(1) : null;
    }
    
    private String generateImports(List<Field> fields) {
        StringBuilder imports = new StringBuilder();
        boolean needsList = false;
        boolean needsMap = false;
        boolean needsTime = false;
        
        for (Field f : fields) {
            if (f.javaType.startsWith("List<")) needsList = true;
            if (f.javaType.startsWith("Map<")) needsMap = true;
            if (f.javaType.contains("java.time")) needsTime = true;
        }
        
        if (needsList) imports.append("import java.util.List;\n");
        if (needsMap) imports.append("import java.util.Map;\n");
        if (needsTime) imports.append("import java.time.*;\n");
        
        if (imports.length() > 0) imports.append("\n");
        return imports.toString();
    }
    
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // FUNCTION TRANSMUTATION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * TRANSMUTE FUNCTION
     * Converts Go 'func Name(params) returns { ... }' into Java method.
     */
    public String transmuteFunc(String goFunc) {
        StringBuilder java = new StringBuilder();
        
        // Pattern: func (receiver) Name(params) (returns) { body }
        Pattern funcPattern = Pattern.compile(
            "func\\s*(?:\\((\\w+)\\s+(\\*?\\w+)\\))?\\s*(\\w+)\\s*\\(([^)]*)\\)\\s*(?:\\(([^)]*)\\)|([\\w*\\[\\]]+))?\\s*\\{([^}]*)\\}",
            Pattern.DOTALL
        );
        Matcher m = funcPattern.matcher(goFunc);
        
        if (!m.find()) {
            return "// ERROR: Could not parse function";
        }
        
        String receiverName = m.group(1);
        String receiverType = m.group(2);
        String funcName = m.group(3);
        String params = m.group(4);
        String multiReturns = m.group(5);
        String singleReturn = m.group(6);
        String body = m.group(7);
        
        // Determine return type
        String returnType = "void";
        if (singleReturn != null && !singleReturn.isEmpty()) {
            returnType = mapType(singleReturn);
        } else if (multiReturns != null && !multiReturns.isEmpty()) {
            // Multiple returns become a tuple or the first value
            String[] returns = multiReturns.split(",");
            returnType = mapType(returns[0].trim());
            // TODO: Could generate a Result<T, E> class for error returns
        }
        
        // Generate method
        java.append("    public ").append(returnType).append(" ").append(funcName).append("(");
        
        // Parse params
        if (params != null && !params.isEmpty()) {
            String[] paramList = params.split(",");
            for (int i = 0; i < paramList.length; i++) {
                String param = paramList[i].trim();
                String[] parts = param.split("\\s+");
                if (parts.length >= 2) {
                    String pName = parts[0];
                    String pType = mapType(parts[1]);
                    java.append(pType).append(" ").append(pName);
                    if (i < paramList.length - 1) java.append(", ");
                }
            }
        }
        
        java.append(") {\n");
        java.append("        // TODO: Transmute body\n");
        java.append("        // Original Go:\n");
        for (String line : body.split("\n")) {
            java.append("        // ").append(line.trim()).append("\n");
        }
        java.append("    }\n");
        
        return java.toString();
    }

    // ═══════════════════════════════════════════════════════════════════
    // GOROUTINE TRANSMUTATION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * TRANSMUTE GOROUTINE
     * Converts 'go func() { ... }()' into Java Virtual Thread.
     */
    public String transmuteGoroutine(String goRoutine) {
        // Pattern: go func() { body }()
        Pattern p = Pattern.compile("go\\s+func\\s*\\(\\)\\s*\\{([^}]*)\\}\\s*\\(\\)");
        Matcher m = p.matcher(goRoutine);
        
        if (m.find()) {
            String body = m.group(1);
            return String.format(
                "Thread.startVirtualThread(() -> {\n" +
                "    // Transmuted from goroutine\n" +
                "    %s\n" +
                "});",
                body.trim()
            );
        }
        
        // Simple: go functionCall()
        Pattern simple = Pattern.compile("go\\s+(\\w+)\\s*\\(([^)]*)\\)");
        Matcher s = simple.matcher(goRoutine);
        if (s.find()) {
            String funcName = s.group(1);
            String args = s.group(2);
            return String.format(
                "Thread.startVirtualThread(() -> %s(%s));",
                funcName, args
            );
        }
        
        return "// ERROR: Could not parse goroutine";
    }

    // ═══════════════════════════════════════════════════════════════════
    // FULL FILE TRANSMUTATION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * TRANSMUTE FILE
     * Full Go source file → Java class file.
     */
    public TransmutationResult transmuteFile(String goSource) {
        TransmutationResult result = new TransmutationResult();
        result.original = goSource;
        
        // Extract package name
        Pattern pkgPattern = Pattern.compile("package\\s+(\\w+)");
        Matcher pkgM = pkgPattern.matcher(goSource);
        if (pkgM.find()) {
            result.goPackage = pkgM.group(1);
        }
        
        // Find all structs
        Pattern structPattern = Pattern.compile(
            "type\\s+(\\w+)\\s+struct\\s*\\{[^}]+\\}",
            Pattern.DOTALL
        );
        Matcher structM = structPattern.matcher(goSource);
        while (structM.find()) {
            String structCode = structM.group(0);
            String javaClass = transmuteStruct(structCode);
            result.classes.add(javaClass);
        }
        
        result.success = !result.classes.isEmpty();
        return result;
    }

    // ═══════════════════════════════════════════════════════════════════
    // CONFIGURATION
    // ═══════════════════════════════════════════════════════════════════
    
    public GoTransmuter setPackage(String pkg) {
        this.packageName = pkg;
        return this;
    }
    
    public GoTransmuter withGettersSetters(boolean enabled) {
        this.generateGettersSetters = enabled;
        return this;
    }
    
    public GoTransmuter withToString(boolean enabled) {
        this.generateToString = enabled;
        return this;
    }
    
    public GoTransmuter withBuilder(boolean enabled) {
        this.generateBuilder = enabled;
        return this;
    }

    // ═══════════════════════════════════════════════════════════════════
    // INNER CLASSES
    // ═══════════════════════════════════════════════════════════════════
    
    private static class Field {
        String name;
        String goType;
        String javaType;
        String jsonTag;
        String comment;
        boolean embedded;
    }
    
    public static class TransmutationResult {
        public String original;
        public String goPackage;
        public List<String> classes = new ArrayList<>();
        public List<String> errors = new ArrayList<>();
        public boolean success;
    }
}
