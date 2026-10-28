package fraymus.factory;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SchemaRegistry {
    private final Map<String, ComponentSchema> schemas = new LinkedHashMap<>();

    public synchronized void register(ComponentSchema schema) {
        String key = schema.type() + "@" + schema.version();
        ComponentSchema prior = schemas.putIfAbsent(key, schema);
        if (prior != null && !prior.equals(schema)) throw new IllegalArgumentException("Conflicting schema: " + key);
    }

    public synchronized ComponentSchema require(String type, int version) {
        ComponentSchema schema = schemas.get(type + "@" + version);
        if (schema == null) throw new IllegalArgumentException("Unregistered schema: " + type + "@" + version);
        return schema;
    }
}
