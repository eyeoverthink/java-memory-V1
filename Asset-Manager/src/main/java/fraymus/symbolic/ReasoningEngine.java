package fraymus.symbolic;

/**
 * 🧩 REASONING ENGINE - Transitive Logic
 * "Multi-hop inference without explicit rules"
 * 
 * Traditional AI:
 * - Requires explicit reasoning rules
 * - If-then logic programmed manually
 * - Limited to predefined patterns
 * 
 * Holo-Graph Reasoning:
 * - Transitive inference emerges from vector operations
 * - A→B, B→C automatically enables A→C queries
 * - No explicit rules needed
 * 
 * Example:
 * - Facts: Fraymus→written_in→Java, Java→created_by→Gosling
 * - Query: "Who created the language Fraymus is written in?"
 * - Process: Fraymus→Java→Gosling (2 hops)
 * - Result: Gosling
 * 
 * This is symbolic reasoning via vector algebra.
 */
public class ReasoningEngine {

    private final HoloGraph graph;

    public ReasoningEngine(HoloGraph graph) {
        this.graph = graph;
    }

    /**
     * MULTI-HOP QUERY: Traverse multiple relations
     * 
     * Subject → Rel1 → ? → Rel2 → Answer
     * 
     * Example:
     * - Start: "Fraymus"
     * - Relations: ["written_in", "created_by"]
     * - Path: Fraymus→Java→Gosling
     * - Result: "Gosling"
     * 
     * @param startNode Starting concept
     * @param relations Array of relations to traverse
     * @return Final concept reached
     */
    public String multiHop(String startNode, String[] relations) {
        String current = startNode;
        System.out.print("   [HOPPING] " + current);

        for (String rel : relations) {
            String next = graph.ask(current, rel);
            System.out.print(" --" + rel + "--> " + next);
            
            if (next.equals("???")) {
                System.out.println();
                return "???";
            }
            current = next;
        }
        System.out.println();
        return current;
    }

    /**
     * BIDIRECTIONAL QUERY: Try both forward and inverse
     * 
     * Useful when you don't know the direction of the relation.
     */
    public String bidirectional(String concept1, String relation, String concept2) {
        // Try forward: concept1 --relation--> ?
        String forward = graph.ask(concept1, relation);
        if (forward.equals(concept2)) {
            return "FORWARD: " + concept1 + " --" + relation + "--> " + concept2;
        }

        // Try inverse: ? --relation--> concept2
        String inverse = graph.askInverse(concept2, relation);
        if (inverse.equals(concept1)) {
            return "INVERSE: " + concept1 + " <--" + relation + "-- " + concept2;
        }

        return "NO_RELATION";
    }

    /**
     * VERIFY FACT: Check if a fact exists in the graph
     */
    public boolean verify(String subj, String rel, String obj) {
        String result = graph.ask(subj, rel);
        return result.equals(obj);
    }

    /**
     * EXPLAIN PATH: Show reasoning steps
     */
    public void explain(String startNode, String[] relations) {
        System.out.println("\n   [EXPLANATION]");
        System.out.println("   Start: " + startNode);
        
        String current = startNode;
        for (int i = 0; i < relations.length; i++) {
            String rel = relations[i];
            String next = graph.ask(current, rel);
            
            System.out.println("   Step " + (i + 1) + ": " + current + " --" + rel + "--> " + next);
            
            if (next.equals("???")) {
                System.out.println("   [FAILED] Path broken at step " + (i + 1));
                return;
            }
            current = next;
        }
        
        System.out.println("   Final: " + current);
    }
}
