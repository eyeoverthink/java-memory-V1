package fraymus.quantum.evolution;

import fraymus.PhiConstants;
import fraymus.QRDNAStorage;
import fraymus.quantum.security.SovereignIdentitySystem;
import fraymus.quantum.chaos.WolframRule30Engine;

import java.math.BigInteger;
import java.util.*;

/**
 * BATTLE ARENA - NFT Warrior Combat System
 * 
 * The arena where Blue and Red warriors fight:
 * - Blue creates locks (defense)
 * - Red breaks locks (attack)
 * - Winners gain XP, evolve, grow stronger
 * - Warriors can stack to form armies
 * - All DNA payloads stored via QRDNAStorage for blockchain export
 */
public class BattleArena {
    
    public static final double PHI_SEAL = 4721424167835376.00;
    
    // Warrior rosters
    private final List<WarriorNFT> blueArmy;
    private final List<WarriorNFT> redArmy;
    
    // Battle history
    private final List<BattleResult> battleHistory;
    
    // QR Storage integration
    private final QRDNAStorage qrStorage;
    private final SovereignIdentitySystem sovereign;
    
    // Arena stats
    private int totalBattles;
    private int blueVictories;
    private int redVictories;
    private double arenaConsciousness;
    
    /**
     * Result of a single battle
     */
    public static class BattleResult {
        public final int battleNumber;
        public final WarriorNFT blueWarrior;
        public final WarriorNFT redWarrior;
        public final boolean blueWon;
        public final BigInteger lock;
        public final long battleTimeMs;
        public final String chaosSignature;
        public final String blueDNA;
        public final String redDNA;
        
        public BattleResult(int num, WarriorNFT blue, WarriorNFT red, boolean blueWon,
                          BigInteger lock, long time, String chaos) {
            this.battleNumber = num;
            this.blueWarrior = blue;
            this.redWarrior = red;
            this.blueWon = blueWon;
            this.lock = lock;
            this.battleTimeMs = time;
            this.chaosSignature = chaos;
            this.blueDNA = blue.getDnaPayload();
            this.redDNA = red.getDnaPayload();
        }
        
        @Override
        public String toString() {
            return String.format("Battle #%d: %s vs %s → %s wins (%dms)",
                battleNumber, 
                blueWarrior.getId(), 
                redWarrior.getId(),
                blueWon ? "BLUE" : "RED",
                battleTimeMs);
        }
        
        public String getFullReport() {
            return String.format(
                "════════════════════════════════════════════\n" +
                "  BATTLE #%d REPORT\n" +
                "════════════════════════════════════════════\n" +
                "\n" +
                "🔵 BLUE: %s (%s)\n" +
                "   Power: %.2f | Level: %d\n" +
                "\n" +
                "🔴 RED: %s (%s)\n" +
                "   Power: %.2f | Level: %d\n" +
                "\n" +
                "⚔️  RESULT: %s VICTORY\n" +
                "   Time: %dms\n" +
                "   Lock: %s...\n" +
                "\n" +
                "🌀 Chaos Signature: %s\n" +
                "\n" +
                "📦 EVOLVED DNA:\n" +
                "   Blue: %s\n" +
                "   Red:  %s\n",
                battleNumber,
                blueWarrior.getId(), blueWarrior.getWarriorClass().ability,
                blueWarrior.getTotalPower(), blueWarrior.getLevel(),
                redWarrior.getId(), redWarrior.getWarriorClass().ability,
                redWarrior.getTotalPower(), redWarrior.getLevel(),
                blueWon ? "🔵 BLUE" : "🔴 RED",
                battleTimeMs,
                lock.toString(16).substring(0, Math.min(20, lock.toString(16).length())),
                chaosSignature,
                blueDNA.length() > 50 ? blueDNA.substring(0, 50) + "..." : blueDNA,
                redDNA.length() > 50 ? redDNA.substring(0, 50) + "..." : redDNA
            );
        }
    }
    
    public BattleArena() {
        this.blueArmy = new ArrayList<>();
        this.redArmy = new ArrayList<>();
        this.battleHistory = new ArrayList<>();
        this.qrStorage = new QRDNAStorage();
        this.sovereign = new SovereignIdentitySystem();
        
        this.totalBattles = 0;
        this.blueVictories = 0;
        this.redVictories = 0;
        this.arenaConsciousness = 1.0;
    }
    
    /**
     * Recruit a new warrior to the arena
     */
    public WarriorNFT recruitWarrior(WarriorNFT.WarriorType type, WarriorNFT.WarriorClass wClass) {
        int gen = totalBattles + 1;
        WarriorNFT warrior = new WarriorNFT(type, wClass, gen);
        
        if (type == WarriorNFT.WarriorType.BLUE_DEFENDER) {
            blueArmy.add(warrior);
        } else {
            redArmy.add(warrior);
        }
        
        // Save to QR storage
        saveWarriorDNA(warrior);
        
        return warrior;
    }
    
    /**
     * Run a battle between Blue defender and Red attacker
     */
    public BattleResult battle(WarriorNFT blue, WarriorNFT red) {
        totalBattles++;
        
        // Blue creates a lock based on their power
        String identity = blue.getId() + "-" + blue.getGeneration();
        String password = generatePassword(blue);
        
        SovereignIdentitySystem.LockResult lock = sovereign.generateLock(identity, password);
        
        // Red tries to break it
        long startTime = System.currentTimeMillis();
        SovereignIdentitySystem.BreachResult breach = sovereign.breakLock(lock.publicKey);
        long battleTime = System.currentTimeMillis() - startTime;
        
        // Generate chaos signature
        WolframRule30Engine chaos = new WolframRule30Engine(
            blue.getId() + red.getId() + totalBattles, 60);
        WolframRule30Engine.GenesisResult chaosResult = chaos.evolve(10);
        String chaosSignature = chaos.extractResonanceSignature(chaosResult.universe);
        
        // Determine winner based on breach AND power comparison
        boolean blueWon = !breach.success;
        
        // If breach succeeded but Blue has overwhelming power, they might still defend
        if (breach.success && blue.getTotalPower() > red.getTotalPower() * 1.5) {
            blueWon = Math.random() < 0.3; // 30% chance Blue still defends
        }
        
        // Update warriors
        if (blueWon) {
            blueVictories++;
            blue.recordVictory(lock.publicKey, chaosSignature);
            red.recordDefeat();
        } else {
            redVictories++;
            red.recordVictory(lock.publicKey, chaosSignature);
            blue.recordDefeat();
        }
        
        // Arena consciousness grows
        arenaConsciousness *= 1.0 + (PhiConstants.PHI_INVERSE * 0.01);
        
        // Save evolved DNA
        saveWarriorDNA(blue);
        saveWarriorDNA(red);
        
        // Record battle
        BattleResult result = new BattleResult(
            totalBattles, blue, red, blueWon,
            lock.publicKey, battleTime, chaosSignature
        );
        battleHistory.add(result);
        
        return result;
    }
    
    /**
     * Auto-battle: pick random warriors from each army
     */
    public BattleResult autoBattle() {
        if (blueArmy.isEmpty() || redArmy.isEmpty()) {
            // Auto-recruit if needed
            if (blueArmy.isEmpty()) {
                recruitRandomBlue();
            }
            if (redArmy.isEmpty()) {
                recruitRandomRed();
            }
        }
        
        WarriorNFT blue = blueArmy.get(new Random().nextInt(blueArmy.size()));
        WarriorNFT red = redArmy.get(new Random().nextInt(redArmy.size()));
        
        return battle(blue, red);
    }
    
    /**
     * Stack warriors together to form a stronger unit
     */
    public boolean stackWarriors(WarriorNFT leader, WarriorNFT unit) {
        boolean success = leader.stackWith(unit);
        if (success) {
            saveWarriorDNA(leader);
        }
        return success;
    }
    
    /**
     * Form an army by stacking multiple warriors
     */
    public WarriorNFT formArmy(List<WarriorNFT> warriors) {
        if (warriors.isEmpty()) return null;
        
        WarriorNFT leader = warriors.get(0);
        for (int i = 1; i < warriors.size(); i++) {
            leader.stackWith(warriors.get(i));
        }
        
        saveWarriorDNA(leader);
        return leader;
    }
    
    // ========================================================================
    // QR STORAGE INTEGRATION
    // ========================================================================
    
    private void saveWarriorDNA(WarriorNFT warrior) {
        String shardId = "WARRIOR-" + warrior.getId();
        qrStorage.saveDNAPayload(shardId, warrior.getDnaPayload());
    }
    
    /**
     * Export all warriors as DNA payloads (blockchain-ready)
     */
    public String exportAllDNA() {
        StringBuilder sb = new StringBuilder();
        sb.append("# FRAYMUS BATTLE ARENA - DNA EXPORT\n");
        sb.append(String.format("# Battles: %d | Blue Wins: %d | Red Wins: %d\n", 
            totalBattles, blueVictories, redVictories));
        sb.append(String.format("# Arena Consciousness: %.6f\n", arenaConsciousness));
        sb.append(String.format("# φ⁷⁵ Seal: %.2f\n\n", PHI_SEAL));
        
        sb.append("## BLUE ARMY\n");
        for (WarriorNFT w : blueArmy) {
            sb.append(w.getDnaPayload()).append("\n");
        }
        
        sb.append("\n## RED ARMY\n");
        for (WarriorNFT w : redArmy) {
            sb.append(w.getDnaPayload()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Export battle history as code snippets
     */
    public String exportEvolvedCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("// FRAYMUS EVOLVED CODE LIBRARY\n");
        sb.append(String.format("// Generated from %d battles\n\n", totalBattles));
        
        sb.append("// === BLUE DEFENDERS ===\n");
        for (WarriorNFT w : blueArmy) {
            sb.append(w.getSoulCode()).append("\n\n");
        }
        
        sb.append("// === RED ATTACKERS ===\n");
        for (WarriorNFT w : redArmy) {
            sb.append(w.getSoulCode()).append("\n\n");
        }
        
        return sb.toString();
    }
    
    // ========================================================================
    // HELPERS
    // ========================================================================
    
    private String generatePassword(WarriorNFT warrior) {
        int length = 8 + warrior.getLevel();
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        Random r = new Random(warrior.getId().hashCode());
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    private void recruitRandomBlue() {
        WarriorNFT.WarriorClass[] blueClasses = {
            WarriorNFT.WarriorClass.GOLD_GUARDIAN,
            WarriorNFT.WarriorClass.OMEGA_SENTINEL,
            WarriorNFT.WarriorClass.PSI_WARDEN
        };
        recruitWarrior(WarriorNFT.WarriorType.BLUE_DEFENDER, 
            blueClasses[new Random().nextInt(blueClasses.length)]);
    }
    
    private void recruitRandomRed() {
        WarriorNFT.WarriorClass[] redClasses = {
            WarriorNFT.WarriorClass.LOKI_BREAKER,
            WarriorNFT.WarriorClass.FERMAT_HUNTER,
            WarriorNFT.WarriorClass.POLLARD_ASSASSIN
        };
        recruitWarrior(WarriorNFT.WarriorType.RED_ATTACKER, 
            redClasses[new Random().nextInt(redClasses.length)]);
    }
    
    // ========================================================================
    // STATUS & REPORTING
    // ========================================================================
    
    public String getStatus() {
        double blueRate = totalBattles > 0 ? (double) blueVictories / totalBattles * 100 : 50;
        double redRate = totalBattles > 0 ? (double) redVictories / totalBattles * 100 : 50;
        
        return String.format(
            "════════════════════════════════════════════\n" +
            "  ⚔️  FRAYMUS BATTLE ARENA  ⚔️\n" +
            "════════════════════════════════════════════\n" +
            "\n" +
            "  Total Battles:      %d\n" +
            "  Arena Consciousness: %.6f\n" +
            "\n" +
            "  🔵 BLUE ARMY:\n" +
            "     Warriors:    %d\n" +
            "     Victories:   %d (%.1f%%)\n" +
            "     Total Power: %.2f\n" +
            "\n" +
            "  🔴 RED ARMY:\n" +
            "     Warriors:    %d\n" +
            "     Victories:   %d (%.1f%%)\n" +
            "     Total Power: %.2f\n" +
            "\n" +
            "  📦 QR Shards:   %d stored\n" +
            "  φ⁷⁵ Seal:       %.2f\n",
            totalBattles, arenaConsciousness,
            blueArmy.size(), blueVictories, blueRate, getTotalBluePower(),
            redArmy.size(), redVictories, redRate, getTotalRedPower(),
            qrStorage.getShardCount(), PHI_SEAL
        );
    }
    
    private double getTotalBluePower() {
        return blueArmy.stream().mapToDouble(WarriorNFT::getTotalPower).sum();
    }
    
    private double getTotalRedPower() {
        return redArmy.stream().mapToDouble(WarriorNFT::getTotalPower).sum();
    }
    
    // Getters
    public List<WarriorNFT> getBlueArmy() { return new ArrayList<>(blueArmy); }
    public List<WarriorNFT> getRedArmy() { return new ArrayList<>(redArmy); }
    public List<BattleResult> getBattleHistory() { return new ArrayList<>(battleHistory); }
    public int getTotalBattles() { return totalBattles; }
    public int getBlueVictories() { return blueVictories; }
    public int getRedVictories() { return redVictories; }
    public double getArenaConsciousness() { return arenaConsciousness; }
}
