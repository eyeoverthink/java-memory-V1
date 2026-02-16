package fraymus.ui;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import fraymus.brain.ManifoldBrain;
import fraymus.senses.BioSymbiosis;

import javax.swing.*;
import java.awt.*;

/**
 * ARENA WINDOW 3D: EMBEDDED LIBGDX WINDOW
 * 
 * Creates a window in the Java app that embeds the 3D manifold renderer.
 * Replaces the 1D-2D boilerplate arena with multi-dimensional visualization.
 * 
 * Shows what's actually happening:
 * - Manifold brain structure in 3D
 * - Reasoning paths flowing through nodes
 * - Bio-feedback color modulation
 * - Real-time knowledge connections
 */
public class ArenaWindow3D extends JFrame {
    
    private ManifoldRenderer3D renderer;
    private Lwjgl3Application gdxApp;
    private JPanel controlPanel;
    private JLabel statsLabel;
    
    public ArenaWindow3D(ManifoldBrain brain, BioSymbiosis bioSymbiosis) {
        super("FRAYMUS Arena - 3D Manifold Visualization");
        
        // Create renderer
        renderer = new ManifoldRenderer3D(brain, bioSymbiosis);
        
        // Setup window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());
        
        // Create control panel
        createControlPanel();
        
        // Start LibGDX in separate thread
        startLibGDX();
        
        // Center window
        setLocationRelativeTo(null);
        
        System.out.println("🌊⚡ ARENA WINDOW 3D INITIALIZED");
    }
    
    /**
     * Create control panel with buttons and stats
     */
    private void createControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setBackground(Color.BLACK);
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Stats label
        statsLabel = new JLabel("Initializing...");
        statsLabel.setForeground(Color.CYAN);
        statsLabel.setFont(new Font("Courier New", Font.BOLD, 14));
        controlPanel.add(statsLabel);
        
        // Toggle rotation button
        JButton rotateBtn = createButton("Toggle Rotation");
        rotateBtn.addActionListener(e -> {
            if (renderer != null) {
                renderer.toggleAutoRotate();
            }
        });
        controlPanel.add(rotateBtn);
        
        // Simulate reasoning button
        JButton reasonBtn = createButton("Simulate Reasoning");
        reasonBtn.addActionListener(e -> {
            if (renderer != null) {
                // Simulate a reasoning path
                java.util.List<String> path = java.util.Arrays.asList(
                    "Concept_0", "Concept_5", "Concept_10", "Concept_15"
                );
                renderer.visualizeReasoningPath(path);
            }
        });
        controlPanel.add(reasonBtn);
        
        // Add to window
        add(controlPanel, BorderLayout.SOUTH);
        
        // Update stats periodically
        Timer statsTimer = new Timer(1000, e -> updateStats());
        statsTimer.start();
    }
    
    /**
     * Create styled button
     */
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.CYAN);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Courier New", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
        return btn;
    }
    
    /**
     * Start LibGDX application
     */
    private void startLibGDX() {
        new Thread(() -> {
            Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
            config.setTitle("FRAYMUS Manifold Brain");
            config.setWindowedMode(1200, 700);
            config.useVsync(true);
            config.setForegroundFPS(60);
            
            gdxApp = new Lwjgl3Application(renderer, config);
        }).start();
    }
    
    /**
     * Update statistics display
     */
    private void updateStats() {
        if (renderer != null) {
            statsLabel.setText("🧠⚡ " + renderer.getStats());
        }
    }
    
    /**
     * Show window
     */
    public void show() {
        setVisible(true);
    }
    
    /**
     * Close window and cleanup
     */
    public void close() {
        if (gdxApp != null) {
            gdxApp.exit();
        }
        dispose();
    }
    
    /**
     * Main method for standalone testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create dummy brain and bio-symbiosis for testing
            ManifoldBrain brain = new ManifoldBrain(512);
            BioSymbiosis bioSymbiosis = new BioSymbiosis();
            
            ArenaWindow3D window = new ArenaWindow3D(brain, bioSymbiosis);
            window.show();
            
            System.out.println("🌊⚡ Arena Window 3D - Standalone Test Mode");
        });
    }
}
