package fraymus.simulations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 🌌 FRAY UNIVERSE - Solar System Gravity Visualizer
 * "The cosmos is within us. We are made of star-stuff."
 * 
 * FEATURES:
 * - Real planetary distances (scaled)
 * - Gravitational force calculations (Newton's Law)
 * - Visual gravity field lines
 * - Interactive zoom and pan
 * - Real-time orbital animation
 * 
 * PHYSICS:
 * F = G * (m1 * m2) / r²
 * G = 6.674 × 10⁻¹¹ N⋅m²/kg²
 */
public class FrayUniverse extends JPanel implements ActionListener {

    // === CONSTANTS ===
    private static final double G = 6.674e-11;  // Gravitational constant
    private static final double AU = 1.496e11;  // 1 Astronomical Unit in meters
    private static final double SCALE = 2.5e-10; // Pixels per meter
    private static final int WIDTH = 1400;
    private static final int HEIGHT = 900;

    // === STATE ===
    private List<CelestialBody> bodies = new ArrayList<>();
    private Timer timer;
    private double time = 0;
    private double zoomLevel = 1.0;
    private int panX = 0, panY = 0;
    private boolean showOrbits = true;
    private boolean showGravityLines = true;
    private boolean showLabels = true;
    private boolean animating = true;
    private CelestialBody selectedBody = null;

    // === COLORS ===
    private static final Color SPACE_BLACK = new Color(5, 5, 15);
    private static final Color GRID_COLOR = new Color(30, 30, 50);
    private static final Color TEXT_COLOR = new Color(200, 200, 220);
    private static final Color GRAVITY_COLOR = new Color(100, 50, 150, 80);

    public FrayUniverse() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(SPACE_BLACK);
        initializeSolarSystem();
        setupControls();
        
        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    private void initializeSolarSystem() {
        // SUN (center of mass)
        bodies.add(new CelestialBody(
            "Sun", 0, 1.989e30, 50, 
            new Color(255, 200, 50), new Color(255, 150, 0)
        ));

        // PLANETS: name, distance(AU), mass(kg), radius(visual), color
        bodies.add(new CelestialBody(
            "Mercury", 0.387 * AU, 3.285e23, 6, 
            new Color(180, 180, 180), new Color(120, 120, 120)
        ));
        
        bodies.add(new CelestialBody(
            "Venus", 0.723 * AU, 4.867e24, 10, 
            new Color(230, 180, 100), new Color(200, 150, 80)
        ));
        
        bodies.add(new CelestialBody(
            "Earth", 1.0 * AU, 5.972e24, 11, 
            new Color(70, 130, 200), new Color(50, 100, 150)
        ));
        
        bodies.add(new CelestialBody(
            "Mars", 1.524 * AU, 6.39e23, 8, 
            new Color(200, 100, 50), new Color(150, 70, 30)
        ));
        
        bodies.add(new CelestialBody(
            "Jupiter", 5.203 * AU, 1.898e27, 30, 
            new Color(200, 150, 100), new Color(180, 130, 80)
        ));
        
        bodies.add(new CelestialBody(
            "Saturn", 9.537 * AU, 5.683e26, 26, 
            new Color(220, 190, 140), new Color(200, 170, 120)
        ));
        
        bodies.add(new CelestialBody(
            "Uranus", 19.191 * AU, 8.681e25, 16, 
            new Color(150, 200, 220), new Color(120, 180, 200)
        ));
        
        bodies.add(new CelestialBody(
            "Neptune", 30.069 * AU, 1.024e26, 15, 
            new Color(70, 100, 200), new Color(50, 80, 180)
        ));
    }

    private void setupControls() {
        setFocusable(true);
        
        addMouseWheelListener(e -> {
            double delta = e.getPreciseWheelRotation();
            zoomLevel *= (delta > 0) ? 0.9 : 1.1;
            zoomLevel = Math.max(0.1, Math.min(5.0, zoomLevel));
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            int lastX, lastY;
            
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
                
                // Check for planet click
                for (CelestialBody body : bodies) {
                    Point2D pos = getScreenPosition(body);
                    double dist = pos.distance(e.getX(), e.getY());
                    if (dist < body.radius * zoomLevel + 10) {
                        selectedBody = body;
                        repaint();
                        return;
                    }
                }
                selectedBody = null;
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                panX += e.getX() - lastX;
                panY += e.getY() - lastY;
                lastX = e.getX();
                lastY = e.getY();
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Handled in mousePressed
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        animating = !animating;
                        break;
                    case KeyEvent.VK_O:
                        showOrbits = !showOrbits;
                        break;
                    case KeyEvent.VK_G:
                        showGravityLines = !showGravityLines;
                        break;
                    case KeyEvent.VK_L:
                        showLabels = !showLabels;
                        break;
                    case KeyEvent.VK_R:
                        zoomLevel = 1.0;
                        panX = 0;
                        panY = 0;
                        break;
                    case KeyEvent.VK_PLUS:
                    case KeyEvent.VK_EQUALS:
                        zoomLevel *= 1.2;
                        break;
                    case KeyEvent.VK_MINUS:
                        zoomLevel *= 0.8;
                        break;
                }
                repaint();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (animating) {
            time += 0.02; // Time step
            updateOrbits();
        }
        repaint();
    }

    private void updateOrbits() {
        CelestialBody sun = bodies.get(0);
        
        for (int i = 1; i < bodies.size(); i++) {
            CelestialBody planet = bodies.get(i);
            
            // Calculate orbital period (Kepler's 3rd law approximation)
            double orbitalPeriod = 2 * Math.PI * Math.sqrt(
                Math.pow(planet.distanceFromSun, 3) / (G * sun.mass)
            );
            
            // Angular position
            double angle = (time * 1e7) / orbitalPeriod * 2 * Math.PI;
            planet.angle = angle;
            
            // Cartesian position
            planet.x = Math.cos(angle) * planet.distanceFromSun;
            planet.y = Math.sin(angle) * planet.distanceFromSun;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int centerX = WIDTH / 2 + panX;
        int centerY = HEIGHT / 2 + panY;

        // Draw starfield
        drawStarfield(g2d);

        // Draw orbital paths
        if (showOrbits) {
            drawOrbits(g2d, centerX, centerY);
        }

        // Draw gravity field lines
        if (showGravityLines) {
            drawGravityField(g2d, centerX, centerY);
        }

        // Draw celestial bodies
        for (CelestialBody body : bodies) {
            drawBody(g2d, body, centerX, centerY);
        }

        // Draw info panel
        drawInfoPanel(g2d);

        // Draw selected body info
        if (selectedBody != null) {
            drawSelectedInfo(g2d);
        }

        // Draw controls help
        drawControls(g2d);
    }

    private void drawStarfield(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 100));
        for (int i = 0; i < 200; i++) {
            int x = (int)(Math.sin(i * 7.3) * WIDTH/2 + WIDTH/2);
            int y = (int)(Math.cos(i * 11.7) * HEIGHT/2 + HEIGHT/2);
            int size = (i % 3 == 0) ? 2 : 1;
            g2d.fillOval(x, y, size, size);
        }
    }

    private void drawOrbits(Graphics2D g2d, int centerX, int centerY) {
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5, 5}, 0));
        
        for (int i = 1; i < bodies.size(); i++) {
            CelestialBody body = bodies.get(i);
            int orbitRadius = (int)(body.distanceFromSun * SCALE * zoomLevel);
            
            g2d.setColor(new Color(body.color.getRed(), body.color.getGreen(), body.color.getBlue(), 50));
            g2d.drawOval(centerX - orbitRadius, centerY - orbitRadius, orbitRadius * 2, orbitRadius * 2);
        }
        
        g2d.setStroke(new BasicStroke(1));
    }

    private void drawGravityField(Graphics2D g2d, int centerX, int centerY) {
        CelestialBody sun = bodies.get(0);
        
        for (int i = 1; i < bodies.size(); i++) {
            CelestialBody planet = bodies.get(i);
            Point2D sunPos = new Point2D.Double(centerX, centerY);
            Point2D planetPos = getScreenPosition(planet);
            
            // Calculate gravitational force
            double force = calculateGravity(sun, planet);
            
            // Draw gradient line representing gravity
            GradientPaint gradient = new GradientPaint(
                (float)sunPos.getX(), (float)sunPos.getY(), new Color(255, 200, 50, 30),
                (float)planetPos.getX(), (float)planetPos.getY(), new Color(planet.color.getRed(), planet.color.getGreen(), planet.color.getBlue(), 30)
            );
            
            g2d.setPaint(gradient);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine((int)sunPos.getX(), (int)sunPos.getY(), (int)planetPos.getX(), (int)planetPos.getY());
        }
    }

    private void drawBody(Graphics2D g2d, CelestialBody body, int centerX, int centerY) {
        int screenX, screenY;
        
        if (body.name.equals("Sun")) {
            screenX = centerX;
            screenY = centerY;
            
            // Sun glow effect
            for (int i = 5; i > 0; i--) {
                int glowRadius = (int)(body.radius * zoomLevel) + i * 8;
                g2d.setColor(new Color(255, 200, 50, 20));
                g2d.fillOval(screenX - glowRadius, screenY - glowRadius, glowRadius * 2, glowRadius * 2);
            }
        } else {
            screenX = centerX + (int)(body.x * SCALE * zoomLevel);
            screenY = centerY + (int)(body.y * SCALE * zoomLevel);
        }

        int radius = (int)(body.radius * zoomLevel);
        radius = Math.max(3, radius);

        // Planet shadow (3D effect)
        GradientPaint gradient = new GradientPaint(
            screenX - radius/2, screenY - radius/2, body.color,
            screenX + radius/2, screenY + radius/2, body.colorDark
        );
        g2d.setPaint(gradient);
        g2d.fillOval(screenX - radius, screenY - radius, radius * 2, radius * 2);

        // Saturn's rings
        if (body.name.equals("Saturn")) {
            g2d.setColor(new Color(200, 180, 140, 150));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(screenX - radius * 2, screenY - radius / 3, radius * 4, radius * 2 / 3);
        }

        // Highlight
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.fillOval(screenX - radius/2, screenY - radius/2, radius/2, radius/2);

        // Selection indicator
        if (body == selectedBody) {
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(screenX - radius - 5, screenY - radius - 5, (radius + 5) * 2, (radius + 5) * 2);
        }

        // Label
        if (showLabels && zoomLevel > 0.3) {
            g2d.setColor(TEXT_COLOR);
            g2d.setFont(new Font("Consolas", Font.BOLD, 12));
            g2d.drawString(body.name, screenX + radius + 5, screenY + 5);
        }
    }

    private void drawInfoPanel(Graphics2D g2d) {
        // Title
        g2d.setColor(new Color(20, 20, 30, 200));
        g2d.fillRoundRect(10, 10, 300, 140, 10, 10);
        
        g2d.setColor(new Color(100, 150, 255));
        g2d.setFont(new Font("Consolas", Font.BOLD, 18));
        g2d.drawString("🌌 FRAY UNIVERSE", 20, 35);
        
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Consolas", Font.PLAIN, 12));
        g2d.drawString("Solar System Gravity Model", 20, 55);
        g2d.drawString(String.format("Zoom: %.1fx", zoomLevel), 20, 75);
        g2d.drawString(String.format("Time: %.2f years", time * 1e7 / (365.25 * 24 * 3600)), 20, 95);
        g2d.drawString(animating ? "▶ RUNNING" : "⏸ PAUSED", 20, 115);
        g2d.drawString("G = 6.674×10⁻¹¹ N⋅m²/kg²", 20, 135);
    }

    private void drawSelectedInfo(Graphics2D g2d) {
        CelestialBody sun = bodies.get(0);
        int panelWidth = 320;
        
        g2d.setColor(new Color(20, 20, 30, 220));
        g2d.fillRoundRect(WIDTH - panelWidth - 10, 10, panelWidth, 200, 10, 10);
        
        g2d.setColor(selectedBody.color);
        g2d.setFont(new Font("Consolas", Font.BOLD, 16));
        g2d.drawString("◉ " + selectedBody.name.toUpperCase(), WIDTH - panelWidth, 35);
        
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        int y = 55;
        int lineHeight = 20;
        
        // Distance from Sun
        double distAU = selectedBody.distanceFromSun / AU;
        g2d.drawString(String.format("Distance: %.3f AU", distAU), WIDTH - panelWidth, y);
        g2d.drawString(String.format("         (%.2e km)", selectedBody.distanceFromSun / 1000), WIDTH - panelWidth, y + 15);
        y += lineHeight * 2;
        
        // Mass
        g2d.drawString(String.format("Mass: %.3e kg", selectedBody.mass), WIDTH - panelWidth, y);
        y += lineHeight;
        
        // Gravitational force to Sun (if not Sun)
        if (!selectedBody.name.equals("Sun")) {
            double force = calculateGravity(sun, selectedBody);
            g2d.drawString(String.format("Gravity to Sun: %.3e N", force), WIDTH - panelWidth, y);
            y += lineHeight;
            
            // Orbital velocity
            double velocity = Math.sqrt(G * sun.mass / selectedBody.distanceFromSun);
            g2d.drawString(String.format("Orbital Velocity: %.2f km/s", velocity / 1000), WIDTH - panelWidth, y);
            y += lineHeight;
            
            // Orbital period
            double period = 2 * Math.PI * selectedBody.distanceFromSun / velocity;
            double periodYears = period / (365.25 * 24 * 3600);
            g2d.drawString(String.format("Orbital Period: %.2f years", periodYears), WIDTH - panelWidth, y);
            y += lineHeight;
            
            // Escape velocity
            double escapeV = Math.sqrt(2 * G * selectedBody.mass / (selectedBody.radius * 1e6));
            g2d.drawString(String.format("Escape Velocity: %.2f km/s", escapeV / 1000), WIDTH - panelWidth, y);
        }
    }

    private void drawControls(Graphics2D g2d) {
        g2d.setColor(new Color(20, 20, 30, 180));
        g2d.fillRoundRect(10, HEIGHT - 110, 280, 100, 10, 10);
        
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Consolas", Font.PLAIN, 11));
        
        int y = HEIGHT - 90;
        g2d.drawString("[SPACE] Play/Pause    [R] Reset View", 20, y);
        g2d.drawString("[O] Toggle Orbits     [G] Toggle Gravity", 20, y + 18);
        g2d.drawString("[L] Toggle Labels     [+/-] Zoom", 20, y + 36);
        g2d.drawString("[SCROLL] Zoom         [DRAG] Pan", 20, y + 54);
        g2d.drawString("[CLICK] Select Planet", 20, y + 72);
    }

    private Point2D getScreenPosition(CelestialBody body) {
        int centerX = WIDTH / 2 + panX;
        int centerY = HEIGHT / 2 + panY;
        
        if (body.name.equals("Sun")) {
            return new Point2D.Double(centerX, centerY);
        }
        
        return new Point2D.Double(
            centerX + body.x * SCALE * zoomLevel,
            centerY + body.y * SCALE * zoomLevel
        );
    }

    private double calculateGravity(CelestialBody a, CelestialBody b) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance == 0) return 0;
        
        return G * a.mass * b.mass / (distance * distance);
    }

    // === CELESTIAL BODY CLASS ===
    static class CelestialBody {
        String name;
        double x, y;
        double distanceFromSun;
        double mass;
        int radius;
        Color color, colorDark;
        double angle = 0;

        CelestialBody(String name, double distance, double mass, int radius, Color color, Color colorDark) {
            this.name = name;
            this.distanceFromSun = distance;
            this.mass = mass;
            this.radius = radius;
            this.color = color;
            this.colorDark = colorDark;
            this.x = distance;
            this.y = 0;
        }
    }

    // === MAIN ===
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("🌌 FRAY UNIVERSE - Solar System Gravity Visualizer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new FrayUniverse());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            System.out.println("╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║     🌌 FRAY UNIVERSE - SOLAR SYSTEM GRAVITY MODEL         ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║  PHYSICS: F = G × (m₁ × m₂) / r²                          ║");
            System.out.println("║  G = 6.674 × 10⁻¹¹ N⋅m²/kg²                               ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║  CONTROLS:                                                ║");
            System.out.println("║  [SPACE] Play/Pause     [O] Toggle Orbits                 ║");
            System.out.println("║  [G] Toggle Gravity     [L] Toggle Labels                 ║");
            System.out.println("║  [R] Reset View         [+/-] Zoom                        ║");
            System.out.println("║  [SCROLL] Zoom          [DRAG] Pan                        ║");
            System.out.println("║  [CLICK] Select Planet for Details                        ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
        });
    }
}
