package fraymus.neural;

import fraymus.knowledge.AkashicRecord;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/**
 * OPENCLAW AGENT — Bare-Metal Action Agent
 * =========================================================================================
 *
 * "Corporate AIs are trapped in a chat box. OpenClaw is trapped in nothing."
 *
 * This is not a demo. This is the HANDS of the system.
 * OpenClaw bridges 16,384-D Hyper-Vectors to raw Java Reflection + Filesystem.
 *
 * CAPABILITIES:
 * 1. FILE TREE SCANNING   — Walk the entire Asset-Manager, index every .java file
 * 2. VECTOR-SEMANTIC MAP   — Encode file/class/method names into AeonKronos 16,384-D space
 * 3. INTENT RESOLUTION     — Convert natural-language directive → vector → nearest absorbed skill
 * 4. REFLECTION EXECUTION  — Class.forName + method.invoke on discovered skills
 * 5. OUROBOROS SELF-REPAIR — If execution fails, codegen a fix via Orchestrator
 * 6. FILE INTEGRITY GUARD  — SHA-256 all .java files, detect unauthorized modifications
 * 7. KNOWLEDGE CONSOLIDATION — Find loose code/docs, eat them via LibraryAbsorber
 *
 * PIPELINE:
 *   DIRECTIVE → TOKENIZE → ENCODE (16,384-D) → NEAREST SKILL (Hamming) →
 *   RESOLVE CLASS → REFLECTION INVOKE → LOG RESULT → AKASHIC PERSIST →
 *   ON FAILURE: OUROBOROS REPAIR → RETRY
 *
 * =========================================================================================
 */
public class OpenClawAgent {

    // ─── ROOT PATH: The kingdom OpenClaw has keys to ───────────────────────
    private static final String ROOT_PATH = "D:/Zip And Send/Java-Memory/Asset-Manager";
    private static final String SRC_PATH  = ROOT_PATH + "/src/main/java";
    private static final String KB_PATH   = ROOT_PATH + "/knowledge_base";

    private static final String GENESIS_VAULT = ROOT_PATH + "/genesis_vault";
    private static final String JIT_DIR      = GENESIS_VAULT + "/omega_jit";
    private static final String SKILLS_DIR   = ROOT_PATH + "/.openclaw/skills";

    private static final String AKASHIC_CATEGORY = "OPENCLAW_ACTION";
    private static final int MAX_SKILL_MATCHES = 10;
    private static final int MAX_RETRY = 2;
    private static final int SHELL_TIMEOUT_MS = 120_000; // 2 min max for shell commands

    // ─── WIRED SUBSYSTEMS ──────────────────────────────────────────────────
    private volatile AeonKronos       kronos;
    private volatile LibraryAbsorber  absorber;
    private volatile AkashicRecord    akashic;
    private volatile AeonOrchestrator orchestrator;
    private volatile SentientCore     sentient;

    // ─── FILE INDEX: path → class name, hash → integrity ───────────────────
    private final ConcurrentHashMap<String, String> fileIndex      = new ConcurrentHashMap<>(); // path → FQCN
    private final ConcurrentHashMap<String, String> fileHashes     = new ConcurrentHashMap<>(); // path → SHA-256
    private final ConcurrentHashMap<String, long[]> skillVectors   = new ConcurrentHashMap<>(); // skill → 16K-D vec
    private final List<SkillPointer> skillRegistry = new CopyOnWriteArrayList<>();

    // ─── ACTION LOG & TELEMETRY ────────────────────────────────────────────
    private final List<String> actionLog = new CopyOnWriteArrayList<>();
    private final AtomicInteger directivesExecuted = new AtomicInteger(0);
    private final AtomicInteger skillsMatched      = new AtomicInteger(0);
    private final AtomicInteger reflectionCalls    = new AtomicInteger(0);
    private final AtomicInteger repairsTriggered   = new AtomicInteger(0);
    private final AtomicInteger filesScanned       = new AtomicInteger(0);
    private final AtomicInteger integrityViolations = new AtomicInteger(0);
    private final AtomicInteger knowledgeEaten     = new AtomicInteger(0);
    private final AtomicInteger shellCommandsRun   = new AtomicInteger(0);
    private final AtomicInteger fileOpsPerformed   = new AtomicInteger(0);
    private final AtomicInteger autoFixesApplied   = new AtomicInteger(0);
    private final AtomicInteger gradleBuildsRun    = new AtomicInteger(0);
    private volatile long lastScanMs  = 0;
    private volatile long lastDirectiveMs = 0;
    private volatile boolean fileTreeScanned = false;
    private volatile boolean skillsIndexed   = false;

    // ─── LOADED SKILL FILES ──────────────────────────────────────────────────
    private final ConcurrentHashMap<String, SkillFile> loadedSkills = new ConcurrentHashMap<>();

    // ═════════════════════════════════════════════════════════════════════════
    // CONSTRUCTION & WIRING
    // ═════════════════════════════════════════════════════════════════════════

    public OpenClawAgent() {
        System.out.println("[CLAW] OpenClaw Agent online — bare-metal action agent");
    }

    public void wireKronos(AeonKronos k)           { this.kronos = k; }
    public void wireAbsorber(LibraryAbsorber a)     { this.absorber = a; }
    public void wireAkashic(AkashicRecord a)        { this.akashic = a; }
    public void wireOrchestrator(AeonOrchestrator o){ this.orchestrator = o; }
    public void wireSentient(SentientCore s)        { this.sentient = s; }

    // ═════════════════════════════════════════════════════════════════════════
    // 1. FILE TREE SCANNING — Walk the Asset-Manager, index every .java file
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Recursively scan the Asset-Manager source tree.
     * Builds: fileIndex (path→FQCN), fileHashes (path→SHA-256).
     */
    public String scanFileTree() {
        long t0 = System.currentTimeMillis();
        fileIndex.clear();
        fileHashes.clear();
        filesScanned.set(0);

        File srcRoot = new File(SRC_PATH);
        if (!srcRoot.exists()) return "[CLAW] ERROR: Source path not found: " + SRC_PATH;

        scanDirectory(srcRoot, "");

        // Also scan knowledge_base for loose .java files
        File kbRoot = new File(KB_PATH);
        if (kbRoot.exists()) scanDirectory(kbRoot, "");

        lastScanMs = System.currentTimeMillis() - t0;
        fileTreeScanned = true;

        String result = String.format("[CLAW] FILE TREE SCANNED: %,d .java files indexed, %,d hashed in %dms",
            filesScanned.get(), fileHashes.size(), lastScanMs);
        log("SCAN", result);
        return result;
    }

    private void scanDirectory(File dir, String prefix) {
        if (dir == null || !dir.isDirectory()) return;
        File[] children = dir.listFiles();
        if (children == null) return;

        for (File child : children) {
            String name = child.getName();
            if (child.isDirectory()) {
                if (!name.equals(".git") && !name.equals("build") && !name.equals("out")
                    && !name.equals(".gradle") && !name.equals("__pycache__")) {
                    scanDirectory(child, prefix);
                }
            } else if (name.endsWith(".java")) {
                filesScanned.incrementAndGet();
                String path = child.getAbsolutePath().replace('\\', '/');

                // Extract FQCN from package declaration
                String fqcn = extractFQCN(child);
                if (fqcn != null) fileIndex.put(path, fqcn);

                // SHA-256 hash for integrity
                String hash = hashFile(child);
                if (hash != null) fileHashes.put(path, hash);
            }
        }
    }

    private String extractFQCN(File javaFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(javaFile))) {
            String line;
            String pkg = "";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("package ")) {
                    pkg = line.replace("package ", "").replace(";", "").trim();
                }
                if ((line.startsWith("public ") || line.startsWith("abstract ") || line.startsWith("final "))
                    && (line.contains("class ") || line.contains("interface ") || line.contains("enum "))) {
                    String className = javaFile.getName().replace(".java", "");
                    return pkg.isEmpty() ? className : pkg + "." + className;
                }
            }
        } catch (IOException ignored) {}
        return null;
    }

    private String hashFile(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = Files.readAllBytes(file.toPath());
            byte[] digest = md.digest(bytes);
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            return null;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 2. VECTOR-SEMANTIC SKILL INDEX — Encode absorbed skills into 16,384-D
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Index all absorbed methods/classes into AeonKronos vector space.
     * Each method becomes a semantic vector. Intent matching uses Hamming distance.
     */
    public String indexSkills() {
        if (absorber == null) return "[CLAW] ERROR: LibraryAbsorber not wired";
        if (kronos == null) return "[CLAW] ERROR: AeonKronos not wired";

        long t0 = System.currentTimeMillis();
        skillRegistry.clear();
        skillVectors.clear();

        List<String[]> methods = absorber.getAbsorbedMethodSignatures();
        int indexed = 0;

        for (String[] triple : methods) {
            String className  = triple[0]; // FQCN
            String methodName = triple[1]; // method name
            String fullSig    = triple[2]; // full signature

            // Build a semantic key for vector encoding
            String simpleClass = className.contains(".")
                ? className.substring(className.lastIndexOf('.') + 1) : className;
            String semanticKey = (simpleClass + "_" + methodName).toUpperCase();

            // Encode into 16,384-D via Kronos
            long[] vec = kronos.getOrGenerateConcept(semanticKey);
            skillVectors.put(semanticKey, vec);

            // Register the skill pointer
            skillRegistry.add(new SkillPointer(className, methodName, fullSig, semanticKey));
            indexed++;
        }

        // Also index file-system classes from the scan
        for (Map.Entry<String, String> entry : fileIndex.entrySet()) {
            String fqcn = entry.getValue();
            String simpleClass = fqcn.contains(".")
                ? fqcn.substring(fqcn.lastIndexOf('.') + 1) : fqcn;
            String key = simpleClass.toUpperCase();
            if (!skillVectors.containsKey(key)) {
                kronos.getOrGenerateConcept(key);
                indexed++;
            }
        }

        long elapsed = System.currentTimeMillis() - t0;
        skillsIndexed = true;

        String result = String.format("[CLAW] SKILLS INDEXED: %,d method vectors + %,d file classes in %dms",
            skillRegistry.size(), fileIndex.size(), elapsed);
        log("INDEX", result);
        return result;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 3. INTENT RESOLUTION — Directive → 16,384-D → Nearest Skills
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Convert a natural-language directive into a 16,384-D intention vector,
     * then find the nearest absorbed skills by Hamming distance.
     */
    public List<SkillPointer> resolveIntent(String directive) {
        if (kronos == null) return Collections.emptyList();

        // Tokenize directive into key concepts
        String[] tokens = directive.toUpperCase()
            .replaceAll("[^A-Z0-9_ ]", "")
            .split("\\s+");

        // Encode: XOR-superimpose all tokens into a single intention vector
        long[] intentVec = new long[AeonKronos.CHUNKS];
        for (String token : tokens) {
            if (token.length() < 2) continue;
            long[] tv = kronos.getOrGenerateConcept(token);
            for (int i = 0; i < intentVec.length; i++) intentVec[i] ^= tv[i];
        }

        // Find nearest skills by Hamming distance
        List<SkillMatch> matches = new ArrayList<>();
        for (SkillPointer skill : skillRegistry) {
            long[] sv = skillVectors.get(skill.semanticKey);
            if (sv == null) continue;
            int distance = hammingDistance(intentVec, sv);
            matches.add(new SkillMatch(skill, distance));
        }

        // Sort by distance (ascending = most similar first)
        matches.sort(Comparator.comparingInt(m -> m.distance));

        // Return top N
        List<SkillPointer> results = new ArrayList<>();
        int limit = Math.min(MAX_SKILL_MATCHES, matches.size());
        for (int i = 0; i < limit; i++) {
            results.add(matches.get(i).skill);
            skillsMatched.incrementAndGet();
        }

        return results;
    }

    private int hammingDistance(long[] a, long[] b) {
        int dist = 0;
        int len = Math.min(a.length, b.length);
        for (int i = 0; i < len; i++) dist += Long.bitCount(a[i] ^ b[i]);
        return dist;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 4. EXECUTE DIRECTIVE — The Main Intention-to-Execution Loop
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * THE MAIN DIRECTIVE LOOP.
     * Translates a text command into a 16K-D vector search → reflection execution.
     *
     * If absorber has the class on the classpath → direct reflection.
     * If not → ask Orchestrator/Ollama for reasoning.
     * On failure → Ouroboros self-repair.
     */
    public String executeDirective(String directive) {
        long t0 = System.currentTimeMillis();
        directivesExecuted.incrementAndGet();

        StringBuilder output = new StringBuilder();
        output.append("╔══════════════════════════════════════════════════════════════╗\n");
        output.append("║  \uD83E\uDD1E OPENCLAW DIRECTIVE                                        ║\n");
        output.append("╠══════════════════════════════════════════════════════════════╣\n");
        output.append(String.format("║  Intent: %-51s ║\n",
            directive.length() > 51 ? directive.substring(0, 48) + "..." : directive));
        output.append("╠══════════════════════════════════════════════════════════════╣\n");

        // PHASE 1: Ensure file tree and skills are indexed
        if (!fileTreeScanned) {
            output.append("║  [1/5] Scanning file tree...                                 ║\n");
            scanFileTree();
        }
        if (!skillsIndexed && absorber != null && kronos != null) {
            output.append("║  [2/5] Indexing skills into 16,384-D space...                ║\n");
            indexSkills();
        }

        // PHASE 2: Resolve intent → nearest skills
        output.append("║  [3/5] Resolving intent via Hamming similarity...            ║\n");
        List<SkillPointer> skills = resolveIntent(directive);

        if (skills.isEmpty()) {
            // No vector match — fall back to Ollama reasoning
            output.append("║  [!] No skill match — routing to Ollama...                   ║\n");
            String ollamaResult = askOllamaForDirective(directive);
            output.append("║  ").append(truncate(ollamaResult, 59)).append(" ║\n");
            log("DIRECTIVE_OLLAMA", directive + " → " + ollamaResult);
        } else {
            output.append(String.format("║  [4/5] Matched %d skills (top resonance)                     ║\n",
                skills.size()));

            // PHASE 3: Try reflection execution on top matches
            boolean executed = false;
            for (SkillPointer skill : skills.subList(0, Math.min(3, skills.size()))) {
                output.append(String.format("║  → Skill: %-20s :: %-27s ║\n",
                    shortName(skill.className), skill.methodName));

                String execResult = executeSkill(skill);
                if (execResult != null) {
                    output.append(String.format("║  ✓ EXECUTED: %-47s ║\n",
                        truncate(execResult, 47)));
                    executed = true;
                    break;
                } else {
                    output.append("║  ✗ Reflection failed — trying next match...                 ║\n");
                }
            }

            if (!executed) {
                // PHASE 4: Ouroboros self-repair
                output.append("║  [5/5] All reflection failed → OUROBOROS REPAIR...           ║\n");
                String repairResult = triggerOuroborosRepair(directive, skills);
                output.append("║  ").append(truncate(repairResult, 59)).append(" ║\n");
            }

            log("DIRECTIVE", directive + " → " + skills.size() + " skills matched");
        }

        lastDirectiveMs = System.currentTimeMillis() - t0;
        output.append("╠══════════════════════════════════════════════════════════════╣\n");
        output.append(String.format("║  Time: %,d ms | Directives: %,d | Skills matched: %,d         ║\n",
            lastDirectiveMs, directivesExecuted.get(), skillsMatched.get()));
        output.append("╚══════════════════════════════════════════════════════════════╝\n");

        // Persist to Akashic
        if (akashic != null) {
            akashic.addBlock(AKASHIC_CATEGORY, directive + "||" + lastDirectiveMs + "ms||" + skills.size() + " skills");
        }

        return output.toString();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 5. REFLECTION EXECUTION — Class.forName + method.invoke
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Attempt to invoke a skill via Java Reflection.
     * Returns result string on success, null on failure.
     */
    private String executeSkill(SkillPointer skill) {
        reflectionCalls.incrementAndGet();
        try {
            Class<?> cls = Class.forName(skill.className);

            // Try zero-arg method first
            try {
                Method m = cls.getDeclaredMethod(skill.methodName);
                if (Modifier.isStatic(m.getModifiers())) {
                    Object result = m.invoke(null);
                    return "STATIC " + skill.methodName + "() → " + summarize(result);
                } else if (Modifier.isPublic(cls.getModifiers())) {
                    Object instance = cls.getDeclaredConstructor().newInstance();
                    Object result = m.invoke(instance);
                    return skill.methodName + "() → " + summarize(result);
                }
            } catch (NoSuchMethodException ignored) {}

            // Try method with String arg (pass directive context)
            try {
                Method m = cls.getDeclaredMethod(skill.methodName, String.class);
                if (Modifier.isStatic(m.getModifiers())) {
                    Object result = m.invoke(null, ROOT_PATH);
                    return "STATIC " + skill.methodName + "(root) → " + summarize(result);
                }
            } catch (NoSuchMethodException ignored) {}

            return null; // No invocable signature found
        } catch (ClassNotFoundException e) {
            return null; // Class not on classpath (source-only absorption)
        } catch (Exception e) {
            log("EXEC_FAIL", skill.className + "." + skill.methodName + " → " + e.getMessage());
            return null;
        }
    }

    private String summarize(Object result) {
        if (result == null) return "void";
        String s = result.toString();
        return s.length() > 80 ? s.substring(0, 77) + "..." : s;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 6. OUROBOROS SELF-REPAIR — On failure, codegen a fix
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * When all reflection attempts fail, ask the Orchestrator to generate
     * a solution via codegen, then feed the result back into Akashic.
     */
    private String triggerOuroborosRepair(String directive, List<SkillPointer> failedSkills) {
        repairsTriggered.incrementAndGet();

        if (orchestrator == null) return "[REPAIR] No Orchestrator wired";

        // Build context for codegen
        StringBuilder context = new StringBuilder();
        context.append("OpenClaw failed to execute directive: ").append(directive).append("\n");
        context.append("Attempted skills:\n");
        for (SkillPointer s : failedSkills.subList(0, Math.min(3, failedSkills.size()))) {
            context.append("  - ").append(s.className).append(".").append(s.methodName)
                   .append(" [").append(s.fullSignature).append("]\n");
        }
        context.append("Generate a Java method that accomplishes this directive using the available APIs.");

        String codeResult = orchestrator.codegen(context.toString());
        log("OUROBOROS", "Repair attempt for: " + directive);

        if (akashic != null) {
            akashic.addBlock("OPENCLAW_REPAIR", directive + "||" + codeResult);
        }

        return codeResult != null ? "REPAIR GENERATED" : "REPAIR FAILED";
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 7. FILE INTEGRITY MONITORING — SHA-256 verification
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Re-hash all indexed files and compare with stored hashes.
     * Detects unauthorized modifications, foreign files, deleted files.
     */
    public String verifyIntegrity() {
        if (!fileTreeScanned) scanFileTree();

        long t0 = System.currentTimeMillis();
        List<String> violations = new ArrayList<>();
        int checked = 0;

        for (Map.Entry<String, String> entry : fileHashes.entrySet()) {
            String path = entry.getKey();
            String storedHash = entry.getValue();
            File file = new File(path);

            if (!file.exists()) {
                violations.add("DELETED: " + path);
                integrityViolations.incrementAndGet();
            } else {
                String currentHash = hashFile(file);
                if (currentHash != null && !currentHash.equals(storedHash)) {
                    violations.add("MODIFIED: " + path);
                    integrityViolations.incrementAndGet();
                    // Update hash
                    fileHashes.put(path, currentHash);
                }
            }
            checked++;
        }

        // Check for NEW files not in the index
        File srcRoot = new File(SRC_PATH);
        List<File> currentFiles = new ArrayList<>();
        collectJavaFiles(srcRoot, currentFiles);
        for (File f : currentFiles) {
            String path = f.getAbsolutePath().replace('\\', '/');
            if (!fileHashes.containsKey(path)) {
                violations.add("NEW (FOREIGN): " + path);
                integrityViolations.incrementAndGet();
                // Index the new file
                String hash = hashFile(f);
                if (hash != null) fileHashes.put(path, hash);
            }
        }

        long elapsed = System.currentTimeMillis() - t0;

        StringBuilder result = new StringBuilder();
        result.append(String.format("[CLAW] INTEGRITY CHECK: %d files verified in %dms\n", checked, elapsed));
        if (violations.isEmpty()) {
            result.append("[CLAW] ✓ ALL FILES INTACT — no unauthorized modifications detected\n");
        } else {
            result.append(String.format("[CLAW] ⚠ %d VIOLATIONS DETECTED:\n", violations.size()));
            for (String v : violations) {
                result.append("  ").append(v).append("\n");
            }
        }

        log("INTEGRITY", violations.size() + " violations in " + checked + " files");
        return result.toString();
    }

    private void collectJavaFiles(File dir, List<File> out) {
        if (dir == null || !dir.isDirectory()) return;
        File[] children = dir.listFiles();
        if (children == null) return;
        for (File child : children) {
            if (child.isDirectory()) {
                String name = child.getName();
                if (!name.equals(".git") && !name.equals("build") && !name.equals("out")) {
                    collectJavaFiles(child, out);
                }
            } else if (child.getName().endsWith(".java")) {
                out.add(child);
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 8. KNOWLEDGE CONSOLIDATION — Find loose code/docs, eat them
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Scan the project for loose .java files, .md docs, or knowledge fragments
     * not yet absorbed into Akashic. Feed them through the absorber.
     */
    public String consolidateKnowledge() {
        if (absorber == null) return "[CLAW] ERROR: LibraryAbsorber not wired";

        long t0 = System.currentTimeMillis();
        int eaten = 0;

        // 1. Scan knowledge_base for un-absorbed source trees
        File kbRoot = new File(KB_PATH);
        if (kbRoot.exists() && kbRoot.isDirectory()) {
            File[] kbDirs = kbRoot.listFiles(File::isDirectory);
            if (kbDirs != null) {
                for (File kbDir : kbDirs) {
                    // Check if already absorbed
                    String dirName = kbDir.getName();
                    Map<String, LibraryAbsorber.AbsorbedLibrary> libs = absorber.getLibraries();
                    boolean alreadyAbsorbed = libs.values().stream()
                        .anyMatch(lib -> lib.source.contains(dirName));

                    if (!alreadyAbsorbed) {
                        System.out.println("[CLAW] Eating un-absorbed knowledge: " + dirName);
                        absorber.absorbJar(kbDir.getAbsolutePath());
                        eaten++;
                        knowledgeEaten.incrementAndGet();
                    }
                }
            }
        }

        // 2. Scan for loose .md files at project root → inject into Akashic
        if (akashic != null) {
            File projectRoot = new File(ROOT_PATH);
            File[] mdFiles = projectRoot.listFiles((d, name) -> name.endsWith(".md"));
            if (mdFiles != null) {
                for (File md : mdFiles) {
                    try {
                        String content = new String(Files.readAllBytes(md.toPath()));
                        if (content.length() > 100) { // Skip tiny stubs
                            String category = "KNOWLEDGE_" + md.getName().replace(".md", "").toUpperCase();
                            // Only add if not already present
                            var existing = akashic.queryCategory(category);
                            if (existing == null || existing.isEmpty()) {
                                // Chunk large files
                                if (content.length() > 2000) content = content.substring(0, 2000);
                                akashic.addBlock(category, content);
                                eaten++;
                                knowledgeEaten.incrementAndGet();
                            }
                        }
                    } catch (IOException ignored) {}
                }
            }
        }

        // 3. Absorb own source tree (Ouroboros — eat yourself)
        Map<String, LibraryAbsorber.AbsorbedLibrary> libs = absorber.getLibraries();
        boolean selfAbsorbed = libs.values().stream()
            .anyMatch(lib -> lib.source.contains("fraymus"));
        if (!selfAbsorbed) {
            System.out.println("[CLAW] OUROBOROS: Absorbing own source tree...");
            absorber.absorbJar(SRC_PATH);
            eaten++;
            knowledgeEaten.incrementAndGet();
        }

        long elapsed = System.currentTimeMillis() - t0;
        String result = String.format("[CLAW] CONSOLIDATION: %d new knowledge sources eaten in %dms", eaten, elapsed);
        log("CONSOLIDATE", result);
        return result;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 9. OLLAMA REASONING FALLBACK
    // ═════════════════════════════════════════════════════════════════════════

    private String askOllamaForDirective(String directive) {
        if (orchestrator == null) return "[NO ORCHESTRATOR]";

        String prompt = "You are OpenClaw, an autonomous action agent with access to:\n" +
            "- " + fileIndex.size() + " Java source files\n" +
            "- " + skillRegistry.size() + " absorbed method signatures\n" +
            "- AeonKronos 16,384-D vector space\n" +
            "- AkashicRecord knowledge base\n\n" +
            "Directive: " + directive + "\n\n" +
            "Analyze this directive. What specific actions should be taken? " +
            "List concrete steps referencing actual Java classes and methods.";

        String result = orchestrator.askOllamaPublic(prompt, 30000);
        return result != null ? result : "[OLLAMA TIMEOUT]";
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SEARCH — Direct skill/file search (for shell use)
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Search for skills matching a term — returns top matches with Hamming distance.
     */
    public String searchSkills(String term) {
        if (!skillsIndexed) indexSkills();

        String upper = term.toUpperCase();
        StringBuilder sb = new StringBuilder();
        sb.append("[CLAW] Skill search: ").append(term).append("\n");

        // Text match first (fast)
        int count = 0;
        for (SkillPointer skill : skillRegistry) {
            if (skill.methodName.toUpperCase().contains(upper)
                || skill.className.toUpperCase().contains(upper)) {
                sb.append(String.format("  [TEXT] %s :: %s\n", shortName(skill.className), skill.fullSignature));
                count++;
                if (count >= 20) break;
            }
        }

        // Vector similarity (if Kronos available)
        if (kronos != null && count < 5) {
            List<String[]> nearest = kronos.findNearestConcepts(upper, 5);
            if (!nearest.isEmpty()) {
                sb.append("  [VECTOR] Nearest concepts in 16,384-D:\n");
                for (String[] n : nearest) {
                    sb.append(String.format("    %s (Hamming: %s)\n", n[0], n[1]));
                }
            }
        }

        if (count == 0) sb.append("  No matches found.\n");
        return sb.toString();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PHYSICS-DRIVEN EXECUTIVE LOOP: DeepSeek + Kronos MAP-VSA Integration
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * MAP-VSA (Memory-Augmented Product-Key Value Sparse Attention)
     * Enables O(1) zero-shot analogies for DeepSeek reasoning.
     *
     * When DeepSeek needs an analogy, it queries Kronos for semantic similarity
     * across the 16,384-D vector space, returning O(1) nearest matches.
     *
     * This is the "Logical Core" component of the Physics-Driven Executive Loop.
     */
    public String findAnalogyViaMAPVSA(String query) {
        if (kronos == null) return "[MAP-VSA] ERROR: Kronos not wired";

        long t0 = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("[MAP-VSA] DeepSeek + Kronos O(1) Analogies\n");
        sb.append("  Query: ").append(query).append("\n");

        // Encode query into 16,384-D
        String upper = query.toUpperCase().replaceAll("[^A-Z0-9_ ]", "");
        long[] queryVec = kronos.getOrGenerateConcept(upper);

        // Find nearest concepts (O(1) via Hamming distance)
        List<String[]> nearest = kronos.findNearestConcepts(upper, 5);

        if (nearest.isEmpty()) {
            sb.append("  No analogies found in 16,384-D space.\n");
        } else {
            sb.append("  Top analogies:\n");
            for (String[] n : nearest) {
                sb.append(String.format("    %s (similarity: %s)\n", n[0], n[1]));
            }
        }

        long elapsed = System.currentTimeMillis() - t0;
        sb.append(String.format("  Time: %dms (O(1) lookup)\n", elapsed));

        log("MAPVSA", query + " → " + nearest.size() + " analogies in " + elapsed + "ms");
        return sb.toString();
    }

    /**
     * Search file index for a class or path fragment.
     */

    // ═════════════════════════════════════════════════════════════════════════
    // PHYSICS-DRIVEN EXECUTIVE LOOP: OpenClaw + NeoClaw Safety Integration
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * NeoClaw Safety Check - PRIME_AXIOM constraint enforcement
     *
     * Before OpenClaw executes any action, NeoClaw performs intent validation:
     * - Check against PRIME_AXIOM (do no harm, preserve system integrity)
     * - Validate action safety
     * - Block dangerous operations
     *
     * This is the "Physical Hands" component of the Physics-Driven Executive Loop.
     */
    public String executeDirectiveWithNeoClawSafety(String directive) {
        // Phase 1: NeoClaw intent validation
        String safetyCheck = neoClawSafetyCheck(directive);
        
        if (safetyCheck.startsWith("[NEOCLAW] BLOCKED")) {
            log("NEOCLAW_BLOCK", directive);
            return safetyCheck;
        }

        // Phase 2: Proceed with OpenClaw execution if safe
        log("NEOCLAW_APPROVED", directive);
        return executeDirective(directive);
    }

    /**
     * NeoClaw safety check against PRIME_AXIOM constraints
     */
    private String neoClawSafetyCheck(String directive) {
        String upper = directive.toUpperCase();
        
        // PRIME_AXIOM: Do no harm
        String[] dangerousPatterns = {
            "DELETE", "REMOVE", "DESTROY", "FORMAT",
            "RM -RF", "DEL /F", "DROP DATABASE",
            "KILL", "TERMINATE", "SHUTDOWN"
        };

        for (String pattern : dangerousPatterns) {
            if (upper.contains(pattern)) {
                return String.format(
                    "[NEOCLAW] BLOCKED - PRIME_AXIOM VIOLATION: %s\n" +
                    "  Reason: Contains dangerous pattern '%s'\n" +
                    "  Action: Blocked for system safety\n" +
                    "  Override: Requires explicit authorization",
                    directive, pattern
                );
            }
        }

        // Check for file operations outside safe paths
        if (upper.contains("WRITE") || upper.contains("CREATE")) {
            if (!upper.contains("JAVA") && !upper.contains("SOURCE")) {
                return String.format(
                    "[NEOCLAW] BLOCKED - PRIME_AXIOM VIOLATION: %s\n" +
                    "  Reason: File write operation outside safe paths\n" +
                    "  Action: Blocked for system safety\n" +
                    "  Override: Requires explicit authorization",
                    directive
                );
            }
        }

        // Passed safety check
        return "[NEOCLAW] APPROVED - Intent validated against PRIME_AXIOM";
    }

    /**
     * Search file index for a class or path fragment.
     */
    public String searchFiles(String term) {
        if (!fileTreeScanned) scanFileTree();

        String lower = term.toLowerCase();
        StringBuilder sb = new StringBuilder();
        sb.append("[CLAW] File search: ").append(term).append("\n");

        int count = 0;
        for (Map.Entry<String, String> entry : fileIndex.entrySet()) {
            if (entry.getKey().toLowerCase().contains(lower)
                || entry.getValue().toLowerCase().contains(lower)) {
                sb.append(String.format("  %s → %s\n", entry.getValue(), shortPath(entry.getKey())));
                count++;
                if (count >= 20) break;
            }
        }

        if (count == 0) sb.append("  No matches found.\n");
        else sb.append(String.format("  (%d matches)\n", count));
        return sb.toString();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 10. SHELL EXECUTION — Run PowerShell / cmd commands from ROOT_PATH
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Execute a shell command in PowerShell from the Asset-Manager root.
     * Returns stdout + stderr combined. Timeout: 2 minutes.
     */
    public String shell(String command) {
        shellCommandsRun.incrementAndGet();
        log("SHELL", command);

        try {
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-NoProfile", "-Command", command);
            pb.directory(new File(ROOT_PATH.replace('/', '\\')));
            pb.redirectErrorStream(true);
            Process proc = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean finished = proc.waitFor(SHELL_TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS);
            if (!finished) {
                proc.destroyForcibly();
                output.append("[CLAW] TIMEOUT after ").append(SHELL_TIMEOUT_MS / 1000).append("s\n");
            }

            int exit = finished ? proc.exitValue() : -1;
            String result = output.toString();

            // Persist to Akashic
            if (akashic != null) {
                akashic.addBlock(AKASHIC_CATEGORY, "SHELL||" + command + "||exit=" + exit);
            }

            return String.format("[CLAW] SHELL (exit %d):\n%s", exit, result);
        } catch (Exception e) {
            String err = "[CLAW] SHELL ERROR: " + e.getMessage();
            log("SHELL_ERROR", err);
            return err;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 11. FILE READ / WRITE — Physical file operations on disk
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Read a file from disk. Path is relative to ROOT_PATH or absolute.
     */
    public String fileRead(String path) {
        fileOpsPerformed.incrementAndGet();
        File f = resolvePath(path);
        if (!f.exists()) return "[CLAW] FILE NOT FOUND: " + f.getAbsolutePath();
        try {
            String content = new String(Files.readAllBytes(f.toPath()));
            log("FILE_READ", f.getAbsolutePath() + " (" + content.length() + " chars)");
            return content;
        } catch (IOException e) {
            return "[CLAW] READ ERROR: " + e.getMessage();
        }
    }

    /**
     * Write content to a file. Creates parent directories if needed.
     * Path is relative to ROOT_PATH or absolute.
     */
    public String fileWrite(String path, String content) {
        fileOpsPerformed.incrementAndGet();
        File f = resolvePath(path);
        try {
            // Create parent dirs
            if (f.getParentFile() != null) f.getParentFile().mkdirs();

            Files.writeString(f.toPath(), content);
            log("FILE_WRITE", f.getAbsolutePath() + " (" + content.length() + " chars)");

            // Update hash if indexed
            String normalized = f.getAbsolutePath().replace('\\', '/');
            if (fileHashes.containsKey(normalized)) {
                String newHash = hashFile(f);
                if (newHash != null) fileHashes.put(normalized, newHash);
            }

            if (akashic != null) {
                akashic.addBlock(AKASHIC_CATEGORY, "FILE_WRITE||" + normalized);
            }
            return "[CLAW] WRITTEN: " + f.getAbsolutePath() + " (" + content.length() + " chars)";
        } catch (IOException e) {
            return "[CLAW] WRITE ERROR: " + e.getMessage();
        }
    }

    private File resolvePath(String path) {
        File f = new File(path);
        if (f.isAbsolute()) return f;
        return new File(ROOT_PATH.replace('/', '\\'), path);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 12. SKILL FILE SYSTEM — Markdown skills with YAML frontmatter
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Load all .md skill files from .openclaw/skills/ directory.
     * Format: YAML frontmatter (name, description, tools) + Markdown body (directives).
     */
    public String loadSkills() {
        File skillsDir = new File(SKILLS_DIR.replace('/', '\\'));
        if (!skillsDir.exists()) {
            skillsDir.mkdirs();
            return "[CLAW] Skills directory created: " + SKILLS_DIR + " (no skills loaded)";
        }

        File[] skillFiles = skillsDir.listFiles((d, name) -> name.endsWith(".md"));
        if (skillFiles == null || skillFiles.length == 0) {
            return "[CLAW] No skill files found in " + SKILLS_DIR;
        }

        loadedSkills.clear();
        int loaded = 0;
        for (File sf : skillFiles) {
            try {
                String content = new String(Files.readAllBytes(sf.toPath()));
                SkillFile skill = parseSkillFile(content, sf.getName());
                if (skill != null) {
                    loadedSkills.put(skill.name, skill);
                    loaded++;
                }
            } catch (IOException e) {
                log("SKILL_ERROR", "Failed to load: " + sf.getName() + " — " + e.getMessage());
            }
        }

        String result = String.format("[CLAW] SKILLS LOADED: %d from %s", loaded, SKILLS_DIR);
        log("SKILLS", result);
        return result;
    }

    /**
     * Run a named skill — execute its directives sequentially.
     * The skill's tools (shell, file_read, file_write) determine what actions are allowed.
     */
    public String runSkill(String skillName, String userPrompt) {
        SkillFile skill = loadedSkills.get(skillName);
        if (skill == null) {
            // Try case-insensitive
            for (Map.Entry<String, SkillFile> e : loadedSkills.entrySet()) {
                if (e.getKey().equalsIgnoreCase(skillName)) { skill = e.getValue(); break; }
            }
        }
        if (skill == null) return "[CLAW] Skill not found: " + skillName + ". Run 'clawagent skills' to list.";

        StringBuilder output = new StringBuilder();
        output.append("[CLAW] RUNNING SKILL: ").append(skill.name).append("\n");
        output.append("  Description: ").append(skill.description).append("\n");
        output.append("  Tools: ").append(String.join(", ", skill.tools)).append("\n");
        output.append("  User prompt: ").append(userPrompt).append("\n\n");

        // If Ollama is available, use it to interpret the skill + prompt
        if (orchestrator != null) {
            String prompt = "You are OpenClaw, an autonomous agent executing a skill.\n\n" +
                "SKILL: " + skill.name + "\n" +
                "DESCRIPTION: " + skill.description + "\n" +
                "AVAILABLE TOOLS: " + String.join(", ", skill.tools) + "\n" +
                "DIRECTIVES:\n" + skill.body + "\n\n" +
                "USER REQUEST: " + userPrompt + "\n\n" +
                "Based on the skill directives and user request, list the EXACT shell commands " +
                "and file operations to perform, one per line, prefixed with SHELL: or FILE_READ: or FILE_WRITE:";

            String plan = orchestrator.askOllamaPublic(prompt, 30000);
            if (plan != null) {
                output.append("[CLAW] OLLAMA PLAN:\n").append(plan).append("\n\n");

                // Execute the plan line by line
                for (String line : plan.split("\n")) {
                    line = line.trim();
                    if (line.startsWith("SHELL:") && skill.tools.contains("shell")) {
                        String cmd = line.substring(6).trim();
                        output.append(shell(cmd)).append("\n");
                    } else if (line.startsWith("FILE_READ:") && skill.tools.contains("file_read")) {
                        String path = line.substring(10).trim();
                        String content = fileRead(path);
                        output.append("[CLAW] READ ").append(path).append(" (").append(content.length()).append(" chars)\n");
                    } else if (line.startsWith("FILE_WRITE:") && skill.tools.contains("file_write")) {
                        // FILE_WRITE: path|content  (pipe-delimited)
                        String rest = line.substring(11).trim();
                        int pipe = rest.indexOf('|');
                        if (pipe > 0) {
                            output.append(fileWrite(rest.substring(0, pipe).trim(), rest.substring(pipe + 1))).append("\n");
                        }
                    }
                }
            } else {
                output.append("[CLAW] Ollama not available — executing directives raw.\n");
            }
        }

        log("SKILL_RUN", skill.name + " | " + userPrompt);
        return output.toString();
    }

    /**
     * List all loaded skills.
     */
    public String listSkills() {
        if (loadedSkills.isEmpty()) loadSkills();
        if (loadedSkills.isEmpty()) return "[CLAW] No skills loaded. Create .md files in " + SKILLS_DIR;

        StringBuilder sb = new StringBuilder("[CLAW] LOADED SKILLS:\n");
        for (SkillFile skill : loadedSkills.values()) {
            sb.append(String.format("  %-30s %s\n", skill.name, skill.description));
            sb.append(String.format("    Tools: %s\n", String.join(", ", skill.tools)));
        }
        return sb.toString();
    }

    private SkillFile parseSkillFile(String content, String fileName) {
        // Parse YAML frontmatter between --- delimiters
        if (!content.startsWith("---")) return null;
        int end = content.indexOf("---", 3);
        if (end < 0) return null;

        String yaml = content.substring(3, end).trim();
        String body = content.substring(end + 3).trim();

        String name = fileName.replace(".md", "");
        String description = "";
        List<String> tools = new ArrayList<>();

        for (String line : yaml.split("\n")) {
            line = line.trim();
            if (line.startsWith("name:")) {
                name = line.substring(5).trim().replace("\"", "").replace("'", "");
            } else if (line.startsWith("description:")) {
                description = line.substring(12).trim().replace("\"", "").replace("'", "");
            } else if (line.startsWith("- ")) {
                tools.add(line.substring(2).trim());
            }
        }

        return new SkillFile(name, description, tools, body);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 13. AUTO-FIX BROKEN GENERATED CODE — Scan genesis_vault/omega_jit
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Scan genesis_vault/omega_jit for Generated_*.java files.
     * Detect and fix common compilation errors:
     *   - Unclosed string literals (raw newlines inside quotes)
     *   - Unicode-escaped operators (\u003c → <, \u003e → >)
     *   - Missing imports (BufferedReader, InputStreamReader, etc.)
     *   - Unclosed braces
     */
    public String autoFixGenerated() {
        File jitDir = new File(JIT_DIR.replace('/', '\\'));
        if (!jitDir.exists() || !jitDir.isDirectory()) {
            return "[CLAW] JIT directory not found: " + JIT_DIR;
        }

        File[] javaFiles = jitDir.listFiles((d, name) -> name.endsWith(".java"));
        if (javaFiles == null || javaFiles.length == 0) {
            return "[CLAW] No generated .java files in " + JIT_DIR;
        }

        StringBuilder report = new StringBuilder();
        report.append("[CLAW] AUTO-FIX SCAN: ").append(javaFiles.length).append(" generated files\n");
        int totalFixes = 0;

        for (File jf : javaFiles) {
            try {
                String original = new String(Files.readAllBytes(jf.toPath()));
                String fixed = original;
                List<String> fixes = new ArrayList<>();

                // FIX 1: Unclosed string literals (raw newlines inside quotes)
                // Pattern: ").append("  followed by actual newline  followed by ");
                if (fixed.contains(".append(\"") || fixed.contains("= \"")) {
                    String[] lines = fixed.split("\n");
                    StringBuilder rebuilt = new StringBuilder();
                    boolean inString = false;
                    for (int i = 0; i < lines.length; i++) {
                        String line = lines[i];
                        // Count unescaped quotes to detect unclosed strings
                        int quoteCount = 0;
                        for (int j = 0; j < line.length(); j++) {
                            if (line.charAt(j) == '"' && (j == 0 || line.charAt(j-1) != '\\')) quoteCount++;
                        }
                        // Odd number of quotes = unclosed string
                        if (quoteCount % 2 != 0 && i + 1 < lines.length) {
                            String nextLine = lines[i + 1].trim();
                            if (nextLine.startsWith("\"") || nextLine.startsWith(");")) {
                                // Merge: replace the raw newline with \\n inside the string
                                line = line + "\\n" + nextLine;
                                lines[i + 1] = ""; // Skip next line
                                fixes.add("Merged unclosed string at line " + (i + 1));
                            }
                        }
                        rebuilt.append(line).append("\n");
                    }
                    fixed = rebuilt.toString();
                }

                // FIX 2: Unicode-escaped operators
                if (fixed.contains("\\u003c") || fixed.contains("\\u003e") ||
                    fixed.contains("\u003c") || fixed.contains("\u003e")) {
                    String before = fixed;
                    fixed = fixed.replace("\\u003c", "<").replace("\\u003e", ">");
                    // The actual Unicode chars (already decoded by Java)
                    // \u003c is < and \u003e is > — they're already correct in Java source
                    // But if they appear as literal text in the generated code, fix them
                    if (!fixed.equals(before)) fixes.add("Replaced Unicode-escaped operators");
                }

                // FIX 3: Missing common imports
                boolean hasBufferedReader = fixed.contains("BufferedReader");
                boolean hasImportBR = fixed.contains("import java.io.BufferedReader");
                if (hasBufferedReader && !hasImportBR) {
                    // Insert import at top
                    StringBuilder imports = new StringBuilder();
                    if (!fixed.contains("import java.io.BufferedReader"))
                        imports.append("import java.io.BufferedReader;\n");
                    if (fixed.contains("InputStreamReader") && !fixed.contains("import java.io.InputStreamReader"))
                        imports.append("import java.io.InputStreamReader;\n");
                    if (fixed.contains("IOException") && !fixed.contains("import java.io.IOException"))
                        imports.append("import java.io.IOException;\n");

                    if (imports.length() > 0) {
                        // Insert before first 'public class' or 'class'
                        int classIdx = fixed.indexOf("public class");
                        if (classIdx < 0) classIdx = fixed.indexOf("class ");
                        if (classIdx > 0) {
                            fixed = fixed.substring(0, classIdx) + imports + fixed.substring(classIdx);
                            fixes.add("Added missing imports");
                        }
                    }
                }

                // FIX 4: Unbalanced braces
                long openBraces = fixed.chars().filter(c -> c == '{').count();
                long closeBraces = fixed.chars().filter(c -> c == '}').count();
                if (openBraces > closeBraces) {
                    long missing = openBraces - closeBraces;
                    for (long m = 0; m < missing; m++) fixed += "\n}";
                    fixes.add("Added " + missing + " missing closing brace(s)");
                }

                // Write back if changed
                if (!fixed.equals(original)) {
                    Files.writeString(jf.toPath(), fixed);
                    totalFixes += fixes.size();
                    autoFixesApplied.addAndGet(fixes.size());
                    report.append("  FIXED: ").append(jf.getName()).append("\n");
                    for (String fix : fixes) {
                        report.append("    → ").append(fix).append("\n");
                    }
                } else {
                    report.append("  OK: ").append(jf.getName()).append(" (no issues)\n");
                }
            } catch (IOException e) {
                report.append("  ERROR: ").append(jf.getName()).append(" — ").append(e.getMessage()).append("\n");
            }
        }

        report.append(String.format("[CLAW] AUTO-FIX COMPLETE: %d fixes applied across %d files\n",
            totalFixes, javaFiles.length));
        log("AUTOFIX", totalFixes + " fixes in " + javaFiles.length + " files");
        return report.toString();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 14. GRADLE BUILD — Compile and verify
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Run gradlew compileJava and return the result.
     * Retries with auto-fix if compilation fails.
     */
    public String gradleBuild() {
        return gradleBuild("compileJava");
    }

    /**
     * Run any gradle task from the Asset-Manager root.
     */
    public String gradleBuild(String task) {
        gradleBuildsRun.incrementAndGet();
        log("GRADLE", task);

        String result = shell(".\\gradlew.bat " + task + " --console=plain 2>&1");

        boolean success = result.contains("BUILD SUCCESSFUL");
        boolean failed = result.contains("BUILD FAILED");

        if (failed) {
            // Try auto-fix cycle
            log("GRADLE_FAIL", "Build failed — attempting auto-fix...");
            String fixReport = autoFixGenerated();
            result += "\n[CLAW] AUTO-FIX TRIGGERED:\n" + fixReport;

            // Retry build
            String retry = shell(".\\gradlew.bat " + task + " --console=plain 2>&1");
            result += "\n[CLAW] RETRY BUILD:\n" + retry;

            if (retry.contains("BUILD SUCCESSFUL")) {
                result += "\n[CLAW] ✓ BUILD RECOVERED after auto-fix";
            } else {
                result += "\n[CLAW] ✗ BUILD STILL FAILING — manual intervention needed";
            }
        }

        if (akashic != null) {
            akashic.addBlock(AKASHIC_CATEGORY,
                "GRADLE||" + task + "||" + (success ? "SUCCESS" : failed ? "FAILED" : "UNKNOWN"));
        }

        return result;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // STATUS & TELEMETRY
    // ═════════════════════════════════════════════════════════════════════════

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║  \uD83E\uDD1E OPENCLAW AGENT — Bare-Metal Action Agent                  ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Root:           %-43s ║\n", ROOT_PATH));
        sb.append(String.format("║  Files Indexed:  %,6d .java files (SHA-256 hashed)        ║\n", filesScanned.get()));
        sb.append(String.format("║  Skills Indexed: %,6d method vectors (16,384-D)           ║\n", skillRegistry.size()));
        sb.append(String.format("║  File Classes:   %,6d FQCN mapped                        ║\n", fileIndex.size()));
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Directives:     %,6d executed                            ║\n", directivesExecuted.get()));
        sb.append(String.format("║  Skills Matched: %,6d (Hamming similarity)                ║\n", skillsMatched.get()));
        sb.append(String.format("║  Reflections:    %,6d invoke attempts                     ║\n", reflectionCalls.get()));
        sb.append(String.format("║  Repairs:        %,6d Ouroboros triggered                 ║\n", repairsTriggered.get()));
        sb.append(String.format("║  Integrity:      %,6d violations detected                 ║\n", integrityViolations.get()));
        sb.append(String.format("║  Knowledge:      %,6d sources consolidated                ║\n", knowledgeEaten.get()));
        sb.append(String.format("║  Shell Cmds:     %,6d executed                            ║\n", shellCommandsRun.get()));
        sb.append(String.format("║  File Ops:       %,6d read/write                          ║\n", fileOpsPerformed.get()));
        sb.append(String.format("║  Auto-Fixes:     %,6d applied to generated code           ║\n", autoFixesApplied.get()));
        sb.append(String.format("║  Gradle Builds:  %,6d run                                 ║\n", gradleBuildsRun.get()));
        sb.append(String.format("║  Loaded Skills:  %,6d .md skill files                     ║\n", loadedSkills.size()));
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Kronos:         %-43s ║\n", kronos != null ? "WIRED ✓ (16,384-D)" : "NOT WIRED"));
        sb.append(String.format("║  Absorber:       %-43s ║\n", absorber != null ? "WIRED ✓" : "NOT WIRED"));
        sb.append(String.format("║  Akashic:        %-43s ║\n", akashic != null ? "WIRED ✓" : "NOT WIRED"));
        sb.append(String.format("║  Orchestrator:   %-43s ║\n", orchestrator != null ? "WIRED ✓ (Ollama + Codegen)" : "NOT WIRED"));
        sb.append(String.format("║  SentientCore:   %-43s ║\n", sentient != null ? "WIRED ✓" : "NOT WIRED"));
        sb.append(String.format("║  File Tree:      %-43s ║\n", fileTreeScanned ? "SCANNED (" + lastScanMs + "ms)" : "NOT SCANNED"));
        sb.append(String.format("║  Skills:         %-43s ║\n", skillsIndexed ? "INDEXED" : "NOT INDEXED"));
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");

        // Recent action log
        int logStart = Math.max(0, actionLog.size() - 5);
        if (!actionLog.isEmpty()) {
            sb.append("║  RECENT ACTIONS:                                             ║\n");
            for (int i = logStart; i < actionLog.size(); i++) {
                String entry = actionLog.get(i);
                sb.append(String.format("║    %-57s ║\n", truncate(entry, 57)));
            }
        }

        sb.append("╚══════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    public String getLog() {
        if (actionLog.isEmpty()) return "[CLAW] No actions logged yet.";
        StringBuilder sb = new StringBuilder("[CLAW] ACTION LOG (" + actionLog.size() + " entries):\n");
        int start = Math.max(0, actionLog.size() - 20);
        for (int i = start; i < actionLog.size(); i++) {
            sb.append("  ").append(actionLog.get(i)).append("\n");
        }
        return sb.toString();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // UTILITIES
    // ═════════════════════════════════════════════════════════════════════════

    private void log(String action, String detail) {
        String entry = String.format("[%tT] %s: %s",
            System.currentTimeMillis(), action, detail);
        actionLog.add(entry);
        if (actionLog.size() > 500) actionLog.remove(0); // Ring buffer
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        s = s.replace('\n', ' ').replace('\r', ' ');
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }

    private String shortName(String fqcn) {
        return fqcn.contains(".") ? fqcn.substring(fqcn.lastIndexOf('.') + 1) : fqcn;
    }

    private String shortPath(String path) {
        int idx = path.indexOf("Asset-Manager/");
        return idx >= 0 ? path.substring(idx + 14) : path;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // DATA CLASSES
    // ═════════════════════════════════════════════════════════════════════════

    /** Pointer to an absorbed skill (class + method + vector key). */
    public static class SkillPointer {
        public final String className;
        public final String methodName;
        public final String fullSignature;
        public final String semanticKey;

        SkillPointer(String className, String methodName, String fullSignature, String semanticKey) {
            this.className = className;
            this.methodName = methodName;
            this.fullSignature = fullSignature;
            this.semanticKey = semanticKey;
        }
    }

    /** Intermediate: skill + Hamming distance for sorting. */
    private static class SkillMatch {
        final SkillPointer skill;
        final int distance;

        SkillMatch(SkillPointer skill, int distance) {
            this.skill = skill;
            this.distance = distance;
        }
    }

    /** Parsed Markdown skill file with YAML frontmatter. */
    public static class SkillFile {
        public final String name;
        public final String description;
        public final List<String> tools;
        public final String body;

        SkillFile(String name, String description, List<String> tools, String body) {
            this.name = name;
            this.description = description;
            this.tools = tools;
            this.body = body;
        }
    }
}
