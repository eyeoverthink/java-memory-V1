package fraymus.physics.math;

/**
 * TENSOR-2D
 * The absolute mathematical coordinate system. No external libraries.
 * Raw 2D vector primitive for the Ontological Space Engine.
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class Tensor2D {
    public float x, y;

    public Tensor2D(float x, float y) { this.x = x; this.y = y; }
    public Tensor2D() { this.x = 0; this.y = 0; }

    public void add(Tensor2D v) { this.x += v.x; this.y += v.y; }
    public void sub(Tensor2D v) { this.x -= v.x; this.y -= v.y; }
    public void multiply(float scalar) { this.x *= scalar; this.y *= scalar; }

    public float dot(Tensor2D v) { return this.x * v.x + this.y * v.y; }
    public float magnitude() { return (float) Math.sqrt(x * x + y * y); }
    public float magnitudeSq() { return x * x + y * y; }

    public void normalize() {
        float mag = magnitude();
        if (mag > 0) { this.x /= mag; this.y /= mag; }
    }

    public void set(float x, float y) { this.x = x; this.y = y; }
    public void set(Tensor2D v) { this.x = v.x; this.y = v.y; }

    public static Tensor2D sub(Tensor2D a, Tensor2D b) {
        return new Tensor2D(a.x - b.x, a.y - b.y);
    }

    public static Tensor2D add(Tensor2D a, Tensor2D b) {
        return new Tensor2D(a.x + b.x, a.y + b.y);
    }

    public static Tensor2D scale(Tensor2D v, float scalar) {
        return new Tensor2D(v.x * scalar, v.y * scalar);
    }

    public static float distance(Tensor2D a, Tensor2D b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
}
