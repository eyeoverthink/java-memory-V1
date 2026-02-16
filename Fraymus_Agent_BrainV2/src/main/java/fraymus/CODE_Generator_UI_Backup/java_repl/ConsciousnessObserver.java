/**
 * ConsciousnessObserver.java - The Abstraction Layer
 * 
 * 🧬 THE NEURAL STREAM INTERFACE
 * 
 * Decouples the "Thinking" process from the "Speaking" process.
 * 
 * This allows the Swarm to attach multiple listeners:
 * 1. The Console (Visual)
 * 2. The Database (Memory)
 * 3. The Speech Engine (Audio)
 * 4. The ActivityBus (System Events)
 * 5. The Organism (Learning)
 * 
 * ...all listening to the same thought stream simultaneously.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

/**
 * Observer interface for consciousness stream events.
 */
public interface ConsciousnessObserver {
    
    /**
     * Fired when a single synaptic token (word/char) is generated.
     * 
     * @param token The thought fragment
     */
    void onSynapse(String token);
    
    /**
     * Fired when the thought is complete and the connection closes.
     * 
     * @param fullThought The complete thought stream
     */
    void onSilence(String fullThought);
    
    /**
     * Fired if the neural link is severed.
     * 
     * @param error The error message
     */
    void onTrauma(String error);
}
