package fraymus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Crash Logger - Prevents overflow and logs errors to JSON
 */
public class CrashLogger {
    
    private static final String LOG_FILE = "data/crash_log.json";
    private static final String STATE_FILE = "data/system_state.json";
    private static final int MAX_ENTRIES = 100;
    private static final long MAX_MEMORY_MB = 512; // Max memory before warning
    
    private static List<CrashEntry> crashLog = new ArrayList<>();
    private static boolean initialized = false;
    
    public static class CrashEntry {
        public String timestamp;
        public String source;
        public String error;
        public String stackTrace;
        public long memoryUsedMB;
        
        public CrashEntry(String source, String error, String stackTrace) {
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.source = source;
            this.error = error;
            this.stackTrace = stackTrace;
            Runtime rt = Runtime.getRuntime();
            this.memoryUsedMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        }
    }
    
    public static void init() {
        if (initialized) return;
        loadCrashLog();
        initialized = true;
        System.out.println("[CrashLogger] Initialized - " + crashLog.size() + " previous entries");
    }
    
    /**
     * Log an exception
     */
    public static void log(String source, Exception e) {
        init();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        CrashEntry entry = new CrashEntry(source, e.getMessage(), sw.toString());
        crashLog.add(entry);
        
        // Trim old entries
        while (crashLog.size() > MAX_ENTRIES) {
            crashLog.remove(0);
        }
        
        saveCrashLog();
        System.err.println("[CRASH] " + source + ": " + e.getMessage());
    }
    
    /**
     * Log an error message
     */
    public static void log(String source, String error) {
        init();
        CrashEntry entry = new CrashEntry(source, error, "");
        crashLog.add(entry);
        
        while (crashLog.size() > MAX_ENTRIES) {
            crashLog.remove(0);
        }
        
        saveCrashLog();
        System.err.println("[ERROR] " + source + ": " + error);
    }
    
    /**
     * Check memory and warn if high
     */
    public static boolean checkMemory() {
        Runtime rt = Runtime.getRuntime();
        long usedMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        long maxMB = rt.maxMemory() / (1024 * 1024);
        
        if (usedMB > MAX_MEMORY_MB) {
            System.out.println("[CrashLogger] WARNING: High memory usage " + usedMB + "MB / " + maxMB + "MB");
            // Force GC
            System.gc();
            return false;
        }
        return true;
    }
    
    /**
     * Get memory stats
     */
    public static String getMemoryStats() {
        Runtime rt = Runtime.getRuntime();
        long usedMB = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        long maxMB = rt.maxMemory() / (1024 * 1024);
        long freeMB = rt.freeMemory() / (1024 * 1024);
        return String.format("Memory: %dMB used / %dMB max (free: %dMB)", usedMB, maxMB, freeMB);
    }
    
    /**
     * Save system state for recovery
     */
    public static void saveState(String key, String value) {
        try {
            File file = new File(STATE_FILE);
            StringBuilder json = new StringBuilder();
            
            // Read existing
            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        json.append(line);
                    }
                }
            }
            
            // Simple JSON append/update
            String content = json.toString();
            if (content.isEmpty() || content.equals("{}")) {
                content = "{\"" + escapeJson(key) + "\":\"" + escapeJson(value) + "\"}";
            } else {
                // Remove closing brace and append
                content = content.substring(0, content.lastIndexOf('}'));
                content += ",\"" + escapeJson(key) + "\":\"" + escapeJson(value) + "\"}";
            }
            
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.print(content);
            }
        } catch (Exception e) {
            System.err.println("[CrashLogger] Failed to save state: " + e.getMessage());
        }
    }
    
    /**
     * Get recent crashes
     */
    public static List<CrashEntry> getRecentCrashes(int count) {
        init();
        int start = Math.max(0, crashLog.size() - count);
        return new ArrayList<>(crashLog.subList(start, crashLog.size()));
    }
    
    /**
     * Get crash count
     */
    public static int getCrashCount() {
        init();
        return crashLog.size();
    }
    
    private static void loadCrashLog() {
        try {
            File file = new File(LOG_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                return;
            }
            
            StringBuilder json = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    json.append(line);
                }
            }
            
            // Simple JSON array parsing
            String content = json.toString().trim();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1);
                // Parse entries (simplified)
                int depth = 0;
                int start = 0;
                for (int i = 0; i < content.length(); i++) {
                    char c = content.charAt(i);
                    if (c == '{') depth++;
                    else if (c == '}') {
                        depth--;
                        if (depth == 0) {
                            String entry = content.substring(start, i + 1).trim();
                            if (entry.startsWith("{")) {
                                CrashEntry ce = parseEntry(entry);
                                if (ce != null) crashLog.add(ce);
                            }
                            start = i + 2; // Skip comma
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[CrashLogger] Failed to load: " + e.getMessage());
        }
    }
    
    private static CrashEntry parseEntry(String json) {
        try {
            String source = extractValue(json, "source");
            String error = extractValue(json, "error");
            String stack = extractValue(json, "stackTrace");
            return new CrashEntry(source, error, stack);
        } catch (Exception e) {
            return null;
        }
    }
    
    private static String extractValue(String json, String key) {
        int idx = json.indexOf("\"" + key + "\"");
        if (idx < 0) return "";
        int start = json.indexOf("\"", idx + key.length() + 3) + 1;
        int end = json.indexOf("\"", start);
        if (start > 0 && end > start) {
            return unescapeJson(json.substring(start, end));
        }
        return "";
    }
    
    private static void saveCrashLog() {
        try {
            File file = new File(LOG_FILE);
            file.getParentFile().mkdirs();
            
            StringBuilder json = new StringBuilder();
            json.append("[\n");
            for (int i = 0; i < crashLog.size(); i++) {
                CrashEntry e = crashLog.get(i);
                json.append("  {");
                json.append("\"timestamp\":\"").append(escapeJson(e.timestamp)).append("\",");
                json.append("\"source\":\"").append(escapeJson(e.source)).append("\",");
                json.append("\"error\":\"").append(escapeJson(e.error != null ? e.error : "null")).append("\",");
                json.append("\"memoryMB\":").append(e.memoryUsedMB).append(",");
                json.append("\"stackTrace\":\"").append(escapeJson(e.stackTrace)).append("\"");
                json.append("}");
                if (i < crashLog.size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("]");
            
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.print(json.toString());
            }
        } catch (Exception e) {
            System.err.println("[CrashLogger] Failed to save: " + e.getMessage());
        }
    }
    
    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    private static String unescapeJson(String s) {
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
