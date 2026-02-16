package fraymus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class CodeConcept {

    private static final Random rng = new Random();
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    private static int globalId = 0;

    public final int id;
    public final String creatorName;
    public final AntRole creatorRole;
    public String code;
    public int generation;

    public double harmonicFrequency;
    public double resonance;
    public double coherence;
    public double complexity;

    public double correctness;
    public double efficiency;
    public double elegance;
    public double fitness;

    public int wins;
    public int losses;
    public int battles;
    public long createdTick;

    public String hash;

    public CodeConcept(String creatorName, AntRole creatorRole, String code, int generation, long tick) {
        this.id = globalId++;
        this.creatorName = creatorName;
        this.creatorRole = creatorRole;
        this.code = code;
        this.generation = generation;
        this.createdTick = tick;

        this.harmonicFrequency = 432.0 + rng.nextDouble() * 96.0;
        this.resonance = 0.5 + rng.nextDouble() * 0.5;
        this.coherence = 0.3 + rng.nextDouble() * 0.7;
        this.complexity = evaluateComplexity(code);

        evaluateQualities();
        this.fitness = computePhiFitness();
        this.hash = computeHash();

        this.wins = 0;
        this.losses = 0;
        this.battles = 0;
    }

    private CodeConcept(CodeConcept parent) {
        this.id = globalId++;
        this.creatorName = parent.creatorName;
        this.creatorRole = parent.creatorRole;
        this.code = parent.code;
        this.generation = parent.generation + 1;
        this.createdTick = parent.createdTick;

        this.harmonicFrequency = parent.harmonicFrequency;
        this.resonance = parent.resonance;
        this.coherence = parent.coherence;
        this.complexity = parent.complexity;

        this.correctness = parent.correctness;
        this.efficiency = parent.efficiency;
        this.elegance = parent.elegance;
        this.fitness = parent.fitness;

        this.wins = 0;
        this.losses = 0;
        this.battles = 0;

        this.hash = computeHash();
    }

    private double evaluateComplexity(String code) {
        if (code == null || code.isEmpty()) return 0;

        double length = code.length();
        int operators = 0;
        int controlFlow = 0;
        int functions = 0;

        for (char c : code.toCharArray()) {
            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '&' || c == '|' || c == '^') operators++;
            if (c == '{' || c == '}') controlFlow++;
        }

        if (code.contains("for")) functions++;
        if (code.contains("while")) functions++;
        if (code.contains("if")) functions++;
        if (code.contains("return")) functions++;
        if (code.contains("switch")) functions += 2;

        return Math.min(1.0, (operators * 0.05 + controlFlow * 0.1 + functions * 0.15 + length * 0.002));
    }

    private void evaluateQualities() {
        correctness = 0.5 + (resonance * 0.3) + (coherence * 0.2);
        correctness = Math.min(1.0, correctness);

        efficiency = complexity > 0 ? Math.min(1.0, (1.0 - complexity * 0.3) + resonance * 0.4) : 0.5;

        double uniqueChars = code != null ? (double) code.chars().distinct().count() / Math.max(1, code.length()) : 0;
        elegance = 0.3 + uniqueChars * 0.3 + (1.0 - Math.abs(complexity - 0.5)) * 0.4;
        elegance = Math.min(1.0, elegance);
    }

    public double computePhiFitness() {
        double phiFitness = (correctness * PHI) + (efficiency * (1.0 / PHI)) + (elegance * PHI * PHI);
        double normalizer = PHI + (1.0 / PHI) + (PHI * PHI);
        fitness = phiFitness / normalizer;

        double battleBonus = battles > 0 ? (double) wins / battles * 0.1 : 0;
        fitness = Math.min(1.0, fitness + battleBonus);

        return fitness;
    }

    public boolean battle(CodeConcept opponent) {
        this.battles++;
        opponent.battles++;

        double myScore = this.fitness * (0.9 + rng.nextDouble() * 0.2);
        double theirScore = opponent.fitness * (0.9 + rng.nextDouble() * 0.2);

        double freqBonus = Math.abs(this.harmonicFrequency - 480.0) < Math.abs(opponent.harmonicFrequency - 480.0) ? 0.05 : -0.05;
        myScore += freqBonus;

        if (myScore >= theirScore) {
            this.wins++;
            opponent.losses++;
            this.resonance = Math.min(1.0, this.resonance + 0.02);
            opponent.resonance = Math.max(0.0, opponent.resonance - 0.01);
            return true;
        } else {
            this.losses++;
            opponent.wins++;
            opponent.resonance = Math.min(1.0, opponent.resonance + 0.02);
            this.resonance = Math.max(0.0, this.resonance - 0.01);
            return false;
        }
    }

    public CodeConcept mutate() {
        CodeConcept child = new CodeConcept(this);

        if (code != null && code.length() > 10) {
            char[] chars = code.toCharArray();
            int pos = rng.nextInt(chars.length);
            if (Character.isLetter(chars[pos])) {
                chars[pos] = (char) ('a' + rng.nextInt(26));
            }
            child.code = new String(chars);
        }

        child.harmonicFrequency += (rng.nextDouble() - 0.5) * 10.0;
        child.harmonicFrequency = Math.max(432, Math.min(528, child.harmonicFrequency));

        child.resonance += (rng.nextDouble() - 0.5) * 0.1;
        child.resonance = Math.max(0, Math.min(1, child.resonance));

        child.coherence += (rng.nextDouble() - 0.5) * 0.1;
        child.coherence = Math.max(0, Math.min(1, child.coherence));

        child.complexity = child.evaluateComplexity(child.code);
        child.evaluateQualities();
        child.fitness = child.computePhiFitness();
        child.hash = child.computeHash();

        return child;
    }

    public static CodeConcept crossover(CodeConcept a, CodeConcept b) {
        CodeConcept child = new CodeConcept(a);
        child.generation = Math.max(a.generation, b.generation) + 1;

        if (a.code != null && b.code != null) {
            int splitA = a.code.length() / 2;
            int splitB = b.code.length() / 2;
            child.code = a.code.substring(0, splitA) + b.code.substring(splitB);
        }

        child.harmonicFrequency = (a.harmonicFrequency + b.harmonicFrequency) / 2.0;
        child.resonance = (a.resonance * a.fitness + b.resonance * b.fitness) / (a.fitness + b.fitness + 0.001);
        child.coherence = Math.max(a.coherence, b.coherence);

        child.complexity = child.evaluateComplexity(child.code);
        child.evaluateQualities();
        child.fitness = child.computePhiFitness();
        child.hash = child.computeHash();

        return child;
    }

    private String computeHash() {
        String input = id + code + harmonicFrequency + resonance + generation;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                hex.append(String.format("%02x", digest[i]));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.format("%016x", input.hashCode());
        }
    }

    public String getStatusLine() {
        return String.format("[%s] Gen%d fit=%.3f W:%d/L:%d %s by %s",
            hash.substring(0, 8), generation, fitness, wins, losses,
            creatorRole.displayName, creatorName);
    }

    @Override
    public String toString() {
        return String.format("Concept{id=%d, role=%s, gen=%d, fit=%.4f, freq=%.1f, battles=%d, W/L=%d/%d}",
            id, creatorRole, generation, fitness, harmonicFrequency, battles, wins, losses);
    }
}
