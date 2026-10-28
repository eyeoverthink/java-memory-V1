package brain_architecture.synapses;

import brain_architecture.synapses.HDCSimilarityEngine;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SynapseManager - Manages synaptic connections between concepts.
 * 
 * Implements Hebbian learning: "fire together, wire together"
 * Creates connections based on semantic similarity
 * Manages synaptic plasticity (LTP, LTD, STDP)
 */
public class SynapseManager {
    
    private final Map<UUID, Concept> concepts;
    private final Map<UUID, List<Synapse>> synapsesBySource;
    private final Map<UUID, List<Synapse>> synapsesByTarget;
    private final HDCSimilarityEngine hdcEngine;
    
    private final double similarityThreshold; // Threshold for creating synapses
    private final double initialWeight; // Initial synaptic weight
    private final double plasticityRate; // Learning rate
    
    public SynapseManager() {
        this(null, 0.7, 0.5, 0.1);
    }
    
    public SynapseManager(HDCSimilarityEngine hdcEngine, 
                        double similarityThreshold,
                        double initialWeight,
                        double plasticityRate) {
        this.concepts = new ConcurrentHashMap<>();
        this.synapsesBySource = new ConcurrentHashMap<>();
        this.synapsesByTarget = new ConcurrentHashMap<>();
        this.hdcEngine = hdcEngine;
        this.similarityThreshold = similarityThreshold;
        this.initialWeight = initialWeight;
        this.plasticityRate = plasticityRate;
    }
    
    /**
     * Register a concept with the synapse manager.
     */
    public void registerConcept(Concept concept) {
        concepts.put(concept.getConceptId(), concept);
    }
    
    /**
     * Create synaptic connections between concepts based on semantic similarity.
     * 
     * Uses HDC vectors to calculate similarity.
     * Creates bidirectional connections if similarity exceeds threshold.
     */
    public void createSynapticConnections(List<Concept> newConcepts) {
        // Register new concepts
        for (Concept concept : newConcepts) {
            registerConcept(concept);
        }
        
        // Create connections with all existing concepts
        for (Concept newConcept : newConcepts) {
            for (Concept existingConcept : concepts.values()) {
                if (newConcept.getConceptId().equals(existingConcept.getConceptId())) {
                    continue; // Skip self-connection
                }
                
                // Calculate semantic similarity using HDC
                double similarity = calculateSimilarity(newConcept, existingConcept);
                
                // Create synapse if similarity exceeds threshold
                if (similarity >= similarityThreshold) {
                    createSynapse(newConcept, existingConcept, similarity);
                }
            }
        }
    }
    
    /**
     * Calculate semantic similarity between two concepts using HDC.
     */
    private double calculateSimilarity(Concept conceptA, Concept conceptB) {
        if (hdcEngine != null) {
            return hdcEngine.calculateSimilarity(
                conceptA.getHdcVector(),
                conceptB.getHdcVector()
            );
        }
        
        // Fallback to simple Hamming distance if HDC engine not available
        BitSet vectorA = conceptA.getHdcVector();
        BitSet vectorB = conceptB.getHdcVector();
        
        BitSet xor = (BitSet) vectorA.clone();
        xor.xor(vectorB);
        
        int hammingDistance = xor.cardinality();
        int dimension = 16384; // HDC dimension
        
        return 1.0 - (double) hammingDistance / dimension;
    }
    
    /**
     * Create a synapse between two concepts.
     */
    private void createSynapse(Concept source, Concept target, double similarity) {
        // Check if synapse already exists
        if (synapseExists(source.getConceptId(), target.getConceptId())) {
            return;
        }
        
        // Determine neurotransmitter based on categories
        Neurotransmitter neurotransmitter = determineNeurotransmitter(source, target);
        
        // Create synapse with weight based on similarity
        double weight = initialWeight * similarity;
        Synapse synapse = new Synapse(
            source.getConceptId(),
            target.getConceptId(),
            weight,
            neurotransmitter
        );
        
        // Store synapse
        synapsesBySource.computeIfAbsent(source.getConceptId(), k -> new ArrayList<>()).add(synapse);
        synapsesByTarget.computeIfAbsent(target.getConceptId(), k -> new ArrayList<>()).add(synapse);
    }
    
    /**
     * Determine neurotransmitter based on concept categories.
     */
    private Neurotransmitter determineNeurotransmitter(Concept source, Concept target) {
        // Simple heuristic based on categories
        String sourceCategory = source.getCategory().toLowerCase();
        String targetCategory = target.getCategory().toLowerCase();
        
        // Sensory to interneuron: excitatory (acetylcholine)
        if (sourceCategory.contains("sensory") || sourceCategory.contains("input")) {
            return Neurotransmitter.ACETYLCHOLINE;
        }
        
        // Inhibitory connections: GABA or Glutamate
        if (sourceCategory.contains("inhibitory") || targetCategory.contains("motor")) {
            return Neurotransmitter.GABA;
        }
        
        // Learning/reward: Dopamine
        if (sourceCategory.contains("reward") || sourceCategory.contains("learning")) {
            return Neurotransmitter.DOPAMINE;
        }
        
        // Social behavior: Serotonin
        if (sourceCategory.contains("social") || sourceCategory.contains("communication")) {
            return Neurotransmitter.SEROTONIN;
        }
        
        // High arousal/flight: Octopamine
        if (sourceCategory.contains("arousal") || sourceCategory.contains("flight")) {
            return Neurotransmitter.OCTOPAMINE;
        }
        
        // Default: excitatory
        return Neurotransmitter.ACETYLCHOLINE;
    }
    
    /**
     * Check if a synapse already exists between two concepts.
     */
    private boolean synapseExists(UUID sourceId, UUID targetId) {
        List<Synapse> sourceSynapses = synapsesBySource.get(sourceId);
        if (sourceSynapses == null) {
            return false;
        }
        
        return sourceSynapses.stream()
            .anyMatch(s -> s.getTargetConceptId().equals(targetId));
    }
    
    /**
     * Get all synapses from a source concept.
     */
    public List<Synapse> getOutgoingSynapses(UUID conceptId) {
        return synapsesBySource.getOrDefault(conceptId, Collections.emptyList());
    }
    
    /**
     * Get all synapses to a target concept.
     */
    public List<Synapse> getIncomingSynapses(UUID conceptId) {
        return synapsesByTarget.getOrDefault(conceptId, Collections.emptyList());
    }
    
    /**
     * Get all concepts.
     */
    public Collection<Concept> getAllConcepts() {
        return Collections.unmodifiableCollection(concepts.values());
    }
    
    /**
     * Get total number of concepts.
     */
    public int getConceptCount() {
        return concepts.size();
    }
    
    /**
     * Get total number of synapses.
     */
    public int getSynapseCount() {
        return synapsesBySource.values().stream()
            .mapToInt(List::size)
            .sum();
    }
    
    /**
     * Apply Hebbian learning to all synapses.
     * 
     * Strengthens synapses where pre- and post-synaptic concepts are co-activated.
     */
    public void applyHebbianLearning() {
        for (List<Synapse> synapses : synapsesBySource.values()) {
            for (Synapse synapse : synapses) {
                Concept source = concepts.get(synapse.getSourceConceptId());
                Concept target = concepts.get(synapse.getTargetConceptId());
                
                if (source != null && target != null) {
                    // Use activation count as proxy for activity
                    double preActivity = source.getActivationCount();
                    double postActivity = target.getActivationCount();
                    
                    // Normalize activity
                    preActivity = Math.min(1.0, preActivity / 100.0);
                    postActivity = Math.min(1.0, postActivity / 100.0);
                    
                    synapse.strengthen(preActivity, postActivity);
                }
            }
        }
    }
    
    /**
     * Apply STDP to all synapses based on activation timing.
     */
    public void applySTDP() {
        for (List<Synapse> synapses : synapsesBySource.values()) {
            for (Synapse synapse : synapses) {
                Concept source = concepts.get(synapse.getSourceConceptId());
                Concept target = concepts.get(synapse.getTargetConceptId());
                
                if (source != null && target != null) {
                    if (source.getLastActivated() > 0 && target.getLastActivated() > 0) {
                        synapse.applySTDP(source.getLastActivated(), target.getLastActivated());
                    }
                }
            }
        }
    }
    
    /**
     * Apply homeostatic plasticity to maintain stability.
     */
    public void applyHomeostaticPlasticity() {
        double targetWeight = 1.0;
        double normalizationRate = 0.01;
        
        for (List<Synapse> synapses : synapsesBySource.values()) {
            for (Synapse synapse : synapses) {
                synapse.normalize(targetWeight, normalizationRate);
            }
        }
    }
    
    /**
     * Apply decay to all synapses (forgetting).
     */
    public void applyDecay(double decayRate) {
        for (List<Synapse> synapses : synapsesBySource.values()) {
            for (Synapse synapse : synapses) {
                synapse.applyDecay(decayRate);
            }
        }
    }
    
    /**
     * Prune weak synapses (below threshold).
     */
    public void pruneWeakSynapses(double threshold) {
        for (UUID sourceId : synapsesBySource.keySet()) {
            List<Synapse> synapses = synapsesBySource.get(sourceId);
            synapses.removeIf(s -> {
                if (s.getWeight() < threshold) {
                    // Also remove from target map
                    List<Synapse> targetSynapses = synapsesByTarget.get(s.getTargetConceptId());
                    if (targetSynapses != null) {
                        targetSynapses.remove(s);
                    }
                    return true;
                }
                return false;
            });
        }
    }
}
