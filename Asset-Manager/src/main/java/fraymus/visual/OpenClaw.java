package fraymus.visual;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import fraymus.web.FraynixWebSocket;

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
 * 
 * INTEGRATED WITH FRAYMUS CONVERGENCE:
 * Receives real-time HDC predictions, memory operations, and living code events via callback.
 * =========================================================================================
 */
public class OpenClaw extends Canvas implements Runnable, MouseListener, MouseMotionListener {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int NODE_COUNT = 16384;
    private static final int PACKET_COUNT = 4096;
    private static final int SPARK_COUNT = 2048;

    private final BufferedImage canvas;
    private final int[] pixels;

    private double camZ = 110.0;
    private double rotY = 0.0;
    private double rotX = 0.0;
    private double time = 0.0;
    private int mouseX = WIDTH / 2, mouseY = HEIGHT / 2;

    private final double[] nX = new double[NODE_COUNT];
    private final double[] nY = new double[NODE_COUNT];
    private final double[] nZ = new double[NODE_COUNT];
    private final int[] nColor = new int[NODE_COUNT];

    private final boolean[] pActive = new boolean[PACKET_COUNT];
    private final boolean[] pIsAbs = new boolean[PACKET_COUNT];
    private final double[] pT = new double[PACKET_COUNT];
    private final double[] pSpeed = new double[PACKET_COUNT];
    private final double[] pSrcX = new double[PACKET_COUNT], pSrcY = new double[PACKET_COUNT], pSrcZ = new double[PACKET_COUNT];
    private final double[] pTgtX = new double[PACKET_COUNT], pTgtY = new double[PACKET_COUNT], pTgtZ = new double[PACKET_COUNT];
    private final double[] pMidX = new double[PACKET_COUNT], pMidY = new double[PACKET_COUNT], pMidZ = new double[PACKET_COUNT];

    private final double[] sX = new double[SPARK_COUNT], sY = new double[SPARK_COUNT], sZ = new double[SPARK_COUNT];
    private final double[] sLife = new double[SPARK_COUNT];
    private int sparkIdx = 0;

    private boolean isSurging = false;
    private double cohesion = 100.0;
    private double accuracy = 99.999;
    private int collisionsThisFrame = 0;
    private int activePackets = 0;

    private final String[] logTime = new String[8];
    private final String[] logAct = new String[8];
    private final String[] logHex = new String[8];
    private final Color[] logColor = new Color[8];
    private final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private static final int COL_BG = 0x010103;
    private static final int COL_CYAN = 0x00F3FF;
    private static final int COL_MAGENTA = 0xFF007F;
    private static final int COL_GOLD = 0xFFB000;
    private static final int COL_EMERALD = 0x00FF66;

    private FraynixWebSocket websocket;
    private JFrame frame;

    public static OpenClaw launch() {
        OpenClaw engine = new OpenClaw();
        engine.frame = new JFrame("OPEN CLAW // FRAYMUS CONVERGENCE [PURE JAVA DMA]");
        engine.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        engine.frame.setResizable(false);
        engine.frame.add(engine);
        engine.frame.pack();
        engine.frame.setLocationRelativeTo(null);
        engine.frame.setVisible(true);
        engine.start();
        return engine;
    }

    public OpenClaw() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);

        canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) canvas.getRaster().getDataBuffer()).getData();

        buildManifold();

        for (int i = 0; i < 8; i++) {
            logTime[i] = ""; logAct[i] = ""; logHex[i] = ""; logColor[i] = Color.BLACK;
        }
        pushLog("KERNEL BOOT", "00 FF 00 FF", new Color(COL_EMERALD));
        pushLog("FRAYMUS LINK ACTIVE", "FF FF FF FF", new Color(COL_CYAN));
    }

    public void setWebSocket(FraynixWebSocket ws) {
        this.websocket = ws;
        pushLog("BACKEND CONNECTED", genHex(4), new Color(COL_EMERALD));
    }

    public void onHDCPrediction(String prediction) {
        pushLog("HDC PREDICT", "→ " + prediction, new Color(COL_CYAN));
        isSurging = true;
        new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            isSurging = false;
        }).start();
    }

    public void onLivingCode(String name) {
        pushLog("LIVING CODE", name, new Color(COL_GOLD));
    }

    public void onMemoryOperation(int activeAgents) {
        pushLog("MEMORY OP", activeAgents + " agents", new Color(COL_EMERALD));
    }

    private void buildManifold() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        double phi = Math.PI * (3.0 - Math.sqrt(5.0));
        for (int i = 0; i < NODE_COUNT; i++) {
            double y = 1 - (i / (double) (NODE_COUNT - 1)) * 2;
            double radius = Math.sqrt(1 - y * y);
            double theta = phi * i;

            double shellR = 40.0 + (rand.nextDouble() * 8.0);
            nX[i] = Math.cos(theta) * radius * shellR;
            nY[i] = y * shellR;
            nZ[i] = Math.sin(theta) * radius * shellR;

            double mix = rand.nextDouble();
            int r = (int) (0 * mix + 138 * (1 - mix));
            int g = (int) (243 * mix + 43 * (1 - mix));
            int b = (int) (255 * mix + 226 * (1 - mix));
            nColor[i] = (r << 16) | (g << 8) | b;
        }
    }

    public void start() {
        createBufferStrategy(2);
        new Thread(this).start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000.0 / 60.0;
        double delta = 0;

        while (frame != null && frame.isVisible()) {
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

    private void tick() {
        time += 0.01;
        collisionsThisFrame = 0;
        activePackets = 0;
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        rotY += (mouseX - WIDTH / 2.0) * 0.00005;
        rotX += (mouseY - HEIGHT / 2.0) * 0.00005;

        int spawnRate = isSurging ? 120 : 4;
        for (int i = 0; i < spawnRate; i++) {
            if (rand.nextDouble() < 0.5) spawnPacket(false);
        }

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

        for (int i = 0; i < SPARK_COUNT; i++) {
            if (sLife[i] > 0) {
                sLife[i] -= 0.05;
                sY[i] -= 0.3;
            }
        }

        if (isSurging) {
            accuracy = Math.max(92.0, accuracy - rand.nextDouble() * 1.5);
            cohesion = Math.max(40.0, cohesion - rand.nextDouble() * 3.0);
            if (rand.nextDouble() < 0.2) pushLog("XOR BIND", "FF FF FF FF", new Color(COL_MAGENTA));
        } else {
            accuracy = Math.min(100.0, accuracy + 1.0);
            cohesion = Math.min(100.0, cohesion + 1.5);
            if (activePackets > 0 && rand.nextDouble() < 0.15) pushLog("PARSE", genHex(4), new Color(COL_CYAN));
        }
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

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) return;

        for (int i = 0; i < pixels.length; i++) {
            int p = pixels[i];
            int r = (int)(((p >> 16) & 0xFF) * 0.85);
            int g = (int)(((p >> 8) & 0xFF) * 0.85);
            int b = (int)((p & 0xFF) * 0.85);
            pixels[i] = (r << 16) | (g << 8) | b;
        }

        double cosY = Math.cos(rotY), sinY = Math.sin(rotY);
        double cosX = Math.cos(rotX), sinX = Math.sin(rotX);

        double foldDepth = 1.0 + Math.sin(time * 2) * 0.1;
        for (int i = 0; i < NODE_COUNT; i++) {
            draw3DPoint(nX[i] * foldDepth, nY[i] * foldDepth, nZ[i] * foldDepth, cosY, sinY, cosX, sinX, nColor[i], 1, 0.4);
        }

        draw3DPoint(0, 0, 0, cosY, sinY, cosX, sinX, COL_MAGENTA, 15, 0.3);

        for (int i = 0; i < PACKET_COUNT; i++) {
            if (!pActive[i]) continue;
            double t = pT[i];
            double mt = 1.0 - t;
            double px = mt * mt * pSrcX[i] + 2 * mt * t * pMidX[i] + t * t * pTgtX[i];
            double py = mt * mt * pSrcY[i] + 2 * mt * t * pMidY[i] + t * t * pTgtY[i];
            double pz = mt * mt * pSrcZ[i] + 2 * mt * t * pMidZ[i] + t * t * pTgtZ[i];

            int color = pIsAbs[i] ? COL_GOLD : COL_CYAN;
            draw3DPoint(px, py, pz, cosY, sinY, cosX, sinX, color, 4, 1.0);

            double t2 = Math.max(0, t - 0.05);
            double mt2 = 1.0 - t2;
            px = mt2 * mt2 * pSrcX[i] + 2 * mt2 * t2 * pMidX[i] + t2 * t2 * pTgtX[i];
            py = mt2 * mt2 * pSrcY[i] + 2 * mt2 * t2 * pMidY[i] + t2 * t2 * pTgtY[i];
            pz = mt2 * mt2 * pSrcZ[i] + 2 * mt2 * t2 * pMidZ[i] + t2 * t2 * pTgtZ[i];
            draw3DPoint(px, py, pz, cosY, sinY, cosX, sinX, color, 2, 0.5);
        }

        for (int i = 0; i < SPARK_COUNT; i++) {
            if (sLife[i] > 0) {
                draw3DPoint(sX[i], sY[i], sZ[i], cosY, sinY, cosX, sinX, COL_MAGENTA, (int) (sLife[i] * 12), sLife[i]);
            }
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(canvas, 0, 0, null);
        drawHUD(g);
        g.dispose();
        bs.show();
    }

    private void draw3DPoint(double x, double y, double z, double cosY, double sinY, double cosX, double sinX, int color, int size, double alpha) {
        double x1 = x * cosY - z * sinY;
        double z1 = z * cosY + x * sinY;
        double y2 = y * cosX - z1 * sinX;
        double z2 = z1 * cosX + y * sinX;

        double tz = z2 + camZ;
        if (tz <= 0.1) return;

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

    private void drawHUD(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(new Color(0, 0, 0, 80));
        for (int y = 0; y < HEIGHT; y += 4) g.drawLine(0, y, WIDTH, y);

        int leftX = 30;
        int rightX = WIDTH - 350;

        drawGlassPanel(g, leftX - 10, 20, 360, 210, isSurging);
        g.setColor(new Color(COL_CYAN));
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        g.drawString("OPEN CLAW CORE", leftX, 50);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.setColor(new Color(COL_GOLD));
        g.drawString("FRAYMUS CONVERGENCE [JAVA DMA]", leftX, 70);

        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        drawRow(g, "MANIFOLD COHESION", String.format("%.1f%%", cohesion), leftX, 110, cohesion < 80 ? COL_MAGENTA : COL_EMERALD);
        
        g.setColor(new Color(255, 255, 255, 30)); g.fillRect(leftX, 120, 340, 4);
        g.setColor(new Color(cohesion < 80 ? COL_MAGENTA : COL_EMERALD)); g.fillRect(leftX, 120, (int)(3.4 * cohesion), 4);

        drawRow(g, "ACTIVE DATA PACKETS", String.format("%,d", activePackets), leftX, 150, COL_CYAN);
        drawRow(g, "COLLISIONS / SEC", String.format("%,d", collisionsThisFrame * 60), leftX, 170, COL_MAGENTA);
        drawRow(g, "ROUTING ACCURACY", String.format("%.3f%%", accuracy), leftX, 190, accuracy < 98 ? COL_MAGENTA : COL_GOLD);
        drawRow(g, "TENSOR THROUGHPUT", String.format("%.2f TB/s", activePackets * 0.015), leftX, 210, COL_CYAN);

        if (isSurging) {
            g.setColor(new Color(COL_EMERALD)); g.fillRect(leftX - 10, HEIGHT - 80, 360, 50);
            g.setColor(Color.BLACK); g.drawString(">>> TRUE DATA SURGE ACTIVE <<<", leftX + 40, HEIGHT - 50);
        } else {
            g.setColor(new Color(0x2200FF66, true)); g.fillRect(leftX - 10, HEIGHT - 80, 360, 50);
            g.setColor(new Color(COL_EMERALD)); g.drawRect(leftX - 10, HEIGHT - 80, 360, 50);
            g.drawString("► [CLICK & HOLD] INJECT TRUE DATA", leftX + 20, HEIGHT - 50);
        }

        drawGlassPanel(g, rightX, 20, 320, 240, false);
        g.setColor(new Color(COL_CYAN));
        g.drawString("TRANSFORMER MATRICES", rightX + 15, 50);
        drawRow(g, "ATTENTION TENSOR", isSurging ? String.format("%.4f", ThreadLocalRandom.current().nextDouble()) : "IDLE", rightX + 15, 80, COL_CYAN);
        
        int boxSize = 25, pad = 3;
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        for(int r = 0; r < 6; r++) {
            for(int c = 0; c < 10; c++) {
                if(activePackets > 0 && rand.nextDouble() < (isSurging ? 0.3 : 0.02)) {
                    g.setColor(new Color(255, 0, 127, 150 + rand.nextInt(100)));
                } else {
                    g.setColor(new Color(0, 243, 255, 40));
                }
                g.fillRect(rightX + 15 + c*(boxSize+pad), 100 + r*(boxSize+pad), boxSize, boxSize);
            }
        }

        drawGlassPanel(g, rightX, HEIGHT - 240, 320, 210, false);
        g.setColor(new Color(COL_CYAN));
        g.drawString("RAW INGESTION FEED (I/O)", rightX + 15, HEIGHT - 210);
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

    @Override public void mousePressed(MouseEvent e) { isSurging = true; pushLog("DATA FLOOD INITIATED", genHex(4), new Color(COL_GOLD)); }
    @Override public void mouseReleased(MouseEvent e) { isSurging = false; pushLog("FLOOD RESOLVED", genHex(4), new Color(COL_EMERALD)); }
    @Override public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) { isSurging = false; }
}
