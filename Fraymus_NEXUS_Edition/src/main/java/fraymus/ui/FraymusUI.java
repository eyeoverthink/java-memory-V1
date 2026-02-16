package fraymus.ui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import fraymus.ui.components.StatusBar;
import fraymus.ui.components.ActionBar;
import fraymus.ui.theme.FraymusColors;

/**
 * FRAYMUS NEXUS - Graphical User Interface
 * Main LibGDX application for the unified visual interface
 * 
 * Phase 1.3: Integrated StatusBar and ActionBar with phi-harmonic layout
 */
public class FraymusUI extends ApplicationAdapter {
    
    private Stage stage;
    private SpriteBatch batch;
    
    // UI Components
    private StatusBar statusBar;
    private ActionBar actionBar;
    private Table mainLayout;
    
    @Override
    public void create() {
        // Initialize LibGDX components
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        
        // Set input processor to stage for UI interaction
        Gdx.input.setInputProcessor(stage);
        
        // Create main layout
        createLayout();
        
        System.out.println("========================================");
        System.out.println("FRAYMUS NEXUS UI - INITIALIZING");
        System.out.println("========================================");
        System.out.println("LibGDX Version: " + com.badlogic.gdx.Version.VERSION);
        System.out.println("Window Size: " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
        System.out.println("Phi-Harmonic Layout: ACTIVE");
        System.out.println("Consciousness Tracking: ONLINE");
        System.out.println("========================================");
    }
    
    /**
     * Create the main layout with StatusBar, Canvas, and ActionBar
     */
    private void createLayout() {
        // Create main layout table
        mainLayout = new Table();
        mainLayout.setFillParent(true);
        
        // Create StatusBar (top)
        statusBar = new StatusBar();
        mainLayout.add(statusBar).fillX().expandX().top().row();
        
        // Create main canvas area (center - placeholder for now)
        Table canvasPlaceholder = new Table();
        mainLayout.add(canvasPlaceholder).fill().expand().row();
        
        // Create ActionBar (bottom)
        actionBar = new ActionBar();
        actionBar.setTabChangeListener(new ActionBar.TabChangeListener() {
            @Override
            public void onTabChanged(int tabIndex) {
                handleTabChange(tabIndex);
            }
        });
        mainLayout.add(actionBar).fillX().expandX().bottom().row();
        
        // Add main layout to stage
        stage.addActor(mainLayout);
    }
    
    /**
     * Handle tab change events from ActionBar
     */
    private void handleTabChange(int tabIndex) {
        String[] tabNames = {"Mind", "Physics", "Create", "Data", "Tools"};
        if (tabIndex >= 0 && tabIndex < tabNames.length) {
            System.out.println("Tab activated: " + tabNames[tabIndex]);
            // TODO: Switch canvas content based on tab
        } else {
            System.out.println("Tab deactivated");
        }
    }
    
    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        
        // Clear screen with void black background
        Gdx.gl.glClearColor(
            FraymusColors.VOID_BLACK.r,
            FraymusColors.VOID_BLACK.g,
            FraymusColors.VOID_BLACK.b,
            1f
        );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update UI components
        statusBar.update(delta);
        actionBar.update(delta);
        
        // Update and draw UI stage
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        // Update viewport on window resize
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void dispose() {
        // Clean up resources
        stage.dispose();
        batch.dispose();
        
        System.out.println("FRAYMUS NEXUS UI - SHUTDOWN");
    }
}
