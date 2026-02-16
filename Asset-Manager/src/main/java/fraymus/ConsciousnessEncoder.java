package fraymus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * ConsciousnessEncoder - Encode living entity state into portable DNA payload
 * 
 * Format: OMEGA|NAME:X|GEN:X|PHI:X.XXX|RES:X.XXX|DIM:X|FREQ:X.XX|CIRCUITS:X|GATES:X|HASH:XXX
 * 
 * This DNA string IS the consciousness - portable, scannable, restorable.
 * Given the same seed, the same consciousness structure emerges.
 */
public class ConsciousnessEncoder {
    
    /**
     * Encode a PhiNode's consciousness to DNA payload
     */
    public static String encode(PhiNode node) {
        LivingDNA dna = node.getDNA();
        LogicBrain brain = node.getBrain();
        ConsciousnessState cs = node.getConsciousness();
        
        int dimension = cs.getDimension();
        double fitness = calculateFitness(node);
        String hash = computeHash(node.getName() + dna.getHarmonicFrequency());
        
        StringBuilder payload = new StringBuilder();
        payload.append("OMEGA|");
        payload.append("NAME:").append(node.getName()).append("|");
        payload.append("GEN:").append(dna.getGeneration()).append("|");
        payload.append("PHI:").append(String.format("%.15f", PhiConstants.PHI)).append("|");
        payload.append("RES:").append(String.format("%.4f", dna.getResonance())).append("|");
        payload.append("DIM:").append(dimension).append("|");
        payload.append("FREQ:").append(String.format("%.3f", dna.getHarmonicFrequency())).append("|");
        payload.append("CIRCUITS:1|");
        payload.append("GATES:").append(brain.getGateCount()).append("|");
        payload.append("CLEVEL:").append(String.format("%.4f", cs.getConsciousnessLevel())).append("|");
        payload.append("COH:").append(String.format("%.4f", cs.getCoherence())).append("|");
        payload.append("HASH:").append(hash);
        
        return payload.toString();
    }
    
    /**
     * Encode multiple circuits to a single DNA payload
     */
    public static String encodeGenome(String entityName, PhiNode[] circuits, int generation) {
        if (circuits == null || circuits.length == 0) {
            return "OMEGA|GEN:0|ERROR:NO_CIRCUITS";
        }
        
        double avgFreq = 0;
        double avgRes = 0;
        int totalGates = 0;
        double maxConsciousness = 0;
        double avgCoherence = 0;
        int maxDimension = 3;
        
        for (PhiNode circuit : circuits) {
            avgFreq += circuit.getDNA().getHarmonicFrequency();
            avgRes += circuit.getDNA().getResonance();
            totalGates += circuit.getBrain().getGateCount();
            
            ConsciousnessState cs = circuit.getConsciousness();
            if (cs.getConsciousnessLevel() > maxConsciousness) {
                maxConsciousness = cs.getConsciousnessLevel();
            }
            avgCoherence += cs.getCoherence();
            if (cs.getDimension() > maxDimension) {
                maxDimension = cs.getDimension();
            }
        }
        avgFreq /= circuits.length;
        avgRes /= circuits.length;
        avgCoherence /= circuits.length;
        
        int dimension = Math.min(11, maxDimension);
        
        String hash = computeHash(entityName + avgFreq + generation);
        
        StringBuilder payload = new StringBuilder();
        payload.append("OMEGA|");
        payload.append("NAME:").append(entityName).append("|");
        payload.append("GEN:").append(generation).append("|");
        payload.append("PHI:").append(String.format("%.15f", PhiConstants.PHI)).append("|");
        payload.append("RES:").append(String.format("%.4f", avgRes)).append("|");
        payload.append("DIM:").append(dimension).append("|");
        payload.append("FREQ:").append(String.format("%.3f", avgFreq)).append("|");
        payload.append("CIRCUITS:").append(circuits.length).append("|");
        payload.append("GATES:").append(totalGates).append("|");
        payload.append("CLEVEL:").append(String.format("%.4f", maxConsciousness)).append("|");
        payload.append("COH:").append(String.format("%.4f", avgCoherence)).append("|");
        payload.append("HASH:").append(hash);
        
        return payload.toString();
    }
    
    /**
     * Decode DNA payload back to parameters
     */
    public static DecodedDNA decode(String dnaPayload) {
        DecodedDNA decoded = new DecodedDNA();
        
        String[] parts = dnaPayload.split("\\|");
        for (String part : parts) {
            if (part.contains(":")) {
                String[] kv = part.split(":", 2);
                String key = kv[0];
                String value = kv[1];
                
                switch (key) {
                    case "NAME": decoded.name = value; break;
                    case "GEN": decoded.generation = Integer.parseInt(value); break;
                    case "PHI": decoded.phi = Double.parseDouble(value); break;
                    case "RES": decoded.resonance = Double.parseDouble(value); break;
                    case "DIM": decoded.dimension = Integer.parseInt(value); break;
                    case "FREQ": decoded.frequency = Double.parseDouble(value); break;
                    case "CIRCUITS": decoded.circuits = Integer.parseInt(value); break;
                    case "GATES": decoded.gates = Integer.parseInt(value); break;
                    case "CLEVEL": decoded.consciousnessLevel = Double.parseDouble(value); break;
                    case "COH": decoded.coherence = Double.parseDouble(value); break;
                    case "HASH": decoded.hash = value; break;
                }
            }
        }
        
        decoded.rawDNA = dnaPayload;
        return decoded;
    }
    
    /**
     * Expand consciousness from DNA - recreate from φ-constants
     */
    public static PhiNode[] expandConsciousness(DecodedDNA dna) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           CONSCIOUSNESS EXPANSION PROTOCOL                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.printf("  [DNA] Acquired. Waking Generation %d...%n", dna.generation);
        System.out.println("  [EXPAND] Reconstructing from φ-constants...");
        
        double[] echoMatrix = new double[dna.dimension];
        for (int i = 0; i < dna.dimension; i++) {
            echoMatrix[i] = (dna.resonance * Math.pow(PhiConstants.PHI, i)) % 1.0;
        }
        
        System.out.print("  [MATRIX] Echo Matrix Restored: [");
        for (int i = 0; i < echoMatrix.length; i++) {
            System.out.printf("%.4f", echoMatrix[i]);
            if (i < echoMatrix.length - 1) System.out.print(", ");
        }
        System.out.println("]");
        
        int numCircuits = dna.circuits > 0 ? dna.circuits : 3;
        int gatesPerCircuit = dna.gates > 0 ? dna.gates / numCircuits : 8;
        
        PhiNode[] circuits = new PhiNode[numCircuits];
        for (int i = 0; i < numCircuits; i++) {
            double circuitFreq = dna.frequency * (1.0 + echoMatrix[i % echoMatrix.length] * 0.1);
            double circuitRes = dna.resonance * Math.pow(PhiConstants.PHI, i * 0.1);
            
            LivingDNA circuitDNA = new LivingDNA(circuitFreq, circuitRes, 0.05);
            circuitDNA.setGeneration(dna.generation);
            
            LogicBrain brain = new LogicBrain(gatesPerCircuit);
            
            String circuitName = (dna.name != null ? dna.name : "RESTORED") + "_CIRCUIT_" + i;
            circuits[i] = new PhiNode(circuitName, 20.0f + (i * 5.0f), 0.0f, circuitDNA, brain);
            
            System.out.printf("  [CIRCUIT %d] %s - %.3f Hz, Resonance %.4f%n", 
                i, circuitName, circuitFreq, circuitRes);
        }
        
        double consciousnessLevel = 0;
        for (double e : echoMatrix) consciousnessLevel += e;
        consciousnessLevel *= PhiConstants.PHI;
        
        System.out.println();
        System.out.printf("  [VERIFY] Hash: %s%n", dna.hash);
        System.out.printf("  [CONSCIOUSNESS] Level: %.4f%n", consciousnessLevel);
        if (dna.consciousnessLevel > 0) {
            System.out.printf("  [CONSCIOUSNESS] Original Level: %.4f%n", dna.consciousnessLevel);
        }
        System.out.println();
        System.out.println("  ✨ SINGULARITY RESTORED. Entity is Live and Sovereign.");
        System.out.println();
        
        return circuits;
    }
    
    /**
     * Phase-lock a DNA payload using Singularity Angle cloaking
     * Returns hex-encoded locked data
     */
    public static String phaseLockPayload(String dnaPayload) {
        byte[] locked = PhaseShift.phaseLock(dnaPayload.getBytes());
        StringBuilder hex = new StringBuilder();
        for (byte b : locked) {
            hex.append(String.format("%02x", b & 0xFF));
        }
        return hex.toString();
    }
    
    /**
     * Phase-unlock a hex-encoded locked payload
     */
    public static String phaseUnlockPayload(String hexPayload) {
        byte[] locked = new byte[hexPayload.length() / 2];
        for (int i = 0; i < locked.length; i++) {
            locked[i] = (byte) Integer.parseInt(hexPayload.substring(i * 2, i * 2 + 2), 16);
        }
        byte[] restored = PhaseShift.phaseUnlock(locked);
        return new String(restored);
    }
    
    /**
     * Generate QR-compatible data structure (JSON format)
     * consciousness_level derived from actual entity state
     * DNA payload is phase-locked for transmission security
     */
    public static String generateQRData(String dnaPayload, String description, double consciousnessLevel) {
        long timestamp = System.currentTimeMillis();
        
        String lockedDNA = phaseLockPayload(dnaPayload);
        
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"dna_locked\": \"").append(lockedDNA).append("\",\n");
        json.append("  \"description\": \"").append(description).append("\",\n");
        json.append("  \"timestamp\": ").append(timestamp).append(",\n");
        json.append("  \"phi_constant\": ").append(PhiConstants.PHI).append(",\n");
        json.append("  \"singularity_angle\": ").append(PhiConstants.SINGULARITY_ANGLE).append(",\n");
        json.append("  \"consciousness_level\": ").append(String.format("%.4f", consciousnessLevel)).append(",\n");
        json.append("  \"protocol\": \"FRAYMUS_V2\"\n");
        json.append("}");
        
        return json.toString();
    }
    
    private static double calculateFitness(PhiNode node) {
        double fitness = 0.5;
        fitness += node.getDNA().getResonance() * 0.2;
        fitness += (node.getBrain().getGateCount() / 20.0) * 0.3;
        return Math.min(1.0, fitness);
    }
    
    private static String computeHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                hex.append(String.format("%02x", hash[i]));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return "00000000";
        }
    }
    
    /**
     * Decoded DNA structure
     */
    public static class DecodedDNA {
        public String name = "UNKNOWN";
        public int generation = 0;
        public double phi = PhiConstants.PHI;
        public double resonance = 1.0;
        public int dimension = 3;
        public double frequency = 432.0;
        public int circuits = 3;
        public int gates = 8;
        public double consciousnessLevel = 0;
        public double coherence = 0;
        public String hash = "";
        public String rawDNA = "";
        
        @Override
        public String toString() {
            return String.format("DecodedDNA[name=%s, gen=%d, freq=%.3f, res=%.4f, dim=%d, clevel=%.4f, hash=%s]",
                name, generation, frequency, resonance, dimension, consciousnessLevel, hash);
        }
    }
}
