package io.fraymus.ai.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SESSION MEMORY - Per-Connection Chat History
 * 
 * Maintains bounded conversation history for each session/connection
 */
public class SessionMemory {

    private final Map<String, Deque<OllamaClient.Message>> sessions = new ConcurrentHashMap<>();
    private final int maxMessages;

    public SessionMemory(int maxMessages) {
        this.maxMessages = maxMessages;
    }

    /**
     * PUSH MESSAGE
     * Add message to session history (bounded)
     */
    public void push(String sessionId, String role, String content) {
        Deque<OllamaClient.Message> history = sessions.computeIfAbsent(sessionId, k -> new ArrayDeque<>());
        
        history.addLast(new OllamaClient.Message(role, content));
        
        // Keep only last N messages (excluding system messages)
        while (history.size() > maxMessages) {
            OllamaClient.Message first = history.peekFirst();
            if (first != null && !"system".equals(first.role)) {
                history.removeFirst();
            } else {
                break;
            }
        }
    }

    /**
     * SNAPSHOT
     * Get immutable copy of session history
     */
    public List<OllamaClient.Message> snapshot(String sessionId) {
        Deque<OllamaClient.Message> history = sessions.get(sessionId);
        if (history == null) return List.of();
        return new ArrayList<>(history);
    }

    /**
     * CLEAR SESSION
     */
    public void clear(String sessionId) {
        sessions.remove(sessionId);
    }

    /**
     * CLEAR ALL
     */
    public void clearAll() {
        sessions.clear();
    }

    /**
     * SIZE
     */
    public int size(String sessionId) {
        Deque<OllamaClient.Message> history = sessions.get(sessionId);
        return history == null ? 0 : history.size();
    }
}
