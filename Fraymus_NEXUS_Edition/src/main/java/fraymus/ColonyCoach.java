package fraymus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColonyCoach {

    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    private static final int EVALUATION_INTERVAL = 180;
    private static final int CODE_GENERATION_INTERVAL = 300;

    private final ConceptArena arena;
    private final GenesisMemory memory;

    private int tickCounter = 0;
    private int codeGenCounter = 0;
    private int totalEvaluations = 0;
    private int totalCodeGenerated = 0;
    private int coachingDecisions = 0;

    private double colonyHealth = 0.5;
    private double colonyProductivity = 0.0;
    private double colonyDiversity = 0.0;
    private double colonyResonance = 0.0;

    private String lastCoachingAction = "Observing";
    private final List<String> coachLog = new ArrayList<>();
    private static final int MAX_COACH_LOG = 50;

    private final Map<AntRole, RoleMetrics> roleMetrics = new HashMap<>();

    public static class RoleMetrics {
        public int entityCount = 0;
        public double avgFitness = 0;
        public double avgEnergy = 0;
        public int conceptsGenerated = 0;
        public int conceptWins = 0;
        public double productivity = 0;

        public String getSummary() {
            return String.format("n=%d fit=%.3f e=%.2f code=%d wins=%d prod=%.2f",
                entityCount, avgFitness, avgEnergy, conceptsGenerated, conceptWins, productivity);
        }
    }

    public ColonyCoach(ConceptArena arena, GenesisMemory memory) {
        this.arena = arena;
        this.memory = memory;
        for (AntRole role : AntRole.values()) {
            roleMetrics.put(role, new RoleMetrics());
        }
    }

    public void tick(List<PhiNode> nodes, long worldTick) {
        tickCounter++;

        if (tickCounter % EVALUATION_INTERVAL == 0) {
            evaluateColony(nodes);
        }

        if (tickCounter % CODE_GENERATION_INTERVAL == 0) {
            generateCode(nodes, worldTick);
        }

        if (tickCounter % (EVALUATION_INTERVAL * 3) == 0) {
            arena.evolve();
            addCoachLog("Arena evolution cycle " + arena.getEvolutionCycle());
        }
    }

    private void evaluateColony(List<PhiNode> nodes) {
        totalEvaluations++;

        for (RoleMetrics rm : roleMetrics.values()) {
            rm.entityCount = 0;
            rm.avgFitness = 0;
            rm.avgEnergy = 0;
        }

        double totalFitness = 0;
        double totalEnergy = 0;
        int spikeCount = 0;
        Map<AntRole, Integer> roleCounts = new HashMap<>();

        for (PhiNode node : nodes) {
            AntRole role = node.getRole();
            RoleMetrics rm = roleMetrics.get(role);
            rm.entityCount++;
            rm.avgFitness += node.adaptiveEngine.getCurrentFitness();
            rm.avgEnergy += node.energy;

            totalFitness += node.adaptiveEngine.getCurrentFitness();
            totalEnergy += node.energy;
            if (node.spikeFlash) spikeCount++;

            roleCounts.merge(role, 1, Integer::sum);
        }

        int pop = nodes.size();
        if (pop > 0) {
            for (RoleMetrics rm : roleMetrics.values()) {
                if (rm.entityCount > 0) {
                    rm.avgFitness /= rm.entityCount;
                    rm.avgEnergy /= rm.entityCount;
                    rm.productivity = rm.avgFitness * rm.avgEnergy;
                }
            }

            colonyHealth = (totalEnergy / pop + totalFitness / pop) / 2.0;
            colonyProductivity = arena.getConceptCount() > 0 ? arena.getAverageFitness() : 0;

            int rolesPresent = 0;
            for (int count : roleCounts.values()) {
                if (count > 0) rolesPresent++;
            }
            colonyDiversity = (double) rolesPresent / AntRole.values().length;

            colonyResonance = (double) spikeCount / Math.max(1, pop);
        }

        makeCoachingDecision(nodes, roleCounts);
    }

    private void makeCoachingDecision(List<PhiNode> nodes, Map<AntRole, Integer> roleCounts) {
        coachingDecisions++;

        if (colonyHealth < 0.3) {
            lastCoachingAction = "Colony stressed - encouraging conservation";
            addCoachLog("ALERT: Colony health low (" + String.format("%.2f", colonyHealth) + ")");
            if (memory != null) {
                memory.record("COLONY_EVENT", "coach|health_alert|" + String.format("%.3f", colonyHealth));
            }
        } else if (colonyDiversity < 0.6) {
            AntRole missing = findMissingRole(roleCounts);
            lastCoachingAction = "Need more " + (missing != null ? missing.displayName : "diversity");
            addCoachLog("DIVERSITY: Encouraging " + (missing != null ? missing.displayName : "varied") + " specialization");
            if (memory != null) {
                memory.record("COLONY_EVENT", "coach|diversity_push|" + colonyDiversity);
            }
        } else if (colonyProductivity > 0.7) {
            lastCoachingAction = "Colony thriving - pushing evolution";
            addCoachLog("THRIVING: Colony productivity high, accelerating evolution");
        } else {
            lastCoachingAction = "Steady state - observing patterns";
        }
    }

    private AntRole findMissingRole(Map<AntRole, Integer> roleCounts) {
        AntRole least = null;
        int leastCount = Integer.MAX_VALUE;
        for (AntRole role : AntRole.values()) {
            int count = roleCounts.getOrDefault(role, 0);
            if (count < leastCount) {
                leastCount = count;
                least = role;
            }
        }
        return least;
    }

    private void generateCode(List<PhiNode> nodes, long worldTick) {
        for (PhiNode node : nodes) {
            if (node.energy < 0.3f) continue;
            if (node.age < 100) continue;

            boolean spikeActive = node.quantumClock != null && node.quantumClock.isSpikeActive();
            boolean highFitness = node.adaptiveEngine.getCurrentFitness() > 0.4;

            if (spikeActive || (highFitness && codeGenCounter % 3 == 0)) {
                AntRole role = node.getRole();
                String code = role.generateCodeFragment(node);

                if (code != null && !code.isEmpty()) {
                    CodeConcept concept = new CodeConcept(
                        node.name, role, code, node.dna.getGeneration(), worldTick
                    );

                    concept.harmonicFrequency = node.frequency;
                    concept.resonance = node.phiResonance;
                    concept.coherence = node.consciousness.getCoherence();
                    concept.computePhiFitness();

                    arena.submit(concept);
                    totalCodeGenerated++;

                    RoleMetrics rm = roleMetrics.get(role);
                    rm.conceptsGenerated++;

                    node.setLastGeneratedConcept(concept);

                    if (totalCodeGenerated % 5 == 0) {
                        FraymusUI.addLog(String.format("[CODE] %s (%s) generated concept %s fit=%.3f",
                            node.name, role.displayName, concept.hash.substring(0, 8), concept.fitness));
                    }

                    if (memory != null && totalCodeGenerated % 10 == 0) {
                        memory.record("COLONY_EVENT",
                            String.format("code_gen|%s|%s|fit=%.3f", node.name, role.name(), concept.fitness));
                    }
                }
            }
        }
        codeGenCounter++;
    }

    private void addCoachLog(String msg) {
        coachLog.add(String.format("[E%d] %s", totalEvaluations, msg));
        if (coachLog.size() > MAX_COACH_LOG) {
            coachLog.remove(0);
        }
    }

    public AntRole suggestRoleForChild(PhiNode parent) {
        Map<AntRole, Integer> roleCounts = new HashMap<>();
        for (AntRole role : AntRole.values()) {
            roleCounts.put(role, roleMetrics.get(role).entityCount);
        }

        AntRole missing = findMissingRole(roleCounts);
        if (missing != null && roleMetrics.get(missing).entityCount == 0) {
            return missing;
        }

        AntRole parentRole = parent.getRole();
        double inheritChance = parentRole.concentrationFactor;
        if (Math.random() < inheritChance * 0.6) {
            return parentRole;
        }

        return AntRole.assignFromFrequency(parent.frequency);
    }

    public ConceptArena getArena() { return arena; }
    public double getColonyHealth() { return colonyHealth; }
    public double getColonyProductivity() { return colonyProductivity; }
    public double getColonyDiversity() { return colonyDiversity; }
    public double getColonyResonance() { return colonyResonance; }
    public String getLastCoachingAction() { return lastCoachingAction; }
    public List<String> getCoachLog() { return coachLog; }
    public int getTotalEvaluations() { return totalEvaluations; }
    public int getTotalCodeGenerated() { return totalCodeGenerated; }
    public int getCoachingDecisions() { return coachingDecisions; }
    public Map<AntRole, RoleMetrics> getRoleMetrics() { return roleMetrics; }
}
