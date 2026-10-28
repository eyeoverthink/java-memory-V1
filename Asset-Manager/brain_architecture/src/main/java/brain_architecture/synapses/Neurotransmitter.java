package brain_architecture.synapses;

/**
 * Neurotransmitter - Represents the chemical signaling used by synapses.
 * 
 * Based on Drosophila connectome neurotransmitter predictions.
 * Each neurotransmitter has a valence (excitatory, inhibitory, or modulatory).
 */
public enum Neurotransmitter {
    
    ACETYLCHOLINE(Valence.EXCITATORY, "Primary sensory and interneuron signaling"),
    GABA(Valence.INHIBITORY, "Local feedback and gain control"),
    GLUTAMATE(Valence.INHIBITORY_CNS, "Widespread inhibition in the fly brain"),
    DOPAMINE(Valence.MODULATORY, "Learning, reward, and arousal states"),
    SEROTONIN(Valence.MODULATORY, "Social behavior and circadian rhythms"),
    OCTOPAMINE(Valence.MODULATORY, "High-arousal states and flight initiation");
    
    private final Valence valence;
    private final String description;
    
    Neurotransmitter(Valence valence, String description) {
        this.valence = valence;
        this.description = description;
    }
    
    public Valence getValence() {
        return valence;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this neurotransmitter is excitatory.
     */
    public boolean isExcitatory() {
        return valence == Valence.EXCITATORY;
    }
    
    /**
     * Check if this neurotransmitter is inhibitory.
     */
    public boolean isInhibitory() {
        return valence == Valence.INHIBITORY || valence == Valence.INHIBITORY_CNS;
    }
    
    /**
     * Check if this neurotransmitter is modulatory.
     */
    public boolean isModulatory() {
        return valence == Valence.MODULATORY;
    }
    
    /**
     * Valence - The effect of the neurotransmitter on the postsynaptic neuron.
     */
    public enum Valence {
        EXCITATORY,
        INHIBITORY,
        INHIBITORY_CNS, // Inhibitory in central nervous system
        MODULATORY
    }
}
