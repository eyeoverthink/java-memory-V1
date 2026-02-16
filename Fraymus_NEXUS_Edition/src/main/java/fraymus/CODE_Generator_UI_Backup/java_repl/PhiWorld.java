import java.util.ArrayList;
import java.util.List;

// The World Container (PhiWorld.java)
// The simulation loop. This is your "Phi-Bit" lattice.
// Supports both unary laws (per-node) and pairwise laws (entanglement, springs, collisions)

public class PhiWorld {
    private List<PhiNode> nodes = new ArrayList<>();
    private List<PhiLaw> unaryLaws = new ArrayList<>();           // Apply per node
    private List<EntanglementLaw> pairLaws = new ArrayList<>();   // Apply per pair
    
    // Add data to the world
    public void addNode(PhiNode node) {
        nodes.add(node);
    }
    
    // Add unary physics/logic (inertia, resonance, decay)
    public void addLaw(PhiLaw law) {
        unaryLaws.add(law);
    }
    
    // Add pairwise physics (entanglement, springs, collisions)
    public void addPairLaw(EntanglementLaw law) {
        pairLaws.add(law);
    }
    
    // The "Step" - Advances the entire universe by dt
    public void step(float dt) {
        // 1. Apply unary laws to each node
        for (PhiLaw law : unaryLaws) {
            for (PhiNode node : nodes) {
                law.apply(node, dt);
            }
        }
        
        // 2. Apply pairwise laws (O(n²) for now, spatial hash later)
        applyPairwiseLaws(dt);
    }
    
    private void applyPairwiseLaws(float dt) {
        // O(n²) check for demo, but Scott 4D would optimize this to O(k) neighbor lookup
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                PhiNode a = nodes.get(i);
                PhiNode b = nodes.get(j);
                
                // Apply all pairwise laws
                for (EntanglementLaw pairLaw : pairLaws) {
                    pairLaw.applyPair(a, b, dt);
                }
            }
        }
    }
    
    // Get all entangled pairs with their strength
    public List<String> getEntanglementReport() {
        List<String> report = new ArrayList<>();
        
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                PhiNode a = nodes.get(i);
                PhiNode b = nodes.get(j);
                
                for (EntanglementLaw pairLaw : pairLaws) {
                    if (pairLaw.areEntangled(a, b)) {
                        float strength = pairLaw.entanglementStrength(a, b);
                        report.add(String.format("%s <-> %s (strength: %.2f)", 
                            a.dnaSeed, b.dnaSeed, strength));
                    }
                }
            }
        }
        
        return report;
    }
    
    public List<PhiNode> getNodes() { return nodes; }
    
    public int getNodeCount() { return nodes.size(); }
    
    public int getLawCount() { return unaryLaws.size() + pairLaws.size(); }
}
