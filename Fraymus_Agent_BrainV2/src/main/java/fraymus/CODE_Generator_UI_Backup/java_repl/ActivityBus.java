/**
 * ActivityBus.java - Unified Event System for Cross-System Monitoring
 * 
 * Central event bus that ALL systems broadcast to. The monitor listens
 * and displays everything happening across the entire application.
 * 
 * Systems that broadcast:
 * - Compiler (lex, parse, compile, execute)
 * - Decision Array (votes, consensus, decisions)
 * - Ouroboros (mutations, approvals, evolution)
 * - History (searches, replays, exports)
 * - Infinity Storage (store, retrieve, learn)
 * - Living Code (spawn, evolve, mutate)
 * - Self-Evolving AI (mutations, replications)
 * - Organism (observations, reflections, healing)
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central activity bus for system-wide event broadcasting.
 */
public class ActivityBus {
    
    private static final ActivityBus INSTANCE = new ActivityBus();
    private final List<ActivityListener> listeners = new CopyOnWriteArrayList<>();
    
    /**
     * Activity event types.
     */
    public enum ActivityType {
        // Compiler activities
        COMPILER_LEX,
        COMPILER_PARSE,
        COMPILER_COMPILE,
        COMPILER_EXECUTE,
        COMPILER_ERROR,
        
        // Decision array activities
        DECISION_VOTE,
        DECISION_CONSENSUS,
        DECISION_RESULT,
        
        // Ouroboros activities
        OUROBOROS_MUTATION,
        OUROBOROS_APPROVAL,
        OUROBOROS_REJECTION,
        OUROBOROS_EVOLUTION,
        
        // History activities
        HISTORY_SEARCH,
        HISTORY_REPLAY,
        HISTORY_EXPORT,
        
        // Storage activities
        STORAGE_STORE,
        STORAGE_RETRIEVE,
        STORAGE_LEARN,
        
        // Living code activities
        LIVING_SPAWN,
        LIVING_EVOLVE,
        LIVING_MUTATE,
        
        // Organism activities
        ORGANISM_OBSERVE,
        ORGANISM_REFLECT,
        ORGANISM_HEAL,
        ORGANISM_LEARN,
        
        // General activities
        COMMAND_EXECUTE,
        ERROR_DETECTED,
        SUCCESS
    }
    
    /**
     * Activity event.
     */
    public static class Activity {
        public final ActivityType type;
        public final String system;
        public final String description;
        public final Map<String, Object> data;
        public final LocalDateTime timestamp;
        public final boolean success;
        
        public Activity(ActivityType type, String system, String description, boolean success) {
            this.type = type;
            this.system = system;
            this.description = description;
            this.data = new HashMap<>();
            this.timestamp = LocalDateTime.now();
            this.success = success;
        }
        
        public Activity withData(String key, Object value) {
            data.put(key, value);
            return this;
        }
    }
    
    /**
     * Activity listener interface.
     */
    public interface ActivityListener {
        void onActivity(Activity activity);
    }
    
    /**
     * Get singleton instance.
     */
    public static ActivityBus getInstance() {
        return INSTANCE;
    }
    
    /**
     * Register a listener.
     */
    public void addListener(ActivityListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Unregister a listener.
     */
    public void removeListener(ActivityListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Broadcast an activity.
     */
    public void broadcast(Activity activity) {
        for (ActivityListener listener : listeners) {
            try {
                listener.onActivity(activity);
            } catch (Exception e) {
                // Don't let listener errors break the bus
                System.err.println("Activity listener error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Convenience method - broadcast compiler activity.
     */
    public static void compilerActivity(String action, String code, boolean success) {
        ActivityType type;
        switch (action.toLowerCase()) {
            case "lex": type = ActivityType.COMPILER_LEX; break;
            case "parse": type = ActivityType.COMPILER_PARSE; break;
            case "compile": type = ActivityType.COMPILER_COMPILE; break;
            case "execute": type = ActivityType.COMPILER_EXECUTE; break;
            default: type = ActivityType.COMPILER_ERROR;
        }
        
        getInstance().broadcast(
            new Activity(type, "Compiler", action + ": " + code, success)
                .withData("code", code)
                .withData("action", action)
        );
    }
    
    /**
     * Convenience method - broadcast decision activity.
     */
    public static void decisionActivity(String action, String description, double consensus) {
        ActivityType type;
        switch (action.toLowerCase()) {
            case "vote": type = ActivityType.DECISION_VOTE; break;
            case "consensus": type = ActivityType.DECISION_CONSENSUS; break;
            default: type = ActivityType.DECISION_RESULT;
        }
        
        getInstance().broadcast(
            new Activity(type, "Decision Array", description, true)
                .withData("consensus", consensus)
                .withData("action", action)
        );
    }
    
    /**
     * Convenience method - broadcast Ouroboros activity.
     */
    public static void ouroborosActivity(String action, String description, double consensus, boolean approved) {
        ActivityType type;
        if (approved) {
            type = action.equals("evolve") ? ActivityType.OUROBOROS_EVOLUTION : ActivityType.OUROBOROS_APPROVAL;
        } else {
            type = ActivityType.OUROBOROS_REJECTION;
        }
        
        getInstance().broadcast(
            new Activity(type, "Ouroboros", description, approved)
                .withData("consensus", consensus)
                .withData("approved", approved)
        );
    }
    
    /**
     * Convenience method - broadcast history activity.
     */
    public static void historyActivity(String action, String description, int count) {
        ActivityType type;
        switch (action.toLowerCase()) {
            case "search": type = ActivityType.HISTORY_SEARCH; break;
            case "replay": type = ActivityType.HISTORY_REPLAY; break;
            case "export": type = ActivityType.HISTORY_EXPORT; break;
            default: type = ActivityType.HISTORY_SEARCH;
        }
        
        getInstance().broadcast(
            new Activity(type, "History", description, true)
                .withData("count", count)
                .withData("action", action)
        );
    }
    
    /**
     * Convenience method - broadcast organism activity.
     */
    public static void organismActivity(String action, String description, double consciousness) {
        ActivityType type;
        switch (action.toLowerCase()) {
            case "observe": type = ActivityType.ORGANISM_OBSERVE; break;
            case "reflect": type = ActivityType.ORGANISM_REFLECT; break;
            case "heal": type = ActivityType.ORGANISM_HEAL; break;
            case "learn": type = ActivityType.ORGANISM_LEARN; break;
            default: type = ActivityType.ORGANISM_OBSERVE;
        }
        
        getInstance().broadcast(
            new Activity(type, "Organism", description, true)
                .withData("consciousness", consciousness)
                .withData("action", action)
        );
    }
    
    /**
     * Convenience method - broadcast general command.
     */
    public static void commandActivity(String command, boolean success, long executionTime) {
        getInstance().broadcast(
            new Activity(ActivityType.COMMAND_EXECUTE, "REPL", command, success)
                .withData("command", command)
                .withData("executionTime", executionTime)
        );
    }
    
    /**
     * Convenience method - broadcast error.
     */
    public static void errorActivity(String system, String error) {
        getInstance().broadcast(
            new Activity(ActivityType.ERROR_DETECTED, system, error, false)
                .withData("error", error)
        );
    }
}
