package gemini.root;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PROTOCOL: JSON message helper for WebSocket communication.
 * 
 * Message types:
 * - token: streaming token
 * - final: complete response
 * - info: system information
 * - error: error message
 * - start: stream start signal
 */
public final class Protocol {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private Protocol() {}

    public static String msg(String type, String data) {
        try {
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("type", type);
            out.put("data", data);
            return GSON.toJson(out);
        } catch (Exception e) {
            return "{\"type\":\"error\",\"data\":\"Protocol encode failed\"}";
        }
    }
}
