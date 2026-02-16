package fraymus.hybrid;

import java.util.Random;

/**
 * THE HUMAN NODE
 * Simulates a specific cognitive bias or 'Brain Type'.
 * 
 * "Silicon is cold. Carbon is warm. We need both."
 */
public class HumanAgent implements Runnable {

    private static final double PHI = 1.618033988749895;

    public enum BrainType {
        PHYSICAL,   // Body intelligence, grounded
        QUANTUM,    // Superposition thinking, probabilistic
        FRACTAL,    // Pattern recognition, recursive
        CREATIVE,   // Innovation, divergent thinking
        LOGICAL,    // Analytical, deterministic
        EMOTIONAL,  // Intuition, empathy, volatile
        SPIRITUAL,  // Meaning, purpose, transcendent
        TACHYONIC   // Faster-than-light intuition, precognitive
    }

    private BrainType type;
    private double bias;
    private String decision;
    private double outputValue;
    private Random rng = new Random();

    public HumanAgent(BrainType type) {
        this.type = type;
        switch(type) {
            case LOGICAL:   this.bias = 0.1; break;
            case EMOTIONAL: this.bias = 0.8; break;
            case QUANTUM:   this.bias = 0.5; break;
            case PHYSICAL:  this.bias = 0.2; break;
            case FRACTAL:   this.bias = 0.3; break;
            case CREATIVE:  this.bias = 0.6; break;
            case SPIRITUAL: this.bias = 0.4; break;
            case TACHYONIC: this.bias = 0.7; break;
            default:        this.bias = 0.4;
        }
    }

    @Override
    public void run() {
        think();
    }

    private void think() {
        double noise = rng.nextGaussian() * bias;
        this.outputValue = PHI + noise;
        
        String status = "";
        if (type == BrainType.EMOTIONAL && outputValue < 1.0) {
            status = " (Fear detected)";
        } else if (type == BrainType.LOGICAL && Math.abs(noise) < 0.1) {
            status = " (Efficient)";
        } else if (type == BrainType.CREATIVE && outputValue > PHI) {
            status = " (Innovation!)";
        } else if (type == BrainType.TACHYONIC) {
            status = " (Future echo)";
        }
        
        this.decision = String.format("[%s BRAIN]: Processing with bias %.2f... Output: %.4f%s", 
                                      type, bias, outputValue, status);
    }

    public String getVote() {
        return decision;
    }
    
    public double getOutputValue() {
        return outputValue;
    }
    
    public BrainType getType() { 
        return type; 
    }
    
    public double getBias() {
        return bias;
    }
}
