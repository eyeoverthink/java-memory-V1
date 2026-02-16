package fraymus.agent;

import fraymus.brain.OllamaSpine;
import fraymus.limbs.ClawConnector;
import fraymus.core.AuditLog;
import fraymus.memory.BlackBox;
import java.io.File;

/**
 * AGENT BOOT - Demonstration of Offline AGI
 * 
 * Shows the complete Trinity in action:
 * - Brain (Ollama): Local LLM for reasoning
 * - Hands (OpenClaw): Autonomous execution
 * - Soul (Fraynix): Physics-driven orchestration
 * 
 * This is Private, Offline AGI:
 * - Private: No data leaves your machine
 * - Offline: Works without internet (local models)
 * - Agentic: It doesn't just talk; it does
 */
public class AgentBoot {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🧬 FRAYNIX OFFLINE AGI - THE TRINITY                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Private: No data leaves your machine");
        System.out.println("Offline: Works without internet (local models)");
        System.out.println("Agentic: It doesn't just talk; it does");
        System.out.println();
        
        // Initialize components
        AuditLog auditLog = new AuditLog("audit");
        auditLog.start();
        
        BlackBox memory = new BlackBox(auditLog);
        OllamaSpine brain = new OllamaSpine("llama3");
        ClawConnector hands = new ClawConnector();
        
        // Check if Ollama is available
        System.out.println("🧠 Checking Ollama availability...");
        if (!brain.isAvailable()) {
            System.err.println("❌ Ollama not running!");
            System.err.println("   Start it with: ollama serve");
            System.err.println();
            auditLog.stop();
            return;
        }
        System.out.println("   ✓ Ollama is online (model: " + brain.getModel() + ")");
        System.out.println();
        
        // Parse command line arguments
        if (args.length == 0) {
            runDemo(brain, hands, memory, auditLog);
        } else {
            String command = args[0].toLowerCase();
            
            switch (command) {
                case "analyze":
                    if (args.length < 2) {
                        System.err.println("Usage: analyze <file>");
                        break;
                    }
                    analyzeFile(args[1], brain, hands, memory, auditLog);
                    break;
                    
                case "swarm":
                    if (args.length < 2) {
                        System.err.println("Usage: swarm <directory> [pattern]");
                        break;
                    }
                    String pattern = args.length > 2 ? args[2] : ".*\\.java";
                    deploySwarm(args[1], pattern, brain, hands, memory, auditLog);
                    break;
                    
                case "demo":
                    runDemo(brain, hands, memory, auditLog);
                    break;
                    
                default:
                    System.err.println("Unknown command: " + command);
                    System.err.println("Available commands: analyze, swarm, demo");
            }
        }
        
        auditLog.stop();
    }

    /**
     * Analyze single file
     */
    private static void analyzeFile(String filePath, OllamaSpine brain, 
                                   ClawConnector hands, BlackBox memory, AuditLog auditLog) {
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.err.println("❌ File not found: " + filePath);
            return;
        }
        
        FraynixAgent agent = new FraynixAgent(file, brain, hands, memory, auditLog);
        agent.run();
    }

    /**
     * Deploy swarm to directory
     */
    private static void deploySwarm(String dirPath, String pattern, OllamaSpine brain,
                                   ClawConnector hands, BlackBox memory, AuditLog auditLog) {
        File directory = new File(dirPath);
        
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("❌ Directory not found: " + dirPath);
            return;
        }
        
        AgentSwarm swarm = new AgentSwarm(brain, hands, memory, auditLog, 4);
        swarm.deployToDirectory(directory, pattern);
        swarm.waitForCompletion(10);
        swarm.shutdown();
    }

    /**
     * Run demonstration
     */
    private static void runDemo(OllamaSpine brain, ClawConnector hands, 
                               BlackBox memory, AuditLog auditLog) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🎯 DEMO MODE                                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("This demo shows the Trinity in action:");
        System.out.println();
        
        // Demo 1: Brain thinking
        System.out.println("═══ DEMO 1: THE BRAIN (Ollama) ═══");
        System.out.println();
        String thought = brain.think("What is the golden ratio and why is it important in nature?");
        System.out.println("Brain's response: " + thought.substring(0, Math.min(200, thought.length())) + "...");
        System.out.println();
        
        // Demo 2: Code analysis
        System.out.println("═══ DEMO 2: CODE ANALYSIS ═══");
        System.out.println();
        String sampleCode = "public class Test { public static void main(String[] args) { System.out.println(\"Hello\"); } }";
        String analysis = brain.analyzeCode(sampleCode);
        System.out.println("Analysis: " + analysis);
        System.out.println();
        
        // Demo 3: Memory recall
        System.out.println("═══ DEMO 3: MEMORY SYSTEM ═══");
        System.out.println();
        memory.remember("Demo test", "Successfully demonstrated offline AGI", true);
        String recall = memory.recall("Demo");
        System.out.println("Memory recall: " + recall);
        System.out.println();
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         ✅ DEMO COMPLETE                                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("To analyze a file:");
        System.out.println("  ./gradlew runAgentBoot -Pargs=\"analyze,path/to/file.java\"");
        System.out.println();
        System.out.println("To deploy swarm:");
        System.out.println("  ./gradlew runAgentBoot -Pargs=\"swarm,src/main/java,.*\\.java\"");
        System.out.println();
    }
}
