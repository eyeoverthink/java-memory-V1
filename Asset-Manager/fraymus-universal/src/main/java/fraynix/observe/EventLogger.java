package fraynix.observe;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * EVENT LOGGER: Audit-grade observability for Fraynix.
 * 
 * Outputs:
 *   - events.jsonl (structured events)
 *   - metrics.csv (time-series metrics)
 *   - run_summary.json (session summary)
 * 
 * Rule: No magic. If it "healed," it must show what changed, why, and proof it worked.
 */
public class EventLogger implements AutoCloseable {

    private final Path logDir;
    private final BufferedWriter eventsWriter;
    private final BufferedWriter metricsWriter;
    private final String runId;
    private final Instant startTime;
    
    private final BlockingQueue<LogEntry> queue;
    private final ExecutorService writer;
    private volatile boolean running = true;
    
    private final AtomicLong eventCount = new AtomicLong();
    private final Map<String, AtomicLong> eventCounts = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> latencyHistograms = new ConcurrentHashMap<>();

    public EventLogger() throws IOException {
        this(Path.of(".fraynix", "logs"));
    }

    public EventLogger(Path logDir) throws IOException {
        this.logDir = logDir;
        this.runId = generateRunId();
        this.startTime = Instant.now();
        
        // Create log directory
        Files.createDirectories(logDir);
        
        // Open writers
        Path runDir = logDir.resolve(runId);
        Files.createDirectories(runDir);
        
        this.eventsWriter = Files.newBufferedWriter(
            runDir.resolve("events.jsonl"),
            StandardOpenOption.CREATE, StandardOpenOption.APPEND
        );
        
        this.metricsWriter = Files.newBufferedWriter(
            runDir.resolve("metrics.csv"),
            StandardOpenOption.CREATE, StandardOpenOption.APPEND
        );
        
        // Write CSV header
        metricsWriter.write("timestamp,metric,value,tags\n");
        metricsWriter.flush();
        
        // Async writer thread
        this.queue = new LinkedBlockingQueue<>(10000);
        this.writer = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "EventLogger-Writer");
            t.setDaemon(true);
            return t;
        });
        writer.submit(this::processQueue);
        
        // Log startup
        logEvent("system_start", Map.of(
            "runId", runId,
            "timestamp", startTime.toString(),
            "javaVersion", System.getProperty("java.version"),
            "os", System.getProperty("os.name")
        ));
        
        System.out.println("📋 EventLogger started → " + runDir);
    }

    private String generateRunId() {
        return DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
            .format(java.time.LocalDateTime.now()) + "_" + 
            UUID.randomUUID().toString().substring(0, 8);
    }

    public void logEvent(String eventType, Map<String, Object> data) {
        eventCount.incrementAndGet();
        eventCounts.computeIfAbsent(eventType, k -> new AtomicLong()).incrementAndGet();
        
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("timestamp", Instant.now().toString());
        event.put("type", eventType);
        event.put("runId", runId);
        event.putAll(data);
        
        queue.offer(new LogEntry(LogEntry.Type.EVENT, toJson(event)));
    }

    public void logIntentReceived(String id, String type, String origin, String priority) {
        logEvent("intent_received", Map.of(
            "intent_id", id,
            "intent_type", type,
            "origin", origin,
            "priority", priority
        ));
    }

    public void logDecisionMade(String intentId, String policy, String action, double confidence, String reason) {
        logEvent("decision_made", Map.of(
            "intent_id", intentId,
            "policy", policy,
            "action", action,
            "confidence", confidence,
            "reason", reason
        ));
    }

    public void logActionTaken(String intentId, String action, boolean success, long durationMs) {
        logEvent("action_taken", Map.of(
            "intent_id", intentId,
            "action", action,
            "success", success,
            "duration_ms", durationMs
        ));
    }

    public void logArtifactCreated(String artifactId, String name, String type, String hash) {
        logEvent("artifact_created", Map.of(
            "artifact_id", artifactId,
            "name", name,
            "type", type,
            "hash", hash
        ));
    }

    public void logRepairAttempted(String fileId, String issue, String action) {
        logEvent("repair_attempted", Map.of(
            "file_id", fileId,
            "issue", issue,
            "action", action
        ));
    }

    public void logRepairVerified(String fileId, boolean success, String proof) {
        logEvent("repair_verified", Map.of(
            "file_id", fileId,
            "success", success,
            "proof", proof
        ));
    }

    public void logMetric(String metric, double value, Map<String, String> tags) {
        String tagStr = tags.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .reduce((a, b) -> a + ";" + b)
            .orElse("");
        
        String line = String.format("%s,%s,%.4f,%s%n",
            Instant.now().toString(), metric, value, tagStr);
        
        queue.offer(new LogEntry(LogEntry.Type.METRIC, line));
        
        // Track for histograms
        latencyHistograms.computeIfAbsent(metric, k -> new CopyOnWriteArrayList<>())
            .add((long) value);
    }

    public void logMetric(String metric, double value) {
        logMetric(metric, value, Map.of());
    }

    private void processQueue() {
        while (running || !queue.isEmpty()) {
            try {
                LogEntry entry = queue.poll(100, TimeUnit.MILLISECONDS);
                if (entry != null) {
                    if (entry.type == LogEntry.Type.EVENT) {
                        eventsWriter.write(entry.data);
                        eventsWriter.newLine();
                        eventsWriter.flush();
                    } else {
                        metricsWriter.write(entry.data);
                        metricsWriter.flush();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                System.err.println("EventLogger write error: " + e.getMessage());
            }
        }
    }

    public void writeRunSummary() throws IOException {
        Instant endTime = Instant.now();
        long durationMs = java.time.Duration.between(startTime, endTime).toMillis();
        
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("runId", runId);
        summary.put("startTime", startTime.toString());
        summary.put("endTime", endTime.toString());
        summary.put("durationMs", durationMs);
        summary.put("totalEvents", eventCount.get());
        
        // Event counts by type
        Map<String, Long> counts = new LinkedHashMap<>();
        eventCounts.forEach((k, v) -> counts.put(k, v.get()));
        summary.put("eventCounts", counts);
        
        // Latency stats
        Map<String, Map<String, Long>> latencyStats = new LinkedHashMap<>();
        latencyHistograms.forEach((metric, values) -> {
            if (!values.isEmpty()) {
                Collections.sort(values);
                Map<String, Long> stats = new LinkedHashMap<>();
                stats.put("count", (long) values.size());
                stats.put("min", values.get(0));
                stats.put("max", values.get(values.size() - 1));
                stats.put("avg", values.stream().mapToLong(v -> v).sum() / values.size());
                int p99Idx = (int) (values.size() * 0.99);
                stats.put("p99", values.get(Math.min(p99Idx, values.size() - 1)));
                latencyStats.put(metric, stats);
            }
        });
        summary.put("latencyStats", latencyStats);
        
        // Write summary
        Path summaryPath = logDir.resolve(runId).resolve("run_summary.json");
        Files.writeString(summaryPath, toPrettyJson(summary));
        
        System.out.println("📊 Run summary written → " + summaryPath);
    }

    @Override
    public void close() throws IOException {
        // Log shutdown
        logEvent("system_stop", Map.of(
            "totalEvents", eventCount.get(),
            "durationMs", java.time.Duration.between(startTime, Instant.now()).toMillis()
        ));
        
        running = false;
        writer.shutdown();
        try {
            writer.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        writeRunSummary();
        eventsWriter.close();
        metricsWriter.close();
    }

    public String getRunId() { return runId; }
    public long getEventCount() { return eventCount.get(); }
    public Path getLogDir() { return logDir.resolve(runId); }

    private String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(e.getKey()).append("\":");
            Object v = e.getValue();
            if (v instanceof String) {
                sb.append("\"").append(escapeJson((String) v)).append("\"");
            } else if (v instanceof Map) {
                sb.append(toJson((Map<String, Object>) v));
            } else {
                sb.append(v);
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private String toPrettyJson(Map<String, Object> map) {
        return toPrettyJson(map, 0);
    }

    private String toPrettyJson(Map<String, Object> map, int indent) {
        StringBuilder sb = new StringBuilder();
        String pad = "  ".repeat(indent);
        String padInner = "  ".repeat(indent + 1);
        
        sb.append("{\n");
        boolean first = true;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!first) sb.append(",\n");
            first = false;
            sb.append(padInner).append("\"").append(e.getKey()).append("\": ");
            Object v = e.getValue();
            if (v instanceof String) {
                sb.append("\"").append(escapeJson((String) v)).append("\"");
            } else if (v instanceof Map) {
                sb.append(toPrettyJson((Map<String, Object>) v, indent + 1));
            } else {
                sb.append(v);
            }
        }
        sb.append("\n").append(pad).append("}");
        return sb.toString();
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private record LogEntry(Type type, String data) {
        enum Type { EVENT, METRIC }
    }
}
