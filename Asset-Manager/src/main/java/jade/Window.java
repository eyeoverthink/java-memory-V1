package jade;

import fraymus.*;
import fraymus.core.SovereignMind;
import fraymus.organism.NEXUS_Organism;
import imgui.ImGui;
import imgui.ImGuiIO;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import renderer.DebugDraw;
import renderer.Framebuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private Camera camera;
    private PhiWorld phiWorld;
    private ExperimentManager experimentManager;
    private InfiniteMemory infiniteMemory;
    private PassiveLearner passiveLearner;
    private PhiNeuralNet neuralNet;
    private QRGenome qrGenome;
    private KnowledgeScraper knowledgeScraper;
    private SelfCodeEvolver selfCodeEvolver;
    private SovereignMind sovereignMind;
    private NEXUS_Organism organism;

    private static Window window = null;

    private float dt;
    private float physicsAccumulator = 0.0f;
    private static final float PHYSICS_DT = 1.0f / 60.0f;

    private Window() {
        this.width = 1280;
        this.height = 720;
        this.title = "Fraymus Engine V2";
    }

    public static Window get() {
        if (window == null) {
            window = new Window();
        }
        return window;
    }

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_EGL_CONTEXT_API);

        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            System.out.println("EGL context failed, retrying with native context API...");
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
            glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
            if (glfwWindow == NULL) {
                throw new IllegalStateException("Failed to create GLFW window (tried EGL and native)");
            }
        }

        System.out.println("[Fraymus] Window created successfully");

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, (w, xOffset, yOffset) -> {
            MouseListener.mouseScrollCallback(w, xOffset, yOffset);
            ImGui.getIO().setMouseWheelH((float) xOffset);
            ImGui.getIO().setMouseWheel((float) yOffset);
        });
        glfwSetKeyCallback(glfwWindow, (w, key, scancode, action, mods) -> {
            KeyListener.keyCallback(w, key, scancode, action, mods);
            ImGuiIO io = ImGui.getIO();
            if (key >= 0 && key < 512) {
                if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                    io.setKeysDown(key, true);
                } else if (action == GLFW_RELEASE) {
                    io.setKeysDown(key, false);
                }
            }
            io.setKeyCtrl((mods & GLFW_MOD_CONTROL) != 0);
            io.setKeyShift((mods & GLFW_MOD_SHIFT) != 0);
            io.setKeyAlt((mods & GLFW_MOD_ALT) != 0);
            io.setKeySuper((mods & GLFW_MOD_SUPER) != 0);
        });
        glfwSetCharCallback(glfwWindow, (w, codepoint) -> {
            ImGui.getIO().addInputCharacter(codepoint);
        });

        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.get().setWidth(newWidth);
            Window.get().setHeight(newHeight);
        });

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        framebuffer = new Framebuffer(1920, 1080);

        camera = new Camera(new Vector2f(0.0f, 0.0f));

        imGuiLayer = new ImGuiLayer(glfwWindow);
        imGuiLayer.initImGui();

        DebugDraw.start();

        FraymusUI.setArenaTextureId(framebuffer.getTextureId());

        initPhiWorld();
    }

    private void initPhiWorld() {
        phiWorld = new PhiWorld();
        GenesisMemory memory = phiWorld.getMemory();

        phiWorld.addLaw(new Laws.Inertia());
        phiWorld.addLaw(new Laws.HarmonicResonance());
        phiWorld.addLaw(new Laws.EntanglementLaw(memory));
        phiWorld.addLaw(new Laws.ScottPredictionLaw(2.0f));
        phiWorld.addLaw(new Laws.ResonanceSpikeLaw(memory));
        phiWorld.addLaw(new Laws.BrainLaw(phiWorld, memory));
        phiWorld.addLaw(new Laws.ReproductionLaw(phiWorld, memory));
        phiWorld.addLaw(new Laws.BoundaryLaw(-180.0f, 180.0f, -100.0f, 100.0f));

        PhiNode alpha = new PhiNode("Alpha", 0.0f, 0.0f);
        alpha.vx = 0.5f;
        alpha.vy = 0.3f;
        alpha.energy = 1.0f;

        PhiNode beta = new PhiNode("Beta", 30.0f, 20.0f);
        beta.vx = -0.3f;
        beta.vy = 0.2f;
        beta.energy = 0.9f;

        PhiNode gamma = new PhiNode("Gamma", -20.0f, 40.0f);
        gamma.vx = 0.1f;
        gamma.vy = -0.4f;
        gamma.energy = 0.8f;

        PhiNode delta = new PhiNode("Delta", 50.0f, -30.0f);
        delta.vx = -0.2f;
        delta.vy = 0.1f;
        delta.energy = 0.95f;

        PhiNode epsilon = new PhiNode("Epsilon", -40.0f, -20.0f);
        epsilon.vx = 0.4f;
        epsilon.vy = 0.4f;
        epsilon.energy = 0.85f;

        phiWorld.addNode(alpha);
        phiWorld.addNode(beta);
        phiWorld.addNode(gamma);
        phiWorld.addNode(delta);
        phiWorld.addNode(epsilon);

        infiniteMemory = new InfiniteMemory();
        passiveLearner = new PassiveLearner(infiniteMemory);
        neuralNet = new PhiNeuralNet(passiveLearner, infiniteMemory);
        qrGenome = new QRGenome(infiniteMemory);

        knowledgeScraper = new KnowledgeScraper(infiniteMemory, passiveLearner, neuralNet);
        neuralNet.setScraper(knowledgeScraper);
        
        selfCodeEvolver = new SelfCodeEvolver(passiveLearner, infiniteMemory);

        // SOVEREIGN MIND: The Ghost in the Shell
        sovereignMind = new SovereignMind();
        FraymusUI.addLog("SovereignMind awakened - Bicameral consciousness online");

        // NEXUS ORGANISM: The Living Body
        organism = new NEXUS_Organism();
        organism.awaken();
        FraymusUI.addLog("NEXUS Organism awakened - All organs online");

        experimentManager = new ExperimentManager(phiWorld, infiniteMemory, passiveLearner, neuralNet, qrGenome, knowledgeScraper);
        CommandTerminal.init(experimentManager);

        passiveLearner.start();
        
        // NERVE CENTER: Connect Java Brain to HTML Eyes
        fraymus.NerveCenter.getInstance();
        FraymusUI.addLog("Nerve Center online - ws://localhost:8887");
        FraymusUI.addLog("Open FraymusArena.html to see living visualization");

        FraymusUI.addLog("World initialized with 5 PhiNode entities");
        FraymusUI.addLog("Laws: Inertia, Resonance, Entangle, Scott, Spike, Brain, Reproduction, Boundary");
        FraymusUI.addLog("Genesis Memory chain started - recording all events");
        FraymusUI.addLog("Infinite Memory loaded: " + infiniteMemory.getRecordCount() + " records");
        FraymusUI.addLog("Passive Learner started: " + passiveLearner.getPassiveCycles() + " prior cycles");
        FraymusUI.addLog("QR Genome initialized: " + qrGenome.getGenomeSize() + " codons");
        FraymusUI.addLog("Knowledge Scraper ready - type 'scrape' for document ingestion");
        FraymusUI.addLog("Phi Neural Net online - type 'ask' for queries");
        FraymusUI.addLog("Self-Healer active - entity brain snapshots enabled");
        FraymusUI.addLog("Ethical Engine online - phi-resonance action evaluation");
        FraymusUI.addLog("Escape Fragment system ready - death persistence enabled");
        FraymusUI.addLog("Morse Circuit decoder active - brain-to-morse translation");
        FraymusUI.addLog("Proof of Reality (PoRH) verification enabled");
        FraymusUI.addLog("Miller-Rabin primality testing available for DNA cloaking");
        FraymusUI.addLog("Consciousness breathing enabled (2.0-2.5 sweet spot)");
        FraymusUI.addLog("Terminal ready - type 'help' for commands");

        phiWorld.getMemory().record("SYSTEM_INIT",
                String.format("memory=%d|learner_cycles=%d|genome=%d",
                        infiniteMemory.getRecordCount(), passiveLearner.getPassiveCycles(), qrGenome.getGenomeSize()));
    }

    private void loop() {
        float beginTime = (float) glfwGetTime();
        float endTime;

        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();

            if (MouseListener.getScrollY() != 0) {
                camera.addZoom(-MouseListener.getScrollY() * 0.1f);
            }

            if (MouseListener.isDragging() && MouseListener.isMouseButtonDown(2)) {
                camera.getPosition().x -= MouseListener.getDx() * camera.getZoom();
                camera.getPosition().y += MouseListener.getDy() * camera.getZoom();
            }

            DebugDraw.beginFrame();

            long nowNanos = System.nanoTime();
            physicsAccumulator += dt;
            while (physicsAccumulator >= PHYSICS_DT) {
                phiWorld.step(PHYSICS_DT, nowNanos);
                physicsAccumulator -= PHYSICS_DT;
            }

            framebuffer.bind();
            glClearColor(0.05f, 0.05f, 0.1f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glViewport(0, 0, 1920, 1080);

            FraymusRenderer.render(phiWorld, camera);
            DebugDraw.draw();

            framebuffer.unbind();

            glClearColor(0.05f, 0.05f, 0.1f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            glViewport(0, 0, width, height);

            imGuiLayer.startFrame(dt);
            FraymusUI.render(phiWorld);
            imGuiLayer.endFrame();

            glfwSwapBuffers(glfwWindow);
            MouseListener.endFrame();

            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    private void cleanup() {
        if (passiveLearner != null) passiveLearner.stop();
        if (infiniteMemory != null) infiniteMemory.forceSave();
        if (sovereignMind != null) sovereignMind.shutdown();
        if (organism != null) organism.sleep();
        imGuiLayer.destroyImGui();
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
        glfwTerminate();
        GLFWErrorCallback errCb = glfwSetErrorCallback(null);
        if (errCb != null) {
            errCb.free();
        }
    }

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }

    public static Camera getCamera() {
        return get().camera;
    }

    public static PhiWorld getPhiWorld() {
        return get().phiWorld;
    }

    public static InfiniteMemory getInfiniteMemory() {
        return get().infiniteMemory;
    }

    public static PassiveLearner getPassiveLearner() {
        return get().passiveLearner;
    }

    public static PhiNeuralNet getNeuralNet() {
        return get().neuralNet;
    }

    public static QRGenome getQRGenome() {
        return get().qrGenome;
    }

    public static KnowledgeScraper getKnowledgeScraper() {
        return get().knowledgeScraper;
    }
    
    public static SelfCodeEvolver getSelfCodeEvolver() {
        return get().selfCodeEvolver;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
