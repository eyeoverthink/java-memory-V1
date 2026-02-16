package fraymus.core;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import org.json.JSONObject;

/**
 * AUDIT LOG - Observability backbone
 * 
 * Every action is logged: intent_received, decision_made, action_taken, etc.
 * Outputs: events.jsonl, run_summary.json, metrics.csv
 */
public class AuditLog {
    
    private final Path eventsFile;
    private final Path metricsFile;
    private final Path summaryFile;
    private final BlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>();
    private final ExecutorService writer;
    private volatile boolean running = false;
    
    private final Map<String, Long> eventCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> errorCounts = new ConcurrentHashMap<>();
    
    public AuditLog(String outputDir) {
        Path dir = Paths.get(outputDir);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create audit log directory", e);
        }
        
        this.eventsFile = dir.resolve("events.jsonl");
        this.metricsFile = dir.resolve("metrics.csv");
        this.summaryFile = dir.resolve("run_summary.json");
        
        this.writer = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "AuditLog-Writer");
            t.setDaemon(true);
            return t;
        });
    }
    
    /**
     * Start logging
     */
    public void start() {
        if (running) return;
        running = true;
        
        // Initialize files
        try {
            if (!Files.exists(eventsFile)) {
                Files.createFile(eventsFile);
            }
            if (!Files.exists(metricsFile)) {
                Files.write(metricsFile, "timestamp,event_type,count,errors\n".getBytes());
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize audit log files: " + e.getMessage());
        }
        
        // Start writer thread
        writer.submit(this::writeLoop);
    }
    
    /**
     * Stop logging and write summary
     */
    public void stop() {
        running = false;
        writeSummary();
        writer.shutdown();
        try {
            writer.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            writer.shutdownNow();
        }
    }
    
    /**
     * Log an event
     */
    public void log(String eventType, Object data) {
        log(eventType, data, null);
    }
    
    /**
     * Log an event with error
     */
    public void log(String eventType, Object data, Exception error) {
        LogEntry entry = new LogEntry(eventType, data, error);
        queue.offer(entry);
        
        eventCounts.merge(eventType, 1L, Long::sum);
        if (error != null) {
            errorCounts.merge(eventType, 1L, Long::sum);
        }
    }
    
    /**
     * Main write loop
     */
    private void writeLoop() {
        while (running) {
            try {
                LogEntry entry = queue.poll(100, TimeUnit.MILLISECONDS);
                if (entry != null) {
                    writeEntry(entry);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // Flush remaining entries
        while (!queue.isEmpty()) {
            LogEntry entry = queue.poll();
            if (entry != null) {
                writeEntry(entry);
            }
        }
    }
    
    /**
     * Write single entry
     */
    private void writeEntry(LogEntry entry) {
        try {
            // Write to events.jsonl
            JSONObject json = new JSONObject();
            json.put("timestamp", entry.timestamp);
            json.put("event_type", entry.eventType);
            json.put("data", entry.data != null ? entry.data.toString() : "null");
            if (entry.error != null) {
                json.put("error", entry.error.getMessage());
                json.put("stack_trace", getStackTrace(entry.error));
            }
            
            Files.write(eventsFile, (json.toString() + "\n").getBytes(), 
                StandardOpenOption.APPEND);
            
        } catch (IOException e) {
            System.err.println("Failed to write audit log: " + e.getMessage());
        }
    }
    
    /**
     * Write summary
     */
    private void writeSummary() {
        try {
            JSONObject summary = new JSONObject();
            summary.put("run_ended", Instant.now().toString());
            summary.put("total_events", eventCounts.values().stream().mapToLong(Long::longValue).sum());
            summary.put("total_errors", errorCounts.values().stream().mapToLong(Long::longValue).sum());
            
            JSONObject events = new JSONObject();
            for (Map.Entry<String, Long> e : eventCounts.entrySet()) {
                events.put(e.getKey(), e.getValue());
            }
            summary.put("event_counts", events);
            
            JSONObject errorJson = new JSONObject();
            for (Map.Entry<String, Long> e : errorCounts.entrySet()) {
                errorJson.put(e.getKey(), e.getValue());
            }
            summary.put("error_counts", errorJson);
            
            Files.write(summaryFile, summary.toString(2).getBytes());
            
            // Also write metrics CSV
            StringBuilder csv = new StringBuilder();
            for (Map.Entry<String, Long> e : eventCounts.entrySet()) {
                long errorCount = errorCounts.getOrDefault(e.getKey(), 0L);
                csv.append(Instant.now()).append(",")
                   .append(e.getKey()).append(",")
                   .append(e.getValue()).append(",")
                   .append(errorCount).append("\n");
            }
            Files.write(metricsFile, csv.toString().getBytes(), StandardOpenOption.APPEND);
            
        } catch (IOException e) {
            System.err.println("Failed to write summary: " + e.getMessage());
        }
    }
    
    /**
     * Get stack trace as string
     */
    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    
    /**
     * Log entry
     */
    private static class LogEntry {
        final long timestamp;
        final String eventType;
        final Object data;
        final Exception error;
        
        LogEntry(String eventType, Object data, Exception error) {
            this.timestamp = System.currentTimeMillis();
            this.eventType = eventType;
            this.data = data;
            this.error = error;
        }
    }
}
