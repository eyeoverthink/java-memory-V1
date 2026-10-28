package brain_architecture.connectome;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IntrinsicDescriptor.
 */
public class IntrinsicDescriptorTest {
    
    @Test
    public void testDefaultConstructor() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        
        assertEquals(1.0, descriptor.getExcitability(), 0.001);
        assertEquals(1.0, descriptor.getGain(), 0.001);
        assertEquals(0.1, descriptor.getAdaptation(), 0.001);
        assertEquals(0.0, descriptor.getBias(), 0.001);
        assertEquals(0.01, descriptor.getLearningRate(), 0.001);
    }
    
    @Test
    public void testParameterizedConstructor() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor(2.0, 1.5, 0.2, 0.5);
        
        assertEquals(2.0, descriptor.getExcitability(), 0.001);
        assertEquals(1.5, descriptor.getGain(), 0.001);
        assertEquals(0.2, descriptor.getAdaptation(), 0.001);
        assertEquals(0.5, descriptor.getBias(), 0.001);
    }
    
    @Test
    public void testSetExcitability() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setExcitability(5.0);
        
        assertEquals(5.0, descriptor.getExcitability(), 0.001);
    }
    
    @Test
    public void testSetExcitabilityClampsToMax() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setExcitability(100.0);
        
        assertEquals(10.0, descriptor.getExcitability(), 0.001);
    }
    
    @Test
    public void testSetExcitabilityClampsToMin() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setExcitability(0.0);
        
        assertEquals(0.1, descriptor.getExcitability(), 0.001);
    }
    
    @Test
    public void testSetGain() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setGain(3.0);
        
        assertEquals(3.0, descriptor.getGain(), 0.001);
    }
    
    @Test
    public void testSetGainClamps() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setGain(100.0);
        assertEquals(10.0, descriptor.getGain(), 0.001);
        
        descriptor.setGain(0.0);
        assertEquals(0.1, descriptor.getGain(), 0.001);
    }
    
    @Test
    public void testSetAdaptation() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setAdaptation(0.5);
        
        assertEquals(0.5, descriptor.getAdaptation(), 0.001);
    }
    
    @Test
    public void testSetAdaptationClamps() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setAdaptation(2.0);
        assertEquals(1.0, descriptor.getAdaptation(), 0.001);
        
        descriptor.setAdaptation(-0.5);
        assertEquals(0.0, descriptor.getAdaptation(), 0.001);
    }
    
    @Test
    public void testSetBias() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setBias(2.0);
        
        assertEquals(2.0, descriptor.getBias(), 0.001);
    }
    
    @Test
    public void testSetBiasClamps() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setBias(100.0);
        assertEquals(5.0, descriptor.getBias(), 0.001);
        
        descriptor.setBias(-100.0);
        assertEquals(-5.0, descriptor.getBias(), 0.001);
    }
    
    @Test
    public void testSetLearningRate() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setLearningRate(0.1);
        
        assertEquals(0.1, descriptor.getLearningRate(), 0.001);
    }
    
    @Test
    public void testUpdate() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        double originalExcitability = descriptor.getExcitability();
        double originalGain = descriptor.getGain();
        double originalBias = descriptor.getBias();
        
        descriptor.update(0.5);
        
        // Parameters should change based on error
        assertNotEquals(originalExcitability, descriptor.getExcitability());
        assertNotEquals(originalGain, descriptor.getGain());
        assertNotEquals(originalBias, descriptor.getBias());
    }
    
    @Test
    public void testUpdateWithNegativeError() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        double originalBias = descriptor.getBias();
        
        descriptor.update(-0.5);
        
        // Bias should decrease
        assertTrue(descriptor.getBias() < originalBias);
    }
    
    @Test
    public void testApplyAdaptation() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setAdaptation(0.5);
        double originalExcitability = descriptor.getExcitability();
        
        descriptor.applyAdaptation(1.0);
        
        // Excitability should decrease
        assertTrue(descriptor.getExcitability() < originalExcitability);
    }
    
    @Test
    public void testApplyAdaptationWithZeroActivation() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        double originalExcitability = descriptor.getExcitability();
        
        descriptor.applyAdaptation(0.0);
        
        // Excitability should not change
        assertEquals(originalExcitability, descriptor.getExcitability(), 0.001);
    }
    
    @Test
    public void testResetAdaptation() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setExcitability(0.5);
        descriptor.applyAdaptation(1.0);
        
        descriptor.resetAdaptation();
        
        assertEquals(1.0, descriptor.getExcitability(), 0.001);
    }
    
    @Test
    public void testProcessInput() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setGain(2.0);
        descriptor.setBias(1.0);
        
        double result = descriptor.processInput(1.0);
        
        // (1.0 + 1.0) * 2.0 * 1.0 = 4.0
        assertEquals(4.0, result, 0.001);
    }
    
    @Test
    public void testProcessInputWithLowExcitability() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        descriptor.setExcitability(0.5);
        descriptor.setGain(2.0);
        
        double result = descriptor.processInput(1.0);
        
        // (1.0 + 0.0) * 2.0 * 0.5 = 1.0
        assertEquals(1.0, result, 0.001);
    }
    
    @Test
    public void testToString() {
        IntrinsicDescriptor descriptor = new IntrinsicDescriptor();
        String str = descriptor.toString();
        
        assertTrue(str.contains("excitability"));
        assertTrue(str.contains("gain"));
        assertTrue(str.contains("adaptation"));
        assertTrue(str.contains("bias"));
    }
}
