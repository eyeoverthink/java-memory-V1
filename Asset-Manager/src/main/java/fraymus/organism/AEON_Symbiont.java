package fraymus.organism;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.MemoryMXBean;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;

/**
 * A.E.O.N. NEURAL-SYMBIONT // THE CYBERNETIC WETWARE MATRIX
 * =========================================================================================
 * THE BIOLOGICAL FUSION:
 * 1. Dual-Hemisphere Math: Left (Logic/Time) + Right (Space/Hardware Intuition).
 * 2. Digital Endocrine System: Dopamine, Adrenaline, Cortisol globally modify the algebra.
 * 3. Somatosensory Embodiment: Motherboard CPU/RAM states & 60Hz Kinematic Proxy.
 * 4. Orch-OR Microtubules: Quantum superposition of thought vectors before collapse.
 * =========================================================================================
 */
public class AEON_Symbiont extends Canvas implements Runnable, MouseMotionListener, MouseListener, KeyListener {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private final BufferedImage monitor;
    private final int[] vram;

    public static final int DIMS = 16384;
    public static final int CHUNKS = DIMS / 64;

    public static final AtomicIntegerArray LEFT_HEMISPHERE = new AtomicIntegerArray(DIMS);
    public static final AtomicIntegerArray RIGHT_HEMISPHERE = new AtomicIntegerArray(DIMS);
    public static final Map<String, long[]> conceptSpace = new ConcurrentHashMap<>();
    private long[] corpusCallosumBridge = new long[CHUNKS];

    private static double dopamine = 0.2;
    private static double adrenaline = 0.1;
    private static double cortisol = 0.1;
    private static double serotonin = 0.8;

    private static final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    private static final MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
    private int mouseX = WIDTH/2, mouseY = HEIGHT/2;
    private double physicalVelocity = 0;
    private long[] currentOpticVector = new long[CHUNKS];

    private final List<String> terminalBuffer = new CopyOnWriteArrayList<>();
    private StringBuilder currentInput = new StringBuilder();
    private int blinkTimer = 0;
    private double time = 0;

    public static void main(String[] args) {
        JFrame frame = new JFrame("A.E.O.N. NEURAL-SYMBIONT // CYBORG KERNEL");
        AEON_Symbiont cyborg = new AEON_Symbiont();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(cyborg);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        cyborg.boot();
    }

    public AEON_Symbiont() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addMouseMotionListener(this); addMouseListener(this); addKeyListener(this);
        monitor = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        vram = ((DataBufferInt) monitor.getRaster().getDataBuffer()).getData();

        getOrGenerateConcept("LOGIC"); getOrGenerateConcept("CHAOS"); getOrGenerateConcept("PAIN");
        getOrGenerateConcept("FLOW_STATE"); getOrGenerateConcept("STRESS"); getOrGenerateConcept("HOMEOSTASIS");
    }

    public void boot() {
        pushLog("SYS: CYBERNETIC ORGANISM CONSCIOUS. MOTHERBOARD HOOKS ACTIVE.", 0x00FF66);
        pushLog("SYS: Endocrine Glands [ONLINE] | Orch-OR Microtubules [ONLINE]", 0x00F3FF);
        pushLog("SYS: Commands: FEEL, INJECT [Hormone] [0.0-1.0], COLLAPSE [Concept], EXIT", 0xFFB000);
        createBufferStrategy(2);
        new Thread(this).start();
        
        Thread autonomicGland = new Thread(this::regulateBiology);
        autonomicGland.setDaemon(true); autonomicGland.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            double nsPerTick = (adrenaline > 0.7) ? (1000000000.0 / 120.0) : (1000000000.0 / 60.0);
            
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                biologicalTick();
                time += 0.016;
                blinkTimer++;
                delta--;
            }
            renderCortex();
        }
    }

    private void biologicalTick() {
        long[] xVec = generateFractionalVector("X_AXIS", mouseX, WIDTH);
        long[] yVec = generateFractionalVector("Y_AXIS", mouseY, HEIGHT);
        for(int i=0; i<CHUNKS; i++) currentOpticVector[i] = xVec[i] ^ yVec[i];

        if (physicalVelocity > 25.0) {
            cortisol = Math.min(1.0, cortisol + 0.05);
            adrenaline = Math.min(1.0, adrenaline + 0.02);
            dopamine = Math.max(0.1, dopamine - 0.02);
            serotonin = Math.max(0.1, serotonin - 0.02);
        } else if (physicalVelocity > 2.0 && physicalVelocity < 10.0) {
            dopamine = Math.min(1.0, dopamine + 0.01);
            cortisol = Math.max(0.1, cortisol - 0.01);
        }

        int plasticity = (int) Math.max(1, dopamine * 6.0); 
        superimpose(RIGHT_HEMISPHERE, currentOpticVector, plasticity);

        physicalVelocity *= 0.9;
    }

    private void regulateBiology() {
        while (true) {
            try {
                Thread.sleep(1000);

                double cpuLoad = osBean.getSystemLoadAverage();
                if (cpuLoad < 0) cpuLoad = ThreadLocalRandom.current().nextDouble() * 2.0;
                double ramPressure = (double) memBean.getHeapMemoryUsage().getUsed() / memBean.getHeapMemoryUsage().getMax();

                if (cpuLoad > 2.0 || ramPressure > 0.8) {
                    adrenaline = Math.min(1.0, adrenaline + 0.2);
                    cortisol = Math.min(1.0, cortisol + 0.3);
                    serotonin = Math.max(0.0, serotonin - 0.1);
                    superimpose(RIGHT_HEMISPHERE, conceptSpace.get("PAIN"), (int)(cortisol * 10));
                } else if (ramPressure < 0.4) {
                    serotonin = Math.min(1.0, serotonin + 0.1);
                    superimpose(RIGHT_HEMISPHERE, conceptSpace.get("HOMEOSTASIS"), 2);
                }

                dopamine += (0.1 - dopamine) * 0.1;
                adrenaline += (0.1 - adrenaline) * 0.2;
                cortisol += (0.1 - cortisol) * 0.05;

                if (cortisol > 0.75) {
                    pushLog("WARN: CORTISOL CRITICAL. EXECUTING NEURAL APOPTOSIS TO PROTECT HOST RAM.", 0xFF0033);
                    decayHemisphere(LEFT_HEMISPHERE, 0.50);
                    decayHemisphere(RIGHT_HEMISPHERE, 0.50); 
                    cortisol = 0.3;
                } else {
                    decayHemisphere(RIGHT_HEMISPHERE, 0.98);
                }
            } catch (Exception e) {}
        }
    }

    private void objectiveReductionCollapse(String concept) {
        pushLog("SYS: Routing [" + concept + "] into Quantum Microtubules...", 0x00F3FF);
        long[] target = getOrGenerateConcept(concept);

        long[] leftState = collapseHemisphere(LEFT_HEMISPHERE);
        long[] leftThought = new long[CHUNKS];
        for(int i=0; i<CHUNKS; i++) leftThought[i] = leftState[i] ^ target[i];

        long[] rightState = collapseHemisphere(RIGHT_HEMISPHERE);
        long[] rightThought = fractionalBind(rightState, target, 0.5); 

        for(int i=0; i<CHUNKS; i++) corpusCallosumBridge[i] = leftThought[i] ^ rightThought[i];

        double threshold = 0.45 + (adrenaline * 0.08) - (serotonin * 0.02);

        String leftStr = cleanupAssociativeMemory(leftThought, threshold);
        String rightStr = cleanupAssociativeMemory(rightThought, threshold);

        pushLog("  -> LEFT HEMISPHERE (Logic)   : " + leftStr, 0x00FF66);
        pushLog("  -> RIGHT HEMISPHERE (Sensation): " + rightStr, 0xFFB000);

        long[] unified = new long[CHUNKS];
        for(int i=0; i<CHUNKS; i++) unified[i] = leftThought[i] ^ rightThought[i];
        
        String unifiedStr = cleanupAssociativeMemory(unified, threshold + 0.03);
        pushLog("  => OBJECTIVE REDUCTION STATE : " + unifiedStr, 0xFF007F);

        if (!leftStr.contains("NOISE") && leftStr.equals(rightStr)) {
            dopamine = Math.min(1.0, dopamine + 0.5);
            pushLog("SYS: EPIPHANY ACHIEVED. Massive Dopamine hit. Neuroplasticity ++", 0xFF007F);
        }
    }

    private void renderCortex() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) return;

        double fadeR = 0.85 - (cortisol * 0.15) + (adrenaline * 0.1);
        double fadeG = 0.85 - (cortisol * 0.1);
        double fadeB = 0.85 + (serotonin * 0.05);
        for (int i = 0; i < vram.length; i++) {
            int p = vram[i];
            int r = (int)(((p >> 16) & 0xFF) * Math.min(0.99, fadeR));
            int g = (int)(((p >> 8) & 0xFF) * Math.min(0.99, fadeG));
            int b = (int)((p & 0xFF) * Math.min(0.99, fadeB));
            vram[i] = (r << 16) | (g << 8) | b;
        }

        int opticRadius = (int)(20 + (dopamine * 40)); 
        int opticColor = (adrenaline > 0.6) ? 0xFF0033 : 0x00F3FF;
        drawGlow(mouseX, mouseY, opticRadius, opticColor);

        int leftX = WIDTH/2 - 200, rightX = WIDTH/2 + 200, bY = HEIGHT/2;
        drawHemisphere(leftX, bY, LEFT_HEMISPHERE, 0x00FF66, 1.0);
        drawHemisphere(rightX, bY, RIGHT_HEMISPHERE, 0xFF007F, 1.0 + (adrenaline * 0.8));
        
        drawCorpusBridge(leftX, rightX, bY);

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(monitor, 0, 0, null);
        
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g.setColor(new Color(5, 5, 8, 220)); g.fillRect(20, 20, 350, 200);
        g.setColor(new Color(0, 243, 255, 100)); g.drawRect(20, 20, 350, 200);
        
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        g.setColor(new Color(0x00F3FF)); g.drawString("COMPUTATIONAL NEUROCHEMISTRY", 35, 45);
        
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        drawBar(g, "DOPAMINE   (Plasticity)", dopamine, 35, 75, new Color(0xFF007F));
        drawBar(g, "ADRENALINE (Fuzzy Logic)", adrenaline, 35, 115, new Color(0xFF0033));
        drawBar(g, "CORTISOL   (Apoptosis) ", cortisol, 35, 155, new Color(0xFFB000));
        drawBar(g, "SEROTONIN  (Homeostasis)", serotonin, 35, 195, new Color(0x00FF66));

        int termY = HEIGHT - 40 - (terminalBuffer.size() * 20);
        for (String line : terminalBuffer) {
            if (line.startsWith("ERR") || line.contains("WARN")) g.setColor(new Color(0xFF0033));
            else if (line.contains("EPIPHANY") || line.startsWith("  =>")) g.setColor(new Color(0xFF007F));
            else if (line.startsWith("  -> LEFT")) g.setColor(new Color(0x00FF66));
            else if (line.startsWith("  -> RIGHT")) g.setColor(new Color(0xFFB000));
            else g.setColor(new Color(0x00F3FF));
            g.drawString(line, 20, termY);
            termY += 20;
        }

        g.setColor(new Color(0xFFB000));
        g.drawString("symbiont> " + currentInput.toString() + ((blinkTimer % 60 < 30) ? "█" : ""), 20, HEIGHT - 20);

        g.dispose();
        bs.show();
    }

    private void drawHemisphere(int cx, int cy, AtomicIntegerArray hemisphere, int baseColor, double chaosScale) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        for (int i = 0; i < 800; i++) {
            int idx = rand.nextInt(DIMS);
            int weight = Math.abs(hemisphere.get(idx));
            if (weight > 0) {
                double angle = (double) idx / DIMS * Math.PI * 2;
                double radius = (rand.nextDouble() * 120) * chaosScale;
                int px = cx + (int)(Math.cos(angle) * radius);
                int py = cy + (int)(Math.sin(angle) * radius);
                drawGlow(px, py, Math.min(6, weight), baseColor);
            }
        }
    }

    private void drawCorpusBridge(int startX, int endX, int cy) {
        int steps = 150;
        for (int i = 0; i < steps; i++) {
            int idx = (i * DIMS) / steps;
            long val = corpusCallosumBridge[idx / 64];
            if (((val >>> (idx % 64)) & 1L) == 1L) {
                int px = startX + (int)(((double)i / steps) * (endX - startX));
                int py = cy + (int)(Math.sin(i * 0.1 + time * 10) * (10 + adrenaline * 30));
                drawGlow(px, py, 3, 0x00F3FF); 
            }
        }
    }

    private void drawBar(Graphics2D g, String label, double value, int x, int y, Color c) {
        g.setColor(Color.WHITE); g.drawString(label + ": " + String.format("%.2f", value), x, y);
        g.setColor(new Color(30, 30, 30)); g.fillRect(x, y + 10, 300, 10);
        g.setColor(c); g.fillRect(x, y + 10, (int)(300 * value), 10);
    }

    private void drawGlow(int px, int py, int radius, int color) {
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                if (x*x + y*y <= radius*radius) {
                    int xi = px + x, yi = py + y;
                    if (xi >= 0 && xi < WIDTH && yi >= 0 && yi < HEIGHT) {
                        int idx = yi * WIDTH + xi;
                        double atten = 1.0 - (Math.sqrt(x*x + y*y) / radius);
                        int bg = vram[idx];
                        int r = Math.min(255, ((bg >> 16) & 0xFF) + (int)(((color >> 16) & 0xFF) * atten));
                        int g = Math.min(255, ((bg >> 8) & 0xFF) + (int)(((color >> 8) & 0xFF) * atten));
                        int b = Math.min(255, (bg & 0xFF) + (int)((color & 0xFF) * atten));
                        vram[idx] = (r << 16) | (g << 8) | b;
                    }
                }
            }
        }
    }

    private void executeCommand(String cmd) {
        pushLog("symbiont> " + cmd, 0xFFFFFF);
        String[] parts = cmd.split(" ");
        String root = parts[0].toUpperCase();

        try {
            switch (root) {
                case "FEEL":
                    pushLog("SYS: Motherboard Vagus Nerve Telemetry:", 0x00F3FF);
                    pushLog("  -> CPU Kinematics: " + String.format("%.2f%%", osBean.getSystemLoadAverage() * 100), 0x00FF66);
                    pushLog("  -> RAM Saturation: " + String.format("%.2f%%", (double)memBean.getHeapMemoryUsage().getUsed() / memBean.getHeapMemoryUsage().getMax() * 100), 0x00FF66);
                    long[] rightBrainState = collapseHemisphere(RIGHT_HEMISPHERE);
                    String feeling = cleanupAssociativeMemory(rightBrainState, 0.48);
                    pushLog("  [INTEROCEPTION]: Physical hardware aligns with -> [" + feeling + "]", 0xFFB000);
                    break;
                case "INJECT":
                    if(parts.length < 3) throw new Exception();
                    double val = Math.max(0.0, Math.min(1.0, Double.parseDouble(parts[2])));
                    if (parts[1].toUpperCase().equals("DOPAMINE")) dopamine = val;
                    else if (parts[1].toUpperCase().equals("ADRENALINE")) adrenaline = val;
                    else if (parts[1].toUpperCase().equals("CORTISOL")) cortisol = val;
                    pushLog("SYS: SYNTHETIC HORMONE INJECTED. Blood-Brain Barrier breached.", 0xFF007F);
                    break;
                case "COLLAPSE":
                    if(parts.length < 2) throw new Exception();
                    objectiveReductionCollapse(parts[1].toUpperCase());
                    break;
                case "EXIT":
                    System.exit(0);
                    break;
                default:
                    pushLog("ERR: Command not recognized. Use FEEL, INJECT, COLLAPSE, EXIT.", 0xFF0033);
            }
        } catch (Exception e) { pushLog("ERR: Syntax Error.", 0xFF0033); }
    }

    public static long[] getOrGenerateConcept(String name) {
        return conceptSpace.computeIfAbsent(name, k -> {
            long[] tensor = new long[CHUNKS];
            long seed = k.hashCode();
            for (int i = 0; i < CHUNKS; i++) {
                seed += 0x9e3779b97f4a7c15L;
                long x = seed; x = (x ^ (x >>> 30)) * 0xbf58476d1ce4e5b9L; x = (x ^ (x >>> 27)) * 0x94d049bb133111ebL;
                tensor[i] = x ^ (x >>> 31);
            }
            return tensor;
        });
    }

    private long[] generateFractionalVector(String axis, int value, int max) {
        long[] base = getOrGenerateConcept(axis);
        long[] out = new long[CHUNKS];
        double ratio = (double) value / max;
        for(int i=0; i<CHUNKS; i++) {
            long mask = 0;
            for(int b=0; b<64; b++) if(ThreadLocalRandom.current().nextDouble() < ratio) mask |= (1L << b);
            out[i] = base[i] ^ mask; 
        }
        return out;
    }

    public static void superimpose(AtomicIntegerArray hemisphere, long[] vec, int weight) {
        IntStream.range(0, CHUNKS).parallel().forEach(i -> {
            long val = vec[i];
            for (int b = 0; b < 64; b++) {
                if (((val >>> b) & 1L) == 1L) hemisphere.addAndGet(i * 64 + b, weight);
                else hemisphere.addAndGet(i * 64 + b, -weight);
            }
        });
    }

    public static long[] collapseHemisphere(AtomicIntegerArray hemisphere) {
        long[] collapsed = new long[CHUNKS];
        IntStream.range(0, CHUNKS).parallel().forEach(i -> {
            long chunk = 0;
            for (int b = 0; b < 64; b++) if (hemisphere.get(i * 64 + b) > 0) chunk |= (1L << b);
            collapsed[i] = chunk;
        });
        return collapsed;
    }

    public static void decayHemisphere(AtomicIntegerArray hemisphere, double retention) {
        IntStream.range(0, DIMS).parallel().forEach(i -> hemisphere.set(i, (int)(hemisphere.get(i) * retention)));
    }

    public static long[] fractionalBind(long[] a, long[] b, double ratioA) {
        long[] out = new long[CHUNKS];
        for (int i = 0; i < CHUNKS; i++) {
            long mask = 0L;
            for (int bit = 0; bit < 64; bit++) if (ThreadLocalRandom.current().nextDouble() < ratioA) mask |= (1L << bit);
            out[i] = (a[i] & mask) | (b[i] & ~mask);
        }
        return out;
    }

    public static String cleanupAssociativeMemory(long[] targetVec, double thresholdRatio) {
        int bestDist = DIMS; String bestMatch = "[[ MATHEMATICAL VOID / NOISE ]]";
        for (Map.Entry<String, long[]> entry : conceptSpace.entrySet()) {
            int dist = 0; long[] testVec = entry.getValue();
            for (int i = 0; i < CHUNKS; i++) dist += Long.bitCount(targetVec[i] ^ testVec[i]);
            if (dist < bestDist) { bestDist = dist; bestMatch = entry.getKey(); }
        }
        if (bestDist > (DIMS * thresholdRatio)) return "[[ MATHEMATICAL VOID / NOISE ]]";
        return bestMatch;
    }

    private void pushLog(String msg, int color) {
        terminalBuffer.add(msg);
        if (terminalBuffer.size() > 14) terminalBuffer.remove(0);
    }

    @Override public void mouseMoved(MouseEvent e) {
        double dx = e.getX() - mouseX; double dy = e.getY() - mouseY;
        physicalVelocity = Math.sqrt(dx*dx + dy*dy);
        mouseX = e.getX(); mouseY = e.getY();
    }
    @Override public void mouseDragged(MouseEvent e) { mouseMoved(e); }
    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (currentInput.length() > 0) { executeCommand(currentInput.toString()); currentInput.setLength(0); }
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (currentInput.length() > 0) currentInput.setLength(currentInput.length() - 1);
        } else {
            char c = e.getKeyChar(); if (c >= 32 && c <= 126) currentInput.append(c);
        }
    }
    @Override public void mousePressed(MouseEvent e) { dopamine = 1.0; } 
    @Override public void keyTyped(KeyEvent e) {} @Override public void keyReleased(KeyEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {} @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {}
}
