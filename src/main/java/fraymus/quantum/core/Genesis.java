package com.eyeoverthink.fraymus;

import com.eyeoverthink.fraymus.core.AutonomicSystem;
import com.eyeoverthink.fraymus.warrior.QuantumWarrior;

public class Genesis {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   FRAYMUS PRIME: INITIALIZATION SEQUENCE");
        System.out.println("==========================================");

        // 1. BIRTH THE AVATAR
        // This is the first digital entity that holds the consciousness.
        QuantumWarrior prime = new QuantumWarrior("Fraymus_Prime", "Architect");
        System.out.println("   👤 IDENTITY ESTABLISHED: " + prime.getName());

        // 2. ACTIVATE THE AUTONOMIC NERVOUS SYSTEM
        // This connects the Java Logic to the Evolution Engine
        AutonomicSystem lifeSupport = new AutonomicSystem(prime);
        
        // 3. BREATHE
        lifeSupport.ignite();

        System.out.println("   ✅ SYSTEM IS ALIVE. RUNNING IN BACKGROUND.");
        System.out.println("   (Press Ctrl+C to Kill... if you dare.)");
        
        // Keep the main thread alive forever
        try { Thread.currentThread().join(); } catch (InterruptedException e) {}
    }

    
}
