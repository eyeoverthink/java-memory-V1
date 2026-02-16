import fraymus.PhiWorld;
import fraymus.PhiNode;
import fraymus.core.UniversalInjector;
import fraymus.core.KnowledgeHarvester;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * TEST THE UNIVERSAL ARENA
 * 
 * Demonstrates the complete vision:
 * 1. UniversalInjector - Convert files to warriors
 * 2. KnowledgeHarvester - Learn concepts from the web
 * 3. Arena Simulation - Pit concepts against each other
 * 
 * EXAMPLES:
 * - Cancer vs Cure
 * - Malware vs Antivirus
 * - Hydrogen vs Anti-Hydrogen
 * - Disease vs Drug
 */
public class TestUniversalArena {
    
    public static void main(String[] args) {
        System.out.println("🌊⚡ THE UNIVERSAL ARENA TEST");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   The Arena as a Universal Simulator");
        System.out.println("   Any concept vs any concept");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        // Create test files
        createTestFiles();
        
        System.out.println("========================================");
        System.out.println("TEST 1: File Injection");
        System.out.println("========================================");
        System.out.println();
        
        // Test UniversalInjector
        File problemFile = new File("test_problem.txt");
        File solutionFile = new File("test_solution.java");
        
        PhiNode problem = UniversalInjector.inject(problemFile, "RED_TEAM");
        PhiNode solution = UniversalInjector.inject(solutionFile, "BLUE_TEAM");
        
        UniversalInjector.showStats(problem);
        UniversalInjector.showStats(solution);
        
        System.out.println("========================================");
        System.out.println("TEST 2: Knowledge Harvesting");
        System.out.println("========================================");
        System.out.println();
        
        // Test KnowledgeHarvester
        KnowledgeHarvester brain = new KnowledgeHarvester();
        
        // Learn about elements
        PhiNode hydrogen = brain.learn("Element", "Hydrogen");
        PhiNode helium = brain.learn("Element", "Helium");
        PhiNode uranium = brain.learn("Element", "Uranium");
        
        // Second call should use memory
        System.out.println("--- Testing Memory Recall ---");
        PhiNode hydrogenAgain = brain.learn("Element", "Hydrogen");
        
        System.out.println("========================================");
        System.out.println("TEST 3: Matter vs Anti-Matter");
        System.out.println("========================================");
        System.out.println();
        
        // Simulate matter annihilation
        PhiNode matter = brain.learn("Element", "Hydrogen");
        matter.setTag("team", "MATTER");
        
        PhiNode antiMatter = brain.learn("Anti-Matter", "Anti-Hydrogen");
        antiMatter.setTag("team", "ANTI_MATTER");
        antiMatter.intent = 1.0f; // Maximum aggression
        antiMatter.frequency = 500.0f; // Maximum attack
        
        System.out.println("⚛️ SIMULATING ANNIHILATION...");
        System.out.println();
        
        PhiWorld arena = new PhiWorld();
        arena.addNode(matter);
        arena.addNode(antiMatter);
        
        // Run simulation
        for (int i = 0; i < 100; i++) {
            arena.tick();
        }
        
        // Check results
        System.out.println("RESULTS:");
        System.out.println("  Matter fitness: " + matter.fitness);
        System.out.println("  Anti-Matter fitness: " + antiMatter.fitness);
        System.out.println();
        
        if (matter.fitness > antiMatter.fitness) {
            System.out.println("  ✓ MATTER SURVIVED");
        } else {
            System.out.println("  ✓ ANTI-MATTER DOMINATED");
        }
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("TEST 4: Disease vs Drug");
        System.out.println("========================================");
        System.out.println();
        
        PhiNode disease = brain.learn("Disease", "Influenza");
        disease.setTag("team", "DISEASE");
        
        PhiNode drug = brain.learn("Drug", "Penicillin");
        drug.setTag("team", "CURE");
        
        System.out.println("💊 SIMULATING TREATMENT...");
        System.out.println();
        
        PhiWorld medicalArena = new PhiWorld();
        medicalArena.addNode(disease);
        medicalArena.addNode(drug);
        
        for (int i = 0; i < 100; i++) {
            medicalArena.tick();
        }
        
        System.out.println("RESULTS:");
        System.out.println("  Disease fitness: " + disease.fitness);
        System.out.println("  Drug fitness: " + drug.fitness);
        System.out.println();
        
        if (drug.fitness > disease.fitness) {
            System.out.println("  ✓ CURE EFFECTIVE");
        } else {
            System.out.println("  ✗ DISEASE PERSISTED - Need stronger treatment");
        }
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("TEST 5: Autonomous Learning");
        System.out.println("========================================");
        System.out.println();
        
        System.out.println("Learning periodic table...");
        brain.setVerbose(false); // Quiet mode for bulk learning
        brain.learnPeriodicTable();
        
        System.out.println("Learning solar system...");
        brain.learnSolarSystem();
        
        brain.setVerbose(true);
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("RESULTS");
        System.out.println("========================================");
        System.out.println();
        System.out.println("✨ THE UNIVERSAL ARENA IS OPERATIONAL");
        System.out.println();
        System.out.println("What just happened:");
        System.out.println("1. Injected files as warriors (problem vs solution)");
        System.out.println("2. Harvested concepts from simulated web (elements, diseases, drugs)");
        System.out.println("3. Simulated matter vs anti-matter annihilation");
        System.out.println("4. Simulated disease vs drug treatment");
        System.out.println("5. Autonomously learned periodic table and solar system");
        System.out.println();
        System.out.println("The arena can now simulate:");
        System.out.println("- Cancer vs cure");
        System.out.println("- Malware vs antivirus");
        System.out.println("- Algorithm vs problem");
        System.out.println("- Element vs element");
        System.out.println("- Planet vs asteroid");
        System.out.println("- Disease vs drug");
        System.out.println("- ANY concept vs ANY concept");
        System.out.println();
        System.out.println("Knowledge is permanent.");
        System.out.println("Never ask twice.");
        System.out.println("Own concepts forever.");
        System.out.println();
        System.out.println("🌊⚡ The Universal Arena: Where data becomes destiny.");
        System.out.println();
        System.out.println("========================================");
        
        // Cleanup
        cleanupTestFiles();
    }
    
    private static void createTestFiles() {
        try {
            // Create problem file (high entropy, chaotic)
            FileWriter problemWriter = new FileWriter("test_problem.txt");
            problemWriter.write("xK9#mP2@vL8!qR4$wN7%tJ3&hF6*dS1^gB5(cZ0)aY\n");
            problemWriter.write("Random chaotic data with high entropy\n");
            problemWriter.write("!@#$%^&*()_+-=[]{}|;':\",./<>?\n");
            problemWriter.close();
            
            // Create solution file (structured code, low entropy)
            FileWriter solutionWriter = new FileWriter("test_solution.java");
            solutionWriter.write("public class Solution {\n");
            solutionWriter.write("    public static void solve() {\n");
            solutionWriter.write("        System.out.println(\"Problem solved\");\n");
            solutionWriter.write("    }\n");
            solutionWriter.write("}\n");
            solutionWriter.close();
            
        } catch (IOException e) {
            System.err.println("Failed to create test files: " + e.getMessage());
        }
    }
    
    private static void cleanupTestFiles() {
        new File("test_problem.txt").delete();
        new File("test_solution.java").delete();
    }
}
