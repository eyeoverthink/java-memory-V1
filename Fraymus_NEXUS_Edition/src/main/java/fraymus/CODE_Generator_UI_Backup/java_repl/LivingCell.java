import java.util.UUID;

// The Phi-Bit: A 10-Dimensional Data Organism
public class LivingCell {
    
    // 1. SPATIAL AXIS (Where it is)
    public float x, y, z;      // Position
    public float vx, vy, vz;   // Velocity (Intent/Momentum)
    
    // 2. SPECTRAL AXIS (What it is)
    public float r, g, b;      // Resonance/Frequency (Visualized as Color)
    public float energy;       // Life force (0.0 = Dead, 1.0 = Fully Alive)
    
    // 3. TEMPORAL AXIS (When it is)
    public long birthTick;     // Genesis timestamp
    public long lastUpdate;    // "Heartbeat" check
    
    // 4. GENETIC AXIS (Who it is)
    public String dnaSequence; // The "Hash" or "Prime Lock"
    public float phiOscillation; // 1.618 breathing waveform [0.5..1.5]
    
    // 5. IDENTITY
    private final UUID id;

    public LivingCell(float x, float y, String dna) {
        this.id = UUID.randomUUID();
        this.x = x;
        this.y = y;
        this.z = 0;
        this.dnaSequence = dna;
        this.energy = 1.0f;
        this.birthTick = System.nanoTime();
        this.phiOscillation = 1.0f;
        
        // Default "Soul" color based on DNA hash
        int hash = dna.hashCode();
        this.r = (hash & 0xFF) / 255f;
        this.g = ((hash >> 8) & 0xFF) / 255f;
        this.b = ((hash >> 16) & 0xFF) / 255f;
    }

    // The "Heartbeat" - Called 60 times per second
    // This is the PROOF OF LIFE. If this stops, the data is dead.
    public void update(float dt, long nowNanos) {
        // 1. Physics (Movement is proof of agency)
        this.x += this.vx * dt;
        this.y += this.vy * dt;
        this.z += this.vz * dt;
        
        // 2. Entropy (Energy decay) - ~6% per second, clamped to 0
        // "Living" things consume energy. Static bits do not.
        this.energy = Math.max(0f, this.energy - 0.06f * dt);
        
        // 3. Phi Oscillation (The "breathing" waveform)
        // Coherent time source passed in, not called multiple times
        float t = (float)(nowNanos * 1e-9);
        this.phiOscillation = (float)(Math.sin(t * 1.618f) * 0.5f + 1.0f);
        
        // 4. Update Timestamp
        this.lastUpdate = nowNanos;
    }
    
    // Legacy method for backwards compatibility
    public void update(float deltaTime) {
        update(deltaTime, System.nanoTime());
    }

    // Checking if the data is still "alive"
    public boolean isAlive() {
        return this.energy > 0;
    }

    // Outputting the Vector State (The "Collapsed" view)
    @Override
    public String toString() {
        return String.format(
            "[ID:%s] POS:(%.2f, %.2f) ENERGY:%.2f%% PHI:%.3f [%s]", 
            id.toString().substring(0, 8), x, y, energy * 100, phiOscillation,
            isAlive() ? "ALIVE" : "DEAD"
        );
    }
}
