package fraymus.universe;

/**
 * 🧬 COSMOS DEMO - Gen 126
 * Demonstrates the 17-Dimensional Universe Engine.
 * 
 * Features:
 * - N-body gravity in 17D space
 * - Quantum entanglement
 * - Wormhole bridges
 * - Dimensional slice visualization
 */
public class CosmosDemo {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🌌 HYPER-COSMOS - Gen 126                                    ║");
        System.out.println("║  17-Dimensional Universe Engine                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Create 17D universe
        Cosmos universe = new Cosmos(17);
        
        // Add observer for events
        universe.addObserver((type, primary, secondary) -> {
            switch (type) {
                case "INJECT":
                    System.out.println("   ★ STAR BORN: " + primary.name);
                    break;
                case "MERGE":
                    System.out.println("   💫 STELLAR MERGER: " + primary.name + " + " + secondary.name);
                    break;
                case "COLLAPSE":
                    System.out.println("   ◉ BLACK HOLE FORMED: " + primary.name);
                    break;
                case "ABSORB":
                    System.out.println("   🕳️ ABSORBED: " + secondary.name + " → " + primary.name);
                    break;
                case "ENTANGLE":
                    System.out.println("   ⚛️ ENTANGLED: " + primary.id + " ↔ " + secondary.id);
                    break;
                case "WORMHOLE":
                    System.out.println("   🌀 WORMHOLE: " + primary.name + " ═══ " + secondary.name);
                    break;
            }
        });
        
        // Big Bang!
        System.out.println("\n💥 INITIATING BIG BANG...\n");
        universe.bigBang(20);
        
        // Start simulation
        System.out.println("🌌 STARTING COSMIC SIMULATION...\n");
        universe.start();
        
        // Let it run for a bit
        Thread.sleep(1000);
        
        // Add some data stars
        System.out.println("\n★ INJECTING KNOWLEDGE STARS...\n");
        CosmicNode quantum = universe.inject("Quantum mechanics describes wave-particle duality");
        CosmicNode gravity = universe.inject("General relativity describes spacetime curvature");
        CosmicNode string = universe.inject("String theory unifies quantum and gravity");
        CosmicNode mTheory = universe.inject("M-Theory exists in 11 dimensions");
        
        // Create entanglement
        System.out.println("\n⚛️ CREATING QUANTUM ENTANGLEMENT...");
        universe.entangle(quantum, gravity);
        
        // Create wormhole
        System.out.println("\n🌀 OPENING WORMHOLE...");
        universe.wormhole(string, mTheory);
        
        Thread.sleep(2000);
        
        // View different dimensional slices
        System.out.println("\n🔭 DIMENSIONAL SCANS:\n");
        
        // Spatial slice (X-Y)
        System.out.println("--- SLICE: Dimensions 0 × 1 (Spatial X-Y) ---");
        System.out.println(universe.telescope(0, 1));
        
        Thread.sleep(500);
        
        // Spatial-Temporal slice (X-Time)
        System.out.println("--- SLICE: Dimensions 0 × 3 (Space-Time) ---");
        System.out.println(universe.telescope(0, 3));
        
        Thread.sleep(500);
        
        // Sentiment-Complexity slice
        System.out.println("--- SLICE: Dimensions 5 × 6 (Sentiment-Complexity) ---");
        System.out.println(universe.telescope(5, 6));
        
        // Status report
        System.out.println("\n" + universe.status());
        
        // Search
        System.out.println("\n🔍 SEARCHING FOR 'quantum'...");
        var matches = universe.find("quantum");
        for (var star : matches) {
            System.out.println("   Found: " + star);
        }
        
        // Let it evolve more
        System.out.println("\n⏳ LETTING UNIVERSE EVOLVE (3 seconds)...\n");
        Thread.sleep(3000);
        
        // Final state
        System.out.println(universe.telescope(0, 1));
        System.out.println(universe.status());
        
        // Stop
        System.out.println("\n🛑 FREEZING UNIVERSE...");
        universe.stop();
        Thread.sleep(100);
        
        System.out.println("\n✨ SIMULATION COMPLETE");
        System.out.println("   Final generation: " + universe.getGeneration());
        System.out.println("   Cosmic time: " + String.format("%.2f", universe.getCosmicTime()) + " seconds");
        System.out.println("   Surviving stars: " + universe.size());
    }
}
