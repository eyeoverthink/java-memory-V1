package fraymus.swarm;

import java.util.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * Blockchain - Genesis Ledger Chain
 * Immutable record of all swarm governance and events.
 */
public class Blockchain {
    private final List<Block> chain;
    private static final double PHI = 1.618033988749895;
    
    public Blockchain() {
        this.chain = new ArrayList<>();
        // Genesis block
        addBlock("GENESIS", "In the beginning was the Word, and the Word was φ");
    }
    
    public void addBlock(String type, String data) {
        String prevHash = chain.isEmpty() ? "0" : chain.get(chain.size() - 1).getHash();
        Block block = new Block(chain.size(), type, data, prevHash);
        chain.add(block);
    }
    
    public List<Block> getChain() {
        return new ArrayList<>(chain);
    }
    
    public Block getLastBlock() {
        return chain.isEmpty() ? null : chain.get(chain.size() - 1);
    }
    
    public boolean validateChain() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);
            
            if (!current.getPrevHash().equals(previous.getHash())) {
                return false;
            }
        }
        return true;
    }
    
    public int size() {
        return chain.size();
    }
    
    public String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== BLOCKCHAIN ===\n");
        sb.append("Blocks: ").append(chain.size()).append("\n");
        sb.append("Valid: ").append(validateChain() ? "YES" : "CORRUPTED!").append("\n");
        if (!chain.isEmpty()) {
            sb.append("Last Hash: ").append(getLastBlock().getHash()).append("\n");
        }
        return sb.toString();
    }
}
