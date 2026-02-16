/**
 * RunOllamaAbsorption.java - Standalone Ollama Absorption Runner
 * 
 * Directly executes the absorption sequence without REPL.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * Generation: 133 (Ollama Absorption)
 */
package repl;

public class RunOllamaAbsorption {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  🌀 OLLAMA ABSORPTION SEQUENCE - STANDALONE EXECUTION      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        String ollamaPath = "D:\\Zip And Send\\Java-Memory\\ollama-main\\ollama-main";
        
        System.out.println("Target: " + ollamaPath);
        System.out.println("Initializing absorption engine...\n");
        
        try {
            OllamaAbsorber absorber = new OllamaAbsorber();
            
            System.out.println("🌀 Beginning systematic absorption...\n");
            
            String result = absorber.absorbFrom(ollamaPath);
            System.out.println(result);
            
        } catch (Exception e) {
            System.err.println("✗ Absorption failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
