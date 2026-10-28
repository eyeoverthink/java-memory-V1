package fraymus.physics.math;

/**
 * FRAYMATH
 * The linear algebra foundation of the physical universe.
 * Vec2: 2D vector with cross product (torque), dot product, normalization.
 * Mat22: 2x2 rotation matrix from angle in radians.
 *
 * No external libraries. Pure sovereign mathematics.
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class FrayMath {

    public static class Vec2 {
        public float x, y;

        public Vec2() {}
        public Vec2(float x, float y) { this.x = x; this.y = y; }

        public void add(Vec2 v) { this.x += v.x; this.y += v.y; }
        public void sub(Vec2 v) { this.x -= v.x; this.y -= v.y; }
        public void mul(float s) { this.x *= s; this.y *= s; }

        public float dot(Vec2 v) { return this.x * v.x + this.y * v.y; }

        // 2D Cross Product yields a scalar (Z-axis torque)
        public float cross(Vec2 v) { return this.x * v.y - this.y * v.x; }

        // Scalar cross Vector yields orthogonal Vector
        public Vec2 cross(float s) { return new Vec2(s * this.y, -s * this.x); }

        public float lengthSq() { return x * x + y * y; }
        public float length() { return (float) Math.sqrt(lengthSq()); }

        public void normalize() {
            float len = length();
            if (len > 0.0001f) { x /= len; y /= len; }
        }

        public void set(float x, float y) { this.x = x; this.y = y; }
        public void set(Vec2 v) { this.x = v.x; this.y = v.y; }

        public static Vec2 sub(Vec2 a, Vec2 b) { return new Vec2(a.x - b.x, a.y - b.y); }
        public static Vec2 add(Vec2 a, Vec2 b) { return new Vec2(a.x + b.x, a.y + b.y); }
        public static Vec2 scale(Vec2 v, float s) { return new Vec2(v.x * s, v.y * s); }
        public static Vec2 negate(Vec2 v) { return new Vec2(-v.x, -v.y); }

        @Override
        public String toString() { return String.format("(%.2f, %.2f)", x, y); }
    }

    public static class Mat22 {
        public float m00, m01, m10, m11;

        // Construct a rotation matrix from an angle in radians
        public Mat22(float angle) {
            float c = (float) Math.cos(angle);
            float s = (float) Math.sin(angle);
            m00 = c;  m01 = -s;
            m10 = s;  m11 = c;
        }

        // Construct from explicit values
        public Mat22(float m00, float m01, float m10, float m11) {
            this.m00 = m00; this.m01 = m01;
            this.m10 = m10; this.m11 = m11;
        }

        // Multiply Matrix by Vector (Rotates the vector)
        public Vec2 mul(Vec2 v) {
            return new Vec2(m00 * v.x + m01 * v.y, m10 * v.x + m11 * v.y);
        }

        // Transpose (inverse of rotation matrix)
        public Mat22 transpose() {
            return new Mat22(m00, m10, m01, m11);
        }
    }

    // --- Utility constants ---
    public static final float PI = 3.14159265358979f;
    public static final float PHI = 1.61803398874989f;

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}
