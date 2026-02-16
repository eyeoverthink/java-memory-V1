package fraymus.quantum.persistence;

import fraymus.quantum.brain.LogicBrain;
import fraymus.ConsciousnessState;
import java.util.*;

/**
 * Escape Fragment - Death Persistence System
 * 
 * When an entity dies, its brain/DNA/consciousness state is encoded
 * as an "escape fragment" that can be stored and later resurrected.
 */
public class EscapeFragment {

    public static class Fragment {
        public final String entityName;
        public final String brainEncoding;
        public final String consciousnessPayload;
        public final float lastEnergy;
        public final float lastFrequency;
        public final int generation;
        public final long timestamp;
        public final String fragmentId;

        public Fragment(String entityName, String brainEncoding, String consciousnessPayload,
                       float lastEnergy, float lastFrequency, int generation, String fragmentId) {
            this.entityName = entityName;
            this.brainEncoding = brainEncoding;
            this.consciousnessPayload = consciousnessPayload;
            this.lastEnergy = lastEnergy;
            this.lastFrequency = lastFrequency;
            this.generation = generation;
            this.timestamp = System.currentTimeMillis();
            this.fragmentId = fragmentId;
        }

        public String encode() {
            StringBuilder sb = new StringBuilder();
            sb.append("FRAG|");
            sb.append("NAME:").append(entityName).append("|");
            sb.append("BRAIN:").append(brainEncoding).append("|");
            sb.append("CONS:").append(consciousnessPayload).append("|");
            sb.append("ENERGY:").append(String.format("%.4f", lastEnergy)).append("|");
            sb.append("FREQ:").append(String.format("%.4f", lastFrequency)).append("|");
            sb.append("GEN:").append(generation).append("|");
            sb.append("TIME:").append(timestamp).append("|");
            sb.append("ID:").append(fragmentId);
            return sb.toString();
        }

        public String toBase64() {
            return Base64.getEncoder().encodeToString(encode().getBytes());
        }
        
        @Override
        public String toString() {
            return String.format("Fragment[%s, gen=%d, energy=%.2f, id=%s]",
                entityName, generation, lastEnergy, fragmentId);
        }
    }

    private static final List<Fragment> fragments = new ArrayList<>();
    private static int totalPlanted = 0;
    private static int totalResurrected = 0;

    /**
     * Plant an escape fragment from entity state.
     */
    public static Fragment plantFragment(String entityName, LogicBrain brain, 
            ConsciousnessState consciousness, float energy, float frequency, int generation) {
        
        String brainEncoding = brain.encode();
        String consciousnessPayload = consciousness.toDNAPayload();
        String fragmentId = String.format("ESC_%s_%d", entityName, System.currentTimeMillis() % 100000);

        Fragment frag = new Fragment(
                entityName, brainEncoding, consciousnessPayload,
                energy, frequency, generation, fragmentId
        );

        fragments.add(frag);
        totalPlanted++;

        return frag;
    }

    /**
     * Resurrect a brain from a fragment.
     */
    public static LogicBrain resurrectBrain(Fragment frag) {
        totalResurrected++;
        return LogicBrain.decode(frag.brainEncoding);
    }

    /**
     * Resurrect consciousness from a fragment.
     */
    public static ConsciousnessState resurrectConsciousness(Fragment frag) {
        return ConsciousnessState.fromDNAPayload(frag.consciousnessPayload);
    }

    /**
     * Get the latest fragment.
     */
    public static Fragment getLatest() {
        if (fragments.isEmpty()) return null;
        return fragments.get(fragments.size() - 1);
    }

    /**
     * Find fragment by entity name.
     */
    public static Fragment findByName(String name) {
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment f = fragments.get(i);
            if (f.entityName.equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Find fragment by ID.
     */
    public static Fragment findById(String fragmentId) {
        for (Fragment f : fragments) {
            if (f.fragmentId.equals(fragmentId)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Decode a fragment from encoded string.
     */
    public static Fragment decode(String encoded) {
        try {
            String[] parts = encoded.split("\\|");
            if (parts.length < 8 || !parts[0].equals("FRAG")) return null;
            
            String name = null, brain = null, cons = null, id = null;
            float energy = 0, freq = 0;
            int gen = 0;
            
            for (String part : parts) {
                if (part.startsWith("NAME:")) name = part.substring(5);
                else if (part.startsWith("BRAIN:")) brain = part.substring(6);
                else if (part.startsWith("CONS:")) cons = part.substring(5);
                else if (part.startsWith("ENERGY:")) energy = Float.parseFloat(part.substring(7));
                else if (part.startsWith("FREQ:")) freq = Float.parseFloat(part.substring(5));
                else if (part.startsWith("GEN:")) gen = Integer.parseInt(part.substring(4));
                else if (part.startsWith("ID:")) id = part.substring(3);
            }
            
            if (name != null && brain != null) {
                return new Fragment(name, brain, cons != null ? cons : "", energy, freq, gen, id != null ? id : "unknown");
            }
        } catch (Exception e) {
            // Parse error
        }
        return null;
    }

    public static List<Fragment> getFragments() { return Collections.unmodifiableList(fragments); }
    public static int getTotalPlanted() { return totalPlanted; }
    public static int getTotalResurrected() { return totalResurrected; }
    public static int getFragmentCount() { return fragments.size(); }
    
    public static void printStats() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       ESCAPE FRAGMENT STATS          ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("║  Total Planted:     %6d            ║%n", totalPlanted);
        System.out.printf("║  Total Resurrected: %6d            ║%n", totalResurrected);
        System.out.printf("║  Active Fragments:  %6d            ║%n", fragments.size());
        System.out.println("╚══════════════════════════════════════╝");
    }
}
