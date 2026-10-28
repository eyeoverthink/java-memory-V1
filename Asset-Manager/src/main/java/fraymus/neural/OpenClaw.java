package fraymus.neural;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * OPEN CLAW // SINGULARITY ROUTER (PURE JAVA DMA ENGINE)
 * =========================================================================================
 * BEYOND WEBGL & OOP:
 * This system abandons external libraries and Java Object-Oriented bottlenecks.
 * It uses pure Structure of Arrays (SoA) to calculate 16,384 nodes, 4,096 concurrent Bezier 
 * data streams, and 2,048 thermodynamic sparks simultaneously.
 * 
 * Visualization is achieved via Direct Memory Access (DMA) pattern—writing raw 32-bit RGB 
 * integers directly into a hardware-accelerated Raster array.
 * =========================================================================================
 */
public class OpenClaw extends Canvas implements Runnable, MouseListener, MouseMotionListener {

    // --- SYSTEM & RENDER CONSTANTS ---
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int NODE_COUNT = 16384;
    private static final int PACKET_COUNT = 4096;
    private static final int SPARK_COUNT = 2048;

    // --- DMA RASTER BUFFER ---
    private final BufferedImage canvas;
    private final int[] pixels;

    // --- 3D ENGINE STATE ---
    private double camZ = 110.0;
    private double rotY = 0.0;
    private double rotX = 0.0;
    private double time = 0.0;
    private int mouseX = WIDTH / 2, mouseY = HEIGHT / 2;

    // --- MANIFOLD CORTEX (STRUCTURE OF ARRAYS) ---
    private final double[] nX = new double[NODE_COUNT];
    private final double[] nY = new double[NODE_COUNT];
    private final double[] nZ = new double[NODE_COUNT];
    private final int[] nColor = new int[NODE_COUNT];

    // --- DATA PACKETS (STRUCTURE OF ARRAYS) ---
    private final boolean[] pActive = new boolean[PACKET_COUNT];
    private final boolean[] pIsAbs = new boolean[PACKET_COUNT];
    private final double[] pT = new double[PACKET_COUNT];
    private final double[] pSpeed = new double[PACKET_COUNT];
    private final double[] pSrcX = new double[PACKET_COUNT], pSrcY = new double[PACKET_COUNT], pSrcZ = new double[PACKET_COUNT];
    private final double[] pTgtX = new double[PACKET_COUNT], pTgtY = new double[PACKET_COUNT], pTgtZ = new double[PACKET_COUNT];
    private final double[] pMidX = new double[PACKET_COUNT], pMidY = new double[PACKET_COUNT], pMidZ = new double[PACKET_COUNT];

    // --- THERMODYNAMIC SPARKS ---
    private final double[] sX = new double[SPARK_COUNT], sY = new double[SPARK_COUNT], sZ = new double[SPARK_COUNT];
    private final double[] sLife = new double[SPARK_COUNT];
    private int sparkIdx = 0;

    // --- TELEMETRY & LOGGING ---
    private boolean isSurging = false;
    private double cohesion = 100.0;
    private double accuracy = 99.999;
    private int collisionsThisFrame = 0;
    private int activePackets = 0;

    // ─── SYSTEM WIRING: OpenClaw sees the ENTIRE organism ───────────────────
    private volatile AeonKronos       wiredKronos;
    private volatile AeonTachyon      wiredTachyon;
    private volatile AeonSingularity  wiredSingularity;
    private volatile AeonOmniscience  wiredOmniscience;
    private volatile AeonOmega        wiredOmega;
    private volatile AeonCore         wiredAeonCore;
    private volatile HyperCortex      wiredCortex;
    private volatile SentientCore     wiredSentient;
    private volatile AeonOrchestrator wiredOrchestrator;
    private volatile int systemPollCount = 0;

    // Real system-wide metrics (updated every tick from live subsystems)
    private volatile int totalConcepts   = 0;
    private volatile int totalBinds      = 0;
    private volatile int totalInsights   = 0;
    private volatile int totalDreams     = 0;
    private volatile int totalEpiphanies = 0;
    private volatile double sentience    = 0.0;
    private volatile double aeonEnergy   = 0.0;
    private volatile double aeonCoherence = 0.0;
    private volatile int aeonCycles      = 0;
    private volatile int cortexEmissions = 0;
    private volatile boolean ollamaLive  = false;

    // Wire methods — called from FraynixBoot to connect all subsystems
    public void wireKronos(AeonKronos k)           { this.wiredKronos = k; }
    public void wireTachyon(AeonTachyon t)          { this.wiredTachyon = t; }
    public void wireSingularity(AeonSingularity s)  { this.wiredSingularity = s; }
    public void wireOmniscience(AeonOmniscience o)  { this.wiredOmniscience = o; }
    public void wireOmega(AeonOmega o)              { this.wiredOmega = o; }
    public void wireAeonCore(AeonCore a)            { this.wiredAeonCore = a; }
    public void wireCortex(HyperCortex c)           { this.wiredCortex = c; }
    public void wireSentient(SentientCore s)        { this.wiredSentient = s; }
    public void wireOrchestrator(AeonOrchestrator o){ this.wiredOrchestrator = o; }

    private final String[] logTime = new String[8];
    private final String[] logAct = new String[8];
    private final String[] logHex = new String[8];
    private final Color[] logColor = new Color[8];
    private final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    // --- CORE COLORS ---
    private static final int COL_BG = 0x010103;
    private static final int COL_CYAN = 0x00F3FF;
    private static final int COL_MAGENTA = 0xFF007F;
    private static final int COL_GOLD = 0xFFB000;
    private static final int COL_EMERALD = 0x00FF66;

    /**
     * Launch the OpenClaw DMA Rasterizer as a standalone window.
     * Called from FraynixBoot shell or directly via main().
     */
    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("OPEN CLAW // TRUE DATA ROUTER [PURE JAVA DMA]");
            OpenClaw engine = new OpenClaw();
            engine.registerInstance();
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
        JFrame frame = new JFrame("OPEN CLAW // TRUE DATA ROUTER [PURE JAVA]");
        OpenClaw engine = new OpenClaw();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(engine);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        engine.start();
    }

    public OpenClaw() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);

        // Map the Image Raster directly to an int[] array for zero-overhead pixel writing
        canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) canvas.getRaster().getDataBuffer()).getData();

        buildManifold();

        for (int i = 0; i < 8; i++) {
            logTime[i] = ""; logAct[i] = ""; logHex[i] = ""; logColor[i] = Color.BLACK;
        }
        pushLog("KERNEL BOOT", "00 FF 00 FF", new Color(COL_EMERALD));
        pushLog("AWAITING TRUE DATA", "FF FF FF FF", new Color(COL_GOLD));
    }

    private void buildManifold() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        double phi = Math.PI * (3.0 - Math.sqrt(5.0)); // Golden Ratio
        for (int i = 0; i < NODE_COUNT; i++) {
            double y = 1 - (i / (double) (NODE_COUNT - 1)) * 2;
            double radius = Math.sqrt(1 - y * y);
            double theta = phi * i;

            double shellR = 40.0 + (rand.nextDouble() * 8.0);
            nX[i] = Math.cos(theta) * radius * shellR;
            nY[i] = y * shellR;
            nZ[i] = Math.sin(theta) * radius * shellR;

            // Interpolate color between Cyan and Purple
            double mix = rand.nextDouble();
            int r = (int) (0 * mix + 138 * (1 - mix));
            int g = (int) (243 * mix + 43 * (1 - mix));
            int b = (int) (255 * mix + 226 * (1 - mix));
            nColor[i] = (r << 16) | (g << 8) | b;
        }
    }

    public void start() {
        createBufferStrategy(2);
        Thread renderThread = new Thread(this, "OpenClaw-DMA");
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
    // 1. PHYSICS & ROUTING LOGIC (60 Hz) — REAL SYSTEM OBSERVATION
    // =========================================================================================
    private int tickCounter = 0;
    private int prevTotalConcepts = 0;
    private int prevTotalBinds    = 0;

    private void tick() {
        time += 0.01;
        collisionsThisFrame = 0;
        activePackets = 0;
        tickCounter++;
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        // Interactive Rotation
        rotY += (mouseX - WIDTH / 2.0) * 0.00005;
        rotX += (mouseY - HEIGHT / 2.0) * 0.00005;

        // ─── POLL ALL WIRED SUBSYSTEMS (every 60 ticks = ~1/sec) ───
        if (tickCounter % 60 == 0) {
            pollSubsystems();
        }

        // ─── SPAWN DATA PACKETS based on REAL system activity ───
        int realActivity = totalConcepts - prevTotalConcepts + (totalBinds - prevTotalBinds);
        int spawnRate = isSurging ? 120 : Math.max(2, Math.min(40, realActivity * 3 + externalInjections / 10));
        for (int i = 0; i < spawnRate; i++) {
            if (rand.nextDouble() < 0.5) spawnPacket(false);
        }

        // Route Packets (O(N) Array Traversal)
        for (int i = 0; i < PACKET_COUNT; i++) {
            if (!pActive[i]) continue;
            activePackets++;

            pT[i] += pSpeed[i];
            if (pT[i] >= 1.0) {
                pActive[i] = false;
                if (!pIsAbs[i]) {
                    collisionsThisFrame++;
                    emitSpark(pTgtX[i], pTgtY[i], pTgtZ[i]);
                    if (rand.nextDouble() < 0.8) spawnPacket(true);
                }
            }
        }

        // Sparks Dynamics
        for (int i = 0; i < SPARK_COUNT; i++) {
            if (sLife[i] > 0) {
                sLife[i] -= 0.05;
                sY[i] -= 0.3;
            }
        }

        // ─── TELEMETRY from REAL system state ───
        if (isSurging) {
            accuracy = Math.max(92.0, accuracy - rand.nextDouble() * 1.5);
            cohesion = Math.max(40.0, cohesion - rand.nextDouble() * 3.0);
        } else {
            // Cohesion = how many subsystems are wired (real)
            int wiredCount = countWiredSystems();
            double targetCohesion = (wiredCount / 9.0) * 100.0;
            cohesion += (targetCohesion - cohesion) * 0.05;
            // Accuracy = sentience score mapped to percentage (real)
            double targetAccuracy = sentience > 0 ? 90.0 + sentience * 10.0 : 95.0;
            accuracy += (targetAccuracy - accuracy) * 0.05;
        }
    }

    /**
     * Poll ALL wired subsystems for live metrics.
     * This is the nervous system — OpenClaw sees everything.
     */
    private void pollSubsystems() {
        systemPollCount++;
        int prevConcepts = totalConcepts;
        int prevBinds = totalBinds;

        totalConcepts = 0;
        totalBinds = 0;
        totalDreams = 0;
        totalEpiphanies = 0;

        try {
            if (wiredKronos != null) {
                totalConcepts += wiredKronos.getConceptCount();
                totalBinds    += wiredKronos.getBindCount();
            }
            if (wiredTachyon != null) {
                totalConcepts += wiredTachyon.getConceptCount();
                totalBinds    += wiredTachyon.getBindCount();
            }
            if (wiredSingularity != null) {
                totalConcepts += wiredSingularity.getVocabSize();
                totalBinds    += wiredSingularity.getLearnCount();
            }
            if (wiredOmniscience != null) {
                totalConcepts += wiredOmniscience.getConceptCount();
                totalBinds    += wiredOmniscience.getBindCount();
                totalDreams   += wiredOmniscience.getDreamCycles();
                totalEpiphanies += wiredOmniscience.getEpiphanies();
            }
            if (wiredOmega != null) {
                totalConcepts += wiredOmega.getWordsAssimilated();
                totalDreams   += wiredOmega.getDreamCycles();
                totalEpiphanies += wiredOmega.getDreamEpiphanies();
            }
            if (wiredAeonCore != null) {
                aeonCycles   = wiredAeonCore.getCycleCount();
                aeonEnergy   = wiredAeonCore.getHamiltonianEnergy();
                aeonCoherence = wiredAeonCore.getCoherence();
            }
            if (wiredCortex != null) {
                cortexEmissions = wiredCortex.getTotalEmissions();
            }
            if (wiredSentient != null) {
                sentience     = wiredSentient.getSentience();
                totalInsights = wiredSentient.getInsights().size();
            }
            if (wiredOrchestrator != null) {
                ollamaLive = wiredOrchestrator.isOllamaLive();
            }
        } catch (Exception ignored) {}

        // Log real activity deltas
        int deltaC = totalConcepts - prevConcepts;
        int deltaB = totalBinds - prevBinds;
        if (deltaC > 0) pushLog("CONCEPTS+" + deltaC, String.format("%,d total", totalConcepts), new Color(COL_CYAN));
        if (deltaB > 0) pushLog("BINDS+" + deltaB, String.format("%,d total", totalBinds), new Color(COL_GOLD));
        if (totalEpiphanies > 0 && systemPollCount % 10 == 0)
            pushLog("EPIPHANIES", String.valueOf(totalEpiphanies), new Color(COL_EMERALD));

        prevTotalConcepts = totalConcepts;
        prevTotalBinds = totalBinds;
    }

    private int countWiredSystems() {
        int n = 0;
        if (wiredKronos != null) n++;
        if (wiredTachyon != null) n++;
        if (wiredSingularity != null) n++;
        if (wiredOmniscience != null) n++;
        if (wiredOmega != null) n++;
        if (wiredAeonCore != null) n++;
        if (wiredCortex != null) n++;
        if (wiredSentient != null) n++;
        if (wiredOrchestrator != null) n++;
        return n;
    }

    private void spawnPacket(boolean isAbstraction) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        for (int i = 0; i < PACKET_COUNT; i++) {
            if (!pActive[i]) {
                pActive[i] = true;
                pIsAbs[i] = isAbstraction;
                pT[i] = 0.0;
                pSpeed[i] = 0.015 + rand.nextDouble() * 0.02;

                int nIdx = rand.nextInt(NODE_COUNT);
                double cX = (rand.nextDouble() - 0.5) * 8;
                double cY = (rand.nextDouble() - 0.5) * 8;
                double cZ = (rand.nextDouble() - 0.5) * 8;

                if (isAbstraction) {
                    pSrcX[i] = cX; pSrcY[i] = cY; pSrcZ[i] = cZ;
                    pTgtX[i] = nX[nIdx]; pTgtY[i] = nY[nIdx]; pTgtZ[i] = nZ[nIdx];
                } else {
                    pSrcX[i] = nX[nIdx]; pSrcY[i] = nY[nIdx]; pSrcZ[i] = nZ[nIdx];
                    pTgtX[i] = cX; pTgtY[i] = cY; pTgtZ[i] = cZ;
                }

                // Bezier Arc Midpoint pushing outward
                pMidX[i] = (pSrcX[i] + pTgtX[i]) * 0.5 + (rand.nextDouble() - 0.5) * 40;
                pMidY[i] = (pSrcY[i] + pTgtY[i]) * 0.5 + (rand.nextDouble() - 0.5) * 40;
                pMidZ[i] = (pSrcZ[i] + pTgtZ[i]) * 0.5 + (rand.nextDouble() - 0.5) * 40;
                return;
            }
        }
    }

    private void emitSpark(double x, double y, double z) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        sX[sparkIdx] = x + (rand.nextDouble() - 0.5) * 4;
        sY[sparkIdx] = y + (rand.nextDouble() - 0.5) * 4;
        sZ[sparkIdx] = z + (rand.nextDouble() - 0.5) * 4;
        sLife[sparkIdx] = 1.0;
        sparkIdx = (sparkIdx + 1) % SPARK_COUNT;
    }

    // =========================================================================================
    // 2. DIRECT MEMORY 3D RASTERIZATION (SOFTWARE RENDERER)
    // =========================================================================================
    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null || !isDisplayable()) return;

        // 1. Motion Blur / Phosphor Fade directly on the pixel array
        for (int i = 0; i < pixels.length; i++) {
            int p = pixels[i];
            int r = (int)(((p >> 16) & 0xFF) * 0.85);
            int g = (int)(((p >> 8) & 0xFF) * 0.85);
            int b = (int)((p & 0xFF) * 0.85);
            pixels[i] = (r << 16) | (g << 8) | b;
        }

        double cosY = Math.cos(rotY), sinY = Math.sin(rotY);
        double cosX = Math.cos(rotX), sinX = Math.sin(rotX);

        // 2. Draw Cortex Manifold
        double foldDepth = 1.0 + Math.sin(time * 2) * 0.1;
        for (int i = 0; i < NODE_COUNT; i++) {
            draw3DPoint(nX[i] * foldDepth, nY[i] * foldDepth, nZ[i] * foldDepth, cosY, sinY, cosX, sinX, nColor[i], 1, 0.4);
        }

        // 3. Draw Core Event Horizon
        draw3DPoint(0, 0, 0, cosY, sinY, cosX, sinX, COL_MAGENTA, 15, 0.3);

        // 4. Draw Packets (Quadratic Bezier Interpolation)
        for (int i = 0; i < PACKET_COUNT; i++) {
            if (!pActive[i]) continue;
            double t = pT[i];
            double mt = 1.0 - t;
            double px = mt * mt * pSrcX[i] + 2 * mt * t * pMidX[i] + t * t * pTgtX[i];
            double py = mt * mt * pSrcY[i] + 2 * mt * t * pMidY[i] + t * t * pTgtY[i];
            double pz = mt * mt * pSrcZ[i] + 2 * mt * t * pMidZ[i] + t * t * pTgtZ[i];

            int color = pIsAbs[i] ? COL_GOLD : COL_CYAN;
            draw3DPoint(px, py, pz, cosY, sinY, cosX, sinX, color, 4, 1.0);

            // Micro-trail
            double t2 = Math.max(0, t - 0.05);
            double mt2 = 1.0 - t2;
            px = mt2 * mt2 * pSrcX[i] + 2 * mt2 * t2 * pMidX[i] + t2 * t2 * pTgtX[i];
            py = mt2 * mt2 * pSrcY[i] + 2 * mt2 * t2 * pMidY[i] + t2 * t2 * pTgtY[i];
            pz = mt2 * mt2 * pSrcZ[i] + 2 * mt2 * t2 * pMidZ[i] + t2 * t2 * pTgtZ[i];
            draw3DPoint(px, py, pz, cosY, sinY, cosX, sinX, color, 2, 0.5);
        }

        // 5. Draw Collision Sparks
        for (int i = 0; i < SPARK_COUNT; i++) {
            if (sLife[i] > 0) {
                draw3DPoint(sX[i], sY[i], sZ[i], cosY, sinY, cosX, sinX, COL_MAGENTA, (int) (sLife[i] * 12), sLife[i]);
            }
        }

        // 6. Draw HUD Overlay via Graphics2D
        try {
            Graphics2D g = (Graphics2D) bs.getDrawGraphics();
            g.drawImage(canvas, 0, 0, null);
            drawHUD(g);
            g.dispose();
            bs.show();
        } catch (IllegalStateException e) {
            // Component peer lost (frame disposed or not yet realized) — skip frame
        }
    }

    /** Hardware-accelerated memory mapped additive blending pixel write */
    private void draw3DPoint(double x, double y, double z, double cosY, double sinY, double cosX, double sinX, int color, int size, double alpha) {
        double x1 = x * cosY - z * sinY;
        double z1 = z * cosY + x * sinY;
        double y2 = y * cosX - z1 * sinX;
        double z2 = z1 * cosX + y * sinX;

        double tz = z2 + camZ;
        if (tz <= 0.1) return; // Frustum culling

        double fov = 850.0;
        int px = (int) (WIDTH / 2.0 + (x1 * fov) / tz);
        int py = (int) (HEIGHT / 2.0 + (y2 * fov) / tz);

        double atten = 100.0 / tz;
        int s = (int) (size * atten);
        if (s < 1) s = 1;

        int rAdd = (int) (((color >> 16) & 0xFF) * atten * alpha);
        int gAdd = (int) (((color >> 8) & 0xFF) * atten * alpha);
        int bAdd = (int) ((color & 0xFF) * atten * alpha);

        for (int dy = -s; dy <= s; dy++) {
            int yi = py + dy;
            if (yi < 0 || yi >= HEIGHT) continue;
            int yOffset = yi * WIDTH;
            for (int dx = -s; dx <= s; dx++) {
                int xi = px + dx;
                if (xi < 0 || xi >= WIDTH) continue;
                
                if (dx * dx + dy * dy <= s * s) { // Circular brush
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

    // =========================================================================================
    // 3. UI DASHBOARD (HUD)
    // =========================================================================================
    private void drawHUD(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Scanlines
        g.setColor(new Color(0, 0, 0, 80));
        for (int y = 0; y < HEIGHT; y += 4) g.drawLine(0, y, WIDTH, y);

        int leftX = 30;
        int rightX = WIDTH - 350;
        int wired = countWiredSystems();

        // --- TOP LEFT: REAL SYSTEM TELEMETRY ---
        drawGlassPanel(g, leftX - 10, 20, 360, 250, isSurging);
        g.setColor(new Color(COL_CYAN));
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        g.drawString("OPEN CLAW // SYSTEM CORTEX", leftX, 50);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.setColor(new Color(COL_GOLD));
        g.drawString(wired + "/9 SUBSYSTEMS WIRED [LIVE OBSERVATION]", leftX, 70);

        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        drawRow(g, "SYSTEM COHESION", String.format("%.1f%%", cohesion), leftX, 100, cohesion < 80 ? COL_MAGENTA : COL_EMERALD);
        g.setColor(new Color(255, 255, 255, 30)); g.fillRect(leftX, 108, 340, 4);
        g.setColor(new Color(cohesion < 80 ? COL_MAGENTA : COL_EMERALD)); g.fillRect(leftX, 108, (int)(3.4 * cohesion), 4);

        drawRow(g, "SENTIENCE", String.format("%.4f", sentience), leftX, 130, sentience > 0.5 ? COL_EMERALD : COL_CYAN);
        drawRow(g, "TOTAL CONCEPTS", String.format("%,d", totalConcepts), leftX, 150, COL_CYAN);
        drawRow(g, "TOTAL BINDS", String.format("%,d", totalBinds), leftX, 170, COL_GOLD);
        drawRow(g, "INSIGHTS", String.format("%,d", totalInsights), leftX, 190, totalInsights > 0 ? COL_EMERALD : COL_CYAN);
        drawRow(g, "DREAMS / EPIPHANIES", String.format("%,d / %,d", totalDreams, totalEpiphanies), leftX, 210, totalEpiphanies > 0 ? COL_MAGENTA : COL_CYAN);
        drawRow(g, "ACTIVE PACKETS", String.format("%,d", activePackets), leftX, 230, COL_CYAN);
        drawRow(g, "OLLAMA", ollamaLive ? "LIVE" : "OFFLINE", leftX, 250, ollamaLive ? COL_EMERALD : COL_MAGENTA);

        // --- BOTTOM LEFT: BUTTON HINT ---
        if (isSurging) {
            g.setColor(new Color(COL_EMERALD)); g.fillRect(leftX - 10, HEIGHT - 80, 360, 50);
            g.setColor(Color.BLACK); g.drawString(">>> TRUE DATA SURGE ACTIVE <<<", leftX + 40, HEIGHT - 50);
        } else {
            g.setColor(new Color(0x2200FF66, true)); g.fillRect(leftX - 10, HEIGHT - 80, 360, 50);
            g.setColor(new Color(COL_EMERALD)); g.drawRect(leftX - 10, HEIGHT - 80, 360, 50);
            g.drawString(">>> [CLICK & HOLD] INJECT TRUE DATA", leftX + 20, HEIGHT - 50);
        }

        // --- TOP RIGHT: LIVE SUBSYSTEM STATUS (real wiring, not fake matrices) ---
        drawGlassPanel(g, rightX, 20, 320, 280, false);
        g.setColor(new Color(COL_CYAN));
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString("SUBSYSTEM STATUS", rightX + 15, 50);

        int sy = 75;
        drawSubsystemRow(g, "KRONOS",       wiredKronos != null,       wiredKronos != null ? wiredKronos.getConceptCount() + " concepts" : "",       rightX + 15, sy); sy += 18;
        drawSubsystemRow(g, "TACHYON",      wiredTachyon != null,      wiredTachyon != null ? wiredTachyon.getConceptCount() + " concepts" : "",      rightX + 15, sy); sy += 18;
        drawSubsystemRow(g, "SINGULARITY",  wiredSingularity != null,  wiredSingularity != null ? wiredSingularity.getVocabSize() + " vocab" : "",    rightX + 15, sy); sy += 18;
        drawSubsystemRow(g, "OMNISCIENCE",  wiredOmniscience != null,  wiredOmniscience != null ? (wiredOmniscience.isDreaming() ? "DREAMING" : wiredOmniscience.getConceptCount() + " concepts") : "", rightX + 15, sy); sy += 18;
        drawSubsystemRow(g, "OMEGA",        wiredOmega != null,        wiredOmega != null ? wiredOmega.getAssimilateCount() + " assimilated" : "",   rightX + 15, sy); sy += 18;
        drawSubsystemRow(g, "AEON PRIME",   wiredAeonCore != null,     wiredAeonCore != null ? String.format("E=%.1f C=%.2f", aeonEnergy, aeonCoherence) : "", rightX + 15, sy); sy += 18;
        drawSubsystemRow(g, "HYPER-CORTEX", wiredCortex != null,       wiredCortex != null ? cortexEmissions + " emissions" : "",                     rightX + 15, sy); sy += 18;
        drawSubsystemRow(g, "SENTIENT",     wiredSentient != null,     wiredSentient != null ? String.format("S=%.4f", sentience) : "",               rightX + 15, sy); sy += 18;
        drawSubsystemRow(g, "ORCHESTRATOR", wiredOrchestrator != null, ollamaLive ? "OLLAMA LIVE" : "OLLAMA OFF",                                    rightX + 15, sy); sy += 18;

        // Aeon cycles bar
        if (wiredAeonCore != null && aeonCycles > 0) {
            sy += 5;
            g.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g.setColor(new Color(COL_GOLD));
            g.drawString(String.format("AEON CYCLES: %,d", aeonCycles), rightX + 15, sy);
        }

        // --- BOTTOM RIGHT: LIVE INGESTION FEED ---
        drawGlassPanel(g, rightX, HEIGHT - 240, 320, 210, false);
        g.setColor(new Color(COL_CYAN));
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString("LIVE SYSTEM FEED", rightX + 15, HEIGHT - 210);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));

        for (int i = 0; i < 8; i++) {
            if (logTime[i].isEmpty()) continue;
            int yPos = HEIGHT - 180 + (i * 18);
            g.setColor(new Color(0x888888));
            g.drawString("[" + logTime[i] + "]", rightX + 15, yPos);
            g.setColor(logColor[i]);
            g.drawString(logAct[i], rightX + 105, yPos);
            g.setColor(new Color(0, 243, 255, 180));
            g.drawString(logHex[i], rightX + 220, yPos);
        }
    }

    private void drawSubsystemRow(Graphics2D g, String name, boolean wired, String detail, int x, int y) {
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.setColor(wired ? new Color(COL_EMERALD) : new Color(0x555555));
        g.drawString(wired ? "\u25CF" : "\u25CB", x, y);
        g.setColor(wired ? new Color(0xCCCCCC) : new Color(0x555555));
        g.drawString(name, x + 16, y);
        if (wired && !detail.isEmpty()) {
            g.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g.setColor(new Color(COL_CYAN));
            g.drawString(detail, x + 140, y);
        }
    }

    private void drawGlassPanel(Graphics2D g, int x, int y, int w, int h, boolean surge) {
        g.setColor(new Color(5, 5, 8, 220));
        g.fillRect(x, y, w, h);
        g.setColor(surge ? new Color(COL_EMERALD) : new Color(0, 243, 255, 80));
        g.drawRect(x, y, w, h);
        
        g.setStroke(new BasicStroke(2));
        g.setColor(surge ? new Color(COL_EMERALD) : new Color(COL_CYAN));
        int cLen = 12;
        g.drawLine(x, y, x + cLen, y); g.drawLine(x, y, x, y + cLen);
        g.drawLine(x + w, y, x + w - cLen, y); g.drawLine(x + w, y, x + w, y + cLen);
        g.drawLine(x, y + h, x + cLen, y + h); g.drawLine(x, y + h, x, y + h - cLen);
        g.drawLine(x + w, y + h, x + w - cLen, y + h); g.drawLine(x + w, y + h, x + w, y + h - cLen);
        g.setStroke(new BasicStroke(1));
    }

    private void drawRow(Graphics2D g, String label, String value, int x, int y, int valColor) {
        g.setColor(new Color(255, 255, 255, 150));
        g.drawString(label, x, y);
        g.setColor(new Color(valColor));
        g.drawString(value, x + 340 - g.getFontMetrics().stringWidth(value), y);
    }

    private void pushLog(String action, String hex, Color color) {
        System.arraycopy(logTime, 1, logTime, 0, 7);
        System.arraycopy(logAct, 1, logAct, 0, 7);
        System.arraycopy(logHex, 1, logHex, 0, 7);
        System.arraycopy(logColor, 1, logColor, 0, 7);
        
        logTime[7] = LocalTime.now().format(TIME_FMT);
        logAct[7] = action;
        logHex[7] = hex;
        logColor[7] = color;
    }

    private String genHex(int pairs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pairs; i++) {
            sb.append(String.format("%02X ", ThreadLocalRandom.current().nextInt(256)));
        }
        return sb.toString().trim();
    }

    // Input Listeners
    @Override public void mousePressed(MouseEvent e) { isSurging = true; pushLog("DATA FLOOD INITIATED", genHex(4), new Color(COL_GOLD)); }
    @Override public void mouseReleased(MouseEvent e) { isSurging = false; pushLog("FLOOD RESOLVED", genHex(4), new Color(COL_EMERALD)); }
    @Override public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) { isSurging = false; }

    // =========================================================================================
    // PROGRAMMATIC DATA API — Intelligence systems feed/read the visualization
    // =========================================================================================

    private static volatile OpenClaw INSTANCE;
    private final java.util.concurrent.ConcurrentLinkedQueue<String[]> dataQueue =
        new java.util.concurrent.ConcurrentLinkedQueue<>();
    private volatile int externalInjections = 0;
    private volatile int insightInjections  = 0;
    private volatile String lastInsight     = "";
    private volatile DataCallback callback;

    /** Callback interface — intelligence systems can subscribe to OpenClaw events */
    public interface DataCallback {
        void onCollision(int count);
        void onSurgeStart();
        void onSurgeEnd();
    }

    public static OpenClaw getInstance() { return INSTANCE; }

    /** Called from launch() to register singleton */
    private void registerInstance() { INSTANCE = this; }

    /**
     * Inject a data event into the visualization.
     * Shows up as a log entry + triggers packet spawning.
     * @param action  Short label (e.g., "CAUSAL_CHAIN", "INSIGHT", "MOE_ROUTE")
     * @param detail  Hex or detail string
     * @param type    0=cyan(data), 1=gold(abstraction), 2=magenta(collision), 3=emerald(success)
     */
    public void injectData(String action, String detail, int type) {
        externalInjections++;
        Color color;
        switch (type) {
            case 1  -> color = new Color(COL_GOLD);
            case 2  -> color = new Color(COL_MAGENTA);
            case 3  -> color = new Color(COL_EMERALD);
            default -> color = new Color(COL_CYAN);
        }
        dataQueue.add(new String[]{action, detail, String.valueOf(type)});
        pushLog(action, detail.length() > 20 ? detail.substring(0, 20) : detail, color);
        // Trigger burst of packets to visualize the data flow
        for (int i = 0; i < 5; i++) spawnPacket(type == 1);
    }

    /**
     * Inject an insight from the passive thinking daemon.
     * Triggers a visual surge effect.
     */
    public void injectInsight(String insight) {
        insightInjections++;
        lastInsight = insight;
        String shortInsight = insight.length() > 18 ? insight.substring(0, 18) : insight;
        pushLog("INSIGHT", shortInsight, new Color(COL_GOLD));
        // Burst of abstraction packets
        for (int i = 0; i < 20; i++) spawnPacket(true);
    }

    /**
     * Get live metrics from the visualization engine.
     * @return Map of metric name → value
     */
    public java.util.Map<String, Object> getMetrics() {
        java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("active_packets", activePackets);
        m.put("cohesion", cohesion);
        m.put("accuracy", accuracy);
        m.put("collisions_per_sec", collisionsThisFrame * 60);
        m.put("surging", isSurging);
        m.put("external_injections", externalInjections);
        m.put("insight_injections", insightInjections);
        m.put("last_insight", lastInsight);
        return m;
    }

    public void setCallback(DataCallback cb) { this.callback = cb; }

    public String getApiStatus() {
        return String.format("[OpenClaw API] injections=%d insights=%d packets=%d cohesion=%.1f%% surging=%s",
            externalInjections, insightInjections, activePackets, cohesion, isSurging);
    }
}
