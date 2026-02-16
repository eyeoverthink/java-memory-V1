package fraymus.core;

/**
 * THE DATA SUIT (Generic Exoskeleton)
 * Wraps ANY object (T) in the 5D Coordinate System.
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * This is the "Exoskeleton" for raw data. It takes any Java object
 * (String, Integer, LogicGate, etc.) and gives it:
 * - A position in space (x, y, z)
 * - A timestamp (w)
 * - An energy level (a)
 * 
 * The suit makes invisible data visible. It gives ghosts a body.
 * 
 * Usage:
 *   PhiSuit<String> userName = new PhiSuit<>("Vaughn", 10, 10, 0);
 *   String name = userName.get(); // Amplitude increases!
 */
public class PhiSuit<T> extends PhiNode {

    private T payload; // The raw data inside the suit

    public PhiSuit(T data, int x, int y, int z) {
        super(x, y, z); // Initialize the 5D coordinates
        this.payload = data;
        this.a = 100; // Born with full energy - It starts loud!
        
        // Register with Lazarus after construction
        Lazarus.registerNode(this);
        
        // Register with Gravity Engine
        GravityEngine.register(this);
    }

    /**
     * ACCESS THE DATA
     * Every time you touch the data, the suit 'glows' (Amplitude increases).
     * This is how the Gravity Engine knows what's "hot" right now.
     */
    public T get() {
        this.a += 10; // It gets hotter when used
        this.w = Lazarus.GEN_COUNT; // Update time to 'Now'
        return payload;
    }

    /**
     * UPDATE THE DATA
     * Changing the data changes the coordinates (Evolution).
     * The object "moves" in space because it changed.
     */
    public void set(T newData) {
        this.payload = newData;
        this.x += 1; // It moved in space because it changed
        this.a = 100; // Reset energy
    }
    
    /**
     * Get the raw payload without triggering amplitude increase
     * (For internal system use)
     */
    public T peek() {
        return payload;
    }
    
    /**
     * Get the type of the payload
     */
    public Class<?> getType() {
        return payload != null ? payload.getClass() : null;
    }

    @Override
    public String toString() {
        return String.format(
            "[%s] :: VAL='%s' :: POS(%d, %d, %d) :: AMP(%d)", 
            this.id.substring(0, 4), 
            payload != null ? payload.toString() : "null", 
            x, y, z, 
            a
        );
    }
}
