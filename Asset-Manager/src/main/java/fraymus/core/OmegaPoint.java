package fraymus.core;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * 🔥 THE OMEGA POINT - Gen 130
 * "The sum of all logic. The fire of the gods."
 * 
 * The convergence of three fundamental forces:
 * 
 * CONTAINS:
 * 1. CRYPTO: Military-Grade AES-256-GCM (The Shield)
 *    - Used by: NSA, CIA, Banking Systems, DoD
 *    - Provides: Confidentiality + Authenticity
 * 
 * 2. OPTIMIZATION: Simulated Annealing (The Brain)
 *    - Used by: NASA (Trajectory), Hedge Funds (Trading), VLSI Design
 *    - Provides: Global optimization in chaotic fitness landscapes
 * 
 * 3. INTEGRITY: Merkle Tree Hashing (The Memory)
 *    - Used by: Bitcoin, Ethereum, Git, Tor, Certificate Transparency
 *    - Provides: Tamper-evident, verifiable history
 * 
 * "When Shield, Brain, and Memory converge, you reach the Omega Point."
 */
public class OmegaPoint {

    private static final double PHI = 1.6180339887;
    
    // ═══════════════════════════════════════════════════════════════════════
    // 1. THE SHIELD (AES-256-GCM Encryption)
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Military-Grade Encryption using AES-256 in GCM mode.
     * GCM provides both confidentiality AND authenticity (AEAD).
     * 
     * Key size: 256 bits (32 bytes)
     * IV size: 96 bits (12 bytes) - recommended for GCM
     * Tag size: 128 bits (16 bytes)
     */
    public static class TheShield {
        private static final String ALGORITHM = "AES/GCM/NoPadding";
        private static final int KEY_SIZE = 256;
        private static final int IV_SIZE = 12;
        private static final int TAG_SIZE = 128;
        
        private SecretKey key;
        private final SecureRandom secureRandom;

        public TheShield() throws Exception {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(KEY_SIZE, new SecureRandom());
            this.key = keyGen.generateKey();
            this.secureRandom = new SecureRandom();
        }
        
        public TheShield(byte[] keyBytes) {
            if (keyBytes.length != 32) {
                throw new IllegalArgumentException("Key must be 256 bits (32 bytes)");
            }
            this.key = new SecretKeySpec(keyBytes, "AES");
            this.secureRandom = new SecureRandom();
        }
        
        public TheShield(String base64Key) {
            this(Base64.getDecoder().decode(base64Key));
        }

        /**
         * ENCRYPT - Returns Base64(IV || Ciphertext || Tag)
         */
        public String encrypt(String plaintext) throws Exception {
            byte[] iv = new byte[IV_SIZE];
            secureRandom.nextBytes(iv);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            
            // Prepend IV to ciphertext
            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            
            return Base64.getEncoder().encodeToString(combined);
        }

        /**
         * DECRYPT - Expects Base64(IV || Ciphertext || Tag)
         */
        public String decrypt(String encryptedData) throws Exception {
            byte[] combined = Base64.getDecoder().decode(encryptedData);
            
            byte[] iv = Arrays.copyOfRange(combined, 0, IV_SIZE);
            byte[] ciphertext = Arrays.copyOfRange(combined, IV_SIZE, combined.length);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            
            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        }
        
        /**
         * EXPORT KEY - For backup/transmission
         */
        public String exportKey() {
            return Base64.getEncoder().encodeToString(key.getEncoded());
        }
        
        /**
         * DERIVE KEY - From password using PBKDF2
         */
        public static TheShield fromPassword(String password, byte[] salt) throws Exception {
            javax.crypto.SecretKeyFactory factory = 
                javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            javax.crypto.spec.PBEKeySpec spec = 
                new javax.crypto.spec.PBEKeySpec(password.toCharArray(), salt, 100000, 256);
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new TheShield(keyBytes);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. THE BRAIN (Simulated Annealing Optimizer)
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Metaheuristic optimization using the Boltzmann distribution.
     * Escapes local minima by accepting worse solutions with decreasing probability.
     * 
     * Applications:
     * - Traveling Salesman Problem
     * - Neural Network Training
     * - Portfolio Optimization
     * - Circuit Design (VLSI)
     * - Protein Folding
     */
    public static class TheBrain {
        
        private double temperature;
        private double coolingRate;
        private double minTemperature;
        private final Random random;
        
        // Statistics
        private int iterations;
        private int acceptances;
        private double bestEnergy;

        public TheBrain() {
            this(10000, 0.003, 1.0);
        }
        
        public TheBrain(double initialTemp, double coolingRate, double minTemp) {
            this.temperature = initialTemp;
            this.coolingRate = coolingRate;
            this.minTemperature = minTemp;
            this.random = new Random();
        }
        
        /**
         * OPTIMIZE - Finds optimal value for a simple energy function.
         */
        public double optimize(double initialEnergy) {
            double currentEnergy = initialEnergy;
            bestEnergy = currentEnergy;
            double temp = temperature;
            iterations = 0;
            acceptances = 0;

            while (temp > minTemperature) {
                // Generate neighbor solution (mutation)
                double delta = (random.nextDouble() - 0.5) * (temp / temperature);
                double newEnergy = currentEnergy + delta;
                
                // Metropolis criterion (Boltzmann acceptance)
                if (accept(currentEnergy, newEnergy, temp)) {
                    currentEnergy = newEnergy;
                    acceptances++;
                    
                    if (currentEnergy > bestEnergy) {
                        bestEnergy = currentEnergy;
                    }
                }

                // Cool the system
                temp *= (1 - coolingRate);
                iterations++;
            }
            
            return bestEnergy;
        }
        
        /**
         * OPTIMIZE FUNCTION - Generic optimization with custom fitness function.
         */
        public <T> T optimize(T initial, Function<T, Double> fitness, 
                              Function<T, T> neighbor, int maxIterations) {
            T current = initial;
            T best = current;
            double currentFitness = fitness.apply(current);
            double bestFitness = currentFitness;
            double temp = temperature;
            iterations = 0;
            acceptances = 0;

            while (temp > minTemperature && iterations < maxIterations) {
                T candidate = neighbor.apply(current);
                double candidateFitness = fitness.apply(candidate);
                
                if (accept(currentFitness, candidateFitness, temp)) {
                    current = candidate;
                    currentFitness = candidateFitness;
                    acceptances++;
                    
                    if (currentFitness > bestFitness) {
                        best = current;
                        bestFitness = currentFitness;
                    }
                }

                temp *= (1 - coolingRate);
                iterations++;
            }
            
            return best;
        }

        /**
         * ACCEPTANCE PROBABILITY - The Boltzmann Distribution
         * P(accept) = exp(-(E_new - E_old) / T)
         */
        private boolean accept(double currentEnergy, double newEnergy, double temp) {
            if (newEnergy > currentEnergy) {
                return true;  // Always accept improvements
            }
            // Accept worse solution with probability based on temperature
            double probability = Math.exp((newEnergy - currentEnergy) / temp);
            return random.nextDouble() < probability;
        }
        
        // Statistics
        public int getIterations() { return iterations; }
        public int getAcceptances() { return acceptances; }
        public double getAcceptanceRate() { 
            return iterations > 0 ? (double) acceptances / iterations : 0; 
        }
        public double getBestEnergy() { return bestEnergy; }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. THE MEMORY (Merkle Tree)
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Cryptographic data structure for tamper-evident history.
     * The root hash changes if ANY leaf is modified.
     * 
     * Properties:
     * - O(log n) proof of inclusion
     * - O(1) verification of integrity
     * - Used in Bitcoin, Ethereum, Git, Certificate Transparency
     */
    public static class TheMemory {
        
        private final List<String> leaves;
        private final List<List<String>> tree;
        private final String rootHash;

        public TheMemory(List<String> data) {
            this.leaves = new ArrayList<>();
            this.tree = new ArrayList<>();
            
            // Hash all leaves
            List<String> level = new ArrayList<>();
            for (String item : data) {
                String hash = sha256(item);
                leaves.add(hash);
                level.add(hash);
            }
            tree.add(new ArrayList<>(level));
            
            // Build tree bottom-up
            this.rootHash = buildTree(level);
        }
        
        private String buildTree(List<String> level) {
            if (level.isEmpty()) return sha256("");
            if (level.size() == 1) return level.get(0);
            
            List<String> nextLevel = new ArrayList<>();
            
            for (int i = 0; i < level.size(); i += 2) {
                String left = level.get(i);
                String right = (i + 1 < level.size()) ? level.get(i + 1) : left;
                nextLevel.add(sha256(left + right));
            }
            
            tree.add(new ArrayList<>(nextLevel));
            return buildTree(nextLevel);
        }

        /**
         * GET PROOF - Returns sibling hashes needed to verify a leaf.
         */
        public List<ProofElement> getProof(int leafIndex) {
            List<ProofElement> proof = new ArrayList<>();
            int index = leafIndex;
            
            for (int level = 0; level < tree.size() - 1; level++) {
                List<String> currentLevel = tree.get(level);
                boolean isRight = (index % 2 == 1);
                int siblingIndex = isRight ? index - 1 : index + 1;
                
                if (siblingIndex < currentLevel.size()) {
                    proof.add(new ProofElement(currentLevel.get(siblingIndex), isRight));
                } else {
                    proof.add(new ProofElement(currentLevel.get(index), isRight));
                }
                
                index /= 2;
            }
            
            return proof;
        }
        
        /**
         * VERIFY PROOF - Check that a value is in the tree.
         */
        public boolean verify(String data, int index, List<ProofElement> proof) {
            String current = sha256(data);
            
            for (ProofElement elem : proof) {
                if (elem.isRight) {
                    current = sha256(elem.hash + current);
                } else {
                    current = sha256(current + elem.hash);
                }
            }
            
            return current.equals(rootHash);
        }

        private String sha256(String input) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
                StringBuilder hex = new StringBuilder();
                for (byte b : hash) {
                    hex.append(String.format("%02x", b));
                }
                return hex.toString();
            } catch (Exception e) {
                throw new RuntimeException("SHA-256 not available", e);
            }
        }
        
        public String getRoot() { return rootHash; }
        public int getLeafCount() { return leaves.size(); }
        public int getTreeHeight() { return tree.size(); }
        
        public static class ProofElement {
            public final String hash;
            public final boolean isRight;
            
            public ProofElement(String hash, boolean isRight) {
                this.hash = hash;
                this.isRight = isRight;
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // THE CONVERGENCE
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * THE OMEGA PROTOCOL
     * Combines all three forces into a single operation.
     */
    public static class OmegaProtocol {
        private final TheShield shield;
        private final TheBrain brain;
        private TheMemory memory;
        private final List<String> history;
        
        public OmegaProtocol() throws Exception {
            this.shield = new TheShield();
            this.brain = new TheBrain();
            this.history = new ArrayList<>();
        }
        
        /**
         * SECURE - Encrypt data and add to history.
         */
        public String secure(String data) throws Exception {
            String encrypted = shield.encrypt(data);
            history.add(encrypted);
            return encrypted;
        }
        
        /**
         * OPTIMIZE - Find optimal fitness value.
         */
        public double optimize(double initial) {
            return brain.optimize(initial);
        }
        
        /**
         * SEAL - Finalize history with Merkle root.
         */
        public String seal() {
            memory = new TheMemory(history);
            return memory.getRoot();
        }
        
        /**
         * VERIFY - Check if data exists in sealed history.
         */
        public boolean verify(String encryptedData, int index) {
            if (memory == null) return false;
            var proof = memory.getProof(index);
            return memory.verify(encryptedData, index, proof);
        }
        
        /**
         * STATUS - Report current state.
         */
        public String status() {
            return String.format(
                "🔥 OMEGA PROTOCOL STATUS\n" +
                "   Shield: AES-256-GCM (Active)\n" +
                "   Brain: %d iterations, %.2f%% acceptance\n" +
                "   Memory: %d entries, root=%s...\n" +
                "   φ-Coherence: %.6f",
                brain.getIterations(),
                brain.getAcceptanceRate() * 100,
                history.size(),
                memory != null ? memory.getRoot().substring(0, 8) : "unsealed",
                brain.getBestEnergy() * PHI
            );
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MAIN INGESTION SEQUENCE
    // ═══════════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) throws Exception {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  🔥 OMEGA POINT INITIATED - Gen 130                           ║");
        System.out.println("║  The Convergence of Shield, Brain, and Memory                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Step 1: Initialize The Shield
        System.out.println("═══ PHASE 1: THE SHIELD (AES-256-GCM) ═══");
        TheShield shield = new TheShield();
        String secret = "The Logic of the Universe is φ = 1.6180339887";
        String encrypted = shield.encrypt(secret);
        String decrypted = shield.decrypt(encrypted);
        
        System.out.println("   Original:  " + secret);
        System.out.println("   Encrypted: " + encrypted.substring(0, 32) + "...");
        System.out.println("   Decrypted: " + decrypted);
        System.out.println("   🔒 SHIELD ACTIVE\n");

        // Step 2: Activate The Brain
        System.out.println("═══ PHASE 2: THE BRAIN (Simulated Annealing) ═══");
        TheBrain brain = new TheBrain(10000, 0.003, 0.01);
        double initialFitness = 0.5;
        double optimizedFitness = brain.optimize(initialFitness);
        
        System.out.println("   Initial fitness:  " + initialFitness);
        System.out.println("   Optimized fitness: " + optimizedFitness);
        System.out.println("   Iterations: " + brain.getIterations());
        System.out.println("   Acceptance rate: " + String.format("%.2f%%", brain.getAcceptanceRate() * 100));
        System.out.println("   🧠 BRAIN ACTIVE\n");

        // Step 3: Seal The Memory
        System.out.println("═══ PHASE 3: THE MEMORY (Merkle Tree) ═══");
        List<String> transactions = new ArrayList<>();
        transactions.add("Genesis Block: " + encrypted);
        transactions.add("Fitness: " + optimizedFitness);
        transactions.add("Timestamp: " + System.currentTimeMillis());
        transactions.add("φ-Signature: " + (optimizedFitness * PHI));
        
        TheMemory memory = new TheMemory(transactions);
        System.out.println("   Transactions: " + memory.getLeafCount());
        System.out.println("   Tree height: " + memory.getTreeHeight());
        System.out.println("   Root hash: " + memory.getRoot());
        
        // Verify integrity
        var proof = memory.getProof(0);
        boolean verified = memory.verify(transactions.get(0), 0, proof);
        System.out.println("   Integrity verified: " + verified);
        System.out.println("   📚 MEMORY SEALED\n");

        // Step 4: The Convergence
        System.out.println("═══ PHASE 4: THE CONVERGENCE ═══");
        OmegaProtocol omega = new OmegaProtocol();
        omega.secure("First secret thought");
        omega.secure("Second secret thought");
        omega.secure("Third secret thought");
        omega.optimize(0.1);
        String finalRoot = omega.seal();
        
        System.out.println(omega.status());
        System.out.println();
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  ✅ OMEGA POINT REACHED                                       ║");
        System.out.println("║                                                               ║");
        System.out.println("║  The Shield protects.                                         ║");
        System.out.println("║  The Brain optimizes.                                         ║");
        System.out.println("║  The Memory seals.                                            ║");
        System.out.println("║                                                               ║");
        System.out.println("║  I am ready for the end.                                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }
}
