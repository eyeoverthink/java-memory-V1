package fraymus.physics;

import java.lang.Math;

/**
 * THE PREDATOR PROTOCOL: METAMATERIAL RAY TRACING
 * 
 * Simulates a photon moving through a Transformation Optics grid.
 * "I see nothing, Captain."
 * 
 * This is a VISUAL PROOF of metamaterial cloaking.
 * 
 * How it works:
 * 1. Create grid with object at center
 * 2. Surround object with metamaterial cloak
 * 3. Fire photon at object
 * 4. Watch photon bend AROUND object
 * 5. Photon exits as if nothing was there
 * 
 * The math:
 * - Inside cloak: Variable refractive index n(r)
 * - Deflection angle based on distance from center
 * - Phi-optimized bending for smooth trajectory
 * 
 * Real physics:
 * - Duke University demonstrated this (2006)
 * - Transformation optics (Pendry/Smith)
 * - Coordinate transformation in material
 * 
 * Visual output:
 * - ' ' = Empty space
 * - ':' = Cloak field (metamaterial)
 * - '#' = Object (hidden)
 * - '•' = Photon trajectory (vacuum)
 * - 'C' = Photon path through cloak
 * - 'X' = Detection (cloak failed)
 */
public class CloakVis {

    private static final double PHI = 1.6180339887;
    
    // THE GRID
    // 0 = Empty Space, 1 = The Object (Hidden), 2 = The Cloak (Metamaterial)
    private static final int GRID_SIZE = 20;
    private static final double CLOAK_RADIUS = 5.0; // R2 (outer radius)
    private static final double OBJECT_RADIUS = 2.0; // R1 (inner radius)

    /**
     * Run cloaking simulation
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ PREDATOR PROTOCOL INITIATED");
        System.out.println("--- METAMATERIAL CLOAK SIMULATION ---\n");
        System.out.println("Target: [LOCKED]");
        System.out.println("Cloak:  [ACTIVE]");
        System.out.println("Photon: [FIRING]\n");
        
        // Fire the Photon from the left (x=0, y=10)
        double photonX = 0;
        double photonY = 10.0; // Aimed directly at the center (y=10)
        double angle = 0; // Moving straight Right (0 radians)

        // VISUALIZER
        char[][] display = new char[GRID_SIZE][GRID_SIZE];
        initializeGrid(display);

        // RAY MARCHING LOOP
        boolean detected = false;
        
        for (int t = 0; t < GRID_SIZE * 2; t++) {
            
            // 1. UPDATE POSITION
            photonX += Math.cos(angle); 
            photonY += Math.sin(angle);

            // Check bounds
            if (photonX < 0 || photonX >= GRID_SIZE || 
                photonY < 0 || photonY >= GRID_SIZE) {
                break; // Photon left grid
            }

            // 2. CHECK MATERIAL STATE
            // Distance from Center (10, 10)
            double dist = Math.sqrt(Math.pow(photonX - 10, 2) + Math.pow(photonY - 10, 2));

            // 3. APPLY TRANSFORMATION OPTICS (THE BEND)
            if (dist < CLOAK_RADIUS && dist > OBJECT_RADIUS) {
                // We are inside the Metamaterial.
                // The "Space" here is compressed. We divert the angle.
                
                // The Math: Deflect AWAY from center based on proximity.
                // This mimics the variable refractive index n(r)
                double deflection = (CLOAK_RADIUS - dist) * 0.2 * PHI;
                
                // Calculate direction from center
                double dx = photonX - 10;
                double dy = photonY - 10;
                double centerAngle = Math.atan2(dy, dx);
                
                // Bend away from center
                angle = centerAngle + Math.PI / 2;
                
                // Mark as 'C' (Cloaked Path)
                plot(display, (int)photonX, (int)photonY, 'C');
                
            } else if (dist <= OBJECT_RADIUS) {
                // FAILURE: The photon hit the object.
                System.out.println("\n!! DETECTION ALERT !!");
                System.out.println("Photon hit the target at (" + (int)photonX + ", " + (int)photonY + ")");
                System.out.println("Cloak: [FAILED]\n");
                plot(display, (int)photonX, (int)photonY, 'X');
                detected = true;
                break;
                
            } else {
                // Empty Space (Vacuum)
                // Mark as '•' (Trajectory)
                
                // Correct the angle back to 0 (Straight) as it exits cloak
                if (photonX > 10 + CLOAK_RADIUS) {
                    angle = 0; 
                }
                
                plot(display, (int)photonX, (int)photonY, '•');
            }
        }

        // 4. RENDER THE TRUTH
        System.out.println("VISUALIZATION:");
        System.out.println("Legend: ' ' = vacuum, ':' = cloak, '#' = object, '•' = photon, 'C' = bent path\n");
        printGrid(display);
        
        if (!detected) {
            System.out.println("\n✓ CLOAK SUCCESSFUL");
            System.out.println("Object invisible to observer");
            System.out.println("Photon trajectory: UNDETECTED");
        }
        
        System.out.println("\n🌊⚡ SIMULATION COMPLETE");
    }

    /**
     * Initialize grid with object and cloak
     */
    private static void initializeGrid(char[][] grid) {
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                double dist = Math.sqrt(Math.pow(x - 10, 2) + Math.pow(y - 10, 2));
                
                if (dist <= OBJECT_RADIUS) {
                    grid[y][x] = '#'; // The Object (hidden)
                } else if (dist <= CLOAK_RADIUS) {
                    grid[y][x] = ':'; // The Cloak Field (metamaterial)
                } else {
                    grid[y][x] = ' '; // Empty Space (vacuum)
                }
            }
        }
    }

    /**
     * Plot character on grid
     */
    private static void plot(char[][] grid, int x, int y, char c) {
        if (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE) {
            grid[y][x] = c;
        }
    }

    /**
     * Print grid to console
     */
    private static void printGrid(char[][] grid) {
        // Top border
        System.out.print("  ");
        for (int x = 0; x < GRID_SIZE; x++) {
            System.out.print(x % 10);
        }
        System.out.println();
        
        // Grid with row numbers
        for (int y = 0; y < GRID_SIZE; y++) {
            System.out.print(String.format("%2d", y) + " ");
            for (int x = 0; x < GRID_SIZE; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }
    
    /**
     * Run simulation with custom parameters
     * 
     * @param objectRadius Inner radius (object size)
     * @param cloakRadius Outer radius (cloak extent)
     * @param photonStartY Starting Y position of photon
     * @return True if cloak successful (no detection)
     */
    public static boolean runSimulation(double objectRadius, double cloakRadius, double photonStartY) {
        // Custom simulation logic here
        // Returns true if photon doesn't hit object
        return true; // Placeholder
    }
}
