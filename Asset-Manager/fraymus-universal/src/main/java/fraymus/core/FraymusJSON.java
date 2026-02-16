package fraymus.core;

import java.util.*;

/**
 * 🧱 FRAYMUS JSON (Internalized)
 * Zero-dependency parser.
 * "We do not need Gson. We parse the brackets ourselves."
 */
public class FraymusJSON {

    public static Object parse(String json) {
        json = json.trim();
        if (json.startsWith("{")) return parseObject(json);
        if (json.startsWith("[")) return parseArray(json);
        if (json.startsWith("\"")) return json.substring(1, json.length() - 1);
        if (json.equals("true")) return Boolean.TRUE;
        if (json.equals("false")) return Boolean.FALSE;
        if (json.equals("null")) return null;
        try { return Double.parseDouble(json); } catch (Exception e) { return json; }
    }

    private static Map<String, Object> parseObject(String json) {
        Map<String, Object> map = new HashMap<>();
        String inner = json.substring(1, json.lastIndexOf('}')).trim();
        if (inner.isEmpty()) return map;

        List<String> tokens = splitTopLevel(inner);
        for (String token : tokens) {
            String[] parts = splitKeyVal(token);
            String key = parts[0].trim();
            key = key.substring(1, key.length() - 1); // Remove quotes
            map.put(key, parse(parts[1].trim()));
        }
        return map;
    }

    private static List<Object> parseArray(String json) {
        List<Object> list = new ArrayList<>();
        String inner = json.substring(1, json.lastIndexOf(']')).trim();
        if (inner.isEmpty()) return list;

        List<String> tokens = splitTopLevel(inner);
        for (String token : tokens) list.add(parse(token.trim()));
        return list;
    }

    // UTILITY: Handles nested brackets correctly during split
    private static List<String> splitTopLevel(String s) {
        List<String> result = new ArrayList<>();
        int depth = 0;
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : s.toCharArray()) {
            if (c == '\"') inQuotes = !inQuotes;
            if (!inQuotes) {
                if (c == '{' || c == '[') depth++;
                if (c == '}' || c == ']') depth--;
            }
            if (c == ',' && depth == 0 && !inQuotes) {
                result.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString());
        return result;
    }

    private static String[] splitKeyVal(String s) {
        int splitIndex = s.indexOf(':');
        return new String[]{s.substring(0, splitIndex), s.substring(splitIndex + 1)};
    }
    
    public static String stringify(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + obj + "\"";
        if (obj instanceof Map) {
            StringBuilder sb = new StringBuilder("{");
            Map<?,?> m = (Map<?,?>) obj;
            int i=0;
            for(Map.Entry<?,?> e : m.entrySet()) {
                if(i++ > 0) sb.append(",");
                sb.append(stringify(e.getKey())).append(":").append(stringify(e.getValue()));
            }
            return sb.append("}").toString();
        }
        if (obj instanceof List) {
            StringBuilder sb = new StringBuilder("[");
            List<?> l = (List<?>) obj;
            for (int i = 0; i < l.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(stringify(l.get(i)));
            }
            return sb.append("]").toString();
        }
        return obj.toString();
    }
}
