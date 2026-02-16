package gemini.root;

import org.java_websocket.WebSocket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SessionMemory {
    private final int maxMessages;
    private final int maxTokens;
    private final Map<WebSocket, Deque<OllamaSpine.Msg>> store = new ConcurrentHashMap<>();

    public SessionMemory(int maxMessages, int maxTokens) {
        this.maxMessages = maxMessages;
        this.maxTokens = maxTokens;
    }

    public void init(WebSocket conn) {
        store.put(conn, new ArrayDeque<>());
    }

    public void drop(WebSocket conn) {
        store.remove(conn);
    }

    public void clear(WebSocket conn) {
        store.put(conn, new ArrayDeque<>());
    }

    public void push(WebSocket conn, String role, String content) {
        Deque<OllamaSpine.Msg> history = store.computeIfAbsent(conn, k -> new ArrayDeque<>());
        history.add(new OllamaSpine.Msg(role, content));
        
        // Simple pruning by count
        while (history.size() > maxMessages) {
            history.removeFirst();
        }
        
        // Simple pruning by length (approx 4 chars per token)
        int currentLen = history.stream().mapToInt(m -> m.content.length()).sum();
        while (currentLen > maxTokens * 4 && history.size() > 1) {
             OllamaSpine.Msg removed = history.removeFirst();
             currentLen -= removed.content.length();
        }
    }

    public Deque<OllamaSpine.Msg> snapshot(WebSocket conn) {
        Deque<OllamaSpine.Msg> h = store.get(conn);
        return h == null ? new ArrayDeque<>() : new ArrayDeque<>(h);
    }
}
