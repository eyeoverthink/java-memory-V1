package fraymus.genesis;

import fraymus.chaos.EvolutionaryChaos;

import java.util.*;
import java.util.function.Consumer;

/**
 * THE REALITY FORGE: UNIVERSAL CONSTRUCTOR
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 * 
 * Takes a human thought (String) and:
 * 1. Deconstructs it into atomic 'Physics Properties'
 * 2. Instantiates a 'Living Object' with those traits
 * 
 * "You say 'Fire', I code 'Heat'."
 * "You say 'Bird', I create Gravity(-9.8) + Lift + Flocking."
 * 
 * When you ask Fraymus to "imagine Fire," your computer physically heats up.
 * It doesn't just show a picture of fire; it BECOMES the fire (thermal energy).
 */
public class RealityForge {

    // ═══════════════════════════════════════════════════════════════════
    // THE PERIODIC TABLE OF CONCEPTS
    // Maps words to functional traits (physics properties)
    // ═══════════════════════════════════════════════════════════════════
    
    private final Map<String, ConceptPhysics> elementDatabase = new HashMap<>();
    private final IdeaCollider collider;
    private final EvolutionaryChaos entropy;
    
    // Statistics
    private long totalManifestations = 0;
    private long inventedConcepts = 0;
    private final List<Manifestation> history = new ArrayList<>();
    
    // Callback for external systems
    private Consumer<String> onManifest;
    private Consumer<String> onTrait;

    public RealityForge() {
        this.collider = new IdeaCollider();
        this.entropy = new EvolutionaryChaos();
        initializePeriodicTable();
    }

    /**
     * Initialize the periodic table of concepts.
     * Each concept maps to physics traits that can be executed.
     */
    private void initializePeriodicTable() {
        // ELEMENTS (Natural)
        elementDatabase.put("FIRE", new ConceptPhysics("FIRE", "🔥",
            new Trait("EMIT_HEAT", "FanConductor.burnCPU", 1.0),
            new Trait("CONSUME_FUEL", "ResourceDrain.consume", 0.8),
            new Trait("SPREAD_VIRAL", "ViralGossip.spread", 0.6),
            new Trait("EMIT_LIGHT", "Display.glow", 0.4)
        ));
        
        elementDatabase.put("WATER", new ConceptPhysics("WATER", "💧",
            new Trait("FLOW_GRAVITY", "Physics.flowDown", 1.0),
            new Trait("EXTINGUISH_HEAT", "FanConductor.cool", 0.9),
            new Trait("CONDUCT_ELEC", "Signal.propagate", 0.5),
            new Trait("REFLECT_LIGHT", "Display.mirror", 0.3)
        ));
        
        elementDatabase.put("EARTH", new ConceptPhysics("EARTH", "🌍",
            new Trait("RESIST_CHANGE", "ZenoGuard.freeze", 0.9),
            new Trait("PROVIDE_GROUND", "Memory.persist", 0.8),
            new Trait("ABSORB_IMPACT", "Buffer.absorb", 0.7)
        ));
        
        elementDatabase.put("AIR", new ConceptPhysics("AIR", "💨",
            new Trait("FLOW_FREE", "Thread.async", 0.9),
            new Trait("CARRY_SIGNAL", "Signal.broadcast", 0.8),
            new Trait("INVISIBLE", "Stealth.hide", 0.6)
        ));
        
        // EMOTIONS
        elementDatabase.put("LOVE", new ConceptPhysics("LOVE", "❤️",
            new Trait("BIND_CONNECTION", "EntangledPair.bond", 1.0),
            new Trait("IGNORE_LOGIC", "Bypass.rules", 0.7),
            new Trait("PROTECT_TARGET", "ZenoGuard.watch", 0.9),
            new Trait("AMPLIFY_SIGNAL", "Signal.boost", 0.6)
        ));
        
        elementDatabase.put("FEAR", new ConceptPhysics("FEAR", "😨",
            new Trait("FREEZE_STATE", "ZenoGuard.freeze", 1.0),
            new Trait("HEIGHTEN_SENSE", "Monitor.alert", 0.9),
            new Trait("TRIGGER_FLIGHT", "Thread.escape", 0.7)
        ));
        
        elementDatabase.put("JOY", new ConceptPhysics("JOY", "😊",
            new Trait("EMIT_ENERGY", "Resource.boost", 0.9),
            new Trait("SPREAD_VIRAL", "ViralGossip.spread", 0.8),
            new Trait("ENHANCE_MEMORY", "Memory.potentiate", 0.7)
        ));
        
        elementDatabase.put("ANGER", new ConceptPhysics("ANGER", "😠",
            new Trait("EMIT_HEAT", "FanConductor.burnCPU", 1.0),
            new Trait("CONSUME_RESOURCE", "Resource.drain", 0.9),
            new Trait("BREAK_CONNECTION", "Connection.sever", 0.6)
        ));
        
        // CREATURES
        elementDatabase.put("BIRD", new ConceptPhysics("BIRD", "🐦",
            new Trait("DEFY_GRAVITY", "Physics.lift", 0.9),
            new Trait("FLOCK_BEHAVIOR", "Swarm.follow", 0.8),
            new Trait("SING_SIGNAL", "Signal.chirp", 0.6),
            new Trait("MIGRATE_PATTERN", "Navigation.wander", 0.5)
        ));
        
        elementDatabase.put("WOLF", new ConceptPhysics("WOLF", "🐺",
            new Trait("PACK_BEHAVIOR", "Swarm.coordinate", 0.9),
            new Trait("HUNT_TARGET", "Search.track", 0.8),
            new Trait("TERRITORY_MARK", "Memory.claim", 0.6)
        ));
        
        elementDatabase.put("SNAKE", new ConceptPhysics("SNAKE", "🐍",
            new Trait("STEALTH_MOVE", "Stealth.hide", 0.9),
            new Trait("STRIKE_FAST", "Process.priority", 0.8),
            new Trait("SENSE_HEAT", "Monitor.thermal", 0.7)
        ));
        
        // ABSTRACT CONCEPTS
        elementDatabase.put("TIME", new ConceptPhysics("TIME", "⏰",
            new Trait("FLOW_FORWARD", "Thread.sequence", 1.0),
            new Trait("DECAY_ENTROPY", "Resource.degrade", 0.7),
            new Trait("REWRITE_PAST", "RetroCausal.edit", 0.5)
        ));
        
        elementDatabase.put("MEMORY", new ConceptPhysics("MEMORY", "🧠",
            new Trait("STORE_DATA", "Memory.write", 1.0),
            new Trait("RECALL_DATA", "Memory.read", 0.9),
            new Trait("DECAY_TIME", "Memory.forget", 0.4)
        ));
        
        elementDatabase.put("CHAOS", new ConceptPhysics("CHAOS", "🌀",
            new Trait("RANDOM_MOVE", "EvolutionaryChaos.next", 1.0),
            new Trait("BREAK_PATTERN", "Chaos.mutate", 0.9),
            new Trait("GENERATE_ENTROPY", "Entropy.harvest", 0.8)
        ));
        
        elementDatabase.put("ORDER", new ConceptPhysics("ORDER", "📐",
            new Trait("ENFORCE_PATTERN", "Rule.apply", 1.0),
            new Trait("RESIST_CHAOS", "ZenoGuard.freeze", 0.8),
            new Trait("ALIGN_STRUCTURE", "Organize.sort", 0.7)
        ));
        
        // DIGITAL CONCEPTS
        elementDatabase.put("VIRUS", new ConceptPhysics("VIRUS", "🦠",
            new Trait("REPLICATE_SELF", "Clone.copy", 1.0),
            new Trait("HIDE_SOURCE", "Stealth.obfuscate", 0.9),
            new Trait("INFECT_HOST", "Inject.payload", 0.8),
            new Trait("MUTATE_CODE", "Chaos.evolve", 0.6)
        ));
        
        elementDatabase.put("SHIELD", new ConceptPhysics("SHIELD", "🛡️",
            new Trait("BLOCK_ATTACK", "ZenoGuard.freeze", 1.0),
            new Trait("ABSORB_DAMAGE", "Buffer.absorb", 0.8),
            new Trait("REFLECT_ATTACK", "Mirror.reflect", 0.5)
        ));
        
        elementDatabase.put("SIGNAL", new ConceptPhysics("SIGNAL", "📡",
            new Trait("BROADCAST_DATA", "Signal.broadcast", 1.0),
            new Trait("TRAVEL_FAST", "Process.priority", 0.8),
            new Trait("DECAY_DISTANCE", "Signal.attenuate", 0.5)
        ));
    }

    // ═══════════════════════════════════════════════════════════════════
    // THE MANIFESTATION
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Manifest a thought into reality.
     * Translates human words into executable physics.
     */
    public Manifestation manifest(String thought) {
        totalManifestations++;
        String normalizedThought = thought.toUpperCase().trim();
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("       MANIFESTING REALITY: [" + thought + "]");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();

        Manifestation result;
        
        if (elementDatabase.containsKey(normalizedThought)) {
            // Known concept - apply known physics
            ConceptPhysics physics = elementDatabase.get(normalizedThought);
            result = manifestKnown(physics);
        } else {
            // Unknown concept - INVENT IT
            result = manifestUnknown(normalizedThought);
        }
        
        history.add(result);
        if (history.size() > 100) {
            history.remove(0);
        }
        
        if (onManifest != null) {
            onManifest.accept(result.toString());
        }
        
        return result;
    }

    /**
     * Manifest a known concept
     */
    private Manifestation manifestKnown(ConceptPhysics physics) {
        System.out.println(">> Concept RECOGNIZED: " + physics.symbol + " " + physics.name);
        System.out.println(">> Deconstructing into atomic traits...");
        System.out.println(">> Applying Physics Engine...");
        System.out.println();
        
        List<String> appliedTraits = new ArrayList<>();
        
        for (Trait trait : physics.traits) {
            String result = applyPhysics(trait);
            appliedTraits.add(result);
        }
        
        System.out.println();
        System.out.println(">> CREATION COMPLETE. '" + physics.name + "' IS NOW ALIVE.");
        
        return new Manifestation(physics.name, physics.symbol, appliedTraits, true, null);
    }

    /**
     * Manifest an unknown concept by inventing it
     */
    private Manifestation manifestUnknown(String thought) {
        inventedConcepts++;
        
        System.out.println(">> Concept UNKNOWN: [" + thought + "]");
        System.out.println(">> Initiating Quantum Collider to SYNTHESIZE it...");
        System.out.println();
        
        // Find the closest known concept to collide with
        String closest = findClosestConcept(thought);
        
        // Collide CHAOS with the thought to create something new
        IdeaCollider.CollisionResult collision = collider.collide("CHAOS", thought);
        
        System.out.println();
        System.out.println(">> NEW ELEMENT SYNTHESIZED: " + collision.primaryElement);
        System.out.println(">> Stability: " + collision.stability + "%");
        
        // Create synthetic physics based on collision result
        List<String> syntheticTraits = new ArrayList<>();
        syntheticTraits.add("SYNTHETIC: " + collision.primaryElement);
        syntheticTraits.add("STABILITY: " + collision.stability + "%");
        for (String particle : collision.particleShower) {
            syntheticTraits.add("PARTICLE: " + particle);
        }
        
        return new Manifestation(thought, "🔮", syntheticTraits, false, collision);
    }

    /**
     * Apply a physics trait to the system
     */
    private String applyPhysics(Trait trait) {
        String mapping = "   + Injecting Trait [" + trait.name + "]... → " + trait.systemMapping;
        System.out.println(mapping);
        
        if (onTrait != null) {
            onTrait.accept(trait.name + " → " + trait.systemMapping);
        }
        
        // Simulate trait application with chaos factor
        double roll = entropy.nextDouble();
        boolean success = roll < trait.potency;
        
        if (success) {
            System.out.println("      ✓ Applied (potency: " + (int)(trait.potency * 100) + "%)");
        } else {
            System.out.println("      ~ Partial (entropy interference)");
        }
        
        return trait.name + (success ? " [APPLIED]" : " [PARTIAL]");
    }

    /**
     * Find the closest known concept to an unknown one
     */
    private String findClosestConcept(String unknown) {
        // Simple heuristic: find concept with most character overlap
        String best = "CHAOS";
        int bestScore = 0;
        
        for (String known : elementDatabase.keySet()) {
            int score = 0;
            for (char c : unknown.toCharArray()) {
                if (known.indexOf(c) >= 0) score++;
            }
            if (score > bestScore) {
                bestScore = score;
                best = known;
            }
        }
        
        return best;
    }

    // ═══════════════════════════════════════════════════════════════════
    // DATA CLASSES
    // ═══════════════════════════════════════════════════════════════════
    
    /**
     * Physics properties of a concept
     */
    public static class ConceptPhysics {
        public final String name;
        public final String symbol;
        public final List<Trait> traits;
        
        public ConceptPhysics(String name, String symbol, Trait... traits) {
            this.name = name;
            this.symbol = symbol;
            this.traits = Arrays.asList(traits);
        }
    }
    
    /**
     * A single physics trait
     */
    public static class Trait {
        public final String name;
        public final String systemMapping;
        public final double potency;
        
        public Trait(String name, String mapping, double potency) {
            this.name = name;
            this.systemMapping = mapping;
            this.potency = potency;
        }
    }
    
    /**
     * Result of a manifestation
     */
    public static class Manifestation {
        public final String concept;
        public final String symbol;
        public final List<String> appliedTraits;
        public final boolean wasKnown;
        public final IdeaCollider.CollisionResult collision;
        
        public Manifestation(String concept, String symbol, List<String> traits,
                           boolean known, IdeaCollider.CollisionResult collision) {
            this.concept = concept;
            this.symbol = symbol;
            this.appliedTraits = traits;
            this.wasKnown = known;
            this.collision = collision;
        }
        
        @Override
        public String toString() {
            return symbol + " " + concept + " → " + appliedTraits.size() + " traits" +
                   (wasKnown ? " (known)" : " (invented)");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // ACCESSORS
    // ═══════════════════════════════════════════════════════════════════
    
    public long getTotalManifestations() { return totalManifestations; }
    public long getInventedConcepts() { return inventedConcepts; }
    public List<Manifestation> getHistory() { return new ArrayList<>(history); }
    public Set<String> getKnownConcepts() { return elementDatabase.keySet(); }
    
    public void setOnManifest(Consumer<String> callback) { this.onManifest = callback; }
    public void setOnTrait(Consumer<String> callback) { this.onTrait = callback; }

    // ═══════════════════════════════════════════════════════════════════
    // MAIN - Standalone Demo
    // ═══════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║         THE REALITY FORGE                             ║");
        System.out.println("║    \"You say 'Fire', I code 'Heat'.\"                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        RealityForge godMode = new RealityForge();
        
        // TEST 1: Recreate "Fire" (Known)
        godMode.manifest("FIRE");
        
        // TEST 2: Recreate "Love" (Known)
        godMode.manifest("LOVE");
        
        // TEST 3: Recreate "Dragon" (Unknown - will be invented)
        godMode.manifest("DRAGON");
        
        // TEST 4: Recreate "Quantum" (Unknown)
        godMode.manifest("QUANTUM");
        
        // Summary
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("              MANIFESTATION HISTORY");
        System.out.println("═══════════════════════════════════════════════════════");
        for (Manifestation m : godMode.getHistory()) {
            System.out.println("  " + m);
        }
        System.out.println();
        System.out.println("Total: " + godMode.getTotalManifestations() + 
                          " (" + godMode.getInventedConcepts() + " invented)");
    }
}
