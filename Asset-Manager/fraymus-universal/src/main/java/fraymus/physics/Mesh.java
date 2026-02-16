package fraymus.physics;

import java.util.Arrays;

/**
 * 🕸️ REACTIVE MESH
 * "Vertices that bleed data."
 */
public class Mesh {
    
    // 3D Vertices (x,y,z per point)
    public double[] vertices;
    private double[] originalVertices; // Memory of "Rest State"
    public final int pointCount;

    public Mesh(int points) {
        this.pointCount = points;
        this.vertices = new double[points * 3];
        // Generate a Sphere
        for(int i=0; i<points; i++) {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.acos(2 * Math.random() - 1);
            vertices[i*3] = Math.sin(phi) * Math.cos(theta);     // x
            vertices[i*3+1] = Math.sin(phi) * Math.sin(theta);   // y
            vertices[i*3+2] = Math.cos(phi);                     // z
        }
        this.originalVertices = Arrays.copyOf(vertices, vertices.length);
    }

    /**
     * DEFORM
     * Warps the mesh based on "Data Stress" (HyperVelocity).
     * Uses Sine waves modulated by the Golden Ratio (PHI).
     */
    public void deform(double stress, long time) {
        double phi = 1.618;
        for(int i=0; i<vertices.length; i+=3) {
            // Restore towards original
            double ox = originalVertices[i];
            double oy = originalVertices[i+1];
            double oz = originalVertices[i+2];

            // Apply Ripple
            double noise = Math.sin((time * 0.01) + (i * phi)) * stress * 0.2;
            
            vertices[i]   = ox + (ox * noise);
            vertices[i+1] = oy + (oy * noise);
            vertices[i+2] = oz + (oz * noise);
        }
    }

    /**
     * Reset mesh to rest state.
     */
    public void reset() {
        System.arraycopy(originalVertices, 0, vertices, 0, vertices.length);
    }

    /**
     * Calculate total deformation from rest state.
     */
    public double getDeformation() {
        double sum = 0;
        for (int i = 0; i < vertices.length; i++) {
            double d = vertices[i] - originalVertices[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }
    
    public String exportObj() {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<vertices.length; i+=3) {
            sb.append(String.format("v %.4f %.4f %.4f\n", vertices[i], vertices[i+1], vertices[i+2]));
        }
        return sb.toString();
    }
}
