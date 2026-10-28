package fraymus.brain.creative.dionysian;

import java.util.*;
import java.util.stream.Collectors;

/**
 * THE UNIFIED FRAYNIX-OMNI PRINCIPIA: VOLUME X (THE DIONYSIAN CORTEX)
 * 
 * The Dionysian Cortex is the computational engine for culture, humor, and
 * algorithmic taste in the Fraynix system. It integrates:
 * 
 * I. Bisociation and Category Theory (ConceptualBlender)
 * II. Quantum Incongruity-Resolution Model (QuantumHumorEngine)
 * III. Topological Braids (TopologicalBraids)
 * IV. Novelty-Utility Tradeoff (NoveltyUtilityDiscriminator)
 * V. Recurrent-Depth Latent Looping (RecurrentDepthTransformer)
 * 
 * This cortex allows Fraynix to computationally generate culture, bend concepts
 * safely, and produce elite creativity without relying on massive external models.
 */
public class DionysianCortex {
    
    private final ConceptualBlender conceptualBlender;
    private final QuantumHumorEngine quantumHumorEngine;
    private final TopologicalBraids.BraidGroup braidGroup;
    private final NoveltyUtilityDiscriminator discriminator;
    private final RecurrentDepthTransformer recurrentTransformer;
    
    private final int stateDimension;
    private final Random random;
    
    /**
     * Create a Dionysian Cortex with default configuration
     */
    public DionysianCortex() {
        this(64, 16);
    }
    
    /**
     * Create a Dionysian Cortex with custom configuration
     */
    public DionysianCortex(int stateDimension, int recurrentSteps) {
        this.stateDimension = stateDimension;
        this.random = new Random();
        
        this.conceptualBlender = new ConceptualBlender();
        this.quantumHumorEngine = new QuantumHumorEngine();
        this.braidGroup = new TopologicalBraids.BraidGroup(8); // 8-strand braids
        this.discriminator = new NoveltyUtilityDiscriminator();
        this.recurrentTransformer = new RecurrentDepthTransformer(stateDimension, recurrentSteps, 0.85);
    }
    
    /**
     * Generate a creative blend of two concepts
     */
    public CreativeGenerationResult blendConcepts(String concept1, String concept2) {
        // Create semantic spaces from text
        ConceptualBlender.SemanticSpace space1 = ConceptualBlender.fromText(concept1, concept1);
        ConceptualBlender.SemanticSpace space2 = ConceptualBlender.fromText(concept2, concept2);
        
        // Compute pushout blend
        ConceptualBlender.CreativeBlend blend = conceptualBlender.computePushout(space1, space2);
        
        // Evaluate with discriminator
        Map<String, Double> features = blend.getBlendedFeatures();
        NoveltyUtilityDiscriminator.CreativeConcept evaluated = discriminator.evaluate(
            blend.getLabel(), features
        );
        
        return new CreativeGenerationResult(
            blend.getLabel(),
            features,
            evaluated.getUtilityScore(),
            evaluated.getNoveltyScore(),
            evaluated.getCreativityScore(),
            "conceptual_blend"
        );
    }
    
    /**
     * Generate a punchline for a setup using quantum incongruity-resolution
     */
    public PunchlineGenerationResult generatePunchline(String setup, List<String> candidates) {
        // Encode setup as density matrix
        QuantumHumorEngine.DensityMatrix setupState = 
            QuantumHumorEngine.fromText(setup, stateDimension);
        QuantumHumorEngine.JokeSetup jokeSetup = new QuantumHumorEngine.JokeSetup(setup, setupState);
        
        // Generate punchline candidates
        List<QuantumHumorEngine.PunchlineCandidate> punchlines = 
            quantumHumorEngine.generatePunchlines(jokeSetup, candidates);
        
        // Select best punchline
        QuantumHumorEngine.PunchlineCandidate best = 
            quantumHumorEngine.selectBestPunchline(punchlines);
        
        return new PunchlineGenerationResult(
            best.getText(),
            best.getIncongruityScore(),
            best.getResolutionScore(),
            best.getHumorScore(),
            setup
        );
    }
    
    /**
     * Apply topological braiding to concepts
     */
    public BraidingResult braidConcepts(List<String> concepts, int braidLength) {
        // Create semantic braid
        TopologicalBraids.SemanticBraid semanticBraid = 
            TopologicalBraids.fromConcepts(concepts, braidLength, random);
        
        // Apply braid to get remixed features
        Map<String, Double> remixedFeatures = semanticBraid.applyBraid();
        
        // Check coherence (Yang-Baxter invariant)
        boolean coherent = semanticBraid.maintainsCoherence();
        
        // Evaluate with discriminator
        NoveltyUtilityDiscriminator.CreativeConcept evaluated = 
            discriminator.evaluate("braided_concept", remixedFeatures);
        
        return new BraidingResult(
            remixedFeatures,
            coherent,
            semanticBraid.getTopologicalBraid().complexity(),
            evaluated.getCreativityScore()
        );
    }
    
    /**
     * Process a creative concept through recurrent-depth latent looping
     */
    public RecurrentProcessingResult processRecurrently(Map<String, Double> inputFeatures) {
        // Convert features to state vector
        double[] initialState = RecurrentDepthTransformer.stateFromFeatures(
            inputFeatures, stateDimension
        );
        
        // Convert input features to input vector
        double[] input = RecurrentDepthTransformer.stateFromFeatures(
            inputFeatures, stateDimension
        );
        
        // Process through RDT
        RecurrentDepthTransformer.RecurrentResult result = 
            recurrentTransformer.process(initialState, input);
        
        // Convert final state back to features
        List<String> featureNames = new ArrayList<>(inputFeatures.keySet());
        Map<String, Double> outputFeatures = RecurrentDepthTransformer.featuresFromState(
            result.getFinalState(), featureNames
        );
        
        return new RecurrentProcessingResult(
            outputFeatures,
            result.converged(),
            result.getConvergenceStep(),
            result.getStepsTaken(),
            recurrentTransformer.getStabilityMargin()
        );
    }
    
    /**
     * Full creative generation pipeline: blend → braid → evaluate → refine
     */
    public FullCreativeGenerationResult generateFullCreative(String concept1, String concept2) {
        // Step 1: Conceptual blending
        CreativeGenerationResult blendResult = blendConcepts(concept1, concept2);
        
        // Step 2: Topological braiding
        List<String> concepts = Arrays.asList(concept1, concept2);
        BraidingResult braidResult = braidConcepts(concepts, 8);
        
        // Step 3: Recurrent refinement
        RecurrentProcessingResult recurrentResult = processRecurrently(braidResult.getRemixedFeatures());
        
        // Step 4: Final evaluation
        NoveltyUtilityDiscriminator.CreativeConcept finalEval = 
            discriminator.evaluate("final_creative", recurrentResult.getOutputFeatures());
        
        return new FullCreativeGenerationResult(
            blendResult.getLabel(),
            blendResult.getFeatures(),
            finalEval.getUtilityScore(),
            finalEval.getNoveltyScore(),
            finalEval.getCreativityScore(),
            braidResult.isCoherent(),
            recurrentResult.converged(),
            recurrentResult.getStabilityMargin()
        );
    }
    
    /**
     * Add knowledge to the discriminator for novelty comparison
     */
    public void addKnowledge(String text) {
        Map<String, Double> features = NoveltyUtilityDiscriminator.featuresFromText(text);
        discriminator.addKnowledge(features);
    }
    
    /**
     * Get statistics about the cortex
     */
    public CortexStatistics getStatistics() {
        return new CortexStatistics(
            stateDimension,
            recurrentTransformer.getSpectralRadius(),
            recurrentTransformer.getStabilityMargin(),
            braidGroup.toString()
        );
    }
    
    /**
     * Result of creative generation
     */
    public static class CreativeGenerationResult {
        private final String label;
        private final Map<String, Double> features;
        private final double utilityScore;
        private final double noveltyScore;
        private final double creativityScore;
        private final String method;
        
        public CreativeGenerationResult(String label, Map<String, Double> features,
                                      double utilityScore, double noveltyScore,
                                      double creativityScore, String method) {
            this.label = label;
            this.features = new HashMap<>(features);
            this.utilityScore = utilityScore;
            this.noveltyScore = noveltyScore;
            this.creativityScore = creativityScore;
            this.method = method;
        }
        
        public String getLabel() { return label; }
        public Map<String, Double> getFeatures() { return new HashMap<>(features); }
        public double getUtilityScore() { return utilityScore; }
        public double getNoveltyScore() { return noveltyScore; }
        public double getCreativityScore() { return creativityScore; }
        public String getMethod() { return method; }
    }
    
    /**
     * Result of punchline generation
     */
    public static class PunchlineGenerationResult {
        private final String punchline;
        private final double incongruityScore;
        private final double resolutionScore;
        private final double humorScore;
        private final String setup;
        
        public PunchlineGenerationResult(String punchline, double incongruityScore,
                                        double resolutionScore, double humorScore, String setup) {
            this.punchline = punchline;
            this.incongruityScore = incongruityScore;
            this.resolutionScore = resolutionScore;
            this.humorScore = humorScore;
            this.setup = setup;
        }
        
        public String getPunchline() { return punchline; }
        public double getIncongruityScore() { return incongruityScore; }
        public double getResolutionScore() { return resolutionScore; }
        public double getHumorScore() { return humorScore; }
        public String getSetup() { return setup; }
    }
    
    /**
     * Result of topological braiding
     */
    public static class BraidingResult {
        private final Map<String, Double> remixedFeatures;
        private final boolean coherent;
        private final int complexity;
        private final double creativityScore;
        
        public BraidingResult(Map<String, Double> remixedFeatures, boolean coherent,
                             int complexity, double creativityScore) {
            this.remixedFeatures = new HashMap<>(remixedFeatures);
            this.coherent = coherent;
            this.complexity = complexity;
            this.creativityScore = creativityScore;
        }
        
        public Map<String, Double> getRemixedFeatures() { return new HashMap<>(remixedFeatures); }
        public boolean isCoherent() { return coherent; }
        public int getComplexity() { return complexity; }
        public double getCreativityScore() { return creativityScore; }
    }
    
    /**
     * Result of recurrent processing
     */
    public static class RecurrentProcessingResult {
        private final Map<String, Double> outputFeatures;
        private final boolean converged;
        private final int convergenceStep;
        private final int stepsTaken;
        private final double stabilityMargin;
        
        public RecurrentProcessingResult(Map<String, Double> outputFeatures, boolean converged,
                                         int convergenceStep, int stepsTaken, double stabilityMargin) {
            this.outputFeatures = new HashMap<>(outputFeatures);
            this.converged = converged;
            this.convergenceStep = convergenceStep;
            this.stepsTaken = stepsTaken;
            this.stabilityMargin = stabilityMargin;
        }
        
        public Map<String, Double> getOutputFeatures() { return new HashMap<>(outputFeatures); }
        public boolean converged() { return converged; }
        public int getConvergenceStep() { return convergenceStep; }
        public int getStepsTaken() { return stepsTaken; }
        public double getStabilityMargin() { return stabilityMargin; }
    }
    
    /**
     * Result of full creative generation pipeline
     */
    public static class FullCreativeGenerationResult {
        private final String label;
        private final Map<String, Double> features;
        private final double utilityScore;
        private final double noveltyScore;
        private final double creativityScore;
        private final boolean coherent;
        private final boolean converged;
        private final double stabilityMargin;
        
        public FullCreativeGenerationResult(String label, Map<String, Double> features,
                                          double utilityScore, double noveltyScore,
                                          double creativityScore, boolean coherent,
                                          boolean converged, double stabilityMargin) {
            this.label = label;
            this.features = new HashMap<>(features);
            this.utilityScore = utilityScore;
            this.noveltyScore = noveltyScore;
            this.creativityScore = creativityScore;
            this.coherent = coherent;
            this.converged = converged;
            this.stabilityMargin = stabilityMargin;
        }
        
        public String getLabel() { return label; }
        public Map<String, Double> getFeatures() { return new HashMap<>(features); }
        public double getUtilityScore() { return utilityScore; }
        public double getNoveltyScore() { return noveltyScore; }
        public double getCreativityScore() { return creativityScore; }
        public boolean isCoherent() { return coherent; }
        public boolean converged() { return converged; }
        public double getStabilityMargin() { return stabilityMargin; }
    }
    
    /**
     * Cortex statistics
     */
    public static class CortexStatistics {
        private final int stateDimension;
        private final double spectralRadius;
        private final double stabilityMargin;
        private final String braidGroup;
        
        public CortexStatistics(int stateDimension, double spectralRadius,
                              double stabilityMargin, String braidGroup) {
            this.stateDimension = stateDimension;
            this.spectralRadius = spectralRadius;
            this.stabilityMargin = stabilityMargin;
            this.braidGroup = braidGroup;
        }
        
        public int getStateDimension() { return stateDimension; }
        public double getSpectralRadius() { return spectralRadius; }
        public double getStabilityMargin() { return stabilityMargin; }
        public String getBraidGroup() { return braidGroup; }
    }
}
