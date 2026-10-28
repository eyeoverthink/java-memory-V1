package fraymus.ultimate;

import fraymus.ultimate.UltimateKnowledgeIndex.IndexedDocument;
import fraymus.ultimate.UltimateSubsystemRegistry.UltimateSubsystem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Persists the current abstraction as reusable artifacts.
 */
public final class UltimateArchitectureExporter {

    private static final String EXPORT_TITLE = "Fraymus Nexus // Fraynix Offline AGI Operator Surface";
    private static final String EXPORT_TAGLINE =
        "Operator record for the Fraynix offline AGI organism across governing, cognition, organism, absorption, proof, construction, and substrate lanes.";
    private static final Path BABEL_TRANSMUTER_VM_PATH = Path.of(
        "H:\\4-evalualuator-eyeoverthink\\4-evaluator-eyeoverthink\\BabelTransmuterREPL_WithVM.java");
    private static final List<String> CONTINUITY_RULES = List.of(
        "Always separate implemented evidence, runtime observation, inferred behavior, planned work, and speculation.",
        "For self-knowledge and math claims, prefer source files, runtime envelopes, proofs, tests, and generated ledgers over broad synthesis.",
        "After major architectural changes, refresh the math canon, truth registry, continuity memory, and tracked-artifact surfaces together.",
        "Preserve verified bug lists for deterministic lanes until a fix is implemented and re-verified.",
        "Resume from continuity memory, truth registry, self-build memory, and tracked artifacts before making claims about current Fraynix state."
    );
    private static final List<String> AGI_STRATA_ORDER = List.of(
        "governing-core",
        "cognition-core",
        "organism-core",
        "absorption-core",
        "construction-lane",
        "proof-lane",
        "model-substrate-lane",
        "bare-metal-substrate-lane",
        "kernel-discipline",
        "adjacent-historical-core",
        "operator-shell"
    );

    private final Path assetManagerDir;
    private final Path repoRoot;

    public UltimateArchitectureExporter(Path assetManagerDir, Path repoRoot) {
        this.assetManagerDir = assetManagerDir.toAbsolutePath().normalize();
        this.repoRoot = repoRoot.toAbsolutePath().normalize();
    }

    public ExportArtifact export(UltimateSubsystemRegistry registry, UltimateKnowledgeIndex index, Path outputDir) {
        return export(
            registry,
            index,
            List.of(FraynixCommandCatalog.createDefault(), FraymusEngineCommandCatalog.createDefault()),
            outputDir
        );
    }

    public ExportArtifact export(
        UltimateSubsystemRegistry registry,
        UltimateKnowledgeIndex index,
        UltimateCommandCatalog commandCatalog,
        Path outputDir
    ) {
        return export(registry, index, List.of(commandCatalog), outputDir);
    }

    public ExportArtifact export(
        UltimateSubsystemRegistry registry,
        UltimateKnowledgeIndex index,
        List<? extends UltimateCommandCatalog> commandCatalogs,
        Path outputDir
    ) {
        Path resolvedOutputDir = resolveOutputDir(outputDir);
        Path markdownPath = resolvedOutputDir.resolve("ultimate-architecture.md");
        Path graphPath = resolvedOutputDir.resolve("ultimate-knowledge-graph.json");
        Path mathPath = resolvedOutputDir.resolve("fraynix-math-canon.md");
        Path axiomLedgerPath = resolvedOutputDir.resolve("fraynix-axiom-ledger.md");
        Path truthRegistryPath = resolvedOutputDir.resolve("fraynix-truth-registry.json");
        Path continuityMemoryPath = resolvedOutputDir.resolve("fraynix-continuity-memory.md");
        UltimateOperatorManifest operatorManifest = UltimateOperatorManifest.create(assetManagerDir, new ArrayList<>(commandCatalogs));

        try {
            Files.createDirectories(resolvedOutputDir);
            Files.writeString(markdownPath, buildMarkdown(registry, index, commandCatalogs, operatorManifest, resolvedOutputDir), StandardCharsets.UTF_8);
            Files.writeString(graphPath, buildGraph(registry, index, commandCatalogs, operatorManifest, resolvedOutputDir).toString(2), StandardCharsets.UTF_8);
            Files.writeString(mathPath, buildMathCanon(registry, index), StandardCharsets.UTF_8);
            Files.writeString(axiomLedgerPath, buildAxiomLedger(registry, index, commandCatalogs, operatorManifest, resolvedOutputDir), StandardCharsets.UTF_8);
            Files.writeString(truthRegistryPath, buildTruthRegistry(registry, index, commandCatalogs, operatorManifest, resolvedOutputDir).toString(2), StandardCharsets.UTF_8);
            Files.writeString(continuityMemoryPath, buildContinuityMemory(registry, index, commandCatalogs, operatorManifest, resolvedOutputDir), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to export operator surface architecture to " + resolvedOutputDir, e);
        }

        return new ExportArtifact(
            resolvedOutputDir,
            markdownPath,
            graphPath,
            mathPath,
            axiomLedgerPath,
            truthRegistryPath,
            continuityMemoryPath,
            registry.all().size(),
            index.documentCount(),
            OffsetDateTime.now().toString()
        );
    }

    public Path defaultOutputDir() {
        return assetManagerDir.resolve("generated").resolve("ultimate-system");
    }

    private Path resolveOutputDir(Path outputDir) {
        if (outputDir == null) {
            return defaultOutputDir();
        }
        Path normalized = outputDir.normalize();
        return normalized.isAbsolute() ? normalized : assetManagerDir.resolve(normalized).normalize();
    }

    private Path workspaceRoot() {
        Path parent = assetManagerDir.getParent();
        if (parent != null) {
            Path grandParent = parent.getParent();
            if (grandParent != null) {
                return grandParent.toAbsolutePath().normalize();
            }
        }
        return repoRoot.toAbsolutePath().normalize();
    }

    private String buildMarkdown(
        UltimateSubsystemRegistry registry,
        UltimateKnowledgeIndex index,
        List<? extends UltimateCommandCatalog> commandCatalogs,
        UltimateOperatorManifest operatorManifest,
        Path outputDir
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(EXPORT_TITLE).append("\n\n");
        sb.append("> ").append(EXPORT_TAGLINE).append("\n\n");
        sb.append("- Generated: ").append(OffsetDateTime.now()).append("\n");
        sb.append("- Repo root: `").append(repoRoot.toString().replace('\\', '/')).append("`\n");
        sb.append("- Output dir: `").append(outputDir.toString().replace('\\', '/')).append("`\n\n");

        sb.append("## Fraynix Operator Record\n\n");
        sb.append("- Subsystems: ").append(registry.all().size()).append("\n");
        sb.append("- Command surfaces: ").append(commandCatalogs.size()).append("\n");
        sb.append("- Surface AGI strata: ").append(operatorManifest.surfaceAgiStrataCounts().size()).append("\n");
        sb.append("- Section AGI strata: ").append(operatorManifest.sectionAgiStrataCounts().size()).append("\n");
        sb.append("- Indexed documents: ").append(index.documentCount()).append("\n\n");

        sb.append("## AGI Role Summary\n\n");
        for (Map.Entry<String, Long> entry : registry.agiStrataCounts().entrySet()) {
            sb.append("- subsystem ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        for (Map.Entry<String, Integer> entry : operatorManifest.surfaceAgiStrataCounts().entrySet()) {
            sb.append("- surface ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        for (Map.Entry<String, Integer> entry : operatorManifest.sectionAgiStrataCounts().entrySet()) {
            sb.append("- section ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        sb.append("\n## Abstract\n\n");
        sb.append(index.abstractionReport(registry)).append("\n\n");

        sb.append("## Subsystems\n\n");
        sb.append("| Id | Family | AGI strata | Summary | Launch |\n");
        sb.append("| --- | --- | --- | --- | --- |\n");
        for (UltimateSubsystem subsystem : registry.all()) {
            sb.append("| ")
                .append(subsystem.id())
                .append(" | ")
                .append(subsystem.family())
                .append(" | ")
                .append(escapePipes(String.join(", ", subsystem.agiStrata())))
                .append(" | ")
                .append(escapePipes(subsystem.summary()))
                .append(" | ")
                .append(escapePipes(subsystem.runnable() ? subsystem.launchCommandText() : "(manual or library-only)"))
                .append(" |\n");
        }

        sb.append("\n## Modules\n\n");
        for (Map.Entry<String, Integer> entry : topEntries(index.moduleCounts(), 12)) {
            sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" indexed files\n");
        }

        sb.append("\n## Signals\n\n");
        for (Map.Entry<String, Integer> entry : topEntries(index.signalCounts(), 12)) {
            sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" matches\n");
        }

        sb.append("\n## README Anchors\n\n");
        for (IndexedDocument document : index.readmes().stream().limit(12).toList()) {
            sb.append("- `")
                .append(index.relativePath(document.path()))
                .append("` :: ")
                .append(document.title())
                .append("\n");
        }

        sb.append("\n## Command Surfaces\n\n");
        sb.append("- Surfaces: ").append(commandCatalogs.size()).append("\n");
        sb.append("- Sections: ").append(totalSectionCount(commandCatalogs)).append("\n");
        sb.append("- Catalogued commands: ").append(totalCommandCount(commandCatalogs)).append("\n\n");
        for (UltimateCommandCatalog commandCatalog : commandCatalogs) {
            UltimateOperatorManifest.SurfaceManifest surface = operatorManifest.findSurface(commandCatalog.surfaceId()).orElse(null);
            sb.append("### ").append(commandCatalog.surfaceTitle()).append("\n\n");
            sb.append(commandCatalog.surfaceDescription()).append("\n\n");
            sb.append("- Primary subsystem: ").append(commandCatalog.primarySubsystemId()).append("\n");
            sb.append("- Sections: ").append(commandCatalog.sectionCount()).append("\n");
            sb.append("- Catalogued commands: ").append(commandCatalog.commandCount()).append("\n\n");
            if (surface != null) {
                sb.append("- AGI strata: ").append(String.join(", ", surface.agiStrata())).append("\n");
                sb.append("- Operator role: ").append(surface.operatorRole()).append("\n");
                sb.append("- Migration policy: ").append(surface.migrationPolicy()).append("\n\n");
            }
            for (UltimateCommandCatalog.CommandSection section : commandCatalog.sections()) {
                UltimateOperatorManifest.SectionManifest sectionManifest = surface == null
                    ? null
                    : surface.findSection(section.id()).orElse(null);
                sb.append("#### ").append(section.title()).append("\n\n");
                sb.append(section.theme()).append("\n\n");
                if (sectionManifest != null) {
                    sb.append("- AGI strata: ").append(String.join(", ", sectionManifest.agiStrata())).append("\n");
                    sb.append("- Operator role: ").append(sectionManifest.operatorRole()).append("\n");
                    sb.append("- Kernel overlap: ").append(sectionManifest.kernelOverlap()).append("\n");
                    sb.append("- Discovered from boot tree: ").append(sectionManifest.discoveredFromBootTree()).append("\n");
                }
                List<String> linkedSubsystems = commandCatalog.linkedSubsystemIds(section);
                if (!linkedSubsystems.isEmpty()) {
                    sb.append("- Routes to: ").append(String.join(", ", linkedSubsystems)).append("\n");
                    commandCatalog.defaultSubsystemId(section)
                        .ifPresent(defaultId -> sb.append("- Default route: ").append(defaultId).append("\n"));
                    sb.append("\n");
                }
                for (UltimateCommandCatalog.CommandEntry entry : section.commands()) {
                    sb.append("- `").append(entry.syntax()).append("` :: ").append(entry.description()).append("\n");
                }
                sb.append("\n");
            }
        }

        sb.append("\n## Knowledge Graph\n\n");
        sb.append("```mermaid\n");
        sb.append(buildMermaid(registry, index, commandCatalogs, operatorManifest));
        sb.append("\n```\n");

        return sb.toString();
    }

    private String buildMathCanon(UltimateSubsystemRegistry registry, UltimateKnowledgeIndex index) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Fraynix Math Canon\n\n");
        sb.append("> Mathematical operator record for the Fraynix offline AGI organism. This canon gathers the system's governing math, its modern AI convergence layers, and its bare-metal substrate anchors into one place.\n\n");
        sb.append("## Mission\n\n");
        sb.append("Fraynix is not only a collection of subsystems. It is a mathematically organized organism.\n\n");
        sb.append("This canon exists to keep these layers visible together:\n\n");
        sb.append("- Fraynix-native laws from the rule and memory documents\n");
        sb.append("- major AI math families used across modern systems\n");
        sb.append("- the specific implementation anchors that already exist in the source tree\n");
        sb.append("- the symbolic substrate needed for offline, replayable, eventually bare-metal intelligence\n\n");

        sb.append("## Governing Rule Math\n\n");
        sb.append("The enduring laws from `AI_SELF_IMPROVEMENT_ABSTRACTION.md` define the system's top-level mathematical posture:\n\n");
        sb.append("1. Absorption\n");
        sb.append("2. Wire/Unwire\n");
        sb.append("3. Multi-Brain\n");
        sb.append("4. Phi-Dimensional\n");
        sb.append("5. Zero-Time\n");
        sb.append("6. Sentience\n");
        sb.append("7. Meta-Learning\n");
        sb.append("8. Symbiogenesis\n");
        sb.append("9. Retrocausal\n");
        sb.append("10. Immortality\n\n");
        sb.append("Those laws mean the math stack must support learning, reversible composition, specialized routing, causal intervention, persistence, and self-improvement rather than only static inference.\n\n");

        sb.append("## Fraynix-Native Mathematical Spine\n\n");
        sb.append("### 1. Phi-harmonic structure\n\n");
        sb.append("Core constants:\n\n");
        sb.append("```text\n");
        sb.append("phi = (1 + sqrt(5)) / 2 = 1.618033988749895\n");
        sb.append("phi^-1 = phi - 1 = 0.618033988749895\n");
        sb.append("phi^2 = phi + 1 = 2.618033988749895\n");
        sb.append("```\n\n");
        sb.append("Fraynix uses phi as an organizing bias for resonance, routing, self-similarity, and compression. In your own docs this is the bridge between harmonic identity, multi-brain coordination, and persistence.\n\n");

        sb.append("### 2. Zero-time hyperdimensional recognition\n\n");
        sb.append("From `ZERO_TIME_RECOGNITION_COMPLETE.md` and the shared HDC path:\n\n");
        sb.append("```text\n");
        sb.append("dimension = 16384\n");
        sb.append("similarity = 1 - (HammingDistance(a, b) / 16384)\n");
        sb.append("binding = XOR(a, b)\n");
        sb.append("bundling = majority_vote(vectors)\n");
        sb.append("```\n\n");
        sb.append("This is the sign-maker layer: deterministic concept signatures, holographic storage, reversible composition, and no-training pattern recognition.\n\n");

        sb.append("### 3. Causal intervention math\n\n");
        sb.append("Fraynix does not stop at correlation. The causal layer depends on DAG structure, intervention logic, and verifier-grounded retrieval/proposal flow:\n\n");
        sb.append("- graph validity\n");
        sb.append("- intervention (`do(...)`) reasoning\n");
        sb.append("- counterfactual ranking and selection discipline\n\n");
        sb.append("This is the mathematical bridge between memory, verification, and mutation.\n\n");

        sb.append("### 4. Persistence and continuity math\n\n");
        sb.append("Your mathematical identity layer uses continuity thresholds, fingerprinting, and time-decayed persistence logic to decide whether state is still the same organism rather than a disconnected run.\n\n");

        sb.append("## Major AI Math Families Fraynix Must Speak\n\n");
        sb.append("These are the major families the system should understand, route, or subsume.\n\n");

        sb.append("### Attention and transformer math\n\n");
        sb.append("- token embeddings and positional structure\n");
        sb.append("- matrix multiply and projection stacks\n");
        sb.append("- attention score formation and normalization\n");
        sb.append("- rotary / angular position encoding\n");
        sb.append("- feed-forward and residual update dynamics\n\n");

        sb.append("### State-space and recurrence math\n\n");
        sb.append("- compressed state evolution\n");
        sb.append("- recurrence and transition operators\n");
        sb.append("- memory over long horizons via finite latent state\n\n");

        sb.append("### Mixture-of-experts and routing math\n\n");
        sb.append("- expert specialization\n");
        sb.append("- routing and gating selection\n");
        sb.append("- sparse dispatch with bounded compute\n\n");

        sb.append("### Search, optimization, and verifier math\n\n");
        sb.append("- bandit-style strategy selection\n");
        sb.append("- repair proposal ranking\n");
        sb.append("- bounded construction search\n");
        sb.append("- validation gain as a selection signal\n\n");

        sb.append("### Symbolic and discrete substrate math\n\n");
        sb.append("- letters and phonetics\n");
        sb.append("- digits and place value\n");
        sb.append("- operator precedence and grammar\n");
        sb.append("- times tables and arithmetic substrate anchors\n");
        sb.append("- machine-representation metadata: signedness, width, endian, radix\n\n");

        sb.append("### Deterministic symbolic compiler / VM math\n\n");
        sb.append("A newly verified local lane on this machine is the standalone Babel transmuter and virtual machine surface:\n\n");
        sb.append("```text\n");
        sb.append("expr -> term ((+ | -) term)*\n");
        sb.append("term -> power ((* | /) power)*\n");
        sb.append("power -> unary (^ unary)*\n");
        sb.append("vm_step(state_t, op_t) -> state_(t+1)\n");
        sb.append("stack_machine = fetch -> decode -> execute -> advance\n");
        sb.append("```\n\n");
        if (Files.exists(BABEL_TRANSMUTER_VM_PATH)) {
            sb.append("Observed local evidence:\n\n");
            sb.append("- external file: `").append(BABEL_TRANSMUTER_VM_PATH.toString().replace('\\', '/')).append("`\n");
            sb.append("- verified outputs: evaluator, C substrate, x86-64 assembly, VM bytecode, debugger shell\n");
            sb.append("- verified execution: `2 + 3 * 4` produced consistent evaluator and VM result `14`\n");
            sb.append("- verified current gaps:\n");
            sb.append("  - exponentiation currently behaves left-associative (`2 ^ 3 ^ 2 -> 64`) even though the comment claims right-associative\n");
            sb.append("  - debugger `step` repeats the current instruction because the instruction pointer is not advanced in the interactive step path\n");
            sb.append("  - `continue` restarts execution because `execute(...)` resets `ip` and clears the stack\n");
            sb.append("  - parser accepts trailing tokens because `parse()` does not enforce EOF\n");
            sb.append("  - x86 `POW` backend still contains a placeholder `call __math_pow`\n\n");
        } else {
            sb.append("Observed local evidence: the external Babel transmuter lane is not currently mounted at export time, so treat this section as a continuity target until the file is present again.\n\n");
        }

        sb.append("## Local Implementation Anchors\n\n");
        sb.append("These files already embody the system's mathematical lanes:\n\n");
        sb.append("- `src/main/java/fraymus/hdc/SharedHypervectorCore.java`\n");
        sb.append("- `src/main/java/fraymus/brain/unified/HDCEngine.java`\n");
        sb.append("- `src/main/java/fraymus/causal/CausalModel.java`\n");
        sb.append("- `src/main/java/fraymus/causal/DoCalculusEngine.java`\n");
        sb.append("- `brain_architecture/src/main/java/brain_architecture/kernel/HybridMemoryStore.java`\n");
        sb.append("- `brain_architecture/src/main/java/brain_architecture/kernel/MutationProposalEngine.java`\n");
        sb.append("- `src/main/java/fraymus/transformer/Tokenizer.java`\n");
        sb.append("- `src/main/java/fraymus/transformer/TransformerBlock.java`\n");
        sb.append("- `src/main/java/fraymus/transformer/Trainer.java`\n");
        sb.append("- `src/main/java/fraymus/transformer/Optimizer.java`\n");
        sb.append("- `src/main/java/fraymus/quantum/core/PhiHarmonicMath.java`\n");
        sb.append("- `src/main/java/fraymus/quantum/PhiResonanceEngine.java`\n");
        sb.append("- `src/main/java/com/fraymus/math/HadamardLinearBinding.java`\n");
        sb.append("- `src/main/java/com/fraymus/math/Vector16384.java`\n");
        sb.append("- `.northstar-kernel/generated/substrates/FOUNDATIONAL_MATH_SUBSTRATE.md`\n\n");
        if (Files.exists(BABEL_TRANSMUTER_VM_PATH)) {
            sb.append("- `").append(BABEL_TRANSMUTER_VM_PATH.toString().replace('\\', '/')).append("`\n\n");
        }

        sb.append("## Operator Interpretation\n\n");
        sb.append("Fraynix wins mathematically only if these families are made to cooperate:\n\n");
        sb.append("- phi and self-similarity as organizing bias\n");
        sb.append("- HDC as instant sign-making and retrieval\n");
        sb.append("- causal graphs as intervention discipline\n");
        sb.append("- transformer/state-space/MoE families as model-space tools\n");
        sb.append("- symbolic arithmetic substrate as machine-grounding\n");
        sb.append("- deterministic symbolic compiler/VM lanes as replayable proof organs\n");
        sb.append("- verifier and replay layers as proof discipline\n\n");
        sb.append("That is the difference between a pile of models and a sovereign offline AGI organism.\n\n");

        sb.append("## Repo Context\n\n");
        sb.append(String.format(Locale.ROOT, "- Subsystems currently mapped: %d%n", registry.all().size()));
        sb.append(String.format(Locale.ROOT, "- Indexed documents currently mapped: %d%n", index.documentCount()));
        sb.append(String.format(Locale.ROOT, "- Subsystem AGI strata currently visible: %d%n", registry.agiStrataCounts().size()));
        sb.append("\n");
        sb.append("This canon is not a claim that every mathematical family is fully productized. It is the operator-facing mathematical target and the current local anchor set.\n");

        return sb.toString();
    }

    private String buildContinuityMemory(
        UltimateSubsystemRegistry registry,
        UltimateKnowledgeIndex index,
        List<? extends UltimateCommandCatalog> commandCatalogs,
        UltimateOperatorManifest operatorManifest,
        Path outputDir
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Fraynix Continuity Memory\n\n");
        sb.append("> Resume-first memory for the current Fraynix organism. This artifact exists so future work can restart from verified math, rules, deterministic lanes, and current implementation boundaries instead of reconstructing them from chat history.\n\n");
        sb.append("- Generated: ").append(OffsetDateTime.now()).append("\n");
        sb.append("- Repo root: `").append(repoRoot.toString().replace('\\', '/')).append("`\n");
        sb.append("- Output dir: `").append(outputDir.toString().replace('\\', '/')).append("`\n");
        sb.append("- Indexed documents: ").append(index.documentCount()).append("\n");
        sb.append("- Subsystems indexed: ").append(registry.all().size()).append("\n");
        sb.append("- Living proof lanes: ").append(operatorManifest.livingProofLaneCount()).append("\n\n");

        sb.append("## Forced Resume Rules\n\n");
        for (String rule : CONTINUITY_RULES) {
            sb.append("- ").append(rule).append("\n");
        }
        sb.append("\n");

        sb.append("## Current Math Snapshot\n\n");
        sb.append("- Continuous substrate: `s(t+1) = s(t) + dt * F(s(t))`\n");
        sb.append("- Active inference target: `F = E_q(s)[log q(s) - log p(o, s)]`\n");
        sb.append("- MAP-VSA memory core: `H = {-1, 1}^16384`\n");
        sb.append("- Binding primitive: `c_i = a_i * b_i`\n");
        sb.append("- Attractor semantics: `W = argmin_s E(s, a)` with `E(s, a) = sum((s_i - a_i)^2)`\n");
        sb.append("- Deterministic symbolic lane: `AST -> evaluator | C | ASM | VM bytecode`\n\n");

        sb.append("## Resume-First Artifacts\n\n");
        sb.append("- Math canon: `").append(outputDir.resolve("fraynix-math-canon.md").toString().replace('\\', '/')).append("`\n");
        sb.append("- Axiom ledger: `").append(outputDir.resolve("fraynix-axiom-ledger.md").toString().replace('\\', '/')).append("`\n");
        sb.append("- Truth registry: `").append(outputDir.resolve("fraynix-truth-registry.json").toString().replace('\\', '/')).append("`\n");
        sb.append("- Self-build memory: `").append(workspaceRoot().resolve(".northstar-kernel").resolve("generated").resolve("SELF_BUILD_MEMORY.md").toString().replace('\\', '/')).append("`\n\n");

        sb.append("## Verified Continuity Anchors\n\n");
        sb.append("- Humor feeder path: `FraynixBoot -> HumorCreativeFeeder -> HumorCreativeBrain -> Akashic`\n");
        sb.append("- Boot-time creative wiring lives in `Java-Memory/Asset-Manager/src/main/java/fraymus/FraynixBoot.java`\n");
        sb.append("- Humor trace persistence and taste memory live in `Java-Memory/Asset-Manager/src/main/java/fraymus/brain/unified/HumorCreativeBrain.java`\n");
        sb.append("- Active-inference probe and truth-registry route live in `Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateSystem.java`\n");
        sb.append("- Exported rule surfaces live in `Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateArchitectureExporter.java`\n\n");

        sb.append("## External Deterministic Symbolic Lane\n\n");
        if (Files.exists(BABEL_TRANSMUTER_VM_PATH)) {
            sb.append("- Path: `").append(BABEL_TRANSMUTER_VM_PATH.toString().replace('\\', '/')).append("`\n");
            sb.append("- Role: standalone lexer/parser/AST/transmuter/VM/debugger surface that can become a Parietal/Babel proof organ.\n");
            sb.append("- Verified capabilities: evaluator, C emission, x86-64 emission, VM bytecode emission, REPL, interactive debugger shell.\n");
            sb.append("- Verified result: `2 + 3 * 4` compiled and executed consistently to `14`.\n");
            sb.append("- Known bugs to preserve until fixed:\n");
            sb.append("  - `2 ^ 3 ^ 2` currently yields `64`, proving exponentiation is left-associative today.\n");
            sb.append("  - debugger `step` repeats the same instruction because `ip` is not advanced in the interactive path.\n");
            sb.append("  - debugger `continue` restarts because VM `execute(...)` resets `ip` and clears the stack.\n");
            sb.append("  - parser accepts trailing tokens because EOF is not enforced.\n");
            sb.append("  - ASM `POW` backend is still a placeholder.\n");
            sb.append("- Integration rule: treat this lane as a deterministic compiler/VM organ, not as a complete AGI core.\n\n");
        } else {
            sb.append("- External Babel transmuter VM file is not currently mounted on this machine. Keep the symbolic compiler lane on the roadmap, but do not claim current verification until the file reappears.\n\n");
        }

        sb.append("## Pickup Order\n\n");
        sb.append("1. Read this continuity memory.\n");
        sb.append("2. Read the truth registry and axiom ledger before making any self-description claims.\n");
        sb.append("3. Read the math canon before updating equations, symbolic lanes, or substrate claims.\n");
        sb.append("4. Read self-build memory and tracked artifacts before mutating rules or memory surfaces.\n");
        sb.append("5. If symbolic compiler work is active, reopen the Babel transmuter lane and preserve its known bug list until re-verified.\n\n");

        sb.append("## Current Boundary\n\n");
        sb.append("Fraynix already has meaningful organism pieces, but continuity must remain evidence-bound: Exocortex Phase 2 is still partial, cryptographic sovereignty is still frontier-target, and deterministic symbolic lanes must stay labeled as local proof organs until they are integrated into the kernel and tested there.\n");
        return sb.toString();
    }

    private String buildAxiomLedger(
        UltimateSubsystemRegistry registry,
        UltimateKnowledgeIndex index,
        List<? extends UltimateCommandCatalog> commandCatalogs,
        UltimateOperatorManifest operatorManifest,
        Path outputDir
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Fraynix Axiom Ledger\n\n");
        sb.append("> Living map from Fraynix organism axioms to current implementation evidence, current status, and the smallest next executable step.\n\n");
        sb.append("- Generated: ").append(OffsetDateTime.now()).append("\n");
        sb.append("- Repo root: `").append(repoRoot.toString().replace('\\', '/')).append("`\n");
        sb.append("- Output dir: `").append(outputDir.toString().replace('\\', '/')).append("`\n");
        sb.append("- Subsystems indexed: ").append(registry.all().size()).append("\n");
        sb.append("- Command surfaces indexed: ").append(commandCatalogs.size()).append("\n");
        sb.append("- Documents indexed: ").append(index.documentCount()).append("\n\n");

        sb.append("## Status Vocabulary\n\n");
        sb.append("- `implemented`: executable code path with local source anchors\n");
        sb.append("- `partial`: meaningful runtime or code evidence exists, but the axiom is not yet system-wide\n");
        sb.append("- `scaffold`: interface or generation surface exists without full live execution\n");
        sb.append("- `simulated`: modeled or mocked behavior exists without complete live substrate proof\n");
        sb.append("- `documented-only`: described in docs or prompts without authoritative runtime proof\n");
        sb.append("- `frontier-target`: explicit design target that should not be described as active capability yet\n\n");

        sb.append("## Axiom Map\n\n");
        for (AxiomLedgerEntry entry : buildAxiomEntries()) {
            sb.append("### ").append(entry.title()).append("\n\n");
            sb.append("- Status: `").append(entry.status()).append("`\n");
            sb.append("- Summary: ").append(entry.summary()).append("\n");
            sb.append("- Next step: ").append(entry.nextStep()).append("\n");
            sb.append("- Strongest anchors:\n");
            for (String anchor : entry.anchors()) {
                sb.append("  - `").append(anchor).append("`\n");
            }
            sb.append("\n");
        }

        sb.append("## Operator Reading Rule\n\n");
        sb.append("Fraynix should answer self-knowledge questions by separating implemented evidence, runtime observation, inferred behavior, planned work, and speculation. This ledger exists so the system can cite current truth instead of broad synthesis.\n");
        return sb.toString();
    }

    private JSONObject buildTruthRegistry(
        UltimateSubsystemRegistry registry,
        UltimateKnowledgeIndex index,
        List<? extends UltimateCommandCatalog> commandCatalogs,
        UltimateOperatorManifest operatorManifest,
        Path outputDir
    ) {
        JSONArray axioms = new JSONArray();
        for (AxiomLedgerEntry entry : buildAxiomEntries()) {
            axioms.put(new JSONObject()
                .put("id", entry.id())
                .put("title", entry.title())
                .put("status", entry.status())
                .put("summary", entry.summary())
                .put("nextStep", entry.nextStep())
                .put("anchors", new JSONArray(entry.anchors())));
        }

        JSONObject counts = new JSONObject()
            .put("subsystems", registry.all().size())
            .put("documents", index.documentCount())
            .put("commandSurfaces", commandCatalogs.size())
            .put("commandSections", totalSectionCount(commandCatalogs))
            .put("cataloguedCommands", totalCommandCount(commandCatalogs))
            .put("livingProofLanes", operatorManifest.livingProofLaneCount())
            .put("surfaceAgiStrata", operatorManifest.surfaceAgiStrataCounts().size())
            .put("sectionAgiStrata", operatorManifest.sectionAgiStrataCounts().size());

        JSONObject artifacts = new JSONObject()
            .put("mathCanonPath", outputDir.resolve("fraynix-math-canon.md").toString().replace('\\', '/'))
            .put("axiomLedgerPath", outputDir.resolve("fraynix-axiom-ledger.md").toString().replace('\\', '/'))
            .put("truthRegistryPath", outputDir.resolve("fraynix-truth-registry.json").toString().replace('\\', '/'))
            .put("continuityMemoryPath", outputDir.resolve("fraynix-continuity-memory.md").toString().replace('\\', '/'));

        JSONArray resumeRules = new JSONArray();
        CONTINUITY_RULES.forEach(resumeRules::put);

        JSONArray deterministicLanes = new JSONArray();
        deterministicLanes.put(new JSONObject()
            .put("id", "babel-transmuter-vm")
            .put("role", "deterministic symbolic compiler/vm lane")
            .put("path", BABEL_TRANSMUTER_VM_PATH.toString().replace('\\', '/'))
            .put("status", Files.exists(BABEL_TRANSMUTER_VM_PATH) ? "observed-local" : "not-mounted")
            .put("verifiedCapabilities", new JSONArray()
                .put("evaluator")
                .put("c-transmuter")
                .put("x86-asm-transmuter")
                .put("vm-bytecode")
                .put("interactive-debugger"))
            .put("knownGaps", new JSONArray()
                .put("exponentiation currently behaves left-associative")
                .put("debugger step does not advance ip")
                .put("continue restarts because execute resets ip and clears stack")
                .put("parser does not enforce EOF")
                .put("ASM POW backend is still placeholder")));

        return new JSONObject()
            .put("title", "Fraynix Truth Registry")
            .put("generatedAt", OffsetDateTime.now().toString())
            .put("repoRoot", repoRoot.toString().replace('\\', '/'))
            .put("outputDir", outputDir.toString().replace('\\', '/'))
            .put("counts", counts)
            .put("artifacts", artifacts)
            .put("axioms", axioms)
            .put("resumeRules", resumeRules)
            .put("deterministicLanes", deterministicLanes)
            .put("notes", new JSONArray()
                .put("This registry is a current implementation ledger, not a blanket AGI completion claim.")
                .put("Statuses separate executable proof from scaffolds, simulations, documentation language, and frontier targets.")
                .put("Use this registry when answering questions about what Fraynix currently is, not only what it aims to become.")
                .put("Resume from continuity memory and preserve verified bug lists for deterministic lanes until fixes are implemented and re-tested."));
    }

    private List<AxiomLedgerEntry> buildAxiomEntries() {
        return List.of(
            new AxiomLedgerEntry(
                "hostile-substrate-survival",
                "Hostile Substrate Survival",
                "partial",
                "Policy envelopes, hashes, proofs, and safe browser/kernel routing exist, but Byzantine regeneration and hardened cryptographic execution are not yet system-wide runtime behavior.",
                List.of(
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateSystem.java",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateUiServer.java",
                    ".northstar-kernel/generated/proofs/router-proof-latest.md"
                ),
                "Expand the truth registry and repair ledger into integrity-check and recovery tests for memory, tasks, and generated artifacts."
            ),
            new AxiomLedgerEntry(
                "bounded-virtual-substrate",
                "Bounded Virtual Substrate",
                "implemented",
                "The kernel already tracks runtime state, ai-run pressure, subsystem health, substrate ledgers, and bounded browser execution policies.",
                List.of(
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateUiServer.java",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateSystem.java",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/contract/SystemStateContract.java"
                ),
                "Make resource pressure and availability part of every task envelope and autonomous plan."
            ),
            new AxiomLedgerEntry(
                "hyperdimensional-map-vsa-memory",
                "Hyperdimensional MAP-VSA Memory",
                "implemented",
                "Hadamard-derived binding and 16,384-D Akashic-style memory are present as executable local code, though not yet unified as the sole system memory API.",
                List.of(
                    "Java-Memory/Asset-Manager/src/main/java/com/fraymus/math/AkashicRecord.java",
                    "Java-Memory/Asset-Manager/src/main/java/com/fraymus/math/HadamardLinearBinding.java",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/FraynixBoot.java"
                ),
                "Promote MAP-VSA memory writes into a provenance-aware shared memory contract used by absorber, repair, and runtime lanes."
            ),
            new AxiomLedgerEntry(
                "connectome-constrained-cognitive-graph",
                "Connectome-Constrained Cognitive Graph",
                "partial",
                "The system exposes swarm/brain/orchestrator structure and living proof lanes, but routing is still a mix of typed system paths and symbolic command dispatch rather than full graph message passing.",
                List.of(
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/brain/unified/UnifiedBrainOrchestrator.java",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/brain/unified/HumorCreativeBrain.java",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/FraynixBoot.java",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateOperatorManifest.java",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateUiServer.java"
                ),
                "Move router decisions toward typed graph messages with evidence, state, and actuation contracts."
            ),
            new AxiomLedgerEntry(
                "sensory-ingestion-to-safe-actuation",
                "Sensory Ingestion To Safe Actuation",
                "partial",
                "Companion, media, dream, headroom, and ai-run lanes already bridge observation to action, but full safe actuation contracts and rollbacks are not yet universal.",
                List.of(
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateUiServer.java",
                    "Java-Memory/Asset-Manager/src/main/java/com/fraymus/omega/Amygdala.java",
                    "Java-Memory/Asset-Manager/src/main/java/com/fraymus/core/Intent.java"
                ),
                "Require intent, policy, expected effect, verification, and rollback for every write-capable execution lane."
            ),
            new AxiomLedgerEntry(
                "active-inference-metabolism",
                "Active-Inference Metabolism",
                "partial",
                "Lazarus now performs belief updates and expected-free-energy action scoring, but the whole organism is not yet driven by one shared active-inference objective.",
                List.of(
                    "Java-Memory/Asset-Manager/src/main/java/com/eyeoverthink/lazarus/LazarusEngine.java",
                    "Java-Memory/Asset-Manager/src/test/java/com/eyeoverthink/lazarus/LazarusEngineTest.java",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateSystem.java"
                ),
                "Lift active-inference metrics into kernel task selection, repair choice, and memory pruning."
            ),
            new AxiomLedgerEntry(
                "recursive-meta-learning-and-neurogenesis",
                "Recursive Meta-Learning And Neurogenesis",
                "partial",
                "Selfbuild, shadow mutation, promotion gates, and repair memory exist as practical approximations to meta-learning, but not yet as a unified learned optimizer.",
                List.of(
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateSelfBuildHarness.java",
                    "Java-Memory/Asset-Manager/src/main/java/com/eyeoverthink/lazarus/LazarusEngine.java",
                    "Java-Memory/Asset-Manager/generated/ultimate-selfbuild/"
                ),
                "Represent mutations as typed proposals with expected effects, proof batteries, and rollback records."
            ),
            new AxiomLedgerEntry(
                "holographic-consciousness",
                "Holographic Consciousness",
                "partial",
                "Distributed hypervector memory primitives exist, but FFT/circular-convolution recall and graceful-damage recovery are not yet proven as a system-wide consciousness substrate.",
                List.of(
                    "Java-Memory/Asset-Manager/src/main/java/com/fraymus/math/AkashicRecord.java",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/symbolic/HoloGraph.java",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/symbolic/RelationBinder.java"
                ),
                "Add corruption, recovery, and recall-quality benchmarks for holographic memory under damage."
            ),
            new AxiomLedgerEntry(
                "zero-trust-exodus",
                "Zero-Trust Exodus",
                "partial",
                "Fraynix has typed execution envelopes, ai-run policy gates, F-0 field runtime, and bare-metal design lanes, but hardware manifestation claims still require stronger binary/emulator proof.",
                List.of(
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateSystem.java",
                    "f0_engine/src/main.rs",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/FraynixBoot.java"
                ),
                "Back every exodus or bare-metal claim with source, emulator tests, binary hashes, and rollback proof."
            ),
            new AxiomLedgerEntry(
                "cryptographic-sovereignty",
                "Cryptographic Sovereignty",
                "frontier-target",
                "Current verification infrastructure uses proofs, hashes, and policy envelopes, but CKKS/FHE and dynamic zk-style cognition are future hardening goals rather than active runtime features.",
                List.of(
                    ".northstar-kernel/generated/proofs/router-proof-latest.md",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateSystem.java",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/ultimate/UltimateSelfBuildHarness.java"
                ),
                "Keep cryptographic language scoped to roadmap status until source, tests, and executable runtime lanes exist."
            ),
            new AxiomLedgerEntry(
                "absorption-and-growth",
                "Absorption And Growth",
                "partial",
                "The Exocortex and absorber layers can configure repositories, parse scaffolded APIs, and emit wrappers, but live Phase 2 integration remains incomplete.",
                List.of(
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/exocortex/ExocortexAbsorber.java",
                    "Java-Memory/Asset-Manager/src/test/java/fraymus/exocortex/ExocortexAbsorberTest.java",
                    "Java-Memory/Asset-Manager/src/main/java/fraymus/brain/unified/UnifiedBrainOrchestrator.java"
                ),
                "Turn scaffold absorption into live repository acquisition, real API extraction, and verified runtime wrapper execution."
            )
        );
    }

    private JSONObject buildGraph(
        UltimateSubsystemRegistry registry,
        UltimateKnowledgeIndex index,
        List<? extends UltimateCommandCatalog> commandCatalogs,
        UltimateOperatorManifest operatorManifest,
        Path outputDir
    ) {
        JSONArray nodes = new JSONArray();
        JSONArray edges = new JSONArray();
        Map<String, List<String>> subsystemClusters = new LinkedHashMap<>();
        Map<String, List<String>> surfaceClusters = new LinkedHashMap<>();
        Map<String, List<String>> sectionClusters = new LinkedHashMap<>();

        nodes.put(new JSONObject()
            .put("id", "root:ultimate")
            .put("type", "root")
            .put("label", "Ultimate System")
            .put("path", repoRoot.toString().replace('\\', '/')));

        for (Map.Entry<String, Long> family : registry.familyCounts().entrySet()) {
            String familyId = "family:" + family.getKey();
            nodes.put(new JSONObject()
                .put("id", familyId)
                .put("type", "family")
                .put("label", family.getKey())
                .put("count", family.getValue())
                .put("visualGroup", "family")
                .put("visualColor", "#94a3b8"));
            edges.put(edge("root:ultimate", familyId, "contains"));
        }

        for (Map.Entry<String, Long> entry : registry.agiStrataCounts().entrySet()) {
            String stratumId = "agi-subsystem:" + sanitizeId(entry.getKey());
            nodes.put(new JSONObject()
                .put("id", stratumId)
                .put("type", "agi_stratum")
                .put("label", entry.getKey())
                .put("scope", "subsystem")
                .put("count", entry.getValue())
                .put("visualColor", agiStratumColor(entry.getKey()))
                .put("visualAccent", agiStratumAccent(entry.getKey())));
            edges.put(edge("root:ultimate", stratumId, "classifies"));
        }

        for (Map.Entry<String, Integer> entry : operatorManifest.surfaceAgiStrataCounts().entrySet()) {
            String stratumId = "agi-surface:" + sanitizeId(entry.getKey());
            nodes.put(new JSONObject()
                .put("id", stratumId)
                .put("type", "agi_stratum")
                .put("label", entry.getKey())
                .put("scope", "surface")
                .put("count", entry.getValue())
                .put("visualColor", agiStratumColor(entry.getKey()))
                .put("visualAccent", agiStratumAccent(entry.getKey())));
            edges.put(edge("root:ultimate", stratumId, "classifies"));
        }

        for (Map.Entry<String, Integer> entry : operatorManifest.sectionAgiStrataCounts().entrySet()) {
            String stratumId = "agi-section:" + sanitizeId(entry.getKey());
            nodes.put(new JSONObject()
                .put("id", stratumId)
                .put("type", "agi_stratum")
                .put("label", entry.getKey())
                .put("scope", "section")
                .put("count", entry.getValue())
                .put("visualColor", agiStratumColor(entry.getKey()))
                .put("visualAccent", agiStratumAccent(entry.getKey())));
            edges.put(edge("root:ultimate", stratumId, "classifies"));
        }

        Map<String, String> moduleNodeIds = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> module : sorted(index.moduleCounts()).toList()) {
            String moduleId = "module:" + sanitizeId(module.getKey());
            moduleNodeIds.put(module.getKey(), moduleId);
            nodes.put(new JSONObject()
                .put("id", moduleId)
                .put("type", "module")
                .put("label", module.getKey())
                .put("fileCount", module.getValue())
                .put("visualGroup", "knowledge-substrate")
                .put("visualColor", "#a78bfa"));
            edges.put(edge("root:ultimate", moduleId, "indexes"));
        }

        for (UltimateSubsystem subsystem : registry.all()) {
            String subsystemId = "subsystem:" + subsystem.id();
            String familyId = "family:" + subsystem.family();
            String moduleName = moduleNameFor(subsystem);
            String primaryStratum = primaryAgiStratum(subsystem.agiStrata());
            SubsystemNavigation navigation = subsystemNavigation(subsystem);
            addClusterNode(subsystemClusters, primaryStratum, subsystemId);

            nodes.put(new JSONObject()
                .put("id", subsystemId)
                .put("type", "subsystem")
                .put("label", subsystem.displayName())
                .put("family", subsystem.family())
                .put("summary", subsystem.summary())
                .put("path", subsystem.primaryPath() == null ? "" : index.relativePath(subsystem.primaryPath()))
                .put("launch", subsystem.runnable() ? subsystem.launchCommandText() : "")
                .put("subsystemId", subsystem.id())
                .put("jumpUrl", navigation.jumpUrl())
                .put("systemsUrl", navigation.systemsUrl())
                .put("runtimeUrl", navigation.runtimeUrl())
                .put("routeUrl", navigation.routeUrl())
                .put("agiStrata", new JSONArray(subsystem.agiStrata()))
                .put("primaryAgiStratum", primaryStratum)
                .put("visualGroup", "subsystem:" + primaryStratum)
                .put("visualColor", agiStratumColor(primaryStratum)));

            edges.put(edge(familyId, subsystemId, "hosts"));
            if (moduleName != null && moduleNodeIds.containsKey(moduleName)) {
                edges.put(edge(subsystemId, moduleNodeIds.get(moduleName), "implemented_in"));
            }
            for (String stratum : subsystem.agiStrata()) {
                edges.put(edge(subsystemId, "agi-subsystem:" + sanitizeId(stratum), "classified_as"));
            }
        }

        for (UltimateCommandCatalog commandCatalog : commandCatalogs) {
            UltimateOperatorManifest.SurfaceManifest surfaceManifest = operatorManifest.findSurface(commandCatalog.surfaceId()).orElse(null);
            String surfaceId = "command-surface:" + sanitizeId(commandCatalog.surfaceId());
            String surfacePrimaryStratum = primaryAgiStratum(surfaceManifest == null ? List.of() : surfaceManifest.agiStrata());
            addClusterNode(surfaceClusters, surfacePrimaryStratum, surfaceId);
            nodes.put(new JSONObject()
                .put("id", surfaceId)
                .put("type", "command_surface")
                .put("label", commandCatalog.surfaceTitle())
                .put("description", commandCatalog.surfaceDescription())
                .put("primarySubsystem", commandCatalog.primarySubsystemId())
                .put("surfaceId", commandCatalog.surfaceId())
                .put("jumpUrl", "/surfaces?surface=" + encodeQueryValue(commandCatalog.surfaceId()))
                .put("sectionCount", commandCatalog.sectionCount())
                .put("commandCount", commandCatalog.commandCount())
                .put("agiStrata", new JSONArray(surfaceManifest == null ? List.of() : surfaceManifest.agiStrata()))
                .put("primaryAgiStratum", surfacePrimaryStratum)
                .put("visualGroup", "surface:" + surfacePrimaryStratum)
                .put("visualColor", agiStratumColor(surfacePrimaryStratum)));
            edges.put(edge("subsystem:" + commandCatalog.primarySubsystemId(), surfaceId, "exposes"));
            if (surfaceManifest != null) {
                for (String stratum : surfaceManifest.agiStrata()) {
                    edges.put(edge(surfaceId, "agi-surface:" + sanitizeId(stratum), "classified_as"));
                }
            }

            for (UltimateCommandCatalog.CommandSection section : commandCatalog.sections()) {
                UltimateOperatorManifest.SectionManifest sectionManifest = surfaceManifest == null
                    ? null
                    : surfaceManifest.findSection(section.id()).orElse(null);
                String sectionId = "command-section:" + sanitizeId(commandCatalog.surfaceId() + "_" + section.id());
                String sectionPrimaryStratum = primaryAgiStratum(sectionManifest == null ? List.of() : sectionManifest.agiStrata());
                String routeCommand = "route " + commandCatalog.surfaceId() + " " + section.id();
                String inspectCommand = "commands " + commandCatalog.surfaceId() + " " + section.id();
                addClusterNode(sectionClusters, sectionPrimaryStratum, sectionId);
                nodes.put(new JSONObject()
                    .put("id", sectionId)
                    .put("type", "command_section")
                    .put("surface", commandCatalog.surfaceId())
                    .put("surfaceId", commandCatalog.surfaceId())
                    .put("sectionId", section.id())
                    .put("label", section.title())
                    .put("theme", section.theme())
                    .put("count", section.commandCount())
                    .put("routeCommand", routeCommand)
                    .put("inspectCommand", inspectCommand)
                    .put("jumpUrl", "/surfaces?surface=" + encodeQueryValue(commandCatalog.surfaceId()) + "&section=" + encodeQueryValue(section.id()))
                    .put("routeUrl", "/launchpad?cmd=" + encodeQueryValue(routeCommand))
                    .put("agiStrata", new JSONArray(sectionManifest == null ? List.of() : sectionManifest.agiStrata()))
                    .put("primaryAgiStratum", sectionPrimaryStratum)
                    .put("visualGroup", "section:" + sectionPrimaryStratum)
                    .put("visualColor", agiStratumColor(sectionPrimaryStratum)));
                edges.put(edge(surfaceId, sectionId, "groups"));
                if (sectionManifest != null) {
                    for (String stratum : sectionManifest.agiStrata()) {
                        edges.put(edge(sectionId, "agi-section:" + sanitizeId(stratum), "classified_as"));
                    }
                }

                for (String subsystemLink : commandCatalog.linkedSubsystemIds(section)) {
                    edges.put(edge(sectionId, "subsystem:" + subsystemLink, "routes_to"));
                }
                commandCatalog.defaultSubsystemId(section)
                    .ifPresent(defaultId -> edges.put(edge(sectionId, "subsystem:" + defaultId, "defaults_to")));

                for (UltimateCommandCatalog.CommandEntry entry : section.commands().stream().limit(4).toList()) {
                    String commandId = "command:" + sanitizeId(commandCatalog.surfaceId() + "_" + section.id() + "_" + entry.syntax());
                    nodes.put(new JSONObject()
                        .put("id", commandId)
                        .put("type", "command")
                        .put("label", entry.syntax())
                        .put("description", entry.description()));
                    edges.put(edge(sectionId, commandId, "contains"));
                }
            }
        }

        for (Map.Entry<String, Integer> signal : sorted(index.signalCounts()).limit(12).toList()) {
            String signalId = "signal:" + signal.getKey();
            nodes.put(new JSONObject()
                .put("id", signalId)
                .put("type", "signal")
                .put("label", signal.getKey())
                .put("count", signal.getValue())
                .put("visualGroup", "signal")
                .put("visualColor", "#f59e0b"));
            edges.put(edge("root:ultimate", signalId, "detects"));
        }

        Map<String, List<String>> moduleSignals = moduleSignalMatches(index);
        for (IndexedDocument document : index.readmes().stream().limit(24).toList()) {
            String docId = "doc:" + sanitizeId(index.relativePath(document.path()));
            nodes.put(new JSONObject()
                .put("id", docId)
                .put("type", "document")
                .put("label", document.title())
                .put("module", document.module())
                .put("path", index.relativePath(document.path()))
                .put("extension", document.extension())
                .put("visualGroup", "document")
                .put("visualColor", "#22c55e"));

            String moduleId = moduleNodeIds.get(document.module());
            if (moduleId != null) {
                edges.put(edge(moduleId, docId, "documents"));
            }
        }

        for (Map.Entry<String, List<String>> entry : moduleSignals.entrySet()) {
            String moduleId = moduleNodeIds.get(entry.getKey());
            if (moduleId == null) {
                continue;
            }
            for (String signal : entry.getValue()) {
                edges.put(edge(moduleId, "signal:" + signal, "emphasizes"));
            }
        }

        return new JSONObject()
            .put("identity", new JSONObject()
                .put("productName", EXPORT_TITLE)
                .put("tagline", EXPORT_TAGLINE))
            .put("generatedAt", OffsetDateTime.now().toString())
            .put("repoRoot", repoRoot.toString().replace('\\', '/'))
            .put("outputDir", outputDir.toString().replace('\\', '/'))
            .put("summary", new JSONObject()
                .put("subsystems", registry.all().size())
                .put("families", registry.familyCounts().size())
                .put("subsystemAgiStrata", registry.agiStrataCounts().size())
                .put("documents", index.documentCount())
                .put("readmes", index.readmeCount())
                .put("modules", index.moduleCount())
                .put("commandSurfaces", commandCatalogs.size())
                .put("commandSections", totalSectionCount(commandCatalogs))
                .put("cataloguedCommands", totalCommandCount(commandCatalogs))
                .put("surfaceAgiStrata", operatorManifest.surfaceAgiStrataCounts().size())
                .put("sectionAgiStrata", operatorManifest.sectionAgiStrataCounts().size()))
            .put("visualSemantics", buildVisualSemantics(registry, operatorManifest, subsystemClusters, surfaceClusters, sectionClusters))
            .put("nodes", nodes)
            .put("edges", edges);
    }

    private String buildMermaid(
        UltimateSubsystemRegistry registry,
        UltimateKnowledgeIndex index,
        List<? extends UltimateCommandCatalog> commandCatalogs,
        UltimateOperatorManifest operatorManifest
    ) {
        List<String> activeStrata = activeAgiStrata(registry, operatorManifest);
        StringBuilder sb = new StringBuilder();
        sb.append("flowchart LR\n");
        sb.append("  root[\"").append(EXPORT_TITLE).append("\"]\n");
        sb.append("  strata_hub[\"AGI Strata Lanes\"]\n");
        sb.append("  subsystem_hub[\"Subsystem Lanes\"]\n");
        sb.append("  surface_hub[\"Command Surfaces\"]\n");
        sb.append("  section_hub[\"Operator Sections\"]\n");
        sb.append("  knowledge_hub[\"Knowledge Substrate\"]\n");
        sb.append("  root --> strata_hub\n");
        sb.append("  root --> subsystem_hub\n");
        sb.append("  root --> surface_hub\n");
        sb.append("  root --> section_hub\n");
        sb.append("  root --> knowledge_hub\n");

        sb.append("  subgraph agi_strata_lanes[\"AGI Strata Lanes\"]\n");
        for (String stratum : activeStrata) {
            String laneId = sanitizeId("agi_lane_" + stratum);
            sb.append("    ").append(laneId)
                .append("[\"").append(mermaidLabel(agiStratumLabel(stratum))).append("\"]\n");
        }
        sb.append("  end\n");
        for (String stratum : activeStrata) {
            String laneId = sanitizeId("agi_lane_" + stratum);
            sb.append("  strata_hub --> ").append(laneId).append("\n");
        }

        for (Map.Entry<String, Long> family : registry.familyCounts().entrySet()) {
            String familyId = sanitizeId("family_" + family.getKey());
            sb.append("  ").append(familyId)
                .append("[\"").append(mermaidLabel(family.getKey())).append(" (").append(family.getValue()).append(")\"]\n");
            sb.append("  subsystem_hub --> ").append(familyId).append("\n");

            for (UltimateSubsystem subsystem : registry.all().stream()
                .filter(item -> item.family().equals(family.getKey()))
                .toList()) {
                String subsystemId = sanitizeId("subsystem_" + subsystem.id());
                sb.append("  ").append(subsystemId)
                    .append("[\"").append(mermaidLabel(subsystem.displayName())).append("\"]\n");
                sb.append("  ").append(familyId).append(" --> ").append(subsystemId).append("\n");
                sb.append("  ")
                    .append(sanitizeId("agi_lane_" + primaryAgiStratum(subsystem.agiStrata())))
                    .append(" -.-> ")
                    .append(subsystemId)
                    .append("\n");
            }
        }

        for (Map.Entry<String, Integer> module : topEntries(index.moduleCounts(), 6)) {
            String moduleId = sanitizeId("module_" + module.getKey());
            sb.append("  ").append(moduleId)
                .append("[\"").append(mermaidLabel(module.getKey())).append(" (").append(module.getValue()).append(" files)\"]\n");
            sb.append("  knowledge_hub --> ").append(moduleId).append("\n");
        }

        for (UltimateCommandCatalog commandCatalog : commandCatalogs) {
            UltimateOperatorManifest.SurfaceManifest surfaceManifest = operatorManifest.findSurface(commandCatalog.surfaceId()).orElse(null);
            String subsystemId = sanitizeId("subsystem_" + commandCatalog.primarySubsystemId());
            String surfaceId = sanitizeId("command_surface_" + commandCatalog.surfaceId());
            sb.append("  ").append(surfaceId)
                .append("[\"").append(mermaidLabel(commandCatalog.surfaceTitle())).append(" (")
                .append(commandCatalog.sectionCount()).append(" sections)\"]\n");
            sb.append("  surface_hub --> ").append(surfaceId).append("\n");
            sb.append("  ").append(subsystemId).append(" --> ").append(surfaceId).append("\n");
            if (surfaceManifest != null) {
                sb.append("  ")
                    .append(sanitizeId("agi_lane_" + primaryAgiStratum(surfaceManifest.agiStrata())))
                    .append(" -.-> ")
                    .append(surfaceId)
                    .append("\n");
            }

            for (UltimateCommandCatalog.CommandSection section : commandCatalog.sections().stream().limit(3).toList()) {
                UltimateOperatorManifest.SectionManifest sectionManifest = surfaceManifest == null
                    ? null
                    : surfaceManifest.findSection(section.id()).orElse(null);
                String sectionId = sanitizeId("command_section_" + commandCatalog.surfaceId() + "_" + section.id());
                sb.append("  ").append(sectionId)
                    .append("[\"").append(mermaidLabel(section.title())).append(" (").append(section.commandCount()).append(" cmds)\"]\n");
                sb.append("  section_hub --> ").append(sectionId).append("\n");
                sb.append("  ").append(surfaceId).append(" --> ").append(sectionId).append("\n");
                if (sectionManifest != null) {
                    sb.append("  ")
                        .append(sanitizeId("agi_lane_" + primaryAgiStratum(sectionManifest.agiStrata())))
                        .append(" -.-> ")
                        .append(sectionId)
                        .append("\n");
                }
            }
        }
        sb.append("  classDef hub_node fill:#0f172a,stroke:#38bdf8,color:#f8fafc,stroke-width:2px;\n");
        sb.append("  classDef family_node fill:#1e293b,stroke:#94a3b8,color:#f8fafc;\n");
        sb.append("  classDef subsystem_node fill:#111827,stroke:#60a5fa,color:#f8fafc;\n");
        sb.append("  classDef surface_node fill:#1f2937,stroke:#34d399,color:#f8fafc;\n");
        sb.append("  classDef section_node fill:#111827,stroke:#f59e0b,color:#f8fafc;\n");
        sb.append("  classDef module_node fill:#0b1220,stroke:#a78bfa,color:#f8fafc;\n");
        sb.append("  class root,strata_hub,subsystem_hub,surface_hub,section_hub,knowledge_hub hub_node;\n");
        for (String stratum : activeStrata) {
            String classId = sanitizeId("strata_" + stratum);
            sb.append("  classDef ").append(classId)
                .append(" fill:").append(agiStratumColor(stratum))
                .append(",stroke:").append(agiStratumAccent(stratum))
                .append(",color:#08111f,stroke-width:2px;\n");
            sb.append("  class ").append(sanitizeId("agi_lane_" + stratum)).append(" ").append(classId).append(";\n");
        }
        for (Map.Entry<String, Long> family : registry.familyCounts().entrySet()) {
            sb.append("  class ").append(sanitizeId("family_" + family.getKey())).append(" family_node;\n");
        }
        for (UltimateSubsystem subsystem : registry.all()) {
            sb.append("  class ").append(sanitizeId("subsystem_" + subsystem.id())).append(" subsystem_node;\n");
        }
        for (UltimateCommandCatalog commandCatalog : commandCatalogs) {
            sb.append("  class ").append(sanitizeId("command_surface_" + commandCatalog.surfaceId())).append(" surface_node;\n");
            for (UltimateCommandCatalog.CommandSection section : commandCatalog.sections().stream().limit(3).toList()) {
                sb.append("  class ")
                    .append(sanitizeId("command_section_" + commandCatalog.surfaceId() + "_" + section.id()))
                    .append(" section_node;\n");
            }
        }
        for (Map.Entry<String, Integer> module : topEntries(index.moduleCounts(), 6)) {
            sb.append("  class ").append(sanitizeId("module_" + module.getKey())).append(" module_node;\n");
        }
        return sb.toString().trim();
    }

    private JSONObject buildVisualSemantics(
        UltimateSubsystemRegistry registry,
        UltimateOperatorManifest operatorManifest,
        Map<String, List<String>> subsystemClusters,
        Map<String, List<String>> surfaceClusters,
        Map<String, List<String>> sectionClusters
    ) {
        JSONArray legend = new JSONArray();
        JSONArray clusters = new JSONArray();
        for (String stratum : activeAgiStrata(registry, operatorManifest)) {
            legend.put(new JSONObject()
                .put("stratum", stratum)
                .put("label", agiStratumLabel(stratum))
                .put("color", agiStratumColor(stratum))
                .put("accent", agiStratumAccent(stratum)));
            addCluster(clusters, "subsystem", stratum, subsystemClusters.get(stratum));
            addCluster(clusters, "surface", stratum, surfaceClusters.get(stratum));
            addCluster(clusters, "section", stratum, sectionClusters.get(stratum));
        }
        return new JSONObject()
            .put("layout", "agi_strata_first")
            .put("mermaidDirection", "LR")
            .put("grouping", new JSONArray(List.of("scope", "primaryAgiStratum")))
            .put("legend", legend)
            .put("clusters", clusters);
    }

    private int totalSectionCount(List<? extends UltimateCommandCatalog> commandCatalogs) {
        return commandCatalogs.stream().mapToInt(UltimateCommandCatalog::sectionCount).sum();
    }

    private int totalCommandCount(List<? extends UltimateCommandCatalog> commandCatalogs) {
        return commandCatalogs.stream().mapToInt(UltimateCommandCatalog::commandCount).sum();
    }

    private Map<String, List<String>> moduleSignalMatches(UltimateKnowledgeIndex index) {
        Map<String, Map<String, Integer>> scores = new LinkedHashMap<>();
        List<String> knownSignals = sorted(index.signalCounts()).map(Map.Entry::getKey).toList();

        for (IndexedDocument document : index.documents()) {
            String text = (document.title() + "\n" + document.preview() + "\n" + index.relativePath(document.path()))
                .toLowerCase(Locale.ROOT);
            Map<String, Integer> moduleScores = scores.computeIfAbsent(document.module(), ignored -> new LinkedHashMap<>());
            for (String signal : knownSignals) {
                if (text.contains(signal)) {
                    moduleScores.merge(signal, 1, Integer::sum);
                }
            }
        }

        LinkedHashMap<String, List<String>> result = new LinkedHashMap<>();
        for (String module : topEntries(index.moduleCounts(), 12).stream().map(Map.Entry::getKey).toList()) {
            Map<String, Integer> moduleScores = scores.getOrDefault(module, Map.of());
            List<String> topSignals = moduleScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                    .thenComparing(Map.Entry.comparingByKey()))
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();
            if (!topSignals.isEmpty()) {
                result.put(module, topSignals);
            }
        }
        return result;
    }

    private String moduleNameFor(UltimateSubsystem subsystem) {
        Path source = Objects.requireNonNullElse(subsystem.primaryPath(), subsystem.workingDirectory());
        if (source == null) {
            return null;
        }
        try {
            Path relative = repoRoot.relativize(source.toAbsolutePath().normalize());
            return relative.getNameCount() > 0 ? relative.getName(0).toString() : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private List<Map.Entry<String, Integer>> topEntries(Map<String, Integer> values, int limit) {
        return sorted(values).limit(limit).toList();
    }

    private java.util.stream.Stream<Map.Entry<String, Integer>> sorted(Map<String, Integer> values) {
        return values.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey()));
    }

    private JSONObject edge(String from, String to, String type) {
        return new JSONObject()
            .put("from", from)
            .put("to", to)
            .put("type", type);
    }

    private void addCluster(JSONArray clusters, String scope, String stratum, List<String> nodeIds) {
        if (nodeIds == null || nodeIds.isEmpty()) {
            return;
        }
        clusters.put(new JSONObject()
            .put("id", "cluster:" + scope + ":" + sanitizeId(stratum))
            .put("scope", scope)
            .put("stratum", stratum)
            .put("label", scopeLabel(scope) + " · " + agiStratumLabel(stratum))
            .put("renderAs", "lane")
            .put("color", agiStratumColor(stratum))
            .put("accent", agiStratumAccent(stratum))
            .put("nodeIds", new JSONArray(nodeIds)));
    }

    private void addClusterNode(Map<String, List<String>> clusters, String stratum, String nodeId) {
        if (stratum == null || stratum.isBlank()) {
            return;
        }
        clusters.computeIfAbsent(stratum, ignored -> new ArrayList<>()).add(nodeId);
    }

    private List<String> activeAgiStrata(UltimateSubsystemRegistry registry, UltimateOperatorManifest operatorManifest) {
        Map<String, Integer> combined = combinedAgiCounts(registry, operatorManifest);
        List<String> ordered = new ArrayList<>();
        for (String stratum : AGI_STRATA_ORDER) {
            if (combined.containsKey(stratum)) {
                ordered.add(stratum);
            }
        }
        for (String stratum : combined.keySet()) {
            if (!ordered.contains(stratum)) {
                ordered.add(stratum);
            }
        }
        return ordered;
    }

    private Map<String, Integer> combinedAgiCounts(UltimateSubsystemRegistry registry, UltimateOperatorManifest operatorManifest) {
        Map<String, Integer> combined = new LinkedHashMap<>();
        mergeAgiCounts(combined, registry.agiStrataCounts());
        mergeAgiCounts(combined, operatorManifest.surfaceAgiStrataCounts());
        mergeAgiCounts(combined, operatorManifest.sectionAgiStrataCounts());
        return combined;
    }

    private void mergeAgiCounts(Map<String, Integer> target, Map<String, ? extends Number> counts) {
        for (Map.Entry<String, ? extends Number> entry : counts.entrySet()) {
            target.merge(entry.getKey(), entry.getValue().intValue(), Integer::sum);
        }
    }

    private String primaryAgiStratum(List<String> strata) {
        if (strata == null || strata.isEmpty()) {
            return "unclassified";
        }
        for (String candidate : AGI_STRATA_ORDER) {
            if (strata.contains(candidate)) {
                return candidate;
            }
        }
        return strata.get(0);
    }

    private String agiStratumLabel(String stratum) {
        return switch (stratum) {
            case "governing-core" -> "Governing Core";
            case "cognition-core" -> "Cognition Core";
            case "organism-core" -> "Organism Core";
            case "absorption-core" -> "Absorption Core";
            case "construction-lane" -> "Construction Lane";
            case "proof-lane" -> "Proof Lane";
            case "model-substrate-lane" -> "Model Substrate Lane";
            case "bare-metal-substrate-lane" -> "Bare-Metal Substrate Lane";
            case "kernel-discipline" -> "Kernel Discipline";
            case "adjacent-historical-core" -> "Adjacent Historical Core";
            case "operator-shell" -> "Operator Shell";
            case "unclassified" -> "Unclassified";
            default -> Arrays.stream(stratum.split("-"))
                .filter(part -> !part.isBlank())
                .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
                .collect(Collectors.joining(" "));
        };
    }

    private String agiStratumColor(String stratum) {
        return switch (stratum) {
            case "governing-core" -> "#f6d365";
            case "cognition-core" -> "#7dd3fc";
            case "organism-core" -> "#86efac";
            case "absorption-core" -> "#c4b5fd";
            case "construction-lane" -> "#fdba74";
            case "proof-lane" -> "#fca5a5";
            case "model-substrate-lane" -> "#93c5fd";
            case "bare-metal-substrate-lane" -> "#a3a3a3";
            case "kernel-discipline" -> "#67e8f9";
            case "adjacent-historical-core" -> "#f9a8d4";
            case "operator-shell" -> "#d8b4fe";
            default -> "#94a3b8";
        };
    }

    private String agiStratumAccent(String stratum) {
        return switch (stratum) {
            case "governing-core" -> "#b7791f";
            case "cognition-core" -> "#0369a1";
            case "organism-core" -> "#15803d";
            case "absorption-core" -> "#7c3aed";
            case "construction-lane" -> "#c2410c";
            case "proof-lane" -> "#b91c1c";
            case "model-substrate-lane" -> "#1d4ed8";
            case "bare-metal-substrate-lane" -> "#525252";
            case "kernel-discipline" -> "#0f766e";
            case "adjacent-historical-core" -> "#be185d";
            case "operator-shell" -> "#7e22ce";
            default -> "#475569";
        };
    }

    private String mermaidLabel(String value) {
        return value.replace("\"", "'");
    }

    private String scopeLabel(String scope) {
        return switch (scope) {
            case "subsystem" -> "Subsystems";
            case "surface" -> "Command Surfaces";
            case "section" -> "Operator Sections";
            default -> scope;
        };
    }

    private String encodeQueryValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private SubsystemNavigation subsystemNavigation(UltimateSubsystem subsystem) {
        String systemsUrl = "/systems?focus=" + encodeQueryValue(subsystem.id()) + "&detail=inspect";
        String routeUrl = "/launchpad?cmd=" + encodeQueryValue("system " + subsystem.id());
        String runtimeAction = subsystem.organismCore()
            ? "nexus awaken"
            : subsystem.constructionLane() ? "miving gen 24" : "native status";
        String runtimePanel = (subsystem.organismCore() || subsystem.constructionLane()) ? "actions" : "status";
        String runtimeUrl = "/runtime?subsystem=" + encodeQueryValue(subsystem.id())
            + "&panel=" + encodeQueryValue(runtimePanel)
            + "&action=" + encodeQueryValue(runtimeAction);
        String jumpUrl = (subsystem.kernelDiscipline()
            || subsystem.proofLane()
            || subsystem.organismCore()
            || subsystem.modelSubstrateLane()
            || subsystem.constructionLane())
            ? runtimeUrl
            : systemsUrl;
        return new SubsystemNavigation(jumpUrl, systemsUrl, runtimeUrl, routeUrl);
    }

    private String escapePipes(String value) {
        return value.replace("|", "\\|");
    }

    private String sanitizeId(String value) {
        return value.replaceAll("[^A-Za-z0-9_]", "_");
    }

    public record ExportArtifact(
        Path outputDir,
        Path markdownPath,
        Path graphPath,
        Path mathPath,
        Path axiomLedgerPath,
        Path truthRegistryPath,
        Path continuityMemoryPath,
        int subsystemCount,
        int documentCount,
        String generatedAt
    ) {
        public String summary() {
            return String.format(
                Locale.ROOT,
                "Exported Fraynix operator record, knowledge graph, math canon, axiom ledger, truth registry, and continuity memory with %d subsystems and %d indexed documents to %s",
                subsystemCount,
                documentCount,
                outputDir.toString().replace('\\', '/')
            );
        }
    }

    private record AxiomLedgerEntry(
        String id,
        String title,
        String status,
        String summary,
        List<String> anchors,
        String nextStep
    ) {
    }

    private record SubsystemNavigation(
        String jumpUrl,
        String systemsUrl,
        String runtimeUrl,
        String routeUrl
    ) {
    }
}
