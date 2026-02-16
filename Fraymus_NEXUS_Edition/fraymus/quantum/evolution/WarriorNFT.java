package fraymus.quantum.evolution;

import fraymus.PhiConstants;
import fraymus.QRDNAStorage;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * WARRIOR NFT - Blue Team / Red Team Character System
 * 
 * Each warrior is an NFT-ready entity that:
 * - Has stats that grow through battle (strength, XP, generation)
 * - Contains evolved code as their "soul" (the actual algorithm)
 * - Can STACK with other warriors to create combined power
 * - Encodes to blockchain-ready DNA payload via QRDNAStorage
 * 
 * Blue Warriors: Defenders - create impenetrable systems when stacked
 * Red Warriors: Attackers - create unstoppable systems when stacked
 */
public class WarriorNFT {
    
    // φ⁷⁵ Validation Seal
    public static final double PHI_SEAL = 4721424167835376.00;
    
    public enum WarriorType {
        BLUE_DEFENDER,   // Creates locks, defensive algorithms
        RED_ATTACKER     // Breaks locks, offensive algorithms
    }
    
    public enum WarriorClass {
        // Blue Team Classes
        GOLD_GUARDIAN("GOLD", 1.618, "φ-Harmonic Shield"),
        OMEGA_SENTINEL("OMEGA", 0.567, "Grounding Lock"),
        PSI_WARDEN("PSI", 3.14159, "Transcendent Barrier"),
        
        // Red Team Classes  
        LOKI_BREAKER("LOKI", 2.718, "Chaos Injection"),
        FERMAT_HUNTER("FERMAT", 1.414, "Factor Extraction"),
        POLLARD_ASSASSIN("POLLARD", 1.732, "Rho Penetration");
        
        public final String code;
        public final double resonance;
        public final String ability;
        
        WarriorClass(String code, double resonance, String ability) {
            this.code = code;
            this.resonance = resonance;
            this.ability = ability;
        }
    }
    
    // === CORE IDENTITY ===
    private final String id;              // Unique NFT ID (hash-based)
    private final WarriorType type;
    private final WarriorClass warriorClass;
    private final long birthTimestamp;
    
    // === STATS (grow through battle) ===
    private int level;
    private long xp;
    private double strength;              // Combat power
    private double defense;               // Resistance to attacks
    private double speed;                 // Attack/defense speed
    private int victories;
    private int defeats;
    private int generation;               // Evolution generation
    
    // === SOUL (the evolved code) ===
    private String soulCode;              // The actual algorithm this warrior embodies
    private String chaosSignature;        // Wolfram Rule 30 signature
    private BigInteger lockKey;           // Last lock created/broken
    
    // === STACKING ===
    private final List<WarriorNFT> stackedWith;  // Combined warriors
    private double stackBonus;            // Multiplier from stacking
    
    // === DNA ENCODING ===
    private String dnaPayload;            // Blockchain-ready encoding
    
    private static final Random random = new Random();
    
    /**
     * Birth a new Warrior NFT
     */
    public WarriorNFT(WarriorType type, WarriorClass warriorClass, int generation) {
        this.type = type;
        this.warriorClass = warriorClass;
        this.generation = generation;
        this.birthTimestamp = System.currentTimeMillis();
        
        // Generate unique ID
        this.id = generateId();
        
        // Initialize stats based on class resonance
        this.level = 1;
        this.xp = 0;
        this.strength = warriorClass.resonance * PhiConstants.PHI;
        this.defense = warriorClass.resonance * PhiConstants.PHI_INVERSE;
        this.speed = Math.sqrt(warriorClass.resonance);
        this.victories = 0;
        this.defeats = 0;
        
        // Initialize soul with base algorithm
        this.soulCode = generateBaseSoul();
        this.chaosSignature = "";
        this.lockKey = BigInteger.ZERO;
        
        // Initialize stacking
        this.stackedWith = new ArrayList<>();
        this.stackBonus = 1.0;
        
        // Generate DNA payload
        this.dnaPayload = encodeToDNA();
    }
    
    /**
     * Generate unique warrior ID (NFT hash)
     */
    private String generateId() {
        try {
            String seed = type.name() + warriorClass.code + birthTimestamp + random.nextLong();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(seed.getBytes());
            StringBuilder sb = new StringBuilder("FRAY-");
            for (int i = 0; i < 8; i++) {
                sb.append(String.format("%02x", hash[i]));
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            return "FRAY-" + System.currentTimeMillis();
        }
    }
    
    /**
     * Generate base soul code for this warrior class
     */
    private String generateBaseSoul() {
        if (type == WarriorType.BLUE_DEFENDER) {
            return generateDefenderSoul();
        } else {
            return generateAttackerSoul();
        }
    }
    
    private String generateDefenderSoul() {
        return String.format(
            "// %s DEFENDER - %s\n" +
            "// Generation: %d | Class: %s\n" +
            "function defend(identity, threat) {\n" +
            "  const PHI = %.15f;\n" +
            "  const RESONANCE = %.6f;\n" +
            "  const STRENGTH = %.4f;\n" +
            "  \n" +
            "  // %s\n" +
            "  let shield = sha256(identity) * BigInt(Math.floor(PHI * 1e15));\n" +
            "  let barrier = nextPrime(shield %% (2n ** %dn));\n" +
            "  \n" +
            "  return {\n" +
            "    lock: barrier * nextPrime(barrier + 1n),\n" +
            "    seal: %.2f\n" +
            "  };\n" +
            "}",
            warriorClass.code, warriorClass.ability,
            generation, warriorClass.name(),
            PhiConstants.PHI, warriorClass.resonance, strength,
            warriorClass.ability,
            50 + level * 2,
            PHI_SEAL
        );
    }
    
    private String generateAttackerSoul() {
        String strategy = switch (warriorClass) {
            case LOKI_BREAKER -> "CHAOS_INJECTION";
            case FERMAT_HUNTER -> "FERMAT_FACTOR";
            case POLLARD_ASSASSIN -> "POLLARD_RHO";
            default -> "BRUTE_FORCE";
        };
        
        return String.format(
            "// %s ATTACKER - %s\n" +
            "// Generation: %d | Class: %s\n" +
            "async function attack(target) {\n" +
            "  const PHI_INV = %.15f;\n" +
            "  const RESONANCE = %.6f;\n" +
            "  const STRENGTH = %.4f;\n" +
            "  const MAX_CYCLES = %d;\n" +
            "  \n" +
            "  // %s - %s\n" +
            "%s" +
            "  \n" +
            "  return { success: factor !== target && factor !== 1n, factor };\n" +
            "}",
            warriorClass.code, warriorClass.ability,
            generation, warriorClass.name(),
            PhiConstants.PHI_INVERSE, warriorClass.resonance, strength,
            (long)(1_000_000 * strength),
            strategy, warriorClass.ability,
            getAttackCode(strategy)
        );
    }
    
    private String getAttackCode(String strategy) {
        return switch (strategy) {
            case "POLLARD_RHO" -> 
                "  let x = 2n, y = 2n, d = 1n;\n" +
                "  while (d === 1n) { x = f(x); y = f(f(y)); d = gcd(abs(x-y), target); }\n" +
                "  let factor = d;\n";
            case "FERMAT_FACTOR" ->
                "  let a = sqrt(target) + 1n;\n" +
                "  while (!isSquare(a*a - target)) a++;\n" +
                "  let factor = a - sqrt(a*a - target);\n";
            case "CHAOS_INJECTION" ->
                "  let chaos = wolframRule30(target, 100);\n" +
                "  let factor = extractPrimeFromChaos(chaos, target);\n";
            default ->
                "  let factor = 2n;\n" +
                "  while (target % factor !== 0n) factor++;\n";
        };
    }
    
    // ========================================================================
    // BATTLE SYSTEM
    // ========================================================================
    
    /**
     * Record a victory - gain XP and evolve
     */
    public void recordVictory(BigInteger key, String newChaosSignature) {
        victories++;
        long xpGain = (long)(100 * strength * stackBonus);
        xp += xpGain;
        this.lockKey = key;
        this.chaosSignature = newChaosSignature;
        
        // Level up check
        checkLevelUp();
        
        // Evolve soul code
        evolveSoul(true);
        
        // Update DNA
        this.dnaPayload = encodeToDNA();
    }
    
    /**
     * Record a defeat - still gain some XP, learn from failure
     */
    public void recordDefeat() {
        defeats++;
        long xpGain = (long)(25 * strength);  // Less XP but still learn
        xp += xpGain;
        
        checkLevelUp();
        evolveSoul(false);
        this.dnaPayload = encodeToDNA();
    }
    
    private void checkLevelUp() {
        long xpRequired = (long)(100 * Math.pow(PhiConstants.PHI, level));
        while (xp >= xpRequired) {
            level++;
            strength *= 1.0 + (PhiConstants.PHI_INVERSE * 0.1);
            defense *= 1.0 + (PhiConstants.PHI_INVERSE * 0.08);
            speed *= 1.0 + (PhiConstants.PHI_INVERSE * 0.05);
            xpRequired = (long)(100 * Math.pow(PhiConstants.PHI, level));
        }
    }
    
    /**
     * Evolve the soul code based on battle outcome
     */
    private void evolveSoul(boolean victory) {
        generation++;
        
        // Regenerate soul with new stats
        this.soulCode = generateBaseSoul();
        
        // Add evolution comment
        String evolutionNote = String.format(
            "\n// EVOLUTION %d: %s | STR:%.2f DEF:%.2f SPD:%.2f\n",
            generation, victory ? "VICTORY" : "DEFEAT",
            strength, defense, speed
        );
        this.soulCode = evolutionNote + this.soulCode;
    }
    
    // ========================================================================
    // STACKING SYSTEM - Combine Warriors for Greater Power
    // ========================================================================
    
    /**
     * Stack this warrior with another - combine powers
     * Blue + Blue = Impenetrable Defense
     * Red + Red = Unstoppable Attack
     * Blue + Red = Balanced Hybrid (rare)
     */
    public boolean stackWith(WarriorNFT other) {
        if (stackedWith.contains(other)) return false;
        
        stackedWith.add(other);
        
        // Calculate stack bonus
        if (this.type == other.type) {
            // Same type = multiplicative bonus
            stackBonus *= 1.0 + (other.strength * PhiConstants.PHI_INVERSE * 0.5);
        } else {
            // Different type = smaller but balanced bonus
            stackBonus *= 1.0 + (other.strength * PhiConstants.PHI_INVERSE * 0.25);
        }
        
        // Combine soul codes
        this.soulCode = generateStackedSoul();
        this.dnaPayload = encodeToDNA();
        
        return true;
    }
    
    /**
     * Get total combat power (including stacked warriors)
     */
    public double getTotalPower() {
        double basePower = (strength + defense + speed) / 3.0;
        return basePower * stackBonus * level;
    }
    
    private String generateStackedSoul() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("// STACKED WARRIOR COLLECTIVE - %d UNITS\n", stackedWith.size() + 1));
        sb.append(String.format("// Combined Power: %.4f | Stack Bonus: %.2fx\n\n", getTotalPower(), stackBonus));
        
        sb.append("const COLLECTIVE = {\n");
        sb.append(String.format("  leader: '%s',\n", id));
        sb.append("  units: [\n");
        
        for (WarriorNFT w : stackedWith) {
            sb.append(String.format("    { id: '%s', class: '%s', power: %.2f },\n", 
                w.id, w.warriorClass.code, w.getTotalPower()));
        }
        
        sb.append("  ],\n");
        sb.append(String.format("  execute: %s\n", type == WarriorType.BLUE_DEFENDER ? "defendCollective" : "attackCollective"));
        sb.append("};\n\n");
        
        sb.append(soulCode);
        
        return sb.toString();
    }
    
    // ========================================================================
    // DNA ENCODING - Blockchain Ready
    // ========================================================================
    
    /**
     * Encode warrior to blockchain-ready DNA payload
     * Format: FRAY-NFT|ID|TYPE|CLASS|GEN|LVL|STR|DEF|SPD|XP|V|D|STACK|SOUL_HASH|SEAL
     */
    public String encodeToDNA() {
        String soulHash = hashSoul();
        
        return String.format(
            "FRAY-NFT|%s|%s|%s|G%d|L%d|S%.4f|D%.4f|P%.4f|X%d|V%d|F%d|K%d|%s|%.2f",
            id,
            type == WarriorType.BLUE_DEFENDER ? "BLUE" : "RED",
            warriorClass.code,
            generation,
            level,
            strength,
            defense,
            speed,
            xp,
            victories,
            defeats,
            stackedWith.size(),
            soulHash,
            PHI_SEAL
        );
    }
    
    private String hashSoul() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(soulCode.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                sb.append(String.format("%02x", hash[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            return "00000000";
        }
    }
    
    /**
     * Decode DNA payload back to warrior (resurrection)
     */
    public static WarriorNFT fromDNA(String dnaPayload) {
        try {
            String[] parts = dnaPayload.split("\\|");
            if (!parts[0].equals("FRAY-NFT")) return null;
            
            // Parse type and class
            WarriorType type = parts[2].equals("BLUE") ? 
                WarriorType.BLUE_DEFENDER : WarriorType.RED_ATTACKER;
            
            WarriorClass wClass = null;
            for (WarriorClass c : WarriorClass.values()) {
                if (c.code.equals(parts[3])) {
                    wClass = c;
                    break;
                }
            }
            if (wClass == null) return null;
            
            int gen = Integer.parseInt(parts[4].substring(1));
            
            WarriorNFT warrior = new WarriorNFT(type, wClass, gen);
            
            // Restore stats
            warrior.level = Integer.parseInt(parts[5].substring(1));
            warrior.strength = Double.parseDouble(parts[6].substring(1));
            warrior.defense = Double.parseDouble(parts[7].substring(1));
            warrior.speed = Double.parseDouble(parts[8].substring(1));
            warrior.xp = Long.parseLong(parts[9].substring(1));
            warrior.victories = Integer.parseInt(parts[10].substring(1));
            warrior.defeats = Integer.parseInt(parts[11].substring(1));
            
            return warrior;
        } catch (Exception e) {
            return null;
        }
    }
    
    // ========================================================================
    // STATUS & REPORTING
    // ========================================================================
    
    public String getStatus() {
        double winRate = victories + defeats > 0 ? 
            (double) victories / (victories + defeats) * 100 : 50.0;
        
        return String.format(
            "══════════════════════════════════════════\n" +
            "  %s WARRIOR: %s\n" +
            "  Class: %s - %s\n" +
            "══════════════════════════════════════════\n" +
            "\n" +
            "  Level: %d (XP: %d)\n" +
            "  Generation: %d\n" +
            "\n" +
            "  ⚔️  STATS:\n" +
            "     Strength: %.4f\n" +
            "     Defense:  %.4f\n" +
            "     Speed:    %.4f\n" +
            "     Power:    %.4f\n" +
            "\n" +
            "  🏆 RECORD:\n" +
            "     Victories: %d\n" +
            "     Defeats:   %d\n" +
            "     Win Rate:  %.1f%%\n" +
            "\n" +
            "  🔗 STACK:\n" +
            "     Units:     %d\n" +
            "     Bonus:     %.2fx\n" +
            "\n" +
            "  📦 DNA: %s\n" +
            "  φ⁷⁵ Seal: %.2f\n",
            type == WarriorType.BLUE_DEFENDER ? "🔵 BLUE" : "🔴 RED",
            id,
            warriorClass.code, warriorClass.ability,
            level, xp, generation,
            strength, defense, speed, getTotalPower(),
            victories, defeats, winRate,
            stackedWith.size(), stackBonus,
            dnaPayload.length() > 60 ? dnaPayload.substring(0, 60) + "..." : dnaPayload,
            PHI_SEAL
        );
    }
    
    public String getSoulCode() { return soulCode; }
    public String getDnaPayload() { return dnaPayload; }
    public String getId() { return id; }
    public WarriorType getType() { return type; }
    public WarriorClass getWarriorClass() { return warriorClass; }
    public int getLevel() { return level; }
    public double getStrength() { return strength; }
    public double getDefense() { return defense; }
    public double getSpeed() { return speed; }
    public int getVictories() { return victories; }
    public int getDefeats() { return defeats; }
    public int getGeneration() { return generation; }
    public List<WarriorNFT> getStackedWith() { return new ArrayList<>(stackedWith); }
    public double getStackBonus() { return stackBonus; }
}
