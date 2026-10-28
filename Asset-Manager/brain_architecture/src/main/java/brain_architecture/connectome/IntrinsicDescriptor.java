package brain_architecture.connectome;

/**
 * IntrinsicDescriptor - Trainable parameter per neuron that captures properties
 * not in the static connectome structure.
 * 
 * Inspired by the Drosophila connectome where each neuron has intrinsic
 * properties like excitability, gain, and adaptation that are optimized
 * via learning.
 */
public class IntrinsicDescriptor {
    
    private double excitability; // Base activation threshold
    private double gain; // Amplification factor
    private double adaptation; // Adaptation rate
    private double bias; // Constant bias
    private double learningRate;
    
    public IntrinsicDescriptor() {
        this.excitability = 1.0;
        this.gain = 1.0;
        this.adaptation = 0.1;
        this.bias = 0.0;
        this.learningRate = 0.01;
    }
    
    public IntrinsicDescriptor(double excitability, double gain, double adaptation, double bias) {
        this.excitability = excitability;
        this.gain = gain;
        this.adaptation = adaptation;
        this.bias = bias;
        this.learningRate = 0.01;
    }
    
    public double getExcitability() {
        return excitability;
    }
    
    public void setExcitability(double excitability) {
        this.excitability = Math.max(0.1, Math.min(10.0, excitability));
    }
    
    public double getGain() {
        return gain;
    }
    
    public void setGain(double gain) {
        this.gain = Math.max(0.1, Math.min(10.0, gain));
    }
    
    public double getAdaptation() {
        return adaptation;
    }
    
    public void setAdaptation(double adaptation) {
        this.adaptation = Math.max(0.0, Math.min(1.0, adaptation));
    }
    
    public double getBias() {
        return bias;
    }
    
    public void setBias(double bias) {
        this.bias = Math.max(-5.0, Math.min(5.0, bias));
    }
    
    public double getLearningRate() {
        return learningRate;
    }
    
    public void setLearningRate(double learningRate) {
        this.learningRate = Math.max(0.001, Math.min(0.5, learningRate));
    }
    
    /**
     * Update intrinsic descriptor based on error signal (reinforcement learning).
     */
    public void update(double error) {
        // Update parameters based on error gradient
        this.excitability += error * learningRate * 0.1;
        this.gain += error * learningRate * 0.1;
        this.bias += error * learningRate;
        
        // Clamp values to valid ranges
        this.excitability = Math.max(0.1, Math.min(10.0, this.excitability));
        this.gain = Math.max(0.1, Math.min(10.0, this.gain));
        this.bias = Math.max(-5.0, Math.min(5.0, this.bias));
    }
    
    /**
     * Apply adaptation to reduce repeated firing (fatigue).
     */
    public void applyAdaptation(double activation) {
        this.excitability *= (1.0 - adaptation * activation);
        this.excitability = Math.max(0.1, this.excitability);
    }
    
    /**
     * Reset adaptation (recovery from fatigue).
     */
    public void resetAdaptation() {
        this.excitability = 1.0;
    }
    
    /**
     * Compute effective input after applying intrinsic descriptor.
     */
    public double processInput(double input) {
        return (input + bias) * gain * excitability;
    }
    
    @Override
    public String toString() {
        return "IntrinsicDescriptor{" +
            "excitability=" + String.format("%.3f", excitability) +
            ", gain=" + String.format("%.3f", gain) +
            ", adaptation=" + String.format("%.3f", adaptation) +
            ", bias=" + String.format("%.3f", bias) +
            ", learningRate=" + String.format("%.3f", learningRate) +
            '}';
    }
}
