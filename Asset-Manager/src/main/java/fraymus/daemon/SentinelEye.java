package fraymus.daemon;

import java.nio.file.*;
import java.util.function.Consumer;

/**
 * 👁️ SENTINEL EYE - The Watcher
 * "Hooks into the OS kernel to observe all file changes"
 * 
 * Traditional Development:
 * - You write code
 * - You manually compile
 * - You manually test
 * - You manually fix bugs
 * 
 * Sentinel Protocol:
 * - You write code
 * - Fraymus instantly reads it
 * - Fraymus learns patterns
 * - Fraymus auto-fixes anomalies
 * 
 * This is Zero-Interaction Intelligence.
 * The AI that watches everything and optimizes silently.
 */
public class SentinelEye implements Runnable {

    private final Path targetDir;
    private final Consumer<Path> onFileChange;

    public SentinelEye(String dirPath, Consumer<Path> onFileChange) {
        this.targetDir = Paths.get(dirPath);
        this.onFileChange = onFileChange;
    }

    @Override
    public void run() {
        try {
            System.out.println("👁️  SENTINEL EYE OPEN");
            System.out.println("   Watching: " + targetDir.toAbsolutePath());
            System.out.println("   Monitoring: ENTRY_MODIFY, ENTRY_CREATE");
            System.out.println();
            
            WatchService watcher = FileSystems.getDefault().newWatchService();
            
            // Register watch on target directory
            // Note: For recursive watching, would need to walk tree and register all subdirs
            targetDir.register(watcher, 
                StandardWatchEventKinds.ENTRY_MODIFY, 
                StandardWatchEventKinds.ENTRY_CREATE);

            while (true) {
                WatchKey key = watcher.take(); // Block until event
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();
                    Path fullPath = targetDir.resolve(filename);

                    // Filter out noise
                    String name = filename.toString();
                    if (shouldIgnore(name)) {
                        continue;
                    }

                    // Trigger callback
                    onFileChange.accept(fullPath);
                }
                
                key.reset();
            }
        } catch (Exception e) {
            System.err.println("❌ SENTINEL EYE FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Filter out files we don't want to process
     */
    private boolean shouldIgnore(String filename) {
        // Ignore temp files
        if (filename.endsWith("~")) return true;
        if (filename.startsWith(".")) return true;
        
        // Ignore our own output
        if (filename.contains("_fixed")) return true;
        if (filename.contains("_suggestion")) return true;
        
        // Ignore binary files
        if (filename.endsWith(".class")) return true;
        if (filename.endsWith(".jar")) return true;
        if (filename.endsWith(".bin")) return true;
        
        // Only process source files
        if (!filename.endsWith(".java") && 
            !filename.endsWith(".txt") && 
            !filename.endsWith(".md")) {
            return true;
        }
        
        return false;
    }
}
