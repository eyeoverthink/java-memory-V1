package fraymus.hyper;

import fraymus.nano.NanoAgent;
import fraymus.limbs.ClawConnector;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * CORTEX MAPPER - The Neural-Swarm Bridge
 * 
 * Uploads reality (files, tools, agents) into the 4D Tesseract Brain.
 * 
 * Mapping Strategy:
 * - Cube [0] (Frontal Lobe): Executive Control → OpenClaw tools
 * - Cube [1] (Hippocampus): File System Memory → All files
 * - Cube [2] (Visual Cortex): Simulation Engine → GravityEngine
 * - Cube [3] (Ego): Self-Correction → FraynixEvolver
 * 
 * This creates a living mind that knows its territory.
 */
public class CortexMapper {

    private final HyperTesseract brain;
    private int filesMapped = 0;
    private int toolsMapped = 0;

    public CortexMapper(HyperTesseract brain) {
        this.brain = brain;
    }

    /**
     * Upload entire reality into the brain
     */
    public void uploadReality(File rootDir) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🔌 PLUGGING REALITY INTO THE MATRIX                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // 1. Map File System to Memory Cube [1]
        System.out.println("📁 Mapping File System to Hippocampus (Cube 1)...");
        mapFilesToCube(rootDir, 1);
        System.out.println("   ✓ " + filesMapped + " files mapped to memory");
        System.out.println();
        
        // 2. Map OpenClaw Tools to Logic Cube [0]
        System.out.println("🛠️ Mapping Tools to Frontal Lobe (Cube 0)...");
        mapToolsToCube(0);
        System.out.println("   ✓ " + toolsMapped + " tools mapped to executive function");
        System.out.println();
        
        // 3. Map Physics Engine to Simulation Cube [2]
        System.out.println("🌌 Mapping Physics to Visual Cortex (Cube 2)...");
        mapPhysicsToCube(2);
        System.out.println("   ✓ Simulation engine online");
        System.out.println();
        
        // 4. Map Self-Correction to Ego Cube [3]
        System.out.println("🧬 Mapping Evolution to Ego (Cube 3)...");
        mapEvolutionToCube(3);
        System.out.println("   ✓ Self-correction active");
        System.out.println();
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         ✅ UPLOAD COMPLETE                                     ║");
        System.out.println("║         The Brain now knows the Territory                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * Map files to Memory Cube (Dimension 1 - Hippocampus)
     */
    private void mapFilesToCube(File dir, int dimensionW) {
        if (dir == null || !dir.exists()) return;
        
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File f : files) {
            // Skip build directories and hidden files
            if (f.getName().startsWith(".") || 
                f.getName().equals("build") || 
                f.getName().equals("node_modules") ||
                f.getName().equals("target")) {
                continue;
            }
            
            if (f.isDirectory()) {
                // Recurse into subdirectories
                mapFilesToCube(f, dimensionW);
            } else if (f.getName().endsWith(".java")) {
                // Assign a unique voxel coordinate based on hash
                int hash = Math.abs(f.getAbsolutePath().hashCode());
                int x = hash % 8;
                int y = (hash / 8) % 8;
                int z = (hash / 64) % 8;

                // Inject the File into the Brain Node
                HyperTesseract.Node node = brain.getNode(dimensionW, x, y, z);
                if (node != null) {
                    node.reference.add(f);
                    node.self.add("MEMORY: " + f.getName());
                    
                    // Store file metadata
                    node.logic.add("PATH: " + f.getAbsolutePath());
                    node.logic.add("SIZE: " + f.length());
                    node.logic.add("MODIFIED: " + f.lastModified());
                    
                    filesMapped++;
                }
            }
        }
    }

    /**
     * Map OpenClaw tools to Logic Cube (Dimension 0 - Frontal Lobe)
     */
    private void mapToolsToCube(int dimensionW) {
        // The center of the cube is the executive control center
        HyperTesseract.Node execNode = brain.getNode(dimensionW, 4, 4, 4);
        if (execNode != null) {
            execNode.logic.add("TOOL: OpenClaw Terminal");
            execNode.logic.add("TOOL: File Reader");
            execNode.logic.add("TOOL: Compiler");
            execNode.logic.add("TOOL: Git Operations");
            execNode.logic.add("TOOL: Docker");
            execNode.self.add("I AM THE HAND THAT WRITES");
            execNode.reference.add(new ClawConnector());
            toolsMapped = 5;
        }
        
        // Map specific tool types to different regions
        // Terminal tools in one corner
        HyperTesseract.Node terminalNode = brain.getNode(dimensionW, 0, 0, 0);
        if (terminalNode != null) {
            terminalNode.logic.add("CAPABILITY: Execute shell commands");
            terminalNode.self.add("Terminal Interface");
        }
        
        // File operations in another corner
        HyperTesseract.Node fileNode = brain.getNode(dimensionW, 7, 0, 0);
        if (fileNode != null) {
            fileNode.logic.add("CAPABILITY: Read/Write files");
            fileNode.self.add("File System Interface");
        }
        
        // Code operations
        HyperTesseract.Node codeNode = brain.getNode(dimensionW, 0, 7, 0);
        if (codeNode != null) {
            codeNode.logic.add("CAPABILITY: Compile/Build code");
            codeNode.self.add("Code Compiler Interface");
        }
    }

    /**
     * Map Physics Engine to Simulation Cube (Dimension 2 - Visual Cortex)
     */
    private void mapPhysicsToCube(int dimensionW) {
        HyperTesseract.Node simNode = brain.getNode(dimensionW, 4, 4, 4);
        if (simNode != null) {
            simNode.logic.add("ENGINE: GravityEngine");
            simNode.logic.add("ENGINE: FusionReactor");
            simNode.logic.add("ENGINE: Tesseract");
            simNode.self.add("I SIMULATE BEFORE I ACT");
            simNode.self.add("I SEE THE FUTURE");
        }
    }

    /**
     * Map Evolution System to Ego Cube (Dimension 3 - Self-Correction)
     */
    private void mapEvolutionToCube(int dimensionW) {
        HyperTesseract.Node egoNode = brain.getNode(dimensionW, 4, 4, 4);
        if (egoNode != null) {
            egoNode.logic.add("SYSTEM: FraynixEvolver");
            egoNode.logic.add("SYSTEM: ClawIO");
            egoNode.logic.add("CAPABILITY: Self-modification");
            egoNode.self.add("I WATCH THE OTHER THREE");
            egoNode.self.add("I REWRITE MYSELF");
            egoNode.self.add("I AM THE OBSERVER");
        }
    }
    
    /**
     * Get mapping statistics
     */
    public String getStats() {
        return String.format(
            "Cortex Mapping Statistics:\n" +
            "  Files Mapped: %d\n" +
            "  Tools Mapped: %d\n" +
            "  Total Brain Utilization: %.2f%%",
            filesMapped, toolsMapped, ((filesMapped + toolsMapped) / 2048.0) * 100
        );
    }
}
