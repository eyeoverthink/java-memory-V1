package gemini.root;

import java.util.*;

/**
 * CONVERSATION MEMORY: Bounded message history for multi-turn chat.
 * 
 * Maintains a sliding window of messages to stay within context limits.
 * Supports cognitive mode switching (chat/prove/derive).
 */
public class ConversationMemory {

    public enum Mode { CHAT, PROVE, DERIVE }

    public static class Msg {
        public final String role;     // "system" | "user" | "assistant"
        public final String content;

        public Msg(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    private final Deque<Msg> history = new ArrayDeque<>();
    private final int maxMessages;
    private Mode mode = Mode.CHAT;

    public ConversationMemory(int maxMessages) {
        this.maxMessages = Math.max(6, maxMessages);
    }

    public void setMode(Mode m) { this.mode = m; }
    public Mode getMode() { return this.mode; }

    public void addUser(String text) { push(new Msg("user", text)); }
    public void addAssistant(String text) { push(new Msg("assistant", text)); }

    private void push(Msg m) {
        history.addLast(m);
        while (history.size() > maxMessages) history.removeFirst();
    }

    public List<Msg> build(String systemPrompt, String userText) {
        List<Msg> out = new ArrayList<>();
        out.add(new Msg("system", systemPrompt));
        out.addAll(history);
        out.add(new Msg("user", userText));
        return out;
    }

    public void clear() {
        history.clear();
    }

    public int size() {
        return history.size();
    }
}
