package gemini.root;

import gemini.root.ConversationMemory.Mode;
import java.util.Locale;
import java.util.Map;

/**
 * COMMAND ROUTER: Java chooses cognition mode.
 * 
 * Routes user input to appropriate handlers:
 * - TRANSMUTE: file ingestion
 * - /mode: switch cognitive mode
 * - /prove: one-shot proof mode
 * - /derive: one-shot derivation mode
 * - default: use current persistent mode
 */
public final class CommandRouter {

    public static class Route {
        public final boolean handled;
        public final String immediateReply;   // for commands that return immediately
        public final Mode mode;               // cognitive mode for the LLM call
        public final String userText;         // cleaned user query for the model
        public final Map<String, Object> formatSchema; // null for plain text

        private Route(boolean handled, String immediateReply, Mode mode, String userText, Map<String, Object> formatSchema) {
            this.handled = handled;
            this.immediateReply = immediateReply;
            this.mode = mode;
            this.userText = userText;
            this.formatSchema = formatSchema;
        }

        public static Route immediate(String msg) { 
            return new Route(true, msg, Mode.CHAT, null, null); 
        }
        
        public static Route llm(Mode mode, String userText, Map<String, Object> schema) {
            return new Route(false, null, mode, userText, schema);
        }
    }

    private CommandRouter() {}

    public static Route route(String raw, ConversationMemory mem, Transmudder soul) {
        String msg = raw == null ? "" : raw.trim();
        if (msg.isEmpty()) return Route.immediate("Say something.");

        // Ingest command
        if (msg.toUpperCase(Locale.ROOT).startsWith("TRANSMUTE:")) {
            String path = msg.substring("TRANSMUTE:".length()).trim();
            soul.transmuteFile(path);
            return Route.immediate("Absorbed: " + path);
        }

        // Mode switching
        if (msg.toLowerCase(Locale.ROOT).startsWith("/mode")) {
            String[] parts = msg.split("\\s+", 2);
            String m = parts.length > 1 ? parts[1].trim().toLowerCase(Locale.ROOT) : "";
            switch (m) {
                case "prove" -> mem.setMode(Mode.PROVE);
                case "derive" -> mem.setMode(Mode.DERIVE);
                default -> mem.setMode(Mode.CHAT);
            }
            return Route.immediate("Mode set to: " + mem.getMode());
        }

        // One-shot cognitive commands
        if (msg.toLowerCase(Locale.ROOT).startsWith("/prove") || msg.toUpperCase(Locale.ROOT).startsWith("PROVE:")) {
            String q = msg.contains(":") ? msg.substring(msg.indexOf(":") + 1).trim() 
                                         : msg.replaceFirst("(?i)/prove", "").trim();
            return Route.llm(Mode.PROVE, q, Schemas.proofSchema());
        }

        if (msg.toLowerCase(Locale.ROOT).startsWith("/derive") || msg.toUpperCase(Locale.ROOT).startsWith("DERIVE:")) {
            String q = msg.contains(":") ? msg.substring(msg.indexOf(":") + 1).trim() 
                                         : msg.replaceFirst("(?i)/derive", "").trim();
            return Route.llm(Mode.DERIVE, q, Schemas.derivationSchema());
        }

        // Default: use current persistent mode
        Mode mode = mem.getMode();
        return switch (mode) {
            case PROVE -> Route.llm(Mode.PROVE, msg, Schemas.proofSchema());
            case DERIVE -> Route.llm(Mode.DERIVE, msg, Schemas.derivationSchema());
            default -> Route.llm(Mode.CHAT, msg, null);
        };
    }
}
