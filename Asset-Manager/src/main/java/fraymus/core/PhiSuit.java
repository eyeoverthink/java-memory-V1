package fraymus.core;

/**
 * PHI SUIT (The Data Exoskeleton)
 * "Wraps ANY object in the 4D Coordinate System."
 * 
 * This is the generic wrapper that turns raw data into spatial objects.
 * Every time you access the data, the suit "glows" (amplitude increases).
 * Every time you modify the data, the suit "moves" (coordinates shift).
 * 
 * Usage:
 *   PhiSuit<String> userName = new PhiSuit<>("Vaughn", 10, 10, 0);
 *   PhiSuit<Integer> iq = new PhiSuit<>(150, 50, 50, 10);
 *   
 *   userName.get(); // Amplitude spikes, suit glows
 *   iq.set(160);    // Coordinates shift, suit evolves
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class PhiSuit<T> extends SpatialNode {
    
    // The raw data inside the suit
    private T payload;
    
    // Semantic label (optional)
    private String label;
    
    // Access statistics
    private int accessCount;
    private int modifyCount;
    
    public PhiSuit(T data, int x, int y, int z) {
        super(x, y, z);
        this.payload = data;
        this.label = data != null ? data.getClass().getSimpleName() : "null";
        this.accessCount = 0;
        this.modifyCount = 0;
    }
    
    public PhiSuit(T data, int x, int y, int z, String label) {
        this(data, x, y, z);
        this.label = label;
    }
    
    /**
     * ACCESS THE DATA
     * Every time you touch the data, the suit 'glows' (Amplitude increases).
     */
    public T get() {
        this.a = Math.min(100, this.a + 10); // Heat up
        this.w = SpatialRegistry.getGeneration(); // Update time to 'Now'
        this.accessCount++;
        return payload;
    }
    
    /**
     * PEEK - Access without heating
     */
    public T peek() {
        return payload;
    }
    
    /**
     * UPDATE THE DATA
     * Changing the data changes the coordinates (Evolution).
     */
    public void set(T newData) {
        this.payload = newData;
        this.x += 1; // It moved in space because it changed
        this.a = 100; // Reset energy (rebirth)
        this.w = SpatialRegistry.getGeneration();
        this.modifyCount++;
    }
    
    /**
     * Get the semantic label
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Set semantic label
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * Get access count
     */
    public int getAccessCount() {
        return accessCount;
    }
    
    /**
     * Get modify count
     */
    public int getModifyCount() {
        return modifyCount;
    }
    
    /**
     * Calculate "heat signature" based on recent activity
     */
    public double getHeatSignature() {
        return (a / 100.0) * PHI * (1 + accessCount * 0.1);
    }
    
    /**
     * Check if this suit is "hot" (actively used)
     */
    public boolean isHot() {
        return a > 50;
    }
    
    /**
     * Check if this suit is "cold" (inactive)
     */
    public boolean isCold() {
        return a < 10;
    }
    
    @Override
    public String toString() {
        return String.format(
            "[%s] :: LABEL='%s' VAL='%s' :: POS(%d, %d, %d) :: AMP(%d) :: ACCESS(%d)",
            this.id.substring(0, 8),
            label,
            payload != null ? payload.toString() : "null",
            x, y, z,
            a,
            accessCount
        );
    }
    
    /**
     * Detailed status report
     */
    public String getStatus() {
        return String.format(
            "════════════════════════════════════════════\n" +
            "  📦 PHI SUIT STATUS\n" +
            "════════════════════════════════════════════\n" +
            "  ID:           %s\n" +
            "  Label:        %s\n" +
            "  Payload:      %s\n" +
            "  Type:         %s\n" +
            "  Position:     (%d, %d, %d)\n" +
            "  Generation:   %d\n" +
            "  Amplitude:    %d/100 %s\n" +
            "  Heat Sig:     %.4f\n" +
            "  Accesses:     %d\n" +
            "  Modifications: %d\n" +
            "  Age:          %d ms\n" +
            "  Status:       %s\n",
            id,
            label,
            payload,
            payload != null ? payload.getClass().getName() : "null",
            x, y, z,
            w,
            a, isHot() ? "🔥" : (isCold() ? "❄️" : "~"),
            getHeatSignature(),
            accessCount,
            modifyCount,
            getAge(),
            isDead() ? "DEAD" : "ALIVE"
        );
    }
}
