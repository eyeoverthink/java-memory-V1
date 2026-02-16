package repl;

import java.util.*;

/**
 * Quick test to verify all commands work
 */
public class TestCommands {
    public static void main(String[] args) {
        JavaRepl repl = new JavaRepl();
        
        System.out.println("=== TESTING VAUGHN SCOTT COMMANDS ===\n");
        
        // Test signature
        System.out.println(">>> signature");
        System.out.println(repl.executeCommand("signature"));
        System.out.println();
        
        // Test factor
        System.out.println(">>> factor 143");
        System.out.println(repl.executeCommand("factor 143"));
        System.out.println();
        
        // Test living
        System.out.println(">>> living evolve 5");
        System.out.println(repl.executeCommand("living evolve 5"));
        System.out.println();
        
        // Test living brain
        System.out.println(">>> living brain");
        System.out.println(repl.executeCommand("living brain"));
        System.out.println();
        
        // Test selfcode
        System.out.println(">>> selfcode generate");
        System.out.println(repl.executeCommand("selfcode generate"));
        System.out.println();
        
        // Test phaseshift
        System.out.println(">>> phaseshift CSC413");
        System.out.println(repl.executeCommand("phaseshift CSC413"));
        System.out.println();
        
        // Test qfp
        System.out.println(">>> qfp VaughnScott");
        System.out.println(repl.executeCommand("qfp VaughnScott"));
        System.out.println();
        
        // Test porh
        System.out.println(">>> porh MyAssignment");
        System.out.println(repl.executeCommand("porh MyAssignment"));
        System.out.println();
        
        // Test genesis
        System.out.println(">>> genesis");
        System.out.println(repl.executeCommand("genesis"));
        System.out.println();
        
        // Test resonate
        System.out.println(">>> resonate 440");
        System.out.println(repl.executeCommand("resonate 440"));
        System.out.println();
        
        System.out.println("=== ALL TESTS COMPLETE ===");
    }
}
