package fraymus.transformation;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * 🔬 File Scanner - Actual Implementation
 * 
 * Scans files for transformation patterns using code analysis.
 * Extracts transformations from code in various languages.
 */
public class FileScannerImpl {
    
    private boolean initialized;
    private List<String> discoveredPatterns;
    
    // Patterns for different languages
    private static final Pattern JAVA_METHOD = Pattern.compile(
        "(public|private|protected)?\\s*(static)?\\s*\\w+\\s+(\\w+)\\s*\\([^)]*\\)\\s*\\{"
    );
    
    private static final Pattern PYTHON_FUNCTION = Pattern.compile(
        "def\\s+(\\w+)\\s*\\([^)]*\\):"
    );
    
    private static final Pattern CPP_FUNCTION = Pattern.compile(
        "\\w+\\s+(\\w+)\\s*\\([^)]*\\)\\s*\\{"
    );
    
    private static final Pattern RUST_FUNCTION = Pattern.compile(
        "fn\\s+(\\w+)\\s*\\([^)]*\\)\\s*(->\\s*\\w+)?\\s*\\{"
    );
    
    /**
     * Initialize file scanner
     */
    public void initialize() {
        discoveredPatterns = new ArrayList<>();
        initialized = true;
        System.out.println("  [File Scanner] Initialized");
    }
    
    /**
     * Scan directory for transformation patterns
     */
    public List<String> scan(String directory) {
        if (!initialized) {
            System.out.println("  [File Scanner] Not initialized");
            return new ArrayList<>();
        }
        
        System.out.println("  [File Scanner] Scanning: " + directory);
        
        try {
            Files.walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .filter(this::isCodeFile)
                .filter(this::shouldScanFile)
                .limit(100)  // Limit to 100 files to prevent excessive scanning
                .forEach(this::scanFile);
        } catch (IOException e) {
            System.out.println("  [File Scanner] Error scanning: " + e.getMessage());
        }
        
        System.out.println("  [File Scanner] Discovered patterns: " + discoveredPatterns.size());
        return discoveredPatterns;
    }
    
    /**
     * Check if file should be scanned (exclude certain directories)
     */
    private boolean shouldScanFile(Path file) {
        String path = file.toString().toLowerCase();
        
        // Exclude common directories that shouldn't be scanned
        String[] excludedDirs = {
            "node_modules", ".git", "build", "target", "gradle", ".gradle",
            ".idea", ".vscode", "dist", "out", "bin", "obj"
        };
        
        for (String dir : excludedDirs) {
            if (path.contains("\\" + dir + "\\") || path.contains("/" + dir + "/")) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if file is a code file
     */
    private boolean isCodeFile(Path file) {
        String fileName = file.toString().toLowerCase();
        return fileName.endsWith(".java") || 
               fileName.endsWith(".py") || 
               fileName.endsWith(".cpp") || 
               fileName.endsWith(".c") || 
               fileName.endsWith(".h") || 
               fileName.endsWith(".rs") || 
               fileName.endsWith(".js") || 
               fileName.endsWith(".ts");
    }
    
    /**
     * Scan single file for transformation patterns
     */
    private void scanFile(Path file) {
        try {
            String content = new String(Files.readAllBytes(file));
            String fileName = file.toString();
            
            if (fileName.endsWith(".java")) {
                extractJavaTransformations(content, fileName);
            } else if (fileName.endsWith(".py")) {
                extractPythonTransformations(content, fileName);
            } else if (fileName.endsWith(".cpp") || fileName.endsWith(".c") || fileName.endsWith(".h")) {
                extractCppTransformations(content, fileName);
            } else if (fileName.endsWith(".rs")) {
                extractRustTransformations(content, fileName);
            } else if (fileName.endsWith(".js") || fileName.endsWith(".ts")) {
                extractJavaScriptTransformations(content, fileName);
            }
        } catch (IOException e) {
            // Silently skip unreadable files
        }
    }
    
    /**
     * Extract transformations from Java code
     */
    private void extractJavaTransformations(String content, String fileName) {
        Matcher matcher = JAVA_METHOD.matcher(content);
        while (matcher.find()) {
            String methodName = matcher.group(3);
            discoveredPatterns.add("JAVA:" + fileName + ":" + methodName);
        }
    }
    
    /**
     * Extract transformations from Python code
     */
    private void extractPythonTransformations(String content, String fileName) {
        Matcher matcher = PYTHON_FUNCTION.matcher(content);
        while (matcher.find()) {
            String functionName = matcher.group(1);
            discoveredPatterns.add("PYTHON:" + fileName + ":" + functionName);
        }
    }
    
    /**
     * Extract transformations from C++ code
     */
    private void extractCppTransformations(String content, String fileName) {
        Matcher matcher = CPP_FUNCTION.matcher(content);
        while (matcher.find()) {
            String functionName = matcher.group(1);
            discoveredPatterns.add("CPP:" + fileName + ":" + functionName);
        }
    }
    
    /**
     * Extract transformations from Rust code
     */
    private void extractRustTransformations(String content, String fileName) {
        Matcher matcher = RUST_FUNCTION.matcher(content);
        while (matcher.find()) {
            String functionName = matcher.group(1);
            discoveredPatterns.add("RUST:" + fileName + ":" + functionName);
        }
    }
    
    /**
     * Extract transformations from JavaScript/TypeScript code
     */
    private void extractJavaScriptTransformations(String content, String fileName) {
        // Simple pattern for function declarations
        Pattern pattern = Pattern.compile("function\\s+(\\w+)\\s*\\([^)]*\\)");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String functionName = matcher.group(1);
            discoveredPatterns.add("JS:" + fileName + ":" + functionName);
        }
        
        // Arrow functions
        Pattern arrowPattern = Pattern.compile("(\\w+)\\s*=\\s*\\([^)]*\\)\\s*=>");
        matcher = arrowPattern.matcher(content);
        while (matcher.find()) {
            String functionName = matcher.group(1);
            discoveredPatterns.add("JS:" + fileName + ":" + functionName);
        }
    }
    
    /**
     * Get status
     */
    public String getStatus() {
        return initialized ? "READY" : "NOT INITIALIZED";
    }
    
    /**
     * Shutdown
     */
    public void shutdown() {
        initialized = false;
        System.out.println("  [File Scanner] Shutdown complete");
    }
}
