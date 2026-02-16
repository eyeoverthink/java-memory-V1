/**
 * ExportCommands.java - REPL Export Commands
 * 
 * Registers export commands in the REPL:
 * - :export assembly - Export assembly/bytecode
 * - :export organism - Export organism data
 * - :export activity - Export activity feed
 * - :export snapshot - Export runtime snapshot
 * - :export all - Export everything
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import java.util.*;

/**
 * Export command registration for REPL.
 */
public class ExportCommands {
    
    private static String lastAssembly = "";
    private static String lastActivity = "";
    
    /**
     * Register all export commands.
     */
    public static void registerAll(ReplCommandRegistry registry) {
        
        // :EXPORT command - Universal export
        registry.register(":export",
            args -> {
                if (args.isEmpty()) {
                    return "Usage: :export <type>\n" +
                           "Types:\n" +
                           "  assembly  - Export assembly/bytecode output\n" +
                           "  organism  - Export organism monitoring data\n" +
                           "  activity  - Export activity feed logs\n" +
                           "  snapshot  - Export runtime snapshot\n" +
                           "  all       - Export complete system state\n" +
                           "\nExample: :export assembly";
                }
                
                String type = args.get(0).toLowerCase();
                
                switch (type) {
                    case "assembly":
                    case "asm":
                        return ExportManager.exportAssembly(lastAssembly);
                    
                    case "organism":
                    case "org":
                        return ExportManager.exportOrganism(OrganismCommands.getOrganism());
                    
                    case "activity":
                    case "act":
                        return ExportManager.exportActivityFeed(lastActivity);
                    
                    case "snapshot":
                    case "snap":
                        RuntimeInspector.ExecutionSnapshot snapshot = RuntimeInspector.captureSnapshot();
                        return ExportManager.exportSnapshot(snapshot);
                    
                    case "all":
                    case "full":
                        RuntimeInspector.ExecutionSnapshot fullSnapshot = RuntimeInspector.captureSnapshot();
                        return ExportManager.exportFullState(
                            lastAssembly,
                            OrganismCommands.getOrganism(),
                            lastActivity,
                            fullSnapshot
                        );
                    
                    default:
                        return "Unknown export type: " + type + "\n" +
                               "Use :export for help";
                }
            },
            "Export system data to files",
            ":export <type>");
    }
    
    /**
     * Update cached assembly output.
     */
    public static void updateAssembly(String assembly) {
        lastAssembly = assembly;
    }
    
    /**
     * Update cached activity feed.
     */
    public static void updateActivity(String activity) {
        lastActivity = activity;
    }
}
