package fraymus.physics;

import fraymus.physics.math.FrayMath.Vec2;

/**
 * CONTACT MANIFOLD
 * Contains the mathematical data of a physical collision.
 * Normal vector, penetration depth, and exact contact point.
 *
 * Resolves collisions using:
 *   - Sequential Impulse (linear + angular)
 *   - Coulomb Friction (static + dynamic)
 *   - Positional Correction (anti-sinking)
 *
 * Patent: VS-PoQC-19046423-phi^75-2025
 */
public class ContactManifold {

    public OntologicalBody A;
    public OntologicalBody B;
    public float penetration;
    public Vec2 normal;        // Direction to push B away from A
    public Vec2 contactPoint;  // Where the physical impact occurred

    public ContactManifold(OntologicalBody a, OntologicalBody b) {
        this.A = a;
        this.B = b;
        this.normal = new Vec2();
        this.contactPoint = new Vec2();
    }

    /**
     * Solves the impulse math to push the bodies apart and apply friction.
     * Accounts for both linear and angular momentum transfer.
     */
    public void resolve() {
        // Calculate radii from center of mass to contact point
        Vec2 ra = new Vec2(contactPoint.x - A.position.x, contactPoint.y - A.position.y);
        Vec2 rb = new Vec2(contactPoint.x - B.position.x, contactPoint.y - B.position.y);

        // Relative velocity at the contact point (Linear + Rotational)
        // v_rel = v_b + (w_b x r_b) - v_a - (w_a x r_a)
        Vec2 rv = new Vec2();
        rv.x = B.velocity.x - (B.angularVelocity * rb.y) - (A.velocity.x - (A.angularVelocity * ra.y));
        rv.y = B.velocity.y + (B.angularVelocity * rb.x) - (A.velocity.y + (A.angularVelocity * ra.x));

        // Calculate velocity along the normal
        float contactVel = rv.dot(normal);

        // Do not resolve if velocities are separating
        if (contactVel > 0) return;

        // Calculate restitution (bounce)
        float e = Math.min(A.restitution, B.restitution);

        // Calculate Impulse Scalar (j)
        float raCrossN = ra.cross(normal);
        float rbCrossN = rb.cross(normal);

        float invMassSum = A.invMass + B.invMass +
                           (raCrossN * raCrossN) * A.invInertia +
                           (rbCrossN * rbCrossN) * B.invInertia;

        float j = -(1.0f + e) * contactVel;
        j /= invMassSum;

        // Apply Impulse
        Vec2 impulse = new Vec2(normal.x * j, normal.y * j);
        applyImpulse(impulse, ra, rb);

        // Apply Friction
        resolveFriction(rv, ra, rb, invMassSum, j);
    }

    private void applyImpulse(Vec2 impulse, Vec2 ra, Vec2 rb) {
        A.velocity.x -= impulse.x * A.invMass;
        A.velocity.y -= impulse.y * A.invMass;
        A.angularVelocity -= A.invInertia * ra.cross(impulse);

        B.velocity.x += impulse.x * B.invMass;
        B.velocity.y += impulse.y * B.invMass;
        B.angularVelocity += B.invInertia * rb.cross(impulse);
    }

    private void resolveFriction(Vec2 rv, Vec2 ra, Vec2 rb, float invMassSum, float jNormal) {
        // Solve for the tangent vector (perpendicular to normal)
        float rvDotN = rv.dot(normal);
        Vec2 tangent = new Vec2(rv.x - normal.x * rvDotN, rv.y - normal.y * rvDotN);
        tangent.normalize();

        // Solve for friction impulse magnitude
        float jt = -rv.dot(tangent);
        jt /= invMassSum;

        if (Math.abs(jt) < 0.0001f) return;

        // Coulomb's Law: Friction cannot exceed (Normal Force * Friction Coefficient)
        float mu = (float) Math.sqrt(
            A.staticFriction * A.staticFriction + B.staticFriction * B.staticFriction);

        Vec2 frictionImpulse;
        if (Math.abs(jt) < jNormal * mu) {
            // Static friction
            frictionImpulse = new Vec2(tangent.x * jt, tangent.y * jt);
        } else {
            // Dynamic sliding friction
            float dynamicMu = (float) Math.sqrt(
                A.dynamicFriction * A.dynamicFriction + B.dynamicFriction * B.dynamicFriction);
            frictionImpulse = new Vec2(
                tangent.x * -jNormal * dynamicMu,
                tangent.y * -jNormal * dynamicMu);
        }

        applyImpulse(frictionImpulse, ra, rb);
    }

    /**
     * Positional correction prevents objects from sinking due to floating point drift.
     */
    public void applyPositionalCorrection() {
        final float percent = 0.2f;
        final float slop = 0.01f;

        float correctionMag = Math.max(penetration - slop, 0.0f) / (A.invMass + B.invMass) * percent;
        Vec2 correction = new Vec2(normal.x * correctionMag, normal.y * correctionMag);

        A.position.x -= correction.x * A.invMass;
        A.position.y -= correction.y * A.invMass;

        B.position.x += correction.x * B.invMass;
        B.position.y += correction.y * B.invMass;
    }
}
