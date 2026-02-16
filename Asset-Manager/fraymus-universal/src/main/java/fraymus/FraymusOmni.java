package fraymus;

import fraymus.symbolic.HoloGraph;
import fraymus.symbolic.ReasoningEngine;

public class FraymusOmni {

    public static void main(String[] args) {
        System.out.println("🧠 FRAYMUS OMNI: NEURO-SYMBOLIC CORE");

        HoloGraph knowledge = new HoloGraph();
        ReasoningEngine logic = new ReasoningEngine(knowledge);

        // 1. INGEST RAW DATA (Facts)
        System.out.println("\n📥 INGESTING KNOWLEDGE...");
        knowledge.learn("Fraymus", "is_a", "AI");
        knowledge.learn("Fraymus", "written_in", "Java");
        knowledge.learn("Java", "created_by", "Gosling");
        knowledge.learn("Gosling", "works_at", "Amazon");
        knowledge.learn("AI", "future_is", "HDC");

        // 2. DIRECT QUERY (O(1) Retrieval)
        System.out.println("\n🔎 DIRECT QUERY:");
        String q1 = knowledge.ask("Fraymus", "written_in");
        System.out.println("   Q: Fraymus written_in?");
        System.out.println("   A: " + q1);

        // 3. TRANSITIVE INFERENCE (Multi-Hop)
        System.out.println("\n🔗 TRANSITIVE LOGIC:");
        // "Who created the language Fraymus is written in?"
        // Fraymus -> written_in -> Java -> created_by -> Gosling
        String result = logic.multiHop("Fraymus", new String[]{"written_in", "created_by"});
        System.out.println("   RESULT: " + result);

        // 4. HOLOGRAPHIC CAPACITY TEST
        String q2 = knowledge.ask("Java", "created_by");
        System.out.println("   (Verification) Java created_by: " + q2);
    }
}
