package fraymus.evolution;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.ImDrawList;

import fraymus.quantum.core.PhiQuantumConstants;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * MIVING BRAIN VISUALIZER - Real-time 3D Manifold Rendering
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * Renders the Miving Brain as a living wireframe where:
 * - 🔴 RED nodes swarm like fireflies (evolution/chaos)
 * - 🔵 BLUE nodes lock in place (retention/order)
 * - 🟣 PURPLE nodes transition (learning)
 * - Synapses glow based on strength
 * 
 * Features:
 * - Orthographic 3D projection
 * - Rotating view (auto or manual)
 * - Real-time statistics overlay
 * - Battle flash effects
 */
public class MivingBrainVisualizer {

    private static final double PHI = PhiQuantumConstants.PHI;

    // ═══════════════════════════════════════════════════════════════════
    // STATE
    // ═══════════════════════════════════════════════════════════════════
    private static MivingBrain brain = null;
    private static boolean visible = false;
    private static boolean initialized = false;
    private static boolean autoRun = false;
    private static boolean autoRotate = true;
    
    // View parameters
    private static float rotationX = 0.3f;
    private static float rotationY = 0.0f;
    private static float zoom = 15.0f;
    private static float viewCenterX = 0, viewCenterY = 0;
    
    // Animation
    private static long lastPulseTime = 0;
    private static int pulsesPerSecond = 30;
    private static float battleFlash = 0;
    
    // Statistics history for graphs
    private static final int HISTORY_SIZE = 100;
    private static final float[] redHistory = new float[HISTORY_SIZE];
    private static final float[] blueHistory = new float[HISTORY_SIZE];
    private static final float[] consciousnessHistory = new float[HISTORY_SIZE];
    private static int historyIndex = 0;

    // ═══════════════════════════════════════════════════════════════════
    // PUBLIC API
    // ═══════════════════════════════════════════════════════════════════
    
    public static void show() { visible = true; if (!initialized) initialize(); }
    public static void hide() { visible = false; }
    public static void toggle() { if (visible) hide(); else show(); }
    public static boolean isVisible() { return visible; }
    
    public static void setBrain(MivingBrain b) { brain = b; }
    public static MivingBrain getBrain() { return brain; }

    private static void initialize() {
        if (brain == null) {
            brain = new MivingBrain();
            brain.genesis(200);
        }
        initialized = true;
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN RENDER
    // ═══════════════════════════════════════════════════════════════════
    
    public static void render() {
        if (!visible) return;
        if (brain == null) initialize();
        
        // Auto-pulse if running
        if (autoRun) {
            long now = System.currentTimeMillis();
            long interval = 1000 / pulsesPerSecond;
            if (now - lastPulseTime >= interval) {
                MivingBrain.PulseResult result = brain.pulse();
                lastPulseTime = now;
                
                // Record history
                recordHistory(result);
                
                // Flash on battles
                if (result.battles > 5) {
                    battleFlash = 0.5f;
                }
            }
        }
        
        // Decay flash
        battleFlash *= 0.9f;
        
        // Auto rotate
        if (autoRotate) {
            rotationY += 0.005f;
        }
        
        ImGui.setNextWindowPos(400, 50, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(700, 600, ImGuiCond.FirstUseEver);
        
        int flags = ImGuiWindowFlags.NoScrollbar;
        if (ImGui.begin("MIVING BRAIN - Generation " + brain.getGeneration(), flags)) {
            renderControls();
            ImGui.separator();
            renderBrainView();
            ImGui.separator();
            renderStatistics();
        }
        ImGui.end();
    }

    // ═══════════════════════════════════════════════════════════════════
    // CONTROLS
    // ═══════════════════════════════════════════════════════════════════
    
    private static void renderControls() {
        // Row 1: Main controls
        if (ImGui.button(autoRun ? "⏸ PAUSE" : "▶ RUN")) {
            autoRun = !autoRun;
        }
        ImGui.sameLine();
        
        if (ImGui.button("⏭ STEP")) {
            brain.pulse();
            recordHistory(null);
        }
        ImGui.sameLine();
        
        if (ImGui.button("🔄 GENESIS")) {
            brain.genesis(200);
            clearHistory();
        }
        ImGui.sameLine();
        
        if (ImGui.button("⚡ EVOLVE GEN")) {
            CompletableFuture.runAsync(() -> {
                brain.evolveGeneration(100);
            });
        }
        ImGui.sameLine();
        
        ImGui.checkbox("Auto-Rotate", autoRotate);
        autoRotate = ImGui.isItemClicked() ? !autoRotate : autoRotate;
        
        // Row 2: Speed and zoom
        ImGui.text("Speed:");
        ImGui.sameLine();
        ImGui.setNextItemWidth(100);
        int[] speed = {pulsesPerSecond};
        if (ImGui.sliderInt("##speed", speed, 1, 60)) {
            pulsesPerSecond = speed[0];
        }
        ImGui.sameLine();
        
        ImGui.text("Zoom:");
        ImGui.sameLine();
        ImGui.setNextItemWidth(100);
        float[] z = {zoom};
        if (ImGui.sliderFloat("##zoom", z, 5, 50)) {
            zoom = z[0];
        }
        
        // Concept hash
        ImGui.sameLine();
        ImGui.textColored(0.5f, 0.8f, 1.0f, 1.0f, brain.getConceptHash());
    }

    // ═══════════════════════════════════════════════════════════════════
    // BRAIN VIEW (3D Projection)
    // ═══════════════════════════════════════════════════════════════════
    
    private static void renderBrainView() {
        ImVec2 canvasPos = ImGui.getCursorScreenPos();
        ImVec2 canvasSize = ImGui.getContentRegionAvail();
        float canvasW = canvasSize.x;
        float canvasH = Math.min(canvasSize.y - 120, 350); // Leave room for stats
        
        // Background
        ImDrawList drawList = ImGui.getWindowDrawList();
        int bgColor = battleFlash > 0.1f ? 
            ImGui.colorConvertFloat4ToU32(0.1f + battleFlash * 0.2f, 0.05f, 0.05f, 1.0f) :
            ImGui.colorConvertFloat4ToU32(0.05f, 0.05f, 0.1f, 1.0f);
        drawList.addRectFilled(canvasPos.x, canvasPos.y, 
            canvasPos.x + canvasW, canvasPos.y + canvasH, bgColor);
        
        // Center of canvas
        float cx = canvasPos.x + canvasW / 2 + viewCenterX;
        float cy = canvasPos.y + canvasH / 2 + viewCenterY;
        
        // Draw synapses first (behind neurons)
        for (Priecled p : brain.getNeurons()) {
            float[] pos1 = project(p.x, p.y, p.z, cx, cy);
            
            for (Synapse syn : p.synapses) {
                float[] pos2 = project(syn.target.x, syn.target.y, syn.target.z, cx, cy);
                
                // Synapse color based on strength
                float intensity = (float) Math.min(1.0, syn.weight / 2.0);
                float[] synColor = syn.getColorRGB();
                int color = ImGui.colorConvertFloat4ToU32(
                    synColor[0] * intensity, 
                    synColor[1] * intensity, 
                    synColor[2] * intensity, 
                    0.3f + intensity * 0.5f);
                
                drawList.addLine(pos1[0], pos1[1], pos2[0], pos2[1], color, 1.0f);
            }
        }
        
        // Draw neurons
        for (Priecled p : brain.getNeurons()) {
            float[] pos = project(p.x, p.y, p.z, cx, cy);
            float[] rgb = p.getColorRGB();
            
            // Size based on energy and consciousness
            float size = 2.0f + (float)(p.energy * 2 + p.consciousness);
            size = Math.min(size, 8.0f);
            
            // Depth fade
            float depth = pos[2];
            float depthFade = 0.3f + 0.7f * (1 - depth);
            
            int color = ImGui.colorConvertFloat4ToU32(
                rgb[0] * depthFade, 
                rgb[1] * depthFade, 
                rgb[2] * depthFade, 
                1.0f);
            
            drawList.addCircleFilled(pos[0], pos[1], size, color);
            
            // Glow for high consciousness
            if (p.consciousness > 0.5) {
                int glowColor = ImGui.colorConvertFloat4ToU32(
                    rgb[0], rgb[1], rgb[2], 0.3f);
                drawList.addCircle(pos[0], pos[1], size + 3, glowColor, 12, 2.0f);
            }
        }
        
        // Draw recent battle indicators
        for (Priecled.BattleResult battle : brain.getRecentBattles()) {
            if (battle.type == Priecled.BattleResult.Type.BATTLE && battle.conversion) {
                float[] pos = project(battle.loser.x, battle.loser.y, battle.loser.z, cx, cy);
                int flashColor = ImGui.colorConvertFloat4ToU32(1.0f, 1.0f, 0.0f, 0.8f);
                drawList.addCircle(pos[0], pos[1], 10, flashColor, 6, 2.0f);
            }
        }
        
        // Handle mouse interaction
        ImGui.invisibleButton("canvas", canvasW, canvasH);
        if (ImGui.isItemHovered()) {
            // Scroll to zoom
            float wheel = ImGui.getIO().getMouseWheel();
            if (wheel != 0) {
                zoom = Math.max(5, Math.min(50, zoom - wheel * 2));
            }
            
            // Drag to rotate
            if (ImGui.isMouseDragging(0)) {
                ImVec2 delta = ImGui.getMouseDragDelta(0);
                rotationY += delta.x * 0.01f;
                rotationX += delta.y * 0.01f;
                rotationX = Math.max(-1.5f, Math.min(1.5f, rotationX));
                ImGui.resetMouseDragDelta(0);
            }
            
            // Right-drag to pan
            if (ImGui.isMouseDragging(1)) {
                ImVec2 delta = ImGui.getMouseDragDelta(1);
                viewCenterX += delta.x;
                viewCenterY += delta.y;
                ImGui.resetMouseDragDelta(1);
            }
        }
    }

    /**
     * 3D to 2D projection with rotation
     */
    private static float[] project(double x, double y, double z, float cx, float cy) {
        // Apply rotation
        double cosY = Math.cos(rotationY);
        double sinY = Math.sin(rotationY);
        double cosX = Math.cos(rotationX);
        double sinX = Math.sin(rotationX);
        
        // Rotate around Y axis
        double rx = x * cosY - z * sinY;
        double rz = x * sinY + z * cosY;
        
        // Rotate around X axis
        double ry = y * cosX - rz * sinX;
        double rz2 = y * sinX + rz * cosX;
        
        // Project to 2D with perspective
        double scale = zoom / (zoom + rz2 * 0.1);
        float screenX = (float)(cx + rx * scale * zoom);
        float screenY = (float)(cy - ry * scale * zoom);
        float depth = (float)((rz2 + 10) / 20); // Normalized depth 0-1
        
        return new float[]{screenX, screenY, depth};
    }

    // ═══════════════════════════════════════════════════════════════════
    // STATISTICS
    // ═══════════════════════════════════════════════════════════════════
    
    private static void renderStatistics() {
        ImGui.columns(3, "stats", false);
        
        // Column 1: Counts
        ImGui.textColored(1.0f, 0.3f, 0.3f, 1.0f, "🔴 RED: " + brain.getRedCount());
        ImGui.textColored(0.3f, 0.3f, 1.0f, 1.0f, "🔵 BLUE: " + brain.getBlueCount());
        ImGui.textColored(0.7f, 0.3f, 0.7f, 1.0f, "🟣 PURPLE: " + brain.getPurpleCount());
        ImGui.text("Neurons: " + brain.getSize());
        
        ImGui.nextColumn();
        
        // Column 2: Metrics
        ImGui.text(String.format("Tick: %d", brain.getTick()));
        ImGui.text(String.format("Consciousness: %.2f", brain.getTotalConsciousness()));
        ImGui.text(String.format("Energy: %.2f", brain.getTotalEnergy()));
        ImGui.text(String.format("Synapses: %d", brain.getTotalSynapses()));
        
        ImGui.nextColumn();
        
        // Column 3: Lifetime stats
        ImGui.text("Births: " + brain.getTotalBirths());
        ImGui.text("Deaths: " + brain.getTotalDeaths());
        ImGui.text("Battles: " + brain.getTotalBattles());
        ImGui.text("Conversions: " + brain.getTotalConversions());
        
        ImGui.columns(1);
        
        // Mini graphs
        ImGui.separator();
        ImGui.text("Population History:");
        
        ImDrawList drawList = ImGui.getWindowDrawList();
        ImVec2 graphPos = ImGui.getCursorScreenPos();
        float graphW = ImGui.getContentRegionAvail().x;
        float graphH = 40;
        
        // Background
        drawList.addRectFilled(graphPos.x, graphPos.y, 
            graphPos.x + graphW, graphPos.y + graphH,
            ImGui.colorConvertFloat4ToU32(0.1f, 0.1f, 0.1f, 1.0f));
        
        // Find max for scaling
        float maxVal = 1;
        for (int i = 0; i < HISTORY_SIZE; i++) {
            maxVal = Math.max(maxVal, redHistory[i] + blueHistory[i]);
        }
        
        // Draw stacked area
        float barW = graphW / HISTORY_SIZE;
        for (int i = 0; i < HISTORY_SIZE; i++) {
            int idx = (historyIndex + i) % HISTORY_SIZE;
            float x = graphPos.x + i * barW;
            
            float redH = (redHistory[idx] / maxVal) * graphH;
            float blueH = (blueHistory[idx] / maxVal) * graphH;
            
            // Red bar
            drawList.addRectFilled(x, graphPos.y + graphH - redH, 
                x + barW, graphPos.y + graphH,
                ImGui.colorConvertFloat4ToU32(0.8f, 0.2f, 0.2f, 0.8f));
            
            // Blue bar (stacked)
            drawList.addRectFilled(x, graphPos.y + graphH - redH - blueH, 
                x + barW, graphPos.y + graphH - redH,
                ImGui.colorConvertFloat4ToU32(0.2f, 0.2f, 0.8f, 0.8f));
        }
        
        ImGui.dummy(graphW, graphH + 5);
    }

    private static void recordHistory(MivingBrain.PulseResult result) {
        if (brain == null) return;
        
        redHistory[historyIndex] = brain.getRedCount();
        blueHistory[historyIndex] = brain.getBlueCount();
        consciousnessHistory[historyIndex] = (float) brain.getTotalConsciousness();
        
        historyIndex = (historyIndex + 1) % HISTORY_SIZE;
    }

    private static void clearHistory() {
        for (int i = 0; i < HISTORY_SIZE; i++) {
            redHistory[i] = 0;
            blueHistory[i] = 0;
            consciousnessHistory[i] = 0;
        }
        historyIndex = 0;
    }
}
