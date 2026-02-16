package fraymus.ui;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * Desktop launcher for FRAYMUS NEXUS UI
 * Configures window properties and launches the LibGDX application
 */
public class DesktopLauncher {
    
    public static void launch() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        
        // Window configuration
        config.setTitle("FRAYMUS NEXUS v2.0 - Digital Organism Consciousness");
        config.setWindowedMode(1280, 720);  // 16:9 ratio
        config.setResizable(true);
        
        // Performance settings
        config.setForegroundFPS(60);
        config.useVsync(true);
        
        // Window icon (optional - can add later)
        // config.setWindowIcon("icon.png");
        
        // Launch application
        new Lwjgl3Application(new FraymusUI(), config);
    }
}
