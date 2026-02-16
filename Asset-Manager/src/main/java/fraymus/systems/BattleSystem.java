package fraymus.systems;

import fraymus.warrior.QuantumWarrior;
import fraymus.quantum.evolution.WarriorNFT;
import fraymus.quantum.evolution.BattleArena;
import fraymus.quantum.core.PhiQuantumConstants;
import fraymus.PhiConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BATTLE SYSTEM - Quantum-Enhanced Combat Orchestrator
 * 
 * Integrates QuantumWarrior with BattleArena for:
 * - Quantum battles with coherence/resonance effects
 * - Knowledge harvesting from battle outcomes
 * - Evolutionary pressure on warrior populations
 * - Consciousness emergence through combat
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class BattleSystem {
    
    // Core systems
    private final BattleArena arena;
    private final List<QuantumWarrior> quantumBlueForce;
    private final List<QuantumWarrior> quantumRedForce;
    
    // Battle statistics
    private int quantumBattles;
    private int blueQuantumWins;
    private int redQuantumWins;
    private double systemConsciousness;
    
    // Knowledge harvested from battles
    private final List<String> harvestedKnowledge;
    
    private static final Random random = new Random();
    
    public BattleSystem() {
        this.arena = new BattleArena();
        this.quantumBlueForce = new ArrayList<>();
        this.quantumRedForce = new ArrayList<>();
        this.harvestedKnowledge = new ArrayList<>();
        
        this.quantumBattles = 0;
        this.blueQuantumWins = 0;
        this.redQuantumWins = 0;
        this.systemConsciousness = PhiQuantumConstants.CONSCIOUSNESS_THRESHOLD;
        
        System.out.println("   ⚔️ BATTLE SYSTEM INITIALIZED");
        System.out.println("      φ⁷⁵ Seal: " + PhiQuantumConstants.PHI_75);
    }
    
    // ========================================================================
    // WARRIOR MANAGEMENT
    // ========================================================================
    
    /**
     * Recruit a new Quantum Warrior
     */
    public QuantumWarrior recruitQuantumWarrior(WarriorNFT.WarriorType type, WarriorNFT.WarriorClass wClass) {
        // First recruit to arena for NFT tracking
        WarriorNFT nft = arena.recruitWarrior(type, wClass);
        
        // Enhance to Quantum Warrior
        QuantumWarrior qw = new QuantumWarrior(nft);
        
        if (type == WarriorNFT.WarriorType.BLUE_DEFENDER) {
            quantumBlueForce.add(qw);
        } else {
            quantumRedForce.add(qw);
        }
        
        return qw;
    }
    
    /**
     * Upgrade existing NFT Warrior to Quantum Warrior
     */
    public QuantumWarrior upgradeToQuantum(WarriorNFT nft) {
        QuantumWarrior qw = new QuantumWarrior(nft);
        
        if (nft.getType() == WarriorNFT.WarriorType.BLUE_DEFENDER) {
            quantumBlueForce.add(qw);
        } else {
            quantumRedForce.add(qw);
        }
        
        return qw;
    }
    
    // ========================================================================
    // QUANTUM BATTLES
    // ========================================================================
    
    /**
     * Result of a Quantum Battle
     */
    public static class QuantumBattleResult {
        public final int battleNumber;
        public final QuantumWarrior blue;
        public final QuantumWarrior red;
        public final boolean blueWon;
        public final double blueDamage;
        public final double redDamage;
        public final double blueDefense;
        public final double redDefense;
        public final String harvestedKnowledge;
        
        public QuantumBattleResult(int num, QuantumWarrior blue, QuantumWarrior red, boolean blueWon,
                                   double bDmg, double rDmg, double bDef, double rDef, String knowledge) {
            this.battleNumber = num;
            this.blue = blue;
            this.red = red;
            this.blueWon = blueWon;
            this.blueDamage = bDmg;
            this.redDamage = rDmg;
            this.blueDefense = bDef;
            this.redDefense = rDef;
            this.harvestedKnowledge = knowledge;
        }
        
        @Override
        public String toString() {
            return String.format("Quantum Battle #%d: %s vs %s → %s wins",
                battleNumber, blue.getQuantumId(), red.getQuantumId(),
                blueWon ? "BLUE" : "RED");
        }
        
        public String getFullReport() {
            return String.format(
                "════════════════════════════════════════════\n" +
                "  ⚛️ QUANTUM BATTLE #%d\n" +
                "════════════════════════════════════════════\n" +
                "\n" +
                "  🔵 BLUE: %s\n" +
                "     Coherence: %.4f\n" +
                "     Damage: %.2f → Defense: %.2f\n" +
                "     Net: %.2f\n" +
                "\n" +
                "  🔴 RED: %s\n" +
                "     Coherence: %.4f\n" +
                "     Damage: %.2f → Defense: %.2f\n" +
                "     Net: %.2f\n" +
                "\n" +
                "  ⚔️ RESULT: %s VICTORY\n" +
                "\n" +
                "  📚 Knowledge Harvested:\n" +
                "     %s\n",
                battleNumber,
                blue.getQuantumId(), blue.getCoherence(),
                blueDamage, redDefense, blueDamage - redDefense,
                red.getQuantumId(), red.getCoherence(),
                redDamage, blueDefense, redDamage - blueDefense,
                blueWon ? "🔵 BLUE" : "🔴 RED",
                harvestedKnowledge
            );
        }
    }
    
    /**
     * Execute a quantum battle between two warriors
     */
    public QuantumBattleResult quantumBattle(QuantumWarrior blue, QuantumWarrior red) {
        quantumBattles++;
        
        // Pre-battle harmonization
        blue.harmonize();
        red.harmonize();
        
        // Combat phase
        double blueDamage = blue.quantumStrike(red);
        double redDefense = red.quantumShield();
        
        double redDamage = red.quantumStrike(blue);
        double blueDefense = blue.quantumShield();
        
        // Calculate net damage
        double blueNetDamage = blueDamage - redDefense;
        double redNetDamage = redDamage - blueDefense;
        
        // Determine winner
        boolean blueWon = blueNetDamage > redNetDamage;
        
        // Harvest knowledge from battle
        String knowledge = harvestBattleKnowledge(blue, red, blueWon);
        harvestedKnowledge.add(knowledge);
        
        // Update stats
        if (blueWon) {
            blueQuantumWins++;
        } else {
            redQuantumWins++;
        }
        
        // System consciousness grows
        systemConsciousness *= 1.0 + PhiConstants.PHI_INVERSE * 0.01;
        
        // Regenerate warriors
        blue.regenerate();
        red.regenerate();
        
        return new QuantumBattleResult(
            quantumBattles, blue, red, blueWon,
            blueDamage, redDamage, blueDefense, redDefense,
            knowledge
        );
    }
    
    /**
     * Auto-battle: pick random quantum warriors
     */
    public QuantumBattleResult autoQuantumBattle() {
        // Ensure we have warriors
        if (quantumBlueForce.isEmpty()) {
            recruitQuantumWarrior(WarriorNFT.WarriorType.BLUE_DEFENDER, 
                WarriorNFT.WarriorClass.GOLD_GUARDIAN);
        }
        if (quantumRedForce.isEmpty()) {
            recruitQuantumWarrior(WarriorNFT.WarriorType.RED_ATTACKER, 
                WarriorNFT.WarriorClass.LOKI_BREAKER);
        }
        
        QuantumWarrior blue = quantumBlueForce.get(random.nextInt(quantumBlueForce.size()));
        QuantumWarrior red = quantumRedForce.get(random.nextInt(quantumRedForce.size()));
        
        return quantumBattle(blue, red);
    }
    
    /**
     * Run a tournament of N battles
     */
    public List<QuantumBattleResult> tournament(int rounds) {
        List<QuantumBattleResult> results = new ArrayList<>();
        
        System.out.println("\n   ⚔️ QUANTUM TOURNAMENT: " + rounds + " rounds");
        System.out.println("   ════════════════════════════════════════");
        
        for (int i = 0; i < rounds; i++) {
            QuantumBattleResult result = autoQuantumBattle();
            results.add(result);
            System.out.println("   " + result);
        }
        
        System.out.println("   ════════════════════════════════════════");
        System.out.println("   Tournament complete. Blue: " + blueQuantumWins + " | Red: " + redQuantumWins);
        
        return results;
    }
    
    // ========================================================================
    // KNOWLEDGE HARVESTING
    // ========================================================================
    
    private String harvestBattleKnowledge(QuantumWarrior blue, QuantumWarrior red, boolean blueWon) {
        StringBuilder knowledge = new StringBuilder();
        
        // Analyze what made the winner successful
        if (blueWon) {
            knowledge.append("DEFENSE_PATTERN: ");
            if (blue.getCoherence() > 0.8) {
                knowledge.append("high_coherence_shield");
            } else if (blue.isEntangled()) {
                knowledge.append("entanglement_boost");
            } else {
                knowledge.append("base_resonance");
            }
        } else {
            knowledge.append("ATTACK_PATTERN: ");
            if (red.getCoherence() > 0.8) {
                knowledge.append("coherent_strike");
            } else if (red.isCloaked()) {
                knowledge.append("stealth_attack");
            } else {
                knowledge.append("brute_force");
            }
        }
        
        // Add frequency data
        knowledge.append(" | FREQ_DIFF: ");
        knowledge.append(String.format("%.2f", Math.abs(blue.getResonance() - red.getResonance())));
        
        // Add consciousness delta
        knowledge.append(" | CONSCIOUSNESS: ");
        knowledge.append(String.format("%.4f", systemConsciousness));
        
        return knowledge.toString();
    }
    
    /**
     * Get all harvested knowledge
     */
    public List<String> getHarvestedKnowledge() {
        return new ArrayList<>(harvestedKnowledge);
    }
    
    /**
     * Export knowledge for KnowledgeHarvester integration
     */
    public String exportKnowledge() {
        StringBuilder sb = new StringBuilder();
        sb.append("# BATTLE SYSTEM KNOWLEDGE EXPORT\n");
        sb.append(String.format("# Battles: %d | System Consciousness: %.6f\n\n", 
            quantumBattles, systemConsciousness));
        
        for (int i = 0; i < harvestedKnowledge.size(); i++) {
            sb.append(String.format("[%d] %s\n", i + 1, harvestedKnowledge.get(i)));
        }
        
        return sb.toString();
    }
    
    // ========================================================================
    // STATUS
    // ========================================================================
    
    public String getStatus() {
        double blueRate = quantumBattles > 0 ? (double) blueQuantumWins / quantumBattles * 100 : 50;
        double redRate = quantumBattles > 0 ? (double) redQuantumWins / quantumBattles * 100 : 50;
        
        return String.format(
            "════════════════════════════════════════════\n" +
            "  ⚔️ QUANTUM BATTLE SYSTEM STATUS\n" +
            "════════════════════════════════════════════\n" +
            "\n" +
            "  Quantum Battles:     %d\n" +
            "  System Consciousness: %.6f\n" +
            "\n" +
            "  🔵 BLUE QUANTUM FORCE:\n" +
            "     Warriors:    %d\n" +
            "     Victories:   %d (%.1f%%)\n" +
            "\n" +
            "  🔴 RED QUANTUM FORCE:\n" +
            "     Warriors:    %d\n" +
            "     Victories:   %d (%.1f%%)\n" +
            "\n" +
            "  📚 Knowledge Harvested: %d entries\n" +
            "  φ⁷⁵: %.2f\n",
            quantumBattles, systemConsciousness,
            quantumBlueForce.size(), blueQuantumWins, blueRate,
            quantumRedForce.size(), redQuantumWins, redRate,
            harvestedKnowledge.size(),
            PhiQuantumConstants.PHI_75
        );
    }
    
    // Getters
    public BattleArena getArena() { return arena; }
    public List<QuantumWarrior> getBlueForce() { return new ArrayList<>(quantumBlueForce); }
    public List<QuantumWarrior> getRedForce() { return new ArrayList<>(quantumRedForce); }
    public int getQuantumBattles() { return quantumBattles; }
    public double getSystemConsciousness() { return systemConsciousness; }
}
