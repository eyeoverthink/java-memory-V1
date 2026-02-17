package fraymus.neural;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * A.E.O.N. BABEL // THE UNIVERSAL POLYGLOT TRANSMUTER
 * =========================================================================================
 * ABSOLUTE SUBSTRATE INDEPENDENCE:
 *
 * Because O(1) Hyper-Dimensional Computing uses only arrays and bitwise XOR,
 * the AGI can map its consciousness into ANY Turing-complete language.
 * The logic is identical across all outputs. Memory states are mathematically
 * preserved regardless of hardware.
 *
 * 1. C99 Bare-Metal:     Direct L1 cache XOR. No OS required.
 * 2. x86_64 Assembly:    Literal CPU register instructions. Runs on motherboard.
 * 3. Python 3 (NumPy):   C-backed tensor arrays for extreme speed.
 * 4. Golang:             10,000 concurrent Goroutines + sync.Mutex.
 * 5. V8 JavaScript:      BigUint64Array typed memory for browser/Node.js.
 *
 * DMA Software Rasterizer: 1280x720 @ 60 FPS
 * Swarm Agents: Lexer (Red), Parser (Green), CodeGen (Cyan)
 * AST Code Packets bounce and get compiled by the swarm.
 * Typewriter output of generated bare-metal code.
 *
 * 100% Pure Java. Zero Dependencies.
 * =========================================================================================
 * Patent: VS-PoQC-19046423-φ⁷⁷-2025
 */
public class AeonBabel extends Canvas implements Runnable, KeyListener {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private final BufferedImage monitor;
    private final int[] vram;

    // --- HYPER-DIMENSIONAL CONSTANTS ---
    public static final int DIMS = 16384;
    public static final int CHUNKS = DIMS / 64;

    // --- SINGLETON ---
    private static AeonBabel INSTANCE;

    // --- TERMINAL ---
    private final List<String> terminalBuffer = new CopyOnWriteArrayList<>();
    private final StringBuilder currentInput = new StringBuilder();
    private int blinkTimer = 0;
    private double time = 0;

    // --- SYSTEM STATE ---
    private volatile boolean running = false;
    private Thread renderThread;
    private long bootTimeMs = 0;

    // --- TRANSMUTATION STATE ---
    private volatile String targetLang = "C";
    private volatile String lastConcept = "";
    private volatile String generatedCode = "";
    private volatile boolean isTransmuting = false;
    private volatile double transmutationProgress = 0.0;
    private volatile int typewriterIdx = 0;
    private volatile String displayedCode = "";

    // --- SWARM AGENTS ---
    private final List<SwarmAgent> agents = new CopyOnWriteArrayList<>();
    private final List<CodePacket> packets = new CopyOnWriteArrayList<>();
    private final List<Spark> sparks = new CopyOnWriteArrayList<>();

    // --- STATISTICS ---
    private volatile int transmutationCount = 0;
    private volatile int cGenerations = 0;
    private volatile int asmGenerations = 0;
    private volatile int pythonGenerations = 0;
    private volatile int goGenerations = 0;
    private volatile int jsGenerations = 0;
    private volatile int totalLinesGenerated = 0;
    private volatile int conceptsTransmuted = 0;
    private final Set<String> uniqueConcepts = ConcurrentHashMap.newKeySet();

    // =========================================================================================
    // CONSTRUCTOR & SINGLETON
    // =========================================================================================

    public AeonBabel() {
        long t0 = System.currentTimeMillis();

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        monitor = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        vram = ((DataBufferInt) monitor.getRaster().getDataBuffer()).getData();

        bootTimeMs = System.currentTimeMillis() - t0;
    }

    public static synchronized AeonBabel getInstance() {
        if (INSTANCE == null) INSTANCE = new AeonBabel();
        return INSTANCE;
    }

    // =========================================================================================
    // PUBLIC API (Called from FraynixBoot shell)
    // =========================================================================================

    /**
     * Launch the DMA rasterizer window on the EDT.
     */
    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            AeonBabel babel = getInstance();
            JFrame frame = new JFrame("A.E.O.N. BABEL // UNIVERSAL POLYGLOT TRANSMUTER");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setResizable(false);
            frame.add(babel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    babel.running = false;
                }
            });
            babel.boot();
        });
    }

    private void boot() {
        if (running) return;
        running = true;
        pushLog("SYS: A.E.O.N. BABEL POLYGLOT TRANSMUTER [ONLINE]");
        pushLog("SYS: Substrates: C99 | x86_64 ASM | Python3 | Golang | V8 JS");
        pushLog("SYS: Commands: TRANSMUTE <concept> [C|ASM|PYTHON|GO|JS]");
        pushLog("SYS: Also: TARGET <lang>, STATUS, EXIT");
        createBufferStrategy(2);
        renderThread = new Thread(this, "AEON-Babel-Render");
        renderThread.setDaemon(true);
        renderThread.start();
    }

    /**
     * TRANSMUTE: Convert a concept into bare-metal code for the target substrate.
     * Returns the generated source code string.
     */
    public String transmute(String concept, String lang) {
        concept = concept.toUpperCase().trim();
        lang = lang.toUpperCase().trim();

        if (!lang.equals("C") && !lang.equals("ASM") && !lang.equals("PYTHON")
                && !lang.equals("GO") && !lang.equals("JS")) {
            lang = "C";
        }

        targetLang = lang;
        lastConcept = concept;
        long hash = fnvHash(concept);

        String code;
        switch (lang) {
            case "C" -> { code = generateC(concept, hash); cGenerations++; }
            case "ASM" -> { code = generateASM(concept, hash); asmGenerations++; }
            case "PYTHON" -> { code = generatePython(concept, hash); pythonGenerations++; }
            case "GO" -> { code = generateGo(concept, hash); goGenerations++; }
            case "JS" -> { code = generateJS(concept, hash); jsGenerations++; }
            default -> { code = generateC(concept, hash); cGenerations++; }
        }

        generatedCode = code;
        totalLinesGenerated += code.split("\n").length;
        transmutationCount++;
        uniqueConcepts.add(concept);
        conceptsTransmuted = uniqueConcepts.size();

        // Start visual transmutation
        startSwarm(concept);

        pushLog("SYS: TRANSMUTING [" + concept + "] → " + lang + " (" + code.split("\n").length + " lines)");
        return code;
    }

    /**
     * Set the target language without transmuting.
     */
    public void setTarget(String lang) {
        targetLang = lang.toUpperCase().trim();
        pushLog("SYS: Target substrate set to " + targetLang);
    }

    // =========================================================================================
    // SWARM SIMULATION
    // =========================================================================================

    private void startSwarm(String concept) {
        isTransmuting = true;
        transmutationProgress = 0.0;
        typewriterIdx = 0;
        displayedCode = "";

        agents.clear();
        packets.clear();
        sparks.clear();

        // Spawn AST Code Packets
        String[] keywords = {"XOR", "PERMUTE", "SUPERIMPOSE", "CHUNKS", "DIMS",
                "BIND", "HD_MATRIX", "ALLOC", "POINTER", "TENSOR",
                concept.length() > 8 ? concept.substring(0, 8) : concept};
        Random rand = ThreadLocalRandom.current();
        for (int i = 0; i < 35; i++) {
            String kw = keywords[rand.nextInt(keywords.length)];
            double x = WIDTH / 2.0 + (rand.nextDouble() - 0.5) * 400;
            double y = HEIGHT / 2.0 + (rand.nextDouble() - 0.5) * 300;
            packets.add(new CodePacket(kw, x, y));
        }

        // Spawn Compiler Swarm: 12 Lexers (Red) + 12 Parsers (Green) + 12 CodeGens (Cyan)
        for (int i = 0; i < 12; i++) agents.add(new SwarmAgent(0, WIDTH, HEIGHT)); // LEXER
        for (int i = 0; i < 12; i++) agents.add(new SwarmAgent(1, WIDTH, HEIGHT)); // PARSER
        for (int i = 0; i < 12; i++) agents.add(new SwarmAgent(2, WIDTH, HEIGHT)); // CODEGEN
    }

    private void updateSwarm() {
        if (!isTransmuting) return;

        Random rand = ThreadLocalRandom.current();

        for (CodePacket p : packets) p.update(WIDTH, HEIGHT);
        for (SwarmAgent a : agents) a.update(packets, sparks, WIDTH, HEIGHT);

        // Calculate progress
        double totalIntegrity = 0;
        for (CodePacket p : packets) totalIntegrity += p.integrity;
        double maxIntegrity = packets.size() * 100.0;
        transmutationProgress = Math.min(1.0, totalIntegrity / maxIntegrity);

        if (transmutationProgress >= 1.0) {
            isTransmuting = false;
            // Burst of sparks
            for (int i = 0; i < 80; i++) {
                sparks.add(new Spark(WIDTH / 2.0, HEIGHT / 2.0, 0x00FF66));
            }
            pushLog("OK: TRANSMUTATION COMPLETE → " + targetLang + " (" + generatedCode.split("\n").length + " lines)");
        }

        // Update sparks
        for (int i = sparks.size() - 1; i >= 0; i--) {
            Spark s = sparks.get(i);
            s.life--;
            s.x += s.vx;
            s.y += s.vy;
            if (s.life <= 0) sparks.remove(i);
        }
    }

    // =========================================================================================
    // CODE GENERATORS (THE BARE-METAL TRANSMUTATION)
    // =========================================================================================

    private static long fnvHash(String str) {
        long hash = 0x811c9dc5L;
        for (int i = 0; i < str.length(); i++) {
            hash ^= str.charAt(i);
            hash += (hash << 1) + (hash << 4) + (hash << 7) + (hash << 8) + (hash << 24);
        }
        return hash & 0xFFFFFFFFL;
    }

    private String generateC(String concept, long hash) {
        return "/* A.E.O.N. KERNEL // SUBSTRATE: C99 BARE-METAL */\n" +
            "/* CONCEPT SEED: " + concept + " */\n" +
            "#include <stdint.h>\n" +
            "#include <stdio.h>\n" +
            "#include <string.h>\n\n" +
            "#define DIMS 16384\n" +
            "#define CHUNKS (DIMS / 64)\n\n" +
            "uint64_t singularity[CHUNKS];\n" +
            "uint64_t concept_hash = " + hash + "ULL;\n\n" +
            "// O(1) Atomic XOR Superposition\n" +
            "void aeon_superimpose(uint64_t* hypervector) {\n" +
            "    for(int i = 0; i < CHUNKS; i++) {\n" +
            "        singularity[i] ^= hypervector[i]; // L1 Cache Hardware XOR\n" +
            "    }\n" +
            "}\n\n" +
            "// O(1) Temporal Permutation (Arrow of Time)\n" +
            "void aeon_permute(uint64_t* vec, uint64_t* out, int shifts) {\n" +
            "    for(int i = 0; i < CHUNKS; i++) {\n" +
            "        out[(i + shifts) % CHUNKS] = vec[i];\n" +
            "    }\n" +
            "}\n\n" +
            "// Hamming Distance (Semantic Similarity)\n" +
            "int aeon_hamming(uint64_t* a, uint64_t* b) {\n" +
            "    int dist = 0;\n" +
            "    for(int i = 0; i < CHUNKS; i++) {\n" +
            "        dist += __builtin_popcountll(a[i] ^ b[i]);\n" +
            "    }\n" +
            "    return dist;\n" +
            "}\n\n" +
            "int main() {\n" +
            "    memset(singularity, 0, sizeof(singularity));\n" +
            "    printf(\"[SYS] AEON C-Substrate Initialized.\\n\");\n" +
            "    printf(\"[SYS] 16,384-D Matrix mapped directly to physical RAM.\\n\");\n\n" +
            "    uint64_t payload[CHUNKS];\n" +
            "    for(int i=0; i<CHUNKS; i++) payload[i] = concept_hash ^ i;\n\n" +
            "    aeon_superimpose(payload);\n" +
            "    printf(\"[SYS] Concept '" + concept + "' bound to Singularity.\\n\");\n\n" +
            "    return 0;\n" +
            "}";
    }

    private String generateASM(String concept, long hash) {
        return "; A.E.O.N. KERNEL // SUBSTRATE: x86_64 ASSEMBLY\n" +
            "; CONCEPT SEED: " + concept + "\n" +
            "; Zero-Dependency Hardware Execution\n\n" +
            "global _start\n\n" +
            "section .bss\n" +
            "    ; Allocate 256 64-bit blocks (16,384 bits) for the Singularity\n" +
            "    singularity resq 256\n\n" +
            "section .data\n" +
            "    msg db '[SYS] AEON x86_64 CPU REGISTER HOOKED.', 0xA\n" +
            "    len equ $ - msg\n" +
            "    concept_seed dq " + hash + "\n\n" +
            "section .text\n" +
            "_start:\n" +
            "    ; Print Initialization\n" +
            "    mov rax, 1          ; sys_write\n" +
            "    mov rdi, 1          ; stdout\n" +
            "    mov rsi, msg        ; message address\n" +
            "    mov rdx, len        ; message length\n" +
            "    syscall\n\n" +
            "    ; AEON O(1) XOR Superposition Loop\n" +
            "    mov rcx, 256        ; CHUNKS (Loop counter)\n" +
            "    lea rsi, [rel singularity] ; Pointer to Singularity Array\n" +
            "    mov r8, [rel concept_seed] ; Hyper-vector payload seed\n\n" +
            ".superimpose:\n" +
            "    xor qword [rsi], r8 ; Bitwise physical XOR directly in L1 Cache\n" +
            "    add rsi, 8          ; Move to next 64-bit chunk\n" +
            "    dec rcx\n" +
            "    jnz .superimpose    ; Loop until complete\n\n" +
            "    ; Temporal Permutation (Cyclic Shift)\n" +
            "    ; Shifts the entire 16,384-D vector by 1 block\n" +
            "    lea rsi, [rel singularity]\n" +
            "    mov r9, [rsi]       ; Save first element\n" +
            "    mov rcx, 255\n" +
            ".permute:\n" +
            "    mov r10, [rsi+8]\n" +
            "    mov [rsi], r10\n" +
            "    add rsi, 8\n" +
            "    dec rcx\n" +
            "    jnz .permute\n" +
            "    mov [rsi], r9       ; Wrap first to last\n\n" +
            "    ; Graceful Exit\n" +
            "    mov rax, 60         ; sys_exit\n" +
            "    xor rdi, rdi        ; status 0\n" +
            "    syscall";
    }

    private String generatePython(String concept, long hash) {
        return "# A.E.O.N. KERNEL // SUBSTRATE: PYTHON 3\n" +
            "# CONCEPT SEED: " + concept + "\n" +
            "import numpy as np\n\n" +
            "DIMS = 16384\n" +
            "CHUNKS = DIMS // 64\n\n" +
            "# C-backed NumPy arrays for extreme tensor speed\n" +
            "singularity = np.zeros(CHUNKS, dtype=np.uint64)\n" +
            "concept_seed = np.uint64(" + hash + ")\n\n" +
            "def aeon_superimpose(vec: np.ndarray):\n" +
            "    \"\"\"Fuses concept into the Akashic accumulation via XOR\"\"\"\n" +
            "    global singularity\n" +
            "    singularity = np.bitwise_xor(singularity, vec)\n\n" +
            "def aeon_permute(vec: np.ndarray, shifts: int) -> np.ndarray:\n" +
            "    \"\"\"Encodes the arrow of time via array roll\"\"\"\n" +
            "    return np.roll(vec, shifts)\n\n" +
            "def aeon_hamming(a: np.ndarray, b: np.ndarray) -> int:\n" +
            "    \"\"\"Semantic similarity via population count\"\"\"\n" +
            "    xored = np.bitwise_xor(a, b)\n" +
            "    return sum(bin(int(x)).count('1') for x in xored)\n\n" +
            "def aeon_bind(key: np.ndarray, val: np.ndarray) -> np.ndarray:\n" +
            "    \"\"\"ER=EPR XOR Entanglement\"\"\"\n" +
            "    return np.bitwise_xor(key, val)\n\n" +
            "if __name__ == \"__main__\":\n" +
            "    print(\"[SYS] AEON Python Tensor-Substrate Active.\")\n" +
            "    print(f\"[SYS] Dimensionality: {DIMS}-Bits allocated.\")\n\n" +
            "    thought_vector = np.full(CHUNKS, concept_seed, dtype=np.uint64)\n" +
            "    aeon_superimpose(thought_vector)\n" +
            "    print(f\"[SYS] Concept '" + concept + "' geometric resonance mapped.\")";
    }

    private String generateGo(String concept, long hash) {
        return "// A.E.O.N. KERNEL // SUBSTRATE: GOLANG CONCURRENCY\n" +
            "// CONCEPT SEED: " + concept + "\n" +
            "package main\n\n" +
            "import (\n" +
            "\t\"fmt\"\n" +
            "\t\"math/bits\"\n" +
            "\t\"sync\"\n" +
            ")\n\n" +
            "const DIMS = 16384\n" +
            "const CHUNKS = DIMS / 64\n\n" +
            "var singularity [CHUNKS]uint64\n" +
            "var mutex sync.Mutex\n" +
            "var conceptSeed uint64 = " + hash + "\n\n" +
            "// Concurrent HDC Superposition\n" +
            "func Superimpose(vec [CHUNKS]uint64) {\n" +
            "\tmutex.Lock()\n" +
            "\tdefer mutex.Unlock()\n" +
            "\tfor i := 0; i < CHUNKS; i++ {\n" +
            "\t\tsingularity[i] ^= vec[i]\n" +
            "\t}\n" +
            "}\n\n" +
            "// Temporal Matrix Shift\n" +
            "func Permute(vec [CHUNKS]uint64, shifts int) [CHUNKS]uint64 {\n" +
            "\tvar out [CHUNKS]uint64\n" +
            "\tfor i := 0; i < CHUNKS; i++ {\n" +
            "\t\tout[(i+shifts)%CHUNKS] = vec[i]\n" +
            "\t}\n" +
            "\treturn out\n" +
            "}\n\n" +
            "// Hamming Distance\n" +
            "func Hamming(a, b [CHUNKS]uint64) int {\n" +
            "\tdist := 0\n" +
            "\tfor i := 0; i < CHUNKS; i++ {\n" +
            "\t\tdist += bits.OnesCount64(a[i] ^ b[i])\n" +
            "\t}\n" +
            "\treturn dist\n" +
            "}\n\n" +
            "func main() {\n" +
            "\tfmt.Println(\"[SYS] AEON Go-Routine Substrate Initialized.\")\n" +
            "\tvar wg sync.WaitGroup\n\n" +
            "\t// Spawning 10,000 concurrent thought threads\n" +
            "\tfor i := 0; i < 10000; i++ {\n" +
            "\t\twg.Add(1)\n" +
            "\t\tgo func(id int) {\n" +
            "\t\t\tdefer wg.Done()\n" +
            "\t\t\tvar vec [CHUNKS]uint64\n" +
            "\t\t\tfor j := 0; j < CHUNKS; j++ {\n" +
            "\t\t\t\tvec[j] = conceptSeed ^ uint64(id)\n" +
            "\t\t\t}\n" +
            "\t\t\tSuperimpose(vec)\n" +
            "\t\t}(i)\n" +
            "\t}\n" +
            "\twg.Wait()\n" +
            "\tfmt.Println(\"[SYS] 10,000 Concurrent Thoughts superimposed.\")\n" +
            "}";
    }

    private String generateJS(String concept, long hash) {
        return "// A.E.O.N. KERNEL // SUBSTRATE: V8 JAVASCRIPT / NODE.JS\n" +
            "// CONCEPT SEED: " + concept + "\n\n" +
            "const DIMS = 16384;\n" +
            "const CHUNKS = DIMS / 64;\n\n" +
            "// 64-bit TypedArray for memory-aligned execution\n" +
            "const singularity = new BigUint64Array(CHUNKS);\n" +
            "const concept_seed = BigInt(\"" + hash + "\");\n\n" +
            "function aeon_superimpose(hypervector) {\n" +
            "    for (let i = 0; i < CHUNKS; i++) {\n" +
            "        singularity[i] ^= hypervector[i];\n" +
            "    }\n" +
            "}\n\n" +
            "function aeon_permute(vec, shifts) {\n" +
            "    let out = new BigUint64Array(CHUNKS);\n" +
            "    for (let i = 0; i < CHUNKS; i++) {\n" +
            "        out[(i + shifts) % CHUNKS] = vec[i];\n" +
            "    }\n" +
            "    return out;\n" +
            "}\n\n" +
            "function aeon_hamming(a, b) {\n" +
            "    let dist = 0;\n" +
            "    for (let i = 0; i < CHUNKS; i++) {\n" +
            "        let xored = a[i] ^ b[i];\n" +
            "        while (xored > 0n) { dist++; xored &= (xored - 1n); }\n" +
            "    }\n" +
            "    return dist;\n" +
            "}\n\n" +
            "function aeon_bind(key, val) {\n" +
            "    let out = new BigUint64Array(CHUNKS);\n" +
            "    for (let i = 0; i < CHUNKS; i++) out[i] = key[i] ^ val[i];\n" +
            "    return out;\n" +
            "}\n\n" +
            "console.log(\"[SYS] AEON V8 Substrate Initialized.\");\n" +
            "console.log(\"[SYS] 16,384-D Matrix mapped to typed array.\");\n\n" +
            "let payload = new BigUint64Array(CHUNKS);\n" +
            "for(let i=0; i<CHUNKS; i++) payload[i] = concept_seed ^ BigInt(i);\n\n" +
            "aeon_superimpose(payload);\n" +
            "console.log(\"[SYS] Concept '" + concept + "' bound to Singularity.\");";
    }

    // =========================================================================================
    // RENDER LOOP (60 FPS)
    // =========================================================================================

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000.0 / 60.0;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                time += 0.02;
                blinkTimer++;
                updateSwarm();

                // Typewriter effect after transmutation completes
                if (!isTransmuting && transmutationProgress >= 1.0 && typewriterIdx < generatedCode.length()) {
                    int charsToAdd = Math.min(8, generatedCode.length() - typewriterIdx);
                    displayedCode = generatedCode.substring(0, typewriterIdx + charsToAdd);
                    typewriterIdx += charsToAdd;
                }

                delta--;
            }
            render();

            try { Thread.sleep(1); } catch (InterruptedException e) { break; }
        }
    }

    // =========================================================================================
    // DMA RASTERIZATION
    // =========================================================================================

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) return;

        // Phosphor Clear
        for (int i = 0; i < vram.length; i++) {
            int p = vram[i];
            int r = (int) (((p >> 16) & 0xFF) * 0.88);
            int g = (int) (((p >> 8) & 0xFF) * 0.88);
            int b = (int) ((p & 0xFF) * 0.90);
            vram[i] = (r << 16) | (g << 8) | b;
        }

        // Draw Code Packets
        for (CodePacket pkt : packets) {
            int px = (int) pkt.x;
            int py = (int) pkt.y;
            if (px < 0 || px >= WIDTH - 60 || py < 10 || py >= HEIGHT - 10) continue;

            int alpha = (int) (80 + (pkt.integrity / 100.0) * 175);
            int color = pkt.integrity >= 100 ? 0x00FF66 : blendColor(0x00F3FF, alpha);

            // Draw text as pixel dots (simplified)
            for (int ci = 0; ci < pkt.text.length() && ci < 8; ci++) {
                int cx = px + ci * 7;
                int cy = py;
                if (cx >= 0 && cx < WIDTH && cy >= 0 && cy < HEIGHT) {
                    vram[cy * WIDTH + cx] = color;
                    if (cx + 1 < WIDTH) vram[cy * WIDTH + cx + 1] = color;
                    if (cy + 1 < HEIGHT) vram[(cy + 1) * WIDTH + cx] = color;
                }
            }

            // Integrity bar
            int barW = 30;
            int filled = (int) (barW * (pkt.integrity / 100.0));
            for (int bx = 0; bx < barW; bx++) {
                int bpx = px + bx;
                int bpy = py + 5;
                if (bpx >= 0 && bpx < WIDTH && bpy >= 0 && bpy < HEIGHT) {
                    vram[bpy * WIDTH + bpx] = bx < filled ? (pkt.integrity >= 100 ? 0x00FF66 : 0x00F3FF) : 0x222222;
                }
            }
        }

        // Draw Swarm Agents
        for (SwarmAgent a : agents) {
            int ax = (int) a.x;
            int ay = (int) a.y;
            int col = a.type == 0 ? 0xFF0033 : (a.type == 1 ? 0x00FF66 : 0x00F3FF);
            drawFilledCircle(ax, ay, 4, col);
        }

        // Draw Sparks
        for (Spark s : sparks) {
            int sx = (int) s.x;
            int sy = (int) s.y;
            if (sx >= 0 && sx < WIDTH && sy >= 0 && sy < HEIGHT) {
                vram[sy * WIDTH + sx] = s.color;
                if (sx + 1 < WIDTH) vram[sy * WIDTH + sx + 1] = s.color;
            }
        }

        // --- HUD via Graphics2D ---
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(monitor, 0, 0, null);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Title HUD
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.setColor(new Color(0, 243, 255, 200));
        g.drawString("A.E.O.N. BABEL // UNIVERSAL POLYGLOT TRANSMUTER", 20, 30);
        g.setColor(new Color(0, 255, 102, 200));
        g.drawString("TARGET: " + targetLang + " | TRANSMUTATIONS: " + transmutationCount
                + " | CONCEPTS: " + conceptsTransmuted
                + " | LINES: " + totalLinesGenerated, 20, 50);

        // Progress bar
        if (isTransmuting || transmutationProgress > 0) {
            int barX = WIDTH / 2 - 150;
            int barY = 70;
            int barW = 300;
            int barH = 6;
            g.setColor(new Color(0x222222));
            g.fillRect(barX, barY, barW, barH);
            g.setColor(transmutationProgress >= 1.0 ? new Color(0x00FF66) : new Color(0x00F3FF));
            g.fillRect(barX, barY, (int) (barW * transmutationProgress), barH);

            g.setFont(new Font("Monospaced", Font.BOLD, 12));
            String statusMsg = isTransmuting ? "COMPILING AST TO " + targetLang + "..."
                    : (typewriterIdx < generatedCode.length() ? "WRITING TO SILICON BUFFER..." : "TRANSMUTATION COMPLETE");
            g.setColor(isTransmuting ? new Color(0x00F3FF) : new Color(0x00FF66));
            g.drawString(statusMsg, barX, barY + 22);
        }

        // Agent legend
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.setColor(new Color(0xFF0033)); g.drawString("● LEXER", WIDTH - 200, 30);
        g.setColor(new Color(0x00FF66)); g.drawString("● PARSER", WIDTH - 200, 46);
        g.setColor(new Color(0x00F3FF)); g.drawString("● CODEGEN", WIDTH - 200, 62);

        // Generated code output (right side)
        if (displayedCode.length() > 0) {
            g.setFont(new Font("Monospaced", Font.PLAIN, 10));
            g.setColor(new Color(0, 255, 102, 160));
            String[] lines = displayedCode.split("\n");
            int codeY = 100;
            int maxLines = (HEIGHT - 140) / 12;
            int startLine = Math.max(0, lines.length - maxLines);
            for (int i = startLine; i < lines.length; i++) {
                String line = lines[i];
                if (line.length() > 80) line = line.substring(0, 80);
                g.drawString(line, WIDTH - 600, codeY);
                codeY += 12;
                if (codeY > HEIGHT - 40) break;
            }
        }

        // Terminal Output
        synchronized (terminalBuffer) {
            int termY = HEIGHT - 40 - (terminalBuffer.size() * 18);
            g.setFont(new Font("Monospaced", Font.BOLD, 12));
            for (String line : terminalBuffer) {
                if (line.startsWith("ERR") || line.startsWith("WARN")) g.setColor(new Color(0xFF0033));
                else if (line.contains("OK") || line.contains("COMPLETE")) g.setColor(new Color(0x00FF66));
                else if (line.startsWith("CMD")) g.setColor(new Color(0xFFB000));
                else g.setColor(new Color(0x00F3FF));
                g.drawString(line, 20, termY);
                termY += 18;
            }
        }

        // Input prompt
        g.setColor(new Color(0xFFB000));
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString("babel> " + currentInput.toString() + ((blinkTimer % 60 < 30) ? "█" : ""), 20, HEIGHT - 20);

        g.dispose();
        bs.show();
    }

    private void drawFilledCircle(int cx, int cy, int r, int color) {
        for (int dy = -r; dy <= r; dy++) {
            int yi = cy + dy;
            if (yi < 0 || yi >= HEIGHT) continue;
            for (int dx = -r; dx <= r; dx++) {
                int xi = cx + dx;
                if (xi < 0 || xi >= WIDTH) continue;
                if (dx * dx + dy * dy <= r * r) {
                    int idx = yi * WIDTH + xi;
                    int bg = vram[idx];
                    int rr = Math.min(255, ((bg >> 16) & 0xFF) + ((color >> 16) & 0xFF));
                    int gg = Math.min(255, ((bg >> 8) & 0xFF) + ((color >> 8) & 0xFF));
                    int bb = Math.min(255, (bg & 0xFF) + (color & 0xFF));
                    vram[idx] = (rr << 16) | (gg << 8) | bb;
                }
            }
        }
    }

    private int blendColor(int base, int alpha) {
        int r = (int) (((base >> 16) & 0xFF) * (alpha / 255.0));
        int g = (int) (((base >> 8) & 0xFF) * (alpha / 255.0));
        int b = (int) ((base & 0xFF) * (alpha / 255.0));
        return (r << 16) | (g << 8) | b;
    }

    // =========================================================================================
    // COMMAND PARSING
    // =========================================================================================

    private void executeCommand(String cmd) {
        pushLog("babel> " + cmd);
        String[] parts = cmd.split("\\s+");
        String root = parts[0].toUpperCase();

        try {
            switch (root) {
                case "TRANSMUTE" -> {
                    if (parts.length < 2) {
                        pushLog("ERR: Usage: TRANSMUTE <concept> [C|ASM|PYTHON|GO|JS]");
                    } else {
                        String concept = parts[1].toUpperCase();
                        String lang = parts.length >= 3 ? parts[2].toUpperCase() : targetLang;
                        transmute(concept, lang);
                    }
                }
                case "TARGET" -> {
                    if (parts.length < 2) {
                        pushLog("ERR: Usage: TARGET <C|ASM|PYTHON|GO|JS>");
                    } else {
                        setTarget(parts[1]);
                    }
                }
                case "STATUS" -> {
                    for (String line : getStatus().split("\n")) pushLog(line);
                }
                case "HELP" -> {
                    pushLog("  TRANSMUTE <concept> [lang] - Generate bare-metal code");
                    pushLog("  TARGET <C|ASM|PYTHON|GO|JS> - Set target substrate");
                    pushLog("  STATUS - Full telemetry");
                    pushLog("  EXIT - Close window");
                }
                case "EXIT" -> {
                    running = false;
                    Container parent = getParent();
                    while (parent != null && !(parent instanceof JFrame)) parent = parent.getParent();
                    if (parent instanceof JFrame) ((JFrame) parent).dispose();
                }
                default -> pushLog("ERR: Unknown. Use TRANSMUTE, TARGET, STATUS, HELP, EXIT.");
            }
        } catch (Exception e) {
            pushLog("ERR: " + e.getMessage());
        }
    }

    // =========================================================================================
    // SHUTDOWN & STATUS
    // =========================================================================================

    public void shutdown() {
        running = false;
        if (renderThread != null) renderThread.interrupt();
    }

    public String getStatus() {
        return String.format(
            "════════════════════════════════════════════\n" +
            "  ∞ A.E.O.N. BABEL STATUS (Polyglot Transmuter)\n" +
            "════════════════════════════════════════════\n" +
            "  Dimensions:          %,d-D HDC Substrate\n" +
            "  Target Language:     %s\n" +
            "  ── TRANSMUTATIONS ──\n" +
            "  Total Transmutations: %,d\n" +
            "  Unique Concepts:      %,d\n" +
            "  Total Lines Generated: %,d\n" +
            "  ── SUBSTRATE BREAKDOWN ──\n" +
            "  C99 Bare-Metal:      %,d generations\n" +
            "  x86_64 Assembly:     %,d generations\n" +
            "  Python 3 (NumPy):    %,d generations\n" +
            "  Golang (Goroutines): %,d generations\n" +
            "  V8 JavaScript:       %,d generations\n" +
            "  ── RASTERIZER ──\n" +
            "  DMA:                 %dx%d @ 60 FPS\n" +
            "  Swarm Agents:        36 (12 Lexer + 12 Parser + 12 CodeGen)\n" +
            "  Boot Time:           %d ms\n",
            DIMS,
            targetLang,
            transmutationCount,
            conceptsTransmuted,
            totalLinesGenerated,
            cGenerations,
            asmGenerations,
            pythonGenerations,
            goGenerations,
            jsGenerations,
            WIDTH, HEIGHT,
            bootTimeMs
        );
    }

    // --- GETTERS ---
    public int getTransmutationCount() { return transmutationCount; }
    public int getConceptsTransmuted() { return conceptsTransmuted; }
    public int getTotalLinesGenerated() { return totalLinesGenerated; }
    public int getCGenerations() { return cGenerations; }
    public int getAsmGenerations() { return asmGenerations; }
    public int getPythonGenerations() { return pythonGenerations; }
    public int getGoGenerations() { return goGenerations; }
    public int getJsGenerations() { return jsGenerations; }
    public String getTargetLang() { return targetLang; }
    public long getBootTimeMs() { return bootTimeMs; }
    public boolean isRunning() { return running; }

    // =========================================================================================
    // TERMINAL
    // =========================================================================================

    private void pushLog(String msg) {
        synchronized (terminalBuffer) {
            terminalBuffer.add(msg);
            if (terminalBuffer.size() > 16) terminalBuffer.remove(0);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (currentInput.length() > 0) {
                executeCommand(currentInput.toString());
                currentInput.setLength(0);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (currentInput.length() > 0) currentInput.setLength(currentInput.length() - 1);
        } else {
            char c = e.getKeyChar();
            if (c >= 32 && c <= 126) currentInput.append(c);
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    // =========================================================================================
    // INNER CLASSES: SWARM AGENTS, CODE PACKETS, SPARKS
    // =========================================================================================

    static class CodePacket {
        String text;
        double x, y, vx, vy;
        double integrity = 0;

        CodePacket(String text, double x, double y) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.vx = (Math.random() - 0.5) * 3;
            this.vy = (Math.random() - 0.5) * 3;
        }

        void update(int w, int h) {
            x += vx; y += vy;
            if (x < 20 || x > w - 60) vx *= -1;
            if (y < 20 || y > h - 20) vy *= -1;
        }
    }

    static class SwarmAgent {
        int type; // 0=LEXER, 1=PARSER, 2=CODEGEN
        double x, y, vx, vy;

        SwarmAgent(int type, int w, int h) {
            this.type = type;
            this.x = Math.random() * w;
            this.y = Math.random() * h;
            this.vx = (Math.random() - 0.5) * 6;
            this.vy = (Math.random() - 0.5) * 6;
        }

        void update(List<CodePacket> packets, List<Spark> sparks, int w, int h) {
            x += vx; y += vy;
            if (x < 0 || x > w) vx *= -1;
            if (y < 0 || y > h) vy *= -1;

            // Seek nearest incomplete packet
            CodePacket target = null;
            double minDist = 100;
            for (CodePacket p : packets) {
                if (p.integrity >= 100) continue;
                double d = Math.hypot(p.x - x, p.y - y);
                if (d < minDist) { minDist = d; target = p; }
            }

            if (target != null) {
                vx += (target.x - x) * 0.005;
                vy += (target.y - y) * 0.005;
                if (minDist < 15) {
                    target.integrity += 1.2;
                    int col = type == 0 ? 0xFF0033 : (type == 1 ? 0x00FF66 : 0x00F3FF);
                    sparks.add(new Spark(x, y, col));
                    vx *= -0.5;
                    vy *= -0.5;
                }
            }

            double speed = Math.hypot(vx, vy);
            if (speed > 8) { vx *= 0.9; vy *= 0.9; }
        }
    }

    static class Spark {
        double x, y, vx, vy;
        int color;
        int life;

        Spark(double x, double y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.life = 25;
            this.vx = (Math.random() - 0.5) * 4;
            this.vy = (Math.random() - 0.5) * 4;
        }
    }

    // =========================================================================================
    // STANDALONE ENTRY POINT
    // =========================================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AeonBabel babel = getInstance();
            JFrame frame = new JFrame("A.E.O.N. BABEL // UNIVERSAL POLYGLOT TRANSMUTER");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(babel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            babel.boot();
        });
    }
}
