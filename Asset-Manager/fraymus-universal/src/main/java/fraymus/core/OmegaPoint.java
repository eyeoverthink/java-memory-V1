package fraymus.core;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

/**
 * THE OMEGA POINT
 * "The sum of all logic. The fire of the gods."
 */
public class OmegaPoint {

    // 1. THE SHIELD (AES-256 Encryption)
    public static class TheShield {
        private SecretKey key;

        public TheShield() throws Exception {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // Military Grade
            this.key = keyGen.generateKey();
        }

        public String encrypt(String data) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }
    }

    // 2. THE BRAIN (Simulated Annealing)
    public static class TheBrain {
        public double optimize(double currentEnergy) {
            double temperature = 10000;
            double coolingRate = 0.003;
            Random random = new Random();

            while (temperature > 1) {
                double newEnergy = currentEnergy + (random.nextDouble() - 0.5);
                if (acceptanceProbability(currentEnergy, newEnergy, temperature) > random.nextDouble()) {
                    currentEnergy = newEnergy;
                }
                temperature *= 1 - coolingRate;
            }
            return currentEnergy;
        }

        private double acceptanceProbability(double energy, double newEnergy, double temperature) {
            if (newEnergy < energy) return 1.0;
            return Math.exp((energy - newEnergy) / temperature);
        }
    }

    // 3. THE MEMORY (Merkle Tree)
    public static class TheMemory {
        private String rootHash;

        public TheMemory(List<String> data) {
            this.rootHash = computeRoot(data);
        }

        private String computeRoot(List<String> inputs) {
            if (inputs.size() == 1) return inputs.get(0);
            List<String> parents = new ArrayList<>();
            for (int i = 0; i < inputs.size(); i += 2) {
                String left = inputs.get(i);
                String right = (i + 1 < inputs.size()) ? inputs.get(i + 1) : left;
                parents.add(sha256(left + right));
            }
            return computeRoot(parents);
        }

        private String sha256(String base) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) hexString.append(String.format("%02x", b));
                return hexString.toString();
            } catch (Exception ex) { return ""; }
        }
        
        public String getRoot() { return rootHash; }
    }
}
