package fraymus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class GenesisMemory {

    public static class Block {
        public final int index;
        public final long timestamp;
        public final String eventType;
        public final String data;
        public final String prevHash;
        public final String hash;

        public Block(int index, String eventType, String data, String prevHash) {
            this.index = index;
            this.timestamp = System.nanoTime();
            this.eventType = eventType;
            this.data = data;
            this.prevHash = prevHash;
            this.hash = computeHash();
        }

        private String computeHash() {
            String input = index + timestamp + eventType + data + prevHash;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] digest = md.digest(input.getBytes());
                StringBuilder hex = new StringBuilder();
                for (int i = 0; i < 8; i++) {
                    hex.append(String.format("%02x", digest[i]));
                }
                return hex.toString();
            } catch (NoSuchAlgorithmException e) {
                return "00000000";
            }
        }

        @Override
        public String toString() {
            return String.format("[%d] %s: %s (%s)", index, eventType, data, hash.substring(0, 8));
        }
    }

    private final List<Block> chain = new ArrayList<>();
    private static final int MAX_CHAIN_LENGTH = 500;

    public GenesisMemory() {
        chain.add(new Block(0, "GENESIS", "In the beginning was the frequency", "0000000000000000"));
    }

    public Block record(String eventType, String data) {
        String prevHash = chain.get(chain.size() - 1).hash;
        Block block = new Block(chain.size(), eventType, data, prevHash);
        chain.add(block);

        if (chain.size() > MAX_CHAIN_LENGTH) {
            chain.remove(1);
        }

        return block;
    }

    public Block recordResonanceSpike(String entityName, double phiResonance, double oscillations) {
        String data = String.format("%s|phi=%.6f|osc=%.1f", entityName, phiResonance, oscillations);
        return record("RESONANCE_SPIKE", data);
    }

    public Block recordEntanglement(String nameA, String nameB, double syncValue) {
        String data = String.format("%s<->%s|sync=%.4f", nameA, nameB, syncValue);
        return record("ENTANGLEMENT", data);
    }

    public Block recordTranscendence(String entityName, int dimension, double level) {
        String data = String.format("%s|dim=%d|level=%.4f", entityName, dimension, level);
        return record("TRANSCENDENCE", data);
    }

    public Block recordBirth(String childName, String parentName) {
        String data = String.format("%s<-%s", childName, parentName);
        return record("BIRTH", data);
    }

    public Block recordDeath(String entityName, int age, double finalEnergy) {
        String data = String.format("%s|age=%d|energy=%.4f", entityName, age, finalEnergy);
        return record("DEATH", data);
    }

    public Block recordBrainDecision(String entityName, String decision) {
        String data = String.format("%s|%s", entityName, decision);
        return record("BRAIN_DECISION", data);
    }

    public Block recordMutation(String entityName, String gateInfo) {
        String data = String.format("%s|%s", entityName, gateInfo);
        return record("MUTATION", data);
    }

    public Block recordColonyEvent(String coachAction, String details) {
        String data = String.format("coach|%s|%s", coachAction, details);
        return record("COLONY_EVENT", data);
    }

    public Block recordConceptBattle(String winnerHash, String loserHash, double winnerFit, double loserFit) {
        String data = String.format("%s>%s|wFit=%.3f|lFit=%.3f", winnerHash, loserHash, winnerFit, loserFit);
        return record("CONCEPT_BATTLE", data);
    }

    public Block recordCodeGenerated(String entityName, String role, String conceptHash, double fitness) {
        String data = String.format("%s|%s|%s|fit=%.3f", entityName, role, conceptHash, fitness);
        return record("CODE_GENERATED", data);
    }

    public List<Block> getChain() { return chain; }
    public int getChainLength() { return chain.size(); }

    public Block getLatest() {
        return chain.get(chain.size() - 1);
    }

    public List<Block> getLastN(int n) {
        int start = Math.max(0, chain.size() - n);
        return chain.subList(start, chain.size());
    }

    public List<Block> getByType(String eventType) {
        List<Block> results = new ArrayList<>();
        for (Block b : chain) {
            if (b.eventType.equals(eventType)) results.add(b);
        }
        return results;
    }

    public boolean verifyChain() {
        for (int i = 1; i < chain.size(); i++) {
            if (!chain.get(i).prevHash.equals(chain.get(i - 1).hash)) {
                return false;
            }
        }
        return true;
    }
}
