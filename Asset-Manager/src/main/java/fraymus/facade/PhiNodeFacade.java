package fraymus.facade;

// SELFBUILD_TARGET Asset-Manager/src/main/java/fraymus/facade/PhiNodeFacade.java
// SELFBUILD_GOAL promote facade selfbuild set
// SELFBUILD_FINGERPRINT φ-7A56B903B39AA

import fraymus.PhiNode;
import fraymus.LivingDNA;
import fraymus.LogicBrain;
import java.util.ArrayList;
import java.util.List;

/**
 * PHI NODE FACADE - Abstracted from PhiNode
 * 
 * Creates and manages PhiNodes with sensible defaults,
 * without requiring external PhiWorld, DNACloaker setup,
 * or any UI framework.
 * 
 * CLI-native interface for living node operations.
 * 
 * φ^∞ © 2026 Vaughn Scott
 */
public class PhiNodeFacade {

    private static final double PHI = 1.6180339887;

    private final List<PhiNode> nodes = new ArrayList<>();
    private long tickCount = 0;

    // ═══════════════════════════════════════════════════════════════════
    // NODE CREATION
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Create a new PhiNode with default DNA and brain.
     */
    public PhiNode spawn(String name, float x, float y) {
        float freq = (float)(PHI * (nodes.size() + 1));
        LivingDNA dna = new LivingDNA(freq, 1.0, 0.05);
        LogicBrain brain = new LogicBrain(8);
        PhiNode node = new PhiNode(name, x, y, dna, brain);
        nodes.add(node);
        return node;
    }

    /**
     * Spawn a node at a phi-spiral position.
     */
    public PhiNode spawnOnSpiral(String name) {
        int i = nodes.size();
        double angle = i * Math.PI * 2 * (1.0 / PHI);
        float radius = (float)(10.0 * Math.sqrt(i + 1));
        float x = (float)(Math.cos(angle) * radius);
        float y = (float)(Math.sin(angle) * radius);
        return spawn(name, x, y);
    }

    // ═══════════════════════════════════════════════════════════════════
    // SIMULATION
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Tick all nodes forward one step.
     */
    public String tick(float dt) {
        tickCount++;
        long now = System.nanoTime();
        int alive = 0;
        int dead = 0;

        for (PhiNode node : nodes) {
            if (node.isAlive()) {
                node.updateInternalState(dt, now);
                alive++;
            } else {
                dead++;
            }
        }
        return String.format("   [PHINODE] Tick %d: %d alive, %d dead", tickCount, alive, dead);
    }

    /**
     * Run n ticks and report summary.
     */
    public String simulate(int ticks) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("   [PHINODE] Simulating %d ticks for %d nodes...\n", ticks, nodes.size()));

        for (int i = 0; i < ticks; i++) {
            long now = System.nanoTime();
            for (PhiNode node : nodes) {
                if (node.isAlive()) {
                    node.updateInternalState(0.1f, now);
                }
            }
            tickCount++;

            if (i % (ticks / 5 + 1) == 0) {
                long aliveCount = nodes.stream().filter(PhiNode::isAlive).count();
                sb.append(String.format("     Tick %d: %d/%d alive\n", tickCount, aliveCount, nodes.size()));
            }
        }

        sb.append(getStatus());
        return sb.toString();
    }

    /**
     * Attempt reproduction between two nodes.
     */
    public String reproduce(String parentA, String parentB, String childName) {
        PhiNode a = findNode(parentA);
        PhiNode b = findNode(parentB);
        if (a == null) return "   [!] Node not found: " + parentA;
        if (b == null) return "   [!] Node not found: " + parentB;
        if (!a.canReproduce()) return "   [!] " + parentA + " cannot reproduce (need size>15, energy>70%)";
        if (!b.canReproduce()) return "   [!] " + parentB + " cannot reproduce (need size>15, energy>70%)";

        float cx = (a.x + b.x) / 2;
        float cy = (a.y + b.y) / 2;
        PhiNode child = a.reproduce(b, childName, cx, cy);
        nodes.add(child);

        return String.format("   [PHINODE] %s born at (%.1f, %.1f) from %s × %s\n     %s",
            childName, cx, cy, parentA, parentB, child);
    }

    // ═══════════════════════════════════════════════════════════════════
    // QUERY
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Think with a node's brain — feed inputs, get decision.
     */
    public String think(String nodeName, int[] inputs) {
        PhiNode node = findNode(nodeName);
        if (node == null) return "   [!] Node not found: " + nodeName;

        int[] output = node.think(inputs);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("   [THINK] %s brain output: [", nodeName));
        for (int i = 0; i < output.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(output[i]);
        }
        sb.append("]\n");
        sb.append("     Decision: ").append(node.getBrain().getLastDecision());
        return sb.toString();
    }

    /**
     * Get a node's full Java code representation.
     */
    public String toCode(String nodeName) {
        PhiNode node = findNode(nodeName);
        if (node == null) return "   [!] Node not found: " + nodeName;
        return node.toJavaCode();
    }

    /**
     * List all nodes.
     */
    public String listNodes() {
        if (nodes.isEmpty()) return "   [PHINODE] No nodes. Use 'phinode spawn <name>' to create.";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("   [PHINODE] %d nodes:\n", nodes.size()));
        for (PhiNode node : nodes) {
            sb.append("     ").append(node).append("\n");
        }
        return sb.toString();
    }

    // ═══════════════════════════════════════════════════════════════════
    // ACCESSORS
    // ═══════════════════════════════════════════════════════════════════

    public List<PhiNode> getNodes() { return nodes; }
    public long getTickCount() { return tickCount; }

    public PhiNode findNode(String name) {
        for (PhiNode node : nodes) {
            if (node.getName().equalsIgnoreCase(name)) return node;
        }
        return null;
    }

    public String getStatus() {
        long alive = nodes.stream().filter(PhiNode::isAlive).count();
        double avgEnergy = nodes.stream().mapToDouble(n -> n.energy).average().orElse(0);
        double avgFreq = nodes.stream().mapToDouble(n -> n.frequency).average().orElse(0);

        return String.format(
            "═══════════════════════════════════════════\n" +
            "  ⚛ PHI NODE FACADE STATUS\n" +
            "═══════════════════════════════════════════\n" +
            "  Total Nodes:    %d\n" +
            "  Alive:          %d\n" +
            "  Dead:           %d\n" +
            "  Avg Energy:     %.1f%%\n" +
            "  Avg Frequency:  %.2f Hz\n" +
            "  Ticks:          %d\n" +
            "  φ Constant:     %.10f\n",
            nodes.size(), alive, nodes.size() - alive,
            avgEnergy * 100, avgFreq, tickCount, PHI);
    }
}
