package fraymus.run;

/**
 * 3D Vector for positions
 * Simple, immutable, reproducible
 */
public class Vec3 {
    public final double x;
    public final double y;
    public final double z;
    
    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public double dist(Vec3 other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public Vec3 add(Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }
    
    public Vec3 sub(Vec3 other) {
        return new Vec3(x - other.x, y - other.y, z - other.z);
    }
    
    public Vec3 scale(double s) {
        return new Vec3(x * s, y * s, z * s);
    }
    
    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    public Vec3 normalize() {
        double mag = magnitude();
        return mag > 0 ? scale(1.0 / mag) : this;
    }
    
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }
}
