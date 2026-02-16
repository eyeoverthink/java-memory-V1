package fraymus;

import fraymus.core.OuroborosCPU;

public class TheSingularity {

    public static void main(String[] args) {
        System.out.println("🌀 INITIATING OUROBOROS PROTOCOL...");
        System.out.println("   Loading broken genome (infinite stagnation loop)...\n");

        OuroborosCPU cpu = new OuroborosCPU();

        // 1. THE BAD GENOME
        // LOAD 0, ADD 0, JMP 0 (Infinite Loop of Nothing)
        byte[] brokenDna = {
            0x01, 0x00,       // LOAD 0
            0x03, 0x00,       // ADD 0
            0x05, 0x00        // JMP 0
        };

        cpu.flash(brokenDna);

        // 2. THE EVOLUTION
        int cycles = 0;
        while (cpu.running && cycles < 50) {
            System.out.printf("TICK %2d | PC: 0x%02X | ACC: %3d | OP: 0x%02X",
                    cycles, cpu.pc, cpu.acc, cpu.ram[cpu.pc] & 0xFF);

            cpu.tick();

            System.out.println();
            cycles++;

            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }

        System.out.println();
        System.out.println("═══════════════════════════════════════");
        System.out.println("🌀 CYCLES RUN:    " + cycles);
        System.out.println("🧬 MUTATIONS:     " + cpu.getMutationCount());
        System.out.println("🧠 FINAL ACC:     " + cpu.acc);
        System.out.println("🌀 SYSTEM STATE:  " + (cpu.acc > 0 ? "✅ EVOLVED" : "❌ STAGNANT"));
        System.out.println("═══════════════════════════════════════");
    }
}
