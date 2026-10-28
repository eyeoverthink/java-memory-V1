package fraymus.organism;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * KIWI — Midbrain Swarm Orchestrator
 *
 * Connectomic message-passing between all organism nodes.
 * Each node registers a handler; messages are routed point-to-point or broadcast.
 */
public class SwarmOrchestrator {

    public static class SwarmMessage {
        public final String  from;
        public final String  to;
        public final String  signal;
        public final String  payload;
        public final Instant timestamp;

        public SwarmMessage(String from, String to, String signal, String payload) {
            this.from      = from;
            this.to        = to;
            this.signal    = signal;
            this.payload   = payload;
            this.timestamp = Instant.now();
        }

        @Override
        public String toString() {
            return String.format("[%s→%s] %s: %s", from, to, signal, payload);
        }
    }

    private final Map<String, Consumer<SwarmMessage>> handlers = new ConcurrentHashMap<>();
    private final List<SwarmMessage>                  log      = new CopyOnWriteArrayList<>();
    private boolean active = false;

    public void initialize() { active = true; }

    public void register(String node, Consumer<SwarmMessage> handler) {
        handlers.put(node.toUpperCase(), handler);
    }

    public void route(String from, String to, String signal, String payload) {
        SwarmMessage msg = new SwarmMessage(from, to, signal, payload);
        log.add(msg);
        Consumer<SwarmMessage> h = handlers.get(to.toUpperCase());
        if (h != null) h.accept(msg);
    }

    public void broadcast(String from, String signal, String payload) {
        SwarmMessage msg = new SwarmMessage(from, "*", signal, payload);
        log.add(msg);
        handlers.values().forEach(h -> h.accept(msg));
    }

    public List<SwarmMessage> getLog()  { return new ArrayList<>(log); }
    public int  getNodeCount()          { return handlers.size(); }
    public boolean isActive()           { return active; }

    public String getStatus() {
        return String.format("[KIWI] Nodes=%d Messages=%d Active=%s",
            handlers.size(), log.size(), active);
    }
}
