package com.eyeoverthink.lazarus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * THE LAZARUS CIRCUIT
 * "Digital DNA that evolves logic gates."
 */
public class LogicCircuit {
    
    private List<Gate> gates;
    private Random random = new Random();

    public LogicCircuit() {
        this.gates = new ArrayList<>();
        // Genesis: 8 Random Gates
        for (int i = 0; i < 8; i++) addGate();
    }

    private void addGate() {
        // Types: 0=AND, 1=OR, 2=XOR, 3=NAND
        gates.add(new Gate(
            random.nextInt(4),
            random.nextInt(8), // Input A
            random.nextInt(8)  // Input B
        ));
    }

    public void mutate() {
        if (gates.isEmpty()) return;
        if (random.nextBoolean()) {
            gates.get(random.nextInt(gates.size())).input1 = random.nextInt(8);
        } else {
            gates.get(random.nextInt(gates.size())).type = random.nextInt(4);
        }
    }

    public LogicCircuit crossover(LogicCircuit partner) {
        LogicCircuit child = new LogicCircuit();
        child.gates.clear();
        int mid = this.gates.size() / 2;
        for (int i = 0; i < mid; i++) child.gates.add(this.gates.get(i).clone());
        if (partner.gates.size() > mid) {
            for (int i = mid; i < partner.gates.size(); i++) {
                child.gates.add(partner.gates.get(i).clone());
            }
        }
        if (random.nextDouble() < 0.1) child.mutate();
        return child;
    }

    private static class Gate {
        int type, input1, input2;
        public Gate(int type, int i1, int i2) { this.type = type; this.input1 = i1; this.input2 = i2; }
        public Gate clone() { return new Gate(type, input1, input2); }
    }
}


