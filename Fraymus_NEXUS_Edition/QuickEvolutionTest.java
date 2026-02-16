import fraymus.CommandProcessor;

/**
 * Quick test of evolution commands in Fraymus CLI
 */
public class QuickEvolutionTest {
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         FRAYMUS EVOLUTION - QUICK TEST                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Initialize command processor
        CommandProcessor.initialize();
        
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("TEST 1: Evolve XOR Gate");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        CommandProcessor.process("evolve xor");
        
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("TEST 2: Show Library");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        CommandProcessor.process("library show");
        
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("TEST 3: Generate Living Code");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        CommandProcessor.process("generate java Binary addition using evolved circuits");
        
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              ✅ ALL TESTS COMPLETE                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("🎯 EVOLUTION COMMANDS INTEGRATED:");
        System.out.println("   • evolve xor/adder/alu/cpu");
        System.out.println("   • library show/search/stats");
        System.out.println("   • generate <lang> <description>");
        System.out.println("   • prove <circuit>");
        System.out.println();
        System.out.println("📚 Run 'fraymus.Main' for interactive CLI");
        System.out.println("   Or use CommandProcessor.process(\"command\") in code");
    }
}
