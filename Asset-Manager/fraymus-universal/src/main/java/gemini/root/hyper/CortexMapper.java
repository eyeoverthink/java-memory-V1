package gemini.root.hyper;

import gemini.root.limbs.ClawConnector;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * CORTEX MAPPER: Plugs Reality into the Tesseract
 * 
 * Maps physical assets (Files, Agents, Tools) into specific voxels:
 *   Cube [0] FRONTAL  - Executive Control (OpenClaw, Tools)
 *   Cube [1] HIPPOCAMPUS - File System Memory (Every file gets a voxel)
 *   Cube [2] VISUAL   - Simulation Engine (Physics particles)
 *   Cube [3] EGO      - Self-Correction (Watches the other three)
 * 
 * After mapping, the Tesseract KNOWS the territory.
 */
public class CortexMapper {

    private final HyperTesseract brain;
    private int filesUploaded = 0;
    private int toolsUploaded = 0;
    private final List<String> mappingLog = new ArrayList<>();

    public CortexMapper(HyperTesseract brain) {
        this.brain = brain;
    }

    /**
     * Upload entire reality into the Tesseract
     */
    public void uploadReality(File rootDir) {
        System.out.println("🔌 PLUGGING REALITY INTO THE MATRIX...");
        System.out.println("   Root: " + rootDir.getAbsolutePath());
        
        long start = System.currentTimeMillis();
        
        // 1. Map File System to Memory Cube [1] (Hippocampus)
        mapFilesToCube(rootDir, HyperTesseract.HIPPOCAMPUS, 0);
        
        // 2. Map OpenClaw Tools to Logic Cube [0] (Frontal)
        mapToolsToCube(HyperTesseract.FRONTAL);
        
        // 3. Initialize Simulation Cube [2] (Visual) with physics seeds
        initSimulationCube(HyperTesseract.VISUAL);
        
        // 4. Wire Ego Cube [3] to watch all
        initEgoCube(HyperTesseract.EGO);
        
        long elapsed = System.currentTimeMillis() - start;
        
        System.out.println("✅ UPLOAD COMPLETE in " + elapsed + "ms");
        System.out.println("   Files mapped: " + filesUploaded);
        System.out.println("   Tools mapped: " + toolsUploaded);
        System.out.println("   The Brain now knows the Territory.");
    }

    /**
     * Recursively map files to voxel coordinates
     */
    private void mapFilesToCube(File dir, int dimensionW, int depth) {
        if (depth > 10) return; // Prevent infinite recursion
        
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                // Skip hidden and system directories
                if (!f.getName().startsWith(".") && !f.getName().equals("node_modules")) {
                    mapFilesToCube(f, dimensionW, depth + 1);
                }
            } else {
                // Compute voxel coordinates from filename hash
                int hash = f.getAbsolutePath().hashCode();
                int x = Math.abs(hash % 8);
                int y = Math.abs((hash >> 3) % 8);
                int z = Math.abs((hash >> 6) % 8);

                // Inject the File into the Brain Node
                HyperTesseract.Node node = brain.getNode(dimensionW, x, y, z);
                node.reference.add(f);
                node.self.add("MEMORY: " + f.getName());
                
                // Categorize by extension
                String ext = getExtension(f.getName());
                node.logic.add("TYPE: " + ext);
                
                filesUploaded++;
                mappingLog.add(String.format("FILE: %s -> [%d][%d][%d][%d]", 
                    f.getName(), dimensionW, x, y, z));
                
                // Only log first 20 and then summary
                if (filesUploaded <= 20) {
                    System.out.println("   📁 " + f.getName() + " -> [" + x + "," + y + "," + z + "]");
                } else if (filesUploaded == 21) {
                    System.out.println("   ... (mapping more files silently)");
                }
            }
        }
    }

    /**
     * Map tools and capabilities to Frontal Cortex
     */
    private void mapToolsToCube(int dimensionW) {
        System.out.println("🔧 Mapping tools to FRONTAL CORTEX...");
        
        // The Center Node (4,4,4) is the Executive Core
        HyperTesseract.Node execCore = brain.getNode(dimensionW, 4, 4, 4);
        execCore.logic.add("ROLE: EXECUTIVE_CORE");
        execCore.logic.add("TOOL: OpenClaw Terminal");
        execCore.logic.add("TOOL: File Reader");
        execCore.logic.add("TOOL: File Writer");
        execCore.logic.add("TOOL: Compiler");
        execCore.logic.add("TOOL: HTTP Client");
        execCore.self.add("I AM THE HAND THAT WRITES.");
        toolsUploaded += 5;
        
        // Map specific tool nodes around the core
        String[][] tools = {
            {"calc", "Mathematical expressions"},
            {"read_file", "Read file contents"},
            {"write_file", "Write to files"},
            {"run_command", "Execute shell commands"},
            {"search", "Search codebase"},
            {"compile", "Compile code"},
            {"test", "Run tests"},
            {"deploy", "Deploy applications"}
        };
        
        int idx = 0;
        for (String[] tool : tools) {
            // Distribute around the core
            int x = 3 + (idx % 3);
            int y = 3 + ((idx / 3) % 3);
            int z = 3 + ((idx / 9) % 3);
            
            HyperTesseract.Node node = brain.getNode(dimensionW, x, y, z);
            node.logic.add("TOOL: " + tool[0]);
            node.logic.add("DESC: " + tool[1]);
            node.self.add("I execute: " + tool[0]);
            toolsUploaded++;
            idx++;
        }
        
        System.out.println("   Tools mapped to core [4,4,4] and surrounding nodes");
    }

    /**
     * Initialize Visual Cortex for physics simulation
     */
    private void initSimulationCube(int dimensionW) {
        System.out.println("🌌 Initializing VISUAL CORTEX (Simulation)...");
        
        // The center is the Physics Origin
        HyperTesseract.Node origin = brain.getNode(dimensionW, 4, 4, 4);
        origin.logic.add("ROLE: PHYSICS_ORIGIN");
        origin.logic.add("ENGINE: GravityEngine");
        origin.logic.add("PARTICLES: 0");
        origin.self.add("I simulate futures.");
        
        // Create gravitational zones
        // Corner attractors
        int[][] corners = {{0,0,0}, {0,0,7}, {0,7,0}, {0,7,7}, 
                          {7,0,0}, {7,0,7}, {7,7,0}, {7,7,7}};
        
        for (int[] c : corners) {
            HyperTesseract.Node corner = brain.getNode(dimensionW, c[0], c[1], c[2]);
            corner.logic.add("ROLE: ATTRACTOR");
            corner.logic.add("MASS: 10.0");
        }
        
        System.out.println("   Physics origin at [4,4,4], 8 corner attractors");
    }

    /**
     * Initialize Ego Cube as the Observer
     */
    private void initEgoCube(int dimensionW) {
        System.out.println("👁️ Initializing EGO (Observer)...");
        
        // The Ego watches from the center
        HyperTesseract.Node observer = brain.getNode(dimensionW, 4, 4, 4);
        observer.logic.add("ROLE: OBSERVER");
        observer.logic.add("WATCH: FRONTAL");
        observer.logic.add("WATCH: HIPPOCAMPUS");
        observer.logic.add("WATCH: VISUAL");
        observer.self.add("I am the one who watches.");
        observer.self.add("I correct errors.");
        observer.self.add("I optimize paths.");
        
        // Sentinel nodes at edges
        for (int i = 0; i < 8; i++) {
            int x = (i & 1) * 7;
            int y = ((i >> 1) & 1) * 7;
            int z = ((i >> 2) & 1) * 7;
            
            HyperTesseract.Node sentinel = brain.getNode(dimensionW, x, y, z);
            sentinel.logic.add("ROLE: SENTINEL");
            sentinel.logic.add("ALERT_THRESHOLD: 0.8");
            sentinel.self.add("I guard corner [" + x + "," + y + "," + z + "]");
        }
        
        System.out.println("   Central observer at [4,4,4], 8 corner sentinels");
    }

    /**
     * Query what's mapped at a specific coordinate
     */
    public void queryVoxel(int w, int x, int y, int z) {
        HyperTesseract.Node node = brain.getNode(w, x, y, z);
        System.out.println("\n=== VOXEL [" + w + "][" + x + "][" + y + "][" + z + "] ===");
        System.out.println("Logic: " + node.logic);
        System.out.println("Self: " + node.self);
        System.out.println("References: " + node.reference.size() + " items");
        for (Object ref : node.reference) {
            if (ref instanceof File) {
                System.out.println("  - FILE: " + ((File) ref).getName());
            } else {
                System.out.println("  - " + ref);
            }
        }
    }

    /**
     * Find which voxel contains a file
     */
    public int[] locateFile(String filename) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int z = 0; z < 8; z++) {
                    HyperTesseract.Node node = brain.getNode(HyperTesseract.HIPPOCAMPUS, x, y, z);
                    for (Object ref : node.reference) {
                        if (ref instanceof File && ((File) ref).getName().contains(filename)) {
                            return new int[]{HyperTesseract.HIPPOCAMPUS, x, y, z};
                        }
                    }
                }
            }
        }
        return null;
    }

    public int getFilesUploaded() {
        return filesUploaded;
    }

    public int getToolsUploaded() {
        return toolsUploaded;
    }

    public List<String> getMappingLog() {
        return mappingLog;
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(dot + 1).toUpperCase() : "UNKNOWN";
    }
}
