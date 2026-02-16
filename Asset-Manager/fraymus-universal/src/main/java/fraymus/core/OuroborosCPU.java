package fraymus.core;

import java.util.Random;

public class OuroborosCPU {

    public byte[] ram = new byte[1024]; // 1KB of Mutable Reality
    public int pc = 0;                  // The Time Cursor (Program Counter)
    public int acc = 0;                 // Accumulator
    public boolean running = true;

    private final Random rng = new Random();

    // Mutation telemetry
    private int mutationCount = 0;
    private int stagnantCycles = 0;

    // LOAD PROGRAM
    public void flash(byte[] program) {
        System.arraycopy(program, 0, ram, 0, Math.min(program.length, ram.length));
    }

    /**
     * THE TICK: Where Physics meets Logic
     */
    public void tick() {
        if (!running || pc < 0 || pc >= ram.length) {
            running = false;
            return;
        }

        int prevAcc = acc;

        // 1. FETCH
        byte op = ram[pc];

        // 2. EXECUTE (Standard Logic)
        switch (op) {
            case 0x01: // LOAD immediate
                if (pc + 1 < ram.length) acc = ram[++pc] & 0xFF;
                break;
            case 0x02: // STORE (acc -> ram[arg])
                if (pc + 1 < ram.length) {
                    int addr = ram[++pc] & 0xFF;
                    if (addr < ram.length) ram[addr] = (byte) (acc & 0xFF);
                }
                break;
            case 0x03: // ADD immediate
                if (pc + 1 < ram.length) acc += ram[++pc] & 0xFF;
                break;
            case 0x04: // SUB immediate
                if (pc + 1 < ram.length) acc -= ram[++pc] & 0xFF;
                break;
            case 0x05: // JUMP absolute
                if (pc + 1 < ram.length) {
                    pc = ram[pc + 1] & 0xFF;
                    return; // don't increment pc
                }
                break;
            case 0x06: // JUMP IF ACC > 0
                if (pc + 1 < ram.length) {
                    if (acc > 0) {
                        pc = ram[pc + 1] & 0xFF;
                        return;
                    }
                    pc++; // skip arg
                }
                break;
            case (byte) 0xFF: // HALT
                running = false;
                return;
            default:
                break; // NOP
        }

        pc++;

        // 3. THE OUROBOROS CHECK (Self-Reflection)
        if (acc == prevAcc) {
            stagnantCycles++;
        } else {
            stagnantCycles = 0;
        }

        double entropy = calculateSystemEntropy();

        if (entropy > 0.8) {
            mutateFuture();
        }
    }

    /**
     * SELF-SURGERY: The CPU rewrites the code ahead of the cursor.
     */
    private void mutateFuture() {
        int target = (pc + 1) % ram.length;
        mutationCount++;
        System.out.println("   ⚡ ENTROPY SPIKE #" + mutationCount +
                ". MUTATING ADDRESS [0x" + Integer.toHexString(target) + "]");

        // Pick a constructive opcode + operand pair
        // Bias toward LOAD <nonzero> or ADD <nonzero> to escape stagnation
        int roll = rng.nextInt(10);
        if (roll < 4) {
            // LOAD <random nonzero>
            ram[target] = 0x01;
            if (target + 1 < ram.length) ram[target + 1] = (byte) (1 + rng.nextInt(127));
        } else if (roll < 8) {
            // ADD <random nonzero>
            ram[target] = 0x03;
            if (target + 1 < ram.length) ram[target + 1] = (byte) (1 + rng.nextInt(63));
        } else {
            // HALT (let the organism rest)
            ram[target] = (byte) 0xFF;
        }
    }

    private double calculateSystemEntropy() {
        // Stagnation = high entropy
        if (stagnantCycles >= 3) return 0.9;
        if (acc == 0) return 0.85;
        return 0.1;
    }

    public int getMutationCount() {
        return mutationCount;
    }
}
