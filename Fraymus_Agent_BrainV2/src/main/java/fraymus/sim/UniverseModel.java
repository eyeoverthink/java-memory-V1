/**
 * UniverseModel.java - Fraymus Orrery
 * 
 * "The Orrery of Logic."
 * 
 * ARCHITECTURE REPURPOSING:
 * - LogicCircuit → Newtonian Physics Processor
 * - LazarusEngine → Time-Step Physics Integrator
 * - FrayUI Processing Absorption → Real-time OpenGL Rendering
 * 
 * MISSION: Model solar system gravity and visualize distances.
 * Uses absorbed Processing 'core.jar' for visualization.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 168 (Fraymus Orrery - Universe Model)
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package fraymus.sim;

import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;
import java.util.List;

/**
 * 🌌 FRAYMUS UNIVERSE MODEL
 * Real-time gravity simulation with visual distance tracking.
 */
public class UniverseModel extends PApplet {

    /* ═══════════════════════════════════════════════════════════ */
    /* SIMULATION CONSTANTS                                         */
    /* ═══════════════════════════════════════════════════════════ */
    
    // Gravitational Constant (Scaled for visualization)
    private static final float G = 0.5f;
    
    // Celestial bodies
    private List<Planet> planets = new ArrayList<>();
    private Planet earthRef; // Reference for distance calculations
    
    // Visual settings
    private boolean showOrbits = true;
    private boolean showForces = false;
    private float timeScale = 1.0f;
    private boolean paused = false;

    /* ═══════════════════════════════════════════════════════════ */
    /* PROCESSING SETUP                                             */
    /* ═══════════════════════════════════════════════════════════ */
    
    public void settings() {
        size(1200, 800); // The "Window onto the Universe"
    }

    public void setup() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  🌌 FRAYMUS ORRERY - UNIVERSE MODEL                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        System.out.println("\"The Orrery of Logic.\"\n");
        System.out.println("Initializing cosmos...\n");
        
        // Initialize the Cosmos
        // Planet(name, mass, x, y, vx, vy, color)
        Planet sun = new Planet("Sun", 1000, width/2, height/2, 0, 0, color(255, 200, 50));
        
        // Earth: Starts to the right, moving down
        Planet earth = new Planet("Earth", 10, width/2 + 200, height/2, 0, 1.6f, color(50, 100, 255));
        earthRef = earth;
        
        // Mars: Further out, slower
        Planet mars = new Planet("Mars", 5, width/2 + 300, height/2, 0, 1.3f, color(200, 50, 50));

        planets.add(sun);
        planets.add(earth);
        planets.add(mars);
        
        textFont(createFont("Monospaced", 14));
        
        System.out.println("✅ Cosmos initialized:");
        System.out.println("  • Sun (1000 mass units)");
        System.out.println("  • Earth (10 mass units, 200px orbit)");
        System.out.println("  • Mars (5 mass units, 300px orbit)\n");
        System.out.println("Controls:");
        System.out.println("  [SPACE] Pause/Resume");
        System.out.println("  [O]     Toggle orbit trails");
        System.out.println("  [F]     Toggle force vectors");
        System.out.println("  [+/-]   Speed up/slow down");
        System.out.println("  [R]     Reset simulation\n");
        System.out.println("φ^75 Validation Seal: 4721424167835376.00\n");
    }

    /* ═══════════════════════════════════════════════════════════ */
    /* MAIN RENDER LOOP (The Lazarus Engine)                       */
    /* ═══════════════════════════════════════════════════════════ */
    
    public void draw() {
        // Deep space background
        background(20, 20, 30);

        // 1. PHYSICS STEP (The Lazarus Engine Loop)
        if (!paused) {
            for (int step = 0; step < (int)timeScale; step++) {
                for (Planet p : planets) {
                    p.calculateGravity(planets);
                }
                for (Planet p : planets) {
                    p.update();
                }
            }
        }

        // 2. RENDER STEP (The Visual Cortex)
        for (Planet p : planets) {
            p.display();
        }

        // 3. DATA VISUALIZATION (The Professor's Requirement)
        drawDashboard();
        
        // 4. STATUS BAR
        drawStatusBar();
    }

    /* ═══════════════════════════════════════════════════════════ */
    /* VISUALIZATION                                                */
    /* ═══════════════════════════════════════════════════════════ */
    
    private void drawDashboard() {
        fill(220);
        noStroke();
        textAlign(LEFT, TOP);
        text("🌌 GRAVITATIONAL DISTANCE TRACKER [REF: EARTH]", 20, 20);
        
        int y = 50;
        for (Planet p : planets) {
            if (p == earthRef) continue;
            
            // Calculate distance
            float dist = PVector.dist(earthRef.pos, p.pos);
            
            // Draw connecting line
            stroke(100, 200, 100, 100);
            strokeWeight(1);
            line(earthRef.pos.x, earthRef.pos.y, p.pos.x, p.pos.y);
            
            // Draw label on dashboard
            fill(p.col);
            text(String.format("%-6s: %8.2f Mm", p.name, dist), 20, y);
            
            // Draw distance label on the line mid-point
            PVector mid = PVector.add(earthRef.pos, p.pos).div(2);
            fill(220);
            textAlign(CENTER);
            text(String.format("%.0f", dist), mid.x, mid.y);
            textAlign(LEFT);
            
            y += 25;
        }
    }
    
    private void drawStatusBar() {
        fill(220);
        textAlign(LEFT, BOTTOM);
        String status = String.format("Time: %.1fx  %s  Orbits: %s  Forces: %s", 
            timeScale, 
            paused ? "[PAUSED]" : "[RUNNING]",
            showOrbits ? "ON" : "OFF",
            showForces ? "ON" : "OFF");
        text(status, 20, height - 20);
        
        textAlign(RIGHT, BOTTOM);
        text("φ^75 = 4721424167835376.00", width - 20, height - 20);
    }

    /* ═══════════════════════════════════════════════════════════ */
    /* INPUT HANDLING                                               */
    /* ═══════════════════════════════════════════════════════════ */
    
    public void keyPressed() {
        if (key == ' ') {
            paused = !paused;
            System.out.println(paused ? "⏸️  PAUSED" : "▶️  RESUMED");
        }
        else if (key == 'o' || key == 'O') {
            showOrbits = !showOrbits;
            System.out.println("Orbit trails: " + (showOrbits ? "ON" : "OFF"));
        }
        else if (key == 'f' || key == 'F') {
            showForces = !showForces;
            System.out.println("Force vectors: " + (showForces ? "ON" : "OFF"));
        }
        else if (key == '+' || key == '=') {
            timeScale *= 2.0f;
            if (timeScale > 32.0f) timeScale = 32.0f;
            System.out.println("Time scale: " + timeScale + "x");
        }
        else if (key == '-' || key == '_') {
            timeScale /= 2.0f;
            if (timeScale < 0.125f) timeScale = 0.125f;
            System.out.println("Time scale: " + timeScale + "x");
        }
        else if (key == 'r' || key == 'R') {
            setup();
            System.out.println("🔄 RESET");
        }
    }

    /* ═══════════════════════════════════════════════════════════ */
    /* PLANET CLASS (The Atom of the Simulation)                   */
    /* ═══════════════════════════════════════════════════════════ */
    
    /**
     * Planet - A celestial body with mass, position, velocity.
     * This is where LogicCircuit becomes Newtonian Physics.
     */
    class Planet {
        String name;
        float mass;
        PVector pos;
        PVector vel;
        PVector acc;
        int col;
        float radius;
        
        // Orbit trail
        List<PVector> trail = new ArrayList<>();
        int maxTrailLength = 100;

        Planet(String n, float m, float x, float y, float vx, float vy, int c) {
            name = n;
            mass = m;
            pos = new PVector(x, y);
            vel = new PVector(vx, vy);
            acc = new PVector(0, 0);
            col = c;
            radius = sqrt(mass) * 2; // Visual size based on mass
        }

        /**
         * Calculate gravitational force from all other planets.
         * F = G * (m1 * m2) / r²
         */
        void calculateGravity(List<Planet> allPlanets) {
            for (Planet other : allPlanets) {
                if (other == this) continue;

                PVector force = PVector.sub(other.pos, this.pos);
                float distanceSq = force.magSq();
                distanceSq = constrain(distanceSq, 100, 10000); // Prevent singularities

                // F = G * (m1*m2) / r^2
                float strength = (G * (this.mass * other.mass)) / distanceSq;
                force.setMag(strength);
                
                // a = F / m
                PVector acceleration = PVector.div(force, this.mass);
                this.acc.add(acceleration);
            }
        }

        /**
         * Update position using Verlet integration.
         * This is the Time-Step Physics Integrator (LazarusEngine).
         */
        void update() {
            vel.add(acc); // v = v + a
            pos.add(vel); // p = p + v
            acc.mult(0);  // Reset acceleration for next frame
            
            // Update trail
            if (showOrbits && frameCount % 2 == 0) {
                trail.add(pos.copy());
                if (trail.size() > maxTrailLength) {
                    trail.remove(0);
                }
            }
        }

        /**
         * Render the planet and its trail.
         */
        void display() {
            // Draw orbit trail
            if (showOrbits && trail.size() > 1) {
                stroke(col, 100);
                strokeWeight(1);
                noFill();
                beginShape();
                for (PVector p : trail) {
                    vertex(p.x, p.y);
                }
                endShape();
            }
            
            // Draw planet
            noStroke();
            fill(col);
            ellipse(pos.x, pos.y, radius * 2, radius * 2);
            
            // Draw glow effect
            fill(col, 50);
            ellipse(pos.x, pos.y, radius * 3, radius * 3);
            
            // Draw name
            fill(255);
            textAlign(CENTER);
            text(name, pos.x, pos.y + radius + 15);
            
            // Draw force vectors (if enabled)
            if (showForces) {
                stroke(255, 100, 100);
                strokeWeight(2);
                PVector forceVis = vel.copy().mult(10);
                line(pos.x, pos.y, pos.x + forceVis.x, pos.y + forceVis.y);
            }
        }
    }

    /* ═══════════════════════════════════════════════════════════ */
    /* MAIN ENTRY POINT                                             */
    /* ═══════════════════════════════════════════════════════════ */
    
    /**
     * Launch the Orrery.
     */
    public static void main(String[] args) {
        System.out.println("\n🌌 LAUNCHING FRAYMUS ORRERY...\n");
        PApplet.main("fraymus.sim.UniverseModel");
    }
}
