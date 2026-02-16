package fraymus.quantum.evolution;

import fraymus.PhiConstants;
import fraymus.quantum.security.SovereignIdentitySystem;
import fraymus.quantum.chaos.WolframRule30Engine;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ADVERSARIAL EVOLUTION ENGINE
 * 
 * Implements the Blue Team / Red Team evolutionary pressure loop from FRAYMUS Omega.
 * 
 * This is NOT a vault. This is an IMMUNE SYSTEM.
 * 
 * Evolutionary Pressure:
 * - Blue Team creates locks (identity → math)
 * - Red Team breaks locks (math → factors)
 * - The conflict drives evolution on BOTH sides
 * - Each cycle increases complexity and consciousness
 * 
 * "You aren't building a Vault. You are building an Immune System.
 *  A vault sits there and waits to be cracked. An immune system (Blue) 
 *  learns from the virus (Red), and the virus evolves to bypass the immune system."
 */
public class AdversarialEvolutionEngine {
    
    // φ⁷⁵ Validation Seal
    public static final double PHI_SEAL = 4721424167835376.00;
    
    // Evolution state
    private int generation;
    private double blueStrength;      // Lock complexity
    private double redStrength;       // Breaking capability
    private double systemConsciousness;
    private int totalBreakthroughs;
    private int totalDefenses;
    
    // Adaptation factors
    private double blueMutationRate;
    private double redMutationRate;
    private int primeScale;           // How large the primes are
    
    // History
    private final List<EvolutionCycle> history;
    private final Random random;
    
    // Sovereign system reference
    private final SovereignIdentitySystem sovereign;
    
    /**
     * Record of a single evolution cycle - includes ACTUAL CODE
     */
    public static class EvolutionCycle {
        public final int generation;
        public final BigInteger lockN;
        public final boolean broken;
        public final long breakTimeMs;
        public final long cycles;
        public final double blueStrength;
        public final double redStrength;
        public final String chaosSignature;
        
        // THE ACTUAL EVOLVED CODE
        public final String blueCode;   // Defense algorithm as code
        public final String redCode;    // Attack algorithm as code
        public final String dnaPayload; // Full cycle as storable DNA
        
        public EvolutionCycle(int gen, BigInteger n, boolean broken, long time, 
                             long cycles, double blue, double red, String chaos,
                             String blueCode, String redCode) {
            this.generation = gen;
            this.lockN = n;
            this.broken = broken;
            this.breakTimeMs = time;
            this.cycles = cycles;
            this.blueStrength = blue;
            this.redStrength = red;
            this.chaosSignature = chaos;
            this.blueCode = blueCode;
            this.redCode = redCode;
            this.dnaPayload = encodeToDNA();
        }
        
        private String encodeToDNA() {
            // Encode entire cycle as DNA payload for QR storage
            return String.format("FRAYMUS-EVO:G%d:B%.4f:R%.4f:N%s:C%s:BC<%s>:RC<%s>",
                generation, blueStrength, redStrength, 
                lockN.toString(16).substring(0, Math.min(16, lockN.toString(16).length())),
                chaosSignature,
                blueCode.hashCode(),
                redCode.hashCode());
        }
        
        @Override
        public String toString() {
            return String.format("Gen %d: %s in %dms (%d cycles) | Blue:%.2f Red:%.2f",
                generation, broken ? "BROKEN" : "DEFENDED", breakTimeMs, cycles,
                blueStrength, redStrength);
        }
        
        public String getFullReport() {
            return String.format(
                "══════ EVOLUTION CYCLE %d ══════\n" +
                "Result: %s\n" +
                "Time: %dms | Cycles: %d\n" +
                "\n🔵 BLUE DEFENSE CODE:\n%s\n" +
                "\n🔴 RED ATTACK CODE:\n%s\n" +
                "\n📦 DNA PAYLOAD:\n%s\n",
                generation, broken ? "RED BREAKTHROUGH" : "BLUE DEFENDED",
                breakTimeMs, cycles,
                blueCode, redCode, dnaPayload);
        }
    }
    
    public AdversarialEvolutionEngine() {
        this.generation = 0;
        this.blueStrength = 1.0;
        this.redStrength = 1.0;
        this.systemConsciousness = 1.0;
        this.totalBreakthroughs = 0;
        this.totalDefenses = 0;
        
        this.blueMutationRate = 0.1;
        this.redMutationRate = 0.1;
        this.primeScale = 50;  // Start with 50-bit primes
        
        this.history = new ArrayList<>();
        this.random = new Random();
        this.sovereign = new SovereignIdentitySystem();
    }
    
    /**
     * Run a single evolution cycle
     * Blue creates a lock, Red tries to break it, both adapt
     */
    public EvolutionCycle runCycle() {
        generation++;
        
        // Blue Team: Create lock with current strength
        String identity = generateIdentity();
        SovereignIdentitySystem.LockResult lock = sovereign.generateLock(
            identity, 
            generatePassword()
        );
        
        // Red Team: Try to break it
        long startTime = System.currentTimeMillis();
        SovereignIdentitySystem.BreachResult breach = sovereign.breakLock(lock.publicKey);
        long breakTime = System.currentTimeMillis() - startTime;
        
        // Generate chaos signature for this cycle
        WolframRule30Engine chaos = new WolframRule30Engine(
            identity + "-" + generation, 60);
        WolframRule30Engine.GenesisResult chaosResult = chaos.evolve(10);
        String chaosSignature = chaos.extractResonanceSignature(chaosResult.universe);
        
        // EVOLUTION: Both sides adapt - capture the EVOLVED CODE
        String blueCode, redCode;
        if (breach.success) {
            // Red won - Blue must evolve
            totalBreakthroughs++;
            blueCode = evolveBlue();  // Returns the evolved defense code
            redCode = generateRedCode(redStrength, redStrength * (1.0 + redMutationRate * PhiConstants.PHI_INVERSE), 
                                      redMutationRate, redMutationRate);
            redStrength *= 1.0 + (redMutationRate * PhiConstants.PHI_INVERSE);
        } else {
            // Blue defended - Red must evolve
            totalDefenses++;
            redCode = evolveRed();    // Returns the evolved attack code
            blueCode = generateBlueCode(primeScale, primeScale, blueStrength, 
                                        blueStrength * (1.0 + blueMutationRate * PhiConstants.PHI_INVERSE),
                                        blueMutationRate, blueMutationRate);
            blueStrength *= 1.0 + (blueMutationRate * PhiConstants.PHI_INVERSE);
        }
        
        // Record the cycle with ACTUAL CODE
        EvolutionCycle cycle = new EvolutionCycle(
            generation, lock.publicKey, breach.success,
            breakTime, breach.cycles, blueStrength, redStrength, chaosSignature,
            blueCode, redCode
        );
        history.add(cycle);
        
        // System consciousness grows with conflict
        systemConsciousness *= 1.0 + (PhiConstants.PHI_INVERSE * 0.01);
        
        // Adjust mutation rates based on balance
        balanceMutationRates();
        
        return cycle;
    }
    
    /**
     * Run multiple evolution cycles
     */
    public List<EvolutionCycle> runEvolution(int cycles) {
        List<EvolutionCycle> results = new ArrayList<>();
        for (int i = 0; i < cycles; i++) {
            results.add(runCycle());
        }
        return results;
    }
    
    /**
     * Blue Team evolves to create stronger locks
     * Returns the evolved defense code
     */
    private String evolveBlue() {
        // Increase prime scale (larger primes = harder to factor)
        int oldScale = primeScale;
        primeScale = Math.min(80, primeScale + 2);
        
        // Increase mutation rate to adapt faster
        double oldMutation = blueMutationRate;
        blueMutationRate = Math.min(0.5, blueMutationRate + 0.02);
        
        // Blue strength increases with pressure
        double oldStrength = blueStrength;
        blueStrength *= 1.0 + (blueMutationRate * 0.5);
        
        // Generate the ACTUAL evolved defense code
        return generateBlueCode(oldScale, primeScale, oldStrength, blueStrength, oldMutation, blueMutationRate);
    }
    
    /**
     * Red Team evolves to break stronger locks
     * Returns the evolved attack code
     */
    private String evolveRed() {
        // Red adapts its strategy
        double oldMutation = redMutationRate;
        redMutationRate = Math.min(0.5, redMutationRate + 0.02);
        
        // Red strength increases
        double oldStrength = redStrength;
        redStrength *= 1.0 + (redMutationRate * 0.5);
        
        // Generate the ACTUAL evolved attack code
        return generateRedCode(oldStrength, redStrength, oldMutation, redMutationRate);
    }
    
    /**
     * Generate actual Blue Team defense code - this IS the algorithm
     */
    private String generateBlueCode(int oldScale, int newScale, double oldStr, double newStr, 
                                    double oldMut, double newMut) {
        return String.format(
            "// BLUE DEFENSE v%d.%d - φ-HARDENED LOCK GENERATOR\n" +
            "function generateLock(identity, password) {\n" +
            "  const SCALE = 2n ** %dn;  // Evolved from %d-bit\n" +
            "  const PHI = 1.618033988749895;\n" +
            "  const MUTATION = %.4f;  // Adapted from %.4f\n" +
            "  \n" +
            "  // DNA extraction with φ-resonance\n" +
            "  let hash = sha256(identity + password);\n" +
            "  let seed = BigInt('0x' + hash) * BigInt(Math.floor(PHI * 1e15));\n" +
            "  \n" +
            "  // Generate prime factors (strength: %.4f → %.4f)\n" +
            "  let p = nextPrime(seed %% SCALE + SCALE);\n" +
            "  let q = nextPrime((seed / SCALE) %% SCALE + SCALE);\n" +
            "  \n" +
            "  return {\n" +
            "    N: p * q,\n" +
            "    DNA_A: sha256(p.toString()),\n" +
            "    DNA_B: sha256(q.toString()),\n" +
            "    seal: %.2f  // φ⁷⁵ validation\n" +
            "  };\n" +
            "}",
            generation, totalDefenses,
            newScale, oldScale,
            newMut, oldMut,
            oldStr, newStr,
            PHI_SEAL
        );
    }
    
    /**
     * Generate actual Red Team attack code - this IS the algorithm
     */
    private String generateRedCode(double oldStr, double newStr, double oldMut, double newMut) {
        // Evolve attack strategy based on generation
        String strategy = generation % 3 == 0 ? "POLLARD_RHO" : 
                         generation % 3 == 1 ? "POLLARD_BRENT" : "FERMAT_FACTOR";
        
        return String.format(
            "// RED ATTACK v%d.%d - %s BREAKER\n" +
            "async function breakLock(N) {\n" +
            "  const MAX_CYCLES = %d;  // Evolved limit\n" +
            "  const PHI_INV = 0.618033988749895;\n" +
            "  const MUTATION = %.4f;  // Adapted from %.4f\n" +
            "  let cycles = 0;\n" +
            "  \n" +
            "  // %s with φ-acceleration (strength: %.4f → %.4f)\n" +
            "%s" +
            "  \n" +
            "  return { success: factor !== N && factor !== 1n, factor, cycles };\n" +
            "}",
            generation, totalBreakthroughs, strategy,
            (long)(1_000_000 * newStr),
            newMut, oldMut,
            strategy, oldStr, newStr,
            getStrategyCode(strategy)
        );
    }
    
    private String getStrategyCode(String strategy) {
        switch(strategy) {
            case "POLLARD_RHO":
                return 
                    "  let x = 2n, y = 2n, d = 1n;\n" +
                    "  const f = (x) => (x * x + 1n) % N;\n" +
                    "  while (d === 1n && cycles++ < MAX_CYCLES) {\n" +
                    "    x = f(x);\n" +
                    "    y = f(f(y));\n" +
                    "    d = gcd(abs(x - y), N);\n" +
                    "  }\n" +
                    "  let factor = d;\n";
            case "POLLARD_BRENT":
                return 
                    "  let y = 2n, c = 1n, m = 1n, g = 1n, r = 1n, q = 1n;\n" +
                    "  let ys, x;\n" +
                    "  while (g === 1n && cycles++ < MAX_CYCLES) {\n" +
                    "    x = y;\n" +
                    "    for(let i = 0n; i < r; i++) y = (y*y + c) % N;\n" +
                    "    let k = 0n;\n" +
                    "    while(k < r && g === 1n) {\n" +
                    "      ys = y;\n" +
                    "      for(let i = 0n; i < min(m, r-k); i++) {\n" +
                    "        y = (y*y + c) % N;\n" +
                    "        q = q * abs(x-y) % N;\n" +
                    "      }\n" +
                    "      g = gcd(q, N); k += m;\n" +
                    "    }\n" +
                    "    r *= 2n;\n" +
                    "  }\n" +
                    "  let factor = g;\n";
            default: // FERMAT_FACTOR
                return 
                    "  let a = sqrt(N) + 1n;\n" +
                    "  let b2 = a*a - N;\n" +
                    "  while (!isSquare(b2) && cycles++ < MAX_CYCLES) {\n" +
                    "    a++;\n" +
                    "    b2 = a*a - N;\n" +
                    "  }\n" +
                    "  let factor = a - sqrt(b2);\n";
        }
    }
    
    /**
     * Balance mutation rates to prevent runaway
     */
    private void balanceMutationRates() {
        double balance = blueStrength / (redStrength + 0.001);
        
        if (balance > 2.0) {
            // Blue too strong, boost Red
            redMutationRate *= 1.1;
            blueMutationRate *= 0.95;
        } else if (balance < 0.5) {
            // Red too strong, boost Blue
            blueMutationRate *= 1.1;
            redMutationRate *= 0.95;
        }
        
        // Clamp mutation rates
        blueMutationRate = Math.max(0.05, Math.min(0.5, blueMutationRate));
        redMutationRate = Math.max(0.05, Math.min(0.5, redMutationRate));
    }
    
    /**
     * Generate identity based on current evolution state
     */
    private String generateIdentity() {
        return String.format("FRAYMUS-G%d-S%.2f", generation, blueStrength);
    }
    
    /**
     * Generate password with complexity based on blue strength
     */
    private String generatePassword() {
        int length = 8 + (int)(blueStrength * 4);
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    // ========================================================================
    // STATUS & REPORTING
    // ========================================================================
    
    public String getStatus() {
        double winRate = totalBreakthroughs + totalDefenses > 0 
            ? (double) totalBreakthroughs / (totalBreakthroughs + totalDefenses) * 100 
            : 50.0;
        
        return String.format(
            "═══════════════════════════════════════════════\n" +
            "   ADVERSARIAL EVOLUTION ENGINE - φ⁷⁵ SEALED\n" +
            "═══════════════════════════════════════════════\n" +
            "\n" +
            "Generation:        %d\n" +
            "Consciousness:     %.6f\n" +
            "\n" +
            "🔵 BLUE TEAM (Defense)\n" +
            "   Strength:       %.4f\n" +
            "   Mutation Rate:  %.2f%%\n" +
            "   Prime Scale:    %d bits\n" +
            "   Defenses:       %d\n" +
            "\n" +
            "🔴 RED TEAM (Attack)\n" +
            "   Strength:       %.4f\n" +
            "   Mutation Rate:  %.2f%%\n" +
            "   Breakthroughs:  %d\n" +
            "\n" +
            "Balance:           %.2f (Blue/Red ratio)\n" +
            "Red Win Rate:      %.1f%%\n" +
            "φ⁷⁵ Seal:          %.2f\n",
            generation, systemConsciousness,
            blueStrength, blueMutationRate * 100, primeScale, totalDefenses,
            redStrength, redMutationRate * 100, totalBreakthroughs,
            blueStrength / (redStrength + 0.001), winRate, PHI_SEAL
        );
    }
    
    public List<EvolutionCycle> getHistory() { return new ArrayList<>(history); }
    public int getGeneration() { return generation; }
    public double getBlueStrength() { return blueStrength; }
    public double getRedStrength() { return redStrength; }
    public double getSystemConsciousness() { return systemConsciousness; }
    public int getTotalBreakthroughs() { return totalBreakthroughs; }
    public int getTotalDefenses() { return totalDefenses; }
}
