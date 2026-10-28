package brain_architecture.connectome;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

/**
 * Unit tests for Neuron.
 */
public class NeuronTest {
    
    @Test
    public void testConstructorWithType() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        
        assertEquals(Neuron.NeuronType.EXCITATORY, neuron.getType());
        assertNotNull(neuron.getNeuronId());
        assertEquals(0.0, neuron.getMembranePotential(), 0.001);
        assertEquals(1.0, neuron.getThreshold(), 0.001);
        assertEquals(-1.0, neuron.getLastSpikeTime(), 0.001);
        assertEquals(0, neuron.getSpikeCount());
    }
    
    @Test
    public void testConstructorWithNeuropil() {
        Neuropil neuropil = new Neuropil("AL", "Antennal Lobe", Neuropil.NeuropilType.SENSORY);
        Neuron neuron = new Neuron(Neuron.NeuronType.INHIBITORY, neuropil);
        
        assertEquals(Neuron.NeuronType.INHIBITORY, neuron.getType());
        assertEquals(neuropil, neuron.getNeuropil());
    }
    
    @Test
    public void testGetNeuronId() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        UUID id = neuron.getNeuronId();
        
        assertNotNull(id);
    }
    
    @Test
    public void testGetType() {
        Neuron neuron = new Neuron(Neuron.NeuronType.MODULATORY);
        
        assertEquals(Neuron.NeuronType.MODULATORY, neuron.getType());
    }
    
    @Test
    public void testSetNeuropil() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuropil neuropil = new Neuropil("MB", "Mushroom Body", Neuropil.NeuropilType.MEMORY);
        
        neuron.setNeuropil(neuropil);
        
        assertEquals(neuropil, neuron.getNeuropil());
    }
    
    @Test
    public void testGetIntrinsicDescriptor() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        
        assertNotNull(neuron.getIntrinsicDescriptor());
    }
    
    @Test
    public void testSetIntrinsicDescriptor() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor(2.0, 1.5, 0.2, 0.5);
        
        neuron.setIntrinsicDescriptor(descriptor);
        
        assertEquals(descriptor, neuron.getIntrinsicDescriptor());
    }
    
    @Test
    public void testSetMembranePotential() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        neuron.setMembranePotential(0.5);
        
        assertEquals(0.5, neuron.getMembranePotential(), 0.001);
    }
    
    @Test
    public void testSetThreshold() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        neuron.setThreshold(1.5);
        
        assertEquals(1.5, neuron.getThreshold(), 0.001);
    }
    
    @Test
    public void testSetThresholdClampsToMin() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        neuron.setThreshold(0.0);
        
        assertEquals(0.1, neuron.getThreshold(), 0.001);
    }
    
    @Test
    public void testSetLeakRate() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        neuron.setLeakRate(0.2);
        
        assertEquals(0.2, neuron.getLeakRate(), 0.001);
    }
    
    @Test
    public void testSetLeakRateClamps() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        neuron.setLeakRate(2.0);
        assertEquals(1.0, neuron.getLeakRate(), 0.001);
        
        neuron.setLeakRate(-0.5);
        assertEquals(0.0, neuron.getLeakRate(), 0.001);
    }
    
    @Test
    public void testSetResetPotential() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        neuron.setResetPotential(-0.5);
        
        assertEquals(-0.5, neuron.getResetPotential(), 0.001);
    }
    
    @Test
    public void testActivateBelowThreshold() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        
        boolean fired = neuron.activate(0.5, 0.0);
        
        assertFalse(fired);
        assertEquals(0, neuron.getSpikeCount());
    }
    
    @Test
    public void testActivateAboveThreshold() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        
        boolean fired = neuron.activate(1.5, 0.0);
        
        assertTrue(fired);
        assertEquals(1, neuron.getSpikeCount());
        assertEquals(0.0, neuron.getLastSpikeTime(), 0.001);
    }
    
    @Test
    public void testActivateWithLeak() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        neuron.setLeakRate(0.5);
        
        neuron.activate(1.0, 0.0); // Potential = 1.0
        neuron.activate(0.0, 1.0); // Leaked to 0.5, no new input
        
        assertTrue(neuron.getMembranePotential() < 1.0);
    }
    
    @Test
    public void testActivateWithGain() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        neuron.getIntrinsicDescriptor().setGain(2.0);
        
        boolean fired = neuron.activate(0.6, 0.0); // 0.6 * 2.0 = 1.2 > threshold
        
        assertTrue(fired);
    }
    
    @Test
    public void testReset() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        neuron.activate(2.0, 0.0);
        neuron.setMembranePotential(0.5);
        
        neuron.reset();
        
        assertEquals(0.0, neuron.getMembranePotential(), 0.001);
        assertEquals(-1.0, neuron.getLastSpikeTime(), 0.001);
        assertEquals(0, neuron.getSpikeCount());
        assertEquals(1.0, neuron.getIntrinsicDescriptor().getExcitability(), 0.001);
    }
    
    @Test
    public void testUpdateIntrinsicDescriptor() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        double originalBias = neuron.getIntrinsicDescriptor().getBias();
        
        neuron.updateIntrinsicDescriptor(0.5);
        
        assertNotEquals(originalBias, neuron.getIntrinsicDescriptor().getBias());
    }
    
    @Test
    public void testGetFiringRate() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        
        neuron.activate(2.0, 0.0);
        neuron.activate(2.0, 1.0);
        neuron.activate(2.0, 2.0);
        
        double rate = neuron.getFiringRate(3.0);
        
        assertEquals(1.0, rate, 0.001); // 3 spikes / 3 time units
    }
    
    @Test
    public void testGetFiringRateNoSpikes() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        
        double rate = neuron.getFiringRate(1.0);
        
        assertEquals(0.0, rate, 0.001);
    }
    
    @Test
    public void testGetFiringRateZeroTimeWindow() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        neuron.activate(2.0, 0.0);
        
        double rate = neuron.getFiringRate(0.0);
        
        assertEquals(0.0, rate, 0.001);
    }
    
    @Test
    public void testNeuronTypeExcitatory() {
        Neuron.NeuronType type = Neuron.NeuronType.EXCITATORY;
        
        assertEquals("Excitatory neuron", type.getDescription());
    }
    
    @Test
    public void testNeuronTypeInhibitory() {
        Neuron.NeuronType type = Neuron.NeuronType.INHIBITORY;
        
        assertEquals("Inhibitory neuron", type.getDescription());
    }
    
    @Test
    public void testNeuronTypeModulatory() {
        Neuron.NeuronType type = Neuron.NeuronType.MODULATORY;
        
        assertEquals("Modulatory neuron", type.getDescription());
    }
    
    @Test
    public void testToString() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        String str = neuron.toString();
        
        assertTrue(str.contains("EXCITATORY"));
        assertTrue(str.contains("potential"));
    }
    
    @Test
    public void testEqualsSameObject() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        
        assertEquals(neuron, neuron);
    }
    
    @Test
    public void testEqualsDifferentObject() {
        Neuron neuron1 = new Neuron(Neuron.NeuronType.EXCITATORY);
        Neuron neuron2 = new Neuron(Neuron.NeuronType.EXCITATORY);
        
        assertNotEquals(neuron1, neuron2);
    }
    
    @Test
    public void testHashCode() {
        Neuron neuron = new Neuron(Neuron.NeuronType.EXCITATORY);
        
        assertNotNull(neuron.hashCode());
    }
}
