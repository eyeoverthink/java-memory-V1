package brain_architecture.kernel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Writes durable markdown memory artifacts that capture current kernel state and forward goals.
 */
public final class ProjectMemoryArtifactWriter {
    private static final Path CANONICAL_DOC_PATH = Path.of(
        "Java-Memory",
        "Asset-Manager",
        "brain_architecture",
        "NORTHSTAR_KERNEL_MEMORY_AND_FORECAST.md"
    );
    private static final Path ARCHITECTURE_DOC_PATH = Path.of(
        "Java-Memory",
        "Asset-Manager",
        "brain_architecture",
        "NORTHSTAR_KERNEL_ARCHITECTURE.md"
    );
    private static final Path GOALS_DOC_PATH = Path.of(
        "Java-Memory",
        "Asset-Manager",
        "brain_architecture",
        "KERNEL_GOALS_FORECASTS.md"
    );
    private static final Path TRUE_OUTLINE_PATH = Path.of("FRAYNIX_TRUE_OUTLINE.md");
    private static final Path AUDIT_MATRIX_PATH = Path.of("FRAYNIX_IMPLEMENTATION_AUDIT_MATRIX.tsv");
    private static final Path CONTINUITY_PROTOCOL_PATH = Path.of("FRAYNIX_CONTINUITY_PROTOCOL.md");

    private final KernelConfiguration configuration;
    private final RepairMemoryStore repairMemoryStore;

    public ProjectMemoryArtifactWriter(KernelConfiguration configuration, RepairMemoryStore repairMemoryStore) {
        this.configuration = configuration;
        this.repairMemoryStore = repairMemoryStore;
    }

    public MemoryArtifacts writeArtifacts(Map<String, Object> status) throws IOException {
        Path generatedRoot = configuration.getStateRoot().resolve("generated");
        Files.createDirectories(generatedRoot);

        Path operationalMemory = generatedRoot.resolve("SELF_BUILD_MEMORY.md");
        Path forecastMemory = generatedRoot.resolve("SELF_BUILD_FORECAST.md");
        Path canonicalDoc = configuration.getWorkspaceRoot().resolve(CANONICAL_DOC_PATH).normalize();
        Path architectureDoc = configuration.getWorkspaceRoot().resolve(ARCHITECTURE_DOC_PATH).normalize();
        Path goalsDoc = configuration.getWorkspaceRoot().resolve(GOALS_DOC_PATH).normalize();
        Path trueOutlineDoc = configuration.getWorkspaceRoot().resolve(TRUE_OUTLINE_PATH).normalize();
        Path auditMatrixDoc = configuration.getWorkspaceRoot().resolve(AUDIT_MATRIX_PATH).normalize();
        Path continuityProtocolDoc = configuration.getWorkspaceRoot().resolve(CONTINUITY_PROTOCOL_PATH).normalize();

        Files.writeString(operationalMemory, buildOperationalMemory(status, canonicalDoc, architectureDoc, goalsDoc), StandardCharsets.UTF_8);
        Files.writeString(forecastMemory, buildForecastMemory(status, canonicalDoc, architectureDoc, goalsDoc), StandardCharsets.UTF_8);

        List<Path> artifacts = new ArrayList<>();
        List<Path> dualCanonDocs = new ArrayList<>();
        if (Files.exists(canonicalDoc)) {
            artifacts.add(configuration.getWorkspaceRoot().relativize(canonicalDoc));
        }
        if (Files.exists(architectureDoc)) {
            artifacts.add(configuration.getWorkspaceRoot().relativize(architectureDoc));
        }
        if (Files.exists(goalsDoc)) {
            artifacts.add(configuration.getWorkspaceRoot().relativize(goalsDoc));
        }
        if (Files.exists(trueOutlineDoc)) {
            Path relative = configuration.getWorkspaceRoot().relativize(trueOutlineDoc);
            dualCanonDocs.add(relative);
            artifacts.add(relative);
        }
        if (Files.exists(auditMatrixDoc)) {
            Path relative = configuration.getWorkspaceRoot().relativize(auditMatrixDoc);
            dualCanonDocs.add(relative);
            artifacts.add(relative);
        }
        if (Files.exists(continuityProtocolDoc)) {
            Path relative = configuration.getWorkspaceRoot().relativize(continuityProtocolDoc);
            dualCanonDocs.add(relative);
            artifacts.add(relative);
        }
        artifacts.add(configuration.getWorkspaceRoot().relativize(operationalMemory));
        artifacts.add(configuration.getWorkspaceRoot().relativize(forecastMemory));
        return new MemoryArtifacts(artifacts, dualCanonDocs, canonicalDoc, architectureDoc, goalsDoc, operationalMemory, forecastMemory);
    }

    private String buildOperationalMemory(Map<String, Object> status, Path canonicalDoc, Path architectureDoc, Path goalsDoc) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Self-Build Memory\n\n");
        builder.append("Generated: ").append(Instant.now()).append("\n\n");
        builder.append("## Purpose\n\n");
        builder.append("This artifact is an operational memory for the offline Fraynix kernel. It records what the system currently is, how it reached this state, and which durable assets should be treated as canonical context during future self-improvement passes.\n\n");
        builder.append("## Canonical Documentation\n\n");
        if (Files.exists(canonicalDoc)) {
            builder.append("- Checked-in system narrative: `").append(configuration.getWorkspaceRoot().relativize(canonicalDoc)).append("`\n");
        } else {
            builder.append("- No checked-in canonical memory document was found at `").append(CANONICAL_DOC_PATH).append("`.\n");
        }
        if (Files.exists(architectureDoc)) {
            builder.append("- Checked-in architecture breakdown: `").append(configuration.getWorkspaceRoot().relativize(architectureDoc)).append("`\n");
        }
        if (Files.exists(goalsDoc)) {
            builder.append("- Checked-in goals and forecasts: `").append(configuration.getWorkspaceRoot().relativize(goalsDoc)).append("`\n");
        }
        Path trueOutlineDoc = configuration.getWorkspaceRoot().resolve(TRUE_OUTLINE_PATH).normalize();
        Path auditMatrixDoc = configuration.getWorkspaceRoot().resolve(AUDIT_MATRIX_PATH).normalize();
        Path continuityProtocolDoc = configuration.getWorkspaceRoot().resolve(CONTINUITY_PROTOCOL_PATH).normalize();
        if (Files.exists(trueOutlineDoc)) {
            builder.append("- Strict truth outline: `").append(configuration.getWorkspaceRoot().relativize(trueOutlineDoc)).append("`\n");
        }
        if (Files.exists(auditMatrixDoc)) {
            builder.append("- Claim-to-evidence matrix: `").append(configuration.getWorkspaceRoot().relativize(auditMatrixDoc)).append("`\n");
        }
        if (Files.exists(continuityProtocolDoc)) {
            builder.append("- Session continuity protocol: `").append(configuration.getWorkspaceRoot().relativize(continuityProtocolDoc)).append("`\n");
        }
        builder.append("- Generated runtime memory: `").append(configuration.getWorkspaceRoot().relativize(configuration.getStateRoot().resolve("generated").resolve("SELF_BUILD_MEMORY.md"))).append("`\n");
        builder.append("- Generated forecast memory: `").append(configuration.getWorkspaceRoot().relativize(configuration.getStateRoot().resolve("generated").resolve("SELF_BUILD_FORECAST.md"))).append("`\n\n");

        builder.append("## Current Runtime Snapshot\n\n");
        builder.append("- Workspace root: `").append(configuration.getWorkspaceRoot()).append("`\n");
        builder.append("- State root: `").append(configuration.getStateRoot()).append("`\n");
        builder.append("- Offline enforced: `").append(configuration.isOfflineEnforced()).append("`\n");
        builder.append("- Indexed documents: `").append(status.getOrDefault("documents", 0)).append("`\n");
        builder.append("- Tracked artifacts: `").append(status.getOrDefault("trackedArtifacts", 0)).append("`\n");
        builder.append("- Repair lessons: `").append(status.getOrDefault("repairLessons", 0)).append("`\n");
        builder.append("- Repair templates: `").append(status.getOrDefault("repairTemplates", 0)).append("`\n");
        builder.append("- Truth claims (verified/scaffold/speculative): `")
            .append(status.getOrDefault("truthClaimsVerified", 0))
            .append("/")
            .append(status.getOrDefault("truthClaimsScaffold", 0))
            .append("/")
            .append(status.getOrDefault("truthClaimsSpeculative", 0))
            .append("`\n");
        builder.append("- Canon last refresh: `").append(status.getOrDefault("canonLastRefreshAt", "")).append("`\n");
        builder.append("- Journal entries: `").append(status.getOrDefault("journalEntries", 0)).append("`\n");
        builder.append("- F0 runtime available: `").append(status.getOrDefault("f0Available", false)).append("`\n\n");

        builder.append("## Durable Capability Memory\n\n");
        builder.append("- Typed control flow exists: `TaskEnvelope -> Planner -> Executor -> Verifier -> Memory -> Mutation Engine`.\n");
        builder.append("- Mutation is shadow-first and promotion-only-on-validation.\n");
        builder.append("- The verifier emits structured diagnostics with file, line, column, severity, and message.\n");
        builder.append("- Semantic repairs exist for imports, signature drift, missing methods, missing fields, public type/file mismatches, and package/source-path mismatches.\n");
        builder.append("- PDF distillation produces labeled local corpus memory (`spec`, `code-idea`, `test-idea`, `failure`, `research`, `vision`).\n");
        builder.append("- Successful repairs are persisted as lessons and templates so future mutation proposals can reuse what already worked.\n\n");

        builder.append("## Repair Memory Summary\n\n");
        List<RepairMemoryStore.RepairLesson> lessons = repairMemoryStore.snapshotLessons();
        if (lessons.isEmpty()) {
            builder.append("- No successful repair lessons have been recorded yet.\n");
        } else {
            for (RepairMemoryStore.RepairLesson lesson : lessons.stream().limit(8).toList()) {
                builder.append("- `").append(lesson.repairKind()).append("` successes=`").append(lesson.successCount()).append("` sample=`")
                    .append(lesson.sampleTitle()).append("`\n");
            }
        }
        builder.append("\n## Repair Template Summary\n\n");
        List<RepairMemoryStore.RepairTemplate> templates = repairMemoryStore.snapshotTemplates();
        if (templates.isEmpty()) {
            builder.append("- No repair templates have been derived yet.\n");
        } else {
            for (RepairMemoryStore.RepairTemplate template : templates.stream().limit(8).toList()) {
                String attributes = template.attributes().entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining(", "));
                builder.append("- `").append(template.repairKind()).append("` template=`").append(template.templateKey())
                    .append("` attrs=`").append(attributes).append("` successes=`").append(template.successCount()).append("`\n");
            }
        }
        builder.append("\n## Retrieval Guidance\n\n");
        builder.append("- Use the checked-in markdown narrative for high-level architecture and historical decisions.\n");
        builder.append("- Use the generated memory artifact for current runtime truth and recent repair-memory state.\n");
        builder.append("- Prefer validated repair templates over re-guessing stub shapes when the repair kind matches.\n");
        builder.append("- Never bypass validation gates based on memory alone.\n");
        return builder.toString();
    }

    private String buildForecastMemory(Map<String, Object> status, Path canonicalDoc, Path architectureDoc, Path goalsDoc) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Self-Build Forecast\n\n");
        builder.append("Generated: ").append(Instant.now()).append("\n\n");
        builder.append("## Immediate Goal\n\n");
        builder.append("Raise the system from deterministic self-repair toward deterministic self-construction: the same offline kernel that repairs itself should also improve how it produces future repairs, tests, and reproducible build surfaces.\n\n");
        builder.append("## Near-Term Forecast\n\n");
        builder.append("- Expand template-backed reuse from missing methods and fields into signature repairs and generated regression tests.\n");
        builder.append("- Record repair outcomes as compact local lessons that distinguish successful template reuse from heuristic fallback.\n");
        builder.append("- Improve proposal ranking so repeated successful templates outrank generic generated artifacts even before evolution scoring.\n\n");
        builder.append("## Mid-Term Forecast\n\n");
        builder.append("- Add repair-template families for unresolved methods on foreign types, import-classpath ambiguity resolution, and focused regression scaffolds.\n");
        builder.append("- Introduce mutation benchmarks that measure solve rate, verifier catch rate, rollback success, and template reuse effectiveness.\n");
        builder.append("- Feed distilled corpus failures and local repair memory into one shared proposal surface so design memory and empirical repair memory cooperate.\n\n");
        builder.append("## Long-Term Forecast\n\n");
        builder.append("- Let the kernel generate and validate broader architectural upgrades while still respecting typed boundaries and shadow validation.\n");
        builder.append("- Keep the substrate boundary intact so higher-level intelligence can target user-space, `f0_engine`, and later sovereign runtime backends through one ABI.\n");
        builder.append("- Grow a self-build memory stack that combines canonical checked-in docs, generated runtime memory, replay traces, Merkle journal history, repair lessons, and repair templates.\n\n");
        builder.append("## Current Anchors\n\n");
        builder.append("- Canonical doc exists: `").append(Files.exists(canonicalDoc)).append("`\n");
        builder.append("- Architecture doc exists: `").append(Files.exists(architectureDoc)).append("`\n");
        builder.append("- Goals doc exists: `").append(Files.exists(goalsDoc)).append("`\n");
        builder.append("- Repair lessons: `").append(status.getOrDefault("repairLessons", 0)).append("`\n");
        builder.append("- Repair templates: `").append(status.getOrDefault("repairTemplates", 0)).append("`\n");
        builder.append("- Indexed documents: `").append(status.getOrDefault("documents", 0)).append("`\n");
        builder.append("- Truth claims (verified/scaffold/speculative): `")
            .append(status.getOrDefault("truthClaimsVerified", 0))
            .append("/")
            .append(status.getOrDefault("truthClaimsScaffold", 0))
            .append("/")
            .append(status.getOrDefault("truthClaimsSpeculative", 0))
            .append("`\n");
        return builder.toString();
    }

    public record MemoryArtifacts(
            List<Path> artifactPaths,
            List<Path> dualCanonDocs,
            Path canonicalDoc,
            Path architectureDoc,
            Path goalsDoc,
            Path operationalMemory,
            Path forecastMemory) {
    }
}
