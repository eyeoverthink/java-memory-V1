package brain_architecture.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * BlockChain - Immutable append-only ledger for knowledge graph deltas.
 * Provides audit trail and integrity verification.
 */
public class BlockChain {
    private List<Block> chain;
    private int difficulty;
    private String genesisHash;
    
    /**
     * Constructor with default difficulty (3 leading zeros).
     */
    public BlockChain() {
        this.chain = new ArrayList<>();
        this.difficulty = 3;
        this.genesisHash = null;
    }
    
    /**
     * Constructor with custom difficulty.
     */
    public BlockChain(int difficulty) {
        this.chain = new ArrayList<>();
        this.difficulty = difficulty;
        this.genesisHash = null;
    }
    
    /**
     * Create and add genesis block.
     */
    public void createGenesisBlock(KnowledgeGraphDelta initialData) {
        if (genesisHash != null) {
            throw new IllegalStateException("Genesis block already created");
        }
        
        Block genesisBlock = new Block(0, initialData, "0");
        genesisBlock.mine(difficulty);
        chain.add(genesisBlock);
        this.genesisHash = genesisBlock.getHash();
    }
    
    /**
     * Add a new block to the chain.
     */
    public void addBlock(KnowledgeGraphDelta data) {
        if (chain.isEmpty()) {
            throw new IllegalStateException("Cannot add block: genesis block not created");
        }
        
        Block latestBlock = getLatestBlock();
        Block newBlock = new Block(latestBlock.getIndex() + 1, data, latestBlock.getHash());
        newBlock.mine(difficulty);
        chain.add(newBlock);
    }
    
    /**
     * Get the latest block in the chain.
     */
    public Block getLatestBlock() {
        if (chain.isEmpty()) {
            throw new IllegalStateException("Blockchain is empty");
        }
        return chain.get(chain.size() - 1);
    }
    
    /**
     * Get block by index.
     */
    public Block getBlock(long index) {
        if (index < 0 || index >= chain.size()) {
            throw new IndexOutOfBoundsException("Invalid block index: " + index);
        }
        return chain.get((int) index);
    }
    
    /**
     * Validate the entire blockchain.
     * Checks that each block's hash is correct and that previous hash links are valid.
     */
    public boolean isChainValid() {
        for (int i = 0; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            
            // Check if block's hash is valid
            if (!currentBlock.isValid()) {
                return false;
            }
            
            // Check previous hash link (except for genesis block)
            if (i > 0) {
                Block previousBlock = chain.get(i - 1);
                if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                    return false;
                }
            }
            
            // Check genesis block hash
            if (i == 0 && genesisHash != null && !currentBlock.getHash().equals(genesisHash)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Calculate Merkle root of all block hashes.
     * Provides a single hash representing the entire chain state.
     */
    public String calculateMerkleRoot() {
        if (chain.isEmpty()) {
            return "";
        }
        
        List<String> hashes = new ArrayList<>();
        for (Block block : chain) {
            hashes.add(block.getHash());
        }
        
        while (hashes.size() > 1) {
            List<String> newHashes = new ArrayList<>();
            for (int i = 0; i < hashes.size(); i += 2) {
                String left = hashes.get(i);
                String right = (i + 1 < hashes.size()) ? hashes.get(i + 1) : left;
                newHashes.add(applySha256(left + right));
            }
            hashes = newHashes;
        }
        
        return hashes.get(0);
    }
    
    /**
     * Apply SHA-256 hash function.
     */
    private String applySha256(String input) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Get all blocks in the chain.
     */
    public List<Block> getChain() {
        return new ArrayList<>(chain);
    }
    
    /**
     * Get the size of the chain.
     */
    public int getSize() {
        return chain.size();
    }
    
    /**
     * Get the genesis hash.
     */
    public String getGenesisHash() {
        return genesisHash;
    }
    
    /**
     * Get the mining difficulty.
     */
    public int getDifficulty() {
        return difficulty;
    }
    
    /**
     * Set the mining difficulty.
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    
    /**
     * Check if blockchain has genesis block.
     */
    public boolean hasGenesisBlock() {
        return genesisHash != null && !chain.isEmpty();
    }
}
