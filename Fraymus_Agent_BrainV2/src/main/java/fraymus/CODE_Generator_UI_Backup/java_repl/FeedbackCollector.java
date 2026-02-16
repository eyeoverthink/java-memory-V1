/**
 * FeedbackCollector.java - Async Feedback Persistence
 * 
 * Saves all Ollama responses to timestamped files in background.
 * Never blocks the terminal.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.concurrent.*;

public class FeedbackCollector {
    
    private static final String FEEDBACK_DIR = "memory/feedback";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    private final ExecutorService executor;
    private final PersistenceEngine persistence;
    
    public FeedbackCollector(PersistenceEngine persistence) {
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "Feedback-Collector");
            t.setDaemon(true);
            return t;
        });
        this.persistence = persistence;
        
        try {
            Files.createDirectories(Paths.get(FEEDBACK_DIR));
        } catch (IOException e) {
            System.err.println("Warning: Could not create feedback directory");
        }
    }
    
    /**
     * Save feedback asynchronously (never blocks).
     */
    public void saveFeedback(String prompt, String response) {
        executor.submit(() -> {
            try {
                String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
                
                // 1. Save to JSON file
                String filename = String.format("%s/feedback_%s.json", FEEDBACK_DIR, timestamp);
                String json = String.format(
                    "{\"timestamp\":\"%s\",\"prompt\":\"%s\",\"response\":\"%s\"}",
                    timestamp,
                    escapeJson(prompt),
                    escapeJson(response)
                );
                Files.writeString(Paths.get(filename), json);
                
                // 2. Save to persistence engine (7 backends)
                if (persistence != null) {
                    persistence.persist("feedback", prompt, response);
                }
                
            } catch (Exception e) {
                System.err.println("Feedback save failed: " + e.getMessage());
            }
        });
    }
    
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}
