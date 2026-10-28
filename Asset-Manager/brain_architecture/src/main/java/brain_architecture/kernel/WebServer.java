package brain_architecture.kernel;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Simple HTTP server for Fraynix kernel web UI.
 */
public final class WebServer implements AutoCloseable {
    private final HttpServer server;
    private final OfflineKernel kernel;
    private final Path webRoot;

    public WebServer(OfflineKernel kernel, Path webRoot, int port) throws IOException {
        this.kernel = kernel;
        this.webRoot = webRoot.toAbsolutePath().normalize();
        
        Files.createDirectories(this.webRoot);
        
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::handleRoot);
        server.createContext("/api/status", this::handleStatus);
        server.createContext("/api/submit", this::handleSubmit);
        server.createContext("/api/substrates", this::handleSubstrates);
        server.setExecutor(null);
        server.start();
    }

    private void handleRoot(HttpExchange exchange) throws IOException {
        Path indexPath = webRoot.resolve("index.html");
        if (!Files.exists(indexPath)) {
            String html = generateIndexHtml();
            Files.writeString(indexPath, html, StandardCharsets.UTF_8);
        }
        serveFile(exchange, indexPath);
    }

    private void handleStatus(HttpExchange exchange) throws IOException {
        Map<String, Object> status = kernel.getStatus();
        String json = "{\"status\":" + escapeJson(status.toString()) + "}";
        sendJson(exchange, json);
    }

    private void handleSubmit(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method not allowed");
            return;
        }
        
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        // Parse simple form: goal=...&model=...
        String goal = extractParam(body, "goal");
        String modelId = extractParam(body, "model");
        
        if (goal == null || goal.isBlank()) {
            sendError(exchange, 400, "Missing goal parameter");
            return;
        }
        
        TaskEnvelope.Builder builder = TaskEnvelope.builder(goal);
        if (modelId != null && !modelId.isBlank()) {
            builder.strategy("substrate", "local-model");
            builder.strategy("model.id", modelId);
        }
        
        KernelRunResult result = kernel.runTask(builder.build(), ValidationSuite.empty());
        String json = String.format("{\"success\":%s,\"trace\":%s}", 
            result.getTrace().getOutcome().equals("PASSED"),
            escapeJson(result.getTrace().toSummaryMap().toString()));
        sendJson(exchange, json);
    }

    private void handleSubstrates(HttpExchange exchange) throws IOException {
        // Trigger substrate bootstrap and return report
        FoundationalSubstrateReport report = kernel.bootstrapFoundationalSubstrates(List.of());
        Map<String, Object> summary = report.toSummaryMap();
        int models = (int) summary.get("models");
        int tokenizers = (int) summary.get("tokenizers");
        int runtimes = (int) summary.get("runtimes");
        String json = String.format("{\"models\":%d,\"tokenizers\":%d,\"runtimes\":%d}", models, tokenizers, runtimes);
        sendJson(exchange, json);
    }

    private void serveFile(HttpExchange exchange, Path path) throws IOException {
        if (!Files.exists(path)) {
            sendError(exchange, 404, "Not found");
            return;
        }
        
        String contentType = getContentType(path);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] content = Files.readAllBytes(path);
        exchange.sendResponseHeaders(200, content.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(content);
        }
    }

    private void sendJson(HttpExchange exchange, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] content = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, content.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(content);
        }
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        byte[] content = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, content.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(content);
        }
    }

    private String extractParam(String body, String key) {
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                try {
                    return java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                } catch (Exception e) {
                    return kv[1];
                }
            }
        }
        return null;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    private String getContentType(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        if (name.endsWith(".html")) return "text/html";
        if (name.endsWith(".css")) return "text/css";
        if (name.endsWith(".js")) return "application/javascript";
        if (name.endsWith(".json")) return "application/json";
        return "text/plain";
    }

    private String generateIndexHtml() {
        return """
<!DOCTYPE html>
<html>
<head>
    <title>Fraynix Kernel</title>
    <style>
        body { font-family: system-ui, sans-serif; max-width: 900px; margin: 40px auto; padding: 20px; background: #0f172a; color: #e2e8f0; }
        h1 { color: #38bdf8; border-bottom: 2px solid #1e293b; padding-bottom: 10px; }
        .section { background: #1e293b; padding: 20px; border-radius: 8px; margin: 20px 0; }
        label { display: block; margin: 10px 0 5px; color: #94a3b8; }
        input, textarea, select { width: 100%; padding: 10px; background: #0f172a; border: 1px solid #334155; color: #e2e8f0; border-radius: 4px; box-sizing: border-box; }
        button { background: #3b82f6; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; margin-top: 10px; }
        button:hover { background: #2563eb; }
        #result { background: #0f172a; padding: 15px; border-radius: 4px; margin-top: 15px; white-space: pre-wrap; font-family: monospace; }
        .stat { display: inline-block; background: #334155; padding: 10px 15px; border-radius: 4px; margin-right: 10px; }
        .stat-label { color: #94a3b8; font-size: 12px; }
        .stat-value { color: #38bdf8; font-size: 18px; font-weight: bold; }
    </style>
</head>
<body>
    <h1>Fraynix Kernel</h1>
    
    <div class="section">
        <h2>Status</h2>
        <div id="status">Loading...</div>
    </div>
    
    <div class="section">
        <h2>Submit Task</h2>
        <label>Goal</label>
        <textarea id="goal" rows="3" placeholder="Describe what you want the kernel to do..."></textarea>
        <label>Model (optional)</label>
        <select id="model">
            <option value="">Auto-select</option>
            <option value="gpt2">GPT-2 Bundle</option>
            <option value="llama3">Llama 3 (Ollama)</option>
            <option value="dolphin-mistral">Dolphin Mistral (Ollama)</option>
        </select>
        <button onclick="submitTask()">Run Task</button>
        <div id="result"></div>
    </div>
    
    <div class="section">
        <h2>Substrates</h2>
        <div id="substrates">Loading...</div>
    </div>
    
    <script>
        function loadStatus() {
            fetch('/api/status').then(r => r.json()).then(d => {
                document.getElementById('status').textContent = d.status;
            });
        }
        
        function loadSubstrates() {
            fetch('/api/substrates').then(r => r.json()).then(d => {
                document.getElementById('substrates').innerHTML = 
                    '<span class="stat"><div class="stat-label">Models</div><div class="stat-value">' + d.models + '</div></span>' +
                    '<span class="stat"><div class="stat-label">Tokenizers</div><div class="stat-value">' + d.tokenizers + '</div></span>' +
                    '<span class="stat"><div class="stat-label">Runtimes</div><div class="stat-value">' + d.runtimes + '</div></span>';
            });
        }
        
        function submitTask() {
            const goal = document.getElementById('goal').value;
            const model = document.getElementById('model').value;
            if (!goal) return;
            
            const params = new URLSearchParams();
            params.append('goal', goal);
            if (model) params.append('model', model);
            
            fetch('/api/submit', { method: 'POST', body: params })
                .then(r => r.json())
                .then(d => {
                    document.getElementById('result').textContent = JSON.stringify(d, null, 2);
                });
        }
        
        loadStatus();
        loadSubstrates();
        setInterval(loadStatus, 5000);
    </script>
</body>
</html>
""";
    }

    @Override
    public void close() {
        server.stop(0);
    }

    public static void main(String[] args) throws Exception {
        Path workspace = Path.of(args.length > 0 ? args[0] : ".").toAbsolutePath().normalize();
        Path webRoot = workspace.resolve(".northstar-kernel/web");
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;
        
        try (OfflineKernel kernel = new OfflineKernel(KernelConfiguration.defaultForWorkspace(workspace));
             WebServer server = new WebServer(kernel, webRoot, port)) {
            System.out.println("Fraynix web UI: http://localhost:" + port);
            System.out.println("Press Ctrl+C to stop");
            Thread.sleep(Long.MAX_VALUE);
        }
    }
}
