package gemini.root.genesis;

import gemini.root.limbs.ClawConnector;
import gemini.root.physics.HiveGravityEngine;
import gemini.root.physics.PhiSuit;
import gemini.root.OllamaSpine;
import gemini.root.genesis.GenesisArchitect.Blueprint;
import gemini.root.genesis.GenesisArchitect.Module;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CONSTRUCTION SWARM: The Factory Arm
 * 
 * Takes a Blueprint and spawns Parallel BuilderAgents to construct
 * all modules simultaneously. Each agent is a physics particle that
 * can attract helpers via gravity.
 * 
 * This is the Recursive Constructor Swarm.
 */
public class ConstructionSwarm {

    private final HiveGravityEngine universe;
    private final File outputRoot;
    private OllamaSpine brain;
    private boolean useLLM = false;
    
    private final List<BuilderAgent> agents = new ArrayList<>();
    private final AtomicInteger completedCount = new AtomicInteger(0);
    private final AtomicInteger failedCount = new AtomicInteger(0);

    public ConstructionSwarm(HiveGravityEngine universe, File outputRoot) {
        this.universe = universe;
        this.outputRoot = outputRoot;
        
        // Try to initialize Ollama
        try {
            this.brain = new OllamaSpine("llama3", "nomic-embed-text");
            this.useLLM = true;
        } catch (Exception e) {
            System.out.println("🏗️ SWARM: No LLM, using code templates");
        }
    }

    public ConstructionSwarm(HiveGravityEngine universe, File outputRoot, OllamaSpine brain) {
        this.universe = universe;
        this.outputRoot = outputRoot;
        this.brain = brain;
        this.useLLM = true;
    }

    /**
     * Build all modules from the Blueprint in parallel
     */
    public void build(Blueprint plan) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   🏗️ CONSTRUCTION SWARM: DEPLOYING BUILDERS            ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("Output: " + outputRoot.getAbsolutePath());
        System.out.println("Modules: " + plan.modules.size());
        System.out.println();

        // Create output directory
        outputRoot.mkdirs();
        
        // Write blueprint to output
        try {
            Files.writeString(
                new File(outputRoot, "BLUEPRINT.json").toPath(),
                plan.toJSON()
            );
        } catch (Exception e) {
            System.err.println("Warning: Could not save blueprint: " + e.getMessage());
        }

        // Spawn a BuilderAgent for each module
        CountDownLatch latch = new CountDownLatch(plan.modules.size());
        
        int index = 0;
        for (Module module : plan.modules) {
            // Position agents in a spiral pattern
            double angle = index * 0.5;
            double radius = 20 + index * 5;
            int x = (int)(50 + radius * Math.cos(angle));
            int y = (int)(50 + radius * Math.sin(angle));
            int z = 50 + (index % 10) * 5;
            
            BuilderAgent builder = new BuilderAgent(module, x, y, z, latch);
            agents.add(builder);
            
            // Register in physics universe
            universe.register(builder);
            
            // Start builder thread
            Thread t = new Thread(builder, "Builder-" + module.name);
            t.start();
            
            index++;
        }

        System.out.println("🚀 " + plan.modules.size() + " BuilderAgents deployed. Building in parallel...\n");

        // Wait for all builders to complete (with timeout)
        try {
            boolean finished = latch.await(300, java.util.concurrent.TimeUnit.SECONDS);
            if (!finished) {
                System.out.println("⚠️ Build timed out after 5 minutes");
            }
        } catch (InterruptedException e) {
            System.out.println("⚠️ Build interrupted");
        }

        // Report results
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║   🏗️ CONSTRUCTION COMPLETE                             ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.printf("║   ✅ Succeeded: %-5d                                   ║%n", completedCount.get());
        System.out.printf("║   ❌ Failed:    %-5d                                   ║%n", failedCount.get());
        System.out.println("║   📁 Output: " + truncate(outputRoot.getAbsolutePath(), 40) + "  ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }

    /**
     * THE BUILDER AGENT: A Nano-Bot that writes code
     * 
     * Each agent is also a physics particle - it can attract
     * helper agents via gravity when it needs assistance.
     */
    public class BuilderAgent extends PhiSuit<Module> implements Runnable {
        
        private final ClawConnector claw = new ClawConnector();
        private final CountDownLatch latch;
        private volatile boolean complete = false;
        private volatile boolean success = false;
        private String error = null;

        public BuilderAgent(Module module, int x, int y, int z, CountDownLatch latch) {
            super(module, x, y, z);
            this.label = "BUILDER_" + module.name;
            this.amplitude = 10.0;  // Initial mass
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                System.out.println("   🔨 " + label + ": Building " + data.name + " (" + data.tech + ")...");
                
                // 1. GENERATE CODE
                String code = generateCode();
                
                // 2. MATERIALIZE (Write to file system)
                File target = new File(outputRoot, data.path);
                target.getParentFile().mkdirs();
                
                Files.writeString(target.toPath(), code);
                
                // 3. SUCCESS
                this.amplitude = 100.0;  // Increase gravity to signal completion
                this.success = true;
                completedCount.incrementAndGet();
                
                System.out.println("   ✅ " + label + ": " + data.path + " created (" + code.length() + " chars)");
                
            } catch (Exception e) {
                this.error = e.getMessage();
                this.amplitude = 1.0;  // Low gravity = failed
                failedCount.incrementAndGet();
                System.err.println("   ❌ " + label + " FAILED: " + e.getMessage());
            } finally {
                this.complete = true;
                this.active = false;
                latch.countDown();
            }
        }

        /**
         * Generate code for this module
         */
        private String generateCode() {
            if (useLLM) {
                return generateWithLLM();
            } else {
                return generateWithTemplate();
            }
        }

        /**
         * Generate code using LLM
         */
        private String generateWithLLM() {
            String systemPrompt = """
                You are a Senior Software Engineer. Write production-quality code.
                Return ONLY the code, no explanations. Include proper imports and comments.
                Follow best practices for the specified technology.
                """;
            
            String userPrompt = String.format(
                "Write complete, runnable code for: %s\n" +
                "Technology: %s\n" +
                "Type: %s\n" +
                "File: %s\n" +
                "Return ONLY the code.",
                data.name, data.tech, data.type, data.path
            );
            
            try {
                List<OllamaSpine.Msg> messages = new ArrayList<>();
                messages.add(new OllamaSpine.Msg("system", systemPrompt));
                messages.add(new OllamaSpine.Msg("user", userPrompt));
                
                return brain.chatOnce(messages, null, null);
            } catch (Exception e) {
                System.out.println("      LLM failed for " + data.name + ", using template");
                return generateWithTemplate();
            }
        }

        /**
         * Generate code using templates
         */
        private String generateWithTemplate() {
            String ext = getExtension(data.path);
            
            switch (ext) {
                case "js":
                case "jsx":
                    return generateJavaScript();
                case "py":
                    return generatePython();
                case "java":
                    return generateJava();
                case "sql":
                    return generateSQL();
                case "yaml":
                case "yml":
                    return generateYAML();
                case "json":
                    return generateJSON();
                case "md":
                    return generateMarkdown();
                case "css":
                    return generateCSS();
                default:
                    return generateGeneric();
            }
        }

        private String generateJavaScript() {
            if (data.type.equals("FRONTEND")) {
                return String.format("""
                    // %s - Generated by Fraynix Genesis Engine
                    // Technology: %s
                    
                    import React, { useState, useEffect } from 'react';
                    
                    export default function %s() {
                        const [data, setData] = useState(null);
                        const [loading, setLoading] = useState(true);
                        
                        useEffect(() => {
                            // TODO: Implement data fetching
                            setLoading(false);
                        }, []);
                        
                        if (loading) return <div>Loading...</div>;
                        
                        return (
                            <div className="container mx-auto p-4">
                                <h1 className="text-2xl font-bold">%s</h1>
                                {/* TODO: Implement component UI */}
                            </div>
                        );
                    }
                    """, data.name, data.tech, sanitizeName(data.name), data.name);
            } else {
                return String.format("""
                    // %s - Generated by Fraynix Genesis Engine
                    // Technology: %s
                    
                    const express = require('express');
                    const app = express();
                    const PORT = process.env.PORT || 3000;
                    
                    app.use(express.json());
                    
                    // Health check
                    app.get('/health', (req, res) => {
                        res.json({ status: 'ok', module: '%s' });
                    });
                    
                    // TODO: Implement %s routes
                    app.get('/api/%s', (req, res) => {
                        res.json({ message: '%s endpoint' });
                    });
                    
                    app.listen(PORT, () => {
                        console.log(`%s running on port ${PORT}`);
                    });
                    
                    module.exports = app;
                    """, data.name, data.tech, data.name, data.name, 
                    data.name.toLowerCase(), data.name, data.name);
            }
        }

        private String generatePython() {
            return String.format("""
                # %s - Generated by Fraynix Genesis Engine
                # Technology: %s
                
                import logging
                from typing import Dict, List, Optional
                from dataclasses import dataclass
                
                logging.basicConfig(level=logging.INFO)
                logger = logging.getLogger(__name__)
                
                @dataclass
                class %sConfig:
                    \"\"\"Configuration for %s\"\"\"
                    enabled: bool = True
                    debug: bool = False
                
                class %s:
                    \"\"\"
                    %s
                    
                    Generated by Fraynix Genesis Engine.
                    \"\"\"
                    
                    def __init__(self, config: Optional[%sConfig] = None):
                        self.config = config or %sConfig()
                        logger.info(f"%s initialized")
                    
                    def process(self, data: Dict) -> Dict:
                        \"\"\"Process incoming data.\"\"\"
                        # TODO: Implement processing logic
                        logger.info(f"Processing: {data}")
                        return {"status": "processed", "input": data}
                    
                    def run(self):
                        \"\"\"Main execution loop.\"\"\"
                        logger.info("%s starting...")
                        # TODO: Implement main loop
                        pass
                
                if __name__ == "__main__":
                    engine = %s()
                    engine.run()
                """, data.name, data.tech, 
                sanitizeName(data.name), data.name,
                sanitizeName(data.name), data.name, 
                sanitizeName(data.name), sanitizeName(data.name),
                data.name, data.name, sanitizeName(data.name));
        }

        private String generateJava() {
            String className = sanitizeName(data.name);
            return String.format("""
                /**
                 * %s - Generated by Fraynix Genesis Engine
                 * Technology: %s
                 */
                package genesis;
                
                import java.util.*;
                
                public class %s {
                    
                    private boolean running = false;
                    
                    public %s() {
                        System.out.println("%s initialized");
                    }
                    
                    public void start() {
                        running = true;
                        System.out.println("%s started");
                        // TODO: Implement main logic
                    }
                    
                    public void stop() {
                        running = false;
                        System.out.println("%s stopped");
                    }
                    
                    public Map<String, Object> process(Map<String, Object> input) {
                        // TODO: Implement processing
                        Map<String, Object> result = new HashMap<>();
                        result.put("status", "processed");
                        result.put("input", input);
                        return result;
                    }
                    
                    public static void main(String[] args) {
                        %s instance = new %s();
                        instance.start();
                    }
                }
                """, data.name, data.tech, className, className, 
                data.name, data.name, data.name, className, className);
        }

        private String generateSQL() {
            String tableName = data.name.toLowerCase().replace(" ", "_");
            return String.format("""
                -- %s - Generated by Fraynix Genesis Engine
                -- Technology: %s
                
                -- Create main table
                CREATE TABLE IF NOT EXISTS %s (
                    id SERIAL PRIMARY KEY,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    data JSONB,
                    status VARCHAR(50) DEFAULT 'active'
                );
                
                -- Create index for common queries
                CREATE INDEX IF NOT EXISTS idx_%s_status ON %s(status);
                CREATE INDEX IF NOT EXISTS idx_%s_created ON %s(created_at);
                
                -- Audit log table
                CREATE TABLE IF NOT EXISTS %s_audit (
                    id SERIAL PRIMARY KEY,
                    %s_id INTEGER REFERENCES %s(id),
                    action VARCHAR(50),
                    old_data JSONB,
                    new_data JSONB,
                    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                
                -- TODO: Add additional tables and constraints
                """, data.name, data.tech, tableName, tableName, tableName,
                tableName, tableName, tableName, tableName, tableName);
        }

        private String generateYAML() {
            return String.format("""
                # %s - Generated by Fraynix Genesis Engine
                # Technology: %s
                
                app:
                  name: %s
                  version: 1.0.0
                  environment: development
                
                server:
                  host: localhost
                  port: 3000
                
                database:
                  type: sqlite
                  path: ./data/app.db
                
                logging:
                  level: info
                  format: json
                
                features:
                  enabled: true
                  debug: false
                
                # TODO: Add additional configuration
                """, data.name, data.tech, data.name.toLowerCase());
        }

        private String generateJSON() {
            return String.format("""
                {
                  "name": "%s",
                  "version": "1.0.0",
                  "description": "Generated by Fraynix Genesis Engine",
                  "technology": "%s",
                  "config": {
                    "enabled": true,
                    "debug": false
                  },
                  "metadata": {
                    "generator": "Fraynix Genesis Engine",
                    "timestamp": %d
                  }
                }
                """, data.name, data.tech, System.currentTimeMillis());
        }

        private String generateMarkdown() {
            return String.format("""
                # %s
                
                > Generated by Fraynix Genesis Engine
                
                ## Overview
                
                This project was automatically generated from the intent:
                *TODO: Add project description*
                
                ## Technology Stack
                
                - **Type:** %s
                - **Technology:** %s
                
                ## Getting Started
                
                ```bash
                # Install dependencies
                npm install  # or pip install -r requirements.txt
                
                # Run the application
                npm start    # or python main.py
                ```
                
                ## Project Structure
                
                ```
                %s
                ```
                
                ## License
                
                MIT License - Generated by Fraynix
                """, data.name, data.type, data.tech, data.path);
        }

        private String generateCSS() {
            return """
                /* Generated by Fraynix Genesis Engine */
                
                :root {
                  --primary-color: #3b82f6;
                  --secondary-color: #10b981;
                  --background: #ffffff;
                  --text: #1f2937;
                  --border: #e5e7eb;
                }
                
                * {
                  margin: 0;
                  padding: 0;
                  box-sizing: border-box;
                }
                
                body {
                  font-family: system-ui, -apple-system, sans-serif;
                  background: var(--background);
                  color: var(--text);
                  line-height: 1.6;
                }
                
                .container {
                  max-width: 1200px;
                  margin: 0 auto;
                  padding: 1rem;
                }
                
                /* TODO: Add additional styles */
                """;
        }

        private String generateGeneric() {
            return String.format("""
                # %s
                # Generated by Fraynix Genesis Engine
                # Technology: %s
                # Type: %s
                
                # TODO: Implement %s
                
                """, data.name, data.tech, data.type, data.name);
        }

        private String getExtension(String path) {
            int dot = path.lastIndexOf('.');
            return dot > 0 ? path.substring(dot + 1).toLowerCase() : "";
        }

        private String sanitizeName(String name) {
            return name.replaceAll("[^a-zA-Z0-9]", "");
        }

        public boolean isComplete() {
            return complete;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getError() {
            return error;
        }
    }

    private String truncate(String s, int max) {
        return s.length() > max ? "..." + s.substring(s.length() - max + 3) : s;
    }

    public List<BuilderAgent> getAgents() {
        return agents;
    }

    public int getCompletedCount() {
        return completedCount.get();
    }

    public int getFailedCount() {
        return failedCount.get();
    }
}
