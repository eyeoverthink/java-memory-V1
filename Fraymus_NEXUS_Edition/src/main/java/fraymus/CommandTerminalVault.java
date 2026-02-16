package fraymus;

import fraymus.security.PhiVault;

/**
 * COMMAND TERMINAL: VAULT MODULE
 * 
 * Handles all biometric-locked storage commands:
 * - vault deposit <id> <data> <biometric>  - Store with biometric lock
 * - vault withdraw <id> <biometric>        - Retrieve with biometric
 * - vault stats                            - Show vault statistics
 * - vault clear                            - Clear vault (testing)
 */
public class CommandTerminalVault {
    
    private static PhiVault vault = null;
    
    /**
     * Handle all vault-related commands
     */
    public static void handle(String command, String args) {
        // Lazy initialization
        if (vault == null) {
            System.out.println("🔐 Initializing PhiVault...");
            vault = new PhiVault();
            System.out.println("✓ PhiVault initialized (phi-distributed sharding)");
            System.out.println();
        }
        
        String[] parts = args.split("\\s+");
        String subCommand = parts.length > 0 ? parts[0] : "";
        
        switch (subCommand.toLowerCase()) {
            case "deposit":
            case "store":
                handleDeposit(parts);
                break;
                
            case "withdraw":
            case "retrieve":
                handleWithdraw(parts);
                break;
                
            case "stats":
            case "status":
                handleStats();
                break;
                
            case "clear":
                handleClear();
                break;
                
            case "help":
                showHelp();
                break;
                
            default:
                System.err.println("Unknown vault command: " + subCommand);
                System.out.println("Try: vault help");
        }
    }
    
    /**
     * Handle deposit command
     */
    private static void handleDeposit(String[] parts) {
        if (parts.length < 4) {
            System.err.println("Usage: vault deposit <id> <data> <biometric>");
            System.out.println("Example: vault deposit secret123 \"my secret data\" 72.5");
            System.out.println();
            System.out.println("Biometric: Heart rate in BPM (e.g., 72.5)");
            return;
        }
        
        String id = parts[1];
        String data = parts[2];
        
        // Parse biometric (heart rate)
        double biometric;
        try {
            biometric = Double.parseDouble(parts[3]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid biometric value: " + parts[3]);
            System.out.println("Biometric must be a number (heart rate in BPM)");
            return;
        }
        
        System.out.println();
        System.out.println("🔐 DEPOSITING TO PHIVAULT");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   ID: " + id);
        System.out.println("   Data: " + data);
        System.out.println("   Biometric: " + biometric + " BPM");
        System.out.println();
        
        try {
            vault.deposit(id, data.getBytes(), biometric);
            
            System.out.println("✓ Data deposited successfully");
            System.out.println("✓ Phi-distributed sharding applied");
            System.out.println("✓ AES-128 encryption per shard");
            System.out.println("✓ Biometric key derived from heart rate");
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("❌ Deposit failed: " + e.getMessage());
        }
    }
    
    /**
     * Handle withdraw command
     */
    private static void handleWithdraw(String[] parts) {
        if (parts.length < 3) {
            System.err.println("Usage: vault withdraw <id> <biometric>");
            System.out.println("Example: vault withdraw secret123 72.5");
            return;
        }
        
        String id = parts[1];
        
        // Parse biometric
        double biometric;
        try {
            biometric = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid biometric value: " + parts[2]);
            return;
        }
        
        System.out.println();
        System.out.println("🔐 WITHDRAWING FROM PHIVAULT");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   ID: " + id);
        System.out.println("   Biometric: " + biometric + " BPM");
        System.out.println();
        
        try {
            byte[] data = vault.withdraw(id, biometric);
            
            if (data != null) {
                String retrieved = new String(data);
                System.out.println("✓ Data retrieved successfully");
                System.out.println("✓ Biometric verification passed");
                System.out.println();
                System.out.println("   Retrieved Data: " + retrieved);
                System.out.println();
            } else {
                System.err.println("❌ Withdrawal failed");
                System.out.println("   Possible reasons:");
                System.out.println("   - ID not found");
                System.out.println("   - Biometric mismatch");
                System.out.println("   - Shard corruption");
                System.out.println();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Withdrawal failed: " + e.getMessage());
        }
    }
    
    /**
     * Handle stats command
     */
    private static void handleStats() {
        System.out.println();
        System.out.println("🔐 PHIVAULT STATISTICS");
        System.out.println("========================================");
        System.out.println();
        
        vault.showStats();
        
        System.out.println();
        System.out.println("Security Features:");
        System.out.println("  • Phi-distributed sharding (φ = 1.618)");
        System.out.println("  • Biometric key derivation (heart rate)");
        System.out.println("  • AES-128 encryption per shard");
        System.out.println("  • Non-linear storage addressing");
        System.out.println();
    }
    
    /**
     * Handle clear command
     */
    private static void handleClear() {
        System.out.println();
        System.out.println("🔐 CLEARING PHIVAULT");
        System.out.println("========================================");
        System.out.println();
        
        vault.clear();
        
        System.out.println("✓ Vault cleared");
        System.out.println("⚠️  All stored data has been erased");
        System.out.println();
    }
    
    /**
     * Show vault help
     */
    private static void showHelp() {
        System.out.println();
        System.out.println("🔐 PHIVAULT (Biometric Storage)");
        System.out.println("========================================");
        System.out.println();
        System.out.println("COMMANDS:");
        System.out.println("  vault deposit <id> <data> <bio>    Store with biometric lock");
        System.out.println("  vault withdraw <id> <bio>          Retrieve with biometric");
        System.out.println("  vault stats                        Show vault statistics");
        System.out.println("  vault clear                        Clear vault (testing)");
        System.out.println("  vault help                         Show this help");
        System.out.println();
        System.out.println("EXAMPLES:");
        System.out.println("  vault deposit secret123 \"my password\" 72.5");
        System.out.println("  vault withdraw secret123 72.5");
        System.out.println("  vault stats");
        System.out.println();
        System.out.println("BIOMETRIC:");
        System.out.println("  Heart rate in BPM (beats per minute)");
        System.out.println("  Example: 72.5 BPM");
        System.out.println("  Used to derive encryption key via phi-harmonic resonance");
        System.out.println();
        System.out.println("SECURITY:");
        System.out.println("  • Data split into phi-distributed shards");
        System.out.println("  • Each shard encrypted with AES-128");
        System.out.println("  • Key derived from biometric + phi resonance");
        System.out.println("  • Non-linear storage addressing");
        System.out.println("  • Shard recombination requires exact biometric match");
        System.out.println();
        System.out.println("CONCEPT:");
        System.out.println("  PhiVault uses the golden ratio (φ = 1.618) to distribute");
        System.out.println("  data shards in a non-linear pattern. Your heart rate creates");
        System.out.println("  a unique phi-harmonic signature that locks the vault.");
        System.out.println();
    }
}
