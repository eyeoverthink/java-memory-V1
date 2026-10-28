package fraymus.senses;

import fraymus.core.ComputeScheduler;
import fraymus.core.GpuProfile;
import fraymus.core.OllamaBridge;
import fraymus.visual.ThoughtAnalyzer;
import fraymus.visual.ThoughtFrame;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final ComputeScheduler compute;
    private final GpuProfile gpu;
    private final AtomicInteger pendingVideos;
    
    // Statistics
    private int totalConversations = 0;
    private int totalThoughts = 0;
    private volatile int totalVideos = 0;
    
    // Configuration
    private boolean autoVisualize = true;
    private int thoughtsPerVideo = 3; // Generate video every N thoughts
    private double minEntropyChange = 0.2; // Minimum entropy change to trigger video
    private int maxVideosPerQuestion = 2;
    private int maxPendingVideos = 1;
    private int videosThisQuestion = 0;
    
    // ThoughtFrame now imported from fraymus.visual package
    
    public ReactiveVisualAI() {
        this("eyeoverthink/Fraymus");
    }
    
    public ReactiveVisualAI(String model) {
        this.brain = new OllamaBridge(model);
        this.thoughtQueue = new LinkedBlockingQueue<>();
        this.thinking = new AtomicBoolean(false);
        this.pendingVideos = new AtomicInteger(0);
        this.compute = ComputeScheduler.getInstance();
        this.gpu = GpuProfile.getInstance();
        
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
        videosThisQuestion = 0;
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("🎬 VISUAL THOUGHT PROCESS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("❓ QUESTION: " + question);
        System.out.println();
        
        // Visualization thread no longer needed - video generation is async via ComputeScheduler IO pool
        
        // Build enhanced prompt that encourages step-by-step thinking
        String enhancedPrompt = buildThinkingPrompt(question);
        
        // Get response with streaming
        StringBuilder fullResponse = new StringBuilder();
        List<ThoughtFrame> thoughts = new ArrayList<>();
        
        System.out.println("💭 THINKING PROCESS:");
        System.out.println("─────────────────────────────────────────────────────────────");
        
        // Stream the response and capture thoughts
        String response = brain.speak(null, enhancedPrompt);
        if (autoVisualize && VisualCortex.isAvailable()) {
            brain.releaseForVisuals();
        }
        
        // Parse response into thought frames
        String[] lines = response.split("\n");
        double lastEntropy = 0.5;
        int thoughtCount = 0;
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            // Use the shared ThoughtAnalyzer for richer entropy/confidence
            ThoughtFrame frame = ThoughtAnalyzer.analyze(line);
            thoughts.add(frame);
            totalThoughts++;
            thoughtCount++;
            
            // Print thought with visual indicator
            System.out.println(frame.indicator + " " + line);
            
            // Generate video if conditions met
            if (autoVisualize && shouldGenerateVideo(thoughtCount, frame.entropy, lastEntropy, frame)) {
                queueVideoGeneration(frame);
            }
            
            fullResponse.append(line).append("\n");
            lastEntropy = frame.entropy;
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
        System.out.println("   Videos completed: " + totalVideos + "; pending: " + pendingVideos.get());
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        return fullResponse.toString();
    }
    
    /**
     * Build a prompt that encourages step-by-step visual thinking
     */
    private String buildThinkingPrompt(String question) {
        return String.format("""
            Answer accurately and concisely using up to four short sentences.
            
            Question: %s
            
            Provide a helpful answer and, when useful, a concrete visual description.
            Respect the requested length. Do not narrate an internal thought process.
            Distinguish established facts from speculation.
            
            Your response:
            """, question);
    }
    
    // Entropy, confidence, and indicator logic now delegated to ThoughtAnalyzer.
    
    /**
     * Decide if we should generate a video for this thought.
     * Triggers: periodic, entropy shift, conclusion, or crystalline certainty.
     */
    private boolean shouldGenerateVideo(int thoughtCount, double entropy, double lastEntropy, ThoughtFrame frame) {
        // Generate every N thoughts
        if (thoughtCount % thoughtsPerVideo == 0) {
            return true;
        }
        // Generate on significant entropy change
        if (Math.abs(entropy - lastEntropy) > minEntropyChange) {
            return true;
        }
        // Conclusion always gets a video
        if (frame.isConclusion) {
            return true;
        }
        // High-confidence crystalline thoughts (special moments)
        if (frame.entropy < 0.2 && frame.confidence > 0.8) {
            return true;
        }
        return false;
    }
    
    /**
     * Queue a video generation for a thought
     */
    private void queueVideoGeneration(ThoughtFrame frame) {
        queueVideoGeneration(frame, false);
    }

    private void queueVideoGeneration(ThoughtFrame frame, boolean force) {
        if (!autoVisualize || !VisualCortex.isAvailable()) {
            return;
        }
        if (!force && videosThisQuestion >= maxVideosPerQuestion) {
            return;
        }
        if (pendingVideos.get() >= maxPendingVideos) {
            return;
        }

        videosThisQuestion++;
        pendingVideos.incrementAndGet();

        System.out.println("   🎥 [Queued visual reflection...]");
        String concept = extractConcept(frame.thought);
        double consciousness = frame.confidence;

        // Submit to the shared, hardware-aware IO pool instead of a private executor.
        Future<?> f = compute.submitIo(() -> {
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
            } finally {
                pendingVideos.decrementAndGet();
            }
        });
        // Track the future so shutdown can be clean (optional tracking)
        activeFutures.add(f);
    }
    
    /**
     * Generate final conclusion video
     */
    private void generateConclusionVideo(String question, ThoughtFrame finalThought) {
        ThoughtFrame conclusion = new ThoughtFrame(
            "AI conclusion: " + extractConcept(finalThought.thought),
            finalThought.entropy,
            finalThought.confidence,
            System.currentTimeMillis(),
            true,
            "\uD83D\uDCA5" // Conclusion indicator
        );
        queueVideoGeneration(conclusion, true);
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
    // Track active futures for clean shutdown (replaces private executor tracking)
    private final List<Future<?>> activeFutures = new ArrayList<>();

    public void awaitVisuals() {
        for (Future<?> future : activeFutures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (java.util.concurrent.ExecutionException e) {
                System.out.println("Visual job failed: " + e.getCause());
            }
        }
        activeFutures.clear();
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
               Pending videos: %d
               Auto-visualization: %s
               Thoughts per video: %d
               Max videos/question: %d
            """,
            totalConversations, totalThoughts, totalVideos,
            pendingVideos.get(),
            autoVisualize ? "ON" : "OFF", thoughtsPerVideo, maxVideosPerQuestion);
    }
    
    // Configuration setters
    public void setAutoVisualize(boolean enabled) { this.autoVisualize = enabled; }
    public void setThoughtsPerVideo(int n) { this.thoughtsPerVideo = n; }
    public void setMinEntropyChange(double threshold) { this.minEntropyChange = threshold; }
    public void setMaxVideosPerQuestion(int maxVideos) { this.maxVideosPerQuestion = maxVideos; }
    public void setMaxPendingVideos(int maxPending) { this.maxPendingVideos = maxPending; }
}
