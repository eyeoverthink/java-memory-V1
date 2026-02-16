/**
 * HistoryManager.java - φ-Harmonic Command History with DB Persistence
 * 
 * Interactive history management with SQLite persistence, search, replay,
 * and φ-harmonic resonance tracking.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Manages command history with file-based persistence and interactive features.
 */
public class HistoryManager {
    
    private static final double PHI = 1.618033988749895;
    private static final String HISTORY_FILE = "repl_history.txt";
    private static final String SESSION_FILE = "repl_session.txt";
    
    private List<HistoryEntry> sessionHistory;
    private List<HistoryEntry> allHistory;
    private int sessionId;
    
    /**
     * History entry with metadata.
     */
    public static class HistoryEntry {
        public final int id;
        public final int sessionId;
        public final String command;
        public final LocalDateTime timestamp;
        public final String result;
        public final double phiResonance;
        public final long executionTimeMs;
        
        public HistoryEntry(int id, int sessionId, String command, LocalDateTime timestamp, 
                          String result, double phiResonance, long executionTimeMs) {
            this.id = id;
            this.sessionId = sessionId;
            this.command = command;
            this.timestamp = timestamp;
            this.result = result;
            this.phiResonance = phiResonance;
            this.executionTimeMs = executionTimeMs;
        }
    }
    
    /**
     * Initialize history manager with file-based storage.
     */
    public HistoryManager() {
        this.sessionHistory = new ArrayList<>();
        this.allHistory = new ArrayList<>();
        this.sessionId = (int) (System.currentTimeMillis() / 1000);
        loadHistoryFromFile();
    }
    
    /**
     * Load history from file.
     */
    private void loadHistoryFromFile() {
        try {
            File file = new File(HISTORY_FILE);
            if (!file.exists()) {
                return;
            }
            
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Parse format: sessionId|command|timestamp|result|phiResonance|executionTime
                String[] parts = line.split("\\|", 6);
                if (parts.length >= 6) {
                    try {
                        int sid = Integer.parseInt(parts[0]);
                        String cmd = parts[1];
                        LocalDateTime ts = LocalDateTime.parse(parts[2], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        String res = parts[3].equals("null") ? null : parts[3];
                        double phi = Double.parseDouble(parts[4]);
                        long execTime = Long.parseLong(parts[5]);
                        
                        allHistory.add(new HistoryEntry(
                            allHistory.size() + 1, sid, cmd, ts, res, phi, execTime
                        ));
                    } catch (Exception e) {
                        // Skip malformed lines
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist or can't be read - that's okay
        }
    }
    
    /**
     * Add command to history.
     */
    public void addCommand(String command, String result, long executionTimeMs) {
        LocalDateTime now = LocalDateTime.now();
        double phiResonance = calculatePhiResonance(command);
        
        HistoryEntry entry = new HistoryEntry(
            sessionHistory.size() + 1,
            sessionId,
            command,
            now,
            result,
            phiResonance,
            executionTimeMs
        );
        
        sessionHistory.add(entry);
        saveToFile(entry);
    }
    
    /**
     * Calculate φ-harmonic resonance of command.
     */
    private double calculatePhiResonance(String command) {
        if (command == null || command.isEmpty()) return 0.0;
        
        double resonance = 0.0;
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            resonance += (c * Math.pow(PHI, (i % 7) / 7.0)) % 1.0;
        }
        
        return (resonance / command.length()) * PHI;
    }
    
    /**
     * Save entry to file.
     */
    private void saveToFile(HistoryEntry entry) {
        try {
            String line = String.format("%d|%s|%s|%s|%.4f|%d\n",
                entry.sessionId,
                entry.command.replace("|", "\\|"),
                entry.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                entry.result != null ? entry.result.substring(0, Math.min(200, entry.result.length())).replace("|", "\\|") : "null",
                entry.phiResonance,
                entry.executionTimeMs
            );
            
            Files.write(
                Paths.get(HISTORY_FILE),
                line.getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
            
            allHistory.add(entry);
            
        } catch (IOException e) {
            System.err.println("Failed to save history entry: " + e.getMessage());
        }
    }
    
    /**
     * Get session history.
     */
    public List<HistoryEntry> getSessionHistory() {
        return new ArrayList<>(sessionHistory);
    }
    
    /**
     * Search history by pattern.
     */
    public List<HistoryEntry> search(String pattern) {
        List<HistoryEntry> results = new ArrayList<>();
        String lowerPattern = pattern.toLowerCase();
        
        for (HistoryEntry entry : sessionHistory) {
            if (entry.command.toLowerCase().contains(lowerPattern)) {
                results.add(entry);
            }
        }
        
        return results;
    }
    
    /**
     * Search all history in file.
     */
    public List<HistoryEntry> searchDatabase(String pattern) {
        List<HistoryEntry> results = new ArrayList<>();
        String lowerPattern = pattern.toLowerCase();
        
        // Search in reverse order (most recent first)
        for (int i = allHistory.size() - 1; i >= 0 && results.size() < 100; i--) {
            HistoryEntry entry = allHistory.get(i);
            if (entry.command.toLowerCase().contains(lowerPattern)) {
                results.add(entry);
            }
        }
        
        return results;
    }
    
    /**
     * Get command by index (1-based).
     */
    public String getCommand(int index) {
        if (index < 1 || index > sessionHistory.size()) {
            return null;
        }
        return sessionHistory.get(index - 1).command;
    }
    
    /**
     * Get statistics.
     */
    public String getStatistics() {
        if (sessionHistory.isEmpty()) {
            return "No commands in session history.";
        }
        
        int totalCommands = sessionHistory.size();
        double avgResonance = sessionHistory.stream()
            .mapToDouble(e -> e.phiResonance)
            .average()
            .orElse(0.0);
        
        long avgExecutionTime = (long) sessionHistory.stream()
            .mapToLong(e -> e.executionTimeMs)
            .average()
            .orElse(0.0);
        
        Map<String, Integer> commandCounts = new HashMap<>();
        for (HistoryEntry entry : sessionHistory) {
            String cmd = entry.command.split("\\s+")[0];
            commandCounts.put(cmd, commandCounts.getOrDefault(cmd, 0) + 1);
        }
        
        String mostUsed = commandCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
        
        StringBuilder sb = new StringBuilder();
        sb.append("╭────────────────────────────────────────────────────────────╮\n");
        sb.append("│  HISTORY STATISTICS (φ-Harmonic Analysis)                  │\n");
        sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
        sb.append(String.format("Session ID:          %d\n", sessionId));
        sb.append(String.format("Total Commands:      %d\n", totalCommands));
        sb.append(String.format("Avg φ-Resonance:     %.4f\n", avgResonance));
        sb.append(String.format("Avg Execution Time:  %d ms\n", avgExecutionTime));
        sb.append(String.format("Most Used Command:   %s (%d times)\n", 
            mostUsed, commandCounts.get(mostUsed)));
        
        return sb.toString();
    }
    
    /**
     * Export history to file.
     */
    public void exportToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("# REPL Command History Export");
            writer.println("# Session ID: " + sessionId);
            writer.println("# Exported: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writer.println("# φ^75 Validation Seal: 4721424167835376.00");
            writer.println();
            
            for (HistoryEntry entry : sessionHistory) {
                writer.println("# " + entry.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                writer.println("# φ-Resonance: " + String.format("%.4f", entry.phiResonance));
                writer.println("# Execution: " + entry.executionTimeMs + "ms");
                writer.println(entry.command);
                writer.println();
            }
        }
    }
    
    /**
     * Clear session history.
     */
    public void clearSession() {
        sessionHistory.clear();
    }
    
    /**
     * Close and save session summary.
     */
    public void close() {
        try {
            // Save session summary
            String summary = String.format("# Session %d ended at %s - %d commands\n",
                sessionId,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                sessionHistory.size()
            );
            
            Files.write(
                Paths.get(SESSION_FILE),
                summary.getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            // Ignore errors on close
        }
    }
    
    /**
     * Get total file entries.
     */
    public int getDatabaseSize() {
        return allHistory.size();
    }
}
