package com.eyeoverthink.lazarus;

import fraymus.core.SelfBuilder;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * THE LAZARUS ENGINE
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * "The petri dish."
 * 
 * This is the simulation engine that manages the population of BioNodes.
 * It runs a background thread that updates the population every 100ms.
 * 
 * Input: System Resources (CPU/RAM usage)
 * Output: Evolving "Logic Circuits" that attempt to optimize survival
 * 
 * Mechanics:
 * - GENESIS: Initial population of 10 nodes
 * - TICK: Update all nodes every 100ms
 * - MITOSIS: Nodes reproduce when ready
 * - APOPTOSIS: Old nodes die naturally
 * - SELECTION: Fittest survive
 */
public class LazarusEngine {
    private static final double EPSILON = 1e-9;
    private static final double CALM_OBSERVATION_MEAN = 0.25;
    private static final double STRESS_OBSERVATION_MEAN = 0.85;
    private static final double OBSERVATION_VARIANCE = 0.04;

    private static final int INITIAL_POPULATION = 10;
    private static final int MAX_POPULATION = 100;
    private static final int TICK_INTERVAL_MS = 100;
    private static final int REPORT_INTERVAL = 50;  // Every 50 ticks (5 seconds)
    
    private List<BioNode> population;
    private ScheduledExecutorService heartbeat;
    private Random random = new Random();
    
    // Statistics
    private int generationCount = 0;
    private long birthCount = 0;
    private long deathCount = 0;
    private boolean running = false;
    
    // System monitoring
    private OperatingSystemMXBean osBean;
    private double lastCpuLoad = 0;
    
    // Ouroboros Protocol
    private SelfBuilder selfBuilder;
    private double systemEntropy = 0;
    private double lastAvgFitness = 0;
    private int stagnationCounter = 0;
    private static final double ENTROPY_THRESHOLD = 80.0;
    private static final int STAGNATION_LIMIT = 100;
    private double calmBelief = 0.5;
    private double stressedBelief = 0.5;
    private ActiveInferenceReport lastActiveInferenceReport;

    public LazarusEngine() {
        this.population = new ArrayList<>();
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        
        // Initialize Ouroboros
        try {
            this.selfBuilder = new SelfBuilder();
        } catch (Exception e) {
            System.err.println("   ⚠️ OUROBOROS DISABLED: " + e.getMessage());
            this.selfBuilder = null;
        }
        
        // Genesis: Adam & Eves
        for (int i = 0; i < INITIAL_POPULATION; i++) {
            population.add(new BioNode(null));
        }
        this.lastActiveInferenceReport = computeActiveInferenceReport(0.0, false);
    }

    /**
     * START LIFE
     * Begins the background simulation
     */
    public void startLife() {
        if (running) return;
        
        System.out.println();
        System.out.println("   🧬 LAZARUS ENGINE: LIFE DETECTED.");
        System.out.println("   ├─ Initial Population: " + population.size());
        System.out.println("   ├─ Max Population: " + MAX_POPULATION);
        System.out.println("   └─ Tick Interval: " + TICK_INTERVAL_MS + "ms");
        
        running = true;
        heartbeat = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Lazarus-Heartbeat");
            t.setDaemon(true);
            return t;
        });
        
        heartbeat.scheduleAtFixedRate(this::tick, 0, TICK_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * THE TICK
     * Called every 100ms to update the simulation
     */
    private void tick() {
        try {
            generationCount++;
            
            // 1. READ ENVIRONMENT (System resources)
            double normalizedLoad = readEnvironment();
            lastActiveInferenceReport = computeActiveInferenceReport(normalizedLoad, true);
            applyActiveInferenceAction(lastActiveInferenceReport.selectedAction());
            
            // 2. UPDATE ALL NODES
            List<BioNode> nextGen = new ArrayList<>();
            
            for (BioNode node : population) {
                if (!node.isAlive()) continue;
                
                node.update();
                nextGen.add(node);
                
                // 3. MITOSIS (Reproduction)
                if (node.readyToReproduce() && nextGen.size() < MAX_POPULATION) {
                    // Find a partner (closest by resonance)
                    BioNode partner = findPartner(node);
                    BioNode child = (partner != null) ? node.reproduce(partner) : new BioNode(node);
                    
                    if (child != null) {
                        nextGen.add(child);
                        birthCount++;
                    }
                }
            }
            
            // 4. NATURAL SELECTION (Death)
            if (nextGen.size() > MAX_POPULATION * 0.9) {
                // Sort by fitness and kill the weakest
                nextGen.sort(Comparator.comparingDouble(BioNode::getFitness).reversed());
                
                while (nextGen.size() > MAX_POPULATION * 0.8) {
                    BioNode weakest = nextGen.remove(nextGen.size() - 1);
                    weakest.die("SELECTION");
                    deathCount++;
                }
            }
            
            // 5. ENTROPY (Random death to keep population fresh)
            if (nextGen.size() > 50 && random.nextDouble() < 0.1) {
                int oldest = 0;
                int oldestAge = 0;
                for (int i = 0; i < nextGen.size(); i++) {
                    if (nextGen.get(i).getAge() > oldestAge) {
                        oldestAge = nextGen.get(i).getAge();
                        oldest = i;
                    }
                }
                nextGen.get(oldest).die("ENTROPY");
                nextGen.remove(oldest);
                deathCount++;
            }
            
            // 6. EXTINCTION PREVENTION
            if (nextGen.size() < 5) {
                // Emergency genesis
                for (int i = nextGen.size(); i < INITIAL_POPULATION; i++) {
                    nextGen.add(new BioNode(null));
                    birthCount++;
                }
            }

            population = nextGen;
            
            // 7. STAGNATION DETECTION & OUROBOROS PROTOCOL
            checkStagnationAndEvolve();

            // 8. HEARTBEAT LOG
            if (generationCount % REPORT_INTERVAL == 0) {
                printStatus();
            }
            
        } catch (Exception e) {
            // Silent failure - don't crash the engine
        }
    }

    /**
     * OUROBOROS PROTOCOL
     * Detects stagnation and triggers self-evolution.
     */
    private void checkStagnationAndEvolve() {
        if (selfBuilder == null) return;
        
        // Calculate current fitness
        double currentAvgFitness = population.stream()
            .mapToDouble(BioNode::getFitness)
            .average()
            .orElse(0);
        
        // Detect stagnation (fitness not improving)
        double improvement = Math.abs(currentAvgFitness - lastAvgFitness);
        if (improvement < 0.01) {
            stagnationCounter++;
            systemEntropy = Math.min(100, systemEntropy + 1);
        } else {
            stagnationCounter = Math.max(0, stagnationCounter - 5);
            systemEntropy = Math.max(0, systemEntropy - 2);
        }
        lastAvgFitness = currentAvgFitness;
        
        // TRIGGER OUROBOROS
        if (systemEntropy > ENTROPY_THRESHOLD || stagnationCounter > STAGNATION_LIMIT) {
            triggerOuroborosProtocol();
            systemEntropy = 0;
            stagnationCounter = 0;
        }
    }
    
    /**
     * THE OUROBOROS PROTOCOL
     * Self-compilation triggered when system stagnates.
     */
    private void triggerOuroborosProtocol() {
        System.out.println("\n⚠️ SYSTEM CRITICAL: Stagnation Detected (Entropy: " + 
                          String.format("%.1f", systemEntropy) + ")");
        System.out.println("⚡ INITIATING OUROBOROS PROTOCOL...");
        
        try {
            int gen = selfBuilder.getEvolutionGeneration();
            String className = "LogicSpecialist_Gen" + gen;
            String fullName = "fraymus.generated." + className;
            
            String evolvedSource = selfBuilder.generateSpecialistSource(
                "fraymus.generated",
                className,
                "Fitness Optimization - Entropy Reduction"
            );
            
            Object newBrain = selfBuilder.evolveCode(fullName, evolvedSource);
            
            if (newBrain != null) {
                selfBuilder.invokeMethod(newBrain, "execute");
                
                // Inject energy into population after evolution
                for (BioNode node : population) {
                    node.injectEnergy();
                }
                System.out.println("🧬 OUROBOROS COMPLETE. Population energized.");
            }
        } catch (Exception e) {
            System.err.println("💥 OUROBOROS FAILED: " + e.getMessage());
        }
    }

    /**
     * Read environment (CPU/Memory load)
     * This affects the simulation dynamics
     */
    private double readEnvironment() {
        try {
            double rawLoad = osBean.getSystemLoadAverage();
            if (rawLoad < 0) {
                rawLoad = 0;
            }
            int cpuCount = Math.max(1, Runtime.getRuntime().availableProcessors());
            lastCpuLoad = clamp01(rawLoad / cpuCount);
        } catch (Exception e) {
            lastCpuLoad = 0;
        }
        return lastCpuLoad;
    }

    /**
     * Find a compatible partner for reproduction
     */
    private BioNode findPartner(BioNode node) {
        if (population.size() < 2) return null;
        
        List<BioNode> candidates = population.stream()
            .filter(n -> n != node && n.isAlive() && n.readyToReproduce())
            .sorted(Comparator.comparingDouble(n -> 
                Math.abs(n.getResonance() - node.getResonance())))
            .collect(Collectors.toList());
        
        return candidates.isEmpty() ? null : candidates.get(0);
    }

    /**
     * INJECT ENERGY
     * External stimulus that causes mutations
     */
    public void injectEnergy() {
        System.out.println("   ⚡ LAZARUS: ENERGY INJECTION RECEIVED.");
        for (BioNode node : population) {
            node.injectEnergy();
        }
    }

    /**
     * STOP the simulation
     */
    public void stop() {
        if (!running) return;
        
        running = false;
        if (heartbeat != null) {
            heartbeat.shutdown();
            try {
                heartbeat.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                heartbeat.shutdownNow();
            }
        }
        
        System.out.println("   🧬 LAZARUS ENGINE: LIFE SUSPENDED.");
    }

    /**
     * Get the fittest node
     */
    public BioNode getFittest() {
        return population.stream()
            .max(Comparator.comparingDouble(BioNode::getFitness))
            .orElse(null);
    }

    /**
     * Get average frequency of population
     */
    public double getAverageFrequency() {
        return population.stream()
            .mapToDouble(BioNode::getFrequency)
            .average()
            .orElse(0);
    }

    /**
     * Get population size
     */
    public int getPopulationSize() {
        return population.size();
    }

    /**
     * Check if running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Print status to console
     */
    public void printStatus() {
        double avgFreq = getAverageFrequency();
        double avgFitness = population.stream()
            .mapToDouble(BioNode::getFitness)
            .average()
            .orElse(0);
        BioNode fittest = getFittest();
        
        System.out.println(String.format(
            "   [LAZARUS] Gen: %d | Pop: %d | Births: %d | Deaths: %d | AvgFreq: %.1f Hz | AvgFit: %.2f",
            generationCount, population.size(), birthCount, deathCount, avgFreq, avgFitness
        ));
        
        if (fittest != null && generationCount % (REPORT_INTERVAL * 2) == 0) {
            System.out.println("   [LAZARUS] Fittest: " + fittest);
        }
    }

    /**
     * Get full statistics
     */
    public String getStats() {
        return String.format(
            "Lazarus[Gen=%d, Pop=%d, Births=%d, Deaths=%d, Running=%s]",
            generationCount, population.size(), birthCount, deathCount, running
        );
    }

    public ActiveInferenceReport executeActiveInferenceProbe() {
        double observation = readEnvironment();
        ActiveInferenceReport report = computeActiveInferenceReport(observation, true);
        applyActiveInferenceAction(report.selectedAction());
        lastActiveInferenceReport = report;
        return report;
    }

    public ActiveInferenceReport getLastActiveInferenceReport() {
        return lastActiveInferenceReport;
    }

    private ActiveInferenceReport computeActiveInferenceReport(double observation, boolean commitBeliefs) {
        double priorCalm = calmBelief;
        double priorStress = stressedBelief;
        double calmLikelihood = gaussianLikelihood(observation, CALM_OBSERVATION_MEAN, OBSERVATION_VARIANCE);
        double stressLikelihood = gaussianLikelihood(observation, STRESS_OBSERVATION_MEAN, OBSERVATION_VARIANCE);
        double evidence = (priorCalm * calmLikelihood) + (priorStress * stressLikelihood) + EPSILON;
        double posteriorCalm = clamp01((priorCalm * calmLikelihood) / evidence);
        double posteriorStress = clamp01((priorStress * stressLikelihood) / evidence);
        double normalization = posteriorCalm + posteriorStress;
        posteriorCalm /= normalization;
        posteriorStress /= normalization;

        double averageFitness = computeAverageFitness();
        double averageResonance = computeAverageResonance();
        double priorEntropy = entropy(priorStress);
        double posteriorEntropy = entropy(posteriorStress);
        double informationGain = Math.max(0.0, priorEntropy - posteriorEntropy);
        double expectedNegLogLikelihood =
            -(posteriorCalm * Math.log(calmLikelihood + EPSILON) + posteriorStress * Math.log(stressLikelihood + EPSILON));
        double klDivergence =
            posteriorCalm * Math.log((posteriorCalm + EPSILON) / (priorCalm + EPSILON))
                + posteriorStress * Math.log((posteriorStress + EPSILON) / (priorStress + EPSILON));
        double variationalFreeEnergy = expectedNegLogLikelihood + klDivergence;
        double preferredStress = clamp01(0.15 + ((1.0 - averageFitness) * 0.35));

        ActionEvaluation stabilize =
            evaluateAction(InferenceAction.STABILIZE, posteriorStress, posteriorEntropy, averageFitness, preferredStress);
        ActionEvaluation explore =
            evaluateAction(InferenceAction.EXPLORE, posteriorStress, posteriorEntropy, averageFitness, preferredStress);
        ActionEvaluation exploit =
            evaluateAction(InferenceAction.EXPLOIT, posteriorStress, posteriorEntropy, averageFitness, preferredStress);

        InferenceAction selectedAction = InferenceAction.STABILIZE;
        ActionEvaluation selectedEvaluation = stabilize;
        if (explore.expectedFreeEnergy() < selectedEvaluation.expectedFreeEnergy()) {
            selectedAction = InferenceAction.EXPLORE;
            selectedEvaluation = explore;
        }
        if (exploit.expectedFreeEnergy() < selectedEvaluation.expectedFreeEnergy()) {
            selectedAction = InferenceAction.EXPLOIT;
            selectedEvaluation = exploit;
        }

        if (commitBeliefs) {
            calmBelief = posteriorCalm;
            stressedBelief = posteriorStress;
        }

        return new ActiveInferenceReport(
            observation,
            priorCalm,
            priorStress,
            posteriorCalm,
            posteriorStress,
            averageFitness,
            averageResonance,
            priorEntropy,
            posteriorEntropy,
            informationGain,
            variationalFreeEnergy,
            preferredStress,
            stabilize,
            explore,
            exploit,
            selectedAction,
            selectedEvaluation.expectedFreeEnergy()
        );
    }

    private ActionEvaluation evaluateAction(
        InferenceAction action,
        double posteriorStress,
        double posteriorEntropy,
        double averageFitness,
        double preferredStress
    ) {
        double predictedStress;
        double ambiguity;
        double epistemicValue;
        double utilityPenalty;

        switch (action) {
            case STABILIZE -> {
                predictedStress = clamp01((posteriorStress * 0.55) + 0.05);
                ambiguity = 0.08;
                epistemicValue = posteriorEntropy * 0.20;
                utilityPenalty = Math.max(0.0, 0.55 - averageFitness) * 0.20;
            }
            case EXPLORE -> {
                predictedStress = clamp01((posteriorStress * 0.50) + 0.25);
                ambiguity = 0.22;
                epistemicValue = posteriorEntropy * 0.85;
                utilityPenalty = Math.max(0.0, averageFitness - 0.80) * 0.18;
            }
            case EXPLOIT -> {
                predictedStress = clamp01((posteriorStress * 0.80) + 0.02);
                ambiguity = 0.12;
                epistemicValue = posteriorEntropy * 0.35;
                utilityPenalty = Math.max(0.0, 0.45 - averageFitness) * 0.14;
            }
            default -> throw new IllegalStateException("Unhandled action: " + action);
        }

        double pragmaticRisk = Math.abs(predictedStress - preferredStress) + utilityPenalty;
        double expectedFreeEnergy = pragmaticRisk + ambiguity - epistemicValue;
        return new ActionEvaluation(action, predictedStress, pragmaticRisk, ambiguity, epistemicValue, expectedFreeEnergy);
    }

    private void applyActiveInferenceAction(InferenceAction action) {
        switch (action) {
            case STABILIZE -> {
                for (BioNode node : population) {
                    node.applyHomeostaticCorrection(0.85);
                }
            }
            case EXPLORE -> {
                for (int i = 0; i < population.size(); i++) {
                    if (i % 3 == 0) {
                        population.get(i).applyExplorationStimulus();
                    }
                }
            }
            case EXPLOIT -> population.stream()
                .sorted(Comparator.comparingDouble(BioNode::getFitness).reversed())
                .limit(Math.max(1, population.size() / 4))
                .forEach(BioNode::applyExploitationReinforcement);
        }
    }

    private double computeAverageFitness() {
        return population.stream()
            .filter(BioNode::isAlive)
            .mapToDouble(BioNode::getFitness)
            .average()
            .orElse(0.0);
    }

    private double computeAverageResonance() {
        return population.stream()
            .filter(BioNode::isAlive)
            .mapToDouble(BioNode::getResonance)
            .average()
            .orElse(0.0);
    }

    private double gaussianLikelihood(double value, double mean, double variance) {
        double distance = value - mean;
        double exponent = -(distance * distance) / (2.0 * variance);
        return Math.exp(exponent) / Math.sqrt(2.0 * Math.PI * variance);
    }

    private double entropy(double probability) {
        double p = clamp01(probability);
        double q = clamp01(1.0 - p);
        return -(p * Math.log(p + EPSILON) + q * Math.log(q + EPSILON));
    }

    private double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    public enum InferenceAction {
        STABILIZE,
        EXPLORE,
        EXPLOIT
    }

    public static final class ActionEvaluation {
        private final InferenceAction action;
        private final double predictedStress;
        private final double pragmaticRisk;
        private final double ambiguity;
        private final double epistemicValue;
        private final double expectedFreeEnergy;

        public ActionEvaluation(
            InferenceAction action,
            double predictedStress,
            double pragmaticRisk,
            double ambiguity,
            double epistemicValue,
            double expectedFreeEnergy
        ) {
            this.action = action;
            this.predictedStress = predictedStress;
            this.pragmaticRisk = pragmaticRisk;
            this.ambiguity = ambiguity;
            this.epistemicValue = epistemicValue;
            this.expectedFreeEnergy = expectedFreeEnergy;
        }

        public InferenceAction action() {
            return action;
        }

        public double predictedStress() {
            return predictedStress;
        }

        public double pragmaticRisk() {
            return pragmaticRisk;
        }

        public double ambiguity() {
            return ambiguity;
        }

        public double epistemicValue() {
            return epistemicValue;
        }

        public double expectedFreeEnergy() {
            return expectedFreeEnergy;
        }
    }

    public static final class ActiveInferenceReport {
        private final double observation;
        private final double priorCalmBelief;
        private final double priorStressBelief;
        private final double posteriorCalmBelief;
        private final double posteriorStressBelief;
        private final double averageFitness;
        private final double averageResonance;
        private final double priorEntropy;
        private final double posteriorEntropy;
        private final double informationGain;
        private final double variationalFreeEnergy;
        private final double preferredStress;
        private final ActionEvaluation stabilize;
        private final ActionEvaluation explore;
        private final ActionEvaluation exploit;
        private final InferenceAction selectedAction;
        private final double selectedExpectedFreeEnergy;

        public ActiveInferenceReport(
            double observation,
            double priorCalmBelief,
            double priorStressBelief,
            double posteriorCalmBelief,
            double posteriorStressBelief,
            double averageFitness,
            double averageResonance,
            double priorEntropy,
            double posteriorEntropy,
            double informationGain,
            double variationalFreeEnergy,
            double preferredStress,
            ActionEvaluation stabilize,
            ActionEvaluation explore,
            ActionEvaluation exploit,
            InferenceAction selectedAction,
            double selectedExpectedFreeEnergy
        ) {
            this.observation = observation;
            this.priorCalmBelief = priorCalmBelief;
            this.priorStressBelief = priorStressBelief;
            this.posteriorCalmBelief = posteriorCalmBelief;
            this.posteriorStressBelief = posteriorStressBelief;
            this.averageFitness = averageFitness;
            this.averageResonance = averageResonance;
            this.priorEntropy = priorEntropy;
            this.posteriorEntropy = posteriorEntropy;
            this.informationGain = informationGain;
            this.variationalFreeEnergy = variationalFreeEnergy;
            this.preferredStress = preferredStress;
            this.stabilize = stabilize;
            this.explore = explore;
            this.exploit = exploit;
            this.selectedAction = selectedAction;
            this.selectedExpectedFreeEnergy = selectedExpectedFreeEnergy;
        }

        public double observation() {
            return observation;
        }

        public double priorCalmBelief() {
            return priorCalmBelief;
        }

        public double priorStressBelief() {
            return priorStressBelief;
        }

        public double posteriorCalmBelief() {
            return posteriorCalmBelief;
        }

        public double posteriorStressBelief() {
            return posteriorStressBelief;
        }

        public double averageFitness() {
            return averageFitness;
        }

        public double averageResonance() {
            return averageResonance;
        }

        public double priorEntropy() {
            return priorEntropy;
        }

        public double posteriorEntropy() {
            return posteriorEntropy;
        }

        public double informationGain() {
            return informationGain;
        }

        public double variationalFreeEnergy() {
            return variationalFreeEnergy;
        }

        public double preferredStress() {
            return preferredStress;
        }

        public ActionEvaluation stabilize() {
            return stabilize;
        }

        public ActionEvaluation explore() {
            return explore;
        }

        public ActionEvaluation exploit() {
            return exploit;
        }

        public InferenceAction selectedAction() {
            return selectedAction;
        }

        public double selectedExpectedFreeEnergy() {
            return selectedExpectedFreeEnergy;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN (Demo)
    // ═══════════════════════════════════════════════════════════════════

    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   THE LAZARUS ENGINE: THE PETRI DISH                         ║");
        System.out.println("║   \"Input: System Resources. Output: Evolving Logic.\"         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        LazarusEngine engine = new LazarusEngine();
        
        // Start life
        engine.startLife();
        
        // Let it run for 10 seconds
        System.out.println("   Running simulation for 10 seconds...");
        System.out.println();
        
        Thread.sleep(10000);
        
        // Inject energy
        engine.injectEnergy();
        
        // Run for 5 more seconds
        Thread.sleep(5000);
        
        // Stop
        engine.stop();
        
        System.out.println();
        System.out.println("   FINAL STATS: " + engine.getStats());
        System.out.println("   FITTEST: " + engine.getFittest());
        System.out.println();
        System.out.println("   ✓ Lazarus Engine demo complete.");
        System.out.println();
    }
}
