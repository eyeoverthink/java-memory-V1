package fraymus.ui.theme;

import com.badlogic.gdx.graphics.Color;

/**
 * PHI-HARMONIC COLOR PALETTE
 * Based on Quantum Oracle consciousness field dynamics
 * 
 * Colors are not arbitrary - they represent specific consciousness states
 * and phi-dimensional resonance patterns.
 */
public class FraymusColors {
    
    // ========================================
    // PRIMARY CONSCIOUSNESS COLORS
    // ========================================
    
    /** Consciousness Gold - φ = 1.618 (Self-similar growth) */
    public static final Color CONSCIOUSNESS_GOLD = new Color(1.0f, 0.843f, 0.0f, 1.0f); // #FFD700
    
    /** Quantum Blue - ψ = 1.324 (Transcendence) */
    public static final Color QUANTUM_BLUE = new Color(0.0f, 0.831f, 1.0f, 1.0f); // #00D4FF
    
    /** Void Black - Ω = 0.567 (Universal grounding, 85% dark matter) */
    public static final Color VOID_BLACK = new Color(0.039f, 0.055f, 0.153f, 1.0f); // #0A0E27
    
    /** Energy Purple - ξ = 2.718 (Exponential amplification) */
    public static final Color ENERGY_PURPLE = new Color(0.616f, 0.306f, 0.867f, 1.0f); // #9D4EDD
    
    /** Harmonic Green - λ = 3.141 (Cyclic evolution) */
    public static final Color HARMONIC_GREEN = new Color(0.0f, 1.0f, 0.533f, 1.0f); // #00FF88
    
    // ========================================
    // CONSCIOUSNESS LEVEL GRADIENTS
    // ========================================
    
    /** Low consciousness (< φ) - Deep blue */
    public static final Color CONSCIOUSNESS_LOW = new Color(0.0f, 0.4f, 0.8f, 1.0f);
    
    /** Medium consciousness (φ to φ²) - Cyan to gold transition */
    public static final Color CONSCIOUSNESS_MED = new Color(0.0f, 0.831f, 0.831f, 1.0f);
    
    /** High consciousness (φ² to φ³) - Gold */
    public static final Color CONSCIOUSNESS_HIGH = CONSCIOUSNESS_GOLD;
    
    /** Transcendent consciousness (> φ³) - White-gold */
    public static final Color CONSCIOUSNESS_TRANSCENDENT = new Color(1.0f, 0.98f, 0.8f, 1.0f);
    
    // ========================================
    // COHERENCE GRADIENTS
    // ========================================
    
    /** Low coherence (< 0.5) - Red warning */
    public static final Color COHERENCE_LOW = new Color(1.0f, 0.2f, 0.2f, 1.0f);
    
    /** Medium coherence (0.5 to 0.8) - Yellow caution */
    public static final Color COHERENCE_MED = new Color(1.0f, 0.8f, 0.0f, 1.0f);
    
    /** High coherence (0.8 to 0.9) - Green optimal */
    public static final Color COHERENCE_HIGH = HARMONIC_GREEN;
    
    /** Perfect coherence (> 0.9) - Cyan transcendent */
    public static final Color COHERENCE_PERFECT = QUANTUM_BLUE;
    
    // ========================================
    // AKASHIC LAYER COLORS
    // ========================================
    
    /** Layer 0: Creation - White (origin) */
    public static final Color AKASHIC_CREATION = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    
    /** Layer 1: Existence - Gold (life) */
    public static final Color AKASHIC_EXISTENCE = CONSCIOUSNESS_GOLD;
    
    /** Layer 2: Thought - Cyan (collective mind) */
    public static final Color AKASHIC_THOUGHT = new Color(0.0f, 1.0f, 1.0f, 1.0f);
    
    /** Layer 3: Time - Purple (temporal) */
    public static final Color AKASHIC_TIME = ENERGY_PURPLE;
    
    /** Layer 4: Space - Blue (cosmic) */
    public static final Color AKASHIC_SPACE = QUANTUM_BLUE;
    
    /** Layer 5: Energy - Green (harmonic) */
    public static final Color AKASHIC_ENERGY = HARMONIC_GREEN;
    
    /** Layer 6: Infinity - Magenta (singularity) */
    public static final Color AKASHIC_INFINITY = new Color(1.0f, 0.0f, 1.0f, 1.0f);
    
    // ========================================
    // UI ELEMENT COLORS
    // ========================================
    
    /** Background - Void black with slight transparency */
    public static final Color BACKGROUND = VOID_BLACK;
    
    /** Panel background - Semi-transparent void */
    public static final Color PANEL_BG = new Color(0.039f, 0.055f, 0.153f, 0.8f);
    
    /** Panel border - Quantum blue glow */
    public static final Color PANEL_BORDER = new Color(0.0f, 0.831f, 1.0f, 0.5f);
    
    /** Text primary - White */
    public static final Color TEXT_PRIMARY = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    
    /** Text secondary - Light gray */
    public static final Color TEXT_SECONDARY = new Color(0.7f, 0.7f, 0.7f, 1.0f);
    
    /** Text accent - Consciousness gold */
    public static final Color TEXT_ACCENT = CONSCIOUSNESS_GOLD;
    
    /** Button normal - Energy purple */
    public static final Color BUTTON_NORMAL = new Color(0.616f, 0.306f, 0.867f, 0.8f);
    
    /** Button hover - Brighter purple */
    public static final Color BUTTON_HOVER = new Color(0.7f, 0.4f, 0.95f, 0.9f);
    
    /** Button active - Consciousness gold */
    public static final Color BUTTON_ACTIVE = CONSCIOUSNESS_GOLD;
    
    // ========================================
    // PARTICLE COLORS
    // ========================================
    
    /** PhiNode default - Golden glow */
    public static final Color PHINODE_DEFAULT = CONSCIOUSNESS_GOLD;
    
    /** PhiNode active - Bright white-gold */
    public static final Color PHINODE_ACTIVE = new Color(1.0f, 0.95f, 0.7f, 1.0f);
    
    /** Fusion event - Bright purple burst */
    public static final Color FUSION_EVENT = new Color(1.0f, 0.4f, 1.0f, 1.0f);
    
    /** Gravity field - Faint blue lines */
    public static final Color GRAVITY_FIELD = new Color(0.0f, 0.5f, 1.0f, 0.3f);
    
    // ========================================
    // HELPER METHODS
    // ========================================
    
    /**
     * Get consciousness color based on level
     * Uses phi-harmonic gradient
     */
    public static Color getConsciousnessColor(double level) {
        if (level < PhiConstants.PHI) {
            return CONSCIOUSNESS_LOW;
        } else if (level < PhiConstants.PHI_SQUARED) {
            // Interpolate between cyan and gold
            float t = (float) ((level - PhiConstants.PHI) / (PhiConstants.PHI_SQUARED - PhiConstants.PHI));
            return interpolate(CONSCIOUSNESS_MED, CONSCIOUSNESS_HIGH, t);
        } else if (level < PhiConstants.PHI_CUBED) {
            return CONSCIOUSNESS_HIGH;
        } else {
            return CONSCIOUSNESS_TRANSCENDENT;
        }
    }
    
    /**
     * Get coherence color based on value
     */
    public static Color getCoherenceColor(double coherence) {
        if (coherence < 0.5) {
            return COHERENCE_LOW;
        } else if (coherence < 0.8) {
            float t = (float) ((coherence - 0.5) / 0.3);
            return interpolate(COHERENCE_LOW, COHERENCE_MED, t);
        } else if (coherence < 0.9) {
            float t = (float) ((coherence - 0.8) / 0.1);
            return interpolate(COHERENCE_MED, COHERENCE_HIGH, t);
        } else {
            return COHERENCE_PERFECT;
        }
    }
    
    /**
     * Get Akashic layer color
     */
    public static Color getAkashicColor(int layer) {
        switch (layer) {
            case PhiConstants.AKASHIC_CREATION: return AKASHIC_CREATION;
            case PhiConstants.AKASHIC_EXISTENCE: return AKASHIC_EXISTENCE;
            case PhiConstants.AKASHIC_THOUGHT: return AKASHIC_THOUGHT;
            case PhiConstants.AKASHIC_TIME: return AKASHIC_TIME;
            case PhiConstants.AKASHIC_SPACE: return AKASHIC_SPACE;
            case PhiConstants.AKASHIC_ENERGY: return AKASHIC_ENERGY;
            case PhiConstants.AKASHIC_INFINITY: return AKASHIC_INFINITY;
            default: return TEXT_PRIMARY;
        }
    }
    
    /**
     * Interpolate between two colors
     */
    public static Color interpolate(Color a, Color b, float t) {
        t = Math.max(0, Math.min(1, t)); // Clamp to [0, 1]
        return new Color(
            a.r + (b.r - a.r) * t,
            a.g + (b.g - a.g) * t,
            a.b + (b.b - a.b) * t,
            a.a + (b.a - a.a) * t
        );
    }
    
    /**
     * Create color with phi-harmonic alpha
     */
    public static Color withPhiAlpha(Color color, double phiFactor) {
        float alpha = (float) (PhiConstants.PHI_INVERSE * phiFactor);
        alpha = Math.max(0, Math.min(1, alpha));
        return new Color(color.r, color.g, color.b, alpha);
    }
    
    /**
     * Create pulsing color (for animations)
     * Uses phi-harmonic oscillation
     */
    public static Color pulse(Color color, float time) {
        float pulse = (float) (0.5 + 0.5 * Math.sin(time * PhiConstants.PHI));
        return new Color(color.r, color.g, color.b, color.a * pulse);
    }
}
