package gemini.root;

import java.util.*;

/**
 * SCHEMAS: JSON schema definitions for structured outputs.
 * 
 * Ollama supports format: "json" or format: {json schema} on /api/chat.
 * These schemas enforce output contracts for proof/derivation modes.
 */
public final class Schemas {
    private Schemas() {}

    public static Map<String, Object> proofSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("claim", Map.of("type", "string"));
        props.put("definitions", Map.of("type", "array", "items", Map.of("type", "string")));
        props.put("assumptions", Map.of("type", "array", "items", Map.of("type", "string")));
        props.put("steps", Map.of("type", "array", "items", Map.of("type", "string")));
        props.put("result", Map.of("type", "string", "enum", List.of("proved", "disproved", "inconclusive")));
        props.put("confidence", Map.of("type", "number", "minimum", 0, "maximum", 1));
        props.put("notes", Map.of("type", "string"));

        schema.put("properties", props);
        schema.put("required", List.of("claim", "definitions", "assumptions", "steps", "result", "confidence", "notes"));
        return schema;
    }

    public static Map<String, Object> derivationSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("problem", Map.of("type", "string"));
        props.put("given", Map.of("type", "array", "items", Map.of("type", "string")));
        props.put("derive", Map.of("type", "array", "items", Map.of("type", "string")));
        props.put("final_expression", Map.of("type", "string"));
        props.put("sanity_checks", Map.of("type", "array", "items", Map.of("type", "string")));
        props.put("confidence", Map.of("type", "number", "minimum", 0, "maximum", 1));

        schema.put("properties", props);
        schema.put("required", List.of("problem", "given", "derive", "final_expression", "sanity_checks", "confidence"));
        return schema;
    }

    public static Map<String, Object> toolPlanSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> callItem = new LinkedHashMap<>();
        callItem.put("type", "object");
        Map<String, Object> callProps = new LinkedHashMap<>();
        callProps.put("tool", Map.of(
            "type", "string",
            "enum", List.of("none", "calc", "memory_search", "list_files", "write_file", "index_path")
        ));
        callProps.put("args", Map.of("type", "object"));
        callItem.put("properties", callProps);
        callItem.put("required", List.of("tool", "args"));

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("calls", Map.of("type", "array", "maxItems", 3, "items", callItem));
        props.put("intent", Map.of("type", "string"));

        schema.put("properties", props);
        schema.put("required", List.of("calls", "intent"));
        return schema;
    }
}
