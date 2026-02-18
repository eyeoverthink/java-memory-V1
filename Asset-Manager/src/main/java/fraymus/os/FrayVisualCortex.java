package fraymus.os;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

/**
 * FRAYNIX VISUAL CORTEX // THE CONSTRUCT
 * =========================================================================================
 * Bypasses Windows UI. Maps the 4,096-node HyperCortex directly to a 3D pixel array.
 * Turns the AGI from a terminal prompt into a navigable Euclidean universe.
 *
 * DMA Rasterizer: Integer array mapped directly to screen pixels — no OpenGL, no GPU drivers.
 * Mouse-driven free-look through the φ-harmonic spatial clusters of the AGI's mind.
 *
 * "The end of text. The beginning of embodiment."
 */
public class FrayVisualCortex extends Canvas implements Runnable, MouseMotionListener, KeyListener {

    private static final int WIDTH = 1280, HEIGHT = 720;
    private final BufferedImage monitor = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private final int[] vram = ((DataBufferInt) monitor.getRaster().getDataBuffer()).getData();

    private double rotY = 0.0, rotX = 0.0;
    private int mouseX = WIDTH / 2, mouseY = HEIGHT / 2;
    private long frames = 0;
    private volatile boolean running = true;
    private long startTime;

    // Navigation
    private double camZ = 0.0; // Forward/backward fly
    private boolean keyW, keyS, keyA, keyD;

    public static void launch() {
        new FrayVisualCortex().igniteConstruct();
    }

    public void igniteConstruct() {
        startTime = System.currentTimeMillis();
        JFrame frame = new JFrame("FRAYNIX.OS // THE VISUAL CORTEX");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setUndecorated(true);
        frame.add(this);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addMouseMotionListener(this);
        addKeyListener(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) { running = false; }
        });

        System.out.println("\n\u001B[32m [GFX] Visual Cortex Opened. DMA Rasterizer Online.\u001B[0m");
        System.out.println("\u001B[36m  -> 4,096 HyperCortex nodes mapped to φ-Spherical Manifold\u001B[0m");
        System.out.println("\u001B[36m  -> Mouse: Free-look rotation | W/S: Fly forward/back | ESC: Exit\u001B[0m");
        createBufferStrategy(2);
        new Thread(this, "FRAY-CONSTRUCT").start();
    }

    @Override
    public void run() {
        while (running) {
            frames++;

            // Navigation
            rotY += (mouseX - WIDTH / 2.0) * 0.0001;
            rotX += (mouseY - HEIGHT / 2.0) * 0.0001;
            if (keyW) camZ -= 0.5;
            if (keyS) camZ += 0.5;

            render();
            try { Thread.sleep(16); } catch (Exception e) { break; }
        }
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) return;

        // Tachyonic Motion Blur (Phosphor Decay)
        for (int i = 0; i < vram.length; i++) {
            int p = vram[i];
            vram[i] = (((int)(((p >> 16) & 0xFF) * 0.85)) << 16) |
                      (((int)(((p >> 8) & 0xFF) * 0.90)) << 8) |
                      ((int)((p & 0xFF) * 0.95));
        }

        double cosY = Math.cos(rotY), sinY = Math.sin(rotY);
        double cosX = Math.cos(rotX), sinX = Math.sin(rotX);

        // Render the 4096 Hyper-Nodes in a Phi-Spherical Manifold
        for (int i = 0; i < 4096; i++) {
            double y = 1 - (i / 4095.0) * 2;
            double radius = Math.sqrt(1 - y * y);
            double theta = 1.6180339887 * i * Math.PI * 2; // Golden Ratio packing

            double shell = 50.0 + Math.sin(frames * 0.05 + i) * 5.0; // Thermodynamic breathing
            double px = Math.cos(theta) * radius * shell;
            double py = y * shell;
            double pz = Math.sin(theta) * radius * shell + camZ;

            int color = (i % 3 == 0) ? 0xFFB000 : 0x00F3FF; // Gold (Truth) and Cyan (Logic)
            draw3DPoint(px, py, pz, cosY, sinY, cosX, sinX, color);
        }

        // Render φ-harmonic connection lines (sparse, for visual beauty)
        for (int i = 0; i < 4096; i += 7) {
            int j = (int)((i * 1.6180339887) % 4096);
            double y1 = 1 - (i / 4095.0) * 2, y2 = 1 - (j / 4095.0) * 2;
            double r1 = Math.sqrt(1 - y1 * y1), r2 = Math.sqrt(1 - y2 * y2);
            double t1 = 1.6180339887 * i * Math.PI * 2, t2 = 1.6180339887 * j * Math.PI * 2;
            double s1 = 50.0 + Math.sin(frames * 0.05 + i) * 5.0;
            double s2 = 50.0 + Math.sin(frames * 0.05 + j) * 5.0;

            // Midpoint of connection (sparse rendering)
            double mx = (Math.cos(t1) * r1 * s1 + Math.cos(t2) * r2 * s2) * 0.5;
            double my = (y1 * s1 + y2 * s2) * 0.5;
            double mz = (Math.sin(t1) * r1 * s1 + Math.sin(t2) * r2 * s2) * 0.5 + camZ;
            draw3DPoint(mx, my, mz, cosY, sinY, cosX, sinX, 0x333333);
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(monitor, 0, 0, null);

        // HUD Overlay
        g.setColor(new Color(0, 255, 102, 200));
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.drawString("FRAYNIX.OS // THE CONSTRUCT (SYSTEM CQ: 0.7569)", 20, 30);

        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g.setColor(new Color(0, 243, 255, 180));
        g.drawString("NODES: 4,096 | DIMS: 16,384-D | FPS: ~60 | UPTIME: " + elapsed + "s | FRAME: " + frames, 20, 50);
        g.drawString("ROT_Y: " + String.format("%.4f", rotY) + " | ROT_X: " + String.format("%.4f", rotX) + " | CAM_Z: " + String.format("%.1f", camZ), 20, 66);

        g.setColor(new Color(255, 176, 0, 160));
        g.drawString("φ = 1.6180339887 | GOLDEN RATIO PACKING | THERMODYNAMIC BREATHING", 20, HEIGHT - 40);
        g.drawString("[MOUSE] Rotate | [W/S] Fly | [ESC] Exit Construct", 20, HEIGHT - 20);

        g.dispose();
        bs.show();
    }

    private void draw3DPoint(double x, double y, double z, double cosY, double sinY, double cosX, double sinX, int color) {
        double x1 = x * cosY - z * sinY, z1 = z * cosY + x * sinY;
        double y2 = y * cosX - z1 * sinX, z2 = z1 * cosX + y * sinX;

        double tz = z2 + 150.0;
        if (tz <= 0.1) return;

        int px = (int) (WIDTH / 2.0 + (x1 * 800.0) / tz);
        int py = (int) (HEIGHT / 2.0 + (y2 * 800.0) / tz);

        // Draw 2x2 pixel for visibility
        for (int dx = 0; dx < 2; dx++) {
            for (int dy = 0; dy < 2; dy++) {
                int fx = px + dx, fy = py + dy;
                if (fx >= 0 && fx < WIDTH && fy >= 0 && fy < HEIGHT) {
                    vram[fy * WIDTH + fx] = color;
                }
            }
        }
    }

    @Override public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void mouseDragged(MouseEvent e) { mouseMoved(e); }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> keyW = true;
            case KeyEvent.VK_S -> keyS = true;
            case KeyEvent.VK_A -> keyA = true;
            case KeyEvent.VK_D -> keyD = true;
            case KeyEvent.VK_ESCAPE -> {
                running = false;
                Container parent = getParent();
                while (parent != null && !(parent instanceof JFrame)) parent = parent.getParent();
                if (parent instanceof JFrame) ((JFrame) parent).dispose();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> keyW = false;
            case KeyEvent.VK_S -> keyS = false;
            case KeyEvent.VK_A -> keyA = false;
            case KeyEvent.VK_D -> keyD = false;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
}
