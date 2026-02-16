package fraymus.quantum.brain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Strategy Genome - Brain configuration snapshot for adaptive evolution
 * 
 * Stores the complete gate configuration of a LogicBrain,
 * allowing fitness tracking, comparison, and restoration.
 */
public class StrategyGenome {

    public final int[] gateTypes;
    public final int[] input1s;
    public final int[] input2s;
    public double fitnessScore;
    public int generationBorn;
    public int survivedTicks;
    public int reproductionCount;
    public String hash;

    public StrategyGenome(LogicBrain brain, int generation) {
        List<LogicGate> gates = brain.getGates();
        int n = gates.size();
        this.gateTypes = new int[n];
        this.input1s = new int[n];
        this.input2s = new int[n];
        for (int i = 0; i < n; i++) {
            LogicGate g = gates.get(i);
            gateTypes[i] = g.type;
            input1s[i] = g.in1;
            input2s[i] = g.in2;
        }
        this.fitnessScore = 0;
        this.generationBorn = generation;
        this.survivedTicks = 0;
        this.reproductionCount = 0;
        this.hash = computeHash();
    }

    private StrategyGenome(int[] types, int[] in1, int[] in2, double fitness, int gen) {
        this.gateTypes = types.clone();
        this.input1s = in1.clone();
        this.input2s = in2.clone();
        this.fitnessScore = fitness;
        this.generationBorn = gen;
        this.survivedTicks = 0;
        this.reproductionCount = 0;
        this.hash = computeHash();
    }

    private String computeHash() {
        String input = Arrays.toString(gateTypes) + Arrays.toString(input1s) + Arrays.toString(input2s);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                hex.append(String.format("%02x", digest[i]));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return "00000000";
        }
    }

    public void applyTo(LogicBrain brain) {
        List<LogicGate> gates = brain.getGates();
        int n = Math.min(gateTypes.length, gates.size());
        for (int i = 0; i < n; i++) {
            LogicGate g = gates.get(i);
            g.type = gateTypes[i];
            g.in1 = input1s[i];
            g.in2 = input2s[i];
        }
    }

    public double similarity(StrategyGenome other) {
        int matches = 0;
        int total = Math.min(gateTypes.length, other.gateTypes.length);
        for (int i = 0; i < total; i++) {
            if (gateTypes[i] == other.gateTypes[i]) matches++;
            if (input1s[i] == other.input1s[i]) matches++;
            if (input2s[i] == other.input2s[i]) matches++;
        }
        return (double) matches / (total * 3);
    }

    public StrategyGenome copy() {
        StrategyGenome sg = new StrategyGenome(gateTypes, input1s, input2s, fitnessScore, generationBorn);
        sg.survivedTicks = this.survivedTicks;
        sg.reproductionCount = this.reproductionCount;
        return sg;
    }

    public String encode() {
        StringBuilder sb = new StringBuilder();
        sb.append("SG|");
        for (int i = 0; i < gateTypes.length; i++) {
            sb.append(gateTypes[i]).append(",");
            sb.append(input1s[i]).append(",");
            sb.append(input2s[i]);
            if (i < gateTypes.length - 1) sb.append(";");
        }
        sb.append("|").append(String.format("%.4f", fitnessScore));
        sb.append("|").append(generationBorn);
        return sb.toString();
    }

    public static StrategyGenome decode(String encoded) {
        try {
            String[] parts = encoded.split("\\|");
            if (parts.length < 4 || !parts[0].equals("SG")) return null;
            String[] gateParts = parts[1].split(";");
            int n = gateParts.length;
            int[] types = new int[n];
            int[] in1 = new int[n];
            int[] in2 = new int[n];
            for (int i = 0; i < n; i++) {
                String[] vals = gateParts[i].split(",");
                types[i] = Integer.parseInt(vals[0]);
                in1[i] = Integer.parseInt(vals[1]);
                in2[i] = Integer.parseInt(vals[2]);
            }
            double fitness = Double.parseDouble(parts[2]);
            int gen = Integer.parseInt(parts[3]);
            return new StrategyGenome(types, in1, in2, fitness, gen);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("Strategy[hash=%s, fitness=%.3f, gen=%d, survived=%d, reproduced=%d]",
                hash.substring(0, 8), fitnessScore, generationBorn, survivedTicks, reproductionCount);
    }
}
