package fraymus.physics.cognition;

import fraymus.physics.math.FrayMath.Vec2;

/**
 * 💥 MEMETIC COLLISION RESOLVER
 * When two Cognitive Bodies collide, they exchange physical momentum AND data.
 *
 * The kinetic force of the impact determines how much their emotions
 * and thoughts bleed into one another.
 *
 * Three outcomes:
 *   1. INFECTION: High-conviction body overwrites low-conviction body's thought
 *   2. EPIPHANY: Similar conviction bodies merge thoughts into new synthesis
 *   3. BOUNCE: Impact too weak for data exchange (just physical collision)
 */
public class MemeticExchange {

    /**
     * Resolve the cognitive impact between two colliding bodies.
     * Called after the physical impulse has been applied.
     *
     * @param A First cognitive body
     * @param B Second cognitive body
     * @param impactVelocity The relative velocity at point of impact
     */
    public static void resolveCognitiveImpact(CognitiveBody A, CognitiveBody B, float impactVelocity) {

        // If the physical impact is too weak, no data is exchanged. They just bump.
        if (impactVelocity < 1.0f) return;

        A.recordCollision();
        B.recordCollision();

        System.out.println("\n   [💥] COGNITIVE COLLISION: [" + A.identity + "] smashed into [" + B.identity + "]");
        System.out.println("      -> Impact Force: " + String.format("%.2f", impactVelocity));

        // 1. EMOTIONAL BLEED
        // The emotions average out based on Openness and Impact Force.
        float blendFactor = (A.openness + B.openness) * 0.5f * (impactVelocity / 10.0f);
        blendFactor = Math.min(blendFactor, 0.8f); // Cap the blend so identities aren't instantly destroyed

        Vec2 oldA_Emotion = new Vec2(A.emotionalState.x, A.emotionalState.y);

        A.emotionalState.x = A.emotionalState.x + (B.emotionalState.x - A.emotionalState.x) * blendFactor;
        A.emotionalState.y = A.emotionalState.y + (B.emotionalState.y - A.emotionalState.y) * blendFactor;

        B.emotionalState.x = B.emotionalState.x + (oldA_Emotion.x - B.emotionalState.x) * blendFactor;
        B.emotionalState.y = B.emotionalState.y + (oldA_Emotion.y - B.emotionalState.y) * blendFactor;

        // 2. THOUGHT INFECTION (The heavier conviction overrides the weaker one)
        if (A.conviction > B.conviction * 1.5f && A.openness < 0.5f) {
            // A crushes B's thought process
            System.out.println("      -> [" + A.identity + "]'s conviction overwhelmed [" + B.identity + "].");
            B.activeThought = A.activeThought + " (Infected by " + A.identity + ")";
            B.emotionalState.y -= 0.5f; // B experiences emotional depression (lethargy) from losing the argument
            A.recordInfection();
            B.recordInfection();
        }
        else if (B.conviction > A.conviction * 1.5f && B.openness < 0.5f) {
            // B crushes A
            System.out.println("      -> [" + B.identity + "]'s conviction overwhelmed [" + A.identity + "].");
            A.activeThought = B.activeThought + " (Infected by " + B.identity + ")";
            A.emotionalState.y -= 0.5f;
            A.recordInfection();
            B.recordInfection();
        }
        else {
            // Epiphany: They combine thoughts into a new synthetic concept
            System.out.println("      -> EPIPHANY: Thoughts merged.");
            String thoughtA = A.activeThought;
            String thoughtB = B.activeThought;
            String synthesis;
            if (thoughtA.length() > 1 && thoughtB.length() > 1) {
                synthesis = thoughtA.substring(0, thoughtA.length() / 2) +
                            thoughtB.substring(thoughtB.length() / 2);
            } else {
                synthesis = thoughtA + "+" + thoughtB;
            }
            A.activeThought = synthesis;
            B.activeThought = synthesis;

            // Both experience a spike in positive arousal (Joy/Frenzy) from the synthesis
            A.emotionalState.x += 0.5f; A.emotionalState.y += 0.5f;
            B.emotionalState.x += 0.5f; B.emotionalState.y += 0.5f;
            A.recordEpiphany();
            B.recordEpiphany();
        }

        System.out.println("      -> [" + A.identity + "] now thinks: \"" + A.activeThought + "\" " + A.getEmotionalQuadrant());
        System.out.println("      -> [" + B.identity + "] now thinks: \"" + B.activeThought + "\" " + B.getEmotionalQuadrant());
    }
}
