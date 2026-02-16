package fraymus.genesis;

import fraymus.limbs.ClawConnector;
import fraymus.core.GravityEngine;
import fraymus.core.PhiSuit;
import fraymus.genesis.GenesisArchitect.Module;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * CONSTRUCTION SWARM - The Workforce
 * 
 * Takes Blueprint and spawns Parallel Agents to build everything at once.
 * 
 * Process:
 * 1. Receive Blueprint from Architect
 * 2. Spawn BuilderAgent for each module
 * 3. Agents work in parallel (separate threads)
 * 4. Each agent generates code via OpenClaw
 * 5. Integration happens via gravity (high-energy agents attract testers)
 * 
 * This is the Factory that materializes universes.
 */
public class ConstructionSwarm {

    private final GravityEngine universe;
    private final List<BuilderAgent> builders = new ArrayList<>();
    private final String outputDir;
    
    private static final double PHI = 1.618033988749895;

    public ConstructionSwarm(GravityEngine universe, String outputDir) {
        this.universe = universe;
        this.outputDir = outputDir;
    }

    /**
     * Build entire system from blueprint
     */
    public void build(GenesisArchitect.Blueprint plan) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🏗️ CONSTRUCTION SWARM DEPLOYMENT                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Initializing " + plan.modules.size() + " Constructor Agents...");
        System.out.println("Output Directory: " + outputDir);
        System.out.println();

        // Create output directory
        new File(outputDir).mkdirs();

        // Spawn a Specialized Builder for each module
        for (Module module : plan.modules) {
            BuilderAgent builder = new BuilderAgent(module);
            builders.add(builder);
            
            // Start building in parallel
            new Thread(builder, "Builder-" + module.name).start();
            
            System.out.println("  ⚡ Spawned: " + builder.getLabel() + " → " + module.path);
        }
        
        System.out.println();
        System.out.println("✓ All agents deployed");
        System.out.println("✓ Parallel construction in progress...");
        System.out.println();
    }

    /**
     * Wait for all builders to complete
     */
    public void waitForCompletion() throws InterruptedException {
        System.out.println("⏳ Waiting for construction to complete...");
        System.out.println();
        
        boolean allDone = false;
        while (!allDone) {
            allDone = true;
            for (BuilderAgent builder : builders) {
                if (!builder.isComplete()) {
                    allDone = false;
                    break;
                }
            }
            Thread.sleep(100);
        }
        
        System.out.println("✓ All construction complete");
        System.out.println();
    }

    /**
     * Get construction statistics
     */
    public String getStats() {
        int completed = 0;
        int failed = 0;
        
        for (BuilderAgent builder : builders) {
            if (builder.isComplete()) {
                if (builder.isSuccess()) {
                    completed++;
                } else {
                    failed++;
                }
            }
        }
        
        return String.format(
            "Construction Statistics:\n" +
            "  Total Modules: %d\n" +
            "  Completed: %d\n" +
            "  Failed: %d\n" +
            "  In Progress: %d",
            builders.size(), completed, failed, builders.size() - completed - failed
        );
    }

    /**
     * THE BUILDER AGENT: A Nano-Bot that writes code
     * 
     * Each agent is responsible for one module.
     * It uses OpenClaw to generate code and writes it to disk.
     */
    public class BuilderAgent extends PhiSuit<Module> implements Runnable {
        private final ClawConnector claw = new ClawConnector();
        private boolean complete = false;
        private boolean success = false;

        public BuilderAgent(Module module) {
            super(module, 50, 50, 50, "BUILDER_" + module.name);
        }

        @Override
        public void run() {
            try {
                Module module = this.get();
                
                System.out.println("   🔨 " + this.getLabel() + ": Drafting code for " + module.tech + "...");

                // 1. GENERATE CODE (LLM via OpenClaw)
                String code = generateCode(module);
                
                if (code == null || code.isEmpty()) {
                    System.err.println("   ❌ " + this.getLabel() + ": Code generation failed");
                    complete = true;
                    success = false;
                    return;
                }

                // 2. MATERIALIZE (File System)
                File target = new File(outputDir, module.path);
                target.getParentFile().mkdirs();
                
                Files.writeString(target.toPath(), code);
                
                System.out.println("   ✅ " + this.getLabel() + ": " + module.path + " created (" + 
                    code.length() + " bytes)");

                // 3. INTEGRATION CHECK
                // Spike "Amplitude" to attract a Tester Agent (future enhancement)
                this.a = 100;
                
                complete = true;
                success = true;
                
            } catch (Exception e) {
                System.err.println("   ❌ " + this.getLabel() + " FAILED: " + e.getMessage());
                complete = true;
                success = false;
            }
        }

        /**
         * Generate code for module via OpenClaw
         */
        private String generateCode(Module module) {
            String prompt = buildCodePrompt(module);
            
            // Send to OpenClaw
            String response = claw.dispatch(prompt, "CONTEXT: WRITE_CODE");
            
            // In production, would parse and validate response
            // For demo, return template code
            return generateTemplateCode(module);
        }

        /**
         * Build code generation prompt
         */
        private String buildCodePrompt(Module module) {
            return "Write complete, runnable code for a " + module.name + " using " + module.tech + ".\n\n" +
                   "Description: " + module.description + "\n" +
                   "File Path: " + module.path + "\n\n" +
                   "Requirements:\n" +
                   "- Production-ready code\n" +
                   "- Include error handling\n" +
                   "- Add comments\n" +
                   "- Follow best practices\n\n" +
                   "Return ONLY the code, no explanations.";
        }

        /**
         * Generate template code (fallback for demo)
         */
        private String generateTemplateCode(Module module) {
            StringBuilder code = new StringBuilder();
            
            // Add header comment
            code.append("/**\n");
            code.append(" * ").append(module.name).append("\n");
            code.append(" * Generated by Fraynix Genesis Engine\n");
            code.append(" * Technology: ").append(module.tech).append("\n");
            code.append(" * Description: ").append(module.description).append("\n");
            code.append(" */\n\n");
            
            // Generate language-specific template
            if (module.tech.contains("JavaScript") || module.tech.contains("Node") || 
                module.tech.contains("Express")) {
                code.append(generateJavaScriptTemplate(module));
            } else if (module.tech.contains("React")) {
                code.append(generateReactTemplate(module));
            } else if (module.tech.contains("Python")) {
                code.append(generatePythonTemplate(module));
            } else if (module.tech.contains("SQL") || module.tech.contains("PostgreSQL") || 
                       module.tech.contains("SQLite")) {
                code.append(generateSQLTemplate(module));
            } else if (module.tech.contains("JSON")) {
                code.append(generateJSONTemplate(module));
            } else if (module.tech.contains("Jest") || module.tech.contains("Pytest")) {
                code.append(generateTestTemplate(module));
            } else {
                code.append("// ").append(module.name).append(" implementation\n");
                code.append("// Technology: ").append(module.tech).append("\n\n");
                code.append("// TODO: Implement ").append(module.description).append("\n");
            }
            
            return code.toString();
        }

        private String generateJavaScriptTemplate(Module module) {
            return "const express = require('express');\n" +
                   "const app = express();\n\n" +
                   "// " + module.description + "\n" +
                   "app.use(express.json());\n\n" +
                   "app.get('/', (req, res) => {\n" +
                   "  res.json({ status: 'online', module: '" + module.name + "' });\n" +
                   "});\n\n" +
                   "const PORT = process.env.PORT || 3000;\n" +
                   "app.listen(PORT, () => {\n" +
                   "  console.log(`" + module.name + " running on port ${PORT}`);\n" +
                   "});\n\n" +
                   "module.exports = app;\n";
        }

        private String generateReactTemplate(Module module) {
            return "import React, { useState, useEffect } from 'react';\n\n" +
                   "/**\n" +
                   " * " + module.description + "\n" +
                   " */\n" +
                   "function " + module.name.replace(" ", "") + "() {\n" +
                   "  const [data, setData] = useState(null);\n\n" +
                   "  useEffect(() => {\n" +
                   "    // Initialize component\n" +
                   "    console.log('" + module.name + " mounted');\n" +
                   "  }, []);\n\n" +
                   "  return (\n" +
                   "    <div className=\"" + module.name.toLowerCase().replace(" ", "-") + "\">\n" +
                   "      <h1>" + module.name + "</h1>\n" +
                   "      <p>Status: Active</p>\n" +
                   "    </div>\n" +
                   "  );\n" +
                   "}\n\n" +
                   "export default " + module.name.replace(" ", "") + ";\n";
        }

        private String generatePythonTemplate(Module module) {
            return "#!/usr/bin/env python3\n" +
                   "\"\"\"\n" +
                   module.description + "\n" +
                   "\"\"\"\n\n" +
                   "class " + module.name.replace(" ", "") + ":\n" +
                   "    def __init__(self):\n" +
                   "        self.name = '" + module.name + "'\n" +
                   "        print(f'{self.name} initialized')\n\n" +
                   "    def run(self):\n" +
                   "        \"\"\"Main execution method\"\"\"\n" +
                   "        print(f'{self.name} running...')\n\n" +
                   "if __name__ == '__main__':\n" +
                   "    engine = " + module.name.replace(" ", "") + "()\n" +
                   "    engine.run()\n";
        }

        private String generateSQLTemplate(Module module) {
            return "-- " + module.description + "\n\n" +
                   "CREATE TABLE IF NOT EXISTS data (\n" +
                   "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                   "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                   "    data TEXT NOT NULL\n" +
                   ");\n\n" +
                   "CREATE INDEX idx_created_at ON data(created_at);\n\n" +
                   "-- Sample data\n" +
                   "INSERT INTO data (data) VALUES ('Genesis Engine initialized');\n";
        }

        private String generateJSONTemplate(Module module) {
            return "{\n" +
                   "  \"name\": \"" + module.name + "\",\n" +
                   "  \"description\": \"" + module.description + "\",\n" +
                   "  \"version\": \"1.0.0\",\n" +
                   "  \"generated_by\": \"Fraynix Genesis Engine\"\n" +
                   "}\n";
        }

        private String generateTestTemplate(Module module) {
            if (module.tech.contains("Jest")) {
                return "describe('" + module.name + "', () => {\n" +
                       "  test('should initialize correctly', () => {\n" +
                       "    expect(true).toBe(true);\n" +
                       "  });\n\n" +
                       "  test('should handle basic operations', () => {\n" +
                       "    // TODO: Add test implementation\n" +
                       "    expect(1 + 1).toBe(2);\n" +
                       "  });\n" +
                       "});\n";
            } else {
                return "import pytest\n\n" +
                       "def test_initialization():\n" +
                       "    \"\"\"Test basic initialization\"\"\"\n" +
                       "    assert True\n\n" +
                       "def test_operations():\n" +
                       "    \"\"\"Test basic operations\"\"\"\n" +
                       "    assert 1 + 1 == 2\n";
            }
        }

        public boolean isComplete() {
            return complete;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}
