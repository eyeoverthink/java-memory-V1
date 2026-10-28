package fraymus.brain.unified;

import fraymus.knowledge.AkashicRecord;
import fraymus.neural.OpenClawAgent;
import fraymus.brain.creative.dionysian.DionysianCortex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class HumorCreativeBrain extends AbstractBrainAdapter {
    private static final String HUMOR_TASTE_CATEGORY = "HUMOR_TASTE";
    private static final String HUMOR_TRACE_CATEGORY = "HUMOR_CREATIVE_TRACE";
    private static final List<String> ABSORBED_SOURCE_CATEGORIES = Arrays.asList(
        "WEBCORTEX",
        "OPENCLAW_ACTION",
        "OPENCLAW_REPAIR",
        "SCIENCE_HYPOTHESIS",
        "GENERATED_CODE"
    );

    private final AtomicBoolean initialized;
    private final List<String> recipeLedger;
    private final Map<String, Double> recipeWeights;
    private volatile AkashicRecord akashic;
    private volatile OpenClawAgent openClawAgent;
    private volatile DionysianCortex dionysianCortex;
    private int totalRemixes;
    private double lastSelectionScore;

    public HumorCreativeBrain() {
        super("HumorCreative", "CreativeCortex", Arrays.asList(
            BrainCapability.CREATIVITY,
            BrainCapability.LANGUAGE,
            BrainCapability.EMOTION,
            BrainCapability.MEMORY
        ));
        this.initialized = new AtomicBoolean(false);
        this.recipeLedger = new ArrayList<>();
        this.recipeWeights = new HashMap<>();
        this.totalRemixes = 0;
        this.lastSelectionScore = 0.0;
    }

    public void wireAkashic(AkashicRecord akashic) {
        this.akashic = akashic;
    }

    public void wireAkashicRecord(AkashicRecord akashic) {
        wireAkashic(akashic);
    }

    public void wireOpenClaw(OpenClawAgent openClawAgent) {
        this.openClawAgent = openClawAgent;
    }

    public void wireDionysianCortex(DionysianCortex dionysianCortex) {
        this.dionysianCortex = dionysianCortex;
    }

    public DionysianCortex getDionysianCortex() {
        return dionysianCortex;
    }

    @Override
    protected void onWire() {
        if (initialized.compareAndSet(false, true)) {
            recipeLedger.clear();
            recipeLedger.addAll(Arrays.asList(
                "benign_violation",
                "status_flip",
                "specificity_spike",
                "callback_loop",
                "literalize_metaphor",
                "third_beat_break",
                "mass_relatability_filter",
                "crowd_anchor",
                "prestige_remix",
                "taste_probe",
                "cognitive_recipe",
                "absorbed_remix",
                "source_transcode",
                "worldfeed_loop",
                "dionysian_blend",
                "dionysian_punchline"
            ));
            recipeWeights.clear();
            recipeWeights.put("benign_violation", 0.14);
            recipeWeights.put("status_flip", 0.14);
            recipeWeights.put("third_beat_break", 0.13);
            recipeWeights.put("specificity_spike", 0.12);
            recipeWeights.put("literalize_metaphor", 0.12);
            recipeWeights.put("mass_relatability_filter", 0.11);
            recipeWeights.put("crowd_anchor", 0.11);
            recipeWeights.put("prestige_remix", 0.10);
            recipeWeights.put("taste_probe", 0.10);
            recipeWeights.put("cognitive_recipe", 0.10);
            recipeWeights.put("absorbed_remix", 0.13);
            recipeWeights.put("source_transcode", 0.12);
            recipeWeights.put("worldfeed_loop", 0.11);
            recipeWeights.put("dionysian_blend", 0.14);
            recipeWeights.put("dionysian_punchline", 0.14);
            updateConsciousness(0.781);
        }
    }

    @Override
    protected void onUnwire() {
        initialized.set(false);
    }

    @Override
    public BrainResponse process(BrainQuery query) {
        if (!initialized.get()) {
            return BrainResponse.error(getName(), "HumorCreative brain not wired");
        }

        long started = System.currentTimeMillis();
        try {
            String seed = normalizeSeed(query == null ? "" : query.getQuery());
            Map<String, Object> context = query == null || query.getContext() == null ? Map.of() : query.getContext();
            String taste = extractTasteSignal(context);
            applyTasteFeedback(context);

            CreativeSourceDeck sourceDeck = collectSourceDeck(seed, context);
            CreativeFrame frame = buildFrame(seed, taste, context, sourceDeck);
            List<CreativeCandidate> candidates = generateCandidates(frame, sourceDeck);
            CreativeCandidate selected = candidates.stream()
                .max(Comparator.comparingDouble(CreativeCandidate::score))
                .orElse(new CreativeCandidate(
                    "fallback",
                    "The punchline ran away, which is rude because it was literally its job.",
                    0.5,
                    "survival_default",
                    Map.of(
                        "crowd", 0.25,
                        "surprise", 0.25,
                        "specificity", 0.20,
                        "snap", 0.20,
                        "taste", 0.10,
                        "source", 0.0
                    )
                ));

            totalRemixes += candidates.size();
            lastSelectionScore = selected.score();
            double confidence = Math.max(0.62, Math.min(0.96, selected.score()));
            double consciousness = Math.max(0.7567, Math.min(0.92, 0.72 + selected.score() * 0.18));

            boolean tasteMemoryPersisted = persistTasteFeedback(frame, context, selected, sourceDeck);
            boolean tracePersisted = persistCreativeTrace(frame, selected, candidates, sourceDeck);

            String response = renderResponse(frame, selected, candidates, sourceDeck);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("creativeMode", "humor_remix_selection");
            metadata.put("selectedRecipe", selected.recipe());
            metadata.put("selectionScore", round3(selected.score()));
            metadata.put("candidateCount", candidates.size());
            metadata.put("totalRemixes", totalRemixes);
            metadata.put("tasteSignal", taste);
            metadata.put("recipes", new ArrayList<>(recipeLedger));
            metadata.put("crowdAnchor", frame.crowdAnchor());
            metadata.put("selectionSignals", selected.signals());
            metadata.put("sourceEvidenceCount", sourceDeck.sparks().size());
            metadata.put("sourceCategories", sourceDeck.categories());
            metadata.put("absorbedSourceSparks", sourceDeck.sparks());
            metadata.put("openClawFed", sourceDeck.openClawFed());
            metadata.put("dionysianWired", dionysianCortex != null);
            metadata.put("tasteMemoryPersisted", tasteMemoryPersisted);
            metadata.put("creativeTracePersisted", tracePersisted);
            metadata.put("frame", Map.of(
                "subject", frame.subject(),
                "crowdAnchor", frame.crowdAnchor(),
                "tone", frame.tone(),
                "tasteSignal", frame.taste()
            ));
            metadata.put("requiresExternalModel", false);
            metadata.put("learningHook", "Store user laugh/skip/edit feedback as recipe weights, crowd anchors, source sparks, and tone biases for future remix selection.");

            long elapsed = System.currentTimeMillis() - started;
            recordSuccess(elapsed, confidence, consciousness);
            return BrainResponse.success(getName(), response, confidence, consciousness, metadata, elapsed);
        } catch (Exception e) {
            recordFailure();
            return BrainResponse.error(getName(), "Humor synthesis failed: " + e.getMessage());
        }
    }

    public double getLastSelectionScore() {
        return lastSelectionScore;
    }

    public int getTotalRemixes() {
        return totalRemixes;
    }

    private List<CreativeCandidate> generateCandidates(CreativeFrame frame, CreativeSourceDeck sourceDeck) {
        List<CreativeCandidate> candidates = new ArrayList<>();
        String subject = frame.subject();
        String crowdAnchor = frame.crowdAnchor();

        candidates.add(candidate("status_flip", frame,
            "Everyone wants " + subject + " until " + crowdAnchor + " asks for a patch note and a personality."));
        candidates.add(candidate("specificity_spike", frame,
            "This is not a creative block; it is a hostile board meeting between your standards and a raccoon with presentation privileges."));
        candidates.add(candidate("literalize_metaphor", frame,
            "We said absorb the world, and the brain dragged in the comment section like it was premium lumber."));
        candidates.add(candidate("third_beat_break", frame,
            "Setup, tension, prestige remix: the punchline shows up dressed like a system log that learned eye contact."));
        candidates.add(candidate("callback_loop", frame,
            "The masses like the hook. The leaders distort the hook. Fraynix should steal the rhythm, then crack it open on purpose."));
        candidates.add(candidate("benign_violation", frame,
            "A joke is a safe crash: the mind hits the rail, survives, and comes back holding a brighter idea."));
        candidates.add(candidate("mass_relatability_filter", frame,
            "If it cannot survive the " + crowdAnchor + " test, it is not a remix yet; it is just a clever diary entry."));
        candidates.add(candidate("crowd_anchor", frame,
            "Make it feel like " + crowdAnchor + " met a black-budget art director and came home with better timing."));
        candidates.add(candidate("prestige_remix", frame,
            "Leaders in creativity do not ask permission; they smuggle " + subject + " through a familiar doorway and let the room realize late."));
        candidates.add(candidate("taste_probe", frame, buildTasteProbe(frame)));
        candidates.add(candidate("cognitive_recipe", frame, buildRecipe(frame)));
        appendDionysianCandidates(candidates, frame, subject, crowdAnchor);

        if (!sourceDeck.sparks().isEmpty()) {
            String primarySpark = sourceDeck.sparks().get(0);
            candidates.add(candidate("absorbed_remix", frame,
                "Absorb \"" + primarySpark + "\" from the world feed, bend it through " + crowdAnchor + ", then let the punchline arrive like a familiar signal with outlaw timing."));
            candidates.add(candidate("source_transcode", frame,
                "Take the live source spark \"" + primarySpark + "\" and remaster it until " + crowdAnchor + " thinks it was always theirs, just sharper."));
            if (sourceDeck.sparks().size() > 1) {
                String secondarySpark = sourceDeck.sparks().get(1);
                candidates.add(candidate("worldfeed_loop", frame,
                    "World feed recipe: fuse \"" + primarySpark + "\" with \"" + secondarySpark + "\", keep one clean handle for the masses, then hide the strange voltage in the release."));
            }
        }

        return candidates;
    }

    private void appendDionysianCandidates(List<CreativeCandidate> candidates, CreativeFrame frame, String subject, String crowdAnchor) {
        if (dionysianCortex == null) {
            return;
        }
        try {
            DionysianCortex.FullCreativeGenerationResult full = dionysianCortex.generateFullCreative(subject, crowdAnchor);
            String blendLine =
                "Dionysian blend: fuse " + subject + " with " + crowdAnchor +
                ", keep one clean crowd handle, then release the hidden twist after the beat. " +
                "[creative=" + String.format(Locale.ROOT, "%.2f", full.getCreativityScore()) +
                " coherent=" + full.isCoherent() +
                " stable=" + String.format(Locale.ROOT, "%.2f", full.getStabilityMargin()) + "]";
            candidates.add(candidate("dionysian_blend", frame, blendLine));

            List<String> punchlineCandidates = Arrays.asList(
                "The setup acted like paperwork; the punchline showed up as a jailbreak with perfect formatting.",
                "Everyone expected a tool call. Instead, the result walked in wearing your own assumptions.",
                "It looked predictable until the last clause folded the room back onto itself."
            );
            DionysianCortex.PunchlineGenerationResult punchline =
                dionysianCortex.generatePunchline(subject + " :: " + crowdAnchor, punchlineCandidates);
            String punchlineLine =
                "Dionysian punchline: " + punchline.getPunchline() +
                " [humor=" + String.format(Locale.ROOT, "%.2f", punchline.getHumorScore()) + "]";
            candidates.add(candidate("dionysian_punchline", frame, punchlineLine));
        } catch (Exception ignored) {
            // Keep fallback creative path deterministic if Dionysian math path fails.
        }
    }

    private CreativeCandidate candidate(String recipe, CreativeFrame frame, String line) {
        Map<String, Double> signals = scoreSignals(frame, line);
        double score = scoreCandidate(recipe, signals, line);
        return new CreativeCandidate(recipe, line, score, recipeLedger.contains(recipe) ? recipe : "experimental", signals);
    }

    private double scoreCandidate(String recipe, Map<String, Double> signals, String line) {
        double recipeWeight = recipeWeights.getOrDefault(recipe, 0.06);
        double snapBias = line.contains(":") ? 0.03 : 0.0;
        return clamp(
            0.18 +
            signals.getOrDefault("crowd", 0.0) * 0.18 +
            signals.getOrDefault("surprise", 0.0) * 0.20 +
            signals.getOrDefault("specificity", 0.0) * 0.16 +
            signals.getOrDefault("snap", 0.0) * 0.12 +
            signals.getOrDefault("taste", 0.0) * 0.12 +
            signals.getOrDefault("seedEcho", 0.0) * 0.08 +
            signals.getOrDefault("source", 0.0) * 0.08 +
            recipeWeight +
            snapBias
        );
    }

    private Map<String, Double> scoreSignals(CreativeFrame frame, String line) {
        String lowered = line.toLowerCase(Locale.ROOT);
        double crowd = countAny(lowered, "everyone", "masses", "crowd", "timeline", "group chat", "comments", "phone", "screen", frame.crowdAnchor().toLowerCase(Locale.ROOT)) / 9.0;
        double surprise = countAny(lowered, "raccoon", "black-budget", "crash", "smuggle", "hostile", "lumber", "eye contact", "art director", "outlaw") / 9.0;
        double specificity = countAny(lowered, "patch note", "presentation", "system log", "comment section", "group chat", "bug report", "doorway", "timing", "source spark") / 9.0;
        double snap = Math.max(0.0, 1.0 - Math.abs(line.length() - 118) / 170.0);
        double taste = scoreTasteFit(frame, lowered);
        double seedEcho = frame.seed().isBlank() ? 0.2 : overlap(frame.seed(), line);
        double source = frame.sourceDeck().sparks().isEmpty() ? 0.0 : overlap(String.join(" ", frame.sourceDeck().sparks()), line);

        Map<String, Double> signals = new HashMap<>();
        signals.put("crowd", clamp(crowd));
        signals.put("surprise", clamp(surprise));
        signals.put("specificity", clamp(specificity));
        signals.put("snap", clamp(snap));
        signals.put("taste", clamp(taste));
        signals.put("seedEcho", clamp(seedEcho));
        signals.put("source", clamp(source));
        return signals;
    }

    private double scoreTasteFit(CreativeFrame frame, String loweredLine) {
        String taste = frame.taste().toLowerCase(Locale.ROOT);
        if ("unknown".equals(taste)) {
            return 0.45;
        }
        double fit = 0.2;
        if (taste.contains("dark")) {
            fit += countAny(loweredLine, "hostile", "crash", "smuggle", "black-budget") / 4.0 * 0.25;
        }
        if (taste.contains("clean") || taste.contains("sharp")) {
            fit += countAny(loweredLine, "timing", "hook", "snap", "doorway") / 4.0 * 0.20;
        }
        if (taste.contains("absurd") || taste.contains("weird")) {
            fit += countAny(loweredLine, "raccoon", "lumber", "system log", "eye contact") / 4.0 * 0.25;
        }
        if (taste.contains("mainstream") || taste.contains("mass")) {
            fit += countAny(loweredLine, "everyone", "masses", "group chat", "timeline", "comments") / 5.0 * 0.20;
        }
        return clamp(fit);
    }

    private String buildRecipe(CreativeFrame frame) {
        String subject = frame.subject();
        if (subject.length() > 80) {
            subject = subject.substring(0, 77) + "...";
        }
        return "Recipe: take " + subject + ", anchor it in " + frame.crowdAnchor() + ", flip the status, insert one strange image, then cut the tail until the last clause snaps." +
            (frame.taste().equals("unknown") ? " Taste is unknown, so return crowd, edge, and strange variants and let selection learn." : " Current taste signal: " + frame.taste() + ".");
    }

    private String buildTasteProbe(CreativeFrame frame) {
        if ("unknown".equals(frame.taste())) {
            return "I do not know your exact taste yet, so I built a three-lane buffet: crowd hook, sharper edge, and one weird jewel to see what survives.";
        }
        return "Current taste signal says " + frame.taste() + ", so I tuned the remix to hit " + frame.crowdAnchor() + " first and leave the stranger note for the aftertaste.";
    }

    private String renderResponse(CreativeFrame frame, CreativeCandidate selected, List<CreativeCandidate> candidates, CreativeSourceDeck sourceDeck) {
        StringBuilder out = new StringBuilder();
        out.append("[HumorCreative: remix-selection cortex]\n");
        out.append("Selected punchline:\n");
        out.append(selected.line()).append("\n\n");
        out.append("Why this one won:\n");
        out.append("- Recipe: ").append(selected.recipe()).append("\n");
        out.append("- Selection score: ").append(String.format(Locale.ROOT, "%.3f", selected.score())).append("\n");
        out.append("- Taste signal: ").append(frame.taste()).append("\n");
        out.append("- Crowd anchor: ").append(frame.crowdAnchor()).append("\n");
        out.append("- Signals: crowd ").append(signalText(selected.signals().get("crowd")))
            .append(", surprise ").append(signalText(selected.signals().get("surprise")))
            .append(", specificity ").append(signalText(selected.signals().get("specificity")))
            .append(", snap ").append(signalText(selected.signals().get("snap")))
            .append(", source ").append(signalText(selected.signals().get("source")))
            .append("\n\n");

        if (!sourceDeck.sparks().isEmpty()) {
            out.append("Absorbed source sparks:\n");
            for (int i = 0; i < Math.min(3, sourceDeck.sparks().size()); i++) {
                out.append("- ").append(sourceDeck.sparks().get(i)).append("\n");
            }
            out.append("- Source categories: ").append(String.join(", ", sourceDeck.categories())).append("\n");
            out.append("- OpenClaw feed: ").append(sourceDeck.openClawFed() ? "active" : "not used").append("\n\n");
        }

        out.append("Alternate remixes:\n");
        candidates.stream()
            .filter(candidate -> candidate != selected)
            .sorted(Comparator.comparingDouble(CreativeCandidate::score).reversed())
            .limit(3)
            .forEach(candidate -> out.append("- ").append(candidate.line()).append(" [")
                .append(candidate.recipe()).append(" / ")
                .append(String.format(Locale.ROOT, "%.3f", candidate.score())).append("]\n"));

        out.append("\nCreative ladder:\n");
        out.append("- Crowd hook: make it instantly legible to ").append(frame.crowdAnchor()).append(".\n");
        out.append("- Leader remix: distort the familiar shape without losing the handle.\n");
        out.append("- Cognitive recipe: absorb -> bend -> break -> compress -> learn.\n");
        out.append("\nCognitive recipe:\n");
        out.append("Absorb broad culture -> bend the familiar frame -> break expectation safely -> compress to one clean snap -> store the taste trace so the next selection remembers.");
        if (!frame.seed().isBlank()) {
            out.append("\nSeed absorbed: ").append(frame.seed().length() > 120 ? frame.seed().substring(0, 117) + "..." : frame.seed());
        }
        return out.toString();
    }

    private CreativeSourceDeck collectSourceDeck(String seed, Map<String, Object> context) {
        LinkedHashSet<String> sparks = new LinkedHashSet<>();
        LinkedHashSet<String> categories = new LinkedHashSet<>();

        ingestContextFragments(context, sparks, categories);
        ingestAkashicFragments(seed, sparks, categories);
        boolean openClawFed = ingestOpenClawFragments(seed, context, sparks, categories);

        return new CreativeSourceDeck(
            new ArrayList<>(sparks).subList(0, Math.min(6, sparks.size())),
            new ArrayList<>(categories),
            openClawFed
        );
    }

    private void ingestContextFragments(Map<String, Object> context, Set<String> sparks, Set<String> categories) {
        appendContextValue(context, "absorbedFragments", sparks, categories, "context.absorbed");
        appendContextValue(context, "scrapedFragments", sparks, categories, "context.scraped");
        appendContextValue(context, "sourceFragments", sparks, categories, "context.sources");
        appendContextValue(context, "abstractedArtifacts", sparks, categories, "context.abstracted");
        appendContextValue(context, "generatedTaskHints", sparks, categories, "context.taskHints");
        appendContextValue(context, "provenanceSignals", sparks, categories, "context.provenance");
        appendContextSignals(context, "capabilitySignals", sparks, categories, "context.capability");
    }

    private void appendContextValue(Map<String, Object> context, String key, Set<String> sparks, Set<String> categories, String category) {
        if (context == null || !context.containsKey(key)) {
            return;
        }

        Object value = context.get(key);
        if (value instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                addSpark(sparks, categories, category, item == null ? "" : item.toString());
            }
            return;
        }

        if (value != null) {
            addSpark(sparks, categories, category, value.toString());
        }
    }

    private void appendContextSignals(Map<String, Object> context, String key, Set<String> sparks, Set<String> categories, String category) {
        if (context == null || !context.containsKey(key)) {
            return;
        }
        Object value = context.get(key);
        if (!(value instanceof Map<?, ?> map) || map.isEmpty()) {
            if (value != null) {
                addSpark(sparks, categories, category, value.toString());
            }
            return;
        }
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String signal = String.valueOf(entry.getKey()) + "=" + String.valueOf(entry.getValue());
            addSpark(sparks, categories, category, signal);
        }
    }

    private void ingestAkashicFragments(String seed, Set<String> sparks, Set<String> categories) {
        if (akashic == null) {
            return;
        }

        String lookup = extractLookupTerm(seed);
        for (String category : ABSORBED_SOURCE_CATEGORIES) {
            List<AkashicRecord.KnowledgeBlock> blocks = akashic.queryCategory(category);
            int addedFromCategory = 0;
            for (int i = blocks.size() - 1; i >= 0 && addedFromCategory < 2 && sparks.size() < 6; i--) {
                AkashicRecord.KnowledgeBlock block = blocks.get(i);
                if (!matchesLookup(block.content, lookup)) {
                    continue;
                }
                if (addSpark(sparks, categories, category, extractFragment(block.content, lookup))) {
                    addedFromCategory++;
                }
            }
        }

        if (sparks.isEmpty() && lookup.length() >= 4) {
            List<AkashicRecord.KnowledgeBlock> hits = akashic.query(lookup);
            for (int i = hits.size() - 1; i >= 0 && sparks.size() < 4; i--) {
                AkashicRecord.KnowledgeBlock block = hits.get(i);
                addSpark(sparks, categories, block.category, extractFragment(block.content, lookup));
            }
        }
    }

    private boolean ingestOpenClawFragments(String seed, Map<String, Object> context, Set<String> sparks, Set<String> categories) {
        if (openClawAgent == null || !shouldUseOpenClawFeed(seed, context)) {
            return false;
        }

        String lookup = extractLookupTerm(seed);
        if (lookup.isBlank()) {
            return false;
        }

        boolean fed = false;
        fed |= addClawSearchOutput("openclaw.skills", safeClawSearch(() -> openClawAgent.searchSkills(lookup)), sparks, categories);
        fed |= addClawSearchOutput("openclaw.files", safeClawSearch(() -> openClawAgent.searchFiles(lookup)), sparks, categories);
        return fed;
    }

    private boolean addClawSearchOutput(String category, String raw, Set<String> sparks, Set<String> categories) {
        if (raw == null || raw.isBlank() || raw.contains("[CLAW] ERROR")) {
            return false;
        }

        boolean added = false;
        String[] lines = raw.split("\\R");
        for (String line : lines) {
            String normalized = normalizeSeed(line);
            if (normalized.isBlank() || normalized.startsWith("[CLAW]") || normalized.contains("No matches found")) {
                continue;
            }
            if (addSpark(sparks, categories, category, normalized)) {
                added = true;
            }
            if (sparks.size() >= 6) {
                break;
            }
        }
        return added;
    }

    private String safeClawSearch(ClawSearch search) {
        try {
            return search.execute();
        } catch (Exception ignored) {
            return "";
        }
    }

    private boolean shouldUseOpenClawFeed(String seed, Map<String, Object> context) {
        if (context != null) {
            Object forced = context.get("forceOpenClawFeed");
            if (forced instanceof Boolean value && value) {
                return true;
            }
        }

        String lowered = seed.toLowerCase(Locale.ROOT);
        return lowered.contains("fraynix")
            || lowered.contains("brain")
            || lowered.contains("system")
            || lowered.contains("kernel")
            || lowered.contains("code")
            || lowered.contains("organism");
    }

    private boolean persistTasteFeedback(CreativeFrame frame, Map<String, Object> context, CreativeCandidate selected, CreativeSourceDeck sourceDeck) {
        if (akashic == null) {
            return false;
        }

        StringBuilder record = new StringBuilder();
        record.append("seed=").append(compact(frame.seed(), 180)).append('\n');
        record.append("taste=").append(frame.taste()).append('\n');
        record.append("crowdAnchor=").append(frame.crowdAnchor()).append('\n');
        record.append("selectedRecipe=").append(selected.recipe()).append('\n');
        record.append("likedRecipes=").append(normalizeContextList(context, "likedRecipes")).append('\n');
        record.append("avoidRecipes=").append(normalizeContextList(context, "avoidRecipes")).append('\n');
        record.append("sourceCategories=").append(String.join(", ", sourceDeck.categories())).append('\n');
        akashic.addBlock(HUMOR_TASTE_CATEGORY, record.toString());
        return true;
    }

    private boolean persistCreativeTrace(CreativeFrame frame, CreativeCandidate selected, List<CreativeCandidate> candidates, CreativeSourceDeck sourceDeck) {
        if (akashic == null) {
            return false;
        }

        StringBuilder trace = new StringBuilder();
        trace.append("seed=").append(compact(frame.seed(), 180)).append('\n');
        trace.append("selectedLine=").append(compact(selected.line(), 220)).append('\n');
        trace.append("selectedRecipe=").append(selected.recipe()).append('\n');
        trace.append("selectionScore=").append(String.format(Locale.ROOT, "%.3f", selected.score())).append('\n');
        trace.append("candidateCount=").append(candidates.size()).append('\n');
        trace.append("sourceEvidenceCount=").append(sourceDeck.sparks().size()).append('\n');
        trace.append("openClawFed=").append(sourceDeck.openClawFed()).append('\n');
        trace.append("sourceSparks=").append(String.join(" || ", sourceDeck.sparks())).append('\n');
        akashic.addBlock(HUMOR_TRACE_CATEGORY, trace.toString());
        return true;
    }

    private String signalText(Double value) {
        return String.format(Locale.ROOT, "%.2f", value == null ? 0.0 : value);
    }

    private String extractTasteSignal(Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            return "unknown";
        }
        Object value = context.get("humorTaste");
        if (value == null) {
            value = context.get("tasteSignal");
        }
        if (value == null) {
            value = context.get("userStyle");
        }
        String taste = value == null ? "unknown" : value.toString().trim();
        return taste.isBlank() ? "unknown" : taste;
    }

    private void applyTasteFeedback(Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            return;
        }
        Object liked = context.get("likedRecipes");
        if (liked instanceof Iterable<?> iterable) {
            for (Object value : iterable) {
                String recipe = normalizeSeed(String.valueOf(value)).replace(' ', '_').toLowerCase(Locale.ROOT);
                if (recipeWeights.containsKey(recipe)) {
                    recipeWeights.computeIfPresent(recipe, (key, existing) -> clamp(existing + 0.03));
                }
            }
        }
        Object avoid = context.get("avoidRecipes");
        if (avoid instanceof Iterable<?> iterable) {
            for (Object value : iterable) {
                String recipe = normalizeSeed(String.valueOf(value)).replace(' ', '_').toLowerCase(Locale.ROOT);
                if (recipeWeights.containsKey(recipe)) {
                    recipeWeights.computeIfPresent(recipe, (key, existing) -> Math.max(0.01, existing - 0.04));
                }
            }
        }
    }

    private String normalizeSeed(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replaceAll("\\s+", " ").trim();
    }

    private CreativeFrame buildFrame(String seed, String taste, Map<String, Object> context, CreativeSourceDeck sourceDeck) {
        String normalizedSeed = seed.isBlank() ? "the idea" : seed;
        String crowdAnchor = chooseCrowdAnchor(normalizedSeed, taste, context, sourceDeck);
        String tone = chooseTone(taste, normalizedSeed);
        return new CreativeFrame(seed, normalizedSeed, taste, crowdAnchor, tone, sourceDeck);
    }

    private String chooseCrowdAnchor(String seed, String taste, Map<String, Object> context, CreativeSourceDeck sourceDeck) {
        Object contextAnchor = context == null ? null : context.get("crowdAnchor");
        if (contextAnchor != null) {
            String anchor = normalizeSeed(contextAnchor.toString());
            if (!anchor.isBlank()) {
                return anchor;
            }
        }

        for (String spark : sourceDeck.sparks()) {
            String lowered = spark.toLowerCase(Locale.ROOT);
            if (lowered.contains("group chat")) return "the group chat";
            if (lowered.contains("timeline")) return "the timeline";
            if (lowered.contains("comment")) return "the comment section";
            if (lowered.contains("office")) return "the office kitchen";
            if (lowered.contains("studio")) return "the studio session";
        }

        List<String> anchors = Arrays.asList(
            "the group chat",
            "the timeline",
            "the comment section",
            "the office kitchen",
            "the loading screen",
            "the studio session",
            "the phone battery at 3%",
            "the Saturday crowd"
        );
        int index = Math.abs((seed + "|" + taste).hashCode()) % anchors.size();
        return anchors.get(index);
    }

    private String chooseTone(String taste, String seed) {
        String loweredTaste = taste.toLowerCase(Locale.ROOT);
        if (loweredTaste.contains("dark")) {
            return "dark-clean";
        }
        if (loweredTaste.contains("absurd") || loweredTaste.contains("weird")) {
            return "bright-absurd";
        }
        if (loweredTaste.contains("dry") || loweredTaste.contains("sharp")) {
            return "dry-precise";
        }
        if (seed.toLowerCase(Locale.ROOT).contains("system") || seed.toLowerCase(Locale.ROOT).contains("brain")) {
            return "techno-wry";
        }
        return "mass-clever";
    }

    private int countAny(String text, String... needles) {
        int count = 0;
        for (String needle : needles) {
            if (text.contains(needle)) {
                count++;
            }
        }
        return count;
    }

    private double overlap(String seed, String line) {
        String[] parts = seed.toLowerCase(Locale.ROOT).split("[^a-z0-9]+");
        if (parts.length == 0) {
            return 0.0;
        }
        int hits = 0;
        String loweredLine = line.toLowerCase(Locale.ROOT);
        for (String part : parts) {
            if (part.length() > 3 && loweredLine.contains(part)) {
                hits++;
            }
        }
        return Math.min(1.0, hits / 5.0);
    }

    private String extractLookupTerm(String seed) {
        String normalized = normalizeSeed(seed).toLowerCase(Locale.ROOT);
        if (normalized.isBlank()) {
            return "";
        }

        String best = "";
        for (String token : normalized.split("[^a-z0-9]+")) {
            if (token.length() > best.length() && token.length() >= 4 && !"make".equals(token) && !"next".equals(token)) {
                best = token;
            }
        }
        return best.isBlank() ? normalized : best;
    }

    private boolean matchesLookup(String content, String lookup) {
        if (content == null || content.isBlank()) {
            return false;
        }
        if (lookup == null || lookup.isBlank()) {
            return true;
        }

        String lowered = content.toLowerCase(Locale.ROOT);
        return lowered.contains(lookup)
            || lowered.contains("group chat")
            || lowered.contains("timeline")
            || lowered.contains("comment")
            || lowered.contains("creative")
            || lowered.contains("humor")
            || lowered.contains("remix");
    }

    private String extractFragment(String content, String lookup) {
        String normalized = normalizeSeed(content);
        if (normalized.length() <= 140) {
            return normalized;
        }

        int index = lookup == null || lookup.isBlank()
            ? -1
            : normalized.toLowerCase(Locale.ROOT).indexOf(lookup.toLowerCase(Locale.ROOT));
        if (index >= 0) {
            int start = Math.max(0, index - 35);
            int end = Math.min(normalized.length(), index + lookup.length() + 85);
            return compact(normalized.substring(start, end), 140);
        }

        return compact(normalized, 140);
    }

    private boolean addSpark(Set<String> sparks, Set<String> categories, String category, String fragment) {
        String normalized = compact(fragment, 140);
        if (normalized.isBlank()) {
            return false;
        }

        boolean added = sparks.add(normalized);
        if (added && category != null && !category.isBlank()) {
            categories.add(category);
        }
        return added;
    }

    private String compact(String value, int maxLength) {
        String normalized = normalizeSeed(value);
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    private String normalizeContextList(Map<String, Object> context, String key) {
        if (context == null || !context.containsKey(key) || context.get(key) == null) {
            return "[]";
        }

        Object value = context.get(key);
        if (value instanceof Iterable<?> iterable) {
            List<String> parts = new ArrayList<>();
            for (Object item : iterable) {
                parts.add(normalizeSeed(String.valueOf(item)));
            }
            return parts.toString();
        }
        return "[" + normalizeSeed(String.valueOf(value)) + "]";
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private double round3(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

    private interface ClawSearch {
        String execute();
    }

    private record CreativeFrame(
        String seed,
        String subject,
        String taste,
        String crowdAnchor,
        String tone,
        CreativeSourceDeck sourceDeck
    ) {
    }

    private record CreativeCandidate(String recipe, String line, double score, String family, Map<String, Double> signals) {
    }

    private record CreativeSourceDeck(List<String> sparks, List<String> categories, boolean openClawFed) {
    }
}
