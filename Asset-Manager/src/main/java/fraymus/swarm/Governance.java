package fraymus.swarm;

import java.util.*;
import java.security.MessageDigest;

/**
 * Governance - Decentralized Voting on Blockchain
 * Nodes vote on proposals, results are recorded on-chain.
 */
public class Governance {
    private final List<Node> nodes;
    private final Blockchain blockchain;
    private final Map<String, Proposal> activeProposals;
    
    public Governance(List<Node> nodes, Blockchain blockchain) {
        this.nodes = nodes;
        this.blockchain = blockchain;
        this.activeProposals = new HashMap<>();
    }
    
    public String createProposal(String title, String description) {
        Proposal p = new Proposal(title, description, nodes.size());
        activeProposals.put(p.id, p);
        blockchain.addBlock("PROPOSAL_CREATED", p.toString());
        return p.id;
    }
    
    public void castVote(String proposalId, Node voter, boolean approve) {
        Proposal p = activeProposals.get(proposalId);
        if (p != null && !p.hasVoted(voter.getNodeId())) {
            p.vote(voter.getNodeId(), approve);
            blockchain.addBlock("VOTE_CAST", 
                String.format("%s voted %s on %s", voter.getNodeId(), approve ? "YES" : "NO", proposalId));
            
            // Check if voting complete
            if (p.isComplete()) {
                finalizeProposal(p);
            }
        }
    }
    
    public void castVote(String vote) {
        // Legacy single-vote method - creates proposal and auto-approves
        String proposalId = createProposal("Quick Vote", vote);
        for (Node node : nodes) {
            castVote(proposalId, node, true);
        }
    }
    
    private void finalizeProposal(Proposal p) {
        String result = p.getResult();
        blockchain.addBlock("PROPOSAL_FINALIZED", 
            String.format("%s: %s (YES:%d NO:%d)", p.id, result, p.yesVotes, p.noVotes));
        activeProposals.remove(p.id);
    }
    
    public List<Block> getGovernanceBlocks() {
        return blockchain.getChain();
    }
    
    public Blockchain getBlockchain() {
        return blockchain;
    }
    
    public static class Proposal {
        public final String id;
        public final String title;
        public final String description;
        public final int requiredVotes;
        public int yesVotes;
        public int noVotes;
        private final Set<String> voters;
        
        public Proposal(String title, String description, int requiredVotes) {
            this.id = "PROP-" + Long.toHexString(System.nanoTime()).toUpperCase();
            this.title = title;
            this.description = description;
            this.requiredVotes = requiredVotes;
            this.voters = new HashSet<>();
        }
        
        public void vote(String voterId, boolean approve) {
            if (!voters.contains(voterId)) {
                voters.add(voterId);
                if (approve) yesVotes++;
                else noVotes++;
            }
        }
        
        public boolean hasVoted(String voterId) {
            return voters.contains(voterId);
        }
        
        public boolean isComplete() {
            return voters.size() >= requiredVotes;
        }
        
        public String getResult() {
            if (yesVotes > noVotes) return "APPROVED";
            if (noVotes > yesVotes) return "REJECTED";
            return "TIE";
        }
        
        @Override
        public String toString() {
            return String.format("%s: %s", id, title);
        }
    }
}
