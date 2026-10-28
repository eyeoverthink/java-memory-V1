package fraymus.vision;

import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.*;

/**
 * Liveness Detector - Anti-Spoofing System
 * 
 * This system detects whether a face is live (real person) or spoofed
 * (photo, video, mask, 3D model). It uses multiple detection methods
 * to ensure robustness against various attack vectors.
 * 
 * Features:
 * - Blink detection (eye movement)
 * - Head movement detection (pose changes)
 * - Skin texture analysis (real skin vs printed)
 * - Reflection detection (screen glare)
 * - Depth analysis (if depth sensor available)
 * - Challenge-response (random movements)
 */
public class LivenessDetector {
    
    private final boolean useDepthSensor;
    private final Random challengeGenerator;
    
    // Detection thresholds
    private static final double BLINK_THRESHOLD = 0.3; // Eye closure threshold
    private static final double MOVEMENT_THRESHOLD = 10.0; // Minimum pixel movement
    private static final double TEXTURE_THRESHOLD = 0.5; // Skin texture complexity
    private static final int CHALLENGE_TIMEOUT = 5000; // 5 seconds to complete challenge
    
    public LivenessDetector(boolean useDepthSensor) {
        this.useDepthSensor = useDepthSensor;
        this.challengeGenerator = new SecureRandom();
    }
    
    /**
     * Perform comprehensive liveness detection
     * 
     * @param frames Sequence of frames (video or image sequence)
     * @return LivenessResult with detection scores
     */
    public LivenessResult detectLiveness(List<BufferedImage> frames) {
        LivenessResult result = new LivenessResult();
        
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║              LIVENESS DETECTION - ANTI-SPOOFING                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        if (frames.size() < 2) {
            System.out.println("ERROR: Need at least 2 frames for liveness detection");
            result.isLive = false;
            result.reason = "Insufficient frames for liveness detection";
            return result;
        }
        
        // Detection 1: Blink detection
        System.out.println("[1] BLINK DETECTION");
        double blinkScore = detectBlinks(frames);
        result.blinkScore = blinkScore;
        System.out.println("    Blink score: " + String.format("%.4f", blinkScore));
        System.out.println();
        
        // Detection 2: Head movement
        System.out.println("[2] HEAD MOVEMENT DETECTION");
        double movementScore = detectHeadMovement(frames);
        result.movementScore = movementScore;
        System.out.println("    Movement score: " + String.format("%.4f", movementScore));
        System.out.println();
        
        // Detection 3: Skin texture analysis
        System.out.println("[3] SKIN TEXTURE ANALYSIS");
        double textureScore = analyzeSkinTexture(frames.get(0));
        result.textureScore = textureScore;
        System.out.println("    Texture score: " + String.format("%.4f", textureScore));
        System.out.println();
        
        // Detection 4: Reflection detection
        System.out.println("[4] REFLECTION DETECTION");
        double reflectionScore = detectReflections(frames.get(0));
        result.reflectionScore = reflectionScore;
        System.out.println("    Reflection score: " + String.format("%.4f", reflectionScore));
        System.out.println();
        
        // Detection 5: Depth analysis (if available)
        if (useDepthSensor) {
            System.out.println("[5] DEPTH ANALYSIS");
            double depthScore = analyzeDepth(frames);
            result.depthScore = depthScore;
            System.out.println("    Depth score: " + String.format("%.4f", depthScore));
            System.out.println();
        }
        
        // Detection 6: Challenge-response (if interactive)
        System.out.println("[6] CHALLENGE-RESPONSE");
        LivenessChallenge challenge = generateChallenge();
        System.out.println("    Generated challenge: " + challenge.type);
        System.out.println("    Instructions: " + challenge.instructions);
        System.out.println("    (In production, user would complete this challenge)");
        result.challenge = challenge;
        System.out.println();
        
        // Calculate overall liveness score
        double overallScore = calculateOverallScore(result);
        result.overallScore = overallScore;
        
        // Determine if live
        result.isLive = overallScore > 0.6; // Threshold for live detection
        result.reason = result.isLive ? "Multiple liveness indicators passed" : "Liveness indicators failed";
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("LIVENESS RESULT");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("    Overall score: " + String.format("%.4f", overallScore));
        System.out.println("    Is live: " + (result.isLive ? "YES" : "NO"));
        System.out.println("    Reason: " + result.reason);
        System.out.println();
        
        return result;
    }
    
    /**
     * Detect blinks in frame sequence
     */
    private double detectBlinks(List<BufferedImage> frames) {
        // Simulated blink detection
        // In production, would use eye landmark tracking
        
        double blinkCount = 0;
        double totalFrames = frames.size();
        
        // Simulate detecting blinks in 20% of frames
        for (int i = 0; i < frames.size(); i++) {
            if (challengeGenerator.nextDouble() < 0.2) {
                blinkCount++;
            }
        }
        
        double blinkScore = blinkCount / totalFrames;
        return Math.min(blinkScore * 5, 1.0); // Normalize to [0, 1]
    }
    
    /**
     * Detect head movement between frames
     */
    private double detectHeadMovement(List<BufferedImage> frames) {
        // Simulated movement detection
        // In production, would use facial landmark tracking
        
        double totalMovement = 0;
        int comparisons = 0;
        
        for (int i = 1; i < frames.size(); i++) {
            // Simulate movement detection
            double movement = challengeGenerator.nextDouble() * 20.0;
            totalMovement += movement;
            comparisons++;
        }
        
        if (comparisons == 0) return 0.0;
        
        double avgMovement = totalMovement / comparisons;
        return Math.min(avgMovement / MOVEMENT_THRESHOLD, 1.0);
    }
    
    /**
     * Analyze skin texture complexity
     */
    private double analyzeSkinTexture(BufferedImage image) {
        // Calculate texture complexity using local variance
        int width = image.getWidth();
        int height = image.getHeight();
        
        double totalVariance = 0;
        int regions = 0;
        
        int blockSize = 16;
        for (int y = 0; y < height - blockSize; y += blockSize) {
            for (int x = 0; x < width - blockSize; x += blockSize) {
                double blockMean = 0;
                double blockVar = 0;
                
                for (int dy = 0; dy < blockSize; dy++) {
                    for (int dx = 0; dx < blockSize; dx++) {
                        int rgb = image.getRGB(x + dx, y + dy);
                        int gray = (rgb >> 16) & 0xFF;
                        blockMean += gray;
                    }
                }
                blockMean /= (blockSize * blockSize);
                
                for (int dy = 0; dy < blockSize; dy++) {
                    for (int dx = 0; dx < blockSize; dx++) {
                        int rgb = image.getRGB(x + dx, y + dy);
                        int gray = (rgb >> 16) & 0xFF;
                        blockVar += Math.pow(gray - blockMean, 2);
                    }
                }
                blockVar /= (blockSize * blockSize);
                
                totalVariance += blockVar;
                regions++;
            }
        }
        
        double avgVariance = totalVariance / regions;
        return Math.min(avgVariance / 1000.0, 1.0); // Normalize
    }
    
    /**
     * Detect reflections (screen glare, printed photo)
     */
    private double detectReflections(BufferedImage image) {
        // Detect bright spots that indicate reflections
        int width = image.getWidth();
        int height = image.getHeight();
        
        int brightPixels = 0;
        int totalPixels = width * height;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                
                // Detect very bright pixels (potential reflections)
                if (red > 240 && green > 240 && blue > 240) {
                    brightPixels++;
                }
            }
        }
        
        double reflectionRatio = (double) brightPixels / totalPixels;
        return 1.0 - Math.min(reflectionRatio * 100, 1.0); // Lower is worse
    }
    
    /**
     * Analyze depth (if depth sensor available)
     */
    private double analyzeDepth(List<BufferedImage> frames) {
        // Simulated depth analysis
        // In production, would use actual depth sensor data
        
        // Return random score for demo
        return 0.5 + challengeGenerator.nextDouble() * 0.4;
    }
    
    /**
     * Generate a random challenge for user to complete
     */
    private LivenessChallenge generateChallenge() {
        LivenessChallenge challenge = new LivenessChallenge();
        
        String[] challengeTypes = {
            "BLINK_THREE_TIMES",
            "TURN_HEAD_LEFT",
            "TURN_HEAD_RIGHT",
            "SMILE",
            "OPEN_MOUTH"
        };
        
        String[] instructions = {
            "Blink three times",
            "Turn your head to the left",
            "Turn your head to the right",
            "Smile",
            "Open your mouth"
        };
        
        int index = challengeGenerator.nextInt(challengeTypes.length);
        challenge.type = challengeTypes[index];
        challenge.instructions = instructions[index];
        challenge.timestamp = System.currentTimeMillis();
        challenge.timeout = CHALLENGE_TIMEOUT;
        
        return challenge;
    }
    
    /**
     * Calculate overall liveness score from individual scores
     */
    private double calculateOverallScore(LivenessResult result) {
        double score = 0;
        int count = 0;
        
        if (result.blinkScore >= 0) {
            score += result.blinkScore * 0.25;
            count++;
        }
        if (result.movementScore >= 0) {
            score += result.movementScore * 0.25;
            count++;
        }
        if (result.textureScore >= 0) {
            score += result.textureScore * 0.2;
            count++;
        }
        if (result.reflectionScore >= 0) {
            score += result.reflectionScore * 0.15;
            count++;
        }
        if (result.depthScore >= 0) {
            score += result.depthScore * 0.15;
            count++;
        }
        
        return score;
    }
    
    /**
     * Liveness result
     */
    public static class LivenessResult {
        public boolean isLive;
        public String reason;
        public double overallScore;
        public double blinkScore = -1;
        public double movementScore = -1;
        public double textureScore = -1;
        public double reflectionScore = -1;
        public double depthScore = -1;
        public LivenessChallenge challenge;
    }
    
    /**
     * Liveness challenge
     */
    public static class LivenessChallenge {
        public String type;
        public String instructions;
        public long timestamp;
        public int timeout;
        public boolean completed;
    }
}
