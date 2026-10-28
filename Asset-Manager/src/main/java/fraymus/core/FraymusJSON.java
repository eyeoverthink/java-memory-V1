package fraymus.core;

import java.util.*;

/**
 * 🧱 FRAYMUS JSON (Sovereign Parser)
 * "We do not need Gson. We parse the brackets ourselves."
 * 
 * Zero-dependency recursive descent JSON parser.
 * Converts raw strings into HashMaps and ArrayLists.
 * 
 * SOVEREIGNTY PRINCIPLE:
 * External libraries are dependencies.
 * Dependencies are attack vectors.
 * We forge our own tools.
 * 
 * FEATURES:
 * - Parses objects, arrays, strings, numbers, booleans, null
 * - Handles nested structures
 * - Proper quote and bracket depth tracking
 * - Stringify for serialization
 * 
 * USAGE:
 *   Object data = FraymusJSON.parse("{\"key\": \"value\"}");
 *   String json = FraymusJSON.stringify(data);
 */
public class FraymusJSON {

    /**
     * Parse one complete JSON value from a machine protocol. Unlike the legacy
     * permissive parse method, malformed or trailing input throws an exception.
     * The legacy entry point is intentionally unchanged for existing callers.
     */
    public static Object parseStrict(String json) {
        if (json == null) throw new IllegalArgumentException("JSON input is null");
        StrictReader reader = new StrictReader(json);
        Object value = reader.value(0);
        reader.whitespace();
        if (reader.position != json.length()) throw reader.error("Trailing input");
        return value;
    }

    private static final class StrictReader {
        private final String input;
        private int position;

        private StrictReader(String input) { this.input = input; }

        private IllegalArgumentException error(String message) {
            return new IllegalArgumentException(message + " at character " + position);
        }

        private void whitespace() {
            while (position < input.length()) {
                char c = input.charAt(position);
                if (c != ' ' && c != '\t' && c != '\r' && c != '\n') break;
                position++;
            }
        }

        private boolean take(char expected) {
            if (position < input.length() && input.charAt(position) == expected) {
                position++;
                return true;
            }
            return false;
        }

        private void require(char expected) {
            if (!take(expected)) throw error("Expected '" + expected + "'");
        }

        private Object value(int depth) {
            if (depth > 128) throw error("JSON nesting limit exceeded");
            whitespace();
            if (position == input.length()) throw error("Expected JSON value");
            char c = input.charAt(position);
            if (c == '"') return string();
            if (c == '{') {
                position++;
                Map<String, Object> object = new LinkedHashMap<>();
                whitespace();
                if (take('}')) return object;
                do {
                    whitespace();
                    String key = string();
                    whitespace();
                    require(':');
                    if (object.containsKey(key)) throw error("Duplicate object key");
                    object.put(key, value(depth + 1));
                    whitespace();
                    if (take('}')) return object;
                    require(',');
                } while (true);
            }
            if (c == '[') {
                position++;
                List<Object> array = new ArrayList<>();
                whitespace();
                if (take(']')) return array;
                do {
                    array.add(value(depth + 1));
                    whitespace();
                    if (take(']')) return array;
                    require(',');
                } while (true);
            }
            if (c == 't') return literal("true", Boolean.TRUE);
            if (c == 'f') return literal("false", Boolean.FALSE);
            if (c == 'n') return literal("null", null);
            if (c == '-' || digit(c)) return number();
            throw error("Expected JSON value");
        }

        private Object literal(String text, Object result) {
            if (!input.startsWith(text, position)) throw error("Invalid literal");
            position += text.length();
            return result;
        }

        private String string() {
            require('"');
            StringBuilder result = new StringBuilder();
            while (position < input.length()) {
                char c = input.charAt(position++);
                if (c == '"') return result.toString();
                if (c < 0x20) throw error("Unescaped control character");
                if (c != '\\') {
                    result.append(c);
                    continue;
                }
                if (position == input.length()) throw error("Unfinished escape");
                char escaped = input.charAt(position++);
                switch (escaped) {
                    case '"', '\\', '/' -> result.append(escaped);
                    case 'b' -> result.append('\b');
                    case 'f' -> result.append('\f');
                    case 'n' -> result.append('\n');
                    case 'r' -> result.append('\r');
                    case 't' -> result.append('\t');
                    case 'u' -> {
                        if (position + 4 > input.length()) throw error("Incomplete Unicode escape");
                        int code = 0;
                        for (int i = 0; i < 4; i++) {
                            char hex = input.charAt(position++);
                            int digit = hex >= '0' && hex <= '9' ? hex - '0'
                                : hex >= 'a' && hex <= 'f' ? hex - 'a' + 10
                                : hex >= 'A' && hex <= 'F' ? hex - 'A' + 10 : -1;
                            if (digit < 0) throw error("Invalid Unicode escape");
                            code = code * 16 + digit;
                        }
                        result.append((char) code);
                    }
                    default -> throw error("Invalid string escape");
                }
            }
            throw error("Unterminated string");
        }

        private static boolean digit(char c) { return c >= '0' && c <= '9'; }

        private void digits() {
            int start = position;
            while (position < input.length() && digit(input.charAt(position))) position++;
            if (position == start) throw error("Expected digit");
        }

        private Number number() {
            int start = position;
            take('-');
            if (!take('0')) digits();
            boolean fractional = false;
            if (take('.')) {
                fractional = true;
                digits();
            }
            if (take('e') || take('E')) {
                fractional = true;
                if (!take('+')) take('-');
                digits();
            }
            String text = input.substring(start, position);
            if (!fractional) {
                try { return Long.valueOf(text); }
                catch (NumberFormatException ignored) { /* Try finite double. */ }
            }
            double result = Double.parseDouble(text);
            if (!Double.isFinite(result)) throw error("Non-finite number");
            return result;
        }
    }
    
    /**
     * Parse JSON string into Java objects
     * 
     * @param json Raw JSON string
     * @return Map for objects, List for arrays, primitives for values
     */
    public static Object parse(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        
        json = json.trim();
        
        // Object
        if (json.startsWith("{")) {
            return parseObject(json);
        }
        
        // Array
        if (json.startsWith("[")) {
            return parseArray(json);
        }
        
        // String
        if (json.startsWith("\"")) {
            return parseString(json);
        }
        
        // Boolean
        if (json.equals("true")) {
            return Boolean.TRUE;
        }
        if (json.equals("false")) {
            return Boolean.FALSE;
        }
        
        // Null
        if (json.equals("null")) {
            return null;
        }
        
        // Number
        try {
            if (json.contains(".")) {
                return Double.parseDouble(json);
            } else {
                return Long.parseLong(json);
            }
        } catch (NumberFormatException e) {
            // Fallback: return as string
            return json;
        }
    }
    
    /**
     * Parse JSON object into HashMap
     */
    private static Map<String, Object> parseObject(String json) {
        Map<String, Object> map = new LinkedHashMap<>();
        
        // Remove outer braces
        String inner = json.substring(1, json.lastIndexOf('}')).trim();
        
        if (inner.isEmpty()) {
            return map;
        }
        
        // Split into key-value pairs
        List<String> tokens = splitTopLevel(inner, ',');
        
        for (String token : tokens) {
            String[] parts = splitKeyValue(token);
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                
                // Remove quotes from key
                if (key.startsWith("\"") && key.endsWith("\"")) {
                    key = key.substring(1, key.length() - 1);
                }
                
                // Recursively parse value
                map.put(key, parse(value));
            }
        }
        
        return map;
    }
    
    /**
     * Parse JSON array into ArrayList
     */
    private static List<Object> parseArray(String json) {
        List<Object> list = new ArrayList<>();
        
        // Remove outer brackets
        String inner = json.substring(1, json.lastIndexOf(']')).trim();
        
        if (inner.isEmpty()) {
            return list;
        }
        
        // Split into elements
        List<String> tokens = splitTopLevel(inner, ',');
        
        for (String token : tokens) {
            list.add(parse(token.trim()));
        }
        
        return list;
    }
    
    /**
     * Parse JSON string (remove quotes and handle escapes)
     */
    private static String parseString(String json) {
        if (json.length() < 2) {
            return "";
        }
        
        String str = json.substring(1, json.length() - 1);
        
        // Handle escape sequences
        str = str.replace("\\\"", "\"");
        str = str.replace("\\\\", "\\");
        str = str.replace("\\n", "\n");
        str = str.replace("\\r", "\r");
        str = str.replace("\\t", "\t");
        
        return str;
    }
    
    /**
     * Split string at top level (respecting nested brackets and quotes)
     * 
     * @param s String to split
     * @param delimiter Delimiter character
     * @return List of split tokens
     */
    private static List<String> splitTopLevel(String s, char delimiter) {
        List<String> result = new ArrayList<>();
        
        int depth = 0;
        boolean inQuotes = false;
        boolean escaped = false;
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            
            // Handle escape sequences
            if (escaped) {
                sb.append(c);
                escaped = false;
                continue;
            }
            
            if (c == '\\') {
                escaped = true;
                sb.append(c);
                continue;
            }
            
            // Track quote state
            if (c == '\"') {
                inQuotes = !inQuotes;
                sb.append(c);
                continue;
            }
            
            // Track bracket/brace depth (only outside quotes)
            if (!inQuotes) {
                if (c == '{' || c == '[') {
                    depth++;
                } else if (c == '}' || c == ']') {
                    depth--;
                }
            }
            
            // Split at delimiter if at top level
            if (c == delimiter && depth == 0 && !inQuotes) {
                result.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        
        // Add final token
        if (sb.length() > 0) {
            result.add(sb.toString());
        }
        
        return result;
    }
    
    /**
     * Split key-value pair at colon
     */
    private static String[] splitKeyValue(String s) {
        // Find first colon outside quotes
        boolean inQuotes = false;
        boolean escaped = false;
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            
            if (escaped) {
                escaped = false;
                continue;
            }
            
            if (c == '\\') {
                escaped = true;
                continue;
            }
            
            if (c == '\"') {
                inQuotes = !inQuotes;
                continue;
            }
            
            if (c == ':' && !inQuotes) {
                return new String[]{
                    s.substring(0, i),
                    s.substring(i + 1)
                };
            }
        }
        
        // Fallback
        return new String[]{s, ""};
    }
    
    /**
     * Stringify Java object into JSON
     * 
     * @param obj Object to serialize
     * @return JSON string
     */
    public static String stringify(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        // String
        if (obj instanceof String) {
            return stringifyString((String) obj);
        }
        
        // Boolean
        if (obj instanceof Boolean) {
            return obj.toString();
        }
        
        // Number
        if (obj instanceof Number) {
            return obj.toString();
        }
        
        // Map (Object)
        if (obj instanceof Map) {
            return stringifyObject((Map<?, ?>) obj);
        }
        
        // List (Array)
        if (obj instanceof List) {
            return stringifyArray((List<?>) obj);
        }
        
        // Fallback: toString
        return "\"" + obj.toString() + "\"";
    }
    
    /**
     * Stringify string with proper escaping
     */
    private static String stringifyString(String str) {
        StringBuilder sb = new StringBuilder("\"");
        
        for (char c : str.toCharArray()) {
            switch (c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append("\\u00");
                        sb.append(Character.forDigit(c >>> 4, 16));
                        sb.append(Character.forDigit(c & 15, 16));
                    } else {
                        sb.append(c);
                    }
            }
        }
        
        sb.append("\"");
        return sb.toString();
    }
    
    /**
     * Stringify map as JSON object
     */
    private static String stringifyObject(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        
        int i = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (i++ > 0) {
                sb.append(",");
            }
            
            // Key (always string)
            sb.append(stringify(entry.getKey().toString()));
            sb.append(":");
            
            // Value (recursive)
            sb.append(stringify(entry.getValue()));
        }
        
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Stringify list as JSON array
     */
    private static String stringifyArray(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(stringify(list.get(i)));
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Pretty print JSON with indentation
     */
    public static String prettyPrint(Object obj) {
        return prettyPrint(obj, 0);
    }
    
    private static String prettyPrint(Object obj, int indent) {
        String indentStr = "  ".repeat(indent);
        String nextIndentStr = "  ".repeat(indent + 1);
        
        if (obj == null) {
            return "null";
        }
        
        if (obj instanceof String) {
            return stringifyString((String) obj);
        }
        
        if (obj instanceof Boolean || obj instanceof Number) {
            return obj.toString();
        }
        
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            if (map.isEmpty()) {
                return "{}";
            }
            
            StringBuilder sb = new StringBuilder("{\n");
            int i = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (i++ > 0) {
                    sb.append(",\n");
                }
                sb.append(nextIndentStr);
                sb.append(stringify(entry.getKey().toString()));
                sb.append(": ");
                sb.append(prettyPrint(entry.getValue(), indent + 1));
            }
            sb.append("\n").append(indentStr).append("}");
            return sb.toString();
        }
        
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            if (list.isEmpty()) {
                return "[]";
            }
            
            StringBuilder sb = new StringBuilder("[\n");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    sb.append(",\n");
                }
                sb.append(nextIndentStr);
                sb.append(prettyPrint(list.get(i), indent + 1));
            }
            sb.append("\n").append(indentStr).append("]");
            return sb.toString();
        }
        
        return "\"" + obj.toString() + "\"";
    }
}
