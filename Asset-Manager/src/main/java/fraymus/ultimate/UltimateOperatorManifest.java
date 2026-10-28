package fraymus.ultimate;

import fraymus.ultimate.UltimateCommandCatalog.CommandEntry;
import fraymus.ultimate.UltimateCommandCatalog.CommandSection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runtime-authoritative operator manifest for the browser shell and command
 * surfaces.
 */
public final class UltimateOperatorManifest {

    private static final Pattern BOOT_CASE_PATTERN = Pattern.compile(
        "(?m)^\\s*case\\s+((?:\"[^\"]+\"\\s*,\\s*)*\"[^\"]+\")\\s*->\\s*\\{"
    );
    private static final Pattern CASE_LABEL_PATTERN = Pattern.compile("\"([^\"]+)\"");
    private static final Pattern QUOTED_MESSAGE_PATTERN = Pattern.compile("System\\.out\\.println\\(\"([^\"]+)\"");
    private static final Pattern STATIC_TARGET_PATTERN = Pattern.compile(
        "([A-Za-z0-9_$.]+)\\.(launch|main|runSession|executeExodus|excreteGenesisImage)\\s*\\("
    );
    private static final Pattern INSTANCE_TARGET_PATTERN = Pattern.compile(
        "new\\s+([A-Za-z0-9_$.]+)\\s*\\([^;\\n]*?\\)\\.(startREPL|launch|main|runSession)\\s*\\("
    );

    private final List<SurfaceManifest> surfaces;
    private final List<LivingProofLane> livingProofLanes;
    private final int bootTreeBranchCount;
    private final int uncataloguedBootBranchCount;

    private UltimateOperatorManifest(
        List<SurfaceManifest> surfaces,
        List<LivingProofLane> livingProofLanes,
        int bootTreeBranchCount,
        int uncataloguedBootBranchCount
    ) {
        this.surfaces = List.copyOf(surfaces);
        this.livingProofLanes = List.copyOf(livingProofLanes);
        this.bootTreeBranchCount = bootTreeBranchCount;
        this.uncataloguedBootBranchCount = uncataloguedBootBranchCount;
    }

    public static UltimateOperatorManifest create(List<UltimateCommandCatalog> catalogs) {
        return create(null, catalogs);
    }

    public static UltimateOperatorManifest create(Path assetManagerDir, List<UltimateCommandCatalog> catalogs) {
        List<BootBranch> bootBranches = discoverFraynixBootBranches(assetManagerDir);
        List<SurfaceManifest> built = new ArrayList<>();
        int uncataloguedCount = 0;
        for (UltimateCommandCatalog catalog : catalogs) {
            SurfaceManifest surface = toSurfaceManifest(catalog, bootBranches);
            built.add(surface);
            if ("fraynix".equals(catalog.surfaceId())) {
                uncataloguedCount = surface.discoveredCommandCount();
            }
        }
        return new UltimateOperatorManifest(
            built,
            livingProofLanes(built),
            bootBranches.size(),
            uncataloguedCount
        );
    }

    public List<SurfaceManifest> surfaces() {
        return surfaces;
    }

    public List<LivingProofLane> livingProofLanes() {
        return livingProofLanes;
    }

    public Optional<SurfaceManifest> findSurface(String surfaceId) {
        if (surfaceId == null || surfaceId.isBlank()) {
            return Optional.empty();
        }
        String needle = normalize(surfaceId);
        return surfaces.stream()
            .filter(surface -> normalize(surface.id()).equals(needle))
            .findFirst();
    }

    public Optional<LivingProofLane> findLivingProofLane(String laneId) {
        if (laneId == null || laneId.isBlank()) {
            return Optional.empty();
        }
        String needle = normalize(laneId);
        return livingProofLanes.stream()
            .filter(lane -> normalize(lane.id()).equals(needle)
                || normalize(lane.title()).equals(needle))
            .findFirst();
    }

    public int surfaceCount() {
        return surfaces.size();
    }

    public int sectionCount() {
        return surfaces.stream().mapToInt(SurfaceManifest::sectionCount).sum();
    }

    public int commandCount() {
        return surfaces.stream().mapToInt(SurfaceManifest::commandCount).sum();
    }

    public int livingProofLaneCount() {
        return livingProofLanes.size();
    }

    public int kernelPreferredSurfaceCount() {
        return (int) surfaces.stream().filter(SurfaceManifest::kernelOwnedPreferred).count();
    }

    public int kernelOverlapSectionCount() {
        return (int) surfaces.stream()
            .flatMap(surface -> surface.sections().stream())
            .filter(SectionManifest::kernelOverlap)
            .count();
    }

    public int bootTreeBranchCount() {
        return bootTreeBranchCount;
    }

    public int uncataloguedBootBranchCount() {
        return uncataloguedBootBranchCount;
    }

    public Map<String, Integer> priorityCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (SurfaceManifest surface : surfaces) {
            for (SectionManifest section : surface.sections()) {
                counts.merge(section.integrationPriority(), 1, Integer::sum);
            }
        }
        return counts;
    }

    public Map<String, Integer> surfaceAgiStrataCounts() {
        return countStrata(surfaces.stream().map(SurfaceManifest::agiStrata).toList());
    }

    public Map<String, Integer> sectionAgiStrataCounts() {
        return countStrata(
            surfaces.stream()
                .flatMap(surface -> surface.sections().stream())
                .map(SectionManifest::agiStrata)
                .toList()
        );
    }

    public Map<String, Integer> livingProofLaneAgiStrataCounts() {
        return countStrata(livingProofLanes.stream().map(LivingProofLane::agiStrata).toList());
    }

    private static SurfaceManifest toSurfaceManifest(UltimateCommandCatalog catalog, List<BootBranch> bootBranches) {
        SurfaceMetadata surfaceMetadata = surfaceMetadata(catalog.surfaceId());
        List<SectionManifest> sections = new ArrayList<>();
        for (CommandSection section : catalog.sections()) {
            sections.add(toSectionManifest(catalog, section));
        }

        if ("fraynix".equals(catalog.surfaceId())) {
            sections.addAll(discoveredBootSections(catalog, sections, bootBranches));
        }

        return new SurfaceManifest(
            catalog.surfaceId(),
            catalog.surfaceTitle(),
            catalog.surfaceDescription(),
            catalog.primarySubsystemId(),
            surfaceMetadata.authorityLevel(),
            surfaceMetadata.executionModel(),
            surfaceMetadata.kernelOwnedPreferred(),
            surfaceMetadata.operatorRole(),
            surfaceMetadata.kernelEquivalentStatus(),
            surfaceMetadata.migrationPolicy(),
            surfaceMetadata.integrationPriority(),
            surfaceMetadata.notes(),
            surfaceMetadata.agiStrata(),
            sections
        );
    }

    private static List<SectionManifest> discoveredBootSections(
        UltimateCommandCatalog catalog,
        List<SectionManifest> curatedSections,
        List<BootBranch> bootBranches
    ) {
        if (bootBranches.isEmpty()) {
            return List.of();
        }

        LinkedHashSet<String> curatedVerbs = new LinkedHashSet<>();
        for (SectionManifest section : curatedSections) {
            curatedVerbs.addAll(section.primaryVerbs());
        }

        List<BootBranch> uncatalogued = new ArrayList<>();
        for (BootBranch branch : bootBranches) {
            boolean overlapsCurated = branch.aliases().stream()
                .map(UltimateOperatorManifest::normalize)
                .anyMatch(curatedVerbs::contains);
            if (!overlapsCurated) {
                uncatalogued.add(branch);
            }
        }

        if (uncatalogued.isEmpty()) {
            return List.of();
        }

        List<BootBranch> launchBranches = new ArrayList<>();
        List<BootBranch> commandBranches = new ArrayList<>();
        for (BootBranch branch : uncatalogued) {
            if (branch.launchBranch()) {
                launchBranches.add(branch);
            } else {
                commandBranches.add(branch);
            }
        }

        List<SectionManifest> discovered = new ArrayList<>();
        if (!launchBranches.isEmpty()) {
            discovered.add(discoveredSection(
                catalog.surfaceId(),
                "boot-tree-launches",
                "Boot Tree Launch Branches",
                "Uncatalogued FraynixBoot branches that jump directly into real launch targets or REPL-style entrypoints.",
                launchBranches
            ));
        }
        if (!commandBranches.isEmpty()) {
            discovered.add(discoveredSection(
                catalog.surfaceId(),
                "boot-tree-discovery",
                "Boot Tree Uncatalogued Branches",
                "Real FraynixBoot branches that exist in source but are not yet represented in the curated command catalog.",
                commandBranches
            ));
        }
        return discovered;
    }

    private static SectionManifest discoveredSection(
        String surfaceId,
        String id,
        String title,
        String theme,
        List<BootBranch> branches
    ) {
        SectionMetadata metadata = sectionMetadata(surfaceId, id);
        List<CommandDescriptor> commands = new ArrayList<>();
        LinkedHashSet<String> verbs = new LinkedHashSet<>();
        for (BootBranch branch : branches) {
            verbs.addAll(branch.aliases().stream().map(UltimateOperatorManifest::normalize).toList());
            commands.add(new CommandDescriptor(
                branch.syntax(),
                branch.description(),
                normalize(branch.primaryAlias()),
                true,
                branch.launchBranch(),
                branch.lineNumber(),
                branch.aliases()
            ));
        }

        return new SectionManifest(
            surfaceId,
            id,
            title,
            theme,
            "fraynix-boot",
            List.of("fraynix-boot"),
            metadata.kernelOverlap(),
            metadata.operatorRole(),
            metadata.kernelEquivalentStatus(),
            metadata.migrationPolicy(),
            metadata.integrationPriority(),
            metadata.notes(),
            metadata.agiStrata(),
            true,
            List.copyOf(verbs),
            commands
        );
    }

    private static SectionManifest toSectionManifest(UltimateCommandCatalog catalog, CommandSection section) {
        SectionMetadata metadata = sectionMetadata(catalog.surfaceId(), section.id());
        List<CommandDescriptor> commands = new ArrayList<>();
        LinkedHashSet<String> verbs = new LinkedHashSet<>();
        for (CommandEntry entry : section.commands()) {
            String primaryVerb = primaryVerb(entry.syntax());
            commands.add(new CommandDescriptor(entry.syntax(), entry.description(), primaryVerb));
            if (!primaryVerb.isBlank()) {
                verbs.add(primaryVerb);
            }
        }

        return new SectionManifest(
            catalog.surfaceId(),
            section.id(),
            section.title(),
            section.theme(),
            catalog.defaultSubsystemId(section).orElse(""),
            List.copyOf(catalog.linkedSubsystemIds(section)),
            metadata.kernelOverlap(),
            metadata.operatorRole(),
            metadata.kernelEquivalentStatus(),
            metadata.migrationPolicy(),
            metadata.integrationPriority(),
            metadata.notes(),
            metadata.agiStrata(),
            false,
            List.copyOf(verbs),
            commands
        );
    }

    private static SurfaceMetadata surfaceMetadata(String surfaceId) {
        return switch (surfaceId) {
            case "fraynix" -> new SurfaceMetadata(
                "legacy breadth surface",
                "mixed shell and external launch",
                false,
                "mega-shell compatibility lane",
                "partial",
                "Route safe overlap into the unified shell, but keep launch-only branches explicit and visible.",
                "high",
                "Maps the historic Fraynix breadth without pretending every boot branch is already kernel-owned.",
                strata("governing-core", "cognition-core", "organism-core", "absorption-core", "construction-lane", "operator-shell")
            );
            case "engine" -> new SurfaceMetadata(
                "specialist console surface",
                "engine console and live specialist tools",
                false,
                "deep subsystem cockpit",
                "partial",
                "Preserve specialist tooling while shared verbs route through one operator story.",
                "medium",
                "Holds powerful capability clusters that should stay visible even before full kernel replacement exists.",
                strata("cognition-core", "organism-core", "absorption-core", "construction-lane", "proof-lane", "operator-shell")
            );
            case "native" -> new SurfaceMetadata(
                "authoritative kernel-adjacent surface",
                "direct in-process execution",
                true,
                "kernel-owned execution lane",
                "full",
                "Prefer this surface whenever a shared capability already has a native in-process path.",
                "critical",
                "This is the safest overlap surface because it already executes inside UltimateSystem.",
                strata("kernel-discipline", "proof-lane", "construction-lane", "model-substrate-lane", "organism-core")
            );
            default -> new SurfaceMetadata(
                "mapped operator surface",
                "catalogued execution",
                false,
                "mapped shell lane",
                "partial",
                "Keep visible and route by explicit subsystem ownership.",
                "medium",
                "Default manifest metadata.",
                strata("operator-shell")
            );
        };
    }

    private static SectionMetadata sectionMetadata(String surfaceId, String sectionId) {
        return switch (surfaceId + ":" + sectionId) {
            case "fraynix:core" -> new SectionMetadata(
                true,
                "core compatibility shell",
                "partial",
                "Prefer unified shell verbs for status, search, paths, and export while boot-only generators remain explicit.",
                "critical",
                "This is where legacy operator expectations meet the safer unified shell.",
                strata("governing-core", "absorption-core", "construction-lane", "operator-shell")
            );
            case "fraynix:cognition" -> new SectionMetadata(
                false,
                "cognition and embodiment lane",
                "partial",
                "Keep visible as a launch-rich surface while the bounded kernel continues absorbing overlapping behaviors.",
                "high",
                "OpenClaw, AEON, and cortex surfaces are real operator entrypoints even when not fully browser-runnable.",
                strata("cognition-core", "organism-core", "operator-shell")
            );
            case "fraynix:bridge" -> new SectionMetadata(
                true,
                "shared substrate bridge",
                "high",
                "Prefer the native bridge surface for overlap and use Fraynix names as compatibility aliases.",
                "critical",
                "This section most clearly overlaps the direct in-process runtime.",
                strata("cognition-core", "kernel-discipline", "proof-lane", "operator-shell")
            );
            case "fraynix:convergence" -> new SectionMetadata(
                true,
                "organism and physics orchestration",
                "partial",
                "Cross-link into native spatial and organism controls where overlap exists, but keep organism launches explicit.",
                "high",
                "This is one of the strongest merge points between the old organism lane and the bounded runtime.",
                strata("organism-core", "cognition-core", "operator-shell")
            );
            case "fraynix:facades" -> new SectionMetadata(
                true,
                "kernel-facing facade lane",
                "high",
                "Preserve facades as wrappers, but treat their routed execution path as unified-shell-owned.",
                "critical",
                "This section is already close to the north-star control story.",
                strata("proof-lane", "kernel-discipline", "construction-lane", "operator-shell")
            );
            case "fraynix:daemons" -> new SectionMetadata(
                false,
                "long-running infrastructure launches",
                "partial",
                "Keep these as explicit launch routes and status surfaces rather than pretending they are browser-safe commands.",
                "high",
                "These commands expose real system breadth and should remain mapped even before deeper unification.",
                strata("organism-core", "operator-shell")
            );
            case "fraynix:boot-tree-launches" -> new SectionMetadata(
                false,
                "real boot launch discovery lane",
                "unmapped",
                "Keep visible in the operator surface until each launch branch is either curated or intentionally retired.",
                "high",
                "Discovered from the real FraynixBoot command tree rather than the curated catalog.",
                strata("operator-shell", "adjacent-historical-core")
            );
            case "fraynix:boot-tree-discovery" -> new SectionMetadata(
                false,
                "real boot discovery lane",
                "unmapped",
                "Keep visible in the operator surface until each branch is curated, routed, or replaced.",
                "high",
                "Source-discovered FraynixBoot branches that would otherwise stay invisible to the browser shell.",
                strata("operator-shell", "adjacent-historical-core")
            );
            case "engine:exploration" -> new SectionMetadata(
                true,
                "telemetry and discovery console",
                "partial",
                "Keep as a fast specialist inspection lane, but route shared discovery tasks through the unified shell.",
                "high",
                "Good candidate for a future operator dashboard subsection.",
                strata("proof-lane", "operator-shell")
            );
            case "engine:advanced-subsystems" -> new SectionMetadata(
                false,
                "expert subsystem cockpit",
                "partial",
                "Preserve this as a specialist surface until equivalent kernel-owned inspection paths exist.",
                "medium",
                "Contains many real capabilities that should be surfaced honestly instead of hidden.",
                strata("cognition-core", "organism-core", "adjacent-historical-core", "operator-shell")
            );
            case "engine:nexus-organism" -> new SectionMetadata(
                true,
                "organism control panel",
                "high",
                "Cross-link with native organism controls and keep both visible until one fully subsumes the other.",
                "high",
                "This is another active merge seam between organism and kernel lanes.",
                strata("organism-core", "proof-lane", "operator-shell")
            );
            case "engine:spatial-computing" -> new SectionMetadata(
                true,
                "spatial and physics cockpit",
                "high",
                "Prefer native spatial runtime controls for direct execution, keep engine views for specialist inspection.",
                "high",
                "Maps closely onto the native spatial lane.",
                strata("organism-core", "bare-metal-substrate-lane", "operator-shell")
            );
            case "native:runtime-native" -> new SectionMetadata(
                true,
                "authoritative runtime status",
                "full",
                "Keep authoritative. Shared status views should point here, not bypass it.",
                "critical",
                "This section is already the direct runtime truth.",
                strata("proof-lane", "kernel-discipline")
            );
            case "native:bridge-native" -> new SectionMetadata(
                true,
                "authoritative bridge execution",
                "full",
                "Keep authoritative and prefer it for all overlap with Fraynix bridge commands.",
                "critical",
                "Direct bridge execution inside UltimateSystem.",
                strata("proof-lane", "kernel-discipline", "model-substrate-lane")
            );
            case "native:quantum-native" -> new SectionMetadata(
                true,
                "authoritative quantum primitives",
                "full",
                "Keep authoritative for safe overlap and route compatibility names here.",
                "critical",
                "Direct quantum primitives with no external launch dependency.",
                strata("cognition-core", "proof-lane", "kernel-discipline")
            );
            case "native:genesis-native" -> new SectionMetadata(
                true,
                "authoritative genesis primitives",
                "full",
                "Keep authoritative and expose as the native fallback for shared forge/collision verbs.",
                "critical",
                "Already kernel-owned inside the browser shell.",
                strata("construction-lane", "proof-lane", "kernel-discipline")
            );
            case "native:organism-native" -> new SectionMetadata(
                true,
                "authoritative organism runtime",
                "full",
                "Keep authoritative for overlap while older organism shells remain mapped as broader launch surfaces.",
                "critical",
                "Bridges the immortal kernel and the broader organism lane.",
                strata("organism-core", "proof-lane", "kernel-discipline")
            );
            case "native:spatial-native" -> new SectionMetadata(
                true,
                "authoritative spatial runtime",
                "full",
                "Keep authoritative for overlap with gravity, fusion, suit, and Miving runtime controls.",
                "critical",
                "This section already behaves like the stable runtime core for spatial execution.",
                strata("organism-core", "bare-metal-substrate-lane", "proof-lane", "kernel-discipline")
            );
            default -> defaultSectionMetadata(surfaceId);
        };
    }

    private static SectionMetadata defaultSectionMetadata(String surfaceId) {
        return switch (surfaceId) {
            case "native" -> new SectionMetadata(
                true,
                "kernel-owned runtime lane",
                "full",
                "Keep authoritative and surface it first anywhere the same capability appears elsewhere.",
                "critical",
                "Default metadata for native sections.",
                strata("proof-lane", "kernel-discipline")
            );
            case "fraynix" -> new SectionMetadata(
                false,
                "legacy compatibility lane",
                "partial",
                "Keep mapped and visible, then route only the safe overlap into the unified shell.",
                "high",
                "Default metadata for Fraynix sections.",
                strata("operator-shell")
            );
            case "engine" -> new SectionMetadata(
                false,
                "specialist inspection lane",
                "partial",
                "Keep visible as expert tooling while unified equivalents are still incomplete.",
                "medium",
                "Default metadata for engine sections.",
                strata("operator-shell")
            );
            default -> new SectionMetadata(
                false,
                "mapped operator lane",
                "partial",
                "Keep visible and route by explicit ownership.",
                "medium",
                "Default metadata.",
                strata("operator-shell")
            );
        };
    }

    private static Map<String, Integer> countStrata(List<List<String>> strataGroups) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (List<String> strata : strataGroups) {
            LinkedHashSet<String> unique = new LinkedHashSet<>(strata);
            for (String stratum : unique) {
                counts.merge(stratum, 1, Integer::sum);
            }
        }
        return counts;
    }

    private static List<BootBranch> discoverFraynixBootBranches(Path assetManagerDir) {
        if (assetManagerDir == null) {
            return List.of();
        }

        Path bootPath = assetManagerDir.resolve("src/main/java/fraymus/FraynixBoot.java");
        if (!Files.exists(bootPath)) {
            return List.of();
        }

        try {
            String source = Files.readString(bootPath);
            List<BootBranch> discovered = new ArrayList<>();
            Matcher matcher = BOOT_CASE_PATTERN.matcher(source);
            while (matcher.find()) {
                List<String> aliases = parseCaseLabels(matcher.group(1));
                if (aliases.isEmpty()) {
                    continue;
                }
                int openBrace = source.indexOf('{', matcher.end() - 1);
                if (openBrace < 0) {
                    continue;
                }
                int closeBrace = findMatchingBrace(source, openBrace);
                if (closeBrace < 0) {
                    continue;
                }

                String block = source.substring(openBrace + 1, closeBrace);
                int lineNumber = lineNumberAt(source, matcher.start());
                List<String> targetHints = targetHints(block);
                boolean launchBranch = isLaunchBranch(block, targetHints);
                boolean multiCommandBranch = isMultiCommandBranch(block);
                discovered.add(new BootBranch(
                    aliases,
                    lineNumber,
                    launchBranch,
                    multiCommandBranch,
                    targetHints,
                    describeBootBranch(aliases, lineNumber, launchBranch, multiCommandBranch, targetHints, block)
                ));
            }
            return List.copyOf(discovered);
        } catch (IOException exception) {
            return List.of();
        }
    }

    private static List<String> parseCaseLabels(String labelsBlock) {
        List<String> labels = new ArrayList<>();
        Matcher matcher = CASE_LABEL_PATTERN.matcher(labelsBlock);
        while (matcher.find()) {
            labels.add(matcher.group(1));
        }
        return List.copyOf(labels);
    }

    private static int findMatchingBrace(String text, int openBrace) {
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int index = openBrace; index < text.length(); index++) {
            char ch = text.charAt(index);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (ch == '\\') {
                    escaped = true;
                } else if (ch == '"') {
                    inString = false;
                }
                continue;
            }

            if (ch == '"') {
                inString = true;
            } else if (ch == '{') {
                depth++;
            } else if (ch == '}') {
                depth--;
                if (depth == 0) {
                    return index;
                }
            }
        }
        return -1;
    }

    private static int lineNumberAt(String text, int offset) {
        int line = 1;
        for (int index = 0; index < offset && index < text.length(); index++) {
            if (text.charAt(index) == '\n') {
                line++;
            }
        }
        return line;
    }

    private static List<String> targetHints(String block) {
        LinkedHashSet<String> targets = new LinkedHashSet<>();
        Matcher staticTargets = STATIC_TARGET_PATTERN.matcher(block);
        while (staticTargets.find()) {
            targets.add(shortTypeName(staticTargets.group(1)) + "." + staticTargets.group(2));
        }
        Matcher instanceTargets = INSTANCE_TARGET_PATTERN.matcher(block);
        while (instanceTargets.find()) {
            targets.add(shortTypeName(instanceTargets.group(1)) + "." + instanceTargets.group(2));
        }
        return List.copyOf(targets);
    }

    private static boolean isLaunchBranch(String block, List<String> targetHints) {
        return !targetHints.isEmpty()
            || block.contains(".launch(")
            || block.contains(".main(")
            || block.contains("startREPL(")
            || block.contains("runSession(");
    }

    private static boolean isMultiCommandBranch(String block) {
        return block.contains("switch (")
            || block.contains("if (arg")
            || block.contains("else if (arg")
            || block.contains("startsWith(")
            || block.contains("equals(");
    }

    private static String describeBootBranch(
        List<String> aliases,
        int lineNumber,
        boolean launchBranch,
        boolean multiCommandBranch,
        List<String> targetHints,
        String block
    ) {
        StringBuilder description = new StringBuilder();
        description.append("Discovered from FraynixBoot line ").append(lineNumber).append(". ");
        if (launchBranch) {
            description.append("Real launch branch");
        } else if (multiCommandBranch) {
            description.append("Real multi-command branch");
        } else {
            description.append("Real boot branch");
        }

        if (!targetHints.isEmpty()) {
            description.append(" targeting ").append(String.join(", ", targetHints.subList(0, Math.min(2, targetHints.size()))));
        }
        description.append(".");

        String messageHint = firstQuotedMessage(block);
        if (!messageHint.isBlank()) {
            description.append(" ").append(messageHint);
        } else if (aliases.size() > 1) {
            description.append(" Aliases: ").append(String.join(", ", aliases)).append(".");
        }
        return description.toString();
    }

    private static String firstQuotedMessage(String block) {
        Matcher matcher = QUOTED_MESSAGE_PATTERN.matcher(block);
        if (!matcher.find()) {
            return "";
        }
        String raw = matcher.group(1)
            .replace("\\n", " ")
            .replace("\\t", " ")
            .replaceAll("\\s+", " ")
            .trim();
        if (raw.isBlank()) {
            return "";
        }
        if (raw.length() > 110) {
            raw = raw.substring(0, 107).trim() + "...";
        }
        return raw;
    }

    private static String shortTypeName(String value) {
        int lastDot = value.lastIndexOf('.');
        return lastDot >= 0 ? value.substring(lastDot + 1) : value;
    }

    private static String primaryVerb(String syntax) {
        if (syntax == null || syntax.isBlank()) {
            return "";
        }

        String head = syntax.trim().split("\\s+", 2)[0];
        String normalized = head.replaceAll("[^A-Za-z0-9/_|-]", "");
        if (normalized.contains("/")) {
            normalized = normalized.substring(0, normalized.indexOf('/'));
        }
        if (normalized.contains("|")) {
            normalized = normalized.substring(0, normalized.indexOf('|'));
        }
        return normalized.toLowerCase(Locale.ROOT);
    }

    private static String normalize(String value) {
        return value == null
            ? ""
            : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "");
    }

    private static List<LivingProofLane> livingProofLanes(List<SurfaceManifest> surfaces) {
        return List.of(
            new LivingProofLane(
                "morse",
                "Morse",
                "Thermal, fan, and symbolic signaling lanes that already behave like living organs and proof surfaces instead of disposable demos.",
                "symbolic signaling and embodied proof lane",
                strata("organism-core", "proof-lane", "bare-metal-substrate-lane", "operator-shell"),
                List.of(
                    livingProofSection(surfaces, "engine", "advanced-subsystems"),
                    livingProofSection(surfaces, "engine", "omni-caster"),
                    livingProofSection(surfaces, "engine", "physics-engine")
                ),
                List.of("morse", "omni thermal <msg>", "dump <msg>"),
                List.of(
                    "src/main/java/fraymus/MorseCircuit.java",
                    "src/main/java/fraymus/physics/FanConductor.java",
                    "src/main/java/fraymus/CommandTerminal.java",
                    "src/main/java/fraymus/organism/NEXUS_Organism.java"
                ),
                List.of(
                    "Broca's area, thermal Morse, and fan-conductor signaling already exist as organism I/O.",
                    "This lane is both communication and proof because the broadcast path is embodied in the system."
                )
            ),
            new LivingProofLane(
                "ouroboros",
                "Ouroboros",
                "Recursive self-coding, omega JIT mutation, and dream-adjacent self-rewrite surfaces that keep Fraynix from being a static machine.",
                "self-rewrite and recursive evolution lane",
                strata("cognition-core", "construction-lane", "adjacent-historical-core", "operator-shell"),
                List.of(
                    livingProofSection(surfaces, "fraynix", "aeon-specialists"),
                    livingProofSection(surfaces, "fraynix", "genesis"),
                    livingProofSection(surfaces, "fraynix", "daemons")
                ),
                List.of("omega ouroboros", "dream", "rigorous"),
                List.of(
                    "src/main/java/fraymus/alchemy/OuroborosJIT.java",
                    "src/main/java/fraymus/neural/AeonOmega.java",
                    "src/main/java/fraymus/FraynixBoot.java",
                    "src/main/java/fraymus/FraymusConvergence.java"
                ),
                List.of(
                    "Ouroboros is part of the living recursive identity story, not a side gimmick.",
                    "The dream and omega lanes show how self-modification, sleep, and generation stay coupled."
                )
            ),
            new LivingProofLane(
                "proof",
                "Proof",
                "Formal and living proof harnesses that demonstrate the system's math, validation discipline, replay, and reality claims.",
                "validation and living proof discipline lane",
                strata("proof-lane", "kernel-discipline", "governing-core", "operator-shell"),
                List.of(
                    livingProofSection(surfaces, "engine", "advanced-subsystems"),
                    livingProofSection(surfaces, "fraynix", "daemons"),
                    livingProofSection(surfaces, "native", "runtime-native")
                ),
                List.of("porh [entity]", "benchmark complete", "validate", "native status", "willow status", "willow proof 8"),
                List.of(
                    "src/main/java/fraymus/validation/SystemProof.java",
                    "src/main/java/fraymus/FraynixProof.java",
                    "src/main/java/fraymus/validation/AIProof.java",
                    "src/main/java/fraymus/neural/AeonWillowCrusher.java",
                    "CORE_VS_DEMOS.md"
                ),
                List.of(
                    "A large part of Fraynix proof is already encoded in validators, not just in external benchmarks.",
                    "This lane keeps the older proof mains visible beside the newer kernel discipline.",
                    "Willow belongs here as a live proof organ because Fraynix already uses it in both boot and proof surfaces."
                )
            ),
            new LivingProofLane(
                "organism-validators",
                "Organism validators",
                "NEXUS, Portal, Lazarus, dream, survival, and soul-transmutation surfaces that act like living-system validators rather than flat feature demos.",
                "organism vitality and continuity lane",
                strata("organism-core", "absorption-core", "proof-lane", "operator-shell"),
                List.of(
                    livingProofSection(surfaces, "engine", "nexus-organism"),
                    livingProofSection(surfaces, "engine", "advanced-systems"),
                    livingProofSection(surfaces, "fraynix", "survival"),
                    livingProofSection(surfaces, "fraynix", "convergence"),
                    livingProofSection(surfaces, "fraynix", "genesis")
                ),
                List.of("nexus awaken", "nexus status", "lazarus start/stop", "lazarus crystallize", "dream", "transmudder proof <path>", "transmudder stats"),
                List.of(
                    "src/main/java/fraymus/organism/NEXUS_Organism.java",
                    "src/main/java/fraymus/absorption/Portal.java",
                    "src/main/java/fraymus/ui/PortalPanel.java",
                    "src/main/java/fraymus/web/FraynixWebSocket.java",
                    "src/main/java/fraymus/ollama/Transmudder.java",
                    "src/main/java/fraymus/ollama/UniversalMain.java"
                ),
                List.of(
                    "Portal, Lazarus, dream state, and NEXUS are living validation surfaces for the organism contract.",
                    "These are where persistence, intake, vitality, and continuity become visible to the operator.",
                    "Transmudder is the soul-side proof that documents and PDFs can be turned into living context instead of staying dead files."
                )
            ),
            new LivingProofLane(
                "substrate-transduction",
                "Substrate transduction",
                "EMF, omni-channel, thermal, and transduction surfaces that treat the substrate itself as part of the Fraynix control organism.",
                "substrate-aware signaling and transduction lane",
                strata("bare-metal-substrate-lane", "organism-core", "proof-lane", "operator-shell"),
                List.of(
                    livingProofSection(surfaces, "fraynix", "bridge"),
                    livingProofSection(surfaces, "engine", "omni-caster"),
                    livingProofSection(surfaces, "engine", "physics-engine"),
                    livingProofSection(surfaces, "native", "bridge-native")
                ),
                List.of("emf <concept>", "pulse <concept>", "omni thermal <msg>", "dump <msg>"),
                List.of(
                    "src/main/java/fraymus/metal/ApotheosisEMF.java",
                    "src/main/java/fraymus/reality/AEON_Apotheosis.java",
                    "src/main/java/fraymus/os/FrayOmniverseBuilder.java",
                    "src/main/java/fraymus/ultimate/UltimateImmortalKernel.java"
                ),
                List.of(
                    "This lane stays visible as substrate transduction, not flattened into generic messaging.",
                    "It is part of the organism's substrate ambition even when some paths are still exploratory."
                )
            )
        );
    }

    private static LivingProofSection livingProofSection(
        List<SurfaceManifest> surfaces,
        String surfaceId,
        String sectionId
    ) {
        Optional<SurfaceManifest> surface = surfaces.stream()
            .filter(item -> normalize(item.id()).equals(normalize(surfaceId)))
            .findFirst();
        if (surface.isEmpty()) {
            return new LivingProofSection(
                surfaceId,
                surfaceId,
                sectionId,
                sectionId,
                "",
                "/surfaces?surface=" + surfaceId + "&section=" + sectionId,
                "/launchpad?cmd=" + sectionId
            );
        }

        Optional<SectionManifest> section = surface.get().findSection(sectionId);
        if (section.isEmpty()) {
            return new LivingProofSection(
                surface.get().id(),
                surface.get().title(),
                sectionId,
                sectionId,
                "",
                "/surfaces?surface=" + surface.get().id() + "&section=" + sectionId,
                "/launchpad?cmd=" + sectionId
            );
        }

        String routeTarget = section.get().primaryVerbs().isEmpty()
            ? section.get().id()
            : section.get().primaryVerbs().get(0);
        return new LivingProofSection(
            surface.get().id(),
            surface.get().title(),
            section.get().id(),
            section.get().title(),
            section.get().theme(),
            "/surfaces?surface=" + surface.get().id() + "&section=" + section.get().id(),
            "/launchpad?cmd=" + routeTarget
        );
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

    public record SurfaceManifest(
        String id,
        String title,
        String description,
        String primarySubsystemId,
        String authorityLevel,
        String executionModel,
        boolean kernelOwnedPreferred,
        String operatorRole,
        String kernelEquivalentStatus,
        String migrationPolicy,
        String integrationPriority,
        String notes,
        List<String> agiStrata,
        List<SectionManifest> sections
    ) {
        public SurfaceManifest {
            agiStrata = List.copyOf(agiStrata);
            sections = List.copyOf(sections);
        }

        public int sectionCount() {
            return sections.size();
        }

        public int commandCount() {
            return sections.stream().mapToInt(SectionManifest::commandCount).sum();
        }

        public int discoveredSectionCount() {
            return (int) sections.stream().filter(SectionManifest::discoveredFromBootTree).count();
        }

        public int discoveredCommandCount() {
            return sections.stream()
                .filter(SectionManifest::discoveredFromBootTree)
                .mapToInt(SectionManifest::commandCount)
                .sum();
        }

        public Optional<SectionManifest> findSection(String sectionId) {
            if (sectionId == null || sectionId.isBlank()) {
                return Optional.empty();
            }
            String needle = normalize(sectionId);
            return sections.stream()
                .filter(section -> normalize(section.id()).equals(needle)
                    || normalize(section.title()).equals(needle))
                .findFirst();
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
    }

    public record LivingProofLane(
        String id,
        String title,
        String summary,
        String operatorRole,
        List<String> agiStrata,
        List<LivingProofSection> sections,
        List<String> focusCommands,
        List<String> evidencePaths,
        List<String> notes
    ) {
        public LivingProofLane {
            agiStrata = List.copyOf(agiStrata);
            sections = List.copyOf(sections);
            focusCommands = List.copyOf(focusCommands);
            evidencePaths = List.copyOf(evidencePaths);
            notes = List.copyOf(notes);
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
    }

    public record LivingProofSection(
        String surfaceId,
        String surfaceTitle,
        String sectionId,
        String sectionTitle,
        String theme,
        String jumpUrl,
        String routeUrl
    ) {
    }

    public record SectionManifest(
        String surfaceId,
        String id,
        String title,
        String theme,
        String defaultSubsystemId,
        List<String> linkedSubsystemIds,
        boolean kernelOverlap,
        String operatorRole,
        String kernelEquivalentStatus,
        String migrationPolicy,
        String integrationPriority,
        String notes,
        List<String> agiStrata,
        boolean discoveredFromBootTree,
        List<String> primaryVerbs,
        List<CommandDescriptor> commands
    ) {
        public SectionManifest {
            agiStrata = List.copyOf(agiStrata);
            linkedSubsystemIds = List.copyOf(linkedSubsystemIds);
            primaryVerbs = List.copyOf(primaryVerbs);
            commands = List.copyOf(commands);
        }

        public int commandCount() {
            return commands.size();
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
    }

    public record CommandDescriptor(
        String syntax,
        String description,
        String primaryVerb,
        boolean discoveredFromBootTree,
        boolean launchBranch,
        int sourceLine,
        List<String> aliases
    ) {
        public CommandDescriptor {
            aliases = List.copyOf(aliases);
        }

        public CommandDescriptor(String syntax, String description, String primaryVerb) {
            this(syntax, description, primaryVerb, false, false, -1, List.of());
        }
    }

    private record SurfaceMetadata(
        String authorityLevel,
        String executionModel,
        boolean kernelOwnedPreferred,
        String operatorRole,
        String kernelEquivalentStatus,
        String migrationPolicy,
        String integrationPriority,
        String notes,
        List<String> agiStrata
    ) {
    }

    private record SectionMetadata(
        boolean kernelOverlap,
        String operatorRole,
        String kernelEquivalentStatus,
        String migrationPolicy,
        String integrationPriority,
        String notes,
        List<String> agiStrata
    ) {
    }

    private record BootBranch(
        List<String> aliases,
        int lineNumber,
        boolean launchBranch,
        boolean multiCommandBranch,
        List<String> targetHints,
        String description
    ) {
        public String syntax() {
            return String.join(" | ", aliases);
        }

        public String primaryAlias() {
            return aliases.isEmpty() ? "" : aliases.get(0);
        }
    }
}
