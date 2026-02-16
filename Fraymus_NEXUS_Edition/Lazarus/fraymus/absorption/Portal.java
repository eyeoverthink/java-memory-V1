package fraymus.absorption;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * PORTAL: THE INTAKE VALVE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "A swirling vortex. Drop anything in. Watch it become knowledge."
 * 
 * This is the Drop Zone interface for the Black Hole Protocol.
 * It accepts any input and routes it to the appropriate absorber.
 * 
 * Input Types:
 * - .jar file → Library absorption
 * - .java file → Source code analysis
 * - .txt/.md → Text concept extraction
 * - .json/.xml → Data structure analysis
 * - URL → Web content scraping
 * - Image → Pattern recognition (future)
 * - Directory → Recursive absorption
 * 
 * The Portal is the single entry point for all knowledge ingestion.
 * One interface to consume the world.
 */
public class Portal {

    private BlackHoleProtocol blackHole;
    private URLAbsorber webEater;
    private List<IngestionRecord> history;
    
    // Statistics
    private long itemsIngested = 0;
    private long bytesConsumed = 0;

    public Portal() {
        this.blackHole = new BlackHoleProtocol();
        this.webEater = new URLAbsorber(blackHole.akashic);
        this.history = new ArrayList<>();
        
        System.out.println();
        System.out.println("🌀 PORTAL OPENED");
        System.out.println("   Mode: Universal Intake");
        System.out.println("   Status: HUNGRY");
    }

    // ═══════════════════════════════════════════════════════════════════
    // THE DROP ZONE
    // ═══════════════════════════════════════════════════════════════════

    /**
     * DROP - Accept anything and route to appropriate handler
     */
    public void drop(String input) {
        System.out.println();
        System.out.println("🌀 PORTAL: ITEM DETECTED");
        System.out.println("========================================");
        System.out.println("   >> INPUT: " + input);
        
        // Determine input type
        InputType type = classifyInput(input);
        System.out.println("   >> TYPE: " + type);
        System.out.println();
        
        // Route to appropriate handler
        switch (type) {
            case JAR_FILE:
                ingestJar(input);
                break;
            case JAVA_FILE:
                ingestSourceFile(input);
                break;
            case TEXT_FILE:
                ingestTextFile(input);
                break;
            case URL:
                ingestUrl(input);
                break;
            case DIRECTORY:
                ingestDirectory(input);
                break;
            case PACKAGE:
                ingestPackage(input);
                break;
            case CLASS:
                ingestClass(input);
                break;
            default:
                System.out.println("   !! UNKNOWN INPUT TYPE");
        }
        
        // Record history
        history.add(new IngestionRecord(input, type, System.currentTimeMillis()));
        itemsIngested++;
        
        System.out.println("========================================");
    }

    /**
     * DROP FILE - Accept a File object
     */
    public void drop(File file) {
        drop(file.getAbsolutePath());
    }

    /**
     * DROP PATH - Accept a Path object
     */
    public void drop(Path path) {
        drop(path.toAbsolutePath().toString());
    }

    // ═══════════════════════════════════════════════════════════════════
    // INPUT CLASSIFICATION
    // ═══════════════════════════════════════════════════════════════════

    /**
     * CLASSIFY - Determine what type of input this is
     */
    private InputType classifyInput(String input) {
        // URL check
        if (input.startsWith("http://") || input.startsWith("https://")) {
            return InputType.URL;
        }
        
        // File/directory check
        File file = new File(input);
        if (file.exists()) {
            if (file.isDirectory()) {
                return InputType.DIRECTORY;
            }
            
            String name = file.getName().toLowerCase();
            if (name.endsWith(".jar")) return InputType.JAR_FILE;
            if (name.endsWith(".java")) return InputType.JAVA_FILE;
            if (name.endsWith(".txt") || name.endsWith(".md")) return InputType.TEXT_FILE;
            if (name.endsWith(".json") || name.endsWith(".xml")) return InputType.DATA_FILE;
            if (name.endsWith(".class")) return InputType.CLASS_FILE;
        }
        
        // Package name check (e.g., "java.util")
        if (input.matches("[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)+")) {
            return InputType.PACKAGE;
        }
        
        // Class name check (e.g., "java.lang.Math")
        if (input.matches("[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)*\\.[A-Z][a-zA-Z0-9]*")) {
            return InputType.CLASS;
        }
        
        return InputType.UNKNOWN;
    }

    // ═══════════════════════════════════════════════════════════════════
    // INGESTION HANDLERS
    // ═══════════════════════════════════════════════════════════════════

    private void ingestJar(String path) {
        System.out.println("   🌀 INGESTING JAR FILE...");
        blackHole.absorbJar(path);
        
        try {
            bytesConsumed += Files.size(Paths.get(path));
        } catch (Exception e) {
            // Ignore
        }
    }

    private void ingestSourceFile(String path) {
        System.out.println("   🌀 INGESTING SOURCE FILE...");
        
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            bytesConsumed += content.length();
            
            // Extract class and method signatures from source
            List<String> extracted = extractFromSource(content);
            System.out.println("   >> EXTRACTED: " + extracted.size() + " code elements");
            
        } catch (Exception e) {
            System.out.println("   !! INGESTION FAILED: " + e.getMessage());
        }
    }

    private void ingestTextFile(String path) {
        System.out.println("   🌀 INGESTING TEXT FILE...");
        
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            bytesConsumed += content.length();
            
            // Store as knowledge
            blackHole.akashic.addBlock("TEXT_KNOWLEDGE", "FILE: " + path + " | " + 
                content.substring(0, Math.min(500, content.length())));
            
            System.out.println("   >> CONSUMED: " + content.length() + " bytes");
            
        } catch (Exception e) {
            System.out.println("   !! INGESTION FAILED: " + e.getMessage());
        }
    }

    private void ingestUrl(String url) {
        System.out.println("   🌀 INGESTING URL VIA WEB EATER...");
        webEater.absorb(url);
    }

    private void ingestDirectory(String path) {
        System.out.println("   🌀 INGESTING DIRECTORY...");
        
        try {
            Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    String name = file.getFileName().toString().toLowerCase();
                    if (name.endsWith(".jar") || name.endsWith(".java") || 
                        name.endsWith(".class")) {
                        drop(file);
                    }
                });
        } catch (Exception e) {
            System.out.println("   !! INGESTION FAILED: " + e.getMessage());
        }
    }

    private void ingestPackage(String packageName) {
        System.out.println("   🌀 INGESTING PACKAGE...");
        blackHole.absorbPackage(packageName);
    }

    private void ingestClass(String className) {
        System.out.println("   🌀 INGESTING CLASS...");
        
        try {
            Class<?> clazz = Class.forName(className);
            blackHole.absorbClass(clazz);
        } catch (Exception e) {
            System.out.println("   !! CLASS NOT FOUND: " + className);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════

    private List<String> extractFromSource(String source) {
        List<String> elements = new ArrayList<>();
        
        // Simple regex-based extraction
        String[] lines = source.split("\n");
        for (String line : lines) {
            line = line.trim();
            
            // Class declarations
            if (line.startsWith("public class ") || line.startsWith("class ")) {
                elements.add("CLASS: " + line);
            }
            
            // Method declarations
            if (line.contains("public ") && line.contains("(") && line.contains(")") && 
                !line.contains("class ")) {
                elements.add("METHOD: " + line);
            }
        }
        
        return elements;
    }

    // ═══════════════════════════════════════════════════════════════════
    // QUERY INTERFACE
    // ═══════════════════════════════════════════════════════════════════

    /**
     * ASK - Natural language query to the absorbed knowledge
     */
    public Object ask(String query) {
        System.out.println();
        System.out.println("🌀 PORTAL QUERY: " + query);
        System.out.println("========================================");
        
        // Try to execute as skill first
        Object result = blackHole.executeSkill(query);
        if (result != null) {
            return result;
        }
        
        // Search absorbed knowledge
        var skills = blackHole.querySkills(query);
        if (!skills.isEmpty()) {
            System.out.println("   >> FOUND " + skills.size() + " matching skills:");
            for (var skill : skills) {
                System.out.println("   • " + skill.description);
            }
        }
        
        return skills;
    }

    // ═══════════════════════════════════════════════════════════════════
    // STATISTICS
    // ═══════════════════════════════════════════════════════════════════

    public void printStats() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────┐");
        System.out.println("│ PORTAL STATISTICS                                           │");
        System.out.println("├─────────────────────────────────────────────────────────────┤");
        System.out.println("│ Items Ingested:      " + String.format("%-36d", itemsIngested) + "│");
        System.out.println("│ Bytes Consumed:      " + String.format("%-36d", bytesConsumed) + "│");
        System.out.println("│ History Size:        " + String.format("%-36d", history.size()) + "│");
        System.out.println("└─────────────────────────────────────────────────────────────┘");
        
        blackHole.printStats();
    }

    public void showHistory() {
        System.out.println();
        System.out.println("🌀 INGESTION HISTORY:");
        System.out.println("========================================");
        
        for (IngestionRecord record : history) {
            System.out.println("   " + record);
        }
        
        System.out.println("========================================");
    }

    // ═══════════════════════════════════════════════════════════════════
    // INNER CLASSES
    // ═══════════════════════════════════════════════════════════════════

    public enum InputType {
        JAR_FILE, JAVA_FILE, CLASS_FILE, TEXT_FILE, DATA_FILE,
        URL, DIRECTORY, PACKAGE, CLASS, UNKNOWN
    }

    private static class IngestionRecord {
        String input;
        InputType type;
        long timestamp;
        
        IngestionRecord(String input, InputType type, long timestamp) {
            this.input = input;
            this.type = type;
            this.timestamp = timestamp;
        }
        
        @Override
        public String toString() {
            return "[" + type + "] " + input;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN (Demo)
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   THE PORTAL: UNIVERSAL INTAKE VALVE                         ║");
        System.out.println("║   \"A swirling vortex. Drop anything in.\"                     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        Portal portal = new Portal();
        
        // TEST 1: Drop a package
        System.out.println();
        System.out.println("TEST 1: DROP A PACKAGE");
        portal.drop("java.lang.Math");
        
        // TEST 2: Drop a class
        System.out.println();
        System.out.println("TEST 2: DROP A CLASS");
        portal.drop("java.util.ArrayList");
        
        // TEST 3: Ask a question
        System.out.println();
        System.out.println("TEST 3: ASK A QUESTION");
        Object result = portal.ask("sqrt");
        
        // TEST 4: Execute with parameters
        System.out.println();
        System.out.println("TEST 4: EXECUTE ABSORBED SKILL");
        portal.blackHole.executeSkill("sqrt", 25.0);
        portal.blackHole.executeSkill("abs", -42);
        
        // TEST 5: Show history
        portal.showHistory();
        
        // TEST 6: Statistics
        portal.printStats();
        
        System.out.println();
        System.out.println("   THE USER EXPERIENCE:");
        System.out.println("   ├─ You: Drag quantum_mechanics_lib.jar into Portal");
        System.out.println("   ├─ Console: SKILL ACQUIRED: QuantumSolver can calculateWaveFunction");
        System.out.println("   ├─ You: portal.ask(\"Calculate wave function\")");
        System.out.println("   └─ Fraymus: Executing QuantumSolver.calculateWaveFunction()...");
        System.out.println();
        System.out.println("   It learns by eating.");
        System.out.println();
    }
}
