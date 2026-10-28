package fraymus.emotion;

import java.util.*;
import java.util.stream.Collectors;

/** Tiny zero-dependency JSON writer/reader used by EmotionEngine (copied from the attached server, made locale-safe). */
public final class EmotionJson {
    private EmotionJson() {}

    public static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    @SuppressWarnings("unchecked")
    public static String toJson(Object o) {
        if (o == null) return "null";
        if (o instanceof String s) return "\"" + esc(s) + "\"";
        if (o instanceof Number n) return n instanceof Double d ? String.format(Locale.ROOT, "%.6f", d) : n.toString();
        if (o instanceof Boolean b) return b.toString();
        if (o instanceof Map<?, ?> m) {
            return "{" + ((Map<String, Object>) m).entrySet().stream()
                .map(e -> "\"" + esc(e.getKey()) + "\":" + toJson(e.getValue()))
                .collect(Collectors.joining(",")) + "}";
        }
        if (o instanceof List<?> l) return "[" + l.stream().map(EmotionJson::toJson).collect(Collectors.joining(",")) + "]";
        return "\"" + esc(o.toString()) + "\"";
    }

    public static Object parse(String json) {
        json = json.trim();
        if (json.startsWith("{")) {
            var map = new LinkedHashMap<String, Object>();
            json = json.substring(1, json.lastIndexOf('}')).trim();
            if (json.isEmpty()) return map;
            for (String pair : splitTop(json)) {
                int colon = pair.indexOf(':');
                if (colon < 0) continue;
                String key = pair.substring(0, colon).trim();
                if (key.startsWith("\"")) key = key.substring(1, key.length() - 1);
                map.put(key, parseVal(pair.substring(colon + 1).trim()));
            }
            return map;
        }
        return parseVal(json);
    }

    private static List<String> splitTop(String s) {
        int depth = 0, start = 0; boolean inStr = false;
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"' && (i == 0 || s.charAt(i - 1) != '\\')) inStr = !inStr;
            if (!inStr) { if (c == '{' || c == '[') depth++; if (c == '}' || c == ']') depth--; }
            if (c == ',' && depth == 0 && !inStr) { parts.add(s.substring(start, i).trim()); start = i + 1; }
        }
        parts.add(s.substring(start).trim());
        return parts;
    }

    private static Object parseVal(String v) {
        v = v.trim();
        if (v.equals("null")) return null;
        if (v.equals("true")) return true;
        if (v.equals("false")) return false;
        if (v.startsWith("\"")) return v.substring(1, v.length() - 1).replace("\\\"", "\"").replace("\\n", "\n").replace("\\\\", "\\");
        if (v.startsWith("{")) return parse(v);
        if (v.startsWith("[")) {
            var list = new ArrayList<Object>();
            v = v.substring(1, v.lastIndexOf(']')).trim();
            if (!v.isEmpty()) for (String p : splitTop(v)) list.add(parseVal(p));
            return list;
        }
        try { return Double.parseDouble(v); } catch (NumberFormatException e) { return v; }
    }

    public static double num(Object o, double dflt) {
        if (o instanceof Number n) return n.doubleValue();
        try { return o == null ? dflt : Double.parseDouble(o.toString()); } catch (Exception e) { return dflt; }
    }
}

