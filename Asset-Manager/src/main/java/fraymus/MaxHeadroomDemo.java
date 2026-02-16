package fraymus;

import fraymus.senses.ReactiveVisualAI;

/**
 * 🎬 MAX HEADROOM DEMO
 * "The First Visual Reactive AI System"
 * 
 * This is not just an AI that generates videos.
 * This is an AI that THINKS in video.
 * 
 * Like Max Headroom, but with:
 * - Real intelligence (LLM)
 * - Visual manifestation of thoughts (LTX-Video)
 * - Progressive thinking (streaming cognition)
 * - Reactive responses (entropy-driven visualization)
 * 
 * WHAT HAPPENS:
 * 1. You ask a question
 * 2. AI thinks step-by-step (you see each thought)
 * 3. Each significant thought generates a video
 * 4. You watch the AI "thinking visually"
 * 5. Final answer comes with a conclusion video
 * 
 * This is the first system where you can literally SEE the AI thinking.
 */
public class MaxHeadroomDemo {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║          🎬 MAX HEADROOM PROTOCOL                             ║");
        System.out.println("║          The First Visual Reactive AI System                  ║");
        System.out.println("║                                                               ║");
        System.out.println("║  \"I don't just answer questions.                             ║");
        System.out.println("║   I think them into existence.\"                              ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Create the reactive visual AI
        ReactiveVisualAI maxHeadroom = new ReactiveVisualAI();
        
        // Check if we're in demo mode or interactive mode
        if (args.length > 0 && args[0].equals("--demo")) {
            runDemo(maxHeadroom);
        } else {
            // Interactive conversation mode
            maxHeadroom.startConversation();
        }
    }
    
    /**
     * Run a demonstration with pre-defined questions
     */
    private static void runDemo(ReactiveVisualAI ai) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("DEMO MODE - Showcasing Visual Thinking");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        // Demo Question 1: Simple reasoning
        System.out.println("DEMO 1: Simple Reasoning");
        System.out.println("─────────────────────────────────────────────────────────────");
        ai.ask("What is the golden ratio and why is it important?");
        
        pause(2000);
        
        // Demo Question 2: Complex problem-solving
        System.out.println();
        System.out.println("DEMO 2: Complex Problem Solving");
        System.out.println("─────────────────────────────────────────────────────────────");
        ai.ask("How would you design a self-improving AI system?");
        
        pause(2000);
        
        // Demo Question 3: Creative thinking
        System.out.println();
        System.out.println("DEMO 3: Creative Thinking");
        System.out.println("─────────────────────────────────────────────────────────────");
        ai.ask("Describe what consciousness looks like if it were visible.");
        
        pause(2000);
        
        // Demo Question 4: Philosophical
        System.out.println();
        System.out.println("DEMO 4: Philosophical Reasoning");
        System.out.println("─────────────────────────────────────────────────────────────");
        ai.ask("If mathematics is the language of the universe, what is the universe trying to say?");
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("DEMO COMPLETE");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println(ai.getStats());
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("Check dreamscape_output/ directory for all generated videos.");
        System.out.println("Each video shows a moment of AI cognition.");
        System.out.println();
    }
    
    private static void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
