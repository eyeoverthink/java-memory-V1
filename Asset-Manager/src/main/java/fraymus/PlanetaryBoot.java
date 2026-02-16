package fraymus;

import fraymus.bio.NeuroQuant;
import fraymus.net.PlanetaryNode;
import fraymus.net.PlanetaryBootstrap;
import fraymus.net.VectorRouter;
import fraymus.core.AuditLog;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * 🌍 PLANETARY BOOT - Global Semantic Network Launcher
 * "Decentralized thinking across the planet."
 * 
 * This creates a node in the Planetary Cortex:
 * 1. Creates unique semantic identity (10,000D vector)
 * 2. Starts TCP server for global connections
 * 3. Connects to bootstrap seeds (Genesis Nodes)
 * 4. Enables semantic routing by concept
 * 
 * Architecture:
 * - Traditional P2P: Find files by ID (Kademlia/DHT)
 * - Planetary Cortex: Find intelligence by CONCEPT (Vector Routing)
 * 
 * Usage:
 * - Genesis Node: java -jar app.jar 8000
 * - Join Node: java -jar app.jar 8001 127.0.0.1 8000
 * 
 * Arguments:
 * - args[0]: Port to listen on (default: 7777)
 * - args[1]: Seed IP to connect to (optional, Genesis mode if not provided)
 * - args[2]: Seed port (default: same as args[0])
 */
public class PlanetaryBoot {

    public static void main(String[] args) throws Exception {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🌍 PLANETARY CORTEX v1.0                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Initialize
        AuditLog auditLog = new AuditLog("audit");
        auditLog.start();

        // 1. GENERATE IDENTITY
        String identity = "NODE_" + (System.currentTimeMillis() % 10000);
        System.out.println("Identity: " + identity);
        
        // Create semantic signature
        NeuroQuant myVector = new NeuroQuant(identity);
        
        // Add expertise (what this node knows about)
        System.out.println();
        System.out.println("Expertise:");
        System.out.println("  - Java Programming");
        System.out.println("  - Artificial Intelligence");
        System.out.println("  - Distributed Systems");
        System.out.println("  - Quantum Computing");
        System.out.println();

        // 2. CONFIGURE PORT
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 7777;
        
        // 3. START PLANETARY NODE
        PlanetaryNode node = new PlanetaryNode(myVector, port, auditLog);
        node.start(); // Start Server
        
        // Get local IP
        String localIp = InetAddress.getLocalHost().getHostAddress();
        System.out.println("📍 LOCAL ADDRESS: " + localIp + ":" + port);
        System.out.println();

        // 4. BOOTSTRAP LOGIC
        if (args.length > 1) {
            // JOIN MODE: Connect to seed
            String seedIp = args[1];
            int seedPort = (args.length > 2) ? Integer.parseInt(args[2]) : port;
            
            System.out.println("🔗 MODE: JOINING CLUSTER");
            System.out.println("   Seed: " + seedIp + ":" + seedPort);
            System.out.println();
            
            PlanetaryBootstrap bootstrap = new PlanetaryBootstrap(node);
            bootstrap.connectToSeed(seedIp, seedPort);
        } else {
            // GENESIS MODE: First node
            System.out.println("👑 MODE: GENESIS NODE");
            System.out.println("   Waiting for peers to connect...");
            System.out.println("   Other nodes can join with:");
            System.out.println("   java -jar app.jar <port> " + localIp + " " + port);
            System.out.println();
        }

        // Start node
        node.start();
        
        // Give it time to bootstrap
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 4. Interactive Shell
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         PLANETARY SHELL                                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  peers          - Show connected peers");
        System.out.println("  route <query>  - Find expert for concept");
        System.out.println("  stats          - Show statistics");
        System.out.println("  help           - Show this help");
        System.out.println("  exit           - Shutdown node");
        System.out.println();

        VectorRouter router = new VectorRouter();
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("planetary> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) continue;
            
            String[] parts = input.split("\\s+", 2);
            String command = parts[0].toLowerCase();
            
            switch (command) {
                case "peers":
                    showPeers(node);
                    break;
                    
                case "route":
                    if (parts.length < 2) {
                        System.out.println("Usage: route <concept>");
                        break;
                    }
                    routeQuery(parts[1], node, router);
                    break;
                    
                case "stats":
                    showStats(node);
                    break;
                    
                case "help":
                    showHelp();
                    break;
                    
                case "exit":
                    System.out.println();
                    System.out.println("╔═══════════════════════════════════════════════════════════════╗");
                    System.out.println("║         🌍 PLANETARY NODE SHUTTING DOWN                       ║");
                    System.out.println("╚═══════════════════════════════════════════════════════════════╝");
                    System.out.println();
                    System.out.println("Final Statistics:");
                    System.out.println("  " + node.getStats());
                    System.out.println();
                    
                    node.stop();
                    auditLog.stop();
                    scanner.close();
                    return;
                    
                default:
                    System.out.println("Unknown command: " + command);
                    System.out.println("Type 'help' for available commands");
            }
            
            System.out.println();
        }
    }

    private static void showPeers(PlanetaryNode node) {
        var peerBook = node.getPeerBook();
        var peerVectors = node.getPeerVectors();
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("CONNECTED PEERS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
        if (peerBook.isEmpty()) {
            System.out.println("No peers connected yet");
        } else {
            System.out.println("Total Peers: " + peerBook.size());
            System.out.println();
            
            for (var entry : peerBook.entrySet()) {
                String id = entry.getKey();
                String address = entry.getValue();
                
                // Find vector
                NeuroQuant vector = peerVectors.stream()
                    .filter(v -> v.id.equals(id))
                    .findFirst()
                    .orElse(null);
                
                System.out.println("  ID: " + id);
                System.out.println("  Address: " + address);
                if (vector != null) {
                    System.out.println("  Energy: " + String.format("%.2f", vector.energy));
                }
                System.out.println();
            }
        }
    }

    private static void routeQuery(String concept, PlanetaryNode node, VectorRouter router) {
        System.out.println();
        
        // Create query vector
        NeuroQuant query = new NeuroQuant(concept);
        
        // Route
        String target = router.routeQuery(
            query,
            node.getPeerVectors(),
            node.getPeerBook()
        );
        
        if (target != null) {
            System.out.println("✅ Query can be routed to: " + target);
        } else {
            System.out.println("❌ No expert found in network");
            System.out.println("   Try connecting to more peers");
        }
    }

    private static void showStats(PlanetaryNode node) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("NODE STATISTICS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println(node.getStats());
    }

    private static void showHelp() {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("PLANETARY SHELL COMMANDS");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("peers          - Show all connected peers");
        System.out.println("route <query>  - Find expert for concept");
        System.out.println("                 Example: route Quantum Encryption");
        System.out.println("stats          - Show node statistics");
        System.out.println("help           - Show this help");
        System.out.println("exit           - Shutdown node");
    }
}
