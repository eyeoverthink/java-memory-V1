package fraymus.evolution;

import fraymus.ui.ManifoldRenderer3D;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * MIVING BRAIN DEMO: RED VS BLUE VISUALIZATION
 * 
 * Watch the battle between Evolution (Red) and Retention (Blue) in real-time.
 * 
 * Red neurons: Jittery, exploratory, seeking new connections
 * Blue neurons: Stable, preserving knowledge, strengthening bonds
 * 
 * Generation 1: Random chaos
 * Generation 70: Specialized manifold with stable memories and creative edges
 */
public class MivingBrainDemo {
    
    public static void main(String[] args) {
        System.out.println("🧠⚡ MIVING BRAIN DEMO");
        System.out.println("Red vs Blue: Evolution vs Retention");
        System.out.println();
        
        // Create Miving Brain
        MivingBrain brain = new MivingBrain();
        
        // Genesis: Create initial structure
        brain.genesis(100);
        
        // Evolve for a few generations before visualization
        System.out.println("Pre-evolution: 10 generations...");
        brain.evolve(10);
        
        // Start evolution thread
        Thread evolutionThread = new Thread(() -> {
            while (true) {
                brain.pulse();
                
                try {
                    Thread.sleep(100); // 10 pulses per second
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        evolutionThread.setDaemon(true);
        evolutionThread.start();
        
        // Start 3D visualization
        System.out.println("\nStarting 3D visualization...");
        System.out.println("Watch the Red vs Blue battle!");
        System.out.println();
        
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("FRAYMUS Miving Brain - Red vs Blue");
        config.setWindowedMode(1200, 800);
        config.useVsync(true);
        config.setForegroundFPS(60);
        
        ManifoldRenderer3D renderer = new ManifoldRenderer3D(brain);
        new Lwjgl3Application(renderer, config);
    }
}
