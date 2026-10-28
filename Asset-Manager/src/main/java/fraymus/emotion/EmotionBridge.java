package fraymus.emotion;

import fraymus.knowledge.AkashicRecord;
import java.nio.file.Path;
import java.util.*;

/**
 * emotion / feel: drives the 7-layer EmotionEngine from the FraynixBoot prompt and logs a summary block
 * (category EMOTION_STATE) to the AkashicRecord every time it runs.
 * State lives under FRAYNIX_ROOT/data/emotion (data/ is git-ignored); override with EMOTION_DATA.
 */
public class EmotionBridge {
    private static EmotionEngine engine;
    private static boolean restored;

    static Path dataDir() {
        String e = System.getenv("EMOTION_DATA");
        if (e != null && !e.isBlank()) return Path.of(e);
        String root = System.getenv("FRAYNIX_ROOT");
        return Path.of(root != null && !root.isBlank() ? root : System.getProperty("user.dir")).resolve("data").resolve("emotion");
    }

    static synchronized EmotionEngine engine() {
        if (engine == null) {
            engine = new EmotionEngine(dataDir());
            restored = engine.load();
        }
        return engine;
    }

    static String f(double d) { return String.format(Locale.ROOT, "%.3f", d); }
    static String bar(double v, double lo, double hi) {
        int n = (int) Math.round((EmotionEngine.clamp(v, lo, hi) - lo) / (hi - lo) * 20);
        return "[" + "#".repeat(n) + ".".repeat(20 - n) + "]";
    }

    public static void run(String arg, AkashicRecord ak) {
        String[] t = (arg == null ? "" : arg.trim()).split("\\s+");
        String sub = t[0].isEmpty() ? "status" : t[0].toLowerCase();
        EmotionEngine en = engine();
        String what;
        try {
            switch (sub) {
                case "help", "?" -> { help(); return; }
                case "status", "show" -> { status(en); what = "status"; }
                case "tick" -> {
                    int n = 1;
                    if (t.length > 1) n = Math.max(1, Math.min(100, Integer.parseInt(t[1])));
                    for (int i = 0; i < n; i++) en.tick();
                    en.saveState();
                    System.out.println("ticked " + n + "x (decay toward set points)");
                    status(en); what = "tick x" + n;
                }
                case "perturb" -> {
                    if (t.length < 3) { System.out.println("usage: emotion perturb <" + String.join("|", EmotionEngine.BODY_DIMS) + "> <delta -1..1>"); return; }
                    double d = Double.parseDouble(t[2]);
                    if (!en.perturbBody(t[1].toLowerCase(), d)) { System.out.println("\u001B[31m[!] unknown dimension '" + t[1] + "'. Choose: " + String.join(", ", EmotionEngine.BODY_DIMS) + "\u001B[0m"); return; }
                    en.computeCoreFeel(); en.modulateBehavior(); en.saveState();
                    System.out.println("perturbed " + t[1].toLowerCase() + " by " + d);
                    status(en); what = "perturb " + t[1].toLowerCase() + " " + d;
                }
                case "event" -> {
                    if (t.length < 2) { System.out.println("usage: emotion event <type> [description...] [goal_relevance=.. likelihood=.. control=.. caused_by_self=.. fairness=.. novelty=.. user_valence=.. bond_delta=..]"); System.out.println("types: goal_progress goal_blocked surprise_positive surprise_negative loss connection disconnect novel_input routine energy_drain energy_restore (anything else = neutral)"); return; }
                    Map<String, Double> hints = new HashMap<>(Map.of("goal_relevance", 0.7));
                    Double uv = null, bd = null; StringBuilder desc = new StringBuilder();
                    for (int i = 2; i < t.length; i++) {
                        int eq = t[i].indexOf('=');
                        if (eq > 0) {
                            String k = t[i].substring(0, eq).toLowerCase(); double v = Double.parseDouble(t[i].substring(eq + 1));
                            if (k.equals("user_valence")) uv = v; else if (k.equals("bond_delta")) bd = v; else hints.put(k, v);
                        } else desc.append(desc.length() > 0 ? " " : "").append(t[i]);
                    }
                    en.processEvent(t[1].toLowerCase(), desc.length() == 0 ? t[1] : desc.toString(), hints, null, uv, bd);
                    status(en); what = "event " + t[1].toLowerCase() + (desc.length() > 0 ? ": " + desc : "");
                }
                case "markers" -> {
                    int n = t.length > 1 ? Math.max(1, Integer.parseInt(t[1])) : 10;
                    List<EmotionEngine.SomaticMarker> ms = new ArrayList<>(en.markerIndex.values());
                    ms.sort(Comparator.comparing(EmotionEngine.SomaticMarker::timestamp));
                    System.out.println("somatic markers: " + ms.size() + " (showing last " + Math.min(n, ms.size()) + ")");
                    for (int i = Math.max(0, ms.size() - n); i < ms.size(); i++) {
                        var m = ms.get(i);
                        System.out.println("  " + m.timestamp() + " | " + m.dominantEmotion() + " " + f(m.emotionIntensity()) + " | v=" + f(m.valence()) + " a=" + f(m.arousal()) + " | " + m.context());
                    }
                    what = "markers (" + ms.size() + ")";
                }
                case "tag" -> {
                    if (t.length < 2) { System.out.println("usage: emotion tag <text>"); return; }
                    String ctx = arg.trim().substring(3).trim();
                    var m = en.tagMemory(ctx); en.saveState();
                    System.out.println("tagged: " + m.dominantEmotion() + " " + f(m.emotionIntensity()) + " (v=" + f(m.valence()) + ", a=" + f(m.arousal()) + ")");
                    what = "tag: " + ctx;
                }
                case "recall" -> {
                    if (t.length < 2) { System.out.println("usage: emotion recall <text>"); return; }
                    String ctx = arg.trim().substring(6).trim();
                    var m = en.recall(ctx);
                    if (m == null) System.out.println("no matching somatic marker");
                    else System.out.println("recalled: " + m.context() + " -> " + m.dominantEmotion() + " " + f(m.emotionIntensity()) + " (v=" + f(m.valence()) + ", a=" + f(m.arousal()) + ")");
                    what = "recall: " + ctx;
                }
                case "reset" -> { en.initBody(); en.dopamine = 0; en.tickCount = 0; en.activeEmotions.clear(); en.computeCoreFeel(); en.modulateBehavior(); en.saveState(); System.out.println("body reset to set points (somatic markers kept on disk)"); status(en); what = "reset"; }
                default -> { System.out.println("unknown subcommand '" + sub + "'"); help(); return; }
            }
        } catch (NumberFormatException e) { System.out.println("\u001B[31m[!] expected a number: " + e.getMessage() + "\u001B[0m"); return; }
        catch (Exception e) { System.out.println("\u001B[31m[!] emotion failed: " + e + "\u001B[0m"); return; }
        remember(en, what, ak);
    }

    static void remember(EmotionEngine en, String what, AkashicRecord ak) {
        String top = en.activeEmotions.isEmpty() ? "none" : en.activeEmotions.get(0).name() + " " + f(en.activeEmotions.get(0).intensity());
        String content = "EMOTION " + what + "\n" + "valence=" + f(en.valence) + " arousal=" + f(en.arousal) + " dopamine=" + f(en.dopamine)
            + " dev=" + f(en.bodyDeviation()) + " tick=" + en.tickCount + "\n" + "top emotion: " + top + " | social: " + en.socialEmotion
            + " | markers=" + en.markerIndex.size() + " | conflicts=" + en.detectConflicts();
        if (ak != null) {
            String h = ak.addBlock("EMOTION_STATE", content);
            System.out.println("  \u001B[32m[brain] stored in AkashicRecord: " + h + " (EMOTION_STATE)\u001B[0m");
        }
    }

    static void status(EmotionEngine en) {
        System.out.println("=== EMOTION ENGINE (7-layer) === data: " + en.dataDir() + (restored ? "  [restored from disk]" : "  [fresh]"));
        System.out.println("L1 body (set point):");
        for (int i = 0; i < EmotionEngine.BODY_DIMS.length; i++)
            System.out.println("   " + String.format("%-12s", EmotionEngine.BODY_DIMS[i]) + bar(en.body[i], 0, 1) + " " + f(en.body[i]) + "  (" + EmotionEngine.SET_POINTS[i] + ")");
        System.out.println("   deviation " + f(en.bodyDeviation()));
        System.out.println("L2 valence " + f(en.valence) + " " + bar(en.valence, -1, 1) + "  arousal " + f(en.arousal) + "  dopamine " + f(en.dopamine));
        System.out.print("L3 emotions: ");
        if (en.activeEmotions.isEmpty()) System.out.println("none");
        else { StringBuilder sb = new StringBuilder(); for (var e : en.activeEmotions) sb.append(e.name()).append(' ').append(f(e.intensity())).append("  "); System.out.println(sb.toString().trim()); }
        StringBuilder d = new StringBuilder();
        for (int i = 0; i < EmotionEngine.DIAL_NAMES.length; i++) d.append(EmotionEngine.DIAL_NAMES[i]).append('=').append(f(en.dials[i])).append("  ");
        System.out.println("L4 dials: " + d.toString().trim());
        System.out.println("L5 somatic markers: " + en.markerIndex.size());
        System.out.println("L6 social: " + en.socialEmotion + " " + f(en.socialIntensity) + " (bond " + f(en.bondLevel) + ", empathy " + f(en.empathyRead) + ")");
        System.out.println("L7 tick " + en.tickCount + " | conflicts " + en.detectConflicts() + " | log depth " + en.emotionLog.size());
    }

    static void help() {
        System.out.println("emotion | feel [status]                      show all 7 layers");
        System.out.println("emotion tick [n]                           decay toward set points (n up to 100)");
        System.out.println("emotion perturb <dim> <delta>              nudge a body dimension: " + String.join(", ", EmotionEngine.BODY_DIMS));
        System.out.println("emotion event <type> [text] [k=v ...]      full pipeline (appraise, dials, social, tag, tick); keys: goal_relevance likelihood control caused_by_self fairness novelty user_valence bond_delta");
        System.out.println("emotion markers [n] | tag <text> | recall <text>   somatic markers (memories tagged with feeling)");
        System.out.println("emotion reset                              body back to set points");
        System.out.println("Each run logs an EMOTION_STATE block to the AkashicRecord. State: FRAYNIX_ROOT/data/emotion");
    }
}

