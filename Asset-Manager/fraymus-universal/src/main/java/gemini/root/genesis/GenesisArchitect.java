package gemini.root.genesis;

import gemini.root.limbs.ClawConnector;
import gemini.root.memory.BlackBox;
import gemini.root.OllamaSpine;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GENESIS ARCHITECT: The Brain that Spawns Universes
 * 
 * Takes a one-sentence intent and decomposes it into a rigorous
 * engineering Blueprint - complete software stack specification.
 * 
 * Uses LLM (via Ollama or OpenClaw) as the System Architect oracle.
 */
public class GenesisArchitect {

    private final ClawConnector claw;
    private final BlackBox memory;
    private OllamaSpine brain;
    private boolean useLLM = false;

    public GenesisArchitect() {
        this.claw = new ClawConnector();
        this.memory = new BlackBox();
        
        // Try to initialize Ollama for local LLM
        try {
            this.brain = new OllamaSpine("llama3", "nomic-embed-text");
            this.useLLM = true;
            System.out.println("📐 ARCHITECT: LLM backend available (Ollama)");
        } catch (Exception e) {
            System.out.println("📐 ARCHITECT: No LLM, using template patterns");
        }
    }

    public GenesisArchitect(OllamaSpine brain) {
        this.claw = new ClawConnector();
        this.memory = new BlackBox();
        this.brain = brain;
        this.useLLM = true;
    }

    public GenesisArchitect(BlackBox sharedMemory) {
        this.claw = new ClawConnector();
        this.memory = sharedMemory;
        
        try {
            this.brain = new OllamaSpine("llama3", "nomic-embed-text");
            this.useLLM = true;
        } catch (Exception e) {
            // fallback
        }
    }

    /**
     * THE BIG BANG: Create a universe from a sentence.
     */
    public Blueprint designSystem(String intent) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   📐 GENESIS ARCHITECT: DESIGNING SYSTEM               ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("Intent: \"" + intent + "\"");
        System.out.println();

        // 1. RECALL PAST WISDOM
        String wisdom = memory.recall(intent);
        System.out.println("🧠 MEMORY: " + wisdom);
        System.out.println();

        Blueprint plan;
        
        if (useLLM) {
            plan = designWithLLM(intent, wisdom);
        } else {
            plan = designWithTemplates(intent);
        }
        
        System.out.println("\n✅ BLUEPRINT GENERATED: " + plan.modules.size() + " modules");
        for (Module m : plan.modules) {
            System.out.println("   [" + m.type + "] " + m.name + " (" + m.tech + ") → " + m.path);
        }
        
        return plan;
    }

    /**
     * Record the outcome of a Genesis build
     */
    public void recordOutcome(String intent, boolean success, String details) {
        memory.remember(
            "GENESIS: " + intent,
            details,
            success
        );
    }

    public BlackBox getMemory() {
        return memory;
    }

    /**
     * Design using LLM oracle (with wisdom context)
     */
    private Blueprint designWithLLM(String intent, String wisdom) {
        String systemPrompt = """
            You are a Senior System Architect. Design a complete software stack.
            Return a structured list of modules in this EXACT format, one per line:
            MODULE|name|type|technology|filepath
            
            Types: BACKEND, FRONTEND, DATABASE, CONFIG, TEST, DOCS
            
            Be specific with technologies (Node.js, React, PostgreSQL, etc.)
            Include at least: backend, frontend, database schema, config, and tests.
            """;
        
        String userPrompt = "Design a full software stack for: " + intent;
        
        // Add wisdom from past experiences
        if (wisdom != null && !wisdom.contains("NO PRECEDENT")) {
            userPrompt += "\n\nPAST EXPERIENCE TO CONSIDER:\n" + wisdom;
        }
        
        try {
            List<OllamaSpine.Msg> messages = new ArrayList<>();
            messages.add(new OllamaSpine.Msg("system", systemPrompt));
            messages.add(new OllamaSpine.Msg("user", userPrompt));
            
            String response = brain.chatOnce(messages, null, null);
            return parseLLMResponse(response, intent);
        } catch (Exception e) {
            System.out.println("   LLM failed, falling back to templates: " + e.getMessage());
            return designWithTemplates(intent);
        }
    }

    /**
     * Parse LLM response into Blueprint
     */
    private Blueprint parseLLMResponse(String response, String intent) {
        Blueprint plan = new Blueprint();
        plan.intent = intent;
        plan.timestamp = System.currentTimeMillis();
        
        // Parse MODULE|name|type|tech|path format
        Pattern pattern = Pattern.compile("MODULE\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|([^|\\n]+)");
        Matcher matcher = pattern.matcher(response);
        
        while (matcher.find()) {
            Module m = new Module(
                matcher.group(1).trim(),  // name
                matcher.group(2).trim(),  // type
                matcher.group(3).trim(),  // tech
                matcher.group(4).trim()   // path
            );
            plan.modules.add(m);
        }
        
        // If parsing failed, use templates
        if (plan.modules.isEmpty()) {
            return designWithTemplates(intent);
        }
        
        return plan;
    }

    /**
     * Design using pattern templates (no LLM needed)
     */
    private Blueprint designWithTemplates(String intent) {
        Blueprint plan = new Blueprint();
        plan.intent = intent;
        plan.timestamp = System.currentTimeMillis();
        
        String intentLower = intent.toLowerCase();
        
        // Detect project type from intent
        if (intentLower.contains("chat") || intentLower.contains("messaging")) {
            plan.projectType = "REALTIME_APP";
            addChatAppModules(plan);
        } else if (intentLower.contains("trading") || intentLower.contains("exchange") || intentLower.contains("crypto")) {
            plan.projectType = "TRADING_SYSTEM";
            addTradingModules(plan);
        } else if (intentLower.contains("api") || intentLower.contains("rest") || intentLower.contains("service")) {
            plan.projectType = "API_SERVICE";
            addAPIModules(plan);
        } else if (intentLower.contains("dashboard") || intentLower.contains("admin")) {
            plan.projectType = "DASHBOARD";
            addDashboardModules(plan);
        } else if (intentLower.contains("game") || intentLower.contains("arcade")) {
            plan.projectType = "GAME";
            addGameModules(plan);
        } else {
            plan.projectType = "FULLSTACK_APP";
            addFullstackModules(plan);
        }
        
        // Always add common modules
        plan.modules.add(new Module("Config", "CONFIG", "YAML", "config/config.yaml"));
        plan.modules.add(new Module("README", "DOCS", "Markdown", "README.md"));
        plan.modules.add(new Module("Tests", "TEST", "Jest/Pytest", "tests/test_main.js"));
        
        return plan;
    }

    private void addChatAppModules(Blueprint plan) {
        plan.modules.add(new Module("ChatServer", "BACKEND", "Node.js/WebSocket", "src/server/index.js"));
        plan.modules.add(new Module("MessageHandler", "BACKEND", "Node.js", "src/server/messages.js"));
        plan.modules.add(new Module("Encryption", "BACKEND", "Node.js/crypto", "src/server/crypto.js"));
        plan.modules.add(new Module("ChatUI", "FRONTEND", "React", "src/ui/App.jsx"));
        plan.modules.add(new Module("ChatComponent", "FRONTEND", "React", "src/ui/Chat.jsx"));
        plan.modules.add(new Module("Database", "DATABASE", "SQLite", "schema/messages.sql"));
    }

    private void addTradingModules(Blueprint plan) {
        plan.modules.add(new Module("TradeEngine", "BACKEND", "Python", "src/engine/trade_engine.py"));
        plan.modules.add(new Module("OrderBook", "BACKEND", "Python", "src/engine/order_book.py"));
        plan.modules.add(new Module("MarketData", "BACKEND", "Python", "src/engine/market_data.py"));
        plan.modules.add(new Module("RiskManager", "BACKEND", "Python", "src/engine/risk.py"));
        plan.modules.add(new Module("Dashboard", "FRONTEND", "React", "src/ui/Dashboard.jsx"));
        plan.modules.add(new Module("Charts", "FRONTEND", "React/D3", "src/ui/Charts.jsx"));
        plan.modules.add(new Module("Database", "DATABASE", "PostgreSQL", "schema/trades.sql"));
        plan.modules.add(new Module("API", "BACKEND", "FastAPI", "src/api/main.py"));
    }

    private void addAPIModules(Blueprint plan) {
        plan.modules.add(new Module("APIServer", "BACKEND", "Express.js", "src/server/index.js"));
        plan.modules.add(new Module("Routes", "BACKEND", "Express.js", "src/server/routes.js"));
        plan.modules.add(new Module("Controllers", "BACKEND", "Express.js", "src/server/controllers.js"));
        plan.modules.add(new Module("Models", "BACKEND", "Sequelize", "src/models/index.js"));
        plan.modules.add(new Module("Database", "DATABASE", "PostgreSQL", "schema/schema.sql"));
        plan.modules.add(new Module("Swagger", "DOCS", "OpenAPI", "docs/openapi.yaml"));
    }

    private void addDashboardModules(Blueprint plan) {
        plan.modules.add(new Module("Dashboard", "FRONTEND", "React", "src/ui/App.jsx"));
        plan.modules.add(new Module("Sidebar", "FRONTEND", "React", "src/ui/Sidebar.jsx"));
        plan.modules.add(new Module("DataGrid", "FRONTEND", "React/AG-Grid", "src/ui/DataGrid.jsx"));
        plan.modules.add(new Module("Charts", "FRONTEND", "React/Recharts", "src/ui/Charts.jsx"));
        plan.modules.add(new Module("API", "BACKEND", "Express.js", "src/server/api.js"));
        plan.modules.add(new Module("Database", "DATABASE", "SQLite", "data/dashboard.db"));
    }

    private void addGameModules(Blueprint plan) {
        plan.modules.add(new Module("GameEngine", "BACKEND", "Java", "src/engine/GameEngine.java"));
        plan.modules.add(new Module("Physics", "BACKEND", "Java", "src/engine/Physics.java"));
        plan.modules.add(new Module("Renderer", "FRONTEND", "Java/OpenGL", "src/render/Renderer.java"));
        plan.modules.add(new Module("Input", "BACKEND", "Java", "src/input/InputHandler.java"));
        plan.modules.add(new Module("Assets", "CONFIG", "JSON", "assets/manifest.json"));
    }

    private void addFullstackModules(Blueprint plan) {
        plan.modules.add(new Module("Backend", "BACKEND", "Node.js", "src/server/index.js"));
        plan.modules.add(new Module("Routes", "BACKEND", "Express.js", "src/server/routes.js"));
        plan.modules.add(new Module("Frontend", "FRONTEND", "React", "src/ui/App.jsx"));
        plan.modules.add(new Module("Components", "FRONTEND", "React", "src/ui/components/index.js"));
        plan.modules.add(new Module("Database", "DATABASE", "SQLite", "schema/schema.sql"));
        plan.modules.add(new Module("Styles", "FRONTEND", "TailwindCSS", "src/ui/styles.css"));
    }

    // ========== DATA STRUCTURES ==========

    public static class Blueprint {
        public String intent;
        public String projectType;
        public long timestamp;
        public List<Module> modules = new ArrayList<>();
        
        public String toJSON() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"intent\": \"").append(intent).append("\",\n");
            sb.append("  \"projectType\": \"").append(projectType).append("\",\n");
            sb.append("  \"modules\": [\n");
            for (int i = 0; i < modules.size(); i++) {
                Module m = modules.get(i);
                sb.append("    {\"name\": \"").append(m.name)
                  .append("\", \"type\": \"").append(m.type)
                  .append("\", \"tech\": \"").append(m.tech)
                  .append("\", \"path\": \"").append(m.path).append("\"}");
                if (i < modules.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ]\n}");
            return sb.toString();
        }
    }

    public static class Module {
        public String name;
        public String type;    // BACKEND, FRONTEND, DATABASE, CONFIG, TEST, DOCS
        public String tech;    // Node.js, React, PostgreSQL, etc.
        public String path;    // File path
        
        public Module(String name, String type, String tech, String path) {
            this.name = name;
            this.type = type;
            this.tech = tech;
            this.path = path;
        }
    }
}
