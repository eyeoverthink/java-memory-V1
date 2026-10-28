package fraymus.physics.cognition;

import fraymus.physics.math.FrayMath.Vec2;
import fraymus.physics.OntologicalBody;

/**
 * 🧠 COGNITIVE BODY
 * A physical rigid body that encapsulates human data, emotions, and memories.
 * It moves through the simulation, reacting physically and emotionally to collisions.
 *
 * Emotional Model: Circumplex Model of Affect (2D Vector Space)
 *   X-Axis (Valence): -1.0 (Agony) to +1.0 (Ecstasy)
 *   Y-Axis (Arousal): -1.0 (Lethargy) to +1.0 (Frenzy)
 *
 * Physical-Cognitive Mapping:
 *   Mass       → Conviction (resistance to change)
 *   Restitution → 1 - Openness (closed minds bounce, open minds absorb)
 *   Friction   → Stubbornness (resistance to new information)
 *   Velocity   → Train of Thought / Emotional Momentum
 */
public class CognitiveBody extends OntologicalBody {

    public String identity;          // The human or concept encapsulated
    public String activeThought;     // The current data payload

    // --- THE EMOTIONAL TENSOR ---
    // Mapped to the Circumplex Model of Affect (2D Vector Space)
    // X-Axis (Valence): -1.0 (Agony) to +1.0 (Ecstasy)
    // Y-Axis (Arousal): -1.0 (Lethargy) to +1.0 (Frenzy)
    public Vec2 emotionalState;

    // --- COGNITIVE PROPERTIES ---
    public float conviction;         // Replaces Physical Mass. Higher conviction = harder to change mind.
    public float openness;           // Replaces Restitution. High openness absorbs data; low bounces it away.

    // Collision history
    private int collisionCount = 0;
    private int thoughtInfectionCount = 0;
    private int epiphanyCount = 0;

    public CognitiveBody(String id, String thought, float x, float y, float conviction, float openness) {
        super(id, x, y, 2.0f, 2.0f, conviction); // Maps conviction directly to physical mass
        this.identity = id;
        this.activeThought = thought;
        this.conviction = conviction;
        this.openness = Math.max(0.0f, Math.min(1.0f, openness));

        // Start in a neutral, calm emotional state
        this.emotionalState = new Vec2(0.0f, 0.0f);

        // Physical properties derived from psychology
        this.restitution = 1.0f - this.openness; // Closed minds bounce violently. Open minds absorb.
        this.dynamicFriction = conviction / 10.0f; // High conviction drags/resists movement.
    }

    /**
     * The internal biological process executed every game tick.
     */
    public void processCognition(float dt) {
        // 1. Emotional Decay: Emotions naturally drift back toward neutral (0,0) over time
        this.emotionalState.x -= (this.emotionalState.x * 0.1f) * dt;
        this.emotionalState.y -= (this.emotionalState.y * 0.1f) * dt;

        // 2. Thought-Velocity Coupling: High emotional arousal physically speeds up the body
        if (this.emotionalState.y > 0.5f) {
            this.addForce(new Vec2(this.velocity.x * 0.05f, this.velocity.y * 0.05f)); // Frenzy causes acceleration
        }

        // 3. Clamp emotional state to [-1, 1]
        this.emotionalState.x = Math.max(-1.0f, Math.min(1.0f, this.emotionalState.x));
        this.emotionalState.y = Math.max(-1.0f, Math.min(1.0f, this.emotionalState.y));
    }

    /**
     * Returns the emotional quadrant label.
     */
    public String getEmotionalQuadrant() {
        if (emotionalState.x >= 0 && emotionalState.y >= 0) return "ECSTATIC-FRENZY";
        if (emotionalState.x >= 0 && emotionalState.y < 0)  return "CONTENT-CALM";
        if (emotionalState.x < 0 && emotionalState.y >= 0)  return "ANGRY-PANIC";
        return "DEPRESSED-LETHARGIC";
    }

    /**
     * Returns a compact status string.
     */
    public String getStatus() {
        return String.format("[%s] thought='%s' emotion=(%.2f,%.2f) %s conv=%.1f open=%.1f collisions=%d",
            identity, activeThought, emotionalState.x, emotionalState.y,
            getEmotionalQuadrant(), conviction, openness, collisionCount);
    }

    public void recordCollision() { collisionCount++; }
    public void recordInfection() { thoughtInfectionCount++; }
    public void recordEpiphany() { epiphanyCount++; }

    public int getCollisionCount() { return collisionCount; }
    public int getThoughtInfectionCount() { return thoughtInfectionCount; }
    public int getEpiphanyCount() { return epiphanyCount; }
}
