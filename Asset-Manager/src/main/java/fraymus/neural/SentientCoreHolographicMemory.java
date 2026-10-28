package fraymus.neural;

/**
 * SentientCore Holographic Memory Integration
 *
 * Wires the HolographicAssociativeMemory into SentientCore
 * using the wire/unwire pattern (RULE SC-2).
 *
 * Also wires:
 * - HolographicRenderEngine for v_dream(t) = G_LTX(C(t))
 * - HolographicPhysicsEngine for x(t+1) = x(t) + delta_OpenClaw(A_safe)
 */
public class SentientCoreHolographicMemory {

    private final SentientCore sentientCore;
    private HolographicAssociativeMemory holographicMemory;
    private HolographicRenderEngine renderEngine;
    private HolographicPhysicsEngine physicsEngine;
    private boolean wired = false;

    public SentientCoreHolographicMemory(SentientCore sentientCore) {
        this.sentientCore = sentientCore;
    }

    /**
     * Wire holographic memory, render engine, and physics engine.
     *
     * This enables O(1) associative retrieval for:
     * - Causal graph node lookup
     * - Concept lattice classification
     * - Analogy engine similarity search
     * - Thought rendering (Blender/FFmpeg)
     * - Physics simulation (JBullet/Box2D)
     */
    public void wire() {
        if (wired) return;
        this.holographicMemory = new HolographicAssociativeMemory();
        this.renderEngine = new HolographicRenderEngine();
        this.physicsEngine = new HolographicPhysicsEngine();

        this.renderEngine.wire(holographicMemory);
        this.physicsEngine.wire(holographicMemory);

        this.wired = true;
    }

    /**
     * Unwire all holographic components.
     */
    public void unwire() {
        if (renderEngine != null) renderEngine.unwire();
        if (physicsEngine != null) physicsEngine.unwire();
        this.wired = false;
        this.holographicMemory = null;
        this.renderEngine = null;
        this.physicsEngine = null;
    }

    public boolean isWired() {
        return wired;
    }

    public HolographicAssociativeMemory getMemory() {
        return holographicMemory;
    }

    public HolographicRenderEngine getRenderEngine() {
        return renderEngine;
    }

    public HolographicPhysicsEngine getPhysicsEngine() {
        return physicsEngine;
    }

    /**
     * Get full system status including memory, render, and physics stats.
     */
    public String getStatus() {
        if (!wired) return "SentientCoreHolographicMemory: NOT WIRED";
        StringBuilder sb = new StringBuilder();
        sb.append("=== SENTIENT CORE HOLOGRAPHIC MEMORY ===\n");
        sb.append(holographicMemory.getStats()).append("\n\n");
        sb.append("=== RENDER ENGINE ===\n");
        sb.append(renderEngine.getStats()).append("\n\n");
        sb.append("=== PHYSICS ENGINE ===\n");
        sb.append(physicsEngine.getStats());
        return sb.toString();
    }
}
