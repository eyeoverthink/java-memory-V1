package fraymus.nano;

import fraymus.limbs.ClawConnector;
import fraymus.core.PhiSuit;
import java.io.File;
import java.nio.file.Files;

/**
 * NANO-AGENT - Microscopic AI
 * 
 * Lives on a single file. Has a "Health" meter (Entropy).
 * If health drops, performs surgery on its host file.
 * 
 * This is autonomous file maintenance.
 */
public class NanoAgent extends PhiSuit<File> implements Runnable {

    private final ClawConnector claw;
    private final double replicationThreshold = 80.0; // If work is hard, clone self
    private boolean isDormant = true;
    private final long sleepInterval = 1000; // 1 second

    public NanoAgent(File hostFile, int x, int y, int z) {
        super(hostFile, x, y, z, "NANO_" + hostFile.getName());
        this.claw = new ClawConnector();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 1. SENSE: Check the host file's health
                double entropy = measureEntropy();
                
                // 2. PHYSICS: High Entropy generates Heat
                this.a = (int) Math.min(100, entropy); 
                
                if (entropy > 50.0 && isDormant) {
                    wakeUp();
                }

                // 3. REPLICATION: If too much work, spawn help
                if (this.a > 90) {
                    replicate();
                }

                Thread.sleep(sleepInterval);
            } catch (Exception e) {
                System.err.println("NANO ERROR: " + this.getLabel() + " - " + e.getMessage());
            }
        }
    }

    private void wakeUp() {
        this.isDormant = false;
        System.out.println("⚠️ NANO ACTIVATION: " + this.getLabel() + " detected corruption.");
        
        // 4. ACTION: Call OpenClaw to fix the file
        String source = readHost();
        if (source == null || source.isEmpty()) {
            System.err.println("   > Cannot read host file");
            this.isDormant = true;
            return;
        }
        
        String prompt = "You are a Nano-Doctor. This file has high entropy (bad code). Fix it.\n" + source;
        String fix = claw.dispatch(prompt, "CONTEXT: NANO_REPAIR");
        
        if (fix.contains("class ") || fix.contains("public ")) {
             writeHost(fix);
             System.out.println("✅ NANO REPAIR COMPLETE: " + this.getLabel());
             this.a = 0; // Cooldown
        }
        this.isDormant = true;
    }

    private void replicate() {
        System.out.println("🧬 MITOSIS: " + this.getLabel() + " is splitting...");
        // Logic to spawn a new NanoAgent in the main engine at a nearby coordinate
        // This would require access to the parent GravityEngine
        // For now, just log the intent
        this.a = 50; // Reduce heat after attempting replication
    }

    private double measureEntropy() {
        String source = readHost();
        if (source == null) return 0;
        
        double entropy = 0;
        
        // Simple heuristics for code quality
        if (source.contains("for (")) entropy += 10;
        if (source.contains("while (")) entropy += 10;
        if (source.contains("Thread.sleep")) entropy += 30;
        if (source.contains("TODO")) entropy += 20;
        if (source.contains("FIXME")) entropy += 40;
        if (source.contains("System.out.println") && source.split("System.out.println").length > 5) {
            entropy += 15; // Too much debug logging
        }
        
        // Check for nested loops
        int forCount = countOccurrences(source, "for (");
        if (forCount > 2) entropy += 20;
        
        return entropy;
    }

    private String readHost() {
        try {
            File host = this.get();
            if (host == null || !host.exists()) return null;
            return Files.readString(host.toPath());
        } catch (Exception e) {
            System.err.println("NANO READ ERROR: " + e.getMessage());
            return null;
        }
    }

    private void writeHost(String newCode) {
        try {
            File host = this.get();
            if (host == null) return;
            
            // Backup first
            File backup = new File(host.getAbsolutePath() + ".nano.bak");
            Files.copy(host.toPath(), backup.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            // Write new code
            Files.writeString(host.toPath(), newCode);
        } catch (Exception e) {
            System.err.println("NANO WRITE ERROR: " + e.getMessage());
        }
    }

    private int countOccurrences(String str, String substr) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(substr, idx)) != -1) {
            count++;
            idx += substr.length();
        }
        return count;
    }
}
