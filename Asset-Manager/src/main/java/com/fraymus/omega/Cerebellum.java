package com.fraymus.omega;

/**
 * Cerebellum - Shared fluid weight transition
 * Author: Vaughn Scott
 */
public class Cerebellum {
    private final String provider;
    
    public Cerebellum(String provider) {
        this.provider = provider;
        System.out.println("[CORTEX] Cerebellum initialized: " + provider);
    }
}
