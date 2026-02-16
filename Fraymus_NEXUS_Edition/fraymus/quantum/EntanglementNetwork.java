package fraymus.quantum;

import fraymus.quantum.core.PhiQuantumConstants;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ENTANGLEMENT NETWORK: QUANTUM MESH
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "A web of souls, spanning the void."
 * 
 * Extends EntangledPair to support multiple particles:
 * - GHZ States: N particles all entangled together
 * - W States: Entanglement survives particle loss
 * - Cluster States: Grid of entangled qubits
 * 
 * Applications:
 * - Quantum teleportation (state transfer)
 * - Quantum error correction
 * - Distributed quantum computation
 * - Unbreakable communication channels
 */
public class EntanglementNetwork {

    private static final double PHI = PhiQuantumConstants.PHI;

    // ═══════════════════════════════════════════════════════════════════
    // NETWORK STATE
    // ═══════════════════════════════════════════════════════════════════
    
    // All particles in the network
    private final Map<String, NetworkParticle> particles = new ConcurrentHashMap<>();
    
    // Entanglement links (particle pairs that are entangled)
    private final Map<String, Set<String>> entanglementLinks = new ConcurrentHashMap<>();
    
    // Global network state
    private volatile boolean networkActive = false;
    private volatile int globalPhase = 0; // 0 = superposition
    
    // Statistics
    private final AtomicLong totalObservations = new AtomicLong(0);
    private final AtomicLong totalCollapses = new AtomicLong(0);
    private final AtomicLong instantPropagations = new AtomicLong(0);
    
    // Entanglement types
    public enum EntanglementType {
        GHZ,      // Greenberger–Horne–Zeilinger: All or nothing
        W_STATE,  // W state: Robust to particle loss
        CLUSTER,  // Cluster: Grid topology
        CHAIN,    // Linear chain
        STAR      // Star topology (one center)
    }
    
    private EntanglementType networkType = EntanglementType.GHZ;

    /**
     * A particle in the entanglement network
     */
    public class NetworkParticle implements Runnable {
        public final String id;
        public final int index;
        
        private volatile int spin = 0;  // 0 = superposition, 1 = UP, -1 = DOWN
        private volatile boolean alive = true;
        private volatile boolean observed = false;
        
        private Thread thread;
        private long birthTime;
        private int stateChanges = 0;
        private int propagationsReceived = 0;

        public NetworkParticle(String id, int index) {
            this.id = id;
            this.index = index;
            this.birthTime = System.nanoTime();
        }

        @Override
        public void run() {
            log("[" + id + "] Joined entanglement network");
            
            while (alive && networkActive) {
                totalObservations.incrementAndGet();
                
                // Check for propagated states from entangled partners
                Set<String> partners = entanglementLinks.get(id);
                if (partners != null && !partners.isEmpty()) {
                    for (String partnerId : partners) {
                        NetworkParticle partner = particles.get(partnerId);
                        if (partner != null && partner.observed && !this.observed) {
                            // INSTANT PROPAGATION
                            // Partner was observed - we must react
                            propagateFromPartner(partner);
                        }
                    }
                }
                
                // Reset observation flag after processing
                if (observed) {
                    try { Thread.sleep(100); } catch (Exception e) {}
                    observed = false;
                }
                
                Thread.onSpinWait();
            }
            
            alive = false;
            double lifetimeMs = (System.nanoTime() - birthTime) / 1e6;
            log("XX [" + id + "] Decoherence. Lifetime: " + 
                String.format("%.2f", lifetimeMs) + "ms");
        }

        /**
         * React to entangled partner's state change
         */
        private void propagateFromPartner(NetworkParticle partner) {
            int oldSpin = this.spin;
            
            switch (networkType) {
                case GHZ:
                    // GHZ: All particles have SAME spin
                    this.spin = partner.spin;
                    break;
                    
                case W_STATE:
                    // W: Only ONE particle is UP, rest are DOWN
                    // If partner went UP, I go DOWN
                    this.spin = (partner.spin == 1) ? -1 : 0;
                    break;
                    
                case CLUSTER:
                case CHAIN:
                    // Alternating pattern
                    this.spin = (this.index % 2 == partner.index % 2) ? 
                        partner.spin : -partner.spin;
                    break;
                    
                case STAR:
                    // Center dictates all
                    if (partner.index == 0) {
                        this.spin = -partner.spin; // Satellites are opposite
                    }
                    break;
            }
            
            if (oldSpin != this.spin) {
                stateChanges++;
                propagationsReceived++;
                instantPropagations.incrementAndGet();
                
                String spinStr = (spin == 1) ? "UP ↑" : (spin == -1) ? "DOWN ↓" : "SUPER";
                log(">> [" + id + "] PROPAGATION from " + partner.id + " → " + spinStr);
            }
            
            this.observed = true;
        }

        /**
         * Directly observe this particle
         */
        public void observe(int forcedSpin) {
            int oldSpin = this.spin;
            this.spin = forcedSpin;
            this.observed = true;
            
            if (oldSpin != this.spin) {
                stateChanges++;
            }
            
            String spinStr = (spin == 1) ? "UP ↑" : "DOWN ↓";
            log(">> [" + id + "] OBSERVED → " + spinStr);
        }

        public void start() {
            thread = new Thread(this, "Particle-" + id);
            thread.start();
        }

        public void stop() {
            alive = false;
            if (thread != null) {
                try { thread.join(100); } catch (Exception e) {}
            }
        }

        // Accessors
        public int getSpin() { return spin; }
        public boolean isAlive() { return alive; }
        public boolean isObserved() { return observed; }
        public int getStateChanges() { return stateChanges; }
        public int getPropagationsReceived() { return propagationsReceived; }
    }

    // ═══════════════════════════════════════════════════════════════════
    // NETWORK OPERATIONS
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Create a GHZ-style network with N particles
     */
    public void createGHZNetwork(int numParticles) {
        createNetwork(numParticles, EntanglementType.GHZ);
    }

    /**
     * Create a network with specified topology
     */
    public void createNetwork(int numParticles, EntanglementType type) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   CREATING ENTANGLEMENT NETWORK");
        System.out.println("═══════════════════════════════════════════");
        System.out.println();
        System.out.println("Type: " + type);
        System.out.println("Particles: " + numParticles);
        System.out.println();
        
        this.networkType = type;
        this.networkActive = true;
        
        // Create particles
        for (int i = 0; i < numParticles; i++) {
            String id = "Q" + i;
            NetworkParticle particle = new NetworkParticle(id, i);
            particles.put(id, particle);
            entanglementLinks.put(id, new HashSet<>());
        }
        
        // Create entanglement links based on topology
        switch (type) {
            case GHZ:
                // All particles entangled with all others
                for (String id1 : particles.keySet()) {
                    for (String id2 : particles.keySet()) {
                        if (!id1.equals(id2)) {
                            entanglementLinks.get(id1).add(id2);
                        }
                    }
                }
                break;
                
            case W_STATE:
                // Same as GHZ but behavior differs
                for (String id1 : particles.keySet()) {
                    for (String id2 : particles.keySet()) {
                        if (!id1.equals(id2)) {
                            entanglementLinks.get(id1).add(id2);
                        }
                    }
                }
                break;
                
            case CHAIN:
                // Linear chain: Q0 - Q1 - Q2 - ...
                for (int i = 0; i < numParticles - 1; i++) {
                    String id1 = "Q" + i;
                    String id2 = "Q" + (i + 1);
                    entanglementLinks.get(id1).add(id2);
                    entanglementLinks.get(id2).add(id1);
                }
                break;
                
            case STAR:
                // Q0 is center, all others connect to it
                String center = "Q0";
                for (int i = 1; i < numParticles; i++) {
                    String satellite = "Q" + i;
                    entanglementLinks.get(center).add(satellite);
                    entanglementLinks.get(satellite).add(center);
                }
                break;
                
            case CLUSTER:
                // Grid-like: each connects to neighbors
                int gridSize = (int) Math.sqrt(numParticles);
                for (int i = 0; i < numParticles; i++) {
                    String id = "Q" + i;
                    // Connect to right neighbor
                    if ((i + 1) % gridSize != 0 && i + 1 < numParticles) {
                        entanglementLinks.get(id).add("Q" + (i + 1));
                        entanglementLinks.get("Q" + (i + 1)).add(id);
                    }
                    // Connect to bottom neighbor
                    if (i + gridSize < numParticles) {
                        entanglementLinks.get(id).add("Q" + (i + gridSize));
                        entanglementLinks.get("Q" + (i + gridSize)).add(id);
                    }
                }
                break;
        }
        
        // Start all particles
        for (NetworkParticle p : particles.values()) {
            p.start();
        }
        
        System.out.println(">> Network active with " + particles.size() + " particles.");
        System.out.println(">> Entanglement links: " + countLinks());
    }

    /**
     * Observe a specific particle
     */
    public void observe(String particleId, int spin) {
        NetworkParticle particle = particles.get(particleId);
        if (particle == null) {
            System.out.println("Particle " + particleId + " not found.");
            return;
        }
        
        System.out.println();
        System.out.println(">> OBSERVING " + particleId + "...");
        particle.observe(spin);
        
        // Give time for propagation
        try { Thread.sleep(200); } catch (Exception e) {}
    }

    /**
     * Observe random particle with random spin
     */
    public void observeRandom() {
        if (particles.isEmpty()) return;
        
        List<String> ids = new ArrayList<>(particles.keySet());
        String randomId = ids.get((int)(Math.random() * ids.size()));
        int randomSpin = (Math.random() > 0.5) ? 1 : -1;
        
        observe(randomId, randomSpin);
    }

    /**
     * Kill a specific particle - watch the cascade
     */
    public void killParticle(String particleId) {
        NetworkParticle particle = particles.get(particleId);
        if (particle == null) return;
        
        System.out.println();
        System.out.println(">> KILLING " + particleId + "...");
        
        long killTime = System.nanoTime();
        particle.stop();
        
        // For GHZ, all particles die
        if (networkType == EntanglementType.GHZ) {
            System.out.println(">> GHZ STATE: All particles collapse!");
            collapseNetwork();
        }
        // For W state, network survives
        else if (networkType == EntanglementType.W_STATE) {
            System.out.println(">> W STATE: Network survives particle loss.");
            particles.remove(particleId);
            totalCollapses.incrementAndGet();
        }
        
        long responseTime = System.nanoTime() - killTime;
        System.out.println(">> Cascade time: " + responseTime + " ns");
    }

    /**
     * Collapse entire network
     */
    public void collapseNetwork() {
        System.out.println();
        System.out.println(">> COLLAPSING ENTIRE NETWORK...");
        
        networkActive = false;
        
        for (NetworkParticle p : particles.values()) {
            p.stop();
            totalCollapses.incrementAndGet();
        }
        
        particles.clear();
        entanglementLinks.clear();
        
        System.out.println(">> Network collapsed.");
        System.out.println(">> Total observations: " + totalObservations.get());
        System.out.println(">> Instant propagations: " + instantPropagations.get());
    }

    /**
     * Get network status
     */
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════════\n");
        sb.append("   ENTANGLEMENT NETWORK STATUS\n");
        sb.append("═══════════════════════════════════════════\n");
        sb.append("\n");
        
        sb.append("Network Type: ").append(networkType).append("\n");
        sb.append("Active: ").append(networkActive).append("\n");
        sb.append("Particles: ").append(particles.size()).append("\n");
        sb.append("Links: ").append(countLinks()).append("\n");
        sb.append("\n");
        
        sb.append("PARTICLES:\n");
        for (NetworkParticle p : particles.values()) {
            String spinStr = (p.getSpin() == 1) ? "↑" : (p.getSpin() == -1) ? "↓" : "○";
            String aliveStr = p.isAlive() ? "●" : "×";
            sb.append("  [").append(p.id).append("] ");
            sb.append(aliveStr).append(" ");
            sb.append(spinStr).append(" ");
            sb.append("changes=").append(p.getStateChanges());
            sb.append(" props=").append(p.getPropagationsReceived());
            sb.append("\n");
        }
        
        sb.append("\n");
        sb.append("STATISTICS:\n");
        sb.append("  Observations: ").append(totalObservations.get()).append("\n");
        sb.append("  Collapses: ").append(totalCollapses.get()).append("\n");
        sb.append("  Instant Propagations: ").append(instantPropagations.get()).append("\n");
        
        return sb.toString();
    }

    private int countLinks() {
        int count = 0;
        for (Set<String> links : entanglementLinks.values()) {
            count += links.size();
        }
        return count / 2; // Each link counted twice
    }

    private void log(String msg) {
        System.out.println(msg);
    }

    public boolean isActive() { return networkActive; }
    public int getParticleCount() { return particles.size(); }
    public EntanglementType getNetworkType() { return networkType; }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN - Standalone Demo
    // ═══════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════");
        System.out.println("   ENTANGLEMENT NETWORK DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════");
        System.out.println();
        
        EntanglementNetwork network = new EntanglementNetwork();
        
        // Create a 5-particle GHZ network
        network.createGHZNetwork(5);
        
        try {
            Thread.sleep(1000);
            
            // Observe Q0 - all should react
            network.observe("Q0", 1);
            Thread.sleep(1000);
            
            // Observe Q2
            network.observe("Q2", -1);
            Thread.sleep(1000);
            
            System.out.println(network.getStatus());
            
            // Kill Q0 - in GHZ, all die
            network.killParticle("Q0");
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println(network.getStatus());
    }
}
