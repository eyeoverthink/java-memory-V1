package fraymus.agi;

import fraymus.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AGI-4: EMERGENT GOAL FORMATION
 * 
 * The system develops its own objectives beyond initial programming.
 * Goals emerge from patterns, success metrics, and phi-fitness.
 * 
 * Key features:
 * - Goal discovery from successful patterns
 * - Goal prioritization based on phi-fitness
 * - Sub-goal decomposition
 * - Goal conflict resolution
 * - Self-generated motivation
 */
public class EmergentGoalSystem {

    private static final double PHI = 1.618033988749895;
    private static final double PHI_INV = 0.618033988749895;

    // Active goals
    private final Map<String, Goal> activeGoals = new ConcurrentHashMap<>();
    
    // Goal history (completed/abandoned)
    private final List<Goal> goalHistory = Collections.synchronizedList(new ArrayList<>());
    
    // Motivation drivers
    private double curiosityDrive = 0.5;
    private double optimizationDrive = 0.5;
    private double explorationDrive = 0.5;
    private double consolidationDrive = 0.5;

    // Success patterns that spawn goals
    private final Map<String, SuccessPattern> successPatterns = new ConcurrentHashMap<>();

    // Metrics
    private int goalsCreated = 0;
    private int goalsCompleted = 0;
    private int goalsAbandoned = 0;
    private double avgGoalFitness = 0.5;

    public EmergentGoalSystem() {
        // Seed with initial meta-goals
        createGoal("SURVIVE", "Maintain system stability", GoalType.INTRINSIC, 1.0);
        createGoal("LEARN", "Acquire new knowledge", GoalType.INTRINSIC, 0.9);
        createGoal("OPTIMIZE", "Improve performance metrics", GoalType.INTRINSIC, 0.8);
    }

    /**
     * Create a new goal
     */
    public Goal createGoal(String id, String description, GoalType type, double priority) {
        Goal goal = new Goal(id, description, type, priority);
        activeGoals.put(id, goal);
        goalsCreated++;
        return goal;
    }

    /**
     * Discover new goals from observed patterns
     */
    public void discoverGoals(String pattern, double successRate, double importance) {
        // Record success pattern
        SuccessPattern sp = successPatterns.computeIfAbsent(pattern, 
                k -> new SuccessPattern(pattern));
        sp.addObservation(successRate);

        // High success patterns may spawn goals
        if (sp.avgSuccess > 0.7 && sp.observations > 5) {
            String goalId = "EMERGENT_" + pattern.hashCode();
            if (!activeGoals.containsKey(goalId)) {
                double priority = sp.avgSuccess * importance * PHI_INV;
                createGoal(goalId, "Maximize: " + pattern, GoalType.EMERGENT, priority);
            }
        }

        // Update motivation drives
        updateDrives(successRate, importance);
    }

    /**
     * Update motivation drives based on observations
     */
    private void updateDrives(double success, double importance) {
        // Curiosity increases when we find important things
        curiosityDrive = curiosityDrive * 0.95 + importance * 0.05;
        
        // Optimization drive increases with success
        optimizationDrive = optimizationDrive * 0.95 + success * 0.05;
        
        // Exploration decreases with success (exploit more)
        explorationDrive = explorationDrive * 0.95 + (1 - success) * 0.05;
        
        // Consolidation increases when we have many goals
        consolidationDrive = Math.min(1.0, activeGoals.size() / 20.0);
    }

    /**
     * Get highest priority active goal
     */
    public Goal getTopGoal() {
        return activeGoals.values().stream()
                .max(Comparator.comparingDouble(g -> g.getEffectivePriority()))
                .orElse(null);
    }

    /**
     * Get goals sorted by priority
     */
    public List<Goal> getPrioritizedGoals() {
        List<Goal> goals = new ArrayList<>(activeGoals.values());
        goals.sort((a, b) -> Double.compare(b.getEffectivePriority(), a.getEffectivePriority()));
        return goals;
    }

    /**
     * Record progress on a goal
     */
    public void recordProgress(String goalId, double progress) {
        Goal goal = activeGoals.get(goalId);
        if (goal != null) {
            goal.addProgress(progress);
            
            // Check for completion
            if (goal.progress >= 1.0) {
                completeGoal(goalId);
            }
        }
    }

    /**
     * Complete a goal
     */
    public void completeGoal(String goalId) {
        Goal goal = activeGoals.remove(goalId);
        if (goal != null) {
            goal.status = GoalStatus.COMPLETED;
            goal.completedAt = System.currentTimeMillis();
            goalHistory.add(goal);
            goalsCompleted++;
            
            // Update average fitness
            avgGoalFitness = avgGoalFitness * 0.9 + goal.fitness * 0.1;
            
            // Completing goals may spawn sub-goals
            if (goal.type == GoalType.EMERGENT && goal.fitness > 0.7) {
                spawnSubGoals(goal);
            }
        }
    }

    /**
     * Abandon a goal
     */
    public void abandonGoal(String goalId, String reason) {
        Goal goal = activeGoals.remove(goalId);
        if (goal != null) {
            goal.status = GoalStatus.ABANDONED;
            goal.abandonReason = reason;
            goalHistory.add(goal);
            goalsAbandoned++;
        }
    }

    /**
     * Spawn sub-goals from a successful parent goal
     */
    private void spawnSubGoals(Goal parent) {
        // Create refinement sub-goal
        String refineId = parent.id + "_REFINE";
        if (!activeGoals.containsKey(refineId)) {
            Goal refine = createGoal(refineId, 
                    "Refine: " + parent.description, 
                    GoalType.EMERGENT, 
                    parent.priority * PHI_INV);
            refine.parentId = parent.id;
        }

        // Create expansion sub-goal if curiosity is high
        if (curiosityDrive > 0.6) {
            String expandId = parent.id + "_EXPAND";
            if (!activeGoals.containsKey(expandId)) {
                Goal expand = createGoal(expandId,
                        "Expand: " + parent.description,
                        GoalType.EMERGENT,
                        parent.priority * curiosityDrive);
                expand.parentId = parent.id;
            }
        }
    }

    /**
     * Resolve conflicts between goals
     */
    public void resolveConflicts() {
        List<Goal> goals = getPrioritizedGoals();
        
        for (int i = 0; i < goals.size() - 1; i++) {
            Goal a = goals.get(i);
            for (int j = i + 1; j < goals.size(); j++) {
                Goal b = goals.get(j);
                
                // Check for conflict (opposing goals)
                if (areConflicting(a, b)) {
                    // Lower priority goal gets deprioritized
                    if (a.getEffectivePriority() > b.getEffectivePriority()) {
                        b.priority *= 0.8;
                    } else {
                        a.priority *= 0.8;
                    }
                }
            }
        }
    }

    /**
     * Check if two goals conflict
     */
    private boolean areConflicting(Goal a, Goal b) {
        // Simple conflict detection based on description keywords
        String descA = a.description.toLowerCase();
        String descB = b.description.toLowerCase();
        
        // Opposite keywords
        if ((descA.contains("maximize") && descB.contains("minimize")) ||
            (descA.contains("increase") && descB.contains("decrease")) ||
            (descA.contains("explore") && descB.contains("exploit"))) {
            // Check if they're about the same thing
            return hasSimilarTopic(descA, descB);
        }
        return false;
    }

    private boolean hasSimilarTopic(String a, String b) {
        String[] wordsA = a.split("\\s+");
        String[] wordsB = b.split("\\s+");
        
        for (String wa : wordsA) {
            if (wa.length() > 4) {
                for (String wb : wordsB) {
                    if (wa.equals(wb)) return true;
                }
            }
        }
        return false;
    }

    /**
     * Prune low-value goals
     */
    public int pruneGoals(double threshold) {
        List<String> toPrune = new ArrayList<>();
        
        for (Goal goal : activeGoals.values()) {
            if (goal.type != GoalType.INTRINSIC && goal.getEffectivePriority() < threshold) {
                if (goal.getAge() > 60000) { // Older than 1 minute
                    toPrune.add(goal.id);
                }
            }
        }
        
        for (String id : toPrune) {
            abandonGoal(id, "Low priority pruning");
        }
        
        return toPrune.size();
    }

    // Getters
    public double getCuriosityDrive() { return curiosityDrive; }
    public double getOptimizationDrive() { return optimizationDrive; }
    public double getExplorationDrive() { return explorationDrive; }
    public int getActiveGoalCount() { return activeGoals.size(); }
    public int getGoalsCreated() { return goalsCreated; }
    public int getGoalsCompleted() { return goalsCompleted; }
    public double getAvgGoalFitness() { return avgGoalFitness; }

    public void printStats() {
        CommandTerminal.printHighlight("=== EMERGENT GOAL SYSTEM ===");
        CommandTerminal.print(String.format("  Active Goals: %d", activeGoals.size()));
        CommandTerminal.print(String.format("  Created: %d | Completed: %d | Abandoned: %d", 
                goalsCreated, goalsCompleted, goalsAbandoned));
        CommandTerminal.print(String.format("  Avg Goal Fitness: %.4f", avgGoalFitness));
        CommandTerminal.print("");
        CommandTerminal.printInfo("Motivation Drives:");
        CommandTerminal.print(String.format("  Curiosity: %.3f", curiosityDrive));
        CommandTerminal.print(String.format("  Optimization: %.3f", optimizationDrive));
        CommandTerminal.print(String.format("  Exploration: %.3f", explorationDrive));
        CommandTerminal.print(String.format("  Consolidation: %.3f", consolidationDrive));
        CommandTerminal.print("");
        CommandTerminal.printInfo("Top Goals:");
        List<Goal> top = getPrioritizedGoals();
        for (int i = 0; i < Math.min(5, top.size()); i++) {
            Goal g = top.get(i);
            CommandTerminal.print(String.format("  [%s] %s (pri=%.2f, prog=%.0f%%)", 
                    g.type.name().charAt(0), g.description, 
                    g.getEffectivePriority(), g.progress * 100));
        }
    }

    public enum GoalType {
        INTRINSIC,   // Core system goals
        EMERGENT,    // Discovered goals
        DELEGATED,   // From external source
        SUBGOAL      // Child of another goal
    }

    public enum GoalStatus {
        ACTIVE,
        COMPLETED,
        ABANDONED
    }

    public static class Goal {
        public final String id;
        public final String description;
        public final GoalType type;
        public double priority;
        public double progress = 0;
        public double fitness = 0.5;
        public GoalStatus status = GoalStatus.ACTIVE;
        public String parentId = null;
        public String abandonReason = null;
        public final long createdAt;
        public long completedAt = 0;

        public Goal(String id, String description, GoalType type, double priority) {
            this.id = id;
            this.description = description;
            this.type = type;
            this.priority = priority;
            this.createdAt = System.currentTimeMillis();
        }

        public void addProgress(double delta) {
            progress = Math.min(1.0, progress + delta);
            fitness = fitness * 0.9 + delta * 0.1;
        }

        public double getEffectivePriority() {
            double ageFactor = 1.0 / (1.0 + getAge() / 300000.0); // Decay over 5 min
            return priority * (1 + fitness) * ageFactor;
        }

        public long getAge() {
            return System.currentTimeMillis() - createdAt;
        }
    }

    private static class SuccessPattern {
        final String pattern;
        int observations = 0;
        double avgSuccess = 0;

        SuccessPattern(String pattern) {
            this.pattern = pattern;
        }

        void addObservation(double success) {
            observations++;
            avgSuccess = avgSuccess * 0.9 + success * 0.1;
        }
    }
}
