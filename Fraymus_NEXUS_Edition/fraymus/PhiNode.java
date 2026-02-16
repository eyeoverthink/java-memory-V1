package fraymus;

import java.math.BigInteger;

public class PhiNode {

    public final String name;
    public final BigInteger signature;
    public final DNACloaker.CloakedIdentity cloakedIdentity;

    public float x, y, z;
    public float vx, vy, vz;
    public float r, g, b;
    public float energy;

    public float frequency;
    public float phase;
    public float phiResonance;

    public final LivingDNA dna;
    public final LogicBrain brain;
    public final ConsciousnessState consciousness;
    public final QuantumClock quantumClock;
    public final AdaptiveLogicEngine adaptiveEngine;

    public int age;
    public double size;
    public double baseSize;
    public double pulse;

    public long lastUpdateNanos;
    public boolean spikeFlash;

    private AntRole role;
    private CodeConcept lastGeneratedConcept;

    public PhiNode(String name, float x, float y, float freq, LivingDNA dna, LogicBrain brain) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = 0;
        this.vx = 0;
        this.vy = 0;
        this.vz = 0;
        this.energy = 1.0f;

        this.dna = dna != null ? dna : (freq > 0 ? new LivingDNA(freq, 1.0, 0.05) : new LivingDNA());
        this.brain = brain != null ? brain : new LogicBrain(8);
        this.consciousness = new ConsciousnessState();

        this.frequency = (float) this.dna.harmonicFrequency;
        this.phase = 0;

        this.quantumClock = new QuantumClock(this.frequency);
        this.adaptiveEngine = new AdaptiveLogicEngine(this.brain, this.dna.getGeneration());

        String inherited = this.dna.getInheritedStrategies();
        if (inherited != null && !inherited.isEmpty()) {
            this.adaptiveEngine.decodeStrategies(inherited);
        }

        this.cloakedIdentity = DNACloaker.generateCloakedIdentity(name);
        this.signature = cloakedIdentity.N;

        int hash = this.signature.hashCode();
        this.r = ((hash >> 16) & 0xFF) / 255f;
        this.g = ((hash >> 8) & 0xFF) / 255f;
        this.b = (hash & 0xFF) / 255f;

        this.age = 0;
        this.baseSize = 5.0 + (Math.abs(hash % 50)) / 10.0;
        this.size = baseSize;
        this.pulse = 0;
        this.spikeFlash = false;

        this.role = AntRole.assignFromFrequency(this.frequency);
        this.lastGeneratedConcept = null;

        this.lastUpdateNanos = System.nanoTime();
    }

    public PhiNode(float x, float y, float freq, String name) {
        this(name, x, y, freq, null, null);
    }

    public PhiNode(String name, float x, float y) {
        this(name, x, y, 0, null, null);
    }

    public PhiNode(String name, float x, float y, LivingDNA dna, LogicBrain brain) {
        this(name, x, y, 0, dna, brain);
    }

    public void updateInternalState(float dt, long nowNanos) {
        age++;

        double t = nowNanos * 1e-9;
        pulse = dna.pulse(t);
        size = Math.max(1.0, baseSize + pulse * 5.0);

        dna.evolve();
        frequency = (float) dna.harmonicFrequency;

        quantumClock.setPendulumFrequency(frequency);
        quantumClock.tick(dt);

        this.phiResonance = (float) quantumClock.getPhiResonance();
        this.spikeFlash = quantumClock.isSpikeActive();

        this.energy = Math.max(0f, this.energy - 0.01f * dt);

        consciousness.recordThought();

        SelfHealer.tick(this);
        MorseCircuit.tickEntity(this);

        this.lastUpdateNanos = nowNanos;
    }

    public int[] think(int[] inputs) {
        consciousness.recordThought();
        return brain.compute(inputs);
    }

    public boolean isEntangledWith(PhiNode other) {
        float epsilonFreq = 0.5f;
        float epsilonPhase = 0.2f;
        return Math.abs(this.frequency - other.frequency) < epsilonFreq &&
                Math.abs(this.phase - other.phase) < epsilonPhase;
    }

    public boolean canReproduce() {
        return size > 15.0 && energy > 0.7f;
    }

    public PhiNode reproduce(PhiNode partner, String childName, float childX, float childY) {
        LivingDNA childDNA = dna.copy();
        childDNA.setGeneration(dna.getGeneration() + 1);

        String parentStrategies = adaptiveEngine.encodeStrategies();
        childDNA.setInheritedStrategies(parentStrategies);

        LogicBrain childBrain = brain.crossover(partner != null ? partner.brain : brain);

        baseSize *= 0.6;
        energy *= 0.7f;

        PhiNode child = new PhiNode(childName, childX, childY, childDNA, childBrain);

        child.adaptiveEngine.inheritStrategies(this.adaptiveEngine);
        if (partner != null) {
            child.adaptiveEngine.inheritStrategies(partner.adaptiveEngine);
        }

        adaptiveEngine.getCurrentBaseline().reproductionCount++;

        return child;
    }

    public boolean isAlive() {
        return energy > 0.001f;
    }

    public void boostEnergy(float amount) {
        energy = Math.min(1.0f, energy + amount);
    }

    public void decay(float rate) {
        energy = Math.max(0f, energy - rate);
    }

    public LivingDNA getDNA() { return dna; }
    public LogicBrain getBrain() { return brain; }
    public ConsciousnessState getConsciousness() { return consciousness; }
    public QuantumClock getQuantumClock() { return quantumClock; }
    public AdaptiveLogicEngine getAdaptiveEngine() { return adaptiveEngine; }
    public String getName() { return name; }

    public AntRole getRole() { return role; }
    public void setRole(AntRole role) { this.role = role; }
    public CodeConcept getLastGeneratedConcept() { return lastGeneratedConcept; }
    public void setLastGeneratedConcept(CodeConcept concept) { this.lastGeneratedConcept = concept; }

    public String toJavaCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("// Living Entity: %s\n", name));
        sb.append(String.format("// Cloaked Identity: %s\n", cloakedIdentity));
        sb.append(String.format("// Age: %d | Size: %.2f | Energy: %.1f%%\n", age, size, energy * 100));
        sb.append(String.format("// Consciousness: %s\n", consciousness));
        sb.append(String.format("// Brain: %s\n", brain));
        sb.append(String.format("// QuantumClock: %s\n", quantumClock));
        sb.append(String.format("new PhiNode(\n"));
        sb.append(String.format("    \"%s\",\n", name));
        sb.append(String.format("    %.2ff, %.2ff,\n", x, y));
        sb.append(String.format("    %s,\n", dna.toJavaCode()));
        sb.append(String.format("    new LogicBrain(%d)\n", brain.getGateCount()));
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String toString() {
        String status = isAlive() ? "ALIVE" : "DEAD";
        String spike = spikeFlash ? " [SPIKE!]" : "";
        return String.format("[NODE %s] Role:%s Pos:(%.2f, %.2f) Freq:%.2f Phase:%.2f Energy:%.1f%% Phi:%.3f Brain:%s [%s]%s",
                name, role.displayName, x, y, frequency, phase, energy * 100, phiResonance, brain.getLastDecision(), status, spike);
    }
}
