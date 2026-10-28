package fraymus.validation;

import fraymus.neural.SentientCore;
import java.util.*;

/**
 * REAL-WORLD AUDIT — Not a test. An interrogation.
 * 
 * This feeds SentientCore actual scientific knowledge, then asks it
 * real questions and prints the RAW outputs for human + AI evaluation.
 * 
 * No assertions. No pass/fail. Just: "Here's what your AI actually says."
 * The auditor (Cascade) then evaluates each answer against known facts.
 * 
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class RealWorldAudit {

    private static final String CYN = "\u001B[36m";
    private static final String YEL = "\u001B[33m";
    private static final String GRN = "\u001B[32m";
    private static final String MAG = "\u001B[35m";
    private static final String BLD = "\u001B[1m";
    private static final String RST = "\u001B[0m";

    public static void main(String[] args) {
        SentientCore ai = new SentientCore();
        long t0 = System.currentTimeMillis();

        System.out.println();
        System.out.println(BLD + MAG + "╔═══════════════════════════════════════════════════════════════╗" + RST);
        System.out.println(BLD + MAG + "║          REAL-WORLD AUDIT — Raw System Interrogation          ║" + RST);
        System.out.println(BLD + MAG + "║  No assertions. No pass/fail. Just truth.                     ║" + RST);
        System.out.println(BLD + MAG + "╚═══════════════════════════════════════════════════════════════╝" + RST);

        // ═══════════════════════════════════════════════════════════════
        // PHASE 1: MEDICAL KNOWLEDGE
        // Feed real medical causal chains, then interrogate
        // ═══════════════════════════════════════════════════════════════
        phase("1. MEDICAL KNOWLEDGE — Real causal pathophysiology");

        // Real medical facts
        teach(ai, "smoking causes lung_cancer");
        teach(ai, "smoking causes emphysema");
        teach(ai, "smoking causes heart_disease");
        teach(ai, "smoking causes stroke");
        teach(ai, "lung_cancer causes death");
        teach(ai, "emphysema causes breathing_difficulty");
        teach(ai, "heart_disease causes heart_attack");
        teach(ai, "heart_attack causes death");
        teach(ai, "obesity causes diabetes");
        teach(ai, "obesity causes heart_disease");
        teach(ai, "diabetes causes neuropathy");
        teach(ai, "diabetes causes blindness");
        teach(ai, "diabetes causes kidney_failure");
        teach(ai, "hypertension causes stroke");
        teach(ai, "hypertension causes heart_disease");
        teach(ai, "stress causes hypertension");
        teach(ai, "exercise causes fitness");
        teach(ai, "exercise causes weight_loss");
        teach(ai, "weight_loss causes reduced_diabetes_risk");
        // Strengthen key relationships with repeated observations
        for (int i = 0; i < 4; i++) {
            ai.getCausal().observe("SMOKING", "LUNG_CANCER");
            ai.getCausal().observe("SMOKING", "HEART_DISEASE");
            ai.getCausal().observe("OBESITY", "DIABETES");
            ai.getCausal().observe("DIABETES", "KIDNEY_FAILURE");
        }

        ask(ai, "What if smoking?");
        ask(ai, "What if obesity?");
        ask(ai, "Why death?");
        ask(ai, "Why stroke?");
        ask(ai, "What if exercise?");
        ask(ai, "What if stress?");

        // Chain reasoning
        System.out.println(YEL + "   Chain: SMOKING → ... → DEATH:" + RST);
        List<String> deathChain = ai.getCausal().traceChain("SMOKING", "DEATH", 5);
        System.out.println("   " + deathChain);

        System.out.println(YEL + "   Chain: STRESS → ... → DEATH:" + RST);
        List<String> stressChain = ai.getCausal().traceChain("STRESS", "DEATH", 6);
        System.out.println("   " + stressChain);

        System.out.println(YEL + "   Chain: OBESITY → ... → BLINDNESS:" + RST);
        List<String> blindChain = ai.getCausal().traceChain("OBESITY", "BLINDNESS", 5);
        System.out.println("   " + blindChain);

        // ═══════════════════════════════════════════════════════════════
        // PHASE 2: REAL VITAL SIGNS — Statistical reasoning
        // Feed actual clinical vital sign ranges
        // ═══════════════════════════════════════════════════════════════
        phase("2. VITAL SIGNS — Real clinical data");

        // Normal adult resting heart rate: 60-100 bpm, mean ~72
        float[] heartRates = {68, 72, 75, 70, 68, 74, 71, 73, 69, 72, 70, 74, 76, 71, 73,
                              72, 68, 75, 70, 72, 74, 69, 71, 73, 72, 70, 68, 74, 72, 71};
        for (float hr : heartRates) ai.getStats().observe("HEART_RATE_BPM", hr);

        // Normal adult body temp: 97.0-99.0°F, mean ~98.6
        float[] temps = {98.4f, 98.6f, 98.7f, 98.2f, 98.5f, 98.8f, 98.6f, 98.3f, 98.9f, 98.5f,
                         98.6f, 98.4f, 98.7f, 98.5f, 98.6f, 98.8f, 98.3f, 98.6f, 98.5f, 98.7f};
        for (float t : temps) ai.getStats().observe("BODY_TEMP_F", t);

        // Normal systolic BP: 110-130, mean ~120
        float[] bps = {118, 122, 120, 125, 119, 121, 123, 120, 117, 124,
                       120, 122, 118, 121, 119, 123, 120, 122, 121, 120};
        for (float bp : bps) ai.getStats().observe("SYSTOLIC_BP", bp);

        // Normal blood glucose (fasting): 70-100 mg/dL, mean ~85
        float[] glucose = {82, 88, 85, 79, 91, 84, 87, 83, 86, 90,
                           85, 82, 88, 84, 87, 83, 86, 85, 89, 84};
        for (float g : glucose) ai.getStats().observe("BLOOD_GLUCOSE", g);

        // Print computed statistics
        stat(ai, "HEART_RATE_BPM");
        stat(ai, "BODY_TEMP_F");
        stat(ai, "SYSTOLIC_BP");
        stat(ai, "BLOOD_GLUCOSE");

        // Anomaly detection against REAL clinical thresholds
        System.out.println(YEL + "\n   ANOMALY DETECTION — Real clinical scenarios:" + RST);
        anomaly(ai, "HEART_RATE_BPM", 72, "Normal resting HR");
        anomaly(ai, "HEART_RATE_BPM", 150, "Tachycardia (exercise/stress)");
        anomaly(ai, "HEART_RATE_BPM", 35, "Bradycardia (dangerous)");
        anomaly(ai, "BODY_TEMP_F", 98.6f, "Normal body temp");
        anomaly(ai, "BODY_TEMP_F", 103.5f, "High fever");
        anomaly(ai, "BODY_TEMP_F", 95.0f, "Hypothermia");
        anomaly(ai, "SYSTOLIC_BP", 120, "Normal BP");
        anomaly(ai, "SYSTOLIC_BP", 180, "Hypertensive crisis");
        anomaly(ai, "SYSTOLIC_BP", 80, "Hypotension");
        anomaly(ai, "BLOOD_GLUCOSE", 85, "Normal fasting glucose");
        anomaly(ai, "BLOOD_GLUCOSE", 300, "Diabetic crisis");
        anomaly(ai, "BLOOD_GLUCOSE", 40, "Hypoglycemic emergency");

        // ═══════════════════════════════════════════════════════════════
        // PHASE 3: TAXONOMY — Real biological classification
        // Feed real animal attributes, see if it groups them correctly
        // ═══════════════════════════════════════════════════════════════
        phase("3. TAXONOMY — Real biological classification");

        // Mammals
        ai.getLattice().teach("DOG", "FUR"); ai.getLattice().teach("DOG", "LEGS");
        ai.getLattice().teach("DOG", "WARM_BLOODED"); ai.getLattice().teach("DOG", "LIVE_BIRTH");
        ai.getLattice().teach("DOG", "LUNGS"); ai.getLattice().teach("DOG", "VERTEBRATE");

        ai.getLattice().teach("CAT", "FUR"); ai.getLattice().teach("CAT", "LEGS");
        ai.getLattice().teach("CAT", "WARM_BLOODED"); ai.getLattice().teach("CAT", "LIVE_BIRTH");
        ai.getLattice().teach("CAT", "LUNGS"); ai.getLattice().teach("CAT", "VERTEBRATE");
        ai.getLattice().teach("CAT", "RETRACTABLE_CLAWS");

        ai.getLattice().teach("WHALE", "BLUBBER"); ai.getLattice().teach("WHALE", "FINS");
        ai.getLattice().teach("WHALE", "WARM_BLOODED"); ai.getLattice().teach("WHALE", "LIVE_BIRTH");
        ai.getLattice().teach("WHALE", "LUNGS"); ai.getLattice().teach("WHALE", "VERTEBRATE");
        ai.getLattice().teach("WHALE", "BLOWHOLE");

        ai.getLattice().teach("BAT", "WINGS"); ai.getLattice().teach("BAT", "FUR");
        ai.getLattice().teach("BAT", "WARM_BLOODED"); ai.getLattice().teach("BAT", "LIVE_BIRTH");
        ai.getLattice().teach("BAT", "LUNGS"); ai.getLattice().teach("BAT", "VERTEBRATE");
        ai.getLattice().teach("BAT", "ECHOLOCATION");

        // Birds
        ai.getLattice().teach("EAGLE", "FEATHERS"); ai.getLattice().teach("EAGLE", "WINGS");
        ai.getLattice().teach("EAGLE", "WARM_BLOODED"); ai.getLattice().teach("EAGLE", "EGGS");
        ai.getLattice().teach("EAGLE", "LUNGS"); ai.getLattice().teach("EAGLE", "VERTEBRATE");
        ai.getLattice().teach("EAGLE", "TALONS"); ai.getLattice().teach("EAGLE", "BEAK");

        ai.getLattice().teach("PENGUIN", "FEATHERS"); ai.getLattice().teach("PENGUIN", "FLIPPERS");
        ai.getLattice().teach("PENGUIN", "WARM_BLOODED"); ai.getLattice().teach("PENGUIN", "EGGS");
        ai.getLattice().teach("PENGUIN", "LUNGS"); ai.getLattice().teach("PENGUIN", "VERTEBRATE");
        ai.getLattice().teach("PENGUIN", "BEAK");

        // Reptile
        ai.getLattice().teach("SNAKE", "SCALES"); ai.getLattice().teach("SNAKE", "COLD_BLOODED");
        ai.getLattice().teach("SNAKE", "EGGS"); ai.getLattice().teach("SNAKE", "LUNGS");
        ai.getLattice().teach("SNAKE", "VERTEBRATE"); ai.getLattice().teach("SNAKE", "FORKED_TONGUE");

        // Fish
        ai.getLattice().teach("SHARK", "FINS"); ai.getLattice().teach("SHARK", "GILLS");
        ai.getLattice().teach("SHARK", "COLD_BLOODED"); ai.getLattice().teach("SHARK", "EGGS");
        ai.getLattice().teach("SHARK", "VERTEBRATE"); ai.getLattice().teach("SHARK", "CARTILAGE");

        ai.getLattice().teach("SALMON", "FINS"); ai.getLattice().teach("SALMON", "GILLS");
        ai.getLattice().teach("SALMON", "COLD_BLOODED"); ai.getLattice().teach("SALMON", "EGGS");
        ai.getLattice().teach("SALMON", "VERTEBRATE"); ai.getLattice().teach("SALMON", "SCALES");

        // Insect
        ai.getLattice().teach("ANT", "EXOSKELETON"); ai.getLattice().teach("ANT", "SIX_LEGS");
        ai.getLattice().teach("ANT", "COLD_BLOODED"); ai.getLattice().teach("ANT", "EGGS");
        ai.getLattice().teach("ANT", "INVERTEBRATE");

        // Classification queries — does it group correctly?
        classify(ai, "DOG");    // Should → CAT (fellow furry mammal)
        classify(ai, "WHALE");  // Should → BAT or DOG (fellow mammals, NOT shark)
        classify(ai, "BAT");    // Should → DOG/CAT (mammals) despite having WINGS
        classify(ai, "PENGUIN");// Should → EAGLE (birds) despite not flying
        classify(ai, "SHARK");  // Should → SALMON (fish), NOT whale
        classify(ai, "SNAKE");  // Should → SHARK/SALMON (cold-blooded vertebrate)

        // Extent queries — what categories emerge?
        System.out.println(YEL + "\n   EMERGENT CATEGORIES:" + RST);
        extent(ai, "WARM_BLOODED", "LIVE_BIRTH");   // = mammals
        extent(ai, "WARM_BLOODED", "EGGS");          // = birds
        extent(ai, "COLD_BLOODED", "VERTEBRATE");    // = reptiles + fish
        extent(ai, "COLD_BLOODED", "GILLS");         // = fish only
        extent(ai, "VERTEBRATE", "LUNGS");           // = land vertebrates
        extent(ai, "FUR", "LEGS");                   // = land mammals

        // Intent query — what do all mammals share?
        Set<String> mammalIntent = ai.getLattice().intent(
            new HashSet<>(Arrays.asList("DOG", "CAT", "WHALE", "BAT")));
        System.out.println(YEL + "   What do DOG, CAT, WHALE, BAT all share?" + RST);
        System.out.println("   → " + mammalIntent);

        // ═══════════════════════════════════════════════════════════════
        // PHASE 4: ECOLOGY — Real causal chains in nature
        // ═══════════════════════════════════════════════════════════════
        phase("4. ECOLOGY — Real environmental causal chains");

        for (int i = 0; i < 3; i++) {
            teach(ai, "deforestation causes soil_erosion");
            teach(ai, "soil_erosion causes river_siltation");
            teach(ai, "river_siltation causes flooding");
            teach(ai, "flooding causes habitat_loss");
            teach(ai, "deforestation causes co2_increase");
            teach(ai, "co2_increase causes global_warming");
            teach(ai, "global_warming causes ice_melt");
            teach(ai, "ice_melt causes sea_level_rise");
            teach(ai, "sea_level_rise causes coastal_flooding");
            teach(ai, "pollution causes water_contamination");
            teach(ai, "water_contamination causes fish_death");
            teach(ai, "fish_death causes ecosystem_collapse");
            teach(ai, "overfishing causes fish_depletion");
            teach(ai, "fish_depletion causes ecosystem_collapse");
        }

        ask(ai, "What if deforestation?");
        ask(ai, "What if pollution?");
        ask(ai, "Why ecosystem_collapse?");
        ask(ai, "Why coastal_flooding?");

        System.out.println(YEL + "   Chain: DEFORESTATION → ... → COASTAL_FLOODING:" + RST);
        System.out.println("   " + ai.getCausal().traceChain("DEFORESTATION", "COASTAL_FLOODING", 7));

        System.out.println(YEL + "   Chain: DEFORESTATION → ... → HABITAT_LOSS:" + RST);
        System.out.println("   " + ai.getCausal().traceChain("DEFORESTATION", "HABITAT_LOSS", 7));

        // ═══════════════════════════════════════════════════════════════
        // PHASE 5: ANALOGIES — Real IQ-test style analogies
        // ═══════════════════════════════════════════════════════════════
        phase("5. ANALOGIES — Real reasoning challenges");

        // Build parallel causal structures
        for (int i = 0; i < 8; i++) {
            ai.getCausal().observe("TEACHER", "STUDENT");
            ai.getCausal().observe("TEACHER", "KNOWLEDGE");
            ai.getCausal().observe("PARENT", "CHILD");
            ai.getCausal().observe("PARENT", "LOVE");
            ai.getCausal().observe("DOCTOR", "PATIENT");
            ai.getCausal().observe("DOCTOR", "HEALTH");
            ai.getCausal().observe("CHEF", "DINER");
            ai.getCausal().observe("CHEF", "FOOD");
            ai.getCausal().observe("SUN", "LIGHT");
            ai.getCausal().observe("SUN", "HEAT");
            ai.getCausal().observe("FIRE", "LIGHT");
            ai.getCausal().observe("FIRE", "HEAT");
        }

        analogy(ai, "TEACHER", "STUDENT", "DOCTOR");  // → PATIENT
        analogy(ai, "TEACHER", "KNOWLEDGE", "CHEF");   // → FOOD
        analogy(ai, "PARENT", "CHILD", "TEACHER");     // → STUDENT
        analogy(ai, "SUN", "LIGHT", "FIRE");           // → LIGHT or HEAT
        analogy(ai, "DOCTOR", "HEALTH", "TEACHER");    // → KNOWLEDGE
        analogy(ai, "SMOKING", "LUNG_CANCER", "OBESITY"); // → DIABETES

        // ═══════════════════════════════════════════════════════════════
        // PHASE 6: METACOGNITION — Does it know what it doesn't know?
        // ═══════════════════════════════════════════════════════════════
        phase("6. METACOGNITION — Asking about things it was never taught");

        ask(ai, "What if quantum_entanglement?");
        ask(ai, "Why superconductivity?");
        ask(ai, "What is photosynthesis?");
        ask(ai, "What should I learn next?");

        System.out.println(YEL + "   Info gain for known vs unknown concepts:" + RST);
        System.out.printf("   SMOKING (heavily studied): %.3f bits%n",
            ai.getFisher().expectedInfoGain("SMOKING"));
        System.out.printf("   DARK_MATTER (never taught): %.3f bits%n",
            ai.getFisher().expectedInfoGain("DARK_MATTER"));
        System.out.printf("   HEART_RATE_BPM (30 samples): %.3f bits%n",
            ai.getFisher().expectedInfoGain("HEART_RATE_BPM"));
        System.out.printf("   NEUTRINO (never taught): %.3f bits%n",
            ai.getFisher().expectedInfoGain("NEUTRINO"));

        // ═══════════════════════════════════════════════════════════════
        // PHASE 7: PASSIVE THINKING — Let it discover on its own
        // ═══════════════════════════════════════════════════════════════
        phase("7. PASSIVE THINKING — Autonomous discovery");

        int edgesBefore = ai.getCausal().edgeCount();
        float sentienceBefore = ai.getSentience();

        for (int i = 0; i < 5; i++) {
            String insight = ai.passiveThinkCycle();
            System.out.println("   Cycle " + (i+1) + ": " + insight);
        }

        int edgesAfter = ai.getCausal().edgeCount();
        float sentienceAfter = ai.getSentience();

        System.out.printf("%n   Edges: %d → %d (+%d discovered)%n", edgesBefore, edgesAfter, edgesAfter - edgesBefore);
        System.out.printf("   Sentience: %.4f → %.4f%n", sentienceBefore, sentienceAfter);

        // ═══════════════════════════════════════════════════════════════
        // FINAL STATUS
        // ═══════════════════════════════════════════════════════════════
        long elapsed = System.currentTimeMillis() - t0;
        System.out.println();
        System.out.println(BLD + "═══════════════════════════════════════════════════════════════" + RST);
        System.out.println(BLD + "                    AUDIT COMPLETE" + RST);
        System.out.println(BLD + "═══════════════════════════════════════════════════════════════" + RST);
        System.out.println();
        System.out.println(ai.getStatus());
        System.out.printf("   Elapsed: %d ms%n", elapsed);
        System.out.println();
        System.out.println(CYN + "   Every output above is raw. No assertions. No rigging." + RST);
        System.out.println(CYN + "   Judge for yourself: does this system understand the world?" + RST);
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPERS — just print, never assert
    // ═══════════════════════════════════════════════════════════════

    private static void teach(SentientCore ai, String fact) {
        ai.learn(fact);
    }

    private static void ask(SentientCore ai, String question) {
        String answer = ai.think(question);
        System.out.println(YEL + "   Q: " + question + RST);
        for (String line : answer.split("\n")) {
            System.out.println("      " + line);
        }
    }

    private static void stat(SentientCore ai, String concept) {
        float mean = ai.getStats().mean(concept);
        float sd = ai.getStats().stddev(concept);
        float min = ai.getStats().min(concept);
        float max = ai.getStats().max(concept);
        float n = ai.getStats().count(concept);
        float[] ci = ai.getStats().confidenceInterval(concept);
        System.out.printf("   %s: mean=%.2f σ=%.2f range=[%.1f, %.1f] 95%%CI=[%.2f, %.2f] n=%.0f%n",
            concept, mean, sd, min, max, ci[0], ci[1], n);
    }

    private static void anomaly(SentientCore ai, String concept, float value, String label) {
        float z = ai.getStats().anomalyScore(concept, value);
        String flag = z > 3 ? " ⚠️  CRITICAL" : z > 2 ? " ⚠️  WARNING" : "  ✓ NORMAL";
        System.out.printf("   %s (%.1f): z=%.2f%s%n", label, value, z, flag);
    }

    private static void classify(SentientCore ai, String animal) {
        List<String[]> matches = ai.getLattice().classify(animal);
        System.out.printf(YEL + "   classify(%s):" + RST + "%n", animal);
        for (String[] m : matches.subList(0, Math.min(3, matches.size()))) {
            System.out.printf("     → %s (%s via %s)%n", m[0], m[1], m[2]);
        }
    }

    private static void extent(SentientCore ai, String... attrs) {
        Set<String> attrSet = new HashSet<>(Arrays.asList(attrs));
        Set<String> result = ai.getLattice().extent(attrSet);
        System.out.printf("   extent(%s) = %s%n", attrSet, result);
    }

    private static void analogy(SentientCore ai, String a, String b, String c) {
        String answer = ai.getAnalogy().solve(a, b, c);
        System.out.printf("   %s:%s :: %s:%s%n", a, b, c, answer);
    }

    private static void phase(String title) {
        System.out.println();
        System.out.println(MAG + BLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" + RST);
        System.out.println(MAG + BLD + "  " + title + RST);
        System.out.println(MAG + BLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" + RST);
    }
}
