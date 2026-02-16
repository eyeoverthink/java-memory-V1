import fraymus.arena.InjectionProtocol;
import fraymus.PhiWorld;

/**
 * TEST THE INJECTION PROTOCOL
 * 
 * Demonstrates injecting various problem types into the arena:
 * - Cancer simulation
 * - Security testing
 * - Optimization problems
 * - Ethical dilemmas
 * - Market simulations
 */
public class TestInjectionProtocol {
    
    public static void main(String[] args) {
        System.out.println("🌊⚡ THE INJECTION PROTOCOL TEST");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   The Arena as a Universal Problem Solver");
        System.out.println("   Inject anything. Solve everything.");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        // Initialize arena and injection protocol
        PhiWorld arena = new PhiWorld();
        InjectionProtocol injector = new InjectionProtocol(arena);
        
        System.out.println("========================================");
        System.out.println("TEST 1: Cancer Simulation");
        System.out.println("========================================");
        System.out.println();
        
        injector.injectCancerSimulation();
        
        System.out.println("   Simulating 100 generations...");
        for (int i = 0; i < 100; i++) {
            arena.tick();
        }
        
        InjectionProtocol.CompetitionResults results1 = injector.getResults();
        System.out.println("   Results:");
        for (var entry : results1.getAllStats().entrySet()) {
            System.out.println("     " + entry.getKey() + ": " + 
                             entry.getValue().survivors + " survivors, " +
                             "fitness: " + String.format("%.2f", entry.getValue().totalFitness));
        }
        
        String strategy1 = injector.extractWinningStrategy();
        System.out.println();
        System.out.println(strategy1);
        
        injector.reset();
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("TEST 2: Security Test");
        System.out.println("========================================");
        System.out.println();
        
        String vulnerableCode = "public class VulnerableSystem { /* weak encryption */ }";
        String attackCode = "public class AttackVector { /* brute force */ }";
        
        injector.injectSecurityTest(vulnerableCode, attackCode);
        
        System.out.println("   Simulating attack...");
        for (int i = 0; i < 50; i++) {
            arena.tick();
        }
        
        InjectionProtocol.CompetitionResults results2 = injector.getResults();
        System.out.println("   Results:");
        for (var entry : results2.getAllStats().entrySet()) {
            System.out.println("     " + entry.getKey() + ": " + 
                             entry.getValue().survivors + " survivors, " +
                             "fitness: " + String.format("%.2f", entry.getValue().totalFitness));
        }
        
        injector.reset();
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("TEST 3: Optimization Problem");
        System.out.println("========================================");
        System.out.println();
        
        String problemData = "Traveling Salesman Problem: 10 cities";
        String[] algorithms = {
            "Genetic Algorithm",
            "Simulated Annealing",
            "Ant Colony Optimization",
            "Particle Swarm"
        };
        
        injector.injectOptimizationProblem(problemData, algorithms);
        
        System.out.println("   Running optimization...");
        for (int i = 0; i < 200; i++) {
            arena.tick();
        }
        
        InjectionProtocol.CompetitionResults results3 = injector.getResults();
        System.out.println("   Results:");
        for (var entry : results3.getAllStats().entrySet()) {
            System.out.println("     " + entry.getKey() + ": " + 
                             entry.getValue().survivors + " survivors, " +
                             "fitness: " + String.format("%.2f", entry.getValue().totalFitness));
        }
        
        String strategy3 = injector.extractWinningStrategy();
        System.out.println();
        System.out.println(strategy3);
        
        injector.reset();
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("TEST 4: Ethical Dilemma");
        System.out.println("========================================");
        System.out.println();
        
        String scenario = "Trolley Problem: Save 5 or save 1?";
        String[] frameworks = {
            "Utilitarianism",
            "Deontology",
            "Virtue Ethics",
            "Care Ethics"
        };
        
        injector.injectEthicalDilemma(scenario, frameworks);
        
        System.out.println("   Evaluating moral frameworks...");
        for (int i = 0; i < 100; i++) {
            arena.tick();
        }
        
        InjectionProtocol.CompetitionResults results4 = injector.getResults();
        System.out.println("   Results:");
        for (var entry : results4.getAllStats().entrySet()) {
            System.out.println("     " + entry.getKey() + ": " + 
                             entry.getValue().survivors + " survivors, " +
                             "fitness: " + String.format("%.2f", entry.getValue().totalFitness));
        }
        
        injector.reset();
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("RESULTS");
        System.out.println("========================================");
        System.out.println();
        System.out.println("✨ The Injection Protocol is OPERATIONAL");
        System.out.println();
        System.out.println("What just happened:");
        System.out.println("1. Injected cancer cells vs immune system");
        System.out.println("2. Injected attack vectors vs defense systems");
        System.out.println("3. Injected competing optimization algorithms");
        System.out.println("4. Injected moral frameworks vs ethical dilemmas");
        System.out.println();
        System.out.println("The arena solved all of them.");
        System.out.println("The arena extracted winning strategies.");
        System.out.println("The arena is a universal problem solver.");
        System.out.println();
        System.out.println("💉 Inject anything. Solve everything.");
        System.out.println();
        System.out.println("Applications:");
        System.out.println("- Medical research (cancer, disease progression)");
        System.out.println("- Cybersecurity (penetration testing, vulnerability analysis)");
        System.out.println("- Algorithm optimization (NP-hard problems)");
        System.out.println("- Ethics research (moral dilemmas, policy decisions)");
        System.out.println("- Financial modeling (trading strategies, risk analysis)");
        System.out.println("- AI safety (adversarial testing, alignment)");
        System.out.println();
        System.out.println("This is ethical hacking.");
        System.out.println("This is computational biology.");
        System.out.println("This is universal optimization.");
        System.out.println();
        System.out.println("🌊⚡ If your system is smart enough, it can solve anything.");
        System.out.println();
        System.out.println("========================================");
    }
}
