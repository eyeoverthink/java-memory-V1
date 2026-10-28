package brain_architecture.connectome;

import brain_architecture.synapses.Neurotransmitter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

/**
 * Unit tests for ConnectomeSynapse.
 */
public class ConnectomeSynapseTest {
    
    private final UUID sourceId = UUID.randomUUID();
    private final UUID targetId = UUID.randomUUID();
    
    @Test
    public void testConstructor() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        
        assertEquals(sourceId, synapse.getSourceNeuronId());
        assertEquals(targetId, synapse.getTargetNeuronId());
        assertEquals(1.0, synapse.getWeight(), 0.001);
        assertEquals(Neurotransmitter.ACETYLCHOLINE, synapse.getNeurotransmitter());
        assertEquals(0, synapse.getActivationCount());
        assertNotNull(synapse.getSynapseId());
    }
    
    @Test
    public void testGetSynapseId() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.GABA);
        
        assertNotNull(synapse.getSynapseId());
    }
    
    @Test
    public void testSetWeight() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        synapse.setWeight(1.5);
        
        assertEquals(1.5, synapse.getWeight(), 0.001);
    }
    
    @Test
    public void testSetWeightClampsToMax() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        synapse.setWeight(10.0);
        
        assertEquals(2.0, synapse.getWeight(), 0.001);
    }
    
    @Test
    public void testSetWeightClampsToMin() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        synapse.setWeight(-1.0);
        
        assertEquals(0.0, synapse.getWeight(), 0.001);
    }
    
    @Test
    public void testSetPlasticityRate() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        synapse.setPlasticityRate(0.5);
        
        assertEquals(0.5, synapse.getPlasticityRate(), 0.001);
    }
    
    @Test
    public void testSetPlasticityRateClamps() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        synapse.setPlasticityRate(2.0);
        assertEquals(1.0, synapse.getPlasticityRate(), 0.001);
        
        synapse.setPlasticityRate(-0.5);
        assertEquals(0.0, synapse.getPlasticityRate(), 0.001);
    }
    
    @Test
    public void testActivate() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        
        synapse.activate();
        
        assertEquals(1, synapse.getActivationCount());
        assertTrue(synapse.getLastActivated() > 0);
    }
    
    @Test
    public void testActivateMultipleTimes() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        
        synapse.activate();
        synapse.activate();
        synapse.activate();
        
        assertEquals(3, synapse.getActivationCount());
    }
    
    @Test
    public void testStrengthen() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        double originalWeight = synapse.getWeight();
        
        synapse.strengthen(1.0, 1.0);
        
        assertTrue(synapse.getWeight() > originalWeight);
    }
    
    @Test
    public void testStrengthenClampsToMax() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.9, Neurotransmitter.ACETYLCHOLINE);
        
        synapse.strengthen(1.0, 1.0);
        
        assertEquals(2.0, synapse.getWeight(), 0.001);
    }
    
    @Test
    public void testWeaken() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        double originalWeight = synapse.getWeight();
        
        synapse.weaken(0.3);
        
        assertTrue(synapse.getWeight() < originalWeight);
    }
    
    @Test
    public void testWeakenClampsToMin() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 0.2, Neurotransmitter.ACETYLCHOLINE);
        
        synapse.weaken(0.5);
        
        assertEquals(0.0, synapse.getWeight(), 0.001);
    }
    
    @Test
    public void testApplySTDPPositiveDelta() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        double originalWeight = synapse.getWeight();
        
        // Pre before post: should strengthen
        synapse.applySTDP(10.0, 5.0);
        
        assertTrue(synapse.getWeight() > originalWeight);
    }
    
    @Test
    public void testApplySTDPNegativeDelta() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        double originalWeight = synapse.getWeight();
        
        // Post before pre: should weaken
        synapse.applySTDP(5.0, 10.0);
        
        assertTrue(synapse.getWeight() < originalWeight);
    }
    
    @Test
    public void testNormalize() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 0.5, Neurotransmitter.ACETYLCHOLINE);
        
        synapse.normalize(1.0, 0.5);
        
        // Should move toward target
        assertTrue(synapse.getWeight() > 0.5);
        assertTrue(synapse.getWeight() < 1.0);
    }
    
    @Test
    public void testApplyDecay() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        double originalWeight = synapse.getWeight();
        
        synapse.applyDecay(0.1);
        
        assertTrue(synapse.getWeight() < originalWeight);
    }
    
    @Test
    public void testApplyDecayToZero() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 0.1, Neurotransmitter.ACETYLCHOLINE);
        
        synapse.applyDecay(1.0);
        
        assertEquals(0.0, synapse.getWeight(), 0.001);
    }
    
    @Test
    public void testGetEffectiveWeightExcitatory() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        
        assertEquals(1.0, synapse.getEffectiveWeight(), 0.001);
    }
    
    @Test
    public void testGetEffectiveWeightInhibitory() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.GABA);
        
        assertEquals(-1.0, synapse.getEffectiveWeight(), 0.001);
    }
    
    @Test
    public void testGetEffectiveWeightGlutamate() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.GLUTAMATE);
        
        assertEquals(-1.0, synapse.getEffectiveWeight(), 0.001);
    }
    
    @Test
    public void testToString() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        String str = synapse.toString();
        
        assertTrue(str.contains("source"));
        assertTrue(str.contains("target"));
        assertTrue(str.contains("ACETYLCHOLINE"));
    }
    
    @Test
    public void testEqualsSameObject() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        
        assertEquals(synapse, synapse);
    }
    
    @Test
    public void testEqualsDifferentObject() {
        ConnectomeSynapse synapse1 = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        ConnectomeSynapse synapse2 = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        
        assertNotEquals(synapse1, synapse2);
    }
    
    @Test
    public void testHashCode() {
        ConnectomeSynapse synapse = new ConnectomeSynapse(
            sourceId, targetId, 1.0, Neurotransmitter.ACETYLCHOLINE);
        
        assertNotNull(synapse.hashCode());
    }
}
