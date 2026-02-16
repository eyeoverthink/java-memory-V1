/**
 * MassAbsorber.java - Directory Tree Crawler
 * 
 * "Walks the earth, eating libraries."
 * 
 * Recursively scans directories for alien code files (.py, .go, .cpp, .rs, etc.)
 * and feeds them to the PhilosophersStone for transmutation.
 * 
 * This is the assimilation tubule of the Borg Collective.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 129 (Mass Absorption)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * Mass directory crawler and absorber.
 */
public class MassAbsorber {
    
    private PhilosophersStone stone;
    private Set<String> targetExtensions;
    private int filesProcessed = 0;
    private int filesSucceeded = 0;
    
    public MassAbsorber() {
        this.stone = new PhilosophersStone();
        this.targetExtensions = new HashSet<>(Arrays.asList(
            ".py", ".go", ".cpp", ".cc", ".c", ".h", 
            ".rs", ".js", ".ts", ".cs", ".rb", ".php"
        ));
        
        ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
            ActivityBus.ActivityType.COMMAND_EXECUTE,
            "MassAbsorber",
            "🌪️ MASS ABSORBER ACTIVE. Ready to consume libraries.",
            true
        ));
    }
    
    /**
     * CONSUME - Recursively absorb all alien code in directory.
     */
    public void consume(String directoryPath) {
        try {
            Path rootPath = Paths.get(directoryPath);
            
            if (!Files.exists(rootPath)) {
                System.err.println("✗ Directory not found: " + directoryPath);
                return;
            }
            
            if (!Files.isDirectory(rootPath)) {
                System.err.println("✗ Not a directory: " + directoryPath);
                return;
            }
            
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║  🌪️ MASS ABSORPTION INITIATED                             ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            System.out.println("Target: " + rootPath.toAbsolutePath() + "\n");
            
            ActivityBus.getInstance().broadcast(new ActivityBus.Activity(
                ActivityBus.ActivityType.COMMAND_EXECUTE,
                "MassAbsorber",
                "Consuming: " + directoryPath,
                true
            ));
            
            // Walk directory tree
            try (Stream<Path> paths = Files.walk(rootPath)) {
                paths
                    .filter(Files::isRegularFile)
                    .filter(this::isTargetFile)
                    .forEach(this::processFile);
            }
            
            // Report results
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║  🌪️ MASS ABSORPTION COMPLETE                              ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            System.out.println("Files Processed: " + filesProcessed);
            System.out.println("Files Succeeded: " + filesSucceeded);
            System.out.println("Files Failed: " + (filesProcessed - filesSucceeded));
            
            double successRate = filesProcessed > 0 ? 
                (double) filesSucceeded / filesProcessed * 100 : 0;
            System.out.println("Success Rate: " + String.format("%.1f%%", successRate) + "\n");
            
        } catch (IOException e) {
            System.err.println("✗ Mass absorption failed: " + e.getMessage());
        }
    }
    
    /**
     * Check if file is a target for absorption.
     */
    private boolean isTargetFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return targetExtensions.stream().anyMatch(fileName::endsWith);
    }
    
    /**
     * Process individual file.
     */
    private void processFile(Path path) {
        filesProcessed++;
        
        System.out.println("\n[" + filesProcessed + "] " + path.getFileName());
        
        boolean success = stone.assimilate(path.toFile());
        
        if (success) {
            filesSucceeded++;
        }
    }
    
    /**
     * Add custom file extension to target list.
     */
    public void addExtension(String extension) {
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }
        targetExtensions.add(extension.toLowerCase());
    }
    
    /**
     * Get statistics.
     */
    public String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║  🌪️ MASS ABSORBER STATS                                   ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append("Files Processed: ").append(filesProcessed).append("\n");
        sb.append("Files Succeeded: ").append(filesSucceeded).append("\n");
        sb.append("Files Failed: ").append(filesProcessed - filesSucceeded).append("\n\n");
        sb.append("Target Extensions: ").append(targetExtensions).append("\n\n");
        sb.append(stone.getStats());
        
        return sb.toString();
    }
}
