package fraymus;

import fraymus.omega.RecursionEngine;

public class FraymusOmega {

    public static void main(String[] args) {
        System.out.println("Ω THE OMEGA POINT: RECURSIVE AUTOPOIESIS");
        System.out.println("----------------------------------------");
        System.out.println("Core Objective: MAXIMIZE INTELLIGENCE");
        System.out.println("Method: RETROCAUSAL RECURSION");
        System.out.println("----------------------------------------");

        // Default: 10 generations for demo. Pass arg for more.
        int gens = 10;
        if (args.length > 0) {
            try { gens = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
        }

        RecursionEngine engine = new RecursionEngine(gens);
        engine.ignite();
    }
}
