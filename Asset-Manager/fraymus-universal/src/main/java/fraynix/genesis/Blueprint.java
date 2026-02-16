package fraynix.genesis;

import fraynix.core.Artifact;

import java.util.*;

/**
 * BLUEPRINT: Complete software stack specification.
 * 
 * modules + dependencies + entrypoints + tests
 */
public class Blueprint {

    private final String id;
    private final String intent;
    private final long timestamp;
    private final List<Module> modules;
    private final List<String> dependencies;
    private final List<String> entrypoints;
    private final List<String> tests;
    private final Map<String, String> metadata;

    public Blueprint(String intent) {
        this.id = UUID.randomUUID().toString();
        this.intent = intent;
        this.timestamp = System.currentTimeMillis();
        this.modules = new ArrayList<>();
        this.dependencies = new ArrayList<>();
        this.entrypoints = new ArrayList<>();
        this.tests = new ArrayList<>();
        this.metadata = new HashMap<>();
    }

    public String getId() { return id; }
    public String getIntent() { return intent; }
    public long getTimestamp() { return timestamp; }
    public List<Module> getModules() { return modules; }
    public List<String> getDependencies() { return dependencies; }
    public List<String> getEntrypoints() { return entrypoints; }
    public List<String> getTests() { return tests; }
    public Map<String, String> getMetadata() { return metadata; }

    public Blueprint addModule(Module module) {
        modules.add(module);
        return this;
    }

    public Blueprint addDependency(String dep) {
        dependencies.add(dep);
        return this;
    }

    public Blueprint addEntrypoint(String entry) {
        entrypoints.add(entry);
        return this;
    }

    public Blueprint addTest(String test) {
        tests.add(test);
        return this;
    }

    public Blueprint setMetadata(String key, String value) {
        metadata.put(key, value);
        return this;
    }

    public boolean isValid() {
        return !modules.isEmpty() && intent != null && !intent.isEmpty();
    }

    public record Module(
        String name,
        ModuleType type,
        String technology,
        String filePath,
        String template,
        Map<String, String> config
    ) {
        public Module(String name, ModuleType type, String technology, String filePath) {
            this(name, type, technology, filePath, null, Map.of());
        }
    }

    public enum ModuleType {
        BACKEND,
        FRONTEND,
        DATABASE,
        CONFIG,
        TEST,
        DOCS,
        SCRIPT,
        LIBRARY
    }

    @Override
    public String toString() {
        return String.format("Blueprint[%s|%d modules|%s]", 
            intent.length() > 30 ? intent.substring(0, 30) + "..." : intent,
            modules.size(),
            id.substring(0, 8));
    }
}
