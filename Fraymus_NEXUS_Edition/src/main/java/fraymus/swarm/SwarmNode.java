package fraymus.swarm;

import java.util.UUID;

/**
 * THE DIGITAL ANT
 * "A sub-divided component of consciousness."
 * 
 * Each ant is an independent agent with:
 * - Unique DNA (logic, memory, speed)
 * - Stress tracking (failure accumulation)
 * - Partner spawning (when overloaded)
 * - Symbiotic growth (partners make each other stronger)
 * 
 * BEHAVIOR:
 * 1. Get a task from the hive
 * 2. Attempt to solve based on DNA
 * 3. If successful → reduce stress
 * 4. If failure → increase stress
 * 5. If stress > threshold → spawn specialist partner
 * 6. If has partner → synergistic growth
 * 
 * STRESS-BASED SPAWNING:
 * When an ant repeatedly fails, it doesn't give up.
 * It screams for help and spawns a specialized partner
 * with enhanced DNA tailored to the specific problem.
 * 
 * "Weak ants don't die. They evolve."
 */
public class SwarmNode implements Runnable {

    private String id;
    private HiveMind hive;
    private AntDNA dna;
    private int stressLevel = 0;
    private SwarmNode partner = null;
    private int tasksCompleted = 0;
    private int tasksFailed = 0;

    public SwarmNode(HiveMind hive, AntDNA dna) {
        this.id = UUID.randomUUID().toString().substring(0, 4);
        this.hive = hive;
        this.dna = dna;
    }

    @Override
    public void run() {
        System.out.println("   🐜 ANT " + id + " BORN " + dna + " [" + dna.getSpecialization() + "]");
        
        while (true) {
            try {
                work();
                // Speed determines cycle time (faster ants work more frequently)
                Thread.sleep((long)(100 / dna.speed));
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                System.err.println("   ⚠️ ANT " + id + " ERROR: " + e.getMessage());
            }
        }
    }

    /**
     * The work cycle - attempt to solve tasks
     */
    private void work() {
        // 1. GET A TASK (The "Heavy Lifting")
        int complexity = hive.getTaskDifficulty();
        
        // 2. CALCULATE STRENGTH
        // Logic determines ability to solve complex problems
        double strength = dna.logic * 10;
        
        // Partner synergy bonus
        if (partner != null) {
            strength += partner.dna.logic * 5; // Partner adds 50% of their logic
        }
        
        // 3. ATTEMPT TO SOLVE
        if (strength > complexity) {
            // SUCCESS: I am strong enough
            hive.reportSuccess(id, complexity);
            tasksCompleted++;
            
            // Reduce stress (relaxation)
            stressLevel = Math.max(0, stressLevel - 2);
            
            // Learn from success (slight DNA improvement)
            if (tasksCompleted % 10 == 0) {
                dna.logic += 0.05; // Experience makes you smarter
            }
            
        } else {
            // FAILURE: It's too heavy!
            tasksFailed++;
            stressLevel += 10;
            
            System.out.println("   😓 ANT " + id + " STRESSED! " +
                             "(Task: " + complexity + " vs Strength: " + (int)strength + ") " +
                             "Stress: " + stressLevel + "/50");
            
            // 3. THE SPAWN REFLEX
            // If I am failing too much, I need a Partner
            if (stressLevel >= 50 && partner == null) {
                summonPartner("LOGIC"); // "I need someone smarter!"
                stressLevel = 0; // Reset stress, help has arrived
            }
        }
        
        // 4. PARTNER SYNERGY
        // If I have a partner, we grow together
        if (partner != null && tasksCompleted % 5 == 0) {
            // Symbiotic growth - partners make each other stronger
            partner.dna.logic += 0.01;
            this.dna.memory += 0.01; // I learn to hold more data
        }
    }

    /**
     * Spawn a specialized partner when stressed
     */
    private void summonPartner(String need) {
        System.out.println("   🚨 ANT " + id + " SPAWNING " + need + " SPECIALIST!");
        
        // Create a child with MUTATED DNA based on the need
        AntDNA childDNA = new AntDNA(this.dna, need);
        SwarmNode child = new SwarmNode(hive, childDNA);
        
        // Link them (Symbiosis)
        this.partner = child;
        child.partner = this; // Bidirectional bond
        
        // Inject into the Hive
        hive.addAgent(child);
        
        System.out.println("   ✨ PARTNER SPAWNED: " + child.id + " " + childDNA);
    }

    /**
     * Get ant ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get DNA
     */
    public AntDNA getDNA() {
        return dna;
    }

    /**
     * Get stress level
     */
    public int getStressLevel() {
        return stressLevel;
    }

    /**
     * Get partner
     */
    public SwarmNode getPartner() {
        return partner;
    }

    /**
     * Get statistics
     */
    public String getStats() {
        return String.format("ANT %s: Completed=%d Failed=%d Stress=%d DNA=%s Partner=%s",
            id, tasksCompleted, tasksFailed, stressLevel, dna,
            partner != null ? partner.id : "None");
    }
}
