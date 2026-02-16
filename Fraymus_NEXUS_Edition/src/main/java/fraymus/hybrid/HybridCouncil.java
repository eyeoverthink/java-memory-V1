package fraymus.hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * THE HYBRID COUNCIL
 * Merges the 'Council of Eight' (Human Array) with the FVM (Machine Core).
 * 
 * Silicon Logic (Cold, Binary, Deterministic)
 *        +
 * Carbon Logic (Warm, Heuristic, Emotional)
 *        =
 * Hybrid Consensus (φ-weighted truth)
 */
public class HybridCouncil {

    private static final double PHI = 1.618033988749895;
    
    private ExecutorService threadPool;
    private List<HumanAgent> humanArray;
    private double lastConsensus;
    private boolean lastDecision;

    public HybridCouncil() {
        System.out.println("🏛️ CONVENING THE HYBRID COUNCIL...");
        this.threadPool = Executors.newFixedThreadPool(8);
        this.humanArray = new ArrayList<>();

        for (HumanAgent.BrainType type : HumanAgent.BrainType.values()) {
            humanArray.add(new HumanAgent(type));
        }
        
        System.out.println("   8 Specialized Cognitive Agents initialized.");
    }

    /**
     * THE DECISION PROTOCOL
     * 1. Ask the Human Array (Heuristic)
     * 2. Ask the Machine Core (Deterministic)
     * 3. Synthesize via φ-weighting
     */
    public boolean convene(String problem) {
        System.out.println("\n⚡ PROBLEM SUBMITTED: " + problem);

        try {
            List<Future<?>> futures = new ArrayList<>();
            for (HumanAgent agent : humanArray) {
                futures.add(threadPool.submit(agent));
            }

            for (Future<?> f : futures) f.get();

            return synthesizeResults();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean synthesizeResults() {
        System.out.println("\n📊 --- COUNCIL RESULTS ---");
        
        double weightedSum = 0;
        
        for (HumanAgent agent : humanArray) {
            System.out.println("   " + agent.getVote());
            
            switch (agent.getType()) {
                case LOGICAL:
                case PHYSICAL:
                    weightedSum += 1.5 * agent.getOutputValue();
                    break;
                case EMOTIONAL:
                    weightedSum += 0.5 * agent.getOutputValue();
                    break;
                case TACHYONIC:
                    weightedSum += PHI * agent.getOutputValue();
                    break;
                default:
                    weightedSum += 1.0 * agent.getOutputValue();
            }
        }

        this.lastConsensus = weightedSum / 8.0;
        
        System.out.println("\n🌊 HYBRID CONSENSUS SCORE: " + String.format("%.4f", lastConsensus));
        
        if (lastConsensus > PHI) {
            System.out.println("   ✅ ACTION APPROVED: Coherence exceeds φ.");
            this.lastDecision = true;
        } else {
            System.out.println("   ❌ ACTION REJECTED: Entropy too high.");
            this.lastDecision = false;
        }
        
        return lastDecision;
    }
    
    public double getLastConsensus() {
        return lastConsensus;
    }
    
    public boolean getLastDecision() {
        return lastDecision;
    }
    
    public void shutdown() {
        threadPool.shutdown();
    }
}
