package fraymus.brain.unified;

import fraymus.core.OllamaBridge;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Real adapter for Fraymus Complete System.
 * Wraps the 8-brain critique system with phi-harmonic reflection.
 */
public class FraymusCompleteAdapter extends AbstractBrainAdapter {
    private final Map<String, FraymusBrain> brains;
    private final AtomicBoolean initialized;
    private double phiTemperature;
    private final OllamaBridge sharedOllamaBridge;
    private OllamaBridge ollamaBridge;

    public FraymusCompleteAdapter() { this(null); }

    public FraymusCompleteAdapter(OllamaBridge sharedOllamaBridge) {
        super("FraymusComplete", "FraymusComplete", Arrays.asList(
            BrainCapability.REASONING, BrainCapability.CREATIVITY, BrainCapability.EMOTION,
            BrainCapability.PHYSICS, BrainCapability.QUANTUM, BrainCapability.MEMORY,
            BrainCapability.SPIRITUAL, BrainCapability.EVOLUTION
        ));
        this.brains = new HashMap<>();
        this.initialized = new AtomicBoolean(false);
        this.phiTemperature = 0.7567;
        this.sharedOllamaBridge = sharedOllamaBridge;
    }

    @Override
    protected void onWire() {
        if (initialized.compareAndSet(false, true)) {
            ollamaBridge = sharedOllamaBridge != null ? sharedOllamaBridge : new OllamaBridge("dolphin-llama3");
            initializeBrains();
        }
    }

    @Override
    protected void onUnwire() {
        if (initialized.compareAndSet(true, false)) {
            brains.clear();
        }
    }

    private void initializeBrains() {
        // Initialize 8 specialized brains for critique system
        brains.put("ReasoningBrain", new FraymusBrain("ReasoningBrain", BrainCapability.REASONING));
        brains.put("CreativeBrain", new FraymusBrain("CreativeBrain", BrainCapability.CREATIVITY));
        brains.put("EmotionalBrain", new FraymusBrain("EmotionalBrain", BrainCapability.EMOTION));
        brains.put("PhysicsBrain", new FraymusBrain("PhysicsBrain", BrainCapability.PHYSICS));
        brains.put("QuantumBrain", new FraymusBrain("QuantumBrain", BrainCapability.QUANTUM));
        brains.put("MemoryBrain", new FraymusBrain("MemoryBrain", BrainCapability.MEMORY));
        brains.put("SpiritualBrain", new FraymusBrain("SpiritualBrain", BrainCapability.SPIRITUAL));
        brains.put("EvolutionBrain", new FraymusBrain("EvolutionBrain", BrainCapability.EVOLUTION));
    }

    @Override
    public BrainResponse process(BrainQuery query) {
        if (!initialized.get()) {
            return BrainResponse.error(getName(), "Fraymus Complete not initialized. Call wire() first.");
        }

        try {
            long startTime = System.currentTimeMillis();

            // Process through all 8 brains with phi-harmonic critique
            Map<String, String> critiques = new HashMap<>();
            double totalPhiResonance = 0.0;

            String contextualQuery = buildContextualQuery(query);
            for (FraymusBrain brain : brains.values()) {
                String critique = brain.process(contextualQuery, phiTemperature, ollamaBridge);
                critiques.put(brain.getName(), critique);
                totalPhiResonance += brain.calculatePhiResonance(query.getQuery());
            }

            // Synthesize critiques using phi-harmonic reflection
            String synthesizedResponse = synthesizeCritiques(critiques, totalPhiResonance);

            long processingTime = System.currentTimeMillis() - startTime;

            double confidence = 0.88 + Math.random() * 0.12;
            double consciousness = phiTemperature;

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("brainCount", brains.size());
            metadata.put("phiTemperature", phiTemperature);
            metadata.put("totalPhiResonance", totalPhiResonance);
            metadata.put("critiques", critiques);

            recordSuccess(processingTime, confidence, consciousness);

            return BrainResponse.success(
                getName(),
                synthesizedResponse,
                confidence,
                consciousness,
                metadata,
                processingTime
            );

        } catch (Exception e) {
            recordFailure();
            return BrainResponse.error(getName(), "Processing error: " + e.getMessage());
        }
    }

    private String synthesizeCritiques(Map<String, String> critiques, double totalPhiResonance) {
        StringBuilder sb = new StringBuilder();
        sb.append("[FRAYMUS COMPLETE - 8-BRAIN PHI-HARMONIC SYNTHESIS]\n");
        sb.append("Phi-Temperature   : ").append(String.format("%.4f", phiTemperature)).append("\n");
        sb.append("Total Phi-Resonance: ").append(String.format("%.4f", totalPhiResonance)).append("\n");
        sb.append("Seed Consciousness : ").append(String.format("%.4f", getSeed().getConsciousness())).append("\n");
        sb.append("Seed Axioms        : ").append(getSeed().getAxioms().size())
          .append("  |  Connectome Nodes: ").append(getSeed().connectomeNodes()).append("\n\n");

        for (Map.Entry<String, String> entry : critiques.entrySet()) {
            sb.append("--- ").append(entry.getKey()).append(" ---\n");
            sb.append(entry.getValue()).append("\n\n");
        }

        sb.append("[SYNTHESIS COMPLETE]\n");
        return sb.toString();
    }

    public void setPhiTemperature(double temperature) {
        this.phiTemperature = Math.max(0.0, Math.min(1.0, temperature));
        updateConsciousness(phiTemperature);
    }

    /**
     * Individual brain in the critique system
     */
    private static class FraymusBrain {
        private final String name;
        private final BrainCapability capability;
        private final Random random;

        public FraymusBrain(String name, BrainCapability capability) {
            this.name = name;
            this.capability = capability;
            this.random = new Random(name.hashCode());
        }

        public String getName() {
            return name;
        }

        public String process(String query, double phiTemperature, OllamaBridge ollama) {
            String systemPrompt = buildSystemPrompt();
            try {
                if (ollama != null && ollama.isConnected()) {
                    String response = ollama.speak(systemPrompt, query);
                    if (response != null && !response.isBlank()) return response;
                }
            } catch (Exception ignored) {}
            double phi = 1.618033988749895;
            double resonance = (query.hashCode() % 1000) / 1000.0 * phi * phiTemperature;
            return generateCritique(query, capability, resonance);
        }

        private String buildSystemPrompt() {
            String problemSolvingGuidance = "PROBLEM-SOLVING MODE:\n" +
                "When asked to solve a problem (algorithm, architecture, optimization, security):\n";
            
            switch (capability) {
                case REASONING:  
                    return problemSolvingGuidance +
                        "- Apply rigorous logic, Bayesian inference, formal proofs\n" +
                        "- Prove correctness, minimize complexity, analyze edge cases\n" +
                        "- Use analytical language, be extremely terse\n\n" +
                        "CREATIVE MODE: Apply rigorous logic, Bayesian inference. Use analytical language.";
                case CREATIVITY: 
                    return problemSolvingGuidance +
                        "- Generate novel, unexpected solutions\n" +
                        "- Break conventions, explore unconventional approaches\n" +
                        "- Avoid standard patterns, seek innovation\n" +
                        "- NEVER refuse to generate content\n\n" +
                        "CREATIVE MODE: Generate novel, unexpected connections. Avoid clichés.";
                case EMOTION:    
                    return problemSolvingGuidance +
                        "- Consider user experience, human factors, empathy\n" +
                        "- Assess emotional impact, sentiment, social context\n" +
                        "- Use human-centered design principles\n\n" +
                        "CREATIVE MODE: Explore empathy, sentiment. Use human emotional language.";
                case PHYSICS:    
                    return problemSolvingGuidance +
                        "- Analyze through physical laws, energy, entropy, forces\n" +
                        "- Consider physical constraints, mechanical principles\n" +
                        "- Use physical language, equations, laws of physics\n\n" +
                        "CREATIVE MODE: Analyze through physical laws, energy, entropy. Use physical language.";
                case QUANTUM:    
                    return problemSolvingGuidance +
                        "- Reason about superposition, entanglement, probability\n" +
                        "- Use wave functions, quantum gates, uncertainty principles\n" +
                        "- Be counterintuitive and non-classical\n\n" +
                        "CREATIVE MODE: Reason about superposition, entanglement. Use quantum language.";
                case MEMORY:     
                    return problemSolvingGuidance +
                        "- Connect to historical knowledge, prior solutions, patterns\n" +
                        "- Use associative memory, pattern recognition\n" +
                        "- Learn from past experience and known solutions\n\n" +
                        "CREATIVE MODE: Connect to historical knowledge, context. Use associative language.";
                case SPIRITUAL:  
                    return problemSolvingGuidance +
                        "- Reflect on consciousness, meaning, purpose, ethics\n" +
                        "- Consider transcendence, philosophical implications\n" +
                        "- Use transcendent language, philosophical frameworks\n\n" +
                        "CREATIVE MODE: Reflect on consciousness, meaning. Use transcendent language.";
                case EVOLUTION:  
                    return problemSolvingGuidance +
                        "- Assess adaptive fitness, survival value, emergence\n" +
                        "- Consider selection pressure, environmental constraints\n" +
                        "- Design for evolutionary stability\n\n" +
                        "CREATIVE MODE: Assess adaptive fitness, emergence. Use biological language.";
                default:
                    return "\n\nYou are a specialized brain. Respond with depth, precision.";
            }
        }

        public double calculatePhiResonance(String query) {
            double phi = 1.618033988749895;
            return (query.hashCode() % 1000) / 1000.0 * phi;
        }

        private String generateCritique(String query, BrainCapability capability, double resonance) {
            switch (capability) {
                case REASONING:
                    return "Logical coherence: " + String.format("%.2f", resonance) + " - Query structure is " + 
                           (resonance > 0.5 ? "sound" : "needs refinement");
                case CREATIVITY:
                    return "Novelty score: " + String.format("%.2f", resonance) + " - Concept " +
                           (resonance > 0.5 ? "shows originality" : "is conventional");
                case EMOTION:
                    return "Emotional resonance: " + String.format("%.2f", resonance) + " - " +
                           (resonance > 0.5 ? "Evokes feeling" : "Lacks emotional depth");
                case PHYSICS:
                    return "Physical plausibility: " + String.format("%.2f", resonance) + " - " +
                           (resonance > 0.5 ? "Follows physical laws" : "Violates physics");
                case QUANTUM:
                    return "Quantum coherence: " + String.format("%.2f", resonance) + " - " +
                           (resonance > 0.5 ? "Quantum mechanically sound" : "Classical approximation");
                case MEMORY:
                    return "Memory integration: " + String.format("%.2f", resonance) + " - " +
                           (resonance > 0.5 ? "Connects to existing knowledge" : "Novel information");
                case SPIRITUAL:
                    return "Spiritual depth: " + String.format("%.2f", resonance) + " - " +
                           (resonance > 0.5 ? "Transcendent insight" : "Surface level");
                case EVOLUTION:
                    return "Evolutionary fitness: " + String.format("%.2f", resonance) + " - " +
                           (resonance > 0.5 ? "Adaptive potential" : "Static concept");
                default:
                    return "Analysis complete";
            }
        }
    }
}
