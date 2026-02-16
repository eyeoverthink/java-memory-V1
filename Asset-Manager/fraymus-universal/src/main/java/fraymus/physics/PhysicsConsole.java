package fraymus.physics;

import java.util.Scanner;

/**
 * ⚛️ HYPER-PHYSICS CONSOLE
 * Standalone controller for the 17D physics engine.
 * Inject concepts. Watch the mesh bleed.
 */
public class PhysicsConsole {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("⚛️ HYPER-PHYSICS ENGINE ONLINE.");
        
        // 1. CREATE THE BODY
        // We create a Rigid Body made of "LOGIC". It is heavy.
        HyperRigidBody body = new HyperRigidBody("PURE_LOGIC", new Mesh(100));
        System.out.printf("   [SPAWNED] ID: %s | MASS: %.2f | DIMENSIONS: 17%n", body.id, body.dataMass);

        Scanner scanner = new Scanner(System.in);
        
        // SIMULATION LOOP
        Thread sim = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                body.update(0.016); // 60 FPS tick
                try { Thread.sleep(16); } catch (InterruptedException e) { break; }
            }
        }, "HyperPhysics-Sim");
        sim.setDaemon(true);
        sim.start();

        System.out.println("   [CTRL] Type a concept to apply force (e.g., 'CHAOS', 'ORDER', 'LOVE')");
        System.out.println("   [CTRL] Type 'dump' to see tracking data.");
        System.out.println("   [CTRL] Type 'export' to export mesh as OBJ.");
        System.out.println("   [CTRL] Type 'exit' to quit.");
        
        while(true) {
            System.out.print("⚛️> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            
            switch (input.toLowerCase()) {
                case "dump" -> {
                    System.out.println("   📜 TRACKING LOG:");
                    for(String log : body.collisionLog) System.out.println("      " + log);
                    System.out.println("   " + body.getStatus());
                }
                case "export" -> {
                    System.out.println(body.geometry.exportObj());
                }
                case "exit", "quit" -> {
                    System.out.println("⚛️ ENGINE OFFLINE.");
                    return;
                }
                default -> {
                    // APPLY DATA FORCE
                    body.applyDataForce(input);
                    System.out.printf("   💥 IMPACT REGISTERED. Speed: %.4f | Deformation: %.4f%n",
                            body.getHyperSpeed(), body.geometry.getDeformation());
                }
            }
        }
    }
}
