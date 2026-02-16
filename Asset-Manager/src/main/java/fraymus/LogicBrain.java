package fraymus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LogicBrain {
    private static final Random rng = new Random();

    public static final int SEEK = 0;
    public static final int FLEE = 1;
    public static final int REPRODUCE = 2;
    public static final int MUTATE = 3;
    public static final int CONSERVE = 4;
    public static final int ENTANGLE_SEEK = 5;
    public static final int ENERGY_BURST = 6;
    public static final int EVOLVE_DNA = 7;

    public static final String[] OUTPUT_NAMES = {
            "SEEK", "FLEE", "REPRODUCE", "MUTATE",
            "CONSERVE", "ENTANGLE_SEEK", "ENERGY_BURST", "EVOLVE_DNA"
    };

    public List<LogicGate> gates;
    private int[] lastOutputs;
    private int[] lastInputs;
    private int thinkCount;
    private String lastDecision;

    public LogicBrain(int numGates) {
        gates = new ArrayList<>();
        for (int i = 0; i < Math.max(numGates, 8); i++) {
            gates.add(LogicGate.random());
        }
        lastOutputs = new int[gates.size()];
        lastInputs = new int[0];
        thinkCount = 0;
        lastDecision = "IDLE";
    }

    public LogicBrain() {
        this(8);
    }

    private LogicBrain(List<LogicGate> gates) {
        this.gates = gates;
        this.lastOutputs = new int[gates.size()];
        this.lastInputs = new int[0];
        this.thinkCount = 0;
        this.lastDecision = "IDLE";
    }

    public int[] compute(int[] inputs) {
        this.lastInputs = inputs.clone();
        int[] outputs = new int[gates.size()];
        for (int i = 0; i < gates.size(); i++) {
            outputs[i] = gates.get(i).compute(inputs);
        }
        this.lastOutputs = outputs;
        thinkCount++;
        return outputs;
    }

    public static int[] buildSensorInputs(
            int nearbyCount,
            float avgFreqDiff,
            float energy,
            float phiResonance,
            float coherence,
            float phase,
            boolean spikeActive,
            int age
    ) {
        int[] inputs = new int[8];
        inputs[0] = nearbyCount > 2 ? 1 : 0;
        inputs[1] = avgFreqDiff < 5.0f ? 1 : 0;
        inputs[2] = energy > 0.7f ? 1 : 0;
        inputs[3] = phiResonance > 0.8f ? 1 : 0;
        inputs[4] = coherence > 0.9f ? 1 : 0;
        inputs[5] = phase > (float) Math.PI ? 1 : 0;
        inputs[6] = spikeActive ? 1 : 0;
        inputs[7] = age > 500 ? 1 : 0;
        return inputs;
    }

    public String interpretOutputs(int[] outputs) {
        StringBuilder sb = new StringBuilder();
        int activeCount = 0;
        for (int i = 0; i < Math.min(outputs.length, OUTPUT_NAMES.length); i++) {
            if (outputs[i] == 1) {
                if (activeCount > 0) sb.append("+");
                sb.append(OUTPUT_NAMES[i]);
                activeCount++;
            }
        }
        if (activeCount == 0) sb.append("IDLE");
        lastDecision = sb.toString();
        return lastDecision;
    }

    public boolean wantsToSeek(int[] outputs) { return outputs.length > SEEK && outputs[SEEK] == 1; }
    public boolean wantsToFlee(int[] outputs) { return outputs.length > FLEE && outputs[FLEE] == 1; }
    public boolean wantsToReproduce(int[] outputs) { return outputs.length > REPRODUCE && outputs[REPRODUCE] == 1; }
    public boolean wantsToMutate(int[] outputs) { return outputs.length > MUTATE && outputs[MUTATE] == 1; }
    public boolean wantsToConserve(int[] outputs) { return outputs.length > CONSERVE && outputs[CONSERVE] == 1; }
    public boolean wantsEntangleSeek(int[] outputs) { return outputs.length > ENTANGLE_SEEK && outputs[ENTANGLE_SEEK] == 1; }
    public boolean wantsEnergyBurst(int[] outputs) { return outputs.length > ENERGY_BURST && outputs[ENERGY_BURST] == 1; }
    public boolean wantsEvolveDNA(int[] outputs) { return outputs.length > EVOLVE_DNA && outputs[EVOLVE_DNA] == 1; }

    public void mutate() {
        if (!gates.isEmpty()) {
            LogicGate gate = gates.get(rng.nextInt(gates.size()));
            gate.mutate();
        }
    }

    public LogicBrain crossover(LogicBrain partner) {
        List<LogicGate> childGates = new ArrayList<>();
        int mid = gates.size() / 2;

        for (int i = 0; i < mid && i < gates.size(); i++) {
            childGates.add(gates.get(i).copy());
        }
        for (int i = mid; i < partner.gates.size(); i++) {
            childGates.add(partner.gates.get(i).copy());
        }

        while (childGates.size() < 8) {
            childGates.add(LogicGate.random());
        }

        LogicBrain child = new LogicBrain(childGates);
        if (rng.nextDouble() < 0.1) {
            child.mutate();
        }
        return child;
    }

    public int[] getLastOutputs() { return lastOutputs; }
    public int[] getLastInputs() { return lastInputs; }
    public int getThinkCount() { return thinkCount; }
    public String getLastDecision() { return lastDecision; }

    public String toJavaCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("new LogicGate[] {\n");
        for (int i = 0; i < gates.size(); i++) {
            LogicGate g = gates.get(i);
            sb.append(String.format("            new LogicGate(%d, %d, %d)", g.type, g.in1, g.in2));
            if (i < gates.size() - 1) sb.append(",");
            sb.append(" // ").append(g.getTypeName());
            if (i < OUTPUT_NAMES.length) sb.append(" -> ").append(OUTPUT_NAMES[i]);
            sb.append("\n");
        }
        sb.append("        }");
        return sb.toString();
    }

    public int getGateCount() {
        return gates.size();
    }

    public String encode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < gates.size(); i++) {
            sb.append(gates.get(i).encode());
            if (i < gates.size() - 1) sb.append("|");
        }
        return sb.toString();
    }

    public static LogicBrain decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) return new LogicBrain();
        String[] parts = encoded.split("\\|");
        List<LogicGate> gateList = new ArrayList<>();
        for (String part : parts) {
            gateList.add(LogicGate.decode(part));
        }
        return new LogicBrain(gateList);
    }

    @Override
    public String toString() {
        return String.format("Brain[%d gates, thinks=%d, decision=%s]", gates.size(), thinkCount, lastDecision);
    }
}
