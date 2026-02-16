package fraymus.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fraymus.ui.theme.FraymusColors;
import fraymus.ui.theme.PhiConstants;

/**
 * ACTION BAR - Always Visible Main Navigation
 * 
 * Five main action categories:
 * 🧠 Mind (AI & Consciousness)
 * ⚛️ Physics (Spatial Computing)
 * 🌊 Create (Generative)
 * 📊 Data (Analysis)
 * ⚙️ Tools (System)
 * 
 * Height: 55px (Fibonacci UNIT_5)
 * Spacing: Phi-harmonic (13px between buttons)
 */
public class ActionBar extends Table {
    
    // Action buttons
    private TextButton mindButton;
    private TextButton physicsButton;
    private TextButton createButton;
    private TextButton dataButton;
    private TextButton toolsButton;
    
    // Current active tab
    private int activeTab = -1;
    
    // Animation
    private float time;
    
    // Callback for tab changes
    public interface TabChangeListener {
        void onTabChanged(int tabIndex);
    }
    
    private TabChangeListener tabChangeListener;
    
    public ActionBar() {
        this.time = 0;
        setupLayout();
    }
    
    private void setupLayout() {
        // Set background color
        setBackground(createBackground());
        
        // Set fixed height (Fibonacci UNIT_5 = 55px)
        setHeight(PhiConstants.UNIT_5);
        
        // Padding using Fibonacci spacing
        pad(PhiConstants.UNIT_1); // 8px padding
        
        // Center the buttons
        add().expandX();
        
        // Create the 5 action buttons
        mindButton = createActionButton("🧠 Mind", 0);
        add(mindButton).padRight(PhiConstants.UNIT_2); // 13px spacing
        
        physicsButton = createActionButton("⚛️ Physics", 1);
        add(physicsButton).padRight(PhiConstants.UNIT_2);
        
        createButton = createActionButton("🌊 Create", 2);
        add(createButton).padRight(PhiConstants.UNIT_2);
        
        dataButton = createActionButton("📊 Data", 3);
        add(dataButton).padRight(PhiConstants.UNIT_2);
        
        toolsButton = createActionButton("⚙️ Tools", 4);
        add(toolsButton);
        
        // Center the buttons
        add().expandX();
    }
    
    /**
     * Create action button with phi-harmonic styling
     */
    private TextButton createActionButton(String text, int tabIndex) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = new BitmapFont(); // TODO: Load custom font
        style.fontColor = FraymusColors.TEXT_PRIMARY;
        style.downFontColor = FraymusColors.CONSCIOUSNESS_GOLD;
        style.overFontColor = FraymusColors.QUANTUM_BLUE;
        
        TextButton button = new TextButton(text, style);
        
        // Set size using Fibonacci proportions
        button.setWidth(PhiConstants.UNIT_6); // 89px
        button.setHeight(PhiConstants.UNIT_4); // 34px
        
        // Add click listener
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setActiveTab(tabIndex);
            }
        });
        
        return button;
    }
    
    /**
     * Set active tab
     */
    public void setActiveTab(int tabIndex) {
        if (activeTab == tabIndex) {
            // Toggle off if clicking same tab
            activeTab = -1;
        } else {
            activeTab = tabIndex;
        }
        
        // Update button colors
        updateButtonColors();
        
        // Notify listener
        if (tabChangeListener != null) {
            tabChangeListener.onTabChanged(activeTab);
        }
    }
    
    /**
     * Update button colors based on active state
     */
    private void updateButtonColors() {
        updateButtonColor(mindButton, 0);
        updateButtonColor(physicsButton, 1);
        updateButtonColor(createButton, 2);
        updateButtonColor(dataButton, 3);
        updateButtonColor(toolsButton, 4);
    }
    
    /**
     * Update individual button color
     */
    private void updateButtonColor(TextButton button, int index) {
        if (index == activeTab) {
            button.setColor(FraymusColors.BUTTON_ACTIVE);
        } else {
            button.setColor(FraymusColors.BUTTON_NORMAL);
        }
    }
    
    /**
     * Update animation
     */
    public void update(float delta) {
        time += delta;
        
        // Add subtle phi-harmonic pulse to active button
        if (activeTab >= 0) {
            float pulse = (float) (0.9 + 0.1 * Math.sin(time * PhiConstants.PHI));
            TextButton activeButton = getButtonByIndex(activeTab);
            if (activeButton != null) {
                Color color = FraymusColors.BUTTON_ACTIVE;
                activeButton.setColor(color.r, color.g, color.b, pulse);
            }
        }
    }
    
    /**
     * Get button by index
     */
    private TextButton getButtonByIndex(int index) {
        switch (index) {
            case 0: return mindButton;
            case 1: return physicsButton;
            case 2: return createButton;
            case 3: return dataButton;
            case 4: return toolsButton;
            default: return null;
        }
    }
    
    /**
     * Set tab change listener
     */
    public void setTabChangeListener(TabChangeListener listener) {
        this.tabChangeListener = listener;
    }
    
    /**
     * Get active tab index (-1 if none)
     */
    public int getActiveTab() {
        return activeTab;
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
