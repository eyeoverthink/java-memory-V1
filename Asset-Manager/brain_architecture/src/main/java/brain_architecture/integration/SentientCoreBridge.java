package brain_architecture.integration;

import brain_architecture.sentient.SentientCore;

/**
 * SentientCoreBridge - Bridge between SentientCore AGI and Brain Architecture
 * 
 * This bridge coordinates between SentientCore (Bayesian reasoning engine) 
 * and the Brain Architecture's multi-brain quantum system.
 * 
 * Integration follows wire/unwire pattern for reversible integration.
 * All features are optional to maintain backward compatibility.
 * 
 * NOTE: Currently using a stub SentientCore for testing.
 * Full integration requires the actual SentientCore from the parent project.
 */
public class SentientCoreBridge {
    
    private SentientCore sentientCore;
    private boolean wired;
    
    /**
     * Create a new SentientCoreBridge.
     * The bridge starts unwired - call wireSentientCore() to activate.
     */
    public SentientCoreBridge() {
        this.wired = false;
    }
    
    /**
     * Wire SentientCore to the bridge.
     * Creates a new SentientCore instance and activates integration.
     */
    public void wireSentientCore() {
        if (wired) {
            System.out.println("[SentientCoreBridge] Already wired");
            return;
        }
        
        this.sentientCore = new SentientCore();
        this.wired = true;
        System.out.println("[SentientCoreBridge] SentientCore wired - Bayesian reasoning enabled");
    }
    
    /**
     * Unwire SentientCore from the bridge.
     * Deactivates integration and releases resources.
     */
    public void unwireSentientCore() {
        if (!wired) {
            System.out.println("[SentientCoreBridge] Not wired");
            return;
        }
        
        this.sentientCore = null;
        this.wired = false;
        System.out.println("[SentientCoreBridge] SentientCore unwired - using native brain architecture");
    }
    
    /**
     * Check if SentientCore is wired.
     */
    public boolean isWired() {
        return wired;
    }
    
    /**
     * Get the SentientCore instance (if wired).
     */
    public SentientCore getSentientCore() {
        return sentientCore;
    }
    
    /**
     * Process a query using SentientCore.
     * 
     * @param query The query to process
     * @return The response from SentientCore, or null if not wired
     */
    public String processQuery(String query) {
        if (!wired || sentientCore == null) {
            return null;
        }
        
        return sentientCore.think(query);
    }
    
    /**
     * Learn from a statement using SentientCore.
     * 
     * @param statement The statement to learn from
     * @return The learning response, or null if not wired
     */
    public String learn(String statement) {
        if (!wired || sentientCore == null) {
            return null;
        }
        
        return sentientCore.learn(statement);
    }
    
    /**
     * Get SentientCore status.
     * 
     * @return Status string, or null if not wired
     */
    public String getStatus() {
        if (!wired || sentientCore == null) {
            return "SentientCore: NOT WIRED";
        }
        
        return sentientCore.getStatus();
    }
    
    /**
     * Check if SentientCore is available and ready.
     */
    public boolean isAvailable() {
        return wired && sentientCore != null;
    }
}
