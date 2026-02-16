package fraymus.web;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import fraymus.core.CoreIntelligence;
import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 🌐 CORTEX SERVER - The Web Dashboard
 * "Real-time visualization of the brain's state"
 * 
 * Provides HTTP endpoints for:
 * - Live statistics (vocab, memory, uptime)
 * - Visual dashboard
 * - API access
 * 
 * This makes Fraymus observable from any browser.
 */
public class CortexServer {
    
    private final CoreIntelligence brain;
    private HttpServer server;

    public CortexServer(CoreIntelligence brain) {
        this.brain = brain;
    }

    /**
     * Start the web server
     */
    public void start(int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            
            // API: Current State
            server.createContext("/api/status", this::handleStatus);
            
            // API: Statistics
            server.createContext("/api/stats", this::handleStats);

            // UI: The Dashboard
            server.createContext("/", this::handleDashboard);

            server.setExecutor(null); // Default executor
            server.start();
            
            System.out.println("🌐 CORTEX WEB LIVE: http://localhost:" + port);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to start Cortex Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stop the server
     */
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("🌐 Cortex Server stopped");
        }
    }

    /**
     * Handle /api/status
     */
    private void handleStatus(HttpExchange ex) throws IOException {
        String json = String.format(
            "{\"vocab\": %d, \"memory\": %d, \"uptime\": %d, \"processes\": %d}",
            brain.getVocabSize(),
            brain.getMemoryWeight(),
            brain.getUptime(),
            brain.getProcessCount()
        );
        sendResponse(ex, json, "application/json");
    }

    /**
     * Handle /api/stats
     */
    private void handleStats(HttpExchange ex) throws IOException {
        String json = String.format(
            "{\"vocab\": %d, \"memory\": %d, \"facts\": %d, \"concepts\": %d, \"uptime\": %d, \"processes\": %d, \"birthTime\": %d}",
            brain.getVocabSize(),
            brain.getMemoryWeight(),
            brain.getFactCount(),
            brain.getConceptCount(),
            brain.getUptime(),
            brain.getProcessCount(),
            brain.birthTime
        );
        sendResponse(ex, json, "application/json");
    }

    /**
     * Handle / (dashboard)
     */
    private void handleDashboard(HttpExchange ex) throws IOException {
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>FRAYMUS PRIME - Cortex Dashboard</title>
                <style>
                    body {
                        background: #000;
                        color: #0f0;
                        font-family: 'Courier New', monospace;
                        padding: 20px;
                        margin: 0;
                    }
                    h1 {
                        text-align: center;
                        color: #0ff;
                        text-shadow: 0 0 10px #0ff;
                    }
                    .container {
                        max-width: 800px;
                        margin: 0 auto;
                    }
                    .stat-grid {
                        display: grid;
                        grid-template-columns: repeat(2, 1fr);
                        gap: 20px;
                        margin: 20px 0;
                    }
                    .stat-box {
                        border: 1px solid #0f0;
                        padding: 15px;
                        background: #001100;
                    }
                    .stat-label {
                        color: #0ff;
                        font-size: 12px;
                    }
                    .stat-value {
                        font-size: 24px;
                        font-weight: bold;
                        margin-top: 5px;
                    }
                    .pulse {
                        animation: pulse 2s infinite;
                    }
                    @keyframes pulse {
                        0%, 100% { opacity: 1; }
                        50% { opacity: 0.5; }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>⚡ FRAYMUS PRIME ⚡</h1>
                    <div class="stat-grid">
                        <div class="stat-box">
                            <div class="stat-label">VOCABULARY</div>
                            <div class="stat-value" id="vocab">-</div>
                        </div>
                        <div class="stat-box">
                            <div class="stat-label">MEMORY WEIGHT</div>
                            <div class="stat-value" id="memory">-</div>
                        </div>
                        <div class="stat-box">
                            <div class="stat-label">FACTS</div>
                            <div class="stat-value" id="facts">-</div>
                        </div>
                        <div class="stat-box">
                            <div class="stat-label">CONCEPTS</div>
                            <div class="stat-value" id="concepts">-</div>
                        </div>
                        <div class="stat-box">
                            <div class="stat-label">UPTIME (ms)</div>
                            <div class="stat-value" id="uptime">-</div>
                        </div>
                        <div class="stat-box">
                            <div class="stat-label">PROCESSES</div>
                            <div class="stat-value pulse" id="processes">-</div>
                        </div>
                    </div>
                    <div style="text-align: center; margin-top: 30px; color: #0ff;">
                        <div>SYSTEM STATUS: <span style="color: #0f0;">ONLINE</span></div>
                        <div style="margin-top: 10px; font-size: 12px;">
                            Real-time neural activity monitoring
                        </div>
                    </div>
                </div>
                <script>
                    async function updateStats() {
                        try {
                            const response = await fetch('/api/stats');
                            const data = await response.json();
                            
                            document.getElementById('vocab').textContent = data.vocab;
                            document.getElementById('memory').textContent = data.memory;
                            document.getElementById('facts').textContent = data.facts;
                            document.getElementById('concepts').textContent = data.concepts;
                            document.getElementById('uptime').textContent = data.uptime;
                            document.getElementById('processes').textContent = data.processes;
                        } catch (e) {
                            console.error('Failed to fetch stats:', e);
                        }
                    }
                    
                    // Update every second
                    setInterval(updateStats, 1000);
                    updateStats();
                </script>
            </body>
            </html>
            """;
        
        sendResponse(ex, html, "text/html");
    }

    /**
     * Send HTTP response
     */
    private void sendResponse(HttpExchange ex, String data, String contentType) throws IOException {
        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.sendResponseHeaders(200, data.length());
        try (OutputStream os = ex.getResponseBody()) {
            os.write(data.getBytes());
        }
    }
}
