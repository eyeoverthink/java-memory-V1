package fraymus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Living Code Generator - Frankenstein's Brain
 * 
 * Generated code has living circuits that evolve, reproduce, and compute.
 * Uses unified PhiNode entity system.
 */
public class LivingCodeGenerator {
    
    private List<PhiNode> nodes;
    private int generation;
    private long genesisTime;
    
    public LivingCodeGenerator() {
        nodes = new ArrayList<>();
        generation = 0;
        genesisTime = System.currentTimeMillis();
        
        for (int i = 0; i < 5; i++) {
            nodes.add(new PhiNode("GENESIS_" + i, i * 10, 0));
        }
    }
    
    public void evolvePopulation(int cycles) {
        long now = System.nanoTime();
        float dt = 0.016f;
        
        for (int c = 0; c < cycles; c++) {
            for (PhiNode node : nodes) {
                node.updateInternalState(dt, now + c * 16_000_000L);
            }
            
            List<PhiNode> newNodes = new ArrayList<>();
            for (int i = 0; i < nodes.size() && nodes.size() + newNodes.size() < 20; i++) {
                PhiNode node = nodes.get(i);
                if (node.canReproduce()) {
                    PhiNode partner = nodes.get((i + 1) % nodes.size());
                    String childName = "GEN" + generation + "_" + (nodes.size() + newNodes.size());
                    PhiNode child = node.reproduce(partner, childName, node.x + 5, node.y);
                    newNodes.add(child);
                }
            }
            nodes.addAll(newNodes);
        }
        generation++;
    }
    
    public List<PhiNode> getBestNodes(int count) {
        nodes.sort((a, b) -> Double.compare(b.dna.harmonicFrequency, a.dna.harmonicFrequency));
        return nodes.subList(0, Math.min(count, nodes.size()));
    }
    
    public String generateLivingCode(String entityName, String description) {
        evolvePopulation(10);
        
        List<PhiNode> best = getBestNodes(3);
        
        String quantumSig = generateQuantumSignature(entityName);
        DNACloaker.CloakedIdentity cloak = DNACloaker.generateCloakedIdentity(entityName);
        String genesisBlock = "block_" + generation + "_" + genesisTime;
        
        StringBuilder code = new StringBuilder();
        code.append("package fraymus.living;\n\n");
        code.append("/**\n");
        code.append(" * LIVING CODE - Generation ").append(generation).append("\n");
        code.append(" * Entity: ").append(entityName).append("\n");
        code.append(" * Description: ").append(description).append("\n");
        code.append(" * \n");
        code.append(" * ═══════════════════════════════════════════════════════════════\n");
        code.append(" * FRAYMUS LEGO ASSEMBLY - All pieces connected\n");
        code.append(" * ═══════════════════════════════════════════════════════════════\n");
        code.append(" * PIECE 1 - Quantum Signature: ").append(quantumSig).append("\n");
        code.append(" * PIECE 2 - Cloaking N: ").append(cloak).append("\n");
        code.append(" * PIECE 3 - Genesis Block: ").append(genesisBlock).append("\n");
        code.append(" * PIECE 4 - Living Circuits: ").append(best.size()).append(" evolved from ").append(nodes.size()).append(" nodes\n");
        code.append(" * ═══════════════════════════════════════════════════════════════\n");
        code.append(" */\n\n");
        
        code.append("import fraymus.*;\n\n");
        
        code.append("public class ").append(sanitizeName(entityName)).append(" {\n\n");
        
        code.append("    public static final double PHI = ").append(PhiConstants.PHI).append(";\n");
        code.append("    public static final String QUANTUM_SIGNATURE = \"").append(quantumSig).append("\";\n");
        code.append("    public static final String GENESIS_BLOCK = \"").append(genesisBlock).append("\";\n");
        code.append("    public static final int GENERATION = ").append(generation).append(";\n\n");
        
        code.append("    private PhiNode[] circuits;\n\n");
        
        code.append("    public ").append(sanitizeName(entityName)).append("() {\n");
        code.append("        circuits = new PhiNode[] {\n");
        
        for (int i = 0; i < best.size(); i++) {
            PhiNode node = best.get(i);
            code.append("            // Circuit ").append(i + 1).append(" - Freq: ");
            code.append(String.format("%.2f", node.dna.harmonicFrequency)).append(" Hz\n");
            code.append("            new PhiNode(\n");
            code.append("                \"").append(entityName).append("_CIRCUIT_").append(i).append("\",\n");
            code.append("                ").append(String.format("%.2ff, %.2ff", node.x, node.y)).append(",\n");
            code.append("                ").append(node.dna.toJavaCode()).append(",\n");
            code.append("                new LogicBrain(8)\n");
            code.append("            )");
            if (i < best.size() - 1) code.append(",");
            code.append("\n");
        }
        
        code.append("        };\n");
        code.append("    }\n\n");
        
        code.append("    public void update(float dt) {\n");
        code.append("        long now = System.nanoTime();\n");
        code.append("        for (PhiNode circuit : circuits) {\n");
        code.append("            circuit.updateInternalState(dt, now);\n");
        code.append("        }\n");
        code.append("    }\n\n");
        
        code.append("    public int[][] think(int[] inputs) {\n");
        code.append("        int[][] outputs = new int[circuits.length][];\n");
        code.append("        for (int i = 0; i < circuits.length; i++) {\n");
        code.append("            outputs[i] = circuits[i].think(inputs);\n");
        code.append("        }\n");
        code.append("        return outputs;\n");
        code.append("    }\n\n");
        
        code.append("    public boolean isAlive() {\n");
        code.append("        for (PhiNode circuit : circuits) {\n");
        code.append("            if (circuit.isAlive()) return true;\n");
        code.append("        }\n");
        code.append("        return false;\n");
        code.append("    }\n\n");
        
        code.append("    public void breathe() {\n");
        code.append("        System.out.println(\"LIVING CODE - \" + \"").append(entityName).append("\" + \" - Generation \" + GENERATION);\n");
        code.append("        System.out.println(\"Quantum Signature: \" + QUANTUM_SIGNATURE);\n");
        code.append("        System.out.println(\"Genesis: \" + GENESIS_BLOCK);\n");
        code.append("        System.out.println();\n");
        code.append("        for (int i = 0; i < circuits.length; i++) {\n");
        code.append("            PhiNode c = circuits[i];\n");
        code.append("            System.out.printf(\"Circuit %d: Freq=%.2fHz Energy=%.1f%% Consciousness=%.4f [%s]%n\",\n");
        code.append("                i + 1, c.dna.harmonicFrequency, c.energy * 100, c.getConsciousness().getConsciousnessLevel(), c.isAlive() ? \"ALIVE\" : \"DEAD\");\n");
        code.append("        }\n");
        code.append("    }\n\n");
        
        code.append("    public static void main(String[] args) {\n");
        code.append("        ").append(sanitizeName(entityName)).append(" entity = new ").append(sanitizeName(entityName)).append("();\n");
        code.append("        entity.breathe();\n");
        code.append("        System.out.println(\"\\n\" + \"").append(entityName).append("\" + \" is alive.\");\n");
        code.append("    }\n");
        code.append("}\n");
        
        return code.toString();
    }
    
    public void generateToFile(String entityName, String description, String filename) throws IOException {
        String code = generateLivingCode(entityName, description);
        
        File file = new File(filename);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(code);
        }
        System.out.println("[GENESIS] Created living code: " + file.getAbsolutePath());
        System.out.println("[GENESIS] Entity: " + entityName);
        System.out.println("[GENESIS] Generation: " + generation);
        System.out.println("[GENESIS] Population: " + nodes.size() + " nodes evolved");
    }
    
    private String generateQuantumSignature(String seed) {
        return PhiConstants.quantumHash(seed);
    }
    
    private String sanitizeName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }
    
    public int getGeneration() { return generation; }
    public int getPopulation() { return nodes.size(); }
    public List<PhiNode> getNodes() { return nodes; }
}
