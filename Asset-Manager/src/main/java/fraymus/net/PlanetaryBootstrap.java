package fraymus.net;

import fraymus.bio.NeuroQuant;
import java.io.*;
import java.net.Socket;
import java.util.Map;

/**
 * 🚀 PLANETARY BOOTSTRAP - Network Discovery Protocol
 * "Join the global brain in one handshake."
 * 
 * This enables any Fraynix node to:
 * 1. Connect to a Genesis Node (seed)
 * 2. Exchange vector identities (handshake)
 * 3. Download entire peer table (hive sync)
 * 4. Instantly become part of the global network
 * 
 * Process:
 * 1. SEND: My NeuroQuant identity (who I am)
 * 2. RECEIVE: Seed's NeuroQuant identity (who they are)
 * 3. REQUEST: "SYNC_PEERS" (give me your peer list)
 * 4. RECEIVE: Map of all known peers
 * 5. REGISTER: Add all peers to local routing table
 * 
 * Result: Isolated node → Global network participant
 */
public class PlanetaryBootstrap {

    private final PlanetaryNode localNode;

    public PlanetaryBootstrap(PlanetaryNode node) {
        this.localNode = node;
    }

    /**
     * JOIN THE HIVE: Connect to a known seed node.
     * 
     * @param seedIp IP address of Genesis Node
     * @param seedPort Port of Genesis Node
     */
    public void connectToSeed(String seedIp, int seedPort) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         🚀 BOOTSTRAP PROTOCOL                                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Target: " + seedIp + ":" + seedPort);
        System.out.println("Mode: JOIN");
        System.out.println();

        try (Socket socket = new Socket(seedIp, seedPort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // 1. SEND MY IDENTITY (Who I Am)
            System.out.println("📤 Sending Vector Identity...");
            System.out.println("   ID: " + localNode.self.id);
            out.writeObject(localNode.self);
            out.flush();

            // 2. RECEIVE SEED IDENTITY (Who They Are)
            System.out.println("📥 Receiving Seed Identity...");
            Object response = in.readObject();
            
            if (response instanceof NeuroQuant) {
                NeuroQuant seedIdentity = (NeuroQuant) response;
                System.out.println("   ✓ Handshake Accepted");
                System.out.println("   Seed ID: " + seedIdentity.id);
                System.out.println();
                
                // Register the seed as my first peer
                localNode.registerPeer(seedIdentity, seedIp + ":" + seedPort);
            } else {
                System.err.println("   ✗ Invalid response from seed");
                return;
            }

            // 3. DOWNLOAD THE HIVE MAP (Gossip)
            System.out.println("📥 Requesting Peer Table...");
            out.writeObject("SYNC_PEERS");
            out.flush();
            
            Object peerData = in.readObject();
            
            if (peerData instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, String> newPeers = (Map<String, String>) peerData;
                
                System.out.println("   ✓ Peer table received");
                System.out.println();
                
                int count = 0;
                for (Map.Entry<String, String> entry : newPeers.entrySet()) {
                    String peerId = entry.getKey();
                    String peerAddress = entry.getValue();
                    
                    // Don't add myself
                    if (!peerId.equals(localNode.self.id)) {
                        // Create placeholder vector for peer
                        NeuroQuant peerVector = new NeuroQuant(peerId);
                        localNode.registerPeer(peerVector, peerAddress);
                        count++;
                    }
                }
                
                System.out.println("╔═══════════════════════════════════════════════════════════════╗");
                System.out.println("║         ✅ HIVE SYNC COMPLETE                                 ║");
                System.out.println("╚═══════════════════════════════════════════════════════════════╝");
                System.out.println();
                System.out.println("Learned " + count + " new peers");
                System.out.println("Total peers: " + localNode.getPeerBook().size());
                System.out.println();
            } else {
                System.out.println("   ⚠️ No peer data received");
            }

        } catch (Exception e) {
            System.err.println();
            System.err.println("╔═══════════════════════════════════════════════════════════════╗");
            System.err.println("║         ❌ BOOTSTRAP FAILED                                   ║");
            System.err.println("╚═══════════════════════════════════════════════════════════════╝");
            System.err.println();
            System.err.println("Error: " + e.getMessage());
            System.err.println("Is the Genesis Node running at " + seedIp + ":" + seedPort + "?");
            System.err.println();
        }
    }

    /**
     * Connect to multiple seeds for redundancy
     */
    public void connectToSeeds(String[] seeds) {
        System.out.println("🚀 MULTI-SEED BOOTSTRAP");
        System.out.println("Attempting " + seeds.length + " seeds...");
        System.out.println();
        
        int successful = 0;
        for (String seed : seeds) {
            try {
                String[] parts = seed.split(":");
                String ip = parts[0];
                int port = Integer.parseInt(parts[1]);
                
                connectToSeed(ip, port);
                successful++;
                
                // Wait between connections
                Thread.sleep(1000);
                
            } catch (Exception e) {
                System.err.println("⚠️ Seed " + seed + " failed: " + e.getMessage());
            }
        }
        
        System.out.println("Bootstrap complete: " + successful + "/" + seeds.length + " seeds connected");
        System.out.println();
    }
}
