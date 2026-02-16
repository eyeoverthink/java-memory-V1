package fraymus.symbolic;

public class ReasoningEngine {

    private final HoloGraph graph;

    public ReasoningEngine(HoloGraph graph) {
        this.graph = graph;
    }

    /**
     * MULTI-HOP QUERY: Subject -> Rel1 -> ? -> Rel2 -> Answer
     * e.g., "Language of the Creator of Fraymus?"
     * Fraymus --Creator--> User --Language--> Java
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
}
