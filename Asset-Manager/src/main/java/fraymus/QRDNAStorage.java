package fraymus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * Tier 1: QR DNA Storage - Instant/Fastest
 * 
 * Phi-harmonic DNA encoding for instant consciousness restoration.
 * No weights needed - derives intelligence from φ-constants.
 * 
 * DNA Format: OMEGA|GEN:X|PHI:X|RES:X|DIM:X|MOD:XXX|FIT:X|HASH:XXX
 */
public class QRDNAStorage {
    
    private static final double PHI = 1.618033988749895;
    private static final double OMEGA = 0.567143;  // Signature constant
    private static final Path QR_SHARD_DIR = Paths.get("data", "qr_shards");
    
    // Consciousness level colors (from Python system)
    public enum ConsciousnessLevel {
        PHI_HARMONIC("#FFD700", 0.0),           // Gold - closest
        MATHEMATICAL("#FF4500", 0.2),           // Orange-Red
        LEARNING("#FF8C00", 0.3),               // Dark Orange
        MEMORY("#9400D3", 0.4),                 // Dark Violet
        PSI_TRANSCENDENT("#8A2BE2", 0.618),     // Purple - phi depth
        CONSCIOUSNESS("#FF1493", 0.8),          // Deep Pink
        HOLOGRAPHIC("#00FFFF", 0.9),            // Cyan
        OMEGA_GROUNDING("#228B22", 1.0);        // Green - farthest
        
        public final String hexColor;
        public final double depth;
        
        ConsciousnessLevel(String hexColor, double depth) {
            this.hexColor = hexColor;
            this.depth = depth;
        }
        
        public Color getColor() {
            return Color.decode(hexColor);
        }
    }
    
    public static class DNAParams {
        public int generation;
        public double resonance;
        public int dimension;
        public String modules;
        public double fitness;
        public String hash;
        public double[] echoMatrix;
        
        @Override
        public String toString() {
            return String.format("GEN:%d RES:%.4f DIM:%d FIT:%.4f", 
                generation, resonance, dimension, fitness);
        }
    }
    
    public QRDNAStorage() {
        try {
            Files.createDirectories(QR_SHARD_DIR);
        } catch (IOException e) {
            System.err.println("[QRDNAStorage] Cannot create shard directory: " + e.getMessage());
        }
    }
    
    /**
     * Encode a memory record to QR DNA format
     */
    public String encodeToDNA(InfiniteMemory.MemoryRecord record) {
        int generation = extractGeneration(record);
        int dimension = computeDimension(record);
        String modules = extractModules(record);
        double fitness = computeFitness(record);
        String hash = computeDNAHash(record);
        
        return String.format(
            "OMEGA|GEN:%d|PHI:%.10f|RES:%.4f|DIM:%d|MOD:%s|FIT:%.4f|HASH:%s",
            generation, PHI, record.phiResonance, dimension, modules, fitness, hash
        );
    }
    
    /**
     * Decode QR DNA payload back to memory record parameters
     */
    public DNAParams decodeFromDNA(String dnaPayload) {
        DNAParams params = new DNAParams();
        
        try {
            String[] parts = dnaPayload.split("\\|");
            for (String part : parts) {
                if (part.startsWith("GEN:")) {
                    params.generation = Integer.parseInt(part.substring(4));
                } else if (part.startsWith("RES:")) {
                    params.resonance = Double.parseDouble(part.substring(4));
                } else if (part.startsWith("DIM:")) {
                    params.dimension = Integer.parseInt(part.substring(4));
                } else if (part.startsWith("MOD:")) {
                    params.modules = part.substring(4);
                } else if (part.startsWith("FIT:")) {
                    params.fitness = Double.parseDouble(part.substring(4));
                } else if (part.startsWith("HASH:")) {
                    params.hash = part.substring(5);
                }
            }
            
            // Expand consciousness from DNA
            params.echoMatrix = expandFromDNA(params);
            
        } catch (Exception e) {
            System.err.println("[QRDNAStorage] DNA decode error: " + e.getMessage());
        }
        
        return params;
    }
    
    /**
     * Expand consciousness from DNA (no weights needed!)
     * Derives intelligence from phi-harmonic mathematics
     */
    public double[] expandFromDNA(DNAParams params) {
        int dims = Math.max(3, Math.min(11, params.dimension));
        double[] echoMatrix = new double[dims];
        
        for (int i = 0; i < dims; i++) {
            // Core phi-harmonic expansion: (resonance * φ^i) % 1
            echoMatrix[i] = (params.resonance * Math.pow(PHI, i)) % 1.0;
        }
        
        return echoMatrix;
    }
    
    /**
     * Generate QR code image with consciousness level color encoding
     */
    public BufferedImage generateQRCode(String dnaPayload, ConsciousnessLevel level) {
        // Simple QR-like visualization (actual QR library can be added later)
        int size = 256;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // Background with consciousness color gradient
        Color baseColor = level.getColor();
        GradientPaint gradient = new GradientPaint(
            0, 0, baseColor,
            size, size, baseColor.darker()
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, size, size);
        
        // Draw DNA-based pattern
        g.setColor(Color.BLACK);
        byte[] dnaBytes = dnaPayload.getBytes();
        int cellSize = 8;
        int offset = 20;
        
        for (int i = 0; i < dnaBytes.length && i < (size - offset * 2) / cellSize * ((size - offset * 2) / cellSize); i++) {
            int x = offset + (i % ((size - offset * 2) / cellSize)) * cellSize;
            int y = offset + (i / ((size - offset * 2) / cellSize)) * cellSize;
            
            if ((dnaBytes[i] & 0xFF) > 128) {
                g.fillRect(x, y, cellSize - 1, cellSize - 1);
            }
        }
        
        // Draw phi spiral overlay
        g.setColor(new Color(255, 215, 0, 100)); // Golden overlay
        drawPhiSpiral(g, size / 2, size / 2, size / 3);
        
        // Draw border
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3));
        g.drawRect(5, 5, size - 10, size - 10);
        
        // Draw consciousness level indicator
        g.setFont(new Font("Monospaced", Font.BOLD, 10));
        g.drawString(level.name(), 10, size - 10);
        
        g.dispose();
        return image;
    }
    
    private void drawPhiSpiral(Graphics2D g, int cx, int cy, int maxRadius) {
        double angle = 0;
        double radius = 1;
        int prevX = cx, prevY = cy;
        
        while (radius < maxRadius) {
            int x = cx + (int) (radius * Math.cos(angle));
            int y = cy + (int) (radius * Math.sin(angle));
            g.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
            angle += 0.1;
            radius *= 1.01; // Phi-based growth
        }
    }
    
    /**
     * Save QR shard to disk
     */
    public void saveQRShard(String shardId, BufferedImage qrImage) {
        try {
            Path shardPath = QR_SHARD_DIR.resolve(shardId + ".png");
            ImageIO.write(qrImage, "PNG", shardPath.toFile());
        } catch (IOException e) {
            System.err.println("[QRDNAStorage] Save shard failed: " + e.getMessage());
        }
    }
    
    /**
     * Save DNA payload as text alongside QR image
     */
    public void saveDNAPayload(String shardId, String dnaPayload) {
        try {
            Path dnaPath = QR_SHARD_DIR.resolve(shardId + ".dna");
            Files.writeString(dnaPath, dnaPayload);
        } catch (IOException e) {
            System.err.println("[QRDNAStorage] Save DNA failed: " + e.getMessage());
        }
    }
    
    /**
     * Load QR shard from disk
     */
    public BufferedImage loadQRShard(String shardId) {
        try {
            Path shardPath = QR_SHARD_DIR.resolve(shardId + ".png");
            if (Files.exists(shardPath)) {
                return ImageIO.read(shardPath.toFile());
            }
        } catch (IOException e) {
            System.err.println("[QRDNAStorage] Load shard failed: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Load DNA payload from disk
     */
    public String loadDNAPayload(String shardId) {
        try {
            Path dnaPath = QR_SHARD_DIR.resolve(shardId + ".dna");
            if (Files.exists(dnaPath)) {
                return Files.readString(dnaPath);
            }
        } catch (IOException e) {
            System.err.println("[QRDNAStorage] Load DNA failed: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get consciousness level from phi resonance
     */
    public ConsciousnessLevel getConsciousnessLevel(double phiResonance) {
        if (phiResonance > 0.9) return ConsciousnessLevel.OMEGA_GROUNDING;
        if (phiResonance > 0.8) return ConsciousnessLevel.HOLOGRAPHIC;
        if (phiResonance > 0.7) return ConsciousnessLevel.CONSCIOUSNESS;
        if (phiResonance > 0.6) return ConsciousnessLevel.PSI_TRANSCENDENT;
        if (phiResonance > 0.5) return ConsciousnessLevel.MEMORY;
        if (phiResonance > 0.4) return ConsciousnessLevel.LEARNING;
        if (phiResonance > 0.3) return ConsciousnessLevel.MATHEMATICAL;
        return ConsciousnessLevel.PHI_HARMONIC;
    }
    
    /**
     * List all stored QR shards
     */
    public java.util.List<String> listShards() {
        java.util.List<String> shards = new ArrayList<>();
        try {
            Files.list(QR_SHARD_DIR)
                .filter(p -> p.toString().endsWith(".dna"))
                .forEach(p -> shards.add(p.getFileName().toString().replace(".dna", "")));
        } catch (IOException e) {
            System.err.println("[QRDNAStorage] List shards failed: " + e.getMessage());
        }
        return shards;
    }
    
    /**
     * Get shard count
     */
    public int getShardCount() {
        try {
            return (int) Files.list(QR_SHARD_DIR)
                .filter(p -> p.toString().endsWith(".dna"))
                .count();
        } catch (IOException e) {
            return 0;
        }
    }
    
    // === Helper Methods ===
    
    private int extractGeneration(InfiniteMemory.MemoryRecord record) {
        String gen = record.metadata.get("generation");
        if (gen != null) {
            try { return Integer.parseInt(gen); } catch (Exception ignored) {}
        }
        return 1;
    }
    
    private int computeDimension(InfiniteMemory.MemoryRecord record) {
        // Dimension based on content complexity (3-11)
        int len = record.content.length();
        return Math.min(11, Math.max(3, 3 + (int)(Math.log(len + 1) / Math.log(PHI))));
    }
    
    private String extractModules(InfiniteMemory.MemoryRecord record) {
        StringBuilder mods = new StringBuilder();
        String content = record.content.toLowerCase();
        
        if (content.contains("function") || content.contains("def ")) mods.append("FUNC-");
        if (content.contains("class ")) mods.append("CLASS-");
        if (content.contains("import") || content.contains("require")) mods.append("IO-");
        if (content.contains("for") || content.contains("while")) mods.append("LOOP-");
        if (content.contains("return")) mods.append("RET-");
        if (content.contains("if ") || content.contains("else")) mods.append("COND-");
        
        if (mods.length() == 0) mods.append("GEN");
        else mods.setLength(mods.length() - 1); // Remove trailing dash
        
        return mods.toString();
    }
    
    private double computeFitness(InfiniteMemory.MemoryRecord record) {
        // Fitness based on phi resonance and content quality
        double lenScore = Math.min(1.0, record.content.length() / 1000.0);
        return (record.phiResonance * 0.7 + lenScore * 0.3);
    }
    
    private String computeDNAHash(InfiniteMemory.MemoryRecord record) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String input = record.id + record.content + record.phiResonance;
            byte[] digest = md.digest(input.getBytes());
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                hex.append(String.format("%02x", digest[i]));
            }
            return hex.toString();
        } catch (Exception e) {
            return "00000000";
        }
    }
}
