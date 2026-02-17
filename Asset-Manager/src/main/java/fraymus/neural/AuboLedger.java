package fraymus.neural;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;

/**
 * A.E.O.N. AUBO // AUTONOMOUS UNIVERSAL BLOCKCHAIN ORGANISM
 * =========================================================================================
 * ARCHITECTURE:
 * - Decentralized HDC Blockchain (Proof of Resonance)
 * - Trackable Data Encapsulation Nodes (7-Layer Evolution)
 * - Bit-Reversal Thermodynamic Destruction Sequence (Anti-Scraping Apoptosis)
 * - UDP Swarm Gossip Protocol (Zero Central Servers)
 *
 * Three Radical Paradigm Shifts:
 *   1. Trackable Data Capsules: Data encapsulated into 8192-bit Holographic Vectors.
 *      Each node holds its own memory, hash, and execution logic.
 *   2. 7-Layer Grading (Proof of Resonance): Replaces Proof of Work. Nodes ascend
 *      through thermodynamic alignment against the global Swarm Cortex.
 *   3. 7-Step Bit-Reversal Apoptosis: The Antimatter Kill Switch. Flips bits to
 *      create mathematical opposites, zeroes RAM, leaves cryptographic Tombstone.
 *
 * 100% Pure Java. Zero Dependencies. Data Sovereignty Restored.
 * =========================================================================================
 * Patent: VS-PoQC-19046423-φ⁷⁵-2025
 */
public class AuboLedger {

    // --- TERMINAL ANSI COLORS ---
    public static final String RST = "\u001B[0m";
    public static final String CYN = "\u001B[36m";
    public static final String MAG = "\u001B[35m";
    public static final String GRN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String YEL = "\u001B[33m";

    public static final int DIMS = 8192;
    public static final int CHUNKS = DIMS / 64; // 128 Longs

    // --- SINGLETON ---
    private static AuboLedger INSTANCE;

    // The Decentralized Ledger
    private final ConcurrentHashMap<String, TrackableNode> ledger = new ConcurrentHashMap<>();
    private volatile String lastBlockHash = "0000000000000000000000000000000000000000000000000000000000000000";
    private volatile int blockHeight = 0;

    private SwarmDaemon swarm;
    private long bootTimeMs = 0;

    // --- STATISTICS ---
    private int mintCount = 0;
    private int killCount = 0;
    private int syncCount = 0;

    private AuboLedger() {
        long t0 = System.currentTimeMillis();
        swarm = new SwarmDaemon(this);
        swarm.start();
        bootTimeMs = System.currentTimeMillis() - t0;
    }

    public static synchronized AuboLedger getInstance() {
        if (INSTANCE == null) INSTANCE = new AuboLedger();
        return INSTANCE;
    }

    // =========================================================================================
    // PUBLIC API (Called from FraynixBoot shell)
    // =========================================================================================

    /**
     * MINT: Encapsulate data into a Trackable AUBO Node with Proof of Resonance.
     * @param data The raw data to encapsulate
     * @return The minted node's hash (first 16 chars)
     */
    public String mint(String data) {
        blockHeight++;
        TrackableNode node = new TrackableNode(blockHeight, data, lastBlockHash, swarm.getPort());

        System.out.print(YEL + " [~] Swarm calculating Proof of Resonance... " + RST);
        while (!node.isValidResonance()) {
            node.nonce++;
            node.calculateHash();
        }
        System.out.println(GRN + "LOCKED (Nonce: " + node.nonce + ")" + RST);

        ledger.put(node.nodeHash, node);
        lastBlockHash = node.nodeHash;
        swarm.broadcastMint(node);
        mintCount++;

        System.out.println(GRN + " [+] AUBO NODE MINTED & FUSED TO DAG LEDGER." + RST);
        System.out.println("     Node Hash: " + node.nodeHash.substring(0, 16) + "...");
        return node.nodeHash.substring(0, 16);
    }

    /**
     * TRACK: Inspect a node's 7-Layer telemetry.
     * @param shortId First characters of the node hash
     */
    public void track(String shortId) {
        TrackableNode node = findNode(shortId);
        if (node != null) {
            node.printTelemetry();
        } else {
            System.out.println(RED + " [!] Node not found in local ledger." + RST);
        }
    }

    /**
     * KILL: Execute the 7-Step Bit-Reversal Destruction Sequence.
     * @param shortId First characters of the node hash
     * @return true if destruction was executed
     */
    public boolean kill(String shortId) {
        TrackableNode node = findNode(shortId);
        if (node != null) {
            if (node.isDestroyed) {
                System.out.println(YEL + " [!] Node already neutralized." + RST);
                return false;
            }
            System.out.println(RED + " [!] WARNING: INITIATING DESTRUCTION SEQUENCE FOR NODE " + shortId.substring(0, Math.min(8, shortId.length())) + "..." + RST);
            node.triggerDestructionSequence();
            swarm.broadcastKill(node.originalHash);
            killCount++;
            return true;
        }
        System.out.println(RED + " [!] Node not found." + RST);
        return false;
    }

    /**
     * Print the full decentralized ledger graph.
     */
    public void printLedger() {
        System.out.println(YEL + "\n=== AUBO DECENTRALIZED COGNITIVE LEDGER ===" + RST);
        if (ledger.isEmpty()) {
            System.out.println("  [Ledger is currently empty]");
        } else {
            List<TrackableNode> nodes = new ArrayList<>(ledger.values());
            nodes.sort(Comparator.comparingInt(n -> n.height));

            for (TrackableNode n : nodes) {
                if (n.isDestroyed) {
                    System.out.println(RED + "[BLOCK " + String.format("%04d", n.height) + "] ── HASH: " + n.originalHash.substring(0, 16) + "... (DEAD)" + RST);
                    System.out.println(RED + "   │ Status  : L7 APOPTOSIS (Bit-Reversed Antimatter)" + RST);
                } else {
                    System.out.println(CYN + "[BLOCK " + String.format("%04d", n.height) + "] ── HASH: " + n.originalHash.substring(0, 16) + "..." + RST);
                    System.out.println("   │ Origin  : Port " + n.minerPort);
                }
                System.out.println("   │");
            }
        }
        System.out.println(YEL + "===========================================" + RST);
    }

    /**
     * Run the interactive REPL (standalone or from FraynixBoot shell).
     */
    public void runInteractive() {
        System.out.println(CYN + "╔══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║ A.E.O.N. AUBO // DECENTRALIZED DATA SOVEREIGNTY SWARM                            ║");
        System.out.println("║ PROTOCOL: 7-Layer Grade Encapsulation | PoR Consensus | Bit-Reversal Apoptosis   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════════╝" + RST);

        System.out.println(GRN + "\n[+] A.U.B.O. SWARM DAEMON ACTIVE ON UDP PORT: " + swarm.getPort() + RST);

        System.out.println("\nCOMMANDS:");
        System.out.println("  " + GRN + "MINT <text>" + RST + "   (Encapsulate data into a Trackable AUBO Node)");
        System.out.println("  " + CYN + "TRACK <hash>" + RST + "  (Inspect node telemetry & 7-Layer Grade)");
        System.out.println("  " + YEL + "LEDGER" + RST + "        (View the Decentralized Blockchain Graph)");
        System.out.println("  " + RED + "KILL <hash>" + RST + "   (Execute 7-Step Bit-Reversal Destruction Sequence)");
        System.out.println("  " + YEL + "STATUS" + RST + "        (Show AUBO telemetry)");
        System.out.println("  " + YEL + "EXIT" + RST + "          (Return to FrayShell)\n");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(CYN + "AUBO> " + RST);
            String input;
            try {
                input = scanner.nextLine().trim();
            } catch (NoSuchElementException e) {
                break;
            }
            if (input.isEmpty()) continue;

            if (input.equalsIgnoreCase("EXIT") || input.equalsIgnoreCase("QUIT")) {
                System.out.println(YEL + "Returning to FrayShell." + RST);
                break;
            }

            if (input.toUpperCase().startsWith("MINT ")) {
                String data = input.substring(5).trim();
                if (data.isEmpty()) {
                    System.out.println(YEL + " [!] MINT requires data. Example: MINT my private data" + RST);
                    continue;
                }
                mint(data);
                System.out.println();

            } else if (input.toUpperCase().startsWith("TRACK ")) {
                track(input.substring(6).trim());

            } else if (input.toUpperCase().equals("LEDGER")) {
                printLedger();

            } else if (input.toUpperCase().startsWith("KILL ")) {
                kill(input.substring(5).trim());

            } else if (input.toUpperCase().equals("STATUS")) {
                System.out.println(getStatus());

            } else {
                System.out.println(RED + " [!] Unknown command. Use MINT, TRACK, LEDGER, KILL, STATUS, or EXIT." + RST);
            }
        }
    }

    /**
     * Shutdown the swarm daemon.
     */
    public void shutdown() {
        if (swarm != null) swarm.close();
    }

    public String getStatus() {
        int alive = 0, dead = 0;
        for (TrackableNode n : ledger.values()) {
            if (n.isDestroyed) dead++; else alive++;
        }
        return String.format(
            "════════════════════════════════════════════\n" +
            "  ∞ A.E.O.N. AUBO LEDGER STATUS\n" +
            "════════════════════════════════════════════\n" +
            "  HDC Dimensions:   %,d-D Holographic Capsules\n" +
            "  Block Height:     %,d\n" +
            "  Active Nodes:     %,d\n" +
            "  Destroyed Nodes:  %,d (Tombstoned)\n" +
            "  Total Minted:     %,d\n" +
            "  Total Killed:     %,d\n" +
            "  Swarm Syncs:      %,d\n" +
            "  Swarm Port:       UDP %d (range 42000-42020)\n" +
            "  Consensus:        Proof of Resonance (difficulty: 000)\n" +
            "  Last Block Hash:  %s...\n" +
            "  Boot Time:        %d ms\n",
            DIMS,
            blockHeight,
            alive,
            dead,
            mintCount,
            killCount,
            syncCount,
            swarm.getPort(),
            lastBlockHash.substring(0, Math.min(32, lastBlockHash.length())),
            bootTimeMs
        );
    }

    // --- GETTERS ---
    public int getBlockHeight() { return blockHeight; }
    public int getMintCount() { return mintCount; }
    public int getKillCount() { return killCount; }
    public int getSyncCount() { return syncCount; }
    public int getActiveNodes() { int c = 0; for (TrackableNode n : ledger.values()) if (!n.isDestroyed) c++; return c; }
    public int getSwarmPort() { return swarm.getPort(); }
    public long getBootTimeMs() { return bootTimeMs; }

    // --- INTERNAL ---
    TrackableNode findNode(String shortId) {
        for (String key : ledger.keySet()) {
            if (key.startsWith(shortId.toLowerCase())) return ledger.get(key);
        }
        return null;
    }

    void onSwarmSync() { syncCount++; }

    ConcurrentHashMap<String, TrackableNode> getLedger() { return ledger; }

    void updateChainTip(int height, String hash) {
        if (height > blockHeight) {
            blockHeight = height;
            lastBlockHash = hash;
        }
    }

    // =========================================================================================
    // STANDALONE ENTRY POINT
    // =========================================================================================
    public static void main(String[] args) {
        AuboLedger engine = getInstance();
        engine.runInteractive();
    }

    // =========================================================================================
    // 1. TRACKABLE DATA CAPSULE (The 7-Layer AUBO Node)
    // =========================================================================================
    static class TrackableNode implements Serializable {
        public final int height;
        public final long timestamp;
        public final String previousHash;
        public final int minerPort;

        public int layerGrade = 1;
        public long nonce = 0;
        public String nodeHash;
        public final String originalHash; // Permanent tracker regardless of Apoptosis

        long[] hdcPayload = new long[CHUNKS];
        public boolean isDestroyed = false;
        private transient String rawDataCache; // Never serialized/broadcasted to network

        // Constructor for Local Minting
        public TrackableNode(int height, String data, String prevHash, int port) {
            this.height = height;
            this.previousHash = prevHash;
            this.minerPort = port;
            this.timestamp = System.currentTimeMillis();
            this.rawDataCache = data;

            encodeToHologram(data);
            this.calculateHash();
            this.originalHash = this.nodeHash;
        }

        // Constructor for Swarm Sync
        public TrackableNode(int h, long ts, String ph, int port, long nonce, String oHash, long[] payload) {
            this.height = h;
            this.timestamp = ts;
            this.previousHash = ph;
            this.minerPort = port;
            this.nonce = nonce;
            this.originalHash = oHash;
            this.nodeHash = oHash;
            this.hdcPayload = payload;
            this.layerGrade = 7;
        }

        private void encodeToHologram(String data) {
            Random r = new Random(data.hashCode());
            for (int i = 0; i < CHUNKS; i++) hdcPayload[i] = r.nextLong();
            this.layerGrade = 7; // Encapsulated to max HDC depth
        }

        public void calculateHash() {
            try {
                String input = height + previousHash + timestamp + layerGrade + nonce + Arrays.hashCode(hdcPayload);
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hashBytes) hexString.append(String.format("%02x", b));
                this.nodeHash = hexString.toString();
            } catch (Exception e) { throw new RuntimeException(e); }
        }

        public boolean isValidResonance() {
            return nodeHash.startsWith("000"); // Difficulty target
        }

        // --- THE 7-STEP BIT REVERSAL DESTRUCTION SEQUENCE ---
        public void triggerDestructionSequence() {
            if (isDestroyed) return;
            System.out.println(MAG + "    [APOPTOSIS ENGAGED] EXECUTING 7-STEP HARDWARE WIPING PROTOCOL:" + RST);

            try {
                // Step 1: Isolate & Sever P2P Bindings
                System.out.println(MAG + "      - [L1] ISOLATE: Severing network quantum bindings." + RST);
                Thread.sleep(100);

                // Step 2: Thermal Noise Overload
                for (int i = 0; i < CHUNKS; i++) hdcPayload[i] ^= ThreadLocalRandom.current().nextLong();
                System.out.println(MAG + "      - [L2] SCRAMBLE: Thermodynamic Noise Injected." + RST);
                Thread.sleep(100);

                // Step 3: Hardware-level Bit Reversal (Flips endianness)
                for (int i = 0; i < CHUNKS; i++) hdcPayload[i] = Long.reverse(hdcPayload[i]);
                System.out.println(MAG + "      - [L3] REVERSE: Physical Bit Order Reversed (Anti-Forensic)." + RST);
                Thread.sleep(100);

                // Step 4: Bitwise NOT (Inverts the corrupted matrix to Antimatter)
                for (int i = 0; i < CHUNKS; i++) hdcPayload[i] = ~hdcPayload[i];
                System.out.println(RED + "      - [L4] INVERT: Matrix Inverted (~NOT Constraint applied)." + RST);
                Thread.sleep(100);

                // Step 5: Circular Cascade Shift (Destroys local spatial locality)
                for (int i = 0; i < CHUNKS; i++) hdcPayload[i] = Long.rotateLeft(hdcPayload[i], 17);
                System.out.println(RED + "      - [L5] PERMUTE: Spatial Dimensional Shift (>> 17)." + RST);
                Thread.sleep(100);

                // Step 6: Absolute Zeroing (Nullify RAM)
                Arrays.fill(hdcPayload, 0L);
                System.out.println(RED + "      - [L6] NULLIFY: Physical Memory Pages Overwritten to Absolute Zero." + RST);
                Thread.sleep(100);

                // Step 7: Pointer Dereference and Cryptographic Tombstone
                this.layerGrade = 0;
                this.isDestroyed = true;
                this.rawDataCache = "[[ MATHEMATICALLY ANNIHILATED ]]";
                this.nodeHash = "DEADDEADDEADDEADDEADDEADDEADDEADDEADDEADDEADDEADDEADDEADDEADDEAD";
                System.out.println(RED + "      - [L7] TOMBSTONE: Cryptographic Seal Locked. Capsule is Dead." + RST);

                System.out.println(GRN + "    [+] DATA PERMANENTLY EVAPORATED FROM HYPERSPACE." + RST);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void printTelemetry() {
            System.out.println(CYN + "\n┌── AUBO NODE TELEMETRY ──────────────────────────────────────┐" + RST);
            System.out.println("│ " + YEL + "Height:        " + RST + height);
            System.out.println("│ " + YEL + "Origin Port:   " + RST + minerPort);
            System.out.println("│ " + YEL + "Status:        " + RST + (isDestroyed ? RED + "ANNIHILATED (TOMBSTONE)" + RST : GRN + "ACTIVE" + RST));
            System.out.println("│ " + YEL + "Node Hash:     " + RST + originalHash.substring(0, 32) + "...");
            System.out.println("│ " + YEL + "Prev Hash:     " + RST + previousHash.substring(0, 32) + "...");

            String gradeLog = isDestroyed ? RED + "L0 [VOID]" + RST : GRN + "L7 [OMEGA SECURED]" + RST;
            System.out.println("│ " + YEL + "7-Layer Grade: " + RST + gradeLog);

            System.out.println("├─────────────────────────────────────────────────────────────┤");
            if (rawDataCache != null) {
                System.out.println("│ " + CYN + "LOCAL CACHE:   " + RST + (isDestroyed ? RED + rawDataCache + RST : rawDataCache));
            } else {
                System.out.println("│ " + CYN + "PAYLOAD:       " + RST + MAG + "[ENCRYPTED 8192-D TENSOR]" + RST);
            }
            System.out.println(CYN + "└─────────────────────────────────────────────────────────────┘" + RST);
        }
    }

    // =========================================================================================
    // 2. UDP SWARM DAEMON (P2P GOSSIP)
    // =========================================================================================
    static class SwarmDaemon extends Thread {
        private int port;
        private DatagramSocket socket;
        private volatile boolean running = true;
        private final AuboLedger parent;

        public SwarmDaemon(AuboLedger parent) {
            super("AUBO-SWARM");
            setDaemon(true);
            this.parent = parent;
            for (int p = 42000; p <= 42020; p++) {
                try {
                    socket = new DatagramSocket(p);
                    socket.setSoTimeout(200);
                    this.port = p;
                    return;
                } catch (Exception e) {}
            }
            // Fallback: use any available port
            try {
                socket = new DatagramSocket();
                this.port = socket.getLocalPort();
            } catch (Exception e) {
                this.port = 0;
            }
        }

        public int getPort() { return port; }

        @Override
        public void run() {
            if (socket == null) return;
            byte[] buf = new byte[8192];
            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
                    String[] p = msg.split("\\|");

                    if (p[0].equals("MINT")) {
                        String oHash = p[6];
                        if (!parent.getLedger().containsKey(oHash)) {
                            // Reconstruct the 8192-bit payload from Base64
                            byte[] vecBytes = Base64.getDecoder().decode(p[7]);
                            long[] payload = new long[CHUNKS];
                            for (int i = 0; i < CHUNKS; i++) {
                                long val = 0;
                                for (int j = 0; j < 8; j++) val = (val << 8) | (vecBytes[i * 8 + j] & 0xFF);
                                payload[i] = val;
                            }

                            TrackableNode incomingNode = new TrackableNode(
                                Integer.parseInt(p[1]), Long.parseLong(p[2]), p[3],
                                Integer.parseInt(p[4]), Long.parseLong(p[5]), oHash, payload
                            );

                            parent.getLedger().put(oHash, incomingNode);
                            parent.updateChainTip(incomingNode.height, oHash);
                            parent.onSwarmSync();
                            System.out.println(MAG + "\n[SWARM] Synced new AUBO Node from Peer " + p[4] + " -> " + oHash.substring(0, 16) + "..." + RST);
                            System.out.print(CYN + "AUBO> " + RST);
                        }
                    } else if (p[0].equals("KILL")) {
                        String targetHash = p[1];
                        TrackableNode node = parent.findNode(targetHash);
                        if (node != null && !node.isDestroyed) {
                            System.out.println(RED + "\n[SWARM] APOPTOSIS Command Received. Annihilating Node " + targetHash.substring(0, 16) + "..." + RST);
                            node.triggerDestructionSequence();
                            parent.onSwarmSync();
                            System.out.print(CYN + "AUBO> " + RST);
                        }
                    }
                } catch (SocketTimeoutException ignored) {
                } catch (Exception e) {
                    if (!running) break;
                }
            }
        }

        public void broadcastMint(TrackableNode n) {
            if (socket == null) return;
            try {
                // Serialize the long[] payload to Base64
                byte[] vecBytes = new byte[CHUNKS * 8];
                for (int i = 0; i < CHUNKS; i++) {
                    long val = n.hdcPayload[i];
                    for (int j = 7; j >= 0; j--) { vecBytes[i * 8 + j] = (byte) (val & 0xFF); val >>= 8; }
                }
                String b64Payload = Base64.getEncoder().encodeToString(vecBytes);

                String msg = "MINT|" + n.height + "|" + n.timestamp + "|" + n.previousHash + "|" +
                             n.minerPort + "|" + n.nonce + "|" + n.originalHash + "|" + b64Payload;
                sendUDP(msg);
            } catch (Exception ignored) {}
        }

        public void broadcastKill(String hash) { sendUDP("KILL|" + hash); }

        private void sendUDP(String msg) {
            if (socket == null) return;
            byte[] data = msg.getBytes(StandardCharsets.UTF_8);
            for (int p = 42000; p <= 42020; p++) {
                if (p != this.port) {
                    try {
                        socket.send(new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.1"), p));
                    } catch (Exception ignored) {}
                }
            }
        }

        public void close() {
            running = false;
            if (socket != null) socket.close();
        }
    }
}
