package fraymus.physics;

import java.util.Arrays;

/**
 * 🕸️ REACTIVE MESH
 * "Vertices that bleed data."
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * REVOLUTIONARY CONCEPT:
 * Standard meshes are static geometry. This mesh is REACTIVE.
 * 
 * When the HyperRigidBody accelerates in dimension 17,
 * this mesh ripples in dimension 3.
 * 
 * DEFORMATION MECHANICS:
 * - Data stress → Vertex displacement
 * - Phi-harmonic waves → Natural oscillation patterns
 * - Time-based evolution → Breathing geometry
 * 
 * This is not a 3D model. This is a LIVING SURFACE.
 */
public class Mesh {
    
    // ═══════════════════════════════════════════════════════════════════
    // GEOMETRY DATA
    // ═══════════════════════════════════════════════════════════════════
    
    public double[] vertices;           // Current vertex positions (x,y,z per point)
    private double[] originalVertices;  // Rest state (memory of original form)
    private double[] velocities;        // Vertex velocities (for physics simulation)
    
    public int vertexCount;
    
    // ═══════════════════════════════════════════════════════════════════
    // DEFORMATION STATE
    // ═══════════════════════════════════════════════════════════════════
    
    private double currentStress = 0;
    private double maxStressRecorded = 0;
    private long lastDeformTime = 0;
    
    // ═══════════════════════════════════════════════════════════════════
    // CONSTANTS
    // ═══════════════════════════════════════════════════════════════════
    
    private static final double PHI = 1.618033988749895;
    private static final double ELASTICITY = 0.95;  // How quickly mesh returns to rest
    private static final double DAMPING = 0.98;     // Velocity damping
    
    /**
     * Constructor - Generate spherical mesh
     * 
     * @param points Number of vertices
     */
    public Mesh(int points) {
        this.vertexCount = points;
        this.vertices = new double[points * 3];
        this.velocities = new double[points * 3];
        
        // Generate a sphere using fibonacci spiral
        generateFibonacciSphere(points);
        
        // Store original state
        this.originalVertices = Arrays.copyOf(vertices, vertices.length);
        
        System.out.println("🕸️ MESH CREATED");
        System.out.println("   Vertices: " + points);
        System.out.println("   Topology: Fibonacci Sphere");
    }
    
    /**
     * Generate sphere using fibonacci spiral (optimal point distribution)
     */
    private void generateFibonacciSphere(int points) {
        double goldenAngle = Math.PI * (3.0 - Math.sqrt(5.0));  // ~137.5 degrees
        
        for (int i = 0; i < points; i++) {
            double y = 1 - (i / (double) (points - 1)) * 2;  // y from 1 to -1
            double radius = Math.sqrt(1 - y * y);
            
            double theta = goldenAngle * i;
            
            double x = Math.cos(theta) * radius;
            double z = Math.sin(theta) * radius;
            
            vertices[i * 3] = x;
            vertices[i * 3 + 1] = y;
            vertices[i * 3 + 2] = z;
        }
    }
    
    /**
     * DEFORM
     * Warps the mesh based on "Data Stress" (HyperVelocity).
     * Uses sine waves modulated by the Golden Ratio (PHI).
     * 
     * @param stress Stress level (0.0 - 1.0+)
     * @param time Current time in milliseconds
     */
    public void deform(double stress, long time) {
        this.currentStress = stress;
        this.maxStressRecorded = Math.max(maxStressRecorded, stress);
        this.lastDeformTime = time;
        
        // Time-based oscillation
        double t = time * 0.001; // Convert to seconds
        
        for (int i = 0; i < vertices.length; i += 3) {
            // Get original position
            double ox = originalVertices[i];
            double oy = originalVertices[i + 1];
            double oz = originalVertices[i + 2];
            
            // Calculate distance from origin (for radial effects)
            double r = Math.sqrt(ox * ox + oy * oy + oz * oz);
            
            // Generate phi-harmonic wave
            // Multiple frequencies create complex interference patterns
            double wave1 = Math.sin(t * PHI + (i * PHI * 0.1)) * stress * 0.15;
            double wave2 = Math.sin(t * PHI * 2 + (i * PHI * 0.05)) * stress * 0.1;
            double wave3 = Math.cos(t * PHI * 0.5 + (i * PHI * 0.2)) * stress * 0.08;
            
            double totalWave = wave1 + wave2 + wave3;
            
            // Apply radial displacement (breathing effect)
            double radialDisplacement = 1.0 + totalWave;
            
            // Calculate target position
            double targetX = ox * radialDisplacement;
            double targetY = oy * radialDisplacement;
            double targetZ = oz * radialDisplacement;
            
            // Apply spring force towards target (elastic deformation)
            double dx = targetX - vertices[i];
            double dy = targetY - vertices[i + 1];
            double dz = targetZ - vertices[i + 2];
            
            // Update velocities (physics simulation)
            velocities[i] += dx * ELASTICITY;
            velocities[i + 1] += dy * ELASTICITY;
            velocities[i + 2] += dz * ELASTICITY;
            
            // Apply damping
            velocities[i] *= DAMPING;
            velocities[i + 1] *= DAMPING;
            velocities[i + 2] *= DAMPING;
            
            // Update positions
            vertices[i] += velocities[i];
            vertices[i + 1] += velocities[i + 1];
            vertices[i + 2] += velocities[i + 2];
        }
    }
    
    /**
     * Update mesh (call every frame even without stress)
     * Allows mesh to return to rest state
     */
    public void update(long time) {
        // Gradually reduce stress
        currentStress *= 0.95;
        
        // If no recent deformation, relax towards original
        if (time - lastDeformTime > 100) {
            relax(0.1);
        }
    }
    
    /**
     * Relax mesh towards original state
     * 
     * @param factor Relaxation factor (0.0 - 1.0)
     */
    public void relax(double factor) {
        for (int i = 0; i < vertices.length; i++) {
            // Interpolate towards original
            vertices[i] = vertices[i] * (1.0 - factor) + originalVertices[i] * factor;
            
            // Dampen velocities
            velocities[i] *= 0.9;
        }
    }
    
    /**
     * Reset mesh to original state
     */
    public void reset() {
        System.arraycopy(originalVertices, 0, vertices, 0, vertices.length);
        Arrays.fill(velocities, 0);
        currentStress = 0;
    }
    
    /**
     * Export mesh as OBJ format
     * 
     * @return OBJ file content
     */
    public String exportObj() {
        StringBuilder sb = new StringBuilder();
        
        // Header
        sb.append("# Fraymus Reactive Mesh\n");
        sb.append("# Vertices: ").append(vertexCount).append("\n");
        sb.append("# Max Stress: ").append(String.format("%.4f", maxStressRecorded)).append("\n");
        sb.append("\n");
        
        // Vertices
        for (int i = 0; i < vertices.length; i += 3) {
            sb.append(String.format("v %.6f %.6f %.6f\n", 
                vertices[i], vertices[i + 1], vertices[i + 2]));
        }
        
        sb.append("\n");
        
        // Generate faces (simple triangulation)
        // For a proper mesh, you'd need actual connectivity data
        // This creates a simple point cloud representation
        sb.append("# Point cloud representation\n");
        sb.append("# Use external tool for proper triangulation\n");
        
        return sb.toString();
    }
    
    /**
     * Export mesh as PLY format (better for point clouds)
     * 
     * @return PLY file content
     */
    public String exportPly() {
        StringBuilder sb = new StringBuilder();
        
        // Header
        sb.append("ply\n");
        sb.append("format ascii 1.0\n");
        sb.append("comment Fraymus Reactive Mesh\n");
        sb.append("element vertex ").append(vertexCount).append("\n");
        sb.append("property float x\n");
        sb.append("property float y\n");
        sb.append("property float z\n");
        sb.append("end_header\n");
        
        // Vertices
        for (int i = 0; i < vertices.length; i += 3) {
            sb.append(String.format("%.6f %.6f %.6f\n", 
                vertices[i], vertices[i + 1], vertices[i + 2]));
        }
        
        return sb.toString();
    }
    
    /**
     * Get mesh statistics
     */
    public String getStats() {
        // Calculate bounding box
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
        
        for (int i = 0; i < vertices.length; i += 3) {
            minX = Math.min(minX, vertices[i]);
            maxX = Math.max(maxX, vertices[i]);
            minY = Math.min(minY, vertices[i + 1]);
            maxY = Math.max(maxY, vertices[i + 1]);
            minZ = Math.min(minZ, vertices[i + 2]);
            maxZ = Math.max(maxZ, vertices[i + 2]);
        }
        
        // Calculate average displacement from original
        double totalDisplacement = 0;
        for (int i = 0; i < vertices.length; i++) {
            double diff = vertices[i] - originalVertices[i];
            totalDisplacement += diff * diff;
        }
        double avgDisplacement = Math.sqrt(totalDisplacement / vertices.length);
        
        return String.format("""
            🕸️ MESH STATISTICS
               Vertices: %d
               Bounds: [%.2f,%.2f] [%.2f,%.2f] [%.2f,%.2f]
               Current Stress: %.4f
               Max Stress: %.4f
               Avg Displacement: %.6f
            """,
            vertexCount,
            minX, maxX, minY, maxY, minZ, maxZ,
            currentStress, maxStressRecorded, avgDisplacement);
    }
    
    /**
     * Apply local deformation at specific point
     * 
     * @param px Point x
     * @param py Point y
     * @param pz Point z
     * @param radius Influence radius
     * @param strength Deformation strength
     */
    public void applyLocalDeformation(double px, double py, double pz, double radius, double strength) {
        for (int i = 0; i < vertices.length; i += 3) {
            double dx = vertices[i] - px;
            double dy = vertices[i + 1] - py;
            double dz = vertices[i + 2] - pz;
            
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            
            if (distance < radius) {
                // Falloff based on distance
                double falloff = 1.0 - (distance / radius);
                falloff = falloff * falloff; // Quadratic falloff
                
                // Apply displacement
                double displacement = strength * falloff;
                
                // Radial direction
                if (distance > 0.001) {
                    velocities[i] += (dx / distance) * displacement;
                    velocities[i + 1] += (dy / distance) * displacement;
                    velocities[i + 2] += (dz / distance) * displacement;
                }
            }
        }
    }
}
