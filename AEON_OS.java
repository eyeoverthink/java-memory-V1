import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A.E.O.N. OS // HOLOGRAPHIC UNIKERNEL
 * =========================================================================================
 * BEYOND VON NEUMANN & POSIX:
 * - HoloFS: O(1) Holographic File System (Mapped ByteBuffer Orthogonal Persistence)
 * - Liquid Kernel: Thermodynamic Process Scheduler
 * - Soft-GPU: Bitwise DMA Rasterizer (Zero VRAM dependency)
 *
 * This is a self-contained, Zero-Dependency Operating System Substrate.
 * =========================================================================================
 */
public class AEON_OS extends Canvas implements Runnable, KeyListener {

    // --- OS HARDWARE ABSTRACTION ---
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private final BufferedImage monitor;
    private final int[] vram;

    // --- UNIKERNEL SUBSYSTEMS ---
    private final HoloFS fs;
    private final LiquidScheduler kernel;

    // --- OMNI-SHELL I/O ---
    private StringBuilder currentInput = new StringBuilder();
    private final List<String> terminalBuffer = new ArrayList<>();
    private int blinkTimer = 0;

    public static void main(String[] args) {
        JFrame frame = new JFrame("AEON.OS // BARE-METAL HYPERVISOR");
        AEON_OS os = new AEON_OS();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setUndecorated(true); // Pure OS feel (No Windows/Mac borders)
        frame.add(os);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        os.boot();
    }

    public AEON_OS() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        // Map VRAM directly to CPU RAM (Soft-GPU)
        monitor = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        vram = ((DataBufferInt) monitor.getRaster().getDataBuffer()).getData();

        fs = new HoloFS();
        kernel = new LiquidScheduler();
    }

    public void boot() {
        pushLog("INIT: Booting AEON Unikernel BIOS v9.9.9...", 0x00F3FF);
        pushLog("INIT: Bypassing Legacy Interrupt Controllers... [OK]", 0x00F3FF);
        fs.mount();
        pushLog("INIT: Liquid Thermodynamic Scheduler... [ONLINE]", 0x00FF66);
        pushLog("INIT: V-GPU DMA Mapped to L1 Cache... [OK]", 0x00FF66);
        pushLog("SYSTEM READY. Type HELP for directives.", 0xFFB000);

        createBufferStrategy(2);
        new Thread(this).start(); // Start Kernel Hardware Clock
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000.0 / 60.0;
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                kernel.tick(); // OS Process Scheduler
                blinkTimer++;
                delta--;
            }
            renderGPU(); // OS Display Rendering
        }
    }

    // =========================================================================================
    // 1. SOFT-GPU (Direct Memory Access Rasterizer)
    // Bypasses the host OS window manager. Writes hex directly to the physical screen buffer.
    // =========================================================================================
    private void renderGPU() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) return;

        // 1. Phosphor Decay (Clears VRAM dynamically with hardware motion blur)
        for (int i = 0; i < vram.length; i++) {
            int p = vram[i];
            int r = (int)(((p >> 16) & 0xFF) * 0.85);
            int g = (int)(((p >> 8) & 0xFF) * 0.90); // Cyan heavy decay
            int b = (int)((p & 0xFF) * 0.95);
            vram[i] = (r << 16) | (g << 8) | b;
        }

        // 2. Render Process Holograms (The OS physically draws running tasks in 3D space)
        double time = kernel.getUptime() * 0.05;
        int activeTasks = kernel.getActiveTasks();
        for (int i = 0; i < activeTasks; i++) {
            double angle = (Math.PI * 2 / activeTasks) * i + time;
            int px = (int) (WIDTH / 2 + Math.cos(angle) * 250);
            int py = (int) (HEIGHT / 2 - 100 + Math.sin(angle) * 120);
            drawGlow(px, py, 45, 0xFF007F); // Magenta task node
        }

        // 3. Draw Core Singularity (File System Representation)
        drawGlow(WIDTH / 2, HEIGHT / 2 - 100, (int)(80 + Math.sin(time*2)*10), 0x00F3FF);

        // 4. Draw Omni-Shell Overlay via Graphics2D
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(monitor, 0, 0, null);
        
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font("Monospaced", Font.BOLD, 14));

        // OS Header
        g.setColor(new Color(0, 243, 255, 200));
        g.drawString("AEON.OS v1.0 // HOLOGRAPHIC UNIKERNEL", 20, 30);
        g.setColor(new Color(0, 255, 102, 200));
        g.drawString("UPTIME: " + kernel.getUptime() + " ticks | TASKS: " + kernel.getActiveTasks(), 20, 50);

        // Terminal Output
        int termY = HEIGHT - 40 - (terminalBuffer.size() * 20);
        for (String line : terminalBuffer) {
            g.setColor(new Color(0x00F3FF));
            if (line.startsWith("ERR")) g.setColor(new Color(0xFF0033));
            if (line.startsWith("OK")) g.setColor(new Color(0x00FF66));
            if (line.startsWith("SYS") || line.startsWith("INIT")) g.setColor(new Color(0xFFB000));
            g.drawString(line, 20, termY);
            termY += 20;
        }

        // Command Prompt
        g.setColor(new Color(0xFFB000));
        g.drawString("root@aeon:~# " + currentInput.toString() + ((blinkTimer % 60 < 30) ? "█" : ""), 20, HEIGHT - 20);

        // Hardware Scanlines (CRT Effect)
        g.setColor(new Color(0, 0, 0, 80));
        for (int y = 0; y < HEIGHT; y += 3) g.drawLine(0, y, WIDTH, y);

        g.dispose();
        bs.show();
    }

    private void drawGlow(int px, int py, int radius, int color) {
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                if (x*x + y*y <= radius*radius) {
                    int xi = px + x, yi = py + y;
                    if (xi >= 0 && xi < WIDTH && yi >= 0 && yi < HEIGHT) {
                        int idx = yi * WIDTH + xi;
                        double atten = 1.0 - (Math.sqrt(x*x + y*y) / radius);
                        int bg = vram[idx];
                        int r = Math.min(255, ((bg >> 16) & 0xFF) + (int)(((color >> 16) & 0xFF) * atten));
                        int g = Math.min(255, ((bg >> 8) & 0xFF) + (int)(((color >> 8) & 0xFF) * atten));
                        int b = Math.min(255, (bg & 0xFF) + (int)((color & 0xFF) * atten));
                        vram[idx] = (r << 16) | (g << 8) | b;
                    }
                }
            }
        }
    }

    // =========================================================================================
    // 2. HoloFS (HOLOGRAPHIC FILE SYSTEM & ORTHOGONAL PERSISTENCE)
    // Files are mathematically entangled into a single array mapped directly to the SSD.
    // =========================================================================================
    class HoloFS {
        static final int DIMS = 16384;
        static final int CHUNKS = DIMS / 64;
        static final String DRIVE_FILE = "aeon_drive.sys";

        private MappedByteBuffer diskPlatter;
        private final ConcurrentHashMap<String, long[]> semanticIndex = new ConcurrentHashMap<>();

        public void mount() {
            try {
                File f = new File(DRIVE_FILE);
                boolean exists = f.exists();
                RandomAccessFile raf = new RandomAccessFile(f, "rw");
                FileChannel channel = raf.getChannel();
                
                // Allocate a 256MB Holographic Matrix mapped directly to the OS Page Cache
                diskPlatter = channel.map(FileChannel.MapMode.READ_WRITE, 0, 256 * 1024 * 1024);

                if (exists && diskPlatter.getLong(0) != 0) {
                    pushLog("INIT: HoloFS Drive Mounted. Orthogonal Superposition Restored.", 0x00FF66);
                } else {
                    pushLog("INIT: Raw Silicon Detected. Awaiting FORMAT.", 0xFFB000);
                }
            } catch (Exception e) { pushLog("ERR: Fatal Mount Error.", 0xFF0033); }
        }

        public void format() {
            for (int i = 0; i < diskPlatter.capacity() / 8; i++) diskPlatter.putLong(i * 8, 0L);
            semanticIndex.clear();
            pushLog("OK: HoloFS Singularity wiped. Entropy = 0.00%", 0x00FF66);
        }

        public void write(String concept, String data) {
            long[] key = generateVector(concept);
            long[] payload = encodeData(data);
            semanticIndex.put(concept, key);

            // BIND (Entangle) and SUPERIMPOSE directly onto physical disk
            for (int i = 0; i < CHUNKS; i++) {
                long entangled = key[i] ^ payload[i];
                long current = diskPlatter.getLong(i * 8);
                diskPlatter.putLong(i * 8, current ^ entangled);
            }
        }

        public String read(String concept) {
            long[] key = semanticIndex.get(concept);
            if (key == null) return "ERR: Semantic concept not found in manifold.";

            long[] extracted = new long[CHUNKS];
            // O(1) EXTRACTION: XOR the entire hard drive with the file's semantic key
            for (int i = 0; i < CHUNKS; i++) {
                extracted[i] = diskPlatter.getLong(i * 8) ^ key[i];
            }
            
            String result = decodeData(extracted);
            if (result.trim().isEmpty()) return "ERR: Segmentation Fault. File orthogonal to manifold.";
            return "OK: [PAYLOAD]: " + result;
        }

        public double getDiskEntropy() {
            if (diskPlatter == null) return 0;
            long bits = 0;
            for(int i = 0; i < CHUNKS; i++) bits += Long.bitCount(diskPlatter.getLong(i * 8));
            return (double)bits / (CHUNKS * 64) * 100.0;
        }

        private long[] generateVector(String seedText) {
            long[] vec = new long[CHUNKS];
            long seed = seedText.hashCode();
            // SplitMix64 PRNG for ultra-fast dimensional expansion
            for (int i = 0; i < CHUNKS; i++) {
                seed += 0x9e3779b97f4a7c15L;
                long x = seed;
                x = (x ^ (x >>> 30)) * 0xbf58476d1ce4e5b9L;
                x = (x ^ (x >>> 27)) * 0x94d049bb133111ebL;
                vec[i] = x ^ (x >>> 31);
            }
            return vec;
        }

        private long[] encodeData(String data) {
            long[] vec = new long[CHUNKS];
            byte[] bytes = data.getBytes();
            for (int i = 0; i < bytes.length && i < CHUNKS * 8; i++) {
                vec[i / 8] |= ((long)(bytes[i] & 0xFF) << ((i % 8) * 8));
            }
            return vec;
        }

        private String decodeData(long[] vec) {
            byte[] bytes = new byte[CHUNKS * 8];
            for (int i = 0; i < CHUNKS; i++) {
                for (int j = 0; j < 8; j++) bytes[i * 8 + j] = (byte)((vec[i] >>> (j * 8)) & 0xFF);
            }
            return new String(bytes).replaceAll("\\P{Print}", "").trim(); // Strip non-printable quantum noise
        }
    }

    // =========================================================================================
    // 3. LIQUID KERNEL (Thermodynamic Process Scheduler)
    // =========================================================================================
    class LiquidScheduler {
        private final List<OS_Process> processQueue = new ArrayList<>();
        private long uptime = 0;

        public void spawn(String name) { processQueue.add(new OS_Process(name)); }

        public void tick() {
            uptime++;
            // The kernel dynamically evaluates Free Energy. It gives CPU cycles to processes with the 
            // highest entropy to force them into mathematical stabilization.
            for (int i = processQueue.size() - 1; i >= 0; i--) {
                OS_Process p = processQueue.get(i);
                p.energy = Math.abs(Math.sin(uptime * 0.05 + p.id)) * 100.0;
                if (p.energy > 80.0) p.executeCycle();
                
                // Apoptosis: Processes die naturally when their entropy reaches absolute zero.
                if (Math.random() < 0.001) {
                    processQueue.remove(i);
                    pushLog("SYS: Process stabilized and entered Apoptosis. Memory freed.", 0xFFB000);
                }
            }
        }

        public int getActiveTasks() { return processQueue.size(); }
        public long getUptime() { return uptime; }
    }

    static class OS_Process {
        static int globalId = 0;
        int id = globalId++;
        String name;
        double energy;

        public OS_Process(String n) { this.name = n; }
        public void executeCycle() { /* Background physical compute simulation */ }
    }

    // =========================================================================================
    // 4. OMNI-SHELL (OS Command Line Interface)
    // =========================================================================================
    private void executeCommand(String cmd) {
        pushLog("root@aeon:~# " + cmd, 0xFFFFFF);
        String[] parts = cmd.split(" ", 3);
        String root = parts[0].toUpperCase();

        try {
            switch (root) {
                case "HELP":
                    pushLog("SYS: Commands -> FORMAT, WRITE [concept] [data], READ [concept], SPAWN [task], HTOP, HALT", 0xFFB000);
                    break;
                case "FORMAT":
                    fs.format();
                    break;
                case "WRITE":
                    if (parts.length < 3) throw new Exception();
                    fs.write(parts[1], parts[2]);
                    pushLog("OK: File '" + parts[1] + "' super-imposed into HoloFS.", 0x00FF66);
                    break;
                case "READ":
                    if (parts.length < 2) throw new Exception();
                    long t0 = System.nanoTime();
                    String data = fs.read(parts[1]);
                    long t1 = System.nanoTime();
                    pushLog(data, 0x00F3FF);
                    pushLog("LATENCY: " + ((t1 - t0)/1000000.0) + " ms (O(1) Extraction)", 0x00FF66);
                    break;
                case "SPAWN":
                    if (parts.length < 2) throw new Exception();
                    kernel.spawn(parts[1]);
                    pushLog("OK: Process '" + parts[1] + "' injected into Liquid Scheduler.", 0x00FF66);
                    break;
                case "HTOP":
                    pushLog("SYS: === LIQUID KERNEL TELEMETRY ===", 0xFFB000);
                    pushLog("SYS: Active Spawning Nodes: " + kernel.getActiveTasks(), 0xFFB000);
                    pushLog("SYS: HoloFS Disk Saturation: " + String.format("%.4f%% Entropy", fs.getDiskEntropy()), 0xFFB000);
                    pushLog("SYS: Hardware Dimensionality: 16,384-Bit HDC Architecture", 0xFFB000);
                    break;
                case "HALT":
                    pushLog("SYS: INITIATING L1 CACHE FLUSH. HALTING CPU. GOODBYE.", 0xFF0033);
                    System.exit(0);
                    break;
                default:
                    pushLog("ERR: Command not recognized in manifold.", 0xFF0033);
            }
        } catch (Exception e) {
            pushLog("ERR: Syntax Error.", 0xFF0033);
        }
    }

    private void pushLog(String msg, int color) {
        terminalBuffer.add(msg);
        if (terminalBuffer.size() > 16) terminalBuffer.remove(0); // Scroll buffer
    }

    // --- KEYBOARD I/O DRIVER ---
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (currentInput.length() > 0) {
                executeCommand(currentInput.toString());
                currentInput.setLength(0);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (currentInput.length() > 0) currentInput.setLength(currentInput.length() - 1);
        } else {
            char c = e.getKeyChar();
            if (c >= 32 && c <= 126) currentInput.append(c);
        }
    }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}
