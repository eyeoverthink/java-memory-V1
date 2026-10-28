package fraymus.quantum;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fraymus.emotion.EmotionEngine;
import javax.tools.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * OUROBOROS-GAUNTLET RSI ENGINE — HTTP API server (packaged for FraynixBoot).
 *
 * Source: the attached single-file OuroborosQuantumServer. Evolution logic is unchanged. Hardening added for FraynixBoot:
 *  - binds 127.0.0.1 ONLY (original bound 0.0.0.0),
 *  - rejects requests with an Origin header or a Host other than 127.0.0.1/localhost (blocks browser CSRF and DNS rebinding),
 *    and no longer sends Access-Control-Allow-Origin: * (original did),
 *  - request body capped at 64 KB, evolve/rollback serialised, locale-safe number formatting,
 *  - data directory is injected (was a static system property) and refuses to sit inside a source tree,
 *  - stop() added so FraynixBoot can shut it down.
 *
 * What /api/evolve really does: it mutates a fixed toy kernel (sum of k^2+k) with three fixed string transforms,
 * compiles each candidate IN MEMORY (no class or source file is written anywhere), loads it through a throw-away
 * ClassLoader and times it. The "hot swap" is only a field assignment on the in-memory genome. It never reads or writes
 * project source files or QuantumEngine.java. The only files written are state.json, ledger.jsonl, emotion_state.json
 * and somatic_markers.jsonl inside the data dir.
 *
 * API:  GET /api/health, GET /api/state, POST /api/evolve, POST /api/rollback,
 *       GET /api/emotion/feeling, POST /api/emotion/event, POST /api/emotion/tick, POST /api/emotion/recall
 */
public class OuroborosQuantumServer {

    static final double PHI = 1.618033988749895;
    static final double GOLDEN_ANGLE = 2.0 * Math.PI * (1.0 - 1.0 / PHI);
    static final double EPSILON = 1e-9;
    static final int MAX_BODY = 64 * 1024;
    final Path dataDir;
    final Path stateFile;
    final Path ledgerFile;
    HttpServer httpServer;
    int boundPort = -1;

    public OuroborosQuantumServer(Path dataDir) {
        this.dataDir = dataDir.toAbsolutePath().normalize();
        this.stateFile = this.dataDir.resolve("state.json");
        this.ledgerFile = this.dataDir.resolve("ledger.jsonl");
        this.emotion = new EmotionEngine(this.dataDir);
    }

    // --- Cognitive Event ---
    record CognitiveEvent(String eventId, String episodeId, long seq,
                          String timestamp, String eventType, String detail, double fitnessScore) {
        String toJson() {
            return String.format(Locale.ROOT, "{\"seq\":%d,\"eventId\":\"%s\",\"episodeId\":\"%s\",\"timestamp\":\"%s\",\"eventType\":\"%s\",\"detail\":\"%s\",\"fitness\":%.6f}",
                seq, eventId, episodeId, timestamp, eventType, jsonEsc(detail), fitnessScore);
        }
    }

    // --- Polar Genome Node ---
    static class PolarGenomeNode {
        String codeHash, sourceCode;
        int generation, accessCount;
        double fitness, importance, radius, theta;

        PolarGenomeNode(String hash, String src, int gen, double fit, double imp) {
            this.codeHash = hash; this.sourceCode = src; this.generation = gen;
            this.fitness = fit; this.importance = Math.min(1.0, Math.max(0.0, imp));
            this.radius = Math.pow(1.0 - this.importance, PHI) * 10.0;
            this.theta = (Math.abs((double) hash.hashCode()) * GOLDEN_ANGLE) % (2.0 * Math.PI);
            this.accessCount = 1;
        }

        double getContinuity() {
            return importance * (0.5 + Math.min(100, accessCount) / 100.0 * 0.5);
        }
    }

    // --- Rule 30 Chaos ---
    static class Rule30Chaos {
        static final int[] RULE = {0, 0, 0, 1, 1, 1, 1, 0};
        int[] row;

        Rule30Chaos(String seed, int width) {
            this.row = new int[width];
            byte[] h = sha256Bytes(seed);
            for (int i = 0; i < width; i++) row[i] = (h[i % h.length] >> (i % 8)) & 1;
        }

        int nextBits(int count) {
            int val = 0;
            for (int b = 0; b < count; b++) {
                int n = row.length; int[] next = new int[n];
                for (int i = 0; i < n; i++) {
                    int l = row[(i - 1 + n) % n], c = row[i], r = row[(i + 1) % n];
                    next[i] = RULE[7 - ((l << 2) | (c << 1) | r)];
                }
                row = next;
                val = (val << 1) | row[n / 2];
            }
            return val;
        }
    }

    // --- In-Memory Compiler (nothing is written to disk) ---
    static class MemoryCompiler {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        static class Src extends SimpleJavaFileObject {
            final String code;
            Src(String name, String code) {
                super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
                this.code = code;
            }
            @Override public CharSequence getCharContent(boolean b) { return code; }
        }

        static class Bin extends SimpleJavaFileObject {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Bin(String name) { super(URI.create("mem:///" + name.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS); }
            byte[] getBytes() { return bos.toByteArray(); }
            @Override public OutputStream openOutputStream() { return bos; }
        }

        Class<?> compileAndLoad(String className, String src) throws Exception {
            if (compiler == null) throw new IllegalStateException("JDK compiler unavailable");
            Map<String, Bin> bins = new ConcurrentHashMap<>();
            var stdMgr = compiler.getStandardFileManager(null, null, null);
            var mgr = new ForwardingJavaFileManager<>(stdMgr) {
                @Override public JavaFileObject getJavaFileForOutput(JavaFileManager.Location l, String name, JavaFileObject.Kind k, FileObject s) {
                    Bin b = new Bin(name); bins.put(name, b); return b;
                }
            };
            var diag = new DiagnosticCollector<JavaFileObject>();
            if (!Boolean.TRUE.equals(compiler.getTask(null, mgr, diag, null, null, List.of(new Src(className, src))).call())) {
                var err = diag.getDiagnostics().stream().map(d -> "L" + d.getLineNumber() + ":" + d.getMessage(null)).collect(Collectors.joining("; "));
                throw new Exception("Compilation failed: " + err);
            }
            ClassLoader loader = new ClassLoader(OuroborosQuantumServer.class.getClassLoader()) {
                @Override protected Class<?> findClass(String name) throws ClassNotFoundException {
                    Bin b = bins.get(name); if (b == null) return super.findClass(name);
                    byte[] bytes = b.getBytes(); return defineClass(name, bytes, 0, bytes.length);
                }
            };
            return loader.loadClass(className);
        }
    }

    // --- Engine State ---
    final List<CognitiveEvent> ledger = Collections.synchronizedList(new ArrayList<>());
    final Map<String, PolarGenomeNode> centripetalMem = new ConcurrentHashMap<>();
    final Set<String> blackHoleHashes = ConcurrentHashMap.newKeySet();
    final Deque<PolarGenomeNode> fossilArchive = new ConcurrentLinkedDeque<>();
    final MemoryCompiler sandbox = new MemoryCompiler();
    final Rule30Chaos chaos = new Rule30Chaos("VAUGHN_SCOTT_OUROBOROS_SEED", 63);
    final ExecutorService pool = Executors.newFixedThreadPool(3, r -> { Thread t = new Thread(r, "ouroboros-eval"); t.setDaemon(true); return t; });
    final EmotionEngine emotion;
    volatile PolarGenomeNode activeGenome;
    volatile double baselineNs = 0.0;
    long ledgerSeq = 0;
    List<Map<String, Object>> lastReceipts = new ArrayList<>();

    // --- Logging ---
    synchronized CognitiveEvent logEvent(String epId, String type, String detail, double fitness) {
        var ev = new CognitiveEvent(sha256(epId + ":" + (++ledgerSeq) + ":" + type).substring(0, 12),
            epId, ledgerSeq, Instant.now().toString(), type, detail, fitness);
        ledger.add(ev);
        System.out.println(String.format(Locale.ROOT, "[LEDGER #%02d | %s] %-24s | Fit: %8.4f | %s",
            ev.seq(), ev.episodeId(), ev.eventType(), ev.fitnessScore(), ev.detail()));
        appendLedger(ev);
        return ev;
    }

    // --- Structural Entropy ---
    double entropy(String src) {
        double e = 0;
        e += count(src, "for (") * 12 + count(src, "for(") * 12 + count(src, "while (") * 12;
        e += count(src, "Thread.sleep") * 50 + count(src, "Math.pow") * 15;
        e += count(src, "+=") * 4 + src.lines().count() * 0.5;
        return e;
    }

    int count(String s, String t) {
        int c = 0, i = 0; while ((i = s.indexOf(t, i)) != -1) { c++; i += t.length(); } return c;
    }

    // --- 5-Stage Quantum Arena Gauntlet ---
    double evaluateInArena(String className, String src, String epId, String branch) {
        String hash = sha256(src);
        if (blackHoleHashes.contains(hash)) {
            logEvent(epId, "BLACK_HOLE_SKIP", branch + " hash in Event Horizon", 0.0);
            return 0.0;
        }
        try {
            Class<?> clazz = sandbox.compileAndLoad(className, src);
            Method method = clazz.getMethod("computeSeries", int.class);

            int[] inputs = {1, 10, 100, 1000, 5000};
            for (int n : inputs) {
                double expected = ((double) n * (n + 1) * (n + 2)) / 3.0;
                double actual = (double) method.invoke(null, n);
                if (Math.abs(actual - expected) > EPSILON || Double.isNaN(actual)) {
                    blackHoleHashes.add(hash);
                    logEvent(epId, "GAUNTLET_REJECTED", branch + " failed at n=" + n +
                        " (expected=" + expected + ", got=" + actual + ")", 0.0);
                    return 0.0;
                }
            }

            for (int i = 0; i < 50; i++) method.invoke(null, 2000);
            long start = System.nanoTime();
            int runs = 200;
            for (int i = 0; i < runs; i++) method.invoke(null, 5000);
            double avgNs = (System.nanoTime() - start) / (double) runs;

            if (baselineNs <= 0.0) baselineNs = avgNs;
            double ent = entropy(src);
            double speedup = baselineNs / Math.max(1.0, avgNs);
            double elegance = Math.pow(PHI, -ent / 100.0);
            double fitness = speedup * elegance;

            logEvent(epId, "ARENA_VERIFIED", String.format(Locale.ROOT, "%s | %.0f ns | E=%.1f | %.2fx speedup",
                branch, avgNs, ent, speedup), fitness);
            return fitness;
        } catch (Exception e) {
            blackHoleHashes.add(hash);
            logEvent(epId, "COMPILE_OR_RUNTIME_ERR", branch + " -> " + e.getMessage(), 0.0);
            return 0.0;
        }
    }

    // --- Mutation Engines ---
    String mutateAlpha(String src) {
        return src.replace("Math.pow(k, 2)", "((double) k * k)");
    }

    String mutateBeta(String src) {
        int bits = chaos.nextBits(2);
        if (bits == 0) return src.replace("+ k;", "- k;"); // deliberate bad mutation
        return src.replace("double term = Math.pow(k, 2) + k;\n            sum += term;",
            "sum += ((double) k * k) + k;");
    }

    String mutateGamma(String src) {
        return """

            public class CandidateKernel {
                public static double computeSeries(int n) {
                    double d = n;
                    return (d * (d + 1.0) * (d + 2.0)) / 3.0;
                }
            }
            """;
    }

    // --- Genesis ---
    void bootstrapGenesis(String className, String src) {
        String epId = "EP_GENESIS_00";
        double fit = evaluateInArena(className, src, epId, "GENESIS_SEED");
        String hash = sha256(src);
        activeGenome = new PolarGenomeNode(hash, src, 0, fit, 0.5);
        centripetalMem.put(hash, activeGenome);
        logEvent(epId, "GENESIS_ESTABLISHED", "r=" + String.format(Locale.ROOT, "%.3f", activeGenome.radius) +
            " | continuity=" + String.format(Locale.ROOT, "%.3f", activeGenome.getContinuity()), fit);
        saveState();
    }

    static final String GEN0_CODE = """

        public class CandidateKernel {
            public static double computeSeries(int n) {
                double sum = 0.0;
                for (int k = 1; k <= n; k++) {
                    double term = Math.pow(k, 2) + k;
                    sum += term;
                }
                return sum;
            }
        }
        """;

    // --- Evolution Cycle ---
    final Object cycleLock = new Object();   // NOT 'this': logEvent() is synchronized on this and runs on the worker threads

    Map<String, Object> runEvolutionCycle() throws Exception {
        synchronized (cycleLock) { return runEvolutionCycleLocked(); }
    }

    Map<String, Object> runEvolutionCycleLocked() throws Exception {
        int gen = activeGenome.generation + 1;
        String epId = String.format("EP_CYCLE_%02d", gen);
        String className = "CandidateKernel";
        String currentSrc = activeGenome.sourceCode;

        logEvent(epId, "OBSERVE_ENTROPY", "Gen=" + activeGenome.generation +
            " | E=" + entropy(currentSrc), activeGenome.fitness);

        String srcA = mutateAlpha(currentSrc);
        String srcB = mutateBeta(currentSrc);
        String srcG = (gen >= 2) ? mutateGamma(currentSrc) : mutateAlpha(currentSrc);

        var futures = pool.invokeAll(List.of(
            (Callable<double[]>) () -> new double[]{evaluateInArena(className, srcA, epId, "ALPHA_AST"), 0},
            (Callable<double[]>) () -> new double[]{evaluateInArena(className, srcB, epId, "BETA_RULE30"), 1},
            (Callable<double[]>) () -> new double[]{evaluateInArena(className, srcG, epId, "GAMMA_BICAMERAL"), 2}
        ));

        String[] labels = {"ALPHA_AST", "BETA_RULE30", "GAMMA_BICAMERAL"};
        String[] sources = {srcA, srcB, srcG};
        String bestSrc = currentSrc; double bestFit = activeGenome.fitness; String winner = "NONE";
        lastReceipts = new ArrayList<>();

        for (int i = 0; i < futures.size(); i++) {
            double fit = futures.get(i).get()[0];
            lastReceipts.add(Map.of("branch", labels[i], "hash", sha256(sources[i]).substring(0, 12),
                "passed", fit > 0, "fitness", fit));
            if (fit > bestFit) { bestFit = fit; bestSrc = sources[i]; winner = labels[i]; }
        }

        if (!bestSrc.equals(currentSrc)) {
            fossilArchive.push(activeGenome);
            logEvent(epId, "FOSSIL_ARCHIVED", "Gen " + activeGenome.generation +
                " (" + activeGenome.codeHash.substring(0, 8) + ") -> fossil", activeGenome.fitness);
            String newHash = sha256(bestSrc);
            var next = new PolarGenomeNode(newHash, bestSrc, gen, bestFit,
                Math.min(1.0, activeGenome.importance + 0.25));
            next.accessCount = activeGenome.accessCount + 1;
            activeGenome = next;
            centripetalMem.put(newHash, activeGenome);
            logEvent(epId, "GENOME_SUPERSEDED", winner + " won | r=" +
                String.format(Locale.ROOT, "%.3f", activeGenome.radius), bestFit);
        } else {
            activeGenome.accessCount++;
            logEvent(epId, "NO_SUPERIOR_MUTATION", "Retaining current genome", activeGenome.fitness);
        }

        saveState();
        return stateMap();
    }

    // --- Rollback ---
    Map<String, Object> rollback() {
        synchronized (cycleLock) { return rollbackLocked(); }
    }

    Map<String, Object> rollbackLocked() {
        if (fossilArchive.isEmpty()) return Map.of("error", "No fossils to restore");
        var prev = fossilArchive.pop();
        logEvent("EP_ROLLBACK", "FOSSIL_RESTORED", "Rolled back to Gen " + prev.generation, prev.fitness);
        activeGenome = prev;
        saveState();
        return stateMap();
    }

    // --- State Serialization ---
    Map<String, Object> stateMap() {
        var m = new LinkedHashMap<String, Object>();
        m.put("activeGenome", activeGenome != null ? Map.of(
            "hash", activeGenome.codeHash, "gen", activeGenome.generation,
            "fitness", activeGenome.fitness, "importance", activeGenome.importance,
            "radius", activeGenome.radius, "theta", activeGenome.theta,
            "accessCount", activeGenome.accessCount, "continuity", activeGenome.getContinuity()
        ) : null);
        m.put("activeSource", activeGenome != null ? activeGenome.sourceCode : "");
        m.put("orbits", centripetalMem.values().stream()
            .map(n -> Map.of("hash", n.codeHash, "gen", n.generation, "fitness", n.fitness,
                "radius", n.radius, "theta", n.theta, "active", n == activeGenome))
            .toList());
        m.put("fossilCount", fossilArchive.size());
        m.put("blackHoleCount", blackHoleHashes.size());
        m.put("blackHoleHashes", new ArrayList<>(blackHoleHashes));
        m.put("receipts", lastReceipts);
        m.put("baselineLatencyNs", baselineNs);
        m.put("ledger", ledger.stream().map(e -> Map.of(
            "seq", e.seq(), "eventId", e.eventId(), "episodeId", e.episodeId(),
            "eventType", e.eventType(), "detail", e.detail(), "fitness", e.fitnessScore()
        )).toList());
        return m;
    }

    void saveState() {
        try {
            Files.createDirectories(dataDir);
            Files.writeString(stateFile, toJson(stateMap()), StandardCharsets.UTF_8);
        } catch (Exception e) { System.err.println("Save failed: " + e.getMessage()); }
    }

    void appendLedger(CognitiveEvent ev) {
        try {
            Files.createDirectories(dataDir);
            Files.writeString(ledgerFile, ev.toJson() + "\n", StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) { System.err.println("Ledger append failed: " + e.getMessage()); }
    }

    // --- HTTP Server (loopback only) ---
    boolean guard(HttpExchange ex) throws IOException {
        String host = ex.getRequestHeaders().getFirst("Host");
        String origin = ex.getRequestHeaders().getFirst("Origin");
        boolean hostOk = host != null && (host.equals("127.0.0.1:" + boundPort) || host.equals("localhost:" + boundPort)
            || host.equals("127.0.0.1") || host.equals("localhost"));
        if (!hostOk || origin != null) { respond(ex, 403, "{\"error\":\"forbidden: local non-browser clients only\"}"); return false; }
        return true;
    }

    void ctx(String path, HttpHandler h) {
        httpServer.createContext(path, ex -> { if (guard(ex)) h.handle(ex); });
    }

    static String readBody(HttpExchange ex) throws IOException {
        byte[] b = ex.getRequestBody().readNBytes(MAX_BODY + 1);
        if (b.length > MAX_BODY) throw new IOException("request body too large");
        return new String(b, StandardCharsets.UTF_8);
    }

    public synchronized void startServer(int port) throws Exception {
        if (httpServer != null) throw new IllegalStateException("already started");
        httpServer = HttpServer.create(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), port), 0);
        boundPort = httpServer.getAddress().getPort();

        ctx("/api/health", ex -> respond(ex, 200, "{\"status\":\"healthy\",\"engine\":\"ouroboros-gauntlet\",\"bind\":\"127.0.0.1\",\"generation\":" +
            (activeGenome != null ? activeGenome.generation : -1) + "}"));

        ctx("/api/state", ex -> {
            if (!"GET".equals(ex.getRequestMethod())) { respond(ex, 405, "{\"error\":\"GET only\"}"); return; }
            respond(ex, 200, toJson(stateMap()));
        });

        ctx("/api/evolve", ex -> {
            if (!"POST".equals(ex.getRequestMethod())) { respond(ex, 405, "{\"error\":\"POST only\"}"); return; }
            try {
                respond(ex, 200, toJson(runEvolutionCycle()));
            } catch (Exception e) {
                respond(ex, 500, "{\"error\":\"" + jsonEsc(e.getMessage()) + "\"}");
            }
        });

        ctx("/api/rollback", ex -> {
            if (!"POST".equals(ex.getRequestMethod())) { respond(ex, 405, "{\"error\":\"POST only\"}"); return; }
            respond(ex, 200, toJson(rollback()));
        });

        ctx("/api/emotion/feeling", ex -> {
            if (!"GET".equals(ex.getRequestMethod())) { respond(ex, 405, "{\"error\":\"GET only\"}"); return; }
            respond(ex, 200, toJson(emotion.stateMap()));
        });

        ctx("/api/emotion/event", ex -> {
            if (!"POST".equals(ex.getRequestMethod())) { respond(ex, 405, "{\"error\":\"POST only\"}"); return; }
            try {
                String reqBody = readBody(ex);
                @SuppressWarnings("unchecked")
                Map<String, Object> input = (Map<String, Object>) parseJsonObj(reqBody);
                String eventType = String.valueOf(input.getOrDefault("type", "neutral"));
                String desc = String.valueOf(input.getOrDefault("description", ""));
                Map<String, Double> appraisalHints = toDoubleMap(input.get("appraisal"));
                Map<String, Double> bodyPerturb = toDoubleMap(input.get("body"));
                Double uv = input.containsKey("user_valence") ? toDouble(input.get("user_valence")) : null;
                Double bd = input.containsKey("bond_delta") ? toDouble(input.get("bond_delta")) : null;
                Map<String, Object> result;
                synchronized (emotion) { result = emotion.processEvent(eventType, desc, appraisalHints, bodyPerturb, uv, bd); }
                respond(ex, 200, toJson(result));
            } catch (Exception e) {
                respond(ex, 400, "{\"error\":\"" + jsonEsc(e.getMessage()) + "\"}");
            }
        });

        ctx("/api/emotion/tick", ex -> {
            if (!"POST".equals(ex.getRequestMethod())) { respond(ex, 405, "{\"error\":\"POST only\"}"); return; }
            synchronized (emotion) { emotion.tick(); emotion.saveState(); }
            respond(ex, 200, toJson(emotion.stateMap()));
        });

        ctx("/api/emotion/recall", ex -> {
            if (!"POST".equals(ex.getRequestMethod())) { respond(ex, 405, "{\"error\":\"POST only\"}"); return; }
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> input = (Map<String, Object>) parseJsonObj(readBody(ex));
                String context = String.valueOf(input.getOrDefault("context", ""));
                var marker = emotion.recall(context);
                if (marker == null) {
                    respond(ex, 200, "{\"found\":false}");
                } else {
                    var m = new LinkedHashMap<String, Object>();
                    m.put("found", true);
                    m.put("context", marker.context());
                    m.put("valence", marker.valence());
                    m.put("arousal", marker.arousal());
                    m.put("emotion", marker.dominantEmotion());
                    m.put("intensity", marker.emotionIntensity());
                    m.put("timestamp", marker.timestamp());
                    respond(ex, 200, toJson(m));
                }
            } catch (Exception e) {
                respond(ex, 400, "{\"error\":\"" + jsonEsc(e.getMessage()) + "\"}");
            }
        });

        httpServer.setExecutor(Executors.newFixedThreadPool(4, r -> { Thread t = new Thread(r, "ouroboros-http"); t.setDaemon(true); return t; }));
        httpServer.start();
        System.out.println("OUROBOROS-GAUNTLET RSI ENGINE - LIVE on 127.0.0.1:" + boundPort + " (loopback only), data: " + dataDir);
    }

    public synchronized void stop() {
        if (httpServer != null) { httpServer.stop(0); httpServer = null; }
        pool.shutdownNow();
    }

    static void respond(HttpExchange ex, int code, String body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, b.length);
        ex.getResponseBody().write(b); ex.getResponseBody().close();
    }

    // --- Minimal JSON serializer ---
    @SuppressWarnings("unchecked")
    static String toJson(Object o) {
        if (o == null) return "null";
        if (o instanceof String s) return "\"" + jsonEsc(s) + "\"";
        if (o instanceof Number n) return n instanceof Double d ? String.format(Locale.ROOT, "%.6f", d) : n.toString();
        if (o instanceof Boolean b) return b.toString();
        if (o instanceof Map<?,?> m) {
            return "{" + ((Map<String,Object>) m).entrySet().stream()
                .map(e -> "\"" + jsonEsc(e.getKey()) + "\":" + toJson(e.getValue()))
                .collect(Collectors.joining(",")) + "}";
        }
        if (o instanceof List<?> l) {
            return "[" + l.stream().map(OuroborosQuantumServer::toJson).collect(Collectors.joining(",")) + "]";
        }
        return "\"" + jsonEsc(o.toString()) + "\"";
    }

    static String jsonEsc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    static byte[] sha256Bytes(String input) {
        try { return MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8)); }
        catch (Exception e) { return input.getBytes(StandardCharsets.UTF_8); }
    }
    static String sha256(String input) {
        byte[] b = sha256Bytes(input);
        var sb = new StringBuilder(); for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }

    // --- Minimal JSON parser (request bodies) ---
    static Object parseJsonObj(String json) {
        json = json.trim();
        if (json.startsWith("{")) {
            var map = new LinkedHashMap<String, Object>();
            json = json.substring(1, json.lastIndexOf('}')).trim();
            if (json.isEmpty()) return map;
            int depth = 0; int start = 0; boolean inStr = false;
            List<String> pairs = new ArrayList<>();
            for (int i = 0; i < json.length(); i++) {
                char c = json.charAt(i);
                if (c == '"' && (i == 0 || json.charAt(i-1) != '\\')) inStr = !inStr;
                if (!inStr) { if (c == '{' || c == '[') depth++; if (c == '}' || c == ']') depth--; }
                if (c == ',' && depth == 0 && !inStr) { pairs.add(json.substring(start, i).trim()); start = i + 1; }
            }
            pairs.add(json.substring(start).trim());
            for (String pair : pairs) {
                int colon = pair.indexOf(':');
                if (colon < 0) continue;
                String key = pair.substring(0, colon).trim();
                if (key.startsWith("\"")) key = key.substring(1, key.length() - 1);
                String val = pair.substring(colon + 1).trim();
                map.put(key, parseJsonVal(val));
            }
            return map;
        }
        return parseJsonVal(json);
    }

    static Object parseJsonVal(String v) {
        v = v.trim();
        if (v.equals("null")) return null;
        if (v.equals("true")) return true;
        if (v.equals("false")) return false;
        if (v.startsWith("\"")) return v.substring(1, v.length() - 1).replace("\\\"", "\"").replace("\\n", "\n");
        if (v.startsWith("{")) return parseJsonObj(v);
        if (v.startsWith("[")) {
            var list = new ArrayList<>();
            v = v.substring(1, v.lastIndexOf(']')).trim();
            if (!v.isEmpty()) {
                int depth = 0; int start = 0; boolean inStr = false;
                for (int i = 0; i < v.length(); i++) {
                    char c = v.charAt(i);
                    if (c == '"' && (i == 0 || v.charAt(i-1) != '\\')) inStr = !inStr;
                    if (!inStr) { if (c == '{' || c == '[') depth++; if (c == '}' || c == ']') depth--; }
                    if (c == ',' && depth == 0 && !inStr) { list.add(parseJsonVal(v.substring(start, i))); start = i + 1; }
                }
                list.add(parseJsonVal(v.substring(start)));
            }
            return list;
        }
        try { return Double.parseDouble(v); } catch (NumberFormatException e) { return v; }
    }

    @SuppressWarnings("unchecked")
    static Map<String, Double> toDoubleMap(Object o) {
        if (o == null) return new HashMap<>();
        if (o instanceof Map<?,?> m) {
            var result = new HashMap<String, Double>();
            for (var e : ((Map<String, Object>) m).entrySet()) result.put(e.getKey(), toDouble(e.getValue()));
            return result;
        }
        return new HashMap<>();
    }

    static Double toDouble(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(o.toString()); } catch (Exception e) { return 0.0; }
    }

    // --- Main (child-process entry; FraynixBoot starts it through OuroborosServerBridge) ---
    public static void main(String[] args) throws Exception {
        int port = 8765;
        String data = System.getProperty("ouroboros.data");
        for (String a : args) {
            if (a.startsWith("--port=")) port = Integer.parseInt(a.substring(7));
            if (a.startsWith("--data=")) data = a.substring(7);
        }
        if (data == null || data.isBlank()) data = System.getProperty("user.home") + "/Fraymus_Master_System/ouroboros/server-data";
        Path dir = Path.of(data).toAbsolutePath().normalize();
        String ds = dir.toString().replace('\\', '/');
        if (ds.contains("/src/main/") || Files.exists(dir.resolve("build.gradle"))) {
            System.err.println("refusing data dir inside a source tree: " + dir); System.exit(2);
        }
        var engine = new OuroborosQuantumServer(dir);
        engine.emotion.load();
        engine.bootstrapGenesis("CandidateKernel", GEN0_CODE);
        engine.startServer(port);
    }
}

