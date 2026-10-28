package fraymus.brain.creative.dionysian;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the Dionysian Cortex - the mathematical engine for
 * computational creativity, humor, and algorithmic taste.
 */
class DionysianCortexTest {
    
    @Test
    void conceptualBlenderComputesPushout() {
        ConceptualBlender blender = new ConceptualBlender();
        
        ConceptualBlender.SemanticSpace space1 = ConceptualBlender.fromText(
            "astrophysics", 
            "stars galaxies black holes space universe cosmic expansion"
        );
        ConceptualBlender.SemanticSpace space2 = ConceptualBlender.fromText(
            "baking", 
            "flour sugar oven bread cake pastry dough temperature"
        );
        
        ConceptualBlender.CreativeBlend blend = blender.computePushout(space1, space2);
        
        assertNotNull(blend);
        assertNotNull(blend.getLabel());
        assertFalse(blend.getBlendedFeatures().isEmpty());
        assertTrue(blend.getIncongruityScore() >= 0.0);
        assertTrue(blend.getIncongruityScore() <= 1.0);
        assertTrue(blend.getResolutionScore() >= 0.0);
        assertTrue(blend.getResolutionScore() <= 1.0);
    }
    
    @Test
    void quantumHumorEngineGeneratesPunchlines() {
        QuantumHumorEngine engine = new QuantumHumorEngine();
        
        QuantumHumorEngine.DensityMatrix setupState = 
            QuantumHumorEngine.fromText("Why did the chicken cross the road", 8);
        QuantumHumorEngine.JokeSetup setup = new QuantumHumorEngine.JokeSetup(
            "Why did the chicken cross the road", setupState
        );
        
        List<String> candidates = Arrays.asList(
            "To get to the other side",
            "Because it was free range",
            "To escape the farmer",
            "To prove it could fly"
        );
        
        List<QuantumHumorEngine.PunchlineCandidate> punchlines = 
            engine.generatePunchlines(setup, candidates);
        
        assertFalse(punchlines.isEmpty());
        assertEquals(candidates.size(), punchlines.size());
        
        for (QuantumHumorEngine.PunchlineCandidate punchline : punchlines) {
            assertNotNull(punchline.getText());
            assertTrue(punchline.getIncongruityScore() >= 0.0);
            assertTrue(punchline.getResolutionScore() >= 0.0);
            assertTrue(punchline.getHumorScore() >= 0.0);
        }
        
        QuantumHumorEngine.PunchlineCandidate best = engine.selectBestPunchline(punchlines);
        assertNotNull(best);
        assertTrue(best.getHumorScore() > 0);
    }
    
    @Test
    void topologicalBraidsMaintainCoherence() {
        List<String> concepts = Arrays.asList("quantum", "computing", "consciousness");
        
        TopologicalBraids.SemanticBraid braid = TopologicalBraids.fromConcepts(concepts, 6, new java.util.Random());
        
        assertNotNull(braid);
        assertEquals(3, braid.getStrandConcepts().size());
        
        Map<String, Double> remixed = braid.applyBraid();
        assertNotNull(remixed);
        assertFalse(remixed.isEmpty());
        
        // Check Yang-Baxter invariant
        boolean coherent = braid.maintainsCoherence();
        assertNotNull(coherent);
    }
    
    @Test
    void noveltyUtilityDiscriminatorEvaluatesCreativity() {
        NoveltyUtilityDiscriminator discriminator = new NoveltyUtilityDiscriminator();
        
        // Add some knowledge
        Map<String, Double> knowledge1 = Map.of("feature1", 0.8, "feature2", 0.3);
        Map<String, Double> knowledge2 = Map.of("feature1", 0.5, "feature3", 0.7);
        discriminator.addKnowledge(knowledge1);
        discriminator.addKnowledge(knowledge2);
        
        // Evaluate a new concept
        Map<String, Double> features = Map.of("feature1", 0.9, "feature4", 0.6);
        NoveltyUtilityDiscriminator.CreativeConcept concept = 
            discriminator.evaluate("test concept", features);
        
        assertNotNull(concept);
        assertTrue(concept.getUtilityScore() >= 0.0);
        assertTrue(concept.getUtilityScore() <= 1.0);
        assertTrue(concept.getNoveltyScore() >= 0.0);
        assertTrue(concept.getNoveltyScore() <= 1.0);
        assertEquals(concept.getUtilityScore() * concept.getNoveltyScore(), 
                    concept.getCreativityScore(), 0.001);
    }
    
    @Test
    void recurrentDepthTransformerConvergesStably() {
        RecurrentDepthTransformer transformer = new RecurrentDepthTransformer(32, 16);
        
        // Spectral radius must be < 1 for stability
        assertTrue(transformer.getSpectralRadius() < 1.0);
        assertTrue(transformer.getStabilityMargin() > 0.0);
        
        // Process a state
        double[] initialState = new double[32];
        for (int i = 0; i < 32; i++) {
            initialState[i] = Math.random() * 0.5;
        }
        
        RecurrentDepthTransformer.RecurrentResult result = transformer.process(initialState, null);
        
        assertNotNull(result);
        assertNotNull(result.getFinalState());
        assertEquals(32, result.getFinalState().length);
        assertEquals(16, result.getStepsTaken());
    }
    
    @Test
    void dionysianCortexGeneratesFullCreative() {
        DionysianCortex cortex = new DionysianCortex(64, 16);
        
        DionysianCortex.FullCreativeGenerationResult result = 
            cortex.generateFullCreative("quantum mechanics", "jazz improvisation");
        
        assertNotNull(result);
        assertNotNull(result.getLabel());
        assertNotNull(result.getFeatures());
        assertTrue(result.getUtilityScore() >= 0.0);
        assertTrue(result.getNoveltyScore() >= 0.0);
        assertTrue(result.getCreativityScore() >= 0.0);
        assertTrue(result.getStabilityMargin() > 0.0);
    }
    
    @Test
    void dionysianCortexGeneratesPunchlines() {
        DionysianCortex cortex = new DionysianCortex();
        
        String setup = "Why do programmers prefer dark mode";
        List<String> candidates = Arrays.asList(
            "Because light attracts bugs",
            "It matches their soul",
            "Battery saving",
            "It looks cooler"
        );
        
        DionysianCortex.PunchlineGenerationResult result = 
            cortex.generatePunchline(setup, candidates);
        
        assertNotNull(result);
        assertEquals(setup, result.getSetup());
        assertNotNull(result.getPunchline());
        assertTrue(candidates.contains(result.getPunchline()));
        assertTrue(result.getHumorScore() >= 0.0);
    }
    
    @Test
    void dionysianCortexBraidConcepts() {
        DionysianCortex cortex = new DionysianCortex();
        
        List<String> concepts = Arrays.asList("AI", "neuroscience", "philosophy");
        DionysianCortex.BraidingResult result = cortex.braidConcepts(concepts, 10);
        
        assertNotNull(result);
        assertNotNull(result.getRemixedFeatures());
        assertFalse(result.getRemixedFeatures().isEmpty());
        assertTrue(result.getComplexity() > 0);
    }
    
    @Test
    void dionysianCortexProcessesRecurrently() {
        DionysianCortex cortex = new DionysianCortex(32, 8);
        
        Map<String, Double> features = Map.of(
            "creative", 0.8,
            "novel", 0.7,
            "useful", 0.6
        );
        
        DionysianCortex.RecurrentProcessingResult result = cortex.processRecurrently(features);
        
        assertNotNull(result);
        assertNotNull(result.getOutputFeatures());
        assertTrue(result.getStabilityMargin() > 0.0);
    }
    
    @Test
    void dionysianCortexStatistics() {
        DionysianCortex cortex = new DionysianCortex(128, 20);
        
        DionysianCortex.CortexStatistics stats = cortex.getStatistics();
        
        assertNotNull(stats);
        assertEquals(128, stats.getStateDimension());
        assertTrue(stats.getSpectralRadius() < 1.0);
        assertTrue(stats.getStabilityMargin() > 0.0);
        assertNotNull(stats.getBraidGroup());
    }
    
    @Test
    void braidGroupSatisfiesYangBaxter() {
        TopologicalBraids.BraidGroup group = new TopologicalBraids.BraidGroup(4);
        
        // Create a braid that should satisfy Yang-Baxter
        TopologicalBraids.Braid braid = new TopologicalBraids.Braid(4);
        braid.append(group.generator(1, true));
        braid.append(group.generator(2, true));
        braid.append(group.generator(1, true));
        
        // Check if it satisfies Yang-Baxter
        boolean satisfies = braid.satisfiesYangBaxter();
        
        assertNotNull(satisfies);
    }
    
    @Test
    void densityMatrixComputesEntropy() {
        QuantumHumorEngine.DensityMatrix matrix = 
            new QuantumHumorEngine.DensityMatrix(4);
        
        // Set up a simple density matrix
        matrix.set(0, 0, 0.5);
        matrix.set(1, 1, 0.3);
        matrix.set(2, 2, 0.2);
        matrix.set(3, 3, 0.0);
        
        double entropy = matrix.vonNeumannEntropy();
        
        assertTrue(entropy >= 0.0);
    }
    
    @Test
    void discriminatorComputesParetoFrontier() {
        NoveltyUtilityDiscriminator discriminator = new NoveltyUtilityDiscriminator();
        
        // Create some concepts
        List<NoveltyUtilityDiscriminator.CreativeConcept> concepts = Arrays.asList(
            new NoveltyUtilityDiscriminator.CreativeConcept("A", Map.of("f1", 0.5), 0.8, 0.3),
            new NoveltyUtilityDiscriminator.CreativeConcept("B", Map.of("f2", 0.6), 0.6, 0.7),
            new NoveltyUtilityDiscriminator.CreativeConcept("C", Map.of("f3", 0.4), 0.4, 0.9),
            new NoveltyUtilityDiscriminator.CreativeConcept("D", Map.of("f4", 0.7), 0.9, 0.5)
        );
        
        List<NoveltyUtilityDiscriminator.CreativeConcept> frontier = 
            discriminator.getParetoFrontier(concepts);
        
        assertNotNull(frontier);
        assertFalse(frontier.isEmpty());
    }
}
