// EntanglementLaw.java
// Physical coupling between nodes with matching frequency
// Turns "entanglement" from a boolean check into actual physics

public class EntanglementLaw implements PhiLaw {
    
    private final float epsFreq;      // Frequency tolerance for entanglement
    private final float kPhase;       // Phase coupling strength
    private final float kEnergy;      // Energy sharing coefficient
    
    public EntanglementLaw() {
        this(0.02f, 2.0f, 0.1f);
    }
    
    public EntanglementLaw(float epsFreq, float kPhase, float kEnergy) {
        this.epsFreq = epsFreq;
        this.kPhase = kPhase;
        this.kEnergy = kEnergy;
    }
    
    @Override
    public void apply(PhiNode node, float dt) {
        // No-op for unary application - entanglement is pairwise
    }
    
    // The actual physics: phase synchronization between entangled nodes
    public void applyPair(PhiNode a, PhiNode b, float dt) {
        // Check frequency match (entanglement condition)
        if (Math.abs(a.frequency - b.frequency) > epsFreq) {
            return; // Not entangled - frequencies too different
        }
        
        // Calculate phase difference (wrapped to [-π, π])
        float phaseDiff = wrapPhase(a.phase - b.phase);
        
        // Phase correction force (spring-like coupling)
        // Nodes with matching frequency will synchronize their phases
        float correction = -kPhase * phaseDiff * dt;
        
        a.phase += correction;
        b.phase -= correction;
        
        // Wrap phases back to [0, 2π]
        a.phase = normalizePhase(a.phase);
        b.phase = normalizePhase(b.phase);
        
        // Energy sharing (entangled nodes share life force)
        float energyDiff = a.energy - b.energy;
        float energyTransfer = kEnergy * energyDiff * dt;
        
        a.energy -= energyTransfer;
        b.energy += energyTransfer;
        
        // Clamp energy to valid range
        a.energy = Math.max(0f, Math.min(1f, a.energy));
        b.energy = Math.max(0f, Math.min(1f, b.energy));
    }
    
    // Wrap phase difference to [-π, π] for shortest path correction
    private float wrapPhase(float p) {
        while (p > Math.PI) p -= (float)(2 * Math.PI);
        while (p < -Math.PI) p += (float)(2 * Math.PI);
        return p;
    }
    
    // Normalize phase to [0, 2π]
    private float normalizePhase(float p) {
        while (p < 0) p += (float)(2 * Math.PI);
        while (p >= 2 * Math.PI) p -= (float)(2 * Math.PI);
        return p;
    }
    
    // Check if two nodes are currently entangled
    public boolean areEntangled(PhiNode a, PhiNode b) {
        return Math.abs(a.frequency - b.frequency) <= epsFreq;
    }
    
    // Calculate entanglement strength (0 = not entangled, 1 = perfectly synced)
    public float entanglementStrength(PhiNode a, PhiNode b) {
        if (!areEntangled(a, b)) return 0f;
        
        float phaseDiff = Math.abs(wrapPhase(a.phase - b.phase));
        float phaseSync = 1f - (phaseDiff / (float)Math.PI);
        
        float freqMatch = 1f - (Math.abs(a.frequency - b.frequency) / epsFreq);
        
        return phaseSync * freqMatch;
    }
}
