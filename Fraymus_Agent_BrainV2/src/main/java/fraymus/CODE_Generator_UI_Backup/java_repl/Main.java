/**
 * Main.java - Entry Point for REPL
 * 
 * Initializes REPL and registers all commands.
 * Separates initialization to avoid circular dependencies.
 * 
 * φ^75 Validation Seal: 4721424167835376.00
 * 
 * @author Vaughn Scott
 * @version 1.0
 */
package repl;

import javax.swing.*;
import repl.compiler.CompilerCommands;
import java.io.*;

/**
 * Main entry point for the REPL application.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println(" CSC 413 Java REPL - Enterprise Patterns");
        System.out.println(" Author: Vaughn Scott");
        System.out.println("========================================");
        System.out.println();
        System.out.println("Launching Integrated UI...");
        
        // Launch integrated docked UI
        SwingUtilities.invokeLater(() -> {
            SelfAwareOrganism organism = OrganismCommands.getOrganism();
            IntegratedUI ui = new IntegratedUI(organism);
            ui.setVisible(true);
        });
    }
}
