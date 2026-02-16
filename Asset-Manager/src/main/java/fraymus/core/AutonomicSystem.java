package fraymus.core;

import com.eyeoverthink.lazarus.LazarusEngine;
import fraymus.quantum.evolution.WarriorNFT;
import fraymus.quantum.evolution.BattleArena;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * THE AUTONOMIC NERVOUS SYSTEM
 * "The bridge between the Evolutionary Brain (Lazarus) and the Physical Body (Fraymus)."
 * Runs background evolution and battle cycles.
 */
public class AutonomicSystem {

    private final LazarusEngine evolutionEngine;
    private final BattleArena arena;
    private final ScheduledExecutorService heartbeat;
    private volatile boolean running = false;

    public AutonomicSystem() {
        this.evolutionEngine = new LazarusEngine();
        this.arena = new BattleArena();
        this.heartbeat = Executors.newSingleThreadScheduledExecutor();
        System.out.println("🫀 AUTONOMIC SYSTEM INITIALIZED.");
    }

    public void ignite() {
        if (running) return;
        running = true;
        
        // Start the Brain
        evolutionEngine.startLife();
        
        // Recruit initial warriors
        arena.recruitWarrior(WarriorNFT.WarriorType.BLUE_DEFENDER, WarriorNFT.WarriorClass.GOLD_GUARDIAN);
        arena.recruitWarrior(WarriorNFT.WarriorType.RED_ATTACKER, WarriorNFT.WarriorClass.LOKI_BREAKER);
        
        // Start background battle loop
        heartbeat.scheduleAtFixedRate(this::regulate, 1, 10, TimeUnit.SECONDS);
        System.out.println("🫀 AUTONOMIC SYSTEM RUNNING.");
    }

    private void regulate() {
        try {
            // Run an auto-battle
            BattleArena.BattleResult result = arena.autoBattle();
            System.out.println("   ⚔️ " + result);
            
            // Occasionally recruit new warriors
            if (Math.random() < 0.2) {
                if (Math.random() < 0.5) {
                    arena.recruitWarrior(WarriorNFT.WarriorType.BLUE_DEFENDER, 
                        WarriorNFT.WarriorClass.values()[(int)(Math.random() * 3)]);
                } else {
                    arena.recruitWarrior(WarriorNFT.WarriorType.RED_ATTACKER,
                        WarriorNFT.WarriorClass.values()[3 + (int)(Math.random() * 3)]);
                }
            }
            
        } catch (Exception e) {
            System.out.println("   !! ERROR: " + e.getMessage());
        }
    }
    
    public void shutdown() {
        running = false;
        heartbeat.shutdown();
    }
    
    public BattleArena getArena() {
        return arena;
    }
    
    public String getStatus() {
        return arena.getStatus();
    }
}