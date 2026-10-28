package com.fraymus.omega;

/**
 * Spinal Cord - Low-latency token reflex
 * Author: Vaughn Scott
 */
public class SpinalCord {
    private final Cerebellum bridge;
    
    public SpinalCord(Cerebellum bridge) {
        this.bridge = bridge;
        System.out.println("[CORTEX] Spinal Cord reflex arc initialized");
    }
}
