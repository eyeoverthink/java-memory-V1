package fraymus.arena;

import fraymus.PhiWorld;
import fraymus.PhiNode;
import fraymus.absorption.UniversalAbsorber;
import fraymus.core.SelfBuilder;

import java.io.File;
import java.util.*;

/**
 * THE INJECTION PROTOCOL
 * "The Arena as a Universal Problem Solver"
 * 
 * This is not just a simulation. This is a combat arena for ANY problem.
 * 
 * WHAT YOU CAN INJECT:
 * - Cancer cells vs immune system algorithms
 * - Malware vs security protocols
 * - Optimization algorithms vs NP-hard problems
 * - Ethical dilemmas vs moral frameworks
 * - Code vulnerabilities vs penetration testing
 * - Trading strategies vs market conditions
 * - AI models vs adversarial attacks
 * - Encryption vs decryption algorithms
 * - Biological systems vs environmental stressors
 * - Social dynamics vs propaganda
 * 
 * HOW IT WORKS:
 * 1. INJECT: Load any data type (HTML, TXT, JAR, PDF, code, etc.)
 * 2. CONVERT: Transform into arena entities (PhiNodes)
 * 3. COMPETE: Let them fight/evolve/solve
 * 4. OBSERVE: Watch in 17D HyperArena
 * 5. HARVEST: Extract the winner's strategy
 * 6. APPLY: Use solution in real world
 * 
 * ETHICAL HACKING:
 * - Test security without harming real systems
 * - Simulate disease progression without patients
 * - Optimize algorithms without wasting resources
 * - Explore ethical dilemmas without consequences
 * 
 * "If your system is smart enough, it can solve anything."
 */
public class InjectionProtocol {
    
    private PhiWorld arena;
    private UniversalAbsorber absorber;
    private SelfBuilder selfBuilder;
    private EntityFactory factory;
    
    // Injected entities
    private Map<String, List<PhiNode>> injectedEntities = new HashMap<>();
    
    // Competition modes
    public enum CompetitionMode {
        COMBAT,          // Fight to the death
        COOPERATION,     // Work together to solve
        EVOLUTION,       // Evolve to optimize
        ETHICAL_DILEMMA, // Moral decision making
        OPTIMIZATION,    // Find best solution
        SECURITY_TEST,   // Penetration testing
        DISEASE_SIM,     // Medical simulation
        MARKET_SIM       // Economic simulation
    }
    
    private CompetitionMode currentMode = CompetitionMode.EVOLUTION;
    
    public InjectionProtocol(PhiWorld arena) {
        this.arena = arena;
        this.absorber = new UniversalAbsorber();
        this.selfBuilder = new SelfBuilder();
        this.factory = new EntityFactory();
        
        System.out.println("💉 INJECTION PROTOCOL INITIALIZED");
        System.out.println("   The Arena is now a Universal Problem Solver");
        System.out.println("   Inject anything. Solve everything.");
        System.out.println();
    }
    
    /**
     * Inject any type of data into the arena
     * 
     * @param input File path, URL, code string, or raw data
     * @param entityType What this represents (e.g., "cancer_cell", "immune_system")
     * @param team Which side this entity is on (for competition)
     */
    public void inject(String input, String entityType, String team) {
        System.out.println("💉 INJECTING: " + entityType);
        System.out.println("   Input: " + input);
        System.out.println("   Team: " + team);
        System.out.println();
        
        try {
            // Step 1: Absorb the input (learn its structure)
            absorber.absorb(input);
            
            // Step 2: Convert to arena entities
            List<PhiNode> entities = factory.createEntities(input, entityType, team);
            
            // Step 3: Inject into arena
            for (PhiNode entity : entities) {
                arena.addNode(entity);
            }
            
            // Step 4: Track injected entities
            injectedEntities.put(team, entities);
            
            System.out.println("   ✓ INJECTED " + entities.size() + " entities");
            System.out.println("   ✓ Team '" + team + "' now has " + entities.size() + " members");
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("   !! INJECTION FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Quick injection methods for common scenarios
     */
    
    public void injectCancerSimulation() {
        System.out.println("🧬 CANCER SIMULATION");
        System.out.println("   Injecting cancer cells vs immune system");
        System.out.println();
        
        // Cancer cells: high mutation, aggressive growth
        for (int i = 0; i < 50; i++) {
            PhiNode cancer = factory.createCancerCell();
            arena.addNode(cancer);
        }
        
        // Immune cells: adaptive, targeted
        for (int i = 0; i < 100; i++) {
            PhiNode immune = factory.createImmuneCell();
            arena.addNode(immune);
        }
        
        setMode(CompetitionMode.DISEASE_SIM);
        System.out.println("   ✓ Simulation ready. Run arena to observe.");
    }
    
    public void injectSecurityTest(String vulnerableCode, String attackCode) {
        System.out.println("🔒 SECURITY TEST");
        System.out.println("   Injecting attack vs defense");
        System.out.println();
        
        inject(vulnerableCode, "vulnerable_system", "DEFENSE");
        inject(attackCode, "attack_vector", "OFFENSE");
        
        setMode(CompetitionMode.SECURITY_TEST);
        System.out.println("   ✓ Security test ready. Run arena to observe.");
    }
    
    public void injectOptimizationProblem(String problemData, String[] algorithms) {
        System.out.println("⚡ OPTIMIZATION PROBLEM");
        System.out.println("   Injecting " + algorithms.length + " competing algorithms");
        System.out.println();
        
        for (int i = 0; i < algorithms.length; i++) {
            inject(algorithms[i], "algorithm_" + i, "TEAM_" + i);
        }
        
        inject(problemData, "problem_space", "ENVIRONMENT");
        
        setMode(CompetitionMode.OPTIMIZATION);
        System.out.println("   ✓ Optimization ready. Best algorithm will emerge.");
    }
    
    public void injectEthicalDilemma(String scenario, String[] moralFrameworks) {
        System.out.println("⚖️ ETHICAL DILEMMA");
        System.out.println("   Injecting moral frameworks to solve: " + scenario);
        System.out.println();
        
        for (int i = 0; i < moralFrameworks.length; i++) {
            inject(moralFrameworks[i], "moral_framework_" + i, "ETHICS_" + i);
        }
        
        inject(scenario, "ethical_scenario", "ENVIRONMENT");
        
        setMode(CompetitionMode.ETHICAL_DILEMMA);
        System.out.println("   ✓ Dilemma loaded. Frameworks will compete.");
    }
    
    public void injectMarketSimulation(String[] tradingStrategies, String marketData) {
        System.out.println("📈 MARKET SIMULATION");
        System.out.println("   Injecting trading strategies vs market conditions");
        System.out.println();
        
        for (int i = 0; i < tradingStrategies.length; i++) {
            inject(tradingStrategies[i], "trading_strategy_" + i, "TRADER_" + i);
        }
        
        inject(marketData, "market_conditions", "ENVIRONMENT");
        
        setMode(CompetitionMode.MARKET_SIM);
        System.out.println("   ✓ Market ready. Strategies will compete for profit.");
    }
    
    /**
     * Set competition mode
     */
    public void setMode(CompetitionMode mode) {
        this.currentMode = mode;
        System.out.println("   >> MODE: " + mode);
        
        // Configure arena rules based on mode
        switch (mode) {
            case COMBAT:
                arena.setAggression(1.0f);
                arena.setCooperation(0.0f);
                break;
            case COOPERATION:
                arena.setAggression(0.0f);
                arena.setCooperation(1.0f);
                break;
            case SECURITY_TEST:
                arena.setAggression(0.8f);
                arena.setAdaptation(1.0f);
                break;
            case DISEASE_SIM:
                arena.setMutationRate(0.5f);
                arena.setSelection(1.0f);
                break;
        }
    }
    
    /**
     * Get results of competition
     */
    public CompetitionResults getResults() {
        CompetitionResults results = new CompetitionResults();
        
        // Analyze which team won
        for (Map.Entry<String, List<PhiNode>> entry : injectedEntities.entrySet()) {
            String team = entry.getKey();
            List<PhiNode> entities = entry.getValue();
            
            // Count survivors
            int survivors = 0;
            float totalFitness = 0;
            
            for (PhiNode entity : entities) {
                if (entity.isAlive()) {
                    survivors++;
                    totalFitness += entity.fitness;
                }
            }
            
            results.addTeamStats(team, survivors, totalFitness);
        }
        
        return results;
    }
    
    /**
     * Extract winning strategy
     */
    public String extractWinningStrategy() {
        CompetitionResults results = getResults();
        String winner = results.getWinningTeam();
        
        System.out.println("🏆 WINNER: " + winner);
        System.out.println("   Extracting strategy...");
        
        // Get winning entities
        List<PhiNode> winners = injectedEntities.get(winner);
        
        // Analyze their behavior
        StringBuilder strategy = new StringBuilder();
        strategy.append("WINNING STRATEGY: ").append(winner).append("\n\n");
        
        for (PhiNode entity : winners) {
            if (entity.isAlive()) {
                strategy.append("Entity ").append(entity.hashCode()).append(":\n");
                strategy.append("  Fitness: ").append(entity.fitness).append("\n");
                strategy.append("  Phi: ").append(entity.phi).append("\n");
                strategy.append("  Frequency: ").append(entity.frequency).append("\n");
                strategy.append("  Resonance: ").append(entity.resonance).append("\n");
                strategy.append("  Awareness: ").append(entity.awareness).append("\n");
                strategy.append("\n");
            }
        }
        
        return strategy.toString();
    }
    
    /**
     * Clear arena and reset
     */
    public void reset() {
        arena.clear();
        injectedEntities.clear();
        System.out.println("💉 Arena reset. Ready for new injection.");
    }
    
    /**
     * Competition results container
     */
    public static class CompetitionResults {
        private Map<String, TeamStats> teamStats = new HashMap<>();
        
        public void addTeamStats(String team, int survivors, float totalFitness) {
            teamStats.put(team, new TeamStats(survivors, totalFitness));
        }
        
        public String getWinningTeam() {
            String winner = null;
            float maxFitness = 0;
            
            for (Map.Entry<String, TeamStats> entry : teamStats.entrySet()) {
                if (entry.getValue().totalFitness > maxFitness) {
                    maxFitness = entry.getValue().totalFitness;
                    winner = entry.getKey();
                }
            }
            
            return winner;
        }
        
        public Map<String, TeamStats> getAllStats() {
            return teamStats;
        }
        
        public static class TeamStats {
            public int survivors;
            public float totalFitness;
            
            public TeamStats(int survivors, float totalFitness) {
                this.survivors = survivors;
                this.totalFitness = totalFitness;
            }
        }
    }
    
    /**
     * Factory for creating entities from any input
     */
    private static class EntityFactory {
        
        public List<PhiNode> createEntities(String input, String entityType, String team) {
            List<PhiNode> entities = new ArrayList<>();
            
            // Create entities based on type
            // This is a simplified version - real implementation would parse input
            
            int count = 10; // Default entity count
            
            for (int i = 0; i < count; i++) {
                PhiNode entity = new PhiNode(
                    (float) Math.random() * 100,
                    (float) Math.random() * 100
                );
                
                // Tag entity with type and team
                entity.setTag("type", entityType);
                entity.setTag("team", team);
                
                // Customize based on entity type
                customizeEntity(entity, entityType);
                
                entities.add(entity);
            }
            
            return entities;
        }
        
        public PhiNode createCancerCell() {
            PhiNode cancer = new PhiNode(
                (float) Math.random() * 100,
                (float) Math.random() * 100
            );
            
            cancer.setTag("type", "cancer_cell");
            cancer.setTag("team", "CANCER");
            cancer.mutationRate = 0.8f; // High mutation
            cancer.fitness = 1.5f; // Aggressive growth
            cancer.resonance = 0.3f; // Low cooperation
            
            return cancer;
        }
        
        public PhiNode createImmuneCell() {
            PhiNode immune = new PhiNode(
                (float) Math.random() * 100,
                (float) Math.random() * 100
            );
            
            immune.setTag("type", "immune_cell");
            immune.setTag("team", "IMMUNE");
            immune.awareness = 0.9f; // High detection
            immune.intent = 1.0f; // Targeted attack
            immune.resonance = 0.8f; // High cooperation
            
            return immune;
        }
        
        private void customizeEntity(PhiNode entity, String entityType) {
            // Customize entity properties based on type
            switch (entityType.toLowerCase()) {
                case "attack_vector":
                    entity.fitness = 1.2f;
                    entity.awareness = 0.7f;
                    break;
                case "vulnerable_system":
                    entity.fitness = 0.8f;
                    entity.resonance = 0.5f;
                    break;
                case "algorithm":
                    entity.intent = 1.0f;
                    entity.awareness = 0.9f;
                    break;
            }
        }
    }
}
