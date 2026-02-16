package fraymus.ollama;

import fraymus.ollama.CommandRouter.Mode;
import java.util.*;

/**
 * CONVERSATION MEMORY - Bounded Message History
 * 
 * Real messages[] array instead of stuffing everything into one prompt
 * 
 * Features:
 * - Bounded history (prevents context overflow)
 * - Role-based messages (system/user/assistant)
 * - Persistent cognitive mode
 * - Automatic history pruning
 */
public class ConversationMemory {

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

    /**
     * SET MODE
     * Changes persistent cognitive mode
     */
    public void setMode(Mode m) {
        this.mode = m;
    }

    /**
     * GET MODE
     */
    public Mode getMode() {
        return this.mode;
    }

    /**
     * ADD USER MESSAGE
     */
    public void addUser(String text) {
        push(new Msg("user", text));
    }

    /**
     * ADD ASSISTANT MESSAGE
     */
    public void addAssistant(String text) {
        push(new Msg("assistant", text));
    }

    /**
     * PUSH MESSAGE
     * Adds message and prunes if over limit
     */
    private void push(Msg m) {
        history.addLast(m);
        while (history.size() > maxMessages) {
            history.removeFirst();
        }
    }

    /**
     * BUILD MESSAGE LIST
     * Creates complete message array for Ollama /api/chat
     */
    public List<Msg> build(String systemPrompt, String userText) {
        List<Msg> out = new ArrayList<>();
        out.add(new Msg("system", systemPrompt));
        out.addAll(history);
        out.add(new Msg("user", userText));
        return out;
    }

    /**
     * CLEAR HISTORY
     */
    public void clear() {
        history.clear();
    }

    /**
     * GET SIZE
     */
    public int size() {
        return history.size();
    }

    /**
     * Print statistics
     */
    public void printStats() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   CONVERSATION MEMORY STATISTICS                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Current Mode: " + mode);
        System.out.println("  Messages in History: " + history.size());
        System.out.println("  Max Messages: " + maxMessages);
        
        if (!history.isEmpty()) {
            int userCount = 0;
            int assistantCount = 0;
            for (Msg m : history) {
                if ("user".equals(m.role)) userCount++;
                if ("assistant".equals(m.role)) assistantCount++;
            }
            System.out.println("  User Messages: " + userCount);
            System.out.println("  Assistant Messages: " + assistantCount);
        }
    }
}
