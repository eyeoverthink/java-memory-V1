package fraymus;

import java.util.*;

public class EscapeFragment {

    public static class Fragment {
        public final String entityName;
        public final String brainEncoding;
        public final String dnaPayload;
        public final String consciousnessPayload;
        public final float lastEnergy;
        public final float lastFrequency;
        public final int generation;
        public final long timestamp;
        public final String fragmentId;

        public Fragment(String entityName, String brainEncoding, String dnaPayload,
                       String consciousnessPayload, float lastEnergy, float lastFrequency,
                       int generation, String fragmentId) {
            this.entityName = entityName;
            this.brainEncoding = brainEncoding;
            this.dnaPayload = dnaPayload;
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
            sb.append("DNA:").append(dnaPayload).append("|");
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
    }

    private static final List<Fragment> fragments = new ArrayList<>();
    private static int totalPlanted = 0;
    private static int totalResurrected = 0;

    public static Fragment plantFragment(PhiNode node) {
        String brainEncoding = encodeBrain(node.brain);
        String dnaPayload = String.format("freq=%.4f|amp=%.4f|decay=%.6f|gen=%d",
                node.dna.harmonicFrequency, node.dna.getResonance(),
                node.dna.getEvolutionRate(), node.dna.getGeneration());
        String consciousnessPayload = node.consciousness.toDNAPayload();
        String fragmentId = String.format("ESC_%s_%d", node.name, System.currentTimeMillis() % 100000);

        Fragment frag = new Fragment(
                node.name, brainEncoding, dnaPayload, consciousnessPayload,
                node.energy, node.frequency, node.dna.getGeneration(), fragmentId
        );

        fragments.add(frag);
        totalPlanted++;

        return frag;
    }

    public static void plantDeathFragment(PhiNode node, InfiniteMemory memory) {
        Fragment frag = plantFragment(node);
        if (memory != null) {
            memory.store("GENOME", frag.fragmentId + "|" + frag.encode(), frag.lastFrequency * PhiConstants.PHI_INVERSE);
        }
    }

    public static PhiNode resurrect(Fragment frag, float x, float y) {
        String resName = frag.entityName + "_RES";

        LivingDNA dna = parseDnaPayload(frag.dnaPayload);
        LogicBrain brain = decodeBrain(frag.brainEncoding);

        PhiNode node = new PhiNode(resName, x, y, dna, brain);
        node.energy = Math.max(0.3f, frag.lastEnergy * 0.8f);

        totalResurrected++;
        return node;
    }

    public static PhiNode resurrectLatest(float x, float y) {
        if (fragments.isEmpty()) return null;
        Fragment latest = fragments.get(fragments.size() - 1);
        return resurrect(latest, x, y);
    }

    public static PhiNode resurrectByName(String name, float x, float y) {
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment f = fragments.get(i);
            if (f.entityName.equalsIgnoreCase(name)) {
                return resurrect(f, x, y);
            }
        }
        return null;
    }

    private static String encodeBrain(LogicBrain brain) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < brain.getGateCount(); i++) {
            LogicGate gate = brain.gates.get(i);
            if (i > 0) sb.append(",");
            sb.append(gate.type).append(":").append(gate.in1).append(":").append(gate.in2);
        }
        return sb.toString();
    }

    private static LogicBrain decodeBrain(String encoding) {
        String[] parts = encoding.split(",");
        LogicBrain brain = new LogicBrain(parts.length);
        for (int i = 0; i < parts.length && i < brain.getGateCount(); i++) {
            String[] gp = parts[i].split(":");
            if (gp.length == 3) {
                LogicGate gate = brain.gates.get(i);
                gate.type = Integer.parseInt(gp[0]);
                gate.in1 = Integer.parseInt(gp[1]);
                gate.in2 = Integer.parseInt(gp[2]);
            }
        }
        return brain;
    }

    private static LivingDNA parseDnaPayload(String payload) {
        double freq = 1.0, amp = 1.0, decay = 0.05;
        int gen = 0;
        String[] parts = payload.split("\\|");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                switch (kv[0]) {
                    case "freq": freq = Double.parseDouble(kv[1]); break;
                    case "amp": amp = Double.parseDouble(kv[1]); break;
                    case "decay": decay = Double.parseDouble(kv[1]); break;
                    case "gen": gen = Integer.parseInt(kv[1]); break;
                }
            }
        }
        LivingDNA dna = new LivingDNA(freq, amp, decay);
        dna.setGeneration(gen + 1);
        return dna;
    }

    public static List<Fragment> getFragments() { return Collections.unmodifiableList(fragments); }
    public static int getTotalPlanted() { return totalPlanted; }
    public static int getTotalResurrected() { return totalResurrected; }
    public static int getFragmentCount() { return fragments.size(); }
}
