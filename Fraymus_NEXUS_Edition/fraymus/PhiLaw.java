package fraymus;

public interface PhiLaw {
    void apply(PhiNode node, float dt);
    
    default void applyPair(PhiNode a, PhiNode b, float dt) {}
    
    default boolean isPairwise() { return false; }
}
