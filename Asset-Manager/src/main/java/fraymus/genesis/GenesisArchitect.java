package fraymus.genesis;

import fraymus.limbs.ClawConnector;
import java.util.ArrayList;
import java.util.List;

/**
 * GENESIS ARCHITECT - The Brain
 * 
 * Takes vague intent and transforms it into rigorous engineering plan.
 * 
 * Process:
 * 1. Receive intent ("Build a decentralized exchange")
 * 2. Consult Oracle (LLM via OpenClaw)
 * 3. Generate Blueprint (JSON structure)
 * 4. Return Module list for parallel construction
 * 
 * This is the Architect that designs universes.
 */
public class GenesisArchitect {

    private final ClawConnector claw;
    
    private static final double PHI = 1.618033988749895;

    public GenesisArchitect() {
        this.claw = new ClawConnector();
    }

    /**
     * THE BIG BANG: Create a universe from a sentence.
     */
    public Blueprint designSystem(String intent) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         📐 GENESIS ARCHITECT                                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Intent: " + intent);
        System.out.println();
        System.out.println("Consulting the Oracle...");

        // 1. CONSULT THE ORACLE (LLM via OpenClaw)
        // We ask the LLM to act as a System Architect and return structured plan
        String prompt = buildArchitectPrompt(intent);
        
        System.out.println("Generating system architecture...");
        String response = claw.dispatch(prompt, "CONTEXT: SYSTEM_DESIGN");
        
        // 2. PARSE BLUEPRINT
        Blueprint plan = parseBlueprint(response, intent);
        
        System.out.println();
        System.out.println("✓ Blueprint Generated:");
        System.out.println("  Modules: " + plan.modules.size());
        System.out.println("  Technologies: " + plan.getTechnologies());
        System.out.println();
        
        for (Module module : plan.modules) {
            System.out.println("  • " + module.name + " (" + module.tech + ") → " + module.path);
        }
        
        System.out.println();
        
        return plan;
    }

    /**
     * Build architect prompt for LLM
     */
    private String buildArchitectPrompt(String intent) {
        return "You are a Senior System Architect. Design a complete software stack for: '" + intent + "'.\n\n" +
               "Provide a structured breakdown with:\n" +
               "- Module name\n" +
               "- Technology/Language\n" +
               "- File path\n" +
               "- Brief description\n\n" +
               "Include all necessary components: Backend, Frontend, Database, Tests, Config.\n" +
               "Be specific about technologies (e.g., Node.js, React, PostgreSQL).\n" +
               "Return a clear, structured response.";
    }

    /**
     * Parse LLM response into Blueprint
     * In production, this would use proper JSON parsing
     */
    private Blueprint parseBlueprint(String response, String intent) {
        Blueprint plan = new Blueprint();
        plan.intent = intent;
        
        // For demo purposes, create a realistic blueprint based on common patterns
        // In production, this would parse the actual LLM response
        
        if (intent.toLowerCase().contains("chat") || intent.toLowerCase().contains("messaging")) {
            // Chat app architecture
            plan.modules.add(new Module("WebSocket Server", "Node.js", "src/server/websocket.js", 
                "Real-time message handling"));
            plan.modules.add(new Module("Message Store", "PostgreSQL", "src/db/schema.sql", 
                "Message persistence"));
            plan.modules.add(new Module("Chat UI", "React", "src/ui/ChatApp.jsx", 
                "User interface"));
            plan.modules.add(new Module("Encryption Module", "JavaScript", "src/crypto/encryption.js", 
                "End-to-end encryption"));
            plan.modules.add(new Module("API Server", "Express", "src/server/api.js", 
                "REST API endpoints"));
            plan.modules.add(new Module("Tests", "Jest", "tests/integration.test.js", 
                "Integration tests"));
        } else if (intent.toLowerCase().contains("exchange") || intent.toLowerCase().contains("trading")) {
            // Exchange architecture
            plan.modules.add(new Module("Order Book", "Python", "src/engine/orderbook.py", 
                "Order matching engine"));
            plan.modules.add(new Module("Trading API", "FastAPI", "src/api/trading.py", 
                "Trading endpoints"));
            plan.modules.add(new Module("Market Data", "Redis", "src/db/market_data.conf", 
                "Real-time market data"));
            plan.modules.add(new Module("Trading UI", "React", "src/ui/TradingDashboard.jsx", 
                "Trading interface"));
            plan.modules.add(new Module("Wallet Service", "Solidity", "src/contracts/Wallet.sol", 
                "Smart contract wallet"));
            plan.modules.add(new Module("Tests", "Pytest", "tests/test_trading.py", 
                "Trading tests"));
        } else {
            // Generic web app architecture
            plan.modules.add(new Module("Backend API", "Node.js", "src/server/index.js", 
                "Main API server"));
            plan.modules.add(new Module("Database", "SQLite", "src/db/schema.sql", 
                "Data persistence"));
            plan.modules.add(new Module("Frontend", "React", "src/ui/App.jsx", 
                "User interface"));
            plan.modules.add(new Module("Config", "JSON", "config/app.json", 
                "Application config"));
            plan.modules.add(new Module("Tests", "Jest", "tests/app.test.js", 
                "Unit tests"));
        }
        
        return plan;
    }

    /**
     * Blueprint data structure
     */
    public static class Blueprint {
        public String intent;
        public List<Module> modules = new ArrayList<>();
        
        public String getTechnologies() {
            List<String> techs = new ArrayList<>();
            for (Module m : modules) {
                if (!techs.contains(m.tech)) {
                    techs.add(m.tech);
                }
            }
            return String.join(", ", techs);
        }
    }

    /**
     * Module data structure
     */
    public static class Module {
        public String name;
        public String tech;
        public String path;
        public String description;
        
        public Module(String name, String tech, String path, String description) {
            this.name = name;
            this.tech = tech;
            this.path = path;
            this.description = description;
        }
    }
}
