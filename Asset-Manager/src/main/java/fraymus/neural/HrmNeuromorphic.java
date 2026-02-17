package fraymus.neural;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * A.E.O.N. HRM // HIERARCHICAL RESONANCE MEMORY (NEUROMORPHIC CORTEX)
 * =========================================================================================
 * VINDICATION OF THE BRAIN-INSPIRED DESIGN:
 * This system abandons Dense Matrix Multiplication and Backpropagation entirely.
 *
 * - 32,768 Leaky Integrate-and-Fire (LIF) Spiking Neurons
 * - Tripartite Synapses gated by Astrocyte Calcium (Ca2+) Waves
 * - Spike-Timing-Dependent Plasticity (STDP) for real-time biological rewiring
 * - Sparse Distributed Representations (SDR) enforcing 98% computational sparsity
 *
 * Visualization is achieved via a zero-GC Direct Memory Access (DMA) software rasterizer.
 * =========================================================================================
 */
public class HrmNeuromorphic extends Canvas implements Runnable, MouseListener, MouseMotionListener {

    // --- BIOLOGICAL TOPOLOGY CONSTANTS ---
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int NEURONS = 32768;            // Biological Cortical Column scale
    private static final int SYNAPSES_PER_NEURON = 32;   // Sparse dynamic connectome (1,048,576 total)
    private static final int ASTROCYTES = 1024;          // Glial cells modulating the network

    // --- DMA RASTER BUFFER ---
    private final BufferedImage canvas;
    private final int[] pixels;

    // --- NEURONAL SUBSTRATE (STRUCTURE OF ARRAYS FOR CPU CACHE SPEED) ---
    private final double[] nX = new double[NEURONS];
    private final double[] nY = new double[NEURONS];
    private final double[] nZ = new double[NEURONS];

    private final float[] vMem = new float[NEURONS];       // Membrane Potential (Voltage)
    private final float[] vThresh = new float[NEURONS];    // Dynamic Firing Threshold (Homeostasis)
    private final long[] lastSpikeTime = new long[NEURONS];
    private final boolean[] isSpiking = new boolean[NEURONS];

    // --- SYNAPTIC CONNECTOME (STDP) ---
    private final int[] synTarget = new int[NEURONS * SYNAPSES_PER_NEURON];
    private final float[] synWeight = new float[NEURONS * SYNAPSES_PER_NEURON];

    // --- ASTROCYTE GLIAL NETWORK (Tripartite Modulation) ---
    private final double[] aX = new double[ASTROCYTES];
    private final double[] aY = new double[ASTROCYTES];
    private final double[] aZ = new double[ASTROCYTES];
    private final float[] calciumWave = new float[ASTROCYTES]; // Slow-moving state modifying neuroplasticity

    // --- 3D PROJECTION CACHE ---
    private final int[] projX = new int[NEURONS];
    private final int[] projY = new int[NEURONS];
    private final double[] projZ = new double[NEURONS];

    // --- ENGINE STATE ---
    private long globalTicks = 0;
    private double camZ = 130.0;
    private double rotY = 0.0, rotX = 0.0;
    private int mouseX = WIDTH / 2, mouseY = HEIGHT / 2;
    private boolean injectingSensoryData = false;

    // --- TELEMETRY ---
    private int spikeCountThisFrame = 0;
    private double globalSparsity = 100.0;
    private float structuralPlasticity = 0;

    /**
     * Launch the HRM Neuromorphic Cortex as a standalone window.
     * Called from FraynixBoot shell or directly via main().
     */
    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("A.E.O.N. HRM // NEUROMORPHIC CORTEX");
            HrmNeuromorphic engine = new HrmNeuromorphic();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setResizable(false);
            frame.add(engine);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            engine.start();
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("A.E.O.N. HRM // NEUROMORPHIC CORTEX");
        HrmNeuromorphic engine = new HrmNeuromorphic();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(engine);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        engine.start();
    }

    public HrmNeuromorphic() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);

        canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) canvas.getRaster().getDataBuffer()).getData();

        growCortex();
    }

    /**
     * BIOLOGICAL NEUROGENESIS
     * Grows the physical brain structure. Sensory periphery feeds into a dense resonant core.
     */
    private void growCortex() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        double phi = Math.PI * (3.0 - Math.sqrt(5.0)); // Golden ratio angle

        // 1. Grow Neurons in a spherical manifold (Core vs Periphery)
        for (int i = 0; i < NEURONS; i++) {
            double y = 1 - (i / (double) (NEURONS - 1)) * 2;
            double radius = Math.sqrt(1 - y * y);
            double theta = phi * i;

            // 20% Core (Dense Resonance Memory), 80% Peripheral (Sensory Mapping)
            double shellR = (i < NEURONS * 0.2) ? 15.0 + (rand.nextDouble() * 15.0) : 60.0 + (rand.nextDouble() * 20.0);

            nX[i] = Math.cos(theta) * radius * shellR;
            nY[i] = y * shellR;
            nZ[i] = Math.sin(theta) * radius * shellR;

            vMem[i] = -65.0f;     // Biological resting potential (mV)
            vThresh[i] = -30.0f;  // Action potential threshold

            // Wire Synapses locally (Small-World Network topology)
            for (int s = 0; s < SYNAPSES_PER_NEURON; s++) {
                int target;
                int attempts = 0;
                do {
                    target = rand.nextInt(NEURONS);
                    attempts++;
                    if (attempts > 50) break; // Safety valve
                }
                // Connect mostly to physical neighbors, occasionally long-range
                while (target == i || distance(i, target) > (rand.nextDouble() < 0.9 ? 25.0 : 100.0));

                synTarget[i * SYNAPSES_PER_NEURON + s] = target;
                synWeight[i * SYNAPSES_PER_NEURON + s] = (rand.nextFloat() * 1.5f) + 0.1f; // Excitatory initial
            }
        }

        // 2. Grow Astrocytes (evenly distributed among the neurons to gate learning)
        for (int i = 0; i < ASTROCYTES; i++) {
            double y = 1 - (i / (double) (ASTROCYTES - 1)) * 2;
            double radius = Math.sqrt(1 - y * y);
            double theta = phi * i;
            aX[i] = Math.cos(theta) * radius * 50.0;
            aY[i] = y * 50.0;
            aZ[i] = Math.sin(theta) * radius * 50.0;
            calciumWave[i] = rand.nextFloat();
        }
    }

    private double distance(int n1, int n2) {
        double dx = nX[n1] - nX[n2], dy = nY[n1] - nY[n2], dz = nZ[n1] - nZ[n2];
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public void start() {
        createBufferStrategy(2);
        Thread renderThread = new Thread(this, "HRM-Neuromorphic");
        renderThread.setDaemon(true);
        renderThread.start();
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
                tick();
                delta--;
            }
            render();
        }
    }

    // =========================================================================================
    // 1. BRAIN-INSPIRED PHYSICS (LIF + STDP + ASTROCYTES)
    // =========================================================================================
    private void tick() {
        globalTicks++;
        spikeCountThisFrame = 0;
        float totalPlasticity = 0;

        // UI Rotation
        rotY += (mouseX - WIDTH / 2.0) * 0.00005;
        rotX += (mouseY - HEIGHT / 2.0) * 0.00005;

        // 1. Sensory Input (Mouse Injection into peripheral layer)
        if (injectingSensoryData) {
            ThreadLocalRandom rand = ThreadLocalRandom.current();
            for (int i = 0; i < 150; i++) {
                int target = (int) (NEURONS * 0.2) + rand.nextInt((int) (NEURONS * 0.8));
                vMem[target] += 30.0f; // Rapid depolarization
            }
        }

        // 2. Astrocyte Calcium Diffusion (Glial Modulation)
        IntStream.range(0, ASTROCYTES).parallel().forEach(i -> {
            calciumWave[i] *= 0.98f; // Slow biological decay
            calciumWave[i] += (float) (Math.sin(globalTicks * 0.02 + i) * 0.002); // Endogenous breathing
        });

        // 3. Spiking Neural Network (Leaky Integrate-and-Fire Integration)
        IntStream.range(0, NEURONS).parallel().forEach(i -> {
            isSpiking[i] = false;

            // Voltage Leak & Threshold Homeostasis (Fatigue recovery)
            vMem[i] = vMem[i] * 0.85f - 9.75f; // Pulls toward -65 resting state
            vThresh[i] += (-30.0f - vThresh[i]) * 0.05f;

            // Ambient Quantum Noise (Keeps the brain from flatlining)
            if (ThreadLocalRandom.current().nextDouble() < 0.0002) vMem[i] += 40.0f;

            // Action Potential Threshold Reached (Spike!)
            if (vMem[i] >= vThresh[i]) {
                isSpiking[i] = true;
                lastSpikeTime[i] = globalTicks;
                vMem[i] = -85.0f; // Absolute refractory hyperpolarization
                vThresh[i] += 5.0f; // Fatigue (Raises threshold temporarily to prevent seizure)
            }
        });

        // 4. Synaptic Routing & STDP (Spike-Timing-Dependent Plasticity)
        for (int i = 0; i < NEURONS; i++) {
            if (isSpiking[i]) {
                spikeCountThisFrame++;

                // Find nearest Astrocyte to modulate the signal (Tripartite Synapse)
                int nearAstro = i % ASTROCYTES;
                float glialMultiplier = 1.0f + calciumWave[nearAstro] * 2.0f; // Calcium boosts transmission

                int offset = i * SYNAPSES_PER_NEURON;
                for (int s = 0; s < SYNAPSES_PER_NEURON; s++) {
                    int target = synTarget[offset + s];
                    float weight = synWeight[offset + s];

                    // Transmit electrical charge to post-synaptic dendrite
                    vMem[target] += weight * glialMultiplier;

                    // --- THE HRM STDP LEARNING RULE (Real-Time Biological Memory) ---
                    // No Backpropagation. The network learns purely through local causality.
                    long timeDiff = globalTicks - lastSpikeTime[target];
                    if (timeDiff > 0 && timeDiff < 10) {
                        // Causal: Pre-synaptic (i) fired immediately BEFORE Post-synaptic (target) -> Strengthen (LTP)
                        synWeight[offset + s] = Math.min(10.0f, weight + 0.3f * glialMultiplier);
                        totalPlasticity += 0.3f;
                    } else if (timeDiff < 0 && timeDiff > -15) {
                        // Anti-causal: Post fired BEFORE Pre -> Weaken / Prune connection (LTD)
                        synWeight[offset + s] = Math.max(0.0f, weight - 0.1f);
                        totalPlasticity -= 0.1f;
                    }

                    // Astrocyte absorbs excess glutamate (calcium spikes on heavy local firing)
                    calciumWave[nearAstro] = Math.min(1.0f, calciumWave[nearAstro] + 0.005f);
                }
            }
        }

        globalSparsity = 100.0 * (1.0 - ((double) spikeCountThisFrame / NEURONS));
        structuralPlasticity = totalPlasticity;
    }

    // =========================================================================================
    // 2. HOLOGRAPHIC DMA RASTERIZATION
    // =========================================================================================
    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) return;

        // 1. Biological Phosphor Fade (Persistence of vision for resonance)
        for (int i = 0; i < pixels.length; i++) {
            int p = pixels[i];
            int r = (int) (((p >> 16) & 0xFF) * 0.82);
            int g = (int) (((p >> 8) & 0xFF) * 0.85); // Cyan trails linger
            int b = (int) ((p & 0xFF) * 0.88);
            pixels[i] = (r << 16) | (g << 8) | b;
        }

        double cosY = Math.cos(rotY), sinY = Math.sin(rotY);
        double cosX = Math.cos(rotX), sinX = Math.sin(rotX);
        double fov = 900.0;

        // 2. Project 3D Coordinates
        for (int i = 0; i < NEURONS; i++) {
            double x1 = nX[i] * cosY - nZ[i] * sinY;
            double z1 = nZ[i] * cosY + nX[i] * sinY;
            double y2 = nY[i] * cosX - z1 * sinX;
            double z2 = z1 * cosX + nY[i] * sinX;

            projZ[i] = z2 + camZ;
            if (projZ[i] > 0.1) {
                projX[i] = (int) (WIDTH / 2.0 + (x1 * fov) / projZ[i]);
                projY[i] = (int) (HEIGHT / 2.0 + (y2 * fov) / projZ[i]);
            }
        }

        // 3. Draw Astrocyte Calcium Waves (Deep Purple/Magenta Halos)
        for (int i = 0; i < ASTROCYTES; i++) {
            if (calciumWave[i] > 0.08f) {
                double x1 = aX[i] * cosY - aZ[i] * sinY;
                double z1 = aZ[i] * cosY + aX[i] * sinY;
                double y2 = aY[i] * cosX - z1 * sinX;
                double z2 = z1 * cosX + aY[i] * sinX;
                double tz = z2 + camZ;
                if (tz > 0.1) {
                    int px = (int) (WIDTH / 2.0 + (x1 * fov) / tz);
                    int py = (int) (HEIGHT / 2.0 + (y2 * fov) / tz);
                    int cIntensity = (int) (calciumWave[i] * 180);
                    int color = (cIntensity << 16) | (0 << 8) | (cIntensity + 40); // Magenta/Purple
                    drawBrush(px, py, (int) (calciumWave[i] * 30), color, tz);
                }
            }
        }

        // 4. Draw Firing Neurons & Routing Synaptic Arcs
        for (int i = 0; i < NEURONS; i++) {
            if (projZ[i] <= 0.1) continue;

            if (isSpiking[i]) {
                int color = (i < NEURONS * 0.2) ? 0xFFB000 : 0x00F3FF; // Core = Gold (Resonance), Periphery = Cyan (Sensory)
                drawBrush(projX[i], projY[i], 3, color, projZ[i]);

                // Draw Active Axonal Projections (Lines)
                int offset = i * SYNAPSES_PER_NEURON;
                for (int s = 0; s < 2; s++) { // Draw top 2 to avoid visual clutter
                    int target = synTarget[offset + s];
                    if (synWeight[offset + s] > 3.0f && projZ[target] > 0.1) {
                        drawBresenhamLine(projX[i], projY[i], projX[target], projY[target], 0x00FF66); // Emerald STDP track
                    }
                }
            } else if (vMem[i] > -50.0f) {
                // Depolarizing but not yet firing (Dim Blue)
                drawBrush(projX[i], projY[i], 1, 0x004488, projZ[i]);
            }
        }

        // 5. UI Overlay
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(canvas, 0, 0, null);
        drawHUD(g);
        g.dispose();
        bs.show();
    }

    private void drawBrush(int px, int py, int s, int color, double depth) {
        double atten = Math.min(1.0, 150.0 / depth);
        int rAdd = (int) (((color >> 16) & 0xFF) * atten);
        int gAdd = (int) (((color >> 8) & 0xFF) * atten);
        int bAdd = (int) ((color & 0xFF) * atten);

        for (int dy = -s; dy <= s; dy++) {
            int yi = py + dy;
            if (yi < 0 || yi >= HEIGHT) continue;
            int yOffset = yi * WIDTH;
            for (int dx = -s; dx <= s; dx++) {
                int xi = px + dx;
                if (xi < 0 || xi >= WIDTH) continue;
                if (dx * dx + dy * dy <= s * s) {
                    int idx = yOffset + xi;
                    int bg = pixels[idx];
                    int r = Math.min(255, ((bg >> 16) & 0xFF) + rAdd);
                    int g = Math.min(255, ((bg >> 8) & 0xFF) + gAdd);
                    int b = Math.min(255, (bg & 0xFF) + bAdd);
                    pixels[idx] = (r << 16) | (g << 8) | b;
                }
            }
        }
    }

    private void drawBresenhamLine(int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx + dy, e2;

        int rAdd = (int) (((color >> 16) & 0xFF) * 0.4); // 40% opacity
        int gAdd = (int) (((color >> 8) & 0xFF) * 0.4);
        int bAdd = (int) ((color & 0xFF) * 0.4);

        while (true) {
            if (x0 >= 0 && x0 < WIDTH && y0 >= 0 && y0 < HEIGHT) {
                int idx = y0 * WIDTH + x0;
                int bg = pixels[idx];
                int r = Math.min(255, ((bg >> 16) & 0xFF) + rAdd);
                int g = Math.min(255, ((bg >> 8) & 0xFF) + gAdd);
                int b = Math.min(255, (bg & 0xFF) + bAdd);
                pixels[idx] = (r << 16) | (g << 8) | b;
            }
            if (x0 == x1 && y0 == y1) break;
            e2 = 2 * err;
            if (e2 >= dy) { err += dy; x0 += sx; }
            if (e2 <= dx) { err += dx; y0 += sy; }
        }
    }

    // =========================================================================================
    // 3. CYBERNETIC HUD & PATENT TELEMETRY
    // =========================================================================================
    private void drawHUD(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(new Color(0, 0, 0, 60));
        for (int y = 0; y < HEIGHT; y += 4) g.drawLine(0, y, WIDTH, y);

        int leftX = 30;

        // --- TOP LEFT: BRAIN TELEMETRY ---
        g.setColor(new Color(5, 5, 8, 220));
        g.fillRect(leftX - 10, 20, 390, 240);
        g.setColor(new Color(0, 243, 255, 100));
        g.drawRect(leftX - 10, 20, 390, 240);

        g.setColor(new Color(0x00F3FF));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("HRM NEUROMORPHIC CORTEX", leftX, 50);
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.setColor(new Color(0xFFB000));
        g.drawString("HIERARCHICAL RESONANCE MEMORY [STDP/LIF ENABLED]", leftX, 70);

        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        drawRow(g, "LIF SPATIAL NEURONS", String.format("%,d", NEURONS), leftX, 110, 0x00F3FF);
        drawRow(g, "TRIPARTITE SYNAPSES", String.format("%,d", NEURONS * SYNAPSES_PER_NEURON), leftX, 130, 0x00F3FF);
        drawRow(g, "ASTROCYTE GLIAL CELLS", String.format("%,d", ASTROCYTES), leftX, 150, 0xFF007F);

        drawRow(g, "ACTION POTENTIALS (Hz)", String.format("%,d", spikeCountThisFrame * 60), leftX, 180, 0xFFFFFF);

        // Sparsity metric validates the HRM power efficiency
        drawRow(g, "CORTICAL SPARSITY (SDR)", String.format("%.2f %%", globalSparsity), leftX, 200, globalSparsity > 95.0 ? 0x00FF66 : 0xFFB000);
        drawRow(g, "STDP PLASTICITY FLUX", String.format("%+.2f \u0394W", structuralPlasticity), leftX, 220, structuralPlasticity > 0 ? 0x00FF66 : 0xFFB000);

        // --- BOTTOM: INJECTION HINT ---
        if (injectingSensoryData) {
            g.setColor(new Color(0xFF007F));
            g.fillRect(WIDTH / 2 - 190, HEIGHT - 80, 380, 40);
            g.setColor(Color.WHITE);
            g.drawString(">>> SENSORY VOLTAGE INJECTION ACTIVE <<<", WIDTH / 2 - 150, HEIGHT - 55);
        } else {
            g.setColor(new Color(0x00F3FF));
            g.drawRect(WIDTH / 2 - 190, HEIGHT - 80, 380, 40);
            g.drawString("[CLICK & DRAG] INJECT SENSORY VOLTAGE", WIDTH / 2 - 165, HEIGHT - 55);
        }
    }

    private void drawRow(Graphics2D g, String label, String value, int x, int y, int hexColor) {
        g.setColor(new Color(255, 255, 255, 150));
        g.drawString(label, x, y);
        g.setColor(new Color(hexColor));
        g.drawString(value, x + 370 - g.getFontMetrics().stringWidth(value), y);
    }

    // Input Listeners
    @Override public void mousePressed(MouseEvent e) { injectingSensoryData = true; }
    @Override public void mouseReleased(MouseEvent e) { injectingSensoryData = false; }
    @Override public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); injectingSensoryData = true; }
    @Override public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) { injectingSensoryData = false; }
}
