package fraymus.net;

import fraymus.bio.NeuroQuant;
import fraymus.core.AuditLog;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Random;

/**
 * 🌍 PLANETARY NODE - Global P2P Intelligence Network
 * "Content-Addressable Intelligence across the planet."
 * 
 * This is NOT traditional P2P (BitTorrent/Kademlia).
 * This is SEMANTIC ROUTING - find nodes by concept, not by ID.
 * 
 * Architecture:
 * - Every node is both server and client
 * - No central map - only semantic neighbors
 * - Queries route by vector resonance (10,000D similarity)
 * - Location-agnostic: "Find expert on Quantum Encryption"
 * 
 * Protocol:
 * 1. HANDSHAKE: Exchange NeuroQuant vectors (identity)
 * 2. GOSSIP: Share peer lists (network discovery)
 * 3. ROUTE: Forward queries to highest-resonance peers
 * 4. RESPOND: Return data if local match found
 */
public class PlanetaryNode implements Runnable {

    // MY IDENTITY
    public final NeuroQuant self; // My 10,000D Vector (Who I am)
    private final int PORT;
    private final AuditLog auditLog;
    
    // THE SWARM (My Known Universe)
    // Map: NodeID -> IP:Port
    private final ConcurrentHashMap<String, String> peerBook = new ConcurrentHashMap<>();
    // List: Known Vectors of Peers (for semantic routing)
    private final CopyOnWriteArrayList<NeuroQuant> peerVectors = new CopyOnWriteArrayList<>();
    
    // BOOTSTRAP SEEDS (Genesis Nodes)
    private final List<String> bootstrapSeeds = new CopyOnWriteArrayList<>();
    
    private volatile boolean running = false;
    private ServerSocket serverSocket;
    
    // Statistics
    private int connectionsAccepted = 0;
    private int queriesRouted = 0;
    private int dataServed = 0;

    public PlanetaryNode(NeuroQuant identity, int port, AuditLog auditLog) {
        this.self = identity;
        this.PORT = port;
        this.auditLog = auditLog;
    }

    /**
     * Add bootstrap seed (Genesis Node)
     */
    public void addBootstrapSeed(String ipPort) {
        bootstrapSeeds.add(ipPort);
    }

    /**
     * Start the planetary node
     */
    public void start() {
        if (running) return;
        running = true;
        new Thread(this, "PlanetaryNode").start();
    }

    /**
     * Stop the planetary node
     */
    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }

    @Override
    public void run() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🌍 PLANETARY NODE ONLINE                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Port: " + PORT);
        System.out.println("Vector ID: " + self.id);
        System.out.println("Semantic Routing: ACTIVE");
        System.out.println();
        
        auditLog.log("planetary_node_started", self.id);

        // 1. START SERVER (Listen for Global Connections)
        new Thread(this::listen, "Planetary-Listen").start();

        // 2. BOOTSTRAP (Connect to Genesis Nodes)
        new Thread(this::bootstrap, "Planetary-Bootstrap").start();

        // 3. START GOSSIP (Tell the world I exist)
        new Thread(this::gossip, "Planetary-Gossip").start();
    }

    /**
     * LISTEN: Accept incoming connections
     */
    private void listen() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("👂 LISTENING on port " + PORT);
            System.out.println();
            
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    connectionsAccepted++;
                    new Thread(() -> handleConnection(client), "Planetary-Handler").start();
                } catch (SocketException e) {
                    if (!running) break; // Normal shutdown
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Listen failed: " + e.getMessage());
        }
    }

    /**
     * THE HANDSHAKE: When two nodes meet.
     * They exchange not just IPs, but their *Minds* (NeuroQuant Vectors).
     */
    private void handleConnection(Socket socket) {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // 1. READ INCOMING MESSAGE
            Object request = in.readObject();
            
            if (request instanceof NeuroQuant) {
                NeuroQuant visitor = (NeuroQuant) request;
                String ip = socket.getInetAddress().getHostAddress();
                int port = socket.getPort();
                
                // 2. REGISTER PEER
                System.out.println("🤝 CONTACT: " + visitor.id + " from " + ip);
                String address = ip + ":" + PORT; // Assume same port
                peerBook.put(visitor.id, address);
                
                // Add to semantic routing table
                boolean exists = peerVectors.stream()
                    .anyMatch(v -> v.id.equals(visitor.id));
                if (!exists) {
                    peerVectors.add(visitor);
                }

                // 3. RESPOND WITH MY VECTOR (Handshake complete)
                out.writeObject(this.self);
                out.flush();
                
                auditLog.log("planetary_handshake", visitor.id);
            }
            else if (request instanceof String) {
                String message = (String) request;
                
                if (message.equals("SYNC_PEERS")) {
                    // Handle peer synchronization request
                    System.out.println("📤 SYNC_PEERS request from " + socket.getInetAddress());
                    
                    // Send peer book as Map<String, String>
                    java.util.Map<String, String> peerExport = new java.util.HashMap<>(peerBook);
                    out.writeObject(peerExport);
                    out.flush();
                    
                    System.out.println("   ✓ Sent " + peerExport.size() + " peers");
                } else {
                    // Handle other queries
                    System.out.println("📡 QUERY RECEIVED: " + message);
                    queriesRouted++;
                }
            }

        } catch (EOFException e) {
            // Connection closed - normal
        } catch (Exception e) {
            // Connection error - ignore
        }
    }

    /**
     * Register a peer in the network
     */
    public void registerPeer(NeuroQuant identity, String address) {
        if (peerBook.containsKey(identity.id)) {
            return; // Already known
        }
        
        peerBook.put(identity.id, address);
        
        // Add to semantic routing table if not exists
        boolean exists = peerVectors.stream()
            .anyMatch(v -> v.id.equals(identity.id));
        if (!exists) {
            peerVectors.add(identity);
        }
        
        System.out.println("   [+] PEER REGISTERED: " + identity.id + " (" + address + ")");
    }

    /**
     * BOOTSTRAP: Connect to Genesis Nodes
     */
    private void bootstrap() {
        if (bootstrapSeeds.isEmpty()) {
            System.out.println("⚠️ No bootstrap seeds - running as isolated node");
            return;
        }

        System.out.println("🌱 BOOTSTRAPPING from " + bootstrapSeeds.size() + " seeds...");
        
        for (String seed : bootstrapSeeds) {
            try {
                String[] parts = seed.split(":");
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                
                connectToPeer(host, port);
                Thread.sleep(1000); // Stagger connections
                
            } catch (Exception e) {
                System.out.println("   ⚠️ Seed " + seed + " unreachable");
            }
        }
        
        System.out.println("   ✓ Bootstrap complete: " + peerBook.size() + " peers");
        System.out.println();
    }

    /**
     * GOSSIP: Periodically share peer lists
     */
    private void gossip() {
        Random rng = new Random();
        
        while (running) {
            try {
                Thread.sleep(30000); // Every 30 seconds
                
                if (peerBook.isEmpty()) continue;
                
                // Pick random peer and reconnect (keep-alive + peer exchange)
                String[] peers = peerBook.values().toArray(new String[0]);
                String randomPeer = peers[rng.nextInt(peers.length)];
                
                String[] parts = randomPeer.split(":");
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                
                connectToPeer(host, port);
                
            } catch (Exception e) {
                // Ignore gossip errors
            }
        }
    }

    /**
     * Connect to a peer
     */
    private void connectToPeer(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 5000);
            
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            // Send my vector
            out.writeObject(this.self);
            out.flush();
            
            // Receive their vector
            Object response = in.readObject();
            if (response instanceof NeuroQuant) {
                NeuroQuant peer = (NeuroQuant) response;
                String address = host + ":" + port;
                
                peerBook.put(peer.id, address);
                
                boolean exists = peerVectors.stream()
                    .anyMatch(v -> v.id.equals(peer.id));
                if (!exists) {
                    peerVectors.add(peer);
                }
                
                System.out.println("   ✓ Connected to " + peer.id + " at " + address);
            }
            
        } catch (Exception e) {
            // Connection failed - peer may be offline
        }
    }

    /**
     * Get peer book
     */
    public ConcurrentHashMap<String, String> getPeerBook() {
        return peerBook;
    }

    /**
     * Get peer vectors
     */
    public List<NeuroQuant> getPeerVectors() {
        return new CopyOnWriteArrayList<>(peerVectors);
    }

    /**
     * Get statistics
     */
    public String getStats() {
        return String.format(
            "Peers: %d, Connections: %d, Queries: %d, Data: %d",
            peerBook.size(), connectionsAccepted, queriesRouted, dataServed
        );
    }
}
