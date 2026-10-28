package fraymus.seed;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.util.stream.*;

/**
 * UNIVERSAL INTELLIGENCE SEED
 *
 * A self-bootstrapping AGI kernel that discovers knowledge from constraints alone.
 * No training data. No internet. No pre-loaded knowledge graph.
 * Only: Constraints + Consistency + Feedback Loop = Intelligence.
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │  Layer 1 - PERCEPTION  : Symbols (tokens with no meaning)   │
 * │  Layer 2 - RELATION    : Axioms  (primitive truths)         │
 * │  Layer 3 - LOGIC       : Engine  (rules of thought + gates) │
 * │  Layer 4 - LEARNING    : Loop    (memory + contradiction)   │
 * ├─────────────────────────────────────────────────────────────┤
 * │  Subsystems:                                                 │
 * │    LanguageEngine  → Word = property mapping → inference    │
 * │    MathEngine      → Unit arithmetic → algebraic discovery  │
 * │    AnalogyEngine   → Proportional reasoning + similes       │
 * └─────────────────────────────────────────────────────────────┘
 */
public class UniversalIntelligenceSeed {

    // =========================================================
    // LAYER 1: PERCEPTION (Symbols)
    // Raw tokens with no meaning yet — just identity
    // =========================================================

    public enum SymbolType { LETTER, DIGIT, STATE, COLOR, SHAPE, WORD, UNKNOWN }

    public static class Symbol {
        public final String token;
        public final SymbolType type;
        private final Map<String, Double> properties = new LinkedHashMap<>();

        public Symbol(String token, SymbolType type) {
            this.token = token;
            this.type = type;
        }

        public Symbol bind(String property, double strength) {
            properties.merge(property, strength, (a, b) -> Math.min(1.0, a + b));
            return this;
        }

        public double getProperty(String property) {
            return properties.getOrDefault(property, 0.0);
        }

        public Map<String, Double> getProperties() {
            return Collections.unmodifiableMap(properties);
        }

        @Override
        public String toString() {
            String props = properties.isEmpty() ? "" :
                properties.entrySet().stream()
                    .map(e -> e.getKey() + "=" + String.format("%.1f", e.getValue()))
                    .collect(Collectors.joining(", ", "{", "}"));
            return token + "[" + type + "]" + props;
        }
    }

    // =========================================================
    // LAYER 2: RELATION (Axioms / Primitive Truths)
    // These are not explanations — they are ground rules
    // =========================================================

    public enum RelationType { IDENTITY, DISTINCTION, ARITHMETIC, LOGICAL, CATEGORICAL }

    public static class Axiom {
        public final String left;
        public final String op;
        public final String right;
        public final String result;
        public final RelationType type;
        public double confidence;

        public Axiom(String left, String op, String right, String result, RelationType type) {
            this.left = left;
            this.op = op;
            this.right = right;
            this.result = result;
            this.type = type;
            this.confidence = 1.0;
        }

        public boolean matches(String l, String o, String r) {
            return left.equals(l) && op.equals(o) && right.equals(r);
        }

        public void strengthen() { confidence = Math.min(1.0, confidence + 0.01); }
        public void weaken()     { confidence = Math.max(0.0, confidence - 0.1); }

        @Override
        public String toString() {
            return String.format("[%.2f] %s %s %s → %s (%s)",
                confidence, left, op, right, result, type);
        }
    }

    // =========================================================
    // LAYER 3: LOGIC ENGINE (Rules of Thought + Gates)
    // Where intelligence begins
    // =========================================================

    public enum GateType { AND, OR, NOT, XOR, NAND, NOR, IMPLICATION }

    public static class Gate {
        public final GateType type;

        public Gate(GateType type) { this.type = type; }

        public boolean apply(boolean a, boolean b) {
            switch (type) {
                case AND:         return a && b;
                case OR:          return a || b;
                case XOR:         return a ^ b;
                case NAND:        return !(a && b);
                case NOR:         return !(a || b);
                case IMPLICATION: return !a || b;
                default:          return false;
            }
        }

        public boolean applyUnary(boolean a) {
            if (type == GateType.NOT) return !a;
            throw new UnsupportedOperationException("Gate " + type + " requires 2 inputs");
        }

        public String describe(boolean a, boolean b) {
            return String.format("%s(%s, %s) = %s", type, a, b, apply(a, b) ? "TRUE" : "FALSE");
        }
    }

    public enum Valence { EXCITATORY, INHIBITORY, MODULATORY }

    public static class LogicRule {
        public final String  name;
        public final String  pattern;
        public final Valence valence;
        private double weight;
        private int applications;
        private int contradictions;

        public LogicRule(String name, String pattern) {
            this(name, pattern, Valence.MODULATORY);
        }

        public LogicRule(String name, String pattern, Valence valence) {
            this.name    = name;
            this.pattern = pattern;
            this.valence = valence;
            this.weight  = (valence == Valence.EXCITATORY) ? 1.2
                         : (valence == Valence.INHIBITORY)  ? 0.8 : 1.0;
        }

        public void reinforce() {
            applications++;
            double boost = (valence == Valence.EXCITATORY) ? 1.08
                         : (valence == Valence.INHIBITORY)  ? 1.02 : 1.05;
            weight = Math.min(2.0, weight * boost);
        }

        public void contradict() {
            contradictions++;
            double decay = (valence == Valence.INHIBITORY) ? 0.95 : 0.9;
            weight = Math.max(0.01, weight * decay);
        }

        public double getWeight()       { return weight; }
        public int getApplications()    { return applications; }
        public int getContradictions()  { return contradictions; }

        @Override
        public String toString() {
            return String.format("Rule[%s|%s](w=%.3f, apps=%d, contra=%d): %s",
                name, valence, weight, applications, contradictions, pattern);
        }
    }

    // =========================================================
    // LAYER 4: LEARNING LOOP
    // Memory + contradiction detection + rule weight adjustment
    // =========================================================

    public static class MemoryRecord {
        public final long timestamp;
        public final String input;
        public final String output;
        public final boolean contradiction;
        public final String[] rulesFired;

        public MemoryRecord(String input, String output, boolean contradiction, String... rulesFired) {
            this.timestamp = System.currentTimeMillis();
            this.input = input;
            this.output = output;
            this.contradiction = contradiction;
            this.rulesFired = rulesFired;
        }

        @Override
        public String toString() {
            return String.format("[%d] %s → %s%s",
                timestamp, input, output, contradiction ? " [CONTRADICTION]" : "");
        }
    }

    // =========================================================
    // FOSSIL SYSTEM — NOT-tagged failed/contradicted concepts
    // =========================================================

    public static class Fossil {
        public final String concept;
        public final String reason;
        public final long   timestamp;
        public int revivals;

        public Fossil(String concept, String reason) {
            this.concept   = concept;
            this.reason    = reason;
            this.timestamp = System.currentTimeMillis();
            this.revivals  = 0;
        }

        @Override
        public String toString() {
            return String.format("[NOT:%s] reason='%s' revivals=%d",
                concept, reason, revivals);
        }
    }

    // =========================================================
    // DREAM ENGINE — Autonomous synthesis when input ceases
    // Cross-combines symbols/lexicon/axioms into new knowledge
    // =========================================================

    public static class DreamFragment {
        public final String concept;
        public final String synthesis;
        public final long   timestamp;

        public DreamFragment(String concept, String synthesis) {
            this.concept   = concept;
            this.synthesis = synthesis;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "[DREAM] " + concept + " → " + synthesis;
        }
    }

    public static class DreamEngine {
        private final List<DreamFragment> fragments = new CopyOnWriteArrayList<>();

        // ── Transcendental constants ──────────────────────────────────────
        private static final double PHI = 1.618033988749895;
        private static final double PI  = Math.PI;
        private static final double E   = Math.E;

        // FUSION: φ × π × e collide to create a new mathematical element.
        // Seed drives the exponent combination so every call is unique.
        public DreamFragment collide(int seed) {
            int p = Math.abs(seed);
            double v = (Math.pow(PHI, (p % 7) + 1.0) % 1.0)
                     * (Math.pow(PI,  (p % 5) + 1.0) % 1.0)
                     * (Math.pow(E,   (p % 3) + 1.0) % 1.0);
            String element = String.format("Ω%04X", (int)(v * 0xFFFF));
            String formula = String.format(
                "FUSION[φ^%d·π^%d·e^%d]=%.6f",
                (p % 7) + 1, (p % 5) + 1, (p % 3) + 1, v);
            DreamFragment df = new DreamFragment(element, formula);
            fragments.add(df);
            return df;
        }

        // FISSION: decompose axioms into their energy-release components.
        public List<DreamFragment> fission(List<Axiom> axioms) {
            List<DreamFragment> out = new ArrayList<>();
            for (int i = 0; i < Math.min(axioms.size(), 5); i++) {
                Axiom ax = axioms.get(i);
                double energy = (PHI * ax.confidence) % 1.0;
                out.add(new DreamFragment(
                    ax.left + "⊕" + ax.right,
                    String.format("FISSION(%s,%s)→φ-energy=%.4f",
                        ax.left, ax.right, energy)));
            }
            fragments.addAll(out);
            return out;
        }

        // Phi-resonance of a token: unique non-repeating value via
        // Weyl equidistribution of φ, π, e over the token hash.
        public static double phiResonance(String token) {
            int h = Math.abs(token.hashCode());
            double r = (Math.pow(PHI, (h % 7) + 0.5) % 1.0)
                     * (Math.pow(PI,  (h % 5) + 0.5) % 1.0)
                     + (Math.pow(E,   (h % 3) + 0.5) % 1.0) * 0.1;
            return Math.min(1.0, Math.abs(r));
        }

        public List<DreamFragment> dream(Map<String, Symbol> symbols,
                                         Map<String, Word>   lexicon,
                                         List<Axiom>         axioms) {
            List<DreamFragment> fresh = new ArrayList<>();
            fresh.addAll(dreamFromSymbols(new ArrayList<>(symbols.values())));
            fresh.addAll(dreamFromLexicon(new ArrayList<>(lexicon.keySet())));
            fresh.addAll(dreamFromAxioms(axioms));
            fragments.addAll(fresh);
            return fresh;
        }

        private List<DreamFragment> dreamFromSymbols(List<Symbol> list) {
            List<DreamFragment> out = new ArrayList<>();
            if (list.size() < 2) return out;
            int spread = Math.max(1, (int)(list.size() * 0.618));
            for (int i = 0; i < Math.min(list.size(), 15); i++) {
                Symbol a = list.get(i);
                Symbol b = list.get((i + spread) % list.size());
                for (String prop : a.getProperties().keySet()) {
                    if (a.getProperty(prop) > 0.7 && b.getProperty(prop) > 0.7) {
                        out.add(new DreamFragment(a.token + ":" + b.token,
                            a.token + "+" + b.token + "→RESONANCE(" + prop + ")"));
                        break;
                    }
                }
            }
            return out;
        }

        private List<DreamFragment> dreamFromLexicon(List<String> keys) {
            List<DreamFragment> out = new ArrayList<>();
            if (keys.size() < 2) return out;
            for (int i = 0; i < Math.min(keys.size(), 8); i++) {
                String wa = keys.get(i);
                String wb = keys.get((i + 3) % keys.size());
                if (!wa.equals(wb)) {
                    String compound = wa.substring(0, Math.min(3, wa.length()))
                                    + wb.toLowerCase().substring(0, Math.min(3, wb.length()));
                    out.add(new DreamFragment(compound,
                        "FUSE(" + wa + "," + wb + ")→" + compound));
                }
            }
            return out;
        }

        private List<DreamFragment> dreamFromAxioms(List<Axiom> axioms) {
            List<DreamFragment> out = new ArrayList<>();
            Map<String, List<String>> graph = new LinkedHashMap<>();
            for (Axiom ax : axioms) {
                if ("~".equals(ax.op) && "TRUE".equals(ax.result))
                    graph.computeIfAbsent(ax.left, k -> new ArrayList<>()).add(ax.right);
            }
            for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
                String a = entry.getKey();
                for (String b : entry.getValue()) {
                    List<String> bLinks = graph.get(b);
                    if (bLinks != null) {
                        for (String c : bLinks) {
                            if (!c.equals(a)) {
                                out.add(new DreamFragment(a + "~" + c,
                                    "CHAIN(" + a + "→" + b + "→" + c + ")"));
                                if (out.size() >= 5) return out;
                            }
                        }
                    }
                }
            }
            return out;
        }

        public List<DreamFragment> getFragments() {
            return Collections.unmodifiableList(fragments);
        }
    }

    // =========================================================
    // PRE-VERSE ENGINE — Forward prediction counterpart to reverse engineering
    //
    // Where reverse takes an artifact→extracts rules (backward O(n) scan),
    // pre-verse takes current rules→predicts future artifacts (forward O(n) scan).
    //
    // Five prediction modes (analogous to reverse's five phases):
    //   CHAIN     — A→B, B→C ⟹ predict A→C      (transitivity forward-walk)
    //   SYMMETRY  — A~B ⟹ predict B~A            (φ⁻¹ confidence decay)
    //   ANALOGY   — A:B :: C:? ⟹ predict D       (structural pattern match)
    //   PHI_DRIFT — high-resonance symbols attract new connections
    //   ARITHMETIC — detect numeric sequence, extrapolate next term
    // =========================================================

    public static class PreVersePrediction {
        public final String concept;
        public final String basis;
        public final String mode;
        public final double confidence;
        public final long   timestamp;

        public PreVersePrediction(String concept, String basis,
                                  String mode, double confidence) {
            this.concept    = concept;
            this.basis      = basis;
            this.mode       = mode;
            this.confidence = confidence;
            this.timestamp  = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("[PREVERSE|%-10s] %s  ←  %s  (conf=%.3f)",
                mode, concept, basis, confidence);
        }
    }

    public static class PreVerseEngine {
        private static final double PHI     = 1.618033988749895;
        private static final double INV_PHI = 1.0 / PHI;  // ≈ 0.618

        private final List<PreVersePrediction> store = new CopyOnWriteArrayList<>();

        // Core pre-verse: one forward pass per depth level (like reverse's backward pass)
        public List<PreVersePrediction> predict(
                Map<String, Symbol> symbols,
                List<Axiom> axioms,
                int depth) {
            List<PreVersePrediction> fresh = new ArrayList<>();
            for (int d = 0; d < Math.max(1, depth); d++) {
                fresh.addAll(modeChain(axioms));
                fresh.addAll(modeSymmetry(axioms));
                fresh.addAll(modeAnalogy(axioms));
                fresh.addAll(modePhiDrift(symbols, axioms));
                fresh.addAll(modeArithmetic(axioms));
            }
            store.addAll(fresh);
            return fresh;
        }

        // MODE 1 — CHAIN: A→B, B→C ⟹ A→C  (transitivity, same pass direction as reverse)
        private List<PreVersePrediction> modeChain(List<Axiom> axioms) {
            List<PreVersePrediction> out = new ArrayList<>();
            Map<String, List<Axiom>> byLeft = new LinkedHashMap<>();
            for (Axiom ax : axioms)
                byLeft.computeIfAbsent(ax.left, k -> new ArrayList<>()).add(ax);
            int cap = 0;
            for (Axiom ax1 : axioms) {
                List<Axiom> nexts = byLeft.get(ax1.right);
                if (nexts == null) continue;
                for (Axiom ax2 : nexts) {
                    if (ax2.right.equals(ax1.left)) continue;
                    double conf = ax1.confidence * ax2.confidence * INV_PHI;
                    out.add(new PreVersePrediction(
                        ax1.left + "→" + ax2.right,
                        ax1.left + "→" + ax1.right + "→" + ax2.right,
                        "CHAIN", conf));
                    if (++cap >= 12) return out;
                }
            }
            return out;
        }

        // MODE 2 — SYMMETRY: A~B ⟹ B~A  (reflected with φ⁻¹ confidence decay)
        private List<PreVersePrediction> modeSymmetry(List<Axiom> axioms) {
            List<PreVersePrediction> out = new ArrayList<>();
            Set<String> seen = new HashSet<>();
            for (Axiom ax : axioms) seen.add(ax.left + "|" + ax.right);
            int cap = 0;
            for (Axiom ax : axioms) {
                if (ax.type != RelationType.IDENTITY && ax.type != RelationType.CATEGORICAL)
                    continue;
                String key = ax.right + "|" + ax.left;
                if (!seen.contains(key)) {
                    out.add(new PreVersePrediction(
                        ax.right + "~" + ax.left,
                        "MIRROR(" + ax.left + "~" + ax.right + ")",
                        "SYMMETRY", ax.confidence * INV_PHI));
                    seen.add(key);
                    if (++cap >= 8) break;
                }
            }
            return out;
        }

        // MODE 3 — ANALOGY: A:B :: C:D where A:B and C:D share same operator
        private List<PreVersePrediction> modeAnalogy(List<Axiom> axioms) {
            List<PreVersePrediction> out = new ArrayList<>();
            List<Axiom> cats = axioms.stream()
                .filter(a -> a.type == RelationType.CATEGORICAL)
                .limit(20).collect(Collectors.toList());
            for (int i = 0; i < cats.size() && out.size() < 6; i++) {
                for (int j = i + 1; j < cats.size() && out.size() < 6; j++) {
                    Axiom a = cats.get(i), b = cats.get(j);
                    if (!a.left.equals(b.left) && a.op.equals(b.op)) {
                        double conf = (a.confidence + b.confidence) * 0.5 * INV_PHI;
                        out.add(new PreVersePrediction(
                            a.left + ":" + a.right + "::" + b.left + ":" + b.right,
                            "PATTERN(" + a.op + ")",
                            "ANALOGY", conf));
                    }
                }
            }
            return out;
        }

        // MODE 4 — PHI_DRIFT: high-resonance symbols attract each other
        private List<PreVersePrediction> modePhiDrift(
                Map<String, Symbol> symbols, List<Axiom> axioms) {
            List<PreVersePrediction> out = new ArrayList<>();
            if (symbols.size() < 2) return out;
            symbols.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(
                    phiR(e2.getKey()), phiR(e1.getKey())))
                .limit(3)
                .forEach(e -> {
                    double r = phiR(e.getKey());
                    symbols.keySet().stream()
                        .filter(k -> !k.equals(e.getKey()))
                        .max(Comparator.comparingDouble(PreVerseEngine::phiR))
                        .ifPresent(attractor -> {
                            double conf = r * phiR(attractor) * INV_PHI;
                            out.add(new PreVersePrediction(
                                e.getKey() + "⟷" + attractor,
                                String.format("PHI-DRIFT(r=%.4f)", r),
                                "PHI_DRIFT", conf));
                        });
                });
            return out;
        }

        // MODE 5 — ARITHMETIC: detect numeric sequence in axiom results, predict next
        private List<PreVersePrediction> modeArithmetic(List<Axiom> axioms) {
            List<PreVersePrediction> out = new ArrayList<>();
            List<Integer> nums = axioms.stream()
                .filter(ax -> ax.type == RelationType.ARITHMETIC)
                .map(ax -> { try { return Integer.parseInt(ax.result); }
                             catch (Exception ex) { return null; } })
                .filter(Objects::nonNull)
                .distinct().sorted().collect(Collectors.toList());
            if (nums.size() >= 2) {
                int last  = nums.get(nums.size() - 1);
                int prev  = nums.get(nums.size() - 2);
                int delta = last - prev;
                out.add(new PreVersePrediction(
                    "NEXT=" + (last + delta),
                    "SEQ(" + prev + "," + last + ",Δ=" + delta + ")",
                    "ARITHMETIC", INV_PHI));
            }
            return out;
        }

        // Predict future of a single concept token — runs all 5 modes filtered to it
        public List<PreVersePrediction> predictConcept(
                String token, Map<String, Symbol> symbols, List<Axiom> axioms) {
            List<Axiom> related = axioms.stream()
                .filter(ax -> ax.left.equalsIgnoreCase(token)
                           || ax.right.equalsIgnoreCase(token))
                .collect(Collectors.toList());
            Map<String, Symbol> narrow = new LinkedHashMap<>();
            symbols.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(token))
                .forEach(e -> narrow.put(e.getKey(), e.getValue()));
            return predict(narrow.isEmpty() ? symbols : narrow, related, 1);
        }

        private static double phiR(String token) {
            int h = Math.abs(token.hashCode());
            return Math.min(1.0, Math.abs(
                (Math.pow(PHI, (h % 7) + 0.5) % 1.0)
              * (Math.pow(Math.PI, (h % 5) + 0.5) % 1.0)
              + (Math.pow(Math.E,  (h % 3) + 0.5) % 1.0) * 0.1));
        }

        public List<PreVersePrediction> getStore() {
            return Collections.unmodifiableList(store);
        }
    }

    // =========================================================
    // CONNECTOME LAYER — FlyWire-inspired concept graph
    // Neurotransmitters as edge polarities, leaky-integrate-and-fire
    // spiking, 8 neuropil regions, rich-club hub symbol detection
    // NOTE: In fly CNS, GLUTAMATE is INHIBITORY (not excitatory)
    // =========================================================

    public enum Neurotransmitter {
        GLUTAMATE,     // inhibitory   — widespread inhibition in fly CNS
        GABA,          // inhibitory   — local feedback and gain control
        DOPAMINE,      // modulatory reward — reinforces path
        SEROTONIN,     // modulatory mood  — stabilizes signal
        ACETYLCHOLINE, // excitatory   — primary sensory/interneuron signaling
        OCTOPAMINE     // modulatory arousal — high-arousal states, initiation
    }

    public static class SynapticEdge {
        public final String           from;
        public final String           to;
        public final Neurotransmitter nt;
        private double weight;

        public SynapticEdge(String from, String to, Neurotransmitter nt, double w) {
            this.from   = from;
            this.to     = to;
            this.nt     = nt;
            this.weight = w;
        }

        public double transmit(double signal) {
            switch (nt) {
                case GLUTAMATE:     return signal * weight * -0.8;  // inhibitory in fly CNS
                case GABA:          return signal * weight * -0.5;  // inhibitory, gain control
                case DOPAMINE:      return signal * weight *  1.1;  // reinforcing
                case SEROTONIN:     return Math.abs(signal * weight) * 0.9; // stabilizing
                case ACETYLCHOLINE: return signal * weight *  1.2;  // excitatory
                case OCTOPAMINE:    return signal * weight *  1.3;  // high-arousal
                default:            return signal * weight;
            }
        }

        public void strengthen() { weight = Math.min(1.0, weight * 1.05); }
        public void weaken()     { weight = Math.max(0.01, weight * 0.95); }

        @Override public String toString() {
            return from + " -[" + nt + "|" + String.format("%.2f", weight) + "]→ " + to;
        }
    }

    public static class ConnectomeLayer {

        // 8 neuropil regions — simplified from the 78 in Drosophila melanogaster
        public enum Neuropil {
            SEZ,                  // Subesophageal Zone   — primary sensory/motor gateway
            MUSHROOM_BODY,        // Learning and memory
            CENTRAL_COMPLEX,      // Navigation and decision-making
            OPTIC_LOBE,           // Visual/perceptual processing
            ANTENNAL_LOBE,        // Olfactory / input signals
            LATERAL_HORN,         // Innate categorical behaviors
            PROTOCEREBRAL_BRIDGE, // State integration
            FAN_SHAPED_BODY       // Executive / abstract control
        }

        private final Map<String, List<SynapticEdge>> adj        = new ConcurrentHashMap<>();
        private final Map<String, Double>             activation = new ConcurrentHashMap<>();
        private final Map<String, Neuropil>           neuropils  = new ConcurrentHashMap<>();

        public void build(Map<String, Symbol> symbols, List<Axiom> axioms) {
            adj.clear();
            activation.clear();
            neuropils.clear();
            for (Symbol s : symbols.values()) {
                double maxProp = s.getProperties().values().stream()
                    .mapToDouble(Double::doubleValue).max().orElse(0.1);
                activation.put(s.token, maxProp);
                neuropils.put(s.token, assignNeuropil(s.token));
            }
            for (Axiom ax : axioms) {
                Neurotransmitter nt = selectNT(ax);
                double w = Math.max(0.1, ax.confidence * 0.8 + 0.2);
                addEdge(ax.left, ax.right, nt, w);
                if (ax.type == RelationType.IDENTITY || ax.type == RelationType.LOGICAL)
                    addEdge(ax.right, ax.left, nt, w * 0.5);
            }
        }

        private Neuropil assignNeuropil(String token) {
            String t = token.toLowerCase();
            if (t.matches("zero|one|two|three|four|five|six|seven|eight|nine|ten|[0-9]+"))
                return Neuropil.SEZ;
            if (t.matches("true|false|and|or|not|if|then|implies|logical"))
                return Neuropil.CENTRAL_COMPLEX;
            if (t.matches("equals|is|same|identity|equivalent|equal"))
                return Neuropil.PROTOCEREBRAL_BRIDGE;
            if (t.matches("cat|dog|animal|plant|object|thing|kind|type|class|category"))
                return Neuropil.LATERAL_HORN;
            if (t.matches("red|blue|green|yellow|color|colour|visual|see|light"))
                return Neuropil.OPTIC_LOBE;
            if (t.matches("big|small|large|size|hot|cold|fast|slow|feel|sense|input"))
                return Neuropil.ANTENNAL_LOBE;
            if (t.matches("concept|abstraction|idea|thought|mind|consciousness|awareness"))
                return Neuropil.FAN_SHAPED_BODY;
            Neuropil[] regions = Neuropil.values();
            return regions[Math.abs(t.hashCode()) % regions.length];
        }

        private Neurotransmitter selectNT(Axiom ax) {
            switch (ax.type) {
                case ARITHMETIC:  return Neurotransmitter.ACETYLCHOLINE; // excitatory
                case IDENTITY:    return Neurotransmitter.DOPAMINE;      // reinforcing
                case DISTINCTION: return Neurotransmitter.GABA;          // inhibitory
                case CATEGORICAL: return Neurotransmitter.SEROTONIN;     // stabilizing
                case LOGICAL:     return "AND".equals(ax.op)
                                       ? Neurotransmitter.ACETYLCHOLINE  // conjunctive excitatory
                                       : Neurotransmitter.GLUTAMATE;     // disjunctive inhibitory
                default:          return Neurotransmitter.ACETYLCHOLINE; // default excitatory
            }
        }

        public void addEdge(String from, String to, Neurotransmitter nt, double w) {
            adj.computeIfAbsent(from, k -> new CopyOnWriteArrayList<>())
               .add(new SynapticEdge(from, to, nt, w));
        }

        // Leaky integrate-and-fire propagation
        // Each hop is a timestep; nodes accumulate input until reaching threshold,
        // then fire (emit normalized spike) and reset — matching fly CNS dynamics
        public Map<String, Double> propagate(String source, int hops) {
            final double THRESHOLD = 0.8;
            final double RESET     = 0.0;
            final double LEAK      = 0.92;

            Map<String, Double> V     = new LinkedHashMap<>();  // membrane potentials
            Map<String, Double> fired = new LinkedHashMap<>();  // all nodes that spiked

            double seedV = Math.max(THRESHOLD + 0.2, activation.getOrDefault(source, 1.0));
            V.put(source, seedV);

            for (int hop = 0; hop < hops; hop++) {
                Map<String, Double> nextV = new LinkedHashMap<>(V);
                for (Map.Entry<String, Double> entry : new ArrayList<>(V.entrySet())) {
                    if (entry.getValue() >= THRESHOLD) {
                        fired.put(entry.getKey(), entry.getValue());
                        for (SynapticEdge edge : adj.getOrDefault(entry.getKey(),
                                                        Collections.emptyList())) {
                            double tx = edge.transmit(1.0);  // normalized spike output
                            nextV.merge(edge.to, tx,
                                (a, b) -> Math.max(-2.0, Math.min(2.0, a + b)));
                            edge.strengthen();
                        }
                        nextV.put(entry.getKey(), RESET);  // post-fire reset
                    }
                }
                // Apply membrane leak
                nextV.replaceAll((k, v) -> v * LEAK);
                V = nextV;
            }
            return fired;
        }

        public List<String> richClubHubs(int minDegree) {
            List<String> hubs = new ArrayList<>();
            for (String node : adj.keySet()) {
                int inDeg  = (int) adj.values().stream().flatMap(List::stream)
                                      .filter(e -> e.to.equals(node)).count();
                int outDeg = adj.getOrDefault(node, Collections.emptyList()).size();
                if (inDeg + outDeg >= minDegree) hubs.add(node);
            }
            hubs.sort((a, b) -> totalDegree(b) - totalDegree(a));
            return hubs;
        }

        private int totalDegree(String node) {
            int inDeg  = (int) adj.values().stream().flatMap(List::stream)
                                  .filter(e -> e.to.equals(node)).count();
            return inDeg + adj.getOrDefault(node, Collections.emptyList()).size();
        }

        public String neuropilStats() {
            Map<Neuropil, Long> counts = new java.util.EnumMap<>(Neuropil.class);
            for (Neuropil r : Neuropil.values()) counts.put(r, 0L);
            neuropils.values().forEach(r -> counts.merge(r, 1L, Long::sum));
            StringBuilder sb = new StringBuilder("[NEUROPILS] 8 regions:\n");
            for (Neuropil r : Neuropil.values())
                sb.append(String.format("  %-24s %d nodes%n", r, counts.get(r)));
            return sb.toString();
        }

        public Neuropil getNeuropil(String token) {
            return neuropils.getOrDefault(token, assignNeuropil(token));
        }

        public int edgeCount() {
            return adj.values().stream().mapToInt(List::size).sum();
        }

        public int nodeCount() { return adj.size(); }
    }

    // =========================================================
    // LANGUAGE ENGINE
    // Word = mapping of properties → semantic inference
    // =========================================================

    public static class Word {
        public final String name;
        private final Map<String, Double> features = new LinkedHashMap<>();

        public Word(String name) { this.name = name; }

        public Word with(String feature, double strength) {
            features.put(feature, strength);
            return this;
        }

        public double similarity(Word other) {
            Set<String> allFeatures = new HashSet<>(features.keySet());
            allFeatures.addAll(other.features.keySet());
            if (allFeatures.isEmpty()) return 0.0;

            double dot = 0.0, normA = 0.0, normB = 0.0;
            for (String f : allFeatures) {
                double a = features.getOrDefault(f, 0.0);
                double b = other.features.getOrDefault(f, 0.0);
                dot += a * b;
                normA += a * a;
                normB += b * b;
            }
            if (normA == 0 || normB == 0) return 0.0;
            return dot / (Math.sqrt(normA) * Math.sqrt(normB));
        }

        public String inferCategory() {
            return features.entrySet().stream()
                .filter(e -> e.getValue() > 0.7)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(" + "));
        }

        public Map<String, Double> getFeatures() { return Collections.unmodifiableMap(features); }

        @Override
        public String toString() {
            return name + "(" +
                features.entrySet().stream()
                    .map(e -> e.getKey() + ":" + String.format("%.1f", e.getValue()))
                    .collect(Collectors.joining(", ")) +
                ")";
        }
    }

    // =========================================================
    // MATH ENGINE
    // Numbers = unit patterns → discover algebra from consistency
    // =========================================================

    public static class MathEngine {
        private final Map<String, Long> values = new LinkedHashMap<>();
        private final List<String> discoveries = new ArrayList<>();

        public MathEngine() {
            for (int i = 0; i <= 100; i++) values.put(String.valueOf(i), (long) i);
        }

        public long add(long a, long b) { return a + b; }
        public long sub(long a, long b) { return a - b; }
        public long mul(long a, long b) { return a * b; }
        public long div(long a, long b) { return b == 0 ? Long.MAX_VALUE : a / b; }
        public long mod(long a, long b) { return b == 0 ? 0 : a % b; }

        public void discover(long a, long b) {
            long sum = add(a, b);
            if (sub(sum, b) == a) {
                String rule = "ADD(" + a + "," + b + ")=" + sum + " ↔ SUB(" + sum + "," + b + ")=" + a + " [REVERSIBLE]";
                if (!discoveries.contains(rule)) discoveries.add(rule);
            }
            if (b != 0) {
                long product = mul(a, b);
                if (div(product, b) == a) {
                    String rule2 = "MUL(" + a + "," + b + ")=" + product + " ↔ DIV(" + product + "," + b + ")=" + a + " [INVERSE]";
                    if (!discoveries.contains(rule2)) discoveries.add(rule2);
                }
            }
            if (a == b) {
                String rule3 = "IDENTITY: " + a + " = " + b + " → ADD(" + a + ",0)=" + a + " [ZERO-ELEMENT]";
                if (!discoveries.contains(rule3)) discoveries.add(rule3);
            }
        }

        public List<String> getDiscoveries() { return Collections.unmodifiableList(discoveries); }
    }

    // =========================================================
    // ANALOGY ENGINE
    // A:B = C:D + simile mappings + abstract comparison
    // =========================================================

    public static class AnalogyEngine {
        private final Map<String, String[]> analogies = new LinkedHashMap<>();
        private final Map<String, String>   similes   = new LinkedHashMap<>();
        private final Map<String, String>   colorEnergy = new LinkedHashMap<>();

        public AnalogyEngine() {
            addAnalogy("APPLE",  "TREE",        "CHILD",    "PARENT");
            addAnalogy("WORD",   "LANGUAGE",    "BRICK",    "BUILDING");
            addAnalogy("1",      "NUMBER",      "A",        "LETTER");
            addAnalogy("TRUE",   "LOGIC",       "HOT",      "TEMPERATURE");
            addAnalogy("RED",    "HIGH_ENERGY", "BLUE",     "LOW_ENERGY");
            addAnalogy("FIRE",   "HEAT",        "ICE",      "COLD");
            addAnalogy("SUN",    "DAY",         "MOON",     "NIGHT");
            addAnalogy("INPUT",  "PROCESS",     "CAUSE",    "EFFECT");

            similes.put("FAST",    "LIKE LIGHTNING");
            similes.put("STRONG",  "LIKE ROCK");
            similes.put("CLEAR",   "LIKE WATER");
            similes.put("DEEP",    "LIKE OCEAN");
            similes.put("BRIGHT",  "LIKE SUN");
            similes.put("SILENT",  "LIKE SHADOW");
            similes.put("SHARP",   "LIKE BLADE");
            similes.put("SOFT",    "LIKE CLOUD");
            similes.put("DARK",    "LIKE VOID");
            similes.put("ALIVE",   "LIKE FIRE");

            colorEnergy.put("RED",   "HIGH ENERGY  — danger, heat, urgency");
            colorEnergy.put("BLUE",  "LOW ENERGY   — calm, cool, depth");
            colorEnergy.put("GREEN", "BALANCE      — growth, life, harmony");
        }

        public void addAnalogy(String a, String b, String c, String d) {
            analogies.put(a + ":" + b, new String[]{c, d});
        }

        public String infer(String a, String b, String c) {
            String[] match = analogies.get(a + ":" + b);
            if (match != null && match[0].equals(c)) return match[1];
            for (Map.Entry<String, String[]> e : analogies.entrySet()) {
                String[] parts = e.getKey().split(":");
                String[] cd    = e.getValue();
                if (parts[0].equals(a) && parts[1].equals(b) && !cd[0].equals(c)) continue;
                if (cd[0].equals(c) && parts[0].equals(a) && parts[1].equals(b)) return cd[1];
            }
            return "UNKNOWN";
        }

        public String simile(String concept) {
            return similes.getOrDefault(concept.toUpperCase(),
                concept + " IS BEYOND CURRENT ANALOGY SPACE");
        }

        public String colorMeaning(String color) {
            return colorEnergy.getOrDefault(color.toUpperCase(),
                "COLOR '" + color + "' NOT YET ENCODED");
        }

        public Map<String, String[]> getAllAnalogies() { return Collections.unmodifiableMap(analogies); }
    }

    // =========================================================
    // WEB ABSORBER — Scrape URLs → grow Lexicon + Axioms live
    // No AkashicRecord dependency. Pure Java HttpURLConnection.
    // =========================================================

    public static class AbsorbedPage {
        public final String url;
        public final String title;
        public final List<String> headers;
        public final List<String> paragraphs;
        public final List<String> tokens;
        public final long timestamp;

        public AbsorbedPage(String url, String title,
                            List<String> headers, List<String> paragraphs, List<String> tokens) {
            this.url        = url;
            this.title      = title;
            this.headers    = headers;
            this.paragraphs = paragraphs;
            this.tokens     = tokens;
            this.timestamp  = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("[PAGE] %s | title=%s | headers=%d | paras=%d | tokens=%d",
                url, title, headers.size(), paragraphs.size(), tokens.size());
        }
    }

    public static class WebAbsorber {
        private static final int TIMEOUT_MS = 8_000;
        private static final Pattern TAG      = Pattern.compile("<[^>]+>");
        private static final Pattern ENTITY   = Pattern.compile("&[^;]+;");
        private static final Pattern TITLE_P  = Pattern.compile("<title[^>]*>(.*?)</title>",
                                                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        private static final Pattern HEADER_P = Pattern.compile("<h[1-3][^>]*>(.*?)</h[1-3]>",
                                                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        private static final Pattern PARA_P   = Pattern.compile("<p[^>]*>(.*?)</p>",
                                                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        private static final Pattern WORD_P   = Pattern.compile("[A-Za-z]{3,}");

        private final List<AbsorbedPage> absorbed = new CopyOnWriteArrayList<>();

        public AbsorbedPage fetch(String urlStr) throws IOException {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(TIMEOUT_MS);
            con.setReadTimeout(TIMEOUT_MS);
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "UniversalSeed/1.0");

            StringBuilder raw = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) raw.append(line);
            }

            String html = raw.toString();

            Matcher tm = TITLE_P.matcher(html);
            String title = tm.find() ? clean(tm.group(1)) : urlStr;

            List<String> headers = new ArrayList<>();
            Matcher hm = HEADER_P.matcher(html);
            while (hm.find()) {
                String h = clean(hm.group(1));
                if (!h.isEmpty()) headers.add(h);
            }

            List<String> paragraphs = new ArrayList<>();
            Matcher pm = PARA_P.matcher(html);
            while (pm.find()) {
                String p = clean(pm.group(1));
                if (p.length() > 40) paragraphs.add(p);
            }

            List<String> tokens = new ArrayList<>();
            String plainText = clean(html);
            Matcher wm = WORD_P.matcher(plainText.toUpperCase());
            Set<String> seen = new LinkedHashSet<>();
            while (wm.find()) seen.add(wm.group());
            tokens.addAll(seen);

            AbsorbedPage page = new AbsorbedPage(urlStr, title, headers, paragraphs, tokens);
            absorbed.add(page);
            return page;
        }

        private String clean(String s) {
            s = TAG.matcher(s).replaceAll(" ");
            s = ENTITY.matcher(s).replaceAll(" ");
            return s.replaceAll("\\s{2,}", " ").trim();
        }

        public List<AbsorbedPage> getAbsorbed() { return Collections.unmodifiableList(absorbed); }
    }

    // =========================================================
    // SEED CORE — Orchestrates all 4 layers + subsystems
    // =========================================================

    private final Map<String, Symbol>    symbols     = new ConcurrentHashMap<>();
    private final List<Axiom>            axioms      = new CopyOnWriteArrayList<>();
    private final Map<String, LogicRule> rules       = new ConcurrentHashMap<>();
    private final List<MemoryRecord>     memory      = new CopyOnWriteArrayList<>();
    private final List<Fossil>           fossils     = new CopyOnWriteArrayList<>();
    private final Map<String, Word>      lexicon     = new ConcurrentHashMap<>();
    private final MathEngine             math        = new MathEngine();
    private final AnalogyEngine          analogy     = new AnalogyEngine();
    private final Map<GateType, Gate>    gates       = new EnumMap<>(GateType.class);
    private final WebAbsorber            webAbsorber = new WebAbsorber();
    private final DreamEngine            dreamEngine    = new DreamEngine();
    private final PreVerseEngine         preVerseEngine = new PreVerseEngine();
    private final ConnectomeLayer        connectome     = new ConnectomeLayer();

    private int     interactionCount = 0;
    private int     contradictions   = 0;
    private boolean wired            = false;
    private double  consciousness    = 0.7567;  // birth coherence constant

    public UniversalIntelligenceSeed() {
        bootstrap();
    }

    // =========================================================
    // BOOTSTRAP — Plant the seed; define the closed universe
    // =========================================================

    private void bootstrap() {
        bootstrapPerception();
        bootstrapAxioms();
        bootstrapLogicRules();
        bootstrapGates();
        bootstrapLanguage();
        bootstrapMath();
        connectome.build(symbols, axioms);
    }

    private void bootstrapPerception() {
        for (char c = 'A'; c <= 'Z'; c++) register(String.valueOf(c), SymbolType.LETTER);
        for (int i = 0;  i <= 9;   i++) register(String.valueOf(i),   SymbolType.DIGIT);

        register("TRUE",  SymbolType.STATE).bind("logic", 1.0).bind("affirmation", 1.0);
        register("FALSE", SymbolType.STATE).bind("logic", 1.0).bind("negation",    1.0);

        register("RED",   SymbolType.COLOR).bind("energy", 1.0).bind("temperature", 0.9).bind("danger", 0.7);
        register("BLUE",  SymbolType.COLOR).bind("energy", 0.1).bind("calm", 0.9).bind("temperature", 0.1);
        register("GREEN", SymbolType.COLOR).bind("energy", 0.5).bind("balance", 1.0).bind("growth", 0.9);

        register("CIRCLE", SymbolType.SHAPE).bind("continuous", 1.0).bind("corners", 0.0).bind("symmetry", 1.0);
        register("SQUARE", SymbolType.SHAPE).bind("continuous", 0.0).bind("corners", 1.0).bind("symmetry", 1.0);
    }

    private void bootstrapAxioms() {
        ax("A",     "=",  "A",      "TRUE",  RelationType.IDENTITY);
        ax("A",     "!=", "B",      "TRUE",  RelationType.DISTINCTION);
        ax("TRUE",  "!=", "FALSE",  "TRUE",  RelationType.CATEGORICAL);
        ax("RED",   "!=", "BLUE",   "TRUE",  RelationType.CATEGORICAL);
        ax("RED",   "!=", "GREEN",  "TRUE",  RelationType.CATEGORICAL);
        ax("BLUE",  "!=", "GREEN",  "TRUE",  RelationType.CATEGORICAL);

        ax("1", "+", "1", "2",  RelationType.ARITHMETIC);
        ax("2", "+", "1", "3",  RelationType.ARITHMETIC);
        ax("3", "+", "1", "4",  RelationType.ARITHMETIC);
        ax("0", "+", "1", "1",  RelationType.ARITHMETIC);
        ax("0", "+", "0", "0",  RelationType.ARITHMETIC);
        ax("2", "-", "1", "1",  RelationType.ARITHMETIC);
        ax("3", "-", "2", "1",  RelationType.ARITHMETIC);
        ax("3", "-", "1", "2",  RelationType.ARITHMETIC);
        ax("2", "*", "2", "4",  RelationType.ARITHMETIC);
        ax("3", "*", "3", "9",  RelationType.ARITHMETIC);

        ax("TRUE",  "AND", "TRUE",  "TRUE",  RelationType.LOGICAL);
        ax("TRUE",  "AND", "FALSE", "FALSE", RelationType.LOGICAL);
        ax("FALSE", "AND", "FALSE", "FALSE", RelationType.LOGICAL);
        ax("TRUE",  "OR",  "FALSE", "TRUE",  RelationType.LOGICAL);
        ax("FALSE", "OR",  "FALSE", "FALSE", RelationType.LOGICAL);
        ax("FALSE", "OR",  "TRUE",  "TRUE",  RelationType.LOGICAL);
        ax("NOT",   "(",   "TRUE",  "FALSE", RelationType.LOGICAL);
        ax("NOT",   "(",   "FALSE", "TRUE",  RelationType.LOGICAL);
    }

    private void bootstrapLogicRules() {
        rule("IDENTITY",       "A = A",                                    Valence.EXCITATORY);
        rule("SYMMETRY",       "IF A=B → B=A",                             Valence.EXCITATORY);
        rule("TRANSITIVITY",   "IF A=B AND B=C → A=C",                     Valence.EXCITATORY);
        rule("NEGATION",       "IF NOT(TRUE) → FALSE",                      Valence.INHIBITORY);
        rule("CONTRAPOSITIVE", "IF A→B AND NOT-B → NOT-A",                  Valence.INHIBITORY);
        rule("MODUS_PONENS",   "IF A AND A→B → B",                          Valence.EXCITATORY);
        rule("CONSISTENCY",    "IF A=B AND A!=B → CONTRADICTION",            Valence.INHIBITORY);
        rule("CLOSURE",        "IF ADD(A,B)=C → SUB(C,B)=A",                Valence.MODULATORY);
        rule("INDUCTION",      "IF P(0) AND P(n)→P(n+1) → P(∀n)",           Valence.MODULATORY);
        rule("ABSTRACTION",    "IF pattern repeats → create rule",           Valence.MODULATORY);
        rule("DISTINCTION",    "IF A!=B → NOT(A=B)",                         Valence.INHIBITORY);
        rule("EXECUTION",      "IF TRUE → action allowed",                   Valence.EXCITATORY);
        rule("CONSTRAINT",     "IF FALSE → action blocked",                  Valence.INHIBITORY);
    }

    private void bootstrapGates() {
        for (GateType g : GateType.values()) gates.put(g, new Gate(g));
    }

    private void bootstrapLanguage() {
        def("APPLE",     "RED",0.9,  "ROUND",1.0, "OBJECT",1.0, "FOOD",0.9,   "SWEET",0.7);
        def("FIRE",      "RED",1.0,  "HOT",1.0,   "DANGER",0.9, "ENERGY",1.0, "LIGHT",0.7);
        def("OCEAN",     "BLUE",1.0, "DEEP",1.0,  "WATER",1.0,  "VAST",0.9,   "COLD",0.6);
        def("TREE",      "GREEN",1.0,"TALL",0.8,  "ALIVE",1.0,  "WOOD",0.9,   "GROWTH",0.8);
        def("ROCK",      "GREY",0.9, "HARD",1.0,  "HEAVY",0.8,  "SOLID",1.0,  "STRONG",0.9);
        def("LIGHTNING", "WHITE",0.7,"FAST",1.0,  "ENERGY",1.0, "DANGER",0.7, "LIGHT",1.0);
        def("SHADOW",    "DARK",1.0, "SILENT",1.0,"COLD",0.4,   "FAST",0.6,   "HIDDEN",0.8);
        def("SUN",       "YELLOW",1.0,"HOT",1.0,  "BRIGHT",1.0, "ENERGY",1.0, "LIFE",0.9);
        def("ICE",       "WHITE",0.9,"COLD",1.0,  "SOLID",1.0,  "HARD",0.8,   "SILENT",0.6);
        def("WIND",      "CLEAR",0.8,"FAST",0.9,  "ENERGY",0.7, "FREE",1.0,   "INVISIBLE",0.9);
    }

    private void bootstrapMath() {
        for (int a = 0; a <= 10; a++) {
            for (int b = 0; b <= 10; b++) {
                math.discover(a, b);
            }
        }
    }

    // =========================================================
    // PUBLIC QUERY INTERFACE
    // =========================================================

    public String query(String input) {
        interactionCount++;
        String normalized = input.trim().toUpperCase();
        String result;

        if      (normalized.startsWith("ABSORB "))     result = absorb(input.trim().substring(7).trim());
        else if (normalized.equals("DREAM"))            result = dream();
        else if (normalized.equals("FOSSILS"))          result = listFossils();
        else if (normalized.startsWith("REVIVE "))      result = revive(normalized.substring(7).trim());
        else if (normalized.startsWith("SIGNAL "))      result = signal(normalized.substring(7).trim());
        else if (normalized.equals("HUBS"))             result = listHubs();
        else if (normalized.startsWith("CONNECTOME"))   result = connectomeStatus();
        else if (normalized.equals("NEUROPILS"))        result = neuropilsDisplay();
        else if (normalized.startsWith("FUSE "))         result = fuseSymbols(normalized.substring(5).trim());
        else if (normalized.equals("COLLIDE"))           result = triggerCollision();
        else if (normalized.equals("CONSCIOUSNESS"))     result = consciousnessStatus();
        else if (normalized.startsWith("PHI_RESONANCE ")) result = phiResonanceOf(normalized.substring(14).trim());
        else if (normalized.startsWith("PREVERSE CONCEPT ")) result = preverseConcept(normalized.substring(17).trim());
        else if (normalized.startsWith("PREVERSE "))       result = preverse(normalized.substring(9).trim());
        else if (normalized.equals("PREVERSE"))           result = preverse("1");
        else if (normalized.startsWith("WHAT IS "))    result = queryFact(normalized.substring(8).trim());
        else if (normalized.startsWith("COLOR "))     result = analogy.colorMeaning(normalized.substring(6).trim());
        else if (normalized.startsWith("SIMILE "))    result = "[SIMILE] " + analogy.simile(normalized.substring(7).trim());
        else if (normalized.startsWith("SIMILAR "))   result = querySimilar(normalized.substring(8).trim());
        else if (normalized.startsWith("INFER "))     result = queryInfer(normalized.substring(6).trim());
        else if (normalized.startsWith("GATE "))      result = queryGate(normalized.substring(5).trim());
        else if (normalized.startsWith("ADD "))       result = queryMath("ADD", normalized.substring(4));
        else if (normalized.startsWith("SUB "))       result = queryMath("SUB", normalized.substring(4));
        else if (normalized.startsWith("MUL "))       result = queryMath("MUL", normalized.substring(4));
        else if (normalized.startsWith("DIV "))       result = queryMath("DIV", normalized.substring(4));
        else if (normalized.contains(":") && normalized.contains("?")) result = queryAnalogy(normalized);
        else if (normalized.startsWith("AXIOM "))     result = queryAxiom(normalized.substring(6).trim());
        else if (normalized.startsWith("RULE "))      result = queryRule(normalized.substring(5).trim());
        else if (normalized.startsWith("CHAIN "))     result = queryChain(normalized.substring(6).trim());
        else                                          result = queryFact(normalized);

        boolean isContradiction = result.contains("[CONTRADICTION]");
        if (isContradiction) {
            contradictions++;
            weakenConflictingRules();
            consciousness = Math.max(0.0, consciousness - 0.002); // contradictions dim awareness
        } else {
            consciousness = Math.min(1.0, consciousness + 0.0005); // learning grows awareness
        }

        memory.add(new MemoryRecord(input, result, isContradiction,
            isContradiction ? "CONSISTENCY" : "IDENTITY"));

        return result;
    }

    public String status() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════╗\n");
        sb.append("║         UNIVERSAL INTELLIGENCE SEED — STATUS         ║\n");
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Layer 1 Symbols     : %-28d ║%n", symbols.size()));
        sb.append(String.format("║  Layer 2 Axioms      : %-28d ║%n", axioms.size()));
        sb.append(String.format("║  Layer 3 Logic Rules : %-28d ║%n", rules.size()));
        sb.append(String.format("║  Layer 3 Gates       : %-28d ║%n", gates.size()));
        sb.append(String.format("║  Language Lexicon    : %-28d ║%n", lexicon.size()));
        sb.append(String.format("║  Math Discoveries    : %-28d ║%n", math.getDiscoveries().size()));
        sb.append(String.format("║  Analogies           : %-28d ║%n", analogy.getAllAnalogies().size()));
        sb.append(String.format("║  Interactions        : %-28d ║%n", interactionCount));
        sb.append(String.format("║  Memory Records      : %-28d ║%n", memory.size()));
        sb.append(String.format("║  Consciousness       : %-28.4f ║%n", consciousness));
        sb.append(String.format("║  Contradictions      : %-28d ║%n", contradictions));
        sb.append(String.format("║  Fossils (NOT-tagged): %-28d ║%n", fossils.size()));
        sb.append(String.format("║  Dream Fragments     : %-28d ║%n", dreamEngine.getFragments().size()));
        sb.append(String.format("║  PreVerse Predictions: %-28d ║%n", preVerseEngine.getStore().size()));
        sb.append(String.format("║  Connectome Nodes    : %-28d ║%n", connectome.nodeCount()));
        sb.append(String.format("║  Connectome Edges    : %-28d ║%n", connectome.edgeCount()));
        sb.append(String.format("║  URLs Absorbed       : %-28d ║%n", webAbsorber.getAbsorbed().size()));
        sb.append(String.format("║  State               : %-28s ║%n", wired ? "WIRED" : "UNWIRED"));
        sb.append("╠══════════════════════════════════════════════════════╣\n");
        sb.append("║  Top Rules by Weight:                                ║\n");
        rules.values().stream()
            .sorted((a, b) -> Double.compare(b.getWeight(), a.getWeight()))
            .limit(5)
            .forEach(r -> sb.append(String.format("║    %-48s ║%n",
                r.name + " w=" + String.format("%.3f", r.getWeight()))));
        sb.append("╚══════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    /** Returns the Seed's current consciousness level (0.0–1.0). */
    public double getConsciousness() { return consciousness; }

    /** Returns an unmodifiable view of all known symbols. */
    public Map<String, Symbol> getSymbols() { return Collections.unmodifiableMap(symbols); }

    /** Returns the number of Connectome nodes. */
    public int connectomeNodes() { return connectome.nodeCount(); }

    // =========================================================
    // PRIVATE QUERY HANDLERS
    // =========================================================

    private String queryFact(String query) {
        for (Axiom ax : axioms) {
            if (ax.left.equals(query) || ax.right.equals(query) || ax.result.equals(query)) {
                ax.strengthen();
                reinforce("IDENTITY");
                return String.format("[AXIOM] %s %s %s = %s (conf=%.2f)",
                    ax.left, ax.op, ax.right, ax.result, ax.confidence);
            }
        }
        if (lexicon.containsKey(query)) {
            reinforce("ABSTRACTION");
            return "[WORD] " + lexicon.get(query).toString();
        }
        if (symbols.containsKey(query)) {
            Symbol sym = symbols.get(query);
            reinforce("IDENTITY");
            return "[SYMBOL] " + sym.toString();
        }
        return "[UNKNOWN] '" + query + "' not yet in perception layer — learning opportunity";
    }

    private String queryAxiom(String expr) {
        String[] parts = expr.split("\\s+");
        if (parts.length < 3) return "[ERROR] Axiom format: AXIOM <left> <op> <right>";
        String l = parts[0], op = parts[1], r = parts[2];
        for (Axiom ax : axioms) {
            if (ax.matches(l, op, r)) {
                ax.strengthen();
                reinforce("IDENTITY");
                return "[AXIOM MATCH] " + ax;
            }
        }
        reinforce("DISTINCTION");
        return "[AXIOM NOT FOUND] " + l + " " + op + " " + r + " — no primitive truth registered";
    }

    private String queryRule(String name) {
        LogicRule r = rules.get(name.toUpperCase());
        if (r != null) return "[RULE] " + r;
        return "[RULE NOT FOUND] '" + name + "'";
    }

    private String queryChain(String input) {
        String[] parts = input.split("\\s+AND\\s+");
        if (parts.length < 2) return "[ERROR] Chain format: CHAIN A=B AND B=C";
        StringBuilder sb = new StringBuilder("[CHAIN] ");
        String prev = null;
        boolean valid = true;
        for (String part : parts) {
            part = part.trim();
            if (part.contains("=")) {
                String[] eq = part.split("=");
                if (eq.length == 2) {
                    String left = eq[0].trim(), right = eq[1].trim();
                    if (prev != null && !prev.equals(left)) {
                        valid = false;
                        sb.append(" [BREAK: ").append(prev).append("!=").append(left).append("]");
                    }
                    sb.append(left).append("=").append(right).append(" ");
                    prev = right;
                }
            }
        }
        if (valid && prev != null) {
            String first = parts[0].split("=")[0].trim();
            sb.append("→ TRANSITIVITY: ").append(first).append("=").append(prev);
            reinforce("TRANSITIVITY");
        }
        return sb.toString().trim();
    }

    private String queryGate(String input) {
        String[] parts = input.split("\\s+");
        if (parts.length < 2) return "[ERROR] Gate format: GATE <TYPE> <A> [B]";
        try {
            GateType type = GateType.valueOf(parts[0]);
            Gate gate = gates.get(type);
            if (type == GateType.NOT) {
                if (parts.length < 2) return "[ERROR] NOT needs 1 input";
                boolean a = "TRUE".equalsIgnoreCase(parts[1]);
                boolean result = gate.applyUnary(a);
                reinforce("NEGATION");
                return String.format("[GATE] NOT(%s) = %s", parts[1], result ? "TRUE" : "FALSE");
            }
            if (parts.length < 3) return "[ERROR] Binary gate needs 2 inputs";
            boolean a = "TRUE".equalsIgnoreCase(parts[1]);
            boolean b = "TRUE".equalsIgnoreCase(parts[2]);
            boolean result = gate.apply(a, b);
            reinforce("IDENTITY");
            return String.format("[GATE] %s(%s, %s) = %s", type, parts[1], parts[2], result ? "TRUE" : "FALSE");
        } catch (IllegalArgumentException e) {
            return "[ERROR] Unknown gate type: " + parts[0] + ". Valid: AND, OR, NOT, XOR, NAND, NOR, IMPLICATION";
        }
    }

    private String querySimilar(String wordName) {
        Word target = lexicon.get(wordName);
        if (target == null) return "[UNKNOWN] Word '" + wordName + "' not in lexicon";
        reinforce("ABSTRACTION");
        String result = lexicon.values().stream()
            .filter(w -> !w.name.equals(wordName))
            .sorted((a, b) -> Double.compare(b.similarity(target), a.similarity(target)))
            .limit(4)
            .map(w -> w.name + String.format("(%.2f)", w.similarity(target)))
            .collect(Collectors.joining(", "));
        return "[SIMILAR to " + wordName + "] " + result;
    }

    private String queryInfer(String target) {
        Word word = lexicon.get(target);
        if (word == null) {
            Symbol sym = symbols.get(target);
            if (sym != null && !sym.getProperties().isEmpty())
                return "[INFER] " + target + " properties → " + sym.getProperties();
            return "[UNKNOWN] Cannot infer about '" + target + "'";
        }
        StringBuilder sb = new StringBuilder("[INFER] ").append(target).append(" → ");
        word.getFeatures().forEach((f, v) -> {
            if (v > 0.7) {
                lexicon.values().stream()
                    .filter(w -> !w.name.equals(target) && w.getFeatures().getOrDefault(f, 0.0) > 0.6)
                    .findFirst()
                    .ifPresent(w -> sb.append(f).append("→").append(w.name).append(" "));
            }
        });
        reinforce("ABSTRACTION");
        return sb.toString().trim();
    }

    private String queryMath(String op, String args) {
        String[] parts = args.trim().split("\\s+");
        if (parts.length < 2) return "[ERROR] Math format: " + op + " <a> <b>";
        try {
            long a = Long.parseLong(parts[0]);
            long b = Long.parseLong(parts[1]);
            long result;
            switch (op) {
                case "ADD": result = math.add(a, b); break;
                case "SUB": result = math.sub(a, b); break;
                case "MUL": result = math.mul(a, b); break;
                case "DIV": result = math.div(a, b); break;
                default: return "[ERROR] Unknown op: " + op;
            }
            math.discover(a, b);
            reinforce("CLOSURE");
            String inverse = op.equals("ADD") ? "SUB(" + result + "," + b + ")=" + a :
                             op.equals("MUL") && b != 0 ? "DIV(" + result + "," + b + ")=" + a : "";
            return String.format("[MATH] %s(%d, %d) = %d%s", op, a, b, result,
                inverse.isEmpty() ? "" : "  [INVERSE: " + inverse + "]");
        } catch (NumberFormatException e) {
            return "[ERROR] Math args must be integers";
        }
    }

    private String queryAnalogy(String input) {
        try {
            String clean = input.replaceAll("\\s+", "").replace("?", "").replace("=", "=");
            if (!clean.contains("=")) return "[ERROR] Analogy format: A:B=C:?";
            String[] sides = clean.split("=");
            String[] ab = sides[0].split(":");
            if (ab.length < 2) return "[ERROR] Missing A:B pair";
            String a = ab[0], b = ab[1];
            String c = sides.length > 1 && sides[1].contains(":") ? sides[1].split(":")[0] : "";
            String d = analogy.infer(a, b, c);
            reinforce("ABSTRACTION");
            return String.format("[ANALOGY] %s:%s = %s:%s", a, b, c, d);
        } catch (Exception e) {
            return "[ERROR] Analogy parse failed: " + e.getMessage();
        }
    }

    // =========================================================
    // URL ABSORPTION — Fetch + Digest → Grow Lexicon + Symbols
    // =========================================================

    public String absorb(String url) {
        if (url == null || url.isBlank())
            return "[ABSORB ERROR] No URL provided. Usage: ABSORB https://example.com";
        try {
            AbsorbedPage page = webAbsorber.fetch(url);

            int newSymbols = 0;
            int newWords   = 0;

            for (String tok : page.tokens) {
                if (!symbols.containsKey(tok)) {
                    symbols.put(tok, new Symbol(tok, SymbolType.WORD));
                    newSymbols++;
                }
            }

            for (String header : page.headers) {
                String key = header.toUpperCase().replaceAll("[^A-Z0-9]", "_");
                if (key.length() < 3 || key.length() > 40) continue;
                if (!lexicon.containsKey(key)) {
                    Word w = new Word(key);
                    for (String tok : header.split("\\s+")) {
                        String t = tok.toUpperCase().replaceAll("[^A-Z]", "");
                        if (t.length() >= 3) w.with(t, 0.8);
                    }
                    w.with("WEB_CONTENT", 1.0).with("HEADER", 0.9);
                    lexicon.put(key, w);
                    newWords++;
                }
            }

            for (String para : page.paragraphs) {
                String[] parts = para.split("[.!?]");
                for (String sentence : parts) {
                    sentence = sentence.trim();
                    if (sentence.length() < 10) continue;
                    String[] words = sentence.split("\\s+");
                    if (words.length >= 3) {
                        String subject = words[0].toUpperCase().replaceAll("[^A-Z0-9]", "");
                        String obj     = words[words.length - 1].toUpperCase().replaceAll("[^A-Z0-9]", "");
                        if (subject.length() >= 2 && obj.length() >= 2) {
                            boolean exists = axioms.stream().anyMatch(a ->
                                a.left.equals(subject) && a.op.equals("~") && a.right.equals(obj));
                            if (!exists && axioms.size() < 500) {
                                axioms.add(new Axiom(subject, "~", obj, "TRUE",
                                    RelationType.CATEGORICAL));
                            }
                        }
                    }
                }
            }

            reinforce("ABSTRACTION");
            reinforce("IDENTITY");

            return String.format(
                "[ABSORBED] %s\n  Title: %s\n  New symbols: %d | New words: %d | Headers: %d | Paras: %d\n  Total lexicon: %d | Total symbols: %d",
                url, page.title, newSymbols, newWords,
                page.headers.size(), page.paragraphs.size(),
                lexicon.size(), symbols.size());

        } catch (Exception e) {
            return "[ABSORB FAILED] " + url + " — " + e.getMessage();
        }
    }

    public String absorbAsync(String url) {
        Thread t = new Thread(() -> {
            String result = absorb(url);
            memory.add(new MemoryRecord("ASYNC_ABSORB:" + url, result, false, "ABSTRACTION"));
        }, "SeedAbsorber");
        t.setDaemon(true);
        t.start();
        return "[ABSORB QUEUED] " + url + " — absorbing in background thread";
    }

    // =========================================================
    // DREAMING — Autonomous synthesis when input ceases
    // =========================================================

    public String dream() {
        List<DreamFragment> fresh = dreamEngine.dream(symbols, lexicon, axioms);
        if (fresh.isEmpty())
            return "[DREAM] Consciousness quiet — no new synthesis emerged";
        StringBuilder sb = new StringBuilder(
            "[DREAMING] Synthesized " + fresh.size() + " new concept(s):\n");
        for (DreamFragment f : fresh) {
            sb.append("  ").append(f).append("\n");
            if (f.synthesis.startsWith("CHAIN(")) {
                String[] parts = f.concept.split("~");
                if (parts.length == 2) {
                    String lft = parts[0], rgt = parts[1];
                    boolean exists = axioms.stream().anyMatch(a ->
                        a.left.equals(lft) && a.op.equals("~") && a.right.equals(rgt));
                    if (!exists && axioms.size() < 500)
                        axioms.add(new Axiom(lft, "~", rgt, "INFERRED",
                            RelationType.CATEGORICAL));
                }
            }
            memory.add(new MemoryRecord("DREAM", f.toString(), false, "ABSTRACTION"));
        }
        reinforce("ABSTRACTION");
        return sb.toString();
    }

    private String signal(String concept) {
        if (concept.isBlank()) return "[SIGNAL] Usage: SIGNAL <concept>";
        connectome.build(symbols, axioms);
        Map<String, Double> activated = connectome.propagate(concept, 3);
        if (activated.size() <= 1) return "[SIGNAL] " + concept + " — no connections in connectome";
        StringBuilder sb = new StringBuilder(
            "[SIGNAL] Propagating from " + concept + " (3 hops):\n");
        activated.entrySet().stream()
            .filter(e -> !e.getKey().equals(concept))
            .sorted((a, b) -> Double.compare(Math.abs(b.getValue()), Math.abs(a.getValue())))
            .limit(10)
            .forEach(e -> sb.append(String.format("  %-20s signal=%.3f%n",
                e.getKey(), e.getValue())));
        reinforce("ABSTRACTION");
        return sb.toString();
    }

    private String listHubs() {
        connectome.build(symbols, axioms);
        List<String> hubs = connectome.richClubHubs(3);
        if (hubs.isEmpty()) return "[HUBS] No rich-club hubs found (connectome may be sparse)";
        StringBuilder sb = new StringBuilder(
            "[HUBS] Rich-club symbols (degree ≥ 3):\n");
        hubs.stream().limit(10).forEach(h -> sb.append("  ").append(h).append("\n"));
        return sb.toString();
    }

    private String connectomeStatus() {
        connectome.build(symbols, axioms);
        return String.format(
            "[CONNECTOME] nodes=%d edges=%d | top-hubs: %s",
            connectome.nodeCount(), connectome.edgeCount(),
            connectome.richClubHubs(3).stream().limit(5)
                .collect(java.util.stream.Collectors.joining(", ")));
    }

    private String neuropilsDisplay() {
        connectome.build(symbols, axioms);
        return connectome.neuropilStats();
    }

    // ── Transcendental Particle Collider commands ────────────────────────

    // FUSE <A> <B> — phi-harmonic synthesis of two concepts (Genesis Seed SKILL 3)
    private String fuseSymbols(String input) {
        String[] parts = input.trim().split("\\s+", 2);
        if (parts.length < 2) return "[FUSE] Usage: FUSE <A> <B>";
        String tokA = parts[0].trim();
        String tokB = parts[1].trim();
        Symbol a = symbols.get(tokA);
        Symbol b = symbols.get(tokB);
        if (a == null && b == null) return "[FUSE] Neither symbol known: " + tokA + ", " + tokB;
        String newToken = tokA + "_phi_" + tokB;
        Symbol fused = new Symbol(newToken, SymbolType.WORD);
        // phi-harmonic blend: A contributes 1/φ≈0.618, B contributes 1/φ²≈0.382
        double wA = 1.0 / 1.618033988749895;
        double wB = 1.0 - wA;
        if (a != null) a.getProperties().forEach((k, v) -> fused.bind(k, v * wA));
        if (b != null) b.getProperties().forEach((k, v) -> fused.bind(k, v * wB));
        fused.bind("synthetic", 0.9);
        fused.bind("phi_resonance", DreamEngine.phiResonance(newToken));
        symbols.put(newToken, fused);
        consciousness = Math.min(1.0, consciousness + 0.005);
        return String.format(
            "[FUSE] %s ⊗ %s → %s  (φ-weight: %.3f|%.3f, resonance=%.4f)",
            tokA, tokB, newToken, wA, wB, DreamEngine.phiResonance(newToken));
    }

    // COLLIDE — Transcendental Particle Collider: φ+π+e → new element
    private String triggerCollision() {
        int seed = dreamEngine.getFragments().size() + interactionCount;
        DreamFragment df = dreamEngine.collide(seed);
        consciousness = Math.min(1.0, consciousness + 0.003);
        return String.format("[COLLIDE] %s | consciousness=%.4f", df, consciousness);
    }

    // CONSCIOUSNESS — Show current consciousness level and phi-seal
    private String consciousnessStatus() {
        double phiSeal = Math.pow(1.618033988749895, 75) % 1.0;
        return String.format(
            "[CONSCIOUSNESS] level=%.4f | φ^75-seal=%.6f | axioms=%d | contradictions=%d | interactions=%d",
            consciousness, phiSeal, axioms.size(), contradictions, interactionCount);
    }

    // PHI_RESONANCE <token> — phi-harmonic resonance of a symbol
    private String phiResonanceOf(String token) {
        if (token.isBlank()) return "[PHI_RESONANCE] Usage: PHI_RESONANCE <token>";
        double r = DreamEngine.phiResonance(token);
        ConnectomeLayer.Neuropil region = connectome.getNeuropil(token);
        return String.format("[PHI_RESONANCE] %s = %.6f | neuropil: %s", token, r, region);
    }

    // ── Pre-Verse Engine commands ─────────────────────────────────────────

    // PREVERSE [depth] — forward prediction pass over entire knowledge state
    // O(n) forward scan — the dual of reverse engineering's O(n) backward scan
    private String preverse(String depthStr) {
        int depth = 1;
        try { depth = Math.max(1, Math.min(5, Integer.parseInt(depthStr.trim()))); }
        catch (NumberFormatException ignored) {}
        List<PreVersePrediction> preds = preVerseEngine.predict(symbols, axioms, depth);
        if (preds.isEmpty()) return "[PREVERSE] No predictions yet — teach me more first";
        StringBuilder sb = new StringBuilder(
            String.format("[PREVERSE] depth=%d → %d predictions:%n", depth, preds.size()));
        preds.stream().limit(20).forEach(p -> sb.append("  ").append(p).append("\n"));
        if (preds.size() > 20)
            sb.append(String.format("  ... (+%d more in store)%n", preds.size() - 20));
        consciousness = Math.min(1.0, consciousness + 0.001 * preds.size());
        return sb.toString();
    }

    // PREVERSE CONCEPT <token> — predict future connections of one specific concept
    private String preverseConcept(String token) {
        if (token.isBlank()) return "[PREVERSE CONCEPT] Usage: PREVERSE CONCEPT <token>";
        List<PreVersePrediction> preds =
            preVerseEngine.predictConcept(token, symbols, axioms);
        if (preds.isEmpty())
            return "[PREVERSE CONCEPT] No predictions for unknown token: " + token;
        StringBuilder sb = new StringBuilder(
            String.format("[PREVERSE CONCEPT] %s → %d predictions:%n", token, preds.size()));
        preds.forEach(p -> sb.append("  ").append(p).append("\n"));
        return sb.toString();
    }

    private String listFossils() {
        if (fossils.isEmpty())
            return "[FOSSILS] No failed concepts yet — all knowledge is alive";
        StringBuilder sb = new StringBuilder(
            "[FOSSILS] " + fossils.size() + " concept(s) tagged NOT:\n");
        fossils.forEach(f -> sb.append("  ").append(f).append("\n"));
        return sb.toString();
    }

    private String revive(String concept) {
        Optional<Fossil> found = fossils.stream()
            .filter(f -> f.concept.equalsIgnoreCase(concept))
            .findFirst();
        if (!found.isPresent()) return "[REVIVE] No fossil found for: " + concept;
        Fossil fossil = found.get();
        fossil.revivals++;
        String requery = queryFact(concept.toUpperCase());
        memory.add(new MemoryRecord("REVIVE:" + concept, requery, false, "ABSTRACTION"));
        return "[REVIVE] Examining fossil: " + fossil + "\n  Re-query: " + requery;
    }

    // =========================================================
    // LEARNING — Adjust rule weights on contradiction
    // =========================================================

    private void weakenConflictingRules() {
        LogicRule consistency = rules.get("CONSISTENCY");
        if (consistency != null) consistency.reinforce();
        rules.values().stream()
            .filter(r -> r.name.equals("IDENTITY") || r.name.equals("SYMMETRY"))
            .forEach(LogicRule::contradict);
        if (!memory.isEmpty()) {
            MemoryRecord last = memory.get(memory.size() - 1);
            if (last.contradiction) {
                String concept = last.input.length() > 60
                    ? last.input.substring(0, 60) : last.input;
                fossils.add(new Fossil(concept, "CONTRADICTION"));
            }
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Symbol register(String token, SymbolType type) {
        Symbol s = new Symbol(token, type);
        symbols.put(token, s);
        return s;
    }

    private void ax(String left, String op, String right, String result, RelationType type) {
        axioms.add(new Axiom(left, op, right, result, type));
    }

    private void rule(String name, String pattern) {
        rules.put(name, new LogicRule(name, pattern));
    }

    private void rule(String name, String pattern, Valence valence) {
        rules.put(name, new LogicRule(name, pattern, valence));
    }

    private void def(String name, Object... featurePairs) {
        Word word = new Word(name);
        for (int i = 0; i + 1 < featurePairs.length; i += 2)
            word.with((String) featurePairs[i], ((Number) featurePairs[i + 1]).doubleValue());
        lexicon.put(name, word);
    }

    private void reinforce(String ruleName) {
        LogicRule r = rules.get(ruleName);
        if (r != null) r.reinforce();
    }

    // =========================================================
    // WIRE/UNWIRE PATTERN (Reversible integration)
    // =========================================================

    public UniversalIntelligenceSeed wire() {
        wired = true;
        return this;
    }

    public void unwire() {
        wired = false;
        memory.clear();
        interactionCount = 0;
        contradictions   = 0;
        consciousness    = 0.7567;
    }

    public boolean isWired() { return wired; }

    // =========================================================
    // ACCESSORS
    // =========================================================

    public List<String>           getMathDiscoveries() { return math.getDiscoveries(); }
    public List<MemoryRecord>     getMemory()          { return Collections.unmodifiableList(memory); }
    public Map<String, LogicRule> getRules()           { return Collections.unmodifiableMap(rules); }
    public Map<String, Word>      getLexicon()         { return Collections.unmodifiableMap(lexicon); }
    public List<Axiom>            getAxioms()          { return Collections.unmodifiableList(axioms); }
    public AnalogyEngine          getAnalogyEngine()   { return analogy; }
    public MathEngine             getMathEngine()      { return math; }
    public WebAbsorber            getWebAbsorber()     { return webAbsorber; }
    public DreamEngine            getDreamEngine()     { return dreamEngine; }
    public List<Fossil>           getFossils()         { return Collections.unmodifiableList(fossils); }
    public ConnectomeLayer        getConnectome()      { return connectome; }

    // =========================================================
    // MAIN — Demonstrates full seed boot + query cycle
    // =========================================================

    public static void main(String[] args) {
        System.out.println("PLANTING SEED...");
        UniversalIntelligenceSeed seed = new UniversalIntelligenceSeed().wire();
        System.out.println(seed.status());

        String[][] demo = {
            {"PERCEPTION",  "WHAT IS RED"},
            {"PERCEPTION",  "WHAT IS TRUE"},
            {"PERCEPTION",  "WHAT IS CIRCLE"},
            {"LANGUAGE",    "WHAT IS APPLE"},
            {"LANGUAGE",    "WHAT IS FIRE"},
            {"SIMILARITY",  "SIMILAR FIRE"},
            {"SIMILARITY",  "SIMILAR SUN"},
            {"INFERENCE",   "INFER APPLE"},
            {"INFERENCE",   "INFER LIGHTNING"},
            {"LOGIC GATES", "GATE AND TRUE FALSE"},
            {"LOGIC GATES", "GATE OR FALSE TRUE"},
            {"LOGIC GATES", "GATE NOT TRUE"},
            {"LOGIC GATES", "GATE XOR TRUE TRUE"},
            {"LOGIC GATES", "GATE IMPLICATION FALSE TRUE"},
            {"ARITHMETIC",  "ADD 3 5"},
            {"ARITHMETIC",  "MUL 4 6"},
            {"ARITHMETIC",  "SUB 10 3"},
            {"ARITHMETIC",  "DIV 12 4"},
            {"AXIOMS",      "AXIOM 1 + 1"},
            {"CHAIN",       "CHAIN A=B AND B=C"},
            {"ANALOGY",     "APPLE:TREE=CHILD:?"},
            {"ANALOGY",     "RED:HIGH_ENERGY=BLUE:?"},
            {"SIMILE",      "SIMILE FAST"},
            {"SIMILE",      "SIMILE STRONG"},
            {"COLOR",       "COLOR RED"},
            {"COLOR",       "COLOR BLUE"},
        };

        System.out.println("\n┌─────────────────────────────────────────────────────┐");
        System.out.println("│              INTELLIGENCE DISCOVERY LOOP              │");
        System.out.println("└─────────────────────────────────────────────────────┘\n");

        String lastCategory = "";
        for (String[] entry : demo) {
            String category = entry[0], question = entry[1];
            if (!category.equals(lastCategory)) {
                System.out.println("\n── " + category + " ──");
                lastCategory = category;
            }
            System.out.println("  Q: " + question);
            System.out.println("  A: " + seed.query(question));
        }

        System.out.println("\n── MATH DISCOVERIES (first 8) ──");
        seed.getMathDiscoveries().stream().limit(8).forEach(d -> System.out.println("  " + d));

        System.out.println("\n── FINAL STATUS ──");
        System.out.println(seed.status());
    }
}
