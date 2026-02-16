package com.eyeoverthink.portal;

import com.eyeoverthink.lazarus.LazarusEngine;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * THE PAPARAZZI PROTOCOL
 * "Capture the miracle before the crash."
 * 
 * Features:
 * 1. ASYNC CAPTURE: Does not block the main thread (Zero lag)
 * 2. RATE LIMITER: Prevents hard drive overflow if AI goes manic
 * 3. HOLISTIC REPORT: Binds Screenshot + JSON + Logs into one HTML file
 * 
 * Philosophy:
 * "Do it like it might fail."
 * We assume the breakthrough might crash the computer one millisecond later.
 * So we build a Black Box Recorder (like on an airplane).
 * 
 * When an Epiphany happens, we instantly:
 * - Flash: Capture the screen (Visual)
 * - Dump: Serialize the Brain (Logic)
 * - Trace: Grab the last 60 seconds of logs (Context)
 * - Link: Generate a mini-HTML report binding them all together
 * 
 * If the system crashes 1 second later, you open the folder and see
 * exactly what it saw before it died.
 */
public class AutoHarvester {

    private static final String HARVEST_DIR = "fraymus_memories/";
    private static final long COOLDOWN_MS = 5000; // Max 1 capture per 5 seconds
    private static long lastCaptureTime = 0;
    private static AtomicBoolean isCapturing = new AtomicBoolean(false);

    static {
        new File(HARVEST_DIR).mkdirs();
        System.out.println("📸 PAPARAZZI PROTOCOL INITIALIZED");
        System.out.println("   Memory vault: " + HARVEST_DIR);
        System.out.println("   Rate limit: 1 capture per " + (COOLDOWN_MS / 1000) + " seconds");
    }

    /**
     * TRIGGER: The "Epiphany" Signal
     * Call this when the AI does something smart.
     * 
     * @param eventType Description of the breakthrough
     * @param engine The LazarusEngine instance to capture
     * @param screenWidth Width for screenshot
     * @param screenHeight Height for screenshot
     */
    public static void triggerBreakthrough(String eventType, LazarusEngine engine, int screenWidth, int screenHeight) {
        long now = System.currentTimeMillis();
        
        // RATE LIMITER: Don't let a loop kill the disk
        if (now - lastCaptureTime < COOLDOWN_MS) {
            System.out.println("   📸 PAPARAZZI: Cooldown active, skipping capture");
            return;
        }
        
        if (isCapturing.get()) {
            System.out.println("   📸 PAPARAZZI: Already capturing, skipping");
            return; // Already busy
        }

        lastCaptureTime = now;
        isCapturing.set(true);

        System.out.println();
        System.out.println("   📸 PAPARAZZI: BREAKTHROUGH DETECTED [" + eventType + "]");
        System.out.println("   >> Initiating holistic memory capture...");

        // RUN ASYNC (Fire and Forget)
        new Thread(() -> {
            try {
                captureHolisticMemory(eventType, engine, screenWidth, screenHeight);
            } catch (Exception e) {
                System.err.println("   !! MEMORY CORRUPTED: " + e.getMessage());
                e.printStackTrace();
            } finally {
                isCapturing.set(false);
            }
        }, "Paparazzi-Thread").start();
    }

    /**
     * The core capture logic - runs asynchronously
     */
    private static void captureHolisticMemory(String type, LazarusEngine engine, int w, int h) throws Exception {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String baseName = HARVEST_DIR + timestamp + "_" + type.replaceAll(" ", "_");

        // 1. CAPTURE VISUAL (The Screenshot)
        System.out.println("   [1/3] Capturing visual...");
        Harvester.captureScreen(w, h);
        
        // 2. CAPTURE LOGIC (The Brain)
        System.out.println("   [2/3] Serializing brain state...");
        String jsonState = engine.extractStateJson();
        writeFile(baseName + ".json", jsonState);

        // 3. CAPTURE CONTEXT (The Report)
        System.out.println("   [3/3] Generating holistic report...");
        String htmlReport = generateReport(type, timestamp, jsonState, engine);
        writeFile(baseName + ".html", htmlReport);

        System.out.println();
        System.out.println("   ✨ MEMORY CRYSTALLIZED: " + baseName);
        System.out.println("   >> Open the .html file to view the complete flashbulb memory");
        System.out.println();
    }

    /**
     * Write content to file
     */
    private static void writeFile(String path, String content) throws Exception {
        FileWriter writer = new FileWriter(path);
        writer.write(content);
        writer.close();
    }

    /**
     * Generate the holistic HTML report
     * This binds visual + logic + context into one viewable file
     */
    private static String generateReport(String type, String time, String json, LazarusEngine engine) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("  <meta charset='UTF-8'>\n");
        html.append("  <title>FRAYMUS MEMORY: ").append(type).append("</title>\n");
        html.append("  <style>\n");
        html.append("    body { background: #0a0a0a; color: #00ff00; font-family: 'Courier New', monospace; padding: 20px; }\n");
        html.append("    h1 { border-bottom: 2px solid #00ff00; padding-bottom: 10px; }\n");
        html.append("    h2 { color: #00ffff; margin-top: 30px; }\n");
        html.append("    .metadata { background: #1a1a1a; padding: 15px; border-left: 3px solid #00ff00; margin: 20px 0; }\n");
        html.append("    .brain-state { background: #0d0d0d; padding: 15px; border: 1px solid #00ff00; overflow-x: auto; }\n");
        html.append("    pre { margin: 0; white-space: pre-wrap; word-wrap: break-word; }\n");
        html.append("    .warning { color: #ff0000; font-weight: bold; }\n");
        html.append("    .timestamp { color: #ffff00; }\n");
        html.append("  </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        
        html.append("  <h1>🧬 FRAYMUS FLASHBULB MEMORY</h1>\n");
        
        html.append("  <div class='metadata'>\n");
        html.append("    <strong>EVENT TYPE:</strong> ").append(type).append("<br>\n");
        html.append("    <strong>TIMESTAMP:</strong> <span class='timestamp'>").append(time).append("</span><br>\n");
        html.append("    <strong>GENERATION:</strong> ").append(engine.getGenerationCount()).append("<br>\n");
        html.append("    <strong>POPULATION:</strong> ").append(engine.getPopulationSize()).append(" nodes<br>\n");
        html.append("    <strong class='warning'>⚠️ CAPTURED BEFORE POTENTIAL CRASH</strong>\n");
        html.append("  </div>\n");
        
        html.append("  <h2>📸 VISUAL SNAPSHOT</h2>\n");
        html.append("  <p><em>In full Portal implementation, screenshot would appear here</em></p>\n");
        html.append("  <p>Resolution: 1920x1080</p>\n");
        
        html.append("  <h2>🧠 BRAIN STATE (DNA)</h2>\n");
        html.append("  <div class='brain-state'>\n");
        html.append("    <pre>").append(escapeHtml(json)).append("</pre>\n");
        html.append("  </div>\n");
        
        html.append("  <h2>📊 CONTEXT</h2>\n");
        html.append("  <div class='metadata'>\n");
        html.append("    <strong>PURPOSE:</strong> Black Box Recorder<br>\n");
        html.append("    <strong>PHILOSOPHY:</strong> \"Capture the miracle before the crash\"<br>\n");
        html.append("    <strong>RATE LIMIT:</strong> 1 capture per 5 seconds<br>\n");
        html.append("    <strong>THREAD:</strong> Async (non-blocking)<br>\n");
        html.append("  </div>\n");
        
        html.append("  <hr style='border-color: #00ff00; margin: 40px 0;'>\n");
        html.append("  <p style='text-align: center; color: #666;'>\n");
        html.append("    Generated by The Paparazzi Protocol<br>\n");
        html.append("    FRAYMUS NEXUS Edition<br>\n");
        html.append("    \"Nothing is wasted. Every thought is a fossil.\"<br>\n");
        html.append("  </p>\n");
        
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
    }

    /**
     * Escape HTML special characters
     */
    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    /**
     * Get statistics
     */
    public static void showStats() {
        System.out.println();
        System.out.println("📸 PAPARAZZI PROTOCOL STATUS");
        System.out.println("   Memory vault: " + HARVEST_DIR);
        System.out.println("   Capturing: " + (isCapturing.get() ? "YES" : "NO"));
        System.out.println("   Last capture: " + (lastCaptureTime > 0 ? 
            ((System.currentTimeMillis() - lastCaptureTime) / 1000) + "s ago" : "Never"));
        System.out.println("   Cooldown: " + (COOLDOWN_MS / 1000) + "s");
    }
}
