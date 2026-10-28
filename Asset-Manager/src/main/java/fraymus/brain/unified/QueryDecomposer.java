package fraymus.brain.unified;

import fraymus.core.OllamaBridge;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Decomposes complex multi-domain queries into typed sub-tasks,
 * each routed to the most appropriate specialized brain.
 *
 * Strategy (tried in order):
 *   1. Structural parsing — split on numbered markers, bullet points, "include:" sections
 *   2. Keyword-to-brain mapping — classify each segment
 *   3. Ollama pre-pass — ask local model to decompose (fallback for unstructured queries)
 */
public class QueryDecomposer {

    private final OllamaBridge ollama;
    private final BrainRegistry registry;

    // ── Structural split patterns ──────────────────────────────────────
    private static final Pattern NUMBERED_ITEM = Pattern.compile(
        "(?:(?:^|\\n)\\s*(\\d+)[.)]\\s*|(?:(?:^|\\n)\\s*[-•*]\\s*))(.+?)(?=(?:\\n\\s*(\\d+)[.)]\\s*|\\n\\s*[-•*]\\s*|$))",
        Pattern.DOTALL);

    private static final Pattern INCLUDE_COLON = Pattern.compile(
        "(?:include|including|with|cover(?:ing)?)\\s*:\\s*(.+?)(?=\\n\\n|$)",
        Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    // ── Keyword → TaskType mapping ──────────────────────────────────────
    private static final Map<String, SubTask.TaskType> KEYWORD_TYPE = new LinkedHashMap<>();
    static {
        KEYWORD_TYPE.put("derive", SubTask.TaskType.MATH);
        KEYWORD_TYPE.put("equation", SubTask.TaskType.MATH);
        KEYWORD_TYPE.put("formula", SubTask.TaskType.MATH);
        KEYWORD_TYPE.put("mathematical", SubTask.TaskType.MATH);
        KEYWORD_TYPE.put("calculate", SubTask.TaskType.MATH);
        KEYWORD_TYPE.put("compute", SubTask.TaskType.MATH);

        KEYWORD_TYPE.put("physics", SubTask.TaskType.PHYSICS);
        KEYWORD_TYPE.put("simulate", SubTask.TaskType.PHYSICS);
        KEYWORD_TYPE.put("wave", SubTask.TaskType.PHYSICS);
        KEYWORD_TYPE.put("stress", SubTask.TaskType.PHYSICS);
        KEYWORD_TYPE.put("propagation", SubTask.TaskType.PHYSICS);
        KEYWORD_TYPE.put("thermal", SubTask.TaskType.PHYSICS);
        KEYWORD_TYPE.put("material", SubTask.TaskType.PHYSICS);

        KEYWORD_TYPE.put("code", SubTask.TaskType.CODE);
        KEYWORD_TYPE.put("python", SubTask.TaskType.CODE);
        KEYWORD_TYPE.put("generate", SubTask.TaskType.CODE);
        KEYWORD_TYPE.put("implement", SubTask.TaskType.CODE);
        KEYWORD_TYPE.put("program", SubTask.TaskType.CODE);

        KEYWORD_TYPE.put("analogy", SubTask.TaskType.ANALOGY);
        KEYWORD_TYPE.put("analogous", SubTask.TaskType.ANALOGY);
        KEYWORD_TYPE.put("map", SubTask.TaskType.ANALOGY);
        KEYWORD_TYPE.put("cross-domain", SubTask.TaskType.ANALOGY);

        KEYWORD_TYPE.put("evolve", SubTask.TaskType.EVOLUTION);
        KEYWORD_TYPE.put("evolutionary", SubTask.TaskType.EVOLUTION);
        KEYWORD_TYPE.put("genetic", SubTask.TaskType.EVOLUTION);
        KEYWORD_TYPE.put("mutate", SubTask.TaskType.EVOLUTION);
        KEYWORD_TYPE.put("fitness", SubTask.TaskType.EVOLUTION);
        KEYWORD_TYPE.put("optimize", SubTask.TaskType.EVOLUTION);

        KEYWORD_TYPE.put("design", SubTask.TaskType.CREATIVE);
        KEYWORD_TYPE.put("invent", SubTask.TaskType.CREATIVE);
        KEYWORD_TYPE.put("novel", SubTask.TaskType.CREATIVE);
        KEYWORD_TYPE.put("creative", SubTask.TaskType.CREATIVE);
        KEYWORD_TYPE.put("mechanism", SubTask.TaskType.CREATIVE);

        KEYWORD_TYPE.put("detect", SubTask.TaskType.REASONING);
        KEYWORD_TYPE.put("analyze", SubTask.TaskType.REASONING);
        KEYWORD_TYPE.put("reason", SubTask.TaskType.REASONING);
        KEYWORD_TYPE.put("logic", SubTask.TaskType.REASONING);

        KEYWORD_TYPE.put("quantum", SubTask.TaskType.QUANTUM);
        KEYWORD_TYPE.put("consciousness", SubTask.TaskType.CONSCIOUSNESS);
        KEYWORD_TYPE.put("memory", SubTask.TaskType.MEMORY);
        KEYWORD_TYPE.put("visualize", SubTask.TaskType.VISION);
    }

    // ── TaskType → preferred brain name ─────────────────────────────────
    private static final Map<SubTask.TaskType, String> TYPE_BRAIN = new LinkedHashMap<>();
    static {
        TYPE_BRAIN.put(SubTask.TaskType.MATH, "Demiurge");
        TYPE_BRAIN.put(SubTask.TaskType.PHYSICS, "Demiurge");
        TYPE_BRAIN.put(SubTask.TaskType.CODE, "Babel");
        TYPE_BRAIN.put(SubTask.TaskType.ANALOGY, "Kronos");
        TYPE_BRAIN.put(SubTask.TaskType.EVOLUTION, "Evolutionary");
        TYPE_BRAIN.put(SubTask.TaskType.CREATIVE, "Creative");
        TYPE_BRAIN.put(SubTask.TaskType.REASONING, "Logical");
        TYPE_BRAIN.put(SubTask.TaskType.QUANTUM, "Quantum");
        TYPE_BRAIN.put(SubTask.TaskType.CONSCIOUSNESS, "Consciousness");
        TYPE_BRAIN.put(SubTask.TaskType.MEMORY, "Memory");
        TYPE_BRAIN.put(SubTask.TaskType.VISION, "Visual");
    }

    // ── TaskType → BrainCapability ──────────────────────────────────────
    private static final Map<SubTask.TaskType, List<BrainCapability>> TYPE_CAPS = new LinkedHashMap<>();
    static {
        TYPE_CAPS.put(SubTask.TaskType.MATH, List.of(BrainCapability.MATHEMATICS));
        TYPE_CAPS.put(SubTask.TaskType.PHYSICS, List.of(BrainCapability.PHYSICS));
        TYPE_CAPS.put(SubTask.TaskType.CODE, List.of(BrainCapability.DATA, BrainCapability.REASONING));
        TYPE_CAPS.put(SubTask.TaskType.ANALOGY, List.of(BrainCapability.FRACTAL, BrainCapability.REASONING));
        TYPE_CAPS.put(SubTask.TaskType.EVOLUTION, List.of(BrainCapability.EVOLUTION));
        TYPE_CAPS.put(SubTask.TaskType.CREATIVE, List.of(BrainCapability.CREATIVITY));
        TYPE_CAPS.put(SubTask.TaskType.REASONING, List.of(BrainCapability.REASONING));
        TYPE_CAPS.put(SubTask.TaskType.QUANTUM, List.of(BrainCapability.QUANTUM));
        TYPE_CAPS.put(SubTask.TaskType.CONSCIOUSNESS, List.of(BrainCapability.CONSCIOUSNESS));
        TYPE_CAPS.put(SubTask.TaskType.MEMORY, List.of(BrainCapability.MEMORY));
        TYPE_CAPS.put(SubTask.TaskType.VISION, List.of(BrainCapability.VISION));
        TYPE_CAPS.put(SubTask.TaskType.GENERAL, List.of(BrainCapability.REASONING));
    }

    public QueryDecomposer(OllamaBridge ollama, BrainRegistry registry) {
        this.ollama = ollama;
        this.registry = registry;
    }

    /**
     * Determine if a query is complex enough to benefit from decomposition.
     */
    public boolean isDecomposable(BrainQuery query) {
        String text = query.getQuery();
        if (text == null || text.isBlank()) return false;

        // Check for structural markers
        if (NUMBERED_ITEM.matcher(text).find()) return true;
        if (INCLUDE_COLON.matcher(text).find()) return true;

        // Check for multi-domain keyword density
        int domainCount = countDistinctDomains(text.toLowerCase(Locale.ROOT));
        return domainCount >= 3;
    }

    /**
     * Decompose a complex query into ordered sub-tasks.
     */
    public List<SubTask> decompose(BrainQuery query) {
        String text = query.getQuery();
        if (text == null || text.isBlank()) return List.of();

        List<SubTask> tasks = new ArrayList<>();

        // Strategy 1: Structural split on numbered items
        List<String> segments = splitNumbered(text);
        if (segments.size() > 1) {
            for (int i = 0; i < segments.size(); i++) {
                String segment = segments.get(i).trim();
                if (segment.isBlank()) continue;
                SubTask.TaskType type = classifySegment(segment);
                tasks.add(buildSubTask(type, segment, i));
            }
            return deduplicate(tasks);
        }

        // Strategy 2: Split on "include:" sections
        segments = splitOnInclude(text);
        if (segments.size() > 1) {
            for (int i = 0; i < segments.size(); i++) {
                String segment = segments.get(i).trim();
                if (segment.isBlank()) continue;
                SubTask.TaskType type = classifySegment(segment);
                tasks.add(buildSubTask(type, segment, i));
            }
            return deduplicate(tasks);
        }

        // Strategy 3: Ollama-assisted decomposition
        if (ollama != null && ollama.isConnected()) {
            List<SubTask> ollamaTasks = ollamaDecompose(text);
            if (!ollamaTasks.isEmpty()) return ollamaTasks;
        }

        // Fallback: single general task
        tasks.add(buildSubTask(SubTask.TaskType.GENERAL, text, 0));
        return tasks;
    }

    // ── Structural Splitting ────────────────────────────────────────────

    private List<String> splitNumbered(String text) {
        List<String> segments = new ArrayList<>();
        Matcher m = NUMBERED_ITEM.matcher(text);
        int lastEnd = 0;

        while (m.find()) {
            if (m.start() > lastEnd) {
                String preamble = text.substring(lastEnd, m.start()).trim();
                if (!preamble.isBlank()) segments.add(preamble);
            }
            segments.add(m.group(2).trim());
            lastEnd = m.end();
        }

        if (lastEnd < text.length()) {
            String remainder = text.substring(lastEnd).trim();
            if (!remainder.isBlank()) segments.add(remainder);
        }

        // If no numbered items found, return the whole text as one segment
        if (segments.isEmpty()) segments.add(text);
        return segments;
    }

    private List<String> splitOnInclude(String text) {
        List<String> segments = new ArrayList<>();
        Matcher m = INCLUDE_COLON.matcher(text);

        if (m.find()) {
            int lastEnd = 0;
            do {
                if (m.start() > lastEnd) {
                    String preamble = text.substring(lastEnd, m.start()).trim();
                    if (!preamble.isBlank()) segments.add(preamble);
                }
                // Split the include body on commas or semicolons for sub-items
                String body = m.group(1).trim();
                String[] subItems = body.split("[,;]\\s*(?=(?:[^()]*\\([^()]*\\))*[^()]*$)");
                for (String item : subItems) {
                    String trimmed = item.trim();
                    if (!trimmed.isBlank()) segments.add(trimmed);
                }
                lastEnd = m.end();
            } while (m.find());

            if (lastEnd < text.length()) {
                String remainder = text.substring(lastEnd).trim();
                if (!remainder.isBlank()) segments.add(remainder);
            }
        }

        if (segments.isEmpty()) segments.add(text);
        return segments;
    }

    // ── Classification ──────────────────────────────────────────────────

    private SubTask.TaskType classifySegment(String segment) {
        String lower = segment.toLowerCase(Locale.ROOT);
        Map<SubTask.TaskType, Integer> scores = new LinkedHashMap<>();

        for (Map.Entry<String, SubTask.TaskType> entry : KEYWORD_TYPE.entrySet()) {
            if (lower.contains(entry.getKey())) {
                scores.merge(entry.getValue(), 1, Integer::sum);
            }
        }

        if (scores.isEmpty()) return SubTask.TaskType.GENERAL;

        return scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(SubTask.TaskType.GENERAL);
    }

    private int countDistinctDomains(String lower) {
        Set<SubTask.TaskType> domains = new HashSet<>();
        for (Map.Entry<String, SubTask.TaskType> entry : KEYWORD_TYPE.entrySet()) {
            if (lower.contains(entry.getKey())) {
                domains.add(entry.getValue());
            }
        }
        return domains.size();
    }

    // ── SubTask Construction ────────────────────────────────────────────

    private SubTask buildSubTask(SubTask.TaskType type, String prompt, int index) {
        String brainName = resolveBrainName(type);
        List<BrainCapability> caps = TYPE_CAPS.getOrDefault(type,
            List.of(BrainCapability.REASONING));
        return new SubTask(type, prompt, brainName, caps, index);
    }

    private String resolveBrainName(SubTask.TaskType type) {
        String preferred = TYPE_BRAIN.get(type);
        if (preferred != null && registry != null) {
            Optional<UnifiedBrain> brain = registry.getBrain(preferred);
            if (brain.isPresent() && brain.get().getHealth() > 0.5) {
                return preferred;
            }
        }
        // Fallback: find any healthy brain with matching capabilities
        if (registry != null) {
            List<BrainCapability> caps = TYPE_CAPS.get(type);
            if (caps != null) {
                List<UnifiedBrain> candidates = registry.getHealthyBrainsWithCapabilities(caps);
                if (!candidates.isEmpty()) return candidates.get(0).getName();
            }
        }
        return null; // orchestrator will auto-select
    }

    // ── Deduplication ───────────────────────────────────────────────────

    private List<SubTask> deduplicate(List<SubTask> tasks) {
        if (tasks.size() <= 1) return tasks;

        List<SubTask> result = new ArrayList<>();
        Set<String> seenPrompts = new HashSet<>();

        for (SubTask task : tasks) {
            String normalized = task.getPrompt().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
            if (seenPrompts.add(normalized)) {
                result.add(task);
            }
        }
        return result;
    }

    // ── Ollama-Assisted Decomposition ───────────────────────────────────

    private List<SubTask> ollamaDecompose(String text) {
        try {
            String system = """
                You are a query decomposition engine. Given a complex multi-domain query,
                break it into focused sub-tasks. Output ONLY valid JSON array.

                Each object must have:
                - "type": one of MATH, PHYSICS, CODE, ANALOGY, EVOLUTION, CREATIVE, REASONING, GENERAL
                - "prompt": the focused sub-task prompt
                - "order": integer ordering

                Example input: "Design a hull material with math model, detection, sealing, evolution, and Python code"
                Example output:
                [{"type":"MATH","prompt":"Derive the wave equation for stress propagation through a phi-spiral lattice with phi-based damping terms","order":0},
                 {"type":"PHYSICS","prompt":"Model the standing wave interference pattern and how a fracture creates a detectable phase shift","order":1},
                 {"type":"CREATIVE","prompt":"Design an autonomous sealing mechanism triggered by fracture detection","order":2},
                 {"type":"EVOLUTION","prompt":"Design an evolutionary algorithm to optimize lattice geometry with fitness function and mutation operators","order":3},
                 {"type":"CODE","prompt":"Generate working Python code that simulates the material under thermal stress with visualization","order":4}]
                """;

            String ctx = "Complex query to decompose:\n\n" + text + "\n\nOutput JSON array only:";
            String raw = ollama.speak(system, ctx);

            if (raw == null || raw.isBlank()) return List.of();

            // Extract JSON array from response
            String json = extractJsonArray(raw);
            if (json == null) return List.of();

            return parseSubTasks(json);
        } catch (Exception e) {
            return List.of();
        }
    }

    private String extractJsonArray(String raw) {
        int start = raw.indexOf('[');
        int end = raw.lastIndexOf(']');
        if (start >= 0 && end > start) {
            return raw.substring(start, end + 1);
        }
        return null;
    }

    private List<SubTask> parseSubTasks(String json) {
        List<SubTask> tasks = new ArrayList<>();
        try {
            // Simple manual JSON parsing to avoid dependency
            Pattern objPattern = Pattern.compile(
                "\\{[^}]+\\}");
            Matcher m = objPattern.matcher(json);
            int order = 0;

            while (m.find()) {
                String obj = m.group();
                String typeStr = extractJsonString(obj, "type");
                String prompt = extractJsonString(obj, "prompt");
                String orderStr = extractJsonString(obj, "order");

                if (typeStr != null && prompt != null) {
                    SubTask.TaskType type;
                    try {
                        type = SubTask.TaskType.valueOf(typeStr.toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException e) {
                        type = SubTask.TaskType.GENERAL;
                    }

                    int idx = order;
                    if (orderStr != null) {
                        try { idx = Integer.parseInt(orderStr); } catch (NumberFormatException ignored) {}
                    }

                    tasks.add(buildSubTask(type, prompt, idx));
                }
                order++;
            }
        } catch (Exception e) {
            return List.of();
        }
        return tasks;
    }

    private String extractJsonString(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }
}
