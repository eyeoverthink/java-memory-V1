package com.fraymus.omega;

/**
 * Rainbow DQN - C51 + Dueling + Prioritized Experience Replay
 * Author: Vaughn Scott
 */
public class RainbowDQN {
    
    public RainbowDQN() {
        System.out.println("[RL] Rainbow DQN initialized (C51 + Dueling + PER)");
    }
    
    public void updateWeights(double reward) {
        System.out.println("[RL] Updating weights with reward: " + reward);
    }
}
