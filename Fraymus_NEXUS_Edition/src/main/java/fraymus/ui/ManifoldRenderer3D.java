package fraymus.ui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import fraymus.brain.ManifoldBrain;
import fraymus.brain.PhiManifold;
import fraymus.senses.BioSymbiosis;
import fraymus.evolution.MivingBrain;
import fraymus.evolution.Priecled;

import java.util.*;

/**
 * MANIFOLD RENDERER 3D: LIBGDX-BASED VISUALIZATION
 * 
 * Embeds in Java application to provide real-time 3D visualization of:
 * - Manifold brain structure (phi-spiral nodes)
 * - Reasoning paths (animated flow)
 * - Knowledge connections (weighted edges)
 * - Bio-feedback effects (color, pulse, distortion)
 * 
 * Replaces 1D-2D boilerplate arena with multi-dimensional graphics.
 * Shows what's actually happening inside FRAYMUS.
 */
public class ManifoldRenderer3D extends ApplicationAdapter {
    
    private static final float PHI = 1.6180339887f;
    private static final float GOLDEN_ANGLE = 137.5077640500378f;
    
    // LibGDX components
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Environment environment;
    
    // 3D models
    private Array<ModelInstance> nodeInstances;
    private Array<ModelInstance> connectionInstances;
    private Model nodeModel;
    private Model connectionModel;
    
    // FRAYMUS integration
    private ManifoldBrain brain;
    private BioSymbiosis bioSymbiosis;
    private MivingBrain mivingBrain;
    
    // Visualization state
    private List<Node3D> nodes;
    private List<Connection3D> connections;
    private List<ReasoningPath> activePaths;
    
    // Animation
    private float time = 0;
    private boolean autoRotate = true;
    private float cameraAngle = 0;
    
    public ManifoldRenderer3D(ManifoldBrain brain, BioSymbiosis bioSymbiosis) {
        this.brain = brain;
        this.bioSymbiosis = bioSymbiosis;
        this.nodes = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.activePaths = new ArrayList<>();
    }
    
    public ManifoldRenderer3D(MivingBrain mivingBrain) {
        this.mivingBrain = mivingBrain;
        this.nodes = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.activePaths = new ArrayList<>();
    }
    
    @Override
    public void create() {
        // Camera setup
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(50f, 30f, 50f);
        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.far = 300f;
        camera.update();
        
        // Model batch for rendering
        modelBatch = new ModelBatch();
        
        // Environment (lighting)
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0f, 1f, 1f, 1f, -1f, -0.8f, -0.2f));
        environment.add(new DirectionalLight().set(1f, 0f, 1f, 0.5f, 1f, 0.8f, 0.2f));
        
        // Create 3D models
        createModels();
        
        // Initialize manifold structure
        createManifoldStructure();
        
        System.out.println("🧠⚡ MANIFOLD RENDERER 3D INITIALIZED");
        System.out.println("   Nodes: " + nodes.size());
        System.out.println("   Connections: " + connections.size());
    }
    
    /**
     * Create 3D models for nodes and connections
     */
    private void createModels() {
        ModelBuilder modelBuilder = new ModelBuilder();
        
        // Node model (sphere with glow)
        nodeModel = modelBuilder.createSphere(
            1f, 1f, 1f,
            16, 16,
            new Material(ColorAttribute.createDiffuse(Color.CYAN)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        
        // Connection model (cylinder)
        connectionModel = modelBuilder.createCylinder(
            0.1f, 1f, 0.1f,
            8,
            new Material(ColorAttribute.createDiffuse(new Color(0, 1, 1, 0.3f))),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        
        nodeInstances = new Array<>();
        connectionInstances = new Array<>();
    }
    
    /**
     * Create manifold structure with phi-spiral distribution
     */
    private void createManifoldStructure() {
        if (mivingBrain != null) {
            // Use Miving Brain neurons
            syncWithMivingBrain();
        } else {
            // Create default structure
            int nodeCount = 50;
            float radius = 30f;
            
            // Create nodes in phi-spiral pattern
            for (int i = 0; i < nodeCount; i++) {
                // Phi-spiral positioning (Fibonacci sphere)
                float theta = i * GOLDEN_ANGLE * (float)Math.PI / 180f;
                float phi = (float)Math.acos(1 - 2 * (i + 0.5f) / nodeCount);
                float r = radius * (float)Math.pow(PHI, -i / 20f);
                
                float x = r * (float)Math.sin(phi) * (float)Math.cos(theta);
                float y = r * (float)Math.sin(phi) * (float)Math.sin(theta);
                float z = r * (float)Math.cos(phi);
                
                createNode(x, y, z, "Concept_" + i);
            }
            
            // Create phi-weighted connections
            createPhiConnections();
        }
    }
    
    /**
     * Sync visualization with Miving Brain state
     */
    private void syncWithMivingBrain() {
        if (mivingBrain == null) return;
        
        // Clear existing
        nodes.clear();
        connections.clear();
        nodeInstances.clear();
        connectionInstances.clear();
        
        // Create nodes from Priecleds
        for (Priecled p : mivingBrain.getNeurons()) {
            createNodeFromPriecled(p);
        }
        
        // Create connections from synapses
        for (Priecled p : mivingBrain.getNeurons()) {
            for (fraymus.evolution.Synapse s : p.synapses) {
                // Find node instances
                Node3D nodeA = findNodeByPriecled(p);
                Node3D nodeB = findNodeByPriecled(s.target);
                
                if (nodeA != null && nodeB != null) {
                    createConnectionBetweenNodes(nodeA, nodeB);
                }
            }
        }
    }
    
    /**
     * Create node from Priecled
     */
    private void createNodeFromPriecled(Priecled p) {
        Node3D node = new Node3D((float)p.x, (float)p.y, (float)p.z, p.id);
        node.priecled = p;
        nodes.add(node);
        
        // Create model instance
        ModelInstance instance = new ModelInstance(nodeModel);
        instance.transform.setToTranslation((float)p.x, (float)p.y, (float)p.z);
        
        // Scale based on energy
        float scale = (float)(0.3 + p.energy * 0.3);
        instance.transform.scale(scale, scale, scale);
        
        // Color based on alignment (Red vs Blue)
        float[] color = p.getColor();
        instance.materials.get(0).set(
            ColorAttribute.createDiffuse(new Color(color[0], color[1], color[2], 1f))
        );
        
        nodeInstances.add(instance);
        node.modelInstance = instance;
    }
    
    /**
     * Find node by Priecled
     */
    private Node3D findNodeByPriecled(Priecled p) {
        for (Node3D node : nodes) {
            if (node.priecled == p) {
                return node;
            }
        }
        return null;
    }
    
    /**
     * Create connection between existing nodes
     */
    private void createConnectionBetweenNodes(Node3D nodeA, Node3D nodeB) {
        Connection3D connection = new Connection3D(nodeA, nodeB);
        connections.add(connection);
        
        // Create line model instance
        ModelInstance instance = new ModelInstance(connectionModel);
        
        // Position and orient cylinder between nodes
        Vector3 posA = new Vector3(nodeA.x, nodeA.y, nodeA.z);
        Vector3 posB = new Vector3(nodeB.x, nodeB.y, nodeB.z);
        Vector3 midpoint = new Vector3(posA).lerp(posB, 0.5f);
        float length = posA.dst(posB);
        
        instance.transform.setToTranslation(midpoint);
        instance.transform.scale(1f, length, 1f);
        
        // Orient towards target
        Vector3 direction = new Vector3(posB).sub(posA).nor();
        Vector3 up = new Vector3(0, 1, 0);
        if (Math.abs(direction.dot(up)) > 0.99f) {
            up = new Vector3(1, 0, 0);
        }
        Vector3 right = new Vector3(up).crs(direction).nor();
        up = new Vector3(direction).crs(right).nor();
        
        connectionInstances.add(instance);
        connection.modelInstance = instance;
    }
    
    /**
     * Create node at position
     */
    private void createNode(float x, float y, float z, String label) {
        Node3D node = new Node3D(x, y, z, label);
        nodes.add(node);
        
        // Create model instance
        ModelInstance instance = new ModelInstance(nodeModel);
        instance.transform.setToTranslation(x, y, z);
        instance.transform.scale(0.5f, 0.5f, 0.5f);
        nodeInstances.add(instance);
        
        node.modelInstance = instance;
    }
    
    /**
     * Create phi-weighted connections between nodes
     */
    private void createPhiConnections() {
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                Node3D nodeA = nodes.get(i);
                Node3D nodeB = nodes.get(j);
                
                // Calculate distance
                float dx = nodeA.x - nodeB.x;
                float dy = nodeA.y - nodeB.y;
                float dz = nodeA.z - nodeB.z;
                float distance = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
                
                // Phi-weighted probability
                float phiWeight = (float)Math.pow(PHI, -distance / 10f);
                
                if (Math.random() < phiWeight * 0.3) {
                    createConnection(nodeA, nodeB);
                }
            }
        }
    }
    
    /**
     * Create connection between two nodes
     */
    private void createConnection(Node3D nodeA, Node3D nodeB) {
        Connection3D connection = new Connection3D(nodeA, nodeB);
        connections.add(connection);
        
        // Create line model instance
        ModelInstance instance = new ModelInstance(connectionModel);
        
        // Position and orient cylinder between nodes
        Vector3 posA = new Vector3(nodeA.x, nodeA.y, nodeA.z);
        Vector3 posB = new Vector3(nodeB.x, nodeB.y, nodeB.z);
        Vector3 midpoint = new Vector3(posA).lerp(posB, 0.5f);
        float length = posA.dst(posB);
        
        instance.transform.setToTranslation(midpoint);
        instance.transform.scale(1f, length, 1f);
        
        // Orient towards target
        Vector3 direction = new Vector3(posB).sub(posA).nor();
        Vector3 up = new Vector3(0, 1, 0);
        if (Math.abs(direction.dot(up)) > 0.99f) {
            up = new Vector3(1, 0, 0);
        }
        Vector3 right = new Vector3(up).crs(direction).nor();
        up = new Vector3(direction).crs(right).nor();
        
        connectionInstances.add(instance);
        connection.modelInstance = instance;
        
        nodeA.connections.add(nodeB);
        nodeB.connections.add(nodeA);
    }
    
    /**
     * Visualize reasoning path through manifold
     */
    public void visualizeReasoningPath(List<String> conceptPath) {
        if (conceptPath == null || conceptPath.isEmpty()) return;
        
        List<Node3D> pathNodes = new ArrayList<>();
        
        // Find nodes matching concept path
        for (String concept : conceptPath) {
            for (Node3D node : nodes) {
                if (node.label.contains(concept)) {
                    pathNodes.add(node);
                    break;
                }
            }
        }
        
        if (pathNodes.size() > 1) {
            ReasoningPath path = new ReasoningPath(pathNodes);
            activePaths.add(path);
            
            System.out.println("🧠 Visualizing reasoning path: " + pathNodes.size() + " nodes");
        }
    }
    
    @Override
    public void render() {
        time += Gdx.graphics.getDeltaTime();
        
        // Sync with Miving Brain if active
        if (mivingBrain != null && Math.random() < 0.01) { // Update occasionally
            syncWithMivingBrain();
        }
        
        // Clear screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        
        // Update camera
        if (autoRotate) {
            cameraAngle += 0.2f * Gdx.graphics.getDeltaTime();
            camera.position.x = 50f * (float)Math.sin(cameraAngle);
            camera.position.z = 50f * (float)Math.cos(cameraAngle);
            camera.lookAt(0, 0, 0);
            camera.update();
        }
        
        // Update node animations
        updateNodeAnimations();
        
        // Update reasoning path animations
        updateReasoningPaths();
        
        // Render all models
        modelBatch.begin(camera);
        
        // Render connections (first, so they're behind nodes)
        for (ModelInstance instance : connectionInstances) {
            modelBatch.render(instance, environment);
        }
        
        // Render nodes
        for (ModelInstance instance : nodeInstances) {
            modelBatch.render(instance, environment);
        }
        
        modelBatch.end();
    }
    
    /**
     * Update node animations (phi-harmonic pulsing)
     */
    private void updateNodeAnimations() {
        for (int i = 0; i < nodes.size(); i++) {
            Node3D node = nodes.get(i);
            
            // Phi-harmonic pulse
            float pulse = (float)Math.sin(time * 2f + i * PHI) * 0.1f + 0.9f;
            
            // Bio-feedback color modulation
            if (bioSymbiosis != null) {
                float stress = bioSymbiosis.getStressLevel();
                
                // Stressed = red, calm = cyan
                Color color = new Color(stress, 1f - stress, 1f, 1f);
                node.modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(color));
            }
            
            // Apply pulse scale
            Vector3 pos = new Vector3(node.x, node.y, node.z);
            node.modelInstance.transform.setToTranslation(pos);
            node.modelInstance.transform.scale(pulse * 0.5f, pulse * 0.5f, pulse * 0.5f);
        }
    }
    
    /**
     * Update reasoning path animations
     */
    private void updateReasoningPaths() {
        Iterator<ReasoningPath> iterator = activePaths.iterator();
        
        while (iterator.hasNext()) {
            ReasoningPath path = iterator.next();
            path.update(Gdx.graphics.getDeltaTime());
            
            // Highlight current node in path
            if (path.currentIndex < path.nodes.size()) {
                Node3D node = path.nodes.get(path.currentIndex);
                
                // Amplify scale
                Vector3 pos = new Vector3(node.x, node.y, node.z);
                node.modelInstance.transform.setToTranslation(pos);
                node.modelInstance.transform.scale(1.5f, 1.5f, 1.5f);
                
                // Bright color
                node.modelInstance.materials.get(0).set(
                    ColorAttribute.createDiffuse(Color.WHITE)
                );
            }
            
            // Remove completed paths
            if (path.isComplete()) {
                iterator.remove();
            }
        }
    }
    
    @Override
    public void dispose() {
        modelBatch.dispose();
        nodeModel.dispose();
        connectionModel.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
    
    /**
     * Toggle auto-rotation
     */
    public void toggleAutoRotate() {
        autoRotate = !autoRotate;
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        return String.format(
            "Nodes: %d | Connections: %d | Active Paths: %d | FPS: %d",
            nodes.size(),
            connections.size(),
            activePaths.size(),
            Gdx.graphics.getFramesPerSecond()
        );
    }
    
    /**
     * Node in 3D space
     */
    private static class Node3D {
        float x, y, z;
        String label;
        List<Node3D> connections;
        ModelInstance modelInstance;
        Priecled priecled; // Link to Miving Brain neuron
        
        Node3D(float x, float y, float z, String label) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.label = label;
            this.connections = new ArrayList<>();
        }
    }
    
    /**
     * Connection between nodes
     */
    private static class Connection3D {
        Node3D nodeA, nodeB;
        float weight;
        ModelInstance modelInstance;
        
        Connection3D(Node3D nodeA, Node3D nodeB) {
            this.nodeA = nodeA;
            this.nodeB = nodeB;
            this.weight = (float)(Math.random() * PHI);
        }
    }
    
    /**
     * Animated reasoning path
     */
    private static class ReasoningPath {
        List<Node3D> nodes;
        int currentIndex;
        float timer;
        float stepDuration = 0.2f;
        
        ReasoningPath(List<Node3D> nodes) {
            this.nodes = nodes;
            this.currentIndex = 0;
            this.timer = 0;
        }
        
        void update(float delta) {
            timer += delta;
            if (timer >= stepDuration) {
                timer = 0;
                currentIndex++;
            }
        }
        
        boolean isComplete() {
            return currentIndex >= nodes.size();
        }
    }
}
