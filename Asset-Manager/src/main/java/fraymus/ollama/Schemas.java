package fraymus.ollama;

import java.util.*;

/**
 * SCHEMAS - Structured Output Definitions
 * 
 * JSON schemas for Ollama's format parameter
 * Enables structured cognition modes: proof, derivation
 * 
 * Ollama supports format: "json" or format: {json schema}
 */
public final class Schemas {
    private Schemas() {}

    /**
     * PROOF SCHEMA
     * Formal mathematical/logical proof structure
     */
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

    /**
     * DERIVATION SCHEMA
     * Mathematical derivation structure
     */
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

    /**
     * EXTRACTION SCHEMA
     * Extract structured data from documents
     */
    public static Map<String, Object> extractionSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("entities", Map.of("type", "array", "items", Map.of("type", "string")));
        props.put("key_facts", Map.of("type", "array", "items", Map.of("type", "string")));
        props.put("relationships", Map.of("type", "array", "items", Map.of("type", "string")));
        props.put("summary", Map.of("type", "string"));

        schema.put("properties", props);
        schema.put("required", List.of("entities", "key_facts", "relationships", "summary"));
        return schema;
    }
}
