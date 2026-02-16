package fraymus.net;

import fraymus.bio.NeuroQuant;
import java.util.List;
import java.util.Map;

/**
 * 🧭 VECTOR ROUTER - Semantic Routing Engine
 * "Find data not by filename, but by Meaning."
 * 
 * This is the "Google" of the Planetary Cortex.
 * Instead of routing by IP address, we route by CONCEPT.
 * 
 * Traditional Routing:
 * - "Send packet to 192.168.1.5" (location-dependent, fragile)
 * 
 * Semantic Routing:
 * - "Send packet to node that understands 'Quantum Encryption'" (location-agnostic, robust)
 * 
 * Process:
 * 1. Receive query as NeuroQuant (10,000D concept vector)
 * 2. Calculate Hamming Distance to all known peers
 * 3. Find peer with highest resonance (expertise)
 * 4. Return IP address for routing
 * 
 * This enables:
 * - Content-addressable intelligence
 * - Automatic expert discovery
 * - Fault-tolerant routing (semantic neighbors can substitute)
 */
public class VectorRouter {

    /**
     * FIND EXPERT: Who in my network understands this concept best?
     * 
     * @param query The concept to find (as 10,000D vector)
     * @param peers List of known peer vectors
     * @param addressBook Map of peer IDs to IP addresses
     * @return IP address of best peer, or null if no match
     */
    public String routeQuery(NeuroQuant query, List<NeuroQuant> peers, 
                            Map<String, String> addressBook) {
        
        if (peers.isEmpty()) {
            System.out.println("📡 ROUTING: No peers available");
            return null;
        }

        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         📡 SEMANTIC ROUTING                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Query: " + query.id);
        System.out.println("Scanning " + peers.size() + " peers...");
        System.out.println();

        NeuroQuant bestPeer = null;
        double maxResonance = -1.0;

        // Calculate resonance with each peer
        for (NeuroQuant peer : peers) {
            // HYPER-MATH: Calculate 10,000D Resonance
            double similarity = query.resonance(peer);
            
            System.out.println("   Peer: " + peer.id);
            System.out.println("   Resonance: " + String.format("%.4f", similarity));
            
            if (similarity > maxResonance) {
                maxResonance = similarity;
                bestPeer = peer;
            }
        }

        System.out.println();

        // Return best match if above threshold
        if (bestPeer != null && maxResonance > 0.1) {
            String address = addressBook.get(bestPeer.id);
            
            System.out.println("✅ TARGET ACQUIRED");
            System.out.println("   Expert: " + bestPeer.id);
            System.out.println("   Resonance: " + String.format("%.4f", maxResonance));
            System.out.println("   Address: " + address);
            System.out.println();
            
            return address;
        }

        System.out.println("❌ NO EXPERT FOUND");
        System.out.println("   Max resonance: " + String.format("%.4f", maxResonance));
        System.out.println("   Threshold: 0.1000");
        System.out.println();
        
        return null; // No one knows
    }

    /**
     * MULTI-HOP ROUTING: Route through multiple peers
     * 
     * If no direct match, find the peer most likely to know someone who knows.
     * This creates a "semantic gradient" that flows toward expertise.
     */
    public String routeMultiHop(NeuroQuant query, List<NeuroQuant> peers,
                                Map<String, String> addressBook, int maxHops) {
        
        System.out.println("🔄 MULTI-HOP ROUTING (max " + maxHops + " hops)");
        
        // For now, just use single-hop routing
        // In production, this would recursively query peers
        return routeQuery(query, peers, addressBook);
    }

    /**
     * BROADCAST QUERY: Send to all peers above threshold
     * 
     * For queries that need multiple perspectives.
     */
    public List<String> broadcastQuery(NeuroQuant query, List<NeuroQuant> peers,
                                      Map<String, String> addressBook, double threshold) {
        
        System.out.println("📢 BROADCAST QUERY (threshold: " + String.format("%.2f", threshold) + ")");
        
        List<String> targets = new java.util.ArrayList<>();
        
        for (NeuroQuant peer : peers) {
            double similarity = query.resonance(peer);
            
            if (similarity > threshold) {
                String address = addressBook.get(peer.id);
                if (address != null) {
                    targets.add(address);
                    System.out.println("   ✓ " + peer.id + " (" + String.format("%.4f", similarity) + ")");
                }
            }
        }
        
        System.out.println("   Total targets: " + targets.size());
        System.out.println();
        
        return targets;
    }

    /**
     * FIND SEMANTIC NEIGHBORS: Get peers similar to a concept
     * 
     * Used for building semantic clusters.
     */
    public List<NeuroQuant> findNeighbors(NeuroQuant concept, List<NeuroQuant> peers,
                                         int count, double minResonance) {
        
        List<NeuroQuant> neighbors = new java.util.ArrayList<>();
        
        // Sort peers by resonance
        List<PeerScore> scores = new java.util.ArrayList<>();
        for (NeuroQuant peer : peers) {
            double resonance = concept.resonance(peer);
            if (resonance >= minResonance) {
                scores.add(new PeerScore(peer, resonance));
            }
        }
        
        // Sort descending by resonance
        scores.sort((a, b) -> Double.compare(b.resonance, a.resonance));
        
        // Take top N
        for (int i = 0; i < Math.min(count, scores.size()); i++) {
            neighbors.add(scores.get(i).peer);
        }
        
        return neighbors;
    }

    /**
     * Helper class for sorting peers by resonance
     */
    private static class PeerScore {
        final NeuroQuant peer;
        final double resonance;
        
        PeerScore(NeuroQuant peer, double resonance) {
            this.peer = peer;
            this.resonance = resonance;
        }
    }
}
