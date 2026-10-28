package fraymus.core;

import java.util.List;
import java.util.Map;

public final class FraymusJsonStrictTest {
    private static int checks;
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    private static void rejects(String json) {
        try {
            FraymusJSON.parseStrict(json);
        } catch (IllegalArgumentException expected) {
            checks++;
            return;
        }
        throw new AssertionError("Accepted malformed JSON: " + json);
    }
    public static void main(String[] args) {
        Map<?, ?> value = (Map<?, ?>) FraymusJSON.parseStrict(
            " \n{\"text\":\"C:\\\\camera \\u03c6 \\ud83d\\ude00 \\\"ok\\\"\","
            + "\"values\":[null,true,false,-1,0.25,1e-7,2E+3],\"nested\":{}}\t");
        check(value.get("text").equals("C:\\camera \u03c6 \ud83d\ude00 \"ok\""), "Unicode and escapes");
        List<?> values = (List<?>) value.get("values");
        check(values.get(0) == null && values.get(1).equals(true) && values.get(2).equals(false), "Literals");
        check(values.get(3).equals(-1L), "Integer preserved");
        check(((Number) values.get(5)).doubleValue() == 1e-7, "Negative exponent");
        check(((Number) values.get(6)).doubleValue() == 2000.0, "Positive exponent");
        check(FraymusJSON.parseStrict("[]").equals(List.of()), "Empty array");
        check(FraymusJSON.parseStrict("{}").equals(Map.of()), "Empty object");
        check(FraymusJSON.parseStrict("null") == null, "Root null");
        check(FraymusJSON.parseStrict("\"\\b\\f\\n\\r\\t\\/\\\\\"").equals("\b\f\n\r\t/\\"), "All escapes");
        for (String invalid : new String[] {"", " ", "{", "[", "{\"ok\":true,}", "[1,]",
            "{ok:true}", "{\"ok\" true}", "{\"ok\":true broken}", "{} trailing", "true false",
            "01", "-01", "+1", "1.", ".5", "1e", "1e+", "NaN", "Infinity", "1e309",
            "\"unterminated", "\"\\q\"", "\"\\u12ZZ\"", "\"\\u12\"", "\"raw\nnewline\"",
            "{\"ok\":true,\"ok\":false}"}) rejects(invalid);
        rejects(null);
        rejects("[".repeat(140) + "0" + "]".repeat(140));
        check(FraymusJSON.parse("loose legacy input").equals("loose legacy input"), "Legacy permissive API unchanged");
        Map<?, ?> roundtrip = (Map<?, ?>) FraymusJSON.parseStrict(
            FraymusJSON.stringify(Map.of("key", List.of("quoted \"value\"", 3, true))));
        check(((List<?>) roundtrip.get("key")).get(0).equals("quoted \"value\""), "Existing stringify compatibility");
        for (char control = 0; control < 0x20; control++) {
            String text = "before" + control + "after";
            check(FraymusJSON.parseStrict(FraymusJSON.stringify(text)).equals(text),
                "Stringify escapes JSON control character " + (int) control);
        }
        System.out.println("FraymusJsonStrictTest passed: " + checks + " checks");
    }
}
