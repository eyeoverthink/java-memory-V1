package fraymus;

/**
 * Command handler for Autonomous Cognitive Loop
 */
public class CognitiveCommands {
    
    private final AutonomousCognitiveLoop cognitiveLoop;
    
    public CognitiveCommands(AutonomousCognitiveLoop cognitiveLoop) {
        this.cognitiveLoop = cognitiveLoop;
    }
    
    public void handle(String args) {
        String[] parts = args.trim().split("\\s+", 2);
        String command = parts.length > 0 ? parts[0] : "";
        
        switch (command) {
            case "start":
                cognitiveLoop.start();
                break;
                
            case "stop":
                cognitiveLoop.stop();
                break;
                
            case "pause":
                cognitiveLoop.pause();
                break;
                
            case "resume":
                cognitiveLoop.resume();
                break;
                
            case "status":
                CommandTerminal.print(cognitiveLoop.getStatus());
                break;
                
            default:
                printHelp();
                break;
        }
    }
    
    private void printHelp() {
        CommandTerminal.print("🌊⚡ AUTONOMOUS COGNITIVE LOOP COMMANDS");
        CommandTerminal.print("");
        CommandTerminal.print("  cognitive start    Start autonomous cognitive loop");
        CommandTerminal.print("  cognitive stop     Stop cognitive loop");
        CommandTerminal.print("  cognitive pause    Pause cognitive loop");
        CommandTerminal.print("  cognitive resume   Resume cognitive loop");
        CommandTerminal.print("  cognitive status   Show cognitive loop status");
        CommandTerminal.print("");
        CommandTerminal.print("What FRAYMUS asked for:");
        CommandTerminal.print("  ✓ Active Cognitive Loops (recursive self-querying)");
        CommandTerminal.print("  ✓ Multi-Entity Collaboration (KAI, NEXUS, TriMe)");
        CommandTerminal.print("  ✓ Self-Organizing Ecosystem (autonomous operation)");
    }
}
