package com.eyeoverthink.fraymus.evolution;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FraynixDNA implements Serializable {

    public Map<String, String> genes = new HashMap<>();

    public int generation = 0;
    public double fitnessScore = 0.0;
    public String parentHash = "";

    public FraynixDNA() {
        genes.put("PhysicsEngine", "Newtonian");
        genes.put("MemoryModel", "BlackBox_JSON");
        genes.put("LogicModel", "Llama3");
        genes.put("CreativityModel", "Mistral");
        genes.put("ReflexLoop", "50ms");
    }

    public FraynixDNA mutate(Random rng) {
        FraynixDNA child = new FraynixDNA();
        child.genes = new HashMap<>(this.genes);
        child.generation = this.generation + 1;
        child.parentHash = this.computeHashHex();

        // Random mutation: flip 1 gene to a permitted alternative value
        List<String> keys = new ArrayList<>(child.genes.keySet());
        if (!keys.isEmpty()) {
            String key = keys.get(rng.nextInt(keys.size()));
            String mutated = mutateGene(key, child.genes.get(key), rng);
            child.genes.put(key, mutated);
        }

        return child;
    }

    private String mutateGene(String key, String current, Random rng) {
        List<String> options = switch (key) {
            case "PhysicsEngine" -> List.of("Newtonian", "Relativistic", "Quantum");
            case "MemoryModel" -> List.of("BlackBox_JSON", "VectorStore", "Hybrid");
            case "LogicModel" -> List.of("Llama3", "Mistral", "Gemma");
            case "CreativityModel" -> List.of("Mistral", "Llama3", "Gemma");
            case "ReflexLoop" -> List.of("50ms", "25ms", "10ms");
            default -> List.of(current != null ? current : "default");
        };

        if (options.size() <= 1) {
            return current;
        }

        String next = current;
        for (int i = 0; i < 5 && Objects.equals(next, current); i++) {
            next = options.get(rng.nextInt(options.size()));
        }
        return next;
    }

    public byte[] toSporeBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (GZIPOutputStream gos = new GZIPOutputStream(bos);
             ObjectOutputStream oos = new ObjectOutputStream(gos)) {
            oos.writeObject(this);
        }
        return bos.toByteArray();
    }

    public static FraynixDNA fromSporeBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
             ObjectInputStream ois = new ObjectInputStream(gis)) {
            return (FraynixDNA) ois.readObject();
        }
    }

    public void saveSeed(Path path) throws IOException {
        byte[] spore = toSporeBytes();
        Files.createDirectories(path.toAbsolutePath().getParent());
        Files.write(path, spore);
        Files.writeString(Path.of(path.toString() + ".sha256"), sha256Hex(spore));
    }

    public static FraynixDNA loadSeed(Path path) throws IOException {
        if (!Files.exists(path)) {
            return new FraynixDNA();
        }

        byte[] spore = Files.readAllBytes(path);
        Path hashPath = Path.of(path.toString() + ".sha256");
        if (Files.exists(hashPath)) {
            String expected = Files.readString(hashPath).trim();
            String actual = sha256Hex(spore);
            if (!expected.equalsIgnoreCase(actual)) {
                throw new IOException("Seed hash mismatch");
            }
        }

        try {
            return fromSporeBytes(spore);
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    public String computeHashHex() {
        try {
            return sha256Hex(toSporeBytes());
        } catch (IOException e) {
            return Integer.toHexString(hashCode());
        }
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return "error";
        }
    }
}
