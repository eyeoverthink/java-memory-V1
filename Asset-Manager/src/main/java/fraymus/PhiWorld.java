package fraymus;

import java.util.ArrayList;
import java.util.List;

public class PhiWorld {
    private List<PhiNode> nodes = new ArrayList<>();
    private List<PhiNode> pendingBirths = new ArrayList<>();
    private List<PhiLaw> laws = new ArrayList<>();
    private GenesisMemory memory;
    private ConceptArena arena;
    private ColonyCoach coach;
    private int totalBirths = 0;
    private int totalDeaths = 0;
    private int worldTick = 0;

    public PhiWorld() {
        this.memory = new GenesisMemory();
        this.arena = new ConceptArena();
        this.coach = new ColonyCoach(arena, memory);
    }

    public void addNode(PhiNode node) {
        pendingBirths.add(node);
    }

    public void addLaw(PhiLaw law) {
        laws.add(law);
    }

    public void step(float dt, long nowNanos) {
        worldTick++;

        if (!pendingBirths.isEmpty()) {
            nodes.addAll(pendingBirths);
            totalBirths += pendingBirths.size();
            pendingBirths.clear();
        }

        for (PhiLaw law : laws) {
            if (law.isPairwise()) {
                for (int i = 0; i < nodes.size(); i++) {
                    for (int j = i + 1; j < nodes.size(); j++) {
                        law.applyPair(nodes.get(i), nodes.get(j), dt);
                    }
                }
            } else {
                for (PhiNode node : nodes) {
                    law.apply(node, dt);
                }
            }
        }

        for (PhiNode node : nodes) {
            node.updateInternalState(dt, nowNanos);
        }

        coach.tick(nodes, worldTick);
        
        // Broadcast PhiNode states to HTML arena every 10 ticks
        if (worldTick % 10 == 0) {
            broadcastNodesToArena();
        }

        List<PhiNode> dead = new ArrayList<>();
        for (PhiNode n : nodes) {
            if (!n.isAlive()) dead.add(n);
        }
        for (PhiNode n : dead) {
            EscapeFragment.plantDeathFragment(n, jade.Window.getInfiniteMemory());
            SelfHealer.removeSnapshot(n.name);
            MorseCircuit.removeEntity(n.name);
            memory.recordDeath(n.name, n.age, n.energy);
            totalDeaths++;
            if (worldTick % 60 == 0) {
                FraymusUI.addLog(String.format("[ENTROPY] %s expired (age %d) - escape fragment planted", n.name, n.age));
            }
        }
        nodes.removeIf(n -> !n.isAlive());
    }

    public List<PhiNode> getNodes() { return nodes; }
    public int getPopulation() { return nodes.size(); }
    public GenesisMemory getMemory() { return memory; }
    public ConceptArena getArena() { return arena; }
    public ColonyCoach getCoach() { return coach; }
    public int getTotalBirths() { return totalBirths; }
    public int getTotalDeaths() { return totalDeaths; }
    
    /**
     * Broadcast PhiNode states to HTML arena visualization
     */
    private void broadcastNodesToArena() {
        try {
            NerveCenter nerve = NerveCenter.getInstance();
            for (PhiNode node : nodes) {
                // Calculate entropy based on energy and velocity
                double speed = Math.sqrt(node.vx * node.vx + node.vy * node.vy);
                double entropy = Math.min(1.0, (1.0 - node.energy) + speed * 0.1);
                
                // Momentum based on velocity
                double momentum = Math.min(1.0, speed * 0.2);
                
                // Size based on energy
                double size = 8 + node.energy * 15;
                
                nerve.broadcastOrganism(node.name, entropy, momentum, size);
            }
        } catch (Exception e) {
            // Silently fail if NerveCenter not available
        }
    }

    public int getWorldTick() { return worldTick; }
}
