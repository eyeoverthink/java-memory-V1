package fraymus.sim;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 🌌 FRAYMUS ORRERY - STANDALONE VERSION
 * "The Orrery of Logic - No Dependencies Required"
 * 
 * This is a pure Java Swing version that doesn't require Processing.
 * Same physics, same visuals, zero external dependencies.
 * 
 * PHYSICS: F = G × (m₁ × m₂) / r²
 * 
 * ARCHITECTURE:
 * - LogicCircuit → Newtonian Physics Processor
 * - LazarusEngine → Time-Step Physics Integrator
 * - Swing → Hardware-accelerated rendering
 */
public class OrreryStandalone extends JPanel implements ActionListener {

    // ═══════════════════════════════════════════════════════════════════
    // SIMULATION CONSTANTS
    // ═══════════════════════════════════════════════════════════════════
    private static final float G = 0.5f;  // Gravitational constant (scaled)
    private static final int WIDTH = 1400;
    private static final int HEIGHT = 900;
    
    private List<Planet> planets = new ArrayList<>();
    private List<Point2D.Float> trails = new ArrayList<>();
    private List<Integer> trailColors = new ArrayList<>();
    private Planet earthRef;
    
    private Timer timer;
    private boolean paused = false;
    private boolean showTrails = true;
    private boolean showForceVectors = true;
    private float timeScale = 1.0f;
    private int frameCount = 0;

    // ═══════════════════════════════════════════════════════════════════
    // COLORS
    // ═══════════════════════════════════════════════════════════════════
    private static final Color SPACE = new Color(15, 15, 25);
    private static final Color SUN_COLOR = new Color(255, 200, 50);
    private static final Color MERCURY_COLOR = new Color(180, 180, 180);
    private static final Color VENUS_COLOR = new Color(230, 180, 100);
    private static final Color EARTH_COLOR = new Color(50, 100, 255);
    private static final Color MARS_COLOR = new Color(200, 80, 50);
    private static final Color JUPITER_COLOR = new Color(200, 150, 100);

    public OrreryStandalone() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(SPACE);
        setFocusable(true);
        
        initializeSolarSystem();
        setupInput();
        
        timer = new Timer(16, this);  // ~60 FPS
        timer.start();
        
        printBanner();
    }

    private void initializeSolarSystem() {
        planets.clear();
        trails.clear();
        trailColors.clear();
        
        int cx = WIDTH / 2;
        int cy = HEIGHT / 2;
        
        // SUN - Massive, stationary
        planets.add(new Planet("☀ Sun", 1000, cx, cy, 0, 0, SUN_COLOR, true));
        
        // MERCURY
        planets.add(new Planet("Mercury", 2, cx + 80, cy, 0, 2.8f, MERCURY_COLOR, false));
        
        // VENUS
        planets.add(new Planet("Venus", 6, cx + 130, cy, 0, 2.2f, VENUS_COLOR, false));
        
        // EARTH (Reference)
        Planet earth = new Planet("🌍 Earth", 10, cx + 200, cy, 0, 1.8f, EARTH_COLOR, false);
        earthRef = earth;
        planets.add(earth);
        
        // MARS
        planets.add(new Planet("Mars", 5, cx + 280, cy, 0, 1.5f, MARS_COLOR, false));
        
        // JUPITER
        planets.add(new Planet("Jupiter", 80, cx + 400, cy, 0, 1.1f, JUPITER_COLOR, false));
    }

    private void setupInput() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        paused = !paused;
                        break;
                    case KeyEvent.VK_T:
                        showTrails = !showTrails;
                        if (!showTrails) {
                            trails.clear();
                            trailColors.clear();
                        }
                        break;
                    case KeyEvent.VK_F:
                        showForceVectors = !showForceVectors;
                        break;
                    case KeyEvent.VK_R:
                        initializeSolarSystem();
                        break;
                    case KeyEvent.VK_EQUALS:
                    case KeyEvent.VK_PLUS:
                        timeScale = Math.min(timeScale + 0.5f, 5.0f);
                        break;
                    case KeyEvent.VK_MINUS:
                        timeScale = Math.max(timeScale - 0.5f, 0.5f);
                        break;
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // Add moon at click location
                    float dx = WIDTH/2 - e.getX();
                    float dy = HEIGHT/2 - e.getY();
                    float dist = (float) Math.sqrt(dx*dx + dy*dy);
                    float orbitalV = (float) Math.sqrt(G * 1000 / dist);
                    
                    float vx = -dy / dist * orbitalV;
                    float vy = dx / dist * orbitalV;
                    
                    planets.add(new Planet("Moon" + (planets.size() - 5), 1, 
                                           e.getX(), e.getY(), vx, vy, 
                                           new Color(180, 180, 200), false));
                }
            }
        });
    }

    private void printBanner() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      🌌 FRAYMUS ORRERY - STANDALONE VERSION               ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║  PHYSICS: F = G × (m₁ × m₂) / r²                          ║");
        System.out.println("║  ENGINE:  Time-Step Verlet Integration                    ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║  CONTROLS:                                                ║");
        System.out.println("║  [SPACE] Pause/Resume    [T] Toggle Trails                ║");
        System.out.println("║  [F] Toggle Force Vectors    [R] Reset                    ║");
        System.out.println("║  [+/-] Time Scale    [CLICK] Add Moon                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused) {
            // Physics integration
            for (int t = 0; t < (int) timeScale; t++) {
                for (Planet p : planets) {
                    p.calculateGravity(planets);
                }
                for (Planet p : planets) {
                    p.update();
                }
            }
            
            // Record trails
            if (showTrails && frameCount % 3 == 0) {
                for (Planet p : planets) {
                    if (!p.isSun) {
                        trails.add(new Point2D.Float(p.x, p.y));
                        trailColors.add(p.color.getRGB());
                    }
                }
            }
            
            // Limit trail length
            while (trails.size() > 3000) {
                trails.remove(0);
                trailColors.remove(0);
            }
        }
        
        frameCount++;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawStarfield(g2d);
        
        if (showTrails) {
            drawTrails(g2d);
        }
        
        drawGravityLines(g2d);
        
        if (showForceVectors) {
            drawForceVectors(g2d);
        }
        
        for (Planet p : planets) {
            p.draw(g2d);
        }
        
        drawDashboard(g2d);
        drawControls(g2d);
    }

    private void drawStarfield(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 150));
        for (int i = 0; i < 150; i++) {
            int x = (int) ((Math.sin(i * 7.3) + 1) * WIDTH / 2) % WIDTH;
            int y = (int) ((Math.cos(i * 11.7) + 1) * HEIGHT / 2) % HEIGHT;
            int size = (i % 3 == 0) ? 2 : 1;
            g2d.fillOval(x, y, size, size);
        }
    }

    private void drawTrails(Graphics2D g2d) {
        for (int i = 0; i < trails.size(); i++) {
            Point2D.Float p = trails.get(i);
            g2d.setColor(new Color(trailColors.get(i) | 0x33000000, true));
            g2d.fillRect((int) p.x, (int) p.y, 1, 1);
        }
    }

    private void drawGravityLines(Graphics2D g2d) {
        if (earthRef == null) return;
        
        g2d.setStroke(new BasicStroke(1));
        
        for (Planet p : planets) {
            if (p == earthRef) continue;
            
            float dist = distance(earthRef.x, earthRef.y, p.x, p.y);
            int alpha = (int) map(dist, 0, 500, 150, 30);
            alpha = Math.max(10, Math.min(150, alpha));
            
            g2d.setColor(new Color(100, 200, 100, alpha));
            g2d.drawLine((int) earthRef.x, (int) earthRef.y, (int) p.x, (int) p.y);
            
            // Distance label
            float midX = (earthRef.x + p.x) / 2;
            float midY = (earthRef.y + p.y) / 2;
            g2d.setColor(new Color(200, 255, 200));
            g2d.setFont(new Font("Consolas", Font.PLAIN, 11));
            g2d.drawString(String.format("%.0f Mm", dist), midX, midY - 8);
        }
    }

    private void drawForceVectors(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(2));
        
        for (Planet p : planets) {
            if (p.isSun) continue;
            
            // Velocity vector (cyan)
            g2d.setColor(new Color(0, 255, 255, 150));
            g2d.drawLine((int) p.x, (int) p.y, 
                        (int) (p.x + p.vx * 20), (int) (p.y + p.vy * 20));
            
            // Acceleration vector (magenta)
            g2d.setColor(new Color(255, 0, 255, 150));
            g2d.drawLine((int) p.x, (int) p.y,
                        (int) (p.x + p.lastAx * 500), (int) (p.y + p.lastAy * 500));
        }
    }

    private void drawDashboard(Graphics2D g2d) {
        // Panel
        g2d.setColor(new Color(20, 20, 30, 220));
        g2d.fillRoundRect(10, 10, 320, 220, 8, 8);
        
        // Title
        g2d.setColor(new Color(100, 200, 255));
        g2d.setFont(new Font("Consolas", Font.BOLD, 16));
        g2d.drawString("🌌 GRAVITATIONAL DISTANCE TRACKER", 20, 30);
        
        g2d.setColor(new Color(150, 150, 150));
        g2d.setFont(new Font("Consolas", Font.PLAIN, 11));
        g2d.drawString("Reference: Earth", 20, 48);
        
        g2d.setColor(new Color(100, 100, 100));
        g2d.drawLine(20, 58, 310, 58);
        
        // Headers
        g2d.setColor(new Color(180, 180, 180));
        g2d.setFont(new Font("Consolas", Font.PLAIN, 13));
        g2d.drawString("BODY", 25, 75);
        g2d.drawString("DISTANCE", 130, 75);
        g2d.drawString("GRAVITY (N)", 220, 75);
        
        int y = 95;
        for (Planet p : planets) {
            if (p == earthRef) continue;
            
            float dist = distance(earthRef.x, earthRef.y, p.x, p.y);
            float force = (G * earthRef.mass * p.mass) / (dist * dist);
            
            g2d.setColor(p.color);
            g2d.drawString(p.name, 25, y);
            
            g2d.setColor(new Color(200, 255, 200));
            g2d.drawString(String.format("%8.1f Mm", dist), 130, y);
            
            g2d.setColor(new Color(255, 200, 100));
            g2d.drawString(String.format("%.4f", force), 220, y);
            
            y += 20;
        }
        
        // Formula
        g2d.setColor(new Color(100, 100, 100));
        g2d.setFont(new Font("Consolas", Font.PLAIN, 10));
        g2d.drawString("F = G × (m₁ × m₂) / r²", 20, 205);
        g2d.drawString("G = " + G + " (scaled)", 180, 205);
    }

    private void drawControls(Graphics2D g2d) {
        g2d.setColor(new Color(20, 20, 30, 180));
        g2d.fillRoundRect(WIDTH - 260, HEIGHT - 90, 250, 80, 8, 8);
        
        g2d.setColor(new Color(180, 180, 180));
        g2d.setFont(new Font("Consolas", Font.PLAIN, 11));
        
        int y = HEIGHT - 75;
        g2d.drawString("[SPACE] " + (paused ? "Resume" : "Pause"), WIDTH - 250, y);
        g2d.drawString("[T] Trails: " + (showTrails ? "ON" : "OFF"), WIDTH - 120, y);
        y += 18;
        g2d.drawString("[F] Forces: " + (showForceVectors ? "ON" : "OFF"), WIDTH - 250, y);
        g2d.drawString("[R] Reset", WIDTH - 120, y);
        y += 18;
        g2d.drawString("[+/-] Time: " + timeScale + "x", WIDTH - 250, y);
        g2d.drawString("[CLICK] Add Body", WIDTH - 120, y);
        y += 18;
        g2d.drawString("FPS: ~60", WIDTH - 250, y);
    }

    // ═══════════════════════════════════════════════════════════════════
    // UTILITIES
    // ═══════════════════════════════════════════════════════════════════
    
    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    private float map(float value, float start1, float stop1, float start2, float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

    // ═══════════════════════════════════════════════════════════════════
    // PLANET CLASS
    // ═══════════════════════════════════════════════════════════════════
    
    class Planet {
        String name;
        float mass;
        float x, y;
        float vx, vy;
        float ax, ay;
        float lastAx, lastAy;
        Color color;
        float radius;
        boolean isSun;

        Planet(String n, float m, float x, float y, float vx, float vy, Color c, boolean sun) {
            this.name = n;
            this.mass = m;
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.ax = 0;
            this.ay = 0;
            this.color = c;
            this.radius = (float) Math.sqrt(m) * 2;
            this.isSun = sun;
        }

        void calculateGravity(List<Planet> all) {
            ax = 0;
            ay = 0;
            
            for (Planet other : all) {
                if (other == this) continue;
                
                float dx = other.x - this.x;
                float dy = other.y - this.y;
                float distSq = dx * dx + dy * dy;
                distSq = Math.max(100, Math.min(100000, distSq));
                
                float dist = (float) Math.sqrt(distSq);
                float force = (G * this.mass * other.mass) / distSq;
                
                ax += (force * dx / dist) / this.mass;
                ay += (force * dy / dist) / this.mass;
            }
            
            lastAx = ax;
            lastAy = ay;
        }

        void update() {
            if (isSun) return;
            
            vx += ax;
            vy += ay;
            x += vx;
            y += vy;
        }

        void draw(Graphics2D g2d) {
            // Glow for Sun
            if (isSun) {
                for (int i = 5; i > 0; i--) {
                    g2d.setColor(new Color(255, 200, 50, 30));
                    int glowR = (int) (radius + i * 8);
                    g2d.fillOval((int) x - glowR, (int) y - glowR, glowR * 2, glowR * 2);
                }
            }
            
            // Body
            g2d.setColor(color);
            int r = (int) radius;
            g2d.fillOval((int) x - r, (int) y - r, r * 2, r * 2);
            
            // Highlight
            g2d.setColor(new Color(255, 255, 255, 100));
            int hr = r / 3;
            g2d.fillOval((int) x - r/2, (int) y - r/2, hr, hr);
            
            // Label
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Consolas", Font.PLAIN, 12));
            FontMetrics fm = g2d.getFontMetrics();
            int tw = fm.stringWidth(name);
            g2d.drawString(name, (int) x - tw/2, (int) y + r + 15);
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN
    // ═══════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("🌌 FRAYMUS ORRERY - Gravitational Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new OrreryStandalone());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}
