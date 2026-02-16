import java.math.BigInteger;

// The State-Vector Cell (PhiNode.java)
// The object itself. Not a bit, not a pixel, but a tracked entity
// with geometry, frequency, and identity.

public class PhiNode {
    // --- SPATIAL STATE (Geometry) ---
    public float x, y, z;
    public float vx, vy, vz; // Velocity for Scott 4D projection

    // --- SPECTRAL STATE (Information/Color) ---
    public float r, g, b;    // Informational encoding
    public float energy;     // Life force (0.0 - 1.0)

    // --- TEMPORAL STATE (Phase Space) ---
    public float frequency;  // The "Clock" of this specific node
    public float phase;      // Relative alignment
    public long lastUpdate;  // Timestamp of last update
    
    // --- IDENTITY (The "Soul") ---
    public final BigInteger signature;  // Prime number identity
    public final byte[] hashBytes;      // Compact 32-byte identity for networking
    public final String hexId;          // Short hex ID for logging
    public final String dnaSeed;

    public PhiNode(float x, float y, float freq, String dnaSeed) {
        this.x = x;
        this.y = y;
        this.z = 0;
        this.frequency = freq;
        this.phase = 0;
        this.energy = 1.0f;
        this.dnaSeed = dnaSeed;
        this.lastUpdate = System.nanoTime();
        
        // Cryptographic Generation (full identity with hash bytes)
        DNACloaker.IdentityResult identity = DNACloaker.generateIdentityFull(dnaSeed);
        this.signature = identity.signature;
        this.hashBytes = identity.hashBytes;
        this.hexId = identity.hexHash;
        
        // Visualizing the Identity (Hash -> Color)
        int hash = this.signature.hashCode();
        this.r = ((hash >> 16) & 0xFF) / 255f;
        this.g = ((hash >> 8) & 0xFF) / 255f;
        this.b = (hash & 0xFF) / 255f;
    }

    // The "Entanglement" Check
    // Returns true if nodes share frequency and phase (Physics Entanglement)
    public boolean isEntangledWith(PhiNode other) {
        float epsilonFreq = 0.01f;
        float epsilonPhase = 0.05f;
        
        return Math.abs(this.frequency - other.frequency) < epsilonFreq &&
               Math.abs(this.phase - other.phase) < epsilonPhase;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] Pos:(%.2f, %.2f) Freq:%.2f Phase:%.2f Energy:%.0f%%", 
            hexId, x, y, frequency, phase, energy * 100);
    }
    
    // Update method for consistency with LivingCell
    public void update(float dt, long nowNanos) {
        // Physics
        x += vx * dt;
        y += vy * dt;
        z += vz * dt;
        
        // Energy decay
        energy = Math.max(0f, energy - 0.06f * dt);
        
        // Timestamp
        lastUpdate = nowNanos;
    }
    
    public boolean isAlive() {
        return energy > 0;
    }
}
