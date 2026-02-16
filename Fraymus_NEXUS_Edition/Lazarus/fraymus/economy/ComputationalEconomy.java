package fraymus.economy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * THE LABOR & VALUE ENGINE: COMPUTATIONAL ECONOMY
 * 
 * Section XII: Proof of Phi-Work (PoPW)
 * 
 * Value is generated not by random hashing (Bitcoin), but by "Manifold Optimization."
 * 
 * W = ∫ (System_Entropy_Initial - System_Entropy_Final) dt
 * 1 Credit = 1 Unit of Entropy Reduction (Organization)
 * 
 * The "Skill Shard" Market:
 * Skill_Value(s) = Usage_Count × (Success_Rate / CPU_Cost)
 * 
 * Smart Contract:
 * If FRAYMUS uses Module_X to solve a problem:
 *   Transfer(0.001 Credits) -> Author_Wallet_X
 * 
 * "Work creates value. Organization is work."
 */
public class ComputationalEconomy {

    private static final double PHI = 1.6180339887;
    
    // Ledger
    private final Map<String, Double> wallets = new ConcurrentHashMap<>();
    private final Map<String, SkillShard> skillMarket = new ConcurrentHashMap<>();
    
    // Economy metrics
    private double totalCreditsIssued = 0;
    private double totalCreditsTransferred = 0;
    private long totalTransactions = 0;
    
    // Entropy tracking
    private double systemEntropy = 1.0; // Start at maximum disorder

    public ComputationalEconomy() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         COMPUTATIONAL ECONOMY: PoPW INITIALIZED           ║");
        System.out.println("║         \"Work creates value. Organization is work.\"       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        // Genesis wallet
        wallets.put("FRAYMUS_TREASURY", 1000000.0);
        totalCreditsIssued = 1000000.0;
    }

    /**
     * Skill Shard - A monetizable module/capability
     */
    public static class SkillShard {
        public final String id;
        public final String authorWallet;
        public final String name;
        public final String description;
        public double cpuCostAvg;      // Average CPU time in ms
        public int usageCount;
        public int successCount;
        public double totalEarnings;
        public final long createdAt;
        
        public SkillShard(String id, String author, String name, String desc) {
            this.id = id;
            this.authorWallet = author;
            this.name = name;
            this.description = desc;
            this.cpuCostAvg = 1.0;
            this.usageCount = 0;
            this.successCount = 0;
            this.totalEarnings = 0;
            this.createdAt = System.currentTimeMillis();
        }
        
        public double getSuccessRate() {
            return usageCount > 0 ? (double) successCount / usageCount : 0;
        }
        
        public double getValue() {
            // Skill_Value(s) = Usage_Count × (Success_Rate / CPU_Cost)
            if (cpuCostAvg <= 0) cpuCostAvg = 1.0;
            return usageCount * (getSuccessRate() / cpuCostAvg);
        }
        
        public double getPayoutPerUse() {
            // Base payout scaled by PHI and success rate
            return 0.001 * PHI * getSuccessRate();
        }
    }

    /**
     * Proof of Phi-Work: Calculate work value from entropy reduction
     * 
     * @param entropyBefore System entropy before work
     * @param entropyAfter System entropy after work
     * @param durationMs Time taken in milliseconds
     * @return Credits earned
     */
    public double calculatePhiWork(double entropyBefore, double entropyAfter, long durationMs) {
        // W = ∫ (Entropy_Initial - Entropy_Final) dt
        double entropyReduction = entropyBefore - entropyAfter;
        
        if (entropyReduction <= 0) {
            return 0; // No value for increasing disorder
        }
        
        // Normalize by time (faster = more valuable)
        double timeNormalized = 1000.0 / Math.max(durationMs, 1);
        
        // Apply PHI scaling
        double work = entropyReduction * timeNormalized * PHI;
        
        return Math.max(0, work);
    }

    /**
     * Register a new Skill Shard in the market
     */
    public String registerSkill(String authorWallet, String name, String description) {
        // Create wallet if doesn't exist
        wallets.putIfAbsent(authorWallet, 0.0);
        
        // Generate skill ID
        String id = "SKILL-" + generateHash(name + authorWallet + System.currentTimeMillis());
        
        SkillShard shard = new SkillShard(id, authorWallet, name, description);
        skillMarket.put(id, shard);
        
        System.out.println("  ✓ Skill Registered: " + name);
        System.out.println("    ID: " + id);
        System.out.println("    Author: " + authorWallet);
        
        return id;
    }

    /**
     * Use a skill - tracks usage and pays author
     * 
     * @param skillId The skill to invoke
     * @param success Whether the invocation succeeded
     * @param cpuTimeMs CPU time consumed
     * @return Credits paid to author
     */
    public double useSkill(String skillId, boolean success, long cpuTimeMs) {
        SkillShard shard = skillMarket.get(skillId);
        if (shard == null) {
            System.err.println("  ✗ Unknown skill: " + skillId);
            return 0;
        }
        
        // Update stats
        shard.usageCount++;
        if (success) shard.successCount++;
        
        // Update rolling average CPU cost
        shard.cpuCostAvg = (shard.cpuCostAvg * (shard.usageCount - 1) + cpuTimeMs) / shard.usageCount;
        
        // Calculate payout
        double payout = shard.getPayoutPerUse();
        if (payout > 0) {
            transfer("FRAYMUS_TREASURY", shard.authorWallet, payout);
            shard.totalEarnings += payout;
        }
        
        return payout;
    }

    /**
     * Transfer credits between wallets
     */
    public boolean transfer(String from, String to, double amount) {
        if (amount <= 0) return false;
        
        Double fromBalance = wallets.get(from);
        if (fromBalance == null || fromBalance < amount) {
            return false;
        }
        
        wallets.put(from, fromBalance - amount);
        wallets.merge(to, amount, Double::sum);
        
        totalCreditsTransferred += amount;
        totalTransactions++;
        
        return true;
    }

    /**
     * Issue new credits (mint) - only for verified work
     */
    public void issueCredits(String wallet, double amount, String reason) {
        if (amount <= 0) return;
        
        wallets.merge(wallet, amount, Double::sum);
        totalCreditsIssued += amount;
        
        System.out.println("  💰 Minted: " + String.format("%.6f", amount) + " credits");
        System.out.println("     To: " + wallet);
        System.out.println("     Reason: " + reason);
    }

    /**
     * Perform entropy reduction work and earn credits
     * 
     * @param wallet Worker's wallet
     * @param entropyWork The actual entropy reduction achieved
     * @param durationMs Time taken
     * @return Credits earned
     */
    public double performWork(String wallet, double entropyWork, long durationMs) {
        double oldEntropy = systemEntropy;
        double newEntropy = Math.max(0, systemEntropy - entropyWork);
        
        double credits = calculatePhiWork(oldEntropy, newEntropy, durationMs);
        
        if (credits > 0) {
            systemEntropy = newEntropy;
            issueCredits(wallet, credits, "Entropy Reduction Work");
        }
        
        return credits;
    }

    /**
     * Get wallet balance
     */
    public double getBalance(String wallet) {
        return wallets.getOrDefault(wallet, 0.0);
    }

    /**
     * Get skill market stats
     */
    public String getMarketStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("║              SKILL SHARD MARKET                           ║\n");
        sb.append("╠═══════════════════════════════════════════════════════════╣\n");
        
        for (SkillShard shard : skillMarket.values()) {
            sb.append(String.format("║  %s\n", shard.name));
            sb.append(String.format("║    Uses: %d | Success: %.1f%% | Value: %.4f\n", 
                shard.usageCount, shard.getSuccessRate() * 100, shard.getValue()));
            sb.append(String.format("║    Earnings: %.6f credits\n", shard.totalEarnings));
        }
        
        if (skillMarket.isEmpty()) {
            sb.append("║  (No skills registered yet)\n");
        }
        
        sb.append("╚═══════════════════════════════════════════════════════════╝");
        return sb.toString();
    }

    /**
     * Get economy stats
     */
    public String getStats() {
        return String.format(
            "╔═══════════════════════════════════════════════════════════╗\n" +
            "║              COMPUTATIONAL ECONOMY STATS                  ║\n" +
            "╠═══════════════════════════════════════════════════════════╣\n" +
            "║  Total Credits Issued: %.2f                              \n" +
            "║  Total Transferred: %.6f                                 \n" +
            "║  Transactions: %d                                        \n" +
            "║  System Entropy: %.6f                                    \n" +
            "║  Wallets: %d                                             \n" +
            "║  Skills: %d                                              \n" +
            "╚═══════════════════════════════════════════════════════════╝",
            totalCreditsIssued, totalCreditsTransferred, totalTransactions,
            systemEntropy, wallets.size(), skillMarket.size());
    }

    private String generateHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                hex.append(String.format("%02X", hash[i]));
            }
            return hex.toString();
        } catch (Exception e) {
            return Long.toHexString(System.currentTimeMillis());
        }
    }

    // --- MAIN: TEST HARNESS ---
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         COMPUTATIONAL ECONOMY TEST                        ║");
        System.out.println("║         Proof of Phi-Work                                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
        
        ComputationalEconomy economy = new ComputationalEconomy();
        
        // Create a developer wallet
        String devWallet = "DEV_ALICE_001";
        
        // Register a skill
        System.out.println("\n--- REGISTERING SKILL ---");
        String skillId = economy.registerSkill(devWallet, "ImageClassifier", 
            "Neural network image classification");
        
        // Simulate usage
        System.out.println("\n--- SIMULATING SKILL USAGE ---");
        for (int i = 0; i < 10; i++) {
            boolean success = Math.random() > 0.2; // 80% success rate
            long cpuTime = 50 + (long)(Math.random() * 100);
            double payout = economy.useSkill(skillId, success, cpuTime);
            System.out.printf("  Use %d: %s - Earned: %.6f credits%n", 
                i + 1, success ? "SUCCESS" : "FAIL", payout);
        }
        
        // Perform entropy work
        System.out.println("\n--- PERFORMING PHI-WORK ---");
        String workerWallet = "WORKER_BOB_001";
        double earned = economy.performWork(workerWallet, 0.1, 500);
        System.out.printf("  Bob earned: %.6f credits for entropy reduction%n", earned);
        
        // Check balances
        System.out.println("\n--- WALLET BALANCES ---");
        System.out.printf("  Alice (Developer): %.6f credits%n", economy.getBalance(devWallet));
        System.out.printf("  Bob (Worker): %.6f credits%n", economy.getBalance(workerWallet));
        System.out.printf("  Treasury: %.2f credits%n", economy.getBalance("FRAYMUS_TREASURY"));
        
        System.out.println(economy.getStats());
        System.out.println(economy.getMarketStats());
        
        System.out.println("\n✓ COMPUTATIONAL ECONOMY: OPERATIONAL");
    }
}
