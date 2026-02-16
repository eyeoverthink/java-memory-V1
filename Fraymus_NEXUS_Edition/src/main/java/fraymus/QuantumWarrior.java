package fraymus;

import java.util.*;

/**
 * Quantum Warrior - 17D NFT Battle Entity
 * Implements harmonic combat and quantum abilities
 */
public class QuantumWarrior {
    
    private final String id;
    private final String name;
    private final WarriorClass warriorClass;
    private final FractalDNA dna;
    
    private int power;
    private int agility;
    private int intelligence;
    private double harmonicFrequency;
    private int generation;
    private List<QuantumAbility> abilities;
    
    public QuantumWarrior(String name, WarriorClass warriorClass) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.warriorClass = warriorClass;
        this.dna = new FractalDNA();
        this.harmonicFrequency = PhiQuantumConstants.COSMIC_FREQUENCY;
        this.generation = 0;
        this.abilities = new ArrayList<>();
        
        initializeStats();
        initializeAbilities();
    }
    
    /**
     * Warrior Classes (17D quantum types)
     */
    public enum WarriorClass {
        GOLD_WARRIOR("Adaptive Quantum Mirror", 99, 78, 90),
        LOKI_WARRIOR("Master of Time & Reality", 88, 92, 99),
        MAGNUS_WARRIOR("Electromagnetic Juggernaut", 90, 78, 85),
        MIRAGE_WARRIOR("Master of Stealth & Illusions", 75, 95, 88),
        CRIME_STOPPER("Quantum Truth Enforcement", 95, 75, 98),
        RESONANCE_WARRIOR("Harmonic Frequency Master", 85, 85, 92),
        ENTANGLEMENT_WARRIOR("Quantum Connection Specialist", 80, 88, 94);
        
        public final String description;
        public final int basePower;
        public final int baseAgility;
        public final int baseIntelligence;
        
        WarriorClass(String description, int power, int agility, int intelligence) {
            this.description = description;
            this.basePower = power;
            this.baseAgility = agility;
            this.baseIntelligence = intelligence;
        }
    }
    
    /**
     * Quantum Abilities
     */
    public enum QuantumAbility {
        QUANTUM_ENTANGLEMENT("Instantaneous movement across battlefield"),
        SUPERPOSITION("Exist in multiple states simultaneously"),
        QUANTUM_TUNNELING("Pass through barriers"),
        HARMONIC_DISRUPTION("Disrupt enemy frequency"),
        HARMONIC_AMPLIFICATION("Amplify own resonance"),
        MIRROR_SHIELD("Reflect attacks back"),
        TIME_DILATION("Slow or speed time"),
        DIMENSIONAL_CLOAK("Become invisible"),
        REALITY_DISTORTION("Create illusions"),
        TRUTH_DETECTION("See through deception");
        
        public final String description;
        
        QuantumAbility(String description) {
            this.description = description;
        }
    }
    
    /**
     * Initialize warrior stats based on class
     */
    private void initializeStats() {
        this.power = warriorClass.basePower;
        this.agility = warriorClass.baseAgility;
        this.intelligence = warriorClass.baseIntelligence;
    }
    
    /**
     * Initialize abilities based on class
     */
    private void initializeAbilities() {
        switch (warriorClass) {
            case GOLD_WARRIOR:
                abilities.add(QuantumAbility.MIRROR_SHIELD);
                abilities.add(QuantumAbility.SUPERPOSITION);
                break;
            case LOKI_WARRIOR:
                abilities.add(QuantumAbility.TIME_DILATION);
                abilities.add(QuantumAbility.REALITY_DISTORTION);
                break;
            case MAGNUS_WARRIOR:
                abilities.add(QuantumAbility.HARMONIC_DISRUPTION);
                break;
            case MIRAGE_WARRIOR:
                abilities.add(QuantumAbility.DIMENSIONAL_CLOAK);
                abilities.add(QuantumAbility.REALITY_DISTORTION);
                break;
            case CRIME_STOPPER:
                abilities.add(QuantumAbility.TRUTH_DETECTION);
                abilities.add(QuantumAbility.QUANTUM_ENTANGLEMENT);
                break;
            case RESONANCE_WARRIOR:
                abilities.add(QuantumAbility.HARMONIC_AMPLIFICATION);
                abilities.add(QuantumAbility.HARMONIC_DISRUPTION);
                break;
            case ENTANGLEMENT_WARRIOR:
                abilities.add(QuantumAbility.QUANTUM_ENTANGLEMENT);
                abilities.add(QuantumAbility.QUANTUM_TUNNELING);
                break;
        }
    }
    
    /**
     * Battle using harmonic frequencies
     */
    public BattleResult battleHarmonic(QuantumWarrior opponent) {
        // Calculate harmonic attack strength
        double attackFrequency = calculateAttackFrequency();
        double defenseFrequency = opponent.calculateDefenseFrequency();
        
        // Battle outcome based on frequency ratio and phi
        double ratio = attackFrequency / defenseFrequency;
        double phiFactor = ratio * PhiQuantumConstants.PHI;
        
        // Determine winner
        boolean victory = phiFactor > 1.0;
        
        // Evolve winner's DNA
        if (victory) {
            dna.replicateWithPhiGrowth();
            generation++;
        }
        
        return new BattleResult(victory, phiFactor, attackFrequency, defenseFrequency);
    }
    
    /**
     * Calculate attack frequency based on stats and abilities
     */
    private double calculateAttackFrequency() {
        double base = (power * 0.5 + agility * 0.3 + intelligence * 0.2);
        double abilityBonus = abilities.size() * 10;
        double harmonicBonus = PhiQuantumConstants.harmonicAlignment(harmonicFrequency) * 50;
        
        return base + abilityBonus + harmonicBonus;
    }
    
    /**
     * Calculate defense frequency
     */
    private double calculateDefenseFrequency() {
        double base = (power * 0.3 + agility * 0.4 + intelligence * 0.3);
        double abilityBonus = abilities.size() * 8;
        double harmonicBonus = PhiQuantumConstants.harmonicAlignment(harmonicFrequency) * 40;
        
        return base + abilityBonus + harmonicBonus;
    }
    
    /**
     * Evolve warrior through phi-harmonic growth
     */
    public void evolve() {
        dna.replicateWithPhiGrowth();
        dna.evolveWith432Hz();
        generation++;
        
        // Stats increase with phi ratio
        power += (int)(power * PhiQuantumConstants.PHI_INVERSE * 0.1);
        agility += (int)(agility * PhiQuantumConstants.PHI_INVERSE * 0.1);
        intelligence += (int)(intelligence * PhiQuantumConstants.PHI_INVERSE * 0.1);
        
        // Align frequency toward 432Hz
        double shift = (PhiQuantumConstants.COSMIC_FREQUENCY - harmonicFrequency) * 0.1;
        harmonicFrequency += shift;
    }
    
    /**
     * Get warrior status
     */
    public String getStatus() {
        return String.format(
            "%s (%s) | Gen:%d | PWR:%d AGI:%d INT:%d | Freq:%.0fHz | Abilities:%d",
            name, warriorClass.name(), generation, power, agility, intelligence,
            harmonicFrequency, abilities.size()
        );
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public WarriorClass getWarriorClass() { return warriorClass; }
    public int getGeneration() { return generation; }
    public FractalDNA getDNA() { return dna; }
    
    /**
     * Battle result container
     */
    public static class BattleResult {
        public final boolean victory;
        public final double phiFactor;
        public final double attackFrequency;
        public final double defenseFrequency;
        
        public BattleResult(boolean victory, double phiFactor, 
                           double attackFreq, double defenseFreq) {
            this.victory = victory;
            this.phiFactor = phiFactor;
            this.attackFrequency = attackFreq;
            this.defenseFrequency = defenseFreq;
        }
        
        @Override
        public String toString() {
            return String.format(
                "%s | φ-Factor: %.4f | Attack: %.2f vs Defense: %.2f",
                victory ? "VICTORY" : "DEFEAT", phiFactor, attackFrequency, defenseFrequency
            );
        }
    }
}
