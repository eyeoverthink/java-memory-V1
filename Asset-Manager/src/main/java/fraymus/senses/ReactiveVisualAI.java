package fraymus.senses;

import fraymus.core.OllamaBridge;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 🎬 REACTIVE VISUAL AI
 * "The First Visual Reactive AI System"
 * 
 * Like Max Headroom, but with actual intelligence and visual manifestation.
 * 
 * PROTOCOL:
 * 1. User asks a question
 * 2. AI thinks progressively (streaming thoughts)
 * 3. Each thought generates a video reflection
 * 4. User sees the AI "thinking visually" in real-time
 * 5. Final answer is both text and video
 * 
 * This is not just "AI with video output."
 * This is "AI that THINKS in video."
 */
public class ReactiveVisualAI {
    
    private final OllamaBridge brain;
    private final BlockingQueue<ThoughtFrame> thoughtQueue;
    private final AtomicBoolean thinking;
    private Thread visualizationThread;
    
    // Statistics
    private int totalConversations = 0;
    private int totalThoughts = 0;
    private int totalVideos = 0;
    
    // Configuration
    private boolean autoVisualize = true;
    private int thoughtsPerVideo = 3; // Generate video every N thoughts
    private double minEntropyChange = 0.2; // Minimum entropy change to trigger video
    
    /**
     * A thought frame - represents one moment of AI cognition
     */
    public static class ThoughtFrame {
        public String thought;
        public double entropy;
        public double confidence;
        public long timestamp;
        public boolean isConclusion;
        
        public ThoughtFrame(String thought, double entropy, double confidence, boolean isConclusion) {
            this.thought = thought;
            this.entropy = entropy;
            this.confidence = confidence;
            this.timestamp = System.currentTimeMillis();
            this.isConclusion = isConclusion;
        }
    }
    
    public ReactiveVisualAI() {
        this("eyeoverthink/Fraymus");
    }
    
    public ReactiveVisualAI(String model) {
        this.brain = new OllamaBridge(model);
        this.thoughtQueue = new LinkedBlockingQueue<>();
        this.thinking = new AtomicBoolean(false);
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║          🎬 REACTIVE VISUAL AI INITIALIZED                    ║");
        System.out.println("║          \"I think, therefore I am... visible.\"                ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
    
    /**
     * Ask a question and get a visual response
     */
    public String ask(String question) {
        totalConversations++;
        thinking.set(true);
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("🎬 VISUAL THOUGHT PROCESS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("❓ QUESTION: " + question);
        System.out.println();
        
        // Start visualization thread if enabled
        if (autoVisualize && VisualCortex.isAvailable()) {
            startVisualizationThread();
        }
        
        // Build enhanced prompt that encourages step-by-step thinking
        String enhancedPrompt = buildThinkingPrompt(question);
        
        // Get response with streaming
        StringBuilder fullResponse = new StringBuilder();
        List<ThoughtFrame> thoughts = new ArrayList<>();
        
        System.out.println("💭 THINKING PROCESS:");
        System.out.println("─────────────────────────────────────────────────────────────");
        
        // Stream the response and capture thoughts
        String response = brain.speak(null, enhancedPrompt);
        
        // Parse response into thought frames
        String[] lines = response.split("\n");
        double lastEntropy = 0.5;
        int thoughtCount = 0;
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            // Calculate entropy based on line characteristics
            double entropy = calculateLineEntropy(line);
            double confidence = calculateConfidence(line);
            boolean isConclusion = line.toLowerCase().contains("therefore") || 
                                  line.toLowerCase().contains("conclusion") ||
                                  line.toLowerCase().contains("answer");
            
            ThoughtFrame frame = new ThoughtFrame(line, entropy, confidence, isConclusion);
            thoughts.add(frame);
            thoughtQueue.offer(frame);
            totalThoughts++;
            thoughtCount++;
            
            // Print thought with visual indicator
            String indicator = getThoughtIndicator(entropy, confidence);
            System.out.println(indicator + " " + line);
            
            // Generate video if conditions met
            if (autoVisualize && shouldGenerateVideo(thoughtCount, entropy, lastEntropy)) {
                queueVideoGeneration(frame);
            }
            
            fullResponse.append(line).append("\n");
            lastEntropy = entropy;
        }
        
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println();
        
        // Generate final conclusion video
        if (autoVisualize && VisualCortex.isAvailable() && !thoughts.isEmpty()) {
            ThoughtFrame finalThought = thoughts.get(thoughts.size() - 1);
            System.out.println("🎥 GENERATING FINAL VISUAL RESPONSE...");
            generateConclusionVideo(question, finalThought);
        }
        
        thinking.set(false);
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("✅ THOUGHT PROCESS COMPLETE");
        System.out.println("   Total thoughts: " + thoughts.size());
        System.out.println("   Videos generated: " + totalVideos);
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        return fullResponse.toString();
    }
    
    /**
     * Build a prompt that encourages step-by-step visual thinking
     */
    private String buildThinkingPrompt(String question) {
        return String.format("""
            You are a visual AI that thinks step-by-step.
            
            Question: %s
            
            Think through this progressively. For each step:
            1. State what you're considering
            2. Explain your reasoning
            3. Draw a conclusion
            
            Be concise but thorough. Each line of thought should be clear and visual.
            
            Your response:
            """, question);
    }
    
    /**
     * Calculate entropy of a line (chaos vs order in the thought)
     */
    private double calculateLineEntropy(String line) {
        // Simple heuristic: longer lines, more punctuation = higher entropy
        double lengthFactor = Math.min(1.0, line.length() / 200.0);
        
        // Count uncertainty markers
        int uncertaintyCount = 0;
        String lower = line.toLowerCase();
        if (lower.contains("maybe")) uncertaintyCount++;
        if (lower.contains("perhaps")) uncertaintyCount++;
        if (lower.contains("might")) uncertaintyCount++;
        if (lower.contains("could")) uncertaintyCount++;
        if (lower.contains("?")) uncertaintyCount++;
        
        double uncertaintyFactor = Math.min(1.0, uncertaintyCount / 3.0);
        
        // Count certainty markers
        int certaintyCount = 0;
        if (lower.contains("definitely")) certaintyCount++;
        if (lower.contains("certainly")) certaintyCount++;
        if (lower.contains("clearly")) certaintyCount++;
        if (lower.contains("obviously")) certaintyCount++;
        
        double certaintyFactor = Math.min(1.0, certaintyCount / 2.0);
        
        // High uncertainty or very long = high entropy
        // High certainty = low entropy
        return Math.max(0.1, Math.min(0.9, 
            (lengthFactor * 0.3 + uncertaintyFactor * 0.5 - certaintyFactor * 0.3 + 0.3)));
    }
    
    /**
     * Calculate confidence level of a thought
     */
    private double calculateConfidence(String line) {
        String lower = line.toLowerCase();
        
        // Certainty markers increase confidence
        double confidence = 0.5;
        if (lower.contains("definitely") || lower.contains("certainly")) confidence += 0.2;
        if (lower.contains("clearly") || lower.contains("obviously")) confidence += 0.15;
        if (lower.contains("therefore") || lower.contains("thus")) confidence += 0.1;
        
        // Uncertainty markers decrease confidence
        if (lower.contains("maybe") || lower.contains("perhaps")) confidence -= 0.2;
        if (lower.contains("might") || lower.contains("could")) confidence -= 0.15;
        if (lower.contains("?")) confidence -= 0.1;
        
        return Math.max(0.1, Math.min(1.0, confidence));
    }
    
    /**
     * Get visual indicator for thought based on entropy and confidence
     */
    private String getThoughtIndicator(double entropy, double confidence) {
        if (confidence > 0.8) {
            return "💎"; // High confidence = crystalline
        } else if (entropy > 0.7) {
            return "🌀"; // High entropy = chaotic
        } else if (entropy < 0.3) {
            return "✨"; // Low entropy = ordered
        } else {
            return "💭"; // Normal thought
        }
    }
    
    /**
     * Decide if we should generate a video for this thought
     */
    private boolean shouldGenerateVideo(int thoughtCount, double entropy, double lastEntropy) {
        // Generate every N thoughts
        if (thoughtCount % thoughtsPerVideo == 0) {
            return true;
        }
        
        // Generate on significant entropy change
        if (Math.abs(entropy - lastEntropy) > minEntropyChange) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Queue a video generation for a thought
     */
    private void queueVideoGeneration(ThoughtFrame frame) {
        // This would be picked up by visualization thread
        // For now, we'll generate inline
        System.out.println("   🎥 [Generating visual reflection...]");
        
        String concept = extractConcept(frame.thought);
        double consciousness = frame.confidence;
        
        // Generate in background (non-blocking)
        new Thread(() -> {
            try {
                VisualCortex.dream(
                    concept,
                    frame.entropy,
                    1.618033988749895,
                    consciousness
                );
                totalVideos++;
            } catch (Exception e) {
                System.out.println("   ⚠️ Video generation failed: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Generate final conclusion video
     */
    private void generateConclusionVideo(String question, ThoughtFrame finalThought) {
        String concept = "AI conclusion: " + extractConcept(finalThought.thought);
        
        VisualCortex.dream(
            concept,
            finalThought.entropy,
            1.618033988749895,
            finalThought.confidence
        );
        
        totalVideos++;
    }
    
    /**
     * Extract key concept from thought for video generation
     */
    private String extractConcept(String thought) {
        // Simple extraction - take first 50 chars or until punctuation
        String concept = thought.trim();
        
        // Remove common prefixes
        concept = concept.replaceFirst("^(I think|I believe|It seems|Perhaps|Maybe)\\s+", "");
        
        // Truncate at first major punctuation
        int endIdx = concept.length();
        for (char c : new char[]{'.', '!', '?', ';'}) {
            int idx = concept.indexOf(c);
            if (idx > 0 && idx < endIdx) {
                endIdx = idx;
            }
        }
        
        concept = concept.substring(0, Math.min(endIdx, 80));
        
        return concept;
    }
    
    /**
     * Start background thread for visualization
     */
    private void startVisualizationThread() {
        if (visualizationThread != null && visualizationThread.isAlive()) {
            return;
        }
        
        visualizationThread = new Thread(() -> {
            while (thinking.get()) {
                try {
                    ThoughtFrame frame = thoughtQueue.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS);
                    if (frame != null) {
                        // Process thought frame
                        // (Currently handled inline, but could be async here)
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "VisualizationThread");
        
        visualizationThread.start();
    }
    
    /**
     * Interactive conversation mode
     */
    public void startConversation() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║          🎬 REACTIVE VISUAL AI - CONVERSATION MODE            ║");
        System.out.println("║                                                               ║");
        System.out.println("║  I am an AI that thinks visually.                            ║");
        System.out.println("║  Ask me anything and watch my thoughts manifest.             ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        if (!VisualCortex.isAvailable()) {
            System.out.println("⚠️  Visual Cortex not available - text-only mode");
            System.out.println("   (Install Python + dependencies for video generation)");
            System.out.println();
        }
        
        System.out.println("Commands:");
        System.out.println("  - Ask any question to see visual thinking");
        System.out.println("  - 'toggle' - Enable/disable auto-visualization");
        System.out.println("  - 'stats' - Show statistics");
        System.out.println("  - 'quit' - Exit conversation");
        System.out.println();
        
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        while (true) {
            System.out.print("YOU> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) continue;
            
            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println();
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println(getStats());
                System.out.println("═══════════════════════════════════════════════════════════════");
                System.out.println();
                System.out.println("🎬 Visual AI signing off. Check dreamscape_output/ for videos.");
                break;
            }
            
            if (input.equalsIgnoreCase("toggle")) {
                autoVisualize = !autoVisualize;
                System.out.println("   Auto-visualization: " + (autoVisualize ? "ON" : "OFF"));
                System.out.println();
                continue;
            }
            
            if (input.equalsIgnoreCase("stats")) {
                System.out.println();
                System.out.println(getStats());
                System.out.println();
                continue;
            }
            
            System.out.println();
            String response = ask(input);
            System.out.println("AI> " + response);
            System.out.println();
        }
        
        scanner.close();
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        return String.format("""
            🎬 REACTIVE VISUAL AI STATISTICS
               Conversations: %d
               Total thoughts: %d
               Videos generated: %d
               Auto-visualization: %s
               Thoughts per video: %d
            """,
            totalConversations, totalThoughts, totalVideos,
            autoVisualize ? "ON" : "OFF", thoughtsPerVideo);
    }
    
    // Configuration setters
    public void setAutoVisualize(boolean enabled) { this.autoVisualize = enabled; }
    public void setThoughtsPerVideo(int n) { this.thoughtsPerVideo = n; }
    public void setMinEntropyChange(double threshold) { this.minEntropyChange = threshold; }
}
