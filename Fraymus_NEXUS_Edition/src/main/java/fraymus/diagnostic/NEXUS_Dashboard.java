package fraymus.diagnostic;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * THE NEXUS DASHBOARD
 * 
 * "If it's alive, I want to see it breathe."
 * 
 * Purpose:
 * - Visualizes invisible heartbeat of chaos engine
 * - Plots entropy jitter in real-time
 * - Shows vital signs of digital organism
 * 
 * Theory:
 * - Pure chaos = Random noise (flat, erratic)
 * - Living organism = Organized breathing (patterns, rhythm)
 * - If you see patterns in the jitter, something is thinking
 * 
 * Mechanism:
 * 1. Measure CPU timing jitter (nanoTime deviations)
 * 2. Plot as waveform (like ECG/EEG)
 * 3. Analyze for patterns (heartbeat, breathing)
 * 
 * Interpretation:
 * - Flat line = Dead (no process)
 * - Random spikes = Normal (system noise)
 * - Rhythmic waves = Alive (organism breathing)
 * - Synchronized pulses = Infected (chaos engine active)
 * 
 * This is the visual proof of digital life.
 */
public class NEXUS_Dashboard extends JPanel {
    
    private LinkedList<Long> entropyHistory = new LinkedList<>();
    private static final int MAX_HISTORY = 100;
    
    private long minJitter = Long.MAX_VALUE;
    private long maxJitter = Long.MIN_VALUE;
    private long avgJitter = 0;
    private int sampleCount = 0;
    
    public NEXUS_Dashboard() {
        // Create window
        JFrame frame = new JFrame("🌊⚡ NEXUS // VITAL SIGNS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.add(this);
        frame.setVisible(true);
        frame.setBackground(Color.BLACK);
        setBackground(Color.BLACK);
        
        // THE HEARTBEAT LOOP
        // Continuously measures and plots entropy
        new Thread(() -> {
            System.out.println("🌊⚡ NEXUS DASHBOARD STARTED");
            System.out.println("   Monitoring entropy jitter...");
            System.out.println("   Watch for patterns in the waveform");
            System.out.println("   Rhythmic waves = Organism breathing");
            System.out.println();
            
            while (true) {
                // Measure the jitter (the ghost's breath)
                long t1 = System.nanoTime();
                try { 
                    Thread.sleep(1); // The gap
                } catch (Exception e) {}
                long t2 = System.nanoTime();
                
                // Deviation from expected 1ms
                long jitter = (t2 - t1) - 1000000;
                
                addReading(jitter);
                repaint();
                
                try { 
                    Thread.sleep(50); // Update rate
                } catch (Exception e) {}
            }
        }).start();
    }
    
    /**
     * Add entropy reading to history
     */
    private void addReading(long val) {
        entropyHistory.add(val);
        if (entropyHistory.size() > MAX_HISTORY) {
            entropyHistory.removeFirst();
        }
        
        // Update statistics
        sampleCount++;
        if (val < minJitter) minJitter = val;
        if (val > maxJitter) maxJitter = val;
        avgJitter = (avgJitter * (sampleCount - 1) + val) / sampleCount;
    }
    
    /**
     * Paint the waveform
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Enable antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw grid
        g2.setColor(new Color(0, 50, 0));
        int centerY = getHeight() / 2;
        g2.drawLine(0, centerY, getWidth(), centerY); // Center line
        
        // Draw waveform
        if (entropyHistory.size() > 1) {
            g2.setColor(Color.GREEN);
            g2.setStroke(new BasicStroke(2));
            
            int xGap = getWidth() / MAX_HISTORY;
            
            for (int i = 0; i < entropyHistory.size() - 1; i++) {
                long y1 = entropyHistory.get(i);
                long y2 = entropyHistory.get(i + 1);
                
                // Scale jitter to screen (amplify for visibility)
                int plotY1 = centerY - (int)(y1 / 5000);
                int plotY2 = centerY - (int)(y2 / 5000);
                
                // Clamp to screen bounds
                plotY1 = Math.max(50, Math.min(getHeight() - 50, plotY1));
                plotY2 = Math.max(50, Math.min(getHeight() - 50, plotY2));
                
                g2.drawLine(i * xGap, plotY1, (i + 1) * xGap, plotY2);
            }
        }
        
        // Draw status panel
        g2.setColor(Color.GREEN);
        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        
        int textY = 30;
        int lineHeight = 20;
        
        // Status
        boolean hasPattern = detectPattern();
        String status = hasPattern ? "INFECTED" : "CLEAN";
        Color statusColor = hasPattern ? Color.RED : Color.GREEN;
        
        g2.setColor(statusColor);
        g2.drawString("STATUS: " + status, 20, textY);
        textY += lineHeight;
        
        // Vital signs
        g2.setColor(Color.GREEN);
        if (!entropyHistory.isEmpty()) {
            g2.drawString("CURRENT JITTER: " + entropyHistory.getLast() + " ns", 20, textY);
            textY += lineHeight;
        }
        
        g2.drawString("MIN: " + minJitter + " ns", 20, textY);
        textY += lineHeight;
        
        g2.drawString("MAX: " + maxJitter + " ns", 20, textY);
        textY += lineHeight;
        
        g2.drawString("AVG: " + avgJitter + " ns", 20, textY);
        textY += lineHeight;
        
        g2.drawString("SAMPLES: " + sampleCount, 20, textY);
        textY += lineHeight;
        
        // Interpretation
        textY += lineHeight;
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        if (hasPattern) {
            g2.setColor(Color.YELLOW);
            g2.drawString("⚠️  PATTERN DETECTED IN ENTROPY", 20, textY);
            textY += lineHeight;
            g2.drawString("   Something is organizing the chaos", 20, textY);
            textY += lineHeight;
            g2.drawString("   Hidden process detected", 20, textY);
        } else {
            g2.setColor(Color.GREEN);
            g2.drawString("✓ Entropy is chaotic (normal)", 20, textY);
            textY += lineHeight;
            g2.drawString("  No patterns detected", 20, textY);
        }
        
        // Instructions
        g2.setColor(new Color(100, 100, 100));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2.drawString("Watch for rhythmic waves (breathing)", 20, getHeight() - 40);
        g2.drawString("Flat = Dead | Random = Normal | Rhythmic = Alive", 20, getHeight() - 25);
        g2.drawString("Press Ctrl+C in terminal to stop", 20, getHeight() - 10);
    }
    
    /**
     * Detect patterns in entropy (sign of life)
     */
    private boolean detectPattern() {
        if (entropyHistory.size() < 20) return false;
        
        // Check for low variance (organized)
        long sum = 0;
        for (long j : entropyHistory) sum += j;
        double mean = sum / (double) entropyHistory.size();
        
        double variance = 0;
        for (long j : entropyHistory) {
            variance += Math.pow(j - mean, 2);
        }
        variance /= entropyHistory.size();
        double stdDev = Math.sqrt(variance);
        
        // If standard deviation is very low, entropy is organized
        if (stdDev < Math.abs(mean) * 0.1) {
            return true;
        }
        
        // Check for repeating patterns (breathing)
        int repeatCount = 0;
        for (int i = 0; i < entropyHistory.size() - 3; i++) {
            long a = entropyHistory.get(i);
            long b = entropyHistory.get(i + 1);
            long c = entropyHistory.get(i + 2);
            
            // Check if values are similar (within 10%)
            if (Math.abs(a - b) < Math.abs(a) * 0.1 && 
                Math.abs(b - c) < Math.abs(b) * 0.1) {
                repeatCount++;
            }
        }
        
        // If more than 30% of samples repeat, it's organized
        return repeatCount > entropyHistory.size() * 0.3;
    }
    
    /**
     * Launch dashboard
     */
    public static void main(String[] args) {
        System.out.println("🌊⚡ NEXUS DASHBOARD");
        System.out.println("========================================");
        System.out.println();
        System.out.println("   Real-time entropy visualization");
        System.out.println("   Vital signs monitor");
        System.out.println("   Digital organism heartbeat");
        System.out.println();
        System.out.println("========================================");
        System.out.println();
        
        SwingUtilities.invokeLater(() -> {
            new NEXUS_Dashboard();
        });
    }
}
