package fraymus.field;

/**
 * F-0 Kernel Interactive Demonstration
 * 
 * Demonstrates the C-ISA (Continuous Instruction Set Architecture)
 * executing on the continuous field with real-time visualization.
 */
public class F0KernelDemo {
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          FRAYNIX UNIFIED COMPUTATION KERNEL (F-0)            ║");
        System.out.println("║          Continuous Field Computation Simulator            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        F0KernelSimulator kernel = new F0KernelSimulator(20);
        
        System.out.println("=== INITIAL STATE ===");
        System.out.println(kernel.visualizeField());
        System.out.println();
        
        // Demonstration 1: EXC (Excite) instruction
        System.out.println("=== DEMONSTRATION 1: EXC [10], 0.8 (Excitation) ===");
        System.out.println("Injecting positive energy at address 10...");
        kernel.EXC(10, 0.8);
        System.out.println(kernel.visualizeField());
        System.out.println("Field[10] = " + String.format("%.3f", kernel.getFieldAt(10)));
        System.out.println();
        
        // Demonstration 2: INH (Inhibit) instruction
        System.out.println("=== DEMONSTRATION 2: INH [5], 0.6 (Inhibition) ===");
        System.out.println("Injecting negative energy at address 5...");
        kernel.INH(5, 0.6);
        System.out.println(kernel.visualizeField());
        System.out.println("Field[5] = " + String.format("%.3f", kernel.getFieldAt(5)));
        System.out.println();
        
        // Demonstration 3: SUP (Superpose) instruction
        System.out.println("=== DEMONSTRATION 3: SUP [5], [10] (Superposition) ===");
        System.out.println("Entangling field regions at addresses 5 and 10...");
        kernel.SUP(5, 10);
        System.out.println(kernel.visualizeField());
        System.out.println("Field[5] = " + String.format("%.3f", kernel.getFieldAt(5)));
        System.out.println("Field[10] = " + String.format("%.3f", kernel.getFieldAt(10)));
        System.out.println();
        
        // Demonstration 4: CLP (Collapse) instruction
        System.out.println("=== DEMONSTRATION 4: CLP [10] (Collapse) ===");
        System.out.println("Collapsing continuous field at address 10 to discrete state...");
        int collapsed = kernel.CLP(10);
        System.out.println("Collapsed value: " + collapsed);
        System.out.println("Field[10] = " + String.format("%.3f", kernel.getFieldAt(10)));
        System.out.println();
        
        // Demonstration 5: Differential Equation Integration
        System.out.println("=== DEMONSTRATION 5: Differential Equation Integration ===");
        System.out.println("Resetting field and running 100 integration steps...");
        kernel.reset();
        kernel.EXC(10, 1.0);
        
        for (int i = 0; i < 100; i++) {
            kernel.integrateStep();
            if (i % 20 == 0) {
                System.out.println("Step " + i + ": " + kernel.visualizeField());
            }
        }
        System.out.println();
        
        // Demonstration 6: Attractor Sculpting
        System.out.println("=== DEMONSTRATION 6: Attractor Sculpting (Language as Geometry) ===");
        System.out.println("Resetting field...");
        kernel.reset();
        System.out.println("Initial state:");
        System.out.println(kernel.visualizeField());
        
        System.out.println("Sculpting field toward 'APPLE' attractor...");
        for (int i = 0; i < 50; i++) {
            kernel.sculptToAttractor("APPLE");
            kernel.integrateStep();
        }
        System.out.println("After sculpting:");
        System.out.println(kernel.visualizeField());
        System.out.println("Energy to 'APPLE': " + String.format("%.4f", kernel.energyToAttractor("APPLE")));
        System.out.println("Recognized as: " + kernel.recognizeNearestAttractor());
        System.out.println();
        
        // Demonstration 7: Multiple Attractor Competition
        System.out.println("=== DEMONSTRATION 7: Attractor Competition ===");
        System.out.println("Sculpting field toward 'FIRE' attractor...");
        kernel.reset();
        for (int i = 0; i < 30; i++) {
            kernel.sculptToAttractor("FIRE");
            kernel.integrateStep();
        }
        
        System.out.println("After 'FIRE' sculpting:");
        System.out.println(kernel.visualizeField());
        System.out.println("Energy to 'FIRE': " + String.format("%.4f", kernel.energyToAttractor("FIRE")));
        System.out.println("Energy to 'APPLE': " + String.format("%.4f", kernel.energyToAttractor("APPLE")));
        System.out.println("Recognized as: " + kernel.recognizeNearestAttractor());
        System.out.println();
        
        System.out.println("Now sculpting toward 'APPLE' (competition)...");
        for (int i = 0; i < 30; i++) {
            kernel.sculptToAttractor("APPLE");
            kernel.integrateStep();
        }
        
        System.out.println("After 'APPLE' sculpting:");
        System.out.println(kernel.visualizeField());
        System.out.println("Energy to 'FIRE': " + String.format("%.4f", kernel.energyToAttractor("FIRE")));
        System.out.println("Energy to 'APPLE': " + String.format("%.4f", kernel.energyToAttractor("APPLE")));
        System.out.println("Recognized as: " + kernel.recognizeNearestAttractor());
        System.out.println();
        
        // Demonstration 8: Ripple Effect
        System.out.println("=== DEMONSTRATION 8: Energy Ripple (Interrupt as Perturbation) ===");
        kernel.reset();
        System.out.println("Initial state:");
        System.out.println(kernel.visualizeField());
        
        System.out.println("Injecting energy spike at center (address 10)...");
        kernel.EXC(10, 1.0);
        System.out.println(kernel.visualizeField());
        
        System.out.println("Running integration to show ripple propagation...");
        for (int i = 0; i < 20; i++) {
            kernel.integrateStep();
            if (i % 5 == 0) {
                System.out.println("Step " + i + ": " + kernel.visualizeField());
            }
        }
        System.out.println();
        
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    DEMONSTRATION COMPLETE                     ║");
        System.out.println("║                                                              ║");
        System.out.println("║  The F-0 Kernel successfully demonstrates:                  ║");
        System.out.println("║  - Continuous field computation                              ║");
        System.out.println("║  - C-ISA instruction execution                              ║");
        System.out.println("║  - Differential equation integration                      ║");
        System.out.println("║  - Energy ripple effects                                    ║");
        System.out.println("║  - Attractor-based language semantics                      ║");
        System.out.println("║  - Language as geometry (field shaping)                     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
}
