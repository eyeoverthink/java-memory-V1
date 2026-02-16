package gemini.root;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

/**
 * TRACE LOGGER: Per-request observability.
 * Logs: requestId, RAG snippets, tool calls, reflect on/off, model, timing.
 * Output: memory/trace_*.jsonl
 */
public class TraceLogger {

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private static final Path TRACE_DIR = Path.of("memory");

    public static class Trace {
        public String requestId;
        public long timestamp;
        public String user;
        public List<String> ragSnippetIds = new ArrayList<>();
        public List<String> toolCalls = new ArrayList<>();
        public int toolOutputLen;
        public boolean reflectEnabled;
        public String model;
        public int ctxSize;
        public long durationMs;
        public String answerPreview;
    }

    private static Path getTraceFile() {
        String date = Instant.now().toString().substring(0, 10);
        return TRACE_DIR.resolve("trace_" + date + ".jsonl");
    }

    public static String newRequestId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static void log(Trace t) {
        try {
            Files.createDirectories(TRACE_DIR);
            Path f = getTraceFile();
            
            JsonObject o = new JsonObject();
            o.addProperty("requestId", t.requestId);
            o.addProperty("timestamp", t.timestamp);
            o.addProperty("user", truncate(t.user, 200));
            o.add("ragSnippetIds", gson.toJsonTree(t.ragSnippetIds));
            o.add("toolCalls", gson.toJsonTree(t.toolCalls));
            o.addProperty("toolOutputLen", t.toolOutputLen);
            o.addProperty("reflectEnabled", t.reflectEnabled);
            o.addProperty("model", t.model);
            o.addProperty("ctxSize", t.ctxSize);
            o.addProperty("durationMs", t.durationMs);
            o.addProperty("answerPreview", truncate(t.answerPreview, 200));

            try (BufferedWriter bw = Files.newBufferedWriter(f, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                bw.write(gson.toJson(o));
                bw.newLine();
            }
        } catch (Exception e) {
            System.err.println("[TRACE_ERROR] " + e.getMessage());
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
