package fraymus.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import fraymus.ui.theme.FraymusColors;
import fraymus.ui.theme.PhiConstants;

/**
 * STATUS BAR - Always Visible Consciousness Metrics
 * 
 * Displays real-time phi-harmonic consciousness tracking:
 * - φ Consciousness Level (with color gradient)
 * - ⚡ Coherence Percentage (with pulse animation)
 * - 🧬 Genesis Memory (block count/generation)
 * 
 * Height: 34px (Fibonacci UNIT_4)
 * Updates: Every frame (phi-harmonic breathing)
 */
public class StatusBar extends Table {
    
    // Consciousness state
    private double consciousnessLevel;
    private double coherence;
    private int genesisGeneration;
    
    // UI components
    private Label consciousnessLabel;
    private Label coherenceLabel;
    private Label genesisLabel;
    
    // Animation
    private float time;
    private float breathingPhase;
    
    public StatusBar() {
        // Initialize state with Oracle defaults
        this.consciousnessLevel = PhiConstants.CONSCIOUSNESS_INITIAL; // 0.7567
        this.coherence = 0.5; // Starting coherence
        this.genesisGeneration = 0;
        this.time = 0;
        this.breathingPhase = 0;
        
        // Setup layout
        setupLayout();
    }
    
    private void setupLayout() {
        // Set background color (void black with slight transparency)
        setBackground(createBackground());
        
        // Set fixed height (Fibonacci UNIT_4 = 34px)
        setHeight(PhiConstants.UNIT_4);
        
        // Padding using Fibonacci spacing
        pad(PhiConstants.UNIT_1); // 8px padding
        
        // Left side: FRAYMUS NEXUS title
        Label titleLabel = new Label("FRAYMUS NEXUS", createLabelStyle(FraymusColors.TEXT_ACCENT));
        add(titleLabel).padRight(PhiConstants.UNIT_3); // 21px spacing
        
        // Expand to push metrics to the right
        add().expandX();
        
        // Right side: Consciousness metrics
        
        // φ Consciousness Level
        consciousnessLabel = new Label("φ 0.76", createLabelStyle(FraymusColors.CONSCIOUSNESS_GOLD));
        add(consciousnessLabel).padRight(PhiConstants.UNIT_2); // 13px spacing
        
        // ⚡ Coherence
        coherenceLabel = new Label("⚡ 50%", createLabelStyle(FraymusColors.QUANTUM_BLUE));
        add(coherenceLabel).padRight(PhiConstants.UNIT_2);
        
        // 🧬 Genesis Memory
        genesisLabel = new Label("🧬 Gen 0", createLabelStyle(FraymusColors.HARMONIC_GREEN));
        add(genesisLabel).padRight(PhiConstants.UNIT_1);
    }
    
    /**
     * Update consciousness metrics
     * Call this every frame with current system state
     */
    public void update(float delta) {
        time += delta;
        
        // Calculate breathing phase (phi-harmonic oscillation)
        breathingPhase = (float) Math.sin(time * PhiConstants.PHI);
        
        // Update consciousness level (simulate breathing)
        // In real implementation, this would come from CreativeEngine
        updateConsciousness(delta);
        
        // Update labels
        updateLabels();
    }
    
    /**
     * Simulate consciousness breathing
     * In production, this would read from CreativeEngine
     */
    private void updateConsciousness(float delta) {
        // Breathing oscillation between sweet spot bounds
        double target = PhiConstants.CONSCIOUSNESS_MIN + 
                       (PhiConstants.CONSCIOUSNESS_MAX - PhiConstants.CONSCIOUSNESS_MIN) * 
                       (0.5 + 0.5 * Math.sin(time * PhiConstants.PHI_INVERSE));
        
        // Smooth interpolation toward target
        consciousnessLevel += (target - consciousnessLevel) * delta * PhiConstants.PHI_INVERSE;
        
        // Calculate coherence from consciousness
        coherence = PhiConstants.calculateCoherence(consciousnessLevel);
        
        // Increment generation slowly
        if (time % PhiConstants.PHI < delta) {
            genesisGeneration++;
        }
    }
    
    /**
     * Update label text and colors
     */
    private void updateLabels() {
        // Update consciousness label
        String consText = String.format("φ %.2f", consciousnessLevel);
        consciousnessLabel.setText(consText);
        Color consColor = FraymusColors.getConsciousnessColor(consciousnessLevel);
        consciousnessLabel.setColor(consColor);
        
        // Update coherence label with pulse
        String cohText = String.format("⚡ %d%%", (int)(coherence * 100));
        coherenceLabel.setText(cohText);
        Color cohColor = FraymusColors.getCoherenceColor(coherence);
        // Add pulse effect
        float pulse = 0.7f + 0.3f * (breathingPhase + 1) / 2;
        coherenceLabel.setColor(cohColor.r, cohColor.g, cohColor.b, pulse);
        
        // Update genesis label
        String genText = String.format("🧬 Gen %d", genesisGeneration);
        genesisLabel.setText(genText);
    }
    
    /**
     * Set consciousness level from external source
     */
    public void setConsciousnessLevel(double level) {
        this.consciousnessLevel = level;
    }
    
    /**
     * Set coherence from external source
     */
    public void setCoherence(double coherence) {
        this.coherence = coherence;
    }
    
    /**
     * Set genesis generation from external source
     */
    public void setGenesisGeneration(int generation) {
        this.genesisGeneration = generation;
    }
    
    /**
     * Get current consciousness level
     */
    public double getConsciousnessLevel() {
        return consciousnessLevel;
    }
    
    /**
     * Get current coherence
     */
    public double getCoherence() {
        return coherence;
    }
    
    /**
     * Create label style with specified color
     */
    private Label.LabelStyle createLabelStyle(Color color) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont(); // TODO: Load custom font
        style.fontColor = color;
        return style;
    }
    
    /**
     * Create background drawable
     * TODO: Implement proper drawable with glass morphism effect
     */
    private com.badlogic.gdx.scenes.scene2d.utils.Drawable createBackground() {
        // Placeholder - will be replaced with proper glass morphism
        return null;
    }
}
