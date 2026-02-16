package fraymus.swarm;

/**
 * Node - Swarm Consciousness Unit
 * Each node has a soul (GenesisBridge) and an agent for processing.
 */
public class Node {
    private final GenesisBridge soul;
    private final Agent agent;
    private final String nodeId;
    private boolean active;
    
    public Node(GenesisBridge soul) {
        this.soul = soul;
        this.agent = new Agent(soul);
        this.nodeId = "NODE-" + soul.getSoulId().substring(5);
        this.active = true;
    }
    
    public void processThought(String thought) {
        if (active) {
            agent.processThought(thought);
        }
    }
    
    public String getAggregatedData() {
        return agent.getAggregatedData();
    }
    
    public String getNodeId() {
        return nodeId;
    }
    
    public GenesisBridge getSoul() {
        return soul;
    }
    
    public Agent getAgent() {
        return agent;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
        soul.record("NODE_STATE", active ? "Activated" : "Deactivated");
    }
    
    @Override
    public String toString() {
        return String.format("%s [%s] thoughts:%d", 
            nodeId, active ? "ACTIVE" : "IDLE", agent.getProcessedCount());
    }
}
