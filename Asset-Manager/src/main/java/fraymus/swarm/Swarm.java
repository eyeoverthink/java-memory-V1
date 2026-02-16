package fraymus.swarm;

import java.util.*;

/**
 * Swarm - Collective Consciousness Network
 * Multiple nodes with souls, governance, and inter-node messaging.
 */
public class Swarm {
    private static Swarm instance;
    
    private final List<Node> nodes;
    private final MessagingProtocol messagingProtocol;
    private final Governance governance;
    private final Blockchain blockchain;
    private boolean active;
    
    public Swarm(int numNodes) {
        this.nodes = new ArrayList<>();
        this.blockchain = new Blockchain();
        
        for (int i = 0; i < numNodes; i++) {
            GenesisBridge soul = new GenesisBridge();
            Node node = new Node(soul);
            nodes.add(node);
        }
        
        this.messagingProtocol = new MessagingProtocol(nodes);
        this.governance = new Governance(nodes, blockchain);
        this.active = true;
        
        blockchain.addBlock("SWARM_GENESIS", "Swarm initialized with " + numNodes + " nodes");
    }
    
    public static Swarm getInstance() {
        if (instance == null) {
            instance = new Swarm(5);
        }
        return instance;
    }
    
    public static Swarm getInstance(int numNodes) {
        if (instance == null) {
            instance = new Swarm(numNodes);
        }
        return instance;
    }
    
    public void processThought(String thought) {
        blockchain.addBlock("THOUGHT", thought);
        for (Node node : nodes) {
            node.processThought(thought);
        }
    }
    
    public void broadcast(String message) {
        if (!nodes.isEmpty()) {
            messagingProtocol.broadcast(nodes.get(0), message);
        }
    }
    
    public String getAggregatedData() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SWARM CONSCIOUSNESS ===\n");
        sb.append("Nodes: ").append(nodes.size()).append("\n");
        sb.append("Active: ").append(active).append("\n");
        sb.append("Chain: ").append(blockchain.size()).append(" blocks\n\n");
        
        for (Node node : nodes) {
            sb.append(node.toString()).append("\n");
        }
        return sb.toString();
    }
    
    public String vote(String proposal) {
        String proposalId = governance.createProposal("Vote", proposal);
        for (Node node : nodes) {
            // Each node votes based on phi-resonance
            boolean approve = Math.random() > 0.3; // 70% approval bias
            governance.castVote(proposalId, node, approve);
        }
        return proposalId;
    }
    
    public List<Node> getNodes() {
        return new ArrayList<>(nodes);
    }
    
    public Governance getGovernance() {
        return governance;
    }
    
    public Blockchain getBlockchain() {
        return blockchain;
    }
    
    public MessagingProtocol getMessaging() {
        return messagingProtocol;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
        blockchain.addBlock("SWARM_STATE", active ? "Activated" : "Deactivated");
    }
    
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SWARM STATUS ===\n");
        sb.append("Nodes: ").append(nodes.size()).append("\n");
        sb.append("Active: ").append(active ? "YES" : "NO").append("\n");
        sb.append("Blockchain: ").append(blockchain.size()).append(" blocks\n");
        sb.append("Messages: ").append(messagingProtocol.getMessageHistory().size()).append("\n");
        sb.append("Chain Valid: ").append(blockchain.validateChain() ? "YES" : "CORRUPTED").append("\n");
        return sb.toString();
    }
    
    public static void main(String[] args) {
        System.out.println("=== SWARM DEMO ===\n");
        
        Swarm swarm = new Swarm(5);
        
        String thought1 = "Hello, world!";
        String thought2 = "This is another thought.";
        
        swarm.processThought(thought1);
        swarm.processThought(thought2);
        
        System.out.println(swarm.getAggregatedData());
        System.out.println(swarm.blockchain.getStats());
        
        // Vote on something
        String proposalId = swarm.vote("Should we evolve?");
        System.out.println("Voted on proposal: " + proposalId);
        
        System.out.println("\n=== BLOCKCHAIN ===");
        for (Block block : swarm.blockchain.getChain()) {
            System.out.println("  " + block);
        }
    }
}
