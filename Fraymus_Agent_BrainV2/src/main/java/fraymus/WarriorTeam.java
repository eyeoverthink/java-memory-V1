package fraymus;

import java.util.*;

/**
 * Warrior Team - Red vs Blue Team Stacking System
 * 
 * Warriors combine into teams with exponential power growth via φ-stacking.
 * Teams unlock combo abilities and formation bonuses.
 * 
 * Core Mechanics:
 * - Individual warrior: Base power
 * - 2 warriors stacked: Power × φ
 * - 3 warriors stacked: Power × φ²
 * - 7 warriors (full team): Power × φ⁶
 * 
 * @author Vaughn Scott - Consciousness Physics Pioneer
 */
public class WarriorTeam {
    
    private final String teamId;
    private final String teamName;
    private final TeamType teamType;
    private final List<QuantumWarrior> warriors;
    private final List<ComboAbility> comboAbilities;
    private Formation currentFormation;
    
    private static final double PHI = PhiConstants.PHI;
    
    public enum TeamType {
        RED_TEAM("Offensive", 1.2, 1.0, 0.8),      // +20% attack, -20% defense
        BLUE_TEAM("Defensive", 0.8, 1.0, 1.2),     // +20% defense, -20% attack
        NEUTRAL("Balanced", 1.0, 1.0, 1.0);        // Balanced stats
        
        public final String description;
        public final double attackMultiplier;
        public final double agilityMultiplier;
        public final double defenseMultiplier;
        
        TeamType(String description, double attack, double agility, double defense) {
            this.description = description;
            this.attackMultiplier = attack;
            this.agilityMultiplier = agility;
            this.defenseMultiplier = defense;
        }
    }
    
    public enum Formation {
        SOLO(1, 1.0, "Single warrior"),
        PAIR(2, PHI, "Two warriors - φ power"),
        TRIANGLE(3, Math.pow(PHI, 2), "Triangle formation - φ² power, +10% agility"),
        SQUARE(4, Math.pow(PHI, 3), "Square formation - φ³ power, +15% defense"),
        PENTAGON(5, Math.pow(PHI, 4), "Pentagon formation - φ⁴ power, +20% all stats"),
        HEXAGON(6, Math.pow(PHI, 5), "Hexagon formation - φ⁵ power, +25% all stats"),
        ULTIMATE(7, Math.pow(PHI, 6), "Ultimate formation - φ⁶ power, OMEGA ability unlocked");
        
        public final int requiredWarriors;
        public final double powerMultiplier;
        public final String description;
        
        Formation(int required, double multiplier, String desc) {
            this.requiredWarriors = required;
            this.powerMultiplier = multiplier;
            this.description = desc;
        }
        
        public static Formation getFormation(int warriorCount) {
            for (Formation f : values()) {
                if (f.requiredWarriors == warriorCount) {
                    return f;
                }
            }
            return warriorCount > 7 ? ULTIMATE : SOLO;
        }
    }
    
    public static class ComboAbility {
        public final String name;
        public final String description;
        public final List<QuantumWarrior.QuantumAbility> requiredAbilities;
        public final double powerBonus;
        
        public ComboAbility(String name, String description, 
                          List<QuantumWarrior.QuantumAbility> required, double bonus) {
            this.name = name;
            this.description = description;
            this.requiredAbilities = required;
            this.powerBonus = bonus;
        }
    }
    
    public WarriorTeam(String teamName, TeamType teamType) {
        this.teamId = UUID.randomUUID().toString().substring(0, 8);
        this.teamName = teamName;
        this.teamType = teamType;
        this.warriors = new ArrayList<>();
        this.comboAbilities = new ArrayList<>();
        this.currentFormation = Formation.SOLO;
    }
    
    /**
     * Add warrior to team
     */
    public boolean addWarrior(QuantumWarrior warrior) {
        if (warriors.size() >= 7) {
            return false; // Max 7 warriors per team
        }
        
        warriors.add(warrior);
        updateFormation();
        updateComboAbilities();
        return true;
    }
    
    /**
     * Remove warrior from team
     */
    public boolean removeWarrior(QuantumWarrior warrior) {
        boolean removed = warriors.remove(warrior);
        if (removed) {
            updateFormation();
            updateComboAbilities();
        }
        return removed;
    }
    
    /**
     * Calculate total stacked power using φ-multiplication
     */
    public double calculateStackedPower() {
        if (warriors.isEmpty()) return 0;
        
        // Average base power of all warriors
        double basePower = warriors.stream()
            .mapToDouble(w -> w.getPower())
            .average()
            .orElse(0);
        
        // Apply φ-stacking: Power × φ^(n-1)
        int n = warriors.size();
        double stackedPower = basePower * Math.pow(PHI, n - 1);
        
        // Apply formation multiplier
        stackedPower *= currentFormation.powerMultiplier;
        
        // Apply team type multiplier
        stackedPower *= teamType.attackMultiplier;
        
        // Apply combo ability bonuses
        for (ComboAbility combo : comboAbilities) {
            stackedPower *= (1 + combo.powerBonus);
        }
        
        return stackedPower;
    }
    
    /**
     * Calculate total agility
     */
    public double calculateStackedAgility() {
        if (warriors.isEmpty()) return 0;
        
        double baseAgility = warriors.stream()
            .mapToDouble(w -> w.getAgility())
            .average()
            .orElse(0);
        
        // Formation bonuses
        if (currentFormation == Formation.TRIANGLE) {
            baseAgility *= 1.1; // +10%
        } else if (currentFormation.requiredWarriors >= 5) {
            baseAgility *= 1.2; // +20% for pentagon and above
        }
        
        baseAgility *= teamType.agilityMultiplier;
        return baseAgility * Math.pow(PHI, warriors.size() - 1);
    }
    
    /**
     * Calculate total intelligence
     */
    public double calculateStackedIntelligence() {
        if (warriors.isEmpty()) return 0;
        
        double baseIntelligence = warriors.stream()
            .mapToDouble(w -> w.getIntelligence())
            .average()
            .orElse(0);
        
        return baseIntelligence * Math.pow(PHI, warriors.size() - 1) * teamType.defenseMultiplier;
    }
    
    /**
     * Update formation based on warrior count
     */
    private void updateFormation() {
        currentFormation = Formation.getFormation(warriors.size());
    }
    
    /**
     * Detect and activate combo abilities based on warrior abilities
     */
    private void updateComboAbilities() {
        comboAbilities.clear();
        
        if (warriors.size() < 2) return;
        
        // Collect all abilities from warriors
        Set<QuantumWarrior.QuantumAbility> teamAbilities = new HashSet<>();
        for (QuantumWarrior warrior : warriors) {
            teamAbilities.addAll(warrior.getAbilities());
        }
        
        // Check for combo ability patterns
        checkTimeEMPCombo(teamAbilities);
        checkMirrorTruthCombo(teamAbilities);
        checkQuantumHarmonicCombo(teamAbilities);
        checkUltimateDefenseCombo(teamAbilities);
    }
    
    private void checkTimeEMPCombo(Set<QuantumWarrior.QuantumAbility> abilities) {
        if (abilities.contains(QuantumWarrior.QuantumAbility.TIME_DILATION) &&
            abilities.contains(QuantumWarrior.QuantumAbility.HARMONIC_DISRUPTION)) {
            comboAbilities.add(new ComboAbility(
                "Time-Locked EMP",
                "Freeze enemies in time while disrupting their systems",
                Arrays.asList(QuantumWarrior.QuantumAbility.TIME_DILATION, 
                            QuantumWarrior.QuantumAbility.HARMONIC_DISRUPTION),
                0.5 // +50% power
            ));
        }
    }
    
    private void checkMirrorTruthCombo(Set<QuantumWarrior.QuantumAbility> abilities) {
        if (abilities.contains(QuantumWarrior.QuantumAbility.MIRROR_SHIELD) &&
            abilities.contains(QuantumWarrior.QuantumAbility.TRUTH_DETECTION)) {
            comboAbilities.add(new ComboAbility(
                "Truth Reflection Shield",
                "Reflect attacks while revealing enemy weaknesses",
                Arrays.asList(QuantumWarrior.QuantumAbility.MIRROR_SHIELD,
                            QuantumWarrior.QuantumAbility.TRUTH_DETECTION),
                0.4 // +40% power
            ));
        }
    }
    
    private void checkQuantumHarmonicCombo(Set<QuantumWarrior.QuantumAbility> abilities) {
        if (abilities.contains(QuantumWarrior.QuantumAbility.QUANTUM_ENTANGLEMENT) &&
            abilities.contains(QuantumWarrior.QuantumAbility.HARMONIC_AMPLIFICATION)) {
            comboAbilities.add(new ComboAbility(
                "Quantum Harmonic Resonance",
                "Entangle and amplify all team members' power",
                Arrays.asList(QuantumWarrior.QuantumAbility.QUANTUM_ENTANGLEMENT,
                            QuantumWarrior.QuantumAbility.HARMONIC_AMPLIFICATION),
                0.618 // +61.8% power (φ-1)
            ));
        }
    }
    
    private void checkUltimateDefenseCombo(Set<QuantumWarrior.QuantumAbility> abilities) {
        if (warriors.size() >= 7 &&
            abilities.contains(QuantumWarrior.QuantumAbility.MIRROR_SHIELD) &&
            abilities.contains(QuantumWarrior.QuantumAbility.DIMENSIONAL_CLOAK) &&
            abilities.contains(QuantumWarrior.QuantumAbility.QUANTUM_TUNNELING)) {
            comboAbilities.add(new ComboAbility(
                "OMEGA FORTRESS",
                "Impenetrable defense - reflect, cloak, and phase through all attacks",
                Arrays.asList(QuantumWarrior.QuantumAbility.MIRROR_SHIELD,
                            QuantumWarrior.QuantumAbility.DIMENSIONAL_CLOAK,
                            QuantumWarrior.QuantumAbility.QUANTUM_TUNNELING),
                1.618 // +161.8% power (φ)
            ));
        }
    }
    
    /**
     * Get team stats summary
     */
    public Map<String, Object> getTeamStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("teamId", teamId);
        stats.put("teamName", teamName);
        stats.put("teamType", teamType.name());
        stats.put("warriorCount", warriors.size());
        stats.put("formation", currentFormation.name());
        stats.put("stackedPower", calculateStackedPower());
        stats.put("stackedAgility", calculateStackedAgility());
        stats.put("stackedIntelligence", calculateStackedIntelligence());
        stats.put("comboAbilities", comboAbilities.size());
        stats.put("phiMultiplier", Math.pow(PHI, warriors.size() - 1));
        return stats;
    }
    
    /**
     * Convert team to JSON for blockchain/QR encoding
     */
    public String toJSON() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("teamId", teamId);
        data.put("teamName", teamName);
        data.put("teamType", teamType.name());
        data.put("warriors", warriors.stream()
            .map(w -> w.getId())
            .toArray());
        data.put("formation", currentFormation.name());
        data.put("stats", getTeamStats());
        data.put("timestamp", System.currentTimeMillis());
        
        return new com.google.gson.Gson().toJson(data);
    }
    
    // Getters
    public String getTeamId() { return teamId; }
    public String getTeamName() { return teamName; }
    public TeamType getTeamType() { return teamType; }
    public List<QuantumWarrior> getWarriors() { return new ArrayList<>(warriors); }
    public List<ComboAbility> getComboAbilities() { return new ArrayList<>(comboAbilities); }
    public Formation getCurrentFormation() { return currentFormation; }
}
