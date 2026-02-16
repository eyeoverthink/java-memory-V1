// The Law Interface (PhiLaw.java)
// The rules of your universe. This decouples "Data" from "Behavior."

public interface PhiLaw {
    // Apply this law to a specific node for a specific timestep
    void apply(PhiNode node, float dt);
}
