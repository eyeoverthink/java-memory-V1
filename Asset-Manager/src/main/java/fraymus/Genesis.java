package fraymus;

import fraymus.quantum.evolution.WarriorNFT;
import fraymus.quantum.evolution.BattleArena;

/**
 * GENESIS - Birth of Fraymus
 * Uses existing WarriorNFT and BattleArena systems
 */
public class Genesis {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   FRAYMUS PRIME: INITIALIZATION SEQUENCE");
        System.out.println("==========================================");

        // 1. CREATE THE BATTLE ARENA
        BattleArena arena = new BattleArena();
        System.out.println("   ⚔️ ARENA INITIALIZED");

        // 2. RECRUIT WARRIORS
        WarriorNFT blueGuardian = arena.recruitWarrior(
            WarriorNFT.WarriorType.BLUE_DEFENDER,
            WarriorNFT.WarriorClass.GOLD_GUARDIAN
        );
        System.out.println("   🔵 BLUE RECRUITED: " + blueGuardian.getId());

        WarriorNFT redAttacker = arena.recruitWarrior(
            WarriorNFT.WarriorType.RED_ATTACKER,
            WarriorNFT.WarriorClass.LOKI_BREAKER
        );
        System.out.println("   � RED RECRUITED: " + redAttacker.getId());

        // 3. BATTLE!
        System.out.println("\n" + "=".repeat(42));
        System.out.println("   ⚔️ FIRST BATTLE: BLUE vs RED");
        System.out.println("=".repeat(42));
        
        BattleArena.BattleResult result = arena.battle(blueGuardian, redAttacker);
        System.out.println(result.getFullReport());

        // 4. SHOW ARENA STATUS
        System.out.println(arena.getStatus());

        // 5. RUN 5 MORE AUTO-BATTLES
        System.out.println("\n" + "=".repeat(42));
        System.out.println("   🔄 RUNNING 5 AUTO-BATTLES...");
        System.out.println("=".repeat(42));
        
        for (int i = 0; i < 5; i++) {
            BattleArena.BattleResult auto = arena.autoBattle();
            System.out.println("   " + auto);
        }

        // 6. FINAL STATUS
        System.out.println("\n" + arena.getStatus());
        
        // 7. EXPORT DNA
        System.out.println("\n" + "=".repeat(42));
        System.out.println("   📦 DNA EXPORT");
        System.out.println("=".repeat(42));
        System.out.println(arena.exportAllDNA());

        System.out.println("\n   ✅ GENESIS COMPLETE.");
    }
}
