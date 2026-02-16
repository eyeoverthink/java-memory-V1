package fraymus.ollama;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PROTOCOL - JSON Message Envelopes
 * 
 * Standardized WebSocket message format:
 * - token: Streaming token from LLM
 * - final: Complete response
 * - info: System information
 * - error: Error message
 * - start: Thinking indicator
 */
public final class Protocol {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Protocol() {}

    /**
     * Create JSON message envelope
     */
    public static String msg(String type, String data) {
        try {
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("type", type);
            out.put("data", data);
            return MAPPER.writeValueAsString(out);
        } catch (Exception e) {
            return "{\"type\":\"error\",\"data\":\"Protocol encode failed\"}";
        }
    }

    /**
     * Create message with metadata
     */
    public static String msgWithMeta(String type, String data, Map<String, Object> meta) {
        try {
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("type", type);
            out.put("data", data);
            if (meta != null && !meta.isEmpty()) {
                out.put("meta", meta);
            }
            return MAPPER.writeValueAsString(out);
        } catch (Exception e) {
            return "{\"type\":\"error\",\"data\":\"Protocol encode failed\"}";
        }
    }
}
