package fraymus.bio;

/**
 * 🧬 BIO-DIGITAL BRAIN DEMO - Gen 125
 * Demonstrates the LazarusNetwork autopoietic neural system.
 * 
 * Features demonstrated:
 * - 432Hz frequency-locked processing
 * - Self-organizing spatial clustering
 * - Quantum entanglement between neurons
 * - Fractal replication (mitosis)
 * - Hebbian learning (STDP)
 */
public class BioDemo {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🧬 LAZARUS NETWORK - Gen 125                                 ║");
        System.out.println("║  Bio-Digital Brain @ 432Hz                                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Create the network
        LazarusNetwork brain = new LazarusNetwork();
        
        // Add observer for events
        brain.addObserver((type, primary, secondary) -> {
            switch (type) {
                case "INJECT":
                    System.out.println("   💉 INJECTED: " + primary.label);
                    break;
                case "MITOSIS":
                    System.out.println("   ✨ MITOSIS: " + primary.label + " → " + secondary.label);
                    break;
                case "FUSION":
                    System.out.println("   🔥 FUSION: " + primary.label + " ⊕ " + secondary.label);
                    break;
                case "ENTANGLE":
                    System.out.println("   ⚛️ ENTANGLE: " + primary.id + " ↔ " + secondary.id);
                    break;
            }
        });
        
        // Inject initial thoughts
        System.out.println("\n⚡ INJECTING SEED THOUGHTS...\n");
        
        NeuroParticle quantum = brain.inject("Quantum mechanics describes reality");
        NeuroParticle wave = brain.inject("Wave function collapse on observation");
        NeuroParticle entangle = brain.inject("Entangled particles share state");
        NeuroParticle neural = brain.inject("Neural networks learn patterns");
        NeuroParticle hebbian = brain.inject("Hebbian learning strengthens connections");
        NeuroParticle phi = brain.inject("Phi 1.618 golden ratio");
        
        // Create some explicit connections
        quantum.connect(wave, 0.8);
        quantum.connect(entangle, 0.9);
        neural.connect(hebbian, 0.85);
        
        // Entangle two neurons
        System.out.println("\n⚛️ CREATING QUANTUM ENTANGLEMENT...");
        brain.entangle(quantum, entangle);
        System.out.println("   Entangled: " + quantum.id + " ↔ " + entangle.id);
        System.out.println("   Key: " + quantum.entanglementKey);
        
        // Start the 432Hz heartbeat
        System.out.println("\n🎵 STARTING 432Hz HEARTBEAT...\n");
        brain.start();
        
        // Let it run for a bit
        Thread.sleep(1000);
        System.out.println(brain.status());
        
        // Stimulate the network
        System.out.println("\n⚡ STIMULATING 'quantum' neurons...\n");
        brain.stimulate("quantum", 0.95);
        
        Thread.sleep(2000);
        System.out.println(brain.status());
        
        // Add more thoughts (watch them auto-connect)
        System.out.println("\n💉 INJECTING NEW THOUGHTS...\n");
        brain.inject("Superposition allows multiple states");
        brain.inject("Wave particle duality");
        brain.inject("Fractal patterns self-repeat");
        
        Thread.sleep(2000);
        System.out.println(brain.status());
        
        // Print snapshot
        System.out.println("\n📊 NEURON SNAPSHOT:\n");
        for (NeuroParticle n : brain.getSnapshot()) {
            System.out.println("   " + n.toString());
            for (Synapse s : n.synapses) {
                System.out.println("      " + s.toString());
            }
        }
        
        // Stop the network
        System.out.println("\n🛑 SHUTTING DOWN...");
        brain.stop();
        Thread.sleep(100);
        
        System.out.println("\n✨ DEMO COMPLETE");
        System.out.println("   Final generation: " + brain.getGeneration());
        System.out.println("   Final neuron count: " + brain.size());
    }
}
