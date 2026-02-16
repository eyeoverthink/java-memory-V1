package gemini.root;

import com.google.gson.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SystemMainFinal {

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private static final JsonObject TOOL_PLAN_SCHEMA = JsonParser.parseString("""
    {
      "type": "object",
      "properties": {
        "calls": {
          "type": "array",
          "items": {
            "type": "object",
            "properties": {
              "tool": { "type": "string", "enum": ["none","calc","memory_search","list_files","write_file","index_path"] },
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

    public static void main(String[] args) throws Exception {
        Config cfg = Config.fromArgs(args);
        System.out.println("--- FRAYMUS: UNIVERSAL INTERFACE (SESSION + REFLECTOR) ---");

        Hippocampus.recall();
        VectorVault vault = new VectorVault();
        vault.load();
        
        OllamaSpine brain = new OllamaSpine(cfg.chatModel, cfg.embedModel);
        Transmudder soul = new Transmudder();
        ToolRouter tools = new ToolRouter(vault, soul, brain);
        RagEngine rag = new RagEngine(brain, vault);
        
        SessionMemory sessions = new SessionMemory(40, 12_000);
        Reflector reflector = new Reflector(brain);
        ConcurrentHashMap<WebSocket, Boolean> reflectEnabled = new ConcurrentHashMap<>();

        // CLI Indexing
        if (cfg.indexPath != null) {
            JsonObject a = new JsonObject(); a.addProperty("path", cfg.indexPath);
            System.out.println(tools.run("index_path", a).output);
            return;
        }

        WebSocketServer nerve = new WebSocketServer(new InetSocketAddress(cfg.port)) {
            @Override public void onOpen(WebSocket conn, ClientHandshake handshake) {
                sessions.init(conn);
                reflectEnabled.put(conn, true); // Default ON
                conn.send("FRAYMUS ONLINE. Reflect: ON. Use !fast <msg> to skip.");
            }
            @Override public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                sessions.drop(conn);
                reflectEnabled.remove(conn);
            }
            @Override public void onError(WebSocket conn, Exception ex) { ex.printStackTrace(); }
            @Override public void onStart() { System.out.println(">>> [NERVE] Listening on " + cfg.port); }

            @Override public void onMessage(WebSocket conn, String message) {
                try {
                    String user = message == null ? "" : message.trim();
                    if (user.isEmpty()) return;

                    // --- COMMANDS ---
                    if (user.equalsIgnoreCase("RESET")) { sessions.clear(conn); conn.send("SESSION CLEARED"); return; }
                    if (user.equalsIgnoreCase("REFLECT ON")) { reflectEnabled.put(conn, true); conn.send("REFLECTOR: ON"); return; }
                    if (user.equalsIgnoreCase("REFLECT OFF")) { reflectEnabled.put(conn, false); conn.send("REFLECTOR: OFF"); return; }
                    
                    if (user.toUpperCase().startsWith("TRANSMUTE:")) {
                        String path = user.substring(10).trim();
                        indexOneFile(path, soul, vault, brain);
                        conn.send("ABSORBED: " + path);
                        return;
                    }

                    // --- FAST MODE ---
                    boolean useFast = !reflectEnabled.getOrDefault(conn, true);
                    if (user.startsWith("!fast ")) {
                        useFast = true;
                        user = user.substring(6).trim();
                    }

                    // --- TOOL EXECUTION ---
                    if (user.startsWith("!calc ")) {
                         JsonObject a = new JsonObject(); a.addProperty("expression", user.substring(6));
                         conn.send(tools.run("calc", a).output);
                         return;
                    }

                    // --- RAG & PLANNING ---
                    String ragContext = rag.buildContext(user, 6, 8000);
                    
                    // Planning (Using history for better intent detection)
                    List<OllamaSpine.Msg> planMsgs = new ArrayList<>(sessions.snapshot(conn));
                    planMsgs.add(new OllamaSpine.Msg("system", "You are a Tool Planner. Decide tools. Return JSON."));
                    planMsgs.add(new OllamaSpine.Msg("user", "CONTEXT: " + ragContext + "\nQUERY: " + user));
                    
                    String planJson = brain.chatOnce(planMsgs, TOOL_PLAN_SCHEMA, Map.of("temperature", 0));
                    JsonArray calls = safeParseCalls(planJson);

                    StringBuilder toolResults = new StringBuilder();
                    if (calls != null) {
                        for (int i=0; i<calls.size(); i++) {
                            JsonObject call = calls.get(i).getAsJsonObject();
                            String t = call.get("tool").getAsString();
                            if (!"none".equals(t)) {
                                var res = tools.run(t, call.get("args").getAsJsonObject());
                                toolResults.append("TOOL(").append(t).append("): ").append(res.output).append("\n");
                            }
                        }
                    }

                    // --- GENERATION ---
                    String contextPacket = ragContext + "\n\n" + toolResults + "USER QUESTION:\n" + user;
                    String answer;

                    if (useFast) {
                        List<OllamaSpine.Msg> fastMsgs = new ArrayList<>();
                        fastMsgs.add(new OllamaSpine.Msg("system", "You are FRAYMUS. Cite [S#]. Be concise."));
                        fastMsgs.addAll(sessions.snapshot(conn));
                        fastMsgs.add(new OllamaSpine.Msg("user", contextPacket));
                        answer = brain.chatOnce(fastMsgs, null, Map.of("temperature", 0.2));
                    } else {
                        conn.send("[REFLECTOR] Draft -> Critique -> Refine...");
                        answer = reflector.reflect(user, contextPacket, new ArrayList<>(sessions.snapshot(conn)));
                    }

                    // --- MEMORY ---
                    Hippocampus.commitMemory("CONVERSATION", "User: " + user + " | AI: " + answer);
                    sessions.push(conn, "user", user);
                    sessions.push(conn, "assistant", answer);
                    conn.send(answer);

                } catch (Exception e) { conn.send("[ERROR] " + e.getMessage()); }
            }
        };
        nerve.setReuseAddr(true);
        nerve.start();
        System.out.println(">>> [FRAYMUS] WebSocket listening on ws://localhost:" + cfg.port);
        Thread.currentThread().join();
    }
    
    private static JsonArray safeParseCalls(String json) {
        try { return JsonParser.parseString(json).getAsJsonObject().getAsJsonArray("calls"); } 
        catch (Exception e) { return null; }
    }
    
    private static void indexOneFile(String f, Transmudder s, VectorVault v, OllamaSpine b) throws Exception {
        List<String> c = s.chunk(s.cleanse(s.readFileToText(f)), 1000, 100);
        v.addAndPersist(f, c, b.embedBatch(c));
    }
}
