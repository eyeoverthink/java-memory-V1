package fraymus.llm;

/**
 * 🧬 CHAT MESSAGE - Gen 131
 * Represents a message in a conversation.
 */
public class ChatMessage {
    public final String role;
    public final String content;
    public final long timestamp;
    
    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }
    
    @Override
    public String toString() {
        return "<|" + role + "|>\n" + content;
    }
}
