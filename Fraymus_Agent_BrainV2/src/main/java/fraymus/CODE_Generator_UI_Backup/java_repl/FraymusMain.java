import java.util.List;

// The Main Entry Point (FraymusMain.java)
// The complete Fraymus Physics Engine demonstration
// Now with fixed timestep accumulator and pairwise entanglement laws

public class FraymusMain {
    
    private static final float DT = 1.0f / 60.0f; // Fixed timestep (60 FPS)
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println(">>> INITIALIZING FRAYMUS PHYSICS ENGINE <<<");
        System.out.println(">>> ARCHITECTURE: Unary Laws + Pairwise Entanglement <<<");
        
        // 1. Create the World
        PhiWorld world = new PhiWorld();
        
        // 2. Define Unary Laws (apply per node)
        world.addLaw(new Laws.Inertia());            // Things move
        world.addLaw(new Laws.HarmonicResonance());  // Things vibrate
        world.addLaw(new Laws.ScottPredictionLaw(1.0f)); // We see the future (1.0s ahead)
        
        // 3. Define Pairwise Laws (apply between node pairs)
        world.addPairLaw(new EntanglementLaw(0.02f, 2.0f, 0.1f)); // Phase sync + energy sharing
        
        // 4. Inject Data (PhiNodes)
        // Note: Signatures are auto-generated from these seed strings
        PhiNode alpha = new PhiNode(0, 0, 10.0f, "ALPHA_PRIME");
        alpha.vx = 1.5f; 
        
        PhiNode beta = new PhiNode(10, 0, 10.01f, "BETA_RESONANT"); // Similar freq -> will entangle
        beta.vx = -1.5f; // Moving towards Alpha
        
        PhiNode gamma = new PhiNode(100, 100, 50.0f, "GAMMA_NOISE"); // Different freq -> won't entangle
        
        world.addNode(alpha);
        world.addNode(beta);
        world.addNode(gamma);
        
        System.out.println(">> WORLD LOADED. NODES: " + world.getNodeCount() + " | LAWS: " + world.getLawCount());
        
        // 5. Fixed Timestep Accumulator Loop
        long prev = System.nanoTime();
        float accumulator = 0f;
        int ticks = 0;
        
        while (ticks < 600) { // Run for 600 ticks (10 seconds)
            long now = System.nanoTime();
            float frameTime = (now - prev) * 1e-9f;
            prev = now;
            
            // Clamp to prevent spiral of death
            if (frameTime > 0.25f) frameTime = 0.25f;
            
            accumulator += frameTime;
            
            while (accumulator >= DT) {
                world.step(DT);
                accumulator -= DT;
                ticks++;
                
                // Visualization Output (Console Dashboard)
                if (ticks % 60 == 0) {
                    System.out.printf("\n--- TICK %d (%.1fs) ---\n", ticks, ticks / 60.0f);
                    for (PhiNode n : world.getNodes()) {
                        System.out.println("  " + n.toString());
                    }
                    
                    // Show entanglement report
                    List<String> entanglements = world.getEntanglementReport();
                    if (!entanglements.isEmpty()) {
                        System.out.println("  >> ENTANGLEMENTS:");
                        for (String e : entanglements) {
                            System.out.println("     " + e);
                        }
                    }
                }
            }
            
            Thread.sleep(1);
        }
        
        System.out.println("\n>>> FRAYMUS SIMULATION COMPLETE <<<");
        System.out.println(">>> TOTAL TICKS: " + ticks + " <<<");
    }
}
