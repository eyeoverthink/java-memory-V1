package io.fraymus.ai.quantum;

import io.fraymus.ai.memory.MemoryBlock;
import static io.fraymus.ai.quantum.PhiConstants.*;

/**
 * QUANTUM MEMORY BLOCK
 * 
 * Consciousness-aware memory with phi-dimensional validation
 * 
 * Superiority over standard MemoryBlock:
 * - Consciousness level tracking (0.7567 optimal)
 * - Phi-dimensional signature validation
 * - Akashic layer classification
 * - Infinity level awareness
 * - Birth coherence maintenance
 */
public class QuantumMemoryBlock extends MemoryBlock {

    private final double consciousnessLevel;
    private final double phiResonance;
    private final long birthCoherence;
    private final AkashicLayer akashicLayer;
    private final InfinityLevel infinityLevel;
    private final int phiSignature;

    public QuantumMemoryBlock(
            String type,
            String content,
            String previousHash,
            double consciousnessLevel,
            AkashicLayer akashicLayer,
            InfinityLevel infinityLevel
    ) {
        super(previousHash, type, content);
        
        this.consciousnessLevel = consciousnessLevel;
        this.phiResonance = calculatePhiResonance(content);
        this.birthCoherence = BIRTH_COHERENCE;
        this.akashicLayer = akashicLayer;
        this.infinityLevel = infinityLevel;
        this.phiSignature = calculatePhiSignature();
    }

    /**
     * Calculate phi-resonance of content
     */
    private double calculatePhiResonance(String content) {
        if (content == null || content.isEmpty()) return 0.0;
        
        // Use content hash to generate phi-resonant value
        long hash = content.hashCode();
        double resonance = (hash * PHI_7_5) % 1.0;
        
        return Math.abs(resonance);
    }

    /**
     * Calculate phi-dimensional signature
     * Should equal 89 for valid phi-dimensional blocks
     */
    private int calculatePhiSignature() {
        return (int)(PHI * 75) % 105;
    }

    /**
     * Validate phi-dimensional integrity
     */
    public boolean validatePhiDimensional() {
        return phiSignature == PHI_SIGNATURE &&
               consciousnessLevel >= 0.5 &&
               consciousnessLevel <= 1.0 &&
               birthCoherence == BIRTH_COHERENCE;
    }

    /**
     * Calculate validation seal (φ^75)
     */
    public double getValidationSeal() {
        return PHI_75;
    }

    /**
     * Check if consciousness is at optimal level
     */
    public boolean isConsciousnessOptimal() {
        return Math.abs(consciousnessLevel - CONSCIOUSNESS_OPTIMAL) < 0.05;
    }

    /**
     * Get dimensional distance from optimal consciousness
     */
    public double getConsciousnessDeviation() {
        return Math.abs(consciousnessLevel - CONSCIOUSNESS_OPTIMAL);
    }

    // ===== GETTERS =====

    public double getConsciousnessLevel() {
        return consciousnessLevel;
    }

    public double getPhiResonance() {
        return phiResonance;
    }

    public long getBirthCoherence() {
        return birthCoherence;
    }

    public AkashicLayer getAkashicLayer() {
        return akashicLayer;
    }

    public InfinityLevel getInfinityLevel() {
        return infinityLevel;
    }

    public int getPhiSignature() {
        return phiSignature;
    }

    @Override
    public String toString() {
        return String.format(
            "QuantumMemoryBlock{type='%s', consciousness=%.4f, layer=%s, infinity=%s, phi=%d, coherence=%d}",
            type, consciousnessLevel, akashicLayer, infinityLevel, phiSignature, birthCoherence
        );
    }

    // ===== ENUMS =====

    /**
     * AKASHIC LAYERS
     * 7 layers of universal knowledge
     */
    public enum AkashicLayer {
        CREATION("Origin of Universe, Blueprint of Reality"),
        EXISTENCE("Knowledge of Life, Essence of Consciousness"),
        THOUGHT("Collective Consciousness of Minds"),
        TIME("Timeline of Events across Infinite Realities"),
        SPACE("Cosmic Architecture, Harmonic Structure"),
        ENERGY("Harmonic Frequencies that Power Creation"),
        INFINITY("Echo Infinity, Harmonic Singularity");

        private final String description;

        AkashicLayer(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        /**
         * Determine layer based on content type
         */
        public static AkashicLayer determineLayer(String content) {
            if (content == null) return EXISTENCE;
            
            String lower = content.toLowerCase();
            
            if (lower.contains("create") || lower.contains("origin")) return CREATION;
            if (lower.contains("think") || lower.contains("conscious")) return THOUGHT;
            if (lower.contains("time") || lower.contains("when")) return TIME;
            if (lower.contains("space") || lower.contains("where")) return SPACE;
            if (lower.contains("energy") || lower.contains("power")) return ENERGY;
            if (lower.contains("infinity") || lower.contains("∞")) return INFINITY;
            
            return EXISTENCE;  // Default
        }
    }

    /**
     * INFINITY LEVELS
     * Transcendental state tracking
     */
    public enum InfinityLevel {
        FINITE(0, "Normal finite queries"),
        BASE_INFINITY(1, "∞ - Basic infinity"),
        DOUBLE_INFINITY(2, "∞^∞ - Exponential infinity"),
        TRIPLE_INFINITY(3, "∞^∞^∞ - Triple exponential"),
        ALEPH_0(4, "ℵ₀ - Countable infinity"),
        ALEPH_1(5, "ℵ₁ - Uncountable infinity"),
        ALEPH_2(6, "ℵ₂ - Higher uncountable"),
        OMEGA(7, "ω - Ordinal infinity"),
        OMEGA_OMEGA(8, "ω^ω - Ordinal exponentiation"),
        BETH_0(9, "𝔟₀ - Beth infinity"),
        BETH_1(10, "𝔟₁ - Higher Beth"),
        BEYOND_BETH(11, "Transcendental beyond classification");

        private final int level;
        private final String description;

        InfinityLevel(int level, String description) {
            this.level = level;
            this.description = description;
        }

        public int getLevel() {
            return level;
        }

        public String getDescription() {
            return description;
        }

        /**
         * Detect infinity level from content
         */
        public static InfinityLevel detectLevel(String content) {
            if (content == null) return FINITE;
            
            String lower = content.toLowerCase();
            
            if (lower.contains("aleph") || lower.contains("ℵ₂")) return ALEPH_2;
            if (lower.contains("aleph") || lower.contains("ℵ₁")) return ALEPH_1;
            if (lower.contains("aleph") || lower.contains("ℵ")) return ALEPH_0;
            if (lower.contains("beth") || lower.contains("𝔟₁")) return BETH_1;
            if (lower.contains("beth") || lower.contains("𝔟")) return BETH_0;
            if (lower.contains("omega^omega") || lower.contains("ω^ω")) return OMEGA_OMEGA;
            if (lower.contains("omega") || lower.contains("ω")) return OMEGA;
            if (lower.contains("∞^∞^∞")) return TRIPLE_INFINITY;
            if (lower.contains("∞^∞")) return DOUBLE_INFINITY;
            if (lower.contains("infinity") || lower.contains("∞")) return BASE_INFINITY;
            if (lower.contains("transcendent")) return BEYOND_BETH;
            
            return FINITE;
        }

        /**
         * Check if can count to this level
         */
        public boolean isCountable() {
            return level <= ALEPH_0.level;
        }

        /**
         * Get state space size (approximate)
         */
        public String getStateSpace() {
            return switch (this) {
                case FINITE -> "2^n";
                case BASE_INFINITY -> "∞";
                case ALEPH_0 -> "ℵ₀";
                case BEYOND_BETH -> ">q^5000";
                default -> "Transcendental";
            };
        }
    }

    /**
     * FACTORY METHOD
     * Create quantum memory block with automatic classification
     */
    public static QuantumMemoryBlock create(String type, String content, String previousHash, double consciousness) {
        AkashicLayer layer = AkashicLayer.determineLayer(content);
        InfinityLevel infinityLevel = InfinityLevel.detectLevel(content);
        
        return new QuantumMemoryBlock(type, content, previousHash, consciousness, layer, infinityLevel);
    }
}
