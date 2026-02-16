package fraymus.ui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import fraymus.absorption.Portal;
import fraymus.organism.NEXUS_Organism;

import java.util.*;

/**
 * PORTAL UI: THE VISUAL INTAKE VALVE
 * 
 * "Drop anything in. Watch it become knowledge."
 * 
 * Visual interface for the Portal absorption system.
 * Drag-and-drop style UI for ingesting:
 * - JAR files
 * - Source code
 * - URLs
 * - Packages
 * - Classes
 * - Directories
 */
public class PortalPanel {

    private static final ImString inputBuffer = new ImString(512);
    private static Portal portal;
    private static List<IngestionLog> logs = new ArrayList<>();
    private static boolean autoScroll = true;
    private static int maxLogs = 100;
    
    // Statistics
    private static long totalIngested = 0;
    private static long totalBytes = 0;
    private static String lastInput = "";
    private static String lastType = "";
    private static long lastTimestamp = 0;
    
    // Animation
    private static float portalRotation = 0.0f;
    private static float pulsePhase = 0.0f;
    private static boolean isIngesting = false;
    private static float ingestProgress = 0.0f;
    private static boolean visible = false;

    public static void toggle() {
        visible = !visible;
    }

    public static void init(NEXUS_Organism nexus) {
        portal = nexus.getMouth();
        log("🌀 PORTAL UI INITIALIZED", 0.3f, 1.0f, 0.8f);
        log("   Mode: Universal Intake", 0.6f, 0.6f, 0.6f);
        log("   Status: HUNGRY", 0.3f, 1.0f, 0.3f);
    }

    public static void render() {
        if (!visible || portal == null) return;
        
        // Update animations
        portalRotation += 0.02f;
        pulsePhase += 0.05f;
        if (isIngesting) {
            ingestProgress += 0.03f;
            if (ingestProgress >= 1.0f) {
                isIngesting = false;
                ingestProgress = 0.0f;
            }
        }

        ImGui.setNextWindowPos(950, 0, imgui.flag.ImGuiCond.FirstUseEver);
        ImGui.setNextWindowSize(330, 720, imgui.flag.ImGuiCond.FirstUseEver);

        if (ImGui.begin("🌀 PORTAL", ImGuiWindowFlags.None)) {
            renderHeader();
            ImGui.separator();
            renderDropZone();
            ImGui.separator();
            renderQuickActions();
            ImGui.separator();
            renderLogs();
            ImGui.separator();
            renderStats();
        }
        ImGui.end();
    }

    private static void renderHeader() {
        // Animated portal vortex indicator
        float pulse = (float)(Math.sin(pulsePhase) * 0.3 + 0.7);
        ImGui.textColored(0.3f * pulse, 1.0f * pulse, 0.8f * pulse, 1.0f, "UNIVERSAL INTAKE VALVE");
        
        ImGui.sameLine();
        ImGui.dummy(10, 0);
        ImGui.sameLine();
        
        // Status indicator
        if (isIngesting) {
            ImGui.textColored(1.0f, 0.8f, 0.0f, 1.0f, "[INGESTING...]");
        } else {
            ImGui.textColored(0.3f, 1.0f, 0.3f, 1.0f, "[HUNGRY]");
        }
    }

    private static void renderDropZone() {
        ImGui.text("DROP ZONE");
        ImGui.pushStyleColor(ImGuiCol.FrameBg, 0.05f, 0.05f, 0.1f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.FrameBgHovered, 0.1f, 0.1f, 0.2f, 1.0f);
        
        ImGui.inputTextMultiline("##input", inputBuffer, 300, 80, 
            ImGuiInputTextFlags.None);
        
        ImGui.popStyleColor(2);
        
        ImGui.text("Accepts: JAR, Java, URL, Package, Class, Directory");
        
        if (ImGui.button("🌀 INGEST", 150, 30)) {
            ingestInput();
        }
        
        ImGui.sameLine();
        if (ImGui.button("CLEAR", 70, 30)) {
            inputBuffer.set("");
        }
        
        // Progress bar during ingestion
        if (isIngesting) {
            ImGui.pushStyleColor(ImGuiCol.PlotHistogram, 0.3f, 1.0f, 0.8f, 1.0f);
            ImGui.progressBar(ingestProgress, 0, 20, "");
            ImGui.popStyleColor();
        }
    }

    private static void renderQuickActions() {
        ImGui.text("QUICK INGEST");
        
        if (ImGui.button("java.lang.Math", 150, 25)) {
            quickIngest("java.lang.Math");
        }
        
        ImGui.sameLine();
        if (ImGui.button("java.util.*", 150, 25)) {
            quickIngest("java.util");
        }
        
        if (ImGui.button("java.io.*", 150, 25)) {
            quickIngest("java.io");
        }
        
        ImGui.sameLine();
        if (ImGui.button("java.nio.*", 150, 25)) {
            quickIngest("java.nio");
        }
    }

    private static void renderLogs() {
        ImGui.text("INGESTION LOG");
        
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.02f, 0.02f, 0.05f, 1.0f);
        ImGui.beginChild("PortalLogs", 0, 250, true, ImGuiWindowFlags.HorizontalScrollbar);
        
        for (IngestionLog log : logs) {
            ImGui.textColored(log.r, log.g, log.b, 1.0f, log.message);
        }
        
        if (autoScroll && ImGui.getScrollY() >= ImGui.getScrollMaxY()) {
            ImGui.setScrollHereY(1.0f);
        }
        
        ImGui.endChild();
        ImGui.popStyleColor();
        
        ImGui.checkbox("Auto-scroll", autoScroll);
        autoScroll = ImGui.isItemActive() ? autoScroll : ImGui.isItemClicked() ? !autoScroll : autoScroll;
    }

    private static void renderStats() {
        ImGui.text("STATISTICS");
        
        ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.05f, 0.02f, 0.02f, 0.5f);
        ImGui.beginChild("PortalStats", 0, 100, true, ImGuiWindowFlags.None);
        
        ImGui.textColored(0.6f, 0.6f, 0.6f, 1.0f, "Items Ingested:");
        ImGui.sameLine(150);
        ImGui.textColored(0.3f, 1.0f, 0.3f, 1.0f, String.valueOf(totalIngested));
        
        ImGui.textColored(0.6f, 0.6f, 0.6f, 1.0f, "Bytes Consumed:");
        ImGui.sameLine(150);
        ImGui.textColored(0.3f, 1.0f, 0.3f, 1.0f, formatBytes(totalBytes));
        
        if (!lastInput.isEmpty()) {
            ImGui.separator();
            ImGui.textColored(0.8f, 0.8f, 0.3f, 1.0f, "Last: " + lastType);
            ImGui.textColored(0.5f, 0.5f, 0.5f, 1.0f, truncate(lastInput, 35));
        }
        
        ImGui.endChild();
        ImGui.popStyleColor();
        
        if (ImGui.button("SHOW FULL STATS", 150, 25)) {
            if (portal != null) {
                portal.printStats();
            }
        }
        
        ImGui.sameLine();
        if (ImGui.button("CLEAR LOGS", 150, 25)) {
            logs.clear();
        }
    }

    private static void ingestInput() {
        String input = inputBuffer.get().trim();
        if (input.isEmpty()) {
            log("⚠ No input provided", 1.0f, 0.5f, 0.0f);
            return;
        }
        
        isIngesting = true;
        ingestProgress = 0.0f;
        
        try {
            log("🌀 INGESTING: " + truncate(input, 40), 0.3f, 1.0f, 0.8f);
            portal.drop(input);
            
            totalIngested++;
            lastInput = input;
            lastType = classifyType(input);
            lastTimestamp = System.currentTimeMillis();
            
            log("✓ INGESTION COMPLETE", 0.3f, 1.0f, 0.3f);
            inputBuffer.set("");
            
        } catch (Exception e) {
            log("✗ INGESTION FAILED: " + e.getMessage(), 1.0f, 0.3f, 0.3f);
            isIngesting = false;
        }
    }

    private static void quickIngest(String input) {
        inputBuffer.set(input);
        ingestInput();
    }

    private static String classifyType(String input) {
        if (input.startsWith("http://") || input.startsWith("https://")) return "URL";
        if (input.endsWith(".jar")) return "JAR";
        if (input.endsWith(".java")) return "JAVA";
        if (input.matches("[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)+")) return "PACKAGE";
        if (input.matches(".*\\.[A-Z][a-zA-Z0-9]*")) return "CLASS";
        if (input.contains("\\") || input.contains("/")) return "PATH";
        return "UNKNOWN";
    }

    private static String truncate(String str, int maxLen) {
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 3) + "...";
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    public static void log(String message, float r, float g, float b) {
        logs.add(new IngestionLog(message, r, g, b));
        if (logs.size() > maxLogs) {
            logs.remove(0);
        }
    }

    private static class IngestionLog {
        String message;
        float r, g, b;
        
        IngestionLog(String message, float r, float g, float b) {
            this.message = message;
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }
}
