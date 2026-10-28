package fraymus.ultimate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Registry that abstracts the major runtime families into one map.
 */
public final class UltimateSubsystemRegistry {

    private final Path repoRoot;
    private final Map<String, UltimateSubsystem> subsystems;

    private UltimateSubsystemRegistry(Path repoRoot, Map<String, UltimateSubsystem> subsystems) {
        this.repoRoot = repoRoot;
        this.subsystems = Map.copyOf(subsystems);
    }

    public static UltimateSubsystemRegistry createDefault(Path repoRoot, Path assetManagerDir) {
        LinkedHashMap<String, UltimateSubsystem> map = new LinkedHashMap<>();

        registerIfPresent(map, new UltimateSubsystem(
            "ultimate",
            "orchestration",
            "Ultimate System",
            assetManagerDir.resolve("src/main/java/fraymus/ultimate/UltimateSystem.java"),
            "Single abstraction shell over subsystems, docs, files, algorithms, and launch routes.",
            List.of("repo knowledge indexing", "subsystem routing", "math and logic facade access"),
            List.of("phi abstraction", "symbolic routing", "knowledge synthesis"),
            assetManagerDir,
            windowsGradleCommand("runUltimateSystem"),
            "The new top-level entrypoint for this repo.",
            strata("proof-lane", "kernel-discipline", "operator-shell", "construction-lane")
        ));

        registerIfPresent(map, new UltimateSubsystem(
            "convergence",
            "orchestration",
            "Fraymus Convergence",
            assetManagerDir.resolve("src/main/java/fraymus/FraymusConvergence.java"),
            "Current practical operator console for AEON systems.",
            List.of("HDC learning", "AEON launchers", "docker sandbox", "obsidian weaving"),
            List.of("hyperdimensional computing", "temporal reasoning", "semantic binding"),
            assetManagerDir,
            windowsGradleCommand("runConvergence"),
            "Best day-to-day shell for the main Asset-Manager build.",
            strata("cognition-core", "operator-shell", "construction-lane")
        ));

        registerIfPresent(map, new UltimateSubsystem(
            "fraymus-engine",
            "interface",
            "Fraymus Engine V2",
            assetManagerDir.resolve("src/main/java/fraymus/FraymusUI.java"),
            "Live arena cockpit with docked inspectors, terminal commands, memory panes, and organism controls.",
            List.of("visual arena", "command terminal", "entity inspectors", "memory panels", "world telemetry"),
            List.of("phi simulation", "entity evolution", "holographic memory", "spatial dynamics"),
            assetManagerDir,
            windowsGradleCommand("run"),
            "GUI-first operator surface for the Fraymus Engine V2 runtime.",
            strata("organism-core", "cognition-core", "absorption-core", "operator-shell")
        ));

        registerIfPresent(map, new UltimateSubsystem(
            "fraynix-boot",
            "orchestration",
            "Fraynix Boot",
            assetManagerDir.resolve("src/main/java/fraymus/FraynixBoot.java"),
            "Large historical super-console that exposes most experimental systems.",
            List.of("mega command surface", "frayfs boot", "aeon stack", "organism and swarm launchers"),
            List.of("phi systems", "simulation math", "recursive bootstrapping"),
            assetManagerDir,
            windowsGradleCommand("runFraynixBoot"),
            "Widest access to legacy and experimental capabilities.",
            strata("governing-core", "cognition-core", "absorption-core", "operator-shell", "adjacent-historical-core")
        ));

        registerIfPresent(map, new UltimateSubsystem(
            "willow-quantum",
            "science",
            "Aeon Willow Crusher",
            assetManagerDir.resolve("src/main/java/fraymus/neural/AeonWillowCrusher.java"),
            "Phi-resonance quantum kernel with 105 logical qubits, beat-frequency entanglement, harmonic echo, and waveform telemetry.",
            List.of("hadamard superposition", "beat-frequency entanglement", "harmonic echo", "goertzel measurement", "waveform telemetry"),
            List.of("phi resonance", "harmonic echo", "quantum waveform", "beat-frequency entanglement"),
            assetManagerDir,
            List.of(),
            "Quantum/proof organ already wired into FraynixBoot and FraynixProof.",
            strata("cognition-core", "proof-lane", "bare-metal-substrate-lane", "operator-shell")
        ));

        registerIfPresent(map, new UltimateSubsystem(
            "fraynix-organism",
            "orchestration",
            "Fraynix Organism",
            assetManagerDir.resolve("src/main/java/fraymus/FraynixOrganism.java"),
            "Unified daemon-style organism runtime inside Asset-Manager.",
            List.of("daemon shell", "central nervous system", "agent wiring"),
            List.of("organism modeling", "heartbeat loops", "service coordination"),
            assetManagerDir,
            windowsGradleCommand("runOrganism"),
            "More daemon-like than the boot consoles.",
            strata("organism-core", "operator-shell")
        ));

        Path universalDir = assetManagerDir.resolve("fraymus-universal");
        registerIfPresent(map, new UltimateSubsystem(
            "fraynix-os",
            "os",
            "Fraynix OS",
            universalDir.resolve("src/main/java/fraynix/FraynixOS.java"),
            "Cleaner next-gen Fraynix runtime with an OS-like architecture.",
            List.of("intent bus", "frayfs", "nano swarm", "dream state"),
            List.of("capability routing", "kernel abstraction", "semantic filesystem"),
            universalDir,
            windowsGradleCommand("run"),
            "Best architectural base for long-term evolution.",
            strata("governing-core", "cognition-core", "kernel-discipline", "operator-shell")
        ));

        Path aiCoreDir = repoRoot.resolve("fraymus-ai-core");
        registerIfPresent(map, new UltimateSubsystem(
            "fraymus-ai-core",
            "ai",
            "Fraymus AI Core",
            aiCoreDir.resolve("src/main/java/io/fraymus/ai/FraymusAI.java"),
            "Reusable local AI library with Ollama, RAG, tools, and memory.",
            List.of("chat", "embeddings", "RAG", "persistent memory", "reflection"),
            List.of("vector similarity", "retrieval scoring", "memory chains"),
            aiCoreDir,
            List.of(),
            "The cleanest reusable AI surface in the repo.",
            strata("cognition-core", "absorption-core", "model-substrate-lane", "proof-lane")
        ));

        registerIfPresent(map, new UltimateSubsystem(
            "transmudder-soul",
            "ai",
            "Transmudder Soul",
            assetManagerDir.resolve("src/main/java/fraymus/ollama/Transmudder.java"),
            "Living-memory ingestion lane that transmutes PDFs and text into cleaned operator context for the Ollama spine.",
            List.of("pdf transmutation", "text cleansing", "context accumulation", "directory digestion"),
            List.of("symbol grounding", "memory compression", "context transmutation", "operator absorption"),
            assetManagerDir,
            List.of(),
            "Absorption soul already used by the Ollama spine and Fraynix mainline boot.",
            strata("absorption-core", "proof-lane", "model-substrate-lane", "operator-shell")
        ));

        Path nexusDir = repoRoot.resolve("Fraymus_NEXUS_Edition");
        registerIfPresent(map, new UltimateSubsystem(
            "nexus-edition",
            "interface",
            "NEXUS Edition",
            nexusDir.resolve("src/main/java/fraymus/Main.java"),
            "GUI-first organism branch with API exposure and expanded runtime ideas.",
            List.of("gui runtime", "organism layer", "http api"),
            List.of("organism math", "state orchestration", "api exposure"),
            nexusDir,
            List.of(),
            "Useful as a parallel interface branch rather than the main build.",
            strata("organism-core", "operator-shell", "adjacent-historical-core")
        ));

        Path applicationsDir = assetManagerDir.resolve("src/main/java/fraymus/applications");
        registerIfPresent(map, new UltimateSubsystem(
            "applications",
            "science",
            "Research Applications",
            applicationsDir,
            "Applied engines for protein folding, cancer research, and drug discovery.",
            List.of("protein folding", "cancer research", "drug discovery"),
            List.of("optimization", "simulation scoring", "scientific benchmarking"),
            assetManagerDir,
            windowsGradleCommand("runAllApplications"),
            "Applied problem-solving family built on top of the broader system.",
            strata("construction-lane")
        ));

        registerIfPresent(map, new UltimateSubsystem(
            "video-cortex",
            "interface",
            "Video Cortex Pipeline",
            assetManagerDir.resolve("VideoCortex.py"),
            "Python visual layer for reactive media, transmutation, and Max Headroom style output.",
            List.of("video reactivity", "python bridge", "visual transmutation"),
            List.of("signal flow", "media transforms", "feedback loops"),
            assetManagerDir,
            List.of(),
            "Bridges the Java systems into visual and media-oriented workflows.",
            strata("operator-shell", "organism-core")
        ));

        return new UltimateSubsystemRegistry(repoRoot, map);
    }

    private static void registerIfPresent(Map<String, UltimateSubsystem> map, UltimateSubsystem subsystem) {
        boolean pathPresent = subsystem.primaryPath() != null && Files.exists(subsystem.primaryPath());
        boolean workingDirPresent = subsystem.workingDirectory() != null && Files.exists(subsystem.workingDirectory());
        if (pathPresent || workingDirPresent) {
            map.put(subsystem.id(), subsystem);
        }
    }

    private static List<String> windowsGradleCommand(String task) {
        return List.of("cmd", "/c", "gradlew.bat", task);
    }

    public List<UltimateSubsystem> all() {
        return new ArrayList<>(subsystems.values());
    }

    public Optional<UltimateSubsystem> find(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        String needle = id.trim().toLowerCase(Locale.ROOT);
        return subsystems.values().stream()
            .filter(subsystem -> subsystem.id().equalsIgnoreCase(needle))
            .findFirst();
    }

    public Map<String, Long> familyCounts() {
        return subsystems.values().stream()
            .collect(Collectors.groupingBy(UltimateSubsystem::family, LinkedHashMap::new, Collectors.counting()));
    }

    public Map<String, Long> agiStrataCounts() {
        LinkedHashMap<String, Long> counts = new LinkedHashMap<>();
        for (UltimateSubsystem subsystem : subsystems.values()) {
            LinkedHashSet<String> unique = new LinkedHashSet<>(subsystem.agiStrata());
            for (String stratum : unique) {
                counts.merge(stratum, 1L, Long::sum);
            }
        }
        return counts;
    }

    public String renderSummary() {
        if (subsystems.isEmpty()) {
            return "ULTIMATE SUBSYSTEM REGISTRY\nNo subsystems were detected from the current repo root.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ULTIMATE SUBSYSTEM REGISTRY\n");
        sb.append("Subsystems: ").append(subsystems.size())
            .append(" | Families: ").append(familyCounts().size()).append("\n");

        for (UltimateSubsystem subsystem : subsystems.values()) {
            sb.append(String.format(
                Locale.ROOT,
                "  %-16s [%s] %s%n",
                subsystem.id(),
                subsystem.family(),
                subsystem.summary()
            ));
        }
        return sb.toString().trim();
    }

    public String renderDetails(String id) {
        Optional<UltimateSubsystem> match = find(id);
        if (match.isEmpty()) {
            return "Usage: system <id>\nTry: systems";
        }

        UltimateSubsystem subsystem = match.get();
        StringBuilder sb = new StringBuilder();
        sb.append("SUBSYSTEM: ").append(subsystem.displayName()).append("\n");
        sb.append("Id: ").append(subsystem.id()).append("\n");
        sb.append("Family: ").append(subsystem.family()).append("\n");
        sb.append("Role: ").append(subsystem.designRole()).append("\n");
        sb.append("Path: ").append(displayPath(subsystem.primaryPath())).append("\n");
        sb.append("Summary: ").append(subsystem.summary()).append("\n");
        sb.append("AGI strata: ").append(String.join(", ", subsystem.agiStrata())).append("\n");
        sb.append("Capabilities:\n");
        for (String capability : subsystem.capabilities()) {
            sb.append("  - ").append(capability).append("\n");
        }
        sb.append("Math and logic:\n");
        for (String principle : subsystem.mathKeywords()) {
            sb.append("  - ").append(principle).append("\n");
        }
        sb.append("Launch: ").append(subsystem.runnable() ? subsystem.launchCommandText() : "(manual or library-only)").append("\n");
        sb.append("Working dir: ").append(displayPath(subsystem.workingDirectory()));
        return sb.toString();
    }

    private String displayPath(Path path) {
        if (path == null) {
            return "(none)";
        }
        try {
            if (path.startsWith(repoRoot)) {
                return repoRoot.relativize(path).toString().replace('\\', '/');
            }
        } catch (IllegalArgumentException ignored) {
            // Fall back to absolute path below.
        }
        return path.toString().replace('\\', '/');
    }

    private static List<String> strata(String... values) {
        LinkedHashSet<String> unique = new LinkedHashSet<>();
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                unique.add(value);
            }
        }
        return List.copyOf(unique);
    }

    private static boolean hasStratum(List<String> agiStrata, String stratum) {
        return agiStrata.stream().anyMatch(stratum::equals);
    }

    public record UltimateSubsystem(
        String id,
        String family,
        String displayName,
        Path primaryPath,
        String summary,
        List<String> capabilities,
        List<String> mathKeywords,
        Path workingDirectory,
        List<String> launchCommand,
        String designRole,
        List<String> agiStrata
    ) {
        public UltimateSubsystem {
            capabilities = List.copyOf(capabilities);
            mathKeywords = List.copyOf(mathKeywords);
            launchCommand = List.copyOf(launchCommand);
            agiStrata = List.copyOf(agiStrata);
        }

        public boolean runnable() {
            return !launchCommand.isEmpty() && workingDirectory != null && Files.exists(workingDirectory);
        }

        public String launchCommandText() {
            return launchCommand.isEmpty() ? "(not wired)" : String.join(" ", launchCommand);
        }

        public boolean governingCore() {
            return hasStratum(agiStrata, "governing-core");
        }

        public boolean cognitionCore() {
            return hasStratum(agiStrata, "cognition-core");
        }

        public boolean organismCore() {
            return hasStratum(agiStrata, "organism-core");
        }

        public boolean absorptionCore() {
            return hasStratum(agiStrata, "absorption-core");
        }

        public boolean proofLane() {
            return hasStratum(agiStrata, "proof-lane");
        }

        public boolean modelSubstrateLane() {
            return hasStratum(agiStrata, "model-substrate-lane");
        }

        public boolean bareMetalSubstrateLane() {
            return hasStratum(agiStrata, "bare-metal-substrate-lane");
        }

        public boolean adjacentHistoricalCore() {
            return hasStratum(agiStrata, "adjacent-historical-core");
        }

        public boolean operatorShell() {
            return hasStratum(agiStrata, "operator-shell");
        }

        public boolean kernelDiscipline() {
            return hasStratum(agiStrata, "kernel-discipline");
        }

        public boolean constructionLane() {
            return hasStratum(agiStrata, "construction-lane");
        }
    }
}
