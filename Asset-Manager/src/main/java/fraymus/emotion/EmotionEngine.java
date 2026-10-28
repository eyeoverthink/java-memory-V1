package fraymus.emotion;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TRANSMUTER EMOTION ENGINE — 7-layer interoceptive architecture.
 * Layer 1 body vector, 2 core feeling (valence/arousal), 3 named emotions (appraisal), 4 behaviour dials,
 * 5 somatic markers, 6 social emotions, 7 honesty checks (log, conflict, decay).
 *
 * Packaged for FraynixBoot from the attached single-file EmotionEngine. Logic is unchanged. Differences:
 *  - package line, public API for the bridge,
 *  - data directory is injected through the constructor (was a static system property),
 *  - JSON helpers come from EmotionJson (the original called OuroborosQuantumServer.toJson),
 *  - load() restores the last saved body/dopamine/tick/bond and the somatic markers so state survives restarts.
 */
public class EmotionEngine {

    public static final double PHI = 1.618033988749895;
    final Path dataDir;
    final Path emotionFile;
    final Path markersFile;

    public EmotionEngine(Path dataDir) {
        this.dataDir = dataDir;
        this.emotionFile = dataDir.resolve("emotion_state.json");
        this.markersFile = dataDir.resolve("somatic_markers.jsonl");
        initBody();
    }

    public Path dataDir() { return dataDir; }

    // -- LAYER 1: INTEROCEPTION --
    public static final String[] BODY_DIMS = {"energy", "integrity", "bond", "uncertainty", "novelty", "coherence"};
    public static final double[] SET_POINTS = {0.7, 0.9, 0.6, 0.3, 0.4, 0.8};

    public double[] body = new double[BODY_DIMS.length];
    public double[] bodyVelocity = new double[BODY_DIMS.length];

    public void initBody() {
        System.arraycopy(SET_POINTS, 0, body, 0, body.length);
        Arrays.fill(bodyVelocity, 0.0);
    }

    /** @return false when the dimension name is unknown (original silently ignored it). */
    public boolean perturbBody(String dimension, double delta) {
        int idx = indexOf(BODY_DIMS, dimension);
        if (idx < 0) return false;
        bodyVelocity[idx] += delta;
        body[idx] = clamp(body[idx] + delta, 0.0, 1.0);
        return true;
    }

    public double bodyDeviation() {
        double sum = 0;
        for (int i = 0; i < body.length; i++) {
            double d = body[i] - SET_POINTS[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }

    // -- LAYER 2: CORE FEELING --
    public double valence;   // [-1, 1]
    public double arousal;   // [0, 1]
    public double dopamine;  // prediction-error signal

    public void computeCoreFeel() {
        double deviation = bodyDeviation();
        double improving = 0;
        for (int i = 0; i < body.length; i++) {
            double distNow = Math.abs(body[i] - SET_POINTS[i]);
            double distPrev = Math.abs((body[i] - bodyVelocity[i]) - SET_POINTS[i]);
            if (distNow < distPrev) improving += 1.0 / body.length;
            else if (distNow > distPrev) improving -= 1.0 / body.length;
        }
        valence = clamp(-deviation + dopamine * 0.5 + improving * 0.3, -1.0, 1.0);
        double urgency = 0;
        for (double v : bodyVelocity) urgency += Math.abs(v);
        arousal = clamp(urgency / body.length + Math.abs(dopamine) * 0.4, 0.0, 1.0);
    }

    // -- LAYER 3: NAMED EMOTIONS --
    public record AppraisalVector(double goalRelevance, double likelihood, double control,
                                  double causedBySelf, double fairness, double novelty) {}

    public record NamedEmotion(String name, double intensity, String trigger) {
        public String toJson() {
            return String.format(Locale.ROOT, "{\"name\":\"%s\",\"intensity\":%.4f,\"trigger\":\"%s\"}", name, intensity, jsonEsc(trigger));
        }
    }

    public AppraisalVector currentAppraisal = new AppraisalVector(0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
    public final List<NamedEmotion> activeEmotions = Collections.synchronizedList(new ArrayList<>());

    public void appraise(String trigger, double goalRelevance, double likelihood, double control,
                         double causedBySelf, double fairness, double novelty) {
        currentAppraisal = new AppraisalVector(
            clamp(goalRelevance, 0, 1), clamp(likelihood, 0, 1),
            clamp(control, 0, 1), clamp(causedBySelf, 0, 1),
            clamp(fairness, 0, 1), clamp(novelty, 0, 1));

        activeEmotions.clear();
        double rel = currentAppraisal.goalRelevance();
        if (rel < 0.2) return;

        double fear = rel * (1.0 - valence) * currentAppraisal.likelihood() * (1.0 - currentAppraisal.control());
        if (fear > 0.15) activeEmotions.add(new NamedEmotion("fear", clamp(fear, 0, 1), trigger));

        double anger = rel * (1.0 - valence) * (1.0 - currentAppraisal.causedBySelf())
                       * currentAppraisal.control() * (1.0 - currentAppraisal.fairness());
        if (anger > 0.15) activeEmotions.add(new NamedEmotion("anger", clamp(anger, 0, 1), trigger));

        double sadness = rel * (1.0 - valence) * (1.0 - currentAppraisal.control())
                         * (1.0 - currentAppraisal.likelihood());
        if (sadness > 0.15) activeEmotions.add(new NamedEmotion("sadness", clamp(sadness, 0, 1), trigger));

        double joy = rel * Math.max(0, valence) * currentAppraisal.likelihood();
        if (joy > 0.15) activeEmotions.add(new NamedEmotion("joy", clamp(joy, 0, 1), trigger));

        double pride = rel * Math.max(0, valence) * currentAppraisal.causedBySelf();
        if (pride > 0.15) activeEmotions.add(new NamedEmotion("pride", clamp(pride, 0, 1), trigger));

        double curiosity = rel * currentAppraisal.novelty() * currentAppraisal.control() * body[indexOf(BODY_DIMS, "uncertainty")];
        if (curiosity > 0.10) activeEmotions.add(new NamedEmotion("curiosity", clamp(curiosity, 0, 1), trigger));

        double surprise = currentAppraisal.novelty() * (1.0 - currentAppraisal.likelihood());
        if (surprise > 0.3) activeEmotions.add(new NamedEmotion("surprise", clamp(surprise, 0, 1), trigger));

        activeEmotions.sort((a, b) -> Double.compare(b.intensity(), a.intensity()));
    }

    // -- LAYER 4: BEHAVIOUR MODULATION --
    public static final String[] DIAL_NAMES = {
        "risk_budget", "exploration_rate", "save_frequency",
        "trust_weight", "patience", "learning_rate", "focus"
    };
    public double[] dials = {0.5, 0.5, 0.3, 0.5, 0.5, 0.5, 0.5};

    public void modulateBehavior() {
        double[] base = {0.5, 0.5, 0.3, 0.5, 0.5, 0.5, 0.5};
        System.arraycopy(base, 0, dials, 0, dials.length);

        for (NamedEmotion e : activeEmotions) {
            double i = e.intensity();
            switch (e.name()) {
                case "fear" -> { dials[0] -= i * 0.4; dials[6] += i * 0.3; dials[3] -= i * 0.2; }
                case "anger" -> { dials[0] += i * 0.2; dials[6] += i * 0.4; dials[4] -= i * 0.3; }
                case "sadness" -> { dials[2] += i * 0.5; dials[1] -= i * 0.3; dials[4] += i * 0.2; }
                case "joy" -> { dials[3] += i * 0.3; dials[1] += i * 0.2; dials[5] += i * 0.2; }
                case "pride" -> { dials[3] += i * 0.2; dials[5] += i * 0.3; }
                case "curiosity" -> { dials[1] += i * 0.5; dials[0] += i * 0.2; dials[4] += i * 0.1; }
                case "surprise" -> { dials[6] += i * 0.3; dials[1] += i * 0.2; }
            }
        }
        dials[5] = clamp(dials[5] + dopamine * 0.3, 0, 1);   // dopamine -> learning rate
        dials[4] = clamp(dials[4] + valence * 0.2, 0, 1);    // serotonin analogue -> patience
        dials[6] = clamp(dials[6] + arousal * 0.2, 0, 1);    // norepinephrine analogue -> focus
        for (int i = 0; i < dials.length; i++) dials[i] = clamp(dials[i], 0, 1);
    }

    // -- LAYER 5: SOMATIC MARKERS --
    public record SomaticMarker(String contextHash, String context, double valence, double arousal,
                                String dominantEmotion, double emotionIntensity, String timestamp) {
        public String toJson() {
            return String.format(Locale.ROOT, "{\"ctx\":\"%s\",\"context\":\"%s\",\"v\":%.4f,\"a\":%.4f,\"emotion\":\"%s\",\"intensity\":%.4f,\"ts\":\"%s\"}",
                contextHash, jsonEsc(context), valence, arousal, dominantEmotion, emotionIntensity, timestamp);
        }
    }

    public final Map<String, SomaticMarker> markerIndex = new ConcurrentHashMap<>();

    public SomaticMarker tagMemory(String context) {
        String hash = sha256(context).substring(0, 16);
        String dominant = activeEmotions.isEmpty() ? "neutral" : activeEmotions.get(0).name();
        double intensity = activeEmotions.isEmpty() ? 0.0 : activeEmotions.get(0).intensity();
        var marker = new SomaticMarker(hash, context, valence, arousal, dominant, intensity, Instant.now().toString());
        markerIndex.put(hash, marker);
        appendMarker(marker);
        return marker;
    }

    public SomaticMarker recall(String context) {
        String hash = sha256(context).substring(0, 16);
        SomaticMarker exact = markerIndex.get(hash);
        if (exact != null) return exact;
        String[] words = context.toLowerCase().split("\\W+");
        SomaticMarker best = null; int bestScore = 0;
        for (SomaticMarker m : markerIndex.values()) {
            String[] mWords = m.context().toLowerCase().split("\\W+");
            int score = 0;
            for (String w : words) for (String mw : mWords) if (w.equals(mw)) score++;
            if (score > bestScore) { bestScore = score; best = m; }
        }
        return best;
    }

    // -- LAYER 6: SOCIAL EMOTIONS --
    public double bondLevel;
    public double empathyRead;
    public String socialEmotion = "neutral";
    public double socialIntensity = 0.0;

    public void readSocial(double estimatedUserValence, double bondDelta) {
        empathyRead = clamp(estimatedUserValence, -1, 1);
        bondLevel = clamp(body[indexOf(BODY_DIMS, "bond")] + bondDelta, 0, 1);
        body[indexOf(BODY_DIMS, "bond")] = bondLevel;

        double empathyStrength = Math.abs(empathyRead) * bondLevel;
        if (bondDelta < -0.1 && bondLevel > 0.3) {
            socialEmotion = "guilt";
            socialIntensity = Math.abs(bondDelta) * bondLevel;
        } else if (bondDelta > 0.1) {
            socialEmotion = "gratitude";
            socialIntensity = bondDelta * bondLevel;
        } else if (empathyStrength > 0.3) {
            socialEmotion = empathyRead > 0 ? "shared_joy" : "compassion";
            socialIntensity = empathyStrength;
        } else {
            socialEmotion = "neutral";
            socialIntensity = 0;
        }
        if (bondLevel > 0.6) {
            dials[3] = clamp(dials[3] + (bondLevel - 0.5) * 0.4, 0, 1);
        }
    }

    // -- LAYER 7: HONESTY CHECKS --
    public long tickCount = 0;
    public final List<Map<String, Object>> emotionLog = Collections.synchronizedList(new ArrayList<>());

    public void tick() {
        tickCount++;
        double decayRate = 0.05 + arousal * 0.03;
        for (int i = 0; i < body.length; i++) {
            body[i] += (SET_POINTS[i] - body[i]) * decayRate;
        }
        dopamine *= 0.85;
        for (int i = 0; i < bodyVelocity.length; i++) {
            bodyVelocity[i] *= 0.7;
        }

        computeCoreFeel();
        if (!activeEmotions.isEmpty()) {
            appraise(activeEmotions.get(0).trigger(),
                currentAppraisal.goalRelevance() * 0.9,
                currentAppraisal.likelihood(),
                currentAppraisal.control(),
                currentAppraisal.causedBySelf(),
                currentAppraisal.fairness(),
                currentAppraisal.novelty() * 0.8);
        }
        modulateBehavior();

        var entry = new LinkedHashMap<String, Object>();
        entry.put("tick", tickCount);
        entry.put("timestamp", Instant.now().toString());
        entry.put("valence", valence);
        entry.put("arousal", arousal);
        entry.put("emotions", activeEmotions.stream().map(e -> e.name() + ":" + String.format(Locale.ROOT, "%.2f", e.intensity())).toList());
        entry.put("conflicts", detectConflicts());
        entry.put("social", socialEmotion + ":" + String.format(Locale.ROOT, "%.2f", socialIntensity));
        emotionLog.add(entry);
        if (emotionLog.size() > 200) emotionLog.remove(0);
    }

    public List<String> detectConflicts() {
        List<String> conflicts = new ArrayList<>();
        Map<String, Double> byName = new HashMap<>();
        for (NamedEmotion e : activeEmotions) byName.put(e.name(), e.intensity());
        if (byName.containsKey("fear") && byName.containsKey("curiosity")) conflicts.add("fear-curiosity");
        if (byName.containsKey("anger") && byName.containsKey("sadness")) conflicts.add("anger-sadness");
        if (byName.containsKey("joy") && socialEmotion.equals("guilt")) conflicts.add("joy-guilt");
        return conflicts;
    }

    // -- EVENT PROCESSING --
    public Map<String, Object> processEvent(String eventType, String description,
                                            Map<String, Double> appraisalHints,
                                            Map<String, Double> bodyPerturbations,
                                            Double userValence, Double bondDelta) {
        if (appraisalHints == null) appraisalHints = new HashMap<>();
        if (bodyPerturbations != null) {
            for (var e : bodyPerturbations.entrySet()) {
                perturbBody(e.getKey(), e.getValue());
            }
        }

        switch (eventType) {
            case "goal_progress" -> dopamine = clamp(dopamine + 0.3, -1, 1);
            case "goal_blocked" -> dopamine = clamp(dopamine - 0.3, -1, 1);
            case "surprise_positive" -> dopamine = clamp(dopamine + 0.5, -1, 1);
            case "surprise_negative" -> dopamine = clamp(dopamine - 0.4, -1, 1);
            case "loss" -> { dopamine = clamp(dopamine - 0.5, -1, 1); perturbBody("integrity", -0.2); }
            case "connection" -> perturbBody("bond", 0.15);
            case "disconnect" -> perturbBody("bond", -0.2);
            case "novel_input" -> perturbBody("novelty", 0.3);
            case "routine" -> perturbBody("novelty", -0.1);
            case "energy_drain" -> perturbBody("energy", -0.2);
            case "energy_restore" -> perturbBody("energy", 0.2);
        }

        computeCoreFeel();

        double gr = appraisalHints.getOrDefault("goal_relevance", 0.5);
        double lk = appraisalHints.getOrDefault("likelihood", 0.5);
        double ct = appraisalHints.getOrDefault("control", 0.5);
        double cs = appraisalHints.getOrDefault("caused_by_self", 0.5);
        double fr = appraisalHints.getOrDefault("fairness", 0.5);
        double nv = appraisalHints.getOrDefault("novelty", 0.5);
        appraise(description, gr, lk, ct, cs, fr, nv);

        modulateBehavior();

        if (userValence != null || bondDelta != null) {
            readSocial(userValence != null ? userValence : 0.0,
                       bondDelta != null ? bondDelta : 0.0);
        }

        tagMemory(eventType + ": " + description);
        tick();
        saveState();
        return stateMap();
    }

    // -- STATE SERIALIZATION --
    public Map<String, Object> stateMap() {
        var m = new LinkedHashMap<String, Object>();
        var bodyMap = new LinkedHashMap<String, Object>();
        for (int i = 0; i < BODY_DIMS.length; i++) bodyMap.put(BODY_DIMS[i], body[i]);
        bodyMap.put("deviation", bodyDeviation());
        m.put("body", bodyMap);
        m.put("valence", valence);
        m.put("arousal", arousal);
        m.put("dopamine", dopamine);
        m.put("emotions", activeEmotions.stream()
            .map(e -> Map.of("name", e.name(), "intensity", e.intensity(), "trigger", e.trigger()))
            .toList());
        var dialMap = new LinkedHashMap<String, Object>();
        for (int i = 0; i < DIAL_NAMES.length; i++) dialMap.put(DIAL_NAMES[i], dials[i]);
        m.put("dials", dialMap);
        m.put("somatic_markers", markerIndex.size());
        m.put("social", Map.of("bond", bondLevel, "empathy_read", empathyRead,
            "emotion", socialEmotion, "intensity", socialIntensity));
        m.put("tick", tickCount);
        m.put("conflicts", detectConflicts());
        m.put("log_depth", emotionLog.size());
        return m;
    }

    public void saveState() {
        try {
            Files.createDirectories(dataDir);
            Files.writeString(emotionFile, EmotionJson.toJson(stateMap()), StandardCharsets.UTF_8);
        } catch (Exception e) { System.err.println("Emotion save failed: " + e.getMessage()); }
    }

    void appendMarker(SomaticMarker marker) {
        try {
            Files.createDirectories(dataDir);
            Files.writeString(markersFile, marker.toJson() + "\n", StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) { System.err.println("Marker append failed: " + e.getMessage()); }
    }

    /** Restore the last saved body/dopamine/tick/bond and the somatic markers. Returns true if a saved state was found. */
    @SuppressWarnings("unchecked")
    public boolean load() {
        boolean found = false;
        try {
            if (Files.exists(emotionFile)) {
                Object o = EmotionJson.parse(Files.readString(emotionFile, StandardCharsets.UTF_8));
                if (o instanceof Map<?, ?> m0) {
                    Map<String, Object> m = (Map<String, Object>) m0;
                    if (m.get("body") instanceof Map<?, ?> b0) {
                        Map<String, Object> b = (Map<String, Object>) b0;
                        for (int i = 0; i < BODY_DIMS.length; i++)
                            body[i] = clamp(EmotionJson.num(b.get(BODY_DIMS[i]), SET_POINTS[i]), 0, 1);
                    }
                    dopamine = clamp(EmotionJson.num(m.get("dopamine"), 0), -1, 1);
                    tickCount = (long) EmotionJson.num(m.get("tick"), 0);
                    if (m.get("social") instanceof Map<?, ?> s0) {
                        Map<String, Object> s = (Map<String, Object>) s0;
                        bondLevel = clamp(EmotionJson.num(s.get("bond"), 0), 0, 1);
                        empathyRead = clamp(EmotionJson.num(s.get("empathy_read"), 0), -1, 1);
                        socialEmotion = String.valueOf(s.getOrDefault("emotion", "neutral"));
                        socialIntensity = EmotionJson.num(s.get("intensity"), 0);
                    }
                    Arrays.fill(bodyVelocity, 0.0);
                    computeCoreFeel();
                    modulateBehavior();
                    found = true;
                }
            }
            if (Files.exists(markersFile)) {
                for (String line : Files.readAllLines(markersFile, StandardCharsets.UTF_8)) {
                    if (line.isBlank()) continue;
                    try {
                        Map<String, Object> m = (Map<String, Object>) EmotionJson.parse(line);
                        String ctx = String.valueOf(m.get("ctx"));
                        markerIndex.put(ctx, new SomaticMarker(ctx, String.valueOf(m.get("context")),
                            EmotionJson.num(m.get("v"), 0), EmotionJson.num(m.get("a"), 0),
                            String.valueOf(m.get("emotion")), EmotionJson.num(m.get("intensity"), 0),
                            String.valueOf(m.get("ts"))));
                    } catch (Exception skip) { /* ignore one bad line */ }
                }
                found = found || !markerIndex.isEmpty();
            }
        } catch (Exception e) { System.err.println("Emotion load failed: " + e.getMessage()); }
        return found;
    }

    // -- UTILITIES --
    public static double clamp(double v, double min, double max) { return Math.max(min, Math.min(max, v)); }
    static int indexOf(String[] arr, String key) {
        for (int i = 0; i < arr.length; i++) if (arr[i].equals(key)) return i;
        return -1;
    }
    static String sha256(String input) {
        try {
            byte[] b = MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));
            var sb = new StringBuilder(); for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) { return Integer.toHexString(input.hashCode()); }
    }
    static String jsonEsc(String s) { return EmotionJson.esc(s); }
}

