package fraymus.nano;

import fraymus.core.GravityEngine;
import java.io.File;
import java.util.Random;

/**
 * FILE SYSTEM GALAXY - The Galaxy Mapper
 * 
 * Turns your Project Folder into a Physics Simulation.
 * Walks your directory, creates a NanoAgent for every file,
 * and places them in 3D space based on file type and size.
 */
public class FileSystemGalaxy {

    private final GravityEngine universe;
    private final Random rng = new Random();
    private int agentCount = 0;

    public FileSystemGalaxy(GravityEngine universe) {
        this.universe = universe;
    }

    public void ingest(String rootPath) {
        System.out.println("🌌 MAPPING UNIVERSE: " + rootPath);
        File root = new File(rootPath);
        mapRecursively(root, 0, 0, 0);
        System.out.println("✨ GALAXY MAPPED: " + agentCount + " nano-agents spawned");
    }

    private void mapRecursively(File dir, int cx, int cy, int cz) {
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

            // Random offset for "Orbit"
            int x = cx + rng.nextInt(20) - 10;
            int y = cy + rng.nextInt(20) - 10;
            int z = cz + rng.nextInt(20) - 10;

            if (f.isDirectory()) {
                // Directories are Centers of Gravity
                mapRecursively(f, x, y, z);
            } else if (f.getName().endsWith(".java")) {
                // Java Files become Living Nano-Agents
                NanoAgent agent = new NanoAgent(f, x, y, z);
                
                // Start the agent's life thread
                new Thread(agent).start();
                
                // Note: NanoAgent auto-registers via SpatialNode constructor
                agentCount++;
                System.out.println("   ⭐ Star Born: " + f.getName() + " at (" + x + "," + y + "," + z + ")");
            }
        }
    }
    
    public int getAgentCount() {
        return agentCount;
    }
}
