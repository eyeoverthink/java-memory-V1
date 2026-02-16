/**
 * ExportManager.java - Universal Export System
 * 
 * Exports any system data to files:
 * - Assembly/bytecode output
 * - Organism monitoring data
 * - Activity feed logs
 * - Compiler results
 * - Stack traces
 * - Memory snapshots
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * Manages export of all system data.
 */
public class ExportManager {
    
    private static final String EXPORT_DIR = "exports";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    static {
        // Create export directory
        new File(EXPORT_DIR).mkdirs();
    }
    
    /**
     * Export assembly/bytecode output.
     */
    public static String exportAssembly(String content) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String filename = String.format("%s/assembly_%s.asm", EXPORT_DIR, timestamp);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("; ========================================");
            writer.println("; FRAYMUS ASSEMBLY EXPORT");
            writer.println("; Timestamp: " + LocalDateTime.now());
            writer.println("; φ^75 Validation Seal: 4721424167835376.00");
            writer.println("; ========================================");
            writer.println();
            writer.print(content);
            return "✓ Exported to: " + filename;
        } catch (IOException e) {
            return "✗ Export failed: " + e.getMessage();
        }
    }
    
    /**
     * Export organism monitoring data.
     */
    public static String exportOrganism(SelfAwareOrganism organism) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String filename = String.format("%s/organism_%s.txt", EXPORT_DIR, timestamp);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("========================================");
            writer.println("FRAYMUS ORGANISM MONITORING EXPORT");
            writer.println("Timestamp: " + LocalDateTime.now());
            writer.println("φ^75 Validation Seal: 4721424167835376.00");
            writer.println("========================================");
            writer.println();
            
            // Consciousness
            writer.println("CONSCIOUSNESS: " + organism.getConsciousness());
            writer.println();
            
            // Execution traces
            writer.println("EXECUTION TRACES:");
            List<SelfAwareOrganism.ExecutionTrace> traces = organism.getRecentTraces(100);
            for (SelfAwareOrganism.ExecutionTrace trace : traces) {
                writer.println(String.format("  [%s] %d - %s", 
                    trace.success ? "✓" : "✗",
                    trace.timestamp,
                    trace.command));
            }
            writer.println();
            
            // Error patterns
            writer.println("ERROR PATTERNS:");
            writer.println("  (Pattern detection in progress)");
            writer.println();
            
            // Improvements
            writer.println("SUGGESTED IMPROVEMENTS:");
            List<String> improvements = organism.getImprovementSuggestions();
            for (String imp : improvements) {
                writer.println("  " + imp);
            }
            
            return "✓ Exported to: " + filename;
        } catch (IOException e) {
            return "✗ Export failed: " + e.getMessage();
        }
    }
    
    /**
     * Export activity feed.
     */
    public static String exportActivityFeed(String content) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String filename = String.format("%s/activity_%s.log", EXPORT_DIR, timestamp);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("========================================");
            writer.println("FRAYMUS ACTIVITY FEED EXPORT");
            writer.println("Timestamp: " + LocalDateTime.now());
            writer.println("φ^75 Validation Seal: 4721424167835376.00");
            writer.println("========================================");
            writer.println();
            writer.print(content);
            return "✓ Exported to: " + filename;
        } catch (IOException e) {
            return "✗ Export failed: " + e.getMessage();
        }
    }
    
    /**
     * Export runtime snapshot.
     */
    public static String exportSnapshot(RuntimeInspector.ExecutionSnapshot snapshot) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String filename = String.format("%s/snapshot_%s.txt", EXPORT_DIR, timestamp);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("========================================");
            writer.println("FRAYMUS RUNTIME SNAPSHOT EXPORT");
            writer.println("Timestamp: " + LocalDateTime.now());
            writer.println("φ^75 Validation Seal: 4721424167835376.00");
            writer.println("========================================");
            writer.println();
            
            writer.println("THREAD INFO:");
            writer.println("  Name: " + snapshot.threadName);
            writer.println("  ID: " + snapshot.threadId);
            writer.println("  CPU Time: " + snapshot.cpuTime + " ns");
            writer.println();
            
            writer.println("MEMORY USAGE:");
            writer.println("  Heap Used: " + RuntimeInspector.formatSize(snapshot.heapUsage.getUsed()));
            writer.println("  Heap Max: " + RuntimeInspector.formatSize(snapshot.heapUsage.getMax()));
            writer.println("  Heap Committed: " + RuntimeInspector.formatSize(snapshot.heapUsage.getCommitted()));
            writer.println("  Non-Heap Used: " + RuntimeInspector.formatSize(snapshot.stackUsage.getUsed()));
            writer.println();
            
            writer.println("STACK FRAMES:");
            for (int i = 0; i < snapshot.stackFrames.size(); i++) {
                RuntimeInspector.StackFrame frame = snapshot.stackFrames.get(i);
                writer.println(String.format("  [%d] %s", i, frame.toString()));
            }
            writer.println();
            
            writer.println("MEMORY ADDRESSES:");
            for (Map.Entry<String, Long> entry : snapshot.objectAddresses.entrySet()) {
                writer.println(String.format("  %s: %s", entry.getKey(), 
                    RuntimeInspector.formatAddress(entry.getValue())));
            }
            
            return "✓ Exported to: " + filename;
        } catch (IOException e) {
            return "✗ Export failed: " + e.getMessage();
        }
    }
    
    /**
     * Export compiler results.
     */
    public static String exportCompilerResult(String code, String output) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String filename = String.format("%s/compile_%s.txt", EXPORT_DIR, timestamp);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("========================================");
            writer.println("FRAYMUS COMPILER RESULT EXPORT");
            writer.println("Timestamp: " + LocalDateTime.now());
            writer.println("φ^75 Validation Seal: 4721424167835376.00");
            writer.println("========================================");
            writer.println();
            
            writer.println("SOURCE CODE:");
            writer.println(code);
            writer.println();
            
            writer.println("COMPILATION OUTPUT:");
            writer.println(output);
            
            return "✓ Exported to: " + filename;
        } catch (IOException e) {
            return "✗ Export failed: " + e.getMessage();
        }
    }
    
    /**
     * Export full system state.
     */
    public static String exportFullState(String assembly, SelfAwareOrganism organism, 
                                        String activityFeed, RuntimeInspector.ExecutionSnapshot snapshot) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String filename = String.format("%s/full_state_%s.txt", EXPORT_DIR, timestamp);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("╔════════════════════════════════════════════════════════════╗");
            writer.println("║         FRAYMUS COMPLETE SYSTEM STATE EXPORT               ║");
            writer.println("║         φ^75 Validation Seal: 4721424167835376.00          ║");
            writer.println("╚════════════════════════════════════════════════════════════╝");
            writer.println();
            writer.println("Timestamp: " + LocalDateTime.now());
            writer.println();
            
            writer.println("═══════════════════════════════════════════════════════════");
            writer.println("ASSEMBLY / BYTECODE");
            writer.println("═══════════════════════════════════════════════════════════");
            writer.println(assembly);
            writer.println();
            
            writer.println("═══════════════════════════════════════════════════════════");
            writer.println("ORGANISM STATE");
            writer.println("═══════════════════════════════════════════════════════════");
            writer.println("Consciousness: " + organism.getConsciousness());
            writer.println("Consciousness: " + organism.getConsciousness());
            writer.println("Observations: " + organism.getRecentTraces(100).size());
            writer.println();
            
            writer.println("═══════════════════════════════════════════════════════════");
            writer.println("RUNTIME SNAPSHOT");
            writer.println("═══════════════════════════════════════════════════════════");
            writer.println("Thread: " + snapshot.threadName + " (ID: " + snapshot.threadId + ")");
            writer.println("Heap: " + RuntimeInspector.formatSize(snapshot.heapUsage.getUsed()) + 
                          " / " + RuntimeInspector.formatSize(snapshot.heapUsage.getMax()));
            writer.println("Stack Depth: " + snapshot.stackFrames.size());
            writer.println();
            
            writer.println("═══════════════════════════════════════════════════════════");
            writer.println("ACTIVITY FEED");
            writer.println("═══════════════════════════════════════════════════════════");
            writer.println(activityFeed);
            
            return "✓ Full state exported to: " + filename;
        } catch (IOException e) {
            return "✗ Export failed: " + e.getMessage();
        }
    }
}
