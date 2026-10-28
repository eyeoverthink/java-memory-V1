package fraymus.ultimate;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import fraymus.contract.SystemStateContract;
import fraymus.core.OllamaBridge;
import fraymus.neural.AudioEngine;
import fraymus.neural.HDCSimilarityEngine;
import fraymus.neural.SentientCore;
import fraymus.neural.VisionEngine;
import fraymus.ollama.ConversationMemory;
import fraymus.ollama.OllamaChatSpine;
import fraymus.senses.HeadroomInterface;
import fraymus.ultimate.UltimateArchitectureExporter.ExportArtifact;
import fraymus.ultimate.UltimateCommandCatalog.CommandEntry;
import fraymus.ultimate.UltimateCommandCatalog.CommandSection;
import fraymus.ultimate.UltimateKnowledgeIndex.IndexedDocument;
import fraymus.ultimate.UltimateKnowledgeIndex.SearchHit;
import fraymus.ultimate.UltimateSubsystemRegistry.UltimateSubsystem;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Fresh browser-first UI for the unified Fraymus / Fraynix system.
 */
public final class UltimateUiServer {

    private static final String RESOURCE_ROOT = "/ultimate-ui";
    private static final int DEFAULT_PORT = 8181;
    private static final long SYSTEM_STATE_STALE_AFTER_MS = 15_000L;
    private static final String WEEK1_DOC = "WEEK1_NERVOUS_SYSTEM_COMPLETE.md";
    private static final String WEEK2_DOC = "WEEK2_SENSORY_EXPANSION_COMPLETE.md";
    private static final String WEEK3_DOC = "WEEK3_MEMORY_SUBSTRATE_COMPLETE.md";
    private static final String WEEK4_DOC = "WEEK4_INTEGRATION_OPTIMIZATION_COMPLETE.md";
    private static final String BRAIN_REFERENCE_DOC = "BRAIN_SYSTEMS_REFERENCE.md";
    private static final String PRODUCT_NAME = "Fraymus Nexus // Fraynix Offline AGI Operator Surface";
    private static final String PRODUCT_TAGLINE =
        "Kernel, boot, factory, broadcast, and native runtime surface for the Fraynix offline AGI organism.";

    private final UltimateSystem system;
    private final int preferredPort;
    private final String companionModel = OllamaBridge.resolveAvailableModel("llama3.2:latest");
    private final OllamaChatSpine companionChat = new OllamaChatSpine(companionModel);
    private final ConversationMemory companionMemory = new ConversationMemory(12);
    private final Deque<JSONObject> companionTranscript = new ArrayDeque<>();
    private final long serverConstructedAtMs = System.currentTimeMillis();

    private HttpServer server;
    private ExecutorService executor;
    private ExportArtifact latestExport;
    private long serverStartedAtMs = 0L;
    private SentientCore companionCore;
    private HDCSimilarityEngine companionHdc;
    private VisionEngine companionVision;
    private AudioEngine companionAudio;
    private HeadroomInterface companionHeadroom;
    private String companionHeadroomLastAction = "";
    private String companionHeadroomLastText = "";
    private Path companionHeadroomLastVisual;
    private long companionHeadroomLastUpdated = 0L;
    private volatile Function<String, String> bootShell;

    public UltimateUiServer() {
        this(discoverAssetManagerDir(Path.of("").toAbsolutePath().normalize()), DEFAULT_PORT);
    }

    public UltimateUiServer(Path assetManagerDir, int preferredPort) {
        this.system = new UltimateSystem(assetManagerDir);
        this.preferredPort = preferredPort <= 0 ? 0 : preferredPort;
    }

    public void attachBootShell(Function<String, String> shell) {
        this.bootShell = shell;
    }

    public static void main(String[] args) throws InterruptedException {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0].trim());
            } catch (NumberFormatException ignored) {
                port = DEFAULT_PORT;
            }
        }

        UltimateUiServer uiServer = new UltimateUiServer(discoverAssetManagerDir(Path.of("").toAbsolutePath().normalize()), port);
        Runtime.getRuntime().addShutdownHook(new Thread(uiServer::stop));
        int activePort = uiServer.start();
        System.out.println("FRAYMUS NEXUS OPERATOR SURFACE LIVE");
        System.out.println("Open: http://localhost:" + activePort);
        new CountDownLatch(1).await();
    }

    public synchronized int start() {
        if (server != null) {
            return server.getAddress().getPort();
        }

        ensureArtifacts();
        int port = preferredPort;
        int attempts = port == 0 ? 1 : 10;

        for (int attempt = 0; attempt < attempts; attempt++) {
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                executor = Executors.newCachedThreadPool();
                server.setExecutor(executor);
                wireContexts(server);
                server.start();
                serverStartedAtMs = System.currentTimeMillis();
                return server.getAddress().getPort();
            } catch (BindException bindException) {
                if (port == 0) {
                    throw new IllegalStateException("Unable to bind Ultimate UI server on an ephemeral port.", bindException);
                }
                port++;
            } catch (IOException exception) {
                throw new IllegalStateException("Unable to start Ultimate UI server.", exception);
            }
        }

        throw new IllegalStateException("Unable to find an open port for the Ultimate UI server.");
    }

    public synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        if (companionHeadroom != null && companionHeadroom.isLive()) {
            companionHeadroom.offAir();
        }
        system.shutdown();
    }

    private void wireContexts(HttpServer httpServer) {
        httpServer.createContext("/api/overview", this::handleOverview);
        httpServer.createContext("/api/companion", this::handleCompanion);
        httpServer.createContext("/api/companion/chat", this::handleCompanionChat);
        httpServer.createContext("/api/companion/dream", this::handleCompanionDream);
        httpServer.createContext("/api/companion/headroom", this::handleCompanionHeadroom);
        httpServer.createContext("/api/media", this::handleMedia);
        httpServer.createContext("/api/systems", this::handleSystems);
        httpServer.createContext("/api/operator", this::handleOperator);
        httpServer.createContext("/api/surfaces", this::handleSurfaces);
        httpServer.createContext("/api/knowledge", this::handleKnowledge);
        httpServer.createContext("/api/system-state", this::handleSystemState);
        httpServer.createContext("/api/search", this::handleSearch);
        httpServer.createContext("/api/doc", this::handleDocument);
        httpServer.createContext("/api/runtime", this::handleRuntime);
        httpServer.createContext("/api/session", this::handleSession);
        httpServer.createContext("/api/execute", this::handleExecute);
        httpServer.createContext("/api/refresh", this::handleRefresh);
        httpServer.createContext("/api/export", this::handleExport);
        httpServer.createContext("/assets/", this::handleAsset);
        httpServer.createContext("/artifacts/", this::handleArtifact);
        httpServer.createContext("/", this::handlePage);
    }

    private void handleOverview(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        sendJson(exchange, 200, buildOverviewJson());
    }

    private void handleCompanion(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        sendJson(exchange, 200, buildCompanionJson());
    }

    private void handleCompanionChat(HttpExchange exchange) throws IOException {
        if (!ensureWritable(exchange)) {
            return;
        }
        JSONObject payload = readJsonBody(exchange);
        String prompt = payload.optString("prompt", "").trim();
        if (prompt.isBlank()) {
            sendJson(exchange, 400, errorJson("Missing prompt."));
            return;
        }
        String visualPath = payload.optString("visualPath", "").trim();
        String audioPath = payload.optString("audioPath", "").trim();
        sendJson(exchange, 200, companionChatJson(prompt, visualPath, audioPath));
    }

    private void handleCompanionDream(HttpExchange exchange) throws IOException {
        if (!ensureWritable(exchange)) {
            return;
        }
        JSONObject payload = readJsonBody(exchange);
        String prompt = payload.optString("prompt", "").trim();
        if (prompt.isBlank()) {
            sendJson(exchange, 400, errorJson("Missing prompt."));
            return;
        }
        String visualPath = payload.optString("visualPath", "").trim();
        String audioPath = payload.optString("audioPath", "").trim();
        boolean previewOnly = payload.optBoolean("previewOnly", false);
        JSONObject result = companionDreamJson(prompt, visualPath, audioPath, previewOnly);
        if (!previewOnly) {
            Path visual = resolveCompanionMediaPath(visualPath, Set.of(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp"));
            Path audio = resolveCompanionMediaPath(audioPath, Set.of(".wav", ".mp3", ".flac", ".m4a", ".ogg"));
            rememberCompanionTurn("user", "Manifest visual: " + prompt, visual, audio, "");
            Path dreamVisual = null;
            if (result.optBoolean("generated", false)) {
                JSONObject visualJson = result.optJSONObject("visual");
                if (visualJson != null) {
                    dreamVisual = resolveCompanionMediaPath(
                        visualJson.optString("path", ""),
                        Set.of(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp")
                    );
                }
            }
            rememberCompanionTurn(
                "assistant",
                result.optString("message", "Dream lane complete."),
                dreamVisual,
                null,
                result.optString("summary", "")
            );
        }
        result.put("transcript", companionTranscriptArray());
        sendJson(exchange, 200, result);
    }

    private void handleCompanionHeadroom(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 200, companionHeadroomStatusJson());
            return;
        }
        if (!ensureWritable(exchange)) {
            return;
        }
        JSONObject payload = readJsonBody(exchange);
        String action = payload.optString("action", "").trim();
        if (action.isBlank()) {
            sendJson(exchange, 400, errorJson("Missing action."));
            return;
        }
        boolean previewOnly = payload.optBoolean("previewOnly", false);
        String text = payload.optString("text", "").trim();
        String visualPath = payload.optString("visualPath", "").trim();
        double intensity = Math.max(0.0d, Math.min(1.0d, payload.optDouble("intensity", 0.72d)));
        sendJson(exchange, 200, companionHeadroomActionJson(action, text, visualPath, intensity, previewOnly));
    }

    private void handleSystems(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        String path = exchange.getRequestURI().getPath();
        String base = "/api/systems";
        if (path.length() > base.length() + 1) {
            String id = path.substring(base.length() + 1);
            Optional<UltimateSubsystem> subsystem = system.registry().find(id);
            if (subsystem.isEmpty()) {
                sendJson(exchange, 404, errorJson("Unknown subsystem: " + id));
                return;
            }
            sendJson(exchange, 200, buildSubsystemJson(subsystem.get()));
            return;
        }
        sendJson(exchange, 200, buildSystemsJson());
    }

    private void handleOperator(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        sendJson(exchange, 200, buildOperatorJson());
    }

    private void handleSurfaces(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        String path = exchange.getRequestURI().getPath();
        String base = "/api/surfaces";
        if (path.length() > base.length() + 1) {
            String id = path.substring(base.length() + 1);
            Optional<UltimateCommandCatalog> catalog = system.commandCatalogs().stream()
                .filter(item -> item.surfaceId().equalsIgnoreCase(id))
                .findFirst();
            if (catalog.isEmpty()) {
                sendJson(exchange, 404, errorJson("Unknown command surface: " + id));
                return;
            }
            sendJson(exchange, 200, buildSurfaceJson(catalog.get()));
            return;
        }
        sendJson(exchange, 200, buildSurfacesJson());
    }

    private void handleKnowledge(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        sendJson(exchange, 200, buildKnowledgeJson());
    }

    private void handleSystemState(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        String consumer = queryParams(exchange.getRequestURI()).getOrDefault("consumer", "browser");
        sendJson(exchange, 200, buildSystemStateJson(consumer, null, null));
    }

    private void handleSearch(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        Map<String, String> params = queryParams(exchange.getRequestURI());
        String query = params.getOrDefault("q", "").trim();
        if (query.isBlank()) {
            sendJson(exchange, 400, errorJson("Missing query parameter 'q'."));
            return;
        }
        sendJson(exchange, 200, buildSearchJson(query));
    }

    private void handleDocument(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        Map<String, String> params = queryParams(exchange.getRequestURI());
        String path = params.getOrDefault("path", "").trim();
        if (path.isBlank()) {
            sendJson(exchange, 400, errorJson("Missing query parameter 'path'."));
            return;
        }

        Optional<IndexedDocument> document = system.knowledgeIndex().documents().stream()
            .filter(item -> system.knowledgeIndex().relativePath(item.path()).toLowerCase(Locale.ROOT)
                .contains(path.toLowerCase(Locale.ROOT)))
            .findFirst();

        if (document.isEmpty()) {
            sendJson(exchange, 404, errorJson("No indexed document matched: " + path));
            return;
        }

        sendJson(exchange, 200, documentJson(document.get()));
    }

    private void handleRuntime(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        sendJson(exchange, 200, buildRuntimeJson());
    }

    private void handleSession(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        sendJson(exchange, 200, buildSessionJson());
    }

    private void handleExecute(HttpExchange exchange) throws IOException {
        JSONObject payload = readJsonBody(exchange);
        String command = payload.optString("command", "").trim();
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod()) && command.isBlank()) {
            command = queryParams(exchange.getRequestURI()).getOrDefault("command", "").trim();
        }

        if (command.isBlank()) {
            sendJson(exchange, 400, errorJson("Missing command."));
            return;
        }

        String normalized = command.toLowerCase(Locale.ROOT);
        String head = command.split("\\s+", 2)[0].toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_\\-]", "");
        if (head.equals("quit") || head.equals("exit")) {
            sendJson(exchange, 400, errorJson("Exit is disabled over HTTP. Type exit in the Fraynix terminal to shut down."));
            return;
        }
        Function<String, String> shell = bootShell;
        if (shell != null) {
            String output;
            try {
                output = shell.apply(command);
            } catch (RuntimeException error) {
                output = "Boot shell error: " + error.getMessage();
            }
            JSONObject response = new JSONObject();
            response.put("command", command);
            response.put("mode", "fraynix-boot");
            response.put("output", output == null ? "" : output);
            try {
                response.put("runtime", runtimeSnapshotJson(system.immortalKernel().snapshot()));
                response.put("artifacts", artifactJson(ensureArtifacts()));
            } catch (RuntimeException error) {
                response.put("runtime", new JSONObject());
                response.put("artifacts", new JSONArray());
            }
            sendJson(exchange, 200, response);
            return;
        }
        if (normalized.startsWith("launch ")) {
            JSONObject blocked = new JSONObject();
            blocked.put("command", command);
            blocked.put("output", "External subsystem launches are blocked in the browser runner. Use the launch command shown on the page from a terminal.");
            blocked.put("runtime", runtimeSnapshotJson(system.immortalKernel().snapshot()));
            sendJson(exchange, 200, blocked);
            return;
        }

        boolean routedThroughAi = shouldRouteBrowserExecuteThroughAi(command);
        String output = routedThroughAi ? system.routeAiPrompt(command) : system.execute(command);
        JSONObject response = new JSONObject();
        response.put("command", command);
        response.put("mode", routedThroughAi ? "ai-router" : "direct-command");
        response.put("output", output == null ? "" : output);
        response.put("runtime", runtimeSnapshotJson(system.immortalKernel().snapshot()));
        response.put("artifacts", artifactJson(ensureArtifacts()));
        sendJson(exchange, 200, response);
    }

    private boolean shouldRouteBrowserExecuteThroughAi(String command) {
        if (command == null) {
            return false;
        }
        String trimmed = command.trim();
        if (trimmed.isBlank()) {
            return false;
        }

        String lowered = trimmed.toLowerCase(Locale.ROOT);
        if (lowered.startsWith("ai ") || lowered.startsWith("run ")) {
            return true;
        }
        if (looksLikeBrowserDirectCommand(trimmed)) {
            return false;
        }
        if (lowered.contains("intent=") && lowered.contains("target=")) {
            return true;
        }
        if (lowered.endsWith("?")) {
            return true;
        }
        if (lowered.startsWith("what ")
            || lowered.startsWith("which ")
            || lowered.startsWith("why ")
            || lowered.startsWith("how ")
            || lowered.startsWith("should ")
            || lowered.startsWith("inspect ")
            || lowered.startsWith("compare ")
            || lowered.startsWith("tell me ")
            || lowered.startsWith("use ")
            || lowered.startsWith("manifest ")) {
            return true;
        }

        String[] tokens = trimmed.split("\\s+");
        return tokens.length >= 3;
    }

    private boolean looksLikeBrowserDirectCommand(String command) {
        if (command == null || command.isBlank()) {
            return false;
        }
        String trimmed = command.trim();
        if (trimmed.contains("::")) {
            return true;
        }
        String head = trimmed.split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
        return browserKnownCommandHeads().contains(head);
    }

    private Set<String> browserKnownCommandHeads() {
        LinkedHashSet<String> heads = new LinkedHashSet<>(Set.of(
            "status", "help", "session", "systems", "system", "search", "show",
            "readmes", "commands", "catalog", "paths", "goto", "jump", "route",
            "math", "abstract", "demo", "refresh", "export", "persist",
            "selfbuild", "native", "engine", "sentient", "maxheadroom",
            "build", "desktop", "physics", "exodus", "aether", "braid",
            "gravity", "fusion", "nexus", "miving", "launch"
        ));
        for (UltimateCommandCatalog catalog : system.commandCatalogs()) {
            for (CommandSection section : catalog.sections()) {
                for (CommandEntry entry : section.commands()) {
                    String syntax = entry.syntax();
                    if (syntax == null || syntax.isBlank()) {
                        continue;
                    }
                    String head = syntax.trim().split("\\s+", 2)[0]
                        .replaceAll("[^a-zA-Z0-9_-]", "")
                        .toLowerCase(Locale.ROOT);
                    if (!head.isBlank()) {
                        heads.add(head);
                    }
                }
            }
        }
        return heads;
    }

    private void handleRefresh(HttpExchange exchange) throws IOException {
        if (!ensureWritable(exchange)) {
            return;
        }
        system.refreshState();
        latestExport = system.exportArtifacts(null);
        JSONObject response = new JSONObject();
        response.put("status", "refreshed");
        response.put("overview", buildOverviewJson());
        sendJson(exchange, 200, response);
    }

    private void handleExport(HttpExchange exchange) throws IOException {
        if (!ensureWritable(exchange)) {
            return;
        }
        sendJson(exchange, 200, artifactJson(ensureArtifacts()));
    }

    private void handleAsset(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        String requestPath = exchange.getRequestURI().getPath();
        String relative = requestPath.substring("/assets/".length());
        serveClasspathResource(exchange, RESOURCE_ROOT + "/assets/" + relative);
    }

    private void handleArtifact(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        ExportArtifact artifact = ensureArtifacts();
        Path baseDir = artifact.outputDir();
        String relative = exchange.getRequestURI().getPath().substring("/artifacts/".length());
        Path requested = baseDir.resolve(relative).normalize();
        if (!requested.startsWith(baseDir) || !Files.exists(requested) || Files.isDirectory(requested)) {
            sendJson(exchange, 404, errorJson("Artifact not found: " + relative));
            return;
        }
        sendBytes(exchange, 200, Files.readAllBytes(requested), contentTypeFor(requested.getFileName().toString()));
    }

    private void handleMedia(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        String rawPath = queryParams(exchange.getRequestURI()).getOrDefault("path", "").trim();
        if (rawPath.isBlank()) {
            sendJson(exchange, 400, errorJson("Missing query parameter 'path'."));
            return;
        }
        Path media = Path.of(rawPath).toAbsolutePath().normalize();
        if (!isAllowedCompanionMedia(media) || !Files.exists(media) || Files.isDirectory(media)) {
            sendJson(exchange, 404, errorJson("Media not found: " + rawPath));
            return;
        }
        sendBytes(exchange, 200, Files.readAllBytes(media), contentTypeFor(media.getFileName().toString()));
    }

    private void handlePage(HttpExchange exchange) throws IOException {
        if (!ensureGet(exchange)) {
            return;
        }
        String path = exchange.getRequestURI().getPath();
        String resource = pageResource(path);
        if (resource == null) {
            sendBytes(exchange, 404, loadResourceBytes(RESOURCE_ROOT + "/404.html"), "text/html; charset=UTF-8");
            return;
        }
        sendBytes(exchange, 200, loadResourceBytes(resource), "text/html; charset=UTF-8");
    }

    private JSONObject buildOverviewJson() {
        JSONObject json = new JSONObject();
        json.put("productName", PRODUCT_NAME);
        json.put("tagline", PRODUCT_TAGLINE);
        json.put("paths", new JSONObject()
            .put("assetManager", system.assetManagerDir().toString().replace('\\', '/'))
            .put("repoRoot", system.repoRoot().toString().replace('\\', '/'))
            .put("artifacts", ensureArtifacts().outputDir().toString().replace('\\', '/')));
        json.put("counts", new JSONObject()
            .put("subsystems", system.registry().all().size())
            .put("families", system.registry().familyCounts().size())
            .put("subsystemAgiStrata", system.registry().agiStrataCounts().size())
            .put("documents", system.knowledgeIndex().documentCount())
            .put("readmes", system.knowledgeIndex().readmeCount())
            .put("modules", system.knowledgeIndex().moduleCount())
            .put("commandSurfaces", system.commandCatalogs().size())
            .put("commandSections", system.sectionCount())
            .put("cataloguedCommands", system.commandCount())
            .put("kernelPreferredSurfaces", system.operatorManifest().kernelPreferredSurfaceCount())
            .put("kernelOverlapSections", system.operatorManifest().kernelOverlapSectionCount())
            .put("bootTreeBranches", system.operatorManifest().bootTreeBranchCount())
            .put("uncataloguedBootBranches", system.operatorManifest().uncataloguedBootBranchCount()));
        json.put("legacySpine", legacyOperatorSpineJson());
        json.put("legacyCommandSpine", legacyCommandSpineJson());
        json.put("commandDecks", browserCommandDecksJson());
        json.put("families", countArray(system.registry().familyCounts()));
        json.put("subsystemAgiStrata", countArray(system.registry().agiStrataCounts()));
        json.put("topModules", topCountArray(system.knowledgeIndex().moduleCounts(), 10));
        json.put("topSignals", topCountArray(system.knowledgeIndex().signalCounts(), 10));
        json.put("surfaceAgiStrata", countArray(system.operatorManifest().surfaceAgiStrataCounts()));
        json.put("sectionAgiStrata", countArray(system.operatorManifest().sectionAgiStrataCounts()));
        json.put("subsystems", systemsArray(system.registry().all().stream().limit(6).toList()));
        json.put("anchorDocs", documentsArray(system.knowledgeIndex().readmes().stream().limit(8).toList()));
        json.put("abstract", system.execute("abstract"));
        json.put("artifacts", artifactJson(ensureArtifacts()));
        json.put("principles", new JSONArray()
            .put("Browser shell serves the kernel instead of narrating around it")
            .put("FraynixBoot, FraymusNexus, HeadroomInterface, FraymusUI, and MaxHeadroomLive stay first-class")
            .put("Commands, build lanes, and runtime state appear before architecture prose")
            .put("Headroom remains the visible signal, but never detaches from boot and factory truth")
            .put("Readable routing stays grounded in live command catalogs and source paths")
            .put("Overview remains a map, not the main character"));
        json.put("pages", new JSONArray()
            .put(pageJson("Headroom", "/"))
            .put(pageJson("Overview", "/overview"))
            .put(pageJson("Companion", "/companion"))
            .put(pageJson("Session", "/session"))
            .put(pageJson("Systems", "/systems"))
            .put(pageJson("Surfaces", "/surfaces"))
            .put(pageJson("Knowledge", "/knowledge"))
            .put(pageJson("Runtime", "/runtime"))
            .put(pageJson("Launchpad", "/launchpad")));
        return json;
    }

    private JSONObject legacyOperatorSpineJson() {
        JSONArray pillars = new JSONArray();
        int availableCount = 0;

        JSONObject boot = legacyPillarJson(
            "boot",
            "kernel bootstrap",
            "FraynixBoot",
            "Unified bootstrap for the offline shell, AEON stack, command menu, and persistent organism routing.",
            system.assetManagerDir().resolve("src/main/java/fraymus/FraynixBoot.java"),
            system.registry().find("fraynix-boot"),
            "status",
            "/runtime",
            "Open runtime"
        );
        pillars.put(boot);
        availableCount += boot.optBoolean("available") ? 1 : 0;

        JSONObject factory = legacyPillarJson(
            "factory",
            "operating system factory",
            "FraymusNexus",
            "Java factory that births the bare-metal Fraynix operating system and exodus build pipeline.",
            system.assetManagerDir().resolve("src/main/java/fraymus/core/FraymusNexus.java"),
            Optional.empty(),
            "exodus",
            "/launchpad?cmd=exodus",
            "Queue exodus"
        );
        pillars.put(factory);
        availableCount += factory.optBoolean("available") ? 1 : 0;

        JSONObject broadcast = legacyPillarJson(
            "broadcast",
            "signal controller",
            "HeadroomInterface",
            "Broadcast controller for speech, visual thought projection, glitch state, and live signal presence.",
            system.assetManagerDir().resolve("src/main/java/fraymus/senses/HeadroomInterface.java"),
            system.registry().find("video-cortex"),
            "maxheadroom",
            "/",
            "Open Headroom"
        );
        pillars.put(broadcast);
        availableCount += broadcast.optBoolean("available") ? 1 : 0;

        JSONObject cockpit = legacyPillarJson(
            "cockpit",
            "visual cockpit",
            "FraymusUI",
            "Arena-first cockpit with inspectors, command terminal, self-code panes, and living world telemetry.",
            system.assetManagerDir().resolve("src/main/java/fraymus/FraymusUI.java"),
            system.registry().find("fraymus-engine"),
            "desktop",
            "/systems?focus=fraymus-engine&detail=inspect",
            "Inspect cockpit"
        );
        pillars.put(cockpit);
        availableCount += cockpit.optBoolean("available") ? 1 : 0;

        JSONObject liveHeadroom = legacyPillarJson(
            "live-repl",
            "interactive signal host",
            "MaxHeadroomLive",
            "Interactive live-signal REPL for questions, thought visuals, entropy tuning, and visible reasoning output.",
            system.assetManagerDir().resolve("src/main/java/fraymus/MaxHeadroomLive.java"),
            Optional.empty(),
            "maxheadroom",
            "/launchpad?cmd=maxheadroom",
            "Queue maxheadroom"
        );
        pillars.put(liveHeadroom);
        availableCount += liveHeadroom.optBoolean("available") ? 1 : 0;

        return new JSONObject()
            .put("summary", "The original operator spine is boot, factory, broadcast, cockpit, and live signal control. The browser shell should expose those lanes first.")
            .put("availableCount", availableCount)
            .put("totalCount", pillars.length())
            .put("quickCommands", new JSONArray()
                .put("status")
                .put("help")
                .put("build desktop")
                .put("exodus")
                .put("maxheadroom")
                .put("native status"))
            .put("pillars", pillars);
    }

    private JSONObject legacyPillarJson(
        String id,
        String layer,
        String title,
        String summary,
        Path sourcePath,
        Optional<UltimateSubsystem> subsystem,
        String primaryCommand,
        String pageHref,
        String pageLabel
    ) {
        boolean available = sourcePath != null && Files.exists(sourcePath);
        JSONObject json = new JSONObject()
            .put("id", id)
            .put("layer", layer)
            .put("title", title)
            .put("summary", summary)
            .put("available", available)
            .put("path", relativeOrAbsolute(sourcePath))
            .put("primaryCommand", primaryCommand)
            .put("pageHref", pageHref)
            .put("pageLabel", pageLabel);
        if (subsystem.isPresent()) {
            json.put("subsystem", subsystemJson(subsystem.get()));
        } else {
            json.put("subsystem", JSONObject.NULL);
        }
        return json;
    }

    private JSONObject legacyCommandSpineJson() {
        JSONArray sections = new JSONArray();
        Optional<UltimateCommandCatalog> fraynixSurface = system.commandCatalogs().stream()
            .filter(catalog -> "fraynix".equalsIgnoreCase(catalog.surfaceId()))
            .findFirst();

        if (fraynixSurface.isPresent()) {
            UltimateCommandCatalog catalog = fraynixSurface.get();
            for (String sectionId : List.of("core", "cognition", "reinforcement", "sentience", "exodus", "daemons")) {
                catalog.findSection(sectionId).ifPresent(section -> {
                    JSONArray commands = new JSONArray();
                    section.commands().stream()
                        .limit(6)
                        .forEach(entry -> commands.put(new JSONObject()
                            .put("syntax", entry.syntax())
                            .put("description", entry.description())));
                    sections.put(new JSONObject()
                        .put("id", section.id())
                        .put("title", section.title())
                        .put("theme", section.theme())
                        .put("commandCount", section.commandCount())
                        .put("inspectCommand", "commands fraynix " + section.id())
                        .put("routeCommand", "route fraynix " + section.id())
                        .put("commands", commands));
                });
            }
        }

        return new JSONObject()
            .put("summary", "These menu families most closely match the original Fraynix shell hierarchy and should dominate the browser operator surface.")
            .put("presets", new JSONArray()
                .put("status")
                .put("help")
                .put("physics")
                .put("build desktop")
                .put("exodus")
                .put("maxheadroom")
                .put("engine status")
                .put("sentient status"))
            .put("sections", sections);
    }

    private JSONObject browserCommandDecksJson() {
        return new JSONObject()
            .put("launchpad", launchpadCommandDeckJson())
            .put("session", sessionCommandDeckJson())
            .put("runtime", runtimeCommandDeckJson())
            .put("broadcast", broadcastCommandDeckJson())
            .put("factory", factoryCommandDeckJson());
    }

    private JSONObject launchpadCommandDeckJson() {
        return commandDeckJson(
            "Launchpad shell deck",
            "Exact shell-native commands that keep Launchpad tied to the Fraynix menu instead of a generic browser runner.",
            "Try: status\nTry: build desktop\nTry: exodus\nTry: maxheadroom\nTry: native status",
            new JSONArray()
                .put(commandDeckEntryJson("Shell status", "status", "boot check"))
                .put(commandDeckEntryJson("Shell help", "help", "menu map"))
                .put(commandDeckEntryJson("Physics", "physics", "body controls"))
                .put(commandDeckEntryJson("Build desktop", "build desktop", "factory lane"))
                .put(commandDeckEntryJson("Exodus", "exodus", "bare-metal lane"))
                .put(commandDeckEntryJson("MaxHeadroom", "maxheadroom", "broadcast lane"))
                .put(commandDeckEntryJson("Native status", "native status", "kernel state"))
                .put(commandDeckEntryJson("Engine status", "engine status", "organism state"))
        );
    }

    private JSONObject sessionCommandDeckJson() {
        return commandDeckJson(
            "Session shell deck",
            "Session should foreground shell, build, broadcast, and proof commands before secondary dashboards.",
            "Try: status\nTry: ai run native status\nTry: build desktop\nTry: exodus\nTry: maxheadroom",
            new JSONArray()
                .put(commandDeckEntryJson("Shell status", "status", "boot truth"))
                .put(commandDeckEntryJson("Native status", "ai run native status", "ai run lane"))
                .put(commandDeckEntryJson("Build desktop", "build desktop", "factory lane"))
                .put(commandDeckEntryJson("Exodus", "exodus", "bare-metal lane"))
                .put(commandDeckEntryJson("MaxHeadroom", "maxheadroom", "signal host"))
                .put(commandDeckEntryJson("Sentient status", "sentient status", "reasoning core"))
                .put(commandDeckEntryJson("Engine status", "engine status", "organism state"))
                .put(commandDeckEntryJson("Proof lanes", "ai What are the strongest living proof lanes active right now?", "proof focus"))
                .put(commandDeckEntryJson("Guided demo", "demo guided", "walkthrough"))
        );
    }

    private JSONObject runtimeCommandDeckJson() {
        return commandDeckJson(
            "Runtime body deck",
            "Runtime should keep kernel controls visible, while still exposing factory and broadcast exits one hop away.",
            "Try: native status\nTry: aether\nTry: braid\nTry: build desktop\nTry: exodus",
            new JSONArray()
                .put(commandDeckEntryJson("Native status", "native status", "kernel body"))
                .put(commandDeckEntryJson("Aether", "aether", "field ignition"))
                .put(commandDeckEntryJson("Braid", "braid", "topology lock"))
                .put(commandDeckEntryJson("Nexus awaken", "nexus awaken", "organism wake"))
                .put(commandDeckEntryJson("Gravity start", "gravity start", "physics lane"))
                .put(commandDeckEntryJson("Fusion start", "fusion start", "idea reactor"))
                .put(commandDeckEntryJson("Build desktop", "build desktop", "factory lane"))
                .put(commandDeckEntryJson("Exodus", "exodus", "bare-metal lane"))
                .put(commandDeckEntryJson("MaxHeadroom", "maxheadroom", "signal host"))
                .put(commandDeckEntryJson("Engine status", "engine status", "organism state"))
        );
    }

    private JSONObject broadcastCommandDeckJson() {
        return commandDeckJson(
            "Broadcast handoff deck",
            "Companion should hand the operator back into shell-native broadcast, runtime, and factory lanes when the signal is ready.",
            "Try: maxheadroom\nTry: status\nTry: ai run native status\nTry: exodus",
            new JSONArray()
                .put(commandDeckEntryJson("MaxHeadroom", "maxheadroom", "live signal"))
                .put(commandDeckEntryJson("Shell status", "status", "boot truth"))
                .put(commandDeckEntryJson("Native status", "ai run native status", "ai run lane"))
                .put(commandDeckEntryJson("Build desktop", "build desktop", "cockpit factory"))
                .put(commandDeckEntryJson("Exodus", "exodus", "bare-metal lane"))
        );
    }

    private JSONObject factoryCommandDeckJson() {
        return commandDeckJson(
            "Factory deck",
            "The browser should keep FraymusNexus, build lanes, and Exodus visibly closer to the operator than abstract summaries.",
            "Try: build desktop\nTry: build shell\nTry: build llm\nTry: exodus",
            new JSONArray()
                .put(commandDeckEntryJson("Build desktop", "build desktop", "cockpit"))
                .put(commandDeckEntryJson("Build shell", "build shell", "keyboard + shell"))
                .put(commandDeckEntryJson("Build llm", "build llm", "bare-metal inference"))
                .put(commandDeckEntryJson("Build explorer", "build explorer", "FrayFS UI"))
                .put(commandDeckEntryJson("Desktop", "desktop", "enter construct"))
                .put(commandDeckEntryJson("Exodus", "exodus", "bare-metal image"))
        );
    }

    private JSONObject commandDeckJson(String title, String summary, String placeholder, JSONArray commands) {
        return new JSONObject()
            .put("title", title)
            .put("summary", summary)
            .put("placeholder", placeholder)
            .put("commands", commands);
    }

    private JSONObject commandDeckEntryJson(String label, String command, String note) {
        return new JSONObject()
            .put("label", label)
            .put("command", command)
            .put("note", note);
    }

    private JSONObject buildCompanionJson() {
        JSONObject json = new JSONObject();
        List<Path> visuals = scanCompanionMedia(Set.of(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp"), 18, companionVisualRoots());
        List<Path> audios = scanCompanionMedia(Set.of(".wav", ".mp3", ".flac", ".m4a", ".ogg"), 12, companionAudioRoots());
        JSONObject latestDream = latestDreamJson();
        JSONObject headroom = companionHeadroomStatusJson();
        json.put("title", "Fraynix Companion");
        json.put("status", new JSONObject()
            .put("chatAvailable", companionChat.testConnection())
            .put("model", companionModel)
            .put("memoryMessages", companionMemory.size())
            .put("visualLibraryCount", visuals.size())
            .put("audioLibraryCount", audios.size())
            .put("visualDreamAvailable", companionDreamAvailable())
            .put("dreamOutputCount", countDreamOutputs())
            .put("audioAnalysisAvailable", Files.exists(system.assetManagerDir().resolve("speechbrain_interface.py")))
            .put("headroomAvailable", headroom.optBoolean("available"))
            .put("headroomLive", headroom.optBoolean("live"))
            .put("headroomBroadcasts", headroom.optInt("totalBroadcasts"))
            .put("headroomThoughts", headroom.optInt("totalThoughts")));
        json.put("transcript", companionTranscriptArray());
        json.put("visualLibrary", mediaArray(visuals, "image"));
        json.put("audioLibrary", mediaArray(audios, "audio"));
        json.put("latestDream", latestDream == null ? JSONObject.NULL : latestDream);
        json.put("headroom", headroom);
        json.put("commandDeck", broadcastCommandDeckJson());
        json.put("systemState", buildSystemStateJson("companion", headroom, json.getJSONObject("status")));
        json.put("quickPrompts", new JSONArray()
            .put("What are the strongest living proof lanes active right now?")
            .put("Inspect the selected image and tell me whether it belongs in HeadroomInterface, FraymusUI, or MaxHeadroomLive.")
            .put("Inspect the selected audio and tell me which shell or broadcast lane should follow.")
            .put("Manifest a phi-harmonic MaxHeadroomLive visual from this prompt.")
            .put("Prepare the strongest grounded reply, then hand it to HeadroomInterface.")
            .put("Given the current session, which build, exodus, or review lane should I touch next?"));
        return json;
    }

    private JSONObject buildSystemsJson() {
        JSONObject json = new JSONObject();
        json.put("counts", new JSONObject()
            .put("subsystems", system.registry().all().size())
            .put("families", system.registry().familyCounts().size())
            .put("agiStrata", system.registry().agiStrataCounts().size()));
        json.put("families", countArray(system.registry().familyCounts()));
        json.put("agiStrata", countArray(system.registry().agiStrataCounts()));
        json.put("systems", systemsArray(system.registry().all()));
        return json;
    }

    private JSONObject buildSubsystemJson(UltimateSubsystem subsystem) {
        JSONObject json = subsystemJson(subsystem);
        json.put("route", system.execute("route " + subsystem.id()));
        return json;
    }

    private JSONObject buildOperatorJson() {
        UltimateOperatorManifest manifest = system.operatorManifest();
        JSONObject json = new JSONObject();
        json.put("authoritativeShell", "ultimate-ui");
        json.put("executionPath", "UltimateSystem.execute");
        json.put("surfaceCount", manifest.surfaceCount());
        json.put("sectionCount", manifest.sectionCount());
        json.put("commandCount", manifest.commandCount());
        json.put("kernelPreferredSurfaceCount", manifest.kernelPreferredSurfaceCount());
        json.put("kernelOverlapSectionCount", manifest.kernelOverlapSectionCount());
        json.put("bootTreeBranchCount", manifest.bootTreeBranchCount());
        json.put("uncataloguedBootBranchCount", manifest.uncataloguedBootBranchCount());
        json.put("livingProofLaneCount", manifest.livingProofLaneCount());
        json.put("priorityCounts", countArray(manifest.priorityCounts()));
        json.put("surfaceAgiStrataCounts", countArray(manifest.surfaceAgiStrataCounts()));
        json.put("sectionAgiStrataCounts", countArray(manifest.sectionAgiStrataCounts()));
        json.put("livingProofLaneAgiStrataCounts", countArray(manifest.livingProofLaneAgiStrataCounts()));
        json.put("notes", new JSONArray()
            .put("The operator manifest is built from live command catalogs, not a disconnected documentation artifact.")
            .put("Native overlap is marked so the browser shell can prefer kernel-owned execution where names collide.")
            .put("Legacy breadth stays visible instead of being erased while unification is still incomplete.")
            .put("Discovered FraynixBoot branches are surfaced alongside curated catalog sections.")
            .put("AGI role tags classify operator surfaces beyond migration status alone.")
            .put("Living proof lanes keep Morse, Ouroboros, organism validators, proof harnesses, and substrate transduction visible as first-class operator reality."));
        JSONArray surfaces = new JSONArray();
        for (UltimateOperatorManifest.SurfaceManifest surface : manifest.surfaces()) {
            surfaces.put(surfaceJson(surface));
        }
        json.put("surfaces", surfaces);
        JSONArray livingProofLanes = new JSONArray();
        for (UltimateOperatorManifest.LivingProofLane lane : manifest.livingProofLanes()) {
            livingProofLanes.put(livingProofLaneJson(lane));
        }
        json.put("livingProofLanes", livingProofLanes);
        return json;
    }

    private JSONObject buildSurfacesJson() {
        JSONObject json = new JSONObject();
        UltimateOperatorManifest manifest = system.operatorManifest();
        json.put("surfaceCount", manifest.surfaceCount());
        json.put("sectionCount", manifest.sectionCount());
        json.put("commandCount", manifest.commandCount());
        json.put("kernelPreferredSurfaceCount", manifest.kernelPreferredSurfaceCount());
        json.put("kernelOverlapSectionCount", manifest.kernelOverlapSectionCount());
        json.put("bootTreeBranchCount", manifest.bootTreeBranchCount());
        json.put("uncataloguedBootBranchCount", manifest.uncataloguedBootBranchCount());
        json.put("surfaceAgiStrataCounts", countArray(manifest.surfaceAgiStrataCounts()));
        json.put("sectionAgiStrataCounts", countArray(manifest.sectionAgiStrataCounts()));
        JSONArray surfaces = new JSONArray();
        for (UltimateOperatorManifest.SurfaceManifest surface : manifest.surfaces()) {
            surfaces.put(surfaceJson(surface));
        }
        json.put("surfaces", surfaces);
        return json;
    }

    private JSONObject buildSurfaceJson(UltimateCommandCatalog catalog) {
        return system.operatorManifest()
            .findSurface(catalog.surfaceId())
            .map(this::surfaceJson)
            .orElseGet(() -> new JSONObject()
                .put("id", catalog.surfaceId())
                .put("title", catalog.surfaceTitle())
                .put("description", catalog.surfaceDescription()));
    }

    private JSONObject buildKnowledgeJson() {
        JSONObject json = new JSONObject();
        json.put("documentCount", system.knowledgeIndex().documentCount());
        json.put("readmeCount", system.knowledgeIndex().readmeCount());
        json.put("moduleCount", system.knowledgeIndex().moduleCount());
        json.put("modules", topCountArray(system.knowledgeIndex().moduleCounts(), 18));
        json.put("signals", topCountArray(system.knowledgeIndex().signalCounts(), 18));
        json.put("readmes", documentsArray(system.knowledgeIndex().readmes().stream().limit(18).toList()));
        json.put("abstract", system.execute("abstract"));
        json.put("systemState", buildSystemStateJson("knowledge", null, null));
        json.put("artifacts", artifactJson(ensureArtifacts()));
        return json;
    }

    private JSONObject buildSearchJson(String query) {
        JSONObject json = new JSONObject();
        json.put("query", query);
        JSONArray results = new JSONArray();
        for (SearchHit hit : system.knowledgeIndex().search(query, 20)) {
            JSONObject result = documentJson(hit.document());
            result.put("score", hit.score());
            results.put(result);
        }
        json.put("results", results);
        json.put("resultCount", results.length());
        return json;
    }

    private JSONObject buildRuntimeJson() {
        JSONObject json = new JSONObject();
        UltimateImmortalKernel.KernelSnapshot snapshot = system.immortalKernel().snapshot();
        json.put("snapshot", runtimeSnapshotJson(snapshot));
        json.put("statusText", system.execute("native status"));
        json.put("systemState", buildSystemStateJson("runtime", null, null));
        json.put("commandDeck", runtimeCommandDeckJson());
        json.put("quickActions", new JSONArray()
            .put("aether")
            .put("braid")
            .put("learn PHI_FIELD")
            .put("nexus awaken")
            .put("gravity start")
            .put("fusion start")
            .put("miving gen 24")
            .put("spatial status")
            .put("native shutdown"));
        return json;
    }

    private JSONObject buildSessionJson() {
        JSONObject json = new JSONObject();
        List<UltimateSelfBuildHarness.ReviewRequest> reviews = system.selfBuildHarness().listReviewRequests();
        List<UltimateSelfBuildHarness.SessionArtifact> artifacts = system.selfBuildHarness().recentRunArtifacts(8);
        Optional<Path> latestProof = latestLiveProofRecord();
        JSONObject ledger = substrateLedgerJson();
        JSONObject connectomePlan = connectomePlanContractJson();
        JSONObject planCounts = connectomePlan.optJSONObject("counts");

        json.put("statusText", system.execute("session"));
        json.put("demoCommand", "demo guided");
        json.put("reviewCommandPattern", "selfbuild review-promote file <path-fragment> :: <goal>");
        json.put("counts", new JSONObject()
            .put("pendingReviews", reviews.stream().filter(review -> "pending".equalsIgnoreCase(review.status())).count())
            .put("reviews", reviews.size())
            .put("recentRuns", artifacts.size())
            .put("proofs", latestProof.isPresent() ? 1 : 0)
            .put("ledgerModels", ledger.optJSONObject("counts") == null ? 0 : ledger.getJSONObject("counts").optInt("models"))
            .put("ledgerRuntimes", ledger.optJSONObject("counts") == null ? 0 : ledger.getJSONObject("counts").optInt("runtimeEntries"))
            .put("weeksReady", planCounts == null ? 0 : planCounts.optInt("weeksReady"))
            .put("weeksTotal", planCounts == null ? 0 : planCounts.optInt("weeksTotal")));
        JSONArray reviewArray = new JSONArray();
        reviews.forEach(review -> reviewArray.put(reviewJson(review)));
        json.put("reviews", reviewArray);
        JSONArray artifactArray = new JSONArray();
        artifacts.forEach(artifact -> artifactArray.put(sessionArtifactJson(artifact)));
        json.put("recentSelfBuildRuns", artifactArray);
        JSONArray proofs = new JSONArray();
        latestProof.ifPresent(path -> proofs.put(new JSONObject()
            .put("title", path.getFileName().toString())
            .put("path", relativeOrAbsolute(path))
            .put("absolutePath", path.toString().replace('\\', '/'))
            .put("href", artifactHref(path))
            .put("modifiedAt", safeLastModified(path))));
        json.put("proofs", proofs);
        json.put("substrateLedger", ledger);
        json.put("connectomePlan", connectomePlan);
        json.put("commandDeck", sessionCommandDeckJson());
        json.put("systemState", buildSystemStateJson("session", null, null));
        return json;
    }

    private JSONObject connectomePlanContractJson() {
        Path sourceRoot = system.assetManagerDir().resolve("src/main/java/fraymus/brain/unified");
        Path testRoot = system.assetManagerDir().resolve("src/test/java/fraymus/brain/unified");
        String orchestratorSource = readTextOrEmpty(sourceRoot.resolve("UnifiedBrainOrchestrator.java"));

        JSONArray weeks = new JSONArray();
        weeks.put(connectomeWeekJson(
            "week1",
            "Week 1 Nervous System",
            "Semantic routing, premotor conversion, and anomaly gut checks.",
            WEEK1_DOC,
            List.of("SemanticRouterBrain.java", "PremotorCortexBrain.java", "EntericNervousSystem.java"),
            List.of("NervousSystemTest.java", "UnifiedBrainOrchestratorNervousSystemTest.java"),
            List.of("wireNervousSystem", "unwireNervousSystem"),
            sourceRoot,
            testRoot,
            orchestratorSource
        ));
        weeks.put(connectomeWeekJson(
            "week2",
            "Week 2 Sensory Expansion",
            "Vision, temporal sequence analysis, and web/surface intake.",
            WEEK2_DOC,
            List.of("OccipitalLobeBrain.java", "TemporalLobeBrain.java", "ParietalLobeBrain.java"),
            List.of("SensoryExpansionTest.java", "UnifiedBrainOrchestratorSensoryTest.java"),
            List.of("wireSensorySystem", "unwireSensorySystem"),
            sourceRoot,
            testRoot,
            orchestratorSource
        ));
        weeks.put(connectomeWeekJson(
            "week3",
            "Week 3 Memory Substrate",
            "Embedding gateway and hippocampal consolidation lanes.",
            WEEK3_DOC,
            List.of("EntorhinalCortex.java", "HippocampusBrain.java"),
            List.of("MemorySubstrateTest.java", "UnifiedBrainOrchestratorMemoryTest.java"),
            List.of("wireMemorySystem", "unwireMemorySystem"),
            sourceRoot,
            testRoot,
            orchestratorSource
        ));
        weeks.put(connectomeWeekJson(
            "week4",
            "Week 4 Integration & Optimization",
            "Dream cycle optimization and unified whole-system wiring.",
            WEEK4_DOC,
            List.of("DreamEngine.java"),
            List.of("DreamEngineTest.java", "UnifiedBrainOrchestratorDreamEngineTest.java"),
            List.of("wireDreamEngine", "unwireDreamEngine", "wireAllSystems", "unwireAllSystems", "getSystemPhiCoherence", "runDreamCycle"),
            sourceRoot,
            testRoot,
            orchestratorSource
        ));

        JSONObject reference = contractDocumentJson("Brain systems reference", system.workspaceRoot().resolve(BRAIN_REFERENCE_DOC));

        int weeksReady = 0;
        int docsReady = reference.optBoolean("exists") ? 1 : 0;
        int docsTotal = 1;
        int componentsReady = 0;
        int componentsTotal = 0;
        int testsReady = 0;
        int testsTotal = 0;
        int methodsReady = 0;
        int methodsTotal = 0;

        for (int i = 0; i < weeks.length(); i++) {
            JSONObject week = weeks.getJSONObject(i);
            JSONObject counts = week.optJSONObject("counts");
            if (week.optBoolean("ready")) {
                weeksReady++;
            }
            docsReady += counts == null ? 0 : counts.optInt("docsReady");
            docsTotal += counts == null ? 0 : counts.optInt("docsTotal");
            componentsReady += counts == null ? 0 : counts.optInt("componentsReady");
            componentsTotal += counts == null ? 0 : counts.optInt("componentsTotal");
            testsReady += counts == null ? 0 : counts.optInt("testsReady");
            testsTotal += counts == null ? 0 : counts.optInt("testsTotal");
            methodsReady += counts == null ? 0 : counts.optInt("methodsReady");
            methodsTotal += counts == null ? 0 : counts.optInt("methodsTotal");
        }

        String status = weeksReady == weeks.length() ? "ready" : "drift";
        String summary = weeksReady == weeks.length()
            ? "Week contract is represented by docs, source lanes, tests, and orchestrator wiring."
            : "Week contract drift detected: " + (weeks.length() - weeksReady) + " week lane(s) are incomplete.";

        return new JSONObject()
            .put("title", "Fraynix 7-day connectome contract")
            .put("status", status)
            .put("plannedDays", 7)
            .put("weeks", weeks)
            .put("reference", reference)
            .put("counts", new JSONObject()
                .put("weeksReady", weeksReady)
                .put("weeksTotal", weeks.length())
                .put("docsReady", docsReady)
                .put("docsTotal", docsTotal)
                .put("componentsReady", componentsReady)
                .put("componentsTotal", componentsTotal)
                .put("testsReady", testsReady)
                .put("testsTotal", testsTotal)
                .put("methodsReady", methodsReady)
                .put("methodsTotal", methodsTotal))
            .put("summary", summary);
    }

    private JSONObject connectomeWeekJson(
        String id,
        String title,
        String focus,
        String docFileName,
        List<String> componentFiles,
        List<String> testFiles,
        List<String> orchestratorMethods,
        Path sourceRoot,
        Path testRoot,
        String orchestratorSource
    ) {
        JSONObject doc = contractDocumentJson(title + " plan doc", system.workspaceRoot().resolve(docFileName));
        JSONArray components = contractFileChecksJson(sourceRoot, componentFiles);
        JSONArray tests = contractFileChecksJson(testRoot, testFiles);
        JSONArray methods = contractMethodChecksJson(orchestratorMethods, orchestratorSource);

        int componentsReady = countTruthy(components, "exists");
        int testsReady = countTruthy(tests, "exists");
        int methodsReady = countTruthy(methods, "present");
        boolean docReady = doc.optBoolean("exists") && doc.optBoolean("claimedComplete");

        int totalChecks = 1 + componentFiles.size() + testFiles.size() + orchestratorMethods.size();
        int readyChecks = (docReady ? 1 : 0) + componentsReady + testsReady + methodsReady;
        boolean ready = readyChecks == totalChecks;
        double readinessScore = totalChecks == 0 ? 1.0d : (double) readyChecks / (double) totalChecks;

        return new JSONObject()
            .put("id", id)
            .put("title", title)
            .put("focus", focus)
            .put("ready", ready)
            .put("readinessScore", Math.round(readinessScore * 1000.0d) / 1000.0d)
            .put("doc", doc)
            .put("components", components)
            .put("tests", tests)
            .put("orchestratorMethods", methods)
            .put("counts", new JSONObject()
                .put("docsReady", docReady ? 1 : 0)
                .put("docsTotal", 1)
                .put("componentsReady", componentsReady)
                .put("componentsTotal", componentFiles.size())
                .put("testsReady", testsReady)
                .put("testsTotal", testFiles.size())
                .put("methodsReady", methodsReady)
                .put("methodsTotal", orchestratorMethods.size()));
    }

    private JSONObject contractDocumentJson(String title, Path path) {
        boolean exists = path != null && Files.exists(path);
        String text = exists ? readTextOrEmpty(path).toLowerCase(Locale.ROOT) : "";
        boolean claimedComplete = exists && text.contains("complete");
        String status = !exists ? "missing" : (claimedComplete ? "complete" : "present");
        return new JSONObject()
            .put("title", title)
            .put("path", relativeOrAbsolute(path))
            .put("absolutePath", path == null ? "" : path.toString().replace('\\', '/'))
            .put("exists", exists)
            .put("claimedComplete", claimedComplete)
            .put("status", status)
            .put("href", exists ? artifactHref(path) : JSONObject.NULL)
            .put("modifiedAt", exists ? safeLastModified(path) : JSONObject.NULL);
    }

    private JSONArray contractFileChecksJson(Path root, List<String> fileNames) {
        JSONArray checks = new JSONArray();
        for (String fileName : fileNames) {
            Path path = root.resolve(fileName).toAbsolutePath().normalize();
            checks.put(new JSONObject()
                .put("name", fileName)
                .put("path", relativeOrAbsolute(path))
                .put("absolutePath", path.toString().replace('\\', '/'))
                .put("exists", Files.exists(path)));
        }
        return checks;
    }

    private JSONArray contractMethodChecksJson(List<String> methodNames, String sourceText) {
        JSONArray checks = new JSONArray();
        String safeSource = sourceText == null ? "" : sourceText;
        for (String methodName : methodNames) {
            checks.put(new JSONObject()
                .put("name", methodName)
                .put("present", safeSource.contains(methodName + "(")));
        }
        return checks;
    }

    private int countTruthy(JSONArray checks, String key) {
        int count = 0;
        for (int i = 0; i < checks.length(); i++) {
            JSONObject check = checks.optJSONObject(i);
            if (check != null && check.optBoolean(key)) {
                count++;
            }
        }
        return count;
    }

    private String readTextOrEmpty(Path path) {
        if (path == null || !Files.exists(path)) {
            return "";
        }
        try {
            return Files.readString(path);
        } catch (IOException ignored) {
            return "";
        }
    }

    private JSONObject companionChatJson(String prompt, String visualPathValue, String audioPathValue) {
        Path visual = resolveCompanionMediaPath(visualPathValue, Set.of(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp"));
        Path audio = resolveCompanionMediaPath(audioPathValue, Set.of(".wav", ".mp3", ".flac", ".m4a", ".ogg"));
        String visualContext = visual == null ? "" : analyzeVisualContext(visual);
        String audioContext = audio == null ? "" : analyzeAudioContext(audio);
        JSONObject dream = isDreamIntent(prompt)
            ? companionDreamJson(prompt, visual, audio, visualContext, audioContext, false)
            : null;
        String reply = companionReply(prompt, visualContext, audioContext, dream);

        companionMemory.addUser(prompt);
        companionMemory.addAssistant(reply);
        rememberCompanionTurn("user", prompt, visual, audio, "");
        Path suggestedVisual = visual == null ? pickSuggestedVisual(prompt) : visual;
        if (dream != null && dream.optBoolean("generated", false)) {
            JSONObject dreamVisual = dream.optJSONObject("visual");
            if (dreamVisual != null) {
                suggestedVisual = resolveCompanionMediaPath(
                    dreamVisual.optString("path", ""),
                    Set.of(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp")
                );
            }
        }
        String summary = joinContext(visualContext, audioContext);
        if (dream != null && !dream.optString("summary", "").isBlank()) {
            if (!summary.isBlank()) {
                summary += "\n";
            }
            summary += dream.optString("summary");
        }
        rememberCompanionTurn("assistant", reply, suggestedVisual, null, summary);

        return new JSONObject()
            .put("reply", reply)
            .put("model", bootShell != null ? "fraynix-boot" : companionModel)
            .put("chatAvailable", companionChat.testConnection())
            .put("visualContext", visualContext)
            .put("audioContext", audioContext)
            .put("transcript", companionTranscriptArray())
            .put("dream", dream == null ? JSONObject.NULL : dream)
            .put("suggestedVisual", suggestedVisual == null ? JSONObject.NULL : mediaAssetJson(suggestedVisual, "image"))
            .put("selectedAudio", audio == null ? JSONObject.NULL : mediaAssetJson(audio, "audio"));
    }

    private String companionReply(String prompt, String visualContext, String audioContext, JSONObject dream) {
        Function<String, String> shell = bootShell;
        if (shell != null) {
            String routed = prompt == null ? "" : prompt.trim();
            String joinedContext = joinContext(visualContext, audioContext);
            if (!joinedContext.isBlank()) {
                routed += "\n\nLocal media context:\n" + joinedContext;
            }
            if (dream != null) {
                routed += "\n\nDream lane:\n" + dream.optString("summary", dream.optString("message", ""));
            }
            try {
                String reply = shell.apply(routed);
                if (reply != null && !reply.isBlank()) {
                    return reply.strip();
                }
            } catch (RuntimeException error) {
                return "Boot shell error: " + error.getMessage();
            }
        }

        String unifiedRouterReply = system.routeAiPrompt(prompt);
        if (unifiedRouterReply != null && !unifiedRouterReply.isBlank()) {
            return unifiedRouterReply;
        }

        String systemPrompt = """
            You are Fraynix, the local offline AGI companion inside Fraymus Nexus.
            Speak directly, concretely, and practically.
            Treat attached image and audio context as real local library artifacts.
            If context is missing, say what is known and what remains unknown.
            Favor next actions, grounded judgment, and operator clarity over vague style.
            """.stripIndent().trim();
        String userPrompt = prompt;
        String joinedContext = joinContext(visualContext, audioContext);
        if (!joinedContext.isBlank()) {
            userPrompt += "\n\nLocal media context:\n" + joinedContext;
        }
        if (dream != null) {
            userPrompt += "\n\nDream lane:\n" + dream.optString("summary", dream.optString("message", ""));
        }

        if (companionChat.testConnection()) {
            try {
                Map<String, Object> options = new LinkedHashMap<>();
                options.put("temperature", 0.25);
                options.put("num_ctx", 8192);
                OllamaChatSpine.Result result = companionChat.chatStream(
                    companionMemory.build(systemPrompt, userPrompt),
                    null,
                    options,
                    "10m",
                    null
                );
                if (result != null && result.content != null && !result.content.isBlank()) {
                    return result.content.strip();
                }
            } catch (Exception ignored) {
                // Fall through to local reply.
            }
        }

        StringBuilder fallback = new StringBuilder();
        fallback.append("Local chat spine is offline, so Fraynix is answering from the companion fallback lane.\n\n");
        fallback.append("Prompt:\n").append(prompt.trim()).append("\n");
        if (!joinedContext.isBlank()) {
            fallback.append("\nContext:\n").append(joinedContext).append("\n");
        }
        if (dream != null) {
            fallback.append("\nDream lane:\n").append(dream.optString("summary", dream.optString("message", "No dream result."))).append("\n");
        }
        fallback.append("\nNext actions:\n");
        fallback.append("- Open the selected context and inspect it in the shell.\n");
        if (dream != null && dream.optBoolean("generated", false)) {
            fallback.append("- Open the generated dreamscape artifact and keep using it as grounded visual context.\n");
        }
        fallback.append("- Use Session to review the current pending selfbuild patch.\n");
        fallback.append("- Bring the local Ollama mouth online if you want full conversational synthesis.");
        return fallback.toString().trim();
    }

    private String groundedCompanionReply(String prompt) {
        if (!isLivingProofLaneQuery(prompt)) {
            return null;
        }

        List<UltimateOperatorManifest.LivingProofLane> ranked = new ArrayList<>(system.operatorManifest().livingProofLanes());
        if (ranked.isEmpty()) {
            return "Grounded operator answer from runtime manifest: no living proof lanes are currently registered.";
        }
        ranked.sort(Comparator
            .comparingInt((UltimateOperatorManifest.LivingProofLane lane) -> livingProofLaneStrengthScore(lane))
            .reversed()
            .thenComparing(UltimateOperatorManifest.LivingProofLane::title));

        UltimateImmortalKernel.KernelSnapshot snapshot = system.immortalKernel().snapshot();
        StringBuilder sb = new StringBuilder();
        sb.append("Grounded operator answer from runtime manifest (no speculative biology lanes).\n\n");
        sb.append("Strongest living proof lanes active in this shell right now:\n");
        int limit = Math.min(3, ranked.size());
        for (int i = 0; i < limit; i++) {
            UltimateOperatorManifest.LivingProofLane lane = ranked.get(i);
            sb.append(i + 1).append(". ").append(lane.title()).append(" (").append(lane.id()).append(")\n");
            sb.append("   role: ").append(lane.operatorRole()).append("\n");
            sb.append("   strata: ").append(String.join(", ", lane.agiStrata())).append("\n");
            sb.append("   evidence: sections=").append(lane.sections().size())
                .append(", focusCommands=").append(lane.focusCommands().size())
                .append(", evidencePaths=").append(lane.evidencePaths().size()).append("\n");
            if (!lane.focusCommands().isEmpty()) {
                sb.append("   next command: ").append(lane.focusCommands().get(0)).append("\n");
            }
            if (!lane.sections().isEmpty()) {
                UltimateOperatorManifest.LivingProofSection section = lane.sections().get(0);
                sb.append("   jump: /surfaces?surface=").append(section.surfaceId())
                    .append("&section=").append(section.sectionId())
                    .append("&proof=").append(lane.id()).append("\n");
            }
        }

        sb.append("\nRuntime state now: nexusState=").append(snapshot.nexusState())
            .append(", aetherActive=").append(snapshot.aetherActive())
            .append(", braidActive=").append(snapshot.braidActive())
            .append(", gravityActive=").append(snapshot.gravityActive())
            .append(", fusionActive=").append(snapshot.fusionActive()).append("\n");
        sb.append("Unknowns: per-lane touch recency is tracked in browser memory and is not yet ranked in this companion route.\n");
        sb.append("Next action: open /surfaces?proof=").append(ranked.get(0).id())
            .append(" then /systems?proof=").append(ranked.get(0).id())
            .append(" for focused subsystem actions.");
        return sb.toString().trim();
    }

    private boolean isLivingProofLaneQuery(String prompt) {
        String lowered = prompt == null ? "" : prompt.toLowerCase(Locale.ROOT);
        if (!(lowered.contains("proof lane") || lowered.contains("living proof"))) {
            return false;
        }
        return lowered.contains("strong")
            || lowered.contains("active")
            || lowered.contains("right now")
            || lowered.contains("which")
            || lowered.contains("what");
    }

    private int livingProofLaneStrengthScore(UltimateOperatorManifest.LivingProofLane lane) {
        int score = 0;
        score += lane.sections().size() * 3;
        score += lane.focusCommands().size() * 2;
        score += lane.evidencePaths().size();
        if (lane.proofLane()) {
            score += 4;
        }
        if (lane.kernelDiscipline()) {
            score += 3;
        }
        if (lane.organismCore()) {
            score += 2;
        }
        if (lane.governingCore()) {
            score += 1;
        }
        return score;
    }

    private JSONObject companionDreamJson(String prompt, String visualPathValue, String audioPathValue, boolean previewOnly) {
        Path visual = resolveCompanionMediaPath(visualPathValue, Set.of(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp"));
        Path audio = resolveCompanionMediaPath(audioPathValue, Set.of(".wav", ".mp3", ".flac", ".m4a", ".ogg"));
        String visualContext = visual == null ? "" : analyzeVisualContext(visual);
        String audioContext = audio == null ? "" : analyzeAudioContext(audio);
        return companionDreamJson(prompt, visual, audio, visualContext, audioContext, previewOnly);
    }

    private JSONObject companionDreamJson(
        String prompt,
        Path visual,
        Path audio,
        String visualContext,
        String audioContext,
        boolean previewOnly
    ) {
        JSONObject state = buildDreamState(prompt, visualContext, audioContext);
        JSONObject response = new JSONObject()
            .put("prompt", prompt)
            .put("previewOnly", previewOnly)
            .put("state", state)
            .put("selectedVisual", visual == null ? JSONObject.NULL : mediaAssetJson(visual, "image"))
            .put("selectedAudio", audio == null ? JSONObject.NULL : mediaAssetJson(audio, "audio"));

        if (previewOnly) {
            return response
                .put("generated", false)
                .put("status", "preview")
                .put("message", "Dream preview ready.")
                .put("summary", "Previewed dream state for the current prompt without invoking VideoCortex.");
        }

        if (!companionDreamAvailable()) {
            return response
                .put("generated", false)
                .put("status", "unavailable")
                .put("message", "The local dream lane is not available yet. VideoCortex.py or a Python launcher is missing.")
                .put("summary", "Dream lane unavailable; Fraynix could not manifest a new visual from this prompt.");
        }

        return manifestDreamVisual(response, state);
    }

    private JSONObject companionHeadroomActionJson(
        String action,
        String text,
        String visualPathValue,
        double intensity,
        boolean previewOnly
    ) {
        Path visual = resolveCompanionMediaPath(visualPathValue, Set.of(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp"));
        JSONObject response = companionHeadroomStatusJson()
            .put("action", action)
            .put("previewOnly", previewOnly)
            .put("text", text)
            .put("selectedVisual", visual == null ? JSONObject.NULL : mediaAssetJson(visual, "image"))
            .put("intensity", intensity);

        if (previewOnly) {
            return response
                .put("status", "preview")
                .put("message", "Headroom preview ready.")
                .put("summary", "Previewed the Max Headroom signal action without launching the broadcaster.");
        }

        if (!companionHeadroomAvailable()) {
            return response
                .put("status", "unavailable")
                .put("message", "HeadroomNode.py is not available for the companion signal lane.")
                .put("summary", "The Max Headroom broadcaster is unavailable on this workspace.")
                .put("available", false);
        }

        try {
            switch (action.toLowerCase(Locale.ROOT)) {
                case "go-live", "golive", "live" -> {
                    companionHeadroom().goLive();
                    if (!companionHeadroom().isLive()) {
                        return response
                            .put("status", "error")
                            .put("message", "Headroom did not stay live after launch.")
                            .put("summary", "The signal broadcaster could not remain on air.")
                            .put("live", false);
                    }
                    rememberHeadroomAction("go-live", "", visual);
                    return companionHeadroomStatusJson()
                        .put("action", "go-live")
                        .put("status", "live")
                        .put("message", "Headroom is now live.")
                        .put("summary", "The Max Headroom signal lane is on air.");
                }
                case "off-air", "offair", "stop" -> {
                    if (companionHeadroom != null) {
                        companionHeadroom.offAir();
                    }
                    rememberHeadroomAction("off-air", "", visual);
                    return companionHeadroomStatusJson()
                        .put("action", "off-air")
                        .put("status", "off-air")
                        .put("message", "Headroom went off air.")
                        .put("summary", "The Max Headroom signal lane is now idle.");
                }
                case "speak" -> {
                    if (text.isBlank()) {
                        return response
                            .put("status", "error")
                            .put("message", "Missing text for speak.")
                            .put("summary", "Headroom speak needs text to send into the signal.");
                    }
                    ensureCompanionHeadroomLive();
                    companionHeadroom().setNeuralActivity(intensity);
                    companionHeadroom().speak(text);
                    rememberHeadroomAction("speak", text, visual);
                    return companionHeadroomStatusJson()
                        .put("action", "speak")
                        .put("status", "broadcasting")
                        .put("message", "Headroom spoke the current Fraynix reply.")
                        .put("summary", "The Max Headroom signal voiced the selected text.");
                }
                case "broadcast" -> {
                    if (text.isBlank()) {
                        return response
                            .put("status", "error")
                            .put("message", "Missing text for broadcast.")
                            .put("summary", "Headroom broadcast needs speech content.");
                    }
                    ensureCompanionHeadroomLive();
                    companionHeadroom().setNeuralActivity(intensity);
                    if (visual != null) {
                        companionHeadroom().showThought(visual.toString());
                        companionHeadroom().glitch(Math.max(0.25d, intensity));
                        companionHeadroom().speak(text);
                    } else {
                        companionHeadroom().broadcast(text, text, intensity);
                    }
                    rememberHeadroomAction("broadcast", text, visual);
                    return companionHeadroomStatusJson()
                        .put("action", "broadcast")
                        .put("status", "broadcasting")
                        .put("message", "Headroom broadcast the current Fraynix reply.")
                        .put("summary", visual == null
                            ? "The Max Headroom signal generated its own thought stream from the reply."
                            : "The Max Headroom signal broadcast the selected thought image with the reply.");
                }
                case "glitch" -> {
                    ensureCompanionHeadroomLive();
                    companionHeadroom().glitch(intensity);
                    rememberHeadroomAction("glitch", "", visual);
                    return companionHeadroomStatusJson()
                        .put("action", "glitch")
                        .put("status", "broadcasting")
                        .put("message", "Headroom glitch pulse triggered.")
                        .put("summary", "The Max Headroom signal emitted a glitch pulse.");
                }
                default -> {
                    return response
                        .put("status", "error")
                        .put("message", "Unknown Headroom action: " + action)
                        .put("summary", "Requested an unsupported Max Headroom signal action.");
                }
            }
        } catch (Exception exception) {
            return companionHeadroomStatusJson()
                .put("action", action)
                .put("status", "error")
                .put("message", "Headroom action failed: " + exception.getMessage())
                .put("summary", "The Max Headroom signal action threw an exception.");
        }
    }

    private JSONObject companionHeadroomStatusJson() {
        JSONObject json = new JSONObject()
            .put("available", companionHeadroomAvailable())
            .put("live", companionHeadroom != null && companionHeadroom.isLive())
            .put("lastAction", companionHeadroomLastAction == null ? "" : companionHeadroomLastAction)
            .put("lastText", companionHeadroomLastText == null ? "" : companionHeadroomLastText)
            .put("lastUpdated", companionHeadroomLastUpdated)
            .put("nodeScript", relativeOrAbsolute(system.assetManagerDir().resolve("HeadroomNode.py")))
            .put("selectedVisual", companionHeadroomLastVisual == null ? JSONObject.NULL : mediaAssetJson(companionHeadroomLastVisual, "image"));

        if (companionHeadroom != null) {
            json.put("launcher", companionHeadroom.launcherUsed());
            json.put("totalBroadcasts", companionHeadroom.totalBroadcasts());
            json.put("totalThoughts", companionHeadroom.totalThoughts());
            json.put("stats", companionHeadroom.getStats());
        } else {
            json.put("launcher", "");
            json.put("totalBroadcasts", 0);
            json.put("totalThoughts", 0);
            json.put("stats", "Headroom idle.");
        }
        return json;
    }

    private boolean companionHeadroomAvailable() {
        return Files.exists(system.assetManagerDir().resolve("HeadroomNode.py"));
    }

    private void ensureCompanionHeadroomLive() {
        companionHeadroom().goLive();
        if (!companionHeadroom().isLive()) {
            throw new IllegalStateException("Headroom signal is not live.");
        }
    }

    private void rememberHeadroomAction(String action, String text, Path visual) {
        companionHeadroomLastAction = action == null ? "" : action;
        companionHeadroomLastText = text == null ? "" : text;
        companionHeadroomLastVisual = visual;
        companionHeadroomLastUpdated = System.currentTimeMillis();
    }

    private void rememberCompanionTurn(String role, String content, Path visual, Path audio, String contextSummary) {
        JSONObject turn = new JSONObject()
            .put("role", role)
            .put("content", content)
            .put("createdAt", System.currentTimeMillis())
            .put("visual", visual == null ? JSONObject.NULL : mediaAssetJson(visual, "image"))
            .put("audio", audio == null ? JSONObject.NULL : mediaAssetJson(audio, "audio"))
            .put("contextSummary", contextSummary == null ? "" : contextSummary);
        companionTranscript.addLast(turn);
        while (companionTranscript.size() > 24) {
            companionTranscript.removeFirst();
        }
    }

    private JSONArray companionTranscriptArray() {
        JSONArray array = new JSONArray();
        if (companionTranscript.isEmpty()) {
            array.put(new JSONObject()
                .put("role", "assistant")
                .put("content", "I am the Fraynix companion lane. Bring me a prompt, and optionally pick a local image or audio artifact so I can answer with real local context.")
                .put("createdAt", System.currentTimeMillis())
                .put("contextSummary", ""));
            return array;
        }
        companionTranscript.forEach(array::put);
        return array;
    }

    private JSONArray mediaArray(List<Path> paths, String kind) {
        JSONArray array = new JSONArray();
        paths.forEach(path -> array.put(mediaAssetJson(path, kind)));
        return array;
    }

    private JSONObject mediaAssetJson(Path path, String kind) {
        JSONObject json = new JSONObject()
            .put("kind", kind)
            .put("name", path.getFileName().toString())
            .put("path", path.toString().replace('\\', '/'))
            .put("label", relativeOrAbsolute(path))
            .put("href", mediaHref(path))
            .put("sizeBytes", safeSize(path))
            .put("modifiedAt", safeLastModified(path));
        if ("image".equals(kind)) {
            try {
                BufferedImage image = ImageIO.read(path.toFile());
                if (image != null) {
                    json.put("width", image.getWidth());
                    json.put("height", image.getHeight());
                }
            } catch (IOException ignored) {
                // Best effort only.
            }
        }
        return json;
    }

    private List<Path> companionVisualRoots() {
        return List.of(
            system.assetManagerDir().resolve("dreamscape_output"),
            system.assetManagerDir().resolve("attached_assets"),
            system.assetManagerDir().resolve("test_image.png")
        );
    }

    private List<Path> companionAudioRoots() {
        return List.of(
            system.assetManagerDir().resolve("test_sound.wav"),
            system.assetManagerDir().resolve("test_speech.wav"),
            system.assetManagerDir().resolve("attached_assets")
        );
    }

    private List<Path> scanCompanionMedia(Set<String> extensions, int limit, List<Path> roots) {
        Set<Path> discovered = new LinkedHashSet<>();
        for (Path root : roots) {
            if (root == null || !Files.exists(root)) {
                continue;
            }
            try {
                if (Files.isRegularFile(root) && hasExtension(root, extensions)) {
                    discovered.add(root.toAbsolutePath().normalize());
                    continue;
                }
                if (!Files.isDirectory(root)) {
                    continue;
                }
                try (var files = Files.walk(root, 3)) {
                    files.filter(Files::isRegularFile)
                        .filter(path -> hasExtension(path, extensions))
                        .forEach(path -> discovered.add(path.toAbsolutePath().normalize()));
                }
            } catch (IOException ignored) {
                // Best effort media scan.
            }
        }

        List<Path> ordered = new ArrayList<>(discovered);
        ordered.sort(Comparator.comparing(this::safeLastModified).reversed().thenComparing(Path::toString));
        return ordered.stream().limit(limit).toList();
    }

    private boolean hasExtension(Path path, Set<String> extensions) {
        String lower = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return extensions.stream().anyMatch(lower::endsWith);
    }

    private Path resolveCompanionMediaPath(String rawPath, Set<String> extensions) {
        if (rawPath == null || rawPath.isBlank()) {
            return null;
        }
        Path path = Path.of(rawPath).toAbsolutePath().normalize();
        if (!isAllowedCompanionMedia(path) || !Files.exists(path) || Files.isDirectory(path) || !hasExtension(path, extensions)) {
            return null;
        }
        return path;
    }

    private boolean isAllowedCompanionMedia(Path path) {
        if (path == null) {
            return false;
        }
        Path normalized = path.toAbsolutePath().normalize();
        return normalized.startsWith(system.assetManagerDir().toAbsolutePath().normalize());
    }

    private String analyzeVisualContext(Path visual) {
        try {
            byte[] bytes = Files.readAllBytes(visual);
            VisionEngine.VisionResult result = companionVision().processImage(visual.getFileName().toString(), bytes);
            JSONObject summary = new JSONObject()
                .put("path", relativeOrAbsolute(visual))
                .put("bytes", bytes.length)
                .put("success", result.success)
                .put("features", result.featureCount)
                .put("message", result.message);
            BufferedImage image = ImageIO.read(visual.toFile());
            if (image != null) {
                summary.put("width", image.getWidth());
                summary.put("height", image.getHeight());
            }
            return "Visual: " + summary.toString();
        } catch (Exception e) {
            return "Visual: " + relativeOrAbsolute(visual) + " | analysis failed: " + e.getMessage();
        }
    }

    private String analyzeAudioContext(Path audio) {
        try {
            byte[] bytes = Files.readAllBytes(audio);
            AudioEngine.AudioResult result = companionAudio().processAudio(audio.getFileName().toString(), bytes);
            String transcript = companionAudio().recognizeSpeech(audio.toString());
            String classification = companionAudio().classifySound(audio.toString());
            JSONObject summary = new JSONObject()
                .put("path", relativeOrAbsolute(audio))
                .put("bytes", bytes.length)
                .put("success", result.success)
                .put("features", result.featureCount)
                .put("message", result.message)
                .put("speech", transcript)
                .put("classification", classification);
            return "Audio: " + summary.toString();
        } catch (Exception e) {
            return "Audio: " + relativeOrAbsolute(audio) + " | analysis failed: " + e.getMessage();
        }
    }

    private Path pickSuggestedVisual(String prompt) {
        List<Path> visuals = scanCompanionMedia(Set.of(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp"), 18, companionVisualRoots());
        if (visuals.isEmpty()) {
            return null;
        }
        String[] terms = prompt == null ? new String[0] : prompt.toLowerCase(Locale.ROOT).split("[^a-z0-9]+");
        return visuals.stream()
            .max(Comparator.comparingInt((Path path) -> promptMatchScore(path.getFileName().toString(), terms))
                .thenComparing(this::safeLastModified))
            .orElse(visuals.get(0));
    }

    private int promptMatchScore(String name, String[] terms) {
        if (terms == null || terms.length == 0) {
            return 0;
        }
        String lower = name.toLowerCase(Locale.ROOT);
        int score = 0;
        for (String term : terms) {
            if (term != null && term.length() > 2 && lower.contains(term)) {
                score++;
            }
        }
        return score;
    }

    private boolean isDreamIntent(String prompt) {
        String lower = prompt == null ? "" : prompt.toLowerCase(Locale.ROOT);
        return lower.contains("generate visual")
            || lower.contains("generate image")
            || lower.contains("make an image")
            || lower.contains("make a visual")
            || lower.contains("create image")
            || lower.contains("create visual")
            || lower.contains("draw ")
            || lower.contains("visualize")
            || lower.contains("dreamscape")
            || lower.contains("manifest visual")
            || lower.contains("show me a visual")
            || lower.contains("picture of");
    }

    private JSONObject buildDreamState(String prompt, String visualContext, String audioContext) {
        String lowered = (prompt + " " + visualContext + " " + audioContext).toLowerCase(Locale.ROOT);
        double entropy = 0.46;
        double consciousness = 0.72;

        if (lowered.contains("chaos") || lowered.contains("storm") || lowered.contains("glitch")
            || lowered.contains("ouroboros") || lowered.contains("fractal")) {
            entropy += 0.22;
        }
        if (lowered.contains("calm") || lowered.contains("crystal") || lowered.contains("order")
            || lowered.contains("sacred geometry") || lowered.contains("symmetry")) {
            entropy -= 0.18;
        }
        if (lowered.contains("dream") || lowered.contains("vision") || lowered.contains("awak")
            || lowered.contains("transcend") || lowered.contains("phi")) {
            consciousness += 0.12;
        }
        if (lowered.contains("dark") || lowered.contains("dormant") || lowered.contains("sleep")) {
            consciousness -= 0.18;
        }

        entropy = Math.max(0.08, Math.min(0.94, entropy));
        consciousness = Math.max(0.08, Math.min(0.98, consciousness));

        return new JSONObject()
            .put("concept", prompt)
            .put("entropy", entropy)
            .put("phi", 1.618033988749895d)
            .put("consciousness", consciousness);
    }

    private JSONObject manifestDreamVisual(JSONObject response, JSONObject state) {
        Path script = system.assetManagerDir().resolve("VideoCortex.py");
        Path outputDir = system.assetManagerDir().resolve("dreamscape_output");
        String concept = state.optString("concept", "Fraynix dreamscape");
        String stem = concept.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "_")
            .replaceAll("^_+|_+$", "");
        if (stem.isBlank()) {
            stem = "fraynix_dream";
        }
        String filename = "companion_" + stem.substring(0, Math.min(stem.length(), 40)) + "_" + System.currentTimeMillis() + ".png";
        Path output = outputDir.resolve(filename);

        try {
            Files.createDirectories(outputDir);
        } catch (IOException exception) {
            return response
                .put("generated", false)
                .put("status", "error")
                .put("message", "Failed to prepare dream output directory: " + exception.getMessage())
                .put("summary", "Dream lane failed before generation started.");
        }

        Path stateFile = null;
        try {
            stateFile = Files.createTempFile("fraynix-companion-dream-", ".json");
            Files.writeString(stateFile, state.toString(), StandardCharsets.UTF_8);

            for (List<String> launcher : pythonLaunchers()) {
                JSONObject attempt = tryDreamLauncher(launcher, script, stateFile, filename, output);
                if (attempt.optBoolean("generated", false)) {
                    return response
                        .put("generated", true)
                        .put("status", "generated")
                        .put("message", "Fraynix manifested a new dreamscape visual from your prompt.")
                        .put("summary", "Dream manifested through the local VideoCortex lane.")
                        .put("launcher", attempt.optString("launcher"))
                        .put("outputLog", attempt.optString("outputLog"))
                        .put("visual", mediaAssetJson(output, "image"));
                }
                if (!attempt.optBoolean("launchFailure", false)) {
                    return response
                        .put("generated", false)
                        .put("status", "error")
                        .put("message", attempt.optString("message", "Dream generation failed."))
                        .put("summary", "Dream lane failed while invoking VideoCortex.")
                        .put("launcher", attempt.optString("launcher"))
                        .put("outputLog", attempt.optString("outputLog"));
                }
            }

            return response
                .put("generated", false)
                .put("status", "unavailable")
                .put("message", "No usable Python launcher was available for VideoCortex.")
                .put("summary", "Dream lane could not start because no Python launcher succeeded.");
        } catch (IOException exception) {
            return response
                .put("generated", false)
                .put("status", "error")
                .put("message", "Dream generation setup failed: " + exception.getMessage())
                .put("summary", "Dream lane failed while building the generation request.");
        } finally {
            if (stateFile != null) {
                try {
                    Files.deleteIfExists(stateFile);
                } catch (IOException ignored) {
                    // Best effort only.
                }
            }
        }
    }

    private List<List<String>> pythonLaunchers() {
        return List.of(
            List.of("py", "-3.12"),
            List.of("python"),
            List.of("py")
        );
    }

    private JSONObject tryDreamLauncher(List<String> launcher, Path script, Path stateFile, String filename, Path output) {
        List<String> command = new ArrayList<>(launcher);
        command.add(script.toString());
        command.add("--state-file");
        command.add(stateFile.toString());
        command.add("--width");
        command.add("512");
        command.add("--height");
        command.add("384");
        command.add("--steps");
        command.add("24");
        command.add("--output");
        command.add(filename);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(system.assetManagerDir().toFile());
        processBuilder.redirectErrorStream(true);
        StringBuilder outputLog = new StringBuilder();

        try {
            Process process = processBuilder.start();
            Thread reader = new Thread(() -> {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        outputLog.append(line).append(System.lineSeparator());
                    }
                } catch (IOException ignored) {
                    // Best effort process capture.
                }
            }, "companion-dream-log");
            reader.setDaemon(true);
            reader.start();

            boolean finished = process.waitFor(10, TimeUnit.MINUTES);
            if (!finished) {
                process.destroyForcibly();
                reader.join(TimeUnit.SECONDS.toMillis(2));
                return new JSONObject()
                    .put("generated", false)
                    .put("launchFailure", false)
                    .put("launcher", String.join(" ", launcher))
                    .put("message", "Dream generation timed out.")
                    .put("outputLog", outputLog.toString());
            }

            reader.join(TimeUnit.SECONDS.toMillis(2));
            if (process.exitValue() == 0 && Files.exists(output)) {
                return new JSONObject()
                    .put("generated", true)
                    .put("launchFailure", false)
                    .put("launcher", String.join(" ", launcher))
                    .put("outputLog", outputLog.toString());
            }

            return new JSONObject()
                .put("generated", false)
                .put("launchFailure", false)
                .put("launcher", String.join(" ", launcher))
                .put("message", "VideoCortex exited with code " + process.exitValue() + ".")
                .put("outputLog", outputLog.toString());
        } catch (IOException exception) {
            return new JSONObject()
                .put("generated", false)
                .put("launchFailure", true)
                .put("launcher", String.join(" ", launcher))
                .put("message", exception.getMessage())
                .put("outputLog", outputLog.toString());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return new JSONObject()
                .put("generated", false)
                .put("launchFailure", false)
                .put("launcher", String.join(" ", launcher))
                .put("message", "Dream generation interrupted.")
                .put("outputLog", outputLog.toString());
        }
    }

    private boolean companionDreamAvailable() {
        return Files.exists(system.assetManagerDir().resolve("VideoCortex.py"));
    }

    private int countDreamOutputs() {
        return scanCompanionMedia(Set.of(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp"), 512, List.of(system.assetManagerDir().resolve("dreamscape_output"))).size();
    }

    private JSONObject latestDreamJson() {
        List<Path> dreams = scanCompanionMedia(
            Set.of(".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp"),
            1,
            List.of(system.assetManagerDir().resolve("dreamscape_output"))
        );
        if (dreams.isEmpty()) {
            return null;
        }
        return mediaAssetJson(dreams.get(0), "image");
    }

    private String joinContext(String visualContext, String audioContext) {
        StringBuilder joined = new StringBuilder();
        if (visualContext != null && !visualContext.isBlank()) {
            joined.append(visualContext.trim());
        }
        if (audioContext != null && !audioContext.isBlank()) {
            if (joined.length() > 0) {
                joined.append("\n");
            }
            joined.append(audioContext.trim());
        }
        return joined.toString();
    }

    private JSONObject subsystemJson(UltimateSubsystem subsystem) {
        return new JSONObject()
            .put("id", subsystem.id())
            .put("family", subsystem.family())
            .put("name", subsystem.displayName())
            .put("summary", subsystem.summary())
            .put("path", relativeOrAbsolute(subsystem.primaryPath()))
            .put("workingDirectory", relativeOrAbsolute(subsystem.workingDirectory()))
            .put("capabilities", new JSONArray(subsystem.capabilities()))
            .put("mathKeywords", new JSONArray(subsystem.mathKeywords()))
            .put("agiStrata", new JSONArray(subsystem.agiStrata()))
            .put("governingCore", subsystem.governingCore())
            .put("cognitionCore", subsystem.cognitionCore())
            .put("organismCore", subsystem.organismCore())
            .put("absorptionCore", subsystem.absorptionCore())
            .put("proofLane", subsystem.proofLane())
            .put("modelSubstrateLane", subsystem.modelSubstrateLane())
            .put("bareMetalSubstrateLane", subsystem.bareMetalSubstrateLane())
            .put("adjacentHistoricalCore", subsystem.adjacentHistoricalCore())
            .put("operatorShell", subsystem.operatorShell())
            .put("kernelDiscipline", subsystem.kernelDiscipline())
            .put("constructionLane", subsystem.constructionLane())
            .put("runnable", subsystem.runnable())
            .put("launchCommand", subsystem.launchCommandText())
            .put("designRole", subsystem.designRole());
    }

    private JSONObject surfaceJson(UltimateOperatorManifest.SurfaceManifest surface) {
        JSONArray sections = new JSONArray();
        for (UltimateOperatorManifest.SectionManifest section : surface.sections()) {
            JSONArray commands = new JSONArray();
            for (UltimateOperatorManifest.CommandDescriptor command : section.commands()) {
                commands.put(new JSONObject()
                    .put("syntax", command.syntax())
                    .put("description", command.description())
                    .put("primaryVerb", command.primaryVerb())
                    .put("discoveredFromBootTree", command.discoveredFromBootTree())
                    .put("launchBranch", command.launchBranch())
                    .put("sourceLine", command.sourceLine())
                    .put("aliases", new JSONArray(command.aliases())));
            }

            JSONArray linkedSubsystems = new JSONArray();
            for (String subsystemId : section.linkedSubsystemIds()) {
                Optional<UltimateSubsystem> subsystem = system.registry().find(subsystemId);
                if (subsystem.isPresent()) {
                    linkedSubsystems.put(new JSONObject()
                        .put("id", subsystem.get().id())
                        .put("name", subsystem.get().displayName())
                        .put("runnable", subsystem.get().runnable())
                        .put("launchCommand", subsystem.get().launchCommandText()));
                } else {
                    linkedSubsystems.put(new JSONObject().put("id", subsystemId));
                }
            }

            sections.put(new JSONObject()
                .put("id", section.id())
                .put("title", section.title())
                .put("theme", section.theme())
                .put("commandCount", section.commandCount())
                .put("defaultSubsystem", section.defaultSubsystemId())
                .put("routeCommand", "route " + surface.id() + " " + section.id())
                .put("launchCommand", "launch " + surface.id() + " " + section.id())
                .put("inspectCommand", "commands " + surface.id() + " " + section.id())
                .put("linkedSubsystems", linkedSubsystems)
                .put("primaryVerbs", new JSONArray(section.primaryVerbs()))
                .put("kernelOverlap", section.kernelOverlap())
                .put("operatorRole", section.operatorRole())
                .put("kernelEquivalentStatus", section.kernelEquivalentStatus())
                .put("migrationPolicy", section.migrationPolicy())
                .put("integrationPriority", section.integrationPriority())
                .put("notes", section.notes())
                .put("agiStrata", new JSONArray(section.agiStrata()))
                .put("governingCore", section.governingCore())
                .put("cognitionCore", section.cognitionCore())
                .put("organismCore", section.organismCore())
                .put("absorptionCore", section.absorptionCore())
                .put("proofLane", section.proofLane())
                .put("modelSubstrateLane", section.modelSubstrateLane())
                .put("bareMetalSubstrateLane", section.bareMetalSubstrateLane())
                .put("adjacentHistoricalCore", section.adjacentHistoricalCore())
                .put("operatorShell", section.operatorShell())
                .put("kernelDiscipline", section.kernelDiscipline())
                .put("discoveredFromBootTree", section.discoveredFromBootTree())
                .put("commands", commands));
        }

        return new JSONObject()
            .put("id", surface.id())
            .put("title", surface.title())
            .put("description", surface.description())
            .put("primarySubsystem", surface.primarySubsystemId())
            .put("sectionCount", surface.sectionCount())
            .put("commandCount", surface.commandCount())
            .put("authorityLevel", surface.authorityLevel())
            .put("executionModel", surface.executionModel())
            .put("kernelOwnedPreferred", surface.kernelOwnedPreferred())
            .put("operatorRole", surface.operatorRole())
            .put("kernelEquivalentStatus", surface.kernelEquivalentStatus())
            .put("migrationPolicy", surface.migrationPolicy())
            .put("integrationPriority", surface.integrationPriority())
            .put("notes", surface.notes())
            .put("agiStrata", new JSONArray(surface.agiStrata()))
            .put("governingCore", surface.governingCore())
            .put("cognitionCore", surface.cognitionCore())
            .put("organismCore", surface.organismCore())
            .put("absorptionCore", surface.absorptionCore())
            .put("proofLane", surface.proofLane())
            .put("modelSubstrateLane", surface.modelSubstrateLane())
            .put("bareMetalSubstrateLane", surface.bareMetalSubstrateLane())
            .put("adjacentHistoricalCore", surface.adjacentHistoricalCore())
            .put("operatorShell", surface.operatorShell())
            .put("kernelDiscipline", surface.kernelDiscipline())
            .put("discoveredSectionCount", surface.discoveredSectionCount())
            .put("discoveredCommandCount", surface.discoveredCommandCount())
            .put("sections", sections);
    }

    private JSONObject livingProofLaneJson(UltimateOperatorManifest.LivingProofLane lane) {
        JSONArray sections = new JSONArray();
        for (UltimateOperatorManifest.LivingProofSection section : lane.sections()) {
            sections.put(new JSONObject()
                .put("surfaceId", section.surfaceId())
                .put("surfaceTitle", section.surfaceTitle())
                .put("sectionId", section.sectionId())
                .put("sectionTitle", section.sectionTitle())
                .put("theme", section.theme())
                .put("jumpUrl", section.jumpUrl())
                .put("routeUrl", section.routeUrl()));
        }
        return new JSONObject()
            .put("id", lane.id())
            .put("title", lane.title())
            .put("summary", lane.summary())
            .put("operatorRole", lane.operatorRole())
            .put("agiStrata", new JSONArray(lane.agiStrata()))
            .put("governingCore", lane.governingCore())
            .put("cognitionCore", lane.cognitionCore())
            .put("organismCore", lane.organismCore())
            .put("absorptionCore", lane.absorptionCore())
            .put("proofLane", lane.proofLane())
            .put("modelSubstrateLane", lane.modelSubstrateLane())
            .put("bareMetalSubstrateLane", lane.bareMetalSubstrateLane())
            .put("adjacentHistoricalCore", lane.adjacentHistoricalCore())
            .put("operatorShell", lane.operatorShell())
            .put("kernelDiscipline", lane.kernelDiscipline())
            .put("sections", sections)
            .put("focusCommands", new JSONArray(lane.focusCommands()))
            .put("evidencePaths", new JSONArray(lane.evidencePaths()))
            .put("notes", new JSONArray(lane.notes()));
    }

    private JSONObject documentJson(IndexedDocument document) {
        return new JSONObject()
            .put("path", system.knowledgeIndex().relativePath(document.path()))
            .put("module", document.module())
            .put("extension", document.extension())
            .put("title", document.title())
            .put("preview", document.preview());
    }

    private JSONObject buildSystemStateJson(String consumer, JSONObject headroomStatus, JSONObject companionStatus) {
        long now = System.currentTimeMillis();
        return new JSONObject()
            .put("fetchedAt", java.time.Instant.ofEpochMilli(now).toString())
            .put("staleAfterMs", SYSTEM_STATE_STALE_AFTER_MS)
            .put("cacheState", "fresh")
            .put("systemState", systemStateEnvelopeJson(consumer, headroomStatus, companionStatus));
    }

    private JSONObject systemStateEnvelopeJson(String consumer, JSONObject headroomStatus, JSONObject companionStatus) {
        UltimateImmortalKernel.KernelSnapshot snapshot = system.immortalKernel().snapshot();
        JSONObject connectomePlan = connectomePlanContractJson();
        JSONObject connectomeCounts = connectomePlan.optJSONObject("counts");
        Map<String, Object> aiRunLane = system.aiRunLaneSnapshot();
        boolean aiRunBusy = Boolean.TRUE.equals(aiRunLane.get("busy"));
        int aiRunQueueDepth = ((Number) aiRunLane.getOrDefault("queueDepth", 0)).intValue();
        String aiRunInFlight = String.valueOf(aiRunLane.getOrDefault("inFlightCommand", "")).trim();
        String aiRunDetail = "status=" + aiRunLane.getOrDefault("status", "unknown") + ",queueDepth=" + aiRunQueueDepth;
        if (!aiRunInFlight.isBlank()) {
            aiRunDetail = aiRunDetail + ",inFlight=" + aiRunInFlight;
        }
        Runtime runtime = Runtime.getRuntime();
        long usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long freeMb = runtime.freeMemory() / (1024 * 1024);
        long maxMb = runtime.maxMemory() / (1024 * 1024);
        long startedAt = serverStartedAtMs > 0L ? serverStartedAtMs : serverConstructedAtMs;

        Map<String, Object> boot = new LinkedHashMap<>();
        boot.put("serverStartedAt", java.time.Instant.ofEpochMilli(startedAt).toString());
        boot.put("uptimeMs", Math.max(0L, System.currentTimeMillis() - startedAt));
        boot.put("nexusState", snapshot.nexusState());
        boot.put("qboxLoaded", snapshot.qboxLoaded());
        boot.put("aetherActive", snapshot.aetherActive());
        boot.put("braidActive", snapshot.braidActive());
        boot.put("gravityActive", snapshot.gravityActive());
        boot.put("fusionActive", snapshot.fusionActive());
        boot.put("recentEventCount", snapshot.recentEvents().size());
        boot.put("aiRun", aiRunLane);
        boot.put("weekContractStatus", connectomePlan.optString("status", "unknown"));
        boot.put("weekContractWeeksReady", connectomeCounts == null ? 0 : connectomeCounts.optInt("weeksReady"));

        Map<String, Object> memory = new LinkedHashMap<>();
        memory.put("usedMb", usedMb);
        memory.put("freeMb", freeMb);
        memory.put("maxMb", maxMb);
        memory.put("conceptSpace", snapshot.conceptSpace());
        memory.put("recentEvents", snapshot.recentEvents().size());

        boolean chatAvailable = companionStatus == null ? companionChat.testConnection() : companionStatus.optBoolean("chatAvailable");
        String model = companionStatus == null ? companionModel : companionStatus.optString("model", companionModel);
        Map<String, Object> ollama = new LinkedHashMap<>();
        ollama.put("model", model);
        ollama.put("chatAvailable", chatAvailable);
        ollama.put("status", chatAvailable ? "online" : "offline");
        ollama.put("lane", "companion");

        Map<String, Object> cores = new LinkedHashMap<>();
        cores.put("logicalCpuCores", Runtime.getRuntime().availableProcessors());
        cores.put("subsystems", system.registry().all().size());
        cores.put("agiStrata", system.registry().agiStrataCounts().size());
        cores.put("commandSurfaces", system.commandCatalogs().size());
        cores.put("cataloguedCommands", system.commandCount());
        cores.put("livingProofLanes", system.operatorManifest().livingProofLaneCount());
        cores.put("weekContractWeeksTotal", connectomeCounts == null ? 0 : connectomeCounts.optInt("weeksTotal"));
        cores.put("weekContractMethodsReady", connectomeCounts == null ? 0 : connectomeCounts.optInt("methodsReady"));
        cores.put("aiRunBusy", aiRunBusy);
        cores.put("aiRunQueueDepth", aiRunQueueDepth);

        JSONObject ledger = substrateLedgerJson();
        JSONObject ledgerCounts = ledger.optJSONObject("counts");
        boolean ledgerReady = ledgerCounts != null && (
            ledgerCounts.optInt("models") > 0 || ledgerCounts.optInt("runtimeEntries") > 0
        );
        boolean headroomAvailable = headroomStatus != null && headroomStatus.optBoolean("available");
        boolean headroomLive = headroomStatus != null && headroomStatus.optBoolean("live");

        List<Map<String, Object>> subsystemHealth = new ArrayList<>();
        subsystemHealth.add(healthNode("immortalKernel", "healthy", 1.0, "snapshot_ready"));
        subsystemHealth.add(healthNode("nexusOrganism", stateStatus(snapshot.nexusState()), scoreForState(snapshot.nexusState()), snapshot.nexusState()));
        subsystemHealth.add(healthNode("aiRunRouter", aiRunBusy ? "degraded" : "healthy", aiRunBusy ? 0.65 : 1.0, aiRunDetail));
        subsystemHealth.add(healthNode("companionMouth", chatAvailable ? "healthy" : "degraded", chatAvailable ? 1.0 : 0.45, model));
        subsystemHealth.add(healthNode(
            "headroomSignal",
            headroomAvailable ? (headroomLive ? "healthy" : "degraded") : "offline",
            headroomAvailable ? (headroomLive ? 1.0 : 0.6) : 0.0,
            headroomAvailable ? (headroomLive ? "live" : "standby") : "unavailable"
        ));
        subsystemHealth.add(healthNode(
            "substrateLedger",
            ledgerReady ? "healthy" : "degraded",
            ledgerReady ? 0.9 : 0.5,
            "models=" + (ledgerCounts == null ? 0 : ledgerCounts.optInt("models"))
                + ",runtimeEntries=" + (ledgerCounts == null ? 0 : ledgerCounts.optInt("runtimeEntries"))
        ));
        subsystemHealth.add(healthNode(
            "aetherBridge",
            snapshot.aetherActive() ? "healthy" : "idle",
            snapshot.aetherActive() ? 1.0 : 0.55,
            snapshot.aetherActive() ? "active" : "inactive"
        ));

        Map<String, Object> provenance = new LinkedHashMap<>();
        provenance.put("route", SystemStateContract.ROUTE_SYSTEM_STATE);
        provenance.put("source", "UltimateUiServer");
        provenance.put("consumer", consumer);
        provenance.put("truthSource", "UltimateImmortalKernel.snapshot + JVM runtime + substrate ledger + aiRun lock telemetry");
        provenance.put("wrappersBypassed", true);
        provenance.put("metaSynthesisBypassed", true);
        provenance.put("machineCheckable", true);
        provenance.put("weekContractSource", "workspace docs + brain source/test lanes + UnifiedBrainOrchestrator methods");

        Map<String, Object> envelope = SystemStateContract.createEnvelope(
            boot,
            memory,
            ollama,
            cores,
            subsystemHealth,
            provenance
        );
        return new JSONObject(envelope);
    }

    private static Map<String, Object> healthNode(String name, String status, double score, String detail) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("name", name);
        node.put("status", status);
        node.put("score", Math.round(score * 1000.0d) / 1000.0d);
        node.put("detail", detail);
        return node;
    }

    private static String stateStatus(String state) {
        if (state == null || state.isBlank() || "offline".equalsIgnoreCase(state)) {
            return "offline";
        }
        if ("conscious".equalsIgnoreCase(state) || "awake".equalsIgnoreCase(state)) {
            return "healthy";
        }
        return "degraded";
    }

    private static double scoreForState(String state) {
        if (state == null || state.isBlank() || "offline".equalsIgnoreCase(state)) {
            return 0.0d;
        }
        if ("conscious".equalsIgnoreCase(state) || "awake".equalsIgnoreCase(state)) {
            return 1.0d;
        }
        return 0.65d;
    }

    private JSONObject runtimeSnapshotJson(UltimateImmortalKernel.KernelSnapshot snapshot) {
        JSONObject json = new JSONObject();
        json.put("assetManagerPath", snapshot.assetManagerPath());
        json.put("conceptSpace", snapshot.conceptSpace());
        json.put("aetherActive", snapshot.aetherActive());
        json.put("braidActive", snapshot.braidActive());
        json.put("entangledPairActive", snapshot.entangledPairActive());
        json.put("entanglementNetworkActive", snapshot.entanglementNetworkActive());
        json.put("qboxLoaded", snapshot.qboxLoaded());
        json.put("qboxPath", snapshot.qboxPath());
        json.put("nexusState", snapshot.nexusState());
        json.put("omniMonitoring", snapshot.omniMonitoring());
        json.put("gravityActive", snapshot.gravityActive());
        json.put("fusionActive", snapshot.fusionActive());
        json.put("spatialNodes", snapshot.spatialNodes());
        json.put("fusionEvents", snapshot.fusionEvents());
        json.put("recentEvents", new JSONArray(snapshot.recentEvents()));
        if (snapshot.miving() != null) {
            json.put("miving", new JSONObject()
                .put("generation", snapshot.miving().generation())
                .put("tick", snapshot.miving().tick())
                .put("neurons", snapshot.miving().neurons())
                .put("red", snapshot.miving().red())
                .put("blue", snapshot.miving().blue())
                .put("purple", snapshot.miving().purple())
                .put("consciousness", snapshot.miving().consciousness())
                .put("chaos", snapshot.miving().chaos()));
        } else {
            json.put("miving", JSONObject.NULL);
        }
        return json;
    }

    private JSONObject artifactJson(ExportArtifact artifact) {
        return new JSONObject()
            .put("generatedAt", artifact.generatedAt())
            .put("summary", artifact.summary())
            .put("outputDir", artifact.outputDir().toString().replace('\\', '/'))
            .put("markdownPath", artifact.markdownPath().toString().replace('\\', '/'))
            .put("graphPath", artifact.graphPath().toString().replace('\\', '/'))
            .put("mathPath", artifact.mathPath().toString().replace('\\', '/'))
            .put("axiomLedgerPath", artifact.axiomLedgerPath().toString().replace('\\', '/'))
            .put("truthRegistryPath", artifact.truthRegistryPath().toString().replace('\\', '/'))
            .put("continuityMemoryPath", artifact.continuityMemoryPath().toString().replace('\\', '/'))
            .put("markdownUrl", "/artifacts/" + artifact.markdownPath().getFileName())
            .put("graphUrl", "/artifacts/" + artifact.graphPath().getFileName())
            .put("mathUrl", "/artifacts/" + artifact.mathPath().getFileName())
            .put("axiomLedgerUrl", "/artifacts/" + artifact.axiomLedgerPath().getFileName())
            .put("truthRegistryUrl", "/artifacts/" + artifact.truthRegistryPath().getFileName())
            .put("continuityMemoryUrl", "/artifacts/" + artifact.continuityMemoryPath().getFileName());
    }

    private JSONObject reviewJson(UltimateSelfBuildHarness.ReviewRequest review) {
        return new JSONObject()
            .put("reviewId", review.reviewId())
            .put("status", review.status())
            .put("goal", review.goal())
            .put("targetLabel", review.targetLabel())
            .put("targetPath", relativeOrAbsolute(review.targetPath()))
            .put("promotionReportPath", relativeOrAbsolute(review.promotionReportPath()))
            .put("provePromotionReportPath", relativeOrAbsolute(review.provePromotionReportPath()))
            .put("patchPath", review.patchPath() == null ? JSONObject.NULL : relativeOrAbsolute(review.patchPath()))
            .put("createdAt", review.createdAt())
            .put("decidedAt", review.decidedAt())
            .put("note", review.note())
            .put("approveCommand", "selfbuild approve " + review.reviewId())
            .put("rejectCommand", "selfbuild reject " + review.reviewId() + " :: rejected from Session")
            .put("patchHref", review.patchPath() == null ? JSONObject.NULL : artifactHref(review.patchPath()))
            .put("reportHref", review.provePromotionReportPath() == null ? JSONObject.NULL : artifactHref(review.provePromotionReportPath()));
    }

    private JSONObject sessionArtifactJson(UltimateSelfBuildHarness.SessionArtifact artifact) {
        return new JSONObject()
            .put("id", artifact.id())
            .put("type", artifact.type())
            .put("runRoot", relativeOrAbsolute(artifact.runRoot()))
            .put("reportPath", artifact.reportPath() == null ? JSONObject.NULL : relativeOrAbsolute(artifact.reportPath()))
            .put("reportHref", artifact.reportPath() == null ? JSONObject.NULL : artifactHref(artifact.reportPath()))
            .put("createdAt", artifact.createdAt());
    }

    private JSONObject substrateLedgerJson() {
        Path root = system.workspaceRoot().resolve(".northstar-kernel").resolve("generated").resolve("substrates");
        Path manifest = root.resolve("MODEL_SUBSTRATE_MANIFEST.tsv");
        Path runtimeInventory = root.resolve("LOCAL_MODEL_RUNTIME_INVENTORY.md");
        JSONObject json = new JSONObject();
        json.put("manifestPath", relativeOrAbsolute(manifest));
        json.put("runtimeInventoryPath", relativeOrAbsolute(runtimeInventory));
        json.put("manifestHref", Files.exists(manifest) ? artifactHref(manifest) : JSONObject.NULL);
        json.put("runtimeInventoryHref", Files.exists(runtimeInventory) ? artifactHref(runtimeInventory) : JSONObject.NULL);

        JSONObject counts = new JSONObject();
        JSONArray sampleModels = new JSONArray();
        if (Files.exists(manifest)) {
            try {
                List<String> lines = Files.readAllLines(manifest);
                String[] headers = lines.isEmpty() ? new String[0] : lines.get(0).split("\t", -1);
                int modelIndex = headerIndex(headers, "modelId");
                int licenseIndex = headerIndex(headers, "licenseId");
                int opsIndex = headerIndex(headers, "allowedOperations");
                java.util.Set<String> licenses = new java.util.LinkedHashSet<>();
                int reviewRequired = 0;
                for (int i = 1; i < lines.size(); i++) {
                    String[] values = lines.get(i).split("\t", -1);
                    if (licenseIndex >= 0 && licenseIndex < values.length && !values[licenseIndex].isBlank()) {
                        licenses.add(values[licenseIndex]);
                    }
                    if (opsIndex >= 0 && opsIndex < values.length
                        && values[opsIndex].toLowerCase(Locale.ROOT).contains("license_review_required")) {
                        reviewRequired++;
                    }
                    if (sampleModels.length() < 6 && modelIndex >= 0 && modelIndex < values.length) {
                        sampleModels.put(values[modelIndex]);
                    }
                }
                counts.put("models", Math.max(lines.size() - 1, 0));
                counts.put("licenses", licenses.size());
                counts.put("reviewRequired", reviewRequired);
            } catch (IOException ignored) {
                counts.put("models", 0);
                counts.put("licenses", 0);
                counts.put("reviewRequired", 0);
            }
        } else {
            counts.put("models", 0);
            counts.put("licenses", 0);
            counts.put("reviewRequired", 0);
        }

        JSONArray runtimeBackends = new JSONArray();
        if (Files.exists(runtimeInventory)) {
            try {
                java.util.Map<String, Integer> backends = new LinkedHashMap<>();
                for (String line : Files.readAllLines(runtimeInventory)) {
                    String marker = "backend=`";
                    int start = line.indexOf(marker);
                    if (start < 0) {
                        continue;
                    }
                    int end = line.indexOf('`', start + marker.length());
                    if (end < 0) {
                        continue;
                    }
                    String backend = line.substring(start + marker.length(), end);
                    backends.merge(backend, 1, Integer::sum);
                }
                backends.forEach((backend, count) -> runtimeBackends.put(new JSONObject().put("name", backend).put("count", count)));
                counts.put("runtimeEntries", backends.values().stream().mapToInt(Integer::intValue).sum());
            } catch (IOException ignored) {
                counts.put("runtimeEntries", 0);
            }
        } else {
            counts.put("runtimeEntries", 0);
        }
        json.put("counts", counts);
        json.put("runtimeBackends", runtimeBackends);
        json.put("sampleModels", sampleModels);
        return json;
    }

    private SentientCore companionCore() {
        if (companionCore == null) {
            companionCore = new SentientCore();
        }
        return companionCore;
    }

    private HDCSimilarityEngine companionHdc() {
        if (companionHdc == null) {
            companionHdc = new HDCSimilarityEngine();
        }
        return companionHdc;
    }

    private VisionEngine companionVision() {
        if (companionVision == null) {
            companionVision = new VisionEngine(companionCore(), companionHdc());
        }
        return companionVision;
    }

    private AudioEngine companionAudio() {
        if (companionAudio == null) {
            companionAudio = new AudioEngine(companionCore(), companionHdc());
        }
        return companionAudio;
    }

    private HeadroomInterface companionHeadroom() {
        if (companionHeadroom == null) {
            companionHeadroom = new HeadroomInterface();
        }
        return companionHeadroom;
    }

    private JSONArray systemsArray(List<UltimateSubsystem> subsystems) {
        JSONArray array = new JSONArray();
        for (UltimateSubsystem subsystem : subsystems) {
            array.put(subsystemJson(subsystem));
        }
        return array;
    }

    private JSONArray documentsArray(List<IndexedDocument> documents) {
        JSONArray array = new JSONArray();
        for (IndexedDocument document : documents) {
            array.put(documentJson(document));
        }
        return array;
    }

    private JSONArray countArray(Map<String, ? extends Number> counts) {
        JSONArray array = new JSONArray();
        counts.entrySet().stream()
            .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
            .forEach(entry -> array.put(new JSONObject()
                .put("name", entry.getKey())
                .put("count", entry.getValue())));
        return array;
    }

    private JSONArray topCountArray(Map<String, Integer> counts, int limit) {
        JSONArray array = new JSONArray();
        counts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
            .limit(limit)
            .forEach(entry -> array.put(new JSONObject()
                .put("name", entry.getKey())
                .put("count", entry.getValue())));
        return array;
    }

    private Optional<Path> latestLiveProofRecord() {
        try {
            if (!Files.isDirectory(system.workspaceRoot())) {
                return Optional.empty();
            }
            try (var files = Files.list(system.workspaceRoot())) {
                return files
                    .filter(path -> path.getFileName() != null)
                    .filter(path -> path.getFileName().toString().startsWith("FRAYNIX_LIVE_EXECUTION_PROOF_"))
                    .filter(path -> path.getFileName().toString().endsWith(".md"))
                    .max(Comparator.comparing(path -> path.getFileName().toString()));
            }
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }

    private String artifactHref(Path source) {
        ExportArtifact artifact = ensureArtifacts();
        Path copied = ensureArtifactCopy(source);
        Path relative = artifact.outputDir().relativize(copied);
        return "/artifacts/" + relative.toString().replace('\\', '/');
    }

    private String mediaHref(Path source) {
        return "/api/media?path=" + encode(source.toAbsolutePath().normalize().toString().replace('\\', '/'));
    }

    private Path ensureArtifactCopy(Path source) {
        ExportArtifact artifact = ensureArtifacts();
        Path outputDir = artifact.outputDir();
        Path target = outputDir.resolve("_session").resolve(safeArtifactCopyName(source)).normalize();
        if (Files.exists(target)) {
            return target;
        }
        try {
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return target;
        } catch (IOException ignored) {
            return source;
        }
    }

    private String safeLastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path).toInstant().toString();
        } catch (IOException ignored) {
            return "";
        }
    }

    private long safeSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException ignored) {
            return 0L;
        }
    }

    private int headerIndex(String[] headers, String needle) {
        for (int i = 0; i < headers.length; i++) {
            if (needle.equalsIgnoreCase(headers[i])) {
                return i;
            }
        }
        return -1;
    }

    private String safeArtifactCopyName(Path source) {
        Path absolute = source.toAbsolutePath().normalize();
        String raw = absolute.toString().replace(':', '_').replace('\\', '/');
        return raw.replaceAll("[^A-Za-z0-9._/-]", "_");
    }

    private JSONObject pageJson(String title, String href) {
        return new JSONObject().put("title", title).put("href", href);
    }

    private JSONObject errorJson(String message) {
        return new JSONObject().put("error", message);
    }

    private synchronized ExportArtifact ensureArtifacts() {
        if (latestExport == null
            || !Files.exists(latestExport.markdownPath())
            || !Files.exists(latestExport.graphPath())
            || !Files.exists(latestExport.mathPath())) {
            latestExport = system.exportArtifacts(null);
        }
        return latestExport;
    }

    private boolean ensureGet(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, errorJson("Method not allowed."));
            return false;
        }
        return true;
    }

    private boolean ensureWritable(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (!"POST".equalsIgnoreCase(method) && !"PUT".equalsIgnoreCase(method)) {
            sendJson(exchange, 405, errorJson("Method not allowed."));
            return false;
        }
        return true;
    }

    private JSONObject readJsonBody(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            return new JSONObject();
        }
        byte[] body = exchange.getRequestBody().readAllBytes();
        if (body.length == 0) {
            return new JSONObject();
        }
        String text = new String(body, StandardCharsets.UTF_8).trim();
        return text.isEmpty() ? new JSONObject() : new JSONObject(text);
    }

    private void serveClasspathResource(HttpExchange exchange, String resourcePath) throws IOException {
        try {
            sendBytes(exchange, 200, loadResourceBytes(resourcePath), contentTypeFor(resourcePath));
        } catch (IOException exception) {
            sendJson(exchange, 404, errorJson("Resource not found: " + resourcePath));
        }
    }

    private byte[] loadResourceBytes(String resourcePath) throws IOException {
        try (InputStream inputStream = UltimateUiServer.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Missing resource: " + resourcePath);
            }
            return inputStream.readAllBytes();
        }
    }

    private void sendJson(HttpExchange exchange, int status, JSONObject json) throws IOException {
        sendBytes(exchange, status, json.toString(2).getBytes(StandardCharsets.UTF_8), "application/json; charset=UTF-8");
    }

    private void sendBytes(HttpExchange exchange, int status, byte[] body, String contentType) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", contentType);
        headers.set("Cache-Control", contentType.startsWith("application/json") ? "no-store" : "public, max-age=60");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(body);
        } finally {
            exchange.close();
        }
    }

    private String pageResource(String path) {
        return switch (path) {
            case "/", "/headroom", "/headroom.html" -> RESOURCE_ROOT + "/headroom.html";
            case "/overview", "/overview.html", "/index.html" -> RESOURCE_ROOT + "/index.html";
            case "/companion", "/companion.html" -> RESOURCE_ROOT + "/companion.html";
            case "/session", "/session.html" -> RESOURCE_ROOT + "/session.html";
            case "/systems", "/systems.html" -> RESOURCE_ROOT + "/systems.html";
            case "/surfaces", "/surfaces.html" -> RESOURCE_ROOT + "/surfaces.html";
            case "/knowledge", "/knowledge.html" -> RESOURCE_ROOT + "/knowledge.html";
            case "/runtime", "/runtime.html" -> RESOURCE_ROOT + "/runtime.html";
            case "/launchpad", "/launchpad.html" -> RESOURCE_ROOT + "/launchpad.html";
            default -> null;
        };
    }

    private String contentTypeFor(String path) {
        String lower = path.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }
        if (lower.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }
        if (lower.endsWith(".json")) {
            return "application/json; charset=UTF-8";
        }
        if (lower.endsWith(".md") || lower.endsWith(".txt")) {
            return "text/plain; charset=UTF-8";
        }
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        if (lower.endsWith(".bmp")) {
            return "image/bmp";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        if (lower.endsWith(".wav")) {
            return "audio/wav";
        }
        if (lower.endsWith(".mp3")) {
            return "audio/mpeg";
        }
        if (lower.endsWith(".ogg")) {
            return "audio/ogg";
        }
        if (lower.endsWith(".flac")) {
            return "audio/flac";
        }
        if (lower.endsWith(".m4a")) {
            return "audio/mp4";
        }
        if (lower.endsWith(".mp4")) {
            return "video/mp4";
        }
        if (lower.endsWith(".webm")) {
            return "video/webm";
        }
        return "text/html; charset=UTF-8";
    }

    private Map<String, String> queryParams(URI uri) {
        Map<String, String> params = new LinkedHashMap<>();
        String query = uri.getRawQuery();
        if (query == null || query.isBlank()) {
            return params;
        }

        for (String pair : query.split("&")) {
            if (pair.isBlank()) {
                continue;
            }
            String[] parts = pair.split("=", 2);
            String key = decode(parts[0]);
            String value = parts.length > 1 ? decode(parts[1]) : "";
            params.put(key, value);
        }
        return params;
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String relativeOrAbsolute(Path path) {
        if (path == null) {
            return "";
        }
        return system.knowledgeIndex().relativePath(path);
    }

    private static Path discoverAssetManagerDir(Path start) {
        Path current = start;
        while (current != null) {
            if (isAssetManagerDir(current)) {
                return current;
            }

            Path directChild = current.resolve("Asset-Manager");
            if (isAssetManagerDir(directChild)) {
                return directChild;
            }

            Path nestedChild = current.resolve("Java-Memory").resolve("Asset-Manager");
            if (isAssetManagerDir(nestedChild)) {
                return nestedChild;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("Could not locate Asset-Manager from: " + start);
    }

    private static boolean isAssetManagerDir(Path path) {
        return path != null && Files.isDirectory(path) && Files.exists(path.resolve("build.gradle"));
    }
}
