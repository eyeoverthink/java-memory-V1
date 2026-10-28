package fraymus.contract;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Shared contract for machine-checkable grounded system state reporting.
 */
public final class SystemStateContract {

    public static final String QUERY_ROUTE_KEY = "queryRoute";
    public static final String ROUTE_SYSTEM_STATE = "SYSTEM_STATE";
    public static final String QUERY_HINT_KEY = "systemState";
    public static final String ENVELOPE_KEY = "systemStateEnvelope";

    private SystemStateContract() {
        // Static utility.
    }

    public static boolean isSystemStateQuery(String query, Map<String, Object> context) {
        if (context != null) {
            Object route = context.get(QUERY_ROUTE_KEY);
            if (route != null && ROUTE_SYSTEM_STATE.equalsIgnoreCase(String.valueOf(route).trim())) {
                return true;
            }
            Object hint = context.get(QUERY_HINT_KEY);
            if (Boolean.TRUE.equals(hint)) {
                return true;
            }
            if (hint != null && "true".equalsIgnoreCase(String.valueOf(hint).trim())) {
                return true;
            }
        }

        if (query == null || query.isBlank()) {
            return false;
        }

        String lowered = query.toLowerCase(Locale.ROOT).trim();
        if (lowered.equals("status") || lowered.equals("state") || lowered.equals("system state")) {
            return true;
        }
        if (lowered.contains("current system and state")) {
            return true;
        }
        boolean mentionsState = lowered.contains("system state")
            || lowered.contains("current state")
            || lowered.contains("runtime state")
            || lowered.contains("status");
        boolean introspective = lowered.contains("describe")
            || lowered.contains("what")
            || lowered.contains("how")
            || lowered.contains("current");
        boolean selfRef = lowered.contains("your ")
            || lowered.contains("you ");
        return mentionsState && (introspective || selfRef);
    }

    public static Map<String, Object> createEnvelope(
        Map<String, Object> boot,
        Map<String, Object> memory,
        Map<String, Object> ollama,
        Map<String, Object> cores,
        List<Map<String, Object>> subsystemHealth,
        Map<String, Object> provenance
    ) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("timestamp", Instant.now().toString());
        envelope.put("boot", copyMap(boot));
        envelope.put("memory", copyMap(memory));
        envelope.put("ollama", copyMap(ollama));
        envelope.put("cores", copyMap(cores));
        envelope.put("subsystemHealth", copyList(subsystemHealth));
        envelope.put("provenance", copyMap(provenance));
        return envelope;
    }

    public static Map<String, Object> normalizeEnvelope(Map<String, Object> rawEnvelope) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("timestamp", timestampValue(rawEnvelope == null ? null : rawEnvelope.get("timestamp")));
        envelope.put("boot", coerceMap(rawEnvelope == null ? null : rawEnvelope.get("boot")));
        envelope.put("memory", coerceMap(rawEnvelope == null ? null : rawEnvelope.get("memory")));
        envelope.put("ollama", coerceMap(rawEnvelope == null ? null : rawEnvelope.get("ollama")));
        envelope.put("cores", coerceMap(rawEnvelope == null ? null : rawEnvelope.get("cores")));
        envelope.put("subsystemHealth", coerceListOfMaps(rawEnvelope == null ? null : rawEnvelope.get("subsystemHealth")));
        envelope.put("provenance", coerceMap(rawEnvelope == null ? null : rawEnvelope.get("provenance")));
        return envelope;
    }

    public static String renderEnvelope(Map<String, Object> rawEnvelope) {
        Map<String, Object> envelope = normalizeEnvelope(rawEnvelope);
        StringBuilder sb = new StringBuilder();
        sb.append("Grounded SYSTEM_STATE envelope (machine-checkable)\n");
        sb.append("timestamp=").append(envelope.get("timestamp")).append('\n');
        sb.append("boot: ").append(compactMap(coerceMap(envelope.get("boot")))).append('\n');
        sb.append("memory: ").append(compactMap(coerceMap(envelope.get("memory")))).append('\n');
        sb.append("ollama: ").append(compactMap(coerceMap(envelope.get("ollama")))).append('\n');
        sb.append("cores: ").append(compactMap(coerceMap(envelope.get("cores")))).append('\n');

        List<Map<String, Object>> subsystemHealth = coerceListOfMaps(envelope.get("subsystemHealth"));
        if (subsystemHealth.isEmpty()) {
            sb.append("subsystemHealth: []\n");
        } else {
            sb.append("subsystemHealth:\n");
            for (Map<String, Object> item : subsystemHealth) {
                String name = Objects.toString(item.getOrDefault("name", "unknown"), "unknown");
                String status = Objects.toString(item.getOrDefault("status", "unknown"), "unknown");
                String score = Objects.toString(item.getOrDefault("score", "?"), "?");
                sb.append("  - ").append(name)
                    .append(" | status=").append(status)
                    .append(" | score=").append(score);
                if (item.containsKey("detail")) {
                    sb.append(" | detail=").append(Objects.toString(item.get("detail"), ""));
                }
                sb.append('\n');
            }
        }

        sb.append("provenance: ").append(compactMap(coerceMap(envelope.get("provenance"))));
        return sb.toString();
    }

    private static String timestampValue(Object value) {
        if (value == null) {
            return Instant.now().toString();
        }
        String string = String.valueOf(value).trim();
        return string.isBlank() ? Instant.now().toString() : string;
    }

    private static Map<String, Object> copyMap(Map<String, Object> map) {
        if (map == null) {
            return new LinkedHashMap<>();
        }
        return new LinkedHashMap<>(map);
    }

    private static List<Map<String, Object>> copyList(List<Map<String, Object>> list) {
        List<Map<String, Object>> copied = new ArrayList<>();
        if (list == null) {
            return copied;
        }
        for (Map<String, Object> item : list) {
            copied.add(copyMap(item));
        }
        return copied;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> coerceMap(Object value) {
        if (!(value instanceof Map<?, ?> rawMap)) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            String key = String.valueOf(entry.getKey());
            map.put(key, entry.getValue());
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> coerceListOfMaps(Object value) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (!(value instanceof List<?> rawList)) {
            return list;
        }
        for (Object item : rawList) {
            if (item instanceof Map<?, ?>) {
                list.add(coerceMap(item));
            }
        }
        return list;
    }

    private static String compactMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        StringJoiner joiner = new StringJoiner(", ", "{", "}");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            joiner.add(entry.getKey() + "=" + Objects.toString(entry.getValue(), ""));
        }
        return joiner.toString();
    }
}
