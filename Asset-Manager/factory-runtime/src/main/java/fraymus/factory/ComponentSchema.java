package fraymus.factory;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Versioned data contract. Values stay portable; UI/model classes never enter the core. */
public record ComponentSchema(String type, int version, Map<String, Kind> fields, Set<String> required) {
    public enum Kind { TEXT, NUMBER, BOOLEAN, ENTITY_REFERENCE }

    public ComponentSchema {
        if (type == null || type.isBlank() || version < 1) throw new IllegalArgumentException("Invalid schema identity");
        fields = Map.copyOf(fields);
        required = Set.copyOf(required);
        if (!fields.keySet().containsAll(required)) throw new IllegalArgumentException("Unknown required field");
    }

    public void validate(ComponentRecord component) {
        if (!type.equals(component.type()) || version != component.version()) throw new IllegalArgumentException("Schema mismatch: " + type);
        if (!component.values().keySet().containsAll(required)) throw new IllegalArgumentException("Required fields missing: " + type);
        for (var field : component.values().entrySet()) {
            Kind kind = fields.get(field.getKey());
            if (kind == null) throw new IllegalArgumentException("Unknown field: " + field.getKey());
            String value = field.getValue();
            switch (kind) {
                case NUMBER -> {
                    if (!Double.isFinite(Double.parseDouble(value))) throw new IllegalArgumentException("Non-finite value");
                }
                case BOOLEAN -> {
                    if (!value.equals("true") && !value.equals("false")) throw new IllegalArgumentException("Invalid boolean");
                }
                case ENTITY_REFERENCE -> UUID.fromString(value);
                case TEXT -> { }
            }
        }
    }
}
