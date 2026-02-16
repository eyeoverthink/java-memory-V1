/**
 * HistoryCommands.java - Interactive History Commands
 * 
 * Enhanced history commands with search, replay, export, and statistics.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Register all history-related commands.
 */
public class HistoryCommands {
    
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    /**
     * Register all history commands.
     */
    public static void registerAll(ReplCommandRegistry registry, JavaRepl repl, HistoryManager historyManager) {
        
        // :HISTORY - Show session history with enhanced display
        registry.register(":history",
            args -> {
                List<HistoryManager.HistoryEntry> history = historyManager.getSessionHistory();
                if (history.isEmpty()) {
                    return "No commands in history.";
                }
                
                int limit = 50; // Default limit
                int start = 0;
                
                // Parse arguments: :history [limit] or :history [start] [limit]
                if (args.size() == 1) {
                    try {
                        limit = Integer.parseInt(args.get(0));
                    } catch (NumberFormatException e) {
                        return "Invalid limit: " + args.get(0);
                    }
                } else if (args.size() == 2) {
                    try {
                        start = Integer.parseInt(args.get(0));
                        limit = Integer.parseInt(args.get(1));
                    } catch (NumberFormatException e) {
                        return "Invalid range: " + args.get(0) + " " + args.get(1);
                    }
                }
                
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────────────────────╮\n");
                sb.append("│  COMMAND HISTORY (φ-Resonance Preserved)                                   │\n");
                sb.append("╰────────────────────────────────────────────────────────────────────────────╯\n\n");
                
                int end = Math.min(start + limit, history.size());
                for (int i = start; i < end; i++) {
                    HistoryManager.HistoryEntry entry = history.get(i);
                    String time = entry.timestamp.format(TIME_FORMAT);
                    String phiStr = String.format("φ:%.3f", entry.phiResonance);
                    String execStr = String.format("%dms", entry.executionTimeMs);
                    
                    sb.append(String.format("%4d  [%s] [%s] [%s]  %s\n", 
                        i + 1, time, phiStr, execStr, entry.command));
                }
                
                sb.append(String.format("\nShowing %d-%d of %d commands", 
                    start + 1, end, history.size()));
                sb.append("\nUse: :history [limit] or :history [start] [limit]");
                
                return sb.toString();
            },
            "Show command history with φ-resonance and timing",
            ":history [limit] or :history [start] [limit]");
        
        // :HSEARCH - Search history
        registry.register(":hsearch",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :hsearch <pattern>";
                }
                
                String pattern = String.join(" ", args);
                List<HistoryManager.HistoryEntry> results = historyManager.search(pattern);
                
                if (results.isEmpty()) {
                    return "No matches found for: " + pattern;
                }
                
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────────────────────╮\n");
                sb.append(String.format("│  SEARCH RESULTS: \"%s\"", pattern));
                sb.append(" ".repeat(Math.max(0, 76 - pattern.length() - 21)));
                sb.append("│\n");
                sb.append("╰────────────────────────────────────────────────────────────────────────────╯\n\n");
                
                for (HistoryManager.HistoryEntry entry : results) {
                    String time = entry.timestamp.format(TIME_FORMAT);
                    sb.append(String.format("%4d  [%s]  %s\n", entry.id, time, entry.command));
                }
                
                sb.append(String.format("\nFound %d matches", results.size()));
                
                return sb.toString();
            },
            "Search command history by pattern",
            ":hsearch <pattern>");
        
        // :HDBSEARCH - Search all database history
        registry.register(":hdbsearch",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :hdbsearch <pattern>";
                }
                
                String pattern = String.join(" ", args);
                List<HistoryManager.HistoryEntry> results = historyManager.searchDatabase(pattern);
                
                if (results.isEmpty()) {
                    return "No matches found in database for: " + pattern;
                }
                
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────────────────────╮\n");
                sb.append(String.format("│  DATABASE SEARCH: \"%s\"", pattern));
                sb.append(" ".repeat(Math.max(0, 76 - pattern.length() - 21)));
                sb.append("│\n");
                sb.append("╰────────────────────────────────────────────────────────────────────────────╯\n\n");
                
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd HH:mm");
                for (HistoryManager.HistoryEntry entry : results) {
                    String time = entry.timestamp.format(dateFormat);
                    String session = String.format("S%d", entry.sessionId);
                    sb.append(String.format("%6d  [%s] [%s]  %s\n", 
                        entry.id, time, session, entry.command));
                }
                
                sb.append(String.format("\nFound %d matches (showing last 100)", results.size()));
                
                return sb.toString();
            },
            "Search all history in database",
            ":hdbsearch <pattern>");
        
        // :HREPLAY - Replay command by number
        registry.register(":hreplay",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :hreplay <number>";
                }
                
                try {
                    int index = Integer.parseInt(args.get(0));
                    String command = historyManager.getCommand(index);
                    
                    if (command == null) {
                        return "Invalid history index: " + index;
                    }
                    
                    return "REPLAY:" + command; // Special prefix for REPL to execute
                    
                } catch (NumberFormatException e) {
                    return "Invalid number: " + args.get(0);
                }
            },
            "Replay command from history by number",
            ":hreplay <number>");
        
        // :HSTATS - Show history statistics
        registry.register(":hstats",
            args -> historyManager.getStatistics(),
            "Show history statistics and φ-harmonic analysis",
            ":hstats");
        
        // :HEXPORT - Export history to file
        registry.register(":hexport",
            args -> {
                String filename = args.isEmpty() ? "history_export.txt" : args.get(0);
                
                try {
                    historyManager.exportToFile(filename);
                    return String.format("History exported to: %s", filename);
                } catch (IOException e) {
                    return "Export failed: " + e.getMessage();
                }
            },
            "Export history to file",
            ":hexport [filename]");
        
        // :HCLEAR - Clear session history
        registry.register(":hclear",
            args -> {
                int size = historyManager.getSessionHistory().size();
                historyManager.clearSession();
                return String.format("Cleared %d commands from session history", size);
            },
            "Clear session history (database preserved)",
            ":hclear");
        
        // :HDBSIZE - Show database size
        registry.register(":hdbsize",
            args -> {
                int dbSize = historyManager.getDatabaseSize();
                int sessionSize = historyManager.getSessionHistory().size();
                
                StringBuilder sb = new StringBuilder();
                sb.append("╭────────────────────────────────────────────────────────────╮\n");
                sb.append("│  HISTORY DATABASE STATUS                                   │\n");
                sb.append("╰────────────────────────────────────────────────────────────╯\n\n");
                sb.append(String.format("Session History:   %d commands\n", sessionSize));
                sb.append(String.format("Database Total:    %d commands\n", dbSize));
                sb.append(String.format("Database File:     repl_history.db\n"));
                
                return sb.toString();
            },
            "Show history database size and status",
            ":hdbsize");
    }
}
