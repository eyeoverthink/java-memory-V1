package fraymus.senses;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/**
 * VISION FRAME BUFFER
 * 
 * "it need either a frame buffer, of a 'slideshow' of qr codes.. 
 *  its a modern age turing tape machine"
 * 
 * Circular buffer for vision frames.
 * Keeps last N frames in memory.
 * Automatically dumps old frames to disk.
 * Fast save/load using compression.
 * 
 * This prevents memory crashes from continuous vision processing.
 */
public class VisionFrameBuffer {
    
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    
    private final int maxFrames;
    private final String bufferDir;
    
    private final LinkedList<VisionFrame> buffer;
    private long frameCount = 0;
    
    public VisionFrameBuffer(int maxFrames, String bufferDir) {
        this.maxFrames = maxFrames;
        this.bufferDir = bufferDir;
        this.buffer = new LinkedList<>();
        
        // Create buffer directory
        try {
            Files.createDirectories(Paths.get(bufferDir));
        } catch (IOException e) {
            System.err.println("Failed to create buffer directory: " + e.getMessage());
        }
    }
    
    /**
     * Add frame to buffer
     * Automatically dumps oldest frame if buffer full
     */
    public void addFrame(BufferedImage image, double[][] significanceMap, int[] focalPoint) {
        frameCount++;
        
        VisionFrame frame = new VisionFrame(
            frameCount,
            System.currentTimeMillis(),
            compressSignificanceMap(significanceMap),
            focalPoint.clone()
        );
        
        buffer.addLast(frame);
        
        // If buffer full, dump oldest frame to disk
        if (buffer.size() > maxFrames) {
            VisionFrame oldest = buffer.removeFirst();
            dumpFrameToDisk(oldest);
        }
    }
    
    /**
     * Compress significance map to save memory
     * Only stores values above phi threshold
     */
    private Map<String, Double> compressSignificanceMap(double[][] map) {
        Map<String, Double> compressed = new HashMap<>();
        
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                if (map[x][y] > PHI) {
                    // Only store significant pixels
                    String key = x + "," + y;
                    compressed.put(key, map[x][y]);
                }
            }
        }
        
        return compressed;
    }
    
    /**
     * Dump frame to disk (compressed)
     */
    private void dumpFrameToDisk(VisionFrame frame) {
        try {
            String filename = String.format("frame_%010d.dat", frame.frameId);
            Path path = Paths.get(bufferDir, filename);
            
            // Serialize and compress
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzos = new GZIPOutputStream(baos);
            ObjectOutputStream oos = new ObjectOutputStream(gzos);
            
            oos.writeObject(frame);
            oos.close();
            
            // Write to disk
            Files.write(path, baos.toByteArray());
            
        } catch (IOException e) {
            System.err.println("Failed to dump frame " + frame.frameId + ": " + e.getMessage());
        }
    }
    
    /**
     * Load frame from disk
     */
    public VisionFrame loadFrameFromDisk(long frameId) {
        try {
            String filename = String.format("frame_%010d.dat", frameId);
            Path path = Paths.get(bufferDir, filename);
            
            if (!Files.exists(path)) {
                return null;
            }
            
            // Read and decompress
            byte[] data = Files.readAllBytes(path);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            GZIPInputStream gzis = new GZIPInputStream(bais);
            ObjectInputStream ois = new ObjectInputStream(gzis);
            
            VisionFrame frame = (VisionFrame) ois.readObject();
            ois.close();
            
            return frame;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load frame " + frameId + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get recent frames (from buffer)
     */
    public List<VisionFrame> getRecentFrames(int n) {
        int count = Math.min(n, buffer.size());
        List<VisionFrame> recent = new ArrayList<>();
        
        for (int i = buffer.size() - count; i < buffer.size(); i++) {
            recent.add(buffer.get(i));
        }
        
        return recent;
    }
    
    /**
     * Get frame by ID (checks buffer first, then disk)
     */
    public VisionFrame getFrame(long frameId) {
        // Check buffer first
        for (VisionFrame frame : buffer) {
            if (frame.frameId == frameId) {
                return frame;
            }
        }
        
        // Check disk
        return loadFrameFromDisk(frameId);
    }
    
    /**
     * Clear old frames from disk
     */
    public void clearOldFrames(long keepLastN) {
        try {
            long cutoff = frameCount - keepLastN;
            
            Files.list(Paths.get(bufferDir))
                .filter(p -> p.toString().endsWith(".dat"))
                .forEach(p -> {
                    try {
                        String name = p.getFileName().toString();
                        long id = Long.parseLong(name.substring(6, 16));
                        
                        if (id < cutoff) {
                            Files.delete(p);
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                });
                
        } catch (IOException e) {
            System.err.println("Failed to clear old frames: " + e.getMessage());
        }
    }
    
    /**
     * Get buffer statistics
     */
    public String getStats() {
        long diskFrames = 0;
        long diskSize = 0;
        
        try {
            diskFrames = Files.list(Paths.get(bufferDir))
                .filter(p -> p.toString().endsWith(".dat"))
                .count();
            
            diskSize = Files.list(Paths.get(bufferDir))
                .filter(p -> p.toString().endsWith(".dat"))
                .mapToLong(p -> {
                    try {
                        return Files.size(p);
                    } catch (IOException e) {
                        return 0;
                    }
                })
                .sum();
                
        } catch (IOException e) {
            // Ignore
        }
        
        return String.format(
            "🎞️  VISION FRAME BUFFER\n" +
            "   Total Frames: %d\n" +
            "   In Memory: %d\n" +
            "   On Disk: %d\n" +
            "   Disk Size: %.2f MB\n" +
            "   Max Buffer: %d frames\n" +
            "   Compression: GZIP\n",
            frameCount,
            buffer.size(),
            diskFrames,
            diskSize / (1024.0 * 1024.0),
            maxFrames
        );
    }
    
    /**
     * Estimate memory usage
     */
    public long estimateMemoryUsage() {
        // Rough estimate: each compressed frame ~1KB
        return buffer.size() * 1024L;
    }
    
    /**
     * Vision frame data
     */
    public static class VisionFrame implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public final long frameId;
        public final long timestamp;
        public final Map<String, Double> compressedSignificance;
        public final int[] focalPoint;
        
        public VisionFrame(long frameId, long timestamp, 
                          Map<String, Double> compressedSignificance, int[] focalPoint) {
            this.frameId = frameId;
            this.timestamp = timestamp;
            this.compressedSignificance = compressedSignificance;
            this.focalPoint = focalPoint;
        }
        
        /**
         * Decompress to full significance map
         */
        public double[][] decompress(int width, int height) {
            double[][] map = new double[width][height];
            
            for (Map.Entry<String, Double> entry : compressedSignificance.entrySet()) {
                String[] coords = entry.getKey().split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                
                if (x < width && y < height) {
                    map[x][y] = entry.getValue();
                }
            }
            
            return map;
        }
        
        @Override
        public String toString() {
            return String.format(
                "Frame{id=%d, time=%d, focal=(%d,%d), pixels=%d}",
                frameId, timestamp, focalPoint[0], focalPoint[1],
                compressedSignificance.size()
            );
        }
    }
}
