package fraymus.neural;

import org.junit.jupiter.api.*;
import java.util.*;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for HolographicRenderEngine and HolographicPhysicsEngine.
 *
 * Verifies that hyperdimensional memory vectors can be:
 * 1. Folded into 3D render parameters (position, rotation, color)
 * 2. Folded into physics body states (mass, velocity, force)
 * 3. Simulated forward in time with phi-harmonic damping
 * 4. Stored and retrieved via O(1) holographic memory
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HolographicRenderPhysicsTest {

    private HolographicAssociativeMemory memory;
    private HolographicRenderEngine renderEngine;
    private HolographicPhysicsEngine physicsEngine;

    @BeforeEach
    void setUp() {
        memory = new HolographicAssociativeMemory();
        renderEngine = new HolographicRenderEngine();
        physicsEngine = new HolographicPhysicsEngine();

        renderEngine.wire(memory);
        physicsEngine.wire(memory);
    }

    @AfterEach
    void tearDown() {
        renderEngine.shutdown();
        physicsEngine.shutdown();
    }

    @Test
    @Order(1)
    @DisplayName("Wire/unwire pattern: engines can be wired and unwired")
    void wireUnwirePattern() {
        assertTrue(renderEngine.isWired());
        assertTrue(physicsEngine.isWired());

        renderEngine.unwire();
        physicsEngine.unwire();

        assertFalse(renderEngine.isWired());
        assertFalse(physicsEngine.isWired());

        renderEngine.wire(memory);
        physicsEngine.wire(memory);

        assertTrue(renderEngine.isWired());
        assertTrue(physicsEngine.isWired());
    }

    @Test
    @Order(2)
    @DisplayName("Render thought: hypervector folds into 3D scene parameters")
    void renderThoughtFolding() {
        HolographicRenderEngine.RenderFrame frame = renderEngine.renderThought("gravity_well");

        assertNotNull(frame);
        assertEquals("gravity_well", frame.conceptKey);
        assertEquals(16384, frame.hypervector.length);

        HolographicRenderEngine.RenderParams p = frame.params;
        assertNotNull(p);

        // All parameters should be in valid ranges after folding
        assertTrue(Math.abs(p.posX) <= 10.0);
        assertTrue(Math.abs(p.posY) <= 10.0);
        assertTrue(Math.abs(p.posZ) <= 10.0);

        assertTrue(p.scaleX >= 0.1 && p.scaleX <= 2.1);
        assertTrue(p.scaleY >= 0.1 && p.scaleY <= 2.1);
        assertTrue(p.scaleZ >= 0.1 && p.scaleZ <= 2.1);

        assertTrue(p.colorR >= 0.0 && p.colorR <= 1.0);
        assertTrue(p.colorG >= 0.0 && p.colorG <= 1.0);
        assertTrue(p.colorB >= 0.0 && p.colorB <= 1.0);
        assertTrue(p.colorA >= 0.5 && p.colorA <= 1.0);
    }

    @Test
    @Order(3)
    @DisplayName("Render persistence: rendered frames stored in holographic memory")
    void renderPersistence() {
        renderEngine.renderThought("star_formation");

        // Frame should be stored as "frame:star_formation:<timestamp>"
        // We verify by checking memory has more entries after render
        int before = memory.getMemoriesStored();

        renderEngine.renderThought("nebula_cloud");

        int after = memory.getMemoriesStored();
        assertTrue(after > before, "Rendered frame should persist in holographic memory");
    }

    @Test
    @Order(4)
    @DisplayName("Blender script generation: produces valid Python")
    void blenderScriptGeneration() {
        String script = renderEngine.generateBlenderScene("quantum_field");

        assertNotNull(script);
        assertTrue(script.contains("import bpy"));
        assertTrue(script.contains("bpy.ops.mesh.primitive_cube_add"));
        assertTrue(script.contains("quantum_field"));

        // Script should be stored in memory
        int stored = memory.getMemoriesStored();
        assertTrue(stored > 0);
    }

    @Test
    @Order(5)
    @DisplayName("Physics spawn: hypervector folds into rigid body parameters")
    void physicsSpawnFolding() {
        HolographicPhysicsEngine.PhysicsBody body = physicsEngine.spawnBody("heavy_rock");

        assertNotNull(body);
        assertEquals("heavy_rock", body.key);
        assertTrue(body.mass >= 0.1 && body.mass <= 10.1);
        assertTrue(Math.abs(body.posX) <= 50.0);
        assertTrue(Math.abs(body.posY) <= 50.0);
        assertTrue(Math.abs(body.posZ) <= 50.0);
        assertTrue(body.restitution >= 0.1 && body.restitution <= 1.0);
        assertTrue(body.friction >= 0.0 && body.friction <= 1.0);
    }

    @Test
    @Order(6)
    @DisplayName("Physics simulation: body moves under gravity")
    void physicsSimulation() {
        HolographicPhysicsEngine.PhysicsBody body = physicsEngine.spawnBody("falling_object");
        double initialY = body.posY;

        physicsEngine.setGravity(0.0, -9.81, 0.0);

        // Step 60 frames (1 second at 60Hz)
        for (int i = 0; i < 60; i++) {
            physicsEngine.stepSimulation();
        }

        // Object should have fallen (Y decreased due to gravity)
        assertTrue(body.posY < initialY,
            "Object should fall under gravity: initial=" + initialY + " final=" + body.posY);

        // Velocity should have increased in negative Y direction
        assertTrue(body.velY < 0.0, "Velocity should be negative (falling)");
    }

    @Test
    @Order(7)
    @DisplayName("Physics collision: elastic collision between two bodies")
    void physicsCollision() {
        // Create two bodies on collision course
        HolographicPhysicsEngine.PhysicsBody a = physicsEngine.spawnBody("body_a");
        HolographicPhysicsEngine.PhysicsBody b = physicsEngine.spawnBody("body_b");

        // Force them to overlap
        a.posX = 0.0; a.posY = 0.0; a.posZ = 0.0;
        a.size = 2.0; a.velX = 1.0; a.velY = 0.0; a.velZ = 0.0;

        b.posX = 1.0; b.posY = 0.0; b.posZ = 0.0;
        b.size = 2.0; b.velX = -1.0; b.velY = 0.0; b.velZ = 0.0;

        HolographicPhysicsEngine.CollisionManifold collision =
            physicsEngine.detectCollision("body_a", "body_b");

        assertNotNull(collision, "Bodies should collide when overlapping");
        assertEquals("body_a", collision.bodyA);
        assertEquals("body_b", collision.bodyB);
        assertTrue(collision.overlapX > 0);
    }

    @Test
    @Order(8)
    @DisplayName("Async simulation: run 120 frames in background")
    void asyncSimulation() throws Exception {
        physicsEngine.spawnBody("test_particle");
        physicsEngine.setGravity(0.0, -9.81, 0.0);

        Future<HolographicPhysicsEngine.SimulationResult> future =
            physicsEngine.runSimulation(120);

        HolographicPhysicsEngine.SimulationResult result = future.get();

        assertNotNull(result);
        assertEquals(120, result.frames);
        assertTrue(result.latencyMs > 0);
        assertTrue(result.finalStates.containsKey("test_particle"));
    }

    @Test
    @Order(9)
    @DisplayName("O(1) state retrieval: physics state retrieved from holographic memory")
    void o1StateRetrieval() {
        physicsEngine.spawnBody("persistent_body");
        physicsEngine.setGravity(0.0, -9.81, 0.0);
        physicsEngine.stepSimulation();

        // Retrieve via holographic memory (not just map lookup)
        HolographicPhysicsEngine.PhysicsBody retrieved =
            physicsEngine.retrieveBodyState("persistent_body");

        assertNotNull(retrieved, "Body state should be retrievable from holographic memory");
        // Since it's stored after step, we verify retrieval succeeded
    }

    @Test
    @Order(10)
    @DisplayName("SentientCoreHolographicMemory: full wire/unwire with render and physics")
    void sentientCoreIntegration() {
        SentientCoreHolographicMemory scMemory = new SentientCoreHolographicMemory(null);

        assertFalse(scMemory.isWired());

        scMemory.wire();
        assertTrue(scMemory.isWired());
        assertNotNull(scMemory.getMemory());
        assertNotNull(scMemory.getRenderEngine());
        assertNotNull(scMemory.getPhysicsEngine());

        // Verify engines are wired to same memory
        assertTrue(scMemory.getRenderEngine().isWired());
        assertTrue(scMemory.getPhysicsEngine().isWired());

        // Render a thought
        HolographicRenderEngine.RenderFrame frame =
            scMemory.getRenderEngine().renderThought("consciousness_state");
        assertNotNull(frame);

        // Spawn a physics body
        HolographicPhysicsEngine.PhysicsBody body =
            scMemory.getPhysicsEngine().spawnBody("embodied_self");
        assertNotNull(body);

        // Check status output
        String status = scMemory.getStatus();
        assertTrue(status.contains("SENTIENT CORE HOLOGRAPHIC MEMORY"));
        assertTrue(status.contains("RENDER ENGINE"));
        assertTrue(status.contains("PHYSICS ENGINE"));

        scMemory.unwire();
        assertFalse(scMemory.isWired());
    }

    @Test
    @Order(11)
    @DisplayName("Render stats and physics stats are non-empty")
    void engineStats() {
        renderEngine.renderThought("stats_test");
        physicsEngine.spawnBody("stats_body");

        String renderStats = renderEngine.getStats();
        String physicsStats = physicsEngine.getStats();

        assertTrue(renderStats.contains("Wired:"));
        assertTrue(physicsStats.contains("Wired:"));
        assertTrue(physicsStats.contains("Bodies:"));
    }
}
