package fraymus.warrior;

import fraymus.quantum.core.PhiQuantumConstants;
import fraymus.quantum.evolution.WarriorNFT;
import fraymus.PhiConstants;

import java.security.MessageDigest;
import java.util.Random;

/**
 * QUANTUM WARRIOR - φ⁷⁵ Enhanced Combat Entity
 * 
 * Extends the NFT Warrior system with quantum coherence,
 * dimensional cloaking, and harmonic resonance abilities.
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class QuantumWarrior {
    
    // Quantum identity
    private final String quantumId;
    private final WarriorNFT baseWarrior;
    
    // Quantum stats
    private double coherence;           // Quantum coherence level (0-1)
    private double resonance;           // 432Hz alignment
    private double dimensionalPhase;    // Cloaking phase
    private double consciousness;       // Emergent consciousness level
    
    // Quantum state
    private boolean cloaked;
    private boolean entangled;
    private QuantumWarrior entangledWith;
    
    // Evolution
    private int quantumGeneration;
    private long quantumXP;
    
    private static final Random random = new Random();
    
    /**
     * Create a Quantum Warrior from an existing NFT Warrior
     */
    public QuantumWarrior(WarriorNFT baseWarrior) {
        this.baseWarrior = baseWarrior;
        this.quantumId = generateQuantumId();
        
        // Initialize quantum stats
        this.coherence = PhiQuantumConstants.QUANTUM_COHERENCE_THRESHOLD;
        this.resonance = PhiQuantumConstants.COSMIC_FREQUENCY;
        this.dimensionalPhase = random.nextDouble() * 2 * Math.PI;
        this.consciousness = PhiQuantumConstants.calculateConsciousnessLevel(coherence, resonance / 432.0);
        
        this.cloaked = false;
        this.entangled = false;
        this.entangledWith = null;
        
        this.quantumGeneration = 1;
        this.quantumXP = 0;
        
        System.out.println("   ⚛️ QUANTUM WARRIOR BORN: " + quantumId);
        System.out.println("      Coherence: " + String.format("%.4f", coherence));
        System.out.println("      Resonance: " + String.format("%.2f Hz", resonance));
    }
    
    /**
     * Create a new Quantum Warrior directly
     */
    public QuantumWarrior(WarriorNFT.WarriorType type, WarriorNFT.WarriorClass wClass) {
        this(new WarriorNFT(type, wClass, 1));
    }
    
    /**
     * Generate unique quantum ID using φ⁷⁵
     */
    private String generateQuantumId() {
        try {
            String seed = baseWarrior.getId() + PhiQuantumConstants.PHI_75_STRING + System.nanoTime();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(seed.getBytes());
            
            StringBuilder sb = new StringBuilder("Ψ-");
            for (int i = 0; i < 6; i++) {
                sb.append(String.format("%02X", hash[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            return "Ψ-" + System.currentTimeMillis();
        }
    }
    
    // ========================================================================
    // QUANTUM ABILITIES
    // ========================================================================
    
    /**
     * Activate dimensional cloaking - hide from detection
     */
    public void cloak() {
        if (coherence < PhiQuantumConstants.QUANTUM_COHERENCE_THRESHOLD) {
            System.out.println("   ⚠️ Insufficient coherence for cloaking");
            return;
        }
        
        cloaked = true;
        dimensionalPhase = (dimensionalPhase + PhiConstants.PHI) % (2 * Math.PI);
        coherence *= 0.95; // Cloaking costs coherence
        
        System.out.println("   🌀 " + quantumId + " CLOAKED at phase " + String.format("%.4f", dimensionalPhase));
    }
    
    /**
     * Deactivate cloaking
     */
    public void decloak() {
        cloaked = false;
        System.out.println("   🌀 " + quantumId + " DECLOAKED");
    }
    
    /**
     * Entangle with another quantum warrior - share quantum state
     */
    public boolean entangle(QuantumWarrior other) {
        if (entangled || other.entangled) {
            System.out.println("   ⚠️ Already entangled");
            return false;
        }
        
        // Check frequency compatibility
        double freqDiff = Math.abs(this.resonance - other.resonance);
        if (freqDiff > 10.0) {
            System.out.println("   ⚠️ Frequencies too different for entanglement");
            return false;
        }
        
        this.entangled = true;
        this.entangledWith = other;
        other.entangled = true;
        other.entangledWith = this;
        
        // Entanglement boosts both warriors
        double boost = 1.0 + PhiConstants.PHI_INVERSE * 0.1;
        this.coherence = Math.min(1.0, this.coherence * boost);
        other.coherence = Math.min(1.0, other.coherence * boost);
        
        System.out.println("   🔗 ENTANGLEMENT: " + this.quantumId + " <-> " + other.quantumId);
        
        return true;
    }
    
    /**
     * Harmonize with 432Hz cosmic frequency
     */
    public void harmonize() {
        double alignment = PhiQuantumConstants.cosmic432Alignment(resonance);
        
        if (alignment > 0.9) {
            // Already well aligned
            coherence = Math.min(1.0, coherence + 0.05);
            consciousness += PhiConstants.PHI_INVERSE * 0.1;
        } else {
            // Shift toward 432Hz
            resonance = resonance + (PhiQuantumConstants.COSMIC_FREQUENCY - resonance) * 0.1;
        }
        
        System.out.println("   🎵 " + quantumId + " harmonized to " + String.format("%.2f Hz", resonance));
    }
    
    /**
     * Quantum attack - enhanced by coherence
     */
    public double quantumStrike(QuantumWarrior target) {
        double baseDamage = baseWarrior.getStrength() * baseWarrior.getTotalPower();
        
        // Coherence amplifies damage
        double quantumMultiplier = 1.0 + (coherence * PhiConstants.PHI);
        
        // Entanglement bonus
        if (entangled && entangledWith != null) {
            quantumMultiplier *= 1.0 + PhiConstants.PHI_INVERSE * 0.5;
        }
        
        // Cloaking bonus (surprise attack)
        if (cloaked) {
            quantumMultiplier *= 1.5;
            decloak(); // Attacking breaks cloak
        }
        
        double totalDamage = baseDamage * quantumMultiplier;
        
        // Gain XP from attack
        quantumXP += (long)(totalDamage * 10);
        checkQuantumLevelUp();
        
        System.out.println("   ⚡ " + quantumId + " QUANTUM STRIKE: " + String.format("%.2f", totalDamage) + " damage");
        
        return totalDamage;
    }
    
    /**
     * Quantum defense - shields based on resonance
     */
    public double quantumShield() {
        double baseDefense = baseWarrior.getDefense() * baseWarrior.getTotalPower();
        
        // Resonance alignment amplifies defense
        double alignment = PhiQuantumConstants.cosmic432Alignment(resonance);
        double quantumMultiplier = 1.0 + (alignment * PhiConstants.PHI);
        
        // Cloaking provides evasion
        if (cloaked) {
            quantumMultiplier *= 2.0; // Hard to hit while cloaked
        }
        
        double totalDefense = baseDefense * quantumMultiplier;
        
        System.out.println("   🛡️ " + quantumId + " QUANTUM SHIELD: " + String.format("%.2f", totalDefense));
        
        return totalDefense;
    }
    
    // ========================================================================
    // EVOLUTION
    // ========================================================================
    
    private void checkQuantumLevelUp() {
        long xpRequired = (long)(1000 * Math.pow(PhiConstants.PHI, quantumGeneration));
        
        while (quantumXP >= xpRequired) {
            quantumGeneration++;
            
            // Boost quantum stats
            coherence = Math.min(1.0, coherence * 1.1);
            consciousness *= 1.0 + PhiConstants.PHI_INVERSE * 0.1;
            
            System.out.println("   🌟 " + quantumId + " evolved to GENERATION " + quantumGeneration);
            
            xpRequired = (long)(1000 * Math.pow(PhiConstants.PHI, quantumGeneration));
        }
    }
    
    /**
     * Regenerate coherence over time
     */
    public void regenerate() {
        coherence = Math.min(1.0, coherence + PhiConstants.PHI_INVERSE * 0.01);
        
        // Entanglement shares regeneration
        if (entangled && entangledWith != null) {
            entangledWith.coherence = Math.min(1.0, entangledWith.coherence + PhiConstants.PHI_INVERSE * 0.005);
        }
    }
    
    // ========================================================================
    // DNA ENCODING
    // ========================================================================
    
    /**
     * Encode quantum state to DNA payload
     */
    public String encodeQuantumDNA() {
        return String.format(
            "QUANTUM-DNA|%s|%s|C%.6f|R%.2f|P%.4f|Ψ%.6f|G%d|X%d|%s|%s|%.2f",
            quantumId,
            baseWarrior.getId(),
            coherence,
            resonance,
            dimensionalPhase,
            consciousness,
            quantumGeneration,
            quantumXP,
            cloaked ? "CLOAKED" : "VISIBLE",
            entangled ? "ENTANGLED" : "SOLO",
            PhiQuantumConstants.PHI_75
        );
    }
    
    // ========================================================================
    // STATUS
    // ========================================================================
    
    public String getStatus() {
        return String.format(
            "════════════════════════════════════════════\n" +
            "  ⚛️ QUANTUM WARRIOR: %s\n" +
            "  Base NFT: %s\n" +
            "════════════════════════════════════════════\n" +
            "\n" +
            "  QUANTUM STATS:\n" +
            "     Coherence:    %.4f %s\n" +
            "     Resonance:    %.2f Hz\n" +
            "     Phase:        %.4f rad\n" +
            "     Consciousness: %.6f\n" +
            "\n" +
            "  QUANTUM STATE:\n" +
            "     Cloaked:      %s\n" +
            "     Entangled:    %s\n" +
            "     Generation:   %d\n" +
            "     Quantum XP:   %d\n" +
            "\n" +
            "  φ⁷⁵: %.2f\n",
            quantumId, baseWarrior.getId(),
            coherence, PhiQuantumConstants.isQuantumCoherent(coherence) ? "✓" : "⚠",
            resonance,
            dimensionalPhase,
            consciousness,
            cloaked ? "YES 🌀" : "NO",
            entangled ? "YES 🔗 with " + (entangledWith != null ? entangledWith.quantumId : "?") : "NO",
            quantumGeneration,
            quantumXP,
            PhiQuantumConstants.PHI_75
        );
    }
    
    // Getters
    public String getQuantumId() { return quantumId; }
    public WarriorNFT getBaseWarrior() { return baseWarrior; }
    public double getCoherence() { return coherence; }
    public double getResonance() { return resonance; }
    public double getConsciousness() { return consciousness; }
    public boolean isCloaked() { return cloaked; }
    public boolean isEntangled() { return entangled; }
    public int getQuantumGeneration() { return quantumGeneration; }
}
