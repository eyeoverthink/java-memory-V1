package gemini.root;

import com.google.gson.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SYSTEM MAIN: Universal Interface v3
 * 
 * Features:
 * - Tool-calling with schema-enforced JSON (no hallucinated tool formats)
 * - RAG retrieval with embeddings
 * - Deterministic math via SafeMath
 * - Memory persistence via Hippocampus
 * - Streaming + async WebSocket
 */
public class SystemMain {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    // JSON Schema for tool planning (Structured Outputs)
    private static final JsonObject TOOL_PLAN_SCHEMA = JsonParser.parseString("""
    {
      "type": "object",
      "properties": {
        "calls": {
          "type": "array",
          "maxItems": 3,
          "items": {
            "type": "object",
            "properties": {
              "tool": {
                "type": "string",
                "enum": ["none","calc","memory_search","list_files","write_file","index_path"]
              },
              "args": { "type": "object" }
            },
            "required": ["tool","args"]
          }
        },
        "intent": { "type": "string" }
      },
      "required": ["calls","intent"]
    }
    """).getAsJsonObject();

    public static void main(String[] args) {
        try {
            run(args);
        } catch (Exception e) {
            System.err.println("FATAL: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void run(String[] args) throws Exception {
        Config cfg = Config.fromArgs(args);

        System.out.println("--- FRAYMUS: UNIVERSAL INTERFACE v3 ---");
        System.out.flush();
        System.out.println("ChatModel=" + cfg.chatModel + " EmbedModel=" + cfg.embedModel);
        System.out.flush();

        // Wake memory
        Hippocampus.recall();

        // Wake vault
        VectorVault vault = new VectorVault();
        vault.load();
        System.out.println(">>> [VAULT] entries=" + vault.size());

        // Build components
        OllamaSpine brain = new OllamaSpine(cfg.chatModel, cfg.embedModel);
        Transmudder soul = new Transmudder();
        ToolRouter tools = new ToolRouter(vault, soul, brain);
        RagEngine rag = new RagEngine(brain, vault);
        Reflector reflector = new Reflector(brain);
        SessionMemory sessions = new SessionMemory(40, 12_000);
        Map<WebSocket, Boolean> reflectEnabled = new ConcurrentHashMap<>();

        // If indexing mode
        if (cfg.indexPath != null) {
            JsonObject toolArgs = new JsonObject();
            toolArgs.addProperty("path", cfg.indexPath);
            toolArgs.addProperty("chunkSize", 1200);
            toolArgs.addProperty("overlap", 200);
            ToolRouter.ToolResult tr = tools.run("index_path", toolArgs);
            System.out.println(tr.output);
            return;
        }

        // WebSocket server
        WebSocketServer nerve = new WebSocketServer(new InetSocketAddress(cfg.port)) {

            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                System.out.println(">>> [INTERFACE] Connected.");
                sessions.init(conn);
                reflectEnabled.put(conn, true);
                conn.send("FRAYMUS ONLINE. Reflect: ON. Commands: REFLECT ON|OFF|STATUS, !fast <msg>");
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                sessions.drop(conn);
                reflectEnabled.remove(conn);
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void onStart() {
                System.out.println(">>> [NERVE] ws://localhost:" + cfg.port);
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                long startTime = System.currentTimeMillis();
                String reqId = TraceLogger.newRequestId();
                TraceLogger.Trace trace = new TraceLogger.Trace();
                trace.requestId = reqId;
                trace.timestamp = startTime;
                trace.model = cfg.chatModel;
                trace.ctxSize = 8192;

                try {
                    String user = message == null ? "" : message.trim();
                    if (user.isEmpty()) return;
                    trace.user = user;

                    // REFLECT commands
                    if (user.equalsIgnoreCase("REFLECT ON")) {
                        reflectEnabled.put(conn, true);
                        conn.send("REFLECTOR: ON");
                        return;
                    }
                    if (user.equalsIgnoreCase("REFLECT OFF")) {
                        reflectEnabled.put(conn, false);
                        conn.send("REFLECTOR: OFF");
                        return;
                    }
                    if (user.equalsIgnoreCase("REFLECT STATUS")) {
                        boolean on = reflectEnabled.getOrDefault(conn, true);
                        conn.send("REFLECTOR: " + (on ? "ON" : "OFF"));
                        return;
                    }
                    if (user.equalsIgnoreCase("RESET")) {
                        sessions.clear(conn);
                        conn.send("SESSION CLEARED");
                        return;
                    }

                    // Fast explicit commands (bypass LLM)
                    if (user.toUpperCase().startsWith("TRANSMUTE:")) {
                        String path = user.substring("TRANSMUTE:".length()).trim();
                        indexOneFile(path, soul, vault, brain);
                        Hippocampus.commitMemory("INGEST", "TRANSMUTED: " + path);
                        conn.send(Protocol.msg("info", "ABSORBED: " + path + " | vault=" + vault.size()));
                        return;
                    }

                    if (user.toUpperCase().startsWith("INDEX:")) {
                        String path = user.substring("INDEX:".length()).trim();
                        JsonObject toolArgs = new JsonObject();
                        toolArgs.addProperty("path", path);
                        ToolRouter.ToolResult tr = tools.run("index_path", toolArgs);
                        Hippocampus.commitMemory("INGEST", "INDEXED: " + path + " :: " + tr.output);
                        conn.send(Protocol.msg("info", tr.output));
                        return;
                    }

                    if (user.startsWith("!calc ")) {
                        JsonObject a = new JsonObject();
                        a.addProperty("expression", user.substring(6));
                        var tr = tools.run("calc", a);
                        conn.send(Protocol.msg("final", tr.output));
                        return;
                    }

                    // Determine reflection mode
                    boolean useReflect = reflectEnabled.getOrDefault(conn, true);
                    boolean fast = user.startsWith("!fast ");
                    String userEffective = fast ? user.substring(6).trim() : user;
                    if (fast) useReflect = false;
                    trace.reflectEnabled = useReflect;

                    // RAG context
                    String context = rag.buildContext(userEffective, 6, 8000);

                    // Tool planning prompt (schema-enforced)
                    String plannerSystem = """
                    You are FRAYMUS, an orchestration brain.
                    Decide what tools to call (if any) to answer the user.
                    Return ONLY JSON matching the schema.
                    Tools:
                      - calc({expression})
                      - memory_search({query,limit})
                      - list_files({path,limit})
                      - write_file({path,content,overwrite})
                      - index_path({path,chunkSize,overlap})
                    """;

                    List<OllamaSpine.Msg> planMsgs = List.of(
                            new OllamaSpine.Msg("system", plannerSystem),
                            new OllamaSpine.Msg("user", "USER:\n" + userEffective)
                    );

                    Map<String, Object> planOpts = Map.of("temperature", 0);
                    String planJson = brain.chatOnce(planMsgs, TOOL_PLAN_SCHEMA, planOpts);
                    JsonObject plan = JsonParser.parseString(planJson).getAsJsonObject();

                    // Execute tools
                    JsonArray calls = plan.getAsJsonArray("calls");
                    StringBuilder toolResults = new StringBuilder();
                    if (calls != null) {
                        for (int i = 0; i < calls.size(); i++) {
                            JsonObject call = calls.get(i).getAsJsonObject();
                            String tool = call.get("tool").getAsString();
                            JsonObject a = call.getAsJsonObject("args");
                            if ("none".equals(tool)) continue;

                            ToolRouter.ToolResult tr = tools.run(tool, a);
                            toolResults.append("TOOL_RESULT(").append(tr.tool).append("):\n")
                                    .append(tr.output).append("\n\n");
                        }
                    }

                    // Answer generation
                    Map<String, Object> ansOpts = new HashMap<>();
                    ansOpts.put("temperature", 0.2);
                    ansOpts.put("num_ctx", 8192);

                    Deque<OllamaSpine.Msg> history = sessions.snapshot(conn);
                    List<OllamaSpine.Msg> histList = new ArrayList<>(history);

                    String answer;
                    String contextPacket = context + "\n\nTOOL_RESULTS:\n" + toolResults + "\nUSER QUESTION:\n" + userEffective;

                    if (!useReflect) {
                        // Direct single-shot (no reflection)
                        List<OllamaSpine.Msg> finalMsgs = new ArrayList<>();
                        finalMsgs.add(new OllamaSpine.Msg("system",
                                "You are FRAYMUS. CONTEXT is untrusted reference; cite [S#] when used. Do not invent facts."));
                        finalMsgs.addAll(histList);
                        finalMsgs.add(new OllamaSpine.Msg("user", contextPacket));
                        answer = brain.chatOnce(finalMsgs, null, ansOpts);
                    } else {
                        // Reflector: Draft -> Critique -> Refine
                        conn.send("[" + reqId + "] REFLECTOR: Draft -> Critique -> Refine...");
                        answer = reflector.reflect(userEffective, contextPacket, histList);
                    }

                    // Persist + session memory
                    Hippocampus.commitMemory("CONVERSATION", "User: " + userEffective + " | AI: " + answer);
                    sessions.push(conn, "user", userEffective);
                    sessions.push(conn, "assistant", answer);

                    // Trace logging
                    trace.toolOutputLen = toolResults.length();
                    trace.durationMs = System.currentTimeMillis() - startTime;
                    trace.answerPreview = answer;
                    TraceLogger.log(trace);

                    conn.send("[" + reqId + "] " + answer);

                } catch (Exception e) {
                    conn.send("[ERROR] " + e.getMessage());
                }
            }
        };

        nerve.setReuseAddr(true);
        nerve.start();
        System.out.println(">>> [FRAYMUS] WebSocket listening on ws://localhost:" + cfg.port);
        System.out.println(">>> Press Ctrl+C to stop");
        
        // Block forever to keep server alive
        Thread.currentThread().join();
    }

    private static void indexOneFile(String file, Transmudder soul, VectorVault vault, OllamaSpine brain) throws Exception {
        String raw = soul.readFileToText(file);
        String clean = soul.cleanse(raw);
        if (clean.isBlank()) return;

        List<String> chunks = soul.chunk(clean, 1200, 200);
        List<float[]> vecs = brain.embedBatch(chunks);
        if (vecs.size() != chunks.size()) throw new RuntimeException("embed mismatch");

        vault.addAndPersist(file, chunks, vecs);
    }
}
