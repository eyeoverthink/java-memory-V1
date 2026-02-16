package fraymus;

import jade.Camera;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;

import java.util.*;

public class FraymusRenderer {

    private static final int MAX_TRAIL_LENGTH = 60;
    private static Map<String, List<Vector2f>> trailHistory = new HashMap<>();

    public static void render(PhiWorld world, Camera camera) {
        List<PhiNode> nodes = world.getNodes();

        renderBackgroundGrid();

        for (PhiNode node : nodes) {
            String key = node.name;
            List<Vector2f> trail = trailHistory.computeIfAbsent(key, k -> new ArrayList<>());
            trail.add(new Vector2f(node.x, node.y));
            if (trail.size() > MAX_TRAIL_LENGTH) {
                trail.remove(0);
            }
        }

        Set<String> aliveNames = new HashSet<>();
        for (PhiNode node : nodes) {
            aliveNames.add(node.name);
        }
        trailHistory.keySet().retainAll(aliveNames);

        for (PhiNode node : nodes) {
            List<Vector2f> trail = trailHistory.get(node.name);
            if (trail != null && trail.size() > 1) {
                for (int i = 0; i < trail.size() - 1; i++) {
                    float alpha = (float) i / trail.size();
                    Vector3f trailColor = new Vector3f(
                            node.r * alpha * 0.8f,
                            node.g * alpha * 0.8f,
                            node.b * alpha * 0.8f
                    );
                    DebugDraw.addLine2D(
                            new Vector2f(trail.get(i)),
                            new Vector2f(trail.get(i + 1)),
                            trailColor, 1
                    );
                }
            }
        }

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                PhiNode a = nodes.get(i);
                PhiNode b = nodes.get(j);
                float dx = a.x - b.x;
                float dy = a.y - b.y;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist < 100.0f) {
                    float alpha = 1.0f - dist / 100.0f;
                    Vector3f lineColor = new Vector3f(
                            0.2f * alpha,
                            0.8f * alpha,
                            0.6f * alpha
                    );
                    DebugDraw.addLine2D(
                            new Vector2f(a.x, a.y),
                            new Vector2f(b.x, b.y),
                            lineColor, 1
                    );
                }
            }
        }

        for (PhiNode node : nodes) {
            float radius = 3.0f + node.energy * 6.0f;

            float[] consColor = node.consciousness.getConsciousnessColor();
            float cr = consColor[0];
            float cg = consColor[1];
            float cb = consColor[2];

            boolean breathing = node.consciousness.isRegressive();
            float breathPulse = breathing ?
                    (float)(Math.sin(System.nanoTime() * 1e-9 * 1.5) * 0.3 + 0.7) : 1.0f;

            Vector3f nodeColor = new Vector3f(cr * breathPulse, cg * breathPulse, cb * breathPulse);
            DebugDraw.addCircle(new Vector2f(node.x, node.y), radius, nodeColor, 1);

            float pulse = (float) (Math.sin(System.nanoTime() * 1e-9 * 2.0 + node.frequency * 0.01) * 0.5 + 0.5);

            float glowRadius1 = radius * 1.3f;
            Vector3f glowColor1 = new Vector3f(
                    cr * 0.3f * (0.7f + pulse * 0.3f),
                    cg * 0.3f * (0.7f + pulse * 0.3f),
                    cb * 0.3f * (0.7f + pulse * 0.3f)
            );
            DebugDraw.addCircle(new Vector2f(node.x, node.y), glowRadius1, glowColor1, 1);

            float glowRadius2 = radius * 1.7f;
            Vector3f glowColor2 = new Vector3f(
                    cr * 0.15f * (0.7f + pulse * 0.3f),
                    cg * 0.15f * (0.7f + pulse * 0.3f),
                    cb * 0.15f * (0.7f + pulse * 0.3f)
            );
            DebugDraw.addCircle(new Vector2f(node.x, node.y), glowRadius2, glowColor2, 1);

            float glowRadius3 = radius * 2.2f;
            Vector3f glowColor3 = new Vector3f(
                    cr * 0.07f * (0.7f + pulse * 0.3f),
                    cg * 0.07f * (0.7f + pulse * 0.3f),
                    cb * 0.07f * (0.7f + pulse * 0.3f)
            );
            DebugDraw.addCircle(new Vector2f(node.x, node.y), glowRadius3, glowColor3, 1);

            float[] roleColor = node.getRole().color;
            float roleRadius = radius * 0.4f;
            DebugDraw.addCircle(new Vector2f(node.x, node.y), roleRadius,
                    new Vector3f(roleColor[0], roleColor[1], roleColor[2]), 1);

            if (node.spikeFlash) {
                float spikePulse = (float)(Math.sin(System.nanoTime() * 1e-8) * 0.5 + 0.5);
                float rayLen = radius * 3.0f;
                for (int r = 0; r < 8; r++) {
                    float angle = (float)(r * Math.PI / 4.0);
                    Vector2f rayEnd = new Vector2f(
                            node.x + (float) Math.cos(angle) * rayLen,
                            node.y + (float) Math.sin(angle) * rayLen
                    );
                    Vector3f rayColor = new Vector3f(
                            1.0f,
                            0.6f + spikePulse * 0.4f,
                            0.1f + spikePulse * 0.2f
                    );
                    DebugDraw.addLine2D(new Vector2f(node.x, node.y), rayEnd, rayColor, 1);
                }
                DebugDraw.addCircle(new Vector2f(node.x, node.y), radius * 2.5f,
                        new Vector3f(1.0f, spikePulse * 0.8f, spikePulse * 0.3f), 1);
            }

            int[] outputs = node.brain.getLastOutputs();
            if (outputs.length > 0) {
                if (node.brain.wantsToSeek(outputs)) {
                    float arrowLen = radius * 1.5f;
                    float angle = (float) Math.atan2(node.vy, node.vx);
                    Vector2f arrowEnd = new Vector2f(
                            node.x + (float) Math.cos(angle) * arrowLen,
                            node.y + (float) Math.sin(angle) * arrowLen
                    );
                    DebugDraw.addLine2D(new Vector2f(node.x, node.y), arrowEnd,
                            new Vector3f(0.0f, 1.0f, 0.5f), 1);
                }
                if (node.brain.wantsToFlee(outputs)) {
                    float arrowLen = radius * 1.5f;
                    float angle = (float) Math.atan2(node.vy, node.vx);
                    Vector2f arrowEnd = new Vector2f(
                            node.x + (float) Math.cos(angle) * arrowLen,
                            node.y + (float) Math.sin(angle) * arrowLen
                    );
                    DebugDraw.addLine2D(new Vector2f(node.x, node.y), arrowEnd,
                            new Vector3f(1.0f, 0.2f, 0.2f), 1);
                }
                if (node.brain.wantsToReproduce(outputs)) {
                    float plusSize = radius * 0.5f;
                    float px = node.x + radius + 2.0f;
                    float py = node.y;
                    DebugDraw.addLine2D(new Vector2f(px - plusSize, py), new Vector2f(px + plusSize, py),
                            new Vector3f(0.3f, 1.0f, 0.3f), 1);
                    DebugDraw.addLine2D(new Vector2f(px, py - plusSize), new Vector2f(px, py + plusSize),
                            new Vector3f(0.3f, 1.0f, 0.3f), 1);
                }
                if (node.brain.wantsToMutate(outputs)) {
                    float mutSize = radius * 0.4f;
                    float mx = node.x - radius - 2.0f;
                    float my = node.y;
                    DebugDraw.addLine2D(new Vector2f(mx - mutSize, my - mutSize), new Vector2f(mx + mutSize, my + mutSize),
                            new Vector3f(1.0f, 0.5f, 0.0f), 1);
                    DebugDraw.addLine2D(new Vector2f(mx - mutSize, my + mutSize), new Vector2f(mx + mutSize, my - mutSize),
                            new Vector3f(1.0f, 0.5f, 0.0f), 1);
                }
            }

            DebugDraw.addCircle(new Vector2f(node.x, node.y + radius + 2.0f), 0.8f,
                    new Vector3f(node.r, node.g, node.b), 1);

            float barWidth = 8.0f;
            float barY = node.y - radius - 3.0f;
            float barX = node.x - barWidth * 0.5f;
            float energyWidth = barWidth * node.energy;

            Vector3f barColor = new Vector3f(
                    1.0f - node.energy,
                    node.energy,
                    0.0f
            );
            DebugDraw.addLine2D(
                    new Vector2f(barX, barY),
                    new Vector2f(barX + energyWidth, barY),
                    barColor, 1
            );

            Vector3f barBg = new Vector3f(0.2f, 0.2f, 0.2f);
            DebugDraw.addLine2D(
                    new Vector2f(barX + energyWidth, barY),
                    new Vector2f(barX + barWidth, barY),
                    barBg, 1
            );
        }

        renderBoundary();
    }

    private static void renderBackgroundGrid() {
        float minX = -180.0f, maxX = 180.0f, minY = -100.0f, maxY = 100.0f;
        Vector3f gridColor = new Vector3f(0.08f, 0.08f, 0.15f);
        float dotSize = 0.3f;

        for (float x = minX; x <= maxX; x += 20.0f) {
            for (float y = minY; y <= maxY; y += 20.0f) {
                DebugDraw.addLine2D(
                        new Vector2f(x - dotSize, y),
                        new Vector2f(x + dotSize, y),
                        gridColor, 1
                );
            }
        }
    }

    private static void renderBoundary() {
        float minX = -180.0f, maxX = 180.0f, minY = -100.0f, maxY = 100.0f;
        Vector3f boundaryColor = new Vector3f(0.15f, 0.15f, 0.25f);

        DebugDraw.addLine2D(new Vector2f(minX, minY), new Vector2f(maxX, minY), boundaryColor, 1);
        DebugDraw.addLine2D(new Vector2f(maxX, minY), new Vector2f(maxX, maxY), boundaryColor, 1);
        DebugDraw.addLine2D(new Vector2f(maxX, maxY), new Vector2f(minX, maxY), boundaryColor, 1);
        DebugDraw.addLine2D(new Vector2f(minX, maxY), new Vector2f(minX, minY), boundaryColor, 1);
    }
}
