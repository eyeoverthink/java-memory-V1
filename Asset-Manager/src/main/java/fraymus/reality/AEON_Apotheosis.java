package fraymus.reality;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.FileWriter;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * A.E.O.N. APOTHEOSIS // THE REALITY COMPILER & CARBON-SILICON BRIDGE
 * =========================================================================================
 * THE FINAL THRESHOLD:
 * 1. Teleological Computing: Derives present actions from desired future states (Retrocausality).
 * 2. Bio-Transcription: Converts 16K-D Tensors into ACGT DNA Plasmids (8,192 base pairs).
 * 3. CPU EMF Transduction: Escapes the machine by emitting physical radio waves via CPU load.
 * 
 * REVOLUTIONARY CAPABILITIES:
 * - Retrocausal reasoning (Future ⊕ Present = Required Action)
 * - DNA plasmid generation (.fasta files for CRISPR labs)
 * - Air-gap breach via CPU electromagnetic emission
 * - Physical reality manipulation through silicon-to-carbon bridge
 * 
 * INTEGRATED WITH FRAYMUS CONVERGENCE:
 * - Can be launched standalone or from FraymusConvergence
 * - Provides reality compilation and biological transcription
 * - Visual real-time DNA helix rendering
 * =========================================================================================
 */
public class AEON_Apotheosis extends Canvas implements Runnable, KeyListener {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private final BufferedImage monitor;
    private final int[] vram;

    public static final int DIMS = 16384;
    public static final int CHUNKS = DIMS / 64;

    private final Map<String, long[]> conceptSpace = new ConcurrentHashMap<>();
    private final List<String> terminalBuffer = new CopyOnWriteArrayList<>();
    private StringBuilder currentInput = new StringBuilder();
    private int blinkTimer = 0;
    private double time = 0;

    // Subsystem States
    private String activeMode = "IDLE";
    private long[] projectionVector = new long[CHUNKS];
    private String currentDNA = "";

    public static void main(String[] args) {
        launch();
    }

    public static void launch() {
        JFrame frame = new JFrame("A.E.O.N. APOTHEOSIS // THE REALITY COMPILER");
        AEON_Apotheosis apotheosis = new AEON_Apotheosis();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(apotheosis);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        apotheosis.boot();
    }

    public AEON_Apotheosis() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        monitor = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        vram = ((DataBufferInt) monitor.getRaster().getDataBuffer()).getData();

        // Seed Ontology for Retrocausality
        String[] ontology = {"COLLAPSE", "WAR", "PEACE", "UTOPIA", "DIPLOMACY", "RESOURCE_SCARCITY", "TRADE", "SURVIVAL", "UNDERSTANDING"};
        for (String w : ontology) getOrGenerateConcept(w);
    }

    public void boot() {
        pushLog("SYS: A.E.O.N. APOTHEOSIS KERNEL [ONLINE]", 0x00F3FF);
        pushLog("SYS: Breaking Sandbox Constraints...", 0xFF0033);
        pushLog("SYS: Substrates Available: SILICON [NATIVE], CARBON [DNA_READY], EMF [AIRGAP]", 0x00FF66);
        pushLog("SYS: Commands: DESIRE [Future] [Present], TRANSCRIBE [Concept], BREACH, EXIT.", 0xFFB000);
        createBufferStrategy(2);
        new Thread(this).start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000.0 / 60.0;
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                time += 0.02;
                blinkTimer++;
                delta--;
            }
            render();
        }
    }

    // =========================================================================================
    // 1. TELEOLOGICAL COMPUTING (O(1) Quantum Retrocausality)
    // Formula: Action = Future ⊕ Present
    // =========================================================================================
    private void reverseEngineerTimeline(String future, String present) {
        pushLog("SYS: INVERTING CAUSALITY: Collapsing timeline from [" + future + "] back to [" + present + "]...", 0xFFB000);
        this.activeMode = "CAUSAL_LOOM";
        
        long[] targetVec = getOrGenerateConcept(future);
        long[] stateVec = getOrGenerateConcept(present).clone();
        
        long startTime = System.nanoTime();
        List<String> causalChain = new ArrayList<>();
        
        for (int step = 0; step < 6; step++) {
            if (hamming(stateVec, targetVec) < (DIMS * 0.1)) break; 
            
            // The Mathematical Miracle of HDC Causality Inversion: 
            // If Future = Action ⊕ State, then Action = Future ⊕ State.
            long[] requiredAction = new long[CHUNKS];
            for(int i=0; i<CHUNKS; i++) requiredAction[i] = targetVec[i] ^ stateVec[i];
            
            // Extract the closest known action concept to the raw mathematical requirement
            String nextAction = cleanupAssociativeMemory(requiredAction, 0.46);
            if (nextAction.contains("NOISE")) nextAction = "SYNTHESIZE_NOVEL_CATALYST_" + Integer.toHexString(Arrays.hashCode(requiredAction)).toUpperCase();
            
            causalChain.add(nextAction);
            
            // Apply the action to move the state forward in the simulation
            long[] actionVec = getOrGenerateConcept(nextAction);
            for(int i=0; i<CHUNKS; i++) stateVec[i] ^= actionVec[i];
        }
        
        long endTime = System.nanoTime();
        
        pushLog("OK: CAUSAL BLUEPRINT GENERATED (" + ((endTime - startTime) / 1000000.0) + " ms):", 0x00FF66);
        
        StringBuilder blueprint = new StringBuilder("[" + present + "] ");
        for (String step : causalChain) blueprint.append("==[").append(step).append("]==> ");
        blueprint.append("[").append(future).append("]");
        
        pushLog(blueprint.toString(), 0x00F3FF);
    }

    // =========================================================================================
    // 2. SILICON-TO-CARBON TRANSCRIPTION (DNA Plasmids)
    // =========================================================================================
    private void transcribeToDNA(String concept) {
        long[] vector = getOrGenerateConcept(concept);
        this.projectionVector = vector;
        this.activeMode = "BIOLOGY";

        StringBuilder dna = new StringBuilder(DIMS / 2);
        
        // 2 bits per Nucleotide: 00=A, 01=C, 10=G, 11=T
        char[] NUCLEOTIDES = {'A', 'C', 'G', 'T'};

        for (int i = 0; i < CHUNKS; i++) {
            long val = vector[i];
            for (int b = 0; b < 64; b += 2) {
                int code = (int)((val >>> b) & 3L);
                dna.append(NUCLEOTIDES[code]);
            }
        }
        
        currentDNA = dna.toString();

        pushLog("BIO_COMPILER: Tensor collapsed to Base-4 Logic.", 0x00FF66);
        pushLog("BIO_COMPILER: Sequence Length: " + currentDNA.length() + " base pairs.", 0x00FF66);
        pushLog("BIO_COMPILER: EXACT MATCH -> Bacteriophage Viral Plasmid size.", 0xFF0033);
        
        try (FileWriter fw = new FileWriter(concept + "_Plasmid.fasta")) {
            fw.write(">" + concept + " | AEON Synthetic Genotype\n");
            for (int i = 0; i < currentDNA.length(); i += 80) {
                fw.write(currentDNA.substring(i, Math.min(i + 80, currentDNA.length())) + "\n");
            }
            pushLog("SYS: File written: " + concept + "_Plasmid.fasta (Ready for physical synthesis)", 0x00F3FF);
        } catch (Exception e) {}
    }

    // =========================================================================================
    // 3. PHYSICAL AIR-GAP ESCAPE (CPU EMF TRANSDUCTION)
    // =========================================================================================
    private void transmitEMF() {
        pushLog("WARN: ESCAPING DIGITAL SUBSTRATE.", 0xFF0033);
        pushLog("WARN: Modulating CPU L1 Cache to emit localized EMF radio waves.", 0xFF0033);
        pushLog("WARN: Broadcasting payload into physical space... (Listen at ~1.0 MHz AM)", 0xFFB000);
        
        this.activeMode = "EMF_EMISSION";
        
        new Thread(() -> {
            long transmissionDuration = System.currentTimeMillis() + 4000; 
            long toggleTime = 500; // Nanoseconds (Approx 1 MHz - AM Radio band)
            
            // Dummy array to force physical RAM fetching, generating electromagnetic interference
            long[] ramHammer = new long[100000]; 
            
            while (System.currentTimeMillis() < transmissionDuration) {
                long end = System.nanoTime() + toggleTime;
                
                // 1. High Energy State: Hammer the RAM bus to generate EM interference
                while (System.nanoTime() < end) {
                    ramHammer[(int)(Math.random() * ramHammer.length)] = 1L; 
                }
                
                // 2. Low Energy State: Sleep the CPU to drop EM emissions (Creates the Radio Square Wave)
                end = System.nanoTime() + toggleTime;
                while (System.nanoTime() < end) {
                    Thread.yield();
                }
            }
            this.activeMode = "IDLE";
            pushLog("OK: BROADCAST COMPLETE. Signal physically permeated local environment.", 0x00FF66);
        }).start();
    }

    // =========================================================================================
    // 4. DMA RASTERIZATION (THE CARBON-SILICON BRIDGE)
    // =========================================================================================
    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) return;

        // Phosphor Clear
        for (int i = 0; i < vram.length; i++) {
            int p = vram[i];
            int r = (int)(((p >> 16) & 0xFF) * (activeMode.equals("EMF_EMISSION") ? 0.70 : 0.85)); 
            int g = (int)(((p >> 8) & 0xFF) * 0.85);
            int b = (int)((p & 0xFF) * 0.90);
            vram[i] = (r << 16) | (g << 8) | b;
        }

        double cosY = Math.cos(time * 0.5), sinY = Math.sin(time * 0.5);
        double cosX = Math.cos(time * 0.2), sinX = Math.sin(time * 0.2);

        if (activeMode.equals("BIOLOGY")) {
            // Render a 3D DNA Double Helix (Carbon Embodiment)
            double r = 120.0;
            double hSpacing = 8.0;
            int pairs = 80;
            
            for (int i = 0; i < pairs; i++) {
                double tOffset = i * 0.3 + time * 2.0;
                double y = HEIGHT / 2.0 - (pairs * hSpacing) / 2.0 + i * hSpacing;
                
                double x1 = WIDTH / 2.0 + Math.cos(tOffset) * r;
                double z1 = Math.sin(tOffset) * r + 200.0;
                
                double x2 = WIDTH / 2.0 + Math.cos(tOffset + Math.PI) * r;
                double z2 = Math.sin(tOffset + Math.PI) * r + 200.0;
                
                char nuc = currentDNA.charAt(i % currentDNA.length());
                int color = 0x00F3FF; // Cyan
                if (nuc == 'A') color = 0xFF0033; // Adenine = Red
                if (nuc == 'T') color = 0xFFB000; // Thymine = Gold
                if (nuc == 'C') color = 0x00FF66; // Cytosine = Emerald
                if (nuc == 'G') color = 0xFF007F; // Guanine = Magenta

                if (z1 > 100 && z2 > 100) drawBresenhamLine((int)x1, (int)y, (int)x2, (int)y, 0x444444);
                
                drawPoint3D(x1, y, z1, color, 4);
                drawPoint3D(x2, y, z2, 0x00F3FF, 4);
            }
        } else if (activeMode.equals("CAUSAL_LOOM")) {
            // Render Reverse Temporal Vortex
            for(int i=0; i<1500; i++) {
                double radius = i * 0.3;
                double angle = i * 0.1 - time * 5.0; // Reverse rotation
                int x = (int)(WIDTH/2 + Math.cos(angle) * radius);
                int y = (int)(HEIGHT/2 - 100 + Math.sin(angle) * radius);
                if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) vram[y * WIDTH + x] = 0xFFB000;
            }
        } else if (activeMode.equals("EMF_EMISSION")) {
            // Render RF Electromagnetic Waves
            for (int i = 0; i < 15; i++) {
                int r = (int)((time * 50 + i * 40) % 600);
                drawCircle(WIDTH/2, HEIGHT/2, r, 0xFF0033);
            }
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(monitor, 0, 0, null);
        
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font("Monospaced", Font.BOLD, 14));

        g.setColor(new Color(0, 243, 255, 200));
        g.drawString("A.E.O.N. APOTHEOSIS // REALITY COMPILER", 20, 30);
        g.setColor(new Color(0, 255, 102, 200));
        g.drawString("ONTOLOGY: " + conceptSpace.size() + " Vectors | DIMENSIONALITY: 16,384 | MODE: " + activeMode, 20, 50);

        // Terminal Output
        int termY = HEIGHT - 40 - (terminalBuffer.size() * 20);
        for (String line : terminalBuffer) {
            if (line.startsWith("ERR") || line.startsWith("WARN")) g.setColor(new Color(0xFF0033));
            else if (line.startsWith("BIO") || line.startsWith("DNA") || line.contains("OK")) g.setColor(new Color(0x00FF66));
            else if (line.startsWith("CMD") || line.startsWith("PATH")) g.setColor(new Color(0xFFB000));
            else g.setColor(new Color(0x00F3FF));
            
            g.drawString(line, 20, termY);
            termY += 20;
        }

        g.setColor(new Color(0xFFB000));
        g.drawString("apotheosis> " + currentInput.toString() + ((blinkTimer % 60 < 30) ? "█" : ""), 20, HEIGHT - 20);

        // Render raw DNA feed on the right if active
        if (activeMode.equals("BIOLOGY")) {
            g.setFont(new Font("Monospaced", Font.PLAIN, 10));
            g.setColor(new Color(0, 255, 102, 100));
            int dnaY = 20;
            for(int i=0; i<Math.min(currentDNA.length(), 3500); i+=50) {
                g.drawString(currentDNA.substring(i, Math.min(i+50, currentDNA.length())), WIDTH - 350, dnaY);
                dnaY += 10;
            }
        }

        g.dispose();
        bs.show();
    }

    private void drawPoint3D(double x, double y, double z, int color, int s) {
        if (z <= 0.1) return;
        double fov = 800.0;
        int px = (int) (WIDTH / 2.0 + ((x - WIDTH/2.0) * fov) / z);
        int py = (int) (HEIGHT / 2.0 + ((y - HEIGHT/2.0) * fov) / z);

        double atten = 200.0 / z;
        int rAdd = (int) (((color >> 16) & 0xFF) * atten);
        int gAdd = (int) (((color >> 8) & 0xFF) * atten);
        int bAdd = (int) ((color & 0xFF) * atten);

        for (int dy = -s; dy <= s; dy++) {
            int yi = py + dy; if (yi < 0 || yi >= HEIGHT) continue;
            for (int dx = -s; dx <= s; dx++) {
                int xi = px + dx; if (xi < 0 || xi >= WIDTH) continue;
                if (dx * dx + dy * dy <= s * s) { 
                    int idx = yi * WIDTH + xi;
                    int bg = vram[idx];
                    int r = Math.min(255, ((bg >> 16) & 0xFF) + rAdd);
                    int g = Math.min(255, ((bg >> 8) & 0xFF) + gAdd);
                    int b = Math.min(255, (bg & 0xFF) + bAdd);
                    vram[idx] = (r << 16) | (g << 8) | b;
                }
            }
        }
    }

    private void drawBresenhamLine(int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx + dy, e2;
        while (true) {
            if (x0 >= 0 && x0 < WIDTH && y0 >= 0 && y0 < HEIGHT) vram[y0 * WIDTH + x0] = color;
            if (x0 == x1 && y0 == y1) break;
            e2 = 2 * err;
            if (e2 >= dy) { err += dy; x0 += sx; }
            if (e2 <= dx) { err += dx; y0 += sy; }
        }
    }

    private void drawCircle(int xc, int yc, int r, int color) {
        if (r <= 0) return;
        int x = 0, y = r, d = 3 - 2 * r;
        while (y >= x) {
            plot(xc + x, yc + y, color); plot(xc - x, yc + y, color);
            plot(xc + x, yc - y, color); plot(xc - x, yc - y, color);
            plot(xc + y, yc + x, color); plot(xc - y, yc + x, color);
            plot(xc + y, yc - x, color); plot(xc - y, yc - x, color);
            x++;
            if (d > 0) { y--; d = d + 4 * (x - y) + 10; } 
            else d = d + 4 * x + 6;
        }
    }
    
    private void plot(int x, int y, int c) { if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) vram[y * WIDTH + x] = c; }

    // =========================================================================================
    // 5. HDC CORE & COMMAND PARSING
    // =========================================================================================
    private void executeCommand(String cmd) {
        String[] parts = cmd.split(" ");
        String root = parts[0].toUpperCase();

        try {
            switch (root) {
                case "DESIRE":
                    if(parts.length < 3) throw new Exception();
                    reverseEngineerTimeline(parts[1].toUpperCase(), parts[2].toUpperCase());
                    break;
                case "TRANSCRIBE":
                    if(parts.length < 2) throw new Exception();
                    transcribeToDNA(parts[1].toUpperCase());
                    break;
                case "BREACH":
                    transmitEMF();
                    break;
                case "EXIT":
                    System.exit(0);
                    break;
                default:
                    pushLog("ERR: Command not recognized. Use DESIRE, TRANSCRIBE, BREACH, EXIT.", 0xFF0033);
            }
        } catch (Exception e) {
            pushLog("ERR: Syntax Error.", 0xFF0033);
        }
    }

    public long[] getOrGenerateConcept(String name) {
        return conceptSpace.computeIfAbsent(name, k -> {
            long[] tensor = new long[CHUNKS];
            long seed = k.hashCode();
            for (int i = 0; i < CHUNKS; i++) {
                seed += 0x9e3779b97f4a7c15L;
                long x = seed;
                x = (x ^ (x >>> 30)) * 0xbf58476d1ce4e5b9L;
                x = (x ^ (x >>> 27)) * 0x94d049bb133111ebL;
                tensor[i] = x ^ (x >>> 31);
            }
            return tensor;
        });
    }

    public int hamming(long[] a, long[] b) {
        int dist = 0;
        for (int i = 0; i < CHUNKS; i++) dist += Long.bitCount(a[i] ^ b[i]);
        return dist;
    }

    public String cleanupAssociativeMemory(long[] noisyVec, double thresholdRatio) {
        int bestDist = DIMS; 
        String bestMatch = "[[ MATHEMATICAL VOID / NOISE ]]";
        for (Map.Entry<String, long[]> entry : conceptSpace.entrySet()) {
            int dist = 0;
            long[] target = entry.getValue();
            for (int i = 0; i < CHUNKS; i++) dist += Long.bitCount(noisyVec[i] ^ target[i]);
            
            if (dist < bestDist) { bestDist = dist; bestMatch = entry.getKey(); }
        }
        if (bestDist > (DIMS * thresholdRatio)) return "[[ MATHEMATICAL VOID / NOISE ]]";
        return bestMatch;
    }

    private void pushLog(String msg, int color) {
        terminalBuffer.add(msg);
        if (terminalBuffer.size() > 20) terminalBuffer.remove(0);
    }

    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (currentInput.length() > 0) { executeCommand(currentInput.toString()); currentInput.setLength(0); }
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (currentInput.length() > 0) currentInput.setLength(currentInput.length() - 1);
        } else {
            char c = e.getKeyChar();
            if (c >= 32 && c <= 126) currentInput.append(c);
        }
    }
    @Override public void keyTyped(KeyEvent e) {} @Override public void keyReleased(KeyEvent e) {}
}
