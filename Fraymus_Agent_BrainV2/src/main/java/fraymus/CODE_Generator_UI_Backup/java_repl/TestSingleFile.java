package repl;

import java.io.File;

public class TestSingleFile {
    public static void main(String[] args) {
        System.out.println("Testing PhilosophersStone with simple Go file...\n");
        
        PhilosophersStone stone = new PhilosophersStone();
        File testFile = new File("D:\\Zip And Send\\Java-Memory\\test_simple.go");
        
        if (!testFile.exists()) {
            System.err.println("Test file not found: " + testFile.getAbsolutePath());
            return;
        }
        
        System.out.println("Target: " + testFile.getName());
        System.out.println("Starting transmutation...\n");
        
        boolean success = stone.assimilate(testFile);
        
        if (success) {
            System.out.println("\n✅ SUCCESS - Check fraymus/evolved/ for output");
        } else {
            System.out.println("\n✗ FAILED - See errors above");
        }
    }
}
